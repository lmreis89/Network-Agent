package syntax;

public class DefinitionNode implements Node {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8473109042193457372L;
	String aID, author, authMail, date, comment, obs;

	public String getaID() {
		return aID;
	}

	public String getAuthor() {
		return author;
	}

	public String getAuthMail() {
		return authMail;
	}

	public String getDate() {
		return date;
	}

	public String getComment() {
		return comment;
	}

	public String getObs() {
		return obs;
	}

	public DefinitionNode(String aID, String author, String authMail,
			String date, String comment, String obs) {
		super();
		this.aID = aID.substring(1,aID.length()-1);
		this.author = author.substring(1,author.length()-1);
		this.authMail = authMail.substring(1,authMail.length()-1);
		this.date = date.substring(1,date.length()-1);
		this.comment = comment.substring(1,comment.length()-1);
		this.obs = obs.substring(1,obs.length()-1);
	}

	@Override
	public String unparse() {
		return "AgentDefinition(ID: "+aID+
				", author: "+author+
				", authormail: "+authMail+
				", date: "+date+
				", comment: "+comment+
				", obs: "+obs+")\n\t";
	}

	
}
