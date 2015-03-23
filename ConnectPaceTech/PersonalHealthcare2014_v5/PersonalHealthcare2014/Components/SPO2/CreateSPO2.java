import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CreateSPO2
{
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;

	public static void main(String[] args) throws Exception
	{
		universal = new Socket("127.0.0.1", 7999);

		mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name","SPO2");
		mEncoder.sendMsg(msg23, universal.getOutputStream());
		
		KeyValueList kvList;
		outstream = universal.getOutputStream();
		
		while(true)
		{	
			kvList = mDecoder.getMsg();
			ProcessMsg(kvList);
		}	
	}
	
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
			MsgName: SPO2ToGUI	MsgID: 1004	Attrs:
		For more information about KeyValueList, read comments in Util.java.
		****************************************************/
		case 33:
			System.out.println("Message MsgName:SPO2_reading MsgID:33 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:SPO2_reading MsgID:33
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/



			break;
		case 1005:
			System.out.println("Message MsgName:GUIToSPO2 MsgID:1005 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:GUIToSPO2 MsgID:1005
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			//String SPO2_enable = kvList.getValue("SPO2_enable");
			//if(SPO2_enable.equals("true"))
			//	System.out.println("SPO2 is selected.");
			System.out.println("yoyo");
			File file = new File("C:\\Users\\Josh\\OneDrive\\Git\\CS1631TeamDream\\ConnectPaceTechPersonalHealthcare2014_v5\\PersonalHealthcare2014\\xml\\DataXML\\SPO2_reading.XML");
					if(!file.exists())
						System.out.println("File: "+ file.getName()+ " does not exist!");
					else
						System.out.println("Load: "+ file.getName());
					
					String filename = file.getName();
					System.out.println("loading: " + filename);
					if ((filename.endsWith(".xml")) || (filename.endsWith(".XML"))){
						KeyValueList tempkvlist = SIS_XMLUtil.readFromXML(file);
					    if (tempkvlist != null){
					    	String lname = tempkvlist.getValue("LastName");
		        			String fname = tempkvlist.getValue("FirstName");
		        			String time = tempkvlist.getValue("DateTime");
		        			String spo2 = tempkvlist.getValue("SPO2");
		        			System.out.println("SPO2 Monitor:");
		        			System.out.println("LastName: " + lname + " FirstName: " + fname + " SPO2: " + spo2 + " Date: " + time);

					    	KeyValueList msgToSPO2 = new KeyValueList();
							msgToSPO2.addPair("MsgID","1004");
							msgToSPO2.addPair("SPO2_enable","true");
							msgToSPO2.addPair("LastName",lname);
							msgToSPO2.addPair("FirstName",fname);
							msgToSPO2.addPair("Date",time);
							msgToSPO2.addPair("SPO2",spo2);
							System.out.println("Send to GUI...");
							mEncoder.sendMsg(msgToSPO2, universal.getOutputStream());
					    }
					    else{
					    	System.out.println("Error: File " + filename + " could not be loaded.");
					    }
					}


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
