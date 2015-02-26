import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

class Eliminator
{	
	public static void main(String []args) throws Exception
	{	
		if (args.length < 1)
        {
            System.out.println("Too few arguments.");
            System.out.println("Run it like this: java Eliminator SISServerIP");
            System.exit(0);
        }

		Socket universal = new Socket(args[0], 7999);

		MsgEncoder mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Name","Eliminator");
		mEncoder.sendMsg(msg23, universal.getOutputStream());
		
		ArrayList<History> histories = new ArrayList<History>();
		KeyValueList kvInput;
		KeyValueList kvOutput;
		String compname;
		History history = null;
		String selectresult;
		ArrayList<String> selectresults;
		OutputStream outstream = universal.getOutputStream();
		
		while(true)
		{	
			kvInput = mDecoder.getMsg();
			int MsgID = Integer.parseInt(kvInput.getValue("MsgID"));
			compname = kvInput.getValue("CompName");
			String type = kvInput.getValue("Type");
			boolean flag;
			switch(MsgID)
			{
			case 300:
				flag = false;
				for(int i=0; i<histories.size(); i++)
					if(histories.get(i).tag.equals(type))
					{
						flag = true;
						history = histories.get(i);
						break;
					}
				if(!flag)
				{
					histories.add(new History(type));
					history= histories.get(histories.size()-1);
				}
				flag = false;
				if(kvInput.getValue("Correctness").equalsIgnoreCase("yes"))
					history.Add(compname, true);
				else
					history.Add(compname, false);
				break;
			case 301:
				flag = false;
				for(int i=0; i<histories.size(); i++)
					if(histories.get(i).tag.equals(type))
					{
						flag = true;
						history = histories.get(i);
						break;
					}
				if(!flag)
				{
					histories.add(new History(type));
					history= histories.get(histories.size()-1);
				}
				flag = false;
				history.Activate(kvInput.getValue("CompName"));
				break;
			case 302:
				flag = false;
				for(int i=0; i<histories.size(); i++)
					if(histories.get(i).tag.equals(type))
					{
						flag = true;
						history = histories.get(i);
						break;
					}
				if(!flag)
				{
					histories.add(new History(type));
					history= histories.get(histories.size()-1);
				}
				flag = false;
				history.Deactivate(kvInput.getValue("CompName"));
				break;
			case 310:
				if(type!=null)
				{
					flag = false;
					for(int i=0; i<histories.size(); i++)
						if(histories.get(i).tag.equals(type))
						{
							flag = true;
							history = histories.get(i);
							break;
						}
					if(flag)
					{
						selectresults = history.Select();
						kvOutput = new KeyValueList();
						kvOutput.addPair("MsgID", "303");
						kvOutput.addPair("Description", "Component Selection Results");
						int count = 1;
						for(int i=0; i<selectresults.size(); i++)
						{
							kvOutput.addPair("ActiveComp "+String.valueOf(count), selectresults.get(i));
							count++;
							history.Activate(selectresults.get(i));
						}
						count = 1;
						for(int i=0; i<history.history.size(); i++)
						{
							if(history.history.get(i).active && !selectresults.contains(history.history.get(i).CompName))
							{
								kvOutput.addPair("DeactiveComp "+String.valueOf(count), history.history.get(i).CompName);
								count++;
								history.Deactivate(history.history.get(i).CompName);
							}
						}
						mEncoder.sendMsg(kvOutput, outstream);
					}
					flag = false;
				}
				break;
			case 311:
				for(int i=0; i<histories.size(); i++)
				{
					history = histories.get(i);
					selectresults = history.Select();
					kvOutput = new KeyValueList();
					kvOutput.addPair("MsgID", "303");
					kvOutput.addPair("Description", "Component Selection Results");
					int count = 1;
					for(int j=0; j<selectresults.size(); j++)
					{
						kvOutput.addPair("ActiveComp "+String.valueOf(count), selectresults.get(j));
						count++;
						history.Activate(selectresults.get(j));
					}
					count = 1;
					for(int j=0; j<history.history.size(); j++)
					{
						if(history.history.get(j).active && !selectresults.contains(history.history.get(j).CompName))
						{
							kvOutput.addPair("DeactiveComp "+String.valueOf(count), history.history.get(j).CompName);
							count++;
							history.Deactivate(history.history.get(j).CompName);
						}
					}
					mEncoder.sendMsg(kvOutput, outstream);
				}	
				break;
			case 312:
				if(type==null)
					break;
				flag = false;
				for(int i=0; i<histories.size(); i++)
					if(histories.get(i).tag.equals(type))
					{
						flag = true;
						history = histories.get(i);
						break;
					}
				if(flag)
					history.Clear();
				flag = false;
				break;
			case 313:
				for(int i=0; i<histories.size(); i++)
					histories.get(i).Clear();
				break;
			default:
				break;
			}
			if(MsgID!=300)
				continue;
			/*if(history.ReachCycle())//select one
			{//XXX
				selectresult = history.SelectOne();
				kvOutput = new KeyValueList();
				kvOutput.addPair("MsgID", "303");
				kvOutput.addPair("Description", "Component Selection Results");
				kvOutput.addPair("ActiveComp 1", selectresult);
				history.Activate(selectresult);
				int count = 1;
				for(int i=0; i<history.history.size(); i++)
				{
					if(history.history.get(i).active && !history.history.get(i).CompName.equals(selectresult))
					{
						kvOutput.addPair("DeactiveComp "+String.valueOf(count), history.history.get(i).CompName);
						count++;
						history.Deactivate(history.history.get(i).CompName);
					}
				}
				mEncoder.sendMsg(kvOutput, outstream);
			}*/
			if(history.ReachCycle())//select multiple
			{
				selectresults = history.Select();
				kvOutput = new KeyValueList();
				kvOutput.addPair("MsgID", "303");
				kvOutput.addPair("Description", "Component Selection Results");
				int count = 1;
				for(int i=0; i<selectresults.size(); i++)
				{
					kvOutput.addPair("ActiveComp "+String.valueOf(count), selectresults.get(i));
					count++;
					history.Activate(selectresults.get(i));
				}
				count = 1;
				for(int i=0; i<history.history.size(); i++)
				{
					if(history.history.get(i).active && !selectresults.contains(history.history.get(i).CompName))
					{
						kvOutput.addPair("DeactiveComp "+String.valueOf(count), history.history.get(i).CompName);
						count++;
						history.Deactivate(history.history.get(i).CompName);
					}
				}
				mEncoder.sendMsg(kvOutput, outstream);
			}
		}
		
	}
	
	static class History
	{
		ArrayList<HistoryItem> history = new ArrayList<HistoryItem>();  
		final int TestPeriod = 15;
		final double Threashold = 0.7;
		String tag;
		int count;
		
		History(String in)
		{
			tag = in;
			count = 1;
		}
		
		void Add(String in1, boolean in2)
		{
			int index;
			if((index = CompExist(in1))!=-1)
			{
				if(!history.get(index).active)
					return;
				if(in2)
					history.get(index).correct++;
				else
					history.get(index).incorrect++;
			}
			else
			{
				history.add(new HistoryItem(in1));
				if(in2)
					history.get(history.size()-1).correct++;
				else
					history.get(history.size()-1).incorrect++;
			}
		}
		
		int CompExist(String in)
		{
			if(history.size()==0)
				return -1;
			for(int i=0; i<history.size(); i++)
				if(history.get(i).CompName.equals(in))
					return i;
			return -1;
		}
		
		class HistoryItem
		{
			String CompName;
			int correct;
			int incorrect;
			boolean active;
			
			HistoryItem(String in)
			{
				CompName = in;
				correct = 0;
				incorrect = 0;
				active = true;
			}
		}
		
		void Clear()
		{
			if(history.size()==0)
				return;
			for(int i=0; i<history.size(); i++)
			{
				history.get(i).correct = 0;
				history.get(i).incorrect = 0;
			}
		}
		
		boolean ReachCycle()
		{
			if(history.size()==0)
				return false;
			for(int i=0; i<history.size(); i++)
				if(history.get(i).active && history.get(i).correct+history.get(i).incorrect<TestPeriod*count)
					return false;
			count++;
			return true;
		}
		
		String SelectOne()
		{
			double maxcorrectrate = -1;
			String result = null;
			for(int i=0; i<history.size(); i++)
			{
				if(history.get(i).active && (1.0*history.get(i).correct/(history.get(i).correct+history.get(i).incorrect+1))>maxcorrectrate)
				{
					result = history.get(i).CompName;
					maxcorrectrate = 1.0*history.get(i).correct/(history.get(i).correct+history.get(i).incorrect+1);
				}
			}
			return result;
		}
		
		ArrayList<String> Select()
		{
			ArrayList<String> result = new ArrayList<String>();
			for(int i=0; i<history.size(); i++)
			{
				if(history.get(i).active && (history.get(i).correct+history.get(i).incorrect==0 || (1.0*history.get(i).correct/(history.get(i).correct+history.get(i).incorrect))>Threashold))
				{
					result.add(history.get(i).CompName);
				}
			}
			return result;
		}
		
		void Activate(String in)
		{
			if(history.size()==0)
				return;
			for(int i=0; i<history.size(); i++)
				if(history.get(i).CompName.equals(in))
				{
					history.get(i).active = true;
					return;
				}
		}
		
		void Deactivate(String in)
		{
			if(history.size()==0)
				return;
			for(int i=0; i<history.size(); i++)
				if(history.get(i).CompName.equals(in))
				{
					history.get(i).active = false;
					history.get(i).correct = 0;
					history.get(i).incorrect = 0;
					return;
				}
		}
	}
}