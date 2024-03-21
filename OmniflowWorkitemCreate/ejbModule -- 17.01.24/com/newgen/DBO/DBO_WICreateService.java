package com.newgen.DBO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;

import com.newgen.custom.CreateWorkitem;
/*import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.omni.wf.util.excp.NGException;*/
import com.newgen.ws.EE_EAI_HEADER;
import com.newgen.ws.exception.WICreateException;
import com.newgen.ws.request.Attribute;
import com.newgen.ws.request.Attributes;
import com.newgen.ws.request.Document;
import com.newgen.ws.request.Documents;
import com.newgen.ws.request.WICreateRequest;
import com.newgen.ws.response.WICreateResponse;
import com.newgen.ws.util.XMLParser;

import ISPack.CPISDocumentTxn;
import ISPack.ISUtil.JPDBRecoverDocData;
import ISPack.ISUtil.JPISException;
import ISPack.ISUtil.JPISIsIndex;
import adminclient.OSASecurity;

public class DBO_WICreateService extends CreateWorkitem {

	//CONSTANTS 
	final private String SUCCESS_STATUS="0";

	final private String WSR_PROCESS="USR_0_WSR_PROCESS";
	final private String WSR_ATTRDETAILS="USR_0_WSR_ATTRDETAILS";
	final private String WSR_DOCTYPEDETAILS="USR_0_WSR_DOCTYPEDETAILS";
	
	
	// for DBO
	final private String DBO_WIHISTORY = "USR_0_DBO_WIHISTORY";
	final private String txnTableName="RB_DBO_TXNTABLE";
	final private String DBOFircoTableName="USR_0_DBO_FIRCO_DTLS";
	final private String q_queueID = "";
	private int iFircoCount=0;
	private String completeFircoData = "";

	private String sProcessName;
	private String sSubProcess;
	private String sInitiateAlso;
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
	private Documents documentsObj;
	private Document documentObj[];
	private String sDocumentName;
	private String sDocumentType;
	private String sDocBase64;
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
	String sFilePath = "";
	JPISIsIndex NewIsIndex;
	JPDBRecoverDocData dataJPDB;
	String documentTag = "";
	String sDumpLocation = "";
	boolean sessionFlag = false;
	String processID = "";
	String trTableColumn = "";
	String trTableValue = "";
	String wiName = "";
	String externalTableName = "";
	String transactionTableName = "";
	String processDefID = "";
	String InitiationQueueID ="";
	String categoryId = "";
	String subCategoryId = "";
	String categoryName = "";
	String sTotalRetrieved = "";
	String sDate = "";
	String strAttributeTagForDOB="";
	
	String WINAME="";
	
	
    String RepetitiveMainTags = "";
    
    String InputMessage = "";
    LinkedHashMap<String, HashMap<String, String>> hmMain = new LinkedHashMap();	
    HashMap<String, String> hm1 = new HashMap<String, String>(); 
    
    static HashMap<String, String> hmExtMandDBO = new HashMap();
    static HashMap<String, String> hmRptProcessIdDBO = new HashMap();
    static HashMap<String, String> hmRptTransTableDBO = new HashMap();
    static LinkedHashMap<String, HashMap<String, String>> hmRptAttrAndColDBO = new LinkedHashMap();
    static LinkedHashMap<String, HashMap<String, String>> hmRptAttrAndMandDBO = new LinkedHashMap();
    static LinkedHashMap<String, HashMap<String, String>> hmRptAttrAndFormatDBO = new LinkedHashMap();
    static LinkedHashMap<String, HashMap<String, String>> hmRptAttrAndTypeDBO = new LinkedHashMap();
    
    String InputMessage1 = "";
    //private static NGEjbClient ngEjbClient;
    
	public WICreateResponse wiCreate(WICreateRequest request,String attributeList[]) throws WICreateException { 
		
			try
			{
				WriteLog("inside wiCreate DBO");
				//ngEjbClient = NGEjbClient.getSharedInstance();
				headerObjResponse=new EE_EAI_HEADER();
				//initialize Logger
				//initializeLogger();					
				iFircoCount = 0;
				completeFircoData="";
				//Load file configuration
				loadConfiguration();
				
				//Load ResourceBundle
				loadResourceBundle();
				
				//Fetching request parameters 
				fetchRequestParameters(request);
				
				//Validating Input Parameters and Check Attribute Name from USR_0_WSR_ATTRDETAILS
				validateRequestParameters(attributeList);
				
				// Duplicate check based on prospect Id
				duplicateWorkitemCheckBasedOnProspect(hm.get("ProspectID").trim());
				if(!"".equalsIgnoreCase(WINAME.trim()) || "null".equalsIgnoreCase(WINAME.trim())){
					WriteLog("returnCode: "+3335+" returnDesc: "+pCodes.getString("3335")+":"+hm.get("ProspectID").trim());
					setFailureParamResponse();
					response.setWorkitemNumber(WINAME);
					response.setErrorCode("3335");
					response.setErrorDescription(pCodes.getString("3335")+":"+hm.get("ProspectID").trim());
					return response;
				}
				
				if("DBO".equalsIgnoreCase(sProcessName))
					validateRepetitiveRequestParametersDBO();
				attributeTag=attributeTag+createRepetativeAttributeXML();
				
				//Checking existing session
				checkExistingSession();
				if ("NOTINUSE".equalsIgnoreCase(sProcessName))
            	{	
	            	//Check if all Mandatory Document attributes present in USR_0_WSR_DOCTYPEDETAILS have come 
	            	checkMandatoryDocDetails();
	            	//Download Input Documents at Server and add them to SMS
	            	downloadDocument();
            	}
            	runWICall();//WFUploadWorkItemCall executed in this method
				
				if ("DBO".equalsIgnoreCase(sProcessName))
            	{
            		insertIntoHistoryDBO(); //Entry into history table   
            		//InsertRecordsForRepetitiveTagsIBPS();
            	}

          	}
			catch(WICreateException e)
			{
				WriteLog("WICreateException caught:Message- "+e.getMessage());
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
				WriteLog("Exception caught:Message- "+e.getMessage());
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
				//hmRptAttrAndColDBO.clear();
				//hmRptAttrAndMandDBO.clear();
				//hmExtMandDBO.clear();
				hmRptProcessIdDBO.clear();
				hmRptTransTableDBO.clear();
			}
			return response;
		
	}
	
	
	//Added by Sajan to get card product from master
	private String getCardProducrFromMaster()throws WICreateException,Exception{
		String strCardProduct="";
		String strQuery="";
		if("AE".equalsIgnoreCase(hm.get("Nationality")))
			strQuery="select CARD_PRODUCT from CDOB_CARDPROUDCT with(nolock) where CDOB_PRODUCT='"+hm.get("Product_Type")+"' and NATIONALITY='AE'";
		else
			strQuery="select CARD_PRODUCT from CDOB_CARDPROUDCT with(nolock) where CDOB_PRODUCT='"+hm.get("Product_Type")+"' and NATIONALITY='All'";
		sInputXML = getAPSelectWithColumnNamesXML(strQuery);
		WriteLog("Get Card Product Input xml: " + sInputXML);
		sOutputXML = executeAPI(sInputXML);
		WriteLog("Get Card Product Output xml: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
		strCardProduct = getTagValues(sOutputXML, "CARD_PRODUCT");
		WriteLog("CardProduct: " + strCardProduct);
		
		return strCardProduct;
	}
	private String getInputXMLInsert(String tableName)
	{
		 return "<?xml version=\"1.0\"?>" +
	            "<APInsert_Input>" +
				"<Option>APInsert</Option>" +
				"<TableName>" + tableName + "</TableName>" +
				"<ColName>" + trTableColumn + "</ColName>" +
				"<Values>" + trTableValue + "</Values>" +
				"<EngineName>" + sCabinetName + "</EngineName>" +
				"<SessionId>" + sSessionID + "</SessionId>" +
	            "</APInsert_Input>";
	}
	//Added by Sajan for FALCON CDOB
	private String getAssignAttributeInputXML(){
		String inputXML="<?xml version=\"1.0\"?>"+"<WMAssignWorkItemAttributes_Input><Option>WMAssignWorkItemAttributes</Option>" +
				"<EngineName>"+sCabinetName+"</EngineName><SessionId>"+sSessionID+"</SessionId>" +
				"<ProcessInstanceId>"+response.getWorkitemNumber()+"</ProcessInstanceId><WorkitemId>1</WorkitemId>" +
				"<ActivityId>17</ActivityId><ProcessDefId>"+processDefID+"</ProcessDefId><ActivityType>1</ActivityType>" +
				"<UserDefVarFlag>Y</UserDefVarFlag><LastModifiedTime></LastModifiedTime><complete>I</complete>" +
				"<Attributes>"+strAttributeTagForDOB+"</Attributes></WMAssignWorkItemAttributes_Input>";
		return inputXML;
	}
	
	public  String getWorkItemInput(String workitemNumber){
		return "<?xml version=\"1.0\"?>" + "<WMGetWorkItem_Input>"
				+ "<Option>WMGetWorkItem</Option>" + "<ProcessInstanceId>" + workitemNumber
				+ "</ProcessInstanceId>" + "<WorkItemId>1</WorkItemId>"
				+ "<EngineName>"
				+ sCabinetName + "</EngineName>" + "<SessionId>" + sSessionID
				+ "</SessionId>" + "</WMGetWorkItem_Input>";
	}
	private void runWICall() throws WICreateException,Exception
	{
		sInputXML=getWFUploadWorkItemXML();
		WriteLog("WFUploadWorkItemXML Input: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("WFUploadWorkItemXML Output: "+sOutputXML);
    	xmlobj=new XMLParser(sOutputXML);
    	//Check Main Code
		checkCallsMainCode(xmlobj);  
		wiName=getTagValues(sOutputXML,"ProcessInstanceId");
		WriteLog("WINAME: "+wiName);
		//Update associated trans table
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

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime())); //2014/08/06 16:00:22
		
		//trTableColumn = "winame,wsname,decision,actiondatetime,remarks,username,entrydatetime";
		trTableColumn = "WINAME,WORKSTEP,DECISION,ACTION_DATE_TIME,REMARKS,USER_NAME,ENTRY_DATE_TIME";
		WriteLog("trTableColumn dor DAC" + trTableColumn);
		trTableValue = "'"+ wiName + "','Initiation','Submit','" + sDate + "','Workitem created through webservice','" + sUsername + "','" + sDate + "'";
		WriteLog("trTableValue for DBO" + trTableValue);
		sInputXML = getAPInsertInputXML(DBO_WIHISTORY,trTableColumn,trTableValue);
		WriteLog("APInsert Input History: " + sInputXML);
		sOutputXML = executeAPI(sInputXML);
		WriteLog("APInsert Output History: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	private String getAPInsertInputXML(String tableName,String colName,String colValues) {
		return "<?xml version=\"1.0\"?>" + "<APInsert_Input>"
				+ "<Option>APInsert</Option>" + "<TableName>" + tableName
				+ "</TableName>" + "<ColName>" + colName + "</ColName>"
				+ "<Values>" + colValues + "</Values>" + "<EngineName>"
				+ sCabinetName + "</EngineName>" + "<SessionId>" + sSessionID
				+ "</SessionId>" + "</APInsert_Input>";
	}
	
	private void downloadDocument() throws WICreateException, Exception
	{
		WriteLog("inside downloadDocument");
		for(int i=0;i<documentObj.length;i++)
    	{
			sDocumentName=documentObj[i].getDocumentName();
    		sDocumentType=documentObj[i].getDocumentType();
    		sDocBase64=documentObj[i].getBase64String();
    		WriteLog("Doc Name: "+sDocumentName);
    		WriteLog("Doc Type: "+sDocumentType);
    		//Initial check for document type and base64 
    		if(sDocumentType.equalsIgnoreCase("") && !sDocumentName.equalsIgnoreCase(""))
    		{
    			WriteLog("No Value for Document Type");
    			throw new WICreateException("1012",pCodes.getString("1012")+": "+sDocumentName);
    		}
    		if(sDocBase64.equalsIgnoreCase("") && !sDocumentName.equalsIgnoreCase(""))
    		{
    			WriteLog("No Value for DocBase64");
    			throw new WICreateException("1008",pCodes.getString("1008")+": "+sDocumentName);
    		}
    		if(!sDocumentType.equalsIgnoreCase("") && !sDocumentName.equalsIgnoreCase("") && !sDocBase64.equalsIgnoreCase(""))
    		{
    			validateDocumentNameType();
    			dumpFileOnServer();
    			addToSMS(); 
    			deleteDumpedFileOnServer();
    		}
    	}
	}
	private void getProcessDefID() throws WICreateException, Exception
	{
		sInputXML=getAPSelectWithColumnNamesXML("select a.PROCESSDEFID, b.PROCESSID from processdeftable a with(nolock),"+WSR_PROCESS+" b with(nolock) where a.processname='"+sProcessName+"' and b.SUBPROCESSNAME='"+sSubProcess+"' and a.processname=b.processname and isactive='Y'");
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
	private void getQueueDefID() throws WICreateException, Exception
	{
		sInputXML=getAPSelectWithColumnNamesXML("select QueueID from QUEUEDEFTABLE with(nolock) where ProcessName='"+sProcessName+"' and QueueName='DBO_Initiation'");
		WriteLog("APSelectWithColumnNames Input: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("APSelectWithColumnNames Output: "+sOutputXML);
    	xmlobj=new XMLParser(sOutputXML);
    	//Check Main Code
		checkCallsMainCode(xmlobj); 
		InitiationQueueID=getTagValues(sOutputXML, "QueueID");
		WriteLog("QueueID: "+InitiationQueueID);
		if(InitiationQueueID.equalsIgnoreCase(""))
			throw new WICreateException("6015",pCodes.getString("6015")+":"+sProcessName+"/"+sSubProcess);
		
		
	}
	private void checkMandatoryDocDetails() throws WICreateException, Exception
	{

		WriteLog("inside checkMandatoryDocDetails");
		sInputXML=getAPSelectWithColumnNamesXML("select DOCUMENTNAME from "+WSR_DOCTYPEDETAILS+" with(nolock) where PROCESSID='"+processID+"' and ISMANDATORY='Y' and ISACTIVE='Y'");
		WriteLog("Input XML: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		String documentList []=getTagValues(sOutputXML,"DOCUMENTNAME").split("`");
		WriteLog("Doc Length: "+documentList.length);
		sTotalRetrieved=getTagValues(sOutputXML,"TotalRetrieved");
		if(!sTotalRetrieved.equalsIgnoreCase("0"))
		{
			for(int i=0;i<documentList.length;i++)
			{
				String flag="N";
				for(int j=0;j<documentObj.length;j++)
				{
					if(documentList[i].equalsIgnoreCase(documentObj[j].getDocumentName()))
						flag="Y";
				}
			    //WriteLog("Value of flag: "+flag);
			    if(flag.equalsIgnoreCase("N"))
					throw new WICreateException("1023",pCodes.getString("1023")+": "+documentList[i]);
			}
		}
		/*
		else
		{
			throw new WICreateException("1022",pCodes.getString("1022")+" for process id: "+processID);
		}
		*/
		
	}
	
	private void checkExistingSession() throws Exception, WICreateException
	{
		WriteLog("inside checkExistingSession");
		//String getSessionQry="select top(1) RANDOMNUMBER from pdbconnection";
		String getSessionQry="select randomnumber from pdbconnection with(nolock) where userindex in (select userindex from pdbuser with(nolock) where username='"+sUsername+"')";
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
	
	private void validateDocumentNameType() throws WICreateException
	{ 
		//Fetch from USR_0_WSR_DOCTYPEDETAILS
		sInputXML=getAPSelectWithColumnNamesXML("select DOCUMENTNAME, DOCTYPE, ISMANDATORY  from "+WSR_DOCTYPEDETAILS+" with(nolock) where PROCESSID='"+processID+"' and DOCUMENTNAME='"+sDocumentName+"' and DOCTYPE='"+sDocumentType+"' and ISMANDATORY='Y' and ISACTIVE='Y'");
		WriteLog("APSelectWithColumnNames Input: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("APSelectWithColumnNames Output: "+sOutputXML);
    	xmlobj=new XMLParser(sOutputXML);
    	//Check Main Code
		checkCallsMainCode(xmlobj); 
		String docNameTable=getTagValues(sOutputXML, "DOCUMENTNAME");
		String docTypeTable=getTagValues(sOutputXML, "DOCTYPE");
		//String isMand=getTagValues(sOutputXML, "ISMANDATORY");
		if(docNameTable.equalsIgnoreCase(""))
		{
			WriteLog("No Value mapped for DocName");
			throw new WICreateException("1018",pCodes.getString("1018")+": "+sDocumentName+" and "+sDocumentType);
		}
		 
		
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
	private void addToSMS() throws WICreateException,IOException, Exception
	{
		//Adding Document to SMS
		WriteLog("SMS Addition starts for: "+sFilePath);
		NewIsIndex = new JPISIsIndex();
		dataJPDB =  new JPDBRecoverDocData();
		try
		{
			CPISDocumentTxn.AddDocument_MT(null, sJtsIp, (short)iJtsPort,
					sCabinetName, (short)iVolId, sFilePath, dataJPDB, null, NewIsIndex);
		
		
		String isIndex=(new StringBuilder()).
				append(NewIsIndex.m_nDocIndex).append("#").append(NewIsIndex.m_sVolumeId).append("#").toString();
		WriteLog("Data returned from SMS: "+isIndex);
		documentTag=documentTag+sDocumentName+" "+isIndex+" ";
		}
		catch(JPISException e)
		{
			WriteLog("JPISException: "+e);
			throw new WICreateException("1000",pCodes.getString("1000")+" : "+e.getMessage());
		}
		WriteLog("Document Tag: "+documentTag);
		
	}
	private void dumpFileOnServer() throws WICreateException,IOException,Exception
	{
		try
		{
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
			String strDate = sdf.format(cal.getTime());
			strDate= strDate.replace("/","");
		    strDate=strDate.replace(":","");
		    strDate=strDate.replace(".","");
		    strDate=strDate.replace(" ","");
		    sDumpLocation=sTempLoc+"/BinaryDump/"+strDate+"/";
			File fDumpFolder=new File(sDumpLocation);
			if(!fDumpFolder.exists())
				fDumpFolder.mkdirs();
			sFilePath=new StringBuilder().append(sDumpLocation).append(sDocumentName).append(".").append(sDocumentType).toString();
			WriteLog("Dump File Path: "+sFilePath);
			byte[] base64Bytes = sDocBase64.getBytes(Charset.forName("UTF-8"));
			WriteLog("Size of base64 encoded bytes: "+base64Bytes.length);
			byte binaryBytes[] = Base64.decodeBase64(base64Bytes);
			File fDumpFile=new File(sFilePath);
			FileOutputStream fileoutputstream = new FileOutputStream(fDumpFile);
			fileoutputstream.write(binaryBytes);
			fileoutputstream.close();
		}
		catch (IOException e)
		{
			WriteLog("IOException: "+e);
			throw new WICreateException("1004",pCodes.getString("1004")+" : "+e.getMessage());
		
		}
		catch (Exception e)
		{
			WriteLog("Exception: "+e);
			throw new WICreateException("1004",pCodes.getString("1004")+" : "+e.getMessage());
		}
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

	
	
	private void deleteDumpedFileOnServer() 
	{
		WriteLog("inside deleteDumpedFileOnServer");
		try
		{
			File fDumpFolder=new File(sDumpLocation);
			FileUtils.deleteDirectory(fDumpFolder);
			WriteLog("Folder Deleted");
		}
		catch (Exception e)
		{
			WriteLog("Exception: "+e.getMessage());
		}
	}
	
	
	private void loadConfiguration() throws IOException, Exception
	{
		//load file configuration
		WriteLog("Loading Configuration file 123");
		try
		{
			
			Properties p = new Properties();
			String sConfigFile=new StringBuilder().append(System.getProperty("user.dir"))
					.append(System.getProperty("file.separator")).append("BPMCustomWebservicesConf")
					.append(System.getProperty("file.separator")).append("config.properties").toString();
			
			WriteLog("config file path is "+sConfigFile);
			
			p.load(new FileInputStream(sConfigFile));
			WriteLog("After p load");
		    sCabinetName=p.getProperty("CabinetName");
		    sJtsIp=p.getProperty("JtsIp");
		    iJtsPort=Integer.parseInt(p.getProperty("JtsPort"));
		    sUsername=p.getProperty("username");
		
			//WriteLog("sPassword::"+sPassword);
			sPassword=decryptPassword(p.getProperty("password"));	
			//WriteLog("after decrption::"+sPassword);
			sTempLoc=p.getProperty("TempDocumentLoc");
		    iVolId=Integer.parseInt(p.getProperty("volid"));
		    WriteLog("CabinetName: "+sCabinetName+", JtsIp: "+sJtsIp+", JtsPort: "+iJtsPort+" ,Username: "+sUsername+", Password: "+p.getProperty("password")+" ,VolumeID: "+iVolId);
		    WriteLog("Configuration file loaded successfuly");
		    response.setCabinetName(sCabinetName);
		    response.setJtsIp(sJtsIp);
		    response.setJtsPort(iJtsPort);
		    response.setUsername(sUsername);
		}
		catch(Exception e)
		{
			WriteLog("Inside exception of log decryption");
			throw new WICreateException("3001",pCodes.getString("3001"));
		}  
	}
	/*
	private String decryptPassword(String pass) throws Exception
	{
		return AESEncryption.decrypt(pass);
	}
	*/
	private void initializeLogger() throws IOException
	{
		Properties p = new Properties();
		String log4JPropertyFile=new StringBuilder().append(System.getProperty("user.dir"))
				.append(System.getProperty("file.separator")).append("BPMCustomWebservicesConf")
				.append(System.getProperty("file.separator")).append("log4j.properties").toString();
		//System.out.println("Path of log4j file: "+log4JPropertyFile);
		
		p.load(new FileInputStream(log4JPropertyFile));
		PropertyConfigurator.configure(p);
		WriteLog("Logger is configured successfully");
		
	}
	
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
		
		/*try {
			return ngEjbClient.makeCall(sJtsIp, iJtsPort + "", "WebSphere", sInputXML);
		} catch (NGException e) {
			WriteLog("Exception: "+e.getMessage());
			throw new WICreateException("1006",pCodes.getString("1006")+" : "+e.getMessage());
		}*/
     	
	}
	
	private void fetchRequestParameters(WICreateRequest req) throws WICreateException
	{		
		try
		{
			WriteLog("Inside fetchRequestParameters");
			sProcessName=(req.getProcessName()==null)? "" : req.getProcessName().trim();
			sSubProcess=(req.getSubProcess()==null)? "" : req.getSubProcess().trim();
			sInitiateAlso=(req.getInitiateAlso()==null)? "" : req.getInitiateAlso().trim();
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
				hm.put(attributeObj[i].getName(),attrValue );
			}
			//Fetch Document base64 String 
			documentsObj=req.getDocuments();
			documentObj=documentsObj.getDocument();
			
			WriteLog("sProcessName: "+sProcessName+", sSubProcess: "+sSubProcess
					+" sInitiateAlso: "+sInitiateAlso);
		}
		catch(Exception e)
		{
			WriteLog("Exception: "+e.getMessage());
			throw new WICreateException("1007",pCodes.getString("1007")+" : "+e.getMessage());
		}
	}
	private void checkAttributeFormatAndLength(String attributeName, String attributeValue, String attrFormat, String attType) throws WICreateException, Exception
	{
		WriteLog("inside checkAttributeFormatAndLength attributeName-"+attributeName+", attType-"+attType+", attrFormat-"+attrFormat+", attributeValue-"+attributeValue);
		
		if(!(attributeValue.equalsIgnoreCase("")))
		{
			int attributelength = attributeValue.length();
			if(!attType.equalsIgnoreCase(""))
			{
				if(attType.equalsIgnoreCase("DATE"))
				{
					try
					{
						Date date = null;
						SimpleDateFormat sdf = new SimpleDateFormat(attrFormat);
					    date = sdf.parse(attributeValue);
					    if (!attributeValue.equals(sdf.format(date))) 
					    {
					    	throw new WICreateException("1116",pCodes.getString("1116")+" :"+attributeName);
					    }
					}
					catch(Exception ex)
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
					//WriteLog("inside 991--"+attributeName);	
					int attributesize = Integer.parseInt(attrFormat);
					if(attributelength>attributesize)
					{
						throw new WICreateException("1111",pCodes.getString("1111")+" :"+attributeName);
					}
					//WriteLog("inside 992--"+attributeName);	
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
						WriteLog("inside APLPHANUMERICWITHSPECIALCHAR-"+attributeValue);
						//patternMatch="^[a-zA-Z0-9-#_!.@()+/%&\\s|~ ]*$";
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
				
				if (!ExtColsName[i].trim().equalsIgnoreCase("") && !ExtColsName[i].trim().equalsIgnoreCase("#"))
				{
					WriteLog("this is a extn table attribute:-"+AttrTagsName[i]);
					
					attributeTag=attributeTag+"\n<"+ExtColsName[i]+">"+attributeValue+"</"+ExtColsName[i]+">";
					//WriteLog("attributeTag:-"+attributeTag);
					if("DBO".equalsIgnoreCase(sProcessName))
					{
						if("TLNumber".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_TL_Number>"+attributeValue+"</copyof_TL_Number>";
						else if("TradeName".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_TradeName>"+attributeValue+"</copyof_TradeName>";
						else if("TLIssuingAuthority".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_TL_Issuing_Authority>"+attributeValue+"</copyof_TL_Issuing_Authority>";
						else if("TLIssuingAuthorityEmirate".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_TL_Issuing_Authority_Emirate>"+attributeValue+"</copyof_TL_Issuing_Authority_Emirate>";
						else if("TLExpiryDate".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_TL_ExpiryDate>"+attributeValue+"</copyof_TL_ExpiryDate>";
						else if("TLTypeOfOffice".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_TL_TypeofOffice>"+attributeValue+"</copyof_TL_TypeofOffice>";
						else if("DateOfIncorporation".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_DateofIncorporation>"+attributeValue+"</copyof_DateofIncorporation>";
						else if("CompanyName".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_CompanyName>"+attributeValue+"</copyof_CompanyName>";
						else if("CompanyShortName".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_CompanyShortName>"+attributeValue+"</copyof_CompanyShortName>";
						else if("NameOnChqBk".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_NameOnChequeBook>"+attributeValue+"</copyof_NameOnChequeBook>";
						else if("CompanyCategory".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_CompanyCategory>"+attributeValue+"</copyof_CompanyCategory>";
						else if("Persona".equalsIgnoreCase(AttrTagsName[i]))
							attributeTag=attributeTag+"\n<copyof_Persona>"+attributeValue+"</copyof_Persona>";
						else if("RiskScore".equalsIgnoreCase(AttrTagsName[i]) && !"".equalsIgnoreCase(attributeValue))
						{
							double riskScore = Double.parseDouble(attributeValue);
							String isHighRisk = "N";
							if(riskScore >= 4.05)
								isHighRisk = "Y";
							attributeTag=attributeTag+"\n<IsHighRisk>"+isHighRisk+"</IsHighRisk>";
						}
					}
				}
				else if(!txnColsName[i].trim().equalsIgnoreCase("") && !txnColsName[i].trim().equalsIgnoreCase("#"))
				{
					WriteLog("this is a txn table attribute:-"+AttrTagsName[i]);
					txnTableAttributes=txnTableAttributes+"\n<"+txnColsName[i]+">"+attributeValue+"</"+txnColsName[i]+">";
					//WriteLog("txnTableAttributes:-"+txnTableAttributes);
					
					if("DBO".equalsIgnoreCase(sProcessName))
					{
						if("LicenseType".equalsIgnoreCase(AttrTagsName[i]))
							txnTableAttributes=txnTableAttributes+"\n<copyof_LicenseType>"+attributeValue+"</copyof_LicenseType>";
						else if("CountryOfIncorporation".equalsIgnoreCase(AttrTagsName[i]))
							txnTableAttributes=txnTableAttributes+"\n<copyof_CountryOfIncoporation>"+attributeValue+"</copyof_CountryOfIncoporation>";
					}
					
					/*if("FircoAlertDetails".equalsIgnoreCase(AttrTagsName[i]))
					{
						completeFircoData=completeFircoData+parseFircoDetails(attributeValue, hmt.get("ProspectID").trim());
					}*/
				}
				else
				{
					WriteLog("some error occured for attribute:-"+AttrTagsName[i]);
					throw new WICreateException("1015",pCodes.getString("1015")+" :"+AttrTagsName[i]);
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
			txnTableAttributes="";
		attributeTag=attributeTag+txnTableAttributes;
		WriteLog("Final attributeTag:-"+attributeTag);
		
				
	}
	
	private void getTableName() throws WICreateException, Exception
	{
		//from tables here
		String getTableNameQry="select EXTERNALTABLE ,TRANSACTIONTABLE  from "+WSR_PROCESS+" with(nolock) where PROCESSID='"+processID+"'";
		sInputXML=getAPSelectWithColumnNamesXML(getTableNameQry);
		WriteLog("Input XML: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output XML: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		checkCallsMainCode(xmlobj);
		externalTableName=getTagValues(sOutputXML, "EXTERNALTABLE");
		WriteLog("External table Name: "+externalTableName);
		transactionTableName=getTagValues(sOutputXML, "TRANSACTIONTABLE");
		WriteLog("Transaction table Name: "+transactionTableName);
	}
	private void validateRequestParameters(String attributeList[]) throws WICreateException,Exception
	{
		WriteLog("Inside validateRequestParameters");
		if(sProcessName.equalsIgnoreCase("?") || sProcessName.equalsIgnoreCase(""))
			throw new WICreateException("1001",pCodes.getString("1001"));
		if((sSubProcess.equalsIgnoreCase("?") || sSubProcess.equalsIgnoreCase("")))
			throw new WICreateException("1002",pCodes.getString("1002"));
		if(!(sInitiateAlso.equalsIgnoreCase("Y") || sInitiateAlso.equalsIgnoreCase("N")))
			throw new WICreateException("1003",pCodes.getString("1003"));
		if(sRequestorChannelId.equalsIgnoreCase("?") || sRequestorChannelId.equalsIgnoreCase(""))
			throw new WICreateException("1011",pCodes.getString("1011"));
		//Fetch ProcessDefID
		getProcessDefID();
		//Fetch queue Id where wi need to be created
		getQueueDefID();
		
		//Check if all Mandatory attributes present in USR_0_WSR_ATTRDETAILS have come
		if("DBO".equalsIgnoreCase(sProcessName)){
			checkMandatoryAttributeDBO();
		}
		
		Date d= new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sDate = dateFormat.format(d);
		
		if("DBO".equalsIgnoreCase(sProcessName)){
			checkExtTableAttrIBPS(hm);
		}
		
		getTableName();
	}
	private void checkMandatoryAttributeDBO() throws WICreateException, Exception
	{
		WriteLog("inside checkMandatoryAttributeDBO");
		String attributeList [];
		if(hmExtMandDBO.size() == 0)
		{
			sInputXML=getAPSelectWithColumnNamesXML("select ATTRIBUTENAME from "+WSR_ATTRDETAILS+" with(nolock) where PROCESSID='"+processID+"' and ISMANDATORY='Y' and ISACTIVE='Y'");
			WriteLog("Input XML: "+sInputXML);
			sOutputXML=executeAPI(sInputXML);
			WriteLog("Output XML: "+sOutputXML);
			xmlobj=new XMLParser(sOutputXML);
			checkCallsMainCode(xmlobj);
			attributeList=getTagValues(sOutputXML,"ATTRIBUTENAME").split("`");
			if(attributeList.length>0)
			{
				for(int i=0;i<attributeList.length;i++)
				{
					hmExtMandDBO.put(attributeList[i], "");
				}
			}	
		}
		else 
		{
			String AttrTagName = "";
			for (String name: hmExtMandDBO.keySet()){
				if(AttrTagName.equalsIgnoreCase(""))
					AttrTagName = name.toString();
				else 
					AttrTagName = AttrTagName+"`"+name.toString();
			}
			attributeList=AttrTagName.split("`");
		}
		
		
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
			        //WriteLog("The key is "+me.getKey());
			        if(me.getKey().toString().equalsIgnoreCase(attributeList[i])){
			        	flag="Y";
			        	break;
			        }
			   
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
	
	private void validateRepetitiveRequestParametersDBO() throws WICreateException, Exception
	{
		String repetitiveListMain[];
		String repetitiveList[];
		
		RepetitiveMainTags = checkMandatoryRepetitiveTagsDBO();
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
			        			String RepetitiveProcessID = hmRptProcessIdDBO.get(attributeList[i]);
			        			checkMandatoryRepetitiveAttributeDBO(RepetitiveProcessID,"Y",hm1);
				        		hmMain.put(attributeList[i]+"-"+Integer.toString(j), hm1);
				        	}
			        	}
					}
				}
			}
		}
			
		
		
	}
	private String checkMandatoryRepetitiveTagsDBO() throws WICreateException, Exception
	{
		WriteLog("inside checkMandatoryRepetitiveTagsDBO");
		String sInputXML=getAPSelectWithColumnNamesXML("select REPETITIVETAGNAME,PROCESSID,TRANSACTIONTABLE,ISMANDATORY from USR_0_WSR_PROCESS_REPETITIVE with(nolock) where ProcessName='"+sProcessName+"' and SUBPROCESSNAME='"+sSubProcess+"' and ISACTIVE='Y'");
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
			    if(flag.equalsIgnoreCase("N"))
					throw new WICreateException("1020",pCodes.getString("1020")+": "+RepetitiveList[i]);
			
			    hmRptProcessIdDBO.put(RepetitiveList[i], RepProcessId[i]);
			    hmRptTransTableDBO.put(RepProcessId[i], RepTransTable[i]);
			}
		}
		return RepetitiveMainTags;
	}
	

	
	private String checkMandatoryRepetitiveAttributeDBO(String RepetitiveProcessId,String isValidatingMandate, HashMap<String, String> RepetitiveReqAttr) throws WICreateException, Exception
	{
		WriteLog("inside checkMandatoryRepetitiveAttributeIBPS");
		String AttrTagName = "";
		String TransColName = "";
		String AttrFormat = "";
		String AttrType = "";
		String isMandatory = "";
		if(!hmRptAttrAndColDBO.containsKey(RepetitiveProcessId))
		{
			String sInputXML=getAPSelectWithColumnNamesXML("select ATTRIBUTENAME,TRANSACTIONTABLECOLNAME,isnull(nullif(ATTRIBUTE_FORMAT,''),'#') as ATTRIBUTE_FORMAT, isnull(nullif(ATTRIBUTE_TYPE,''),'#') as ATTRIBUTE_TYPE,ISMANDATORY from USR_0_WSR_ATTRDETAILS_REPETITIVE with(nolock) where PROCESSID='"+RepetitiveProcessId+"' and ISACTIVE='Y' order by AttributeName");
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
			hmAttr = hmRptAttrAndColDBO.get(RepetitiveProcessId);
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
			hmMand = hmRptAttrAndMandDBO.get(RepetitiveProcessId);
			for (String name1: hmMand.keySet()){
				if(isMandatory.equalsIgnoreCase(""))
					isMandatory = hmMand.get(name1).toString(); 
				else
					isMandatory = isMandatory+"`"+hmMand.get(name1).toString();
			}
			
			HashMap<String, String> hmFormat = new HashMap();
			hmFormat = hmRptAttrAndFormatDBO.get(RepetitiveProcessId);
			for (String name1: hmFormat.keySet()){
				if(AttrFormat.equalsIgnoreCase(""))
					AttrFormat = hmFormat.get(name1).toString(); 
				else
					AttrFormat = AttrFormat+"`"+hmFormat.get(name1).toString();
			}
			
			HashMap<String, String> hmType = new HashMap();
			hmType = hmRptAttrAndTypeDBO.get(RepetitiveProcessId);
			for (String name1: hmType.keySet()){
				if(AttrType.equalsIgnoreCase(""))
					AttrType = hmType.get(name1).toString(); 
				else
					AttrType = AttrType+"`"+hmType.get(name1).toString();
			}
			
		}
		
		if(isValidatingMandate.equalsIgnoreCase("Y"))
		{	
			WriteLog("Check111");
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
				    if(flag.equalsIgnoreCase("N"))
						throw new WICreateException("1020",pCodes.getString("1020")+": "+AttrTagsName[i]+" for "+ RepetitiveProcessId);
				    
				   
				    WriteLog("Check222 - "+AttrTagsName[i]);
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
						WriteLog("Check333 - "+AttrTagsName[i]);	
				}
			}
			else
			{
				throw new WICreateException("1021",pCodes.getString("1021")+" for process id: "+processID);
			}
			
			if(!hmRptAttrAndColDBO.containsKey(RepetitiveProcessId))
			{
				if(AttrTagsName.length>0)
				{
					HashMap<String, String> hmtmp = new HashMap();
					for(int i=0;i<AttrTagsName.length;i++)
					{
						hmtmp.put(AttrTagsName[i], TransColsName[i]);
					}
					hmRptAttrAndColDBO.put(RepetitiveProcessId, hmtmp);
					
					HashMap<String, String> hmtmp1 = new HashMap();
					for(int i=0;i<AttrTagsName.length;i++)
					{
						hmtmp1.put(AttrTagsName[i], MandateList[i]);
					}
					hmRptAttrAndMandDBO.put(RepetitiveProcessId, hmtmp1);
					
					HashMap<String, String> hmtmp2 = new HashMap();
					for(int i=0;i<AttrTagsName.length;i++)
					{
						hmtmp2.put(AttrTagsName[i], AttrFormatList[i]);
					}
					hmRptAttrAndFormatDBO.put(RepetitiveProcessId, hmtmp2);
					
					HashMap<String, String> hmtmp3 = new HashMap();
					for(int i=0;i<AttrTagsName.length;i++)
					{
						hmtmp3.put(AttrTagsName[i], AttrTypeList[i]);
					}
					hmRptAttrAndTypeDBO.put(RepetitiveProcessId, hmtmp3);
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
	private void InsertRecordsForRepetitiveTagsIBPS() throws WICreateException, Exception
	{
		if(hmMain.size()!= 0)
		{
			WriteLog("Inside InsertRecordsForRepetitiveTagsIBPS");
			for (String name: hmMain.keySet()){
	            String key = name.toString();
	            String keyvalue = hmMain.get(name).toString();  
	            //WriteLog("hmMain: "+key + " " + keyvalue);  
	            String RepetitiveTags [] = key.split("-");
	            String RepetitiveProcessID = hmRptProcessIdDBO.get(RepetitiveTags[0]);
				String RepetitiveTagTableName=hmRptTransTableDBO.get(RepetitiveProcessID);
				//WriteLog("RepetitiveProcessID:"+RepetitiveProcessID);
				//WriteLog("RepetitiveTagTableName:"+RepetitiveTagTableName);
				HashMap<String, String> hm1 = new HashMap(); 
				hm1 = hmMain.get(key);
				//WriteLog(key+" hm1 size: "+hm1.size());
				
				HashMap<String, String> hmtmp = new HashMap();
				hmtmp = hmRptAttrAndColDBO.get(RepetitiveProcessID);
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
	}
	
	private void insertIntoRepetitiveGridTable(String trTableName, String trTableColumn, String trTableValue) throws WICreateException, Exception {

		if ("OECD".equalsIgnoreCase(sProcessName))
			trTableColumn = "WINAME,"+trTableColumn;
		if ("DBO".equalsIgnoreCase(sProcessName))
			trTableColumn = "WI_NAME,"+trTableColumn;
		if ("DigitalAO".equalsIgnoreCase(sProcessName))
			trTableColumn = "WI_NAME,"+trTableColumn;  //hritik 5.4.22  
		if ("Digital_CC".equalsIgnoreCase(sProcessName))
			trTableColumn = "WI_NAME,"+trTableColumn;
		if ("BSR".equalsIgnoreCase(sProcessName))
			trTableColumn = "WI_NAME,"+trTableColumn; //Reddy 7.6.22 BSR Process
		if ("BAIS".equalsIgnoreCase(sProcessName))
			trTableColumn = "WINAME,"+trTableColumn; //SOUMYA 
		
		WriteLog("trTableColumn final: " + trTableColumn);
		trTableValue = "'"+ wiName + "'," + trTableValue;
		WriteLog("trTableValue final: " + trTableValue);
		String sInputXML = getDBInsertInputRepetitiveGridTable(trTableName, trTableColumn, trTableValue);
		WriteLog("APInsert Input RepetitiveGridTable: " + sInputXML);
		String sOutputXML = executeAPI(sInputXML);
		WriteLog("APInsert Output RepetitiveGridTable: " + sOutputXML);
		xmlobj = new XMLParser(sOutputXML);
		// Check Main Code
		checkCallsMainCode(xmlobj);
	}
	private String createRepetativeAttributeXML() throws WICreateException, Exception{
		if(hmMain.size()!= 0)
		{
			WriteLog("Inside InsertRecordsForRepetitiveTagsIBPS");
			Date d= new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sDate = dateFormat.format(d);
			String RepetitiveTagsAttribute="";
			for (String name: hmMain.keySet()){
	            String key = name.toString();
	            String keyvalue = hmMain.get(name).toString();  
	            //WriteLog("hmMain: "+key + " " + keyvalue);  
	            String RepetitiveTags [] = key.split("-");
	            String RepetitiveProcessID = hmRptProcessIdDBO.get(RepetitiveTags[0]);
				String RepetitiveTagTableName=hmRptTransTableDBO.get(RepetitiveProcessID);
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
				hmtmp = hmRptAttrAndColDBO.get(RepetitiveProcessID);
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
								RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<"+TransColListArr[j]+">"+value+"</"+TransColListArr[j]+">";
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
									if("DBOWBA_RelatedPartyDetails".equalsIgnoreCase(RepetitiveProcessID))
									{
										if("FullName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_FullName>"+value+"</copyof_FullName>";
										else if("FirstName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_FirstName>"+value+"</copyof_FirstName>";
										else if("MiddleName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_MiddleName>"+value+"</copyof_MiddleName>";
										else if("LastName".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_LastName>"+value+"</copyof_LastName>";
										else if("NameOnDebitCard".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_NameOnDebitCard>"+value+"</copyof_NameOnDebitCard>";
										else if("DOB".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_DOB>"+value+"</copyof_DOB>";
										else if("Title".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_Title>"+value+"</copyof_Title>";
										else if("Nationality".equalsIgnoreCase(TransColListArr[j]))
											RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<copyof_Nationality>"+value+"</copyof_Nationality>";
									}
								}
							}
						}
					}
					//insertIntoRepetitiveGridTable(RepetitiveTagTableName, insertColumns, insertValues);
				}
				
				if("DBO".equalsIgnoreCase(sProcessName))
				{
					if("STP".equalsIgnoreCase(hm.get("TypeOfRequest")) && "DBOWBA_RelatedPartyDetails".equalsIgnoreCase(RepetitiveProcessID))
					{
						//RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<ChequeBookRecipient>Y</ChequeBookRecipient>";
						RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<PEP>NPEP</PEP>";
					}
					if( ("SP".equalsIgnoreCase(hm.get("Persona")) || "SPLL".equalsIgnoreCase(hm.get("Persona"))) 
							&& "DBOWBA_RelatedPartyDetails".equalsIgnoreCase(RepetitiveProcessID))
					{
						if("Y".equalsIgnoreCase(hm.get("IsChqBkReq"))||"Yes".equalsIgnoreCase(hm.get("IsChqBkReq")))
							RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<ChequeBookRecipient>Y</ChequeBookRecipient>";
						else
							RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<ChequeBookRecipient>N</ChequeBookRecipient>";
					}
					
					if("DBOWBA_FircoDetails".equalsIgnoreCase(RepetitiveProcessID))
					{
						RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<Hit_Date_Time>"+sDate+"</Hit_Date_Time>"; 
					}
				}
				
				RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n</Q_"+RepetitiveTagTableName+">";
				
			}	
			return	RepetitiveTagsAttribute+completeFircoData;
		}
		else
			return "";
	}
	
	private String getDBInsertInputRepetitiveGridTable(String trTableName, String trTableColumn, String trTableValue) {
		return "<?xml version=\"1.0\"?>" + "<APInsert_Input>"
				+ "<Option>APInsert</Option>" + "<TableName>" + trTableName
				+ "</TableName>" + "<ColName>" + trTableColumn + "</ColName>"
				+ "<Values>" + trTableValue + "</Values>" + "<EngineName>"
				+ sCabinetName + "</EngineName>" + "<SessionId>" + sSessionID
				+ "</SessionId>" + "</APInsert_Input>";
	}
	// End - adding methods for repetitive tags added on 26/10/2020
	
	
	public void check_high_risk()throws WICreateException, Exception
	{ //try catch 
		WriteLog("Inside DigitalAO high_risk");
		String high_risk="N";
		try
		{
			String score = hm.get("Risk_Score");
			Double risk_src = Double.parseDouble(score);
			if(risk_src>=4)
			{
				high_risk="Y";
			}
			else
			{
				high_risk="N";
			}
		}
		catch(Exception e){
			WriteLog("Exception: "+e.getMessage());
			throw new WICreateException("7032",pCodes.getString("7032")+" for process id: "+processID);
		}
		// how to put in WFUploadWorkItemXML?
		hm.put("risk_score_flag",high_risk);
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
	
	private String getWFUploadWorkItemXML()
	{
				
			return  "<?xml version=\"1.0\"?>\n"+
					"<WFUploadWorkItem_Input>\n"+
					"<Option>WFUploadWorkItem</Option>\n"+
					"<EngineName>"+sCabinetName+"</EngineName>\n"+
					"<SessionId>"+sSessionID+"</SessionId>\n"+
					"<ProcessDefId>"+processDefID+"</ProcessDefId>\n"+
					"<QueueId>"+InitiationQueueID+"</QueueId>\n"+
					"<InitiateAlso>"+sInitiateAlso+"</InitiateAlso>\n"+
					"<Attributes>"+attributeTag+"</Attributes>\n"+
					"<UserDefVarFlag>Y</UserDefVarFlag>\n"+
					"</WFUploadWorkItem_Input>";
		
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
	
	public static String apDeleteInput(String cabinetName,String sessionID, String tableName, String sWhere)
	{ 	
		StringBuffer ipXMLBuffer=new StringBuffer();

		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<APDelete_Input>\n");
		ipXMLBuffer.append("<Option>APDelete</Option>\n");
		ipXMLBuffer.append("<TableName>");
		ipXMLBuffer.append(tableName);
		ipXMLBuffer.append("</TableName>\n");
		ipXMLBuffer.append("<WhereClause>");
		ipXMLBuffer.append(sWhere);
		ipXMLBuffer.append("</WhereClause>\n");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<SessionId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</SessionId>\n");
		ipXMLBuffer.append("</APDelete_Input>");

		return ipXMLBuffer.toString();
	}
	
	public String parseFircoDetails( String FircoData, String relatedPartyId) throws WICreateException
	{
		/*String fircoDetails = "";
		String[] arrOfStr1 = FircoData.split("=============================");
		 if(arrOfStr1.length==1)
		 {
			 WriteLog("RLOS value of ReturnCodeFIRCO");
		 }
		 else if(arrOfStr1.length>1)
		 {
			// String rlos_firco_update ="update NG_RLOS_FIRCO set call_valid = 'N' where Workitem_no='"+processInstanceID+"' and Call_type='FIRCO'";
			 try {
				
				
				 Map<String,String> Columnvalues = new HashMap<String,String>(); 
				 for(int j=1;j<arrOfStr1.length;j++)
				 {
					 fircoDetails = "Q_"+DBOFircoTableName;
					 String sRecords=arrOfStr1[j].replace(": \n", ":"); 
					 sRecords=sRecords.replace(":\n", ":");
					 sRecords=sRecords.replace("'", "''"); // added to handle the PCUG 99 - firco impacted cases.
					 
					 if (sRecords.contains("Suspect detected")) {
						 BufferedReader bufReader = new BufferedReader(new StringReader(sRecords));
						 String line=null;
						 while((line=bufReader.readLine()) != null )
						 {
							 String[] PDFColumns = {"OFAC ID", "NAME", "MATCHINGTEXT", "ORIGIN", "DESIGNATION", "DATE OF BIRTH", "USER DATA 1", "NATIONALITY", "PASSPORT", "ADDITIONAL INFOS"};
							 for(int k=0;k<PDFColumns.length;k++)
							 {
								 if(line.contains(PDFColumns[k]+":"))
								 {
									 String colData = "";
									 String [] tmp = line.split(":");
									 //iRBLSysCheckIntegrationLog.iRBLSysCheckIntegrationLogger.debug("tmp.length : "+tmp.length+", line : "+line);
	
									 //********below loop added for handling hardcoded Fircosoft XML in offshore dev server
									 if(tmp.length == 1)
										 colData="";
									 else if(tmp[1].trim().equalsIgnoreCase("Synonyms") || tmp[1].trim().equalsIgnoreCase("none") || tmp[1].trim().equalsIgnoreCase(""))
										 colData="";
									 else
									 {
										 //colData=tmp[1].trim();
										 for(int m=1; m<tmp.length; m++)
										 {
											 colData=colData+" "+tmp[m].trim();
										 }
									 }
	
									 if("DATE OF BIRTH".equalsIgnoreCase(PDFColumns[k].trim()))
									 {
										 colData=colData.trim();
										 if(colData.length()==4)
											 colData="01-01-"+colData;
										 else if(colData.length()>10)
											 colData = colData.substring(0,10);
									 }	
	
									 Columnvalues.put(PDFColumns[k],colData);
									 WriteLog("Columnvalues " + Columnvalues);
									 WriteLog("colData " + colData);
									 WriteLog("PDFColumns " + PDFColumns[k]);
								 }
							 }
						 }
						String addinfo = Columnvalues.get("ADDITIONAL INFOS");
						addinfo=addinfo.replaceAll("'", "''");
						WriteLog("addinfo " + addinfo);
						
						String Name = Columnvalues.get("NAME");
						Name=Name.replaceAll("'", "''");
						WriteLog("Name " + Name);
						
						trTableColumn = "WI_name,U_ID,Additiona_info,Name,Date_of_birth,Designation,Matchingtext,Origin,Nationality,Passport,user_data_1";
						WriteLog("trTableColumn" + trTableColumn);
	
						trTableValue = "'" + wiName + "','" + Columnvalues.get("OFAC ID").toString().trim() + "'," + "'"
								+ addinfo + "','" + Name + "','" + Columnvalues.get("DATE OF BIRTH") + "'," + "'" + Columnvalues.get("DESIGNATION")
								+ "','" + Columnvalues.get("MATCHINGTEXT") + "','" + Columnvalues.get("ORIGIN") + "'," + "'" + Columnvalues.get("NATIONALITY") + "','" + Columnvalues.get("PASSPORT") + "','"
								+ Columnvalues.get("USER DATA 1")+"'" ;
				
						WriteLog("trTableValue " + trTableValue);
						String inputXML = getInputXMLInsert(DBOFircoTableName);
						WriteLog("APInsert Input History: " + inputXML);
						sOutputXML = executeAPI(inputXML);
						WriteLog("APInsert Output History: " + sOutputXML);
						xmlobj = new XMLParser(sOutputXML);
						
						checkCallsMainCode(xmlobj);
						
						
						fircoDetails = fircoDetails+"<WINAME>"+wiName+"</WINAME>";
						fircoDetails = fircoDetails+"<Sr_No>"++"</Sr_No>";
						fircoDetails = fircoDetails+"<Ofac_ID>"+Columnvalues.get("OFAC ID").toString().trim()+"</Ofac_ID>";
						fircoDetails = fircoDetails+"<CIF_ID>"+relatedPartyId+"</CIF_ID>";
						fircoDetails = fircoDetails+"<Reference_No>"++"</Reference_No>";
						fircoDetails = fircoDetails+"<Matching_Text>"++"</Matching_Text>";
						fircoDetails = fircoDetails+"<Name>"++"</Name>";
						fircoDetails = fircoDetails+"<Origin>"++"</Origin>";
						fircoDetails = fircoDetails+"<Designation>"++"</Designation>";
						fircoDetails = fircoDetails+"<Date_of_Birth>"++"</Date_of_Birth>";
						fircoDetails = fircoDetails+"<User_data1>"++"</User_data1>";
						fircoDetails = fircoDetails+"<Nationality>"++"</Nationality>";
						fircoDetails = fircoDetails+"<Passport>"++"</Passport>";
						fircoDetails = fircoDetails+"<AdditionalInfos>"++"</AdditionalInfos>";
						fircoDetails = fircoDetails+"<LPEP_FPEP>"++"</LPEP_FPEP>";


						Columnvalues.clear();
					 }
					 
					 fircoDetails = fircoDetails+"/Q_"+DBOFircoTableName;
				 }

			 } catch(Exception e){
				 e.printStackTrace();
				 WriteLog(" Exceptio After ng_FIRCO_InsertData: "+e.getMessage());
			 }
		 }
		 
		 return fircoDetails;*/
		
		 String fircoDetails = "";
		 String[] arrOfStr1 = null; 
		   if (FircoData.contains("Suspect detected #1"))
		   {						   	
				arrOfStr1 = FircoData.split("=============================");
				WriteLog("arrOfStr1.length: "+arrOfStr1.length);
				if(arrOfStr1.length==2)
				{
					WriteLog("No Records Found : ");
					return "";
				}
				else if(arrOfStr1.length>2)
				{
					try {
						int FIRCOSOFTGridsize = 0;
				    	String colNames = "";
				    	String colValues = "";
						
                      for(int i=1;i<arrOfStr1.length-1;i++)
						{
                    	  	fircoDetails = fircoDetails+"<Q_"+DBOFircoTableName+">";
                    	  	WriteLog("Firco sRecords arrOfStr1[i]: "+arrOfStr1[i]);
	                      	String sRecords=arrOfStr1[i].replace(": \n", ":"); 
	                      	sRecords=sRecords.replace(":\n", ":");
	                      	WriteLog("Firco sRecords: "+sRecords);
							Map<String,String> Columnvalues = new HashMap<String,String>(); 
							BufferedReader bufReader = new BufferedReader(new StringReader(sRecords));
							String line=null;
							while( (line=bufReader.readLine()) != null )
							{
								String[] PDFColumns = {"OFAC ID", "NAME", "MATCHINGTEXT", "ORIGIN", "DESIGNATION", "DATE OF BIRTH", "USER DATA 1", "NATIONALITY", "PASSPORT", "ADDITIONAL INFOS", "PEP-FEP"};
								for(int k=0;k<PDFColumns.length;k++)
								{
									if(line.contains(PDFColumns[k]+":"))
									{
										String colData = "";
										String [] tmp = line.split(":");
										//iRBLSysCheckIntegrationLog.iRBLSysCheckIntegrationLogger.debug("tmp.length : "+tmp.length+", line : "+line);
									
										//********below loop added for handling hardcoded Fircosoft XML in offshore dev server
										if(tmp.length == 1)
											colData="";//***************************
										else if(tmp[1].trim().equalsIgnoreCase("Synonyms") || tmp[1].trim().equalsIgnoreCase("none") || tmp[1].trim().equalsIgnoreCase(""))
											colData="";
										else
										{
											//colData=tmp[1].trim();
											for(int m=1; m<tmp.length; m++)
											{
												colData=colData+" "+tmp[m].trim();
											}
										}
										
										/*if("DATE OF BIRTH".equalsIgnoreCase(PDFColumns[k].trim()))
										{
											colData=colData.trim();
											try
											{
												if(colData.length()==4) // when received only one year like 1975
													colData="01-01-"+colData; 
												else if(colData.contains(" ") && colData.substring(0, colData.indexOf(" ")).length() == 4) // when received multiple year like 1975 1976
												{
													colData="01-01-"+colData.substring(0, colData.indexOf(" "));
												}
												else if(colData.length() == 10 && !colData.contains(" ")) // when only one valid date is received like 01-01-1975
													colData = colData;
												else if(colData.length()>10) // when multiple valid date is received like 01-01-1975 01-01-1976
													colData = colData.substring(0,10);
												else 
													colData = "";
												
												if(colData.contains(" "))
													colData = "";
											}
											catch (Exception e)
											{
												iRBLSysCheckIntegrationLog.iRBLSysCheckIntegrationLogger.debug("excep in parsing date:"+colData);
												colData = "";
											}
										}*/	
										
										Columnvalues.put(PDFColumns[k],colData);
										WriteLog("ColName: "+PDFColumns[k]+", ColData: "+colData);
									}
								}
							}									
							
							WriteLog("Firco Response pasrsing done successfully");
							
							String PEPValue = "NPEP";
							String sPEP = Columnvalues.get("PEP-FEP").toString().trim().replace("'", "").replace(",","");
							if("1 0".equalsIgnoreCase(sPEP))
								PEPValue = "LPEP";
							if("0 1".equalsIgnoreCase(sPEP) || "1 1".equalsIgnoreCase(sPEP))
								PEPValue = "FPEP";
							
							iFircoCount = iFircoCount+1;
							fircoDetails = fircoDetails+"<Sr_No>"+iFircoCount+"</Sr_No>";
							fircoDetails = fircoDetails+"<Ofac_ID>"+Columnvalues.get("OFAC ID").toString().trim().replace("'", "").replace(",","")+"</Ofac_ID>";
							fircoDetails = fircoDetails+"<CIF_ID>"+relatedPartyId+"</CIF_ID>";
							fircoDetails = fircoDetails+"<Reference_No>"+Columnvalues.get("OFAC ID").toString().trim().replace("'", "").replace(",","")+"</Reference_No>";
							fircoDetails = fircoDetails+"<Matching_Text>"+Columnvalues.get("MATCHINGTEXT").toString().trim().replace("'", "").replace(",","")+"</Matching_Text>";
							fircoDetails = fircoDetails+"<Name>"+Columnvalues.get("NAME").toString().trim().replace("'", "").replace(",","")+"</Name>";
							fircoDetails = fircoDetails+"<Origin>"+Columnvalues.get("ORIGIN").toString().trim().replace("'", "").replace(",","")+"</Origin>";
							fircoDetails = fircoDetails+"<Designation>"+Columnvalues.get("DESIGNATION").toString().trim().replace("'", "").replace(",","")+"</Designation>";
							fircoDetails = fircoDetails+"<Date_of_Birth>"+Columnvalues.get("DATE OF BIRTH").toString().trim().replace("'", "").replace(",","")+"</Date_of_Birth>";
							fircoDetails = fircoDetails+"<User_data1>"+Columnvalues.get("USER DATA 1").toString().trim().replace("'", "").replace(",","")+"</User_data1>";
							fircoDetails = fircoDetails+"<Nationality>"+Columnvalues.get("NATIONALITY").toString().trim().replace("'", "").replace(",","")+"</Nationality>";
							fircoDetails = fircoDetails+"<Passport>"+Columnvalues.get("PASSPORT").toString().trim().replace("'", "").replace(",","")+"</Passport>";
							fircoDetails = fircoDetails+"<AdditionalInfos>"+Columnvalues.get("ADDITIONAL INFOS").toString().trim().replace("'", "").replace(",","")+"</AdditionalInfos>";
							fircoDetails = fircoDetails+"<LPEP_FPEP>"+PEPValue+"</LPEP_FPEP>";

							fircoDetails = fircoDetails+"</Q_"+DBOFircoTableName+">";
							
							Columnvalues.clear();
							
							WriteLog("@@@@@@@@@@ for fircosoft detail call :::");
							
						}	
					}catch(Exception e)
					{
						WriteLog("Exception in parsing firco response");
						//objRespBean.setFircosoft_Details("Not Checked");									
						e.printStackTrace();
					}
				    
				}
	   	} else {
	   		WriteLog("No Records Found : ");
	   		//**************
	   		return "";
	   	}
		
		return fircoDetails;
	}
	
	private void duplicateWorkitemCheckBasedOnProspect(String prospect) throws Exception
	{
		String sQuery="select top 1 WINAME,CREATEDAT from RB_DBO_EXTTABLE with (nolock) where ProspectID='"+prospect+"' order by CREATEDAT desc";
		sInputXML=getAPSelectWithColumnNamesXML(sQuery);
		WriteLog("Input: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		WriteLog("Output: "+sOutputXML);
		xmlobj=new XMLParser(sOutputXML);
		//Check Main Code
		checkCallsMainCode(xmlobj); 
		WINAME=xmlobj.getValueOf("WINAME");
		String WI_create_date=xmlobj.getValueOf("CREATEDAT");
		WriteLog("Workitem number: "+WINAME);
		WriteLog("CREATEDAT of the request"+WI_create_date);
	}

	
}
