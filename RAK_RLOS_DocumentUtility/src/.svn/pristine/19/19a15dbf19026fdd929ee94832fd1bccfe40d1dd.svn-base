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


package com.newgen.Falcon.Document;


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

public class FalconDocument implements Runnable{	
	
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
	private String strFileNametoMove="";
	
	static Map<String, String> falconDocumentCofigParamMap= new HashMap<String, String>();
	private Map <String, String> executeXMLMapMethod = new HashMap<String, String>();
	private static NGEjbClient ngEjbClientFalconDocument;
	private final String ECRN_CRN_PROC="generateCRNANDECRN";
	
	public void run()
	{
		int sleepIntervalInMin=0;
		try
		{
			FalconDocumentLog.setLogger();
			ngEjbClientFalconDocument = NGEjbClient.getSharedInstance();

			FalconDocumentLog.mLogger.debug("Connecting to Cabinet.");

			int configReadStatus = readConfig();

			FalconDocumentLog.mLogger.debug("configReadStatus "+configReadStatus);
			if(configReadStatus !=0)
			{
				FalconDocumentLog.mLogger.error("Could not Read Config Properties [FalconDocument]");
				return;
			}

			cabinetName = CommonConnection.getCabinetName();
			FalconDocumentLog.mLogger.debug("Cabinet Name: " + cabinetName);

			jtsIP = CommonConnection.getJTSIP();
			FalconDocumentLog.mLogger.debug("JTSIP: " + jtsIP);

			jtsPort = CommonConnection.getJTSPort();
			FalconDocumentLog.mLogger.debug("JTSPORT: " + jtsPort);

			smsPort = CommonConnection.getsSMSPort();
			FalconDocumentLog.mLogger.debug("SMSPort: " + smsPort);			

			sleepIntervalInMin=Integer.parseInt(falconDocumentCofigParamMap.get("SleepIntervalInMin"));
			FalconDocumentLog.mLogger.debug("SleepIntervalInMin: "+sleepIntervalInMin);

			attributeNames=falconDocumentCofigParamMap.get("AttributeNames").split(",");
			FalconDocumentLog.mLogger.debug("AttributeNames: " + attributeNames);

			ExternalTable=falconDocumentCofigParamMap.get("ExtTableName");
			FalconDocumentLog.mLogger.debug("ExternalTable: " + ExternalTable);

			destFilePath=falconDocumentCofigParamMap.get("destFilePath");
			FalconDocumentLog.mLogger.debug("destFilePath: " + destFilePath);

			ErrorFolder=falconDocumentCofigParamMap.get("failDestFilePath");
			FalconDocumentLog.mLogger.debug("ErrorFolder: " + ErrorFolder);

			volumeID=falconDocumentCofigParamMap.get("VolumeID");
			FalconDocumentLog.mLogger.debug("VolumeID: " + volumeID);

			MaxNoOfTries=falconDocumentCofigParamMap.get("MaxNoOfTries");   //Not getting used anywhere Sajan
			FalconDocumentLog.mLogger.debug("MaxNoOfTries: " + MaxNoOfTries);

			TimeIntervalBetweenTrialsInMin=Integer.parseInt(falconDocumentCofigParamMap.get("TimeIntervalBetweenTrialsInMin"));
			FalconDocumentLog.mLogger.debug("TimeIntervalBetweenTrialsInMin: " + TimeIntervalBetweenTrialsInMin);
			
			sessionId = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);
			if(sessionId.trim().equalsIgnoreCase(""))
			{
				FalconDocumentLog.mLogger.debug("Could Not Connect to Server!");
			}
			else
			{
				FalconDocumentLog.mLogger.debug("Session ID found: " + sessionId);
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
			FalconDocumentLog.mLogger.error("Exception Occurred in FALCON Document Document Thread: "+e);
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			FalconDocumentLog.mLogger.error("Exception Occurred in FALCON Document Thread : "+result);
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
			FalconDocumentLog.mLogger.error("Exception has occured while loading properties file "+e.getMessage());
			return -1 ;			
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	private void startFalconDocumentUtility() throws Exception
	{
		FalconDocumentLog.mLogger.info("ProcessWI function for Falcon Document Utility started");

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

		sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);

		if(sessionId==null || sessionId.equalsIgnoreCase("") || sessionId.equalsIgnoreCase("null"))
		{
			FalconDocumentLog.mLogger.error("Could Not Get Session ID "+sessionId);
			return;
		}

		List<WorkItem> wiList = new ArrayList<WorkItem>();
		try
		{
			queueID = falconDocumentCofigParamMap.get("QueueID");
			FalconDocumentLog.mLogger.debug("QueueID: " + queueID);
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
			for (WorkItem wi : wiList)
			{
				workItemName = wi.getAttribute("WorkItemName");
				parentFolderIndex = wi.getAttribute("ITEMINDEX");
				FalconDocumentLog.mLogger.info("The work Item number: " + workItemName);
				FalconDocumentLog.mLogger.info("The parentFolder of work Item: " +workItemName+ " issss " +parentFolderIndex);


				FilePath=falconDocumentCofigParamMap.get("filePath");
				FalconDocumentLog.mLogger.debug("filePath: " + FilePath);

				File folder = new File(FilePath);  //RAKFolder
				File[] listOfFiles = folder.listFiles();
				FalconDocumentLog.mLogger.info("List of all folders are--"+listOfFiles);

				//String LastAttachTryTime = wi.getAttribute("LAST_ATTACH_TRY_TIME");	//Changes needed Sajan
				
				String LastAttachTryTime="";
				
				Date CurrentDateTime= new Date();
				DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
				String formattedCurrentDateTime = dateFormat.format(CurrentDateTime);
				
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
						FalconDocumentLog.mLogger.info("d2 ----"+d2);

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
				if(diffMinutes>TimeIntervalBetweenTrialsInMin)
				{
					FalconDocumentLog.mLogger.info("Inside if loop 100");
					for (File file : listOfFiles)
					{
						FalconDocumentLog.mLogger.info("Inside for loop 101");
						if (file.isDirectory())
						{
							FalconDocumentLog.mLogger.info("Inside if loop 102");
							FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" This is a folder : "+file.getName());

							String foldername = file.getName();
							String path = file.getAbsolutePath();

							if(foldername.equalsIgnoreCase(workItemName))
							{
								FalconDocumentLog.mLogger.info("Inside 103");
								FalconDocumentLog.mLogger.info("Processing Starts for "+workItemName);
								documentFolder = new File(path);
								File[] listOfDocument = documentFolder.listFiles();
								for (File listOfDoc : listOfDocument)
								{
									if (listOfDoc.isFile())
									{
										strfullFileName = listOfDoc.getName();
										strFileNametoMove=strfullFileName;
										FalconDocumentLog.mLogger.info("test 111 file name "+strfullFileName);
										strfullFileName=strfullFileName.substring(strfullFileName.indexOf("_",3)+1,strfullFileName.lastIndexOf("_"))+strfullFileName.substring(strfullFileName.lastIndexOf("."));

										FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" strfullFileName : "+strfullFileName);


										strDocumentName = strfullFileName.substring(0,strfullFileName.lastIndexOf("."));

										strExtension = strfullFileName.substring(strfullFileName.lastIndexOf(".")+1,strfullFileName.length());
										if(strExtension.equalsIgnoreCase("JPG") || strExtension.equalsIgnoreCase("TIF") || strExtension.equalsIgnoreCase("JPEG") || strExtension.equalsIgnoreCase("TIFF"))
										{
											DocumentType = "I";
										}
										else
										{
											DocumentType = "N";
										}

										FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" strDocumentName : "+strDocumentName+" strExtension : "+strExtension);
										String fileExtension= getFileExtension(listOfDoc);

										FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" fileExtension : "+fileExtension);

										for (int i = 0; i < 3; i++)
										{
											FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" Inside for Loop!");

											JPISIsIndex ISINDEX = new JPISIsIndex();
											JPDBRecoverDocData JPISDEC = new JPDBRecoverDocData();
											lLngFileSize = listOfDoc.length();
											lstrDocFileSize = Long.toString(lLngFileSize);

											if(lLngFileSize != 0L)
											{
												FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" The Document address is: "+path+System.getProperty("file.separator")+listOfDoc.getName());
												String docPath=path+System.getProperty("file.separator")+listOfDoc.getName();

												try
												{
													FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" before CPISDocumentTxn AddDocument MT: ");

													if(smsPort.startsWith("33"))
													{
														CPISDocumentTxn.AddDocument_MT(null, jtsIP , Short.parseShort(smsPort), cabinetName, Short.parseShort(volumeID), docPath, JPISDEC, "",ISINDEX);
													}
													else
													{
														CPISDocumentTxn.AddDocument_MT(null, jtsIP , Short.parseShort(smsPort), cabinetName, Short.parseShort(volumeID), docPath, JPISDEC, null,"JNDI", ISINDEX);
													}	

													FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" after CPISDocumentTxn AddDocument MT: ");

													String sISIndex = ISINDEX.m_nDocIndex + "#" + ISINDEX.m_sVolumeId;
													FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" sISIndex: "+sISIndex);
													sMappedInputXml = CommonMethods.getNGOAddDocument(parentFolderIndex,strDocumentName,DocumentType,strExtension,sISIndex,lstrDocFileSize,volumeID,cabinetName,sessionId);
													FalconDocumentLog.mLogger.debug("workItemName: "+workItemName+" sMappedInputXml "+sMappedInputXml);
													FalconDocumentLog.mLogger.debug("Input xml For NGOAddDocument Call: "+sMappedInputXml);

													sOutputXml=WFNGExecute(sMappedInputXml,jtsIP,Integer.parseInt(jtsPort),1);
													sOutputXml=sOutputXml.replace("<Document>","");
													sOutputXml=sOutputXml.replace("</Document>","");
													FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" Output xml For NGOAddDocument Call: "+sOutputXml);
													FalconDocumentLog.mLogger.debug("Output xml For NGOAddDocument Call: "+sOutputXml);
													statusXML = CommonMethods.getTagValues(sOutputXml,"Status");
													FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" The maincode of the output xml file is " +statusXML);

												}
												catch (NumberFormatException e)
												{
													FalconDocumentLog.mLogger.info("workItemName1:"+e.getMessage());
													e.printStackTrace();
													catchflag=true;
												}
												catch (JPISException e)
												{
													FalconDocumentLog.mLogger.info("workItemName2:"+e.getMessage());
													e.printStackTrace();
													catchflag=true;
												}
												catch (Exception e)
												{
													FalconDocumentLog.mLogger.info("workItemName3:"+e.getMessage());
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
										FalconDocumentLog.mLogger.info("statusXML maincode is--"+statusXML);
										if("0".equalsIgnoreCase(statusXML)){
											FalconDocumentLog.mLogger.debug("File "+strFileNametoMove +" destination "+destFilePath);
											source = ""+documentFolder+System.getProperty("file.separator")+strFileNametoMove+"";
											dest = ""+destFilePath+System.getProperty("file.separator")+sdate+System.getProperty("file.separator")+workItemName;
											TimeStamp=get_timestamp();
											newFilename = Move(dest,source,TimeStamp);
										}
										FalconDocumentLog.mLogger.info("catch flag is--"+catchflag);
										if(!("0".equalsIgnoreCase(statusXML)) || catchflag==true){
											FalconDocumentLog.mLogger.info("WI Going to the error folder");
											FalconDocumentLog.mLogger.debug("File "+strFileNametoMove +" destination "+destFilePath);
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
										documentFolder.delete();
										//historyCaller(workItemName,true);
										decisionToUpdate="Success";
										FalconDocumentLog.mLogger.info("Current date time is---"+get_timestamp());
										//updateExternalTable(ExternalTable,"DECISION,","'" + decisionToUpdate + "'","ITEMINDEX='"+parentFolderIndex+"'");
										doneWorkItem(workItemName, "");
										//getDataForCRNECRN();
										//generateCRNECRN();
									}
									
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
								
							}
							else
							{
								FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" Folder name doesn't match the workitem name");
							}
						}
						else
						{
							FalconDocumentLog.mLogger.info("workItemName: "+workItemName+" It is not a folder"+file.getName());
						}
					}
				}
				else
				{
					continue;
				}
				//}

				// updating Last try time in external table
				try
				{
					
						FalconDocumentLog.mLogger.info("updating AttachDocNoOfTries");
						//updateExternalTable(ExternalTable,"LAST_ATTACH_TRY_TIME","'"+formattedCurrentDateTime+"'","ITEMINDEX='"+parentFolderIndex+"'");    // Last try date time column name needs to be changed Sajan
					
				}
				catch (Exception e)
				{
					FalconDocumentLog.mLogger.info("exception in updating AttachDocNoOfTries");
				}
				//****************************************
			}
		}
		FalconDocumentLog.mLogger.info("exiting ProcessWI function FALCON Document Utility");
	}
	
	//Getting input param for ECRN CRN
	private void getDataForCRNECRN(){
		try{
			String strQuery="select top 1 CardProduct from ng_RLOS_GR_Product with (nolock) where Prod_WIname='CC-0030043319-process'";
			String strInputXML=CommonMethods.apSelectWithColumnNames(strQuery, cabinetName, sessionId);
			FalconDocumentLog.mLogger.info("Getting Card Product from Product grid table "+strInputXML);
			String strOutputXML = WFNGExecute(strInputXML, jtsIP, Integer.parseInt(jtsPort), 1);
			FalconDocumentLog.mLogger.info("OutputXML for getting card product is table "+strOutputXML);
			XMLParser objXMLParser=new XMLParser();
			objXMLParser.setInputXML(strOutputXML);
			String strmainCode=objXMLParser.getValueOf("MainCode");
			
			if("0".equals(strmainCode))
				strCardProduct=objXMLParser.getValueOf("CardProduct");
		}
		catch(IOException ex){
			FalconDocumentLog.mLogger.error("IO EXception in gettinng card product getDataForCRNECRN");
		}
		catch(Exception e){
			FalconDocumentLog.mLogger.error("Generic Exception in getDataForCRNECRN "+e.getMessage());
		}
	}
	
	//Calling AP Procedure for CRN ECRN generation
	private void generateCRNECRN(){
		try{
		StringBuffer sInputXML=new StringBuffer();
		sInputXML.append("<?xml version=\"1.0\"?>");
		sInputXML.append("<APProcedure2_Input>");
		sInputXML.append("<Option>APProcedure2</Option>");
		sInputXML.append("<ProcName>"+ECRN_CRN_PROC+"</ProcName>");	
		sInputXML.append("<Params>'CC-0030043319-process','FALCON TITANIUM','Primary',''</Params>");
		sInputXML.append("<EngineName>"+cabinetName+"</EngineName>");
		sInputXML.append("<SessionId>"+sessionId+"</SessionId>");
		sInputXML.append("<NoOfCols>1</NoOfCols>");
		sInputXML.append("</APProcedure2_Input>");
		
		FalconDocumentLog.mLogger.info("Input XML for AP Procedure is  "+sInputXML);
		FalconDocumentLog.mLogger.error("JTS IP is "+jtsIP);
		FalconDocumentLog.mLogger.error("Port is "+jtsPort);
		String strOutputXML = WFNGExecute(sInputXML.toString(), jtsIP, Integer.parseInt(jtsPort), 1);
		FalconDocumentLog.mLogger.info("OutputXML for AP procedure is table "+strOutputXML);
		XMLParser objXMLParser=new XMLParser();
		objXMLParser.setInputXML(strOutputXML);
		String strmainCode=objXMLParser.getValueOf("MainCode");
		FalconDocumentLog.mLogger.info("main code for AP procedure for CRN ECRN generation is "+strmainCode);
		}
		catch(Exception e){
			FalconDocumentLog.mLogger.error("Exception in AP procedure "+e.getMessage());
			e.getStackTrace();
		}
	}
	
	private void doneWorkItem(String wi_name,String values,Boolean... compeletFlag)
	{
		assert compeletFlag.length <= 1;
		sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);
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
					FalconDocumentLog.mLogger.error("Exception in Execute : " + e);
					sessionCheckInt++;
					waiteloopExecute(waitLoop);
					sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);
					continue;
				}
				sessionCheckInt++;
				if (CommonMethods.getTagValues((String)executeXMLMapMethod.get("getWorkItemOutputXML"),"MainCode").equalsIgnoreCase("11"))
				{
					sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);

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
					FalconDocumentLog.mLogger.info("inputXml1 ---: "+executeXMLMapMethod.get("inputXml1"));
					FalconDocumentLog.mLogger.debug("Output XML APCOMPLETE "+executeXMLMapMethod.get("inputXml1"));
					try
					{
						executeXMLMapMethod.put("outXml1",WFNGExecute((String)executeXMLMapMethod.get("inputXml1"),jtsIP,Integer.parseInt(jtsPort),1));
					}
					catch(Exception e)
					{
						FalconDocumentLog.mLogger.error("Exception in Execute : " + e);
						sessionCheckInt++;
						waiteloopExecute(waitLoop);
						sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);

						continue;
					}

					FalconDocumentLog.mLogger.info("outXml1 "+executeXMLMapMethod.get("outXml1"));
					sessionCheckInt++;
					if (CommonMethods.getTagValues((String)executeXMLMapMethod.get("outXml1"),"MainCode").equalsIgnoreCase("11"))
					{
						sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);

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
				FalconDocumentLog.mLogger.info("Completed "+wi_name);
			}
			else
			{
				FalconDocumentLog.mLogger.info("Problem in completion of "+wi_name+" ,Maincode :"+CommonMethods.getTagValues((String)executeXMLMapMethod.get("outXml1"),"MainCode"));
			}
		}
		catch(Exception e)
		{
			FalconDocumentLog.mLogger.error("Exception in workitem done = " +e);

			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			FalconDocumentLog.mLogger.error("Exception Occurred in done wi : "+result);
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
				FalconDocumentLog.mLogger.debug("inputXmlcheckAPUpdate : " + inputXmlcheckAPUpdate);
				String outXmlCheckAPUpdate=null;
				outXmlCheckAPUpdate=WFNGExecute(inputXmlcheckAPUpdate,jtsIP,Integer.parseInt(jtsPort),1);
				FalconDocumentLog.mLogger.info("outXmlCheckAPUpdate : " + outXmlCheckAPUpdate);
				objXMLParser.setInputXML(outXmlCheckAPUpdate);
				String mainCodeforCheckUpdate = null;
				mainCodeforCheckUpdate=objXMLParser.getValueOf("MainCode");
				if (!mainCodeforCheckUpdate.equalsIgnoreCase("0"))
				{
					FalconDocumentLog.mLogger.error("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
					System.out.println("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
				}
				else
				{
					FalconDocumentLog.mLogger.error("Succesfully updated "+tablename+" table");
					System.out.println("Succesfully updated "+tablename+" table");
				}
				mainCode=Integer.parseInt(mainCodeforCheckUpdate);
				if (mainCode == 11)
				{
					sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);
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
				FalconDocumentLog.mLogger.error("Inside create validateSessionID exception"+e);
			}
		}
	}
	
	private void historyCaller(String workItemName, boolean DocAttached)
	{
		FalconDocumentLog.mLogger.debug("In History Caller method");

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
					FalconDocumentLog.mLogger.debug("FormattedActionDateTime: "+formattedActionDateTime);

					String entryDatetime=getEntryDatetimefromDB(workItemName);


					String values = "'" + WINAME +"'" + "," + "'" + WSNAME +"'" + "," + "'" + decision +"'" + ","  + "'"+formattedActionDateTime+"'" + "," + "'" + remarks +"'" + "," +  "'" + lusername + "'" +  "," + "'"+entryDatetime+"'";
					FalconDocumentLog.mLogger.debug("Values for history : \n"+values);

					String sInputXMLAPInsert = CommonMethods.apInsert(cabinetName,sessionId,columns,values,hist_table);

					FalconDocumentLog.mLogger.info("History_InputXml::::::::::\n"+sInputXMLAPInsert);
					sOutputXML= WFNGExecute(sInputXMLAPInsert,jtsIP,Integer.parseInt(jtsPort),1);
					FalconDocumentLog.mLogger.info("History_OutputXml::::::::::\n"+sOutputXML);
					objXMLParser.setInputXML(sOutputXML);
					mainCodeforAPInsert=objXMLParser.getValueOf("MainCode");

				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				FalconDocumentLog.mLogger.error("Exception in historyCaller of UpdateExpiryDate", e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);
				continue;

			}
			if (mainCodeforAPInsert.equalsIgnoreCase("11")) 
			{
				sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);
			}
			else
			{
				sessionCheckInt++;
				break;
			}
		}
		if(mainCodeforAPInsert.equalsIgnoreCase("0"))
		{
			FalconDocumentLog.mLogger.info("Insert Successful");
		}
		else
		{
			FalconDocumentLog.mLogger.info("Insert Unsuccessful");
		}
		FalconDocumentLog.mLogger.debug("Out History Caller method");
	}
	
	
	//Entry date logic and value needs to be checked Sajan
	public String getEntryDatetimefromDB(String workItemName)
	{
		FalconDocumentLog.mLogger.info("Start of function getEntryDatetimefromDB ");
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
				FalconDocumentLog.mLogger.info("Getting getIntegrationErrorDescription from exttable table "+InputXMLEntryDate);
				outputXMLEntryDate = WFNGExecute(InputXMLEntryDate, jtsIP, Integer.parseInt(jtsPort), 1);
				FalconDocumentLog.mLogger.info("OutputXML for getting getIntegrationErrorDescription from external table "+outputXMLEntryDate);
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
				sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);

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
				FalconDocumentLog.mLogger.debug("FormattedEntryDatetime: "+formattedEntryDatetime);

				FalconDocumentLog.mLogger.info("newentrydatetime "+ formattedEntryDatetime);
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
		FalconDocumentLog.mLogger.info("Starting loadWorkitem function for queueID -->"+queueID);
		List workItemList = null;
		String workItemListInputXML="";
		sessionCheckInt=0;
		String workItemListOutputXML="";
		FalconDocumentLog.mLogger.info("loopCount aa:" + loopCount);
		FalconDocumentLog.mLogger.info("lastWorkItemId aa:" + lastWorkItemId);
		FalconDocumentLog.mLogger.info("lastProcessInstanceId aa:" + lastProcessInstanceId);
		while(sessionCheckInt<loopCount)
		{
			FalconDocumentLog.mLogger.info("123 cabinet name..."+cabinetName);
			FalconDocumentLog.mLogger.info("123 session id is..."+sessionId);
			workItemListInputXML = CommonMethods.getFetchWorkItemsInputXML(lastProcessInstanceId, lastWorkItemId, sessionId, cabinetName, queueID);
			FalconDocumentLog.mLogger.info("workItemListInputXML aa:" + workItemListInputXML);
			try
			{
				workItemListOutputXML=WFNGExecute(workItemListInputXML,jtsIP,Integer.parseInt(jtsPort),1);
			}
			catch(Exception e)
			{
				FalconDocumentLog.mLogger.error("Exception in Execute : " + e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);
				continue;
			}

			FalconDocumentLog.mLogger.info("workItemListOutputXML : " + workItemListOutputXML);
			if (CommonMethods.getTagValues(workItemListOutputXML,"MainCode").equalsIgnoreCase("11"))
			{
				sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);
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
		FalconDocumentLog.mLogger.info("Exiting loadWorkitem function for queueID -->"+queueID);
		return workItemList;
	}
	 
	@SuppressWarnings({ "unchecked" })
	private List getWorkItems(String sessionId, String workItemListOutputXML, String[] last) throws NumberFormatException, Exception
	{
		// TODO Auto-generated method stub
		FalconDocumentLog.mLogger.info("Starting getWorkitems function ");
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

			FalconDocumentLog.mLogger.info("last[0] : "+last[0]);
		}
		FalconDocumentLog.mLogger.info("Exiting getWorkitems function");
		return workItems;
	}
	 
	@SuppressWarnings("unchecked")
	private WorkItem getWI(String sessionId, Node inst) throws NumberFormatException, IOException, Exception
	{
		FalconDocumentLog.mLogger.info("Starting getWI function");
		WorkItem wi = new WorkItem();
		wi.processInstanceId = CommonMethods.getTagValues(inst, "ProcessInstanceId");
		wi.workItemId = CommonMethods.getTagValues(inst, "WorkItemId");
		String fetchAttributeInputXML="";
		String fetchAttributeOutputXML="";
		sessionCheckInt=0;
		while(sessionCheckInt<loopCount)
		{
			fetchAttributeInputXML = CommonMethods.getFetchWorkItemAttributesXML(cabinetName,sessionId,wi.processInstanceId, wi.workItemId);
			FalconDocumentLog.mLogger.info("FetchAttributeInputXMl "+fetchAttributeInputXML);
			fetchAttributeOutputXML=WFNGExecute(fetchAttributeInputXML,jtsIP,Integer.parseInt(jtsPort),1);
			fetchAttributeOutputXML=fetchAttributeOutputXML.replaceAll("&","&amp;");
			FalconDocumentLog.mLogger.info("fetchAttributeOutputXML "+fetchAttributeOutputXML);
			if (CommonMethods.getTagValues(fetchAttributeOutputXML, "MainCode").equalsIgnoreCase("11"))
			{
				sessionId  = CommonConnection.getSessionID(FalconDocumentLog.mLogger, false);

			} else {
					sessionCheckInt++;
					break;
					}

			if (CommonMethods.getMainCode(fetchAttributeOutputXML) != 0)
			{
				FalconDocumentLog.mLogger.debug(" MapXML.getMainCode(fetchAttributeOutputXML) != 0 ");
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
			FalconDocumentLog.mLogger.debug("Inside catch of get wi function with exception.."+e);
		}
		FalconDocumentLog.mLogger.info("Exiting getWI function");
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