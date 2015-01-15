package syntax;

public class CloneNode implements Node {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6394348740873111432L;
	protected ExecNode execStatement;

	public ExecNode getExecStatement() {
		return execStatement;
	}

	public CloneNode(ExecNode execStatement) {
		super();
		this.execStatement = execStatement;
	}

	@Override
	public String unparse() {
		return "Clone("+execStatement.unparse()+")";
	}
	
	

}
