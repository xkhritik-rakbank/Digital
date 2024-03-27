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

var underwritingflag='N';

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

//Kamran 15052023
function checkCustNameLength(){

		var FirstNameVal = getValue("FirstName");
		var MiddleNameVal = getValue("MiddleName");
		var LastNameVal = getValue("LastName");
		var FullNameWithMiddle = (FirstNameVal + " " + MiddleNameVal + " " + LastNameVal).length;
		var FullNameWithoutMiddle = (FirstNameVal + " " + LastNameVal).length;
				if (ActivityName == 'Exceptions' && MiddleNameVal == "" && FullNameWithoutMiddle >80){
						showMessage(controlId,"Maximum accepted letter for First Name and Last Name must not exceed 80 characters","error")	
						return false;
					
				 }
				 else if (ActivityName == 'Exceptions' && MiddleNameVal != "" && FullNameWithMiddle >80){
						showMessage(controlId,"Maximum accepted letter for First Name, Middle Name and Last Name must not exceed 80 characters","error")	
						return false;
					
				 }
				
				
}

function afterFormload()
{	
	setCommonVariables();
	enableDecision();
	
	if(ActivityName=="Source_Refer") {
		insertIntoSalaryTable('formload');
	}
	if(ActivityName=="Exceptions") { // Added - Hritik 30.01.2024 PDSc - 1436
		populateExceptionAlert();
	}
		
	setStyle("Fetch_Manual_Dectech", "disable", "false");
	setStyle("Fetch_AECB_Report", "disable", "false");
	//setControlValue("LoggedInUser",user);
	//setStyle("HIDDEN_SECTION","visible","false");
	//setStyle("REJECT_REASON_GRID","visible","false");
	//setStyle("CHANNEL","disable","true");
	//setControlValue("DECISION","");
	populateDecisionDropDown();
	onLoadEnableDisable();
	readOnlyValidations();
	setControlValue("NetSalaryChange","Clicked");
	try {	
				saveWorkItem();
			} catch(ex) {
				showMessage("DECISION",'Kindly click on Save button then Close the workitem, Open it again and submit with appropriate decision.',"error");
				return false;
			}
	//enableDisableAfterFormLoad();
	//if (ActivityName == 'Initiation')
		//loadSolId(user);	
}

function readOnlyValidations()
{
                var readOnlyflag=(parent.document.title).indexOf("(read only)");
                //console.log('readOnlyflag--'+readOnlyflag);
                if(readOnlyflag > 0) // workitem opened in ReadOnly Mode
                {
                                var status= executeServerEvent("DCC_Form", "ReadOnly", "", false);
                }
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
function isDocTypeAttached(docattached)
{
	var docInterface = window.parent.getInterfaceData('D');
	var docListSize= docInterface.length;
	var attachedFlag =  false;   
	for(var docCounter=0;docCounter<docListSize;docCounter++)
	{
		var docName = docInterface[docCounter].name;
		if(docName == docattached)
		{
			attachedFlag = true;
			break;			
		}		
	}
	return attachedFlag;
}

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
		var underWritingLimitChange = getValue("underWritingLimitChange");
	    if (SalaryChangeflag == "Unclicked"){
		showMessage('Decision','Net salary is changed. Please execute Dectech call',"error");
		setFocus("Decision");
		return false;	
	   }
	   
	   if (underWritingLimitChange== "Y"){
		showMessage('Decision','Underwriting value is changed. Please execute Dectech call',"error");
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
		
		if(ActivityName=="DCC_Experience" && getValue('Decision') == "Rescheduled"){
		setControlValue("CardOps_Reschedule","Y");
		}
		
		if(ActivityName=="Exceptions" && getValue("Decision")!='Refer to source')
		{
		if(!isDocTypeAttached("NON_STP_CAM_Report")){
			showMessage('GenerateCam','Please generate NON_STP_CAM_Report...',"error");
			return false;
		}
		
		var FirstNameVal = getValue("FirstName");
		var MiddleNameVal = getValue("MiddleName");
		var LastNameVal = getValue("LastName");
		var FullNameWithMiddle = (FirstNameVal + " " + MiddleNameVal + " " + LastNameVal).length;
		var FullNameWithoutMiddle = (FirstNameVal + " " + LastNameVal).length;
				if (MiddleNameVal == "" && FullNameWithoutMiddle >80){
						showMessage('',"Maximum accepted letter for First Name and Last Name must not exceed 80 characters","error")	
						return false;
					
				 }
				 else if (MiddleNameVal != "" && FullNameWithMiddle >80){
						showMessage('',"Maximum accepted letter for First Name, Middle Name and Last Name must not exceed 80 characters","error")	
						return false;
					
				 }
		
		}
		
		if(!isDocTypeAttached("Customer_Consent_Form_Signed") && ActivityName == "Card_Ops" && getValue('Decision') == "Approve"){
			showMessage('','Please get "Customer_Consent_Form_Signed" document added...',"error");
			return false;
		}
		
		//if(getValue('Decision')=="" || getValue('Decision')=='Select')
				//showMessage("DECISION",'Kindly select Decision.',"error");
				//return false;
		var ensureDelegationCheck = getValue('EnsureDelegationCheck');
        if(ActivityName=="Exceptions" && ensureDelegationCheck ==  false && getValue("Decision")!='Refer to source')
		{
			showMessage('ensureDelegationCheck','Please check Ensure delegation is in place to approve',"error");
		return false;
		}

		//Kamran 15052023
		
		
		var confirmDoneResponse = confirm("You are about to submit the workitem. Do you wish to continue?");
		
		if(confirmDoneResponse ==  true)
		{
			
			if(ActivityName=='ETB_Intro')
			{
			var etbStatus = etbInsertion();
			if(etbStatus != 'INSERTED' && etbStatus != ''){
				showMessage("DECISION",'Insert or Update failed.',"error");
				return false;
			}
			}
			
			var Decision = getValue('Decision');
			var ntb_val = getValue('NTB');
			
			if(ActivityName=='Card_Ops' && Decision=='Approve' && ntb_val=='false')
			{
				var opsstatus = cardopsdecision();
				if(opsstatus != 'Updated' ){
				showMessage("DECISION",'Update failed',"error");
				return false;
				}
			}
			
			var status = insertIntoHistoryTable();
			if(ActivityName=='Source_Refer')
			{
				var Decision = getValue('Decision');
				setControlValue("Source_refer_decision",Decision);			
			}
			insertIntoSalaryTable('introdone');
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
			if(ActivityName=="Exceptions"){
			setControlValue("NetSalaryChange","Unclicked");		   
			}
			setStyle('BankingDtlsSave','disable','false');
				break;
		case 'Net_Salary2_change' : 
		setStyle('BankingDtlsSave','disable','false');
		if(ActivityName=="Exceptions"){		   
		   setControlValue("NetSalaryChange","Unclicked");
		}
				break;
        case 'Net_Salary3_change' :		
		   setStyle('BankingDtlsSave','disable','false');
		   if(ActivityName=="Exceptions"){	
		   setControlValue("NetSalaryChange","Unclicked");
		   }
				break;	
		case 'Net_salary1_date_change' :
			setStyle('BankingDtlsSave','disable','false');
				break;

		case 'Net_salary1_date_focus' :
			setStyle('BankingDtlsSave','disable','false');
				break;
		case 'Underwriting_Limit_change':
			var Final_Limit_val = getValue("Final_Limit");
			var Underwriting_Limit_val = getValue("Underwriting_Limit");
			Underwriting_Limit_val=parseInt(Underwriting_Limit_val);
			Final_Limit_val==parseInt(Final_Limit_val);
			if(Final_Limit_val<Underwriting_Limit_val){
				//showMessage('BankingDtlsSave','Underwriting limit value should be less than final limit',"error");
				showMessage('BankingDtlsSave','Please Validate Underwriting limit.',"error");
				//setControlValue("Underwriting_Limit","0.00");
				setFocus("Underwriting_Limit");
			}
			else{
				setControlValue("underWritingLimitChange","Y");
				underwritingflag='Y';
				saveWorkItem();
			}
			
			 break;
	case 'RM_Code_change' : 
			confirm("This action will change the RM CODE.");
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
				saveWorkItem();
				showMessage('BankingDtlsSave','Net salary is updated',"error");
				setControlValue("overrideIncomeFromDectech","false");
				setStyle('BankingDtlsSave','disable','true');
			    executeServerEvent("BankingDtlsSave","Click","",true);
				break;
		case 'Old_Salary_click':
			    var oldSalary = executeServerEvent("Old_Salary","Click","",true);
				showMessage('Old_Salary',oldSalary,"error");
				break;
		case 'GenerateCam_click':
				saveWorkItem();
			    var response = executeServerEvent("GenerateCam","Click","",true);
				
				if(response == 'First Name is Mandatory')
			    	{
						showMessage('FirstName','First Name is Mandatory! Kindly Enter First Name',"error");
			    	}
				else if(response == 'Last Name is Mandatory')
			    	{
						showMessage('LastName','Last Name is Mandatory! Kindly Enter Last Name',"error");
			    	}
				
			    else if(response == 'FAIL')
			    	{
						showMessage('Decision','Some server Side error occured while generating CAM!',"error");
			    	}
			    else
			    	{
			    		if(response.indexOf('~')!=-1)
			    			{
			    				var arr = response.split("~");
			    				if(arr.length==2)
			    					{
				    					try 
				    					{
				    				    	window.parent.WFSave();
				    					} catch(ex)
				    					{
				    						//winref.document.write("******ex="+ex.description);
				    					}
			    					
				    					window.parent.customAddDoc(arr[1],arr[2],"new");
										showMessage('Decision','Non-STP CAM is generated!',"error");
			    					}
			    				else
			    					showMessage('Decision','Some server Side error occured while generating CAM!',"error");
			    					
			    			}
			    		else
			    			showMessage('Decision','Some server Side error occured while generating CAM!',"error");
			    		
			    	}
				break;
				
		case 'Fetch_Manual_Dectech_click':
		        clearTable("Deviation_Desc_Grid",true);
				setControlValue("NetSalaryChange","Clicked");
				
				//underwritingflag='N'
				saveWorkItem();
				var result = executeServerEvent("Fetch_Manual_Dectech","Click","",true);
				if(result == "DECTECH CALL SUCCESS"){
					setControlValue("underWritingLimitChange","N");
					saveWorkItem();
					showMessage(controlId,'DECTECH CALL SUCCESS',"error");
				}
				if(result == "INVALID SESSION"){
					showMessage(controlId,'INVALID SESSION',"error");
				}
				//showMessage(controlId,result,"confirm")
				break;
				
				
        //Kamran 06042023				
		case 'getDocPassword_click':
				var result = executeServerEvent("getDocPassword","Click","",true);
				if(result==''){
					showMessage(controlId,"No password record present for the case","confirm")	
				}
				else{
				    showMessage(controlId,result,"confirm")
				}
				
				break;
				
		case 'rerun_aecb_change':	
				saveWorkItem();
				break;
		
		//Kamran 11052023				
		case 'FirstName_change':
		var FirstNameVal = getValue("FirstName");
		var MiddleNameVal = getValue("MiddleName");
		var LastNameVal = getValue("LastName");
		var FullNameWithMiddle = (FirstNameVal + " " + MiddleNameVal + " " + LastNameVal).length;
		var FullNameWithoutMiddle = (FirstNameVal + " " + LastNameVal).length;
				if (ActivityName == 'Exceptions' && MiddleNameVal == "" && FullNameWithoutMiddle >80){
						showMessage(controlId,"Maximum accepted letter for First Name and Last Name must not exceed 80 characters","error")	
						return false;
					
				 }
				 else if (ActivityName == 'Exceptions' && MiddleNameVal != "" && FullNameWithMiddle >80){
						showMessage(controlId,"Maximum accepted letter for First Name, Middle Name and Last Name must not exceed 80 characters","error")	
						return false;
					
				 }
				  else if (ActivityName == 'Exceptions' && MiddleNameVal == "" && FullNameWithoutMiddle <80){
						setControlValue("CUSTOMERNAME",FirstNameVal + " " + LastNameVal);
						saveWorkItem();
					
				 }
				  else if (ActivityName == 'Exceptions' && MiddleNameVal != "" && FullNameWithoutMiddle <80){
						setControlValue("CUSTOMERNAME",FirstNameVal + " " + MiddleNameVal + " " + LastNameVal);
						saveWorkItem();
				 }
				
				break;
				
				
		//Kamran 11052023			
		case 'MiddleName_change':
		var FirstNameVal = getValue("FirstName");
		var MiddleNameVal = getValue("MiddleName");
		var LastNameVal = getValue("LastName");
		var FullNameWithMiddle = (FirstNameVal + " " + MiddleNameVal + " " + LastNameVal).length;
		var FullNameWithoutMiddle = (FirstNameVal + " " + LastNameVal).length;
				if (ActivityName == 'Exceptions' && MiddleNameVal == "" && FullNameWithoutMiddle >80){
						showMessage(controlId,"Maximum accepted letter for First Name and Last Name must not exceed 80 characters","error")	
						return false;
					
				 }
				 else if (ActivityName == 'Exceptions' && MiddleNameVal != "" && FullNameWithMiddle >80){
						showMessage(controlId,"Maximum accepted letter for First Name, Middle Name and Last Name must not exceed 80 characters","error")	
						return false;
					
				 }
				    else if (ActivityName == 'Exceptions' && MiddleNameVal == "" && FullNameWithoutMiddle <80){
						setControlValue("CUSTOMERNAME",FirstNameVal + " " + LastNameVal);
						saveWorkItem();
					
				 }
				  else if (ActivityName == 'Exceptions' && MiddleNameVal != "" && FullNameWithoutMiddle <80){
						setControlValue("CUSTOMERNAME",FirstNameVal + " " + MiddleNameVal + " " + LastNameVal);
						saveWorkItem();
				 }
				
				break;
				
				
		//Kamran 11052023				
		case 'LastName_change':
		var FirstNameVal = getValue("FirstName");
		var MiddleNameVal = getValue("MiddleName");
		var LastNameVal = getValue("LastName");
		var FullNameWithMiddle = (FirstNameVal + " " + MiddleNameVal + " " + LastNameVal).length;
		var FullNameWithoutMiddle = (FirstNameVal + " " + LastNameVal).length;
				if (ActivityName == 'Exceptions' && MiddleNameVal == "" && FullNameWithoutMiddle >80){
						showMessage(controlId,"Maximum accepted letter for First Name and Last Name must not exceed 80 characters","error")	
						return false;
					
				 }
				 else if (ActivityName == 'Exceptions' && MiddleNameVal != "" && FullNameWithMiddle >80){
						showMessage(controlId,"Maximum accepted letter for First Name, Middle Name and Last Name must not exceed 80 characters","error")	
						return false;
					
				 }
				   else if (ActivityName == 'Exceptions' && MiddleNameVal == "" && FullNameWithoutMiddle <80){
						setControlValue("CUSTOMERNAME",FirstNameVal + " " + LastNameVal);
						saveWorkItem();
					
				 }
				  else if (ActivityName == 'Exceptions' && MiddleNameVal != "" && FullNameWithoutMiddle <80){
						setControlValue("CUSTOMERNAME",FirstNameVal + " " + MiddleNameVal + " " + LastNameVal);
						saveWorkItem();
				 }
				
				break;
				
				
		case 'Fetch_AECB_Report_click':
				if(ActivityName=="Source_Refer")
				{
					var url_val=getValue("AECB_URL");
					if(url_val=='' || url_val==null){
						showMessage(controlId,"No value presentfor AECB URL","confirm")	
					}
					else{
					window.open(getValue("AECB_URL"),"_self")
					}
				}
				else
				{
					var result = executeServerEvent("Fetch_AECB_Report","Click","",true);
					if(result==''){
						showMessage(controlId,"No record present, kindly re run the AECB call again","confirm")	
					}
					else{
						window.open(result);
					}
				}
				break;
			case 'Remarks_change':
			if(ActivityName=="Sys_WI_Update")
			{
				var val_remarks=getValue('Remarks');
				var len_remarks=getValue('Remarks').length;
				//const specialchar=`\`!@#%^&*()_+\=\[\]{}:"\\|<>\/?~`;	
				
				if(len_remarks>100)
				{
					showMessage('Remarks','Length of remarks must be les than 100' ,"error");
					setFocus("Remarks");
				}	
				
					}
			break;
			
			case 'LOB_change':
				var value=getValue('LOB');
				const pattern= /^(0[0-9]|[0-9][0-9])\.(0[0-9]|1[0-2])$/;
				var ret = pattern.test(value);
				if(ret == false) {
					showMessage(controlId,"Please enter correct format(YY.MM)","error")	
					setControlValue(controlId,'');
				}
			break;
			
		case 'click':
				break;	
	}
}




