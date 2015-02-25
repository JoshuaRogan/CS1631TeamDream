import java.util.*;
import java.net.*;
import java.io.*;
public class InputProcessor{
	
	public static void main(String[] args) throws IOException{
		boolean connectionTrue = true;
		String serverName = args[0];
		final int PORT = 7999;
		InetAddress addr = InetAddress.getByName(serverName);
		final Socket connection = new Socket(addr, PORT);   // Connect to server with new socket
		BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		System.out.println(connection.toString());
		while(connectionTrue){
			
			if(input.ready()){
				System.out.println(connection.isConnected());
				String inputMessage = input.readLine();
			}
			//System.out.println(inputMessage);
			//receiveMessage(connection);
			
		}
		System.out.println("Not connected");
	
	}
	
	/*public void receiveMessage(Socket connection){
		
	}*/

}