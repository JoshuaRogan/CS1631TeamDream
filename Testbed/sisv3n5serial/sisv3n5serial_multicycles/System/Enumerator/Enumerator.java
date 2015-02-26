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


public class Enumerator
{
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;
	private static MsgDecoder mDecoder;
	
	private static String configfile = "../Config/Config.txt";
	private static ArrayList<Cycle> cycles = new ArrayList<Cycle>();
	private static int concurrentcomps;
	private static String compdir = "../../Components/";
	private static boolean compconnected, connectstatusrequest;
	private static ArrayList<KeyValueList> msgtobeprocessed = new ArrayList<KeyValueList>();
	

	public static void main(String[] args) throws Exception
	{
		concurrentcomps = 3;
		
		LoadConfig(true);
		LoadConfig(false);
		
		universal = new Socket("127.0.0.1", 7999);

		mEncoder = new MsgEncoder();
		mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name","Enumerator");
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
		Comp tempcomp;
		KeyValueList result;
		ArrayList<String> templist;
		
		switch(MsgID)
		{
		case 26:
			System.out.println("Connect to SISServer successful.");
			break;
		case 613:
			for(int i=0; i<cycles.size(); i++)
				cycles.get(i).resetall();
			break;
		case 611:
			templist = kvList.getValueLike("Cycle");
			if(templist == null)
				return;
			for(int i=0; i<templist.size(); i++)
			{
				if(findCycle(templist.get(i))!=-1)
					cycles.get(findCycle(templist.get(i))).start();
			}
			break;
		case 632:
			tempcyclestr = kvList.getValue("Cycle"); 
			tempcompstr = kvList.getValue("Algorithm");
			tempcycle = cycles.get(findCycle(tempcyclestr));
			tempcomp = tempcycle.comps.get(tempcycle.findComp(tempcompstr));
			templist = kvList.getValueLike("LiveComp");
			if(templist == null)
				tempcomp.comps2enumerate = concurrentcomps;
			else
				tempcomp.comps2enumerate = concurrentcomps - templist.size();
			if(Boolean.parseBoolean(kvList.getValue("FinalRound")))
			{
				tempcomp.concentrated = true;
				if(tempcycle.parallel)
				{
					if(tempcycle.checkConcentrated())
					{
						send623(tempcyclestr);
						tempcycle.resetall();
					}
				}
				else
				{
					if(tempcycle.sequentialindex > tempcycle.comps.size())
					{
						send623(tempcyclestr);
						tempcycle.resetall();
					}
					else
					{
						tempcycle.comps.get(tempcycle.sequentialindex-1).start();
						tempcycle.sequentialindex++;
					}
				}
			}
			else
				tempcomp.createcomps();
			break;
		case 22:
			System.exit(0);
			break;
		case 24:
			System.out.println("Enumerator Activated");
			break;
		case 25:
			System.out.println("Enumerator Deactivated");
			break;
		case 28:
			if(kvList.getValue("Connection").equalsIgnoreCase("yes"))
				compconnected = true;
			else
				compconnected = false;
			connectstatusrequest = false;
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
		String temp, temp1, temp2, temp3;
		if(blockname.equalsIgnoreCase("CycleList"))
		{
			cycles.clear();
			Scanner sc = new Scanner(blockcontent);
			while(sc.hasNextLine())
			{
				temp = sc.nextLine().trim();
				if(temp.length() == 0)
					continue;
				temp1 = temp.substring(temp.indexOf('\t')).trim();
				temp1 = temp1.substring(0, temp1.indexOf('\t')).trim();
				cycles.add(new Cycle(temp.substring(0, temp.indexOf('\t')).trim(), temp1.equalsIgnoreCase("parallel")));
			}
		}
		Cycle tempcycle;
		StringTokenizer st;
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
				st = new StringTokenizer(temp, "\t");
				temp1 = st.nextToken();
				temp2 = st.nextToken();
				if(st.hasMoreTokens())
					temp3 = st.nextToken();
				else
					temp3 = "null";
				tempcycle.addComp(temp1, temp2, temp3);
			}
		}
		if(blockname.equalsIgnoreCase("ConcurrentComps"))
		{
			Scanner sc = new Scanner(blockcontent);
			while(sc.hasNextLine())
			{
				temp = sc.nextLine().trim();
				if(temp.length() == 0)
					continue;
				concurrentcomps = Integer.parseInt(temp);
				break;
			}
		}
	}
	
	static class Comp
	{
		String name;
		String path;
		String cycle;
		ArrayList<String> pars = new ArrayList<String>();
		//reset items
		int enumeratecount = 1;
		int comps2enumerate = 0;
		boolean concentrated = false;
		
		Comp(String in1, String in2, String in3, String in4)
		{
			name = in1;
			path = in2;
			pars.add(in3);
			cycle = in4;
		}
		
		void start() throws Exception
		{
			comps2enumerate = concurrentcomps;
			enumeratecount = 1;
			createcomps();
		}
		
		void createcomps() throws Exception
		{
			boolean finalround = false;
			if(comps2enumerate <= 0)
				comps2enumerate = 1;
			if(comps2enumerate + enumeratecount > pars.size())
			{
				finalround = true;
				comps2enumerate = pars.size()-enumeratecount+1;
			}
			
			String algpath, algfile;
			String parlist;
			KeyValueList kvList;
			for(int i=0; i<comps2enumerate; i++)
			{
				if(path.indexOf('/')!=-1)
				{
					algpath = path.substring(0, path.lastIndexOf('/')).trim();
					algfile = path.substring(path.lastIndexOf('/')+1).trim();
				}
				else
				{
					algpath = "";
					algfile = path;
				}
				parlist = pars.get(enumeratecount-1);
				if(parlist.equalsIgnoreCase("null"))
					parlist = "";
				kvList = new KeyValueList();
				kvList.addPair("MsgID", "20");
				kvList.addPair("Description", "Create cycle "+cycle+" algorithm "+name+" instance");
				kvList.addPair("Name", cycle+"_"+name+"_"+enumeratecount);
				kvList.addPair("InputMsgID 1", "601");
				kvList.addPair("OutputMsgID 1", "602");
				kvList.addPair("OutputMsgID 2", "614");
				mEncoder.sendMsg(kvList, universal.getOutputStream());
				Thread.currentThread().sleep(1000);
				Runtime.getRuntime().exec("cmd.exe /k start \""+cycle+"_"+name+"_"+enumeratecount+"\" java -cp "+compdir+algpath+" "+algfile+" "+enumeratecount+" "+parlist);
				Thread.currentThread().sleep(1000);
				kvList = new KeyValueList();
				kvList.addPair("MsgID", "621");
				kvList.addPair("Description", "From Enumerator, notify Eliminator created algorithm instance.");
				kvList.addPair("Name", "Enumerator");
				kvList.addPair("Cycle", cycle);
				kvList.addPair("Algorithm", name);
				kvList.addPair("CompName", cycle+"_"+name+"_"+enumeratecount);
				if(parlist.length()==0)
					parlist = "null";
				kvList.addPair("Parameters", parlist);
				mEncoder.sendMsg(kvList, universal.getOutputStream());
				Thread.currentThread().sleep(1000);
				compconnected = false;
				connectstatusrequest = false;
				while(!compconnected)
				{
					if(!connectstatusrequest)
					{
						kvList = new KeyValueList();
						kvList.addPair("MsgID", "27");
						kvList.addPair("Description", "Check if the created algorithm instance is connected");
						kvList.addPair("Name", "Enumerator");
						kvList.addPair("CompName", cycle+"_"+name+"_"+enumeratecount);
						mEncoder.sendMsg(kvList, universal.getOutputStream());
						connectstatusrequest = true;
						Thread.currentThread().sleep(100);
					}
					else
						Thread.currentThread().sleep(100);
					
					kvList = mDecoder.getMsg();
					if(kvList.getValue("MsgID").equals("28"))
						ProcessMsg(kvList);
					else
						msgtobeprocessed.add(kvList);
					
				}
				while(msgtobeprocessed.size()!=0)
					ProcessMsg(msgtobeprocessed.remove(0));
				enumeratecount++;
			}
			kvList = new KeyValueList();
			kvList.addPair("MsgID", "622");
			kvList.addPair("Description", "From Enumerator, notify Eliminator algorithm creation complete.");
			kvList.addPair("Name", "Enumerator");
			kvList.addPair("Cycle", cycle);
			kvList.addPair("Algorithm", name);
			kvList.addPair("FinalRound", String.valueOf(finalround));
			mEncoder.sendMsg(kvList, universal.getOutputStream());
			Thread.currentThread().sleep(1000);
			comps2enumerate = 0;

		}
	}
	
	static class Cycle
	{
		String name;
		boolean parallel;
		ArrayList<Comp> comps = new ArrayList<Comp>();
		//reset item
		int sequentialindex = 1;
		
		Cycle(String in1, boolean in2)
		{
			name = in1;
			parallel = in2;
		}
		
		void resetall()
		{
			sequentialindex = 1;
			for(int i=0; i<comps.size(); i++)
			{
				comps.get(i).comps2enumerate = 0;
				comps.get(i).enumeratecount = 1;
				comps.get(i).concentrated = false;
			}
		}
		
		void addComp(String in1, String in2, String in3)
		{
			if(findComp(in1) == -1)
			{
				comps.add(new Comp(in1, in2, in3, name));
			}
			else
			{
				if(comps.get(findComp(in1)).path.equals(in2))
					comps.get(findComp(in1)).pars.add(in3);
				else
				{
					System.out.println("Error in config file, failed to start");
					System.exit(0);
				}
			}
		}
		
		int findComp(String in)
		{
			for(int i=0; i<comps.size(); i++)
				if(comps.get(i).name.equals(in))
					return i;
			return -1;
		}
		
		void start() throws Exception
		{
			if(comps.size()==0)
			{
				send623(name);
				return;
			}
			if(parallel)
			{
				for(int i=0; i<comps.size(); i++)
					comps.get(i).start();
			}
			else
			{
				comps.get(0).start();
				sequentialindex++;
			}
		}
		
		boolean checkConcentrated()
		{
			for(int i=0; i<comps.size(); i++)
				if(!comps.get(i).concentrated)
					return false;
			return true;
		}
	}
	
	static int findCycle(String in)
	{
		for(int i=0; i<cycles.size(); i++)
			if(cycles.get(i).name.equals(in))
				return i;
		return -1;
	}
	
	static void send623(String in) throws Exception
	{
		KeyValueList result = new KeyValueList();
		result.addPair("MsgID","623");
		result.addPair("Description", "From Enumerator, notify Eliminator to do cycle concentration.");
		result.addPair("Name","Enumerator");
		result.addPair("Cycle", in);
		mEncoder.sendMsg(result, universal.getOutputStream());
	}
}
