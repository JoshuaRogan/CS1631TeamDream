

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;


public class SIS_Remote {
	static CreateGUI gui;
	static Socket serverconn;
	static SIS_Remote.ReceiveBuff receivedmsgs;
	static KeyValueList lastreceivedmsg;
	static ArrayList<SIS_LoadedMsg> loadedmsgs;
	static SIS_LoadedMsg currentloadedmsg;
	static MsgDecoder mDecoder;
	static MsgEncoder mEncoder;
	static int refreshrate;
	static Thread receivethread;
	static Thread displaythread;
	public static void init() throws IOException{
		receivedmsgs = new SIS_Remote.ReceiveBuff();
		lastreceivedmsg = null;
		loadedmsgs = new ArrayList();
		KeyValueList kvlist = new KeyValueList();
		kvlist.addPair("MsgID", "1000");
		kvlist.addPair("Description", "Test Msg");
		currentloadedmsg = new SIS_LoadedMsg(0, kvlist);
		loadedmsgs.add(currentloadedmsg);     
		mDecoder = new MsgDecoder(serverconn.getInputStream());
		mEncoder = new MsgEncoder();
		refreshrate = 2000;
		
		receivethread = new Thread(new Runnable(){
			public void run() {
				try{
					for(;;){
						KeyValueList kvInput = SIS_Remote.mDecoder.getMsg();
						if (kvInput != null){
							SIS_Remote.receivedmsgs.add(kvInput);
							SIS_Remote.lastreceivedmsg = kvInput;
							System.out.println("received\n" + kvInput);
						}
					}
				}
				catch (Exception localException) {}
			}
			});
			receivethread.start();
			     
		displaythread = new Thread(new Runnable(){
			public void run(){
				try{
					for(;;){
						KeyValueList kvInput = SIS_Remote.receivedmsgs.remove();
						//SIS_Remote.gui.displayKvList(kvInput, "r");
						System.out.println("disp\n" + kvInput);
						Thread.currentThread();
						Thread.sleep(SIS_Remote.refreshrate);
			        }
				}
				catch (Exception localException) {}
			}
		});
		displaythread.start();
	}
			 
	static class ReceiveBuff
	{
		private LinkedList<KeyValueList> buffs;
		     
		ReceiveBuff(){
			this.buffs = new LinkedList();
		}
		
		synchronized void add(KeyValueList in){
			this.buffs.add(in);
			notify();
		}
		
		synchronized KeyValueList remove(){
			if (this.buffs.isEmpty()) {
				try{
					wait();
				}
				catch (InterruptedException localInterruptedException) {}
			}
			return (KeyValueList)this.buffs.poll();
		}
	}
}
