package mail;
 
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class SSLgMail extends MailService
{
	private String user, pass;
	private final String 
				mailSuffix = "@gmail.com", 
				prefix = 	"------- --------------------------------- ------\n"+
							"--- THIS MESSAGE WAS AUTOMATICALLY GENERATED ---\n"+
							"------------------------------------------------\n",
				suffix = 	"------------------------------------------------\n"+
							"-----------------END OF MESSAGE-----------------\n"+
							"------------------------------------------------\n";


//	public static void main(String[] args) 
//	{
//		SSLgMail mail = new SSLgMail("agentplatform.mail","themotherfin");
//		while(true)
//		{
//			System.out.print("Insert mail address\n>");
//			String email = new Scanner(System.in).nextLine();
//	
//			if(mail.isMailAddress(email))
//			{
//				mail.sendMail(email,"BA!BAM!!!!!");
//				System.out.println("Mail sent to: "+email);
//				break;
//			}
//		}
//	}
	
	

	
	public SSLgMail(AccountInfo mail) 
	{
		super();
		this.user = mail.getUsername();
		this.pass = mail.getPass();
	}


	public void sendMail(String to, String mess) throws MessagingException
	{
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
 
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user,pass);
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

