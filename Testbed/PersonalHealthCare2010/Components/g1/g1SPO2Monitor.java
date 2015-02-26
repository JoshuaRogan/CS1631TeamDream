/********************************************************
**Authors: John Harley, Ryan Brashear                  **
**Project: g1SPO2Monitor.java              			   **
**Modified: 4-7-10          						   **
**Class: CS1631               						   **
**Due: 4-13-10               						   **
********************************************************/
import java.io.*;
import java.net.*;
import java.util.*;

public class g1SPO2Monitor
{
Socket universal;


	g1SPO2Monitor(String servaddr)
	{
		try{
		universal = new Socket(servaddr, 7999);

		MsgEncoder mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList kvlResult = new KeyValueList();
		kvlResult.addPair("MsgID","23");
		kvlResult.addPair("Description", "Connect to Server");
		kvlResult.addPair("SecurityLevel", "3");
		kvlResult.addPair("Name", "g1SPO2Monitor");
        mEncoder.sendMsg(kvlResult, universal.getOutputStream());
		/* receiving thread*/
		Thread t = new Thread (new Runnable()
			{
				public void run()
				{
					KeyValueList kvInput;
					try
					{
						while(true)
						{	
							kvInput = mDecoder.getMsg();
							System.out.println("**************Recieved Msg**************");
							System.out.println(kvInput);
							processMsg(kvInput);
						}
					}
					catch(Exception ex)
					{ System.out.println("Exception while sending to universal interface..");}
				}
			});
			t.setDaemon(true);
			t.start();
		}
		catch(Exception ex)
		{
			System.out.println("Exception reading from standard input..");
		}
	try
	{
		MsgEncoder mEncoder = new MsgEncoder();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true)
			{
				String input = br.readLine();
				KeyValueList kvList = new KeyValueList();	
				StringTokenizer st = new StringTokenizer(input, "$$$");
				while (st.hasMoreTokens()) 
				{
					kvList.addPair(st.nextToken(), st.nextToken());
				}

				mEncoder.sendMsg(kvList, universal.getOutputStream());
			}
		}
		catch(Exception ex)
		{ 
			System.out.println("Exception reading from standard input..");
		}
	}
	public static void main(String args[])
	{
		g1SPO2Monitor app = new g1SPO2Monitor(args[0]);
	}
	public void processMsg(KeyValueList kvList) throws Exception
	{
    	int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
    	ArrayList<Socket> sendlist;
    	ArrayList<String> inmsgs, outmsgs;
    	CompMsgControl tempcmc;
    	String tempstr;
		
		switch(MsgID)
		{
		case 33:
			String result1 = kvList.getValue("SPO2");
			String result2 = kvList.getValue("DateTime");
			int aInt = Integer.parseInt(result1);
			if (aInt < 91)
			{
			    MsgEncoder mEncoder = new MsgEncoder();
			    KeyValueList kvResult = new KeyValueList();
			    kvResult.addPair("MsgID","34");
			    kvResult.addPair("Description", "SPO2 Alert");
			    kvResult.addPair("SPO2", result1);
			    kvResult.addPair("Alert Type", "SPO2 Alert");
			    kvResult.addPair("Name", "g1 SPO2 Alert");
			    kvResult.addPair("DateTime", result2);
			    try 
			    {
			    	mEncoder.sendMsg(kvResult, universal.getOutputStream());
			    } 
			    catch(Exception ex)
			    { System.out.println("Exception sending SPO2 Alert..");}
			}
		break;
		}
    }
}