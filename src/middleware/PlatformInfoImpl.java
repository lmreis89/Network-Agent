package middleware;

public class PlatformInfoImpl implements PlatformInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String host;
	private String name;
	
	public PlatformInfoImpl(String host, String name) {
		this.host = host;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getHost() {
		return host;
	}
	
}
