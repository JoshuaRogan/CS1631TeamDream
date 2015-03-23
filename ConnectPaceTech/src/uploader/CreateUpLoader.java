package uploader;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;
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


public class CreateUpLoader {
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;

	// variables for sending emails
	static final String SMTP_HOST_NAME = "smtp.gmail.com";
	static final String SMTP_PORT = "465";
	static final String emailMsgTxt = "Test Message Contents";
	static final String emailSubjectTxt = "SPO2 Data From SIS System."; // title
	static final String emailFromAddress = "SISTester777@gmail.com";
	static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	static final String[] sendTo = { "chang@cs.pitt.edu" }; //This is receiver

	public static void main(String[] args) throws Exception {
		universal = new Socket("127.0.0.1", 7999);

		mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());

		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID", "23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name", "UpLoader");
		mEncoder.sendMsg(msg23, universal.getOutputStream());

		KeyValueList kvList;
		outstream = universal.getOutputStream();
		//String lname = kvList.getValue("LastName");
		//String fname = kvList.getValue("FirstName");
		//String result1 = kvList.getValue("SPO2");
//		String result2 = kvList.getValue("DateTime");
		
		PostQuery postq= new PostQuery();
		String url = "http://ksiresearch.org/seke/PHP_Post.php";
		//String query = "insert into user values ('" + lname+" "+fname + "','" + result1 + "')";
		//postq.PostToPHP(url,query);
		
		while (true) {
			kvList = mDecoder.getMsg();
			ProcessMsg(kvList);
		}
	}

	/*
	 * Method for sending email
	 */
	static void sendSSLMessage(String recipients[], String subject,
			String message, String from, String lname, String fname,
			String result1, String result2) throws MessagingException {
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
					part.setText("SIS DATA:");
					newMultipart.addBodyPart(part);

					part = new MimeBodyPart();
					// the first string is email context
					part.setContent("Here is the SPO2 data(This is an automatic massage send from SIS system): LastName: " 
					+ lname + " FirstName: " + fname + " SPO2 Result: " + result1 + " " + result2,
							"text/html");
					newMultipart.addBodyPart(part);
				}
			}
			BodyPart part = new MimeBodyPart();
			part.setText("Here is the SPO2 data(This is an automatic massage send from SIS system): LastName: " 
					+ lname + " FirstName: " + fname + " SPO2 Result: " + result1 + " " + result2);
			multipart.addBodyPart(part);
			msg.setContent(multipart);
		}
		Transport.send(msg);
		System.out.println("Sucessfully Sent mail to All Users, lol.\n");
	}



	static void ProcessMsg(KeyValueList kvList) throws Exception {
		int MsgID = Integer.parseInt(kvList.getValue("MsgID"));

		switch (MsgID) {
		/****************************************************
		 * Below are the main part of the component program. All received msgs
		 * are encoded as a KeyValueList kvList. kvList is a vector of <String
		 * key, String value> pairs. The 5 main methods of KeyValueList are int
		 * size() to get the size of KeyValueList String getValue(String key) to
		 * get value given key void addPair(String key, String value) to add
		 * <Key, Value> pair to KeyValueList void setValue(String key, String
		 * value) to set value to specific key String toString()
		 * System.out.print(KeyValueList) could work The following code can be
		 * used to new and send a KeyValueList msg to SISServer KeyValueList msg
		 * = new KeyValueList(); msg.addPair("MsgID","23");
		 * msg.addPair("Description","Connect to SISServer");
		 * msg.addPair("Attribute","Value"); ... ... mEncoder.sendMsg(msg,
		 * universal.getOutputStream()); //This line sends the msg NOTE: Always
		 * check whether all the attributes of a msg are in the KVList before
		 * sending it. Don't forget to send a msg after processing an incoming
		 * msg if necessary. All msgs must have the following 2 attributes:
		 * MsgID and Description. Below are the sending messages' attributes
		 * list: MsgName: Acknowledgement MsgID: 8888 Attrs: MsgName: SendOut
		 * MsgID: 6666 Attrs: For more information about KeyValueList, read
		 * comments in Util.java.
		 ****************************************************/
		case 33:
			/*************************************************
			 * Add code below to process Message MsgName:SPO2_reading MsgID:33
			 * This message has following attributes: , use
			 * KeyValueList.getValue(String key) to get the values. If needed,
			 * don't forget to send a msg after processing. See previous
			 * comments on how to send a message.
			 *************************************************/

			// lname and fname should be obtained from kvList from outpurStream
			String lname = kvList.getValue("LastName");
			String fname = kvList.getValue("FirstName");
			String result1 = kvList.getValue("SPO2");
			String result2 = kvList.getValue("DateTime");
			System.out
					.println("================= Start of message =================");
			System.out.println("Last name: " + lname);
			System.out.println("First name: " + fname);
			System.out.println("MsgID: " + 33);
			System.out.println("Description: SPO2 reading.");
			System.out.println("SPO2 value: " + result1);
			System.out.println("Time: " + result2);
			System.out
					.println("================== End of message ==================\n");

			/*
			 * Uploading functions will be written here
			 */
			sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt,
					emailFromAddress, lname, fname, result1, result2);
			/*
			 * ======================= end ==========================
			 */

			break;
		/*************************************************
		 * Below are system messages. No modification required.
		 *************************************************/
		case 26:
			System.out.println("Connect to SISServer successful.");
			break;
		case 22:
			System.exit(0);
			break;
		case 24:
			System.out.println("Algorithm Activated");
			break;
		case 25:
			System.out.println("Algorithm Deactivated");
			break;
		default:
			break;
		}
	}
}
