package syntax;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ParamsNode implements Node 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8482167236369542992L;
	protected List<String> params;

	public ParamsNode() 
	{
		super();
		this.params = new LinkedList<String>();
	}

	public List<String> getParams() 
	{
		return params;
	}
	
	public void addParam(String param) 
	{
		params.add(param.substring(1,param.length()-1));
	}

	@Override
	public String unparse() 
	{
		Iterator<String> it = params.iterator();
		String ret = "Params(";
		
		while(it.hasNext())
			ret += it.next();
		
		return ret + ")";
	}
}
