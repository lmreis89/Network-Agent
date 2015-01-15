package syntax;

public class HostNodeNode implements Node
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1057732842198785729L;
	protected String hostNodeUrl;

	public HostNodeNode(String hostNodeUrl) {
		super();
		this.hostNodeUrl = hostNodeUrl.substring(1, hostNodeUrl.length()-1);
	}

	public String getHostNodeUrl() {
		return hostNodeUrl;
	}

	@Override
	public String unparse() {
		return "HostNode("+hostNodeUrl+")";
	}
	
	
}
