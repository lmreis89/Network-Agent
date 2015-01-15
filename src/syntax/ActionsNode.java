package syntax;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ActionsNode implements Node 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6923164375425504983L;
	public List<ActionNode> list;

	public ActionsNode() {
		super();
		list = new LinkedList<ActionNode>();
	}
	
	public void addNode(ActionNode newNode)
	{
		list.add(newNode);
	}

	public List<ActionNode> getActions() {
		return list;
	}

	@Override
	public String unparse() {
		Iterator<ActionNode> it = list.iterator();
		String ret = "Actions(";
		
		while(it.hasNext())
			ret += "\n\t\t\t"+it.next().unparse();
		
		return ret + ")";
	}
	
	
}
