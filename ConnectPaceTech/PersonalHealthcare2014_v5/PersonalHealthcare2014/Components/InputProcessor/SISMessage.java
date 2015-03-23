//package system;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * SIS message: key-value string delimited by "$$$"
 * 
 * @author Jerry Ye (Yinglin Sun)
 *
 */
public class SISMessage {
	
	private Map<String, String> attrs = new LinkedHashMap<String, String>();
	
	public SISMessage() {}
	
	public SISMessage(String msg) {
		/* Parse message */
		StringTokenizer tokenizer = new StringTokenizer(msg, "$$$");
		while(tokenizer.hasMoreTokens()) {
			String key = tokenizer.nextToken();
			String value = tokenizer.nextToken();
			attrs.put(key, value);
		}
	}
	
	/**
	 * Add an attribute
	 * @param name attribute name
	 * @param value attribute value
	 */
	public void addAttr(String name, String value) {
		attrs.put(name, value);
	}
	
	/**
	 * Get attribute value
	 * @param key attribute name
	 * @return attribute value
	 */
	public String getAttr(String key) {
		return attrs.get(key);
	}

	/**
	 * @return message ID
	 */
	public int getID() {
		return Integer.valueOf(attrs.get("MsgID"));
	}
	
	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		
		Iterator<Map.Entry<String, String>> ite = attrs.entrySet().iterator();
		while(ite.hasNext()) {
			Map.Entry<String, String> entry = ite.next();
			sbuf.append(entry.getKey()).append("$$$").append(entry.getValue()).append("$$$");
		}
		sbuf.delete(sbuf.length()-3, sbuf.length());
		
		return sbuf.toString();
	}
	
	public String getFormatString() {
		StringBuffer sbuf = new StringBuffer();
		
		Iterator<Map.Entry<String, String>> ite = attrs.entrySet().iterator();
		while(ite.hasNext()) {
			Map.Entry<String, String> entry = ite.next();
			sbuf.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
		}
		
		return sbuf.toString();
	}
}
