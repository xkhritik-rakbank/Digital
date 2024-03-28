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


package com.newgen.DigitalAO.Document;


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

public class DAODocument implements Runnable {	
	
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
	private  String workItemId;
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
	private String strFileNametoMove="";
	
	private static Map<String, String> DAODocumentCofigParamMap= new HashMap<String, String>();
	private Map <String, String> executeXMLMapMethod = new HashMap<String, String>();
	private static NGEjbClient ngEjbClientDAODocument;
	private final String ECRN_CRN_PROC="generateCRNANDECRN";
	
	public void run()
	{
		int sleepIntervalInMin=0;
		try
		{
			DAODocumentLog.setLogger();
			ngEjbClientDAODocument = NGEjbClient.getSharedInstance();

			DAODocumentLog.mLogger.debug("Connecting to Cabinet.");

			int configReadStatus = readConfig();

			DAODocumentLog.mLogger.debug("configReadStatus "+configReadStatus);
			if(configReadStatus !=0)
			{
				DAODocumentLog.mLogger.error("Could not Read Config Properties [FalconDocument]");
				return;
			}

			cabinetName = CommonConnection.getCabinetName();
			DAODocumentLog.mLogger.debug("Cabinet Name: " + cabinetName);

			jtsIP = CommonConnection.getJTSIP();
			DAODocumentLog.mLogger.debug("JTSIP: " + jtsIP);

			jtsPort = CommonConnection.getJTSPort();
			DAODocumentLog.mLogger.debug("JTSPORT: " + jtsPort);

			smsPort = CommonConnection.getsSMSPort();
			DAODocumentLog.mLogger.debug("SMSPort: " + smsPort);			

			sleepIntervalInMin=Integer.parseInt(DAODocumentCofigParamMap.get("SleepIntervalInMin"));
			DAODocumentLog.mLogger.debug("SleepIntervalInMin: "+sleepIntervalInMin);

			attributeNames=DAODocumentCofigParamMap.get("AttributeNames").split(",");
			DAODocumentLog.mLogger.debug("AttributeNames: " + attributeNames);

			ExternalTable=DAODocumentCofigParamMap.get("ExtTableName");
			DAODocumentLog.mLogger.debug("ExternalTable: " + ExternalTable);

			destFilePath=DAODocumentCofigParamMap.get("destFilePath");
			DAODocumentLog.mLogger.debug("destFilePath: " + destFilePath);

			ErrorFolder=DAODocumentCofigParamMap.get("failDestFilePath");
			DAODocumentLog.mLogger.debug("ErrorFolder: " + ErrorFolder);

			volumeID=DAODocumentCofigParamMap.get("VolumeID");
			DAODocumentLog.mLogger.debug("VolumeID: " + volumeID);

			MaxNoOfTries=DAODocumentCofigParamMap.get("MaxNoOfTries");   //Not getting used anywhere Sajan
			DAODocumentLog.mLogger.debug("MaxNoOfTries: " + MaxNoOfTries);

			TimeIntervalBetweenTrialsInMin=Integer.parseInt(DAODocumentCofigParamMap.get("TimeIntervalBetweenTrialsInMin"));
			DAODocumentLog.mLogger.debug("TimeIntervalBetweenTrialsInMin: " + TimeIntervalBetweenTrialsInMin);
			
			sessionId = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);
			if(sessionId.trim().equalsIgnoreCase(""))
			{
				DAODocumentLog.mLogger.debug("Could Not Connect to Server!");
			}
			else
			{
				DAODocumentLog.mLogger.debug("Session ID found: " + sessionId);
				while(true)
				{
					startFalconDocumentUtility();
					System.out.println("No More workitems to Process, Sleeping!");
					Thread.sleep(sleepIntervalInMin*60*1000);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			DAODocumentLog.mLogger.error("Exception Occurred in DAO Document Document Thread: "+e);
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			DAODocumentLog.mLogger.error("Exception Occurred in DAO Document Thread : "+result);
		}
	}
	
	private int readConfig()
	{
		Properties p = null;
		try 
		{
			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "ConfigFiles"+ File.separator+ "Digital_AO_Document_Config.properties")));
			Enumeration<?> names = p.propertyNames();
			while (names.hasMoreElements())
			{
			    String name = (String) names.nextElement();
			    DAODocumentCofigParamMap.put(name, p.getProperty(name));
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception in Read INI: "+ e.getMessage());
			DAODocumentLog.mLogger.error("Exception has occured while loading properties file "+e.getMessage());
			return -1 ;			
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	private void startFalconDocumentUtility() throws Exception
	{
		DAODocumentLog.mLogger.info("ProcessWI function for DAO Document Utility started");

		String sOutputXml="";
		String sMappedInputXml="";
		long lLngFileSize = 0L;
		String lstrDocFileSize = "";
		String decisionToUpdate="";
		String statusXML="";
		String ErrorMsg="";
		String strfullFileName="";
		String strDocumentName="";
		String strExtension="";
		String DocumentType="";
		String FilePath="";
		boolean catchflag=false;
		int iNoOfTries=0;
		int iMaxNoOfTries=0;

		sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);

		if(sessionId==null || sessionId.equalsIgnoreCase("") || sessionId.equalsIgnoreCase("null"))
		{
			DAODocumentLog.mLogger.error("Could Not Get Session ID "+sessionId);
			return;
		}

		List<DAOWorkItem> wiList = new ArrayList<DAOWorkItem>();
		try
		{
			queueID = DAODocumentCofigParamMap.get("QueueID");
			DAODocumentLog.mLogger.debug("QueueID: " + queueID);
			wiList = loadWorkItems(queueID,sessionId);
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

		//getDataForCRNECRN();
		//generateCRNECRN();
		
		if (wiList != null)
		{
			for (DAOWorkItem wi : wiList)
			{
				//changed by deepanshu
				//workItemName = wi.getAttribute(lastProcessInstanceId);
				workItemName = wi.processInstanceId;
				workItemId = wi.workItemId;
				
				DAODocumentLog.mLogger.info("The workItemName of workItem: " +workItemName+ "  workItemId is " +workItemId);
			
				System.out.print(workItemName);
				
				parentFolderIndex = wi.getAttribute("ITEMINDEX");
				DAODocumentLog.mLogger.info("The work Item number: " + workItemName);
				DAODocumentLog.mLogger.info("The parentFolder of work Item: " +workItemName+ " issss " +parentFolderIndex);

				boolean ErrorFlag = true;
				String PreviousStage = wi.getAttribute("prevws");
				String NoOfTries = wi.getAttribute("ATTACHDOCNOOFTRIES");
				String CurrentStage = wi.getAttribute("currentws");
				
				DAODocumentLog.mLogger.info("The PreviousStage of work Item: " +workItemName+ " is " +PreviousStage);
				DAODocumentLog.mLogger.info("The CurrentStage of work Item: " +workItemName+ " iss" +CurrentStage);
				DAODocumentLog.mLogger.info("The ATTACHDOCNOOFTRIES of work Item: " +workItemName+ " isss " +NoOfTries);
				
				FilePath=DAODocumentCofigParamMap.get("filePath");
				DAODocumentLog.mLogger.debug("filePath: " + FilePath);

				File folder = new File(FilePath);  //RAKFolder
				File[] listOfFiles = folder.listFiles();
				DAODocumentLog.mLogger.info("List of all folders are--"+listOfFiles);

				String LastAttachTryTime = wi.getAttribute("LAST_ATTACH_TRY_TIME");
				DAODocumentLog.mLogger.info("The LastAttachTryTime of work Item: " +workItemName+ " isssss " +LastAttachTryTime);
				
				Date CurrentDateTime= new Date();
				DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
				String formattedCurrentDateTime = dateFormat.format(CurrentDateTime);
				DAODocumentLog.mLogger.info("formattedCurrentDateTime--"+formattedCurrentDateTime);
				
				if (NoOfTries == null || NoOfTries.equalsIgnoreCase("") || NoOfTries == "" || (PreviousStage.equalsIgnoreCase("error_handling") && NoOfTries.equalsIgnoreCase("4")) )
				{
					NoOfTries = "0";
					LastAttachTryTime = "";
				}
				
				long diffMinutes=0;
				if(!(LastAttachTryTime==null || LastAttachTryTime.equalsIgnoreCase("")))
				{
					Date d2 = null;

					try
					{
						SimpleDateFormat inputDateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
						SimpleDateFormat outputDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");

						Date LasttrytimeFormat = inputDateformat.parse(LastAttachTryTime);
						String formattedLastTryDatetime=outputDateFormat.format(LasttrytimeFormat);

						d2=dateFormat.parse(formattedLastTryDatetime);
						DAODocumentLog.mLogger.info("d2 ----"+d2);

					}

					catch(Exception e)
					{
						e.printStackTrace();
						catchflag=true;
					}
					long diff = CurrentDateTime.getTime() - d2.getTime();
					diffMinutes = diff / (60 * 1000) % 60;
				}
				else
				{
					diffMinutes = 10000;
				}

				File documentFolder = null;
				iNoOfTries = Integer.parseInt(NoOfTries);
				DAODocumentLog.mLogger.info("work Item number: " + workItemName + " iNoOfTries is: "+iNoOfTries+" ,PreviousStage: "+PreviousStage);
				DAODocumentLog.mLogger.info("No if tries are ----"+iNoOfTries);
				iMaxNoOfTries = Integer.parseInt(MaxNoOfTries);
				DAODocumentLog.mLogger.info("diffMinutes ----"+diffMinutes);
				DAODocumentLog.mLogger.info("TimeIntervalBetweenTrialsInMin ----"+TimeIntervalBetweenTrialsInMin);
				
				if (iNoOfTries < iMaxNoOfTries)
				{
					if(diffMinutes>TimeIntervalBetweenTrialsInMin)
					{
						DAODocumentLog.mLogger.info("Inside if loop 100");
						for (File file : listOfFiles)
						{
							DAODocumentLog.mLogger.info("Inside for loop 101");
							if (file.isDirectory())
							{
								DAODocumentLog.mLogger.info("Inside if loop 102");
								DAODocumentLog.mLogger.info("workItemName: "+workItemName+" This is a folder : "+file.getName());
	
								String foldername = file.getName();
								String path = file.getAbsolutePath();
	
								if(foldername.equalsIgnoreCase(workItemName))
								{
									DAODocumentLog.mLogger.info("Inside 103");
									DAODocumentLog.mLogger.info("Processing Starts for "+workItemName);
									documentFolder = new File(path);
									File[] listOfDocument = documentFolder.listFiles();
									for (File listOfDoc : listOfDocument)
									{
										if (listOfDoc.isFile())
										{
											strfullFileName = listOfDoc.getName();
											strFileNametoMove=strfullFileName;
											DAODocumentLog.mLogger.info("test 111 file name "+strfullFileName);
											
											try{
												//changed by deepanshu
												//strfullFileName=strfullFileName.substring(strfullFileName.indexOf("_",3)+1,strfullFileName.lastIndexOf("_"))+strfullFileName.substring(strfullFileName.lastIndexOf("."));
												strfullFileName=strfullFileName.substring(strfullFileName.indexOf("_",3)+1);
												//strfullFileName=strfullFileName.substring(strfullFileName.indexOf("_",3)+1,strfullFileName.lastIndexOf("_"));
												System.out.print(strfullFileName);
											}catch (StringIndexOutOfBoundsException e){
												System.out.print("string out of bond index : "+ strfullFileName);
											}
	
											DAODocumentLog.mLogger.info("workItemName: "+workItemName+" strfullFileName : "+strfullFileName);
	
	
											strDocumentName = strfullFileName.substring(0,strfullFileName.lastIndexOf("."));
	
											strExtension = strfullFileName.substring(strfullFileName.lastIndexOf(".")+1,strfullFileName.length());
											if(strExtension.equalsIgnoreCase("JPG") || strExtension.equalsIgnoreCase("TIF") || strExtension.equalsIgnoreCase("JPEG") || strExtension.equalsIgnoreCase("TIFF") || strExtension.equalsIgnoreCase("PNG"))
											{
												DocumentType = "I";
											}
											else
											{
												DocumentType = "N";
											}
	
											DAODocumentLog.mLogger.info("workItemName: "+workItemName+" strDocumentName : "+strDocumentName+" strExtension : "+strExtension);
											String fileExtension= getFileExtension(listOfDoc);
	
											DAODocumentLog.mLogger.info("workItemName: "+workItemName+" fileExtension : "+fileExtension);
	
											for (int i = 0; i < 3; i++)
											{
												DAODocumentLog.mLogger.info("workItemName: "+workItemName+" Inside for Loop!");
	
												JPISIsIndex ISINDEX = new JPISIsIndex();
												JPDBRecoverDocData JPISDEC = new JPDBRecoverDocData();
												lLngFileSize = listOfDoc.length();
												lstrDocFileSize = Long.toString(lLngFileSize);
	
												if(lLngFileSize != 0L)
												{
													DAODocumentLog.mLogger.info("workItemName: "+workItemName+" The Document address is: "+path+System.getProperty("file.separator")+listOfDoc.getName());
													String docPath=path+System.getProperty("file.separator")+listOfDoc.getName();
	
													try
													{
														DAODocumentLog.mLogger.info("workItemName: "+workItemName+" before CPISDocumentTxn AddDocument MT: ");
	
														if(smsPort.startsWith("33"))
														{
															CPISDocumentTxn.AddDocument_MT(null, jtsIP , Short.parseShort(smsPort), cabinetName, Short.parseShort(volumeID), docPath, JPISDEC, "",ISINDEX);
														}
														else
														{
															CPISDocumentTxn.AddDocument_MT(null, jtsIP , Short.parseShort(smsPort), cabinetName, Short.parseShort(volumeID), docPath, JPISDEC, null,"JNDI", ISINDEX);
														}	
	
														DAODocumentLog.mLogger.info("workItemName: "+workItemName+" after CPISDocumentTxn AddDocument MT: ");
	
														String sISIndex = ISINDEX.m_nDocIndex + "#" + ISINDEX.m_sVolumeId;
														DAODocumentLog.mLogger.info("workItemName: "+workItemName+" sISIndex: "+sISIndex);
														sMappedInputXml = CommonMethods.getNGOAddDocument(parentFolderIndex,strDocumentName,DocumentType,strExtension,sISIndex,lstrDocFileSize,volumeID,cabinetName,sessionId);
														DAODocumentLog.mLogger.debug("workItemName: "+workItemName+" sMappedInputXml "+sMappedInputXml);
														DAODocumentLog.mLogger.debug("Input xml For NGOAddDocument Call: "+sMappedInputXml);
	
														sOutputXml=WFNGExecute(sMappedInputXml,jtsIP,Integer.parseInt(jtsPort),1);
														sOutputXml=sOutputXml.replace("<Document>","");
														sOutputXml=sOutputXml.replace("</Document>","");
														DAODocumentLog.mLogger.info("workItemName: "+workItemName+" Output xml For NGOAddDocument Call: "+sOutputXml);
														DAODocumentLog.mLogger.debug("Output xml For NGOAddDocument Call: "+sOutputXml);
														statusXML = CommonMethods.getTagValues(sOutputXml,"Status");
														ErrorMsg = CommonMethods.getTagValues(sOutputXml,"Error");
														DAODocumentLog.mLogger.info("workItemName: "+workItemName+" The maincode of the output xml file is " +statusXML);
	
													}
													catch (NumberFormatException e)
													{
														DAODocumentLog.mLogger.info("workItemName1:"+e.getMessage());
														e.printStackTrace();
														catchflag=true;
													}
													catch (JPISException e)
													{
														DAODocumentLog.mLogger.info("workItemName2:"+e.getMessage());
														e.printStackTrace();
														catchflag=true;
													}
													catch (Exception e)
													{
														DAODocumentLog.mLogger.info("workItemName3:"+e.getMessage());
														e.printStackTrace();
														catchflag=true;
													}
												}
												if(statusXML.equalsIgnoreCase("0"))
													i=3;
											}
	
											//update historytable external table and doneworkitem
											now = new Date();
											Format formatter = new SimpleDateFormat("dd-MMM-yy");
											sdate = formatter.format(now);
											DAODocumentLog.mLogger.info("statusXML maincode is--"+statusXML);
											if("0".equalsIgnoreCase(statusXML)){
												DAODocumentLog.mLogger.debug("File "+strFileNametoMove +" destination "+destFilePath);
												source = ""+documentFolder+System.getProperty("file.separator")+strFileNametoMove+"";
												dest = ""+destFilePath+System.getProperty("file.separator")+sdate+System.getProperty("file.separator")+workItemName;
												TimeStamp=get_timestamp();
												newFilename = Move(dest,source,TimeStamp);
											}
											DAODocumentLog.mLogger.info("catch flag is--"+catchflag);
											if(!("0".equalsIgnoreCase(statusXML)) || catchflag==true){
												DAODocumentLog.mLogger.info("WI Going to the error folder");
												DAODocumentLog.mLogger.debug("File "+strFileNametoMove +" destination "+destFilePath);
												source = ""+documentFolder+System.getProperty("file.separator")+strFileNametoMove+"";
												dest = ""+ErrorFolder+System.getProperty("file.separator")+sdate+System.getProperty("file.separator")+workItemName;
												TimeStamp=get_timestamp();
												newFilename = Move(dest,source,TimeStamp);
												continue;
											}
										}
									}
									
									try
									{
										if("0".equalsIgnoreCase(statusXML)){
											// Remarks_dec
											String Remarks_dec="Expected Documents Attached by Utility";
											documentFolder.delete();
											decisionToUpdate="Success";
											DAODocumentLog.mLogger.info("Current date time is---"+get_timestamp());
											updateExternalTable(ExternalTable,"Decision,LAST_ATTACH_TRY_TIME,ATTACHDOCNOOFTRIES","'" + decisionToUpdate + "','"+formattedCurrentDateTime+"','"+String.valueOf(iNoOfTries)+"'","ITEMINDEX='"+parentFolderIndex+"'");
											historyCaller(workItemName,decisionToUpdate,Remarks_dec);
										}
										else{
											documentFolder.delete();
											if(ErrorMsg.trim().equalsIgnoreCase(""))
												ErrorMsg = "Expected Documents are not available or error in attaching";
											decisionToUpdate="Failure";
											historyCaller(workItemName,decisionToUpdate,ErrorMsg);
											updateExternalTable(ExternalTable,"Decision,LAST_ATTACH_TRY_TIME,ATTACHDOCNOOFTRIES","'" + decisionToUpdate + "','"+formattedCurrentDateTime+"','"+String.valueOf(iNoOfTries)+"'","ITEMINDEX='"+parentFolderIndex+"'");
										}
									}
									catch (Exception e)
									{
										e.printStackTrace();
									}
									ErrorFlag = false;
									doneWorkItem(workItemName, "");
								}
								else
								{
									DAODocumentLog.mLogger.info("workItemName: "+workItemName+" Folder name doesn't match the workitem name");
								}
							}
							else
							{
								DAODocumentLog.mLogger.info("workItemName: "+workItemName+" It is not a folder"+file.getName());
							}
						}
					}
					else 
					{
						continue;
					}
				}
					// updating Last try time in external table
				try
				{
					if(ErrorFlag)
					{
						DAODocumentLog.mLogger.info("updating AttachDocNoOfTries");
						decisionToUpdate = "Failure";
						ErrorMsg = "Document Not Available";
						iNoOfTries++;
						updateExternalTable(ExternalTable,"Decision,ATTACHDOCNOOFTRIES,LAST_ATTACH_TRY_TIME","'" + decisionToUpdate + "','"+String.valueOf(iNoOfTries)+"','"+formattedCurrentDateTime+"'","ITEMINDEX='"+parentFolderIndex+"'");
						
						if (iNoOfTries > iMaxNoOfTries)
						{
							historyCaller(workItemName,"Failure","Expected Documents are not available");
							doneWorkItem(workItemName, "");
						}
					}
				}
				catch (Exception e)
				{
					DAODocumentLog.mLogger.info("exception in updating AttachDocNoOfTries");
				}
			}
		}
		DAODocumentLog.mLogger.info("exiting ProcessWI function DAO Document Utility");
	}
	
	
	private void doneWorkItem(String wi_name,String values,Boolean... compeletFlag)
	{
		assert compeletFlag.length <= 1;
		sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);
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
					DAODocumentLog.mLogger.error("Exception in Execute : " + e);
					sessionCheckInt++;
					waiteloopExecute(waitLoop);
					sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);
					continue;
				}
				sessionCheckInt++;
				if (CommonMethods.getTagValues((String)executeXMLMapMethod.get("getWorkItemOutputXML"),"MainCode").equalsIgnoreCase("11"))
				{
					sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);

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
					DAODocumentLog.mLogger.info("inputXml1 ---: "+executeXMLMapMethod.get("inputXml1"));
					DAODocumentLog.mLogger.debug("Output XML APCOMPLETE "+executeXMLMapMethod.get("inputXml1"));
					try
					{
						executeXMLMapMethod.put("outXml1",WFNGExecute((String)executeXMLMapMethod.get("inputXml1"),jtsIP,Integer.parseInt(jtsPort),1));
					}
					catch(Exception e)
					{
						DAODocumentLog.mLogger.error("Exception in Execute : " + e);
						sessionCheckInt++;
						waiteloopExecute(waitLoop);
						sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);

						continue;
					}

					DAODocumentLog.mLogger.info("outXml1 "+executeXMLMapMethod.get("outXml1"));
					sessionCheckInt++;
					if (CommonMethods.getTagValues((String)executeXMLMapMethod.get("outXml1"),"MainCode").equalsIgnoreCase("11"))
					{
						sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);

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
				DAODocumentLog.mLogger.info("Completed "+wi_name);
			}
			else
			{
				DAODocumentLog.mLogger.info("Problem in completion of "+wi_name+" ,Maincode :"+CommonMethods.getTagValues((String)executeXMLMapMethod.get("outXml1"),"MainCode"));
			}
		}
		catch(Exception e)
		{
			DAODocumentLog.mLogger.error("Exception in workitem done = " +e);

			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			DAODocumentLog.mLogger.error("Exception Occurred in done wi : "+result);
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
				DAODocumentLog.mLogger.debug("inputXmlcheckAPUpdate : " + inputXmlcheckAPUpdate);
				String outXmlCheckAPUpdate=null;
				outXmlCheckAPUpdate=WFNGExecute(inputXmlcheckAPUpdate,jtsIP,Integer.parseInt(jtsPort),1);
				DAODocumentLog.mLogger.info("outXmlCheckAPUpdate : " + outXmlCheckAPUpdate);
				objXMLParser.setInputXML(outXmlCheckAPUpdate);
				String mainCodeforCheckUpdate = null;
				mainCodeforCheckUpdate=objXMLParser.getValueOf("MainCode");
				if (!mainCodeforCheckUpdate.equalsIgnoreCase("0"))
				{
					DAODocumentLog.mLogger.error("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
					System.out.println("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
				}
				else
				{
					DAODocumentLog.mLogger.error("Succesfully updated "+tablename+" table");
					System.out.println("Succesfully updated "+tablename+" table");
				}
				mainCode=Integer.parseInt(mainCodeforCheckUpdate);
				if (mainCode == 11)
				{
					sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);
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
				DAODocumentLog.mLogger.error("Inside create validateSessionID exception"+e);
			}
		}
	}
	
	private void historyCaller(String workItemName, String decision, String remarks)
	{
		DAODocumentLog.mLogger.debug("In History Caller method");

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
					String hist_table="NG_DAO_GR_DECISION_HISTORY";    //History table name needs to be changed Sajan
					String columns="wi_name,workstep,Decision,decision_date_time,Remarks,user_name,entry_date_time";
					String WINAME=workItemName;
					String WSNAME="attach_documents";
					String lusername="System";

					SimpleDateFormat outputDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
					Date actionDateTime= new Date();
					String formattedActionDateTime=outputDateFormat.format(actionDateTime);
					DAODocumentLog.mLogger.debug("FormattedActionDateTime: "+formattedActionDateTime);

					String entryDatetime=getEntryDatetimefromDB(workItemName);

					String values = "'" + WINAME +"'" + "," + "'" + WSNAME +"'" + "," + "'" + decision +"'" + ","  + "'"+formattedActionDateTime+"'" + "," + "'" + remarks +"'" + "," +  "'" + lusername + "'" +  "," + "'"+entryDatetime+"'";
					DAODocumentLog.mLogger.debug("Values for history : \n"+values);

					String sInputXMLAPInsert = CommonMethods.apInsert(cabinetName,sessionId,columns,values,hist_table);

					DAODocumentLog.mLogger.info("History_InputXml::::::::::\n"+sInputXMLAPInsert);
					sOutputXML= WFNGExecute(sInputXMLAPInsert,jtsIP,Integer.parseInt(jtsPort),1);
					DAODocumentLog.mLogger.info("History_OutputXml::::::::::\n"+sOutputXML);
					objXMLParser.setInputXML(sOutputXML);
					mainCodeforAPInsert=objXMLParser.getValueOf("MainCode");

				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				DAODocumentLog.mLogger.error("Exception in historyCaller of UpdateExpiryDate", e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);
				continue;

			}
			if (mainCodeforAPInsert.equalsIgnoreCase("11")) 
			{
				sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);
			}
			else
			{
				sessionCheckInt++;
				break;
			}
		}
		if(mainCodeforAPInsert.equalsIgnoreCase("0"))
		{
			DAODocumentLog.mLogger.info("Insert Successful");
		}
		else
		{
			DAODocumentLog.mLogger.info("Insert Unsuccessful");
		}
		DAODocumentLog.mLogger.debug("Out History Caller method");
	}
	
	
	//Entry date logic and value needs to be checked Sajan
	private String getEntryDatetimefromDB(String workItemName)
	{
		DAODocumentLog.mLogger.info("Start of function getEntryDatetimefromDB ");
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
				String sqlQuery = "select entryat from NG_DAO_EXTTABLE with(nolock) where WI_NAME='"+workItemName+"'";
				String InputXMLEntryDate = CommonMethods.apSelectWithColumnNames(sqlQuery,cabinetName, sessionId);
				DAODocumentLog.mLogger.info("Getting getIntegrationErrorDescription from exttable table "+InputXMLEntryDate);
				outputXMLEntryDate = WFNGExecute(InputXMLEntryDate, jtsIP, Integer.parseInt(jtsPort), 1);
				DAODocumentLog.mLogger.info("OutputXML for getting getIntegrationErrorDescription from external table "+outputXMLEntryDate);
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
				sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);

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
				DAODocumentLog.mLogger.debug("FormattedEntryDatetime: "+formattedEntryDatetime);

				DAODocumentLog.mLogger.info("newentrydatetime "+ formattedEntryDatetime);
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
	private List loadWorkItems(String queueID,String sessionId) throws NumberFormatException, IOException, Exception
	{
		DAODocumentLog.mLogger.info("Starting loadWorkitem function for queueID -->"+queueID);
		List workItemList = null;
		String workItemListInputXML="";
		sessionCheckInt=0;
		String workItemListOutputXML="";
		DAODocumentLog.mLogger.info("loopCount aa:" + loopCount);
		DAODocumentLog.mLogger.info("lastWorkItemId aa:" + lastWorkItemId);
		DAODocumentLog.mLogger.info("lastProcessInstanceId aa:" + lastProcessInstanceId);
		while(sessionCheckInt<loopCount)
		{
			DAODocumentLog.mLogger.info("123 cabinet name..."+cabinetName);
			DAODocumentLog.mLogger.info("123 session id is..."+sessionId);
			//workItemListInputXML = CommonMethods.getFetchWorkItemsInputXML(lastProcessInstanceId, lastWorkItemId, sessionId, cabinetName, queueID);
			if(lastProcessInstanceId.equals("")){
				workItemListInputXML = CommonMethods.getFetchWorkItemsInputXML(lastProcessInstanceId, lastWorkItemId, sessionId, cabinetName, queueID);
				}
			else
			{ workItemListInputXML = CommonMethods.getFetchWorkItemsInputXML(lastProcessInstanceId, lastWorkItemId, sessionId, cabinetName, queueID,lastWorkItemId);
			}
			DAODocumentLog.mLogger.info("workItemListInputXML aa:" + workItemListInputXML);
			try
			{
				workItemListOutputXML=WFNGExecute(workItemListInputXML,jtsIP,Integer.parseInt(jtsPort),1);
			}
			catch(Exception e)
			{
				DAODocumentLog.mLogger.error("Exception in Execute : " + e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);
				continue;
			}

			DAODocumentLog.mLogger.info("workItemListOutputXML : " + workItemListOutputXML);
			if (CommonMethods.getTagValues(workItemListOutputXML,"MainCode").equalsIgnoreCase("11"))
			{
				sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);
			}
			/*else
			{
				sessionCheckInt++;
				break;
			}*/
		/*}

		int i = 0;
		while(i <= 3)
		{*/
			if (CommonMethods.getMainCode(workItemListOutputXML) == 0)
			{
				String [] last = new String[2];
				workItemList = new ArrayList();
				List workItems = getWorkItems(sessionId,workItemListOutputXML, last);
				workItemList.addAll(workItems);
				int RetrievedCount = Integer.parseInt(CommonMethods.getTagValues(workItemListOutputXML, "RetrievedCount"));
				if(RetrievedCount>99){
					lastProcessInstanceId = last[0];
					lastWorkItemId = last[1];
				}
				else{
					lastProcessInstanceId = "";
					lastWorkItemId = "";					
					break;
				}
					
			}
			else
			{
				lastProcessInstanceId = "";
				lastWorkItemId = "";
			}
		}
		DAODocumentLog.mLogger.info("Exiting loadWorkitem function for queueID -->"+queueID);
		return workItemList;
	}
	 
	@SuppressWarnings({ "unchecked" })
	private List getWorkItems(String sessionId, String workItemListOutputXML, String[] last) throws NumberFormatException, Exception
	{
		// TODO Auto-generated method stub
		DAODocumentLog.mLogger.info("Starting getWorkitems function ");
		Document doc = CommonMethods.getDocument(workItemListOutputXML);

		NodeList instruments = doc.getElementsByTagName("Instrument");
		List workItems = new ArrayList();

		int length = instruments.getLength();

		for (int i =0; i < length; ++i)
		{
			Node inst = instruments.item(i);
			DAOWorkItem wi = getWI(sessionId, inst);
			workItems.add(wi);
		}
		int size = workItems.size();
		if (size > 0)
		{
			DAOWorkItem item = (DAOWorkItem)workItems.get(size -1);
			last[0] = item.processInstanceId;
			last[1] = item.workItemId;

			DAODocumentLog.mLogger.info("last[0] : "+last[0]);
		}
		DAODocumentLog.mLogger.info("Exiting getWorkitems function");
		return workItems;
	}
	 
	@SuppressWarnings("unchecked")
	private DAOWorkItem getWI(String sessionId, Node inst) throws NumberFormatException, IOException, Exception
	{
		DAODocumentLog.mLogger.info("Starting getWI function");
		DAOWorkItem wi = new DAOWorkItem();
		wi.processInstanceId = CommonMethods.getTagValues(inst, "ProcessInstanceId");
		wi.workItemId = CommonMethods.getTagValues(inst, "WorkItemId");
		String fetchAttributeInputXML="";
		String fetchAttributeOutputXML="";
		sessionCheckInt=0;
		while(sessionCheckInt<loopCount)
		{
			fetchAttributeInputXML = CommonMethods.getFetchWorkItemAttributesXML(cabinetName,sessionId,wi.processInstanceId, wi.workItemId);
			DAODocumentLog.mLogger.info("FetchAttributeInputXMl "+fetchAttributeInputXML);
			fetchAttributeOutputXML=WFNGExecute(fetchAttributeInputXML,jtsIP,Integer.parseInt(jtsPort),1);
			fetchAttributeOutputXML=fetchAttributeOutputXML.replaceAll("&","&amp;");
			DAODocumentLog.mLogger.info("fetchAttributeOutputXML "+fetchAttributeOutputXML);
			if (CommonMethods.getTagValues(fetchAttributeOutputXML, "MainCode").equalsIgnoreCase("11"))
			{
				sessionId  = CommonConnection.getSessionID(DAODocumentLog.mLogger, false);

			} else {
					sessionCheckInt++;
					break;
					}

			if (CommonMethods.getMainCode(fetchAttributeOutputXML) != 0)
			{
				DAODocumentLog.mLogger.debug(" MapXML.getMainCode(fetchAttributeOutputXML) != 0 ");
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
			DAODocumentLog.mLogger.debug("Inside catch of get wi function with exception.."+e);
		}
		DAODocumentLog.mLogger.info("Exiting getWI function");
		return wi;
	}
	 
	private static String getAttribute(String fetchAttributeOutputXML, String accountNo) throws ParserConfigurationException, SAXException, IOException 
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
	 
	private static String WFNGExecute(String ipXML, String serverIP,
				int serverPort, int flag) throws IOException, Exception 
	{
		String jtsPort=""+serverPort;
		if (jtsPort.startsWith("33"))
			return WFCallBroker.execute(ipXML, serverIP, serverPort, flag);
		else
			return ngEjbClientDAODocument.makeCall(serverIP, serverPort + "", "WebSphere",
						ipXML);
	}
}