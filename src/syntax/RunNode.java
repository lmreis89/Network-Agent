package syntax;

public class RunNode implements Node 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8514031648830812891L;
	protected ParamsNode params; 
	protected String classname, urldir;
	
	public RunNode( String classname, ParamsNode params,String urldir) {
		super();

		this.params = params;
		this.classname = classname.substring(1);
		if(urldir != null)
			this.urldir = urldir.substring(1,urldir.length()-1);
	}
	
	public boolean hasParams()
	{
		return params != null;
	}
	
	public boolean hasUrlDir()
	{
		return urldir != null;
	}
	
	public ParamsNode getParams() {
		return params;
	}
	
	public String getClassname() {
		return classname;
	}
	
	public String getUrldir() {
		return urldir;
	}

	@Override
	public String unparse() {
		return "RunNode("+(params!= null ? params.unparse() : "")+", classname:"+(classname!=null ? classname: "null")+", urldir:"+(urldir!=null ? urldir: "null")+")";
	}

}
