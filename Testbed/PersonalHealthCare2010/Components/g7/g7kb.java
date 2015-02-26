/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.*;
import java.net.*;
import java.util.*;

public class g7kb implements ComponentBase
{
	int aggregation_interval;
	int pulse_rate;
	double std_dev_time_between;
	double avg_qt_interval;
	double avg_pr_interval;
	String time;
	String lead1;
	String lead2;
	String lead3;
	Socket server;
    MsgEncoder mEncoder;
    MsgDecoder mDecoder;
    boolean activated;


	//private Socket client;

    g7kb(String serveraddress)
	{
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
            msg.addPair("Name", "g7kb");
            mEncoder.sendMsg(msg, server.getOutputStream());

            //loop waiting for input
            while (true){
                    KeyValueList kvInput = mDecoder.getMsg();
                    if (kvInput != null){
                        System.out.println("kb received message "+ kvInput.getValue("MsgID"));
                        processMsg(kvInput);
                    }

              
                Thread.sleep(50);
            }

	} catch (Exception e) {e.printStackTrace();}
    }



    public static void main(String args[]){
		g7kb app = new g7kb(args[0]);
	}//end main

    synchronized public void processMsg(KeyValueList kvList) throws Exception
	{
    	int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
System.out.println("kb processing "+MsgID);
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
				activated = true;
        		break;
        	}



	 	    case(170):{//ekg kb query
	 	    	if (activated){
	 	    		//get info
	 	    		Double qrs = Double.valueOf(kvList.getValue("AvgQrsInterval"));
	 	    		Double pr = Double.valueOf(kvList.getValue("AvgQrsInterval"));
	 	    		Double qt = Double.valueOf(kvList.getValue("AvgQtInterval"));
	 	    		int BPM = Integer.valueOf(kvList.getValue("BPM"));	    		
	 	    		StringBuffer diagnosis = new StringBuffer("");
	 	    		StringBuffer suggestions = new StringBuffer("");
	 	    		
	 	    		//determine if bad
	 	    		boolean badresult = false;
	 
	 	    		String time = kvList.getValue("DateTime");	    		
	 	    		
	 	    		//look at pr
	 	    		if (pr < 0.12){
	 	    			//short pr
	 	    			diagnosis.append("Hypermagnesemia, Lown-Ganong-Levine, or PW (Wolff-Parkinson-White) Syndrome.  ");
	 	    			suggestions.append("See doctor for shortened PR Interval.  ");
	 	    			badresult = true;
	 	    		}
	 	    		else if (pr > 0.20){
	 	    			//prolonged pr
	 	    			diagnosis.append("Slowed conduction in AV node.  ");
	 	    			suggestions.append("See doctor for Slowed conduction in bundle branch.  ");
	 	    			badresult = true;
	 	    		}
	 	    		
	 	    		//look at qrs
	 	    		if (qrs > .1 && qrs < .12 ){
	 	    			//semi-long qrs
	 	    			diagnosis.append("Incomplete right or left bundle branch block, Nonspecific intraventricular conduction delay, or left anterior or posterior fascicular block.  ");
	 	    			suggestions.append("See doctor for prolonged QRS Interval.  ");
	 	    			badresult = true;
	 	    		}
	 	    		else if (qrs > .12){
	 	    			//prolonged qrs
	 	    			diagnosis.append("Complete RBBB or LBBB. Nonspecific IVCD, Ectopic rhythms originating in the ventricle.  ");
	 	    			suggestions.append("See doctor for prolonged QRS Interval.  ");
	 	    			badresult = true;
	 	    		}
	 	    		
	 	    		//look at qt
	 	    		//need to calc a reference via bpm
	 	    		double refpt = .4 + ((70-BPM)*.2);
	 	    		if (qt > refpt){
	 	    			//prolonged qt
	 	    			diagnosis.append("Hypocalcemia.  ");
	 	    			suggestions.append("See doctor for prolonged QT Interval.  ");			
	 	    			badresult = true;
	 	    		}
	 	    		
	 	    		//send result
	 	    		msg = new KeyValueList();

	 	    		if (badresult==true){
	 	    			msg.addPair("EkgStatus", "bad");
	 	    			System.out.println("kb sending bad result");
	 	    		}
	 	    		else{
	 	    			msg.addPair("EkgStatus", "ok");
	 	    		}
	 	    		msg.addPair("MsgID","171");
	 	    		msg.addPair("Description", "EKG Query Result");
	 	    		msg.addPair("DateTime", time);
	 	    			
	 	    		msg.addPair("Diagnosis", diagnosis.toString());
	 	    		msg.addPair("Suggestions", suggestions.toString());
		    	
                MsgEncoder mEncoder= new MsgEncoder();
				try {
					mEncoder.sendMsg(msg, server.getOutputStream());
				} catch (IOException e) {e.printStackTrace();}

	 	    	}//end if activated
		        break;
	 	    }
		}
    }
}

