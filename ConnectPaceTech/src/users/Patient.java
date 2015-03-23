package users;

public class Patient {
	String patientID;
	String lname;
	String fname;
	String age;

	public Patient(String ln, String fn) {
		this.lname = ln;
		this.fname = fn;
	}

	public String getLastName() {
		return lname;
	}

	public String getFirstName() {
		return fname;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
