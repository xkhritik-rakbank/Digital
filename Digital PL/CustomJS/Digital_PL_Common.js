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
function loadYearDropDown(){
                var dd1Years = document.getElementById("Additional_Documents_Year");
                var currentYear = (new Date()).getFullYear();
                for (var i = currentYear; i >= 1950; i--){
                                var option = document.createElement("OPTION");
                                option.innerHTML = i;
                                option.value = i;
                                dd1Years.appendChild(option);
                }
                
                //for month drop down
                var now = new Date();
                var dd1Months = document.getElementById("Additional_Documents_Month");
                var theMonths = ["January","February","March","April","May","June","July","August","September","October","November","December"]
                var year = getValue('Additional_Documents_Year');
                var currentMonth = theMonths[now.getMonth()];
                var index = 0;
                var j=0;
                if(year == now.getFullYear()){
                    for(; j<theMonths.length;j++)
                    {
                        if(currentMonth == theMonths[j]){
                            index = j;
                            break;
                        }
                    }
                    for (var i = 0; i <= index; i++){
                        var option = document.createElement("OPTION");
                        option.innerHTML = theMonths[i];
                        option.value = theMonths[i];
                        dd1Months.appendChild(option);
                    
                    }
                                
                }else{
                                
                    for (var i = 0; i < 12; i++){
                        var option = document.createElement("OPTION");
                        option.innerHTML = theMonths[i];
                        option.value = theMonths[i];
                        dd1Months.appendChild(option);
                    
                    }
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
function collapseSection(controlNameList)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"sectionstate","collapsed")
		}
		catch(Exception)
		{}
	}
}
function customTableCellDateRange(tableId)
{
	var rowcount=getGridRowCount(tableId);
		var today = new Date();
		//var dd = String(today.getDate()).padStart(2, '0');
		
		var dd = today.getDate();
		if(dd<10){
			dd = 0 + String(dd);
		}
		//var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
		var mm = today.getMonth()+1;
		if(mm<10){
			mm = 0 + String(mm);
		}
		
		var yyyy = today.getFullYear();

		today = dd + '/' + mm + '/' + yyyy;
		for(var i=0;i<rowcount;i++)
		{
			setTableCellDateRange(tableId,i,2,'',today);
			setTableCellDateRange(tableId,i,3,'',today);
		}

	
}
