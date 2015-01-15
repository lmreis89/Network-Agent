package agent;

import java.io.Serializable;
import java.util.LinkedList;

public class Log implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7811900901331788163L;
	private String peerName;
	private LinkedList<String> peerActions;
	
	public Log(String name)
	{
		this.peerName=name;
		this.peerActions=new LinkedList<String>();
	}

	public String getFirst() {
		return peerName;
	}

	public LinkedList<String> getSecond() {
		return peerActions;
	}
	
	@Override
	public String toString()
	{
		String output = "";
		output += "Name: "+peerName + "\n";
		for(String action : peerActions)
		{
			output += action + "\n";
		}
		
		return output;
	}
	
}
