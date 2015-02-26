/********************************************************
**Authors: John Harley, Ryan Brashear                  **
**Project: g1GUI.java                				   **
**Modified: 4-7-10          						   **
**Class: CS1631               						   **
**Due: 4-13-10               						   **
********************************************************/
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class g1GUI extends NewJFrame 
{
    Socket universal;
    JFrame frame;
    JFrame frame1;
    JOptionPane pane;
    int n = 0;
    public static String userName;
    public static String Age;
    public static String Height;
    public static String Weight;
    public static String Diabetes;
    public static String heartDisease;
    public static String Meal;
    public static String Sex;
    String userName2;
    String Age2;
    String Height2;
    String Weight2;
    String Diabetes2;
    String heartDisease2;
    String Meal2;
    String Sex2;
    String time;
   
	g1GUI(String servaddr)
	{
		
		try{
		universal = new Socket(servaddr, 7999);

		MsgEncoder mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList kvlResult = new KeyValueList();
		kvlResult.addPair("MsgID","23");
		kvlResult.addPair("Description", "Connect to Server");
		kvlResult.addPair("SecurityLevel", "3");
		kvlResult.addPair("Name", "g1GUI");
        mEncoder.sendMsg(kvlResult, universal.getOutputStream());
        
        MsgEncoder mgEncoder = new MsgEncoder();
        KeyValueList kvgResult = new KeyValueList();
        
        NewJFrame frame = new NewJFrame();
        frame.setVisible(true);
        while(NewJFrame.userName.equals("") || NewJFrame.Age.equals("") || NewJFrame.Sex.equals("") || NewJFrame.heartDisease.equals("") || NewJFrame.Meal.equals("") || NewJFrame.Diabetes.equals("") || NewJFrame.Weight.equals("") || NewJFrame.Height.equals(""))
        {
        
        }  
        time = Long.valueOf((new Date()).getTime()).toString();
        
     
		kvgResult.addPair("MsgID","45");
		kvgResult.addPair("Description", "User Profile");
		kvgResult.addPair("UserName", NewJFrame.userName);
		kvgResult.addPair("Sex", NewJFrame.Sex);
		kvgResult.addPair("Age", NewJFrame.Age);
		kvgResult.addPair("Weight", NewJFrame.Weight);
		kvgResult.addPair("Height", NewJFrame.Height);
		kvgResult.addPair("Diabetes", NewJFrame.Diabetes);
		kvgResult.addPair("HeartDisease", NewJFrame.heartDisease);
		kvgResult.addPair("Meal", NewJFrame.Meal);
		kvgResult.addPair("DateTime", time);
        mgEncoder.sendMsg(kvgResult, universal.getOutputStream());
        userName2 = NewJFrame.userName;
        Age2 = NewJFrame.Age;
        Height2 = NewJFrame.Height;
        Weight2 = NewJFrame.Weight;
        Diabetes2 = NewJFrame.Diabetes;
        heartDisease2 = NewJFrame.heartDisease;
        Meal2 = NewJFrame.Meal;
        Sex2 = NewJFrame.Sex;
  
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
							System.out.println("**************Received Msg**************");
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
		}catch(Exception ex)
		{System.out.println("Exception reading from standard input..");}
	    
		try{
		while (true)
		{
	        if (!NewJFrame.userName.equals(userName2) || !NewJFrame.Age.equals(Age2) || !NewJFrame.Sex.equals(Sex2)|| !NewJFrame.Weight.equals(Weight2)|| !NewJFrame.Height.equals(Height2)|| !NewJFrame.heartDisease.equals(heartDisease2)|| !NewJFrame.Diabetes.equals(Diabetes2)|| !NewJFrame.Meal.equals(Meal2) )
			{
			    MsgEncoder mgEncoder = new MsgEncoder();
		        KeyValueList kResult = new KeyValueList();
		        time = Long.valueOf((new Date()).getTime()).toString();
		        kResult.addPair("MsgID","45");
				kResult.addPair("Description", "User Profile");
				kResult.addPair("UserName", NewJFrame.userName);
				kResult.addPair("Sex", NewJFrame.Sex);
				kResult.addPair("Age", NewJFrame.Age);
				kResult.addPair("Weight", NewJFrame.Weight);
				kResult.addPair("Height", NewJFrame.Height);
				kResult.addPair("Diabetes", NewJFrame.Diabetes);
				kResult.addPair("HeartDisease", NewJFrame.heartDisease);
				kResult.addPair("Meal", NewJFrame.Meal);
				kResult.addPair("DateTime", time);
		        mgEncoder.sendMsg(kResult, universal.getOutputStream());
		        userName2 = NewJFrame.userName;
		        Age2 = NewJFrame.Age;
		        Height2 = NewJFrame.Height;
		        Weight2 = NewJFrame.Weight;
		        Diabetes2 = NewJFrame.Diabetes;
	    	    heartDisease2 = NewJFrame.heartDisease;
		        Meal2 = NewJFrame.Meal;
			    Sex2 = NewJFrame.Sex;
		    }
		}
			
		}catch(Exception ex)
		{ System.out.println("Exception reading from standard input..");}
	}
	
	
	
	public static void main(String args[])
	{
		g1GUI app = new g1GUI(args[0]);
	}
	
	

	public void processMsg(KeyValueList kvList) throws Exception
	{
    	int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
    	ArrayList<Socket> sendlist;
    	ArrayList<String> inmsgs, outmsgs;
    	CompMsgControl tempcmc;
    	String tempstr;
    	String result1;
    	String result2;
    	String result3;
    	String result4;
    	String result5;
    	String result6;
    	String result7;
    	String result8;
    	String result9;
    	String result10;

    	switch(MsgID)
		{
        case 31:
			
			result1 = kvList.getValue("Systolic");
			result2 = kvList.getValue("Diastolic");
			result3 = kvList.getValue("Pulse");
			try{
			NewJFrame.systolic.setText(result1);
			NewJFrame.diastolic.setText(result2);
			NewJFrame.pulse.setText(result3);
			}
			catch(Exception ex)
			{ System.out.println("Exception reading BloodPressure..");}
		break;
		case 32:
			/*
			result1 = kvList.getValue("Systolic");
			result2 = kvList.getValue("Diastolic");
			result3 = kvList.getValue("Pulse");
			result4 = kvList.getValue("Alert Type");
			result5 = kvList.getValue("Name");
			result6 = kvList.getValue("DateTime");
			*/
			JFrame frame6 = new JFrame();
			JOptionPane.showMessageDialog(frame6,"Blood Pressure is Abnormal!",null,JOptionPane.WARNING_MESSAGE);

		break;
		case 33:
			result1 = kvList.getValue("SPO2");
			NewJFrame.spo2.setText(result1);
		break;
		case 34:
			/*
			result1 = kvList.getValue("SPO2");
	        result2 = kvList.getValue("Alert Type");
			result3 = kvList.getValue("Name");
			result4 = kvList.getValue("DateTime");
            */
			JFrame frame3 = new JFrame();
			JOptionPane.showMessageDialog(frame3,"SPO2 Levels Are Too Low!","Low Warning",JOptionPane.WARNING_MESSAGE);
		break;
		case 36:
			//result1 = kvList.getValue("Alert Type");
			//result2 = kvList.getValue("Name");
			//result3 = kvList.getValue("DateTime");
			JFrame frame4 = new JFrame();
			JOptionPane.showMessageDialog(frame4,"EKG Alert!",null,JOptionPane.WARNING_MESSAGE);

		break;
		case 38:
			/*
			result1 = kvList.getValue("Systolic");
			result2 = kvList.getValue("Diastolic");
			result3 = kvList.getValue("Pulse");
			result4 = kvList.getValue("DateTimeBP");
			result5 = kvList.getValue("SPO2");
			result6 = kvList.getValue("DateTimeSPO2");
			result7 = kvList.getValue("DateTimeEKG");
			result8 = kvList.getValue("Alert Type");
			result9 = kvList.getValue("Name");
			result10 = kvList.getValue("DateTime");
			*/
			JFrame frame5 = new JFrame();
			JOptionPane.showMessageDialog(frame5,"Emergency!",null,JOptionPane.WARNING_MESSAGE);
		break;
        case 41:
			result1 = kvList.getValue("Blood Sugar");
			try{
			NewJFrame.bloodSugar.setText(result1);
			}
			catch(Exception ex)
			{ System.out.println("Exception reading blood sugar..");}
		break;
        case 42:
			JFrame frame7 = new JFrame();
			JOptionPane.showMessageDialog(frame7,"Blood Sugar Alert!",null,JOptionPane.WARNING_MESSAGE);
		break;
		}
    }

	
}