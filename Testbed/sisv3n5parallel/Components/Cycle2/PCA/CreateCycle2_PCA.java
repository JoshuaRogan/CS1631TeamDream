import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import PCA_I.*;

public class CreateCycle2_PCA
{
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;
	static int index;
	static final String cycle = "Cycle2";
	static final String algorithm = "PCA";
	static double P;
	static String OutputPath;
	static String rootdir = "../../Data/";

	public static void main(String[] args) throws Exception
	{
		if (args.length < 3)
        {
            System.out.println("Too few arguments.");
            System.out.println("Run it like this: java CreatePCA PCA_Index P OutputPath");
            System.exit(0);
        }
		universal = new Socket("127.0.0.1", 7999);

		mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		index = Integer.parseInt(args[0]);
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name","Cycle2_PCA_"+index);
		mEncoder.sendMsg(msg23, universal.getOutputStream());
		
		KeyValueList kvList;
		outstream = universal.getOutputStream();
		
		/******************************
		* Reminder: Don't forget to save algorithm arguments in args[]  
		* before going into the following main loop.
		*******************************/
		P = Double.valueOf(args[1]);
		OutputPath = args[2];
		File outdir = new File(rootdir+OutputPath);
		if(!outdir.exists())
			outdir.mkdirs();
		
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
			* 	msg614.addPair("Description", "From algorithm instance Cycle2_PCA, notify TimeController necessary update of environment variables.");
			* 	msg614.addPair("Name", "Cycle2_PCA_"+index);
			* 	msg614.addPair("EnvironmentVarName1", "value");
			* 	msg614.addPair("EnvironmentVarName2", "value");
			* 	mEncoder.sendMsg(msg614, universal.getOutputStream());
			******************************/
       		System.out.println(data);
			PCA_I pca = new PCA_I();
			Object[] out = new Object[1];
			Object[] in = {data,rootdir+"Face\\"+data+"\\",rootdir+OutputPath+"\\",P};
			pca.PCA_I(out, in);			
			
			KeyValueList result = new KeyValueList();
			result.addPair("MsgID","602");
			result.addPair("Description", "Cycle2_PCA Algorithm Analysis Result");
			result.addPair("Name","Cycle2_PCA_"+index);
			result.addPair("Cycle", cycle);
			result.addPair("Algorithm", algorithm);
			result.addPair("Data", data);

			//result.addPair("P", String.valueOf(P));				
			String strtemp = OutputPath+"/"+"PCA_"+ data +"_P"+String.valueOf(P)+".txt";
			result.addPair("Result", strtemp);
			/******************************
			* result.addPair("Result", Your String Representation of the Result);
			*******************************/
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
}
