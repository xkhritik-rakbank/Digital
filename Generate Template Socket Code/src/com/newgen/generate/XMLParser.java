/*
---------------------------------------------------------------------------------------------------------
                  NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group                   : Application - Projects
Project/Product			: STP (V1.0) 
Application				: STP Hold
Module					: STP Hold Case Processing   
File Name				: ReadIni.java
Author 					: Ajay Kumar
Date (DD/MM/YYYY)		: 29/06/2009
Description 			: Contains the methods for parsng a XML file.
---------------------------------------------------------------------------------------------------------
                 	CHANGE HISTORY
---------------------------------------------------------------------------------------------------------

Problem No/CR No        Change Date           Changed By             Change Description
---------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------
*/


package com.newgen.generate;

import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
/**
 * <p>Title: STP</p>
 * <p>Description: Contains the methods for parsng a XML file.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Newgen Software Technologies Ltd.</p>
 * @author Garima Agarwal
 * @version 1.0
 */
public class XMLParser {
	
	
	
	private String parseString;
	private String copyString;
	private int IndexOfPrevSrch;
	
	public XMLParser() {
	}
	
	public XMLParser(String parseThisString) {
		copyString = new String(parseThisString);
		parseString = toUpperCase(copyString, 0, 0);
	}
	
	public void setInputXML(String ParseThisString) {
		if (ParseThisString != null) {
			copyString = new String(ParseThisString);
			parseString = toUpperCase(copyString, 0, 0);
			IndexOfPrevSrch = 0;
		} else {
			parseString = null;
			copyString = null;
			IndexOfPrevSrch = 0;
		}
	}
	
	public String getServiceName() {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getServiceName");
		try {
			return new String(copyString.substring(parseString.indexOf(
					toUpperCase(
							"<Option>", 0, 0)) +
							(new String(toUpperCase("<Option>",
									0, 0))).length(),
									parseString.indexOf(toUpperCase(
											"</Option>", 0, 0))));
		} catch (StringIndexOutOfBoundsException lExcp) {
		
			throw lExcp;
		}
	}
	
	public String getServiceName(char chr) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getServiceName");
		try {
			if (chr == 'A') {
				return new String(copyString.substring(parseString.indexOf(
						"<AdminOption>".toUpperCase()) +
						(new String("<AdminOption>".
								toUpperCase())).length(),
								parseString.indexOf(
										"</AdminOption>".toUpperCase())));
			} else {
				return "";
			}
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
			return "NoServiceFound";
		}
	}
	
	public boolean validateXML() {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.validateXML");
		try {
			return parseString.indexOf("<?xml version=\"1.0\"?>".toUpperCase()) !=
				-1;
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
			return false;
		}
	}
	
	public String getValueOf(String valueOf) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getValueOf");
		try {
			return new String(copyString.substring(parseString.indexOf("<" +
					toUpperCase(valueOf, 0, 0) + ">") + valueOf.length() + 2,
					parseString.
					indexOf("</" +
							toUpperCase(valueOf, 0, 0) + ">")));
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
		
			return "";
		}
	}
	
	public String getValueOf(String valueOf, String type) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getValueOf");
		try {
			if ("Binary".equalsIgnoreCase(type)) {
				int startPos = copyString.indexOf("<" + valueOf + ">");
				if (startPos == -1) {
					return "";
				} else {
					int endPos = copyString.lastIndexOf("</" + valueOf + ">");
					startPos += (new String("<" + valueOf + ">")).length();
					return copyString.substring(startPos, endPos);
				}
			} else {
				return "";
			}
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
		
			return "";
		}
	}
	
	public String getValueOf(String valueOf, boolean fromlast) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getValueOf");
		try {
			if (fromlast) {
				return new String(copyString.substring(parseString.indexOf("<" +
						toUpperCase(valueOf, 0, 0) + ">") + valueOf.length() +
						2,
						parseString.lastIndexOf("</" +
								toUpperCase(valueOf, 0, 0) +
						">")));
			} else {
				return new String(copyString.substring(parseString.indexOf("<" +
						toUpperCase(valueOf, 0, 0) + ">") + valueOf.length() +
						2,
						parseString.indexOf("</" +
								toUpperCase(valueOf, 0, 0) + ">")));
			}
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
		
			return "";
		}
	}
	
	public String getValueOf(String valueOf, int start, int end) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getValueOf");
		try {
			if (start >= 0) {
				int endIndex = parseString.indexOf("</" +
						toUpperCase(valueOf, 0, 0) +
						">", start);
				if (endIndex > start && (end == 0 || end >= endIndex)) {
					return new String(copyString.substring(parseString.indexOf(
							"<" +
							toUpperCase(valueOf, 0, 0) + ">", start) +
							valueOf.length() + 2,
							endIndex));
				}
			}
			return "";
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
			return "";
		}
	}
	
	public int getStartIndex(String tag, int start, int end) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getStartIndex");
		try {
			if (start >= 0) {
				int startIndex = parseString.indexOf("<" +
						toUpperCase(tag, 0, 0) + ">",
						start);
				if (startIndex >= start && (end == 0 || end >= startIndex)) {
					return startIndex + tag.length() + 2;
				}
			}
			return -1;
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
			return -1;
		}
	}
	
	public int getEndIndex(String tag, int start, int end) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getEndIndex");
		try {
			if (start >= 0) {
				int endIndex = parseString.indexOf("</" + toUpperCase(tag, 0, 0) +
						">",
						start);
				if (endIndex > start && (end == 0 || end >= endIndex)) {
					return endIndex;
				}
			}
			return -1;
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
			return -1;
		}
	}
	
	public int getTagStartIndex(String tag, int start, int end) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getTagStartIndex");
		try {
			if (start >= 0) {
				int startIndex = parseString.indexOf("<" +
						toUpperCase(tag, 0, 0) + ">",
						start);
				if (startIndex >= start && (end == 0 || end >= startIndex)) {
					return startIndex;
				}
			}
			return -1;
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
			return -1;
		}
	}
	
	public int getTagEndIndex(String tag, int start, int end) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getTagEndIndex");
		try {
			if (start >= 0) {
				int endIndex = parseString.indexOf("</" + toUpperCase(tag, 0, 0) +
						">",
						start);
				if (endIndex > start && (end == 0 || end >= endIndex)) {
					return endIndex + tag.length() + 3;
				}
			}
			return -1;
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
			return -1;
		}
	}
	
	public String getFirstValueOf(String valueOf) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getFirstValueOf");
		try {
			IndexOfPrevSrch = parseString.indexOf("<" + toUpperCase(valueOf, 0, 0) + ">");
			return new String(copyString.substring(IndexOfPrevSrch +
					valueOf.length() +
					2,
					parseString.indexOf("</" +
							toUpperCase(valueOf, 0, 0) + ">")));
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
			return "";
		}
	}
	
	public String getFirstValueOf(String valueOf, int start) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getFirstValueOf");
		try {
			IndexOfPrevSrch = parseString.indexOf("<" +
					toUpperCase(valueOf, 0, 0) +
					">", start);
			return new String(copyString.substring(IndexOfPrevSrch +
					valueOf.length() +
					2,
					parseString.indexOf("</" +
							toUpperCase(valueOf, 0, 0) + ">", start)));
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
		
			return "";
		}
	}
	
	public String getNextValueOf(String valueOf) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getNextValueOf");
		try {
			IndexOfPrevSrch = parseString.indexOf("<" +
					toUpperCase(valueOf, 0, 0) +
					">",
					IndexOfPrevSrch +
					valueOf.length() +
					2);
			return new String(copyString.substring(IndexOfPrevSrch +
					valueOf.length() +
					2,
					parseString.indexOf("</" +
							toUpperCase(valueOf, 0, 0) + ">",
							IndexOfPrevSrch)));
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
			return "";
		}
	}
	
	public int getNoOfFields(String tag) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getNoOfFields");
		int noOfFields = 0;
		int beginPos = 0;
		try {
			for (tag = toUpperCase(tag, 0, 0) + ">";
			parseString.indexOf("<" + tag, beginPos) != -1;
			beginPos += tag.length() + 2) {
				noOfFields++;
				beginPos = parseString.indexOf("</" + tag, beginPos);
				if (beginPos == -1) {
					break;
				}
			}
			
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
		}
		return noOfFields;
	}
	
	public int getNoOfFields(String tag, int startPos, int endPos) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getNoOfFields");
		int noOfFields = 0;
		int beginPos = startPos;
		try {
			for (tag = toUpperCase(tag, 0, 0) + ">";
			parseString.indexOf("<" + tag, beginPos) != -1 &&
			(beginPos < endPos || endPos == 0); ) {
				beginPos = parseString.indexOf("</" + tag, beginPos) +
				tag.length() + 2;
				if (beginPos != -1 && (beginPos <= endPos || endPos == 0)) {
					noOfFields++;
				}
			}
			
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
		}
		return noOfFields;
	}
	
	public String convertToSQLString(String strName) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.convertToSQLString");
		try {
			for (int count = strName.indexOf("["); count != -1;
			count = strName.indexOf("[", count + 2)) {
				strName = strName.substring(0, count) + "[[]" +
				strName.substring(count + 1, strName.length());
				
			}
		} catch (Exception lobjExcp) {
		
		}
		try {
			for (int count = strName.indexOf("_"); count != -1;
			count = strName.indexOf("_", count + 2)) {
				strName = strName.substring(0, count) + "[_]" +
				strName.substring(count + 1, strName.length());
				
			}
		} catch (Exception lobjExcp1) {
			
			}
		try {
			for (int count = strName.indexOf("%"); count != -1;
			count = strName.indexOf("%", count + 2)) {
				strName = strName.substring(0, count) + "[%]" +
				strName.substring(count + 1, strName.length());
				
			}
		} catch (Exception lobjExcp2) {
			
		}
		strName = strName.replace('?', '_');
		return strName;
	}
	
	public String getValueOf(String valueOf, String type, int from, int end) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.getNoOfFields");
		try {
			if ("Binary".equalsIgnoreCase(type)) {
				int startPos = copyString.indexOf("<" + valueOf + ">", from);
				if (startPos == -1) {
					return "";
				}
				int endPos = copyString.indexOf("</" + valueOf + ">", from);
				if (endPos > end) {
					return "";
				} else {
					startPos += (new String("<" + valueOf + ">")).length();
					return copyString.substring(startPos, endPos);
				}
			} else {
				return "";
			}
		} catch (StringIndexOutOfBoundsException
				lStringIOBExcp) {
			
			return "";
		}
	}
	
	public String toUpperCase(String valueOf, int begin, int end) throws
	StringIndexOutOfBoundsException {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.toUpperCase");
		String returnStr = "";
		try {
			int count = valueOf.length();
			char strChar[] = new char[count];
			valueOf.getChars(0, count, strChar, 0);
			while (count-- > 0) {
				strChar[count] = Character.toUpperCase(strChar[count]);
			}
			returnStr = new String(strChar);
		} catch (ArrayIndexOutOfBoundsException lobjArrayIOBExcp) {
			
		}
		return returnStr;
	}
	
	public String changeValue(String ParseString, String TagName,
			String NewValue) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.changeValue");
		try {
			String ParseStringTmp = ParseString.toUpperCase();
			String StrTag = (new String("<" + TagName + ">")).toUpperCase();
			int StartIndex = ParseStringTmp.indexOf(StrTag) + StrTag.length();
			int EndIndex = ParseStringTmp.indexOf((new String("</" + TagName + ">")).toUpperCase());
			String RetStr = ParseString.substring(0, StartIndex);
			RetStr = RetStr + NewValue + ParseString.substring(EndIndex);
			return RetStr;
		} catch (Exception lobjExcp) {
			
			return "";
		}
	}
	
	public void changeValue(String TagName, String NewValue) {
		String lExceptionId = new String("com.newgen.srvr.xml.XMLParser.changeValue");
		try {
			String StrTag = ("<" + TagName + ">").toUpperCase();
			int StartIndex = parseString.indexOf(StrTag);
			if (StartIndex > -1) {
				StartIndex += StrTag.length();
				int EndIndex = parseString.indexOf(("</" + TagName + ">").
						toUpperCase());
				String RetStr = copyString.substring(0, StartIndex);
				copyString = RetStr + NewValue + copyString.substring(EndIndex);
			} else {
				int EndIndex = parseString.lastIndexOf("</"); 
					StartIndex = parseString.lastIndexOf("</");
				String RetStr = copyString.substring(0, StartIndex);
				copyString = RetStr + "<" + TagName + ">" + NewValue + "</" +
				TagName +
				">" + copyString.substring(EndIndex);
			}
			parseString = toUpperCase(copyString, 0, 0);
		} catch (Exception lobjExcp) {
			
		}
	}
	
	public String toString() {
		return copyString;
	}
	
	public static String getTagValue(String xml,String tag) throws ParserConfigurationException, SAXException, IOException
	{
		Document doc=getDocument(xml);
		NodeList nodeList = doc.getElementsByTagName(tag);

		int length = nodeList.getLength();

		if (length > 0)
		{
			Node node =  nodeList.item(0);
			
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				NodeList childNodes = node.getChildNodes();
				String value = "";
				int count = childNodes.getLength();
				for (int i = 0; i < count; i++)
				{
					Node item = childNodes.item(i);
					if (item.getNodeType() == Node.TEXT_NODE)
					{
						value += item.getNodeValue();
					}
				}
				return value;
			}
			else if (node.getNodeType() == Node.TEXT_NODE)
			{
				return node.getNodeValue();
			}
		}
		return "";
	}
	
	public static List<List<String>> getTagValueCAM(String xml) throws ParserConfigurationException, SAXException, IOException
	{
		Document doc=getDocument(xml.replaceAll("&", "#andsymb#"));
		NodeList nodeList = doc.getElementsByTagName("Record");

		int length = nodeList.getLength();
		List<List<String>> rows=null;
		if(length>0) {
			 rows= new ArrayList<List<String>>();
			for(int i=0;i<length;i++) {
				Node n = nodeList.item(i);
				NodeList chilnodes = n.getChildNodes();
				List<String> dataValues = new ArrayList<String>();
				for(int j=0;j<chilnodes.getLength();j++) {
					Node cn = chilnodes.item(j);
					dataValues.add((cn.getTextContent()).replaceAll("#andsymb#", "&"));
				}
				rows.add(dataValues);
			}
		}
		
		return rows;
	}
	
	
	public static Document getDocument(String xml) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(xml)));
		return doc;
	}

	/**
	 * mLogger
	 * @return Logger
	 */

	
}