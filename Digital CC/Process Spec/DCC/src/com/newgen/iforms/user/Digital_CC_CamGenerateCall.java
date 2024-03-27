package com.newgen.iforms.user;




import com.newgen.iforms.custom.IFormReference;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;

import java.io.BufferedInputStream;
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
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;

public class Digital_CC_CamGenerateCall extends Digital_CC_Common {
	static Map<String, String> configParamMap = new HashMap<String, String>();
	String processInstanceID = "";
	String cabinetName = "";
	String sessionId = "";
	String serverIp = "";
	String serverPort = "";
	public String onevent(IFormReference iformObj, String control, String StringData) throws Exception {
		String returnValue="";
		this.processInstanceID = getWorkitemName(iformObj);
		this.cabinetName =getCabinetName(iformObj);
		this.sessionId = getSessionId(iformObj);
		this.serverIp = iformObj.getServerIp();
		this.serverPort = iformObj.getServerPort();
		int configReadStatus = readConfig();
		

		 Digital_CC.mLogger.debug("configReadStatus "+configReadStatus);
		if(configReadStatus !=0)
		{
			Digital_CC.mLogger.error("Could not Read Config Properties");
			return "";
		}
		  String DBQuery = "SELECT Is_CAM_generated,CIF,Is_STP FROM NG_DCC_EXTTABLE with(nolock) WHERE WI_NAME='" + processInstanceID + "'";
		 
	         
	        String extTabDataINPXML = Digital_CC_Common.apSelectWithColumnNames(DBQuery,  getCabinetName(iformObj),getSessionId(iformObj));
	        Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataINPXML);
	        //String extTabDataOUPXML = Digital_CC_Common.WFNGExecute(extTabDataINPXML, iformObj.getServerIp(), iformObj.getServerPort(), 1);
	        String extTabDataOUPXML = Digital_CC_Common.WFNGExecute(extTabDataINPXML, iformObj.getServerIp(), iformObj.getServerPort(), 1);
	        Digital_CC.mLogger.debug("extTabDataOPXML: " + extTabDataOUPXML);	
			
	        XMLParser xmlParserDataDB = new XMLParser(extTabDataOUPXML);
	        
	        String Is_CAM_Generated = xmlParserDataDB.getValueOf("Is_CAM_generated");
	        Digital_CC.mLogger.debug("Is_CAM_Generated: inside cam generation onevent " + Is_CAM_Generated);
	        String Cif_Id = xmlParserDataDB.getValueOf("CIF");
	        Digital_CC.mLogger.debug("Cif_Id: inside cam generation onevent " + Cif_Id);
	        String Is_STP = xmlParserDataDB.getValueOf("Is_STP");
	        Digital_CC.mLogger.debug("Is_STP: inside cam generation onevent " + Is_STP);
	        String pdfName = "";
	        if(Is_STP.equalsIgnoreCase("Y") ){
	        	Is_STP = "N";
	        	Is_CAM_Generated ="N";
	        }
	        
	       // if(!"Y".equalsIgnoreCase(Is_CAM_Generated)){
	        	if (Is_STP.equalsIgnoreCase("N")){
	        		
	        		 pdfName = "NON_STP_CAM_Report";
	        		 Digital_CC.mLogger.debug("pdfName: inside cam generation onevent " + pdfName);
		        	 
	        	}
	        	 String output = generate_CAM_ReportT(iformObj,pdfName,Cif_Id,processInstanceID,getSessionId(iformObj));
	        	 if(output!=null && output.contains("~"))
	        	 {
	        		 String arr[] = output.split("~");
	        		 if(arr.length>2)
	        		 {
	        			 return arr[0]+"~"+arr[1];
	        		 }
	        	 }
	        	 

			/*}
	        else{
				Digital_CC.mLogger.debug("Cam Report Is Already Generated");
			}*/
	        
	      return "FAIL";
	}
	
	private int readConfig()
	{
		Properties p = null;
		try {

			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "CustomConfig"+ File.separator+ "DCC_CAMGen_Config.properties")));

			Enumeration<?> names = p.propertyNames();

			while (names.hasMoreElements())
			{
				String name = (String) names.nextElement();
				configParamMap.put(name, p.getProperty(name));
			}
		}
		catch (Exception e)
		{
			return -1 ;
		}
		return 0;
	}

	public String generate_CAM_ReportT(IFormReference iformObj,String pdfName, String Cif_Id, String processInstanceID, String sessionId)
			throws IOException, Exception {

		Digital_CC.mLogger.debug("Inside generate cam report method: ");
		
		String gtIP = configParamMap.get("gtIP");
		Digital_CC.mLogger.debug("gtIP: " + gtIP);

		String gtPortProperty = configParamMap.get("gtPort");
		Digital_CC.mLogger.debug("gtPortProperty: " + gtPortProperty);

		int gtPort = Integer.parseInt(gtPortProperty);
		Digital_CC.mLogger.debug("gtPort: " + gtPort);
		
		// for current date time 
		Date d = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String CurrentDateTime = dateFormat.format(d);
	
		String Query = "select distinct Prospect_Creation_Date,Is_STP,case when MiddleName is null then CONCAT(FirstName,' ',LastName) else CONCAT(FirstName,' ',MiddleName,' ',LastName) end as CUSTOMERNAME ,Nationality_Desc, Cust_Decl_Salary,FinalTAI,"
                + "Product_Desc,(select Card_Type_Desc from ng_dcc_master_cardtype where card_type_code=Selected_Card_Type ) as Selected_Card_Type,card_application_date,Age,ApprovedLimit,EFMS_Status,"
                + "Requested_Limit,isnull(FIRCO_Status,'No-Hit') as FIRCO_Status, Employer_Name,Date_Of_Joining,Designation_Desc,employercode,"
                + "FinalDBR,Aecb_score,Final_Limit,Output_Affordable_Ratio,deviation_description,"
                + "delegation_authority,Score_range,Dectech_Decision,Net_Salary1,Net_Salary2,Net_Salary3,Non_STP_reason,Underwriting_decision,UW_Decision,decline_reason,Decision,remarks from NG_DCC_EXTTABLE with (NOLOCK) where Wi_Name ='"
                + processInstanceID + "'";


		// for user name and decision at cad analy 1

		String Query2 = "select Top 1 user_name,Decision,Remarks,rejectReason from NG_DCC_GR_DECISION_HISTORY with (NOLOCK)  where wi_name ='"
				+ processInstanceID + "' order by decision_date_time desc";
		
		String Query3 = "select card_type_desc from ng_dcc_master_cardType with(nolock) where card_type_code = (select top 1 Selected_Card_Type from NG_DCC_EXTTABLE with(nolock) where Wi_Name='"+processInstanceID+"' )";

		Digital_CC.mLogger.debug("Query : " + Query);
		Digital_CC.mLogger.debug("Query2 : " + Query2);
		Digital_CC.mLogger.debug("Query3 : " + Query3);
		
		String extTabDataIPXML = Digital_CC_Common.apSelectWithColumnNames(Query, cabinetName,sessionId);
		Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
		String extTabDataOPXML = Digital_CC_Common.WFNGExecute(extTabDataIPXML, serverIp,
				serverPort, 1);
		Digital_CC.mLogger.debug("extTabDataOPXML: " + extTabDataOPXML);

		XMLParser xmlParserData = new XMLParser(extTabDataOPXML);

		// **************************** for second query
		// ************************
		String extTabDataIPXML2 = Digital_CC_Common.apSelectWithColumnNames(Query2,cabinetName,sessionId);
		Digital_CC.mLogger.debug("extTabDataIPXML2: " + extTabDataIPXML2);
		String extTabDataOPXML2 = Digital_CC_Common.WFNGExecute(extTabDataIPXML2, serverIp,
				serverPort, 1);
		Digital_CC.mLogger.debug("extTabDataOPXML2: " + extTabDataOPXML2);

		XMLParser xmlParserData2 = new XMLParser(extTabDataOPXML2);
		
		String extTabDataIPXML3 = Digital_CC_Common.apSelectWithColumnNames(Query3,cabinetName,sessionId);
		Digital_CC.mLogger.debug("extTabDataIPXML3: " + extTabDataIPXML3);
		String extTabDataOPXML3 = Digital_CC_Common.WFNGExecute(extTabDataIPXML3, serverIp,
				serverPort, 1);
		Digital_CC.mLogger.debug("extTabDataOPXML3: " + extTabDataOPXML3);

		XMLParser xmlParserData3 = new XMLParser(extTabDataOPXML3);
		// ****************************************************************************************
		
		// variables
		String attrbList = "";
		String Is_STP = xmlParserData.getValueOf("Is_STP");
		Digital_CC.mLogger.debug("Is_STP: " + Is_STP);

		/*String Created_Date = xmlParserData.getValueOf("Created_Date");
		Digital_CC.mLogger.debug("Created_Date: " + Created_Date);*/

		String CifId = xmlParserData.getValueOf("CifId");
		String CUSTOMERNAME = xmlParserData.getValueOf("CUSTOMERNAME");
		Digital_CC.mLogger.debug("CUSTOMERNAME: " + CUSTOMERNAME);

		String Nationality = xmlParserData.getValueOf("Nationality_Desc");
		Digital_CC.mLogger.debug("Nationality: " + Nationality);

		String Card_Type = xmlParserData.getValueOf("Applied_card");
		Digital_CC.mLogger.debug("Card_Type: " + Card_Type);
		
		String Product_type = xmlParserData.getValueOf("Product_Desc");
		Digital_CC.mLogger.debug("Product_Desc: " + Product_type);

		String Selected_Card_Type = xmlParserData3.getValueOf("card_type_desc");
		Digital_CC.mLogger.debug("Selected_Card_Type: " + Selected_Card_Type);

		String card_application_date = xmlParserData.getValueOf("Prospect_Creation_Date");
		Digital_CC.mLogger.debug("card_application_date: " + card_application_date);

		String Age = xmlParserData.getValueOf("Age");
		Digital_CC.mLogger.debug("Age: " + Age);

		String IPA_Limit = xmlParserData.getValueOf("ApprovedLimit");
		Digital_CC.mLogger.debug("IPA_Limit: " + IPA_Limit);

		String EFMS_Status = xmlParserData.getValueOf("EFMS_Status");
		Digital_CC.mLogger.debug("EFMS_Status: " + EFMS_Status);

		String Requested_Limit = xmlParserData.getValueOf("Requested_Limit");
		Digital_CC.mLogger.debug("Requested_Limit: " + Requested_Limit);

		String FIRCO_Status = xmlParserData.getValueOf("FIRCO_Status");
		Digital_CC.mLogger.debug("FIRCO_Status: " + FIRCO_Status);

		String Employer_Name = xmlParserData.getValueOf("Employer_Name");
		Digital_CC.mLogger.debug("Employer_Name: " + Employer_Name);

		String Date_Of_Joining = xmlParserData.getValueOf("Date_Of_Joining");
		Digital_CC.mLogger.debug("Date_Of_Joining: " + Date_Of_Joining);

		String Designation = xmlParserData.getValueOf("Designation_Desc");
		Digital_CC.mLogger.debug("Designation: " + Designation);
		
		String employercode = xmlParserData.getValueOf("employercode");
		Digital_CC.mLogger.debug("employercode: " + employercode);

		// employer status pl miss

		String Final_Limit = xmlParserData.getValueOf("Final_Limit");
		Digital_CC.mLogger.debug("Final_Limit: " + Final_Limit);

		// declared income miss
		String DeclIncome = xmlParserData.getValueOf("Cust_Decl_Salary");
		Digital_CC.mLogger.debug("DeclIncome: " + DeclIncome);
		// final income - final limit
		String FinalIncome = xmlParserData.getValueOf("FinalTAI");
		Digital_CC.mLogger.debug("FinalIncome: " + FinalIncome);
		
		String Decision = xmlParserData.getValueOf("Decision");
		Digital_CC.mLogger.debug("Decision: " + Decision);

		String Aecb_score = xmlParserData.getValueOf("Aecb_score");
		Digital_CC.mLogger.debug("Aecb_score: " + Aecb_score);

		String Score_range = xmlParserData.getValueOf("Score_range");
		Digital_CC.mLogger.debug("Score_range: " + Score_range);

		String FinalDBR = xmlParserData.getValueOf("FinalDBR");
		Digital_CC.mLogger.debug("FinalDBR: " + FinalDBR);

		String DBR_lifeStyle_expenses =(String) iformObj.getValue("DBR_lifeStyle_expenses");
		Digital_CC.mLogger.debug("DBR_lifeStyle_expenses: " + DBR_lifeStyle_expenses);

		String deviation_description = xmlParserData.getValueOf("deviation_description");
		Digital_CC.mLogger.debug("deviation_description: " + deviation_description);

		String delegation_authority = xmlParserData.getValueOf("delegation_authority");
		Digital_CC.mLogger.debug("delegation_authority: " + delegation_authority);
		
		String Dectech_Decision = xmlParserData.getValueOf("Dectech_Decision");
		Digital_CC.mLogger.debug("Dectech_Decision: " + Dectech_Decision);
		
		String Net_Salary1 = xmlParserData.getValueOf("Net_Salary1");
		Digital_CC.mLogger.debug("Net_Salary1: " + Net_Salary1);
		
		String Net_Salary2 = xmlParserData.getValueOf("Net_Salary2");
		Digital_CC.mLogger.debug("Net_Salary2: " + Net_Salary2);
		
		String Net_Salary3 = xmlParserData.getValueOf("Net_Salary3");
		Digital_CC.mLogger.debug("Net_Salary3: " + Net_Salary3);
		
		
		
		
		
		//non stp reason , deviation desc
		String Non_STP_reason = xmlParserData.getValueOf("Non_STP_reason");
		Digital_CC.mLogger.debug("Non_STP_reason: " + Non_STP_reason);
		
		
		//If A -Approve, If D decline: If R with Nstp N: DECLINE; If R with NSTP Y: Refer
		if("A".equalsIgnoreCase(Dectech_Decision))
			Dectech_Decision="Approve";
		else if("D".equalsIgnoreCase(Dectech_Decision))
			Dectech_Decision="Decline";
		else if("R".equalsIgnoreCase(Dectech_Decision))
			{
				if("Y".equalsIgnoreCase(Is_STP))
					Dectech_Decision="Decline";
				else if("N".equalsIgnoreCase(Is_STP))
					Dectech_Decision="Refer";
			}
		

		String Underwriting_decision = xmlParserData.getValueOf("Underwriting_decision");
		Digital_CC.mLogger.debug("Underwriting_decision: " + Underwriting_decision);

		// *********************second query varr
		String user_name  = iformObj.getUserName();
		if(user_name == null){
			user_name = "";
		}
		Digital_CC.mLogger.debug("user_name: " + user_name);

		String Remarks = xmlParserData.getValueOf("remarks");
		Digital_CC.mLogger.debug("Remarks: " + Remarks);

		String decline_reason = xmlParserData.getValueOf("decline_reason");
		Digital_CC.mLogger.debug("decline_reason: " + decline_reason);

		// third query ************************************
		String sQueryDecisionHistory ="select top 1 (select Description from NG_MASTER_EmployerStatusCC where Code= COMPANY_STATUS_CC) as \"COMPANY_STATUS_CC\",(select Description from NG_MASTER_EmployerStatusPL where Code=COMPANY_STATUS_PL) as \"COMPANY_STATUS_PL\", (select Description from NG_MASTER_EmployerCategory_PL where Code=EMPLOYER_CATEGORY_PL) as \"EMPLOYER_CATEGORY_PL\"," +
        "EMPLOYER_CATEGORY_PL_EXPAT,EMPLOYER_CATEGORY_PL_NATIONAL,INCLUDED_IN_CC_ALOC,INCLUDED_IN_PL_ALOC,cast(DATE_OF_INCLUSION_IN_CC_ALOC as date) as DATE_OF_INCLUSION_IN_CC_ALOC," +
        "cast(DATE_OF_INCLUSION_IN_PL_ALOC as date) as DATE_OF_INCLUSION_IN_PL_ALOC,ALOC_REMARKS_PL from NG_RLOS_ALOC_OFFLINE_DATA with (nolock) where EMPLOYER_CODE='"+employercode+"'";

		String extTabDataIPXMLDecisionHistory = Digital_CC_Common.apSelectWithColumnNames(sQueryDecisionHistory, cabinetName,
				sessionId);
		Digital_CC.mLogger.debug("extTabDataIPXMLDecisionHistory: " + extTabDataIPXMLDecisionHistory);
		String extTabDataOPXMLDecisionHistory = Digital_CC_Common.WFNGExecute(extTabDataIPXMLDecisionHistory, serverIp,
				serverPort, 1);
		Digital_CC.mLogger.debug("extTabDataOPXMLDecisionHistory: " + extTabDataOPXMLDecisionHistory);

		XMLParser xmlParserDataDecisionHistory = new XMLParser(extTabDataOPXMLDecisionHistory);
		
		//********************************************************
		
		// variables define 
		
		String COMPANY_STATUS_CC = xmlParserDataDecisionHistory.getValueOf("COMPANY_STATUS_CC");
		Digital_CC.mLogger.debug("COMPANY_STATUS_CC: " + COMPANY_STATUS_CC);
		
		String COMPANY_STATUS_PL = xmlParserDataDecisionHistory.getValueOf("COMPANY_STATUS_PL");
		if(COMPANY_STATUS_PL == null){
			COMPANY_STATUS_PL = "";
		}
		Digital_CC.mLogger.debug("COMPANY_STATUS_PL: " + COMPANY_STATUS_PL);
		
		String EMPLOYER_CATEGORY_PL = xmlParserDataDecisionHistory.getValueOf("EMPLOYER_CATEGORY_PL");
		if(EMPLOYER_CATEGORY_PL == null){
			EMPLOYER_CATEGORY_PL = "";
		}
		Digital_CC.mLogger.debug("EMPLOYER_CATEGORY_PL: " + EMPLOYER_CATEGORY_PL);
		
		
		
		attrbList += "&<currentDateTime>&" + CurrentDateTime;
		attrbList += "&<customerName>&" + CUSTOMERNAME;
		attrbList += "&<nationality>&" + Nationality;
		attrbList += "&<cardType>&" + Selected_Card_Type;
		attrbList += "&<card_application_date>&" + card_application_date;
		attrbList += "&<cardApplicationDate>&" + card_application_date;
		
		attrbList += "&<CIF>&" + Cif_Id;
		attrbList += "&<age>&" + Age;
		attrbList += "&<productType>&" + Product_type;
		attrbList += "&<IPALimit>&" + IPA_Limit;
		attrbList += "&<EFMSStatus>&" + EFMS_Status;
		attrbList += "&<requestedLimit>&" + Requested_Limit;
		attrbList += "&<fircoStatus>&" + FIRCO_Status;

		attrbList += "&<employerName>&" + Employer_Name;
		attrbList += "&<Date_Of_Joining>&" + Date_Of_Joining;
		attrbList += "&<Designation>&" + Designation;
		attrbList += "&<employercode>&" + employercode;
		attrbList += "&<Final_Limit>&" + Final_Limit;
		attrbList += "&<Score_range>&" + Score_range;
		attrbList += "&<Aecb_score>&" + Aecb_score;
		attrbList += "&<FinalDBR>&" + FinalDBR;
		attrbList += "&<DBR_lifeStyle_expenses>&" + DBR_lifeStyle_expenses;
		attrbList += "&<deviation_description>&" + deviation_description;
		attrbList += "&<delegation_authority>&" + delegation_authority;
		attrbList += "&<Dectech_Decision>&" + Dectech_Decision;
		attrbList += "&<Underwriting_decision>&" + Decision;
		attrbList += "&<Decision>&" + Decision;

		// **************//
		attrbList += "&<user_name>&" + user_name;
		attrbList += "&<Remarks>&" + Remarks;
		attrbList += "&<rejectReason>&" + decline_reason;
		
		attrbList += "&<Net_Salary1>&" + Net_Salary1;
		attrbList += "&<Net_Salary2>&" + Net_Salary2;
		attrbList += "&<Net_Salary3>&" + Net_Salary3;
		if (Non_STP_reason.contains("~")){
			Non_STP_reason = Non_STP_reason.replace("~", ",");
		}
		attrbList += "&<Non_STP_reason>&" + Non_STP_reason;
		attrbList += "&<FinalIncome>&" + FinalIncome;
		attrbList += "&<DeclIncome>&" + DeclIncome;

		
		
		attrbList += "&<COMPANY_STATUS_CC>&" + COMPANY_STATUS_CC;
		attrbList += "&<COMPANY_STATUS_PL>&" + COMPANY_STATUS_PL;
		attrbList += "&<EMPLOYER_CATEGORY_PL>&" + EMPLOYER_CATEGORY_PL;
		attrbList += "&<workitemNumber>&" + processInstanceID;

		String output = makeSocketCall(attrbList, processInstanceID, pdfName, sessionId, gtIP, gtPort);
		
		Digital_CC.mLogger.debug("attrbList" + attrbList);
		Digital_CC.mLogger.debug("output" + output);
		
	

		//return attrbList;
		return output; 

	}
	

	public String makeSocketCall(String argumentString, String wi_name, String docName, String sessionId, String gtIP,
			int gtPort) {
		String socketParams = argumentString + "~" + wi_name + "~" + docName + "~" + sessionId;

		System.out.println("socketParams -- " + socketParams);
		Digital_CC.mLogger.debug("socketParams" + socketParams);

		Socket template_socket = null;
		DataOutputStream template_dout = null;
		DataInputStream template_in = null;
		String result = "";
		try {
			// Socket write code started
			template_socket = new Socket(gtIP, gtPort);
			Digital_CC.mLogger.debug("template_socket" + template_socket);

			template_dout = new DataOutputStream(template_socket.getOutputStream());
			Digital_CC.mLogger.debug("template_dout" + template_dout);

			if (socketParams != null && socketParams.length() > 0) {
				int outPut_len = socketParams.getBytes("UTF-8").length;
				Digital_CC.mLogger.debug("outPut_len" + outPut_len);
				// CreditCard.mLogger.info("Final XML output len:
				// "+outPut_len +
				// "");
				socketParams = outPut_len + "##8##;" + socketParams;
				Digital_CC.mLogger.debug("socketParams--" + socketParams);
				// CreditCard.mLogger.info("MqInputRequest"+"Input Request
				// Bytes : "+
				// mqInputRequest.getBytes("UTF-16LE"));

				template_dout.write(socketParams.getBytes("UTF-8"));
				template_dout.flush();
			} else {
				notify();
			}
			// Socket write code ended and read code started
			template_socket.setSoTimeout(60 * 1000);
			template_in = new DataInputStream(new BufferedInputStream(template_socket.getInputStream()));
			byte[] readBuffer = new byte[50000];
			int num = template_in.read(readBuffer);
			if (num > 0) {
				byte[] arrayBytes = new byte[num];
				System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
				result = new String(arrayBytes, "UTF-8");
				Digital_CC.mLogger.debug("result--" + result);
			}
		}

		catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (Exception io) {
			io.printStackTrace();
		} finally {
			try {
				if (template_dout != null) {
					template_dout.close();
					template_dout = null;
				}
				if (template_in != null) {
					template_in.close();
					template_in = null;
				}
				if (template_socket != null) {
					if (!template_socket.isClosed()) {
						template_socket.close();
					}
					template_socket = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
}
