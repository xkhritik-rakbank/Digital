package com.newgen.iforms.user;

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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.newgen.iforms.custom.IFormReference;
import com.newgen.omni.jts.cmgr.XMLParser;

public class Digital_PL_GenerateCam extends Digital_PL_Common {
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
		

		 Digital_PL.mLogger.debug("configReadStatus "+configReadStatus);
		if(configReadStatus !=0)
		{
			Digital_PL.mLogger.error("Could not Read Config Properties");
			return "";
		}
		  String DBQuery = "SELECT Is_CAM_generated,CIF,IsSTP FROM NG_DPL_EXTTABLE with(nolock) WHERE WINAME='" + processInstanceID + "'";
		 
	         
	        String extTabDataINPXML = Digital_PL_Common.apSelectWithColumnNames(DBQuery,  getCabinetName(iformObj),getSessionId(iformObj));
	        Digital_PL.mLogger.debug("extTabDataIPXML: " + extTabDataINPXML);
	        //String extTabDataOUPXML = Digital_PL_Common.WFNGExecute(extTabDataINPXML, iformObj.getServerIp(), iformObj.getServerPort(), 1);
	        String extTabDataOUPXML = Digital_PL_Common.WFNGExecute(extTabDataINPXML, iformObj.getServerIp(), iformObj.getServerPort(), 1);
	        Digital_PL.mLogger.debug("extTabDataOPXML: " + extTabDataOUPXML);	
			
	        XMLParser xmlParserDataDB = new XMLParser(extTabDataOUPXML);
	        
	        String Is_CAM_Generated = xmlParserDataDB.getValueOf("Is_CAM_generated");
	        Digital_PL.mLogger.debug("Is_CAM_Generated: inside cam generation onevent " + Is_CAM_Generated);
	        String Cif_Id = xmlParserDataDB.getValueOf("CIF");
	        Digital_PL.mLogger.debug("Cif_Id: inside cam generation onevent " + Cif_Id);
	        String Is_STP = xmlParserDataDB.getValueOf("Is_STP");
	        Digital_PL.mLogger.debug("Is_STP: inside cam generation onevent " + Is_STP);
	        String pdfName = "DPL_NON_STP_CAM_Report";
	        if(Is_STP.equalsIgnoreCase("Y") ){
	        	Is_STP = "N";
	        	Is_CAM_Generated ="N";
	        }
	        Digital_PL.mLogger.debug("pdfName: inside cam generation onevent " + pdfName);
	        
	        	/*if ("N".equalsIgnoreCase(Is_STP)){
	        		
	        		 pdfName = "DPL_STP_CAM_Report";
	        		 Digital_PL.mLogger.debug("pdfName: inside cam generation onevent " + pdfName);
		        	 
	        	}*/
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
				Digital_PL.mLogger.debug("Cam Report Is Already Generated");
			}*/
	        
	      return "FAIL";
	}
	
	private int readConfig()
	{
		Properties p = null;
		try {

			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "CustomConfig"+ File.separator+ "DPL_CAMGen_Config.properties")));

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

		Digital_PL.mLogger.debug("Inside generate cam report method: ");
		
		String gtIP = configParamMap.get("gtIP");
		Digital_PL.mLogger.debug("gtIP: " + gtIP);

		String gtPortProperty = configParamMap.get("gtPort");
		Digital_PL.mLogger.debug("gtPortProperty: " + gtPortProperty);

		int gtPort = Integer.parseInt(gtPortProperty);
		Digital_PL.mLogger.debug("gtPort: " + gtPort);
		
		// for current date time 
		Date d = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String CurrentDateTime = dateFormat.format(d);
	
		/*String Query = "select distinct a.IsSTP,case when a.MiddleName is null then CONCAT(a.FirstName,' ',a.LastName) else "
                + "CONCAT(a.FirstName,' ',a.MiddleName,' ',a.LastName) end as CUSTOMERNAME ,a.WINAME, a.CIF,a.ProspectID,a.Age,"
                + "a.Nationality,a.Score_range,a.AECB_Score, a.EmployerName, a.StartofJob,a.JobTitle,a.EmployerCode,a.ProductType,"
                + "a.RequestedLoanAmount, a.RequestedLoanTenor,a.FirstRepaymentDate,a.CustomerDeclaredMonthlyIncome,a.FinalDBR,"
                + "c.AECB_Sal_Month_1,c.AECB_Sal_Month_2,c.AECB_Sal_Month_3 ,a.DectechDecision,a.Final_Limit"
                + "from NG_DPL_EXTTABLE a with (NOLOCK) "
                + "join NG_DPL_AECB_Details c with (NOLOCK) on a.WINAME=c.WI_NAME "
                + "join NG_DPL_EmploymentDetails d with (NOLOCK) on a.WINAME= d.WI_NAME "
                + "where a.WINAME  ='" + processInstanceID + "'";*/
		
		String Query = "select distinct a.IsSTP,case when a.MiddleName is null then CONCAT(a.FirstName,' ',a.LastName) else "
				+ "CONCAT(a.FirstName,' ',a.MiddleName,' ',a.LastName) end as CUSTOMERNAME ,a.WINAME, a.CIF,a.ProspectID,a.Age,a.EMI,"
				+ "a.Nationality,a.Score_range,a.AECB_Score, a.EmployerName, a.StartofJob,a.JobTitle,a.delegation_authority,"
				+ "a.EmployerCode,a.ProductType,a.LoanType,a.FinalDBR,a.FinalTAI,a.LoanAmount,a.LoanMultiple,a.StressDBR,a.AffordabilityRatio,"
				+ "a.RequestedLoanAmount, a.RequestedLoanTenor,a.FirstRepaymentDate,"
				+ "a.CustomerDeclaredMonthlyIncome,d.EMPLOYER_CATEGORY_PL_EXPAT,d.EMPLOYER_CATEGORY_PL_NATIONAL,"
				+ "c.AECB_Sal_Month_1,c.AECB_Sal_Month_2,c.AECB_Sal_Month_3 ,a.DectechDecision,a.Final_Limit "
				+ "from NG_DPL_EXTTABLE a with (NOLOCK) "
				+ "join NG_DPL_AECB_Details c with (NOLOCK) on a.WINAME=c.WI_NAME "
				+ "join NG_DPL_EmploymentDetails d with (NOLOCK) on a.WINAME= d.WI_NAME "
				+ "where a.WINAME  ='" + processInstanceID + "'";

		

		// for user name and decision at cad analy 1

		String Query2 = "select Top 1 username,Decision,Remarks,Reject_Reason from NG_DPL_GR_DECISION_HISTORY with (NOLOCK)  where wi_name ='"
				+ processInstanceID + "' order by Decision_Date_Time desc";
		
		//String Query3 = "select card_type_desc from ng_dcc_master_cardType with(nolock) where card_type_code = (select top 1 Selected_Card_Type from NG_DCC_EXTTABLE with(nolock) where Wi_Name='"+processInstanceID+"' )";

		Digital_PL.mLogger.debug("Query : " + Query);
		Digital_PL.mLogger.debug("Query2 : " + Query2);
		//Digital_PL.mLogger.debug("Query3 : " + Query3);
		
		String extTabDataIPXML = Digital_PL_Common.apSelectWithColumnNames(Query, cabinetName,sessionId);
		Digital_PL.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
		String extTabDataOPXML = Digital_PL_Common.WFNGExecute(extTabDataIPXML, serverIp,
				serverPort, 1);
		Digital_PL.mLogger.debug("extTabDataOPXML: " + extTabDataOPXML);

		XMLParser xmlParserData = new XMLParser(extTabDataOPXML);

		// **************************** for second query
		// ************************
		String extTabDataIPXML2 = Digital_PL_Common.apSelectWithColumnNames(Query2,cabinetName,sessionId);
		Digital_PL.mLogger.debug("extTabDataIPXML2: " + extTabDataIPXML2);
		String extTabDataOPXML2 = Digital_PL_Common.WFNGExecute(extTabDataIPXML2, serverIp,
				serverPort, 1);
		Digital_PL.mLogger.debug("extTabDataOPXML2: " + extTabDataOPXML2);

		XMLParser xmlParserData2 = new XMLParser(extTabDataOPXML2);
		
		// ****************************************************************************************
		
		// variables
		String Is_STP = xmlParserData.getValueOf("IsSTP");
		Digital_PL.mLogger.debug("Is_STP: " + Is_STP);

		String CifId = xmlParserData.getValueOf("CIF");
		String CUSTOMERNAME = xmlParserData.getValueOf("CUSTOMERNAME");
		Digital_PL.mLogger.debug("CUSTOMERNAME: " + CUSTOMERNAME);

		String Nationality = xmlParserData.getValueOf("Nationality");
		Digital_PL.mLogger.debug("Nationality: " + Nationality);
		
		String Employer_Name = xmlParserData.getValueOf("EmployerName");
		Digital_PL.mLogger.debug("Employer_Name: " + Employer_Name);

		String Date_Of_Joining = xmlParserData.getValueOf("StartofJob");
		Digital_PL.mLogger.debug("Date_Of_Joining: " + Date_Of_Joining);

		String Designation = xmlParserData.getValueOf("JobTitle");
		Digital_PL.mLogger.debug("Designation: " + Designation);
		
		String EmployerCode = xmlParserData.getValueOf("EmployerCode");
		Digital_PL.mLogger.debug("employercode: " + EmployerCode);
		
		String DeclIncome = xmlParserData.getValueOf("CustomerDeclaredMonthlyIncome");
		Digital_PL.mLogger.debug("DeclIncome: " + DeclIncome);
		
		
		String ProspectID = xmlParserData.getValueOf("ProspectID");
		Digital_PL.mLogger.debug("ProspectID: " + ProspectID);
		
		String Age = xmlParserData.getValueOf("Age");
		Digital_PL.mLogger.debug("Age: " + Age);

		String AECB_Score = xmlParserData.getValueOf("AECB_Score");
		Digital_PL.mLogger.debug("Aecb_score: " + AECB_Score);

		String Score_range = xmlParserData.getValueOf("Score_range");
		Digital_PL.mLogger.debug("Score_range: " + Score_range);
		
		String StartofJob = xmlParserData.getValueOf("StartofJob");
		Digital_PL.mLogger.debug("StartofJob: " + StartofJob);
		
		
		String JobTitle = xmlParserData.getValueOf("JobTitle");
		Digital_PL.mLogger.debug("JobTitle: " + JobTitle);
		
		String EMPLOYER_CATEGORY_PL_EXPAT = xmlParserData.getValueOf("EMPLOYER_CATEGORY_PL_EXPAT");
		Digital_PL.mLogger.debug("EMPLOYER_CATEGORY_PL_EXPAT: " + EMPLOYER_CATEGORY_PL_EXPAT);
		
		String EMPLOYER_CATEGORY_PL_NATIONAL = xmlParserData.getValueOf("EMPLOYER_CATEGORY_PL_NATIONAL");
		Digital_PL.mLogger.debug("EMPLOYER_CATEGORY_PL_NATIONAL: " + EMPLOYER_CATEGORY_PL_NATIONAL);
		
		
		String LoanType = xmlParserData.getValueOf("LoanType");
		Digital_PL.mLogger.debug("LoanType: " + LoanType);
		
		String FinalDBR = xmlParserData.getValueOf("FinalDBR");
		Digital_PL.mLogger.debug("FinalDBR: " + FinalDBR);
		
		String RequestedLoanTenor = xmlParserData.getValueOf("RequestedLoanTenor");
		Digital_PL.mLogger.debug("RequestedLoanTenor: " + RequestedLoanTenor);
		
		String FirstRepaymentDate = xmlParserData.getValueOf("FirstRepaymentDate");
		Digital_PL.mLogger.debug("FirstRepaymentDate: " + FirstRepaymentDate);
		
		String CustomerDeclaredMonthlyIncome = xmlParserData.getValueOf("CustomerDeclaredMonthlyIncome");
		Digital_PL.mLogger.debug("CustomerDeclaredMonthlyIncome: " + CustomerDeclaredMonthlyIncome);
		
		String AECB_Sal_Month_1 = xmlParserData.getValueOf("AECB_Sal_Month_1");
		Digital_PL.mLogger.debug("AECB_Sal_Month_1: " + AECB_Sal_Month_1);
		
		String AECB_Sal_Month_2 = xmlParserData.getValueOf("AECB_Sal_Month_2");
		Digital_PL.mLogger.debug("AECB_Sal_Month_2: " + AECB_Sal_Month_2);
		
		String AECB_Sal_Month_3 = xmlParserData.getValueOf("AECB_Sal_Month_3");
		Digital_PL.mLogger.debug("AECB_Sal_Month_3: " + AECB_Sal_Month_3);
		
		String FTS_Sal_Month_1 =  (String) iformObj.getValue("Net_Salary_Month1");
		Digital_PL.mLogger.debug("FTS_Sal_Month_1: " + FTS_Sal_Month_1);
		
		String FTS_Sal_Month_2 =  (String) iformObj.getValue("Net_Salary_Month2");
		Digital_PL.mLogger.debug("FTS_Sal_Month2: " + FTS_Sal_Month_2);
		
		String FTS_Sal_Month_3 =  (String) iformObj.getValue("Net_Salary_Month3");
		Digital_PL.mLogger.debug("FTS_Sal_Month_3: " + FTS_Sal_Month_3);
		//If A -Approve, If D decline: If R with Nstp N: DECLINE; If R with NSTP Y: Refer
		
		String Dectech_Decision = xmlParserData.getValueOf("DectechDecision");
		Digital_PL.mLogger.debug("Dectech_Decision: " + Dectech_Decision);
		
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
		
		String FinalTAI = xmlParserData.getValueOf("FinalTAI");
		Digital_PL.mLogger.debug("FinalTAI: " + FinalTAI);
		
		String LoanAmount = xmlParserData.getValueOf("LoanAmount");
		Digital_PL.mLogger.debug("LoanAmount: " + LoanAmount);
		
		String LoanMultiple = xmlParserData.getValueOf("LoanMultiple");
		Digital_PL.mLogger.debug("LoanMultiple: " + LoanMultiple);
		
		String StressDBR = xmlParserData.getValueOf("StressDBR");
		Digital_PL.mLogger.debug("StressDBR: " + StressDBR);
		
		String AffordabilityRatio = xmlParserData.getValueOf("AffordabilityRatio");
		Digital_PL.mLogger.debug("AffordabilityRatio: " + AffordabilityRatio);
		
		String delegation_authority = xmlParserData.getValueOf("delegation_authority");
		Digital_PL.mLogger.debug("delegation_authority: " + delegation_authority);
		
		String WI_Name = (String) iformObj.getValue("WI_Number");
		Digital_PL.mLogger.debug("WI_Name: " + WI_Name);
		
		String EMI = xmlParserData.getValueOf("EMI");
		Digital_PL.mLogger.debug("EMI: " + EMI);
		
		String los = "";
		Digital_PL.mLogger.debug("los: " + los);
		
		
		

		String Underwriting_decision = xmlParserData.getValueOf("Underwriting_decision");
		Digital_PL.mLogger.debug("Underwriting_decision: " + Underwriting_decision);

		// *********************second query varr
		String user_name  = iformObj.getUserName();
		if(user_name == null){
			user_name = "";
		}
		Digital_PL.mLogger.debug("user_name: " + user_name);

		String Remarks = xmlParserData.getValueOf("remarks");
		Digital_PL.mLogger.debug("Remarks: " + Remarks);

		String decline_reason = xmlParserData.getValueOf("decline_reason");
		Digital_PL.mLogger.debug("decline_reason: " + decline_reason);

		// third query ************************************
		String sQueryDecisionHistory ="select top 1 (select Description from NG_MASTER_EmployerStatusCC where Code= COMPANY_STATUS_CC) as \"COMPANY_STATUS_CC\",(select Description from NG_MASTER_EmployerStatusPL where Code=COMPANY_STATUS_PL) as \"COMPANY_STATUS_PL\", (select Description from NG_MASTER_EmployerCategory_PL where Code=EMPLOYER_CATEGORY_PL) as \"EMPLOYER_CATEGORY_PL\"," +
        "EMPLOYER_CATEGORY_PL_EXPAT,EMPLOYER_CATEGORY_PL_NATIONAL,INCLUDED_IN_CC_ALOC,INCLUDED_IN_PL_ALOC,cast(DATE_OF_INCLUSION_IN_CC_ALOC as date) as DATE_OF_INCLUSION_IN_CC_ALOC," +
        "cast(DATE_OF_INCLUSION_IN_PL_ALOC as date) as DATE_OF_INCLUSION_IN_PL_ALOC,ALOC_REMARKS_PL from NG_RLOS_ALOC_OFFLINE_DATA with (nolock) where EMPLOYER_CODE='"+EmployerCode+"'";

		String extTabDataIPXMLDecisionHistory = Digital_PL_Common.apSelectWithColumnNames(sQueryDecisionHistory, cabinetName,
				sessionId);
		Digital_PL.mLogger.debug("extTabDataIPXMLDecisionHistory: " + extTabDataIPXMLDecisionHistory);
		String extTabDataOPXMLDecisionHistory = Digital_PL_Common.WFNGExecute(extTabDataIPXMLDecisionHistory, serverIp,
				serverPort, 1);
		Digital_PL.mLogger.debug("extTabDataOPXMLDecisionHistory: " + extTabDataOPXMLDecisionHistory);

		XMLParser xmlParserDataDecisionHistory = new XMLParser(extTabDataOPXMLDecisionHistory);
		
		//********************************************************
		
		// variables define 
		
		String COMPANY_STATUS_CC = xmlParserDataDecisionHistory.getValueOf("COMPANY_STATUS_CC");
		Digital_PL.mLogger.debug("COMPANY_STATUS_CC: " + COMPANY_STATUS_CC);
		String attrbList="";
		String COMPANY_STATUS_PL = xmlParserDataDecisionHistory.getValueOf("COMPANY_STATUS_PL");
		if(COMPANY_STATUS_PL == null){
			COMPANY_STATUS_PL = "";
		}
		Digital_PL.mLogger.debug("COMPANY_STATUS_PL: " + COMPANY_STATUS_PL);
		
		String EMPLOYER_CATEGORY_PL = xmlParserDataDecisionHistory.getValueOf("EMPLOYER_CATEGORY_PL");
		if(EMPLOYER_CATEGORY_PL == null){
			EMPLOYER_CATEGORY_PL = "";
		}
		Digital_PL.mLogger.debug("EMPLOYER_CATEGORY_PL: " + EMPLOYER_CATEGORY_PL);
		
		attrbList += "&<currentDateTime>&" + CurrentDateTime;
		attrbList += "&<customerName>&" + CUSTOMERNAME;
		attrbList += "&<workitemNumber>&" + WI_Name;
		attrbList += "&<nationality>&" + Nationality;
		attrbList += "&<cardType>&" + "";
		attrbList += "&<cardApplicationDate>&" + "";
		
		attrbList += "&<CIF>&" + Cif_Id;
		attrbList += "&<age>&" + "";
		
		
		attrbList += "&<agreementNumber>&" + ProspectID;
		attrbList += "&<currentAge>&" +Age;
		attrbList += "&<ageAtMaturity>&" +Age;
		attrbList += "&<sourceBranch>&" +"";
		attrbList += "&<AECBRange>&" +Score_range;
		attrbList += "&<AECBScore>&"+ AECB_Score;
		
		Digital_PL.mLogger.debug("attrbList1: " + attrbList);

		attrbList += "&<employerName>&" + Employer_Name;
		attrbList += "&<Date_Of_Joining>&" + StartofJob;
		attrbList += "&<Designation>&" + JobTitle;
		attrbList += "&<employerCode>&" + EmployerCode;
		
		attrbList += "&<yearsInEmployment>&" +los;
		attrbList += "&<employerStatusCC>&"+COMPANY_STATUS_CC;//
		attrbList += "&<employerCategoryPL>&" +COMPANY_STATUS_PL;// ---(NG_DPL_EmploymentDetails)
		attrbList += "&<EMPLOYER_STATUS_PL_EXPAT>&"+EMPLOYER_CATEGORY_PL_EXPAT;// ---(NG_DPL_EmploymentDetails)
		attrbList += "&<EMPLOYER_STATUS_PL_NATIONAL>&"+EMPLOYER_CATEGORY_PL_NATIONAL; // ---(NG_DPL_EmploymentDetails)
		attrbList += "&<NATURE_OF_BUSINESS>&"+"";
		attrbList += "&<loanType>&"+LoanType;
		attrbList += "&<loanApplicationDate>&"+"";
		attrbList += "&<loanAmount>&"+FinalDBR;
		
		attrbList += "&<Tenor>&"+RequestedLoanTenor;
		attrbList += "&<firstRepaymentDetail>&"+FirstRepaymentDate;
		attrbList += "&<loanMultiple>&"+LoanMultiple;
		attrbList += "&<loanEMI>&"+EMI;
		attrbList += "&<declaredIncome>&"+CustomerDeclaredMonthlyIncome;
		attrbList += "&<totalIncome>&"+"";
		attrbList += "&<fianlDBR>&"+FinalDBR;
		
		attrbList += "&<netSalary1>&"+FTS_Sal_Month_1;
		attrbList += "&<netSalary2>&"+FTS_Sal_Month_2;
		attrbList += "&<netSalary3>&"+FTS_Sal_Month_3;
		attrbList += "&<stressDBR>&"+StressDBR;
		attrbList += "&<affordabilityRatio>&"+AffordabilityRatio;
		
		attrbList += "&<AECBsalary1>&"+AECB_Sal_Month_1;
		attrbList += "&<AECBsalary2>&"+AECB_Sal_Month_2;
		attrbList += "&<AECBsalary3>&"+AECB_Sal_Month_3;
		attrbList += "&<reviewedBy>&"+user_name;
		attrbList += "&<creditRemarks>&"+Remarks;
		attrbList += "&<OutputDelegationAuthority>&"+delegation_authority;
		
		attrbList += "&<finalDecision>&"+Dectech_Decision;
		
		
		String output = makeSocketCall(attrbList, processInstanceID, pdfName, sessionId, gtIP, gtPort);
		
		Digital_PL.mLogger.debug("attrbList" + attrbList);
		Digital_PL.mLogger.debug("output" + output);
		
	

		//return attrbList;
		return output; 

	}
	

	public String makeSocketCall(String argumentString, String wi_name, String docName, String sessionId, String gtIP,
			int gtPort) {
		String socketParams = argumentString + "~" + wi_name + "~" + docName + "~" + sessionId;

		System.out.println("socketParams -- " + socketParams);
		Digital_PL.mLogger.debug("socketParams" + socketParams);

		Socket template_socket = null;
		DataOutputStream template_dout = null;
		DataInputStream template_in = null;
		String result = "";
		try {
			// Socket write code started
			template_socket = new Socket(gtIP, gtPort);
			Digital_PL.mLogger.debug("template_socket" + template_socket);

			template_dout = new DataOutputStream(template_socket.getOutputStream());
			Digital_PL.mLogger.debug("template_dout" + template_dout);

			if (socketParams != null && socketParams.length() > 0) {
				int outPut_len = socketParams.getBytes("UTF-8").length;
				Digital_PL.mLogger.debug("outPut_len" + outPut_len);
				// CreditCard.mLogger.info("Final XML output len:
				// "+outPut_len +
				// "");
				socketParams = outPut_len + "##8##;" + socketParams;
				Digital_PL.mLogger.debug("socketParams--" + socketParams);
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
				Digital_PL.mLogger.debug("result--" + result);
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
