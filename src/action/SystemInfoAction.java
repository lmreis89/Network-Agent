package action;

import java.io.File;

import agent.AgentInfo;
import exception.ActionFailedException;

public class SystemInfoAction implements Action 
{
	protected AgentInfo agent;
	
	public SystemInfoAction() 
	{
		super();
		agent = null;
	}

	@Override
	public void run(String... strings) throws ActionFailedException 
	{
		String out = "\n<START OF REPORT FOR " + agent.getCurrentHostName() + " >" + "\n";
		out += "OS: "+ System.getProperty("os.name") + "\n";
		out += "Available Processors: " + Runtime.getRuntime().availableProcessors() + "\n";
		out +="Free memory (bytes): " + Runtime.getRuntime().freeMemory() + "\n";
		long maxMemory = Runtime.getRuntime().maxMemory() ;
		out += "Maximum memory (bytes): " + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory) + "\n";
		out += "Total memory (bytes): " + Runtime.getRuntime().totalMemory() + "\n";
		File[] roots = File.listRoots() ;
		for (File root : roots) {
		      out += "File system root: " + root.getAbsolutePath() + "\n";
		      out += "Total space (bytes): " + root.getTotalSpace() + "\n";
		      out += "Free space (bytes): " + root.getFreeSpace() + "\n";
		      out += "Usable space (bytes): " + root.getUsableSpace() + "\n";
		    }
		
		out += "<END OF REPORT>"+"\n";
		
		agent.addToReport("SystemInfoAction", out);
	}
	
	
	@Override
	public void addAgentInfo(AgentInfo info) 
	{
		this.agent = info;
	}

}
