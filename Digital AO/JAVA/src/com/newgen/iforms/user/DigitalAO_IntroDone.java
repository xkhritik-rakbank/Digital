package com.newgen.iforms.user;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.newgen.iforms.custom.IFormReference;
import com.newgen.omni.jts.cmgr.XMLParser;

public class DigitalAO_IntroDone extends DigitalAO_Common
{
	public String onIntroduceDone(IFormReference iform, String controlName,String event, String data)
	{
		String strReturn="";
		DigitalAO.mLogger.debug("This is DAO_IntroDone_Event");
		if("InsertIntoHistory".equals(controlName))
		{
			try {
				DigitalAO.mLogger.debug("InsertIntoHistory : Try ");
				
				JSONArray jsonArray=new JSONArray();
				JSONObject obj=new JSONObject();
				
				Date d = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String strDate = dateFormat.format(d);
			    
			    DigitalAO.mLogger.debug("strDate: " +strDate);
			    DigitalAO.mLogger.debug("entry_date_time: " +iform.getValue("EntryDateTime"));
			    
			    String entrydatetime = (String) iform.getValue("EntryDateTime");
			    Date d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(entrydatetime);
			    String entrydatetime_format = dateFormat.format(d1);
			   
			    DigitalAO.mLogger.debug("getClass EntryDateTime : " +iform.getValue("EntryDateTime").getClass().getName());
			    
				obj.put("Date Time",strDate);
				obj.put("Workstep",iform.getActivityName());
				obj.put("User Name", iform.getUserName());
				obj.put("Decision",iform.getValue("Decision"));
				obj.put("Remarks", iform.getValue("Remarks_dec"));
				obj.put("Reject reasons", iform.getValue("rejectReason"));
				obj.put("Entry Date Time",entrydatetime_format);
				
				DigitalAO.mLogger.debug("Decision: " +iform.getValue("Decision"));
				DigitalAO.mLogger.debug("Remarks_dec: " +iform.getValue("Remarks_dec"));
				DigitalAO.mLogger.debug("rejectReason: " +iform.getValue("rejectReason"));
				
				jsonArray.add(obj);
				iform.addDataToGrid("NG_DAO_GR_DECISION_HISTORY", jsonArray);
				DigitalAO.mLogger.debug("jsonArray : "+jsonArray);
				strReturn = "INSERTED";
				
				DigitalAO.mLogger.debug("WINAME: "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", ControlName: "+controlName+", WI Histroy Added Successfully!");
			} 
			catch (Exception e) {
				DigitalAO.mLogger.debug("Exception in inserting WI History!" + e.getMessage());
			}
		}
		
		if("firco_flag_update".equalsIgnoreCase(controlName))
		{
			String WS = iform.getActivityName();
			if(WS.equalsIgnoreCase("compliance_wc") || WS.equalsIgnoreCase("operations"))
			{
				String decision="";
				String get_Dec= "select top 1 Decision from NG_DAO_GR_DECISION_HISTORY with (nolock) where wi_name='"+getWorkitemName(iform)+"' and workstep='"+WS+"'";
				List<List<String>> get_decision_value = iform.getDataFromDB(get_Dec);
				
				if (!get_decision_value.isEmpty()) {
					DigitalAO.mLogger.debug("Inside get_decision_value: ");
					decision = get_decision_value.get(0).get(0);
					DigitalAO.mLogger.debug("get_decision_value: " + get_decision_value);
				} else {
					DigitalAO.mLogger.debug("get_decision_value is empty!!");
				}
				
				if(decision.equalsIgnoreCase("Approve"))
				{
					setControlValue("firco_hit", "N", iform);
					strReturn="firco flag set";
				}
				else
				{
					strReturn="firco flag not set";
				}
			}
		}
		
		if("highrisk_flag_update".equalsIgnoreCase(controlName))
		{
			try{

				String WS = iform.getActivityName();
				String decision = (String) iform.getValue("Decision");
				String highrisk = (String) iform.getValue("high_risk");
				DigitalAO.mLogger.debug("WS: " + WS + " decision: " +decision+ " highrisk: "+highrisk);
				
				if(WS.equalsIgnoreCase("compliance"))
				{
					if("Approve".equalsIgnoreCase(decision))
					{
						setControlValue("high_risk", "Clear", iform);
					}
				}
			}
			catch(Exception e){
				DigitalAO.mLogger.debug("Inside catch highrisk_flag_update set value"+e.getMessage());
			}
		}
		
		if("notify_set".equals(controlName))
		{
			try{
				
				DigitalAO.mLogger.debug("Inside try Notify set value for deh workitemstatus and event");
				String workstep =iform.getActivityName();
				String decision=(String) iform.getValue("Decision");
				String Name_modify = (String) iform.getValue("Name_modify");
				String UpdProspectReqd = (String) iform.getValue("UpdProspectReqd");
				String User_Edit_name = (String) iform.getValue("User_Edit_name");
				String is_EmployerAdd_req = (String) iform.getValue("is_EmployerAdd_req");
				String EmployerName = (String) iform.getValue("Company_employer_name");
				String EmployerName_deh = (String) iform.getValue("Company_employer_name_deh");
				
				
				DigitalAO.mLogger.debug(" Notify workstep "+workstep);
				DigitalAO.mLogger.debug(" Notify decision "+decision);
				DigitalAO.mLogger.debug(" Notify UpdProspectReqd "+UpdProspectReqd);
				DigitalAO.mLogger.debug(" Notify User_Edit_name "+User_Edit_name);
				DigitalAO.mLogger.debug(" is_EmployerAdd_req "+is_EmployerAdd_req);
				DigitalAO.mLogger.debug("  EmployerName "+EmployerName);
				DigitalAO.mLogger.debug(" EmployerName_deh "+EmployerName_deh);
				
				String deh_Workitem_status="";
				String deh_Event="";
				if (workstep.equalsIgnoreCase("compliance") || workstep.equalsIgnoreCase("compliance_wc") || workstep.equalsIgnoreCase("operations"))
				{
					
					//vinayak chnages employer chnges event is add employer to finacle at operations ws
					//(((decision.equalsIgnoreCase("Approve")||decision.equalsIgnoreCase("Refer to compliane WC"))&& is_EmployerAdd_req.equalsIgnoreCase("Y") && workstep.equalsIgnoreCase("operations"))||(decision.equalsIgnoreCase("Approve")&& is_EmployerAdd_req.equalsIgnoreCase("Y"))){
					DigitalAO.mLogger.debug("Notify : is_EmployerAdd_req : "+is_EmployerAdd_req);
					DigitalAO.mLogger.debug("Notify : decision : "+decision);
					DigitalAO.mLogger.debug(" Notify workstep "+workstep);
					if  ((decision.equalsIgnoreCase("Approve")||decision.equalsIgnoreCase("Refer to compliane WC"))&& (!EmployerName_deh.equalsIgnoreCase(EmployerName)||("Y".equalsIgnoreCase(is_EmployerAdd_req))) ){
						deh_Event="EMP_CREATE";
	    				deh_Workitem_status = "EMP";
	    			}					
					// earlier this condition was if condition now chnages to else if
					else if("Approve".equalsIgnoreCase(decision) && "True".equalsIgnoreCase(UpdProspectReqd) && 
							("name".equalsIgnoreCase(User_Edit_name) || "both".equalsIgnoreCase(User_Edit_name)))
					{	// New change for Name edit by user to send in notify.
						deh_Workitem_status = "APP";
						deh_Event="NAME_STATUS_CHANGE";
					}					
					 				
					else if (decision.equalsIgnoreCase("Approve")){
						deh_Event="STATUS_CHANGE";
	    				deh_Workitem_status = "APP";
	    			}
				
					else if (decision.equalsIgnoreCase("Reject")){
	    				deh_Workitem_status = "REJ";
	    				deh_Event="STATUS_CHANGE";
	    			}
	    		}
				else if (workstep.equalsIgnoreCase("Additional_cust_details")){
					deh_Event="REQUEST_CUSTOMER_QUERY";
					if (decision.equalsIgnoreCase("Reject")){
	    				deh_Workitem_status = "REJ";
	    				deh_Event="STATUS_CHANGE";
					}
					else{
						deh_Event="REQUEST_CUSTOMER_QUERY";
						deh_Workitem_status = "INP";
					}
	    		}
				else if(workstep.equalsIgnoreCase("sign_upload_checker")){
    				// vinayak comment jira 3359
					
					if (decision.equalsIgnoreCase("Approve")){
						deh_Event="One_pager_signed";
						deh_Workitem_status = "";
					}
					else if(decision.equalsIgnoreCase("Reject")){
						deh_Event="STATUS_CHANGE";
						deh_Workitem_status = "REJ";
					}
				}
				else if(workstep.equalsIgnoreCase("Ops_Account_Closure_Checker"))
				{
					deh_Event="STATUS_CHANGE";
					deh_Workitem_status = "CLO";
				}
				else if(workstep.equalsIgnoreCase("Manual_Archive"))
				{
					if (decision.equalsIgnoreCase("Approve")){
						deh_Event="STATUS_CHANGE";
						deh_Workitem_status = "COM";
					}
				}
				DigitalAO.mLogger.debug("Notify : deh_Workitem_status : "+deh_Workitem_status);
				DigitalAO.mLogger.debug("Notify : deh_Event : "+deh_Event);
				setControlValue("deh_Workitem_status", deh_Workitem_status, iform);
				setControlValue("deh_Event", deh_Event, iform);
				strReturn = "Value set";
			}
			
			catch(Exception e){
				DigitalAO.mLogger.debug("Inside catch Notify set value for deh workitemstatus and event"+e.getMessage());
			}
		}
		
		if("set_ntb".equals(controlName))
		{
			String ntb = (String) iform.getValue("is_Ntb");
			DigitalAO.mLogger.debug("Old NTB value: "+ntb);
			String CIF = (String) iform.getValue("CIF");
			DigitalAO.mLogger.debug("CIF: "+CIF);
			try{
				if("NTB".equalsIgnoreCase(CIF)){
					setControlValue("is_Ntb", "Y", iform);
					DigitalAO.mLogger.debug("if case turned to NTB then setting ntb as Y");
				}
				else {
					setControlValue("is_Ntb", "N", iform);
					DigitalAO.mLogger.debug("if case turned to ETB then setting ntb as N");
				}
			}
			
			catch(Exception e){
				DigitalAO.mLogger.debug("Inside catch SET NTB for dedupe cases"+e.getMessage());
			}
		}
		
		
		//vinayak changes 05/08/23 send for mail with one pager
		
		if("mailOnePager".equals(controlName))
		{

			try
			{
			String wi_name=getWorkitemName(iform);
			String mainCodeforAPInsert=null;
			String sOutputXML_mail="";
			
			//dao read config 
			int configReadStatus = readConfig();
			
			DigitalAO.mLogger.debug("configReadStatus " + configReadStatus);
			if (configReadStatus != 0) {
				DigitalAO.mLogger.error("Could not Read Config Properties [properties]");
				return "Error in Reading config File";
			}
			
			String sessionId = getSessionId(iform);
			DigitalAO.mLogger.debug("sessionID  :" + sessionId);	

			String CabinetName = DAOConfigProperties.get("CabinetName");
			DigitalAO.mLogger.debug("CabinetName  :" + CabinetName);

			String JTSIP = DAOConfigProperties.get("JTSIP");
			DigitalAO.mLogger.debug("JTSIP  :" + JTSIP);

			String JTSPort = DAOConfigProperties.get("JTSPort");
			DigitalAO.mLogger.debug("JTSPort  :" + JTSPort);

			String VolumeId = DAOConfigProperties.get("VolumeId");
			DigitalAO.mLogger.debug("VolumeId  :" + VolumeId);
			
			String ProcessDefId=DAOConfigProperties.get("ProcessDefId");
			DigitalAO.mLogger.debug("ProcessDefId  :" + ProcessDefId);
			
			String INSERTEDTIME=DigitalAO_Common.getdateCurrentDateInSQLFormat();
			DigitalAO.mLogger.debug("INSERTEDTIME  :" + INSERTEDTIME);
			
			String query="Select top 1 ISnull(ImageIndex,'') as ImageIndex,ISnull(concat(NAME,'.',AppName),'') as ATTACHMENTNAMES, volumeId from pdbdocument with (nolock) "
							+ "WHERE DocumentIndex in (select DocumentIndex from PDBDocumentContent where ParentFolderIndex =(select FolderIndex from PDBFolder where Name = '"+wi_name+"'))and"
							+ " name like 'Wet_signature_form%' order by DocumentIndex desc";

			List<List<String>> get_query_value = iform.getDataFromDB(query);
			String ImageIndex = get_query_value.get(0).get(0);
			String ATTACHMENTNAMES = get_query_value.get(0).get(1).trim();
			String volumeId = get_query_value.get(0).get(2);
			
			String query_exttable="select MailTemplate,MailSubject,FromMail,* from ng_master_dao_email_trigger where workstepname='sign_upload_checker'";

			List<List<String>> get_queryExttable_value = iform.getDataFromDB(query_exttable);
			String MailTemplate = get_queryExttable_value.get(0).get(0);
			String MailSubject = get_queryExttable_value.get(0).get(1);
			String FromMail = get_queryExttable_value.get(0).get(2);
			
			String toMailID=(String) iform.getValue("email_id_1");
			String wfattachmentNames=ATTACHMENTNAMES+";";
			String wfattachmentIndex=ImageIndex+"#"+volumeId+"#;";
			String CustomerName = iform.getValue("Given_Name") + " " +  iform.getValue("Surname");
			DigitalAO.mLogger.debug("CustomerName  :" + CustomerName);
			String FinalMailStr = MailTemplate.replaceAll("#Customer_Name#",CustomerName);
			
						
			DigitalAO.mLogger.debug(" ImageIndex :" + ImageIndex);
			DigitalAO.mLogger.debug(" ATTACHMENTNAMES :" + ATTACHMENTNAMES);
			DigitalAO.mLogger.debug(" volumeId :" + volumeId);
			DigitalAO.mLogger.debug(" MailTemplate :" + MailTemplate);
			DigitalAO.mLogger.debug(" MailSubject :" + MailSubject);
			DigitalAO.mLogger.debug(" FromMail :" + FromMail);
			DigitalAO.mLogger.debug(" toMailID :" + toMailID);
			DigitalAO.mLogger.debug(" wfattachmentNames :" + wfattachmentNames);
			DigitalAO.mLogger.debug(" wfattachmentIndex :" + wfattachmentIndex);
			DigitalAO.mLogger.debug(" FinalMailStr :" + FinalMailStr);
					
					
			XMLParser objXMLParser = new XMLParser();
			

			String columnName="MAILFROM,MAILTO,MAILSUBJECT,MAILMESSAGE,MAILCONTENTTYPE,MAILPRIORITY,MAILSTATUS,INSERTEDBY,MAILACTIONTYPE,INSERTEDTIME,PROCESSDEFID,PROCESSINSTANCEID,WORKITEMID,ACTIVITYID,NOOFTRIALS,attachmentNames,attachmentISINDEX";
			String strValues="'"+FromMail+"','"+toMailID+"','"+MailSubject+"',N'"+FinalMailStr+"','text/html;charset=UTF-8','1','N','CUSTOM','TRIGGER','"+INSERTEDTIME+"','"+ProcessDefId+"','"+wi_name+"','1','1','0','"+wfattachmentNames+"','"+wfattachmentIndex+"'";
			
			DigitalAO.mLogger.debug(" columnName :" + columnName);
			DigitalAO.mLogger.debug(" strValues :" + strValues);
			
					String sInputXML_mail = "<?xml version=\"1.0\"?>" +
					"<APInsert_Input>" +
					"<Option>APInsert</Option>" +
					"<TableName>WFMAILQUEUETABLE</TableName>" +
					"<ColName>" + columnName + "</ColName>" +
					"<Values>" + strValues + "</Values>" +
					"<EngineName>" + CabinetName + "</EngineName>" +
					"<SessionId>" + sessionId + "</SessionId>" +
					"</APInsert_Input>";
					DigitalAO.mLogger.debug("Mail Insert InputXml::::::::::\n"+sInputXML_mail);
					sOutputXML_mail =WFNGExecute(sInputXML_mail, JTSIP,JTSPort,0);
					DigitalAO.mLogger.debug("Mail Insert OutputXml::::::::::\n"+sOutputXML_mail);
					objXMLParser.setInputXML(sOutputXML_mail);
					mainCodeforAPInsert=objXMLParser.getValueOf("MainCode");
					
					if(mainCodeforAPInsert.equalsIgnoreCase("0"))
					{
						DigitalAO.mLogger.debug("mail Insert Successful");	
						 iform.setValue("is_OnePager_mailed","Y");
						 return "INSERTED";
					}
					else
					{
						DigitalAO.mLogger.debug("mail Insert Unsuccessful");
						 iform.setValue("is_OnePager_mailed","N");
						 return "NOTINSERTED";
					}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				DigitalAO.mLogger.debug("Exception in Sending mail", e);
				
			}
		}
		
	/*	if("SystemCheckIntegrationlatestSuccessDate".equals(controlName))  // this is required to check 30 days expiry on the queues
		{
			String SysCheckDateTime = "";
			try 
			{				
				List lstDecisions = iform
					.getDataFromDB("select top 1 ACTION_DATE_TIME from USR_0_IRBL_WIHISTORY with(nolock) where WI_NAME = '"+getWorkitemName(iform)+"' and WORKSTEP = 'Sys_Checks_Integration' order by ACTION_DATE_TIME desc");
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", lstDecisions : "+lstDecisions.toString());
								
				for(int i=0;i<lstDecisions.size();i++)
				{
					List<String> arr1=(List)lstDecisions.get(i);
					SysCheckDateTime= arr1.get(0);
				}
				strReturn = SysCheckDateTime;
				
			}
			catch (Exception e) 
			{
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+iform.getActivityName()+", Exception in SystemCheckIntegrationlatestSuccessDate " + e.getMessage());
			}
			
		} */
		
		return strReturn;
	}
}