
function saveClickOperation()
{
	
}

function insertIntoHistoryTable()
{	
	var historyTableInsert=executeServerEvent('InsertIntoHistory','introducedone','',true).trim(); 
	return historyTableInsert;
}

function setNotifyFlag()
{
	var set_item_event =executeServerEvent('notify_set','introducedone','',true).trim();
	return set_item_event;
}

function firco_flag_update()
{
	var firco_update =executeServerEvent('firco_flag_update','introducedone','',true).trim();
	return firco_update;
}

function highrisk_flag_update()
{
	var highrisk_update =executeServerEvent('highrisk_flag_update','introducedone','',true).trim();
	return highrisk_update;
}

function sendmailTemplate()
{
	var mailOnePager_val =executeServerEvent('mailOnePager','introducedone','',true).trim();
	return mailOnePager_val;
}