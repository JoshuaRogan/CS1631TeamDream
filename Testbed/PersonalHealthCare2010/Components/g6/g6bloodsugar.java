import java.net.*;
import java.io.*;

public class g6bloodsugar implements ComponentBase, Runnable
{
	private Socket universal;
	private MsgEncoder mEncoder;
	private MsgDecoder mDecoder;
	private boolean active;
	private Thresholds itsThresholds;
	private int BloodSugar;
	
	g6bloodsugar(String IP)
	{
		try
		{
			universal = new Socket(IP, 7999);
			mEncoder = new MsgEncoder();
			mDecoder = new MsgDecoder(universal.getInputStream());
			active = false;
			itsThresholds = Thresholds.getDefaultThresholds();
		}
		catch (Exception e)
		{
			System.out.println("Exception g6bloodsugar(String IP)");
		}
	}
	
	public void run()
	{
		KeyValueList kvInput;
		try
		{
			while (true)
			{
				kvInput = mDecoder.getMsg();
				processMsg(kvInput);
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception run()");
		}
	}
	
	synchronized public void processMsg(KeyValueList kvList)
	{
		int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
		
		if (MsgID==24)
		{
			active = true;
			System.out.println("processMsg 24");
			
			return;
		}
		
		if (!active)
		{
			System.out.println("!active");
			
			return;
		}
		
		switch (MsgID)
		{
			case 25:
				active = false;
				System.out.println("processMsg 25");
				break;
			case 160:
				itsThresholds = new Thresholds(kvList);
				break;
			case 41:
				BloodSugar = Integer.parseInt(kvList.getValue("Blood Sugar"));
				Alert anAlert = Alert.getFrom(itsThresholds, BloodSugar);
				if (anAlert!=null)
				{
					sendAlert(anAlert);
				}
				break;
			case 45:
				Alert.UserName = kvList.getValue("UserName");
				break;
		}
	}
	
	public void sendAlert(Alert theAlert)
	{
		try
		{
			mEncoder.sendMsg(theAlert.toKeyValueList(), universal.getOutputStream());
		}
		catch (Exception e)
		{
			System.out.println("Exception sendAlert(Alert theAlert)");
		}
	}
	
	public void sendMessage(String xmlpath)
	{
		try
		{
			KeyValueListWrapper aKeyValueListWrapper = new KeyValueListWrapper(xmlpath);
			mEncoder.sendMsg(aKeyValueListWrapper, universal.getOutputStream());
		}
		catch (Exception e)
		{
			System.out.println("Exception sendMessage(String xmlpath)");
		}
	}
	
	public boolean waitForAck() throws Exception
	{
		KeyValueList aKeyValueList = mDecoder.getMsg();
		String MsgID = aKeyValueList.getValue("MsgID");
		if (MsgID.equals("26"))
		{
			active = true;
		
			return true;
		}
		else
		{
			return false;
		}
	}

	public static void main(String []args) throws Exception
	{
		/*
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input = br.readLine();
		}
		catch (Exception e) {}
		*/
		
		g6bloodsugar ag6bloodsugar = new g6bloodsugar(args[0]);
		ag6bloodsugar.sendMessage("g6bloodsugarconnect.XML");
		
		if (!ag6bloodsugar.waitForAck())
		{
			throw new Exception("waitForAck");
		}
		
		Thread g6bloodsugarThread = new Thread(ag6bloodsugar);
		g6bloodsugarThread.setDaemon(false);
		g6bloodsugarThread.start();
	}
}
