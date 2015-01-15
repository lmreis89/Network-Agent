

import java.io.File;

import action.Action;
import agent.AgentInfo;
import exception.ActionFailedException;

public class SearchFileAction implements Action 
{
	
	protected AgentInfo agent;
	protected String match;
	
	public SearchFileAction() 
	{
		super();
		agent = null;
		match = "";
	}

	@Override
	public void run(String... strings) throws ActionFailedException 
	{
	
		File [] roots = File.listRoots();
		boolean found = false;
		String target = strings[0];
	
			
		for(int i = 0; i < roots.length  ; i++ )
		{
			if(found)
				break;

			if(System.getProperty("os.name").equals("Linux") && roots[i].getAbsolutePath().equals("/"))
				found = searchForFile(new File("/home/"), target);
			else
			found = searchForFile(roots[i], target);
			
		}
	
		agent.addToReport("\nSearchFileAction", 
				"Search result for \""+target+"\" @"+agent.getCurrentHostName()+": "+(found?"found @"+match:"not found")+"\n");
	}

	private boolean searchForFile(File directory, String target) 
	{
		if(!directory.canRead() && !directory.isDirectory() && !directory.exists())
			return false;
		
		File [] dir = directory.listFiles();
		if(dir == null)
			return false;
		
		for(File f : dir)
		{
			if(f.isFile())
			{
				if(f.getName().equals(target))
				{
					match = f.getAbsolutePath();
					return true;
				}
			}
			else
			{
				boolean found = searchForFile(f,target);
				if(found)
					return true;
			}
		}

		
		return false;
	}

	@Override
	public void addAgentInfo(AgentInfo info) 
	{
		agent = info;

	}

}
