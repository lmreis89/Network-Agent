package action;

import exception.ActionFailedException;
import agent.AgentInfo;

public class PrintReportAction implements Action 
{
	private AgentInfo info;
	
	public PrintReportAction()
	{
		info = null;
	}

	@Override
	public void run(String...strings) throws ActionFailedException 
	{
		if(info != null)
			System.out.println(info.getReport().getLast().toString());
		else
			throw new ActionFailedException("Empty report");
	}

	@Override
	public void addAgentInfo(AgentInfo info) {
		this.info= info;
	}
}
