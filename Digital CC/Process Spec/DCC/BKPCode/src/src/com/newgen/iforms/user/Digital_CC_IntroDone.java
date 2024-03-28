package com.newgen.iforms.user;

import java.util.Calendar;
import java.util.List;
import java.text.SimpleDateFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.newgen.iforms.custom.IFormReference;

public class Digital_CC_IntroDone extends Digital_CC_Common
{
	public String onIntroduceDone(IFormReference iform, String controlName,String event, String data)
	{
		String strReturn="";
		Digital_CC.mLogger.debug("This is DAO_IntroDone_Event");
		if("InsertIntoHistory".equals(controlName))
		{
			try {
				Digital_CC.mLogger.debug("InsertIntoHistory : Try ");
			
			/*
				Digital_CC.mLogger.debug("Reject Reasons Grid Length is "+data);
				String strRejectReasons="";
				String strRejectCodes = "";
				for(int p=0;p<Integer.parseInt(data);p++)
				{
					/*if(strRejectReasons=="")
						strRejectReasons=iformObj.getTableCellValue("REJECT_REASON_GRID",p,0);
					else
						strRejectReasons=strRejectReasons+"#"+iformObj.getTableCellValue("REJECT_REASON_GRID",p,0);*/
					
		/*			String completeReason = null;
					completeReason = iform.getTableCellValue("REJECT_REASON_GRID", p, 0);
					Digital_CC.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", Complete Reject Reasons" + completeReason);
					
					if (strRejectReasons == "")
					{						
						if(completeReason.indexOf("-")>-1)
						{
							strRejectCodes=completeReason.substring(0,completeReason.indexOf("-")).replace("(", "").replace(")", "");
							Digital_CC.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", Reject Reasons code" + strRejectCodes);
							strRejectReasons=completeReason.substring(completeReason.indexOf("-")+1);
							Digital_CC.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", Reject Reasons" + strRejectReasons);
						}
						else
						{
							strRejectReasons=completeReason;
							Digital_CC.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", Reject Reasons else block" + strRejectReasons);
						}
					}	
					else
					{
						Digital_CC.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", Reject Reasons 1" + strRejectReasons);						
						if(completeReason.indexOf("-")>-1)
						{
							strRejectCodes=strRejectCodes+"#"+completeReason.substring(0,completeReason.indexOf("-")).replace("(", "").replace(")", "");
							Digital_CC.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", Reject Reasons code" + strRejectCodes);
							strRejectReasons=strRejectReasons+"#"+completeReason.substring(completeReason.indexOf("-")+1);
							Digital_CC.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", Reject Reasons" + strRejectReasons);
						}
						else
						{
							strRejectReasons=strRejectReasons+"#"+completeReason;
							Digital_CC.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", Reject Reasons else block2" + strRejectReasons);
						}
						
						Digital_CC.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", Reject Reasons 2" + strRejectReasons);
					}
					
				}
				/*String EntryDateTime = iform.getValue("EntryDateTime").toString();
				String newEntryDateTime="";
				if(!EntryDateTime.equals(""))
				{
					String[] a = EntryDateTime.split(" ");
					String[] d = a[0].split("-");
					String[] t = a[1].split(":");
					
					//Added for handling month***************
					String[] month_array={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
					String[] Integer_array={"01","02","03","04","05","06","07","08","09","10","11","12"};
					for (int z=0;z<month_array.length;z++)
					{
						if(d[1].indexOf(month_array[z]) != -1)
							d[1]=Integer_array[z];
					}
					//************************************
					
					newEntryDateTime=d[2]+'/'+d[1]+'/'+d[0]+' '+t[0]+':'+t[1]+':'+t[2];
					
				}*/
				
			//	Digital_CC.mLogger.debug("Final reject reasons are "+strRejectReasons);
				JSONArray jsonArray=new JSONArray();
				JSONObject obj=new JSONObject();
				Calendar cal = Calendar.getInstance();
			   // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");			   
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    String strDate = sdf.format(cal.getTime());
			    
			    Digital_CC.mLogger.debug("strDate: " +strDate);
			    
				/*obj.put("Date Time",strDate);
				obj.put("Workstep",iform.getActivityName());
				obj.put("User Name", iform.getUserName());
				obj.put("Decision",iform.getValue("Decision"));
				obj.put("Remarks", iform.getValue("Remarks_dec"));
				obj.put("Reject reasons", iform.getValue("rejectReason"));*/
			    
			    obj.put("Decision Date",strDate);
				obj.put("Workstep",iform.getActivityName());
				obj.put("User Name", iform.getUserName());
				obj.put("Decision",iform.getValue("Decision"));
				obj.put("Remarks", iform.getValue("Remarks"));
				obj.put("Rejected Reason", iform.getValue("Decline_reason"));
				
				
			
				Digital_CC.mLogger.debug("Decision: " +iform.getValue("Decision"));
				Digital_CC.mLogger.debug("Remarks_dec: " +iform.getValue("Remarks"));
				Digital_CC.mLogger.debug("rejectReason: " +iform.getValue("Decline_reason"));
				
		/*		if("Initiation".equalsIgnoreCase(iform.getActivityName()))
					obj.put("Entry Date Time",iform.getValue("CreatedDateTime"));
				else
					obj.put("Entry Date Time",iform.getValue("EntryDateTime"));
				
				Digital_CC.mLogger.debug("Entry Date Time : "+obj.get("Entry Date Time")); */
				jsonArray.add(obj);
				iform.addDataToGrid("NG_DCC_GR_DECISION_HISTORY", jsonArray);
				
				Digital_CC.mLogger.debug("jsonArray : "+jsonArray);
			
				strReturn = "INSERTED";
				
				Digital_CC.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", WI Histroy Added Successfully!");
			} 
			catch (Exception e) {
				Digital_CC.mLogger.debug("Exception in inserting WI History!" + e.getMessage());
			}
		}
		
	/*	if("SystemCheckIntegrationlatestSuccessDate".equals(controlName))  // this is required to check 30 days expiry on the queues
		{
			String SysCheckDateTime = "";
			try 
			{				
				List lstDecisions = iform
					.getDataFromDB("select top 1 ACTION_DATE_TIME from USR_0_IRBL_WIHISTORY with(nolock) where WI_NAME = '"+getWorkitemName(iform)+"' and WORKSTEP = 'Sys_Checks_Integration' order by ACTION_DATE_TIME desc");
				Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", lstDecisions : "+lstDecisions.toString());
								
				for(int i=0;i<lstDecisions.size();i++)
				{
					List<String> arr1=(List)lstDecisions.get(i);
					SysCheckDateTime= arr1.get(0);
				}
				strReturn = SysCheckDateTime;
				
			}
			catch (Exception e) 
			{
				Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", Exception in SystemCheckIntegrationlatestSuccessDate " + e.getMessage());
			}
			
		} */
		
		return strReturn;
	}
}