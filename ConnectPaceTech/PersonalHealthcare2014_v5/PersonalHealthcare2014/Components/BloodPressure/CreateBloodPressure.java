import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CreateBloodPressure
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
		msg23.addPair("Name","BloodPressure");
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
			MsgName: BPToGUI	MsgID: 1006	Attrs:
		For more information about KeyValueList, read comments in Util.java.
		****************************************************/
		case 1001:
			System.out.println("Message MsgName:BloodPressure_reading MsgID:1001 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:BloodPressure_reading MsgID:1001
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/



			break;
		case 1007:
			System.out.println("Message MsgName:GUIToBP MsgID:1007 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:GUIToBP MsgID:1007
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			//String BP_enable = kvList.getValue("BloodPressure_enable");
			//if(BP_enable.equals("true"))
			//	System.out.println("BP is selected.");

			File file = new File("C:\\Users\\Josh\\OneDrive\\Git\\CS1631TeamDream\\ConnectPaceTech\\PersonalHealthcare2014_v5\\PersonalHealthcare2014\\xml\\DataXML\\BloodPressure_reading.XML");
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
		        			String systolic = tempkvlist.getValue("Systolic");
							String diastolic = tempkvlist.getValue("Diastolic");
							String pulse = tempkvlist.getValue("Pulse");
							/*String desc = tempkvlist.getValue("Description");
							desc = desc 
								+ "\nSystolic: " + systolic
								+ "\nDiastolic: " + diastolic
								+ "\nPulse: " + pulse;*/
		        			System.out.println("BloodPressure Monitor:");
		        			System.out.println("LastName: " + lname + " FirstName: " + fname 
		        				+ " Systolic: " + systolic 
		        				+ " Diastolic: " + diastolic
		        				+ " Pulse: " + pulse
		        				+ " Date: " + time);

					    	KeyValueList msgToGUI = new KeyValueList();
							msgToGUI.addPair("MsgID","1006");
							msgToGUI.addPair("BP_enable","true");
							msgToGUI.addPair("LastName",lname);
							msgToGUI.addPair("FirstName",fname);
							msgToGUI.addPair("Date",time);
							msgToGUI.addPair("Systolic",systolic);
							msgToGUI.addPair("Diastolic",diastolic);
							msgToGUI.addPair("Pulse",pulse);
							
							mEncoder.sendMsg(msgToGUI, universal.getOutputStream());
					    }
					    else{
					    	System.out.println("Error: File " + filename + " could not be loaded.");
					    }
					    //CreateGUI.this.displayKvList(tempkvlist, "s");
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
