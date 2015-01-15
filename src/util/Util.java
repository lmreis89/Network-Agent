package util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;

import javax.rmi.ssl.SslRMIClientSocketFactory;

import middleware.AgentPlatform;
import middleware.ConfigManager;
import middleware.PlatformInfo;

public class Util {

	public static AgentPlatform getStub(PlatformInfo info) throws RemoteException, NotBoundException
	{
		
		AgentPlatform platformToMigrate = null;
		ConfigManager man = ConfigManager.getInstance();
		Registry reg;
		if(man.getConfigs().get("security").charAt(0) == 'y')
			reg = LocateRegistry.getRegistry(info.getHost(), 3001, new SslRMIClientSocketFactory());
		else
			reg = LocateRegistry.getRegistry(info.getHost(), 3001);
		
		platformToMigrate = (AgentPlatform) reg.lookup(info.getName());

		return platformToMigrate;
	}
	

	public static String getRealAddress() throws SocketException, UnknownHostException
	{
		String os = System.getProperty("os.name").toLowerCase();
		NetworkInterface ni;
	    if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {   
	        ni = NetworkInterface.getByName("wlan0");
	        if(ni == null)
	        	ni = NetworkInterface.getByName("eth0");
	        Enumeration<InetAddress> ias = ni.getInetAddresses();

	        InetAddress iaddress;
	        do {
	            iaddress = ias.nextElement();
	        } while(!(iaddress instanceof Inet4Address));

	        return iaddress.getHostAddress();
	    }
	    else
	    	return InetAddress.getLocalHost().getHostAddress();
	}
	
	public static void wait10() 
	{
		try { Thread.sleep(10000, 0); }
		catch (InterruptedException e1) { e1.printStackTrace(); }	
	}
}
