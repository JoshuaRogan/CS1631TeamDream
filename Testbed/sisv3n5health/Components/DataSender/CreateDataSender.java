import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CreateDataSender
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
		msg23.addPair("Name","DataSender");
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
			MsgName: SPO2 data	MsgID: 1001	Attrs: SPO2Data
			MsgName: BloodSugar data	MsgID: 1004	Attrs: BloodSugarData
			MsgName: BloodPressure data	MsgID: 1006	Attrs: BloodPressureData
			MsgName: EKG data	MsgID: 1008	Attrs: EKGData
		For more information about KeyValueList, read comments in Util.java.
		****************************************************/
		case 1009:
			System.out.println("Message MsgName:Dummy Input MsgID:1009 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:Dummy Input MsgID:1009
			This message has following attributes: InputData, use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			msg = new KeyValueList();
			msg.addPair("MsgID","1001");
			msg.addPair("Description","SPO2 data");
			msg.addPair("SPO2Data","Value");
			mEncoder.sendMsg(msg, universal.getOutputStream());
			msg = new KeyValueList();
			msg.addPair("MsgID","1004");
			msg.addPair("Description","BloodSugar data");
			msg.addPair("BloodSugarData","Value");
			mEncoder.sendMsg(msg, universal.getOutputStream());
			msg = new KeyValueList();
			msg.addPair("MsgID","1006");
			msg.addPair("Description","BloodPressure data");
			msg.addPair("BloodPressureData","Value");
			mEncoder.sendMsg(msg, universal.getOutputStream());
			msg = new KeyValueList();
			msg.addPair("MsgID","1008");
			msg.addPair("Description","EKG data");
			msg.addPair("EKGData","Value");
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
