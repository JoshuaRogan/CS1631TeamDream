/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class g7ekg implements ComponentBase
{
	//declare intervals
	double qrs_interval = 0;
	double pr_interval = 0;
	double qt_interval = 0;
	
	String time;
	String lead1;
	String lead2;
	String lead3;
	int BPM=0;
	Socket server;
    MsgEncoder mEncoder;
    MsgDecoder mDecoder;
    boolean activated;//unused
	//private Socket client;

    g7ekg(String serveraddress){
    	try {
            activated = false;
            server = new Socket(serveraddress, 7999);
            //System.out.println("Connected to SISServer");
            mEncoder = new MsgEncoder();
            mDecoder = new MsgDecoder(server.getInputStream());

            //send connect to server message
            KeyValueList msg = null;
            msg = new KeyValueList();
            msg.addPair("MsgID","23");
            msg.addPair("Description", "Connect to Server");
            msg.addPair("SecurityLevel", "3");
            msg.addPair("Name", "g7ekg");
            mEncoder.sendMsg(msg, server.getOutputStream());

            //loop waiting for input
            while (true){
                KeyValueList kvInput = mDecoder.getMsg();
                if (kvInput != null){
                        //System.out.println("received message "+ kvInput.getValue("MsgID"));
                        processMsg(kvInput);
                }
                Thread.sleep(50);
            }

    	} catch (Exception e) {e.printStackTrace();}
    }


    public static void main(String args[]){
		g7ekg app = new g7ekg(args[0]);
	}//end main

    synchronized public void processMsg(KeyValueList kvList) throws Exception
	{
        int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
        System.out.println("EKGmon processing "+MsgID);
    	ArrayList<Socket> sendlist;
    	ArrayList<String> inmsgs, outmsgs;
    	CompMsgControl tempcmc;
    	String tempstr;
        KeyValueList msg = null;
        boolean sendmsg = false; //determines whether we send a message

		switch(MsgID)
		{
    		case(45):{//user profile
			//receive user profile - not relevant for current ekg kb
    		//allow component to accept other messages
    			activated = true;
    			break;
    		}
                case(24):{//activate component

                    //System.out.println("activate message");
                    activated = true;
                    break;
                }
                case(25):{//deactivate component
                	if (activated){
                		//System.out.println("deactivate message");
                		activated = false;
                	}
                    break;
                }
	    case(35):{//ekg reading message
	    	if (activated){
	    		//get the timestamp
	    		time = (new SimpleDateFormat("yyyy-MM-DD HH:mm:ss.SSSSSSSS")).format(new Date());
	    		System.out.println("time: "+time);
	    		
	    	//send data to method to be analyzed
	    		analyzeReading(kvList.getValue("LeadI"),kvList.getValue("LeadII"),kvList.getValue("LeadIII"));

	    		//get the timestamp
	    		time = (new SimpleDateFormat("yyyy-MM-DD HH:mm:ss.SSSSSSSS")).format(new Date());
	    		System.out.println("time: "+time);
	    		//construct a message for the knowledge base
	    		msg = new KeyValueList();
	    		msg.addPair("MsgID","170");
	    		msg.addPair("Description", "EKG KnowledgeBase Query");
	    		msg.addPair("DateTime", time);
	    		msg.addPair("BPM", String.valueOf(BPM));
	    		msg.addPair("AvgQtInterval", String.valueOf(qt_interval));
	    		msg.addPair("AvgPrInterval", String.valueOf(pr_interval));
	    		msg.addPair("AvgQrsInterval", String.valueOf(qrs_interval));
	    	

	    		MsgEncoder mEncoder= new MsgEncoder();
				try {
					mEncoder.sendMsg(msg, server.getOutputStream());
				} catch (IOException e) {e.printStackTrace();}
	    	}//endif activated
	    	break;
	    	
	    }//end case 35
	    case(171):{//ekg kb result message
	    	if (activated){
	    		//get the result
	    		String result = kvList.getValue("EkgStatus");

	    		//if result is bad, send alerts
	    		if (result.equals("bad")){
	    			msg = new KeyValueList();
	    			msg.addPair("MsgID","36");
	    			msg.addPair("Description", "EKG Alert");
	    			msg.addPair("LeadI", lead1);
	    			msg.addPair("LeadII", lead2);
	    			msg.addPair("LeadIII", lead3);
	    			msg.addPair("DateTime", time);
	    			msg.addPair("AlertType", "EKG Alert");
	    			msg.addPair("Name", "g7 EKG Monitor");
	    			msg.addPair("Diagosis", kvList.getValue("Diagnosis"));
	    			msg.addPair("Suggestions", kvList.getValue("Suggestions"));
	    			msg.addPair("DateTime", kvList.getValue("DateTime"));
	    		}
                MsgEncoder mEncoder= new MsgEncoder();
                try {
                	mEncoder.sendMsg(msg, server.getOutputStream());
                } catch (IOException e) {e.printStackTrace();}
	    	}//end if activated
            break;
	    }
	}
    }
    
    
	private void analyzeReading(String lead1string, String lead2string, String lead3string){
		lead1 = new String(lead1string);
		lead2 = new String(lead2string);
		lead3 = new String(lead3string);
		System.out.println("lead3string = "+lead3string);
		String lead1array[] = lead1string.split(" ");
		String lead2array[] = lead2string.split(" ");
		String lead3array[] = lead3string.split(" ");
		int num_data_pts = lead1array.length; //gets the amount of data received
		
		int time_index = 0; //marks the the place in the timeline
		int pulse_count = 0;
		
		//declare markers for current pulse intervals
		int mark1 = 0;
		int mark2 = 0;
		int mark3 = 0;
		int mark4 = 0;
		
		//declare tracking variables
		boolean lead1decreasing = false;
		boolean lead2decreasing = false;
		int count=0;
		
		//reset intervals
		qrs_interval = 0;
		pr_interval = 0;
		qt_interval = 0;
		
		//declare data pts
		int datapt1=-1;
		int datapt2=-1;
		int datapt3=-1;
		int datapt4=-1;
		

		
		//loop while more data to analyze
		while((time_index+1) < num_data_pts){
System.out.println("start of new pulse analysis: timeindex = "+time_index);
			datapt1=-1;
			datapt2=-1;
			datapt3=-1;
			datapt4=-1;
			
			
			//watch for lead3 going above zero after ~ 30 consecutive zeros
			count = 0;
			//wait for ~30 zeros
			while (count < 30 && ((time_index+1) < num_data_pts)){
				datapt1 = Integer.valueOf(lead3array[time_index]);
				time_index++;
				datapt2 = Integer.valueOf(lead3array[time_index]);
				if (datapt1 == 0 && datapt2 == 0){
					count++;
				}
			}
			//wait for lead3 going above zero
			while (Integer.valueOf(lead3array[time_index]) < 1 && (time_index+1) < num_data_pts){
				time_index++;
			}
			//set mark1
			mark1 = time_index;
			
			//watch for lead3 to be steady at zero and then lead 1 and lead 2 dropping
			//wait for lead3 to be steady (at zero)
			count = 0;
			while (count < 3 && ((time_index+1) < num_data_pts)){
				datapt1 = Integer.valueOf(lead3array[time_index]);
				time_index++;
				datapt2 = Integer.valueOf(lead3array[time_index]);
				if (datapt1 == 0 && datapt2 == 0){
					count++;
				}
			}
			//wait for both lead1 and lea2 to be decreasing
			lead1decreasing = false;
			lead2decreasing = false;
			while ((lead1decreasing == false && lead2decreasing == false) && ((time_index+1) < num_data_pts)){
				datapt1 = Integer.valueOf(lead1array[time_index]);
				datapt3 = Integer.valueOf(lead2array[time_index]);
				time_index++;
				datapt2 = Integer.valueOf(lead1array[time_index]);
				datapt4 = Integer.valueOf(lead2array[time_index]);
				if (datapt1 > datapt2){
					lead1decreasing = true;
				}
				else{
					lead1decreasing = false;
				}
				if (datapt3 > datapt4){
					lead2decreasing = true;
				}
				else{
					lead2decreasing = false;
				}
			}
			//set mark2
			mark2 = time_index;
			//watch for lead3 to spike low (below -1000)
			while (Integer.valueOf(lead3array[time_index]) > -1000  && ((time_index+1) < num_data_pts)){
				time_index++;
			}
			//then watch for lead3 to go above zero
			while (Integer.valueOf(lead3array[time_index]) <= 0 && ((time_index+1) < num_data_pts)){
				time_index++;
			}
			//set mark3
			mark3 = time_index;
			
			//watch for lead3 to be steady at zero
			while((Integer.valueOf(lead3array[time_index]) != 0 && Integer.valueOf(lead3array[time_index++]) != 0) && ((time_index+1) < num_data_pts));
			//set mark4
			mark4 = time_index;
		
			
			//calculate and average the intervals
			pulse_count++;
System.out.println("new pulse analyzed: "+pulse_count);
			pr_interval = (pr_interval*(pulse_count - 1) + (mark2 - mark1)*0.01 )/pulse_count;
			qrs_interval = (pr_interval*(pulse_count - 1) + (mark3 - mark2)*0.01)/pulse_count;
			qt_interval = (pr_interval*(pulse_count - 1) + (mark4 - mark2)*0.01)/pulse_count;
	 	
		}//continue and analyze next pulse
System.out.println("calculating BPM");
		//calculate the BPM to the nearest 10
		BPM  = (pulse_count / ( time_index/100)) * 60;
		BPM  = (int) (Math.round(((double)BPM)/10.0) * 10); //round() returns a long
		
	}//end analyzeReading()
}
