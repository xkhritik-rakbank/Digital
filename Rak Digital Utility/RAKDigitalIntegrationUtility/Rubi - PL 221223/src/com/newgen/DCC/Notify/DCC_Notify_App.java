/*
---------------------------------------------------------------------------------------------------------
                  NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group                   : Application - Projects
Project/Product			: RAK BPM
Application				: RAK BPM Utility
Module					: RAOP Status
File Name				: RAOPStatus.java
Author 					: Ravindra Kumar	
Date (DD/MM/YYYY)		: 01/06/2022

---------------------------------------------------------------------------------------------------------
                 	CHANGE HISTORY
---------------------------------------------------------------------------------------------------------

Problem No/CR No        Change Date           Changed By             Change Description
---------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------
*/


package com.newgen.DCC.Notify;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import ISPack.CImageServer;
import ISPack.CPISDocumentTxn;
import ISPack.ISUtil.JPISException;
import Jdts.DataObject.JPDBString;

import com.newgen.DCC.Final_Limit_Increase.DCC_FINAL_LIMIT_LOG;
import com.newgen.DCC.Update_AssignCIF.DCC_DocumentGeneration;
import com.newgen.DCC.Update_AssignCIF.DCC_UpdateAssignCIFLog;
import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;
import com.newgen.wfdesktop.xmlapi.WFXmlResponse;


public class DCC_Notify_App implements Runnable
{
	static Map<String, String> NotifyAppConfigParamMap= new HashMap<String, String>();
	private static NGEjbClient ngEjbClientCIFVer;
	int socketConnectionTimeout=0;
	int integrationWaitTime=0;
	int sleepIntervalInMin=0;
	private static String cabinetName = null;
	private static String jtsIP = null;
	private static String jtsPort = null;
	private static String queueID = null;
	private static String propDocsPath = null;
	
	public String sessionID = "";
	
	public String fromMailID="";
	public String toMailID = "";
	public String mailSubject = "";
	public String MailStr="";
	public String Hold_AlternateCard="";
	public static int sessionCheckInt=0;
	public static int waitLoop=50;
	public static int loopCount=50;
	//09042023
	private static String DCC_WICREATION_DATE=null;
	//private static String docList = null;
	@Override
	public void run()
	{
		String sessionID = "";
		
		

		try
		{
			DCCNotifyLog.setLogger();

			DCCNotifyLog.DCCNotifyLogger.debug("Connecting to Cabinet.");

			int configReadStatus = readConfig();

			DCCNotifyLog.DCCNotifyLogger.debug("configReadStatus "+configReadStatus);
			if(configReadStatus !=0)
			{
				DCCNotifyLog.DCCNotifyLogger.error("Could not Read Config Properties [DCCNotifyAPP]");
				return;
			}

			cabinetName = CommonConnection.getCabinetName();
			DCCNotifyLog.DCCNotifyLogger.debug("Cabinet Name: " + cabinetName);

			jtsIP = CommonConnection.getJTSIP();
			DCCNotifyLog.DCCNotifyLogger.debug("JTSIP: " + jtsIP);

			jtsPort = CommonConnection.getJTSPort();
			DCCNotifyLog.DCCNotifyLogger.debug("JTSPORT: " + jtsPort);

			queueID = NotifyAppConfigParamMap.get("queueID");
			DCCNotifyLog.DCCNotifyLogger.debug("QueueID: " + queueID);
			
			propDocsPath=NotifyAppConfigParamMap.get("DOCGENERATIONPATH");
			DCCNotifyLog.DCCNotifyLogger.debug("propDocsPath: "+propDocsPath);
			
			/*docList=NotifyAppConfigParamMap.get("COOLINGDOCS");
			DCCNotifyLog.DCCNotifyLogger.debug("docList: "+docList);*/
			
			fromMailID= NotifyAppConfigParamMap.get("fromMailID");
			DCCNotifyLog.DCCNotifyLogger.debug("fromMailID: "+fromMailID);
			
			toMailID=NotifyAppConfigParamMap.get("toMailID");
			DCCNotifyLog.DCCNotifyLogger.debug("toMailID: "+toMailID);
			
			mailSubject=NotifyAppConfigParamMap.get("mailSubject");
			DCCNotifyLog.DCCNotifyLogger.debug("mailSubject: "+mailSubject);
			
			MailStr=NotifyAppConfigParamMap.get("MailStr");
			DCCNotifyLog.DCCNotifyLogger.debug("MailStr: "+MailStr);
			
			 //Hold_AlternateCard - Added HRITIK 18.10.23 - to keep the cases of alternative cards on hold till app is published.
			Hold_AlternateCard=NotifyAppConfigParamMap.get("Hold_AlternateCard");
			DCCNotifyLog.DCCNotifyLogger.debug("Hold_AlternateCard: "+Hold_AlternateCard);
					
			socketConnectionTimeout=Integer.parseInt(NotifyAppConfigParamMap.get("MQ_SOCKET_CONNECTION_TIMEOUT"));
			DCCNotifyLog.DCCNotifyLogger.debug("SocketConnectionTimeOut: "+socketConnectionTimeout);

			integrationWaitTime=Integer.parseInt(NotifyAppConfigParamMap.get("INTEGRATION_WAIT_TIME"));
			DCCNotifyLog.DCCNotifyLogger.debug("IntegrationWaitTime: "+integrationWaitTime);

			sleepIntervalInMin=Integer.parseInt(NotifyAppConfigParamMap.get("SleepIntervalInMin"));
			DCCNotifyLog.DCCNotifyLogger.debug("SleepIntervalInMin: "+sleepIntervalInMin);
			
			//09042023
			DCC_WICREATION_DATE=NotifyAppConfigParamMap.get("DCC_WICREATION_DATE");
			DCCNotifyLog.DCCNotifyLogger.debug("DCC_WICREATION_DATE: "+DCC_WICREATION_DATE);


			sessionID = CommonConnection.getSessionID(DCCNotifyLog.DCCNotifyLogger, false);

			if(sessionID.trim().equalsIgnoreCase(""))
			{
				DCCNotifyLog.DCCNotifyLogger.debug("Could Not Connect to Server!");
			}
			else
			{
				DCCNotifyLog.DCCNotifyLogger.debug("Session ID found: " + sessionID);
				HashMap<String, String> socketDetailsMap = CommonMethods.socketConnectionDetails(cabinetName, jtsIP, jtsPort, sessionID);
				while (true) {
					DCCNotifyLog.setLogger();
					DCCNotifyLog.DCCNotifyLogger.debug("DCC Notify TO DEH ...123.");
					DCC_NotifyAppUtility(cabinetName, jtsIP, jtsPort, sessionID, queueID, socketConnectionTimeout, integrationWaitTime, socketDetailsMap);
					System.out.println("No More workitems to Process, Sleeping!");
					Thread.sleep(sleepIntervalInMin * 60 * 1000);
				}
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
			DCCNotifyLog.DCCNotifyLogger.error("Exception Occurred in DCCNotifyAPP : "+e);
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			DCCNotifyLog.DCCNotifyLogger.error("Exception Occurred in DCCNotifyAPP : "+result);
		}
	}

	@SuppressWarnings("unused")
	private void DCC_NotifyAppUtility(String cabinetName, String sJtsIp, String iJtsPort, String sessionId, String queueID, 
			int socketConnectionTimeOut, int integrationWaitTime, HashMap<String, String> socketDetailsMap)
	{
		final String ws_name="Sys_DEH_Notify";
		
		try
		{
			final HashMap<String, String> CheckGridDataMap = new HashMap<String, String>();
			//Validate Session ID
			sessionId  = CommonConnection.getSessionID(DCCNotifyLog.DCCNotifyLogger, false);

			if (sessionId == null || sessionId.equalsIgnoreCase("") || sessionId.equalsIgnoreCase("null"))
			{
				DCCNotifyLog.DCCNotifyLogger.error("Could Not Get Session ID "+sessionId);
				return;
			}

			//Fetch all Work-Items on given queueID.
			DCCNotifyLog.DCCNotifyLogger.debug("Fetching all Workitems on DCCNotifyAPP queue");
			System.out.println("Fetching all Workitems on CIF_Update_Initial queue");
			String fetchWorkitemListInputXML=CommonMethods.fetchWorkItemsInput(cabinetName, sessionId, queueID);
			DCCNotifyLog.DCCNotifyLogger.debug("InputXML for fetchWorkList Call: "+fetchWorkitemListInputXML);

			String fetchWorkitemListOutputXML= CommonMethods.WFNGExecute(fetchWorkitemListInputXML,sJtsIp,iJtsPort,1);

			DCCNotifyLog.DCCNotifyLogger.debug("WMFetchWorkList DCCNotifyAPP OutputXML: "+fetchWorkitemListOutputXML);

			XMLParser xmlParserFetchWorkItemlist = new XMLParser(fetchWorkitemListOutputXML);

			String fetchWorkItemListMainCode = xmlParserFetchWorkItemlist.getValueOf("MainCode");
			DCCNotifyLog.DCCNotifyLogger.debug("FetchWorkItemListMainCode: "+fetchWorkItemListMainCode);

			int fetchWorkitemListCount = Integer.parseInt(xmlParserFetchWorkItemlist.getValueOf("RetrievedCount"));
			DCCNotifyLog.DCCNotifyLogger.debug("RetrievedCount for WMFetchWorkList Call: "+fetchWorkitemListCount);

			DCCNotifyLog.DCCNotifyLogger.debug("Number of workitems retrieved on DCCNotifyAPP: "+fetchWorkitemListCount);

			System.out.println("Number of workitems retrieved on DCCNotifyAPP: "+fetchWorkitemListCount);

			if (fetchWorkItemListMainCode.trim().equals("0") && fetchWorkitemListCount > 0)
			{
				for(int i=0; i<fetchWorkitemListCount; i++)
				{
					String fetchWorkItemlistData=xmlParserFetchWorkItemlist.getNextValueOf("Instrument");
					fetchWorkItemlistData =fetchWorkItemlistData.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");

					DCCNotifyLog.DCCNotifyLogger.debug("Parsing <Instrument> in WMFetchWorkList OutputXML: "+fetchWorkItemlistData);
					XMLParser xmlParserfetchWorkItemData = new XMLParser(fetchWorkItemlistData);

					String processInstanceID=xmlParserfetchWorkItemData.getValueOf("ProcessInstanceId");
					DCCNotifyLog.DCCNotifyLogger.debug("Current ProcessInstanceID: "+processInstanceID);
					//processInstanceID="DCC-0000000833-process";
					DCCNotifyLog.DCCNotifyLogger.debug("Processing Workitem: "+processInstanceID);
					System.out.println("\nProcessing Workitem: "+processInstanceID);

					String WorkItemID=xmlParserfetchWorkItemData.getValueOf("WorkItemId");
					DCCNotifyLog.DCCNotifyLogger.debug("Current WorkItemID: "+WorkItemID);

					String entryDateTime=xmlParserfetchWorkItemData.getValueOf("EntryDateTime");
					DCCNotifyLog.DCCNotifyLogger.debug("Current EntryDateTime: "+entryDateTime);

					String ActivityName=xmlParserfetchWorkItemData.getValueOf("ActivityName");
					DCCNotifyLog.DCCNotifyLogger.debug("ActivityName: "+ActivityName);
					
					String ActivityID = xmlParserfetchWorkItemData.getValueOf("WorkStageId");
					DCCNotifyLog.DCCNotifyLogger.debug("ActivityID: "+ActivityID);
					String ActivityType = xmlParserfetchWorkItemData.getValueOf("ActivityType");
					DCCNotifyLog.DCCNotifyLogger.debug("ActivityType: "+ActivityType);
					String ProcessDefId = xmlParserfetchWorkItemData.getValueOf("RouteId");
					DCCNotifyLog.DCCNotifyLogger.debug("ProcessDefId: "+ProcessDefId);
					
					
					String decisionValue="";
					String Notify_app_input_xml="";
					
				    String DBQuery ="select  CIF,NTB,FIRCO_Flag,FircoUpdateAction,EFMS_Status,FTS_Ack_flg,Dectech_Decision,Decision,Final_Limit,Card_Limit,ADDITIONAL_DOCUMENT_REQUIRED,"
				    		+"prospect_id,NOTIFY_DEH_IDENTIFIER,Card_Product_Sub_Type," +
				    		"cast(isnull(Expense1,0) as int)+cast(isnull(Expense2,0) as int)+cast(isnull(Expense3,0) as int)+cast(isnull(Expense4,0) as int) as LifeStyleExpenditure," +
				    		"FinalTAI,DBR_lifeStyle_expenses,Output_TotalDeduction,Output_Stress_BufferAmt,cancel_in_cooling_period,Decline_reason,Product,Nationality,"
				    		+"FATCA_Tin_Number,IBPS_Expiry,IsVirtualCardActive,CAST(Introduction_Date AS DATE) AS Introduction_Date,EFR_NSTP,FirstName,MiddleName,LastName,"
				    		+"Expense1,Expense2,Expense3,Expense4,employercode,Employer_Name,FinalDBR,FATCA_Tin_Number,Fatca,OutputAlternateCard,OUTPUT_ELIGIBLE_CARD,Tin_reason  from NG_DCC_EXTTABLE with(nolock)"
				    		+ "where WI_name='"+processInstanceID+"'";
				   
				    String extTabDataIPXML =CommonMethods.apSelectWithColumnNames(DBQuery, cabinetName, CommonConnection.getSessionID(DCCNotifyLog.DCCNotifyLogger, false));
				    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
				    String extTabDataOPXML = CommonMethods.WFNGExecute(extTabDataIPXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 1);
				    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataOPXML: " + extTabDataOPXML);
				   
				    XMLParser xmlParserData = new XMLParser(extTabDataOPXML);
				    
				    String iTotalrec = xmlParserData.getValueOf("TotalRetrieved");
				   
				    if (xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec!=null && !"".equalsIgnoreCase(iTotalrec) && Integer.parseInt(iTotalrec) > 0)
			        {
				    	String CIF = xmlParserData.getValueOf("CIF");
				    	String NOTIFY_DEH_IDENTIFIER=xmlParserData.getValueOf("NOTIFY_DEH_IDENTIFIER");
				    	String prospect_id=xmlParserData.getValueOf("prospect_id");
				    	String Final_Limit=xmlParserData.getValueOf("Final_Limit");
				    	String Card_Limit=xmlParserData.getValueOf("Card_Limit");
				    	String ADDITIONAL_DOCUMENT_REQUIRED=xmlParserData.getValueOf("ADDITIONAL_DOCUMENT_REQUIRED");
				    	String Decision=xmlParserData.getValueOf("Decision");
				    	String cardType =xmlParserData.getValueOf("Card_Product_Sub_Type");
				    	String firco=xmlParserData.getValueOf("FIRCO_Flag");
				    	String fircoAction=xmlParserData.getValueOf("FircoUpdateAction");
				    	String fts=xmlParserData.getValueOf("FTS_Ack_flg");
				    	String efms=xmlParserData.getValueOf("EFMS_Status");
				    	String DectechDecision=xmlParserData.getValueOf("Dectech_Decision");
				    	String LIFESTYLE=xmlParserData.getValueOf("LifeStyleExpenditure");
						String AECB_MONTHLY=xmlParserData.getValueOf("Output_TotalDeduction");
						String STRESS=xmlParserData.getValueOf("Output_Stress_BufferAmt");
						String ASS_INCOME=xmlParserData.getValueOf("FinalTAI");
						String AFF_RATIO=xmlParserData.getValueOf("DBR_lifeStyle_expenses");
						String cancel_in_cooling_period=xmlParserData.getValueOf("cancel_in_cooling_period");
						String Decline_reason=xmlParserData.getValueOf("Decline_reason");
						String IBPS_Expiry=xmlParserData.getValueOf("IBPS_Expiry");
						String IsVirtualCardActive = xmlParserData.getValueOf("IsVirtualCardActive");
						String Introduction_Date = xmlParserData.getValueOf("Introduction_Date");
						String EFR_NSTP = xmlParserData.getValueOf("EFR_NSTP");
						String FirstName = xmlParserData.getValueOf("FirstName");
						String MiddleName = xmlParserData.getValueOf("MiddleName");
						String LastName = xmlParserData.getValueOf("LastName");
						String FullName = "";
						String Expense1 = xmlParserData.getValueOf("Expense1");
						String Expense2 = xmlParserData.getValueOf("Expense2");
						String Expense3 = xmlParserData.getValueOf("Expense3");
						String Expense4 = xmlParserData.getValueOf("Expense4");
						String employercode = xmlParserData.getValueOf("employercode");
						String Employer_Name = xmlParserData.getValueOf("Employer_Name");
						String FinalDBR = xmlParserData.getValueOf("FinalDBR");
						String FATCA_Tin_Number = xmlParserData.getValueOf("FATCA_Tin_Number");
						String Fatca = xmlParserData.getValueOf("Fatca");
						String Tin_reason= xmlParserData.getValueOf("Tin_reason");
						String NTB=xmlParserData.getValueOf("NTB");
						// Hritik 28.9.23
						String OutputAlternateCard = xmlParserData.getValueOf("OutputAlternateCard");
						String OUTPUT_ELIGIBLE_CARD = xmlParserData.getValueOf("OUTPUT_ELIGIBLE_CARD");
						if(!("").equalsIgnoreCase(MiddleName)){
							 FullName = FirstName + " " + MiddleName + " " + LastName;
						}
						else if(("").equalsIgnoreCase(MiddleName)){
							 FullName = FirstName + " " + LastName;
						}   	
				    	
				    	DCCNotifyLog.DCCNotifyLogger.debug("NOTIFY_DEH_IDENTIFIER: "+NOTIFY_DEH_IDENTIFIER);
				    	DCCNotifyLog.DCCNotifyLogger.debug("prospect_id: "+prospect_id);
				    	DCCNotifyLog.DCCNotifyLogger.debug("Final_Limit: "+Final_Limit);
				    	DCCNotifyLog.DCCNotifyLogger.debug("ADDITIONAL_DOCUMENT_REQUIRED: "+ADDITIONAL_DOCUMENT_REQUIRED);
				    	DCCNotifyLog.DCCNotifyLogger.debug("Decision: "+Decision);
				    	
				    	if(NOTIFY_DEH_IDENTIFIER!=null && !"".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
				    	{
				    		if("FIRCO".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
				    		{ // WS from Firco
				    			/*DCCNotifyLog.DCCNotifyLogger.debug("Inside FIRCO: "+NOTIFY_DEH_IDENTIFIER);
			    				String query = "select Top 1 decision_date_time from NG_DCC_GR_DECISION_HISTORY with(nolock) where workstep='Firco' and wi_name = '" + processInstanceID + "' order by decision_date_time desc";
			    				String ActdateIPXML =CommonMethods.apSelectWithColumnNames(query, cabinetName, CommonConnection.getSessionID(DCCNotifyLog.DCCNotifyLogger, false));
							    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataIPXML: " + ActdateIPXML);
							    String ActdateOPXML = CommonMethods.WFNGExecute(ActdateIPXML,jtsIP, jtsPort, 1);
							    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataOPXML: " + ActdateOPXML);*/
							    
							    String actionDate="";
				    			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//yyyy-MM-dd HH:MM:ss
				    			actionDate=simpleDateFormat.format(new Date());
							    // using xml parser to pass the output data in desired format 
							    //XMLParser xmlParserActData = new XMLParser(ActdateOPXML);
							    // total values retrieved > 0 is a check
							   // int iTotalrecAct = Integer.parseInt(xmlParserActData.getValueOf("TotalRetrieved"));
//							   / if (xmlParserActData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrecAct > 0)
						      
							    //	String actionDateTime=xmlParserActData.getValueOf("decision_date_time");
							    	String additioal_docs_details = "";
							    	/*if(actionDateTime!=null && !"".equalsIgnoreCase(actionDateTime))
							    	{*/
							    		//String actionDate= CommonMethods.parseDate(actionDateTime,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy");//2022-07-22 18:28:37.000
							    		DCCNotifyLog.DCCNotifyLogger.debug("Inside FIRCO actionDate : "+actionDate);
							    		// documents for notify_app - AdditionalDocumentDetails
									    if (ADDITIONAL_DOCUMENT_REQUIRED != null && "Y".equalsIgnoreCase(ADDITIONAL_DOCUMENT_REQUIRED)) 
									    {
									    	// document status SHOULD BE PPENDING
								            String DBQuery_doc ="select case when document_name='Other_Document' then document_remarks else document_name end as document_name,document_month, document_year,document_remarks from NG_DCC_GR_ADDITIONAL_DOCUMENT with(nolock) where WI_name = '" + processInstanceID + "' and document_status='Pending'";
								            
								            String docDataIPXML =CommonMethods.apSelectWithColumnNames(DBQuery_doc, cabinetName, CommonConnection.getSessionID(DCCNotifyLog.DCCNotifyLogger, false));
								    	    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataIPXML: " + docDataIPXML);
								    	    String docDataOPXML = CommonMethods.WFNGExecute(docDataIPXML, jtsIP, jtsPort, 1);
								    	    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataOPXML: " + docDataOPXML);
								            
								    	    XMLParser xmlParserDocData = new XMLParser(docDataOPXML);
								            
								    	    int iTotalrec1 = Integer.parseInt(xmlParserDocData.getValueOf("TotalRetrieved"));
								    	    
								    	    if (xmlParserDocData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec1 > 0)
								            {
												for (int j = 0; j < iTotalrec1; j++)
												{
													String fetchlistData=xmlParserDocData.getNextValueOf("Record");
													fetchlistData =fetchlistData.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");
						
													DCCNotifyLog.DCCNotifyLogger.debug("Parsing <Instrument> in WMFetchWorkList OutputXML: "+fetchWorkItemlistData);
													XMLParser xmlParserfetchData = new XMLParser(fetchlistData);
													
													String monthName=xmlParserfetchData.getValueOf("document_month");
													String year=xmlParserfetchData.getValueOf("document_year");
													String docName=xmlParserfetchData.getValueOf("document_name");
													
													if(monthName!=null && !"".equalsIgnoreCase(monthName) && year!=null && !"".equalsIgnoreCase(year)&& monthName!=null && !"".equalsIgnoreCase(monthName))
													{
														String monthYear=getMonthNumber(monthName)+"-"+year;
														additioal_docs_details += "\t\t" +"<DocNameList>"+ "\n" +
																"\t\t\t" +"<DocumentName>"+xmlParserfetchData.getValueOf("document_name")+"</DocumentName>"+ "\n" +
																"\t\t\t" +"<MonthYear>"+monthYear+"</MonthYear>"+ "\n" +
																"\t\t" +"</DocNameList>";
													}
												}
									    }
									    
									    String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DCC_Integration")
						    		    		.append(System.getProperty("file.separator")).append("FIRCO_DOCS_REQD.txt").toString();
									    BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
							    		
							    		StringBuilder sb=new StringBuilder();
							    		String line=sbf.readLine();
							    		while(line!=null)
							    		{
							    			sb.append(line);
							    			sb.append(System.lineSeparator());
							    			line=sbf.readLine();
							    		}
							    		Notify_app_input_xml=sb.toString();
							    		if(actionDate==null)
							    			actionDate="";
							    		Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
							    		Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
							    		Notify_app_input_xml=Notify_app_input_xml.replace("#DOCREQUIRED#", ADDITIONAL_DOCUMENT_REQUIRED);
							    		Notify_app_input_xml=Notify_app_input_xml.replace("#DOCUMENTLIST#", additioal_docs_details);
							    		Notify_app_input_xml=Notify_app_input_xml.replace("#ACTIONEDDATE#", actionDate);
							    		
							    		
							    		
							    		DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml FIRCO: " + Notify_app_input_xml);
							    		
									    }	      
			    		}
				    	//2703 Salary Document notify-- Kamran
				    	else if("System_SalaryDocReq".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER)){
				    		String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DCC_Integration")
			    		    		.append(System.getProperty("file.separator")).append("SALARY_DOC_REQUIRED.txt").toString();
								    BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
						    		
						    		StringBuilder sb=new StringBuilder();
						    		String line=sbf.readLine();
						    		while(line!=null)
						    		{
						    			sb.append(line);
						    			sb.append(System.lineSeparator());
						    			line=sbf.readLine();
						    		}
						    		String actionDate="";
						    		String salary_docs_details = "";
					    			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//yyyy-MM-dd HH:MM:ss
					    			actionDate=simpleDateFormat.format(new Date());
					    			
						    		
						    		String DBQuery_doc ="select Salary_Doc_Name as Salary_Doc_Name, "
						    				+ "document_month as document_month, document_year as document_year from ng_dcc_gr_salary_doc where WI_name = '" + processInstanceID + "'";
						    		String docDataIPXML =CommonMethods.apSelectWithColumnNames(DBQuery_doc, cabinetName, CommonConnection.getSessionID(DCCNotifyLog.DCCNotifyLogger, false));
							    	    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataIPXML: " + docDataIPXML);
							    	    String docDataOPXML = CommonMethods.WFNGExecute(docDataIPXML, jtsIP, jtsPort, 1);
							    	    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataOPXML: " + docDataOPXML);
							            
							    	    XMLParser xmlParserDocData = new XMLParser(docDataOPXML);
							            
							    	    int iTotalrec1 = Integer.parseInt(xmlParserDocData.getValueOf("TotalRetrieved"));
							    	    if (xmlParserDocData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec1 > 0)
							            {
											for (int j = 0; j < iTotalrec1; j++)
											{
												String fetchlistData=xmlParserDocData.getNextValueOf("Record");
												fetchlistData =fetchlistData.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");
					
												DCCNotifyLog.DCCNotifyLogger.debug("Parsing <Instrument> in WMFetchWorkList OutputXML: "+fetchWorkItemlistData);
												XMLParser xmlParserfetchData = new XMLParser(fetchlistData);
												
												String docName=xmlParserfetchData.getValueOf("Salary_Doc_Name");
												String monthName=xmlParserfetchData.getValueOf("document_month");
												String year=xmlParserfetchData.getValueOf("document_year");
												String monthYear=getMonthNumber(monthName)+"-"+year;
												salary_docs_details += "\t\t" +"<DocNameList>"+ "\n" +
														"\t\t\t" +"<DocumentName>"+xmlParserfetchData.getValueOf("Salary_Doc_Name")+"</DocumentName>"+ "\n" +
														"\t\t\t" +"<MonthYear>"+monthYear+"</MonthYear>"+ "\n" +
														"\t\t" +"</DocNameList>";
											}
							            }
							    	    

						    			Notify_app_input_xml=sb.toString();
						    			Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
							    		Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
							    		Notify_app_input_xml=Notify_app_input_xml.replace("#DOCREQUIRED#", "Y");
							    		Notify_app_input_xml=Notify_app_input_xml.replace("#ACTIONEDDATE#", actionDate);
							    		Notify_app_input_xml=Notify_app_input_xml.replace("#DOCUMENTLIST#", salary_docs_details);
							    		DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml SALARY DOC REQ: " + Notify_app_input_xml);
				    	}
				    	//2703 End
				    	//3003 Salary Document RM Uploaded notify-- Kamran
				    	else if("System_SalaryDoc_RM_Uploaded".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER)){
				    		String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DCC_Integration")
			    		    		.append(System.getProperty("file.separator")).append("SALARY_DOCS_UPLOADED_RM.txt").toString();
								    BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
						    		
						    		StringBuilder sb=new StringBuilder();
						    		String line=sbf.readLine();
						    		while(line!=null)
						    		{
						    			sb.append(line);
						    			sb.append(System.lineSeparator());
						    			line=sbf.readLine();
						    		}
						    		String actionDate="";
						    		String salary_docs_details = "";
					    			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//yyyy-MM-dd HH:MM:ss
					    			actionDate=simpleDateFormat.format(new Date());
					    				Notify_app_input_xml=sb.toString();
						    			Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
							    		Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
							    		Notify_app_input_xml=Notify_app_input_xml.replace("#ACTIONEDDATE#", actionDate);
							    		DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml SALARY_DOCS_UPLOADED_RM: " + Notify_app_input_xml);
				    	}
				    	//2703 End
				    		
					    //0505 Firco Document RM Uploaded notify-- Kamran
					    else if("System_FircoDoc_RM_Uploaded".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER)){
					    		String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DCC_Integration")
				    		    		.append(System.getProperty("file.separator")).append("FIRCO_DOCS_UPLOADED_RM.txt").toString();
									    BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
							    		
							    		StringBuilder sb=new StringBuilder();
							    		String line=sbf.readLine();
							    		while(line!=null)
							    		{
							    			sb.append(line);
							    			sb.append(System.lineSeparator());
							    			line=sbf.readLine();
							    		}
							    		String actionDate="";
							    		String salary_docs_details = "";
						    			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//yyyy-MM-dd HH:MM:ss
						    			actionDate=simpleDateFormat.format(new Date());
						    				Notify_app_input_xml=sb.toString();
							    			Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
								    		Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
								    		Notify_app_input_xml=Notify_app_input_xml.replace("#ACTIONEDDATE#", actionDate);
								    		DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml FIRCO_DOCS_UPLOADED_RM: " + Notify_app_input_xml);
					    	}
					    //0505 End	
				    	else if("Decline_Prospect".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
				    		{ // WS from system assign cif
					    		String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DCC_Integration")
		    		    		.append(System.getProperty("file.separator")).append("DECLINE_PROS_CLOSURE.txt").toString();
							    BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
					    		
					    		StringBuilder sb=new StringBuilder();
					    		String line=sbf.readLine();
					    		while(line!=null)
					    		{
					    			sb.append(line);
					    			sb.append(System.lineSeparator());
					    			line=sbf.readLine();
					    		}
					    		String actionDate="";
				    			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//yyyy-MM-dd HH:MM:ss
				    			actionDate=simpleDateFormat.format(new Date());
				    			
				    			
					    		Notify_app_input_xml=sb.toString();
					    		Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
					    		Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
					    		Notify_app_input_xml=Notify_app_input_xml.replace("#CLOSE_PROSPECT#", "Y");
					    		Notify_app_input_xml=Notify_app_input_xml.replace("#DecisionDate#", actionDate);
					    		
					    		if("CB".equalsIgnoreCase(firco))
					    		{
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#FIRCO_DECLINE#", "Y");
					    		}
					    		else if("N".equalsIgnoreCase(firco)||"FP".equalsIgnoreCase(firco))
					    		{
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#FIRCO_DECLINE#", "N");
					    		}
					    		else
					    		{
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#FIRCO_DECLINE#", "");
					    		}
					    		
					    		if("D".equalsIgnoreCase(DectechDecision))
					    		{
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECTCH_DECLINE#", "Y");
					    		}
					    		else if("A".equalsIgnoreCase(DectechDecision) || "R".equalsIgnoreCase(DectechDecision))
					    		{
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECTCH_DECLINE#", "N");
					    		}
					    		else
					    		{
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECTCH_DECLINE#", "");
					    		}
					    		if("Confirmed Fraud".equalsIgnoreCase(efms) || "Negative".equalsIgnoreCase(efms)) //To be changes as part of EFMS change 22112023 PDSC-1073
					    		{
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#EFMS_DECLINE#", "Y");
					    		}
					    		else if("Non-Alerted".equalsIgnoreCase(efms) || "Closed".equalsIgnoreCase(efms))
					    		{
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#EFMS_DECLINE#", "N");
					    		}
					    		else
					    		{
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#EFMS_DECLINE#", "");
					    		}
					    		if(LIFESTYLE!=null)
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#LIFESTYLE#", LIFESTYLE);
					    		else
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#LIFESTYLE#", "");
					    		if(AECB_MONTHLY!=null)
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#AECB_MONTHLY#", AECB_MONTHLY);
					    		else
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#AECB_MONTHLY#", "");
					    		if(STRESS!=null)
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#STRESS#", STRESS);
					    		else
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#STRESS#", "");
					    		if(ASS_INCOME!=null)
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#ASS_INCOME#", ASS_INCOME);
					    		else
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#ASS_INCOME#","");
					    		if(AFF_RATIO!=null)
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#AFF_RATIO#",AFF_RATIO);
					    		else
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#AFF_RATIO#","");
					    		if(cardType!=null)
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARD_TYPE#", cardType);
					    		else
					    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARD_TYPE#", "");
					    		
					    		Notify_app_input_xml=Notify_app_input_xml.replace("#DEDUCTION#", "");
					    		Notify_app_input_xml=Notify_app_input_xml.replace("#DISPOSE#", "");
					    		
					    		DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml DECLINE_PROS_CLOSURE: " + Notify_app_input_xml);
					    		
					    		
				    		}
				    	else if("Sys_PrimeAWB_Gen".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
			    		{ // WS from sys awb prime 
			    			DCCNotifyLog.DCCNotifyLogger.debug("IBPS Card dispatch");
			    			String actionDate="";
			    			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//yyyy-MM-dd HH:MM:ss
			    			actionDate=simpleDateFormat.format(new Date());
			    			/*String IBPS_FINAL_APPROVAL_query = "select Top 1 decision_date_time from NG_DCC_GR_DECISION_HISTORY with(nolock) where workstep='' and wi_name = '" + processInstanceID + "' order by decision_date_time desc";
		    				String IBPS_FINAL_APPROVAL_ActdateIPXML =CommonMethods.apSelectWithColumnNames(IBPS_FINAL_APPROVAL_query, cabinetName, CommonConnection.getSessionID(DCCNotifyLog.DCCNotifyLogger, false));
						    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataIPXML: " + IBPS_FINAL_APPROVAL_ActdateIPXML);
						    String IBPS_FINAL_APPROVAL_ActdateOPXML = CommonMethods.WFNGExecute(IBPS_FINAL_APPROVAL_ActdateIPXML,jtsIP, jtsPort, 1);
						    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataOPXML: " + IBPS_FINAL_APPROVAL_ActdateOPXML);
						    XMLParser xmlParser_IBPS_FINAL_APPROVAL = new XMLParser(IBPS_FINAL_APPROVAL_ActdateOPXML);
						    int iTotalrec_IBPS_FINAL_APPROVAL = Integer.parseInt(xmlParser_IBPS_FINAL_APPROVAL.getValueOf("TotalRetrieved"));
						    if (xmlParser_IBPS_FINAL_APPROVAL.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec_IBPS_FINAL_APPROVAL > 0)
					        {
						    	actionDateTime=xmlParser_IBPS_FINAL_APPROVAL.getValueOf("decision_date_time");
							    if(actionDateTime!=null && !"".equalsIgnoreCase(actionDateTime))
						    	{
							    	actionDate= CommonMethods.parseDate(actionDateTime,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy");//2022-07-22 18:28:37.000
						    	}
					        }
						    DCCNotifyLog.DCCNotifyLogger.debug("xmlParser_IBPS_FINAL_APPROVAL actionDate: " +actionDate);*/   
				    		DCCNotifyLog.DCCNotifyLogger.debug("COOLING actionDate: " +actionDate);
			    			
			    			
			    			String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DCC_Integration")
	    		    		.append(System.getProperty("file.separator")).append("CARD_DISPATCH.txt").toString();
						    BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
						    
				    		
				    		StringBuilder sb=new StringBuilder();
				    		String line=sbf.readLine();
				    		while(line!=null)
				    		{
				    			sb.append(line);
				    			sb.append(System.lineSeparator());
				    			line=sbf.readLine();
				    		}
				    		Notify_app_input_xml=sb.toString();
				    		
				    		//10042023 --Fetching Itroduction Date 
				    		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
				    		Date Date_Dcc_WiCreationDate = sdFormat.parse(Introduction_Date); //2022-02-02 d1
				    		Date Date_Config_WiCreationDate = sdFormat.parse(NotifyAppConfigParamMap.get("DCC_WICREATION_DATE")); //2023-04-01 d2
				    		
				    		DCCNotifyLog.DCCNotifyLogger.debug("Date_Dcc_WiCreationDate CARD_DISPATCH: " + Date_Dcc_WiCreationDate);
				    		DCCNotifyLog.DCCNotifyLogger.debug("Date_Config_WiCreationDate CARD_DISPATCH: " + Date_Config_WiCreationDate);
				    		
				    		if(Date_Config_WiCreationDate.compareTo(Date_Dcc_WiCreationDate)>0)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#ISVIRTUALCARDACTIVE#", "N");
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#ISVIRTUALCARDACTIVE#", IsVirtualCardActive);
				    		if(processInstanceID!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", "");
				    		if(prospect_id!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", "");
				    		if(actionDate!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECISIONDATE#",actionDate);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECISIONDATE#","");
				    		if(Final_Limit!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARDLIMIT#", Final_Limit);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARDLIMIT#", "");
				    		DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml CARD_DISPATCH: " + Notify_app_input_xml);
			    		}
				    	else if("Card_Ops_Approve".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
			    		{ // WS from cards 
			    			DCCNotifyLog.DCCNotifyLogger.debug("INSIDE COOLING");
			    			
			    			
			    			String actionDate="";
			    			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//YYYY-MM-DD
			    			actionDate=simpleDateFormat.format(new Date());
						    
				    		DCCNotifyLog.DCCNotifyLogger.debug("COOLING actionDate: " +actionDate);
			    			
				    		// Hritik - 140723 -  PSCC-1510 Start
				    		String fileLocation="";
				    		
				    		if("false".equalsIgnoreCase(NTB)){
				    			fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator"))
				    			.append("DCC_Integration").append(System.getProperty("file.separator")).append("COOLING_ETB.txt").toString();
				    		}
				    		else{		    		
				    			fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator"))
				    			.append("DCC_Integration").append(System.getProperty("file.separator")).append("COOLING.txt").toString();
				     		}
				    		// End
				    		
				    		BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
				    		StringBuilder sb=new StringBuilder();
				    		String line=sbf.readLine();
				    		while(line!=null)
				    		{
				    			sb.append(line);
				    			sb.append(System.lineSeparator());
				    			line=sbf.readLine();
				    		}
				    		Notify_app_input_xml=sb.toString();
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#YYYY-MM-DD#", actionDate);
				    		
				    		if(actionDate!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#YYYY-MM-DD#", actionDate);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#YYYY-MM-DD#", "");

				    		if(LIFESTYLE!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#LIFESTYLE#", LIFESTYLE);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#LIFESTYLE#", "");
				    		if(AECB_MONTHLY!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AECB_MONTHLY#", AECB_MONTHLY);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AECB_MONTHLY#", "");
				    		if(STRESS!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#STRESS#", STRESS);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#STRESS#", "");
				    		if(ASS_INCOME!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#ASS_INCOME#", ASS_INCOME);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#ASS_INCOME#","");
				    		if(AFF_RATIO!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AFF_RATIO#",AFF_RATIO);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AFF_RATIO#","");
				    		if(cardType!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARD_TYPE#", cardType);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARD_TYPE#", "");
				    		
				    		if(Final_Limit!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARDLIMIT#", Final_Limit);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARDLIMIT#", "");
				    		
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#DEDUCTION#", "");
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#DISPOSE#", "");
				    		
				    		
				    		String Product=xmlParserData.getValueOf("Product");
							String nationality=xmlParserData.getValueOf("Nationality");
				        	String TIN=xmlParserData.getValueOf("FATCA_Tin_Number");
				        	
							String docList="Customer_Consent_Form_Signed";
							if("ISL".equalsIgnoreCase(Product))
							{
								docList=docList+",MRBH_Agency_Agreement_Signed";
							}
							if("US".equalsIgnoreCase(nationality))
							{
								docList=docList+",W-9_Form_Signed";
							}
							else if(!"US".equalsIgnoreCase(nationality) && TIN!=null && !"".equalsIgnoreCase(TIN))
							{
								docList=docList+",W-8_Form_Signed";
							}
							DCCNotifyLog.DCCNotifyLogger.debug("COOLING Doc List : " +docList);
				    		if(docList!= null && downloadAttachDocuments(processInstanceID,docList)!=null)//&& downloadAttachDocuments(processInstanceID,docList)!=null
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#COOL_DOC_LIST#", docList);
				    		}
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#COOL_DOC_LIST#", "");
				    		
				    		//Kamran 01062023
				    		String PersonalDetails = "";
				    		
				    		if(!"Y".equalsIgnoreCase(EFR_NSTP)){
								PersonalDetails="";
								DCCNotifyLog.DCCNotifyLogger.debug("Cooling Period EFR_NSTP: " +EFR_NSTP);
						    }
							else if("Y".equalsIgnoreCase(EFR_NSTP)){
								PersonalDetails = PersonalDetails + "\n<FirstName>"+FirstName.trim()+"</FirstName>\n";
								PersonalDetails = PersonalDetails + "<MiddleName>"+MiddleName.trim()+"</MiddleName>\n";
								PersonalDetails = PersonalDetails + "<LastName>"+LastName.trim()+"</LastName>\n";
								PersonalDetails = PersonalDetails + "<FullName>"+FullName+"</FullName>\n";
							}
				    		
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#PERSONAL_DETAILS#", PersonalDetails);
				    		
				    		DCCNotifyLog.DCCNotifyLogger.debug("COOLING Notify_app_input_xml : " +Notify_app_input_xml);
			    		}
				    	else if("Card_Ops_Reject".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER)||"Courier_Reject".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER)||"CP_Reject".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
			    		{ // WS from card blck || 
				    		
				    		String actionDate="";
			    			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//YYYY-MM-DD
			    			actionDate=simpleDateFormat.format(new Date());
			    			// Hritik - 140723 -  PSCC-1510 - Start
			    			String fileLocation = "";
			    			
			    			if("false".equalsIgnoreCase(NTB)){
				    			fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator"))
				    			.append("DCC_Integration").append(System.getProperty("file.separator")).append("CANCEL_PROS_CLOSURE_ETB.txt").toString();
				    		}
				    		else{		    		
				    			fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator"))
				    			.append("DCC_Integration").append(System.getProperty("file.separator")).append("CANCEL_PROS_CLOSURE.txt").toString();
				     		}
			    			// End
				    		BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
			    				    					    		
				    		StringBuilder sb=new StringBuilder();
				    		String line=sbf.readLine();
				    		while(line!=null)
				    		{
				    			sb.append(line);
				    			sb.append(System.lineSeparator());
				    			line=sbf.readLine();
				    		}
				    		Notify_app_input_xml=sb.toString();
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#CLOSE_PROSPECT#", "Y");
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#DecisionDate#", actionDate);
				    		
				    		if("Courier_Reject".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#DEL_CLOSEURE#", "Y");
				    		}
				    		else
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#DEL_CLOSEURE#", "N");
				    		}
				    		
				    		if(cancel_in_cooling_period!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CANCEL_COOLING#", cancel_in_cooling_period);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CANCEL_COOLING#", "");
				    		if(Decline_reason!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECLINE_REASON#", Decline_reason);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECLINE_REASON#", "");
				    		if(LIFESTYLE!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#LIFESTYLE#", LIFESTYLE);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#LIFESTYLE#", "");
				    		if(AECB_MONTHLY!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AECB_MONTHLY#", AECB_MONTHLY);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AECB_MONTHLY#", "");
				    		if(STRESS!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#STRESS#", STRESS);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#STRESS#", "");
				    		if(ASS_INCOME!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#ASS_INCOME#", ASS_INCOME);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#ASS_INCOME#","");
				    		if(AFF_RATIO!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AFF_RATIO#",AFF_RATIO);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AFF_RATIO#","");
				    		if(cardType!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARD_TYPE#", cardType);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARD_TYPE#", "");
				    		
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#DEDUCTION#", "");
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#DISPOSE#", "");
				    		
				    		DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml CANCEL_PROS_CLOSURE: " + Notify_app_input_xml);
				    	}
				    	else if("Card_Ops_Refer".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
			    		{ // WS from cards ops
				    		String actionDate="";
			    			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//YYYY-MM-DD
			    			actionDate=simpleDateFormat.format(new Date());
				    		String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DCC_Integration")
			    		    		.append(System.getProperty("file.separator")).append("RESCHEDULE_DOCS.txt").toString();
								    BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
						    		
				    		StringBuilder sb=new StringBuilder();
				    		String line=sbf.readLine();
				    		while(line!=null)
				    		{
				    			sb.append(line);
				    			sb.append(System.lineSeparator());
				    			line=sbf.readLine();
				    		}
				    		Notify_app_input_xml=sb.toString();
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
				    		if(actionDate!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#dd-mm-yyyy#", actionDate);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#dd-mm-yyyy#", "");
			    		}
			    		else if("Sys_Limit_Increase".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
			    		{	// WS from sys limit inc
			    			DCCNotifyLog.DCCNotifyLogger.debug("INSIDE LIMIT_INCREASE");
			    			
			    			
			    			String actionDate="";
			    			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//yyyy-MM-dd HH:MM:ss
			    			actionDate=simpleDateFormat.format(new Date());
			    			/*String LIMIT_INCREASE_query = "select Top 1 decision_date_time from NG_DCC_GR_DECISION_HISTORY with(nolock) where workstep='' and wi_name = '" + processInstanceID + "' order by decision_date_time desc";
		    				String LIMIT_INCREASE_ActdateIPXML =CommonMethods.apSelectWithColumnNames(LIMIT_INCREASE_query, cabinetName, CommonConnection.getSessionID(DCCNotifyLog.DCCNotifyLogger, false));
						    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataIPXML: " + LIMIT_INCREASE_ActdateIPXML);
						    String LIMIT_INCREASE_ActdateOPXML = CommonMethods.WFNGExecute(LIMIT_INCREASE_ActdateIPXML,jtsIP, jtsPort, 1);
						    DCCNotifyLog.DCCNotifyLogger.debug("extTabDataOPXML: " + LIMIT_INCREASE_ActdateOPXML);
						    XMLParser xmlParser_LIMIT_INCREASE = new XMLParser(LIMIT_INCREASE_ActdateOPXML);
						    int iTotalrec_LIMIT_INCREASE = Integer.parseInt(xmlParser_LIMIT_INCREASE.getValueOf("TotalRetrieved"));
						    if (xmlParser_LIMIT_INCREASE.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec_LIMIT_INCREASE > 0)
					        {
						    	actionDateTime=xmlParser_LIMIT_INCREASE.getValueOf("decision_date_time");
							    if(actionDateTime!=null && !"".equalsIgnoreCase(actionDateTime))
						    	{
							    	actionDate= CommonMethods.parseDate(actionDateTime,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy");//2022-07-22 18:28:37.000
						    	}
					        }*/
				    		DCCNotifyLog.DCCNotifyLogger.debug("LIMIT_INCREASE actionDate: " +actionDate);
			    			String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DCC_Integration")
	    		    		.append(System.getProperty("file.separator")).append("LIMIT_INCREASE.txt").toString();
						    BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
				    		
				    		StringBuilder sb=new StringBuilder();
				    		String line=sbf.readLine();
				    		while(line!=null)
				    		{
				    			sb.append(line);
				    			sb.append(System.lineSeparator());
				    			line=sbf.readLine();
				    		}
				    		//eror
				    		//vinayak prime 4 chnage start
				    		String Product="";
				    		String Query1="select Product from NG_DCC_EXTTABLE with(nolock) where Wi_Name='" + processInstanceID + "'";
				    		String exttableinpxml =CommonMethods.apSelectWithColumnNames(Query1, cabinetName, sessionId);
				    		DCCNotifyLog.DCCNotifyLogger.debug("extTabDataIPXML: " + exttableinpxml);
							String extTabDataOPtXML = CommonMethods.WFNGExecute(extTabDataIPXML,sJtsIp, iJtsPort, 1);
							 DCCNotifyLog.DCCNotifyLogger.debug("extTabDataOPXML: " + extTabDataOPtXML);
							// using xml parser to pass the output data in desired format 
						    XMLParser xmlParserDataquery1 = new XMLParser(extTabDataOPtXML);
						    // total values retrieved > 0 is a check
						    int iTotalrecvalue = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));
						   // Main code we get if the ap select call is triggered success.
						    if (xmlParserDataquery1.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrecvalue > 0)
					        {
						    	String xmlDataExtTab = xmlParserDataquery1.getNextValueOf("Record");
					            xmlDataExtTab = xmlDataExtTab.replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");					           
					            XMLParser xmlGetWIDetails = new XMLParser(xmlDataExtTab);
					            Product = xmlGetWIDetails.getValueOf("Product");
					            DCCNotifyLog.DCCNotifyLogger.debug("Product: " + Product);
					        }
						    
						    String TransactionNo ="";
				    		String Query2="select TransactionNo from NG_DCC_MURABAHA_RESPONSE_DATA where WI_NAME='" + processInstanceID + "'";
				    		String murabatableinpxml =CommonMethods.apSelectWithColumnNames(Query2, cabinetName, sessionId);
				    		DCCNotifyLog.DCCNotifyLogger.debug("extTabDataIPXML: " + murabatableinpxml);
							String murabatableoptxml = CommonMethods.WFNGExecute(murabatableinpxml,sJtsIp, iJtsPort, 1);
							 DCCNotifyLog.DCCNotifyLogger.debug("extTabDataOPXML: " + murabatableoptxml);
							// using xml parser to pass the output data in desired format 
						    XMLParser xmlParserDataquery2 = new XMLParser(murabatableoptxml);
						    // total values retrieved > 0 is a check
						    int iTotalrecvalue2 = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));
						   // Main code we get if the ap select call is triggered success.
						    if (xmlParserDataquery2.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrecvalue2 > 0)
					        {
						    	String xmlDataExtTab = xmlParserDataquery2.getNextValueOf("Record");
					            xmlDataExtTab = xmlDataExtTab.replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");
					            XMLParser xmlGetWIDetails = new XMLParser(xmlDataExtTab);
					            TransactionNo = xmlGetWIDetails.getValueOf("TransactionNo");
					            DCCNotifyLog.DCCNotifyLogger.debug("TransactionNo: " + TransactionNo);
					            
					        }
						    
						   
				    		
						    
				    		Notify_app_input_xml=sb.toString();
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
				    		Notify_app_input_xml=Notify_app_input_xml.replace("dd-mm-yyyy", actionDate);
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#CARDLIMIT#",Final_Limit );
				    		 if("ISL".equalsIgnoreCase(Product))
				    		 {
				    			 Notify_app_input_xml=Notify_app_input_xml.replace("#MurabahaCertificateNo#",TransactionNo );  	
							 }
				    		 else{
				    			 Notify_app_input_xml=Notify_app_input_xml.replace("<MurabahaCertificateNo>#MurabahaCertificateNo#</MurabahaCertificateNo>","" );
				    			 Notify_app_input_xml=Notify_app_input_xml.replaceAll("((\r\n)|\n)[\\s\t]*(\\1)+","$1" );
				    			
				    		 }
				    		//vinayak rime 4 chnage ends
				    		
				    		
				    		DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml LIMIT INCsss: " + Notify_app_input_xml);
			    		}
			    		else if("Expire_Prospect".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
			    		{
			    			String actionDate="";
			    			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");//YYYY-MM-DD
			    			actionDate=simpleDateFormat.format(new Date());
				    		String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DCC_Integration")
	    		    		.append(System.getProperty("file.separator")).append("EXPIRED_PROS_CLOSURE.txt").toString();
						    BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
				    		
				    		StringBuilder sb=new StringBuilder();
				    		String line=sbf.readLine();
				    		while(line!=null)
				    		{
				    			sb.append(line);
				    			sb.append(System.lineSeparator());
				    			line=sbf.readLine();
				    		}
				    		Notify_app_input_xml=sb.toString();
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#CLOSE_PROSPECT#", "Y");
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#DecisionDate#", actionDate);
				    		
				    		if("Decline".equalsIgnoreCase(fircoAction))
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#FIRCO_EXPIRY#", "Y");
				    		}
				    		else
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#FIRCO_EXPIRY#", "N");
				    		}
				    		if("D".equalsIgnoreCase(fts))
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#BANK_STATEMENT_EXPIRY#", "Y");
				    		}
				    		else
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#BANK_STATEMENT_EXPIRY#", "N");
				    		}
				    		if("Y".equalsIgnoreCase(IBPS_Expiry))
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#IBPS_EXPIRY#", "Y");
				    		}
				    		else
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#IBPS_EXPIRY#", "N");
				    		}
				    		
				    		if("CB".equalsIgnoreCase(firco))
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#FIRCO_DECLINE#", "Y");
				    		}
				    		else if("N".equalsIgnoreCase(firco)||"FP".equalsIgnoreCase(firco))
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#FIRCO_DECLINE#", "N");
				    		}
				    		else
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#FIRCO_DECLINE#", "");
				    		}
				    		
				    		if("D".equalsIgnoreCase(DectechDecision))
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECTCH_DECLINE#", "Y");
				    		}
				    		else if("A".equalsIgnoreCase(DectechDecision) || "R".equalsIgnoreCase(DectechDecision))
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECTCH_DECLINE#", "N");
				    		}
				    		else
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECTCH_DECLINE#", "");
				    		}
				    		if("Confirmed Fraud".equalsIgnoreCase(efms) || "Negative".equalsIgnoreCase(efms)) //To be changes as part of EFMS change 22112023 PDSC-1073
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#EFMS_DECLINE#", "Y");
				    		}
				    		else if("Non-Alerted".equalsIgnoreCase(efms) || "Closed".equalsIgnoreCase(efms))
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#EFMS_DECLINE#", "N");
				    		}
				    		else
				    		{
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#EFMS_DECLINE#", "");
				    		}
				    		if(LIFESTYLE!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#LIFESTYLE#", LIFESTYLE);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#LIFESTYLE#", "");
				    		if(AECB_MONTHLY!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AECB_MONTHLY#", AECB_MONTHLY);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AECB_MONTHLY#", "");
				    		if(STRESS!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#STRESS#", STRESS);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#STRESS#", "");
				    		if(ASS_INCOME!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#ASS_INCOME#", ASS_INCOME);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#ASS_INCOME#","");
				    		if(AFF_RATIO!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AFF_RATIO#",AFF_RATIO);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AFF_RATIO#","");
				    		if(cardType!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARD_TYPE#", cardType);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARD_TYPE#", "");
				    		if(Decline_reason!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECLINE_REASON#", Decline_reason);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#DECLINE_REASON#", "");
				    		
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#DEDUCTION#", "");
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#DISPOSE#", "");
				    		
				    		DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml Expire_Prospect: " + Notify_app_input_xml);
				    	}
				    		
			    		else if("REFER_QUEUE_APPROVE".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
			    		{
			    			String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DCC_Integration")
			    		    .append(System.getProperty("file.separator")).append("REFER_QUEUE_APPROVE.txt").toString();
							BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
							
						    StringBuilder sb=new StringBuilder();
						    String line=sbf.readLine();
						    
						    while(line!=null)
						    {
						    	sb.append(line);
						    	sb.append(System.lineSeparator());
						    	line=sbf.readLine();
						    }
						    
						    Notify_app_input_xml=sb.toString();
						    Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
						    Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
						    if(ASS_INCOME!=null)
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#GrossSalary#", ASS_INCOME);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#GrossSalary#", "");
						    if(ASS_INCOME!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AssessedIncome#", ASS_INCOME);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#AssessedIncome#","");
						    if(Expense1!=null)
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#LifestyleExpense1#", Expense1);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#LifestyleExpense1#","");
						    if(Expense2!=null)
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#LifestyleExpense2#", Expense2);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#LifestyleExpense2#","");
						    if(Expense3!=null)
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#LifestyleExpense3#", Expense3);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#LifestyleExpense3#","");
						    if(Expense4!=null)
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#LifestyleExpense4#", Expense4);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#LifestyleExpense4#","");
						    if(employercode!=null)
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#Employercode#", employercode);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#Employercode#","");
						    if(Employer_Name!=null)
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#EmployerName#", Employer_Name);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#EmployerName#","");
						    if(FinalDBR!=null)
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#OutputFinalDBR#", FinalDBR);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#OutputFinalDBR#","");
						    if(Fatca!=null)
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#USRelation#", Fatca);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#USRelation#","");
						    if(FATCA_Tin_Number!=null)
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#TIN#", FATCA_Tin_Number);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#TIN#","");
						    if(Tin_reason!=null)
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#FatcaReason#", Tin_reason);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#FatcaReason#","");
						    //Deepak changes done for PDSC-1158, Story PDSC-1010 -- send approved limit - start
						    if(Final_Limit!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARDLIMIT#", Final_Limit);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARDLIMIT#", "");
						    //Deepak changes done for PDSC-1158, Story PDSC-1010 -- send approved limit - End
						    
						    
						    
						    //Rubi - Start PDSC-1078			    
						    if("Y".equalsIgnoreCase(OutputAlternateCard)){
						    	 //Hold_AlternateCard - Added HRITIK 18.10.23 - to keep the cases of alternative cards on hold till app is published.
						    	if("Y".equalsIgnoreCase(Hold_AlternateCard)){
						    		DCCNotifyLog.DCCNotifyLogger.debug("As Hold_AlternateCard is Y so, these cases will be on hold untill it has been changed to N for " + processInstanceID);
						    		continue;
						    	}
						    	
						    	/*	DCCNotifyLog.DCCNotifyLogger.debug("OutputAlternateCard:"+ OutputAlternateCard);
						     		DCCNotifyLog.DCCNotifyLogger.debug("OUTPUT_ELIGIBLE_CARD:"+OUTPUT_ELIGIBLE_CARD);
							    	   
							    	OUTPUT_ELIGIBLE_CARD = OUTPUT_ELIGIBLE_CARD.replaceAll("~", ",");
							    	DCCNotifyLog.DCCNotifyLogger.debug("eligibleCardList after replace: " + OUTPUT_ELIGIBLE_CARD);
							    	Notify_app_input_xml=Notify_app_input_xml.replace("#OUTPUT_ELIGIBLE_CARD#",OUTPUT_ELIGIBLE_CARD);
						       */
						    	
						    	OUTPUT_ELIGIBLE_CARD = OUTPUT_ELIGIBLE_CARD.replaceAll("~", ",");
						    	DCCNotifyLog.DCCNotifyLogger.debug("eligibleCardList after replace: " + OUTPUT_ELIGIBLE_CARD);
								
						    	String AltCard= "\t\t<AlternateCard>"+OutputAlternateCard+"</AlternateCard>";
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#AlternateCard#", AltCard);
						    	DCCNotifyLog.DCCNotifyLogger.debug("AltCard REFER_QUEUE_APPROVE: " + AltCard);
						    	DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml REFER_QUEUE_APPROVE: " + Notify_app_input_xml );
						    	
						    	String NewEligibleCards = "\t\t<NewEligibleCards>"+OUTPUT_ELIGIBLE_CARD+"</NewEligibleCards>";
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#NewEligibleCards#", NewEligibleCards );
						    	DCCNotifyLog.DCCNotifyLogger.debug("NewEligibleCards REFER_QUEUE_APPROVE: " + NewEligibleCards );
						    	DCCNotifyLog.DCCNotifyLogger.debug("NewEligibleCards REFER_QUEUE_APPROVE: " + Notify_app_input_xml );
						    
						    }
						    else{
						    	DCCNotifyLog.DCCNotifyLogger.debug("OutputAlternateCard is N" );
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#AlternateCard#","");
						    	Notify_app_input_xml=Notify_app_input_xml.replace("#NewEligibleCards#","");
						    }
						    //Rubi - End	
						    DCCNotifyLog.DCCNotifyLogger.debug("NewEligibleCards REFER_QUEUE_APPROVE: " + Notify_app_input_xml );
					    	
						    String PersonalDetails = "";
						    
				    		if(!"Y".equalsIgnoreCase(EFR_NSTP)){
								PersonalDetails="";
								DCCNotifyLog.DCCNotifyLogger.debug("Cooling Period EFR_NSTP: " +EFR_NSTP);
						    }
							else if("Y".equalsIgnoreCase(EFR_NSTP)){
								PersonalDetails = PersonalDetails + "\n\t\t<FirstName>"+FirstName.trim()+"</FirstName>\n";
								PersonalDetails = PersonalDetails + "\t\t\t<MiddleName>"+MiddleName.trim()+"</MiddleName>\n";
								PersonalDetails = PersonalDetails + "\t\t\t<LastName>"+LastName.trim()+"</LastName>\n";
								PersonalDetails = PersonalDetails + "\t\t\t<FullName>"+FullName+"</FullName>\n";
							}
				    		
				    		Notify_app_input_xml=Notify_app_input_xml.replace("#PERSONAL_DETAILS#", PersonalDetails);
				    	
						    DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml REFER_QUEUE_APPROVE: " + Notify_app_input_xml);
			    		}
				    		
			    		else if("ETB_CARD_APPROVAL".equalsIgnoreCase(NOTIFY_DEH_IDENTIFIER))
			    		{
			    			String fileLocation=new StringBuffer().append(System.getProperty("user.dir")).append(System.getProperty("file.separator")).append("DCC_Integration")
			    		    .append(System.getProperty("file.separator")).append("ETB_CARD_APPROVAL.txt").toString();
							BufferedReader sbf=new BufferedReader(new FileReader(fileLocation));
							
						    StringBuilder sb=new StringBuilder();
						    String line=sbf.readLine();
						    
						    while(line!=null)
						    {
						    	sb.append(line);
						    	sb.append(System.lineSeparator());
						    	line=sbf.readLine();
						    }
						    
						    Notify_app_input_xml=sb.toString();
						    Notify_app_input_xml=Notify_app_input_xml.replace("#WI_NAME#", processInstanceID);
						    Notify_app_input_xml=Notify_app_input_xml.replace("#PROSPECTID#", prospect_id);
						    
						    if(Final_Limit!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARDLIMIT#", Final_Limit);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CARDLIMIT#", "");
						    
						    if(CIF!=null)
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CIF#", CIF);
				    		else
				    			Notify_app_input_xml=Notify_app_input_xml.replace("#CIF#", "");
						    
						    DCCNotifyLog.DCCNotifyLogger.debug("Notify_app_input_xml REFER_QUEUE_APPROVE: " + Notify_app_input_xml);
			    		}
			    	}
			   }

		    		DCCNotifyLog.DCCNotifyLogger.debug("Notify_appliation: " + Notify_app_input_xml);
						
					String integrationStatus="Success";
					String attributesTag;
					String ErrDesc = "";
					StringBuilder finalString=new StringBuilder();
					finalString = finalString.append(Notify_app_input_xml);
					//changes need to done to updae the correct flag
					//HashMap<String, String> socketConnectionMap =socketConnectionDetails(cabinetName, sJtsIp, iJtsPort, sessionId); 
					
					integrationStatus = socketConnection(cabinetName, CommonConnection.getUsername(), sessionId, sJtsIp, iJtsPort, processInstanceID, ws_name, 60, 65,socketDetailsMap, finalString);
					
					XMLParser xmlParserSocketDetails= new XMLParser(integrationStatus);
					DCCNotifyLog.DCCNotifyLogger.debug(" xmlParserSocketDetails : "+xmlParserSocketDetails);
				    String return_code = xmlParserSocketDetails.getValueOf("ReturnCode");
				    DCCNotifyLog.DCCNotifyLogger.debug("Return Code: "+return_code+ "WI: "+processInstanceID);
				    String return_desc = xmlParserSocketDetails.getValueOf("ReturnDesc");
				    DCCNotifyLog.DCCNotifyLogger.debug("return_desc : "+return_desc+ "WI: "+processInstanceID);
					
				    String MsgId ="";
				    if (integrationStatus.contains("<MessageId>"))
						MsgId = xmlParserSocketDetails.getValueOf("MessageId");
					
				    DCCNotifyLog.DCCNotifyLogger.debug("MsgId : "+MsgId+" for WI: "+processInstanceID);
					
				    if(return_code.equalsIgnoreCase("0000"))
				    {
				    	integrationStatus="Success";
				    	ErrDesc = "Notify Done Successfully";
				    }
				    else
				    {
				    	integrationStatus="Failed";
				    	ErrDesc = "Error in Notify DEH";
				    }
					if ("Success".equalsIgnoreCase(integrationStatus))
					{
						decisionValue = "Success";
						DCCNotifyLog.DCCNotifyLogger.debug("Decision in success: " +decisionValue);
						attributesTag="<Decision>"+decisionValue+"</Decision>";
					}
					else
					{
						ErrDesc = return_desc; //integrationStatus.replace("~", ",").replace("|", "\n");
						decisionValue = "Failed";
						DCCNotifyLog.DCCNotifyLogger.debug("Decision in else : " +decisionValue);
						attributesTag="<Decision>"+decisionValue+"</Decision>";
						sendMail(cabinetName,sessionID,processInstanceID,jtsIP,jtsPort,ErrDesc,return_code,ProcessDefId,MsgId);
					}

					//To be modified according to output of Integration Call.

					//Lock Workitem.
					String getWorkItemInputXML = CommonMethods.getWorkItemInput(cabinetName, sessionId, processInstanceID,WorkItemID);
					String getWorkItemOutputXml = CommonMethods.WFNGExecute(getWorkItemInputXML,sJtsIp,iJtsPort,1);
					DCCNotifyLog.DCCNotifyLogger.debug("Output XML For WmgetWorkItemCall: " + getWorkItemOutputXml);

					XMLParser xmlParserGetWorkItem = new XMLParser(getWorkItemOutputXml);
					String getWorkItemMainCode = xmlParserGetWorkItem.getValueOf("MainCode");
					DCCNotifyLog.DCCNotifyLogger.debug("WmgetWorkItemCall Maincode:  "+ getWorkItemMainCode);

					if (getWorkItemMainCode.trim().equals("0") || true )
					{
						DCCNotifyLog.DCCNotifyLogger.debug("WMgetWorkItemCall Successful: "+getWorkItemMainCode);

						//String assignWorkitemAttributeInputXML=CommonMethods.assignWorkitemAttributeInput(cabinetName, sessionId,processInstanceID,WorkItemID,attributesTag);
						
						String assignWorkitemAttributeInputXML = "<?xml version=\"1.0\"?><WMAssignWorkItemAttributes_Input>"
								+ "<Option>WMAssignWorkItemAttributes</Option>"
								+ "<EngineName>"+cabinetName+"</EngineName>"
								+ "<SessionId>"+sessionId+"</SessionId>"
								+ "<ProcessInstanceId>"+processInstanceID+"</ProcessInstanceId>"
								+ "<WorkItemId>"+WorkItemID+"</WorkItemId>"
								+ "<ActivityId>"+ActivityID+"</ActivityId>"
								+ "<ProcessDefId>"+ProcessDefId+"</ProcessDefId>"
								+ "<LastModifiedTime></LastModifiedTime>"
								+ "<ActivityType>"+ActivityType+"</ActivityType>"
								+ "<complete>D</complete>"
								+ "<AuditStatus></AuditStatus>"
								+ "<Comments></Comments>"
								+ "<UserDefVarFlag>Y</UserDefVarFlag>"
								+ "<Attributes>"+attributesTag+"</Attributes>"
								+ "</WMAssignWorkItemAttributes_Input>";
						
						DCCNotifyLog.DCCNotifyLogger.debug("InputXML for assignWorkitemAttribute Call Notify: "+assignWorkitemAttributeInputXML);

						String assignWorkitemAttributeOutputXML=CommonMethods.WFNGExecute(assignWorkitemAttributeInputXML,sJtsIp,
								iJtsPort,1);
						
						DCCNotifyLog.DCCNotifyLogger.debug("OutputXML for assignWorkitemAttribute Call Notify: "+assignWorkitemAttributeOutputXML);
						
						XMLParser xmlParserWorkitemAttribute = new XMLParser(assignWorkitemAttributeOutputXML);
						String assignWorkitemAttributeMainCode = xmlParserWorkitemAttribute.getValueOf("MainCode");
						DCCNotifyLog.DCCNotifyLogger.debug("AssignWorkitemAttribute MainCode: "+assignWorkitemAttributeMainCode);

						if(assignWorkitemAttributeMainCode.trim().equalsIgnoreCase("0"))
						{
							DCCNotifyLog.DCCNotifyLogger.debug("AssignWorkitemAttribute Successful: "+assignWorkitemAttributeMainCode);
							System.out.println(processInstanceID + "Complete Succesfully with status "+decisionValue);
							DCCNotifyLog.DCCNotifyLogger.debug("WorkItem moved to next Workstep.");
						}
						else
						{
							DCCNotifyLog.DCCNotifyLogger.debug("decisionValue : "+decisionValue);
						}
						
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						
						Date current_date = new Date();
						String formattedActionDatetime=dateFormat.format(current_date);
						DCCNotifyLog.DCCNotifyLogger.debug("FormattedActionDatetime: "+formattedActionDatetime);
						
						SimpleDateFormat inputDateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
						SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");

						Date entryDatetimeFormat = inputDateformat.parse(entryDateTime);
						String formattedEntryDatetime = outputDateFormat.format(entryDatetimeFormat);
						DCCNotifyLog.DCCNotifyLogger.debug("FormattedEntryDatetime: " + formattedEntryDatetime);
						
						String columnNames="wi_name,dec_date,ENTRY_DATE_TIME,workstep,user_name,Decision,Remarks";
						String columnValues="'"+processInstanceID+"','"+formattedActionDatetime+"','"+formattedEntryDatetime+"','"+ActivityName+"','"
						+CommonConnection.getUsername()+"','"+decisionValue+"','"+ErrDesc+"'";

						String apInsertInputXML=CommonMethods.apInsert(cabinetName, sessionId, columnNames, columnValues,"NG_DCC_GR_DECISION_HISTORY");
						DCCNotifyLog.DCCNotifyLogger.debug("APInsertInputXML: "+apInsertInputXML);

						String apInsertOutputXML = CommonMethods.WFNGExecute(apInsertInputXML,sJtsIp,iJtsPort,1);
						DCCNotifyLog.DCCNotifyLogger.debug("APInsertOutputXML: "+ apInsertInputXML);

						XMLParser xmlParserAPInsert = new XMLParser(apInsertOutputXML);
						String apInsertMaincode = xmlParserAPInsert.getValueOf("MainCode");
						DCCNotifyLog.DCCNotifyLogger.debug("Status of apInsertMaincode  "+ apInsertMaincode);

						DCCNotifyLog.DCCNotifyLogger.debug("Completed On "+ ActivityName);

						if(apInsertMaincode.equalsIgnoreCase("0"))
						{
							DCCNotifyLog.DCCNotifyLogger.debug("ApInsert successful: "+apInsertMaincode);
							DCCNotifyLog.DCCNotifyLogger.debug("Inserted in WiHistory table successfully.");
						}
						else
						{
							DCCNotifyLog.DCCNotifyLogger.debug("ApInsert failed: "+apInsertMaincode);
						}
					}
					else
					{
						getWorkItemMainCode="";
						DCCNotifyLog.DCCNotifyLogger.debug("WmgetWorkItem failed: "+getWorkItemMainCode);
					}
				}
			}
		}
			catch (Exception e)

		{
			DCCNotifyLog.DCCNotifyLogger.debug("Exception: "+e.getMessage());
		}
	}
	
	String socketConnection(String cabinetName, String username, String sessionId, String sJtsIp, String iJtsPort, String processInstanceID, String ws_name,
			int connection_timeout, int integrationWaitTime,HashMap<String, String> socketDetailsMap, StringBuilder sInputXML)
	{

		String socketServerIP;
		int socketServerPort;
		Socket socket = null;
		OutputStream out = null;
		InputStream socketInputStream = null;
		DataOutputStream dout = null;
		DataInputStream din = null;
		String outputResponse = null;
		String inputRequest = null;
		String inputMessageID = null;

		try
		{

			DCCNotifyLog.DCCNotifyLogger.debug("userName "+ username);
			DCCNotifyLog.DCCNotifyLogger.debug("SessionId "+ sessionId);

			socketServerIP=socketDetailsMap.get("SocketServerIP");
			DCCNotifyLog.DCCNotifyLogger.debug("SocketServerIP "+ socketServerIP);
			socketServerPort=Integer.parseInt(socketDetailsMap.get("SocketServerPort"));
			DCCNotifyLog.DCCNotifyLogger.debug("SocketServerPort "+ socketServerPort);

	   		if (!("".equalsIgnoreCase(socketServerIP) && socketServerIP == null && socketServerPort==0))
	   		{

    			socket = new Socket(socketServerIP, socketServerPort);
    			socket.setSoTimeout(connection_timeout*1000);
    			out = socket.getOutputStream();
    			socketInputStream = socket.getInputStream();
    			dout = new DataOutputStream(out);
    			din = new DataInputStream(socketInputStream);
    			DCCNotifyLog.DCCNotifyLogger.debug("Dout " + dout);
    			DCCNotifyLog.DCCNotifyLogger.debug("Din " + din);

    			outputResponse = "";

    			inputRequest = getRequestXML(cabinetName,sessionId ,processInstanceID, ws_name, username, sInputXML);


    			if (inputRequest != null && inputRequest.length() > 0)
    			{
    				int inputRequestLen = inputRequest.getBytes("UTF-16LE").length;
    				DCCNotifyLog.DCCNotifyLogger.debug("RequestLen: "+inputRequestLen + "");
    				inputRequest = inputRequestLen + "##8##;" + inputRequest;
    				DCCNotifyLog.DCCNotifyLogger.debug("InputRequest"+"Input Request Bytes : "+ inputRequest.getBytes("UTF-16LE"));
    				dout.write(inputRequest.getBytes("UTF-16LE"));dout.flush();
    			}
    			byte[] readBuffer = new byte[500];
    			int num = din.read(readBuffer);
    			if (num > 0)
    			{
    				byte[] arrayBytes = new byte[num];
    				System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
    				outputResponse = outputResponse+ new String(arrayBytes, "UTF-16LE");
					inputMessageID = outputResponse;
    				DCCNotifyLog.DCCNotifyLogger.debug("OutputResponse: "+outputResponse);

    				if(!"".equalsIgnoreCase(outputResponse))
    					outputResponse = getResponseXML(cabinetName,sJtsIp,iJtsPort,sessionId, processInstanceID,outputResponse,integrationWaitTime);

    				if(outputResponse.contains("&lt;"))
    				{
    					outputResponse=outputResponse.replaceAll("&lt;", "<");
    					outputResponse=outputResponse.replaceAll("&gt;", ">");
    				}
    			}
    			socket.close();

				outputResponse = outputResponse.replaceAll("</MessageId>","</MessageId>/n<InputMessageId>"+inputMessageID+"</InputMessageId>");

				//DCCNotifyAPPLog.DCCNotifyLogger.debug("outputResponse "+outputResponse);
				return outputResponse;

    	 		}

    		else
    		{
    			DCCNotifyLog.DCCNotifyLogger.debug("SocketServerIp and SocketServerPort is not maintained "+"");
    			DCCNotifyLog.DCCNotifyLogger.debug("SocketServerIp is not maintained "+	socketServerIP);
    			DCCNotifyLog.DCCNotifyLogger.debug(" SocketServerPort is not maintained "+	socketServerPort);
    			return "Socket Details not maintained";
    		}

		}

		catch (Exception e)
		{
			System.out.println(e.getMessage());
			DCCNotifyLog.DCCNotifyLogger.debug("Exception Occured Mq_connection_CC"+e.getStackTrace());
			return "";
		}
		finally
		{
			try
			{
				if(out != null)
				{
					out.close();
					out=null;
				}
				if(socketInputStream != null)
				{

					socketInputStream.close();
					socketInputStream=null;
				}
				if(dout != null)
				{

					dout.close();
					dout=null;
				}
				if(din != null)
				{

					din.close();
					din=null;
				}
				if(socket != null)
				{
					if(!socket.isClosed())
						socket.close();
					socket=null;
				}

			}

			catch(Exception e)
			{
				DCCNotifyLog.DCCNotifyLogger.debug("Final Exception Occured Mq_connection_CC"+e.getStackTrace());
				//printException(e);
			}
		}


	}
	private String getResponseXML(String cabinetName,String sJtsIp,String iJtsPort, String sessionId, String processInstanceID,String message_ID, int integrationWaitTime)
	{

		String outputResponseXML="";
		try
		{
			String QueryString = "select OUTPUT_XML from NG_DCC_XMLLOG_HISTORY with (nolock) where MESSAGE_ID ='"+message_ID+"' and WI_NAME = '"+processInstanceID+"'";

			String responseInputXML =CommonMethods.apSelectWithColumnNames(QueryString, cabinetName, sessionId);
			DCCNotifyLog.DCCNotifyLogger.debug("Response APSelect InputXML: "+responseInputXML);

			int Loop_count=0;
			do
			{
				String responseOutputXML=CommonMethods.WFNGExecute(responseInputXML,sJtsIp,iJtsPort,1);
				DCCNotifyLog.DCCNotifyLogger.debug("Response APSelect OutputXML: "+responseOutputXML);

			    XMLParser xmlParserSocketDetails= new XMLParser(responseOutputXML);
			    String responseMainCode = xmlParserSocketDetails.getValueOf("MainCode");
			    DCCNotifyLog.DCCNotifyLogger.debug("ResponseMainCode: "+responseMainCode);



			    int responseTotalRecords = Integer.parseInt(xmlParserSocketDetails.getValueOf("TotalRetrieved"));
			    DCCNotifyLog.DCCNotifyLogger.debug("ResponseTotalRecords: "+responseTotalRecords);

			    if (responseMainCode.equals("0") && responseTotalRecords > 0)
				{

					String responseXMLData=xmlParserSocketDetails.getNextValueOf("Record");
					responseXMLData =responseXMLData.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");

	        		XMLParser xmlParserResponseXMLData = new XMLParser(responseXMLData);
	        		//DCCNotifyAPPLog.DCCNotifyLogger.debug("ResponseXMLData: "+responseXMLData);

	        		outputResponseXML=xmlParserResponseXMLData.getValueOf("OUTPUT_XML");
	        		//DCCNotifyAPPLog.DCCNotifyLogger.debug("OutputResponseXML: "+outputResponseXML);

	        		if("".equalsIgnoreCase(outputResponseXML)){
	        			outputResponseXML="Error";
	    			}
	        		break;
				}
			    Loop_count++;
			    Thread.sleep(1000);
			}
			while(Loop_count<integrationWaitTime);
			DCCNotifyLog.DCCNotifyLogger.debug("integrationWaitTime: "+integrationWaitTime);

		}
		catch(Exception e)
		{
			DCCNotifyLog.DCCNotifyLogger.debug("Exception occurred in outputResponseXML" + e.getMessage());
			outputResponseXML="Error";
		}

		return outputResponseXML;

	}
	
	private String getRequestXML(String cabinetName, String sessionId,
			String processInstanceID, String ws_name, String userName, StringBuilder sInputXML)
	{
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("<APMQPUTGET_Input>");
		strBuff.append("<SessionId>" + sessionId + "</SessionId>");
		strBuff.append("<EngineName>" + cabinetName + "</EngineName>");
		strBuff.append("<XMLHISTORY_TABLENAME>NG_DCC_XMLLOG_HISTORY</XMLHISTORY_TABLENAME>");
		strBuff.append("<WI_NAME>" + processInstanceID + "</WI_NAME>");
		strBuff.append("<WS_NAME>" + ws_name + "</WS_NAME>");
		strBuff.append("<USER_NAME>" + userName + "</USER_NAME>");
		strBuff.append("<MQ_REQUEST_XML>");
		strBuff.append(sInputXML);
		strBuff.append("</MQ_REQUEST_XML>");
		strBuff.append("</APMQPUTGET_Input>");
		DCCNotifyLog.DCCNotifyLogger.debug("GetRequestXML: "+ strBuff.toString());
		return strBuff.toString();
	}
	

	private int readConfig() {
		Properties p = null;
		try {

			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir") + File.separator + "ConfigFiles" + File.separator + "DCC_Notify_Config.properties")));

			Enumeration<?> names = p.propertyNames();

			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				NotifyAppConfigParamMap.put(name, p.getProperty(name));
			}
		} catch (Exception e) {
			return -1;
		}
		return 0;
	}
	
	public  void sendMail(String cabinetName, String sessionId ,String wiName,String jtsIp,String jtsport,String ErrDesc, String return_code,String ProcessDefId,String MsgId)throws Exception
    {
        XMLParser objXMLParser = new XMLParser();
        String sInputXML="";
        String sOutputXML="";
        String mainCodeforAPInsert=null;
        sessionCheckInt=0;
        while(sessionCheckInt<loopCount)
        {
            try
            {
            	DCCNotifyLog.DCCNotifyLogger.debug("workitem name to send mail---"+wiName);
            	DCCNotifyLog.DCCNotifyLogger.debug("ErrorMsg to send mail---"+ErrDesc);
            	DCCNotifyLog.DCCNotifyLogger.debug("return_code to send mail---"+return_code);
            	
            	String FinalMailStr = MailStr.toString().replace("<WI_NAME>",wiName).replace("<ret_Code>",return_code)
            	.replace("<errormsg>",ErrDesc).replace("<MsgID>",MsgId);
            	DCCNotifyLog.DCCNotifyLogger.debug("finalbody: "+FinalMailStr);

            	String columnName="MAILFROM,MAILTO,MAILSUBJECT,MAILMESSAGE,MAILCONTENTTYPE,MAILPRIORITY,MAILSTATUS,INSERTEDBY,MAILACTIONTYPE,INSERTEDTIME,PROCESSDEFID,PROCESSINSTANCEID,WORKITEMID,ACTIVITYID,NOOFTRIALS";
            	String strValues="'"+fromMailID+"','"+toMailID+"','"+mailSubject+"','"+FinalMailStr+"','text/html;charset=UTF-8','1','N','CUSTOM','TRIGGER','"+CommonMethods.getdateCurrentDateInSQLFormat()+"','"+ProcessDefId+"','"+wiName+"','1','1','0'";
                
				sInputXML="<?xml version=\"1.0\"?>" +
                        "<APInsert_Input>" +
                        "<Option>APInsert</Option>" +
                        "<TableName>WFMAILQUEUETABLE</TableName>" +
                        "<ColName>" + columnName + "</ColName>" +
                        "<Values>" + strValues + "</Values>" +
                        "<EngineName>" + cabinetName + "</EngineName>" +
                        "<SessionId>" + sessionID + "</SessionId>" +
                        "</APInsert_Input>";
				
                DCCNotifyLog.DCCNotifyLogger.debug("Mail Insert InputXml::::::::::\n"+sInputXML);
                sOutputXML =CommonMethods.WFNGExecute(sInputXML, jtsIP,jtsPort,0);
                DCCNotifyLog.DCCNotifyLogger.debug("Mail Insert OutputXml::::::::::\n"+sOutputXML);
                objXMLParser.setInputXML(sOutputXML);
                mainCodeforAPInsert=objXMLParser.getValueOf("MainCode");    
            }
			catch(Exception e)
            {
                e.printStackTrace();
                DCCNotifyLog.DCCNotifyLogger.error("Exception in Sending mail", e);
                sessionCheckInt++;
                waiteloopExecute(waitLoop);
                continue;
            }
            if (mainCodeforAPInsert.equalsIgnoreCase("11")) 
            {
                DCCNotifyLog.DCCNotifyLogger.debug("Invalid session in Sending mail");
                sessionCheckInt++;
               
                sessionID=CommonConnection.getSessionID(DCCNotifyLog.DCCNotifyLogger, true);
                continue;
            }
            else
            {
                sessionCheckInt++;
                break;
            }
        }
        if(mainCodeforAPInsert.equalsIgnoreCase("0"))
        {
            DCCNotifyLog.DCCNotifyLogger.debug("mail Insert Successful");
            System.out.println("Mail Insert Successful for "+wiName+" in table WFMAILQUEUETABLE");
        }
        else
        {
            DCCNotifyLog.DCCNotifyLogger.debug("mail Insert Unsuccessful");
            System.out.println("Mail Insert Unsuccessful for "+wiName+"in table WFMAILQUEUETABLE");
        }
    }
	
	protected static String WFNGExecute(String ipXML, String jtsServerIP, String serverPort, int flag)
			throws IOException, Exception {
		DCCNotifyLog.DCCNotifyLogger.debug("In WF NG Execute : " + serverPort);
		try {
			if (serverPort.startsWith("33"))
				return WFCallBroker.execute(ipXML, jtsServerIP, Integer.parseInt(serverPort), 1);
			else
				return ngEjbClientCIFVer.makeCall(jtsServerIP, serverPort, "WebSphere", ipXML);
		} catch (Exception e) {
			DCCNotifyLog.DCCNotifyLogger.debug("Exception Occured in WF NG Execute : " + e.getMessage());
			e.printStackTrace();
			return "Error";
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
	
	private String getMonthNumber(String month)
	{
		String ans="";
		try
		{
			switch(month)
			{
			case "January" :
				ans="01";
				break;
			case "February" :
				ans="02";
				break;
			case "March" :
				ans="03";
				break;
			case "April" :
				ans="04";
				break;
			case "May" :
				ans="05";
				break;
			case "June" :
				ans="06";
				break;
			case "July" :
				ans="07";
				break;
			case "August" :
				ans="08";
				break;
			case "September" :
				ans="09";
				break;
			case "October" :
				ans="10";
				break;
			case "November" :
				ans="11";
				break;
			case "December" :
				ans="12";
				break;
			}
		}
		catch(Exception e)
		{
			DCCNotifyLog.DCCNotifyLogger.debug("Exceptione in getting month no from month name--"+ e.toString());
		}
		
		return ans;
	}
	private String downloadAttachDocuments(String processInstanceID,String docList)
	{
		try
		{
			DCCNotifyLog.DCCNotifyLogger.debug("Doclist to download.."+docList);
			if(docList!=null && docList.contains(","))
			docList=docList.replaceAll(",", "','");
			String query123 = "select name as DOCUMENTNAME, AppName as APPNAME , ImageIndex as IMAGE_INDEX,  VolumeId as VOLUME_ID from PDBDocument with(nolock) where name in ('"+docList+"') and DocumentIndex in ( select DocumentIndex from PDBDocumentContent where ParentFolderIndex = (select itemindex from NG_DCC_EXTTABLE with(nolock) where Wi_Name='"+processInstanceID+"'))";
			String sessionID = CommonConnection.getSessionID(DCCNotifyLog.DCCNotifyLogger, false);
			String sInputXML123 = "<?xml version=\"1.0\"?>"+
					"<APSelectWithColumnNames_Input>"+ 
					"<Option>APSelectWithColumnNames</Option>"+
					"<EngineName>" + cabinetName + "</EngineName> "+
					"<SessionId>" + sessionID + "</SessionId>"+
					"<Query>" + query123 + "</Query>"+
					"</APSelectWithColumnNames_Input>";
					
			DCCNotifyLog.DCCNotifyLogger.debug("Input:123"+sInputXML123);	
			String sOutputXml = CommonMethods.WFNGExecute(sInputXML123,jtsIP,jtsPort,1);
			DCCNotifyLog.DCCNotifyLogger.debug("sOutputXml"+sOutputXml);
			XMLParser xmlParserData = new XMLParser(sOutputXml);
			String mainCode=xmlParserData.getValueOf("MainCode");
			if("0".equalsIgnoreCase(mainCode) && Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"))>0)
			{
				String Records = xmlParserData.getNextValueOf("Records");
				DCCNotifyLog.DCCNotifyLogger.debug("TotalRecords: "+Records);
				String parseStringArray[]=CommonMethods.getTagValues(Records, "Record").split("`");
				DCCNotifyLog.DCCNotifyLogger.debug("Total no of documents: "+parseStringArray.length);
				File file = new File(propDocsPath+System.getProperty("file.separator")+processInstanceID);
				if (!file.exists()) 
				{
					if (file.mkdir()) 
					{
								
					} 
					else 
					{
											
					}
				}
				
				String pathTodownload=propDocsPath+System.getProperty("file.separator")+processInstanceID+System.getProperty("file.separator");
				for(int j=0;j<parseStringArray.length;j++)
				{
					WFXmlResponse parsergetlist = new WFXmlResponse(parseStringArray[j]);
					String docname=parsergetlist.getVal("DOCUMENTNAME");
					String imageindex=parsergetlist.getVal("IMAGE_INDEX");
					int volid=Integer.parseInt(parsergetlist.getVal("VOLUME_ID"));
					String ext =parsergetlist.getVal("APPNAME");
				
				
					DCCNotifyLog.DCCNotifyLogger.debug("sOutputXml docname : "+docname);
					DCCNotifyLog.DCCNotifyLogger.debug("sOutputXml imageindex : "+imageindex);
					DCCNotifyLog.DCCNotifyLogger.debug("sOutputXml volid : "+volid);
					DCCNotifyLog.DCCNotifyLogger.debug("sOutputXml  ext: "+ext);
					
					DCCNotifyLog.DCCNotifyLogger.debug("fewfewbnfewbnfejn  ffewfewfext: "+parseStringArray.length);
									
					DCCNotifyLog.DCCNotifyLogger.debug("temppppp"+pathTodownload);
					DCCNotifyLog.DCCNotifyLogger.debug("dfdsfsdf"+imageindex);
					DCCNotifyLog.DCCNotifyLogger.debug("temppppp"+jtsPort);
					DCCNotifyLog.DCCNotifyLogger.debug("IP"+jtsIP);
					try
					{
						CImageServer cImageServer=null;
						try 
						{
							cImageServer = new CImageServer(null,jtsIP, Short.parseShort(jtsPort));
							//cImageServer = new CImageServer(null,"10.15.12.164", Short.parseShort("2809"));
							//cImageServer = new CImageServer(null,"10.15.12.164", Short.parseShort("3333"));
						}
						catch (JPISException e) 
						{
							DCCNotifyLog.DCCNotifyLogger.debug("inside Catch ");
							DCCNotifyLog.DCCNotifyLogger.debug(e.toString());
							//msg = e.getMessage();
							return null;
			
						}
						DCCNotifyLog.DCCNotifyLogger.debug("inside tryyyy ");
						try{
						   /* JPDBString siteName = new JPDBString();
						    
						    CPISDocumentTxn.GetDocInFile_MT(null, "127.0.0.1", (short)Integer.parseInt(jtsPort),
						    cabinetName, (short)1,(short)volid, Integer.parseInt(imageindex),null, propDocsPath+docname+"."+ext, siteName);
						    */
							DCCNotifyLog.DCCNotifyLogger.debug("jtsPort : "+jtsPort);
							DCCNotifyLog.DCCNotifyLogger.debug("cabinetName  : "+cabinetName);
							DCCNotifyLog.DCCNotifyLogger.debug("volid volid : "+volid);
							DCCNotifyLog.DCCNotifyLogger.debug("doc location: "+pathTodownload+docname+"."+ext);
							int odDownloadCode=cImageServer.JPISGetDocInFile_MT(null,jtsIP, Short.parseShort(jtsPort), cabinetName, Short.parseShort("1"),Short.parseShort(String.valueOf(volid)), 
							Integer.parseInt(imageindex),"",pathTodownload+docname+"."+ext, new JPDBString());
							DCCNotifyLog.DCCNotifyLogger.debug("odDownloadCode--"+odDownloadCode);
							if(odDownloadCode==1)
							{
								DCCNotifyLog.DCCNotifyLogger.debug("DOWNLOAD_CALL_COMPLETE");
							}
							else
							{
								return null;
							}
						    

						}catch(Exception e)
						{
							DCCNotifyLog.DCCNotifyLogger.debug("Exception-"+e.toString());
							DCCNotifyLog.DCCNotifyLogger.debug("sOutputXml : sadfdsfsdf");
							return null;
							//e.printStackTrace();
						} /*catch (JPISException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							DCCNotifyLog.DCCNotifyLogger.debug("Exception-"+e.toString());
							return null;
						}*/
						DCCNotifyLog.DCCNotifyLogger.debug("dddd");	
					}
					catch(Exception e)
					{
							DCCNotifyLog.DCCNotifyLogger.debug("Exception-"+e.toString());
							DCCNotifyLog.DCCNotifyLogger.debug("sOutputXml : sadfdsfsdf");
							return null;
							//e.printStackTrace();
					}
				}
			}
			else
			{
				DCCNotifyLog.DCCNotifyLogger.debug("No documents records received from apselect-"+mainCode);
			}
		}
		catch(Exception e)
		{
			DCCNotifyLog.DCCNotifyLogger.debug("Exception-"+e.toString());
			return null;
		}
		return "Done";
	}
}



