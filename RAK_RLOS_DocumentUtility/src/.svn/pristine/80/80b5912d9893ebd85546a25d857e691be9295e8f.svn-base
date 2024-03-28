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


package com.newgen.DigitalCC.Document;


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

import com.newgen.DigitalAO.Document.DAODocumentLog;
import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;

public class DCCDocument implements Runnable{	
	
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
	static String CreationDateTime = "";
	static String lastProcessInstanceId = "";
	private  int mainCode;
	Date now=null;
	public static String sdate="";
	public static String source=null;
	public static String dest=null;
	public static String TimeStamp="";
	public static String newFilename=null;
	private static String lastfoldername=null;
	private static String sessionId;
	public static int sessionCheckInt=0;
	public static int waitLoop=50;
	public static int loopCount=50;
	private String strCardProduct="";
	private String strFileNametoMove="";
	
	static Map<String, String> DCCDocumentCofigParamMap= new HashMap<String, String>();
	private Map <String, String> executeXMLMapMethod = new HashMap<String, String>();
	private static NGEjbClient ngEjbClientFalconDocument;
	private final String ECRN_CRN_PROC="generateCRNANDECRN";
	
	public void run()
	{
		int sleepIntervalInMin=0;
		try
		{
			DCCDocumentLog.setLogger();
			ngEjbClientFalconDocument = NGEjbClient.getSharedInstance();

			DCCDocumentLog.mLogger.debug("Connecting to Cabinet.");

			int configReadStatus = readConfig();

			DCCDocumentLog.mLogger.debug("configReadStatus "+configReadStatus);
			if(configReadStatus !=0)
			{
				DCCDocumentLog.mLogger.error("Could not Read Config Properties [DCC Document]");
				return;
			}

			cabinetName = CommonConnection.getCabinetName();
			DCCDocumentLog.mLogger.debug("Cabinet Name: " + cabinetName);

			jtsIP = CommonConnection.getJTSIP();
			DCCDocumentLog.mLogger.debug("JTSIP: " + jtsIP);

			jtsPort = CommonConnection.getJTSPort();
			DCCDocumentLog.mLogger.debug("JTSPORT: " + jtsPort);

			smsPort = CommonConnection.getsSMSPort();
			DCCDocumentLog.mLogger.debug("SMSPort: " + smsPort);			

			sleepIntervalInMin=Integer.parseInt(DCCDocumentCofigParamMap.get("SleepIntervalInMin"));
			DCCDocumentLog.mLogger.debug("SleepIntervalInMin: "+sleepIntervalInMin);

			attributeNames=DCCDocumentCofigParamMap.get("AttributeNames").split(",");
			DCCDocumentLog.mLogger.debug("AttributeNames: " + attributeNames);

			ExternalTable=DCCDocumentCofigParamMap.get("ExtTableName");
			DCCDocumentLog.mLogger.debug("ExternalTable: " + ExternalTable);

			destFilePath=DCCDocumentCofigParamMap.get("destFilePath");
			DCCDocumentLog.mLogger.debug("destFilePath: " + destFilePath);

			ErrorFolder=DCCDocumentCofigParamMap.get("failDestFilePath");
			DCCDocumentLog.mLogger.debug("ErrorFolder: " + ErrorFolder);

			volumeID=DCCDocumentCofigParamMap.get("VolumeID");
			DCCDocumentLog.mLogger.debug("VolumeID: " + volumeID);

			MaxNoOfTries=DCCDocumentCofigParamMap.get("MaxNoOfTries");   //Not getting used anywhere Sajan
			DCCDocumentLog.mLogger.debug("MaxNoOfTries: " + MaxNoOfTries);

			TimeIntervalBetweenTrialsInMin=Integer.parseInt(DCCDocumentCofigParamMap.get("TimeIntervalBetweenTrialsInMin"));
			DCCDocumentLog.mLogger.debug("TimeIntervalBetweenTrialsInMin: " + TimeIntervalBetweenTrialsInMin);
			
			sessionId = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
			if(sessionId.trim().equalsIgnoreCase(""))
			{
				DCCDocumentLog.mLogger.debug("Could Not Connect to Server!");
			}
			else
			{
				DCCDocumentLog.mLogger.debug("Session ID found: " + sessionId);
				while(true)
				{
					DCCDocumentLog.setLogger();
					startFalconDocumentUtility();
					System.out.println("No More workitems to Process in DCC, Sleeping!");
					Thread.sleep(sleepIntervalInMin*60*1000);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			DCCDocumentLog.mLogger.error("Exception Occurred in DCC Document Document Thread: "+e);
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			DCCDocumentLog.mLogger.error("Exception Occurred in DCC Document Thread : "+result);
		}
	}
	
	private int readConfig()
	{
		Properties p = null;
		try 
		{
			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "ConfigFiles"+ File.separator+ "Digital_CC_Document_Config.properties")));
			Enumeration<?> names = p.propertyNames();
			while (names.hasMoreElements())
			{
			    String name = (String) names.nextElement();
			    DCCDocumentCofigParamMap.put(name, p.getProperty(name));
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception in Read INI: "+ e.getMessage());
			DCCDocumentLog.mLogger.error("Exception has occured while loading DCC properties file "+e.getMessage());
			return -1 ;			
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	private void startFalconDocumentUtility() throws Exception
	{
		DCCDocumentLog.mLogger.info("ProcessWI function for DCC Document Utility started");

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
		String ErrorMsg="";
		int iNoOfTries=0;
		int iMaxNoOfTries=0;

		sessionId  = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);

		if(sessionId==null || sessionId.equalsIgnoreCase("") || sessionId.equalsIgnoreCase("null"))
		{
			DCCDocumentLog.mLogger.error("Could Not Get Session ID "+sessionId);
			return;
		}

		List<DCCWorkItem> wiList = new ArrayList<DCCWorkItem>();
		try
		{
			queueID = DCCDocumentCofigParamMap.get("QueueID");
			DCCDocumentLog.mLogger.debug("QueueID: " + queueID);
			wiList = loadWorkItems(queueID);
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
			for (DCCWorkItem wi : wiList)
			{
				//workItemName = wi.getAttribute("WorkItemName");
				workItemName = wi.processInstanceId;
				workItemId = wi.workItemId;
				String EntryDateTime=wi.EntryDateTime;
				//System.out.print(workItemName);
				//System.out.print("workItemId :- " +workItemId);
				parentFolderIndex = wi.getAttribute("ITEMINDEX");
				DCCDocumentLog.mLogger.info("The work Item number: " + workItemName+" The work Item ID: " + workItemId+" parentFolder: "+parentFolderIndex);
				//DCCDocumentLog.mLogger.info("The work Item ID: " + workItemId);
				// DCCDocumentLog.mLogger.info("The parentFolder of work Item: " +workItemName+ " issss " +parentFolderIndex);
				boolean ErrorFlag = true;
				
				
				if(parentFolderIndex==null || "".equalsIgnoreCase(parentFolderIndex))
				{
					try
					{
						String query="select itemindex from NG_DCC_EXTTABLE with(nolock) where Wi_Name='"+workItemName+"'";
						String parentFolInd_IPXML =CommonMethods.apSelectWithColumnNames(query, cabinetName, CommonConnection.getSessionID(DCCDocumentLog.mLogger, false));
					    DCCDocumentLog.mLogger.debug("extTabDataIPXML: " + parentFolInd_IPXML);
					    String parentFolInd_OPXML = WFNGExecute(parentFolInd_IPXML,jtsIP, Integer.parseInt(jtsPort), 1);
					    DCCDocumentLog.mLogger.debug("extTabDataOPXML: " + parentFolInd_OPXML);
					    XMLParser xmlParser_parentFolInd= new XMLParser(parentFolInd_OPXML);
					    int iTotalrec= Integer.parseInt(xmlParser_parentFolInd.getValueOf("TotalRetrieved"));
					    if (xmlParser_parentFolInd.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec > 0)
				        {
					    	parentFolderIndex=xmlParser_parentFolInd.getValueOf("itemindex");
				        }
					}
					catch(Exception e)
					{
						DCCDocumentLog.mLogger.info("Error in getting parent folder INDex : "+ e.toString());
					}
				}

				FilePath=DCCDocumentCofigParamMap.get("filePath");
				DCCDocumentLog.mLogger.debug("filePath: " + FilePath);
				String folder_path = Folder_check(workItemName,workItemId);
				DCCDocumentLog.mLogger.info("Final folder path for this instance: " + folder_path);
				Date CurrentDateTime= new Date();
				DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
				String formattedCurrentDateTime = dateFormat.format(CurrentDateTime);
				
				DCCDocumentLog.mLogger.info("formattedCurrentDateTime--"+formattedCurrentDateTime);
					
				
				String LastAttachTryTime="";
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
						DCCDocumentLog.mLogger.info("d2 ----"+d2);

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
				
				

				// to move to next case
				if("".equalsIgnoreCase(folder_path)){
					continue;
				}
				source=folder_path;
				File folder = new File(folder_path);  //RAKFolder
				File[] listOfFiles = folder.listFiles();
				DCCDocumentLog.mLogger.info("# of documents to add in this folder--"+listOfFiles.length);

				//String LastAttachTryTime = wi.getAttribute("LAST_ATTACH_TRY_TIME");	//Changes needed Sajan
				
				
				File documentFolder = null;
				
				//	DCCDocumentLog.mLogger.info("diffMinutes ----"+diffMinutes);
					DCCDocumentLog.mLogger.info("TimeIntervalBetweenTrialsInMin ----"+TimeIntervalBetweenTrialsInMin);
					/*if (iNoOfTries < iMaxNoOfTries) {*/
						if (diffMinutes > TimeIntervalBetweenTrialsInMin && listOfFiles != null)
						{
							DCCDocumentLog.mLogger.info("Inside if loop 100");
							for (File listOfDoc : listOfFiles)
							{
								if (listOfDoc.isFile())
								{
									strfullFileName = listOfDoc.getName();
									strFileNametoMove=strfullFileName;
									DCCDocumentLog.mLogger.info("test 111 file name "+strfullFileName);
									try {
										strfullFileName=strfullFileName.substring(strfullFileName.indexOf("_",3)+1);
										DCCDocumentLog.mLogger.info(strfullFileName);
									}catch(StringIndexOutOfBoundsException e){
										DCCDocumentLog.mLogger.info("string out of range : "+ strfullFileName);
									}
									DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" strfullFileName : "+strfullFileName);

									//strDocumentName = strfullFileName.substring(0,strfullFileName.lastIndexOf("."));
									strDocumentName = getDocumentName(strfullFileName);
									strExtension = strfullFileName.substring(strfullFileName.lastIndexOf(".")+1,strfullFileName.length());
									if(strExtension.equalsIgnoreCase("JPG") || strExtension.equalsIgnoreCase("TIF") || strExtension.equalsIgnoreCase("JPEG") || strExtension.equalsIgnoreCase("TIFF") || strExtension.equalsIgnoreCase("PNG"))
									{
										DocumentType = "I";
									}
									else
									{
										DocumentType = "N";
									}

									DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" strDocumentName : "+strDocumentName+" strExtension : "+strExtension);
									String fileExtension= getFileExtension(listOfDoc);
									DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" fileExtension : "+fileExtension);
									for (int i = 0; i < 3; i++)
									{
										DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" Inside for Loop!");

										JPISIsIndex ISINDEX = new JPISIsIndex();
										JPDBRecoverDocData JPISDEC = new JPDBRecoverDocData();
										lLngFileSize = listOfDoc.length();
										lstrDocFileSize = Long.toString(lLngFileSize);

										if(lLngFileSize != 0L)
										{
											DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" The Document address is: "+folder_path+System.getProperty("file.separator")+listOfDoc.getName());
											String docPath=folder_path+System.getProperty("file.separator")+listOfDoc.getName();
											try
											{
												DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" before CPISDocumentTxn AddDocument MT: ");

												if(smsPort.startsWith("33"))
												{
													CPISDocumentTxn.AddDocument_MT(null, jtsIP , Short.parseShort(smsPort), cabinetName, Short.parseShort(volumeID), docPath, JPISDEC, "",ISINDEX);
												}
												else
												{
													CPISDocumentTxn.AddDocument_MT(null, jtsIP , Short.parseShort(smsPort), cabinetName, Short.parseShort(volumeID), docPath, JPISDEC, null,"JNDI", ISINDEX);
												}	

												DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" after CPISDocumentTxn AddDocument MT: ");

												String sISIndex = ISINDEX.m_nDocIndex + "#" + ISINDEX.m_sVolumeId;
												DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" sISIndex: "+sISIndex);
												sMappedInputXml = CommonMethods.getNGOAddDocument(parentFolderIndex,strDocumentName,DocumentType,strExtension,sISIndex,lstrDocFileSize,volumeID,cabinetName,sessionId);
												DCCDocumentLog.mLogger.debug("workItemName: "+workItemName+" sMappedInputXml "+sMappedInputXml);
												//DCCDocumentLog.mLogger.debug("Input xml For NGOAddDocument Call: "+sMappedInputXml);

												sOutputXml=WFNGExecute(sMappedInputXml,jtsIP,Integer.parseInt(jtsPort),1);
												sOutputXml=sOutputXml.replace("<Document>","");
												sOutputXml=sOutputXml.replace("</Document>","");
												DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" Output xml For NGOAddDocument Call: "+sOutputXml);
												//DCCDocumentLog.mLogger.debug("Output xml For NGOAddDocument Call: "+sOutputXml);
												statusXML = CommonMethods.getTagValues(sOutputXml,"Status");
												DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" The maincode of the output xml file is " +statusXML);

											}
											catch (NumberFormatException e)
											{
												DCCDocumentLog.mLogger.info("workItemName1:"+e.getMessage());
												e.printStackTrace();
												catchflag=true;
											}
											catch (JPISException e)
											{
												DCCDocumentLog.mLogger.info("workItemName2:"+e.getMessage());
												e.printStackTrace();
												catchflag=true;
											}
											catch (Exception e)
											{
												DCCDocumentLog.mLogger.info("workItemName3:"+e.getMessage());
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
									DCCDocumentLog.mLogger.info("statusXML maincode is--"+statusXML);
									if("0".equalsIgnoreCase(statusXML)){
										DCCDocumentLog.mLogger.debug("File "+strFileNametoMove +" destination "+destFilePath);
										String source_new = source+System.getProperty("file.separator")+strFileNametoMove+"";
										dest = ""+destFilePath+System.getProperty("file.separator")+sdate+System.getProperty("file.separator")+workItemName+System.getProperty("file.separator")+lastfoldername;
										TimeStamp=get_timestamp();
										DCCDocumentLog.mLogger.debug("Source of file to move-"+source);
										DCCDocumentLog.mLogger.debug("Final destination of file-"+destFilePath);
										newFilename = Move(dest,source_new,TimeStamp);
									}
									DCCDocumentLog.mLogger.info("catch flag is--"+catchflag);
									if(!("0".equalsIgnoreCase(statusXML)) || catchflag==true){
										DCCDocumentLog.mLogger.info("WI Going to the error folder");
										DCCDocumentLog.mLogger.debug("File "+strFileNametoMove +" destination "+destFilePath);
										source = source+System.getProperty("file.separator")+strFileNametoMove+"";
										dest = ""+ErrorFolder+System.getProperty("file.separator")+sdate+System.getProperty("file.separator")+workItemName+System.getProperty("file.separator")+lastfoldername;
										DCCDocumentLog.mLogger.debug("Final destination of file-"+destFilePath);
										DCCDocumentLog.mLogger.debug("Source of file to move-"+source);
										TimeStamp=get_timestamp();
										newFilename = Move(dest,source,TimeStamp);
										continue;
									}
								}
								//file check & document loop end
							}

							try
							{
								if("0".equalsIgnoreCase(statusXML)){
									String Remarks_dec="Expected Documents Attached by Utility";
									folder.delete();
									//historyCaller(workItemName,true);
									decisionToUpdate="Success";
									DCCDocumentLog.mLogger.info("Current date time is---"+get_timestamp());
									updateExternalTable(ExternalTable,"Decision","'" + decisionToUpdate +"'","ITEMINDEX='"+parentFolderIndex+"'");
									DCCDocumentLog.mLogger.info("updateExternalTable -- "+get_timestamp());
									historyCaller(workItemName,decisionToUpdate,Remarks_dec,EntryDateTime);
									doneWorkItem(workItemName, "");
									DCCDocumentLog.mLogger.info("doneWorkItem -- "+get_timestamp());
								}
								else if(!("0".equalsIgnoreCase(statusXML)) || catchflag==true) {
									folder.delete();
									if(ErrorMsg.trim().equalsIgnoreCase(""))
										ErrorMsg = "Expected Documents are not available or error in attaching";

									decisionToUpdate="Failure";
									historyCaller(workItemName,decisionToUpdate,ErrorMsg,EntryDateTime);
									updateExternalTable(ExternalTable,"Decision","'" + decisionToUpdate +"'","ITEMINDEX='"+parentFolderIndex+"'");
									doneWorkItem(workItemName, "");
									DCCDocumentLog.mLogger.info("doneWorkItem -- "+get_timestamp());
								}
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
							ErrorFlag = false;
							DCCDocumentLog.mLogger.info("Done WI after folder match:: ");
						}
					
				else
				{
					continue;
				}
				//}
				//delete the empty workitem folder
				File wiFolder= new File(FilePath+File.separator+workItemName);
				if(wiFolder.exists() && wiFolder.list().length==0)
					wiFolder.delete();
				
				// updating Last try time in external table
				try
				{

					if(ErrorFlag)
					{
						DCCDocumentLog.mLogger.info("updating AttachDocNoOfTries");
						decisionToUpdate = "Failure";
						ErrorMsg = "Document Not Available";
						iNoOfTries++;
						updateExternalTable(ExternalTable,"Decision,ATTACHDOCNOOFTRIES,LAST_ATTACH_TRY_TIME","'" + decisionToUpdate + "','"+String.valueOf(iNoOfTries)+"','"+formattedCurrentDateTime+"'","ITEMINDEX='"+parentFolderIndex+"'");

						if (iNoOfTries > iMaxNoOfTries)
						{
							historyCaller(workItemName,"Failure","Expected Documents are not available",EntryDateTime);
							DCCDocumentLog.mLogger.info("Done WI when no folder match:: ");
							doneWorkItem(workItemName, "");
						}
					}
				}
				catch (Exception e)
				{
					DCCDocumentLog.mLogger.info("exception in updating AttachDocNoOfTries");
				}
				//****************************************
			}
		}
		DCCDocumentLog.mLogger.info("exiting ProcessWI function FALCON Document Utility");
	}
		//Grtting Document Type as per IBPS directory
		//Added by om.tiwari 08/10/22
		private String getDocumentName(String strfullFileName)
		{
			try
			{
				String docName="";
				if(strfullFileName.contains("Passport_FirstPage"))
					docName="Passport_FirstPage";
				else if(strfullFileName.contains("STP_CAM_Report"))
					docName="STP_CAM_Report";
				else if(strfullFileName.contains("NON_STP_CAM_Report"))
					docName="NON_STP_CAM_Report";
				else if(strfullFileName.contains("Passport_LastPage"))
					docName="Passport_LastPage";
				else if(strfullFileName.contains("Passport_AddressPage"))
					docName="Passport_AddressPage";
				else if(strfullFileName.contains("VISA"))
					docName="VISA";
				else if(strfullFileName.contains("Entry_Currpassport"))
					docName="Entry_Currpassport";
				else if(strfullFileName.contains("Entry_prevpassport"))
					docName="Entry_prevpassport";
				else if(strfullFileName.contains("Pol_clearance"))
					docName="Pol_clearance";
				else if(strfullFileName.contains("Marriage_cert"))
					docName="Marriage_cert";
				else if(strfullFileName.contains("Mot_Fathersname"))
					docName="Mot_Fathersname";
				else if(strfullFileName.contains("EMID_Back"))
					docName="EMID_Back";
				else if(strfullFileName.contains("EMID_Front"))
					docName="EMID_Front";
				else if(strfullFileName.contains("BankStatement_month1"))
					docName="BankStatement_month1";
				else if(strfullFileName.contains("BankStatement_month2"))
					docName="BankStatement_month2";
				else if(strfullFileName.contains("BankStatement_month3"))
					docName="BankStatement_month3";
				else if(strfullFileName.contains("Customer_Consent_Form"))
					docName="Customer_Consent_Form";
				else if(strfullFileName.contains("MRBH_Agency_Agreement"))
					docName="MRBH_Agency_Agreement";
				else if(strfullFileName.contains("W-8_Form"))
					docName="W-8_Form";
				else if(strfullFileName.contains("W-9_Form"))
					docName="W-9_Form";
				else if(strfullFileName.contains("Security_Cheque"))
					docName="Security_Cheque";
				else if(strfullFileName.contains("MOI_Certificate"))
					docName="MOI_Certificate";
				else if(strfullFileName.contains("FTS_Statement"))
					docName="FTS_Statement";
				else if(strfullFileName.contains("FTS_Statement_Analysis"))
					docName="FTS_Statement_Analysis";
				else if(strfullFileName.contains("BankStatement_Statement_Analysis"))
					docName="BankStatement_Statement_Analysis";
				else 
					docName="Other_Document";
				return docName;
			}
			catch(Exception e)
			{
				DCCDocumentLog.mLogger.info("exception in getting document type"+e.toString());
				return"";
			}
		}
	//Getting input param for ECRN CRN
	private void getDataForCRNECRN(){
		try{
			String strQuery="select top 1 CardProduct from ng_RLOS_GR_Product with (nolock) where Prod_WIname='CC-0030043319-process'";
			String strInputXML=CommonMethods.apSelectWithColumnNames(strQuery, cabinetName, sessionId);
			DCCDocumentLog.mLogger.info("Getting Card Product from Product grid table "+strInputXML);
			String strOutputXML = WFNGExecute(strInputXML, jtsIP, Integer.parseInt(jtsPort), 1);
			DCCDocumentLog.mLogger.info("OutputXML for getting card product is table "+strOutputXML);
			XMLParser objXMLParser=new XMLParser();
			objXMLParser.setInputXML(strOutputXML);
			String strmainCode=objXMLParser.getValueOf("MainCode");
			
			if("0".equals(strmainCode))
				strCardProduct=objXMLParser.getValueOf("CardProduct");
		}
		catch(IOException ex){
			DCCDocumentLog.mLogger.error("IO EXception in gettinng card product getDataForCRNECRN");
		}
		catch(Exception e){
			DCCDocumentLog.mLogger.error("Generic Exception in getDataForCRNECRN "+e.getMessage());
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
		
		DCCDocumentLog.mLogger.info("Input XML for AP Procedure is  "+sInputXML);
		DCCDocumentLog.mLogger.error("JTS IP is "+jtsIP);
		DCCDocumentLog.mLogger.error("Port is "+jtsPort);
		String strOutputXML = WFNGExecute(sInputXML.toString(), jtsIP, Integer.parseInt(jtsPort), 1);
		DCCDocumentLog.mLogger.info("OutputXML for AP procedure is table "+strOutputXML);
		XMLParser objXMLParser=new XMLParser();
		objXMLParser.setInputXML(strOutputXML);
		String strmainCode=objXMLParser.getValueOf("MainCode");
		DCCDocumentLog.mLogger.info("main code for AP procedure for CRN ECRN generation is "+strmainCode);
		}
		catch(Exception e){
			DCCDocumentLog.mLogger.error("Exception in AP procedure "+e.getMessage());
			e.getStackTrace();
		}
	}
	
	private void doneWorkItem(String wi_name,String values,Boolean... compeletFlag)
	{
		assert compeletFlag.length <= 1;
		sessionId = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
		try {
			executeXMLMapMethod.clear();
			sessionCheckInt = 0;
			while (sessionCheckInt < loopCount) {
				executeXMLMapMethod.put("getWorkItemInputXML", CommonMethods.getWorkItemInput(cabinetName, sessionId, wi_name, workItemId));
				try {
					executeXMLMapMethod.put("getWorkItemOutputXML", WFNGExecute((String) executeXMLMapMethod.get("getWorkItemInputXML"), jtsIP, Integer.parseInt(jtsPort), 1));
				} catch (Exception e) {
					DCCDocumentLog.mLogger.error("Exception in Execute : " + e);
					sessionCheckInt++;
					waiteloopExecute(waitLoop);
					sessionId = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
					continue;
				}
				sessionCheckInt++;
				if (CommonMethods.getTagValues((String) executeXMLMapMethod.get("getWorkItemOutputXML"), "MainCode").equalsIgnoreCase("11")) {
					sessionId = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);

				} else {
					sessionCheckInt++;
					break;
				}
			}
			if (CommonMethods.getTagValues((String) executeXMLMapMethod.get("getWorkItemOutputXML"), "MainCode").equalsIgnoreCase("0")) {
				sessionCheckInt = 0;
				while (sessionCheckInt < loopCount) {
					executeXMLMapMethod.put("inputXml1", CommonMethods.completeWorkItemInput(cabinetName, sessionId, wi_name, workItemId));
					DCCDocumentLog.mLogger.info("inputXml1 ---: " + executeXMLMapMethod.get("inputXml1"));
					DCCDocumentLog.mLogger.debug("Output XML APCOMPLETE " + executeXMLMapMethod.get("inputXml1"));
					try {
						executeXMLMapMethod.put("outXml1", WFNGExecute((String) executeXMLMapMethod.get("inputXml1"), jtsIP, Integer.parseInt(jtsPort), 1));
					} catch (Exception e) {
						DCCDocumentLog.mLogger.error("Exception in Execute : " + e);
						sessionCheckInt++;
						waiteloopExecute(waitLoop);
						sessionId = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);

						continue;
					}

					DCCDocumentLog.mLogger.info("outXml1 " + executeXMLMapMethod.get("outXml1"));
					sessionCheckInt++;
					if (CommonMethods.getTagValues((String) executeXMLMapMethod.get("outXml1"), "MainCode").equalsIgnoreCase("11")) {
						sessionId = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);

					} else {
						sessionCheckInt++;
						break;
					}
				}
			}
			if (CommonMethods.getTagValues((String) executeXMLMapMethod.get("outXml1"), "MainCode").equalsIgnoreCase("0")) {
				DCCDocumentLog.mLogger.info("Completed " + wi_name);
			} else {
				DCCDocumentLog.mLogger.info("Problem in completion of " + wi_name + " ,Maincode :" + CommonMethods.getTagValues((String) executeXMLMapMethod.get("outXml1"), "MainCode"));
			}
		} catch (Exception e) {
			DCCDocumentLog.mLogger.error("Exception in workitem done = " + e);

			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			DCCDocumentLog.mLogger.error("Exception Occurred in done wi : " + result);
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
				DCCDocumentLog.mLogger.debug("inputXmlcheckAPUpdate : " + inputXmlcheckAPUpdate);
				String outXmlCheckAPUpdate=null;
				outXmlCheckAPUpdate=WFNGExecute(inputXmlcheckAPUpdate,jtsIP,Integer.parseInt(jtsPort),1);
				DCCDocumentLog.mLogger.info("outXmlCheckAPUpdate : " + outXmlCheckAPUpdate);
				objXMLParser.setInputXML(outXmlCheckAPUpdate);
				String mainCodeforCheckUpdate = null;
				mainCodeforCheckUpdate=objXMLParser.getValueOf("MainCode");
				if (!mainCodeforCheckUpdate.equalsIgnoreCase("0"))
				{
					DCCDocumentLog.mLogger.error("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
					System.out.println("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
				}
				else
				{
					DCCDocumentLog.mLogger.error("Succesfully updated "+tablename+" table");
					System.out.println("Succesfully updated "+tablename+" table");
				}
				mainCode=Integer.parseInt(mainCodeforCheckUpdate);
				if (mainCode == 11)
				{
					sessionId  = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
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
				DCCDocumentLog.mLogger.error("Inside create validateSessionID exception"+e);
			}
		}
	}
	
	private void historyCaller(String workItemName,String decision, String remarks,String entryDateTime )
	{
		DCCDocumentLog.mLogger.debug("In History Caller method");

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
					String hist_table="NG_DCC_GR_DECISION_HISTORY"; 
					String columns="wi_name,workstep,Decision,dec_date,Remarks,user_name,ENTRY_DATE_TIME";
					String WINAME=workItemName;
					String WSNAME="Attach_Document";  
					String lusername="System";
					DCCDocumentLog.mLogger.info("decision: "+decision);
					DCCDocumentLog.mLogger.info("remarks: "+remarks);
					
					SimpleDateFormat inputDateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
					//SimpleDateFormat outputDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					SimpleDateFormat outputDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");

					Date actionDateTime= new Date();
					String formattedActionDateTime=outputDateFormat.format(actionDateTime);
					DCCDocumentLog.mLogger.debug("FormattedActionDateTime: "+formattedActionDateTime);
					
					Date entryDatetimeFormat = inputDateformat.parse(entryDateTime);
					String formattedEntryDatetime=outputDateFormat.format(entryDatetimeFormat);
					DCCDocumentLog.mLogger.debug("FormattedEntryDatetime: "+formattedEntryDatetime);
					// String entryDatetime=getEntryDatetimefromDB(workItemName);
					
					String values = "'" + WINAME +"'" + "," + "'" + WSNAME +"'" + "," + "'" + decision +"'" + ","  + "'"+formattedActionDateTime+"'" + "," + "'" + remarks +"'" + "," +  "'" + lusername + "','"+formattedEntryDatetime+"'";
					DCCDocumentLog.mLogger.debug("Values for history : \n"+values);

					String sInputXMLAPInsert = CommonMethods.apInsert(cabinetName,sessionId,columns,values,hist_table);

					DCCDocumentLog.mLogger.info("History_InputXml::::::::::\n"+sInputXMLAPInsert);
					sOutputXML= WFNGExecute(sInputXMLAPInsert,jtsIP,Integer.parseInt(jtsPort),1);
					DCCDocumentLog.mLogger.info("History_OutputXml::::::::::\n"+sOutputXML);
					objXMLParser.setInputXML(sOutputXML);
					mainCodeforAPInsert=objXMLParser.getValueOf("MainCode");

				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				DCCDocumentLog.mLogger.error("Exception in historyCaller of UpdateExpiryDate", e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				sessionId  = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
				continue;

			}
			if (mainCodeforAPInsert.equalsIgnoreCase("11")) 
			{
				sessionId  = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
			}
			else
			{
				sessionCheckInt++;
				break;
			}
		}
		if(mainCodeforAPInsert.equalsIgnoreCase("0"))
		{
			DCCDocumentLog.mLogger.info("Insert Successful to NG_DCC_GR_DECISION_HISTORY");
		}
		else
		{
			DCCDocumentLog.mLogger.info("Insert Unsuccessful to NG_DCC_GR_DECISION_HISTORY");
		}
		DCCDocumentLog.mLogger.debug("Out History Caller method NG_DCC_GR_DECISION_HISTORY");
	}
	
	
	//Entry date logic and value needs to be checked Sajan
	public String getEntryDatetimefromDB(String workItemName)
	{
		DCCDocumentLog.mLogger.info("Start of function getEntryDatetimefromDB ");
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
				DCCDocumentLog.mLogger.info("Getting getIntegrationErrorDescription from exttable table "+InputXMLEntryDate);
				outputXMLEntryDate = WFNGExecute(InputXMLEntryDate, jtsIP, Integer.parseInt(jtsPort), 1);
				DCCDocumentLog.mLogger.info("OutputXML for getting getIntegrationErrorDescription from external table "+outputXMLEntryDate);
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
				sessionId  = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);

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
				DCCDocumentLog.mLogger.debug("FormattedEntryDatetime: "+formattedEntryDatetime);

				DCCDocumentLog.mLogger.info("newentrydatetime "+ formattedEntryDatetime);
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
	private List loadWorkItems(String queueID) throws NumberFormatException, IOException, Exception
	{
		DCCDocumentLog.mLogger.info("Starting loadWorkitem function for queueID -->"+queueID);
		List workItemList = new ArrayList();
		String workItemListInputXML="";
		sessionCheckInt=0;
		String workItemListOutputXML="";
		/*DCCDocumentLog.mLogger.info("loopCount aa:" + loopCount);
		DCCDocumentLog.mLogger.info("lastWorkItemId aa:" + lastWorkItemId);
		DCCDocumentLog.mLogger.info("lastProcessInstanceId aa:" + lastProcessInstanceId);*/
		while(sessionCheckInt<loopCount)
		{
			DCCDocumentLog.mLogger.info("123 cabinet name..."+cabinetName);
			DCCDocumentLog.mLogger.info("123 session id is..."+sessionId);
			//workItemListInputXML = CommonMethods.getFetchWorkItemsInputXML(lastProcessInstanceId, lastWorkItemId, sessionId, cabinetName, queueID);
			if(lastProcessInstanceId.equals("")){
				workItemListInputXML = CommonMethods.getFetchWorkItemsInputXML(lastProcessInstanceId, lastWorkItemId, sessionId, cabinetName, queueID);
				}
			else
			{	 workItemListInputXML = CommonMethods.getFetchWorkItemsInputXML(lastProcessInstanceId, lastWorkItemId, sessionId, cabinetName, queueID,CreationDateTime);
			}
			DCCDocumentLog.mLogger.info("workItemListInputXML aa:" + workItemListInputXML);
			try
			{
				workItemListOutputXML=WFNGExecute(workItemListInputXML,jtsIP,Integer.parseInt(jtsPort),1);
			}
			catch(Exception e)
			{
				DCCDocumentLog.mLogger.error("Exception in Execute : " + e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				sessionId  = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
				continue;
			}

			DCCDocumentLog.mLogger.info("workItemListOutputXML : " + workItemListOutputXML);
			if (CommonMethods.getTagValues(workItemListOutputXML,"MainCode").equalsIgnoreCase("11"))
			{
				sessionId  = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
			}
			DCCDocumentLog.mLogger.info("workItemListOutputXML : " + CommonMethods.getMainCode(workItemListOutputXML));
			if (CommonMethods.getMainCode(workItemListOutputXML) == 0)
			{
				String [] last = new String[3];
				List workItems = getWorkItems(sessionId,workItemListOutputXML, last);
				DCCDocumentLog.mLogger.info("List : " + workItems);
				DCCDocumentLog.mLogger.info("last : " + last);
				workItemList.addAll(workItems);
				int RetrievedCount = Integer.parseInt(CommonMethods.getTagValues(workItemListOutputXML, "RetrievedCount"));
				if(RetrievedCount>99){
					lastProcessInstanceId = last[0];
					lastWorkItemId = last[1];
					CreationDateTime = last[2];
				}
				else{
					lastProcessInstanceId = "";
					lastWorkItemId = "";
					CreationDateTime="";
					break;
				}
			}
			else
			{
				lastProcessInstanceId = "";
				lastWorkItemId = "";
				CreationDateTime="";
				break;
			}
		}
		DCCDocumentLog.mLogger.info("WI list to process the cases -->"+workItemList);
		return workItemList;
	}
	 
	@SuppressWarnings({ "unchecked" })
	private List getWorkItems(String sessionId, String workItemListOutputXML, String[] last) throws NumberFormatException, Exception
	{
		List workItems = new ArrayList();
		try{
			// TODO Auto-generated method stub Inside finally block of getDocument method
			DCCDocumentLog.mLogger.info("Starting getWorkitems function ");
			Document doc = CommonMethods.getDocument(workItemListOutputXML);
			NodeList instruments = doc.getElementsByTagName("Instrument");
			
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
				last[2] = item.CreationDateTime;

				DCCDocumentLog.mLogger.info("last[0] : "+last[0]);
			}
			DCCDocumentLog.mLogger.info("Exiting getWorkitems function");
			return workItems;
		}
		catch(Exception e){
			DCCDocumentLog.mLogger.info("Exception occured in getWorkItems: " + e.getMessage());
		}
		return workItems;
	}
	 
	@SuppressWarnings("unchecked")
	private DCCWorkItem getWI(String sessionID, Node inst) throws NumberFormatException, IOException, Exception
	{
		DCCDocumentLog.mLogger.info("Starting getWI function");
		DCCWorkItem wi = new DCCWorkItem();
		wi.processInstanceId = CommonMethods.getTagValues(inst, "ProcessInstanceId");
		wi.workItemId = CommonMethods.getTagValues(inst, "WorkItemId");
		wi.CreationDateTime = CommonMethods.getTagValues(inst, "CreationDateTime");
		wi.EntryDateTime = CommonMethods.getTagValues(inst, "EntryDateTime");
		String fetchAttributeInputXML="";
		String fetchAttributeOutputXML="";
		sessionCheckInt=0;
		int Maxloop_count = 2;
		try{
			while(sessionCheckInt<Maxloop_count)
			{
				fetchAttributeInputXML = CommonMethods.getFetchWorkItemAttributesXML(cabinetName,sessionID,wi.processInstanceId, wi.workItemId);
				DCCDocumentLog.mLogger.info("FetchAttributeInputXMl "+fetchAttributeInputXML);
				fetchAttributeOutputXML=WFNGExecute(fetchAttributeInputXML,jtsIP,Integer.parseInt(jtsPort),1);
				//DCCDocumentLog.mLogger.info("FetchAttributeOutputXMl "+fetchAttributeOutputXML);
				fetchAttributeOutputXML=fetchAttributeOutputXML.replaceAll("&","&amp;");
				fetchAttributeOutputXML=fetchAttributeOutputXML.replaceAll("\n+","\n");
				//DCCDocumentLog.mLogger.info("fetchAttributeOutputXML "+fetchAttributeOutputXML);
				String MainCode = CommonMethods.getTagValues(fetchAttributeOutputXML, "MainCode");
				DCCDocumentLog.mLogger.info("FetchAttributeOutputXMl MainCode: "+MainCode);
				if ("0".equalsIgnoreCase(MainCode)){
					try
					{
						//if("1".equalsIgnoreCase(wi.workItemId )){
							for (int i = 0; i < attributeNames.length; ++i)
							{
								
								DCCDocumentLog.mLogger.info("attributeNames[i] "+attributeNames[i]);
								String columnValue = getAttribute(fetchAttributeOutputXML, attributeNames[i]);
								DCCDocumentLog.mLogger.info("columnValue "+columnValue);
								if (columnValue != null)
								{
									wi.map.put(attributeNames[i], columnValue);
								}
								else
								{
									wi.map.put(attributeNames[i], "");
								}
							}
						//}
					}
					catch(Exception e)
					{
						e.printStackTrace();
						DCCDocumentLog.mLogger.debug("Inside catch of get wi function with exception.."+e);
					}
					break;
				}
				else if ("11".equalsIgnoreCase(MainCode))
				{
					sessionID  = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
					sessionId = sessionID;
					sessionCheckInt++;
				}
				else {
					DCCDocumentLog.mLogger.debug(" Error in Fetch Attribute Output XML-"+fetchAttributeOutputXML);
					sessionCheckInt++;
					break;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			DCCDocumentLog.mLogger.debug("Inside catch of get wi function with exception.."+e);
		}



		DCCDocumentLog.mLogger.info("Exiting getWI function");
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
	public String Folder_check(String processInstanceID, String workItemId ){
		String result_path= "";
		try{
			String path=DCCDocumentCofigParamMap.get("filePath")+ File.separator+processInstanceID;
			if(Integer.parseInt(workItemId)!=1)
			{
				String prevStep=getPreviousWorkStep(processInstanceID,workItemId);
				if(prevStep!=null && !"".equalsIgnoreCase(prevStep))
				{
					if("FTSDOC".equalsIgnoreCase(prevStep))
					{
						path=path+File.separator+"FTS";
						lastfoldername="FTS";
					}
					else if("FIRCODOC".equalsIgnoreCase(prevStep))
					{
						path=path+File.separator+"Firco";
						lastfoldername="Firco";
					}
				}
				else
				{
					DCCDocumentLog.mLogger.info(" Not a valid previous workstep : "+prevStep);
				}

			}
			else
			{
				path=path+File.separator+"Other";
				lastfoldername="Other";
			}
			
			File docPath = new File(path);
			if(docPath.exists())
			{
				result_path = path;
			}
		}
		catch (Exception e){
			DCCDocumentLog.mLogger.debug("Exception: "+e.getMessage());
		}
		return result_path;
	}
	private  String getPreviousWorkStep( String sWorkItemName, String sWorkitemId )
	{
		String prevWS="";
		DCCDocumentLog.mLogger.info("Start of function getPreviousWorkStep ");
		String outputXML=null;
		String mainCode=null;
		try
		{

			sessionCheckInt=0;
			while(sessionCheckInt<loopCount)
			{
				try 
				{
					XMLParser objXMLParser = new XMLParser();
					String sqlQuery = "select VAR_STR18 as PreviousStage from WFINSTRUMENTTABLE with(nolock) where ProcessInstanceID = '"+sWorkItemName+"' and WorkItemId='"+sWorkitemId+"'";
					String InputXML = CommonMethods.apSelectWithColumnNames(sqlQuery,cabinetName, sessionId);
					DCCDocumentLog.mLogger.info("Getting PreviousWorkStep from instrument table "+InputXML);
					outputXML = WFNGExecute(InputXML, jtsIP, Integer.parseInt(jtsPort), 1);
					DCCDocumentLog.mLogger.info("OutputXML for getting PreviousWorkStep from external table "+outputXML);
					objXMLParser.setInputXML(outputXML);
					mainCode=objXMLParser.getValueOf("MainCode");
					//DCCDocumentLog.mLogger.info("OutputXML for getting PreviousWorkStep from external table "+outputXML);
					if ("0".equalsIgnoreCase(mainCode)) 
					{
						prevWS = CommonMethods.getTagValues(outputXML, "PreviousStage");
						return prevWS;
					}
					else if (!mainCode.equalsIgnoreCase("11"))
					{
						sessionId  = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);

					}
					else
					{
						sessionCheckInt++;
						break;
					}
				} 
				catch (Exception e) 
				{
					DCCDocumentLog.mLogger.info("Getting PreviousWorkStep from instrument table "+e.getMessage());
					sessionCheckInt++;
					waiteloopExecute(waitLoop);
					continue;
				}
				
			}

			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			DCCDocumentLog.mLogger.debug("Inside catch of getPreviousWorkStep function with exception.."+e);
		}
		return prevWS;
	}
	
}