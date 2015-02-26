import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CreateResultUploader
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
		msg23.addPair("Name","ResultUploader");
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
		KeyValueList msg;
		
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
			MsgName: Dummy Output	MsgID: 1010	Attrs: OutputData
		For more information about KeyValueList, read comments in Util.java.
		****************************************************/
		case 1002:
			System.out.println("Message MsgName:SPO2 data analysis result MsgID:1002 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:SPO2 data analysis result MsgID:1002
			This message has following attributes: SPO2Data; SPO2Result, use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/

			msg = new KeyValueList();
			msg.addPair("MsgID","1010");
			msg.addPair("Description","Dummy Output");
			msg.addPair("OutputData","Value");
			mEncoder.sendMsg(msg, universal.getOutputStream());


			break;
		case 1003:
			System.out.println("Message MsgName:BloodSugar data result analysis MsgID:1003 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:BloodSugar data result analysis MsgID:1003
			This message has following attributes: BloodSugarData; BloodSugarResult, use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			msg = new KeyValueList();
			msg.addPair("MsgID","1010");
			msg.addPair("Description","Dummy Output");
			msg.addPair("OutputData","Value");
			mEncoder.sendMsg(msg, universal.getOutputStream());


			break;
		case 1005:
			System.out.println("Message MsgName:BloodPressure data analysis result MsgID:1005 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:BloodPressure data analysis result MsgID:1005
			This message has following attributes: BloodPressureResult; BloodPressureData, use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/


			msg = new KeyValueList();
			msg.addPair("MsgID","1010");
			msg.addPair("Description","Dummy Output");
			msg.addPair("OutputData","Value");
			mEncoder.sendMsg(msg, universal.getOutputStream());
			break;
		case 1007:
			System.out.println("Message MsgName:EKG data analysis result MsgID:1007 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:EKG data analysis result MsgID:1007
			This message has following attributes: EKGDataResult; EKGData, use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			msg = new KeyValueList();
			msg.addPair("MsgID","1010");
			msg.addPair("Description","Dummy Output");
			msg.addPair("OutputData","Value");
			mEncoder.sendMsg(msg, universal.getOutputStream());


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
