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


package com.newgen.DigitalCC.Dispatch;


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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ISPack.CPISDocumentTxn;
import ISPack.ISUtil.JPDBRecoverDocData;
import ISPack.ISUtil.JPISException;
import ISPack.ISUtil.JPISIsIndex;

import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;

public class DCCGenerateFile implements Runnable{	
	
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
	
	static Map<String, String> falconDocumentCofigParamMap= new HashMap<String, String>();
	private Map <String, String> executeXMLMapMethod = new HashMap<String, String>();
	private static NGEjbClient ngEjbClientFalconDocument;
	private final String ECRN_CRN_PROC="generateCRNANDECRN";
	
	public void run()
	{
		int sleepIntervalInMin=0;
		try
		{
			DCCDispatchLog.setLogger();
			ngEjbClientFalconDocument = NGEjbClient.getSharedInstance();

			DCCDispatchLog.mLogger.debug("Connecting to Cabinet.");

			//int configReadStatus = readConfig();

			/*alconDispatchLog.mLogger.debug("configReadStatus "+configReadStatus);
			if(configReadStatus !=0)
			{
				DCCDispatchLog.mLogger.error("Could not Read Config Properties [CMPDocument]");
				return;
			}*/

			cabinetName = CommonConnection.getCabinetName();
			DCCDispatchLog.mLogger.debug("Cabinet Name: " + cabinetName);

			jtsIP = CommonConnection.getJTSIP();
			DCCDispatchLog.mLogger.debug("JTSIP: " + jtsIP);

			jtsPort = CommonConnection.getJTSPort();
			DCCDispatchLog.mLogger.debug("JTSPORT: " + jtsPort);

			smsPort = CommonConnection.getsSMSPort();
			DCCDispatchLog.mLogger.debug("SMSPort: " + smsPort);			

			/*sleepIntervalInMin=Integer.parseInt(falconDocumentCofigParamMap.get("SleepIntervalInMin"));
			DCCDispatchLog.mLogger.debug("SleepIntervalInMin: "+sleepIntervalInMin);

			attributeNames=falconDocumentCofigParamMap.get("AttributeNames").split(",");
			DCCDispatchLog.mLogger.debug("AttributeNames: " + attributeNames);

			ExternalTable=falconDocumentCofigParamMap.get("ExtTableName");
			DCCDispatchLog.mLogger.debug("ExternalTable: " + ExternalTable);

			destFilePath=falconDocumentCofigParamMap.get("destFilePath");
			DCCDispatchLog.mLogger.debug("destFilePath: " + destFilePath);

			ErrorFolder=falconDocumentCofigParamMap.get("failDestFilePath");
			DCCDispatchLog.mLogger.debug("ErrorFolder: " + ErrorFolder);

			volumeID=falconDocumentCofigParamMap.get("VolumeID");
			DCCDispatchLog.mLogger.debug("VolumeID: " + volumeID);

			MaxNoOfTries=falconDocumentCofigParamMap.get("MaxNoOfTries");   //Not getting used anywhere Sajan
			DCCDispatchLog.mLogger.debug("MaxNoOfTries: " + MaxNoOfTries);

			TimeIntervalBetweenTrialsInMin=Integer.parseInt(falconDocumentCofigParamMap.get("TimeIntervalBetweenTrialsInMin"));
			DCCDispatchLog.mLogger.debug("TimeIntervalBetweenTrialsInMin: " + TimeIntervalBetweenTrialsInMin);*/
			
			sessionId = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);
			if(sessionId.trim().equalsIgnoreCase(""))
			{
				DCCDispatchLog.mLogger.debug("Could Not Connect to Server!");
			}
			else
			{
				DCCDispatchLog.mLogger.debug("Session ID found: " + sessionId);
				while(true)
				{
					startFalconDispatchUtility();
					System.out.println("No More workitems to Process, Sleeping!");
					Thread.sleep(sleepIntervalInMin*60*1000);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			DCCDispatchLog.mLogger.error("Exception Occurred in FALCON Document Document Thread: "+e);
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			DCCDispatchLog.mLogger.error("Exception Occurred in FALCON Document Thread : "+result);
		}
	}
	
	private int readConfig()
	{
		Properties p = null;
		try 
		{
			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "ConfigFiles"+ File.separator+ "Falcon_Dispatch_Config.properties")));
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
			DCCDispatchLog.mLogger.error("Exception has occured while loading properties file "+e.getMessage());
			return -1 ;			
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	private void startFalconDispatchUtility() throws Exception
	{
		DCCDispatchLog.mLogger.info("ProcessWI function for Falcon Document Utility started");

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

		sessionId  = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);

		if(sessionId==null || sessionId.equalsIgnoreCase("") || sessionId.equalsIgnoreCase("null"))
		{
			DCCDispatchLog.mLogger.error("Could Not Get Session ID "+sessionId);
			return;
		}

		List<String> wiList = new ArrayList<String>();
		try
		{
			//queueID = falconDocumentCofigParamMap.get("QueueID");
			//DCCDispatchLog.mLogger.debug("QueueID: " + queueID);
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
			String outputFileName = "CAS_DISPATCH_DETAILS_" + sDate + ".txt";
			String sFilePath=".\\CasToCbsFile\\";
			 File fDumpFolder = new File(sFilePath);
		    if (!(fDumpFolder.exists()))
		      fDumpFolder.mkdirs();
		    PrintWriter writer = new PrintWriter(sFilePath+outputFileName, "UTF-8");
		    DCCDispatchLog.mLogger.debug("after printwriter:");
		    DCCDispatchLog.mLogger.debug("File Path Auto:"+sFilePath);
			 writer.println("H|" + sDate + "|FALCON_CDP_DAILY_INC|" + outputFileName);
			 DCCDispatchLog.mLogger.debug("after writer.println::");
			for (String wi : wiList)
			{
				String QueryString = "Select a.AWB_NUMBER,a.CC_WI_NAME,a.PORTAL_REF_NUMBER,b.EMIRATEID,b.CUSTOMERNAME,b.MOBILENO from " +
						"(select AWB_NUMBER,CC_WI_NAME,PORTAL_REF_NUMBER from NG_DOB_EXTTABLE with (nolock) where CC_Wi_Name='"+wi+"') a " +
						"INNER JOIN (Select EmirateID,FirstName+' '+ISNULL(MiddleName,'')+' '+LAstName as CustomerName,MobileNo,wi_name  from ng_RLOS_Customer with (nolock)) b " +
						"on a.CC_WI_NAME=b.wi_name";

				String sInputXML =CommonMethods.apSelectWithColumnNames(QueryString, cabinetName, sessionId);

				DCCDispatchLog.mLogger.debug("APSelect Inputxml to fetch WI data is: "+sInputXML);

				String sOutputXML=WFNGExecute(sInputXML,jtsIP,Integer.parseInt(jtsPort),1);
				DCCDispatchLog.mLogger.debug("APSelect OutputXML to fetch WI data is : "+sOutputXML);
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
			    	
			    	DCCDispatchLog.mLogger.info("B|" + strWIName + "|" + strPortalRefNo+ "|" + strAWBNo + "|" + strEmirateId + "|" + strCustomerName+"|"+strMobileNo);
			    	writer.println("B|" + strWIName + "|" + strPortalRefNo+ "|" + strAWBNo + "|" + strEmirateId + "|" + strCustomerName+"|"+strMobileNo);
			    }
			    updateFlagInExttable(wi);
			}
			writer.println("T|" + wiList.size());
		    writer.close();
		    Thread.sleep(5000);
		}
		
		DCCDispatchLog.mLogger.info("exiting ProcessWI function FALCON Document Utility");
	}
	
	private void updateFlagInExttable(String WorkitemName)throws IOException,Exception{
		String strInputXml=CommonMethods.apUpdateInput(cabinetName, sessionId, "NG_DOB_EXTTABLE", "isDispatchFileDone", "'Y'", "CC_Wi_Name='"+WorkitemName+"'");
		DCCDispatchLog.mLogger.info("Input XML to Upadte flag in table is : Generate File "+strInputXml);
		String sOutputXML=WFNGExecute(strInputXml,jtsIP,Integer.parseInt(jtsPort),1);
		DCCDispatchLog.mLogger.debug("APSelect OutputXML to fetch WI data is : "+sOutputXML);
		
		
	}
	
	private void doneWorkItem(String wi_name,String values,Boolean... compeletFlag)
	{
		assert compeletFlag.length <= 1;
		sessionId  = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);
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
					DCCDispatchLog.mLogger.error("Exception in Execute : " + e);
					sessionCheckInt++;
					waiteloopExecute(waitLoop);
					sessionId  = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);
					continue;
				}
				sessionCheckInt++;
				if (CommonMethods.getTagValues((String)executeXMLMapMethod.get("getWorkItemOutputXML"),"MainCode").equalsIgnoreCase("11"))
				{
					sessionId  = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);

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
					DCCDispatchLog.mLogger.info("inputXml1 ---: "+executeXMLMapMethod.get("inputXml1"));
					DCCDispatchLog.mLogger.debug("Output XML APCOMPLETE "+executeXMLMapMethod.get("inputXml1"));
					try
					{
						executeXMLMapMethod.put("outXml1",WFNGExecute((String)executeXMLMapMethod.get("inputXml1"),jtsIP,Integer.parseInt(jtsPort),1));
					}
					catch(Exception e)
					{
						DCCDispatchLog.mLogger.error("Exception in Execute : " + e);
						sessionCheckInt++;
						waiteloopExecute(waitLoop);
						sessionId  = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);

						continue;
					}

					DCCDispatchLog.mLogger.info("outXml1 "+executeXMLMapMethod.get("outXml1"));
					sessionCheckInt++;
					if (CommonMethods.getTagValues((String)executeXMLMapMethod.get("outXml1"),"MainCode").equalsIgnoreCase("11"))
					{
						sessionId  = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);

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
				DCCDispatchLog.mLogger.info("Completed "+wi_name);
			}
			else
			{
				DCCDispatchLog.mLogger.info("Problem in completion of "+wi_name+" ,Maincode :"+CommonMethods.getTagValues((String)executeXMLMapMethod.get("outXml1"),"MainCode"));
			}
		}
		catch(Exception e)
		{
			DCCDispatchLog.mLogger.error("Exception in workitem done = " +e);

			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			DCCDispatchLog.mLogger.error("Exception Occurred in done wi : "+result);
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
				DCCDispatchLog.mLogger.debug("inputXmlcheckAPUpdate : " + inputXmlcheckAPUpdate);
				String outXmlCheckAPUpdate=null;
				outXmlCheckAPUpdate=WFNGExecute(inputXmlcheckAPUpdate,jtsIP,Integer.parseInt(jtsPort),1);
				DCCDispatchLog.mLogger.info("outXmlCheckAPUpdate : " + outXmlCheckAPUpdate);
				objXMLParser.setInputXML(outXmlCheckAPUpdate);
				String mainCodeforCheckUpdate = null;
				mainCodeforCheckUpdate=objXMLParser.getValueOf("MainCode");
				if (!mainCodeforCheckUpdate.equalsIgnoreCase("0"))
				{
					DCCDispatchLog.mLogger.error("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
					System.out.println("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
				}
				else
				{
					DCCDispatchLog.mLogger.error("Succesfully updated "+tablename+" table");
					System.out.println("Succesfully updated "+tablename+" table");
				}
				mainCode=Integer.parseInt(mainCodeforCheckUpdate);
				if (mainCode == 11)
				{
					sessionId  = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);
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
				DCCDispatchLog.mLogger.error("Inside create validateSessionID exception"+e);
			}
		}
	}
	
	private void historyCaller(String workItemName, boolean DocAttached)
	{
		DCCDispatchLog.mLogger.debug("In History Caller method");

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
					DCCDispatchLog.mLogger.debug("FormattedActionDateTime: "+formattedActionDateTime);

					String entryDatetime=getEntryDatetimefromDB(workItemName);


					String values = "'" + WINAME +"'" + "," + "'" + WSNAME +"'" + "," + "'" + decision +"'" + ","  + "'"+formattedActionDateTime+"'" + "," + "'" + remarks +"'" + "," +  "'" + lusername + "'" +  "," + "'"+entryDatetime+"'";
					DCCDispatchLog.mLogger.debug("Values for history : \n"+values);

					String sInputXMLAPInsert = CommonMethods.apInsert(cabinetName,sessionId,columns,values,hist_table);

					DCCDispatchLog.mLogger.info("History_InputXml::::::::::\n"+sInputXMLAPInsert);
					sOutputXML= WFNGExecute(sInputXMLAPInsert,jtsIP,Integer.parseInt(jtsPort),1);
					DCCDispatchLog.mLogger.info("History_OutputXml::::::::::\n"+sOutputXML);
					objXMLParser.setInputXML(sOutputXML);
					mainCodeforAPInsert=objXMLParser.getValueOf("MainCode");

				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				DCCDispatchLog.mLogger.error("Exception in historyCaller of UpdateExpiryDate", e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				sessionId  = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);
				continue;

			}
			if (mainCodeforAPInsert.equalsIgnoreCase("11")) 
			{
				sessionId  = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);
			}
			else
			{
				sessionCheckInt++;
				break;
			}
		}
		if(mainCodeforAPInsert.equalsIgnoreCase("0"))
		{
			DCCDispatchLog.mLogger.info("Insert Successful");
		}
		else
		{
			DCCDispatchLog.mLogger.info("Insert Unsuccessful");
		}
		DCCDispatchLog.mLogger.debug("Out History Caller method");
	}
	
	
	//Entry date logic and value needs to be checked Sajan
	public String getEntryDatetimefromDB(String workItemName)
	{
		DCCDispatchLog.mLogger.info("Start of function getEntryDatetimefromDB ");
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
				DCCDispatchLog.mLogger.info("Getting getIntegrationErrorDescription from exttable table "+InputXMLEntryDate);
				outputXMLEntryDate = WFNGExecute(InputXMLEntryDate, jtsIP, Integer.parseInt(jtsPort), 1);
				DCCDispatchLog.mLogger.info("OutputXML for getting getIntegrationErrorDescription from external table "+outputXMLEntryDate);
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
				sessionId  = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);

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
				DCCDispatchLog.mLogger.debug("FormattedEntryDatetime: "+formattedEntryDatetime);

				DCCDispatchLog.mLogger.info("newentrydatetime "+ formattedEntryDatetime);
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
	
	public static String Move(String destFolderPath, String srcFolderPath,String append)
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
			File lobjNewFolder = new File(objDestFolder, newFilename +"_"+ append);

			boolean lbSTPuccess = false;
			try
			{
				lbSTPuccess = objsrcFolderPath.renameTo(lobjNewFolder);
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
	private List loadWorkItems() throws Exception
	{
		DCCDispatchLog.mLogger.info("Starting loadWorkitem function");
		List workItemList = null;
		String workItemListInputXML="";
		String QueryString = "SELECT CC_WI_NAME from NG_DOB_EXTTABLE WHERE isDispatchFileDone='N' AND CC_Wi_Name IS NOT NULL";

		String sInputXML =CommonMethods.apSelectWithColumnNames(QueryString, cabinetName, sessionId);

		DCCDispatchLog.mLogger.debug("APSelect Inputxml: "+sInputXML);

		String sOutputXML=WFNGExecute(sInputXML,jtsIP,Integer.parseInt(jtsPort),1);
		DCCDispatchLog.mLogger.debug("APSelect OutputXML: "+sOutputXML);
		XMLParser sXMLParser= new XMLParser(sOutputXML);
	    String sMainCode = sXMLParser.getValueOf("MainCode");
		List<String> wList=new ArrayList<String>();
		int sTotalRecords = Integer.parseInt(sXMLParser.getValueOf("TotalRetrieved"));
		DCCDispatchLog.mLogger.debug("STotalRecords: "+sTotalRecords);
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
	 
	@SuppressWarnings({ "unchecked" })
	private List getWorkItems(String sessionId, String workItemListOutputXML, String[] last) throws NumberFormatException, Exception
	{
		// TODO Auto-generated method stub
		DCCDispatchLog.mLogger.info("Starting getWorkitems function ");
		Document doc = CommonMethods.getDocument(workItemListOutputXML);

		NodeList instruments = doc.getElementsByTagName("Instrument");
		List workItems = new ArrayList();

		int length = instruments.getLength();

		for (int i =0; i < length; ++i)
		{
			Node inst = instruments.item(i);
			DCCWorkItem wi = getWI(sessionId, inst);
			workItems.add(wi);
		}
		int size = workItems.size();
		if (size > 0)
		{
			DCCWorkItem item = (DCCWorkItem)workItems.get(size -1);
			last[0] = item.processInstanceId;
			last[1] = item.workItemId;

			DCCDispatchLog.mLogger.info("last[0] : "+last[0]);
		}
		DCCDispatchLog.mLogger.info("Exiting getWorkitems function");
		return workItems;
	}
	 
	@SuppressWarnings("unchecked")
	private DCCWorkItem getWI(String sessionId, Node inst) throws NumberFormatException, IOException, Exception
	{
		DCCDispatchLog.mLogger.info("Starting getWI function");
		DCCWorkItem wi = new DCCWorkItem();
		wi.processInstanceId = CommonMethods.getTagValues(inst, "ProcessInstanceId");
		wi.workItemId = CommonMethods.getTagValues(inst, "WorkItemId");
		String fetchAttributeInputXML="";
		String fetchAttributeOutputXML="";
		sessionCheckInt=0;
		while(sessionCheckInt<loopCount)
		{
			fetchAttributeInputXML = CommonMethods.getFetchWorkItemAttributesXML(cabinetName,sessionId,wi.processInstanceId, wi.workItemId);
			DCCDispatchLog.mLogger.info("FetchAttributeInputXMl "+fetchAttributeInputXML);
			fetchAttributeOutputXML=WFNGExecute(fetchAttributeInputXML,jtsIP,Integer.parseInt(jtsPort),1);
			fetchAttributeOutputXML=fetchAttributeOutputXML.replaceAll("&","&amp;");
			DCCDispatchLog.mLogger.info("fetchAttributeOutputXML "+fetchAttributeOutputXML);
			if (CommonMethods.getTagValues(fetchAttributeOutputXML, "MainCode").equalsIgnoreCase("11"))
			{
				sessionId  = CommonConnection.getSessionID(DCCDispatchLog.mLogger, false);

			} else {
					sessionCheckInt++;
					break;
					}

			if (CommonMethods.getMainCode(fetchAttributeOutputXML) != 0)
			{
				DCCDispatchLog.mLogger.debug(" MapXML.getMainCode(fetchAttributeOutputXML) != 0 ");
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
			DCCDispatchLog.mLogger.debug("Inside catch of get wi function with exception.."+e);
		}
		DCCDispatchLog.mLogger.info("Exiting getWI function");
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