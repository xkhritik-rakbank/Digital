function enableDisableRejectReasons()
{
	/*if(getValue("qDecision").indexOf("Reject")!=-1 || getValue("qDecision").indexOf("Reject to Initiator")!=-1
	|| getValue("qDecision").indexOf("Reject to CROPS Data Entry Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to CROPS Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to CROPS Disbursal Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to CROPS MCQ Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to Task Force Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to Outdoor Team Maker")!=-1
	|| getValue("qDecision").indexOf("Reject to Sales Deferral Maker")!=-1
	|| getValue("qDecision").indexOf("Decline")!=-1
	)
	{
	setStyle("REJECT_REASON_GRID","visible","true");
	var rejReason=getGridRowCount("REJECT_REASON_GRID");
	if(parseInt(rejReason)==0)
	{
		showMessage("add_REJECT_REASON_GRID",'Please provide Reject Reasons',"error");
		return false;
	}  
	}
	else
	{
		setStyle("REJECT_REASON_GRID","visible","false");
		clearTable("REJECT_REASON_GRID",true);
	}*/
}
function ValidateAlphaNumeric(rowIndex,colIndex,ref,controlId)
{
	/*
    var Defgridsize=getGridRowCount('Q_USR_0_ML_DEFERRAL_DETAILS');
	var value="";
	for(var i=0;i<Defgridsize;i++)
	{
		if(colIndex == 2)
		{
		value=getValueFromTableCell(controlId,i,colIndex);			
		var pattern = /^([0-9]{2})\/([0-9]{2})\/([0-9]{4})$/;
		if (!pattern.test(value)) 
		{
			alert('Invalid date format.Date should be in dd/mm/yyyy format');		
			setTableCellData(controlId,rowIndex,colIndex,"",true);
			setFocus("Q_USR_0_ML_DEFERRAL_DETAILS");
			return false;
		}			
		else
		{
			var currentTime = new Date();
			var dd = currentTime.getDate();
			var mm = currentTime.getMonth() + 1; //January is 0!            
			var yyyy = currentTime.getFullYear();            
			var arrStartDate = value.split("/");            
			var date2 = new Date(arrStartDate[2], arrStartDate[1] - 1, arrStartDate[0]);
			var timeDiffPassport = date2.getTime() - currentTime.getTime();
			
			if(timeDiffPassport < 0)
			{
				alert('Date should be future date.');
				setTableCellData(controlId,rowIndex,colIndex,"",true);
				setFocus("Q_USR_0_ML_DEFERRAL_DETAILS");
				return false;			
			}
	
		 }
	 }
 }	*/
		
}

