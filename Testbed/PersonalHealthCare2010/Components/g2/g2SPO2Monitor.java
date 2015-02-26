import java.net.*;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jaw84, maz29
 */
public class g2SPO2Monitor {
    Socket s;
    boolean didReceive45=false;
    KeyValueList userProfile;
    MsgEncoder en;
    MsgDecoder de;
    
    public g2SPO2Monitor (Socket s) {
        this.s = s;
        KeyValueList inkvl;
		
        try{
            en = new MsgEncoder();
            de = new MsgDecoder(s.getInputStream());
            KeyValueList l = new KeyValueList();
            l.addPair("MsgID", "23");
            l.addPair("Name", "g2SPO2Monitor");
            en.sendMsg(l, s.getOutputStream());

            while(true){
                System.out.println("Waiting for server to send message...");
                inkvl = de.getMsg();
                int msgID = Integer.parseInt(inkvl.getValue("MsgID"));
				
				//check if its message 45	
				if(msgID==45) {
					didReceive45=true;
					userProfile=inkvl;
					System.out.println("Got user profile");
				}
                // look for a message ID in the KeyValueList
                else if(msgID!=-1 && didReceive45){
                    // parse to an int, then do what is needed
                                        
                    switch(msgID){
						case 22: System.exit(0);break;
						
                        case 26: System.out.println("Initialized");break;

                        case 33:
                            // take in SPO2 reading, return msg 34 to server
                            int spo2 = Integer.parseInt(inkvl.getValue("SPO2"));
                            
                            KeyValueList skvl = new KeyValueList();
                            skvl.addPair("MsgID", "34");
							skvl.addPair("Name", "g2SPO2Monitor");
                            
                            if(spo2<92){
                                skvl.addPair("Alert Type","SP02 Alert");
                            }
                            else{
                                skvl.addPair("Alert Type","Normal SPO2");
                            }
                            skvl.addPair("DateTime",inkvl.getValue("DateTime"));
							skvl.addPair("SPO2",""+spo2);
                            en.sendMsg(skvl,s.getOutputStream());
                            System.out.println("To server:\n"+skvl);
                            break;
                        
                        default: System.out.println("SPO2 monitor does not take MsgID: "+msgID);break;
                    }
                }
                else{
                    System.out.println("No MsgID or User Profile not sent");
                }
            }
         }
         catch (Exception e) {
            System.out.println("ERROR: "+e);
        }
    }
        
    public static void main(String[] args) {
        String address;
        int port;

        // get params (address / port)
        if(args.length>=1){
            address = args[0];
        }
        else{
            System.out.println("No address given, assuming 127.0.0.1");
            address = "127.0.0.1";
        }

        if(args.length>1){
            try{
                port = Integer.parseInt(args[1]);
            }
            catch(Exception e){
                System.out.println("Error with given port, assuming 7999");
                port = 7999;
            }
        }
        else{
            System.out.println("No port given, assuming 7999");
            port = 7999;
        }

        try {
            System.out.println("Address: "+address);
            System.out.println("Port: "+port);
            
            Socket s = new Socket(address,port);
            g2SPO2Monitor m = new g2SPO2Monitor(s);
            System.out.println("should not get here!");
        }
         
        catch (Exception e) {
            System.out.println(e);
        }
    }
}