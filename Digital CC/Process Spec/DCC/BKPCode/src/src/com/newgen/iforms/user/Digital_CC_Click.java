package com.newgen.iforms.user;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.newgen.iforms.custom.IFormReference;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Digital_CC_Click extends Digital_CC_Common {

	public String clickEvent(IFormReference iform, String controlName, String event, String data) throws Exception
			 {
		Digital_CC.mLogger.debug("DigitalCC_Clicks");
		Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + getActivityName(iform)
				+ ", controlName " + controlName + ", data " + data);
		// TODO Auto-generated method stub
		/*
		 * if(controlName.equals("PDFGenerate")) return new
		 * DigitalCC_GeneratePDF().onclickevent(iform, controlName, data); else
		 * if(controlName.equals("DecTechCall")) return new
		 * DigitalCC_DecTechCall().onevent(iform, controlName, data); else
		 * if(controlName.equals("ViewAECBReport") ||
		 * controlName.equals("btn_View_Signature")) return new
		 * DigitalCC_Integration().onclickevent(iform, controlName, data); else
		 */
		Digital_CC.mLogger.debug("controlName" + controlName);
		String successIndicator = "FAIL";
		String WINAME = getWorkitemName(iform);
		String Workstep = iform.getActivityName();
		String overrideSalary = (String) iform.getValue("overrideIncomeFromDectech");
		
		
		// code by deepanshu prashar for template generating

		/*if (controlName.equalsIgnoreCase("template_generate")) 
		{
			 return new DAOTemplate().clickEvent(iform, "template_generate",data);
		}
		if(controlName.equalsIgnoreCase("Risk_score_trigger"))
		{
			DigitalCC.mLogger.debug("RISK_SCORE_DETAILS : Click: ");
			return new DigitalCC_Integration().MQ_connection_response(iform, controlName, data);
			
		}*/
		//added by gaurav070922
		if(controlName.equals("overrideIncomeFromDectech")){
			 if ("Exceptions".equalsIgnoreCase(Workstep.trim())) {
				 Digital_CC.mLogger.debug(" overrideSalary on click" + overrideSalary);
					if(overrideSalary.equalsIgnoreCase("true")){
						Digital_CC.mLogger.debug(" overrideSalary on click before enable" + overrideSalary);
						iform.setValue("Net_Salary1", "");
						iform.setValue("Net_Salary2", "");
						iform.setValue("Net_Salary3", "");
						iform.setStyle("Net_Salary1", "disable", "false");
						iform.setStyle("Net_Salary2", "disable", "false");
						iform.setStyle("Net_Salary3", "disable", "false");
						Digital_CC.mLogger.debug(" overrideSalary on click after enable" + overrideSalary);
					}else{
						iform.setStyle("Net_Salary1", "disable", "true");
						iform.setStyle("Net_Salary2", "disable", "true");
						iform.setStyle("Net_Salary3", "disable", "true");
					}
				}
			}
		if(controlName.equals("BankingDtlsSave")){
			 if ("Exceptions".equalsIgnoreCase(Workstep.trim())) {
				 iform.setStyle("Net_Salary1", "disable", "true");
				 iform.setStyle("Net_Salary2", "disable", "true");
				 iform.setStyle("Net_Salary3", "disable", "true");
				/* String Net_Salary1 = (String) iform.getValue("Net_Salary1");
				 String Net_Salary2 = (String) iform.getValue("Net_Salary2");	
				 String Net_Salary3 = (String) iform.getValue("Net_Salary3");	
				 String Net_Salary4 = (String) iform.getValue("Net_Salary4");	
				 String Net_Salary5 = (String) iform.getValue("Net_Salary5");	
				 String Net_Salary6 = (String) iform.getValue("Net_Salary6");	
				 String Net_Salary7 = (String) iform.getValue("Net_Salary7");	
				 Digital_CC.mLogger.debug(" Net_Salary1 before inserting into table" + Net_Salary1);
				 
				 JSONArray jsonArray = new JSONArray();
				 JSONObject obj = new JSONObject();
				 
				 obj.put("", Net_Salary1);
				 obj.put("", Net_Salary2);
				 obj.put("", Net_Salary3);
				 obj.put("", Net_Salary4);
				 obj.put("", Net_Salary5);
				 obj.put("", Net_Salary6);
				 obj.put("", Net_Salary7);
				 
				 jsonArray.add(obj);
				 iform.addDataToGrid("", jsonArray);*/
				 
				}
			}
		
		 
		
		
		//Added by Kamran28072022
		if(controlName.equals("Fetch_Manual_Dectech")){
			return new Digital_CC_DecTechCall().onevent(iform, controlName, data);
			
		}
		//added by gaurav 19092022
		if(controlName.equals("GenerateCam")){
			return new Digital_CC_CamGenerateCall().onevent(iform, controlName, data);
		}
		
		//Added by Kamran28072022
		if(controlName.equals("Fetch_AECB_Report")){
			return new Digital_CC_ViewAECB().onevent(iform, controlName, data);
			
		}
		
		//
		return successIndicator;
	}
}
