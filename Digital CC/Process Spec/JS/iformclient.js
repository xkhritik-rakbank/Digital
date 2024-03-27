// Custom Change Start
var Processname;
var ActivityName;
var WorkitemNo;
var cabName;
var user;
var viewMode;

var File_Common = document.createElement('script');
File_Common.src = '/DCC/DCC/CustomJS/DCC.js';
document.head.appendChild(File_Common);

// Custom Change End


function customValidation(op){
     // Custom Change Start
	/*
	`switch (op) {
        case 'S':
            
            break;
        case 'I':
            
            break;
        case 'D':
           
            break;
        default:
            break;
    }
    
    return true;
	*/
	return customValidationsBeforeSaveDone(op);
	// Custom Change End
}

function formLoad(){
     //alert("inside formLoad");
	// Custom Change Start		
	afterFormload();
	// Custom Change End
}

function onRowClick(tableId,rowIndex){
    return true;
}

function tableOperation(tableId,operationType){
	//added by gaurav
	if(operationType == "DeleteRow" && tableId == "AddnalDocs" ){
		var deleteRowConfirmation = confirm("You are going to delete the selected Row. Do you wish to continue?");
		if(deleteRowConfirmation ==  true)
		{		
			return true;
		}
		else
		{
			return false;
		}
	}
	
}


function listViewLoad(controlId,action){
	//added by gaurav
     if(ActivityName=='Firco')
                {
                  if(controlId=='AddnalDocs')
                         {
							 // popup_addDoc_grid();
                           if(action == "M" || action=="A")
                                {
								 clearComboOptions("Year_test");
								 clearComboOptions("month_test");
						         loadYearDropDown();
								 executeServerEvent(controlId,"FORMLOAD","",true);
								 executeServerEvent(controlId,"change","",true);
                                }
                          
                          }
				}
	//aded by kamran 03042023 salary doc
	if(ActivityName=='Exceptions')
                {
                  if(controlId=='SalaryDocReq')
                         {
							 // popup_addDoc_grid();
                           if(action == "M" || action=="A")
                                {
								 clearComboOptions("Year_test");
								 clearComboOptions("month_test");
						         loadYearDropDown();
								 executeServerEvent(controlId,"FORMLOAD","",true);
								 executeServerEvent(controlId,"change","",true);
                                }
                          
                          }
				 if(controlId=='Internal_Exposure') // Hritik 03.01.24 - PDSC - 1300
                         {
                           if(action == "M"){
								executeServerEvent("Internal_Exposure_M","FORMLOAD","",true);
                            }
							if(action == "A"){
								executeServerEvent("Internal_Exposure_Add","FORMLOAD","",true);
							}
                          }
				}
     if(ActivityName=='Card_Ops')
                {
                  if(controlId=='SuppDetailsGrid')
                         {
                           if(action == "M" || action=="A")
                                {
								 executeServerEvent(controlId,"FORMLOAD","",true);
                                }
                          
                          }
				}
}


function clickLabelLink(labelId){
    if(labelId=="createnewapplication"){
        var ScreenHeight=screen.height;
        var ScreenWidth=screen.width;
        var windowH=600;
        var windowW=1300;
        var WindowHeight=windowH-100;
        var WindowWidth=windowW;
        var WindowLeft=parseInt(ScreenWidth/2)-parseInt(WindowWidth/2);
        var WindowTop=parseInt(ScreenHeight/2)-parseInt(WindowHeight/2)-50;
        var wiWindowRef = window.open("../viewer/portal/initializePortal.jsp?NewApplication=Y&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid), 'NewApplication', 'scrollbars=yes,left='+WindowLeft+',top='+WindowTop+',height='+windowH+',width='+windowW+',resizable=yes')
    }
}
function allowPrecisionInText(){
    return 0;
}

function maxCharacterLimitForRichText(id){
    
    // return no of characters allowed as per condition based on id of the field
    return -1;
}
function showCustomErrorMessage(controlId,errorMsg){
    return errorMsg;
}

function resizeSubForm(buttonId){
    return {
        "Height":450,
        "Width":950
    };
}

function selectFeatureToBeIncludedInRichText(){
    return {
        'bold' :true,
        'italic':true,
        'underline':true,
        'strikeThrough':true,
        'subscript':true,
        'superscript':true,
        'fontFamily':true,
        'fontSize':true,
        'color':true,
        'inlineStyle':false,
        'inlineClass':false,
        'clearFormatting':true,
        'emoticons':false,
        'fontAwesome':false,
        'specialCharacters':false,
        'paragraphFormat':true,
        'lineHeight':true,
        'paragraphStyle':true,
        'align':true,
        'formatOL':false,
        'formatUL':false,
        'outdent':false,
        'indent':false,
        'quote':false,
        'insertLink':false,
        'insertImage':false,
        'insertVideo':false,
        'insertFile':false,
        'insertTable':true,
        'insertHR':true,
        'selectAll':true,
        'getPDF':false,
        'print':false,
        'help':false,
        'html':false,
        'fullscreen':false,
        'undo':true,
        'redo':true
        
    }
}

function allowDuplicateInDropDown(comboName){
    return false;
}

function postChangeEventHandler(controlId, responseData)
{
    
}

function test(){
    executeServerEvent('button1', 'click' , '' , true);
}

function test1(){
    executeServerEvent('TEMPLATE_TYPE', 'change' , '' , true);
}

function customListViewValidation(tableId,flag){
	  if(tableId=='CustomerCallHistory')
                {
                  if(ActivityName=='DCC_Experience')
                         {	 
								 executeServerEvent(tableId,"FORMLOAD","",true);
                         }
				}
       if(tableId == "AddnalDocs")
                         {
	                       var docName = getValue('AddnalDocs_doctype');
						   var docYear = getValue('Year_test');
						   var docMonth = getValue('month_test');
						   var remarks = getValue('AddnalDocs_remarks');
	                       if( docName == "" || docName == null )
	                          {
		                           showMessage(tableId,'Please select Docoment Type',"error");
	                               return false;
	                          }
						   if( docYear == "" || docYear == null )
	                          {
		                           showMessage(tableId,'Please select Year',"error");
	                               return false;
	                          }
						    if( docMonth == "" || docMonth == null )
	                          {
		                           showMessage(tableId,'Please select Month',"error");
	                               return false;
	                          }
							 if(docName == "Other_Document" && remarks == "") 
							 {
								showMessage(tableId,'Please fill Remarks',"error");
	                            return false; 
							 }
						 }
						 return true;
}   



