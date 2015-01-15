

import exception.ActionFailedException;
import action.Action;
import agent.AgentInfo;

public class SleepAction implements Action{

	private int n;
	public SleepAction()
	{
		n=0;
	}
	
	@Override
	public void run(String...strings) throws ActionFailedException 
	{
		
		try {
			this.n = Integer.parseInt(strings[0]);
			Thread.sleep(this.n * 1000);
		} catch (InterruptedException e) {
			throw new ActionFailedException("Thread failed to sleep");
		} catch (NumberFormatException e)
		{
			throw new ActionFailedException("Illegal non-integer argument");
		}
	}

	@Override
	public void addAgentInfo(AgentInfo info) {
		
	}

}
