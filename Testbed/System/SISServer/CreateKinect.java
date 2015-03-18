import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CreateKinect
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
		msg23.addPair("Name","Kinect");
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
			MsgName: OutMsg1	MsgID: 711	Attrs: Status
			MsgName: OutMsg2	MsgID: 712	Attrs: RankedReport
			MsgName: OutMsg3	MsgID: 26	Attrs: AckMsgID; YesNo; Name
		For more information about KeyValueList, read comments in Util.java.
		****************************************************/
		case 400:
			System.out.println("Message MsgName:Msg1 MsgID:400 received, start processing.");
			KeyValueList msg711 = new KeyValueList();
			msg711.addPair("MsgID","711");
			msg711.addPair("Description","Kinect Action 400");
			// msg711.addPair("Status","3");
			
			mEncoder.sendMsg(msg711, universal.getOutputStream());
			
			
			/*************************************************
			Add code below to process Message MsgName:Msg1 MsgID:701
			This message has following attributes: VoterPhoneNo; CandidateID, use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/



			break;
		case 401:
			System.out.println("Message MsgName:Msg2 MsgID:401 received, start processing.");
			KeyValueList msg712 = new KeyValueList();
			msg712.addPair("MsgID","712");
			msg712.addPair("Description","Kinect Action 401");
			// msg712.addPair("RankedReport","Alex;1");
			
			mEncoder.sendMsg(msg712, universal.getOutputStream());
			
			/*************************************************
			Add code below to process Message MsgName:Msg2 MsgID:702
			This message has following attributes: Passcode; N, use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/



			break;
		case 402:
			System.out.println("Message MsgName:Msg3 MsgID:402 received, start processing.");
			KeyValueList msg26 = new KeyValueList();
			msg26.addPair("MsgID","26");
			msg26.addPair("Description","Kinect Action 402");
			msg26.addPair("AckMsgID","8");
			msg26.addPair("YesNo", "Yes");
			msg26.addPair("Name", "Kinect");
			mEncoder.sendMsg(msg26, universal.getOutputStream());
			
			/*************************************************
			Add code below to process Message MsgName:Msg3 MsgID:703
			This message has following attributes: Passcode; CandidateList, use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/



			break;
		/*************************************************
		Below are system messages. No modification required.
		*************************************************/
			
		case 43:
			
			//Detect 
			int responseId = Integer.parseInt(kvList.getValue("value").charAt(0));
			System.out.println(responseId);


			if(responseId == 1){
				//Guy Fell
			}
			else if(responseId == 2){
				//No Fall 
			}
			else if(responeId == 3){
				//No Movement
			}

			break;

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
