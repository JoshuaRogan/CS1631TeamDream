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
import java.util.regex.*;

public class g8ekg
{	
	private static final int PORT = 7999;	
	private Socket socket;
	
	private boolean kbExists = false;
	private boolean profileExists = false;
	private final int maxZeros = 9;
	
	// EKG lead data
	private String lead1;
	private String lead2;
	private String lead3;
	
	// user profile data
	private double bmi;
	private String age;
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
			g8ekg ekg = new g8ekg(args[0]);	
		}
		catch(Exception e)
		{
			System.out.println("Error - Exception in EKGComponent creation: " + e.getMessage());
		}
	}
	
	public g8ekg(String IPAddress) throws Exception
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
					kvOutput.addPair("Name", "g8ekg");
					System.out.println("=========Send msg23=========");
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
			
			case 35:	// *** EKG Reading ***
			System.out.println("Recieved Msg35");
				// aquire raw EKG data
				lead1 = kvInput.getValue("LeadI");
				lead2 = kvInput.getValue("LeadII");
				lead3 = kvInput.getValue("LeadIII");
				
				// only send to KB if it exists and we have user profile data
				if(kbExists == true)
				{
					if(profileExists == true)
					{
						// process raw data to find rate, rhythm, etc
						int[] heartData = ProcessData(lead1, lead2, lead3);
						int rate = heartData[0];
						int rhythm = heartData[1];
						int qtLength = heartData[2];
						
						// send the processed data to the EKG knowledge base
						kvOutput = new KeyValueList();
						kvOutput.addPair("MsgID", "182");
						kvOutput.addPair("Description", "Send EKG Data");
						kvOutput.addPair("HeartRate", "" + rate);
						kvOutput.addPair("Rhythm", "" + rhythm);
						kvOutput.addPair("QTLength", "" + qtLength);
						kvOutput.addPair("BMI", "" + bmi);
						kvOutput.addPair("Age", age);
						kvOutput.addPair("Sex", sex);
						kvOutput.addPair("HeartDisease", heartDisease);
						kvOutput.addPair("DestinationComp", "g8kb");
						kvOutput.addPair("SourceComp", "g8ekg");
						SendToServer(kvOutput);
					}
					else
					{
						System.out.println("Error - Cannot process data, user profile has not been obtained");
					}
				}
				else
				{
					System.out.println("Error - Cannot process data, EKG Knowledge Base does not exist");
				}
				break;
				
			
			case 45:	// *** User Profile ***
			System.out.println("Recieved Msg45");
				// mark that the profile data has been recieved
				profileExists = true;
				
				// parse desired data from profile and store in object
				age = kvInput.getValue("Age");
				sex = kvInput.getValue("Sex");
				heartDisease = kvInput.getValue("HeartDisease");
				
				// calculate bmi using height and weight
				double weight = Double.parseDouble(kvInput.getValue("Weight"));
				double height = Double.parseDouble(kvInput.getValue("Height"));
				bmi = (weight*703)/(height*height);
				break;
				
				
			case 181:	// *** Create EKG Knowledge Base Acknowledgement ***
			System.out.println("Recieved Msg181");
				// if the knowledge base was created, set the exist variable to true
				if(kvInput.getValue("Activated").equals("True"))
				{
					kbExists = true;
				}
				else
				{
					kbExists = false;
					System.out.println("Error - EKG Knowledge Base does not exist");
				}
				break;
				
				
			case 183:	// *** Recieve EKG Data ***
			System.out.println("Recieved Msg183");
				// examine the results from the knowledge base
				String rateResult = kvInput.getValue("HeartRate");
				String rhythmResult = kvInput.getValue("Rhythm");
				String qtResult = kvInput.getValue("QTLength");
				
				// use flag to mark whether an alert must be issued and store appropriate responses
				boolean alert = false;
				String alertType = "EKG Alert";
				String diagnosis = "None";
				String suggestion = "None";
				
				/* check each heart reading for abnormalities in a prioritized order,
						heart rate being most significant and QT length being least significant
				*/
				// ----- Heart Rate -----
				if(!rateResult.equals("Normal"))
				{
					alert = true;
					if(rateResult.equals("Low"))
					{
						alertType = "Abnormally Low Heart Rate";
						diagnosis = "Possible Bradyarrhythmia";
						suggestion = "Consult you doctor.";
					}
					else if(rateResult.equals("High"))
					{
						alertType = "Abnormally High Heart Rate";
						diagnosis = "Possible Tachyarrhythmia";
						suggestion = "Consult you doctor.";
					}
					else if(rateResult.equals("Very High"))
					{
						alertType = "Abnormally High Heart Rate";
						diagnosis = "Tachycardia";
						suggestion = "Seek medical attention as soon as possible.";
					}
					else if(rateResult.equals("Extremely High"))
					{
						alertType = "Abnormally High Heart Rate";
						diagnosis = "Fibrillation";
						suggestion = "Seek medical attention immediately.";
					}
				}
				// ----- Heart Rhythm -----
				else if(!rhythmResult.equals("Normal"))
				{
					alert = true;
					alertType = "Abnormal Heart Rhythm";
					diagnosis = "Possible Arrhythmia";
					suggestion = "Seek medical attention as soon as possible.";					
				}
				// ----- QT Length -----
				else if(!qtResult.equals("Normal"))
				{
					alert = true;
					if(rateResult.equals("Short"))
					{
						alertType = "Abnormally Short QT Length";
						diagnosis = "Short QT Syndrome";
						suggestion = "Consult you doctor.";
					}
					else if(rateResult.equals("Long"))
					{
						alertType = "Abnormally Long QT Length";
						diagnosis = "Long QT Syndrome";
						suggestion = "Consult you doctor.";
					}
				}
				
				// if an alert was asserted, send message 36
				if(alert)
				{
					kvOutput = new KeyValueList();
					kvOutput.addPair("MsgID", "36");
					kvOutput.addPair("Description", "EKG Alert");
					kvOutput.addPair("Alert Type", alertType);					
					kvOutput.addPair("Diagnosis", diagnosis);
					kvOutput.addPair("Suggestions", suggestion);
					kvOutput.addPair("Name", "g8ekg");
					Date date = new Date();
					DateFormat dateformat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
					kvOutput.addPair("DateTime",  dateformat.format(date));
					SendToServer(kvOutput);
				}
				break;
				
				
			default:
				System.out.println("Unsupported Message:  MsgID " + MsgID);
				break;
		}
    }

	//wll send messages to the server
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
			}
			else
			{ 
				System.out.println("Error - Socket is null");
			}
		}
		catch(Exception e)
		{
			System.out.println("Error - Exception while sending message to server: " + e.getClass() + ", " + e.getMessage());
		}
	}
	
	/* ProcesData:
	 *		analyzes peaks and falls of lead readings to produce heart rate, rhythm, and other statstics
			to send to Knowledge Base
	 */
	public int[] ProcessData(String lead1, String lead2, String lead3)
	{		
		System.out.println("Processing EKG Data");
		
		// set the interval of lead readings to 10 milliseconds
		double readInterval = .010;
		Pattern p = Pattern.compile("(0 ){"+maxZeros+",}");

		String[] l1toks;
		String[] l2toks;
		String[] l3toks;
		
		ArrayList<PulseWave> LEADI = new ArrayList<PulseWave>();
		ArrayList<PulseWave> LEADII = new ArrayList<PulseWave>();
		ArrayList<PulseWave> LEADIII = new ArrayList<PulseWave>();
		 
		l1toks = p.split(lead1); 
		l2toks = p.split(lead2);
		l3toks = p.split(lead3);

	//    //this is debug material----------------
	//    System.out.println(l1toks.length);
	//    System.out.println(l2toks.length);
	//    System.out.println(l3toks.length);
		//--------------------------------------

		//for LEADI
		for(int i = 1; i < (l1toks.length-1); i++)
		{
			PulseWave temp = new PulseWave(l1toks[i]);
			LEADI.add(temp);

	//        System.out.println("==========DEBUG==============================================");
	//        System.out.println(l1toks[i]);
	//        System.out.println("P: "+LEADI.get(i-1).getP().toString());
	//        System.out.println("Q: "+LEADI.get(i-1).getQ().toString());
	//        System.out.println("QT: "+LEADI.get(i-1).getQT().toString());
	//        System.out.println("R: "+LEADI.get(i-1).getR().toString());
	//        System.out.println("S: "+LEADI.get(i-1).getS().toString());
	//        System.out.println("T: "+LEADI.get(i-1).getT().toString());
	//        System.out.println("Total: "+LEADI.get(i-1).getTotal() .toString());
	//        System.out.println("==============================DEBUG==========================\n\n\n");
		}

		//for LEADII
		for(int j = 1; j < (l2toks.length - 1); j++)
		{
			PulseWave temp = new PulseWave(l2toks[j]);
			LEADII.add(temp);
			
	//        System.out.println("==========DEBUG==============================================");
	//        System.out.println(l1toks[j]);
	//        System.out.println("P: "+LEADII.get(j-1).getP().toString());
	//        System.out.println("Q: "+LEADII.get(j-1).getQ().toString());
	//        System.out.println("QT: "+LEADII.get(j-1).getQT().toString());
	//        System.out.println("R: "+LEADII.get(j-1).getR().toString());
	//        System.out.println("S: "+LEADII.get(j-1).getS().toString());
	//        System.out.println("T: "+LEADII.get(j-1).getT().toString());
	//        System.out.println("Total: "+LEADII.get(j-1).getTotal() .toString());
	//        System.out.println("==============================DEBUG==========================\n\n\n");
		}
	 
		//for LEADIII
		for(int i = 1; i < (l3toks.length - 1); i++)
		{
			PulseWave temp = new PulseWave(l3toks[i]);
			LEADIII.add(temp);

	//        System.out.println("==========DEBUG3==============================================");
	//        System.out.println(l3toks[i]);
	//        System.out.println("P: "+LEADIII.get(i-1).getP().toString());
	//        System.out.println("Q: "+LEADIII.get(i-1).getQ().toString());
	//        System.out.println("QT: "+LEADIII.get(i-1).getQT().toString());
	//        System.out.println("R: "+LEADIII.get(i-1).getR().toString());
	//        System.out.println("S: "+LEADIII.get(i-1).getS().toString());
	//        System.out.println("T: "+LEADIII.get(i-1).getT().toString());
	//        System.out.println("Total: "+LEADIII.get(i-1).getTotal() .toString());
	//        System.out.println("==============================DEBUG3==========================\n\n\n");
		}

		//we find the value of the total number of readings
		StringTokenizer st = new StringTokenizer(lead1);
		int count = st.countTokens();
		int[] inputLEAD = new int[count];
		int j = 0;
		int curToken;
		while (st.hasMoreTokens())
		{
			curToken = Integer.parseInt(st.nextToken());
			inputLEAD[j] = curToken;
			j++;
		}

       // multiply the total readings by the readings per unit time, then convert to beats ber minute
       double totalTime = count * readInterval;
       double heartRate = ((LEADI.size()+1)/totalTime) * 60;

       //now we need to find average QT length
       double aveQT = 0;
       for(int i = 0; i < LEADI.size(); i++)
       {
           ArrayList temp = LEADI.get(i).getQT();
           aveQT += temp.size();
       }
       aveQT = aveQT/(LEADI.size());
	   
	   // find overall QT length and convert to milliseconds
       double timeQT = (aveQT * readInterval) * 1000;

       //here we will collect the ryhtm of the heart
       Pattern p0 = Pattern.compile("([1-9][0-9]* )+");
       String[] zeroToks;
       zeroToks = p0.split(lead1);
       int goodPulses = 0;
       double aveZero = 0;
	   int goodRhythm = 0;
       for(int i = 0; i < zeroToks.length; i++)
       {
            StringTokenizer zt = new StringTokenizer(zeroToks[i]);
            int zeroCount = zt.countTokens();
            if(zeroCount > 12)
            {
                goodPulses++;
                aveZero = aveZero + zeroCount;
            }
       }
       aveZero = aveZero/goodPulses;
       double rythm = aveZero * readInterval;
//       System.out.println(rythm);
//       System.out.println(goodPulses);
//       System.out.println(aveZero);

		double threshold = .10;
		double lowHold  = (aveZero - (aveZero * threshold));
		double highHold = (aveZero + (aveZero * threshold));
		int badCount = 0;
		for(int i = 0; i < zeroToks.length; i++)
		{
			StringTokenizer zt = new StringTokenizer(zeroToks[i]);
			int zeroCount = zt.countTokens();
			if(zeroCount > maxZeros)
			{
				if((zeroCount < lowHold) || (zeroCount > highHold))
				{ badCount++;}
			}
		}
		// if more than 15% of the total pulses are off the average, send an alert
		if(badCount > (goodPulses * .10))
		{
			goodRhythm = 1;
		}

       //the final readings!
       System.out.println(heartRate);
       System.out.println(timeQT);
       System.out.println(goodRhythm);
		
		int[] results = {(int)heartRate, goodRhythm, (int)timeQT};
		return results;
	}
	
	
	/* Class Pulsewave: divides and stores EKG lead information into specific sections
			for heart beat analysis, including heart rate, rhythm, and QT length
	*/
	private class PulseWave
	{
		private ArrayList totalWave;
		private ArrayList P;
		private ArrayList Q;
		private ArrayList R;
		private ArrayList S;
		private ArrayList T;
		private ArrayList QT;

		public PulseWave()
		{
			totalWave = null;
			P = null;
			Q = null;
			R = null;
			S = null;
			T = null;
			QT = null;
		}
		
		public PulseWave(String sInput)
		{
			//we need to take this string input and make it int[]
		   StringTokenizer st = new StringTokenizer(sInput);
		   int count = st.countTokens();
		   int[] input = new int[count];
		   int j = 0;
		   int curToken;

		   while (st.hasMoreTokens())
		   {
				curToken = Integer.parseInt(st.nextToken());
				input[j] = curToken;
				j++;
		   }
		   //this should turn our string input into a beautiful INT[]...
		   //hopefully

			boolean bP = true, bQ = false, bR = false, bS = false, bT = false;

			P = new ArrayList(15);
			Q = new ArrayList(15);
			R = new ArrayList(15);
			S = new ArrayList(15);
			T = new ArrayList(15);
			QT = new ArrayList(25);
			totalWave = new ArrayList(50);

			for(int i = 0; i < input.length; i++)
			{
				if(bP) //check for PQ
				{
					if(input[i] < 0)
					{
						bP = false;
						Q.add(input[i]);
						bQ = true;
					}
					else
					{P.add(input[i]);}
				} //P CHECK!
				else if(bQ) // check for QR
				{
					if(input[i] > 0)
					{
						bQ = false;
						R.add(input[i]);
						bR = true;
					}
					else
					{Q.add(input[i]);}
				}//end of Q CHECK
				else if(bR) // check for RS
				{
					if(input[i] < 0)
					{
						bR = false;
						S.add(input[i]);
						bS = true;
					}
					else
					{R.add(input[i]);}
				}//end of R CHECK
				else if(bS) //start of ST
				{
					if(input[i] > 0)
					{
						bS = false;
						T.add(input[i]);
						bT = true;
					}
					else
					{ S.add(input[i]); }
				}//end of ST CHECK
				else //start of T to end
				{ T.add(input[i]); }

				//add to the total of the wave
				totalWave.add(input[i]);
			}//end of FOR

			QT.addAll(Q);
			QT.addAll(R);
			QT.addAll(S);
			QT.addAll(T);
		} // end of constructor

		public ArrayList getP()
		{return(P);}

		public ArrayList getQ()
		{return(Q);}

		public ArrayList getR()
		{return(R);}

		public ArrayList getS()
		{return(S);}

		public ArrayList getT()
		{return(T);}

		public ArrayList getTotal()
		{return(totalWave);}

		public ArrayList getQT()
		{return(QT);}
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
