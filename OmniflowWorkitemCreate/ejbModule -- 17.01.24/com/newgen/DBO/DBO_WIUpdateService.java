package com.newgen.DBO;

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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import com.newgen.custom.CreateWorkitem;
/*import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.omni.wf.util.excp.NGException;*/
import com.newgen.ws.EE_EAI_HEADER;
import com.newgen.ws.exception.WICreateException;
import com.newgen.ws.request.Attribute;
import com.newgen.ws.request.Attributes;
import com.newgen.ws.request.WICreateRequest;
import com.newgen.ws.response.WICreateResponse;
import com.newgen.ws.util.XMLParser;

import adminclient.OSASecurity;

public class DBO_WIUpdateService extends CreateWorkitem {

	
	
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
	private String awbColNames = "";
	private String awbColValues = "";
	private String awbNumber = "";
	private String awbDeliveryStatusCode = "";
	private String awbDeliveryStatusDesc = "";
	private String RelatedPartyID="";
	private String ProspectID="";
	private String pingDataColNames = "";
	private String pingDataColValues = "";
	String sFilePath = "";
	boolean sessionFlag = false;
	private String processID = "";
	private String trTableColumn = "";
	private String trTableValue = "";
	private String sWhereClause ="";
	private String wiName = "";
	private String externalTableName = "";
	private String HISTORYTABLE = "";
	private String txnTableName="RB_DBO_TXNTABLE";
	private String processDefID = "";
	private String workitemID = "";
	private String sDate = "";
	
	
	
	private String WINumber = "";
	private String ActivityName="";
	private String PossibleQueues="";
	private String readyForDone="";
	
	//for RAOP process
	private String sCUST_AUTHENTICATION="";
	private String sRemarksAddInfo="";
	
	String InputMessage = "";
	String RepetitiveMainTags = "";
	
	LinkedHashMap<String, HashMap<String, String>> hmMain = new LinkedHashMap();
	
	static HashMap<String, String> hmRptProcessId = new HashMap();
    static HashMap<String, String> hmRptTransTable = new HashMap();
    static LinkedHashMap<String, HashMap<String, String>> hmRptAttrAndCol = new LinkedHashMap();
    static LinkedHashMap<String, HashMap<String, String>> hmRptAttrAndMand = new LinkedHashMap();
    static LinkedHashMap<String, HashMap<String, String>> hmRptAttrAndFormat = new LinkedHashMap();
    static LinkedHashMap<String, HashMap<String, String>> hmRptAttrAndType = new LinkedHashMap();
   // private static NGEjbClient ngEjbClient;
	public WICreateResponse wiUpdate(WICreateRequest request,String attributeList[]) throws WICreateException { 
		
			try
			{
				WriteLog("inside wiUpdate for DBO process");
				headerObjResponse=new EE_EAI_HEADER();
				//ngEjbClient = NGEjbClient.getSharedInstance();
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
				
				validateRepetitiveRequestParameters();
				
				//Checking existing session
				checkExistingSession();
				
				attributeTag=attributeTag+createRepetativeAttributeXML();
				
				getWorkitemID();
        		if("DBO_COURIER_UPDATE".equalsIgnoreCase(processID))
        		{
        			updateInDataAWBTableDBO();
        			if(pingDataColNames != null && !"".equalsIgnoreCase(pingDataColNames))
        				insertIntoEIDAPingDataDBO();
        			if("DD".equalsIgnoreCase(awbDeliveryStatusCode) || "OFD".equalsIgnoreCase(awbDeliveryStatusCode))
        			{
        				getDataForWI();
        				insertDataInNotifyTable();
        			}
        			else if("RTO".equalsIgnoreCase(awbDeliveryStatusCode))
        			{
        				attributeTag+="\n<DECISION>Return to Origin</DECISION>";
        				doneworkitem();
        			}
        			if("0".equalsIgnoreCase(checkForDoneWI().trim()))
					{
        				readyForDone="Yes";
        				//markDoneForAll
        				updateAWBstatusForDeliverToALL();
        				attributeTag+="\n<DECISION>Success</DECISION>";
        				doneworkitem();
        			}
        			insertIntoHistoryDBO(); // inserting in history table
        			// need to write further logic - when to Done  -- when to just update data and wait.
        		}
        		else
        		{
        			doneworkitem();
                	insertIntoHistoryDBO(); // inserting in history table
        		}
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
				hmMain.clear();
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
	
	private void runUpdateWICall() throws WICreateException,Exception
	{
		sInputXML=getWFAssignAtributeWorkItemXML();
		WriteLog("WFAssignAtributeWorkItemXML Input: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("WFAssignAtributeWorkItemXML Output: "+sOutputXML);
    	xmlobj=new XMLParser(sOutputXML);
    	
    	String assignWorkitemAttributeMainCode = getTagValues(sOutputXML,"MainCode");
    	
    	if(!"0".equalsIgnoreCase(assignWorkitemAttributeMainCode))
		{
			// unlock workitem
			
			sInputXML=unlockWorkItemInput_withWorkitemID(workitemID);
			WriteLog("WFUnlockWorkItemXML Input: "+sInputXML);
			sOutputXML=executeAPI(sInputXML);
			WriteLog("WFUnlockWorkItemXML Output: "+sOutputXML);
		}
    	
    	//Check Main Code of assign
		checkCallsMainCode(xmlobj);  
	}
	
	private String getWFAssignAtributeWorkItemXML()
	{
				
		return "<?xml version=\"1.0\"?><WMAssignWorkItemAttributes_Input>"
				+ "<Option>WMAssignWorkItemAttributes</Option>"
				+ "<EngineName>"+sCabinetName+"</EngineName>"
				+ "<SessionId>"+sSessionID+"</SessionId>"
				+ "<ProcessInstanceId>"+WINumber+"</ProcessInstanceId>"
				+ "<WorkItemId>"+workitemID+"</WorkItemId>"
				+ "<ProcessDefId>"+processDefID+"</ProcessDefId>"
				+ "<LastModifiedTime></LastModifiedTime>"
				+ "<complete>S</complete>"
				+ "<AuditStatus></AuditStatus>"
				+ "<Comments></Comments>"
				+ "<UserDefVarFlag>Y</UserDefVarFlag>"
				+ "<Attributes>"+attributeTag+"</Attributes>"
				+ "</WMAssignWorkItemAttributes_Input>";
	}
	
	private void insertIntoHistoryDBO() throws WICreateException, Exception 
	{

		trTableColumn = "WINAME,workstep,decision,action_date_time,remarks,user_name,entry_date_time";
		WriteLog("trTableColumn" + trTableColumn);
		String Remarks = "Update Request Recevied - workitem updated successfully";
		String Decision = "Update Received";
		Date d= new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sDate = dateFormat.format(d);
		if(processID.equalsIgnoreCase("DBO_FULFILLMENT"))
		{	
			Remarks = "Fulfillment Update received and updated successfully";
			Decision = "Fulfillment Data Updated";
		}	
		else if(processID.equalsIgnoreCase("DBO_INFO_UPDATE"))
		{	
			Remarks = "Data Update received and updated successfully";
			Decision = "Data Updated";
		}
		else if(processID.equalsIgnoreCase("DBO_ADDNL_INFO_DOC"))
		{	
			Remarks = "Query or Document response received and updated successfully";
			Decision = "Query Doc Response Received";
		}
		else if(processID.equalsIgnoreCase("DBO_DECLINE"))
		{	
			Remarks = "Workitem Declined by DEH or Withdrawn by customer";
			Decision = "Declined";
		}
		else if(processID.equalsIgnoreCase("DBO_COURIER_UPDATE"))
		{	
			Remarks = "Courier Update received for AWB Number " + hm.get("AWB_Number")+ " with Status "+ awbDeliveryStatusDesc+" and updated successfully";
			Decision = awbDeliveryStatusCode+" Courier Update Received";
		}
		else if(processID.equalsIgnoreCase("DBO_FE_CORRECTION"))
		{	
			Remarks = "Front Correction Update received and updated successfully";
			Decision = "FE Correction Data Updated";
		}
		trTableValue = "'"+ WINumber + "','"+ActivityName+"','"+Decision+"','" + sDate + "','"+Remarks+"','" + sUsername + "','" + getEntryDatetimefromDB() + "'";
		WriteLog("trTableValue" + trTableValue);
		sInputXML = getDBInsertInput(HISTORYTABLE,trTableColumn,trTableValue);
		WriteLog("APInsert Input History: " + sInputXML);
		sOutputXML = executeAPI(sInputXML);
		WriteLog("APInsert Output History: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	
	private void insertIntoEIDAPingDataDBO() throws WICreateException, Exception {

		pingDataColNames = "WINAME,AWBNumber,PingDateTime,"+pingDataColNames;
		WriteLog("EIDA Ping Data Columns" + pingDataColNames);
		
		Date d= new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sDate = dateFormat.format(d);
		
		pingDataColValues = "'"+ WINumber + "','"+awbNumber+"','" + sDate + "',"+pingDataColValues;
		WriteLog("pingDataColValues" + pingDataColValues);
		sInputXML = getDBInsertInput("USR_0_DBO_EIDAPINGDATA",pingDataColNames,pingDataColValues);
		WriteLog("APInsert Input EIDA Ping Data: " + sInputXML);
		sOutputXML = executeAPI(sInputXML);
		WriteLog("APInsert Output EIDA Ping Data: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	private void insertDataInNotifyTable() throws WICreateException, Exception {

		String notifyColNames = "WINAME,ProspectID,RelatedPartyID,NotifyDEHEvent,AWBNo,DeliveryStatus,StatusCode";
		WriteLog("Notify Table Columns" + notifyColNames);
		
		Date d= new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sDate = dateFormat.format(d);
		
		String notifyColValues = "'"+ WINumber + "','"+ProspectID+"','"+RelatedPartyID+"','WI_STATUS','"+awbNumber+"','" + awbDeliveryStatusDesc + "','"+awbDeliveryStatusCode+"'";
		WriteLog("notifyColValues" + notifyColValues);
		sInputXML = getDBInsertInput("USR_0_DBO_DEH_Notification",notifyColNames,notifyColValues);
		WriteLog("APInsert Input Notify Table: " + sInputXML);
		sOutputXML = executeAPI(sInputXML);
		WriteLog("APInsert Output Notify Table: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	public String getEntryDatetimefromDB() throws WICreateException, Exception
	{
		WriteLog("Start of function getEntryDatetimefromDB ");
		String entryDatetimeAttachCust="";
		String formattedEntryDatetime="";
		String outputXMLEntryDate=null;
		String sqlQuery = "select entryat from "+externalTableName+" with(nolock) where WINAME='"+WINumber+"'";
		String InputXMLEntryDate =getAPSelectWithColumnNamesXML(sqlQuery);
		WriteLog("Getting getIntegrationErrorDescription from exttable table "+InputXMLEntryDate);
		outputXMLEntryDate = executeAPI(InputXMLEntryDate);
		WriteLog("OutputXML for getting getIntegrationErrorDescription from external table "+outputXMLEntryDate);
		xmlobj = new XMLParser(outputXMLEntryDate);
		checkCallsMainCode(xmlobj);
		entryDatetimeAttachCust = getTagValues(outputXMLEntryDate, "entryat");

		SimpleDateFormat inputDateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		SimpleDateFormat outputDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");

		Date entryDatetimeFormat = inputDateformat.parse(entryDatetimeAttachCust);
		formattedEntryDatetime=outputDateFormat.format(entryDatetimeFormat);
		WriteLog("FormattedEntryDatetime: "+formattedEntryDatetime);
		WriteLog("newentrydatetime "+ formattedEntryDatetime);
		
		return formattedEntryDatetime;
	}
	
	private void updateFircoIsFrozenFlagInDBO(String RelatedParties) throws WICreateException, Exception {
		
		String colName="Is_Frozen";
		String colVal="'Y'";
		
		WriteLog("Column FircoIsFrozen DBO" + colName);
		WriteLog("Value FircoIsFrozen DBO" + colVal);
		String WhereClause = "WINAME='"+WINumber+"' and RelatedPartyID in ("+RelatedParties+")";
		sInputXML = getInputUpdateTable("USR_0_DBO_FIRCO_DTLS",colName,colVal, WhereClause);
		WriteLog("APUpdate for FircoIsFrozen: " + sInputXML);
		sOutputXML = executeAPI(sInputXML);
		WriteLog("APUpdate for FircoIsFrozen: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	
	private void updateInDataAWBTableDBO() throws WICreateException, Exception {
			
		Date d= new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sDate = dateFormat.format(d);
		if("DD".equalsIgnoreCase(awbDeliveryStatusCode))
		{
			awbColNames="AWB_Status,"+awbColNames;
			awbColValues="'D',"+awbColValues;
		}
		else
		{
			awbColNames="AWB_Status,"+awbColNames;
			awbColValues="'"+awbDeliveryStatusCode+"',"+awbColValues;
		}
			
		WriteLog("awbColNames for AWB DBO" + awbColNames);
		WriteLog("awbColValues for AWB DBO" + awbColValues);
		sWhereClause = "WI_NAME='"+WINumber+"' and AWB_Number='"+awbNumber+"'";
		sInputXML = getAWBDBInputUpdateTable();
		WriteLog("APUpdate for AWB DBO in AWB Status Table: " + sInputXML);
		sOutputXML = executeAPI(sInputXML);
		WriteLog("APUpdate for AWB DBO in AWB Status Table: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	private void updateAWBstatusForDeliverToALL() throws WICreateException, Exception
	{
		awbColNames="AWB_Status";
		awbColValues="'DA'";
		WriteLog("awbColNames for AWB DBO" + awbColNames);
		WriteLog("awbColValues for AWB DBO" + awbColValues);
		sWhereClause = "WI_NAME='"+WINumber+"'";
		sInputXML = getAWBDBInputUpdateTable();
		WriteLog("APUpdate for AWB DBO in AWB Status Table: " + sInputXML);
		sOutputXML = executeAPI(sInputXML);
		WriteLog("APUpdate for AWB DBO in AWB Status Table: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	private String checkForDoneWI() throws WICreateException, Exception {
		
		sInputXML=getAPSelectWithColumnNamesXML("select Count(*) as Count from USR_0_DBO_AWB_Status with(nolock) where wi_name='"+WINumber+"' and AWB_Status !='D'");
		WriteLog("APSelectWithColumnNames Input: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("APSelectWithColumnNames Output: "+sOutputXML);
    	xmlobj=new XMLParser(sOutputXML);
    	//Check Main Code
		checkCallsMainCode(xmlobj); 
		return getTagValues(sOutputXML, "Count");
		
	}
	private String getDataForWI() throws WICreateException, Exception {
		
		sInputXML=getAPSelectWithColumnNamesXML("select RelatedPartyID,Prospect_ID  from USR_0_DBO_AWB_Status with(nolock) where wi_name='"+WINumber+"' and AWB_Number='"+awbNumber+"'");
		WriteLog("APSelectWithColumnNames Input: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("APSelectWithColumnNames Output: "+sOutputXML);
    	xmlobj=new XMLParser(sOutputXML);
    	//Check Main Code
		checkCallsMainCode(xmlobj);
		RelatedPartyID = getTagValues(sOutputXML, "RelatedPartyID");
		ProspectID = getTagValues(sOutputXML, "Prospect_ID");
		return getTagValues(sOutputXML, "Count");
		
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
	private String getAWBDBInputUpdateTable() {
		return "<?xml version=\"1.0\"?>" + "<APUpdate_Input>"
				+ "<Option>APUpdate</Option>" + "<TableName>" + txnTableName
				+ "</TableName>" + "<ColName>" + awbColNames + "</ColName>"
				+ "<Values>" + awbColValues + "</Values>" + "<WhereClause>" + sWhereClause + "</WhereClause>"
				+ "<EngineName>"
				+ sCabinetName + "</EngineName>" + "<SessionId>" + sSessionID
				+ "</SessionId>" + "</APUpdate_Input>";
	}
	
	private String getInputUpdateTable(String TableName, String ColName, String ColValues, String WhereClause) {
		return "<?xml version=\"1.0\"?>" + "<APUpdate_Input>"
				+ "<Option>APUpdate</Option>" + "<TableName>" + TableName
				+ "</TableName>" + "<ColName>" + ColName + "</ColName>"
				+ "<Values>" + ColValues + "</Values>" + "<WhereClause>" + WhereClause + "</WhereClause>"
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

	public  String unlockWorkItemInput_withWorkitemID(String Workitem_ID)
	{
		return "<?xml version=\"1.0\"?>" + "<WMUnlockWorkItem_Input>"
				+ "<Option>WMUnlockWorkItem</Option>" 
				+ "<EngineName>"+sCabinetName+"</EngineName>"
				+ "<SessionId>"+sSessionID+"</SessionId>"	
				+ "<ProcessInstanceID>"+WINumber+"</ProcessInstanceID>"
				+ "<WorkItemID>"+Workitem_ID+"</WorkItemID>"
				+ "</WMUnlockWorkItem_Input>";
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
		
		/*try {
			return ngEjbClient.makeCall(sJtsIp, iJtsPort + "", "WebSphere", sInputXML);
		} catch (NGException e) {
			WriteLog("Exception: "+e.getMessage());
			throw new WICreateException("1006",pCodes.getString("1006")+" : "+e.getMessage());
		}*/
		
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
			
			InputMessage=(req.getInputMessage()==null)? "" : req.getInputMessage().trim();
			InputMessage=(InputMessage==null)? "" : InputMessage.replace("&amp;","&");
			InputMessage=(InputMessage==null)? "" : InputMessage.replace("&AMP;","&");
			
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
				String attrValue=attributeObj[i].getValue().trim();
				attrValue=(attrValue==null)? "" : attrValue.replace("&amp;","&");
				attrValue=(attrValue==null)? "" : attrValue.replace("&AMP;","&");
				hm.put(attributeObj[i].getName(), attrValue);
			}
						
			WriteLog("sProcessName: "+sProcessName+", sSubProcess: "+sSubProcess);
		}
		catch(Exception e)
		{
			WriteLog("Exception: "+e.getMessage());
			throw new WICreateException("1007",pCodes.getString("1007")+" : "+e.getMessage());
		}
	}
	/*private void checkAttributeTable(String attributeName, String attributeValue) throws WICreateException, Exception
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
		
	}*/
	
	private String getWorkitemID() throws WICreateException, Exception
	{
		//String WorkitemId="";
		//String strActivity_name="";
		try{
			
			String getWorkitemIDQry="select workitemid,ActivityName  from QUEUEVIEW with (nolock) where PROCESSINSTANCEID='"+WINumber+"' and ActivityName = '"+ActivityName+"'";
			sInputXML=getAPSelectWithColumnNamesXML(getWorkitemIDQry);
			WriteLog("Input XML: "+sInputXML);
			sOutputXML=executeAPI(sInputXML);
			WriteLog("Output XML: "+sOutputXML);
			xmlobj=new XMLParser(sOutputXML);
			checkCallsMainCode(xmlobj);
			workitemID=getTagValues(sOutputXML, "workitemid");
			ActivityName = getTagValues(sOutputXML, "ActivityName");
			WriteLog("WorkitemId: "+workitemID);
			WriteLog("ActivityName: "+ActivityName);
		}
		catch(Exception e){
			WriteLog("Exception occured while retriving workitem ID: "+e.getMessage());
			workitemID="1";
		}
		//from tables here
		
		return workitemID;
	}
	private void getTableName() throws WICreateException, Exception
	{
		//from tables here
		String getTableNameQry="select EXTERNALTABLE ,HISTORYTABLE,TRANSACTIONTABLE  from "+WSR_PROCESS+" where PROCESSID='"+processID+"'";
		sInputXML=getAPSelectWithColumnNamesXML(getTableNameQry);
		WriteLog("Input XML: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		externalTableName=getTagValues(sOutputXML, "EXTERNALTABLE");
		WriteLog("External table Name: "+externalTableName);
		HISTORYTABLE=getTagValues(sOutputXML, "HISTORYTABLE");
		WriteLog("History table Name: "+HISTORYTABLE);
		txnTableName=getTagValues(sOutputXML, "TRANSACTIONTABLE");
		WriteLog("Transaction table Name: "+txnTableName);
	}
	private void getWorkitemStage(String attributeList[]) throws WICreateException, Exception
	{
		//from tables here
		String attrName="";
	    String attrValue="";
	    String strOperationName="";
		
	    if(hm.containsKey("WorkitemNumber"))
	    	WINumber = hm.get("WorkitemNumber");
	    else if(hm.containsKey("WINUMBER"))
	    	WINumber = hm.get("WINUMBER");
	    
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
		String getTableNameQry="select QUEUENAME from "+WSR_POSSIBLEQUEUES+" with (nolock) where ProcessName='"+sProcessName+"' and SubProcessName = '"+sSubProcess+"' ";
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
	
	
	private String getNotifiedEventStatus() throws WICreateException,Exception
	{
		String getTableNameQry="select NotifyDEHAction from RB_DBO_EXTTABLE with(nolock) where WINAME='"+WINumber+"'";
		sInputXML=getAPSelectWithColumnNamesXML(getTableNameQry);
		WriteLog("Input XML to Get DBO NotifyDEHAction Status: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML to Get DBO NotifyDEHAction Status: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		String NotifyDEHAction =getTagValues(sOutputXML, "NotifyDEHAction");
		WriteLog("DBO NotifyDEHAction Status from External Table: "+NotifyDEHAction);
		return NotifyDEHAction;
	
	}
	private String validateAWBNumberOfRequest() throws WICreateException,Exception
	{
		String getAWBCountQry="select count(*) as Count from USR_0_DBO_AWB_Status with(nolock) where AWB_Number='"+hm.get("AWB_Number").trim()+"' and wi_name='"+WINumber+"'";
		sInputXML=getAPSelectWithColumnNamesXML(getAWBCountQry);
		WriteLog("Input XML to Get AWB Number Existence: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML to Get AWB Number Existence: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		String AWBCount =getTagValues(sOutputXML, "Count");
		WriteLog("DBO AWB Number Status from Awb Status Table: "+AWBCount);
		return AWBCount;
	
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
				
				if("DBO_INFO_UPDATE".equalsIgnoreCase(processID))
				{
					String notifyDEHAction = getNotifiedEventStatus().trim();
					if("INFO_CORECTION".equalsIgnoreCase(notifyDEHAction) ||
							"SOLELLC_TO_SOLE_WITH_INFO_CORECTION".equalsIgnoreCase(notifyDEHAction) || 
							"SOLE_TO_SOLELLC".equalsIgnoreCase(notifyDEHAction) || 
							"SOLE_TO_SOLELLC_INFO_CORECTION".equalsIgnoreCase(notifyDEHAction))
					{
						// Nothing to do - if this value is blank means workitem is waiting for 2nd call from YAP
					} 
					else 
					{
						WriteLog("DBO_INFO_UPDATE request is not applicable for other DEH Notified Event: "+notifyDEHAction);
						throw new WICreateException("12002",pCodes.getString("12002")+" - "+notifyDEHAction);
					}
				}
				else if("DBO_ADDNL_INFO_DOC".equalsIgnoreCase(processID))
				{
					String notifyDEHAction = getNotifiedEventStatus().trim();
					if("ADDNL_INFO_REQ_CUST".equalsIgnoreCase(notifyDEHAction))
					{
						// Nothing to do - if this value is blank means workitem is waiting for 2nd call from YAP
					}
					else
					{
						WriteLog("DBO_ADDNL_INFO_DOC request is not applicable for other DEH Notified Event: "+notifyDEHAction);
						throw new WICreateException("12002",pCodes.getString("12002")+" - "+notifyDEHAction);
					}
				}
				else if("DBO_FULFILLMENT".equalsIgnoreCase(processID))
				{
					String notifyDEHAction = getNotifiedEventStatus().trim();
					if("INIT_ACC_OPENING".equalsIgnoreCase(notifyDEHAction) || 
							"INFO_CORECTION".equalsIgnoreCase(notifyDEHAction) || 
							"SOLELLC_TO_SOLE_WITH_INFO_CORECTION".equalsIgnoreCase(notifyDEHAction) ||
							/*"SOLE_TO_SOLELLC".equalsIgnoreCase(notifyDEHAction) || 
							"SOLE_TO_SOLELLC_INFO_CORECTION".equalsIgnoreCase(notifyDEHAction) ||*/
							"SOLELLC_TO_SOLE".equalsIgnoreCase(notifyDEHAction))
					{
						// Nothing to do - if this value is blank means workitem is waiting for 2nd call from YAP
					} 
					else 
					{
						WriteLog("DBO_FULFILLMENT request is not applicable for DEH Notified Event: "+notifyDEHAction);
						throw new WICreateException("12002",pCodes.getString("12002")+" - "+notifyDEHAction);
					}
				}
				else if("DBO_FE_CORRECTION".equalsIgnoreCase(processID))
				{
					String notifyDEHAction = getNotifiedEventStatus().trim();
					if("FE_CORRECTION".equalsIgnoreCase(notifyDEHAction))
					{
						// Nothing to do - if this value is blank means workitem is waiting for 2nd call from YAP
					}
					else
					{
						WriteLog("DBO_FE_CORRECTION request is not applicable for other DEH Notified Event: "+notifyDEHAction);
						throw new WICreateException("12002",pCodes.getString("12002")+" - "+notifyDEHAction);
					}
				}
			} 
			else 
			{
				WriteLog("Update request is not possible at this queue: "+ActivityName);
				throw new WICreateException("5050",pCodes.getString("5050"));
			}
		//}
		
		
		//Check if all Mandatory attributes present in USR_0_WSR_UPDATE_ATTRDETAILS have come
		checkMandatoryAttribute();
		//Conditional Mandatory attributes check for external table fields added by om.tiwari
		if("DBO_FULFILLMENT".equalsIgnoreCase(processID))
		{
			if(("Y".equalsIgnoreCase(hm.get("IsChqBkReq"))||"Yes".equalsIgnoreCase(hm.get("IsChqBkReq"))) && (!hm.containsKey("ChqBkRefNo") || "".equalsIgnoreCase(hm.get("ChqBkRefNo"))))
			{
				throw new WICreateException("1026",pCodes.getString("1026")+": "+"ChqBkRefNo");
			} 
		}
		else if("DBO_COURIER_UPDATE".equalsIgnoreCase(processID))
		{
			String AWBCount = validateAWBNumberOfRequest().trim();
			if(!"0".equalsIgnoreCase(AWBCount))
			{
				// Nothing to do - if this value is blank means workitem is waiting for 2nd call from YAP
			}
			else
			{
				WriteLog("DBO_COURIER_UPDATE request is not applicable for AWB number: "+hm.get("AWB_Number"));
				throw new WICreateException("12008",pCodes.getString("12008")+" - "+hm.get("AWB_Number"));
			}
		}
		
		//Start - Checking conditional Mandatory when customer is authenticated added by Angad on 04122019
		/*if("RAOP_YAP".equalsIgnoreCase(processID))
		{
			checkConditionalMandatoryAttributeRAOP();
		}*/
		getTableName();
		
		// validate fields and prepare update column and value data
		checkExtTableAttrIBPS(hm);
		
		
		
		// validating date field format in received Request
		//validateInputValuesFunction(attributeList);
		
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
	
	private void checkExtTableAttrIBPS(HashMap<String, String> hmt) throws WICreateException, Exception
	{
		String txnTableAttributes="\n<Q_"+txnTableName+">";
		//WriteLog("inside checkAttributeTable");
		String attributeNames = "";
		for (String name: hmt.keySet()){
			if(attributeNames.equalsIgnoreCase(""))
				attributeNames = "'"+name.toString()+"'";
			else 
				attributeNames = attributeNames+",'"+name.toString()+"'";
		}
		String getExtTransQry= "";
		
		getExtTransQry="select ATTRIBUTENAME, isnull(nullif(EXTERNALTABLECOLNAME,''),'#') as EXTERNALTABLECOLNAME,isnull(nullif(TRANSACTIONTABLECOLNAME,''),'#') as TRANSACTIONTABLECOLNAME, isnull(nullif(ATTRIBUTE_FORMAT,''),'#') as ATTRIBUTE_FORMAT, isnull(nullif(ATTRIBUTE_TYPE,''),'#') as ATTRIBUTE_TYPE, ISMANDATORY from "+WSR_ATTRDETAILS+" with(nolock) where ATTRIBUTENAME in ("+attributeNames+") and PROCESSID='"+processID+"' and ISACTIVE='Y' ";
		
		sInputXML=getAPSelectWithColumnNamesXML(getExtTransQry);
		WriteLog("Input XML: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		xmlobj=new XMLParser(sOutputXML);
		WriteLog("Output XML: "+sOutputXML);
		checkCallsMainCode(xmlobj);
		String attNameFromTable=getTagValues(sOutputXML, "ATTRIBUTENAME");
		String extFlag=getTagValues(sOutputXML, "EXTERNALTABLECOLNAME");
		String isMandatory=getTagValues(sOutputXML, "ISMANDATORY");
		String txnColumnName=getTagValues(sOutputXML, "TRANSACTIONTABLECOLNAME");
		String attributesFormat=getTagValues(sOutputXML, "ATTRIBUTE_FORMAT");
		String attributesType=getTagValues(sOutputXML, "ATTRIBUTE_TYPE");
		//WriteLog("txnColumnName1086:-"+txnColumnName);
		//WriteLog("exttablecolumn1086:-"+extFlag);
		String AttrTagsName []=attNameFromTable.split("`");
		String ExtColsName []=extFlag.split("`");
		String MandateList []=isMandatory.split("`");
		String txnColsName []=txnColumnName.split("`");
		String attributeFormat []=attributesFormat.split("`");
		String attributeType []=attributesType.split("`");
		//WriteLog("AttrTagsName.length:-"+AttrTagsName.length);
		//WriteLog("MandateList.length:-"+MandateList.length);
		//WriteLog("ExtColsName.length:-"+ExtColsName.length);
		//WriteLog("txnColsName.length:-"+txnColsName.length);
		//WriteLog("attributeFormat.length:-"+attributeFormat.length);
		//WriteLog("attributeType.length:-"+attributeType.length);
		// validation to check received attributes tags in request are configured in table
		for (String name: hmt.keySet()){
			String attrtag = name.toString().trim();
			String flg = "N";
			//WriteLog("Attr:"+attNameFromTable);
			for(int i=0;i<AttrTagsName.length;i++)
			{	//WriteLog("Attr111:"+AttrTagsName[i]);
				if(AttrTagsName[i].trim().equalsIgnoreCase(attrtag))
				{	
					flg = "Y";
					break;
				}
			}
			if(flg.equalsIgnoreCase("N"))
			{
				WriteLog("No Value Mapped for attribute:-"+attrtag);
				throw new WICreateException("1017",pCodes.getString("1017")+" :"+attrtag);
			}
		}
		//*******************************************
		attributeTag=attributeTag+"\n<WIUpdateRecSubProc>"+sSubProcess+"</WIUpdateRecSubProc>";
		for(int i=0;i<AttrTagsName.length;i++)
		{
			//WriteLog("AttrTagsName[i]:"+AttrTagsName[i]);	
			//WriteLog("ExtColsName[i]:"+ExtColsName[i]);	
			//WriteLog("MandateList[i]:"+MandateList[i]);	
			String attributeValue = hmt.get(AttrTagsName[i]).trim();
			WriteLog("attribute name:-"+AttrTagsName[i]);
			WriteLog("attribute value before decode:-"+attributeValue);
			
			/*if(attributeValue == null) // means tag not available in received request
			{
				WriteLog("tag not available in received request");
				continue;
			}
			else
				attributeValue=URLEncoder.encode(attributeValue, "UTF-8");*/
			
			//WriteLog("attribute value after decode:-"+attributeValue);
			//block written to validate field level validation
			if(!"".equalsIgnoreCase(attributeValue) && !"".equalsIgnoreCase(attributeFormat[i]) && !"".equalsIgnoreCase(attributeType[i]) && !"#".equalsIgnoreCase(attributeFormat[i]) && !"#".equalsIgnoreCase(attributeType[i]))
			{
				checkAttributeFormatAndLength(AttrTagsName[i],attributeValue,attributeFormat[i],attributeType[i]);
			}
			//WriteLog(i+" attributeValue:-"+attributeValue+"\nattributeTag"+attributeTag);
			
			attributeValue=replaceXChars(attributeValue);//added to handle special characters in request
			
			if(((MandateList[i].trim().equalsIgnoreCase("Y") || MandateList[i].trim().equalsIgnoreCase("N")) 
					&& !attributeValue.equalsIgnoreCase("")) || (MandateList[i].trim().equalsIgnoreCase("N") && attributeValue.equalsIgnoreCase("")))
			{
				//WriteLog("extn table column name:-"+ExtColsName[i]);
				//WriteLog("txn table column name:-"+txnColsName[i]);
				
				if(!"DBO_COURIER_UPDATE".equalsIgnoreCase(processID))
				{
					if (!ExtColsName[i].trim().equalsIgnoreCase("") && !ExtColsName[i].trim().equalsIgnoreCase("#"))
					{
						WriteLog("this is a extn table attribute:-"+AttrTagsName[i]);
						attributeTag=attributeTag+"\n<"+ExtColsName[i]+">"+attributeValue+"</"+ExtColsName[i]+">";
						//WriteLog("attributeTag:-"+attributeTag);
						if("RiskScore".equalsIgnoreCase(AttrTagsName[i]) && !"".equalsIgnoreCase(attributeValue))
						{
							double riskScore = Double.parseDouble(attributeValue);
							String isHighRisk = "N";
							if(riskScore >= 4.05)
								isHighRisk = "Y";
							attributeTag=attributeTag+"\n<IsHighRisk>"+isHighRisk+"</IsHighRisk>";
						}
					}
					else if(!txnColsName[i].trim().equalsIgnoreCase("") && !txnColsName[i].trim().equalsIgnoreCase("#"))
					{
						WriteLog("this is a txn table attribute:-"+AttrTagsName[i]);
						txnTableAttributes=txnTableAttributes+"\n<"+txnColsName[i]+">"+attributeValue+"</"+txnColsName[i]+">";
						//WriteLog("txnTableAttributes:-"+txnTableAttributes);
					}
					else
					{
						WriteLog("some error occured for attribute:-"+AttrTagsName[i]);
						throw new WICreateException("1015",pCodes.getString("1015")+" :"+AttrTagsName[i]);
					}
				}
				else if("DBO_COURIER_UPDATE".equalsIgnoreCase(processID))
				{
					if(!"WI_NAME".equalsIgnoreCase(txnColsName[i].trim()) && !"AWB_Number".equalsIgnoreCase(txnColsName[i].trim()))
					{
						if("".equalsIgnoreCase(awbColNames))
							awbColNames=txnColsName[i].trim();
						else
							awbColNames=awbColNames+","+txnColsName[i].trim();
						
						if("".equalsIgnoreCase(awbColValues))
							awbColValues="'"+attributeValue.trim()+"'";
						else
							awbColValues=awbColValues+","+"'"+attributeValue.trim()+"'";
						
					}
					if("AWB_Number".equalsIgnoreCase(txnColsName[i].trim()))
					{
						awbNumber = attributeValue.trim();
					}
					if("Delivery_Status_Code".equalsIgnoreCase(txnColsName[i].trim()))
					{
						awbDeliveryStatusCode = attributeValue.trim();
					}
					if("Delivery_Status".equalsIgnoreCase(txnColsName[i].trim()))
					{
						awbDeliveryStatusDesc = attributeValue.trim();
					}
					
				}
					 
			}
			else if(MandateList[i].trim().equalsIgnoreCase("Y") && attributeValue.equalsIgnoreCase(""))
			{
				WriteLog("some error occured for attribute2:-"+AttrTagsName[i]);
				 throw new WICreateException("1016",pCodes.getString("1016")+" :"+AttrTagsName[i]);
			}
		}
		
		//WriteLog("Final txn table attributeTag:-"+txnTableAttributes);
		if(!("\n<Q_"+txnTableName+">").equalsIgnoreCase(txnTableAttributes))
			txnTableAttributes=txnTableAttributes+"\n</Q_"+txnTableName+">";
		else
			txnTableAttributes = "";
		
		attributeTag=attributeTag+txnTableAttributes;
		WriteLog("Final attributeTag:-"+attributeTag);
		
				
	}
	
	
	private void validateRepetitiveRequestParameters() throws WICreateException, Exception
	{
		String repetitiveListMain[];
		String repetitiveList[];
		
		RepetitiveMainTags = checkMandatoryRepetitiveTags();
		if(!"".equalsIgnoreCase(RepetitiveMainTags))
		{
			String attributeList []=RepetitiveMainTags.split("`");
			if(attributeList.length>0)
			{
				for(int i=0;i<attributeList.length;i++)
				{
					if(InputMessage.contains(attributeList[i])) 
					{
						repetitiveListMain=getTagValues(InputMessage, attributeList[i]).split("`");
			        	for(int j=0;j<repetitiveListMain.length;j++)
			        	{	
			        		HashMap<String, String> hm1 = new HashMap(); 
			            	repetitiveList=getTagValues(repetitiveListMain[j], "Attribute").split("`");
			        		for(int k=0;k<repetitiveList.length;k++)
			            	{
			        			String attrName=getTagValues(repetitiveList[k],"Name");
				        		String attrValue=getTagValues(repetitiveList[k],"Value");
				    			
				        		hm1.put(attrName, attrValue);
			        		}
			        		if(hm1.size() != 0)
			        		{
			        			String RepetitiveProcessID = hmRptProcessId.get(attributeList[i]);
			        			checkMandatoryRepetitiveAttribute(RepetitiveProcessID,"Y",hm1);
				        		hmMain.put(attributeList[i]+"-"+Integer.toString(j), hm1);
				        	}
			        	}
					}
				}
			}
		}
			
	}
	
	private String checkMandatoryRepetitiveTags() throws WICreateException, Exception
	{
		WriteLog("inside checkMandatoryRepetitiveTags");
		String sInputXML=getAPSelectWithColumnNamesXML("select REPETITIVETAGNAME,PROCESSID,TRANSACTIONTABLE,ISMANDATORY from USR_0_WSR_UPDATE_PROCESS_REPETITIVE with(nolock) where ProcessName='"+sProcessName+"' and SUBPROCESSNAME='"+sSubProcess+"' and ISACTIVE='Y'");
		WriteLog("Input XML: "+sInputXML);
		String sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		String RepetitiveMainTags = getTagValues(sOutputXML,"REPETITIVETAGNAME");
		String RepProcessId[] = getTagValues(sOutputXML,"PROCESSID").split("`");
		String RepTransTable[] = getTagValues(sOutputXML,"TRANSACTIONTABLE").split("`");
		String isMandatory = getTagValues(sOutputXML,"ISMANDATORY");
		String RepetitiveList []=RepetitiveMainTags.split("`");
		String MandateList []=isMandatory.split("`");
		if(MandateList.length>0)
		{
			for(int i=0;i<MandateList.length;i++)
			{
				String flag="Y";
				
				if("Y".equalsIgnoreCase(MandateList[i]))
				{
				    if(InputMessage.contains(RepetitiveList[i]))
				    {
				    	// nothing to do
				    }	
				    else 
				    	flag = "N";
				}
				else //Conditional Validations for repetitive tags in DBO WI update added by om.tiwari
				{
					if("FIRCOHITDetails".equalsIgnoreCase(RepetitiveList[i]))
					{
						String fircoHitAggregates = getTagValues(InputMessage, "FIRCOHITDetails");
						
						String fircoAggregates[] = fircoHitAggregates.split("`");
						boolean emptyAggregateFlag=false;
						for(int k=0;(k<fircoAggregates.length && !"".equalsIgnoreCase(fircoAggregates[k].trim()));k++)
						{
							String allFircoAttributes= getTagValues(fircoAggregates[k], "Attribute");
							String fircoAttributes[] = allFircoAttributes.split("`");
							if(fircoAttributes.length>0 && !"".equalsIgnoreCase(allFircoAttributes.trim()))
							{
								emptyAggregateFlag=true;
								break;
							}
						}
						if("INFO_UPDATE".equalsIgnoreCase(sSubProcess) && hm.containsKey("IsFIRCOHit") && "Y".equalsIgnoreCase(hm.get("IsFIRCOHit")))
						{
							if("".equalsIgnoreCase(fircoHitAggregates))
							{
									throw new WICreateException("12006",pCodes.getString("12006")+": "+RepetitiveList[i]);
							}
							else if(fircoAggregates.length>0)
							{
								if(!emptyAggregateFlag)
									throw new WICreateException("12006",pCodes.getString("12006")+": "+RepetitiveList[i]);
							}
						}
						else if("INFO_UPDATE".equalsIgnoreCase(sSubProcess) && hm.containsKey("IsFIRCOHit") && "N".equalsIgnoreCase(hm.get("IsFIRCOHit")))
						{
							if(emptyAggregateFlag)
							{
								throw new WICreateException("12007",pCodes.getString("12007")+": "+RepetitiveList[i]);
							}
						}
					}
					
				}
			    if(flag.equalsIgnoreCase("N"))
					throw new WICreateException("1020",pCodes.getString("1020")+": "+RepetitiveList[i]);
			
			    hmRptProcessId.put(RepetitiveList[i], RepProcessId[i]);
			    hmRptTransTable.put(RepProcessId[i], RepTransTable[i]);
			}
		}
		return RepetitiveMainTags;
	}
	
	private String checkMandatoryRepetitiveAttribute(String RepetitiveProcessId,String isValidatingMandate, HashMap<String, String> RepetitiveReqAttr) throws WICreateException, Exception
	{
		WriteLog("inside checkMandatoryRepetitiveAttributeIBPS");
		String AttrTagName = "";
		String TransColName = "";
		String AttrFormat = "";
		String AttrType = "";
		String isMandatory = "";
		if(!hmRptAttrAndCol.containsKey(RepetitiveProcessId))
		{
			String sInputXML=getAPSelectWithColumnNamesXML("select ATTRIBUTENAME,TRANSACTIONTABLECOLNAME,isnull(nullif(ATTRIBUTE_FORMAT,''),'#') as ATTRIBUTE_FORMAT, isnull(nullif(ATTRIBUTE_TYPE,''),'#') as ATTRIBUTE_TYPE,ISMANDATORY from USR_0_WSR_UPDATE_ATTRDETAILS_REPETITIVE with(nolock) where PROCESSID='"+RepetitiveProcessId+"' and ISACTIVE='Y' order by AttributeName");
			WriteLog("Input XML: "+sInputXML);
			String sOutputXML=executeAPI(sInputXML);
			WriteLog("Output XML: "+sOutputXML);
			xmlobj=new XMLParser(sOutputXML);
			checkCallsMainCode(xmlobj);
			AttrTagName = getTagValues(sOutputXML,"ATTRIBUTENAME");
			TransColName = getTagValues(sOutputXML,"TRANSACTIONTABLECOLNAME");
			AttrFormat = getTagValues(sOutputXML,"ATTRIBUTE_FORMAT");
			AttrType = getTagValues(sOutputXML,"ATTRIBUTE_TYPE");
			isMandatory = getTagValues(sOutputXML,"ISMANDATORY");
		} 
		else
		{
			HashMap<String, String> hmAttr = new HashMap();
			hmAttr = hmRptAttrAndCol.get(RepetitiveProcessId);
			for (String name: hmAttr.keySet()){
				if(AttrTagName.equalsIgnoreCase(""))
					AttrTagName = name.toString();
				else 
					AttrTagName = AttrTagName+"`"+name.toString();
				
				if(TransColName.equalsIgnoreCase(""))
					TransColName = hmAttr.get(name).toString(); 
				else
					TransColName = TransColName+"`"+hmAttr.get(name).toString();
			}
			
			HashMap<String, String> hmMand = new HashMap();
			hmMand = hmRptAttrAndMand.get(RepetitiveProcessId);
			for (String name1: hmMand.keySet()){
				if(isMandatory.equalsIgnoreCase(""))
					isMandatory = hmMand.get(name1).toString(); 
				else
					isMandatory = isMandatory+"`"+hmMand.get(name1).toString();
			}
			
			HashMap<String, String> hmFormat = new HashMap();
			hmFormat = hmRptAttrAndFormat.get(RepetitiveProcessId);
			for (String name1: hmFormat.keySet()){
				if(AttrFormat.equalsIgnoreCase(""))
					AttrFormat = hmFormat.get(name1).toString(); 
				else
					AttrFormat = AttrFormat+"`"+hmFormat.get(name1).toString();
			}
			
			HashMap<String, String> hmType = new HashMap();
			hmType = hmRptAttrAndType.get(RepetitiveProcessId);
			for (String name1: hmType.keySet()){
				if(AttrType.equalsIgnoreCase(""))
					AttrType = hmType.get(name1).toString(); 
				else
					AttrType = AttrType+"`"+hmType.get(name1).toString();
			}
			
		}
		
		if(isValidatingMandate.equalsIgnoreCase("Y"))
		{	
			String AttrTagsName []=AttrTagName.split("`");
			String TransColsName []=TransColName.split("`");
			String MandateList []=isMandatory.split("`");
			String AttrFormatList []=AttrFormat.split("`");
			String AttrTypeList []=AttrType.split("`");
			if(MandateList.length>0)
			{
				for(int i=0;i<MandateList.length;i++)
				{
					String flag="Y";
					
					if("Y".equalsIgnoreCase(MandateList[i]))
					{
						if(RepetitiveReqAttr.containsKey(AttrTagsName[i]))
						{	
						    if(RepetitiveReqAttr.get(AttrTagsName[i]).trim().equalsIgnoreCase(""))
						    	flag = "N";
						    else {
						    	// nothing to do
						    }
						}
						else
							flag = "N";	
					}
					else // to validate conditional mandatory attributes for repetitive tags , added by om.tiwari // need to review this block
					{
						if("DebitCardRefNo".equalsIgnoreCase(AttrTagsName[i]))
						{
							String isDebitCardReq=RepetitiveReqAttr.get("IsDebitCardRequired").trim();
							if(("FULFILLMENT".equalsIgnoreCase(sSubProcess) || "FE_CORRECTION".equalsIgnoreCase(sSubProcess)) 
									&& ("Y".equalsIgnoreCase(isDebitCardReq) ||"Yes".equalsIgnoreCase(isDebitCardReq)) 
									&& (!RepetitiveReqAttr.containsKey("DebitCardRefNo") || "".equalsIgnoreCase(RepetitiveReqAttr.get("DebitCardRefNo"))))
							{
								throw new WICreateException("1026",pCodes.getString("1026")+": "+"DebitCardRefNo for Related Party:"+RepetitiveReqAttr.get("RelatedPartyID"));
							}
						}
					}
				    if(flag.equalsIgnoreCase("N"))
						throw new WICreateException("1020",pCodes.getString("1020")+": "+AttrTagsName[i]+" for "+ RepetitiveProcessId);
				    
				   
				    //block written to validate field level validation
				    //try{
				    if(RepetitiveReqAttr.containsKey(AttrTagsName[i].trim()))
				    {	
					    String attrVal = RepetitiveReqAttr.get(AttrTagsName[i]).trim();
						if(!"".equalsIgnoreCase(attrVal) && !"".equalsIgnoreCase(AttrFormatList[i]) && !"".equalsIgnoreCase(AttrTypeList[i]) && !"#".equalsIgnoreCase(AttrFormatList[i]) && !"#".equalsIgnoreCase(AttrTypeList[i]))
						{
							checkAttributeFormatAndLength(AttrTagsName[i],attrVal,AttrFormatList[i],AttrTypeList[i]);
						}
				    }	
				   /* }
				    catch(Exception e){
				    	WriteLog("tag isnt available while validating format and type for repetitve tags"+e.getMessage());
				    }*/
				    //**************************************************
				}
			}
			else
			{
				throw new WICreateException("1021",pCodes.getString("1021")+" for process id: "+processID);
			}
			
			if(!hmRptAttrAndCol.containsKey(RepetitiveProcessId))
			{
				if(AttrTagsName.length>0)
				{
					HashMap<String, String> hmtmp = new HashMap();
					for(int i=0;i<AttrTagsName.length;i++)
					{
						hmtmp.put(AttrTagsName[i], TransColsName[i]);
					}
					hmRptAttrAndCol.put(RepetitiveProcessId, hmtmp);
					
					HashMap<String, String> hmtmp1 = new HashMap();
					for(int i=0;i<AttrTagsName.length;i++)
					{
						hmtmp1.put(AttrTagsName[i], MandateList[i]);
					}
					hmRptAttrAndMand.put(RepetitiveProcessId, hmtmp1);
					
					HashMap<String, String> hmtmp2 = new HashMap();
					for(int i=0;i<AttrTagsName.length;i++)
					{
						hmtmp2.put(AttrTagsName[i], AttrFormatList[i]);
					}
					hmRptAttrAndFormat.put(RepetitiveProcessId, hmtmp2);
					
					HashMap<String, String> hmtmp3 = new HashMap();
					for(int i=0;i<AttrTagsName.length;i++)
					{
						hmtmp3.put(AttrTagsName[i], AttrTypeList[i]);
					}
					hmRptAttrAndType.put(RepetitiveProcessId, hmtmp3);
				}	
			}
						
			// Start - Validating conditional mandatory parameters added on 11/07/2021 by Angad
			/*if("DBOWBA_CondHistRelPartDtls".equalsIgnoreCase(RepetitiveProcessId.trim()))
			{
				String RelationshiType = "";
				if(RepetitiveReqAttr.containsKey("RELATIONSHIPTYPE"))
				{	
					RelationshiType = RepetitiveReqAttr.get("RELATIONSHIPTYPE").trim();
				}
				
				if("Guarantor".equalsIgnoreCase(RelationshiType))
				{
					if(RepetitiveReqAttr.containsKey("GUARANTORYCATEGORY"))
					{	
					    if("".equalsIgnoreCase(RepetitiveReqAttr.get("GUARANTORYCATEGORY").trim()))
					    	throw new WICreateException("1020",pCodes.getString("1020")+": "+"GUARANTORYCATEGORY"+" for "+ RepetitiveProcessId);
					    else {
					    	// nothing to do
					    }
					}
					else
						throw new WICreateException("1020",pCodes.getString("1020")+": "+"GUARANTORYCATEGORY"+" for "+ RepetitiveProcessId);
				}
				
				String CompanyFlag = "";
				if(RepetitiveReqAttr.containsKey("COMPANYFLAG"))
				{	
				    CompanyFlag = RepetitiveReqAttr.get("COMPANYFLAG").trim();
				}
				
				// Start - conditional mandatory on the basis of Company Flag
				if("Y".equalsIgnoreCase(CompanyFlag))
				{
					String [] MandAttr = {"COMPANYCATEGORY","YEAROFINCORPORATION","ISTLVALID","COUNTRY","EMIRATE","ISSISCOCOBORROWERGUARANTOR","NAMEOFSISTERCOMPANYOWNER","COBORROWERGUARANTORSTATUS","SHARESBANKACCOUNT"};
					
					for(int i=0; i < MandAttr.length; i++)
					{
						if(RepetitiveReqAttr.containsKey(MandAttr[i]))
						{	
						    if("".equalsIgnoreCase(RepetitiveReqAttr.get(MandAttr[i]).trim()))
						    	throw new WICreateException("1020",pCodes.getString("1020")+": "+MandAttr[i]+" for "+ RepetitiveProcessId);
						    else {
						    	// nothing to do
						    }
						}
						else
							throw new WICreateException("1020",pCodes.getString("1020")+": "+MandAttr[i]+" for "+ RepetitiveProcessId);
					}
				}
				
				if("N".equalsIgnoreCase(CompanyFlag))
				{
					String [] MandAttr = {"FIRSTNAME","LASTNAME","DATEOFBIRTH","EMIRATESID","ISGOVERNMENTRELATION","SIGNATORYFLAG","TYPEOFOWNERSHIP","TYPEOFPROOFPROVIDEDFORLOB","INVOLVEDINBUSINESS","SHAREHOLDINGPERCENTAGE","AUTHORITYTYPE","NATIONALITY","VISASPONSOR","PASSPORTNOVISASPONSOR"};
					
					for(int i=0; i < MandAttr.length; i++)
					{
						if(RepetitiveReqAttr.containsKey(MandAttr[i]))
						{	
						    if("".equalsIgnoreCase(RepetitiveReqAttr.get(MandAttr[i]).trim()))
						    	throw new WICreateException("1020",pCodes.getString("1020")+": "+MandAttr[i]+" for "+ RepetitiveProcessId);
						    else 
						    {
						    	if("SIGNATORYFLAG".equalsIgnoreCase(MandAttr[i]))
						    	{
						    		String SignFlag = RepetitiveReqAttr.get(MandAttr[i]).trim();
						    		if("Y".equalsIgnoreCase(SignFlag))
						    		{
						    			if(RepetitiveReqAttr.containsKey("SIGNATORYPOWERHELDSINCEDATE"))
										{	
										    if("".equalsIgnoreCase(RepetitiveReqAttr.get("SIGNATORYPOWERHELDSINCEDATE").trim()))
										    	throw new WICreateException("1020",pCodes.getString("1020")+": "+"SIGNATORYPOWERHELDSINCEDATE"+" for "+ RepetitiveProcessId);
										    else {
										    	// nothing to do
										    }
										}
										else
											throw new WICreateException("1020",pCodes.getString("1020")+": "+"SIGNATORYPOWERHELDSINCEDATE"+" for "+ RepetitiveProcessId);
						    		}
						    	}
						    }
						}
						else
							throw new WICreateException("1020",pCodes.getString("1020")+": "+MandAttr[i]+" for "+ RepetitiveProcessId);
					}
				}
				// End - conditional mandatory on the basis of Company Flag
				
				String GuarantorCategory = "";
				if(RepetitiveReqAttr.containsKey("GUARANTORYCATEGORY"))
				{	
					GuarantorCategory = RepetitiveReqAttr.get("GUARANTORYCATEGORY").trim();
				}
				
				// Start - conditional mandatory on the basis of Guarantor Category
				if("Company".equalsIgnoreCase(GuarantorCategory))
				{
					String [] MandAttr = {"DATEOFINCORPORATION","LINEOFBUSINESS","ISSUINGEMIRATE"};
					
					for(int i=0; i < MandAttr.length; i++)
					{
						if(RepetitiveReqAttr.containsKey(MandAttr[i]))
						{	
						    if("".equalsIgnoreCase(RepetitiveReqAttr.get(MandAttr[i]).trim()))
						    	throw new WICreateException("1020",pCodes.getString("1020")+": "+MandAttr[i]+" for "+ RepetitiveProcessId);
						    else {
						    	// nothing to do
						    }
						}
						else
							throw new WICreateException("1020",pCodes.getString("1020")+": "+MandAttr[i]+" for "+ RepetitiveProcessId);
					}
				}
				
				if("Individual".equalsIgnoreCase(GuarantorCategory))
				{
					String [] MandAttr = {"GENDER","COUNTRYOFRESIDENCE","PASSPORTNUMBER","PASSPORTEXPIRYDATE","PASSPORTISSUINGCOUNTRY","PREVIOUSPASSPORTNUMBER","VISANUMBER","COUNTRYOFBIRTH","BACKGROUND","FATHERNAME","MOTHERNAME","SPOUSENAME"};
					
					for(int i=0; i < MandAttr.length; i++)
					{
						if(RepetitiveReqAttr.containsKey(MandAttr[i]))
						{	
						    if("".equalsIgnoreCase(RepetitiveReqAttr.get(MandAttr[i]).trim()))
						    	throw new WICreateException("1020",pCodes.getString("1020")+": "+MandAttr[i]+" for "+ RepetitiveProcessId);
						    else {
						    	// nothing to do
						    }
						}
						else
							throw new WICreateException("1020",pCodes.getString("1020")+": "+MandAttr[i]+" for "+ RepetitiveProcessId);
					}
				}
				// End - conditional mandatory on the basis of Guarantor Category
			}*/
			// End - Validating conditional mandatory parameters added on 11/07/2021 by Angad
			
		}
		return AttrTagName;
	}
	
	private void checkAttributeFormatAndLength(String attributeName, String attributeValue, String attrFormat, String attType) throws WICreateException, Exception
	{
		//WriteLog("inside checkAttributeFormatAndLength attributeName-"+attributeName+", attType-"+attType+", attrFormat-"+attrFormat+", attributeValue-"+attributeValue);
		
		if(!(attributeValue.equalsIgnoreCase("")))
		{
			int attributelength = attributeValue.length();
			if(!attType.equalsIgnoreCase(""))
			{
				if(attType.equalsIgnoreCase("DATE"))
				{
					Date date = null;
					SimpleDateFormat sdf = new SimpleDateFormat(attrFormat);
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
					//WriteLog("11 inside checkAttributeFormatAndLength attributeName-"+attributeName+", attType-"+attType+", attrFormat-"+attrFormat+", attributeValue-"+attributeValue);	
					int attributesize = Integer.parseInt(attrFormat);
					if(attributelength>attributesize)
					{
						throw new WICreateException("1111",pCodes.getString("1111")+" :"+attributeName);
					}
					//WriteLog("22 inside checkAttributeFormatAndLength attributeName-"+attributeName+", attType-"+attType+", attrFormat-"+attrFormat+", attributeValue-"+attributeValue);
					String patternMatch = "";
										
					if(attType.equalsIgnoreCase("NUMERIC"))
					{	
						patternMatch="[0-9]+";
						if(!Pattern.matches(patternMatch, attributeValue))
						{
							//WriteLog("inside 995");
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
						patternMatch="^[a-zA-Z0-9-#_!.@()+/%&\\s|~\\[\\]$^*={};:\",<>?\\n\\r\\t\\\\ ]*$";
						if(!Pattern.matches(patternMatch, attributeValue))
						{
							throw new WICreateException("1118",pCodes.getString("1118")+" :"+attributeName);
							
						}
					}
				}
			}
		}
		
	}
	
	
	
	/*private void InsertRecordsForRepetitiveTagsIBPS() throws WICreateException, Exception
	{
		if(hmMain.size()!= 0)
		{
			WriteLog("Inside InsertRecordsForRepetitiveTagsIBPS");
			for (String name: hmMain.keySet()){
	            String key = name.toString();
	            String keyvalue = hmMain.get(name).toString();  
	            //WriteLog("hmMain: "+key + " " + keyvalue);  
	            String RepetitiveTags [] = key.split("-");
	            String RepetitiveProcessID = hmRptProcessId.get(RepetitiveTags[0]);
				String RepetitiveTagTableName=hmRptTransTable.get(RepetitiveProcessID);
				//WriteLog("RepetitiveProcessID:"+RepetitiveProcessID);
				//WriteLog("RepetitiveTagTableName:"+RepetitiveTagTableName);
				HashMap<String, String> hm1 = new HashMap(); 
				hm1 = hmMain.get(key);
				//WriteLog(key+" hm1 size: "+hm1.size());
				
				HashMap<String, String> hmtmp = new HashMap();
				hmtmp = hmRptAttrAndCol.get(RepetitiveProcessID);
				String AttrList = "";
				String TransColList = "";
				//WriteLog("hmRptAttrAndCol size:"+hmRptAttrAndCol.size());
				//WriteLog("hmtmp size:"+hmtmp.size());
				for (String name1: hmtmp.keySet()){
					if(AttrList.equalsIgnoreCase(""))
						AttrList = name1.toString();
					else 
						AttrList = AttrList+"`"+name1.toString();
					
					if(TransColList.equalsIgnoreCase(""))
						TransColList = hmtmp.get(name1).toString(); 
					else
						TransColList = TransColList+"`"+hmtmp.get(name1).toString();
				}
				//WriteLog("AttrList:"+AttrList);
				//WriteLog("TransColList:"+TransColList);
				if(!"".equalsIgnoreCase(AttrList.trim()))
				{
					String AttrListArr [] = AttrList.split("`");
					String TransColListArr [] = TransColList.split("`");
					String insertColumns = "";
					String insertValues = "";
					for(int j=0;j<AttrListArr.length;j++)
					{
						if(hm1.containsKey(AttrListArr[j]))
						{
							String value = hm1.get(AttrListArr[j]);
							if(!value.trim().equalsIgnoreCase("")) 
							{
								value = value.replace("'", "");
							}
							if(!value.trim().equalsIgnoreCase("")) 
							{
								if("".equalsIgnoreCase(insertColumns))
									insertColumns = TransColListArr[j];
								else 
									insertColumns = insertColumns+","+TransColListArr[j];
								
								if("".equalsIgnoreCase(insertValues))
									insertValues = "'"+value+"'";
								else 
									insertValues = insertValues+","+"'"+value+"'";
							}
						}
					}
					insertIntoRepetitiveGridTable(RepetitiveTagTableName, insertColumns, insertValues);
				}
				
			}	
				
		}
	}*/
	
	private String createRepetativeAttributeXML() throws WICreateException, Exception{
		if(hmMain.size()!= 0)
		{
			Date d= new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sDate = dateFormat.format(d);
			WriteLog("Inside createRepetativeAttributeXML");
			String RepetitiveTagsAttribute="";
			String fircoRelatedParties = "";
			int fircoRelatedPartiesCount = 0;
			for (String name: hmMain.keySet()){
	            String key = name.toString();
	            String keyvalue = hmMain.get(name).toString();  
	            //WriteLog("hmMain: "+key + " " + keyvalue);  
	            String RepetitiveTags [] = key.split("-");
	            String RepetitiveProcessID = hmRptProcessId.get(RepetitiveTags[0]);
				String RepetitiveTagTableName=hmRptTransTable.get(RepetitiveProcessID);
				if("".equalsIgnoreCase(RepetitiveTagsAttribute))
				{
					RepetitiveTagsAttribute="\n<Q_"+RepetitiveTagTableName+">";
				}
				else
				{
					RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<Q_"+RepetitiveTagTableName+">";
				}
				

				//WriteLog("RepetitiveProcessID:"+RepetitiveProcessID);
				//WriteLog("RepetitiveTagTableName:"+RepetitiveTagTableName);
				HashMap<String, String> hm1 = new HashMap(); 
				hm1 = hmMain.get(key);
				//WriteLog(key+" hm1 size: "+hm1.size());
				
				HashMap<String, String> hmtmp = new HashMap();
				hmtmp = hmRptAttrAndCol.get(RepetitiveProcessID);
				String AttrList = "";
				String TransColList = "";
				//WriteLog("hmRptAttrAndCol size:"+hmRptAttrAndCol.size());
				//WriteLog("hmtmp size:"+hmtmp.size());
				for (String name1: hmtmp.keySet()){
					if(AttrList.equalsIgnoreCase(""))
						AttrList = name1.toString();
					else 
						AttrList = AttrList+"`"+name1.toString();
					
					if(TransColList.equalsIgnoreCase(""))
						TransColList = hmtmp.get(name1).toString(); 
					else
						TransColList = TransColList+"`"+hmtmp.get(name1).toString();
				}
				//WriteLog("AttrList:"+AttrList);
				//WriteLog("TransColList:"+TransColList);
				if(!"".equalsIgnoreCase(AttrList.trim()))
				{
					String AttrListArr [] = AttrList.split("`");
					String TransColListArr [] = TransColList.split("`");
					String insertColumns = "";
					String insertValues = "";
					for(int j=0;j<AttrListArr.length;j++)
					{
						if(hm1.containsKey(AttrListArr[j]))
						{
							String value = hm1.get(AttrListArr[j]);
							if(!value.trim().equalsIgnoreCase("")) 
							{
								value = value.replace("'", "");
								value=replaceXChars(value);//added to handle special characters in request
							}
							if(!value.trim().equalsIgnoreCase("")) 
							{
								if(!"DBOCOURIERUPDATE_EmiratesIDPingData".equalsIgnoreCase(RepetitiveProcessID))
									RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<"+TransColListArr[j]+">"+value+"</"+TransColListArr[j]+">";
								else if("DBOCOURIERUPDATE_EmiratesIDPingData".equalsIgnoreCase(RepetitiveProcessID))
								{
									if("".equalsIgnoreCase(pingDataColNames))
										pingDataColNames=TransColListArr[j].trim();
									else
										pingDataColNames=pingDataColNames+","+TransColListArr[j].trim();
									
									if("".equalsIgnoreCase(pingDataColValues))
										pingDataColValues="'"+value.trim()+"'";
									else
										pingDataColValues=pingDataColValues+","+"'"+value.trim()+"'";
								}
								/*if("".equalsIgnoreCase(insertColumns))
									insertColumns = TransColListArr[j];
								else 
									insertColumns = insertColumns+","+TransColListArr[j];
								
								if("".equalsIgnoreCase(insertValues))
									insertValues = "'"+value+"'";
								else 
									insertValues = insertValues+","+"'"+value+"'";*/
								
								/*if("FircoAlertDetails".equalsIgnoreCase(AttrListArr[j]))
								{
									completeFircoData=completeFircoData+parseFircoDetails(value, hm1.get("RelatedPartyID").trim());
								}*/
								
								if("DBO".equalsIgnoreCase(sProcessName))
								{
									if("DBOFULFILLMENT_RelatedPartyDetails".equalsIgnoreCase(RepetitiveProcessID))
									{
										if("RelatedPartyID".equalsIgnoreCase(TransColListArr[j]))
										{
											sInputXML=getAPSelectWithColumnNamesXML("select top 1 InsertionOrderId from "+RepetitiveTagTableName+" with(nolock) where WINAME='"+WINumber+"' and  RelatedPartyID='"+value+"' ");
											WriteLog("Input XML: "+sInputXML);
											sOutputXML=executeAPI(sInputXML);
											WriteLog("Output XML: "+sOutputXML);
											xmlobj=new XMLParser(sOutputXML);
											checkCallsMainCode(xmlobj);
											String insertionOrderId=getTagValues(sOutputXML,"InsertionOrderId");
											if("".equalsIgnoreCase(insertionOrderId))
											{
												WriteLog("RelatedPartyID doesnot exists: "+value);
												throw new WICreateException("12003",pCodes.getString("12003")+" - "+value);
											}else
												RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<insertionOrderId>"+insertionOrderId+"</insertionOrderId>";
										}
										/*else if("FullName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_FullName>"+value+"</copyof_FullName>";
										else if("FirstName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_FirstName>"+value+"</copyof_FirstName>";
										else if("MiddleName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_MiddleName>"+value+"</copyof_MiddleName>";
										else if("LastName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_LastName>"+value+"</copyof_LastName>";
										else if("NameOnDebitCard".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_NameOnDebitCard>"+value+"</copyof_NameOnDebitCard>";*/
									}
									else if("DBOADDNLINFODOC_QueryResponse".equalsIgnoreCase(RepetitiveProcessID))
									{
										if("Query_Unique_ID".equalsIgnoreCase(TransColListArr[j]))
										{
											sInputXML=getAPSelectWithColumnNamesXML("select top 1 InsertionOrderId from "+RepetitiveTagTableName+" with(nolock) where WI_NAME='"+WINumber+"' and   Query_Unique_ID='"+value+"' ");
											WriteLog("Input XML: "+sInputXML);
											sOutputXML=executeAPI(sInputXML);
											WriteLog("Output XML: "+sOutputXML);
											xmlobj=new XMLParser(sOutputXML);
											checkCallsMainCode(xmlobj);
											String insertionOrderId=getTagValues(sOutputXML,"InsertionOrderId");
											if("".equalsIgnoreCase(insertionOrderId))
											{
												WriteLog("Query_Unique_ID doesnot exists: "+value);
												throw new WICreateException("12004",pCodes.getString("12004")+" - "+value);
											}
											else
											{
												RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<insertionOrderId>"+insertionOrderId+"</insertionOrderId>";
												RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<Response_Date_Time>"+sDate+"</Response_Date_Time>";
											}
												
										}
									}
									else if("DBOADDNLINFODOC_DocResponse".equalsIgnoreCase(RepetitiveProcessID))
									{
										if("DocUniqueID".equalsIgnoreCase(TransColListArr[j]))
										{
											sInputXML=getAPSelectWithColumnNamesXML("select top 1 InsertionOrderId from "+RepetitiveTagTableName+" with(nolock) where WINAME='"+WINumber+"' and  DocUniqueID='"+value+"' ");
											WriteLog("Input XML: "+sInputXML);
											sOutputXML=executeAPI(sInputXML);
											WriteLog("Output XML: "+sOutputXML);
											xmlobj=new XMLParser(sOutputXML);
											checkCallsMainCode(xmlobj);
											String insertionOrderId=getTagValues(sOutputXML,"InsertionOrderId");
											if("".equalsIgnoreCase(insertionOrderId))
											{
												WriteLog("DocUniqueID doesnot exists: "+value);
												throw new WICreateException("12005",pCodes.getString("12005")+" - "+value);
											}
											else
											{
												RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<insertionOrderId>"+insertionOrderId+"</insertionOrderId>";
												RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<CustRespDateTime>"+sDate+"</CustRespDateTime>";
												RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<IsCustomerResponsed>Y</IsCustomerResponsed>";
											}
												
										}
									}
									else if("DBOFECORR_RelatedPartyDetails".equalsIgnoreCase(RepetitiveProcessID))
									{
										if("RelatedPartyID".equalsIgnoreCase(TransColListArr[j]))
										{
											sInputXML=getAPSelectWithColumnNamesXML("select top 1 InsertionOrderId from "+RepetitiveTagTableName+" with(nolock) where WINAME='"+WINumber+"' and  RelatedPartyID='"+value+"' ");
											WriteLog("Input XML: "+sInputXML);
											sOutputXML=executeAPI(sInputXML);
											WriteLog("Output XML: "+sOutputXML);
											xmlobj=new XMLParser(sOutputXML);
											checkCallsMainCode(xmlobj);
											String insertionOrderId=getTagValues(sOutputXML,"InsertionOrderId");
											if("".equalsIgnoreCase(insertionOrderId))
											{
												WriteLog("RelatedPartyID doesnot exists: "+value);
												throw new WICreateException("12003",pCodes.getString("12003")+" - "+value);
											}else
												RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<insertionOrderId>"+insertionOrderId+"</insertionOrderId>";
										}
										/*else if("FullName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_FullName>"+value+"</copyof_FullName>";
										else if("FirstName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_FirstName>"+value+"</copyof_FirstName>";
										else if("MiddleName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_MiddleName>"+value+"</copyof_MiddleName>";
										else if("LastName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_LastName>"+value+"</copyof_LastName>";
										else if("NameOnDebitCard".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_NameOnDebitCard>"+value+"</copyof_NameOnDebitCard>";*/
									}
									
									// marking isForzen as Y for firco rows which are already available is firco grid for respective related party id
									else if("DBOINFOUPDATE_FircoDetails".equalsIgnoreCase(RepetitiveProcessID) || "DBOFECORR_FIRCOHITDetails".equalsIgnoreCase(RepetitiveProcessID))
									{
										//WriteLog("inside DBOINFOUPDATE_FircoDetails: "+TransColListArr[j]);
										if("RelatedPartyID".equalsIgnoreCase(TransColListArr[j]))
										{
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<Hit_Date_Time>"+sDate+"</Hit_Date_Time>"; // adding datetime along with each row
											
											// taking all recevied related party id in an array
											//WriteLog("inside DBOINFOUPDATE_FircoDetails value: "+value);
											if(fircoRelatedParties.equalsIgnoreCase(""))
												fircoRelatedParties = value;
											else
												fircoRelatedParties = fircoRelatedParties+"#~#"+value;
											fircoRelatedPartiesCount++;
										}
									}
								}
								
								
							}
						}
					}
					//insertIntoRepetitiveGridTable(RepetitiveTagTableName, insertColumns, insertValues);
					
				}
				RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n</Q_"+RepetitiveTagTableName+">";
				
				if("DBOCOURIERUPDATE_EmiratesIDPingData".equalsIgnoreCase(RepetitiveProcessID))
					RepetitiveTagsAttribute="";
			}	
			
			// updating isFrozen as Y for received related party id in firco grid
			if(fircoRelatedPartiesCount > 0)
			{
				WriteLog("fircoRelatedPartiesCount: " + fircoRelatedPartiesCount);
				String arrFircoRelatedParties[] = fircoRelatedParties.split("#~#");
				fircoRelatedParties = "";
				for(int p=0;p<arrFircoRelatedParties.length;p++)
				{
					if(fircoRelatedParties.equalsIgnoreCase(""))
						fircoRelatedParties = "'"+arrFircoRelatedParties[p]+"'";
					else
						fircoRelatedParties = fircoRelatedParties + ",'"+arrFircoRelatedParties[p]+"'";
				}
				if(!fircoRelatedParties.equalsIgnoreCase(""))
					updateFircoIsFrozenFlagInDBO(fircoRelatedParties);
			}
			//////////////////////////////////
			
			return	RepetitiveTagsAttribute;
		}
		else
			return "";
	}
	
	private void insertIntoRepetitiveGridTable(String trTableName, String trTableColumn, String trTableValue) throws WICreateException, Exception {


		trTableColumn = "WINAME,"+trTableColumn;  
		
		WriteLog("trTableColumn final: " + trTableColumn);
		trTableValue = "'"+ WINumber + "'," + trTableValue;
		WriteLog("trTableValue final: " + trTableValue);
		String sInputXML = getDBInsertInputRepetitiveGridTable(trTableName, trTableColumn, trTableValue);
		WriteLog("APInsert Input RepetitiveGridTable: " + sInputXML);
		String sOutputXML = executeAPI(sInputXML);
		WriteLog("APInsert Output RepetitiveGridTable: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	
	private String getDBInsertInputRepetitiveGridTable(String trTableName, String trTableColumn, String trTableValue) {
		return "<?xml version=\"1.0\"?>" + "<APInsert_Input>"
				+ "<Option>APInsert</Option>" + "<TableName>" + trTableName
				+ "</TableName>" + "<ColName>" + trTableColumn + "</ColName>"
				+ "<Values>" + trTableValue + "</Values>" + "<EngineName>"
				+ sCabinetName + "</EngineName>" + "<SessionId>" + sSessionID
				+ "</SessionId>" + "</APInsert_Input>";
	}
	private String replaceXChars(String value)
    {
           // handling of special characters
           if(!"".equalsIgnoreCase(value) && !"null".equalsIgnoreCase(value)  && value != null)
           {
                  if(value.contains("&amp;"))
                        value = value.replace("&amp;", "AAMPRRSNDD");
                  if(value.contains("&lt;"))
                        value = value.replace("&lt;", "LLSSTNSPX");
                  if(value.contains("&gt;"))
                        value = value.replace("&gt;", "GGRTTNSPX");
                  if(value.contains("&"))
                        value = value.replace("&", "&amp;");
                  
                  if(value.contains("AAMPRRSNDD"))
                        value = value.replace("AAMPRRSNDD", "&amp;");
                  if(value.contains("LLSSTNSPX"))
                        value = value.replace("LLSSTNSPX", "&lt;");
                  if(value.contains("GGRTTNSPX"))
                        value = value.replace("GGRTTNSPX", "&gt;");
                  
                  if(value.contains("<"))
                        value = value.replace("<", "&lt;");
                  if(value.contains(">"))
                        value = value.replace(">", "&gt;");
           }
           return value;
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
	String getWorkItemInputXML = getWorkItemInput_withWorkitemID(workitemID);
	WriteLog("Input XML For WmgetWorkItemCall: "+ getWorkItemInputXML);
	String getWorkItemOutputXml = executeAPI(getWorkItemInputXML);
	WriteLog("Output XML For WmgetWorkItemCall: "+ getWorkItemOutputXml);

	XMLParser xmlParserGetWorkItem = new XMLParser(getWorkItemOutputXml);
	String getWorkItemMainCode = xmlParserGetWorkItem.getValueOf("MainCode");
	WriteLog("WmgetWorkItemCall Maincode:  "+ getWorkItemMainCode);

	if (getWorkItemMainCode.trim().equals("0")) 
	{
		WriteLog("WMgetWorkItemCall Successful: "+getWorkItemMainCode);
							
			// Updating Ext or Trans table using wfassign call
			/*if(!"DBO_COURIER_UPDATE".equalsIgnoreCase(processID))
			{*/
				runUpdateWICall();
			//}
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
				System.out.println(WINumber + "Complete Sussessfully with status ");
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
		throw new WICreateException("1007",pCodes.getString("1007"));
	}
	
	}
	catch(Exception e)
	{
		WriteLog("Exception11: "+e.getMessage());
		throw new WICreateException("1007",pCodes.getString("1007")+" : "+e.getMessage());
	}
}

  


}
