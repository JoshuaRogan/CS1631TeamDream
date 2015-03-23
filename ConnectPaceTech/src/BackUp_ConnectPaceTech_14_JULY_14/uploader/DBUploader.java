package uploader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import system.AbstractSISComponent;
import system.SISException;
import system.SISMessage;

/**
 * DB Uploader It uploads the alert message into the remote database
 * 
 * @author Yinglin Sun
 * 
 */
public class DBUploader extends AbstractSISComponent {

	private Connection dbConn;

	private String userid = "UnknownUser";

	public DBUploader() {
	}

	@Override
	public String getName() {
		return "Uploader";
	}

	@Override
	public void initialize() throws SISException {
		super.initialize();

		/* Connect to the remote DB */
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver");
			dbConn = DriverManager.getConnection(
					"jdbc:jtds:sqlserver://64.107.76.51:1433/SIS", "Chronobot",
					"Chr0n0b0t");
		} catch (Exception e) {
			e.printStackTrace();
			throw new SISException(e.getMessage());
		}
	}

	@Override
	public void onMessage(SISMessage msg) {

		int id = msg.getID();

		/* If the message 45 is received, retrieve userid */
		if (id == 45) {
			String s = msg.getAttr("UserName");
			if (s != null)
				userid = s;

			return;
		}

		/* Retrieve the name of the component generating the alert message */
		String comName = msg.getAttr("Name");

		/* Retrieve the Diagnosis information */
		String diag = msg.getAttr("Diagnosis");

		/* Retrieve Suggestions */
		String suggest = msg.getAttr("Suggestions");

		/** Only accept Bloodpressure alert, spo2 alert, and ekg alert */
		PreparedStatement stmt;
		try {
			/* Upload message to DB */
			switch (id) {
			case 32:
				stmt = dbConn
						.prepareStatement("INSERT INTO SISDB (user_id, COMPONENT, SYSTOLIC, DIASTOLIC, PULSE, DIAGNOSIS, SUGGESTIONS, EntryDate, Note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ");
				stmt.setString(1, userid);
				stmt.setString(2, comName);
				stmt.setString(3, msg.getAttr("Systolic"));
				stmt.setString(4, msg.getAttr("Diastolic"));
				stmt.setString(5, msg.getAttr("Pulse"));
				stmt.setString(6, diag);
				stmt.setString(7, suggest);
				stmt.setTimestamp(8, Timestamp.valueOf(msg.getAttr("DateTime")));
				stmt.setString(9, msg.getAttr("Alert Type"));

				break;
			case 34:
				stmt = dbConn
						.prepareStatement("INSERT INTO SISDB (user_id, COMPONENT, SPO2, DIAGNOSIS, SUGGESTIONS, EntryDate, Note) VALUES (?, ?, ?, ?, ?, ?, ?) ");
				stmt.setString(1, userid);
				stmt.setString(2, comName);
				stmt.setString(3, msg.getAttr("SPO2"));
				stmt.setString(4, diag);
				stmt.setString(5, suggest);
				stmt.setTimestamp(6, Timestamp.valueOf(msg.getAttr("DateTime")));
				stmt.setString(7, msg.getAttr("Alert Type"));

				break;
			case 36:
				stmt = dbConn
						.prepareStatement("INSERT INTO SISDB (user_id, COMPONENT, EKG, DIAGNOSIS, SUGGESTIONS, EntryDate, Note) VALUES (?, ?, ?, ?, ?, ?, ?) ");
				stmt.setString(1, userid);
				stmt.setString(2, comName);

				String leadI = msg.getAttr("LeadI");
				String leadII = msg.getAttr("LeadII");
				String leadIII = msg.getAttr("LeadIII");
				stmt.setBytes(3,
						(leadI + "$$$" + leadII + "$$$" + leadIII).getBytes());

				stmt.setString(4, diag);
				stmt.setString(5, suggest);
				stmt.setTimestamp(6, Timestamp.valueOf(msg.getAttr("DateTime")));
				stmt.setString(7, msg.getAttr("Alert Type"));

				break;

			case 42:
				String bloodSugar = msg.getAttr("Blood Sugar");

				stmt = dbConn
						.prepareStatement("INSERT INTO SISDB (user_id, COMPONENT, BLOODSUGAR, DIAGNOSIS, SUGGESTIONS, EntryDate, Note) VALUES (?, ?, ?, ?, ?, ?, ?) ");
				stmt.setString(1, userid);
				stmt.setString(2, comName);
				stmt.setString(3, bloodSugar);
				stmt.setString(4, diag);
				stmt.setString(5, suggest);
				stmt.setTimestamp(6, Timestamp.valueOf(msg.getAttr("DateTime")));
				stmt.setString(7, msg.getAttr("Alert Type"));

				break;

			default:
				return;
			}

			stmt.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shutdown() throws SISException {
		super.shutdown();

		try {
			dbConn.close();
		} catch (SQLException e) {
			throw new SISException(e);
		}
	}

	public static void main(String[] args) {
		DBUploader uploader = new DBUploader();
		try {
			uploader.initialize();
		} catch (SISException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();

			try {
				uploader.shutdown();
			} catch (SISException e1) {
				e1.printStackTrace();
			}

			return;
		}

		waitForTermination();

		try {
			uploader.shutdown();
		} catch (SISException e) {
			e.printStackTrace();
		}
	}
}
