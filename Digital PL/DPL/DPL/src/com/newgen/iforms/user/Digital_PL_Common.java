package com.newgen.iforms.user;



//--------- NEWGEN SOFTWARE TECHNOLOGIES LIMITED ------------------

//Group                                             : Application ï¿½Projects
//Product / Project                                	: EDigitalAOank Group Roll Out
//Module                                            :
//File Name                                         : Common_Function.java
//Author                                            : Piyush Bansal
//Date written (DD/MM/YYYY)          				: 01/Jan/2014
//Description                                       : Java For execution of various user commands and XMLs' which are common to all process

//---------------------------------------------------------------------------------


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.io.*;
import java.sql.Connection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFRun;

import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.newgen.XMLParser.XMLParser;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.xmlapi.IFormXmlResponse;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;
import com.newgen.wfdesktop.xmlapi.WFInputXml;

import ISPack.CPISDocumentTxn;
import ISPack.ISUtil.JPDBRecoverDocData;
import ISPack.ISUtil.JPISException;
import ISPack.ISUtil.JPISIsIndex;

//import com.newgen.iforms.user.*;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.omniforms.FormReference;
import com.newgen.omniforms.context.FormContext;

public class Digital_PL_Common 
{
	
	String 	sLocaleForMessage = java.util.Locale.getDefault().toString();

	public List<List<String>> getDataFromDB(IFormReference iformObj ,String query)
	{
		Digital_PL.mLogger.debug("Inside Done()--->query is: "+query);
		try{
			List<List<String>> result=iformObj.getDataFromDB(query);
			Digital_PL.mLogger.debug("Inside Done()---result:"+result);
			if(!result.isEmpty() && result.get(0)!=null){
				return result;
			}
		}
		catch(Exception e){
			Digital_PL.printException(e);
		}
		return null;

	}
	
	
	
	

	 public static String WFNGExecute(String ipXML, String jtsServerIP, String serverPort,int flag) throws IOException
	 {
	 	
	 	try{
	 		Digital_PL.mLogger.info("WFNGExecute() : "+ipXML+" - "+jtsServerIP+" - "+serverPort+" - "+flag);
	 	if(serverPort.startsWith("33")) {
	 		Digital_PL.mLogger.info("Inside if WFNGExecute() :");
	 		return WFCallBroker.execute(ipXML,jtsServerIP,Integer.parseInt(serverPort),1);
	 	}
	 	else
	 	{
	 		Digital_PL.mLogger.info("Inside else WFNGExecute() :");
	 		return NGEjbClient.getSharedInstance().makeCall(jtsServerIP,serverPort,"WebSphere",ipXML); 
	 	}
	 		//
	 	}
	 	catch(Exception e){
	 		Digital_PL.mLogger.info("Exception Occured in WF NG Execute : "+e.getMessage());	
	 		return "Error";
	 	}
	 	
	 }
	 
 
	 public static String apInsert(String sCabName, String sSessionId, String colNames, String colValues, String tableName)
		{
			StringBuffer ipXMLBuffer=new StringBuffer();

			ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
			ipXMLBuffer.append("<APInsertExtd_Input>\n");
			ipXMLBuffer.append("<Option>APInsert</Option>");
			ipXMLBuffer.append("<TableName>");
			ipXMLBuffer.append(tableName);
			ipXMLBuffer.append("</TableName>");
			ipXMLBuffer.append("<ColName>");
			ipXMLBuffer.append(colNames);
			ipXMLBuffer.append("</ColName>\n");
			ipXMLBuffer.append("<Values>");
			ipXMLBuffer.append(colValues);
			ipXMLBuffer.append("</Values>\n");
			ipXMLBuffer.append("<EngineName>");
			ipXMLBuffer.append(sCabName);
			ipXMLBuffer.append("</EngineName>\n");
			ipXMLBuffer.append("<SessionId>");
			ipXMLBuffer.append(sSessionId);
			ipXMLBuffer.append("</SessionId>\n");
			ipXMLBuffer.append("</APInsertExtd_Input>");

			return ipXMLBuffer.toString();
		}
	 
	 public static String get_NGOAddDocument_Input(String cabinetName, String sessionID, String folderIndex,String docSize,String DocumentName,String strISIndex, String volumeID, String filePath)
	 {
	 	String noOfPage = "1";
	 	
	 	String xml = "<?xml version=\"1.0\"?>" + "\n" +
	 	"<NGOAddDocument_Input>" + "\n" +
	 	"<Option>NGOAddDocument</Option>" + "\n" +
	 	"<CabinetName>" + cabinetName + "</CabinetName>" + "\n" +
	 	"<UserDBId>" + sessionID + "</UserDBId>" + "\n" +
	 	"<GroupIndex>0</GroupIndex>" + "\n" +
	 	"<ParentFolderIndex>" + folderIndex + "</ParentFolderIndex>" + "\n" +
	 	"<DocumentName>" + DocumentName + "</DocumentName>" + "\n" +
	 	"<CreatedByAppName>csv</CreatedByAppName>" + "\n" +
	 	"<Comment></Comment>" + "\n" +
	 	"<VersionComment></VersionComment>" + "\n" +
	 	"<VolumeIndex>" + volumeID + "</VolumeIndex>" + "\n" +
	 	"<FilePath>" + filePath + "</FilePath>" + "\n" +
	 	"<DataDefinition></DataDefinition>" + "\n" +
	 	"<ISIndex>" + strISIndex + "</ISIndex>" + "\n" +
	 	"<NoOfPages>"+noOfPage+"</NoOfPages>" + "\n" +
	 	"<DocumentType>N</DocumentType>" + "\n" +
	 	"<DocumentSize>" + docSize + "</DocumentSize>" + "\n" +
	 	"</NGOAddDocument_Input>";
	 	   
	 	return xml;   
	 }
	
	 public static void waiteloopExecute(long wtime)
	 {
	     try
	 	{
	         for (int i = 0; i < 10; i++)
	 		{
	             Thread.yield();
	             Thread.sleep(wtime / 10);
	         }
	     }
	 	catch (InterruptedException e)
	 	{
	 		Digital_PL.mLogger.info(e.toString());
	 	   Thread.currentThread().interrupt();
	     }
	 }
	 
	 public static String GetTagValue(String XML , String Tagname)
	 {
		 String starttag = "<"+Tagname+">";
		 String endtag = "</"+Tagname+">";
		 Digital_PL.mLogger.info("GetTagValue " + starttag);
		 if(XML.indexOf(starttag)>=0)
		 {
			 if("MATURITYDATE".equals(Tagname))
			 {
				 String date = XML.substring(XML.indexOf(starttag)+(starttag.length()),XML.indexOf(endtag));
				 return date.substring(6,8)+date.substring(4,6)+date.substring(0,4);
			 }
		 return XML.substring(XML.indexOf(starttag)+(starttag.length()),XML.indexOf(endtag));
		 }
		 else
		 {
			 return "";
		 }
	 }
			
	 		
//end here 
	
	public static String maskXmlTags(List<List<String>> outputMQXML,String Tag)
    {
        Pattern p = Pattern.compile("(?<="+Tag+")([-\\s\\w]*)((?:[a-zA-Z0-9][-_\\s]*){0})");
        Matcher m = p.matcher((CharSequence) outputMQXML);
        StringBuffer maskedResult = new StringBuffer();
        while (m.find()) 
        {
            String thisMask = m.group(1).replaceAll("[^-_\\s]", "*");
            m.appendReplacement(maskedResult, thisMask + "$2");
        }
        m.appendTail(maskedResult);
        return maskedResult.toString();
    }
	public String saveDataInDB(IFormReference iformObj ,String query)
	{
		Digital_PL.mLogger.debug("Inside Done()---Exception_Mail_ID->query is: "+query);
		try{
			int mainCode=iformObj.saveDataInDB(query);
			Digital_PL.mLogger.debug("Inside Done()---result:"+mainCode);		
			return mainCode+"";	
		}
		catch(Exception e){
			Digital_PL.printException(e);
		}
		return null;
	}
	
	//**********************************************************************************//
	//Description            	:Method to Trim Strings
	//**********************************************************************************//
	public String Trim(String str)
	{
		if(str==null) return str;
			int i=0, j=0;
		for (i=0;i < str.length();i++)
		{
			if (str.charAt(i) != ' ')
				break;
		}
		for (j=str.length() - 1;j>= 0; j--)
		{
			if (str.charAt(j) != ' ')
			break;
		}
		if (j < i) j = i-1; str = str.substring(i, j+1);
			return str;
	}
	
	public void enableControl(String strFields, IFormReference iformObj)
	{
		String arrFields[] = strFields.split(",");
		for(int idx=0;idx<arrFields.length;idx++)
		{
			try
			{			
				iformObj.getIFormControl(arrFields[idx]).getM_objControlStyle().setM_strEnable("true");
			}
			catch(Exception ex)
			{
				Digital_PL.printException(ex);
			}	
		}
	}
	//rubi
	public void control(String strFields, IFormReference iformObj,String styleType,String controlType)
	{
		Digital_PL.mLogger.info("insode control method .  Styletype :- "+styleType+" . Controltype :- "+controlType);
		String arrFields[] = strFields.split(",");
		for(int idx=0;idx<arrFields.length;idx++)
		{
			try
			{	
				Digital_PL.mLogger.info(arrFields[idx]);
				iformObj.setStyle(arrFields[idx], styleType, controlType);
				//iformObj.getIFormControl(arrFields[idx]).getM_objControlStyle().setM_strEnable("false");
			}
			catch(Exception ex)
			{
				Digital_PL.printException(ex);
			}	
		}
	}
	public void disableControl(String strFields, IFormReference iformObj)
	{
		String arrFields[] = strFields.split(",");
		for(int idx=0;idx<arrFields.length;idx++)
		{
			try
			{			
				iformObj.getIFormControl(arrFields[idx]).getM_objControlStyle().setM_strEnable("false");
			}
			catch(Exception ex)
			{
				Digital_PL.printException(ex);
			}	
		}
	}
	
	public void lockControl(String strFields, IFormReference iformObj)
	{
		String arrFields[] = strFields.split(",");
		for(int idx=0;idx<arrFields.length;idx++)
		{
			try
			{			
				iformObj.getIFormControl(arrFields[idx]).getM_objControlStyle().setM_strReadOnly("true");
			}
			catch(Exception ex)
			{
				Digital_PL.printException(ex);
			}	
		}
	}
	
	public void unlockControl(String strFields, IFormReference iformObj)
	{
		String arrFields[] = strFields.split(",");
		for(int idx=0;idx<arrFields.length;idx++)
		{
			try
			{			
				iformObj.getIFormControl(arrFields[idx]).getM_objControlStyle().setM_strReadOnly("false");
			}
			catch(Exception ex)
			{
				Digital_PL.printException(ex);
			}	
		}
	}
	
	public String getSessionId(IFormReference iformObj)
	{
		return ((iformObj).getObjGeneralData()).getM_strDMSSessionId();
	}
	
	public String getItemIndex(IFormReference iformObj)
	{
		return ((iformObj).getObjGeneralData()).getM_strFolderId();
	}
	
	public String getWorkitemName(IFormReference iformObj)
	{
		return ((iformObj).getObjGeneralData()).getM_strProcessInstanceId();
	}
	
	public void setControlValue(String controlName, String controlValue, IFormReference iformObj)
	{
		iformObj.setValue(controlName,controlValue);
	}
	
	public String getCabinetName(IFormReference iformObj)
	{
		return (String)iformObj.getCabinetName();	
	}
	
	public String getUserName(IFormReference iformObj)
	{
		return (String)iformObj.getUserName();			
	}
	
	public String getActivityName(IFormReference iformObj)
	{
		return (String)iformObj.getActivityName();		
	}
	
	public String getControlValue(String controlName, IFormReference iformObj)
	{
//		return (String)EventHandler.iFormOBJECT.getControlValue(controlName);
		return (String)iformObj.getValue(controlName);
	}
	
	public boolean isControValueEmpty(String controlName, IFormReference iformObj)
	{
		String controlValue = getControlValue(controlName, iformObj);
		
		if(controlValue ==null || controlValue.equals(""))
			return true;
		else
			return false;
	}
	
	//**********************************************************************************//
		//Description            	:Method to convert one Date Format into another with Locale
		//**********************************************************************************//
		public String convertDateFormat(String idate, String ipDateFormat,String opDateFormat, Locale... opLocale)
		{
			Locale defaultLocale = Locale.getDefault();
			
			Digital_PL.mLogger.debug("defaultLocale : "+defaultLocale);
			
			assert opLocale.length <= 1;		
			Locale opDateFmtLocale = opLocale.length > 0 ? opLocale[0] : defaultLocale;
			
			Digital_PL.mLogger.debug("Loacle for output Date : "+opDateFmtLocale);
			try
			{
				if(idate==null)
				{
					return "";
				}
				if(idate.equalsIgnoreCase(""))
				{
					return "";
				}
				Digital_PL.mLogger.debug("idate :"+idate);
				String odate="";
				DateFormat dfinput = new SimpleDateFormat(ipDateFormat);
				DateFormat dfoutput = new SimpleDateFormat(opDateFormat,opDateFmtLocale);

				Date dt=dfinput.parse(idate);
				Digital_PL.mLogger.debug("Indate "+dt);
				odate = dfoutput.format(dt);
				Digital_PL.mLogger.debug("Outdate "+odate);
				return odate;
			}
			catch(Exception e)
			{
				return "";
			}
		}
	
	//******************************************************
	//Description            	:Method to get current date
	//******************************************************
	public String getCurrentDate(String outputFormat)
	{
		String current_date="";
		try
		{
			java.util.Calendar dateCreated1 = java.util.Calendar.getInstance();
			java.text.DateFormat df2 = new java.text.SimpleDateFormat(outputFormat);
			current_date = df2.format(dateCreated1.getTime());
		}
		catch(Exception e)
		{
			System.out.println("Exception in getting Current date :" +e);
		}
		return current_date;
	}

	
	public String ExecuteQueryOnServer(String sInputXML, IFormReference iformObj)
	{
		 try
        {
            Digital_PL.mLogger.debug("Server Ip :"+iformObj.getServerIp());
            Digital_PL.mLogger.debug("Server Port :"+iformObj.getServerPort());
            Digital_PL.mLogger.debug("Input XML :"+sInputXML);
           
            return NGEjbClient.getSharedInstance().makeCall(iformObj.getServerIp(), iformObj.getServerPort() + "", "WebSphere", sInputXML);
        }
        catch(Exception excp)
        {
        	Digital_PL.mLogger.debug("Exception occured in executing API on server :\n"+excp);
        	Digital_PL.printException(excp);
            return "Exception occured in executing API on server :\n"+excp;
        }       
    }

	public String ExecuteQuery_APProcedure(String ProcName,String Params, IFormReference iformObj)
	{
		try{
			
			String sInputXML = "<?xml version=\"1.0\"?>" +"\n"+
	                "<APProcedure_Input>" +"\n"+
					"<Option>APProcedure</Option>" +"\n"+
					"<ProcName>" + ProcName + "</ProcName>" +"\n"+
					"<Params>" + Params + "</Params>" +"\n"+
					"<EngineName>" + getCabinetName(iformObj) + "</EngineName>" +"\n"+
					"<SessionId>" + getSessionId(iformObj)+ "</SessionId>" +"\n"+
	                "</APProcedure_Input>";
			
			Digital_PL.mLogger.debug("Inside ExecuteQuery_APProcedure() [Input xml] \n: "+sInputXML);

			return ExecuteQueryOnServer(sInputXML, iformObj);
		} 
		catch (Exception e) 
		{
			Digital_PL.printException(e);
			return "";
		}			
	}	
	
	public String ExecuteQuery_APSelect(String sQuery, IFormReference iformObj)
	{
		try{
			WFInputXml wfInputXml = new WFInputXml();

			wfInputXml.appendStartCallName("APSelect", "Input");
			wfInputXml.appendTagAndValue("Query", sQuery);
			wfInputXml.appendTagAndValue("EngineName", getCabinetName(iformObj));
			wfInputXml.appendTagAndValue("SessionId", getSessionId(iformObj));
			wfInputXml.appendEndCallName("APSelect", "Input");
			String sInputXML= wfInputXml.toString();

			Digital_PL.mLogger.debug("Inside ExecuteQuery_APSelect [InputXml]:\n "+sInputXML);

			return ExecuteQueryOnServer(sInputXML, iformObj);
		} 
		catch (Exception e) 
		{
			Digital_PL.printException(e);
			return "";
		}			
	}

	public String ExecuteQuery_APSelectWithColumnNames(String sQuery, IFormReference iformObj)
	{
		try{
			WFInputXml wfInputXml = new WFInputXml();
			wfInputXml.appendStartCallName("APSelectWithColumnNames", "Input");
			wfInputXml.appendTagAndValue("Query", sQuery);
			wfInputXml.appendTagAndValue("EngineName", getCabinetName(iformObj));
			wfInputXml.appendTagAndValue("SessionId", getSessionId(iformObj));
			wfInputXml.appendEndCallName("APSelectWithColumnNames", "Input");
			String sInputXML= wfInputXml.toString();

			Digital_PL.mLogger.debug("Inside ExecuteQuery_APSelectWithColumnNames [InputXml]:\n "+sInputXML);

			return ExecuteQueryOnServer(sInputXML, iformObj);
		} 
		catch (Exception e) 
		{
			Digital_PL.printException(e);
			return "";
		}			
	}

	public String ExecuteQuery_APUpdate(String tableName, String columnName, String strValues, String sWhere, IFormReference iformObj)
	{
		try{
			WFInputXml wfInputXml = new WFInputXml();
			if (strValues == null)
			{
				strValues = "''";
			}
			wfInputXml.appendStartCallName("APUpdate", "Input");
			wfInputXml.appendTagAndValue("TableName", tableName);
			wfInputXml.appendTagAndValue("ColName", columnName);
			wfInputXml.appendTagAndValue("Values", strValues);
			wfInputXml.appendTagAndValue("WhereClause", sWhere);
			wfInputXml.appendTagAndValue("EngineName", getCabinetName(iformObj));
			wfInputXml.appendTagAndValue("SessionId", getSessionId(iformObj));
			wfInputXml.appendEndCallName("APUpdate", "Input");

			String sInputXML= wfInputXml.toString();

			Digital_PL.mLogger.debug("Inside ExecuteQuery_APUpdate [InputXml]:\n "+sInputXML);

			return ExecuteQueryOnServer(sInputXML, iformObj);
		} 
		catch (Exception e) 
		{
			Digital_PL.printException(e);
			return "";
		}			
	}
	
	public static String apUpdateInput(String cabinetName,String sessionID, String tableName, String columnName,
			 String strValues,String sWhereClause)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<APUpdate_Input>\n");
		ipXMLBuffer.append("<Option>APUpdate</Option>\n");
		ipXMLBuffer.append("<TableName>");
		ipXMLBuffer.append(tableName);
		ipXMLBuffer.append("</TableName>\n");
		ipXMLBuffer.append("<ColName>");
		ipXMLBuffer.append(columnName);
		ipXMLBuffer.append("</ColName>\n");
		ipXMLBuffer.append("<Values>");
		ipXMLBuffer.append(strValues);
		ipXMLBuffer.append("</Values>\n");
		ipXMLBuffer.append("<WhereClause>");
		ipXMLBuffer.append(sWhereClause);
		ipXMLBuffer.append("</WhereClause>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("</APUpdate_Input>");

		return ipXMLBuffer.toString();

	 }

	public  String ExecuteQuery_APInsert(String tableName, String columnName, String strValues, IFormReference iformObj)
	{
		Digital_PL.mLogger.debug("Inside ExecuteQuery_APInsert()");
		try {
			WFInputXml wfInputXml = new WFInputXml();
			wfInputXml.appendStartCallName("APInsert", "Input");
			wfInputXml.appendTagAndValue("TableName", tableName);
			wfInputXml.appendTagAndValue("ColName", columnName);
			wfInputXml.appendTagAndValue("Values", strValues);
			wfInputXml.appendTagAndValue("EngineName", getCabinetName(iformObj));
			wfInputXml.appendTagAndValue("SessionId",getSessionId(iformObj));//added temp till sessionId is not available
			wfInputXml.appendEndCallName("APInsert", "Input");

			String sInputXML= wfInputXml.toString();

			Digital_PL.mLogger.debug("Inside ExecuteQuery_APInsert [InputXml]:\n "+sInputXML);

			return ExecuteQueryOnServer(sInputXML, iformObj);
			
		} 
		catch (Exception e) 
		{
			Digital_PL.printException(e);
			return "";
		}			

	}

	public String getTagValue(String xml, String tag) 
	{   
		try
		{
			Document doc = getDocument(xml);
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
		}
		catch(Exception e)
		{
			Digital_PL.printException(e);
		}
	return "";
	}
	
	public String getTagValue(Node node, String tag)
	{
		// TODO Auto-generated method stub
		String value = "";

		NodeList nodeList = node.getChildNodes();
		int length = nodeList.getLength();

		for (int i = 0; i < length; ++i) {
			Node child = nodeList.item(i);

			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equalsIgnoreCase(tag)) {
				return child.getTextContent();
			}

		}
		return value;
	}

	public Document getDocument(String xml) throws ParserConfigurationException, SAXException, IOException
	{
		// Step 1: create a DocumentBuilderFactory
		DocumentBuilderFactory dbf =
				DocumentBuilderFactory.newInstance();

		// Step 2: create a DocumentBuilder
		DocumentBuilder db = dbf.newDocumentBuilder();

		// Step 3: parse the input file to get a Document object
		Document doc = db.parse(new InputSource(new StringReader(xml)));
		return doc;
	}
	
	public NodeList getNodeListFromDocument(Document doc,String identifier)
	{
		NodeList records = doc.getElementsByTagName(identifier);
		return records;
	}
	
	//**********************************************************************************//
	//Description            	:Method to Fetch Unique Sequence No from DB
	//**********************************************************************************//
	public String getNextSequenceValue(String seqName, IFormReference iformObj) throws ParserConfigurationException,SAXException,IOException
	{
		String sQuery = "SELECT "+seqName+".NEXTVAL FROM DUAL";
		String outputXML = ExecuteQuery_APSelect(sQuery, iformObj);
		String value = "0";
		int count=0;
		if(outputXML.indexOf("<td>") > -1)
		{
			value=getTagValue(outputXML,"td");
		}
		
		if(!value.equals(""))
			count=Integer.parseInt(value)+1;
		
		return String.valueOf(count);
	}
	
	public String generateResponseString(String SaveFormData, String SuccessOrError, String preAlertMessage,String alertMessageCode ,String postAlertMessage, String call,String data)
	{
		
		return "{'SAVEFORMDATA':'"+SaveFormData+"',"
				+ "'SUCCESSORERROR':'"+SuccessOrError+"',"
				+ "'PREALERTMESSAGE':'"+preAlertMessage+"',"
				+ "'ALERTMESSAGECODE':'"+alertMessageCode+"',"
				+ "'POSTALERTMESSAGE':'"+postAlertMessage+"',"
				+ "'CALL':'"+call+"',"
				+ "'DATA':'"+data+"'}";
	}
	
	public void setBBAN(String countryCode , String accNum, IFormReference iformObj)
	{
		System.out.println("Inside setBBAN method !! "+countryCode+" ; "+accNum);
		try
		{
			if(countryCode.equalsIgnoreCase("GM"))
			{
				long bban = 97 - (Long.parseLong(accNum)  *100)%97;
				setControlValue("BBAN",accNum+bban+"", iformObj);
			}
			//EMZ NUIT and ALTACC change 
			else if(countryCode.equalsIgnoreCase("MZ"))
			{
				String accNo = getControlValue("ACCOUNT_NO", iformObj);
				if(accNo != null && !accNo.equals(""))
				{
					String bban = accNo.substring(1,3)+getControlValue("CUSTOMER_ID", iformObj)+accNo.substring(14);
					setControlValue("BBAN",bban, iformObj);
				}
			}
			//EMZ NUIT and ALTACC change 
		}
		catch(Exception zz)	
		{
			zz.printStackTrace();
		}
	}
	
	//ECD - 23 digit number to be generated based on given logic after account openning
	public String get23DigitAccountNumberRIB(String ibnStr)
	{
		System.out.println("Inside get23DigitAccountNumber method ...");	
		
		StringBuilder input = new StringBuilder();
        input.append(ibnStr);
        input = input.reverse();       
        
		int val = 0;
		String outputAccNumber ="";
		
		for(int i=0;i<input.length();i++)
		{
			if(i==0)
				val+=(3) * Character.getNumericValue(input.charAt(i));
			else if(i==1)
				val+=(30) * Character.getNumericValue(input.charAt(i));
			else if(i==2)
				val+=(9) * Character.getNumericValue(input.charAt(i));
			else if(i==3)
				val+=(90) * Character.getNumericValue(input.charAt(i));
			else if(i==4)
				val+=(27) * Character.getNumericValue(input.charAt(i));
			else if(i==5)
				val+=(76) * Character.getNumericValue(input.charAt(i));
			else if(i==6)
				val+=(81) * Character.getNumericValue(input.charAt(i));
			else if(i==7)
				val+=(34) * Character.getNumericValue(input.charAt(i));
			else if(i==8)
				val+=(49) * Character.getNumericValue(input.charAt(i));
			else if(i==9)
				val+=(5) * Character.getNumericValue(input.charAt(i));
			else if(i==10)
				val+=(50) * Character.getNumericValue(input.charAt(i));
			else if(i==11)
				val+=(15) * Character.getNumericValue(input.charAt(i));
			else if(i==12)
				val+=(53) * Character.getNumericValue(input.charAt(i));
			else if(i==13)
				val+=(45) * Character.getNumericValue(input.charAt(i));
			else if(i==14)
				val+=(62) * Character.getNumericValue(input.charAt(i));
			else if(i==15)
				val+=(38) * Character.getNumericValue(input.charAt(i));
			else if(i==16)
				val+=(89) * Character.getNumericValue(input.charAt(i));
			else if(i==17)
				val+=(17) * Character.getNumericValue(input.charAt(i));
			else if(i==18)
				val+=(73) * Character.getNumericValue(input.charAt(i));						
		}
				
		val = val%97;
		
		val = 97-val;
		
		if(val<0)
			val=val * (-1);		

		if(val<10)		
			outputAccNumber="0"+val;		
		else
			outputAccNumber=val+"";
		
		System.out.println("outputAccNumber:"+outputAccNumber);
		return outputAccNumber;
	}
	
	public String getRibKey(String countryCode,String aff_bankCode,String accNum,String IbanBrCode)
	{
		System.out.println("Inside get Rib Key");
		System.out.println("countryCode :"+countryCode);
		System.out.println("aff_bankCode :"+aff_bankCode);
		System.out.println("accNum :"+accNum);
		System.out.println("IbanBrCode :"+IbanBrCode);

		String extraZero="00";
		if(countryCode.equalsIgnoreCase("TG"))
		{
			int numBankCode=37055;
			return calucalteRibETG(numBankCode,accNum,IbanBrCode);
		}
		else if(countryCode.equalsIgnoreCase("GA"))
		{
			// String substr_acc1=accNum.substring(3,6);
			// String substr_acc2=accNum.substring(8,16);
			String accNumPart=accNum.substring(3,6)+accNum.substring(8,16);
			return calucalteRIBModuloLogic(accNum,IbanBrCode,extraZero,accNumPart,countryCode);
		}
		else if(countryCode.equalsIgnoreCase("GQ"))
		{
			extraZero="";
			String accNumPart=accNum.substring(3,6)+accNum.substring(8,16);
			return calucalteRIBModuloLogic(accNum,IbanBrCode,extraZero,accNumPart,countryCode);
		}
		else if(countryCode.equalsIgnoreCase("BF"))
		{
			String accNumPart=accNum.substring(4,16);
			return calucalteRIBModuloLogic(accNum,IbanBrCode,extraZero,accNumPart,countryCode);
		}
		else if(countryCode.equalsIgnoreCase("BJ"))
		{
			int numBankCode=21062;
			String accNumPart=accNum.substring(4,16);
			return calucalteRIBModuloLogic(accNum,IbanBrCode,extraZero,accNumPart,countryCode);
		}
		else if(countryCode.equalsIgnoreCase("CM")||countryCode.equalsIgnoreCase("CM"))
		{
			String accNumPart=accNum.substring(3,6)+accNum.substring(8,16);
			return calucalteRIBModuloLogic(accNum,IbanBrCode,extraZero,accNumPart,countryCode);
		}
		else if(countryCode.equalsIgnoreCase("CG"))
		{
			//Change done for ECG specific for IBAN NO
			System.out.println("inside ECG for iban ");
			String rib=aff_bankCode+extraZero+accNum.substring(0,3)+accNum.substring(3,6)+accNum.substring(8,16)+"00";
			System.out.println("rib affiliate specific" +rib);
			return clerib1(rib);
		}
		//ECV RIB Change
		else if(countryCode.equalsIgnoreCase("CV"))
		{			
			return calucalteRibECV(countryCode,accNum,aff_bankCode);
		}
		else
		{
			String ibanAccString="";
			ibanAccString=accNum.substring(4,16);
			return ribKeyGenerate(ibanAccString,aff_bankCode,IbanBrCode);
		}
	}
	//ECV RIB Change
	private String calucalteRibECV(String countryCode, String accNum,String aff_bankCode)
	{
		System.out.println("Inside calucalteRibECV method ...");
		String ibanStr=  makeIBANString(countryCode,aff_bankCode, accNum,"");

		System.out.println("ibanStr :"+ibanStr);
		
		StringBuilder input = new StringBuilder();
        input.append(ibanStr);
        input = input.reverse();       
        
		int val = 0;
		String rib ="";
		
		for(int i=0;i<input.length();i++)
		{
			if(i==0)
				val+=(3) * Character.getNumericValue(input.charAt(i));
			else if(i==1)
				val+=(30) * Character.getNumericValue(input.charAt(i));
			else if(i==2)
				val+=(9) * Character.getNumericValue(input.charAt(i));
			else if(i==3)
				val+=(90) * Character.getNumericValue(input.charAt(i));
			else if(i==4)
				val+=(27) * Character.getNumericValue(input.charAt(i));
			else if(i==5)
				val+=(76) * Character.getNumericValue(input.charAt(i));
			else if(i==6)
				val+=(81) * Character.getNumericValue(input.charAt(i));
			else if(i==7)
				val+=(34) * Character.getNumericValue(input.charAt(i));
			else if(i==8)
				val+=(49) * Character.getNumericValue(input.charAt(i));
			else if(i==9)
				val+=(5) * Character.getNumericValue(input.charAt(i));
			else if(i==10)
				val+=(50) * Character.getNumericValue(input.charAt(i));
			else if(i==11)
				val+=(15) * Character.getNumericValue(input.charAt(i));
			else if(i==12)
				val+=(53) * Character.getNumericValue(input.charAt(i));
			else if(i==13)
				val+=(45) * Character.getNumericValue(input.charAt(i));
			else if(i==14)
				val+=(62) * Character.getNumericValue(input.charAt(i));
			else if(i==15)
				val+=(38) * Character.getNumericValue(input.charAt(i));
			else if(i==16)
				val+=(89) * Character.getNumericValue(input.charAt(i));
			else if(i==17)
				val+=(17) * Character.getNumericValue(input.charAt(i));
			else if(i==18)
				val+=(73) * Character.getNumericValue(input.charAt(i));						
		}
				
		val = val%97;
		
		val = 98-val;
		
		if(val<0)
			val=val * (-1);		

		if(val<10)		
			rib="0"+val;		
		else
			rib=val+"";
		
		System.out.println(rib);
		return rib;
	}
	
	public String makeIBANString(String countryCode,String aff_bankCode,String accNum,String ibanBrCode)
	{
		if(countryCode.equalsIgnoreCase("GA"))
		{
			return (aff_bankCode+"00"+accNum.substring(0,3)+accNum.substring(4,6)+"0"+accNum.substring(10,16));
		}
		if(countryCode.equalsIgnoreCase("GQ"))
		{
			return (aff_bankCode+"00"+accNum.substring(0,3)+accNum.substring(3,6)+accNum.substring(8,16));
		}
		else if(countryCode.equalsIgnoreCase("CM")||countryCode.equalsIgnoreCase("CM"))
		{
			return (aff_bankCode+ibanBrCode+accNum.substring(3,6)+accNum.substring(8,16));
		}
		else if(countryCode.equalsIgnoreCase("BF"))
		{
			return (aff_bankCode+"00"+accNum.substring(0,3)+accNum.substring(4,16));
		}
		//ECV RIB Change
		else if(countryCode.equalsIgnoreCase("CV"))
		{
			return (aff_bankCode+"0"+accNum.substring(0,3)+accNum.substring(3,6)+accNum.substring(8,16));
		}
		else if(countryCode.equalsIgnoreCase("CG"))
		{
			//Change done for ECG specific for IBAN NO
			System.out.println("inside ECG for makeIBANString ");
			System.out.println("inside ECG for makeIBANString aff_bankCode " +aff_bankCode);
			System.out.println("inside ECG for makeIBANString accNum " +accNum);
			System.out.println("inside ECG for makeIBANString accNum1 " +accNum.substring(0,3));
			System.out.println("inside ECG for makeIBANString accNum2 " +accNum.substring(3,6));
			System.out.println("inside ECG for makeIBANString accNum3 " +accNum.substring(8,16));
			return (aff_bankCode+"00"+accNum.substring(0,3)+accNum.substring(3,6)+accNum.substring(8,16));
		}
		else
		{
			return (aff_bankCode+ibanBrCode+accNum.substring(4,16));
		}
	}
	
	//**********************************************************************************//
	//Description            	:Method to Generate RIB Key
	//**********************************************************************************//
	public String ribKeyGenerate(String ibanAccString,String aff_bankCode,String ibanBrCode)
	{
		String rib=aff_bankCode;

		rib+=ibanBrCode+ibanAccString+"00";

		String ribKey = clerib(rib);
		return ribKey;
	}

	//**********************************************************************************//
	//Description            	:Method to Generate RIB
	//**********************************************************************************//
	public String clerib(String rib)
	{
		int  i;
		Long Reste ;
		String s="",CleRib="";
		Reste = Long.parseLong("0");
		Reste = ((Reste * 10) + estlettre(rib.charAt(0))) % 97 ;
		Reste = ((Reste * 10) + estlettre(rib.charAt(1))) % 97 ;

		//Long Rest1=estlettre(rib.charAt(1));
		for (i=2;i<rib.length();i++)
		{
			if(i==1){
				//String rKey = estlettre(rib.substring(i,i+1)).toString();
				Reste = ((Reste * 10) +estlettre(rib.charAt(1))) % 97;
			}
			else
			Reste = ((Reste * 10) + Long.parseLong(rib.substring(i,i+1))) % 97;
		}
		s = Long.toString(97 - Reste);
		if (s.length()== 1)
			CleRib = "0" + s;
		else
			CleRib = s;
		return CleRib;
	}
	//**********************************************************************************//
	//Description            	:Method to Generate RIB for ECG
	//**********************************************************************************//
	public String clerib1(String rib) 
	{   
		int  i;
		Long Reste ;
		String s="",CleRib=""; 
		Reste = Long.parseLong("0");
		for (i=0;i<rib.length();i++)
		{ 
			
			Reste = ((Reste * 10) + Long.parseLong(rib.substring(i,i+1))) % 97; 
		} 
		s = Long.toString(97 - Reste);
		if (s.length()== 1) 
			CleRib = "0" + s; 
		else
			CleRib = s; 
			
		System.out.println("CleRib1 aff_speci "+CleRib);	
		return CleRib;
	}
	//**********************************************************************************//
	//Description            	:Method to Generate RIB
	//**********************************************************************************//
	public int estlettre(char e)
	{
		int letter;

		switch (e)
		{
			case 'K':  letter=2; ; break;
			case 'B':  letter=5; break;
			case 'C':  letter=3; break;
			case 'A':  letter=1; break;
			case 'S':  letter=2; break;
			case 'D':  letter=4; break;
			case 'H':  letter=8; break;
			case 'T':  letter=7; break;
			case 'N':  letter=5; break;
			default:   letter=2; break;
		}
		return letter;
	}
	private String calucalteRibETG(int numBankCode,String accNum,String IbanBrCode)
	{
		String rib="";
		String sBranchCode=accNum.substring(0,3);
		int iBranchCode = Integer.parseInt(sBranchCode);

		int substr_acc1=Integer.parseInt(accNum.substring(4,10));
		int substr_acc2=Integer.parseInt(accNum.substring(10,16));

		/* System.out.println("iBranchCode "+iBranchCode);
		System.out.println("substr_acc1 "+substr_acc1);
		System.out.println("substr_acc2 "+substr_acc2); */

//		long intrRIB= (17*30055+53*(1000+701)+81*111403+3*169801);

		int townCode = 1000;

		try{
		// townCode = Integer.parseInt(IbanBrCode);

			if(sBranchCode.equalsIgnoreCase("713"))
			{
				townCode=2000;
			}
			else if(sBranchCode.equalsIgnoreCase("714"))
			{
				townCode=4000;
			}
			else if(sBranchCode.equalsIgnoreCase("712"))
			{
				townCode=5000;
			}
			else if(sBranchCode.equalsIgnoreCase("707"))
			{
				townCode=6000;
			}
			else if(sBranchCode.equalsIgnoreCase("711"))
			{
				townCode=7000;
			}
			else if(sBranchCode.equalsIgnoreCase("715"))
			{
				townCode=10000;
			}
		}
		catch(Exception e)
		{
			townCode=1000;
		}

		long intrRIB= (17*numBankCode+53*(townCode+iBranchCode)+81*substr_acc1+3*substr_acc2);

		// System.out.println("intrRIB "+intrRIB);

		long intrRIB2=97-(intrRIB%97);

		String ribKey = intrRIB2+"";

		return ribKey;
	}

	private String calucalteRIBModuloLogic(String accNum,String IbanBrCode,String extraZero,String accNumPart,String countryCode)
	{
		String rib="";
		String sBranchCode=accNum.substring(0,3);

		String account="00"+sBranchCode;

		if(countryCode.equalsIgnoreCase("BJ"))
		{
			rib+="21062" + IbanBrCode + accNumPart + extraZero;
		}
		if(countryCode.equalsIgnoreCase("CM")||countryCode.equalsIgnoreCase("CM"))
		{
			rib+="10029" + IbanBrCode + accNumPart + extraZero;
		}
		else
		{
			rib+=IbanBrCode + account + accNumPart + extraZero;
		}

		String ribKey = calculateModulus(rib);

		return ribKey;
	}

	public String calculateModulus(String val)
	{
		String value="";
		int len=0;
		int midvalue=0;
		char temp;
		len=val.length();

		for(int i=0;i<len;i++)
		{
			temp=val.charAt(i);
			midvalue=(midvalue*10) + (Character.getNumericValue(temp));
			if(midvalue>97)
			midvalue=midvalue%97;
		}

		midvalue=97-midvalue;

		if(midvalue<0)
		  midvalue=midvalue * (-1);

		value=Integer.toString(midvalue);

		if(midvalue<10)
		{
		   value="0"+midvalue;
		}
		return value;
	}
	
	public String getGridDataXMLInOFFormat(JSONArray jsonArray )
	{
		Digital_PL.mLogger.debug("Inside getGridDataXML method ... ");
		String xmlData="";
		
		try
		{
			Iterator it = jsonArray.iterator();			
			
			List<TreeMap<String,String>> list = new ArrayList<TreeMap<String,String>>();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    DocumentBuilder db = dbf.newDocumentBuilder();

		    Document document = db.newDocument();

		    Element root = document.createElement("ListItems"); 
		    document.appendChild(root);
		    
		    Element totalRecords = document.createElement("TotalRetrieved"); 
		    totalRecords.appendChild(document.createTextNode(jsonArray.size()+""));  
		    root.appendChild(totalRecords);   
		    
			int i =0;
			 while (it.hasNext()) 
			 {
				 Digital_PL.mLogger.debug("Loop ::"+i++);
				 TreeMap<String,String> content = new TreeMap<String,String>();
				 JSONObject obj = (JSONObject) it.next();
				 for (Object e : obj.entrySet()) 
				 {
					    Map.Entry entry = (Map.Entry) e;
					    content.put(String.valueOf(entry.getKey()), entry.getValue().toString());
				 }
				 
				Digital_PL.mLogger.debug("Content ::"+content);
		    	Element listItem = document.createElement("ListItem"); 
		    			
		    	//Element emptyTag = document.createElement("Tag");
		    	//listItem.appendChild(emptyTag);
		    	
			    for(String e  : content.keySet())
			    {    
			    	Digital_PL.mLogger.debug("Map Loop ::"+i+"           Key:"+e+"        Value:"+content.get(e));
			        Element tag = document.createElement("SubItem");
			        String tagValue = content.get(e);
			        
			        if(tagValue==null)
			        	tagValue="";
			        tag.appendChild(document.createTextNode(tagValue));
			        listItem.appendChild(tag);
			    }
			    root.appendChild(listItem);   
				 
		     }
			
			TransformerFactory tf = TransformerFactory.newInstance();
		    Transformer transformer;
		    
		    transformer = tf.newTransformer();
		    StringWriter writer = new StringWriter();
		    transformer.transform(new DOMSource(document), new StreamResult(writer));
		    xmlData = writer.getBuffer().toString();
		    xmlData = xmlData.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");
		    Digital_PL.mLogger.debug("Output data :"+xmlData);
		}
		catch(Exception exc)
		{
			Digital_PL.printException(exc);
		}
		
		return xmlData;
	}
	
	
	public String getGridDataXML(JSONArray jsonArray )
	{
		Digital_PL.mLogger.debug("Inside getGridDataXML method ... ");
		String xmlData="";
		
		try
		{
			Iterator it = jsonArray.iterator();			
			
			List<TreeMap<String,String>> list = new ArrayList<TreeMap<String,String>>();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    DocumentBuilder db = dbf.newDocumentBuilder();

		    Document document = db.newDocument();

		    Element root = document.createElement("ListItems"); 
		    document.appendChild(root);
		    
		    Element totalRecords = document.createElement("TotalRetrieved"); 
		    totalRecords.appendChild(document.createTextNode(jsonArray.size()+""));  
		    root.appendChild(totalRecords);   
		    
			int i =0;
			 while (it.hasNext()) 
			 {
				 Digital_PL.mLogger.debug("Loop ::"+i++);
				 TreeMap<String,String> content = new TreeMap<String,String>();
				 JSONObject obj = (JSONObject) it.next();
				 for (Object e : obj.entrySet()) 
				 {
					    Map.Entry entry = (Map.Entry) e;
					    content.put(String.valueOf(entry.getKey()), entry.getValue().toString());
				 }
				 
				Digital_PL.mLogger.debug("Content ::"+content);
		    	Element listItem = document.createElement("ListItem"); 
		    	//root.appendChild(listItem);
		    	
			    for(String e  : content.keySet())
			    {    
			    	Digital_PL.mLogger.debug("Map Loop ::"+i+"           Key:"+e+"        Value:"+content.get(e));
			        Element tag = document.createElement(e);
			        String tagValue = content.get(e);
			        
			        if(tagValue==null)
			        	tagValue="";
			        tag.appendChild(document.createTextNode(tagValue));
			        listItem.appendChild(tag);
			    }
			    root.appendChild(listItem);   
				 
		     }
			
			TransformerFactory tf = TransformerFactory.newInstance();
		    Transformer transformer;
		    
		    transformer = tf.newTransformer();
		    StringWriter writer = new StringWriter();
		    transformer.transform(new DOMSource(document), new StreamResult(writer));
		    xmlData = writer.getBuffer().toString();
		    xmlData = xmlData.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");
		    Digital_PL.mLogger.debug("Output data :"+xmlData);
		}
		catch(Exception exc)
		{
			Digital_PL.printException(exc);
		}
		
		return xmlData;
	}
	
	public String SearchExistingDoc(IFormReference iformObj,String pid, String FrmType, String sCabname, String sSessionId, String sJtsIp, int iJtsPort_int, String sFilepath) 
	{
		try 
		{
			String strFolderIndex="";
			String strImageIndex="";
			
			String strInputQry1="SELECT FOLDERINDEX,ImageVolumeIndex FROM PDBFOLDER WITH(NOLOCK) WHERE NAME='" + pid + "'";
			
			short iJtsPort = (short) iJtsPort_int;
			
			Digital_PL.mLogger.debug("sInputXML: "+strInputQry1);
			
			List<List<String>> dataFromDB = iformObj.getDataFromDB(strInputQry1);
			for (List<String> tableFrmDB : dataFromDB) {
				strFolderIndex = tableFrmDB.get(0).trim();
				//strImageIndex = tableFrmDB.get(1).trim();
				strImageIndex = Integer.toString(iformObj.getObjGeneralData().getM_iVolId());
			}
			Digital_PL.mLogger.debug("strFolderIndex: "+strFolderIndex);
			Digital_PL.mLogger.debug("strImageIndex: "+strImageIndex);
			
			IFormXmlResponse xmlParserData = new IFormXmlResponse();
			
			if (!(strFolderIndex.equalsIgnoreCase("") && strImageIndex.equalsIgnoreCase(""))) 
			{
				
				String strInputQry2="SELECT a.documentindex,b.ParentFolderIndex FROM PDBDOCUMENT A WITH (NOLOCK), PDBDOCUMENTCONTENT B WITH (NOLOCK)" + "WHERE A.DOCUMENTINDEX= B.DOCUMENTINDEX AND A.NAME IN ('" + FrmType + "','') AND B.PARENTFOLDERINDEX ='" + strFolderIndex + "'";
				Digital_PL.mLogger.debug("sInputXML: "+strInputQry2);
				
				List<List<String>> dataFromDB2 = iformObj.getDataFromDB(strInputQry2);
				Digital_PL.mLogger.debug("dataFromDB2: "+dataFromDB2);
				
				ArrayList<String> strdocumentindex = new ArrayList<String>(dataFromDB2.size());
				Digital_PL.mLogger.debug("strdocumentindex: "+strdocumentindex);
				ArrayList<String> strParentFolderIndex = new ArrayList<String>(dataFromDB2.size());
				Digital_PL.mLogger.debug("strParentFolderIndex: "+strParentFolderIndex);

				for (List<String> tableFrmDB2 : dataFromDB2) {
					Digital_PL.mLogger.debug("tableFrmDB2: "+tableFrmDB2);
					strdocumentindex.add(tableFrmDB2.get(0).trim());
					strParentFolderIndex.add(tableFrmDB2.get(1).trim());
				}
				Digital_PL.mLogger.debug("strdocumentindex: "+strdocumentindex);
				Digital_PL.mLogger.debug("strParentFolderIndex: "+strParentFolderIndex);
				
				Digital_PL.mLogger.debug("dataFromDB2.size();: "+dataFromDB2.size());
								
				Digital_PL.mLogger.debug("dataFromDB2.isEmpty: "+dataFromDB2.isEmpty());
				try 
				{
					Digital_PL.mLogger.debug("Inside Adding PN File: ");
					Digital_PL.mLogger.debug("sFilepath: "+sFilepath);
					String filepath = sFilepath;
					
					File newfile = new File(filepath);
					String name = newfile.getName();
					String ext = "";
					String sMappedInputXml="";
					if (name.contains(".")) {
						ext = name.substring(name.lastIndexOf("."), name.length());
					}
					JPISIsIndex ISINDEX = new JPISIsIndex();
					JPDBRecoverDocData JPISDEC = new JPDBRecoverDocData();
					String strDocumentPath = sFilepath;
					File processFile = null;
					long lLngFileSize = 0L;
					processFile = new File(strDocumentPath);
					
					lLngFileSize = processFile.length();
					String lstrDocFileSize = "";
					lstrDocFileSize = Long.toString(lLngFileSize);
					
					String createdbyappname = "";
					createdbyappname = ext.replaceFirst(".", "");
					Short volIdShort = Short.valueOf(strImageIndex);
					
					Digital_PL.mLogger.debug("lLngFileSize: --"+lLngFileSize);
					if (lLngFileSize != 0L)
					{
						Digital_PL.mLogger.debug("sJtsIp --"+sJtsIp+" iJtsPort-- "+iJtsPort+" sCabname--"+sCabname+" volIdShort.shortValue() --"+volIdShort.shortValue()+" strDocumentPath--"+strDocumentPath+" JPISDEC --"+JPISDEC+"  ISINDEX-- "+ISINDEX);
						CPISDocumentTxn.AddDocument_MT(null, sJtsIp, iJtsPort, sCabname, volIdShort.shortValue(), strDocumentPath, JPISDEC, "", ISINDEX);
						
					}
					Digital_PL.mLogger.debug("dataFromDB2.size(): --"+dataFromDB2.size());
					if (dataFromDB2.size() > 0) 
					{  
						SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
						Date date = new Date(System.currentTimeMillis());
						String strCurrDateTime = formatter.format(date);
						for(int i=0;i<dataFromDB2.size();i++)
						{
							Digital_PL.mLogger.debug("NGOChangeDocumentProperty_Input section");
							 sMappedInputXml = "<?xml version=\"1.0\"?>"
									+ "<NGOChangeDocumentProperty_Input>"
									+ "<Option>NGOChangeDocumentProperty</Option>"
									+ "<CabinetName>" + sCabname + "</CabinetName>"
									+ "<UserDBId>" + sSessionId + "</UserDBId><Document><DocumentIndex>" + strdocumentindex.get(i) + "</DocumentIndex><NoOfPages>1</NoOfPages>"
									+ "<DocumentName>" + FrmType + "</DocumentName>"
									+ "<AccessDateTime>"+strCurrDateTime+"</AccessDateTime>"
									+ "<ExpiryDateTime>2099-12-12 0:0:0.0</ExpiryDateTime>"
									+ "<CreatedByAppName>" + createdbyappname + "</CreatedByAppName>"
									+ "<VersionFlag>N</VersionFlag>"
									+ "<AccessType>S</AccessType>"
									+ "<ISIndex>" + ISINDEX.m_nDocIndex + "#" + ISINDEX.m_sVolumeId + "</ISIndex><TextISIndex>0#0#</TextISIndex>"
									+ "<DocumentType>N</DocumentType>"
									+ "<DocumentSize>" + lstrDocFileSize + "</DocumentSize><Comment>" + createdbyappname + "</Comment><RetainAnnotation>N</RetainAnnotation></Document>"
									+ "</NGOChangeDocumentProperty_Input>";    
						}
					} 
					else 
					{
						
						sMappedInputXml="<?xml version=\"1.0\"?>"+
									"<NGOAddDocument_Input>"+ 
									"<Option>NGOAddDocument</Option>"+ 
									"<CabinetName>"+sCabname+"</CabinetName>"+ 
									"<UserDBId>"+sSessionId+"</UserDBId>" + 
									"<GroupIndex>0</GroupIndex>" +
									"<VersionFlag>Y</VersionFlag>" +
									"<ParentFolderIndex>"+strFolderIndex+"</ParentFolderIndex>" +
									"<DocumentName>"+FrmType+"</DocumentName>"+
									"<CreatedByAppName>"+createdbyappname+"</CreatedByAppName>" +
									"<Comment>"+FrmType+"</Comment>" +
									"<VolumeIndex>"+ISINDEX.m_sVolumeId+"</VolumeIndex>"+
									"<FilePath>"+strDocumentPath+"</FilePath>"+
									"<ISIndex>"+ISINDEX.m_nDocIndex+"#"+ISINDEX.m_sVolumeId+"</ISIndex>" + 
									"<NoOfPages>1</NoOfPages>" + 
									"<DocumentType>N</DocumentType>" +
									"<DocumentSize>"+lstrDocFileSize+"</DocumentSize>" +
									"</NGOAddDocument_Input>";
					
					}
					Digital_PL.mLogger.debug("Document Addition sInputXML: "+sMappedInputXml);
					//String sOutputXml = WFCustomCallBroker.execute(sMappedInputXml, sJtsIp, iJtsPort, 1);
					String sOutputXML = ExecuteQueryOnServer(sMappedInputXml, iformObj);
					xmlParserData.setXmlString((sOutputXML));
					Digital_PL.mLogger.debug("Document Addition sOutputXml: "+sOutputXML);
					String status_D = xmlParserData.getVal("Status");
					if(status_D.equalsIgnoreCase("0")){
						//deleteLocalDocument(sFilepath);
						return sOutputXML;
					} else {
						return "Error in Document Addition";
					}
				} 
				catch (JPISException e) 
				{
					return "Error in Document Addition at Volume";
				} 
				catch (Exception e) 
				{
					return "Exception Occurred in Document Addition";
				}
					
			}
			return "Any Error occurred in Addition of Document";
		} 
		catch (Exception e) 
		{
			return "Exception Occurred in SearchDocument";
		}
	}
	
	public static String apSelectWithColumnNames(String QueryString, String cabinetName, String sessionID)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<APSelect_Input>\n");
		ipXMLBuffer.append("<Option>APSelectWithColumnNames</Option>\n");
		ipXMLBuffer.append("<Query>");
		ipXMLBuffer.append(QueryString);
		ipXMLBuffer.append("</Query>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("</APSelect_Input>");

		return ipXMLBuffer.toString();
	}
	
	public static Double CalculatLOS(String DOJ_Str) {
        Double LOS = 0.00;
        try {
              Integer year = Integer.parseInt(DOJ_Str.split("-")[0]);
              Integer month = Integer.parseInt(DOJ_Str.split("-")[1]);
              Integer day = Integer.parseInt(DOJ_Str.split("-")[2]);
              LocalDate DOJ = LocalDate.of(year,month,day);
              LocalDate CD = LocalDate.now();
              Period p = Period.between(DOJ, CD);
              System.out.println(p.getMonths());
              System.out.println(p.getYears());
              
              LOS += p.getYears();
              LOS = LOS + p.getMonths()/100d;
        } catch (Exception e) {
              e.printStackTrace();
              return LOS;
        }
        
        System.out.println(LOS);
        return LOS;
  }


	
	
	
	public String RISK_SCORE_DETAILS(IFormReference iform) throws IOException
	{
		String risk="";
		try
		{
			Digital_PL.mLogger.debug("Start RISK_SCORE_DETAILS: ");
			
		/*	String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DAO_Integration")
			.append(System.getProperty("file.separator")).append("RISK_SCORE_DETAILS.txt").toString(); */
			
	        risk="<EE_EAI_MESSAGE>"+"\n"+
					"<EE_EAI_HEADER>"+"\n"+
					"<MsgFormat>RISK_SCORE_DETAILS</MsgFormat>"+"\n"+
					"<MsgVersion>0001</MsgVersion>"+"\n"+
					"<RequestorChannelId>CAS</RequestorChannelId>"+"\n"+
					"<RequestorUserId>RAKUSER</RequestorUserId>"+"\n"+
					"<RequestorLanguage>E</RequestorLanguage>"+"\n"+
					"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+"\n"+
					"<ReturnCode>911</ReturnCode>"+"\n"+
					"<ReturnDesc>Issuer Timed Out</ReturnDesc>"+"\n"+
					"<MessageId>123123453</MessageId>"+"\n"+
					"<Extra1>REQ||SHELL.JOHN</Extra1>"+"\n"+
					"<Extra2>YYYY-MM-DDThh:mm:ss.mmm+hh:mm</Extra2>"+"\n"+
					"</EE_EAI_HEADER>"+"\n"+
					"<RiskScoreDetailsRequest>"+"\n"+
						"<RequestInfo>"+"\n"+
							"<RequestType>Reference Id</RequestType>"+"\n"+
							"<RequestValue>Wi_number</RequestValue>"+"\n"+
						"</RequestInfo>"+"\n"+
						"<RequestInfo>"+"\n"+
							"<RequestType>CIF Id</RequestType>"+"\n"+
							"<RequestValue>cif_id</RequestValue>"+"\n"+
							"</RequestInfo>"+"\n"+
						"<CustomerType>Individual</CustomerType>"+"\n"+
						"<CustomerCategory>Resident Individual</CustomerCategory>"+"\n"+
						"<IsPoliticallyExposed>PEP</IsPoliticallyExposed>"+"\n"+
						"<CustomerName>Full_name</CustomerName>"+"\n"+
						"<DSAId>BATDSA1</DSAId>"+"\n"+
						"<RMCode>AISHSC</RMCode>"+"\n"+
						"<EmploymentType>emp_type</EmploymentType>"+"\n"+
						"<Segment>PERSONAL BANKING</Segment>"+"\n"+
						"<SubSegment>PB - NORMAL</SubSegment>"+"\n"+
						"<Demographics>"+"\n"+
							"<Demographic>Country_Residenece</Demographic>"+"\n"+
						"</Demographics>"+"\n"+
						"<Nationalities>"+"\n"+
							"<Nationality>Nationality</Nationality>"+"\n"+
							"<Nationality>Sec_Nationality</Nationality>"+"\n"+
						"</Nationalities>"+"\n"+
						"<Industries>industry_tag</Industries>"+"\n"+
						"<ProductsInfo>"+"\n"+
							"<Product>product_typ</Product>"+"\n"+
							"<Currency>product_curr</Currency>"+"\n"+
						"</ProductsInfo>"+"\n"+
					"</RiskScoreDetailsRequest>"+"\n"+
					"</EE_EAI_MESSAGE>"+"\n";
	        
	       /* BufferedReader sbf=new BufferedReader(new FileReader(risk));
	        
			StringBuilder sb=new StringBuilder();
			risk=sbf.readLine();
			while(risk!=null)
				{
					sb.append(risk);
					sb.append(System.lineSeparator());
					risk=sbf.readLine();
				}
			*/
			String CIF=(String) iform.getValue("CIF");
			String Wi_number = getWorkitemName(iform);
			String middleWi[] = Wi_number.split("-");
			Wi_number = middleWi[1];
			String PEP = (String)iform.getValue("PEP");
			String Full_name = (String) iform.getValue("Given_Name") +" "+ iform.getValue("Surname");
			String emp_type = (String) iform.getValue("employement_type");
			String Nationality = (String) iform.getValue("Nationality");
			String Sec_Nationality = (String) iform.getValue("Secondary_Nationality");
			String product_typ = (String) iform.getValue("product_typw");
			String product_curr = (String) iform.getValue("product_currency");
			String Country_Residenece = (String) iform.getValue("country_of_residence");
			
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:CIF: "+CIF);
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:Wi_number: "+Wi_number);
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:Full_name: "+Full_name);
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:PEP: "+PEP);
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:emp_type: "+emp_type);
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:Nationality: "+Nationality); 
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:Sec_Nationality: "+Sec_Nationality);
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:product_typ: "+product_typ);
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:product_curr: "+product_curr);
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:Country_Residenece: "+Country_Residenece);
			
			// Getting the description for product_typ.
			String Product_type_descptn ="";
			if(product_typ.equalsIgnoreCase("ACNP1")||(product_typ.equalsIgnoreCase("GBNP1")))
				{
					Product_type_descptn="Current Account";
				}
			else
				{
					Product_type_descptn="Savings Account";
				}
			/*
			String Desc_product_typ_query="select CD_DESC from NG_MASTER_DAO_PRODUCT_NAME where CM_CODE='"+product_typ+"'";
			Digital_CC.mLogger.debug("Desc_product_typ_query: "+Desc_product_typ_query);
			List<List<String>> output_Desc_product_typ = iform.getDataFromDB(Desc_product_typ_query);
			Digital_CC.mLogger.debug("output_Desc_product_typ: "+output_Desc_product_typ);
			
			if (!output_Desc_product_typ.isEmpty()) 
			{
				Digital_CC.mLogger.debug("Inside output_Desc_product_typ: ");
				Product_type_descptn = output_Desc_product_typ.get(0).get(0);
				Digital_CC.mLogger.debug("Product_type_descptn: "+Product_type_descptn);
			}
			else{
				Digital_CC.mLogger.debug("Product_type_descptn is empty!!"); 
			}
			*/
			
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:Product_type_descptn:"+Product_type_descptn);
			
			// Getting the description for Country_Residenece.
			String Country_Residenece_descptn ="";
			String Desc_Country_Residenece_query="select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE where CM_CODE='"+Country_Residenece+"'";
			Digital_PL.mLogger.debug("Desc_Country_Residenece_query: "+Desc_Country_Residenece_query);
			List<List<String>> output_Desc_Country_Residenece_query = iform.getDataFromDB(Desc_Country_Residenece_query);
			Digital_PL.mLogger.debug("output_Desc_Country_Residenece_query: "+output_Desc_Country_Residenece_query);
			
			if (!output_Desc_Country_Residenece_query.isEmpty()) 
			{
				Digital_PL.mLogger.debug("Inside output_Desc_Country_Residenece_query: ");
				Country_Residenece_descptn = output_Desc_Country_Residenece_query.get(0).get(0);
				Digital_PL.mLogger.debug("Product_type_descptn: "+Country_Residenece_descptn);
			}
			else{
				Digital_PL.mLogger.debug("Country_Residenece_descptn is empty!!"); 
			}
			
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:Country_Residenece_descptn:"+Country_Residenece_descptn);
			
			// Getting the description for Nationality.
			String Nationality_descptn ="";
			String Desc_Nationality="select CD_DESC from NG_MASTER_DAO_NATIONALITY where CM_CODE='"+Nationality+"'";
			Digital_PL.mLogger.debug("Desc_Country_Residenece_query: "+Desc_Nationality);
			List<List<String>> output_Nationality_query = iform.getDataFromDB(Desc_Nationality);
			Digital_PL.mLogger.debug("output_Desc_Country_Residenece_query: "+output_Nationality_query);
			
			if (!output_Nationality_query.isEmpty()) 
			{
				Digital_PL.mLogger.debug("Inside output_Nationality_query: ");
				Nationality_descptn = output_Nationality_query.get(0).get(0);
				Digital_PL.mLogger.debug("Product_type_descptn: "+Nationality_descptn);
			}
			else{
				Digital_PL.mLogger.debug("Nationality_descptn is empty!!"); 
			}
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:Nationality_descptn:"+Nationality_descptn);
						
			// Getting the description for Sec_Nationality.
			String Sec_Nationality_descptn ="";
			String Desc_Sec_Nationality="select CD_DESC from NG_MASTER_DAO_SECONDARY_NATIONALITY where CM_CODE='"+Sec_Nationality+"'";
			Digital_PL.mLogger.debug("Desc_Country_Residenece_query: "+Desc_Sec_Nationality);
			List<List<String>> output_Sec_Nationality_query = iform.getDataFromDB(Desc_Sec_Nationality);
			Digital_PL.mLogger.debug("output_Desc_Country_Residenece_query: "+output_Sec_Nationality_query);
			
			if (!output_Sec_Nationality_query.isEmpty()) 
			{
				Digital_PL.mLogger.debug("Inside Sec_Nationality_descptn: ");
				Sec_Nationality_descptn = output_Nationality_query.get(0).get(0);
				Digital_PL.mLogger.debug("Sec_Nationality_descptn: "+Sec_Nationality_descptn);
			}
			else{
				Digital_PL.mLogger.debug("Nationality_descptn is empty!!"); 
			}
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:Sec_Nationality_descptn:"+Sec_Nationality_descptn);
			
			// Industry Repeating tag 
			String industry_tag="";
			if(emp_type.equalsIgnoreCase("Self employed"))
			{
				Digital_PL.mLogger.debug("compnay_grid: Self employed");
				int compnay_grid = iform.getDataFromGrid("company_detail").size();
				Digital_PL.mLogger.debug("compnay_grid: Self employed: size"+compnay_grid);
				for (int i = 0; i < compnay_grid; i++)
				{
					String industry_value = iform.getTableCellValue("company_detail", i, 3);
					industry_tag+="<Industry>"+industry_value+"</Industry>";
				}
			}
			else if(emp_type.equalsIgnoreCase("Salaried"))
			{	
				Digital_PL.mLogger.debug("compnay_grid: Salaried");
				industry_tag+="<Industry>EMPLOYED INDIVIDUAL</Industry>";
			}
			
			Digital_PL.mLogger.debug("RISK_SCORE_DETAILS:Industry: "+industry_tag);
			
			// replacing tags ->
			
			Digital_PL.mLogger.debug("sb.string : Risk_score: ");
					
			risk = risk.replace(">cif_id<",">"+CIF+"<").replace(">Wi_number<",">"+Wi_number+"<").replace(">PEP<",">"+PEP+"<")
			.replace(">Full_name<",">"+Full_name+"<").replace(">emp_type<",">"+emp_type+"<").replace(">Country_Residenece<",">"+Country_Residenece_descptn+"<")
			.replace(">Nationality<",">"+Nationality_descptn+"<").replace(">Sec_Nationality<",">"+Sec_Nationality_descptn+"<")
			.replace(">industry_tag<",">"+industry_tag+"<").replace(">product_typ<",">"+Product_type_descptn+"<")
			.replace(">product_curr<",">"+product_curr+"<");
			
			Digital_PL.mLogger.debug("Start RISK_SCORE_DETAILS:  Risk_score before :"+risk);
			
		}
		catch(Exception e)
			{
				Digital_PL.mLogger.debug("RISK_SCORE_DETAILS: Exception"+e.getMessage());
			}
		
		return risk;
	 
	}
	
	
}