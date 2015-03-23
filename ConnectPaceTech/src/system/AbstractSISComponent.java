package system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Abstract class for SIS component
 * 
 * @author Jerry Ye (Yinglin Sun)
 * 
 */
public abstract class AbstractSISComponent {
	private Socket sock;
	private PrintStream output;
	private BufferedReader input;

	private MessageReceiver receiver; /* receive message from SIS server */

	private boolean active = false;

	/**
	 * Do initialization. Concrete component should override and add itself
	 * initialization code.
	 * 
	 * @throws SISException
	 */
	public void initialize() throws SISException {
		/* Connect to the SIS server */
		try {
			// Establish connection to the SIS server
			sock = new Socket("127.0.0.1", 7999);
			output = new PrintStream(sock.getOutputStream());
			input = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));
		} catch (UnknownHostException e) {
			throw new SISException(e);
		} catch (IOException e) {
			throw new SISException(e);
		}

		/* Start to receive message */
		receiver = new MessageReceiver(input);
		receiver.start();

		/* Send the initialization message to the SIS server */
		SISMessage msg = new SISMessage();
		msg.addAttr("MsgID", "23");
		msg.addAttr("Name", getName());
		msg.addAttr("Passcode", " ");
		msg.addAttr("SecurityLevel", " ");

		sendMsgToServer(msg);
	}

	private void turnOn() {
		active = true;

		onActivation();
	}

	private void turnOff() {
		active = false;

		onDeactivation();
	}

	public boolean isActive() {
		return active;
	}

	/**
	 * Send message to SIS server
	 */
	public void sendMsgToServer(SISMessage msg) {
		System.out.println("Sent a message -------------");
		System.out.println(msg.getFormatString());
		//System.out.println("Jerry testing!\n");// reserved space, Jerry test

		output.println(msg.toString());
	}

	/**
	 * This method will be called when the component is about to shut down.
	 * 
	 * @throws SISException
	 */
	public void shutdown() throws SISException {
		// close sockets, that will cause receiver thread to exit
		try {
			sock.close();
		} catch (IOException e) {
			throw new SISException(e);
		}
	}

	/**
	 * Wait for user hitting Enter to terminate
	 */
	public static void waitForTermination() {
		System.out.println("Hit Enter to stop.\n");
		try {
			System.in.read();
		} catch (Throwable thr) {
			thr.printStackTrace();
		}
		;
	}

	/**
	 * @return the component name
	 */
	public abstract String getName();

	/**
	 * This will be called when an application message arrives.
	 * 
	 * @param msg
	 */
	public void onMessage(SISMessage msg) {
	}

	/**
	 * This will be called when activation message arrives.
	 */
	public void onActivation() {
	}

	/**
	 * This will be called when deactivation message arrives.
	 */
	public void onDeactivation() {
	}

	/**
	 * Message receiver thread which listens on socket to receive message from
	 * the SIS server.
	 */
	private class MessageReceiver extends Thread {
		private BufferedReader input;

		public MessageReceiver(BufferedReader input) {
			this.input = input;
		}

		@Override
		public void run() {
			while (true) {
				String line;
				try {
					line = input.readLine();
				} catch (IOException e) {
					// e.printStackTrace();
					break;
				}

				if (line == null)
					break;

				// A message arrived.
				SISMessage msg = new SISMessage(line);

				System.out.println("Received a message ------------");
				System.out.println(msg.getFormatString());

				switch (msg.getID()) {
				case 24:
				case 26:
					turnOn();
					break;
				case 25:
					turnOff();
					break;
				default:
					// If the component is not active, ignore this message.
					if (isActive())
						onMessage(msg);
				}
			} // while
		} // run
	} // class MessageReceiver
}
