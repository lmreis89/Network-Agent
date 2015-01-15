package action;

import exception.ActionFailedException;
import agent.AgentInfo;


public interface Action {

	public void run(String...strings) throws ActionFailedException;
	public void addAgentInfo(AgentInfo info);
}
