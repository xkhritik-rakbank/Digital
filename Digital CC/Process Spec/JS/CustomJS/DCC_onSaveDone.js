var DCC_onLoad = document.createElement('script');
	DCC_onLoad.src = '/DCC/DCC/CustomJS/DCC_onLoad.js';
	document.head.appendChild(DCC_onLoad);
	
var DCC_mandatory = document.createElement('script');
DCC_mandatory.src = '/DCC/DCC/CustomJS/DCC_MandatoryFieldValidations.js';
document.head.appendChild(DCC_mandatory);

	

var DCC_Common = document.createElement('script');
DCC_Common.src = '/DCC/DCC/CustomJS/DCC_Common.js';
document.head.appendChild(DCC_Common);
	




function insertIntoHistoryTable()
{
	//var rejectReasonsGridLength=getGridRowCount('REJECT_REASON_GRID');
	var historyTableInsert=executeServerEvent("InsertIntoHistory","INTRODUCEDONE","",true);
	return historyTableInsert;
}

function etbInsertion()
{
	//var rejectReasonsGridLength=getGridRowCount('REJECT_REASON_GRID');
	var ETBInsert=executeServerEvent("ETB_Intro","INTRODUCEDONE","",true);
	return ETBInsert;
}

function insertIntoSalaryTable(submittype){
	
	if(ActivityName=="Exceptions" || ActivityName=="Source_Refer")
	{
	var SalaryTableInsert=executeServerEvent("InsertIntoSalary","FormLoad",submittype,true);
	}
	
}


function cardopsdecision(){
	var cardops=executeServerEvent("Card_ops_decision","INTRODUCEDONE","",true);
	return cardops;
}

/*function enableDisableRejectReasons()
{
	if((getValue("DECISION").indexOf("Reject")!=-1) || (getValue("DECISION").indexOf("Reject to Initiator")!=-1))
	{
		setStyle("REJECT_REASON_GRID","visible","true");
		
	}
	else
	{
		setStyle("REJECT_REASON_GRID","visible","false");
		clearTable("REJECT_REASON_GRID",true);
	}
	
}*/

/*function setArchivalPath(ActivityName)
{
		if(ActivityName=="Introduction")
		{
			setValues({"ARCHIVALPATHSUCCESS":"Omnidocs\\CentralOperations\\&<CIF_ID>&\\DCC\\&<WI_NAME>&"},true);
			setValues({"ARCHIVALPATHREJECT":"Omnidocs\\CentralOperations\\&<CIF_ID>&\\Rejected\\DCC\\&<WI_NAME>&"},true);
		}
	
}*/
//}