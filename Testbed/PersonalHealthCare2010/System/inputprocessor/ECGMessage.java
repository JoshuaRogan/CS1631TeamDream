

import java.util.List;

/**
 * A ECG message
 * 
 * @author Yinglin Sun
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
		leadII  = ((msg.get(4) << 8) | msg.get(5)) - 32768;
		leadV  = ((msg.get(6) << 8) | msg.get(7)) - 32768;
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
