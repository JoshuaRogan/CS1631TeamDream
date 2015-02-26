/*	EKG Component Code for CS 1631
 *	Group 8:
 *	Brad Vukich
 *	AJ Vento
 * Version 3.0 - For Final Demo
 *	3/30/10
 */
 
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

public class g8kb
{	
	private static final int PORT = 7999;
	private Socket socket;
	
	// user profile data
	private int age;
	private double bmi;
	private String sex;
	private String heartDisease;
	
	public static void main(String[] args)
	{
		// if not enough arguments, print error and exit
		if (args.length < 1)
        {
            System.out.println("Error - Insufficient arguments: Please provide IP address of SISServer");
            System.exit(0);
        }
		try
		{
			g8kb kb = new g8kb(args[0]);	
		}
		catch(Exception e)
		{
			System.out.println("Error - Exception in g8EKGKnowledgeBase creation: " + e.getMessage());
		}
	}
	
	public g8kb(String IPAddress) throws Exception
	{
		// create socket from IP address and port and instanciate message reading objects
		socket = new Socket(IPAddress, PORT);
		MsgEncoder mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(socket.getInputStream());
		
		Thread t = new Thread (new Runnable()
		{
			public void run()
			{
				KeyValueList kvInput;
				try
				{
					// send a request to the server for connection
					KeyValueList kvOutput = new KeyValueList();
					kvOutput.addPair("MsgID", "23");
					kvOutput.addPair("Description", "Connect to Server");
					kvOutput.addPair("Passcode", "****");
					kvOutput.addPair("SecurityLevel", "3");
					kvOutput.addPair("Name", "g8kb");
					System.out.println("=========Send msg23=========");
					SendToServer(kvOutput);
					
					// then send a message to the monitor confirming the KB existance
					kvOutput = new KeyValueList();
					kvOutput.addPair("MsgID", "181");
					kvOutput.addPair("Description", "Create EKG Knowledge Base Acknowledge");
					kvOutput.addPair("AckMsgID", "21");
					kvOutput.addPair("Activated", "True");
					kvOutput.addPair("Name", "g8kb");
					System.out.println("=========Send msg181=========");
					SendToServer(kvOutput);
										
					while(true)
					{	
						// recieve a message, parse it, and print it out depeinding on the MsgID
						kvInput = mDecoder.getMsg();
						if(kvInput != null)
						{
							System.out.println("============== Message Recieved! =================");
							processMsg(kvInput);
						}
					}
				}
				catch(Exception e)
				{
					System.out.println("Error - Exception in Thread Run: " + e.getClass() + ", " + e.getMessage());
				}
			}
		}); // end Thread
		
		// mark thread as a Daemon to keep from inhibiting program shutdown
		t.setDaemon(false);
		t.start();
	}
    
    synchronized public void processMsg(KeyValueList kvInput) throws Exception
	{
    	ArrayList<Socket> sendlist;
    	ArrayList<String> inmsgs, outmsgs;
		int MsgID = Integer.parseInt(kvInput.getValue("MsgID"));
		KeyValueList kvOutput;

		switch(MsgID)
		{
			case 26:	// *** SISServer Acknowledgement ***
			System.out.println("Recieved Msg26");
				break;
				
			case 182:	// *** Send EKG Data ***
			System.out.println("Recieved Msg182");			    
				// set the user profile variables    	
				age = Integer.parseInt(kvInput.getValue("Age"));
				bmi = Double.parseDouble(kvInput.getValue("BMI"));
				sex = kvInput.getValue("Sex");
				heartDisease = kvInput.getValue("HeartDisease");	    
				
				// parse the processed data from the monitor
				int rate = Integer.parseInt(kvInput.getValue("HeartRate"));
				int rhythm = Integer.parseInt(kvInput.getValue("Rhythm"));
				int qt = Integer.parseInt(kvInput.getValue("QTLength"));
				
				// get health diagnosis from the knowledge base
				String rateResult = Diagnose("HeartRate", rate);
				String rhythmResult = Diagnose("Rhythm", rhythm);
				String qtResult = Diagnose("QTLength", qt);
				
				// send results back to the monitor using message 183
				kvOutput = new KeyValueList();
				kvOutput.addPair("MsgID", "183");
				kvOutput.addPair("Description", "Recieve EKG Data");
				kvOutput.addPair("HeartRate", rateResult);
				kvOutput.addPair("Rhythm", rhythmResult);
				kvOutput.addPair("QTLength", qtResult);
				kvOutput.addPair("SourceComp", "g8kb");
				System.out.println("=========Send msg183=========");
				SendToServer(kvOutput);
				break;
				
				
			default:
				System.out.println("Unsupported Message:  MsgID " + MsgID);
				break;
		}
    }

	// wll send messages to the server
	public void SendToServer(KeyValueList kvInput)
	{
		try
		{
			MsgEncoder mEncoder = new MsgEncoder();
			if(socket != null)
			{
				mEncoder.sendMsg(kvInput, socket.getOutputStream());
				System.out.println("Socket = " + socket.toString());
				System.out.println("Stream = " + socket.getOutputStream().toString());
				System.out.println("=========MSG SENT SUCCESS=========");
				System.out.println(kvInput.toString());
			}
			else
			{
				System.out.println("Socket is null...");
			}
		}
		catch(Exception e)
		{
			System.out.println("Error - Exception while sending message to server: " + e.getClass() + ", " + e.getMessage());
		}
	}
	
	public String Diagnose(String statType, int stat)
	{
		// return either Normal or an abnormal string for the specific statType
		String result;
		
		// checking heart rate
		if(statType.equals("HeartRate"))
		{
			// find an acceptable heart rate range based on 3 factors:
			//		age, sex, weight group (based on BMI)
			int min = 0, max = 0;
			
			// for men
			if(sex.equalsIgnoreCase("male"))
			{
				// average weight
				if(bmi < 30)
				{
					if(age <= 25){
						min = 49;
						max = 73;
					}
					else if(age <= 35){
						min = 49;
						max = 74;
					}
					else if(age <= 45){
						min = 50;
						max = 75;
					}
					else if(age <= 55){
						min = 50;
						max = 76;
					}
					else if(age <= 65){
						min = 51;
						max = 75;
					}
					else{	// > 65
						min = 50;
						max = 73;
					}
				}
				// overweight
				if(bmi >= 30)
				{
					if(age <= 25){
						min = 56;
						max = 81;
					}
					else if(age <= 35){
						min = 55;
						max = 81;
					}
					else if(age <= 45){
						min = 57;
						max = 82;
					}
					else if(age <= 55){
						min = 58;
						max = 83;
					}
					else if(age <= 65){
						min = 57;
						max = 81;
					}
					else{	// > 65
						min = 56;
						max = 79;
					}
				}
			}
			// for women
			else
			{
				// average weight
				if(bmi < 30)
				{
					if(age <= 25){
						min = 54;
						max = 78;
					}
					else if(age <= 35){
						min = 54;
						max = 78;
					}
					else if(age <= 45){
						min = 54;
						max = 78;
					}
					else if(age <= 55){
						min = 54;
						max = 77;
					}
					else if(age <= 65){
						min = 54;
						max = 77;
					}
					else{	// > 65
						min = 54;
						max = 76;
					}
				}
				// overweight
				if(bmi >= 30)
				{
					if(age <= 25){
						min = 61;
						max = 84;
					}
					else if(age <= 35){
						min = 60;
						max = 82;
					}
					else if(age <= 45){
						min = 60;
						max = 84;
					}
					else if(age <= 55){
						min = 60;
						max = 83;
					}
					else if(age <= 65){
						min = 60;
						max = 83;
					}
					else{	// > 65
						min = 60;
						max = 84;
					}
				}
			}
			
			// check user heart rate against rage, set health status
			if(stat < min){
				result = "Low";
			}
			else if(stat >= min && stat <= max){
				result = "Normal";
			}
			else{	// stat > max
				// specify levels of fast heart rate for different diagnoses
				if(stat > 350){	// Fibrillation 
					result = "Extremely High";
				}
				if(stat > 100){	// Tachycardia 
					result = "Very High";
				}
				else{	// generally high, Tachyarrhythmia
					result = "High";
				}
			}
			
			System.out.println("Heart Rate of " + stat + " is " + result);
		}
		// checking heart rhythm
		else if(statType.equals("Rhythm"))
		{
			if(stat == 0){
				result = "Normal";
			}
			else{
				result = "Abnormal";	// Arrhythmia
			}
			
			System.out.println("Heart Rhythm of " + stat + " is " + result);
		}
		// checking QT length
		else
		{
			// find range of acceptable QT lengths
			int min = 0, max = 0;
			
			// QT length has a slightly different rage for sexes
			if(sex.equalsIgnoreCase("male")){
				min = 300;
				max = 460;
			}
			else{	// female
				min = 300;
				max = 470;
			}
			
			if(stat < min){
				result = "Short";	// Short QT Syndrome
			}
			else if(stat >= min && stat <= max){
				result = "Normal";
			}
			else{	// stat > max
				result = "Long";	// Long QT Syndrome
			}
			
			System.out.println("QT Legnth of " + stat + " is " + result);
		}
		
		// finally, return the result
		return result;
	}
	
	/////////////////////////////////////// Universal Component Utilites ////////////////////////////////////////////
	
	/* MsgEncoder: standard XML message parser supplied by SISServer, 
			Serialize the KeyValue List and Send it out to a Stream
	*/
	public class MsgEncoder
	{
		private PrintStream printOut;
		
		// Default of delimiter in system is $$$
		private final String delimiter = "$$$";
	   
		public MsgEncoder(){}
	   
		// Encode the Key Value List into a string and Send it out
		public void sendMsg(KeyValueList kvList, OutputStream out) throws IOException
		{
			PrintStream printOut= new PrintStream(out);
			
			if (kvList == null)
			{
				return;
			}
			
			String outMsg= new String();
			for(int i=0; i<kvList.size(); i++)
			{
				if (outMsg.equals(""))
				{
					outMsg = kvList.keyAt(i) + delimiter + kvList.valueAt(i);
				}
				else
				{
					outMsg += delimiter + kvList.keyAt(i) + delimiter + kvList.valueAt(i);
				}
			}
			printOut.println(outMsg);
		}
	}

	/* MsgDecoder: standard XML message parser supplied by SISServer, 
			gets String from input Stream and reconstruct it to a Key Value List
	*/
	public class MsgDecoder 
	{
		private BufferedReader bufferIn;
		private final String delimiter = "$$$";
	   
		public MsgDecoder(InputStream in)
		{
			bufferIn  = new BufferedReader(new InputStreamReader(in));	
		}
	   
		// get String and output KeyValueList	   
		public KeyValueList getMsg() throws IOException
		{
			String strMsg= bufferIn.readLine();
		   
			if (strMsg==null)
			{
				return null;
			}
		   
			KeyValueList kvList = new KeyValueList();	
			StringTokenizer st = new StringTokenizer(strMsg, delimiter);
			while (st.hasMoreTokens()) 
			{
				kvList.addPair(st.nextToken(), st.nextToken());
			}
			return kvList;
		}
	}

	
	/* KeyValueList: standard XML message storage object as specified in SISServer
	*/
	public class KeyValueList
	{
		private Vector keys;
		private Vector values;
	   
		/* Constructor */
		public KeyValueList()
		{
			keys = new Vector();
			values = new Vector();
		}
	   
		// Look up the value given key, used in getValue() 
		public int lookupKey(String strKey)
		{
			for(int i=0; i < keys.size(); i++)
			{
				String k = (String) keys.elementAt(i);
				if (strKey.equals(k))
				{
					return i;
				}
			} 
			return -1;
		}
	   
		// add new (key,value) pair to list	   
		public boolean addPair(String strKey,String strValue)
		{
			return (keys.add(strKey) && values.add(strValue));
		}
	   
		// get the value given key
		public String getValue(String strKey)
		{
			int index=lookupKey(strKey);
			if (index==-1)
			{
				return null;
			}
			return (String) values.elementAt(index);
		} 
		
		public void setValue(int index, String val)
		{
			if(index >= 0 && index < size())
			{
				values.set(index, val);
			}
		}

		// Show whole list
		public String toString()
		{
			String result = new String();
			for(int i=0; i<keys.size(); i++)
			{
				result+=(String) keys.elementAt(i)+":"+(String) values.elementAt(i)+"\n";
			} 
			return result;
		}
	   
		public int size()
		{ 
			return keys.size(); 
		}
	   
		// get Key or Value by index
		public String keyAt(int index){ return (String) keys.elementAt(index);}
		public String valueAt(int index){ return (String) values.elementAt(index);}
		
		public ArrayList<String> getValueLike(String key)
		{
			String temp;
			ArrayList<String> results = new ArrayList<String>();
			for(int i=0; i < keys.size(); i++)
			{
				temp = (String) keys.elementAt(i);
				if (temp.contains(key)) 
				{
					results.add((String) values.elementAt(i));
				}
			}
			if(results.size() == 0)
			{
				return null;
			}
			return results;
		}
	}
	
}
