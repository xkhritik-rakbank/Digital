package com.newgen.CPMS;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import com.newgen.custom.CreateWorkitem;
import com.newgen.ws.EE_EAI_HEADER;
import com.newgen.ws.exception.WICreateException;
import com.newgen.ws.request.Attribute;
import com.newgen.ws.request.Attributes;
import com.newgen.ws.request.WICreateRequest;
import com.newgen.ws.response.WICreateResponse;
import com.newgen.ws.util.XMLParser;

import adminclient.OSASecurity;

public class CPMS_WIUpdateService extends CreateWorkitem {

	
	
	//CONSTANTS
	final private String SUCCESS_STATUS="0";
	//TABLE NAME CONSTANTS
	final private String WSR_PROCESS="USR_0_WSR_UPDATE_PROCESS";
	final private String WSR_ATTRDETAILS="USR_0_WSR_UPDATE_ATTRDETAILS";
	final private String WSR_POSSIBLEQUEUES="USR_0_WSR_UPDATE_POSSIBLEQUEUES";

	

	private String sProcessName;
	private String sSubProcess;
	private String sMsgFormat;
	private String sMsgVersion;
	private String sRequestorChannelId;
	private String sRequestorUserId;
	private String sRequestorLanguage;
	private String sRequestorSecurityInfo;
	private String sMessageId;
	private String sExtra1;
	private String sExtra2;
	private EE_EAI_HEADER headerObjRequest;
	private Attributes attributesObj;
	private Attribute attributeObj[];
	
	//Configuration Parameters
	private String sCabinetName = "";
	private String sJtsIp = "";
	private int iJtsPort;
	private String sUsername = "";
	private String sPassword = "";
	private String sTempLoc = "";
	private int iVolId;
	//Response Parameters
	String eCode = "";
	String eDesc = ""; 
  	//Other vriables/objects
	WICreateResponse response = new WICreateResponse();	
	EE_EAI_HEADER headerObjResponse;
	private ResourceBundle pCodes;
	private String sSessionID = "";
	HashMap<String, String> hm = new HashMap(); 
	String sInputXML = "";
	String sOutputXML = "";
	XMLParser xmlobj;
	private String attributeTag = "";
	private String extColNames = "";
	private String extColValues = "";
	String sFilePath = "";
	boolean sessionFlag = false;
	private String processID = "";
	private String trTableColumn = "";
	private String trTableValue = "";
	private String sWhereClause ="";
	private String wiName = "";
	private String externalTableName = "";
	private String HISTORYTABLE = "";
	private String processDefID = "";
	private String sDate = "";
	
	
	
	private String WINumber = "";
	private String ActivityName="";
	private String PossibleQueues="";
	
	//for RAOP process
	private String sCUST_AUTHENTICATION="";
	private String sRemarksAddInfo="";
		 
	public WICreateResponse wiUpdate(WICreateRequest request,String attributeList[]) throws WICreateException { 
		
			try
			{
				WriteLog("inside wiUpdate for DBO process");
				headerObjResponse=new EE_EAI_HEADER();
				//initialize Logger
				//initializeLogger();	
				
				//Load file configuration
				loadConfiguration();
				
				//Load ResourceBundle
				loadResourceBundle();
				
				//Fetching request parameters 
				fetchRequestParameters(request);
									
				//Validating Input Parameters and Check Attribute Name from USR_0_WSR_UPDATE_ATTRDETAILS
				validateRequestParameters(attributeList);
				
				//Checking existing session
				checkExistingSession();
				
				
            	/*if ("DBO".equalsIgnoreCase(sProcessName))
            	{*/
            		if("RAOP_YAP".equalsIgnoreCase(processID))
            			updateInDataInExtTableRAOP(); // all data updated in External table for 2nd call from YAP
            		if("RAOP_YAP_AddInfo".equalsIgnoreCase(processID))
            			updateInDataInExtTableRAOPAddInfo(); // all data updated in External table for Add Info from YAP
            		insertIntoHistoryDBO(); // inserting in history table
            		doneworkitem();
            	//}
            	// returning success response
            	returnResponse();
		 			                		
          	}
			catch(WICreateException e)
			{
				WriteLog("WICreateException caught:Code- "+e.getErrorCode());
				WriteLog("WICreateException caught:Description "+e.getErrorDesc());
				WriteLog("WICreateException: "+e.toString());
				setFailureParamResponse();
				response.setErrorCode(e.getErrorCode());
				response.setErrorDescription(e.getErrorDesc());
				return response;
			}
			catch(IOException e)
			{
				setFailureParamResponse();
				response.setErrorCode("1009");
        		response.setErrorDescription(pCodes.getString("1009")+": "+e.getMessage());

        		return response;
			}
			catch(Exception e)
			{
				setFailureParamResponse();
				response.setErrorCode("1010");
        		response.setErrorDescription(pCodes.getString("1010")+": "+e.getMessage());
        		return response;
			}
			finally
			{			
				WriteLog("Inside finally method to dispose off conenction objects and hash maps");
				try
				{
					// Commented below code for Invalid Session Issue.
					/*if(sessionFlag==true)
						deleteConnection();*/
				}
				catch (Exception e) 
				{
					WriteLog("Exception: "+e.getMessage());					
				}
				hm.clear();

			}
			return response;
		
	}
	private void returnResponse() throws WICreateException,Exception
	{
		wiName = WINumber;
		setSuccessParamResponse();
		response.setWorkitemNumber(wiName);
		
	}
	private void setSuccessParamResponse()
	{
		headerObjResponse.setExtra1(sExtra1);
		headerObjResponse.setExtra2(sExtra2);
		headerObjResponse.setMessageId(sMessageId);
		headerObjResponse.setMsgFormat(sMsgFormat);
		headerObjResponse.setMsgVersion(sMsgVersion);
		headerObjResponse.setRequestorChannelId(sRequestorChannelId);
		headerObjResponse.setRequestorLanguage(sRequestorLanguage);
		headerObjResponse.setRequestorSecurityInfo(sRequestorSecurityInfo);
		headerObjResponse.setRequestorUserId(sRequestorUserId);
		headerObjResponse.setReturnCode("0");
		headerObjResponse.setReturnDesc("Success");
		response.setEE_EAI_HEADER(headerObjResponse);
	}
	private void setFailureParamResponse()
	{
		headerObjResponse.setExtra1(sExtra1);
		headerObjResponse.setExtra2(sExtra2);
		headerObjResponse.setMessageId(sMessageId);
		headerObjResponse.setMsgFormat(sMsgFormat);
		headerObjResponse.setMsgVersion(sMsgVersion);
		headerObjResponse.setRequestorChannelId(sRequestorChannelId);
		headerObjResponse.setRequestorLanguage(sRequestorLanguage);
		headerObjResponse.setRequestorSecurityInfo(sRequestorSecurityInfo);
		headerObjResponse.setRequestorUserId(sRequestorUserId);
		headerObjResponse.setReturnCode("-1");
		headerObjResponse.setReturnDesc("Failure");
		response.setEE_EAI_HEADER(headerObjResponse);
	}
	
	
	private void insertIntoHistoryDBO() throws WICreateException, Exception {

		trTableColumn = "WINAME,workstep,decision,action_date_time,remarks,user_name,entry_date_time";
		WriteLog("trTableColumn" + trTableColumn);
		String Remarks = "Update Request Recevied for customer authentication: "+sCUST_AUTHENTICATION;
		if(processID.equalsIgnoreCase("RAOP_YAP_AddInfo"))
			Remarks = "Additional Information from YAP is: "+sRemarksAddInfo;
		trTableValue = "'"+ WINumber + "','"+ActivityName+"','Update Request Received','" + sDate + "','"+Remarks+"','" + sUsername + "','" + sDate + "'";
		WriteLog("trTableValue" + trTableValue);
		sInputXML = getDBInsertInput(HISTORYTABLE,trTableColumn,trTableValue);
		WriteLog("APInsert Input History: " + sInputXML);
		sOutputXML = executeAPI(sInputXML);
		WriteLog("APInsert Output History: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	
	private void updateInDataInExtTableRAOP() throws WICreateException, Exception {
			
		Date d= new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sDate = dateFormat.format(d);
	
		extColNames = extColNames+"UPDATERECEIVED,DECISION";
		WriteLog("extColNames for RAOP" + extColNames);
		extColValues = extColValues+"'Y','Submit'";
		WriteLog("extColValues for RAOP" + extColValues);
		sWhereClause = "WINAME='"+WINumber+"'";
		sInputXML = getDBUpdateInputExternalTable();
		WriteLog("APUpdate for RAOP in External Table: " + sInputXML);
		sOutputXML = executeAPI(sInputXML);
		WriteLog("APUpdate for RAOP in External Table: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	
	private void updateInDataInExtTableRAOPAddInfo() throws WICreateException, Exception {
		
		Date d= new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sDate = dateFormat.format(d);
	
		extColNames = extColNames+"UPDATERECEIVED,DECISION";
		WriteLog("extColNames for RAOP" + extColNames);
		extColValues = extColValues+"'N','Submit'";
		WriteLog("extColValues for RAOP" + extColValues);
		sWhereClause = "WINAME='"+WINumber+"'";
		sInputXML = getDBUpdateInputExternalTable();
		WriteLog("APUpdate for RAOP in External Table: " + sInputXML);
		sOutputXML = executeAPI(sInputXML);
		WriteLog("APUpdate for RAOP in External Table: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	
	
	private String getDBInsertInput(String tblName,String tblCol,String tblValues) {
		return "<?xml version=\"1.0\"?>" + "<APInsert_Input>"
				+ "<Option>APInsert</Option>" + "<TableName>" + tblName
				+ "</TableName>" + "<ColName>" + tblCol + "</ColName>"
				+ "<Values>" + tblValues + "</Values>" + "<EngineName>"
				+ sCabinetName + "</EngineName>" + "<SessionId>" + sSessionID
				+ "</SessionId>" + "</APInsert_Input>";
	}
	
	private String getDBUpdateInputExternalTable() {
		return "<?xml version=\"1.0\"?>" + "<APUpdate_Input>"
				+ "<Option>APUpdate</Option>" + "<TableName>" + externalTableName
				+ "</TableName>" + "<ColName>" + extColNames + "</ColName>"
				+ "<Values>" + extColValues + "</Values>" + "<WhereClause>" + sWhereClause + "</WhereClause>"
				+ "<EngineName>"
				+ sCabinetName + "</EngineName>" + "<SessionId>" + sSessionID
				+ "</SessionId>" + "</APUpdate_Input>";
	}
	
	
	public  String getWorkItemInput()
	{
		return "<?xml version=\"1.0\"?>" + "<WMGetWorkItem_Input>"
				+ "<Option>WMGetWorkItem</Option>" + "<ProcessInstanceId>" + WINumber
				+ "</ProcessInstanceId>" + "<WorkItemId>" + "1" + "</WorkItemId>"
				+ "<EngineName>"
				+ sCabinetName + "</EngineName>" + "<SessionId>" + sSessionID
				+ "</SessionId>" + "</WMGetWorkItem_Input>";
	}	
	
	public  String getWorkItemInput_withWorkitemID(String Workitem_ID)
	{
		return "<?xml version=\"1.0\"?>" + "<WMGetWorkItem_Input>"
				+ "<Option>WMGetWorkItem</Option>" + "<ProcessInstanceId>" + WINumber
				+ "</ProcessInstanceId>" + "<WorkItemId>" + Workitem_ID + "</WorkItemId>"
				+ "<EngineName>"
				+ sCabinetName + "</EngineName>" + "<SessionId>" + sSessionID
				+ "</SessionId>" + "</WMGetWorkItem_Input>";
	}	
	
	
	public  String completeWorkItemInput()
	{
		return "<?xml version=\"1.0\"?>" + "<WMCompleteWorkItem_Input>"
				+ "<Option>WMCompleteWorkItem</Option>" + "<ProcessInstanceId>" + WINumber
				+ "</ProcessInstanceId>" + "<WorkItemId>" + "1" + "</WorkItemId>"
				+ "<AuditStatus></AuditStatus>"
				+ "<Comments></Comments>"
				+ "<EngineName>"
				+ sCabinetName + "</EngineName>" + "<SessionId>" + sSessionID
				+ "</SessionId>" + "</WMCompleteWorkItem_Input>";
	}
	public  String completeWorkItemInput_withWorkitemID(String Workitem_ID)
	{
		return "<?xml version=\"1.0\"?>" + "<WMCompleteWorkItem_Input>"
				+ "<Option>WMCompleteWorkItem</Option>" + "<ProcessInstanceId>" + WINumber
				+ "</ProcessInstanceId>" + "<WorkItemId>" + Workitem_ID + "</WorkItemId>"
				+ "<AuditStatus></AuditStatus>"
				+ "<Comments></Comments>"
				+ "<EngineName>"
				+ sCabinetName + "</EngineName>" + "<SessionId>" + sSessionID
				+ "</SessionId>" + "</WMCompleteWorkItem_Input>";
	}	

	private void getProcessDefID() throws WICreateException, Exception
	{
		sInputXML=getAPSelectWithColumnNamesXML("select a.PROCESSDEFID, b.PROCESSID from processdeftable a with(nolock),"+WSR_PROCESS+" b with(nolock) where a.processname='"+sProcessName+"' and b.SUBPROCESSNAME='"+sSubProcess+"' and a.processname=b.processname and b.isactive='Y'");
		WriteLog("APSelectWithColumnNames Input: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("APSelectWithColumnNames Output: "+sOutputXML);
    	xmlobj=new XMLParser(sOutputXML);
    	//Check Main Code
		checkCallsMainCode(xmlobj); 
		processDefID=getTagValues(sOutputXML, "PROCESSDEFID");
		WriteLog("processDefID: "+processDefID);
		processID=getTagValues(sOutputXML, "PROCESSID");
		WriteLog("processID: "+processID);
		if(processID.equalsIgnoreCase(""))
			throw new WICreateException("1019",pCodes.getString("1019")+":"+sProcessName+"/"+sSubProcess);
		
	}
	
	
	private void checkExistingSession() throws Exception, WICreateException
	{
		WriteLog("inside checkExistingSession");
		//String getSessionQry="select top(1) RANDOMNUMBER from pdbconnection";
		String getSessionQry="select randomnumber from pdbconnection where userindex in (select userindex from pdbuser where username='"+sUsername+"')";
		sInputXML=getAPSelectWithColumnNamesXML(getSessionQry);
		WriteLog("Input XML: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		//Check Main Code
		checkCallsMainCode(xmlobj);
		sSessionID=getTagValues(sOutputXML,"randomnumber");
		WriteLog("SessionID: "+sSessionID);
		if(sSessionID.equalsIgnoreCase(""))
		{
			sInputXML=getWMConnectXML();
			//WriteLog("WM Connect Input: "+sInputXML);
			sOutputXML=executeAPI(sInputXML);
			WriteLog("WM Connect Output: "+sOutputXML);
			xmlobj=new XMLParser(sOutputXML);
			//Check Main Code
			checkCallsMainCode(xmlobj);
        	sSessionID=getTagValues(sOutputXML,"SessionId");
        	sessionFlag=true;
		}
		response.setSessionId(sSessionID);
	}
	private void checkCallsMainCode(XMLParser obj) throws WICreateException
	{
		WriteLog("Inside checkCallsMainCode");
		if(!xmlobj.getValueOf("MainCode").trim().equalsIgnoreCase(SUCCESS_STATUS))
 		{
			if (!xmlobj.getValueOf("SubErrorCode").equalsIgnoreCase(""))
			{
				eCode=xmlobj.getValueOf("SubErrorCode");
				eDesc=xmlobj.getValueOf("Description");
			}
			else if(!xmlobj.getValueOf("Output").equalsIgnoreCase(""))
			{
				eCode=xmlobj.getValueOf("MainCode");
				eDesc=xmlobj.getValueOf("Output");
			}
			else 
			{
				eCode=xmlobj.getValueOf("Status");
				eDesc=xmlobj.getValueOf("Error");
			}
    		throw new WICreateException(eCode,eDesc);
       	}
	}
	
	private void loadConfiguration() throws IOException, Exception
	{
		//load file configuration
		WriteLog("Loading Configuration file");
		Properties p = new Properties();
		String sConfigFile=new StringBuilder().append(System.getProperty("user.dir"))
				.append(System.getProperty("file.separator")).append("BPMCustomWebservicesConf")
				.append(System.getProperty("file.separator")).append("config.properties").toString();
		
		p.load(new FileInputStream(sConfigFile));
	    sCabinetName=p.getProperty("CabinetName");
	    sJtsIp=p.getProperty("JtsIp");
	    iJtsPort=Integer.parseInt(p.getProperty("JtsPort"));
	    sUsername=p.getProperty("username");
		try
		{
			//WriteLog("sPassword::"+sPassword);
			sPassword=decryptPassword(p.getProperty("password"));	
			//WriteLog("after decrption::"+sPassword);
		}
		catch(Exception e)
		{
			WriteLog("Inside exception of log decryption");
			throw new WICreateException("3001",pCodes.getString("3001"));
		}
	    sTempLoc=p.getProperty("TempDocumentLoc");
	    iVolId=Integer.parseInt(p.getProperty("volid"));
	    WriteLog("CabinetName: "+sCabinetName+", JtsIp: "+sJtsIp+", JtsPort: "+iJtsPort+" ,Username: "+sUsername+", Password: "+p.getProperty("password")+" ,VolumeID: "+iVolId);
	    WriteLog("Configuration file loaded successfuly");
	    response.setCabinetName(sCabinetName);
	    response.setJtsIp(sJtsIp);
	    response.setJtsPort(iJtsPort);
	    response.setUsername(sUsername);
	}
	/*
	private String decryptPassword(String pass) throws Exception
	{
		return AESEncryption.decrypt(pass);
	}
	*/
	
	
	private String executeAPI(String sInputXML) throws WICreateException
	{
		String sOutputXML="";
		try
		{
			 Socket sock = null;
			 sock = new Socket(sJtsIp, iJtsPort);
			 DataOutputStream oOut = new DataOutputStream(new BufferedOutputStream(sock.getOutputStream()));
			 DataInputStream oIn = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
			 byte[] SendStream = sInputXML.getBytes("8859_1");
			 int strLen = SendStream.length;
	         oOut.writeInt(strLen);
	    	 oOut.write(SendStream, 0, strLen);
			 oOut.flush();
			 int length = 0;
			 length = oIn.readInt();
			 byte[] readStream = new byte[length];
			 oIn.readFully(readStream);
			 sOutputXML= new String(readStream, "8859_1");
			 sock.close(); 
		}
		catch (Exception e)
		{
			WriteLog("Exception: "+e.getMessage());
			throw new WICreateException("1006",pCodes.getString("1006")+" : "+e.getMessage());
		}
		return sOutputXML;
     	
	}

	private void fetchRequestParameters(WICreateRequest req) throws WICreateException
	{		
		try
		{
			WriteLog("Inside fetchRequestParameters");
			sProcessName=(req.getProcessName()==null)? "" : req.getProcessName().trim();
			sSubProcess=(req.getSubProcess()==null)? "" : req.getSubProcess().trim();
			headerObjRequest=req.getEE_EAI_HEADER();
			
			
			sMsgFormat=(headerObjRequest.getMsgFormat()==null)? "" : headerObjRequest.getMsgFormat().trim();
			sMsgVersion=(headerObjRequest.getMsgVersion()==null)? "" : headerObjRequest.getMsgVersion().trim();
			sMsgFormat=(headerObjRequest.getMsgFormat()==null)? "" : headerObjRequest.getMsgFormat().trim();
			sRequestorChannelId=(headerObjRequest.getRequestorChannelId()==null)? "" : headerObjRequest.getRequestorChannelId().trim();
			sRequestorUserId=(headerObjRequest.getRequestorUserId()==null)? "" : headerObjRequest.getRequestorUserId().trim();
			sRequestorLanguage=(headerObjRequest.getRequestorLanguage()==null)? "" : headerObjRequest.getRequestorLanguage().trim();
			sRequestorSecurityInfo=(headerObjRequest.getRequestorSecurityInfo()==null)? "" : headerObjRequest.getRequestorSecurityInfo().trim();
			sExtra2=(headerObjRequest.getExtra2()==null)? "" : headerObjRequest.getExtra2().trim();
			sExtra1=(headerObjRequest.getExtra1()==null)? "" : headerObjRequest.getExtra1().trim();
			sMessageId=(headerObjRequest.getMessageId()==null)? "" : headerObjRequest.getMessageId().trim();
			
			attributesObj=req.getAttributes();
			attributeObj=attributesObj.getAttribute();
			WriteLog("attributeObj.length"+attributeObj.length);
			//Fetch Name Value Attributes in Hash Map
			for(int i=0;i<attributeObj.length;i++)
			{
				
				hm.put(attributeObj[i].getName(), attributeObj[i].getValue());
			}
						
			WriteLog("sProcessName: "+sProcessName+", sSubProcess: "+sSubProcess);
		}
		catch(Exception e)
		{
			WriteLog("Exception: "+e.getMessage());
			throw new WICreateException("1007",pCodes.getString("1007")+" : "+e.getMessage());
		}
	}
	private void checkAttributeTable(String attributeName, String attributeValue) throws WICreateException, Exception
	{
		WriteLog("inside checkAttributeTable");
		String getExtTransQry="";
		if(sProcessName.equalsIgnoreCase("RAOP")) // for iBPS Processes
		{
			getExtTransQry="select ATTRIBUTENAME,EXTERNALTABLECOLNAME, TRANSACTIONTABLECOLNAME, ISMANDATORY,ATTRIBUTE_FORMAT,ATTRIBUTE_TYPE from "+WSR_ATTRDETAILS+" with(nolock) where ATTRIBUTENAME='"+attributeName+"' and PROCESSID='"+processID+"'";
		}
		else // for BPM Processes
		{
			getExtTransQry="select ATTRIBUTENAME,EXTERNALTABLECOLNAME, TRANSACTIONTABLECOLNAME, ISMANDATORY from "+WSR_ATTRDETAILS+" with(nolock) where ATTRIBUTENAME='"+attributeName+"' and PROCESSID='"+processID+"'";
		}	
		sInputXML=getAPSelectWithColumnNamesXML(getExtTransQry);
		WriteLog("Input XML: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		String attNameFromTable=getTagValues(sOutputXML, "ATTRIBUTENAME");
		String extFlag=getTagValues(sOutputXML, "EXTERNALTABLECOLNAME");
		String transFlag=getTagValues(sOutputXML, "TRANSACTIONTABLECOLNAME");
		String isMandatory=getTagValues(sOutputXML, "ISMANDATORY");
		
		WriteLog("EXTERNALTABLECOLNAME: "+extFlag+ "TRANSACTIONTABLECOLNAME: "+transFlag+" PROCESSID: "+processID+" ISMANDATORY: "+isMandatory+" attNameFromTable:"+attNameFromTable);
			
		//Added by Nikita for the checking length of the attribute value at 14062019
		
				if(sProcessName.equalsIgnoreCase("RAOP"))
				{
					
					String strattributesize=getTagValues(sOutputXML, "ATTRIBUTE_FORMAT");
					String attType = getTagValues(sOutputXML, "ATTRIBUTE_TYPE");
					attributeValue=attributeValue.trim();
					if(!(attributeValue.equalsIgnoreCase("")))
					{
						int attributelength = attributeValue.length();
						WriteLog("attributelength--"+attributelength);
						WriteLog("attNameFromTable--"+attNameFromTable);
						if(!attType.equalsIgnoreCase(""))
						{
							if(attType.equalsIgnoreCase("DATE"))
							{
								Date date = null;
								SimpleDateFormat sdf = new SimpleDateFormat(strattributesize);
							    date = sdf.parse(attributeValue);
							    if (!attributeValue.equals(sdf.format(date))) 
							    {
							    	throw new WICreateException("1116",pCodes.getString("1116")+" :"+attributeName);
							    }
							}
							else if(attType.equalsIgnoreCase("AMOUNT"))
							{
								try
								{
									if(!attributeValue.contains("."))
										attributeValue=attributeValue+".00";
									Double.parseDouble(attributeValue);
									
								}
								catch(Exception ex)
								{
									throw new WICreateException("1117",pCodes.getString("1117")+" :"+attributeName);
								}
								
							}
							else
							{
								//WriteLog("inside 991--"+attNameFromTable);	
								int attributesize = Integer.parseInt(strattributesize);
								if(attributelength>attributesize)
								{
									throw new WICreateException("1111",pCodes.getString("1111")+" :"+attributeName);
								}
								
								String patternMatch = "";
													
								if(attType.equalsIgnoreCase("NUMERIC"))
								{	
									patternMatch="[0-9]+";
									if(!Pattern.matches(patternMatch, attributeValue))
									{
										WriteLog("inside 995");
										throw new WICreateException("1112",pCodes.getString("1112")+" :"+attributeName);
									}
									
									
								}
								
								if(attType.equalsIgnoreCase("ALPHANUMERIC"))
								{
									patternMatch="^[a-zA-Z0-9 ]*$";
									if(!Pattern.matches(patternMatch, attributeValue))
									{
										throw new WICreateException("1113",pCodes.getString("1113")+" :"+attributeName);
									}
								}
								
								if(attType.equalsIgnoreCase("ALPHAONLY"))
								{
									patternMatch="^[a-zA-Z ]*$";
									if(!Pattern.matches(patternMatch, attributeValue))
									{
										throw new WICreateException("1114",pCodes.getString("1114")+" :"+attributeName);
									}
								}
								
								if(attType.equalsIgnoreCase("APLPHANUMERICWITHSPACE"))
								{
									patternMatch="^[a-zA-Z0-9 ]*$";
									if(!Pattern.matches(patternMatch, attributeValue))
									{
										throw new WICreateException("1115",pCodes.getString("1115")+" :"+attributeName);
									}
								}
								
								if(attType.equalsIgnoreCase("APLPHANUMERICWITHSPECIALCHAR"))
								{
									patternMatch="^[a-zA-Z0-9-#_!.@()+/%&\\s|~ ]*$";
									if(!Pattern.matches(patternMatch, attributeValue))
									{
										throw new WICreateException("1118",pCodes.getString("1118")+" :"+attributeName);
										
									}
								}
							}
						}
					}
					
				}	
				//Code added by Nikita ends on 18062019
		
		if(!attNameFromTable.equalsIgnoreCase(""))
		{
			WriteLog("Attribute Name: "+attributeName);
			if(((isMandatory.equalsIgnoreCase("Y") || isMandatory.equalsIgnoreCase("N") || isMandatory.equalsIgnoreCase("C")) && !attributeValue.trim().equalsIgnoreCase("")) 
					|| ((isMandatory.equalsIgnoreCase("N") || isMandatory.equalsIgnoreCase("C")) && attributeValue.trim().equalsIgnoreCase("")))
			{
				if (!extFlag.equalsIgnoreCase("") && transFlag.equalsIgnoreCase(""))
				{
					if (attributeValue.contains("'"))
						attributeValue = attributeValue.replace("'", "");
					//added for RAOP Process for 2nd call
					if("RAOP_YAP".equalsIgnoreCase(processID))
					{	
						
						if (extFlag.equalsIgnoreCase("CUSTOMERAUTHENTICATED"))
						{
							sCUST_AUTHENTICATION = attributeValue;
							WriteLog("sCUST_AUTHENTICATION 1---"+attributeValue);
						}
						else if(extFlag.equalsIgnoreCase("YAP_CUST_AUTHENTICATED"))
						{	sCUST_AUTHENTICATION = attributeValue;
							WriteLog("sCUST_AUTHENTICATION 11---"+sCUST_AUTHENTICATION);
						}	
					}
					
					//added for RAOP Process for Additional Info
					if("RAOP_YAP_AddInfo".equalsIgnoreCase(processID))
					{	
						if(extFlag.equalsIgnoreCase("REMARKS"))
						{	sRemarksAddInfo = attributeValue;
							WriteLog("sRemarksAddInfo 11---"+sRemarksAddInfo);
						}	
					}
					
					attributeTag=attributeTag+extFlag+(char)21+attributeValue.trim()+(char)25;
					extColNames=extColNames+extFlag+",";
					extColValues=extColValues+"'"+attributeValue.trim()+"'"+",";
				}
				else
					 throw new WICreateException("1015",pCodes.getString("1015")+" :"+attributeName);
			}
			else if(isMandatory.equalsIgnoreCase("Y") && attributeValue.equalsIgnoreCase(""))
			{
				 throw new WICreateException("1016",pCodes.getString("1016")+" :"+attributeName);
			}
		}
		else
		{	
			WriteLog("No Value Mapped for name");
			throw new WICreateException("1017",pCodes.getString("1017")+" :"+attributeName);
		}
		
	}
	
	private String getWorkitemID() throws WICreateException, Exception
	{
		String WorkitemId="";
		String strActivity_name="";
		try{
			if (ActivityName.toUpperCase().contains("CUSTOMER_HOLD")){
				strActivity_name = "CUSTOMER_HOLD";
			}
			String getWorkitemIDQry="select workitemid  from QUEUEVIEW with (nolock) where PROCESSINSTANCEID='"+WINumber+"' and ActivityName='"+strActivity_name+"'";
			sInputXML=getAPSelectWithColumnNamesXML(getWorkitemIDQry);
			WriteLog("Input XML: "+sInputXML);
			sOutputXML=executeAPI(sInputXML);
			WriteLog("Output XML: "+sOutputXML);
			xmlobj=new XMLParser(sOutputXML);
			checkCallsMainCode(xmlobj);
			WorkitemId=getTagValues(sOutputXML, "workitemid");
			WriteLog("WorkitemId: "+WorkitemId);
		}
		catch(Exception e){
			WriteLog("Exception occured while retriving workitem ID: "+e.getMessage());
			WorkitemId="1";
		}
		//from tables here
		
		return WorkitemId;
	}
	private void getTableName() throws WICreateException, Exception
	{
		//from tables here
		String getTableNameQry="select EXTERNALTABLE ,HISTORYTABLE  from "+WSR_PROCESS+" where PROCESSID='"+processID+"'";
		sInputXML=getAPSelectWithColumnNamesXML(getTableNameQry);
		WriteLog("Input XML: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		externalTableName=getTagValues(sOutputXML, "EXTERNALTABLE");
		WriteLog("External table Name: "+externalTableName);
		HISTORYTABLE=getTagValues(sOutputXML, "HISTORYTABLE");
		WriteLog("Transaction table Name: "+HISTORYTABLE);
	}
	private void getWorkitemStage(String attributeList[]) throws WICreateException, Exception
	{
		//from tables here
		String attrName="";
	    String attrValue="";
	    String strOperationName="";
		for(int i=0;i<attributeList.length;i++)
    	{
    		attrName=getTagValues(attributeList[i],"Name");
    		attrValue=getTagValues(attributeList[i],"Value");
    		if (attrName.equalsIgnoreCase("WINUMBER")){
    			WINumber="";
    			WINumber = attrValue;
    		}
    		if("OperationName".equalsIgnoreCase(attrName)){
    			strOperationName=attrValue;
    		}
    	}
		String getTableNameQry="select ACTIVITYNAME  from QUEUEVIEW with (nolock) where PROCESSINSTANCEID='"+WINumber+"'";
		sInputXML=getAPSelectWithColumnNamesXML(getTableNameQry);
		WriteLog("Input XML to Get Activity Name: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML to Get Activity Name: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		ActivityName=getTagValues(sOutputXML, "ACTIVITYNAME");
		WriteLog("ActivityName from QueueView: "+ActivityName);
		response.setWorkitemNumber(WINumber);
	}
	private void getPossibleUpdateQueues() throws WICreateException, Exception
	{
		//from tables here
		String getTableNameQry="select QUEUENAME from "+WSR_POSSIBLEQUEUES+" with (nolock) where ProcessName='"+sProcessName+"'";
		sInputXML=getAPSelectWithColumnNamesXML(getTableNameQry);
		WriteLog("Input XML to Get Possible Update Queues: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML to Get Possible Update Queues: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		PossibleQueues=getTagValues(sOutputXML, "QUEUENAME");
		WriteLog("PossibleQueues from Master: "+PossibleQueues);
	}
	
	private String getRAOPNationality() throws WICreateException, Exception
	{
		//from tables here
		String getTableNameQry="select NATIONALITY from RB_RAOP_EXTTABLE with(nolock) where WINAME ='"+WINumber+"'";
		String sInputXML=getAPSelectWithColumnNamesXML(getTableNameQry);
		WriteLog("Input XML to Get RAOP Nationality: "+sInputXML);
		String sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML to Get RAOP Nationality: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		String Nationality=getTagValues(sOutputXML, "NATIONALITY");
		WriteLog("Nationality is: "+Nationality);
		return Nationality;
	}
	
	
	private String getRAOPAddInfoStatus() throws WICreateException,Exception
	{
		String getTableNameQry="select PREVWS_InfoReqCust from RB_RAOP_EXTTABLE with(nolock) where WINAME='"+WINumber+"'";
		sInputXML=getAPSelectWithColumnNamesXML(getTableNameQry);
		WriteLog("Input XML to Get RAOP PREVWS_InfoReqCust Status: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML to Get RAOP PREVWS_InfoReqCust Status: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		String PREVWS_InfoReqCust =getTagValues(sOutputXML, "PREVWS_InfoReqCust");
		WriteLog("RAOP PREVWS_InfoReqCust Status from External Table: "+PREVWS_InfoReqCust);
		return PREVWS_InfoReqCust;
	
	}
	
	private void validateRequestParameters(String attributeList[]) throws WICreateException,Exception
	{
		WriteLog("Inside validateRequestParameters");
		if(sProcessName.equalsIgnoreCase("?") || sProcessName.equalsIgnoreCase(""))
			throw new WICreateException("1001",pCodes.getString("1001"));
		
		if(("?".equalsIgnoreCase(sSubProcess) || "".equalsIgnoreCase(sSubProcess)))
			throw new WICreateException("1002",pCodes.getString("1002"));
		
		if(sRequestorChannelId.equalsIgnoreCase("?") || sRequestorChannelId.equalsIgnoreCase(""))
			throw new WICreateException("1011",pCodes.getString("1011"));
		//Fetch ProcessDefID
		getProcessDefID();	
		
		// Checking update is possible for workitem or not		
		getWorkitemStage(attributeList);
		if (ActivityName.equalsIgnoreCase("")) // Activityname blank means, workitem doesn't exists in flow
		{
			throw new WICreateException("5052",pCodes.getString("5052")+": "+WINumber);
		}	
				
		//If User has taken the decision at Remittance Checker as Approve then Update will not be possible
		
		//Modified by Nikita on 06062019 for RAOP Update
		/*if(processID.equalsIgnoreCase("RAOP_YAP") || processID.equalsIgnoreCase("RAOP_YAP_AddInfo"))
		{*/
			getPossibleUpdateQueues();
		//}
		/*if("RMT".equalsIgnoreCase(processID))
		{*/		
			if (PossibleQueues.contains(ActivityName))
			{
				WriteLog("Update request is possible at this queue: "+ActivityName);
				
				if("RAOP_YAP".equalsIgnoreCase(processID))
				{
					String RAOPAddInfoStatus = getRAOPAddInfoStatus().trim();
					if("".equalsIgnoreCase(RAOPAddInfoStatus) || "NULL".equalsIgnoreCase(RAOPAddInfoStatus))
					{
						// Nothing to do - if this value is blank means workitem is waiting for 2nd call from YAP
					} else {
						WriteLog("RAOP_YAP Update request is not possible as Add Info from Customer was asked: "+RAOPAddInfoStatus);
						throw new WICreateException("5050E",pCodes.getString("5050E"));
					}
				}
				else if("RAOP_YAP_AddInfo".equalsIgnoreCase(processID))
				{
					String RAOPAddInfoStatus = getRAOPAddInfoStatus().trim();
					if("".equalsIgnoreCase(RAOPAddInfoStatus) || "NULL".equalsIgnoreCase(RAOPAddInfoStatus))
					{
						WriteLog("RAOP_YAP_AddInfo Update request is not possible as WI is waiting for the second call: "+RAOPAddInfoStatus);
						throw new WICreateException("5050D",pCodes.getString("5050D"));
					} else {
						// Nothing to do - if this value is not blank means workitem is waiting for Additional Info from YAP
					}
				}
			}
		//}
		
		
		//Check if all Mandatory attributes present in USR_0_WSR_UPDATE_ATTRDETAILS have come
		checkMandatoryAttribute();
		
		//Start - Checking conditional Mandatory when customer is authenticated added by Angad on 04122019
		if("RAOP_YAP".equalsIgnoreCase(processID))
		{
			checkConditionalMandatoryAttributeRAOP();
		}
		
		getTableName();
		
		// validating date field format in received Request
		validateInputValuesFunction(attributeList);
		
	}
	private void checkMandatoryAttribute() throws WICreateException, Exception
	{
		WriteLog("inside checkMandatoryAttribute");
		sInputXML=getAPSelectWithColumnNamesXML("select ATTRIBUTENAME from "+WSR_ATTRDETAILS+" where PROCESSID='"+processID+"' and ISMANDATORY='Y' and ISACTIVE='Y'");
		WriteLog("Input XML: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		String attributeList []=getTagValues(sOutputXML,"ATTRIBUTENAME").split("`");
		if(attributeList.length>0)
		{
			for(int i=0;i<attributeList.length;i++)
			{
				String flag="N";
				//Iterate through the Hash Map
				Set<?> set = hm.entrySet();
			    // Get an iterator
			    Iterator<?> j = set.iterator();
			    // Display elements
			    while(j.hasNext())
			    {
			        Map.Entry me = (Map.Entry)j.next();
			        if(me.getKey().toString().equalsIgnoreCase(attributeList[i]))
			        	flag="Y";
			   
			    }
			    //WriteLog("Value of flag: "+flag);
			    if(flag.equalsIgnoreCase("N"))
					throw new WICreateException("1020",pCodes.getString("1020")+": "+attributeList[i]);
			}
		}
		else
		{
			throw new WICreateException("1021",pCodes.getString("1021")+" for process id: "+processID);
		}
			
	}
	
	private void checkConditionalMandatoryAttributeRAOP() throws WICreateException, Exception
	{
		WriteLog("inside checkConditionalMandatoryAttributeRAOP");
		sInputXML=getAPSelectWithColumnNamesXML("select ATTRIBUTENAME from "+WSR_ATTRDETAILS+" where PROCESSID='"+processID+"' and ISMANDATORY='C' and ISACTIVE='Y'");
		WriteLog("Input XML: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		String attributeList []=getTagValues(sOutputXML,"ATTRIBUTENAME").split("`");
		
		if (hm.containsKey("CUSTOMERAUTHENTICATED"))
			sCUST_AUTHENTICATION=hm.get("CUSTOMERAUTHENTICATED");
		
		if(sCUST_AUTHENTICATION.trim().equalsIgnoreCase("Y"))
		{
			if(attributeList.length>0)
			{
				for(int i=0;i<attributeList.length;i++)
				{
					String flag="N";
					//Iterate through the Hash Map
					Set<?> set = hm.entrySet();
				    // Get an iterator
				    Iterator<?> j = set.iterator();
				    // Display elements
				    while(j.hasNext())
				    {
				        Map.Entry me = (Map.Entry)j.next();
				        if(me.getKey().toString().equalsIgnoreCase(attributeList[i])){
				        	flag="Y";
				        	break;
				        }
				    }
				    //WriteLog("Value of flag: "+flag);
				    if(flag.equalsIgnoreCase("N"))
						throw new WICreateException("1020",pCodes.getString("1020")+": "+attributeList[i]);
				    
				    String tempValue = "";
				    if (hm.containsKey(attributeList[i]))
						tempValue=hm.get(attributeList[i]);
					
					if (tempValue.trim().equalsIgnoreCase(""))
					{
						throw new WICreateException("1016",pCodes.getString("1016")+": "+attributeList[i]);
					}
					
				}
			}
			
			// Start - Visa validation for GCC Nationalities added on 14022021 by Angad
			String sVISAEXPDATE = "";
			String sVISAFILENUMBER = "";
			String sVISAISSUEDATE = "";
			String sVISAUIDNUMBER = "";
			if (hm.containsKey("VISAEXPDATE"))
				sVISAEXPDATE=hm.get("VISAEXPDATE");
			if (hm.containsKey("VISAFILENUMBER"))
				sVISAFILENUMBER=hm.get("VISAFILENUMBER");
			if (hm.containsKey("VISAISSUEDATE"))
				sVISAISSUEDATE=hm.get("VISAISSUEDATE");
			if (hm.containsKey("VISAUIDNUMBER"))
				sVISAUIDNUMBER=hm.get("VISAUIDNUMBER");
			
			if("".equalsIgnoreCase(sVISAFILENUMBER) || "".equalsIgnoreCase(sVISAUIDNUMBER) || "".equalsIgnoreCase(sVISAISSUEDATE) || "".equalsIgnoreCase(sVISAEXPDATE))
			{	
				String Nationality = getRAOPNationality().trim();
				if(!"".equalsIgnoreCase(Nationality) && !"null".equalsIgnoreCase(Nationality) && Nationality != null
						&& !"AE".equalsIgnoreCase(Nationality) && !"BH".equalsIgnoreCase(Nationality) && !"KW".equalsIgnoreCase(Nationality) && !"OM".equalsIgnoreCase(Nationality) && !"QA".equalsIgnoreCase(Nationality) && !"SA".equalsIgnoreCase(Nationality))
				{
					if("".equalsIgnoreCase(sVISAFILENUMBER))
						throw new WICreateException("5053A",pCodes.getString("5053A"));
					else if("".equalsIgnoreCase(sVISAUIDNUMBER))
						throw new WICreateException("5053B",pCodes.getString("5053B"));	
					else if("".equalsIgnoreCase(sVISAISSUEDATE))
						throw new WICreateException("5053C",pCodes.getString("5053C"));	
					else if("".equalsIgnoreCase(sVISAEXPDATE))
						throw new WICreateException("5053D",pCodes.getString("5053D"));
				}
			}
			// End - Visa validation for GCC Nationalities added on 14022021 by Angad
			
			//Start - Industry SubSegment mandatory for employment type ‘Self employed’ or ‘A’
			String sEMPLOYMENTTYPE = "";
			if (hm.containsKey("EMPLOYMENTTYPE"))
				sEMPLOYMENTTYPE=hm.get("EMPLOYMENTTYPE");
			if("Self employed".equalsIgnoreCase(sEMPLOYMENTTYPE.trim()) || "A".equalsIgnoreCase(sEMPLOYMENTTYPE.trim()))
			{	
				String sINDUSTRYSUBSEGMENT = "";
				if (hm.containsKey("INDUSTRYSUBSEGMENT"))
					sINDUSTRYSUBSEGMENT=hm.get("INDUSTRYSUBSEGMENT");
				if("".equalsIgnoreCase(sINDUSTRYSUBSEGMENT.trim()))
					throw new WICreateException("5053E",pCodes.getString("5053E"));
			}
			//End - Industry SubSegment mandatory for employment type ‘Self employed’ or ‘A’ 
		}
	}
	
	private void loadResourceBundle()
	{
		WriteLog("inside loadResourceBundle");
		pCodes= PropertyResourceBundle.getBundle("com.newgen.ws.config.StatusCodes");
		if(pCodes==null)
			WriteLog("Error in loading status codes");
		else
			WriteLog("Status Codes loaded successfully");

	}
	 
	private String getAPSelectWithColumnNamesXML(String sQuery)
	{
		
		return 	 "<?xml version='1.0'?>" +
				 "<APSelectWithColumnNames_Input>" +
				 "<Option>APSelectWithColumnNames</Option>" +
				 "<Query>" + sQuery + "</Query>" +
				 "<EngineName>"+sCabinetName+"</EngineName>" +
				 "</APSelectWithColumnNames_Input>";
		
	}
	public static String completeWorkItemInput(String cabName, String sessionID, String workItemName, String WorkItemID){

		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<WMCompleteWorkItem_Input>\n");
		ipXMLBuffer.append("<Option>WMCompleteWorkItem</Option>");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("<ProcessInstanceId>");
		ipXMLBuffer.append(workItemName);
		ipXMLBuffer.append("</ProcessInstanceId>\n");
		ipXMLBuffer.append("<WorkItemId>");
		ipXMLBuffer.append(WorkItemID);
		ipXMLBuffer.append("</WorkItemId>\n");
		ipXMLBuffer.append("<AuditStatus></AuditStatus>\n");
		ipXMLBuffer.append("<Comments></Comments>\n");
		ipXMLBuffer.append("</WMCompleteWorkItem_Input>");

		return ipXMLBuffer.toString();
	}
	
	private String getWMConnectXML()
	{
		
		return "<?xml version=\"1.0\"?>"+
	    "<WMConnect_Input>"+
		"<Option>WMConnect</Option>"+
		"<EngineName>"+sCabinetName+"</EngineName>"+
		"<Participant>"+
		"<Name>"+sUsername+"</Name>"+
		"<Password>"+sPassword+"</Password>"+
		"</Participant>"+
		"</WMConnect_Input>";		
			
	}
	
 
private static String getTagValues (String sXML, String sTagName) 
{  
		String sTagValues = "";
		String sStartTag = "<" + sTagName + ">";
		String sEndTag = "</" + sTagName + ">";
		String tempXML = sXML;
    try	{
	  
		for(int i=0;i<sXML.split(sEndTag).length;i++) 
		{
			if(tempXML.indexOf(sStartTag) != -1) 
			{
				sTagValues += tempXML.substring(tempXML.indexOf(sStartTag) + sStartTag.length(), tempXML.indexOf(sEndTag));
				//System.//out.println("sTagValues"+sTagValues);
				tempXML=tempXML.substring(tempXML.indexOf(sEndTag) + sEndTag.length(), tempXML.length());
	        }
			if(tempXML.indexOf(sStartTag) != -1) 
			{    
				sTagValues +="`";
				//System.//out.println("sTagValues"+sTagValues);
				
			}
			//System.//out.println("sTagValues"+sTagValues);
		}
		//System.//out.println(" Final sTagValues"+sTagValues);
	}

	catch(Exception e) 
	{   
	}
		return sTagValues;
}

private String decryptPassword(String pass)
{
	int len = pass.length();
	byte[] data = new byte[len / 2];
	for (int i = 0; i < len; i += 2) {
		data[i / 2] = (byte) ((Character.digit(pass.charAt(i), 16) << 4)
				+ Character.digit(pass.charAt(i+1), 16));
	}
	String password=OSASecurity.decode(data,"UTF-8");
	return password;
}


private void validateInputValuesFunction(String attributeList[]) throws WICreateException
{
	if (sProcessName.equalsIgnoreCase("RMT"))
	{
		String attrName="";
	    String attrValue="";
	    String find="";
		for(int i=0;i<attributeList.length;i++)
		{
			attrName=getTagValues(attributeList[i],"Name");
			attrValue=getTagValues(attributeList[i],"Value");
			find=validateInputValues(attrName,attrValue);
			if(find.equalsIgnoreCase("false"))
			{
				break;
			}
		}
		//System.out.println("outside "+find+" attrName "+attrName);
		if(find.equalsIgnoreCase("false"))
		{
			
			if(attrName.equalsIgnoreCase("PASSPORTEXPIRYDATE")||
			attrName.equalsIgnoreCase("VISAISSUEDATE")||
			attrName.equalsIgnoreCase("VISAEXPIRYDATE"))
			{
				throw new WICreateException("1005", "Invalid date format for field:" +attrName+" Format should be YYYY-MM-DD format");
			}
		}
	}
}


private String validateInputValues(String attrName,String attrValues)
{
	String found="true";
	
	if((attrName.equalsIgnoreCase("PASSPORTEXPIRYDATE") && !(attrValues.trim().length()==0))
	||(attrName.equalsIgnoreCase("VISAISSUEDATE") && !(attrValues.trim().length()==0))
	||(attrName.equalsIgnoreCase("VISAEXPIRYDATE") && !(attrValues.trim().length()==0)))
	{
		return validateDateFormat(attrValues);
	}
	
	return found;
}

//This function validate the date format in YYYY-MM-DD format
private static String validateDateFormat(String input) 
{
	Date date = null;
	try 
	{
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    date = sdf.parse(input);
	    if (!input.equals(sdf.format(date))) 
	    {
	        date = null;
	    }
	} 
	catch (ParseException ex) 
	{
     return "false";
	}
	if (date == null) 
	{
		return "false";
	} 
	else 
	{
		return "true";
	}	
}

//Added by Sajan for FALCON CDOB
private String getBankDescFromCode(){
	String strBankDesc="";
	try{
	
	sInputXML = getAPSelectWithColumnNamesXML("select top 1 Description from NG_MASTER_BankName with (nolock) where Code='"+hm.get("BankName")+"'");
	WriteLog("Get Bank desc xml: " + sInputXML);
	sOutputXML = executeAPI(sInputXML);
	WriteLog("Get Bank desc Output xml: " + sOutputXML);
	xmlobj = new XMLParser(sOutputXML);
	// Check Main Code
	checkCallsMainCode(xmlobj);
	strBankDesc = getTagValues(sOutputXML, "Description");
	WriteLog("Bank Name is : " + strBankDesc);
	
	}
	catch(WICreateException ex){
		WriteLog("WICreate exception thrown in getBankCodeFromDesc "+ex.getMessage());
	}
	catch(Exception e){
		WriteLog("Generic exception in getBankDescFromCode "+e.getMessage());
	}
	return strBankDesc;
}

private void doneworkitem()throws WICreateException
{
	try
	{
	//Lock Workitem.
	String getWorkItemInputXML = getWorkItemInput();
	String getWorkItemOutputXml = executeAPI(getWorkItemInputXML);
	WriteLog("Output XML For WmgetWorkItemCall: "+ getWorkItemOutputXml);

	XMLParser xmlParserGetWorkItem = new XMLParser(getWorkItemOutputXml);
	String getWorkItemMainCode = xmlParserGetWorkItem.getValueOf("MainCode");
	WriteLog("WmgetWorkItemCall Maincode:  "+ getWorkItemMainCode);

	if (getWorkItemMainCode.trim().equals("0")) 
	{
		WriteLog("WMgetWorkItemCall Successful: "+getWorkItemMainCode);
							
			//Move Workitem to next Workstep 
			String completeWorkItemInputXML = completeWorkItemInput();
			WriteLog("Input XML for wmcompleteWorkItem: "+ completeWorkItemInputXML);

			String completeWorkItemOutputXML = executeAPI(completeWorkItemInputXML);
			WriteLog("Output XML for wmcompleteWorkItem: "+ completeWorkItemOutputXML);

			XMLParser xmlParserCompleteWorkitem = new XMLParser(completeWorkItemOutputXML);
			String completeWorkitemMaincode = xmlParserCompleteWorkitem.getValueOf("MainCode");
			WriteLog("Status of wmcompleteWorkItem  "+ completeWorkitemMaincode);

			if (completeWorkitemMaincode.trim().equalsIgnoreCase("0")) 
			{
				WriteLog("WmCompleteWorkItem successful: "+completeWorkitemMaincode);
				System.out.println(WINumber + "Complete Sussesfully with status ");
				WriteLog("WorkItem moved to next Workstep.");

			}
			else 
			{
				completeWorkitemMaincode="";
				WriteLog("WMCompleteWorkItem failed: "+completeWorkitemMaincode);
			}
		
		
	}
	else
	{
		getWorkItemMainCode="";
		WriteLog("WmgetWorkItem failed: "+getWorkItemMainCode);
	}
	
	}
	catch(Exception e)
	{
		WriteLog("Exception: "+e.getMessage());
		throw new WICreateException("1007",pCodes.getString("1007")+" : "+e.getMessage());
	}
}

  


}
