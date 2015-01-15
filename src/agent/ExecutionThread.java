package agent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import syntax.ParamsNode;
import syntax.RunNode;
import util.Util;
import action.Action;
import classloader.DynamicClass;
import classloader.HTTPLoadableClass;
import classloader.LocalClass;
import exception.ActionFailedException;

import middleware.AgentPlatform;
import middleware.PlatformInfo;
import middleware.PlatformInfoImpl;

public class ExecutionThread extends Thread
{
	protected static final String PLATFORM_IDENTIFIER = "PC-";
	protected Map<String,PlatformInfo> network;
	protected Agent owner;
	public ExecutionThread(Agent owner, Map<String,PlatformInfo> network)
	{
		this.owner = owner;
		this.network = network;
	}
	
	protected DynamicClass getDynamicClass(RunNode runNode) 
			throws ClassNotFoundException, MalformedURLException, IOException, NotBoundException
	{
		if(!runNode.hasUrlDir())
			return new LocalClass(runNode.getClassname());
		if(isPlatform(runNode.getUrldir()))
		{
			String nameToLoad = runNode.getUrldir();
			String hostToLoad = network.get(nameToLoad).getHost();
			AgentPlatform codeSource = Util.getStub(new PlatformInfoImpl(hostToLoad,nameToLoad));
			return codeSource.loadClass(runNode.getClassname());
		}
		else
			return new HTTPLoadableClass(runNode.getUrldir(), runNode.getClassname());
	}

	protected boolean isPlatform(String dir) 
	{
		return dir.startsWith(PLATFORM_IDENTIFIER);
	}

	protected void runAction(Action code,String actionName, Log nodeReport, ParamsNode paramsNode) throws InstantiationException, IllegalAccessException
	{

		String result = "Action: "+actionName;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		try {
			String [] params = new String[1];
		
			if(paramsNode != null)
			{
				Iterator<String> it = paramsNode.getParams().iterator();
				params = new String[paramsNode.getParams().size()];
				for(int i = 0; it.hasNext(); i++)
					params[i] = it.next();
			}
				
			code.addAgentInfo((AgentInfo)owner);
			code.run(params);
			result+=": OK";

		} catch (IllegalArgumentException e1) {
			result+= ": ERROR<"+" illegal arguments one of the Action implementation functions "+">";
		} catch (SecurityException e1) {
			result+= ": ERROR<"+" the downloaded code tried to use unauthorized resources "+">";
		} catch (ActionFailedException e) {
			result+= ": ERROR< "+e.getMessage()+" >"; 
		}

		result+= " LOCALTIME: " + dateFormat.format(date);

		nodeReport.getSecond().addLast(result);
	}
	
	protected void checkout(String currentHost, String currentName,int round, Log log)
	{
		System.out.println("checkout");
		System.out.println(currentHost+"/"+currentName);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		AgentPlatform currentPlatform;
		try {
			currentPlatform = Util.getStub(new PlatformInfoImpl(currentHost,currentName));
			currentPlatform.checkout(owner);
		} catch (RemoteException e) {
		
				Util.wait10();
				if(round < 10)
					checkout(currentHost,currentName,round++,log);
				else
				{
					log.getSecond().addLast("ERROR: Failed to checkout from "+ currentName
							+") [Reason: Communications Failure]" +
							" - "+dateFormat.format(new Date()));
				}
				
		
		} catch (NotBoundException e) {
			Util.wait10();
			if(round < 10)
				checkout(currentHost,currentName,round++,log);
			else
			{
				log.getSecond().addLast("ERROR: Failed to checkout from "+ currentName
						+") [Reason: Server not bound ?]" +
						" - "+dateFormat.format(new Date()));
			}
		
		}
	}


}
