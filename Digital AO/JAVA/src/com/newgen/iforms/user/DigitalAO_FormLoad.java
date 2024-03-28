package com.newgen.iforms.user;

import java.util.List;

import com.newgen.iforms.custom.IFormReference;

public class DigitalAO_FormLoad extends DigitalAO_Common {

	public String formLoadEvent(IFormReference iform, String controlName, String event, String data) {
		String strReturn = "";

		DigitalAO.mLogger.debug("This is DAO_FormLoad_Event" + event + " controlName :" + controlName);

		String Workstep = iform.getActivityName();
		DigitalAO.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + iform.getActivityName()
				+ ", Workstep :" + Workstep);

		if ("section_onLoad".equalsIgnoreCase(controlName)){
			DigitalAO.mLogger.debug("Section Disable :  Form load : log_in_as :"+iform.getUserName());
			setControlValue("log_in_as",iform.getUserName(), iform);
			DigitalAO.mLogger.debug("Section Disable :  Form load : before Section Disable");
			iform.setStyle("Customer_Information", "disable", "true");
			iform.setStyle("DigitalAO_EmpDetails", "disable", "true");
			iform.setStyle("DigitalAO_CompDetails", "disable", "true");
			iform.setStyle("Address_Details", "disable", "true");
			iform.setStyle("Income_Detail", "disable", "true");
			iform.setStyle("Contact_Details", "disable", "true");
			iform.setStyle("FATCA_CRS_Details", "disable", "true");
			iform.setStyle("System_Checks", "disable", "true");
			iform.setStyle("Account_Details", "disable", "true");
			iform.setStyle("UID_Details", "disable", "true");
			iform.setStyle("DigitalAO_BackgroundInfo", "disable", "true");
			iform.setStyle("Additional_Documents_Required", "disable", "true");
			iform.setStyle("NG_DAO_GR_DECISION_HISTORY", "disable", "true");
			iform.setStyle("Generate_RiskScore", "visible", "false");
			iform.setStyle("template_generate_kyc", "visible", "false");
			iform.setStyle("AECB_rpt", "disable", "false");
			//vinayak change jira 3727
			String is_Ntb_val = (String) iform.getValue("is_Ntb");
			if(is_Ntb_val.equalsIgnoreCase("N"))
			{
				iform.setStyle("internalExposureDetails", "visible", "true");	
				String is_stp_val=(String) iform.getValue("is_stp");
				if(("sign_upload_maker").equalsIgnoreCase(Workstep)&& (is_stp_val.equalsIgnoreCase("Y")))
				{
					iform.setStyle("internalExposureDetails", "disable", "false");
				}
			}
			
			if(("operations").equalsIgnoreCase(Workstep)){
				iform.setStyle("UID_Details", "disable", "false");
			//	iform.setStyle("UID_Details_Grid", "disable", "true");
				iform.setStyle("UID_Remarks_grid", "disable", "false");
				iform.setStyle("AECB_rpt", "disable", "false");
				iform.setStyle("Generate_RiskScore", "visible", "false");
				iform.setStyle("template_generate_kyc", "visible", "false");
				iform.setStyle("internalExposureDetails", "disable", "false");//vinayak change jira 3727
				
				// added for name edit changes -- 07.02.2023
				String Name_modify = (String) iform.getValue("Name_modify");
				if((Name_modify.equalsIgnoreCase("Y")))
				{
					iform.setStyle("Given_Name", "disable", "false");
					iform.setStyle("Middle_Name", "disable", "false");
					iform.setStyle("Surname", "disable", "false");
					iform.setStyle("Generate_RiskScore", "visible", "true");
					iform.setStyle("template_generate_kyc", "visible", "true");
					iform.setStyle("Generate_RiskScore", "disable", "false");
					iform.setStyle("Generate_firco_temp", "disable", "false");
					iform.setStyle("template_generate_kyc", "disable", "false");
				}
				if("Y".equalsIgnoreCase((String) iform.getValue("Dedupe_Match_found")))
				{
					iform.setStyle("CIF", "disable", "false");
				}
			}
			
			if(("Ops_Account_Closure_Maker").equalsIgnoreCase(Workstep))
			{
				iform.setStyle("Card_closed", "visible", "true");
				iform.setStyle("Card_closed", "disable", "false");
				iform.setStyle("ChequeBk_destroy", "visible", "true");
				iform.setStyle("ChequeBk_destroy", "disable", "false");
			}
			else if(("Ops_Account_Closure_Checker").equalsIgnoreCase(Workstep)){
				
				iform.setStyle("Card_closed", "visible", "true");
				iform.setStyle("Card_closed", "disable", "true");
				iform.setStyle("ChequeBk_destroy", "visible", "true");
				iform.setStyle("ChequeBk_destroy", "disable", "true");
			}
			else{
				iform.setStyle("Card_closed", "visible", "false");
				iform.setStyle("ChequeBk_destroy", "visible", "false");
			}
			DigitalAO.mLogger.debug("Section Disable :  Form load : after Section Disable");
		}
		
		else if("purpose_description".equalsIgnoreCase(controlName))
		{
			String description_val="";
			String product_category=(String) iform.getValue("Product_Category");
			DigitalAO.mLogger.debug("product_category "+product_category);
			String purpose_account=(String) iform.getValue("Purpose_of_account");
			DigitalAO.mLogger.debug("purpose_account "+purpose_account);
			String purpose_account_sub = purpose_account.substring(0,5);
			DigitalAO.mLogger.debug("purpose_account_sub "+purpose_account_sub);
			
			if(purpose_account_sub.equalsIgnoreCase("Other")){
				description_val=purpose_account;
				DigitalAO.mLogger.debug("description_val Others: "+description_val);
				
				iform.setStyle("Purpose_of_account_description", "disable", "false");
				setControlValue("Purpose_of_account_description",description_val, iform);
				iform.setStyle("Purpose_of_account_description", "disable", "true");
				DigitalAO.mLogger.debug("Value Set");
			}
			
			else if("C".equalsIgnoreCase(product_category)||"Conventional".equalsIgnoreCase(product_category))
			{
				List<List<String>> description= iform.getDataFromDB("select Desc_Conventional from NG_MASTER_DAO_PURPOSE_OF_ACCOUNT with (nolock) where Code_Common='"+purpose_account+"'");
				DigitalAO.mLogger.debug("description: "+description);
				if (!description.isEmpty()) {
					DigitalAO.mLogger.debug("Inside: ");
					description_val = description.get(0).get(0);
					DigitalAO.mLogger.debug("description: " + description_val);
					
					iform.setStyle("Purpose_of_account_description", "disable", "false");
					setControlValue("Purpose_of_account_description",description_val, iform);
					iform.setStyle("Purpose_of_account_description", "disable", "true");
					DigitalAO.mLogger.debug("Value Set");
					
				} else {
					DigitalAO.mLogger.debug("description is empty!!");
				}
				DigitalAO.mLogger.debug("description_val"+description_val);
			}
			else if("I".equalsIgnoreCase(product_category)||"Islamic".equalsIgnoreCase(product_category))
			{
				List<List<String>> description= iform.getDataFromDB("select Desc_Islamic from NG_MASTER_DAO_PURPOSE_OF_ACCOUNT with (nolock) where Code_Common='"+purpose_account+"'");
				DigitalAO.mLogger.debug("description: "+description);
				if (!description.isEmpty()) {
					DigitalAO.mLogger.debug("Inside: ");
					description_val = description.get(0).get(0);
					DigitalAO.mLogger.debug("description: " + description_val);
					
					iform.setStyle("Purpose_of_account_description", "disable", "false");
					setControlValue("Purpose_of_account_description",description_val, iform);
					iform.setStyle("Purpose_of_account_description", "disable", "true");
					DigitalAO.mLogger.debug("Value Set");
					
				} else {
					DigitalAO.mLogger.debug("description is empty!!");
				}
					
				DigitalAO.mLogger.debug("description_val"+description_val);
			}
		}
		
		else if("section_onLoad_Readmode".equalsIgnoreCase(controlName)){
			
			DigitalAO.mLogger.debug("Section Disable :  Form load : controlName: "+controlName);
			iform.setStyle("Customer_Information", "disable", "true");
			iform.setStyle("DigitalAO_EmpDetails", "disable", "true");
			iform.setStyle("DigitalAO_CompDetails", "disable", "true");
			iform.setStyle("Address_Details", "disable", "true");
			iform.setStyle("Income_Detail", "disable", "true");
			iform.setStyle("Contact_Details", "disable", "true");
			iform.setStyle("FATCA_CRS_Details", "disable", "true");
			iform.setStyle("System_Checks", "disable", "true");
			iform.setStyle("Account_Details", "disable", "true");
			iform.setStyle("UID_Details", "disable", "true");
			iform.setStyle("DigitalAO_BackgroundInfo", "disable", "true");
			iform.setStyle("Additional_Documents_Required", "disable", "true");
			iform.setStyle("NG_DAO_GR_DECISION_HISTORY", "disable", "true");
			iform.setStyle("sheet1", "disable", "true");
			iform.setStyle("rejectReason", "disable", "true");
			DigitalAO.mLogger.debug("Section Disable :  Form load : R mode: ");
		}
		
		else if ("company_details_popup_load".equalsIgnoreCase(controlName)){
			 //business details grid validation
			if (Workstep.equalsIgnoreCase("operations")){
			DigitalAO.mLogger.debug("company_details_popup_load :  Form load : before Disable");
			iform.setStyle("companyName", "disable", "true");
		    iform.setStyle("countryOfIncorporation", "disable", "true");
		    iform.setStyle("percentageShareholding", "disable", "true");
			iform.setStyle("annual_turnover", "disable", "true");
			iform.setStyle("annual_profit", "disable", "true");
		    iform.setStyle("tradeLicense", "disable", "true");
			iform.setStyle("Year_Of_Incorporation", "disable", "true");
		 // iform.setStyle("Country_List", "disable", "true"); hritik
			DigitalAO.mLogger.debug("company_details_popup_load :  Form load : after  Disable");
			}
		}
		
		else if ("cust_info_frame".equalsIgnoreCase(controlName)) {
			String highRisk = (String) iform.getValue("High_risk");
			if (Workstep.equalsIgnoreCase("compliance")) 
			{
				DigitalAO.mLogger.debug("High_risk :  Form loand : compliance");

				iform.setStyle("DigitalAO_BackgroundInfo", "visible", "true");
				iform.setStyle("background_information", "visible", "true");
				iform.setStyle("template_generate", "visible", "true");
				iform.setStyle("template_generate", "disable", "false");

				if (highRisk.equalsIgnoreCase("Y") || highRisk.equalsIgnoreCase("Yes")) {
					iform.setStyle("DigitalAO_BackgroundInfo", "disable", "true"); // hritik 29.7.22 as discussed W ST
					iform.setStyle("background_information", "disable", "true");// hritik 29.7.22 as discussed W ST
					iform.setStyle("template_generate", "visible", "true");
					iform.setStyle("template_generate", "disable", "false");
				} else {
					iform.setStyle("DigitalAO_BackgroundInfo", "disable", "true");
					iform.setStyle("background_information", "disable", "true");
					iform.setStyle("template_generate", "disable", "false");
				}
			}
			else 
			{
				iform.setStyle("DigitalAO_BackgroundInfo", "visible", "false"); // hritik 29.7.22 as discussed W ST
				iform.setStyle("background_information", "visible", "false");
				iform.setStyle("template_generate", "visible", "false");
				iform.setStyle("template_generate", "disable", "true");
			}
			
			String decision = (String) iform.getValue("Decision");
			if(decision.equalsIgnoreCase("Approve"))
			{
				iform.setStyle("Remarks_dec", "disable", "false");
				iform.setStyle("Remarks_dec", "mandatory", "false");
				iform.setStyle("rejectReason", "disable", "true");
				iform.setStyle("rejectReason", "mandatory", "false");
			}
			else if(decision.equalsIgnoreCase("Reject")){
				DigitalAO.mLogger.debug("Decision :  RejectReason on change" + decision);
				iform.setStyle("Remarks_dec", "disable", "false");
				iform.setStyle("Remarks_dec", "mandatory", "false");
				iform.setStyle("rejectReason", "disable", "false");
				iform.setStyle("rejectReason", "mandatory", "true");
			}
			else 
			{
				iform.setStyle("Remarks_dec", "disable", "false");
				iform.setStyle("Remarks_dec", "mandatory", "false");
				iform.setStyle("rejectReason", "disable", "true");
				iform.setStyle("rejectReason", "mandatory", "false");
			}
			
			
			// Additional Documents Required
			if (Workstep.equalsIgnoreCase("operations") 
				|| Workstep.equalsIgnoreCase("compliance") 
				|| Workstep.equalsIgnoreCase("compliance_wc") 
				|| Workstep.equalsIgnoreCase("Additional_cust_details")
				|| Workstep.equalsIgnoreCase("wm_control"))
				//|| Workstep.equalsIgnoreCase("sign_upload_checker")) // For STP journey if docs uploaded wrong. New requirement.
			// commented for non etb release
			{
				iform.setStyle("Additional_Documents_Required", "disable", "false");
				iform.setStyle("AddnalDocs", "disable", "false");
			}
			// gross month salary CIF_Update 
			if (Workstep.equalsIgnoreCase("sign_upload_checker"))
			{
				iform.setStyle("CIF_Update", "visible", "true");
				iform.setStyle("sign_upload", "visible", "true");
				iform.setStyle("gross_monthly_salary_income", "disable", "false");
				iform.setStyle("CIF_Update", "disable", "false");
				iform.setStyle("sign_upload", "disable", "false");
				iform.setStyle("Additional_Documents_Required", "disable", "false");
				iform.setStyle("AddnalDocs", "disable", "false");
			}
			else
			{
				iform.setStyle("gross_monthly_salary_income", "disable", "true");
				iform.setStyle("CIF_Update", "disable", "true");
				iform.setStyle("CIF_Update", "visible", "false");
				iform.setStyle("sign_upload", "visible", "false");
			}
			
			if (Workstep.equalsIgnoreCase("operations")) 
			{
				// Risk Score Button
				iform.setStyle("Risk_score_trigger", "disable", "false");
				
				//vinayak added jira 3913 rakempowered changes
				String Payroll_account = (String) iform.getValue("Payroll_account");
				DigitalAO.mLogger.debug("Payroll_account " + Payroll_account);
				if("RAKEmpower".equalsIgnoreCase(Payroll_account)){					
					iform.setStyle("Risk_score_trigger", "disable", "true");
				}
				
				//POA-996 - Hritik 7.7.22 - start
				iform.setStyle("country_of_residence", "disable", "false");
				iform.setStyle("Nationality", "disable", "false");
				
				
			    // PEP
				iform.setStyle("PEP", "disable", "false");
				String Fatca_PEP = (String) iform.getValue("PEP");
				DigitalAO.mLogger.debug("PEP :  formload " + Fatca_PEP);
				if (Fatca_PEP.trim().equalsIgnoreCase("YES") || Fatca_PEP.trim().equalsIgnoreCase("Y")) {
					iform.setStyle("PEP_Category", "visible", "true");
					iform.setStyle("Previous_Designation", "visible", "true");
					iform.setStyle("Relation_Detail_w_PEP", "visible", "true");
					iform.setStyle("name_of_pep", "visible", "true");
					iform.setStyle("country_pep_hold_status", "visible", "true");
					iform.setStyle("Emirate_pep_hold_status", "visible", "true");
				} else {
					iform.setStyle("PEP_Category", "visible", "false");
					iform.setStyle("Previous_Designation", "visible", "false");
					iform.setStyle("Relation_Detail_w_PEP", "visible", "false");
					iform.setStyle("name_of_pep", "visible", "false");
					iform.setStyle("country_pep_hold_status", "visible", "false");
					iform.setStyle("Emirate_pep_hold_status", "visible", "false");
				}
			} 
			else
			{
				iform.setStyle("Fatca_PEP", "disable", "true");
				iform.setStyle("PEP_Category", "visible", "false");
				iform.setStyle("Previous_Designation", "visible", "false");
				iform.setStyle("Relation_Detail_w_PEP", "visible", "false");
				iform.setStyle("name_of_pep", "visible", "false");
				iform.setStyle("country_pep_hold_status", "visible", "false");
				iform.setStyle("Emirate_pep_hold_status", "visible", "false");
			}
			return strReturn = "success executed cust_info_frame";

		}

		else if ("employment_frame".equalsIgnoreCase(controlName)) {
			
			DigitalAO.mLogger.debug("DigitalAO_EmpDetails: Disable on load");
			iform.setStyle("DigitalAO_EmpDetails", "visible", "false");
			iform.setStyle("DigitalAO_CompDetails", "visible", "false");
			iform.setStyle("company_detail", "visible", "false");

			String employement_type = (String) iform.getValue("employement_type");
			DigitalAO.mLogger.debug("DigitalAO_EmpDetails: employement_type: formload" + employement_type);
			if (employement_type.trim().equalsIgnoreCase("Salaried")) {
				iform.setStyle("DigitalAO_EmpDetails", "visible", "true");
				iform.setStyle("DigitalAO_EmpDetails", "disable", "true");
				if (Workstep.equalsIgnoreCase("operations"))
				{
					//iform.setStyle("Company_employer_name", "disable", "false"); chngaes vinayak changes jira 3310
					iform.setStyle("is_modify_employer", "disable", "false");
					iform.setValue("is_EmployerAdd_req", "");//set value of flag as null on load for employer chnage
					String is_modify_employer_val = (String) iform.getValue("is_modify_employer");
					String is_notPicklistEmployer_val = (String) iform.getValue("is_notPicklistEmployer");
					if("true".equalsIgnoreCase(is_modify_employer_val))
					{
					iform.setStyle("picklist_employer_name", "disable", "false");
					iform.setStyle("is_notPicklistEmployer", "visible", "true");
					iform.setStyle("is_notPicklistEmployer", "disable", "false");					
					iform.setStyle("CustomerInput_other_employerName", "disable", "true");
					}
					if("true".equalsIgnoreCase(is_notPicklistEmployer_val))
					{
						iform.setStyle("CustomerInput_other_employerName", "visible", "true");
						iform.setStyle("CustomerInput_other_employerName", "disable", "false");
						iform.setStyle("picklist_employer_name", "disable", "true");						
					}

				}
			} 
			else if (employement_type.trim().equalsIgnoreCase("Self Employed")) {
				if (Workstep.equalsIgnoreCase("operations"))
				{
					iform.setStyle("DigitalAO_CompDetails", "visible", "true");
					iform.setStyle("company_detail", "visible", "true");
					iform.setStyle("DigitalAO_CompDetails", "disable", "false");
					iform.setStyle("company_detail", "disable", "false");
				}
				else{
					iform.setStyle("DigitalAO_CompDetails", "visible", "true");
					iform.setStyle("company_detail", "visible", "true");
					iform.setStyle("DigitalAO_CompDetails", "disable", "true");
					iform.setStyle("company_detail", "disable", "true");
				}
			} 
			else {
				iform.setStyle("DigitalAO_EmpDetails", "visible", "false");
				iform.setStyle("DigitalAO_CompDetails", "visible", "false");
				iform.setStyle("company_detail", "visible", "false");
			}
			return strReturn = "Success disable DigitalAO_EmpDetails";
		}
		
		else if("populateCIF".equals(controlName))
		{
			try
			{
				List CIF = iform.getDataFromDB("select CIFID from NG_DAO_GR_DEDUPE_DETAILS with (nolock) where Wi_Name='"
				+ getWorkitemName(iform) + "'");
				DigitalAO.mLogger.debug(" inside Pop CIF drop down : " + CIF);
				String value = "";
				
				for (int i = 0; i < CIF.size(); i++)
				{
					List<String> arr1 = (List) CIF.get(i);
					value = arr1.get(0);
					iform.addItemInCombo("CIF", value, value);
				}
				iform.addItemInCombo("CIF", "NTB", "NTB");
				strReturn = "CIF Loaded";
			}
			catch(Exception e){
				
				DigitalAO.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + iform.getActivityName()
						+ ", Exception in Pop CIF load " + e.getMessage());
			}
			
		}

		else if ("DecisionDropDown".equals(controlName))
		{
			try {
				
				List lstDecisions = iform
						.getDataFromDB("SELECT decision FROM NG_DAO_MASTER_Decision WITH(NOLOCK) WHERE WorkstepName='"
								+ iform.getActivityName() + "' and Is_Active='Y' ORDER BY decision ASC");
				DigitalAO.mLogger.debug(" inside Decision drop down WS: " + lstDecisions);
				String value = "";
				iform.clearCombo("Decision");
				for (int i = 0; i < lstDecisions.size(); i++) {
					List<String> arr1 = (List) lstDecisions.get(i);
					value = arr1.get(0);
					iform.addItemInCombo("Decision", value, value);
					strReturn = "Decision Loaded";
				}
				
				/*if(Workstep=="Additional details needed" && iform.getValue("is_stp")=="Y")
                {
					iform.removeItemFromCombo("Decision",1);
                    iform.removeItemFromCombo("Decision",2);
                    iform.removeItemFromCombo("Decision",3);
                    iform.removeItemFromCombo("Decision",4);
                    iform.removeItemFromCombo("Decision",5);
                    iform.removeItemFromCombo("Decision",6);
                }*/
				if("sign_upload_checker".equalsIgnoreCase(Workstep) && "Manual_Archive".equalsIgnoreCase((String) iform.getValue("prevws")))
				{
					DigitalAO.mLogger.debug(" inside Decision drop down remove item from combo "+ iform.getValue("prevws"));
					iform.removeItemFromCombo("Decision", 4);
					iform.removeItemFromCombo("Decision", 3);
					iform.removeItemFromCombo("Decision", 1);
				} // POA-2569
				
				if ("Additional_cust_details".equalsIgnoreCase(Workstep) && "sign_upload_checker".equalsIgnoreCase((String) iform.getValue("prevws"))) {
					DigitalAO.mLogger.debug(" inside Decision drop down remove item from combo "+ iform.getValue("prevws"));
					iform.removeItemFromCombo("Decision", 5);
					iform.removeItemFromCombo("Decision", 4);
					iform.removeItemFromCombo("Decision", 3);
					iform.removeItemFromCombo("Decision", 2);
					iform.removeItemFromCombo("Decision", 1);
				//	iform.removeItemFromCombo("Decision", 6);
				}
				else
				{
					iform.removeItemFromCombo("Decision", 6);
				}
				
				String Name_modify = (String) iform.getValue("Name_modify");
				String Firco = (String) iform.getValue("firco_hit");
				String Risk = (String) iform.getValue("high_risk");
				String dedupe  = (String) iform.getValue("Dedupe_Match_found");
				
				if("operations".equalsIgnoreCase(Workstep) && "N".equalsIgnoreCase(Name_modify) 
					&& "N".equalsIgnoreCase(Firco) && "N".equalsIgnoreCase(Risk) && "Y".equalsIgnoreCase(dedupe) )
				{
					DigitalAO.mLogger.debug("Inside Ops condition "+ Workstep);
					iform.removeItemFromCombo("Decision", 3);
					iform.removeItemFromCombo("Decision", 1);
					strReturn = "Decision Loaded at Ops for Dedupe Y & name modify Firco Risk N";
				}
				
				if("operations".equalsIgnoreCase(Workstep) && "Y".equalsIgnoreCase(Name_modify) 
					&& "N".equalsIgnoreCase(Firco) && "N".equalsIgnoreCase(Risk))
				{
					DigitalAO.mLogger.debug("Inside Ops condition "+ Workstep);
					iform.removeItemFromCombo("Decision", 3);
					strReturn = "Decision Loaded at Ops for name modify Y & Firco Risk N";
				}
				
				if("operations".equalsIgnoreCase(Workstep) && "Y".equalsIgnoreCase(Name_modify) 
					&& "N".equalsIgnoreCase(Firco) && "Y".equalsIgnoreCase(Risk))
				{
					DigitalAO.mLogger.debug("Inside Ops condition "+ Workstep);
					iform.removeItemFromCombo("Decision", 3);
					strReturn = "Decision Loaded at Ops for name modify Y & Firco N Risk Y";
				}
				if("operations".equalsIgnoreCase(Workstep) && "Y".equalsIgnoreCase(dedupe) 
						&& "Y".equalsIgnoreCase(Risk))
					{
						DigitalAO.mLogger.debug("Inside Ops condition "+ Workstep);
						iform.removeItemFromCombo("Decision", 3);
						strReturn = "Decision Loaded at Ops for Dedupe Y and  Risk Y";
					}
					String Q_segment=(String) iform.getValue("q_customer_segment");
					String Q_source=(String) iform.getValue("q_source_unit");
					
					DigitalAO.mLogger.debug("Q_segment "+ iform.getValue("q_customer_segment"));
					DigitalAO.mLogger.debug("Q_source "+ iform.getValue("q_source_unit"));
				
				if ("Additional_cust_details".equalsIgnoreCase(Workstep)
					&&(("PAM".equalsIgnoreCase((String) iform.getValue("q_customer_segment")) && "RM".equalsIgnoreCase((String) iform.getValue("q_source_unit"))) 
					||("PAM".equalsIgnoreCase((String) iform.getValue("q_customer_segment")) && "VRM".equalsIgnoreCase((String) iform.getValue("q_source_unit")))))
					{
						DigitalAO.mLogger.debug("PAM - RM/VRM ");
						iform.addItemInCombo("Decision", "send to WM Control", "send to wm_control");
					}
				
			} catch (Exception e) 
			{
				DigitalAO.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", " +
				"WSNAME: " + iform.getActivityName()
				+ ", Exception in Decision drop down load " + e.getMessage());
			}
			return strReturn ="Success DecisionDropDown";
		}
		else if ("template_generate_kyc".equalsIgnoreCase(controlName)) {
			
			String pdfName = "DAO_Template_kyc";
			try{
				DigitalAO.mLogger.debug("template_generate_kyc "+ new DAOTemplate().generate_kyc_temp(iform, getWorkitemName(iform), pdfName));
				return new DAOTemplate().generate_kyc_temp(iform, getWorkitemName(iform), pdfName);
			}
			catch(Exception e){
				DigitalAO.mLogger.debug("template_generate_kyc :"+e.getMessage());
			}
		}
		else if ("template_generate_risk_sheet".equalsIgnoreCase(controlName)) {
			
			try{
				DigitalAO.mLogger.debug("template_generate_risk_sheet "+genrate_risksheet(iform));
				return genrate_risksheet(iform);
			}
			catch(Exception e){
				DigitalAO.mLogger.debug("template_generate_risk_sheet :"+e.getMessage());
			}
		}
		
		else if ("template_generate_firco".equalsIgnoreCase(controlName)) {
			String pdfName = "DAO_Firco_Template";
			try{
				DigitalAO.mLogger.debug("generate_firco_temp "+new DAOTemplate().generate_firco_temp(iform, getWorkitemName(iform), pdfName));
				return new DAOTemplate().generate_firco_temp(iform, getWorkitemName(iform), pdfName);
				}
			catch(Exception e){
				DigitalAO.mLogger.debug("generate_firco_temp :"+e.getMessage());
			}
		}
		
		else if ("template_generate_dedupe".equalsIgnoreCase(controlName)) {
			String pdfName = "DEDUPE_Pdf";
			try{
				DigitalAO.mLogger.debug("generate_dedupe_temp "+new DAOTemplate().generate_dedupe_temp(iform, getWorkitemName(iform), pdfName));
				return new DAOTemplate().generate_dedupe_temp(iform, getWorkitemName(iform), pdfName);
				}
			catch(Exception e){
				DigitalAO.mLogger.debug("generate_dedupe_temp :"+e.getMessage());
			}
		}
		
		

		// To load all the exceptions automatically.
		/*
		 * else if (controlName.equalsIgnoreCase("Exception")) {
		 * iform.getDataFromGrid("Q_USR_0_IRBL_EXCEPTION_HISTORY").clear();
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+
		 * ", after clear from Q_USR_0_IRBL_EXCEPTION_HISTORY "
		 * +iform.getDataFromGrid("Q_USR_0_IRBL_EXCEPTION_HISTORY").size());
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+
		 * ", Data Coming in Exceptions is "+data); try { List<List<String>>
		 * lstException = iform .getDataFromDB(
		 * "SELECT ExceptionName,CanRaise,CanClear,CanView FROM USR_0_IRBL_EXCEPTION_MASTER WITH(NOLOCK) where ISACTIVE='Y' and WORKSTEP_NAME='"
		 * +Workstep+"'"); JSONArray jsonArray = new JSONArray(); String value =
		 * ""; for (int i = 0; i < lstException.size(); i++) { JSONObject obj =
		 * new JSONObject(); List<String> arr = (List) lstException.get(i);
		 * value = arr.get(0); DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ",  value : "+value); obj.put("Exception", value); if
		 * ("".equals(strReturn)) { strReturn = strReturn + value + ":" +
		 * arr.get(1) + ":" + arr.get(2) + ":" + arr.get(3); } else { strReturn
		 * = strReturn + "~" + value + ":" + arr.get(1) + ":" + arr.get(2) + ":"
		 * + arr.get(3); } jsonArray.add(obj); } DigitalAO.mLogger.debug(
		 * "WINAME : "+getWorkitemName(iform)+", WSNAME: "
		 * +iform.getActivityName()+", return for exception " +
		 * jsonArray.toJSONString()); // iform.addDataToGrid("table7",
		 * jsonArray); if(!("Rights".equals(data))) {
		 * iform.addDataToGrid("Q_USR_0_IRBL_EXCEPTION_HISTORY", jsonArray); } }
		 * catch (Exception e) { DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", Exception in ExceptionHistory load " + e.getMessage()); } }
		 * //Raising Automatic
		 * exception************************************************************
		 * ******* else if("RaiseAutomaticException".equals(controlName)) { try
		 * { DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+
		 * ", Data Coming in RaiseAutomaticException is "+data); int tablecount
		 * = iform.getDataFromGrid("Q_USR_0_IRBL_EXCEPTION_HISTORY").size();
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+", tablecount "+tablecount); for
		 * (int i = 0; i< tablecount; i++) { String
		 * exceptioName=iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY"
		 * ,i, 1); if(exceptioName.equalsIgnoreCase(data)) { Calendar cal =
		 * Calendar.getInstance(); SimpleDateFormat sdf = new SimpleDateFormat(
		 * "yyyy-MM-dd HH:mm:ss"); String strDate = sdf.format(cal.getTime());
		 * String strRaisedCleared="Raised"; String strNewLine="";
		 * if("".equals(iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY"
		 * ,i, 4)) ||
		 * iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",i, 4)==null)
		 * strNewLine=""; else strNewLine="\n";
		 * 
		 * 
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",i,0,"true");
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",i,2,iform.
		 * getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",i,
		 * 2)+strNewLine+iform.getActivityName());
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",i,3,iform.
		 * getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",i,
		 * 3)+strNewLine+"System");
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",i,4,iform.
		 * getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",i,
		 * 4)+strNewLine+strRaisedCleared);
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",i,5,iform.
		 * getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",i,
		 * 5)+strNewLine+strDate); strReturn=data; DigitalAO.mLogger.debug(
		 * "WINAME : "+getWorkitemName(iform)+", WSNAME: "
		 * +iform.getActivityName()+
		 * ", Successfully Raised Automatic Exception for "+strReturn); } }
		 * 
		 * } catch (Exception e) { DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", Exception in Raising Automatic Exception " + e.getMessage()); }
		 * //*******************************************************************
		 * ********** } //To set values when user manually make changes in
		 * Exception History Window. else
		 * if("raiseClearException".equals(controlName)) { try {
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+", Data for Exception is "
		 * +data); String strCheckUncheck=iform.getTableCellValue(
		 * "Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.parseInt(data), 0);
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+", Exception check uncheck is "
		 * +iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 0)); Calendar cal = Calendar.getInstance();
		 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 * String strDate = sdf.format(cal.getTime()); String
		 * strRaisedCleared=""; String strNewLine="";
		 * if("".equals(iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY"
		 * ,Integer.parseInt(data), 4)) ||
		 * iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 4)==null) strNewLine=""; else strNewLine="\n";
		 * if("true".equals(strCheckUncheck)) strRaisedCleared="Raised"; else
		 * strRaisedCleared="Approved"; DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", iform.getTableCellValue(Q_USR_0_IRBL_EXCEPTION_HISTORY,Integer.parseInt(data), 2) "
		 * +iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 2)+strNewLine+iform.getActivityName());
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data),2,iform.getTableCellValue(
		 * "Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.parseInt(data),
		 * 2)+strNewLine+iform.getActivityName()); DigitalAO.mLogger.debug(
		 * "WINAME : "+getWorkitemName(iform)+", WSNAME: "
		 * +iform.getActivityName()+
		 * ", iform.getTableCellValue(Q_USR_0_IRBL_EXCEPTION_HISTORY,Integer.parseInt(data), 3) "
		 * +iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 3)+strNewLine+iform.getUserName());
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data),3,iform.getTableCellValue(
		 * "Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.parseInt(data),
		 * 3)+strNewLine+iform.getUserName()); DigitalAO.mLogger.debug(
		 * "WINAME : "+getWorkitemName(iform)+", WSNAME: "
		 * +iform.getActivityName()+
		 * ", iform.getTableCellValue(Q_USR_0_IRBL_EXCEPTION_HISTORY,Integer.parseInt(data), 4) "
		 * +iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 4)+strNewLine+strRaisedCleared);
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data),4,iform.getTableCellValue(
		 * "Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.parseInt(data),
		 * 4)+strNewLine+strRaisedCleared); DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", iform.getTableCellValue(Q_USR_0_IRBL_EXCEPTION_HISTORY,Integer.parseInt(data), 5) "
		 * +iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 5)+strNewLine+strDate);
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data),5,iform.getTableCellValue(
		 * "Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.parseInt(data),
		 * 5)+strNewLine+strDate); strReturn = "Cleared"; } catch (Exception e)
		 * { DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+
		 * ", Exception in Service Request drop down load " + e.getMessage()); }
		 * } //To calculate aging in dsays automatically. else
		 * if("AgeingInDays".equals(controlName)) { try { List lstDecisions =
		 * iform .getDataFromDB("select dbo.GetOPSTAT_IRBL('"
		 * +getWorkitemName(iform)+"','"+iform.getActivityName()+
		 * "')  as OPSTAT where 1= 1"); DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", lstDecisions : "+lstDecisions.toString());
		 * 
		 * //String Ageingvalue=lstDecisions.toString();
		 * 
		 * for(int i=0;i<lstDecisions.size();i++) { List<String>
		 * arr1=(List)lstDecisions.get(i); //Ageingvalue=arr1.get(0);
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+", arr1.get(0) : "+arr1.get(0));
		 * 
		 * strReturn=arr1.get(0); }
		 * 
		 * } catch (Exception e) { DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", Exception in AgeingInDays " + e.getMessage()); } } //To load
		 * RM,SM, SOL_ID from RO automatically. else
		 * if("RO".equals(controlName)) { String ROField = (String)
		 * iform.getValue("RO"); DigitalAO.mLogger.debug("ROField -: "+ROField);
		 * try { List lstDecisions = iform .getDataFromDB(
		 * "SELECT RM,SOLID,SM FROM USR_0_IRBL_RMSMRO_Master WITH(NOLOCK) WHERE RO='"
		 * +ROField+"' AND ISACTIVE='Y' ORDER BY RO ASC");
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+", lstDecisions : "
		 * +lstDecisions.toString());
		 * 
		 * String value1=""; String value2=""; String value3="";
		 * iform.setValue("RM",""); iform.setValue("SOL_ID","");
		 * iform.setValue("SM","");
		 * 
		 * for(int i=0;i<lstDecisions.size();i++) { List<String>
		 * arr1=(List)lstDecisions.get(i); value1=arr1.get(0);
		 * value2=arr1.get(1); value3=arr1.get(2); iform.setValue("RM",value1);
		 * iform.setValue("SOL_ID",value2); iform.setValue("SM",value3);
		 * strReturn="RM SM RO SOL_ID Loaded"; } } catch (Exception e) {
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+
		 * ", Exception in RM,SM,SOLID load " + e.getMessage()); } } //To Fetch
		 * Signature details. else if("Signature".equals(controlName)) { try {
		 * DigitalAO.mLogger.debug("Inside Signature"); List lstDecisions =
		 * iform .getDataFromDB(
		 * "SELECT DISTINCT AcctId FROM USR_0_iRBL_InternalExpo_AcctDetails WITH(NOLOCK) WHERE Wi_Name = '"
		 * +getWorkitemName(iform)+"'"); DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", lstDecisions : "+lstDecisions.toString()); for(int
		 * i=0;i<lstDecisions.size();i++) { List<String>
		 * arr1=(List)lstDecisions.get(i); DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", Account ID : "+arr1.get(0));
		 * 
		 * strReturn=strReturn+arr1.get(0)+"@";
		 * //strReturn=strReturn.substring(0,strReturn.length()-1);
		 * DigitalAO.mLogger.debug("strReturn---"+strReturn); }
		 * 
		 * } catch (Exception e) { DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", Exception in Loading Signature !" + e.getMessage()); } } //Count
		 * for raising Nationality Exception else
		 * if("RestrictedValues".equals(controlName)) { int
		 * CRPartygridsize=iform.getDataFromGrid(
		 * "Q_USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS").size(); //int count=0;
		 * String value=""; String StrNationality=""; try { /*for(int
		 * i=0;i<CRPartygridsize;i++) { String Nationality =
		 * iform.getTableCellValue("Q_USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS",
		 * i,30); if(StrNationality.equals("")) StrNationality=Nationality; else
		 * StrNationality=StrNationality+"','"+Nationality; iRBL.mLogger.debug(
		 * "WINAME : "+getWorkitemName(iform)+", WSNAME: "
		 * +iform.getActivityName()+", StrNationality : "+StrNationality);
		 * 
		 * }
		 * 
		 * String query =
		 * "select count(*) from USR_0_IRBL_CountryMaster with(nolock) where countryCode in("
		 * +
		 * " (select NATIONALITY from USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS with(nolock) where wi_name = '"
		 * +getWorkitemName(iform)+
		 * "' and NATIONALITY is not null and NATIONALITY != ''" + " UNION ALL"
		 * +
		 * " select ADDITIONALNATIONALITY from USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS with(nolock) where wi_name = '"
		 * +getWorkitemName(iform)+
		 * "' and ADDITIONALNATIONALITY is not null and ADDITIONALNATIONALITY != ''"
		 * + " UNION ALL" +
		 * " select ADDITIONALNATIONALITY2 from USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS with(nolock) where wi_name = '"
		 * +getWorkitemName(iform)+
		 * "' and ADDITIONALNATIONALITY2 is not null and ADDITIONALNATIONALITY2 != ''"
		 * + " UNION ALL" +
		 * " select ADDITIONALNATIONALITY3 from USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS with(nolock) where wi_name = '"
		 * +getWorkitemName(iform)+
		 * "' and ADDITIONALNATIONALITY3 is not null and ADDITIONALNATIONALITY3 != '' )"
		 * + " )" + " and IsRestricted = 'Y'";
		 * 
		 * DigitalAO.mLogger.debug("Query for Retricted Nationality :"+query);
		 * 
		 * List lstDecisions = iform .getDataFromDB(query);
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+", lstDecisions : "
		 * +lstDecisions.toString());
		 * 
		 * for(int j=0;j<lstDecisions.size();j++) { List<String>
		 * arr1=(List)lstDecisions.get(j);
		 * 
		 * value=arr1.get(0); } DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", Nationality value : "+value);
		 * 
		 * //value=Integer.toString(count); strReturn=value; } catch (Exception
		 * e) { DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+
		 * ", Exception in Nationality Restriction " + e.getMessage()); } }
		 * //Count for raising Demographic Exception else
		 * if("DemographicValues".equals(controlName)) { int
		 * CRPartygridsize=iform.getDataFromGrid(
		 * "Q_USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS").size(); //int count=0;
		 * String value=""; String StrDemographic=""; try { /*for(int
		 * i=0;i<CRPartygridsize;i++) { String Demographic =
		 * iform.getTableCellValue("Q_USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS",
		 * i,30); if(StrDemographic.equals("")) StrDemographic=Demographic; else
		 * StrDemographic=StrDemographic+"','"+Demographic; iRBL.mLogger.debug(
		 * "WINAME : "+getWorkitemName(iform)+", WSNAME: "
		 * +iform.getActivityName()+", StrDemographic : "+StrDemographic); }
		 * String query =
		 * "select count(*) from USR_0_IRBL_CountryMaster with(nolock) where countryCode in("
		 * +
		 * " select DEMOGRAPHIC from USR_0_IRBL_DEMOGRAPHIC_DTLS with(nolock) where WI_NAME = '"
		 * +getWorkitemName(iform)+"'" + " ) and IsDemographic = 'Y'"; List
		 * lstDecisions = iform .getDataFromDB(query); DigitalAO.mLogger.debug(
		 * "WINAME : "+getWorkitemName(iform)+", WSNAME: "
		 * +iform.getActivityName()+", lstDecisions : "
		 * +lstDecisions.toString());
		 * 
		 * for(int j=0;j<lstDecisions.size();j++) { List<String>
		 * arr1=(List)lstDecisions.get(j);
		 * 
		 * value=arr1.get(0); } DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", Demographic value : "+value);
		 * 
		 * //value=Integer.toString(count); strReturn=value; } catch (Exception
		 * e) { DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+
		 * ", Exception in Demographic Restriction " + e.getMessage()); } } //To
		 * fetch the names of all the exceptions to clear them automatically.
		 * else if("ExceptionNames".equals(controlName)) { int
		 * exceptionGridSize=iform.getDataFromGrid(
		 * "Q_USR_0_IRBL_EXCEPTION_HISTORY").size(); String checkNames=""; try {
		 * for(int i=0;i<exceptionGridSize;i++) { String exceptionName =
		 * iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY", i,1);
		 * if(checkNames.equals("")) checkNames=exceptionName; else
		 * checkNames=checkNames+","+exceptionName; DigitalAO.mLogger.debug(
		 * "WINAME : "+getWorkitemName(iform)+", WSNAME: "
		 * +iform.getActivityName()+", checkNames : "+checkNames); }
		 * strReturn=checkNames; } catch (Exception e) {
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+
		 * ", Exception in Loading Exception Names" + e.getMessage()); } } //To
		 * clear all the exceptions automatically. else
		 * if("raiseAutomaticClearException".equals(controlName)) { try {
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+", Data for Exception is "
		 * +data); String strCheckUncheck=iform.getTableCellValue(
		 * "Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.parseInt(data), 0);
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+", Exception check uncheck is "
		 * +iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 0)); Calendar cal = Calendar.getInstance();
		 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 * String strDate = sdf.format(cal.getTime()); String
		 * strRaisedCleared=""; String strNewLine="";
		 * if("".equals(iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY"
		 * ,Integer.parseInt(data), 4)) ||
		 * iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 4)==null) strNewLine=""; else strNewLine="\n";
		 * if("true".equals(strCheckUncheck)) { strRaisedCleared="Approved";
		 * 
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+
		 * ", iform.getTableCellValue(Q_USR_0_IRBL_EXCEPTION_HISTORY,Integer.parseInt(data), 0) "
		 * +iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 0));
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data),0,"false"); DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", iform.getTableCellValue(Q_USR_0_IRBL_EXCEPTION_HISTORY,Integer.parseInt(data), 2) "
		 * +iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 2)+strNewLine+iform.getActivityName());
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data),2,iform.getTableCellValue(
		 * "Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.parseInt(data),
		 * 2)+strNewLine+iform.getActivityName()); DigitalAO.mLogger.debug(
		 * "WINAME : "+getWorkitemName(iform)+", WSNAME: "
		 * +iform.getActivityName()+
		 * ", iform.getTableCellValue(Q_USR_0_IRBL_EXCEPTION_HISTORY,Integer.parseInt(data), 3) "
		 * +iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 3)+strNewLine+iform.getUserName());
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data),3,iform.getTableCellValue(
		 * "Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.parseInt(data),
		 * 3)+strNewLine+iform.getUserName()); DigitalAO.mLogger.debug(
		 * "WINAME : "+getWorkitemName(iform)+", WSNAME: "
		 * +iform.getActivityName()+
		 * ", iform.getTableCellValue(Q_USR_0_IRBL_EXCEPTION_HISTORY,Integer.parseInt(data), 4) "
		 * +iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 4)+strNewLine+strRaisedCleared);
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data),4,iform.getTableCellValue(
		 * "Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.parseInt(data),
		 * 4)+strNewLine+strRaisedCleared); DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", iform.getTableCellValue(Q_USR_0_IRBL_EXCEPTION_HISTORY,Integer.parseInt(data), 5) "
		 * +iform.getTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data), 5)+strNewLine+strDate);
		 * iform.setTableCellValue("Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.
		 * parseInt(data),5,iform.getTableCellValue(
		 * "Q_USR_0_IRBL_EXCEPTION_HISTORY",Integer.parseInt(data),
		 * 5)+strNewLine+strDate);
		 * 
		 * } strReturn = "Cleared"; } catch (Exception e) {
		 * DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+
		 * ", WSNAME: "+iform.getActivityName()+
		 * ", Exception in Service Request drop down load " + e.getMessage()); }
		 * } //To load Third Party Vendor Email ID from Third Party Vendor field
		 * automatically. else if("SetEmail".equals(controlName)) { String
		 * TPVendor = (String) iform.getValue("THIRD_PARTY_VENDOR");
		 * DigitalAO.mLogger.debug("TPVendor : "+TPVendor); try { List
		 * lstDecisions = iform .getDataFromDB(
		 * "SELECT ThirdParty_Email FROM USR_0_IRBL_THIRDPARTY_MASTER WITH(NOLOCK) WHERE THIRDPARTY='"
		 * +TPVendor+"' AND ISACTIVE='Y'"); DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", lstDecisions : "+lstDecisions.toString());
		 * 
		 * String value1=""; iform.setValue("THIRD_PARTY_VENDOR_EMAIL","");
		 * 
		 * for(int i=0;i<lstDecisions.size();i++) { List<String>
		 * arr1=(List)lstDecisions.get(i); value1=arr1.get(0);
		 * iform.setValue("THIRD_PARTY_VENDOR_EMAIL",value1); strReturn=
		 * "Email Loaded"; } } catch (Exception e) { DigitalAO.mLogger.debug(
		 * "WINAME : "+getWorkitemName(iform)+", WSNAME: "
		 * +iform.getActivityName()+
		 * ", Exception in loading Third Party Vendor Email " + e.getMessage());
		 * } } else if("RestrictedValues_Nationality".equals(controlName)) { try
		 * { //String Nationality = (String)
		 * iform.getValue("Q_USR_0_IRBL_SIGNATORY_GRID_DTLS_NATIONALITY");
		 * /*List lstDecisions = iform .getDataFromDB(
		 * "SELECT IsRestricted FROM USR_0_IRBL_CountryMaster WITH(NOLOCK) WHERE countryCode='"
		 * +iform.getValue("Q_USR_0_IRBL_SIGNATORY_GRID_DTLS_NATIONALITY")+
		 * "' and ISACTIVE='Y'");
		 * 
		 * List lstDecisions = iform .getDataFromDB(
		 * "SELECT NationalityStatus FROM USR_0_IRBL_CountryMaster WITH(NOLOCK) WHERE countryCode='"
		 * +iform.getValue("Q_USR_0_IRBL_SIGNATORY_GRID_DTLS_NATIONALITY")+
		 * "' and ISACTIVE='Y' and NationalityStatus IS NOT NULL");
		 * 
		 * 
		 * String value=""; for(int i=0;i<lstDecisions.size();i++) {
		 * List<String> arr1=(List)lstDecisions.get(i); value=arr1.get(0);
		 * /*if("Y".equalsIgnoreCase(value))
		 * iform.setValue("Q_USR_0_IRBL_SIGNATORY_GRID_DTLS_NATIONALITY_STATUS",
		 * "Restricted"); else
		 * iform.setValue("Q_USR_0_IRBL_SIGNATORY_GRID_DTLS_NATIONALITY_STATUS",
		 * "Not Restricted");
		 * 
		 * iform.setValue("Q_USR_0_IRBL_SIGNATORY_GRID_DTLS_NATIONALITY_STATUS",
		 * value);
		 * 
		 * strReturn="Decision Loaded"; }
		 * 
		 * } catch (Exception e) { DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", Exception in Decision drop down load " + e.getMessage()); } } else
		 * if("ProposedTenorValue".equals(controlName)) { String ProposedTenor =
		 * (String) iform.getValue("PROPOSED_TENOR"); DigitalAO.mLogger.debug(
		 * "ProposedTenor : "+ProposedTenor); try { List lstDecisions = iform
		 * .getDataFromDB(
		 * "select Description from USR_0_iRBL_Proposed_Tenor_Master with(nolock) where code ='"
		 * +ProposedTenor+"'");
		 * 
		 * String value=""; for(int i=0;i<lstDecisions.size();i++) {
		 * List<String> arr1=(List)lstDecisions.get(i); value=arr1.get(0);
		 * 
		 * strReturn=value; }
		 * 
		 * } catch (Exception e) { DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", Exception in loading Proposed Tenor value" + e.getMessage()); } }
		 * else if("setProductPriorityLevelBasedOnPriority".equals(controlName))
		 * { String Priority = (String) iform.getValue("PRIORITY"); String
		 * query=""; if("Express".equalsIgnoreCase(Priority.trim())) query =
		 * "update WFINSTRUMENTTABLE set PriorityLevel = 4 where ProcessInstanceID = '"
		 * +getWorkitemName(iform)+"'"; else query =
		 * "update WFINSTRUMENTTABLE set PriorityLevel = 1 where ProcessInstanceID = '"
		 * +getWorkitemName(iform)+"'"; iform.saveDataInDB(query); } else
		 * if("blacklistException".equals(controlName)) { List lstDecisions =
		 * iform.getDataFromDB(
		 * "SELECT MATCH_STATUS FROM USR_0_IRBL_BLACKLIST_GRID_DTLS WITH(nolock) WHERE WI_NAME='"
		 * +getWorkitemName(iform)+"'"); DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+
		 * ", lstDecisions : "+lstDecisions.toString()); String value="";
		 * for(int i=0;i<lstDecisions.size();i++) { List<String>
		 * arr1=(List)lstDecisions.get(i); value=arr1.get(0);
		 * if("true".equalsIgnoreCase(value)) { strReturn=value; break; } }
		 * 
		 * } else if("blacklistException_firco".equals(controlName)) { List
		 * lstDecisions = iform.getDataFromDB(
		 * "SELECT MATCH_STATUS FROM USR_0_IRBL_FIRCO_GRID_DTLS WITH(nolock) WHERE WI_NAME='"
		 * +getWorkitemName(iform)+"'"); DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", lstDecisions : "+lstDecisions.toString());
		 * String value=""; for(int i=0;i<lstDecisions.size();i++) {
		 * List<String> arr1=(List)lstDecisions.get(i); value=arr1.get(0);
		 * if("true".equalsIgnoreCase(value)) { strReturn=value; break; } } }
		 * else if("PEPException".equals(controlName)) { List lstDecisions =
		 * iform.getDataFromDB(
		 * "SELECT ISGOVERNMENTRELATION, COMPANYCATEGORY FROM USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS WITH(nolock) WHERE WI_NAME='"
		 * +getWorkitemName(iform)+"'"); DigitalAO.mLogger.debug("WINAME : "
		 * +getWorkitemName(iform)+", lstDecisions : "+lstDecisions.toString());
		 * for(int i=0;i<lstDecisions.size();i++) { List<String>
		 * arr1=(List)lstDecisions.get(i); if("Y".equalsIgnoreCase(arr1.get(0))
		 * || "Yes".equalsIgnoreCase(arr1.get(0)) ||
		 * "RF".equalsIgnoreCase(arr1.get(1))) { strReturn="true"; break; } } }
		 * //Count for raising Demographic Exception
		 */
		return strReturn;
	}
}
