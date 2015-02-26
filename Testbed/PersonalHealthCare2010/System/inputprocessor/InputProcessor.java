

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import system.AbstractSISComponent;
import system.SISException;
import system.SISMessage;
import system.Utilities;


/**
 * Health monitor reader
 * It keeps listening on serial port which is connected to the health monitor 
 * device, reads data, parses, makes SIS message and sends to SIS server.
 *    
 * @author Yinglin Sun
 *
 */
public class InputProcessor extends AbstractSISComponent {

	private String portName; /* serial port name, e.g. "COM1" */
	private SerialReader reader;
	
	private SerialPort serialPort;
	private InputStream in;
	
	public InputProcessor(String port) {
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
		
        if ( portIdentifier.isCurrentlyOwned() )
            throw new SISException("Error: Port is currently in use");

        /* Open the port */
        CommPort commPort;
		try {
			commPort = portIdentifier.open(this.getClass().getName(), 2000);
		} catch (PortInUseException e) {
			throw new SISException("Port is currently in use", e);
		}
		
        if ( commPort instanceof SerialPort )
        {
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
				in = serialPort.getInputStream();
			} catch (IOException e) {
				throw new SISException(e);
			}
			
			// create reader
			reader = new SerialReader(in);
		} else {
			throw new SISException("Only serial ports are handled by this example.");
		}
	}

	
	
	@Override
	public void onActivation() {
		while( reader == null)
			continue;
		
		// Start the serial reader
		reader.start();
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
	
	/**
	 * Serial reader thread 
	 */
	private class SerialReader extends Thread 
    {
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

        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void stopReading() { alive = false; }
        
        private void sendVitalData(VitalData vd) {
        	String now = Utilities.now();
        	
        	// Send blood pressure data
        	if (vd.isValidBpress() && !vd.equalBpress(oldVd)) {
				SISMessage bpMsg = new SISMessage();
				bpMsg.addAttr("MsgID", "31");
				bpMsg.addAttr("Systolic", String.valueOf(vd.getSystolic()));
				bpMsg.addAttr("Diastolic", String.valueOf(vd.getDiastolic()));
				bpMsg.addAttr("Pulse", String.valueOf(vd.getPulse()));
				bpMsg.addAttr("DateTime", now);
				sendMsgToServer(bpMsg);
				
				sendECGData();
        	}
        	
			// Send SpO2 data
			if (vd.isValidSPO2() && !vd.equalSpo2(oldVd)) {
				SISMessage spo2Msg = new SISMessage();
				spo2Msg.addAttr("MsgID", "33");
				spo2Msg.addAttr("SPO2", String.valueOf(vd.getSpo2()));
				spo2Msg.addAttr("DateTime", now);
				sendMsgToServer(spo2Msg);
			}
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
        
        public void run ()
        {
        	alive = true;
        	
        	byte[] buf = new byte[256];
            int n = -1;
            try
            {
                while ( ( n = this.in.read(buf)) > -1 )
                {
                	if ( !alive )
                		break;
                	
                	for(int i=0; i<n; i++)
                	{
                		switch(state)
                		{
                		case ST_IDLE:
                			switch(buf[i])
                			{
                			case (byte)128: //0x80, Waveform message starts
                				ecg_buf.add(buf[i]);
                				state = Data_State.ST_ECG_STATUS;
                				
                				break;
                			
                			case 'P':
                				pt_str.append((char)buf[i]);
                				state = Data_State.ST_PT_FLAG;
                				break;
                			
                			case 'T':
                				sync = false;
                				pt_str.append((char)buf[i]);
                				
                				state = Data_State.ST_PT_DATA;
                				break;
                			default:
                				sync = false;
                				break;
                			} // switch(buf[i])
                			
                			break; // case ST_IDLE
                			
                		case ST_ECG_STATUS: /* ECG status byte */
                			if ((buf[i] & 0xf0) > 0)
            				{
            					state = Data_State.ST_IDLE;
            					break;
            				}
                			
            				if (sync)
            				{
            					if ((buf[i] & ECGMessage.SEQ_MASK) != ecg_seq)
            					{
            						sync = false;
            						state = Data_State.ST_IDLE;
            						break;
            					}
            				}
            				else
            				{
            					if ((buf[i] & ECGMessage.SEQ_MASK) == ecg_seq)
            					{
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
            				ecg_buf.add(buf[i]);

            				if (ecg_buf.size() >= ECGMessage.ECG_MSGSIZE)
            				{
            					if (sync)
            					{
            						// A ECG message is ready
            						ECGMessage ecgMsg = new ECGMessage(ecg_buf);
            						
            						// System.out.println(ecgMsg);
            						
            						//if (ecgMsg.isValid()) {
            						if (ecgData.getNumMessages() < 10)
            							ecgData.addECGMessage(ecgMsg);
            						//}
            					}
            					
            					//if (ecgData.getNumMessages() >= 1000)
            					//	sendECGData();
            					
            					ecg_buf.clear();
            					state = Data_State.ST_IDLE;
            				}
            				
                			break;
                		
                		case ST_PT_FLAG:
                			if (buf[i] == 'T')
                			{
                				pt_str.append((char)buf[i]);
                				
                				state = Data_State.ST_PT_DATA;
                			}
                			else
                			{
                				sync = false;
                				if (buf[i] == 128)
                				{
                					state = Data_State.ST_ECG_STATUS;
                					break;
                				}
                				state = Data_State.ST_IDLE;
                				break;
                			}
                			
                			break;
                		case ST_PT_DATA:
                			if (buf[i] == 128)
                			{
                				sync = false;
                				state = Data_State.ST_ECG_STATUS;
                				break;
                			}
                			
                			if (!((buf[i] == 32) || ((buf[i] >= 48) && (buf[i] <= 57))))
                			{
                				sync = false;
                				state = Data_State.ST_IDLE;
                				break;
                			}
                			
                			pt_str.append((char)buf[i]);                			
                			if (pt_str.length() >= PT_BUFSIZE)
                			{
                				sync = true;
                				
                				// A complete PT string is ready
                				VitalData vd = new VitalData(pt_str.toString());

                				// print out on console
                				//System.out.println(vd);

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
                } // while
            } // try
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }



	public static void main(String[] args) {
		String port = "COM1";
		if (args.length == 1)
			port = args[0];
			
		InputProcessor input = new InputProcessor(port);
		try {
			input.initialize();
		} catch (SISException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			
			try {
				input.shutdown();
			} catch (SISException e1) {
				e1.printStackTrace();
			}
			
			return;
		}

		// wait for termination
		waitForTermination();
		
		try {
			input.shutdown();
		} catch (SISException e) {
			e.printStackTrace();
		}
	}

}
