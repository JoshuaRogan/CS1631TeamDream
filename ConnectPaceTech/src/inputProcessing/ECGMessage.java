package inputProcessing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * A ECG message
 * 
 * @author Jerry Ye
 *
 */
public class ECGMessage {
	
	public static final int ECG_FLAG = 0x80;
	public static final byte SEQ_MASK = 0x03;
	public static final int ECG_MSGSIZE = 8;
	
	public static final byte LEAD_MASK = 0x04;
	public static final byte LEADNUM_MASK = 0x08;	
	
	private boolean leadsOn;
	private int numLeads;
	private int leadI;
	private int leadII;
	private int leadV;

	public ECGMessage(List<Byte> msg) {
		
		// Parse status byte
		byte status = msg.get(1);
		leadsOn = ((status & LEAD_MASK) == 0);
		
		if ((status & LEADNUM_MASK) == 0)
			numLeads = 3;
		else
			numLeads = 5;
		
		// Parse lead data
		leadI  = ((msg.get(2) << 8) | msg.get(3)) - 32768;
//		leadII  = ((msg.get(4) << 8) | msg.get(5)) - 32768;
		byte[] bArray = new byte[4];
		bArray[0]=(byte)0;
		bArray[1]=(byte)0;
		bArray[2]=msg.get(4);
		bArray[3]=msg.get(5);

		ByteBuffer bf= ByteBuffer.wrap(bArray);
		bf.order(ByteOrder.BIG_ENDIAN);
		//System.out.println(bf.getInt());
		
		leadII  = bf.getInt() - 32768;
		leadV  = ((msg.get(6) << 8) | msg.get(7)) - 32768;
		
//		leadI  = ((msg.get(3) << 8) | msg.get(2)) - 32768;
	//	leadII  = ((msg.get(5) << 8) | msg.get(4)) - 32768;
		//leadV  = ((msg.get(7) << 8) | msg.get(6)) - 32768;
	}


	public boolean isLeadsOn() {
		return leadsOn;
	}

	public int getNumLeads() {
		return numLeads;
	}

	public int getLeadI() {
		return leadI;
	}

	public int getLeadII() {
		return leadII;
	}

	public int getLeadV() {
		return leadV;
	}

	public boolean isValid() {
		return isLeadsOn();
	}


	@Override
	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		
		sbuf.append("LeadsOn/Off: ").append(isLeadsOn()).append(' ')
			.append("NumLeads: ").append(getNumLeads()).append(' ')
			.append("LeadI: ").append(getLeadI()).append(' ')
			.append("LeadII: ").append(getLeadII()).append(' ')
			.append("LeadV: ").append(getLeadV());
		
		return sbuf.toString();
	}
	
	
}
