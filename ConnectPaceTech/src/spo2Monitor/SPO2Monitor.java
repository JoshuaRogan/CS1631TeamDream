package spo2Monitor;

import system.AbstractSISComponent;
import system.SISException;
import system.SISMessage;

public class SPO2Monitor extends AbstractSISComponent {

	@Override
	public String getName() {
		return "SPO2Monitor";
	}
	
	@Override
	public void onMessage(SISMessage msg) {
		int spo2 = Integer.valueOf(msg.getAttr("SPO2"));
		String dateTime = msg.getAttr("DateTime");
		
		if ( spo2 > 90 ) {
			SISMessage alert = new SISMessage();
			alert.addAttr("MsgID", "34");
			alert.addAttr("SPO2", String.valueOf(spo2));
			alert.addAttr("Alert Type", "SPO2 Alert");
			alert.addAttr("DateTime", dateTime);
			sendMsgToServer(alert);
		}
	}



	public static void main(String[] args) {
		SPO2Monitor monitor = new SPO2Monitor();
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
