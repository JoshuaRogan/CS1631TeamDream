package display;

import system.AbstractSISComponent;
import system.SISException;
import system.SISMessage;


/**
 * Reference implementation for GUI
 * 
 * @author Jerry Ye
 *
 */
public class Display extends AbstractSISComponent {

	@Override
	public String getName() {
		return "GUI";
	}
	
	@Override
	public void onMessage(SISMessage msg) {
		/* Display message on GUI */
		//System.out.println("Display: " + msg);
	}

	public static void main(String[] args) {
		Display display = new Display();
		try {
			display.initialize();
		} catch (SISException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			
			try {
				display.shutdown();
			} catch (SISException e1) {
				e1.printStackTrace();
			}
			
			return;
		}
		
		waitForTermination();
		
		try {
			display.shutdown();
		} catch (SISException e) {
			e.printStackTrace();
		}
	}

}
