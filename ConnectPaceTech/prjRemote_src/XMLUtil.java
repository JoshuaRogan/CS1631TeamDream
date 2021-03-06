/*  1:   */ import java.io.File;
/*  2:   */ import java.io.FileWriter;
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.PrintWriter;
/*  5:   */ import javax.xml.parsers.DocumentBuilder;
/*  6:   */ import javax.xml.parsers.DocumentBuilderFactory;
/*  7:   */ import org.w3c.dom.Document;
/*  8:   */ import org.w3c.dom.Element;
/*  9:   */ import org.w3c.dom.Node;
/* 10:   */ import org.w3c.dom.NodeList;
/* 11:   */ 
/* 12:   */ class XMLUtil
/* 13:   */ {
/* 14:   */   public static void writeToXML(KeyValueList in1, File in2)
/* 15:   */     throws IOException
/* 16:   */   {
/* 17:17 */     if ((in1 == null) || (in1.size() == 0)) {
/* 18:18 */       return;
/* 19:   */     }
/* 20:20 */     if (in2.exists()) {
/* 21:21 */       in2.delete();
/* 22:   */     }
/* 23:22 */     PrintWriter pw = new PrintWriter(new FileWriter(in2));
/* 24:23 */     pw.println("<?xml version=\"1.0\" standalone=\"yes\"?>");
/* 25:24 */     pw.println("<!--Generated by prjRemote Java Version 1.0-->");
/* 26:25 */     pw.println("<Msg>");
/* 27:26 */     pw.println("\t<Head>");
/* 28:27 */     String temp = in1.getValue("MsgID");
/* 29:28 */     if (temp == null) {
/* 30:29 */       temp = "";
/* 31:   */     }
/* 32:30 */     pw.println("\t\t<MsgID>" + temp + "</MsgID>");
/* 33:31 */     temp = in1.getValue("Description");
/* 34:32 */     if (temp == null) {
/* 35:33 */       temp = "";
/* 36:   */     }
/* 37:34 */     pw.println("\t\t<Description>" + temp + "</Description>");
/* 38:35 */     pw.println("\t</Head>");
/* 39:36 */     pw.println("\t<Body>");
/* 40:37 */     for (int i = 0; i < in1.size(); i++) {
/* 41:39 */       if ((!in1.keyAt(i).equals("MsgID")) && (!in1.keyAt(i).equals("Description"))) {
/* 42:41 */         pw.println("\t\t<Item><Key>" + in1.keyAt(i) + "</Key><Value>" + in1.valueAt(i) + "</Value></Item>");
/* 43:   */       }
/* 44:   */     }
/* 45:44 */     pw.println("\t</Body>");
/* 46:45 */     pw.println("</Msg>");
/* 47:46 */     pw.flush();
/* 48:47 */     pw.close();
/* 49:   */   }
/* 50:   */   
/* 51:   */   public static KeyValueList readFromXML(File in)
/* 52:   */   {
/* 53:52 */     KeyValueList result = new KeyValueList();
/* 54:   */     try
/* 55:   */     {
/* 56:55 */       DocumentBuilderFactory aDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
/* 57:56 */       DocumentBuilder aDocumentBuilder = aDocumentBuilderFactory.newDocumentBuilder();
/* 58:57 */       Document aDocument = aDocumentBuilder.parse(in);
/* 59:   */       
/* 60:59 */       Element DocElement = aDocument.getDocumentElement();
/* 61:   */       
/* 62:   */ 
/* 63:   */ 
/* 64:63 */       NodeList aNodeList = DocElement.getElementsByTagName("MsgID");
/* 65:64 */       if (!aNodeList.item(0).hasChildNodes()) {
/* 66:65 */         result.addPair("MsgID", "");
/* 67:   */       } else {
/* 68:67 */         result.addPair("MsgID", aNodeList.item(0).getFirstChild().getNodeValue());
/* 69:   */       }
/* 70:68 */       aNodeList = DocElement.getElementsByTagName("Description");
/* 71:69 */       if (!aNodeList.item(0).hasChildNodes()) {
/* 72:70 */         result.addPair("Description", "");
/* 73:   */       } else {
/* 74:72 */         result.addPair("Description", aNodeList.item(0).getFirstChild().getNodeValue());
/* 75:   */       }
/* 76:73 */       aNodeList = DocElement.getElementsByTagName("Item");
/* 77:74 */       for (int i = 0; i < aNodeList.getLength(); i++)
/* 78:   */       {
/* 79:76 */         NodeList KeyNodeList = ((Element)aNodeList.item(i)).getElementsByTagName("Key");
/* 80:77 */         NodeList ValueNodeList = ((Element)aNodeList.item(i)).getElementsByTagName("Value");
/* 81:78 */         result.addPair(KeyNodeList.item(0).getFirstChild().getNodeValue(), ValueNodeList.item(0).getFirstChild().getNodeValue());
/* 82:   */       }
/* 83:   */     }
/* 84:   */     catch (Exception localException) {}
/* 85:82 */     if (result.size() == 0) {
/* 86:83 */       return null;
/* 87:   */     }
/* 88:84 */     return result;
/* 89:   */   }
/* 90:   */ }


/* Location:           C:\Users\admin.sis\Desktop\Jerry's workspace\ConnectPaceTech\prjRemote\
 * Qualified Name:     XMLUtil
 * JD-Core Version:    0.7.0.1
 */