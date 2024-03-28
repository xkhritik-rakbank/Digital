package com.newgen.iforms.user;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.newgen.XMLParser.XMLParser;
import com.newgen.iforms.custom.IFormReference;

public class Digital_PL_IntroDone extends Digital_PL_Common
{
	public String onIntroduceDone(IFormReference iform, String controlName,String event, String data) 
	{	
		String Workstep = iform.getActivityName();
		String strReturn="";
		Digital_PL.mLogger.debug("This is DPL_IntroDone_Event" + Workstep);
		String WIname = getWorkitemName(iform);
		
		
		if("InsertIntoHistory".equals(controlName))
		{
			String WI_CREATE_TIME = (String) iform.getValue("CreatedDateTime");
			

			try {
				Digital_PL.mLogger.debug("InsertIntoHistory : Try ");
			
				JSONArray jsonArray=new JSONArray();
				JSONObject obj=new JSONObject();
				Calendar cal = Calendar.getInstance();
			   			   
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    String strDate = sdf.format(cal.getTime());
			    LocalDate Prospect_Creation_Date =  LocalDate.now();
			    String entry_date_time = (String)  iform.getValue("Entry_Date_Time");
			    
			    
			    Digital_PL.mLogger.debug("strDate: " +strDate);
			    
			    
				obj.put("Workstep",iform.getActivityName());
				obj.put("User Name", iform.getUserName());
				obj.put("Decision",iform.getValue("Decision"));
				obj.put("Decision Date",strDate);
				obj.put("Rejected Reason", iform.getValue("Rejected_Reason"));
				obj.put("Remarks", iform.getValue("Decision_Remarks"));
				
				/*if(Workstep.equalsIgnoreCase("Initiation")){
					obj.put("Entry Date Time", Prospect_Creation_Date);
				}else{
					obj.put("Entry Date Time", entry_date_time);
				}
				*/
				Digital_PL.mLogger.debug("Decision: " +iform.getValue("Decision"));
				Digital_PL.mLogger.debug("Remarks_dec: " +iform.getValue("Rejected_Reason"));
				Digital_PL.mLogger.debug("rejectReason: " +iform.getValue("Decision_Remarks"));
				Digital_PL.mLogger.debug("Entry Date Time" +Prospect_Creation_Date);
				
		
				jsonArray.add(obj);
				iform.addDataToGrid("Decision_History", jsonArray);
				
				Digital_PL.mLogger.debug("jsonArray : "+jsonArray);
			
				strReturn = "INSERTED";
				
				Digital_PL.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", WI Histroy Added Successfully!");
			
				return strReturn;
			} 
			catch (Exception e) {
				Digital_PL.mLogger.debug("Exception in inserting WI History!" + e.getMessage());
			}
			
		
		}
		
		return "";
	}
}