package com.newgen.iforms.user;

import com.newgen.iforms.custom.IFormReference;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
	
public class DigitalAO_DecTechCall extends DigitalAO_Common 
{		
		LinkedHashMap<String,String> executeXMLMapMain = new LinkedHashMap<String,String>();
		public static String XMLLOG_HISTORY="NG_iRBL_XMLLOG_HISTORY";

	public String onevent(IFormReference iformObj,String control,String StringData) 
	{
		String wiName=getWorkitemName(iformObj);
		String WSNAME=getActivityName(iformObj);
		String returnValue = "";
		String MQ_response="";
		
		if(control.equals("DecTechCall"))
		{
			MQ_response = MQ_connection_response(iformObj,control,StringData);
			
			if(MQ_response.indexOf("<MessageStatus>")!=-1)
				returnValue = MQ_response.substring(MQ_response.indexOf("<MessageStatus>")+"</MessageStatus>".length()-1,MQ_response.indexOf("</MessageStatus>"));
			
			if(MQ_response.contains("INVALID SESSION"))
				returnValue = "INVALID SESSION";
			
			if("Success".equalsIgnoreCase(returnValue))
				returnValue = parseDectechResponse(MQ_response, iformObj, control, StringData);
			
		}
		
        return returnValue;
    }	
	
	public static String printException(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exception = sw.toString();
		return exception;

	}
		
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
		   CharacterData cd = (CharacterData) child;
		   return cd.getData();
		}
		return "NO_DATA";
	}
	
	public String MQ_connection_response(IFormReference iformObj,String control,String Data) 
	{
		
	DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Inside MQ_connection_response function");
	final IFormReference iFormOBJECT;
	final WDGeneralData wdgeneralObj;	
	Socket socket = null;
	OutputStream out = null;
	InputStream socketInputStream = null;
	DataOutputStream dout = null;
	DataInputStream din = null;
	String mqOutputResponse = null;
	String mqOutputResponse1 = null;
	String mqInputRequest = null;	
	String cabinetName = getCabinetName(iformObj);
	String wi_name = getWorkitemName(iformObj);
	String ws_name = getActivityName(iformObj);
	String sessionID = getSessionId(iformObj);
	String userName = getUserName(iformObj);
	String socketServerIP;
	int socketServerPort;
	wdgeneralObj = iformObj.getObjGeneralData();
	sessionID = wdgeneralObj.getM_strDMSSessionId();
	String CIFNumber="";	
	String CallName="";
	
	if(control.equals("DecTechCall"))
	{
		java.util.Date d1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
		String DateExtra2 = sdf1.format(d1)+"+04:00";
		
		DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Inside DecTechCall control--");
		
		CallName="DECTECH";
		DigitalAO.mLogger.debug("DECTECH Call - WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj));
		StringBuilder finalXml = new StringBuilder("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
			  "<soap:Header>\n"+
			   "<ServiceId>CallProcessManager</ServiceId>\n"+
			   "<ServiceType>ProductEligibility</ServiceType>\n"+
			   "<ServiceProviderId>DECTECH</ServiceProviderId>\n"+
			   "<ServiceChannelId>BPM</ServiceChannelId>\n"+
			   "<RequestID>BPMTEST</RequestID>\n"+
			   "<TimeStampyyyymmddhhmmsss>"+DateExtra2+"</TimeStampyyyymmddhhmmsss>\n"+
			   "<RequestLifeCycleStage>CallProcessManagerRequest</RequestLifeCycleStage>\n"+
			   "<MessageStatus>Success</MessageStatus>\n"+
			"</soap:Header>\n"+
			  "<soap:Body>\n"+
			    "<CallProcessManager xmlns=\"http://tempuri.org/\">\n"+
			      "<applicationXML>\n"+
			      DecTechInputBodyXml(iformObj,control,Data)+
				  "</applicationXML> \n"+
				"</CallProcessManager>\n"+
			  "</soap:Body>\n"+
			"</soap:Envelope>");
	mqInputRequest = getMQInputXML(sessionID, cabinetName,wi_name, ws_name, userName, finalXml);
	DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", mqInputRequest for DecTech call" + mqInputRequest);
	}
	
	try {
		
		DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", userName "+ userName);
		DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", sessionID "+ sessionID);
		
		String sMQuery = "SELECT SocketServerIP,SocketServerPort FROM NG_BPM_MQ_TABLE with (nolock) where ProcessName = 'iRBL' and CallingSource = 'Form'";
		List<List<String>> outputMQXML = iformObj.getDataFromDB(sMQuery);
		//CreditCard.mLogger.info("$$outputgGridtXML "+ "sMQuery " + sMQuery);
		if (!outputMQXML.isEmpty()) {
			//CreditCard.mLogger.info("$$outputgGridtXML "+ outputMQXML.get(0).get(0) + "," + outputMQXML.get(0).get(1));
			socketServerIP = outputMQXML.get(0).get(0);
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", socketServerIP " + socketServerIP);
			socketServerPort = Integer.parseInt(outputMQXML.get(0).get(1));
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", socketServerPort " + socketServerPort);
			if (!("".equalsIgnoreCase(socketServerIP) && socketServerIP == null && socketServerPort==0)) {
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Inside serverIP Port " + socketServerPort+ "-socketServerIP-"+socketServerIP);
				socket = new Socket(socketServerIP, socketServerPort);
				//new Code added by Deepak to set connection timeout
				int connection_timeout=60;
					try{
						connection_timeout=70;
					}
					catch(Exception e){
						connection_timeout=60;
					}
					
				socket.setSoTimeout(connection_timeout*1000);
				out = socket.getOutputStream();
				socketInputStream = socket.getInputStream();
				dout = new DataOutputStream(out);
				din = new DataInputStream(socketInputStream);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", dout " + dout);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", din " + din);
				mqOutputResponse = "";
				
		
				if (mqInputRequest != null && mqInputRequest.length() > 0) {
					int outPut_len = mqInputRequest.getBytes("UTF-16LE").length;
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Final XML output len: "+outPut_len + "");
					mqInputRequest = outPut_len + "##8##;" + mqInputRequest;
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", MqInputRequest"+"Input Request Bytes : "+ mqInputRequest.getBytes("UTF-16LE"));
					dout.write(mqInputRequest.getBytes("UTF-16LE"));dout.flush();
				}
				byte[] readBuffer = new byte[500];
				int num = din.read(readBuffer);
				if (num > 0) {
		
					byte[] arrayBytes = new byte[num];
					System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
					mqOutputResponse = mqOutputResponse+ new String(arrayBytes, "UTF-16LE");
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", mqOutputResponse/message ID :  "+mqOutputResponse);
					
					mqOutputResponse = getOutWtthMessageID("DECTECH",iformObj,mqOutputResponse);
											
					if(mqOutputResponse.contains("&lt;")){
						mqOutputResponse=mqOutputResponse.replaceAll("&lt;", "<");
						mqOutputResponse=mqOutputResponse.replaceAll("&gt;", ">");
					}
				}
				socket.close();
				return mqOutputResponse;
				
			} else {
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", SocketServerIp and SocketServerPort is not maintained "+"");
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", SocketServerIp is not maintained "+	socketServerIP);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+",  SocketServerPort is not maintained "+	socketServerPort);
				return "MQ details not maintained";
			}
		} else {
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", SOcket details are not maintained in NG_BPM_MQ_TABLE table"+"");
			return "MQ details not maintained";
		}
		
		} catch (Exception e) {
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Exception Occured Mq_connection_CC"+e.getStackTrace());
		return "";
		}
	finally{
			try{
				if(out != null){
					
					out.close();
					out=null;
					}
				if(socketInputStream != null){
					
					socketInputStream.close();
					socketInputStream=null;
					}
				if(dout != null){
					
					dout.close();
					dout=null;
					}
				if(din != null){
					
					din.close();
					din=null;
					}
				if(socket != null){
					if(!socket.isClosed()){
						socket.close();
					}
					socket=null;
				}
			}catch(Exception e)
			{
				
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Final Exception Occured Mq_connection_CC"+e.getStackTrace());
				
			}
		}
	}
	
	
	private static String getMQInputXML(String sessionID, String cabinetName,
			String wi_name, String ws_name, String userName,
			StringBuilder final_xml) {
		//FormContext.getCurrentInstance().getFormConfig();
		DigitalAO.mLogger.debug("inside getMQInputXML function");
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("<APMQPUTGET_Input>");
		strBuff.append("<SessionId>" + sessionID + "</SessionId>");
		strBuff.append("<EngineName>" + cabinetName + "</EngineName>");
		strBuff.append("<XMLHISTORY_TABLENAME>"+XMLLOG_HISTORY+"</XMLHISTORY_TABLENAME>");
		strBuff.append("<WI_NAME>" + wi_name + "</WI_NAME>");
		strBuff.append("<WS_NAME>" + ws_name + "</WS_NAME>");
		strBuff.append("<USER_NAME>" + userName + "</USER_NAME>");
		strBuff.append("<MQ_REQUEST_XML>");
		strBuff.append(final_xml);
		strBuff.append("</MQ_REQUEST_XML>");
		strBuff.append("</APMQPUTGET_Input>");
		return strBuff.toString();
	}
		
	public String getOutWtthMessageID(String callName,IFormReference iformObj,String message_ID){
		String outputxml="";
		try{
			DigitalAO.mLogger.debug("getOutWtthMessageID - callName :"+callName);
			
			String wi_name = getWorkitemName(iformObj);
			String str_query = "select OUTPUT_XML from "+ XMLLOG_HISTORY +" with (nolock) where MESSAGE_ID ='"+message_ID+"' and WI_NAME = '"+wi_name+"'";
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", inside getOutWtthMessageID str_query: "+ str_query);
			List<List<String>> result=iformObj.getDataFromDB(str_query);
			//below code added by nikhil 18/10 for Connection timeout
			String Integration_timeOut="100";
			int Loop_wait_count=10;
			try
			{
				Loop_wait_count=Integer.parseInt(Integration_timeOut);
			}
			catch(Exception ex)
			{
				Loop_wait_count=10;
			}
		
			for(int Loop_count=0;Loop_count<Loop_wait_count;Loop_count++){
				if(result.size()>0){
					outputxml = result.get(0).get(0);
					break;
				}
				else{
					Thread.sleep(1000);
					result=iformObj.getDataFromDB(str_query);
				}
			}
			
			if("".equalsIgnoreCase(outputxml)){
				outputxml="Error";
			}
			DigitalAO.mLogger.debug("This is output xml from DB");
			String outputxmlMasked = outputxml;
			/*iRBL.mLogger.debug("The output XML is "+outputxml);
			outputxmlMasked = maskXmlogBasedOnCallType(outputxmlMasked,callName);    
			iRBL.mLogger.debug("Masked output XML is "+outputxmlMasked);*/
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", getOutWtthMessageID" + outputxmlMasked);				
		}
		catch(Exception e){
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Exception BTurred in getOutWtthMessageID" + e.getMessage());
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Exception BTurred in getOutWtthMessageID" + e.getStackTrace());
			outputxml="Error";
		}
		return outputxml;
	}
	
	public String maskXmlogBasedOnCallType(String outputxmlMasked, String callType)
	{
		String Tags = "";
		if (callType.equalsIgnoreCase("CUSTOMER_DETAILS"))
		{
			outputxmlMasked = outputxmlMasked.replace("("," ").replace(")"," ").replace("@"," ").replace("+"," ").replace("&amp;/"," ").replace("&amp; /"," ").replace("."," ").replace(","," ");
			Tags = "<ACCNumber>~,~<AccountName>~,~<ECRNumber>~,~<DOB>~,~<MothersName>~,~<IBANNumber>~,~<DocId>~,~<DocExpDt>~,~<DocIssDate>~,~<PassportNum>~,~<MotherMaidenName>~,~<LinkedDebitCardNumber>~,~<FirstName>~,~<MiddleName>~,~<LastName>~,~<FullName>~,~<ARMCode>~,~<ARMName>~,~<PhnCountryCode>~,~<PhnLocalCode>~,~<PhoneNo>~,~<EmailID>~,~<CustomerName>~,~<CustomerMobileNumber>~,~<PrimaryEmailId>~,~<Fax>~,~<AddressType>~,~<AddrLine1>~,~<AddrLine2>~,~<AddrLine3>~,~<AddrLine4>~,~<POBox>~,~<City>~,~<Country>~,~<AddressLine1>~,~<AddressLine2>~,~<AddressLine3>~,~<AddressLine4>~,~<CityCode>~,~<State>~,~<CountryCode>~,~<Nationality>~,~<ResidentCountry>~,~<PrimaryContactName>~,~<PrimaryContactNum>~,~<SecondaryContactName>~,~<SecondaryContactNum>";
			
		}

			else if (callType.equalsIgnoreCase("ACCOUNT_SUMMARY"))
		{
			outputxmlMasked = outputxmlMasked.replace("("," ").replace(")"," ").replace("@"," ").replace("+"," ").replace("&amp;/"," ").replace("&amp; /"," ").replace("."," ").replace(","," ");
			Tags = "<Acid>~,~<Foracid>~,~<NicName>~,~<AccountName>~,~<AcctBal>~,~<LoanAmtAED>~,~<AcctOpnDt>~,~<MaturityAmt>~,~<EffAvailableBal>~,~<EquivalentAmt>~,~<LedgerBalanceinAED>~,~<LedgerBalance>";
		}
		else if (callType.equalsIgnoreCase("SIGNATURE_DETAILS"))
		{
			outputxmlMasked = outputxmlMasked.replace("&amp;/"," ").replace("&amp; /"," ").replace("."," ").replace(","," ");
			Tags = "<CustomerName>";
		}
		if (!Tags.equalsIgnoreCase(""))
		{
	    	String Tag[] = Tags.split("~,~");
	    	for(int i=0;i<Tag.length;i++)
	    	{
	    		//outputxmlMasked = maskXmlTags(outputxmlMasked,Tag[i]);
	    	}
		}
    	return outputxmlMasked;
	}
	
	public String DecTechInputBodyXml(IFormReference iformObj,String control,String Data)
	{
		java.util.Date d1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String AppDate = sdf1.format(d1);
		
		String ApplicationDetailsXML = "<Application><Channel>BBG</Channel><CallType>PM</CallType><ApplicationNumber>"+getWorkitemName(iformObj)+"</ApplicationNumber><Request_From>BPM</Request_From><application_date>"+AppDate+"</application_date></Application>";
		
		String BasicDetailsXML = DecTechBasicDetailsXML(iformObj, control, Data);
		String ProductAttrXML = DecTechProductAttrXML(iformObj, control, Data);
		String CompanyDetailsXML = DecTechCompanyDetailsXML(iformObj, control, Data);
		String CompanyStakeholdersXML = DecTechCompanyStakeholdersXML(iformObj, control, Data);
		String BlacklistNegativeDetailsXML = DecTechBlacklistNegativeDetailsXML(iformObj, control, Data);
		String AccountDetailsXML = DecTechAccountDetailsXML(iformObj, control, Data);
		String TransactionDetailsXML = DecTechTransactionDetailsXML(iformObj, control, Data);
		String AssestDetailsXML = DecTechAssetDetailsXML(iformObj, control, Data);
		String GuarantorsXML = DecTechCompanyGuarantorsXML(iformObj, control, Data);
		String InternalBureauDataXML = DecTechInternalBureauDataXML(iformObj, control, Data);
		String ExternalBureauDataXML = DecTechExternalBureauDataXML(iformObj, control, Data);		
		
		String DecTechBody = ApplicationDetailsXML+"\n"+BasicDetailsXML+"\n"+ProductAttrXML+"\n"+CompanyDetailsXML+"\n"+CompanyStakeholdersXML+"\n"+BlacklistNegativeDetailsXML+"\n"+AccountDetailsXML+"\n"+TransactionDetailsXML+"\n"+AssestDetailsXML+"\n"+GuarantorsXML+"\n"+InternalBureauDataXML+"\n"+ExternalBureauDataXML;
		return "<![CDATA[<ProcessManagerRequest>"+DecTechBody+"</ProcessManagerRequest>]]>";
	}
	
	public String DecTechBasicDetailsXML(IFormReference iformObj,String control,String Data)
	{
		String BasicDetailsXML = "<Basic_Details><applicant_id></applicant_id><Name></Name><Trade_License_Number></Trade_License_Number><Trade_License_Issue_Date></Trade_License_Issue_Date><Trade_License_Expiry_Date></Trade_License_Expiry_Date><Account_Number></Account_Number><Agreement_Number></Agreement_Number><Sourcing_ID_Code></Sourcing_ID_Code></Basic_Details>";
		
		StringBuffer BasicDetailsB = new StringBuffer(BasicDetailsXML);
		
		if(!"".equalsIgnoreCase(getControlValue("CIF_NUMBER",iformObj).trim()))
			BasicDetailsB = BasicDetailsB.insert(BasicDetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),getControlValue("CIF_NUMBER",iformObj).trim());
		else
			BasicDetailsB = BasicDetailsB.delete(BasicDetailsB.indexOf("<applicant_id>"), BasicDetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("COMPANY_NAME",iformObj).trim()))
			BasicDetailsB = BasicDetailsB.insert(BasicDetailsB.indexOf("<Name>")+"<Name>".length(),getControlValue("COMPANY_NAME",iformObj).trim());
		else
			BasicDetailsB = BasicDetailsB.delete(BasicDetailsB.indexOf("<Name>"), BasicDetailsB.indexOf("</Name>")+"</Name>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("TL_NUMBER",iformObj).trim()))
			BasicDetailsB = BasicDetailsB.insert(BasicDetailsB.indexOf("<Trade_License_Number>")+"<Trade_License_Number>".length(),getControlValue("TL_NUMBER",iformObj).trim());
		else
			BasicDetailsB = BasicDetailsB.delete(BasicDetailsB.indexOf("<Trade_License_Number>"), BasicDetailsB.indexOf("</Trade_License_Number>")+"</Trade_License_Number>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("TLISSUEDATE",iformObj).trim()))
		{
			String TLIssue [] = getControlValue("TLISSUEDATE",iformObj).trim().split("/");
			String TLIssDate = TLIssue[2]+"-"+TLIssue[1]+"-"+TLIssue[0];
			BasicDetailsB = BasicDetailsB.insert(BasicDetailsB.indexOf("<Trade_License_Issue_Date>")+"<Trade_License_Issue_Date>".length(),TLIssDate);
		}else
			BasicDetailsB = BasicDetailsB.delete(BasicDetailsB.indexOf("<Trade_License_Issue_Date>"), BasicDetailsB.indexOf("</Trade_License_Issue_Date>")+"</Trade_License_Issue_Date>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("TL_VALID_TILL",iformObj).trim()))
		{	
			String TLValidTill [] = getControlValue("TL_VALID_TILL",iformObj).trim().split("/");
			String TLValidTillDt = TLValidTill[2]+"-"+TLValidTill[1]+"-"+TLValidTill[0];
			BasicDetailsB = BasicDetailsB.insert(BasicDetailsB.indexOf("<Trade_License_Expiry_Date>")+"<Trade_License_Expiry_Date>".length(),TLValidTillDt);
		
		}
		else
			BasicDetailsB = BasicDetailsB.delete(BasicDetailsB.indexOf("<Trade_License_Expiry_Date>"), BasicDetailsB.indexOf("</Trade_License_Expiry_Date>")+"</Trade_License_Expiry_Date>".length());
		
		String AccNo = "";
		List<List<String>> IndstryDtls = iformObj.getDataFromDB("SELECT top 1 AcctId FROM USR_0_iRBL_InternalExpo_AcctDetails with(nolock) WHERE AcctStat = 'ACTIVE' AND AcctType = 'CURRENT ACCOUNT' AND Wi_Name = '"+getWorkitemName(iformObj)+"'");
		for (List<String> row : IndstryDtls) {
			if (!row.get(0).equalsIgnoreCase(""))
			{
				AccNo = row.get(0).trim();
			}
		}
		
		if(!"".equalsIgnoreCase(AccNo))
			BasicDetailsB = BasicDetailsB.insert(BasicDetailsB.indexOf("<Account_Number>")+"<Account_Number>".length(),AccNo);
		else
			BasicDetailsB = BasicDetailsB.delete(BasicDetailsB.indexOf("<Account_Number>"), BasicDetailsB.indexOf("</Account_Number>")+"</Account_Number>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("AGREEMENT_NUMBER",iformObj).trim()))
			BasicDetailsB = BasicDetailsB.insert(BasicDetailsB.indexOf("<Agreement_Number>")+"<Agreement_Number>".length(),getControlValue("AGREEMENT_NUMBER",iformObj).trim());
		else
			BasicDetailsB = BasicDetailsB.delete(BasicDetailsB.indexOf("<Agreement_Number>"), BasicDetailsB.indexOf("</Agreement_Number>")+"</Agreement_Number>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("RO",iformObj).trim()))
			BasicDetailsB = BasicDetailsB.insert(BasicDetailsB.indexOf("<Sourcing_ID_Code>")+"<Sourcing_ID_Code>".length(),getControlValue("RO",iformObj).trim());
		else
			BasicDetailsB = BasicDetailsB.delete(BasicDetailsB.indexOf("<Sourcing_ID_Code>"), BasicDetailsB.indexOf("</Sourcing_ID_Code>")+"</Sourcing_ID_Code>".length());
		
		BasicDetailsXML = BasicDetailsB.toString();
		return BasicDetailsXML;
	}

	public String DecTechProductAttrXML(IFormReference iformObj,String control,String Data)
	{
		String ProductAttrXML= "<Product_Attribute><applicant_id></applicant_id><Loan_Type></Loan_Type><Type_of_Finance></Type_of_Finance><Cust_type></Cust_type><Amount></Amount><Tenor></Tenor><Product_Type></Product_Type><End_use_monitoring></End_use_monitoring><Photograph_as_per_policy></Photograph_as_per_policy><NTC_Amount></NTC_Amount><No_of_EMI_repaid></No_of_EMI_repaid><TESS_Loan_outstanding_RAK></TESS_Loan_outstanding_RAK><Whats_the_reason></Whats_the_reason></Product_Attribute>";
		
		StringBuffer ProductAttrB = new StringBuffer(ProductAttrXML);
		
		if(!"".equalsIgnoreCase(getControlValue("CIF_NUMBER",iformObj).trim()))
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<applicant_id>")+"<applicant_id>".length(),getControlValue("CIF_NUMBER",iformObj).trim());
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<applicant_id>"), ProductAttrB.indexOf("</applicant_id>")+"</applicant_id>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("NEW_TOPUP",iformObj).trim()))
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<Loan_Type>")+"<Loan_Type>".length(),getControlValue("NEW_TOPUP",iformObj).trim());
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<Loan_Type>"), ProductAttrB.indexOf("</Loan_Type>")+"</Loan_Type>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("FINANCE_TYPE",iformObj).trim()))
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<Type_of_Finance>")+"<Type_of_Finance>".length(),getControlValue("FINANCE_TYPE",iformObj).trim());
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<Type_of_Finance>"), ProductAttrB.indexOf("</Type_of_Finance>")+"</Type_of_Finance>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("NTBEXISTING",iformObj).trim()))
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<Cust_type>")+"<Cust_type>".length(),getControlValue("NTBEXISTING",iformObj).trim());
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<Cust_type>"), ProductAttrB.indexOf("</Cust_type>")+"</Cust_type>".length());
	
		if(!"".equalsIgnoreCase(getControlValue("REQUESTEDLOANAMOUNT",iformObj).trim()))
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<Amount>")+"<Amount>".length(),getControlValue("REQUESTEDLOANAMOUNT",iformObj).trim());
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<Amount>"), ProductAttrB.indexOf("</Amount>")+"</Amount>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("PROPOSED_TENOR",iformObj).trim()))
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<Tenor>")+"<Tenor>".length(),getControlValue("PROPOSED_TENOR",iformObj).trim());
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<Tenor>"), ProductAttrB.indexOf("</Tenor>")+"</Tenor>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("CONVENTIONAL_ISLAMIC",iformObj).trim()))
		{
			String ConOrIsl = getControlValue("CONVENTIONAL_ISLAMIC",iformObj).trim();
			if("Islamic".equalsIgnoreCase(ConOrIsl))
				ConOrIsl = "ISL";
			else if("Conventional".equalsIgnoreCase(ConOrIsl))
				ConOrIsl = "CON";
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<Product_Type>")+"<Product_Type>".length(),ConOrIsl);
		}else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<Product_Type>"), ProductAttrB.indexOf("</Product_Type>")+"</Product_Type>".length());
				
		String EUMProposed ="";
		List<List<String>> EUMProposedDtls = iformObj.getDataFromDB("select top 1 eu.EUM_PROPOSED from USR_0_IRBL_EUMPROPOSED_DTLS eu with(nolock) where WI_NAME = '"+getWorkitemName(iformObj)+"' and EUM_PROPOSED is not null and EUM_PROPOSED <> '' ");
		for (List<String> row : EUMProposedDtls) {
			if (!row.get(0).equalsIgnoreCase("") && row.get(0) !=null)
			{
				EUMProposed=row.get(0).trim();
			}
		}
		
		if(!"".equalsIgnoreCase(EUMProposed.trim()))
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<End_use_monitoring>")+"<End_use_monitoring>".length(),EUMProposed.trim());
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<End_use_monitoring>"), ProductAttrB.indexOf("</End_use_monitoring>")+"</End_use_monitoring>".length());
		
		
		String isPhotographDocAttached = "";
		List<List<String>> doc = iformObj.getDataFromDB("select CASE WHEN count(*) > 0 THEN 'Y' ELSE 'N' END from PDBFolder pf with(nolock), PDBDocumentContent pdc with(nolcok), PDBDocument pd with(nolock) "
				+ "where pf.Name = '"+getWorkitemName(iformObj)+"' "
				+ "and pf.FolderIndex = pdc.ParentFolderIndex "
				+ "and pd.DocumentIndex = pdc.DocumentIndex "
				+ "AND pd.Name = 'Photograph_Location_VisitingCard'");
		for (List<String> row : doc) {
			if (!row.get(0).equalsIgnoreCase(""))
			{
				isPhotographDocAttached = row.get(0).trim();
			}
		}		
		
		if(!"".equalsIgnoreCase(isPhotographDocAttached)) 
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<Photograph_as_per_policy>")+"<Photograph_as_per_policy>".length(),isPhotographDocAttached);
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<Photograph_as_per_policy>"), ProductAttrB.indexOf("</Photograph_as_per_policy>")+"</Photograph_as_per_policy>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("NTC_VALUE",iformObj).trim()))
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<NTC_Amount>")+"<NTC_Amount>".length(),getControlValue("NTC_VALUE",iformObj).trim());
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<NTC_Amount>"), ProductAttrB.indexOf("</NTC_Amount>")+"</NTC_Amount>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("NOOFEMIREPAIDTESSLOAN",iformObj).trim()))
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<No_of_EMI_repaid>")+"<No_of_EMI_repaid>".length(),getControlValue("NOOFEMIREPAIDTESSLOAN",iformObj).trim());
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<No_of_EMI_repaid>"), ProductAttrB.indexOf("</No_of_EMI_repaid>")+"</No_of_EMI_repaid>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("TESSLOANOUTSNDRAK",iformObj).trim()))
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<TESS_Loan_outstanding_RAK>")+"<TESS_Loan_outstanding_RAK>".length(),getControlValue("TESSLOANOUTSNDRAK",iformObj).trim());
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<TESS_Loan_outstanding_RAK>"), ProductAttrB.indexOf("</TESS_Loan_outstanding_RAK>")+"</TESS_Loan_outstanding_RAK>".length());
		
		String WhatsTheReason = "";
		List<List<String>> tmp = iformObj.getDataFromDB("select top 1 WHATSTHEREASON from RB_iRBL_TXNTABLE with(nolock) where WI_NAME = '"+getWorkitemName(iformObj)+"' ");
		for (List<String> row : tmp) {
			if (!row.get(0).equalsIgnoreCase(""))
			{
				WhatsTheReason = row.get(0).trim();
			}
		}
		if(!"".equalsIgnoreCase(WhatsTheReason))
			ProductAttrB = ProductAttrB.insert(ProductAttrB.indexOf("<Whats_the_reason>")+"<Whats_the_reason>".length(),WhatsTheReason);
		else
			ProductAttrB = ProductAttrB.delete(ProductAttrB.indexOf("<Whats_the_reason>"), ProductAttrB.indexOf("</Whats_the_reason>")+"</Whats_the_reason>".length());
		
		
		ProductAttrXML = ProductAttrB.toString();
		return ProductAttrXML;
	}

	public String DecTechCompanyDetailsXML(IFormReference iformObj,String control,String Data)
	{
		String CompanyDetailsXML = "<Company_Details><applicant_id></applicant_id><Company_Name></Company_Name><Business_Constitution></Business_Constitution><VAT_Exemption_flag></VAT_Exemption_flag><Number_of_Employees></Number_of_Employees><WPS_routed_from></WPS_routed_from><Company_outsource_staff_flag></Company_outsource_staff_flag><Ownership_of_Premises></Ownership_of_Premises><Type_of_Premises></Type_of_Premises><Emirate></Emirate><Industry></Industry><Industry_Sub_Category></Industry_Sub_Category><Percentage_Annual_Sales></Percentage_Annual_Sales><Percentage_Local_international></Percentage_Local_international><Percentage_Retail></Percentage_Retail><Percentage_Wholesale></Percentage_Wholesale><License_Number></License_Number><License_Issuing_Date></License_Issuing_Date><License_Issuing_authority></License_Issuing_authority><Country_of_Incorporation></Country_of_Incorporation><Date_of_Incorporation></Date_of_Incorporation><Number_of_Years_in_Business></Number_of_Years_in_Business><Landline_Success_validated></Landline_Success_validated><VAT_receipts></VAT_receipts><Is_companydeal_Iran></Is_companydeal_Iran><Firco_Check></Firco_Check><CBRB_rating></CBRB_rating><BCS_check></BCS_check><CBBlackList_Negated_WatchList></CBBlackList_Negated_WatchList><ALOC_FPU></ALOC_FPU></Company_Details>";
		
		StringBuffer DetailsB = new StringBuffer(CompanyDetailsXML);
		
		if(!"".equalsIgnoreCase(getControlValue("CIF_NUMBER",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),getControlValue("CIF_NUMBER",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<applicant_id>"), DetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("COMPANY_NAME",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Company_Name>")+"<Company_Name>".length(),getControlValue("COMPANY_NAME",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Company_Name>"), DetailsB.indexOf("</Company_Name>")+"</Company_Name>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("BUSINESSCONSTITUTION",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Business_Constitution>")+"<Business_Constitution>".length(),getControlValue("BUSINESSCONSTITUTION",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Business_Constitution>"), DetailsB.indexOf("</Business_Constitution>")+"</Business_Constitution>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("VATEXEMPTIONFLAG",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<VAT_Exemption_flag>")+"<VAT_Exemption_flag>".length(),getControlValue("VATEXEMPTIONFLAG",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<VAT_Exemption_flag>"), DetailsB.indexOf("</VAT_Exemption_flag>")+"</VAT_Exemption_flag>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("NUMBEROFEMPLOYEES",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Number_of_Employees>")+"<Number_of_Employees>".length(),getControlValue("NUMBEROFEMPLOYEES",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Number_of_Employees>"), DetailsB.indexOf("</Number_of_Employees>")+"</Number_of_Employees>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("WPSROUTEDFROM",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<WPS_routed_from>")+"<WPS_routed_from>".length(),getControlValue("WPSROUTEDFROM",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<WPS_routed_from>"), DetailsB.indexOf("</WPS_routed_from>")+"</WPS_routed_from>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("COMPANYOUTSOURCESTAFFFLAG",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Company_outsource_staff_flag>")+"<Company_outsource_staff_flag>".length(),getControlValue("COMPANYOUTSOURCESTAFFFLAG",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Company_outsource_staff_flag>"), DetailsB.indexOf("</Company_outsource_staff_flag>")+"</Company_outsource_staff_flag>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("OWNERSHIPOFPREMISES",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Ownership_of_Premises>")+"<Ownership_of_Premises>".length(),getControlValue("OWNERSHIPOFPREMISES",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Ownership_of_Premises>"), DetailsB.indexOf("</Ownership_of_Premises>")+"</Ownership_of_Premises>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("TYPEOFPREMISES",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Type_of_Premises>")+"<Type_of_Premises>".length(),getControlValue("TYPEOFPREMISES",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Type_of_Premises>"), DetailsB.indexOf("</Type_of_Premises>")+"</Type_of_Premises>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("PREMISESEMIRATE",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Emirate>")+"<Emirate>".length(),getControlValue("PREMISESEMIRATE",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Emirate>"), DetailsB.indexOf("</Emirate>")+"</Emirate>".length());
		
		String Industry = "";
		String Industry_Sub_Category ="";
		List<List<String>> IndstryDtls = iformObj.getDataFromDB("SELECT  replace(idm.IndustryDesc,'&','And'), replace(idsm.INDUSTRY_SUBCATEGORY,'&','And') FROM USR_0_IRBL_INDUSTRY_CODE_DTLS idc WITH(NOLOCK), USR_0_IRBL_INDUSTRYCODEMASTER idm with(nolock), USR_0_IRBL_INDUSTRY_SUBCATEGORY_MASTER idsm with(nolock) WHERE idc.INDUSTRY_CODE=idm.IndustryCode and idc.INDUSTRY_SUB_CATEGORY = idsm.INDUSTRY_SUBCATCODE and idc.WI_NAME = '"+getWorkitemName(iformObj)+"' ORDER BY insertionOrderId");
		for (List<String> row : IndstryDtls) {
			if (!row.get(0).equalsIgnoreCase(""))
			{
				if("".equalsIgnoreCase(Industry))
				{
					if(!"".equalsIgnoreCase(row.get(0).trim()))
						Industry="'"+row.get(0).trim()+"'";
				}
				else
				{
					if(!"".equalsIgnoreCase(row.get(0).trim()))
						Industry=Industry+",'"+row.get(0).trim()+"'";
				}
				
				if("".equalsIgnoreCase(Industry_Sub_Category))
				{
					if(!"".equalsIgnoreCase(row.get(1).trim()))
						Industry_Sub_Category="'"+row.get(1).trim()+"'";
				}
				else
				{
					if(!"".equalsIgnoreCase(row.get(1).trim()))
						Industry_Sub_Category=Industry_Sub_Category+",'"+row.get(1).trim()+"'";
				}
			}
		}
		
		if(!"".equalsIgnoreCase(Industry))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Industry>")+"<Industry>".length(),Industry);
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Industry>"), DetailsB.indexOf("</Industry>")+"</Industry>".length());
		
		if(!"".equalsIgnoreCase(Industry_Sub_Category))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Industry_Sub_Category>")+"<Industry_Sub_Category>".length(),Industry_Sub_Category);
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Industry_Sub_Category>"), DetailsB.indexOf("</Industry_Sub_Category>")+"</Industry_Sub_Category>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("PERCENTAGEANNUALSALES",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Percentage_Annual_Sales>")+"<Percentage_Annual_Sales>".length(),getControlValue("PERCENTAGEANNUALSALES",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Percentage_Annual_Sales>"), DetailsB.indexOf("</Percentage_Annual_Sales>")+"</Percentage_Annual_Sales>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("PERCENTAGELOCALINTERNATIONAL",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Percentage_Local_international>")+"<Percentage_Local_international>".length(),getControlValue("PERCENTAGELOCALINTERNATIONAL",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Percentage_Local_international>"), DetailsB.indexOf("</Percentage_Local_international>")+"</Percentage_Local_international>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("PERCENTAGERETAIL",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Percentage_Retail>")+"<Percentage_Retail>".length(),getControlValue("PERCENTAGERETAIL",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Percentage_Retail>"), DetailsB.indexOf("</Percentage_Retail>")+"</Percentage_Retail>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("PERCENTAGEWHOLESALE",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Percentage_Wholesale>")+"<Percentage_Wholesale>".length(),getControlValue("PERCENTAGEWHOLESALE",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Percentage_Wholesale>"), DetailsB.indexOf("</Percentage_Wholesale>")+"</Percentage_Wholesale>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("TL_NUMBER",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<License_Number>")+"<License_Number>".length(),getControlValue("TL_NUMBER",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<License_Number>"), DetailsB.indexOf("</License_Number>")+"</License_Number>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("TLISSUEDATE",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<License_Issuing_Date>")+"<License_Issuing_Date>".length(),getControlValue("TLISSUEDATE",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<License_Issuing_Date>"), DetailsB.indexOf("</License_Issuing_Date>")+"</License_Issuing_Date>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("ISSUING_EMIRATE",iformObj).trim())) // Need to Check the field
			DetailsB = DetailsB.insert(DetailsB.indexOf("<License_Issuing_authority>")+"<License_Issuing_authority>".length(),getControlValue("ISSUING_EMIRATE",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<License_Issuing_authority>"), DetailsB.indexOf("</License_Issuing_authority>")+"</License_Issuing_authority>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("COUNTRYOFINCORPORATION",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Country_of_Incorporation>")+"<Country_of_Incorporation>".length(),getControlValue("COUNTRYOFINCORPORATION",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Country_of_Incorporation>"), DetailsB.indexOf("</Country_of_Incorporation>")+"</Country_of_Incorporation>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("DATEOFINCORPORATION",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Date_of_Incorporation>")+"<Date_of_Incorporation>".length(),getControlValue("DATEOFINCORPORATION",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Date_of_Incorporation>"), DetailsB.indexOf("</Date_of_Incorporation>")+"</Date_of_Incorporation>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("NUMBEROFYEARSINBUSINESS",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Number_of_Years_in_Business>")+"<Number_of_Years_in_Business>".length(),getControlValue("NUMBEROFYEARSINBUSINESS",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Number_of_Years_in_Business>"), DetailsB.indexOf("</Number_of_Years_in_Business>")+"</Number_of_Years_in_Business>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("LANDLINESUCCESSVALIDATED",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Landline_Success_validated>")+"<Landline_Success_validated>".length(),getControlValue("LANDLINESUCCESSVALIDATED",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Landline_Success_validated>"), DetailsB.indexOf("</Landline_Success_validated>")+"</Landline_Success_validated>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("VATRECEIPTS",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<VAT_receipts>")+"<VAT_receipts>".length(),getControlValue("VATRECEIPTS",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<VAT_receipts>"), DetailsB.indexOf("</VAT_receipts>")+"</VAT_receipts>".length());
		
		if(!"".equalsIgnoreCase(getControlValue("ISCOMPANYDEALIRAN",iformObj).trim()))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Is_companydeal_Iran>")+"<Is_companydeal_Iran>".length(),getControlValue("ISCOMPANYDEALIRAN",iformObj).trim());
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<Is_companydeal_Iran>"), DetailsB.indexOf("</Is_companydeal_Iran>")+"</Is_companydeal_Iran>".length());
		
		int FircoHitTrueCount = 0;
		List<List<String>> tmp = iformObj.getDataFromDB("SELECT count(*) AS HITCOUNT FROM USR_0_IRBL_FIRCO_GRID_DTLS WITH(NOLOCK) WHERE WI_NAME = '"+getWorkitemName(iformObj)+"' AND (MATCH_STATUS IS NULL OR MATCH_STATUS != 'FALSE') AND DETAILS_FOR='Company'");
		for (List<String> row : tmp) {
			if (!row.get(0).equalsIgnoreCase(""))
			{
				FircoHitTrueCount = Integer.parseInt(row.get(0).trim());
			}
		}
		
		if(FircoHitTrueCount >= 1)
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Firco_Check>")+"<Firco_Check>".length(),"Y");
		else
			DetailsB = DetailsB.insert(DetailsB.indexOf("<Firco_Check>")+"<Firco_Check>".length(),"N");
		
		//if("45645dfgd".equalsIgnoreCase(getControlValue("ISCOMPANYDEALIRAN").trim())) // Need to Check the field
			//DetailsB = DetailsB.insert(DetailsB.indexOf("<CBRB_rating>")+"<CBRB_rating>".length(),getControlValue("ISCOMPANYDEALIRAN").trim());
		//else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<CBRB_rating>"), DetailsB.indexOf("</CBRB_rating>")+"</CBRB_rating>".length());
		
		
		String BCSVal = "";
		tmp = iformObj.getDataFromDB("SELECT TOP 1 GO_NO_GO FROM USR_0_IRBL_BASIC_LEAD_FIL_DTLS WITH(NOLOCK) WHERE WI_NAME =  '"+getWorkitemName(iformObj)+"' AND CRITERIA = 'BCS check'");
		for (List<String> row : tmp) {
			if (!row.get(0).equalsIgnoreCase(""))
			{
				BCSVal = row.get(0).trim();
			}
		}
		if ("Go".equalsIgnoreCase(BCSVal))
			BCSVal = "N";
		else if ("No Go".equalsIgnoreCase(BCSVal) || "Refer".equalsIgnoreCase(BCSVal))
			BCSVal = "Y";
		
		if(!"".equalsIgnoreCase(BCSVal))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<BCS_check>")+"<BCS_check>".length(),BCSVal);
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<BCS_check>"), DetailsB.indexOf("</BCS_check>")+"</BCS_check>".length());
		
		
		String CBBLVal = "";
		tmp = iformObj.getDataFromDB("SELECT TOP 1 GO_NO_GO FROM USR_0_IRBL_BASIC_LEAD_FIL_DTLS WITH(NOLOCK) WHERE WI_NAME =  '"+getWorkitemName(iformObj)+"' AND CRITERIA = 'CB Blacklist'");
		for (List<String> row : tmp) {
			if (!row.get(0).equalsIgnoreCase(""))
			{
				CBBLVal = row.get(0).trim();
			}
		}
		if ("Go".equalsIgnoreCase(CBBLVal))
			CBBLVal = "N";
		else if ("No Go".equalsIgnoreCase(CBBLVal) || "Refer".equalsIgnoreCase(CBBLVal))
			CBBLVal = "Y";
		
		if(!"".equalsIgnoreCase(CBBLVal)) // Need to Check the field
			DetailsB = DetailsB.insert(DetailsB.indexOf("<CBBlackList_Negated_WatchList>")+"<CBBlackList_Negated_WatchList>".length(),CBBLVal);
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<CBBlackList_Negated_WatchList>"), DetailsB.indexOf("</CBBlackList_Negated_WatchList>")+"</CBBlackList_Negated_WatchList>".length());
		
		
		String ALOCVal = "";
		tmp = iformObj.getDataFromDB("SELECT TOP 1 GO_NO_GO FROM USR_0_IRBL_BASIC_LEAD_FIL_DTLS WITH(NOLOCK) WHERE WI_NAME =  '"+getWorkitemName(iformObj)+"' AND CRITERIA = 'ALOC/FPU'");
		for (List<String> row : tmp) {
			if (!row.get(0).equalsIgnoreCase(""))
			{
				ALOCVal = row.get(0).trim();
			}
		}
		if ("Go".equalsIgnoreCase(ALOCVal))
			ALOCVal = "N";
		else if ("No Go".equalsIgnoreCase(ALOCVal) || "Refer".equalsIgnoreCase(ALOCVal))
			ALOCVal = "Y";
		
		if(!"".equalsIgnoreCase(ALOCVal))
			DetailsB = DetailsB.insert(DetailsB.indexOf("<ALOC_FPU>")+"<ALOC_FPU>".length(),ALOCVal);
		else
			DetailsB = DetailsB.delete(DetailsB.indexOf("<ALOC_FPU>"), DetailsB.indexOf("</ALOC_FPU>")+"</ALOC_FPU>".length());
		
		
		CompanyDetailsXML = DetailsB.toString();
		return CompanyDetailsXML;
	}
	
	public String DecTechCompanyStakeholdersXML(IFormReference iformObj,String control,String Data)
	{
		String CompanyStakeholdersXMLMain = "";
		List<List<String>> RelPartyLst = iformObj.getDataFromDB("SELECT CIF, COMPANYFLAG, RELATIONSHIPTYPE, NAME_OF_SISTER_COMPANY, COMPANYCATEGORY, TL_NUMBER, YEAROFINCORPORATION, ISTLVALID, COUNTRY, EMIRATE, FIRSTNAME, MIDDLENAME, LASTNAME, isnull(format(DATEOFBIRTH,'yyyy-MM-dd'),'') as DATEOFBIRTH, EMIRATESID, ISGOVERNMENTRELATION, SIGNATORYFLAG, TYPEOFOWNERSHIP, TYPEOFPROOFPROVIDEDFORLOB, isnull(format(SIGNATORYPOWERHELDSINCEDATE,'yyyy-MM-dd'),'') as SIGNATORYPOWERHELDSINCEDATE, INVOLVEDINBUSINESS, SHAREHOLDINGPERCENTAGE, AUTHORITYTYPE, NATIONALITY, ADDITIONALNATIONALITY, VISASPONSOR, SISCOTLNO, ISNOCPROVIDED, PASSPORTNOVISASPONSOR, isnull(format(VISAEXPIRYDATE,'yyyy-MM-dd'),'') as VISAEXPIRYDATE,COUNTRYOFRESIDENCE,RELATEDPARTYID,isnull(ISSISCOCOBORROWERGUARANTOR,'') FROM USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS WITH(nolock) WHERE WI_NAME = '"+getWorkitemName(iformObj)+"' AND RELATEDPARTYID IS NOT NULL AND RELATEDPARTYID !='' ORDER BY insertionOrderId");
		
		for (List<String> row : RelPartyLst) {
					
			String CompanyStakeholdersXML = "<Company_Stakeholder><applicant_id></applicant_id><Company_Flag></Company_Flag><Relationship_type></Relationship_type><Company_Name></Company_Name><Company_Category></Company_Category><TL_Number></TL_Number><Year_of_Incorporation></Year_of_Incorporation><Is_TL_Valid></Is_TL_Valid><Country></Country><Emirate></Emirate><First_Name></First_Name><Middle_Name></Middle_Name><Last_Name></Last_Name><Date_Of_Birth></Date_Of_Birth><Emirates_ID></Emirates_ID><Is_Government_Relation></Is_Government_Relation><Signatory_Flag></Signatory_Flag><Type_of_ownership></Type_of_ownership><Type_of_Proof_provided_for_LOB></Type_of_Proof_provided_for_LOB><Sigantory_power_held_since_date></Sigantory_power_held_since_date><Involved_in_business></Involved_in_business><Shareholding_percentage></Shareholding_percentage><Authority_Type></Authority_Type><Nationality></Nationality><Additional_Nationality></Additional_Nationality><Visa_sponsor></Visa_sponsor><Sisco_TLNo></Sisco_TLNo><Is_NOC_Provided></Is_NOC_Provided><Passport_no_Visa_Sponsor></Passport_no_Visa_Sponsor><Visa_Expiry_date></Visa_Expiry_date><Is_Sisco_coborrower_guarantor></Is_Sisco_coborrower_guarantor><Firco_Check></Firco_Check><CBRB_rating></CBRB_rating><BCS_check></BCS_check><CBBlackList_Negated_WatchList></CBBlackList_Negated_WatchList><ALOC_FPU></ALOC_FPU></Company_Stakeholder>";
			
			StringBuffer DetailsB = new StringBuffer(CompanyStakeholdersXML);
			
			if(!"".equalsIgnoreCase(row.get(0).trim())) // CIF ID
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),row.get(0).trim());
			else if(!"".equalsIgnoreCase(row.get(31).trim())) // Related Party Id
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),row.get(31).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<applicant_id>"), DetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
			
			if(!"".equalsIgnoreCase(row.get(1).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Company_Flag>")+"<Company_Flag>".length(),row.get(1).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Company_Flag>"), DetailsB.indexOf("</Company_Flag>")+"</Company_Flag>".length());
			
			if(!"".equalsIgnoreCase(row.get(2).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Relationship_type>")+"<Relationship_type>".length(),row.get(2).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Relationship_type>"), DetailsB.indexOf("</Relationship_type>")+"</Relationship_type>".length());
			
			if(!"".equalsIgnoreCase(row.get(3).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Company_Name>")+"<Company_Name>".length(),row.get(3).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Company_Name>"), DetailsB.indexOf("</Company_Name>")+"</Company_Name>".length());
			
			if(!"".equalsIgnoreCase(row.get(4).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Company_Category>")+"<Company_Category>".length(),row.get(4).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Company_Category>"), DetailsB.indexOf("</Company_Category>")+"</Company_Category>".length());
			
			if(!"".equalsIgnoreCase(row.get(5).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<TL_Number>")+"<TL_Number>".length(),row.get(5).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<TL_Number>"), DetailsB.indexOf("</TL_Number>")+"</TL_Number>".length());
			
			if(!"".equalsIgnoreCase(row.get(6).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Year_of_Incorporation>")+"<Year_of_Incorporation>".length(),row.get(6).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Year_of_Incorporation>"), DetailsB.indexOf("</Year_of_Incorporation>")+"</Year_of_Incorporation>".length());
			
			if(!"".equalsIgnoreCase(row.get(7).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Is_TL_Valid>")+"<Is_TL_Valid>".length(),row.get(7).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Is_TL_Valid>"), DetailsB.indexOf("</Is_TL_Valid>")+"</Is_TL_Valid>".length());
			
			if(!"".equalsIgnoreCase(row.get(8).trim())) // country of incorporation 
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Country>")+"<Country>".length(),row.get(8).trim());
			else if(!"".equalsIgnoreCase(row.get(30).trim())) // country of residence
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Country>")+"<Country>".length(),row.get(30).trim());
			else 
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Country>"), DetailsB.indexOf("</Country>")+"</Country>".length());
			
			if(!"".equalsIgnoreCase(row.get(9).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Emirate>")+"<Emirate>".length(),row.get(9).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Emirate>"), DetailsB.indexOf("</Emirate>")+"</Emirate>".length());
			
			if(!"".equalsIgnoreCase(row.get(10).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<First_Name>")+"<First_Name>".length(),row.get(10).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<First_Name>"), DetailsB.indexOf("</First_Name>")+"</First_Name>".length());
			
			if(!"".equalsIgnoreCase(row.get(11).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Middle_Name>")+"<Middle_Name>".length(),row.get(11).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Middle_Name>"), DetailsB.indexOf("</Middle_Name>")+"</Middle_Name>".length());
			
			if(!"".equalsIgnoreCase(row.get(12).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Last_Name>")+"<Last_Name>".length(),row.get(12).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Last_Name>"), DetailsB.indexOf("</Last_Name>")+"</Last_Name>".length());
			
			if(!"".equalsIgnoreCase(row.get(13).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Date_Of_Birth>")+"<Date_Of_Birth>".length(),row.get(13).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Date_Of_Birth>"), DetailsB.indexOf("</Date_Of_Birth>")+"</Date_Of_Birth>".length());
			
			if(!"".equalsIgnoreCase(row.get(14).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Emirates_ID>")+"<Emirates_ID>".length(),row.get(14).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Emirates_ID>"), DetailsB.indexOf("</Emirates_ID>")+"</Emirates_ID>".length());
			
			String isGovernmentRelation = row.get(15).trim();
			if("LPEP".equalsIgnoreCase(isGovernmentRelation) || "FPEP".equalsIgnoreCase(isGovernmentRelation) || "Y".equalsIgnoreCase(isGovernmentRelation))
				isGovernmentRelation = "Y";
			else
				isGovernmentRelation = "N";
			
			if(!"".equalsIgnoreCase(isGovernmentRelation))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Is_Government_Relation>")+"<Is_Government_Relation>".length(),isGovernmentRelation.trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Is_Government_Relation>"), DetailsB.indexOf("</Is_Government_Relation>")+"</Is_Government_Relation>".length());
			
			if(!"".equalsIgnoreCase(row.get(16).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Signatory_Flag>")+"<Signatory_Flag>".length(),row.get(16).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Signatory_Flag>"), DetailsB.indexOf("</Signatory_Flag>")+"</Signatory_Flag>".length());
			
			if(!"".equalsIgnoreCase(row.get(17).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Type_of_ownership>")+"<Type_of_ownership>".length(),row.get(17).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Type_of_ownership>"), DetailsB.indexOf("</Type_of_ownership>")+"</Type_of_ownership>".length());
			
			if(!"".equalsIgnoreCase(row.get(18).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Type_of_Proof_provided_for_LOB>")+"<Type_of_Proof_provided_for_LOB>".length(),row.get(18).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Type_of_Proof_provided_for_LOB>"), DetailsB.indexOf("</Type_of_Proof_provided_for_LOB>")+"</Type_of_Proof_provided_for_LOB>".length());
			
			if(!"".equalsIgnoreCase(row.get(19).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Sigantory_power_held_since_date>")+"<Sigantory_power_held_since_date>".length(),row.get(19).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Sigantory_power_held_since_date>"), DetailsB.indexOf("</Sigantory_power_held_since_date>")+"</Sigantory_power_held_since_date>".length());
						
			if(!"".equalsIgnoreCase(row.get(20).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Involved_in_business>")+"<Involved_in_business>".length(),row.get(20).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Involved_in_business>"), DetailsB.indexOf("</Involved_in_business>")+"</Involved_in_business>".length());
			
			if(!"".equalsIgnoreCase(row.get(21).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Shareholding_percentage>")+"<Shareholding_percentage>".length(),row.get(21).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Shareholding_percentage>"), DetailsB.indexOf("</Shareholding_percentage>")+"</Shareholding_percentage>".length());
			
			if(!"".equalsIgnoreCase(row.get(22).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Authority_Type>")+"<Authority_Type>".length(),row.get(22).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Authority_Type>"), DetailsB.indexOf("</Authority_Type>")+"</Authority_Type>".length());
			
			if(!"".equalsIgnoreCase(row.get(23).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Nationality>")+"<Nationality>".length(),row.get(23).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Nationality>"), DetailsB.indexOf("</Nationality>")+"</Nationality>".length());
			
			if(!"".equalsIgnoreCase(row.get(24).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Additional_Nationality>")+"<Additional_Nationality>".length(),row.get(24).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Additional_Nationality>"), DetailsB.indexOf("</Additional_Nationality>")+"</Additional_Nationality>".length());
			
			if(!"".equalsIgnoreCase(row.get(25).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Visa_sponsor>")+"<Visa_sponsor>".length(),row.get(25).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Visa_sponsor>"), DetailsB.indexOf("</Visa_sponsor>")+"</Visa_sponsor>".length());
			
			if(!"".equalsIgnoreCase(row.get(26).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Sisco_TLNo>")+"<Sisco_TLNo>".length(),row.get(26).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Sisco_TLNo>"), DetailsB.indexOf("</Sisco_TLNo>")+"</Sisco_TLNo>".length());
			
			if(!"".equalsIgnoreCase(row.get(27).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Is_NOC_Provided>")+"<Is_NOC_Provided>".length(),row.get(27).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Is_NOC_Provided>"), DetailsB.indexOf("</Is_NOC_Provided>")+"</Is_NOC_Provided>".length());
			
			if(!"".equalsIgnoreCase(row.get(28).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Passport_no_Visa_Sponsor>")+"<Passport_no_Visa_Sponsor>".length(),row.get(28).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Passport_no_Visa_Sponsor>"), DetailsB.indexOf("</Passport_no_Visa_Sponsor>")+"</Passport_no_Visa_Sponsor>".length());
			
			if(!"".equalsIgnoreCase(row.get(29).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Visa_Expiry_date>")+"<Visa_Expiry_date>".length(),row.get(29).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Visa_Expiry_date>"), DetailsB.indexOf("</Visa_Expiry_date>")+"</Visa_Expiry_date>".length());
			
			
			if(!"".equalsIgnoreCase(row.get(32).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Is_Sisco_coborrower_guarantor>")+"<Is_Sisco_coborrower_guarantor>".length(),row.get(32).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Is_Sisco_coborrower_guarantor>"), DetailsB.indexOf("</Is_Sisco_coborrower_guarantor>")+"</Is_Sisco_coborrower_guarantor>".length());
			
			////////////////////

			int FircoHitTrueCount = 0;
			List<List<String>> tmp = iformObj.getDataFromDB("SELECT count(*) AS HITCOUNT FROM USR_0_IRBL_FIRCO_GRID_DTLS WITH(NOLOCK) WHERE WI_NAME = '"+getWorkitemName(iformObj)+"' AND (MATCH_STATUS IS NULL OR MATCH_STATUS != 'FALSE') AND DETAILS_FOR='"+row.get(31).trim()+"'");
			for (List<String> row1 : tmp) {
				if (!row1.get(0).equalsIgnoreCase(""))
				{
					FircoHitTrueCount = Integer.parseInt(row1.get(0).trim());
				}
			}
			
			if(FircoHitTrueCount >= 1)
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Firco_Check>")+"<Firco_Check>".length(),"Y");
			else
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Firco_Check>")+"<Firco_Check>".length(),"N");
						
			
			//if("sgshfhfhfd".equalsIgnoreCase(getControlValue("ISCOMPANYDEALIRAN"))) // Need to Check the field
				//DetailsB = DetailsB.insert(DetailsB.indexOf("<CBRB_rating>")+"<CBRB_rating>".length(),getControlValue("ISCOMPANYDEALIRAN"));
			//else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<CBRB_rating>"), DetailsB.indexOf("</CBRB_rating>")+"</CBRB_rating>".length());
			
			
			String BCSVal = "";
			tmp = iformObj.getDataFromDB("SELECT TOP 1 GO_NO_GO FROM USR_0_IRBL_BASIC_LEAD_FIL_DTLS WITH(NOLOCK) WHERE WI_NAME =  '"+getWorkitemName(iformObj)+"' AND CRITERIA = 'BCS check'");
			for (List<String> row1 : tmp) {
				if (!row1.get(0).equalsIgnoreCase(""))
				{
					BCSVal = row1.get(0).trim();
				}
			}
			if ("Go".equalsIgnoreCase(BCSVal))
				BCSVal = "N";
			else if ("No Go".equalsIgnoreCase(BCSVal) || "Refer".equalsIgnoreCase(BCSVal))
				BCSVal = "Y";
			
			if(!"".equalsIgnoreCase(BCSVal))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<BCS_check>")+"<BCS_check>".length(),BCSVal);
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<BCS_check>"), DetailsB.indexOf("</BCS_check>")+"</BCS_check>".length());
			
			
			String CBBLVal = "";
			tmp = iformObj.getDataFromDB("SELECT TOP 1 GO_NO_GO FROM USR_0_IRBL_BASIC_LEAD_FIL_DTLS WITH(NOLOCK) WHERE WI_NAME =  '"+getWorkitemName(iformObj)+"' AND CRITERIA = 'CB Blacklist'");
			for (List<String> row1 : tmp) {
				if (!row1.get(0).equalsIgnoreCase(""))
				{
					CBBLVal = row1.get(0).trim();
				}
			}
			if ("Go".equalsIgnoreCase(CBBLVal))
				CBBLVal = "N";
			else if ("No Go".equalsIgnoreCase(CBBLVal) || "Refer".equalsIgnoreCase(CBBLVal))
				CBBLVal = "Y";
			
			if(!"".equalsIgnoreCase(CBBLVal)) // Need to Check the field
				DetailsB = DetailsB.insert(DetailsB.indexOf("<CBBlackList_Negated_WatchList>")+"<CBBlackList_Negated_WatchList>".length(),CBBLVal);
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<CBBlackList_Negated_WatchList>"), DetailsB.indexOf("</CBBlackList_Negated_WatchList>")+"</CBBlackList_Negated_WatchList>".length());
			
			
			String ALOCVal = "";
			tmp = iformObj.getDataFromDB("SELECT TOP 1 GO_NO_GO FROM USR_0_IRBL_BASIC_LEAD_FIL_DTLS WITH(NOLOCK) WHERE WI_NAME =  '"+getWorkitemName(iformObj)+"' AND CRITERIA = 'ALOC/FPU'");
			for (List<String> row1 : tmp) {
				if (!row1.get(0).equalsIgnoreCase(""))
				{
					ALOCVal = row1.get(0).trim();
				}
			}
			if ("Go".equalsIgnoreCase(ALOCVal))
				ALOCVal = "N";
			else if ("No Go".equalsIgnoreCase(ALOCVal) || "Refer".equalsIgnoreCase(ALOCVal))
				ALOCVal = "Y";
			
			if(!"".equalsIgnoreCase(ALOCVal))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<ALOC_FPU>")+"<ALOC_FPU>".length(),ALOCVal);
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<ALOC_FPU>"), DetailsB.indexOf("</ALOC_FPU>")+"</ALOC_FPU>".length());
			
			
			
			CompanyStakeholdersXML = DetailsB.toString();
			CompanyStakeholdersXMLMain = CompanyStakeholdersXMLMain + CompanyStakeholdersXML;
		}
		
		return CompanyStakeholdersXMLMain;
	}
	
	public String DecTechBlacklistNegativeDetailsXML(IFormReference iformObj,String control,String Data)
	{
		String BlacklistedXML = "";
		String NegativeListedXML = "";
		List<List<String>> dedupDtls = iformObj.getDataFromDB("SELECT CIF_ID, IsBlackListed, IsNegativeListed, RetailCorpFlag FROM USR_0_IRBL_DEDUPE_GRID_DTLS WITH(nolock) WHERE CIF_ID IS NOT NULL AND (IsBlackListed='Y' OR IsBlackListed='N' OR IsNegativeListed='Y' OR IsNegativeListed='N') AND WI_NAME = '"+getWorkitemName(iformObj)+"' ");
		for (List<String> row : dedupDtls) {
			
			if(!"".equalsIgnoreCase(row.get(0).trim()))
			{
				String custType = row.get(3).trim();
				if("R".equalsIgnoreCase(custType))
					custType = "I";
				else if("C".equalsIgnoreCase(custType))
					custType = "C";
				else 
					custType = "I";
				
				if("Y".equalsIgnoreCase(row.get(1).trim()) || "N".equalsIgnoreCase(row.get(1).trim())) // for blacklist
				{
					BlacklistedXML = BlacklistedXML+"<Blacklist_Details>"+
							"<applicant_id>"+row.get(0).trim()+"</applicant_id>"+
							"<blacklist_cust_type>"+custType+"</blacklist_cust_type>"+
							"<internal_blacklist>"+row.get(1).trim()+"</internal_blacklist>"+
							"</Blacklist_Details>";
				}
				
				if("Y".equalsIgnoreCase(row.get(2).trim()) || "N".equalsIgnoreCase(row.get(2).trim())) // for negativelist
				{
					NegativeListedXML = NegativeListedXML+"<Negated_Details>"+
							"<applicant_id>"+row.get(0).trim()+"</applicant_id>"+
							"<negative_cust_type>"+custType+"</negative_cust_type>"+
							"<internal_negative_flag>"+row.get(1).trim()+"</internal_negative_flag>"+
							"</Negated_Details>";
				}
			}
		}
		
		return BlacklistedXML+NegativeListedXML;
	}
	
	public String DecTechAccountDetailsXML(IFormReference iformObj,String control,String Data)
	{
		String AcctDetailsXMLMain = "";
		List<List<String>> AcctDtls = iformObj.getDataFromDB("SELECT CifId, AcctType, CustRoleType, AcctId, AccountOpenDate, AcctStat, AcctSegment, AcctSubSegment, CreditGrade FROM USR_0_iRBL_InternalExpo_AcctDetails WHERE AcctId IS NOT NULL AND AcctId !='' AND AcctStat = 'ACTIVE' AND Wi_Name = '"+getWorkitemName(iformObj)+"' ");
		for (List<String> row : AcctDtls) {
			
			String AcctDetailsXML = "<Account_Details><applicant_id></applicant_id><type_of_account></type_of_account><role></role><account_number></account_number><acct_open_date></acct_open_date><acct_status></acct_status><account_segment></account_segment><account_sub_segment></account_sub_segment><credit_grade_code_individual></credit_grade_code_individual><credit_grade_code_company></credit_grade_code_company></Account_Details>";
			
			StringBuffer DetailsB = new StringBuffer(AcctDetailsXML);
			
			if(!"".equalsIgnoreCase(row.get(0).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),row.get(0).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<applicant_id>"), DetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
		
			if(!"".equalsIgnoreCase(row.get(1).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<type_of_account>")+"<type_of_account>".length(),row.get(1).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<type_of_account>"), DetailsB.indexOf("</type_of_account>")+"</type_of_account>".length());
			
			if(!"".equalsIgnoreCase(row.get(2).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<role>")+"<role>".length(),row.get(2).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<role>"), DetailsB.indexOf("</role>")+"</role>".length());
			
			if(!"".equalsIgnoreCase(row.get(3).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<account_number>")+"<account_number>".length(),row.get(3).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<account_number>"), DetailsB.indexOf("</account_number>")+"</account_number>".length());
			
			if(!"".equalsIgnoreCase(row.get(4).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<acct_open_date>")+"<acct_open_date>".length(),row.get(4).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<acct_open_date>"), DetailsB.indexOf("</acct_open_date>")+"</acct_open_date>".length());
			
			if(!"".equalsIgnoreCase(row.get(5).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<acct_status>")+"<acct_status>".length(),row.get(5).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<acct_status>"), DetailsB.indexOf("</acct_status>")+"</acct_status>".length());
			
			if(!"".equalsIgnoreCase(row.get(6).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<account_segment>")+"<account_segment>".length(),row.get(6).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<account_segment>"), DetailsB.indexOf("</account_segment>")+"</account_segment>".length());
			
			if(!"".equalsIgnoreCase(row.get(7).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<account_sub_segment>")+"<account_sub_segment>".length(),row.get(7).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<account_sub_segment>"), DetailsB.indexOf("</account_sub_segment>")+"</account_sub_segment>".length());
			
			if(!"".equalsIgnoreCase(row.get(8).trim()))
			{	
				DetailsB = DetailsB.insert(DetailsB.indexOf("<credit_grade_code_individual>")+"<credit_grade_code_individual>".length(),row.get(8).trim());
				DetailsB = DetailsB.insert(DetailsB.indexOf("<credit_grade_code_company>")+"<credit_grade_code_company>".length(),row.get(8).trim());
			}else{
				DetailsB = DetailsB.delete(DetailsB.indexOf("<credit_grade_code_individual>"), DetailsB.indexOf("</credit_grade_code_individual>")+"</credit_grade_code_individual>".length());
				DetailsB = DetailsB.delete(DetailsB.indexOf("<credit_grade_code_company>"), DetailsB.indexOf("</credit_grade_code_company>")+"</credit_grade_code_company>".length());
			}
			
			AcctDetailsXML = DetailsB.toString();
			AcctDetailsXMLMain = AcctDetailsXMLMain+AcctDetailsXML;
		}
		
		return AcctDetailsXMLMain;
	}
	
	
	public String DecTechTransactionDetailsXML(IFormReference iformObj,String control,String Data)
	{
		String TransDetailsXMLMain = "";
		List<List<String>> FinDtls = iformObj.getDataFromDB("SELECT TOP 1 isnull(format(TURNOVER_FROM,'yyyy-MM-dd'),'') as TURNOVER_FROM, isnull(format(TURNOVER_TO,'yyyy-MM-dd'),'') as TURNOVER_TO, TURNOVER_AMOUNT, TURNOVER_TIMEPERIOD, NETPROFIT, TOTALMONTHLYCREDITS, AVERAGE_BALANCE_AMOUNT, AVERAGE_BALANCE_TIMEPERIOD, NOOFCREDITSPERQUARTER, NOOFCREDITSPERMONTH, VALUEOFCLEANFUNDEDFACILITY, VAT_TURNOVER_DETAILS, VAT_TURNOVER_TIMEPERIOD, BANKINGTURNOVER, BANKINGTURNOVERFREQUENCY, POS_TURNOVER_AMOUNT, POS_TURNOVER_TIMEPERIOD, CHARGEBACKPERCENTAGE FROM USR_0_IRBL_FINANCIAL_ELIGIBILITY_CHECKS WITH(NOLOCK) WHERE WI_NAME = '"+getWorkitemName(iformObj)+"' ");
		for (List<String> row : FinDtls) {
			
			String TransDetailsXML = "<Transaction_Details><applicant_id></applicant_id><From_period_for_turnover></From_period_for_turnover><To_period_for_turnover></To_period_for_turnover><Annual_financial_turnover></Annual_financial_turnover><Frequency_AFT></Frequency_AFT><Net_Profit></Net_Profit><Total_Monthly_Credits></Total_Monthly_Credits><Average_Balance></Average_Balance><Frequency_AB></Frequency_AB><No_of_credits_per_quarter></No_of_credits_per_quarter><No_of_Credits_per_month></No_of_Credits_per_month><Value_of_Clean_funded_facility></Value_of_Clean_funded_facility><VAT_turnover></VAT_turnover><Frequency_VAT></Frequency_VAT><Banking_turnover></Banking_turnover><Frequency_BT></Frequency_BT><POS_Turnover></POS_Turnover><Frequency_POS></Frequency_POS><Charge_back_percentage></Charge_back_percentage></Transaction_Details>";
			
			StringBuffer DetailsB = new StringBuffer(TransDetailsXML);
			
			if(!"".equalsIgnoreCase(getControlValue("CIF_NUMBER",iformObj).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),getControlValue("CIF_NUMBER",iformObj).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<applicant_id>"), DetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
		
			if(!"".equalsIgnoreCase(row.get(0).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<From_period_for_turnover>")+"<From_period_for_turnover>".length(),row.get(0).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<From_period_for_turnover>"), DetailsB.indexOf("</From_period_for_turnover>")+"</From_period_for_turnover>".length());
			
			if(!"".equalsIgnoreCase(row.get(1).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<To_period_for_turnover>")+"<To_period_for_turnover>".length(),row.get(1).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<To_period_for_turnover>"), DetailsB.indexOf("</To_period_for_turnover>")+"</To_period_for_turnover>".length());
			
			if(!"".equalsIgnoreCase(row.get(2).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Annual_financial_turnover>")+"<Annual_financial_turnover>".length(),row.get(2).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Annual_financial_turnover>"), DetailsB.indexOf("</Annual_financial_turnover>")+"</Annual_financial_turnover>".length());
			
			if(!"".equalsIgnoreCase(row.get(3).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Frequency_AFT>")+"<Frequency_AFT>".length(),row.get(3).trim()); // was passing hardcoded 12 as per sameera mail subject 'Two important things to be fixed', later on it was asked to pass the value available on the workitem.
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Frequency_AFT>"), DetailsB.indexOf("</Frequency_AFT>")+"</Frequency_AFT>".length());
			
			if(!"".equalsIgnoreCase(row.get(4).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Net_Profit>")+"<Net_Profit>".length(),row.get(4).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Net_Profit>"), DetailsB.indexOf("</Net_Profit>")+"</Net_Profit>".length());
			
			if(!"".equalsIgnoreCase(row.get(5).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Total_Monthly_Credits>")+"<Total_Monthly_Credits>".length(),row.get(5).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Total_Monthly_Credits>"), DetailsB.indexOf("</Total_Monthly_Credits>")+"</Total_Monthly_Credits>".length());
			
			if(!"".equalsIgnoreCase(row.get(6).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Average_Balance>")+"<Average_Balance>".length(),row.get(6).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Average_Balance>"), DetailsB.indexOf("</Average_Balance>")+"</Average_Balance>".length());
			
			if(!"".equalsIgnoreCase(row.get(7).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Frequency_AB>")+"<Frequency_AB>".length(),row.get(7).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Frequency_AB>"), DetailsB.indexOf("</Frequency_AB>")+"</Frequency_AB>".length());
			
			if(!"".equalsIgnoreCase(row.get(8).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<No_of_credits_per_quarter>")+"<No_of_credits_per_quarter>".length(),row.get(8).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<No_of_credits_per_quarter>"), DetailsB.indexOf("</No_of_credits_per_quarter>")+"</No_of_credits_per_quarter>".length());
			
			if(!"".equalsIgnoreCase(row.get(9).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<No_of_Credits_per_month>")+"<No_of_Credits_per_month>".length(),row.get(9).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<No_of_Credits_per_month>"), DetailsB.indexOf("</No_of_Credits_per_month>")+"</No_of_Credits_per_month>".length());
			
			String CleanFundedFacility = "";
			String CompanyName = getControlValue("COMPANY_NAME",iformObj).trim();
			List<List<String>> tmp = iformObj.getDataFromDB("select top 1 TOTAL_FUNDED_FACILITY from USR_0_IRBL_EVAL_CHECKS_AECB_GRID_DTLS with(nolock) where WI_NAME = '"+getWorkitemName(iformObj)+"' and rtrim(ltrim(NAME)) = '"+CompanyName+"'");
			for (List<String> row1 : tmp) {
				if (!row1.get(0).equalsIgnoreCase(""))
				{
					CleanFundedFacility = row1.get(0).trim();
				}
			}
			
			if(!"".equalsIgnoreCase(CleanFundedFacility))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Value_of_Clean_funded_facility>")+"<Value_of_Clean_funded_facility>".length(),CleanFundedFacility);
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Value_of_Clean_funded_facility>"), DetailsB.indexOf("</Value_of_Clean_funded_facility>")+"</Value_of_Clean_funded_facility>".length());
			
			if(!"".equalsIgnoreCase(row.get(11).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<VAT_turnover>")+"<VAT_turnover>".length(),row.get(11).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<VAT_turnover>"), DetailsB.indexOf("</VAT_turnover>")+"</VAT_turnover>".length());
			
			if(!"".equalsIgnoreCase(row.get(12).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Frequency_VAT>")+"<Frequency_VAT>".length(),row.get(12).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Frequency_VAT>"), DetailsB.indexOf("</Frequency_VAT>")+"</Frequency_VAT>".length());
			
			if(!"".equalsIgnoreCase(row.get(13).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Banking_turnover>")+"<Banking_turnover>".length(),row.get(13).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Banking_turnover>"), DetailsB.indexOf("</Banking_turnover>")+"</Banking_turnover>".length());
			
			if(!"".equalsIgnoreCase(row.get(14).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Frequency_BT>")+"<Frequency_BT>".length(),row.get(14).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Frequency_BT>"), DetailsB.indexOf("</Frequency_BT>")+"</Frequency_BT>".length());
			
			if(!"".equalsIgnoreCase(row.get(15).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<POS_Turnover>")+"<POS_Turnover>".length(),row.get(15).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<POS_Turnover>"), DetailsB.indexOf("</POS_Turnover>")+"</POS_Turnover>".length());
			
			if(!"".equalsIgnoreCase(row.get(16).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Frequency_POS>")+"<Frequency_POS>".length(),row.get(16).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Frequency_POS>"), DetailsB.indexOf("</Frequency_POS>")+"</Frequency_POS>".length());
			
			if(!"".equalsIgnoreCase(row.get(17).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Charge_back_percentage>")+"<Charge_back_percentage>".length(),row.get(17).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Charge_back_percentage>"), DetailsB.indexOf("</Charge_back_percentage>")+"</Charge_back_percentage>".length());
			
			TransDetailsXML = DetailsB.toString();
			TransDetailsXMLMain = TransDetailsXML;
			break;
		}
		
		return TransDetailsXMLMain;
	}
	
	
	public String DecTechAssetDetailsXML(IFormReference iformObj,String control,String Data)
	{
		String AssetDetailsXMLMain = "";
		List<List<String>> AssetDtls = iformObj.getDataFromDB("SELECT APPLICANTID, DESCRIPTION, ASSETVALUE, REGISTRATIONDETAILS, ISNOTARISED, DOWNPAYMENT, ASSETTYPE, MODELYEAR FROM USR_0_IRBL_ASSET_DTLS WITH(NOLOCK) WHERE WI_NAME = '"+getWorkitemName(iformObj)+"'  ORDER BY insertionOrderId ");
		for (List<String> row : AssetDtls) {
			
			String AssetDetailsXML = "<Asset_Details><applicant_id></applicant_id><Description></Description><Asset_Value></Asset_Value><Registration_details></Registration_details><Is_Notarised></Is_Notarised><Down_Payment></Down_Payment><Asset_Type></Asset_Type><Model_Year></Model_Year></Asset_Details>";
			
			StringBuffer DetailsB = new StringBuffer(AssetDetailsXML);
					
			if(!"".equalsIgnoreCase(row.get(0).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),row.get(0).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<applicant_id>"), DetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
			
			if(!"".equalsIgnoreCase(row.get(1).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Description>")+"<Description>".length(),row.get(1).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Description>"), DetailsB.indexOf("</Description>")+"</Description>".length());
			
			if(!"".equalsIgnoreCase(row.get(2).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Asset_Value>")+"<Asset_Value>".length(),row.get(2).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Asset_Value>"), DetailsB.indexOf("</Asset_Value>")+"</Asset_Value>".length());
			
			if(!"".equalsIgnoreCase(row.get(3).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Registration_details>")+"<Registration_details>".length(),row.get(3).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Registration_details>"), DetailsB.indexOf("</Registration_details>")+"</Registration_details>".length());
			
			if(!"".equalsIgnoreCase(row.get(4).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Is_Notarised>")+"<Is_Notarised>".length(),row.get(4).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Is_Notarised>"), DetailsB.indexOf("</Is_Notarised>")+"</Is_Notarised>".length());
			
			if(!"".equalsIgnoreCase(row.get(5).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Down_Payment>")+"<Down_Payment>".length(),row.get(5).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Down_Payment>"), DetailsB.indexOf("</Down_Payment>")+"</Down_Payment>".length());
			
			if(!"".equalsIgnoreCase(row.get(6).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Asset_Type>")+"<Asset_Type>".length(),row.get(6).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Asset_Type>"), DetailsB.indexOf("</Asset_Type>")+"</Asset_Type>".length());
			
			if(!"".equalsIgnoreCase(row.get(7).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Model_Year>")+"<Model_Year>".length(),row.get(7).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Model_Year>"), DetailsB.indexOf("</Model_Year>")+"</Model_Year>".length());
								
			AssetDetailsXML = DetailsB.toString();
			AssetDetailsXMLMain = AssetDetailsXMLMain+AssetDetailsXML;
		}
		
		return AssetDetailsXMLMain;
	}
	
	
	public String DecTechCompanyGuarantorsXML(IFormReference iformObj,String control,String Data)
	{
		String CompanyGuarantorsXMLMain = "";
		List<List<String>> GuarantorLst = iformObj.getDataFromDB("SELECT CIF, GUARANTORYCATEGORY, NAME_OF_SISTER_COMPANY, FORMAT(DATEOFINCORPORATION, 'yyyy-MM-dd') AS DATEOFINCORPORATION, LINEOFBUSINESS, COUNTRY, ISSUINGEMIRATE, GENDER, FORMAT(DATEOFBIRTH, 'yyyy-MM-dd') AS DATEOFBIRTH, NATIONALITY, COUNTRYOFRESIDENCE, PASSPORTNUMBER, FORMAT(PASSPORTEXPIRYDATE, 'yyyy-MM-dd') AS PASSPORTEXPIRYDATE, PASSPORTISSUINGCOUNTRY, COUNTRYOFBIRTH, EMIRATESID FROM USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS WITH(nolock) WHERE WI_NAME = '"+getWorkitemName(iformObj)+"' AND RELATIONSHIPTYPE = 'GUARANTOR' ORDER BY insertionOrderId");
		
		for (List<String> row : GuarantorLst) {
					
			String CompanyGuarantorsXML = "<Guarantor><applicant_id></applicant_id><Guarantory_Category></Guarantory_Category><Gurantor_Name></Gurantor_Name><Date_of_Incorporation></Date_of_Incorporation><Line_of_Business></Line_of_Business><Country_residence_Incorporation></Country_residence_Incorporation><Issuing_Emirate></Issuing_Emirate><Gender></Gender><Date_of_Birth></Date_of_Birth><Nationality></Nationality><Country_of_residence></Country_of_residence><Passport_number></Passport_number><Passport_Expiry_date></Passport_Expiry_date><Passport_Issuing_Country></Passport_Issuing_Country><Country_of_Birth></Country_of_Birth><EID></EID></Guarantor>";
			
			StringBuffer DetailsB = new StringBuffer(CompanyGuarantorsXML);
			
			if(!"".equalsIgnoreCase(row.get(0).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),row.get(0).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<applicant_id>"), DetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
			
			if(!"".equalsIgnoreCase(row.get(1).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Guarantory_Category>")+"<Guarantory_Category>".length(),row.get(1).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Guarantory_Category>"), DetailsB.indexOf("</Guarantory_Category>")+"</Guarantory_Category>".length());
			
			if(!"".equalsIgnoreCase(row.get(2).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Gurantor_Name>")+"<Gurantor_Name>".length(),row.get(2).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Gurantor_Name>"), DetailsB.indexOf("</Gurantor_Name>")+"</Gurantor_Name>".length());
			
			if(!"".equalsIgnoreCase(row.get(3).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Date_of_Incorporation>")+"<Date_of_Incorporation>".length(),row.get(3).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Date_of_Incorporation>"), DetailsB.indexOf("</Date_of_Incorporation>")+"</Date_of_Incorporation>".length());
					
			if(!"".equalsIgnoreCase(row.get(4).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Line_of_Business>")+"<Line_of_Business>".length(),row.get(4).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Line_of_Business>"), DetailsB.indexOf("</Line_of_Business>")+"</Line_of_Business>".length());
			
			if(!"".equalsIgnoreCase(row.get(5).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Country_residence_Incorporation>")+"<Country_residence_Incorporation>".length(),row.get(5).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Country_residence_Incorporation>"), DetailsB.indexOf("</Country_residence_Incorporation>")+"</Country_residence_Incorporation>".length());
			
			if(!"".equalsIgnoreCase(row.get(6).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Issuing_Emirate>")+"<Issuing_Emirate>".length(),row.get(6).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Issuing_Emirate>"), DetailsB.indexOf("</Issuing_Emirate>")+"</Issuing_Emirate>".length());
			
			if(!"".equalsIgnoreCase(row.get(7).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Gender>")+"<Gender>".length(),row.get(7).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Gender>"), DetailsB.indexOf("</Gender>")+"</Gender>".length());
			
			if(!"".equalsIgnoreCase(row.get(8).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Date_of_Birth>")+"<Date_of_Birth>".length(),row.get(8).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Date_of_Birth>"), DetailsB.indexOf("</Date_of_Birth>")+"</Date_of_Birth>".length());
			
			if(!"".equalsIgnoreCase(row.get(9).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Nationality>")+"<Nationality>".length(),row.get(9).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Nationality>"), DetailsB.indexOf("</Nationality>")+"</Nationality>".length());
			
			if(!"".equalsIgnoreCase(row.get(10).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Country_of_residence>")+"<Country_of_residence>".length(),row.get(10).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Country_of_residence>"), DetailsB.indexOf("</Country_of_residence>")+"</Country_of_residence>".length());
			
			if(!"".equalsIgnoreCase(row.get(11).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Passport_number>")+"<Passport_number>".length(),row.get(11).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Passport_number>"), DetailsB.indexOf("</Passport_number>")+"</Passport_number>".length());
			
			if(!"".equalsIgnoreCase(row.get(12).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Passport_Expiry_date>")+"<Passport_Expiry_date>".length(),row.get(12).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Passport_Expiry_date>"), DetailsB.indexOf("</Passport_Expiry_date>")+"</Passport_Expiry_date>".length());
			
			if(!"".equalsIgnoreCase(row.get(13).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Passport_Issuing_Country>")+"<Passport_Issuing_Country>".length(),row.get(13).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Passport_Issuing_Country>"), DetailsB.indexOf("</Passport_Issuing_Country>")+"</Passport_Issuing_Country>".length());
			
			if(!"".equalsIgnoreCase(row.get(14).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<Country_of_Birth>")+"<Country_of_Birth>".length(),row.get(14).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<Country_of_Birth>"), DetailsB.indexOf("</Country_of_Birth>")+"</Country_of_Birth>".length());
			
			if(!"".equalsIgnoreCase(row.get(15).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<EID>")+"<EID>".length(),row.get(15).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<EID>"), DetailsB.indexOf("</EID>")+"</EID>".length());
			
			CompanyGuarantorsXML = DetailsB.toString();
			CompanyGuarantorsXMLMain = CompanyGuarantorsXMLMain + CompanyGuarantorsXML;
		}
		
		return CompanyGuarantorsXMLMain;
	}
	
	public String DecTechInternalBureauDataXML(IFormReference iformObj,String control,String Data)
	{
		String InternalBureauDataXMLMain = "";
		String InternalBureauXMLMain = InternalBureauData(iformObj, control, Data);
		/*List<List<String>> GuarantorLst = iformObj.getDataFromDB("SELECT ci.CifId, ci.FullNm, isNull((Sum(Abs(convert(float,replace([ci.TotalOutstanding],'NA','0'))))),0), isNull((Sum(Abs(convert(float,replace([ci.TotalOverdue],'NA','0'))))),0), ci.NoOfContracts, ci.TotalExposure, (SELECT COMPANYFLAG  FROM USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS WITH(nolock) WHERE CIF=ci.CifId AND Wi_Name = '"+getWorkitemName(iformObj)+"') AS COMPANYFLAG FROM USR_0_iRBL_InternalExpo_CustInfo ci WITH(nolock) WHERE ci.Wi_Name = '"+getWorkitemName(iformObj)+"' ");
		
		for (List<String> row : GuarantorLst) {
					
			String InternalBureauXML = "<InternalBureau><applicant_id></applicant_id><full_name></full_name><total_out_bal></total_out_bal><total_overdue></total_overdue><no_default_contract></no_default_contract><total_exposure></total_exposure><worst_curr_pay></worst_curr_pay><worst_curr_pay_24></worst_curr_pay_24><worst_status_24></worst_status_24><no_of_rec></no_of_rec><cheque_return_3mon></cheque_return_3mon><dds_return_3mon></dds_return_3mon><cheque_return_6mon></cheque_return_6mon><dds_return_6mon></dds_return_6mon><internal_charge_off></internal_charge_off><cards_b_score></cards_b_score><company_flag></company_flag></InternalBureau>";
			
			StringBuffer DetailsB = new StringBuffer(InternalBureauXML);
			
			if(!row.get(0).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),row.get(0));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<applicant_id>"), DetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
			
			if(!row.get(1).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<full_name>")+"<full_name>".length(),row.get(1));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<full_name>"), DetailsB.indexOf("</full_name>")+"</full_name>".length());
			
			if(!row.get(2).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<total_out_bal>")+"<total_out_bal>".length(),row.get(2));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<total_out_bal>"), DetailsB.indexOf("</total_out_bal>")+"</total_out_bal>".length());
			
			if(!row.get(3).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<total_overdue>")+"<total_overdue>".length(),row.get(3));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<total_overdue>"), DetailsB.indexOf("</total_overdue>")+"</total_overdue>".length());
					
			if(!row.get(4).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<no_default_contract>")+"<no_default_contract>".length(),row.get(4));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<no_default_contract>"), DetailsB.indexOf("</no_default_contract>")+"</no_default_contract>".length());
			
			if(!row.get(5).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<total_exposure>")+"<total_exposure>".length(),row.get(5));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<total_exposure>"), DetailsB.indexOf("</total_exposure>")+"</total_exposure>".length());
			
			if(!row.get(6).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<company_flag>")+"<company_flag>".length(),row.get(6));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<company_flag>"), DetailsB.indexOf("</company_flag>")+"</company_flag>".length());
			
			InternalBureauXML = DetailsB.toString();
			InternalBureauXMLMain = InternalBureauXMLMain + InternalBureauXML;
		}*/
		
		
		String InternalBouncedChequesXMLMain = InternalBouncedCheques(iformObj, control, Data);

		
		
		// Internal Bureau Cards
		String InternalBureauIndProdCardsXMLMain = "";
		List<List<String>> GuarantorLst = iformObj.getDataFromDB("SELECT CifId, CardEmbossNum, CardType, cardstatus, CustRoleType, CardsApplcnRecvdDate, ExpiryDate, OutstandingAmt, WriteoffStat, WriteoffStatDt, CreditLimit, OverdueAmt, NofDaysPmtDelay, MonthsOnBook, LastRepmtDt, CurrentlyCurrent, DPD30Last6Months, SchemeCardProd, MarketingCode, CARD_expiry_date, General_Status, PaymentsAmount, OutstandingAmt, DPD_30_in_last_3_months, DPD_30_in_last_6_months, DPD_30_in_last_9_months, DPD_30_in_last_12_months, DPD_30_in_last_18_months, DPD_30_in_last_24_months, DPD_60_in_last_3_months, DPD_60_in_last_6_months, DPD_60_in_last_9_months, DPD_60_in_last_12_months, DPD_60_in_last_18_months, DPD_60_in_last_24_months, DPD_90_in_last_3_months, DPD_90_in_last_6_months, DPD_90_in_last_9_months, DPD_90_in_last_12_months, DPD_90_in_last_18_months, DPD_90_in_last_24_months, DPD_120_in_last_3_months, DPD_120_in_last_6_months, DPD_120_in_last_9_months, DPD_120_in_last_12_months, DPD_120_in_last_18_months, DPD_120_in_last_24_months, DPD_150_in_last_3_months, DPD_150_in_last_6_months, DPD_150_in_last_9_months, DPD_150_in_last_12_months, DPD_150_in_last_18_months, DPD_150_in_last_24_months, DPD_180_in_last_3_months, DPD_180_in_last_6_months, DPD_180_in_last_9_months, DPD_180_in_last_12_months, DPD_180_in_last_18_months, DPD_180_in_last_24_months, Internal_WriteOff_Check, PaymentsAmount  FROM USR_0_iRBL_InternalExpo_CardDetails WHERE Wi_Name = '"+getWorkitemName(iformObj)+"' ");
		
		for (List<String> row : GuarantorLst) {
					
			String InternalBureauIndProdCardsXML = "<InternalBureauIndividualProducts><applicant_id></applicant_id><internal_bureau_individual_products_id></internal_bureau_individual_products_id><type_product>CARDS</type_product><contract_type></contract_type><provider_no>RAKBANK</provider_no><phase></phase><role_of_customer></role_of_customer><start_date></start_date><close_date></close_date><outstanding_balance></outstanding_balance> <total_amount></total_amount><payments_amount></payments_amount><worst_status></worst_status><worst_status_date></worst_status_date><credit_limit></credit_limit><overdue_amount></overdue_amount><no_of_days_payment_delay></no_of_days_payment_delay><mob></mob><last_repayment_date></last_repayment_date><currently_current></currently_current><dpd_30_last_6_mon></dpd_30_last_6_mon><card_product></card_product><marketing_code></marketing_code><card_expiry_date></card_expiry_date><general_status></general_status><consider_for_obligation>Y</consider_for_obligation><role>Primary</role><emi></emi><os_amt></os_amt><dpd_30_in_last_3mon></dpd_30_in_last_3mon><dpd_30_in_last_6mon></dpd_30_in_last_6mon><dpd_30_in_last_9mon></dpd_30_in_last_9mon><dpd_30_in_last_12mon></dpd_30_in_last_12mon><dpd_30_in_last_18mon></dpd_30_in_last_18mon><dpd_30_in_last_24mon></dpd_30_in_last_24mon><dpd_60_in_last_3mon></dpd_60_in_last_3mon><dpd_60_in_last_6mon></dpd_60_in_last_6mon><dpd_60_in_last_9mon></dpd_60_in_last_9mon><dpd_60_in_last_12mon></dpd_60_in_last_12mon><dpd_60_in_last_18mon></dpd_60_in_last_18mon><dpd_60_in_last_24mon></dpd_60_in_last_24mon><dpd_90_in_last_3mon></dpd_90_in_last_3mon><dpd_90_in_last_6mon></dpd_90_in_last_6mon><dpd_90_in_last_9mon></dpd_90_in_last_9mon><dpd_90_in_last_12mon></dpd_90_in_last_12mon><dpd_90_in_last_18mon></dpd_90_in_last_18mon><dpd_90_in_last_24mon></dpd_90_in_last_24mon><dpd_120_in_last_3mon></dpd_120_in_last_3mon><dpd_120_in_last_6mon></dpd_120_in_last_6mon><dpd_120_in_last_9mon></dpd_120_in_last_9mon><dpd_120_in_last_12mon></dpd_120_in_last_12mon><dpd_120_in_last_18mon></dpd_120_in_last_18mon><dpd_120_in_last_24mon></dpd_120_in_last_24mon><dpd_150_in_last_3mon></dpd_150_in_last_3mon><dpd_150_in_last_6mon></dpd_150_in_last_6mon><dpd_150_in_last_9mon></dpd_150_in_last_9mon><dpd_150_in_last_12mon></dpd_150_in_last_12mon><dpd_150_in_last_18mon></dpd_150_in_last_18mon><dpd_150_in_last_24mon></dpd_150_in_last_24mon><dpd_180_in_last_3mon></dpd_180_in_last_3mon><dpd_180_in_last_6mon></dpd_180_in_last_6mon><dpd_180_in_last_9mon></dpd_180_in_last_9mon><dpd_180_in_last_12mon></dpd_180_in_last_12mon><dpd_180_in_last_18mon></dpd_180_in_last_18mon><dpd_180_in_last_24mon></dpd_180_in_last_24mon><write_off_amount></write_off_amount><company_flag></company_flag></InternalBureauIndividualProducts>";
			
			StringBuffer DetailsB = new StringBuffer(InternalBureauIndProdCardsXML);
			
			if(!"".equalsIgnoreCase(row.get(0).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),row.get(0).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<applicant_id>"), DetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
			
			if(!"".equalsIgnoreCase(row.get(1).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<internal_bureau_individual_products_id>")+"<internal_bureau_individual_products_id>".length(),row.get(1).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<internal_bureau_individual_products_id>"), DetailsB.indexOf("</internal_bureau_individual_products_id>")+"</internal_bureau_individual_products_id>".length());
			
			if(!"".equalsIgnoreCase(row.get(2).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<contract_type>")+"<contract_type>".length(),row.get(2).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<contract_type>"), DetailsB.indexOf("</contract_type>")+"</contract_type>".length());
			
			if(!"".equalsIgnoreCase(row.get(3).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<phase>")+"<phase>".length(),row.get(3).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<phase>"), DetailsB.indexOf("</phase>")+"</phase>".length());
			
			if(!"".equalsIgnoreCase(row.get(4).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<role_of_customer>")+"<role_of_customer>".length(),row.get(4).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<role_of_customer>"), DetailsB.indexOf("</role_of_customer>")+"</role_of_customer>".length());
			
			if(!"".equalsIgnoreCase(row.get(5).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<start_date>")+"<start_date>".length(),row.get(5).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<start_date>"), DetailsB.indexOf("</start_date>")+"</start_date>".length());

			if(!"".equalsIgnoreCase(row.get(6).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<close_date>")+"<close_date>".length(),row.get(6).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<close_date>"), DetailsB.indexOf("</close_date>")+"</close_date>".length());
			
			if(!"".equalsIgnoreCase(row.get(7).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<outstanding_balance>")+"<outstanding_balance>".length(),row.get(7).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<outstanding_balance>"), DetailsB.indexOf("</outstanding_balance>")+"</outstanding_balance>".length());
			
			if(!"".equalsIgnoreCase(row.get(8).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<worst_status>")+"<worst_status>".length(),row.get(8).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<worst_status>"), DetailsB.indexOf("</worst_status>")+"</worst_status>".length());
			
			if(!"".equalsIgnoreCase(row.get(9).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<worst_status_date>")+"<worst_status_date>".length(),row.get(9).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<worst_status_date>"), DetailsB.indexOf("</worst_status_date>")+"</worst_status_date>".length());
			
			if(!"".equalsIgnoreCase(row.get(10).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<total_amount>")+"<total_amount>".length(),row.get(10).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<total_amount>"), DetailsB.indexOf("</total_amount>")+"</total_amount>".length());
			
			if(!"".equalsIgnoreCase(row.get(10).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<credit_limit>")+"<credit_limit>".length(),row.get(10).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<credit_limit>"), DetailsB.indexOf("</credit_limit>")+"</credit_limit>".length());
			
			if(!"".equalsIgnoreCase(row.get(11).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<overdue_amount>")+"<overdue_amount>".length(),row.get(11).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<overdue_amount>"), DetailsB.indexOf("</overdue_amount>")+"</overdue_amount>".length());
			
			if(!"".equalsIgnoreCase(row.get(12).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<no_of_days_payment_delay>")+"<no_of_days_payment_delay>".length(),row.get(12).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<no_of_days_payment_delay>"), DetailsB.indexOf("</no_of_days_payment_delay>")+"</no_of_days_payment_delay>".length());
			
			if(!"".equalsIgnoreCase(row.get(13).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<mob>")+"<mob>".length(),row.get(13).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<mob>"), DetailsB.indexOf("</mob>")+"</mob>".length());
			
			if(!"".equalsIgnoreCase(row.get(14).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<last_repayment_date>")+"<last_repayment_date>".length(),row.get(14).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<last_repayment_date>"), DetailsB.indexOf("</last_repayment_date>")+"</last_repayment_date>".length());
			
			if(!"".equalsIgnoreCase(row.get(15).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<currently_current>")+"<currently_current>".length(),row.get(15).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<currently_current>"), DetailsB.indexOf("</currently_current>")+"</currently_current>".length());
			
			if(!"".equalsIgnoreCase(row.get(16).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_last_6_mon>")+"<dpd_30_last_6_mon>".length(),row.get(16).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_last_6_mon>"), DetailsB.indexOf("</dpd_30_last_6_mon>")+"</dpd_30_last_6_mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(17).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<card_product>")+"<card_product>".length(),row.get(17).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<card_product>"), DetailsB.indexOf("</card_product>")+"</card_product>".length());
			
			if(!"".equalsIgnoreCase(row.get(18).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<marketing_code>")+"<marketing_code>".length(),row.get(18).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<marketing_code>"), DetailsB.indexOf("</marketing_code>")+"</marketing_code>".length());
			
			if(!"".equalsIgnoreCase(row.get(19).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<card_expiry_date>")+"<card_expiry_date>".length(),row.get(19).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<card_expiry_date>"), DetailsB.indexOf("</card_expiry_date>")+"</card_expiry_date>".length());
			
			if(!"".equalsIgnoreCase(row.get(20).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<general_status>")+"<general_status>".length(),row.get(20).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<general_status>"), DetailsB.indexOf("</general_status>")+"</general_status>".length());
			
			if(!"".equalsIgnoreCase(row.get(21)))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<emi>")+"<emi>".length(),row.get(21).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<emi>"), DetailsB.indexOf("</emi>")+"</emi>".length());
			
			if(!"".equalsIgnoreCase(row.get(22).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<os_amt>")+"<os_amt>".length(),row.get(22).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<os_amt>"), DetailsB.indexOf("</os_amt>")+"</os_amt>".length());
			
			if(!"".equalsIgnoreCase(row.get(23).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_3mon>")+"<dpd_30_in_last_3mon>".length(),row.get(23).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_3mon>"), DetailsB.indexOf("</dpd_30_in_last_3mon>")+"</dpd_30_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(24).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_6mon>")+"<dpd_30_in_last_6mon>".length(),row.get(24).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_6mon>"), DetailsB.indexOf("</dpd_30_in_last_6mon>")+"</dpd_30_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(25).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_9mon>")+"<dpd_30_in_last_9mon>".length(),row.get(25).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_9mon>"), DetailsB.indexOf("</dpd_30_in_last_9mon>")+"</dpd_30_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(26).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_12mon>")+"<dpd_30_in_last_12mon>".length(),row.get(26).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_12mon>"), DetailsB.indexOf("</dpd_30_in_last_12mon>")+"</dpd_30_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(27).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_18mon>")+"<dpd_30_in_last_18mon>".length(),row.get(27).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_18mon>"), DetailsB.indexOf("</dpd_30_in_last_18mon>")+"</dpd_30_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(28).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_24mon>")+"<dpd_30_in_last_24mon>".length(),row.get(28).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_24mon>"), DetailsB.indexOf("</dpd_30_in_last_24mon>")+"</dpd_30_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(29).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_3mon>")+"<dpd_60_in_last_3mon>".length(),row.get(29).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_3mon>"), DetailsB.indexOf("</dpd_60_in_last_3mon>")+"</dpd_60_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(30).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_6mon>")+"<dpd_60_in_last_6mon>".length(),row.get(30).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_6mon>"), DetailsB.indexOf("</dpd_60_in_last_6mon>")+"</dpd_60_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(31).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_9mon>")+"<dpd_60_in_last_9mon>".length(),row.get(31).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_9mon>"), DetailsB.indexOf("</dpd_60_in_last_9mon>")+"</dpd_60_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(32).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_12mon>")+"<dpd_60_in_last_12mon>".length(),row.get(32).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_12mon>"), DetailsB.indexOf("</dpd_60_in_last_12mon>")+"</dpd_60_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(33).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_18mon>")+"<dpd_60_in_last_18mon>".length(),row.get(33).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_18mon>"), DetailsB.indexOf("</dpd_60_in_last_18mon>")+"</dpd_60_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(34).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_24mon>")+"<dpd_60_in_last_24mon>".length(),row.get(34).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_24mon>"), DetailsB.indexOf("</dpd_60_in_last_24mon>")+"</dpd_60_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(35).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_3mon>")+"<dpd_90_in_last_3mon>".length(),row.get(35).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_3mon>"), DetailsB.indexOf("</dpd_90_in_last_3mon>")+"</dpd_90_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(36).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_6mon>")+"<dpd_90_in_last_6mon>".length(),row.get(36).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_6mon>"), DetailsB.indexOf("</dpd_90_in_last_6mon>")+"</dpd_90_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(37).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_9mon>")+"<dpd_90_in_last_9mon>".length(),row.get(37).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_9mon>"), DetailsB.indexOf("</dpd_90_in_last_9mon>")+"</dpd_90_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(38).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_12mon>")+"<dpd_90_in_last_12mon>".length(),row.get(38).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_12mon>"), DetailsB.indexOf("</dpd_90_in_last_12mon>")+"</dpd_90_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(39).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_18mon>")+"<dpd_90_in_last_18mon>".length(),row.get(39).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_18mon>"), DetailsB.indexOf("</dpd_90_in_last_18mon>")+"</dpd_90_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(40).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_24mon>")+"<dpd_90_in_last_24mon>".length(),row.get(40).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_24mon>"), DetailsB.indexOf("</dpd_90_in_last_24mon>")+"</dpd_90_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(41).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_3mon>")+"<dpd_120_in_last_3mon>".length(),row.get(41).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_3mon>"), DetailsB.indexOf("</dpd_120_in_last_3mon>")+"</dpd_120_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(42).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_6mon>")+"<dpd_120_in_last_6mon>".length(),row.get(42).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_6mon>"), DetailsB.indexOf("</dpd_120_in_last_6mon>")+"</dpd_120_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(43).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_9mon>")+"<dpd_120_in_last_9mon>".length(),row.get(43).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_9mon>"), DetailsB.indexOf("</dpd_120_in_last_9mon>")+"</dpd_120_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(44).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_12mon>")+"<dpd_120_in_last_12mon>".length(),row.get(44).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_12mon>"), DetailsB.indexOf("</dpd_120_in_last_12mon>")+"</dpd_120_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(45).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_18mon>")+"<dpd_120_in_last_18mon>".length(),row.get(45).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_18mon>"), DetailsB.indexOf("</dpd_120_in_last_18mon>")+"</dpd_120_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(46).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_24mon>")+"<dpd_120_in_last_24mon>".length(),row.get(46).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_24mon>"), DetailsB.indexOf("</dpd_120_in_last_24mon>")+"</dpd_120_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(47).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_3mon>")+"<dpd_150_in_last_3mon>".length(),row.get(47).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_3mon>"), DetailsB.indexOf("</dpd_150_in_last_3mon>")+"</dpd_150_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(48).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_6mon>")+"<dpd_150_in_last_6mon>".length(),row.get(48).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_6mon>"), DetailsB.indexOf("</dpd_150_in_last_6mon>")+"</dpd_150_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(49).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_9mon>")+"<dpd_150_in_last_9mon>".length(),row.get(49).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_9mon>"), DetailsB.indexOf("</dpd_150_in_last_9mon>")+"</dpd_150_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(50).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_12mon>")+"<dpd_150_in_last_12mon>".length(),row.get(50).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_12mon>"), DetailsB.indexOf("</dpd_150_in_last_12mon>")+"</dpd_150_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(51).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_18mon>")+"<dpd_150_in_last_18mon>".length(),row.get(51).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_18mon>"), DetailsB.indexOf("</dpd_150_in_last_18mon>")+"</dpd_150_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(52).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_24mon>")+"<dpd_150_in_last_24mon>".length(),row.get(52).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_24mon>"), DetailsB.indexOf("</dpd_150_in_last_24mon>")+"</dpd_150_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(53).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_3mon>")+"<dpd_180_in_last_3mon>".length(),row.get(53).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_3mon>"), DetailsB.indexOf("</dpd_180_in_last_3mon>")+"</dpd_180_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(54).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_6mon>")+"<dpd_180_in_last_6mon>".length(),row.get(54).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_6mon>"), DetailsB.indexOf("</dpd_180_in_last_6mon>")+"</dpd_180_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(55).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_9mon>")+"<dpd_180_in_last_9mon>".length(),row.get(55).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_9mon>"), DetailsB.indexOf("</dpd_180_in_last_9mon>")+"</dpd_180_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(56).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_12mon>")+"<dpd_180_in_last_12mon>".length(),row.get(56).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_12mon>"), DetailsB.indexOf("</dpd_180_in_last_12mon>")+"</dpd_180_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(57).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_18mon>")+"<dpd_180_in_last_18mon>".length(),row.get(57).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_18mon>"), DetailsB.indexOf("</dpd_180_in_last_18mon>")+"</dpd_180_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(58).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_24mon>")+"<dpd_180_in_last_24mon>".length(),row.get(58).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_24mon>"), DetailsB.indexOf("</dpd_180_in_last_24mon>")+"</dpd_180_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(59).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<write_off_amount>")+"<write_off_amount>".length(),row.get(59).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<write_off_amount>"), DetailsB.indexOf("</write_off_amount>")+"</write_off_amount>".length());
			
			String CompanyFlag = "";
			if (row.get(0).trim() != null && !"".equalsIgnoreCase(row.get(0).trim()) && !"null".equalsIgnoreCase(row.get(0).trim())) 
			{
				CompanyFlag = "N";
				if(row.get(0).trim().equalsIgnoreCase(getControlValue("CIF_NUMBER",iformObj).trim()))
					CompanyFlag = "Y";
				else 
				{
					String qry = "SELECT top 1 COMPANYFLAG FROM USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS WITH(nolock) WHERE CIF='"+row.get(0).trim()+"' AND Wi_Name='"+getWorkitemName(iformObj)+"'";
					List<List<String>> CmpFlgQry = iformObj.getDataFromDB(qry);
					for (int k = 0; k < CmpFlgQry.size(); k++) {
						CompanyFlag = CmpFlgQry.get(k).get(0);
						break;
					}
				}
			}
			
			
			if(!"".equalsIgnoreCase(CompanyFlag))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<company_flag>")+"<company_flag>".length(),CompanyFlag);
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<company_flag>"), DetailsB.indexOf("</company_flag>")+"</company_flag>".length());
			
			if(!"".equalsIgnoreCase(row.get(60).trim())) // need to check this field
				DetailsB = DetailsB.insert(DetailsB.indexOf("<payments_amount>")+"<payments_amount>".length(),row.get(60).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<payments_amount>"), DetailsB.indexOf("</payments_amount>")+"</payments_amount>".length());
						
			InternalBureauIndProdCardsXML = DetailsB.toString();
			InternalBureauIndProdCardsXMLMain = InternalBureauIndProdCardsXMLMain + InternalBureauIndProdCardsXML;
		}	
		/////////////////////////////////////////		
		
		
		// Internal Bureau Loans
		String InternalBureauIndProdLoansXMLMain = "";
		GuarantorLst = iformObj.getDataFromDB("SELECT CifId, AgreementId, LoanType, LoanStat, CustRoleType, Loan_Start_Date, LoanMaturityDate, LoanApprovedDate, OutstandingAmt, TotalLoanAmount, PaymentsAmt, TotalNoOfInstalments, RemainingInstalments, WriteoffStat, WriteoffStatDt, CreditLimit, OverdueAmt, NofDaysPmtDelay, MonthsOnBook, LastRepmtDt, CurrentlyCurrentFlg, SchemeCardProd, PropertyValue, Loan_disbursal_date, MarketingCode, (CAST(TotalNoOfInstalments AS INT)-CAST(RemainingInstalments AS INT)) AS RepaymentDone, General_Status, PaymentsAmt, OutstandingAmt, DPD_30_in_last_3_months, DPD_30_in_last_6_months, DPD_30_in_last_9_months, DPD_30_in_last_12_months, DPD_30_in_last_18_months, DPD_30_in_last_24_months, DPD_60_in_last_3_months, DPD_60_in_last_6_months, DPD_60_in_last_9_months, DPD_60_in_last_12_months, DPD_60_in_last_18_months, DPD_60_in_last_24_months, DPD_90_in_last_3_months, DPD_90_in_last_6_months, DPD_90_in_last_9_months, DPD_90_in_last_12_months, DPD_90_in_last_18_months, DPD_90_in_last_24_months, DPD_120_in_last_3_months, DPD_120_in_last_6_months, DPD_120_in_last_9_months, DPD_120_in_last_12_months, DPD_120_in_last_18_months, DPD_120_in_last_24_months, DPD_150_in_last_3_months, DPD_150_in_last_6_months, DPD_150_in_last_9_months, DPD_150_in_last_12_months, DPD_150_in_last_18_months, DPD_150_in_last_24_months, DPD_180_in_last_3_months, DPD_180_in_last_6_months, DPD_180_in_last_9_months, DPD_180_in_last_12_months, DPD_180_in_last_18_months, DPD_180_in_last_24_months, Internal_WriteOff_Check FROM USR_0_iRBL_InternalExpo_LoanDetails with(nolock) WHERE LoanStat not in ('Pipeline','CAS-Pipeline') AND Wi_Name = '"+getWorkitemName(iformObj)+"' ");
		
		for (List<String> row : GuarantorLst) {
					
			String InternalBureauIndProdLoansXML = "<InternalBureauIndividualProducts><applicant_id></applicant_id><internal_bureau_individual_products_id></internal_bureau_individual_products_id><linked_liability>NA</linked_liability><type_product>LOANS</type_product><contract_type></contract_type><provider_no>RAKBANK</provider_no><phase></phase><role_of_customer></role_of_customer><start_date></start_date><close_date></close_date><approved_date></approved_date><outstanding_balance></outstanding_balance><total_amount></total_amount><payments_amount></payments_amount><total_no_of_instalments></total_no_of_instalments><no_of_remaining_instalments></no_of_remaining_instalments><worst_status></worst_status><worst_status_date></worst_status_date><credit_limit></credit_limit><overdue_amount></overdue_amount><no_of_days_payment_delay></no_of_days_payment_delay><mob></mob><last_repayment_date></last_repayment_date><currently_current></currently_current><card_product></card_product><property_value></property_value><disbursal_date></disbursal_date><marketing_code></marketing_code><no_of_repayments_done></no_of_repayments_done><general_status></general_status><consider_for_obligation>Y</consider_for_obligation><role>Primary</role><emi></emi><os_amt></os_amt><dpd_30_in_last_3mon></dpd_30_in_last_3mon><dpd_30_in_last_6mon></dpd_30_in_last_6mon><dpd_30_in_last_9mon></dpd_30_in_last_9mon><dpd_30_in_last_12mon></dpd_30_in_last_12mon><dpd_30_in_last_18mon></dpd_30_in_last_18mon><dpd_30_in_last_24mon></dpd_30_in_last_24mon><dpd_60_in_last_3mon></dpd_60_in_last_3mon><dpd_60_in_last_6mon></dpd_60_in_last_6mon><dpd_60_in_last_9mon></dpd_60_in_last_9mon><dpd_60_in_last_12mon></dpd_60_in_last_12mon><dpd_60_in_last_18mon></dpd_60_in_last_18mon><dpd_60_in_last_24mon></dpd_60_in_last_24mon><dpd_90_in_last_3mon></dpd_90_in_last_3mon><dpd_90_in_last_6mon></dpd_90_in_last_6mon><dpd_90_in_last_9mon></dpd_90_in_last_9mon><dpd_90_in_last_12mon></dpd_90_in_last_12mon><dpd_90_in_last_18mon></dpd_90_in_last_18mon><dpd_90_in_last_24mon></dpd_90_in_last_24mon><dpd_120_in_last_3mon></dpd_120_in_last_3mon><dpd_120_in_last_6mon></dpd_120_in_last_6mon><dpd_120_in_last_9mon></dpd_120_in_last_9mon><dpd_120_in_last_12mon></dpd_120_in_last_12mon><dpd_120_in_last_18mon></dpd_120_in_last_18mon><dpd_120_in_last_24mon></dpd_120_in_last_24mon><dpd_150_in_last_3mon></dpd_150_in_last_3mon><dpd_150_in_last_6mon></dpd_150_in_last_6mon><dpd_150_in_last_9mon></dpd_150_in_last_9mon><dpd_150_in_last_12mon></dpd_150_in_last_12mon><dpd_150_in_last_18mon></dpd_150_in_last_18mon><dpd_150_in_last_24mon></dpd_150_in_last_24mon><dpd_180_in_last_3mon></dpd_180_in_last_3mon><dpd_180_in_last_6mon></dpd_180_in_last_6mon><dpd_180_in_last_9mon></dpd_180_in_last_9mon><dpd_180_in_last_12mon></dpd_180_in_last_12mon><dpd_180_in_last_18mon></dpd_180_in_last_18mon><dpd_180_in_last_24mon></dpd_180_in_last_24mon><write_off_amount></write_off_amount><company_flag></company_flag></InternalBureauIndividualProducts>";
			
			StringBuffer DetailsB = new StringBuffer(InternalBureauIndProdLoansXML);
			
			if(!"".equalsIgnoreCase(row.get(0).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),row.get(0).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<applicant_id>"), DetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
			
			if(!"".equalsIgnoreCase(row.get(1).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<internal_bureau_individual_products_id>")+"<internal_bureau_individual_products_id>".length(),row.get(1).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<internal_bureau_individual_products_id>"), DetailsB.indexOf("</internal_bureau_individual_products_id>")+"</internal_bureau_individual_products_id>".length());
			
			if(!"".equalsIgnoreCase(row.get(2).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<contract_type>")+"<contract_type>".length(),row.get(2).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<contract_type>"), DetailsB.indexOf("</contract_type>")+"</contract_type>".length());
			
			if(!"".equalsIgnoreCase(row.get(3).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<phase>")+"<phase>".length(),row.get(3).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<phase>"), DetailsB.indexOf("</phase>")+"</phase>".length());
			
			if(!"".equalsIgnoreCase(row.get(4).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<role_of_customer>")+"<role_of_customer>".length(),row.get(4).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<role_of_customer>"), DetailsB.indexOf("</role_of_customer>")+"</role_of_customer>".length());
			
			if(!"".equalsIgnoreCase(row.get(5).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<start_date>")+"<start_date>".length(),row.get(5).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<start_date>"), DetailsB.indexOf("</start_date>")+"</start_date>".length());

			if(!"".equalsIgnoreCase(row.get(6).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<close_date>")+"<close_date>".length(),row.get(6).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<close_date>"), DetailsB.indexOf("</close_date>")+"</close_date>".length());
			
			if(!"".equalsIgnoreCase(row.get(7).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<approved_date>")+"<approved_date>".length(),row.get(7).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<approved_date>"), DetailsB.indexOf("</approved_date>")+"</approved_date>".length());
			
			if(!"".equalsIgnoreCase(row.get(8).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<outstanding_balance>")+"<outstanding_balance>".length(),row.get(8).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<outstanding_balance>"), DetailsB.indexOf("</outstanding_balance>")+"</outstanding_balance>".length());
			
			if(!"".equalsIgnoreCase(row.get(9).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<total_amount>")+"<total_amount>".length(),row.get(9).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<total_amount>"), DetailsB.indexOf("</total_amount>")+"</total_amount>".length());
			
			if(!"".equalsIgnoreCase(row.get(10).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<payments_amount>")+"<payments_amount>".length(),row.get(10).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<payments_amount>"), DetailsB.indexOf("</payments_amount>")+"</payments_amount>".length());
			
			if(!"".equalsIgnoreCase(row.get(11).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<total_no_of_instalments>")+"<total_no_of_instalments>".length(),row.get(11).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<total_no_of_instalments>"), DetailsB.indexOf("</total_no_of_instalments>")+"</total_no_of_instalments>".length());
			
			if(!"".equalsIgnoreCase(row.get(12).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<no_of_remaining_instalments>")+"<no_of_remaining_instalments>".length(),row.get(12).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<no_of_remaining_instalments>"), DetailsB.indexOf("</no_of_remaining_instalments>")+"</no_of_remaining_instalments>".length());
			
			if(!"".equalsIgnoreCase(row.get(13).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<worst_status>")+"<worst_status>".length(),row.get(13).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<worst_status>"), DetailsB.indexOf("</worst_status>")+"</worst_status>".length());
			
			if(!"".equalsIgnoreCase(row.get(14).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<worst_status_date>")+"<worst_status_date>".length(),row.get(14).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<worst_status_date>"), DetailsB.indexOf("</worst_status_date>")+"</worst_status_date>".length());
			
			if(!"".equalsIgnoreCase(row.get(15).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<credit_limit>")+"<credit_limit>".length(),row.get(15).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<credit_limit>"), DetailsB.indexOf("</credit_limit>")+"</credit_limit>".length());
			
			if(!"".equalsIgnoreCase(row.get(16).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<overdue_amount>")+"<overdue_amount>".length(),row.get(16).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<overdue_amount>"), DetailsB.indexOf("</overdue_amount>")+"</overdue_amount>".length());
			
			if(!"".equalsIgnoreCase(row.get(17).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<no_of_days_payment_delay>")+"<no_of_days_payment_delay>".length(),row.get(17).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<no_of_days_payment_delay>"), DetailsB.indexOf("</no_of_days_payment_delay>")+"</no_of_days_payment_delay>".length());
			
			if(!"".equalsIgnoreCase(row.get(18).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<mob>")+"<mob>".length(),row.get(18).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<mob>"), DetailsB.indexOf("</mob>")+"</mob>".length());
			
			if(!"".equalsIgnoreCase(row.get(19).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<last_repayment_date>")+"<last_repayment_date>".length(),row.get(19).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<last_repayment_date>"), DetailsB.indexOf("</last_repayment_date>")+"</last_repayment_date>".length());
			
			if(!"".equalsIgnoreCase(row.get(20).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<currently_current>")+"<currently_current>".length(),row.get(20).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<currently_current>"), DetailsB.indexOf("</currently_current>")+"</currently_current>".length());
							
			if(!"".equalsIgnoreCase(row.get(21).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<card_product>")+"<card_product>".length(),row.get(21).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<card_product>"), DetailsB.indexOf("</card_product>")+"</card_product>".length());
			
			if(!"".equalsIgnoreCase(row.get(22).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<property_value>")+"<property_value>".length(),row.get(22).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<property_value>"), DetailsB.indexOf("</property_value>")+"</property_value>".length());
			
			if(!"".equalsIgnoreCase(row.get(23).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<disbursal_date>")+"<disbursal_date>".length(),row.get(23).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<disbursal_date>"), DetailsB.indexOf("</disbursal_date>")+"</disbursal_date>".length());
			
			if(!"".equalsIgnoreCase(row.get(24).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<marketing_code>")+"<marketing_code>".length(),row.get(24).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<marketing_code>"), DetailsB.indexOf("</marketing_code>")+"</marketing_code>".length());
			
			if(!"".equalsIgnoreCase(row.get(25).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<no_of_repayments_done>")+"<no_of_repayments_done>".length(),row.get(25).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<no_of_repayments_done>"), DetailsB.indexOf("</no_of_repayments_done>")+"</no_of_repayments_done>".length());
			
			if(!"".equalsIgnoreCase(row.get(26).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<general_status>")+"<general_status>".length(),row.get(26).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<general_status>"), DetailsB.indexOf("</general_status>")+"</general_status>".length());
			
			if(!"".equalsIgnoreCase(row.get(27).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<emi>")+"<emi>".length(),row.get(27).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<emi>"), DetailsB.indexOf("</emi>")+"</emi>".length());
			
			if(!"".equalsIgnoreCase(row.get(28).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<os_amt>")+"<os_amt>".length(),row.get(28).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<os_amt>"), DetailsB.indexOf("</os_amt>")+"</os_amt>".length());
			
			if(!"".equalsIgnoreCase(row.get(29).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_3mon>")+"<dpd_30_in_last_3mon>".length(),row.get(29).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_3mon>"), DetailsB.indexOf("</dpd_30_in_last_3mon>")+"</dpd_30_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(30).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_6mon>")+"<dpd_30_in_last_6mon>".length(),row.get(30).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_6mon>"), DetailsB.indexOf("</dpd_30_in_last_6mon>")+"</dpd_30_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(31).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_9mon>")+"<dpd_30_in_last_9mon>".length(),row.get(31).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_9mon>"), DetailsB.indexOf("</dpd_30_in_last_9mon>")+"</dpd_30_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(32).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_12mon>")+"<dpd_30_in_last_12mon>".length(),row.get(32).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_12mon>"), DetailsB.indexOf("</dpd_30_in_last_12mon>")+"</dpd_30_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(33).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_18mon>")+"<dpd_30_in_last_18mon>".length(),row.get(33).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_18mon>"), DetailsB.indexOf("</dpd_30_in_last_18mon>")+"</dpd_30_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(34).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_30_in_last_24mon>")+"<dpd_30_in_last_24mon>".length(),row.get(34).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_30_in_last_24mon>"), DetailsB.indexOf("</dpd_30_in_last_24mon>")+"</dpd_30_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(35).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_3mon>")+"<dpd_60_in_last_3mon>".length(),row.get(35).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_3mon>"), DetailsB.indexOf("</dpd_60_in_last_3mon>")+"</dpd_60_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(36).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_6mon>")+"<dpd_60_in_last_6mon>".length(),row.get(36).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_6mon>"), DetailsB.indexOf("</dpd_60_in_last_6mon>")+"</dpd_60_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(37).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_9mon>")+"<dpd_60_in_last_9mon>".length(),row.get(37).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_9mon>"), DetailsB.indexOf("</dpd_60_in_last_9mon>")+"</dpd_60_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(38).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_12mon>")+"<dpd_60_in_last_12mon>".length(),row.get(38).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_12mon>"), DetailsB.indexOf("</dpd_60_in_last_12mon>")+"</dpd_60_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(39).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_18mon>")+"<dpd_60_in_last_18mon>".length(),row.get(39).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_18mon>"), DetailsB.indexOf("</dpd_60_in_last_18mon>")+"</dpd_60_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(40).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_60_in_last_24mon>")+"<dpd_60_in_last_24mon>".length(),row.get(40).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_60_in_last_24mon>"), DetailsB.indexOf("</dpd_60_in_last_24mon>")+"</dpd_60_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(41).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_3mon>")+"<dpd_90_in_last_3mon>".length(),row.get(41).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_3mon>"), DetailsB.indexOf("</dpd_90_in_last_3mon>")+"</dpd_90_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(42).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_6mon>")+"<dpd_90_in_last_6mon>".length(),row.get(42).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_6mon>"), DetailsB.indexOf("</dpd_90_in_last_6mon>")+"</dpd_90_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(43).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_9mon>")+"<dpd_90_in_last_9mon>".length(),row.get(43).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_9mon>"), DetailsB.indexOf("</dpd_90_in_last_9mon>")+"</dpd_90_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(44).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_12mon>")+"<dpd_90_in_last_12mon>".length(),row.get(44).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_12mon>"), DetailsB.indexOf("</dpd_90_in_last_12mon>")+"</dpd_90_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(45).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_18mon>")+"<dpd_90_in_last_18mon>".length(),row.get(45).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_18mon>"), DetailsB.indexOf("</dpd_90_in_last_18mon>")+"</dpd_90_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(46).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_90_in_last_24mon>")+"<dpd_90_in_last_24mon>".length(),row.get(46).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_90_in_last_24mon>"), DetailsB.indexOf("</dpd_90_in_last_24mon>")+"</dpd_90_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(47).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_3mon>")+"<dpd_120_in_last_3mon>".length(),row.get(47).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_3mon>"), DetailsB.indexOf("</dpd_120_in_last_3mon>")+"</dpd_120_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(48).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_6mon>")+"<dpd_120_in_last_6mon>".length(),row.get(48).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_6mon>"), DetailsB.indexOf("</dpd_120_in_last_6mon>")+"</dpd_120_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(49).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_9mon>")+"<dpd_120_in_last_9mon>".length(),row.get(49).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_9mon>"), DetailsB.indexOf("</dpd_120_in_last_9mon>")+"</dpd_120_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(50)))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_12mon>")+"<dpd_120_in_last_12mon>".length(),row.get(50).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_12mon>"), DetailsB.indexOf("</dpd_120_in_last_12mon>")+"</dpd_120_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(51).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_18mon>")+"<dpd_120_in_last_18mon>".length(),row.get(51).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_18mon>"), DetailsB.indexOf("</dpd_120_in_last_18mon>")+"</dpd_120_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(52).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_120_in_last_24mon>")+"<dpd_120_in_last_24mon>".length(),row.get(52).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_120_in_last_24mon>"), DetailsB.indexOf("</dpd_120_in_last_24mon>")+"</dpd_120_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(53).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_3mon>")+"<dpd_150_in_last_3mon>".length(),row.get(53).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_3mon>"), DetailsB.indexOf("</dpd_150_in_last_3mon>")+"</dpd_150_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(54).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_6mon>")+"<dpd_150_in_last_6mon>".length(),row.get(54).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_6mon>"), DetailsB.indexOf("</dpd_150_in_last_6mon>")+"</dpd_150_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(55).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_9mon>")+"<dpd_150_in_last_9mon>".length(),row.get(55).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_9mon>"), DetailsB.indexOf("</dpd_150_in_last_9mon>")+"</dpd_150_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(56).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_12mon>")+"<dpd_150_in_last_12mon>".length(),row.get(56).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_12mon>"), DetailsB.indexOf("</dpd_150_in_last_12mon>")+"</dpd_150_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(57).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_18mon>")+"<dpd_150_in_last_18mon>".length(),row.get(57).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_18mon>"), DetailsB.indexOf("</dpd_150_in_last_18mon>")+"</dpd_150_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(58).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_150_in_last_24mon>")+"<dpd_150_in_last_24mon>".length(),row.get(58).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_150_in_last_24mon>"), DetailsB.indexOf("</dpd_150_in_last_24mon>")+"</dpd_150_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(59)))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_3mon>")+"<dpd_180_in_last_3mon>".length(),row.get(59).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_3mon>"), DetailsB.indexOf("</dpd_180_in_last_3mon>")+"</dpd_180_in_last_3mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(60).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_6mon>")+"<dpd_180_in_last_6mon>".length(),row.get(60).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_6mon>"), DetailsB.indexOf("</dpd_180_in_last_6mon>")+"</dpd_180_in_last_6mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(61).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_9mon>")+"<dpd_180_in_last_9mon>".length(),row.get(61).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_9mon>"), DetailsB.indexOf("</dpd_180_in_last_9mon>")+"</dpd_180_in_last_9mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(62).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_12mon>")+"<dpd_180_in_last_12mon>".length(),row.get(62).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_12mon>"), DetailsB.indexOf("</dpd_180_in_last_12mon>")+"</dpd_180_in_last_12mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(63).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_18mon>")+"<dpd_180_in_last_18mon>".length(),row.get(63).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_18mon>"), DetailsB.indexOf("</dpd_180_in_last_18mon>")+"</dpd_180_in_last_18mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(64).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dpd_180_in_last_24mon>")+"<dpd_180_in_last_24mon>".length(),row.get(64).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dpd_180_in_last_24mon>"), DetailsB.indexOf("</dpd_180_in_last_24mon>")+"</dpd_180_in_last_24mon>".length());
			
			if(!"".equalsIgnoreCase(row.get(65).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<write_off_amount>")+"<write_off_amount>".length(),row.get(65).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<write_off_amount>"), DetailsB.indexOf("</write_off_amount>")+"</write_off_amount>".length());
			
			String CompanyFlag = "";
			if (row.get(0).trim() != null && !"".equalsIgnoreCase(row.get(0).trim()) && !"null".equalsIgnoreCase(row.get(0).trim())) 
			{
				CompanyFlag = "N";
				if(row.get(0).trim().equalsIgnoreCase(getControlValue("CIF_NUMBER",iformObj).trim()))
					CompanyFlag = "Y";
				else 
				{
					String qry = "SELECT top 1 COMPANYFLAG FROM USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS WITH(nolock) WHERE CIF='"+row.get(0).trim()+"' AND Wi_Name='"+getWorkitemName(iformObj)+"'";
					List<List<String>> CmpFlgQry = iformObj.getDataFromDB(qry);
					for (int k = 0; k < CmpFlgQry.size(); k++) {
						CompanyFlag = CmpFlgQry.get(k).get(0);
						break;
					}
				}
			}
			
			if(!"".equalsIgnoreCase(CompanyFlag))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<company_flag>")+"<company_flag>".length(),CompanyFlag);
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<company_flag>"), DetailsB.indexOf("</company_flag>")+"</company_flag>".length());
			
						
			InternalBureauIndProdLoansXML = DetailsB.toString();
			InternalBureauIndProdLoansXMLMain = InternalBureauIndProdLoansXMLMain + InternalBureauIndProdLoansXML;
		}	
		/////////////////////////////////////////	
	
		
		// Internal Bureau Pipeline Products
		String InternalBureauPplProdXMLMain = InternalBureauPipelineProducts(iformObj, control, Data);
		/*GuarantorLst = iformObj.getDataFromDB("SELECT CifId, CardEmbossNum, CardType, CardStatus, CustRoleType, CreditLimit FROM USR_0_iRBL_InternalExpo_CardDetails with(nolock) WHERE Wi_Name = '"+getWorkitemName(iformObj)+"' ");
		
		for (List<String> row : GuarantorLst) {
					
			String InternalBureauPplProdXML = "<InternalBureauPipelineProducts><applicant_id></applicant_id><internal_bureau_pipeline_products_id></internal_bureau_pipeline_products_id><ppl_type_of_contract></ppl_type_of_contract><ppl_phase></ppl_phase><ppl_role></ppl_role><ppl_credit_limit></ppl_credit_limit><company_flag></company_flag><ppl_consider_for_obligation>Y</ppl_consider_for_obligation></InternalBureauPipelineProducts>";
			
			StringBuffer DetailsB = new StringBuffer(InternalBureauPplProdXML);
			
			if(!"".equalsIgnoreCase(row.get(0).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),row.get(0).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<applicant_id>"), DetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
			
			if(!"".equalsIgnoreCase(row.get(1).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<internal_bureau_individual_products_id>")+"<internal_bureau_individual_products_id>".length(),row.get(1).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<internal_bureau_individual_products_id>"), DetailsB.indexOf("</internal_bureau_individual_products_id>")+"</internal_bureau_individual_products_id>".length());
		
			if(!"".equalsIgnoreCase(row.get(2).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<ppl_type_of_contract>")+"<ppl_type_of_contract>".length(),row.get(2).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<ppl_type_of_contract>"), DetailsB.indexOf("</ppl_type_of_contract>")+"</ppl_type_of_contract>".length());
			
			if(!"".equalsIgnoreCase(row.get(3).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<ppl_phase>")+"<ppl_phase>".length(),row.get(3).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<ppl_phase>"), DetailsB.indexOf("</ppl_phase>")+"</ppl_phase>".length());
			
			if(!"".equalsIgnoreCase(row.get(4).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<ppl_role>")+"<ppl_role>".length(),row.get(4).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<ppl_role>"), DetailsB.indexOf("</ppl_role>")+"</ppl_role>".length());
			
			if(!"".equalsIgnoreCase(row.get(5).trim()))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<ppl_credit_limit>")+"<ppl_credit_limit>".length(),row.get(5).trim());
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<ppl_credit_limit>"), DetailsB.indexOf("</ppl_credit_limit>")+"</ppl_credit_limit>".length());
			
			String CompanyFlag = "";
			if (row.get(0).trim() != null && !"".equalsIgnoreCase(row.get(0).trim()) && !"null".equalsIgnoreCase(row.get(0).trim())) 
			{
				CompanyFlag = "N";
				if(row.get(0).trim().equalsIgnoreCase(getControlValue("CIF_NUMBER").trim()))
					CompanyFlag = "Y";
				else 
				{
					String qry = "SELECT top 1 COMPANYFLAG FROM USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS WITH(nolock) WHERE CIF='"+row.get(0).trim()+"' AND Wi_Name='"+getWorkitemName(iformObj)+"'";
					List<List<String>> CmpFlgQry = iformObj.getDataFromDB(qry);
					for (int k = 0; k < CmpFlgQry.size(); k++) {
						CompanyFlag = CmpFlgQry.get(k).get(0);
						break;
					}
				}
			}
			
			if(!"".equalsIgnoreCase(CompanyFlag))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<company_flag>")+"<company_flag>".length(),CompanyFlag);
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<company_flag>"), DetailsB.indexOf("</company_flag>")+"</company_flag>".length());
			
			InternalBureauPplProdXML = DetailsB.toString();
			InternalBureauPplProdXMLMain = InternalBureauPplProdXMLMain + InternalBureauPplProdXML;
		}*/
		/////////////////////////////////////////////////////////////
		
		if (!"".equalsIgnoreCase(InternalBureauXMLMain) || !"".equalsIgnoreCase(InternalBouncedChequesXMLMain)  || !"".equalsIgnoreCase(InternalBureauIndProdCardsXMLMain) || !"".equalsIgnoreCase(InternalBureauIndProdLoansXMLMain) || !"".equalsIgnoreCase(InternalBureauPplProdXMLMain))
			InternalBureauDataXMLMain = "<InternalBureauData>"+InternalBureauXMLMain+InternalBouncedChequesXMLMain+InternalBureauIndProdCardsXMLMain+InternalBureauIndProdLoansXMLMain+InternalBureauPplProdXMLMain+"</InternalBureauData>";
		
		return InternalBureauDataXMLMain;
	}
	
	public String InternalBureauData(IFormReference formObject,String control,String Data)
	{
		DigitalAO.mLogger.info( "inside InternalBureauData : ");
		String CifId = getControlValue("CIF_NUMBER",formObject);

		String NoOfContracts = "";
		String Total_Exposure = "";
		String WorstCurrentPaymentDelay = ""; 
		String Worst_PaymentDelay_Last24Months = "";
		String Nof_Records = "";

		String  add_xml_str ="";
		try{
			add_xml_str = add_xml_str + "<InternalBureau><applicant_id>"+CifId+"</applicant_id>";
			add_xml_str = add_xml_str + "<full_name>"+getControlValue("COMPANY_NAME",formObject)+"</full_name>";// fullname fieldname to be confirmed from onsite
			String sQuery = "SELECT isNull((Sum(Abs(convert(float,replace([OutstandingAmt],'NA','0'))))),0),isNull((Sum(Abs(convert(float,replace([OverdueAmt],'NA','0'))))),0),isNull((Sum(convert(float,replace([CreditLimit],'NA','0')))),0) FROM USR_0_iRBL_InternalExpo_CardDetails with(nolock) WHERE wi_name= '"+getWorkitemName(formObject)+"' AND Request_Type = 'InternalExposure'  union SELECT   isNull((Sum(Abs(convert(float,replace([TotalOutstandingAmt],'NA','0'))))),0),isNull((Sum(Abs(convert(float,replace([OverdueAmt],'NA','0'))))),0),isNull((Sum(convert(float,replace([TotalLoanAmount],'NA','0')))),0) FROM USR_0_iRBL_InternalExpo_LoanDetails   with (nolock) WHERE wi_name = '"+getWorkitemName(formObject)+"' and  LoanStat  not in ('Pipeline','CAS-Pipeline')";
			List<List<String>> OutputXML = formObject.getDataFromDB(sQuery);
			DigitalAO.mLogger.info("InternalBureauData list size"+OutputXML.size());
			DigitalAO.mLogger.info( "values");
			double TotOutstandingAmt;
			double TotOverdueAmt;


			DigitalAO.mLogger.info( "values");
			TotOutstandingAmt=0.0f;
			TotOverdueAmt=0.0f;


			DigitalAO.mLogger.info( "values");
			for(int i = 0; i<OutputXML.size();i++){
				DigitalAO.mLogger.info( "values"+OutputXML.get(i).get(1));
				if(OutputXML.get(i).get(0)!=null && !OutputXML.get(i).get(1).isEmpty() &&  !"".equalsIgnoreCase(OutputXML.get(i).get(1)) && !"null".equalsIgnoreCase(OutputXML.get(i).get(1)) )
				{
					DigitalAO.mLogger.info( "values."+TotOutstandingAmt+"..");
					TotOutstandingAmt = TotOutstandingAmt + Double.parseDouble(OutputXML.get(i).get(1));
				}
				if(OutputXML.get(i).get(1)!=null && !OutputXML.get(i).get(1).isEmpty() && !"".equalsIgnoreCase(OutputXML.get(i).get(2)) && !"null".equalsIgnoreCase(OutputXML.get(i).get(2)) )
				{
					DigitalAO.mLogger.info( "values."+TotOutstandingAmt+"..");
					TotOverdueAmt = TotOverdueAmt + Double.parseDouble(OutputXML.get(i).get(2));
				}

			}
			String TotOutstandingAmtSt=String.format("%.0f", TotOutstandingAmt);
			String TotOverdueAmtSt=String.format("%.0f", TotOverdueAmt);
			add_xml_str = add_xml_str + "<total_out_bal>"+TotOutstandingAmtSt+"</total_out_bal>";
			add_xml_str = add_xml_str + "<total_overdue>"+TotOverdueAmtSt+"</total_overdue>";
			String sQueryDerived = "select NoOfContracts,Total_Exposure,WorstCurrentPaymentDelay,Worst_PaymentDelay_Last24Months,Nof_Records from USR_0_iRBL_InternalExpo_Derived with (nolock) where Request_Type='CollectionsSummary' and wi_name='"+getWorkitemName(formObject)+"'" ;
			List<List<String>> OutputXMLDerived = formObject.getDataFromDB(sQueryDerived);
			if(OutputXMLDerived!=null && OutputXMLDerived.size()>0 && OutputXMLDerived.get(0)!=null){
				if(!(OutputXMLDerived.get(0).get(0)==null || OutputXMLDerived.get(0).get(0).equals("")) ){
					NoOfContracts= OutputXMLDerived.get(0).get(0).toString();
				}
				if(!(OutputXMLDerived.get(0).get(1)==null || OutputXMLDerived.get(0).get(1).equals("")) ){
					Total_Exposure= OutputXMLDerived.get(0).get(1).toString();
				}
				if(!(OutputXMLDerived.get(0).get(2)==null || OutputXMLDerived.get(0).get(2).equals("")) ){
					WorstCurrentPaymentDelay= OutputXMLDerived.get(0).get(2).toString();
				}
				if(!(OutputXMLDerived.get(0).get(3)==null || OutputXMLDerived.get(0).get(3).equals("")) ){
					Worst_PaymentDelay_Last24Months= OutputXMLDerived.get(0).get(3).toString();
				}
				if(!(OutputXMLDerived.get(0).get(4)==null || OutputXMLDerived.get(0).get(4).equals("")) ){
					Nof_Records= OutputXMLDerived.get(0).get(4).toString();
				}
			}
			add_xml_str = add_xml_str + "<no_default_contract>"+NoOfContracts+"</no_default_contract>";

			add_xml_str = add_xml_str + "<total_exposure>"+Total_Exposure+"</total_exposure>";
			add_xml_str = add_xml_str + "<worst_curr_pay>"+WorstCurrentPaymentDelay+"</worst_curr_pay>"; // to be populated later
			add_xml_str = add_xml_str + "<worst_curr_pay_24>"+Worst_PaymentDelay_Last24Months+"</worst_curr_pay_24>"; // to be populated later
			add_xml_str = add_xml_str + "<no_of_rec>"+Nof_Records+"</no_of_rec>"; 
			
				String sQuerycheque = "SELECT 'DDS 3 months',count(*) FROM USR_0_iRBL_FinancialSummary_ReturnsDtls with (nolock) WHERE CAST(returnDate AS datetime) >= DATEADD(month,-3,GETDATE()) and returntype='DDS' and wi_name='"+getWorkitemName(formObject)+"' union SELECT 'DDS 6 months',count(*) FROM USR_0_iRBL_FinancialSummary_ReturnsDtls with (nolock) WHERE CAST(returnDate AS datetime) >= DATEADD(month,-6,GETDATE()) and returntype='DDS' and wi_name='"+getWorkitemName(formObject)+"' union SELECT 'ICCS 3 months',count(*) FROM USR_0_iRBL_FinancialSummary_ReturnsDtls with (nolock) WHERE CAST(returnDate AS datetime) >= DATEADD(month,-3,GETDATE()) and returntype='ICCS' and wi_name='"+getWorkitemName(formObject)+"' union SELECT 'ICCS 6 months',count(*) FROM USR_0_iRBL_FinancialSummary_ReturnsDtls with (nolock) WHERE CAST(returnDate AS datetime) >= DATEADD(month,-6,GETDATE()) and returntype='ICCS' and wi_name='"+getWorkitemName(formObject)+"'" ;
				List<List<String>> OutputXMLcheque = formObject.getDataFromDB(sQuerycheque);
				
				String cheque_return_6mon = getControlValue("Q_USR_0_IRBL_FINANCIAL_ELIGIBILITY_CHECKS_OUTWARD_CHEQUE_RET_AMT_6MON_DET",formObject).trim(); // POLP-11117
				String cheque_return_3mon = getControlValue("Q_USR_0_IRBL_FINANCIAL_ELIGIBILITY_CHECKS_OUTWARD_CHEQUE_RET_AMT_12MON_DET",formObject).trim(); // POLP-11117
				
				//add_xml_str = add_xml_str + "<cheque_return_3mon>"+OutputXMLcheque.get(2).get(1)+"</cheque_return_3mon>";
				if(!"".equalsIgnoreCase(cheque_return_3mon))
					add_xml_str = add_xml_str + "<cheque_return_3mon>"+cheque_return_3mon+"</cheque_return_3mon>"; // POLP-11117 - data manually entered on 'Outward cheque return amt last 12 mon' field at the frontend will be passed in this tag.
				add_xml_str = add_xml_str + "<dds_return_3mon>"+OutputXMLcheque.get(0).get(1)+"</dds_return_3mon>";
				//add_xml_str = add_xml_str + "<cheque_return_6mon>"+OutputXMLcheque.get(3).get(1)+"</cheque_return_6mon>";
				if(!"".equalsIgnoreCase(cheque_return_6mon))
					add_xml_str = add_xml_str + "<cheque_return_6mon>"+cheque_return_6mon+"</cheque_return_6mon>"; // POLP-11117 - data manually entered on 'Outward cheque return amt last 6 mon' field at the frontend will be passed in this tag.
				add_xml_str = add_xml_str + "<dds_return_6mon>"+OutputXMLcheque.get(1).get(1)+"</dds_return_6mon>";
			
			
			add_xml_str = add_xml_str + "<internal_charge_off>"+"N"+"</internal_charge_off><company_flag>Y</company_flag></InternalBureau>";// to be populated later



			DigitalAO.mLogger.info( "Internal Bureau tag Cration: "+ add_xml_str);
			return add_xml_str;
		}
		catch(Exception e)
		{
			DigitalAO.mLogger.info( "Exception occurred in InternalBureauData()"+e.getMessage()+printException(e));
			return "";
		}

	}
	
	public String InternalBouncedCheques(IFormReference formObject,String control,String Data) {
		DigitalAO.mLogger.info("iRBL DecTech java file"+"inside InternalBouncedCheques : ");
		String sQuery = "select CIFID,AcctId,returntype,returnNumber,returnAmount,retReasonCode,returnDate from USR_0_iRBL_FinancialSummary_ReturnsDtls with (nolock) where wi_name = '"+ getWorkitemName(formObject) + "'";

		String add_xml_str = "";
		List<List<String>> OutputXML = formObject.getDataFromDB(sQuery);

		for (int i = 0; i < OutputXML.size(); i++) {

			String applicantID = "";
			String chequeNo = "";
			String internal_bounced_cheques_id = "";
			String bouncedCheq = "";
			String amount = "";
			String reason = "";
			String returnDate = "";

			if (!(OutputXML.get(i).get(0) == null || OutputXML.get(i).get(0)
					.equals(""))) {
				applicantID = OutputXML.get(i).get(0).toString();
			}
			if (!(OutputXML.get(i).get(1) == null || OutputXML.get(i).get(1)
					.equals(""))) {
				internal_bounced_cheques_id = OutputXML.get(i).get(1).toString();
			}
			if (!(OutputXML.get(i).get(2) == null || OutputXML.get(i).get(2)
					.equals(""))) {
				bouncedCheq = OutputXML.get(i).get(2).toString();
			}
			if (!(OutputXML.get(i).get(3) == null || OutputXML.get(i).get(3)
					.equals(""))) {
				chequeNo = OutputXML.get(i).get(3).toString();
			}
			if (!(OutputXML.get(i).get(4) == null || OutputXML.get(i).get(4)
					.equals(""))) {
				amount = OutputXML.get(i).get(4).toString();
			}
			if (!(OutputXML.get(i).get(5) == null || OutputXML.get(i).get(5)
					.equals(""))) {
				reason = OutputXML.get(i).get(5).toString();
			}
			if (!(OutputXML.get(i).get(6) == null || OutputXML.get(i).get(6)
					.equals(""))) {
				returnDate = OutputXML.get(i).get(6).toString();
			}

			if (applicantID != null && !"".equalsIgnoreCase(applicantID) && !"null".equalsIgnoreCase(applicantID)) 
			{
				String CompanyFlag = "N";
				if(applicantID.equalsIgnoreCase(getControlValue("CIF_NUMBER",formObject)))
					CompanyFlag = "Y";
				else 
				{
					String qry = "SELECT top 1 COMPANYFLAG FROM USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS WITH(nolock) WHERE CIF='"+applicantID+"' AND Wi_Name='"+getWorkitemName(formObject)+"'";
					List<List<String>> CmpFlgQry = formObject.getDataFromDB(qry);
					for (int k = 0; k < CmpFlgQry.size(); k++) {
						CompanyFlag = CmpFlgQry.get(k).get(0);
						break;
					}
				}
				
				add_xml_str = add_xml_str + "<InternalBouncedCheques><applicant_id>" + applicantID + "</applicant_id>";
				add_xml_str = add_xml_str + "<internal_bounced_cheques_id>" + internal_bounced_cheques_id + "</internal_bounced_cheques_id>";
				add_xml_str = add_xml_str + "<bounced_cheque>" +bouncedCheq + "</bounced_cheque>";
				add_xml_str = add_xml_str + "<cheque_no>" + chequeNo + "</cheque_no>";
				add_xml_str = add_xml_str + "<amount>" + amount + "</amount>";
				add_xml_str = add_xml_str + "<reason>" + reason + "</reason>";
				add_xml_str = add_xml_str + "<return_date>" + returnDate + "</return_date>";
				add_xml_str = add_xml_str + "<provider_no>" + "" + "</provider_no>";
				add_xml_str = add_xml_str + "<bounced_cheque_dds>" + bouncedCheq+ "</bounced_cheque_dds>";
				add_xml_str = add_xml_str + "<company_flag>"+CompanyFlag+"</company_flag></InternalBouncedCheques>";
			}

		}
		DigitalAO.mLogger.info("iRBL Internal bounced cheque Cration: "
				+ add_xml_str);
		return add_xml_str;
	}
	
	public String InternalBureauPipelineProducts(IFormReference formObject,String control,String Data) {
		DigitalAO.mLogger.info("inside InternalBureauPipelineProducts : ");
		String sQuery = "SELECT cifid,LoanType,custroletype,lastupdatedate,totalamount,totalnoofinstalments,totalloanamount,agreementId,NoOfDaysInPipeline,case when Consider_For_Obligations is null or Consider_For_Obligations='True' then 'Y' else 'N' end as Consider_For_Obligations,NextInstallmentAmt,SchemeCardProd FROM USR_0_iRBL_InternalExpo_LoanDetails  with (nolock) where Wi_Name = '"
			+ getWorkitemName(formObject)
			+ "' and LoanStat in ('Pipeline','CAS-Pipeline')";
		
		String add_xml_str = "";
		try{
			List<List<String>> OutputXML = formObject.getDataFromDB(sQuery);
		
			for (int i = 0; i < OutputXML.size(); i++) {

				String cifId = "";
				String Product = "";
				String lastUpdateDate = "";
				String TotAmount = "";
				String TotNoOfInstlmnt = "";
				String TotLoanAmt = "";
				String agreementId = "";
				String NoOfDaysInPipeline="";
				String ConsiderForOblig = "";
				String EMI = "";
				if (!(OutputXML.get(i).get(0) == null || OutputXML.get(i).get(0).equals(""))) {
					cifId = OutputXML.get(i).get(0).toString();
				}
				if (!(OutputXML.get(i).get(1) == null || OutputXML.get(i).get(1).equals(""))) {
					Product = OutputXML.get(i).get(1).toString();
				}
				if (!(OutputXML.get(i).get(2) == null || OutputXML.get(i).get(2).equals(""))) {
				}
				if (!(OutputXML.get(i).get(3) == null || OutputXML.get(i).get(3).equals(""))) {
					lastUpdateDate = OutputXML.get(i).get(3).toString();
				}
				if (!(OutputXML.get(i).get(4) == null || OutputXML.get(i).get(4).equals(""))) {
					TotAmount = OutputXML.get(i).get(4).toString();
				}
				if (!(OutputXML.get(i).get(5) == null || OutputXML.get(i).get(5).equals(""))) {
					TotNoOfInstlmnt = OutputXML.get(i).get(5).toString();
				}
				if (!(OutputXML.get(i).get(6) == null || OutputXML.get(i).get(6).equals(""))) {
					TotLoanAmt = OutputXML.get(i).get(6).toString();
				}
				if (!(OutputXML.get(i).get(7) == null || OutputXML.get(i).get(7).equals(""))) {
					agreementId = OutputXML.get(i).get(7).toString();
				}
				if(!(OutputXML.get(i).get(8) == null || "".equalsIgnoreCase(OutputXML.get(i).get(8))) ){
					NoOfDaysInPipeline = OutputXML.get(i).get(8);
				}
				if(!(OutputXML.get(i).get(9) == null || "".equalsIgnoreCase(OutputXML.get(i).get(9))) ){
					ConsiderForOblig = OutputXML.get(i).get(9);
				}
				if(!(OutputXML.get(i).get(10) == null || "".equalsIgnoreCase(OutputXML.get(i).get(10))) ){
					EMI = OutputXML.get(i).get(10);
				}
				if(!(OutputXML.get(i).get(11) == null || "".equalsIgnoreCase(OutputXML.get(i).get(11))) ){
					if(OutputXML.get(i).get(11).equalsIgnoreCase("LOC PREFERRED") || OutputXML.get(i).get(11).equalsIgnoreCase("LOC STANDARD"))
					{
						Product="IM";
					}
				}
				if (cifId != null && !"".equalsIgnoreCase(cifId)&& !"null".equalsIgnoreCase(cifId)) 
				{
					add_xml_str = add_xml_str + "<InternalBureauPipelineProducts>";
					add_xml_str = add_xml_str + "<applicant_id>" + cifId + "</applicant_id>";
					add_xml_str = add_xml_str + "<internal_bureau_pipeline_products_id>" + agreementId + "</internal_bureau_pipeline_products_id>";// to be
					// populated
					// later
					add_xml_str = add_xml_str + "<ppl_provider_no>RAKBANK</ppl_provider_no>";
					//add_xml_str = add_xml_str + "<ppl_product>" + Product + "</ppl_product>";
					
					
					add_xml_str = add_xml_str + "<ppl_type_of_contract>" + Product + "</ppl_type_of_contract>";
					add_xml_str = add_xml_str + "<ppl_phase>PIPELINE</ppl_phase>"; // to
					// be
					// populated
					// later

					add_xml_str = add_xml_str + "<ppl_role>Primary</ppl_role>";
					add_xml_str = add_xml_str + "<ppl_date_of_last_update>" + lastUpdateDate + "</ppl_date_of_last_update>";
					add_xml_str = add_xml_str + "<ppl_total_amount>" + TotAmount + "</ppl_total_amount>";
					add_xml_str = add_xml_str + "<ppl_no_of_instalments>" + TotNoOfInstlmnt + "</ppl_no_of_instalments>";
					add_xml_str = add_xml_str + "<ppl_credit_limit>" + TotLoanAmt + "</ppl_credit_limit>";

					add_xml_str = add_xml_str + "<ppl_no_of_days_in_pipeline>"+NoOfDaysInPipeline+"</ppl_no_of_days_in_pipeline>";
					add_xml_str = add_xml_str + "<company_flag>N</company_flag>";
					add_xml_str = add_xml_str + "<ppl_consider_for_obligation>"+ConsiderForOblig+"</ppl_consider_for_obligation>";
					add_xml_str = add_xml_str + "<ppl_emi>"+EMI+"</ppl_emi>";
					add_xml_str = add_xml_str + "</InternalBureauPipelineProducts>"; // to be populated later
				}

			}
			//iRBL.mLogger.info("DecTech - Internal liab tag Cration: "+ add_xml_str);
		}catch(Exception e){
			DigitalAO.mLogger.info("DecTech-Exception occurred in InternalBureauPipelineProducts()"+ e.getMessage() + "\n Error: "+ printException(e));
		}
		return add_xml_str;
	}
	
	public String DecTechExternalBureauDataXML(IFormReference iformObj,String control,String Data)
	{
		String ExternalBureauDataXMLMain = "";
		String ExternalBureauXMLMain = "";
		List<List<String>> OutputXML = iformObj.getDataFromDB("select CifId,ReferenceNo,fullnm,TotalOutstanding,TotalOverdue,NoOfContracts,Total_Exposure,WorstCurrentPaymentDelay,Worst_PaymentDelay_Last24Months,Worst_Status_Last24Months,Nof_Records,NoOf_Cheque_Return_Last3,Nof_DDES_Return_Last3Months,Nof_Cheque_Return_Last6,DPD30_Last6Months, "
				+ "(select max(ExternalWriteOffCheck) ExternalWriteOffCheck from ((select convert(int,isNULL(ExternalWriteOffCheck,0)) ExternalWriteOffCheck  from USR_0_iRBL_ExternalExpo_CardDetails with(nolock) WHERE wi_name = '"+getWorkitemName(iformObj)+"' AND ProviderNo!='B01' union all select convert(int,isNULL(ExternalWriteOffCheck,0))ExternalWriteOffCheck from USR_0_iRBL_ExternalExpo_LoanDetails with (nolock) WHERE wi_name = '"+getWorkitemName(iformObj)+"' AND ProviderNo!='B01' union all select convert(int,isNULL(ExternalWriteOffCheck,0))ExternalWriteOffCheck from USR_0_iRBL_ExternalExpo_AccountDetails with (nolock) WHERE wi_name = '"+getWorkitemName(iformObj)+"' AND ProviderNo!='B01'))as ExternalWriteOffCheck) , "
				+ "(select count(*) from (select DisputeAlert from USR_0_iRBL_ExternalExpo_LoanDetails with(nolock) where wi_name = '"+getWorkitemName(iformObj)+"' AND DisputeAlert='1' union  select DisputeAlert from USR_0_iRBL_ExternalExpo_CardDetails with(nolock) where wi_name = '"+getWorkitemName(iformObj)+"' AND DisputeAlert='1') as tempTable), "
				+ "AECB_Score,Range, "
				+ "(case when (select count(*) from RB_iRBL_EXTTABLE with(nolock) where CIF_NUMBER = cifid) > 0 then 'Y' else "
				+ "(SELECT top 1 COMPANYFLAG FROM USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS WITH(nolock) WHERE RelatedPartyId=RelatedPartyId AND Wi_Name='"+getWorkitemName(iformObj)+"') end ) AS COMPANYFLAG, "
				+ "RelatedPartyId "
				+ "from USR_0_iRBL_InternalExpo_Derived with (nolock) where wi_name = '"+getWorkitemName(iformObj)+"' AND Request_type= 'ExternalExposure' ");
		
		String AecbHistQuery = "select isnull(max(AECBHistMonthCnt),0) as AECBHistMonthCnt from ( select MAX(cast(isnull(AECBHistMonthCnt,'0') as int)) as AECBHistMonthCnt  from USR_0_iRBL_ExternalExpo_CardDetails with (nolock) where  wi_name  = '"+ getWorkitemName(iformObj) + "' and cardtype not in ( '85','99','Communication Services','TelCo-Mobile Prepaid','101','Current/Saving Account with negative Balance','58','Overdraft') and custroletype not in ('Co-Contract Holder','Guarantor') union all select Max(cast(isnull(AECBHistMonthCnt,'0') as int)) as AECBHistMonthCnt from USR_0_iRBL_ExternalExpo_LoanDetails with (nolock) where wi_name  = '"+ getWorkitemName(iformObj) + "' and loantype not in ('85','99','Communication Services','TelCo-Mobile Prepaid','101','Current/Saving Account with negative Balance','58','Overdraft') and custroletype not in ('Co-Contract Holder','Guarantor')) as ext_expo";
		
		List<List<String>> AecbHistQueryData = iformObj.getDataFromDB(AecbHistQuery);
		
		for (List<String> row : OutputXML) {
					
			String ExternalBureauXML = "<ExternalBureau><applicant_id></applicant_id><bureauone_ref_no></bureauone_ref_no><full_name></full_name><total_out_bal></total_out_bal><total_overdue></total_overdue><no_default_contract></no_default_contract><total_exposure></total_exposure><worst_curr_pay></worst_curr_pay><worst_curr_pay_24></worst_curr_pay_24><worst_status_24></worst_status_24><no_of_rec></no_of_rec><cheque_return_3mon></cheque_return_3mon><dds_return_3mon></dds_return_3mon><cheque_return_6mon></cheque_return_6mon><dds_return_6mon></dds_return_6mon><prod_external_writeoff_amount></prod_external_writeoff_amount><no_months_aecb_history></no_months_aecb_history><aecb_score></aecb_score><range></range><company_flag></company_flag><dispute_alert></dispute_alert></ExternalBureau>";
			
			StringBuffer DetailsB = new StringBuffer(ExternalBureauXML);
			
			if(!row.get(0).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),row.get(0)); // CIF ID
			else if(!row.get(20).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<applicant_id>")+"<applicant_id>".length(),row.get(20)); // Related Party Id
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<applicant_id>"), DetailsB.indexOf("</applicant_id>")+"</applicant_id>".length());
			
			if(!row.get(1).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<bureauone_ref_no>")+"<bureauone_ref_no>".length(),row.get(1));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<bureauone_ref_no>"), DetailsB.indexOf("</bureauone_ref_no>")+"</bureauone_ref_no>".length());
			
			if(!row.get(2).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<full_name>")+"<full_name>".length(),row.get(2));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<full_name>"), DetailsB.indexOf("</full_name>")+"</full_name>".length());
			
			if(!row.get(3).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<total_out_bal>")+"<total_out_bal>".length(),row.get(3));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<total_out_bal>"), DetailsB.indexOf("</total_out_bal>")+"</total_out_bal>".length());
			
			if(!row.get(4).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<total_overdue>")+"<total_overdue>".length(),row.get(4));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<total_overdue>"), DetailsB.indexOf("</total_overdue>")+"</total_overdue>".length());
					
			if(!row.get(5).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<no_default_contract>")+"<no_default_contract>".length(),row.get(5));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<no_default_contract>"), DetailsB.indexOf("</no_default_contract>")+"</no_default_contract>".length());
			
			if(!row.get(6).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<total_exposure>")+"<total_exposure>".length(),row.get(6));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<total_exposure>"), DetailsB.indexOf("</total_exposure>")+"</total_exposure>".length());
			
			if(!row.get(7).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<worst_curr_pay>")+"<worst_curr_pay>".length(),row.get(7));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<worst_curr_pay>"), DetailsB.indexOf("</worst_curr_pay>")+"</worst_curr_pay>".length());
			
			if(!row.get(8).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<worst_curr_pay_24>")+"<worst_curr_pay_24>".length(),row.get(8));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<worst_curr_pay_24>"), DetailsB.indexOf("</worst_curr_pay_24>")+"</worst_curr_pay_24>".length());
			
			if(!row.get(9).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<worst_status_24>")+"<worst_status_24>".length(),row.get(9));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<worst_status_24>"), DetailsB.indexOf("</worst_status_24>")+"</worst_status_24>".length());
			
			if(!row.get(10).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<no_of_rec>")+"<no_of_rec>".length(),row.get(10));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<no_of_rec>"), DetailsB.indexOf("</no_of_rec>")+"</no_of_rec>".length());
			
			if(!row.get(11).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<cheque_return_3mon>")+"<cheque_return_3mon>".length(),row.get(11));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<cheque_return_3mon>"), DetailsB.indexOf("</cheque_return_3mon>")+"</cheque_return_3mon>".length());
			
			if(!row.get(12).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dds_return_3mon>")+"<dds_return_3mon>".length(),row.get(12));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dds_return_3mon>"), DetailsB.indexOf("</dds_return_3mon>")+"</dds_return_3mon>".length());
			
			if(!row.get(13).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<cheque_return_6mon>")+"<cheque_return_6mon>".length(),row.get(13));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<cheque_return_6mon>"), DetailsB.indexOf("</cheque_return_6mon>")+"</cheque_return_6mon>".length());
			
			if(!row.get(14).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dds_return_6mon>")+"<dds_return_6mon>".length(),row.get(14));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dds_return_6mon>"), DetailsB.indexOf("</dds_return_6mon>")+"</dds_return_6mon>".length());
			
			if(!row.get(15).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<prod_external_writeoff_amount>")+"<prod_external_writeoff_amount>".length(),row.get(15));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<prod_external_writeoff_amount>"), DetailsB.indexOf("</prod_external_writeoff_amount>")+"</prod_external_writeoff_amount>".length());
			
			DetailsB = DetailsB.insert(DetailsB.indexOf("<no_months_aecb_history>")+"<no_months_aecb_history>".length(),AecbHistQueryData.get(0).get(0));
						
			if(!row.get(17).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<aecb_score>")+"<aecb_score>".length(),row.get(17));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<aecb_score>"), DetailsB.indexOf("</aecb_score>")+"</aecb_score>".length());
			
			if(!row.get(18).equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<range>")+"<range>".length(),row.get(18));
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<range>"), DetailsB.indexOf("</range>")+"</range>".length());
			
			String companyFlag="N";
			if(row.get(0).trim().equalsIgnoreCase(getControlValue("CIF_NUMBER",iformObj).trim()))
			{
				companyFlag="Y";
			}
			else
			{
				String sQuery1="select TOP 1 COMPANYFLAG from USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS WITH(nolock) WHERE WI_NAME='"+getWorkitemName(iformObj)+"' AND RELATEDPARTYID='"+row.get(20).trim()+"'";
				List<List<String>> CmpFlgQry = iformObj.getDataFromDB(sQuery1);
				for (int k = 0; k < CmpFlgQry.size(); k++) {
					companyFlag = CmpFlgQry.get(k).get(0);
					break;
				}
				
			}
			
			if(!companyFlag.equalsIgnoreCase(""))
				DetailsB = DetailsB.insert(DetailsB.indexOf("<company_flag>")+"<company_flag>".length(),companyFlag);
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<company_flag>"), DetailsB.indexOf("</company_flag>")+"</company_flag>".length());
			
			if(!row.get(16).equalsIgnoreCase(""))
			{
				String dispute_alert = row.get(16);
				if(Integer.parseInt(dispute_alert)>0)
					dispute_alert="Y";
				else
					dispute_alert="N";
				
				DetailsB = DetailsB.insert(DetailsB.indexOf("<dispute_alert>")+"<dispute_alert>".length(),dispute_alert);
			}
			else
				DetailsB = DetailsB.delete(DetailsB.indexOf("<dispute_alert>"), DetailsB.indexOf("</dispute_alert>")+"</dispute_alert>".length());
			
						
			ExternalBureauXML = DetailsB.toString();
			ExternalBureauXMLMain = ExternalBureauXMLMain + ExternalBureauXML;
		}
		
		String ExternalBouncedChequesXML = ExternalBouncedCheques(iformObj, control, Data);
		
		String ExternalBureauIndividualProductsXML = ExternalBureauIndividualProducts(iformObj, control, Data);
		String ExternalBureauPipelineProductsXML = ExternalBureauPipelineProducts(iformObj, control, Data);
		
		if (!"".equalsIgnoreCase(ExternalBureauXMLMain) || !"".equalsIgnoreCase(ExternalBouncedChequesXML)  || !"".equalsIgnoreCase(ExternalBureauIndividualProductsXML) || !"".equalsIgnoreCase(ExternalBureauPipelineProductsXML) )
			ExternalBureauDataXMLMain = "<ExternalBureauData>"+ExternalBureauXMLMain+ExternalBouncedChequesXML+ExternalBureauIndividualProductsXML+ExternalBureauPipelineProductsXML+"</ExternalBureauData>";
		
		return ExternalBureauDataXMLMain;
	}
	
	public String ExternalBouncedCheques(IFormReference formObject,String control,String Data) {
		DigitalAO.mLogger.debug("RLOSCommon java file"+"inside ExternalBouncedCheques : ");
		String sQuery = "SELECT cifid,number,amount,reasoncode,returndate,providerno,ChqType FROM USR_0_iRBL_ExternalExpo_ChequeDetails  with (nolock) where Wi_Name = '"+ getWorkitemName(formObject) + "' and Request_Type = 'ExternalExposure'";
		String add_xml_str = "";
		List<List<String>> OutputXML = formObject.getDataFromDB(sQuery);
		//CreditCard.mLogger.info("ExternalBouncedCheques list size"+ OutputXML.size()+ "");

		for (int i = 0; i < OutputXML.size(); i++) {

			String CifId = "";
			String chqNo = "";
			String Amount = "";
			String Reason = "";
			String returnDate = "";
			String providerNo = "";
			String ChqType="";

			if (!(OutputXML.get(i).get(1) == null || OutputXML.get(i).get(1)
					.equals(""))) {
				chqNo = OutputXML.get(i).get(1).toString();
			}
			if (!(OutputXML.get(i).get(2) == null || OutputXML.get(i).get(2)
					.equals(""))) {
				Amount = OutputXML.get(i).get(2).toString();
			}
			if (!(OutputXML.get(i).get(3) == null || OutputXML.get(i).get(3)
					.equals(""))) {
				Reason = OutputXML.get(i).get(3).toString();
			}
			if (!(OutputXML.get(i).get(4) == null || OutputXML.get(i).get(4)
					.equals(""))) {
				returnDate = OutputXML.get(i).get(4).toString();
			}
			if (!(OutputXML.get(i).get(5) == null || OutputXML.get(i).get(5)
					.equals(""))) {
				providerNo = OutputXML.get(i).get(5).toString();
			}
			if (!(OutputXML.get(i).get(6) == null || OutputXML.get(i).get(6)
					.equals(""))) {
				ChqType = OutputXML.get(i).get(6).toString();
			}

			add_xml_str = add_xml_str + "<ExternalBouncedCheques><applicant_id>" + CifId + "</applicant_id>";
			add_xml_str = add_xml_str + "<external_bounced_cheques_id>" + "" + "</external_bounced_cheques_id>";
			add_xml_str = add_xml_str + "<bounced_cheque>" + ChqType + "</bounced_cheque>";
			add_xml_str = add_xml_str + "<cheque_no>" + chqNo + "</cheque_no>";
			add_xml_str = add_xml_str + "<amount>" + Amount + "</amount>";
			add_xml_str = add_xml_str + "<reason>" + Reason + "</reason>";
			add_xml_str = add_xml_str + "<return_date>" + returnDate + "</return_date>"; // to be populated later
			add_xml_str = add_xml_str + "<provider_no>" + providerNo + "</provider_no><company_flag>N</company_flag></ExternalBouncedCheques>"; // to
			// be
			// populated
			// later

		}
		DigitalAO.mLogger.debug("RLOSCommon"+ "Internal liab tag Cration: "
				+ add_xml_str);
		return add_xml_str;
	}
	
	public String ExternalBureauIndividualProducts(IFormReference formObject,String control,String Data) {
		DigitalAO.mLogger.debug("iRBL_DecTech java file"+"inside ExternalBureauIndividualProducts : ");
		/*String sQuery = "select CifId,AgreementId,LoanType,ProviderNo,LoanStat,CustRoleType,LoanApprovedDate,LoanMaturityDate,OutstandingAmt,TotalAmt,PaymentsAmt,TotalNoOfInstalments,RemainingInstalments,WriteoffStat,WriteoffStatDt,CreditLimit,OverdueAmt,NofDaysPmtDelay,MonthsOnBook,lastrepmtdt,IsCurrent,CurUtilRate,DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,'' as qc_Amnt,'' as Qc_emi,'' as Cac_indicator,Take_Over_Indicator,Consider_For_Obligations,case when IsDuplicate= '1' then 'Y' else 'N' end,avg_utilization,DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount from USR_0_iRBL_ExternalExpo_LoanDetails with (nolock) where wi_name= '"+ getWorkitemName(formObject)
		+ "'  and LoanStat != 'Pipeline'   union select CifId,CardEmbossNum,CardType,ProviderNo,CardStatus,CustRoleType,StartDate,ClosedDate,CurrentBalance,'' as col6,PaymentsAmount,NoOfInstallments,'' as col5,WriteoffStat,WriteoffStatDt,CashLimit,OverdueAmount,NofDaysPmtDelay,MonthsOnBook,lastrepmtdt,IsCurrent,CurUtilRate,DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,qc_amt,qc_emi,CAC_Indicator,Take_Over_Indicator,Consider_For_Obligations,case when IsDuplicate= '1' then 'Y' else 'N' end,avg_utilization,DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount from USR_0_iRBL_ExternalExpo_CardDetails with (nolock) where wi_name  =  '"
		+ getWorkitemName(formObject)+ "' and cardstatus != 'Pipeline'   union select CifId,AcctId,AcctType,ProviderNo,AcctStat,CustRoleType,StartDate,ClosedDate,OutStandingBalance,TotalAmount,PaymentsAmount,'','',WriteoffStat,WriteoffStatDt,CreditLimit,OverdueAmount,NofDaysPmtDelay,MonthsOnBook,'',IsCurrent,CurUtilRate,DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,'','','','',isnull(Consider_For_Obligations,'true'),case when IsDuplicate= '1' then 'Y' else 'N' end,'',DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount from USR_0_iRBL_ExternalExpo_AccountDetails with (nolock)  where wi_name  =  '"+getWorkitemName(formObject)+"' union select CifId,ServiceID,ServiceType,ProviderNo,ServiceStat,CustRoleType,SubscriptionDt,SvcExpDt,'','','','','',WriteoffStat,WriteoffStatDt,'',OverDueAmount,NofDaysPmtDelay,MonthsOnBook,'',IsCurrent,CurUtilRate,'',DPD30_Last6Months,AECBHistMonthCnt,DPD5_Last3Months,'','','','',isnull(Consider_For_Obligations,'true'),case when IsDuplicate= '1' then 'Y' else 'N' end,'',DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount from USR_0_iRBL_ExternalExpo_ServicesDetails with (nolock)  where ServiceStat='Active' and wi_name  =  '"+getWorkitemName(formObject)+"'";
		*/
		String sQuery = "select CifId,AgreementId,LoanType,ProviderNo,LoanStat,CustRoleType,LoanApprovedDate,LoanMaturityDate,OutstandingAmt,TotalAmt,PaymentsAmt,TotalNoOfInstalments,RemainingInstalments,WriteoffStat,WriteoffStatDt,CreditLimit,OverdueAmt,NofDaysPmtDelay,MonthsOnBook,lastrepmtdt,IsCurrent,CurUtilRate,DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,'' as qc_Amnt,'' as Qc_emi,'' as Cac_indicator,Take_Over_Indicator,Consider_For_Obligations,case when IsDuplicate= '1' then 'Y' else 'N' end,avg_utilization,DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount,RelatedPartyId from USR_0_iRBL_ExternalExpo_LoanDetails with (nolock) where wi_name= '"+ getWorkitemName(formObject)
		+ "'  and LoanStat != 'Pipeline'   union select CifId,CardEmbossNum,CardType,ProviderNo,CardStatus,CustRoleType,StartDate,ClosedDate,CurrentBalance,'' as col6,PaymentsAmount,NoOfInstallments,'' as col5,WriteoffStat,WriteoffStatDt,CashLimit,OverdueAmount,NofDaysPmtDelay,MonthsOnBook,lastrepmtdt,IsCurrent,CurUtilRate,DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,qc_amt,qc_emi,CAC_Indicator,Take_Over_Indicator,Consider_For_Obligations,case when IsDuplicate= '1' then 'Y' else 'N' end,avg_utilization,DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount,RelatedPartyId from USR_0_iRBL_ExternalExpo_CardDetails with (nolock) where wi_name  =  '"
		+ getWorkitemName(formObject)+ "' and cardstatus != 'Pipeline'   union select CifId,AcctId,AcctType,ProviderNo,AcctStat,CustRoleType,StartDate,ClosedDate,OutStandingBalance,TotalAmount,PaymentsAmount,'','',WriteoffStat,WriteoffStatDt,CreditLimit,OverdueAmount,NofDaysPmtDelay,MonthsOnBook,'',IsCurrent,CurUtilRate,DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,'','','','',isnull(Consider_For_Obligations,'true'),case when IsDuplicate= '1' then 'Y' else 'N' end,'',DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount,RelatedPartyId from USR_0_iRBL_ExternalExpo_AccountDetails with (nolock)  where wi_name  =  '"+getWorkitemName(formObject)+"' union select CifId,ServiceID,ServiceType,ProviderNo,ServiceStat,CustRoleType,SubscriptionDt,SvcExpDt,'','','','','',WriteoffStat,WriteoffStatDt,'',OverDueAmount,NofDaysPmtDelay,MonthsOnBook,'',IsCurrent,CurUtilRate,'',DPD30_Last6Months,AECBHistMonthCnt,DPD5_Last3Months,'','','','',isnull(Consider_For_Obligations,'true'),case when IsDuplicate= '1' then 'Y' else 'N' end,'',DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount,RelatedPartyId from USR_0_iRBL_ExternalExpo_ServicesDetails with (nolock)  where ServiceStat='Active' and wi_name  =  '"+getWorkitemName(formObject)+"'";
		
		String add_xml_str = "";
		
		List<List<String>> OutputXML = formObject.getDataFromDB(sQuery);
		
		for (int i = 0; i < OutputXML.size(); i++) {

			String CifId = "";
			String AgreementId = "";
			String ContractType = "";
			String provider_no = "";
			String phase = "";
			String CustRoleType = "";
			String start_date = "";
			String close_date = "";
			String OutStanding_Balance = "";
			String TotalAmt = "";
			String PaymentsAmt = "";
			String TotalNoOfInstalments = "";
			String RemainingInstalments = "";
			String WorstStatus = "";
			String WorstStatusDate = "";
			String CreditLimit = "";
			String OverdueAmt = "";
			String NofDaysPmtDelay = "";
			String MonthsOnBook = "";
			String last_repayment_date = "";
			String DPD60Last12Months = "";
			String AECBHistMonthCnt = "";
			String DPD30Last6Months = "";
			String currently_current = "";
			String current_utilization = "";
			String delinquent_in_last_3months = "";
			String QC_Amt = "";
			String QC_emi = "";
			String CAC_Indicator = "";
			String consider_for_obligation = "";
			String Duplicate_flag="";
			String avg_utilization="";
			String DPD60plus_last12month="";
			String DPD5_last12month="";
			String RelatedPartyId="";

			if (!(OutputXML.get(i).get(0) == null || OutputXML.get(i)	.get(0).equals(""))) 
			{
				
				CifId = OutputXML.get(i).get(0).toString();
				
			}
			
			if(!(OutputXML.get(i).get(36) == null || OutputXML.get(i).get(36).equals("")) ){
				RelatedPartyId = OutputXML.get(i).get(36).toString();
			}
			
			if (!(OutputXML.get(i).get(1) == null || OutputXML.get(i).get(1)
					.equals(""))) {
				AgreementId = OutputXML.get(i).get(1).toString();
			}
			if (!(OutputXML.get(i).get(2) == null || OutputXML.get(i).get(2).equals(""))) {
				ContractType = OutputXML.get(i).get(2).toString();
				try {
					String cardquery = "select code from ng_master_contract_type with (nolock) where description='"+ ContractType + "'";
					
					List<List<String>> cardqueryXML = formObject.getDataFromDB(cardquery);
					ContractType = cardqueryXML.get(0).get(0);
					
				} catch (Exception e) {
					DigitalAO.mLogger.debug("ExternalBureauIndividualProducts ContractType Exception"+ e+ "Exception");

					ContractType = OutputXML.get(i).get(2).toString();
				}
			}
			if (!(OutputXML.get(i).get(3) == null || OutputXML.get(i).get(3)
					.equals(""))) {
				provider_no = OutputXML.get(i).get(3).toString();
			}
			if (!(OutputXML.get(i).get(4) == null || OutputXML.get(i).get(4)
					.equals(""))) {
				phase = OutputXML.get(i).get(4).toString();
				if (phase.startsWith("A")) {
					phase = "A";
				} else {
					phase = "C";
				}
			}
			if (!(OutputXML.get(i).get(5) == null || OutputXML.get(i).get(5)
					.equals(""))) {
				CustRoleType = OutputXML.get(i).get(5).toString();
				String sQueryCustRoleType = "select code from ng_master_role_of_customer with(nolock) where Description='"+CustRoleType+"'";
				
				List<List<String>> sQueryCustRoleTypeXML = formObject.getDataFromDB(sQueryCustRoleType);
				try{
					if(sQueryCustRoleTypeXML!=null && sQueryCustRoleTypeXML.size()>0 && sQueryCustRoleTypeXML.get(0)!=null){
						CustRoleType=sQueryCustRoleTypeXML.get(0).get(0);
					}
				}
				catch(Exception e){
					DigitalAO.mLogger.info("Exception occured at sQueryCombinedLimit for"+sQueryCustRoleTypeXML);

				}	}
			if (!(OutputXML.get(i).get(6) == null || OutputXML.get(i).get(6)
					.equals(""))) {
				start_date = OutputXML.get(i).get(6).toString();
			}
			if (!(OutputXML.get(i).get(7) == null || OutputXML.get(i).get(7)
					.equals(""))) {
				close_date = OutputXML.get(i).get(7).toString();
			}
			if (!(OutputXML.get(i).get(8) == null || OutputXML.get(i).get(8)
					.equals(""))) {
				OutStanding_Balance = OutputXML.get(i).get(8).toString();
			}
			if (!(OutputXML.get(i).get(9) == null || OutputXML.get(i).get(9)
					.equals(""))) {
				TotalAmt = OutputXML.get(i).get(9).toString();
			}
			if (!(OutputXML.get(i).get(10) == null || OutputXML.get(i).get(10)
					.equals(""))) {
				PaymentsAmt = OutputXML.get(i).get(10).toString();
			}
			if (!(OutputXML.get(i).get(11) == null || OutputXML.get(i).get(11)
					.equals(""))) {
				TotalNoOfInstalments = OutputXML.get(i).get(11).toString();
			}
			if (!(OutputXML.get(i).get(12) == null || OutputXML.get(i).get(12)
					.equals(""))) {
				RemainingInstalments = OutputXML.get(i).get(12).toString();
			}
			if (!(OutputXML.get(i).get(13) == null || OutputXML.get(i).get(13)
					.equals(""))) {
				WorstStatus = OutputXML.get(i).get(13).toString();
			}
			if (!(OutputXML.get(i).get(14) == null || OutputXML.get(i).get(14)
					.equals(""))) {
				WorstStatusDate = OutputXML.get(i).get(14).toString();
			}
			if (!(OutputXML.get(i).get(15) == null || OutputXML.get(i).get(15)
					.equals(""))) {
				CreditLimit = OutputXML.get(i).get(15).toString();
			}
			if (!(OutputXML.get(i).get(16) == null || OutputXML.get(i).get(16)
					.equals(""))) {
				OverdueAmt = OutputXML.get(i).get(16).toString();
			}
			if (!(OutputXML.get(i).get(17) == null || OutputXML.get(i).get(17)
					.equals(""))) {
				NofDaysPmtDelay = OutputXML.get(i).get(17).toString();
			}
			if (!(OutputXML.get(i).get(18) == null || OutputXML.get(i).get(18)
					.equals(""))) {
				MonthsOnBook = OutputXML.get(i).get(18).toString();
			}
			if (!(OutputXML.get(i).get(19) == null || OutputXML.get(i).get(19)
					.equals(""))) {
				last_repayment_date = OutputXML.get(i).get(19).toString();
			}
			if (!(OutputXML.get(i).get(20) == null || OutputXML.get(i).get(20)
					.equals(""))) {
				currently_current = OutputXML.get(i).get(20).toString();
			}
			if (!(OutputXML.get(i).get(21) == null || OutputXML.get(i).get(21)
					.equals(""))) {
				current_utilization = OutputXML.get(i).get(21).toString();
			}
			if (!(OutputXML.get(i).get(22) == null || OutputXML.get(i).get(22)
					.equals(""))) {
				DPD30Last6Months = OutputXML.get(i).get(22).toString();
			}
			if (!(OutputXML.get(i).get(23) == null || OutputXML.get(i).get(23)
					.equals(""))) {
				DPD60Last12Months = OutputXML.get(i).get(23).toString();
			}
			if (!(OutputXML.get(i).get(24) == null || OutputXML.get(i).get(24)
					.equals(""))) {
				AECBHistMonthCnt = OutputXML.get(i).get(24).toString();
			}

			if (!(OutputXML.get(i).get(25) == null || OutputXML.get(i).get(25)
					.equals(""))) {
				delinquent_in_last_3months = OutputXML.get(i).get(25).toString();
			}
			if (!(OutputXML.get(i).get(26) == null || OutputXML.get(i).get(26)
					.equals(""))) {
				QC_Amt = OutputXML.get(i).get(26).toString();
			}
			if (!(OutputXML.get(i).get(27) == null || OutputXML.get(i).get(27)
					.equals(""))) {
				QC_emi = OutputXML.get(i).get(27).toString();
			}
			if (!(OutputXML.get(i).get(28) == null || OutputXML.get(i).get(28)
					.equals(""))) {
				CAC_Indicator = OutputXML.get(i).get(28).toString();
				if (CAC_Indicator != null && !("".equalsIgnoreCase(CAC_Indicator))) {
					if ("true".equalsIgnoreCase(CAC_Indicator)) 
					{
						CAC_Indicator = "Y";
					} 
					else 
					{
						CAC_Indicator = "N";
					}
				}
			}

			String TakeOverIndicator = "";
			if (!(OutputXML.get(i).get(29) == null || "".equals(OutputXML.get(i).get(29)))) {
				TakeOverIndicator = OutputXML.get(i).get(29).toString();
				if (TakeOverIndicator != null && !("".equalsIgnoreCase(TakeOverIndicator))) 
				{
					if ("true".equalsIgnoreCase(TakeOverIndicator)) 
					{
						TakeOverIndicator = "Y";
					} 
					else 
					{
						TakeOverIndicator = "N";
					}
				}
			}
			if (!(OutputXML.get(i).get(30) == null || "".equals(OutputXML.get(i).get(30))))
			{
				consider_for_obligation = OutputXML.get(i).get(30).toString();
				if (consider_for_obligation != null && !("".equalsIgnoreCase(consider_for_obligation))) {
					if ("true".equalsIgnoreCase(consider_for_obligation)) 
					{
						consider_for_obligation = "Y";
					} 
					else 
					{
						consider_for_obligation = "N";
					}
				}
			}
			if(!(OutputXML.get(i).get(31) == null || OutputXML.get(i).get(31).equals("")) ){
				Duplicate_flag = OutputXML.get(i).get(31).toString();
			}
			
			if(!(OutputXML.get(i).get(32) == null || OutputXML.get(i).get(32).equals("")) ){
				avg_utilization = OutputXML.get(i).get(32).toString();
			}
			if(!(OutputXML.get(i).get(33) == null || OutputXML.get(i).get(33).equals("")) ){
				DPD5_last12month = OutputXML.get(i).get(33).toString();
			}
			if(!(OutputXML.get(i).get(34) == null || OutputXML.get(i).get(34).equals("")) ){
				DPD60plus_last12month = OutputXML.get(i).get(34).toString();
			}
			String MaximumOverDueAmount = "";
			if(!(OutputXML.get(i).get(35) == null || OutputXML.get(i).get(35).equals("")) ){
				DPD60plus_last12month = OutputXML.get(i).get(34).toString();
			}
			
			String Company_flag="N";
			if(CifId.trim().equalsIgnoreCase(getControlValue("CIF_NUMBER",formObject).trim()))
			{
				Company_flag="Y";
			}
			else
			{
				String sQuery1="select TOP 1 COMPANYFLAG from USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS WITH(nolock) WHERE WI_NAME='"+getWorkitemName(formObject)+"' AND RELATEDPARTYID='"+RelatedPartyId+"'";
				List<List<String>> CmpFlgQry = formObject.getDataFromDB(sQuery1);
				for (int k = 0; k < CmpFlgQry.size(); k++) {
					Company_flag = CmpFlgQry.get(k).get(0);
					break;
				}
				
			}
			if(!"".equalsIgnoreCase(CifId))
			{
				add_xml_str = add_xml_str + "<ExternalBureauIndividualProducts><applicant_id>" + CifId + "</applicant_id>";
			}
			else
			{
				add_xml_str = add_xml_str + "<ExternalBureauIndividualProducts><applicant_id>" + RelatedPartyId + "</applicant_id>";
			}
			
			add_xml_str = add_xml_str + "<external_bureau_individual_products_id>" + AgreementId + "</external_bureau_individual_products_id>";
			add_xml_str = add_xml_str + "<contract_type>" + ContractType + "</contract_type>";
			add_xml_str = add_xml_str + "<provider_no>" + provider_no + "</provider_no>";
			add_xml_str = add_xml_str + "<phase>" + phase + "</phase>";
			add_xml_str = add_xml_str + "<role_of_customer>" + CustRoleType + "</role_of_customer>";
			add_xml_str = add_xml_str + "<start_date>" + start_date + "</start_date>";

			add_xml_str = add_xml_str + "<close_date>" + close_date + "</close_date>";
			add_xml_str = add_xml_str + "<outstanding_balance>" + OutStanding_Balance + "</outstanding_balance>";
			add_xml_str = add_xml_str + "<total_amount>" + TotalAmt + "</total_amount>";
			add_xml_str = add_xml_str + "<payments_amount>" + PaymentsAmt + "</payments_amount>";
			add_xml_str = add_xml_str + "<total_no_of_instalments>" + TotalNoOfInstalments + "</total_no_of_instalments>";
			add_xml_str = add_xml_str + "<no_of_remaining_instalments>" + RemainingInstalments + "</no_of_remaining_instalments>";
			add_xml_str = add_xml_str + "<worst_status>" + WorstStatus + "</worst_status>";
			add_xml_str = add_xml_str + "<worst_status_date>" + WorstStatusDate + "</worst_status_date>";

			add_xml_str = add_xml_str + "<credit_limit>" + CreditLimit + "</credit_limit>";
			add_xml_str = add_xml_str + "<overdue_amount>" + OverdueAmt + "</overdue_amount>";
			add_xml_str = add_xml_str + "<no_of_days_payment_delay>" + NofDaysPmtDelay + "</no_of_days_payment_delay>";
			add_xml_str = add_xml_str + "<mob>" + MonthsOnBook + "</mob>";
			add_xml_str = add_xml_str + "<last_repayment_date>" + last_repayment_date + "</last_repayment_date>";
			if (currently_current != null && "1".equalsIgnoreCase(currently_current))
			{
				add_xml_str = add_xml_str + "<currently_current>Y</currently_current>";
			}
			else 
			{
				add_xml_str = add_xml_str + "<currently_current>N</currently_current>";
			}
		
			add_xml_str = add_xml_str + "<dpd_30_last_6_mon>" + DPD30Last6Months + "</dpd_30_last_6_mon>";

			add_xml_str = add_xml_str + "<dpd_60p_in_last_12_mon>" + DPD60plus_last12month + "</dpd_60p_in_last_12_mon>";
			add_xml_str = add_xml_str + "<no_months_aecb_history>" + AECBHistMonthCnt + "</no_months_aecb_history>";
			add_xml_str = add_xml_str + "<maximum_overdue_amount>" + MaximumOverDueAmount + "</maximum_overdue_amount>";
			add_xml_str = add_xml_str + "<delinquent_in_last_3months>" + delinquent_in_last_3months + "</delinquent_in_last_3months>";
			add_xml_str = add_xml_str + "<company_flag>" + Company_flag + "</company_flag>";
			add_xml_str = add_xml_str + "<consider_for_obligation>Y</consider_for_obligation>";
			add_xml_str = add_xml_str + "<duplicate_flag>" + Duplicate_flag + "</duplicate_flag>";
			add_xml_str = add_xml_str + "<avg_utilization>" + avg_utilization + "</avg_utilization>";

			add_xml_str = add_xml_str + "</ExternalBureauIndividualProducts>";
		}
		DigitalAO.mLogger.debug("RLOSCommon"+ "Internal liab tag Cration: "	+ add_xml_str);
		return add_xml_str;
	}
	
	public String ExternalBureauPipelineProducts(IFormReference formObject,String control,String Data) {
		DigitalAO.mLogger.info("RLOSCommon java file"+"inside ExternalBureauPipelineProducts : ");
		
		String sQuery = "select AgreementId,ProviderNo,LoanType,LoanDesc,CustRoleType,Datelastupdated,TotalAmt,TotalNoOfInstalments,'' as col1,NoOfDaysInPipeline,isnull(Consider_For_Obligations,'true'),case when IsDuplicate= '1' then 'Y' else 'N' end,CifId from USR_0_iRBL_ExternalExpo_LoanDetails with (nolock) where wi_name  =  '"+getWorkitemName(formObject)+"' and LoanStat = 'Pipeline' union select CardEmbossNum,ProviderNo,CardType,CardTypeDesc,CustRoleType,LastUpdateDate,'' as col2,NoOfInstallments,TotalAmount,NoOfDaysInPipeLine,isnull(Consider_For_Obligations,'true'),case when IsDuplicate= '1' then 'Y' else 'N' end,CifId  from USR_0_iRBL_ExternalExpo_CardDetails with (nolock) where wi_name  =  '"+getWorkitemName(formObject)+"' and cardstatus = 'Pipeline'";
		
		String add_xml_str = "";
		List<List<String>> OutputXML = formObject.getDataFromDB(sQuery);
		

		for (int i = 0; i < OutputXML.size(); i++) {
			String cifId = "";
			String agreementID = "";
			String ProviderNo = "";
			String contractType = "";
			String productType = "";
			String role = "";
			String lastUpdateDate = "";
			String TotAmt = "";
			String noOfInstalmnt = "";
			String creditLimit = "";
			String noOfDayinPpl = "";
			String consider_for_obligation="";
			String ppl_Duplicate_flag="";

			if (!(OutputXML.get(i).get(0) == null || OutputXML.get(i).get(0)
					.equals(""))) {
				agreementID = OutputXML.get(i).get(0).toString();
			}
			if (!(OutputXML.get(i).get(1) == null || OutputXML.get(i).get(1)
					.equals(""))) {
				ProviderNo = OutputXML.get(i).get(1).toString();
			}
			if (OutputXML.get(i).get(2) != null	&& !OutputXML.get(i).get(2).isEmpty() && !"".equals(OutputXML.get(i).get(2)) && !"null".equalsIgnoreCase(OutputXML.get(i).get(2))) 
			{
				contractType = OutputXML.get(i).get(2).toString();
				
				try {
					String cardquery = "select code from ng_master_contract_type with (nolock) where description='"+ contractType + "'";
				
					List<List<String>> cardqueryXML = formObject.getDataFromDB(cardquery);
					contractType = cardqueryXML.get(0).get(0);
					
				} catch (Exception e) {
					DigitalAO.mLogger.info("ExternalBureauIndividualProducts ContractType Exception"+ e+ "Exception");

					contractType = OutputXML.get(i).get(2).toString();
				}
			}
			if (!(OutputXML.get(i).get(3) == null || OutputXML.get(i).get(3)
					.equals(""))) {
				productType = OutputXML.get(i).get(3).toString();
			}
				
			
			
			if (!(OutputXML.get(i).get(4) == null || OutputXML.get(i).get(4)
					.equals(""))) {
				role = OutputXML.get(i).get(4).toString();
				
				String sQueryCustRoleType = "select code from ng_master_role_of_customer with(nolock) where Description='"+role+"'";
				DigitalAO.mLogger.info("CustRoleType"+sQueryCustRoleType);
				List<List<String>> sQueryCustRoleTypeXML = formObject.getDataFromDB(sQueryCustRoleType);
				try{
					if(sQueryCustRoleTypeXML!=null && sQueryCustRoleTypeXML.size()>0 && sQueryCustRoleTypeXML.get(0)!=null){
						role=sQueryCustRoleTypeXML.get(0).get(0);
					}
				}
				catch(Exception e){
					DigitalAO.mLogger.info("Exception occured at sQueryCombinedLimit for"+sQueryCustRoleTypeXML);
					role = OutputXML.get(i).get(4).toString();
				}
			}
			if (OutputXML.get(i).get(5) != null	&& !OutputXML.get(i).get(5).isEmpty() && !"".equals(OutputXML.get(i).get(5)) && !"null".equalsIgnoreCase(OutputXML.get(i).get(5))) 
			{
				lastUpdateDate = OutputXML.get(i).get(5).toString();
			}
			if (OutputXML.get(i).get(6) != null	&& !OutputXML.get(i).get(6).isEmpty() && !OutputXML.get(i).get(6).equals("")	&& !"null".equalsIgnoreCase(OutputXML.get(i).get(6))) {
				TotAmt = OutputXML.get(i).get(6).toString();
			}
			if (!(OutputXML.get(i).get(7) == null || OutputXML.get(i).get(7).equals(""))) 
			{
				noOfInstalmnt = OutputXML.get(i).get(7).toString();
			}
			if (!(OutputXML.get(i).get(8) == null || OutputXML.get(i).get(8).equals(""))) 
			{
				creditLimit = OutputXML.get(i).get(8).toString();
			}
			if (OutputXML.get(i).get(9) != null	&& !OutputXML.get(i).get(9).isEmpty() && !OutputXML.get(i).get(9).equals("")&& !"null".equalsIgnoreCase(OutputXML.get(i).get(9))) 
			{
				noOfDayinPpl = OutputXML.get(i).get(9).toString();
			}
			if(!(OutputXML.get(i).get(10) == null || "".equals(OutputXML.get(i).get(10))) ){
				consider_for_obligation = OutputXML.get(i).get(10);
				if (consider_for_obligation != null && !("".equalsIgnoreCase(consider_for_obligation))){
					if ("true".equalsIgnoreCase(consider_for_obligation)){
						consider_for_obligation="Y";
					}
					else {
						consider_for_obligation="N";
					}
				}
			}
			if(OutputXML.get(i).get(11)!=null && !OutputXML.get(i).get(11).isEmpty() &&  !"".equalsIgnoreCase(OutputXML.get(i).get(11)) && !"null".equalsIgnoreCase(OutputXML.get(i).get(11)) ){
				ppl_Duplicate_flag = OutputXML.get(i).get(11);
			}
			if(OutputXML.get(i).get(12)!=null && !OutputXML.get(i).get(12).isEmpty() &&  !"".equalsIgnoreCase(OutputXML.get(i).get(12)) && !"null".equalsIgnoreCase(OutputXML.get(i).get(12)) ){
				cifId = OutputXML.get(i).get(12);
			}
			
			
			add_xml_str = add_xml_str + "<ExternalBureauPipelineProducts><applicant_ID>" + cifId + "</applicant_ID>";
			add_xml_str = add_xml_str + "<external_bureau_pipeline_products_id>" + agreementID + "</external_bureau_pipeline_products_id>";
			add_xml_str = add_xml_str + "<ppl_provider_no>" + ProviderNo + "</ppl_provider_no>";
			add_xml_str = add_xml_str + "<ppl_type_of_contract>" + contractType + "</ppl_type_of_contract>";
			add_xml_str = add_xml_str + "<ppl_type_of_product>" + productType + "</ppl_type_of_product>";
			add_xml_str = add_xml_str + "<ppl_phase>" + "PIPELINE" + "</ppl_phase>";
			add_xml_str = add_xml_str + "<ppl_role>" + role + "</ppl_role>";

			add_xml_str = add_xml_str + "<ppl_date_of_last_update>" + lastUpdateDate + "</ppl_date_of_last_update>";
			add_xml_str = add_xml_str + "<ppl_total_amount>" + TotAmt + "</ppl_total_amount>";
			add_xml_str = add_xml_str + "<ppl_no_of_instalments>" + noOfInstalmnt + "</ppl_no_of_instalments>";
			add_xml_str = add_xml_str + "<ppl_credit_limit>" + creditLimit + "</ppl_credit_limit>";
			//changed by nikhil PCSP-822
			add_xml_str = add_xml_str + "<ppl_no_of_days_in_pipeline>" + noOfDayinPpl + "</ppl_no_of_days_in_pipeline><company_flag>N</company_flag><ppl_consider_for_obligation>"+consider_for_obligation+"</ppl_consider_for_obligation><ppl_duplicate_flag>"+ppl_Duplicate_flag+"</ppl_duplicate_flag></ExternalBureauPipelineProducts>"; // to
			// be
			// populated
			// later

		}
		DigitalAO.mLogger.info("iRBL DecTech external pipeline"	+ add_xml_str);
		return add_xml_str;
	}
	
	
	public String parseDectechResponse(String outputResponse, IFormReference formObject,String control,String StringData){
		String retVal = "";
		try{
			DigitalAO.mLogger.info("Inside Dectech response parsing");
			if(outputResponse.indexOf("<PM_Reason_Codes>")>-1 && outputResponse.indexOf("<PM_Outputs>")>-1 ){
				DigitalAO.mLogger.info("inside if PM_codes");
				//Below code added by nikhil for DOM parsing Of dectech
				String Application_output = outputResponse.substring(outputResponse.lastIndexOf("<Application>"),outputResponse.lastIndexOf("</Application>")+14);
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				InputSource is = new InputSource(new StringReader(Application_output));
				Document doc = builder.parse(is);
				doc.getDocumentElement().normalize();

				String squery="";
				String Output_TAI="";
				String Output_Decision="";
				String Output_Final_DBR="";
				String Output_Existing_DBR  ="";
				String Output_Eligible_Amount="";
				String Output_Delegation_Authority="";
				String output_accomodation="";
				String Grade="";
				String Output_Interest_Rate="";
				String Output_Net_Salary_DBR="";
				String ReasonCode="";
				String DeviationCode="";
				String Output_Accommodation_Allowance="";
				String Category="";
				int a=0,b=0,c=0;
				
				final WDGeneralData wdgeneralObj;	
				String cabinetName = getCabinetName(formObject);
				String sessionId = getSessionId(formObject);
				String userName = getUserName(formObject);
				wdgeneralObj = formObject.getObjGeneralData();
				sessionId = wdgeneralObj.getM_strDMSSessionId();
				String jtsIp = wdgeneralObj.getM_strJTSIP();
				String jtsPort = wdgeneralObj.getM_strJTSPORT();
				String strOutputXml = "";
				
				double cac_calc_limit=0.0;
				String cac_calc_limit_str = null;
				
				String Output_Affordable_EMI="";
				String combined_limit="";
				String Output_CPV_Waiver="";
				//Setting the value in ELIGANDPROD info
				if (doc.getElementsByTagName("Output_TAI").getLength()>0){
					Output_TAI = doc.getElementsByTagName("Output_TAI").item(0).getTextContent();
					if (Output_TAI!=null){
						try{
							//setControlValue("cmplx_Liability_New_TAI", Output_TAI,false);
							//setControlValue("cmplx_EligibilityAndProductInfo_FinalTAI", Output_TAI,false);

						}
						catch (Exception e){
							DigitalAO.mLogger.info("Dectech error"+ "Exception:"+e.getMessage());
						}


					}
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_TAI"+Output_TAI);
				}

				
				if (doc.getElementsByTagName("Output_Delegation_Authority").getLength()>0){
					Output_Delegation_Authority = doc.getElementsByTagName("Output_Delegation_Authority").item(0).getTextContent();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Delegation_Authority: "+Output_Delegation_Authority);
					//setControlValue("cmplx_DEC_HighDeligatinAuth", Output_Delegation_Authority,false);
				}
				
				if (doc.getElementsByTagName("Output_Accommodation_Allowance").getLength()>0){
					Output_Accommodation_Allowance = doc.getElementsByTagName("Output_Accommodation_Allowance").item(0).getTextContent();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Accommodation_Allowance"+Output_Accommodation_Allowance);
					//setControlValue("cmplx_IncomeDetails_CompanyAcc", Output_Accommodation_Allowance,false);
				}
				
				if (doc.getElementsByTagName("Output_Decision").getLength()>0){
					Output_Decision = doc.getElementsByTagName("Output_Decision").item(0).getTextContent();
					if (Output_Decision!=null){
						try{
							if ("D".equalsIgnoreCase(Output_Decision)){
								Output_Decision="Declined";
							}	
							else if ("A".equalsIgnoreCase(Output_Decision)){
								Output_Decision="Approve";
							}	
							else if ("R".equalsIgnoreCase(Output_Decision)){
								Output_Decision="Refer";
							}	
							//setControlValue("cmplx_DEC_DectechDecision", Output_Decision,false);
							//setControlValue("dectech_label",Output_Decision);
						}
						catch (Exception e){
							DigitalAO.mLogger.info("Dectech error"+ "Exception:"+e.getMessage());
						}
					}
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_TAI"+Output_TAI);
				}

	
				if (doc.getElementsByTagName("Output_Final_DBR").getLength()>0){

					Output_Final_DBR = doc.getElementsByTagName("Output_Final_DBR").item(0).getTextContent();
					if (Output_Final_DBR!=null){
						//setControlValue("cmplx_EligibilityAndProductInfo_FinalDBR", Output_Final_DBR);
					}
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Final_DBR"+Output_Final_DBR);
				}
				if (doc.getElementsByTagName("Output_Interest_Rate").getLength()>0){
					Output_Interest_Rate = doc.getElementsByTagName("Output_Interest_Rate").item(0).getTextContent();
					if (Output_Interest_Rate!=null){
						//setControlValue("cmplx_EligibilityAndProductInfo_InterestRate", Output_Interest_Rate);
					}
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Interest_Rate"+Output_Interest_Rate);

				}
				//Setting the value in ELIGANDPROD info
				//Setting the value in lIABILITY info
				if (doc.getElementsByTagName("Output_Existing_DBR").getLength()>0){
					Output_Existing_DBR = doc.getElementsByTagName("Output_Existing_DBR").item(0).getTextContent();
					if (Output_Existing_DBR!=null){
						//setControlValue("cmplx_Liability_New_DBR", Output_Existing_DBR);
					}
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Existing_DBR"+Output_Existing_DBR);

				}
				if (doc.getElementsByTagName("Output_Affordable_EMI").getLength()>0){
					Output_Affordable_EMI = doc.getElementsByTagName("Output_Affordable_EMI").item(0).getTextContent();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Affordable_EMI"+Output_Affordable_EMI);

				}
				

				if (doc.getElementsByTagName("Output_Net_Salary_DBR").getLength()>0){
					Output_Net_Salary_DBR = doc.getElementsByTagName("Output_Net_Salary_DBR").item(0).getTextContent();
					if (Output_Net_Salary_DBR!=null){
						//setControlValue("cmplx_Liability_New_DBRNet", Output_Net_Salary_DBR);
					}
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Net_Salary_DBR"+Output_Net_Salary_DBR);

				}
				//Setting the value in lIABILITY info
				//Setting the value in creditCard iFrame
//sagarika//sagarika for CPV decision NOT apllicable if cpv wavier is Y 
				if (doc.getElementsByTagName("Output_CPV_Waiver").getLength()>0){
//sagarika 
					Output_CPV_Waiver = doc.getElementsByTagName("Output_CPV_Waiver").item(0).getTextContent();
					if (Output_CPV_Waiver!=null){
						if(Output_CPV_Waiver.equalsIgnoreCase("Y"))
						{	
							DigitalAO.mLogger.info("Output_CPV_Waiver"+Output_CPV_Waiver);
							//setControlValue("CPV_WAVIER",Output_CPV_Waiver);
							//setControlValue("cmplx_CustDetailVerification_Decision","Not Applicable");
							//String is_cpv_wav="Y";
							//iRBL.mLogger.info("CPV_WAVIER"+getControlValue("CPV_WAVIER"));
						}
						else
						{
							//setControlValue("CPV_WAVIER",Output_CPV_Waiver);
							//iRBL.mLogger.info("CPV_WAVIER"+getControlValue("CPV_WAVIER"));	
						}
						DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Final_DBR"+Output_Final_DBR);
					}
				}

				if (doc.getElementsByTagName("output_accomodation").getLength()>0){
					output_accomodation = doc.getElementsByTagName("output_accomodation").item(0).getTextContent();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Existing_DBR"+output_accomodation);
					if (output_accomodation!=null){
						//setControlValue("cmplx_IncomeDetails_CompanyAcc", output_accomodation);
					}
				}
				if (outputResponse.contains("Grade")){
					Grade = outputResponse.substring(outputResponse.indexOf("<Grade>")+7,outputResponse.indexOf("</Grade>")); ;
					if (Grade!=null){
						//commented by saurabh on 29th May wrt JIRA-10051. Confirmed with Srinidhi this field not to populate in Decision history and on CAM.
					
					}
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Grade"+Grade);

				}
				
				String Output_Loan_Policy = "";
				if (doc.getElementsByTagName("Output_Loan_Policy").getLength()>0){
					Output_Loan_Policy = doc.getElementsByTagName("Output_Loan_Policy").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Loan_Policy: "+Output_Loan_Policy);
				}
				String Output_Loan_Eligibility = "";
				if (doc.getElementsByTagName("Output_Loan_Eligibility").getLength()>0){
					Output_Loan_Eligibility = doc.getElementsByTagName("Output_Loan_Eligibility").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Loan_Eligibility: "+Output_Loan_Eligibility);
				}
				String Output_Loan_Client = "";
				if (doc.getElementsByTagName("Output_Loan_Client").getLength()>0){
					Output_Loan_Client = doc.getElementsByTagName("Output_Loan_Client").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Loan_Client: "+Output_Loan_Client);
				}
				String Output_CFF_Policy = "";
				if (doc.getElementsByTagName("Output_CFF_Policy").getLength()>0){
					Output_CFF_Policy = doc.getElementsByTagName("Output_CFF_Policy").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_CFF_Policy: "+Output_CFF_Policy);
				}
				String Output_CFF_Eligibility = "";
				if (doc.getElementsByTagName("Output_CFF_Eligibility").getLength()>0){
					Output_CFF_Eligibility = doc.getElementsByTagName("Output_CFF_Eligibility").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_CFF_Eligibility: "+Output_CFF_Eligibility);
				}
				String Output_CFF_Client = "";
				if (doc.getElementsByTagName("Output_CFF_Client").getLength()>0){
					Output_CFF_Client = doc.getElementsByTagName("Output_CFF_Client").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_CFF_Client: "+Output_CFF_Client);
				}
				String Output_AVG_EMI_Policy = "";
				if (doc.getElementsByTagName("Output_AVG_EMI_Policy").getLength()>0){
					Output_AVG_EMI_Policy = doc.getElementsByTagName("Output_AVG_EMI_Policy").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_AVG_EMI_Policy: "+Output_AVG_EMI_Policy);
				}
				String Output_AVG_EMI_Eligibility = "";
				if (doc.getElementsByTagName("Output_AVG_EMI_Eligibility").getLength()>0){
					Output_AVG_EMI_Eligibility = doc.getElementsByTagName("Output_AVG_EMI_Eligibility").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_AVG_EMI_Eligibility: "+Output_AVG_EMI_Eligibility);
				}
				String Output_AVG_EMI_Client = "";
				if (doc.getElementsByTagName("Output_AVG_EMI_Client").getLength()>0){
					Output_AVG_EMI_Client = doc.getElementsByTagName("Output_AVG_EMI_Client").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_AVG_EMI_Client: "+Output_AVG_EMI_Client);
				}
				
				if (doc.getElementsByTagName("Output_Eligible_Amount").getLength()>0){
					Output_Eligible_Amount = doc.getElementsByTagName("Output_Eligible_Amount").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Eligible_Amount"+Output_Eligible_Amount);
					setControlValue("TOTAL_ELIGIBILITY_AMOUNT", Output_Eligible_Amount,formObject); // setting total eligibility amount from dectech in loan section on 23/09/2021
				}
				String Output_Eligible_Amount_path = "";
				if (doc.getElementsByTagName("Output_Eligible_Amount_Path").getLength()>0){
					Output_Eligible_Amount_path = doc.getElementsByTagName("Output_Eligible_Amount_Path").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_Eligible_Amount_Path"+Output_Eligible_Amount_path);
				}
				
				String Output_NTC_Amount = "";
				if (doc.getElementsByTagName("Output_NTC_Amount").getLength()>0){
					Output_NTC_Amount = doc.getElementsByTagName("Output_NTC_Amount").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_NTC_Amount"+Output_NTC_Amount);
				}
				
				String Output_BBG_Existing_DBR = "";
				if (doc.getElementsByTagName("Output_BBG_Existing_DBR").getLength()>0){
					Output_BBG_Existing_DBR = doc.getElementsByTagName("Output_BBG_Existing_DBR").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_BBG_Existing_DBR"+Output_BBG_Existing_DBR);
				}
				
				String Output_BBG_Final_DBR = "";
				if (doc.getElementsByTagName("Output_BBG_Final_DBR").getLength()>0){
					Output_BBG_Final_DBR = doc.getElementsByTagName("Output_BBG_Final_DBR").item(0).getTextContent().trim();
					DigitalAO.mLogger.info("$$outputXMLMsg "+"inside outpute get Output_BBG_Final_DBR"+Output_BBG_Final_DBR);
				}
				
				int EligAmtTableSize = formObject.getDataFromGrid("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS").size();
				for (int i = 0; i < EligAmtTableSize; i++) 
				{
					String Criteria = formObject.getTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 0);
					
					if("Turnover To Loan".equalsIgnoreCase(Criteria.trim()))
					{
						formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 1,Output_Loan_Policy);
						formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 2,Output_Loan_Client);
						formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 3,Output_Loan_Eligibility);
					}
					else if("Turnover TO CFF".equalsIgnoreCase(Criteria.trim()))
					{
						formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 1,Output_CFF_Policy);
						formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 2,Output_CFF_Client);
						formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 3,Output_CFF_Eligibility);
					}
					else if("Average Balance To EMI".equalsIgnoreCase(Criteria.trim()))
					{
						formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 1,Output_AVG_EMI_Policy);
						formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 2,Output_AVG_EMI_Client);
						formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 3,Output_AVG_EMI_Eligibility);
					}
					else if("Eligible Amount".equalsIgnoreCase(Criteria.trim()))
					{
						//formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 1,"");
						//formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 2,"");
						formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 3,Output_Eligible_Amount);
					}
					else if("NTC Amount".equalsIgnoreCase(Criteria.trim()))
					{
						//formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 1,"");
						//formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 2,"");
						formObject.setTableCellValue("Q_USR_0_IRBL_ELIGIBLE_AMT_DTLS", i, 3,Output_NTC_Amount);
					}
				}
				
				//Indicative DBR Section
				int IndDBRTableSize = formObject.getDataFromGrid("Q_USR_0_IRBL_INDICATIVE_DTLS").size();
				for (int i = 0; i < IndDBRTableSize; i++) 
				{
					String Criteria = formObject.getTableCellValue("Q_USR_0_IRBL_INDICATIVE_DTLS", i, 0);
					
					if("Turnover Method (EMI / Actual Turnover)".equalsIgnoreCase(Criteria.trim()))
					{
						formObject.setTableCellValue("Q_USR_0_IRBL_INDICATIVE_DTLS", i, 1,Output_BBG_Existing_DBR);
						formObject.setTableCellValue("Q_USR_0_IRBL_INDICATIVE_DTLS", i, 2,Output_BBG_Final_DBR);
					}
				}
				
				// Setting NTC Value in Loan section
				try {
					if(!Output_Eligible_Amount.trim().equalsIgnoreCase("") || getControlValue("TOTAL_CURRENT_OUTSTANDING",formObject).trim() !="")
					{
						double iOutput_Eligible_Amount = 0;
						if(!Output_Eligible_Amount.trim().equalsIgnoreCase(""))
							iOutput_Eligible_Amount = Double.parseDouble(Output_Eligible_Amount.trim());
						
						double iTOTAL_CURRENT_OUTSTANDING = 0;
						if(!getControlValue("TOTAL_CURRENT_OUTSTANDING",formObject).trim().equalsIgnoreCase(""))
							iTOTAL_CURRENT_OUTSTANDING = Double.parseDouble(getControlValue("TOTAL_CURRENT_OUTSTANDING",formObject).trim());
					
						double NTCValue = iOutput_Eligible_Amount - iTOTAL_CURRENT_OUTSTANDING;
						// setControlValue("NTC_VALUE", String.valueOf(NTCValue)); // POLP-10412 - display value <Output_NTC_Amount/> from response
					}
				}
				catch (Exception e)
				{
					DigitalAO.mLogger.info("Exception in NTC Value calculation: "+e.getMessage());
				}
				
				setControlValue("NTC_VALUE", String.valueOf(Output_NTC_Amount),formObject); // POLP-10412 - display value <Output_NTC_Amount/> from response
				
				outputResponse = outputResponse.substring(outputResponse.indexOf("<ProcessManagerResponse>")+24, outputResponse.indexOf("</ProcessManagerResponse>"));
				outputResponse = "<?xml version=\"1.0\"?><Response>" + outputResponse;
				outputResponse = outputResponse+"</Response>";
				DigitalAO.mLogger.info("$$outputResponse "+"inside outpute get outputResponse"+outputResponse);
				DocumentBuilderFactory factory_1 = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder_1 = factory_1.newDocumentBuilder();
				InputSource is_1 = new InputSource(new StringReader(outputResponse));

				Document doc_1 = builder_1.parse(is_1);
				doc_1.getDocumentElement().normalize();

				DigitalAO.mLogger.info("Root element :" +doc.getDocumentElement().getNodeName());

				NodeList nList = doc_1.getElementsByTagName("PM_Reason_Codes");
				
				JSONArray jsonArray=new JSONArray();
				int Approve_counter=0,Refer_decline_counter=0;
				String sReasonCodeDecline="";
				String sReasonCodeRefer="";
				for (int temp = 0; temp < nList.getLength(); temp++) {
					String Reason_Decision="";
					Node nNode = nList.item(temp);


					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						
						
						String subProd="";//getControlValue("cmplx_Product_cmplx_ProductGrid");//2


						ReasonCode= eElement.getElementsByTagName("Reason_Code").item(0).getTextContent();
						Reason_Decision = eElement.getElementsByTagName("Reason_Decision").item(0).getTextContent() ;
						DigitalAO.mLogger.info("dectech ReasonCode---->"+ReasonCode);
						DigitalAO.mLogger.info("dectech Reason_Decision---->"+Reason_Decision);
						String Reason_Description=eElement.getElementsByTagName("Reason_Description").item(0).getTextContent() ;
						DigitalAO.mLogger.info("dectech Reason_Description before replacing &gt;---->"+Reason_Description);
						Reason_Description=Reason_Description.replaceAll("&gt;", ">");
						Reason_Description=Reason_Description.replaceAll("&lt;", "<");
						DigitalAO.mLogger.info("dectech Reason_Description after replacing &gt;---->"+Reason_Description);

						
						JSONObject obj=new JSONObject();
						
						obj.put("Deviation", ReasonCode);
						obj.put("Deviation Description", Reason_Description);
						obj.put("Deviation Remarks", "");
						jsonArray.add(obj);
						
						if(ReasonCode.startsWith("D"))
						{
							if("".equalsIgnoreCase(sReasonCodeDecline))
								sReasonCodeDecline = "'"+ReasonCode+"'";
							else
								sReasonCodeDecline = sReasonCodeDecline + ",'"+ReasonCode+"'";							
						}
						
						if(ReasonCode.startsWith("R"))
						{
							if("".equalsIgnoreCase(sReasonCodeRefer))
								sReasonCodeRefer = "'"+ReasonCode+"'";
							else
								sReasonCodeRefer = sReasonCodeRefer + ",'"+ReasonCode+"'";							
						}
						
					}					
					DigitalAO.mLogger.info("sReasonCodeDecline list ---->"+sReasonCodeDecline);
					DigitalAO.mLogger.info("sReasonCodeRefer list ---->"+sReasonCodeRefer);
				}
				if(jsonArray.size() > 0)
				{
					formObject.clearTable("Q_USR_0_IRBL_SUMMARY_DEV_DTLS");
					formObject.addDataToGrid("Q_USR_0_IRBL_SUMMARY_DEV_DTLS", jsonArray);
				}
				
				// Setting All Statuses(Considered as Pass) in policy grid - First
				int policyGridSize = formObject.getDataFromGrid("Q_USR_0_IRBL_POLICY_CHECKLIST_DTLS").size();
				DigitalAO.mLogger.info("Setting All Statuses(Considered as Pass) in policy grid - First");
				for (int i = 0; i < policyGridSize; i++) 
				{
					String PolicyValue = formObject.getTableCellValue("Q_USR_0_IRBL_POLICY_CHECKLIST_DTLS", i, 1).trim();
					String isDefault = formObject.getTableCellValue("Q_USR_0_IRBL_POLICY_CHECKLIST_DTLS", i, 2).trim();
					if("Y".equalsIgnoreCase(isDefault))
					{
						formObject.setTableCellValue("Q_USR_0_IRBL_POLICY_CHECKLIST_DTLS", i, 1,"Pass");
					}
				}
				///////////////////////////////////////////////////////////////////////
				
				// Setting Refer Statuses in policy grid - Second
				if(!"".equalsIgnoreCase(sReasonCodeRefer))
				{
					String sQuery = "SELECT pc.CRITERIA, pc.CRITERIA_CODE, pd.DISPLAY_RESULT FROM USR_0_IRBL_POLICY_CHCEKLIST_MASTER  pc WITH(nolock), "
							+ "USR_0_IRBL_POLICY_DECTECH_REASONCODE_MAPPING pd WITH(nolock) "
							+ "WHERE pc.CRITERIA_CODE = pd.CRITERIA_CODE "
							+ "AND pd.REASONCODE IN("+sReasonCodeRefer+") AND pd.ISACTIVE='Y'";
					DigitalAO.mLogger.info("Policy Checklist ReasonCode refer Mapping: "+sQuery);
					List<List<String>> reasonlist = formObject.getDataFromDB(sQuery);
	
					for (List<String> row : reasonlist) {
						String CriteriaCodeMaster = row.get(1).trim();
						String DISPLAY_RESULT = row.get(2).trim();
						
						if(!"".equalsIgnoreCase(CriteriaCodeMaster))
						{
							int polictGridSize = formObject.getDataFromGrid("Q_USR_0_IRBL_POLICY_CHECKLIST_DTLS").size();
							for (int i = 0; i < polictGridSize; i++) 
							{
								String CriteriaCodeGrid = formObject.getTableCellValue("Q_USR_0_IRBL_POLICY_CHECKLIST_DTLS", i, 3).trim();
								
								if(CriteriaCodeGrid.equalsIgnoreCase(CriteriaCodeMaster))
								{
									formObject.setTableCellValue("Q_USR_0_IRBL_POLICY_CHECKLIST_DTLS", i, 1,DISPLAY_RESULT);
									break;
								}
							}
						}
					}
				}
				///////////////////////////////////////////////////////////////////////
				
				// Setting Decline Statuses in policy grid - Last
				if(!"".equalsIgnoreCase(sReasonCodeDecline))
				{
					String sQuery = "SELECT pc.CRITERIA, pc.CRITERIA_CODE, pd.DISPLAY_RESULT FROM USR_0_IRBL_POLICY_CHCEKLIST_MASTER  pc WITH(nolock), "
							+ "USR_0_IRBL_POLICY_DECTECH_REASONCODE_MAPPING pd WITH(nolock) "
							+ "WHERE pc.CRITERIA_CODE = pd.CRITERIA_CODE "
							+ "AND pd.REASONCODE IN("+sReasonCodeDecline+") AND pd.ISACTIVE='Y'";
					DigitalAO.mLogger.info("Policy Checklist ReasonCode decline Mapping: "+sQuery);
					List<List<String>> reasonlist = formObject.getDataFromDB(sQuery);
	
					for (List<String> row : reasonlist) {
						String CriteriaCodeMaster = row.get(1).trim();
						String DISPLAY_RESULT = row.get(2).trim();
						
						if(!"".equalsIgnoreCase(CriteriaCodeMaster))
						{
							int polictGridSize = formObject.getDataFromGrid("Q_USR_0_IRBL_POLICY_CHECKLIST_DTLS").size();
							for (int i = 0; i < polictGridSize; i++) 
							{
								String CriteriaCodeGrid = formObject.getTableCellValue("Q_USR_0_IRBL_POLICY_CHECKLIST_DTLS", i, 3).trim();
								
								if(CriteriaCodeGrid.equalsIgnoreCase(CriteriaCodeMaster))
								{
									formObject.setTableCellValue("Q_USR_0_IRBL_POLICY_CHECKLIST_DTLS", i, 1,DISPLAY_RESULT);
									break;
								}
							}
						}
					}
				}
				///////////////////////////////////////////////////////////////////////
				
								

				// Setting All Statuses(Considered as Go) in basic lead grid - First
				String sQuery = "SELECT CRITERIA_CODE FROM USR_0_IRBL_BASIC_LEAD_FILTRATION_MASTER WITH (nolock) WHERE "
						+ "IsDECTECH_BASED = 'Y' AND ISACTIVE='Y'";
				DigitalAO.mLogger.info("Basic Lead ReasonCode All other Mapping: "+sQuery);
				List<List<String>> reasonlist = formObject.getDataFromDB(sQuery);

				for (List<String> row : reasonlist) {
					String CriteriaCodeMaster = row.get(0).trim();
					
					if(!"".equalsIgnoreCase(CriteriaCodeMaster))
					{
						int BasicGridSize = formObject.getDataFromGrid("Q_USR_0_IRBL_BASIC_LEAD_FIL_DTLS").size();
						for (int i = 0; i < BasicGridSize; i++) 
						{
							String DisplayValue = formObject.getTableCellValue("Q_USR_0_IRBL_BASIC_LEAD_FIL_DTLS", i, 2).trim();
							String isDefault = formObject.getTableCellValue("Q_USR_0_IRBL_BASIC_LEAD_FIL_DTLS", i, 3).trim();
							String CriteriaCodeGrid = formObject.getTableCellValue("Q_USR_0_IRBL_BASIC_LEAD_FIL_DTLS", i, 4).trim();
							
							if(CriteriaCodeGrid.equalsIgnoreCase(CriteriaCodeMaster))
							{
								if("Y".equalsIgnoreCase(isDefault))
								{
									formObject.setTableCellValue("Q_USR_0_IRBL_BASIC_LEAD_FIL_DTLS", i, 2,"Go");
									break;
								}
							}
						}
					}
				}
				///////////////////////////////////////////////////////////////////////
				
				// Setting Refer Statuses in basic lead grid - Second
				if(!"".equalsIgnoreCase(sReasonCodeRefer))
				{
					sQuery = "SELECT bl.CRITERIA, bl.CRITERIA_CODE, bd.DISPLAY_RESULT FROM USR_0_IRBL_BASIC_LEAD_FILTRATION_MASTER  bl WITH(nolock), "
							+ "USR_0_IRBL_BASICLEAD_DECTECH_REASONCODE_MAPPING bd WITH(nolock) "
							+ "WHERE bl.CRITERIA_CODE = bd.CRITERIA_CODE "
							+ "AND bd.REASONCODE IN("+sReasonCodeRefer+") AND bd.ISACTIVE='Y'";
					DigitalAO.mLogger.info("Basic Lead ReasonCode refer Mapping: "+sQuery);
					reasonlist = formObject.getDataFromDB(sQuery);
	
					for (List<String> row : reasonlist) {
						String CriteriaCodeMaster = row.get(1).trim();
						String DISPLAY_RESULT = row.get(2).trim();
						
						if(!"".equalsIgnoreCase(CriteriaCodeMaster))
						{
							int BasicGridSize = formObject.getDataFromGrid("Q_USR_0_IRBL_BASIC_LEAD_FIL_DTLS").size();
							for (int i = 0; i < BasicGridSize; i++) 
							{
								String CriteriaCodeGrid = formObject.getTableCellValue("Q_USR_0_IRBL_BASIC_LEAD_FIL_DTLS", i, 4).trim();
								
								if(CriteriaCodeGrid.equalsIgnoreCase(CriteriaCodeMaster))
								{
									formObject.setTableCellValue("Q_USR_0_IRBL_BASIC_LEAD_FIL_DTLS", i, 2,DISPLAY_RESULT);
									break;
								}
							}
						}
					}
				}
				///////////////////////////////////////////////////////////////////////
				
				// Setting Decline Statuses in basic lead grid - Last 
				if(!"".equalsIgnoreCase(sReasonCodeDecline))
				{
					sQuery = "SELECT bl.CRITERIA, bl.CRITERIA_CODE, bd.DISPLAY_RESULT FROM USR_0_IRBL_BASIC_LEAD_FILTRATION_MASTER  bl WITH(nolock), "
							+ "USR_0_IRBL_BASICLEAD_DECTECH_REASONCODE_MAPPING bd WITH(nolock) "
							+ "WHERE bl.CRITERIA_CODE = bd.CRITERIA_CODE "
							+ "AND bd.REASONCODE IN("+sReasonCodeDecline+") AND bd.ISACTIVE='Y'";
					DigitalAO.mLogger.info("Basic Lead ReasonCode decline Mapping: "+sQuery);
					reasonlist = formObject.getDataFromDB(sQuery);
	
					for (List<String> row : reasonlist) {
						String CriteriaCodeMaster = row.get(1).trim();
						String DISPLAY_RESULT = row.get(2).trim();
						
						if(!"".equalsIgnoreCase(CriteriaCodeMaster))
						{
							int BasicGridSize = formObject.getDataFromGrid("Q_USR_0_IRBL_BASIC_LEAD_FIL_DTLS").size();
							for (int i = 0; i < BasicGridSize; i++) 
							{
								String CriteriaCodeGrid = formObject.getTableCellValue("Q_USR_0_IRBL_BASIC_LEAD_FIL_DTLS", i, 4).trim();
								
								if(CriteriaCodeGrid.equalsIgnoreCase(CriteriaCodeMaster))
								{
									formObject.setTableCellValue("Q_USR_0_IRBL_BASIC_LEAD_FIL_DTLS", i, 2,DISPLAY_RESULT);
									break;
								}
							}
						}
					}
				}
				///////////////////////////////////////////////////////////////////////
				
								
				retVal = "Success";
				
			}
		}
		catch(Exception e){
			DigitalAO.mLogger.info("Dectech error"+ "Exception:"+e.getMessage());
			retVal = "Failure";
		}
		return retVal;
	}
	
	public static double Cas_Limit(double aff_emi,double rate,double tenureMonths)
	{
		double pmt;
		try{
			double new_rate = (rate/100)/12;
			 pmt = (aff_emi)*(1-Math.pow(1+new_rate,-tenureMonths))/new_rate;
			DigitalAO.mLogger.info("CC_Common"+"final_rate_new 1ST pmt11 : " + pmt);
		}
		catch(Exception e){
			pmt=0;
		}
		return pmt;
		
	}
}


