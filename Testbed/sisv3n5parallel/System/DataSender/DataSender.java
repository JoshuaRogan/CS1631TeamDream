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


public class DataSender
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
		String data; 
		String tempcyclestr, tempcompstr;
		Cycle tempcycle;
		KeyValueList result;
		
		switch(MsgID)
		{
		case 26:
			System.out.println("Connect to SISServer successful.");
			break;
		case 613:
			for(int i=0; i<cycles.size(); i++)
				cycles.get(i).resetall();
			break;
		case 631:
			tempcyclestr = kvList.getValue("Cycle"); 
			tempcompstr = kvList.getValue("Algorithm");
			tempcycle = cycles.get(findCycle(tempcyclestr));
			if(tempcycle.hasMoreData(tempcompstr))
			{
				result = new KeyValueList();
				result.addPair("MsgID","601");
				result.addPair("Description", "From DataSender, the test data message.");
				result.addPair("Name","DataSender");
				result.addPair("Cycle", tempcyclestr);
				result.addPair("Algorithm", tempcompstr);
				result.addPair("Data", tempcycle.getNextData(tempcompstr));
				mEncoder.sendMsg(result, universal.getOutputStream());
			}
			else
			{
				tempcycle.resettestdataindex(tempcompstr);
				result = new KeyValueList();
				result.addPair("MsgID","604");
				result.addPair("Description", "From DataSender, notify Eliminator all data sent.");
				result.addPair("Name","DataSender");
				result.addPair("Cycle", tempcyclestr);
				result.addPair("Algorithm", tempcompstr);
				mEncoder.sendMsg(result, universal.getOutputStream());
			}
			break;
		case 22:
			System.exit(0);
			break;
		case 24:
			System.out.println("DataSender Activated");
			break;
		case 25:
			System.out.println("DataSender Deactivated");
			break;
		default:
			break;
		}
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
					tempcycle.testdata.add(temp.substring(0, temp.indexOf('\t')).trim());
				else
					tempcycle.testdata.add(temp.trim());
			}
		}
		if(blockname.indexOf(' ')>=0 && blockname.substring(0, blockname.indexOf(' ')).trim().equalsIgnoreCase("CycleAlgorithm"))
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
				if(tempcycle.findComp(temp.substring(0, temp.indexOf('\t')).trim())==-1)
					tempcycle.comps.add(new Comp(temp.substring(0, temp.indexOf('\t')).trim()));
			}
		}
	}
	
	static class Comp
	{
		String name;
		int dataindex;
		
		Comp(String in)
		{
			name = in;
			dataindex = 0;
		}
	}
	
	static class Cycle
	{
		String name;
		ArrayList<String> testdata = new ArrayList<String>();
		ArrayList<Comp> comps = new ArrayList<Comp>();
		
		Cycle(String in)
		{
			name = in;
		}
		
		boolean hasMoreData(String in)
		{
			if(findComp(in)==-1)
				return false;
			if(comps.get(findComp(in)).dataindex<testdata.size())
				return true;
			return false;
		}
		
		String getNextData(String in)
		{
			if(findComp(in)==-1)
				return null;
			if(comps.get(findComp(in)).dataindex<testdata.size())
			{
				comps.get(findComp(in)).dataindex++;
				return testdata.get(comps.get(findComp(in)).dataindex-1);
			}
			return null;
		}
		
		void resettestdataindex(String in)
		{
			if(findComp(in)!=-1)
				comps.get(findComp(in)).dataindex = 0;
		}
		
		void resetall()
		{
			for(int i=0; i<comps.size(); i++)
				comps.get(i).dataindex = 0;
		}
		
		int findComp(String in)
		{
			for(int i=0; i<comps.size(); i++)
				if(comps.get(i).name.equals(in))
					return i;
			return -1;
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