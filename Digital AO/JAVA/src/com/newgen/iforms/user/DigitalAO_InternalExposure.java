package com.newgen.iforms.user;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import com.newgen.iforms.custom.IFormReference;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.omni.wf.util.excp.NGException;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;

public class DigitalAO_InternalExposure extends DigitalAO_Common {
	private static NGEjbClient ngEjbClientExposure;

	
	public String onclickevent(IFormReference iform,String control,String StringData) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException 
	{
		try{
			final HashMap<String, String> CheckGridDataMap = new HashMap<String, String>();
			
			ngEjbClientExposure = NGEjbClient.getSharedInstance();
			String processInstanceID=getWorkitemName(iform);
			String workstepName= getActivityName(iform);
			DigitalAO.mLogger.debug("processInstanceID :" +processInstanceID);
			String cifId=(String) iform.getValue("CIF");
			DigitalAO.mLogger.debug("cifId :" +cifId);
			
			int socketConnectionTimeOut=60;
			int integrationWaitTime=65;
			CheckGridDataMap.put("CIF", cifId);
			
			String cabinetName = getCabinetName(iform);
	    	String wi_name = getWorkitemName(iform);
	    	String ws_name = getActivityName(iform);
	    	String sessionID = getSessionId(iform);
	    	String userName = getUserName(iform);
	    	String jtsPort=iform.getServerPort();
	    	String jtsIP=iform.getServerIp();
			
			HashMap<String, String> socketDetailsMap = socketConnectionDetails(cabinetName, jtsIP, jtsPort, sessionID);
			
			String  intergrationStatus = callInternalExposure(iform,processInstanceID, CheckGridDataMap, integrationWaitTime, socketConnectionTimeOut, socketDetailsMap);
	    	    if(!intergrationStatus.equalsIgnoreCase("N"))
	    	    {
	    	    	DigitalAO.mLogger.debug("Internal exposure call successful with intergrationStatus: " + intergrationStatus);
	    	    	String inputXML = DigitalAO_Common.getAPProcedureInputXML(cabinetName,sessionID,"NG_DAO_SP_Miscellaneous","'"+processInstanceID+"'");
	    	    	DigitalAO.mLogger.debug("AP proc inputXML :" +inputXML);
	    	    	String  strOutputXml = WFNGExecute(inputXML, jtsIP, jtsPort, 1);
	    	    	DigitalAO.mLogger.debug("AP proc strOutputXml :" +strOutputXml);
	    	    	if(strOutputXml.indexOf("<MainCode>0</MainCode>")>-1)	
	    			{
	    	    		DigitalAO.mLogger.debug("Inserted in procedure successfully ");
	    	    		return "Inserted";
	    			}	
	    			else
	    			{
	    				DigitalAO.mLogger.debug("Not inserted in procedure  ");
	    				return "NotInserted";
	    			}
	    	    }
	    	    else{
	    	    	DigitalAO.mLogger.debug("Internal exposure call failed with intergrationStatus: " + intergrationStatus);
	    	    	return "NotInserted";
	    	    }
				
	    	    
		}
		catch (Exception e) {
			DigitalAO.mLogger.debug("getMessage:  onclickevent :" + e.getMessage());
			 return "NotInserted";
		}
		
	}
	
	
	 private  String callInternalExposure(IFormReference iform,String wiName, HashMap<String, String> CheckGridDataMap, int integrationWaitTime, int socketConnectionTimeOut,HashMap<String, String> socketDetailsMap)
		      throws IOException, Exception
		      {
		    	String flag = "";
		    	String return_code = "";
		    	String cabinetName = getCabinetName(iform);
		    	String wi_name = getWorkitemName(iform);
		    	String ws_name = getActivityName(iform);
		    	String sessionID = getSessionId(iform);
		    	String userName = getUserName(iform);
		    	String ServerPort=iform.getServerPort();
		    	String ServerIp=iform.getServerIp();
		    	
		    	DigitalAO.mLogger.debug("cabinetName :" +cabinetName);
		    	DigitalAO.mLogger.debug("ServerPort :" +ServerPort);
		    	DigitalAO.mLogger.debug("ServerIp :" +ServerIp);
		    	DigitalAO.mLogger.debug("sessionID :" +sessionID);
		    	/*
		    	String sMQuery = "SELECT SocketServerIP, SocketServerPort FROM NG_BPM_MQ_TABLE with (nolock) where ProcessName = 'DigitalAO' and CallingSource = 'Form'";
				List<List<String>> outputMQXML = iform.getDataFromDB(sMQuery);
				if (!outputMQXML.isEmpty()) {
					ServerIp = outputMQXML.get(0).get(0);
					ServerPort = outputMQXML.get(0).get(1);
					DigitalAO.mLogger.debug("socketServerIP : " + ServerIp);
					DigitalAO.mLogger.debug("SocketServerPort : " + ServerPort);
				}
		      */
		    	
		    	
			    	Date d1 = new Date();
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
					String DateExtra2 = sdf1.format(d1)+"+04:00";
					
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.mmm");
					String ReqDateTime = sdf2.format(d1);
					
			        StringBuilder sInputXML = new StringBuilder("<EE_EAI_MESSAGE><EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion><RequestorChannelId>BPM</RequestorChannelId>"
			          + "<RequestorUserId>RAKUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo>"
			          + "<ReturnCode>911</ReturnCode><ReturnDesc>IssuerTimedOut</ReturnDesc><MessageId>CUSTOMER_EXPOSUER_0V27</MessageId><Extra1>REQ||SHELL.JOHN</Extra1>"
			          + "<Extra2>"+DateExtra2+"</Extra2></EE_EAI_HEADER><CustomerExposureRequest><BankId>RAK</BankId><BranchId>RAK123</BranchId>"
			          + "<RequestType>InternalExposure</RequestType><CIFId><CIFIdType>Primary</CIFIdType><CIFIdValue>" + CheckGridDataMap.get("CIF")
			          + "</CIFIdValue></CIFId></CustomerExposureRequest>" + "</EE_EAI_MESSAGE>");
			
			        DigitalAO.mLogger.debug("Request  XML for InternalExposure  " + sInputXML);
			        
			        String responseXML = socketConnection(cabinetName, userName,sessionID,ServerIp,ServerPort, wiName, "InternalExposure_Integration", socketConnectionTimeOut, integrationWaitTime,  sInputXML,socketDetailsMap);
			        responseXML=responseXML.replaceAll("<APMQPUTGET_Output>", "").replaceAll("<MQ_RESPONSE_XML>", "").replaceAll("</MQ_RESPONSE_XML>", "").replaceAll("</APMQPUTGET_Output>","").trim();
			       // responseXML="<?xml version=\"1.0\"?><EE_EAI_MESSAGE xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><EE_EAI_HEADER><MsgFormat>CUSTOMER_EXPOSURE</MsgFormat><MsgVersion>0001</MsgVersion><RequestorChannelId>BPM</RequestorChannelId><RequestorUserId>RAKUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>Successful</ReturnDesc><MessageId>CAS169709457192810</MessageId><InputMessageId>CAS169709457192810</InputMessageId><Extra1>REP||SHELL.JOHN</Extra1><Extra2>2023-10-12T11:09:33.046+04:00</Extra2></EE_EAI_HEADER><CustomerExposureResponse><RequestType>InternalExposure</RequestType><IsDirect>Y</IsDirect><CustInfo><CustId><CustIdType>CIF Id</CustIdType><CustIdValue>3016641</CustIdValue></CustId><FullNm></FullNm><BirthDt>1988-09-12</BirthDt><Nationality>INDIAN</Nationality><CustSegment>PERSONAL BANKING</CustSegment><CustSubSegment>PB - NORMAL</CustSubSegment><RMName>PERSONAL BANKER</RMName><CreditGrade>P2 - PERSONAL - ACCEPTABLE CREDIT</CreditGrade><BorrowingCustomer>N</BorrowingCustomer></CustInfo><ProductExposureDetails><AcctDetails><AcctId>0253016641001</AcctId><IBANNumber>AE740400000253016641001</IBANNumber><AcctStat>ACTIVE</AcctStat><AcctCur>AED</AcctCur><AcctNm>SEEMA JAGRATI</AcctNm><AcctType>CURRENT ACCOUNT</AcctType><AcctSegment>PBD</AcctSegment><AcctSubSegment>PBN</AcctSubSegment><CustRoleType>Main</CustRoleType><KeyDt><KeyDtType>AccountOpenDate</KeyDtType><KeyDtValue>2023-10-10</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitSactionDate</KeyDtType><KeyDtValue>2023-10-10</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitExpiryDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitStartDate</KeyDtType><KeyDtValue>2023-10-10</KeyDtValue></KeyDt><AmountDtls><AmtType>AvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>ClearBalanceAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>LedgerBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>EffectiveAvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>CumulativeDebitAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>SanctionLimit</AmtType><Amt>0</Amt></AmountDtls><WriteoffStat>Y</WriteoffStat><WorstDelay24Months>P2</WorstDelay24Months><MonthsOnBook>1.00</MonthsOnBook><LastRepmtDt>OCT</LastRepmtDt><IsCurrent>Y</IsCurrent><ChargeOffFlag>N</ChargeOffFlag><SOLID>025</SOLID><DelinquencyInfo><BucketType>DaysPastDue</BucketType><BucketValue>0</BucketValue></DelinquencyInfo></AcctDetails><AcctDetails><AcctId>0033016641001</AcctId><IBANNumber>AE800400000033016641001</IBANNumber><AcctStat>ACTIVE</AcctStat><AcctCur>AED</AcctCur><AcctNm>SEEMA JAGRATI</AcctNm><AcctType>CURRENT ACCOUNT</AcctType><AcctSegment>PBD</AcctSegment><AcctSubSegment>PBN</AcctSubSegment><CustRoleType>Main</CustRoleType><KeyDt><KeyDtType>AccountOpenDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitSactionDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitExpiryDate</KeyDtType><KeyDtValue>2023-10-12</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitStartDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><AmountDtls><AmtType>AvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>ClearBalanceAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>LedgerBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>EffectiveAvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>CumulativeDebitAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>SanctionLimit</AmtType><Amt>0</Amt></AmountDtls><WriteoffStat>Y</WriteoffStat><WorstDelay24Months>P2</WorstDelay24Months><MonthsOnBook>1.00</MonthsOnBook><LastRepmtDt>OCT</LastRepmtDt><IsCurrent>Y</IsCurrent><ChargeOffFlag>N</ChargeOffFlag><SOLID>003</SOLID><DelinquencyInfo><BucketType>DaysPastDue</BucketType><BucketValue>0</BucketValue></DelinquencyInfo></AcctDetails><AcctDetails><AcctId>0353016641001</AcctId><IBANNumber>AE360400000353016641001</IBANNumber><AcctStat>ACTIVE</AcctStat><AcctCur>AED</AcctCur><AcctNm>SEEMA JAGRATI</AcctNm><AcctType>CURRENT ACCOUNT</AcctType><AcctSegment>PBD</AcctSegment><AcctSubSegment>PBN</AcctSubSegment><CustRoleType>Main</CustRoleType><KeyDt><KeyDtType>AccountOpenDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitSactionDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitExpiryDate</KeyDtType><KeyDtValue>2023-10-12</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitStartDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><AmountDtls><AmtType>AvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>ClearBalanceAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>LedgerBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>EffectiveAvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>CumulativeDebitAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>SanctionLimit</AmtType><Amt>0</Amt></AmountDtls><WriteoffStat>Y</WriteoffStat><WorstDelay24Months>P2</WorstDelay24Months><MonthsOnBook>1.00</MonthsOnBook><LastRepmtDt>OCT</LastRepmtDt><IsCurrent>Y</IsCurrent><ChargeOffFlag>N</ChargeOffFlag><SOLID>035</SOLID><DelinquencyInfo><BucketType>DaysPastDue</BucketType><BucketValue>0</BucketValue></DelinquencyInfo></AcctDetails><AcctDetails><AcctId>0253016641002</AcctId><IBANNumber>AE470400000253016641002</IBANNumber><AcctStat>ACTIVE</AcctStat><AcctCur>AED</AcctCur><AcctNm>SEEMA JAGRATI</AcctNm><AcctType>CURRENT ACCOUNT</AcctType><AcctSegment>PBD</AcctSegment><AcctSubSegment>PBN</AcctSubSegment><CustRoleType>Main</CustRoleType><KeyDt><KeyDtType>AccountOpenDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitSactionDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitExpiryDate</KeyDtType><KeyDtValue>2023-10-12</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitStartDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><AmountDtls><AmtType>AvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>ClearBalanceAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>LedgerBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>EffectiveAvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>CumulativeDebitAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>SanctionLimit</AmtType><Amt>0</Amt></AmountDtls><WriteoffStat>Y</WriteoffStat><WorstDelay24Months>P2</WorstDelay24Months><MonthsOnBook>1.00</MonthsOnBook><LastRepmtDt>OCT</LastRepmtDt><IsCurrent>Y</IsCurrent><ChargeOffFlag>N</ChargeOffFlag><SOLID>025</SOLID><DelinquencyInfo><BucketType>DaysPastDue</BucketType><BucketValue>0</BucketValue></DelinquencyInfo></AcctDetails><AcctDetails><AcctId>0253016641003</AcctId><IBANNumber>AE200400000253016641003</IBANNumber><AcctStat>ACTIVE</AcctStat><AcctCur>AED</AcctCur><AcctNm>SEEMA JAGRATI</AcctNm><AcctType>CURRENT ACCOUNT</AcctType><AcctSegment>PBD</AcctSegment><AcctSubSegment>PBN</AcctSubSegment><CustRoleType>Main</CustRoleType><KeyDt><KeyDtType>AccountOpenDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitSactionDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitExpiryDate</KeyDtType><KeyDtValue>2023-10-12</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitStartDate</KeyDtType><KeyDtValue>2023-10-11</KeyDtValue></KeyDt><AmountDtls><AmtType>AvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>ClearBalanceAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>LedgerBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>EffectiveAvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>CumulativeDebitAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>SanctionLimit</AmtType><Amt>0</Amt></AmountDtls><WriteoffStat>Y</WriteoffStat><WorstDelay24Months>P2</WorstDelay24Months><MonthsOnBook>1.00</MonthsOnBook><LastRepmtDt>OCT</LastRepmtDt><IsCurrent>Y</IsCurrent><ChargeOffFlag>N</ChargeOffFlag><SOLID>025</SOLID><DelinquencyInfo><BucketType>DaysPastDue</BucketType><BucketValue>0</BucketValue></DelinquencyInfo></AcctDetails><AcctDetails><AcctId>0353016641002</AcctId><IBANNumber>AE090400000353016641002</IBANNumber><AcctStat>ACTIVE</AcctStat><AcctCur>AED</AcctCur><AcctNm>SEEMA JAGRATI</AcctNm><AcctType>CURRENT ACCOUNT</AcctType><AcctSegment>PBD</AcctSegment><AcctSubSegment>PBN</AcctSubSegment><CustRoleType>Main</CustRoleType><KeyDt><KeyDtType>AccountOpenDate</KeyDtType><KeyDtValue>2023-10-12</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitSactionDate</KeyDtType><KeyDtValue>2023-10-12</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitExpiryDate</KeyDtType><KeyDtValue>2023-10-13</KeyDtValue></KeyDt><KeyDt><KeyDtType>LimitStartDate</KeyDtType><KeyDtValue>2023-10-12</KeyDtValue></KeyDt><AmountDtls><AmtType>AvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>ClearBalanceAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>LedgerBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>EffectiveAvailableBalance</AmtType><Amt>0.00</Amt></AmountDtls><AmountDtls><AmtType>CumulativeDebitAmount</AmtType><Amt>0</Amt></AmountDtls><AmountDtls><AmtType>SanctionLimit</AmtType><Amt>0</Amt></AmountDtls><WriteoffStat>Y</WriteoffStat><WorstDelay24Months>P2</WorstDelay24Months><MonthsOnBook>0.00</MonthsOnBook><LastRepmtDt>OCT</LastRepmtDt><IsCurrent>Y</IsCurrent><ChargeOffFlag>N</ChargeOffFlag><SOLID>035</SOLID><DelinquencyInfo><BucketType>DaysPastDue</BucketType><BucketValue>0</BucketValue></DelinquencyInfo></AcctDetails></ProductExposureDetails></CustomerExposureResponse></EE_EAI_MESSAGE>";
			        XMLParser xmlParserSocketDetails= new XMLParser(responseXML);
			        
				    return_code = xmlParserSocketDetails.getValueOf("ReturnCode");
				    String return_desc = xmlParserSocketDetails.getValueOf("ReturnDesc").replace("'", "");
					if (return_desc.trim().equalsIgnoreCase(""))
						return_desc = xmlParserSocketDetails.getValueOf("Description").replace("'", "");
					
					String MsgId = "";
					if (responseXML.contains("<MessageId>"))
						MsgId = xmlParserSocketDetails.getValueOf("MessageId");
				    String CallStatus = "";
				    if(return_code.equals("0000") || return_code.equals("CINF377")) // POLP-10724 considering CINF377 (will come for unverified cif) as success, and EXISTINGNTB flag will set to NTB
				    	CallStatus="Success";
				    else
				    	CallStatus="Failure";
				    
			        flag = getOutputXMLValues(responseXML, ServerIp, ServerPort,
			        		sessionID,cabinetName, wiName, CheckGridDataMap.get("Product"),
			          CheckGridDataMap.get("SubProduct"), CheckGridDataMap.get("CIF"), CheckGridDataMap.get("CUSTOMER_TYPE"), CheckGridDataMap.get("RELATEDPARTYID"));
			        flag = flag == "true" ? "Y" : "N";
			        
			        if(return_code.equals("CINF377"))
			        	flag = "NTB";
		        
		    	
		        return flag;
		      }
	 public static String getOutputXMLValues(String parseXml, String wrapperIP, String wrapperPort, String sessionId, String cabinetName, String wi_name, String prod,
		      String subprod, String cifId, String cust_type, String RelatedPartyId)

		      {
		    	DigitalAO.mLogger.debug("Inside getOutputXMLValues");
		        String outputXMLHead = "";
		        String outputXMLMsg = "";
		        String returnDesc = "";
		        String returnCode = "";
		        String response = "";
		        String returnType = "";
		        String result_str = "";
		        String MsgFormat = "";
		        	        
		        try
		          {

		            if (parseXml.indexOf("<EE_EAI_HEADER>") > -1)
		              {
		                outputXMLHead = parseXml.substring(parseXml.indexOf("<EE_EAI_HEADER>"), parseXml.indexOf("</EE_EAI_HEADER>") + 16);
		               
		              }
		            if (outputXMLHead.indexOf("<MsgFormat>") > -1)
		              {
		                response = outputXMLHead.substring(outputXMLHead.indexOf("<MsgFormat>") + 11, outputXMLHead.indexOf("</MsgFormat>"));
		                
		              }
		            if (outputXMLHead.indexOf("<ReturnDesc>") > -1)
		              {
		                returnDesc = outputXMLHead.substring(outputXMLHead.indexOf("<ReturnDesc>") + 12, outputXMLHead.indexOf("</ReturnDesc>"));
		                
		              }
		            if (outputXMLHead.indexOf("<ReturnCode>") > -1)
		              {
		                returnCode = outputXMLHead.substring(outputXMLHead.indexOf("<ReturnCode>") + 12, outputXMLHead.indexOf("</ReturnCode>"));

		              }
		           
		            if (parseXml.indexOf("<RequestType>") > -1)
		              {
		                returnType = parseXml.substring(parseXml.indexOf("<RequestType>") + 13, parseXml.indexOf("</RequestType>"));

		                if ("0000".equalsIgnoreCase(returnCode) || ("ExternalExposure".equalsIgnoreCase(returnType) && ("B003".equalsIgnoreCase(returnCode) || "B005".equalsIgnoreCase(returnCode))))
		                  {
		                    if ("InternalExposure".equalsIgnoreCase(returnType))
		                      {
		                        result_str = parseInternalExposure(returnType, parseXml, wrapperIP, wrapperPort, sessionId, cabinetName, wi_name, prod, subprod, cifId, cust_type, RelatedPartyId);
		                      }		                    
		                  }
		                             
		              
		              }

		           


		            returnType = parseXml.substring(parseXml.indexOf("<MsgFormat>") + 11, parseXml.indexOf("</MsgFormat>"));
		            
		            if (returnType.equalsIgnoreCase("FINANCIAL_SUMMARY") && (result_str.equalsIgnoreCase("")))
		              {
		                result_str = returnCode;
		               
		              }
		          }
		        catch (Exception e)
		          {
		            DigitalAO.mLogger.debug("Exception occured in getOutputXMLValues: " + e.getMessage());
		            e.printStackTrace();
		           
		            result_str = "Failure";
		          }
		        return (result_str);
		      }

	 public static String parseInternalExposure(String returnType, String parseXml, String wrapperIP, String wrapperPort, String sessionId, String cabinetName, String wi_name,
		      String prod, String subprod, String cifId, String cust_type, String RelatedPartyId)
		      {
		        String flag1 = "";
		        String tagName = "";
		        String subTagName = "";
		        String sTableName = "";
		        String sParentTagName = "";
		        String result = "";
		        
		        String subtag_single = "";
		        InputStream is = new ByteArrayInputStream(parseXml.getBytes());
		        System.out.println(parseXml);
		        DigitalAO.mLogger.debug("Inside parseInternalExposure - cifid request-"+cifId);
		        try
		          {
		            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		            Document doc = dBuilder.parse(is);
		            doc.getDocumentElement().normalize();

		            NodeList nList_loan = doc.getElementsByTagName("CustomerExposureResponse");


		            for (int i = 0; i < nList_loan.getLength(); i++)
		              {
		                Node node = nList_loan.item(i);
		                Document newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		                DOMImplementationLS abc = (DOMImplementationLS) newXmlDocument.getImplementation();
		                LSSerializer lsSerializer = abc.createLSSerializer();

		                Element root = newXmlDocument.createElement("root");
		                newXmlDocument.appendChild(root);
		                root.appendChild(newXmlDocument.importNode(node, true));
		                String n_parseXml = lsSerializer.writeToString(newXmlDocument);
		                n_parseXml = n_parseXml.substring(n_parseXml.indexOf("<root>") + 6, n_parseXml.indexOf("</root>"));
		                cifId =
		                  (n_parseXml.contains("<CustIdValue>")) ? n_parseXml.substring(n_parseXml.indexOf("<CustIdValue>") + "</CustIdValue>".length() - 1, n_parseXml.indexOf("</CustIdValue>")) : cifId;
		                  
		                DigitalAO.mLogger.debug("Inside parseInternalExposure - cifid response-"+cifId);  
		               
		                tagName = "LoanDetails";
		                subTagName = "KeyDt,AmountDtls,DelinquencyInfo";
		                sTableName = "ng_dao_gr_InternalExpo_LoanDetails";
		                subtag_single = "";
		                flag1 = commonParseProduct(n_parseXml, tagName, wi_name, returnType, sTableName, wrapperIP, wrapperPort, sessionId, cabinetName, subTagName, prod, subprod, cifId,
		                   cust_type, subtag_single, RelatedPartyId);
		                
		                if (flag1.equalsIgnoreCase("true"))
		                  {
		                    tagName = "CardDetails";
		                    subTagName = "KeyDt,AmountDtls,DelinquencyInfo";
		                    sTableName = "ng_dao_gr_InternalExpo_CardDetails";
		                    subtag_single = "";
		                    flag1 = commonParseProduct(n_parseXml, tagName, wi_name, returnType, sTableName, wrapperIP, wrapperPort, sessionId, cabinetName, subTagName, prod, subprod, cifId, cust_type, subtag_single, RelatedPartyId);

		                    if (flag1.equalsIgnoreCase("true"))
		                      {
		                        tagName = "InvestmentDetails";
		                        subTagName = "AmountDtls";
		                        sTableName = "ng_dao_gr_InternalExpo_InvestmentDetails";
		                        subtag_single = "";
		                        flag1 = commonParseProduct(n_parseXml, tagName, wi_name, returnType, sTableName, wrapperIP, wrapperPort, sessionId, cabinetName, subTagName, prod, subprod,
		                          cifId, cust_type, subtag_single, RelatedPartyId);
		                        
		                        if (flag1.equalsIgnoreCase("true"))
		                          {
		                            tagName = "AcctDetails";
		                            subTagName = "KeyDt,AmountDtls,DelinquencyInfo";
		                            sTableName = "ng_dao_gr_InternalExpo_AcctDetails";
		                            subtag_single = "ODDetails";
		                            flag1 = commonParseProduct(n_parseXml, tagName, wi_name, returnType, sTableName, wrapperIP, wrapperPort, sessionId, cabinetName, subTagName, prod, subprod,
		                              cifId, cust_type, subtag_single, RelatedPartyId);
		                            if (flag1.equalsIgnoreCase("true"))
		                              {
		                                tagName = "Derived";
		                                subTagName = "";
		                                sTableName = "ng_dao_gr_InternalExpo_Derived";
		                                subtag_single = "";
		                                flag1 = commonParseProduct(n_parseXml, tagName, wi_name, returnType, sTableName, wrapperIP, wrapperPort, sessionId, cabinetName, subTagName, prod,
		                                  subprod, cifId, cust_type, subtag_single, RelatedPartyId);
		                                if (flag1.equalsIgnoreCase("true"))
		                                  {
		                                    tagName = "RecordDestribution";
		                                    subTagName = "";
		                                    sTableName = "ng_dao_gr_InternalExpo_RecordDestribution";
		                                    subtag_single = "";
		                                    flag1 = commonParseProduct(n_parseXml, tagName, wi_name, returnType, sTableName, wrapperIP, wrapperPort, sessionId, cabinetName, subTagName, prod,
		                                      subprod, cifId, cust_type, subtag_single, RelatedPartyId);
		                                    //Deepak 22 july 2019 new condition added to save custinfo
		                                    if (flag1.equalsIgnoreCase("true"))
		                                      {
		                                        tagName = "CustInfo";
		                                        subTagName = "";
		                                        sTableName = "ng_dao_gr_InternalExpo_CustInfo";
		                                        subtag_single = "";
		                                        flag1 = commonParseProduct(n_parseXml, tagName, wi_name, returnType, sTableName, wrapperIP, wrapperPort, sessionId, cabinetName, subTagName,
		                                          prod, subprod, cifId, cust_type, subtag_single, RelatedPartyId);
		                                      }
		                                    else
		                                      {
		                                        flag1 = "false";
		                                      }
		                                  }
		                                else
		                                  {
		                                    flag1 = "false";
		                                  }
		                              }
		                            else
		                              {
		                                flag1 = "false";
		                              }
		                          }
		                        else
		                          {
		                            flag1 = "false";
		                          }
		                      }
		                    else
		                      {
		                        flag1 = "false";
		                      }
		                  }
		                else
		                  {
		                    flag1 = "false";
		                  }
		              }
		          }
		        catch (Exception e)
		          {
		            DigitalAO.mLogger.debug("Exception occured in parseInternalExposure: " + e.getMessage());
		            e.printStackTrace();
		            flag1 = "false";
		          }
		        finally
		          {
		            try
		              {
		                if (is != null)
		                  {
		                    is.close();
		                    is = null;
		                  }
		              }
		            catch (Exception e)
		              {
		                DigitalAO.mLogger.debug("Exception occured in is close:  " + e.getMessage());
		              }
		          }
		        return flag1;
		      }
	 
	 public static String commonParseProduct(String parseXml, String tagName, String wi_name, String returnType, String sTableName, String wrapperIP, String wrapperPort, String sessionId,
		      String cabinetName, String subTagName, String prod, String subprod, String cifId, String cust_type, String subtag_single, String RelatedPartyId)
		      {
		        String retVal = "";

		        try
		          {
		            if (!parseXml.contains(tagName))
		              {
		                return "true";
		              }
		            else
		              {
		                DigitalAO.mLogger.debug("inside commonParseProduct for: "+sTableName);
		                String[] valueArr = null;
		                String strInputXml = "";
		                String strOutputXml = "";
		                String columnName = "";
		                String columnValues = "";
		                String tagNameU = "";
		                String subTagNameU = "";
		                String subTagNameU_2 = "";
		                String mainCode = "";
		                String sWhere = "";
		                String row_updated = "";
		                String selectdata = "";
		                String sQry = "";
		                String ReportUrl = "";
		                String NoOfContracts = "";
		                String ECRN = "";
		                String BorrowingCustomer = "";
		                String FullNm = "";
		                String TotalOutstanding = "";
		                String TotalOverdue = "";

		                String companyUpdateQuery = "";
		                String companiestobeUpdated = "";
		                boolean stopIndividualToInsert = false;
		                String referenceNo = "";
		                String scoreInfo = "";
		                String Aecb_Score = "";
		                String range = "";

		                referenceNo =
		                  (parseXml.contains("<ReferenceNumber>")) ? parseXml.substring(parseXml.indexOf("<ReferenceNumber>") + "</ReferenceNumber>".length() - 1, parseXml.indexOf("</ReferenceNumber>")) : "";
		                if (parseXml.contains("<ScoreInfo>"))
		                  {
		                    scoreInfo = parseXml.substring(parseXml.indexOf("<ScoreInfo>") + "</ScoreInfo>".length() - 1, parseXml.indexOf("</ScoreInfo>"));
		                    Aecb_Score = (scoreInfo.contains("<Value>")) ? scoreInfo.substring(scoreInfo.indexOf("<Value>") + "</Value>".length() - 1, scoreInfo.indexOf("</Value>")) : "";
		                    range = (scoreInfo.contains("<Range>")) ? scoreInfo.substring(scoreInfo.indexOf("<Range>") + "</Range>".length() - 1, scoreInfo.indexOf("</Range>")) : "";
		                    DigitalAO.mLogger.debug("parsexml jsp: commonParse: AECB Score: " + Aecb_Score + " Range: " + range);
		                  }

		                
		                ReportUrl = (parseXml.contains("<ReportUrl>")) ? parseXml.substring(parseXml.indexOf("<ReportUrl>") + "</ReportUrl>".length() - 1, parseXml.indexOf("</ReportUrl>")) : "";
		                
		                FullNm = (parseXml.contains("<FullNm>")) ? parseXml.substring(parseXml.indexOf("<FullNm>") + "</FullNm>".length() - 1, parseXml.indexOf("</FullNm>")) : "";
		                TotalOutstanding = (parseXml.contains("<TotalOutstanding>"))
		                  ? parseXml.substring(parseXml.indexOf("<TotalOutstanding>") + "</TotalOutstanding>".length() - 1, parseXml.indexOf("</TotalOutstanding>")) : "";
		                TotalOverdue =
		                  (parseXml.contains("<TotalOverdue>")) ? parseXml.substring(parseXml.indexOf("<TotalOverdue>") + "</TotalOverdue>".length() - 1, parseXml.indexOf("</TotalOverdue>")) : "";
		                NoOfContracts =
		                  (parseXml.contains("<NoOfContracts>")) ? parseXml.substring(parseXml.indexOf("<NoOfContracts>") + "</NoOfContracts>".length() - 1, parseXml.indexOf("</NoOfContracts>")) : "";
		                ECRN = (parseXml.contains("<ECRN>")) ? parseXml.substring(parseXml.indexOf("<ECRN>") + "</ECRN>".length() - 1, parseXml.indexOf("</ECRN>")) : "";
		                BorrowingCustomer = (parseXml.contains("<BorrowingCustomer>"))
		                  ? parseXml.substring(parseXml.indexOf("<BorrowingCustomer>") + "</BorrowingCustomer>".length() - 1, parseXml.indexOf("</BorrowingCustomer>")) : "";


		                Map<String, String> tagValuesMap = new LinkedHashMap<String, String>();
		                tagValuesMap = getTagDataParent_deep(parseXml, tagName, subTagName, subtag_single);

		                Map<String, String> map = tagValuesMap;
		                // String colValue="";
		                for (Map.Entry<String, String> entry : map.entrySet())
		                  {
		                    valueArr = entry.getValue().split("~");
		                    		                    columnName = valueArr[0] + ",CifId,Request_Type,Product_Type,CardType,Wi_Name";
		                    columnValues = valueArr[1] + ",'" + cifId + "','" + returnType + "','" + prod + "','" + subprod + "','" + wi_name + "'";
		                    if (sTableName.equalsIgnoreCase("ng_dao_gr_InternalExpo_CardDetails"))
		                      {
		                        columnName = valueArr[0] + ",Liability_type,Request_Type,FullNm,CifId,RelatedPartyId,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + cust_type + "','" + returnType + "','" + FullNm + "','" + cifId + "','" + RelatedPartyId + "','" + wi_name + "'";
		                        sWhere = "CardEmbossNum = '" + entry.getKey() + "' AND wi_name='" + wi_name + "' And Liability_type ='" + cust_type + "'";
		                        sQry =
		                          "Select count(*) as selectdata from " + sTableName + " where wi_name='" + wi_name + "' And CardEmbossNum = '" + entry.getKey() + "' And Liability_type ='Individual_CIF' ";
		                        //DigitalAO.mLogger.debug( "sQry sQry" + sQry);
		                        if ("Individual_CIF".equalsIgnoreCase(cust_type))
		                          {
		                            companyUpdateQuery =
		                              "Select count(*) as selectdata from " + sTableName + " where wi_name='" + wi_name + "' And CardEmbossNum = '" + entry.getKey() + "' And Liability_type ='Corporate_CIF'";
		                          }
		                        if (parseXml.contains("<LinkedCIFs>"))
		                          {
		                            parseLinkedCif(parseXml, sTableName, cifId, wi_name, entry.getKey(), cust_type, "Card", cabinetName, sessionId, wrapperIP, wrapperPort);
		                          }
		                      }
		                    else if (sTableName.equalsIgnoreCase("ng_dao_gr_InternalExpo_LoanDetails"))
		                      {
		                        columnName = valueArr[0] + ",Liability_type,Request_Type,FullNm,Product_Type,CardType,CifId,RelatedPartyId,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + cust_type + "','" + returnType + "','" + FullNm + "','" + prod + "','" + subprod + "','" + cifId + "','" + RelatedPartyId + "','" + wi_name + "'";
		                        columnName = columnName.replace("OutStandingAmt", "TotalOutStandingAmt");
		                        sWhere = "AgreementId = '" + entry.getKey() + "' AND wi_name='" + wi_name + "' And Liability_type ='" + cust_type + "'";
		                        sQry =
		                          "Select count(*) as selectdata from " + sTableName + " where wi_name='" + wi_name + "' And  AgreementId = '" + entry.getKey() + "' And Liability_type ='Individual_CIF'";
		                        //DigitalAO.mLogger.debug( "sQry  loan sQry" + sQry);
		                        if ("Individual_CIF".equalsIgnoreCase(cust_type))
		                          {
		                            companyUpdateQuery =
		                              "Select count(*) as selectdata from " + sTableName + " where wi_name='" + wi_name + "' And AgreementId = '" + entry.getKey() + "' And Liability_type ='Corporate_CIF'";
		                          }
		                        if (parseXml.contains("<LinkedCIFs>"))
		                          {
		                            parseLinkedCif(parseXml, sTableName, cifId, wi_name, entry.getKey(), cust_type, "Loan", cabinetName, sessionId, wrapperIP, wrapperPort);
		                          }
		                      }
		                    else if (sTableName.equalsIgnoreCase("USR_0_iRBL_ExternalExpo_ChequeDetails"))
		                      {
		                        columnName = valueArr[0] + ",CifId,Request_Type,RelatedPartyId,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + cifId + "','" + returnType + "','" + RelatedPartyId + "','" + wi_name + "'";
		                        sWhere = "Wi_Name='" + wi_name + "' AND Number = '" + entry.getKey() + "'"; // all data received in response will be inserted POLP-11550
		                      }
		                    else if (sTableName.equalsIgnoreCase("USR_0_iRBL_ExternalExpo_LoanDetails"))
		                      {
		                        String History = parseHistoryUtilization(parseXml, entry.getKey(), "LoanDetails", "<History>", "</History>");
		                        History = History.replace("\n", "").replace("\r", "");
		                        String Utilization = parseHistoryUtilization(parseXml, entry.getKey(), "LoanDetails", "<Utilizations24Months>", "</Utilizations24Months>");
		                        Utilization = Utilization.replace("\n", "").replace("\r", "");
		                        DigitalAO.mLogger.debug("inside parseHistoryUtilization" + History);
		                        DigitalAO.mLogger.debug("inside parseHistoryUtilization" + Utilization);
		                        columnName = valueArr[0] + ",Liability_type,Request_Type,FullNm,Product_Type,CardType,CifId,RelatedPartyId,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + cust_type + "','" + returnType + "','" + FullNm + "','" + prod + "','" + subprod + "','" + cifId + "','" + RelatedPartyId + "','" + wi_name + "'";
		                        String columnName_arr[] = columnName.split(",");
		                        String columnValues_arr[] = columnValues.split(",");
		                        for (int arrlen = 0; arrlen < columnName_arr.length; arrlen++)
		                          {
		                            if ("LoanType".equalsIgnoreCase(columnName_arr[arrlen]))
		                              {
		                                DigitalAO.mLogger.debug("inside loan desc tag name" + columnName_arr[arrlen]);
		                                DigitalAO.mLogger.debug("inside loan desc tag value" + columnValues_arr[arrlen]);
		                                String loan_desc = get_loanDesc(columnValues_arr[arrlen], cabinetName, sessionId, wrapperIP, wrapperPort);
		                                columnValues = columnValues.replaceFirst(columnValues_arr[arrlen], loan_desc);

		                              }
		                            if ("History".equalsIgnoreCase(columnName_arr[arrlen]))
		                              {
		                                // DigitalAO.mLogger.debug( "inside loan desc tag name" + columnName_arr[arrlen]);
		                                // DigitalAO.mLogger.debug( "inside loan desc tag value" + columnValues_arr[arrlen]);
		                                //String loan_desc = get_loanDesc(columnValues_arr[arrlen], cabinetName, sessionId, wrapperIP,wrapperPort, appServerType);
		                                columnValues = columnValues.replace(columnValues_arr[arrlen], "'" + History + "'");

		                              }
		                            if ("Utilizations24Months".equalsIgnoreCase(columnName_arr[arrlen]))
		                              {
		                                // DigitalAO.mLogger.debug( "inside loan desc tag name" + columnName_arr[arrlen]);
		                                // DigitalAO.mLogger.debug( "inside loan desc tag value" + columnValues_arr[arrlen]);
		                                //String loan_desc = get_loanDesc(columnValues_arr[arrlen], cabinetName, sessionId, wrapperIP,wrapperPort, appServerType);
		                                columnValues = columnValues.replaceFirst(columnValues_arr[arrlen], "'" + Utilization + "'");

		                              }
		                          }
		                        columnName = columnName.replace("OutStanding Balance", "OutStanding_Balance");
		                        columnName = columnName.replace("LastUpdateDate", "datelastupdated");
		                        columnName = columnName.replace("Total Amount", "Total_Amount");
		                        columnName = columnName.replace("Payments Amount", "Payments_Amount");
		                        columnName = columnName.replace("Overdue Amount", "Overdue_Amount");
		                        DigitalAO.mLogger.debug("inside parseHistoryUtilization" + columnName);
		                        DigitalAO.mLogger.debug("inside parseHistoryUtilization" + columnValues);
		                        //sWhere="Wi_Name='"+parentWiName+"' AND AgreementId = '"+entry.getKey()+"' AND Child_Wi='"+wi_name+"'";
		                        sWhere = "Wi_Name='" + wi_name + "' AND AgreementId = '" + entry.getKey() + "'";
		                      }
		                    else if (sTableName.equalsIgnoreCase("USR_0_iRBL_ExternalExpo_CardDetails"))
		                      {
		                        String History = parseHistoryUtilization(parseXml, entry.getKey(), "CardDetails", "<History>", "</History>");
		                        History = History.replace("\n", "").replace("\r", "");
		                        String Utilization = parseHistoryUtilization(parseXml, entry.getKey(), "CardDetails", "<Utilizations24Months>", "</Utilizations24Months>");
		                        Utilization = Utilization.replace("\n", "").replace("\r", "");
		                        columnName = valueArr[0] + ",Liability_type,Request_Type,FullNm,Product_Type,sub_product_type,CifId,RelatedPartyId,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + cust_type + "','" + returnType + "','" + FullNm + "','" + prod + "','" + subprod + "','" + cifId + "','" + RelatedPartyId + "','" + wi_name + "'";
		                        String columnName_arr[] = columnName.split(",");
		                        String columnValues_arr[] = columnValues.split(",");
		                        for (int arrlen = 0; arrlen < columnName_arr.length; arrlen++)
		                          {
		                            if ("CardType".equalsIgnoreCase(columnName_arr[arrlen]))
		                              {
		                                DigitalAO.mLogger.debug("inside loan desc tag name" + columnName_arr[arrlen]);
		                                DigitalAO.mLogger.debug("inside loan desc tag value" + columnValues_arr[arrlen]);
		                                String loan_desc = get_loanDesc(columnValues_arr[arrlen], cabinetName, sessionId, wrapperIP, wrapperPort);
		                                columnValues = columnValues.replaceFirst(columnValues_arr[arrlen], loan_desc);

		                              }
		                            if ("History".equalsIgnoreCase(columnName_arr[arrlen]))
		                              {
		                                columnValues = columnValues.replace(columnValues_arr[arrlen], "'" + History + "'");

		                              }
		                            if ("Utilizations24Months".equalsIgnoreCase(columnName_arr[arrlen]))
		                              {
		                                columnValues = columnValues.replace(columnValues_arr[arrlen], "'" + Utilization + "'");

		                              }
		                          }
		                        sWhere = "Wi_Name='" + wi_name + "' AND CardEmbossNum = '" + entry.getKey() + "'";
		                       
		                      }
		                    else if (sTableName.equalsIgnoreCase("ng_dao_gr_InternalExpo_Derived"))
		                      {
		                       
		                    	String CBApplicationID = (parseXml.contains("<CBApplicationID>")) ? parseXml.substring(parseXml.indexOf("<CBApplicationID>") + "</CBApplicationID>".length() - 1, parseXml.indexOf("</CBApplicationID>")) : "";
		                    	String FraudFlag = (parseXml.contains("<FraudContractFlag>")) ? parseXml.substring(parseXml.indexOf("<FraudContractFlag>") + "</FraudContractFlag>".length() - 1, parseXml.indexOf("</FraudContractFlag>")) : "";
		                    	columnName = valueArr[0] + ",Range,Request_Type,CifId,FullNm,TotalOutstanding,TotalOverdue,NoOfContracts,ReportURL,ReferenceNo,AECB_Score,RelatedPartyId,CBApplicationID,FraudContractFlag,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + range + "','" + returnType + "','" + cifId + "','" + FullNm + "','" + TotalOutstanding + "','" + TotalOverdue + "','" + NoOfContracts
		                          + "','" + ReportUrl + "','"+ referenceNo + "','" + Aecb_Score + "','" + RelatedPartyId + "','" + CBApplicationID + "','" + FraudFlag + "','" + wi_name + "'";
		                        sWhere = "Wi_Name='" + wi_name + "' AND Request_Type = '" + returnType + "' and RelatedPartyId='" + RelatedPartyId + "'"; //and cifid='" + cifId + "'
		                      }
		                    
		                    else if (sTableName.equalsIgnoreCase("ng_dao_gr_InternalExpo_RecordDestribution"))
		                      {
		                        columnName = valueArr[0] + ",Request_Type,CifId,RelatedPartyId,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + cifId + "','" + returnType + "','" + RelatedPartyId + "','" + wi_name + "'";
		                        sWhere = "Wi_Name='" + wi_name + "' AND ContractType = '" + entry.getKey() + "' AND CifId='" + cifId + "'";
		                      }
		                    
		                    else if (sTableName.equalsIgnoreCase("USR_0_iRBL_ExternalExpo_AccountDetails"))
		                      {
		                        columnName = valueArr[0] + ",CifId,Request_Type,RelatedPartyId,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + cifId + "','" + returnType + "','" + RelatedPartyId + "','" + wi_name + "'";
		                        String columnName_arr[] = columnName.split(",");
		                        String columnValues_arr[] = columnValues.split(",");
		                        for (int arrlen = 0; arrlen < columnName_arr.length; arrlen++)
		                          {
		                            if ("AcctType".equalsIgnoreCase(columnName_arr[arrlen]))
		                              {
		                                DigitalAO.mLogger.debug("inside loan desc tag name" + columnName_arr[arrlen]);
		                                DigitalAO.mLogger.debug("inside loan desc tag value" + columnValues_arr[arrlen]);
		                                String loan_desc = get_loanDesc(columnValues_arr[arrlen], cabinetName, sessionId, wrapperIP, wrapperPort);
		                                columnValues = columnValues.replaceFirst(columnValues_arr[arrlen], loan_desc);
		                                break;
		                              }
		                          }
		                        sWhere = "Wi_Name='" + wi_name + "' AND AcctId = '" + entry.getKey() + "'";//Cif_id removed
		                      }
		                    //Deepak changes done for Service details
		                    else if (sTableName.equalsIgnoreCase("USR_0_iRBL_ExternalExpo_ServicesDetails"))
		                      {
		                        columnName = valueArr[0] + ",CifId,Request_Type,RelatedPartyId,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + cifId + "','" + returnType + "','" + RelatedPartyId + "','" + wi_name  + "'";
		                        String columnName_arr[] = columnName.split(",");
		                        String columnValues_arr[] = columnValues.split(",");

		                        for (int arrlen = 0; arrlen < columnName_arr.length; arrlen++)
		                          {
		                            if ("ServiceName".equalsIgnoreCase(columnName_arr[arrlen]))
		                              {
		                                DigitalAO.mLogger.debug("inside loan desc tag name" + columnName_arr[arrlen]);
		                                DigitalAO.mLogger.debug("inside loan desc tag value" + columnValues_arr[arrlen]);
		                                String loan_desc = get_loanDesc(columnValues_arr[arrlen], cabinetName, sessionId, wrapperIP, wrapperPort);
		                                columnValues = columnValues.replaceFirst(columnValues_arr[arrlen], loan_desc);
		                                break;
		                              }
		                          }
		                        sWhere = "Wi_Name='" + wi_name + "' AND ServiceID = '" + entry.getKey() + "'";
		                      }
		                    //below changes Done to save AccountType in ng_RLOS_CUSTEXPOSE_AcctDetails table on 29th Dec by Disha
		                    else if (sTableName.equalsIgnoreCase("ng_dao_gr_InternalExpo_AcctDetails"))
		                      {
		                        String CreditGrade =
		                          (parseXml.contains("<CreditGrade>")) ? parseXml.substring(parseXml.indexOf("<CreditGrade>") + "</CreditGrade>".length() - 1, parseXml.indexOf("</CreditGrade>")) : "";
		                        //PCASP-2833 
		                        String isDirect = (parseXml.contains("<IsDirect>")) ? parseXml.substring(parseXml.indexOf("<IsDirect>") + "</IsDirect>".length() - 1, parseXml.indexOf("</IsDirect>")) : "";
		                        columnName = valueArr[0] + ",isDirect,Request_Type,CifId,CreditGrade,Account_Type,RelatedPartyId,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + isDirect + "','" + returnType + "','" + cifId + "','" + CreditGrade + "','" + cust_type + "','" + RelatedPartyId + "','" +  wi_name + "'";
		                        sWhere = "Request_Type='" + returnType + "' AND AcctId = '" + entry.getKey() + "' AND wi_name='" + wi_name + "'";// AND Account_Type = '" + cust_type + "'";
		                        String columnName_arr[] = columnName.split(",");
		                        String columnValues_arr[] = columnValues.split(",");
		                        String LimitSactionDate = "";
		                        for (int arrlen = 0; arrlen < columnName_arr.length; arrlen++)
		                          {
		                            if ("LimitSactionDate".equalsIgnoreCase(columnName_arr[arrlen]))
		                              {
		                                DigitalAO.mLogger.debug("inside LimitSactionDate tag name" + columnName_arr[arrlen]);
		                                DigitalAO.mLogger.debug("inside LimitSactionDate value" + columnValues_arr[arrlen]);
		                                LimitSactionDate = columnValues_arr[arrlen];
		                              }
		                            if ("MonthsOnBook".equalsIgnoreCase(columnName_arr[arrlen]))
		                              {
		                                DigitalAO.mLogger.debug("inside MonthsOnBook tag name" + columnName_arr[arrlen]);
		                                DigitalAO.mLogger.debug("inside MonthsOnBook value" + columnValues_arr[arrlen]);
		                                if (!LimitSactionDate.equals(""))
		                                  {
		                                    String MOB = get_Mob_forOD(LimitSactionDate);
		                                    DigitalAO.mLogger.debug("inside MonthsOnBook value" + MOB);
		                                    if (!MOB.equalsIgnoreCase("Invalid"))
		                                      {
		                                        columnValues = columnValues.replace(columnValues_arr[arrlen], "'" + MOB + "'");
		                                      }
		                                  }

		                              }
		                          }
		                        //change by saurabh on 24th Feb for skipping employer accounts to save.
		                        sQry = "Select count(*) as selectdata from NG_RLOS_ALOC_OFFLINE_DATA with(nolock) where CIF_ID ='Nikhil123'";
		                        if (parseXml.contains("<LinkedCIFs>"))
		                          {
		                            parseLinkedCif(parseXml, sTableName, cifId, wi_name, entry.getKey(), cust_type, "Account", cabinetName, sessionId, wrapperIP, wrapperPort);
		                          }
		                        //DigitalAO.mLogger.debug( "sQry  loan sQry" + sQry);    
		                      }
		                    else if (sTableName.equalsIgnoreCase("ng_dao_gr_InternalExpo_InvestmentDetails"))
		                      {

		                        columnName = valueArr[0] + ",CifId,Request_Type,RelatedPartyId,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + cifId + "','" + returnType + "','" + RelatedPartyId + "','" + wi_name  + "'";
		                        sWhere = "Request_Type='" + returnType + "' AND wi_name='" + wi_name + "' and InvestmentID='" + entry.getKey() + "'";

		                      }
		                    //above changes Done to save AccountType in ng_RLOS_CUSTEXPOSE_AcctDetails table on 29th Dec by Disha
		                    //Deepak 22 july 2019 new condition added to save custinfo
		                    else if (sTableName.equalsIgnoreCase("ng_dao_gr_InternalExpo_CustInfo"))
		                      {
		                        String isDirect = (parseXml.contains("<IsDirect>")) ? parseXml.substring(parseXml.indexOf("<IsDirect>") + "</IsDirect>".length() - 1, parseXml.indexOf("</IsDirect>")) : "";
		                        columnName = valueArr[0] + ",isDirect,Request_Type,CifId,RelatedPartyId,Wi_Name";
		                        columnValues = valueArr[1] + ",'" + isDirect +"','" + returnType + "','" + cifId + "','" + RelatedPartyId + "','" + wi_name  + "'";
		                        sWhere = "wi_name='" + wi_name + "' AND Request_Type = '" + returnType + "' AND CifId = '" + cifId + "'";
		                      }
		                    else
		                      {
		                        sWhere = "Request_Type='" + returnType + "' AND wi_name='" + wi_name + "'";
		                      }

		                    strInputXml = DigitalAO_Common.getAPUpdateIpXML(sTableName, columnName, columnValues, sWhere, cabinetName, sessionId);
		                    DigitalAO.mLogger.debug( "strInputXml update for "+sTableName+" table: " + strInputXml);
		                    try
		                      {
		                        
		                        strOutputXml = WFNGExecute(strInputXml, wrapperIP, wrapperPort, 0);;

		                        DigitalAO.mLogger.debug("strOutputXml update for "+sTableName+" table: "+strOutputXml);
		                      }
		                   
		                    catch (Exception ex)
		                      {
		                        DigitalAO.mLogger.debug("Exception update for "+sTableName+" table: " + ex.getMessage());
		                        ex.printStackTrace();
		                      }

		                    tagNameU = "APUpdate_Output";
		                    subTagNameU = "MainCode";
		                    subTagNameU_2 = "Output";
		                    mainCode = getTagValue(strOutputXml, tagNameU, subTagNameU);
		                    row_updated = getTagValue(strOutputXml, tagNameU, subTagNameU_2);
		                    DigitalAO.mLogger.debug("maincode update for "+sTableName+" table:  --> "+mainCode);
		                    DigitalAO.mLogger.debug("row updated update for "+sTableName+" table: --> "+row_updated);
		                    if (!mainCode.equalsIgnoreCase("0") || row_updated.equalsIgnoreCase("0"))
		                      {   DigitalAO.mLogger.debug("sQry sQry sQry");
		                        if (!sQry.equalsIgnoreCase(""))
		                          {
		                            strInputXml = DigitalAO_Common.apSelectWithColumnNames(sQry, cabinetName, sessionId);
		                            try
		                              {
		                                strOutputXml = WFNGExecute(strInputXml, wrapperIP, wrapperPort, 0);
		                              }
		                            
		                            catch (Exception ex)
		                              {
		                                DigitalAO.mLogger.debug("Exception select for "+sTableName+" table sQry sQry sQry: " + ex.getMessage());
		                                ex.printStackTrace();
		                              }
		                            mainCode =
		                              (strOutputXml.contains("<MainCode>")) ? strOutputXml.substring(strOutputXml.indexOf("<MainCode>") + "</MainCode>".length() - 1, strOutputXml.indexOf("</MainCode>")) : "";
		                            DigitalAO.mLogger.debug("maincode select for "+sTableName+" table sQry sQry sQry --> "+mainCode);
		                            selectdata = (strOutputXml.contains("<selectdata>"))
		                              ? strOutputXml.substring(strOutputXml.indexOf("<selectdata>") + "</selectdata>".length() - 1, strOutputXml.indexOf("</selectdata>")) : "";
		                            DigitalAO.mLogger.debug("selectdata select for "+sTableName+" table sQry sQry sQry--> "+selectdata);
		                          }
		                        if (!companyUpdateQuery.equalsIgnoreCase(""))
		                          {
		                            strInputXml = DigitalAO_Common.apSelectWithColumnNames(companyUpdateQuery, cabinetName, sessionId);
		                            try
		                              {
		                            	DigitalAO.mLogger.debug("companyUpdateQuery select for "+sTableName+" table: "+strInputXml);
		                                strOutputXml = WFNGExecute(strInputXml, wrapperIP, wrapperPort, 0);
		                                DigitalAO.mLogger.debug(" companyUpdateQuery select for "+sTableName+" table: "+strOutputXml);
		                              }
		                           
		                            catch (Exception ex)
		                              {
		                                DigitalAO.mLogger.debug("Exception companyUpdateQuery select for "+sTableName+" table: " + ex.getMessage());
		                                ex.printStackTrace();
		                              }

		                            mainCode =
		                              (strOutputXml.contains("<MainCode>")) ? strOutputXml.substring(strOutputXml.indexOf("<MainCode>") + "</MainCode>".length() - 1, strOutputXml.indexOf("</MainCode>")) : "";
		                            DigitalAO.mLogger.debug("maincode companyUpdateQuery select for "+sTableName+" table --> "+mainCode);

		                            companiestobeUpdated = (strOutputXml.contains("<selectdata>"))
		                              ? strOutputXml.substring(strOutputXml.indexOf("<selectdata>") + "</selectdata>".length() - 1, strOutputXml.indexOf("</selectdata>")) : "";
		                            DigitalAO.mLogger.debug("selectdata companyUpdateQuery select for "+sTableName+" table--> "+companiestobeUpdated);

		                            if (Integer.parseInt(companiestobeUpdated) > 0)
		                              {
		                                sWhere = "wi_name='" + wi_name + "' AND CardEmbossNum = '" + entry.getKey() + "' And Liability_type ='Corporate_CIF'";
		                                strInputXml = DigitalAO_Common.getAPUpdateIpXML(sTableName, columnName, columnValues, sWhere, cabinetName, sessionId);
		                                DigitalAO.mLogger.debug( "strInputXml companiestobeUpdated update for "+sTableName+" table: " + strInputXml);
		                                try
		                                  {
		                                    
		                                    strOutputXml = WFNGExecute(strInputXml, wrapperIP, wrapperPort, 0);

		                                    DigitalAO.mLogger.debug("strOutputXml companiestobeUpdated update for "+sTableName+" table: "+strOutputXml);
		                                  }
		                               
		                                catch (Exception ex)
		                                  {
		                                    DigitalAO.mLogger.debug("Exception companiestobeUpdated update for "+sTableName+" table: " + ex.getMessage());
		                                    ex.printStackTrace();
		                                  }

		                                tagNameU = "APUpdate_Output";
		                                subTagNameU = "MainCode";
		                                subTagNameU_2 = "Output";
		                                mainCode = getTagValue(strOutputXml, tagNameU, subTagNameU);
		                               
		                                stopIndividualToInsert = true;
		                              }
		                          }

		                        if (sQry.equalsIgnoreCase("") || (mainCode.equalsIgnoreCase("0") && selectdata.equalsIgnoreCase("0") && !stopIndividualToInsert))
		                          {
		                            strInputXml = DigitalAO_Common.apInsert(cabinetName, sessionId, columnName, columnValues,sTableName);
		                            DigitalAO.mLogger.debug( "strInputXml final insert for "+sTableName+" table:" + strInputXml);
		                            try
		                              {
		                                
		                                strOutputXml = WFNGExecute(strInputXml, wrapperIP, wrapperPort, 0);
		                                    
		                                DigitalAO.mLogger.debug("strOutputXml final insert for "+sTableName+" table: "+strOutputXml);
		                                mainCode = getTagValue(strOutputXml, "APInsert_Output", subTagNameU);
		                                DigitalAO.mLogger.debug("mainCode"+mainCode);
		                                if (!mainCode.equalsIgnoreCase("0"))
		                                  {
		                                    retVal = "false";
		                                    
		                                  }
		                                else
		                                  {
		                                    retVal = "true";
		                                    
		                                  }
		                              }
		                            
		                            catch (Exception ex)
		                              {
		                                DigitalAO.mLogger.debug("Exception strInputXml final insert for "+sTableName+" table: " + ex.getMessage());
		                                ex.printStackTrace();
		                              }
		                          }
		                        else
		                          {
		                            retVal = "true";
		                           
		                          }
		                      }
		                    else
		                      {
		                        retVal = "true";
		                        
		                      }

		                  }
		                DigitalAO.mLogger.debug("return for "+sTableName+" table:finalValue: "+retVal);
		                return retVal;
		              }
		          }
		        catch (Exception e)
		          {
		            DigitalAO.mLogger.debug("Exception occured in commonParseProduct: " + e.getMessage());
		            e.printStackTrace();
		            retVal = "false";
		          }
		        return retVal;
		      }


	

		public static String socketConnection(String cabinetName, String username, String sessionId, String sJtsIp, String iJtsPort, String processInstanceID, String ws_name,
				int connection_timeout, int integrationWaitTime,StringBuilder sInputXML,HashMap<String, String> socketDetailsMap)
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

				DigitalAO.mLogger.debug("userName "+ username);
				DigitalAO.mLogger.debug("SessionId "+ sessionId);

				socketServerIP=socketDetailsMap.get("SocketServerIP");
				DigitalAO.mLogger.debug("SocketServerIP "+ socketServerIP);
				socketServerPort=Integer.parseInt(socketDetailsMap.get("SocketServerPort"));
				DigitalAO.mLogger.debug("SocketServerPort "+ socketServerPort);

		   		if (!("".equalsIgnoreCase(socketServerIP) && socketServerIP == null && socketServerPort==0))
		   		{

	    			socket = new Socket(socketServerIP, socketServerPort);
	    			socket.setSoTimeout(connection_timeout*1000);
	    			out = socket.getOutputStream();
	    			socketInputStream = socket.getInputStream();
	    			dout = new DataOutputStream(out);
	    			din = new DataInputStream(socketInputStream);
	    			DigitalAO.mLogger.debug("Dout " + dout);
	    			DigitalAO.mLogger.debug("Din " + din);

	    			outputResponse = "";

	    			inputRequest = getRequestXML( cabinetName,sessionId ,processInstanceID, ws_name, username, sInputXML);


	    			if (inputRequest != null && inputRequest.length() > 0)
	    			{
	    				int inputRequestLen = inputRequest.getBytes("UTF-16LE").length;
	    				DigitalAO.mLogger.debug("RequestLen: "+inputRequestLen + "");
	    				inputRequest = inputRequestLen + "##8##;" + inputRequest;
	    				DigitalAO.mLogger.debug("InputRequest"+"Input Request Bytes : "+ inputRequest.getBytes("UTF-16LE"));
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
	    				DigitalAO.mLogger.debug("OutputResponse: "+outputResponse);

	    				if(!"".equalsIgnoreCase(outputResponse))
	    					outputResponse = getResponseXML(cabinetName,sJtsIp,iJtsPort,sessionId, processInstanceID,outputResponse,integrationWaitTime );

	    				if(outputResponse.contains("&lt;"))
	    				{
	    					outputResponse=outputResponse.replaceAll("&lt;", "<");
	    					outputResponse=outputResponse.replaceAll("&gt;", ">");
	    				}
	    			}
	    			socket.close();

					outputResponse = outputResponse.replaceAll("</MessageId>","</MessageId><InputMessageId>"+inputMessageID+"</InputMessageId>");

					return outputResponse;

	    	 		}

	    		else
	    		{
	    			DigitalAO.mLogger.debug("SocketServerIp and SocketServerPort is not maintained "+"");
	    			DigitalAO.mLogger.debug("SocketServerIp is not maintained "+	socketServerIP);
	    			DigitalAO.mLogger.debug(" SocketServerPort is not maintained "+	socketServerPort);
	    			return "Socket Details not maintained";
	    		}

			}

			catch (Exception e)
			{
				DigitalAO.mLogger.debug("Exception Occured Mq_connection_CC"+e.getStackTrace());
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
					DigitalAO.mLogger.debug("Final Exception Occured Mq_connection_CC"+e.getStackTrace());
					//printException(e);
				}
			}


		}
				
		
		private static String getResponseXML(String cabinetName,String sJtsIp,String iJtsPort, String sessionId, String processInstanceID,String message_ID, int integrationWaitTime)
		{

			String outputResponseXML="";
			try
			{
				String QueryString = "select OUTPUT_XML from NG_DAO_XMLLOG_HISTORY with (nolock) where MESSAGE_ID ='"+message_ID+"' and WI_NAME = '"+processInstanceID+"'";

				String responseInputXML =DigitalAO_Common.apSelectWithColumnNames(QueryString, cabinetName, sessionId);
				DigitalAO.mLogger.debug("Response APSelect InputXML: "+responseInputXML);

				int Loop_count=0;
				do
				{
					String responseOutputXML=WFNGExecute(responseInputXML,sJtsIp,iJtsPort,1);
					DigitalAO.mLogger.debug("Response APSelect OutputXML: "+responseOutputXML);

				    XMLParser xmlParserSocketDetails= new XMLParser(responseOutputXML);
				    String responseMainCode = xmlParserSocketDetails.getValueOf("MainCode");
				    DigitalAO.mLogger.debug("ResponseMainCode: "+responseMainCode);



				    int responseTotalRecords = Integer.parseInt(xmlParserSocketDetails.getValueOf("TotalRetrieved"));
				    DigitalAO.mLogger.debug("ResponseTotalRecords: "+responseTotalRecords);

				    if (responseMainCode.equals("0") && responseTotalRecords > 0)
					{

						String responseXMLData=xmlParserSocketDetails.getNextValueOf("Record");
						responseXMLData =responseXMLData.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");

		        		XMLParser xmlParserResponseXMLData = new XMLParser(responseXMLData);
		        		
		        		outputResponseXML=xmlParserResponseXMLData.getValueOf("OUTPUT_XML");
		        		

		        		if("".equalsIgnoreCase(outputResponseXML)){
		        			outputResponseXML="Error";
		    			}
		        		break;
					}
				    Loop_count++;
				    Thread.sleep(1000);
				}
				while(Loop_count<integrationWaitTime);
				DigitalAO.mLogger.debug("integrationWaitTime: "+integrationWaitTime);

			}
			catch(Exception e)
			{
				DigitalAO.mLogger.debug("Exception occurred in outputResponseXML" + e.getMessage());
				outputResponseXML="Error";
			}

			return outputResponseXML;

		}
		
		private static String getRequestXML(String cabinetName, String sessionId,
				String processInstanceID, String ws_name, String userName, StringBuilder sInputXML)
		{
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("<APMQPUTGET_Input>");
			strBuff.append("<SessionId>" + sessionId + "</SessionId>");
			strBuff.append("<EngineName>" + cabinetName + "</EngineName>");
			strBuff.append("<XMLHISTORY_TABLENAME>NG_DAO_XMLLOG_HISTORY</XMLHISTORY_TABLENAME>");
			strBuff.append("<WI_NAME>" + processInstanceID + "</WI_NAME>");
			strBuff.append("<WS_NAME>" + ws_name + "</WS_NAME>");
			strBuff.append("<USER_NAME>" + userName + "</USER_NAME>");
			strBuff.append("<MQ_REQUEST_XML>");
			strBuff.append(sInputXML);
			strBuff.append("</MQ_REQUEST_XML>");
			strBuff.append("</APMQPUTGET_Input>");
			DigitalAO.mLogger.debug("GetRequestXML: "+ strBuff.toString());
			return strBuff.toString();
		}
		

		private HashMap<String, String> socketConnectionDetails(String cabinetName, String sJtsIp, String iJtsPort, String sessionID) {
			HashMap<String, String> socketDetailsMap = new HashMap<String, String>();

			try {
				DigitalAO.mLogger.debug("Fetching Socket Connection Details.");
				System.out.println("Fetching Socket Connection Details.");

				String socketDetailsQuery = "SELECT SocketServerIP,SocketServerPort FROM NG_BPM_MQ_TABLE with (nolock) where ProcessName = 'DigitalAO' and CallingSource = 'Utility'";

				String socketDetailsInputXML = DigitalAO_Common.apSelectWithColumnNames(socketDetailsQuery, cabinetName, sessionID);
				DigitalAO.mLogger.debug("Socket Details APSelect InputXML: " + socketDetailsInputXML);

				String socketDetailsOutputXML = WFNGExecute(socketDetailsInputXML, sJtsIp, iJtsPort, 1);
				DigitalAO.mLogger.debug("Socket Details APSelect OutputXML: " + socketDetailsOutputXML);

				XMLParser xmlParserSocketDetails = new XMLParser(socketDetailsOutputXML);
				String socketDetailsMainCode = xmlParserSocketDetails.getValueOf("MainCode");
				DigitalAO.mLogger.debug("SocketDetailsMainCode: " + socketDetailsMainCode);

				int socketDetailsTotalRecords = Integer.parseInt(xmlParserSocketDetails.getValueOf("TotalRetrieved"));
				DigitalAO.mLogger.debug("SocketDetailsTotalRecords: " + socketDetailsTotalRecords);

				if (socketDetailsMainCode.equalsIgnoreCase("0") && socketDetailsTotalRecords > 0) {
					String xmlDataSocketDetails = xmlParserSocketDetails.getNextValueOf("Record");
					xmlDataSocketDetails = xmlDataSocketDetails.replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");

					XMLParser xmlParserSocketDetailsRecord = new XMLParser(xmlDataSocketDetails);

					String socketServerIP = xmlParserSocketDetailsRecord.getValueOf("SocketServerIP");
					DigitalAO.mLogger.debug("SocketServerIP: " + socketServerIP);
					socketDetailsMap.put("SocketServerIP", socketServerIP);

					String socketServerPort = xmlParserSocketDetailsRecord.getValueOf("SocketServerPort");
					DigitalAO.mLogger.debug("SocketServerPort " + socketServerPort);
					socketDetailsMap.put("SocketServerPort", socketServerPort);

					DigitalAO.mLogger.debug("SocketServer Details found.");
					
				}
			} catch (Exception e) {
				DigitalAO.mLogger.debug("Exception in getting Socket Connection Details: " + e.getMessage());
				
			}

			return socketDetailsMap;
		}



		
		
		 public static String parseHistoryUtilization(String Xml, String Agreement_id, String Liability_type, String StartType, String EndType)
	      {
	       
	        String Output_desired = "";

	        try
	          {
	            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	            Document doc = dBuilder.parse(new InputSource(new StringReader(Xml)));
	            doc.getDocumentElement().normalize();
	            String Liabilityid = "";
	            
	            NodeList nList;
	            if ("LoanDetails".equalsIgnoreCase(Liability_type))
	              {
	                nList = doc.getElementsByTagName("LoanDetails");
	                Liabilityid = "AgreementId";
	              }
	            else
	              {
	                nList = doc.getElementsByTagName("CardDetails");
	                Liabilityid = "CardEmbossNum";
	              }

	            DigitalAO.mLogger.debug("Inside parse CIF:: nList.getLength()" + nList.getLength());

	            for (int temp = 0; temp < nList.getLength(); temp++)
	              {
	                Node nNode = nList.item(temp);
	                if (nNode.getNodeType() == Node.ELEMENT_NODE)
	                  {

	                    Element eElement = (Element) nNode;

	                    String Liability_ID = eElement.getElementsByTagName(Liabilityid).item(0).getTextContent();
	                    DigitalAO.mLogger.debug("Inside parse CIF:: AcctId" + Liability_ID);
	                    if (Liability_ID.equalsIgnoreCase(Agreement_id))
	                      {
	                        String Liability_aggregate = nodeToString(nNode);
	                        Output_desired = Liability_aggregate.substring(Liability_aggregate.indexOf(StartType), Liability_aggregate.lastIndexOf(EndType) + EndType.length());

	                      }
	                  }
	              }

	          }
	        catch (Exception ex)
	          {
	            
	            DigitalAO.mLogger.debug("Exception occured in parse history Utilitixation cif : " + ex.getMessage());
	            //ex.printStackTrace();
	          }
	        return Output_desired;
	      }


		 public static String nodeToString(Node node)
	      {
	        StringWriter sw = new StringWriter();
	        try
	          {
	            Transformer t = TransformerFactory.newInstance().newTransformer();
	            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	            t.setOutputProperty(OutputKeys.INDENT, "yes");
	            t.transform(new DOMSource(node), new StreamResult(sw));
	          }
	        catch (TransformerException te)
	          {
	        	DigitalAO.mLogger.debug("nodeToString Transformer Exception");
	          }
	        return sw.toString();
	      }
	 
		 public static String get_Mob_forOD(String LimitSactionDate)
	      {
	        try
	          {
	            LimitSactionDate = LimitSactionDate.replaceAll("'", "");
	            Date Current_date = new Date();
	            Date Old_Date = new SimpleDateFormat("yyyy-MM-dd").parse(LimitSactionDate);
	            int yy = Current_date.getYear() - Old_Date.getYear();
	            int mm = Current_date.getMonth() - Old_Date.getMonth();
	            if (mm < 0)
	              {
	                yy--;
	                mm = 12 - Old_Date.getMonth() + Current_date.getMonth();
	                if (Current_date.getDate() < Old_Date.getDate())
	                  {
	                    mm--;
	                  }
	              }
	            else if (mm == 0 && Current_date.getDate() < Old_Date.getDate())
	              {
	                yy--;
	                mm = 11 - Old_Date.getMonth() + Current_date.getMonth();
	              }
	            else if (mm > 0 && Current_date.getDate() < Old_Date.getDate())
	              {
	                mm--;
	              }
	            else if (Current_date.getDate() - Old_Date.getDate() != 0)
	              {
	                if (mm == 12)
	                  {
	                    yy++;
	                    mm = 0;
	                  }
	              }

	            return String.valueOf((yy * 12) + mm);
	          }
	        catch (Exception ex)
	          {
	        	DigitalAO.mLogger.debug("Exception occured in get_Mob_forOD: " + ex.getMessage());
	            ex.printStackTrace();
	            return "Invalid";
	          }

	      }
		 
		 public static Map<String, String> getTagDataParent_deep(String parseXml, String tagName, String sub_tag, String subtag_single)
	      {

	        Map<String, String> tagValuesMap = new LinkedHashMap<String, String>();
	        InputStream is = new ByteArrayInputStream(parseXml.getBytes());
	        try
	          {;
	            String tag_notused = "BankId,OperationDesc,TxnSummary,#text";

	            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	            Document doc = dBuilder.parse(is);
	            doc.getDocumentElement().normalize();

	            NodeList nList_loan = doc.getElementsByTagName(tagName);
	            for (int i = 0; i < nList_loan.getLength(); i++)
	              {
	                String col_name = "";
	                String col_val = "";
	                NodeList ch_nodeList = nList_loan.item(i).getChildNodes();
	                String id = "";
	                if ("ReturnsDtls".equalsIgnoreCase(tagName))
	                  {
	                    id = ch_nodeList.item(1).getTextContent();
	                  }
	                else if ("SalDetails".equalsIgnoreCase(tagName))
	                  {
	                    id = ch_nodeList.item(0).getTextContent() + i;
	                  }
	                else if ("ServicesDetails".equalsIgnoreCase(tagName))
	                  {
	                    id = ch_nodeList.item(1).getTextContent();
	                  }
	                else if ("InvestmentDetails".equalsIgnoreCase(tagName))
	                  {
	                    id = ch_nodeList.item(1).getTextContent();
	                  }
	                else if ("ChequeDetails".equalsIgnoreCase(tagName))
	                {
	                  id = ch_nodeList.item(1).getTextContent();
	                }
	                else
	                  {
	                    id = ch_nodeList.item(0).getTextContent();
	                  }
	               
	                for (int ch_len = 0; ch_len < ch_nodeList.getLength(); ch_len++)
	                  {
	                    if (sub_tag.contains(ch_nodeList.item(ch_len).getNodeName()))
	                      {
	                        NodeList sub_ch_nodeList = ch_nodeList.item(ch_len).getChildNodes();
	                        if (!sub_ch_nodeList.item(0).getTextContent().equalsIgnoreCase("#text"))
	                          {
	                            if (col_name.equalsIgnoreCase(""))
	                              {
	                                col_name = sub_ch_nodeList.item(0).getTextContent();
	                                col_val = "'" + sub_ch_nodeList.item(1).getTextContent() + "'";
	                              }
	                            else if (!col_name.contains(sub_ch_nodeList.item(0).getTextContent()))
	                              {
	                                col_name = col_name + "," + sub_ch_nodeList.item(0).getTextContent();
	                                col_val = col_val + ",'" + sub_ch_nodeList.item(1).getTextContent() + "'";
	                              }
	                          }

	                      }
	                    else if (tag_notused.contains(ch_nodeList.item(ch_len).getNodeName()))
	                      {
	                       
	                      }
	                    else if (subtag_single.contains(ch_nodeList.item(ch_len).getNodeName()))
	                      {
	                        NodeList sub_ch_nodeList = ch_nodeList.item(ch_len).getChildNodes();
	                        if (!sub_ch_nodeList.item(0).getTextContent().equalsIgnoreCase("#text"))
	                          {
	                            for (int sub_chd_len = 0; sub_chd_len < sub_ch_nodeList.getLength(); sub_chd_len++)
	                              {
	                                if (col_name.equalsIgnoreCase(""))
	                                  {
	                                    col_name = sub_ch_nodeList.item(sub_chd_len).getNodeName();
	                                    col_val = "'" + sub_ch_nodeList.item(sub_chd_len).getTextContent() + "'";
	                                  }
	                                else if (!col_name.contains(sub_ch_nodeList.item(0).getTextContent()))
	                                  {
	                                    col_name = col_name + "," + sub_ch_nodeList.item(sub_chd_len).getNodeName();
	                                    col_val = col_val + ",'" + sub_ch_nodeList.item(sub_chd_len).getTextContent() + "'";
	                                  }
	                              }
	                          }
	                      }
	                    else
	                      {
	                        if (col_name.equalsIgnoreCase(""))
	                          {
	                            col_name = ch_nodeList.item(ch_len).getNodeName();
	                            col_val = "'" + ch_nodeList.item(ch_len).getTextContent() + "'";
	                          }
	                        else if (!col_name.contains(ch_nodeList.item(ch_len).getNodeName()))
	                          {
	                            col_name = col_name + "," + ch_nodeList.item(ch_len).getNodeName();
	                            col_val = col_val + ",'" + ch_nodeList.item(ch_len).getTextContent() + "'";
	                          }

	                      }

	                  }
	                if (!col_name.equalsIgnoreCase(""))
	                  tagValuesMap.put(id, col_name + "~" + col_val);
	              }

	          }
	        catch (Exception e)
	          {
	            DigitalAO.mLogger.debug("Exception occured in getTagDataParent_deep: " + e.getMessage());
	            e.printStackTrace();
	            
	          }
	        finally
	          {
	            try
	              {
	                if (is != null)
	                  {
	                    is.close();
	                    is = null;
	                  }
	              }
	            catch (Exception e)
	              {
	                DigitalAO.mLogger.debug("Exception occured in is close:  " + e.getMessage());
	              }
	          }
	        return tagValuesMap;
	      }

		 public static void parseLinkedCif(String Xml, String TableName, String Main_CIF,String Wi_name, String Agreement_id, String Cust_Type, String Liability_type, String cabinetName,
			      String sessionId, String wrapperIP, String wrapperPort)
			      {
			        DigitalAO.mLogger.debug("Inside parse CIF");
			        

			        try
			          {
			            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			            Document doc = dBuilder.parse(new InputSource(new StringReader(Xml)));
			            doc.getDocumentElement().normalize();
			            String Liabilityid = "";
			            //String ParentTag= doc.getDocumentElement().getNodeName();
			            NodeList nList;
			            if ("Account".equalsIgnoreCase(Liability_type))
			              {
			                nList = doc.getElementsByTagName("AcctDetails");
			                Liabilityid = "AcctId";
			              }
			            else if ("Loan".equalsIgnoreCase(Liability_type))
			              {
			                nList = doc.getElementsByTagName("LoanDetails");
			                Liabilityid = "AgreementId";
			              }
			            else
			              {
			                nList = doc.getElementsByTagName("CardDetails");
			                Liabilityid = "CardEmbossNum";
			              }

			            DigitalAO.mLogger.debug("Inside parse CIF:: nList.getLength()" + nList.getLength());
			            for (int temp = 0; temp < nList.getLength(); temp++)
			              {
			                Node nNode = nList.item(temp);
			                //  DigitalAO.mLogger.debug("\nCurrent Element :" + nNode.getNodeName());

			                if (nNode.getNodeType() == Node.ELEMENT_NODE)
			                  {

			                    Element eElement = (Element) nNode;
			                    String Liability_ID = eElement.getElementsByTagName(Liabilityid).item(0).getTextContent();
			                    DigitalAO.mLogger.debug("Inside parse CIF:: AcctId" + Liability_ID);
			                    if (Liability_ID.equalsIgnoreCase(Agreement_id))
			                      {

			                        NodeList Linked_CIF = eElement.getElementsByTagName("LinkedCIFs");
			                        DigitalAO.mLogger.debug("Inside parse CIF:: Linked_CIF.getLength()" + Linked_CIF.getLength());
			                        for (int temp1 = 0; temp1 < Linked_CIF.getLength(); temp1++)
			                          {
			                            Node node1 = Linked_CIF.item(temp1);
			                            if (node1.getNodeType() == Node.ELEMENT_NODE)
			                              {
			                                Element eElement1 = (Element) node1;
			                                String Linked_CIF1 = eElement1.getElementsByTagName("CIFId").item(0).getTextContent();
			                                String Relation1 = eElement1.getElementsByTagName("RelationType").item(0).getTextContent();
			                                DigitalAO.mLogger.debug("Inside parse CIF:: Linked_CIF" + Linked_CIF1);
			                                DigitalAO.mLogger.debug("Inside parse CIF:: Relation" + Relation1);

			                                String SQuery = "select count(wi_name) as Select_Count from USR_0_iRBL_InternalExpo_LinkedICF where Linked_CIFs='" + Linked_CIF1 + "' and Relation='" + Relation1
			                                  + "' and wi_name='" + Wi_name + "' and Main_Cif='" + Main_CIF + "' and AgreementId='" + Agreement_id + "'";
			                                String strInputXml = DigitalAO_Common.apSelectWithColumnNames(SQuery, cabinetName, sessionId);
			                                String strOutputXml = "";
			                                DigitalAO.mLogger.debug("Inside parse CIF:: ExecuteQuery_APSelect" + strInputXml);
			                                try
			                                  {
			                                    
			                                    strOutputXml = WFNGExecute(strInputXml, wrapperIP, wrapperPort, 0);
			                                    
			                                    DigitalAO.mLogger.debug("Inside parse CIF:: ExecuteQuery_APSelect output" + strOutputXml);
			                                  }
			                                catch (Exception ex)
			                                  {
			                                    DigitalAO.mLogger.debug("Exception occured in commonParseProduct: " + ex.getMessage());
			                                    ex.printStackTrace();
			                                  }
			                                String mainCode = (strOutputXml.contains("<MainCode>"))
			                                  ? strOutputXml.substring(strOutputXml.indexOf("<MainCode>") + "</MainCode>".length() - 1, strOutputXml.indexOf("</MainCode>")) : "";
			                                
			                                DigitalAO.mLogger.debug("Inside parse CIF select mainCode --> " + mainCode);
			                                if ("0".equalsIgnoreCase(mainCode))
			                                  {
			                                    String selectdata = (strOutputXml.contains("<Select_Count>"))
			                                      ? strOutputXml.substring(strOutputXml.indexOf("<Select_Count>") + "</Select_Count>".length() - 1, strOutputXml.indexOf("</Select_Count>")) : "";
			                                    DigitalAO.mLogger.debug("Inside parse CIF select selectdata --> " + selectdata);
			                                    int totalretrieved = Integer.parseInt(selectdata);
			                                    if (totalretrieved == 0)
			                                      {
			                                        
			                                        String sTableName = "USR_0_iRBL_InternalExpo_LinkedICF";
			                                        String columnName = "Wi_name,Linked_CIFs,Relation,AgreementId,Main_Cif,Liability_Type,Cust_Type";
			                                        String columnValues =
			                                          "'" + Wi_name + "','" + Linked_CIF1 + "','" + Relation1 + "','" + Agreement_id + "','" + Main_CIF + "','" + Liability_type + "','" + Cust_Type + "'";
			                                        strInputXml = DigitalAO_Common.apInsert(cabinetName, sessionId, columnName, columnValues,sTableName);
			                                        
			                                        DigitalAO.mLogger.debug(" Parse linked cif  strInputXml" + strInputXml);
			                                        try
			                                          {
			                                            
			                                            strOutputXml = WFNGExecute(strInputXml, wrapperIP, wrapperPort, 0);
			                                            DigitalAO.mLogger.debug(" Parse linked cif  strOutputXml" + strOutputXml);
			                                            //
			                                            mainCode = getTagValue(strOutputXml, "APInsert_Output", "MainCode");


			                                          }
			                                        catch (Exception ex)
			                                          {
			                                            DigitalAO.mLogger.debug("Exception occured in parseCIF: " + ex.getMessage());
			                                            ex.printStackTrace();
			                                          }
			                                      }
			                                  }
			                               
			                              }
			                          }
			                      }
			                  }
			              }
			          }
			        catch (Exception ex)
			          {
			            DigitalAO.mLogger.debug("Exception occured in parse linked cif : " + ex.getMessage());
			            ex.printStackTrace();
			          }
			      }

		    public static String get_loanDesc(String loan_code, String cabinetName, String sessionId, String wrapperIP, String wrapperPort)
		      {
		        String loan_desc = "";
		        try
		          {
		            String str_Loandesc = "select Description from NG_MASTER_contract_type with(nolock) where code = '"+loan_code.replace("'", "")+"'";
		            String params = "code==" + loan_code.replace("'", "");
		            String strInputXml = DigitalAO_Common.apSelectWithColumnNames(str_Loandesc, cabinetName, sessionId);//(str_Loandesc, params, cabinetName, sessionId);
		            
		            String strOutputXml = WFNGExecute(strInputXml, wrapperIP, wrapperPort, 0);
		            DigitalAO.mLogger.debug("inside get_loanDesc strOutputXml:  " + strOutputXml);
		            String Maincode = strOutputXml.substring(strOutputXml.indexOf("<MainCode>") + "</MainCode>".length() - 1, strOutputXml.indexOf("</MainCode>"));
		            if ("0".equalsIgnoreCase(Maincode))
		              {
		                loan_desc = strOutputXml.substring(strOutputXml.indexOf("<Description>") + "</Description>".length() - 1, strOutputXml.indexOf("</Description>"));
		              }
		            else
		              {
		                loan_desc = loan_code;
		              }
		          }
		        catch (Exception e)
		          {
		            DigitalAO.mLogger.debug("Exception occured in get_loanDesc:  " + e.getMessage());
		            loan_desc = loan_code;
		          }
		        return "'" + loan_desc + "'";
		      }

		    public static String getTagValue(String parseXml, String tagName, String subTagName)
		      {
		        
		        String[] valueArr = null;
		        String mainCodeValue = "";


		        try
		          {
		            Map<Integer, String> tagValuesMap = new LinkedHashMap<Integer, String>();
		            tagValuesMap = getTagDataParent(parseXml, tagName, subTagName);

		            Map<Integer, String> map = tagValuesMap;
		            for (Map.Entry<Integer, String> entry : map.entrySet())
		              {
		                valueArr = entry.getValue().split("~");
		                //WriteLog( "tag values" + entry.getValue());
		                mainCodeValue = valueArr[1];
		                //WriteLog( "mainCodeValue" + mainCodeValue);
		              }
		          }
		        catch (Exception e)
		          {
		            DigitalAO.mLogger.debug("Exception occured getTagValue: " + e.getMessage());
		            e.printStackTrace();
		          }
		        return mainCodeValue;
		      }

		    public static Map<Integer, String> getTagDataParent(String parseXml, String tagName, String subTagName)
		      {
		        Map<Integer, String> tagValuesMap = new LinkedHashMap<Integer, String>();
		        InputStream is = new ByteArrayInputStream(parseXml.getBytes());
		        try
		          {
		            
		            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		            Document doc = dBuilder.parse(is);
		            doc.getDocumentElement().normalize();

		            NodeList nList = doc.getElementsByTagName(tagName);

		            String[] values = subTagName.split(",");
		            String value = "";
		            String subTagDerivedvalue = "";
		            for (int temp = 0; temp < nList.getLength(); temp++)
		              {
		                Node nNode = nList.item(temp);
		                if (nNode.getNodeType() == Node.ELEMENT_NODE)
		                  {
		                    Element eElement = (Element) nNode;
		                    Node uNode = eElement.getParentNode();

		                    for (int j = 0; j < values.length; j++)
		                      {
		                        if (eElement.getElementsByTagName(values[j]).item(0) != null)
		                          {
		                            value = value + "," + eElement.getElementsByTagName(values[j]).item(0).getTextContent();
		                            subTagDerivedvalue = subTagDerivedvalue + "," + values[j];
		                          }

		                      }
		                    value = value.substring(1, value.length());
		                    subTagDerivedvalue = subTagDerivedvalue.substring(1, subTagDerivedvalue.length());

		                    Node nNode_c = doc.getElementsByTagName(uNode.getNodeName()).item(temp);
		                    Element eElement_agg = (Element) nNode_c;
		                    String id_val = "";
		                    if (uNode.getNodeName().equalsIgnoreCase("LoanDetails"))
		                      {
		                        id_val = eElement_agg.getElementsByTagName("AgreementId").item(0).getTextContent();
		                      }
		                    else if (uNode.getNodeName().equalsIgnoreCase("CardDetails"))
		                      {
		                        id_val = eElement_agg.getElementsByTagName("CardEmbossNum").item(0).getTextContent();
		                      }
		                    else if (uNode.getNodeName().equalsIgnoreCase("AcctDetails"))
		                      {
		                        id_val = eElement_agg.getElementsByTagName("AcctId").item(0).getTextContent();
		                      }
		                    else
		                      {
		                        id_val = "";
		                      }

		                    tagValuesMap.put(temp + 1, subTagDerivedvalue + "~" + value + "~" + uNode.getNodeName() + "~" + id_val);
		                    value = "";
		                    subTagDerivedvalue = "";
		                  }
		              }

		          }
		        catch (Exception e)
		          {
		            DigitalAO.mLogger.debug("Exception occured in getTagDataParent" + e.getMessage());
		            e.printStackTrace();
		            //WriteLog("Exception occured in getTagDataParent method:  "+e.getMessage());
		          }
		        finally
		          {
		            try
		              {
		                if (is != null)
		                  {
		                    is.close();
		                    is = null;
		                  }
		              }
		            catch (Exception e)
		              {
		                DigitalAO.mLogger.debug("Exception occured in is close:  " + e.getMessage());
		              }
		          }
		        return tagValuesMap;
		      }

		    protected static String WFNGExecute(String ipXML, String jtsServerIP, String serverPort, int flag) throws IOException {
				try {
					DigitalAO.mLogger.info("WFNGExecute() : " + ipXML + " - " + jtsServerIP + " - " + serverPort + " - " + flag);
					if (serverPort.startsWith("33")) {
						DigitalAO.mLogger.info("Inside if WFNGExecute() :");
						return WFCallBroker.execute(ipXML, jtsServerIP, Integer.parseInt(serverPort), 1);
					} else {
						DigitalAO.mLogger.info("Inside else WFNGExecute() :");
						return ngEjbClientExposure.makeCall(jtsServerIP, serverPort, "WebSphere", ipXML);
					}
					//
				} catch (Exception e) {
					DigitalAO.mLogger.info("Exception Occured in WF NG Execute : " + e.getMessage());
					return "Error";
				}
		
			}
			

}
