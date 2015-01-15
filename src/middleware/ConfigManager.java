package middleware;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class ConfigManager 
{
	private static ConfigManager instance;
	public static synchronized ConfigManager getInstance()
	{
		if( instance == null)
			instance = new ConfigManager();
		return instance;
	}
	
	private Map<String,String> configurations;
	
	public void runConfigurations(Scanner in)
	{
		configurations = new HashMap<String,String>();
		
		System.out.println("CONFIGURATION PROGRAM EXECUTING ...");
		setServerName(in);
		isStartNode(in);
		isSecure(in);
		getCodeBase(in);
	}
	
	private void setServerName(Scanner in)
	{
		System.out.println("Insert the platform's name");
		String answer = in.nextLine();
		if(answer.length() < 1)
			setServerName(in);
		else
			this.configurations.put("name",answer);
	}
	
	private void setPeerName(Scanner in)
	{
		System.out.println("Insert the peer name");
		String answer = in.nextLine();
		if(answer.length() < 1)
			setPeerName(in);
		else
			this.configurations.put("peerName",answer);
	}
	
	private void setPeerHost(Scanner in)
	{
		System.out.println("Insert the peer host");
		String answer = in.nextLine();
		if(answer.length() < 1)
			setPeerHost(in);
		else
			this.configurations.put("peerHost",answer);
	}
	
	private void isStartNode(Scanner in)
	{
		System.out.println("Are you the start node ?");
		String answer = in.next();
		in.nextLine();
		answer = answer.toLowerCase();
		
		if(answer.length() > 1 || (answer.charAt(0) == '\n') || (answer.charAt(0) != 'y' && answer.charAt(0) != 'n'))
		{
			System.out.println("Wrong input, please insert Y or N to procede");
			isSecure(in);
		}
		else
		{
			if(answer.charAt(0) != 'y')
			{
				this.setPeerName(in);
				this.setPeerHost(in);
			}
			else
			{
				this.configurations.put("peerName", "");
				this.configurations.put("peerHost", "");
			}
		}
	}
	
	private void getCodeBase(Scanner in) {
		System.out.println("Insert the complete path for this platform's code repository");
		String answer = in.nextLine();
		if(answer.length() < 1 || answer.charAt(0) == '\n' || answer.charAt(answer.length()-1)!= '/')
		{
			System.out.println("Wrong input, please finish your directory with /");
			getCodeBase(in);
			
		}
		else
			this.configurations.put("codeBase", answer);
		
	}

	private void isSecure(Scanner in)
	{
		System.out.println("Do you want this platform to use secure channels?(Y / N)");
		String answer = in.next();
		in.nextLine();
		answer = answer.toLowerCase();
		
		if(answer.length() > 1 || (answer.charAt(0) == '\n') || (answer.charAt(0) != 'y' && answer.charAt(0) != 'n'))
		{
			System.out.println("Wrong input, please insert Y or N to procede");
			isSecure(in);
		}
		else
			this.configurations.put("security", answer);
	}
	
	public Map<String,String> getConfigs()
	{
		return this.configurations;
	}
}
