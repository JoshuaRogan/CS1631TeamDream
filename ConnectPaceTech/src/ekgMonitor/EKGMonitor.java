package ekgMonitor;

import system.AbstractSISComponent;
import system.SISException;
import system.SISMessage;

/**
 * Reference implementation for EKG Monitor
 * @author Jerry Ye
 *
 */
public class EKGMonitor extends AbstractSISComponent {

	@Override
	public String getName() {
		return "EKGMonitor";
	}
	
	@Override
	public void onMessage(SISMessage msg) {
		/* Parse EKG data */
		String leadI = msg.getAttr("LeadI");
		String leadII = msg.getAttr("LeadII");
		String leadIII = msg.getAttr("LeadIII");
		String time = msg.getAttr("DateTime");
		
		/* Send out alert when abnormality in EKG data is found oud */
		if ( true ) {
			SISMessage alert = new SISMessage();
			alert.addAttr("MsgID", "36");
			alert.addAttr("LeadI", leadI);
			alert.addAttr("LeadII", leadII);
			alert.addAttr("LeadIII", leadIII);
			alert.addAttr("Alert Type", "EKG Alert");
			alert.addAttr("DateTime", time);
			
			sendMsgToServer(alert);
		}
	}



	public static void main(String[] args) {
		EKGMonitor monitor = new EKGMonitor();
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
