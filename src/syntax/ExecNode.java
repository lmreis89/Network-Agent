package syntax;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ExecNode implements Node
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3012605867274105913L;
	private List<MigrateNode> subNodes;
	private boolean isCompute ;
	private ComputeNode compute;

	public List<MigrateNode> getSubNodes() {
		return subNodes;
	}
	
	public ExecNode() {
		super();
		subNodes = new LinkedList<MigrateNode>();
		isCompute = false;
		compute = null;

	}
	
	public boolean isCompute() {
		return isCompute;
	}

	public ComputeNode getCompute() {
		return compute;
	}

	public ExecNode(ComputeNode compute){
		super();
		subNodes = null;
		isCompute = true;
		this.compute = compute; 
	}
	
	public void addNode(MigrateNode newNode)
	{
		subNodes.add(newNode);
	}

	@Override
	public String unparse() {
		Iterator<MigrateNode> it = subNodes.iterator();
		String ret = "Exec(";
		
		while(it.hasNext())
			ret += "\n\t\t"+it.next().unparse();
		
		return ret + ")";
	}
	
	

}
