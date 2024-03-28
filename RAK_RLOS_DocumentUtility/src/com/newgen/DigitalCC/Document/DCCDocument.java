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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ISPack.CPISDocumentTxn;
import ISPack.ISUtil.JPDBRecoverDocData;
import ISPack.ISUtil.JPISException;
import ISPack.ISUtil.JPISIsIndex;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.newgen.DigitalAO.Document.DAODocumentLog;
import com.newgen.Falcon.Document.FalconDocumentLog;
import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.encryption.DecryptDoc;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.*;

public class DCCDocument implements Runnable{	
	
	private static  String cabinetName;
	private static  String jtsIP;
	private static  String jtsPort;
	private static  String smsPort;
	private  String [] attributeNames;
	private static String folderdeleteageing;
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
	Date nowdate=null;
	public static String sdate="";
	public static String sdatenow="";
	private String USER_PASSWORD="";
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
	private String DocNameList="";
	private String dataClassFolder="";
	private String dataClassFieldsId="";
	private String FromMailID="";
	private String ToMailID="";
	private String mainParentFolderIndex="";
	private String folderHierarchy="";
	
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
			
			folderdeleteageing=DCCDocumentCofigParamMap.get("folderdeleteageing");
			DCCDocumentLog.mLogger.debug("folderdeleteageing: " + folderdeleteageing);
			
			ErrorFolder=DCCDocumentCofigParamMap.get("failDestFilePath");
			DCCDocumentLog.mLogger.debug("ErrorFolder: " + ErrorFolder);

			volumeID=DCCDocumentCofigParamMap.get("VolumeID");
			DCCDocumentLog.mLogger.debug("VolumeID: " + volumeID);

			MaxNoOfTries=DCCDocumentCofigParamMap.get("MaxNoOfTries");   //Not getting used anywhere Sajan
			DCCDocumentLog.mLogger.debug("MaxNoOfTries: " + MaxNoOfTries);

			TimeIntervalBetweenTrialsInMin=Integer.parseInt(DCCDocumentCofigParamMap.get("TimeIntervalBetweenTrialsInMin"));
			DCCDocumentLog.mLogger.debug("TimeIntervalBetweenTrialsInMin: " + TimeIntervalBetweenTrialsInMin);
			
			DocNameList = DCCDocumentCofigParamMap.get("DocName");
			DCCDocumentLog.mLogger.debug("DocNameList "+DocNameList);
						
			dataClassFolder = DCCDocumentCofigParamMap.get("dataClass_Id");
			DCCDocumentLog.mLogger.debug("dataClassFolder: " + dataClassFolder);

			dataClassFieldsId = DCCDocumentCofigParamMap.get("dataClass_fieldId");
			DCCDocumentLog.mLogger.debug("dataClassId: " + dataClassFieldsId);
			
			FromMailID = DCCDocumentCofigParamMap.get("FromMailID");
			DCCDocumentLog.mLogger.debug("FromMailID: " + FromMailID);

			ToMailID = DCCDocumentCofigParamMap.get("ToMailID");
			DCCDocumentLog.mLogger.debug("ToMailID: " + ToMailID);
			
			mainParentFolderIndex = DCCDocumentCofigParamMap.get("mainParentFolderIndex");
			DCCDocumentLog.mLogger.debug("mainParentFolderIndex: " + mainParentFolderIndex);
			
			sessionId = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
			if(sessionId.trim().equalsIgnoreCase("")) {
				DCCDocumentLog.mLogger.debug("Could Not Connect to Server!");
			}
			else {
				DCCDocumentLog.mLogger.debug("Session ID found: " + sessionId);
				while(true) {
					DCCDocumentLog.setLogger();
					startFalconDocumentUtility();
					FTS_DOC(); // hritik - 17.01.24 PDSC-1368
					DCCDocumentLog.mLogger.info("No More workitems to Process in DCC, Sleeping!");
					Thread.sleep(sleepIntervalInMin*60*1000);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			DCCDocumentLog.mLogger.error("Exception Occurred in DCC Document Document Thread: "+e);
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			DCCDocumentLog.mLogger.error("Exception Occurred in DCC Document Thread : "+result);
		}
	}
	
	private int readConfig() {
		Properties p = null;
		try  {
			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "ConfigFiles"+ File.separator+ "Digital_CC_Document_Config.properties")));
			Enumeration<?> names = p.propertyNames();
			while (names.hasMoreElements())
			{
			    String name = (String) names.nextElement();
			    DCCDocumentCofigParamMap.put(name, p.getProperty(name));
			}
		}
		catch (Exception e) {
			DCCDocumentLog.mLogger.info("Exception in Read INI: "+ e.getMessage());
			DCCDocumentLog.mLogger.error("Exception has occured while loading DCC properties file "+e.getMessage());
			return -1 ;			
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	private void startFalconDocumentUtility() throws Exception {
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
		deletefolder();
		
		
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
				//To Decrypt Document - Kamran 20032023
			    DecryptDoc decryptDoc = new DecryptDoc();
				//to handle the encrypted documents
			    DCCDocumentLog.mLogger.info("Decryption Start here ...");
				nowdate = new Date();
				Format formatternow = new SimpleDateFormat("dd-MMM-yy");
				sdatenow = formatternow.format(nowdate);
				DCCDocumentLog.mLogger.info("sdatenow");
				try{
					String query="select Statement_Key from NG_DCC_EXTTABLE where Wi_Name='"+workItemName+"'";
					String InputXML = CommonMethods.apSelectWithColumnNames(query, cabinetName, CommonConnection.getSessionID(DCCDocumentLog.mLogger, false));
					DCCDocumentLog.mLogger.info(InputXML);
					String OutXml = WFNGExecute(InputXML,jtsIP, Integer.parseInt(jtsPort),1);
					DCCDocumentLog.mLogger.info(OutXml);
					XMLParser xmlParserData=new XMLParser(OutXml);
					int iTotalrec= Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));
					if (xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec > 0) {
						DCCDocumentLog.mLogger.info("Encrypted Password --" + USER_PASSWORD);
						String statmnt_Key = xmlParserData.getValueOf("Statement_Key");
						DCCDocumentLog.mLogger.info("Encrypted Key --   "+statmnt_Key);
						try {
							USER_PASSWORD = decrypt(statmnt_Key);
							DCCDocumentLog.mLogger.info("Decrypted Password --- " + decrypt(statmnt_Key));
						} catch (Exception e) {
							DCCDocumentLog.mLogger.info("Decrypted Password Error-- "+e.toString());
						}

					}

				}
				catch(Exception e)
				{
					DCCDocumentLog.mLogger.info(e.toString());
				}
				CheckEncrypted( folder_path, workItemName, sdatenow, USER_PASSWORD);
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
									
									if(strExtension.equalsIgnoreCase("PNG"))
									{
										DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" strfullFileName : "+strfullFileName);
										DCCDocumentLog.mLogger.info("strExtension: Before PNG: "+strfullFileName);
										strExtension="JPG";
										DCCDocumentLog.mLogger.info("strExtension: After PNG: "+strfullFileName);
									}
									if(strExtension.equalsIgnoreCase("JPG") || strExtension.equalsIgnoreCase("TIF") || strExtension.equalsIgnoreCase("JPEG") || strExtension.equalsIgnoreCase("TIFF"))
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
	
	// Hritik - 17/01/24 PDSC-1368
	private void FTS_DOC()throws Exception {
		
		String wi_name="";
		String Status="";
		String insertion_date="";
		int iTotalrec=0;
		int iTotalrec_WILoc=0;
		String outputXMLFTS_DOC ="";
		String mainCode_WILoc="";
		String outputXML_WILoc="";
		String ActivityName="";
		String strfullFileName="";
		String strDocumentName="";
		String strExtension="";
		String parentFolderIndex="";
		String DocumentType="";
		long lLngFileSize = 0L;
		String lstrDocFileSize = "";
		String sOutputXml="";
		String sMappedInputXml="";
		String statusXML="";
		boolean catchflag=false;
		String strFileNametoMove="";
		String source="";
		String dest="";
		String CIF="";
		String MobileNo="";
		String ECRN="";
		String PassportNo="";
		String EmirateID="";
		String OD_Status="0";
		sessionCheckInt=0;
		
		Date CurrentDateTime= new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		String formattedCurrentDateTime = dateFormat.format(CurrentDateTime);
		
		DCCDocumentLog.mLogger.info("formattedCurrentDateTime--"+formattedCurrentDateTime);
		
			try {
				
				String strQuery_1="SELECT wi_name,Status,insertion_date FROM NG_DCC_FTS_DOC WITH (NOLOCK) WHERE Status='R'";
				List<Map<String, String>> DataFromDB = new ArrayList<Map<String, String>>();
				DataFromDB = getDataFromDBMap(strQuery_1, cabinetName, sessionId, jtsIP, jtsPort);
				
				for (Map<String, String> entry : DataFromDB) {
					
					wi_name = entry.get("wi_name");
					Status = entry.get("Status");
					insertion_date = entry.get("insertion_date");
					
					DCCDocumentLog.mLogger.info("wi_name in FTS_DOC "+wi_name);
					DCCDocumentLog.mLogger.info("Status in FTS_DOC "+Status);
					DCCDocumentLog.mLogger.info("insertion_date in FTS_DOC "+insertion_date);
					
					while(sessionCheckInt<loopCount) {
						try {

							XMLParser objXMLParser_WIWS = new XMLParser();
							String strQuery="SELECT itemindex,CURR_WSNAME,Wi_Name,CIF,MobileNo,ECRN,PassportNo,EmirateID FROM NG_DCC_EXTTABLE WITH (NOLOCK) WHERE Wi_Name='"+wi_name+"'";
							String InputXML_WILoc = CommonMethods.apSelectWithColumnNames(strQuery,cabinetName, sessionId);
							DCCDocumentLog.mLogger.info("Getting InputXML_WILoc "+InputXML_WILoc);
							outputXML_WILoc = WFNGExecute(InputXML_WILoc, jtsIP, Integer.parseInt(jtsPort), 1);
							DCCDocumentLog.mLogger.info("OutputXML outputXML_WILoc "+outputXML_WILoc);
							objXMLParser_WIWS.setInputXML(outputXML_WILoc);
							mainCode_WILoc=objXMLParser_WIWS.getValueOf("MainCode");
							iTotalrec_WILoc= Integer.parseInt(objXMLParser_WIWS.getValueOf("TotalRetrieved"));
						} 
						catch (Exception e) {
							sessionCheckInt++;
							waiteloopExecute(waitLoop);
							DCCDocumentLog.mLogger.info("Exception in outputXML_WILoc apSelectWithColumnNames ");
							continue;
						}
						if (!mainCode_WILoc.equalsIgnoreCase("0")) {
							sessionId  = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
						}
						else {
							sessionCheckInt++;
							break;
						}
					}
					if (mainCode_WILoc.equalsIgnoreCase("0") && iTotalrec_WILoc > 0) {
						
						ActivityName=CommonMethods.getTagValues(outputXML_WILoc, "CURR_WSNAME");
						parentFolderIndex=CommonMethods.getTagValues(outputXML_WILoc, "itemindex");
						CIF=CommonMethods.getTagValues(outputXML_WILoc, "CIF");
						MobileNo=CommonMethods.getTagValues(outputXML_WILoc, "MobileNo");
						ECRN=CommonMethods.getTagValues(outputXML_WILoc, "ECRN");
						PassportNo=CommonMethods.getTagValues(outputXML_WILoc, "PassportNo");
						EmirateID=CommonMethods.getTagValues(outputXML_WILoc, "EmirateID");
						
						DCCDocumentLog.mLogger.info("FOLDER INDEX : "+ parentFolderIndex);
						DCCDocumentLog.mLogger.info("ActivityName in FTS_DOC "+ActivityName);
	
						if("Reject".equalsIgnoreCase(ActivityName) || "Exit".equalsIgnoreCase(ActivityName)) {
							
							DCCDocumentLog.mLogger.info("Case is at "+ActivityName);
							
							String path=DCCDocumentCofigParamMap.get("filePath")+ File.separator+wi_name;
							path=path+File.separator+"FTS";
							
							DCCDocumentLog.mLogger.info("path: "+path);
							source=path;
							File outputFolder=new File(path);
							
							if(outputFolder.exists()) {
							
								DCCDocumentLog.mLogger.info("Folder exist......"+wi_name);
								File folder = new File(path);
								File[] listOfFiles = folder.listFiles();
								DCCDocumentLog.mLogger.info("# of FTS_DOC documents to add in this folder--"+listOfFiles.length);
								
								if(CIF==null || "".equalsIgnoreCase(CIF)) {
									folderHierarchy="***"; // Hritik -  bcoz we don't have CIF to archive the documents in the cif folder hierarchy so folder name "***" is configured in the OD taking the folder index.
								}else {
									folderHierarchy=CIF;
								}
								
								DCCDocumentLog.mLogger.info("folderHierarchy: "+folderHierarchy);
								
								parentFolderIndex = addOrUpdateFolderIfExist(folderHierarchy,sessionId, cabinetName,mainParentFolderIndex,sessionId);
								
								if (parentFolderIndex != null && !parentFolderIndex.isEmpty() && Integer.parseInt(parentFolderIndex) >= 0) {
									
									for (File listOfDoc : listOfFiles) {
										
										if (listOfDoc.isFile()) {
											
											strfullFileName = listOfDoc.getName();
											strFileNametoMove=strfullFileName;
											try {
												strfullFileName=strfullFileName.substring(strfullFileName.indexOf("_",3)+1);
												DCCDocumentLog.mLogger.info(strfullFileName);
											}catch(StringIndexOutOfBoundsException e){
												DCCDocumentLog.mLogger.info("string out of range : "+ strfullFileName);
											}
											
											DCCDocumentLog.mLogger.info("workItemName: "+wi_name+" strfullFileName : "+strfullFileName);
											strDocumentName = getDocumentName(strfullFileName);
											strExtension = strfullFileName.substring(strfullFileName.lastIndexOf(".")+1,strfullFileName.length());
											
											DCCDocumentLog.mLogger.info("strDocumentName FTS_DOC documents "+strDocumentName);
											DCCDocumentLog.mLogger.info("strExtension FTS_DOC documents "+strExtension);
											
											if(strExtension.equalsIgnoreCase("PNG")) {
												DCCDocumentLog.mLogger.info("workItemName: "+workItemName+" strfullFileName : "+strfullFileName);
												DCCDocumentLog.mLogger.info("strExtension: Before PNG: "+strfullFileName);
												strExtension="JPG";
												DCCDocumentLog.mLogger.info("strExtension: After PNG: "+strfullFileName);
											}
											if(strExtension.equalsIgnoreCase("JPG") || strExtension.equalsIgnoreCase("TIF") || strExtension.equalsIgnoreCase("JPEG") || strExtension.equalsIgnoreCase("TIFF")) {
												DocumentType = "I";
											}
											else {
												DocumentType = "N";
											}
											
											for (int j = 0; j < 3; j++) {
												DCCDocumentLog.mLogger.info("FTS DOC workItemName: "+wi_name+" Inside for Loop!");
												
												JPISIsIndex ISINDEX = new JPISIsIndex();
												JPDBRecoverDocData JPISDEC = new JPDBRecoverDocData();
												lLngFileSize = listOfDoc.length();
												lstrDocFileSize = Long.toString(lLngFileSize);
												
												if(lLngFileSize != 0L) {
													DCCDocumentLog.mLogger.info("FTS DOCworkItemName: "+wi_name+" The Document address is: "+path+System.getProperty("file.separator")+listOfDoc.getName());
													String docPath=path+System.getProperty("file.separator")+listOfDoc.getName();
													try {
														DCCDocumentLog.mLogger.info("FTS DOCworkItemName: "+wi_name+" before CPISDocumentTxn AddDocument MT: ");
														
														if(smsPort.startsWith("33")) {
															CPISDocumentTxn.AddDocument_MT(null, jtsIP , Short.parseShort(smsPort), cabinetName, Short.parseShort(volumeID), docPath, JPISDEC, "",ISINDEX);
														}
														else {
															CPISDocumentTxn.AddDocument_MT(null, jtsIP , Short.parseShort(smsPort), cabinetName, Short.parseShort(volumeID), docPath, JPISDEC, null,"JNDI", ISINDEX);
														}	
														
														DCCDocumentLog.mLogger.info("workItemName: "+wi_name+" after CPISDocumentTxn AddDocument MT: ");
														
														String sISIndex = ISINDEX.m_nDocIndex + "#" + ISINDEX.m_sVolumeId;
														DCCDocumentLog.mLogger.info("workItemName: "+wi_name+" sISIndex: "+sISIndex);
														sMappedInputXml = CommonMethods.getNGOAddDocument(parentFolderIndex,strDocumentName,DocumentType,strExtension,sISIndex,lstrDocFileSize,volumeID,cabinetName,sessionId);
														DCCDocumentLog.mLogger.debug("workItemName: "+workItemName+" sMappedInputXml "+sMappedInputXml);
														
														sOutputXml=WFNGExecute(sMappedInputXml,jtsIP,Integer.parseInt(jtsPort),1);
														sOutputXml=sOutputXml.replace("<Document>","");
														sOutputXml=sOutputXml.replace("</Document>","");
														DCCDocumentLog.mLogger.info("workItemName: "+wi_name+" Output xml For NGOAddDocument Call: "+sOutputXml);
														statusXML = CommonMethods.getTagValues(sOutputXml,"Status");
														DCCDocumentLog.mLogger.info("workItemName: "+wi_name+" The maincode of the output xml file is " +statusXML);
														
													}
													catch (NumberFormatException e) {
														DCCDocumentLog.mLogger.info("wi_name:"+e.getMessage());
														e.printStackTrace();
														catchflag=true;
													}
													catch (JPISException e) {
														DCCDocumentLog.mLogger.info("wi_name:"+e.getMessage());
														e.printStackTrace();
														catchflag=true;
													}
													catch (Exception e) {
														DCCDocumentLog.mLogger.info("wi_name:"+e.getMessage());
														e.printStackTrace();
														catchflag=true;
													}
												}
												if(statusXML.equalsIgnoreCase("0")) {
													j=3;
												}
											}
											// 	lastfoldername="FTS"; -- Hard coded coz always wil be FTS Folder.
											now = new Date();
											Format formatter = new SimpleDateFormat("dd-MMM-yy");
											sdate = formatter.format(now);
											DCCDocumentLog.mLogger.info("statusXML maincode is--"+statusXML);
											

										/*	if("0".equalsIgnoreCase(statusXML)) {
												try{	
													// Add to OD -
													List<String> indexIdList = Arrays.asList(dataClassFieldsId.split(","));
													List<String> indexValueList = new ArrayList<String>();
													indexValueList.add(wi_name);
													indexValueList.add(CIF);
													indexValueList.add(ECRN);
													indexValueList.add(PassportNo);
													indexValueList.add(EmirateID);
													indexValueList.add(MobileNo);
													
													OD_Status=ChangeFolderProperty(cabinetName,sessionId,parentFolderIndex,dataClassFolder,indexIdList,indexValueList,jtsIP,jtsPort);
													
												}
												catch(Exception e) {
													DCCDocumentLog.mLogger.info("Exception while changing folder properties "+e.getMessage());
													
													String Subject = "Document import error while applying data class " ;
													String Message = "Error while importing document for WI "+ wi_name ;
													String mailInputXml = sendMail(cabinetName, sessionId, FromMailID, ToMailID, Subject, Message, "", ""); 
													
													String mailOutXml = WFNGExecute(mailInputXml, jtsIP, Integer.parseInt(jtsPort),1);
													DCCDocumentLog.mLogger.info("Email notification sent mailInputXml: "+mailInputXml+"\n mailOutXml: "+mailOutXml);
												}
											}
										*/	
											if("0".equalsIgnoreCase(statusXML) /*&& "0".equalsIgnoreCase(OD_Status)*/) {
												
												DCCDocumentLog.mLogger.debug("File "+strFileNametoMove +" destination "+destFilePath);
												String source_new = path+System.getProperty("file.separator")+strFileNametoMove+"";
												dest = ""+destFilePath+System.getProperty("file.separator")+sdate+System.getProperty("file.separator")+wi_name+System.getProperty("file.separator")+"FTS";
												TimeStamp=get_timestamp();
												DCCDocumentLog.mLogger.debug("Source of file to move- "+source_new);
												DCCDocumentLog.mLogger.debug("Final destination of file- "+dest);
												newFilename = Move(dest,source_new,TimeStamp);
												
											}
											
											DCCDocumentLog.mLogger.info("catch flag is--"+catchflag);
											
											if(!("0".equalsIgnoreCase(statusXML)) || catchflag==true /*|| !("0".equalsIgnoreCase(OD_Status))*/) {
												
												DCCDocumentLog.mLogger.info("WI Going to the error folder");
												DCCDocumentLog.mLogger.debug("File "+strFileNametoMove +" destination "+destFilePath);
												source = path+System.getProperty("file.separator")+strFileNametoMove+"";
												dest = ""+ErrorFolder+System.getProperty("file.separator")+sdate+System.getProperty("file.separator")+wi_name+System.getProperty("file.separator")+"FTS";
												DCCDocumentLog.mLogger.debug("Final destination of file- "+destFilePath);
												DCCDocumentLog.mLogger.debug("Source of file to move- "+source);
												DCCDocumentLog.mLogger.debug("dest of file to move- "+dest);
												TimeStamp=get_timestamp();
												newFilename = Move(dest,source,TimeStamp);
												continue;
											}
										}
									}
								}
								else {
									DCCDocumentLog.mLogger.info(" ");
									catchflag=true;
									String Subject = "Document import error while applying data class " ;
									String Message = "Error while importing document for WI "+ wi_name ;
									String mailInputXml = sendMail(cabinetName, sessionId, FromMailID, ToMailID, Subject, Message, "", ""); 
									
									String mailOutXml = WFNGExecute(mailInputXml, jtsIP, Integer.parseInt(jtsPort),1);
									DCCDocumentLog.mLogger.info("Email notification sent mailInputXml: "+mailInputXml+"\n mailOutXml: "+mailOutXml);
									catchflag=true;
								}
								
								try {
									String decisionToUpdate="";
									String ErrorMsg="";
									if("0".equalsIgnoreCase(statusXML) /*&& "0".equalsIgnoreCase(OD_Status)*/) {
										
										String Remarks_dec="Expected FTS Documents Attached by Utility";
										folder.delete();
										decisionToUpdate="Success";
										DCCDocumentLog.mLogger.info("Current date time is---"+get_timestamp());	
										updateExternalTable("NG_DCC_FTS_DOC","Status","'C'","wi_name='"+wi_name+"'");
										updateExternalTable("NG_DCC_FTS_DOC","Action_date","'"+formattedCurrentDateTime+"'","wi_name='"+wi_name+"'");
										
										historyCaller(wi_name,decisionToUpdate,Remarks_dec,formattedCurrentDateTime);
									}
									else if(!("0".equalsIgnoreCase(statusXML)) || catchflag==true /*|| !("0".equalsIgnoreCase(OD_Status))*/) {
										
										folder.delete();
										if(ErrorMsg.trim().equalsIgnoreCase(""))
										ErrorMsg = "Error in attaching documents to the Omnidocs or Workitem";
										decisionToUpdate="Failure";
										historyCaller(wi_name,decisionToUpdate,ErrorMsg,formattedCurrentDateTime);
									}
								}
								catch (Exception e) {
									DCCDocumentLog.mLogger.info("Exception......"+e.getMessage());
									e.printStackTrace();
								}
								//delete the empty workitem folder
								File wiFolder= new File(DCCDocumentCofigParamMap.get("filePath")+ File.separator+wi_name);
								if(wiFolder.exists() && wiFolder.list().length==0){
									wiFolder.delete();
								}
							}
							else {
								DCCDocumentLog.mLogger.info("Folder doesn't exist so continue......"+wi_name);
								continue;								
							}	
						}
					
					else {
						String path=DCCDocumentCofigParamMap.get("filePath")+ File.separator+wi_name;
						path=path+File.separator+"FTS";
							
						DCCDocumentLog.mLogger.info("path: "+path);
						source=path;
						File outputFolder=new File(path);
							
						if(outputFolder.exists()) {
							DCCDocumentLog.mLogger.info("Folder exist...... " +path);
							File folder = new File(path);
							File[] listOfFiles = folder.listFiles();
							DCCDocumentLog.mLogger.info("# of FTS_DOC documents to add in this folder--"+listOfFiles.length);
							for (File listOfDoc : listOfFiles) {
								
								if (listOfDoc.isFile()) {
									
									strfullFileName = listOfDoc.getName();
									strFileNametoMove=strfullFileName;
									try {
										strfullFileName=strfullFileName.substring(strfullFileName.indexOf("_",3)+1);
										DCCDocumentLog.mLogger.info(strfullFileName);
									}catch(StringIndexOutOfBoundsException e){
										DCCDocumentLog.mLogger.info("string out of range : "+ strfullFileName);
									}
									
									DCCDocumentLog.mLogger.info("workItemName: "+wi_name+" strfullFileName : "+strfullFileName);
									strDocumentName = getDocumentName(strfullFileName);
									strExtension = strfullFileName.substring(strfullFileName.lastIndexOf(".")+1,strfullFileName.length());
										
									DCCDocumentLog.mLogger.info("strDocumentName FTS_DOC documents "+strDocumentName);
									DCCDocumentLog.mLogger.info("strExtension FTS_DOC documents "+strExtension);
									
									if(strExtension.equalsIgnoreCase("PNG")) {
										DCCDocumentLog.mLogger.info("workItemName: "+wi_name+" strfullFileName : "+strfullFileName);
										DCCDocumentLog.mLogger.info("strExtension: Before PNG: "+strfullFileName);
										strExtension="JPG";
										DCCDocumentLog.mLogger.info("strExtension: After PNG: "+strfullFileName);
									}
									if(strExtension.equalsIgnoreCase("JPG") || strExtension.equalsIgnoreCase("TIF") || strExtension.equalsIgnoreCase("JPEG") || strExtension.equalsIgnoreCase("TIFF")) {
										DocumentType = "I";
									}
									else {
										DocumentType = "N";
									}
										
									for (int j = 0; j < 3; j++) {
										DCCDocumentLog.mLogger.info("FTS DOC workItemName: "+wi_name+" Inside for Loop!");
											
										JPISIsIndex ISINDEX = new JPISIsIndex();
										JPDBRecoverDocData JPISDEC = new JPDBRecoverDocData();
										lLngFileSize = listOfDoc.length();
										lstrDocFileSize = Long.toString(lLngFileSize);
											
										if(lLngFileSize != 0L) {
											DCCDocumentLog.mLogger.info("FTS DOCworkItemName: "+wi_name+" The Document address is: "+path+System.getProperty("file.separator")+listOfDoc.getName());
											String docPath=path+System.getProperty("file.separator")+listOfDoc.getName();
											try {
												DCCDocumentLog.mLogger.info("FTS DOCworkItemName: "+wi_name+" before CPISDocumentTxn AddDocument MT: ");
												
												if(smsPort.startsWith("33")) {
													CPISDocumentTxn.AddDocument_MT(null, jtsIP , Short.parseShort(smsPort), cabinetName, Short.parseShort(volumeID), docPath, JPISDEC, "",ISINDEX);
												}
												else {
													CPISDocumentTxn.AddDocument_MT(null, jtsIP , Short.parseShort(smsPort), cabinetName, Short.parseShort(volumeID), docPath, JPISDEC, null,"JNDI", ISINDEX);
												}	
													
												DCCDocumentLog.mLogger.info("workItemName: "+wi_name+" after CPISDocumentTxn AddDocument MT: ");
												
												String sISIndex = ISINDEX.m_nDocIndex + "#" + ISINDEX.m_sVolumeId;
												DCCDocumentLog.mLogger.info("workItemName: "+wi_name+" sISIndex: "+sISIndex);
												sMappedInputXml = CommonMethods.getNGOAddDocument(parentFolderIndex,strDocumentName,DocumentType,strExtension,sISIndex,lstrDocFileSize,volumeID,cabinetName,sessionId);
												DCCDocumentLog.mLogger.debug("workItemName: "+wi_name+" sMappedInputXml "+sMappedInputXml);
												
												sOutputXml=WFNGExecute(sMappedInputXml,jtsIP,Integer.parseInt(jtsPort),1);
												sOutputXml=sOutputXml.replace("<Document>","");
												sOutputXml=sOutputXml.replace("</Document>","");
												DCCDocumentLog.mLogger.info("workItemName: "+wi_name+" Output xml For NGOAddDocument Call: "+sOutputXml);
												//DCCDocumentLog.mLogger.debug("Output xml For NGOAddDocument Call: "+sOutputXml);
												statusXML = CommonMethods.getTagValues(sOutputXml,"Status");
												DCCDocumentLog.mLogger.info("workItemName: "+wi_name+" The maincode of the output xml file is " +statusXML);
													
											}
											catch (NumberFormatException e) {
												DCCDocumentLog.mLogger.info("wi_name:"+e.getMessage());
												e.printStackTrace();
												catchflag=true;
											}
											catch (JPISException e) {
												DCCDocumentLog.mLogger.info("wi_name:"+e.getMessage());
												e.printStackTrace();
												catchflag=true;
											}
											catch (Exception e) {
												DCCDocumentLog.mLogger.info("wi_name:"+e.getMessage());
												e.printStackTrace();
												catchflag=true;
											}
										}
										if(statusXML.equalsIgnoreCase("0")) {
											j=3;
										}
									}
									// 	lastfoldername="FTS"; -- Hard coded coz always wil be FTS Folder.
									now = new Date();
									Format formatter = new SimpleDateFormat("dd-MMM-yy");
									sdate = formatter.format(now);
									DCCDocumentLog.mLogger.info("statusXML maincode is--"+statusXML);
									if("0".equalsIgnoreCase(statusXML)) {
										DCCDocumentLog.mLogger.debug("File "+strFileNametoMove +" destination "+destFilePath);
										String source_new = path+System.getProperty("file.separator")+strFileNametoMove+"";
										dest = ""+destFilePath+System.getProperty("file.separator")+sdate+System.getProperty("file.separator")+wi_name+System.getProperty("file.separator")+"FTS";
										TimeStamp=get_timestamp();
										DCCDocumentLog.mLogger.debug("Source of file to move- "+source);
										DCCDocumentLog.mLogger.debug("Final destination of file- "+destFilePath);
										newFilename = Move(dest,source_new,TimeStamp);
									}	
									DCCDocumentLog.mLogger.info("catch flag is--"+catchflag);
									if(!("0".equalsIgnoreCase(statusXML)) || catchflag==true) {
										DCCDocumentLog.mLogger.info("WI Going to the error folder");
										DCCDocumentLog.mLogger.debug("File "+strFileNametoMove +" destination "+destFilePath);
										source = path+System.getProperty("file.separator")+strFileNametoMove+"";
										dest = ""+ErrorFolder+System.getProperty("file.separator")+sdate+System.getProperty("file.separator")+wi_name+System.getProperty("file.separator")+"FTS";
										DCCDocumentLog.mLogger.debug("Final destination of file- "+destFilePath);
										DCCDocumentLog.mLogger.debug("Source of file to move- "+source);
										TimeStamp=get_timestamp();
										newFilename = Move(dest,source,TimeStamp);
										continue;
									}
								}
							}
							try {
								String decisionToUpdate="";
								String ErrorMsg="";
								if("0".equalsIgnoreCase(statusXML)){
									String Remarks_dec="Expected FTS Documents Attached by Utility";
									folder.delete();
									decisionToUpdate="Success";
									DCCDocumentLog.mLogger.info("Current date time is---"+get_timestamp());
									updateExternalTable("NG_DCC_FTS_DOC","Status","'C'","wi_name='"+wi_name+"'");
									updateExternalTable("NG_DCC_FTS_DOC","Action_date","'"+formattedCurrentDateTime+"'","wi_name='"+wi_name+"'");
									historyCaller(wi_name,decisionToUpdate,Remarks_dec,formattedCurrentDateTime);
								}
								else if(!("0".equalsIgnoreCase(statusXML)) || catchflag==true) {
									folder.delete();
									if(ErrorMsg.trim().equalsIgnoreCase(""))
									ErrorMsg = "Expected Documents are not available or error in attaching";
									decisionToUpdate="Failure";
									historyCaller(wi_name,decisionToUpdate,ErrorMsg,formattedCurrentDateTime);
								}
							}
							catch (Exception e) {
								DCCDocumentLog.mLogger.info("Exception......"+e.getMessage());
								e.printStackTrace();
							}
							//delete the empty workitem folder
							File wiFolder= new File(DCCDocumentCofigParamMap.get("filePath")+ File.separator+wi_name);
							if(wiFolder.exists() && wiFolder.list().length==0){
								wiFolder.delete();
							}
						}
						else {
							DCCDocumentLog.mLogger.info("Folder doesn't exist so continue......"+wi_name);
							continue;								
						}
					}
				}
			}			
		}
		catch(Exception e) {
			DCCDocumentLog.mLogger.info("Exception in FTS_DOC "+e.toString());
			DCCDocumentLog.mLogger.info("Exception in FTS_DOC "+e.getMessage());
		}	
	} // Hritik - 17/01/24 PDSC-1368 - END
	

	private static String addOrUpdateFolderIfExist(String HierarchyKey,String userDBid,String cabinetName, String mainParentFolderIndex,String sessionID)
	{
		// System.out.println("Inside addOrUpdateFolderIfExist..! ");

		String[] sTempFolders = HierarchyKey.split("#");
		String strPreviousFolder="0";
		
		String folderName = "";
		String strLookInFolder="0";
		String statusCode="";
		XMLParser xmlparser;
		String strAddFolderInXML="";
		String strAddFolderOutXML="";
		
		strPreviousFolder= mainParentFolderIndex;

		for(int i=0;i<sTempFolders.length;i++) {
			boolean AddFolder= true;
			DCCDocumentLog.mLogger.info("sTempFolders= "+sTempFolders[i]+" at i= "+i);
			
			DCCDocumentLog.mLogger.info("AddFolder= "+String.valueOf(AddFolder));
			folderName = sTempFolders[i];
			try {
				String folder_index = "select top 1 FolderIndex from PDBFolder where Name = '"+folderName+"' and ParentFolderIndex='"+strPreviousFolder+"';";
				String APSelectfolderIndex = CommonMethods.apSelectWithColumnNames(folder_index,cabinetName, sessionId);
				String APSelectOutputfolderIndex="";
				DCCDocumentLog.mLogger.info("APSelectInputDocSize : " + APSelectfolderIndex);
				XMLParser objXMLParser = new XMLParser();
				try {
					APSelectOutputfolderIndex=WFNGExecute(APSelectfolderIndex,jtsIP,Integer.parseInt(jtsPort),1);
					DCCDocumentLog.mLogger.info("APSelectOutputfolderIndex: " + APSelectOutputfolderIndex);
				}
				catch(Exception e) {
					DCCDocumentLog.mLogger.info("Exception in Execute : " + e.getMessage());
					sessionID = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
				}					
				
				objXMLParser.setInputXML(APSelectOutputfolderIndex);
				String mainCodeAPSelectDocSize = "";
				mainCodeAPSelectDocSize=objXMLParser.getValueOf("MainCode");
				if (mainCodeAPSelectDocSize.equalsIgnoreCase("0")) {
					strLookInFolder = objXMLParser.getValueOf("FolderIndex");
					strPreviousFolder =strLookInFolder;
					AddFolder= false;
				}
			}
			catch(Exception e) {
				DCCDocumentLog.mLogger.info("Exception occured in addOrUpdateFolderIfExist : " + e.getMessage());
			}
			/*
			if(AddFolder) {
				
				DCCDocumentLog.mLogger.info("sTempFolders.length-1= "+folderName);

				DCCDocumentLog.mLogger.info("At i = "+i+" sTempFolders= "+sTempFolders[i]);

				strAddFolderInXML = getAddFolderInputwithoutdc( userDBid, strPreviousFolder , folderName,cabinetName);
				DCCDocumentLog.mLogger.info("strAddFolderInXML ::"+strAddFolderInXML);

				try { 
					strAddFolderOutXML = WFNGExecute(strAddFolderInXML,jtsIP,Integer.parseInt(jtsPort),1);
					
					System.out.println("strAddFolderInXML ::"+strAddFolderOutXML);
					xmlparser = new XMLParser(strAddFolderOutXML);
					statusCode=xmlparser.getValueOf("Status");
					if(statusCode.equalsIgnoreCase("0"))
					{
						strLookInFolder = xmlparser.getValueOf("FolderIndex");
						strPreviousFolder =strLookInFolder;
						System.out.println("value of folder index after execution of add folder call################# "+strLookInFolder);

					}
				}
				catch(Exception e) {
					System.out.println("Error in adding folder in the folder--"+e.toString());
				}
			}
*//*			else
			{
				try
				{
					folderName=sTempFolders[i];
					System.out.println("inside search folder else condition , strLookInFolder= "+strLookInFolder+" sTempFolders[i]= '"+sTempFolders[i]+"' for i= "+i);
					String strSearchFolderInXML=GenerateInputXML.getSearchFolderInput(cabinetName,userDBid,strLookInFolder,folderName);
					String strSearchFolderOutXML = CommonConnection.WFNGExecute(strSearchFolderInXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 0 , mLogger);

					//	String strSearchFolderOutXML = WFCallBroker.execute(strSearchFolderInXML, sJtsAddress, Integer.parseInt(CommonFields.sPortID), 1);
					xmlparser = new XMLParser(strSearchFolderOutXML);

					statusCode=xmlparser.getValueOf("Status");
					if("0".equalsIgnoreCase(statusCode))
					{
						if(!"0".equalsIgnoreCase(xmlparser.getValueOf("NoOfRecordsFetched")))
						{
							strLookInFolder = xmlparser.getValueOf("FolderIndex");
							mLogger.info("inside search folder else condition and folder is already exist with folder id--, strLookInFolder= "+strLookInFolder);
						}
						else
						{
							try {
								strAddFolderInXML = GenerateInputXML.getAddFolderInputwithoutdc( userDBid, strPreviousFolder , sTempFolders[i],cabinetName);
								strAddFolderOutXML = CommonConnection.WFNGExecute(strAddFolderInXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 0 , mLogger);

								// strAddFolderOutXML = WFCallBroker.execute(strAddFolderInXML, CommonFields.sJtsAddress, Integer.parseInt(CommonFields.sPortID), 1);
								xmlparser = new XMLParser(strAddFolderOutXML);
								System.out.println("output xml for add folder call when executed for the first time ############# "+strAddFolderOutXML);
								statusCode=xmlparser.getValueOf("Status");
								if("0".equalsIgnoreCase(statusCode))
								{
									strLookInFolder = xmlparser.getValueOf("FolderIndex");
									mLogger.info("value of folder index after execution of add folder call for FIRST TIME ################# "+strLookInFolder);
									AddFolder = true;

								}
							}
							catch (Exception e)
							{
								mLogger.info("Error while adding the first new folder of the hierachy--"+e.toString());
							}
						}
					}
				}
				catch(Exception e)
				{
					mLogger.info("Error while searching the first folder of the hierachy--"+e.toString());
				}
			}*/
		}
		DCCDocumentLog.mLogger.info("strLookInFolder: " + strLookInFolder);
		return strLookInFolder;
	}

	private static  String getAddFolderInputwithoutdc(String sUserDBID , String sParentFolderIndex , String sFolderName,String sCabinetName){

		String inputXml = "<?xml version=\"1.0\"?>" 
				+	"<NGOAddFolder_Input>" 
				+	"<Option>NGOAddFolder</Option>" 
				+	"<CabinetName>"+ sCabinetName + "</CabinetName>"
				+	"<UserDBId>" + sUserDBID + "</UserDBId><Folder>"
				+	"<ParentFolderIndex>" + sParentFolderIndex + "</ParentFolderIndex>"
				+	"<FolderName>"+ sFolderName +"</FolderName>"
				+	"<CreationDateTime></CreationDateTime>"
				+	"<ExpiryDateTime></ExpiryDateTime>"
				+	"<AccessType>I</AccessType>"
				+	"<ImageVolumeIndex>1</ImageVolumeIndex>"
				+	"<Location></Location>"
				+	"<Comment></Comment>"
				+	"<NoOfDocuments></NoOfDocuments>"
				+	"<NoOfSubFolders></NoOfSubFolders>"
				+	"<DataDefinition>"
				+	"<DataDefName></DataDefName>"
				+ 	"</DataDefinition>" 
				+ 	"</Folder>"
				+ 	"</NGOAddFolder_Input>";
		//CBKLogger.writeLog("inputXml for getAddFolderInputwithoutdc:::: ",inputXml);
		return	inputXml;
	}
	private static String sendMail(String cabinetname,String sessionID,String mailFrom, String mailTo, String mailSubject,String mailMessage,String attachmentIndex, String attachmentName)
	{
		if(attachmentIndex==null)
			attachmentIndex="";
		if(attachmentName==null)
			attachmentName="";

		WFInputXml wfInputXml = new WFInputXml();

		wfInputXml.appendStartCallName("WFAddToMailQueue", "Input");
		wfInputXml.appendTagAndValue("EngineName",cabinetname);
		wfInputXml.appendTagAndValue("SessionId",sessionID);
		wfInputXml.appendTagAndValue("MailFrom",mailFrom);
		wfInputXml.appendTagAndValue("MailTo",mailTo);
		wfInputXml.appendTagAndValue("MailCC","");
		wfInputXml.appendTagAndValue("MailSubject",mailSubject);
		wfInputXml.appendTagAndValue("MailMessage",mailMessage);
		wfInputXml.appendTagAndValue("AttachmentISIndex",attachmentIndex);
		wfInputXml.appendTagAndValue("AttachmentNames",attachmentName);
		wfInputXml.appendTagAndValue("AttachmentExts","");
		wfInputXml.appendEndCallName("WFAddToMailQueue","Input");

		return wfInputXml.toString();
	}
	
	private static String ChangeFolderProperty(String cabinetName,String sessionId,String FolderIndex,String DataDefId,List<String> indexIdList,List<String> indexValueList, String jtsIP,String jtsPort) {
		
		String return_value="0";
		try {
			
			String APChangeFolderPropertyInput = apNGOChangeFolderProperty(cabinetName,sessionId,FolderIndex,DataDefId,indexIdList,indexValueList);
			DCCDocumentLog.mLogger.info("apNGOChangeFolderProperty : " + APChangeFolderPropertyInput);
			String APChangeFolderPropertyOutput="";
			XMLParser objXMLParser = new XMLParser();
			try {
				APChangeFolderPropertyOutput= WFNGExecute(APChangeFolderPropertyInput, jtsIP, Integer.parseInt(jtsPort), 1);
			}
			catch(Exception e) {
				DCCDocumentLog.mLogger.info("Exception in Execute : " + e);
				sessionId = CommonConnection.getSessionID(DCCDocumentLog.mLogger, false);
			}					
			DCCDocumentLog.mLogger.info("APChangeFolderPropertyOutput : " + APChangeFolderPropertyOutput);

			objXMLParser.setInputXML(APChangeFolderPropertyOutput);
			String mainCodeAPSelectDocSize = "";
			mainCodeAPSelectDocSize=objXMLParser.getValueOf("MainCode");
			if (!mainCodeAPSelectDocSize.equalsIgnoreCase("0")) {
				DCCDocumentLog.mLogger.info("Problem in Changing Folder Property for FolderIndex: "+FolderIndex);	
				DCCDocumentLog.mLogger.info("return_value: "+mainCodeAPSelectDocSize);
				return_value = "1";
			}
			else {
				return_value="0";
				DCCDocumentLog.mLogger.info("FolderIndex: "+FolderIndex);
				DCCDocumentLog.mLogger.info("Return_value: "+return_value);
			}
		}
		catch(Exception e){
			return_value = "1";
			DCCDocumentLog.mLogger.info("Exception Occured in WF NG ChangeFolderProperty : "+e.getMessage());
		}
		return return_value;
	}
	
	private static String apNGOChangeFolderProperty(String cabinetName,String sessionId,String FolderIndex,String DataDefId,List<String> indexIdList,List<String> indexValueList) {
           String NGOChangeFolderPropertyXML="<?xml version=\"1.0\"?><NGOChangeFolderProperty_Input>"
                        + "<Option>NGOChangeFolderProperty</Option><CabinetName>"+cabinetName+"</CabinetName>"
                        + "<UserDBId>"+sessionId+"</UserDBId><Folder><FolderIndex>"+FolderIndex+"</FolderIndex><NameLength>255</NameLength>"
                        + "<VersionFlag></VersionFlag><Comment>Not Defined</Comment><DataDefinition><DataDefIndex>"+DataDefId+"</DataDefIndex>"
                        + "<Fields>";
           //TODO For loop on list
           for(int i=0;i<indexIdList.size();i++)
                  NGOChangeFolderPropertyXML += "<Field><IndexId>"+indexIdList.get(i)+"</IndexId><IndexType>S</IndexType><IndexValue>"+indexValueList.get(i)+"</IndexValue></Field>";
           NGOChangeFolderPropertyXML += "</Fields></DataDefinition></Folder></NGOChangeFolderProperty_Input>";
           return NGOChangeFolderPropertyXML;
    }
	
	private static List<Map<String, String>> getDataFromDBMap(String query, String cabinetName, String sessionID, String jtsIP, String jtsPort) {
		List<Map<String, String>> temp = new ArrayList<Map<String, String>>();
		
		try {
			DCCDocumentLog.mLogger.info("Inside function getDataFromDB");
			DCCDocumentLog.mLogger.info("getDataFromDB query is: " + query);
			String InputXML = CommonMethods.apSelectWithColumnNames(query, cabinetName, sessionID);
			String OutXml = WFNGExecute(InputXML, jtsIP, Integer.parseInt(jtsPort), 1);
			OutXml = OutXml.replaceAll("&", "#andsymb#");
			Document recordDoc1 = MapXML.getDocument(OutXml);
			NodeList records1 = recordDoc1.getElementsByTagName("Record");
			if (records1.getLength() > 0) {
				for (int i = 0; i < records1.getLength(); i++) {
					Node n = records1.item(i);
					Map<String, String> t = new HashMap<String, String>();
					if (n.hasChildNodes()) {
						NodeList child = n.getChildNodes();
						for (int j = 0; j < child.getLength(); j++) {
							Node n1 = child.item(j);
							String column = n1.getNodeName();
							String value = n1.getTextContent().replaceAll("#andsymb#", "&");
							if (null != value && !"null".equalsIgnoreCase(value) && !"".equals(value)) {
								DCCDocumentLog.mLogger.info("getDataFromDBMap Setting value of " + column + " as " + value);
								t.put(column, value);
							} else {
								DCCDocumentLog.mLogger.info("getDataFromDBMap Setting value of " + column + " as blank");
								t.put(column, "");
							}
						}
					}
					temp.add(t);
				}
			}

		} catch (Exception e) {
			DCCDocumentLog.mLogger.info("Exception occured in getDataFromDBMap method" + e.getMessage());
		}
		return temp;

	}
		//Grtting Document Type as per IBPS directory
		//Added by om.tiwari 08/10/22
		private String getDocumentName(String strfullFileName)
		{
			try
			{
				String docName="";
				String DocList [] = DocNameList.split(",");
				docName = "Other_Documents";
				for(int i=0;i<DocList.length;i++)
				{
					if(strfullFileName.contains(DocList[i]))
					{
						docName=DocList[i];
						break;
					}
				}
				
			/*	
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
					
		*/		return docName;
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
					DCCDocumentLog.mLogger.info("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
				}
				else
				{
					DCCDocumentLog.mLogger.error("Succesfully updated "+tablename+" table");
					DCCDocumentLog.mLogger.info("Succesfully updated "+tablename+" table");
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
			DCCDocumentLog.mLogger.debug("destFolderPath- "+destFolderPath);
			DCCDocumentLog.mLogger.debug("srcFolderPath- "+srcFolderPath);
			DCCDocumentLog.mLogger.debug("append- "+append);
			
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
				DCCDocumentLog.mLogger.info("SecurityException");
			}
			catch (NullPointerException lobjNPExp)
			{
				DCCDocumentLog.mLogger.info("NullPointerException");
			}
			catch (Exception lobjExp)
			{
				DCCDocumentLog.mLogger.info("Exception");
			}
			if (!lbSTPuccess)
			{
				DCCDocumentLog.mLogger.info("lbSTPuccess");
			}
			else
			{
				DCCDocumentLog.mLogger.info("else");
			}
			objDestFolder = null;
			objsrcFolderPath = null;
			lobjNewFolder = null;
		}
		catch (Exception lobjExp)
		{
			DCCDocumentLog.mLogger.info("Exception in Move: "+lobjExp.getMessage());
		}
		
		DCCDocumentLog.mLogger.debug("newFilename- "+newFilename);
		
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
		String prevStep=getPreviousWorkStep(processInstanceID,workItemId);
		try{
			String path=DCCDocumentCofigParamMap.get("filePath")+ File.separator+processInstanceID;
			if(Integer.parseInt(workItemId)!=1)
			{
				//String prevStep=getPreviousWorkStep(processInstanceID,workItemId);
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
			//30032023 - KAMRAN FOR SALARY DOC
			else if("SLRYDOC".equalsIgnoreCase(prevStep))
			{
				path=path+File.separator+"Salary_document";
				lastfoldername="Salary_document";
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
	public void deletefolder(){
		Calendar rightNow = Calendar.getInstance();
		
		DCCDocumentLog.mLogger.info("Insde deletefolder :");
				int hour = rightNow.get(Calendar.HOUR_OF_DAY);
				if(hour==18)//(hour<13 && hour>=12)||(hour<13 && hour>=12))
				{
					DCCDocumentLog.mLogger.info("deletefolder time : "+hour);
					DCCDocumentLog.mLogger.info("deletefolder time : "+hour);
					
					File file = new File(destFilePath);
					DCCDocumentLog.mLogger.info("deletefolder  : "+file);
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
					deleteDirectory(file, dateFormat);
					//file.delete();
					
				}
		}

		public static void deleteDirectory(File file, SimpleDateFormat dateFormat) {
			
				for (File subfile : file.listFiles()) {
					
					DCCDocumentLog.mLogger.info("Insde deleteDirectory :");

					try{
						String strModifiedDate = dateFormat.format(subfile.lastModified());
						DCCDocumentLog.mLogger.info(strModifiedDate);
						Date parsedModifiedDate = (Date) new SimpleDateFormat("dd-MMM-yy").parse(strModifiedDate);
						DCCDocumentLog.mLogger.info(parsedModifiedDate);
						// deleteDirectory(subfile);
						if(days(parsedModifiedDate,Integer.parseInt(folderdeleteageing))){
							if (subfile.isDirectory()) {
								FileUtils.deleteDirectory(subfile);
								}
						}
					}
					catch(Exception e){
						DCCDocumentLog.mLogger.info("Exception : "+e.getMessage());
					}
					
					/*if (subfile.isDirectory()) {
						deleteDirectory(subfile,dateFormat);
						}
						*/
					/*try {
						String strModifiedDate = dateFormat.format(subfile.lastModified());
						DCCDocumentLog.mLogger.info(strModifiedDate);
						Date parsedModifiedDate = (Date) new SimpleDateFormat("dd-MMM-yy").parse(strModifiedDate);
						DCCDocumentLog.mLogger.info(parsedModifiedDate);
						// deleteDirectory(subfile);
						if(days(parsedModifiedDate,Integer.parseInt(folderdeleteageing))){
							subfile.delete();
							DCCDocumentLog.mLogger.info("subfile deleted");
							DCCDocumentLog.mLogger.info("deleted");
						}
						else{
							DCCDocumentLog.mLogger.info("not deleted");
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}*/
					
				}
			}
			
			public static boolean  days(Date givenDate,int numDays){
				
				final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
				long currentMillis = new Date().getTime(); 
				long millisInDays = numDays * MILLIS_PER_DAY;
				boolean result = givenDate.getTime() < (currentMillis - millisInDays);
				DCCDocumentLog.mLogger.info(result);
				return result;
				
			}
			
			public void CheckEncrypted(String folder_path,String workItemName,String sdate,String USER_PASSWORD) throws IOException, DocumentException
			{
				try
				{
					DCCDocumentLog.mLogger.info(folder_path);
					
					String SRCMOVE=DCCDocumentCofigParamMap.get("filePath")+File.separator+workItemName+File.separator+"Encrypted Docs";
					String DESTMOVE=DCCDocumentCofigParamMap.get("destFilePath")+File.separator+sdate+File.separator+workItemName+File.separator+"Encrypted Docs";
					DCCDocumentLog.mLogger.info("inside CheckEncrypted");
					String[] possibleEncryptedDocsArray=DCCDocumentCofigParamMap.get("encryptedDocuments").split(",");
				
					File folder = new File(folder_path);  
					File[] listOfFiles = folder.listFiles();
					for (File docsname : listOfFiles)
					{
						
						for(int i=0;i<possibleEncryptedDocsArray.length;i++)
						{
							String encryptedDocname=docsname.getName();
							DCCDocumentLog.mLogger.info("inside for loop");
							DCCDocumentLog.mLogger.info(encryptedDocname);
							if (encryptedDocname.contains(possibleEncryptedDocsArray[i]))
							{
								final String documentloc=folder_path+System.getProperty("file.separator")+encryptedDocname;
								String SRC=documentloc;
								DCCDocumentLog.mLogger.info("Source:-"+SRC);
								String DEST=DCCDocumentCofigParamMap.get("filePath")+File.separator+workItemName+File.separator+"Encrypted Docs"+File.separator;
								DCCDocumentLog.mLogger.info("Dest:-"+DEST);
								DCCDocumentLog.mLogger.info(USER_PASSWORD);
								String Result = "";
								try 
								{			
									DCCDocumentLog.mLogger.info("Removing Document Password");
									File file = new File(DEST+encryptedDocname);
									file.getParentFile().mkdirs();
									copyDocs(SRC,DEST,encryptedDocname);
									if(!"".equalsIgnoreCase(USER_PASSWORD)){
										manipulatePdf(DEST+encryptedDocname, SRC,USER_PASSWORD);
										manipulatePdf_New(DEST+encryptedDocname, SRC,USER_PASSWORD); //For pdfbox
										//replaceDecrypted(SRC, DEST);
										DCCDocumentLog.mLogger.info("Document Created Successfully");
										Result="S";
										DCCDocumentLog.mLogger.info("Encryption Result:-"+Result);
									}
									else{
										DCCDocumentLog.mLogger.info("Blank password not accepted");
										Result="F";
									}
									
								}
								catch(FileNotFoundException e) 
								{
									DCCDocumentLog.mLogger.info("Failed to Find Document : Reason - "+e.getMessage());
									Result="F";
								}
								//DCCDocumentLog.mLogger.info(Result);
							}
							
						}
						
					}
					
					moveDocs(SRCMOVE,DESTMOVE);
				}
				catch(Exception e)
				{
					DCCDocumentLog.mLogger.info(e.toString());
				}
			}


			 void manipulatePdf(String src, String dest,String USER_PASSWORD) throws IOException, DocumentException
			 {
			
				 DCCDocumentLog.mLogger.info("inside manipulate pdf");
				 DCCDocumentLog.mLogger.info("inside manipulate pdf src "+src);
				 DCCDocumentLog.mLogger.info("inside manipulate pdf dest "+dest);
				 DCCDocumentLog.mLogger.info("inside manipulate pdf userpass "+USER_PASSWORD);
				 try
				 {
					DCCDocumentLog.mLogger.info("inside manipulate pdf try");
					PdfReader reader = new PdfReader(src, USER_PASSWORD.getBytes());
					DCCDocumentLog.mLogger.info("Computed Password --> "+new String(reader.computeUserPassword()));
					DCCDocumentLog.mLogger.info("inside manipulate pdf after reader");
					PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
					stamper.close();
					reader.close();
					DCCDocumentLog.mLogger.info("done manipulate pdf");
				}
				catch (Exception e) 
				{
					DCCDocumentLog.mLogger.info("Decyption Error in Manipulate PDF --> "+e.toString());
				    updateExternalTable(ExternalTable,"Is_Document_Decrypted","'N'","wi_name='"+workItemName+"'");
				}
				
			}
			 
			//
			 void manipulatePdf_New(String src, String dest,String USER_PASSWORD) throws IOException, DocumentException
			 {
				try{
					File file = new File (src);
					 PDDocument pd = PDDocument.load(file, USER_PASSWORD);
					 pd.setAllSecurityToBeRemoved(true);
					 pd.save(dest);
				} 
				catch(Exception e){
					DCCDocumentLog.mLogger.info("Decyption Error in Manipulate PDF New --> "+e.toString());
				}
			 }
			//
			 
			public void copyDocs(String src11,String dest1,String filename)
			{
				DCCDocumentLog.mLogger.info("inside copydocs");
				
				
				 Path sourceDirectory = Paths.get(src11);
				 Path targetDirectory = Paths.get(dest1+filename);
				 DCCDocumentLog.mLogger.info("Source:-"+sourceDirectory);
				 DCCDocumentLog.mLogger.info("Dest:-"+targetDirectory);
					File file = new File(dest1);
					if(!file.exists()){
						file.getParentFile().mkdirs();
					}  
				        try
				        {
				            Files.copy(sourceDirectory, targetDirectory);
				            DCCDocumentLog.mLogger.info("done in copydocs");
				        }
				        catch (IOException e) 
				        {
				        	DCCDocumentLog.mLogger.info(e.toString());
				        }
			}
			
			public void moveDocs(String src1,String dest1)
			{
				 Path sourceDirectory = Paths.get(src1);
				 Path targetDirectory = Paths.get(dest1);
				 DCCDocumentLog.mLogger.info("INSIDE MOVEDOCS");
				 DCCDocumentLog.mLogger.info(sourceDirectory);
				 DCCDocumentLog.mLogger.info(targetDirectory);
				 File file = new File(dest1);
						if(!file.exists())
						{
						file.getParentFile().mkdirs();
						}  
						
				        try 
				        {
				            Files.move(sourceDirectory, targetDirectory);
				            DCCDocumentLog.mLogger.info("Done IN moveDocs");
				        } 
				        catch (IOException e) 
				        {
				        	DCCDocumentLog.mLogger.info(e.toString());
				        }
			}
			
		
			//For Decrypting the Document//
			public  String decrypt(String cipherText) throws  NoSuchProviderException, IOException {
				cipherText=cipherText.replaceAll(System.lineSeparator(),"").replaceAll("\\s+","");
				DCCDocumentLog.mLogger.info("Getting Encrypted Key -- " + cipherText);
				PublicKey publicKey = getPublickey();
				DCCDocumentLog.mLogger.info("Public Key Received--->"+publicKey);
				Cipher cipher;
				String decryptVal = "";
				try {
					cipher = Cipher.getInstance("RSA");
					cipher.init(Cipher.DECRYPT_MODE, publicKey);
					decryptVal = new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
					System.out.println("Decrypted value --->"+decryptVal);
				} catch (Exception e) {
					System.out.println("Decrypted value Error--->"+e.toString());
				}
				return decryptVal;

			}

			public  PublicKey getPublickey() throws IOException  {
			   String staticPublicKey = new String("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA3jg7GBKduSFjNCix24Bl2ViRYPGbiQUTmebfaJYhYN9OJ1zlb3PgUngDZtrTzNNhPnPuHDGwqt8gdTCAc56dHwl3FRUJVBfrHpuRbcpJ6UZ0GumKzJs/91vdyIIILhSf/GKeqQB88Nu78u1Tj0OtrFY6K7WRmpxiDfP02r+QAQYrcs/US/ONgpib5bXq+uqStmZLJI40Yh/gBIle+UjiSvitnISwPitHONgf4MnI4BK1BWY9pBcSLTFmBK5m04pkvGkTz4MsWjsNqCbv6ojfgSS4qxkc+F9HsUJzsN/fXM9aKPNz19BCjVMGBMtVNuJ+V2tR0kiNDP7qSWX4JNMHMh8U3/L8L8dnrvY8BWVptoKjGOTJo/c7Gmm6vpa6X7pntAMnIifyFypBQZsCQl3rQUFc4Ue2u9+NhOYaXK5E6ghhxF6pbibVyDJTadrAtIG0NZfiUH66u6i7mBPc91UcoOogS6otoGmzE2JAbh1S1tQeLrSJSJfAM6mS5gtpYSkHjgl0RDpmq+AwPMYFW3LcDQ2XxeU9BCBoVv/n5sqmr4J07UzMmBJQz/E8sRkv5dTXvVSARzgu3T1vJCnhYBVWbyio3Q02VFE84vHauN3QU9Ap1xUNxEBxw2AVNFfy5BhGn2Ed0yQzlWDJGoq+I84O5vdZKCPotBjEg2TKHkYYvzkCAwEAAQ==");
			   staticPublicKey=staticPublicKey.replaceAll(System.lineSeparator(),"");
			   System.out.println("Public Key Path---  " +Paths.get(System.getProperty("user.dir")+ File.separator + "ConfigFiles"+ File.separator+ "public.pem"));
			   System.out.println("Public Key ---  "+(staticPublicKey));
				byte[] decoded;
				PublicKey publicKey = null;
				try {
					decoded = Base64.getDecoder().decode(staticPublicKey);
					X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
					KeyFactory kf = KeyFactory.getInstance("RSA");
					publicKey = kf.generatePublic(spec);
				} catch (NoSuchAlgorithmException ae) {
					ae.printStackTrace();
				} catch (InvalidKeySpecException ke) {
					 ke.printStackTrace();
				}
				return publicKey;
			}
			
			public static void main(String[] args) throws NoSuchProviderException, IOException{
				DCCDocument c = new DCCDocument();
				System.out.println("dd");
				c.decrypt("rdGUIUv9ClyhhBNHSgoksSP6HDGC6d9tbm6uTb8beF3orUphA43Upvv7Pdz3AONMqDOd0JF7Rnw9WOSRLGxnl0hA9hD9ai/z0hi8v9o62gkeczCO6VNnCiWoA7dDsBXkgqskpTtcHqLyTxxCrB3DFRCrgcU8RusEtSweXykkvZA+rtQScEvxtN6i7GVGpg1cdRhCLSJelh0mFt3HZWtEMudavgRN1mcLNbjRntirzERa8rpPJdMgzY6QweNHZ/MvnUJJQ/1o27UurCQMZP3vrpupjYWRzON51T7GttQboy/ixzkN5EWTJbasLkcRnZJTy/+BbtYmE1KUg9fgAJEjVw==");
				
			}
		
}