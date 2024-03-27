var DCC_onLoad = document.createElement('script');
	DCC_onLoad.src = '/DCC/DCC/CustomJS/DCC_onLoad.js';
	document.head.appendChild(DCC_onLoad);
	
var DCC_mandatory = document.createElement('script');
	DCC_mandatory.src = '/DCC/DCC/CustomJS/DCC_MandatoryFieldValidations.js';
	document.head.appendChild(DCC_mandatory);

	
var DCC_onSaveDone = document.createElement('script');
DCC_onSaveDone.src = '/DCC/DCC/CustomJS/DCC_onSaveDone.js';
document.head.appendChild(DCC_onSaveDone);

var DCC_Common = document.createElement('script');
DCC_Common.src = '/DCC/DCC/CustomJS/DCC_Common.js';
document.head.appendChild(DCC_Common);






function setCommonVariables()
{
	Processname = getWorkItemData("ProcessName");
	ActivityName =getWorkItemData("ActivityName");
	WorkitemNo =getWorkItemData("processinstanceid");
	cabName =getWorkItemData("cabinetname");
	user= getWorkItemData("username");
	//alert("user--"+user);
	viewMode=window.parent.wiViewMode;	
}

function enableDecision()
{
	var WSName=getWorkItemData("ActivityName");
	if(WSName=="Firco" || WSName=="Exceptions")
	{
		setStyle("Decision", "disable", "false");
		//alert("Reddy"+WSName);
		//var DisableFrames = executeServerEvent("DisableFrames","FORMLOAD", getWorkItemData("ActivityName"), true);
	}
}

function afterFormload()
{	
	setCommonVariables();
	enableDecision();

	setStyle("Fetch_Manual_Dectech", "disable", "false");
	setStyle("Fetch_AECB_Report", "disable", "false");
	//setControlValue("LoggedInUser",user);
	//setStyle("HIDDEN_SECTION","visible","false");
	//setStyle("REJECT_REASON_GRID","visible","false");
	//setStyle("CHANNEL","disable","true");
	//setControlValue("DECISION","");
	populateDecisionDropDown();
	onLoadEnableDisable();
	setControlValue("NetSalaryChange","Clicked");
		
	//enableDisableAfterFormLoad();
	//if (ActivityName == 'Initiation')
		//loadSolId(user);	
}

function popup_addDoc()
{
	var totalRow = getGridRowCount('AddnalDocs');
	if(ActivityName=="Firco" && totalRow == 0 )
	{
		//alert('Please add at least one Docoment for Firco');
		showMessage('Decision','Please add at least one Docoment for Firco',"error");
		setFocus("Decision");
	    return false;
	}
	return true;
	
}
/*function checkDecisionValue()
{
	var totalRow = getGridRowCount('AddnalDocs');
	if(getValue('Decision')=="" || getValue('Decision')=='Select')
	{
		showMessage('Decision','Please select decision',"error");
		setFocus("Decision");
		return false;
	}
	return true;
}*/


function customValidationsBeforeSaveDone(op)
{	
    
	//enabledisabledate();
	if(op=="S")
	{
		return true;
	}
	else if (op=="I" || op=="D")
	{
		var SalaryChangeflag = getValue("NetSalaryChange");
	    if (SalaryChangeflag == "Unclicked"){
		showMessage('Decision','Net salary is changed. Please execute Dectech call',"error");
		setFocus("Decision");
		return false;	
	   }
	
		/*if(!checkDecisionValue())
		{
			return false;
		}*/
		if(!popup_addDoc())
		{
			return false;
		}
		
		//if(getValue('Decision')=="" || getValue('Decision')=='Select')
				//showMessage("DECISION",'Kindly select Decision.',"error");
				//return false;
		
		
		var confirmDoneResponse = confirm("You are about to submit the workitem. Do you wish to continue?");
		
		if(confirmDoneResponse ==  true)
		{	
			var status = insertIntoHistoryTable();
			
			try {	
				saveWorkItem();
			} catch(ex) {
				showMessage("DECISION",'Kindly click on Save button then Close the workitem, Open it again and submit with appropriate decision.',"error");
				return false;
			}
			
			
			
			if(status != 'INSERTED')
				return false;
			
			return true;
		}
		else
		{
			return false;
		}
	}
	return false;
}




function eventDispatched(controlObj,eventObj)
{
	var controlId=controlObj.id;
	var controlEvent=eventObj.type;
	var ControlIdandEvent = controlId+'_'+controlEvent;

	
	switch(controlId,controlEvent)
	{
		case 'change' : 
				executeServerEvent(controlId,controlEvent) 
				break;
		
		case 'click':
		        //executeServerEvent(controlId,controlEvent)
				break;	
						
	}
	
	
	
	 switch(ControlIdandEvent)
	{
		
		case 'Net_Salary1_change' : 
		   setControlValue("NetSalaryChange","Unclicked");
		   setStyle('BankingDtlsSave','disable','false');
				break;
		case 'Net_Salary2_change' : 
		   setStyle('BankingDtlsSave','disable','false');
		   setControlValue("NetSalaryChange","Unclicked");
				break;
        case 'Net_Salary3_change' : 
		   setStyle('BankingDtlsSave','disable','false');
		   setControlValue("NetSalaryChange","Unclicked");
				break;				
			
		case 'Year_test_change' : 
				clearComboOptions("month_test");
				loadYearDropDown();
		        //executeServerEvent(controlId,"change","",true);
				//executeServerEvent(controlId,controlEvent) 
				break;
		case 'overrideIncomeFromDectech_change':
				executeServerEvent("overrideIncomeFromDectech","Click","",true);
				break;
		case 'BankingDtlsSave_click':
				//showMessage(controlId,'Net salary is updated. ,"confirm");
				showMessage('Decision','Net salary is updated',"error");
				setControlValue("overrideIncomeFromDectech","false");
				setStyle('BankingDtlsSave','disable','true');
			    executeServerEvent("BankingDtlsSave","Click","",true);
				break;
		case 'GenerateCam_click':
			    executeServerEvent("GenerateCam","Click","",true);
				showMessage('Decision','Cam is generated...',"error");
				//alert('Cam is generated...');
				break;
				
		case 'Fetch_Manual_Dectech_click':
				setControlValue("NetSalaryChange","Clicked");
				var result = executeServerEvent("Fetch_Manual_Dectech","Click","",true);
				showMessage(controlId,result,"confirm")
				break;
				
				
		case 'Fetch_AECB_Report_click':
				var result = executeServerEvent("Fetch_AECB_Report","Click","",true);
				if(result==''){
					showMessage(controlId,"No record present, kindly re run the AECB call again","confirm")	
				}
				else{
					window.open(result);
				}
				
				break;
				
				
						
		case 'click':
				break;	
	}
	

}



