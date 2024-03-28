var DAO_Common = document.createElement('script');
DAO_Common.src = '/DAO/DAO/CustomJS/DAO_Common.js';
document.head.appendChild(DAO_Common);

var activityname=getWorkItemData("activityName");

function loadSolId(user)
{
	var solId = executeServerEvent("SolId","FORMLOAD",'',true);
	setControlValue("Sol_id",solId);
}

// to set income details values by deepanshu
function setMonthlyCash(){
    var MonthlyTurnOverNonCash = (parseFloat( getValue('gross_monthly_salary_income')) * parseFloat(getValue('PercentageMonthlyExpectedTurnoverNonCash')))/100;
    setValues({"Monthly_expected_turnover_non_cash":MonthlyTurnOverNonCash},true);

    var MonthlyTurnOverCash = (parseFloat( getValue('gross_monthly_salary_income')) * parseFloat(getValue('PercentageMonthlyExpectedTurnoverCash')))/100;
    setValues({"Monthly_expected_turnover_Cash":MonthlyTurnOverCash},true);

    if(parseFloat( getValue('gross_monthly_salary_income')) > parseFloat( getValue('high_range_value')) || parseFloat( getValue('gross_monthly_salary_income')) < parseFloat( getValue('low_range_value'))){

        setValues({"CIF_flag" : 'Y'},true);
    }else{
		setValues({"CIF_flag" : 'N'},true);
	}
}

function disable_form_load()
{
	//disbale customer info tab on loan
    var disable_section = executeServerEvent("section_onLoad","FormLoad","",true);
	var disable_frame2 = executeServerEvent("cust_info_frame","FormLoad","",true);
	var disable_frame3 = executeServerEvent("employment_frame","FormLoad","",true);
	
	clearValue("Remarks_dec");
	clearValue("reject_reason");
	
	return disable_frame2;
	return disable_frame3;
	return disable_section;	
}

function popup_onload()
{
	var prev_ws = getValue('prevws');
	
	if(activityname=='operations' && prev_ws=='compliance')
	{
		alert('The case is refered back from compliance, Please review compliance feedback!!'); 
	}
}

function template_generate_kyc(){
	
	try
	{
		window.parent.showProcessing();	
	}catch(ex){}
	
	var response = executeServerEvent("template_generate_kyc","FormLoad","DAO_Template_kyc",true);
	
	try
	{
		window.parent.hideProcessing();	
	}catch(ex) {}
	
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
		deleteTemplateFromServer(ajaxResult[4]);
		setControlValue("is_kyc_report_onload","true");
		//showMessage("","Successful in attaching KYC template","error");
	}
	else if(AjxResponse.indexOf("0000")==-1)
	{
		setControlValue("is_kyc_report_onload","false");
		showMessage("","Error in attaching KYC template","error");
	}		
	else if(AjxResponse.indexOf("Error")!=-1)
	{
		setControlValue("is_kyc_report_onload","false");
		showMessage("","Problem in fetching attach KYC template","error");		
	}
	saveWorkItem();
}

function template_generate_risk_sheet(){
	
	try
	{
		window.parent.showProcessing();	
	}catch(ex){}
	
	var response = executeServerEvent("template_generate_risk_sheet","FormLoad","",true);
	
	try
	{
		window.parent.hideProcessing();	
	}catch(ex) {}
	
	var AjxResponse;
	var AjxResponseTxt;
	if(response.indexOf("~") != -1)
	{			
		var ajaxResult=response.split("~");
		AjxResponse=ajaxResult[0];
	}
	else
		AjxResponse=response;
	
	if(AjxResponse == "0000")
	{
		window.parent.customAddDoc(ajaxResult[1],ajaxResult[2],ajaxResult[3]);	
		deleteTemplateFromServer(ajaxResult[4]);
		setControlValue("is_risk_score_report_onload","true");
		//showMessage("","Successful in attaching Risk Sheet template","error");
	}
	else if(AjxResponse.indexOf("0000")==-1)
	{
		setControlValue("is_risk_score_report_onload","false");	
		showMessage("","Error in attaching Risk Sheet template","error");
	}		
	else if(AjxResponse.indexOf("Error")!=-1)
	{
		setControlValue("is_risk_score_report_onload","false");
		showMessage("","Problem in fetching attach Risk Sheet template","error");		
	}
	saveWorkItem();
}

function template_generate_firco(){
	
	try
	{
		window.parent.showProcessing();	
	}catch(ex){}
	
	var response = executeServerEvent("template_generate_firco","FormLoad","",true);
	
	try
	{
		window.parent.hideProcessing();	
	}catch(ex) {}
	
	var AjxResponse;
	var AjxResponseTxt;
	if(response.indexOf("~") != -1)
	{		
		var ajaxResult=response.split("~");
		AjxResponse=ajaxResult[0];
	}
	else
		AjxResponse=response;
	
	if(AjxResponse == "0000")
	{
		window.parent.customAddDoc(ajaxResult[1],ajaxResult[2],ajaxResult[3]);	
		deleteTemplateFromServer(ajaxResult[4]);
		setControlValue("is_firco_report_onload","true");
		//showMessage("Generate_firco_temp","Successful in attaching Firco Template","error");
	}
	else if(AjxResponse.indexOf("0000")==-1)
	{
		setControlValue("is_firco_report_onload","false");
		showMessage("Generate_firco_temp","Error in attaching Firco Template","error");			
	}		
	else if(AjxResponse.indexOf("Error")!=-1)
	{
		setControlValue("is_firco_report_onload","false");
		showMessage("Generate_firco_temp","Problem in fetching attach Firco Template","error");		
	}
	saveWorkItem();
}

function template_dedupe(){
	
	try
	{
		window.parent.showProcessing();	
	}catch(ex){}
	
	var response = executeServerEvent("template_generate_dedupe","FormLoad","",true);
	
	try
	{
		window.parent.hideProcessing();	
	}catch(ex) {}
	
	var AjxResponse;
	var AjxResponseTxt;
	if(response.indexOf("~") != -1)
	{		
		var ajaxResult=response.split("~");
		AjxResponse=ajaxResult[0];
	}
	else
		AjxResponse=response;
	
	if(AjxResponse == "0000")
	{
		window.parent.customAddDoc(ajaxResult[1],ajaxResult[2],ajaxResult[3]);	
		deleteTemplateFromServer(ajaxResult[4]);
		setControlValue("is_dedupe_onload","true");
		//showMessage("Generate_firco_temp","Successful in attaching Firco Template","error");
	}
	else if(AjxResponse.indexOf("0000")==-1)
	{
		setControlValue("is_dedupe_onload","false");
		showMessage("","Error in attaching Dedupe Template","error");			
	}		
	else if(AjxResponse.indexOf("Error")!=-1)
	{
		setControlValue("is_firco_report_onload","false");
		showMessage("","Problem in fetching attach Dedupe Template","error");		
	}
	saveWorkItem();
}

function populateCIF()
{
	if(ActivityName == "operations" && getValue("Dedupe_Match_found")=="Y"){
		var response = executeServerEvent("populateCIF","FormLoad","",true);
		return response;
	}
}

function populateAccountpurpose()
{
	executeServerEvent("purpose_description","FormLoad","",true);	
}


function populateDecisionDropDown()
{

	var response = executeServerEvent("DecisionDropDown","FormLoad","",true);
	return response;
	/*var response=executeServerEvent("DecisionDropDown","FormLoad","",true).trim();
	
	if (ActivityName == 'Credit')
	{
		var response1=executeServerEvent("getActivityNameBasedOnWorkitemId","FormLoad","3",true).trim(); // workitem 3 for CPV queue 
		if(response1 != "") // if blank, means collected. if not blank then decision will be disabled  
		{
			var x = document.getElementById("qDecision");
			for (var i = 0; i < x.options.length; i++) 
			{
				if(x.options[i].value=='Re-Perform CPV')
				{
					x.options[i].disabled = true;
				}
			}
		}	
		if(getValue("DecAddInfoReqTakenAtQ")=='' || getValue("DecAddInfoReqTakenAtQ")=='-')
		{
			var x = document.getElementById("qDecision");
			for (var i = 0; i < x.options.length; i++) 
			{
				if(x.options[i].value=='Submit to CROPS')
				{
					x.options[i].disabled = true;
				}
			}
		}
		else if(getValue("DecAddInfoReqTakenAtQ")!='' && getValue("DecAddInfoReqTakenAtQ")!='-')
		{
			var x = document.getElementById("qDecision");
			for (var i = 0; i < x.options.length; i++) 
			{
				if(x.options[i].value=='Approve' || x.options[i].value=='Decline' || x.options[i].value=='Re-Perform CPV')
				{
					x.options[i].disabled = true;
				}
			}
		}
		
	}
	if (ActivityName == 'CPV')
	{
		var response1=executeServerEvent("getActivityNameBasedOnWorkitemId","FormLoad","2",true).trim(); // workitem 2 for CPV queue 
		if(response1 != "") // if blank, means collected. if not blank then decision will be disabled 
		{
			var x = document.getElementById("qDecision");
			for (var i = 0; i < x.options.length; i++) 
			{
				if(x.options[i].value=='Re-Perform Credit')
				{
					x.options[i].disabled = true;
				}
			}
		}	
	}
	*/
}
