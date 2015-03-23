package email;

import java.security.Security;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendEmail {
	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_PORT = "465";
	private static final String emailMsgTxt = "Test Message Contents";
	private static final String emailSubjectTxt = "A test from gmail"; // title
	private static final String emailFromAddress = "SISTester777@gmail.com";
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private static final String[] sendTo = { "stevegeusa@gmail.com" };

	public static void main(String args[]) throws Exception {
		// Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		new SendEmail().sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt,
				emailFromAddress);
		System.out.println("Sucessfully Sent mail to All Users");
	}

	public void sendSSLMessage(String recipients[], String subject,
			String message, String from) throws MessagingException {
		boolean debug = true;
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "true");

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("SISTester777",
								"SKChang2011.");
					}
				});
		session.setDebug(debug);

		Message msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);
		msg.setSubject(subject);
		{
			Multipart multipart = new MimeMultipart("related");
			{
				Multipart newMultipart = new MimeMultipart("alternative");
				BodyPart nestedPart = new MimeBodyPart();
				nestedPart.setContent(newMultipart);
				multipart.addBodyPart(nestedPart);
				{
					BodyPart part = new MimeBodyPart();
					part.setText("SIS_DATA");
					newMultipart.addBodyPart(part);

					part = new MimeBodyPart();
					// the first string is body
					part.setContent("message body", "text/html");
					newMultipart.addBodyPart(part);
				}
			}
			BodyPart part = new MimeBodyPart();
			part.setText("SIS DATA:");
			multipart.addBodyPart(part);
			msg.setContent(multipart);
		}
		Transport.send(msg);
	}
}
