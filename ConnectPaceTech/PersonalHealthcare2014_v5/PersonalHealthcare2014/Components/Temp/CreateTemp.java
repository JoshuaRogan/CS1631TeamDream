import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;
import java.util.*;

public class CreateTemp
{
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;

	public static void main(String[] args) throws Exception
	{
	    Runtime.getRuntime().exec("cmd /c C:/Pcsensor/TEMPer/TEMPerV21.exe");
		universal = new Socket("127.0.0.1", 7999);

		mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name","Temp");
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
			MsgName: TempToGUI	MsgID: 1018	Attrs:
		For more information about KeyValueList, read comments in Util.java.
		****************************************************/
		case 1019:
			System.out.println("Message MsgName:GUIToTemp MsgID:1019 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:GUIToTemp MsgID:1019
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			//int counter = 0;
			Timer timer = new Timer();
			timer.schedule(new TimerTask(){
            public void run(){
                File relative = new File("C:\\Users\\Josh\\OneDrive\\Git\\CS1631TeamDream\\ConnectPaceTech\\Temp\\TempSensor");
                String dir = relative.getAbsolutePath();
                File directory = new File(dir);
                File[] files = directory.listFiles();
                if (files.length == 1 && files[0].getName().charAt(0) == '.'){
                    System.out.println("No available file found");
                }else{
                    String fileName = dir + "/" + files[files.length - 1].getName();
                    try{
                        BufferedReader br = new BufferedReader(new FileReader(fileName));
                        String lastLine = null, temp;
                        while((temp = br.readLine()) != null){
                            lastLine = temp;
                        }
                        String[] str = lastLine.split(",");
                        System.out.println(str[2]+ "    " + str[1] );
						
						KeyValueList msgToTemp = new KeyValueList();
						msgToTemp.addPair("MsgID","1018");
						msgToTemp.addPair("Temp_enable","true");
						msgToTemp.addPair("Date",str[2]);
						msgToTemp.addPair("Temp",str[1]);
						System.out.println("Send to GUI...");
						mEncoder.sendMsg(msgToTemp, universal.getOutputStream());
                        br.close();
                    }catch(IOException ex){
                        System.out.println(ex.toString());
                        System.out.println("Could not find file");
                    }
                }
                
            }
        },0, 5*1000);

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
