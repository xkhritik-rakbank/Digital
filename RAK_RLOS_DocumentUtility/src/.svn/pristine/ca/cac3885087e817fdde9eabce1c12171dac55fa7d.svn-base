/*
---------------------------------------------------------------------------------------------------------
                  NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group                   : Application - Projects
Project/Product			: CAS
Application				: CAS Document Utility
Module					: Falcon Document
File Name				: FalconDocument.java
Author 					: Sajan
Date (DD/MM/YYYY)		: 05/12/2019

---------------------------------------------------------------------------------------------------------
                 	CHANGE HISTORY
---------------------------------------------------------------------------------------------------------

Problem No/CR No        Change Date           Changed By             Change Description
---------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------
*/


package com.newgen.Falcon.ReadCourierFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.newgen.Falcon.Dispatch.WorkItem;
import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;

public class CourierFile implements Runnable{	
	
	private static  String cabinetName;
	private static  String jtsIP;
	private static  String jtsPort;
	private static  String smsPort;
	private  String [] attributeNames;
	private static String ExternalTable;
	private static String destFilePath;
	private static String ErrorFolder;
	private static String volumeID;
	private static String MaxNoOfTries;
	private static int TimeIntervalBetweenTrialsInMin;
	private  String queueID;
	private  String workItemName;
	private  String parentFolderIndex;
	static String lastWorkItemId = "";
	static String lastProcessInstanceId = "";
	private  int mainCode;
	Date now=null;
	public static String sdate="";
	public static String source=null;
	public static String dest=null;
	public static String TimeStamp="";
	public static String newFilename=null;
	private static String sessionId;
	public static int sessionCheckInt=0;
	public static int waitLoop=50;
	public static int loopCount=50;
	private String strCardProduct="";
	private static final String TABLE_NAME="NG_RLOS_COURIER_DETAILS";
	private static final String EXT_TABLE_NAME="NG_DOB_EXTTABLE";
	private static final String COL_NAMES="Tracking_Number,Referenec_Number,ECRN,Content_Type," +
			"Date_Picked,Status,Logistic_Status,Mobile_No,Customer_Address,Customer_Name,courier_WiName";
	private static String COL_VALUES="";
	
	static Map<String, String> falconDocumentCofigParamMap= new HashMap<String, String>();
	private Map <String, String> executeXMLMapMethod = new HashMap<String, String>();
	private static NGEjbClient ngEjbClientFalconDocument;
	
	public void run()
	{
		int sleepIntervalInMin=3;
		try
		{
			FalconCourierFileLog.setLogger();
			ngEjbClientFalconDocument = NGEjbClient.getSharedInstance();

			FalconCourierFileLog.mLogger.debug("Connecting to Cabinet.");

			int configReadStatus = readConfig();

			FalconCourierFileLog.mLogger.debug("configReadStatus "+configReadStatus);
			if(configReadStatus !=0)
			{
				FalconCourierFileLog.mLogger.error("Could not Read Config Properties [CMPDocument]");
				return;
			}

			cabinetName = CommonConnection.getCabinetName();
			FalconCourierFileLog.mLogger.debug("Cabinet Name: " + cabinetName);

			jtsIP = CommonConnection.getJTSIP();
			FalconCourierFileLog.mLogger.debug("JTSIP: " + jtsIP);

			jtsPort = CommonConnection.getJTSPort();
			FalconCourierFileLog.mLogger.debug("JTSPORT: " + jtsPort);

			smsPort = CommonConnection.getsSMSPort();
			FalconCourierFileLog.mLogger.debug("SMSPort: " + smsPort);			

			/*sleepIntervalInMin=Integer.parseInt(falconDocumentCofigParamMap.get("SleepIntervalInMin"));
			FalconCourierFileLog.mLogger.debug("SleepIntervalInMin: "+sleepIntervalInMin);

			attributeNames=falconDocumentCofigParamMap.get("AttributeNames").split(",");
			FalconCourierFileLog.mLogger.debug("AttributeNames: " + attributeNames);

			ExternalTable=falconDocumentCofigParamMap.get("ExtTableName");
			FalconCourierFileLog.mLogger.debug("ExternalTable: " + ExternalTable);

			destFilePath=falconDocumentCofigParamMap.get("destFilePath");
			FalconCourierFileLog.mLogger.debug("destFilePath: " + destFilePath);

			ErrorFolder=falconDocumentCofigParamMap.get("failDestFilePath");
			FalconCourierFileLog.mLogger.debug("ErrorFolder: " + ErrorFolder);

			volumeID=falconDocumentCofigParamMap.get("VolumeID");
			FalconCourierFileLog.mLogger.debug("VolumeID: " + volumeID);

			MaxNoOfTries=falconDocumentCofigParamMap.get("MaxNoOfTries");   //Not getting used anywhere Sajan
			FalconCourierFileLog.mLogger.debug("MaxNoOfTries: " + MaxNoOfTries);

			TimeIntervalBetweenTrialsInMin=Integer.parseInt(falconDocumentCofigParamMap.get("TimeIntervalBetweenTrialsInMin"));
			FalconCourierFileLog.mLogger.debug("TimeIntervalBetweenTrialsInMin: " + TimeIntervalBetweenTrialsInMin);*/
			
			sessionId = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);
			if(sessionId.trim().equalsIgnoreCase(""))
			{
				FalconCourierFileLog.mLogger.debug("Could Not Connect to Server!");
			}
			else
			{
				FalconCourierFileLog.mLogger.debug("Session ID found: " + sessionId);
				while(true)
				{
					startFalconCourierUtility();
					System.out.println("No More workitems to Process, Sleeping!");
					Thread.sleep(sleepIntervalInMin*60*1000);
				}
			}
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			try{
				FalconCourierFileLog.mLogger.error("Exception Occurred in FALCON Document Thread: "+e+" Now runnning the utility again");
				Thread.sleep(1000);
				startFalconCourierUtility();
			}
			catch(Exception ex){
				e.printStackTrace();
			}
			
		}
	}
	
	private int readConfig()
	{
		Properties p = null;
		try 
		{
			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "ConfigFiles"+ File.separator+ "Falcon_Document_Config.properties")));
			Enumeration<?> names = p.propertyNames();
			while (names.hasMoreElements())
			{
			    String name = (String) names.nextElement();
			    falconDocumentCofigParamMap.put(name, p.getProperty(name));
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception in Read INI: "+ e.getMessage());
			FalconCourierFileLog.mLogger.error("Exception has occured while loading properties file "+e.getMessage());
			return -1 ;			
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	private void startFalconCourierUtility() throws Exception
	{
		FalconCourierFileLog.mLogger.info("ProcessWI function for Falcon Courier Utility started");

		String sOutputXml="";
		String sMappedInputXml="";
		long lLngFileSize = 0L;
		String lstrDocFileSize = "";
		String decisionToUpdate="";
		String statusXML="";
		String strfullFileName="";
		String strDocumentName="";
		String strExtension="";
		String DocumentType="";
		String FilePath="";
		boolean catchflag=false;

		sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);

		if(sessionId==null || sessionId.equalsIgnoreCase("") || sessionId.equalsIgnoreCase("null"))
		{
			FalconCourierFileLog.mLogger.error("Could Not Get Session ID "+sessionId);
			return;
		}
		File inputFolder=new File(".\\CourierFile\\input");
		File listFiles[]=inputFolder.listFiles();
		for(File file:listFiles){
			String sFileName = inputFolder+"\\"+file.getName();
			FileInputStream fis = new FileInputStream(sFileName);
			XSSFWorkbook wb = new XSSFWorkbook(sFileName); //or new XSSFWorkbook("/somepath/test.xls")
			XSSFSheet sheet = wb.getSheetAt(0);
			Row myRow=null;
			String strTrackingNo="";
			String strRefNo="";
			String strECRN="";
			String strContentType="";
			String strDatePicked="";
			String strStatus="";
			String strLogisticsStatus="";
			String strMobileNo="";
			String strCustomerAddress="";
			String strCustomerName="";
			for(int k=1;k<100;k++)
			{
				try
				{
					myRow=sheet.getRow(k);
					strTrackingNo=myRow.getCell(0).toString().trim();
					strRefNo=myRow.getCell(1).toString().trim();
					strECRN=myRow.getCell(2).toString().trim();
					strContentType=myRow.getCell(3).toString().trim();
					strDatePicked=myRow.getCell(4).toString().trim();
					strStatus=myRow.getCell(5).toString().trim();
					strLogisticsStatus=myRow.getCell(6).toString().trim();
					strMobileNo=myRow.getCell(7).toString().trim();
					strCustomerAddress=myRow.getCell(8).toString().trim();
					strCustomerName=myRow.getCell(9).toString().trim();
					
					String strWorkitemNumber=getWorkitemNumberFromECRN(strECRN);
					
					
					COL_VALUES="'"+strTrackingNo+"','"+strRefNo+"','"+strECRN+"','"+strContentType+"','"+strDatePicked+"'," +
							"'"+strStatus+"','"+strLogisticsStatus+"','"+strMobileNo+"','"+strCustomerAddress+"','"+strCustomerName+"','"+strWorkitemNumber+"'";
					
					insertRowInTable();
					
					updateExternalTable(strWorkitemNumber,strTrackingNo);
				}
				catch(Exception e){
					FalconCourierFileLog.mLogger.info("Exception in reading xls "+e.getMessage());
					break;
				}
			}
			fis.close();
			wb.close();
			archiveFile(sFileName);
		}
		
		FalconCourierFileLog.mLogger.info("exiting ProcessWI function FALCON Document Utility");
	}
	
	private String getFinalLimit(String workItemNumber) throws IOException,Exception{
		String strQuery="select FINAL_LIMIT from ng_rlos_EligAndProdInfo with (nolock) where wi_name='"+workItemNumber+"'";
		String sInput=CommonMethods.apSelectWithColumnNames(strQuery, cabinetName, sessionId);
		FalconCourierFileLog.mLogger.info("Input XML to get Final limit is "+sInput);
		
		String strOutput=WFNGExecute(sInput, jtsIP, Integer.parseInt(jtsPort), 1);
		FalconCourierFileLog.mLogger.info("Outut XML to get final limit is  "+sInput);
		
		XMLParser xmlParser=new XMLParser(strOutput);
		String strFinalLimit=xmlParser.getValueOf("FINAL_LIMIT");
		return strFinalLimit;
	}
	
	private void archiveFile(String sFileName) throws Exception{
		String timestamp=get_timestamp();
		Move(".\\CourierFile\\arhived", sFileName, timestamp,true);
		startFalconDispatchUtility();
	}
	
	
	private void startFalconDispatchUtility() throws Exception
	{
		FalconCourierFileLog.mLogger.info("ProcessWI function for Falcon Document Utility started");

		String sOutputXml="";
		String sMappedInputXml="";
		long lLngFileSize = 0L;
		String lstrDocFileSize = "";
		String decisionToUpdate="";
		String statusXML="";
		String strfullFileName="";
		String strDocumentName="";
		String strExtension="";
		String DocumentType="";
		String FilePath="";
		boolean catchflag=false;

		sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);

		if(sessionId==null || sessionId.equalsIgnoreCase("") || sessionId.equalsIgnoreCase("null"))
		{
			FalconCourierFileLog.mLogger.error("Could Not Get Session ID "+sessionId);
			return;
		}

		List<String> wiList = new ArrayList<String>();
		try
		{
			//queueID = falconDocumentCofigParamMap.get("QueueID");
			//FalconCourierFileLog.mLogger.debug("QueueID: " + queueID);
			wiList = loadWorkItems();
		}
		catch (NumberFormatException e1)
		{
			catchflag=true;
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			catchflag=true;
			e1.printStackTrace();
		}
		catch (Exception e1)
		{
			catchflag=true;
			e1.printStackTrace();
		}
		DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
	    Calendar cal = Calendar.getInstance();
	    String sDate = dateFormat.format(cal.getTime());
	    
		

		   
	    
	    
		if (wiList.size()>0)
		{
			String outputFileName = "FALCON_CDP_DAILY_INC_" + sDate + ".txt";
			String sFilePath=".\\CasToCbsFile\\";
			 File fDumpFolder = new File(sFilePath);
		    if (!(fDumpFolder.exists()))
		      fDumpFolder.mkdirs();
		    PrintWriter writer = new PrintWriter(sFilePath+outputFileName, "UTF-8");
		    FalconCourierFileLog.mLogger.debug("after printwriter:");
		    FalconCourierFileLog.mLogger.debug("File Path Auto:"+sFilePath);
			 writer.println("H|" + sDate + "|FALCON_CDP_DAILY_INC|" + outputFileName);
			 FalconCourierFileLog.mLogger.debug("after writer.println::");
			for (String wi : wiList)
			{
				String QueryString = "Select a.AWB_NUMBER,a.CC_WI_NAME,a.PORTAL_REF_NUMBER,b.EMIRATEID,b.CUSTOMERNAME,b.MOBILENO from " +
						"(select AWB_NUMBER,CC_WI_NAME,PORTAL_REF_NUMBER from NG_DOB_EXTTABLE with (nolock) where CC_Wi_Name='"+wi+"') a " +
						"INNER JOIN (Select EmirateID,FirstName+' '+ISNULL(MiddleName,'')+' '+LAstName as CustomerName,MobileNo,wi_name  from ng_RLOS_Customer with (nolock)) b " +
						"on a.CC_WI_NAME=b.wi_name";

				String sInputXML =CommonMethods.apSelectWithColumnNames(QueryString, cabinetName, sessionId);

				FalconCourierFileLog.mLogger.debug("APSelect Inputxml to fetch WI data is: "+sInputXML);

				String sOutputXML=WFNGExecute(sInputXML,jtsIP,Integer.parseInt(jtsPort),1);
				FalconCourierFileLog.mLogger.debug("APSelect OutputXML to fetch WI data is : "+sOutputXML);
				XMLParser sXMLParser= new XMLParser(sOutputXML);
			    String sMainCode = sXMLParser.getValueOf("MainCode");
			    String strAWBNo="";
			    String strWIName="";
			    String strPortalRefNo="";
			    String strEmirateId="";
			    String strCustomerName="";
			    String strMobileNo="";
			    if("0".equals(sMainCode)){
			    	strAWBNo=sXMLParser.getValueOf("AWB_NUMBER");
			    	strWIName=sXMLParser.getValueOf("CC_WI_NAME");
			    	strPortalRefNo=sXMLParser.getValueOf("PORTAL_REF_NUMBER");
			    	strEmirateId=sXMLParser.getValueOf("EMIRATEID");
			    	strCustomerName=sXMLParser.getValueOf("CUSTOMERNAME");
			    	strMobileNo=sXMLParser.getValueOf("MOBILENO");
			    	String strFinalLimit=getFinalLimit(strWIName);
			    	FalconCourierFileLog.mLogger.info("B|" + strWIName + "|" + strPortalRefNo+ "|" + strAWBNo + "|" + strEmirateId + "|" + strCustomerName+"|"+strMobileNo);
			    	if(Integer.parseInt(strFinalLimit)==1)
			    		writer.println("B|" + strWIName + "|" + strPortalRefNo+ "|" + strAWBNo + "|" + strEmirateId + "|" + strCustomerName+"|"+strMobileNo+"|"+strFinalLimit+"|Please collect 4 Months Bank Statement");
			    	else
			    		writer.println("B|" + strWIName + "|" + strPortalRefNo+ "|" + strAWBNo + "|" + strEmirateId + "|" + strCustomerName+"|"+strMobileNo+"|"+strFinalLimit+"|");
			    }
			    updateFlagInExttable(wi);
			}
			writer.println("T|" + wiList.size());
		    writer.close();
		    Thread.sleep(5000);
		}
		
		FalconCourierFileLog.mLogger.info("exiting ProcessWI function FALCON Document Utility");
	}
	
	private List loadWorkItems() throws Exception
	{
		FalconCourierFileLog.mLogger.info("Starting loadWorkitem function");
		List workItemList = null;
		String workItemListInputXML="";
		String QueryString = "SELECT CC_WI_NAME from NG_DOB_EXTTABLE WHERE isDispatchFileDone='N' AND CC_Wi_Name IS NOT NULL";

		String sInputXML =CommonMethods.apSelectWithColumnNames(QueryString, cabinetName, sessionId);

		FalconCourierFileLog.mLogger.debug("APSelect Inputxml: "+sInputXML);

		String sOutputXML=WFNGExecute(sInputXML,jtsIP,Integer.parseInt(jtsPort),1);
		FalconCourierFileLog.mLogger.debug("APSelect OutputXML: "+sOutputXML);
		XMLParser sXMLParser= new XMLParser(sOutputXML);
	    String sMainCode = sXMLParser.getValueOf("MainCode");
		List<String> wList=new ArrayList<String>();
		int sTotalRecords = Integer.parseInt(sXMLParser.getValueOf("TotalRetrieved"));
		FalconCourierFileLog.mLogger.debug("STotalRecords: "+sTotalRecords);
		String strWINAME="";
		if (sMainCode.equals("0") && sTotalRecords > 0)
		{
			for(int i=0;i<sTotalRecords;i++)
			{
				String sXMLData=sXMLParser.getNextValueOf("Record");
				sXMLData =sXMLData.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");

        		XMLParser subXMLParser = new XMLParser(sXMLData);
        		strWINAME=subXMLParser.getValueOf("CC_WI_NAME");
        		wList.add(strWINAME);
			}
		}
		return wList;
		
	}
	
	
	private void updateFlagInExttable(String WorkitemName)throws IOException,Exception{
		String strInputXml=CommonMethods.apUpdateInput(cabinetName, sessionId, "NG_DOB_EXTTABLE", "isDispatchFileDone", "'Y'", "CC_Wi_Name='"+WorkitemName+"'");
		FalconCourierFileLog.mLogger.info("Input XML to Upadte flag in table is : Generate File "+strInputXml);
		String sOutputXML=WFNGExecute(strInputXml,jtsIP,Integer.parseInt(jtsPort),1);
		FalconCourierFileLog.mLogger.debug("APSelect OutputXML to fetch WI data is : "+sOutputXML);
		
		
	}
	
	
	private void updateExternalTable(String WorkitemNumber,String strAWB) throws IOException,Exception{
		String sInput=CommonMethods.apUpdateInput(cabinetName, sessionId, EXT_TABLE_NAME, "isDispatchFileDone,AWB_NUMBER", "'N','"+strAWB+"'", "CC_WI_NAME='"+WorkitemNumber+"'");
		FalconCourierFileLog.mLogger.info("Input XML to update Flag in external table is "+sInput);
		String sOutput=WFNGExecute(sInput, jtsIP, Integer.parseInt(jtsPort), 1);
		FalconCourierFileLog.mLogger.info("outpput XML for external table update is "+sOutput);
	}
	private String getWorkitemNumberFromECRN(String ECRN) throws IOException,Exception{
		
		String strQuery="SELECT CREATIONGRID_WINAME from NG_RLOS_gr_CCCreation where ECRN='"+ECRN+"'";
		String sInput=CommonMethods.apSelectWithColumnNames(strQuery, cabinetName, sessionId);
		
		String strOutput=WFNGExecute(sInput, jtsIP, Integer.parseInt(jtsPort), 1);
		FalconCourierFileLog.mLogger.info("Outut XML to get WI from ECRN is "+sInput);
		
		XMLParser xmlParser=new XMLParser(strOutput);
		String WIName=xmlParser.getValueOf("CREATIONGRID_WINAME");
		return WIName;
	}
	
	private void insertRowInTable() throws IOException,Exception{
		String strInput=CommonMethods.apInsert(cabinetName, sessionId, COL_NAMES, COL_VALUES, TABLE_NAME);
		FalconCourierFileLog.mLogger.info("Input XML for insert courie details is "+strInput);
		String strOutputXML=WFNGExecute(strInput, jtsIP, Integer.parseInt(jtsPort), 1);
		FalconCourierFileLog.mLogger.info("Output XML to insert data in table");
	}
	
	private void doneWorkItem(String wi_name,String values,Boolean... compeletFlag)
	{
		assert compeletFlag.length <= 1;
		sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);
		try
		{
			executeXMLMapMethod.clear();
			sessionCheckInt=0;
			while(sessionCheckInt<loopCount)
			{
				executeXMLMapMethod.put("getWorkItemInputXML",CommonMethods.getWorkItemInput(cabinetName,sessionId,wi_name, "1"));
				try
				{
					executeXMLMapMethod.put("getWorkItemOutputXML",WFNGExecute((String)executeXMLMapMethod.get("getWorkItemInputXML"),jtsIP,Integer.parseInt(jtsPort),1));
				}
				catch(Exception e)
				{
					FalconCourierFileLog.mLogger.error("Exception in Execute : " + e);
					sessionCheckInt++;
					waiteloopExecute(waitLoop);
					sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);
					continue;
				}
				sessionCheckInt++;
				if (CommonMethods.getTagValues((String)executeXMLMapMethod.get("getWorkItemOutputXML"),"MainCode").equalsIgnoreCase("11"))
				{
					sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);

				}
				else
				{
					sessionCheckInt++;
					break;
				}
			}
			if (CommonMethods.getTagValues((String)executeXMLMapMethod.get("getWorkItemOutputXML"),"MainCode").equalsIgnoreCase("0")){
				sessionCheckInt=0;
				while(sessionCheckInt<loopCount)
				{
					executeXMLMapMethod.put("inputXml1",CommonMethods.completeWorkItemInput(cabinetName,sessionId,wi_name,Integer.toString(1)));
					FalconCourierFileLog.mLogger.info("inputXml1 ---: "+executeXMLMapMethod.get("inputXml1"));
					FalconCourierFileLog.mLogger.debug("Output XML APCOMPLETE "+executeXMLMapMethod.get("inputXml1"));
					try
					{
						executeXMLMapMethod.put("outXml1",WFNGExecute((String)executeXMLMapMethod.get("inputXml1"),jtsIP,Integer.parseInt(jtsPort),1));
					}
					catch(Exception e)
					{
						FalconCourierFileLog.mLogger.error("Exception in Execute : " + e);
						sessionCheckInt++;
						waiteloopExecute(waitLoop);
						sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);

						continue;
					}

					FalconCourierFileLog.mLogger.info("outXml1 "+executeXMLMapMethod.get("outXml1"));
					sessionCheckInt++;
					if (CommonMethods.getTagValues((String)executeXMLMapMethod.get("outXml1"),"MainCode").equalsIgnoreCase("11"))
					{
						sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);

					}
					else
					{
						sessionCheckInt++;
						break;
					}
				}
			}
			if (CommonMethods.getTagValues((String)executeXMLMapMethod.get("outXml1"),"MainCode").equalsIgnoreCase("0"))
			{
				FalconCourierFileLog.mLogger.info("Completed "+wi_name);
			}
			else
			{
				FalconCourierFileLog.mLogger.info("Problem in completion of "+wi_name+" ,Maincode :"+CommonMethods.getTagValues((String)executeXMLMapMethod.get("outXml1"),"MainCode"));
			}
		}
		catch(Exception e)
		{
			FalconCourierFileLog.mLogger.error("Exception in workitem done = " +e);

			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			FalconCourierFileLog.mLogger.error("Exception Occurred in done wi : "+result);
		}
	}
	
	public static void waiteloopExecute(long wtime) {
		try {
			for (int i = 0; i < 10; i++) {
				Thread.yield();
				Thread.sleep(wtime / 10);
			}
		} catch (InterruptedException e) {
		}
	}
	
	private void updateExternalTable(String tablename, String columnname,String sMessage, String sWhere)
	{
		sessionCheckInt=0;

		while(sessionCheckInt<loopCount)
		{
			try
			{
				XMLParser objXMLParser = new XMLParser();
				String inputXmlcheckAPUpdate = CommonMethods.getAPUpdateIpXML(tablename,columnname,sMessage,sWhere,cabinetName,sessionId);
				FalconCourierFileLog.mLogger.debug("inputXmlcheckAPUpdate : " + inputXmlcheckAPUpdate);
				String outXmlCheckAPUpdate=null;
				outXmlCheckAPUpdate=WFNGExecute(inputXmlcheckAPUpdate,jtsIP,Integer.parseInt(jtsPort),1);
				FalconCourierFileLog.mLogger.info("outXmlCheckAPUpdate : " + outXmlCheckAPUpdate);
				objXMLParser.setInputXML(outXmlCheckAPUpdate);
				String mainCodeforCheckUpdate = null;
				mainCodeforCheckUpdate=objXMLParser.getValueOf("MainCode");
				if (!mainCodeforCheckUpdate.equalsIgnoreCase("0"))
				{
					FalconCourierFileLog.mLogger.error("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
					System.out.println("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
				}
				else
				{
					FalconCourierFileLog.mLogger.error("Succesfully updated "+tablename+" table");
					System.out.println("Succesfully updated "+tablename+" table");
				}
				mainCode=Integer.parseInt(mainCodeforCheckUpdate);
				if (mainCode == 11)
				{
					sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);
				}
				else
				{
					sessionCheckInt++;
					break;
				}

				if (outXmlCheckAPUpdate.equalsIgnoreCase("") || outXmlCheckAPUpdate == "" || outXmlCheckAPUpdate == null)
					break;

			}
			catch(Exception e)
			{
				FalconCourierFileLog.mLogger.error("Inside create validateSessionID exception"+e);
			}
		}
	}
	
	private void historyCaller(String workItemName, boolean DocAttached)
	{
		FalconCourierFileLog.mLogger.debug("In History Caller method");

		XMLParser objXMLParser = new XMLParser();
		String sOutputXML=null;
		String mainCodeforAPInsert=null;
		sessionCheckInt=0;
		while(sessionCheckInt<loopCount)
		{
			try
			{
				if(workItemName!=null)
				{
					String hist_table="NG_RLOS_FALCON_WIHISTORY";    //History table name needs to be changed Sajan
					String columns="wi_name,ws_name,decision,action_date_time,remarks,user_name,Entry_Date_Time";
					String WINAME=workItemName;
					String WSNAME="Attach_Cust_Docs";  // Hold workstep for documents needs to be changed Sajan
					String remarks="";
					String decision = "";
					if(true)
					{
						remarks = "Documents Attached by Utility";    // Remarks there or not Changed Sajan
						decision= "Success";
					}

					String lusername="System";

					SimpleDateFormat outputDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");

					Date actionDateTime= new Date();
					String formattedActionDateTime=outputDateFormat.format(actionDateTime);
					FalconCourierFileLog.mLogger.debug("FormattedActionDateTime: "+formattedActionDateTime);

					String entryDatetime=getEntryDatetimefromDB(workItemName);


					String values = "'" + WINAME +"'" + "," + "'" + WSNAME +"'" + "," + "'" + decision +"'" + ","  + "'"+formattedActionDateTime+"'" + "," + "'" + remarks +"'" + "," +  "'" + lusername + "'" +  "," + "'"+entryDatetime+"'";
					FalconCourierFileLog.mLogger.debug("Values for history : \n"+values);

					String sInputXMLAPInsert = CommonMethods.apInsert(cabinetName,sessionId,columns,values,hist_table);

					FalconCourierFileLog.mLogger.info("History_InputXml::::::::::\n"+sInputXMLAPInsert);
					sOutputXML= WFNGExecute(sInputXMLAPInsert,jtsIP,Integer.parseInt(jtsPort),1);
					FalconCourierFileLog.mLogger.info("History_OutputXml::::::::::\n"+sOutputXML);
					objXMLParser.setInputXML(sOutputXML);
					mainCodeforAPInsert=objXMLParser.getValueOf("MainCode");

				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				FalconCourierFileLog.mLogger.error("Exception in historyCaller of UpdateExpiryDate", e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);
				continue;

			}
			if (mainCodeforAPInsert.equalsIgnoreCase("11")) 
			{
				sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);
			}
			else
			{
				sessionCheckInt++;
				break;
			}
		}
		if(mainCodeforAPInsert.equalsIgnoreCase("0"))
		{
			FalconCourierFileLog.mLogger.info("Insert Successful");
		}
		else
		{
			FalconCourierFileLog.mLogger.info("Insert Unsuccessful");
		}
		FalconCourierFileLog.mLogger.debug("Out History Caller method");
	}
	
	
	//Entry date logic and value needs to be checked Sajan
	public String getEntryDatetimefromDB(String workItemName)
	{
		FalconCourierFileLog.mLogger.info("Start of function getEntryDatetimefromDB ");
		String entryDatetimeAttachCust="";
		String formattedEntryDatetime="";
		String outputXMLEntryDate=null;
		String mainCodeEntryDate=null;

		sessionCheckInt=0;
		while(sessionCheckInt<loopCount)
		{
			try 
			{
				XMLParser objXMLParser = new XMLParser();
				String sqlQuery = "select entryat from NG_DOB_EXTTABLE with(nolock) where WI_NAME='"+workItemName+"'";
				String InputXMLEntryDate = CommonMethods.apSelectWithColumnNames(sqlQuery,cabinetName, sessionId);
				FalconCourierFileLog.mLogger.info("Getting getIntegrationErrorDescription from exttable table "+InputXMLEntryDate);
				outputXMLEntryDate = WFNGExecute(InputXMLEntryDate, jtsIP, Integer.parseInt(jtsPort), 1);
				FalconCourierFileLog.mLogger.info("OutputXML for getting getIntegrationErrorDescription from external table "+outputXMLEntryDate);
				objXMLParser.setInputXML(outputXMLEntryDate);
				mainCodeEntryDate=objXMLParser.getValueOf("MainCode");
			} 
			catch (Exception e) 
			{
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				continue;
			}
			if (!mainCodeEntryDate.equalsIgnoreCase("0"))
			{
				sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);

			}
			else
			{
				sessionCheckInt++;
				break;
			}
		}

		if (mainCodeEntryDate.equalsIgnoreCase("0")) 
		{
			try 
			{
				entryDatetimeAttachCust = CommonMethods.getTagValues(outputXMLEntryDate, "entryat");
				
				SimpleDateFormat inputDateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
				SimpleDateFormat outputDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
				Date entryDatetimeFormat = inputDateformat.parse(entryDatetimeAttachCust);
				formattedEntryDatetime=outputDateFormat.format(entryDatetimeFormat);
				FalconCourierFileLog.mLogger.debug("FormattedEntryDatetime: "+formattedEntryDatetime);

				FalconCourierFileLog.mLogger.info("newentrydatetime "+ formattedEntryDatetime);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return formattedEntryDatetime;
	}
	
	public static String get_timestamp()
	{
		Date present = new Date();
		Format pformatter = new SimpleDateFormat("dd-MM-yyyy-hhmmss");
		TimeStamp=pformatter.format(present);
		return TimeStamp;
	}
	
	public String Move(String pstrDestFolderPath, String pstrFilePathToMove,String append,boolean flag ) 
	{
		//mLogger.info("Inside Move  : "+pstrDestFolderPath+" : "+pstrFilePathToMove);
		String lstrExceptionId = "Text_Read.Move";
		try 
		{

			// Destination directory
			File lobjDestFolder = new File(pstrDestFolderPath);

			if (!lobjDestFolder.exists()) 
			{

				lobjDestFolder.mkdirs();

				//delete destination file if it already exists
				//////////////
			}
			File lobjFileTemp;
			File lobjFileToMove = new File(pstrFilePathToMove);
			String orgFileName=lobjFileToMove.getName();

			if(flag){
				newFilename=orgFileName.substring(0,orgFileName.indexOf("."))+"_"+append+orgFileName.substring(orgFileName.indexOf("."));
				lobjFileTemp = new File(pstrDestFolderPath + File.separator + newFilename);
			}else{
				//mLogger.info("orgFileName::"+orgFileName);
				newFilename=orgFileName;
				lobjFileTemp = new File(pstrDestFolderPath+ File.separator + newFilename );
				//mLogger.info("lobjFileTemp::"+lobjFileTemp);
			}
			if (lobjFileTemp.exists()) 
			{
				//mLogger.info("lobjFileTemp exists");
				if (!lobjFileTemp.isDirectory()) 
				{

					lobjFileTemp.delete();

				} 
				else 
				{

					deleteDir(lobjFileTemp);

				}
			} 
			else 
			{
				//mLogger.info("lobjFileTemp dont exists");
				lobjFileTemp = null;
			}
			File lobjNewFolder ;
			// if(flag){
			lobjNewFolder = new File(lobjDestFolder, newFilename);
			/* }else{
            	 lobjNewFolder = lobjDestFolder;
            }*/


			boolean lbSTPuccess = false;
			try 
			{
				//mLogger.info("lobjFileToMove::"+lobjFileToMove);
				//mLogger.info("lobjNewFolder::"+lobjNewFolder);
				lbSTPuccess = lobjFileToMove.renameTo(lobjNewFolder);
				//mLogger.info("lbSTPuccess::"+lbSTPuccess);
			} 
			catch (SecurityException lobjExp) 
			{

				//mLogger.info("SecurityException " + lobjExp.toString());
			} 
			catch (NullPointerException lobjNPExp) 
			{

				//mLogger.info("NullPointerException " + lobjNPExp.toString());
			} 
			catch (Exception lobjExp) 
			{

				//mLogger.info("Exception " + lobjExp.toString());
			}
			if (!lbSTPuccess) 
			{
				// File was not successfully moved


				//mLogger.info("Failure while moving " + lobjFileToMove.getAbsolutePath() + "===" +
				//	lobjFileToMove.canWrite());
			} 
			else 
			{

				//mLogger.info("Success while moving " + lobjFileToMove.getName() + "to" + pstrDestFolderPath);
				//mLogger.info("Success while moving " + lobjFileToMove.getName() + "to" + lobjNewFolder);
			}
			lobjDestFolder = null;
			lobjFileToMove = null;
			lobjNewFolder = null;
		} 
		catch (Exception lobjExp) 
		{
			//mLogger.info(lstrExceptionId + " : " + "Exception occurred while moving " + pstrFilePathToMove + " to " +
					//":" + lobjExp.toString());

		}

		return newFilename;
	}
	
	
	
	
	public static String Move_1(String destFolderPath, String srcFolderPath,String append)
	{
		try
		{
			File objDestFolder = new File(destFolderPath);
			if (!objDestFolder.exists())
			{
				objDestFolder.mkdirs();
			}
			File objsrcFolderPath = new File(srcFolderPath);
			newFilename = objsrcFolderPath.getName();
			File lobjFileTemp = new File(destFolderPath + File.separator + newFilename);
			if (lobjFileTemp.exists())
			{
				if (!lobjFileTemp.isDirectory())
				{
					lobjFileTemp.delete();
				}
				else
				{
					deleteDir(lobjFileTemp);
				}
			}
			else
			{
				lobjFileTemp = null;
			}
			File lobjNewFolder = new File(objDestFolder, newFilename);

			boolean lbSTPuccess = false;
			try
			{
				lbSTPuccess = lobjNewFolder.renameTo(objsrcFolderPath);
			}
			catch (SecurityException lobjExp)
			{
				System.out.println("SecurityException");
			}
			catch (NullPointerException lobjNPExp)
			{
				System.out.println("NullPointerException");
			}
			catch (Exception lobjExp)
			{
				System.out.println("Exception");
			}
			if (!lbSTPuccess)
			{
				System.out.println("lbSTPuccess");
			}
			else
			{
				System.out.println("else");
			}
			objDestFolder = null;
			objsrcFolderPath = null;
			lobjNewFolder = null;
		}
		catch (Exception lobjExp)
		{
		}

		return newFilename;
	}

	public static boolean deleteDir(File dir) throws Exception {
		if (dir.isDirectory()) {
			String[] lstrChildren = dir.list();
			for (int i = 0; i < lstrChildren.length; i++) {
				boolean success = deleteDir(new File(dir, lstrChildren[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
	
	private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
	
	@SuppressWarnings({"unchecked" })
	private List loadWorkItems(String queueID,String sessionId) throws NumberFormatException, IOException, Exception
	{
		FalconCourierFileLog.mLogger.info("Starting loadWorkitem function for queueID -->"+queueID);
		List workItemList = null;
		String workItemListInputXML="";
		sessionCheckInt=0;
		String workItemListOutputXML="";
		FalconCourierFileLog.mLogger.info("loopCount aa:" + loopCount);
		FalconCourierFileLog.mLogger.info("lastWorkItemId aa:" + lastWorkItemId);
		FalconCourierFileLog.mLogger.info("lastProcessInstanceId aa:" + lastProcessInstanceId);
		while(sessionCheckInt<loopCount)
		{
			FalconCourierFileLog.mLogger.info("123 cabinet name..."+cabinetName);
			FalconCourierFileLog.mLogger.info("123 session id is..."+sessionId);
			workItemListInputXML = CommonMethods.getFetchWorkItemsInputXML(lastProcessInstanceId, lastWorkItemId, sessionId, cabinetName, queueID);
			FalconCourierFileLog.mLogger.info("workItemListInputXML aa:" + workItemListInputXML);
			try
			{
				workItemListOutputXML=WFNGExecute(workItemListInputXML,jtsIP,Integer.parseInt(jtsPort),1);
			}
			catch(Exception e)
			{
				FalconCourierFileLog.mLogger.error("Exception in Execute : " + e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);
				continue;
			}

			FalconCourierFileLog.mLogger.info("workItemListOutputXML : " + workItemListOutputXML);
			if (CommonMethods.getTagValues(workItemListOutputXML,"MainCode").equalsIgnoreCase("11"))
			{
				sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);
			}
			else
			{
				sessionCheckInt++;
				break;
			}
		}

		int i = 0;
		while(i <= 3)
		{
			if (CommonMethods.getMainCode(workItemListOutputXML) == 0)
			{
				i = 4;
				String [] last = new String[2];
				workItemList = new ArrayList();
				List workItems = getWorkItems(sessionId,workItemListOutputXML, last);
				workItemList.addAll(workItems);
				lastProcessInstanceId = "";
				lastWorkItemId = "";
			}
			else
			{
				i++;
				lastProcessInstanceId = "";
				lastWorkItemId = "";
			}
		}
		FalconCourierFileLog.mLogger.info("Exiting loadWorkitem function for queueID -->"+queueID);
		return workItemList;
	}
	 
	@SuppressWarnings({ "unchecked" })
	private List getWorkItems(String sessionId, String workItemListOutputXML, String[] last) throws NumberFormatException, Exception
	{
		// TODO Auto-generated method stub
		FalconCourierFileLog.mLogger.info("Starting getWorkitems function ");
		Document doc = CommonMethods.getDocument(workItemListOutputXML);

		NodeList instruments = doc.getElementsByTagName("Instrument");
		List workItems = new ArrayList();

		int length = instruments.getLength();

		for (int i =0; i < length; ++i)
		{
			Node inst = instruments.item(i);
			WorkItem wi = getWI(sessionId, inst);
			workItems.add(wi);
		}
		int size = workItems.size();
		if (size > 0)
		{
			WorkItem item = (WorkItem)workItems.get(size -1);
			last[0] = item.processInstanceId;
			last[1] = item.workItemId;

			FalconCourierFileLog.mLogger.info("last[0] : "+last[0]);
		}
		FalconCourierFileLog.mLogger.info("Exiting getWorkitems function");
		return workItems;
	}
	 
	@SuppressWarnings("unchecked")
	private WorkItem getWI(String sessionId, Node inst) throws NumberFormatException, IOException, Exception
	{
		FalconCourierFileLog.mLogger.info("Starting getWI function");
		WorkItem wi = new WorkItem();
		wi.processInstanceId = CommonMethods.getTagValues(inst, "ProcessInstanceId");
		wi.workItemId = CommonMethods.getTagValues(inst, "WorkItemId");
		String fetchAttributeInputXML="";
		String fetchAttributeOutputXML="";
		sessionCheckInt=0;
		while(sessionCheckInt<loopCount)
		{
			fetchAttributeInputXML = CommonMethods.getFetchWorkItemAttributesXML(cabinetName,sessionId,wi.processInstanceId, wi.workItemId);
			FalconCourierFileLog.mLogger.info("FetchAttributeInputXMl "+fetchAttributeInputXML);
			fetchAttributeOutputXML=WFNGExecute(fetchAttributeInputXML,jtsIP,Integer.parseInt(jtsPort),1);
			fetchAttributeOutputXML=fetchAttributeOutputXML.replaceAll("&","&amp;");
			FalconCourierFileLog.mLogger.info("fetchAttributeOutputXML "+fetchAttributeOutputXML);
			if (CommonMethods.getTagValues(fetchAttributeOutputXML, "MainCode").equalsIgnoreCase("11"))
			{
				sessionId  = CommonConnection.getSessionID(FalconCourierFileLog.mLogger, false);

			} else {
					sessionCheckInt++;
					break;
					}

			if (CommonMethods.getMainCode(fetchAttributeOutputXML) != 0)
			{
				FalconCourierFileLog.mLogger.debug(" MapXML.getMainCode(fetchAttributeOutputXML) != 0 ");
			}
		}

		try
		{
			for (int i = 0; i < attributeNames.length; ++i)
			{
				String columnValue = getAttribute(fetchAttributeOutputXML, attributeNames[i]);
				if (columnValue != null)
				{
					wi.map.put(attributeNames[i], columnValue);
				}
				else
				{
					wi.map.put(attributeNames[i], "");
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			FalconCourierFileLog.mLogger.debug("Inside catch of get wi function with exception.."+e);
		}
		FalconCourierFileLog.mLogger.info("Exiting getWI function");
		return wi;
	}
	 
	public static String getAttribute(String fetchAttributeOutputXML, String accountNo) throws ParserConfigurationException, SAXException, IOException 
	{
		Document doc = CommonMethods.getDocument(fetchAttributeOutputXML);
		NodeList nodeList = doc.getElementsByTagName("Attribute");
		int length = nodeList.getLength();
		for (int i = 0; i < length; ++i) 
		{
			Node item = nodeList.item(i);
			String name = CommonMethods.getTagValues(item, "Name");
			if (name.trim().equalsIgnoreCase(accountNo.trim())) 
			{
				return CommonMethods.getTagValues(item, "Value");
			}
		}
		return "";
	}
	 
	public static String WFNGExecute(String ipXML, String serverIP,
				int serverPort, int flag) throws IOException, Exception 
	{
		String jtsPort=""+serverPort;
		if (jtsPort.startsWith("33"))
			return WFCallBroker.execute(ipXML, serverIP, serverPort, flag);
		else
			return ngEjbClientFalconDocument.makeCall(serverIP, serverPort + "", "WebSphere",
						ipXML);
	}
}