var DCC_Common = document.createElement('script');
DCC_Common.src = '/DCC/DCC/CustomJS/DCC_Common.js';
document.head.appendChild(DCC_Common);

function loadSolId()
{
	
	var solId = executeServerEvent("SolId","FormLoad",user,true).trim();
	setControlValue("Sol_Id",solId);
}

function onLoadEnableDisable()
{
//added by gaurav	
	var response = executeServerEvent("onLoadEnableDisable","FormLoad","",true);
	return response;
}
/*
function popup_addDoc_grid()
{
//added by gaurav
	var docName = getValue('AddnalDocs_doctype');
	if( docName == "" || docName == null )
	{
		//alert('Please add at least one Docoment for Firco');
		showMessage('Decision','Please select Docoment Type',"error");
		setFocus("Decision");
	    return false;
	}
	return true;
	
}
*/
function loadYearDropDown()
//added by gaurav
{
	var dd1Years = document.getElementById("Year_test");
	var currentYear = (new Date()).getFullYear();
	for (var i = currentYear; i >= 1950; i--){
		var option = document.createElement("OPTION");
		option.innerHTML = i;
		option.value = i;
		dd1Years.appendChild(option);
		
	}
	
	//for month drop down
	var now = new Date();
	var dd1Months = document.getElementById("month_test");
	var theMonths = ["January","February","March","April","May","June","July","August","September","October","November","December"]
	var year = getValue('Year_test');
	var currentMonth = theMonths[now.getMonth()];
	var index = 0;
	var j=0;
	if(year == now.getFullYear()){
		for(; j<theMonths.length;j++)
		{
			if(currentMonth == theMonths[j]){
				index = j;
				break;
			}
		}
		for (var i = 0; i <= index; i++){
			var option = document.createElement("OPTION");
			option.innerHTML = theMonths[i];
			option.value = theMonths[i];
			dd1Months.appendChild(option);
		
		}
		
	}else{
		
		for (var i = 0; i < 12; i++){
			var option = document.createElement("OPTION");
			option.innerHTML = theMonths[i];
			option.value = theMonths[i];
			dd1Months.appendChild(option);
		
		}
	}
		
}

function populateExceptionAlert()
{
	var response = executeServerEvent("Exception_popup","FormLoad","",true);
	if(response=='Completed') {
		showMessage('','The FTS data is received and documents are attached to the workitem, Kindly proceed accordingly.',"error");
	}
	else if(response=='Ready') {
		showMessage('','The FTS data is received but documents are yet to be attached to the workitem, Kindly proceed after sometime.',"error");
	}
	return response;
}



function populateDecisionDropDown()
{
	
	var response = executeServerEvent("DecisionDropDown","FormLoad","",true);
	return response;
	/*if (ActivityName == 'OPS_Maker' || ActivityName == 'OPS_Checker')
	{
		if(getValue("WI_ORIGIN")=="Third_Party")
		{
			var x = document.getElementById("DECISION");
			for (var i = 0; i < x.options.length; i++) 
			{
				if(x.options[i].value=='Reject to Initiator')
				{
					x.options[i].disabled = true;
				}
			}
		}
		if(getValue("WI_ORIGIN")!="Third_Party")
		{
			var x = document.getElementById("DECISION");
			for (var i = 0; i < x.options.length; i++) 
			{
				if(x.options[i].value=='Reject')
				{
					x.options[i].disabled = true;
				}
			}
		}	
		
	}*/
}

function enableDisableAfterFormLoad()
{
	if(ActivityName=="Initiation")
	{	
	    setStyle("CORPORATE_CIF","disable","false");
		setStyle("REQUEST_BY_SIGNATORY_CIF","disable","false");
	
	}
	
	if(ActivityName=="OPS_Maker")
	{		
		setStyle("REQUEST_FOR_CIF","disable","false");
		setStyle("ACCOUNT_NUMBER","disable","false");
		//setStyle("SCHEME_TYPE","disable","false");
		//setStyle("SCHEME_CODE","disable","false");
		//setStyle("IS_RETAIL_CUSTOMER","disable","false");
		setControlValue("IS_RETAIL_CUSTOMER","No");
		setStyle("ISEXISTINGCUSTOMER","disable","false");
		
		if(getValue("WI_ORIGIN")=="Third_Party")
		{
			//setStyle("COUNTRY_OF_RESIDENCE","disable","false");
		} 
		else
		{
			//setStyle("GENDER","disable","false");
			//setStyle("DATE_OF_BIRTH","disable","false");
			//setStyle("TITLE","disable","false");
			setStyle("FIRST_NAME","disable","false");
			setStyle("MIDDLE_NAME","disable","false");
			setStyle("LAST_NAME","disable","false");
			//setStyle("MOTHERS_MAIDEN_NAME","disable","false");
			//setStyle("COUNTRY_OF_RESIDENCE","disable","false");
			//setStyle("NATIONALITY","disable","false");
			//setStyle("UAERESIDENT","disable","false");
			//setStyle("EMIRATES_ID","disable","false");
			setStyle("PASSPORT_NUMBER","disable","false");
			//setStyle("VISA_UID_NUMBER","disable","false");
			setStyle("CARD_EMBOSSING_NAME","disable","false");
			//setStyle("EMAIL_ID","disable","false");
			//setStyle("MOB_NUMBER_COUNTRY_CODE","disable","false");
			//setStyle("MOBILE_NUMBER","disable","false");
		}
	
	}
	//Added by Reddy-Start
	if(ActivityName=="FIRCO")
	{
		var DisableFrames = executeServerEvent("DisableFrames","FORMLOAD", getWorkItemData("ActivityName"), true);
	}
	//Added by Reddy-End
}















