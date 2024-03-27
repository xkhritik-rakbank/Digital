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
		
		if(decision.equalsIgnoreCase("Reject")){
			iform.setStyle("Decline_reason", "visible", "true");
			iform.setStyle("Decline_reason", "mandatory", "true");
		}else{
			iform.setStyle("Decline_reason", "visible", "false");
			iform.setStyle("Decline_reason", "mandatory", "false");
		}
		
		if (Workstep.equalsIgnoreCase("Firco")){
			if (docType.equalsIgnoreCase("Other_Document")) {
				Digital_CC.mLogger.debug("docType : formLoad " + docType);
				iform.setStyle("AddnalDocs_remarks", "visible", "true");
			} else {
				iform.setValue("AddnalDocs_remarks", "");
				iform.setStyle("AddnalDocs_remarks", "visible", "false");      
			}
		}else if (Workstep.equalsIgnoreCase("Card_Ops")) {
			if(decision.equalsIgnoreCase("Reschedule")){
				iform.setStyle("DocTypeSection", "visible", "true");
			}else{
				iform.setStyle("DocTypeSection", "visible", "false");
			}
		}	




		

		return "";
	}

}
