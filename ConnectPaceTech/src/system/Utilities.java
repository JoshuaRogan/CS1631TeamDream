package system;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utilities {
	private Utilities() {}

	private static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss.SSSSSSSS";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		
		return sdf.format(cal.getTime());
	}

}
