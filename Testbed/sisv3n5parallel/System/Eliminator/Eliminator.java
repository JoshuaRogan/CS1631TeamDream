import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Eliminator
{
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;
	
	private static String configfile = "../Config/Config.txt";
	private static ArrayList<Cycle> cycles = new ArrayList<Cycle>();
	private static double elrate;
	private static String elcriteria;

	public static void main(String[] args) throws Exception
	{
		elrate = 0.4;
		elcriteria = "average";
		File tempfile = new File("../../Data/Result/EliminatorLog.txt");
		if(tempfile.exists())
			tempfile.delete();
		LoadConfig(true);
		LoadConfig(false);
		
		universal = new Socket("127.0.0.1", 7999);

		mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name","Eliminator");
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
		String tempcyclestr, tempcompstr, tempinstancestr;
		Cycle tempcycle;
		Comp tempcomp;
		Instance tempinstance;
		Performance temppf;
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
		case 621:
			tempcyclestr = kvList.getValue("Cycle"); 
			tempcompstr = kvList.getValue("Algorithm");
			tempcycle = cycles.get(findCycle(tempcyclestr));
			tempcomp = tempcycle.comps.get(tempcycle.findComp(tempcompstr));
			tempcomp.instances.add(new Instance(kvList.getValue("CompName"), kvList.getValue("Parameters"), tempcompstr, tempcyclestr));
			break;
		case 622:
			tempcyclestr = kvList.getValue("Cycle"); 
			tempcompstr = kvList.getValue("Algorithm");
			tempcycle = cycles.get(findCycle(tempcyclestr));
			tempcomp = tempcycle.comps.get(tempcycle.findComp(tempcompstr));
			tempcomp.finalround = Boolean.parseBoolean(kvList.getValue("FinalRound"));
			result = new KeyValueList();
			result.addPair("MsgID","631");
			result.addPair("Description", "From Eliminator, notify DataSender to send next round of data.");
			result.addPair("Name","Eliminator");
			result.addPair("Cycle", tempcyclestr);
			result.addPair("Algorithm", tempcompstr);
			mEncoder.sendMsg(result, universal.getOutputStream());
			break;
		case 603:
			tempcyclestr = kvList.getValue("Cycle"); 
			tempcompstr = kvList.getValue("Algorithm");
			tempinstancestr = kvList.getValue("CompName");
			tempcycle = cycles.get(findCycle(tempcyclestr));
			tempcomp = tempcycle.comps.get(tempcycle.findComp(tempcompstr));
			tempinstance = tempcomp.instances.get(tempcomp.findInstance(tempinstancestr));
			temppf = new Performance();
			temppf.data = kvList.getValue("Data");
			temppf.result = kvList.getValue("Result");
			temppf.resultevaluation = Double.parseDouble(kvList.getValue("Evaluation"));
			temppf.runtime = Double.parseDouble(kvList.getValue("Runtime"));
			temppf.performance = calculateInstanceGlobalPerformance(temppf.resultevaluation, temppf.runtime, tempcyclestr, tempcompstr);
			tempinstance.addResult(temppf);
			tempinstance.receivedresponse = true;
			tempcomp.checkAllReceivedResponse();
			break;
		case 604:
			tempcyclestr = kvList.getValue("Cycle"); 
			tempcompstr = kvList.getValue("Algorithm");
			tempcycle = cycles.get(findCycle(tempcyclestr));
			tempcomp = tempcycle.comps.get(tempcycle.findComp(tempcompstr));
			int livenum;
			livenum = (int)Math.round((float) tempcomp.instances.size()*elrate);
			if(livenum<=1)
				livenum = 1;
			if(tempcomp.finalround)
				tempcomp.eliminateInstances(1);
			else
				tempcomp.eliminateInstances(livenum);
			break;
		case 623:
			tempcyclestr = kvList.getValue("Cycle"); 
			tempcycle = cycles.get(findCycle(tempcyclestr));
			if(tempcycle.comps.size()>0)
				tempcycle.selectBestAlgorithm();
			result = new KeyValueList();
			result.addPair("MsgID","633");
			result.addPair("Description", "From Eliminator, notify TimeController cycle concentration complete.");
			result.addPair("Name","Eliminator");
			result.addPair("Cycle", tempcyclestr);
			mEncoder.sendMsg(result, universal.getOutputStream());
			if(tempcycle.comps.size()>0)
				break;
			PrintWriter pw = new PrintWriter(new FileWriter(new File("../../Data/Result/EliminatorLog.txt"),true));
			pw.println("Cycle "+tempcyclestr+" running completed.");
			pw.flush();
			pw.close();
			break;
		case 22:
			System.exit(0);
			break;
		case 24:
			System.out.println("Eliminator Activated");
			break;
		case 25:
			System.out.println("Eliminator Deactivated");
			break;
		default:
			break;
		}
	}
	
	/*************************************
	 * The following function 
	 * 	calculateInstanceGlobalPerformance(double presultevaluation, double pruntime, String pcycle, String palgorithm)
	 * gives evaluation of an algorithm instance's global performance. This means Eliminator will use this global performance value
	 * to eliminate algorithm instances. Algorithm instances with smaller global performance will be eliminated.
	 * So global performance value should consider both result evaluation and runtime of an algorithm instance.
	 * Eliminator also uses global performance value to select best algorithm within an SIS cycle. So all global performance 
	 * values are scaled between algorithms.(i.e. Algorithm instance A with global performance value 100 is better than Algorithm instance B
	 * with global performance value 50.)
	 * Implement the function to give proper evaluation of algorithm instance's global performance.
	 * NOTE: The performance value should follow the rule: the bigger the value, the better the performance is.
	 *************************************/
	
	static double calculateInstanceGlobalPerformance(double presultevaluation, double pruntime, String pcycle, String palgorithm)
	{
		/***************************************
		 * Modify the code to give proper evaluation of algorithm instance's global performance.
		 * 	presultevaluation is Verifier's evaluation of algorithm instance's output on some test data
		 * 	pruntime is algorithm instance's running time on some test data
		 * 	pcycle is algorithm instance's cycle type.
		 * 	palgorithm is algorithm instance's algorithm type.
		 ***************************************/
		return presultevaluation;
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
					tempcycle.comps.add(new Comp(temp.substring(0, temp.indexOf('\t')).trim(), tempcycle.name));
			}
		}
		if(blockname.equalsIgnoreCase("SelectionRate"))
		{
			Scanner sc = new Scanner(blockcontent);
			while(sc.hasNextLine())
			{
				temp = sc.nextLine().trim();
				if(temp.length() == 0)
					continue;
				elrate = Double.parseDouble(temp);
				break;
			}
		}
		if(blockname.equalsIgnoreCase("SelectionCriteria"))
		{
			Scanner sc = new Scanner(blockcontent);
			while(sc.hasNextLine())
			{
				temp = sc.nextLine().trim();
				if(temp.length() == 0)
					continue;
				elcriteria = temp;
				break;
			}
		}
	}
	
	static class Instance
	{
		String name;
		String pars;
		String algorithm;
		String cycle;
		ArrayList<Performance> results = new ArrayList<Performance>();
		boolean receivedresponse;
		double globalperformance;
		
		Instance(String in1, String in2, String in3, String in4)
		{
			name = in1;
			pars = in2;
			algorithm = in3;
			cycle = in4;
			receivedresponse = false;
			globalperformance = 0;
		}
		
		void addResult(Performance in)
		{
			for(int i=0; i<results.size(); i++)
				if(results.get(i).data.equals(in.data))
				{
					results.remove(i);
					break;
				}
			results.add(in);
		}
		
		void SetGlobalPerformance()
		{
			double result;
			if(elcriteria.equalsIgnoreCase("Average"))
			{
				result = 0;
				for(int i=0; i<results.size(); i++)
					result = result + results.get(i).performance;
				globalperformance = result/results.size();
			}
			else if(elcriteria.equalsIgnoreCase("Max"))
			{
				result = Double.NEGATIVE_INFINITY;
				for(int i=0; i<results.size(); i++)
					if(result<results.get(i).performance)
						result = results.get(i).performance;
				globalperformance = result;
			}
			else if(elcriteria.equalsIgnoreCase("Min"))
			{
				result = Double.POSITIVE_INFINITY;
				for(int i=0; i<results.size(); i++)
					if(result>results.get(i).performance)
						result = results.get(i).performance;
				globalperformance = result;
			}
			else
			{//Average is the default.
				result = 0;
				for(int i=0; i<results.size(); i++)
					result = result + results.get(i).performance;
				globalperformance = result/results.size();
			}
		}
	}
	
	static class Comp
	{
		String name;
		String cycle;
		ArrayList<Instance> instances = new ArrayList<Instance>();
		boolean finalround;
		
		Comp(String in1, String in2)
		{
			name = in1;
			cycle = in2;
			finalround = false;
		}
		
		int findInstance(String in)
		{
			for(int i=0; i<instances.size(); i++)
				if(instances.get(i).name.equals(in))
					return i;
			return -1;
		}
		
		void checkAllReceivedResponse() throws Exception
		{
			boolean allreceived = true;
			for(int i=0; i<instances.size(); i++)
				if(!instances.get(i).receivedresponse)
				{
					allreceived = false;
					break;
				}
			if(allreceived)
			{
				KeyValueList result = new KeyValueList();
				result.addPair("MsgID","631");
				result.addPair("Description", "From Eliminator, notify DataSender to send next round of data.");
				result.addPair("Name","Eliminator");
				result.addPair("Cycle", cycle);
				result.addPair("Algorithm", name);
				mEncoder.sendMsg(result, universal.getOutputStream());
				for(int i=0; i<instances.size(); i++)
					instances.get(i).receivedresponse = false;
			}
		}
		
		void eliminateInstances(int livenum) throws Exception
		{
			for(int i=0; i<instances.size(); i++)
				instances.get(i).SetGlobalPerformance();
			ArrayList<Instance> results = new ArrayList<Instance>();
			
			if(livenum>instances.size())
				livenum = instances.size();
			Instance worstinstance;
			int deadnum = instances.size()-livenum;
			for(int i=0;i<deadnum;i++)
			{
				worstinstance = SelectWorstInstance();
				results.add(worstinstance);
				instances.remove(worstinstance);
			}
			KeyValueList kvList;
			kvList = new KeyValueList();
			kvList.addPair("MsgID", "632");
			kvList.addPair("Description", "From Eliminator, notify Enumerator elimination result.");
			kvList.addPair("Name", "Eliminator");
			kvList.addPair("Cycle", cycle);
			kvList.addPair("Algorithm", name);
			for(int i=1;i<=instances.size(); i++)
			{
				kvList.addPair("LiveComp "+i, instances.get(i-1).name);
			}
			for(int i=1;i<=results.size(); i++)
			{
				kvList.addPair("DeadComp "+i, results.get(i-1).name);
			}
			kvList.addPair("FinalRound", String.valueOf(finalround));
			mEncoder.sendMsg(kvList, universal.getOutputStream());
			if(finalround)
			{
				Instance tempinstance = instances.get(0);
				PrintWriter pw = new PrintWriter(new FileWriter(new File("../../Data/Result/EliminatorLog.txt"),true));
				pw.println("Cycle "+cycle+" Algorithm "+name+" running completed. Best algorithm instance is:");
				pw.println("Instance name: "+tempinstance.name);
				pw.println("Parameters: "+tempinstance.pars);
				for(int i=0; i<tempinstance.results.size(); i++)
				{
					pw.println("Input Test Data: "+tempinstance.results.get(i).data);
					pw.println("Output Result: "+tempinstance.results.get(i).result);
					pw.println("Result Evaluation: "+tempinstance.results.get(i).resultevaluation);
					pw.println("Runtime: "+tempinstance.results.get(i).runtime);
				}
				pw.flush();
				pw.close();
			}
		}
		
		Instance SelectWorstInstance()
		{
			Instance worstinstance = null;
			for(int i=0;i<instances.size();i++)
			{
				if(worstinstance == null)
				{
					worstinstance = instances.get(i);
					continue;
				}
				if(worstinstance.globalperformance>instances.get(i).globalperformance)
					worstinstance = instances.get(i);
			}
			return worstinstance;
		}
	}
	
	static class Performance
	{
		String data;
		String result;
		double resultevaluation;
		double runtime;
		double performance;
	}
	
	static class Cycle
	{
		String name;
		ArrayList<Comp> comps = new ArrayList<Comp>();
		
		Cycle(String in)
		{
			name = in;
		}
		
		int findComp(String in)
		{
			for(int i=0; i<comps.size(); i++)
				if(comps.get(i).name.equals(in))
					return i;
			return -1;
		}
		
		void resetall()
		{
			for(int i=0; i<comps.size(); i++)
			{
				comps.get(i).instances.clear();
				comps.get(i).finalround = false;
			}
		}
		
		void selectBestAlgorithm() throws Exception
		{
			Instance bestinstance = null;
			for(int i=0;i<comps.size();i++)
			{
				if(bestinstance == null)
				{
					bestinstance = comps.get(i).instances.get(0);
					continue;
				}
				if(bestinstance.globalperformance<comps.get(i).instances.get(0).globalperformance)
					bestinstance = comps.get(i).instances.get(0);
			}
			Instance tempinstance = bestinstance;
			PrintWriter pw = new PrintWriter(new FileWriter(new File("../../Data/Result/EliminatorLog.txt"),true));
			pw.println("Cycle "+name+" running completed. Best algorithm is:");
			pw.println("Algorithm type: "+tempinstance.algorithm);
			pw.println("Instance name: "+tempinstance.name);
			pw.println("Parameters: "+tempinstance.pars);
			for(int i=0; i<tempinstance.results.size(); i++)
			{
				pw.println("Input Test Data: "+tempinstance.results.get(i).data);
				pw.println("Output Result: "+tempinstance.results.get(i).result);
				pw.println("Result Evaluation: "+tempinstance.results.get(i).resultevaluation);
				pw.println("Runtime: "+tempinstance.results.get(i).runtime);
			}
			pw.flush();
			pw.close();
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