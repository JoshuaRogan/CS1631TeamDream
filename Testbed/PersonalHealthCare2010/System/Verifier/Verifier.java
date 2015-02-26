import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

class Verifier
{	
	static ArrayList<KeyValueList> todo = new ArrayList<KeyValueList>();
	static String curevacomp = null;
	static String curevaresult = null;
	static String type = null;
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	
	public static void main(String []args) throws Exception
	{	
		if (args.length < 1)
        {
            System.out.println("Too few arguments.");
            System.out.println("Run it like this: java Verifier SISServerIP");
            System.exit(0);
        }

		Socket universal = new Socket(args[0], 7999);

		mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Name","Verifier");
		mEncoder.sendMsg(msg23, universal.getOutputStream());
		
		KeyValueList kvList;
		outstream = universal.getOutputStream();
		
		
		while(true)
		{	
			kvList = mDecoder.getMsg();
			ProcessMsg(kvList);
			
		}
		
	}
	
	static void ProcessMsg(KeyValueList kvList) throws IOException
	{
		int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
		
		switch(MsgID)
		{
		case 42:
			if(curevacomp!=null)
			{
				todo.add(kvList);
				break;
			}
			KeyValueList serverquery = new KeyValueList();
			serverquery.addPair("MsgID","201");
			serverquery.addPair("Description","Inquire Blood Sugar Empirical Knowledge");
			serverquery.addPair("Passcode","1234");
			serverquery.addPair("Sex",kvList.getValue("Sex"));
			serverquery.addPair("Age",kvList.getValue("Age"));
			serverquery.addPair("Weight",kvList.getValue("Weight"));
			serverquery.addPair("Height",kvList.getValue("Height"));
			serverquery.addPair("Diabetes",kvList.getValue("Diabetes"));
			serverquery.addPair("Heart Disease",kvList.getValue("Heart Disease"));
			serverquery.addPair("Meal",kvList.getValue("Meal"));
			serverquery.addPair("Blood Sugar",kvList.getValue("Blood Sugar"));
			serverquery.addPair("Inquirer","SISServer");
			mEncoder.sendMsg(serverquery, outstream);
			curevacomp = kvList.getValue("Name");
			curevaresult = kvList.getValue("Diagnosis");
			type = "BloodSugarMonitor";
			break;
		case 202:
			if(kvList.getValue("Inquirer").equals("SISServer"))
			{
				KeyValueList msg300 = new KeyValueList();
				msg300.addPair("MsgID","300");
				msg300.addPair("Description","Correctness of Component Performance");
				msg300.addPair("CompName",curevacomp);
				msg300.addPair("Type",type);
				if(kvList.getValue("Diagnosis") != null)
				{
					if(kvList.getValue("Diagnosis").equalsIgnoreCase(curevaresult))
						msg300.addPair("Correctness","Yes");
					else
						msg300.addPair("Correctness","No");
					mEncoder.sendMsg(msg300, outstream);
				}
				curevacomp = null;
				curevaresult = null;
				type = null;
			}
			if(todo.size()!=0)
				ProcessMsg(todo.remove(0));
			break;
		default:
			break;
		}
	}
}