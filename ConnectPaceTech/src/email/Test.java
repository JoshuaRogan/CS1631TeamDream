package email;

import javax.mail.MessagingException;

public class Test {

	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_PORT = "465";
	private static final String emailMsgTxt = "Test Message Contents";
	private static final String emailSubjectTxt = "A test from gmail"; // title
	private static final String emailFromAddress = "SISTester777@gmail.com";
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private static final String[] sendTo = { "stevegeusa@gmail.com" };

	public Test() {

	}

	public static void main(String args[]) throws Exception {
		SendEmail se = new SendEmail();
		se.sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt,
				emailFromAddress);
		Test t = new Test();
	}

}
