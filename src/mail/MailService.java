package mail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

public abstract class MailService 
{
	public boolean isMailAddress(String mail)
	{
		if(mail.split("@").length != 2) 
			return false;
		
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(mail);

		return m.matches();
	}
	
	public abstract void sendMail(String to, String mess) throws MessagingException;
}
