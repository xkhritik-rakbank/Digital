package com.newgen.iforms.user;

import java.util.List;
import java.util.Map;

import com.newgen.iforms.custom.IFormReference;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;

public class Digital_CC_Click extends Digital_CC_Common {

	String processInstanceID = "";
	String cabinetName = "";
	String sessionId = "";
	String serverIp = "";
	String serverPort = "";
	
	 
		
	
	public String clickEvent(IFormReference iform, String controlName, String event, String data) throws Exception
			 {
		this.processInstanceID = getWorkitemName(iform);
		this.cabinetName =getCabinetName(iform);
		this.sessionId = getSessionId(iform);
		this.serverIp = iform.getServerIp();
		this.serverPort = iform.getServerPort();
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
		if(controlName.equals("overrideIncomeFromDectech"))
		{
			 if ("Exceptions".equalsIgnoreCase(Workstep.trim())) {
				 Digital_CC.mLogger.debug(" overrideSalary on click" + overrideSalary);
					if(overrideSalary.equalsIgnoreCase("true")){
						Digital_CC.mLogger.debug(" overrideSalary on click before enable" + overrideSalary);
						/*iform.setValue("Net_Salary1", "");
						iform.setValue("Net_Salary2", "");
						iform.setValue("Net_Salary3", "");*/
						iform.setStyle("Net_Salary1", "disable", "false");
						iform.setStyle("Net_Salary2", "disable", "false");
						iform.setStyle("Net_Salary3", "disable", "false");
						iform.setStyle("uw_income", "disable", "false");
						iform.setStyle("Net_salary1_date", "disable", "false");

						Digital_CC.mLogger.debug(" overrideSalary on click after enable" + overrideSalary);
					}else{
						iform.setStyle("Net_Salary1", "disable", "true");
						iform.setStyle("Net_Salary2", "disable", "true");
						iform.setStyle("Net_Salary3", "disable", "true");
						iform.setStyle("uw_income", "disable", "true");
						iform.setStyle("Net_salary1_date", "disable", "true");
						
					}
				}
			}
		
		else if(controlName.equals("Old_Salary"))
		{
			 if ("Exceptions".equalsIgnoreCase(Workstep.trim())) {
				 
				// String Query_Old_Salary = "select Top 1 Net_Salary1,Net_Salary2,Net_Salary3 from NG_DCC_GR_NetSalaryDetails with (NOLOCK)  where Wi_Name ='" + processInstanceID + "' order by insertion_date_time desc";
				 String Query_Old_Salary = "select distinct workstep,Net_Salary1,Net_Salary2,Net_Salary3,insertion_date_time from NG_DCC_GR_NetSalaryDetails with (NOLOCK) where Wi_Name ='" + processInstanceID + "' and Workstep = 'Sys_FTS_WI_Update'"+
						 					" union all "+
						 					"select top 1 workstep,Net_Salary1,Net_Salary2,Net_Salary3,insertion_date_time from NG_DCC_GR_NetSalaryDetails with (NOLOCK) where Wi_Name ='" + processInstanceID + "' and Workstep = 'Source_Refer' and (Net_Salary1 is not null and Net_Salary1!='' ) order by insertion_date_time desc";
				 Digital_CC.mLogger.debug("Query_Old_Salary : " + Query_Old_Salary);
				 String extTabDataIPXML = Digital_CC_Common.apSelectWithColumnNames(Query_Old_Salary,cabinetName,sessionId);
				 Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
				 String extTabDataOPXML = Digital_CC_Common.WFNGExecute(extTabDataIPXML, serverIp,
						serverPort, 1);
				 Digital_CC.mLogger.debug("extTabDataOPXML2: " + extTabDataOPXML);
				 
				/* String Net_Salary1 = xmlParserData.getValueOf("Net_Salary1");
				 Digital_CC.mLogger.debug("Net_Salary1: " + Net_Salary1);
				 
				 String Net_Salary2 = xmlParserData.getValueOf("Net_Salary2");
				 Digital_CC.mLogger.debug("Net_Salary2: " + Net_Salary2);
				 
				 String Net_Salary3 = xmlParserData.getValueOf("Net_Salary3");
				 Digital_CC.mLogger.debug("Net_Salary3: " + Net_Salary3);*/
				 
				 String oldSalaryDetails = "";
				XMLParser xmlParserData = new XMLParser(extTabDataOPXML);
				 int iTotalrec = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));
				 if (xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec > 0) {
					 
					 String xmlDataExtTab = xmlParserData.getNextValueOf("Record");
						xmlDataExtTab = xmlDataExtTab.replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");

						NGXmlList objWorkList = xmlParserData.createList("Records", "Record");
						
						for (; objWorkList.hasMoreElements(true); objWorkList.skip(true)) {
							
							String workstep = validateValue(objWorkList.getVal("workstep"));
							String Net_Salary1 = validateValue(objWorkList.getVal("Net_Salary1"));
							String Net_Salary2 = validateValue(objWorkList.getVal("Net_Salary2"));
							String Net_Salary3 = validateValue(objWorkList.getVal("Net_Salary3"));
							Digital_CC.mLogger.debug("new changes Net_Salary1: " + Net_Salary1 + ", Net Salary 2 : " + Net_Salary2 + ", Net Salary 3 : " + Net_Salary3);
							
							Digital_CC.mLogger.debug("new changes before oldSalaryDetails: " + oldSalaryDetails);
							 oldSalaryDetails = oldSalaryDetails+ " Workstep : "+ workstep+ ", Net Salary 1 : "+ Net_Salary1+ ", Net Salary 2 : " + Net_Salary2
								+ ", Net Salary 3 : " + Net_Salary3+"\n ********************************* \n";
							 Digital_CC.mLogger.debug("new changes after oldSalaryDetails: " + oldSalaryDetails);
						}
					 
				 }
				
				 return oldSalaryDetails;
				  
			 }
		}
		else if(controlName.equals("BankingDtlsSave"))
		{
			 if ("Exceptions".equalsIgnoreCase(Workstep.trim())) {
				 iform.setStyle("Net_Salary1", "disable", "true");
				 iform.setStyle("Net_Salary2", "disable", "true");
				 iform.setStyle("Net_Salary3", "disable", "true");
				 
				 iform.setStyle("Net_salary1_date", "disable", "true");
				 iform.setStyle("uw_income", "disable", "true");
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
		else if (controlName.equals("Fetch_Manual_Dectech")) {

			return new Digital_CC_DecTechCall().onevent(iform, controlName, data);

		}
		//Added by Kamran06042023
		else if(controlName.equals("getDocPassword"))
		{
			return new Digital_CC_GetDocPassword().onevent(iform, controlName, data);
			
		}
		//added by gaurav 19092022
		
		else if (controlName.equals("GenerateCam")) {
			String FirstName = (String) iform.getValue("FirstName");
			String LastName = (String) iform.getValue("LastName");

			if ("".equalsIgnoreCase(FirstName)) {
				return "First Name is Mandatory";
			} else if ("".equalsIgnoreCase(LastName)) {
				return "Last Name is Mandatory";
			} else if (!"".equalsIgnoreCase(FirstName) && !"".equalsIgnoreCase(LastName)) {
				return new Digital_CC_CamGenerateCall().onevent(iform, controlName, data);
			}

		}
		
		//Added by Kamran28072022
		else if(controlName.equals("Fetch_AECB_Report"))
		{
			return new Digital_CC_ViewAECB().onevent(iform, controlName, data);
			
		}
		
		//
		return successIndicator;
	}
	private static String validateValue(String value) {
		if (value != null && !value.equals("") && !value.equalsIgnoreCase("null")) {
			return value.toString();
		}
		return "";
	}
}

