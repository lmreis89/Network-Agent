package mail;
import java.util.Properties;
 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class TLSgMail extends MailService {
 
	private String user, pass;
	private final String 
				mailSuffix = "@gmail.com", 
				prefix = 	"------- --------------------------------- ------\n"+
							"--- THIS MESSAGE WAS AUTOMATICALLY GENERATED ---\n"+
							"------------------------------------------------\n",
				suffix = 	"------------------------------------------------\n"+
							"-----------------END OF MESSAGE-----------------\n"+
							"------------------------------------------------\n";
	
	
	public TLSgMail(AccountInfo mail) {
		super();
		this.user = mail.getUsername();
		this.pass = mail.getPass();
	}


	@Override
	public void sendMail(String to, String mess) throws MessagingException
	{
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass);
			}
		  });
 
	
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user+mailSuffix));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to));
			message.setSubject("Agent Mail Report");
			message.setText(prefix+mess+"\n"+suffix);
 
			Transport.send(message);
 
 

		
	}
}