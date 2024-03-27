package com.newgen.DPL;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.XMLParser;

public class Digital_PL_CAMTemplate {

	private static Logger DPL_CamGenrateteLog=null;
	public Digital_PL_CAMTemplate(Logger genCam)
	{
		DPL_CamGenrateteLog=genCam;
	}
	
	public String DPL_generate_CAM_ReportT(String pdfName, String Cif_Id, String processInstanceID, String sessionId)
			throws IOException, Exception {

		DPL_CamGenrateteLog.debug("Inside generate cam report method: ");
		

		String prop_file_loc = System.getProperty("user.dir") + System.getProperty("file.separator") + "ConfigFiles"
				+ System.getProperty("file.separator") + "DPL_CAMGen_Config.properties";
		DPL_CamGenrateteLog.debug("prop_file_loc: " + prop_file_loc);

		File file = new File(prop_file_loc);
		FileInputStream fileInput = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(fileInput);
		fileInput.close();

		String gtIP = properties.getProperty("gtIP");
		DPL_CamGenrateteLog.debug("gtIP: " + gtIP);

		String gtPortProperty = properties.getProperty("gtPort");
		DPL_CamGenrateteLog.debug("gtPortProperty: " + gtPortProperty);

		int gtPort = Integer.parseInt(gtPortProperty);
		DPL_CamGenrateteLog.debug("gtPort: " + gtPort);
		
		// for current date time 
		Date d = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String CurrentDateTime = dateFormat.format(d);
	
		//Updated on 25112022 included FIRCO_Status
//		String Query = "select distinct Is_STP,case when MiddleName is null then CONCAT(FirstName,' ',LastName) else CONCAT(FirstName,' ',MiddleName,' ',LastName) end as CUSTOMERNAME ,Nationality_Desc, Cust_Decl_Salary,FinalTAI,"
//				+ "Product_Desc,(select Card_Type_Desc from ng_dcc_master_cardtype where card_type_code=Selected_Card_Type ) as Selected_Card_Type,Prospect_Creation_Date as card_application_date,Age,ApprovedLimit,EFMS_Status,"
//				+ "Requested_Limit,FIRCO_Flag, FIRCO_Status,Employer_Name,Date_Of_Joining,Designation_Desc,employercode,"
//				+ "FinalDBR,Aecb_score,Final_Limit,DBR_lifeStyle_expenses,deviation_description,"
//				+ "delegation_authority,Score_range,Dectech_Decision,Net_Salary1,Net_Salary2,Net_Salary3,Non_STP_reason,Underwriting_decision,UW_Decision,Decision from NG_DCC_EXTTABLE with (NOLOCK) where Wi_Name ='"
//				+ processInstanceID + "'";

		String Query = "select distinct IsSTP,case when MiddleName is null then CONCAT(FirstName,' ',LastName) "
				+ "else CONCAT(FirstName,' ',MiddleName,' ',LastName) end as CUSTOMERNAME ,Nationality, "
				+ "CustomerDeclaredMonthlyIncome,ProductName,ApprovedLoanAmt,EFMS_Status,RequestedLoanAmount, "
				+ "FIRCO_Status,EmployerName,EmployerCode,DectechDecision from NG_DPL_EXTTABLE with (NOLOCK) "
				+ "where WiName ='" + processInstanceID + "'";

		// for user name and decision at cad analy 1

		
		DPL_CamGenrateteLog.debug("Query : " + Query);
		
		String extTabDataIPXML = CommonMethods.apSelectWithColumnNames(Query, CommonConnection.getCabinetName(),
				CommonConnection.getSessionID(DPL_CamGenrateteLog, false));
		DPL_CamGenrateteLog.debug("extTabDataIPXML: " + extTabDataIPXML);
		String extTabDataOPXML = CommonMethods.WFNGExecute(extTabDataIPXML, CommonConnection.getJTSIP(),
				CommonConnection.getJTSPort(), 1);
		DPL_CamGenrateteLog.debug("extTabDataOPXML: " + extTabDataOPXML);

		XMLParser xmlParserData = new XMLParser(extTabDataOPXML);

		// **************************** for second query
		// ************************
		String extTabDataIPXML2 = CommonMethods.apSelectWithColumnNames(Query, CommonConnection.getCabinetName(),
				CommonConnection.getSessionID(DPL_CamGenrateteLog, false));
		DPL_CamGenrateteLog.debug("extTabDataIPXML2: " + extTabDataIPXML2);
		String extTabDataOPXML2 = CommonMethods.WFNGExecute(extTabDataIPXML2, CommonConnection.getJTSIP(),
				CommonConnection.getJTSPort(), 1);
		DPL_CamGenrateteLog.debug("extTabDataOPXML2: " + extTabDataOPXML2);

		XMLParser xmlParserData2 = new XMLParser(extTabDataOPXML2);
		// ****************************************************************************************
		
		// variables
		String attrbList = "";
		String Is_STP = xmlParserData.getValueOf("IsSTP");
		DPL_CamGenrateteLog.debug("Is_STP: " + Is_STP);

		String CifId = xmlParserData.getValueOf("CIF");
		String CUSTOMERNAME = xmlParserData.getValueOf("CUSTOMERNAME");
		DPL_CamGenrateteLog.debug("CUSTOMERNAME: " + CUSTOMERNAME);

		String Nationality = xmlParserData.getValueOf("Nationality");
		DPL_CamGenrateteLog.debug("Nationality: " + Nationality);

		//String Card_Type = xmlParserData.getValueOf("Selected_Card_Type");
		//DPL_CamGenrateteLog.debug("Card_Type: " + Card_Type);
		
		String Product_type = xmlParserData.getValueOf("ProductName");
		DPL_CamGenrateteLog.debug("Product_Desc: " + Product_type);

//		String Selected_Card_Type = xmlParserData.getValueOf("Selected_Card_Type");
//		DPL_CamGenrateteLog.debug("Selected_Card_Type: " + Selected_Card_Type);
//
//		String card_application_date = xmlParserData.getValueOf("card_application_date");
//		if(card_application_date!=null && card_application_date.length()>=10)
//		card_application_date=card_application_date.substring(0,10); //CommonMethods.parseDate(card_application_date,"yyyy-MM-dd HH:mm:ss","dd-MM-yyyy");//2022-09-30 00:00:00
//		DPL_CamGenrateteLog.debug("card_application_date: " + card_application_date);
//		String Age = xmlParserData.getValueOf("Age");
//		DPL_CamGenrateteLog.debug("Age: " + Age);

		String IPA_Limit = xmlParserData.getValueOf("ApprovedLoanAmt");
		DPL_CamGenrateteLog.debug("IPA_Limit: " + IPA_Limit);

		String EFMS_Status = xmlParserData.getValueOf("EFMS_Status");
		DPL_CamGenrateteLog.debug("EFMS_Status: " + EFMS_Status);

		String Requested_Limit = xmlParserData.getValueOf("RequestedLoanAmount");
		DPL_CamGenrateteLog.debug("Requested_Limit: " + Requested_Limit);

		String FIRCO_Flag = xmlParserData.getValueOf("FIRCO_Status");
		DPL_CamGenrateteLog.debug("FIRCO_Flag: " + FIRCO_Flag);
		
		if ("CB".equalsIgnoreCase(FIRCO_Flag) || "N".equalsIgnoreCase(FIRCO_Flag)) {
			FIRCO_Flag = "Hit";
		} else {
			FIRCO_Flag = "No-Hit";
		}
		DPL_CamGenrateteLog.debug("FIRCO_Flag: " + FIRCO_Flag);

		String Employer_Name = xmlParserData.getValueOf("EmployerName");
		DPL_CamGenrateteLog.debug("Employer_Name: " + Employer_Name);

//		String Date_Of_Joining = xmlParserData.getValueOf("Date_Of_Joining");
//		DPL_CamGenrateteLog.debug("Date_Of_Joining: " + Date_Of_Joining);

//		String Designation = xmlParserData.getValueOf("Designation_Desc");
//		DPL_CamGenrateteLog.debug("Designation: " + Designation);
		
		String employercode = xmlParserData.getValueOf("EmployerCode");
		DPL_CamGenrateteLog.debug("employercode: " + employercode);

		// employer status pl miss

//		String Final_Limit = xmlParserData.getValueOf("Final_Limit");
//		DPL_CamGenrateteLog.debug("Final_Limit: " + Final_Limit);

		// declared income miss
		String DeclIncome = xmlParserData.getValueOf("CustomerDeclaredMonthlyIncome");
		DPL_CamGenrateteLog.debug("DeclIncome: " + DeclIncome);
		
//		String FinalIncome = xmlParserData.getValueOf("FinalTAI");
//		DPL_CamGenrateteLog.debug("FinalIncome: " + FinalIncome);
		
//		String Decision = xmlParserData.getValueOf("Decision");
//		DPL_CamGenrateteLog.debug("Decision: " + Decision);

//		String Aecb_score = xmlParserData.getValueOf("Aecb_score");
//		DPL_CamGenrateteLog.debug("Aecb_score: " + Aecb_score);

/*		String Score_range = xmlParserData.getValueOf("Score_range");
		DPL_CamGenrateteLog.debug("Score_range: " + Score_range);

		String FinalDBR = xmlParserData.getValueOf("FinalDBR");
		DPL_CamGenrateteLog.debug("FinalDBR: " + FinalDBR);

		String DBR_lifeStyle_expenses = xmlParserData.getValueOf("DBR_lifeStyle_expenses");
		DPL_CamGenrateteLog.debug("DBR_lifeStyle_expenses: " + DBR_lifeStyle_expenses);

		String deviation_description = xmlParserData.getValueOf("deviation_description");
		DPL_CamGenrateteLog.debug("deviation_description: " + deviation_description);

		String delegation_authority = xmlParserData.getValueOf("delegation_authority");
		DPL_CamGenrateteLog.debug("delegation_authority: " + delegation_authority);
		
		
		String Net_Salary1 = xmlParserData.getValueOf("Net_Salary1");
		DPL_CamGenrateteLog.debug("Net_Salary1: " + Net_Salary1);
		
		String Net_Salary2 = xmlParserData.getValueOf("Net_Salary2");
		DPL_CamGenrateteLog.debug("Net_Salary2: " + Net_Salary2);
		
		String Net_Salary3 = xmlParserData.getValueOf("Net_Salary3");
		DPL_CamGenrateteLog.debug("Net_Salary3: " + Net_Salary3);
		
		//non stp reason , deviation desc
		/*String Non_STP_reason = xmlParserData.getValueOf("Non_STP_reason");
		DPL_CamGenrateteLog.debug("Non_STP_reason: " + Non_STP_reason);*/
		
		
		//If A -Approve, If D decline: If R with Nstp N: DECLINE; If R with NSTP Y: Refer
		
		String Dectech_Decision = xmlParserData.getValueOf("DectechDecision");
		DPL_CamGenrateteLog.debug("Dectech_Decision: " + Dectech_Decision);
		
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
		

//		String Underwriting_decision = xmlParserData.getValueOf("UW_Decision");
//		DPL_CamGenrateteLog.debug("Underwriting_decision: " + Underwriting_decision);

		// *********************second query varr
//		String user_name = xmlParserData2.getValueOf("user_name");
//		DPL_CamGenrateteLog.debug("user_name: " + user_name);
//
//		String Remarks = xmlParserData2.getValueOf("Remarks");
//		DPL_CamGenrateteLog.debug("Remarks: " + Remarks);
//
//		String rejectReason = xmlParserData2.getValueOf("rejectReason");
//		DPL_CamGenrateteLog.debug("rejectReason: " + rejectReason);

		// third query ************************************
		String sQueryDecisionHistory ="select top 1 (select Description from NG_MASTER_EmployerStatusCC where Code= COMPANY_STATUS_CC) as \"COMPANY_STATUS_CC\",(select Description from NG_MASTER_EmployerStatusPL where Code=COMPANY_STATUS_PL) as \"COMPANY_STATUS_PL\", (select Description from NG_MASTER_EmployerCategory_PL where Code=EMPLOYER_CATEGORY_PL) as \"EMPLOYER_CATEGORY_PL\"," +
        "EMPLOYER_CATEGORY_PL_EXPAT,EMPLOYER_CATEGORY_PL_NATIONAL,INCLUDED_IN_CC_ALOC,INCLUDED_IN_PL_ALOC,cast(DATE_OF_INCLUSION_IN_CC_ALOC as date) as DATE_OF_INCLUSION_IN_CC_ALOC," +
        "cast(DATE_OF_INCLUSION_IN_PL_ALOC as date) as DATE_OF_INCLUSION_IN_PL_ALOC,ALOC_REMARKS_PL from NG_RLOS_ALOC_OFFLINE_DATA with (nolock) where EMPLOYER_CODE='"+employercode+"'";

		String extTabDataIPXMLDecisionHistory = CommonMethods.apSelectWithColumnNames(sQueryDecisionHistory, CommonConnection.getCabinetName(),
				CommonConnection.getSessionID(DPL_CamGenrateteLog, false));
		DPL_CamGenrateteLog.debug("extTabDataIPXMLDecisionHistory: " + extTabDataIPXMLDecisionHistory);
		String extTabDataOPXMLDecisionHistory = CommonMethods.WFNGExecute(extTabDataIPXMLDecisionHistory, CommonConnection.getJTSIP(),
				CommonConnection.getJTSPort(), 1);
		DPL_CamGenrateteLog.debug("extTabDataOPXMLDecisionHistory: " + extTabDataOPXMLDecisionHistory);

		XMLParser xmlParserDataDecisionHistory = new XMLParser(extTabDataOPXMLDecisionHistory);
		
		//********************************************************
		
		// variables define 
		
		String COMPANY_STATUS_CC = xmlParserDataDecisionHistory.getValueOf("COMPANY_STATUS_CC");
		DPL_CamGenrateteLog.debug("COMPANY_STATUS_CC: " + COMPANY_STATUS_CC);
		
		String COMPANY_STATUS_PL = xmlParserDataDecisionHistory.getValueOf("COMPANY_STATUS_PL");
		DPL_CamGenrateteLog.debug("COMPANY_STATUS_PL: " + COMPANY_STATUS_PL);
		
		String EMPLOYER_CATEGORY_PL = xmlParserDataDecisionHistory.getValueOf("EMPLOYER_CATEGORY_PL");
		DPL_CamGenrateteLog.debug("EMPLOYER_CATEGORY_PL: " + EMPLOYER_CATEGORY_PL);
		if(EMPLOYER_CATEGORY_PL==null)
			EMPLOYER_CATEGORY_PL="";
		
		//Aloc data save in db added by rubi
		//String column = "COMPANY_STATUS_CC,COMPANY_STATUS_PL,EMPLOYER_CATEGORY_PL";
		//String 
		attrbList += "&<currentDateTime>&" + CurrentDateTime;
		attrbList += "&<customerName>&" + CUSTOMERNAME;
		attrbList += "&<nationality>&" + Nationality;
		attrbList += "&<cardType>&" + "";
		attrbList += "&<cardApplicationDate>&" + "";
		
		attrbList += "&<CIF>&" + Cif_Id;
		attrbList += "&<age>&" + "";
		attrbList += "&<productType>&" + Product_type;
		attrbList += "&<IPALimit>&" + IPA_Limit;
		attrbList += "&<EFMSStatus>&" + EFMS_Status;
		attrbList += "&<requestedLimit>&" + Requested_Limit;
		attrbList += "&<fircoStatus>&" + FIRCO_Flag;
		
		DPL_CamGenrateteLog.debug("attrbList1: " + attrbList);

		attrbList += "&<employerName>&" + Employer_Name;
		attrbList += "&<Date_Of_Joining>&" + "";
		attrbList += "&<Designation>&" + "";
		attrbList += "&<employercode>&" + employercode;
		attrbList += "&<Final_Limit>&" + "";
		attrbList += "&<Score_range>&" + "";
		attrbList += "&<Aecb_score>&" + "";
		attrbList += "&<FinalDBR>&" + "";
		attrbList += "&<DBR_lifeStyle_expenses>&" + "";
		attrbList += "&<deviation_description>&" + "";
		attrbList += "&<delegation_authority>&" + "";
		attrbList += "&<Dectech_Decision>&" + Dectech_Decision;
		attrbList += "&<Underwriting_decision>&" + "";
		attrbList += "&<Decision>&" + "";
		DPL_CamGenrateteLog.debug("FinalIncome: " + "" + "DeclIncome: "+"");
		attrbList += "&<FinalIncome>&" + "";
		attrbList += "&<DeclIncome>&" + DeclIncome;

		DPL_CamGenrateteLog.debug("attrbList2: " + attrbList);
		// **************//
		attrbList += "&<user_name>&" + "";
		attrbList += "&<Remarks>&" + "";
		attrbList += "&<rejectReason>&" + "";
		
		DPL_CamGenrateteLog.debug("attrbList3: " + attrbList);
		
		attrbList += "&<Net_Salary1>&" + "";
		attrbList += "&<Net_Salary2>&" + "";
		attrbList += "&<Net_Salary3>&" + "";
		
		attrbList += "&<COMPANY_STATUS_CC>&" + COMPANY_STATUS_CC;
		attrbList += "&<COMPANY_STATUS_PL>&" + COMPANY_STATUS_PL;
		attrbList += "&<EMPLOYER_CATEGORY_PL>&" + EMPLOYER_CATEGORY_PL;
		attrbList += "&<workitemNumber>&" + processInstanceID;

		DPL_CamGenrateteLog.debug("attrbList" + attrbList);
		
		String output = makeSocketCall(attrbList, processInstanceID, pdfName, sessionId, gtIP, gtPort);
		
		DPL_CamGenrateteLog.debug("attrbList" + attrbList);
		DPL_CamGenrateteLog.debug("output" + output);
		
	

		return output;

	}

	public String makeSocketCall(String argumentString, String wi_name, String docName, String sessionId, String gtIP,
			int gtPort) {
		String socketParams = argumentString + "~" + wi_name + "~" + docName + "~" + sessionId;

		System.out.println("socketParams -- " + socketParams);
		DPL_CamGenrateteLog.debug("socketParams" + socketParams);

		Socket template_socket = null;
		DataOutputStream template_dout = null;
		DataInputStream template_in = null;
		String result = "";
		try {
			// Socket write code started
			template_socket = new Socket(gtIP, gtPort);
			DPL_CamGenrateteLog.debug("template_socket" + template_socket);

			template_dout = new DataOutputStream(template_socket.getOutputStream());
			DPL_CamGenrateteLog.debug("template_dout" + template_dout);

			if (socketParams != null && socketParams.length() > 0) {
				int outPut_len = socketParams.getBytes("UTF-8").length;
				DPL_CamGenrateteLog.debug("outPut_len" + outPut_len);
				// CreditCard.mLogger.info("Final XML output len:
				// "+outPut_len +
				// "");
				socketParams = outPut_len + "##8##;" + socketParams;
				DPL_CamGenrateteLog.debug("socketParams--" + socketParams);
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
				DPL_CamGenrateteLog.debug("result--" + result);
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
