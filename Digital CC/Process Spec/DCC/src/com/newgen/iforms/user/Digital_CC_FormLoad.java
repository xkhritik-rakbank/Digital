package com.newgen.iforms.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.Date;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.sun.javafx.collections.MappingChange.Map;

public class Digital_CC_FormLoad extends Digital_CC_Common {

	String processInstanceID = "";
	String cabinetName = "";
	String sessionId = "";
	String serverIp = "";
	String serverPort = "";
	String oldSalaryDetails = "";

	public String formLoadEvent(IFormReference iform, String controlName, String event, String data) {
		this.processInstanceID = getWorkitemName(iform);
		this.cabinetName = getCabinetName(iform);
		this.sessionId = getSessionId(iform);
		this.serverIp = iform.getServerIp();
		this.serverPort = iform.getServerPort();
		String strReturn = "";
		//Added by Kamran 09032023 For ReadyOnly //R for readonly //W for non readonly
		String isReadOnlyForm = iform.getObjGeneralData().getM_strMode();

		Digital_CC.mLogger.debug("This is DCC_FormLoad_Event" + event + " controlName :" + controlName);
		 Digital_CC.mLogger.debug("data feild value: " + data);

		String Workstep = iform.getActivityName();
		Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + iform.getActivityName()
				+ ", Workstep :" + Workstep);

		// String FIRCO_Flag = (String) iform.getValue("FIRCO_Flag");
		String docType = (String) iform.getValue("AddnalDocs_doctype");
		String decision = (String) iform.getValue("Decision");
		String declineReason = (String) iform.getValue("Decline_reason");
		String overrideSalary = (String) iform.getValue("overrideIncomeFromDectech");
		String Prospect_Creation_Date = (String) iform.getValue("Prospect_Creation_Date");
		Digital_CC.mLogger.debug(" Prospect_Creation_Date on load" + Prospect_Creation_Date);
		String Selected_Card_Type = (String) iform.getValue("Selected_Card_Type");
		Digital_CC.mLogger.debug(" Selected_Card_Type on load" + Selected_Card_Type);
		String Prospect_id = (String) iform.getValue("Prospect_id");
		Digital_CC.mLogger.debug(" Prospect_id on load" + Prospect_id);
		String FirstName = (String) iform.getValue("FirstName");
		String MiddleName = (String) iform.getValue("MiddleName");
		String LastName = (String) iform.getValue("LastName");
		String CUSTOMERNAME = "";

		if (FirstName == null) {
			FirstName = "";
		}
		if (MiddleName == null) {
			MiddleName = "";
		}
		if (LastName == null) {
			LastName = "";
		}
		CUSTOMERNAME = FirstName + " " + MiddleName + " " + LastName;
		Digital_CC.mLogger.debug(" CUSTOMERNAME on load" + CUSTOMERNAME);

		String EMPLOYER_CODE = (String) iform.getValue("employercode");
		String COMPANY_STATUS_CC = (String) iform.getValue("CompanyStatusCC");
		String COMPANY_STATUS_PL = (String) iform.getValue("CompanyStatusPL");
		Digital_CC.mLogger.debug(" overrideSalary on load" + overrideSalary);
		Digital_CC.mLogger.debug(" COMPANY_STATUS_CC on load" + COMPANY_STATUS_CC);
		Digital_CC.mLogger.debug(" COMPANY_STATUS_PL on load" + COMPANY_STATUS_PL);

		String PensionerQueryValue="";
		List<List<String>> pensionerVal = iform.getDataFromDB("select Pensioner from NG_DCC_EXTTABLE with (NOLOCK)  where Wi_Name ='"
		+ processInstanceID + "'");
		Digital_CC.mLogger.debug(" inside Pensioner value WS: " + pensionerVal);
		
		if (!pensionerVal.isEmpty()) {
			Digital_CC.mLogger.debug("Inside output_PensionerQuery: ");
			PensionerQueryValue = pensionerVal.get(0).get(0);
			Digital_CC.mLogger.debug("Pernsioner Value: "+pensionerVal);
		}
		else{
			Digital_CC.mLogger.debug("PensionerValue is empty!!"); 
		}
		
		if("Y".equalsIgnoreCase(PensionerQueryValue)){
			iform.setValue("checkbox2","true");
		}
		else{
			iform.setValue("checkbox2","false");
		}

		//end
		if ("DecisionDropDown".equals(controlName)) {
			// added by gaurav
			try {
				List lstDecisions = iform.getDataFromDB("SELECT decision FROM NG_DCC_MASTER_DECISION WITH(NOLOCK) WHERE WorkstepName='"
				+ iform.getActivityName() + "' and Is_Active='Y' ORDER BY decision ASC");
				
				Digital_CC.mLogger.debug(" inside Decision drop down WS: " + lstDecisions);
				String value = "";
				iform.clearCombo("Decision");
				for (int i = 0; i < lstDecisions.size(); i++) {
					List<String> arr1 = (List) lstDecisions.get(i);
					value = arr1.get(0);
					iform.addItemInCombo("Decision", value, value);
					strReturn = "Decision Loaded";
				}
				String Soft_Decline_flag= (String) iform.getValue("Soft_Decline_flag");
				String q_exceptions_decision= (String) iform.getValue("q_exceptions_decision");
				Digital_CC.mLogger.debug("Soft_Decline_flag " + Soft_Decline_flag);
				Digital_CC.mLogger.debug("q_exceptions_decision " + q_exceptions_decision);
				if(!"Y".equalsIgnoreCase(Soft_Decline_flag) && "Exceptions".equals(Workstep)){
					iform.removeItemFromCombo("Decision",2);
				}
				if("Refer to source".equalsIgnoreCase(q_exceptions_decision) && "Source_Refer".equals(Workstep)){
					iform.removeItemFromCombo("Decision",1);
				}else{
					iform.removeItemFromCombo("Decision",4);
				}

			} catch (Exception e) {
				Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + iform.getActivityName()
				+ ", Exception in Decision drop down load " + e.getMessage());
			}

			try {
				List declineReasons = iform.getDataFromDB(
						"SELECT DeclineReason FROM ng_dcc_master_declineReason WITH(NOLOCK) WHERE WorkstepName='"
								+ iform.getActivityName() + "' and Is_Active='Y' ORDER BY DeclineReason ASC");
				Digital_CC.mLogger.debug(" inside Decline Reason drop down WS: " + declineReasons);
				String value = "";
				iform.clearCombo("Decline_reason");
				for (int i = 0; i < declineReasons.size(); i++) {
					List<String> arr1 = (List) declineReasons.get(i);
					value = arr1.get(0);
					iform.addItemInCombo("Decline_reason", value, value);

				}

			} catch (Exception e) {
				Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + iform.getActivityName()
						+ ", Exception in Decline Reasons drop down load " + e.getMessage());
			}

		}

		else if ("AddnalDocs".equalsIgnoreCase(controlName)) {
			// added by gaurav 1708

			if (Workstep.equalsIgnoreCase("Firco")) {
				if (docType.equalsIgnoreCase("Other_Document")) {
					Digital_CC.mLogger.debug("docType : formLoad " + docType);
					iform.setStyle("AddnalDocs_remarks", "visible", "true");
					iform.setStyle("AddnalDocs_remarks", "mandatory", "true");

				} else {
					iform.setValue("AddnalDocs_remarks", "");
					iform.setStyle("AddnalDocs_remarks", "visible", "false");
					iform.setStyle("AddnalDocs_remarks", "mandatory", "false");
				}
			}
		}
		
		else if ("CustomerCallHistory".equalsIgnoreCase(controlName)) {
			// added by gaurav 2911

			if (Workstep.equalsIgnoreCase("DCC_Experience")) {
				iform.setValue("CustomerCallHistory_user", iform.getUserName());
				
			}
		}
		else if("Internal_Exposure_M".equalsIgnoreCase(controlName)){ 
			// Hritik 03.01.24 - PDSC - 1300
			iform.setStyle("table19_Product", "disable", "true");
			iform.setStyle("table19_Phase", "disable", "true");
			iform.setStyle("table19_Loan_creditLmit_AccBal", "disable", "true");
			iform.setStyle("table19_EMI_Loan", "disable", "true");
			iform.setStyle("table19_Delinquency", "disable", "true");
		}
		else if("Internal_Exposure_Add".equalsIgnoreCase(controlName)){
			// Hritik 03.01.24 - PDSC - 1300
			iform.setStyle("table19_Product", "disable", "true");
			iform.setStyle("table19_Phase", "disable", "true");
			iform.setStyle("table19_Loan_creditLmit_AccBal", "disable", "true");
			iform.setStyle("table19_EMI_Loan", "disable", "true");
			iform.setStyle("table19_Delinquency", "disable", "true");
			iform.setStyle("table19_Consider_obligation", "disable", "true");
		}

		else if ("SuppDetailsGrid".equalsIgnoreCase(controlName)) {
			// added by gaurav 1808
			if (Workstep.equalsIgnoreCase("Card_Ops")) {
				iform.setStyle("SuppDetailsGrid_NameOnCard", "disable", "true");
				iform.setStyle("SuppDetailsGrid_FirstName", "disable", "true");
				iform.setStyle("SuppDetailsGrid_MiddleName", "disable", "true");
				iform.setStyle("SuppDetailsGrid_LastName", "disable", "true");
				iform.setStyle("SuppDetailsGrid_CardReqFor", "visible", "false");
				iform.setStyle("SuppDetailsGrid_RelationType", "visible", "false");
				iform.setStyle("SuppDetailsGrid_UAEResidency", "visible", "false");
				iform.setStyle("SuppDetailsGrid_DOB", "visible", "false");
				iform.setStyle("SuppDetailsGrid_PassportNum", "visible", "false");
				iform.setStyle("SuppDetailsGrid_PassportExp", "visible", "false");
				iform.setStyle("SuppDetailsGrid_Nationality", "visible", "false");
				iform.setStyle("SuppDetailsGrid_NationalityDesc", "visible", "false");
				iform.setStyle("SuppDetailsGrid_MobNo", "visible", "false");
				iform.setStyle("SuppDetailsGrid_Email", "visible", "false");
				iform.setStyle("SuppDetailsGrid_LimitReq", "visible", "false");
				iform.setStyle("SuppDetailsGrid_EID", "visible", "false");
				iform.setStyle("SuppDetailsGrid_EIDexp", "visible", "false");
				iform.setStyle("SuppDetailsGrid_visaNum", "visible", "false");
				iform.setStyle("SuppDetailsGrid_visaExp", "visible", "false");
				iform.setStyle("SuppDetailsGrid_Gender", "visible", "false");
				iform.setStyle("SuppDetailsGrid_GenderDesc", "visible", "false");
				iform.setStyle("SuppDetailsGrid_MotherName", "visible", "false");
				iform.setStyle("SuppDetailsGrid_MaritalStatus", "visible", "false");
				iform.setStyle("SuppDetailsGrid_Profession", "visible", "false");
				iform.setStyle("SuppDetailsGrid_EmpType", "visible", "false");
				iform.setStyle("SuppDetailsGrid_EmpTypeDesc", "visible", "false");
				iform.setStyle("SuppDetailsGrid_CIF_Title", "visible", "false");
				iform.setStyle("SuppDetailsGrid_CIF_TitleDesc", "visible", "false");
				iform.setStyle("SuppDetailsGrid_CIF", "visible", "false");
			}
		}

		else if ("onLoadEnableDisable".equalsIgnoreCase(controlName)) {
			// ENC Reference
			if (!(Prospect_id == null || "".equalsIgnoreCase(Prospect_id))) {
				iform.setValue("textbox214", Prospect_id);
			}
			// AECB Pipeline grid validation
			try {
				if(iform.getDataFromGrid("AECB_Pipeline_Grid").size()>0)
					iform.clearTable("AECB_Pipeline_Grid");
				
				/* PDSC-92 
				"select CifId, AgreementId,ProviderNo,LoanType,LoanDesc,CustRoleType,Datelastupdated,TotalAmt,TotalNoOfInstalments,CreditLimit,'' as col1,NoOfDaysInPipeline,"
				+ "isnull(Consider_For_Obligations,'true') as 'Consider_For_Obligations', case when IsDuplicate= '1' then 'Y' else 'N' end as 'IsDuplicate',case when WriteoffStat= 'W' then  OutstandingAmt else '' end as 'OutstandingAmt' from ng_dcc_cust_extexpo_LoanDetails with (nolock) "
				+ "where Wi_Name  =  '" + processInstanceID + "' and LoanStat = 'Pipeline' and ProviderNo !='B01' "
				+ "union select CifId, CardEmbossNum,ProviderNo,CardType,CardTypeDesc, CustRoleType,LastUpdateDate,'' as col2,NoOfInstallments, '' as col3, TotalAmount, "
				+ "NoOfDaysInPipeLine,isnull(Consider_For_Obligations,'true') as 'Consider_For_Obligations',case when IsDuplicate= '1' then 'Y' else 'N' end as 'IsDuplicate','' as 'OutstandingAmt'  from ng_dcc_cust_extexpo_CardDetails "
				+ "with (nolock) where Wi_Name  =  '" + processInstanceID + "' and cardstatus = 'Pipeline' and ProviderNo !='B01'"; */
				
				String DBQuery4 = "select CifId, AgreementId,ProviderNo,LoanType,LoanDesc,CustRoleType,Datelastupdated,TotalAmt,TotalNoOfInstalments,CreditLimit,'' as col1,NoOfDaysInPipeline,"
					+ "isnull(Consider_For_Obligations,'true') as 'Consider_For_Obligations',case when IsDuplicate= '1' then 'Y' else 'N' end as 'IsDuplicate',case when WriteoffStat= 'W' then  OutstandingAmt else '' end as 'OutstandingAmt' from ng_dcc_cust_extexpo_LoanDetails with (nolock)"
					+ "where Wi_Name= '"+ processInstanceID +"' and LoanStat= 'Pipeline'"
					+ "union select CifId, CardEmbossNum,ProviderNo,CardType,CardTypeDesc, CustRoleType,LastUpdateDate,'' as col2,NoOfInstallments, '' as col3, TotalAmount, "
					+ "NoOfDaysInPipeLine,isnull(Consider_For_Obligations,'true') as 'Consider_For_Obligations',case when IsDuplicate= '1' then 'Y' else 'N' end as 'IsDuplicate','' as 'OutstandingAmt'  from ng_dcc_cust_extexpo_CardDetails "
					+ "with (nolock) where Wi_Name= '" + processInstanceID + "' and cardstatus='Pipeline'";
				
				Digital_CC.mLogger.debug("DBQuery4 : " + DBQuery4);
				
				List<List<String>> twoDarray = iform.getDataFromDB(DBQuery4);
				Digital_CC.mLogger.debug("twoDarray: " + twoDarray);
				if (!twoDarray.isEmpty()) {
					JSONArray jsonArray = new JSONArray();
					iform.clearTable("AECB_Pipeline_Grid");
					Digital_CC.mLogger.debug("twoDarray : " + twoDarray.size());
					for (int i=0; i < twoDarray.size(); i++) {
						Digital_CC.mLogger.debug("i : " + i);
						JSONObject obj1 = new JSONObject();
						String typeOfContract_LoanType = twoDarray.get(i).get(3);
						Digital_CC.mLogger.debug("Reason_Code : " + typeOfContract_LoanType);

						String provider_No = twoDarray.get(i).get(2);
						Digital_CC.mLogger.debug("Reason_Code : " + provider_No);

						//String phasE = "PIPELINE";
						//Changes done for phase same as of exposure.
						String phasE = twoDarray.get(i).get(4);
						Digital_CC.mLogger.debug("Reason_Code : " + provider_No);
						Digital_CC.mLogger.debug("Reason_Code : " + phasE);

						String loanType = twoDarray.get(i).get(3);
						String financeAmt = "";
						if (loanType.toUpperCase().contains("LOAN")) {
							financeAmt += twoDarray.get(i).get(7);
						} else {
							financeAmt += twoDarray.get(i).get(10);
						}
						Digital_CC.mLogger.debug("financeAmt : " + financeAmt);
						String RequestDate = twoDarray.get(i).get(6);
						Digital_CC.mLogger.debug("RequestDate : " + RequestDate);
						obj1.put("Type of Contract", typeOfContract_LoanType);
						obj1.put("Provider No", provider_No);
						obj1.put("Phase", phasE);
						obj1.put("Finance Amount", financeAmt);
						obj1.put("Request Date", RequestDate);
						
					/*	if (jsonArray.contains(obj1)) {
							continue;
						} */ // Cmmnted by Hritik -- 30.8.23 unable to see all the data present in the DB
						jsonArray.add(obj1);
					}
					Digital_CC.mLogger.debug("jsonArray : " + jsonArray);
					iform.addDataToGrid("AECB_Pipeline_Grid", jsonArray);
					
					/*for (int i = 0; i < twoDarray.size(); i++) {
						JSONObject obj1 = new JSONObject();

						String extChargeoff = twoDarray.get(i).get(14);
						Digital_CC.mLogger.debug("extChargeoff : " + extChargeoff);

						obj1.put("External Charge Off", extChargeoff);

						if (jsonArray1.contains(obj1)) {
							continue;
						}

						jsonArray1.add(obj1);
					}

					Digital_CC.mLogger.debug("jsonArray : " + jsonArray1);
					iform.addDataToGrid("DCC_AECB_Exposure_Grid", jsonArray1);*/

					for (int i = 0; i < twoDarray.size(); i++) {

						String extChargeoff = twoDarray.get(i).get(14);
						Digital_CC.mLogger.debug("extChargeoff : " + extChargeoff);
						
						if(!(extChargeoff == null || "".equalsIgnoreCase(extChargeoff))){
						iform.setTableCellValue("DCC_AECB_Exposure_Grid", i, 7, extChargeoff);
						}
					}
				}
			} catch (Exception e) {
				Digital_CC.mLogger.debug("Exception occured in fetching AECB Pipeline grid values: " + e.getMessage());
			}
			// AECB Exposure free fields
			try {
				/*String DBQuery1 = "SELECT Outstanding_Amount FROM ng_dcc_cust_external_exposure with(nolock) WHERE WI_NAME='"
						+ processInstanceID + "'";
				String extTabDataINPXML1 = Digital_CC_Common.apSelectWithColumnNames(DBQuery1, getCabinetName(iform),
						getSessionId(iform));
				Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataINPXML1);
				String extTabDataOUPXML1 = Digital_CC_Common.WFNGExecute(extTabDataINPXML1, iform.getServerIp(),
						iform.getServerPort(), 1);
				Digital_CC.mLogger.debug("extTabDataOPXML: " + extTabDataOUPXML1);

				XMLParser xmlParserDataDB1 = new XMLParser(extTabDataOUPXML1);
				String Outstanding_Amount = xmlParserDataDB1.getValueOf("Outstanding_Amount");*/

				String DBQuery2 = "SELECT Worst_Status_Last24Months,NoOf_Cheque_Return_Last3,Nof_DDES_Return_Last3Months FROM ng_dcc_cust_extexpo_Derived with(nolock) WHERE WI_NAME='"
						+ processInstanceID + "'";
				String extTabDataINPXML2 = Digital_CC_Common.apSelectWithColumnNames(DBQuery2, getCabinetName(iform),
						getSessionId(iform));
				Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataINPXML2);
				String extTabDataOUPXML2 = Digital_CC_Common.WFNGExecute(extTabDataINPXML2, iform.getServerIp(),
						iform.getServerPort(), 1);
				Digital_CC.mLogger.debug("extTabDataOPXML: " + extTabDataOUPXML2);

				XMLParser xmlParserDataDB2 = new XMLParser(extTabDataOUPXML2);
				String Worst_Status_Last24Months = xmlParserDataDB2.getValueOf("Worst_Status_Last24Months");
				String NoOf_Cheque_Return_Last3 = xmlParserDataDB2.getValueOf("NoOf_Cheque_Return_Last3");
				String Nof_DDES_Return_Last3Months = xmlParserDataDB2.getValueOf("Nof_DDES_Return_Last3Months");

				/*String DBQuery3 = "SELECT AECBHistMonthCnt,WriteoffStat FROM ng_dcc_cust_extexpo_LoanDetails with(nolock) WHERE WI_NAME='"
						+ processInstanceID + "'";
				String extTabDataINPXML3 = Digital_CC_Common.apSelectWithColumnNames(DBQuery3, getCabinetName(iform),
						getSessionId(iform));
				Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataINPXML3);
				String extTabDataOUPXML3 = Digital_CC_Common.WFNGExecute(extTabDataINPXML3, iform.getServerIp(),
						iform.getServerPort(), 1);
				Digital_CC.mLogger.debug("extTabDataOPXML: " + extTabDataOUPXML3);

				XMLParser xmlParserDataDB3 = new XMLParser(extTabDataOUPXML3);
				String AECBHistMonthCnt = xmlParserDataDB3.getValueOf("AECBHistMonthCnt");
				String WriteoffStat = xmlParserDataDB3.getValueOf("WriteoffStat");*/

				// AECBHistMonthCnt
				/*Digital_CC.mLogger.debug(" AECBHistMonthCnt on load" + AECBHistMonthCnt);
				if (!(AECBHistMonthCnt == null || "".equalsIgnoreCase(AECBHistMonthCnt))) {
					iform.setValue("AECB_history", AECBHistMonthCnt);
					Digital_CC.mLogger.debug("AECBHistMonthCnt: " + AECBHistMonthCnt);
				}*/

				// Worst_Status_Last24Months
				Digital_CC.mLogger.debug(" Worst_Status_Last24Months on load" + Worst_Status_Last24Months);
				if (!(Worst_Status_Last24Months == null || "".equalsIgnoreCase(Worst_Status_Last24Months))) {
					iform.setValue("wrost_status_date", Worst_Status_Last24Months);
					Digital_CC.mLogger.debug("Worst_Status_Last24Months: " + Worst_Status_Last24Months);
				}

				// NoOf_Cheque_Return_Last3
				String NoOf_Return_Last3 = "";
				if (!(NoOf_Cheque_Return_Last3 == null || "".equalsIgnoreCase(NoOf_Cheque_Return_Last3))) {
					NoOf_Return_Last3 = NoOf_Return_Last3 + NoOf_Cheque_Return_Last3;
					if (!(Nof_DDES_Return_Last3Months == null || "".equalsIgnoreCase(Nof_DDES_Return_Last3Months))) {
						NoOf_Return_Last3 = NoOf_Return_Last3 + "," + Nof_DDES_Return_Last3Months;
						Digital_CC.mLogger.debug(" NoOf_Return_Last3 on load" + NoOf_Return_Last3);
					}
				}
				if (!(NoOf_Return_Last3 == null || "".equalsIgnoreCase(NoOf_Return_Last3))) {
					iform.setValue("cheque_dds_return", NoOf_Return_Last3);
					Digital_CC.mLogger.debug("NoOf_Return_Last3: " + NoOf_Return_Last3);
				}

				// WriteoffStat, externalChargeOff
				/*if (WriteoffStat.equalsIgnoreCase("W")) {
					if (!(Outstanding_Amount == null || "".equalsIgnoreCase(Outstanding_Amount))) {
						iform.setValue("external_charge_of", Outstanding_Amount);
						Digital_CC.mLogger.debug("Outstanding_Amount: " + Outstanding_Amount);
					}
				}*/

			} catch (Exception e) {
				Digital_CC.mLogger.debug("Exception occured in fetching AECB Exposure free fields: " + e.getMessage());
			}

			// non stp reason
			String Non_STP_reason = (String) iform.getValue("Non_STP_reason");
			Digital_CC.mLogger.debug(" Non_STP_reason on load" + Non_STP_reason);
			if (!(Non_STP_reason == null || "".equalsIgnoreCase(Non_STP_reason))) {
				if (Non_STP_reason.contains("~")) {
					Non_STP_reason = Non_STP_reason.replace("~", ",");
				}
				iform.setValue("Non_STP_reason", Non_STP_reason);
				Digital_CC.mLogger.debug(" Non_STP_reason on load after replacing ~ " + Non_STP_reason);
			}
			// Decision and Remarks
			if (Workstep.equalsIgnoreCase("System_Integration") || Workstep.equalsIgnoreCase("Sys_Int_EFMS")
					|| Workstep.equalsIgnoreCase("Sys_FTS_WI_Update") || Workstep.equalsIgnoreCase("Sys_DEH_Notify")
					|| Workstep.equalsIgnoreCase("Sys_Couried_WI_Update") || Workstep.equalsIgnoreCase("System_Wi_hold")
					|| Workstep.equalsIgnoreCase("Sys_PrimeAWB_Gen") || Workstep.equalsIgnoreCase("Sys_CardBalClsBlk")
					|| Workstep.equalsIgnoreCase("Exit") || Workstep.equalsIgnoreCase("Reject")
					|| Workstep.equalsIgnoreCase("Doc_Reverify") || Workstep.equalsIgnoreCase("Attach_Document")
					|| Workstep.equalsIgnoreCase("Sys_Update_Asign_CIF")
					|| Workstep.equalsIgnoreCase("Sys_Update_Asign_CIF") //|| Workstep.equalsIgnoreCase("Sys_WI_Update")
					|| Workstep.equalsIgnoreCase("Sys_CP_WI_Update")
					|| Workstep.equalsIgnoreCase("Sys_Limit_Increase")) {
				iform.setStyle("Decision", "disable", "true");
				iform.setStyle("Remarks", "disable", "true");
			}
			// for dectech button visibility
			if ("Exceptions".equalsIgnoreCase(Workstep.trim()) ) {
				iform.setStyle("Fetch_Manual_Dectech", "disable", "false");
				iform.setStyle("DeviationDescription", "visible", "false");
				iform.setStyle("LOB", "disable", "false");
			} else {
				iform.setStyle("Fetch_Manual_Dectech", "disable", "true");
				iform.setStyle("DeviationDescription", "visible", "false");
				iform.setStyle("LOB", "disable", "true");
			}
			// for aecb button visibility
			// Updated by Kamran 09032023 for REadyonly
			if ("Exceptions".equalsIgnoreCase(Workstep.trim()) || Workstep.equalsIgnoreCase("Sys_Couried_WI_Update")
					|| Workstep.equalsIgnoreCase("Sys_PrimeAWB_Gen") || Workstep.equalsIgnoreCase("Sys_CardBalClsBlk")
					|| Workstep.equalsIgnoreCase("Sys_Update_Asign_CIF")
					|| Workstep.equalsIgnoreCase("Sys_Update_Asign_CIF")
					|| Workstep.equalsIgnoreCase("Sys_CP_WI_Update") || Workstep.equalsIgnoreCase("Card_Ops")
					|| Workstep.equalsIgnoreCase("Sys_Limit_Increase") || isReadOnlyForm.equalsIgnoreCase("R")) {
				// dectech and aecb button
				// iform.setStyle("Fetch_Manual_Dectech", "visible", "true");
				iform.setStyle("Fetch_AECB_Report", "disable", "false");
			} else {
				iform.setStyle("Fetch_AECB_Report", "disable", "true");
			}
			
			// Old Salary Details added to table
			try {

				List<List<String>> twoDarray = iform.getDataFromDB(
						"select count(net_salary1) as rowCountOldSalary from NG_DCC_GR_NetSalaryDetails with (NOLOCK)  where Wi_Name ='"
								+ processInstanceID + "'");
				Digital_CC.mLogger.debug("twoDarray: " + twoDarray);
				String rowCountOldSalary = twoDarray.get(0).get(0);
				Digital_CC.mLogger.debug("rowCountOldSalary: " + rowCountOldSalary);
				if (rowCountOldSalary.equalsIgnoreCase("0")) {

					String Query_Old_Salary = "select net_salary1,net_salary2,net_salary3 from NG_DCC_EXTTABLE with (NOLOCK)  where Wi_Name ='"
							+ processInstanceID + "'";
					Digital_CC.mLogger.debug("Query_Old_Salary : " + Query_Old_Salary);
					String extTabDataIPXML = Digital_CC_Common.apSelectWithColumnNames(Query_Old_Salary, cabinetName,
							sessionId);
					Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
					String extTabDataOPXML = Digital_CC_Common.WFNGExecute(extTabDataIPXML, serverIp, serverPort, 1);
					Digital_CC.mLogger.debug("extTabDataOPXML2: " + extTabDataOPXML);

					XMLParser xmlParserData = new XMLParser(extTabDataOPXML);

					String net_salary1 = xmlParserData.getValueOf("net_salary1");
					Digital_CC.mLogger.debug("net_salary1: " + net_salary1);

					String net_salary2 = xmlParserData.getValueOf("net_salary2");
					Digital_CC.mLogger.debug("net_salary2: " + net_salary2);

					String net_salary3 = xmlParserData.getValueOf("net_salary3");
					Digital_CC.mLogger.debug("net_salary3: " + net_salary3);

					if ((net_salary1 != null && !"".equalsIgnoreCase(net_salary1))
							|| (net_salary2 != null && !"".equalsIgnoreCase(net_salary2))
							|| (net_salary3 != null && !"".equalsIgnoreCase(net_salary3))) {

//						String columnNames = "Net_Salary1,Net_Salary2,Wi_Name,Net_Salary3";
//						String columnValues = "'" + net_salary1 + "','" + net_salary2 + "','" + processInstanceID
//								+ "','" + net_salary3 + "'";
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						
						Date current_date = new Date();
						String formattedActionDatetime=dateFormat.format(current_date);
						
						String columnNames = "Net_Salary1,Net_Salary2,Wi_Name,Net_Salary3,Workstep,insertion_date_time";
						String columnValues = "'" + net_salary1 + "','" + net_salary2 + "','" + processInstanceID
								+ "','" + net_salary3 + "','" + Workstep + "','" + formattedActionDatetime + "'";

//
						String apInsertInputXML = Digital_CC_Common.apInsert(cabinetName, sessionId, columnNames,
								columnValues, "NG_DCC_GR_NetSalaryDetails");
						Digital_CC.mLogger.debug("APInsertInputXML: " + apInsertInputXML);

						String apInsertOutputXML = Digital_CC_Common.WFNGExecute(apInsertInputXML, serverIp, serverPort,
								1);
						Digital_CC.mLogger.debug("APInsertOutputXML: " + apInsertInputXML);

						XMLParser xmlParserAPInsert = new XMLParser(apInsertOutputXML);
						String apInsertMaincode = xmlParserAPInsert.getValueOf("MainCode");
						Digital_CC.mLogger.debug("Status of apInsertMaincode  " + apInsertMaincode);

						if (apInsertMaincode.equalsIgnoreCase("0")) {
							Digital_CC.mLogger.debug("ApInsert successful: " + apInsertMaincode);
						} else {
							Digital_CC.mLogger.debug("ApInsert failed: " + apInsertMaincode);
						}
					}
				}

			} catch (Exception ex) {
				Digital_CC.mLogger.debug("Exception " + ex);
			}
			// Income at rule engine
			String FinalTAI = (String) iform.getValue("FinalTAI");
			Digital_CC.mLogger.debug(" FinalTAI on load" + FinalTAI);
			if (!(FinalTAI == null || "".equalsIgnoreCase(FinalTAI))) {
				iform.setValue("INCOME_AT_RULE_ENGINE", FinalTAI);
			}
			// Remarks
			iform.setValue("Remarks", "");
			// customer Name
			String cust_name = (String) iform.getValue("CUSTOMERNAME");
			if (cust_name == null || "".equalsIgnoreCase(cust_name)) {
				iform.setValue("CUSTOMERNAME", CUSTOMERNAME);
			}
			// card application date
			if (!(Prospect_Creation_Date == null || "".equalsIgnoreCase(Prospect_Creation_Date))) {
				iform.setValue("card_application_date", Prospect_Creation_Date);
			}
			// Applied_card
			if (!(Selected_Card_Type == null || "".equalsIgnoreCase(Selected_Card_Type))) {
				iform.setValue("Applied_card", Selected_Card_Type);
			}
			iform.setStyle("Decision", "disable", "false");
			iform.setStyle("Decision", "mandatory", "true");
			iform.setStyle("Remarks", "disable", "false");
			if ("Exceptions".equalsIgnoreCase(Workstep.trim()) && decision.equalsIgnoreCase("Reject")) {
				iform.setStyle("Decline_reason", "visible", "true");
				iform.setStyle("Decline_reason", "disable", "false");
				iform.setStyle("Decline_reason", "mandatory", "true");
			} else {
				iform.setStyle("Decline_reason", "visible", "false");
				iform.setStyle("Decline_reason", "disable", "true");
				iform.setStyle("Decline_reason", "mandatory", "false");

			}
			if (declineReason.equalsIgnoreCase("Others")) {
				iform.setStyle("Remarks", "mandatory", "true");
			} else {
				iform.setStyle("Remarks", "mandatory", "false");
			}
			// Aloc Dtls table data
			if (COMPANY_STATUS_CC == null || COMPANY_STATUS_CC == "" || COMPANY_STATUS_PL == null
					|| COMPANY_STATUS_PL == "") {
				String query = "select COMPANY_STATUS_CC,COMPANY_STATUS_PL, EMPLOYER_CATEGORY_PL, ALOC_REMARKS_CC, ALOC_REMARKS_PL from NG_RLOS_ALOC_OFFLINE_DATA WITH(nolock) where EMPLOYER_CODE=main_Employer_code and main_Employer_code = '"
						+ EMPLOYER_CODE + "'";
				List<List<String>> AlocDetails = iform.getDataFromDB(query);
				Digital_CC.mLogger.debug(" inside AlocDetails arraylist: " + AlocDetails);
				if (!AlocDetails.isEmpty()) {
					COMPANY_STATUS_CC = AlocDetails.get(0).get(0);
					Digital_CC.mLogger.debug(" inside AlocDetails arraylist COMPANY_STATUS_CC: " + COMPANY_STATUS_CC);
					iform.setValue("CompanyStatusCC", COMPANY_STATUS_CC);
					COMPANY_STATUS_PL = AlocDetails.get(0).get(1);
					iform.setValue("CompanyStatusPL", COMPANY_STATUS_PL);
					String EMPLOYER_CATEGORY_PL = AlocDetails.get(0).get(2);
					iform.setValue("EmployerCategoryPL", EMPLOYER_CATEGORY_PL);
					String ALOC_REMARKS_CC = AlocDetails.get(0).get(3);
					iform.setValue("CC_ALOC_remarks", ALOC_REMARKS_CC);
					String ALOC_REMARKS_PL = AlocDetails.get(0).get(4);
					iform.setValue("PL_ALOC_remarks", ALOC_REMARKS_PL);
				}
			}
			// 1708 added by gaurav override_netSalary
			iform.setValue("overrideIncomeFromDectech", "false");
			if ("Exceptions".equalsIgnoreCase(Workstep.trim())) {
				Digital_CC.mLogger.debug(" inside form load exceptions-: " + Workstep);
				// sections
				iform.setStyle("DCC_PersonalDetails", "visible", "false");
				iform.setStyle("DCC_SuppCardDetails", "visible", "false");
				iform.setStyle("DCC_LiabilityAddition", "visible", "false");
				iform.setStyle("Additional_Documents_Required", "visible", "false");
				iform.setStyle("DocTypeSection", "visible", "false");
				iform.setStyle("SalaryDocReq", "visible", "false");
				// free field
				iform.setStyle("overrideIncomeFromDectech", "disable", "false");
				iform.setStyle("EnsureDelegationCheck", "visible", "true");
				iform.setStyle("EnsureDelegationCheck", "disable", "false");
				iform.setStyle("Old_Salary", "disable", "false");
				iform.setStyle("button6", "disable", "false");
				// GenerateCam button at cad
				iform.setStyle("GenerateCam", "visible", "true");
				iform.setStyle("GenerateCam", "disable", "false");
				//added by om on 19/10/22
				iform.setStyle("Final_Limit", "visible", "true");
				//vinayak
				iform.setStyle("Underwriting_Limit", "visible", "true");
				iform.setStyle("Underwriting_Limit", "disable", "false");
				//
				String final_limit_val=(String) iform.getValue("Final_Limit");
				String Underwriting_Limit_val=(String) iform.getValue("Underwriting_Limit");
				if(Underwriting_Limit_val==null || Underwriting_Limit_val.equalsIgnoreCase("")|| Underwriting_Limit_val.equalsIgnoreCase("null")){
				iform.setValue("Underwriting_Limit", final_limit_val);
				}
				//vinayak 09-07 
				List Is_Document_Decrypted_Qry = iform.getDataFromDB("select Is_Document_Decrypted from NG_DCC_EXTTABLE with (NOLOCK)  WHERE Wi_Name='"+getWorkitemName(iform)+"'");
				String Is_Document_Decrypted="";
				for(int i=0;i<Is_Document_Decrypted_Qry.size();i++)
				{
					List<String> arr1=(List)Is_Document_Decrypted_Qry.get(i);
					Is_Document_Decrypted=arr1.get(0);
					Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", value : "+Is_Document_Decrypted);
				}
				if(Is_Document_Decrypted.equalsIgnoreCase("N")){
					//iform.setStyle("getDocPassword", "visible", "true");
					//iform.setStyle("getDocPassword", "enable", "true");
				}
				else{
					iform.setStyle("getDocPassword", "visible", "false");
					iform.setStyle("getDocPassword", "enable", "false");
				}
				
				
				//Added by Kamran for EFR 10052023
				List EFR_NSTP_Qry = iform.getDataFromDB("select EFR_NSTP from NG_DCC_EXTTABLE with (NOLOCK)  WHERE Wi_Name='"+getWorkitemName(iform)+"'");
				String EFR_NSTP_Flag="";
				for(int i=0;i<EFR_NSTP_Qry.size();i++)
				{
					List<String> arr1=(List)EFR_NSTP_Qry.get(i);
					EFR_NSTP_Flag=arr1.get(0);
					Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", value : "+EFR_NSTP_Flag);
				}
				
				if("Y".equalsIgnoreCase(EFR_NSTP_Flag)){
					iform.setStyle("DCC_PersonalDetails", "visible", "true");
					iform.setStyle("FirstName", "disable", "false");
					iform.setStyle("MiddleName", "disable", "false");
					iform.setStyle("LastName", "disable", "false");
					iform.setStyle("FirstName", "maxlength", "30");
					iform.setStyle("MiddleName", "maxlength", "30");
					iform.setStyle("LastName", "maxlength", "30");
				}
				
				else if("N".equalsIgnoreCase(EFR_NSTP_Flag)){
					iform.setStyle("DCC_PersonalDetails", "visible", "false");
					iform.setStyle("FirstName", "disable", "true");
					iform.setStyle("MiddleName", "disable", "true");
					iform.setStyle("LastName", "disable", "true");
					iform.setStyle("FirstName", "maxlength", "80");
					iform.setStyle("MiddleName", "maxlength", "80");
					iform.setStyle("LastName", "maxlength", "80");
				}
				
				// Hritik 03.01.24 PDSC-1300
				String NTB=(String) iform.getValue("NTB");
				Digital_CC.mLogger.debug("Exceptions WS ETB " +NTB);
				if("false".equalsIgnoreCase(NTB)){
					iform.setStyle("DCC_Internal_Exposure", "visible", "true");
					iform.setStyle("Internal_Exposure", "visible", "true");
					iform.setStyle("DCC_Internal_Exposure", "disable", "false");
					iform.setStyle("Internal_Exposure", "disable", "false");
					iform.setStyle("Active_card", "visible", "true");
					iform.setStyle("Active_card", "disable", "true");
				}
				else{
					iform.setStyle("DCC_Internal_Exposure", "visible", "true");
					iform.setStyle("Internal_Exposure", "visible", "true");
					iform.setStyle("DCC_Internal_Exposure", "disable", "true");
					iform.setStyle("Internal_Exposure", "disable", "true");
					iform.setStyle("Active_cards", "visible", "false");
					iform.setStyle("Active_card", "visible", "false");
					iform.setStyle("Active_card", "disable", "true");
				}
			}
				// added by kamran on 09/01/23 - For Pensioner checkbox
				
			else if (Workstep.equalsIgnoreCase("System_Integration") || Workstep.equalsIgnoreCase("Sys_Int_EFMS")
					|| Workstep.equalsIgnoreCase("Sys_FTS_WI_Update") || Workstep.equalsIgnoreCase("Sys_DEH_Notify")
					|| Workstep.equalsIgnoreCase("Sys_Error_Handling")
					|| Workstep.equalsIgnoreCase("Sys_Couried_WI_Update") || Workstep.equalsIgnoreCase("System_Wi_hold")
					|| Workstep.equalsIgnoreCase("Sys_PrimeAWB_Gen") || Workstep.equalsIgnoreCase("Sys_CardBalClsBlk")
					|| Workstep.equalsIgnoreCase("Exit") || Workstep.equalsIgnoreCase("Reject")
					|| Workstep.equalsIgnoreCase("Doc_Reverify") || Workstep.equalsIgnoreCase("Attach_Document")
					|| Workstep.equalsIgnoreCase("Sys_Update_Asign_CIF")
					|| Workstep.equalsIgnoreCase("Sys_Update_Asign_CIF") //|| Workstep.equalsIgnoreCase("Sys_WI_Update")
					|| Workstep.equalsIgnoreCase("Sys_CP_WI_Update")
					|| Workstep.equalsIgnoreCase("Sys_Limit_Increase")) {
				// sections
				iform.setStyle("DCC_PersonalDetails", "visible", "false");
				iform.setStyle("DCC_SuppCardDetails", "visible", "false");
				iform.setStyle("DCC_LiabilityAddition", "visible", "false");
				iform.setStyle("Additional_Documents_Required", "visible", "false");
				iform.setStyle("DocTypeSection", "visible", "false");
				iform.setStyle("SalaryDocReq", "visible", "false");
				iform.setStyle("Active_cards", "visible", "false");
				// Hritik 03.01.24 PDSC-1300
				iform.setStyle("DCC_Internal_Exposure", "visible", "true");
				iform.setStyle("Internal_Exposure", "visible", "true");
				iform.setStyle("DCC_Internal_Exposure", "disable", "true");
				iform.setStyle("Internal_Exposure", "disable", "true");
				iform.setStyle("Active_cards", "visible", "false");
			}
			
			//Added by kamran 10012023
			else if (Workstep.equalsIgnoreCase("Exit")){
				iform.setStyle("Final_Limit", "visible", "true");	
				// sections
				iform.setStyle("DCC_PersonalDetails", "visible", "false");
				iform.setStyle("DCC_SuppCardDetails", "visible", "false");
				iform.setStyle("DCC_LiabilityAddition", "visible", "false");
				iform.setStyle("Additional_Documents_Required", "visible", "false");
				iform.setStyle("DocTypeSection", "visible", "false");
				iform.setStyle("SalaryDocReq", "visible", "false");
				
				// Hritik 03.01.24 PDSC-1300
				iform.setStyle("DCC_Internal_Exposure", "visible", "true");
				iform.setStyle("Internal_Exposure", "visible", "true");
				iform.setStyle("DCC_Internal_Exposure", "disable", "true");
				iform.setStyle("Internal_Exposure", "disable", "true");
				
				iform.setStyle("Active_cards", "visible", "false");
			}
			
			//added by gaurav 2911
			else if (Workstep.equalsIgnoreCase("Card_Ops")) {
				
				if (decision.equalsIgnoreCase("Reschedule")) {
					iform.setStyle("DocTypeSection", "visible", "true");
				} else {
					iform.setStyle("DocTypeSection", "visible", "false");
				}
				
				// generated document list
				List<List<String>> twoDarray2 = iform.getDataFromDB("select genrateddocumentlist from NG_DCC_EXTTABLE with (NOLOCK)  where Wi_Name ='"
						+ processInstanceID + "'");
				Digital_CC.mLogger.debug("twoDarray2: " + twoDarray2);
				String genratedDocList = twoDarray2.get(0).get(0).replaceAll(",", "\n");
				Digital_CC.mLogger.debug("genratedDocList: " + genratedDocList);
				if (!(genratedDocList == null || "".equalsIgnoreCase(genratedDocList))) {
					iform.setValue("textarea19", genratedDocList);
				}
				//added by Om on 16/12/2022
					List docReschueledlist = iform.getDataFromDB("select DISTINCT(name) from pdbdocument where DocumentIndex in "
							+ "(select DocumentIndex from PDBDocumentContent where ParentFolderIndex = (select itemindex from ng_dcc_exttable where "
							+ "Wi_Name='"+ processInstanceID + "')) "+
							" AND name in ('W8','W9','MRBH_Agency_Agreement',"
							+ "'Customer_Consent_Form_Islamic-Arabic','Customer_Consent_Form_Islamic-English',"
							+ "'W-9_Form','W-8_Form','Security_Cheque','Customer_Consent_Form')");
					
					Digital_CC.mLogger.debug(" inside Reschedule Doc list : " + docReschueledlist);
					String value = "";
					iform.clearCombo("DocumentTypeReq");
					for (int i = 0; i < docReschueledlist.size(); i++) {
						List<String> arr1 = (List) docReschueledlist.get(i);
						value = arr1.get(0);
						iform.addItemInCombo("Q_GR_DocumentName", value, value);
						strReturn = "Document Loaded";
					}

				//till here
				// added by gaurav
				// sections
				iform.setStyle("DCC_PersonalDetails", "visible", "false");
				iform.setStyle("DCC_EmploymentDetails", "visible", "false");
				iform.setStyle("DCC_BankingDetails", "visible", "false");
				iform.setStyle("DCC_RunPolicy", "visible", "false");
				iform.setStyle("DCC_SuppCardDetails", "visible", "false");
				iform.setStyle("Additional_Documents_Required", "visible", "false");
				iform.setStyle("DCC_AECB_Exposure", "visible", "false");
				iform.setStyle("DCC_LiabilityAddition", "visible", "false");
				iform.setStyle("DCC_AECBPipelines", "visible", "false");
				iform.setStyle("DCC_AdditionofChecque", "visible", "false");
				// free fields
				iform.setStyle("Final_Limit", "visible", "true");
				iform.setStyle("Virtual_Card_Limit", "visible", "true");
				iform.setStyle("Card_Number", "visible", "true");
				iform.setStyle("ECRN", "visible", "true");
				iform.setStyle("CRN", "visible", "true");
				iform.setStyle("Dectech_Decision", "visible", "false");
				iform.setStyle("delegation_authority", "visible", "false");
				iform.setStyle("Non_STP_reason", "visible", "false");
				iform.setStyle("Aecb_score", "visible", "false");
				iform.setStyle("Score_range", "visible", "false");
				iform.setStyle("deviation_description", "visible", "false");
				iform.setStyle("Underwriting_decision", "visible", "false");
				iform.setStyle("bureau_reference_number", "visible", "false");
				iform.setStyle("Expense1", "visible", "false");
				iform.setStyle("Expense2", "visible", "false");
				iform.setStyle("Expense3", "visible", "false");
				iform.setStyle("Expense4", "visible", "false");
				iform.setStyle("Expense5", "visible", "false");
				iform.setStyle("Expense6", "visible", "false");
				iform.setStyle("Credit_Shield_Flag", "visible", "false");
				iform.setStyle("RAK_Protect_Flag", "visible", "false");
				iform.setStyle("Self_Supp_Card_Limit", "visible", "false");
				iform.setStyle("Dependents", "visible", "false");
				iform.setStyle("RM_Code", "visible", "true");
				iform.setStyle("textarea19", "visible", "true");
				// Supp Card Details Grid
				iform.setColumnVisible("SuppDetailsGrid", "1", false);
				iform.setColumnVisible("SuppDetailsGrid", "2", false);
				iform.setColumnVisible("SuppDetailsGrid", "3", false);
				iform.setColumnVisible("SuppDetailsGrid", "7", false);
				iform.setColumnVisible("SuppDetailsGrid", "8", false);
				iform.setColumnVisible("SuppDetailsGrid", "9", false);
				iform.setColumnVisible("SuppDetailsGrid", "10", false);
				iform.setColumnVisible("SuppDetailsGrid", "11", false);
				iform.setColumnVisible("SuppDetailsGrid", "12", false);
				iform.setColumnVisible("SuppDetailsGrid", "13", false);
				iform.setColumnVisible("SuppDetailsGrid", "14", false);
				iform.setColumnVisible("SuppDetailsGrid", "15", false);
				iform.setColumnVisible("SuppDetailsGrid", "16", false);
				iform.setColumnVisible("SuppDetailsGrid", "17", false);
				iform.setColumnVisible("SuppDetailsGrid", "18", false);
				iform.setColumnVisible("SuppDetailsGrid", "19", false);
				iform.setColumnVisible("SuppDetailsGrid", "20", false);
				iform.setColumnVisible("SuppDetailsGrid", "21", false);
				iform.setColumnVisible("SuppDetailsGrid", "22", false);
				iform.setColumnVisible("SuppDetailsGrid", "23", false);
				iform.setColumnVisible("SuppDetailsGrid", "24", false);
				iform.setColumnVisible("SuppDetailsGrid", "25", false);
				iform.setColumnVisible("SuppDetailsGrid", "26", false);
				iform.setColumnVisible("SuppDetailsGrid", "27", false);
				iform.setColumnVisible("SuppDetailsGrid", "28", false);
				//added by om.tiwari on 20/02/23 for PDSC-275
				String isNameAmended = (String) iform.getValue("isNameAmended");
				Digital_CC.mLogger.debug("isNameAmended value:-"+isNameAmended);
				if("true".equalsIgnoreCase(isNameAmended.trim()))
				{
					String currName = ((String) iform.getValue("CUSTOMERNAME")).trim();
					Digital_CC.mLogger.debug("currName value:-"+currName);
					if(currName!=null && currName.length()>30)
					{
						currName=currName.substring(30);
					}
					String amenededName = ((String) iform.getValue("AmendedCustName")).trim();
					if("".equalsIgnoreCase(amenededName)|| amenededName==null)
					{
						iform.setValue("AmendedCustName", currName);
					}
					iform.setStyle("isNameAmended", "visible", "true");
					iform.setStyle("isNameAmended", "disable", "false");
					iform.setStyle("AmendedCustName", "visible", "true");
					iform.setStyle("AmendedCustName", "disable", "false");
					iform.setStyle("AmendedCustName", "mandatory", "true");
				}
				//till here
				// Hritik 03.01.24 PDSC-1300
				iform.setStyle("DCC_Internal_Exposure", "visible", "true");
				iform.setStyle("Internal_Exposure", "visible", "true");
				iform.setStyle("DCC_Internal_Exposure", "disable", "true");
				iform.setStyle("Internal_Exposure", "disable", "true");
			}
			
			//2703 new queue kamran
			else if (Workstep.equalsIgnoreCase("Salary_Doc_Required")) {
				iform.setStyle("SalaryDocReq", "visible", "true");
				iform.setStyle("SalaryDocReq", "disable", "true");
			}
			//

			else if (Workstep.equalsIgnoreCase("DCC_Experience")) {
				/*//KAmran 130123 Commented below for showing DocType always in DCC Exp
				if (decision.equalsIgnoreCase("Rescheduled")) {
					iform.setStyle("DocTypeSection", "visible", "true");
				} else {
					iform.setStyle("DocTypeSection", "visible", "false");
				}*/
				// generated document list
				//KAmran 130123 Added below for showing DocType always in DCC Exp
				iform.setStyle("DocTypeSection", "visible", "true");
				iform.setStyle("DocTypeSection", "disable", "true");
				//End
				List<List<String>> twoDarray2 = iform.getDataFromDB(
						"select genrateddocumentlist from NG_DCC_EXTTABLE with (NOLOCK)  where Wi_Name ='"
								+ processInstanceID + "'");
				Digital_CC.mLogger.debug("twoDarray2: " + twoDarray2);
				String genratedDocList = twoDarray2.get(0).get(0).replaceAll(",", "\n");
				Digital_CC.mLogger.debug("genratedDocList: " + genratedDocList);
				if (!(genratedDocList == null || "".equalsIgnoreCase(genratedDocList))) {
					iform.setValue("textarea19", genratedDocList);
				}
				// added by gaurav
				// sections
				iform.setStyle("DCC_PersonalDetails", "visible", "false");
				iform.setStyle("DCC_EmploymentDetails", "visible", "false");
				iform.setStyle("DCC_BankingDetails", "visible", "false");
				iform.setStyle("DCC_RunPolicy", "visible", "false");
				iform.setStyle("DCC_SuppCardDetails", "visible", "false");
				iform.setStyle("Additional_Documents_Required", "visible", "false");
				iform.setStyle("DCC_AECB_Exposure", "visible", "false");
				iform.setStyle("DCC_LiabilityAddition", "visible", "false");
				iform.setStyle("DCC_AECBPipelines", "visible", "false");
				iform.setStyle("DCC_AdditionofChecque", "visible", "false");
				iform.setStyle("CustomerCallHistory", "visible", "true");
				iform.setStyle("CustomerCallHistory", "disable", "false");
				// free fields
				iform.setStyle("Final_Limit", "visible", "true");
				iform.setStyle("Virtual_Card_Limit", "visible", "true");
				iform.setStyle("Card_Number", "visible", "true");
				iform.setStyle("ECRN", "visible", "true");
				iform.setStyle("CRN", "visible", "true");
				iform.setStyle("Dectech_Decision", "visible", "false");
				iform.setStyle("delegation_authority", "visible", "false");
				iform.setStyle("Non_STP_reason", "visible", "false");
				iform.setStyle("Aecb_score", "visible", "false");
				iform.setStyle("Score_range", "visible", "false");
				iform.setStyle("deviation_description", "visible", "false");
				iform.setStyle("Underwriting_decision", "visible", "false");
				iform.setStyle("bureau_reference_number", "visible", "false");
				iform.setStyle("Expense1", "visible", "false");
				iform.setStyle("Expense2", "visible", "false");
				iform.setStyle("Expense3", "visible", "false");
				iform.setStyle("Expense4", "visible", "false");
				iform.setStyle("Expense5", "visible", "false");
				iform.setStyle("Expense6", "visible", "false");
				iform.setStyle("Credit_Shield_Flag", "visible", "false");
				iform.setStyle("RAK_Protect_Flag", "visible", "false");
				iform.setStyle("Self_Supp_Card_Limit", "visible", "false");
				iform.setStyle("Dependents", "visible", "false");
				iform.setStyle("textarea19", "visible", "true");
				// Supp Card Details Grid
				iform.setColumnVisible("SuppDetailsGrid", "1", false);
				iform.setColumnVisible("SuppDetailsGrid", "2", false);
				iform.setColumnVisible("SuppDetailsGrid", "3", false);
				iform.setColumnVisible("SuppDetailsGrid", "7", false);
				iform.setColumnVisible("SuppDetailsGrid", "8", false);
				iform.setColumnVisible("SuppDetailsGrid", "9", false);
				iform.setColumnVisible("SuppDetailsGrid", "10", false);
				iform.setColumnVisible("SuppDetailsGrid", "11", false);
				iform.setColumnVisible("SuppDetailsGrid", "12", false);
				iform.setColumnVisible("SuppDetailsGrid", "13", false);
				iform.setColumnVisible("SuppDetailsGrid", "14", false);
				iform.setColumnVisible("SuppDetailsGrid", "15", false);
				iform.setColumnVisible("SuppDetailsGrid", "16", false);
				iform.setColumnVisible("SuppDetailsGrid", "17", false);
				iform.setColumnVisible("SuppDetailsGrid", "18", false);
				iform.setColumnVisible("SuppDetailsGrid", "19", false);
				iform.setColumnVisible("SuppDetailsGrid", "20", false);
				iform.setColumnVisible("SuppDetailsGrid", "21", false);
				iform.setColumnVisible("SuppDetailsGrid", "22", false);
				iform.setColumnVisible("SuppDetailsGrid", "23", false);
				iform.setColumnVisible("SuppDetailsGrid", "24", false);
				iform.setColumnVisible("SuppDetailsGrid", "25", false);
				iform.setColumnVisible("SuppDetailsGrid", "26", false);
				iform.setColumnVisible("SuppDetailsGrid", "27", false);
				iform.setColumnVisible("SuppDetailsGrid", "28", false);
				//customercallHistory grid field
				iform.setColumnDisable("CustomerCallHistory", "2", true);
				iform.setStyle("CustomerCallHistory_user", "disable", "true");
				//added by om.tiwari on 20/02/23 for PDSC-275
				String isNameAmended = (String) iform.getValue("isNameAmended");
				Digital_CC.mLogger.debug("isNameAmended value:-"+isNameAmended);
				if("true".equalsIgnoreCase(isNameAmended.trim()))
				{
					iform.setStyle("isNameAmended", "visible", "true");
					iform.setStyle("isNameAmended", "disable", "true");
					iform.setStyle("AmendedCustName", "visible", "true");
					iform.setStyle("AmendedCustName", "disable", "true");
				}
				//till here
				// Hritik 03.01.24 PDSC-1300
				iform.setStyle("DCC_Internal_Exposure", "visible", "true");
				iform.setStyle("Internal_Exposure", "visible", "true");
				iform.setStyle("DCC_Internal_Exposure", "disable", "true");
				iform.setStyle("Internal_Exposure", "disable", "true");

			} else if (Workstep.equalsIgnoreCase("Firco")) {
				// added by gaurav
				// sections
				iform.setStyle("DCC_PersonalDetails", "visible", "false");
				iform.setStyle("DCC_EmploymentDetails", "visible", "false");
				iform.setStyle("DCC_BankingDetails", "visible", "false");
				iform.setStyle("DCC_RunPolicy", "visible", "false");
				iform.setStyle("DCC_SuppCardDetails", "visible", "false");
				iform.setStyle("DCC_AECB_Exposure", "visible", "false");
				iform.setStyle("DCC_LiabilityAddition", "visible", "false");
				iform.setStyle("DCC_AECBPipelines", "visible", "false");
				iform.setStyle("DCC_AdditionofChecque", "visible", "false");
				iform.setStyle("DocTypeSection", "visible", "false");
				iform.setStyle("SalaryDocReq", "visible", "false");
				// decision section fields
				iform.setStyle("Dectech_Decision", "visible", "false");
				iform.setStyle("delegation_authority", "visible", "false");
				iform.setStyle("Non_STP_reason", "visible", "false");
				iform.setStyle("Aecb_score", "visible", "false");
				iform.setStyle("Score_range", "visible", "false");
				iform.setStyle("deviation_description", "visible", "false");
				iform.setStyle("Underwriting_decision", "visible", "false");
				iform.setStyle("bureau_reference_number", "visible", "false");
				iform.setStyle("LOB", "disable", "true");
				
				// Hritik 03.01.24 PDSC-1300
				iform.setStyle("DCC_Internal_Exposure", "visible", "true");
				iform.setStyle("Internal_Exposure", "visible", "true");
				iform.setStyle("DCC_Internal_Exposure", "disable", "true");
				iform.setStyle("Internal_Exposure", "disable", "true");
			}
			
			//Added by Kamran 02052023 -RM Access to Upload FIRCO doc at WI Update --Sys_WI_Update
			else if (Workstep.equalsIgnoreCase("Sys_WI_Update")) {
				iform.setStyle("DCC_PersonalDetails", "visible", "false");
				iform.setStyle("DCC_EmploymentDetails", "visible", "false");
				iform.setStyle("DCC_BankingDetails", "visible", "false");
				iform.setStyle("DCC_RunPolicy", "visible", "false");
				iform.setStyle("DCC_SuppCardDetails", "visible", "false");
				iform.setStyle("DCC_AECB_Exposure", "visible", "false");
				iform.setStyle("DCC_LiabilityAddition", "visible", "false");
				iform.setStyle("DCC_AECBPipelines", "visible", "false");
				iform.setStyle("DCC_AdditionofChecque", "visible", "false");
				iform.setStyle("DocTypeSection", "visible", "false");
				iform.setStyle("SalaryDocReq", "visible", "false");
				// decision section fields
				iform.setStyle("Dectech_Decision", "visible", "false");
				iform.setStyle("delegation_authority", "visible", "false");
				iform.setStyle("Non_STP_reason", "visible", "false");
				iform.setStyle("Aecb_score", "visible", "false");
				iform.setStyle("Score_range", "visible", "false");
				iform.setStyle("deviation_description", "visible", "false");
				iform.setStyle("Underwriting_decision", "visible", "false");
				iform.setStyle("bureau_reference_number", "visible", "false");
				iform.setStyle("Additional_Documents_Required", "visible", "true");
				iform.setStyle("Additional_Documents_Required", "disable", "true");
				iform.setStyle("AddnalDocs", "disable", "true");
				
				
				
			}
			
			else if (Workstep.equalsIgnoreCase("Source_Refer")) {
				// added by vk
				iform.setStyle("DCC_PersonalDetails", "visible", "false");
				iform.setStyle("Additional_Documents_Required", "visible", "false");
				iform.setStyle("DCC_AECBPipelines", "visible", "false");
				iform.setStyle("DCC_SuppCardDetails", "visible", "false");
				iform.setStyle("DCC_RunPolicy", "visible", "false");
				iform.setStyle("DocTypeSection", "visible", "false");
				iform.setStyle("SalaryDocReq", "visible", "false");
				iform.setStyle("rerun_aecb", "disable", "false");
				//rerun_aecb
				iform.setStyle("DocTypeSection", "visible", "false");
				iform.setStyle("SalaryDocReq", "visible", "false");
				iform.setStyle("DeviationDescription", "visible", "true");
				iform.setStyle("DeviationDescription", "disable", "true");
				iform.setStyle("Net_Salary1", "disable", "false");
				iform.setStyle("Net_Salary2", "disable", "false");
				iform.setStyle("Net_Salary3", "disable", "false");
				iform.setStyle("BankingDtlsSave", "disable", "false");
				iform.setStyle("Fetch_AECB_Report", "disable", "false");
				iform.setStyle("Final_Limit", "visible", "true");
				iform.setStyle("LOB", "disable", "true");
				
				// Hritik 03.01.24 PDSC-1300
				iform.setStyle("DCC_Internal_Exposure", "visible", "true");
				iform.setStyle("Internal_Exposure", "visible", "true");
				iform.setStyle("DCC_Internal_Exposure", "disable", "true");
				iform.setStyle("Internal_Exposure", "disable", "true");
								
			}
			
			else if (Workstep.equalsIgnoreCase("Source_Refer_Hold")) {
				// added by vk
				iform.setStyle("Additional_Documents_Required", "visible", "false");
				iform.setStyle("DCC_AECBPipelines", "visible", "false");
				iform.setStyle("DCC_SuppCardDetails", "visible", "false");
				iform.setStyle("DCC_RunPolicy", "visible", "false");
				iform.setStyle("DocTypeSection", "visible", "false");
				iform.setStyle("SalaryDocReq", "visible", "false");
				iform.setStyle("DCC_RunPolicy", "visible", "false");
				iform.setStyle("DocTypeSection", "visible", "false");
				iform.setStyle("SalaryDocReq", "visible", "false");
				iform.setStyle("DeviationDescription", "disable", "true");	
				iform.setStyle("LOB", "disable", "true");
				
				// Hritik 03.01.24 PDSC-1300
				iform.setStyle("DCC_Internal_Exposure", "visible", "true");
				iform.setStyle("Internal_Exposure", "visible", "true");
				iform.setStyle("DCC_Internal_Exposure", "disable", "true");
				iform.setStyle("Internal_Exposure", "disable", "true");
			}
		}
			
			//vinayak chnages
			else if("InsertIntoSalary".equals(controlName))
			{
				 Digital_CC.mLogger.debug("inside insert into salary: " );
				 
				 try{
					 
					 Digital_CC.mLogger.debug("data feild value: " + data);
					 	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						
						Date current_date = new Date();
						String formattedActionDatetime=dateFormat.format(current_date);
					 
						if(data.equalsIgnoreCase("formload")){
							
							 Digital_CC.mLogger.debug("inside insert into salary on form load " );
						List<List<String>> twoDarray = iform.getDataFromDB(
								"select count(net_salary1) as rowCountOldSalary from NG_DCC_GR_NetSalaryDetails with (NOLOCK)  where Wi_Name ='"
										+ processInstanceID + "' and Workstep='Source_Refer' ");
						Digital_CC.mLogger.debug("twoDarray: " + twoDarray);
						String rowCountOldSalary = twoDarray.get(0).get(0);
						Digital_CC.mLogger.debug("rowCountOldSalary: " + rowCountOldSalary);
						if (rowCountOldSalary.equalsIgnoreCase("0")){
					 String Query_new_Salary = "select net_salary1,net_salary2,net_salary3 from NG_DCC_EXTTABLE with (NOLOCK)  where Wi_Name ='"
								+ processInstanceID + "'";
						Digital_CC.mLogger.debug("Query_new_Salary : " + Query_new_Salary);
						String extTabDataIPXML = Digital_CC_Common.apSelectWithColumnNames(Query_new_Salary, cabinetName,
								sessionId);
						Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
						String extTabDataOPXML = Digital_CC_Common.WFNGExecute(extTabDataIPXML, serverIp, serverPort, 1);
						Digital_CC.mLogger.debug("extTabDataOPXML2: " + extTabDataOPXML);

						XMLParser xmlParserData = new XMLParser(extTabDataOPXML);

						String net_salary1 = xmlParserData.getValueOf("net_salary1");
						Digital_CC.mLogger.debug("net_salary1: " + net_salary1);

						String net_salary2 = xmlParserData.getValueOf("net_salary2");
						Digital_CC.mLogger.debug("net_salary2: " + net_salary2);

						String net_salary3 = xmlParserData.getValueOf("net_salary3");
						Digital_CC.mLogger.debug("net_salary3: " + net_salary3);

						if ((net_salary1 != null && !"".equalsIgnoreCase(net_salary1))
								|| (net_salary2 != null && !"".equalsIgnoreCase(net_salary2))
								|| (net_salary3 != null && !"".equalsIgnoreCase(net_salary3))) {

							String columnNames = "Net_Salary1,Net_Salary2,Wi_Name,Net_Salary3,Workstep,insertion_date_time";
							String columnValues = "'" + net_salary1 + "','" + net_salary2 + "','" + processInstanceID
									+ "','" + net_salary3 + "','" + Workstep + "','" + formattedActionDatetime + "'";

							String apInsertInputXML = Digital_CC_Common.apInsert(cabinetName, sessionId, columnNames,
									columnValues, "NG_DCC_GR_NetSalaryDetails");
							Digital_CC.mLogger.debug("APInsertInputXML: " + apInsertInputXML);

							String apInsertOutputXML = Digital_CC_Common.WFNGExecute(apInsertInputXML, serverIp, serverPort,
									1);
							Digital_CC.mLogger.debug("APInsertOutputXML: " + apInsertInputXML);

							XMLParser xmlParserAPInsert = new XMLParser(apInsertOutputXML);
							String apInsertMaincode = xmlParserAPInsert.getValueOf("MainCode");
							Digital_CC.mLogger.debug("Status of apInsertMaincode  " + apInsertMaincode);

							if (apInsertMaincode.equalsIgnoreCase("0")) {
								Digital_CC.mLogger.debug("ApInsert successful: " + apInsertMaincode);
							} else {
								Digital_CC.mLogger.debug("ApInsert failed: " + apInsertMaincode);
							}
						}
							strReturn = "INSERTED";
						}
				 }
						//introdone
						else if (data.equalsIgnoreCase("introdone"))
						{
							Digital_CC.mLogger.debug("inside insert into salary on intro done " );
							 String Query_new_Salary = "select net_salary1,net_salary2,net_salary3 from NG_DCC_EXTTABLE with (NOLOCK)  where Wi_Name ='"
										+ processInstanceID + "'";
								Digital_CC.mLogger.debug("Query_new_Salary : " + Query_new_Salary);
								String extTabDataIPXML = Digital_CC_Common.apSelectWithColumnNames(Query_new_Salary, cabinetName,sessionId);
								Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
								String extTabDataOPXML = Digital_CC_Common.WFNGExecute(extTabDataIPXML, serverIp, serverPort, 1);
								Digital_CC.mLogger.debug("extTabDataOPXML2: " + extTabDataOPXML);

								XMLParser xmlParserData = new XMLParser(extTabDataOPXML);

								String net_salary1 = xmlParserData.getValueOf("net_salary1");
								Digital_CC.mLogger.debug("net_salary1: " + net_salary1);

								String net_salary2 = xmlParserData.getValueOf("net_salary2");
								Digital_CC.mLogger.debug("net_salary2: " + net_salary2);

								String net_salary3 = xmlParserData.getValueOf("net_salary3");
								Digital_CC.mLogger.debug("net_salary3: " + net_salary3);

//
								if ((net_salary1 != null && !"".equalsIgnoreCase(net_salary1))
										|| (net_salary2 != null && !"".equalsIgnoreCase(net_salary2))
										|| (net_salary3 != null && !"".equalsIgnoreCase(net_salary3))) {
								
									String columnNames = "Net_Salary1,Net_Salary2,Wi_Name,Net_Salary3,Workstep,insertion_date_time";
									String columnValues = "'" + net_salary1 + "','" + net_salary2 + "','" + processInstanceID
											+ "','" + net_salary3 + "','" + Workstep + "','" + formattedActionDatetime + "'";

									String apInsertInputXML = Digital_CC_Common.apInsert(cabinetName, sessionId, columnNames,
											columnValues, "NG_DCC_GR_NetSalaryDetails");
									Digital_CC.mLogger.debug("APInsertInputXML: " + apInsertInputXML);

									String apInsertOutputXML = Digital_CC_Common.WFNGExecute(apInsertInputXML, serverIp, serverPort,
											1);
									Digital_CC.mLogger.debug("APInsertOutputXML: " + apInsertInputXML);

									XMLParser xmlParserAPInsert = new XMLParser(apInsertOutputXML);
									String apInsertMaincode = xmlParserAPInsert.getValueOf("MainCode");
									Digital_CC.mLogger.debug("Status of apInsertMaincode  " + apInsertMaincode);

									if (apInsertMaincode.equalsIgnoreCase("0")) {
										Digital_CC.mLogger.debug("ApInsert successful: " + apInsertMaincode);
									} else {
										Digital_CC.mLogger.debug("ApInsert failed: " + apInsertMaincode);
									}
								}
									strReturn = "INSERTED";
						}
				 }
				 catch (Exception e) {
						Digital_CC.mLogger.debug("Exception in inserting salary table!" + e.getMessage());
					}
		}
			else if("Exception_popup".equals(controlName)){ // Added - Hritik PDSC-1436 31.01.2024
				
				try{
					String Query_FTS = "select wi_name,Status from NG_DCC_FTS_DOC Where wi_name = '"+processInstanceID+"'";
					String extTabDataIPXML = Digital_CC_Common.apSelectWithColumnNames(Query_FTS, cabinetName,sessionId);
					Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
					String extTabDataOPXML = Digital_CC_Common.WFNGExecute(extTabDataIPXML, serverIp, serverPort, 1);
					Digital_CC.mLogger.debug("extTabDataOPXML2: " + extTabDataOPXML);
					XMLParser xmlParserData = new XMLParser(extTabDataOPXML);
					String Status = xmlParserData.getValueOf("Status");
					String wi_name = xmlParserData.getValueOf("wi_name");
					
					Digital_CC.mLogger.debug("wi_name NG_DCC_FTS_DOC : " + wi_name);
					Digital_CC.mLogger.debug("Status NG_DCC_FTS_DOC : " + Status);
					
					if(Status !=null && !"".equalsIgnoreCase(Status) && "R".equalsIgnoreCase(Status)){
						Digital_CC.mLogger.debug("Status!" + Status);
						strReturn = "Ready";
					}
					else if(Status !=null && !"".equalsIgnoreCase(Status) && "C".equalsIgnoreCase(Status)){
						Digital_CC.mLogger.debug("Status!" + Status);
						strReturn = "Completed";
					}
				}
				
				catch (Exception e) {
					Digital_CC.mLogger.debug("Exception_popup!" + e.getMessage());
				}
			}
		Digital_CC.mLogger.debug("strReturn!" + strReturn);
		return strReturn;
	}
}
