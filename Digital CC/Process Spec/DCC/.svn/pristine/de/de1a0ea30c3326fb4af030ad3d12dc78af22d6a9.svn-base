var DCC_Common = document.createElement('script');
DCC_Common.src = '/DCC/DCC/CustomJS/DCC_Common.js';
document.head.appendChild(DCC_Common);


function mandatoryFieldValidation()
{
	if(getValue('DECISION')=="" || getValue('DECISION')=='')
	{
		showMessage('DECISION','Selecting a Decision is Mandatory to proceed further',"error");
		setFocus("DECISION");
		return false;
	}
	
	if(ActivityName=="Initiation")
	{		
		if(getValue('CORPORATE_CIF')=="" || getValue('CORPORATE_CIF')=='' || getValue('CORPORATE_CIF')==null)
		{			
			showMessage('CORPORATE_CIF','Please mention Corporate CIF',"error");
			setFocus("CORPORATE_CIF");
			return false;
		}
		
		if(getValue('REQUEST_BY_SIGNATORY_CIF')=="" || getValue('REQUEST_BY_SIGNATORY_CIF')=='' || getValue('REQUEST_BY_SIGNATORY_CIF')==null)
		{			
			showMessage('REQUEST_BY_SIGNATORY_CIF','Please mention Request By (Signatory): CIF',"error");
			setFocus("REQUEST_BY_SIGNATORY_CIF");
			return false;
		}				
	}
	
	if(ActivityName=="OPS_Maker")
	{	
		if(getValue("DECISION")=="Submit")	
		{
			if(getValue('REQUEST_FOR_CIF')=="" || getValue('REQUEST_FOR_CIF')=='' || getValue('REQUEST_FOR_CIF')==null)
			{	
				showMessage('REQUEST_FOR_CIF','Please mention Request For CIF',"error");
				setFocus("REQUEST_FOR_CIF");
				return false;
			}
			
			if(getValue('ACCOUNT_NUMBER')=="" || getValue('ACCOUNT_NUMBER')=='' || getValue('ACCOUNT_NUMBER')==null)
			{		
				showMessage('ACCOUNT_NUMBER','Please mention Account No',"error");
				setFocus("ACCOUNT_NUMBER");
				return false;
			}
			
			if(getValue('ISEXISTINGCUSTOMER')=="" || getValue('ISEXISTINGCUSTOMER')=='' || getValue('ISEXISTINGCUSTOMER')==null)
			{		
				showMessage('ISEXISTINGCUSTOMER','Please select Existing Customer',"error");
				setFocus("ISEXISTINGCUSTOMER");
				return false;
			}
			
			if(getValue("WI_ORIGIN")!="Third_Party")
			{
				if(getValue('FIRST_NAME')=="" || getValue('FIRST_NAME')=='' || getValue('FIRST_NAME')==null)
				{	
					showMessage('FIRST_NAME','Please mention First Name',"error");
					setFocus("FIRST_NAME");
					return false;
				}
				
				if(getValue('LAST_NAME')=="" || getValue('LAST_NAME')=='' || getValue('LAST_NAME')==null)
				{	
					showMessage('LAST_NAME','Please mention Last Name',"error");
					setFocus("LAST_NAME");
					return false;
				}
				if(getValue('PASSPORT_NUMBER')=="" || getValue('PASSPORT_NUMBER')=='' || getValue('PASSPORT_NUMBER')==null)
				{	
					showMessage('PASSPORT_NUMBER','Please mention Passport Number',"error");
					setFocus("PASSPORT_NUMBER");
					return false;
				}
				if(getValue('CARD_EMBOSSING_NAME')=="" || getValue('CARD_EMBOSSING_NAME')=='' || getValue('CARD_EMBOSSING_NAME')==null)
				{	
					showMessage('CARD_EMBOSSING_NAME','Please mention Card Embossing Name',"error");
					setFocus("CARD_EMBOSSING_NAME");
					return false;
				}
			}
			
			if(getValue("ISEXISTINGCUSTOMER")=="No")
			{
				var rtnstatus = isDocTypeAttached("Central_Bank_Check_Result");
				if (rtnstatus == false)
				{
					showMessage('','Please attach Central_Bank_Check_Result Document to proceed further',"error");
					return false;
				}
				rtnstatus = isDocTypeAttached("World_Check_Result");
				if (rtnstatus == false)
				{
					showMessage('','Please attach World_Check_Result Document to proceed further',"error");
					return false;
				}
				rtnstatus = isDocTypeAttached("BO_Check_Result");
				if (rtnstatus == false)
				{
					showMessage('','Please attach BO_Check_Result Document to proceed further',"error");
					return false;
				}
			}
		}	
		
	}
	
	if(ActivityName=="OPS_Checker")
	{
		if(getValue("DECISION")=="Approve")	
		{
			var r = confirm("Kindly confirm, CIF Approved & Linked to Corporate CIF");
			if (r == false)
			{
				return false;
			}
		}
	}
	
	if(ActivityName!="OPS_Checker")
	{
		if(getValue("DECISION")=="Reject")	
		{
			if(getValue('REMARKS')=="" || getValue('REMARKS')=='' || getValue('REMARKS')==null)
			{
				showMessage('REMARKS','Please mention Remarks',"error");
				setFocus("REMARKS");
				return false;
			}
		}
	}
	
    if(getValue("DECISION").indexOf("Reject")!=-1)
	{	

		var rejectReasonsGridLength=getGridRowCount('REJECT_REASON_GRID');
		if(rejectReasonsGridLength == 0)
		{
			showMessage('REJECT_REASON_GRID','Please enter atleast one reject reason',"error");
			setFocus("REJECT_REASON_GRID");
			return false;
		}
		if(rejectReasonsGridLength>0)
		{
			for(var i=0;i<rejectReasonsGridLength;i++)
			{
				var cellVal1 = getValueFromTableCell("REJECT_REASON_GRID",i,0);
				if(cellVal1=="Select")
				{
				  showMessage('REJECT_REASON_GRID','Select cannot be selected as a reject reason',"error");
				  setFocus("REJECT_REASON_GRID");
				  return false;
				
				}
			}
		}
	}	
}

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

