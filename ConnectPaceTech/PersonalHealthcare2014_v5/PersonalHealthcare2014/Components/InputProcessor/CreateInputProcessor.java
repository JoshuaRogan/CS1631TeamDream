import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;
public class CreateInputProcessor extends AbstractSISComponent
{
	private String portName; /* serial port name, e.g. "COM1", "COM4" */
	private SerialReader reader;

	private SerialPort serialPort;
	private InputStream inStr;
	private boolean enableFlag;

	// Enable flag for input processor
	static private boolean enableSPO2;
	static private boolean enableBP;
	static private boolean enableEKG;
	static private boolean enableKinect;
	
	
	
	
	public CreateInputProcessor(String port) {
		portName = port;
	}

	@Override
	public String getName() {
		return "InputProcessor";
	}

	@Override
	public void initialize() throws SISException {
		super.initialize();

		/* Set up connection with device */
		CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
			System.out.println("Listening on " + portName + " ......");
		} catch (NoSuchPortException e) {
			throw new SISException("No such port", e);
		}

		if (portIdentifier.isCurrentlyOwned())
			throw new SISException("Error: Port is currently in use");

		/* Open the port */
		CommPort commPort;
		try {
			commPort = portIdentifier.open(this.getClass().getName(), 2000);
		} catch (PortInUseException e) {
			throw new SISException("Port is currently in use", e);
		}

		if (commPort instanceof SerialPort) {
			serialPort = (SerialPort) commPort;
			// Set serial parameters
			try {
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			} catch (UnsupportedCommOperationException e) {
				throw new SISException(e);
			}

			// get input stream
			try {
				//[duncan] hard code here//////////
//				FileInputStream fis= new FileInputStream("C:\\Users\\admin.sis\\Desktop\\EKG30sec60bpm1mv.bin");
	//			inStr=fis;
				///////////////////////////////////
				inStr = serialPort.getInputStream();
			} catch (IOException e) {
				throw new SISException(e);
			}

			// create reader
			reader = new SerialReader(inStr);
		} else {
			throw new SISException(
					"Only serial ports are handled by this example.");
		}
	}

	@Override
	public void onActivation() {
		while (reader == null)
			continue;
		
		
		// ??!! Comment below because the unknown exception.
		// Start the serial reader
		//reader.start();

	}

	@Override
	public void onDeactivation() {
		// stop reading from device
		reader.stopReading();
	}

	@Override
	public void shutdown() throws SISException {
		super.shutdown();
		reader.stopReading();
		serialPort.close();
	}

	/* Serial data state */
	public enum Data_State {
		ST_IDLE, ST_PT_FLAG, ST_PT_DATA, ST_ECG_STATUS, ST_ECG_DATA
	}

	private class SerialReader extends Thread {
		InputStream in;
		VitalData oldVd;
		Data_State state = Data_State.ST_IDLE;
		int PT_BUFSIZE = 92; /* PT sequence size */
		StringBuffer pt_str = new StringBuffer(); /* PT data buffer */

		List<Byte> ecg_buf = new ArrayList<Byte>(); /* ECG data buffer */
		int ecg_seq = 0;

		ECGData ecgData = new ECGData();

		boolean sync;
		boolean alive = false;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public void stopReading() {
			alive = false;
		}

		private void sendVitalData(VitalData vd) {
			String lname = "Chang";
			String fname = "Shi-Kuo";
			String now = Utilities.now();
			//String path = "PersonalHealthcare2014\\xml\\DataXML\\data_reading.xml";
			// Send blood pressure data
			if (vd.isValidBpress() && !vd.equalBpress(oldVd)) {
				SISMessage bpMsg = new SISMessage();
				bpMsg.addAttr("MsgID", "1012");
				bpMsg.addAttr("Systolic", String.valueOf(vd.getSystolic()));
				bpMsg.addAttr("Diastolic", String.valueOf(vd.getDiastolic()));
				bpMsg.addAttr("Pulse", String.valueOf(vd.getPulse()));
				bpMsg.addAttr("DateTime", now);
				sendMsgToServer(bpMsg);
				sendECGData();

				System.out.println("blood pressure added!!");
				String xml = "<?xml version=\"1.0\" standalone=\"yes\"?>\n"
						+ "<Msg>\n"
						+ "<Head><MsgID>31</MsgID><Description>BloodPressure Reading</Description></Head>\n"
						+ "<Body><Item><Key>Systolic</Key><Value>"+String.valueOf(vd.getSystolic()) + "</Value></Item>"
							+"<Item><Key>Diastolic</Key><Value>" + String.valueOf(vd.getDiastolic()) + "</Value></Item>"
							+"<Item><Key>Pulse</Key><Value>"+String.valueOf(vd.getPulse())+"</Value></Item><Item><Key>DateTime</Key><Value>"
						+ now + "</Value></Item><Item><Key>LastName</Key><Value>"
						+ lname + "</Value></Item><Item><Key>FirstName</Key><Value>"
						+ fname + "</Value></Item></Body>\n" + "</Msg>\n";

				String path = "C:\\Users\\Josh\\OneDrive\\Git\\CS1631TeamDream\\ConnectPaceTech\\PersonalHealthcare2014_v4\\PersonalHealthcare2014\\xml\\DataXML\\BloodPressure_reading.xml";

				// System.out.println(xml + path);
				try {
					System.out.println("Update BP!");
					updateXMLFile(path, xml);
				} catch (IOException e) {
					System.out.println("Error when updating XML file!\n");
					e.printStackTrace();
				}
			}
			// Send SpO2 data
			if (vd.isValidSPO2() && !vd.equalSpo2(oldVd)) {
				SISMessage spo2Msg = new SISMessage();
				spo2Msg.addAttr("MsgID", "1012");
				String spo2Val = String.valueOf(vd.getSpo2());
				spo2Msg.addAttr("SPO2", spo2Val);
				spo2Msg.addAttr("DateTime", now);
				spo2Msg.addAttr("LastName", lname);
				spo2Msg.addAttr("FirstName", fname);
				sendMsgToServer(spo2Msg);
				System.out.println("SPO2 added!!");
				String xml = "<?xml version=\"1.0\" standalone=\"yes\"?>\n"
						+ "<Msg>\n"
						+ "<Head><MsgID>33</MsgID><Description>SPO2 Reading</Description></Head>\n"
						+ "<Body><Item><Key>SPO2</Key><Value>" + spo2Val
						+ "</Value></Item><Item><Key>DateTime</Key><Value>"
						+ now + "</Value></Item><Item><Key>LastName</Key><Value>"
						+ lname + "</Value></Item><Item><Key>FirstName</Key><Value>"
						+ fname + "</Value></Item></Body>\n" + "</Msg>\n";

				String path = "C:\\Users\\Josh\\OneDrive\\Git\\CS1631TeamDream\\ConnectPaceTech\\PersonalHealthcare2014_v4\\PersonalHealthcare2014\\xml\\DataXML\\BloodPressure_reading.xml";

				// System.out.println(xml + path);
				try {
					System.out.println("Update SPO2!");
					updateXMLFile(path, xml);
				} catch (IOException e) {
					System.out.println("Error when updating XML file!\n");
					e.printStackTrace();
				}
			}
			/*xml = xml + "<Item><Key>DateTime</Key><Value>"
						+ now + "</Value></Item><Item><Key>LastName</Key><Value>"
						+ lname + "</Value></Item><Item><Key>FirstName</Key><Value>"
						+ fname + "</Value></Item></Body>\n" + "</Msg>\n";
			try {
				updateXMLFile(path, xml);
			} catch (IOException e) {
				System.out.println("Error when updating XML file!\n");
				e.printStackTrace();
			}*/
			//System.out.println("Updated file!!");
		}

		// update XML file for each component
		public void updateXMLFile(String path, String xml) throws IOException {
			java.io.FileWriter fw = new java.io.FileWriter(path);
			fw.write(xml);
			fw.close();
		}

		/**
		 * Send EKG data
		 */
		private void sendECGData() {
			SISMessage ecgMsg = new SISMessage();
			ecgMsg.addAttr("MsgID", "35");
			ecgMsg.addAttr("LeadI", ecgData.getLeadIStr());
			ecgMsg.addAttr("LeadII", ecgData.getLeadIIStr());
			ecgMsg.addAttr("LeadIII", ecgData.getLeadVStr());
			ecgMsg.addAttr("DateTime", Utilities.now());
			sendMsgToServer(ecgMsg);
			// clear ecgdata
			ecgData.clear();
		}

		public void run() {
			alive = true;
			System.out.println("RUN!!");
			byte[] buf = new byte[256];
//			char[] buf = new char[256];
			int n = -1;
			try {
				int count=0;
				
				while ((n = this.in.read(buf)) > -1) {
//				while ((n = this.in.read(buf,0,256)) > -1) {
					//System.out.println("fetching...");
					//System.out.println("n="+n);
					if (!alive)
						break;
					
					for (int i = 0; i < n; i++) {
						//System.out.println("looping...");
						//System.out.println(state);
						
						switch (state) {
						case ST_IDLE:
							switch (buf[i]) {
							case (byte) 128: // 0x80, Waveform message starts
								ecg_buf.add(buf[i]);
								//System.out.println(buf[i]);
								state = Data_State.ST_ECG_STATUS;
								
								break;

							case 'P'://never enter?[duncan]
								pt_str.append((char) buf[i]);
								//System.out.print("P "+(char) buf[i]);
								state = Data_State.ST_PT_FLAG;
								break;

							case 'T'://never enter?[duncan]
								sync = false;
								pt_str.append((char) buf[i]);
								//System.out.println("T "+(char) buf[i]);
								state = Data_State.ST_PT_DATA;
								break;
							default:
								//System.out.println("default "+buf[i]);
								sync = false;
								break;
							} // switch(buf[i])

							break; // case ST_IDLE

						case ST_ECG_STATUS: /* ECG status byte */
							//System.out.println(buf[i]);
							if ((buf[i] & 0xf0) > 0) {
								state = Data_State.ST_IDLE;
								//break;[duncan] delete
							}

							if (sync) {
								if ((buf[i] & ECGMessage.SEQ_MASK) != ecg_seq) {
									sync = false;
									state = Data_State.ST_IDLE;
									break;
								}
							} else {
								if ((buf[i] & ECGMessage.SEQ_MASK) == ecg_seq) {
									sync = true;
								}
							}

							ecg_seq = (buf[i] & ECGMessage.SEQ_MASK);
							ecg_seq += 1;
							ecg_seq &= ECGMessage.SEQ_MASK;
							ecg_buf.add(buf[i]);
							state = Data_State.ST_ECG_DATA;
							break;

						case ST_ECG_DATA:
	//						byte b = (byte) (buf[i] & 0xFF);
//							ecg_buf.add(b);
							ecg_buf.add(buf[i]);
							//System.out.println(buf[i] & 0xFF);
							if (ecg_buf.size() >= ECGMessage.ECG_MSGSIZE) {
								if (sync) {
									// A ECG message is ready
									ECGMessage ecgMsg = new ECGMessage(ecg_buf);

									 //System.out.println("ecgMsg "+ecgMsg.getLeadII());
									// if (ecgMsg.isValid()) {
									if (ecgData.getNumMessages() < 10)
										ecgData.addECGMessage(ecgMsg);
									// }
								}

								// if (ecgData.getNumMessages() >= 1000)
								// sendECGData();

								ecg_buf.clear();
								state = Data_State.ST_IDLE;
							}

							break;

						case ST_PT_FLAG:
							//if((buf[i]!=-128)&&(buf[i]!=0))
							//System.out.println(buf[i]);
							if (buf[i] == 'T') {
								pt_str.append((char) buf[i]);
								//index = ecg_buf.indexOf(buf[i]);
								//System.out.println(ecg_buf.get(index));
								state = Data_State.ST_PT_DATA;
							} else {
								sync = false;
								if (buf[i] == 128) {
									state = Data_State.ST_ECG_STATUS;
									break;
								}
								state = Data_State.ST_IDLE;
								break;
							}

							break;
						case ST_PT_DATA:
							//if((buf[i]!=-128)&&(buf[i]!=0))
							//System.out.println(buf[i]);
							if (buf[i] == 128) {
								sync = false;
								state = Data_State.ST_ECG_STATUS;
								break;
							}

							if (!((buf[i] == 32) || ((buf[i] >= 48) && (buf[i] <= 57)))) {
								sync = false;
								state = Data_State.ST_IDLE;
								break;
							}

							pt_str.append((char) buf[i]);
							//index = ecg_buf.indexOf(buf[i]);
							//System.out.println(ecg_buf.get(index));
							if (pt_str.length() >= PT_BUFSIZE) {
								sync = true;

								// A complete PT string is ready
								VitalData vd = new VitalData(pt_str.toString());

								// print out on console
								//System.out.println("vd "+vd);

								// Send to the SIS server
								sendVitalData(vd);

								oldVd = vd;

								pt_str.delete(0, pt_str.length());

								state = Data_State.ST_IDLE;
							}

							break;

						default:
							sync = false;
							state = Data_State.ST_IDLE;
							break;
						}
					} // for
					/*count++;
					if(count>100){
						for(int i=0;i<ecg_buf.size();i++)
							System.out.println(ecg_buf.get(i));
						break;
					}*/
					//KeyValueList kvList = mDecoder.getMsg();
					//System.out.println("get msg");
					//KeyValueList kvList = mDecoder.getMsg();
					//ProcessMsg(kvList);
					//System.out.println("out of process msg");
				} // while
			} // try
			catch (IOException e) {
				e.printStackTrace();
			}
			catch(Exception localException){System.out.println("NONONONONO");}
		}
	}

	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;
	static CreateInputProcessor input;
	static MsgDecoder mDecoder;
	//static KeyValueList kvList;
	public static void main(String[] args) throws Exception
	{
		String port = "COM3";
		universal = new Socket("127.0.0.1", 7999);
		input = new CreateInputProcessor(port);
		mEncoder = new MsgEncoder();
		enableSPO2 = false;
		enableBP = false;
		enableEKG = false;
		enableKinect = false;
		
		//final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		mDecoder = new MsgDecoder(universal.getInputStream());

		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name","InputProcessor");
		mEncoder.sendMsg(msg23, universal.getOutputStream());
		
		KeyValueList kvList;
		outstream = universal.getOutputStream();
		//CreateInputProcessor input = new CreateInputProcessor(port);
		while(true)
		{	
			kvList = mDecoder.getMsg();
			ProcessMsg(kvList);
		}	
	}
	
	static void ProcessMsg(KeyValueList kvList) throws Exception
	{
		int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
		System.out.println("in process msg");
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
			MsgName: InputToGUI	MsgID: 1012	Attrs:
		For more information about KeyValueList, read comments in Util.java.
		****************************************************/
		case 1013:
			System.out.println("Message MsgName:GUIToInput MsgID:1013 received, start processing.");
			/*************************************************
			Add code below to process Message MsgName:GUIToInput MsgID:1013
			This message has following attributes: , use KeyValueList.getValue(String key) to get the values.
			If needed, don't forget to send a msg after processing. See previous comments on how to send a message.
			*************************************************/
			//String port = "COM4";
			//if (args.length == 1)
			//	port = args[0];
			String enable = kvList.getValue("Enable");
			//CreateInputProcessor input = new CreateInputProcessor(port);
			//if(enableFlag!=true){
				//CreateInputProcessor input = new CreateInputProcessor(port);
			//}
			if(enable.equals("True")){
				/*if(kvList.getValue("EnableBP").equals("True"))
					enableBP = true;
				if(kvList.getValue("EnableSPO2").equals("True"))
					enableSPO2 = true;
				if(kvList.getValue("EnableEKG").equals("True"))
					enableEKG = true;
				if(kvList.getValue("EnableKinect").equals("True"))
					enableKinect = true;*/
					
				try {
					input.initialize();
					input.reader.start();
					break;
				} catch (SISException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();

					try {
						input.shutdown();
					}	 catch (SISException e1) {
						e1.printStackTrace();
					}
					return;
				}

				// wait for termination
				/*waitForTermination();

				try {
					input.shutdown();
				} 	catch (SISException e) {
					e.printStackTrace();
				}*/
			}
			else{
				System.out.println("enable == true");
				try{
					System.out.println("Shut down!!");
					input.shutdown();
				}
				catch(SISException e){
					e.printStackTrace();
				}
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
