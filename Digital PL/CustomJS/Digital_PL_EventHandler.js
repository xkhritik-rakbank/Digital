var Digital_PL_onLoad = document.createElement('script');
Digital_PL_onLoad.src = '/Digital_PL/Digital_PL/CustomJS/Digital_PL_onLoad.js';
document.head.appendChild(Digital_PL_onLoad);

function SetEventValues(CtrId,CtrEvent)
{
	if (CtrEvent =="change" || CtrEvent =="click")
	{
		//No change events mentioned in DCS
	}
}

function executeFormLevelIntegration(controlName)
{
	try {
		window.parent.WFSave();
	} catch(ex) {
		
	}
	var DecValue= executeServerEvent(controlName, "click", "", false);
	CreateIndicator("temp");//Loading Indication will start here
}
