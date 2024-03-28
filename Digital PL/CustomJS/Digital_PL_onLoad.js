var Digital_PL_Common = document.createElement('script');
Digital_PL_Common.src = '/Digital_PL/Digital_PL/CustomJS/Digital_PL_Common.js';
document.head.appendChild(Digital_PL_Common);

function loadSolId()
{
	
	var solId = executeServerEvent("SolId","FormLoad",user,true).trim();
	setControlValue("Sol_Id",solId);
}

/*function populateDecisionDropDown()
{
	var response=executeServerEvent("DecisionDropDown","FormLoad","",true);
	
	if (ActivityName == 'OPS_Maker' || ActivityName == 'OPS_Checker')
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
		
	}
}*/

function collapseSectionAfterFormLoad()
{
	collapseSection('');
}
/*
function readOnlyValidations()
 {
	 var readOnlyflag=(parent.document.title).indexOf("(read only)");
	 //console.log('readOnlyflag--'+readOnlyflag);
	 if(readOnlyflag > 0) // workitem opened in ReadOnly Mode
	{
		var status= executeServerEvent("FPU_Form", "ReadOnly", "", false);
	}
 }*/













