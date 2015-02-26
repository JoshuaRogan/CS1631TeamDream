/*
 *CS1631
 *
 *Personal Health Care System
 *Blood Sugar Knowledge Base
 *
 *Group 5
 *Stephen Paine
 *Hemant Patel
 */
 
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*; //need for date stuff

 public class g5kb
 {
 	private static String theIP;
	private static String kbFileName="g5bloodsugar.kb";
 	public Socket theSocket;
 	public static boolean killDaemon = false;
 	public KeyValueList kvInput;
 	public MsgDecoder mDecoder;
 	public MsgEncoder mEncoder;
 	//public Date date;
	public treeNode root;
 	
 	public static void main(String [] args)
 	{
 		if(args.length != 1)
 		{
 			System.out.println("Incorrect number of parameters.");
 			System.out.println("Use the following instead: java g5kb <SIS Server IP>");
 			System.exit(0);
 		}
 		
 		theIP = args[0];
 		
 		Thread t = new Thread( new Runnable()
 			{
 				public void run()
 				{
 					g5kb BSKB=new g5kb();
					BSKB.messages(); ///DOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
				}
			});  //got from the SIS component example thingy
			
		t.setDaemon(true);
		t.start();
		
		//while(!killDaemon)
		//{
			//chill in here
		//}		
		System.out.println( "Hit Enter to stop.\n" );
		
		try
		{
			System.in.read();
		}
		catch (Throwable thr) {};
		
		//t.stop();
 	}
 	
 	public g5kb()
 	{
 		try
 		{
 		 	theSocket = new Socket (theIP, 7999);
 		 	mEncoder = new MsgEncoder();
			mDecoder = new MsgDecoder(theSocket.getInputStream());
			kvInput = new KeyValueList();
			// Not being used; we're not changing any dates
			//date = new Date(); //java.util.Date()
			
			//DateFormat dateformat = new SimpleDateFormat("yyyy_MM_dd");
			//DateFormat dateformat2 = new SimpleDateFormat("HH:mm:ss");
			//Format date according to XML style
			//DateFormat dateformat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
 		}
 		catch (Exception e)
 		{
 			System.out.println("An error has occurred.  Good thing this isn't a nuclear power plant.");
 			System.out.println("Error: " +e.getMessage());
 			e.printStackTrace();
 		}
 	}
 	
 	public void messages()
 	{
 		//Need to connect to the server
 		System.out.println("Attempting to connect to server");
 		
 		try
 		{
			kvInput.addPair("MsgID","23");
			kvInput.addPair("Description","Connect to server");
			kvInput.addPair("Passcode","****"); //the hell do i put down for passcode...?
			kvInput.addPair("SecurityLevel","3");
			kvInput.addPair("Name","g5kb");
			sendMsgToServer(kvInput);
 		}
 		catch (Exception e)
 		{
 			System.out.println("An error has occurred while trying to connect to SISServer.");
 			//System.out.println("Error: " +e.getMessage());
 			//e.printStackTrace();
 			System.exit(0);
 		}
 		System.out.println("Connected to SISServer.");
 		
 		while(true)
 		{
 			try
 			{
	 			kvInput = mDecoder.getMsg();
 			}
 			catch(Exception e)
 			{
 				System.out.println("An error has occurred while trying to receive a message from SISServer.");
 				//System.out.println("Error: " +e.getMessage());
 				//e.printStackTrace();
 				//System.exit(0);
 			}
 			
 			int msgID = Integer.parseInt(kvInput.getValue("MsgID")); //getMessageID
 			
 			if (msgID == 22)
 			{
 				killDaemon = true;
 			}
 			
 			else if (msgID == 26)
 			{
 				System.out.println("\nReceived an ACK message:\n");
 				System.out.println(kvInput);
 				//Don't think im supposed to do anything else here....?
 				
 			}
 			else if(msgID == 150)
 			{
 				System.out.println("\nReceived a message: ");
 				System.out.println(kvInput);
 				//ackMsg(150,"Yes");  //let Monitor know KB got message 150
 				
 				String diabetes = kvInput.getValue("Diabetes");
 				String temp2 = kvInput.getValue("Age");
 				String username = kvInput.getValue("UserName");
 				String date = kvInput.getValue("DateTime");
 				String temp = kvInput.getValue("Blood Sugar");
 				String Weight = kvInput.getValue("Weight");
 				String result = null;
 				treeNode currNode = null;
 				
 				int BloodSugar = Integer.parseInt(temp);
 				int age = Integer.parseInt(temp2);
 				String agetemp;
 				
 				if(age>15)
 					agetemp = "OVER 15";
 				else
 					agetemp = "UNDER 15";
 					
 				if(diabetes.equals("prediabetic"))
 					diabetes = "DIABETIC";
 				else if (diabetes.equals("diabetic"))
 					diabetes = "DIABETIC";
 				else
 					diabetes = "NONDIABETIC";
 				
 				System.out.println("Computing result");
 				//compute results
 				
 				boolean theKB = createKB();
 				
 				if(theKB)  
 				{
 					//traverse tree
 					if(root.leftNode.name.equals(agetemp))
 					{
 						currNode = root.leftNode;
 						if (currNode.leftNode.name.equals(diabetes))
 						{
 							currNode = currNode.leftNode;
 							
 							int low,high;
 							
 							low = currNode.leftNode.value;
 							high = currNode.rightNode.value;
 							
 							if(BloodSugar < low)
 								result = "Low";
 							else if(BloodSugar > high)
 								result = "High";
 							else
 								result = "Normal";
 						}
 						else
 						{
 							currNode = currNode.rightNode;
 							int low,high;
 							
 							low = currNode.leftNode.value;
 							high = currNode.rightNode.value;
 							
 							if(BloodSugar < low)
 								result = "Low";
 							else if(BloodSugar > high)
 								result = "High";
 							else
 								result = "Normal";
 						}
 						
 					}	
 					else
 					{
 						currNode = root.rightNode;
 						
 						if (currNode.leftNode.name.equals(diabetes))
 						{
 							currNode = currNode.leftNode;
 							
 							int low,high;
 							
 							low = currNode.leftNode.value;
 							high = currNode.rightNode.value;
 							
 							if(BloodSugar < low)
 								result = "Low";
 							else if(BloodSugar > high)
 								result = "High";
 							else
 								result = "Normal";
 						}
 						else
 						{
 							currNode = currNode.rightNode;
 							int low,high;
 							
 							low = currNode.leftNode.value;
 							high = currNode.rightNode.value;
 							
 							if(BloodSugar < low)
 								result = "Low";
 							else if(BloodSugar > high)
 								result = "High";
 							else
 								result = "Normal";
 						}
 					} 					
 				}
 				
 				else //unable to create knowlegde base so use default values
 				{
	 				if (!diabetes.toLowerCase().equals("normal")&& (age >15))
	 				{
	 					if(BloodSugar < 90)
	 						result = "Low";
	 					else if(BloodSugar > 170)
	 						result = "High";
	 					else
	 						result = "Normal";
	 				}
	 				else if (!diabetes.toLowerCase().equals("normal") &&(age <=15))
	 				{
	 					if(BloodSugar < 95)
	 						result = "Low";
	 					else if(BloodSugar > 180)
	 						result = "High";
	 					else
	 						result = "Normal";
	 					
	 				}
	 				else if (diabetes.toLowerCase().equals("normal") && (age >15))
	 				{
	 					if(BloodSugar < 80)
	 						result = "Low";
	 					else if(BloodSugar > 120)
	 						result = "High";
	 					else
	 						result = "Normal";	
	 					
	 				}
	 				else //if (diabetes.toLowerCase().equals("normal") && (age <=15))
	 				{
	 					if(BloodSugar < 90)
	 						result = "Low";
	 					else if(BloodSugar > 130)
	 						result = "High";
	 					else
	 						result = "Normal";
	 				}
 				}
 				
 				KeyValueList theResults = new KeyValueList();
 				theResults.addPair("MsgID","151");
 				theResults.addPair("Description","Return Result");  
 				theResults.addPair("Diagnosis",result);
 				theResults.addPair("UserName", username);
 				theResults.addPair("DateTime",date);
				theResults.addPair("Blood Sugar",temp);
 				
 				if(result.equals("Normal"))
 				{
 					theResults.addPair("Suggestions","Keep staying healthy!");
 				}
 				else if(result.equals("High"))
 				{
 					theResults.addPair("Suggestions","Talk to your physician or doctor.");
 				}
 				else //if(result.equals("Low"))
 				{
 					theResults.addPair("Suggestions","Talk to your physician or doctor.");
 				}
 				
 				//end of computing results	
 				 			 				
 				sendMsgToServer(theResults);
 			}
 			else //receive a message KB is not supposed to receive
 			{
 				System.out.println("Received message with ID " +kvInput.getValue("MsgID") + " for some reason...");
 			}			
 		}
 	}
 	
 	public void ackMsg(int msgID, String yesno)
 	{
		KeyValueList ack=new KeyValueList();
		
		ack.addPair("MsgID","26");
		
		String temp = Integer.toString(msgID);
		
		ack.addPair("AckMsgID",temp);
		ack.addPair("Yes/No",yesno);
		
		sendMsgToServer(ack);
	}
	
	public void sendMsgToServer(KeyValueList temp)
	{
		try
		{
			System.out.println("\nSending message: ");
			System.out.println(temp);
			mEncoder.sendMsg(temp, theSocket.getOutputStream());
		}
		catch (Exception e)
		{
			System.out.println("An error has occurred while trying to send a message to SISServer.");
 			//System.out.println("Error: " +e.getMessage());
 			//e.printStackTrace();
 			System.exit(0);
			
		}
	}
	
	public boolean createKB() //create kb in tree form
	{
		ArrayList<String> input = null;
		boolean exists = (new File(kbFileName)).exists(); 
		if (exists) 
			System.out.println("KB File Exists");
		else
			System.out.println("KB File Doesn't Exist");
		
		if (!exists)
		{
			return false;
		}
		
		try
		{		
			FileInputStream fstream = new FileInputStream(kbFileName);
    		DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		String theLine;
	    	input = new ArrayList<String>();
    	
    		while ((theLine = br.readLine()) != null)
    		{
      			input.add(theLine);
	    	}
    		
    		in.close();
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			return false;
		}
		
		String[] temp;
		String temp2;
		String delimiter = "&&&";
		
		int udl=0,udh=0,unl=0,unh=0,odl=0,odh=0,onl=0,onh =0;
		
		
		//read in values from file
		for (int i = 0; i< input.size();i++)
		{
			//temp2 =(String) input.get(i);
			temp2 = input.get(i);
			temp = temp2.split(delimiter);
			
			if (temp[0].equals("under15"))
			{
				if (temp[1].equals("diabetic"))
				{
					if (temp[2].equals("low"))
					{
						udl = Integer.parseInt(temp[3]);						
					}
					else //if temp[2].equals("high")
					{
						udh = Integer.parseInt(temp[3]);
						
					}
				}
				else //if (temp[1].equals("nondiabetic")
				{
					if (temp[2].equals("low"))
					{
						unl = Integer.parseInt(temp[3]);	
					}
					else //if temp[2].equals("high")
					{
						unh = Integer.parseInt(temp[3]);
					}
				}
			}
			else //(temp[0].equals("over15")
			{
				if (temp[1].equals("diabetic"))
				{
					if (temp[2].equals("low"))
					{
						odl = Integer.parseInt(temp[3]);
					}
					else //if temp[2].equals("high")
					{
						odh = Integer.parseInt(temp[3]);
					}
				}
				else //if (temp[1].equals("nondiabetic")
				{
					if (temp[2].equals("low"))
					{
						onl = Integer.parseInt(temp[3]);	
					}
					else //if temp[2].equals("high")
					{
						onh = Integer.parseInt(temp[3]);
					}
				}	
			}
		}
		
		
		root = new treeNode("ROOT");
		//root.name = "ROOT";
		root.leftNode = new treeNode("OVER 15");
		root.rightNode = new treeNode("UNDER 15");
		
		treeNode rootLeft = root.leftNode;
		treeNode rootRight = root.rightNode;
		
		rootLeft.leftNode = new treeNode("DIABETIC");
		rootLeft.rightNode = new treeNode("NONDIABETIC");
		treeNode dia = rootLeft.leftNode;
		treeNode nonDia = rootLeft.rightNode;
		
		dia.leftNode = new treeNode("LOW",odl);
		dia.rightNode = new treeNode("HIGH",odh);
		nonDia.leftNode = new treeNode("LOW",onl);
		nonDia.rightNode = new treeNode("HIGH",onh);
		
		rootRight.leftNode = new treeNode("DIABETIC");
		rootRight.rightNode = new treeNode("NONDIABETIC");
		dia = rootRight.leftNode;
		nonDia = rootRight.rightNode;
		
		dia.leftNode = new treeNode("LOW",udl);
		dia.rightNode = new treeNode("HIGH",udh);
		nonDia.leftNode = new treeNode("LOW",unl);
		nonDia.rightNode = new treeNode("HIGH",unh);
		
		return true;
	}	
	
 }
 
  class treeNode
 {
 	int value;
 	String name;
 	treeNode leftNode;
 	treeNode rightNode;
 	
 	public treeNode()
 	{
 		//name = "default";
 		//treeNode leftNode;
 		//treeNode rightNode;
 		//value = 0;
 	}
 	public treeNode(String temp)
 	{
 		name = temp;
 		//treeNode leftNode;
 		//treeNode rightNode;
 		//value = 0;
 	}
 	public treeNode(String temp, int temp2)
 	{
 		name = temp;
 		//treeNode leftNode;
 		//treeNode rightNode;
 		value = temp2;
 	}
 	
 	public int getValue() //probably dont need this
 	{
 		return value;
 	}
 	
 	public void setValue(int temp) //probably dont need this
 	{
 		value = temp;
 	}
 	public void setName(String temp)  //probably dont need this
 	{
 		name = temp;
 	}
 	
 }
