import java.awt.Frame;
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

import javax.swing.JOptionPane;


public class TimeController
{
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;
	
	private static String configfile = "../Config/Config.txt";
	private static ArrayList<Cycle> cycles = new ArrayList<Cycle>();
	private static ArrayList<ActiveCycle> activecycles = new ArrayList<ActiveCycle>();
	private static ArrayList<TCRule> tcrules = new ArrayList<TCRule>();
	private static ArrayList<EnVar> envars = new ArrayList<EnVar>();
	private static double[][] tcmatrix;
	private static int cycleswitchmethod;
	private static int seqindex;

	public static void main(String[] args) throws Exception
	{
		tcmatrix = null;
		cycleswitchmethod = 0;
		seqindex = 1;
		LoadConfig();
		adjustCycleSwitchMethod();
		System.out.print("Cycle switch method changed to: ");
		if(cycleswitchmethod==0)
			System.out.println("Sequential");
		if(cycleswitchmethod==1)
			System.out.println("Mapping rules");
		if(cycleswitchmethod==2)
			System.out.println("Matrix");
		
		universal = new Socket("127.0.0.1", 7999);

		mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name","TimeController");
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
		String tempcyclestr;
		KeyValueList result;
		
		switch(MsgID)
		{
		case 26:
			System.out.println("Connect to SISServer successful.");
			if(cycleswitchmethod==0)
			{
				for(int i=0; i<cycles.size(); i++)
				{
					if(i==0)
						cycles.get(i).active = true;
					else
						cycles.get(i).active = false;
				}
				seqindex++;
			}
			Thread.currentThread().sleep(1500);
			startCycles();
			break;
		case 614:
			String tempkey;
			for(int i=0; i<kvList.size(); i++)
			{
				tempkey = kvList.keyAt(i);
				if(findEnVar(tempkey)!=-1)
					envars.get(findEnVar(tempkey)).value = kvList.getValue(tempkey);
			}
			break;
		case 633:
			tempcyclestr = kvList.getValue("Cycle");
			for(int i=0; i<activecycles.size(); i++)
				if(activecycles.get(i).name.equals(tempcyclestr))
				{
					activecycles.get(i).finished = true;
					break;
				}
			boolean allfinished = true;
			for(int i=0; i<activecycles.size(); i++)
				if(!activecycles.get(i).finished)
				{
					allfinished = false;
					break;
				}
			if(allfinished)
			{
				activecycles.clear();
				if(cycleswitchmethod == 0)
				{
					if(seqindex>cycles.size())
						send612();
					else
					{
						for(int i=0; i<cycles.size(); i++)
						{
							if(i==seqindex-1)
								cycles.get(i).active = true;
							else
								cycles.get(i).active = false;
						}
						seqindex++;
						send613();
						Thread.currentThread().sleep(3000);
						startCycles();
					}
				}
				if(cycleswitchmethod == 1)
				{
					if(applyTCRule())
					{
						send613();
						Thread.currentThread().sleep(3000);
						startCycles();
					}
					else
						send612();
				}
				if(cycleswitchmethod == 2)
				{
					double[] prevenvarcycle = new double[envars.size()+cycles.size()];
					double[] nextenvarcycle = new double[envars.size()+cycles.size()];
					for(int i=0; i<envars.size(); i++)
					{
						try{
							prevenvarcycle[i] = Double.parseDouble(envars.get(i).value);
						}catch(Exception e)
						{
							prevenvarcycle[i] = 0;
						}
					}
					for(int i=0; i<cycles.size(); i++)
						if(cycles.get(i).active)
							prevenvarcycle[envars.size()+i] = 1;
						else
							prevenvarcycle[envars.size()+i] = 0;
					double tdouble;
					int tint = tcmatrix.length;
					for(int i=0; i<tint; i++)
					{
						tdouble = 0;
						for(int j=0; j<tint; j++)
							tdouble = tdouble+tcmatrix[j][i]*prevenvarcycle[j];
						nextenvarcycle[i] = tdouble;
					}
					for(int i=0; i<tint; i++)
					{
						if(nextenvarcycle[i]<0)
							nextenvarcycle[i]=0;
						if(nextenvarcycle[i]>1)
							nextenvarcycle[i]=1;
					}
					for(int i=0; i<envars.size(); i++)
						envars.get(i).value = String.valueOf(nextenvarcycle[i]);
					for(int i=0; i<cycles.size(); i++)
						if(nextenvarcycle[envars.size()+i]>0.5)
							cycles.get(i).active = true;
						else
							cycles.get(i).active = false;
					send613();
					Thread.currentThread().sleep(3000);
					startCycles();
				}
			}
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
	
	static boolean applyTCRule()
	{
		TCRule temprule;
		String tcycle;
		boolean tcyclevalue, applyok, tcyclevalue2;
		for(int i=0; i<tcrules.size(); i++)
		{
			temprule = tcrules.get(i);
			applyok = true;
			for(int j=0; j<temprule.prevenvars.size(); j++)
				if(!envarmatch(envars.get(findEnVar(temprule.prevenvars.get(j).name)).value ,temprule.prevenvars.get(j).value))
				{
					applyok = false;
					break;
				}
			if(!applyok)
				continue;
			for(int j=0; j<temprule.prevcycles.size(); j++)
			{
				tcycle = temprule.prevcycles.get(j).name;
				tcyclevalue = temprule.prevcycles.get(j).value;
				tcyclevalue2 = cycles.get(findCycle(tcycle)).active;
				if((tcyclevalue2&&!tcyclevalue) ||(!tcyclevalue2&&tcyclevalue))
				{
					applyok = false;
					break;
				}
			}
			if(applyok)
			{
				for(int j=0; j<temprule.nextenvars.size(); j++)
					envars.get(findEnVar(temprule.nextenvars.get(j).name)).value = temprule.nextenvars.get(j).value;
				for(int j=0; j<temprule.nextcycles.size(); j++)
					cycles.get(findCycle(temprule.nextcycles.get(j).name)).active = temprule.nextcycles.get(j).value;
				return true;
			}
		}
		return false;
	}
	
	static boolean envarmatch(String in1, String in2)
	{
		try{
		if(in1.equals(in2))
			return true;
		if(in2.startsWith(">="))
			if(Double.parseDouble(in1)>=Double.parseDouble(in2.substring(2).trim()))
				return true;
		if(in2.startsWith("<="))
			if(Double.parseDouble(in1)<=Double.parseDouble(in2.substring(2).trim()))
				return true;
		if(in2.startsWith(">"))
			if(Double.parseDouble(in1)>Double.parseDouble(in2.substring(1).trim()))
				return true;
		if(in2.startsWith("<"))
			if(Double.parseDouble(in1)<Double.parseDouble(in2.substring(1).trim()))
				return true;
		if(in2.startsWith("!="))
			if(Double.parseDouble(in1)!=Double.parseDouble(in2.substring(2).trim()))
				return true;
		if(in2.startsWith("=="))
			if(Double.parseDouble(in1)==Double.parseDouble(in2.substring(2).trim()))
				return true;
		if(in2.startsWith("="))
			if(Double.parseDouble(in1)==Double.parseDouble(in2.substring(1).trim()))
				return true;
		if(Double.parseDouble(in1)==Double.parseDouble(in2))
				return true;
		}catch(Exception e)
		{
			return false;
		}
		return false;
	}
	
	static void startCycles() throws Exception
	{
		activecycles.clear();
		for(int i=0; i<cycles.size(); i++)
			if(cycles.get(i).active)
				activecycles.add(new ActiveCycle(cycles.get(i).name));
		if(activecycles.size()>0)
		{
			KeyValueList result = new KeyValueList();
			result.addPair("MsgID","611");
			result.addPair("Description", "From TimeController, notify Enumerator to start.");
			result.addPair("Name","TimeController");
			for(int i=1; i<=activecycles.size(); i++)
				result.addPair("Cycle"+i, activecycles.get(i-1).name);
			mEncoder.sendMsg(result, universal.getOutputStream());
		}
		else
			send612();
				
	}
	
	static void adjustCycleSwitchMethod()
	{
		boolean ruleok = false;
		if(tcrules.size()>0)
			ruleok = true;
		boolean matrixok = false;
		if(tcmatrix!=null && tcmatrix.length==envars.size()+cycles.size())
			matrixok = true;
		if(ruleok && matrixok)
			if(cycleswitchmethod==0)
				cycleswitchmethod=1;
		if(ruleok && !matrixok)
			cycleswitchmethod = 1;
		if(!ruleok && matrixok)
			cycleswitchmethod = 2;
		if(!ruleok && !matrixok)
			cycleswitchmethod = 0;
	}
	
	static void LoadConfig() throws Exception
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
						ProcessBlock(blockname, blockcontent);
						blockname = "";
						blockcontent = "";
						continue;
					}
					System.out.println("Error in config file, failed to start");
					System.exit(0);
				}
				inblock = false;
				ProcessBlock(blockname, blockcontent);
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
				temp1 = temp.substring(temp.lastIndexOf('\t')).trim();
				cycles.add(new Cycle(temp.substring(0, temp.indexOf('\t')).trim(), Boolean.parseBoolean(temp1)));
			}
		}
		if(blockname.equalsIgnoreCase("EnvironmentVars"))
		{
			Scanner sc = new Scanner(blockcontent);
			while(sc.hasNextLine())
			{
				temp = sc.nextLine().trim();
				if(temp.length() == 0)
					continue;
				if(temp.indexOf('\t')>=0)
					envars.add(new EnVar(temp.substring(0, temp.indexOf('\t')).trim(), temp.substring(temp.indexOf('\t')).trim()));
				else
					envars.add(new EnVar(temp, "0"));
			}
		}
		TCRule temprule;
		if(blockname.indexOf(' ')>=0 && blockname.substring(0, blockname.indexOf(' ')).trim().equalsIgnoreCase("CycleSwitchRule"))
		{
			temprule = new TCRule(blockname.substring(blockname.indexOf(' ')).trim());
			tcrules.add(temprule);
			Scanner sc = new Scanner(blockcontent);
			while(sc.hasNextLine())
			{
				temp = sc.nextLine().trim();
				if(temp.length() == 0)
					continue;
				temp1 = temp.substring(0, temp.indexOf('\t')).trim();
				temp2 = temp.substring(temp.indexOf('\t'), temp.lastIndexOf('\t')).trim();
				temp3 = temp.substring(temp.lastIndexOf('\t')).trim();
				if(temp3.equalsIgnoreCase("prevvar"))
					temprule.prevenvars.add(new TCEnVarItem(temp1, temp2));
				if(temp3.equalsIgnoreCase("nextvar"))
					temprule.nextenvars.add(new TCEnVarItem(temp1, temp2));
				if(temp3.equalsIgnoreCase("prevcycle"))
					temprule.prevcycles.add(new TCCycleItem(temp1, Boolean.valueOf(temp2)));
				if(temp3.equalsIgnoreCase("nextcycle"))
					temprule.nextcycles.add(new TCCycleItem(temp1, Boolean.valueOf(temp2)));
			}
		}
		if(blockname.equalsIgnoreCase("CycleSwitchMethod"))
		{
			Scanner sc = new Scanner(blockcontent);
			while(sc.hasNextLine())
			{
				temp = sc.nextLine().trim();
				if(temp.length() == 0)
					continue;
				if(temp.equalsIgnoreCase("rule"))
					cycleswitchmethod = 1;
				if(temp.equalsIgnoreCase("matrix"))
					cycleswitchmethod = 2;
				break;
			}
		}
		if(blockname.equalsIgnoreCase("CycleSwitchMatrix"))
		{
			Scanner sc = new Scanner(blockcontent);
			String matrixstr = "";
			while(sc.hasNextLine())
			{
				temp = sc.nextLine().trim();
				if(temp.length() == 0)
					continue;
				matrixstr = matrixstr+temp+"\n";
			}
			tcmatrix = str2matrix(matrixstr);
		}
	}
	
	static double[][] str2matrix(String in)
	{
		if(in.trim().length()==0)
			return null;
		try{
			Scanner sc = new Scanner(in);
			String ttempstr;
			ArrayList<Double> tarray1 = new ArrayList<Double>();
			ArrayList<Double[]> tarray2 = new ArrayList<Double[]>();
			Double[] darray;
			boolean flag = false;
			int j;
			while(sc.hasNextLine())
			{
				ttempstr = sc.nextLine().trim();
				if(ttempstr.length() == 0)
					continue;
				StringTokenizer st = new StringTokenizer(ttempstr);
				if(!flag)
				{
					while(st.hasMoreTokens())
						tarray1.add(Double.valueOf(st.nextToken()));
					darray = new Double[tarray1.size()];
					for(int i=0; i<tarray1.size(); i++)
						darray[i] = tarray1.get(i);
					tarray2.add(darray);
					flag = true;
				}
				else
				{
					j=0;
					darray = new Double[tarray1.size()];
					while(st.hasMoreTokens())
					{
						darray[j] = Double.valueOf(st.nextToken());
						j++;
					}
					tarray2.add(darray);
				}
			}
			if(tarray2.size()!=tarray1.size())
			{
				return null;
			}
			double[][] result = new double[tarray1.size()][tarray1.size()];
			for(int i=0; i<tarray1.size(); i++)
				for(int k=0; k<tarray1.size(); k++)
					result[i][k] = tarray2.get(i)[k];
			return result;
		}catch(Exception e)
		{
			return null;
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
		boolean active;
		
		Cycle(String in1, boolean in2)
		{
			name = in1;
			active = in2;
		}
	}
	
	static class EnVar
	{
		String name;
		String value;
		EnVar(String in1, String in2)
		{
			name = in1;
			value = in2;
		}
	}
	
	static int findEnVar(String in)
	{
		for(int i=0; i<envars.size(); i++)
			if(envars.get(i).name.equals(in))
				return i;
		return -1;
	}
	
	static int findCycle(String in)
	{
		for(int i=0; i<cycles.size(); i++)
			if(cycles.get(i).name.equals(in))
				return i;
		return -1;
	}
	
	static class TCRule
	{
		String name;
		ArrayList<TCEnVarItem> prevenvars = new ArrayList<TCEnVarItem>();
		ArrayList<TCEnVarItem> nextenvars = new ArrayList<TCEnVarItem>();
		ArrayList<TCCycleItem> prevcycles = new ArrayList<TCCycleItem>();
		ArrayList<TCCycleItem> nextcycles = new ArrayList<TCCycleItem>();
		
		TCRule(String in)
		{
			name = in;
		}
	}
	
	static class TCEnVarItem
	{
		String name;
		String value;
		
		TCEnVarItem(String in1, String in2)
		{
			name = in1;
			value = in2;
		}
	}
	
	static class TCCycleItem
	{
		String name;
		boolean value;
		
		TCCycleItem(String in1, boolean in2)
		{
			name = in1;
			value = in2;
		}
	}
	
	static class ActiveCycle
	{
		String name;
		boolean finished;
		
		ActiveCycle(String in)
		{
			name = in;
			finished = false;
		}
	}
	
	static void send612() throws Exception
	{
		KeyValueList result = new KeyValueList();
		result.addPair("MsgID","612");
		result.addPair("Description", "From TimeController, notify SISServer running finish.");
		result.addPair("Name","TimeController");
		mEncoder.sendMsg(result, universal.getOutputStream());
	}
	
	static void send613() throws Exception
	{
		KeyValueList result = new KeyValueList();
		result.addPair("MsgID","613");
		result.addPair("Description", "From TimeController, notify SIS system to reset.");
		result.addPair("Name","TimeController");
		mEncoder.sendMsg(result, universal.getOutputStream());
	}
}