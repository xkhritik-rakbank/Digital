package com.newgen.iforms.user;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.iforms.xmlapi.IFormXmlResponse;
//import com.newgen.iforms.user.*;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;

import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Phrase;

import ISPack.CImageServer;
import ISPack.CPISDocumentTxn;
import ISPack.ISUtil.JPDBRecoverDocData;
import ISPack.ISUtil.JPISException;
import ISPack.ISUtil.JPISIsIndex;
import Jdts.DataObject.JPDBString;

	public class DigitalAO_Common {
		String sLocaleForMessage = java.util.Locale.getDefault().toString();
	
		public List<List<String>> getDataFromDB(IFormReference iform, String query) {
			DigitalAO.mLogger.debug("Inside Done()--->query is: " + query);
			try {
				List<List<String>> result = iform.getDataFromDB(query);
				DigitalAO.mLogger.debug("Inside Done()---result:" + result);
				if (!result.isEmpty() && result.get(0) != null) {
					return result;
				}
			} catch (Exception e) {
				DigitalAO.printException(e);
			}
			return null;
	
		}
	
		public String AttachDocumentWithWI(IFormReference iform, String pid, String pdfName) {
	
			String docxml = "";
			String documentindex = "";
			String doctype = "";
	
			try {
				DigitalAO.mLogger.debug("inside ODAddDocument");
				DigitalAO.mLogger.debug("Proess Instance Id: " + pid);
				DigitalAO.mLogger.debug("Integration call: " + pdfName);
	
				String sCabname = getCabinetName(iform);
				DigitalAO.mLogger.debug("sCabname" + sCabname);
				String sSessionId = getSessionId(iform);
				DigitalAO.mLogger.debug("sSessionId" + sSessionId);
				String sJtsIp = iform.getServerIp();
				int iJtsPort_int = Integer.parseInt(iform.getServerPort());
	
				String path = System.getProperty("user.dir");// for path
				DigitalAO.mLogger.debug(" \nAbsolute Path :" + path);
				String pdfTemplatePath = "";
				String generatedPdfPath = "";
	
				// Reading path from property file
				Properties properties = new Properties();
				properties.load(new FileInputStream(System.getProperty("user.dir") + System.getProperty("file.separator")
						+ "CustomConfig" + System.getProperty("file.separator") + "RakBankConfig.properties"));
				DigitalAO.mLogger.debug("Template Path: " + pdfName);
	
				String dynamicPdfName = pid + pdfName + ".pdf";
				DigitalAO.mLogger.debug("\nGeneratedPdfPathCheck :" + generatedPdfPath);
				pdfTemplatePath = path + pdfTemplatePath;// Getting complete path of
															// the pdf tempplate
				generatedPdfPath = properties.getProperty("DAO_Form_Template_Path");
	
				DigitalAO.mLogger.debug("\nGeneratedPdfPathCheck :" + generatedPdfPath);
				generatedPdfPath = generatedPdfPath + System.getProperty("file.separator") + dynamicPdfName;
				DigitalAO.mLogger.debug("\nGeneratedPdfPath1 :" + generatedPdfPath);
				generatedPdfPath = path + generatedPdfPath;// Complete path of
															// generated PDF
				DigitalAO.mLogger.debug("\nGeneratedPdfPath :" + generatedPdfPath);
				DigitalAO.mLogger.debug("\npdfTemplatePath:" + pdfTemplatePath);
	
				docxml = SearchExistingDoc(iform, pid, pdfName, sCabname, sSessionId, sJtsIp, iJtsPort_int, generatedPdfPath);
				DigitalAO.mLogger.debug("Final Document Output: " + docxml);
				documentindex = getTagValue(docxml, "DocumentIndex");
	
				doctype = "new";
	
				DigitalAO.mLogger.debug(docxml + "~" + documentindex + "~" + doctype + "~" + dynamicPdfName);
				String Output = "0000~" + docxml + "~" + documentindex + "~" + doctype + "~" + dynamicPdfName;
				DigitalAO.mLogger.debug(" Output: " + Output);
				return Output;
	
			} catch (Exception e) {
				DigitalAO.mLogger.debug("Exception while adding the document: " + e);
				return "Exception while adding the document: " + e;
			}
	
		}
	
		/***********Sign upload utility by Ravindra *************/
	
		String downloadStatus = "";
		String uploadDocStatus = "";
		private static String strSignatureStatusTable;
		private static String trDate;
		private static String fileDownloadLoc;
		private static String ItemIndex;
		private static String EngineName;
		private static String wrapperIP;
		private static String wrapperPort;
		private static String VolumeId;
		private static String SiteId;
		String sessionId = "";
		String WIName = "";
		String AccountNo = "";
		String CifId = "";
		static String CustomerName = "";
		String appServerType = "";
		String wsname = "";
		String CustSeqNo = "";
		String msg = "";
		String Mandates = "";
	
		static Map<String, String> DAOConfigProperties = new HashMap<String, String>();
	
		public String signUploadUtility(IFormReference iform) {
			int sleepIntervalInMin = 0;
			DigitalAO.mLogger.debug("Inside signUpload Utility Method :");
			try {
				int configReadStatus = readConfig();
	
				DigitalAO.mLogger.debug("configReadStatus " + configReadStatus);
				if (configReadStatus != 0) {
					DigitalAO.mLogger.error("Could not Read Config Properties [properties]");
					return "";
				}
				// define properties here
	
				strSignatureStatusTable = DAOConfigProperties.get("strSignatureStatusTable");
				DigitalAO.mLogger.debug("strSignatureStatusTable  :" + strSignatureStatusTable);
	
				trDate = DAOConfigProperties.get("trDate");
				DigitalAO.mLogger.debug("trDate  :" + trDate);
	
				fileDownloadLoc = DAOConfigProperties.get("fileDownloadLoc");
				DigitalAO.mLogger.debug("fileDownloadLoc  :" + fileDownloadLoc);
	
				sessionId = getSessionId(iform);
				DigitalAO.mLogger.debug("sessionID  :" + sessionId);
	
				ItemIndex = getItemIndex(iform);
				DigitalAO.mLogger.debug("ItemIndex  :" + ItemIndex);
	
				EngineName = DAOConfigProperties.get("CabinetName");
				DigitalAO.mLogger.debug("EngineName  :" + EngineName);
	
				WIName = getWorkitemName(iform);
				DigitalAO.mLogger.debug("WIName  :" + WIName);
				
				AccountNo = (String) iform.getValue("account_no");
				DigitalAO.mLogger.debug("AccountNo  :" + WIName);
	
				CifId = (String) iform.getValue("CIF");
				DigitalAO.mLogger.debug("CifId  :" + WIName);
	
				CustomerName = iform.getValue("Given_Name") + " " +  iform.getValue("Surname");
				DigitalAO.mLogger.debug("CustomerName  :" + CustomerName);
	
				wrapperIP = DAOConfigProperties.get("JTSIP");
				DigitalAO.mLogger.debug("wrapperIP  :" + wrapperIP);
	
				wrapperPort = DAOConfigProperties.get("JTSPort");
				DigitalAO.mLogger.debug("wrapperPort  :" + wrapperPort);
	
				VolumeId = DAOConfigProperties.get("VolumeId");
				DigitalAO.mLogger.debug("VolumeId  :" + VolumeId);
	
				SiteId = DAOConfigProperties.get("SiteId");
				DigitalAO.mLogger.debug("SiteId  :" + SiteId);
	
				appServerType = "WebSphere";// for testing
				DigitalAO.mLogger.debug("appServerType  :" + appServerType);
	
				wsname = iform.getActivityName();
				DigitalAO.mLogger.debug("appServerType  :" + appServerType);
	
				CustSeqNo = "123";
				DigitalAO.mLogger.debug("CustSeqNo  :" + CustSeqNo);
	
				Mandates = "82506";
				DigitalAO.mLogger.debug("Mandates :" + Mandates);
	
				if (sessionId.trim().equalsIgnoreCase("")) {
					DigitalAO.mLogger.debug("Could Not Connect to Server!");
				} else {
					DigitalAO.mLogger.debug("Session ID found: " + sessionId);
					msg = processWI(ItemIndex, sessionId, EngineName, WIName, AccountNo, CifId, Mandates,
							CustSeqNo, wrapperIP, wrapperPort, VolumeId, SiteId, appServerType, wsname, iform);
					System.out.println("No More workitems to Process, Sleeping!");
					Thread.sleep(sleepIntervalInMin * 60 * 1000);
				}
	
			} catch (Exception e) {
				e.printStackTrace();
				DigitalAO.mLogger.error("Exception Occurred in FALCON Document Document Thread: " + e);
				final Writer result = new StringWriter();
				final PrintWriter printWriter = new PrintWriter(result);
				e.printStackTrace(printWriter);
				DigitalAO.mLogger.error("Exception Occurred in FALCON Document Thread : " + result);
			}
	
			return msg;
		}
	
		// method start here for sign upload process WI
	
		public String processWI(String ItemIndex, String SessionId, String CabinetName, String WIName, String AccountNo,
				String CifId, String Mandates, String CustSeqNo, String JtsIP, String JtsPort,
				String VolumeId, String SiteId, String appServerType, String wsname,IFormReference iform) {
	
			try {
				DigitalAO.mLogger.debug("After SigUpload Status " + Mandates);
				String sQuery = "select top 1 imageindex from pdbdocument with (nolock) WHERE DocumentIndex in (select DocumentIndex from "
				+ "PDBDocumentContent where ParentFolderIndex =(select FolderIndex from PDBFolder where Name = '"+ WIName +"')) "
				+ "and name like 'Signature%' order by DocumentIndex desc";
				
				List<List<String>> outputXML =  iform.getDataFromDB(sQuery);
				
				DigitalAO.mLogger.debug("inputXML : " + sQuery);
				DigitalAO.mLogger.debug("outputXML: " + outputXML);
				String Iindex = outputXML.get(0).get(0);
				DigitalAO.mLogger.debug("OKKKK: " + Iindex);
				downloadStatus = DownloadDocument(Iindex, WIName, "Signature", strSignatureStatusTable, AccountNo, CustSeqNo, CabinetName, SessionId, JtsIP, JtsPort, VolumeId,String.valueOf(SiteId));
				DigitalAO.mLogger.debug("download status..: " + downloadStatus);
	
				if (downloadStatus != "F") {
					DigitalAO.mLogger.debug("downloadStatus is !=F ");
					uploadDocStatus = getSignatureUploadXML(downloadStatus, AccountNo, CifId, trDate,CustomerName, Mandates);
					DigitalAO.mLogger.debug("uploadDocStatus is... " + uploadDocStatus);
				}
			} catch (Exception e) {
				DigitalAO.mLogger.debug("Exception occured: " + e.getMessage());
			}
			return uploadDocStatus;
		}
	
		// method for download Document
		public String DownloadDocument(String xmlParser, String winame, String docName, String strSignatureStatusTable,String account_no,
				String customer_seq_no, String cabinetName, String sessionId,
				String JtsIP, String JtsPort, String VolumeId, String SiteId) {
	
			DigitalAO.mLogger.debug("inside DownloadDocument");
			String status = "F";
			String msg = "Error";
			String JtsP = JtsPort.toString();
			StringBuffer strFilePath = new StringBuffer();
			try {
				String base64String = null;
				String imageIndex = xmlParser;
				strFilePath.append(System.getProperty("user.dir"));
				strFilePath.append(File.separator);
				strFilePath.append(fileDownloadLoc);
				strFilePath.append(File.separatorChar);
				strFilePath.append(winame);
				strFilePath.append("_");
				strFilePath.append(docName);
				strFilePath.append(".");
				strFilePath.append("JPG");
	
				CImageServer cImageServer = null;
				DigitalAO.mLogger.debug("Line 377");
				try {
					cImageServer = new CImageServer(null, JtsIP, Short.parseShort(JtsP));
				} catch (JPISException e) {
					DigitalAO.mLogger.debug("Error Downloading signature:" + e.getMessage());
					msg = e.getMessage();
					status = "F";
				}
				DigitalAO.mLogger.debug("cImageServer : ------ : " +JtsIP + " : " + Short.parseShort(JtsP)+ " : " +  cabinetName+ " : " + SiteId+ " : " + Short.parseShort(VolumeId)+ " : " + Integer.parseInt(imageIndex)+ " : " + strFilePath.toString()+ " : " + new JPDBString());
				int odDownloadCode=cImageServer.JPISGetDocInFile_MT(null,JtsIP, Short.parseShort(JtsP), cabinetName,Short.parseShort(SiteId), Short.parseShort(VolumeId), Integer.parseInt(imageIndex),"",strFilePath.toString(), new JPDBString());
				DigitalAO.mLogger.debug("ODsDownloadCode: " + String.valueOf(odDownloadCode));
				if (odDownloadCode == 1) {
					try {
						base64String = convertToBase64(strFilePath.toString());
						status = base64String;
						File fForDeletion = new File(strFilePath.toString());
						fForDeletion.delete();
					} catch (Exception e) {
						msg = e.getMessage();
						status = "F";
					}
				} else {
					msg = "Error occured while downloading the document :" + docName;
					status = "F";
				}
			} catch (Exception e) {
				DigitalAO.mLogger.debug(e.getMessage(), e);
				DigitalAO.mLogger.debug("Error Downloading file:" + e.getMessage());
				e.printStackTrace();
				msg = e.getMessage();
				status = "F";
			}
			DigitalAO.mLogger.debug("msg" + msg);
			return status;
		}
	
		// method for convert into base 64
		public static String convertToBase64(String filePath) {
			String retValue = "";
			try {
				DigitalAO.mLogger.debug("inside convertToBase64 method");
				File file = new File(filePath);
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				long size = 0;
				try {
					for (int readNum; (readNum = fis.read(buf)) != -1;) {
						// Writes to this byte array output stream
						bos.write(buf, 0, readNum);
						// out.println("read " + readNum + " bytes,");
						size = size + readNum;
					}
	
					byte[] encodedBytes = Base64.encodeBase64(bos.toByteArray());
					String sEncodedBytes = new String(encodedBytes);
	
					retValue = sEncodedBytes;
					// WriteLog("Base64 string..:" +retValue);
				} catch (IOException ex) {
					DigitalAO.mLogger.debug("Error converting to Base64:" + ex.getMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return retValue;
		}
	
		public String getSignatureUploadXML(String base64String,String ACCNO,String CIFID,String DATE, String CustomerName, String Sig_Remarks)
		{
			java.util.Date d1 = new Date();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
			String DateExtra2 = sdf1.format(d1)+"+04:00";	
			Sig_Remarks="To Sign Singly";
			String integrationXML = "<EE_EAI_MESSAGE>" +
				   "<EE_EAI_HEADER>" +
					  "<MsgFormat>SIGNATURE_ADDITION_REQ</MsgFormat>" +
					  "<MsgVersion>0001</MsgVersion>" +
					  "<RequestorChannelId>BPM</RequestorChannelId>" +
					  "<RequestorUserId>RAKUSER</RequestorUserId>" +
					  "<RequestorLanguage>E</RequestorLanguage>" +
					  "<RequestorSecurityInfo>secure</RequestorSecurityInfo>" +
					  "<ReturnCode>911</ReturnCode>" +
					  "<ReturnDesc>Issuer Timed Out</ReturnDesc>" +
					  "<MessageId>UniqueMessageId123</MessageId>" +
					  "<Extra1>REQ||SHELL.JOHN</Extra1>" +
					  "<Extra2>"+DateExtra2+"</Extra2>" +
				   "</EE_EAI_HEADER>" +
				   "<SignatureAddReq>" +
					  "<BankId>RAK</BankId>" +
					  "<AcctId>"+ACCNO+"</AcctId>" +
					  "<AccType>N</AccType>" +
					  "<CustId>"+CIFID+"</CustId>" +
					  "<BankCode></BankCode>" +
					  "<EmpId></EmpId>" +
					  "<CustomerName>"+CustomerName+"</CustomerName>" +
					  "<SignPowerNumber></SignPowerNumber>" +
					  "<ImageAccessCode>1</ImageAccessCode>" +
					  "<SignExpDate>2112-03-06T23:59:59.000</SignExpDate>" +
					  "<SignEffDate>2010-12-31T23:59:59.000</SignEffDate>" +
					  "<SignFile>"+base64String+"</SignFile>" +
					  "<PictureExpDate>2099-12-31T23:59:59.000</PictureExpDate>" +
					  "<PictureEffDate>2010-12-31T23:59:59.000</PictureEffDate>" +
					  "<PictureFile></PictureFile>" +
					  "<SignGroupId>SVSB11</SignGroupId>" +
					  "<Remarks>"+Sig_Remarks+"</Remarks>" +
				   "</SignatureAddReq>" +
				"</EE_EAI_MESSAGE>";
	
			return integrationXML;
		}
		// **************************sign upload method end here *******************
	
		protected static String WFNGExecute(String ipXML, String jtsServerIP, String serverPort, int flag) throws IOException {
			try {
				DigitalAO.mLogger.info("WFNGExecute() : " + ipXML + " - " + jtsServerIP + " - " + serverPort + " - " + flag);
				if (serverPort.startsWith("33")) {
					DigitalAO.mLogger.info("Inside if WFNGExecute() :");
					return WFCallBroker.execute(ipXML, jtsServerIP, Integer.parseInt(serverPort), 1);
				} else {
					DigitalAO.mLogger.info("Inside else WFNGExecute() :");
					return NGEjbClient.getSharedInstance().makeCall(jtsServerIP, serverPort, "WebSphere", ipXML);
				}
				//
			} catch (Exception e) {
				DigitalAO.mLogger.info("Exception Occured in WF NG Execute : " + e.getMessage());
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
				DigitalAO.mLogger.info(e.toString());
				Thread.currentThread().interrupt();
			}
		}
	
		public static String GetTagValue(String XML, String Tagname) {
			String starttag = "<" + Tagname + ">";
			String endtag = "</" + Tagname + ">";
			DigitalAO.mLogger.info("GetTagValue " + starttag);
			if (XML.indexOf(starttag) >= 0) {
				if ("MATURITYDATE".equals(Tagname)) {
					String date = XML.substring(XML.indexOf(starttag) + (starttag.length()), XML.indexOf(endtag));
					return date.substring(6, 8) + date.substring(4, 6) + date.substring(0, 4);
				}
				return XML.substring(XML.indexOf(starttag) + (starttag.length()), XML.indexOf(endtag));
			} else {
				return "";
			}
		}
	
		// end here
	
		public static String maskXmlTags(List<List<String>> outputMQXML, String Tag) {
			Pattern p = Pattern.compile("(?<=" + Tag + ")([-\\s\\w]*)((?:[a-zA-Z0-9][-_\\s]*){0})");
			Matcher m = p.matcher((CharSequence) outputMQXML);
			StringBuffer maskedResult = new StringBuffer();
			while (m.find()) {
				String thisMask = m.group(1).replaceAll("[^-_\\s]", "*");
				m.appendReplacement(maskedResult, thisMask + "$2");
			}
			m.appendTail(maskedResult);
			return maskedResult.toString();
		}
	
		public String saveDataInDB(IFormReference iform, String query) {
			DigitalAO.mLogger.debug("Inside Done()---Exception_Mail_ID->query is: " + query);
			try {
				int mainCode = iform.saveDataInDB(query);
				DigitalAO.mLogger.debug("Inside Done()---result:" + mainCode);
				return mainCode + "";
			} catch (Exception e) {
				DigitalAO.printException(e);
			}
			return null;
		}
	
		// **********************************************************************************//
		// Description :Method to Trim Strings
		// **********************************************************************************//
		public String Trim(String str) {
			if (str == null)
				return str;
			int i = 0, j = 0;
			for (i = 0; i < str.length(); i++) {
				if (str.charAt(i) != ' ')
					break;
			}
			for (j = str.length() - 1; j >= 0; j--) {
				if (str.charAt(j) != ' ')
					break;
			}
			if (j < i)
				j = i - 1;
			str = str.substring(i, j + 1);
			return str;
		}
	
		public void enableControl(String strFields, IFormReference iform) {
			String arrFields[] = strFields.split(",");
			for (int idx = 0; idx < arrFields.length; idx++) {
				try {
					iform.getIFormControl(arrFields[idx]).getM_objControlStyle().setM_strEnable("true");
				} catch (Exception ex) {
					DigitalAO.printException(ex);
				}
			}
		}
		
		public void disableControl(String strFields, IFormReference iform) {
			String arrFields[] = strFields.split(",");
			for (int idx = 0; idx < arrFields.length; idx++) {
				try {
					iform.getIFormControl(arrFields[idx]).getM_objControlStyle().setM_strEnable("false");
				} catch (Exception ex) {
					DigitalAO.printException(ex);
				}
			}
		}
	
		public void lockControl(String strFields, IFormReference iform) {
			String arrFields[] = strFields.split(",");
			for (int idx = 0; idx < arrFields.length; idx++) {
				try {
					iform.getIFormControl(arrFields[idx]).getM_objControlStyle().setM_strReadOnly("true");
				} catch (Exception ex) {
					DigitalAO.printException(ex);
				}
			}
		}
	
		public void unlockControl(String strFields, IFormReference iform) {
			String arrFields[] = strFields.split(",");
			for (int idx = 0; idx < arrFields.length; idx++) {
				try {
					iform.getIFormControl(arrFields[idx]).getM_objControlStyle().setM_strReadOnly("false");
				} catch (Exception ex) {
					DigitalAO.printException(ex);
				}
			}
		}
	
		public String getSessionId(IFormReference iform) {
			return ((iform).getObjGeneralData()).getM_strDMSSessionId();
		}
	
		public String getItemIndex(IFormReference iform) {
			return ((iform).getObjGeneralData()).getM_strFolderId();
		}
	
		public String getWorkitemName(IFormReference iform) {
			return ((iform).getObjGeneralData()).getM_strProcessInstanceId();
		}
	
		public void setControlValue(String controlName, String controlValue, IFormReference iform) {
			iform.setValue(controlName, controlValue);
		}
	
		public String getCabinetName(IFormReference iform) {
			return (String) iform.getCabinetName();
		}
	
		public String getUserName(IFormReference iform) {
			return (String) iform.getUserName();
		}
	
		public String getActivityName(IFormReference iform) {
			return (String) iform.getActivityName();
		}
	
		public String getControlValue(String controlName, IFormReference iform) {
			// return (String)EventHandler.iFormOBJECT.getControlValue(controlName);
			return (String) iform.getValue(controlName);
		}
	
	
		// ******************************************************
		// Description :Method to get current date
		// ******************************************************
		public String getCurrentDate(String outputFormat) {
			String current_date = "";
			try {
				java.util.Calendar dateCreated1 = java.util.Calendar.getInstance();
				java.text.DateFormat df2 = new java.text.SimpleDateFormat(outputFormat);
				current_date = df2.format(dateCreated1.getTime());
			} catch (Exception e) {
				System.out.println("Exception in getting Current date :" + e);
			}
			return current_date;
		}
	
		public String ExecuteQueryOnServer(String sInputXML, IFormReference iform) {
			try {
				DigitalAO.mLogger.debug("Server Ip :" + iform.getServerIp());
				DigitalAO.mLogger.debug("Server Port :" + iform.getServerPort());
				DigitalAO.mLogger.debug("Input XML :" + sInputXML);
	
				return NGEjbClient.getSharedInstance().makeCall(iform.getServerIp(), iform.getServerPort() + "",
						"WebSphere", sInputXML);
			} catch (Exception excp) {
				DigitalAO.mLogger.debug("Exception occured in executing API on server :\n" + excp);
				DigitalAO.printException(excp);
				return "Exception occured in executing API on server :\n" + excp;
			}
		}
	
		public String getTagValue(String xml, String tag) {
			try {
				Document doc = getDocument(xml);
				NodeList nodeList = doc.getElementsByTagName(tag);
				int length = nodeList.getLength();
				if (length > 0) {
					Node node = nodeList.item(0);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						NodeList childNodes = node.getChildNodes();
						String value = "";
						int count = childNodes.getLength();
						for (int i = 0; i < count; i++) {
							Node item = childNodes.item(i);
							if (item.getNodeType() == Node.TEXT_NODE) {
								value += item.getNodeValue();
							}
						}
						return value;
					} else if (node.getNodeType() == Node.TEXT_NODE) {
						return node.getNodeValue();
					}
				}
			} catch (Exception e) {
				DigitalAO.printException(e);
			}
			return "";
		}
	
		public String getTagValue(Node node, String tag) {
			NodeList nodeList = node.getChildNodes();
			int length = nodeList.getLength();
	
			for (int i = 0; i < length; ++i) {
				Node child = nodeList.item(i);
	
				if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equalsIgnoreCase(tag)) {
					return child.getTextContent();
				}
			}
			return "";
		}
	
		public Document getDocument(String xml) throws ParserConfigurationException, SAXException, IOException {
			// Step 1: create a DocumentBuilderFactory
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// Step 2: create a DocumentBuilder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// Step 3: parse the input file to get a Document object
			Document doc = db.parse(new InputSource(new StringReader(xml)));
			return doc;
		}
	
		public NodeList getNodeListFromDocument(Document doc, String identifier) {
			NodeList records = doc.getElementsByTagName(identifier);
			return records;
		}
	
		public String generateResponseString(String SaveFormData, String SuccessOrError, String preAlertMessage,
				String alertMessageCode, String postAlertMessage, String call, String data) {
			return "{'SAVEFORMDATA':'" + SaveFormData + "'," + "'SUCCESSORERROR':'" + SuccessOrError + "',"
					+ "'PREALERTMESSAGE':'" + preAlertMessage + "'," + "'ALERTMESSAGECODE':'" + alertMessageCode + "',"
					+ "'POSTALERTMESSAGE':'" + postAlertMessage + "'," + "'CALL':'" + call + "'," + "'DATA':'" + data
					+ "'}";
		}
		
		// ECD - 23 digit number to be generated based on given logic after account openning
		public String get23DigitAccountNumberRIB(String ibnStr) {
			System.out.println("Inside get23DigitAccountNumber method ...");
			StringBuilder input = new StringBuilder();
			input.append(ibnStr);
			input = input.reverse();
			int val = 0;
			String outputAccNumber = "";
			for (int i = 0; i < input.length(); i++) {
				if (i == 0)
					val += (3) * Character.getNumericValue(input.charAt(i));
				else if (i == 1)
					val += (30) * Character.getNumericValue(input.charAt(i));
				else if (i == 2)
					val += (9) * Character.getNumericValue(input.charAt(i));
				else if (i == 3)
					val += (90) * Character.getNumericValue(input.charAt(i));
				else if (i == 4)
					val += (27) * Character.getNumericValue(input.charAt(i));
				else if (i == 5)
					val += (76) * Character.getNumericValue(input.charAt(i));
				else if (i == 6)
					val += (81) * Character.getNumericValue(input.charAt(i));
				else if (i == 7)
					val += (34) * Character.getNumericValue(input.charAt(i));
				else if (i == 8)
					val += (49) * Character.getNumericValue(input.charAt(i));
				else if (i == 9)
					val += (5) * Character.getNumericValue(input.charAt(i));
				else if (i == 10)
					val += (50) * Character.getNumericValue(input.charAt(i));
				else if (i == 11)
					val += (15) * Character.getNumericValue(input.charAt(i));
				else if (i == 12)
					val += (53) * Character.getNumericValue(input.charAt(i));
				else if (i == 13)
					val += (45) * Character.getNumericValue(input.charAt(i));
				else if (i == 14)
					val += (62) * Character.getNumericValue(input.charAt(i));
				else if (i == 15)
					val += (38) * Character.getNumericValue(input.charAt(i));
				else if (i == 16)
					val += (89) * Character.getNumericValue(input.charAt(i));
				else if (i == 17)
					val += (17) * Character.getNumericValue(input.charAt(i));
				else if (i == 18)
					val += (73) * Character.getNumericValue(input.charAt(i));
			}
			val = val % 97;
			val = 97 - val;
			if (val < 0)
				val = val * (-1);
			if (val < 10)
				outputAccNumber = "0" + val;
			else
				outputAccNumber = val + "";
	
			System.out.println("outputAccNumber:" + outputAccNumber);
			return outputAccNumber;
		}
	
		public String getRibKey(String countryCode, String aff_bankCode, String accNum, String IbanBrCode) {
			System.out.println("Inside get Rib Key");
			System.out.println("countryCode :" + countryCode);
			System.out.println("aff_bankCode :" + aff_bankCode);
			System.out.println("accNum :" + accNum);
			System.out.println("IbanBrCode :" + IbanBrCode);
			
			String extraZero = "00";
			if (countryCode.equalsIgnoreCase("TG")) {
				int numBankCode = 37055;
				return calucalteRibETG(numBankCode, accNum, IbanBrCode);
			} else if (countryCode.equalsIgnoreCase("GA")) {
				// String substr_acc1=accNum.substring(3,6);
				// String substr_acc2=accNum.substring(8,16);
				String accNumPart = accNum.substring(3, 6) + accNum.substring(8, 16);
				return calucalteRIBModuloLogic(accNum, IbanBrCode, extraZero, accNumPart, countryCode);
			} else if (countryCode.equalsIgnoreCase("GQ")) {
				extraZero = "";
				String accNumPart = accNum.substring(3, 6) + accNum.substring(8, 16);
				return calucalteRIBModuloLogic(accNum, IbanBrCode, extraZero, accNumPart, countryCode);
			} else if (countryCode.equalsIgnoreCase("BF")) {
				String accNumPart = accNum.substring(4, 16);
				return calucalteRIBModuloLogic(accNum, IbanBrCode, extraZero, accNumPart, countryCode);
			} else if (countryCode.equalsIgnoreCase("BJ")) {
				//int numBankCode = 21062;
				String accNumPart = accNum.substring(4, 16);
				return calucalteRIBModuloLogic(accNum, IbanBrCode, extraZero, accNumPart, countryCode);
			} else if (countryCode.equalsIgnoreCase("CM") || countryCode.equalsIgnoreCase("CM")) {
				String accNumPart = accNum.substring(3, 6) + accNum.substring(8, 16);
				return calucalteRIBModuloLogic(accNum, IbanBrCode, extraZero, accNumPart, countryCode);
			} else if (countryCode.equalsIgnoreCase("CG")) {
				// Change done for ECG specific for IBAN NO
				System.out.println("inside ECG for iban ");
				String rib = aff_bankCode + extraZero + accNum.substring(0, 3) + accNum.substring(3, 6)
						+ accNum.substring(8, 16) + "00";
				System.out.println("rib affiliate specific" + rib);
				return clerib1(rib);
			}
			// ECV RIB Change
			else if (countryCode.equalsIgnoreCase("CV")) {
				return calucalteRibECV(countryCode, accNum, aff_bankCode);
			} else {
				String ibanAccString = "";
				ibanAccString = accNum.substring(4, 16);
				return ribKeyGenerate(ibanAccString, aff_bankCode, IbanBrCode);
			}
		}
		// ECV RIB Change
		private String calucalteRibECV(String countryCode, String accNum, String aff_bankCode) {
			System.out.println("Inside calucalteRibECV method ...");
			String ibanStr = makeIBANString(countryCode, aff_bankCode, accNum, "");
			System.out.println("ibanStr :" + ibanStr);
			StringBuilder input = new StringBuilder();
			input.append(ibanStr);
			input = input.reverse();
			int val = 0;
			String rib = "";
			for (int i = 0; i < input.length(); i++) {
				if (i == 0)
					val += (3) * Character.getNumericValue(input.charAt(i));
				else if (i == 1)
					val += (30) * Character.getNumericValue(input.charAt(i));
				else if (i == 2)
					val += (9) * Character.getNumericValue(input.charAt(i));
				else if (i == 3)
					val += (90) * Character.getNumericValue(input.charAt(i));
				else if (i == 4)
					val += (27) * Character.getNumericValue(input.charAt(i));
				else if (i == 5)
					val += (76) * Character.getNumericValue(input.charAt(i));
				else if (i == 6)
					val += (81) * Character.getNumericValue(input.charAt(i));
				else if (i == 7)
					val += (34) * Character.getNumericValue(input.charAt(i));
				else if (i == 8)
					val += (49) * Character.getNumericValue(input.charAt(i));
				else if (i == 9)
					val += (5) * Character.getNumericValue(input.charAt(i));
				else if (i == 10)
					val += (50) * Character.getNumericValue(input.charAt(i));
				else if (i == 11)
					val += (15) * Character.getNumericValue(input.charAt(i));
				else if (i == 12)
					val += (53) * Character.getNumericValue(input.charAt(i));
				else if (i == 13)
					val += (45) * Character.getNumericValue(input.charAt(i));
				else if (i == 14)
					val += (62) * Character.getNumericValue(input.charAt(i));
				else if (i == 15)
					val += (38) * Character.getNumericValue(input.charAt(i));
				else if (i == 16)
					val += (89) * Character.getNumericValue(input.charAt(i));
				else if (i == 17)
					val += (17) * Character.getNumericValue(input.charAt(i));
				else if (i == 18)
					val += (73) * Character.getNumericValue(input.charAt(i));
			}
			val = val % 97;
			val = 98 - val;
			if (val < 0)
				val = val * (-1);
			if (val < 10)
				rib = "0" + val;
			else
				rib = val + "";
			System.out.println(rib);
			return rib;
		}
	
		public String makeIBANString(String countryCode, String aff_bankCode, String accNum, String ibanBrCode) {
			if (countryCode.equalsIgnoreCase("GA")) {
				return (aff_bankCode + "00" + accNum.substring(0, 3) + accNum.substring(4, 6) + "0"
						+ accNum.substring(10, 16));
			}
			if (countryCode.equalsIgnoreCase("GQ")) {
				return (aff_bankCode + "00" + accNum.substring(0, 3) + accNum.substring(3, 6) + accNum.substring(8, 16));
			} else if (countryCode.equalsIgnoreCase("CM") || countryCode.equalsIgnoreCase("CM")) {
				return (aff_bankCode + ibanBrCode + accNum.substring(3, 6) + accNum.substring(8, 16));
			} else if (countryCode.equalsIgnoreCase("BF")) {
				return (aff_bankCode + "00" + accNum.substring(0, 3) + accNum.substring(4, 16));
			}
			// ECV RIB Change
			else if (countryCode.equalsIgnoreCase("CV")) {
				return (aff_bankCode + "0" + accNum.substring(0, 3) + accNum.substring(3, 6) + accNum.substring(8, 16));
			} else if (countryCode.equalsIgnoreCase("CG")) {
				// Change done for ECG specific for IBAN NO
				System.out.println("inside ECG for makeIBANString ");
				System.out.println("inside ECG for makeIBANString aff_bankCode " + aff_bankCode);
				System.out.println("inside ECG for makeIBANString accNum " + accNum);
				System.out.println("inside ECG for makeIBANString accNum1 " + accNum.substring(0, 3));
				System.out.println("inside ECG for makeIBANString accNum2 " + accNum.substring(3, 6));
				System.out.println("inside ECG for makeIBANString accNum3 " + accNum.substring(8, 16));
				return (aff_bankCode + "00" + accNum.substring(0, 3) + accNum.substring(3, 6) + accNum.substring(8, 16));
			} else {
				return (aff_bankCode + ibanBrCode + accNum.substring(4, 16));
			}
		}
	
		// **********************************************************************************//
		// Description :Method to Generate RIB Key
		// **********************************************************************************//
		public String ribKeyGenerate(String ibanAccString, String aff_bankCode, String ibanBrCode) {
			String rib = aff_bankCode;
	
			rib += ibanBrCode + ibanAccString + "00";
	
			String ribKey = clerib(rib);
			return ribKey;
		}
	
		// **********************************************************************************//
		// Description :Method to Generate RIB
		// **********************************************************************************//
		public String clerib(String rib) {
			int i;
			Long Reste;
			String s = "", CleRib = "";
			Reste = Long.parseLong("0");
			Reste = ((Reste * 10) + estlettre(rib.charAt(0))) % 97;
			Reste = ((Reste * 10) + estlettre(rib.charAt(1))) % 97;
	
			// Long Rest1=estlettre(rib.charAt(1));
			for (i = 2; i < rib.length(); i++) {
				if (i == 1) {
					// String rKey = estlettre(rib.substring(i,i+1)).toString();
					Reste = ((Reste * 10) + estlettre(rib.charAt(1))) % 97;
				} else
					Reste = ((Reste * 10) + Long.parseLong(rib.substring(i, i + 1))) % 97;
			}
			s = Long.toString(97 - Reste);
			if (s.length() == 1)
				CleRib = "0" + s;
			else
				CleRib = s;
			return CleRib;
		}
	
		// **********************************************************************************//
		// Description :Method to Generate RIB for ECG
		// **********************************************************************************//
		public String clerib1(String rib) {
			int i;
			Long Reste;
			String s = "", CleRib = "";
			Reste = Long.parseLong("0");
			for (i = 0; i < rib.length(); i++) {
	
				Reste = ((Reste * 10) + Long.parseLong(rib.substring(i, i + 1))) % 97;
			}
			s = Long.toString(97 - Reste);
			if (s.length() == 1)
				CleRib = "0" + s;
			else
				CleRib = s;
	
			System.out.println("CleRib1 aff_speci " + CleRib);
			return CleRib;
		}
	
		// **********************************************************************************//
		// Description :Method to Generate RIB
		// **********************************************************************************//
		public int estlettre(char e) {
			int letter;
	
			switch (e) {
			case 'K':
				letter = 2;
				;
				break;
			case 'B':
				letter = 5;
				break;
			case 'C':
				letter = 3;
				break;
			case 'A':
				letter = 1;
				break;
			case 'S':
				letter = 2;
				break;
			case 'D':
				letter = 4;
				break;
			case 'H':
				letter = 8;
				break;
			case 'T':
				letter = 7;
				break;
			case 'N':
				letter = 5;
				break;
			default:
				letter = 2;
				break;
			}
			return letter;
		}
	
		private String calucalteRibETG(int numBankCode, String accNum, String IbanBrCode) {
			//String rib = "";
			String sBranchCode = accNum.substring(0, 3);
			int iBranchCode = Integer.parseInt(sBranchCode);
	
			int substr_acc1 = Integer.parseInt(accNum.substring(4, 10));
			int substr_acc2 = Integer.parseInt(accNum.substring(10, 16));
	
			int townCode = 1000;
	
			try {
				// townCode = Integer.parseInt(IbanBrCode);
	
				if (sBranchCode.equalsIgnoreCase("713")) {
					townCode = 2000;
				} else if (sBranchCode.equalsIgnoreCase("714")) {
					townCode = 4000;
				} else if (sBranchCode.equalsIgnoreCase("712")) {
					townCode = 5000;
				} else if (sBranchCode.equalsIgnoreCase("707")) {
					townCode = 6000;
				} else if (sBranchCode.equalsIgnoreCase("711")) {
					townCode = 7000;
				} else if (sBranchCode.equalsIgnoreCase("715")) {
					townCode = 10000;
				}
			} catch (Exception e) {
				townCode = 1000;
			}
	
			long intrRIB = (17 * numBankCode + 53 * (townCode + iBranchCode) + 81 * substr_acc1 + 3 * substr_acc2);
	
			// System.out.println("intrRIB "+intrRIB);
	
			long intrRIB2 = 97 - (intrRIB % 97);
	
			String ribKey = intrRIB2 + "";
	
			return ribKey;
		}
	
		private String calucalteRIBModuloLogic(String accNum, String IbanBrCode, String extraZero, String accNumPart,
				String countryCode) {
			String rib = "";
			String sBranchCode = accNum.substring(0, 3);
	
			String account = "00" + sBranchCode;
	
			if (countryCode.equalsIgnoreCase("BJ")) {
				rib += "21062" + IbanBrCode + accNumPart + extraZero;
			}
			if (countryCode.equalsIgnoreCase("CM") || countryCode.equalsIgnoreCase("CM")) {
				rib += "10029" + IbanBrCode + accNumPart + extraZero;
			} else {
				rib += IbanBrCode + account + accNumPart + extraZero;
			}
	
			String ribKey = calculateModulus(rib);
	
			return ribKey;
		}
	
		public String calculateModulus(String val) {
			String value = "";
			int len = 0;
			int midvalue = 0;
			char temp;
			len = val.length();
	
			for (int i = 0; i < len; i++) {
				temp = val.charAt(i);
				midvalue = (midvalue * 10) + (Character.getNumericValue(temp));
				if (midvalue > 97)
					midvalue = midvalue % 97;
			}
	
			midvalue = 97 - midvalue;
	
			if (midvalue < 0)
				midvalue = midvalue * (-1);
	
			value = Integer.toString(midvalue);
	
			if (midvalue < 10) {
				value = "0" + midvalue;
			}
			return value;
		}
		
		
		public String SearchExistingDoc(IFormReference iform, String pid, String FrmType, String sCabname,
				String sSessionId, String sJtsIp, int iJtsPort_int, String sFilepath) {
			try {
				String strFolderIndex = "";
				String strImageIndex = "";
	
				String strInputQry1 = "SELECT FOLDERINDEX,ImageVolumeIndex FROM PDBFOLDER WITH(NOLOCK) WHERE NAME='" + pid
						+ "'";
	
				short iJtsPort = (short) iJtsPort_int;
	
				DigitalAO.mLogger.debug("sInputXML: " + strInputQry1);
	
				List<List<String>> dataFromDB = iform.getDataFromDB(strInputQry1);
				for (List<String> tableFrmDB : dataFromDB) {
					strFolderIndex = tableFrmDB.get(0).trim();
					// strImageIndex = tableFrmDB.get(1).trim();
					strImageIndex = Integer.toString(iform.getObjGeneralData().getM_iVolId());
				}
				DigitalAO.mLogger.debug("strFolderIndex: " + strFolderIndex);
				DigitalAO.mLogger.debug("strImageIndex: " + strImageIndex);
	
				IFormXmlResponse xmlParserData = new IFormXmlResponse();
	
				if (!(strFolderIndex.equalsIgnoreCase("") && strImageIndex.equalsIgnoreCase(""))) {
	
					String strInputQry2 = "SELECT a.documentindex,b.ParentFolderIndex FROM PDBDOCUMENT A WITH (NOLOCK), PDBDOCUMENTCONTENT B WITH (NOLOCK)"
							+ "WHERE A.DOCUMENTINDEX= B.DOCUMENTINDEX AND A.NAME IN ('" + FrmType
							+ "','') AND B.PARENTFOLDERINDEX ='" + strFolderIndex + "'";
					DigitalAO.mLogger.debug("sInputXML: " + strInputQry2);
	
					List<List<String>> dataFromDB2 = iform.getDataFromDB(strInputQry2);
					DigitalAO.mLogger.debug("dataFromDB2: " + dataFromDB2);
	
					ArrayList<String> strdocumentindex = new ArrayList<String>(dataFromDB2.size());
					DigitalAO.mLogger.debug("strdocumentindex: " + strdocumentindex);
					ArrayList<String> strParentFolderIndex = new ArrayList<String>(dataFromDB2.size());
					DigitalAO.mLogger.debug("strParentFolderIndex: " + strParentFolderIndex);
	
					for (List<String> tableFrmDB2 : dataFromDB2) {
						DigitalAO.mLogger.debug("tableFrmDB2: " + tableFrmDB2);
						strdocumentindex.add(tableFrmDB2.get(0).trim());
						strParentFolderIndex.add(tableFrmDB2.get(1).trim());
					}
					DigitalAO.mLogger.debug("strdocumentindex: " + strdocumentindex);
					DigitalAO.mLogger.debug("strParentFolderIndex: " + strParentFolderIndex);
	
					DigitalAO.mLogger.debug("dataFromDB2.size();: " + dataFromDB2.size());
	
					DigitalAO.mLogger.debug("dataFromDB2.isEmpty: " + dataFromDB2.isEmpty());
					try {
						DigitalAO.mLogger.debug("Inside Adding PN File: ");
						DigitalAO.mLogger.debug("sFilepath: " + sFilepath);
						String filepath = sFilepath;
	
						File newfile = new File(filepath);
						String name = newfile.getName();
						String ext = "";
						String sMappedInputXml = "";
						if (name.contains(".")) {
							ext = name.substring(name.lastIndexOf("."), name.length());
						}
						JPISIsIndex ISINDEX = new JPISIsIndex();
						JPDBRecoverDocData JPISDEC = new JPDBRecoverDocData();
						String strDocumentPath = sFilepath;
						File processFile = null;
						long lLngFileSize = 0L;
						processFile = new File(strDocumentPath);
	
						lLngFileSize = processFile.length();
						String lstrDocFileSize = "";
						lstrDocFileSize = Long.toString(lLngFileSize);
	
						String createdbyappname = "";
						createdbyappname = ext.replaceFirst(".", "");
						Short volIdShort = Short.valueOf(strImageIndex);
	
						DigitalAO.mLogger.debug("lLngFileSize: --" + lLngFileSize);
						
						if (lLngFileSize != 0L) {
							DigitalAO.mLogger
									.debug("sJtsIp --" + sJtsIp + " iJtsPort-- " + iJtsPort + " sCabname--" + sCabname
											+ " volIdShort.shortValue() --" + volIdShort.shortValue() + " strDocumentPath--"
											+ strDocumentPath + " JPISDEC --" + JPISDEC + "  ISINDEX-- " + ISINDEX);
							CPISDocumentTxn.AddDocument_MT(null, sJtsIp, iJtsPort, sCabname, volIdShort.shortValue(),
									strDocumentPath, JPISDEC, "", ISINDEX);
	
						}
						
						DigitalAO.mLogger.debug("dataFromDB2.size(): --" + dataFromDB2.size());
						if (dataFromDB2.size() > 0) {
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
							Date date = new Date(System.currentTimeMillis());
							String strCurrDateTime = formatter.format(date);
							for (int i = 0; i < dataFromDB2.size(); i++) {
								DigitalAO.mLogger.debug("NGOChangeDocumentProperty_Input section");
								sMappedInputXml = "<?xml version=\"1.0\"?>" + "<NGOChangeDocumentProperty_Input>"
										+ "<Option>NGOChangeDocumentProperty</Option>" + "<CabinetName>" + sCabname
										+ "</CabinetName>" + "<UserDBId>" + sSessionId
										+ "</UserDBId><Document><DocumentIndex>" + strdocumentindex.get(i)
										+ "</DocumentIndex><NoOfPages>1</NoOfPages>" + "<DocumentName>" + FrmType
										+ "</DocumentName>" + "<AccessDateTime>" + strCurrDateTime + "</AccessDateTime>"
										+ "<ExpiryDateTime>2099-12-12 0:0:0.0</ExpiryDateTime>" + "<CreatedByAppName>"
										+ createdbyappname + "</CreatedByAppName>" + "<VersionFlag>Y</VersionFlag>"
										+ "<AccessType>S</AccessType>" + "<ISIndex>" + ISINDEX.m_nDocIndex + "#"
										+ ISINDEX.m_sVolumeId + "</ISIndex><TextISIndex>0#0#</TextISIndex>"
										+ "<DocumentType>N</DocumentType>" + "<DocumentSize>" + lstrDocFileSize
										+ "</DocumentSize><Comment>" + createdbyappname
										+ "</Comment><RetainAnnotation>N</RetainAnnotation></Document>"
										+ "</NGOChangeDocumentProperty_Input>";
							}
						} else {
	
							sMappedInputXml = "<?xml version=\"1.0\"?>" + "<NGOAddDocument_Input>"
									+ "<Option>NGOAddDocument</Option>" + "<CabinetName>" + sCabname + "</CabinetName>"
									+ "<UserDBId>" + sSessionId + "</UserDBId>" + "<GroupIndex>0</GroupIndex>"
									+ "<VersionFlag>N</VersionFlag>" + "<ParentFolderIndex>" + strFolderIndex
									+ "</ParentFolderIndex>" + "<DocumentName>" + FrmType + "</DocumentName>"
									+ "<CreatedByAppName>" + createdbyappname + "</CreatedByAppName>" + "<Comment>"
									+ FrmType + "</Comment>" + "<VolumeIndex>" + ISINDEX.m_sVolumeId + "</VolumeIndex>"
									+ "<FilePath>" + strDocumentPath + "</FilePath>" + "<ISIndex>" + ISINDEX.m_nDocIndex
									+ "#" + ISINDEX.m_sVolumeId + "</ISIndex>" + "<NoOfPages>1</NoOfPages>"
									+ "<DocumentType>N</DocumentType>" + "<DocumentSize>" + lstrDocFileSize
									+ "</DocumentSize>" + "</NGOAddDocument_Input>";
	
						}
						DigitalAO.mLogger.debug("Document Addition sInputXML: " + sMappedInputXml);
						// String sOutputXml =
						// WFCustomCallBroker.execute(sMappedInputXml, sJtsIp,
						// iJtsPort, 1);
						String sOutputXML = ExecuteQueryOnServer(sMappedInputXml, iform);
						xmlParserData.setXmlString((sOutputXML));
						DigitalAO.mLogger.debug("Document Addition sOutputXml: " + sOutputXML);
						String status_D = xmlParserData.getVal("Status");
						if (status_D.equalsIgnoreCase("0")) {
							// deleteLocalDocument(sFilepath);
							return sOutputXML;
						} else {
							return "Error in Document Addition";
						}
					}
					
					catch (JPISException e) {
						return "Error in Document Addition at Volume";
					}
					
					catch (Exception e) {
						return "Exception Occurred in Document Addition";
					}
	
				}
				return "Any Error occurred in Addition of Document";
			} catch (Exception e) {
				return "Exception Occurred in SearchDocument";
			}
		}
	
		public String RISK_SCORE_DETAILS(IFormReference iform) throws IOException {
			String risk = "";
			try 
			{
				DigitalAO.mLogger.debug("Start RISK_SCORE_DETAILS: ");
				risk = getRisk_XML();
	
				String CIF = (String) iform.getValue("CIF");
				String Wi_number = getWorkitemName(iform);
				String middleWi[] = Wi_number.split("-");
				Wi_number = middleWi[1];
				String PEP = (String) iform.getValue("PEP");
				String Full_name="";
//				String MiddleName = (String)iform.getValue("Middle_Name");
//				if("".equalsIgnoreCase(MiddleName) || MiddleName == null){
//					
//				 Full_name = (String) iform.getValue("Given_Name") + " " + iform.getValue("Surname");}
//				else{
//					
//					 Full_name = (String) iform.getValue("Given_Name") + " "+MiddleName+" " + iform.getValue("Surname");}
				
				Full_name = (String) iform.getValue("Given_Name") + " " + iform.getValue("Surname");
				String emp_type = (String) iform.getValue("employement_type");
				String Nationality = (String) iform.getValue("Nationality");
				String Sec_Nationality = (String) iform.getValue("Secondary_Nationality");
				String product_typ = (String) iform.getValue("product_typw");
				String product_curr = (String) iform.getValue("product_currency");
				String Country_Residenece = (String) iform.getValue("country_of_residence");
	
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:CIF: " + CIF);
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:Wi_number: " + Wi_number);
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:Full_name: " + Full_name);
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:PEP: " + PEP);
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:emp_type: " + emp_type);
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:Nationality: " + Nationality);
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:Sec_Nationality: " + Sec_Nationality);
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:product_typ: " + product_typ);
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:product_curr: " + product_curr);
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:Country_Residenece: " + Country_Residenece);
	
				// Getting the description for product_typ.
				String Product_type_descptn = "";
				//below code commented by vinyak to chnage to display correct account type
				String account_type_query = "select account_type from NG_MASTER_DAO_PRODUCT_NAME with(nolock) where cm_code='" + product_typ + "'";
				List<List<String>> account_type_query_output = iform.getDataFromDB(account_type_query);
				DigitalAO.mLogger.debug("account_type_query_output : " + account_type_query_output);
				String account_type_val="";
				if (!account_type_query_output.isEmpty()) {
					DigitalAO.mLogger.debug("Inside account_type_query_output: ");
					 account_type_val = account_type_query_output.get(0).get(0);
					DigitalAO.mLogger.debug("account_type_val: " + account_type_val);							
				} else {
					DigitalAO.mLogger.debug("account_type_query_output is empty!!");
				}
				
				if (account_type_val.equalsIgnoreCase("Current")){
					Product_type_descptn = "Current Account";
				}
				else if(account_type_val.equalsIgnoreCase("Saving")){
					Product_type_descptn = "Savings Account";
				}
				
				/*
				if (product_typ.equalsIgnoreCase("ACNP1") || (product_typ.equalsIgnoreCase("GBNP1"))) {
					Product_type_descptn = "Current Account";
				} else {
					Product_type_descptn = "Savings Account";
				}*/
	
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:Product_type_descptn:" + Product_type_descptn);
	
				// Getting the description for Nationality.
				String Nationality_descptn = "";
				String Desc_Nationality = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE WITH(NOLOCK) where CM_CODE='"
						+ Nationality + "'";
				DigitalAO.mLogger.debug("Desc_Country_Residenece_query: " + Desc_Nationality);
				List<List<String>> output_Nationality_query = iform.getDataFromDB(Desc_Nationality);
				DigitalAO.mLogger.debug("output_Desc_Country_Residenece_query: " + output_Nationality_query);
	
				if (!output_Nationality_query.isEmpty()) {
					DigitalAO.mLogger.debug("Inside output_Nationality_query: ");
					Nationality_descptn = output_Nationality_query.get(0).get(0);
					DigitalAO.mLogger.debug("Product_type_descptn: " + Nationality_descptn);
				} else {
					DigitalAO.mLogger.debug("Nationality_descptn is empty!!");
				}
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:Nationality_descptn:" + Nationality_descptn);
	
				// Getting the description for Sec_Nationality.
				String Sec_Nationality_descptn = "";
				String Desc_Sec_Nationality = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE WITH(NOLOCK) where CM_CODE='"
						+ Sec_Nationality + "'";
				DigitalAO.mLogger.debug("Desc_Country_Residenece_query: " + Desc_Sec_Nationality);
				List<List<String>> output_Sec_Nationality_query = iform.getDataFromDB(Desc_Sec_Nationality);
				DigitalAO.mLogger.debug("output_Desc_Country_Residenece_query: " + output_Sec_Nationality_query);
	
				if (!output_Sec_Nationality_query.isEmpty()) {
					DigitalAO.mLogger.debug("Inside Sec_Nationality_descptn: ");
					Sec_Nationality_descptn = output_Sec_Nationality_query.get(0).get(0);
					DigitalAO.mLogger.debug("Sec_Nationality_descptn: " + Sec_Nationality_descptn);
				} else {
					DigitalAO.mLogger.debug("Sec_Nationality_descptn is empty!!");
				}
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:Sec_Nationality_descptn:" + Sec_Nationality_descptn);
	
				String Nationality_tag = "";
				if (!"".equalsIgnoreCase(Nationality_descptn)) {
					Nationality_tag += "<Nationality>" + Nationality_descptn + "</Nationality>";
				}
				if (!"".equalsIgnoreCase(Sec_Nationality_descptn)) {
					Nationality_tag += "<Nationality>" + Sec_Nationality_descptn + "</Nationality>";
				}
	
				// Industry Repeating tag
				String industry_tag = "";
				String final_tag_countires_W_business_Conducted = "";
				String Country_Residenece_descptn = "";
				if (emp_type.equalsIgnoreCase("Self employed")) {
					String industry_query_desc = "";
					DigitalAO.mLogger.debug("compnay_grid: Self employed");
					int compnay_grid = iform.getDataFromGrid("company_detail").size();
					DigitalAO.mLogger.debug("compnay_grid: Self employed: size" + compnay_grid);
					for (int i = 0; i < compnay_grid; i++) {
						String industry_value = iform.getTableCellValue("company_detail", i, 3);
						DigitalAO.mLogger.debug("compnay_grid : industry_value Table cell value : " + industry_value);
	
						String industry_query = "select  description from ng_dao_RCC_Industry_master  WITH(NOLOCK) where code='" + industry_value + "'";
						List<List<String>> industry_query_output = iform.getDataFromDB(industry_query);
						DigitalAO.mLogger.debug("compnay_grid : Cont_W_business_out : " + industry_query);
	
						if (!industry_query_output.isEmpty()) {
							DigitalAO.mLogger.debug("Inside Cont_W_business_out: ");
							industry_query_desc = industry_query_output.get(0).get(0);
							DigitalAO.mLogger.debug("industry_query_desc: " + industry_query_desc);
							industry_tag += "<Industry>" + industry_query_desc + "</Industry>";
						} else {
							DigitalAO.mLogger.debug("industry_query_desc is empty!!");
						}
					}
	
					// countries W business conducted logic: self employed case
	
					for (int j = 0; j < compnay_grid; j++) {
						String countries_W_bus = iform.getTableCellValue("company_detail", j, 8);
						String Cont_W_business_out_desc = "";
	
						DigitalAO.mLogger.debug("compnay_grid : countries_W_bus Table cell value : " + countries_W_bus);
	
						String[] countries_W_bus_split = countries_W_bus.split(",");
						for (int k = 0; k < countries_W_bus_split.length; k++) {
	
							String Cont_W_business_Qry = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE where CM_CODE='" + countries_W_bus_split[k] + "'";
							List<List<String>> Cont_W_business_output = iform.getDataFromDB(Cont_W_business_Qry);
	
							DigitalAO.mLogger.debug("compnay_grid : Cont_W_business_out : " + Cont_W_business_output);
	
							if (!Cont_W_business_output.isEmpty()) {
								DigitalAO.mLogger.debug("Inside Cont_W_business_out: ");
								Cont_W_business_out_desc = Cont_W_business_output.get(0).get(0);
								final_tag_countires_W_business_Conducted += "<Demographic>" + Cont_W_business_out_desc + "</Demographic>";
								DigitalAO.mLogger.debug("final_tag_countires_W_business_Conducted: " + final_tag_countires_W_business_Conducted);
							} else {
								DigitalAO.mLogger.debug("Cont_W_business_out_desc is empty!!");
							}
						}
					}
				}
	
				else if (emp_type.equalsIgnoreCase("Salaried")) {
					DigitalAO.mLogger.debug("compnay_grid: Salaried");
					industry_tag="<Industry>Employed Individual</Industry>";
					
//					String industry_query ="select description from ng_dao_RCC_Industry_master  WITH(NOLOCK) where code='"+ iform.getValue("industry_subsegment") + "'";
//					
//					DigitalAO.mLogger.debug(" RISK_SCORE_DETAILS industry_query: " + industry_query);
//					List<List<String>> output_industry_query = iform.getDataFromDB(industry_query);
//					DigitalAO.mLogger.debug("industry_query: " + output_industry_query);
//	
//					if (!output_industry_query.isEmpty())
//					{
//						DigitalAO.mLogger.debug("Inside RISK_SCORE_DETAILS output_industry_query: ");
//						industry = output_industry_query.get(0).get(0);
//						industry_tag += "<Industry>"+industry+"</Industry>";
//						DigitalAO.mLogger.debug("industry: " + industry);
//					}
//					else 
//					{
//						DigitalAO.mLogger.debug("industry is empty!!");
//					}
//					DigitalAO.mLogger.debug("RISK_SCORE_DETAILS : industry:" + industry);
					
					// salaried case : country of residence
	
					String Desc_Country_Residenece_query = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE  WITH(NOLOCK) where CM_CODE='"
							+ Country_Residenece + "'";
					DigitalAO.mLogger.debug("Desc_Country_Residenece_query: " + Desc_Country_Residenece_query);
					List<List<String>> output_Desc_Country_Residenece_query = iform.getDataFromDB(Desc_Country_Residenece_query);
					DigitalAO.mLogger.debug("output_Desc_Country_Residenece_query: " + output_Desc_Country_Residenece_query);
	
					if (!output_Desc_Country_Residenece_query.isEmpty()) {
						DigitalAO.mLogger.debug("Inside output_Desc_Country_Residenece_query: ");
						Country_Residenece_descptn = output_Desc_Country_Residenece_query.get(0).get(0);
						Country_Residenece_descptn = "<Demographic>" + Country_Residenece_descptn + "</Demographic>";
						DigitalAO.mLogger.debug("Country_Residenece_descptn_descptn: " + Country_Residenece_descptn);
					} else {
						DigitalAO.mLogger.debug("Country_Residenece_descptn is empty!!");
					}
	
					DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:Country_Residenece_descptn:" + Country_Residenece_descptn);
	
				}
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS:Industry: " + industry_tag);
	
				// replacing tags ->
	
				DigitalAO.mLogger.debug("sb.string : Risk_score: ");
	
				risk = risk.replace(">cif_id<", ">" + CIF + "<").replace(">Wi_number<", ">" + Wi_number + "<")
						.replace(">PEP<", ">" + PEP + "<").replace(">Full_name<", ">" + Full_name + "<")
						.replace(">emp_type<", ">" + emp_type + "<")
						.replace(">Nationality_tag<", ">" + Nationality_tag + "<")
						.replace(">industry_tag<", ">" + industry_tag + "<")
						.replace(">product_typ<", ">" + Product_type_descptn + "<")
						.replace(">product_curr<", ">" + product_curr + "<");
	
				if (emp_type.equalsIgnoreCase("Salaried")) {
					risk = risk.replace(">Country_Residenece<", ">" + Country_Residenece_descptn + "<");
				} else if (emp_type.equalsIgnoreCase("Self employed")) {
					risk = risk.replace(">Country_Residenece<", ">" + final_tag_countires_W_business_Conducted + "<");
				}
				DigitalAO.mLogger.debug("Start RISK_SCORE_DETAILS:  Risk_score before :" + risk);
			}
			catch (Exception e) {
				DigitalAO.mLogger.debug("RISK_SCORE_DETAILS: Exception" + e.getMessage());
			}
			return risk;
		}
		
		public String CUSTOMER_UPDATE_REQ(IFormReference iform){
			
			String CUSTOMER_UPDATE_REQ_xml=get_CUSTOMER_UPDATE_REQ_xml();
			
			try
			{
				DigitalAO.mLogger.debug("CUSTOMER_UPDATE_REQ_xml : strt: "+CUSTOMER_UPDATE_REQ_xml);
				String Gross_sal = (String) iform.getValue("gross_monthly_salary_income");
				String CIF = (String) iform.getValue("CIF");
				DigitalAO.mLogger.debug("Gross_sal : "+Gross_sal);
				DigitalAO.mLogger.debug("CIF : "+CIF);
				
				CUSTOMER_UPDATE_REQ_xml = CUSTOMER_UPDATE_REQ_xml.replace(">gross_sal<", ">" + Gross_sal + "<")
				.replace(">CIF<", ">" + CIF + "<");
			}
			
			catch (Exception e) {
				DigitalAO.mLogger.debug("CUSTOMER_UPDATE_REQ: Exception" + e.getMessage());
			}
			
			return CUSTOMER_UPDATE_REQ_xml;
		}
		
		private String getRisk_XML() {
			return "<EE_EAI_MESSAGE>" + "\n" + "<EE_EAI_HEADER>" + "\n" + "<MsgFormat>RISK_SCORE_DETAILS</MsgFormat>"
					+ "\n" + "<MsgVersion>0001</MsgVersion>" + "\n" + "<RequestorChannelId>CAS</RequestorChannelId>"
					+ "\n" + "<RequestorUserId>RAKUSER</RequestorUserId>" + "\n"
					+ "<RequestorLanguage>E</RequestorLanguage>" + "\n"
					+ "<RequestorSecurityInfo>secure</RequestorSecurityInfo>" + "\n" + "<ReturnCode>911</ReturnCode>"
					+ "\n" + "<ReturnDesc>Issuer Timed Out</ReturnDesc>" + "\n" + "<MessageId>123123453</MessageId>"
					+ "\n" + "<Extra1>REQ||SHELL.JOHN</Extra1>" + "\n"
					+ "<Extra2>YYYY-MM-DDThh:mm:ss.mmm+hh:mm</Extra2>" + "\n" + "</EE_EAI_HEADER>" + "\n"
					+ "<RiskScoreDetailsRequest>" + "\n" + "<RequestInfo>" + "\n"
					+ "<RequestType>Reference Id</RequestType>" + "\n" + "<RequestValue>Wi_number</RequestValue>" + "\n"
					+ "</RequestInfo>" + "\n" + "<RequestInfo>" + "\n" + "<RequestType>CIF Id</RequestType>" + "\n"
					+ "<RequestValue>cif_id</RequestValue>" + "\n" + "</RequestInfo>" + "\n"
					+ "<CustomerType>Individual</CustomerType>" + "\n"
					+ "<CustomerCategory>Resident Individual</CustomerCategory>" + "\n"
					+ "<IsPoliticallyExposed>PEP</IsPoliticallyExposed>" + "\n"
					+ "<CustomerName>Full_name</CustomerName>" + "\n" + "<DSAId>BATDSA1</DSAId>" + "\n"
					+ "<RMCode>AISHSC</RMCode>" + "\n" + "<EmploymentType>emp_type</EmploymentType>" + "\n"
					+ "<Segment>PERSONAL BANKING</Segment>" + "\n" + "<SubSegment>PB - NORMAL</SubSegment>" + "\n"
					+ "<Demographics>Country_Residenece</Demographics>" + "\n"
					+ "<Nationalities>Nationality_tag</Nationalities>" + "\n" + "<Industries>industry_tag</Industries>"
					+ "\n" + "<ProductsInfo>" + "\n" + "<Product>product_typ</Product>" + "\n"
					+ "<Currency>product_curr</Currency>" + "\n" + "</ProductsInfo>" + "\n"
					+ "</RiskScoreDetailsRequest>" + "\n" + "</EE_EAI_MESSAGE>" + "\n";
		}
		
		private String get_CUSTOMER_UPDATE_REQ_xml(){
			return  "<EE_EAI_MESSAGE>"+ "\n" +
					"<EE_EAI_HEADER>"+ "\n" +
				    "<MsgFormat>CUSTOMER_UPDATE_REQ</MsgFormat>"+ "\n" +
				    "<MsgVersion>001</MsgVersion>" + "\n" +
				    "<RequestorChannelId>CAS</RequestorChannelId>" + "\n" +
				   	"<RequestorUserId>RAKUSER</RequestorUserId>" + "\n" +
				   	"<RequestorLanguage>E</RequestorLanguage>" + "\n" +
				    "<RequestorSecurityInfo>secure</RequestorSecurityInfo>" + "\n" +
				    "<ReturnCode>911</ReturnCode>" + "\n" +
				    "<ReturnDesc>Issuer Timed Out</ReturnDesc>" + "\n" +
				    "<MessageId>Test_CU_0031</MessageId>" + "\n" +
				    "<Extra1>REQ||SHELL.dfgJOHN</Extra1>" + "\n" +
				    "<Extra2>2014-01-19T12:20:58.000+04:00</Extra2>" + "\n" +
				    "</EE_EAI_HEADER>" + "\n" +
				    	"<CustomerDetailsUpdateReq>" + "\n" +
				    	"<BankId>RAK</BankId>" + "\n" +
					      "<CIFId>CIF</CIFId>" + "\n" +
					     "<RetCorpFlag>R</RetCorpFlag>" + "\n" +
					      "<ProductProccessor>FINACLECORE</ProductProccessor>" + "\n" +
					      "<ActionRequired>U</ActionRequired>" + "\n" +
					      "<RtlAddnlDet>" + "\n" +
					        "<GrossSalary>gross_sal</GrossSalary>" + "\n" +
					      "</RtlAddnlDet>" + "\n" +
				    	"</CustomerDetailsUpdateReq>" + "\n" +
				    "</EE_EAI_MESSAGE>" + "\n";
		}
	
		public int readConfig() {
			Properties properties = null;
			try {
				properties = new Properties();
				properties.load(new FileInputStream(new File(System.getProperty("user.dir") + File.separator + "ConfigProps"
						+ File.separator + "digitalAOConfig.Properties")));
				DigitalAO.mLogger.debug("properties :" + properties);
				Enumeration<?> names = properties.propertyNames();
				DigitalAO.mLogger.debug("names :" + names);
	
				while (names.hasMoreElements()) {
					String name = (String) names.nextElement();
					DAOConfigProperties.put(name, properties.getProperty(name));
				}
			} catch (Exception e) {
				System.out.println("Exception in Read INI: " + e.getMessage());
				DigitalAO.mLogger.error("Exception has occured while loading properties file " + e.getMessage());
				return -1;
			}
			return 0;
		}
		
		public String createPDF(IFormReference iform,String template,String wi_name,String pdfName) throws FileNotFoundException, IOException, DocumentException
		{
			try
			{
				String pdfTemplatePath="";	
				String generatedPdfPath = "";
				String CountryName = "";
				String EmploymentType = "";
				String CustomerCategory = "";
				String IndustrySubsegment = "";
				String paramValue="";
				String Country_Residenece_descptn="";
				String Product_type_descptn = "";
				String industry="";
				String final_tag_countires_W_business_Conducted="";
				Hashtable<String, String> hashtable = new Hashtable<String, String>();
				//Reading path from property file
				Properties properties = new Properties();
				properties.load(new FileInputStream(System.getProperty("user.dir") + System.getProperty("file.separator")+ "CustomConfig" + System.getProperty("file.separator")+ "RakBankConfig.properties"));
				
				//Generating the pdf for RAK_Individual_form********************************************************************************************************** 	
				if(template.equalsIgnoreCase("Risk_Score")) 
				{
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nInside Risk_Score ");
										
						Map<String,String> columnvalues = new HashMap<String,String>(); 
						
						String CIF = (String) iform.getValue("CIF");
						String emp_type = (String) iform.getValue("employement_type");
						String Nationality = (String) iform.getValue("Nationality");
						String Wi_number = getWorkitemName(iform);
						String middleWi[] = Wi_number.split("-");
						Wi_number = middleWi[1];
						String PEP = (String) iform.getValue("PEP");
						String MiddleName = (String)iform.getValue("Middle_Name");
						String Full_name="";
						if("".equalsIgnoreCase(MiddleName) || MiddleName == null){
						 Full_name = (String) iform.getValue("Given_Name") + " " + iform.getValue("Surname");
						}
						else{
							 Full_name = (String) iform.getValue("Given_Name") + " "+MiddleName+" " + iform.getValue("Surname");
						}
						//String Full_name = (String) iform.getValue("Given_Name") + " " + iform.getValue("Surname");	
						String Sec_Nationality = (String) iform.getValue("Secondary_Nationality");
						String product_typ = (String) iform.getValue("product_typw");
						String product_curr = (String) iform.getValue("product_currency");
						String Country_Residenece = (String) iform.getValue("country_of_residence");
						
						DigitalAO.mLogger.debug("createPDF :CIF: " + CIF);
						DigitalAO.mLogger.debug("createPDF :Wi_number: " + Wi_number);
						DigitalAO.mLogger.debug("createPDF :Full_name: " + Full_name);
						DigitalAO.mLogger.debug("createPDF :PEP: " + PEP);
						DigitalAO.mLogger.debug("createPDF :emp_type: " + emp_type);
						DigitalAO.mLogger.debug("createPDF :Nationality: " + Nationality);
						DigitalAO.mLogger.debug("createPDF :Sec_Nationality: " + Sec_Nationality);
						DigitalAO.mLogger.debug("createPDF :product_typ: " + product_typ);
						DigitalAO.mLogger.debug("createPDF :product_curr: " + product_curr);
						DigitalAO.mLogger.debug("createPDF :Country_Residenece: " + Country_Residenece);
						
						// Getting the description for product_typ.
						
						
						/*
						if (product_typ.equalsIgnoreCase("ACNP1") || (product_typ.equalsIgnoreCase("GBNP1"))){
							Product_type_descptn = "Current Account";
						}
						else{
							Product_type_descptn = "Savings Account";
						}*/
						
//						above code commented by vinyak to chnage to display correct account type
						String account_type_query = "select account_type from NG_MASTER_DAO_PRODUCT_NAME with(nolock) where cm_code='" + product_typ + "'";
						List<List<String>> account_type_query_output = iform.getDataFromDB(account_type_query);
						DigitalAO.mLogger.debug("account_type_query_output : " + account_type_query_output);
						String account_type_val="";
						if (!account_type_query_output.isEmpty()) {
							DigitalAO.mLogger.debug("Inside account_type_query_output: ");
							account_type_val = account_type_query_output.get(0).get(0);
							DigitalAO.mLogger.debug("account_type_val: " + account_type_val);							
						} else {
							DigitalAO.mLogger.debug("account_type_query_output is empty!!");
						}
						
						if (account_type_val.equalsIgnoreCase("Current")){
							Product_type_descptn = "Current Account";
						}
						else if(account_type_val.equalsIgnoreCase("Saving")){
							Product_type_descptn = "Savings Account";
						}
						
						DigitalAO.mLogger.debug("createPDF : Product_type_descptn:" + Product_type_descptn);
						
						String strQuery = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE WITH(NOLOCK) where CM_CODE='"+ Nationality + "'";
						List<List<String>> outputMQXML = iform.getDataFromDB(strQuery);
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \noutputMQXML"+outputMQXML);
						if (!outputMQXML.isEmpty())
						{
							CountryName = outputMQXML.get(0).get(0);
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nCountryName"+CountryName);
						}
						
						String Sec_Nationality_descptn = "";
						String Desc_Sec_Nationality = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE WITH(NOLOCK) where CM_CODE='"
								+ Sec_Nationality + "'";
						DigitalAO.mLogger.debug("Desc_Country_Residenece_query: " + Desc_Sec_Nationality);
						List<List<String>> output_Sec_Nationality_query = iform.getDataFromDB(Desc_Sec_Nationality);
						DigitalAO.mLogger.debug("output_Desc_Country_Residenece_query: " + output_Sec_Nationality_query);
			
						if (!output_Sec_Nationality_query.isEmpty()){
							DigitalAO.mLogger.debug("createPDF : Inside Sec_Nationality_descptn: ");
							Sec_Nationality_descptn = output_Sec_Nationality_query.get(0).get(0);
							DigitalAO.mLogger.debug("createPDF: Sec_Nationality_descptn: " + Sec_Nationality_descptn);
						} else{
							DigitalAO.mLogger.debug("Sec_Nationality_descptn is empty!!");
						}
						if(!"".equalsIgnoreCase(Sec_Nationality_descptn) || Sec_Nationality_descptn != null)
						{
							CountryName+=","+Sec_Nationality_descptn;
						}
						
						columnvalues.put("NATIONALITY", CountryName);
						
						
						if(emp_type.equalsIgnoreCase("Salaried"))
						{
							/*String industry_query ="select description from ng_dao_RCC_Industry_master  WITH(NOLOCK) where code='"+ iform.getValue("industry_subsegment") + "'";
							
							DigitalAO.mLogger.debug("industry_query: " + industry_query);
							List<List<String>> output_industry_query = iform.getDataFromDB(industry_query);
							DigitalAO.mLogger.debug("industry_query: " + output_industry_query);
							
							if (!output_industry_query.isEmpty())
							{
								DigitalAO.mLogger.debug("Inside output_industry_query: ");
								industry = output_industry_query.get(0).get(0);
								DigitalAO.mLogger.debug("industry: " + industry);
							}
							else 
							{
								DigitalAO.mLogger.debug("industry is empty!!");
							}*/
							industry="Employed Individual";
							DigitalAO.mLogger.debug("createPDF : industry:" + industry);
							
							String Desc_Country_Residenece_query = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE  WITH(NOLOCK) where CM_CODE='"+ Country_Residenece + "'";
							DigitalAO.mLogger.debug("Desc_Country_Residenece_query: " + Desc_Country_Residenece_query);
							List<List<String>> output_Desc_Country_Residenece_query = iform.getDataFromDB(Desc_Country_Residenece_query);
							DigitalAO.mLogger.debug("output_Desc_Country_Residenece_query: " + output_Desc_Country_Residenece_query);
			
							if (!output_Desc_Country_Residenece_query.isEmpty())
							{
								DigitalAO.mLogger.debug("Inside output_Desc_Country_Residenece_query: ");
								Country_Residenece_descptn = output_Desc_Country_Residenece_query.get(0).get(0);
		
								DigitalAO.mLogger.debug("Country_Residenece_descptn_descptn: " + Country_Residenece_descptn);
							}
							else 
							{
								DigitalAO.mLogger.debug("Country_Residenece_descptn is empty!!");
							}
							DigitalAO.mLogger.debug("createPDF : Country_Residenece_descptn:" + Country_Residenece_descptn);
						}
						
						else if (emp_type.equalsIgnoreCase("Self employed"))
						{
							String industry_query_desc = "";
							DigitalAO.mLogger.debug("compnay_grid: Self employed");
							int compnay_grid = iform.getDataFromGrid("company_detail").size();
							DigitalAO.mLogger.debug("compnay_grid: Self employed: size" + compnay_grid);
							for (int i = 0; i < compnay_grid; i++)
							{
								String industry_value = iform.getTableCellValue("company_detail", i, 3);
								DigitalAO.mLogger.debug("compnay_grid : industry_value Table cell value : " + industry_value);
		
								String industry_query = "select  description from ng_dao_RCC_Industry_master  WITH(NOLOCK) where code='" + industry_value + "'";
								List<List<String>> industry_query_output = iform.getDataFromDB(industry_query);
								DigitalAO.mLogger.debug("compnay_grid : industry_value : " + industry_query);
		
								if (!industry_query_output.isEmpty())
								{
									DigitalAO.mLogger.debug("Inside industry_value: ");
									industry_query_desc = industry_query_output.get(0).get(0);
									DigitalAO.mLogger.debug("industry_query_desc: " + industry_query_desc);
									
									if(industry.equalsIgnoreCase(""))
									{
										industry=industry_query_desc;
									}
									else{
										industry+=", "+industry_query_desc;
									}
								}
								else
								{
									DigitalAO.mLogger.debug("industry_query_desc is empty!!");
								}
							}
							for (int j = 0; j < compnay_grid; j++) 
							{
								String countries_W_bus = iform.getTableCellValue("company_detail", j, 8);
								String Cont_W_business_out_desc = "";
				
								DigitalAO.mLogger.debug("compnay_grid : countries_W_bus Table cell value : " + countries_W_bus);
				
								String[] countries_W_bus_split = countries_W_bus.split(",");
								for (int k = 0; k < countries_W_bus_split.length; k++)
								{
				
									String Cont_W_business_Qry = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE where CM_CODE='" + countries_W_bus_split[k] + "'";
									List<List<String>> Cont_W_business_output = iform.getDataFromDB(Cont_W_business_Qry);
				
									DigitalAO.mLogger.debug("compnay_grid : Cont_W_business_out : " + Cont_W_business_output);
				
									if (!Cont_W_business_output.isEmpty()) {
										DigitalAO.mLogger.debug("Inside Cont_W_business_out: ");
										Cont_W_business_out_desc = Cont_W_business_output.get(0).get(0);
										
										if(Country_Residenece_descptn.equalsIgnoreCase(""))
										{
											Country_Residenece_descptn=Cont_W_business_out_desc;
										}
										else{
											Country_Residenece_descptn+=", "+Cont_W_business_out_desc;
										}
										DigitalAO.mLogger.debug("Country_Residenece_descptn: " + Country_Residenece_descptn);
									} else {
										DigitalAO.mLogger.debug("Cont_W_business_out_desc is empty!!");
									}
								}
							}
						}
						
						columnvalues.put("CIF_NUMBER", CIF);
						columnvalues.put("CUSTOMER_TYPE", "Individual");
						columnvalues.put("DEMOGRAPHIC", Country_Residenece_descptn);
						columnvalues.put("PEP", PEP);
						columnvalues.put("RISK_SCORE", getControlValue("risk_score",iform));
						columnvalues.put("acctype_curr", Product_type_descptn+", "+product_curr);
						columnvalues.put("P_FullName", Full_name);
						columnvalues.put("oth_custtype", "Resident Individual");
						columnvalues.put("CUSTOMER_SEGMENT", "PERSONAL BANKING");
						columnvalues.put("CUSTOMER_SUBSEGMENT", "PB - NORMAL");
						columnvalues.put("INDUSTRY_SUBSEGMENT", industry);
						columnvalues.put("EMPLOYMENT_TYPE", emp_type);
						
						if (!getControlValue("RISK_SCORE",iform).equalsIgnoreCase(""))
						{
							String rscore = getControlValue("risk_score",iform);
							if (!rscore.contains("."))
								rscore = rscore+".00";
							double riskscore = Float.parseFloat(rscore);
							String risktype = "";
							if (riskscore >= 1 && riskscore < 2 )
			                {
			                  risktype = "Low";
			                  DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncheck 11: ");
			                }
							else if(riskscore >= 2 && riskscore < 3)
			                {
			                  risktype = "Standard";
			                  DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncheck 22: ");
			                }
							else if(riskscore >= 3 && riskscore < 4)
			                {
			                  risktype = "Medium";
			                  DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncheck 33: ");
			                }
							else if(riskscore >= 4 && riskscore < 5)
			                {
			                  risktype = "High";
			                  DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncheck 44: ");
			                }
							else if(riskscore >= 5)
			                {
			                  risktype = "Elevated";
			                  DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncheck 55: ");
			                }
							columnvalues.put("final_risk_type", risktype);
						}
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncheck 2: ");
						//Enumeration<String> paramNames = request.getParameterNames();
						List<String> arrlist = new ArrayList<String>();
						arrlist.add("CIF_NUMBER");
						arrlist.add("EMPLOYMENT_TYPE");
						arrlist.add("CUSTOMER_TYPE");
						arrlist.add("oth_custtype");
						arrlist.add("CUSTOMER_SEGMENT");
						arrlist.add("CUSTOMER_SUBSEGMENT");
						arrlist.add("DEMOGRAPHIC");
						arrlist.add("INDUSTRY_SUBSEGMENT");
						arrlist.add("NATIONALITY");
						arrlist.add("PEP");
						arrlist.add("RISK_SCORE");
						arrlist.add("acctype_curr");
						arrlist.add("P_FullName");
						arrlist.add("final_risk_type");
						DigitalAO.mLogger.debug("arrlist: "+arrlist);
						//DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncolumnvalues.toString() "+columnvalues.toString() + " : ");
						Enumeration<String> paramNames = Collections.enumeration(arrlist);
						DigitalAO.mLogger.debug("paramNames: "+paramNames);
						while(paramNames.hasMoreElements())
						{
							paramValue="";
							String paramName = (String)paramNames.nextElement();
							//DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nparamName "+paramName + " : ");
							//DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncolumnvalues.toString() "+columnvalues.toString() + " : ");
							
							//Loading values from master table and mapping with pdf
							Set PDFSet = columnvalues.keySet();
							Iterator PDFIt = PDFSet.iterator();
							DigitalAO.mLogger.debug("PDFIt"+PDFIt);
							while(PDFIt.hasNext()) 
							{
								String HT_Key 	= (String)PDFIt.next();
								String HT_Value	= (String)columnvalues.get(HT_Key);
								//DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", HT_Key : "+HT_Key + " HT_Value :"+HT_Value);
								if(paramName.toString().equals(HT_Key.toString()))
								{
									paramValue=HT_Value.toString();
									DigitalAO.mLogger.debug("HT_Value: "+HT_Value);
								}						
							}
							//********************************************************
							
							//DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nparamValue 1"+paramValue);
							if(paramValue.equals(""))
							{						
								paramValue = getControlValue(paramName,iform);
							}
							
							//paramValue = paramValue.replace("`~`","%");
							//DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nparamValue 2"+paramValue);
							hashtable.put(paramName, paramValue);
						}
						pdfTemplatePath = properties.getProperty("DAO_RAK_RiskScore");
				}//*********************************************************************************************************************
				
				String dynamicPdfName = "";
				if(template.equalsIgnoreCase("Risk_Score")){
					dynamicPdfName = wi_name + pdfName + ".pdf"; //Modified by Nikita for risk score pdf on 19042018
				}
				else
				{
					dynamicPdfName = pdfName + "_" + System.currentTimeMillis()/1000*60 + ".pdf";
				}
				String path = System.getProperty("user.dir");
				pdfTemplatePath = path + pdfTemplatePath;//Getting complete path of the pdf tempplate
				generatedPdfPath = properties.getProperty("DAO_GENERTATED_PDF_PATH");//Get the loaction of the path where generated template will be saved
				generatedPdfPath += dynamicPdfName;
				generatedPdfPath = path + generatedPdfPath;//Complete path of generated PDF
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n pdfTemplatePath :" + pdfTemplatePath);		
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n after replace GeneratedPdfPath :" + generatedPdfPath);
				
				String pdfArabtype_Path=properties.getProperty("RAOP_ARABTYPE_PATH");		
				pdfArabtype_Path = path + pdfArabtype_Path;//Getting complete path of the arabtype ttf
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n pdfArabtype_Path :" + pdfArabtype_Path);
				
				PdfReader reader = new PdfReader(pdfTemplatePath);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Created reader object from template :");
				PdfStamper stamp = new PdfStamper(reader,new FileOutputStream(generatedPdfPath)); 	
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Created stamper object from destination :");
				AcroFields form=stamp.getAcroFields();
				BaseFont unicode=BaseFont.createFont(pdfArabtype_Path,BaseFont.IDENTITY_H,BaseFont.EMBEDDED);		
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Created arabtype font:");
				ArrayList al=new ArrayList();
				al.add(unicode);
				form.setSubstitutionFonts(al);
				PdfWriter p= stamp.getWriter();
				p.setRunDirection(p.RUN_DIRECTION_RTL);  
				BaseFont bf1 = BaseFont.createFont (BaseFont.TIMES_ROMAN,BaseFont.CP1252,BaseFont.EMBEDDED);				
				form.addSubstitutionFont(bf1);                 
		
				// Handling values form Hashtable
				Set PDFSet = hashtable.keySet();
				Iterator PDFIt = PDFSet.iterator();
				while(PDFIt.hasNext()) 
				{
					String HT_Key 	= (String)PDFIt.next();
					String HT_Value	= (String)hashtable.get(HT_Key);
					//DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", HT_Key : "+HT_Key + " HT_Value :"+HT_Value);
					form.setField(HT_Key,HT_Value);
				}
				stamp.setFormFlattening(true);
				stamp.close();
				
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncreatePDF : Inside service method : end");
				
				return "Successful in createPDF";
			}
			catch(Exception e)
			{
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nError in createPDF");	
				return "Error in createPDF";
			}
		}
		
		
		public String genrate_risksheet(IFormReference iform) throws FileNotFoundException, IOException, DocumentException
		{
			String PdfName="Risk_Score_Details";
			String Status=createPDF(iform,"Risk_Score",getWorkitemName(iform),PdfName);
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Status : "+Status);
			String Res="";
			if(!Status.contains("Error"))
			{
				Res=AttachDocumentWithWI(iform,getWorkitemName(iform),PdfName);
				DigitalAO.mLogger.debug(" No Error in RISK PDF Gen :  Res"+Res);
				DigitalAO.mLogger.debug("No Error in RISK PDF Gen :  Res"+Res);
				return Res;
			}
			else
			{
				DigitalAO.mLogger.debug("Error in RISK PDF Gen :  Res"+Status);
				return Res=Status;
			}
		
		}
		public static String getdateCurrentDateInSQLFormat()
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
			return simpleDateFormat.format(new Date());
		}
		
		public static String getAPUpdateIpXML(String tableName,String columnName,String strValues,String sWhere,String cabinetName,String sessionId)
		{
			if(strValues==null)
			{
				strValues = "''";
			}

			StringBuffer ipXMLBuffer=new StringBuffer();

			ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
			ipXMLBuffer.append("<APUpdate_Input>\n");
			ipXMLBuffer.append("<Option>APUpdate</Option>");
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
			ipXMLBuffer.append(sWhere);
			ipXMLBuffer.append("</WhereClause>\n");
			ipXMLBuffer.append("<EngineName>");
			ipXMLBuffer.append(cabinetName);
			ipXMLBuffer.append("</EngineName>\n");
			ipXMLBuffer.append("<SessionId>");
			ipXMLBuffer.append(sessionId);
			ipXMLBuffer.append("</SessionId>\n");
			ipXMLBuffer.append("</APUpdate_Input>\n");

			return ipXMLBuffer.toString();
		}
		
		public static String apSelectWithColumnNames(String QueryString, String cabinetName, String sessionID)
		{
			StringBuffer ipXMLBuffer=new StringBuffer();

			ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
			ipXMLBuffer.append("<APSelect_Input>\n");
			ipXMLBuffer.append("<Option>APSelectWithColumnNames</Option>\n");
			ipXMLBuffer.append("<Query>");
			ipXMLBuffer.append(QueryString);
			ipXMLBuffer.append("</Query>\n");
			ipXMLBuffer.append("<EngineName>");
			ipXMLBuffer.append(cabinetName);
			ipXMLBuffer.append("</EngineName>\n");
			ipXMLBuffer.append("<SessionId>");
			ipXMLBuffer.append(sessionID);
			ipXMLBuffer.append("</SessionId>\n");
			ipXMLBuffer.append("</APSelect_Input>");

			return ipXMLBuffer.toString();
		}
		
		public static String apInsert(String sCabName, String sSessionId, String colNames, String colValues, String tableName)
		{
			StringBuffer ipXMLBuffer=new StringBuffer();

			ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
			ipXMLBuffer.append("<APInsertExtd_Input>\n");
			ipXMLBuffer.append("<Option>APInsert</Option>");
			ipXMLBuffer.append("<TableName>");
			ipXMLBuffer.append(tableName);
			ipXMLBuffer.append("</TableName>");
			ipXMLBuffer.append("<ColName>");
			ipXMLBuffer.append(colNames);
			ipXMLBuffer.append("</ColName>\n");
			ipXMLBuffer.append("<Values>");
			ipXMLBuffer.append(colValues);
			ipXMLBuffer.append("</Values>\n");
			ipXMLBuffer.append("<EngineName>");
			ipXMLBuffer.append(sCabName);
			ipXMLBuffer.append("</EngineName>\n");
			ipXMLBuffer.append("<SessionId>");
			ipXMLBuffer.append(sSessionId);
			ipXMLBuffer.append("</SessionId>\n");
			ipXMLBuffer.append("</APInsertExtd_Input>");

			return ipXMLBuffer.toString();
		}
		
		public static String getAPProcedureInputXML(String engineName,String sSessionId,String procName,String Params)
		{
			StringBuffer bfrInputXML = new StringBuffer();
			bfrInputXML.append("<?xml version=\"1.0\"?>\n");
			bfrInputXML.append("<APProcedure_WithDBO_Input>\n");
			bfrInputXML.append("<Option>APProcedure_WithDBO</Option>\n");
			bfrInputXML.append("<ProcName>");
			bfrInputXML.append(procName);
			bfrInputXML.append("</ProcName>");
			bfrInputXML.append("<Params>");
			bfrInputXML.append(Params);
			bfrInputXML.append("</Params>");
			bfrInputXML.append("<EngineName>");
			bfrInputXML.append(engineName);
			bfrInputXML.append("</EngineName>");
			bfrInputXML.append("<SessionId>");
			bfrInputXML.append(sSessionId);
			bfrInputXML.append("</SessionId>");
			bfrInputXML.append("</APProcedure_WithDBO_Input>");		
			return bfrInputXML.toString();
		}
	}