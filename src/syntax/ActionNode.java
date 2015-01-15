package syntax;

public class ActionNode implements Node {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8392161508429556314L;
	protected Node subAction;

	public ActionNode() {

	}

	public Node getSubAction() {
		return subAction;
	}
	
	public void addNode(Node subAction)
	{
		this.subAction = subAction;
	}
	
	/**
	 * 
	 * @return "clone" ou "run"
	 */
	public String subActionType()
	{
		return subAction instanceof CloneNode ? "clone":"run";
	}

	@Override
	public String unparse() {
		return "Action("+subAction.unparse()+")";
	}
	

}
