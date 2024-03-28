var DAO_Common = document.createElement('script');
DAO_Common.src = '/DAO/DAO/CustomJS/DAO_Common.js';
document.head.appendChild(DAO_Common);

var DAO_onLoad = document.createElement('script');
DAO_onLoad.src = '/DAO/DAO/CustomJS/DAO_onLoad.js';
document.head.appendChild(DAO_onLoad);
	
var DAO_onSaveDone = document.createElement('script');
DAO_onSaveDone.src = '/DAO/DAO/CustomJS/DAO_onSaveDone.js';
document.head.appendChild(DAO_onSaveDone);
	
var DAO_mandatory = document.createElement('script');
DAO_mandatory.src = '/DAO/DAO/CustomJS/DAO_MandatoryFieldValidations.js';
document.head.appendChild(DAO_mandatory);

var DAO_EventHandler = document.createElement('script');
DAO_EventHandler.src = '/DAO/DAO/CustomJS/DAO_EventHandler.js';
document.head.appendChild(DAO_EventHandler);

var signuploadFlag='false';


function setCommonVariables()
{
	Processname = getWorkItemData("ProcessName");
	ActivityName =getWorkItemData("ActivityName");
	WorkitemNo =getWorkItemData("processinstanceid");
	cabName =getWorkItemData("cabinetname");
	user= getWorkItemData("username");	
	setControlValue("log_in_as",user);
	viewMode=window.parent.wiViewMode;
}

function afterFormload()
{
	setCommonVariables();
	disable_form_load();
	populateDecisionDropDown();
	populateCIF();
	populateAccountpurpose();
	popup_onload();
	
	
	if(ActivityName=='sign_upload_checker' && getValue("prevws")=='Manual_Archive'){
		setStyle("CIF_Update", "disable", "true");
		setStyle("sign_upload", "disable", "true");
	}
	
	if (ActivityName == "sign_upload_maker" || ActivityName == "operations" || ActivityName == "compliance" || ActivityName == "Ops_Account_Closure_Maker"){
		if(getValue("is_kyc_report_onload") != "true")
		{
			template_generate_kyc();
		}
		if(getValue("is_risk_score_report_onload") != "true")
		{
			template_generate_risk_sheet();
		}
		if(getValue("is_firco_report_onload") != "true")
		{
			template_generate_firco();
		}
		if(getValue("is_dedupe_onload") != "true")
		{
			template_dedupe();
		}
		
		//document.getElementById('template_generate').click();
		//document.getElementById('Risk_score_trigger').click();
		saveWorkItem()
	}

	if(viewMode =='R')
	{
		setStyle("Remarks_dec", "disable", "false");
		setStyle("rejectReason", "disable", "false");
		clearValue("Remarks_dec");
		clearValue("rejectReason");
		setStyle("Remarks_dec", "disable", "true");
		setStyle("rejectReason", "disable", "true");
		
		 var section_onLoad_Readmode = executeServerEvent("section_onLoad_Readmode","FormLoad","",true);
		 return section_onLoad_Readmode;
		 saveWorkItem();
	}
	if(viewMode !='R')
	{
		setStyle("reject_reason", "disable", "false");
		clearValue("rejectReason");
		setStyle("reject_reason", "disable", "true");
		setControlValue('Remarks_dec','')
		saveWorkItem();
	}
	
	/*if (ActivityName == 'Introduction')
		
	if(viewMode s!='R')
	{
	}
	enableDisableAfterFormLoad(ActivityName);	
	enabledisableUIDGrid(ActivityName);
	applyToolTiponFields();
	onloadValidation(ActivityName); */

} 

function customValidationsBeforeSaveDone(op)
{
	if(op=="S")
	{
		if(saveClickOperation()==false)
		{
			return false;
		}
		return true;
	}
	else if (op=="I" || op=="D")
	{
		if(mandatoryFieldValidation(ActivityName)==false)
		{
			return false;
		}
		
		if(ActivityName=='operations' && getValue('Dedupe_Match_found')=='Y' && (getValue('CIF')=="" || 
		getValue('CIF')==null) && getValue("Decision")=='Approve')
		{
			showMessage("CIF","Please select CIF from the dropdown.","error");
			setFocus("CIF");
			return false;
		}
		
		if(ActivityName=='sign_upload_maker' && ((getValue("is_prime_req")=='Y')))
		{
			var resp = Documentlistvalidation();
			if(resp==false)
			{
				return false;
			}
		}
		
		if(ActivityName=='Ops_Account_Closure_Maker' && getValue("Card_closed")!= true  && ((getValue("is_prime_req")=='Y' && getValue("is_Ntb")=='Y')))
		{
			showMessage("Card_closed","Please tick card destroyed checkbox","error");
			setFocus("Card_closed");
			return false;
		}
		
		if(ActivityName=='Ops_Account_Closure_Maker' && getValue("ChequeBk_destroy")!= true && getValue("is_cbs_req")=='Y')
		{
			showMessage("ChequeBk_destroy","Please tick Chequebook Destroyed checkbox","error");
			setFocus("ChequeBk_destroy");
			return false;
		}
		
		if(signuploadFlag=='false' && ActivityName=='sign_upload_checker' && getValue("Decision")=='Approve' && getValue("prevws")!='Manual_Archive'){
			showMessage("sign_upload_click","Please upload Signature!!","error");
			setFocus("sign_upload_click");
			return false;
		}
		
		if(getValue("risk_score")=="" || getValue("risk_score")==null){
			showMessage("Risk_score_trigger","Retrigger risk score calculation","error");
			return false;
		}
		
		if((getValue("Decision")=="Refer to compliance" || getValue("Decision")=="Refer to compliane WC" || getValue("Decision")=="Refer to operations" || getValue("Decision")=="send to compliance WC" || getValue("Decision")=="send to compliance" || getValue("Decision")=="send to operations") && (getValue("Remarks_dec")=="" || getValue("Remarks_dec")==null))
		{
			setStyle("Remarks_dec", "mandatory", "true");
			showMessage("Remarks_dec","Remarks are mandatory!!","error");
			return false;
		}
		
		// jira 3310vinayak
		if(ActivityName=='operations' && getValue("NSTP_employer")=='Y' && (getValue("Company_employer_name")=="" || getValue("Company_employer_name")==null))
		{
			showMessage("Company_employer_name","Employer Name Can Not be Blank.Please Select the Employer Name","error");		setFocus("Company_employer_name");
			return false;
		}
		
		var confirmDoneResponse = confirm("You are about to submit the workitem. Do you wish to continue?");
		
		if(confirmDoneResponse ==  true)
		{		
			AddnalDocsReqd_flag();
			
			if(getValue("Decision")=="Approve" && ActivityName=='operations' && (getValue("Given_Name")!=getValue("DEH_FirstName") || getValue("Middle_Name")!=getValue("DEH_middleName") || getValue("Surname")!=getValue("DEH_LastName"))){
				setControlValue("UpdProspectReqd","True");
				if(getValue("User_Edit_name")=='' || getValue("User_Edit_name")=='name')
				{
					setControlValue("User_Edit_name","name")
				}
				else{
					setControlValue("User_Edit_name","both")
				}
				saveWorkItem();
			}
			
			if(getValue("Decision")=="Approve" && ActivityName=='operations' && getValue('Dedupe_Match_found')=='Y')
			{
				var res = executeServerEvent("set_ntb","introducedone","",true);
			}
			
			
			var status = insertIntoHistoryTable();
			
			//vinayak jira 3310 to handle employer chnages
			if(ActivityName=='operations'  && (((!(getValue("CustomerInput_employerName_freeTxt")=="" || getValue("CustomerInput_employerName_freeTxt")==null)) &&(getValue("CustomerInput_employerName_freeTxt")==getValue("Company_employer_name")))||(!(getValue("CustomerInput_other_employerName")=="" || getValue("CustomerInput_other_employerName")==null))) && (getValue("Decision")=="Approve" || getValue("Decision")=="Refer to compliane WC" || getValue("Decision")=="Additional details needed"))
			{
				setControlValue("is_EmployerAdd_req","Y");
			}
			/*
			else{
				setControlValue("is_EmployerAdd_req","");
			}*/
			//vinayak chnages ends
			
			
			// To set deh flags value in 4 WS - Start
			if(ActivityName=='operations' || ActivityName=='compliance' || ActivityName=='compliance_wc' || ActivityName=='Additional_cust_details' || ActivityName=='sign_upload_checker' || ActivityName=='Ops_Account_Closure_Checker' || ActivityName=='Manual_Archive' )
			{
				var response = setNotifyFlag();	
			}	// - End
			// To update firco flag - Start (operations N compliance WC ws - decision Approve)
			
			if(ActivityName=='operations'|| ActivityName=='Additional_cust_details')
			{
			//	var response = firco_flag_update();	-- discuss with ST!! 22.8.22
			}
			// To set the flag for notidy api in case the name is changes in front end and send updated to deh.
			
			if(ActivityName=='compliance')
			{
				var response = highrisk_flag_update();	
				// to update flag as clear to handle cif verification
			}
			
			
			/*
			if(ActivityName=='sign_upload_checker' && getValue("Decision")=='Approve' && getValue("is_OnePager_mailed")!='Y')
			{
				var response = sendmailTemplate();	
				// to update flag as clear to handle cif verification
				if(response!='INSERTED')
				{
					showMessage("is_OnePager_mailed","ONE PAGER NOT MAILED.","error");
					saveWorkItem();
					return false;
				}
			}
			*/
			
			saveWorkItem();
			return true;
		}
		else
		{
			return false;
		}

	}
	
}

function eventDispatched(controlObj,eventObj)
{
	var controlId=controlObj.id;
	var controlEvent=eventObj.type;
	var ControlIdandEvent = controlId+'_'+controlEvent;

	switch(ControlIdandEvent)
	{
		case 'Introduce_click' : 
			SetEventValues(controlId,controlEvent);
			break;	
		
		case 'template_generate_click':
			AttachDocumentWI(controlId);
			break;
		
		case 'template_generate_kyc_click':
			AttachDocumentWI(controlId);
			break;
		
		case 'Generate_firco_temp_click':
			AttachDocumentWI(controlId);
			break;
		case 'template_generate_dedupe_click':
			AttachDocumentWI(controlId);
			break;
			
		case 'Generate_RiskScore_click':
			AttachDocumentWI(controlId);
			break;

		case 'AECB_rpt_click':
			window.open(getValue("AECB_link"),"_self")
			break;
		
		
		case 'sign_upload_click':
			var result = executeServerEvent("sign_upload","Click","",true);
			if(result=="success"){
				signuploadFlag='true';
				showMessage(controlId,"Sign upload successfully.","error")
			}else
			{
				signuploadFlag='false';
				showMessage(controlId,"Sign upload Failure!!","error")
			}
			break;
			
		case 'risk_score_change':
			setControlValue("UpdProspectReqd","True");
			if(getValue("User_Edit_name")==''){
				setControlValue("User_Edit_name","none")
			}
			saveWorkItem();
			break;
			
		case 'PEP_change':
			setControlValue("UpdProspectReqd","True");
			if(getValue("User_Edit_name")==''){
				setControlValue("User_Edit_name","none")
			}
			saveWorkItem();
			break;
		
		case 'Given_Name_change':
			saveWorkItem();
			break;
			
		case 'Middle_Name_change':
			saveWorkItem();
			break;
			
		case 'Surname_change':
			saveWorkItem();
			break;
		
		case 'gross_monthly_salary_income_change':
			setMonthlyCash();
			break;
		
		case 'Risk_score_trigger_click':
			try
			{
				window.opener.parent.showProcessing();	
			}catch(ex){}
			var response = executeServerEvent("Risk_score_trigger","Click","",true);
			try
			{
				window.opener.parent.hideProcessing();	
			}catch(ex) {}
			
			var AjxResponse;
			var AjxResponseTxt;
			if(response.indexOf("~") != -1)
			{
				var ajaxResult=response.split("~");
				AjxResponse=ajaxResult[0];
				AjxResponseTxt=ajaxResult[1];
			}else
				AjxResponse=response;
			
			if(AjxResponse == "0000" && AjxResponseTxt.indexOf("SUCCESS") != -1)
			{
				window.parent.customAddDoc(ajaxResult[3],ajaxResult[4],ajaxResult[5]);
				deleteTemplateFromServer(ajaxResult[6]);
				showMessage(controlId,"Risk score triggered successfully.","error");
				saveWorkItem();
			}
			else if(AjxResponse.indexOf("0000")==-1)
			{
				showMessage(controlId,"Risk score Integration Failure!!","error");
				break;
			}
			break;
		
		case 'CIF_Update_click':

			var result = executeServerEvent("CIF_Update","Click","",true);
			if(result=="success")
			{
				showMessage(controlId,"CIF update triggered successfully.","error")
			}else
			{
				showMessage(controlId,"CIF update Integration Failure!!","error")
			}
			break;
		
		
		case 'Add_Country_click':
			var country_code = getValue("Country_List");
			if(country_code=="")
			{
				alert("Empty value passed!")
				break;
			}
			
			if(getValue("countryDealingWith")=="")
			{
				setControlValue("countryDealingWith",country_code);
				
				var response = executeServerEvent("risk_score_change","change","",true);
				return response;
			}
			else
			{	var next_val = getValue("countryDealingWith")+","+getValue("Country_List");
				setControlValue("countryDealingWith",next_val);
				
				var response = executeServerEvent("risk_score_change","change","",true);
				return response;
			}
			break;
		// fields for risk score retigger if changed.	
		case 'Nationality_change':
			var nation = getValue('Nationality');
			var sec_nation = getValue('Secondary_Nationality');
			if(nation==sec_nation)
			{
				showMessage(controlId,"Nationality can not be same as Secondary Nationality ","error");
				break;
			}
			var response = executeServerEvent("risk_score_change","change","",true);
			return response;
			break;
			
		case 'country_of_residence_change':
			
			var response = executeServerEvent("risk_score_change","change","",true);
			return response;
			break;
			
		case 'Company_employer_name_change':
			
			var response = executeServerEvent("risk_score_change","change","",true);
			return response;
			break;
				
		case 'industry_change':
			var response = executeServerEvent("risk_score_change","change","",true);
			return response;
			break;
		
		case 'countryDealingWith_change':
			var response = executeServerEvent("risk_score_change","change","",true);
			return response;
			break;
		
		case 'Business_activity_per_TL_change':
			var response = executeServerEvent("risk_score_change","change","",true);
			return response;
			break;
		
		case 'companyName_change':
			var response = executeServerEvent("risk_score_change","change","",true);
			return response;
			break;
		
		case 'table3_document_name_change':
		
			var docName = getValue('table3_document_name');
			var docStatus = 'Pending';
			
			var flag='found';
			var row_count = getGridRowCount("AddnalDocs");
			
			for(var i =0; i<row_count;i++)
			{
				var Doc_grid_status = getValueFromTableCell("AddnalDocs",i,1);
				var Doc_grid_Name = getValueFromTableCell("AddnalDocs",i,0);
				
				if(Doc_grid_Name==docName && Doc_grid_status==docStatus)
				{
					showMessage("","Selected Document is already in the list, Please select another document!!","error");
					setControlValue('table3_document_name','');
					return false;
				}
				else{
					flag='Not found';
				}
			}
			if(flag=='Not found'){
				return true;
			}
			break;
		
		case 'Decision_change':
		
			if(getValue("Decision")=="Additional Details required from Customer" && ActivityName=='Additional_cust_details'){
				validation_on_Decision_change();
				break;
			}
			if(getValue("Decision")=="Additional details needed" && ActivityName=='sign_upload_checker'){
				validation_on_Decision_change();
				break;
			}
			if(getValue("Decision")=="Additional details needed" && ActivityName=='wm_control'){
				validation_on_Decision_change();
				break;
			}
			if((getValue("Decision")=="send to compliance" || getValue("Decision")=="send to operations" || getValue("Decision")=="send to compliance WC" || getValue("Decision")=="Reject" || getValue("Decision")=="Initiate Account closure" || getValue("Decision")=="Additional Details required from Customer") && ActivityName=='Additional_cust_details' && getValue("prevws")=="sign_upload_checker"){
				validation_on_Decision_change();
				break;
			}
			if((getValue("Decision")=="send to compliance" || getValue("Decision")=="send to operations" || getValue("Decision")=="send to compliance WC") && (ActivityName=='Additional_cust_details' || ActivityName=='wm_control')){
				validation_on_Decision_change();
				break;
			}
			if((getValue("Decision")=="Approve" || getValue("Decision")=="Reject") && (ActivityName=='operations' || ActivityName=='compliance' || ActivityName=='compliance_wc'))
			{
				validation_on_Decision_change();
				break;
			}
			if((getValue("Decision")!="Approve") && ActivityName=='operations' && getValue("CIF_verification_flag")=='Y')
			{
				validation_on_Decision_change();
				break;
			}
			if((getValue("Decision")!="Additional details needed" && getValue("Decision")!="Reject" && getValue("Decision")!="Approve") && ActivityName=='operations' && getValue("Name_modify")=='Y' && getValue("firco_hit")!='Y')
			{
				validation_on_Decision_change();
				break;
			}
			if(getValue("Decision")=="Approve" && ActivityName=='operations' && (getValue("Given_Name")!=getValue("DEH_FirstName") || getValue("Middle_Name")!=getValue("DEH_middleName") || getValue("Surname")!=getValue("DEH_LastName"))){
				validation_on_Decision_change();
				break;
			}
			
			if(getValue("Decision")=="Reject" && ActivityName=='Additional_cust_details' && getValue("is_stp")=='Y')
			{
				validation_on_Decision_change();
				break;
			}
			break;

		case 'is_modify_employer_change':
			var response = executeServerEvent("is_modify_employer_change","change","",true);
			return response;			
			break;
		case 'is_notPicklistEmployer_change':
			var response = executeServerEvent("is_notPicklistEmployer_change","change","",true);
			return response;			
			break;
		case 'picklist_employer_name_change':			
			var response = executeServerEvent("picklist_employer_name_change","change","",true);
			return response;			
			break;
		case 'CustomerInput_other_employerName_change':
			var CustomerInput_value=getValue("CustomerInput_other_employerName");
			setControlValue("Company_employer_name",CustomerInput_value);			
			break;
		case 'internalExposureDetails_click':
			
			//saveWorkItem();
			if(getValue("CIF")=="NTB"){
				alert("Please select CIF Id for the Customer!")
				break;
			}
						
			var response = executeServerEvent("InternalExposure","click","",false);
			CreateIndicator("temp");
			
			return response;
			
			break;
	}
	 switch(controlId,controlEvent)
		{
		case 'change' : 
			executeServerEvent(controlId,controlEvent)
			break;
		case 'click':
			break;	
							
		}
}


function postServerEventHandler(controlName,EventType,response)
{
	RemoveIndicator("temp");    //Loading Indication will end here
	if(controlName=="InternalExposure" && EventType=="click")
	{	
		
		if(response== "Inserted")
		{
			showMessage("InternalExposure","Internal Exposure Details Fetched Successful","error");
			refreshFrame("Frame_ExposureDetails");
		}
		else
		{
				showMessage("InternalExposure","Internal Exposure Details Not Fetched","error");
		}
		try {
			window.parent.WFSave();
		} catch(ex) {	
		}
		
	}	
}
// template generation code:
function AttachDocumentWI(controlId)
{
	if(controlId == 'template_generate' || controlId =='template_generate_kyc')
	{
		controlId='template_generate';
		var response = executeServerEvent(controlId,"click","DAO_Template_kyc",true);
		
		var AjxResponse;
		var AjxResponseTxt;
		if(response.indexOf("~") != -1)
		{			
			var ajaxResult=response.split("~");
			//alert("ajaxResult--"+ajaxResult);
			AjxResponse=ajaxResult[0];
		}
		else
			AjxResponse=response;
		
		if(AjxResponse == "0000")
		{ 
			window.parent.customAddDoc(ajaxResult[1],ajaxResult[2],ajaxResult[3]);	
			showMessage(controlId,"Successful in attaching KYC template","error");
			deleteTemplateFromServer(ajaxResult[4]);
			saveWorkItem();
		}
		else if(AjxResponse.indexOf("0000")==-1)
		{
			showMessage(controlId,"Error in attaching KYC template","error");			
		}		
		else if(AjxResponse.indexOf("Error")!=-1)
		{
			showMessage(controlId,"Problem in fetching attach KYC template","error");		
		}
	}
	
	else if(controlId == 'Generate_firco_temp')
	{
		var response = executeServerEvent(controlId,"click","DAO_Firco_Template",true);
		
		var AjxResponse;
		var AjxResponseTxt;
		if(response.indexOf("~") != -1)
		{		
			var ajaxResult=response.split("~");
			//alert("ajaxResult--"+ajaxResult);
			AjxResponse=ajaxResult[0];
		}
		else
			AjxResponse=response;
		
		if(AjxResponse == "0000")
		{
			window.parent.customAddDoc(ajaxResult[1],ajaxResult[2],ajaxResult[3]);	
			showMessage(controlId,"Successful in attaching Firco Template","error");
			deleteTemplateFromServer(ajaxResult[4]);
			saveWorkItem();
		}
		else if(AjxResponse.indexOf("0000")==-1)
		{
			showMessage(controlId,"Error in attaching Firco Template","error");			
		}		
		else if(AjxResponse.indexOf("Error")!=-1)
		{
			showMessage(controlId,"Problem in fetching attach Firco Template","error");		
		}
	}
	else if(controlId == 'template_generate_dedupe')
	{
		var response = executeServerEvent(controlId,"click","DAO_DEDUPE_Template",true);
		
		var AjxResponse;
		var AjxResponseTxt;
		if(response.indexOf("~") != -1)
		{		
			var ajaxResult=response.split("~");
			//alert("ajaxResult--"+ajaxResult);
			AjxResponse=ajaxResult[0];
		}
		else
			AjxResponse=response;
		
		if(AjxResponse == "0000")
		{
			window.parent.customAddDoc(ajaxResult[1],ajaxResult[2],ajaxResult[3]);	
			showMessage(controlId,"Successful in attaching DEDUPE Template","error");
			deleteTemplateFromServer(ajaxResult[4]);
			saveWorkItem();
		}
		else if(AjxResponse.indexOf("0000")==-1)
		{
			showMessage(controlId,"Error in attaching DEDUPE Template","error");			
		}		
		else if(AjxResponse.indexOf("Error")!=-1)
		{
			showMessage(controlId,"Problem in fetching attach DEDUPE Template","error");		
		}
	} 
	else if(controlId == 'Generate_RiskScore')
	{
		var response = executeServerEvent(controlId,"click","Generate_RiskScore",true);
		var AjxResponse;
		var AjxResponseTxt;
		if(response.indexOf("~") != -1)
		{		
			var ajaxResult=response.split("~");
			//alert("ajaxResult--"+ajaxResult);
			AjxResponse=ajaxResult[0];
		}
		else
			AjxResponse=response;
		
		if(AjxResponse == "0000")
		{
			window.parent.customAddDoc(ajaxResult[1],ajaxResult[2],ajaxResult[3]);	
			showMessage(controlId,"Successful in attaching Risk Sheet Template","error");
			deleteTemplateFromServer(ajaxResult[4]);
			saveWorkItem();
		}
		else if(AjxResponse.indexOf("0000")==-1)
		{
			showMessage(controlId,"Error in attaching Risk Sheet Template","error");			
		}		
		else if(AjxResponse.indexOf("Error")!=-1)
		{
			showMessage(controlId,"Problem in fetching attach Risk Sheet Template","error");		
		}
	}
}

function deleteTemplateFromServer (pdfname)
{
	var url = '/DAO/DAO/CustomJSP/DeleteGeneratedTemplate.jsp?pdfname='+pdfname;
	var xhr;
	var ajaxResult;	
	
	if(window.XMLHttpRequest)
		 xhr=new XMLHttpRequest();
	else if(window.ActiveXObject)
		 xhr=new ActiveXObject("Microsoft.XMLHTTP");

	 xhr.open("GET",url,false); 
	 xhr.send(null);
	 
	if (xhr.status == 200) { //Do nothing
	}
	else
	{
		//alert("Error while deleting generated template from server"); commented by stutee.mishra for temp resolution on 21/10/2021
		return false;
	}
}
function onTableCellChange(rowIndex,colIndex,ref,controlId)
{
	/*if(controlId=='Q_USR_0_ML_DEFERRAL_DETAILS')
	{   
	  ValidateAlphaNumeric(rowIndex,colIndex,ref,controlId);
	}*/
	
}

function mandatoryValidation_Add_docs_grid(controlId){
	
	if(controlId == "AddnalDocs")
    {
		var docName = getValue('table3_document_name');
		var docStatus = getValue('table3_document_status');
		
		if(docName==null || docName=='Select' || docName==''){
			showMessage("table3_document_name","Select Document Name","error");	
			return false;
		}
		if(docStatus==null || docStatus=='Select' || docStatus==''){
			showMessage("table3_document_status","Select Document Status","error");
			return false;
		}
		return true;
	}	
}


function AdditionalDocs_grid_check_for_rep_docs(controlId)
{
	if(controlId == "AddnalDocs")
    {
		var docName = getValue('table3_document_name');
		var docStatus = document.getElementById('table3_document_status').value;
		
		var flag='found';
		var row_count = getGridRowCount("AddnalDocs");
		
		for(var i =0; i<row_count;i++)
		{
			var Doc_grid_status = getValueFromTableCell("AddnalDocs",i,1);
			var Doc_grid_Name = getValueFromTableCell("AddnalDocs",i,0);
			
			if(Doc_grid_Name==docName && Doc_grid_status==docStatus)
			{
				showMessage("","Selected Document is already in the list, Please select another document!!","error");
				return false;
			}
			else{
				flag='Not found';
			}
		}
		if(flag=='Not found'){
			return true;
		}
	}
}


function subFormLoad(buttonId)
{
	
}

function addRowPostHook(tableId)
{
}

function subformDoneClick(buttonId)
{
}
