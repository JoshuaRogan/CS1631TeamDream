package bpMonitor;

import system.AbstractSISComponent;
import system.SISException;
import system.SISMessage;

/**
 * Reference implementation for Blood Pressure Monitor
 * 
 * @author Jerry Ye
 *
 */
public class BloodPressureMonitor extends AbstractSISComponent {

	@Override
	public String getName() {
		return "BloodPressureMonitor";
	}
	

	@Override
	public void onMessage(SISMessage msg) {
		/* Parse pressure data */
		int sys = Integer.valueOf(msg.getAttr("Systolic"));
		int disa = Integer.valueOf(msg.getAttr("Diastolic"));
		int pulse = Integer.valueOf(msg.getAttr("Pulse"));
		
		String dateTime = msg.getAttr("DateTime");
		
		/* Send out alert message when abnormality is found out */
		if ( sys > 5 || disa > 5 ) {
			SISMessage alert = new SISMessage();
			alert.addAttr("MsgID", "32");
			alert.addAttr("Systolic", String.valueOf(sys));
			alert.addAttr("Diastolic", String.valueOf(disa));
			alert.addAttr("Pulse", String.valueOf(pulse));
			alert.addAttr("Alert Type", "Blood Pressure Alert");
			alert.addAttr("DateTime", dateTime);
			
			sendMsgToServer(alert);
		}
	}


	public static void main(String[] args) {
		BloodPressureMonitor monitor = new BloodPressureMonitor();
		try {
			monitor.initialize();
		} catch (SISException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			
			try {
				monitor.shutdown();
			} catch (SISException e1) {
				e1.printStackTrace();
			}
			
			return;
		}
		
		waitForTermination();
		
		try {
			monitor.shutdown();
		} catch (SISException e) {
			e.printStackTrace();
		}
	}
}
