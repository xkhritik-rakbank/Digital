package com.newgen.iforms.user;

import java.util.List;
import java.util.Map;

import com.newgen.iforms.custom.IFormReference;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;

public class Digital_PL_Click extends Digital_PL_Common {

	String processInstanceID = "";
	String cabinetName = "";
	String sessionId = "";
	String serverIp = "";
	String serverPort = "";

	public String clickEvent(IFormReference iform, String controlName,
			String event, String data) throws Exception {
		this.processInstanceID = getWorkitemName(iform);
		this.cabinetName = getCabinetName(iform);
		this.sessionId = getSessionId(iform);
		this.serverIp = iform.getServerIp();
		this.serverPort = iform.getServerPort();
		Digital_PL.mLogger.debug("DigitalCC_Clicks");
		Digital_PL.mLogger.debug("WINAME : " + getWorkitemName(iform)
				+ ", WSNAME: " + getActivityName(iform) + ", controlName "
				+ controlName + ", data " + data);

		Digital_PL.mLogger.debug("controlName" + controlName);
		String successIndicator = "FAIL";
		String WINAME = getWorkitemName(iform);
		String Workstep = iform.getActivityName();
		String overrideSalary = (String) iform
				.getValue("overrideIncomeFromDectech");
		
		if(controlName.equals("Fetch_FTS_Report"))
		{
			Digital_PL.mLogger.debug("insode FTS_Report");
			 if ("Exception".equalsIgnoreCase(Workstep.trim())) {
				 
				String Query_Old_Salary = "select Salary_date_Month_1,Net_Salary_Month_1,"
						+ "Salary_date_Month_2,Net_Salary_Month_2,Salary_date_Month_3,Net_Salary_Month_3,"
						+ "Salary_date_Month_4,Net_Salary_Month_4,Salary_date_Month_5,Net_Salary_Month_5,"
						+ "Salary_date_Month_6,Net_Salary_Month_6 from NG_DPL_IncomeExpense with "
						+ "(NOLOCK) where Wi_Name ='" + processInstanceID + "'";
				 
				/*String Query_Old_Salary = "select distinct workstep,Net_Salary1,Net_Salary2,Net_Salary3,"
						+ "insertion_date_time from NG_DCC_GR_NetSalaryDetails with (NOLOCK) where "
						+ "Wi_Name ='" + processInstanceID + "' and Workstep = 'Sys_FTS_WI_Update'"+
						 					" union all "+
						 					"select top 1 workstep,Net_Salary1,Net_Salary2,Net_Salary3,insertion_date_time from NG_DCC_GR_NetSalaryDetails with (NOLOCK) where Wi_Name ='" + processInstanceID + "' and Workstep = 'Source_Refer' and (Net_Salary1 is not null and Net_Salary1!='' ) order by insertion_date_time desc";
				 
				*/
				Digital_PL.mLogger.debug("Query_Old_Salary : " + Query_Old_Salary);
				 String extTabDataIPXML = Digital_PL_Common.apSelectWithColumnNames(Query_Old_Salary,cabinetName,sessionId);
				 Digital_PL.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
				 String extTabDataOPXML = Digital_PL_Common.WFNGExecute(extTabDataIPXML, serverIp,
						serverPort, 1);
				 Digital_PL.mLogger.debug("extTabDataOPXML2: " + extTabDataOPXML);
				
				 
				 String oldSalaryDetails = "";
				 XMLParser xmlParserData = new XMLParser(extTabDataOPXML);
				 int iTotalrec = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));
				 if (xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec > 0) {
					 
					 String xmlDataExtTab = xmlParserData.getNextValueOf("Record");
						xmlDataExtTab = xmlDataExtTab.replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");

						NGXmlList objWorkList = xmlParserData.createList("Records", "Record");
						
						for (; objWorkList.hasMoreElements(true); objWorkList.skip(true)) {
							
							String workstep = validateValue(objWorkList.getVal("workstep"));
							String Net_Salary1 = validateValue(objWorkList.getVal("Net_Salary_Month_1"));
							String Net_Salary2 = validateValue(objWorkList.getVal("Net_Salary_Month_2"));
							String Net_Salary3 = validateValue(objWorkList.getVal("Net_Salary_Month_3"));
							Digital_PL.mLogger.debug("new changes Net_Salary1: " + Net_Salary1 + ", Net Salary 2 : " + Net_Salary2 + ", Net Salary 3 : " + Net_Salary3);
							
							Digital_PL.mLogger.debug("new changes before oldSalaryDetails: " + oldSalaryDetails);
							 oldSalaryDetails = oldSalaryDetails+ " Workstep : "+ workstep+ ", Net Salary 1 : "+ Net_Salary1+ ", Net Salary 2 : " + Net_Salary2
								+ ", Net Salary 3 : " + Net_Salary3+"\n ********************************* \n";
							 Digital_PL.mLogger.debug("new changes after oldSalaryDetails: " + oldSalaryDetails);
						}
					 
				 }
				
				 return oldSalaryDetails;
				  
			 }
			 
		}
		else if(controlName.equals("Fetch_AECB_Report")){
			Digital_PL.mLogger.debug("Inside AECB Report");
			String returnValue="";
			List lstReportUrls = iform.getDataFromDB("SELECT top 1 ReportURL FROM ng_dpl_cust_extexpo_Derived WITH(NOLOCK) WHERE Wi_Name='"+getWorkitemName(iform)+"'");
			
			Digital_PL.mLogger.debug("List of URL:"+lstReportUrls);
			String value="";
			for(int i=0;i<lstReportUrls.size();i++)
			{
				List<String> arr1=(List)lstReportUrls.get(i);
				value=arr1.get(0);
				Digital_PL.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", value : "+value);
				returnValue=value;
			}
			
			return returnValue;
		}
		
		else if(controlName.equals("Regen_CAM")){
			Digital_PL.mLogger.debug("Inside NSTP CAM Report");
			return new Digital_PL_GenerateCam().onevent(iform, controlName, data);
			
		}

		return "";

	}
	
	private static String validateValue(String value) {
		if (value != null && !value.equals("") && !value.equalsIgnoreCase("null")) {
			return value.toString();
		}
		return "";
	}

}
