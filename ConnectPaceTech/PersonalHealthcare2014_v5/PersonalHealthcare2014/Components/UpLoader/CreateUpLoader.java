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

public class CreateUpLoader
{
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;

	// variables for sending emails
	static final String SMTP_HOST_NAME = "smtp.ksiresearch.org.ipage.com";
	static final String SMTP_PORT = "587";
	static final String emailMsgTxt = "Test Message Contents";
	static final String emailSubjectTxt = "Personal Healthcare Data From SIS System."; // title
	static final String emailFromAddress = "seke@ksiresearch.org";
	static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	static final String[] sendTo = { "molimomo0@gmail.com" }; //This is receiver


	public static void main(String[] args) throws Exception
	{
		universal = new Socket("127.0.0.1", 7999);

		mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name","UpLoader");
		mEncoder.sendMsg(msg23, universal.getOutputStream());
		
		KeyValueList kvList;
		outstream = universal.getOutputStream();
		int TestInit = 0;
		
		while(true)
		{	
			kvList = mDecoder.getMsg();
			String lname = kvList.getValue("LastName");
			String fname = kvList.getValue("FirstName");	
			String spo2 = kvList.getValue("SPO2");
			String time = kvList.getValue("Date");
			String systolic = kvList.getValue("Systolic");
			String diastolic = kvList.getValue("Diastolic");
			String pulse = kvList.getValue("Pulse");
			String email = "mal207@pitt.edu";
			
			System.out.println("Test: " + fname);
			if(TestInit > 0){
				String url = "http://ksiresearch.org/chronobot/PHP_Post.php";
				String query = "Insert into users (last_name, first_name,email, date,SPO2,Systolic,Diastolic,Pulse) values ('" 
						+ lname + "','"  + fname + "','" + email+ "','"+time + "','"+spo2 +"','"+systolic+"','"+diastolic+"','"+pulse+"')";
				System.out.println(query);
				PostQuery.PostToPHP(url,query);
			}
			TestInit++;
			ProcessMsg(kvList);
		}	
	}
	/*
	 * Method for sending email
	 */
	static void sendSSLMessage(String recipients[], String subject,
			String message, String from, String lname, String fname,
			String spo2, String systolic, String diastolic, String pulse, String time) throws MessagingException {

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
						return new PasswordAuthentication("seke@ksiresearch.org", "Xiaoyu");
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
					part.setContent("Here is the SPO2 and Blood Pressure data(This is an automatic massage send from SIS system): LastName: " 
					+ lname + " FirstName: " + fname 
							+ "\nSPO2: " + spo2 
							+ "\nSystolic: " + systolic
							+ "\nDiastolic: " + diastolic
							+ "\nPulse: " + pulse , 
							"text/html");
					newMultipart.addBodyPart(part);
				}
			}
			BodyPart part = new MimeBodyPart();
			part.setText("Here is the SPO2 and Blood Pressure data(This is an automatic massage send from SIS system): LastName: " 
					+ lname + " FirstName: " + fname 
					+ "\nSPO2: " + spo2 + " " 
					+ "\nSystolic: " + systolic
					+ "\nDiastolic: " + diastolic
					+ "\nPulse: " + pulse);
			multipart.addBodyPart(part);
			msg.setContent(multipart);
		}
		Transport.send(msg);
		System.out.println("Sucessfully Sent mail to All Users, lol.\n");
	}
	// ============= end of sending email ====================

	static void ProcessMsg(KeyValueList kvList) throws Exception
	{
		int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
		
		switch(MsgID)
		{
		/****************************************************
		Below are the main part of the component program. All received msgs are encoded as a KeyValueList kvList.
		kvList is a vector of <String key, String value> pairs. The 5 main methods of KeyValueList are 
			int size()                                    to get the size of KeyValueList
			String getValue(String key)                   to get value given key
			void addPair(String key, String value)        to add <Key, Value> pair to KeyValueList
			void setValue(String key, String value)       to set value to specific key
			String toString()                             System.out.print(KeyValueList) could work
		The following code can be used to new and send a KeyValueList msg to SISServer
			KeyValueList msg = new KeyValueList();
			msg.addPair("MsgID","23");
			msg.addPair("Description","Connect to SISServer");
			msg.addPair("Attribute","Value");
			... ...
			mEncoder.sendMsg(msg, universal.getOutputStream()); //This line sends the msg
		NOTE: Always check whether all the attributes of a msg are in the KVList before sending it.
		Don't forget to send a msg after processing an incoming msg if necessary.
		All msgs must have the following 2 attributes: MsgID and Description.
		Below are the sending messages' attributes list:
			MsgName: Acknowledgement	MsgID: 8888	Attrs:
			MsgName: SendOut	MsgID: 6666	Attrs:
			MsgName: UploaderToGUI	MsgID: 1008	Attrs:
		For more information about KeyValueList, read comments in Util.java.
		****************************************************/
		case 1009:
			System.out.println("Message MsgName:GUIToUploader MsgID:1009 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:GUIToUploader MsgID:1009
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			String lname = kvList.getValue("LastName");
			String fname = kvList.getValue("FirstName");
			String spo2 = kvList.getValue("SPO2");
			String time = kvList.getValue("Date");
			String systolic = kvList.getValue("Systolic");
			String diastolic = kvList.getValue("Diastolic");
			String pulse = kvList.getValue("Pulse");
			String emails = kvList.getValue("Email");
			System.out
					.println("================= Start of message =================");
			System.out.println("Time: " + time);
			System.out.println("Last name: " + lname);
			System.out.println("First name: " + fname);
			System.out.println("SPO2: " + spo2);
			System.out.println("Systolic: "+systolic);
			System.out.println("Diastolic: "+diastolic);
			System.out.println("Pulse: "+pulse);
			System.out.println("Emails: " + emails);
			System.out
					.println("================== End of message ==================\n");

			/*
			 * Uploading functions will be written here
			 */
			StringTokenizer st = new StringTokenizer(emails, ",");
			ArrayList<String> addrs = new ArrayList<String>();
			int receiverCnt = 0;
			while (st.hasMoreTokens()){
				receiverCnt++;
				addrs.add(st.nextToken());
			}
			String[] re = new String[receiverCnt];
			for(int i=0;i<receiverCnt;i++){
				re[i] = addrs.get(i);
				System.out.println(re[i]);
			}

			KeyValueList msgToGUI = new KeyValueList();
			msgToGUI.addPair("MsgID","1008");
			msgToGUI.addPair("Responces",emails);
			mEncoder.sendMsg(msgToGUI, universal.getOutputStream());


			//sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt,
			//		emailFromAddress, lname, fname, spo2);
			sendSSLMessage(re, emailSubjectTxt, emailMsgTxt,
					emailFromAddress, lname, fname, spo2, systolic, diastolic, pulse, time);
			/*
			 * ======================= end ==========================
			 */






			break;
			case 1017:
			System.out.println("Message MsgName:KinectToUploader MsgID:1017 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:KinectToUploader MsgID:1017
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/



			break;
		/*************************************************
		Below are system messages. No modification required.
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
