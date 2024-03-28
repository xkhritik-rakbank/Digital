package com.newgen.iforms.user;

import com.newgen.iforms.custom.IFormReference;

public class Digital_CC_Change extends Digital_CC_Common {

	public String changeEvent(IFormReference iform, String controlName, String event, String data) {
		// String strReturn="";
		String Workstep = iform.getActivityName();

		Digital_CC.mLogger.debug("Digital_CC_Change");
		Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + getActivityName(iform)
		+ ", controlName " + controlName + ", data " + data);

		//1708 added by gaurav
		String docType = (String) iform.getValue("AddnalDocs_doctype");
		String decision = (String) iform.getValue("Decision");
		String declineReason = (String) iform.getValue("Decline_reason");
		
		if("Exceptions".equalsIgnoreCase(Workstep.trim()) && decision.equalsIgnoreCase("Reject")){
			iform.setStyle("Decline_reason", "visible", "true");
			iform.setStyle("Decline_reason", "disable", "false");
			iform.setStyle("Decline_reason", "mandatory", "true");
			iform.setStyle("SalaryDocReq", "visible", "false");
			iform.setStyle("SalaryDocReq", "disable", "true");
			iform.setStyle("SalaryDocReq", "mandatory", "false");
		}
		//2703 by kamran for new decision Refer
		else if("Exceptions".equalsIgnoreCase(Workstep.trim()) && decision.equalsIgnoreCase("Refer")){
			iform.setStyle("Decline_reason", "visible", "false");
			iform.setStyle("Decline_reason", "disable", "true");
			iform.setStyle("Decline_reason", "mandatory", "false");
			iform.setStyle("SalaryDocReq", "visible", "true");
			iform.setStyle("SalaryDocReq", "disable", "false");
			iform.setStyle("SalaryDocReq", "mandatory", "true");
		}
		else{
			iform.setStyle("Decline_reason", "visible", "false");
			iform.setStyle("Decline_reason", "disable", "true");
			iform.setStyle("Decline_reason", "mandatory", "false");
			iform.setStyle("SalaryDocReq", "visible", "false");
			iform.setStyle("SalaryDocReq", "disable", "true");
			iform.setStyle("SalaryDocReq", "mandatory", "false");
		}
		
		if(declineReason.equalsIgnoreCase("Others")){
			iform.setStyle("Remarks", "mandatory", "true");
		}else{
			iform.setStyle("Remarks", "mandatory", "false");
		}
		
		if (Workstep.equalsIgnoreCase("Firco")){
			if (docType.equalsIgnoreCase("Other_Document")) {
				Digital_CC.mLogger.debug("docType : formLoad " + docType);
				iform.setStyle("AddnalDocs_remarks", "visible", "true");
				iform.setStyle("AddnalDocs_remarks", "mandatory", "true");
			} else {
				iform.setValue("AddnalDocs_remarks", "");
				iform.setStyle("AddnalDocs_remarks", "visible", "false");
				iform.setStyle("AddnalDocs_remarks", "mandatory", "false");
			}
		}else if (Workstep.equalsIgnoreCase("Card_Ops")) {
			if(decision.equalsIgnoreCase("Reschedule")){
				iform.setStyle("DocTypeSection", "visible", "true");
				iform.setStyle("isNameAmended", "visible", "true");//added by om.tiwari on 17/0/23 for PDSC-275
				iform.setStyle("AmendedCustName", "visible", "true");//added by om.tiwari on 17/0/23 for PDSC-275
				iform.setStyle("isNameAmended", "disable", "false");
			}else{
				iform.setStyle("DocTypeSection", "visible", "false");
				iform.setStyle("isNameAmended", "visible", "false");//added by om.tiwari on 17/0/23 for PDSC-275
				iform.setStyle("AmendedCustName", "visible", "false");//added by om.tiwari on 17/0/23 for PDSC-275
			}
			
			//added by om.tiwari on 17/0/23 for PDSC-275
			if("isNameAmended".equalsIgnoreCase(controlName))
			{
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
					
					iform.setStyle("AmendedCustName", "disable", "false");
					iform.setStyle("AmendedCustName", "mandatory", "true");
				}
				else
				{
					iform.setStyle("AmendedCustName", "mandatory", "false");
					iform.setStyle("AmendedCustName", "disable", "true");
				}
			}
			//till here
		}
		else if (Workstep.equalsIgnoreCase("DCC_Experience")) {
			if(decision.equalsIgnoreCase("Rescheduled")){
				iform.setStyle("DocTypeSection", "visible", "true");
			}else{
				iform.setStyle("DocTypeSection", "visible", "false");
			}
		}
		//Added by Kamran 02052023 -RM Access to Upload FIRCO doc at WI Update --Sys_WI_Update
		else if("Sys_WI_Update".equalsIgnoreCase(Workstep.trim())){
			
			if(decision.equalsIgnoreCase("Resubmit")){
				iform.setStyle("Remarks", "mandatory", "true");
				iform.setStyle("Remarks", "maxlength", "100");
			}
			else{
				iform.setStyle("Remarks", "mandatory", "false");
				iform.setStyle("Remarks", "maxlength", "250");
			}
		}
		
		//rubi
		if("Net_salary1_date".equalsIgnoreCase(controlName)|| "uw_income".equalsIgnoreCase(controlName)){
			Digital_CC.mLogger.debug(" Net_Salary1 control name" + controlName);
			 iform.setStyle("BankingDtlsSave", "disable", "false");	
		}

		
		return "";
	}

}
