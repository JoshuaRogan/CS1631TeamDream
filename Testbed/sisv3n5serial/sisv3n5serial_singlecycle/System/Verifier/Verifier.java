import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Verifier
{
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;
	
	private static String configfile = "../Config/Config.txt";
	private static ArrayList<Cycle> cycles = new ArrayList<Cycle>();

	public static void main(String[] args) throws Exception
	{
		LoadConfig(true);
		LoadConfig(false);
		
		universal = new Socket("127.0.0.1", 7999);

		mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
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
	
	static void ProcessMsg(KeyValueList kvList) throws Exception
	{
		int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
		String data; 
		String tempcyclestr, tempcompstr, tempdatastr, tempresultstr;
		Cycle tempcycle;
		KeyValueList result;
		
		switch(MsgID)
		{
		case 26:
			System.out.println("Connect to SISServer successful.");
			break;
		case 602:
			tempcyclestr = kvList.getValue("Cycle"); 
			tempcompstr = kvList.getValue("Algorithm");
			tempdatastr = kvList.getValue("Data");
			tempresultstr = kvList.getValue("Result");
			tempcycle = cycles.get(findCycle(tempcyclestr));
			result = new KeyValueList();
			result.addPair("MsgID","603");
			result.addPair("Description", "From Verifier, the evaluation of cycle "+tempcyclestr+" algorithm "+tempcompstr+" performance.");
			result.addPair("Name","Verifier");
			result.addPair("Cycle", tempcyclestr);
			result.addPair("Algorithm", tempcompstr);
			result.addPair("CompName", kvList.getValue("Name"));
			result.addPair("Data", tempdatastr);
			result.addPair("Result", tempresultstr);
			result.addPair("Evaluation", String.valueOf(evaluatePerformance(tempcyclestr, tempcompstr, tempdatastr, tempresultstr, tempcycle.findTestData(tempdatastr).result)));
			result.addPair("Runtime", kvList.getValue("Runtime"));
			mEncoder.sendMsg(result, universal.getOutputStream());
			break;
		case 22:
			System.exit(0);
			break;
		case 24:
			System.out.println("Verifier Activated");
			break;
		case 25:
			System.out.println("Verifier Deactivated");
			break;
		default:
			break;
		}
	}
	
	/*************************************
	 * The following function 
	 * 	evaluatePerformance(String pcycle, String palgorithm, String ptestdata, String presult, String pexpectedresult)
	 * is the main function of Verifier. 
	 * Implement the function to give proper evaluation of algorithm instance's performance.
	 * NOTE: The performance value should follow the rule: the bigger the value, the better the performance is.
	 *************************************/
	
	static double evaluatePerformance(String pcycle, String palgorithm, String ptestdata, String presult, String pexpectedresult)
	{
		/*************************************
		 * Modify the code to give proper evaluation of algorithm instance's performance.
		 * 	pcycle is algorithm instance's cycle type.
		 * 	palgorithm is algorithm instance's algorithm type.
		 * 	ptestdata is algorithm instance's input test data.
		 * 	presult is algorithm instance's calculated result for ptestdata.
		 * 	pexpectedresult is the expected result for ptestdata in the Config.txt.
		 * 	Reminder: pexpectedvalue might be null.
		 *************************************/
		String result [] = presult.split(" ");
		int [] Result = new int[result.length];
		for(int i=0;i<result.length;i++)
			Result[i] = Integer.parseInt(result[i]);
		
		String expresult [] = pexpectedresult.split(" ");
		int [] expResult = new int[expresult.length];
		for(int i=0;i<expresult.length;i++)
			expResult[i] = Integer.parseInt(expresult[i]);
		
		double dif = 0;
		for(int j =0; j<result.length;j++){
			dif += Math.pow(Math.abs(expResult[j]-Result[j]),2);
		}		
		
		dif = 0 - Math.sqrt(dif);
		return dif;
	}
	
	static void LoadConfig(boolean firstscan) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(configfile));
		String temp;
		boolean inblock = false;
		String blockname = "";
		String blockcontent = "";
		while(br.ready())
		{
			temp = br.readLine().trim();
			if(temp.startsWith("//") || temp.startsWith("$$") || temp.startsWith("/*"))
				continue;
			if(temp.startsWith("BEGIN ") || temp.startsWith("begin ") || temp.startsWith("Begin "))
			{
				if(inblock)
				{
					System.out.println("Error in config file, failed to start");
					System.exit(0);
				}
				inblock = true;
				blockname = temp.substring(temp.indexOf(' ')).trim();
				blockcontent = "";
				continue;
			}
			if(temp.startsWith("END ") || temp.startsWith("end ") || temp.startsWith("End "))
			{
				if(!inblock || !temp.substring(temp.indexOf(' ')).trim().equalsIgnoreCase(blockname))
				{
					if(blockname.indexOf(' ')>=0 && temp.substring(temp.indexOf(' ')).trim().equalsIgnoreCase(blockname.substring(0, blockname.indexOf(' ')).trim()))
					{
						inblock = false;
						if(firstscan)
						{
							if(blockname.equalsIgnoreCase("CycleList"))
							{
								ProcessBlock(blockname, blockcontent);
								br.close();
								return;
							}
						}
						else
						{
							if(!blockname.equalsIgnoreCase("CycleList"))
								ProcessBlock(blockname, blockcontent);
						}
						blockname = "";
						blockcontent = "";
						continue;
					}
					System.out.println("Error in config file, failed to start");
					System.exit(0);
				}
				inblock = false;
				if(firstscan)
				{
					if(blockname.equalsIgnoreCase("CycleList"))
					{
						ProcessBlock(blockname, blockcontent);
						br.close();
						return;
					}
				}
				else
				{
					if(!blockname.equalsIgnoreCase("CycleList"))
						ProcessBlock(blockname, blockcontent);
				}
				blockname = "";
				blockcontent = "";
				continue;
			}
			if(inblock)
			{
				blockcontent = blockcontent+temp+"\n"; 
			}
		}
		br.close();
	}
	
	static void ProcessBlock(String blockname, String blockcontent)
	{
		String temp;
		if(blockname.equalsIgnoreCase("CycleList"))
		{
			cycles.clear();
			Scanner sc = new Scanner(blockcontent);
			while(sc.hasNextLine())
			{
				temp = sc.nextLine().trim();
				if(temp.length() == 0)
					continue;
				cycles.add(new Cycle(temp.substring(0, temp.indexOf('\t')).trim()));
			}
		}
		Cycle tempcycle;
		if(blockname.indexOf(' ')>=0 && blockname.substring(0, blockname.indexOf(' ')).trim().equalsIgnoreCase("CycleTestData"))
		{
			if(findCycle(blockname.substring(blockname.indexOf(' ')).trim()) == -1)
			{
				System.out.println("Error in config file, failed to start");
				System.exit(0);
			}
			tempcycle = cycles.get(findCycle(blockname.substring(blockname.indexOf(' ')).trim()));
			Scanner sc = new Scanner(blockcontent);
			while(sc.hasNextLine())
			{
				temp = sc.nextLine().trim();
				if(temp.length() == 0)
					continue;
				if(temp.indexOf('\t')>=0)
					tempcycle.testdata.add(new TestData(temp.substring(0, temp.indexOf('\t')).trim(), temp.substring(temp.indexOf('\t')).trim()));
				else
					tempcycle.testdata.add(new TestData(temp.trim()));
			}
		}
	}
	
	static class TestData
	{
		String data;
		String result;
		
		TestData(String in)
		{
			data = in;
			result = null;
		}
		
		TestData(String in1, String in2)
		{
			data = in1;
			result = in2;
			if(result.equalsIgnoreCase("null"))
				result = null;
		}
	}
	
	static class Cycle
	{
		String name;
		ArrayList<TestData> testdata = new ArrayList<TestData>();
		
		Cycle(String in)
		{
			name = in;
		}
		
		TestData findTestData(String in)
		{
			for(int i=0; i<testdata.size(); i++)
				if(testdata.get(i).data.equals(in))
					return testdata.get(i);
			return null;
		}
	}
	
	static int findCycle(String in)
	{
		for(int i=0; i<cycles.size(); i++)
			if(cycles.get(i).name.equals(in))
				return i;
		return -1;
	}
}
