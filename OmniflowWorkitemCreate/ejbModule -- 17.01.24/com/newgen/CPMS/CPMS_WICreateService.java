package com.newgen.CPMS;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.omni.wf.util.excp.NGException;
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

public class CPMS_WICreateService extends CreateWorkitem {

	//CONSTANTS 
	final private String SUCCESS_STATUS="0";

	final private String WSR_PROCESS="USR_0_WSR_PROCESS";
	final private String WSR_ATTRDETAILS="USR_0_WSR_ATTRDETAILS";
	final private String WSR_DOCTYPEDETAILS="USR_0_WSR_DOCTYPEDETAILS";
	
	
	// for DBO
	final private String CPMS_WIHISTORY = "NG_CPMS_GR_DECISIONHISTORY";
	final private String txnTableName="RB_CPMS_TXNTABLE";
	final private String q_queueID = "";
	//For CDOB
	final private String CDOB_EXTTABLE="NG_CC_EXTTABLE";
	final private String Customer_Fragment_Table="ng_rlos_Customer";
	final private String CARD_PRODUCT="ng_rlos_IGR_Eligibility_CardProduct";
	final private String CARD_LIMIT="ng_rlos_IGR_Eligibility_CardLimit";

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
    
    static HashMap<String, String> hmExtMandCPMS = new HashMap();
    static HashMap<String, String> hmRptProcessIdCPMS = new HashMap();
    static HashMap<String, String> hmRptTransTableCPMS = new HashMap();
    static LinkedHashMap<String, HashMap<String, String>> hmRptAttrAndColCPMS = new LinkedHashMap();
    static LinkedHashMap<String, HashMap<String, String>> hmRptAttrAndMandCPMS = new LinkedHashMap();
    
  
    String InputMessage1 = "";
  // private static NGEjbClient ngEjbClient;
    
	public WICreateResponse wiCreate(WICreateRequest request,String attributeList[]) throws WICreateException { 
		
			try
			{
				WriteLog("inside wiCreate 1");
				//ngEjbClient = NGEjbClient.getSharedInstance();
				headerObjResponse=new EE_EAI_HEADER();
				//Load file configuration
				loadConfiguration();
				
				//Load ResourceBundle
				loadResourceBundle();
				
				//Fetching request parameters 
				fetchRequestParameters(request);
				
				//Validating Input Parameters and Check Attribute Name from USR_0_WSR_ATTRDETAILS
				validateRequestParameters(attributeList);
				if("CPMS".equalsIgnoreCase(sProcessName))
					validateRepetitiveRequestParametersCPMS();
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
				
				if ("CPMS".equalsIgnoreCase(sProcessName) && "Y".equalsIgnoreCase(sInitiateAlso))
            	{
            		insertIntoHistoryCPMS(); //Entry into history table   
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
				hmRptAttrAndColCPMS.clear();
				hmRptAttrAndMandCPMS.clear();
				hmExtMandCPMS.clear();
				hmRptProcessIdCPMS.clear();
				hmRptTransTableCPMS.clear();
			}
			return response;	
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
		sInputXML=sInputXML.replace("&", "&amp;");
		WriteLog("WFUploadWorkItemXML Input after replacing &: "+sInputXML);
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
	
	private void insertIntoHistoryCPMS() throws WICreateException, Exception {

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		System.out.println(dateFormat.format(cal.getTime())); //2014/08/06 16:00:22
		//trTableColumn = "winame,wsname,decision,actiondatetime,remarks,username,entrydatetime";
		//trTableColumn = "WI_NAME,WORKSTEP,DECISION,DATE_TIME,REMARKS,USER_d,Department";
		trTableColumn ="WI_Name,DATE_TIME,User_d,Workstep,Department,Decision,Remarks";
		WriteLog("trTableColumn dor DAC" + trTableColumn);
		trTableValue = "'"+ wiName + "','" + sDate + "','" + sUsername + "','Initiation','Initiation','Submit','Workitem created through webservice'";
		WriteLog("trTableValue for CPMS" + trTableValue);
		sInputXML = getAPInsertInputXML(CPMS_WIHISTORY,trTableColumn,trTableValue);
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
		sInputXML=getAPSelectWithColumnNamesXML("select QueueID from QUEUEDEFTABLE with(nolock) where ProcessName='"+sProcessName+"' and QueueName='CPMS_Create_Request'");
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
		}
     	*/
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
				//checkAttributeTable(attributeObj[i].getName(),attributeObj[i].getValue());
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
	private void checkAttributeTable(String attributeName, String attributeValue) throws WICreateException, Exception
	{
		//WriteLog("inside checkAttributeTable");
		String getExtTransQry= "";
		if(sProcessName.equalsIgnoreCase("RAOP") || sProcessName.equalsIgnoreCase("CMP") || sProcessName.equalsIgnoreCase("CBP") || sProcessName.equalsIgnoreCase("DAC") || sProcessName.equalsIgnoreCase("ACP") ) // for iBPS Processes
		{
			getExtTransQry="select ATTRIBUTENAME,EXTERNALTABLECOLNAME, TRANSACTIONTABLECOLNAME, ISMANDATORY,ATTRIBUTE_FORMAT,ATTRIBUTE_TYPE from "+WSR_ATTRDETAILS+" with(nolock) where ATTRIBUTENAME='"+attributeName+"' and PROCESSID='"+processID+"'";
		}
		else if (sProcessName.equalsIgnoreCase("Digital_CC"))
		{
			getExtTransQry="select ATTRIBUTENAME,EXTERNALTABLECOLNAME, TRANSACTIONTABLECOLNAME, ISMANDATORY from "+WSR_ATTRDETAILS+" with(nolock) where ATTRIBUTENAME='"+attributeName+"' and PROCESSID='"+processID+"' AND ISACTIVE='Y'";
		}else // for BPM Processes
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
		
		if((sProcessName.equalsIgnoreCase("OECD")) && (attributeName.equalsIgnoreCase("CIFTYPE")) &&  !(attributeValue.equalsIgnoreCase("")))
		{
		//WriteLog("CIFtype------" +attributeValue);	
			if(attributeValue.equalsIgnoreCase("R"))
			{
				attributeValue = "Retail";	
			}
			else
				attributeValue = "Corporate";	
		}
		
		//Added by Nikita for the checking length of the attribute value at 14062019
			
		if(sProcessName.equalsIgnoreCase("RAOP") || sProcessName.equalsIgnoreCase("CMP") || sProcessName.equalsIgnoreCase("CBP") || sProcessName.equalsIgnoreCase("DAC")  || sProcessName.equalsIgnoreCase("ACP"))
		{
			String strattributesize=getTagValues(sOutputXML, "ATTRIBUTE_FORMAT");
			String attType = getTagValues(sOutputXML, "ATTRIBUTE_TYPE");
			attributeValue=attributeValue.trim();
			if(!(attributeValue.equalsIgnoreCase("")))
			{
				int attributelength = attributeValue.length();
				//WriteLog("attributelength--"+attributelength);
				//WriteLog("attNameFromTable--"+attNameFromTable);
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
		//Changes by Sajan for FALCON DOB
		if(!attNameFromTable.equalsIgnoreCase(""))
		{
			WriteLog("Attribute Name: "+attributeName);
			if(((isMandatory.equalsIgnoreCase("Y") || isMandatory.equalsIgnoreCase("N")) && !attributeValue.equalsIgnoreCase("")) || (isMandatory.equalsIgnoreCase("N") && attributeValue.equalsIgnoreCase("")))
			{
				if (!extFlag.equalsIgnoreCase("") && transFlag.equalsIgnoreCase(""))
				{
					attributeTag=attributeTag+extFlag+(char)21+attributeValue+(char)25;
				}
				else if(extFlag.equalsIgnoreCase("") && !transFlag.equalsIgnoreCase(""))
				{
					trTableColumn=trTableColumn+transFlag+",";
					trTableValue=trTableValue+"'"+URLEncoder.encode(attributeValue, "UTF-8")+"'"+",";
				}
				else if(!extFlag.equalsIgnoreCase("") && !transFlag.equalsIgnoreCase(""))
				{
					attributeTag=attributeTag+extFlag+(char)21+attributeValue+(char)25;
					trTableColumn=trTableColumn+transFlag+",";
					trTableValue=trTableValue+"'"+URLEncoder.encode(attributeValue, "UTF-8")+"'"+",";
				}
				else if(extFlag.equalsIgnoreCase("") && transFlag.equalsIgnoreCase(""))
				{
					WriteLog("Both Flag N");
					
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
		
		getExtTransQry="select ATTRIBUTENAME, isnull(nullif(EXTERNALTABLECOLNAME,''),'#') as EXTERNALTABLECOLNAME,isnull(nullif(TRANSACTIONTABLECOLNAME,''),'#') as TRANSACTIONTABLECOLNAME, ISMANDATORY from "+WSR_ATTRDETAILS+" with(nolock) where ATTRIBUTENAME in ("+attributeNames+") and PROCESSID='"+processID+"'";
		
		sInputXML=getAPSelectWithColumnNamesXML(getExtTransQry);
		WriteLog("Input XML: "+sInputXML);
		sOutputXML=executeAPI(sInputXML);
		xmlobj=new XMLParser(sOutputXML);
		WriteLog(" "+sOutputXML);
		checkCallsMainCode(xmlobj);
		String attNameFromTable=getTagValues(sOutputXML, "ATTRIBUTENAME");
		String extFlag=getTagValues(sOutputXML, "EXTERNALTABLECOLNAME");
		String isMandatory=getTagValues(sOutputXML, "ISMANDATORY");
		String txnColumnName=getTagValues(sOutputXML, "TRANSACTIONTABLECOLNAME");
		WriteLog("txnColumnName1086:-"+txnColumnName);
		WriteLog("exttablecolumn1086:-"+extFlag);
		String AttrTagsName []=attNameFromTable.split("`");
		String ExtColsName []=extFlag.split("`");
		String MandateList []=isMandatory.split("`");
		String txnColsName []=txnColumnName.split("`");
		WriteLog("AttrTagsName.length:-"+AttrTagsName.length);
		WriteLog("MandateList.length:-"+MandateList.length);
		WriteLog("ExtColsName.length:-"+ExtColsName.length);
		WriteLog("txnColsName.length:-"+txnColsName.length);
		for (String name: hmt.keySet()){
			String attrtag = name.toString();
			String flg = "N";
			//WriteLog("Attr:"+attNameFromTable);
			for(int i=0;i<AttrTagsName.length;i++)
			{	//WriteLog("Attr111:"+AttrTagsName[i]);
				if(AttrTagsName[i].equalsIgnoreCase(attrtag))
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
		
		for(int i=0;i<AttrTagsName.length;i++)
		{
			String attributeValue = hmt.get(AttrTagsName[i]);
			WriteLog("attribute value:-"+attributeValue);
			//WriteLog("attributeValue:-"+i+attributeValue+"\nattributeTag"+attributeTag);
			if(((MandateList[i].equalsIgnoreCase("Y") || MandateList[i].equalsIgnoreCase("N")) 
					&& !attributeValue.equalsIgnoreCase("")) || (MandateList[i].equalsIgnoreCase("N") && attributeValue.equalsIgnoreCase("")))
			{
				//WriteLog("extn table column name:-"+ExtColsName[i]);
				//WriteLog("txn table column name:-"+txnColsName[i]);
				
				if (!ExtColsName[i].equalsIgnoreCase("")&&!ExtColsName[i].equalsIgnoreCase("#"))
				{
					WriteLog("this is a extn table attribute:-"+AttrTagsName[i]);
					attributeTag=attributeTag+"\n<"+ExtColsName[i]+">"+attributeValue+"</"+ExtColsName[i]+">";
					//WriteLog("attributeTag:-"+attributeTag);
				}
				else if(!txnColsName[i].equalsIgnoreCase("")&&!txnColsName[i].equalsIgnoreCase("#"))
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
			else if(MandateList[i].equalsIgnoreCase("Y") && attributeValue.equalsIgnoreCase(""))
			{
				WriteLog("some error occured for attribute2:-"+AttrTagsName[i]);
				 throw new WICreateException("1016",pCodes.getString("1016")+" :"+AttrTagsName[i]);
			}
		}
		attributeTag+="<CreationChannel>Online</CreationChannel>";
		attributeTag+="<Channel>"+sRequestorChannelId+"</Channel>";
		//WriteLog("Final txn table attributeTag:-"+txnTableAttributes);
		txnTableAttributes=txnTableAttributes+"\n</Q_"+txnTableName+">";
		//WriteLog("txn table:-"+txnTableAttributes);
		//attributeTag=attributeTag+txnTableAttributes;
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
		if("CPMS".equalsIgnoreCase(sProcessName)){
			checkMandatoryAttributeDBO();
		}
		
		Date d= new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sDate = dateFormat.format(d);
		
		if("CPMS".equalsIgnoreCase(sProcessName)){
			checkExtTableAttrIBPS(hm);
		}
		
		getTableName();
	}
	private void checkMandatoryAttributeDBO() throws WICreateException, Exception
	{
		WriteLog("inside checkMandatoryAttributeDBO");
		String attributeList [];
		if(hmExtMandCPMS.size() == 0)
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
					hmExtMandCPMS.put(attributeList[i], "");
				}
			}	
		}
		else 
		{
			String AttrTagName = "";
			for (String name: hmExtMandCPMS.keySet()){
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
	
	private void validateRepetitiveRequestParametersCPMS() throws WICreateException, Exception
	{
		String repetitiveListMain[];
		String repetitiveList[];
		
		RepetitiveMainTags = checkMandatoryRepetitiveTagsCPMS();
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
			        			String RepetitiveProcessID = hmRptProcessIdCPMS.get(attributeList[i]);
			        			checkMandatoryRepetitiveAttributeCPMS(RepetitiveProcessID,"Y",hm1);
				        		hmMain.put(attributeList[i]+"-"+Integer.toString(j), hm1);
				        	}
			        	}
					}
				}
			}
		}	
	}
	private String checkMandatoryRepetitiveTagsCPMS() throws WICreateException, Exception
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
			
			    hmRptProcessIdCPMS.put(RepetitiveList[i], RepProcessId[i]);
			    hmRptTransTableCPMS.put(RepProcessId[i], RepTransTable[i]);
			}
		}
		return RepetitiveMainTags;
	}
	

	
	private String checkMandatoryRepetitiveAttributeCPMS(String RepetitiveProcessId,String isValidatingMandate, HashMap<String, String> RepetitiveReqAttr) throws WICreateException, Exception
	{
		WriteLog("inside checkMandatoryRepetitiveAttributeIBPS");
		String AttrTagName = "";
		String TransColName = "";
		String isMandatory = "";
		if(!hmRptAttrAndColCPMS.containsKey(RepetitiveProcessId))
		{
			String sInputXML=getAPSelectWithColumnNamesXML("select ATTRIBUTENAME,TRANSACTIONTABLECOLNAME,ISMANDATORY from USR_0_WSR_ATTRDETAILS_REPETITIVE with(nolock) where PROCESSID='"+RepetitiveProcessId+"' and ISACTIVE='Y' order by AttributeName");
			WriteLog("Input XML: "+sInputXML);
			String sOutputXML=executeAPI(sInputXML);
			WriteLog("Output XML: "+sOutputXML);
			xmlobj=new XMLParser(sOutputXML);
			checkCallsMainCode(xmlobj);
			AttrTagName = getTagValues(sOutputXML,"ATTRIBUTENAME");
			TransColName = getTagValues(sOutputXML,"TRANSACTIONTABLECOLNAME");
			isMandatory = getTagValues(sOutputXML,"ISMANDATORY");
		} 
		else
		{
			HashMap<String, String> hmAttr = new HashMap();
			hmAttr = hmRptAttrAndColCPMS.get(RepetitiveProcessId);
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
			hmMand = hmRptAttrAndMandCPMS.get(RepetitiveProcessId);
			for (String name1: hmMand.keySet()){
				if(isMandatory.equalsIgnoreCase(""))
					isMandatory = hmMand.get(name1).toString(); 
				else
					isMandatory = isMandatory+"`"+hmMand.get(name1).toString();
			}
		}
		
		if(isValidatingMandate.equalsIgnoreCase("Y"))
		{	
			String AttrTagsName []=AttrTagName.split("`");
			String TransColsName []=TransColName.split("`");
			String MandateList []=isMandatory.split("`");
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
				}
			}
			else
			{
				throw new WICreateException("1021",pCodes.getString("1021")+" for process id: "+processID);
			}
			
			
			if(!hmRptAttrAndColCPMS.containsKey(RepetitiveProcessId))
			{
				if(AttrTagsName.length>0)
				{
					HashMap<String, String> hmtmp = new HashMap();
					for(int i=0;i<AttrTagsName.length;i++)
					{
						hmtmp.put(AttrTagsName[i], TransColsName[i]);
					}
					hmRptAttrAndColCPMS.put(RepetitiveProcessId, hmtmp);
					
					HashMap<String, String> hmtmp1 = new HashMap();
					for(int i=0;i<AttrTagsName.length;i++)
					{
						hmtmp1.put(AttrTagsName[i], MandateList[i]);
					}
					hmRptAttrAndMandCPMS.put(RepetitiveProcessId, hmtmp1);
				}	
			}
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
	            String RepetitiveProcessID = hmRptProcessIdCPMS.get(RepetitiveTags[0]);
				String RepetitiveTagTableName=hmRptTransTableCPMS.get(RepetitiveProcessID);
				HashMap<String, String> hm1 = new HashMap(); 
				hm1 = hmMain.get(key);
				//WriteLog(key+" hm1 size: "+hm1.size());
				
				HashMap<String, String> hmtmp = new HashMap();
				hmtmp = hmRptAttrAndColCPMS.get(RepetitiveProcessID);
				String AttrList = "";
				String TransColList = "";
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
			String RepetitiveTagsAttribute="";
			for (String name: hmMain.keySet()){
	            String key = name.toString();
	            String keyvalue = hmMain.get(name).toString();  
	            //WriteLog("hmMain: "+key + " " + keyvalue);  
	            String RepetitiveTags [] = key.split("-");
	            String RepetitiveProcessID = hmRptProcessIdCPMS.get(RepetitiveTags[0]);
				String RepetitiveTagTableName=hmRptTransTableCPMS.get(RepetitiveProcessID);
				if("".equalsIgnoreCase(RepetitiveTagsAttribute))
				{
					RepetitiveTagsAttribute="\n<Q_"+RepetitiveTagTableName+">";
				}
				else
				{
					RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<Q_"+RepetitiveTagTableName+">";
				}
				HashMap<String, String> hm1 = new HashMap(); 
				hm1 = hmMain.get(key);
				HashMap<String, String> hmtmp = new HashMap();
				hmtmp = hmRptAttrAndColCPMS.get(RepetitiveProcessID);
				String AttrList = "";
				String TransColList = "";
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
								RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n<"+TransColListArr[j]+">"+value+"</"+TransColListArr[j]+">";
							}
						}
					}
				}
				RepetitiveTagsAttribute=RepetitiveTagsAttribute+"\n</Q_"+RepetitiveTagTableName+">";
				
			}	
			return	RepetitiveTagsAttribute;
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
			}
		}
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
	
	public static String parseFircoDetails( String FircoData)
	{
		try
		{
			
		}
		catch(Exception e)
		{
			WriteLog("Exception in parsing Firco details:-" +e.toString());
		}
		return "";
	}


}
