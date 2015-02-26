import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

class KeyValueList
{
	private Vector keys;
	private Vector values;
   
	/* Constructor */
	public KeyValueList()
	{
		keys = new Vector();
		values = new Vector();
	}
   
	/* Look up the value given key, used in getValue() */
   
	public int lookupKey(String strKey)
	{
		for(int i=0; i < keys.size(); i++)
		{
			String k = (String) keys.elementAt(i);
			if (strKey.equals(k)) 
				return i;
		} 
		return -1;
	}
   
	/* add new (key,value) pair to list */
   
	public boolean addPair(String strKey,String strValue)
	{
		return (keys.add(strKey) && values.add(strValue));
	}
   
	/* get the value given key */
   
	public String getValue(String strKey)
	{
		int index=lookupKey(strKey);
		if (index==-1) 
			return null;
		return (String) values.elementAt(index);
	} 
	
	public void setValue(int index, String val)
	{
		if(index >= 0 && index < size())
			values.set(index, val);
	}

	/* Show whole list */
	public String toString()
	{
		String result = new String();
		for(int i=0; i<keys.size(); i++)
		{
       		result+=(String) keys.elementAt(i)+":"+(String) values.elementAt(i)+"\n";
		} 
		return result;
	}
   
	public int size()
	{ 
		return keys.size(); 
	}
   
	/* get Key or Value by index */
	public String keyAt(int index){ return (String) keys.elementAt(index);}
	public String valueAt(int index){ return (String) values.elementAt(index);}
	
	public ArrayList<String> getValueLike(String key)
	{
		String temp;
		ArrayList<String> results = new ArrayList<String>();
		for(int i=0; i < keys.size(); i++)
		{
			temp = (String) keys.elementAt(i);
			if (temp.contains(key)) 
				results.add((String) values.elementAt(i));
		}
		if(results.size() == 0)
			return null;
		return results;
	}
	
	public void prepareForSend()
	{
		String tempstr;
		for(int i=0;i<keys.size();i++)
		{
			tempstr = (String) keys.get(i);
			tempstr = tempstr.replace('$', ' ').trim();
			if(tempstr.length()==0)
				tempstr = " ";
			keys.set(i, tempstr);
		}
		for(int i=0;i<values.size();i++)
		{
			tempstr = (String) values.get(i);
			tempstr = tempstr.replace('$', ' ').trim();
			if(tempstr.length()==0)
				tempstr = " ";
			values.set(i, tempstr);
		}
	}
}

/**************************************************
  Class MsgEncoder:
      Serialize the KeyValue List and Send it out to a Stream.
***************************************************/
class MsgEncoder
{
	private PrintStream printOut;
	/*If you would like to write msg interpreter your self, read below*/
	/* Default of delimiter in system is $$$ */
	private final String delimiter = "$$$";
   
	public MsgEncoder(){}
   
	/* Encode the Key Value List into a string and Send it out */
   
	public void sendMsg(KeyValueList kvList, OutputStream out) throws IOException
	{
		PrintStream printOut= new PrintStream(out);
		if (kvList == null) 
			return;
		String outMsg= new String();
		for(int i=0; i<kvList.size(); i++)
		{
     		if (outMsg.equals(""))
     			outMsg = kvList.keyAt(i) + delimiter + kvList.valueAt(i);
     		else
     			outMsg += delimiter + kvList.keyAt(i) + delimiter + kvList.valueAt(i);
		}
		//System.out.println(outMsg);
		printOut.println(outMsg);
	}
}

/**************************************
  Class MsgDecoder:
     Get String from input Stream and reconstruct it to 
     a Key Value List.
***************************************/

class MsgDecoder 
{
	private BufferedReader bufferIn;
	private final String delimiter = "$$$";
   
	public MsgDecoder(InputStream in)
	{
		bufferIn  = new BufferedReader(new InputStreamReader(in));	
	}
   
	/* get String and output KeyValueList */
   
	public KeyValueList getMsg() throws IOException
	{
		String strMsg= bufferIn.readLine();
       
		if (strMsg==null) 
			return null;
       
		KeyValueList kvList = new KeyValueList();	
		StringTokenizer st = new StringTokenizer(strMsg, delimiter);
		while (st.hasMoreTokens()) 
		{
			kvList.addPair(st.nextToken(), st.nextToken());
		}
		return kvList;
	}
}
