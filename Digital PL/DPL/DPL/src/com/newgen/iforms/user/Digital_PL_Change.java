package com.newgen.iforms.user;

import com.newgen.iforms.custom.IFormReference;

public class Digital_PL_Change extends Digital_PL_Common {

	public String changeEvent(IFormReference iform, String controlName, String event, String data) {
		
		String Workstep = iform.getActivityName();
		Digital_PL.mLogger.debug("In Digital_PL ChangeEvent. Control="+controlName);
		if("Exception".equalsIgnoreCase(Workstep)|| "Refer_to_Compliance".equalsIgnoreCase(Workstep) ){
			String FircoDecision=(String) iform.getValue("FIRCO_Decision");
			if("FIRCO_Decision".equalsIgnoreCase(controlName)){
				if("Document Required".equalsIgnoreCase(FircoDecision)){
					control("Additional_Documents",iform,"visible","true");
					iform.setValue("Notify_DEH_Identifier", "FIRCO");
					iform.setValue("Decision","Submit");
					iform.setStyle("Decision", "disable", "true");
				}
				else if("Negative".equalsIgnoreCase(FircoDecision)){
					iform.setValue("Decision","Reject");
					iform.setStyle("Decision", "disable", "true");
				}
				else{
					iform.clearTable("Additional_Documents");
					control("Additional_Documents",iform,"visible","false");
					iform.setValue("Notify_DEH_Identifier", "");
					iform.setValue("Decision","");
					iform.setStyle("Decision", "disable", "false");
				}
			}
			
				String documentName = (String) iform.getValue("Additional_Documents_Document_Name");
				Digital_PL.mLogger.debug("IdocumentName"+documentName);
				if("Other Document".equalsIgnoreCase(documentName)){
					Digital_PL.mLogger.debug("Other document");
					iform.setStyle("Additional_Documents_Remarks", "visible", "true");
					iform.setValue("Additional_Documents_Remarks", "");
					iform.setStyle("Additional_Documents_Remarks", "mandatory", "true");
				}
				else{
					Digital_PL.mLogger.debug("Not Other document");
					iform.setStyle("Additional_Documents_Remarks", "visible", "false");
					iform.setValue("Additional_Documents_Remarks", "");
					iform.setStyle("Additional_Documents_Remarks", "mandatory", "false");
				}
			
			
			
		}

		
		return "";
	}

}
