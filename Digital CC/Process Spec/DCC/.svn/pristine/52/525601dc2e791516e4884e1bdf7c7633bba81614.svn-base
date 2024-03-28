package com.newgen.iforms.user;

import java.util.List;

import com.newgen.iforms.custom.IFormReference;

public class Digital_CC_FormLoad extends Digital_CC_Common {

	public String formLoadEvent(IFormReference iform, String controlName, String event, String data) {
		String strReturn = "";

		Digital_CC.mLogger.debug("This is DCC_FormLoad_Event" + event + " controlName :" + controlName);

		String Workstep = iform.getActivityName();
		Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + iform.getActivityName()
		+ ", Workstep :" + Workstep);
		
		String docType = (String) iform.getValue("AddnalDocs_doctype");
		String decision = (String) iform.getValue("Decision");
		String overrideSalary = (String) iform.getValue("overrideIncomeFromDectech");
		String Prospect_Creation_Date = (String) iform.getValue("Prospect_Creation_Date");
		Digital_CC.mLogger.debug(" Prospect_Creation_Date on load" + Prospect_Creation_Date);
		String Selected_Card_Type = (String) iform.getValue("Selected_Card_Type");
		Digital_CC.mLogger.debug(" Selected_Card_Type on load" + Selected_Card_Type);
		String FirstName = (String) iform.getValue("FirstName");
		String MiddleName = (String) iform.getValue("MiddleName");
		String LastName = (String) iform.getValue("LastName");
		String CUSTOMERNAME = "";
		
		if(FirstName == null  ){
			FirstName = "";
		}
		if(MiddleName == null  ){
			MiddleName = "";
		}
		if(LastName == null  ){
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
		
		if ("DecisionDropDown".equals(controlName)) {
			//added by gaurav
			try {
				List lstDecisions = iform
						.getDataFromDB("SELECT decision FROM NG_DCC_MASTER_DECISION WITH(NOLOCK) WHERE WorkstepName='"
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

			} catch (Exception e) {
				Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + iform.getActivityName()
				+ ", Exception in Decision drop down load " + e.getMessage());
			}
		}
		
		else if ("AddnalDocs".equalsIgnoreCase(controlName)) {
			//added by gaurav 1708

			if (Workstep.equalsIgnoreCase("Firco")){
				if (docType.equalsIgnoreCase("Other_Document")) {
					Digital_CC.mLogger.debug("docType : formLoad " + docType);
					iform.setStyle("AddnalDocs_remarks", "visible", "true");

				} else {
					iform.setValue("AddnalDocs_remarks", "");
					iform.setStyle("AddnalDocs_remarks", "visible", "false");      
				}
			}
		}

		else if ("SuppDetailsGrid".equalsIgnoreCase(controlName)) {
			//added by gaurav 1808
			if (Workstep.equalsIgnoreCase("Card_Ops")){
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
			//customer Name
			String cust_name = (String) iform.getValue("CUSTOMERNAME");
			if(cust_name == null || "".equalsIgnoreCase(cust_name)){
		    iform.setValue("CUSTOMERNAME", CUSTOMERNAME);
			}
		    //card application date
		    if(!(Prospect_Creation_Date == null || "".equalsIgnoreCase(Prospect_Creation_Date) )){
		    iform.setValue("card_application_date", Prospect_Creation_Date);
		    }
		    //Applied_card
		    if(!(Selected_Card_Type == null || "".equalsIgnoreCase(Selected_Card_Type) )){
		    iform.setValue("Applied_card", Selected_Card_Type);
		    }
			iform.setStyle("Decision", "disable", "false");
			iform.setStyle("Remarks", "disable", "false");
			if(decision.equalsIgnoreCase("Reject")){
				iform.setStyle("Decline_reason", "visible", "true");
				iform.setStyle("Decline_reason", "mandatory", "true");
			}else{
				iform.setStyle("Decline_reason", "visible", "false");
				iform.setStyle("Decline_reason", "mandatory", "false");
			}
			//1708 added by gaurav override_netSalary
			iform.setValue("overrideIncomeFromDectech", "false");
			if ("Exceptions".equalsIgnoreCase(Workstep.trim())) {
				Digital_CC.mLogger.debug(" inside form load exceptions-: " + Workstep);
				//Aloc Dtls table data
				if(COMPANY_STATUS_CC == null || COMPANY_STATUS_CC == "" || COMPANY_STATUS_PL == null || COMPANY_STATUS_PL == ""){
					String query = "select COMPANY_STATUS_CC,COMPANY_STATUS_PL, EMPLOYER_CATEGORY_PL, ALOC_REMARKS_CC, ALOC_REMARKS_PL from NG_RLOS_ALOC_OFFLINE_DATA WITH(nolock) where EMPLOYER_CODE=main_Employer_code and main_Employer_code = '"+ EMPLOYER_CODE +"'";
					List <List<String>> AlocDetails = iform.getDataFromDB(query);
					Digital_CC.mLogger.debug(" inside AlocDetails arraylist: " + AlocDetails);
					if (!AlocDetails.isEmpty()){
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
				//sections
				iform.setStyle("DCC_PersonalDetails", "visible", "false");
				iform.setStyle("DCC_SuppCardDetails", "visible", "false");
				iform.setStyle("DCC_LiabilityAddition", "visible", "false");
				iform.setStyle("Additional_Documents_Required", "visible", "false");
				iform.setStyle("DocTypeSection", "visible", "false");
				//free field
				iform.setStyle("ensureDelegationCheck", "visible", "true");	
				iform.setStyle("button6", "disable", "false");	
				//GenerateCam button at cad
				iform.setStyle("GenerateCam", "visible", "true");
				iform.setStyle("GenerateCam", "disable", "false");
			}
			
			else if (Workstep.equalsIgnoreCase("Card_Ops")) {
				if(decision.equalsIgnoreCase("Reschedule")){
					iform.setStyle("DocTypeSection", "visible", "true");
				}else{
					iform.setStyle("DocTypeSection", "visible", "false");
				}
				//added by gaurav
				//sections
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
				//free fields
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
				iform.setStyle("Fetch_Manual_Dectech", "visible", "false");
				iform.setStyle("Fetch_AECB_Report", "visible", "false");
				iform.setStyle("bureau_reference_number", "visible", "false");
				iform.setStyle("Expense1", "visible", "false");
				iform.setStyle("Expense2", "visible", "false");
				iform.setStyle("Expense3", "visible", "false");
				iform.setStyle("Expense4", "visible", "false");
				iform.setStyle("Expense5", "visible", "false");
				iform.setStyle("Expense6", "visible", "false");
				iform.setStyle("Virtual_Card_Limit", "visible", "false");
				iform.setStyle("Credit_Shield_Flag", "visible", "false");
				iform.setStyle("RAK_Protect_Flag", "visible", "false");
				iform.setStyle("Self_Supp_Card_Limit", "visible", "false");
				iform.setStyle("Dependents", "visible", "false");
				//Supp Card Details Grid
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
				
			}
			else if (Workstep.equalsIgnoreCase("Firco")){
				//added by gaurav
				//sections
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
				//decision section fields
				iform.setStyle("Dectech_Decision", "visible", "false");
				iform.setStyle("delegation_authority", "visible", "false");
				iform.setStyle("Non_STP_reason", "visible", "false");
				iform.setStyle("Aecb_score", "visible", "false");
				iform.setStyle("Score_range", "visible", "false");
				iform.setStyle("deviation_description", "visible", "false");
				iform.setStyle("Underwriting_decision", "visible", "false");
				iform.setStyle("Fetch_Manual_Dectech", "visible", "false");
				iform.setStyle("Fetch_AECB_Report", "visible", "false");
				iform.setStyle("bureau_reference_number", "visible", "false");
			}
			
		}


		
		return strReturn;
	}
}
