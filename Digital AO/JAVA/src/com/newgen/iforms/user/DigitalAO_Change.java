package com.newgen.iforms.user;

import java.util.List;

import com.newgen.iforms.custom.IFormReference;

public class DigitalAO_Change extends DigitalAO_Common {

	public String changeEvent(IFormReference iform, String controlName, String event, String data) {
		// String strReturn="";
		String Workstep = iform.getActivityName();
		String winame=getWorkitemName(iform);
		String MOI_employer="" ;
		String AECB_employer="" ;
		String finacle_employer="";
		String CustomerInput_employerName_freeTxt="";
		String CustomerInput_employerName_Drpdwn="";
		String employer_source_deh="";
		String Company_employer_name_deh="";
		String employer_code_deh="";

		DigitalAO.mLogger.debug("DigitalAO_Change");
		DigitalAO.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + getActivityName(iform)
				+ ", controlName " + controlName + ", data " + data);
		
		String highRisk = (String) iform.getValue("High_risk");
		if (Workstep.equalsIgnoreCase("compliance")) {
			DigitalAO.mLogger.debug("High_risk :  on change : compliance");
			
			iform.setStyle("DigitalAO_BackgroundInfo", "visible", "true");
			iform.setStyle("background_information", "visible", "true");
			iform.setStyle("template_generate", "visible", "true");
			iform.setStyle("template_generate", "disable", "false");

			if (highRisk.equalsIgnoreCase("Y") || highRisk.equalsIgnoreCase("Yes")) {
				iform.setStyle("DigitalAO_BackgroundInfo", "disable", "false");
				iform.setStyle("background_information", "disable", "false");
			} else {
				iform.setStyle("DigitalAO_BackgroundInfo", "disable", "true");
				iform.setStyle("background_information", "disable", "true");
				iform.setStyle("template_generate", "disable", "false");
			}
		} else {
			iform.setStyle("DigitalAO_BackgroundInfo", "visible", "false");
			iform.setStyle("background_information", "visible", "false");
			iform.setStyle("template_generate", "visible", "false");
			iform.setStyle("template_generate", "disable", "true");
		}

		// Remarks
		String decision = (String) iform.getValue("Decision");
		if (decision.equalsIgnoreCase("Submit"))
		{
			DigitalAO.mLogger.debug("Decision : on change " + decision);
			iform.setStyle("Remarks_dec", "disable", "true");
			iform.setStyle("Remarks_dec", "mandatory", "false");
			iform.setStyle("rejectReason", "disable", "true");
			iform.setStyle("rejectReason", "mandatory", "false");
		}
		else if(decision.equalsIgnoreCase("Approve"))
		{
			iform.setStyle("Remarks_dec", "disable", "false");
			iform.setStyle("Remarks_dec", "mandatory", "false");
			iform.setStyle("rejectReason", "disable", "true");
			iform.setStyle("rejectReason", "mandatory", "false");
		}
		else if(decision.equalsIgnoreCase("Reject"))
		{
			DigitalAO.mLogger.debug("Decision :  RejectReason on change" + decision);
			iform.setStyle("Remarks_dec", "disable", "false");
			iform.setStyle("Remarks_dec", "mandatory", "false");
			iform.setStyle("rejectReason", "disable", "false");
			iform.setStyle("rejectReason", "mandatory", "true");
		}
		else if(decision.equalsIgnoreCase("Account Closed"))
		{
			DigitalAO.mLogger.debug("Decision :  RejectReason" + decision);
			iform.setStyle("Remarks_dec", "disable", "false");
			iform.setStyle("Remarks_dec", "mandatory", "false");
			iform.setStyle("rejectReason", "mandatory", "true");
			setControlValue("rejectReason", "001", iform);		
			iform.setStyle("rejectReason", "disable", "true");
		}
		else 
		{
			iform.setStyle("Remarks_dec", "disable", "false");
			iform.setStyle("Remarks_dec", "mandatory", "false");
			iform.setStyle("rejectReason", "disable", "true");
			iform.setStyle("rejectReason", "mandatory", "false");
		}
		
		// PEP
		DigitalAO.mLogger.debug("DigitalAO_Change : PEP :");
		if (Workstep.equalsIgnoreCase("operations"))
		{
			iform.setStyle("PEP", "disable", "false");
			String Fatca_PEP = (String) iform.getValue("PEP");
			DigitalAO.mLogger.debug("DigitalAO_Change : PEP :");
			DigitalAO.mLogger.debug("PEP :  formload " + Fatca_PEP);
			if (Fatca_PEP.trim().equalsIgnoreCase("YES") || Fatca_PEP.trim().equalsIgnoreCase("Y") ) {
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
			//vinayak 14-09-23 jira 3310
			
			
			
			if(controlName.equalsIgnoreCase("is_modify_employer_change"))
			{			
				String is_modify_employer_val = (String) iform.getValue("is_modify_employer");
				DigitalAO.mLogger.debug("is_modify_employer_val : " + is_modify_employer_val);
				
				String Query = "select MOI_employer,AECB_employer,finacle_employer,CustomerInput_employerName_freeTxt,CustomerInput_employerName_Drpdwn,employer_source_deh,Company_employer_name_deh,employer_code_deh from NG_DAO_EXTTABLE with(nolock) where WI_name='"+winame+"'";
				DigitalAO.mLogger.debug("Query : " + Query);
				List<List<String>> result = iform.getDataFromDB(Query);
				if (!result.isEmpty()) 
				{
					 MOI_employer = result.get(0).get(0);
					 AECB_employer = result.get(0).get(1);
					 finacle_employer = result.get(0).get(2);
					 CustomerInput_employerName_freeTxt = result.get(0).get(3);
					 CustomerInput_employerName_Drpdwn = result.get(0).get(4);
					 employer_source_deh = result.get(0).get(5);
					 Company_employer_name_deh= result.get(0).get(6);
					 employer_code_deh=result.get(0).get(7);
					DigitalAO.mLogger.debug("MOI_employer : " + MOI_employer +" |AECB_employer : " + AECB_employer +" |finacle_employer : " + finacle_employer+" |CustomerInput_employerName_freeTxt : " + CustomerInput_employerName_freeTxt+" |CustomerInput_employerName_Drpdwn : " + CustomerInput_employerName_Drpdwn+" |employer_source : " + employer_source_deh +" |employer_code_deh : " + employer_code_deh +" |Company_employer_name_deh : " + Company_employer_name_deh);
				}
				if("true".equalsIgnoreCase(is_modify_employer_val))
				{
					iform.setStyle("picklist_employer_name", "disable", "false");
					iform.setStyle("is_notPicklistEmployer", "visible", "true");
					iform.setStyle("is_notPicklistEmployer", "disable", "false");					
					iform.setStyle("CustomerInput_other_employerName", "disable", "true");
					iform.setValue("employer_source","Ops Updated");
					iform.setValue("Company_employer_name","");
					iform.setValue("employer_code","");
				}
				else if("false".equalsIgnoreCase(is_modify_employer_val))
				{
					iform.setStyle("picklist_employer_name", "disable", "true");
					iform.setStyle("is_notPicklistEmployer", "disable", "true");
					iform.setStyle("CustomerInput_other_employerName", "visible", "false");
					iform.setStyle("is_notPicklistEmployer", "visible", "false");
					iform.setValue("picklist_employer_name", "");
					iform.setValue("is_notPicklistEmployer", "false");
					iform.setValue("CustomerInput_other_employerName", "");
					//set values if check box is unclicked
					iform.setValue("MOI_employer", MOI_employer);
					iform.setValue("AECB_employer", AECB_employer);
					iform.setValue("finacle_employer", finacle_employer);
					iform.setValue("CustomerInput_employerName_Drpdwn", CustomerInput_employerName_Drpdwn);
					iform.setValue("CustomerInput_employerName_freeTxt", CustomerInput_employerName_freeTxt);
					iform.setValue("employer_source",employer_source_deh);
					iform.setValue("Company_employer_name",Company_employer_name_deh);
					iform.setValue("employer_code",employer_code_deh);
				}
			}
			
			if(controlName.equalsIgnoreCase("is_notPicklistEmployer_change"))
			{			
				String is_notPicklistEmployer_val = (String) iform.getValue("is_notPicklistEmployer");
				DigitalAO.mLogger.debug("is_notPicklistEmployer_val : " + is_notPicklistEmployer_val);
				if("true".equalsIgnoreCase(is_notPicklistEmployer_val))
				{
					iform.setStyle("CustomerInput_other_employerName", "visible", "true");
					iform.setStyle("CustomerInput_other_employerName", "disable", "false");
					iform.setStyle("picklist_employer_name", "disable", "true");
					iform.setValue("picklist_employer_name", "");
					iform.setValue("Company_employer_name", "");
					iform.setValue("employer_code", "");
				}
				else if("false".equalsIgnoreCase(is_notPicklistEmployer_val))
				{
					iform.setStyle("CustomerInput_other_employerName", "visible", "false");
					iform.setStyle("picklist_employer_name", "disable", "false");
					iform.setValue("CustomerInput_other_employerName", "");
					iform.setValue("Company_employer_name", "");
				}
			}
			
			if(controlName.equalsIgnoreCase("picklist_employer_name_change"))
			{
				String picklist_employer_name_val = (String) iform.getValue("picklist_employer_name");
				DigitalAO.mLogger.debug("picklist_employer_name_val : " + picklist_employer_name_val);
				iform.setValue("Company_employer_name", picklist_employer_name_val);
				String AlocQuery = "select EMPLOYER_CODE from NG_RLOS_ALOC_OFFLINE_DATA  with(nolock) where EMPR_NAME='"+picklist_employer_name_val+"'";
				DigitalAO.mLogger.debug("Query : " + AlocQuery);
				List<List<String>> alocResult = iform.getDataFromDB(AlocQuery);
				if (!alocResult.isEmpty()) 
				{
					 
					 String aloc_employer_code=alocResult.get(0).get(0);
					DigitalAO.mLogger.debug("aloc_employer_code : " + aloc_employer_code );
					iform.setValue("employer_code",aloc_employer_code);
				}
				
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
		
		if("risk_score_change".equalsIgnoreCase(controlName))
		{
			DigitalAO.mLogger.debug("Empty the risk score when fields values change:");
			iform.setStyle("risk_score", "disable", "false");
			
			setControlValue("risk_score", "", iform);
			
			iform.setStyle("risk_score", "disable", "true");
			
			return "Empty the risk score";
		}
/*
		DigitalAO.mLogger.debug("DigitalAO_EmpDetails: Disable on change");

		iform.setStyle("DigitalAO_EmpDetails", "visible", "false");
		iform.setStyle("DigitalAO_CompDetails", "visible", "false");
		iform.setStyle("company_detail", "visible", "false");
		
		String employement_type = (String) iform.getValue("employement_type");
		DigitalAO.mLogger.debug("DigitalAO_EmpDetails: employement_type: on change" + employement_type);
		if (employement_type.trim().equalsIgnoreCase("Salaried")) {
			iform.setStyle("DigitalAO_EmpDetails", "visible", "true");
			iform.setStyle("DigitalAO_EmpDetails", "disable", "false");

		} else if (employement_type.trim().equalsIgnoreCase("Self Employed")) {
			iform.setStyle("DigitalAO_CompDetails", "visible", "true");
			iform.setStyle("company_detail", "visible", "true");
			iform.setStyle("DigitalAO_CompDetails", "disable", "false");
			iform.setStyle("company_detail", "disable", "false");
		} else {
			iform.setStyle("DigitalAO_EmpDetails", "visible", "false");
			iform.setStyle("DigitalAO_CompDetails", "visible", "false");
			iform.setStyle("company_detail", "visible", "false");
		}
*/

		return "";
	}

}
