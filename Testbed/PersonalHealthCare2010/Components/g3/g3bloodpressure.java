// see Knowledgebase
import java.io.*;
import java.net.*;
import java.util.*;

public class  g3bloodpressure
{ 
	static boolean debug = false;
	
	public static void main(String []args) throws Exception
	{
		if(args.length > 0)
			for(String var : args)
				if(var.equals("-debug"))
					debug = true;
				
		new g3bloodpressure();
	}
	
	public g3bloodpressure()
	{
		String ip = "127.0.0.1";
		int port = 7999;
		Socket server;
		PrintWriter out;
		BufferedReader in;
		String message;
		String[][] parsed;
		int msgid;
		
		try
		{
			server = new Socket(ip, port);
			
			out = new PrintWriter(server.getOutputStream());
			in = new BufferedReader(new InputStreamReader(server.getInputStream()));
		
			if(debug)
				System.out.println("Initializing...");
				
			String[][] outMessage = Parser.parseMessage(Parser.readMessage(23));
			Parser.setVal(outMessage, "Name", "g3bloodpressure");
			out.println(Parser.reparse(outMessage,"$$$"));
			out.flush();
			
			if(debug)
				System.out.println("Waiting for profile...");
			do
			{
				message = in.readLine();
				parsed = Parser.parseMessage(message, "[$][$][$]");
				msgid = Parser.getMessageID(parsed);
			}
			while(msgid != 45);
			
			while(true)
			{
				if(debug)
					System.out.println("Waiting...");
					
				message = in.readLine();
				
				if(debug)
					System.out.println("Message Recieved:\n"+message);
				
				parsed = Parser.parseMessage(message, "[$][$][$]");
				msgid = Parser.getMessageID(parsed);
				
				if(msgid != 26 && msgid != 45)
				{
					out.println(getMessage(msgid, parsed));
					out.flush();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private String getMessage(int msgID, String[][] message)
	{
		String[][] outMessage = new String[0][0];
		if(msgID == 31)
			outMessage = Parser.parseMessage(Parser.readMessage(130));
		else if(msgID == 131)
			outMessage = Parser.parseMessage(Parser.readMessage(32));
		else
		{
			outMessage = Parser.parseMessage(Parser.readMessage(26));
			Parser.setVal(outMessage, "AckMsgID", Integer.toString(msgID));
		}
		
		for(int i = 0; i < message[0].length; i++)
			if(!message[0][i].equals("MsgID") && !message[0][i].equals("Description"))
				Parser.setVal(outMessage, message[0][i], message[1][i]);
		
		String out = Parser.reparse(outMessage, "$$$");
		if(debug)
			System.out.println("Message Sent:\n"+out);
		
		return out;
	}
}

	
