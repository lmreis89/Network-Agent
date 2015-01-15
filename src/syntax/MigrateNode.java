package syntax;

public class MigrateNode implements Node {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7587489316367960596L;
	protected HostNodeNode host;
	protected ActionsNode actions;
	protected ReportNode report; 
	protected boolean trace , output;
	
	public MigrateNode(HostNodeNode host, ActionsNode actions,  boolean trace,ReportNode report,
			boolean output) 
	{
		super();
		this.host = host;
		this.actions = actions;
		this.report = report;
		this.trace = trace;
		this.output = output;
	}
	
	public HostNodeNode getHost() 
	{
		return host;
	}
	
	public ActionsNode getActions() 
	{
		return actions;
	}
	
	public boolean isReport()
	{
		return report != null;
	}
	
	public ReportNode getReport() 
	{
		return report;
	}
	
	public boolean isTrace() 
	{
		return trace;
	}
	
	public boolean isOutput() 
	{
		return output;
	}

	@Override
	public String unparse() {
		return "Migrate("+host.unparse()+
				"output: "+output+", "+
				(report!=null ? ", "+report.unparse(): "")+", "+
				actions.unparse()+
				", trace:"+trace+")";
	}
	
}
