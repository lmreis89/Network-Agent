package agent;



import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Map;

import javax.mail.MessagingException;

import exception.MigrationException;
import syntax.ExecNode;
import syntax.MigrateNode;
import util.Util;
import mail.AccountInfo;
import mail.MailService;
import mail.SSLgMail;
import mail.TLSgMail;
import middleware.AgentPlatform;

import middleware.PlatformInfo;
import middleware.PlatformInfoImpl;

public class AgentImpl extends AgentInfo implements Agent
{

	private PlatformInfo systemAdmin;
	private ExecNode execution;
	private int currentMigrate;
	private Map<String,PlatformInfo> network;
	private AccountInfo mailAccount;
	private boolean finished, finalReport;

	private static final long serialVersionUID = 1L;
	/**
	 * Prefixo de todos os identificadores de uma plataforma
	 */


	public AgentImpl(Map<String,PlatformInfo> network,PlatformInfo sysAdmin,ExecNode node,
			String id,String author,String email,String date,String comm, String obs,boolean finalReport)
	{
		super(id,author,email,date,comm,obs);

		this.network = network;
		mailAccount = new AccountInfo("agentplatform.mail", "themotherfin", true);
		this.systemAdmin = sysAdmin;
		this.execution = node;
		this.currentMigrate = 0;
		this.finalReport = finalReport;

		this.finished = false;
	}

	/**
	 * This method starts the execution for the current node
	 * This method returns immediately since it delegates execution to a different thread
	 */
	@Override
	public void execute() 
	{
		
		if(this.execution.isCompute())
			new AgentComputationThread(this,execution.getCompute(),this.network).start();		
		else
			new AgentMigrationThread(this,execution.getSubNodes().get(currentMigrate),this.network).start();		
	}

	/**
	 * @return report - the complete agent wallet
	 */
	@Override
	public LinkedList<Log> getReport() 
	{
		return report;
	}

	/**
	 * The agent tries to migrate to another node according to it's script
	 * If there are no more nodes to migrate to the agent tries to migrate to
	 * the starting platform
	 * @throws MigrationException 
	 */
	public void migrate()
	{
		
		if(!this.execution.isCompute()){

			if(! this.hasMigrations() )
			{
				
				try {
					this.migrateToRoot(0);
					return;
				} catch (MigrationException e) {
					e.printStackTrace();
				}
			}


			
			MigrateNode current = this.nextMigration();
		
			String nameToMigrate = current.getHost().getHostNodeUrl();
			System.out.println("migrating to: "+nameToMigrate);

			String hostToMigrate = network.get(nameToMigrate).getHost();

			try {
				AgentPlatform platformToMigrate = Util.getStub(new PlatformInfoImpl(hostToMigrate,nameToMigrate));
				this.setCurrentHostName(nameToMigrate);

				platformToMigrate.receiveAgent(this);
			} catch (NotBoundException e) {
				Log log = new Log(nameToMigrate);
				log.getSecond().addLast(nameToMigrate + ": Could not migrate to host");
				this.getReport().addLast(log);
				System.err.println("Could not migrate to host");
				currentMigrate++;
				this.migrate();
			} catch (RemoteException e) {
				Log log = new Log(nameToMigrate);
				log.getSecond().addLast("Could not migrate to host");
				this.getReport().addLast(log);
				System.err.println("Could not migrate to host");
				currentMigrate++;
				this.migrate();
			}
		}
		else
		{
			AgentPlatform sysAdmin;
			for(int i = 0; i<10; i++)
			{
				try {
					
					sysAdmin = Util.getStub(systemAdmin);
					PlatformInfo toMigrate = sysAdmin.nextMigrate();
					try{
						this.setCurrentHostName(toMigrate.getName());
						AgentPlatform platform = Util.getStub(toMigrate);
						platform.receiveAgent(this);
						break;
					} catch (RemoteException e1) {
						Log log = new Log(this.currentHostName);
						log.getSecond().addLast(toMigrate.getName()+"Could not migrate to host"+ " Retrying "+i+" times left");
						this.getReport().addLast(log);
						System.err.println("Could not migrate to host"+ " Retrying "+i+" times left");
						
					} catch (NotBoundException e1) {
						Log log = new Log(currentHostName);
						log.getSecond().addLast(toMigrate.getName()+"Could not migrate to host. Reason: Not Bound"+ " Retrying "+i+" times left");
						this.getReport().addLast(log);
						System.err.println("Could not migrate to host. Reason: Not Bound"+ " Retrying "+i+" times left");
					}
				} catch (RemoteException e) {
					
					Log log = new Log(this.currentHostName);
					log.getSecond().addLast("Could not contact root"+ " Retrying "+i+" times left");
					this.getReport().addLast(log);
					System.err.println("Could not contact root"+ " Retrying "+i+" times left");
				
					
				} catch (NotBoundException e) {
					Log log = new Log(this.currentHostName);
					log.getSecond().addLast("Could not contact root"+ " Retrying "+i+" times left");
					this.getReport().addLast(log);
					System.err.println("Could not contact root"+ " Retrying "+i+" times left");
				
				}
			}

		}
	}

	/**
	 * Sends the complete agent Wallet to the starting platform
	 */
	public void sendReport()
	{
		for(int i =0; i<10;i++)
		{
			try {
				AgentPlatform sysAdmin = Util.getStub(systemAdmin);
				sysAdmin.report(this.getReport());
				break;
			} catch (RemoteException e) {
				Log log = new Log(this.currentHostName);
				log.getSecond().addLast("Could not contact root to deliver report"+ " Retrying "+i+" times left");
				this.getReport().addLast(log);
				System.err.println("Could not contact root to deliver report"+ " Retrying "+i+" times left");
			
			} catch (NotBoundException e) {
				Log log = new Log(this.currentHostName);
				log.getSecond().addLast("Could not contact root to deliver report. Reason: Not Bound"+ " Retrying "+i+" times left");
				this.getReport().addLast(log);
				System.err.println("Could not contact root to deliver report. Reason: Not Bound"+ " Retrying "+i+" times left");
		
			}
		}
	}

	public void incrementMigrate()
	{
		this.currentMigrate++;
	}

	/**
	 * Sends an email to the sequested mail with report in it's contents
	 */
	public void sendStmpReport(String mail, Log report) 
	{

		MailService ms;

		if(mailAccount == null)
			mailAccount = new AccountInfo("agentplatform.mail", "themotherfin", true);
		

		if(mailAccount.isSSL())
			ms = new SSLgMail(mailAccount);
		else
			ms = new TLSgMail(mailAccount);
		
		String reason = ". Reason: "+"Invalid address";
		if(ms.isMailAddress(mail))
		{	
			
			try {
				ms.sendMail(mail, report.toString());
				return;
			} catch (MessagingException e) {
				reason = "";
			}
		}
	
		Log log = new Log(this.currentHostName);
		log.getSecond().addLast("Could not send email to "+mail+reason);
		this.getReport().addLast(log);
		System.err.println("Could not send email "+mail+reason);
	}

	/**
	 * Adds a new Log to the agents wallet
	 */
	public void addToReport(Log nodeReport)
	{
		this.getReport().addLast(nodeReport);
	}

	/**
	 * @return a boolean value that is true if the execution of the agents script has ended
	 */
	@Override
	public boolean finishedExecution() {
		return finished;
	}




	private boolean hasMigrations()
	{
		return currentMigrate < execution.getSubNodes().size();
	}

	private MigrateNode nextMigration()
	{
		return execution.getSubNodes().get(currentMigrate);
		
	}


	public void trace() 
	{
		try {
			AgentPlatform sysAdmin = Util.getStub(systemAdmin);
			sysAdmin.trace(report.getLast().getSecond().getLast(), currentHostName);

		} catch (RemoteException e) {
			Log log = new Log(this.currentHostName);
			log.getSecond().addLast("Could not send trace to root");
			this.getReport().addLast(log);
			System.err.println("Could not send trace to root");
		} catch (NotBoundException e) {
			Log log = new Log(this.currentHostName);
			log.getSecond().addLast("Could not send trace to root. Reason: Not bound");
			this.getReport().addLast(log);
			System.err.println("Could not send trace to root. Reason: Not bound");
		}
	}

	private void finished()
	{
		finished = true;
	}

	@Override
	public void setMailAccount(AccountInfo mailAccount) 
	{
		this.mailAccount = mailAccount;

	}

	public void migrateToRoot(int numTries) throws MigrationException
	{
		if(numTries < 10)
		{
			try {
				this.finished();
				AgentPlatform sysAdmin = Util.getStub(systemAdmin);
				sysAdmin.receiveAgent(this);
			} catch (Exception e) {
				//sleep for ten seconds and then try again 
				//do this 10 times and then give up
				Util.wait10();
				this.migrateToRoot(numTries++);
			}
		}
		else throw new MigrationException("Could not migrate to root");
	}

	@Override
	public boolean isFinalReport() {
		return finalReport;
	}


}
