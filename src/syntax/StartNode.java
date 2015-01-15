package syntax;

public class StartNode implements Node {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6503280016261873153L;
	protected DefinitionNode def;
	protected ExecNode exec;
	protected boolean repFinal;
	
	
	public StartNode(DefinitionNode def, ExecNode exec, boolean repFinal) {
		super();
		this.def = def;
		this.exec = exec;
		this.repFinal = repFinal;
	}
	
	
	public DefinitionNode getDef() {
		return def;
	}
	public ExecNode getExec() {
		return exec;
	}
	public boolean isRepFinal() {
		return repFinal;
	}


	@Override
	public String unparse() {
		return "Start("+def.unparse()+", "+exec.unparse();
	}
	
	
	
	
}
