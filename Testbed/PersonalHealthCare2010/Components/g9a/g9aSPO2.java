import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class g9aSPO2 implements ComponentBase, Runnable{
	private Socket universal;
	private MsgEncoder mEncoder;
	private MsgDecoder mDecoder;
	private boolean active;
	private int spo2;
	private String str;
	private AppManager apMan;

	g9aSPO2(String IP, AppManager apMan)
	{
		try	{
			this.apMan = apMan;
			universal = new Socket(IP, 7999);
			mEncoder = new MsgEncoder();
			mDecoder = new MsgDecoder(universal.getInputStream());
			active = false;
		}
		catch (Exception e)	{
			System.out.println("Exception");

		}
	}
	
	public void sendMessage(String xmlpath)
	{
		try
		{
			g9aWrap kvlWrap = new g9aWrap(xmlpath);
			OutputStream out = universal.getOutputStream();
			mEncoder.sendMsg(kvlWrap, out);
		}
		catch (Exception e)
		{
			System.out.println("Exception sendMessage(String xmlpath)");
		}
	}

	/**
	 * This method change to blocking method, just wait until Server response or terminate signal initiate
	 * @return
	 * @throws Exception
	 */
	public boolean waitForAck() throws Exception
	{
		while (apMan.isRun()){
			KeyValueList kvl = mDecoder.getMsg();
			if (kvl == null) continue;

			String MsgID = kvl.getValue("MsgID");
			if (MsgID.equals("26"))
			{
				active = true;			
				return true;
			}
			
			Thread.sleep(500); //just sleep for a half second, try to avoid CPU overhead
		}
		return false;
	}

	
	public void run(){
		KeyValueList kvInput;
		try	{
			while (apMan.isRun()){
				kvInput = mDecoder.getMsg();
				processMsg(kvInput);
			}
		}
		catch (Exception e)	{
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
			System.out.println("Component Not Active");
			
			return;
		}
		
		switch (MsgID)
		{
			case 25:
				active = false;
				System.out.println("Processing Message 25: Deactivating Component.");
				break;
			
			case 22:
				g9aWrap kvlWrap = new g9aWrap("22.xml");
				
			case 45:
				System.out.println("User Profile Received");
				break;
			
			case 37:
				spo2 = Integer.parseInt(kvList.getValue("SPO2"));
				g9aWrap SPO2Wrap = new g9aWrap("g9aSPO2kb.xml");
				int min = Integer.parseInt(SPO2Wrap.getValue("min"));
				int max = Integer.parseInt(SPO2Wrap.getValue("max"));
				if (spo2 < min || spo2 > max){
					sendMessage("g9aAlert(38).xml");
				} else {
					System.out.println("SPO2 Level = " + spo2);
				}
				
				break;
		}
	}
	
	private ArrayList getSPO2Thresholds(String xml) {
		ArrayList results = new ArrayList();
		DocumentBuilderFactory aDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder aDocumentBuilder;
		try {
			aDocumentBuilder = aDocumentBuilderFactory.newDocumentBuilder();
			Document aDocument = aDocumentBuilder.parse(new File(xml));
			
			NodeList spo2Levels = aDocument.getElementsByTagName("SPO2Level");
			Node spo2Level = spo2Levels.item(0);
			Node minNode = spo2Level.getFirstChild();
			String min = minNode.getTextContent();
			System.out.println("MIN = " + min);
			
			Node maxNode = spo2Level.getLastChild();
			String max = maxNode.getTextContent();
			System.out.println("MAX = " + max);
			results.add(min);
			results.add(max);
		} catch (Exception e) {
			System.err.println("Error parsing g9aSPO2kb.xml file");
			e.printStackTrace();
		}
		
		return results;
	}
	
	public static void main(String []args) throws Exception
	{	
		
		//initiate AppManager and run AppManager
		AppManager apMan = new AppManager();
		Thread apManThread = new Thread(apMan);
		apManThread.start();		
		
		//AppManager send to g9aSPO2, to interupt if application want to exit
		g9aSPO2 ghmspo2 = new g9aSPO2("127.0.0.1", apMan);
		ghmspo2.sendMessage("g9aAttemptServerConnection23.XML");
		
		boolean gotAck = ghmspo2.waitForAck();
		if (gotAck && apMan.isRun() && ghmspo2.active){
			Thread g9aghm = new Thread(ghmspo2);
			g9aghm.setDaemon(false);
			g9aghm.start();		
		}
	}
}


/**
 * 
 * Function : tell application to terminate properly.
 */
class AppManager implements Runnable{
	private boolean run = true;
	
	public boolean isRun(){
		return run;
	}
	
	public void run() {
			try {
				System.out.println("Press any keys to exit application.");
				System.in.read(); //just wait for keyboard hit
				
				//if any keyboard hit set run into false, indicate that app want to exit
				run = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
