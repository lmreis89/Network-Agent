package syntax;

public class ReportNode implements Node 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3906000935771323779L;
	protected String reportType, mailORurldir;

	public String getReportType() {
		return reportType;
	}

	public String getMailORurldir() {
		return mailORurldir;
	}


	public ReportNode(String reportType, String mailORurldir) {
		super();
		this.reportType = reportType;
		this.mailORurldir = mailORurldir;
		if(mailORurldir != null)
			this.mailORurldir = this.mailORurldir.substring(1, this.mailORurldir.length()-1);

	}

	@Override
	public String unparse() {
		return "Report(type: "+reportType+", mailORurl: "+(mailORurldir!=null ? mailORurldir: "null")+")";
	}
	
}
