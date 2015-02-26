import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Date;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//import com.sun.jmx.snmp.Timestamp;


public class g4bloodpressure {
	static Socket socket;
	String delimiter="$$$";
	boolean active=false;
	boolean hasUserInfo=false;
	int systolic,diastolic,pulse,age;
	String date,userName;
	String diagnosis, suggestions;
	String componentName="g4bloodpressure";
	
	public static void main(String args[]){
		new g4bloodpressure();		
	}
	
	public g4bloodpressure(){
		
		socket=null;
		try{
		    socket = new Socket("127.0.0.1", 7999);
		     
		     
		   } catch (UnknownHostException e) {
		     System.out.println("Unknown host: kq6py");
		     System.exit(1);
		   } catch  (IOException e) {
		     System.out.println("No I/O");
		     System.exit(1);
		   }
		   System.out.println("Server accepted connection");
		   
		   
		   buildMsg(23);//builds initial msg23 
		   
		   
		   getMsg(socket);
	}
	class MsgDecoder 
	{
		private BufferedReader bufferIn;
		private final String delimiter = "$$$";
	   
		public MsgDecoder(InputStream in)
		{
			bufferIn  = new BufferedReader(new InputStreamReader(in));	
		}
	   
		/*
	     get String and output KeyValueList
		*/
	   
		public KeyValueList getMsg() throws IOException
		{
			String strMsg= bufferIn.readLine();
			System.out.println("In Msg: "+strMsg);
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

	public void getMsg(Socket socket){
		try
		{
			InputStream in;
			String line;
			BufferedReader bufferIn;
			in=socket.getInputStream();
			bufferIn  = new BufferedReader(new InputStreamReader(in));
			int pos;			
			
			//while not kill msg
			while(true){
				 //Receive msg from server
			    line=bufferIn.readLine(); 
			    if(line!=null)
			    {//print the msg
			     System.out.println("BP received: "+line); //raw message
			     //read in the key value pairs $$$ delimited  
			    StringTokenizer strTok = new StringTokenizer(line, "$"); 
			     
			     ArrayList<String> items = new ArrayList<String>();
			     
			     while(strTok.hasMoreTokens())
			     { 
			        items.add(strTok.nextToken());     
			     }
			     
			     	//we handle msg 24,31,45,141,25,22
			     	pos = items.indexOf("MsgID");
			     	
			     	if(pos != -1 && items.get(pos+1).equals("26"))
			     	{
			     		System.out.println("Connection to the server has been acknowledged and accepted");
			     		
			     	}
			     	else if(pos != -1 && items.get(pos+1).equals("24"))
			     	{
			     		pos=items.indexOf("Name");
			     		if(items.get(pos+1).equals(componentName)){
			     			active=true;
			     			System.out.println("Monitor activated");
			     		}
			     	}
			     	else if(pos != -1 && items.get(pos+1).equals("31"))
			     	{
			     		if(hasUserInfo){
			     			pos=items.indexOf("Systolic");
				     		systolic=Integer.parseInt(items.get(pos+1));
				     		pos=items.indexOf("Diastolic");
				     		diastolic=Integer.parseInt(items.get(pos+1));
				     		pos=items.indexOf("Pulse");
				     		pulse=Integer.parseInt(items.get(pos+1));
				     		pos=items.indexOf("DateTime");
				     		date=items.get(pos+1);
			     			buildMsg(140);
			     		}
			     		else
			     			System.out.println("User information not yet received");
			     	}
			     	else if(pos != -1 && items.get(pos+1).equals("141"))
			     	{
			     		System.out.println("Knowledge Base accepted msg 140");
			     		pos=items.indexOf("Diagnosis");
			     		diagnosis=items.get(pos+1);
			     		pos=items.indexOf("Suggestions");
			     		suggestions=items.get(pos+1);
			     		
			     		pos=items.indexOf("Alert");
			     		if(pos != -1 && items.get(pos+1).equals("Yes"))
			     			buildMsg(32);
			     		
			     	}
			     	else if(pos != -1 && items.get(pos+1).equals("45"))
			     	{
			     		pos=items.indexOf("UserName");
			     		userName=items.get(pos+1);
			     		pos=items.indexOf("Age");
			     		age=Integer.parseInt(items.get(pos+1));
			     		hasUserInfo=true;
			     	}
			     	else if(pos != -1 && items.get(pos+1).equals("25"))
			     	{
			     		pos=items.indexOf("Name");
			     		if(items.get(pos+1).equals(componentName)){
			     			active=false;
			     			System.out.println("Monitor deactivated");
			     		}
			     	}
			     	else if(pos != -1 && items.get(pos+1).equals("22"))
			     	{
			     		pos=items.indexOf("Name");
			     		if(items.get(pos+1).equals(componentName)){
			     			System.out.println("Kill Msg recieved. Shutting down...");
			     			System.exit(1);
			     		}
			     	}
			     	else{
			     		//ignore message
			     	}
			     	
			    }
			    line=null;
			}
		}catch(Exception e){
			System.out.println("Error in getMsg()");
			System.out.println(e);
		}
	}
	/*
	 * only build msg 23, 32, 140
	 */
	public void buildMsg(int msgNum){
		KeyValueList list=new KeyValueList();
		
		if(msgNum!=23 && msgNum!=32 && msgNum!=140)
			System.out.println("msg number: "+msgNum+ "is not applicable message");
		else{
			/*try {//reads XML file
				  File file = new File(".\\xml\\"+msgNum+".xml");
				  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				  DocumentBuilder db = dbf.newDocumentBuilder();
				  Document doc = db.parse(file);
				  doc.getDocumentElement().normalize();
				  //System.out.println("Root element " + doc.getDocumentElement().getNodeName());
				  list.addPair("MsgID", ""+msgNum);
				  NodeList nodeLst=doc.getElementsByTagName("Item");
				  

				  for (int s = 0; s < nodeLst.getLength(); s++) {

				    Node fstNode = nodeLst.item(s);
				    
				    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
				  
				      Element fstElmnt = (Element) fstNode;
				      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("Key");
				      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
				      NodeList keyNameNodeList = fstNmElmnt.getChildNodes();
				     // System.out.println("Key : "  + ((Node) keyNameNodeList.item(0)).getNodeValue());
				      NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("Value");
				      Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
				      NodeList valueNodeList = lstNmElmnt.getChildNodes();
				     // System.out.println("Value : " + ((Node) valueNodeList.item(0)).getNodeValue());
				      
				      
				      list.addPair(keyNameNodeList.item(0).getNodeValue(), valueNodeList.item(0).getNodeValue());
				      
				    }

				  } catch (Exception e) {
				    e.printStackTrace();
				  }*/
				 
			list.addPair("MsgID", ""+msgNum);
					
				  if(msgNum==32){
					  
					  list.addPair("Systolic", ""+systolic);
					  list.addPair("Diastolic", ""+diastolic);
					  list.addPair("Pulse", ""+pulse);
					  list.addPair("Name", componentName);
					  list.addPair("Diagnosis", diagnosis);
					  list.addPair("Suggestions", suggestions);
					  list.addPair("DateTime", date);			  
					  
				  }
				  else if(msgNum==140){
					  list.addPair("Age", ""+age);
					  list.addPair("Systolic", ""+systolic);	
					  list.addPair("Diastolic", ""+ diastolic);	
					  list.addPair("Pulse", ""+pulse);	
					  list.addPair("DateTime", date);
					  
				  }
				  else if(msgNum==23){
					  list.addPair("Name", componentName);
					  list.addPair("SecurityLevel", "3");
					  list.addPair("Passcode", "****");
				  }
				  

				  
			try {
				sendMsg(list);
			   } catch (IOException e) {
				
				e.printStackTrace();
			   }
		}
	}
	
	public void sendMsg(KeyValueList kvList) throws IOException
	{
		
		PrintStream printOut= new PrintStream(socket.getOutputStream());
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
		System.out.println("Sending: "+outMsg);
		printOut.println(outMsg);
	}
}
/* 
Class KeyValueList:
  List of (Key, Value) pair--the basic format of message
  keys: MsgID and Description are required for any messages
*/

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
}