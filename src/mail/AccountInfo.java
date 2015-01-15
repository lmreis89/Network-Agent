package mail;

import java.io.Serializable;

public class AccountInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1443559207197683450L;
	private String username, pass;
	private boolean SSL;
	
	public String getUsername() {
		return username;
	}
	public String getPass() {
		return pass;
	}
	public boolean isSSL() {
		return SSL;
	}
	
	public AccountInfo(String username, String pass, boolean sSL) {

		this.username = username;
		this.pass = pass;
		this.SSL = sSL;
	}
	
}
