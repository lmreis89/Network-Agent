package agent;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class AgentInfo implements Serializable
{

	private static final long serialVersionUID = 1L;
	protected String id, author,authorEmail,date,comment,obs;
	protected LinkedList<Log> report;
	protected String currentHostName;

	public AgentInfo(String id2, String author2, String email, String date2,
			String comm, String obs2) 
	{
		this.id=id2;
		this.author=author2;
		this.authorEmail = email;
		this.date = date2;
		this.comment = comm;
		this.obs = obs2;
		this.report = new LinkedList<Log>();
		this.currentHostName = "";
	}
	
	public String getId() {
		return id;
	}

	public String getAuthor() {
		return author;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public String getDate() {
		return date;
	}

	public String getComment() {
		return comment;
	}

	public String getObs() {
		return obs;
	}

	public LinkedList<Log> getReport() {
		return report;
	}
	
	public void addToReport(String actionName, String message)
	{
		Date d = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		report.getLast().getSecond().addLast("Action: "+actionName+"\n<RETURN> \n"+message+"\nLOCALTIME: "+dateFormat.format(d));
	}
	
	public String getCurrentHostName()
	{
		return this.currentHostName;
	}
	
	public void setCurrentHostName(String host)
	{
		this.currentHostName = host;
	}

}
