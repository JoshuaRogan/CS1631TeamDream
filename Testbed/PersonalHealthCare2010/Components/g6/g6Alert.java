import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

class Alert
{	
	int BloodSugar;
	Date DateTime;
	static String UserName = "";
	
	public static Alert getFrom(Thresholds theThresholds, int theBloodSugar)
	{
		if (theThresholds.hasMinimum&&theBloodSugar<theThresholds.Minimum)
		{
			return new Alert(theBloodSugar);
		}
		
		if (theThresholds.hasMaximum&&theBloodSugar>theThresholds.Maximum)
		{
			return new Alert(theBloodSugar);
		}
	
		return null;
	}
	
	public Alert(int BloodSugar)
	{
		this.BloodSugar = BloodSugar;
		this.DateTime = new Date();
	}
	
	public KeyValueList toKeyValueList()
	{
		DateFormat aDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
	
		KeyValueList aKeyValueList = new KeyValueList();
		
		aKeyValueList.addPair("MsgID", "42");
		aKeyValueList.addPair("Description", "Blood Sugar Alert");
		aKeyValueList.addPair("Blood Sugar", ""+BloodSugar);
		aKeyValueList.addPair("Alert Type", "Blood Sugar Alert");
		aKeyValueList.addPair("Diagnosis", "high risk");
		aKeyValueList.addPair("Suggestions", "see doctor immediately");
		aKeyValueList.addPair("Name", "g6bloodsugar");
		aKeyValueList.addPair("UserName", UserName);
		aKeyValueList.addPair("DateTime", aDateFormat.format(DateTime));
		
		return aKeyValueList;
	}
}
