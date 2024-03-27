package com.newgen.iforms.user;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.newgen.iforms.custom.IFormReference;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;

public class Digital_CC_DecTechCall extends Digital_CC_Common {
	
	LinkedHashMap<String,String> executeXMLMapMain = new LinkedHashMap<String,String>();
	public static String XMLLOG_HISTORY="NG_DCC_XMLLOG_HISTORY";
	
	public String onevent(IFormReference iformObj, String control, String StringData) throws IOException {
		String wiName=getWorkitemName(iformObj);
		String WSNAME=getActivityName(iformObj);
		String returnValue = "";
		String MQ_response="";
		String cabinetName = getCabinetName(iformObj);
		/*String wi_name = getWorkitemName(iformObj);
		String ws_name = getActivityName(iformObj);
		String sessionID = getSessionId(iformObj);
		String userName = getUserName(iformObj);*/
		String decisionValue = "";
		String attributesTag = "";
		String socketServerIP = "";
		String socketServerPort = "";
		
	
	
		MQ_response = MQ_connection_response(iformObj,control,StringData);
		
		
		//	return MQ_response;
		if (MQ_response.indexOf("<MessageStatus>") != -1)
			returnValue = MQ_response.substring(MQ_response
					.indexOf("<MessageStatus>")
					+ "</MessageStatus>".length() - 1, MQ_response
					.indexOf("</MessageStatus>"));

		if (MQ_response.contains("INVALID SESSION"))
			returnValue = "INVALID SESSION";

		if ("Success".equalsIgnoreCase(returnValue))
			// returnValue = MQ_response;
			returnValue = "DECTECH CALL SUCCESS";
           //save response data start
		XMLParser xmlParserSocketDetails= new XMLParser(MQ_response);
		Digital_CC.mLogger.debug(" xmlParserSocketDetails : "+xmlParserSocketDetails);
		String SystemErrorCode = xmlParserSocketDetails.getValueOf("SystemErrorCode");
		Digital_CC.mLogger.debug("SystemErrorCode : "+SystemErrorCode+" for WI: "+wiName);
		String SystemErrorMessage = xmlParserSocketDetails.getValueOf("SystemErrorMessage");
		Digital_CC.mLogger.debug("SystemErrorMessage : "+SystemErrorMessage+" for WI: "+wiName);
		if (SystemErrorCode != null && !SystemErrorCode.equals("")){
			decisionValue = "Failed";
			Digital_CC.mLogger.debug("Decision in else : " +decisionValue);
			attributesTag="<Decision>"+decisionValue+"</Decision>";
		} else {
			decisionValue = "Success";
			Digital_CC.mLogger.debug("Decision in success: " +decisionValue);
			attributesTag="<Decision>"+decisionValue+"</Decision>";
		}
		
		// all the below fields are in <Application> tag
		String Output_Decision = xmlParserSocketDetails.getValueOf("Output_Decision");
		Digital_CC.mLogger.debug("Output_Decision: "+Output_Decision+ "WI: "+wiName);
		String Output_NSTP = xmlParserSocketDetails.getValueOf("Output_NSTP");
		Digital_CC.mLogger.debug("Output_NSTP: "+Output_NSTP+ "WI: "+wiName);
		String Output_NSTP_Reason = xmlParserSocketDetails.getValueOf("Output_NSTP_Reason");
		Digital_CC.mLogger.debug("Output_NSTP_Reason: "+Output_NSTP_Reason+ "WI: "+wiName);
		String Output_TAI = xmlParserSocketDetails.getValueOf("Output_TAI");
		Digital_CC.mLogger.debug("Output_TAI: "+Output_TAI+ "WI: "+wiName);
		String Output_Final_DBR = xmlParserSocketDetails.getValueOf("Output_Final_DBR");
		Digital_CC.mLogger.debug("Output_Final_DBR: "+Output_Final_DBR+ "WI: "+wiName);
		
		//final limit
		String Output_Eligible_Amount = xmlParserSocketDetails.getValueOf("Output_Eligible_Amount");
		Digital_CC.mLogger.debug("Output_Eligible_Amount: "+Output_Eligible_Amount+ "WI: "+wiName);
		//Output_Affordable_Ratio
		String Output_Affordable_Ratio = xmlParserSocketDetails.getValueOf("Output_Affordable_Ratio");
		Digital_CC.mLogger.debug("Output_Affordable_Ratio: "+Output_Affordable_Ratio+ "WI: "+wiName);
		//Output_Delegation_Authority
		String Output_Delegation_Authority = xmlParserSocketDetails.getValueOf("Output_Delegation_Authority");
		Digital_CC.mLogger.debug("Output_Delegation_Authority: "+Output_Delegation_Authority+ "WI: "+wiName);
		
		
		String is_stp = "";
		if (Output_NSTP != null && !"".equals(Output_NSTP)) {
			if (Output_NSTP.equals("Y"))
				is_stp = "N";
			else if (Output_NSTP.equals("N"))
				is_stp = "Y";
		}
		
		
		
		iformObj.setValue("Is_STP", is_stp);
		iformObj.setValue("Dectech_Decision",Output_Decision);
		iformObj.setValue("Non_STP_reason", Output_NSTP_Reason);
		iformObj.setValue("FinalDBR", Output_Final_DBR);
		iformObj.setValue("FinalTAI", Output_TAI);
		iformObj.setValue("Dectech_Flag", "Y");
		
		iformObj.setValue("Final_Limit",Output_Eligible_Amount);
		iformObj.setValue("DBR_lifeStyle_expenses",Output_Affordable_Ratio);
		iformObj.setValue("delegation_authority",Output_Delegation_Authority);

		 Digital_CC.mLogger.debug("Dectech Response insert in exttable successfull... " );
		
		/*String columnNames = "Is_STP, Dectech_Decision, Non_STP_reason, FinalDBR, FinalTAI, Dectech_Flag";
    	String columnValues = "'" + is_stp + "','"+ Output_Decision +"','"+ Output_NSTP_Reason +"','"+ Output_Final_DBR +"','"+ Output_TAI +"','Y'";
    	String sWhereClause = "WI_NAME='" + wiName + "'";
    	String tableName = "NG_DCC_EXTTABLE";
        String inputXML = Digital_CC_Common.apUpdateInput(getCabinetName(iformObj), getSessionId(iformObj), 
        		tableName, columnNames, columnValues, sWhereClause);
        Digital_CC.mLogger.debug("Input XML for apUpdateInput for " + tableName + " Table : " + inputXML);
        String outputXml = Digital_CC_Common.WFNGExecute(inputXML, socketServerIP, socketServerPort, 1);
        Digital_CC.mLogger.debug("Output XML for apUpdateInput for " + tableName + " Table : " + outputXml);
        XMLParser sXMLParserChild = new XMLParser(outputXml);
        String StrMainCode = sXMLParserChild.getValueOf("MainCode");*/
         
       
		
        	
		
       

		   //save response data end
		
		
		
		
		return returnValue;

	}
	
	
	
	public String MQ_connection_response(IFormReference iformObj,String control,String Data) 
	{
		Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Inside MQ_connection_response function for Digital CC Dectech Call");
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
		String userName = getUserName(iformObj);
		String socketServerIP;
		int socketServerPort;
		wdgeneralObj = iformObj.getObjGeneralData();
	    String sessionID = wdgeneralObj.getM_strDMSSessionId();
		String CIFNumber="";	
		String CallName="";
		StringBuilder finalXml = new StringBuilder();
		
		if(control.equals("Fetch_Manual_Dectech")){
			java.util.Date d1 = new Date();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
			String DateExtra2 = sdf1.format(d1)+"+04:00";
			
			Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Inside Digital CC DecTechCall control--");
			CallName="DECTECH";
			Digital_CC.mLogger.debug("DECTECH Call - WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj));
			
			finalXml = new StringBuilder("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
					  "<soap:Header>\n"+
					   "<ServiceId>CallProcessManager</ServiceId>\n"+
					   "<ServiceType>ProductEligibility</ServiceType>\n"+
					   "<ServiceProviderId>DECTECH</ServiceProviderId>\n"+
					   "<ServiceChannelId>CAS</ServiceChannelId>\n"+
					   "<RequestID>CASTEST</RequestID>\n"+
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
			
			
			//finalXml = finalXml.append(DecTechInputBodyXml(iformObj,control,Data));
			mqInputRequest = getMQInputXML(sessionID, cabinetName,wi_name, ws_name, userName, finalXml);
			Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", mqInputRequest for DecTech call" + mqInputRequest);
		}
		
		try {
			
			Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", userName "+ userName);
			Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", sessionID "+ sessionID);
			
			String sMQuery = "SELECT SocketServerIP,SocketServerPort FROM NG_BPM_MQ_TABLE with (nolock) where ProcessName = 'DCC' and CallingSource = 'Form'";
			List<List<String>> outputMQXML = iformObj.getDataFromDB(sMQuery);
			//CreditCard.mLogger.info("$$outputgGridtXML "+ "sMQuery " + sMQuery);
			if (!outputMQXML.isEmpty()) {
				//CreditCard.mLogger.info("$$outputgGridtXML "+ outputMQXML.get(0).get(0) + "," + outputMQXML.get(0).get(1));
				socketServerIP = outputMQXML.get(0).get(0);
				Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", socketServerIP " + socketServerIP);
				socketServerPort = Integer.parseInt(outputMQXML.get(0).get(1));
				Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", socketServerPort " + socketServerPort);
				if (!("".equalsIgnoreCase(socketServerIP) && socketServerIP == null && socketServerPort==0)) {
					Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Inside serverIP Port " + socketServerPort+ "-socketServerIP-"+socketServerIP);
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
					Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", dout " + dout);
					Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", din " + din);
					mqOutputResponse = "";
					
			
					if (mqInputRequest != null && mqInputRequest.length() > 0) {
						int outPut_len = mqInputRequest.getBytes("UTF-16LE").length;
						Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Final XML output len: "+outPut_len + "");
						mqInputRequest = outPut_len + "##8##;" + mqInputRequest;
						Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", MqInputRequest"+"Input Request Bytes : "+ mqInputRequest.getBytes("UTF-16LE"));
						dout.write(mqInputRequest.getBytes("UTF-16LE"));dout.flush();
					}
					
					byte[] readBuffer = new byte[500];
					int num = din.read(readBuffer);
					if (num > 0) {
			
						byte[] arrayBytes = new byte[num];
						System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
						mqOutputResponse = mqOutputResponse+ new String(arrayBytes, "UTF-16LE");
						Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", mqOutputResponse/message ID :  "+mqOutputResponse);
						
						mqOutputResponse = getOutWtthMessageID("DECTECH",iformObj,mqOutputResponse);
												
						if(mqOutputResponse.contains("&lt;")){
							mqOutputResponse=mqOutputResponse.replaceAll("&lt;", "<");
							mqOutputResponse=mqOutputResponse.replaceAll("&gt;", ">");
							
							
						}
					}
					socket.close();
					return mqOutputResponse;
					
				} else {
					Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", SocketServerIp and SocketServerPort is not maintained "+"");
					Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", SocketServerIp is not maintained "+	socketServerIP);
					Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+",  SocketServerPort is not maintained "+	socketServerPort);
					return "MQ details not maintained";
				}
			} else {
				Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", SOcket details are not maintained in NG_BPM_MQ_TABLE table"+"");
				return "MQ details not maintained";
			}
			
			} catch (Exception e) {
				Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Exception Occured Mq_connection_CC"+e.getStackTrace());
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
					
					Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Final Exception Occured Mq_connection_CC"+e.getStackTrace());
					
				}
			}
	}
	
	
	private static String getMQInputXML(String sessionID, String cabinetName,
			String wi_name, String ws_name, String userName,
			StringBuilder final_xml) {
		//FormContext.getCurrentInstance().getFormConfig();
		Digital_CC.mLogger.debug("inside getMQInputXML function");
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
			Digital_CC.mLogger.debug("getOutWtthMessageID - callName :"+callName);
			
			String wi_name = getWorkitemName(iformObj);
			String str_query = "select OUTPUT_XML from "+ XMLLOG_HISTORY +" with (nolock) where MESSAGE_ID ='"+message_ID+"' and WI_NAME = '"+wi_name+"'";
			Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", inside getOutWtthMessageID str_query: "+ str_query);
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
			Digital_CC.mLogger.debug("This is output xml from DB");
			String outputxmlMasked = outputxml;
			/*Digital_CC.mLogger.debug("The output XML is "+outputxml);
			outputxmlMasked = maskXmlogBasedOnCallType(outputxmlMasked,callName);    
			Digital_CC.mLogger.debug("Masked output XML is "+outputxmlMasked);*/
			Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", getOutWtthMessageID" + outputxmlMasked);				
		}
		catch(Exception e){
			Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Exception BTurred in getOutWtthMessageID" + e.getMessage());
			Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", Exception BTurred in getOutWtthMessageID" + e.getStackTrace());
			outputxml="Error";
		}
		return outputxml;
	}
	
	public String DecTechInputBodyXml(IFormReference iformObj,String control,String Data)
	{
		final WDGeneralData wdgeneralObj;	
		String cabinetName = getCabinetName(iformObj);
		String sessionId = getSessionId(iformObj);
		String userName = getUserName(iformObj);
		wdgeneralObj = iformObj.getObjGeneralData();
		sessionId = wdgeneralObj.getM_strDMSSessionId();
		String jtsIp = wdgeneralObj.getM_strJTSIP();
		String jtsPort = wdgeneralObj.getM_strJTSPORT();
		String wi_name = getWorkitemName(iformObj);
		String socketServerIP;
		int socketServerPort;
		String CIFNumber="";	
		String CallName="";
		String applicationXML= "<![CDATA[<ProcessManagerRequest><Application><Channel>CC</Channel><CallType>PM</CallType><ApplicationNumber>Str_ApplicationNumber</ApplicationNumber></Application><ApplicationDetails><full_eligibility_availed>Str_full_eligibility_availed</full_eligibility_availed><product_type>Str_product_type</product_type><app_category>Str_app_category</app_category><requested_product>Str_requested_product</requested_product><requested_limit>Str_requested_limit</requested_limit><sub_product>Str_sub_product</sub_product><requested_card_product>Str_requested_card_product</requested_card_product><application_type>NEWE</application_type><interest_rate>Str_interest_rate</interest_rate><customer_type>Str_customer_type</customer_type><final_limit>Str_final_limit</final_limit><emi>Str_emi</emi><manual_deviation>Str_manual_deviation</manual_deviation><application_date>Str_application_date</application_date></ApplicationDetails><String_ApplicantDetails><String_InternalBureauData><ExternalBureauData><String_ExternalBureau><String_BouncedCheques><String_Utilization24months><String_History_24months><String_CourtCase><String_ExternalBureauIndividualProducts><String_ExternalBureauPipelineProducts></ExternalBureauData><String_Perfios></ProcessManagerRequest>]]>";;
		
		java.util.Date d1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String AppDate = sdf1.format(d1);
		String Process = "DCC";
		String DateExtra2 = sdf1.format(new Date())+"+04:00";
		
		String ApplicationDetailsXML = "<Application><Channel>"+Process+"</Channel><CallType>PM</CallType><ApplicationNumber>"+getWorkitemName(iformObj)+"</ApplicationNumber><Request_From>BPM</Request_From><application_date>"+AppDate+"</application_date></Application>";

		
		String DBQuery = "SELECT Wi_Name, Application_Type, CIF_ID, isnull(Prefer_product,Product) as 'Product', Product_Desc, Sub_Product, Card_Product, CUSTOMERNAME, PassportNo, EmirateID, MobileNo, employercode, "
					+ "Employer_Name, EmploymentType, EmploymentType_Desc, email_id, Final_Limit, VIPFlag, Title, Title_Desc, FirstName, MiddleName, LastName, dob, Age, Nationality, Nationality_Desc, Designation, Designation_Desc, Cust_Decl_Salary, "
					+ "Prospect_id, FinalDBR, FinalTAI, Passport_expiry, Gender_Code, Gender_Desc, IndusSeg, IndusSeg_Desc, EligibleCardProduct, "
					+ "EligibleCardProduct_Desc, Date_Of_Joining, Selected_Card_Type, Prospect_Creation_Date, FIRCO_Flag, Visa_Expiry, "
					+ "Emirates_Visa, EmID_Expiry, Visa_Sponsor_Name,GCC_National, No_earning_members, Earning_members,"
					+ "Dependents, Cust_Decl_Salary,Net_Salary1,Net_Salary2,Net_Salary3,"
					+"Net_Salary1,Net_salary1_date,Net_Salary2,Net_salary2_date,Net_Salary3,Net_salary3_date,"+
					"Net_Salary4,Net_salary4_date,Net_Salary5,Net_salary5_date, Net_Salary6,Net_salary6_date,Net_Salary7,Net_salary7_date,"+
					"Addn_Perfios_EMI_1,Addn_Perfios_EMI_2,Addn_Perfios_EMI_3,Addn_Perfios_EMI_4,"+
					"Addn_Perfios_EMI_5,Addn_Perfios_EMI_6,Addn_Perfios_EMI_7,Addn_Perfios_EMI_8,"+
					"Addn_Perfios_EMI_9,Addn_Perfios_EMI_10,Addn_Perfios_EMI_11,Addn_Perfios_EMI_12,"+
					"Addn_Perfios_EMI_13,Addn_Perfios_EMI_14,Addn_Perfios_EMI_15,Addn_Perfios_EMI_16,"+
					"Addn_Perfios_EMI_17,Addn_Perfios_EMI_18,Addn_Perfios_EMI_19,Addn_Perfios_EMI_20,Addn_Perfios_CC,"
					+ "Addn_Perfios_OD_Amt,Addn_OD_date,Joint_Acct,High_Value_Deposit,Credit_Amount,Stmt_chq_rtn_last_3mnts,"
					+ "Stmt_chq_rtn_cleared_in30_last_3mnts,Stmt_chq_rtn_last_1mnt,Stmt_chq_rtn_cleared_in30_last_1mnt,"
					+ "Stmt_DDS_rtn_last_3mnts,Stmt_DDS_rtn_cleared_in30_last_3mnts,Stmt_DDS_rtn_last_1mnt,Stmt_DDS_rtn_cleared_in30_last_1mnts,"
					+ "Pensioner,Name_match,FCU_indicator,UW_reqd"
					+ " FROM NG_DCC_EXTTABLE with(nolock) WHERE WI_NAME='" + wi_name + "'";
		
		Digital_CC.mLogger.debug("Select NG_DCC_EXTTABLE Query: "+DBQuery);
		String[] columns = { "Wi_Name", "Application_Type", "CIF_ID", "Product", "Product_Desc", "Sub_Product",
					"Card_Product", "CUSTOMERNAME", "PassportNo", "EmirateID", "MobileNo", "employercode",
					"Employer_Name", "EmploymentType", "EmploymentType_Desc", "email_id", "Final_Limit", "VIPFlag",
					"Title", "Title_Desc", "FirstName", "MiddleName", "LastName", "dob", "Age", "Nationality",
					"Nationality_Desc", "Designation", "Designation_Desc", "Cust_Decl_Salary", "Prospect_id",
					"FinalDBR", "FinalTAI", "Passport_expiry", "Gender", "Gender_Desc", "IndusSeg", "IndusSeg_Desc",
					"EligibleCardProduct", "EligibleCardProduct_Desc", "Date_Of_Joining","Selected_Card_Type","Prospect_Creation_Date",
					"FIRCO_Flag", "Visa_Expiry", "Emirates_Visa", "EmID_Expiry","Visa_Sponsor_Name",
					"GCC_National", "Net_Salary1","No_earning_members", "Earning_members" ,"Dependents","Cust_Decl_Salary",
					"Net_Salary1","Net_Salary2","Net_Salary3",
					"Net_salary1_date", "Net_Salary2", "Net_salary2_date", "Net_Salary3", "Net_salary3_date",
					"Net_Salary4", "Net_salary4_date", "Net_Salary5", "Net_salary5_date", "Net_Salary6",
					"Net_salary6_date", "Net_Salary7", "Net_salary7_date", "Addn_Perfios_EMI_1", "Addn_Perfios_EMI_2",
					"Addn_Perfios_EMI_3", "Addn_Perfios_EMI_4", "Addn_Perfios_EMI_5", "Addn_Perfios_EMI_6",
					"Addn_Perfios_EMI_7", "Addn_Perfios_EMI_8", "Addn_Perfios_EMI_9", "Addn_Perfios_EMI_10",
					"Addn_Perfios_EMI_11", "Addn_Perfios_EMI_12", "Addn_Perfios_EMI_13", "Addn_Perfios_EMI_14",
					"Addn_Perfios_EMI_15", "Addn_Perfios_EMI_16", "Addn_Perfios_EMI_17", "Addn_Perfios_EMI_18",
					"Addn_Perfios_EMI_19", "Addn_Perfios_EMI_20", "Addn_Perfios_CC", "Addn_Perfios_OD_Amt",
					"Addn_OD_date", "Joint_Acct", "High_Value_Deposit", "Credit_Amount", "Stmt_chq_rtn_last_3mnts",
					"Stmt_chq_rtn_cleared_in30_last_3mnts", "Stmt_chq_rtn_last_1mnt",
					"Stmt_chq_rtn_cleared_in30_last_1mnt", "Stmt_DDS_rtn_last_3mnts",
					"Stmt_DDS_rtn_cleared_in30_last_3mnts", "Stmt_DDS_rtn_last_1mnt",
					"Stmt_DDS_rtn_cleared_in30_last_1mnts", "Pensioner", "Name_match", "FCU_indicator", "UW_reqd" };
		
		Map<String,String> ApplicantDetails_Map = getDataFromDB(DBQuery, cabinetName, sessionId, jtsIp, jtsPort, columns);	
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder = stringBuilder.append(applicationXML);
		String requested_xml = stringBuilder.toString().replace(">Str_ApplicationNumber<",">"+wi_name+"<");
		
		/** Application Details Tag**/
       /* SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
		requested_xml = requested_xml.replace(">str_TimeStampyyyymmddhhmmsss<",">"+sdf1.format(new Date())+"<");
        String DateExtra2 = sdf1.format(new Date())+"+04:00";*/
		requested_xml = requested_xml.replace(">Str_full_eligibility_availed<",">Select<")
		.replace(">SDateExtra2tr_product_type<",">"+validateValue(ApplicantDetails_Map.get("Product"))+"<")
		.replace(">Str_app_category<",">BAU<")
		.replace(">Str_requested_product<",">CC<")
		.replace(">Str_requested_limit<",">"+validateValue(ApplicantDetails_Map.get("Requested_Limit"))+"<")
		.replace(">Str_sub_product<",">Digital CC STP<")
		.replace(">Str_requested_card_product<",">"+validateValue(ApplicantDetails_Map.get("Selected_Card_Type"))+"<")
		.replace(">Str_interest_rate<",">0.00<")
		.replace(">Str_customer_type<",">NTB<")
		.replace(">Str_final_limit<",">"+validateValue(ApplicantDetails_Map.get("Final_Limit"))+"<")
		.replace(">Str_emi<",">0.00<")
		.replace(">Str_manual_deviation<",">N<")
		.replace(">Str_application_date<",">"+validateValue(ApplicantDetails_Map.get("Prospect_Creation_Date"))+"<");
		requested_xml = requested_xml.replace(">Str_Wi_Name<",">"+wi_name+"<");
		
		String app_details = sInputXmlApplicantDetails(ApplicantDetails_Map, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_ApplicantDetails>",app_details);
		Digital_CC.mLogger.debug("DCC sInputXmlApplicantDetails : "+ requested_xml);
		
			/** internal   Bureau TAG and  sub-tag **/
			String internal_Bureau = sInputXmlInternalBureau(ApplicantDetails_Map);
			requested_xml = requested_xml.replace("<String_InternalBureauData>",internal_Bureau);
			Digital_CC.mLogger.debug("DCC sInputXmlExternalBureau : "+ internal_Bureau);		
			
			/** External Bureau sub-tag **/
		String external_Bureau = sInputXmlExternalBureau(ApplicantDetails_Map, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_ExternalBureau>",external_Bureau);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBureau : "+ requested_xml);
		
		/** Cheque Bounce sub-Tag **/
		String bounced_Cheques = sInputXmlExternalBouncedCheques(wi_name, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_BouncedCheques>",bounced_Cheques);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBouncedCheques : "+ requested_xml);
		
		/** utilization sub-Tag **/
		String utilization = sInputXmlExternalUtilization(wi_name, cabinetName,sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_Utilization24months>",utilization);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBouncedCheques : "+ requested_xml);
		
		/** utilization sub-Tag **/
		String history = sInputXmlExternalHistory(wi_name, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_History_24months>",history);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBouncedCheques : "+ requested_xml);
		
		/** Court Cases sub-Tag **/
		String court_cases = sInputXmlExternalCourtCase(wi_name, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_CourtCase>",court_cases);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalCourtCase : "+ requested_xml);
		
		/** External Bureau Individual Products sub-Tag **/
		String individual_Products = sInputXmlExternalBureauIndividualProducts(wi_name, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_ExternalBureauIndividualProducts>",individual_Products);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBureauIndividualProducts : "+ requested_xml);
		
		/** External Bureau Pipeline Products sub-tag**/
		String pipeline_Products = sInputXmlExternalBureauPipelineProducts(wi_name, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_ExternalBureauPipelineProducts>",pipeline_Products);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBureauPipelineProducts : "+ requested_xml);
		
			/** External Bureau Pipeline Products sub-tag**/
			String perfios_details = sInputXmlPerfios(ApplicantDetails_Map);
			Digital_CC.mLogger.debug("DCC perfios_details : "+ perfios_details);
			requested_xml = requested_xml.replace("<String_Perfios>",perfios_details);
			
			
		Digital_CC.mLogger.debug("DCC"+ "Final XML : "+ requested_xml);
		String integrationStatus="Success";
		String attributesTag;
		String ErrDesc = "";
		StringBuilder finalString=new StringBuilder(requested_xml);
		
		return requested_xml;
	}
	
	
	private static  Map<String,String> getDataFromDB(String query, String cabinetName, String sessionID, String jtsIP, String jtsPort, String... columns) {
		Digital_CC_Common sgetTagValue = new Digital_CC_Common();
		try{
			Digital_CC.mLogger.debug("Inside function getDataFromDB");
			Digital_CC.mLogger.debug("getDataFromDB query is: "+query);
			String InputXML = Digital_CC_Common.apSelectWithColumnNames(query, cabinetName, sessionID);
			Map<String,String> temp = null;
			String OutXml = WFNGExecute(InputXML, jtsIP, jtsPort, 1);
			OutXml = OutXml.replaceAll("&", "#andsymb#");
			Digital_CC.mLogger.debug("getDataFromDB output xml is: "+OutXml);
			Document recordDoc1 = MapXML.getDocument(OutXml);
			NodeList records1 = recordDoc1.getElementsByTagName("Records");
			if (records1.getLength() > 0) {
				temp = new HashMap<String,String>();
				for(String column : columns) {
					
					String value= sgetTagValue.getTagValue(OutXml, column).replaceAll("#andsymb#", "&");
					//String value= getTagValue(OutXml, column);
					Digital_CC.mLogger.debug("value from getTagValue function is:"+value);
					if(null!=value && !"null".equalsIgnoreCase(value) && !"".equals(value)){
						Digital_CC.mLogger.debug("Setting value of "+column+" as "+value);	
						temp.put(column, value);
					}
					else{
						Digital_CC.mLogger.debug("Setting value of "+column+" as blank");
						temp.put(column, "");
					}
				}
				temp.put("TotalRetrieved", sgetTagValue.getTagValue(OutXml, "TotalRetrieved"));
			}
			return temp;	
		}
		catch(Exception ex){
			Digital_CC.mLogger.debug("Exception in getDataFromDB method + "+printException(ex));
			return null;
		}
	}
	
	
	public static String printException(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exception = sw.toString();
		return exception;	
	}
	

	
	
	
	private static  String sInputXmlApplicantDetails(Map<String, String> applicantDetails_Map, String cabinetName, String sessionID, String jtsIP, String jtsPort) {
		String industry_sector = "";
		String industry_macro = "";
		String industry_micro = "";
		String COMPANY_STATUS_CC = "";
		String COMPANY_STATUS_PL = "";
		
		String  INCLUDED_IN_CC_ALOC = ""; 
		String  INCLUDED_IN_PL_ALOC = "";
		String employercode = validateValue(applicantDetails_Map.get("employercode"));
		if (!employercode.equals("")) {
			String query = "select TOP 1 INDUSTRY_SECTOR, INDUSTRY_MACRO, INDUSTRY_MICRO, COMPANY_STATUS_CC,COMPANY_STATUS_PL, INCLUDED_IN_CC_ALOC, INCLUDED_IN_PL_ALOC from NG_RLOS_ALOC_OFFLINE_DATA WITH(nolock) where EMPLOYER_CODE=main_Employer_code and main_Employer_code = '" + employercode+ "'";
			try {
				List<Map<String,String>> OutputXML_ref = getDataFromDBMap(query, cabinetName, sessionID, jtsIP, jtsPort);
				if(OutputXML_ref.size()>0)
				{
					industry_sector=OutputXML_ref.get(0).get("INDUSTRY_SECTOR");
					industry_macro=OutputXML_ref.get(0).get("INDUSTRY_MACRO");
					industry_micro=OutputXML_ref.get(0).get("INDUSTRY_MICRO");
					COMPANY_STATUS_CC=OutputXML_ref.get(0).get("COMPANY_STATUS_CC");
					COMPANY_STATUS_PL=OutputXML_ref.get(0).get("COMPANY_STATUS_PL");
					INCLUDED_IN_CC_ALOC=OutputXML_ref.get(0).get("INCLUDED_IN_CC_ALOC");
					INCLUDED_IN_PL_ALOC=OutputXML_ref.get(0).get("INCLUDED_IN_PL_ALOC");
				}
			}
			catch(Exception e)
			{
				Digital_CC.mLogger.debug(" Exception occurred in ApplicantDetails Query"+ query);
				Digital_CC.mLogger.debug(" Exception occurred in sInputXmlApplicantDetails()"+ e.getMessage());
			}
		}
		String world_check="N";
		if (validateValue(applicantDetails_Map.get("FIRCO_Flag")).equalsIgnoreCase("Y")) {
			world_check= "Y";
		}
		return "<ApplicantDetails>" + ""
		+"<applicant_id>"+applicantDetails_Map.get("Wi_Name")+"</applicant_id>" + ""
		+"<primary_cif>"+validateValue(applicantDetails_Map.get("CIF_ID"))+"</primary_cif>" + ""
		+"<ref_no>"+applicantDetails_Map.get("Prospect_id")+"</ref_no>" + ""
		+"<wi_name>"+applicantDetails_Map.get("Wi_Name")+"</wi_name>" + ""
		+"<cust_name>"+validateValue(applicantDetails_Map.get("FirstName"))+ " "+validateValue(applicantDetails_Map.get("LastName"))+"</cust_name>" + ""
		+"<emp_type>"+validateValue(applicantDetails_Map.get("EmploymentType"))+"</emp_type>" + ""
		+"<dob>"+validateValue(applicantDetails_Map.get("dob"))+"</dob>" + ""
		+"<age>"+validateValue(applicantDetails_Map.get("Age"))+"</age>" + ""
		//+"<dbr>"+validateValue(applicantDetails_Map.get("FinalDBR"))+"</dbr>" + ""
		//+"<tai>25000.00</tai>" + ""
		+"<nationality>"+validateValue(applicantDetails_Map.get("Nationality"))+"</nationality>" + ""
		+"<resident_flag>Y</resident_flag>" + ""
		+"<world_check>"+world_check+"</world_check>" + ""
		//+"<blacklist_cust_type>I</blacklist_cust_type>" + ""
		//+"<negative_cust_type>I</negative_cust_type>" + ""
		+"<no_of_cheque_bounce_int_3mon_Ind>0</no_of_cheque_bounce_int_3mon_Ind>" + "" //TODO This should be if UW adds in grid in IBPS-If cheque
		+"<no_of_dds_return_int_3mon_Ind>0</no_of_dds_return_int_3mon_Ind>" + "" //TODO This should be if UW adds in grid in IBPS- If DDS
		//+"<external_blacklist_flag>I</external_blacklist_flag>" + ""
		+"<los>"+CalculatLOS(applicantDetails_Map.get("Date_Of_Joining"))+"</los>" + ""
		+"<target_segment_code>DIG</target_segment_code>" + ""
		//+"<avg_credit_turnover_3>66666.67</avg_credit_turnover_3>" + ""
		//+"<avg_bal_3>223145.2</avg_bal_3>" + ""
		+"<current_emp_catogery>"+validateValue(applicantDetails_Map.get("EmploymentType_Desc"))+"</current_emp_catogery>" + "" //TODO
	   //	+"<year_in_uae>04.00</year_in_uae>" + ""
		//+"<ref_relationship>Friend</ref_relationship>" + ""
		+"<visa_expiry_date>"+validateValue(applicantDetails_Map.get("Visa_Expiry"))+"</visa_expiry_date>" + ""
		+"<passport_expiry_date>"+validateValue(applicantDetails_Map.get("EmID_Expiry"))+"</passport_expiry_date>" + ""
		+"<emirates_visa>"+validateValue(applicantDetails_Map.get("Emirates_Visa"))+"</emirates_visa>" + ""
		+"<designation>"+validateValue(applicantDetails_Map.get("Designation_Desc"))+"</designation>" + ""
		//+"<emirates_work>DXB</emirates_work>" + ""
		+"<gender>"+validateValue(applicantDetails_Map.get("Gender_Desc"))+"</gender>" + ""
		+"<cust_mobile_no>"+validateValue(applicantDetails_Map.get("MobileNo"))+"</cust_mobile_no>" + ""
		//+"<salary_with_rakbank>N</salary_with_rakbank>" + ""
		//+"<emirates_of_residence>DXB</emirates_of_residence>" + ""
		+"<emp_name>"+validateValue(applicantDetails_Map.get("Employer_Name"))+"</emp_name>" + ""
		+"<emp_code>"+validateValue(applicantDetails_Map.get("Employercode"))+"</emp_code>" + ""
		//+"<type_of_company>"+validateValue(applicantDetails_Map.get("Employercode"))+"</type_of_company>" + "" //TODO Comp_type field from ALOC-Finacle database
	
		/*+"<NegatedDetails>" + ""    </target_segment_code>
		+"</NegatedDetails>" + ""
		+"<BlacklistDetails>" + ""
		+"</BlacklistDetails>" + ""*/
		//+"Str_NegatedDetails" + "" // TODO multiple records replace by this
		//+"<NegatedDetails>" + ""
		//+"<negative_cust_type>I</negative_cust_type>" + ""
		//+"<internal_negative_flag>N</internal_negative_flag>" + ""
		//+"</NegatedDetails>" + ""
		//+"Str_BlacklistDetails" + ""// TODO multiple records replace by this
		/*+"<BlacklistDetails>" + ""
		+"<blacklist_cust_type>I</blacklist_cust_type>" + ""
		+"<internal_blacklist>N</internal_blacklist>" + ""
		+"</BlacklistDetails>" + ""*/
		//+"<cust_type>"+validateValue(applicantDetails_Map.get("Application_Type"))+"</cust_type>" + ""
		/*+"<bank_no_borrowing_relation_individual>0</bank_no_borrowing_relation_individual>" + ""
		+"<bank_no_borrowing_relation_company>0</bank_no_borrowing_relation_company>" + ""
		+"<AccountDetails>" + ""
		+"</AccountDetails>" + ""*/
		+"<industry_sector>"+industry_sector+"</industry_sector>" + ""
		+"<industry_macro>"+industry_macro+"</industry_macro>" + ""
	    +"<industry_micro>"+industry_micro+"</industry_micro>" + ""
		/*+"<no_bank_other_statement_provided>3</no_bank_other_statement_provided>" + ""*/
	   //+"<aggregate_exposed>25000.00</aggregate_exposed>" + ""
		//+"<bvr>N</bvr>" + ""
		+"<cc_employer_status>"+COMPANY_STATUS_CC+"</cc_employer_status>" + ""
		+"<pl_employer_status>"+COMPANY_STATUS_PL+"</pl_employer_status>" + ""
		+"<included_pl_aloc>"+INCLUDED_IN_PL_ALOC+"</included_pl_aloc>" + ""
		+"<included_cc_aloc>"+INCLUDED_IN_CC_ALOC+"</included_cc_aloc>" + ""
		+"<visa_sponsor>"+validateValue(applicantDetails_Map.get("Visa_Sponsor_Name"))+"</visa_sponsor>" + ""
		+"<country_of_residence>AE</country_of_residence>" + ""
		+"<gcc_national>"+validateValue(applicantDetails_Map.get("GCC_National"))+"</gcc_national>" + ""
		+"<employer_type>N</employer_type>" + ""
		+"<aecb_consent>Y</aecb_consent>" + ""
		+"<No_of_dependants>"+validateValue(applicantDetails_Map.get("Dependents"))+"</No_of_dependants>" + ""
		+"<Other_household_income>"+validateValue(applicantDetails_Map.get("Earning_members"))+"</Other_household_income>" + ""
		+"<No_earning_members>"+validateValue(applicantDetails_Map.get("No_earning_members"))+"</No_earning_members>" + ""
		//+"<marketing_code>BAU</marketing_code>" + ""
		//+"<nmf_flag>N</nmf_flag>" + ""
		/*+"<eff_date_estba>2020-06-28</eff_date_estba>" + ""
		+"<eff_lob>2.05</eff_lob>" + ""
		+"<tlc_issue_date>2020-06-28</tlc_issue_date>" + ""
		+"<no_bank_statement>3</no_bank_statement>" + ""
		+"<no_of_partners>1</no_of_partners>" + ""*/
		//+"<standing_instruction>N</standing_instruction>" + ""
		//+"<vip_flag>"+validateValue(applicantDetails_Map.get("VIPFlag"))+"</vip_flag>" + ""
		//+"<title>"+validateValue(applicantDetails_Map.get("Title_Desc"))+"</title>" + ""
		
		//+"<customer_category>5</customer_category>" + ""
		+"</ApplicantDetails>" ;
	}                                                  
    
	
	private static  List<Map<String,String>> getDataFromDBMap(String query, String cabinetName, String sessionID, String jtsIP, String jtsPort){
		try{
			Digital_CC.mLogger.debug("Inside function getDataFromDB");
			Digital_CC.mLogger.debug("getDataFromDB query is: "+query);
			String InputXML = apSelectWithColumnNames(query, cabinetName, sessionID);
			List<Map<String,String>> temp = new ArrayList<Map<String,String>>();
			String OutXml = WFNGExecute(InputXML, jtsIP, jtsPort, 1);
			OutXml = OutXml.replaceAll("&", "#andsymb#");
			Document recordDoc1 = MapXML.getDocument(OutXml);
			NodeList records1 = recordDoc1.getElementsByTagName("Record");
			if (records1.getLength() > 0) {
				for(int i=0;i<records1.getLength();i++){
					Node n = records1.item(i);
					Map<String,String> t = new HashMap<String,String>();
					if(n.hasChildNodes()) {
						NodeList child = n.getChildNodes();
						for(int j=0;j<child.getLength();j++) {
							Node n1 = child.item(j);
							String column = n1.getNodeName();
							String value = n1.getTextContent().replaceAll("#andsymb#", "&");
							if(null!=value && !"null".equalsIgnoreCase(value) && !"".equals(value)){
								Digital_CC.mLogger.debug("getDataFromDBMap Setting value of "+column+" as "+value);	
								t.put(column, value);
							}
							else{
								Digital_CC.mLogger.debug("getDataFromDBMap Setting value of "+column+" as blank");
								t.put(column, "");
							}
						}
					}
					temp.add(t);
				}
			}
			return temp;	
		}
		catch(Exception ex){
			Digital_CC.mLogger.debug("Exception in getDataFromDBMap method + "+printException(ex));
			return null;
		}
	}
	
	//Calculate DOJ formate should be YYYY-MM-DD
		public static Double CalculatLOS(String DOJ_Str) {
			Double LOS = 0.00;
			try {
				Integer year = Integer.parseInt(DOJ_Str.split("-")[0]);
				Integer month = Integer.parseInt(DOJ_Str.split("-")[1]);
				Integer day = Integer.parseInt(DOJ_Str.split("-")[2]);
				LocalDate DOJ = LocalDate.of(year,month,day);
				LocalDate CD = LocalDate.now();
				Period p = Period.between(DOJ, CD);
				System.out.println(p.getMonths());
				System.out.println(p.getYears());
				LOS += p.getYears();
				LOS = LOS + p.getMonths()/100d;
			} catch (Exception e) {
				e.printStackTrace();
				return LOS;
			}
			
			System.out.println(LOS);
			return LOS;
		}
	
	public static String sInputXmlInternalBureau(Map<String, String> applicantDetails_Map) {
		String internal_bureau=  "<InternalBureauData>"+""
		    +"<InternalBureau>"+""
		      +"<company_flag>N</company_flag>"+""
		    +"</InternalBureau>"+""
		    +"<InternalBouncedCheques>"+""
		      +"<company_flag>N</company_flag>"+""
		    +"</InternalBouncedCheques>"+""
		    +"<InternalBureauIndividualProducts>"+""
		      +"<company_flag>N</company_flag>"+""
		    +"</InternalBureauIndividualProducts>"+""
		    +"<InternalBureauPipelineProducts>"+""
		      +"<company_flag>N</company_flag>"+""
		    +"</InternalBureauPipelineProducts>"+""
		    +"<InternalBureauDBRTAICalc>"+""
		      +"<basic>"+validateValue(applicantDetails_Map.get("Cust_Decl_Salary"))+"</basic>"+""
		      +"<gross_salary>"+validateValue(applicantDetails_Map.get("Cust_Decl_Salary"))+"</gross_salary>"+""
		      +"<net_salary_mon1>"+validateValue(applicantDetails_Map.get("Net_Salary1"))+"</net_salary_mon1>"+""
		      +"<net_salary_mon2>"+validateValue(applicantDetails_Map.get("Net_Salary2"))+"</net_salary_mon2>"+""
		      +"<net_salary_mon3>"+validateValue(applicantDetails_Map.get("Net_Salary3"))+"</net_salary_mon3>"+""
		    +"</InternalBureauDBRTAICalc>"+""
		  +"</InternalBureauData>";
		  return internal_bureau;
	}


	private static String sInputXmlExternalBureau(Map<String, String> applicantDetails_Map, String cabinetName, String sessionID, String jtsIP, String jtsPort) 
	{
		String Wi_Name = applicantDetails_Map.get("Wi_Name");
		
		Digital_CC.mLogger.debug("inside ExternalBureauData : ");
		String sQuery = "select distinct CifId, fullnm,TotalOutstanding,TotalOverdue,NoOfContracts,Total_Exposure,WorstCurrentPaymentDelay,"
				+ "Worst_PaymentDelay_Last24Months,Worst_Status_Last24Months,Nof_Records,NoOf_Cheque_Return_Last3,Nof_DDES_Return_Last3Months,"
				+ "Nof_Cheque_Return_Last6,DPD30_Last6Months,(select max(ExternalWriteOffCheck) ExternalWriteOffCheck "
				+ "from ((select convert(int,isNULL(ExternalWriteOffCheck,0)) ExternalWriteOffCheck  from ng_dcc_cust_extexpo_CardDetails with(nolock) "
				+ "where Wi_Name  = '"+Wi_Name+"' and ProviderNo!='B01'  "
				
				+ "union all select convert(int,isNULL(ExternalWriteOffCheck,0)) ExternalWriteOffCheck "
				+ "from ng_dcc_cust_extexpo_LoanDetails where Wi_Name  = '"+Wi_Name+"' and ProviderNo!='B01' "
				
				+ "union all select convert(int,isNULL(ExternalWriteOffCheck,0)) ExternalWriteOffCheck from ng_dcc_cust_extexpo_AccountDetails "
				+ "where Wi_Name = '"+Wi_Name+"' and ProviderNo!='B01')) as ExternalWriteOffCheck) as 'ExternalWriteOffCheck' ,(select count(*) "
				+ "from (select DisputeAlert from ng_dcc_cust_extexpo_LoanDetails with(nolock) where Wi_Name = '"+Wi_Name+"' and DisputeAlert='1' "
				
				+ "union select DisputeAlert from ng_dcc_cust_extexpo_CardDetails with(nolock) where Wi_Name = '"+Wi_Name+"' and DisputeAlert='1') "
				+ "as tempTable) as 'DisputeAlert'  from ng_dcc_cust_extexpo_Derived with (nolock) where Wi_Name  = '"+Wi_Name+"' and Request_type= 'ExternalExposure'";
		
		Digital_CC.mLogger.debug("ExternalBureauData sQuery" + sQuery+ "");
		String AecbHistQuery = "select isnull(max(AECBHistMonthCnt),0) as AECBHistMonthCnt from ( select MAX(cast(isnull(AECBHistMonthCnt,'0') as int)) as AECBHistMonthCnt  "
						+ "from ng_dcc_cust_extexpo_CardDetails with (nolock) where  Wi_Name  = '"+ Wi_Name + "' and cardtype not in ( '85','99','Communication Services',"
						+ "'TelCo-Mobile Prepaid','101','Current/Saving Account with negative Balance','58','Overdraft') and custroletype not in ('Co-Contract Holder','Guarantor') "
						
						+ "union all select Max(cast(isnull(AECBHistMonthCnt,'0') as int)) as AECBHistMonthCnt from ng_dcc_cust_extexpo_LoanDetails with (nolock) "
						+ "where Wi_Name  = '"+ Wi_Name + "' and loantype not in ('85','99','Communication Services','TelCo-Mobile Prepaid','101',"
						+ "'Current/Saving Account with negative Balance','58','Overdraft') and custroletype not in ('Co-Contract Holder','Guarantor')) as ext_expo";
		
		String add_xml_str = "";
		try {
			
			List<Map<String,String>> OutputXML = getDataFromDBMap(sQuery, cabinetName, sessionID, jtsIP, jtsPort);
			Digital_CC.mLogger.debug("ExternalBureauData list size" + OutputXML.size()+ "");
				
			List<Map<String,String>> AecbHistMap = getDataFromDBMap(AecbHistQuery, cabinetName, sessionID, jtsIP, jtsPort);
			Digital_CC.mLogger.debug("ExternalBureauData list size" + AecbHistMap.size()+ "");
			
			if (OutputXML.size() == 0)
			{
				String aecb_score="";
				String range ="";
				String refNo ="";
				//TODO Include cifid in where condition
				String query = "select top 1 ReferenceNo, AECB_Score,Range from ng_dcc_cust_extexpo_Derived with(nolock) where Wi_Name ='"+Wi_Name
						+"' and Request_Type='ExternalExposure' ORDER BY enquiryDate desc"  ;
				try {
					List<Map<String,String>> OutputXML_ref = getDataFromDBMap(query, cabinetName, sessionID, jtsIP, jtsPort);
					if(OutputXML_ref.size()>0)
					{
						refNo=OutputXML_ref.get(0).get("ReferenceNo");
						aecb_score=OutputXML_ref.get(0).get("AECB_Score");
						range=OutputXML_ref.get(0).get("Range");
					}				
				}
				catch(Exception e)
				{
					Digital_CC.mLogger.debug(" Exception occurred in externalBureauData Query"+ query);
					Digital_CC.mLogger.debug(" Exception occurred in externalBureauData()"+ e.getMessage());
				}
				
				Digital_CC.mLogger.debug( "aecb_score :"+aecb_score+" range :: "+range+" refNo:: "+refNo);
				
				add_xml_str +="<ExternalBureau>" + "";
				add_xml_str +="<applicant_id>" + validateValue(applicantDetails_Map.get("Wi_Name")) + "</applicant_id>" + "";
				add_xml_str +="<bureauone_ref_no>"+refNo+"</bureauone_ref_no>" + "";
				add_xml_str +="<full_name>" + validateValue(applicantDetails_Map.get("FirstName")) +" "+ validateValue(applicantDetails_Map.get("LastName")) + "</full_name>" + ""; //, MiddleName, 
				add_xml_str +="<total_out_bal></total_out_bal>" + "";

				add_xml_str +="<total_overdue></total_overdue>" + "";
				add_xml_str +="<no_default_contract></no_default_contract>" + "";
				add_xml_str +="<total_exposure></total_exposure>" + "";
				add_xml_str +="<worst_curr_pay></worst_curr_pay>" + "";
				add_xml_str +="<worst_curr_pay_24></worst_curr_pay_24>" + "";
				//add_xml_str +="<worst_status_24></worst_status_24>" + "";

				add_xml_str +="<no_of_rec></no_of_rec>" + "";
				add_xml_str +="<cheque_return_3mon></cheque_return_3mon>" + "";
				add_xml_str +="<dds_return_3mon></dds_return_3mon>" + "";
				//add_xml_str +="<cheque_return_6mon>" + Nof_Cheque_Return_Last6 + "</cheque_return_6mon>" + "";
				//add_xml_str +="<dds_return_6mon>" + DPD30_Last6Months + "</dds_return_6mon>" + "";
				//add_xml_str +="<prod_external_writeoff_amount>" + "" + "</prod_external_writeoff_amount>" + "";

				add_xml_str +="<no_months_aecb_history>" + AecbHistMap.get(0).get("AECBHistMonthCnt") + "</no_months_aecb_history>" + "";
				//changes done by shivang for 2.1 
				add_xml_str +="<aecb_score>"+aecb_score+"</aecb_score>" + "";
				add_xml_str +="<range>"+range+"</range>" + "";
				add_xml_str +="<company_flag>N</company_flag></ExternalBureau>" + "";

				Digital_CC.mLogger.debug("dectech External : " + add_xml_str);
				return add_xml_str;
			} 
			else {
				for (Map<String,String> map : OutputXML){
					//String CifId = validateValue(map.get("CifId"));
					String fullnm = validateValue(map.get("fullnm"));
					String TotalOutstanding = validateValue(map.get("TotalOutstanding"));
					String TotalOverdue = validateValue(map.get("TotalOverdue"));
					String NoOfContracts = validateValue(map.get("NoOfContracts"));
					String Total_Exposure = validateValue(map.get("Total_Exposure"));
					String WorstCurrentPaymentDelay = validateValue(map.get("WorstCurrentPaymentDelay"));
					String Worst_PaymentDelay_Last24Months = validateValue(map.get("Worst_PaymentDelay_Last24Months"));
					String Worst_Status_Last24Months = validateValue(map.get("Worst_Status_Last24Months"));
					String Nof_Records = validateValue(map.get("Nof_Records"));
					String NoOf_Cheque_Return_Last3 = validateValue(map.get("NoOf_Cheque_Return_Last3"));
					String Nof_DDES_Return_Last3Months = validateValue(map.get("Nof_DDES_Return_Last3Months"));
					String Nof_Cheque_Return_Last6 = validateValue(map.get("Nof_Cheque_Return_Last6"));
					String DPD30_Last6Months = validateValue(map.get("DPD30_Last6Months"));
					//String ExternalWriteOffCheck = checkValue(map.get("ExternalWriteOffCheck"));
					String dispute_alert=validateValue(map.get("tempTable"));
					//String EnquiryDate = validateValue(map.get("EnquiryDate")); //TODO COLUMN IS NOT IN DERIVED TABLE
					
					String aecb_score=""; 
					String range =""; 
					String refNo ="";
					Digital_CC.mLogger.debug( "aecb_score :"+aecb_score+" range :: "+range+" refNo:: "+refNo);
					 
					if (!dispute_alert.equals("")) {
						try {
							if (Integer.parseInt(dispute_alert) > 0) {
								dispute_alert = "Y";
							} else {
								dispute_alert = "N";
							}
						} catch (NumberFormatException e) {
							dispute_alert = "N";
							Digital_CC.mLogger.error( "NumberFormatException : "+e.getMessage());
						}
					} else {
						dispute_alert = "N";
					}
					
					/*String Company_flag = "N";
					String Ref_query = "select ReferenceNo, AECB_Score,Range from ng_dcc_cust_extexpo_Derived with(nolock) where Wi_Name ='"+Wi_Name+"' and Request_Type='ExternalExposure' and CifId='"+CifId+"'";
					try {
						List<Map<String,String>> OutputXML_ref = getDataFromDBMap(Ref_query, cabinetName, sessionID, jtsIP, jtsPort);
						if(OutputXML_ref.size()>0)
						{
							refNo=OutputXML_ref.get(0).get("ReferenceNo");
							aecb_score=OutputXML_ref.get(0).get("AECB_Score");
							range=OutputXML_ref.get(0).get("Range");
							Company_flag = "Y";
						}				
					}
					catch(Exception e)
					{
						Digital_CC.mLogger.debug(" Exception occurred in externalBureauData Query"+ Ref_query);
						Digital_CC.mLogger.debug(" Exception occurred in externalBureauData()"+ e.getMessage());
					}*/
					
					add_xml_str +="<ExternalBureau>" + "";
					add_xml_str +="<applicant_id>" + validateValue(applicantDetails_Map.get("Wi_Name"))+ "</applicant_id>" + "";
					add_xml_str +="<bureauone_ref_no>"+refNo+"</bureauone_ref_no>" + "";
					add_xml_str +="<full_name>" + fullnm+ "</full_name>" + "";
					add_xml_str +="<total_out_bal>"+ TotalOutstanding + "</total_out_bal>" + "";

					add_xml_str +="<total_overdue>"+ TotalOverdue + "</total_overdue>" + "";
					add_xml_str +="<no_default_contract>"+ NoOfContracts + "</no_default_contract>" + "";
					add_xml_str +="<total_exposure>"+ Total_Exposure + "</total_exposure>" + "";
					add_xml_str +="<worst_curr_pay>"+ WorstCurrentPaymentDelay + "</worst_curr_pay>" + "";
					add_xml_str +="<worst_curr_pay_24>"+ Worst_PaymentDelay_Last24Months+ "</worst_curr_pay_24>" + "";
					add_xml_str +="<worst_status_24>"+ Worst_Status_Last24Months + "</worst_status_24>" + "";

					add_xml_str +="<no_of_rec>" + Nof_Records+ "</no_of_rec>" + "";
					add_xml_str +="<cheque_return_3mon>"+ NoOf_Cheque_Return_Last3+ "</cheque_return_3mon>" + "";
					add_xml_str +="<dds_return_3mon>"+ Nof_DDES_Return_Last3Months+ "</dds_return_3mon>" + "";
					add_xml_str +="<cheque_return_6mon>"+ Nof_Cheque_Return_Last6 + "</cheque_return_6mon>" + "";
					add_xml_str +="<dds_return_6mon>"+ DPD30_Last6Months + "</dds_return_6mon>" + "";
					//add_xml_str = add_xml_str+ "<prod_external_writeoff_amount>" +ExternalWriteOffCheck+ "</prod_external_writeoff_amount>" + "";

					add_xml_str +="<no_months_aecb_history>"+ AecbHistMap.get(0).get("AECBHistMonthCnt")+ "</no_months_aecb_history>" + "";

					//changes done by shivang for 2.1 
					add_xml_str +="<aecb_score>"+aecb_score+"</aecb_score>" + "";
					add_xml_str +="<range>"+range+"</range>" + "";
					add_xml_str +="<AECB_Enquiry_date></AECB_Enquiry_date>" + ""; //"+EnquiryDate+"
					add_xml_str +="<company_flag>N</company_flag>" + "";
					add_xml_str +="<dispute_alert>"+dispute_alert+"</dispute_alert></ExternalBureau>";

				}
				Digital_CC.mLogger.debug("RLOSCommon"+"Internal liab tag Cration: " + add_xml_str);
				return add_xml_str;
			}
		}

		catch (Exception e) {
			Digital_CC.mLogger.debug("DECTECH Exception occurred in externalBureauData()"+ e.getMessage() + " Error: "+ e.getMessage());
			return null;
		}
	}
	
	private static  String sInputXmlExternalCourtCase(String Wi_Name, String cabinetName, String sessionID, String jtsIP, String jtsPort) {
		String court_cases = "";
		String QueryCaseDetails ="select CodOrganization, ProviderCaseNo, ReferenceDate, CaseCategoryCode,CaseOpenDate, isnull(CaseCloseDate,'') as CaseCloseDate, CaseStatusCode," +
				"InitialTotalClaimAmount from ng_dcc_cust_extexpo_CaseDetails where Wi_Name='"+Wi_Name+"'";
		Digital_CC.mLogger.debug("Select ng_dcc_cust_extexpo_CaseDetails Query: "+QueryCaseDetails);
		List<Map<String,String>> list_map = getDataFromDBMap(QueryCaseDetails, cabinetName, sessionID, jtsIP, jtsPort);
		Digital_CC.mLogger.debug("Total Retrieved Records: " + list_map.size());
		System.out.println("Total Retrieved Records: " + list_map.size());
		for (Map<String,String> map : list_map) {
			court_cases += "<CourtCase>"+ ""
			+"<CodOrganization>"+validateValue(map.get("CodOrganization"))+"</CodOrganization>"+ ""
			+"<ProviderCaseNo>"+validateValue(map.get("ProviderCaseNo"))+"</ProviderCaseNo>"+ ""
			+"<ReferenceDate>"+validateValue(map.get("ReferenceDate"))+"</ReferenceDate>"+ ""
			+"<CaseCategoryCode>"+validateValue(map.get("CaseCategoryCode"))+"</CaseCategoryCode>"+ ""
			+"<OpenDate>"+validateValue(map.get("CaseOpenDate"))+"</OpenDate>"+ ""
			+"<CloseDate>"+validateValue(map.get("CaseCloseDate"))+"</CloseDate>"+ ""
			+"<CaseStatusCode>"+validateValue(map.get("CaseStatusCode"))+"</CaseStatusCode>"+ ""
			+"<InitialTotalClaimAmount>"+validateValue(map.get("InitialTotalClaimAmount"))+"</InitialTotalClaimAmount>"+ ""
			+"</CourtCase>";
		}
		
		return court_cases;
	}
	
	private static  String sInputXmlExternalBouncedCheques(String wiName, String cabinetName, String sessionID, String jtsIP, String jtsPort) {
		Digital_CC.mLogger.debug("RLOSCommon java file"+"inside ExternalBouncedCheques : ");
		String sQuery = "SELECT CifId,ChqType,number,amount,reasoncode,returndate,providerno FROM ng_dcc_cust_extexpo_ChequeDetails  with (nolock) "
				+ "where Wi_Name = '" + wiName + "' and Request_Type = 'ExternalExposure'";
		
		Digital_CC.mLogger.debug("ExternalBouncedCheques sQuery" + sQuery+ "");
		String add_xml_str = "";
			
		List<Map<String,String>> OutputXML = getDataFromDBMap(sQuery, cabinetName, sessionID, jtsIP, jtsPort);
		Digital_CC.mLogger.debug("ExternalBouncedCheques list size" + OutputXML.size()+ "");

		for (Map<String,String> map : OutputXML) {
			add_xml_str +="<ExternalBouncedCheques><applicant_id>" + wiName + "</applicant_id>"+ "";
			//add_xml_str +="<external_bounced_cheques_id></external_bounced_cheques_id>"+ "";
			add_xml_str +="<bounced_cheque>" + validateValue(map.get("ChqType")) + "</bounced_cheque>"+ "";
			add_xml_str +="<cheque_no>" + validateValue(map.get("number")) + "</cheque_no>"+ "";
			add_xml_str +="<amount>" + validateValue(map.get("amount")) + "</amount>"+ "";
			add_xml_str +="<reason>" + validateValue(map.get("reasoncode")) + "</reason>"+ "";
			add_xml_str +="<return_date>" + validateValue(map.get("returndate")) + "</return_date>"+ "";
			add_xml_str +="<provider_no>" + validateValue(map.get("providerno")) + "</provider_no><company_flag>N</company_flag></ExternalBouncedCheques>"; // to
		}
		Digital_CC.mLogger.debug("RLOSCommon"+ "Internal liab tag Cration: "+ add_xml_str);
		return add_xml_str;
	}
	

	private static String sInputXmlExternalUtilization(String wiName, String cabinetName, String sessionID, String jtsIP, String jtsPort)
	{
		String sQuery = "select CardEmbossNum, Utilizations24Months as UtilizationsMonths from ng_dcc_cust_extexpo_CardDetails where Wi_Name='" + wiName + "' and (History is not null or History!='') "
				+ "union all select AgreementId, Utilizations24Months as UtilizationsMonths from ng_dcc_cust_extexpo_LoanDetails where Wi_Name='" + wiName + "' and (History is not null or History!='')";
		String add_xml_str = "";

		try {
			String extTabDataIPXML = apSelectWithColumnNames(sQuery, cabinetName, sessionID);
			Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
			String extTabDataOPXML = WFNGExecute(extTabDataIPXML,jtsIP, jtsPort, 1);
			Digital_CC.mLogger.debug("extTabDataOPXML: " + extTabDataOPXML);

			XMLParser xmlParserData = new XMLParser(extTabDataOPXML);
			int iTotalrec = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));

			if (xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec > 0) {
				String xmlDataExtTab = xmlParserData.getNextValueOf("Record");
				xmlDataExtTab = xmlDataExtTab.replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");

				NGXmlList objWorkList = xmlParserData.createList("Records", "Record");
				String Utilizations24Months = "";
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true)) {
					String agreementID = validateValue(objWorkList.getVal("CardEmbossNum"));
					String UtilizationTag = validateValue(objWorkList.getVal("UtilizationsMonths"));

					UtilizationTag = UtilizationTag.replaceAll("Utilizations24Months", "Month_Utilization");
					Utilizations24Months += UtilizationTag.replaceAll("<Month_Utilization>", "<Month_Utilization><CB_application_id>" + agreementID + "</CB_application_id>");
				}
				
				if (!Utilizations24Months.equals(""))
					add_xml_str = add_xml_str + "<Utilization24months>" + Utilizations24Months + "</Utilization24months>";
			}
		} catch (Exception e) {
			Digital_CC.mLogger.debug("Utilization24months Exception : " + e.getMessage());
			e.printStackTrace();
		}
		Digital_CC.mLogger.debug("Utilization24months : " + add_xml_str);
		return add_xml_str;
	}
	
	
	
	private static String sInputXmlExternalHistory(String wiName, String cabinetName, String sessionID, String jtsIP, String jtsPort) {
		String sQuery = "select CardEmbossNum, history as extHistory from ng_dcc_cust_extexpo_CardDetails where Wi_Name='" + wiName + "' and (History is not null or History!='') "
				+ "union all select AgreementId, history as extHistory from ng_dcc_cust_extexpo_LoanDetails where Wi_Name='" + wiName + "' and (History is not null or History!='')";

		String add_xml_str = "";
		try {
			String extTabDataIPXML = apSelectWithColumnNames(sQuery, cabinetName, sessionID);
			Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
			String extTabDataOPXML = WFNGExecute(extTabDataIPXML, jtsIP,jtsPort, 1);
			Digital_CC.mLogger.debug("extTabDataOPXML: " + extTabDataOPXML);

			XMLParser xmlParserData = new XMLParser(extTabDataOPXML);
			int iTotalrec = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));

			if (xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec > 0) {
				String xmlDataExtTab = xmlParserData.getNextValueOf("Record");
				xmlDataExtTab = xmlDataExtTab.replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");

				NGXmlList objWorkList = xmlParserData.createList("Records", "Record");
				String history = "";
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true)) {
					String agreementID = validateValue(objWorkList.getVal("CardEmbossNum"));
					String HistoryTag = validateValue(objWorkList.getVal("extHistory"));
					HistoryTag = HistoryTag.replaceAll("Key", "monthyear");

					history += HistoryTag.replaceAll("<History>", "<History><CB_application_id>" + agreementID + "</CB_application_id>");
				}
				
				if (!history.equals(""))
					add_xml_str = add_xml_str + "<History_24months>" + history + "</History_24months>";
			}
		} catch (Exception e) {
			Digital_CC.mLogger.debug("History_24months Exception : " + e.getMessage());
			e.printStackTrace();
		}
		Digital_CC.mLogger.debug("History_24months : " + add_xml_str);
		return add_xml_str;
	}
	

	
	
	private static  String sInputXmlExternalBureauIndividualProducts(String wiName, String cabinetName, String sessionID, String jtsIP, String jtsPort) {
		//Digital_CC.mLogger.info("RLOSCommon java file"+"inside ExternalBureauIndividualProducts : ");
		String sQuery = "select CifId,AgreementId,LoanType,ProviderNo,LoanStat,CustRoleType,LoanApprovedDate,LoanMaturityDate,OutstandingAmt,TotalAmt,PaymentsAmt,"
				+ "TotalNoOfInstalments,RemainingInstalments,WriteoffStat,WriteoffStatDt,CreditLimit,OverdueAmt,NofDaysPmtDelay,MonthsOnBook,lastrepmtdt,IsCurrent,"
				+ "CurUtilRate,DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,'' as qc_Amnt,'' as Qc_emi,'' as Cac_indicator,Take_Over_Indicator,"
				+ "Consider_For_Obligations, case when IsDuplicate= '1' then 'Y' else 'N' end AS IsDuplicate,avg_utilization,DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount,"
				+ "Pmtfreq, MaxOverDueAmountDate from ng_dcc_cust_extexpo_LoanDetails with (nolock) where Wi_Name= '"+ wiName + "'  and LoanStat != 'Pipeline' "
				
		+ "union select CifId,CardEmbossNum,CardType,ProviderNo,CardStatus,CustRoleType,StartDate,ClosedDate,CurrentBalance,'' as col6,"
		+ "PaymentsAmount,NoOfInstallments,'' as col5,WriteoffStat,WriteoffStatDt,CashLimit,OverdueAmount,NofDaysPmtDelay,MonthsOnBook,lastrepmtdt,IsCurrent,CurUtilRate,"
		+ "DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,qc_amt,qc_emi,CAC_Indicator,Take_Over_Indicator,Consider_For_Obligations,case when "
		+ "IsDuplicate= '1' then 'Y' else 'N' end AS IsDuplicate,avg_utilization,DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount,Pmtfreq, MaxOverDueAmountDate from "
		+ "ng_dcc_cust_extexpo_CardDetails with (nolock) where Wi_Name = '" + wiName+ "' and cardstatus != 'Pipeline'   "
		
		+ "union select CifId,AcctId,AcctType,ProviderNo,AcctStat,CustRoleType,StartDate,ClosedDate,OutStandingBalance,TotalAmount,PaymentsAmount,'','',"
		+ "WriteoffStat,WriteoffStatDt,CreditLimit,OverdueAmount,"
		+ "NofDaysPmtDelay,MonthsOnBook,'',IsCurrent,CurUtilRate,DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,'','','','',"
		+ "isnull(Consider_For_Obligations,'true'),case when IsDuplicate= '1' then 'Y' else 'N' end AS IsDuplicate,'',DPD5_Last12Months,DPD60Plus_Last12Months,"
		+ "MaximumOverDueAmount,Pmtfreq, MaxOverDueAmountDate from ng_dcc_cust_extexpo_AccountDetails with (nolock)  where Wi_Name  =  '"+wiName+"' "
		
		+ "union select CifId,ServiceID,ServiceType,ProviderNo,ServiceStat,CustRoleType,SubscriptionDt,SvcExpDt,'','','','','',WriteoffStat,WriteoffStatDt,'',OverDueAmount,"
		+ "NofDaysPmtDelay,MonthsOnBook,'',IsCurrent,CurUtilRate,'',DPD30_Last6Months,AECBHistMonthCnt,DPD5_Last3Months,'','','','',isnull(Consider_For_Obligations,'true')"
		+ ",case when IsDuplicate= '1' then 'Y' else 'N' end AS IsDuplicate,'',DPD5_Last12Months,DPD60Plus_Last12Months,'','','' from ng_dcc_cust_extexpo_ServicesDetails with (nolock)  "
		+ "where ServiceStat='Active' and wi_name  =  '"+wiName+"'";
		
		String add_xml_str = "";
		List<Map<String,String>> OutputXML = getDataFromDBMap(sQuery, cabinetName, sessionID, jtsIP, jtsPort);
		Digital_CC.mLogger.info("ExternalBureauIndividualProducts list size"+ OutputXML.size()+ "");
			
		for (Map<String,String> map : OutputXML){
			
			String ContractType = validateValue(map.get("LoanType"));
			String phase = validateValue(map.get("LoanStat"));
			String CustRoleType = validateValue(map.get("CustRoleType"));
			String start_date = validateValue(map.get("LoanApprovedDate"));
			String close_date = validateValue(map.get("LoanMaturityDate"));
			String OutStanding_Balance = validateValue(map.get("OutstandingAmt"));
			String TotalAmt = validateValue(map.get("TotalAmt"));
			String PaymentsAmt = validateValue(map.get("PaymentsAmt"));
			String TotalNoOfInstalments = validateValue(map.get("TotalNoOfInstalments"));
			String RemainingInstalments = validateValue(map.get("RemainingInstalments"));
			String WorstStatus = validateValue(map.get("WriteoffStat"));
			String WorstStatusDate = validateValue(map.get("WriteoffStatDt"));
			//String CreditLimit = validateValue(map.get("CreditLimit"));
			String OverdueAmt = validateValue(map.get("OverdueAmt"));
			String NofDaysPmtDelay = validateValue(map.get("NofDaysPmtDelay"));
			String MonthsOnBook = validateValue(map.get("MonthsOnBook"));
			String last_repayment_date = validateValue(map.get("lastrepmtdt"));
			//String DPD60Last12Months = validateValue(map.get("DPD60_Last12Months"));
			String AECBHistMonthCnt = validateValue(map.get("AECBHistMonthCnt"));
			String DPD30Last6Months = validateValue(map.get("DPD30_Last6Months"));
			String currently_current = validateValue(map.get("IsCurrent"));
			String current_utilization = validateValue(map.get("CurUtilRate"));
			String delinquent_in_last_3months = validateValue(map.get("DPD5_Last3Months"));
			//String QC_Amt = validateValue(map.get("qc_Amnt"));
			//String QC_emi = validateValue(map.get("Qc_emi"));
			String CAC_Indicator = validateValue(map.get("Cac_indicator"));
			String TakeOverIndicator = validateValue(map.get("Take_Over_Indicator"));
			String consider_for_obligation = validateValue(map.get("Consider_For_Obligations"));
			String Duplicate_flag=validateValue(map.get("IsDuplicate"));
			//String avg_utilization=validateValue(map.get("avg_utilization"));
			String DPD60plus_last12month=validateValue(map.get("DPD60Plus_Last12Months"));
			String DPD5_last12month=validateValue(map.get("DPD5_Last12Months"));
			String MaximumOverDueAmount = validateValue(map.get("MaximumOverDueAmount"));  
			String Pmtfreq = validateValue(map.get("Pmtfreq"));
			String MaxOverDueAmountDate = validateValue(map.get("MaxOverDueAmountDate"));
			
			if (!ContractType.equals("")) {
				try {
					String cardquery = "select code from ng_master_contract_type with (nolock) where description='"+ ContractType + "'";
					Digital_CC.mLogger.info("ExternalBureauIndividualProducts sQuery"+ cardquery+ "");
					Map<String, String> cardqueryXML = getDataFromDB(cardquery, cabinetName, sessionID, jtsIP, jtsPort, "code");
					ContractType = cardqueryXML.get("code");
					Digital_CC.mLogger.info("ExternalBureauIndividualProducts ContractType"+ ContractType+ "ContractType");
				} catch (Exception e) {
					Digital_CC.mLogger.info("ExternalBureauIndividualProducts ContractType Exception"+ e+ "Exception");
				}
			}
			
			phase = phase.startsWith("A") ? "A" : "C";
			
			if (!CustRoleType.equals("")) {
				String sQueryCustRoleType = "select code from ng_master_role_of_customer with(nolock) where Description='"+CustRoleType+"'";
				Digital_CC.mLogger.info("CustRoleType"+sQueryCustRoleType);
				Map<String, String> cardqueryXML = getDataFromDB(sQueryCustRoleType, cabinetName, sessionID, jtsIP, jtsPort, "code");
				try {
					if (cardqueryXML != null && cardqueryXML.size() > 0 && cardqueryXML.get("code") != null) {
						CustRoleType = cardqueryXML.get("code");
					}
				}
				catch(Exception e){
					Digital_CC.mLogger.info("Exception occured at sQueryCombinedLimit for"+sQueryCustRoleType);
				}	
			}

			CAC_Indicator = "true".equalsIgnoreCase(CAC_Indicator) ? "Y" : "N";
			
			TakeOverIndicator = "true".equalsIgnoreCase(TakeOverIndicator) ? "Y" : "N";
			
			consider_for_obligation = "true".equalsIgnoreCase(consider_for_obligation) ? "Y" : "N";
			
			//Always N because of salaried person
		    //String Company_flag="N";

			add_xml_str +="<ExternalBureauIndividualProducts><applicant_id>" + wiName + "</applicant_id>"+ "";
			add_xml_str +="<external_bureau_individual_products_id>" + wiName + "</external_bureau_individual_products_id>"+ "";
			add_xml_str +="<contract_type>SerExp</contract_type>"+ ""; //Default to SerExp
			add_xml_str +="<provider_no>" + map.get("ProviderNo") + "</provider_no>"+ "";
			add_xml_str +="<phase>Active</phase>"+ ""; //Default
			add_xml_str +="<role_of_customer>" + CustRoleType + "</role_of_customer>"+ "";
			add_xml_str +="<start_date>" + start_date + "</start_date>"+ "";

			add_xml_str +="<close_date>" + close_date + "</close_date>"+ "";
			add_xml_str +="<outstanding_balance>" + OutStanding_Balance + "</outstanding_balance>"+ "";
			add_xml_str +="<total_amount>" + TotalAmt + "</total_amount>"+ "";
			add_xml_str +="<payments_amount>" + PaymentsAmt + "</payments_amount>"+ "";
			add_xml_str +="<total_no_of_instalments>" + TotalNoOfInstalments + "</total_no_of_instalments>"+ "";
			add_xml_str +="<no_of_remaining_instalments>" + RemainingInstalments + "</no_of_remaining_instalments>"+ "";
			add_xml_str +="<worst_status>" + WorstStatus + "</worst_status>"+ "";
			add_xml_str +="<worst_status_date>" + WorstStatusDate + "</worst_status_date>"+ "";

			//add_xml_str +="<credit_limit>" + CreditLimit + "</credit_limit>"+ "";
			add_xml_str +="<overdue_amount>" + OverdueAmt + "</overdue_amount>"+ "";
			add_xml_str +="<no_of_days_payment_delay>" + NofDaysPmtDelay + "</no_of_days_payment_delay>"+ "";
			add_xml_str +="<mob>" + MonthsOnBook + "</mob>"+ "";
			add_xml_str +="<last_repayment_date>" + last_repayment_date + "</last_repayment_date>"+ "";

			if (currently_current != null && "1".equalsIgnoreCase(currently_current)) {
				add_xml_str +="<currently_current>Y</currently_current>"+ "";
			} else {
				add_xml_str +="<currently_current>N</currently_current>"+ "";
			}
		
			add_xml_str +="<current_utilization>" + current_utilization + "</current_utilization>"+ "";
			add_xml_str +="<dpd_5_in_last_12_mon>" + DPD5_last12month + "</dpd_5_in_last_12_mon>"+ "";
			add_xml_str +="<dpd_30_last_6_mon>" + DPD30Last6Months + "</dpd_30_last_6_mon>"+ "";

			add_xml_str +="<dpd_60p_in_last_12_mon>" + DPD60plus_last12month + "</dpd_60p_in_last_12_mon>"+ "";
			//add_xml_str +="<dpd_5_in_last_12_mon>" + DPD5_last12month + "</dpd_5_in_last_12_mon>"+ "";
			add_xml_str +="<no_months_aecb_history>" + AECBHistMonthCnt + "</no_months_aecb_history>"+ "";
			add_xml_str +="<maximum_overdue_amount>" + MaximumOverDueAmount + "</maximum_overdue_amount>"+ "";// added by deppanshu
			add_xml_str +="<delinquent_in_last_3months>" + delinquent_in_last_3months + "</delinquent_in_last_3months>"+ "";
			//add_xml_str +="<clean_funded>" + "" + "</clean_funded>"+ "";
			//add_xml_str +="<cac_indicator>" + CAC_Indicator + "</cac_indicator>"+ "";
			//add_xml_str +="<qc_emi>" + QC_emi + "</qc_emi>"+ "";
			//add_xml_str +="<qc_amount>" + QC_Amt + "</qc_amount>">"+ "";
			add_xml_str +="<company_flag>N</company_flag>"+ "";
			//add_xml_str +="<cac_bank_name>" + CAC_BANK_NAME+ "</cac_bank_name>"+ "";
			//add_xml_str +="<take_over_indicator>" + TakeOverIndicator + "</take_over_indicator>"+ "";
			add_xml_str +="<consider_for_obligation>Y</consider_for_obligation>"+ "";
			add_xml_str +="<duplicate_flag>"+Duplicate_flag+"</duplicate_flag>"+ "";
			//add_xml_str +="<avg_utilization>"+avg_utilization+"</avg_utilization>"+ "";
			add_xml_str +="<payment_frequency>"+Pmtfreq+"</payment_frequency>"+ "";
			add_xml_str +="<maximum_overdue_date>"+MaxOverDueAmountDate+"</maximum_overdue_date>"+ "";
			add_xml_str +="</ExternalBureauIndividualProducts>";
			
			Digital_CC.mLogger.info("Internal liab tag Cration: "	+ add_xml_str);
		}
		
		Digital_CC.mLogger.info("Internal liab tag Cration: "	+ add_xml_str);
		return add_xml_str;
	}
	
	private static  String sInputXmlExternalBureauPipelineProducts(String wiName, String cabinetName, String sessionID, String jtsIP, String jtsPort) {
		Digital_CC.mLogger.debug("inside ExternalBureauPipelineProducts : ");
		String sQuery = "select CifId, AgreementId,ProviderNo,LoanType,LoanDesc,CustRoleType,Datelastupdated,TotalAmt,TotalNoOfInstalments,CreditLimit,'' as col1,NoOfDaysInPipeline,"
				+ "isnull(Consider_For_Obligations,'true') as 'Consider_For_Obligations', case when IsDuplicate= '1' then 'Y' else 'N' end as 'IsDuplicate' from ng_dcc_cust_extexpo_LoanDetails with (nolock) "
				+ "where Wi_Name  =  '" + wiName + "' and LoanStat = 'Pipeline'"
				+ "union select CifId, CardEmbossNum,ProviderNo,CardType,CardTypeDesc, CustRoleType,LastUpdateDate,'' as col2,NoOfInstallments, '' as col3, TotalAmount, "
				+ "NoOfDaysInPipeLine,isnull(Consider_For_Obligations,'true') as 'Consider_For_Obligations',case when IsDuplicate= '1' then 'Y' else 'N' end as 'IsDuplicate' from ng_dcc_cust_extexpo_CardDetails "
				+ "with (nolock) where Wi_Name  =  '" + wiName + "' and cardstatus = 'Pipeline'";
		
		Digital_CC.mLogger.debug("ExternalBureauPipelineProducts sQuery" + sQuery+"");
		
		String add_xml_str = "";
		List<Map<String,String>> maps= getDataFromDBMap(sQuery, cabinetName, sessionID, jtsIP, jtsPort);
		Digital_CC.mLogger.info("ExternalBureauPipelineProducts list size"+ maps.size()+ "");
			
		for (Map<String,String> map : maps) {

			String contractType = validateValue(map.get("LoanType"));
			String role = validateValue(map.get("CustRoleType"));
			//String lastUpdateDate = validateValue(map.get("Datelastupdated"));
	    	//String consider_for_obligation=validateValue(map.get("Consider_For_Obligations"));

			if (!contractType.equals("")) {
				try {
					String cardquery = "select code from ng_master_contract_type with (nolock) where description='"+ contractType + "'";
					Digital_CC.mLogger.info("ExternalBureauIndividualProducts sQuery"+ cardquery+ "");
					Map<String, String> cardqueryXML = getDataFromDB(cardquery, cabinetName, sessionID, jtsIP, jtsPort, "code");
					contractType = cardqueryXML.get("code");
					Digital_CC.mLogger.info("ExternalBureauIndividualProducts ContractType"+ contractType+ "ContractType");
				} catch (Exception e) {
					Digital_CC.mLogger.info("ExternalBureauIndividualProducts ContractType Exception"+ e+ "Exception");
				}
			}
			
			if (!role.equals("")) {
				String sQueryCustRoleType = "select code from ng_master_role_of_customer with(nolock) where Description='"+role+"'";
				Digital_CC.mLogger.info("CustRoleType"+sQueryCustRoleType);
				Map<String, String> cardqueryXML = getDataFromDB(sQueryCustRoleType, cabinetName, sessionID, jtsIP, jtsPort, "code");
				try {
					if (cardqueryXML != null && cardqueryXML.size() > 0 && cardqueryXML.get("code") != null) {
						role = cardqueryXML.get("code");
					}
				}
				catch(Exception e){
					Digital_CC.mLogger.info("Exception occured at sQueryCombinedLimit for"+sQueryCustRoleType);
				}	
			}
			
			/*if (!"".equalsIgnoreCase(consider_for_obligation) && "true".equalsIgnoreCase(consider_for_obligation)) {
				consider_for_obligation = "Y";
			} else {
				consider_for_obligation = "N";
			}*/
			
			add_xml_str +="<ExternalBureauPipelineProducts><applicant_ID>" + wiName + "</applicant_ID>"+ "";
			add_xml_str +="<external_bureau_pipeline_products_id>" + validateValue(map.get("AgreementId")) + "</external_bureau_pipeline_products_id>"+ "";
			add_xml_str +="<ppl_provider_no>" + validateValue(map.get("ProviderNo")) + "</ppl_provider_no>"+ "";
			add_xml_str +="<ppl_type_of_contract>" + contractType + "</ppl_type_of_contract>"+ "";
			add_xml_str +="<ppl_type_of_product>" + validateValue(map.get("LoanDesc")) + "</ppl_type_of_product>"+ "";
			add_xml_str +="<ppl_phase>" + "PIPELINE" + "</ppl_phase>"+ "";
			add_xml_str +="<ppl_role>" + role + "</ppl_role>"+ "";
			add_xml_str +="<ppl_date_of_last_update>" + validateValue(map.get("Datelastupdated")) + "</ppl_date_of_last_update>"+ "";
			//add_xml_str +="<ppl_total_amount>" + validateValue(map.get("TotalAmt")) + "</ppl_total_amount>"+ "";
			add_xml_str +="<ppl_no_of_instalments>" + validateValue(map.get("TotalNoOfInstalments")) + "</ppl_no_of_instalments>"+ "";
			if (validateValue(map.get("LoanType")).toUpperCase().contains("LOAN")) {
				add_xml_str +="<ppl_total_amount>" + validateValue(map.get("TotalAmt")) + "</ppl_total_amount>"+ "";
			} else {
				add_xml_str +="<ppl_credit_limit>" + validateValue(map.get("col1")) + "</ppl_credit_limit>"+ "";
			}
			add_xml_str +="<ppl_no_of_days_in_pipeline>" + validateValue(map.get("NoOfDaysInPipeline")) + "</ppl_no_of_days_in_pipeline>"+ "";
			add_xml_str +="<company_flag>N</company_flag>"+ "";
			add_xml_str +="<ppl_consider_for_obligation>Y</ppl_consider_for_obligation>"+ "";
			add_xml_str +="<ppl_duplicate_flag>"+validateValue(map.get("IsDuplicate"))+"</ppl_duplicate_flag></ExternalBureauPipelineProducts>";
		}
		Digital_CC.mLogger.debug("RLOSCommon"+ "Internal liab tag Cration: "	+ add_xml_str);
		return add_xml_str;
	}
	private static  String sInputXmlPerfios(Map<String, String> applicantDetails_Map) {
		String add_xml_str = "<Perfios>"
				+"<Stmt_Salary_1>"+applicantDetails_Map.get("Net_Salary1")+"</Stmt_Salary_1>"
				+"<Stmt_salary1_date>"+applicantDetails_Map.get("Net_salary1_date")+"</Stmt_salary1_date>"
				+"<Stmt_salary_2>"+applicantDetails_Map.get("Net_Salary2")+"</Stmt_salary_2>"
				+"<Stmt_salary2_date>"+applicantDetails_Map.get("Net_salary2_date")+"</Stmt_salary2_date>"
				+"<Stmt_salary_3>"+applicantDetails_Map.get("Net_Salary3")+"</Stmt_salary_3>"
				+"<Stmt_salary3_date>"+applicantDetails_Map.get("Net_salary3_date")+"</Stmt_salary3_date>"
				+"<Stmt_salary_4>"+applicantDetails_Map.get("Net_Salary4")+"</Stmt_salary_4>"
				+"<Stmt_salary4_date>"+applicantDetails_Map.get("Net_salary4_date")+"</Stmt_salary4_date>"
				+"<Stmt_salary_5>"+applicantDetails_Map.get("Net_Salary5")+"</Stmt_salary_5>"
				+"<Stmt_salary5_date>"+applicantDetails_Map.get("Net_salary5_date")+"</Stmt_salary5_date>"
				+"<Stmt_salary_6>"+applicantDetails_Map.get("Net_Salary6")+"</Stmt_salary_6>"
				+"<Stmt_salary6_date>"+applicantDetails_Map.get("Net_salary6_date")+"</Stmt_salary6_date>"
				+"<Stmt_salary_7>"+applicantDetails_Map.get("Net_Salary7")+"</Stmt_salary_7>"
				+"<Stmt_salary7_date>"+applicantDetails_Map.get("Net_salary7_date")+"</Stmt_salary7_date>"
				+"<Addn_Perfios_EMI_1>"+applicantDetails_Map.get("Addn_Perfios_EMI_1")+"</Addn_Perfios_EMI_1>"
				+"<Addn_Perfios_EMI_2>"+applicantDetails_Map.get("Addn_Perfios_EMI_2")+"</Addn_Perfios_EMI_2>"
				+"<Addn_Perfios_EMI_3>"+ applicantDetails_Map.get("Addn_Perfios_EMI_3")+"</Addn_Perfios_EMI_3>" 
				+"<Addn_Perfios_EMI_4>"+applicantDetails_Map.get("Addn_Perfios_EMI_4")+"</Addn_Perfios_EMI_4>"
				+"<Addn_Perfios_EMI_5>"+ applicantDetails_Map.get("Addn_Perfios_EMI_5")+"</Addn_Perfios_EMI_5>" 
				+"<Addn_Perfios_EMI_6>"+applicantDetails_Map.get("Addn_Perfios_EMI_6")+"</Addn_Perfios_EMI_6>"
				+"<Addn_Perfios_EMI_7>"+ applicantDetails_Map.get("Addn_Perfios_EMI_7")+"</Addn_Perfios_EMI_7>" 
				+"<Addn_Perfios_EMI_8>"+applicantDetails_Map.get("Addn_Perfios_EMI_8")+"</Addn_Perfios_EMI_8>"
				+"<Addn_Perfios_EMI_9>"+applicantDetails_Map.get("Addn_Perfios_EMI_9")+"</Addn_Perfios_EMI_9>"
				+"<Addn_Perfios_EMI_10>"+applicantDetails_Map.get("Addn_Perfios_EMI_10")+"</Addn_Perfios_EMI_10>"
				+"<Addn_Perfios_EMI_11>"+applicantDetails_Map.get("Addn_Perfios_EMI_11")+"</Addn_Perfios_EMI_11>"
				+"<Addn_Perfios_EMI_12>"+applicantDetails_Map.get("Addn_Perfios_EMI_12")+"</Addn_Perfios_EMI_12>"
				+"<Addn_Perfios_EMI_13>"+applicantDetails_Map.get("Addn_Perfios_EMI_13")+"</Addn_Perfios_EMI_13>"
				+"<Addn_Perfios_EMI_14>"+applicantDetails_Map.get("Addn_Perfios_EMI_14")+"</Addn_Perfios_EMI_14>"
				+"<Addn_Perfios_EMI_15>"+applicantDetails_Map.get("Addn_Perfios_EMI_15")+"</Addn_Perfios_EMI_15>"
				+"<Addn_Perfios_EMI_16>"+applicantDetails_Map.get("Addn_Perfios_EMI_16")+"</Addn_Perfios_EMI_16>"
				+"<Addn_Perfios_EMI_17>"+applicantDetails_Map.get("Addn_Perfios_EMI_17")+"</Addn_Perfios_EMI_17>"
				+"<Addn_Perfios_EMI_18>"+applicantDetails_Map.get("Addn_Perfios_EMI_18")+"</Addn_Perfios_EMI_18>"
				+"<Addn_Perfios_EMI_19>"+applicantDetails_Map.get("Addn_Perfios_EMI_19")+"</Addn_Perfios_EMI_19>"
				+"<Addn_Perfios_EMI_20>"+applicantDetails_Map.get("Addn_Perfios_EMI_20")+"</Addn_Perfios_EMI_20>"
				+"<Addn_Perfios_CC>"+applicantDetails_Map.get("Addn_Perfios_CC")+"</Addn_Perfios_CC>"
				+"<Addn_Perfios_OD_Amt>"+applicantDetails_Map.get("Addn_Perfios_OD_Amt")+"</Addn_Perfios_OD_Amt>"
				+"<Addn_OD_date>"+applicantDetails_Map.get("Addn_OD_date")+"</Addn_OD_date>"
				+"<Joint_Acct>"+applicantDetails_Map.get("Joint_Acct")+"</Joint_Acct>"
				+"<High_value_deposit>"+applicantDetails_Map.get("High_Value_Deposit")+"</High_value_deposit>"
				+"<Credit_amount>"+applicantDetails_Map.get("Credit_Amount")+"</Credit_amount>"
				+"<Stmt_chq_rtn_last_3mnts>"+applicantDetails_Map.get("Stmt_chq_rtn_last_3mnts")+"</Stmt_chq_rtn_last_3mnts>"
				+"<Stmt_chq_rtn_cleared_in30_last_3mnts>"+applicantDetails_Map.get("Stmt_chq_rtn_cleared_in30_last_3mnts")+"</Stmt_chq_rtn_cleared_in30_last_3mnts>"
				+"<Stmt_chq_rtn_last_1mnt>"+applicantDetails_Map.get("Stmt_chq_rtn_last_1mnt")+"</Stmt_chq_rtn_last_1mnt>"
				+"<Stmt_chq_rtn_cleared_in30_last_1mnt>"+applicantDetails_Map.get("Stmt_chq_rtn_cleared_in30_last_1mnt")+"</Stmt_chq_rtn_cleared_in30_last_1mnt>"
				+"<Stmt_DDS_rtn_last_3mnts>"+applicantDetails_Map.get("Stmt_DDS_rtn_last_3mnts")+"</Stmt_DDS_rtn_last_3mnts>"
				+"<Stmt_DDS_rtn_cleared_in30_last_3mnts>"+applicantDetails_Map.get("Stmt_DDS_rtn_cleared_in30_last_3mnts")+"</Stmt_DDS_rtn_cleared_in30_last_3mnts>"
				+"<Stmt_DDS_rtn_last_1mnt>"+applicantDetails_Map.get("Stmt_DDS_rtn_last_1mnt")+"</Stmt_DDS_rtn_last_1mnt>"
				+"<Stmt_DDS_rtn_cleared_in30_last_1mnts>"+applicantDetails_Map.get("Stmt_DDS_rtn_cleared_in30_last_1mnts")+"</Stmt_DDS_rtn_cleared_in30_last_1mnts>"
				+"<Pensioner>"+applicantDetails_Map.get("Pensioner")+"</Pensioner>"
				+"<Name_match>"+applicantDetails_Map.get("Name_match")+"</Name_match>"
				+"<FCU_indicator>"+applicantDetails_Map.get("FCU_indicator")+"</FCU_indicator>"
				+"<UW_reqd>"+applicantDetails_Map.get("UW_reqd")+"</UW_reqd>"
				+"</Perfios>";
		return add_xml_str;
	}
	private static  String validateValue(String value) {
		if (value != null && ! value.equals("") && !value.equalsIgnoreCase("null")) {
			return value.toString();
		}
		return "";
	}

}
