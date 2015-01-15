package middleware;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import classloader.DynamicClass;
import exception.NoSuchAgentException;

import agent.Agent;
import agent.Log;

public interface AgentPlatform extends Remote{
	public void registerPlatform(PlatformInfo info) throws RemoteException;
	public void receiveAgent(Agent agent) throws RemoteException;
	public DynamicClass loadClass(String name) throws RemoteException;
	public void trace(String trace, String currentHostName) throws RemoteException;
	public void report(List<Log> report) throws RemoteException;
	public void checkout(Agent agent) throws RemoteException;
	public List<Log> getAgentReport(String agentID) throws RemoteException, NoSuchAgentException;
	public PlatformInfo nextMigrate() throws RemoteException;
	public int numAgents() throws RemoteException;
	public Map<String,PlatformInfo> getNetwork() throws RemoteException;

}
