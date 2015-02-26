import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.*;

class g9aWrap extends KeyValueList
{
	public g9aWrap(String xmlpath)
	{
		super();
		try
		{
			DocumentBuilderFactory aDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder aDocumentBuilder = aDocumentBuilderFactory.newDocumentBuilder();
			Document aDocument = aDocumentBuilder.parse(new File(xmlpath));
			
			Element DocElement = aDocument.getDocumentElement();
			NodeList aNodeList;
			NodeList KeyNodeList;
			NodeList ValueNodeList;
			
			aNodeList = DocElement.getElementsByTagName("MsgID");
			addPair("MsgID", aNodeList.item(0).getFirstChild().getNodeValue());
			aNodeList = DocElement.getElementsByTagName("Description");
			addPair("Description", aNodeList.item(0).getFirstChild().getNodeValue());
			aNodeList = DocElement.getElementsByTagName("Item");
			for (int i=0;i<aNodeList.getLength();i++)
			{
				KeyNodeList = ((Element)aNodeList.item(i)).getElementsByTagName("Key");
				ValueNodeList = ((Element)aNodeList.item(i)).getElementsByTagName("Value");
				addPair(KeyNodeList.item(0).getFirstChild().getNodeValue(), ValueNodeList.item(0).getFirstChild().getNodeValue());
			}
			toString();
		}
		catch (Exception e)
		{
			System.out.println("Error");
			e.printStackTrace();
		}
	}
	
	public static void main(String []args)
	{
		g9aWrap kvlWrap = new g9aWrap("26.xml");
//		g9aWrap kvlWrap = new g9aWrap("g9aAttemptServerConnection23.XML");
	}
}