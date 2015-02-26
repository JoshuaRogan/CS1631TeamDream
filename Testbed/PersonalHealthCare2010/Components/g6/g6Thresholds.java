import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

class Thresholds
{
	public int Minimum;
	public int Maximum;
	public boolean hasMinimum;
	public boolean hasMaximum;
	
	Thresholds()
	{
		hasMinimum = false;
		hasMaximum = false;
	}
	
	Thresholds(KeyValueList kvList)
	{
		Minimum = Integer.parseInt(kvList.getValue("Minimum"));
		Maximum = Integer.parseInt(kvList.getValue("Maximum"));
		hasMinimum = Boolean.parseBoolean(kvList.getValue("hasMinimum"));
		hasMaximum = Boolean.parseBoolean(kvList.getValue("hasMaximum"));
	}
	
	public static Thresholds getDefaultThresholds()
	{
		Thresholds aThresholds = new Thresholds();
		
		aThresholds.setMaximum(140);
		
		return aThresholds;
	}
	
	public void setMinimum(int Value)
	{
		Minimum = Value;
		hasMinimum = true;
	}
	
	public void setMaximum(int Value)
	{
		Maximum = Value;
		hasMaximum = true;
	}
	
	public KeyValueList toKeyValueList()
	{
		KeyValueList aKeyValueList = new KeyValueList();
		
		aKeyValueList.addPair("MsgID", "160");
		aKeyValueList.addPair("Description", "Thresholds");
		aKeyValueList.addPair("Minimum", ""+Minimum);
		aKeyValueList.addPair("Maximum", ""+Maximum);
		aKeyValueList.addPair("hasMinimum", ""+hasMinimum);
		aKeyValueList.addPair("hasMaximum", ""+hasMaximum);
		
		return aKeyValueList;
	}
	
	public String toString()
	{
		return ""+hasMinimum+":"+Minimum+"\t"+hasMaximum+":"+Maximum;
	}
}
