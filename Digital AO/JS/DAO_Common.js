function compareStringsIgnoreCase (string1, string2) 
{
	if(string1!=null && string2!=null)
    {
     string1 = string1.toLowerCase();
     string2 = string2.toLowerCase();
     return string1 === string2;
	}
    else
    	return false;
}

function isEmpty(string1)
{
	if(string1==null || string1=='')
		return true;
	else
		return false;		
}

function disableControl(controlNameList)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"disable","true");
		}
		catch(Exception)
		{}	
	}	
}

function enableControl(controlNameList)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"disable","false");
		}
		catch(Exception)
		{}	
	}
}

function lockControl(controlNameList)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"readonly","true");
		}
		catch(Exception)
		{}	
	}
}

function unlockControl(controlNameList)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"readonly","false");
		}
		catch(Exception)
		{}	
	}
}

function showControl(controlNameList)
{	
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"visible","true");
		}
		catch(Exception)
		{}	
	}
}

function hideControl(controlNameList)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"visible","false");
		}
		catch(Exception)
		{}
	}
}

function clearControlValue(controlNameList)
{		
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		var controlName = JSON.parse('{"'+controlNameArr[idx]+'":""}');
		try
		{
			setValues(controlName,true);
		}
		catch(Exception)
		{}
	}		
}

function setControlValue(controlName, controlValue)
{
	var controlObj = JSON.parse('{"'+controlName+'":"'+controlValue+'"}');
	setValues(controlObj,true);
}

function setControlColor(controlNameList,color)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"backcolor",color);
		}
		catch(Exception)
		{}	
	}
	
}

function AddnalDocsReqd_flag()
{
	var row_count = getGridRowCount("AddnalDocs");
	if(row_count==0)
	{
		setControlValue("AddnalDocsReqd","false");
	}
	for(var i =0; i<row_count;i++)
	{
		var status = getValueFromTableCell("AddnalDocs",i,1);
		if(status=='Pending')
		{
			setControlValue("AddnalDocsReqd","true");
		}
		else {
			setControlValue("AddnalDocsReqd","false");
		}
	}
}

function currentDate()
{
	var today = new Date();
	var curr_date = today.getDate()+'/'+today.getMonth()+'/'+today.getFullYear();
	return curr_date;
}