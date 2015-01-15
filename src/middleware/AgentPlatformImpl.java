package middleware;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mail.AccountInfo;

import classloader.DynamicClass;
import classloader.RMILoadableClass;
import exception.NoSuchAgentException;

import parser.ParseException;
import parser.Parser;
import syntax.DefinitionNode;
import syntax.ExecNode;
import syntax.StartNode;
import util.Util;
import visual.TextAreaPrintStream;

import agent.Agent;
import agent.AgentInfo;
import agent.Log;

public class AgentPlatformImpl implements AgentPlatform
{

	private static final long serialVersionUID = 1L;
	private String name, classRepository;
	private Map<String,PlatformInfo> network;
	private Map<String,AgentInfo> executingAgents;
	private Map<String,AgentInfo> createdAgents;
	private TextAreaPrintStream out;
	private AccountInfo mailAccount;

	public AgentPlatformImpl(String platformName,
			String peerHost,String peerName,String classRepository, TextAreaPrintStream out) 
					throws RemoteException, FileNotFoundException, SocketException 
					{
		this.out = out;
		this.classRepository = classRepository;
		this.mailAccount = null;
		try {

			name = platformName;
			PlatformInfo myInfo = new PlatformInfoImpl(Util.getRealAddress(),name);
			network = new HashMap<String,PlatformInfo>();
			executingAgents = new HashMap<String,AgentInfo>();
			createdAgents = new HashMap<String,AgentInfo>();

			if(peerHost.equals(""))
			{
				this.registerPlatform(myInfo);
			}
			else
				this.registerAsNode(peerHost,peerName,myInfo);

		} catch (UnknownHostException e1) {
			System.err.println("The main node does not exist at the provided address["+peerHost+"]");
		}

	}

	private void registerAsNode(String adminHost, String adminName, PlatformInfo myInfo) throws  UnknownHostException
	{
		PlatformInfo helperInfo = new PlatformInfoImpl(adminHost, adminName);
		
		for(int i = 0; i<10; i++)
		{
			try {
	
				AgentPlatform helper = Util.getStub(helperInfo);
				this.network = helper.getNetwork();
				helper.registerPlatform(myInfo);
	
				for (PlatformInfo peerInfo : network.values()) 
				{
					if (!peerInfo.getName().equals(adminName)) 
					{
						AgentPlatform peer = Util.getStub(peerInfo);
						try {
							peer.registerPlatform(myInfo);
						} catch (RemoteException e1) {
							Util.wait10();
							peer.registerPlatform(myInfo);
						}
					}
				}
				
				this.network.put(myInfo.getName(), myInfo);
				break;
				
			} catch (NotBoundException e) {
				System.err.println("Communications failure: Not Bound"+ " Retrying "+i+" times left");
				continue;
			} catch (RemoteException e) {
				System.err.println("Communications failure"+ " Retrying "+i+" times left");
				e.printStackTrace();
				continue;
			}
		}
	}

	@Override
	public void receiveAgent(Agent agent) throws RemoteException{

		System.out.println("Receiving agent");

		if(agent.finishedExecution()){
			
			if(agent.isFinalReport())
				this.report(agent.getReport());
			System.out.println("Agent "+((AgentInfo)agent).getId()+"\nReport is: \n"+agent.getReport().toString());
		}
		else synchronized(executingAgents)
		{
			AgentInfo info = (AgentInfo)agent;
			executingAgents.put(info.getId(),info);
			agent.execute();
		}

	}

	@Override
	public DynamicClass loadClass(String name) throws RemoteException
	{
		return new RMILoadableClass(name,classRepository);
	}

	public void createAgent(ExecNode node, String aID, String aAuthor, String aAuthorEmail, String aDate, String aComment, String aObs,boolean finalReport ) throws SocketException
	{
		PlatformInfo self;
		try {
			self = new PlatformInfoImpl(Util.getRealAddress(),name);

			new AgentCreationThread(network,self,node,aID,aAuthor,aAuthorEmail,aDate,aComment,aObs,mailAccount,finalReport).start();
			createdAgents.put(aID, new AgentInfo(aID, aAuthor, aAuthorEmail, aDate, aComment, aObs));
		} catch (UnknownHostException e) {
			System.err.println("Error obtaining own IP");
			out.println("Error obtaining own IP");
			out.println("Finishing execution...");
		} 
	}

	@Override
	public void registerPlatform(PlatformInfo info) throws RemoteException
	{
		this.network.put(info.getName(),info);
	}

	@Override
	public void trace(String trace, String hostName) throws RemoteException
	{

		out.println(trace+" @"+hostName);

	}

	@Override
	public void report(List<Log> report) throws RemoteException
	{
		
		for(Log log : report)
			out.println(log.toString());

	}


	public void parseAgent(InputStream toParse) throws SocketException, ParseException 
	{
		Parser parser = new Parser(toParse);
		StartNode node;

		node = (StartNode)parser.main();
		ExecNode exec = node.getExec();

		DefinitionNode def = node.getDef();
		this.createAgent(exec,def.getaID(),def.getAuthor(),def.getAuthMail(),def.getDate(),
				def.getComment(),def.getObs(), node.isRepFinal());

	}


	public void checkCode(InputStream toParse) throws ParseException
	{
		new Parser(toParse).main();		
	}

	@Override
	public void checkout(Agent agent) throws RemoteException 
	{

		AgentInfo info = (AgentInfo)agent;
		this.executingAgents.remove(info.getId());

	}

	@Override
	public List<Log> getAgentReport(String agentID) throws RemoteException, NoSuchAgentException 
	{
		if(executingAgents.containsKey(agentID))
			return executingAgents.get(agentID).getReport();
		else throw new NoSuchAgentException();
	}



	public int numAgents() throws RemoteException
	{
		return this.executingAgents.size();
	}

	@Override
	public PlatformInfo nextMigrate() throws RemoteException 
	{
		int min = Integer.MAX_VALUE;
		PlatformInfo next = null;
		for(PlatformInfo info: network.values())
		{
			AgentPlatform platform;
			try {
				platform = Util.getStub(info);
				int toCompare = platform.numAgents();
				if(min > toCompare)
				{
					min = toCompare;
					next = info;
				}

			} catch (NotBoundException e) {
				System.err.println("Platform "+info.getName()+" is unavailable. Reason: Not bound");
				continue;
			}
		}
		return next;
	}


	/******************************
	 * GUI FUNCTIONS **************
	 ******************************/

	public Object[] getPlatforms()
	{
		Iterator<Entry<String, PlatformInfo>> it = this.network.entrySet().iterator();
		Object [][] networkContent = new Object[network.size()+1][1];
		Object [] ret = new Object[]{ new Object[]{ "Node name","Node IP"} ,networkContent };
		networkContent[0] = new Object[]{ "Node Name","Node IP"};
		for(int i = 1; it.hasNext(); i++)
		{
			PlatformInfo pi= it.next().getValue();
			networkContent[i] = new Object[]{ pi.getName(),pi.getHost()};
		}


		return ret;
	}

	public Object[] getCreatedAgents()
	{
		Iterator<Entry<String, AgentInfo>> it = this.createdAgents.entrySet().iterator();
		Object [][] agents = new Object[createdAgents.size()+1][1];
		Object [] ret = new Object[]{ new Object[]{ "Agent ID","Author","Author-mail", "Date", "Comments","Obs"}
		, agents };

		agents[0] = new Object[]{ "Agent ID","Author","Author-mail", "Date", "Comments","Obs"};
		for(int i = 1; it.hasNext(); i++)
		{
			AgentInfo ai= it.next().getValue();	
			agents[i] = new Object[]{ ai.getId(),ai.getAuthor(),ai.getAuthorEmail(),ai.getDate(),ai.getComment(),ai.getObs()};
		}


		return ret;
	}

	public Object[] getExecutingAgents()
	{
		Iterator<Entry<String, AgentInfo>> it = this.executingAgents.entrySet().iterator();
		Object [][] agents = new Object[executingAgents.size()+1][1];
		Object [] ret = new Object[]{ new Object[]{ "Agent ID","Author","Author-mail", "Date", "Comments","Obs"}
		, agents };
		agents[0] = new Object[]{ "Agent ID","Author","Author-mail", "Date", "Comments","Obs"};
		for(int i = 1; it.hasNext(); i++)
		{
			AgentInfo ai= it.next().getValue();	
			agents[i] = new Object[]{ ai.getId(),ai.getAuthor(),ai.getAuthorEmail(),ai.getDate(),ai.getComment(),ai.getObs()};
		}


		return ret;
	}

	public void setMailAccount(AccountInfo accountInfo) 
	{
		this.mailAccount = accountInfo;	
	}

	@Override
	public Map<String, PlatformInfo> getNetwork() throws RemoteException {
		return this.network;
	}

	public List<Log> getRemoteAgentReport(String agentID) throws NoSuchAgentException, SocketException
	{
		List<Log> report = null;
		boolean found = false;
		for(PlatformInfo info: network.values())
		{
			AgentPlatform peer;
			try {
				if(!info.getName().equals(name))
				{
					peer = Util.getStub(info);
					report = peer.getAgentReport(agentID);
					found = true;
					break;
				}
			} 
			catch (RemoteException e) 
			{
				System.err.println("Could not send trace to agent: "+agentID+". Reason: Communications failure");	
			} 
			catch (NotBoundException e) 
			{
				System.err.println("Could not send trace to agent: "+agentID+". Reason: Not bound");
			} 
			catch(NoSuchAgentException e)
			{
				System.err.println("Could not send trace to agent: "+agentID+". Reason: No such agent");
				continue;
			} 

		}
		if(found)
			return report;
		else throw new NoSuchAgentException("Agent ID "+agentID + ": could not be found");

	}

}
