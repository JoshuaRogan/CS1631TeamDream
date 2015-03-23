package inputProcessing;

import java.util.ArrayList;
import java.util.List;

/**
 * ECG data container
 * 
 * @author Jerry Ye
 *
 */
public class ECGData {
	
	private List<Integer> leadI = new ArrayList<Integer>();  /* Lead I  */
	private List<Integer> leadII = new ArrayList<Integer>(); /* lead II */
	private List<Integer> leadV = new ArrayList<Integer>();  /* lead V  */
	
	public ECGData() {}
	
	/**
	 * Append an ECG message
	 * 
	 * @param ecgMsg
	 */
	public void addECGMessage(ECGMessage ecgMsg) {
		leadI.add(ecgMsg.getLeadI());
		leadII.add(ecgMsg.getLeadII());
		leadV.add(ecgMsg.getLeadV());
	}

	/**
	 * @return The number of EKG messages in this data
	 */
	public int getNumMessages() {
		return leadI.size();
	}

	public String getLeadIStr() {
		StringBuffer sbuf = new StringBuffer();
		
		for(int i=0; i<leadI.size(); i++)
			sbuf.append(leadI.get(i)).append(' ');
		
		return sbuf.toString();
	}
	
	public String getLeadIIStr() {
		StringBuffer sbuf = new StringBuffer();
		
		for(int i=0; i<leadII.size(); i++)
			sbuf.append(leadII.get(i)).append(' ');
		
		return sbuf.toString();
	}
	
	public String getLeadVStr() {
		StringBuffer sbuf = new StringBuffer();
		
		for(int i=0; i<leadV.size(); i++)
			sbuf.append(leadV.get(i)).append(' ');
		
		return sbuf.toString();
	}

	public void clear() {
		leadI.clear();
		leadII.clear();
		leadV.clear();
	}
}
