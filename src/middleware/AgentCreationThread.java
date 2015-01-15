package middleware;

import java.util.Map;

import mail.AccountInfo;

import agent.Agent;
import agent.AgentImpl;
import syntax.ExecNode;

public class AgentCreationThread extends Thread 
{
	private Agent agent;

	public AgentCreationThread(Map<String,PlatformInfo> network, PlatformInfo sysAdmin,ExecNode node,
			String aID, String aAuthor,	String aAuthorEmail, String aDate, String aComment, String aObs,boolean finalReport)
	{
		
		this.agent = new AgentImpl(network,sysAdmin,node,aID,aAuthor,aAuthorEmail,aDate,aComment,aObs,finalReport);
	}

	public AgentCreationThread(Map<String, PlatformInfo> network,
			PlatformInfo sysAdmin, ExecNode node, String aID, String aAuthor,
			String aAuthorEmail, String aDate, String aComment, String aObs,
			AccountInfo mailAccount,boolean finalReport) {

		this.agent = new AgentImpl(network,sysAdmin,node,aID,aAuthor,aAuthorEmail,aDate,aComment,aObs,finalReport);
		agent.setMailAccount(mailAccount);
	}

	public void run()
	{
		this.agent.migrate();
	}
}	
