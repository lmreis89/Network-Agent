package syntax;

public class ComputeNode implements Node {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7734583025496736325L;

	private boolean trace, isReport;
	private ReportNode report;
	private ActionsNode actions;
	
	public ComputeNode(boolean trace, ActionsNode actions, ReportNode report) 
	{
		super();
		this.trace = trace;
		if(report != null)
		{
			isReport = true;
			this.report = report;
		}
		this.actions = actions;
	}

	public boolean isTrace() {
		return trace;
	}

	public boolean isReport() {
		return isReport;
	}

	public ReportNode getReport() {
		return report;
	}

	public ActionsNode getActions() {
		return actions;
	}

	@Override
	public String unparse() {
		return "Compute(report: "+report.unparse()+", trace: "+trace+", actions:\n\t"+actions.unparse()+")";
	}

}
