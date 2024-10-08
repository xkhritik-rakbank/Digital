package com.newgen.iforms.user;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.newgen.XMLParser.XMLParser;
import com.newgen.iforms.custom.IFormReference;

public class Digital_CC_IntroDone extends Digital_CC_Common
{
	public String onIntroduceDone(IFormReference iform, String controlName,String event, String data) 
	{	
		String Workstep = iform.getActivityName();
		String strReturn="";
		Digital_CC.mLogger.debug("This is DCC_IntroDone_Event" + Workstep);
		String DCC_WIname = getWorkitemName(iform);
		if("ETB_Intro".equals(controlName))
		{
			String DCC_WI_No = getWorkitemName(iform);
			String DCC_WI_CREATE_TIME = (String) iform.getValue("CreatedDateTime");
			String Prospect_ID = (String) iform.getValue("Prospect_id");
			
			try{
				/*
				String columnNames = "DCC_WI_No,dcc_WI_created_date_TIME,Prospect_ID";
				String columnValues = "'" + DCC_WI_No + "','" + DCC_WI_CREATE_TIME + "','" + Prospect_ID+ "'";
				
				String apInsertInputXML = Digital_CC_Common.apInsert(getCabinetName(iform), getSessionId(iform), columnNames,
				columnValues, "NG_DCC_BSR_update");
				Digital_CC.mLogger.debug("APInsertInputXML: " + apInsertInputXML);
				
				String apInsertOutputXML = Digital_CC_Common.WFNGExecute(apInsertInputXML, iform.getServerIp(), iform.getServerPort(),1);
				Digital_CC.mLogger.debug("APInsertOutputXML: " + apInsertInputXML);
				XMLParser xmlParserAPInsert = new XMLParser(apInsertOutputXML);
				String apInsertMaincode = xmlParserAPInsert.getValueOf("MainCode");
				Digital_CC.mLogger.debug("Status of apInsertMaincode  " + apInsertMaincode);
				if (apInsertMaincode.equalsIgnoreCase("0")){
					
					Digital_CC.mLogger.debug("ApInsert successful: " + apInsertMaincode);
					
					Calendar calendar = Calendar.getInstance();
					Date today = new Date();
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
					String DateExtra2 = sdf1.format(today);
					calendar.add(Calendar.SECOND,15);
					Date addSec = calendar.getTime();
					DateExtra2 = sdf1.format(addSec);
					
					Digital_CC.mLogger.debug("DateExtra2 today : " + DateExtra2);
					Digital_CC.mLogger.debug("DateExtra2 addSec  15: " + DateExtra2);
					
					String columnNames_1 = "ValidTill";
					String columnValues_1 ="'"+DateExtra2+"'"; 
					String sWhereClause = "ProcessInstanceID='" + DCC_WI_No + "'and  ActivityName='Sys_FTS_WI_Update'";
					String tableName = "WFINSTRUMENTTABLE";
					String inputXML_1 =Digital_CC_Common.apUpdateInput(getCabinetName(iform),getSessionId(iform), 
					tableName, columnNames_1, columnValues_1,sWhereClause); 
					Digital_CC.mLogger.debug("Input XML for apUpdateInput for " + tableName + " Table : " + inputXML_1); 
					String outputXml_1 = Digital_CC_Common.WFNGExecute(inputXML_1,iform.getServerIp(), iform.getServerPort(), 1); 
					Digital_CC.mLogger.debug("Output XML for apUpdateInput for " + tableName + " Table : " +outputXml_1); 
					XMLParser sXMLParserChild_1 = new XMLParser(outputXml_1);
					String StrMainCode = sXMLParserChild_1.getValueOf("MainCode");
					
					if (StrMainCode.equalsIgnoreCase("0")){
						Digital_CC.mLogger.debug("ApUpdate successful: " + StrMainCode);
						//return "INSERTED";
					}
					else{
						Digital_CC.mLogger.debug("ApUpdate failed: " + StrMainCode);
						return "fail update";
					}
					
				}else
				{
					Digital_CC.mLogger.debug(" apinsert failed: " + apInsertMaincode);
					return "fail update";
				}*/
				
				Calendar calendar = Calendar.getInstance();
				Date today = new Date();
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
				String DateExtra2 = sdf1.format(today);
				calendar.add(Calendar.SECOND,15);
				Date addSec = calendar.getTime();
				DateExtra2 = sdf1.format(addSec);
				
				Digital_CC.mLogger.debug("DateExtra2 today : " + DateExtra2);
				Digital_CC.mLogger.debug("DateExtra2 addSec  15: " + DateExtra2);
				
				String columnNames_1 = "ValidTill";
				String columnValues_1 ="'"+DateExtra2+"'"; 
				String sWhereClause = "ProcessInstanceID='" + DCC_WI_No + "'and  ActivityName='Sys_FTS_WI_Update'";
				String tableName = "WFINSTRUMENTTABLE";
				String inputXML_1 =Digital_CC_Common.apUpdateInput(getCabinetName(iform),getSessionId(iform), 
				tableName, columnNames_1, columnValues_1,sWhereClause); 
				Digital_CC.mLogger.debug("Input XML for apUpdateInput for " + tableName + " Table : " + inputXML_1); 
				String outputXml_1 = Digital_CC_Common.WFNGExecute(inputXML_1,iform.getServerIp(), iform.getServerPort(), 1); 
				Digital_CC.mLogger.debug("Output XML for apUpdateInput for " + tableName + " Table : " +outputXml_1); 
				XMLParser sXMLParserChild_1 = new XMLParser(outputXml_1);
				String StrMainCode = sXMLParserChild_1.getValueOf("MainCode");
				
				if (StrMainCode.equalsIgnoreCase("0")){
					Digital_CC.mLogger.debug("ApUpdate successful: " + StrMainCode);
				}
				else{
					Digital_CC.mLogger.debug("ApUpdate failed: " + StrMainCode);
					return "fail update";
				}	
			}
			catch(Exception e){
				Digital_CC.mLogger.debug("Exception in inserting! " + e.getMessage());
				return "insert and update fail update";
				}
			}
		
		if("Card_ops_decision".equals(controlName)){
			
			try{
			String columnNames_1 = "Status";
			String columnValues_1 = "'D'";
			String sWhereClause = "wi_name='" + DCC_WIname + "'";
			String tableName = "ng_digital_awb_status";
			String inputXML_1 =Digital_CC_Common.apUpdateInput(getCabinetName(iform),getSessionId(iform), 
			tableName, columnNames_1, columnValues_1,sWhereClause); 
			Digital_CC.mLogger.debug("Input XML for apUpdateInput for " + tableName + " Table : " + inputXML_1); 
			String outputXml_1 = Digital_CC_Common.WFNGExecute(inputXML_1,iform.getServerIp(), iform.getServerPort(), 1); 
			Digital_CC.mLogger.debug("Output XML for apUpdateInput for " + tableName + " Table : " +outputXml_1); 
			XMLParser sXMLParserChild_1 = new XMLParser(outputXml_1);
			String StrMainCode = sXMLParserChild_1.getValueOf("MainCode");
			
			if (StrMainCode.equalsIgnoreCase("0")){
				Digital_CC.mLogger.debug("ApUpdate successful: " + StrMainCode);
				return "Updated";
			}
			else{
				Digital_CC.mLogger.debug("ApUpdate failed: " + StrMainCode);
				return "NotUpdated";
			}
		}
			catch(Exception e){
				Digital_CC.mLogger.debug("Exception in inserting! " + e.getMessage());
				return "update failed";
				}
		}
		
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
			    String Prospect_Creation_Date = (String)  iform.getValue("CreatedDateTime");
			    String entry_date_time = (String)  iform.getValue("entryDateTime");
			    
			    
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
				if(Workstep.equalsIgnoreCase("Initiation")){
					obj.put("Entry Date Time", Prospect_Creation_Date);
				}else{
					obj.put("Entry Date Time", entry_date_time);
				}
				
				
			
				Digital_CC.mLogger.debug("Decision: " +iform.getValue("Decision"));
				Digital_CC.mLogger.debug("Remarks_dec: " +iform.getValue("Remarks"));
				Digital_CC.mLogger.debug("rejectReason: " +iform.getValue("Decline_reason"));
				Digital_CC.mLogger.debug("Entry Date Time" +Prospect_Creation_Date);
				Digital_CC.mLogger.debug("Entry Date Time" + entry_date_time);
				
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