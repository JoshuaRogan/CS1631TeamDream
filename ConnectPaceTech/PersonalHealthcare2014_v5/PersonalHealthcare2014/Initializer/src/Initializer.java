import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.Choice;

public class Initializer {
	
	Choice msglist;
	DefaultTableModel tablemodel1;
	JTable msgdetail2;
	DefaultTableModel tablemodel2;
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;
	
	public static void main(String[] arg) throws Exception{
		System.out.println("Hello!");
		try{
			Initializer initSIS = new Initializer(); 
			try{						
	            Remote.serverconn = new Socket("127.0.0.1", 7999);
			}
			catch(NumberFormatException e1){
				JOptionPane.showMessageDialog(new JFrame(), "Port number must be an integer.");
			}
			catch (UnknownHostException e1){
				JOptionPane.showMessageDialog(new JFrame(), "Connection to server failed 1.");
			}
			catch (IOException e1){
				JOptionPane.showMessageDialog(new JFrame(), "Connection to server failed 2.");
			}
			Remote.init();
			File file = new File("C:\\Users\\Josh\\OneDrive\\Git\\CS1631TeamDream\\ConnectPaceTech\\PersonalHealthcare2014_v5\\PersonalHealthcare2014\\xml\\InitXML\\list_InitXML.txt");
			
			if(!file.exists())
				System.out.println("Initialization File: "+ file.getName()+ " does not exist!");
			else
				System.out.println("Load: "+ file.getName());
			
			String filename = file.getName();
			if ((filename.endsWith(".xml")) || (filename.endsWith(".XML"))){
				initSIS.msglist.removeAll();
				Remote.loadedmsgs.clear();
				Remote.currentloadedmsg = null;
				KeyValueList tempkvlist = XMLUtil.readFromXML(file);
			    if (tempkvlist != null){
			    	initSIS.msglist.add(filename);
			        initSIS.msglist.select(0);
			        Remote.loadedmsgs.add(new LoadedMsg(0, tempkvlist));
			        Remote.currentloadedmsg = (LoadedMsg)Remote.loadedmsgs.get(0);
			    }
			    else{
			    	JOptionPane.showMessageDialog(new JFrame(), "Error: File " + filename + " could not be loaded.");
			        //GUI.this.text3.setText("");
			    	//textArea.setText("Load: "+ filename);
			    }
			    initSIS.displayKvList(tempkvlist, "s");
			}
			else{
				initSIS.msglist.removeAll();
			    Remote.loadedmsgs.clear();
			    System.out.println("after clear");
			    Remote.currentloadedmsg = null;
			    try{
			    	BufferedReader br = new BufferedReader(new FileReader(file));
			        while (br.ready()){
			        	//System.out.println("in While loop...");
			        	String tempstr = br.readLine().trim();
			            if (tempstr.length() != 0){
			            	File tempfile1 = new File(tempstr);
			                if (!tempfile1.exists()){
			                	tempfile1 = new File(System.getProperty("user.dir"), tempstr);
			                	if (!tempfile1.exists()){
			                       JOptionPane.showMessageDialog(new JFrame(), "Error: File " + tempfile1 + " does not exist.");
			                       continue;
			                	}
			                }
			                System.out.println("Load: "+ tempfile1.getName());
			                KeyValueList tempkvlist = XMLUtil.readFromXML(tempfile1);
			                if (tempkvlist != null){
			                	Remote.loadedmsgs.add(new LoadedMsg(Remote.loadedmsgs.size(), tempkvlist));
			                    initSIS.msglist.add(tempfile1.getName());
			                    System.out.println("add: " + tempfile1.getName());
			                }
			            }
			        }
			    }
			    catch (Exception localException) {}
			    
			    KeyValueList tempkvlist;
			    
			    if (initSIS.msglist.getItemCount() != 0){
			    	initSIS.msglist.select(0);
			    	tempkvlist = ((LoadedMsg)Remote.loadedmsgs.get(0)).msg;
			        Remote.currentloadedmsg = (LoadedMsg)Remote.loadedmsgs.get(0);
			    }
			    else{
			    	tempkvlist = null;
			        JOptionPane.showMessageDialog(new JFrame(), "XXXXXXXXX Error: No File loaded.");
			    }
			    initSIS.displayKvList(tempkvlist, "s");
			}
			// send init_xml 
			System.out.println("start sending init...");
			KeyValueList tkvlist = initSIS.readKvList("s");
			if (tkvlist == null) {
				return;
			}
			Remote.currentloadedmsg.msg = tkvlist;
			try{
				System.out.println("load message... " + Remote.loadedmsgs.size());
				for(int i = 0; i < Remote.loadedmsgs.size(); i++){
					KeyValueList kvlist = ((LoadedMsg)Remote.loadedmsgs.get(i)).msg;
					kvlist.prepareForSend();
					Remote.mEncoder.sendMsg(kvlist, Remote.serverconn.getOutputStream());
				}
			}
			catch (IOException localIOException) {}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Initializer(){
		this.msglist = new Choice();
		this.msglist.addItem("TestMsg");
		this.msglist.select(0);
		this.tablemodel1 = new DefaultTableModel();
		this.tablemodel1.addColumn("Key");
		this.tablemodel1.addColumn("Value");
		this.tablemodel2 = new DefaultTableModel();
		this.tablemodel2.addColumn("Key");
		this.tablemodel2.addColumn("Value");
	}
	
	synchronized void displayKvList(KeyValueList in, String flag){
		DefaultTableModel tempmodel;
	    if (flag.equalsIgnoreCase("r")){
	    	tempmodel = this.tablemodel2;
	    }
	    else{
	    	if (flag.equalsIgnoreCase("s")) {
	    		tempmodel = this.tablemodel1;
	    	} 
	    	else {
	    		return;
	    	}
	    }
	    int rowcount = tempmodel.getRowCount();
	    System.out.println("Pass!!!!!!  "+rowcount);
	    for(int i = 0; i < rowcount; i++) {
	    	tempmodel.removeRow(0);
	    }
	    if (in == null) {
	    	return;
	    }
	    System.out.println("size of in: " + in.size());
	    for(int i = 0; i < in.size(); i++) {
	    	tempmodel.addRow(new String[] { in.keyAt(i), in.valueAt(i) });
	    }
	}
	   
	synchronized KeyValueList readKvList(String flag){
		KeyValueList result = null;
		DefaultTableModel tempmodel;
		if(flag.equalsIgnoreCase("r")){
			tempmodel = this.tablemodel2;
		}
		else{
			if(flag.equalsIgnoreCase("s")) {
				tempmodel = this.tablemodel1;
			}
			else {
				return null;
			}
		}

		result = new KeyValueList();
		int rowcount = tempmodel.getRowCount();
	    	for (int i = 0; i < rowcount; i++)
		     {
		       String temp1 = (String)tempmodel.getValueAt(i, 0);
		       String temp2 = (String)tempmodel.getValueAt(i, 1);
		       temp1 = temp1.trim();
		       temp2 = temp2.trim();
		       if ((temp1.indexOf('$') != -1) || (temp2.indexOf('$') != -1))
		       {
		         JOptionPane.showMessageDialog(new JFrame(), "No '$' character allowed in any key or value.\nError: Line " + String.valueOf(i + 1));
		         return null;
		       }
		       if ((temp1.length() == 0) && (temp2.length() != 0))
		       {
		         JOptionPane.showMessageDialog(new JFrame(), "Value \"" + temp2 + "\" has no corresponding Key.\nError: Line " + String.valueOf(i + 1));
		         return null;
		       }
	       if ((temp2.length() == 0) && (temp1.length() != 0))
		       {
		         JOptionPane.showMessageDialog(new JFrame(), "Key \"" + temp2 + "\" has no corresponding Value.\nError: Line " + String.valueOf(i + 1));
		         return null;
		       }
		       if ((temp2.length() != 0) && (temp1.length() != 0)) {
		    	   result.addPair(temp1, temp2);
	       }
		     }
		     if (result.size() == 0)
		     {
		       JOptionPane.showMessageDialog(new JFrame(), "Empty Message.");
		       return null;
		     }
		     return result;
		   }
	
	
}
