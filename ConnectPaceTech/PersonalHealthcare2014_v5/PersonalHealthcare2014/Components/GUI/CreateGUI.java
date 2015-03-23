import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Color;
import java.awt.FlowLayout;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.GroupLayout.Alignment;


import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextArea;
import javax.swing.*;
import org.omg.CORBA.portable.InputStream;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Choice;

public class CreateGUI extends JFrame
{
	
	Choice msglist;
	DefaultTableModel tablemodel1;
	JTable msgdetail2;
	DefaultTableModel tablemodel2;
	private JPanel contentPane;
	private JPanel panel_1;
	private JCheckBox chckbxDr;
	private JCheckBox chckbxHospital;
	private JCheckBox chckbxBloodPressure;
	private JCheckBox chckbxSpo;
	private JCheckBox chckbxEkg;
	private JCheckBox chckbxKinect;
	private JCheckBox chckbxTemp;
	private JPanel panel_2;
	private static JButton btnView;
	private JButton btnUpload;
	private JButton btnInput;
	private static JTextArea textArea;
	private static JTextField textAddr1;
	private static JTextField textAddr2;
	private int infoCount;
	private int receiveCount;
	private static KeyValueList spo2Rec;
	private static KeyValueList TempRec;
	private static KeyValueList bpRec;
	private String content = "";
	private boolean[] infoIdx; 

	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;

	private static String lname = "";
	private static String fname="";
	private static String spo2="";
	private static String Temp="";
	private static String time="";
	private static String systolic="";
	private static String diastolic="";
	private static String pulse="";
	private static String SPO2_enable;
	private static String BP_enable;
	private static boolean inputEnable;
	public static void main(String[] args) throws Exception
	{
		CreateGUI frame = new CreateGUI();
		frame.setVisible(true);
		universal = new Socket("127.0.0.1", 7999);

		mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name","GUI");
		mEncoder.sendMsg(msg23, universal.getOutputStream());
		
		
		KeyValueList kvList;
		outstream = universal.getOutputStream();
		
		while(true)
		{	
			kvList = mDecoder.getMsg();
			ProcessMsg(kvList);
		}	
	}
	/**
	 * Create the frame.
	 */
	public CreateGUI() {
		System.out.println("GUI initializing...");
		this.msglist = new Choice();
		this.msglist.addItem("TestMsg");
		this.msglist.select(0);
		this.tablemodel1 = new DefaultTableModel();
		this.tablemodel1.addColumn("Key");
		this.tablemodel1.addColumn("Value");
		this.tablemodel2 = new DefaultTableModel();
		this.tablemodel2.addColumn("Key");
		this.tablemodel2.addColumn("Value");
		this.infoCount = 0;
		this.receiveCount = 0;
		this.inputEnable = false;
		setTitle("SIS GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(10, 10));
		Font myfont = new Font(Font.DIALOG,Font.PLAIN,20);
   		Color mycolor = Color.BLACK;
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.WEST);
		panel.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		panel.setBorder(BorderFactory.createTitledBorder(null, "Informations", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION ,myfont,mycolor));
		
		textArea = new JTextArea(30,20);
		JScrollPane scrollPane = new JScrollPane( textArea);
		textArea.setEditable(false);
		contentPane.add(scrollPane, BorderLayout.NORTH);
		
		
		chckbxBloodPressure = new JCheckBox("BloodPressure");
		chckbxBloodPressure.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		chckbxBloodPressure.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxBloodPressure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(chckbxBloodPressure.isSelected()){
					infoCount++;
					btnView.setEnabled(true);
					btnInput.setEnabled(true);
				}
				else{
					systolic="";
					diastolic="";
					pulse="";
					infoCount--;
					if(infoCount==0){
						btnView.setEnabled(false);
						btnInput.setEnabled(false);
					}
				}
				System.out.println("info count: " + infoCount);
			}
		});
		panel.add(chckbxBloodPressure);
		
		chckbxSpo = new JCheckBox("SPO2");
		chckbxSpo.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		chckbxSpo.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxSpo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxSpo.isSelected()){
					infoCount++;
					btnView.setEnabled(true);
					btnInput.setEnabled(true);
				}
				else{
					spo2="";
					infoCount--;
					if(infoCount==0){
						btnView.setEnabled(false);
						btnInput.setEnabled(false);
					}						
				}
				System.out.println("info count: " + infoCount);
			}
		});
		panel.add(chckbxSpo);
		
		chckbxEkg = new JCheckBox("EKG");
		chckbxEkg.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		chckbxEkg.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxEkg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxEkg.isSelected()){
					infoCount++;
					btnView.setEnabled(true);
					btnInput.setEnabled(true);
				}
				else{
					infoCount--;
					if(infoCount==0){
						btnView.setEnabled(false);
						btnInput.setEnabled(false);
					}
				}
				System.out.println("info count: " + infoCount);
			}
		});
		panel.add(chckbxEkg);

		chckbxKinect = new JCheckBox("Kinect");
		chckbxKinect.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		chckbxKinect.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxKinect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxKinect.isSelected()){
					infoCount++;
					btnView.setEnabled(true);
					btnInput.setEnabled(true);
				}
				else{
					infoCount--;
					if(infoCount==0){
						btnView.setEnabled(false);
						btnInput.setEnabled(false);
					}
				}
				System.out.println("info count: " + infoCount);
			}
		});
		panel.add(chckbxKinect);

		chckbxTemp = new JCheckBox("Temp");
		chckbxTemp.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		chckbxTemp.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxTemp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxTemp.isSelected()){
					infoCount++;
					btnView.setEnabled(true);
					btnInput.setEnabled(true);
				}
				else{
					Temp="";
					infoCount--;
					if(infoCount==0){
						btnView.setEnabled(false);
						btnInput.setEnabled(false);
					}						
				}
				System.out.println("info count: " + infoCount);
			}
		});
		panel.add(chckbxTemp);
		

		panel_1 = new JPanel();
		GroupLayout layout1 = new GroupLayout(panel_1);
		// Turn on automatically adding gaps between components
   		layout1.setAutoCreateGaps(true);

   		// Turn on automatically creating gaps between components that touch
   		// the edge of the container and the container.
   		layout1.setAutoCreateContainerGaps(true);

		chckbxDr = new JCheckBox("Dr.");
		chckbxDr.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		chckbxDr.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(chckbxDr.isSelected()){
					CreateGUI.this.receiveCount++;
					CreateGUI.this.btnUpload.setEnabled(true);
				}
				else{
					CreateGUI.this.receiveCount--;
					if(CreateGUI.this.receiveCount==0)
						CreateGUI.this.btnUpload.setEnabled(false);
				}
			}
		});
		//panel_1.add(chckbxDr);
		chckbxDr.setHorizontalAlignment(SwingConstants.LEFT);

		chckbxHospital = new JCheckBox("Hospital");
		chckbxHospital.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		chckbxHospital.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(chckbxHospital.isSelected()){
					CreateGUI.this.receiveCount++;
					CreateGUI.this.btnUpload.setEnabled(true);
				}
				else{
					CreateGUI.this.receiveCount--;
					if(CreateGUI.this.receiveCount==0)
						CreateGUI.this.btnUpload.setEnabled(false);
				}
			}
		});
		chckbxHospital.setHorizontalAlignment(SwingConstants.LEFT);
		//panel_1.add(chckbxHospital);


		textAddr1 = new JTextField(20);
		textAddr1.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		textAddr1.setEditable(true);
		textAddr2 = new JTextField(20);
		textAddr2.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		textAddr2.setEditable(true);


		// Create a sequential group for the horizontal axis.
  		GroupLayout.SequentialGroup hGroup = layout1.createSequentialGroup();
  		hGroup.addGroup(layout1.createParallelGroup().addComponent(chckbxDr).addComponent(textAddr1));
   		hGroup.addGroup(layout1.createParallelGroup().addComponent(chckbxHospital).addComponent(textAddr2));
   		layout1.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout1.createSequentialGroup();
		vGroup.addGroup(layout1.createParallelGroup(Alignment.BASELINE).addComponent(chckbxDr).addComponent(textAddr1));
   		vGroup.addGroup(layout1.createParallelGroup(Alignment.BASELINE).addComponent(chckbxHospital).addComponent(textAddr2));
   		layout1.setVerticalGroup(vGroup);


		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		panel_1.setBorder(BorderFactory.createTitledBorder(null, "SendTo", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION ,myfont,mycolor));
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		panel_2 = new JPanel();
		panel_2.setFont(new Font(Font.DIALOG,Font.PLAIN,20));
		contentPane.add(panel_2, BorderLayout.EAST);
		panel_2.setBorder(BorderFactory.createTitledBorder(null, "Action", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION ,myfont,mycolor));
		
		btnView = new JButton("View");
		CreateGUI.this.btnView.setEnabled(false);
		CreateGUI.this.btnView.setFont(new Font(Font.DIALOG, Font.PLAIN,20) );
		btnView.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				textArea.setText(null);
				try{
					if(chckbxSpo.isSelected()){
						KeyValueList msgToSPO2 = new KeyValueList();
						msgToSPO2.addPair("MsgID", "1005");
						msgToSPO2.addPair("SPO2_enable","true");
						mEncoder.sendMsg(msgToSPO2, universal.getOutputStream());
					}
					if(chckbxBloodPressure.isSelected()){
						KeyValueList msgToBP = new KeyValueList();
						msgToBP.addPair("MsgID", "1007");
						msgToBP.addPair("BloodPressure_enable","true");
						mEncoder.sendMsg(msgToBP, universal.getOutputStream());
					}
					if(chckbxTemp.isSelected()){
						Global.checkTemp = 1;
						KeyValueList msgToTemp = new KeyValueList();
						msgToTemp.addPair("MsgID", "1019");
						msgToTemp.addPair("Temp_enable","true");
						mEncoder.sendMsg(msgToTemp, universal.getOutputStream());
					}else{
						Global.checkTemp = 0;
					}					
					if(chckbxEkg.isSelected()){
						System.out.println("EKG is seleceted.");
					}
				}
				catch (Exception localException) {}
			}
		});
		panel_2.add(btnView);
		
		btnUpload = new JButton("Upload");
		CreateGUI.this.btnUpload.setEnabled(false);
		CreateGUI.this.btnUpload.setFont(new Font(Font.DIALOG, Font.PLAIN,20) );
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("Click Upload Button");
				try{
					KeyValueList msgToUploader = new KeyValueList();
					msgToUploader.addPair("MsgID", "1009");
					msgToUploader.addPair("Description", "GUI to Uploader");
					if(chckbxSpo.isSelected()){
						// Update XML file.
						KeyValueList msgToSPO2 = new KeyValueList();
						msgToSPO2.addPair("MsgID", "1005");
						msgToSPO2.addPair("Description", "GUI to SPO2");
						msgToSPO2.addPair("SPO2_enable","true");

						mEncoder.sendMsg(msgToSPO2, universal.getOutputStream());

						// Send information to Uploader
						String tmplname = spo2Rec.getValue("LastName");
						String tmpfname = spo2Rec.getValue("FirstName");
						String tmpspo2 = spo2Rec.getValue("SPO2");
						String tmptime = spo2Rec.getValue("Date");
						msgToUploader.addPair("SPO2_enable", "true");
						msgToUploader.addPair("FirstName", tmpfname);
						msgToUploader.addPair("LastName", tmplname);
						msgToUploader.addPair("SPO2", tmpspo2);
					}
				
					if(chckbxBloodPressure.isSelected()){
						// Update XML file.
						KeyValueList msgToBP = new KeyValueList();
						msgToBP.addPair("MsgID", "1007");
						msgToBP.addPair("Description", "GUI to BloodPressure");
						msgToBP.addPair("BloodPressure_enable","true");
						mEncoder.sendMsg(msgToBP, universal.getOutputStream());

						// Send information to Uploader
						String tmplname = bpRec.getValue("LastName");
						String tmpfname = bpRec.getValue("FirstName");
						String tmpsystolic = bpRec.getValue("Systolic");
						String tmpdiastolic = bpRec.getValue("Diastolic");
						String tmppulse = bpRec.getValue("Pulse");
						String tmptime = bpRec.getValue("Date");
						msgToUploader.addPair("BloodPressure_enable", "true");
						msgToUploader.addPair("FirstName", tmpfname);
						msgToUploader.addPair("LastName", tmplname);
						msgToUploader.addPair("Systolic", tmpsystolic);
						msgToUploader.addPair("Diastolic", tmpdiastolic);
						msgToUploader.addPair("Pulse", tmppulse);
						
					}
					// TODO: Needed to implemented
					if(chckbxEkg.isSelected()){
						// Update XML file.
						// Send information to Uploader
					}
					String addr = "";
					if(chckbxDr.isSelected()){
						addr = textAddr1.getText();
					}
					if(chckbxHospital.isSelected()){
						if(chckbxDr.isSelected()){
							addr = addr +"," +textAddr2.getText();
						}
						else
							addr = textAddr2.getText();
					}
					msgToUploader.addPair("Email",addr);
					mEncoder.sendMsg(msgToUploader, universal.getOutputStream());
					
				}
				catch (Exception localException) {}
			}
		});
		btnUpload.setHorizontalAlignment(SwingConstants.LEFT);
		panel_2.add(btnUpload);
		
		btnInput = new JButton("Input");
		CreateGUI.this.btnInput.setEnabled(false);
		CreateGUI.this.btnInput.setFont(new Font(Font.DIALOG, Font.PLAIN,20) );
		btnInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					KeyValueList msgToInput = new KeyValueList();
					msgToInput.addPair("MsgID", "1013");
					if(inputEnable==true){
						textArea.setText("Stop collecting information...");
						System.out.println("Stop from GUI");
						btnInput.setText("Input");
						msgToInput.addPair("Enable", "False");
						inputEnable = false;
					}
					else{
						textArea.setText("Start collecting information...");
						System.out.println("Input from GUI");
						btnInput.setText("Stop");
						msgToInput.addPair("Enable","True");
						if(chckbxBloodPressure.isSelected())
							msgToInput.addPair("EnableBP","True");
						if(chckbxSpo.isSelected())
							msgToInput.addPair("EnableSPO","True");
						if(chckbxTemp.isSelected())
							msgToInput.addPair("EnableTemp","True");
						if(chckbxEkg.isSelected())
							msgToInput.addPair("EnableEKG","True");
						if(chckbxKinect.isSelected())
							msgToInput.addPair("EnableKinect","True");
						inputEnable = true;
					}
					msgToInput.addPair("Description", "GUI to InputProcessor");
					mEncoder.sendMsg(msgToInput, universal.getOutputStream());
				}
				catch(Exception localException){}				
			}
		});
		panel_2.add(btnInput);
		

		pack();
	}
	static void ProcessMsg(KeyValueList kvList) throws Exception
	{
		int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
		Font font = new Font(Font.DIALOG, Font.PLAIN,20);
		String SPO2_enable = kvList.getValue("SPO2_enable");
		String BP_enable = kvList.getValue("BP_enable");
		String Temp_enable = kvList.getValue("Temp_enable");		
		lname = kvList.getValue("LastName");
		fname = kvList.getValue("FirstName");
		time = kvList.getValue("Date");
		String result;
		
		switch(MsgID)
		{
		/****************************************************
		Below are the main part of the component program. All received msgs are encoded as a KeyValueList kvList.
		kvList is a vector of <String key, String value> pairs. The 5 main methods of KeyValueList are 
			int size()                                    to get the size of KeyValueList
			String getValue(String key)                   to get value given key
			void addPair(String key, String value)        to add <Key, Value> pair to KeyValueList
			void setValue(String key, String value)       to set value to specific key
			String toString()                             System.out.print(KeyValueList) could work
		The following code can be used to new and send a KeyValueList msg to SISServer
			KeyValueList msg = new KeyValueList();
			msg.addPair("MsgID","23");
			msg.addPair("Description","Connect to SISServer");
			msg.addPair("Attribute","Value");
			... ...
			mEncoder.sendMsg(msg, universal.getOutputStream()); //This line sends the msg
		NOTE: Always check whether all the attributes of a msg are in the KVList before sending it.
		Don't forget to send a msg after processing an incoming msg if necessary.
		All msgs must have the following 2 attributes: MsgID and Description.
		Below are the sending messages' attributes list:
			MsgName: GUIToSPO2	MsgID: 1005	Attrs:
			MsgName: GUIToBP	MsgID: 1007	Attrs:
			MsgName: GUIToUploader	MsgID: 1009	Attrs:
			MsgName: GUIToEKG	MsgID: 1011	Attrs:
			MsgName: GUIToKinect	MsgID: 1014	Attrs:
			MsgName: GUIToInput	MsgID: 1013	Attrs:
			MsgName: GUIToTemp	MsgID: 1019	Attrs:
		For more information about KeyValueList, read comments in Util.java.
		****************************************************/
		case 1004:
			System.out.println("Message MsgName:SPO2ToGUI MsgID:1004 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:SPO2ToGUI MsgID:1004
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			System.out.println("Message MsgName:SPO2ToGUI MsgID:1004 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:SPO2ToGUI MsgID:1004
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			spo2Rec = kvList;
			spo2 = kvList.getValue("SPO2");
		    time = kvList.getValue("Date");
			System.out.println("Show SPO2");
			//String desc = kvList.getValue("Description");
			//textArea.setText(desc);
			//content = content 
			//	+"\nSPO2: " + spo2;
			///String result;
			result = "\nLastName: "+lname+
				"\nFirstName: "+fname+
				"\nDate: "+time+
				"\nSPO2: "+spo2+
				"\nSystolic: "+systolic+
				"\nDiatolic: "+diastolic+
				"\nPulse: "+pulse;
			//Font font = new Font(Font.DIALOG, Font.PLAIN,20);
			textArea.setFont(font);
			textArea.setText(result); 
			break;
		case 1006:
			System.out.println("Message MsgName:BPToGUI MsgID:1006 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:BPToGUI MsgID:1006
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			bpRec = kvList;
			systolic = kvList.getValue("Systolic");
			diastolic = kvList.getValue("Diastolic");
			pulse = kvList.getValue("Pulse");
			System.out.println("Show BP");
			//String desc = kvList.getValue("Description");
			//textArea.setText(desc);
			//String result;
			result = "\nLastName: "+lname+
				"\nFirstName: "+fname+
				"\nDate: "+time+
				"\nSPO2: "+spo2+
				"\nSystolic: "+systolic+
				"\nDiatolic: "+diastolic+
				"\nPulse: "+pulse;
			//Font font = new Font(Font.DIALOG, Font.PLAIN,20);
			textArea.setFont(font);
			textArea.setText(result); 


			break;
		case 1008:
			System.out.println("Message MsgName:UploaderToGUI MsgID:1008 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:UploaderToGUI MsgID:1008
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			bpRec = kvList;
			systolic = kvList.getValue("Systolic");
			diastolic = kvList.getValue("Diastolic");
			pulse = kvList.getValue("Pulse");
			System.out.println("Show BP");
			//String desc = kvList.getValue("Description");
			//textArea.setText(desc);
			//String result;
			result = "\nLastName: "+lname+
				"\nFirstName: "+fname+
				"\nDate: "+time+
				"\nSPO2: "+spo2+
				"\nSystolic: "+systolic+
				"\nDiatolic: "+diastolic+
				"\nPulse: "+pulse;
			//Font font = new Font(Font.DIALOG, Font.PLAIN,20);
			textArea.setFont(font);
			textArea.setText(result); 


			break;
		case 1010:
			System.out.println("Message MsgName:EKGToGUI MsgID:1010 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:EKGToGUI MsgID:1010
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/



			break;
		case 1016:
			System.out.println("Message MsgName:KinectToGUI MsgID:1016 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:KinectToGUI MsgID:1016
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/



			break;
		case 1012:
			System.out.println("Message MsgName:InputToGUI MsgID:1012 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:InputToGUI MsgID:1012
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/



			break;
		case 1018:
			System.out.println("Message MsgName:TempToGUI MsgID:1018 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:TempToGUI MsgID:1018
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			if(Global.checkTemp == 1){
			if(textArea.getLineCount() > 15){
				textArea.setText(null);
			}
			TempRec = kvList;
			Temp = kvList.getValue("Temp");
		    time = kvList.getValue("Date");
			System.out.println("Show Temp");
			result = "\nTime: "+time+
				"    Temperature: "+Temp;
			textArea.setFont(font);
			textArea.append(result); 
			}
			break;
		/*************************************************
		Below are system messages. No modification required.
		*************************************************/
		case 26:
			System.out.println("Connect to SISServer successful.");
			break;
		case 22:
			System.exit(0);
			break;
		case 24:
			System.out.println("Algorithm Activated");
			break;
		case 25:
			System.out.println("Algorithm Deactivated");
			break;
		default:
			break;
		}
	}
}

class Global{
	public static int checkTemp = -1;
	public static int checkBP = -1;
	public static int checkSPO2 = -1;
	public static int checkKinect = -1;
	public static int checkEKG = -1;
	public static String buffer;
}