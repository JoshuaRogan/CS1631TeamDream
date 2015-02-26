import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;


public class CreateCycle1_S3
{
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;
	static int index;
	static final String cycle = "Cycle1";
	static final String algorithm = "S3";

	public static void main(String[] args) throws Exception
	{
		if (args.length < 1)
        {
            System.out.println("Too few arguments. At least an index argument is required.");
            System.exit(0);
        }

		universal = new Socket("127.0.0.1", 7999);

		mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		index = Integer.parseInt(args[0]);
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name","Cycle1_S3_"+index);
		mEncoder.sendMsg(msg23, universal.getOutputStream());
		
		KeyValueList kvList;
		outstream = universal.getOutputStream();
		
		/******************************
		* Reminder: Don't forget to save algorithm arguments in args[]  
		* before going into the following main loop.
		*******************************/
		
		while(true)
		{	
			kvList = mDecoder.getMsg();
			ProcessMsg(kvList);
		}
		
	}
	
	static void ProcessMsg(KeyValueList kvList) throws Exception
	{
		int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
		String data; 
		String Result;
		
		switch(MsgID)
		{
		case 26:
			System.out.println("Connect to SISServer successful.");
			break;
		case 601:
			if(!cycle.equals(kvList.getValue("Cycle")) || !algorithm.equals(kvList.getValue("Algorithm")))
				break;
			
			long starttime = System.currentTimeMillis();
			System.out.println("Start Processing Data");
			data = kvList.getValue("Data");
			/*****************************
			* This is the main part of the algorithm.
			* Add code here to process the test data. The testdata is String data above.
			* Add algorithm's process result below, then uncomment the line.
			* Reminder: Don't forget the algorithm might have input arguments.
			* NOTE: You can update TimeController's environment variables if necessary using Msg614 (code below)
			* 	KeyValueList msg614 = new KeyValueList();
			* 	msg614.addPair("MsgID", "614");
			* 	msg614.addPair("Description", "From algorithm instance Cycle1_S3, notify TimeController necessary update of environment variables.");
			* 	msg614.addPair("Name", "Cycle1_S3_"+index);
			* 	msg614.addPair("EnvironmentVarName1", "value");
			* 	msg614.addPair("EnvironmentVarName2", "value");
			* 	mEncoder.sendMsg(msg614, universal.getOutputStream());
			******************************/
			

			System.out.println("The input data is: " + data);
			Result = sequence(data);
			System.out.println("The result data is: "+ Result);			
			
			
			KeyValueList result = new KeyValueList();
			result.addPair("MsgID","602");
			result.addPair("Description", "Cycle1_S3 Algorithm Analysis Result");
			result.addPair("Name","Cycle1_S3_"+index);
			result.addPair("Cycle", cycle);
			result.addPair("Algorithm", algorithm);
			result.addPair("Data", data);
			
			result.addPair("Result", Result);
			
			result.addPair("Runtime", String.valueOf((System.currentTimeMillis()-starttime)/1000.0));
			mEncoder.sendMsg(result, universal.getOutputStream());
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
	
	static String sequence(String data) {
		String Result="";
		int nextInt;
		int len = data.split(" ").length;
		int start = Integer.parseInt(data.split(" ")[len-1])+1;
		for (int i = 0; i<len; i++){
			nextInt = start+i;
		    Result += Integer.toString(nextInt)+" ";
		}
		return Result.substring(0, Result.length()-1);
	}
}
