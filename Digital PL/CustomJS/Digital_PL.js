var Digital_PL_onLoad = document.createElement('script');
	Digital_PL_onLoad.src = '/Digital_PL/Digital_PL/CustomJS/Digital_PL_onLoad.js';
	document.head.appendChild(Digital_PL_onLoad);
	
var Digital_PL_mandatory = document.createElement('script');
	Digital_PL_mandatory.src = '/Digital_PL/Digital_PL/CustomJS/Digital_PL_MandatoryFieldValidations.js';
	document.head.appendChild(Digital_PL_mandatory);

	
var Digital_PL_onSaveDone = document.createElement('script');
Digital_PL_onSaveDone.src = '/Digital_PL/Digital_PL/CustomJS/Digital_PL_onSaveDone.js';
document.head.appendChild(Digital_PL_onSaveDone);

var Digital_PL_Common = document.createElement('script');
Digital_PL_Common.src = '/Digital_PL/Digital_PL/CustomJS/Digital_PL_Common.js';
document.head.appendChild(Digital_PL_Common);

var PopulateButtonFlag=false;

function executeServerEventDigital_PL(controlName, eventType) {

 var flag = false;

 switch (eventType) {

  case 'focus':
   {
    flag = executeFocusEventDigital_PL(controlName);
    break;
   }
  
  case 'change':
   {
    flag = executeChangeEventDigital_PL(controlName);
    break;
   }

  case 'click':
   {
    flag = executeClickEventDigital_PL(controlName);
	
    break;
   }
   
    case 'mousedown':
   {
    flag = executeMouseDownEventDigital_PL(controlName);
    break;
   }

  default:
   return false;
 }

 if (flag) {
  executeServerEvent(controlName, eventType, "", false);

 }

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
		case 'Additional_Documents_Year_change' : 
				clearComboOptions("Additional_Documents_Month");
				loadYearDropDown();
		        //executeServerEvent(controlId,"change","",true);
				//executeServerEvent(controlId,controlEvent) 
				break;
	}
}
function executeFocusEventDigital_PL(controlName) {

 var executeEventFlag = true;
 switch (controlName) {

  case "":
   {
    break;
   }
  default:
   break;
 }
 return executeEventFlag;
}
function executeChangeEventDigital_PL(controlName) {

 var executeEventFlag = true;
 switch (controlName) {

  
   case "":
   {
    break;
   }
  default:
   break;
 }
 return executeEventFlag;
}
function executeClickEventDigital_PL(controlName) {

 var executeEventFlag = true;
 switch (controlName) {

  case "Fetch_FTS_Report":
   {
	   //var Ftsresponse=executeFormLevelIntegration(controlName);
		var Ftsresponse=executeServerEvent("Fetch_FTS_Report","Click","",true);
	   showMessage('Old_Salary',Ftsresponse,"error");
	  executeEventFlag = false;
    break;
   }
   case "Fetch_AECB_Report":{
	   var AECBresponse=executeServerEvent("Fetch_AECB_Report","Click","",true);
	   //showMessage('AECB URL: ',AECBresponse,"error");
	   window.open(AECBresponse);
	   //showMessage("Salary",AECBresponse,"info");
	   executeEventFlag = false;
			
	   break;
   }
   case "Regen_CAM":{
	   saveWorkItem();
	   var RegenCAMResponse=executeServerEvent("Regen_CAM","Click","",true);
	   if(RegenCAMResponse == 'FAIL')
			    	{
						showMessage('Regen_CAM','Some server Side error occured while generating CAM!',"error");
			    	}
			    else
			    	{
			    		if(RegenCAMResponse.indexOf('~')!=-1)
			    			{
			    				var arr = RegenCAMResponse.split("~");
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
	   executeEventFlag = false;
			
	   break;
   }
  default:
   break;
 }
 return executeEventFlag;
}
function executeMouseDownEventDigital_PL(controlName) {

 var executeEventFlag = true;
 switch (controlName) {

  case "":
   {
    break;
   }
  default:
   break;
 }
 return executeEventFlag;
}


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


function afterFormload()
{	
	setCommonVariables();
	//setControlValue("WI_NAME",WorkitemNo);
	var respnse=executeServerEvent('Load', 'formload', '', true);
	
	try {	
				saveWorkItem();
			} catch(ex) {
				showMessage("DECISION",'Kindly click on Save button then Close the workitem, Open it again and submit with appropriate decision.',"error");
				return false;
			}
			//readOnlyValidations();
			//collapseSectionAfterFormLoad();
		
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
		
		var wsname =getWorkItemData("ActivityName");
		//var validationStatus = executeServerEvent("FIRCO_Additional_Documents", "INTRODUCEDONE", "", true);
		var FIRCO_Decision = getValue("FIRCO_Decision");
		var FIRCOHit = getValue("IsFIRCOHit");
		var EFMS_Decision = getValue("EFMS_Status");
		if((FIRCO_Decision=="Document Required") && (wsname=="Exception"|| wsname=="Refer_to_Compliance"))
		{
			var FIRCO_Document_Count = getGridRowCount("Additional_Documents")
			if(FIRCO_Document_Count==0)
			{
			showMessage("Additional_Documents",'Kindly add atleast 1 FIRCO document before proceeding',"error");
				return false;
			}
		
		}
		
		var Decision = getValue("Decision");
		if(Decision==null || Decision==""){
			showMessage("Decision",'Kindly take Decision before submitting the workitem',"error");
				return false;
			
		}
		if(wsname=="Exception"){
			if(FIRCOHit=="Y" && FIRCO_Decision==""){
				alert("Firco decision is awaited");
			}
			if(EFMS_Decision==""){
				alert("EFMS decision is awaited");
			}
		}
		
		var validationStatus = executeServerEvent("InsertIntoHistory", "INTRODUCEDONE", "", true);
		
			return true;
		}
		else
		{
			return false;
		}
		return true;
	}  
	
function customonChangeSectionState(frameId,state){
	
}








	