package agent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import exception.MigrationException;

import action.Action;

import middleware.AgentPlatform;
import middleware.PlatformInfo;
import middleware.PlatformInfoImpl;
import syntax.ActionNode;
import syntax.ComputeNode;
import syntax.RunNode;
import util.Util;

public class AgentComputationThread extends ExecutionThread{

	private ComputeNode compute;

	public AgentComputationThread(Agent owner, ComputeNode compute, Map<String,PlatformInfo> network)
	{
		super(owner,network);
		this.compute = compute;
	}
	
	public void run()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		AgentInfo info = (AgentInfo) owner;
		Log nodeReport = new Log(info.getCurrentHostName());
		owner.getReport().addLast(nodeReport);
		Iterator<ActionNode> actions =  compute.getActions().getActions().iterator();
		
		while(actions.hasNext())
		{
			ActionNode action = actions.next();

			RunNode toExecute = (RunNode) action.getSubAction();
			String classname = toExecute.getClassname();
			try {
					
				

				@SuppressWarnings("unchecked")
				Class<Action> toDo = (Class<Action>) this.getDynamicClass(toExecute).getLoadedClass();
				Action code = (Action) toDo.getConstructors()[0].newInstance();
				this.runAction(code, classname, nodeReport,toExecute.getParams());
				if(compute.isTrace())
					owner.trace();

			} catch (NotBoundException e) {
				nodeReport.getSecond().addLast("ERROR: Failed execution of action: "+
						toExecute.getClassname() +
						"(" + toExecute.getParams().unparse() + ") [Reason: Code Server not bound]" +
						" - "+dateFormat.format(new Date()));
				continue;
			} catch (IllegalArgumentException e) {
				nodeReport.getSecond().addLast("ERROR: Failed execution of action: "+
						toExecute.getClassname() +
						"(" + toExecute.getParams().unparse() + ") [Reason: Wrong parameter]" +
						" - "+dateFormat.format(new Date()));
				continue;
			} catch (SecurityException e) {
				nodeReport.getSecond().addLast("ERROR: Failed execution of action: "+
						toExecute.getClassname() +
						"(" + toExecute.getParams().unparse() + ") [Reason: Security issues]" +
						" - "+dateFormat.format(new Date()));
				continue;
			} catch (InstantiationException e) {
				nodeReport.getSecond().addLast("ERROR: Failed execution of action: "+
						toExecute.getClassname() +
						"(" + toExecute.getParams().unparse() + ") [Reason: Action instantiation problems]" +
						" - "+dateFormat.format(new Date()));
				continue;
			} catch (IllegalAccessException e) {
				nodeReport.getSecond().addLast("ERROR: Failed execution of action: "+
						toExecute.getClassname() +
						"(" + toExecute.getParams().unparse() + ") [Reason: Action illegal access]" +
						" - "+dateFormat.format(new Date()));
				continue;
			} catch (InvocationTargetException e) {
				nodeReport.getSecond().addLast("ERROR: Failed execution of action: "+
						toExecute.getClassname() +
						"(" + toExecute.getParams().unparse() + ") [Reason: Action invocation target problems]" +
						" - "+dateFormat.format(new Date()));
				continue;	
			} catch (RemoteException e) {
				nodeReport.getSecond().addLast("ERROR: Failed execution of action: "+
						toExecute.getClassname() +
						"(" + toExecute.getParams().unparse() + ") [Reason: Communications failure]" +
						" - "+dateFormat.format(new Date()));
				continue;	
			} catch (ClassNotFoundException e) {
				nodeReport.getSecond().addLast("ERROR: Failed execution of action: "+
						toExecute.getClassname() +
						"(" + toExecute.getParams().unparse() + ") [Reason: Missing class definition]" +
						" - "+dateFormat.format(new Date()));
				continue;
			} catch (MalformedURLException e) {
				nodeReport.getSecond().addLast("ERROR: Failed execution of action: "+
						toExecute.getClassname() +
						"(" + toExecute.getParams().unparse() + ") [Reason: Malformed URL]" +
						" - "+dateFormat.format(new Date()));
				continue;
			} catch (IOException e) {
				nodeReport.getSecond().addLast("ERROR: Failed execution of action: "+
						toExecute.getClassname() +
						"(" + toExecute.getParams().unparse() + ") [Reason: I/O failure]" +
						" - "+dateFormat.format(new Date()));
				continue;
			}
		}

		if(compute.isReport())
		{
			String type = compute.getReport().getReportType();
			if(type.equals("callback"))
				owner.sendReport();
			else
				owner.sendStmpReport(compute.getReport().getMailORurldir(), nodeReport);
		}

		String currentName = info.getCurrentHostName();
		String currentHost = network.get(currentName).getHost();

		super.checkout(currentHost,currentName, 0, nodeReport);
		
		try {
			owner.migrateToRoot(0);
		} catch (MigrationException e) {
			nodeReport.getSecond().addLast("ERROR: Failed to migrate from "+ currentName + "to root"
					+") [Reason: "+e.getMessage()+"]" +
					" - "+dateFormat.format(new Date()));
		}
	}
	

}
