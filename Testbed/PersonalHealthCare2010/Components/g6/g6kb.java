import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;
import java.net.*;

public class g6kb implements ComponentBase, Runnable
{
	private Socket universal;
	private MsgEncoder mEncoder;
	private MsgDecoder mDecoder;
	private boolean active;
	private Element root;

	public g6kb(String IP, String xmlpath)
	{
		try
		{
			universal = new Socket(IP, 7999);
			mEncoder = new MsgEncoder();
			mDecoder = new MsgDecoder(universal.getInputStream());
			active = false;
			
			DocumentBuilderFactory aDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder aDocumentBuilder = aDocumentBuilderFactory.newDocumentBuilder();
			Document aDocument = aDocumentBuilder.parse(new File(xmlpath));
			root = aDocument.getDocumentElement();
		}
		catch (Exception e)
		{
			System.out.println("Exception g6kb(String IP, String xmlpath)");
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
			case 45:
				UserProfile anUserProfile = new UserProfile(kvList);
				Thresholds aThresholds = getThresholds(anUserProfile);
				sendThresholds(aThresholds);
				break;
		}
	}	
	
	public void sendThresholds(Thresholds theThresholds)
	{
		try
		{
			mEncoder.sendMsg(theThresholds.toKeyValueList(), universal.getOutputStream());
		}
		catch (Exception e)
		{
			System.out.println("Exception sendAlert(Alert theAlert)");
		}		
	}
		
	private String getDescription(Element theElement)
	{
		NodeList aNodeList = theElement.getElementsByTagName("Description");
		
		return aNodeList.item(0).getFirstChild().getNodeValue();
	}
	
	private Element getElement(NodeList theNodeList, String Description)
	{
		NodeList aNodeList;
		String Value;
	
		for (int i=0;i<theNodeList.getLength();i++)
		{
			aNodeList = ((Element)theNodeList.item(i)).getElementsByTagName("Description");
			Value = aNodeList.item(0).getFirstChild().getNodeValue();
			if (Value.equals(Description))
			{
				return (Element)theNodeList.item(i);
			}
		}
		
		System.out.println("Error getElement(NodeList theNodeList, String Description): "+Description);
		
		return null;
	}
	
	public Thresholds getThresholds(UserProfile theUserProfile)
	{
		Element currElement = root;
		NodeList aNodeList;
		
		aNodeList = currElement.getElementsByTagName("AgeGroup");
		if (!theUserProfile.hasYear)
		{
			currElement = getElement(aNodeList, "Adult");
		}
		else
		{
			int Year = theUserProfile.getYear();
			
			if (Year<6)
			{
				currElement = getElement(aNodeList, "Under Age 6");
			}
			else if (Year>=6&&Year<=12)
			{
				currElement = getElement(aNodeList, "Age 6 - 12");
			}
			else if (Year>=13&&Year<=19)
			{
				currElement = getElement(aNodeList, "Age 13 - 19");
			}
			else
			{
				currElement = getElement(aNodeList, "Adult");
			}
		}
		
		String AgeGroup = getDescription(currElement);
		
		if (AgeGroup.equals("Adult"))
		{
			aNodeList = currElement.getElementsByTagName("Precondition");
			if (!theUserProfile.hasPrecondition)
			{
				currElement = getElement(aNodeList, "Normal");
			}
			else
			{
				String Precondition = theUserProfile.getPrecondition();
				
				currElement = getElement(aNodeList, Precondition);
			}
		}
		
		aNodeList = currElement.getElementsByTagName("Type");
		if (!theUserProfile.hasReadingType)
		{
			currElement = getElement(aNodeList, "Bedtime/overnight");
		}
		else
		{
			String ReadingType = theUserProfile.getReadingType();
			
			currElement = getElement(aNodeList, ReadingType);
		}
		
		Thresholds aThresholds = new Thresholds();
		
		aNodeList = currElement.getElementsByTagName("Min");
		if (aNodeList.getLength()==1)
		{
			int Value = Integer.parseInt(aNodeList.item(0).getFirstChild().getNodeValue());
		
			aThresholds.setMinimum(Value);
		}
		
		aNodeList = currElement.getElementsByTagName("Max");
		if (aNodeList.getLength()==1)
		{
			int Value = Integer.parseInt(aNodeList.item(0).getFirstChild().getNodeValue());
			
			aThresholds.setMaximum(Value);
		}
		
		return aThresholds;
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

		g6kb ag6kb = new g6kb(args[0], "g6bloodsugarkb.XML");
		ag6kb.sendMessage("g6kbconnect.XML");
		
		if (!ag6kb.waitForAck())
		{
			throw new Exception("waitForAck");
		}
		
		Thread g6kbThread = new Thread(ag6kb);
		g6kbThread.setDaemon(false);
		g6kbThread.start();
	}
}
