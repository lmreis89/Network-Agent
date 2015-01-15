package action;


import exception.ActionFailedException;
import agent.AgentInfo;

public class PrintAction implements Action{

	private String toPrint;

	
	public PrintAction(){}
	
	@Override
	public void run(String...strings) throws ActionFailedException 
	{
	
		toPrint = strings[0];
		
		if(!toPrint.equals(""))
		{		
			System.out.println(toPrint);
		}
		else
			throw new ActionFailedException("Empty string");
	}
	
	@Override
	public void addAgentInfo(AgentInfo info) 
	{
	}

	
}
