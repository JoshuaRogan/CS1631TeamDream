
import java.io.*;
import java.net.*;
import java.util.*;

public class SISComponentBase implements ComponentBase
{
	private Socket client;
	static String curevacomp = null;//current evaluated comp
	static String curevaresult = null;
    
    SISComponentBase(Socket cl)
	{
		client = cl;
    }
    
    synchronized public void processMsg(KeyValueList kvList) throws Exception
	{
    	int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
    	ArrayList<Socket> sendlist;
    	ArrayList<String> inmsgs, outmsgs;
    	CompMsgControl tempcmc;
    	String tempstr;
    	KeyValueList sysList;
    	ArrayList<String> eliminatelist;
    	
		switch(MsgID)
		{
		case 20://Add Comp Msg Control List, Unrecognized component cannot be connected
			tempstr = kvList.getValue("Name");
			if(!SISServer.CompExistCMC(tempstr))
			{
				tempcmc = new CompMsgControl();
				tempcmc.compname = tempstr;
				inmsgs = kvList.getValueLike("InputMsgID");
				outmsgs = kvList.getValueLike("OutputMsgID");
				if(inmsgs!=null)
					for(int i=0; i<inmsgs.size(); i++)
						tempcmc.InMsgs.add(Integer.parseInt(inmsgs.get(i)));
				if(outmsgs!=null)
					for(int i=0; i<outmsgs.size(); i++)
						tempcmc.OutMsgs.add(Integer.parseInt(outmsgs.get(i)));
				SISServer.cmc.add(tempcmc);
				SISServer.createhistory.add(kvList);
			}
			else
				System.out.println("Definition of component "+tempstr+" already exists.");
			break;
		case 21://Add Comp Msg Control List, Unrecognized component cannot be connected
			tempstr = kvList.getValue("Name");
			if(!SISServer.CompExistCMC(tempstr))
			{
				tempcmc = new CompMsgControl();
				tempcmc.compname = tempstr;
				inmsgs = kvList.getValueLike("InputMsgID");
				outmsgs = kvList.getValueLike("OutputMsgID");
				if(inmsgs!=null)
					for(int i=0; i<inmsgs.size(); i++)
						tempcmc.InMsgs.add(Integer.parseInt(inmsgs.get(i)));
				if(outmsgs!=null)
					for(int i=0; i<outmsgs.size(); i++)
						tempcmc.OutMsgs.add(Integer.parseInt(outmsgs.get(i)));
				SISServer.cmc.add(tempcmc);
				SISServer.createhistory.add(kvList);
			}
			else
				System.out.println("Definition of component "+tempstr+" already exists.");
			break;
		case 22:
			System.out.println("Unexpected MsgID 22");
			break;
		case 24:
			System.out.println("Unexpected MsgID 24");
			break;
		case 25:
			System.out.println("Unexpected MsgID 25");
			break;
		case 26:
			System.out.println("Unexpected MsgID 26");
			break;
		case 27:
			sysList = new KeyValueList();
			sysList.addPair("MsgID","28");
			sysList.addPair("Description","Response to Msg 27: Check Active Connection Status");
			sysList.addPair("CompName",kvList.getValue("CompName"));
			sendlist = SISServer.FindActiveSocketCSM(kvList.getValue("CompName"));
			if(sendlist == null)
				sysList.addPair("Connection","no");
			else
				sysList.addPair("Connection","yes");
			sendlist = SISServer.FindActiveSocketCSM(kvList.getValue("Name"));
			if(sendlist != null)
				for(int j=0; j<sendlist.size(); j++)
					SendToComponent(sysList, sendlist.get(j));
			break;
		case 202://Personal Health Care KB Response
			if(kvList.getValue("Inquirer").equals("SISServer"))
			{
				for(int i=0; i<SISServer.cmc.size(); i++)
				{
					if(SISServer.cmc.get(i).InMsgs.contains(MsgID) && SISServer.cmc.get(i).compname.equals("Verifier"))
					{
						sendlist = SISServer.FindActiveSocketCSM(SISServer.cmc.get(i).compname);
						if(sendlist != null)
							for(int j=0; j<sendlist.size(); j++)
								SendToComponent(kvList, sendlist.get(j));
					}
			}
			}
			for(int i=0; i<SISServer.cmc.size(); i++)
			{
				if(SISServer.cmc.get(i).InMsgs.contains(MsgID) && kvList.getValue("Inquirer").equals(SISServer.cmc.get(i).compname))
				{
					sendlist = SISServer.FindActiveSocketCSM(SISServer.cmc.get(i).compname);
					if(sendlist != null)
						for(int j=0; j<sendlist.size(); j++)
							SendToComponent(kvList, sendlist.get(j));
				}
			}
			break;
		case 303://Personal Health Care Elimination Results
			ArrayList<String> activatecomplist, deactivatecomplist;
			activatecomplist = kvList.getValueLike("ActiveComp");
			deactivatecomplist = kvList.getValueLike("DeactiveComp");
			if(activatecomplist!=null)
				for(int i=0; i<activatecomplist.size(); i++)
					SISServer.TurnOn(activatecomplist.get(i));
			if(deactivatecomplist!=null)
				for(int i=0; i<deactivatecomplist.size(); i++)
					SISServer.TurnOff(deactivatecomplist.get(i));
			break;
		case 512:
			eliminatelist = kvList.getValueLike("EliminateComp");
			if(eliminatelist!=null)
				for(int i=0; i<eliminatelist.size(); i++)
					SISServer.EliminateComp(eliminatelist.get(i));
			if(kvList.getValue("FinalRound").equalsIgnoreCase("yes"))
			{
				System.out.println("SIS/SIA "+kvList.getValue("Algorithm")+" Algorithm Running Complete. See Eliminator for Best Comp Info.");
			}
			for(int i=0; i<SISServer.cmc.size(); i++)
			{
				if(SISServer.cmc.get(i).InMsgs.contains(MsgID))
				{
					sendlist = SISServer.FindActiveSocketCSM(SISServer.cmc.get(i).compname);
					if(sendlist != null)
						for(int j=0; j<sendlist.size(); j++)
							SendToComponent(kvList, sendlist.get(j));
				}
			}
			break;
		case 515:
			//SISServer.EliminateAll();
			System.out.println("SIS/SIA First Cycle Running Complete. See Eliminator for Best Algorithm Info.");
			for(int i=0; i<SISServer.cmc.size(); i++)
			{
				if(SISServer.cmc.get(i).InMsgs.contains(MsgID))
				{
					sendlist = SISServer.FindActiveSocketCSM(SISServer.cmc.get(i).compname);
					if(sendlist != null)
						for(int j=0; j<sendlist.size(); j++)
							SendToComponent(kvList, sendlist.get(j));
				}
			}
			break;
		case 529:
		case 527:
			SISServer.EliminateAll();
			System.out.println("SIS/SIA Running Complete.");
			for(int i=0; i<SISServer.cmc.size(); i++)
			{
				if(SISServer.cmc.get(i).InMsgs.contains(MsgID))
				{
					sendlist = SISServer.FindActiveSocketCSM(SISServer.cmc.get(i).compname);
					if(sendlist != null)
						for(int j=0; j<sendlist.size(); j++)
							SendToComponent(kvList, sendlist.get(j));
				}
			}
			break;
		case 632:
			eliminatelist = kvList.getValueLike("DeadComp");
			if(eliminatelist!=null)
				for(int i=0; i<eliminatelist.size(); i++)
					SISServer.EliminateComp(eliminatelist.get(i));
			for(int i=0; i<SISServer.cmc.size(); i++)
			{
				if(SISServer.cmc.get(i).InMsgs.contains(MsgID))
				{
					sendlist = SISServer.FindActiveSocketCSM(SISServer.cmc.get(i).compname);
					if(sendlist != null)
						for(int j=0; j<sendlist.size(); j++)
							SendToComponent(kvList, sendlist.get(j));
				}
			}
			break;
		case 612:
			SISServer.EliminateAllwithSysComps();
			System.out.println("SIS system running complete.");
			for(int i=0; i<SISServer.cmc.size(); i++)
			{
				if(SISServer.cmc.get(i).InMsgs.contains(MsgID))
				{
					sendlist = SISServer.FindActiveSocketCSM(SISServer.cmc.get(i).compname);
					if(sendlist != null)
						for(int j=0; j<sendlist.size(); j++)
							SendToComponent(kvList, sendlist.get(j));
				}
			}
			break;
		case 613:
			SISServer.EliminateAll();
			for(int i=0; i<SISServer.cmc.size(); i++)
			{
				if(SISServer.cmc.get(i).InMsgs.contains(MsgID))
				{
					sendlist = SISServer.FindActiveSocketCSM(SISServer.cmc.get(i).compname);
					if(sendlist != null)
						for(int j=0; j<sendlist.size(); j++)
							SendToComponent(kvList, sendlist.get(j));
				}
			}
			break;
		default:
			for(int i=0; i<SISServer.cmc.size(); i++)
			{
				if(SISServer.cmc.get(i).InMsgs.contains(MsgID))
				{
					sendlist = SISServer.FindActiveSocketCSM(SISServer.cmc.get(i).compname);
					if(sendlist != null)
						for(int j=0; j<sendlist.size(); j++)
							SendToComponent(kvList, sendlist.get(j));
				}
			}
			break;
		}
  }

	public void SendToComponent(KeyValueList kvList, Socket component)
	{
		try
		{
			MsgEncoder mEncoder = new MsgEncoder();
			if(component != null)
				mEncoder.sendMsg(kvList, component.getOutputStream());
		}
		catch(Exception ex)
		{ System.out.println("Problem sending to component!");}
	}
}

