import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class g4kb
{
 static Socket socket;
  String delimiter="$$$";
  
  public static void main(String args[])
  {
   g4kb mon= new g4kb();
  }
  
  public g4kb()
  {
   
   socket=null;
   try
   {
    //the KB will have its own connection to the monitor or server?
       socket = new Socket("127.0.0.1", 7999);
        
        
      } 
    catch (UnknownHostException e) 
     {
     System.out.println("Unknown host: kq6py");
         System.exit(1);
     } 
    catch  (IOException e) 
     {
     System.out.println("No I/O");
     System.exit(1);
     }
      System.out.println("Server accepted connection");
      
      
      buildMsg(23);//builds and sends initial msg23 
      
      while(true)
      {
       getMsg(socket); //get a 140
      }
  }
 
  public boolean buildMsg(int num)
  {
   try{
    
   
   PrintStream printOut= new PrintStream(socket.getOutputStream());
  
   if(num != 23 && num != 141)
   {
    System.out.println("Message type: "+num+" not recognized");
    return false;
   }
   
   //send first message
   if(num == 23)
   {
    String str = "MsgID$$$23$$$Passcode$$$****$$$SecurityLevel$$$3$$$Name$$$g4kb";
    System.out.println("KB sending: "+str);
       printOut.println(str);
    return true;
   }
   
   //send status
   if(num == 141)
   {
   
    return true;
   }
   
   }
   catch(IOException e)
   {
    System.out.println("***Error*** >> "+ e);
   }
   return false;
  }
 
  public void getMsg(Socket socket)
  {
    try
    {
     //MsgDecoder mDecoder= new MsgDecoder(socket.getInputStream());
     //MsgEncoder mEncoder= new MsgEncoder();
     KeyValueList kvInput;
     InputStream in;
     String line;
     BufferedReader bufferIn;
     in=socket.getInputStream();
     bufferIn  = new BufferedReader(new InputStreamReader(in));
     //while not kill msg
     
     while(true)
     {
     //Receive msg from server
      line=bufferIn.readLine(); 
      if(line!=null)
      {//print the msg
       System.out.println("KB received: "+line); //raw message
       //read in the key value pairs $$$ delimited  
      StringTokenizer strTok = new StringTokenizer(line, "$"); 
       
       ArrayList<String> items = new ArrayList<String>();
       
       while(strTok.hasMoreTokens())
       { 
          items.add(strTok.nextToken());     
       }
       
   
        int pos = items.indexOf("MsgID");
        
        //if it is a 140 we need to do some work
        if(pos != -1 && items.get(pos+1).equals("140"))
        {
         //fields to set and send
         int s = 0, d = 0, age = 0, p=0;
         
         
          //age
          pos = items.indexOf("Age");
          age = Integer.parseInt(items.get(pos+1));
          
          //diastolic
          pos = items.indexOf("Diastolic");
          d = Integer.parseInt(items.get(pos+1));
       
          //Systolic 
          pos = items.indexOf("Systolic");
          s = Integer.parseInt(items.get(pos+1)); //returns the Systolic value
          
          //get the pulse and return -- not used by this component but apperantly we must have it...
          pos = items.indexOf("Pulse");
          p = Integer.parseInt(items.get(pos+1));
          
          //initial part of message 141
          String dia = "Normal";
          String sug = "Have a great day";
          String str;
          str = "MsgID$$$141$$$Systolic$$$"+s+"$$$Diastolic$$$"+d+"$$$"+"Pulse$$$"+p+"$$$Alert$$$No$$$Alert Type$$$Blood Pressure Alert";
          
          //if it is high check how high
          //set Diagnosis, Suggestions
          boolean alert = checkPressure(s, d, age);
          if ( alert )
          {
            str = "MsgID$$$141$$$Systolic$$$"+s+"$$$Diastolic$$$"+d+"$$$"+"Pulse$$$"+p+"$$$Alert$$$Yes$$$Alert Type$$$Blood Pressure Alert";
           if(s < 90 || d < 60)
           {
             dia = "hypotension (low blood pressure alert)";
             sug = "Consult your doctor";
           }
           //stage 1 
           if(s < 160)
           {
            dia = "Stage 1";
            sug = "Consult your doctor";
           }
           //stage 2
           if(s > 159 && s < 180)
           {
            dia = "Stage 2";
            sug = "Consult your doctor";
           }
           //stage 3
           if(s > 179 && s < 210)
           {
            dia = "Stage 3";
            sug = "Consult your doctor";
           }
           //stage 4
           if(s > 209)
           {
            dia = "Stage 4";
            sug = "Consult your doctor";
           }
           
          }
          
          //build
          str = str+"$$$Diagnosis$$$"+dia+"$$$Suggestions$$$"+sug+"$$$DateTime$$$Sat Feb 27 18:10:20 EST 2010";
          
          //send
          try
          {
           PrintStream printOut= new PrintStream(socket.getOutputStream());
           System.out.println("KB sending: "+str);
        printOut.println(str);
          }
          catch(IOException e)
        {
        System.out.println("***Error*** >> "+ e);
        }
          
         
        }
        //if it is a 26 we are acknowledged
        if(pos != -1 && items.get(pos+1).equals("26"))
        {
         String name;
         //make sure name is correct
         pos = items.indexOf("Name");
         name = (String)items.get(pos+1);
         System.out.println("Acknowledgement [26] recieved.");
        }
        
      }
      line=null;
     }
     
    }catch(Exception e){
     System.out.println("Error in getMsg()");
     System.out.println(e);
    }
   }//end getMsg
  
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
  /* 
   * Knoledge Base values section
   */
  public boolean checkPressure(int s, int d, int age)
  {
    //patch for hypotension detection
    if(s < 90 || d < 60)
    {
      return true;
    }
    
    if(age > 0 && age < 20)
    {
    if(s > 120)
     {
      return true;
     }
     if(d > 81)
     {
      return true;
     }
    }
    else if(age > 19 && age < 25)
    {
    if(s > 132)
     {
      return true;
     }
     if(d > 83)
     {
      return true;
     }
    }
    else if(age > 24 && age < 30)
    {
    if(s > 133)
     {
      return true;
     }
     if(d > 84)
     {
      return true;
     }
    }
    else if(age > 29 && age < 35)
    {
    if(s > 134)
     {
      return true;
     }
     if(d > 85)
     {
      return true;
     }
    }
    else if(age > 34 && age < 40)
    {
    if(s > 135)
     {
      return true;
     }
     if(d > 86)
     {
      return true;
     }
    }
    else if(age > 39 && age < 45)
    {
    if(s > 137)
     {
      return true;
     }
     if(d > 87)
     {
      return true;
     }
    }
    else if(age > 44 && age < 50)
    {
    if(s > 139)
     {
      return true;
     }
     if(d > 88)
     {
      return true;
     }
    }
    else if(age > 49 && age < 55)
    {
    if(s > 142)
     {
      return true;
     }
     if(d > 89)
     {
      return true;
     }
    }
    else if(age > 54 && age < 60)
    {
    if(s > 144)
     {
      return true;
     }
     if(d > 90)
     {
      return true;
     }
    }
    
    else if(age > 59)
    {
     if(s > 147)
     {
      return true;
     }
     if(d > 91)
     {
      return true;
     }
    }
    return false;
  }
}