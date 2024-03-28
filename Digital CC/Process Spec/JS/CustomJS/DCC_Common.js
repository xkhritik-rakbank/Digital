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

