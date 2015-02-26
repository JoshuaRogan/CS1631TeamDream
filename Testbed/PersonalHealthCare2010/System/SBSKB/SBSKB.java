import java.net.*;
import java.io.*;

public class SBSKB implements ComponentBase, Runnable
{
	private Socket universal;
	private MsgEncoder mEncoder;
	private MsgDecoder mDecoder;
	private boolean active;
	private String Passcode;
	private BloodSugarKnowledgeList bskList;
	private boolean locked;
	
	SBSKB(String IP)
	{
		try
		{
			universal = new Socket(IP, 7999);
			mEncoder = new MsgEncoder();
			mDecoder = new MsgDecoder(universal.getInputStream());
			active = false;
			Passcode = "1234";
			bskList = new BloodSugarKnowledgeList();
			locked = false;
		}
		catch (Exception e)
		{
			System.out.println("Exception SBSKB(String IP)");
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
		String InputPasscode;
		
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
			case 200:
				InputPasscode = kvList.getValue("Passcode");
				
				if (InputPasscode.equals(Passcode))
				{			
					bskList.addBloodSugarKnowledgeFrom(kvList);
				}
				break;
			case 201:
				String Inquirer = kvList.getValue("Inquirer");
				String Accessible = "Unknown";
				String Available = "Unknown";
				String Diagnosis = "Unknown";
				
				if (Inquirer.equals("SISServer"))
				{
					InputPasscode = kvList.getValue("Passcode");
					if (InputPasscode.equals(Passcode))
					{
						Accessible = "Yes";
					}
					else
					{
						Accessible = "No";
					}
				}
				else
				{
					if (locked)
					{
						Accessible = "No";
					}
					else
					{
						Accessible = "Yes";
					}
				}
				if (Accessible.equals("Yes"))
				{
					Diagnosis = bskList.getBloodSugarKnowledgeFrom(kvList);
					if (Diagnosis.equals("No"))
					{
						Available = "No";
					}
					else
					{
						Available = "Yes";
					}
				}
				sendOutput(Accessible, Available, kvList, Diagnosis);
				break;				
			case 203:
				InputPasscode = kvList.getValue("Passcode");
				
				if (InputPasscode.equals(Passcode))
				{
					locked = true;
					this.sendMessage("BSKPositiveLock.XML");
				}
				else
				{
					this.sendMessage("BSKNegativeLock.XML");
				}
				break;
			case 204:
				InputPasscode = kvList.getValue("Passcode");
				
				if (InputPasscode.equals(Passcode))
				{
					locked = false;
					
					this.sendMessage("BSKPositiveUnlock.XML");
				}
				else
				{
					this.sendMessage("BSKNegativeUnlock.XML");
				}
				break;
		}
	}
	
	public void sendOutput(String Accessible, String Available, KeyValueList kvList, String Diagnosis)
	{
		KeyValueList aKeyValueList = new KeyValueList();
		
		aKeyValueList.addPair("MsgID", "202");
		aKeyValueList.addPair("Description", "Inquire Blood Sugar Empirical Knowledge");
		aKeyValueList.addPair("Inquirer", kvList.getValue("Inquirer"));
		aKeyValueList.addPair("Accessible", Accessible);
		if (Accessible.equals("Yes"))
		{
			aKeyValueList.addPair("Available", Available);
			if (Available.equals("Yes"))
			{
				aKeyValueList.addPair("Sex", kvList.getValue("Sex"));
				aKeyValueList.addPair("Age", kvList.getValue("Age"));
				aKeyValueList.addPair("Weight", kvList.getValue("Weight"));
				aKeyValueList.addPair("Height", kvList.getValue("Height"));
				aKeyValueList.addPair("Diabetes", kvList.getValue("Diabetes"));
				aKeyValueList.addPair("Heart Disease", kvList.getValue("Heart Disease"));
				aKeyValueList.addPair("Meal", kvList.getValue("Meal"));
				aKeyValueList.addPair("Blood Sugar", kvList.getValue("Blood Sugar"));
				aKeyValueList.addPair("Diagnosis", Diagnosis);
			}
		}
		
		try
		{
			mEncoder.sendMsg(aKeyValueList, universal.getOutputStream());
		}
		catch (Exception e)
		{
			System.out.println("Exception sendOutput(String Accessible, String Available, KeyValueList kvList, String Diagnosis");
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
		SBSKB aSBSKB = new SBSKB(args[0]);
		aSBSKB.sendMessage("SBSKBconnect.XML");
		
		if (!aSBSKB.waitForAck())
		{
			throw new Exception("waitForAck");
		}
		
		Thread SBSKBThread = new Thread(aSBSKB);
		SBSKBThread.setDaemon(false);
		SBSKBThread.start();
	}
}
