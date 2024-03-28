var DAO_Common = document.createElement('script');
DAO_Common.src = '/DAO/DAO/CustomJS/DAO_Common.js';
document.head.appendChild(DAO_Common);

function mandatoryFieldValidation(ActivityName)
{
	return true;
}

function Documentlistvalidation(){
	var documentAvail = window.parent.getInterfaceData('D');
	var flag ='Notfound'
	for (var i =0;i<documentAvail.length;i++){
		
		var docname=documentAvail[i].name;
		if(docname=='Signature'){
			flag='found';
			break;
		}
	}
	if(flag=='Notfound'){
		showMessage("","Please attach signature document!","error");
		setControlValue("Decision","");
		return false;
	}
}

function validation_on_Decision_change()
{
	
	if(getValue("Decision")=="Additional Details required from Customer" && ActivityName=='Additional_cust_details')
	{
		var row_count = getGridRowCount("AddnalDocs");
		if(row_count==0){
			showMessage("Decision","Please add documents in the 'Additional documents required' grid with status as pending to proceed with 'Additional Details required from Customer' decision ","error");
			setFocus("Decision");
			setControlValue("Decision","");
			return false;
		}
		
		var temp='Recieved';
		for(var i =0; i<row_count;i++)
		{
			var status = getValueFromTableCell("AddnalDocs",i,1);
			if(temp==status || status=='Received'){
				continue;
			}
			else {
				status='Pending';
				break;
			}
		}
		
		if(status!='Pending')
		{
			showMessage("Decision","All the documents in the 'Additional documents required' grid are received, please take another decision to proceed.","error");
			setFocus("Decision");
			setControlValue("Decision","");
			return false;
		}
	}
	
	if(getValue("Decision")=="Additional details needed" && ActivityName=='sign_upload_checker')
	{
		var row_count = getGridRowCount("AddnalDocs");
		if(row_count==0)
		{
			showMessage("Decision","Please add documents in the 'Additional documents required' grid with status as pending to proceed with 'Additional details needed' decision ","error");
			setFocus("Decision");
			setControlValue("Decision","");
			return false;
		}
		
		var temp='Recieved';
		for(var i =0; i<row_count;i++)
		{
			var status = getValueFromTableCell("AddnalDocs",i,1);
			if(temp==status || status=='Received'){
				continue;
			}
			else {
				status='Pending';
				break;
			}
		}
		if(status!='Pending')
		{
			showMessage("Decision","All the documents in the 'Additional documents required' grid are received, please take another decision to proceed.","error");
			setFocus("Decision");
			setControlValue("Decision","");
			return false;
		}
	} // POA-3353
	
	
	if(getValue("Decision")=="Additional details needed" && ActivityName=='wm_control')
	{
		var row_count = getGridRowCount("AddnalDocs");
		if(row_count==0)
		{
			showMessage("Decision","Please add documents in the 'Additional documents required' grid with status as pending to proceed with 'Additional details needed' decision ","error");
			setFocus("Decision");
			setControlValue("Decision","");
			return false;
		}
		
		var temp='Recieved';
		for(var i =0; i<row_count;i++)
		{
			var status = getValueFromTableCell("AddnalDocs",i,1);
			if(temp==status || status=='Received'){
				continue;
			}
			else {
				status='Pending';
				break;
			}
		}
		if(status!='Pending')
		{
			showMessage("Decision","All the documents in the 'Additional documents required' grid are received, please take another decision to proceed.","error");
			setFocus("Decision");
			setControlValue("Decision","");
			return false;
		}
	}
	
	if((getValue("Decision")=="send to compliance" || getValue("Decision")=="send to operations" || getValue("Decision")=="send to compliance WC" || getValue("Decision")=="Reject" || getValue("Decision")=="Initiate Account closure" || getValue("Decision")=="Additional Details required from Customer") && ActivityName=='Additional_cust_details' && getValue("prevws")=="sign_upload_checker"){
		showMessage("Decision","Please select decision as Send to sign Upload Checker","error");
		setFocus("Decision");
		setControlValue("Decision","");
		return false;
	}
	
	if((getValue("Decision")=="send to compliance" || getValue("Decision")=="send to operations" || getValue("Decision")=="send to compliance WC") && (ActivityName=='Additional_cust_details' || ActivityName=='wm_control'))
	{
		var row_count = getGridRowCount("AddnalDocs");
		for(var i =0; i<row_count;i++)
		{
			var status = getValueFromTableCell("AddnalDocs",i,1);
			var document= getValueFromTableCell("AddnalDocs",i,0); 
			if(status=='Pending')
			{
				showMessage("Decision","Please check document : '"+document+"' is pending from the customer in 'Additional document required' grid","error");
				setFocus("Decision");
				setControlValue("Decision","");
				return false;
			}
		}
	}
	
	if((getValue("Decision")=="Approve" || getValue("Decision")=="Reject") && (ActivityName=='operations' || ActivityName=='compliance' || ActivityName=='compliance_wc'))
	{
		var row_count = getGridRowCount("AddnalDocs");
		for(var i =0; i<row_count;i++)
		{
			var status = getValueFromTableCell("AddnalDocs",i,1);
			var document= getValueFromTableCell("AddnalDocs",i,0); 
			if(status=='Pending')
			{
				showMessage("Decision","Document : '"+document+"' is pending from the customer in 'Additional document required' grid, Please take another decision.' grid","error");
				setFocus("Decision");
				setControlValue("Decision","");
				return false;
			}
		}
		
	}
	// Hritik 21.9.22 
	if((getValue("Decision")!="Approve") && ActivityName=='operations' && getValue("CIF_verification_flag")=='Y')
	{
		showMessage("Decision","Please select decision as Approve!!","error");
		setFocus("Decision");
		setControlValue("Decision","");
		return false;
	}
	
	if((getValue("Decision")!="Additional details needed" && getValue("Decision")!="Reject" && getValue("Decision")!="Approve") && ActivityName=='operations' && 
	getValue("Name_modify")=='Y' && getValue("firco_hit")!='Y')
	{
		showMessage("Decision","Please select decision as 'Approve/Reject/Additional details needed' because customer name is modified","error");
		setFocus("Decision");
		setControlValue("Decision","");
		return false;
	}
	
	if(getValue("Decision")=="Approve" && ActivityName=='operations' && (getValue("Given_Name")!=getValue("DEH_FirstName") || getValue("Middle_Name")!=getValue("DEH_middleName") || getValue("Surname")!=getValue("DEH_LastName")))
	{
		
		showMessage("Decision","Information : The customer name is modified, kindly perform the checks before you approve the case!","error");
		setFocus("Decision");
		return false;
	}
	
	//vk
	if((getValue("Decision")=="Reject") && ActivityName=='Additional_cust_details' && getValue("is_stp")=='Y')
	{
		showMessage("Can not Reject as it is a STP case");
		setFocus("Decision");
		setControlValue("Decision","");
		return false;
	}
	
}

/*  if(getValue('qDecision')=="" || getValue('qDecision')=='')
	{
		showMessage('qDecision','Please select decision',"error");
		setFocus("qDecision");
		return false;
	}
	
	if(getValue("qDecision").indexOf("Reject")!=-1 || getValue("qDecision").indexOf("Reject to Initiator")!=-1
	|| getValue("qDecision").indexOf("Reject to CROPS Data Entry Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to CROPS Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to CROPS Disbursal Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to CROPS MCQ Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to Task Force Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to Outdoor Team Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to Sales Deferral Maker")!=-1
	)
	{
		setStyle("REJECT_REASON_GRID","visible","true");
		var rejReason=getGridRowCount("REJECT_REASON_GRID");
		if(parseInt(rejReason)==0)
		{
			showMessage("add_REJECT_REASON_GRID",'Please provide Reject Reasons',"error");
			setFocus("add_REJECT_REASON_GRID");
			return false;
		}
	}
   if(ActivityName=='Introduction')
	{ 
		if(getValue("CUSTOMER_NAME")=='')
		{
			showMessage('CUSTOMER_NAME','Customer Name is mandatory to enter',"error");
			setFocus("CUSTOMER_NAME");
			return false;
		}	
        if(getValue("CIF")=='')
		{
			showMessage('CIF','CIF Number is mandatory to enter',"error");
			setFocus("CIF");
			return false;
		}		
        if(getValue("DSA_ID")=='')
		{
			showMessage('DSA_ID','DSA ID is mandatory to enter',"error");
			setFocus("DSA_ID");
			return false;
		}	
        if(getValue("LOAN_AMOUNT")=='')
		{
			showMessage('LOAN_AMOUNT','Loan Amount(AED) is mandatory to enter',"error");
			setFocus("LOAN_AMOUNT");
			return false;
		}
        if(getValue("REQUEST_FOR")=='')
		{
			showMessage('REQUEST_FOR','Request For is mandatory to enter',"error");
			setFocus("REQUEST_FOR");
			return false;
		}
		
		if(getValue("APPLICATION_DATE")=='')
		{
			showMessage('APPLICATION_DATE','Application Date is mandatory to enter',"error");
			setFocus("APPLICATION_DATE");
			return false;
		}
		if(getValue("ISLAMIC_OR_CONVENTIONAL")=='')
		{
			showMessage('ISLAMIC_OR_CONVENTIONAL','Islamic or Conventional is mandatory to enter',"error");
			setFocus("ISLAMIC_OR_CONVENTIONAL");
			return false;
		}
		if(getValue("CASE_TYPE")=='')
		{
			showMessage('CASE_TYPE','Case Type is mandatory to enter',"error");
			setFocus("CASE_TYPE");
			return false;
		}
		if(getValue("HOME_IN_ONE")=='')
		{
			showMessage('HOME_IN_ONE','Home In One is mandatory to enter',"error");
			setFocus("HOME_IN_ONE");
			return false;
		}
		if(getValue("ACCOUNT_NUMBER")=='')
		{
			showMessage('ACCOUNT_NUMBER','Account Number is mandatory to enter',"error");
			setFocus("ACCOUNT_NUMBER");
			return false;
		}
		if(getValue("EMPLOYMENT_TYPE")=='')
		{
			showMessage('EMPLOYMENT_TYPE','Employment Type is mandatory to enter',"error");
			setFocus("EMPLOYMENT_TYPE");
			return false;
		}
		if(getValue("qSZHP")=='')
		{
			showMessage('qSZHP','SZHP is mandatory to enter',"error");
			setFocus("qSZHP");
			return false;
		}
		if(getValue("SourceChannel")=='')
		{
			showMessage('SourceChannel','Source Channel is mandatory to enter',"error");
			setFocus("SourceChannel");
			return false;
		}
		if(getValue("SM")=='')
		{
			showMessage('SM','SM is mandatory to enter',"error");
			setFocus("SM");
			return false;
		}
				
	}
	
	
	if(ActivityName=='CROPS_Disbursal_Maker')
	{
		if(getValue("qDecision") == "Activity Complete")
		{
			if(getValue("MCQ_REQUIRED")=='')
			{
				showMessage('MCQ_REQUIRED','MCQ Required is mandatory to enter',"error");
				setFocus("MCQ_REQUIRED");
				return false;
			}
		}
	}
	
	if(ActivityName=='Task_Force_Maker')
	{	
		if(getValue("APPOINTMENT_TYPE")=='' && getValue("qDecision")!="Reject to Initiator")
		{
			showMessage('APPOINTMENT_TYPE','Appointment Type is mandatory to enter',"error");
			setFocus("APPOINTMENT_TYPE");
			return false;
		}
	}	
	if(getValue('HOLD_TILL_DATE')=='' && getValue("qDecision")=="Hold")
	{
		showMessage('HOLD_TILL_DATE','Please mention the date till when the case will be on Hold',"error");
		setFocus("HOLD_TILL_DATE");
		return false;
	}
	
	if(ActivityName=='CROPS_Data_Entry_Maker')
	{	
		if((getValue('qDecision')=="Activity Complete" && getValue("AGREEMENT_NUMBER")==''))
		{
			showMessage('qDecision','System will allow this decision if Agreement Number is entered by the user',"error");
			setFocus("qDecision");
			return false;
		}
		if((getValue('qDecision')=="Activity Not Complete" && getValue("AGREEMENT_NUMBER")!=''))
		{
			showMessage('qDecision','System will allow this decision if Agreement Number is not entered by the user',"error");
			setFocus("qDecision");
			return false;
		}
		
		
	}
	
	
	if(ActivityName=='CPV')
	{
		var CPVDecision = getValue('qDecision');
		setControlValue("DEC_CPV",CPVDecision);
	}
	
	if(ActivityName=='Sales_Attach_Documents_1')
	{
		var SalesDoc1Decision = getValue('qDecision');
		setControlValue("DEC_SALES_ATTACH_DOC1",SalesDoc1Decision);
	}
	
	
	if(ActivityName=='CROPS_Data_Entry_Checker')
	{	
		if((getValue('qDecision')=="Approve" && getValue("AGREEMENT_NUMBER")==''))
		{
			showMessage('qDecision','System will allow this decision if Agreement Number is entered by the user',"error");
			setFocus("qDecision");
			return false;
		}
		if((getValue('qDecision')=="Activity Not Complete" && getValue("AGREEMENT_NUMBER")!=''))
		{
			showMessage('qDecision','System will allow this decision if Agreement Number is not entered by the user',"error");
			setFocus("qDecision");
			return false;
		}
		 
	}
	
	if(ActivityName=='Credit')
	{
		if(getValue('qDecision')=="Approve")
		{
			if(getValue("LOAN_AMOUNT")=='')
			{
				showMessage('LOAN_AMOUNT','Loan Amount(AED) is mandatory to enter',"error");
				setFocus("LOAN_AMOUNT");
				return false;
			}
			if(getValue("CASE_TYPE")=='')
			{
				showMessage('CASE_TYPE','Case Type is mandatory to enter',"error");
				setFocus("CASE_TYPE");
				return false;
			}
			/*if(getValue("AGREEMENT_NUMBER")=='')
			{
				showMessage('AGREEMENT_NUMBER','Agreement Number is mandatory to enter',"error");
				setFocus("AGREEMENT_NUMBER");
				return false;
			}
			if(getValue("LAF")=='')
			{
				showMessage('LAF','LAF is mandatory to enter',"error");
				setFocus("LAF");
				return false;
			}
			if(getValue("ISLAMIC_OR_CONVENTIONAL")=='')
			{
				showMessage('ISLAMIC_OR_CONVENTIONAL','Islamic or Conventional is mandatory to enter',"error");
				setFocus("ISLAMIC_OR_CONVENTIONAL");
				return false;
			}
		
			var RequestFor = getValue('REQUEST_FOR');
			setControlValue("CREDIT_APPROVAL_FOR",RequestFor);
		}
		
		var CreditDecision = getValue('qDecision');
		setControlValue("DEC_CREDIT",CreditDecision);
	}
	
	if(ActivityName=='Sales_Attach_Documents_1' || ActivityName=='Sales_Attach_Documents')
	{
		var confirmResponse=true;
		if(getValue('REQUEST_FOR')=='')
		{
			showMessage('REQUEST_FOR','Request for is mandatory to enter',"error");
			setFocus("REQUEST_FOR");
			return false;
		}
		if(getValue('qDecision')=="Submit" && getValue('REQUEST_FOR')!="Final Offer Letter")
		{
				confirmResponse = confirm("Please confirm the submission of In Principle Approval");
				if(getValue("IPA_ISSUE_DATE")!='')
				{
					var IPAIssueDT = getValue("IPA_ISSUE_DATE");
					var diffDays=getDateDifferenceIPAIssueDate(IPAIssueDT);

					setControlValue("DIFF_IPA_CURRENT_DATE",diffDays);
				}
		}
		/*if(getValue('qDecision')=="Submit")
		{
			var request_for = getValue('REQUEST_FOR');
			setControlValue("CREDIT_APPROVAL_FOR",request_for);
		}
		if(confirmResponse==true)
		{
			return true;
		}
		else
		{
			return false;
		}		
		
		if(ActivityName=='CROPS_Deferral_Checker')
		{
			if(getValue("qDecision")=="Approve")
			{
				if(getValue("qPrev2PrevWS")!="Task_Force_Maker" && getValue("qPrev2PrevWS")!="Security_Document_Hold" && getValue("qPrev2PrevWS")!="Title_Deed_Hold" && getValue("qPrev2PrevDecision")!="Deferral Documents Attached")
				{
					var DeferralGridCount=getGridRowCount("Q_USR_0_ML_DEFERRAL_DETAILS");
					var DeferralStatus;
					var DefFlag = false;
					if(DeferralGridCount>0)
					{
						for(var i=0;i<DeferralGridCount;i++)
						{
							DeferralStatus=getValueFromTableCell("Q_USR_0_ML_DEFERRAL_DETAILS",i,3);
							if(DeferralStatus != "Closed")
							{
								DefFlag = true;
								break;
							}
						}
						if(DefFlag)
						{
							showMessage('Q_USR_0_ML_DEFERRAL_DETAILS','Kindly close all deferrals before taking approve decision',"error");
							setFocus("Q_USR_0_ML_DEFERRAL_DETAILS");
							return false;
						}
					}
				}
			}
			return true;
		}
		
	}
	
	
	if(ActivityName=='Credit')
	{
		var currdate = new Date();
		var dd = currdate.getDate();
		if (dd < 10) {
				dd = '0' + dd;
			}
		var mm = (currdate.getMonth()+1);
		if (mm < 10) {
				mm = '0' + mm;
			}
		var datetime = currdate.getFullYear() + "-"
		+ mm + "-" 
		+ dd + " "  
		+ currdate.getHours() + ":"  
		+ currdate.getMinutes() + ":" 
		+ currdate.getSeconds();
		
		if(getValue('qDecision')=='Approve' && ( getValue('REQUEST_FOR')=='IPA - Express' 
		|| getValue('REQUEST_FOR')=='IPA - Credit' || getValue('REQUEST_FOR')=='IPA - Normal'))
		{
			setControlValue("IPA_ISSUE_DATE",datetime);
			
		}
	}
		
		if(ActivityName=='Credit' || ActivityName=='CPV' || ActivityName=='CROPS_Data_Entry_Checker' || ActivityName=='Initiator_Reject')
		{
				if(getValue("IPA_ISSUE_DATE")!='')
				{
					var IPAIssueDT = getValue("IPA_ISSUE_DATE");
					var diffDays=getDateDifferenceIPAIssueDate(IPAIssueDT);

					setControlValue("DIFF_IPA_CURRENT_DATE",diffDays);
				}

		}
		
	
	
	return true;
}

function getDateDifferenceIPAIssueDate(IPAIssueDate)
{
	
	IPAIssueDate = replacingMonthWithNumber(IPAIssueDate);
	
	var a = IPAIssueDate.split(" ");
	var d = "";
	var newDate="";
	var t = a[1].split(":");
	if(a[0].indexOf("/") != -1)
	{
		d=a[0].split("/");
		newDate=d[2]+'/'+d[1]+'/'+d[0]+' '+t[0]+':'+t[1]+':'+t[2];
	}
	else if(a[0].indexOf("-") != -1)
	{
		d=a[0].split("-");
		newDate=d[0]+'/'+d[1]+'/'+d[2]+' '+t[0]+':'+t[1]+':'+t[2];
	}
	
	var IPAIssueDate = new Date(newDate);
	var currentdate = new Date();
	var utc2 = Date.UTC(currentdate.getFullYear(), currentdate.getMonth(), currentdate.getDate());
	var utc1 = Date.UTC(IPAIssueDate.getFullYear(), IPAIssueDate.getMonth(), IPAIssueDate.getDate());
	var _MS_PER_DAY = 1000 * 60 * 60 * 24;
	diffDays_CBRB=Math.floor((utc2 - utc1) / _MS_PER_DAY);
	return diffDays_CBRB;
}

/*function replacingMonthWithNumber(datevalue)
{
	datevalue = datevalue.split('Jan').join('01');
	datevalue = datevalue.split('Feb').join('02');
	datevalue = datevalue.split('Mar').join('03');
	datevalue = datevalue.split('Apr').join('04');
	datevalue = datevalue.split('May').join('05');
	datevalue = datevalue.split('Jun').join('06');
	datevalue = datevalue.split('Jul').join('07');
	datevalue = datevalue.split('Aug').join('08');
	datevalue = datevalue.split('Sep').join('09');
	datevalue = datevalue.split('Oct').join('10');
	datevalue = datevalue.split('Nov').join('11');
	datevalue = datevalue.split('Dec').join('12');
	return datevalue;
}*/
	
	
	

