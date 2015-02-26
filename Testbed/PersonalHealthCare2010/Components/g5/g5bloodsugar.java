/*
 * CS1631 - Personal Health System
 * Blood Sugar Monitor Component
 *
 * Group 5
 * Stephen Paine
 * Hemant Patel
 *
 * Spring 2010
 *
*/

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.lang.Object;

public class g5bloodsugar {
	public static String ipAddress;
	public MsgEncoder mEncoder;
	public MsgDecoder mDecoder;
	//public Date date;
	//public DateFormat dateformat;
	public KeyValueList kvList,temp,userProfile;
	public Socket SISServer;
	private Boolean debug;
	public ArrayList<KeyValueList> readings;
	public Boolean receivedProfile;
	public int i;

	public g5bloodsugar(String ipAddr) {
		try {
			SISServer=new Socket(ipAddr, 7999);
			mEncoder=new MsgEncoder();
			mDecoder=new MsgDecoder(SISServer.getInputStream());
			//date=new Date();
			//dateformat=new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
			kvList=new KeyValueList();
			// Set to false to suppress output of messages
			debug=true;
			readings=new ArrayList<KeyValueList>();
			receivedProfile=false;
		} catch(Exception ex) {
			System.out.println("Error initializing!");
			System.exit(0);
		}
	}

	public void sendAck(int MsgID, String result) {
		KeyValueList tmp=new KeyValueList();
		tmp.addPair("MsgID","26");
		tmp.addPair("AckMsgID",""+MsgID);
		tmp.addPair("Yes/No",result);
		sendMsg(tmp);
	}

	public void sendMsg(KeyValueList contents) {
		try {
			if (debug) {
				System.out.println("Sending message "+contents.getValue("MsgID"));
				System.out.println(contents);
			}
			mEncoder.sendMsg(contents, SISServer.getOutputStream());
		} catch(Exception ex) {
			System.out.println("Error sending message!");
			System.exit(0);
		}
	}

	public void doComponent() {
		//Connect to server
		System.out.println("Connecting to SISServer...");
		kvList.addPair("MsgID","23");
		kvList.addPair("Description","Connect to server");
		kvList.addPair("Passcode","****");
		kvList.addPair("SecurityLevel","3");
		kvList.addPair("Name","g5bloodsugar");
		sendMsg(kvList);

		// Start recieving
		try {
			while(true) {
				boolean append = true;
				if (mDecoder==null) {
					System.out.println("mDecoder null!");
					mDecoder = new MsgDecoder(SISServer.getInputStream());
				}
				kvList = mDecoder.getMsg();
				if (kvList==null) {
					System.out.println("Error!! kvList null!");
				}
				System.out.println("Recieved Msg: ");
				int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
			
				switch (MsgID) {
					case 26:
						// Received Acknowledgement Message
						System.out.println("26-Acknowledgement");
						System.out.println(kvList);
						break;
					case 41:
						// Received Blood Sugar Reading Message
						System.out.println("41-Blood Sugar Reading");
						System.out.println(kvList);

						if (receivedProfile) {
							//sendAck(41,"Yes");
						
							temp = new KeyValueList();
							temp.addPair("MsgID","150");
							temp.addPair("Description","Lookup Reading");
						
							temp.addPair("Blood Sugar",kvList.getValue("Blood Sugar"));
							temp.addPair("DateTime",kvList.getValue("DateTime"));
							//temp.addPair("DateTime",dateformat.format(date));
							temp.addPair("UserName",userProfile.getValue("UserName"));
							temp.addPair("Diabetes",userProfile.getValue("Diabetes"));
							temp.addPair("Age",userProfile.getValue("Age"));
							temp.addPair("Weight",userProfile.getValue("Weight"));
						
							sendMsg(temp);
						} else {
							// Store reading until we get user profile
							readings.add(kvList);
						}
						break;
					case 45:
						// Received Profile Message
						System.out.println("45-User Profile");
						System.out.println(kvList);
						userProfile=kvList;
						receivedProfile=true;
						if (readings.size()>0) {
							for (i=0; i<readings.size(); i++) {
								temp = new KeyValueList();
								temp.addPair("MsgID","150");
								temp.addPair("Description","Lookup Reading");
								temp.addPair("Blood Sugar",readings.get(i).getValue("Blood Sugar"));
								temp.addPair("DateTime",readings.get(i).getValue("DateTime"));
								temp.addPair("UserName",userProfile.getValue("UserName"));
								temp.addPair("Diabetes",userProfile.getValue("Diabetes"));
								temp.addPair("Age",userProfile.getValue("Age"));
								temp.addPair("Weight",userProfile.getValue("Weight"));
						
								sendMsg(temp);
							}
							readings.clear();
						}

						break;
					case 151:
						// Received Return Results from KB Message
						System.out.println("151-Return Results (from KB)");
						System.out.println(kvList);
						//sendAck(151,"Yes");
						// Always alert, so patient knows when blood sugar is normal
						//if (!kvList.getValue("Diagnosis").equals("Normal")) {
							temp=new KeyValueList();
							temp.addPair("MsgID","42");
							temp.addPair("Description","Blood Sugar Alert");
							//temp.addPair("Name","g5 Blood Sugar Monitor");
							temp.addPair("Name","g5bloodsugar");
							//temp.addPair("Alert Type","Blood Sugar Alert -- Blood Sugar is "+kvList.getValue("Diagnosis"));
							temp.addPair("Blood Sugar",kvList.getValue("Blood Sugar"));
							temp.addPair("Alert Type","Blood Sugar Alert");
							temp.addPair("Diagnosis",kvList.getValue("Diagnosis"));
							temp.addPair("Suggestions",kvList.getValue("Suggestions"));
							temp.addPair("DateTime",kvList.getValue("DateTime"));
							//temp.addPair("DateTime",dateformat.format(date));
							sendMsg(temp);
						//}
						break;
					default:
						//Shouldn't receive any not for me, just default backup case
						System.out.printf("%d-Not for me\n",MsgID);
						System.out.println(kvList);
				}

			}
		} catch(Exception ex) {
			System.out.println("Exception while reading message from SISServer.");
		}

	}

	public static void main(String[] args) {
		// Check for correct # of arguments
		if (args.length != 1) {
			System.out.println("Too few arguments.");
			System.out.println("\tTo Run g5bloodsugar Component, use command:\n");
			System.out.println("\tjava g5bloodsugar <SISServer IP>\n");
			System.exit(0);
		 }

		ipAddress=args[0];

		//Run component in thread so we can quit gracefully (sort of)
		Thread monitor = new Thread(new Runnable() {
			public void run() {
				g5bloodsugar BSM=new g5bloodsugar(ipAddress);
				BSM.doComponent();
			}
		});

		monitor.setDaemon(true);
		monitor.start();

		System.out.println( "Hit Enter to stop.\n" );
		try {
			System.in.read();
		} catch (Throwable thr) {};
	}
}
