    
    var rowId = 0;
    var valueChanged=false;
    var ComponentValidatedMap ={};
    var datepickerinitialised = false;
    var currentJQueryDatePickerValue="";//Bug 76754
    var tableDataChangeArray=[];
    var isListviewOpened=false;
    var totalSubWindows = 0;    //Bug 81164 
    var allWindows = new Array();
    var isAutoCompleteSelected=false;
    var documentIndex="";
    var isBootboxCloseClicked=false;
    var tileDataModified = false;
    var isAdvanceListView=false
    var isAdvanceNext=false;
    var sessionTimeOut = false;
    var tableComponentMap = []; 
    var serverValidationMap = [];
    var appSessionCheckTimer;
    var sessionExpireWarnVisible = false;
    var sessionTimeReqSent = 0;
    var sessionTimeReqRecv = 0;
    var EFF_SESSION_TIME = 0;
    var EFF_UPDATE_SESSION_TIME = 0;
    var sessionTimerWindows = [];
    //Bug 100042
    /*var dfvalue,dsvalue;
    window.addEventListener('DOMContentLoaded', (event) => {
     if(jQuery("input[datatype='date']").length>0){
                dfvalue = jQuery("input[datatype='date']").attr("dateformat").split("_")[0];
                dsvalue =  jQuery("input[datatype='date']").attr("dateformat").split("_")[1];
                timeFlag =  jQuery("input[datatype='date']").attr("dateformat").split("_")[2]
            }
            else{
                dfvalue = jQuery("input[controltype='date']").attr('dateformat');
                dsvalue = jQuery("input[controltype='date']").attr('dateseparator');
            }
    });*/
    function encode_ParamValue(param)
    {
            return param;
    }

function applyFormattingGrid()
{
    $('.tabletextbox').each(function()
        {
            if(this.getAttribute("maskingpattern")!=null && this.getAttribute("maskingpattern")!=undefined && this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="" )
            {
            if(this.getAttribute("maskingPattern").toString()!='currency_rupees' && this.getAttribute("maskingPattern").toString()!=='currency_dollar' && this.getAttribute("maskingPattern").toString()!=='currency_naira' && this.getAttribute("maskingPattern").toString()!=='currency_yen' && this.getAttribute("maskingPattern").toString()!=='currency_euro'  && this.getAttribute("maskingPattern").toString()!=='currency_french' && this.getAttribute("maskingPattern").toString()!=='currency_bahamas' && this.getAttribute("maskingPattern").toString()!=='currency_greek' && this.getAttribute("maskingPattern").toString()!=='percentage' && this.getAttribute("maskingPattern").toString()!=='dgroup2' && this.getAttribute("maskingPattern").toString()!=='dgroup3' && this.getAttribute("maskingPattern").toString()!=='email' && this.getAttribute("maskingPattern").toString()!=='NZP')
                {
                    var placeholder;
                    placeholder = this.getAttribute("maskingpattern").replace(/[A-Za-z0-9*#]/mg, "_");
                    jQuery(this).mask(this.getAttribute("maskingpattern"), {
                        placeholder: placeholder
                    }, {
                        clearIfNotMatch: true
                    });
                }
                else{
                            maskfield(this,'input');
            }
            }
             var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if(typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking")
            {
                maskfield(this,'input');
            }
        });
}
function ctrOnchangeHandler(thisRef, op){
        if( ! datepickerinitialised )
            return;
		if(thisRef.type!=null){
         if(thisRef.type=="radio"){ 
          if(thisRef.parentElement!=undefined && thisRef.parentElement!=null){    
            if(thisRef.parentElement.classList.contains("radioThree")){
                thisRef.focus();
            }
          }  
         }
        }
        var key = thisRef.getAttribute("dataClass");
        if(key==null || key=="")
            key = thisRef.name;
        if(key=="")
            key = thisRef.id;
		var validateMapKey=thisRef.id;//Bug 83970 Start
        if(thisRef.name!=null&&thisRef.name!=undefined&&thisRef.name!=""&&thisRef.id===thisRef.name)
            validateMapKey=thisRef.name;//Bug 83970 End
        var value;
        //masking creditcard in textbox//
        //    if(jQuery(thisRef).attr("masklength")!=undefined)
        //        value = jQuery("#"+thisRef.id + "-original").val();
        //    else
        value = getControlValue(thisRef);
        if (window.clearEditableComboBoxValueIFNotExists) {
            if (window.clearEditableComboBoxValueIFNotExists()) {
                if (thisRef.getAttribute("datatype") == "combobox" && thisRef.classList.contains("editableCombo")) {
                    if (thisRef.parentNode != null && thisRef.parentNode != 'undefined') {
                        var valueExists = false;
                        var ele = thisRef.parentNode.getElementsByClassName("es-visible");
                        var listItems = jQuery(".es-list").find("li");
                        if (ele != null && ele.length > 0) {
                            for (var p = 0; p < listItems.length; p++) {
                                var option = listItems[p].textContent;
                                if (value == option) {
                                    valueExists = true;
                                    break;
                                }
                            }
                        }
                        if (!valueExists) {
                            value = "";
                            setValue(key, value);
                        }
                    }
                }
            }
        }
        if(thisRef.hasAttribute('autocompletevalue')&&value==='')
        thisRef.setAttribute('autocompletevalue','');
        var type=jQuery(thisRef).attr("datatype");
        if(jQuery(thisRef).attr("type")=="password"){
            type="password";
        }
        if(thisRef.getAttribute("typeofvalue") && (thisRef.getAttribute("typeofvalue")==='Date' || thisRef.getAttribute("typeofvalue")==='Boolean' || thisRef.getAttribute("typeofvalue")==='Integer' || thisRef.getAttribute("typeofvalue")==='Float' || thisRef.getAttribute("typeofvalue")==='Long')){
            if(!validateTypeOfValue(thisRef) || !validateValue(thisRef,type))
            {
                ComponentValidatedMap[validateMapKey]=false;//Bug 83970
                valueChanged=false; 
                return false;
            }
        }
        else{
            if(!(thisRef.type=="range"))
            {
                if(!jQuery(thisRef).hasClass("richtexteditor") && !validateValue(thisRef,type))
                {
                    ComponentValidatedMap[validateMapKey]=false;//Bug 83970
                    valueChanged=false; 
                    return false;
                }
            }


        }
        //Bug 83576 starts
//        if(thisRef.getAttribute("typeofvalue")=="Float" && value!=''){
//            appendZeroesToFloat(thisRef);
//        }
        //Bug 83576 ends
        if(thisRef.type=='text' && !validateMinMaxValue(thisRef.id)){
            valueChanged=false;
            return false;
        }

        if(value!="" && jQuery(thisRef).attr("required")!=undefined)
        {
            var msgRef = document.getElementById(thisRef.id+"_msg");
            var patternRef = document.getElementById(thisRef.id+"_patternMsg");
            jQuery(msgRef).css("display","none");
            jQuery(patternRef).css("display","none");
            toggleErrorTooltip(thisRef,msgRef,patternRef,true,0);
           // jQuery("#"+thisRef.id+"_msg").css("display","none");
          //  jQuery("#"+thisRef.id+"_patternMsg").css("display","none");
            if(jQuery(jQuery(thisRef)[0]).parent().parent().parent().hasClass("floating-label-form-group"))
                jQuery(jQuery(thisRef)[0]).parent().parent().parent().removeClass("mandatory");
            else
                jQuery(jQuery(thisRef)[0]).removeClass("mandatory");
        }

        valueChanged=true; 
        if (validateMapKey in ComponentValidatedMap)//Bug 83970
            delete ComponentValidatedMap[validateMapKey];//Bug 83970
        var combotype =  jQuery(thisRef).attr("combotype");
        var saveencrypt = "N";
        if(jQuery(thisRef).attr("saveencrypt")!="")
            saveencrypt = jQuery(thisRef).attr("saveencrypt");
        var listboxOptions={};

        if(combotype!=undefined){
            //value = thisRef.options[thisRef.selectedIndex].value;
            for (i=0;i<thisRef.length;i++) { 
                if(thisRef[i].selected){
                    listboxOptions[i] = thisRef[i].value; 
                }
            }
            var jsonString = JSON.stringify(listboxOptions);
            value = jsonString;
        }
        //value = getControlValue(thisRef);
        var url = "ifhandler.jsp";
        //    var requestString = "pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&attrName="+encode_utf8(key)+"&attrValue="+encode_utf8(value)+"&op="+op+"&attrType="+type;
        if(type == "date"){
            var dateFormat = jQuery(thisRef).attr("dateformat").split("_")[0];
            var dateSeparator = jQuery(thisRef).attr("dateformat").split("_")[1];
            if(dateSeparator == "1") dateSeparator = "/";
            else if(dateSeparator == "2") dateSeparator = "-";
            else if(dateSeparator == "3") dateSeparator = ".";
            var timeFlag = jQuery(thisRef).attr("dateformat").split("_")[2];
            if($(thisRef).hasClass('openPickerClass'))
            {
                validateDateValue(thisRef);
                value = thisRef.value;
            }
        }
        if(window.formChangeHook)
            formChangeHook(thisRef);
        var requestString = "pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&attrName="+encode_utf8(key)+"&attrValue="+encode_utf8(value)+"&op="+op+"&attrType="+type+"&dateformat="+dateFormat+"&dateseparator="+dateSeparator+"&timeflag="+timeFlag+"&combotype="+combotype+"&saveencrypt="+saveencrypt;          
        var contentLoaderRef = new net.ContentLoader(url, formHandler, formErrorHandler, "POST", requestString, false);
        var valuesJSON={};
        if(contentLoaderRef.req.responseText.trim() !=="" )
        {
            valuesJSON = JSON.parse(contentLoaderRef.req.responseText);
        }
        if(isServerValidation=="true" && contentLoaderRef.req.responseText!="") {
            if(valuesJSON["serverValidation"]!=null){
                if(valuesJSON["serverValidation"]=="false"){
                    var jsonObj = {};
                    jsonObj["controlId"]=valuesJSON["controlName"];
                    jsonObj["controlValue"]=valuesJSON["controlValue"];
                    serverValidationMap[thisRef.id]=jsonObj;
                    showMessage(thisRef.id,THE_VALUE+" "+valuesJSON["controlValue"]+" "+INCOMPATIBLE_MSG+" "+FIELD+" "+valuesJSON["controlName"],"error");
                    return;
                }
            }
            else{
                delete serverValidationMap[thisRef.id];
            }
        }
        if(valuesJSON["APIData"] != null && valuesJSON["APIData"] != "")
        {
            renderExecuteServerEventAPIData(valuesJSON["APIData"]);
        }
        if(valuesJSON["responseData"] != null && valuesJSON["responseData"] != "")
        {
            postChangeEventHandler(key, valuesJSON["responseData"]);
        }
        if(valuesJSON["serverValidation"] == null && !validateUpdateValueResponse(valuesJSON,value,combotype)){
            contentLoaderRef = new net.ContentLoader(url, formHandler, formErrorHandler, "POST", requestString, false);
            if(contentLoaderRef.req.responseText!="") 
                valuesJSON = JSON.parse(contentLoaderRef.req.responseText);
            if(!validateUpdateValueResponse(valuesJSON,value,combotype))
                showSplitMessage("",SERVER_ERROR,SERVER_ERROR_TITLE,"error");
        }

		updateSessionTimeout();
}
        function validateUpdateValueResponse(valuesJSON,attrValue,combotype){
            var valueSet =  valuesJSON["valueset"];
			if(isSkipResponseData == "true")
             return true;   
            if(Object.keys(valuesJSON).length===0)
                return true;
            if(combotype!=undefined){
                valuesJSON=JSON.parse(valuesJSON["valueset"]);
                attrValue=JSON.parse(attrValue);
                for(key in valuesJSON){
                    if(attrValue[key]!=valuesJSON[key])
                        return false;
                }
                return true;
            }
            else{
               if(attrValue!=undefined && attrValue!=null && valueSet!=undefined && valueSet!=null){
                   return (attrValue.toString() == valueSet.toString())
               }
        }
    }


    function formHandler(){
    }

    function formErrorHandler(){

    }

    function validateColumnValue(thisRef,tableId,showMsg){
        if(thisRef.value!==""){
        var value = thisRef.value;
         if(thisRef.getAttribute("maskingPattern")!=null && thisRef.getAttribute("maskingPattern")!=undefined && thisRef.getAttribute("maskingPattern")!='' && thisRef.getAttribute("maskingPattern")!='nomasking' && thisRef.getAttribute("maskingPattern")!='email'){
    	if(thisRef.getAttribute("maskingPattern").toString()==='currency_rupees' || thisRef.getAttribute("maskingPattern").toString()==='currency_dollar' || thisRef.getAttribute("maskingPattern").toString()==='currency_naira' || thisRef.getAttribute("maskingPattern").toString()==='currency_yen' || thisRef.getAttribute("maskingPattern").toString()==='currency_euro' || thisRef.getAttribute("maskingPattern").toString()==='currency_french' || thisRef.getAttribute("maskingPattern").toString()==='currency_bahamas' || thisRef.getAttribute("maskingPattern").toString()==='currency_greek' || thisRef.getAttribute("maskingPattern").toString()==='percentage'|| thisRef.getAttribute("maskingPattern").toString()==='dgroup2'|| thisRef.getAttribute("maskingPattern").toString()==='dgroup3'|| thisRef.getAttribute("maskingPattern").toString()==='NZP')
                    value = jQuery(thisRef).autoNumeric('get');
            else{
                    if(thisRef.getAttribute("datatype") == "date")
                    {
                        value = thisRef.value;
                    }
                    else
                        value = jQuery(thisRef).cleanVal();
                }
        }
        var correctValue =ENTER_CORRECT_VALUE_FOR+thisRef.getAttribute("typeofvalue");
        if(thisRef.getAttribute("typeofvalue")==='Float'){
            var orgvalue = value.replace(".","");
            if(isNaN(parseFloat(value)) || !isFinite(value) || (value.indexOf('.')>0 && (value.length-value.indexOf('.')-1)>(parseInt(thisRef.getAttribute("Precision"))))  ||(orgvalue.length>thisRef.getAttribute("floatlength"))){
                if(!(isNaN(parseFloat(value)) || !isFinite(value)) && (orgvalue.length>thisRef.getAttribute("floatlength"))){
                    correctValue=FLOAT_LENGTH;
                    if(document.getElementById(tableId+"_"+thisRef.getAttribute("labelName")+"_msg")!=undefined)
                        document.getElementById(tableId+"_"+thisRef.getAttribute("labelName")+"_msg").innerHTML = correctValue;
                    thisRef.value="";
                    return false;
                } 
                else if(!(isNaN(parseFloat(value)) || !isFinite(value)) && (value.length-value.indexOf('.')-1)>(parseInt(thisRef.getAttribute("Precision"))))
                    correctValue=PRECISION_VALUE+thisRef.getAttribute("Precision");
                if(document.getElementById(tableId+"_"+thisRef.getAttribute("labelName")+"_msg")!=undefined)
                        document.getElementById(tableId+"_"+thisRef.getAttribute("labelName")+"_msg").innerHTML = correctValue;
                    thisRef.value="";
                    return false;
            }
        } else if(thisRef.getAttribute("typeofvalue")==='Integer'){
                if(isNaN(parseInt(value)) || !isFinite(value) || parseInt("-32768")>parseInt(value) || parseInt("32767")<parseInt(value)){
                    if(!(isNaN(parseInt(value)) || !isFinite(value)) && (parseInt("-32768")>parseInt(value) || parseInt("32767")<parseInt(value))){
                        correctValue=ENTER_VALUE_BETWEEN+INTEGER_RANGE;
                    }
                    if(document.getElementById(tableId+"_"+thisRef.getAttribute("labelName")+"_msg")!=undefined)
                        document.getElementById(tableId+"_"+thisRef.getAttribute("labelName")+"_msg").innerHTML = correctValue;
                    thisRef.value="";
    //                if(showMsg==true)
    //                    showMessage("",correctValue,"error");
                    return false;  
                }
            }
        else if(thisRef.getAttribute("typeofvalue")==='Long'){
        if(isNaN(parseInt(value)) || !isFinite(value) || parseInt("-2147483648")>parseInt(value) || parseInt("2147483647")<parseInt(value)){
                if(!(isNaN(parseInt(value)) || !isFinite(value)) && (parseInt("-2147483648")>parseInt(value) || parseInt("2147483647")<parseInt(value)))
                    correctValue=ENTER_VALUE_BETWEEN+FLOAT_RANGE;
                if(document.getElementById(tableId+"_"+thisRef.getAttribute("labelName")+"_msg")!=undefined)
                    document.getElementById(tableId+"_"+thisRef.getAttribute("labelName")+"_msg").innerHTML = correctValue;
                thisRef.value="";
    //            if(showMsg==true)
    //                showMessage("",correctValue,"error");
                return false;  
            }
        }
        else if(thisRef.getAttribute("typeofvalue")==='Boolean'){
            if(!(value.toLowerCase()==="true" || value.toLowerCase()==="false")){
                return false;  
            }
        }

        }
         return true;
    }

    function customServerValidation(op)
    {
        var url = "ifhandler.jsp";
        var requestString = "pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&CalledFor=Validation";

        switch (op) {
            case 'S':
                requestString += "&op=2";
                break;
            case 'I':
                requestString += "&op=3";
                break;
            case 'D':
                requestString += "&op=4";
                break;
            default:
                break;
        }

        var responseText = iforms.ajax.processRequest(requestString, url);
        if(responseText.trim()!=="")
        {
            var jsonArray=JSON.parse(responseText.trim());            
            for (var i = 0; i < jsonArray.length; i++) 
            {
                 if( jsonArray[i].type==="fail"){
                    if( window.showFailureMessage){
                        showFailureMessage(jsonArray);
                        return false;
                    } 
                    break;
                 }
            }            
        }else
            return true;
        return true;
    }

    function saveForm(op,silentSave)//Bug 85227
    {
        silentSave=typeof silentSave =="undefined"?false:silentSave;//Bug 85227
        var activeElement = document.activeElement;
        var key = activeElement.name;
        var value="";
        var type="";
        var dateFormat="";
        var dateSeparator="";
        var timeFlag="";
        var mandatorycheck=true; 
        var isBackClick=false;
        if(op=="SB"){
            mandatorycheck=false;
            op="SF";
            isBackClick=true;
        }
        if(op=="SC"){
            op="SF";
            isBackClick=true;
        }
        if (key) {
            value = getControlValue(activeElement);
            type = jQuery(activeElement).attr("datatype");
            dateFormat = "";
            dateSeparator = "";
            timeFlag = "";
            if(type == "date"){
             dateFormat = jQuery(activeElement).attr("dateformat").split("_")[0];
             dateSeparator = jQuery(activeElement).attr("dateformat").split("_")[1];
            if(dateSeparator == "1") dateSeparator = "/";
            else if(dateSeparator == "2") dateSeparator = "-";
            else if(dateSeparator == "3") dateSeparator = ".";
             timeFlag = jQuery(activeElement).attr("dateformat").split("_")[2];
            }
        } else {
             key = 'undefined';
             value='';
             type='';
        }
        

        if( op != "S" && mandatorycheck){ 
            if(!validateMandatoryFields())
                return false;
            if( !fetchCollapsedFrameHTML())
              return false;
        }
        if( window.customValidation && mandatorycheck){
            if( ! customValidation( op ) ){
                return false;
            }
        }           

        saveRichTextEditorData();
        var url = "ifhandler.jsp";
        var requestString = "pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&attrName="+encode_utf8(key)+"&attrValue="+encode_utf8(value)+"&attrType="+type+"&dateformat="+dateFormat+"&dateseparator="+dateSeparator+"&timeflag="+timeFlag+"&isBackClick="+isBackClick;

        switch (op) {
            case 'S':
                requestString += "&op=2";
                break;
            case 'I':
                requestString += "&op=3";
                break;
            case 'D':
                requestString += "&op=4";
            case 'SF':
                requestString += "&op=8";
                break;
            default:
                break;
        }
        if(silentSave){//Bug 85227 Start
            var contentLoaderRef=new net.ContentLoader(url, errorMsgHandler, wiOpErrorHandler, "POST", requestString, false);
            contentLoaderRef.Op = op;
        }//Bug 85227 End
        else{
            var contentLoaderRef = new net.ContentLoader(url, wiOpHandler, wiOpErrorHandler, "POST", requestString, false);
            contentLoaderRef.Op = op;
        }
    }


    function errorMsgHandler(){
	if(this.req.getResponseHeader("InvalidSession")==="Y" || this.req.getResponseHeader("Error")=="7006"){
           window.location=contextPath+'/components/error/sessionInvalid.jsp';
        } else if( "Y" === this.req.getResponseHeader("Error") || (this.req.getResponseHeader("errorMessage")!="" && this.req.getResponseHeader("errorMessage")!=null)){
            if(this.req.getResponseHeader("errorCode")=="11")
                showAlertDialog("<b>" + INVALID_SESSION + "</b>", false);
            else
                showAlertDialog("<b>There is some error in Saving Data.</br>Please check logs for error!</b>",true);    
         } 
    }

    function customIFormHandler(op,dummySave)
    {
        if(op=='S' && dummySave=='Y')
            return;
        var isValid = true;
        if( op != 'S' ){//Bug 84948 Start
            var skipValid=false;
            if( window.skipValidation ){
                //return window.skipValidation();
                if(window.skipValidation())
                    skipValid=true;
            }
            if(!skipValid){
                if(!validateMandatoryFields())
                    return false;
            isValid = Object.keys(ComponentValidatedMap).length==0;     //Bug 83114
        }
        }//Bug 84948 End
        if(isValid ){
        var activeElement = document.activeElement;
        var key = activeElement.name;
        if(key) {
        var value = getControlValue(activeElement);
        var type=jQuery(activeElement).attr("datatype");
        var dateFormat="";
        var dateSeparator="";
         var timeFlag="";
        if(type == "date" || type == "shortdate"){
             dateFormat = jQuery(activeElement).attr("dateformat").split("_")[0];
             dateSeparator = jQuery(activeElement).attr("dateformat").split("_")[1];
            if(dateSeparator == "1") dateSeparator = "/";
            else if(dateSeparator == "2") dateSeparator = "-";
            else if(dateSeparator == "3") dateSeparator = ".";        
            if(type == "date")
                timeFlag = jQuery(thisRef).attr("dateformat").split("_")[2];
        }

        } else {
            key = 'undefined';
            value='';
            type='';
        }
        var url = "ifhandler.jsp";
        var requestString = "pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&attrName="+encode_utf8(key)+"&attrValue="+encode_utf8(value)+"&attrType="+type+"&dateformat="+dateFormat+"&dateseparator="+dateSeparator+"&timeflag="+timeFlag+"&mobileMode=N&webdateFormat="+webdateFormat;

        switch (op) {
            case 'S':
                requestString += "&op=2";
                break;
            case 'I':
                requestString += "&op=3";
                break;
            case 'D':
                requestString += "&op=4";
                break;
            default:
                break;
        }        
        var responseText = iforms.ajax.processRequest(requestString, url);
        var retResponseText = responseText;
        if(responseText.trim()!=""){
            var jsonObj=JSON.parse(responseText);
            var ngParam=encode_utf8(jsonObj.ngParam);
            var duplicateRowID = jsonObj.ResponseCode;
            if(duplicateRowID != null && duplicateRowID.trim() != "") {
                return JSON.stringify(jsonObj);
            }
            var taskParam=encode_utf8(jsonObj.taskParam);
            dummySave = (typeof dummySave=='undefined')?"N":dummySave; 
            tid = (typeof tid=='undefined')?"":tid;    
            var reqTok;
//                if(window.parent.opener!=null){
//                    if(typeof window.parent.opener.parent.getRequestTokenSyn!='undefined')		
//                        reqTok = window.parent.opener.parent.getRequestTokenSyn('/webdesktop/servlet/saveformservlet');
//                    else
//                        reqTok = window.parent.getRequestToken('/webdesktop/servlet/saveformservlet'); 
//                } else
              if(window.parent!=null)
                reqTok = window.parent.getRequestToken('/webdesktop/servlet/saveformservlet');
            var sid = jQuery("#wd_sid").val();           
            url="/webdesktop/servlet/saveformservlet?WD_RID="+reqTok+"&WD_SID="+sid;           
            var csc = (typeof window.parent.isCollabServerConnected !='undefined')?(window.parent.isCollabServerConnected)?'Y':'N':'N';         
            requestString="pid="+pid+"&wid="+wid+"&fid="+fid+"&ngParam="+ngParam+"&taskParam="+taskParam+"&taskid="+tid+"&isDummySave="+dummySave+"&processDefId="+processDefId+"&EventType="+op;
            requestString+='&csc='+csc;
            responseText = iforms.ajax.processRequest(requestString, url);
            retResponseText = responseText;
            var pageFidType=fid.substring(0, fid.indexOf("_", 0));//Bug 85461               
            var responseJSON = JSON.parse(responseText);
            if(responseJSON.ResponseCode!='200'){   
                printLog(responseText);
                return retResponseText;
            } else {
                responseText = decodeURIComponent(responseJSON.ResponseMessage);
		}
            if(responseText.trim()!="" && responseText.trim()!='701' && (pageFidType=="Form" || pageFidType=="TaskForm" || pageFidType=="TemplateTaskForm")){//Bug 85461
                url = "ifhandler.jsp";
                requestString = "pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&op=5&AttribXML="+encode_utf8(responseText.trim());
                responseText = iforms.ajax.processRequest(requestString, url);
                tableDataChangeArray=[];

            }
        }
        if(window.saveWorkItemPostHook){
                saveWorkItemPostHook();
        }
        return retResponseText;

      
    }
    else{
        setFocus(Object.keys(ComponentValidatedMap)[0], false);    //Bug 83114
    }
    }

    function getSaveFormXML(op)
    {
        var activeElement = document.activeElement;
        var key = activeElement.name;
        var value = getControlValue(activeElement);
        var type=jQuery(activeElement).attr("datatype");
        var dateFormat="";
        var dateSeparator="";
         var timeFlag="";
        if(type == "date" || type == "shortdate"){
             dateFormat = jQuery(activeElement).attr("dateformat").split("_")[0];
             dateSeparator = jQuery(activeElement).attr("dateformat").split("_")[1];
            if(dateSeparator == "1") dateSeparator = "/";
            else if(dateSeparator == "2") dateSeparator = "-";
            else if(dateSeparator == "3") dateSeparator = ".";        
            if(type == "date")
                timeFlag = jQuery(thisRef).attr("dateformat").split("_")[2];
        }

        if( op != "S"){ 
            if(!validateMandatoryFields())
                return false;
        }
        if( window.customValidation ){
            if( ! customValidation( op ) ){
                return false;
            }
        }           


        var url = "ifhandler.jsp";
        var requestString = "pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&attrName="+encode_utf8(key)+"&attrValue="+encode_utf8(value)+"&attrType="+type+"&dateformat="+dateFormat+"&dateseparator="+dateSeparator+"&timeflag="+timeFlag+"&mobileMode=N&webdateFormat=";

        switch (op) {
            case 'S':
                requestString += "&op=2";
                break;
            case 'I':
                requestString += "&op=3";
                break;
            case 'D':
                requestString += "&op=4";
                break;
            default:
                break;
        }        
        var responseText = iforms.ajax.processRequest(requestString, url);
        if(responseText.trim()!=""){
            var jsonObj=JSON.parse(responseText);
            var ngParam=jsonObj.ngParam;
            return ngParam;        
        }
        return ""; 
    }


    function wiOpHandler(){
        var workdeskWin = window.parent;
        // Form type 'I' indicates Responsive form in iBPS
        var formType = 'I';                
        var errorMessage = this.req.getResponseHeader("errorMessage"); 
        var oper = getQueryVariable(this.params, "op");
        if(oper=="2")
            oper="S";
        else if(oper=="3")
            oper="I";
        else if(oper=="4")
            oper="D";
        if( errorMessage == "" || errorMessage == null ){
            if(oper=='S' && window.saveWorkItemPostHook){
                saveWorkItemPostHook();
            }
            switch (oper) {
                case 'S':
                    if(Object.keys(ComponentValidatedMap).length==0)
                        workdeskWin.postMessage('S', "*");
                    else
                        workdeskWin.postMessage('F', "*");
                    break;
                case 'I':
                    if(Object.keys(ComponentValidatedMap).length==0)
                        workdeskWin.postMessage('I', "*");
                    else
                        workdeskWin.postMessage('F', "*");
                    break;
                case 'D':
                    if(Object.keys(ComponentValidatedMap).length==0)
                        workdeskWin.postMessage('D', "*");
                    else
                        workdeskWin.postMessage('F', "*");
                    break;
                default:
                    break;
            }
        }
        else
        {
            //Bug 81193 
            workdeskWin.postMessage('F:'+errorMessage, "*");
        }
    }

    function wiOpErrorHandler(){
        var workdeskWin = window.parent;
        // Form type 'I' indicates Responsive form in iBPS
        var formType = 'I';

        switch (this.Op) {
            case 'S':
                //workdeskWin.postMessage('S', "*");
                break;
            case 'I':
                //workdeskWin.postMessage('I', "*");
                break;
            case 'D':
                //workdeskWin.postMessage('D', "*");
                break;
            default:
                break;
        }
    }

    function getContentWindow(modalId){
        var returnedObject = null;
        try{
            returnedObject =  window.frames[modalId].contentWindow.document;
        }catch(ex){
            returnedObject =  window.frames[modalId].document;
        }
        return returnedObject;
    }

    //function setSelectedRow()
    //{
    //    var myTrArray =  getContentWindow('iFrameSearchModal').getElementsByClassName("info");
    //    var textBoxValue = "";
    //    if(typeof myTrArray[0] != "undefined"){
    //        textBoxValue = $(myTrArray[0]).find("td:first").html();
    //    }
    //    var ref= getContentWindow('iFrameSearchModal').getElementById("controlId").value;
    //    document.getElementById(ref).value=textBoxValue;
    //    jQuery(document.getElementById(ref)).trigger("change");
    //    document.getElementById("picklistNext").disabled= false;
    //    document.getElementById("picklistPrevious").disabled = true;
    //            
    //}
    function makeAjaxCall(controlId,eventType)
    {
        var url = "action.jsp";
        var requestString=  "controlId="+controlId +"&EventType="+eventType+"&from=serverEvent&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid);  
        var contentLoaderRef = new net.ContentLoader(url, ajaxFormHandler, ajaxFormErrorHandler, "POST", requestString, true);
    }

    function ajaxFormHandler(){
        try{
            var jsonObj = JSON.parse(this.req.responseText);
            for (var i = 0; i < jsonObj.length; i++) 
            {
                if(jsonObj[i].Action=="setValue")
                    setValue(jsonObj[i].ControlName,jsonObj[i].ControlValue);
                else if(jsonObj[i].Action=="setStyle")
                    setStyle(jsonObj[i].ControlName,jsonObj[i].Attribute,jsonObj[i].AttributeValue);
            }
        }
        catch(ex){}
    }
    function ajaxFormErrorHandler(){

    }

    function executeListView(controlId,eventType,dataValue,isCopyRow){
        if(window.tableOperation){
            if((isCopyRow==undefined || isCopyRow==false ) && tableOperation(controlId,"AddRow") == false)
                return;
            if(isCopyRow!=undefined && isCopyRow==true && tableOperation(controlId,"CopyRow") == false)
                return;
        }
        var isDisabled=document.getElementById(controlId).classList.contains("disabledTable");

        var url = "action.jsp";
        var requestString=  "controlId="+encode_utf8(controlId) +"&EventType="+eventType+"&tabledata=yes&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&RowId="+rowId+"&isDisabled="+isDisabled;
        if(dataValue && dataValue!=null && dataValue!='undefined'){
            requestString=requestString+"&dataValue="+encode_utf8(dataValue);
            if(documentIndex!="")
                requestString=requestString+"&docIndex="+documentIndex;
            if(isCopyRow)//issue with copy row in advanced listview
                requestString=requestString+"&copyRowOp=1";
        }

        var contentLoaderRef = new net.ContentLoader(url, addRowlistviewResponseHandler, ajaxFormErrorHandler, "POST", requestString, false);
        setTableModifiedFlag(controlId);

        attachDatePicker();
        rowId ++;
    //     document.getElementById("add_"+controlId).blur();
    }
    function openListViewModel(controlId,eventType,reqString){
        if(window.openOverLay)
        {   
            if( !window.openOverLay(controlId)){
                cancelBubble(); 
                return;
            }
        }

        document.getElementById('table_id').value=controlId;
        var url = "listViewModal.jsp";
        var requestString = "&controlId="+encode_utf8(controlId) +"&EventType="+eventType+"&tabledata=yes&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&RowId="+rowId+"&Operation=add";
        if(reqString && reqString!=='' && reqString != null)
            requestString=reqString;
        var contentLoaderRef = new net.ContentLoader(url, null, ajaxFormErrorHandler, "POST", requestString, false);
        var tableModalDiv =document.getElementById("iFrameListViewModal");
        //tableModalDiv.innerHTML=contentLoaderRef.req.responseText;
        jQuery(tableModalDiv).html(contentLoaderRef.req.responseText);
        if(!reqString){
            document.getElementById("tablelistPrevious").disabled = true;
            document.getElementById("tablelistNext").disabled= true;
        }
        isListviewOpened = true;
        //doInit();
        if(typeof reqString=="undefined")//Bug 80908 Start
            listViewInit(controlId,'A');
        else
            listViewInit(controlId,'M');//Bug 80908 End
    //    $('.textbox').each(function() {
    //        if(this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="" )
    //         maskfield(this,'savedinput');
    //         
    //     })
         isListviewOpened = false;

        //attachDatePicker();

    //    $('.maskedText').each(function(){
    //        var digitGroup  = parseInt(this.getAttribute("dgroup"));
    //        var precision=typeof this.getAttribute("Precision")=='undefined'?'2':this.getAttribute("Precision");
    //        var typeofvalue = this.getAttribute("typeofvalue");
    //        var decimal="";
    //        if(typeofvalue =="Float")
    //                decimal=precision;
    //         jQuery(this).autoNumeric('destroy');
    //        jQuery(this).autoNumeric('init',{
    //            dGroup: digitGroup,
    //            mDec: '2'
    //        });
    //    });

    }

    function maskfield(controlRef,type){
                var max=controlRef.getAttribute("rangemax");
                var min=controlRef.getAttribute("rangemin");
                var controlId = controlRef.getAttribute("id");
                //var typeofvalue=typeof controlRef.getAttribute("typeofvalue")=='controlRef'?'':controlRef.getAttribute("typeofvalue");
                var typeofvalue=controlRef.getAttribute("typeofvalue");
                var precision=typeof controlRef.getAttribute("Precision")=='undefined'?'2':controlRef.getAttribute("Precision");
                var decimal='2';
				if(typeofvalue =="Text")
                   decimal = (window.allowPrecisionInText)? allowPrecisionInText():'0';
                if(typeofvalue =="Float")
                    decimal=(window.allowPrecisionInFloat && (allowPrecisionInFloat(controlId)<precision))?allowPrecisionInFloat(controlId):precision;
                if(typeofvalue =="Integer")
                    decimal='0';
                if(typeofvalue =="Long")
                    decimal='0';
                
                if(controlRef.getAttribute("maskingPattern") && controlRef.getAttribute("maskingPattern").toString()!='nomasking'&& controlRef.getAttribute("maskingPattern").toString()!='email'){
                if(controlRef.getAttribute("maskingPattern").toString()!='currency_rupees' && controlRef.getAttribute("maskingPattern").toString()!=='currency_dollar' && controlRef.getAttribute("maskingPattern").toString()!=='currency_naira' && controlRef.getAttribute("maskingPattern").toString()!=='currency_yen' && controlRef.getAttribute("maskingPattern").toString()!=='currency_euro' && controlRef.getAttribute("maskingPattern").toString()!=='currency_french' && controlRef.getAttribute("maskingPattern").toString()!=='currency_bahamas' && controlRef.getAttribute("maskingPattern").toString()!=='currency_greek' && controlRef.getAttribute("maskingPattern").toString()!=='' && controlRef.getAttribute("maskingPattern").toString()!=='percentage'){
                        var placeholder;
                        if(controlRef.getAttribute("maskingPattern").toString().charAt(controlRef.getAttribute("maskingPattern").toString().length-1)!='$'){
                            if(controlRef.getAttribute("maskingPattern").toString()=='dgroup3' || controlRef.getAttribute("maskingPattern").toString()=='dgroup2'){
                                var digitGroup = parseInt(controlRef.getAttribute("maskingPattern").charAt(controlRef.getAttribute("maskingPattern").length-1));
                                jQuery(controlRef).autoNumeric('init',{
                                    dGroup: digitGroup,
                                    mDec: decimal                                

                                });
                                if(type=='input' && controlRef.value!='')
                                    jQuery(controlRef).autoNumeric('set', controlRef.value);
                                else if(type=='label' && controlRef.innerHTML)
                                    jQuery(controlRef).autoNumeric('set', controlRef.innerHTML);
                            }
                            else{
                                if(typeofvalue=='Float' || controlRef.getAttribute("maskingPattern").toString()=='NZP'){
                                    jQuery(controlRef).autoNumeric('init',{
                                        aSep : '',  
                                        aDec: '.', 
                                        mDec: decimal,
                                        aPad: false
                                    });
                                }
                                else{
                                placeholder=controlRef.getAttribute("maskingPattern").replace(/[A-Za-z0-9*#]/mg , "_");
                                jQuery(controlRef).mask(controlRef.getAttribute("maskingPattern"), {
                                    placeholder: placeholder
                                }, {
                                    clearIfNotMatch: true
                                });
                                }
                            }
                        }
                    }

                    else{
                        var asign='';
                        var dgroup='';
                        var psign='p';
                    var adec='.';
                    var asep=',';
                        if(controlRef.getAttribute("maskingPattern").toString()==='currency_rupees'){
                            asign='Rs ';
                            dgroup=2;
                        }
                        else if(controlRef.getAttribute("maskingPattern").toString()==='currency_dollar'){
                            asign='$ ';
                            dgroup=3;
                        }
                        else if(controlRef.getAttribute("maskingPattern").toString()==='currency_naira'){
                            asign='₦ ';
                            dgroup=3;
                        }
                        else if(controlRef.getAttribute("maskingPattern").toString()==='currency_yen'){
                            asign='¥ ';
                            dgroup=3;
                        }
                        else if(controlRef.getAttribute("maskingPattern").toString()==='currency_euro'){
                            asign='€ ';
                            dgroup=3;
                        }
                    else if(controlRef.getAttribute("maskingPattern").toString()==='currency_french'){
  //                      asign=' CHF';
                        dgroup=3;
                        adec = ',';
                        asep = ' ';
                        psign= 's';
                    }
                    else if(controlRef.getAttribute("maskingPattern").toString()==='currency_bahamas'){
                            asign='B$ ';
                            dgroup=3;
                    }
                    else if(controlRef.getAttribute("maskingPattern").toString()==='currency_greek'){
                        dgroup=3;
                        adec = ',';
                        asep = '.';
                        psign= 's';
                    }
                        if(controlRef.getAttribute("maskingPattern").toString()!=='percentage' && controlRef.getAttribute("maskingPattern").toString() !=='currency_yen'){
                            if(max===null)
                                jQuery(controlRef).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign,
                                    mDec: decimal,
                                aNeg:true,
                                aDec: adec,
                                aSep: asep
                                });
                            else{
                                jQuery(controlRef).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign, 
                                mDec: decimal,
                                aDec: adec,
                                aSep: asep
                                });
                            }
                        }
                        else if(controlRef.getAttribute("maskingPattern").toString() =='currency_yen'){
                            if(max===null)
                                jQuery(controlRef).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign,
                                    mDec: "0",
                                aNeg:true,
                                aDec: adec,
                                aSep: asep
                                });
                            else{
                                jQuery(controlRef).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign, 
                                mDec: "0",
                                aDec: adec,
                                aSep: asep
                                });
                            }
                        }
                        else
                            jQuery(controlRef).autoNumeric('init',{
                                aSign: " %",
                                pSign:'s',
                                mDec: decimal
                            });
                        if(type=='input' && controlRef.value!='')
                            jQuery(controlRef).autoNumeric('set', controlRef.value);
                        else if(type=='label' && controlRef.innerHTML)
                            jQuery(controlRef).autoNumeric('set', controlRef.innerHTML);
                    }

                }
                if((typeofvalue=='Float'||typeofvalue=='Integer'||typeofvalue=='Long' )&& controlRef.getAttribute("maskingPattern") && controlRef.getAttribute("maskingPattern").toString()=='nomasking'){
                   jQuery(controlRef).autoNumeric('init',{
                                aSep : '',  
                                aDec: '.', 
                                mDec: decimal
                            });
                }
                
    }
    //Bug 81747
    function validateListviewDataType(){
     var listViewControls = document.getElementsByClassName('tableControl');
        var advancedListviewControls=document.getElementsByClassName('advancedListviewControl');
        var componentmap=Object.keys(ComponentValidatedMap);
        if(componentmap.length!=0){
             if(document.getElementById('listViewModal').style.display!='none' && listViewControls.length!=0){
                for(var j=0;j<listViewControls.length;j++){
                    for(var k=0;k<componentmap.length;k++){
                     if(componentmap[k]==listViewControls[j].id){
                         if(!isControlVisible(listViewControls[j],"normal",false)){
                             delete componentmap[k];
                         }
                         else{
                             return false;
                         }
                     }
                     }                   
                }
             }
            else if(document.getElementById('advancedListViewModal').style.display!='none' && advancedListviewControls.length!=0){
                for(var j=0;j<advancedListviewControls.length;j++){
                    for(var k=0;k<componentmap.length;k++){
                     if(componentmap[k]==advancedListviewControls[j].id){
                         if(!isControlVisible(listViewControls[j],"advance",false)){
                             delete componentmap[k];
                         }
                         else{
                             return false;
                         }
                     }
                     }                     
                }        
            }        
        }
        return true;
    }
    
     function validateServerListviewDataType(){
     var listViewControls = document.getElementsByClassName('tableControl');
        var advancedListviewControls=document.getElementsByClassName('advancedListviewControl');
        var serverComponentMap = Object.keys(serverValidationMap);
        if(serverComponentMap.length!=0){
             if(document.getElementById('listViewModal').style.display!='none' && listViewControls.length!=0){
                for(var j=0;j<listViewControls.length;j++){
                    for(var k=0;k<serverComponentMap.length;k++){
                     if(serverComponentMap[k]==listViewControls[j].id){
                         if(!isControlVisible(listViewControls[j],"normal",false)){
                             delete serverComponentMap[k];
                         }
                         else{
                             showMessage(listViewControls[j].id,THE_VALUE+" : "+serverValidationMap[listViewControls[j].id].controlValue+" "+INCOMPATIBLE_MSG+" "+FIELD+" "+serverValidationMap[listViewControls[j].id].controlId,"error");
                             return false;
                         }
                     }
                     }                   
                }
             }
            else if(document.getElementById('advancedListViewModal').style.display!='none' && advancedListviewControls.length!=0){
                for(var j=0;j<advancedListviewControls.length;j++){
                    for(var k=0;k<serverComponentMap.length;k++){
                     if(serverComponentMap[k]==advancedListviewControls[j].id){
                         if(!isControlVisible(advancedListviewControls[j],"advance",false)){
                             delete serverComponentMap[k];
                         }
                         else{
                             showMessage(advancedListviewControls[j].id,THE_VALUE+" : "+serverValidationMap[advancedListviewControls[j].id].controlValue+INCOMPATIBLE_MSG+" "+FIELD+" "+serverValidationMap[advancedListviewControls[j].id].controlId,"error");
                             return false;
                         }
                     }
                     }                     
                }        
            }        
        }
        return true;
    }
    
    function addRowToTable(controlId,isNext,isCopyRow){
        //Bug 81747
        var dataTypeValid=validateListviewDataType();
        if(isServerValidation=="true" && dataTypeValid){
             dataTypeValid = validateServerListviewDataType();
        }
        if(!dataTypeValid){
            if(typeof isNext!="undefined"&&isNext){
                if(document.getElementById("addrowandnext_"+controlId)!=null)//Bug 84293
                    document.getElementById("addrowandnext_"+controlId).removeAttribute("data-dismiss");
            }
            else{
                if(document.getElementById("addrow_"+controlId)!=null)//Bug 84293
                    document.getElementById("addrow_"+controlId).removeAttribute("data-dismiss");
                if(document.getElementById("copyrow_"+controlId)!=null)//Bug 84293
                    document.getElementById("copyrow_"+controlId).removeAttribute("data-dismiss");
            }
            return false;
        }
        var valid = validateMandatoryFields();
        if(!valid){
            if(typeof isNext!="undefined"&&isNext){
                if(document.getElementById("addrowandnext_"+controlId)!=null)//Bug 84293
                    document.getElementById("addrowandnext_"+controlId).removeAttribute("data-dismiss");
                }
            else{
                if(document.getElementById("addrow_"+controlId)!=null)//Bug 84293
                    document.getElementById("addrow_"+controlId).removeAttribute("data-dismiss");
                if(document.getElementById("copyrow_"+controlId)!=null)//Bug 84293
                    document.getElementById("copyrow_"+controlId).removeAttribute("data-dismiss");
            }
            return false;
        }
        var customListViewValid ;
        if(window.customListViewValidation){
            customListViewValid = customListViewValidation(controlId,"A");
            if(!customListViewValid){
                if(typeof isNext!="undefined"&&isNext){
                    if(document.getElementById("addrowandnext_"+controlId)!=null)//Bug 84293
                        document.getElementById("addrowandnext_"+controlId).removeAttribute("data-dismiss");
                }
                else{
                    if(document.getElementById("addrow_"+controlId)!=null)//Bug 84293
                        document.getElementById("addrow_"+controlId).removeAttribute("data-dismiss");
                    if(document.getElementById("copyrow_"+controlId)!=null)//Bug 84293
                        document.getElementById("copyrow_"+controlId).removeAttribute("data-dismiss");
                }
                return false;
            }
            else{
                if(typeof isNext!="undefined"&&isNext){
                    if(document.getElementById("addrowandnext_"+controlId)!=null)//Bug 84293
                        document.getElementById("addrowandnext_"+controlId).setAttribute("data-dismiss","modal");
                }
                else{
                    if(document.getElementById("addrow_"+controlId)!=null)//Bug 84293
                        document.getElementById("addrow_"+controlId).setAttribute("data-dismiss","modal");
                    if(document.getElementById("copyrow_"+controlId)!=null)//Bug 84293
                        document.getElementById("copyrow_"+controlId).setAttribute("data-dismiss","modal");
                }
            }
        }

        var dataValue={};
        var elementsArray=document.getElementsByClassName('tableControl');
        var invalidControls=[];
        var nullElements=[];
        var isDuplicate = false;
        var table = document.getElementById(controlId);

        for(var i=0;i<elementsArray.length;i++){
            if(elementsArray[i].className.indexOf("noDuplicate")!=-1){    
                var refclass = elementsArray[i].className.substring(elementsArray[i].className.indexOf("noDuplicate")).split(" ")[0];
                var tableCells = table.getElementsByClassName(refclass);
                for(var j=0;j<tableCells.length;j++){
                    var tdContent = tableCells[j].textContent;
                    if (tdContent!="") {
                        if(elementsArray[i].getAttribute("datatype")=="combobox" && elementsArray[i].getAttribute("type")=="text"){
                            var listItems = $(elementsArray[i]).closest(".es-list").find("li");
                                for (var p = 0; p < listItems.length; p++) {
                                    var option = listItems[p].textContent;
                                    if (listItems[p].getAttribute("value") != null && elementsArray[i].value == option){
                                        if( tdContent == listItems[p].getAttribute("value"))
                                        {
                                            isDuplicate = true;
                                            invalidControls.push(elementsArray[i]);
                                            break;
                                        }                                        
                                    }
                                }
                        }
                        else{
                            if(tdContent.toLowerCase()==elementsArray[i].value.toLowerCase()){
                                isDuplicate = true;
                                invalidControls.push(elementsArray[i]);
                                break;
                            }   
                        }
                    }
                }
            }
        }

        $(elementsArray).each(function(i) {
            if((this.className.indexOf("denyNull")!=-1)&&(this.value==""||this.value==null)){
                nullElements.push(this.className.split("_")[1]);
            }

            if(this.getAttribute("typeofvalue") && (this.getAttribute("typeofvalue")==='Boolean' || this.getAttribute("typeofvalue")==='Integer' || this.getAttribute("typeofvalue")==='Float' || this.getAttribute("typeofvalue")==='Long')){
                if(!validateTypeOfValue(this))
                {
                    invalidControls.push(this);
                }
            }
            else{
                var type=jQuery(this).attr("datatype");
                if(!validateValue(this,type))
                {
                    invalidControls.push(this);
                }
            }

              var value=this.value?getControlValue(this):this.innerHTML;
        if(this.getAttribute("maskingPattern") && (this.getAttribute("maskingPattern").toString()==='currency_rupees' || this.getAttribute("maskingPattern").toString()==='currency_dollar' || this.getAttribute("maskingPattern").toString()==='currency_naira' || this.getAttribute("maskingPattern").toString()==='currency_yen' || this.getAttribute("maskingPattern").toString()==='currency_euro' || this.getAttribute("maskingPattern").toString()==='currency_french' || this.getAttribute("maskingPattern").toString()==='currency_greek' || this.getAttribute("maskingPattern").toString()==='currency_bahamas' || this.getAttribute("maskingPattern").toString()==='percentage'|| this.getAttribute("maskingPattern").toString()==='dgroup2'|| this.getAttribute("maskingPattern").toString()==='dgroup3'|| this.getAttribute("maskingPattern").toString()==='NZP'))
            {
                value =  getControlValue(this);
            }
            if(this.type==='select-one'){
                if($(this).attr('svt')!=null && $(this).attr('svt')==='L'){
                    value=this.value===''?'':value;
                } else {
                   value=this.value===''?'':this.value;
                }
//                    if(isShowGridComboLabel=="true"){
//                      value = getSelectedItemLabel(this.id);
//                  }
            }
            if(this.type && (this.type==="checkbox" || this.type==="radio"))
                value=this.checked;
            if(this.classList.contains("editableCombo")){
                value=getControlValue(this);
                //Bug 91892
//                 if(isShowGridComboLabel){
//                     value=getSelectedItemLabel(this.id);
//                     if(value=="Select") value="";
//                 }
                 //Bug 91892
            }
            dataValue[formatJSONValue(this.getAttribute("labelName"))]=formatJSONValue(value);

        });
        var invalidControl;
        for(var j=0;j<elementsArray.length;j++){
            if(!validateColumnValue(elementsArray[j],controlId,false)){
                invalidControl=elementsArray[j];
                break;

            }
        }
        if(invalidControls.length>0){
            if(typeof isNext!="undefined"&&isNext){
                if(document.getElementById("addrowandnext_"+controlId)!=null)//Bug 84293
                    document.getElementById("addrowandnext_"+controlId).removeAttribute("data-dismiss");
            }
            else{
                 if(document.getElementById("addrow_"+controlId)!=null)//Bug 84293
                    document.getElementById("addrow_"+controlId).removeAttribute("data-dismiss");
                 if(document.getElementById("copyrow_"+controlId)!=null)//Bug 84293
                    document.getElementById("copyrow_"+controlId).removeAttribute("data-dismiss");
            }
            if(isDuplicate)
                showSplitMessage(invalidControls[0],"Duplicate values not allowed in "+invalidControls[0].getAttribute("labelname"),DATA_TITLE,"error");
            return false;
        }

            dataValue = saveRichTextEditorData('iFrameListViewModal',dataValue,isCopyRow);

    //    if(nullElements.length>0){
    //        document.getElementById("addrow_"+controlId).removeAttribute("data-dismiss");
    //        showMessage("","Null values not allowed in "+nullElements,"error");
    //        return false;
    //    }

    //    if(invalidControl!=undefined || invalidControl!=null){
    //        document.getElementById("addrow_"+controlId).removeAttribute("data-dismiss");
    //        var validationmsg = document.getElementById(controlId+"_"+invalidControl.getAttribute("labelName")+"_msg").innerHTML;
    //        showMessage(invalidControl,validationmsg +":"+'<strong>'+invalidControl.getAttribute("labelName")+'</strong>',"error");
    //      
    //        return false;
    //    }

            executeListView(document.getElementById('table_id').value,'click',JSON.stringify(dataValue),isCopyRow);
            if(typeof isNext!="undefined"&&isNext){
                if(document.getElementById("addrowandnext_"+controlId)!=null)//Bug 84293
                    document.getElementById("addrowandnext_"+controlId).setAttribute("data-dismiss","modal");
                    openListViewModel(controlId,'click'); 
            }
            else{
                if(document.getElementById("addrow_"+controlId)!=null)//Bug 84293
                    document.getElementById("addrow_"+controlId).setAttribute("data-dismiss","modal");
                if(document.getElementById("copyrow_"+controlId)!=null)//Bug 84293
                    document.getElementById("copyrow_"+controlId).setAttribute("data-dismiss","modal");
            }
            
            setTableModifiedFlag(controlId);

    }

    function copyRowData(tableId){
     var valid = addRowToTable(tableId,"",true);
     if(multipleRowDuplicate=='true')
     {
        if(document.getElementById("addrow_"+tableId)!=null)
            document.getElementById("addrow_"+tableId).removeAttribute("data-dismiss");
        if(document.getElementById("copyrow_"+tableId)!=null)
            document.getElementById("copyrow_"+tableId).removeAttribute("data-dismiss");
        if(document.getElementById("addrowandnext_"+tableId)!=null)
            document.getElementById("addrowandnext_"+tableId).removeAttribute("data-dismiss");
     }
     //document.getElementsByClassName('selectedRow')[0].classList.remove("selectedRow");
     if((valid==undefined || valid==true) && multipleRowDuplicate!='true'){
        removerowToModify();
      }
     if(window.copyRowPosthook){
         copyRowPosthook(tableId);
     }
    }

    function copyAdvancedListViewRowData(tableId){
     addRowToAdvancedListview(tableId,true);//issue with copy row in advanced listview
     if(window.copyAdvancedListviewRowPosthook){
         copyAdvancedListviewRowPosthook(tableId);
     }
     if(multipleRowDuplicate=='true')
     {
        if(document.getElementById("duplicateAdvancedListviewchanges_"+tableId)!=null)
            document.getElementById("duplicateAdvancedListviewchanges_"+tableId).removeAttribute("data-dismiss");
        if(document.getElementById("addAdvancedListviewrow_"+tableId)!=null)//Bug 84293
        {
            document.getElementById("addAdvancedListviewrow_"+tableId).removeAttribute("data-dismiss");
            document.getElementById("addAdvancedListviewrowNext_"+tableId).removeAttribute("data-dismiss");
        }
     } 
     removeAdvancedListviewrowToModify();
    }

    function modifyTableRows(ref,controlId){
        var rowIndex = $(ref).closest('tr').index();
        if( onRowClick(controlId,rowIndex) ){
            ref.classList.add("rowToModify");

            var colIndex = parseInt($(ref).closest('td').index())-1;
            $(ref).closest('tr')[0].classList.add("selectedRow");
            document.getElementById("rowCount").value=rowIndex;
            var reqString="&controlId="+encode_utf8(controlId) +"&EventType="+"click"+"&tabledata=yes&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&RowId="+rowId+"&modifyFlag=yes"+"&rowIndex="+rowIndex+"&colIndex="+colIndex;
            openListViewModel(controlId,'click',reqString); 
            enableDisableNextPreviiousButton(controlId,rowIndex);            
            $("#listViewModal").modal();
        }
        else{
            $(ref).closest('tr')[0].classList.remove("selectedRow");
        }
        var lineNo=rowIndex+1;
        if(autoIncrementLabelDisplay=='true')
        {
            var value=document.getElementsByClassName('listViewHeader')[0].innerHTML;
            value='{'+lineNo+'}'+value;
            document.getElementsByClassName('listViewHeader')[0].innerHTML=value;
        }
    }
    function modifyAdvancedTableRows(ref,controlId){
        var rowIndex = $(ref).closest('tr').index();
        if( onRowClick(controlId,rowIndex) ){
            ref.classList.add("advancedListviewrowToModify");

            var colIndex = parseInt($(ref).closest('td').index())-1;
            $(ref).closest('tr')[0].classList.add("selectedAdvancedListviewRow");
            document.getElementById("advancedListviewRowCount").value=rowIndex;
            var reqString="&controlId="+encode_utf8(controlId) +"&EventType="+"click"+"&tabledata=yes&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&RowId="+rowId+"&modifyFlag=yes"+"&rowIndex="+rowIndex+"&colIndex="+colIndex;
            openAdvancedListViewModel(controlId,'click',reqString); 
            enableDisableNextPreviousButtonAdvancedListview(controlId,rowIndex);
            $("#advancedListViewModal").modal();
        }
        else{
             $(ref).closest('tr')[0].classList.remove("selectedAdvancedListviewRow");
        }
    }

    function disableListViewControls(controlId){
        var tableRef = document.getElementById(controlId);
        var addRef = document.getElementById("addrow_"+controlId);
        var saveRef = document.getElementById("savechanges_"+controlId);
        var duplicateRef = document.getElementById("copyrow_"+controlId);//Bug 83138
        if(tableRef!=undefined && tableRef.classList.contains("disabledTable")){
            if(addRef!=null && addRef!=undefined){
                addRef.disabled = true;
                addRef.classList.add("disabledBGColor");
                addRef.classList.add("disabledBtnColor");
            }
            if(saveRef!=null && saveRef!=undefined){
                saveRef.disabled = true;
                saveRef.classList.add("disabledBGColor");
                saveRef.classList.add("disabledBtnColor");
            }
            if(duplicateRef!=null && duplicateRef!=undefined){//Bug 83138
                duplicateRef.disabled = true;//Bug 83138
                duplicateRef.classList.add("disabledBGColor");
                duplicateRef.classList.add("disabledBtnColor");
            }
            $(".tableControl").each(function(){
            setStyle($(this).attr("id"),"disable","true");
        });
        $(".tableButtonControl").each(function(){
            setStyle($(this).attr("id"),"disable","true");
        });
        }


    }

    function disableAdvancedListViewControls(controlId){
        var tableRef = document.getElementById(controlId);
        var addRef = document.getElementById("addAdvancedListviewrow_"+controlId);
        var saveRef = document.getElementById("saveAdvancedListviewchanges_"+controlId);
        var duplicateRef = document.getElementById("duplicateAdvancedListviewchanges_"+controlId);//Bug 83138
        if(tableRef!=undefined && tableRef.classList.contains("disabledTable")){
            if(addRef!=null && addRef!=undefined){
                addRef.disabled = true;
                addRef.classList.add("disabledBGColor");
                addRef.classList.add("disabledBtnColor");
            }
            if(saveRef!=null && saveRef!=undefined){
                saveRef.disabled = true;
                saveRef.classList.add("disabledBGColor");
                saveRef.classList.add("disabledBtnColor");
            }
            if(duplicateRef!=null && duplicateRef!=undefined){//Bug 83138
                duplicateRef.disabled = true;//Bug 83138
                duplicateRef.classList.add("disabledBGColor");
                duplicateRef.classList.add("disabledBtnColor");
            }
            $(".advancedListviewControl").each(function(){
                setStyle($(this).attr("id"),"disable","true");
            });
        }


    }

    function disableListView(controlId){
        var control = document.getElementById(controlId);
        var addRef = document.getElementById("add_"+controlId);
                    if(addRef!=null && addRef!=undefined)
                        addRef.disabled = true;
                    var selectAllCheck = document.getElementById("select_"+controlId);
                    if(selectAllCheck!=null && selectAllCheck!=undefined){
                        selectAllCheck.disabled = true;
                    }
                    var selectRowChecks = control.getElementsByClassName("selectRow");
                    var i;
                    for(i=0;i<selectRowChecks.length;i++){
                        selectRowChecks[i].disabled = true;
                        selectRowChecks[i].parentNode.parentNode.classList.add("disabledTableBGColor");
                    }
                    control.classList.add("disabledTable");
                     if(control.getAttribute("type")=="Table"){
                            $("#"+controlId+' .control-class').parent().parent().addClass("disabledTableBGColor");
                             $("#"+controlId+' .control-class').removeClass("disabledBGColor");
                        }
                        else{
                             $("#"+controlId+' .control-class').each(function(){
                                 disablePicklistButtons(this.id, true);
                                 this.parentNode.classList.add("disabledTableBGColor");
                                 this.classList.remove("disabledBGColor");
                             });
                        }
    }

    function enableListView(controlId,showHideAddDelete){
        showGridAddDeleteButtons(controlId,showHideAddDelete);
        var addRef = document.getElementById("add_"+controlId);
          var control = document.getElementById(controlId);
                    if(addRef!=null && addRef!=undefined)
                        addRef.disabled = false;
                    var selectAllCheck = document.getElementById("select_"+controlId);
                    if(selectAllCheck!=null && selectAllCheck!=undefined){
                        selectAllCheck.disabled = false;
                    }
                    var selectRowChecks = control.getElementsByClassName("selectRow");
                    var i;
                    for(i=0;i<selectRowChecks.length;i++){
                        selectRowChecks[i].disabled = false;
                         selectRowChecks[i].parentNode.parentNode.classList.remove("disabledTableBGColor");
                    }
                    control.classList.remove("disabledTable");
                     if(control.getAttribute("type")=="Table"){
                            $("#"+controlId+' .control-class').parent().parent().removeClass("disabledTableBGColor");
                        }
                        else{
                             $("#"+controlId+' .control-class').each(function(){
                                 disablePicklistButtons(this.id, false);
                                 this.parentNode.classList.remove("disabledTableBGColor");
                             });
                        }
    }


    function modifyRowTableData(controlId){
        var charLimitData=document.getElementById("scrollDiv_"+controlId).previousSibling.getElementsByTagName("th");
        var dataTypeValid=validateListviewDataType();
        if(isServerValidation=="true" && dataTypeValid ){
            dataTypeValid = validateServerListviewDataType()
        }
        if(!dataTypeValid){
            document.getElementById("savechanges_"+controlId).removeAttribute("data-dismiss");
            return false;
        }
        else{
            document.getElementById("savechanges_"+controlId).setAttribute("data-dismiss","modal");
        }

        var valid = validateMandatoryFields();
        if(!valid){
            document.getElementById("savechanges_"+controlId).removeAttribute("data-dismiss");
            return false;
        }
        
        var customListViewValid ;
        if(window.customListViewValidation){
            customListViewValid = customListViewValidation(controlId,"M");
            if(!customListViewValid){
                document.getElementById("savechanges_"+controlId).removeAttribute("data-dismiss");
                return false;
            }
            else{
                document.getElementById("savechanges_"+controlId).setAttribute("data-dismiss","modal");
            }
        }
        var elementToModify=document.getElementsByClassName('rowToModify');
        var rowIndex = $(elementToModify[0]).closest('tr').index();
        var invalidControls=[];
        var dataValue={};
        var dataValue1={};
        var elementsArray=document.getElementsByClassName('tableControl');
        var nullElements=[];
        var isDuplicate = false;
        var table = document.getElementById(controlId);

        for(var i=0;i<elementsArray.length;i++){
            if(elementsArray[i].className.indexOf("noDuplicate")!=-1){
                var refclass = elementsArray[i].className.substring(elementsArray[i].className.indexOf("noDuplicate")).split(" ")[0];
                var tableCells = table.getElementsByClassName(refclass);
                for(var j=0;j<tableCells.length;j++){
                    var tdContent = tableCells[j].innerText;
                    if (tdContent!="") {
                        if(elementsArray[i].getAttribute("datatype")=="combobox" && elementsArray[i].getAttribute("type")=="text"){
                            var listItems = $(elementsArray[i]).closest(".es-list").find("li");
                                for (var p = 0; p < listItems.length; p++) {
                                    var option = listItems[p].innerText;
                                    if (listItems[p].getAttribute("value") != null && elementsArray[i].value == option){
                                        if( tdContent == listItems[p].getAttribute("value"))
                                        {
                                            isDuplicate = true;
                                            invalidControls.push(elementsArray[i]);
                                            break;
                                        }                                        
                                    }
                                }
                        }
                        else{
                            if(tdContent.toLowerCase()==elementsArray[i].value.toLowerCase() &&  j!=document.getElementById("rowCount").value){
                            isDuplicate = true;
                            invalidControls.push(elementsArray[i]);
                            break;
                            }
                        }
                    }
                }
            }

        }
        $(elementsArray).each(function(i) {
            if((this.className.indexOf("denyNull")!=-1)&&(this.value==""||this.value==null)){
                nullElements.push(this.className.split("_")[1]);
            }
            if(this.getAttribute("typeofvalue") && (this.getAttribute("typeofvalue")==='Date' || this.getAttribute("typeofvalue")==='Boolean' || this.getAttribute("typeofvalue")==='Integer' || this.getAttribute("typeofvalue")==='Float' || this.getAttribute("typeofvalue")==='Long')){
                if(!validateTypeOfValue(this))
                {
                    invalidControls.push(this);
                }
            }
            else{
                var type=jQuery(this).attr("datatype");
                if(!validateValue(this,type))
                {
                    invalidControls.push(this);
                }
            }
            for(var i=0;i<elementsArray.length;i++){
                if(elementsArray[i].getAttribute("datatype")=="combobox")
                {
                        var listItems = $(elementsArray[i]).closest(".es-list").find("li");
                        for( var p=0; p < listItems.length; p++ ) {
                            var option = listItems[p].innerText;
                            if (elementsArray[i].value == option)
                                value =  listItems[p].getAttribute("value");
                        }
                }
                else
                   {
                    var value;
                    if(this.type == "text" && this.style.textTransform == "uppercase")
                        value=(this.value?this.value:this.innerHTML).toUpperCase();
                    else if(this.type == "text" && this.style.textTransform == "lowercase")
                          value=(this.value?this.value:this.innerHTML).toLowerCase();
                    else
                        value=this.value?this.value:this.innerHTML;
		}
             }
           
        if(this.getAttribute("maskingPattern") && (this.getAttribute("maskingPattern").toString()==='currency_rupees' || this.getAttribute("maskingPattern").toString()==='currency_dollar' || this.getAttribute("maskingPattern").toString()==='currency_naira' || this.getAttribute("maskingPattern").toString()==='currency_yen' || this.getAttribute("maskingPattern").toString()==='currency_euro' || this.getAttribute("maskingPattern").toString()==='currency_french' || this.getAttribute("maskingPattern").toString()==='currency_greek' || this.getAttribute("maskingPattern").toString()==='currency_bahamas' || this.getAttribute("maskingPattern").toString()==='percentage'|| this.getAttribute("maskingPattern").toString()==='dgroup2'|| this.getAttribute("maskingPattern").toString()==='dgroup3'|| this.getAttribute("maskingPattern").toString()==='NZP'))
            {
                value =  getControlValue(this);
            }
            if(this.type==='textarea' || this.type == "text"){
                value=this.value;
                if((value=='' || value==undefined) && (this.classList.contains("richtexteditor"))) //85748
                    value=this.innerHTML; // Bug 84951
            }
            if(this.type==='select-one'){
                if(isShowGridComboLabel=="true"){
                    value = getSelectedItemLabel(this.id);
                }
                if($(this).attr('svt')!=null && $(this).attr('svt')==='L'){
                    value=getControlValue(this);
                    value=this.value===''?'':value;   
                } else {
                   value=this.value===''?'':this.value;   
                }
            }
            if(this.type && (this.type==="checkbox" || this.type==="radio"))
                value=this.checked;
            
            if(this.classList.contains("editableCombo")){
                 value=getControlValue(this);
                 //Bug 91892
//                 if(isShowGridComboLabel){
//                     value=getSelectedItemLabel(this.id);
//                     if(value=="Select") value="";
//                 }
                 //Bug 91892
            }
            dataValue[formatJSONValue(this.getAttribute("labelName"))]=formatJSONValue(value);
            
            if(isCheckboxValueChange=="true")
            {
                if(this.type && (this.type==="checkbox"))
                    if(this.checked)
                        value='Yes';
                    else
                         value='No';
            }
            if(this.type==='select-one'){
                if(isShowGridComboLabel=="true"){
                    value = getSelectedItemLabel(this.id);
                }
            }
            if((this.getAttribute("datatype")==="text"||this.getAttribute("datatype")==="textarea"||this.getAttribute("datatype")==="Text")&&this.type==='text'||(this.type==='textarea' && !this.classList.contains("richtexteditor")))
            {
                var charLim="";
                var ctrl_label=this.parentElement;
                while(!ctrl_label.classList.contains("controls")&&ctrl_label!=null)
                {     
                    ctrl_label=ctrl_label.parentElement;
                }
                for(var k=1;k<charLimitData.length;k++)
                {
                    if(ctrl_label!=null && ctrl_label.previousSibling!=null &&charLimitData[k].innerHTML==ctrl_label.previousSibling.innerText)
                    {
                        if(charLimitData[k].getAttribute("charlimit")!=undefined)
                            charLim=charLimitData[k].getAttribute("charlimit");
                        break;
                    }
                }
                if(charLim!=null&&charLim!=""&&value.length>charLim)
                    dataValue1[formatJSONValue(this.getAttribute("labelName"))]=formatJSONValue(value.substring(0,charLim)+"...");
                else
                    dataValue1[formatJSONValue(this.getAttribute("labelName"))]=formatJSONValue(value);
            }    
            else
                    dataValue1[formatJSONValue(this.getAttribute("labelName"))]=formatJSONValue(value);
        });

        var invalidControl;
         for(var j=0;j<elementsArray.length;j++){
            if(!validateColumnValue(elementsArray[j],controlId,false)){
                invalidControl=elementsArray[j];
                break;

            }
        }

        if(invalidControls.length>0){
            document.getElementById("savechanges_"+controlId).removeAttribute("data-dismiss");
            if(isDuplicate)
                   showSplitMessage(invalidControls[0],"Duplicate values not allowed in "+invalidControls[0].getAttribute("labelname"),DATA_TITLE,"error");
            return false;
        }
    //    if(nullElements.length>0){
    //        document.getElementById("savechanges_"+controlId).removeAttribute("data-dismiss");
    //        showMessage("","Null values not allowed in "+nullElements,"error");
    //        return false;
    //    }
    //     if(invalidControl!=undefined || invalidControl!=null){
    //         document.getElementById("savechanges_"+controlId).removeAttribute("data-dismiss");
    //        var validationmsg = document.getElementById(controlId+"_"+invalidControl.getAttribute("labelName")+"_msg").innerHTML;
    //        showMessage(invalidControl,validationmsg +":"+'<strong>'+invalidControl.getAttribute("labelName")+'</strong>',"error");
    //        return false;
    //    }

            if(isOverlayOpen=="Y"){
                if(window.overrideRestrictOverlay)
                {
                    if(overrideRestrictOverlay(controlId)){
                        document.getElementById("savechanges_"+controlId).setAttribute("data-dismiss","modal");
                    }
                    else {
                        document.getElementById("savechanges_"+controlId).removeAttribute("data-dismiss");
                    }
                }else {
                        document.getElementById("savechanges_"+controlId).removeAttribute("data-dismiss");
                }
            }
            else{
                if(window.allowRestrictedOverlay)
                {
                    if(allowRestrictedOverlay(controlId)){
                        document.getElementById("savechanges_"+controlId).removeAttribute("data-dismiss");
                    }
                    else {
                        document.getElementById("savechanges_"+controlId).setAttribute("data-dismiss","modal");
                    }
                } else{
                    document.getElementById("savechanges_"+controlId).setAttribute("data-dismiss","modal");
                }
            }
            
            dataValue = saveRichTextEditorData('iFrameListViewModal',dataValue);
            var url = "action.jsp";
            var requestString=  "controlId="+encode_utf8(controlId) +"&rowIndex="+rowIndex +"&dataValue=" + encode_utf8(JSON.stringify(dataValue)) + "&modifyFlag=yes";
            if(documentIndex!='')
                requestString = requestString+"&docIndex="+documentIndex;
            var contentLoaderRef = new net.ContentLoader(url, modifyRowlistviewResponseHandler, ajaxFormErrorHandler, "POST", requestString, false);
            try{
                var json=JSON.parse(JSON.stringify(dataValue1));
                var month_array=["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
                $(elementToModify[0].parentNode).children().each(function(i) {
                    if(i>0){
                        if((mobileMode==="ios"||mobileMode==="android")&&this.children[0].getAttribute("columnMappedField")!=null){
                            if(this.children[0].getAttribute("columnMappedField")==="7"||this.children[0].getAttribute("columnMappedField")==="9"){
                                if(json[this.children[0].getAttribute("labelName")]!=""){
                                    var newdate=new Date(json[this.children[0].getAttribute("labelName")]);
                                    var ds="/",df="";
                                    if(globalDateSeparator=="1")
                                        ds="/";
                                    else if(globalDateSeparator=="2")
                                        ds="-";
                                    else if(globalDateSeparator=="3")
                                        ds=".";
                                    if(globalDateFormat=="1")
                                        df=newdate.getDate()+ds+(newdate.getMonth()+1)+ds+newdate.getFullYear();
                                    else if(globalDateFormat=="2")
                                        df=(newdate.getMonth()+1)+ds+newdate.getDate()+ds+newdate.getFullYear();
                                    else if(globalDateFormat=="3")
                                        df=newdate.getFullYear()+ds+(newdate.getMonth()+1)+ds+newdate.getDate();
                                    else if(globalDateFormat=="4")
                                        df=newdate.getDate()+ds+month_array[newdate.getMonth()]+ds+newdate.getFullYear();
                                    if(this.children[0].getAttribute("columnMappedField")==="9")
                                        df+=" "+newdate.getHours()+":"+newdate.getMinutes();
                                    this.children[0].innerHTML=encode_ParamValue(df);
                                }
                                else
                                    this.children[0].innerHTML="";
                            }
                            else
                                this.children[0].innerHTML=encode_ParamValue(escapeStringForHTML(json[this.children[0].getAttribute("labelName")]));
                        }
                        else
                            this.children[0].innerHTML=encode_ParamValue(escapeStringForHTML(json[this.children[0].getAttribute("labelName")]));
                    }
                });
                //elementToModify[0].children[0].innerHTML=json[elementToModify[0].children[0].getAttribute("labelName")];
                if(isOverlayOpen != "Y"){
                    if(window.allowRestrictedOverlay)
                    {
                        if(!allowRestrictedOverlay(controlId)){
                            elementToModify[0].classList.remove("rowToModify");
                            document.getElementsByClassName('selectedRow')[0].classList.remove("selectedRow");
                        }
                    } else{
                        elementToModify[0].classList.remove("rowToModify");
                        document.getElementsByClassName('selectedRow')[0].classList.remove("selectedRow");
                    } 
                }
                $('.listviewlabel').each(function() {
                
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
                || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
                {
                    maskfield(this,'label');
                }

            });
                var totalValueElements=document.getElementById('totallabel_'+controlId).innerHTML.split(",!,");
                for(var i=0;i<totalValueElements.length;i++){
                    if(totalValueElements[i]!=''){
                     $(document.getElementsByClassName(totalValueElements[i].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
                     var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
                    if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
                        || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
                        {
                        maskfield(this,'label');
                    }
                    });
                    }
                    showTotal('',totalValueElements[i]);
                }
                setTableModifiedFlag(controlId);
                reshuffleIndices(controlId);
                if(window.modifyRowPostHook)
                {
                    modifyRowPostHook(controlId,rowIndex);
                }
            }
            catch(ex){}

    }

    function modifyAdvancedRowTableData(controlId){
        if(Object.keys(ComponentValidatedMap).length!=0){
            document.getElementById("saveAdvancedListviewchanges_"+controlId).removeAttribute("data-dismiss");
            return false;
        }
        var elementToModify=document.getElementsByClassName('advancedListviewrowToModify');
        var rowIndex = $(elementToModify[0]).closest('tr').index();
        var valid = validateMandatoryFields();
        if(valid)
            valid = fetchCollapsedFrameHTML(controlId);
        if(isServerValidation=="true" && valid)
            valid = validateServerListviewDataType();
        if(!valid){
            document.getElementById("saveAdvancedListviewchanges_"+controlId).removeAttribute("data-dismiss");
            return false;
        }
        var customListViewValid ;
        if(window.customListViewValidation){
            customListViewValid = customListViewValidation(controlId,"M");
            if(!customListViewValid){
                document.getElementById("saveAdvancedListviewchanges_"+controlId).removeAttribute("data-dismiss");
                return false;
            }
            else{
                document.getElementById("saveAdvancedListviewchanges_"+controlId).setAttribute("data-dismiss","modal");
            }
        }
        var invalidControls=[];
        var dataValue={};
        var elementsArray=document.getElementsByClassName('advancedListviewControl');
        var nullElements=[];
        $(elementsArray).each(function(i) {
            if(this.tagName=='TABLE')
                return true;
            if((this.className.indexOf("denyNull")!=-1)&&(this.value==""||this.value==null)){
                nullElements.push(this.className.split("_")[1]);
            }

            if(this.getAttribute("typeofvalue") && (this.getAttribute("typeofvalue")==='Date' || this.getAttribute("typeofvalue")==='Boolean' || this.getAttribute("typeofvalue")==='Integer' || this.getAttribute("typeofvalue")==='Float' || this.getAttribute("typeofvalue")==='Long')){
                if(!validateTypeOfValue(this))
                {
                    invalidControls.push(this);
                }
            }
            else{
                var type=jQuery(this).attr("datatype");
                if(!validateValue(this,type))
                {
                    invalidControls.push(this);
                }
            }
            var value;
            if($(this).attr('svt')!=null && $(this).attr('svt')==='L'){
              value=this.value?getControlValue(this):this.innerHTML;  
            } else {
              value=this.value?this.value:this.innerHTML;
            }
        if(this.getAttribute("maskingPattern") && (this.getAttribute("maskingPattern").toString()==='currency_rupees' || this.getAttribute("maskingPattern").toString()==='currency_dollar' || this.getAttribute("maskingPattern").toString()==='currency_naira' || this.getAttribute("maskingPattern").toString()==='currency_yen' || this.getAttribute("maskingPattern").toString()==='currency_euro' || this.getAttribute("maskingPattern").toString()==='currency_french' || this.getAttribute("maskingPattern").toString()==='currency_greek' || this.getAttribute("maskingPattern").toString()==='currency_bahamas' || this.getAttribute("maskingPattern").toString()==='percentage'|| this.getAttribute("maskingPattern").toString()==='dgroup2'|| this.getAttribute("maskingPattern").toString()==='dgroup3'|| this.getAttribute("maskingPattern").toString()==='NZP'))
            {
                value =  getControlValue(this);
            }
            if(this.type==='textarea'){
                value=this.value;
                if((value=='' || value==undefined) && (this.classList.contains("richtexteditor"))) //85748
                    value=this.innerHTML;
            }
            if(this.type==='select-one')
                value=this.value===''?'':this.value;
            if(this.type && (this.type==="checkbox" || this.type==="radio"))
                value=this.checked;

            //dataValue[formatJSONValue(this.getAttribute("labelName"))]=formatJSONValue(value);
        });
        var invalidControl;
         for(var j=0;j<elementsArray.length;j++){
             if(elementsArray[j].tagName=='TABLE')
                continue;
            if(!validateColumnValue(elementsArray[j],controlId,false)){
                invalidControl=elementsArray[j];
                break;

            }
        }

        if(invalidControls.length>0){
            document.getElementById("saveAdvancedListviewchanges_"+controlId).removeAttribute("data-dismiss");
            return false;
        }
    //    if(nullElements.length>0){
    //        document.getElementById("savechanges_"+controlId).removeAttribute("data-dismiss");
    //        showMessage("","Null values not allowed in "+nullElements,"error");
    //        return false;
    //    }
    //     if(invalidControl!=undefined || invalidControl!=null){
    //         document.getElementById("savechanges_"+controlId).removeAttribute("data-dismiss");
    //        var validationmsg = document.getElementById(controlId+"_"+invalidControl.getAttribute("labelName")+"_msg").innerHTML;
    //        showMessage(invalidControl,validationmsg +":"+'<strong>'+invalidControl.getAttribute("labelName")+'</strong>',"error");
    //        return false;
    //    } 

		if(isOverlayOpen=="Y"){
            if(window.overrideRestrictOverlay)
            {
                if(overrideRestrictOverlay(controlId)){
                    document.getElementById("saveAdvancedListviewchanges_"+controlId).setAttribute("data-dismiss","modal");
                }
                else {
                    document.getElementById("saveAdvancedListviewchanges_"+controlId).removeAttribute("data-dismiss");
                }
            }else {
                    document.getElementById("saveAdvancedListviewchanges_"+controlId).removeAttribute("data-dismiss");
            }
        }
        else{
            if(window.allowRestrictedOverlay)
            {
                if(allowRestrictedOverlay(controlId)){
                    document.getElementById("saveAdvancedListviewchanges_"+controlId).removeAttribute("data-dismiss");
                }
                else {
                    document.getElementById("saveAdvancedListviewchanges_"+controlId).setAttribute("data-dismiss","modal");
                }
            } else{
                document.getElementById("saveAdvancedListviewchanges_"+controlId).setAttribute("data-dismiss","modal");
            }
        }
	
            dataValue = saveRichTextEditorData('iFrameAdvancedListViewModal',dataValue);
            //document.getElementById("saveAdvancedListviewchanges_"+controlId).setAttribute("data-dismiss","modal");
            var url = "action.jsp";
            var requestString=  "controlId="+encode_utf8(controlId) +"&rowIndex="+rowIndex +"&dataValue=" + encode_utf8(JSON.stringify(dataValue)) + "&modifyFlag=yes";  
            var contentLoaderRef = new net.ContentLoader(url, modifyRowAdvancedlistviewResponseHandler, ajaxFormErrorHandler, "POST", requestString, false);
    }
    function modifyRowAdvancedlistviewResponseHandler(){
        documentIndex="";
        //$("#"+this.req.getResponseHeader("TableId")+ " tbody").append(this.req.responseText);
        var controlId = getQueryVariable(this.params, "controlId");
        var charLimitData=document.getElementById("scrollDiv_"+controlId).previousSibling.getElementsByTagName("th");
        $("#"+controlId).floatThead('reflow');
        var dgroupColumns = this.req.getResponseHeader("dgroupColumns");
        if(dgroupColumns != null && dgroupColumns != undefined){
        for(var i=0;i<dgroupColumns.split(",").length;i++){
            var className = "dgroup_"+controlId+"_"+dgroupColumns.split(",")[i];
            //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);

            $('.'+className).each(function() {
                var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
                var dec = '2';
                if(jQuery(this).attr('typeofvalue')=='Float')
                    dec = jQuery(this).attr('Precision');
                jQuery(this).autoNumeric('init',{
                    dGroup: digitGroup,
                    mDec: dec
                }); 
            });
        }
        }
        $('.listviewlabel').each(function() {
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'savedlabel');
            }

        });
        var dataValue=decode_utf8(this.req.getResponseHeader("modifiedRowData")); //Bug 91541
        var elementToModify=document.getElementsByClassName('advancedListviewrowToModify');
        var rowIndex = $(elementToModify[0]).closest('tr').index();
        try{
            var json=JSON.parse(dataValue);
            var month_array=["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
            $(elementToModify[0].parentNode).children().each(function(i) {
                if(i>0){
                    var labelValue = decode_utf8(json[this.children[0].getAttribute("labelName")]);
                    var charLim="";
                    for(var k=1;k<charLimitData.length;k++)
                    {
                        if(charLimitData[k].innerHTML==this.children[0].getAttribute("labelName"))
                        {
                            if(charLimitData[k].getAttribute("charlimit")!=undefined)
                                charLim=charLimitData[k].getAttribute("charlimit");
                            break;
                        }
                    }
                    if(charLim!=null&&charLim!=""&&labelValue.length>charLim)
                        labelValue=labelValue.substring(0,charLim)+"..."; 
                    if(labelValue!=null&&labelValue!=undefined&&labelValue!="undefined")  //Bug 91729
                        this.children[0].innerHTML=labelValue;
                    if(this.children[0].getAttribute('maskingpattern')!=undefined&&this.children[0].getAttribute('maskingpattern')!="undefined"&&this.children[0].getAttribute('maskingpattern')!="nomasking")
                        applyMaskingValue(this.children[0],labelValue);
                }
            });
            //elementToModify[0].children[0].innerHTML=json[elementToModify[0].children[0].getAttribute("labelName")];
            if(window.isSaveRowAfterModifyCall){
                if(!isSaveRowAfterModifyCall(controlId)){
                    elementToModify[0].classList.remove("advancedListviewrowToModify");
                    document.getElementById("saveAdvancedListviewchanges_"+controlId).setAttribute("data-dismiss","modal");
                }else{
                    chkAndApplyOverlayINI();
                }
            }
            else{
                chkAndApplyOverlayINI();
            }
       
            $('.listviewlabel').each(function() {
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'label');
            }

        });
            var totalValueElements=document.getElementById('totallabel_'+controlId).innerHTML.split(",!,");
            for(var i=0;i<totalValueElements.length;i++){
                if(totalValueElements[i]!=''){
                    $(document.getElementsByClassName(totalValueElements[i].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
                    var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
                    if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
                        || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
                        {
                    maskfield(this,'label');
                    }
                });
                }
                showTotal('',totalValueElements[i]);
            }
            setTableModifiedFlag(controlId);
            reshuffleIndices(controlId);
           
        }
        catch(ex){}
    }
	
	function chkAndApplyOverlayINI()
    {
        if(isOverlayOpen != "Y"){
            if(window.allowRestrictedOverlay)
            {
                if(!allowRestrictedOverlay(controlId)){
                    elementToModify[0].classList.remove("advancedListviewrowToModify");
                    document.getElementsByClassName('selectedAdvancedListviewRow')[0].classList.remove("selectedAdvancedListviewRow");
                }
            } else{
                elementToModify[0].classList.remove("advancedListviewrowToModify");
                document.getElementsByClassName('selectedAdvancedListviewRow')[0].classList.remove("selectedAdvancedListviewRow");
            } 
        }
    }
    
    function  removerowToModify(){
        var elementToModify=document.getElementsByClassName('rowToModify');
        if(elementToModify){
            for(var i=0;i<elementToModify.length;i++){
                elementToModify[i].classList.remove("rowToModify");
            }
        }
        elementToModify=document.getElementsByClassName('selectedRow');
        if(elementToModify){
            for(var i=0;i<elementToModify.length;i++){
                elementToModify[i].classList.remove("selectedRow");
            }
        }
    }

    function  removeAdvancedListviewrowToModify(){
        var elementToModify=document.getElementsByClassName('advancedListviewrowToModify');
        if(elementToModify){
            for(var i=0;i<elementToModify.length;i++){
                elementToModify[i].classList.remove("advancedListviewrowToModify");
            }
        }
        elementToModify=document.getElementsByClassName('selectedAdvancedListviewRow');
        if(elementToModify){
            for(var i=0;i<elementToModify.length;i++){
                elementToModify[i].classList.remove("selectedAdvancedListviewRow");
            }
        }
    }
    function showNextPreviousResultAdvancedListview(controlId,operation){
        try{
        var rowIndex = parseInt(document.getElementById("advancedListviewRowCount").value);
        var elementToModify=document.getElementsByClassName('advancedListviewrowToModify');
        var nextPrevVisibleRowIndex = getNextPreviousVisibleRowIndex(controlId,rowIndex,operation);
        rowIndex = nextPrevVisibleRowIndex;
        var tableRef = document.getElementById(controlId);
        var row = tableRef.tBodies[0].getElementsByTagName("tr")[rowIndex];
        if(row!=undefined && row.getElementsByTagName("td")!=undefined)
            row.getElementsByTagName("td")[1].classList.add('advancedListviewrowToModify');
        if(operation==="next"){
//            $(elementToModify[0]).closest('tr').next().find('td')[1].classList.add('advancedListviewrowToModify');
//            rowIndex=rowIndex+1;
            elementToModify[0].classList.remove('advancedListviewrowToModify');
        }
        else{
//            rowIndex=rowIndex-1;
//            $(elementToModify[0]).closest('tr').prev().find('td')[1].classList.add('advancedListviewrowToModify');
            elementToModify[1].classList.remove('advancedListviewrowToModify');
        }
        document.getElementsByClassName('selectedAdvancedListviewRow')[0].classList.remove("selectedAdvancedListviewRow");
//        $(document.getElementsByClassName('advancedListviewrowToModify')[0]).closest('tr')[0].classList.add("selectedAdvancedListviewRow");
        row.classList.add("selectedAdvancedListviewRow");
        var reqString="&controlId="+encode_utf8(controlId) +"&EventType="+"click"+"&tabledata=yes&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&RowId="+rowId+"&modifyFlag=yes"+"&rowIndex="+rowIndex+"&Operation="+operation;
        openAdvancedListViewModel(controlId,'click',reqString);
        document.getElementById("advancedListviewRowCount").value=rowIndex;
        enableDisableNextPreviousButtonAdvancedListview(controlId,rowIndex);
        disableAdvancedListViewControls(controlId);
        if(window.nextPreviousAdvancedListviewPosthook){
            nextPreviousAdvancedListviewPosthook(controlId,rowIndex,operation);
        }
    }
    catch(ex)
    {
        
    }
    }
    function enableDisableNextPreviousButtonAdvancedListview(controlId,rowIndex){
        var isNextRowVisible=isNextPrevRowVisible(controlId,rowIndex,"next");
        var isPrevRowVisible=isNextPrevRowVisible(controlId,rowIndex,"prev");
        var ref=document.getElementsByClassName('advancedListviewrowToModify')[0];
        if(isNextRowVisible)
            if(document.getElementById("AdvancedListviewlistNext")!=null)
                document.getElementById("AdvancedListviewlistNext").disabled= false;
        else
            if(document.getElementById("AdvancedListviewlistNext")!=null)
                document.getElementById("AdvancedListviewlistNext").disabled= true;
        if(isPrevRowVisible)
            if(document.getElementById("AdvancedListviewlistPrevious")!=null)
                document.getElementById("AdvancedListviewlistPrevious").disabled= false;
        else
            if(document.getElementById("AdvancedListviewlistPrevious")!=null)
                document.getElementById("AdvancedListviewlistPrevious").disabled= true;
    }

    function showNextPreviousResultTable(controlId,operation){
        try{
        var rowIndex = parseInt(document.getElementById("rowCount").value);
        var elementToModify=document.getElementsByClassName('rowToModify');
        if(window.nextPreviousListviewPrehook){
            nextPreviousListviewPrehook(controlId,rowIndex,operation);
        }
        var nextPrevVisibleRowIndex = getNextPreviousVisibleRowIndex(controlId,rowIndex,operation);
        rowIndex = nextPrevVisibleRowIndex;
        var tableRef = document.getElementById(controlId);
        var row = tableRef.tBodies[0].getElementsByTagName("tr")[rowIndex];
        if(row!=undefined && row.getElementsByTagName("td")!=undefined)
            row.getElementsByTagName("td")[1].classList.add('rowToModify');
        if(operation==="next"){
//            $(elementToModify[0]).closest('tr').next().find('td')[1].classList.add('rowToModify');
//            rowIndex=rowIndex+1;
            elementToModify[0].classList.remove('rowToModify');
        }
        else{
//            rowIndex=rowIndex-1;
//            $(elementToModify[0]).closest('tr').prev().find('td')[1].classList.add('rowToModify');
            elementToModify[1].classList.remove('rowToModify');
        }
        document.getElementsByClassName('selectedRow')[0].classList.remove("selectedRow");
//        $(document.getElementsByClassName('rowToModify')[0]).closest('tr')[0].classList.add("selectedRow");
        row.classList.add("selectedRow");
        var reqString="&controlId="+encode_utf8(controlId) +"&EventType="+"click"+"&tabledata=yes&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&RowId="+rowId+"&modifyFlag=yes"+"&rowIndex="+rowIndex+"&Operation="+operation;
        openListViewModel(controlId,'click',reqString);
        document.getElementById("rowCount").value=rowIndex;
        enableDisableNextPreviiousButton(controlId,rowIndex);
        disableListViewControls(controlId);
        var lineNo=rowIndex+1;
        if(autoIncrementLabelDisplay=='true')
        {
            var value=document.getElementsByClassName('listViewHeader')[0].innerHTML;
            value='{'+lineNo+'}'+value;
            document.getElementsByClassName('listViewHeader')[0].innerHTML=value;
        }
    }
    catch(ex){}
    }
    function getNextPreviousVisibleRowIndex(tableId,currentRowIndex,operation){
        var rowIndex=-1;
        var table = document.getElementById(tableId);
        var rows = table.tBodies[0].getElementsByTagName("tr");
        if(operation=="next"){
            for(var i=currentRowIndex+1;i<=rows.length-1;i++){
            if(rows[i]!=undefined && rows[i].style.display!="none"){
                rowIndex=i;
                break;
                }
            }
        }
        else{
            for(var i=currentRowIndex-1;i>=0;i--){
            if(rows[i]!=undefined && rows[i].style.display!="none"){
                rowIndex=i;
                break;
                }
            }
        }
        return rowIndex;
    }
    
    function isNextPrevRowVisible(controlId,rowIndex,operation){
        var isVisible=false;
        var table = document.getElementById(controlId);
        var rows = table.tBodies[0].getElementsByTagName("tr");
        if(operation=="next"){
            for(var i=rowIndex+1;i<=rows.length-1;i++){
            if(rows[i]!=undefined && rows[i].style.display!="none"){
               isVisible=true;
               break;
                }
            }
        }
        else{
            for(var i=rowIndex-1;i>=0;i--){
            if(rows[i]!=undefined && rows[i].style.display!="none"){
               isVisible=true;
                break;
                }
            }
        }
        return isVisible;
    }
    
    function enableDisableNextPreviiousButton(controlId,rowIndex){
        var ref=document.getElementsByClassName('rowToModify')[0];
        var isNextRowVisible=isNextPrevRowVisible(controlId,rowIndex,"next");
        var isPrevRowVisible=isNextPrevRowVisible(controlId,rowIndex,"prev");
        if(isNextRowVisible){
            if(document.getElementById("tablelistNext")!=null)
              document.getElementById("tablelistNext").disabled= false;
        } else {
            if(document.getElementById("tablelistNext")!=null)
              document.getElementById("tablelistNext").disabled= true;
        }    
        if(isPrevRowVisible){
            if(document.getElementById("tablelistPrevious")!=null)
            document.getElementById("tablelistPrevious").disabled= false;
            
        } else {
            if(document.getElementById("tablelistPrevious")!=null)
               document.getElementById("tablelistPrevious").disabled= true;
        }    
    }
    function listviewResponseHandler(){
         var controlId = getQueryVariable(this.params, "controlId");
         var rowIndex = getQueryVariable(this.params, "rowIndex");
         var colIndex = getQueryVariable(this.params, "colIndex");
         var serverValidationJSON = null;
         if(this.req.getResponseHeader("validationJSON")!=null){
             serverValidationJSON = JSON.parse(decode_utf8(this.req.getResponseHeader("validationJSON")));
         }
         
         if(isServerValidation=="true" && (serverValidationJSON!=null||serverValidationJSON!=undefined)){
             if(serverValidationJSON["serverValidation"]=="false"){
                    var tableJSON = {};
                    tableJSON["tableName"]=serverValidationJSON["tableName"];
                     tableJSON["tableId"]=controlId;
                    tableJSON["rowIndex"]=rowIndex;
                    tableJSON["columnName"]=serverValidationJSON["controlName"];
                    tableJSON["cellData"]=serverValidationJSON["controlValue"];
                    if(!cellControlExistsInMap(controlId, rowIndex, serverValidationJSON["controlName"],serverValidationJSON["controlValue"])){
                        tableComponentMap[controlId+"_"+rowIndex+"_"+colIndex]= tableJSON;
                    }
                    showMessage("",THE_VALUE+" : "+serverValidationJSON["controlValue"]+INCOMPATIBLE_MSG+" "+ROW+" : "+rowIndex+","+COLUMN+" : "+serverValidationJSON["controlName"] +OF+" "+TABLE+" "+serverValidationJSON["tableName"] ,"error");
                    return;
                }
         }
         else{
             delete tableComponentMap[controlId+"_"+rowIndex+"_"+colIndex];
         }
        $("#"+controlId+ " tbody").append(this.req.responseText);
        $("#"+controlId).floatThead('reflow');
        var dgroupColumns = this.req.getResponseHeader("dgroupColumns");
        for(var i=0;i<dgroupColumns.split(",").length;i++){
            var className = "dgroup_"+controlId+"_"+dgroupColumns.split(",")[i];
            //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);

            $('.'+className).each(function() {
                var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
                var dec = '2';
                if(jQuery(this).attr('typeofvalue')=='Float')
                    dec = jQuery(this).attr('Precision');
                jQuery(this).autoNumeric('init',{
                    dGroup: digitGroup,
                    mDec: dec
                }); 
            });
        }
        $('.listviewlabel').each(function() {
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'savedlabel');
            }

        });



    //    for(var i=0;i<maskedColumns.split(",").length;i++){
    //        var maskedClass = controlId+"_"+maskedColumns.split(",")[i];
    //        //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);
    //        
    //        $('.'+maskedClass).each(function() {
    //            var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
    //            jQuery(this).autoNumeric('init',{
    //                dGroup: digitGroup,
    //                mDec: '0'
    //            }); 
    //        });
    //    }

    }

function cellControlExistsInMap(tableId,rowIndex,columnName,cellData){
    for(var i =0;i<tableComponentMap.length;i++){
        if(tableComponentMap["tableId"]==tableId && tableComponentMap["rowIndex"]==rowIndex && tableComponentMap["columnName"]==columnName){
            if(tableComponentMap["cellData"]!=cellData){
                tableComponentMap[i]["cellData"]=cellData;
            }
            return true;
        }
    }
    return false;
}

function cellControlExistsInMap(tableId,rowIndex,columnName,cellData){
    for(var i =0;i<tableComponentMap.length;i++){
        if(tableComponentMap["tableId"]==tableId && tableComponentMap["rowIndex"]==rowIndex && tableComponentMap["columnName"]==columnName){
            if(tableComponentMap["cellData"]!=cellData){
                tableComponentMap[i]["cellData"]=cellData;
            }
            return true;
        }
    }
    return false;
}
    function modifyRowlistviewResponseHandler(){
        documentIndex="";
        var controlId = getQueryVariable(this.params, "controlId");
//        document.getElementById(controlId).tBodies[0].innerHTML = document.getElementById(controlId).tBodies[0].innerHTML + this.req.responseText;
        $("#"+controlId+ " tbody").append(this.req.responseText);
        $("#"+controlId).floatThead('reflow');
        
        var dgroupColumns = this.req.getResponseHeader("dgroupColumns");
        for(var i=0;i<dgroupColumns.split(",").length;i++){
            var className = "dgroup_"+controlId+"_"+dgroupColumns.split(",")[i];
            //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);

            $('.'+className).each(function() {
                var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
                var dec = '2';
                if(jQuery(this).attr('typeofvalue')=='Float')
                    dec = jQuery(this).attr('Precision');
                jQuery(this).autoNumeric('init',{
                    dGroup: digitGroup,
                    mDec: dec
                }); 
            });
        }
        $('.listviewlabel').each(function() {
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'savedlabel');
            }

        });




    //    for(var i=0;i<maskedColumns.split(",").length;i++){
    //        var maskedClass = controlId+"_"+maskedColumns.split(",")[i];
    //        //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);
    //        
    //        $('.'+maskedClass).each(function() {
    //            var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
    //            jQuery(this).autoNumeric('init',{
    //                dGroup: digitGroup,
    //                mDec: '0'
    //            }); 
    //        });
    //    }

    }

    function addRowlistviewResponseHandler(){
        documentIndex="";
        if(document.getElementById("advancedListViewModal")!=null && document.getElementById("advancedListViewModal").className==="modal in")
        {
            isAdvanceListView=true;
        }else {
            isAdvanceListView=false;
        }
        var modalControl = document.getElementById("listViewModal");
        var controlId = getQueryVariable(this.params, "controlId");
        var tableControl = document.getElementById(controlId);
        var batchCounter=this.req.getResponseHeader("batchCounter");
        var crossState=document.getElementById("closeButton").getAttribute("state");
//        document.getElementById(controlId).tBodies[0].innerHTML = document.getElementById(controlId).tBodies[0].innerHTML + this.req.responseText;
        //$("#"+controlId+ " tbody").append(this.req.responseText);
        $(document.getElementById(controlId)).find("tbody").append(this.req.responseText);
        $("#"+controlId).floatThead('reflow');
        var totalValueElements=document.getElementById('totallabel_'+controlId).innerHTML.split(",!,");
            for(var i=0;i<totalValueElements.length;i++){
             //var controlRef = document.getElementById('label'+'_'+controlId+'_'+maskedLabels.split(",")[i]);
             if(totalValueElements[i]!=''){
             $(document.getElementsByClassName(totalValueElements[i].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
                 
                var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
                if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
                    || ((typeofvalue=='Float'||typeofvalue=='Integer'||typeofvalue=='Long') && this.getAttribute("maskingpattern")=="nomasking"))
                    {
                    maskfield(this,'label');
                }
         });
             }
                showTotal('',totalValueElements[i]);
            }
        var dgroupColumns = this.req.getResponseHeader("dgroupColumns");
        if(dgroupColumns!=null && dgroupColumns!=undefined){
        for(var i=0;i<dgroupColumns.split(",").length;i++){
            var className = "dgroup_"+controlId+"_"+dgroupColumns.split(",")[i];
            //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);

            $('.'+className).each(function() {
                var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
                var dec = '2';
                if(jQuery(this).attr('typeofvalue')==='Float')
                    dec = jQuery(this).attr('Precision');
                if(!isNaN(digitGroup)){
                    jQuery(this).autoNumeric('init',{
                        dGroup: digitGroup,
                        mDec: dec
                    }); 
                }
            });
        }
        }
        $('.listviewlabel').each(function() {
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || ((typeofvalue=='Float'||typeofvalue=='Integer'||typeofvalue=='Long') && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'savedlabel');
            }

        });
         $('.tabletextbox').each(function() {
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'input');
            }

        });
        
        $('.openPickerClass').each(function()
        {
            if(this.getAttribute("maskingPattern")!=null && this.getAttribute("maskingPattern")!=undefined && this.getAttribute("maskingPattern")!="" )
            {
                maskfield(this,'input');
            }
        });
        initFloatingMessagesForTableCells();
        checkTableHeight(controlId);
        reshuffleIndices(controlId,"",batchCounter);
        if(CleanMapOnCloseModal=="Y"){
            if(window.addRowPostHook)
            {
                addRowPostHook(controlId);
            }
        }
        else{
            if(!isAdvanceListView || modalControl.style.display == 'block' || tableControl.getAttribute("type")=='Table'){
            if(window.addRowPostHook)
                {
                addRowPostHook(controlId);
            }
            }
        }
        if(isAdvanceNext==true && isAdvanceListView==true)
            clearAdvancedListviewMap("A",crossState);



    //    for(var i=0;i<maskedColumns.split(",").length;i++){
    //        var maskedClass = controlId+"_"+maskedColumns.split(",")[i];
    //        //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);
    //        
    //        $('.'+maskedClass).each(function() {
    //            var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
    //            jQuery(this).autoNumeric('init',{
    //                dGroup: digitGroup,
    //                mDec: '0'
    //            }); 
    //        });
    //    }

    }

    function attachDatePicker(){
        //    var actualDateFormat = dateFormat;
        //    var newDateFormat = '';
        //                
        //    if(actualDateFormat == 'dd/MMM/yyyy'){
        //        newDateFormat = 'dd/M/yyyy';
        //    }else if(actualDateFormat == 'dd/MM/yyyy'){
        //        newDateFormat = 'dd/mm/yyyy';
        //    }                
        //    else{
        //        newDateFormat = 'dd/M/yyyy';
        //    }



        //    $('.richtexteditor').each(function() {
        //        $( this ).Editor();
        //    });
        //Bug 100042
        var dfvalue,dsvalue;
       // $(document).ready(function(){
            if(jQuery("input[datatype='date']").length>0){

                dfvalue = jQuery("input[datatype='date']").attr("dateformat").split("_")[0];
                dsvalue =  jQuery("input[datatype='date']").attr("dateformat").split("_")[1];
                timeFlag =  jQuery("input[datatype='date']").attr("dateformat").split("_")[2]
            }
            else{
                dfvalue = jQuery("input[controltype='date']").attr('dateformat');
                dsvalue = jQuery("input[controltype='date']").attr('dateseparator');
            }

       // })
        var df="",ds="";
        if(dsvalue=="1")
            ds="/"
        else if(dsvalue=="2")
            ds="-"
        else if(dsvalue=="3")
            ds="."

        if(dfvalue=="1")
            df="dd"+ds+"mm"+ds+"yyyy"
        else if(dfvalue=="2")
            df="mm"+ds+"dd"+ds+"yyyy"
        else if(dfvalue=="3")
            df="yyyy"+ds+"mm"+ds+"dd"
        else if(dfvalue=="4")
            df="dd"+ds+"mmm"+ds+"yyyy"
        dateFormat = dfvalue;
        dateSeparator = ds;
        var format1 = df;
        var format11=df.toUpperCase(),format12=df.toUpperCase() +  " HH:mm:ss";
        var format21="",format22="";
        var format21="",format22="";
        if(df=="dd"+ds+"mm"+ds+"yyyy")
            format21 = "d"+ds+"m"+ds+"Y";
        else if(df=="mm"+ds+"dd"+ds+"yyyy")
            format21 = "m"+ds+"d"+ds+"Y";
        else if(df=="yyyy"+ds+"mm"+ds+"dd")
            format21 = "Y"+ds+"m"+ds+"d";
        else if(df=="dd"+ds+"mmm"+ds+"yyyy"){
            format21 = "d"+ds+"M"+ds+"Y";
            format1 = "dd"+ds+"M"+ds+"yyyy";
        }

        format22 = format21 + " H:i:s";

        $(".mydatepicker1").datepicker(
        {
            autoclose: true, 
            format: format1, 
            todayHighlight: true,
            useCurrent:false,//Bug 82142
            disableTouchKeyboard: true,
            clearBtn: true,
            beforeShow: function(input, inst) { 
                inst.dpDiv.css({
                    "z-index":1002
                });
            }
        });

        //    $(".mydatepicker1").datepicker(
        //    {
        //        autoclose: true, 
        ////        format: newDateFormat,
        //        format: df,
        //        todayHighlight: true, 
        //        disableTouchKeyboard: true,
        //        clearBtn: true,
        //        beforeShow: function(input, inst) { 
        //        inst.dpDiv.css({"z-index":1001});
        //    }
        //    });

        $(".mydatepicker").datetimepicker1({
            //                format: 'DD/MM/YYYY'
            format: format11,
            ignoreReadonly:true,
            useCurrent:false//Bug 82142
        });
        $(".mydatetimepicker").datetimepicker1({
            format: format12,
            ignoreReadonly:true,
            useCurrent:false//Bug 82142
        });



        try{    
            jQuery('.myjquerydatepicker').each(
                function () {
                    if( this.getAttribute("xd") !== "true"){
                    jQuery(this).datetimepicker({
                        i18n: {
                            de: {
                                months: [
                                    'Januar', 'Februar', 'März', 'April',
                                    'Mai', 'Juni', 'Juli', 'August',
                                    'September', 'Oktober', 'November', 'Dezember',
                                ],
                                dayOfWeek: [
                                    "So.", "Mo", "Di", "Mi",
                                    "Do", "Fr", "Sa.",
                                ]
                            }
                        },
                        timepicker: false,
                        format: format21,
                        scrollMonth: false
                                //         format:'d.m.Y'
                    }

                    )
                    this.setAttribute("xd","true");
                    }
                });
            
            
            
	jQuery('.myjquerydatetimepicker').each(
            function () {
                if( this.getAttribute("xd") !== "true"){
                    jQuery(this).datetimepicker({
                        format: format22,
                        scrollMonth: false
                    }

                    )
                    this.setAttribute("xd","true");
                }
            });
        }
        catch(ex){}

        //Bug 59177 - Datepicker >> If checked mandatory checkbox, irrelevant information is showing in textbox
        datepickerinitialised = true;
        document.getElementById("fade").style.display="none";
    }


    function attachDateRange(ref){
        var currentTime=new Date().getHours()+":"+new Date().getMinutes()+":"+new Date().getSeconds();
        var currentDay = new Date().getDate();
        if($(ref).attr('format')!=undefined && $(ref).attr('format')!=""){
            if($(ref).attr('mindate')!=""&&$(ref).attr('mindate')!==undefined){
                var minDate = moment($(ref).attr('mindate'), $(ref).attr('format').toUpperCase()).format("DD/MM/YYYY");
                if($(ref).attr('format').toUpperCase()=="MM/DD/YYYY"){
                    minDate=$(ref).attr('minDate');
                }
            }
            if($(ref).attr('maxdate')!=""&&$(ref).attr('maxdate')!==undefined){
                var maxDate = moment($(ref).attr('maxdate'), $(ref).attr('format').toUpperCase()).format("DD/MM/YYYY");
                    var maxDateTime=maxDate+" "+currentTime;
                    if($(ref).attr('format').toUpperCase()=="YYYY/MM/DD"){
                        maxDateTime=$(ref).attr('maxdate')+" "+currentTime;
                    }
            }
        }
        
        //jQuery.datetimepicker.setLocale((window.navigator.language).split('-')[0]);
        if(currentDay.toString().length==1)
            currentDay = "0"+currentDay;
        if(((new Date().getMonth()+1).toString()).length > 1)
        var currentDate=currentDay+ "/" +(new Date().getMonth()+1)+ "/"+new Date().getFullYear();
        else
            var currentDate=currentDay+ "/" +("0" + (new Date().getMonth()+1).toString())+ "/"+new Date().getFullYear();
        if(!$(ref).hasClass('myjquerydatepicker') && !$(ref).hasClass('myjquerydatetimepicker')){
            if(minDate!=""&&minDate!==undefined)
                $(ref).data("DateTimePicker").minDate(moment(minDate,'DD/MM/YYYY'));
            if(maxDate!=""&&maxDate!==undefined){
                if(maxDate==currentDate){
                    $(ref).data("DateTimePicker").maxDate(moment(maxDateTime,'DD/MM/YYYY HH:mm:ss')); 
                }else
                    $(ref).data("DateTimePicker").maxDate(moment($(ref).attr('maxdate')+" 23:59:59",'DD/MM/YYYY HH:mm:ss')); 
            }
        }
        else{//Bug 76754 Start        
            if(!$(ref).attr('mindate')==''&&!$(ref).attr('maxdate')==''){
                var maxTime=false;
                var selectedDate=$(ref).val().split(" ")[0];           
                if($(ref).attr('maxdate')==currentDate){
                    if(currentDate==selectedDate || selectedDate=="")
                        maxTime=new Date().getTime();              
                }              
                $(ref).datetimepicker({
                    formatDate:'d/m/y',
                    minDate:$(ref).attr('mindate'),
                    maxDate:$(ref).attr('maxdate'),
                    useCurrent:false,//Bug 82142
                    maxTime:maxTime,
                    onSelectDate:function(a,b){
                          if($(ref).attr('maxdate')==currentDate)
                          {
                            var aDay =  a.getDate();
                            if(aDay.toString().length==1)
                                aDay = "0"+aDay;
                            if(((a.getMonth()+1).toString()).length > 1)  
                            var selectedDate = aDay + "/" + (a.getMonth()+1) +"/" + a.getFullYear();
                            else
                                var selectedDate = aDay + "/" + ("0"+(a.getMonth()+1).toString()) +"/" + a.getFullYear();
                            if(selectedDate=="" || selectedDate==currentDate){                        
                                this.setOptions({                       
                                maxTime:new Date().getTime()                                      
                             });    
                              if(currentDate==selectedDate){
                                    var selectedTime=$(ref).val().split(" ")[1];    
                                    if(selectedTime>currentTime)
                                       $(ref).val(currentDate +" "+new Date().getHours()+":00:00");
                              }
                            } else{
                                this.setOptions({
                                maxTime:false
                             });
                            }    
                          }
                     },
                     onChangeDateTime:function(a,b){
                        if($(b).val()!=currentJQueryDatePickerValue)
                            $(b).trigger("change");
                        currentJQueryDatePickerValue=$(b).val();

                    },
                    onShow:function(a,b){
                        currentJQueryDatePickerValue=$(b).val();
                    }                     
                });
            }
            else if($(ref).attr('mindate')==''&&!$(ref).attr('maxdate')==''){
                var maxTime=false;
                var selectedDate=$(ref).val().split(" ")[0];           
                if($(ref).attr('maxdate')==currentDate){
                    if(currentDate==selectedDate || selectedDate=="")
                        maxTime=new Date().getTime();
                }
                $(ref).datetimepicker({
                    formatDate:'d/m/y',
                    minDate:false,//Bug 81230
                    maxDate:$(ref).attr('maxdate'),   
                    maxTime:maxTime,
                    useCurrent:false,//Bug 82142
                    onSelectDate:function(a,b){
                        var aDay =  a.getDate();
                    if(aDay.toString().length==1)
                        aDay = "0"+aDay;
                       if(((a.getMonth()+1).toString()).length > 1) 
                       var selectedDate = aDay + "/" + (a.getMonth()+1) +"/" + a.getFullYear();
                       else
                            var selectedDate = aDay + "/" + ("0" + (a.getMonth()+1).toString()) +"/" + a.getFullYear();
                       if(selectedDate=="" || selectedDate==currentDate){                       
                            this.setOptions({                       
                            maxTime:new Date().getTime()                        
                         });                  
                        } else{
                            this.setOptions({
                            maxTime:false
                         });
                        }      
                     },
                     onChangeDateTime:function(a,b){
                        if($(b).val()!=currentJQueryDatePickerValue)
                            $(b).trigger("change");
                        currentJQueryDatePickerValue=$(b).val();

                    },
                    onShow:function(a,b){
                        currentJQueryDatePickerValue=$(b).val();

                    }
                });
            }
            else if(!$(ref).attr('mindate')==''&&$(ref).attr('maxdate')==''){
                $(ref).datetimepicker({
                    formatDate:'d/m/y',
                    maxDate:false,//Bug 81230
                    minDate:$(ref).attr('mindate'),
                    useCurrent:false,//Bug 82142
                     onChangeDateTime:function(a,b){
                        if($(b).val()!=currentJQueryDatePickerValue)
                            $(b).trigger("change");
                        currentJQueryDatePickerValue=$(b).val();
                    },
                    onShow:function(a,b){
                        currentJQueryDatePickerValue=$(b).val();
                    }
                });
            }
            else{
                $(ref).datetimepicker({
                    formatDate:'d/m/y',
                    minDate:false,//Bug 81230
                    maxDate:false,//Bug 81230
                    useCurrent:false,//Bug 82142
                    onChangeDateTime:function(a,b){
                        if($(b).val()!=currentJQueryDatePickerValue)
                            $(b).trigger("change");
                        currentJQueryDatePickerValue=$(b).val();

                    },
                    onShow:function(a,b){
                        currentJQueryDatePickerValue=$(b).val();
                    }
                });
            }
            //Bug 76754 End
        }
    }

    function disablePrevious(isDoubleClick){
        var ctrlId;
    try{
         if(isDoubleClick==null || isDoubleClick == undefined){
            getContentWindow('iFrameSearchModal').getElementById("fetchedData").parentNode.innerHTML=""; //Bug 86671 
         }
         else{
             window.parent.getContentWindow('iFrameSearchModal').getElementById("fetchedData").parentNode.innerHTML="";
         }
    }catch(ex){}
        if(isDoubleClick==null || isDoubleClick == undefined){
        document.getElementById("picklistPrevious").disabled = true;
        document.getElementById("picklistNext").disabled= false;
        ctrlId = getContentWindow('iFrameSearchModal').getElementById("controlId").value;
    }
    else{
        window.parent.document.getElementById("picklistPrevious").disabled = true;
        window.parent.document.getElementById("picklistNext").disabled= false;
        ctrlId = window.parent.getContentWindow('iFrameSearchModal').getElementById("controlId").value;
        window.parent.$("#searchModal").modal('toggle');
    }
        
    if((isDoubleClick==null || isDoubleClick == undefined)&& window.postHookPickListCancel ){
        return postHookPickListCancel(ctrlId);
    }
    }

    function showNextPreviousResult(from)
    {
        var contrlid,batchsize,isModal,searchString,columnName;
        try
        {
            contrlid = window.frames["iFrameSearchModal"].contentWindow.document.getElementById("controlId").value;
            batchsize= window.frames["iFrameSearchModal"].contentWindow.document.getElementById("batchSize").value;
            isModal=window.frames["iFrameSearchModal"].contentWindow.document.getElementById("isModal").value;
            searchString=window.frames["iFrameSearchModal"].contentWindow.document.getElementById("searchBox").value;
            columnName=window.frames["iFrameSearchModal"].contentWindow.document.getElementById("selectedColumn").options[window.frames["iFrameSearchModal"].contentWindow.document.getElementById("selectedColumn").selectedIndex].value;
        }
        catch(ex){
            contrlid = window.frames["iFrameSearchModal"].document.getElementById("controlId").value;
            batchsize= window.frames["iFrameSearchModal"].document.getElementById("batchSize").value;
            isModal= window.frames["iFrameSearchModal"].document.getElementById("isModal").value;
            searchString=window.frames["iFrameSearchModal"].document.getElementById("searchBox").value;
            columnName=window.frames["iFrameSearchModal"].document.getElementById("selectedColumn").options[window.frames["iFrameSearchModal"].document.getElementById("selectedColumn").selectedIndex].value;
        }
        var url = "action.jsp";
        requestString=  "controlId="+contrlid +"&from="+from+"&isListModal="+isModal+"&searchString="+encodeURIComponent(searchString)+"&columnName="+encodeURIComponent(columnName);               
        var contentLoaderRef = new net.ContentLoader(url, picklistHandler, picklisterrorHandler, "POST", requestString, true);

    }

    function picklistHandler(){
        try
        {
            if(this.req.getResponseHeader("Next")=="false"){
                document.getElementById("picklistNext").disabled= true;
            }
            else if(this.req.getResponseHeader("Next")=="true"){
                document.getElementById("picklistNext").disabled= false;
            }
            if(this.req.getResponseHeader("Previous")=="false"){
                document.getElementById("picklistPrevious").disabled = true;
            }else if(this.req.getResponseHeader("Previous")=="true"){
                document.getElementById("picklistPrevious").disabled= false;
            }
            //Bug 83107 Start
            $(window.frames["iFrameSearchModal"].contentWindow.document.getElementById("myTable")).find("tbody").html(this.req.responseText);
            if(this.req.getResponseHeader("pickListWidthMapSize")==null ||this.req.getResponseHeader("pickListWidthMapSize")==undefined ||this.req.getResponseHeader("pickListWidthMapSize")=="0")
                $("#myTable").floatThead('reflow');
            showSelectedRow();
            //Bug 83107 End
            //window.frames["iFrameSearchModal"].contentWindow.document.getElementById("myTable").innerHTML = this.req.responseText;
        }
        catch(ex){
            //Bug 83107 Start
            $(window.frames["iFrameSearchModal"].document.getElementById("myTable")).find("tbody").html(this.req.responseText);
            $("#myTable").floatThead('reflow');
            showSelectedRow();
            //Bug 83107 End
            //window.frames["iFrameSearchModal"].document.getElementById("fetchedData").innerHTML = this.req.responseText;
        }

    }

    function picklisterrorHandler(){

    }

    function getControlValue(element)
    {
        if(element.className.search("tile")>=0)
            return encode_ParamValue($(element).attr("value"));

        switch(element.type) {
            case "tile":return encode_ParamValue(element.getAttribute("value"));
            case "range":return encode_ParamValue(element.getAttribute("value"));
            case "textarea":
            case "email":
            case "text":
            {
                    if(element.getAttribute("datatype") == "combobox"){//Bug 83221 Start
                        var ele = element.parentNode.getElementsByClassName("es-visible");
                        var listItems = element.parentNode.getElementsByTagName("li");
                        if (ele != null && ele.length > 0) {
//                        if (ele[0].getAttribute("value") == null || ele[0].getAttribute("value") == undefined)
//                            return "";
                        for( var p=0; p < listItems.length; p++ ) {
                            var option = listItems[p].textContent;
                            if (element.value == option){
                                if($(element).attr('svt')!=null){ //Bug 99635
                                    if($(element).attr('svt')==='V' || $(element).attr('svt')==='') {
                                       return listItems[p].getAttribute("originalValue");      
                                    } else if($(element).attr('svt')==='L'){
                                        return listItems[p].textContent;
                                    } 
                                } else {
                                  return listItems[p].getAttribute("originalValue");
                               }
                                
                            }
                            //return encode_ParamValue(listItems[p].getAttribute("originalValue"));
                        }
                        return element.value;
                        }else{
                            return (element.value == "Select" ? "" : element.value); // Bug 91924
                        }
                    }else{
                    //Bug 83221 End
                    if (element.getAttribute("maskingPattern") != null && element.getAttribute("maskingPattern") != undefined && element.getAttribute("maskingPattern") != '' && element.getAttribute("maskingPattern") != 'nomasking' && element.getAttribute("maskingPattern") != 'email') {
                        if(element.getAttribute("formattedvalue") != null && element.getAttribute("formattedvalue") != undefined && element.getAttribute("formattedvalue") == 'Y'){
                            return element.value ;
                        }
                        else if (element.getAttribute("maskingPattern").toString() === 'currency_rupees' || element.getAttribute("maskingPattern").toString() === 'currency_dollar' || element.getAttribute("maskingPattern").toString() === 'currency_naira' || element.getAttribute("maskingPattern").toString() === 'currency_yen' || element.getAttribute("maskingPattern").toString() === 'currency_euro' || element.getAttribute("maskingPattern").toString() === 'currency_french' || element.getAttribute("maskingPattern").toString() === 'currency_greek' || element.getAttribute("maskingPattern").toString() === 'currency_bahamas' || element.getAttribute("maskingPattern").toString() === 'percentage' || element.getAttribute("maskingPattern").toString() === 'dgroup2' || element.getAttribute("maskingPattern").toString() === 'dgroup3' || element.getAttribute("maskingPattern").toString() === 'NZP')
                            return jQuery(element).autoNumeric('get');
                        else {
                            if (element.getAttribute("datatype") != "date")
                                return jQuery(element).cleanVal();
                            else
                                return encode_ParamValue(element.value);
                        }

                }
                else if (element.getAttribute("custommasking") != null && element.getAttribute("custommasking") != undefined && element.getAttribute("custommasking") != '' && element.getAttribute("custommasking") == 'true') {
                    if (element.getAttribute("datatype") != "date") {
                        return jQuery(element).autoNumeric('get');
                    }
                    else {
                        return element.value;
                    }
                }
                else{
                    if(element.type == "text" && element.style.textTransform == "uppercase")
                        return encode_ParamValue(element.value.toUpperCase());
                    else if(element.type == "text" && element.style.textTransform == "lowercase")
                          return encode_ParamValue(element.value.toLowerCase());
                    else
                        return encode_ParamValue(element.value);
                }
                    }      
                }
            break;
            case "checkbox":
                return element.checked;
                break;
            case "radio":
                return jQuery("input[name="+element.name+"]:checked").val();
                break;
            case "select-one":
                //Bug 81160 - Error in getControlValue() API in IForms 
                if($(element).hasClass("editableCombo")){//Bug 83221 Start
                    element=document.getElementById(element.id);
                    var ele = element.parentNode.getElementsByClassName("es-visible");
                         var listItems = element.parentNode.getElementsByTagName("li");
                        if (ele != null && ele.length > 0) {
//                        if (ele[0].getAttribute("value") == null || ele[0].getAttribute("value") == undefined)
//                            return "";
                        for( var p=0; p < listItems.length; p++ ) {
                            var option = listItems[p].textContent;
                            if (element.value == option){
                                if($(element).attr('svt')!=null){ //Bug 99635
                                    if($(element).attr('svt')==='V' || $(element).attr('svt')==='') {
                                       return listItems[p].getAttribute("originalValue");      
                                    } else if($(element).attr('svt')==='L'){
                                        return listItems[p].textContent;
                                    } 
                                } else {
                                  return listItems[p].getAttribute("originalValue");
                               }
                                
                            }
                        }
                            return element.value;
                        }
                        else{
                            return element.value; // Bug 91924
                        }
                }
                if(element.selectedIndex==-1)
                    return "";
                else {
                    if($(element).attr('svt')!=null){
                        if($(element).attr('svt')==='V' || $(element).attr('svt')==='') {
                            return element.options[element.selectedIndex].value;      
                        } else if($(element).attr('svt')==='L'){
                            return element.options[element.selectedIndex].text;
                        } 
                    } else {
                      return element.options[element.selectedIndex].value ;
                   }
                }   
                break;
            case "date":
                return encode_ParamValue(document.getElementById(element.id).value);
            case "datetime-local":
                return encode_ParamValue(document.getElementById(element.id).value);
            case "password":{   //Bug 86217
                    var eToken = '674649353866384637';
                    var bf = new Blowfish('DES');
                    var ePwd = bf.encryptx(element.value,eToken);
                    //pwd.value = encode_utf8(ePwd);
                    return ePwd;
            }
        }
    }

    /* 
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    if(!com)
    {
        var com = {}
    }

    if(!com.newgen)
    {
        com.newgen={}
    }

      function showGridAddDeleteButtons(controlId,showHideAddDelete){
        var addBtn = document.getElementById("add_"+controlId);
        var deleteBtn = document.getElementById("delete_"+controlId);
        if(showHideAddDelete!=undefined && showHideAddDelete!=null){
            if(showHideAddDelete=="1"){
                deleteBtn.style.display="";
            }
            else if(showHideAddDelete=="2"){
                addBtn.style.display="";
            }
            else if(showHideAddDelete=="3"){
                addBtn.style.display="";
                deleteBtn.style.display="";
            }
            else if(showHideAddDelete =="-1")
            {
                addBtn.style.display="none";
                deleteBtn.style.display="none";   
            }
        }
    }
    function setStyleInRichText(controlId, attributeName, attributeValue){
        var richTextDiv = document.getElementById("expandibleDiv_"+controlId);
        if(richTextDiv!=null && richTextDiv!=undefined){
        if(attributeName=="visible"){
            if(attributeValue=="true" || attributeValue==true){
                var parent = richTextDiv;
                    while(!parent.classList.contains("form-group")){
                        parent = parent.parentNode;
                    }
                    parent.style.display=""; 
                    parent.parentNode.style.display="";
            }
            else if(attributeValue=="false" || attributeValue==false){
                var parent = richTextDiv;
                while(!parent.classList.contains("form-group")){
                    parent = parent.parentNode;
                }
                parent.parentNode.style.display="none";
            }
        }
        else if(attributeName=="disable" || attributeName=="readonly"){
           if(attributeValue=="true"){
               richTextDiv.classList.add("disabledTextarea");
                $("#"+controlId).froalaEditor('edit.off');
           }
           else if(attributeValue=="false"){
                richTextDiv.classList.remove("disabledTextarea");
                $("#"+controlId).froalaEditor('edit.on');
           }
        }
        else if(attributeName=="mandatory"){
            if(attributeValue=="true"){
                document.getElementById(controlId+"_label").classList.add("mandatoryLabel");
                document.getElementById(controlId).required=true;
            }
            else if(attributeValue=="false"){
                 document.getElementById(controlId+"_label").classList.remove("mandatoryLabel");
                 document.getElementById(controlId).required=false;
            }
        }      
    }
       }
    function setStyle(controlId, attributeName, attributeValue,showHideAddDelete, showCheckBoxColumn)
    {
        var control = document.getElementById(controlId);
        if(useCustomIdAsControlName && (control==null || control==undefined)){
            control = document.getElementsByName(controlId)[0];
            if(control != null && control != undefined)
                controlId = control.getAttribute("id");
        }
        var richTextDiv = document.getElementById("expandibleDiv_"+controlId);
        if(richTextDiv!=null && richTextDiv!=undefined)
            setStyleInRichText(controlId,attributeName,attributeValue);
        else
        {
        if(control != null && control !=undefined)
        {
        if( attributeName.toLowerCase() == "backcolor")
        {
			if(window.overrideBackgroundColor){
                if(overrideBackgroundColor()){
                    document.querySelector("#"+controlId).style.setProperty('background-color', attributeValue, 'important');
                }
            } else{
				if(control.classList.contains("picklistStyle")){
					control.classList.remove("picklistStyle");
				}
				if(/(^[0-9A-F]{6}$)|(^#[0-9A-F]{3}$)/i.test(attributeValue))
					jQuery("#"+controlId).css("background-color","#"+attributeValue);
				else
				jQuery("#"+controlId).css("background-color",attributeValue);
			}
        }
        else if(attributeName.toLowerCase() == "title"){
            control.removeAttribute("onmouseover");
            control.title = attributeValue;
        }

        else if( attributeName.toLowerCase() == "fontcolor")
        {
            if(/(^[0-9A-F]{6}$)|(^#[0-9A-F]{3}$)/i.test(attributeValue))
                jQuery("#"+controlId).css("color","#"+attributeValue);
            else
                jQuery("#"+controlId).css("color",attributeValue);        
        }
        else if( attributeName.toLowerCase() == "charcase"){
            if(attributeValue.toLowerCase() == "up")
                jQuery("#"+controlId).css("text-transform","uppercase");
            else if(attributeValue.toLowerCase() == "low")
                jQuery("#"+controlId).css("text-transform","lowercase");
        }
        else if( attributeName.toLowerCase() == "visible")
        {
            if( attributeValue.toLowerCase() == "true"){
                if(control.classList.contains("iform-emptycell")||control.classList.contains("iform-emptyrow")){
                    control.parentNode.style.display="";
                 }
                else if(control.tagName == 'TABLE'){

                    //                var el=control;
                    //                while ((el = el.parentElement) && !el.classList.contains("col-md-12"));
                    //                el.style.display = "";
                    var mainTable = control.parentNode.parentNode.parentNode;
                    mainTable.style.display = "";
                } 
                else if ( $(control).hasClass("colorRange") ){
                    $(control).parents().eq('2').css("display","block");
                }
                else if ( $(control).hasClass("slider2") ){
                    $(control).parents().eq('2').css("display","block");
                }
                else if(controlId.indexOf("sheet")!=-1){
                    //jQuery("#"+controlId).css("display","inline");
                    jQuery("#"+controlId).css("display","");
                    jQuery("#"+controlId+"_label").css("display","");
                }
                else if(controlId.indexOf("Captcha")!=-1)
                {
                    jQuery("#"+controlId).css("display","");
                }
                 else if(control.classList.contains("FrameControl")){
                    control.parentNode.style.display="";
                }
                else if(control.classList.contains("tilestyle")){
                    control.style.display="";
                    control.parentNode.style.display="";
                    if(!control.previousElementSibling.style.display=="none")
                        control.previousElementSibling.style.display="block";
                }
                else{
                    jQuery("#"+controlId).css("display","");
                    jQuery("#"+controlId+"_label").css("display","");
                    control.style.display="";
                    if(control.getAttribute("datatype")!='date' && control.getAttribute("datatype")!='Text' && control.getAttribute("datatype")!='checkbox'&& control.tagName!='LABEL'){
                        var parent = document.getElementById(controlId).parentNode;
                        var isRadio=false;
                        for(var i=0;i<parent.parentNode.classList.length;i++)
                        {
                            if(parent.parentNode.classList[i].indexOf("radio")!==-1)
                            {
                                isRadio=true;
                                break;
                            }    
                        }                                                     
                        if(isRadio)
                        {
                            if(parent.getAttribute('allignment')=="horizontal")
                                parent.style.display="inline-block";
                            else
                                parent.style.display="";
                        }
                        while(!parent.classList.contains("form-group")){
                            parent = parent.parentNode;
                        }
                        parent.style.display=""; 
                        parent.parentNode.style.display="";
                    }
                    else{
                       var parent = document.getElementById(controlId).parentNode;
                        while(!parent.classList.contains("iform-control")){
                            parent = parent.parentNode;
                        }
                        parent.style.display=""; 
                        parent.parentNode.style.display=""; 
                    }
                    if (control.tagName == 'SELECT' && control.multiple) {     //Bug 81918 - setStyle() API not working on multiselect     
                        $(control).siblings().find('.dropdown-toggle').css("display","");
                        jQuery("#"+controlId+"_label").css("display","");
                        jQuery("#"+controlId).css("display","none");
                    }
                }
            }
            else if( attributeValue.toLowerCase() == "false"){
                if(control.classList.contains("iform-emptycell")||control.classList.contains("iform-emptyrow")){
                    control.parentNode.style.display="none";
                 }
                else if(control.tagName == 'TABLE'){
                    //var el=control;
                    //while ((el = el.parentElement) && !el.classList.contains("col-md-12"));
                    //el.style.display = "none";
                    var tableRef = control.parentNode.parentNode.parentNode;
                    tableRef.style.display = "none";
                }
                  else if ( $(control).hasClass("colorRange") ){
                    $(control).parents().eq('2').css("display","none");
                }
                else if ( $(control).hasClass("slider2") ){
                    $(control).parents().eq('2').css("display","none");
                }
                else if(controlId.indexOf("sheet")!=-1){
                    var tabId = controlId.substr(0,controlId.indexOf("_"))+"_"+controlId.substr(controlId.indexOf("sheet")+5, controlId.length-1);
                    jQuery("#"+tabId).css("display","none");
                    jQuery("#"+controlId).css("display","none");
                    jQuery("#"+controlId+"_label").css("display","none");
                }
                else if(controlId.indexOf("Captcha")!=-1)
                {
                    jQuery("#"+controlId).css("display","none");
                }
                else if(control.classList.contains("FrameControl")){
                    control.parentNode.style.display="none";
                }
                else if(control.classList.contains("tilestyle")){
                    control.style.display="none";
                    control.parentNode.style.display="none";
                    control.previousElementSibling.style.display="none";
                }
                else{
                    jQuery("#"+controlId).css("display","none");
                    jQuery("#"+controlId+"_label").css("display","none");
                    if(control.getAttribute("datatype")!='date' && control.getAttribute("datatype")!='Text' && control.getAttribute("datatype")!='checkbox'&& control.tagName!='LABEL'){
                        var parent = document.getElementById(controlId).parentNode;
                        var isRadio=false;
                        for(var i=0;i<parent.parentNode.classList.length;i++)
                        {
                            if(parent.parentNode.classList[i].indexOf("radio")!==-1)
                            {
                                isRadio=true;
                                break;
                            }    
                        }                                                     
                        if(isRadio)
                        {
                            parent.style.display="none";
                        }
                        else
                        {
                            while(!parent.classList.contains("form-group")){
                                parent = parent.parentNode;
                            }
                            parent.parentNode.style.display="none";
                        }
                    }                  
                    else{
                       var parent = document.getElementById(controlId).parentNode;
                        while(!parent.classList.contains("iform-control")){
                            parent = parent.parentNode;
                        }
                        parent.parentNode.style.display="none"; 
                    }
                    if (control.tagName == 'SELECT' && control.multiple) {        //Bug 81918 - setStyle() API not working on multiselect 
                        $(control).siblings().find('.dropdown-toggle').css("display","none");
                        jQuery("#"+controlId+"_label").css("display","none");                
                    }
    //                if(control.getAttribute("datatype")=='date'){
    //                    var calendarIcon = control.parentNode.parentNode.parentNode.childNodes[1];
    //                    calendarIcon.style.display="none";
    //                }
    //                 if(control.getAttribute("datatype")=='checkbox'){
    //                    var checkRef = control.parentNode.parentNode.parentNode;
    //                    checkRef.style.display="none";
    //                }
                }
            }
            $(".iform-table").floatThead('reflow');
        }
        else if( attributeName.toLowerCase() == "disable")
        {
            if( attributeValue.toLowerCase() == "true"){

                    if(control.tagName=="TABLE"){
                        try{
                        var tableDiv = control.parentNode.parentNode;
                        var selectRowColumns = tableDiv.getElementsByClassName("selectRowColumn");                        
                        var addRef = document.getElementById("add_"+controlId);
                        if(addRef!=null && addRef!=undefined){
                            addRef.disabled = true;
                            addRef.style.display = "none";
                        }
                        var deleteRef = document.getElementById("delete_"+controlId);
                        if(deleteRef!=null && deleteRef!=undefined)
                            deleteRef.style.display = "none";  
                        
                        var selectRef = document.getElementById("select_"+controlId);
                        if(showCheckBoxColumn != null && showCheckBoxColumn != undefined && !showCheckBoxColumn){
                            selectRef.parentNode.parentNode.style.display = "none";
                        }
                        
                        var i;
                        for(i=0;i<selectRowColumns.length;i++){
                            selectRowColumns[i].firstElementChild.firstElementChild.disabled=true;
                            if(i!=0){
                                   selectRowColumns[i].parentNode.classList.add("disabledTableBGColor");
                                   jQuery(selectRowColumns[i].parentNode.getElementsByClassName("listviewlabel")).addClass("disabledTableFont");
                            }
                        }
                        $("#"+controlId).floatThead('reflow');
                        var tableinput = control.getElementsByClassName("tableinput");
                        for(var i=0;i<tableinput.length;i++){
                                tableinput[i].removeAttribute("disabled");
                        }
                        control.classList.add("disabledTable");
                        if(control.getAttribute("type")=="Table"){
                            $("#"+controlId+' .control-class').addClass("disabledTableBGColor");
                        }
                        else{
                             $("#"+controlId+' .control-class').each(function(){
                                 this.parentNode.classList.add("disabledTableBGColor");
                             });
                        }
                        if(document.getElementById(controlId+"div_pad")!=null){
                            document.getElementById(controlId+"div_pad").firstChild.style.display="none";
                        }
                        if(isDatePicker=="Y")
                        {
                            var dateicons = control.getElementsByClassName("input-group-addon calenderinput");
                            for(var i=0;i<dateicons.length;i++){
                                dateicons[i].style.visibility="hidden";
                            }
                        }
                        }
                        catch(ex){}
                    }
                   
                    if(control.getAttribute("datatype")!=undefined && control.getAttribute("datatype")=="label")
                    {
                        control.style.pointerEvents = "none";
                    }
                if(control.getAttribute("datatype")!=undefined && control.getAttribute("datatype")=="date"){
                    if(mobileMode!="ios" &&mobileMode!="android")
                    {
                        control.parentNode.parentNode.getElementsByClassName("input-group-addon calenderinput")[0].style.pointerEvents = "none";
                        control.parentNode.parentNode.getElementsByClassName("input-group-addon calenderinput")[0].style.opacity = "0.6";
                   if(isDatePicker=="Y")
                        	control.parentNode.parentNode.getElementsByClassName("input-group-addon calenderinput")[0].style.visibility = "hidden";
                    }
                    
                }
                    if(!control.classList.contains("FrameControl")){
                          control.disabled = "true";
                    }
                    var radiostyle2 = control.getElementsByClassName("radioTwo");
                    if(radiostyle2 != null && radiostyle2 !=undefined)
                    {
                        for (var i = 0; i < radiostyle2.length; i++)
                        {
                            radiostyle2[i].childNodes[1].style.background = "#D3D3D3";
                        }
                    }              
                     if( $(control.parentNode).hasClass("radioThree")){
                            control = control.parentNode.parentNode;
                    }
                var radiostyle3 = control.getElementsByClassName("radioThree");
                if(radiostyle3 != null && radiostyle3 !=undefined)
                {
                    for (i = 0; i < radiostyle3.length; i++)
                    {
                        radiostyle3[i].style.pointerEvents = "none";
						if(radiostyle3[i].classList.contains("active"))
                        {
                            radiostyle3[i].classList.remove("active");
                            radiostyle3[i].classList.add("disabledBGColor");
                        } 
                    }
                }
                if(control.type=="text" || control.type=="textarea")
                    control.title=control.value;
                
                if(control.type!=null&&control.type=="text"){
                   disablePicklistButtons(controlId, true);
            }
                if(control.type!=undefined!=null && control.type!=undefined && control.type=="button"){
                    control.classList.add("disabledBtnColor");
                }

                $("#"+controlId+' .iform-button').addClass("disabledBtnColor");
                $("#"+controlId+' .control-class').attr('disabled', true);//.css("opacity","0.7");
                $("#"+controlId+' .selectRow').attr('disabled', true);
                $("#select_"+controlId).attr('disabled', true);
                $("#"+controlId+' a.control-class').each(function(){
                   // if(this.hasAttribute("href"))
                        $(this).css({"pointer-events": "none","cursor":"default","text-decoration":""});
                })
                $("#"+controlId+' img.control-class').each(function(){
                        $(this).css({"pointer-events": "none"});
                })
                if(control.classList.contains("iformTabControl")){
                    disableDoclistControl(controlId,true);
                }
                if(control.classList.contains("FrameControl")){
                    if(isDatePicker=="Y")
                    {
                        var dateicons = control.getElementsByClassName("input-group-addon calenderinput");
                        for(var i=0;i<dateicons.length;i++){
                            dateicons[i].style.visibility="hidden";
                        }
                    }
                    $("#"+controlId+' .control-class').addClass("disabledBGColor");
                     $("#"+controlId+' .control-class').each(function(){
                         setStyleInRichText(this.id,attributeName,attributeValue);
                         disablePicklistButtons(this.id, true);
                     });
                    $("#"+controlId+' .iform-table').each(function(){
                        if(this.id!="")
                            disableListView(this.id);
                    });
                    disableDoclistControl(controlId,true);
                }
                else{
                if(control.tagName!="TABLE" && !control.classList.contains("iform-radio"))
                   control.classList.add("disabledBGColor");
                if(control.getAttribute("type")=="checkbox")
                   control.parentNode.childNodes[1].style.cssText="background:#D3D3D3 !important";
                } 
                if(showHideAddDelete=="4")
                {                                                             
                    var i;
                    for (i = 0; i < selectRowColumns.length; i++) {
                        selectRowColumns[i].firstElementChild.firstElementChild.removeAttribute("disabled");
                        if (i != 0) {
                            selectRowColumns[i].parentNode.classList.remove("disabledTableBGColor");
                            jQuery(selectRowColumns[i].parentNode.getElementsByClassName("listviewlabel")).removeClass("disabledTableFont");
                        }                     
                    }
                }
                if ( $(control).hasClass("colorRange") ){
                    $($(control).parents().eq('1').find("input")[0]).prop("disabled",true);
                    $($(control).parents().eq('1').find("input")[1]).prop("disabled",true);
                    if($(control).hasClass("disabledBGColor"))
                      $(control).removeClass("disabledBGColor");
                   }
                 else if ( $(control).hasClass("slider2") ){
                    $($(control).parents().eq('1').find("input")[0]).prop("disabled",true);
                    $($(control).parents().eq('1').find("input")[1]).prop("disabled",true);
                    if($(control).hasClass("disabledBGColor"))
                      $(control).removeClass("disabledBGColor");
                   }

            }

            else if( attributeValue.toLowerCase() == "false"){
                if(control.tagName=="TABLE"){
                        //showGridAddDeleteButtons(controlId,showHideAddDelete);
                        var addRef = document.getElementById("add_"+controlId);
                        if(addRef!=null && addRef!=undefined){
                            addRef.disabled = false;
                            addRef.style.display = "";
                        }
                        var deleteRef = document.getElementById("delete_"+controlId);
                        if(deleteRef!=null && deleteRef!=undefined)
                            deleteRef.style.display = "";
                        var tableDiv = control.parentNode.parentNode;
                        var selectRowColumns = tableDiv.getElementsByClassName("selectRowColumn");
                        var i;
                        for(i=0;i<selectRowColumns.length;i++){
                            selectRowColumns[i].firstElementChild.firstElementChild.removeAttribute("disabled");
                            if(i!=0){
                                 selectRowColumns[i].parentNode.classList.remove("disabledTableBGColor");
                                 jQuery(selectRowColumns[i].parentNode.getElementsByClassName("listviewlabel")).removeClass("disabledTableFont");
                            }
                            if(showHideAddDelete=="3"){
                                selectRowColumns[i].style.display="";
                            }
                        }
                        $("#"+controlId).floatThead('reflow');
                        control.classList.remove("disabledTable");
                        if(control.getAttribute("type")=="Table"){
                            $("#"+controlId+' .control-class').removeClass("disabledTableBGColor");
                        }
                        else{
                             $("#"+controlId+' .control-class').each(function(){
                                 this.parentNode.classList.remove("disabledTableBGColor");
                             });
                        }
                        if(document.getElementById(controlId+"div_pad")!=null){
                            document.getElementById(controlId+"div_pad").firstChild.style.display="";
                        }
                        if(isDatePicker=="N")
                        {
                            var dateicons = control.getElementsByClassName("glyphicon-calendar");
                            for(var i=0;i<dateicons.length;i++){
                                dateicons[i].style.visibility="";
                            }
                        }
                        showGridAddDeleteButtons(controlId,showHideAddDelete);

                    }
                   
                    if(control.getAttribute("datatype")!=undefined && control.getAttribute("datatype")=="label")
                    {
                        control.style.pointerEvents = "";
                    }
                if(control.getAttribute("datatype")!=undefined && control.getAttribute("datatype")=="date"){
                    if(mobileMode!="ios" &&mobileMode!="android")
                    {
                        control.parentNode.parentNode.getElementsByClassName("input-group-addon calenderinput")[0].style.pointerEvents = "";
                        control.parentNode.parentNode.getElementsByClassName("input-group-addon calenderinput")[0].style.opacity = "";
                        if(isDatePicker=="N")
                            control.parentNode.parentNode.getElementsByClassName("input-group-addon calenderinput")[0].style.visibility = "";
                        }
                    
                }
                if(control.type!=null&&control.type=="text"){
                    disablePicklistButtons(controlId, false);
                }
                if(control.type!=undefined!=null && control.type!=undefined && control.type=="button"){
                    control.classList.remove("disabledBtnColor");
                }
                control.removeAttribute("disabled");
                var radiostyle2 = null;
                if(typeof control.parentNode.childNodes[1] != "undefined")
                  radiostyle2 = control.getElementsByClassName("radioTwo");
                if(radiostyle2 != null && radiostyle2 !=undefined)
                {
                    for (var i = 0; i < radiostyle2.length; i++)
                    {
                        radiostyle2[i].childNodes[1].style.background = "unset";
                    }
                }
                if( $(control.parentNode).hasClass("radioThree")){
                    control = control.parentNode.parentNode;
                }
                var radiostyle3 = control.getElementsByClassName("radioThree");
                if(radiostyle3 != null && radiostyle3 !=undefined)
                {
                    for (i = 0; i < radiostyle3.length; i++)
                    {
                        radiostyle3[i].style.pointerEvents = "";
						if(radiostyle3[i].classList.contains("disabledBGColor"))
                        {
                            radiostyle3[i].classList.add("active");
                            radiostyle3[i].classList.remove("disabledBGColor");
                        } 
                    }
                }
                if(control.tagName == 'SELECT' && control.multiple){ //Bug 89121 Start
                    $(control).siblings().children()[0].classList.remove("disabled");
                } //Bug 89121 End
                control.classList.remove("disabledBGColor");
                $("#"+controlId+' .iform-button').removeClass("disabledBtnColor");
                $("#"+controlId+' .control-class').attr('disabled', false);//.css("opacity","");
                $("#"+controlId+' a.control-class').each(function(){
                    if(this.hasAttribute("href"))
                        $(this).css({"pointer-events": "","cursor":"","text-decoration":"underline"});
                });
                if(control.classList.contains("iformTabControl")){
                    disableDoclistControl(controlId,false);
                }
                if(control.classList.contains("FrameControl")){
                    if(isDatePicker=="N")
                    {
                        var dateicons = control.getElementsByClassName("input-group-addon calenderinput");
                        for(var i=0;i<dateicons.length;i++){
                            dateicons[i].style.visibility="";
                            dateicons[i].style.opacity = "";
                        }
                    }
                     $("#"+controlId+' .control-class').removeClass("disabledBGColor");
                     $("#"+controlId+' .control-class').each(function(){
                         setStyleInRichText(this.id,attributeName,attributeValue);
                         disablePicklistButtons(this.id, false);
                          if(this.tagName == 'SELECT' && this.multiple){
                            reloadListBoxLayout(this.id);
                        }
                     });
                    $("#"+controlId+' .iform-table').each(function(){
                        if(this.id!="")
                            enableListView(this.id,showHideAddDelete);
                    });
                    disableDoclistControl(controlId,false);
                }
               else{
                 if(control.tagName!="TABLE" && !control.classList.contains("iform-radio")){
                   control.classList.remove("disabledBGColor");
                   if(control.getAttribute("type")=="checkbox"){
                       control.parentNode.childNodes[1].style.cssText="background:unset";
                   }
               }
                    if(showHideAddDelete=="3"){
                       jQuery(".selectRowColumn").css("display","auto");     
                    }
                }
                if ( $(control).hasClass("colorRange") ){
                    $($(control).parents().eq('1').find("input")[0]).prop("disabled",false);
                    $($(control).parents().eq('1').find("input")[1]).prop("disabled",false);
                   }
                else if ( $(control).hasClass("slider2") ){
                    $($(control).parents().eq('1').find("input")[0]).prop("disabled",false);
                    $($(control).parents().eq('1').find("input")[1]).prop("disabled",false);
                   }
                
        }
        }
        else if( attributeName.toLowerCase() == "readonly")
        {
            if( attributeValue.toLowerCase() == "true"){
                if(controlId.indexOf("button")!=-1) 
                    $("#"+controlId).attr('disabled', true);
                control.readOnly  = true;
                if(control.type!=null&&control.type=="text"){
                    if(document.getElementById(controlId+"_pickListbtn")!=null)
                        document.getElementById(controlId+"_pickListbtn").disabled=true;
                     if(document.getElementById(controlId+"_pickListClearbtn")!=null && document.getElementById(controlId+"_pickListClearbtn")!=undefined)
                      document.getElementById(controlId+"_pickListClearbtn").disabled = true;
                }
                if(control.type!=null&&control.type=="select-one"){
                    var options = control.options;
                    for(var i=0;i<options.length;i++){
                        options[i].disabled = true;
                    }
                }
                if(control.classList.contains("FrameControl")){           
                    $("#"+controlId+' .control-class').attr('readonly', true);                                                       
                }
                $("#"+controlId+'.control-class').attr('readonly', true);//Bug 90052    
            }
            else if( attributeValue.toLowerCase() == "false"){
                if(controlId.indexOf("button")!=-1) 
                    $("#"+controlId).attr('disabled', false);
                if(control.type!=null&&control.type=="text"){
                    if(document.getElementById(controlId+"_pickListbtn")!=null)
                        document.getElementById(controlId+"_pickListbtn").disabled=false;
                     if(document.getElementById(controlId+"_pickListClearbtn")!=null && document.getElementById(controlId+"_pickListClearbtn")!=undefined)
                      document.getElementById(controlId+"_pickListClearbtn").disabled = false;
                }
                control.readOnly = false;
                if(control.type!=null&&control.type=="select-one"){
                    var options = control.options;
                    for(var i=0;i<options.length;i++){
                        options[i].disabled = false;
                    }
                }
                if(control.classList.contains("FrameControl")){           
                    $("#"+controlId+' .control-class').attr('readonly', false);        
                }
                $("#"+controlId+'.control-class').attr('readonly', false); //Bug 90052
            }
        }

        else if(attributeName.toLowerCase() == "lock"){
            var lockNode = control.getElementsByClassName("sectionStyle");
            if(attributeValue == "true"){
                lockNode[0].onclick = '';
                document.getElementById(controlId+"_img").style.display="none";
            }
            if(attributeValue == "false"){
                $(lockNode[0]).click(function(){toggleSection(this);});
                document.getElementById(controlId+"_img").style.display="";
    //            lockNode[0].onclick = 'toggleSection()t;'
    //                function(){
    //                toggleSection();
    //            };
            }
        }

        else if(attributeName == "collapsible"){
            var lockNode = control.getElementsByClassName("sectionStyle");
            if(attributeValue == "true"){
                lockNode[0].onclick = function(){
                    toggleSection();
                };
            }
            if(attributeValue == "false"){
                lockNode[0].onclick = '';
            }
        }

        else if(attributeName == "sectionstate"){
            var ref=control.getElementsByClassName("sectionStyle")[0];//Bug 82828
            if(attributeValue == "expanded"){
                if(jQuery(ref).attr("state") == "collapsed")
                    toggleSection(ref);            
            }
            if(attributeValue == "collapsed"){
                if(jQuery(ref).attr("state") == "expanded")
                    toggleSection(ref);
            }
        }

        else if(attributeName == "mandatory"){
            var nodes = control.getElementsByClassName("control-class");
            var mandatoryLabel = document.getElementById(controlId+"_label");
            if(attributeValue == "true"){
                for(var i=0;i<nodes.length;i++){
                    nodes[i].required = true;
                }
                control.required = true;
                if(control.classList.contains("iform-radio")){
                    var buttons = document.getElementsByName(controlId);
                    for(var i=0;i<buttons.length;i++){
                        buttons[i].required = true;
                    }
                }
                mandatoryLabel.classList.add("mandatoryLabel");
            }
            else if(attributeValue == "false"){
                for(var i=0;i<nodes.length;i++){
                    nodes[i].required = false;
                }
                toggleErrorTooltip(control,mndMsgRef,null,false,0);
                control.required = false;
                 if(control.classList.contains("iform-radio")){
                    var buttons = document.getElementsByName(controlId);
                    for(var i=0;i<buttons.length;i++){
                        buttons[i].required = false;
                    }
                }
                mandatoryLabel.classList.remove("mandatoryLabel");
				
//                var ctrlType=jQuery(control).attr("type");//Bug 83904 Start
//                if(control.classList.contains("iform-radio")){
//                    delete ComponentValidatedMap[jQuery(control).attr("id")];
//                }
//                else if(ctrlType=="text" || ctrlType=="textarea"|| typeof ctrlType=="undefined"){
//                    var value=getControlValue(document.getElementById(jQuery(control).attr("id")));
//                    if(typeof ctrlType=="undefined")
//                        value=jQuery(control).val();
//                    if(jQuery(control).attr("multiple")!="undefined"){
//                        jQuery(control).siblings().find('.dropdown-toggle').removeClass("mandatory");
//                    }
//                    if((value!=null) && (value!='') && (!validateValue(document.getElementById(jQuery(control).attr("id")), ctrlType)) && (control.style.display!="none")){                                              
//                        ComponentValidatedMap[jQuery(control).attr("id")]=false;
//                    }
//                    else
//                        delete ComponentValidatedMap[jQuery(control).attr("id")];
//                }
//                else if(ctrlType=="checkbox"){
//                    delete ComponentValidatedMap[jQuery(control).attr("id")];
//                }
                
                if(jQuery(control).parent().parent().parent().hasClass("floating-label-form-group"))
                        jQuery(control).parent().parent().parent().removeClass("mandatory");
                    else
                        jQuery(control).removeClass("mandatory");
                var mndMsgRef=document.getElementById(controlId+"_msg");
                if(mndMsgRef!=null)
                    toggleErrorTooltip(control,mndMsgRef,null,true,0);
                    //mndMsgRef.style.display="none";//Bug 83904 End
            }
        }
        //82661 starts
        else if(attributeName == "maxlength"){
            if(attributeValue!='')
                control.setAttribute("maxlength", attributeValue);
        }
        //82661 ends

        else if( attributeName.toLowerCase() == "fontfamily")
            jQuery("#"+controlId).css("font-family",attributeValue);
        else if( attributeName.toLowerCase() == "fontweight")
            jQuery("#"+controlId).css("font-weight",attributeValue);
        else if( attributeName.toLowerCase() == "fontstyle")
            jQuery("#"+controlId).css("font-style",attributeValue);
        else if( attributeName.toLowerCase() == "fontsize")
            jQuery("#"+controlId).css("font-size",attributeValue);
            else if( attributeName.toLowerCase() == "tooltip")
            jQuery("#"+controlId).attr("title",attributeValue);
        else if(attributeName.toLowerCase() == "custompattern")
        {
            var control = document.getElementById(controlId);
            if(useCustomIdAsControlName && (control==null || control==undefined)){
		            control = document.getElementsByName(controlId)[0];
		            if(control != null && control != undefined)
		                controlId = control.getAttribute("id");
		    }
            if (control != null)
            {
                document.getElementById(controlId + "_patternString").setAttribute("custompattern", attributeValue);
                document.getElementById(controlId + "_patternString").innerHTML = attributeValue;
            }
        }
        else if(attributeName == "identifier"){
             if(attributeValue=="true")
                jQuery("#"+controlId+"_identifier").css("display","");
            else
                jQuery("#"+controlId+"_identifier").css("display","none");
        }
        }
        
        //Bug 81918 - setStyle() API not working on multiselect 
        if (control!=null && control!=undefined && control.tagName == 'SELECT') {           
            if(control.multiple){
                reloadListBoxLayout(controlId);
    }
        }
    }
    }
    
    function disablePicklistButtons(controlId,isDisable){
        if(document.getElementById(controlId+"_pickListbtn")!=null)
            document.getElementById(controlId+"_pickListbtn").disabled=isDisable;
        if(document.getElementById(controlId+"_pickListClearbtn")!=null && document.getElementById(controlId+"_pickListClearbtn")!=undefined)
            document.getElementById(controlId+"_pickListClearbtn").disabled = isDisable;
    }


    function isValueChanged()
    {
        return valueChanged;
    }

    function isComponentValidated(ref)
    {
        var tempFormMode=typeof formMode=="undefined"?"W":formMode;//Bug 82321
        //Bugzilla – Bug 59011
        if( ref != 'Y'&&tempFormMode!="R"){//Bug 82321
          if( window.skipValidation ){
              //return window.skipValidation();
              if(window.skipValidation()){
                  saveRichTextEditorData();//rich text not getting set while using skipValidation
                  return true;
          }
          }
          if(Object.keys(ComponentValidatedMap).length>0){  //Bug 83114
               setFocus(Object.keys(ComponentValidatedMap)[0], false);
               return false;
           }
          if(!validateMandatoryFields())
            return false;
          if( !fetchCollapsedFrameHTML())
              return false;
           if(Object.keys(serverValidationMap).length>0){
               var firstInvalidControlInMap = Object.keys(serverValidationMap)[0];
               showMessage(firstInvalidControlInMap.controlId,THE_VALUE+" "+serverValidationMap[firstInvalidControlInMap].controlValue+" "+INCOMPATIBLE_MSG+" "+FIELD+ " : "+serverValidationMap[firstInvalidControlInMap].controlName,"error");
               //setFocus(Object.keys(serverValidationMap)[0].controlId, false);
               return false;
           }
           if(Object.keys(tableComponentMap).length>0){
               var tableJSON = tableComponentMap[Object.keys(tableComponentMap)[0]];
               setFocus(tableJSON["tableId"], false);
               showMessage("",THE_VALUE+" : "+tableJSON["cellData"]+INCOMPATIBLE_MSG+" "+ROW+" : "+tableJSON["rowIndex"]+","+COLUMN+": "+tableJSON["columnName"] +" "+OF+" "+TABLE+" : "+tableJSON["tableName"],"error");
               return false;
           }
        }

        saveRichTextEditorData();
        if(ref=='Y') {
            ref = 'S';
        } else if(ref == undefined) {
            ref = 'D';
        }
        if(!customServerValidation(ref))
            return false;
         return true;     
    }

    function clearValueChanged()
    {
        valueChanged=false; 
    }

    function fireFormValidation(type)
    {
        if(type != "S")
            return Object.keys(ComponentValidatedMap).length==0;
        else
            return true;
    }


    var ENCODING="UTF-8";
    var hexArr = new Array('0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F');

    function encode_utf8(ch)
    {
        if (ENCODING.toUpperCase() != "UTF-8")
            return escape(ch);

        return encodeURIComponent(ch);

        var i,bytes;
        var utf8 = new String();
        var temp;

        for(i=0, bytes = 0; i<ch.length; i++)
        {
            temp = ch.charCodeAt(i);
            if(temp < 0x80)
            {
                utf8 += String.fromCharCode(temp);
            }
            else if (temp < 0x0800)
            {
                utf8 += String.fromCharCode((temp>> 6 | 0xC0));
                utf8 += String.fromCharCode((temp & 0x3F | 0x80));
            }
            else
            {
                utf8 += String.fromCharCode((temp>> 12 | 0xE0));
                utf8 += String.fromCharCode((temp>> 6 & 0x3F | 0x80));
                utf8 += String.fromCharCode((temp & 0x3F | 0x80));
            }
        }

        if (navigator.appName.indexOf("Netscape") == -1)
        {
            return escape(utf8);
        }
        var esc = new String();
        for(l=0;l<utf8.length;l++)
        {
            if(utf8.charCodeAt(l)<128)
                esc += escape(utf8[l]);
            else
            {
                esc += "%";
                esc += hexArr[utf8.charCodeAt(l)>>4];
                esc += hexArr[utf8.charCodeAt(l) & 0xf];
            }
        }
        return esc;
    }

    function decode_utf8(utftextBytes)
    {
        var utftext = unescape(utftextBytes);
        if (ENCODING.toUpperCase() != "UTF-8")
            return utftext;
        var plaintext = "",temp;

        var i=c1=c2=c3=c4=0;

        while(i<utftext.length)
        {
            c1 = utftext.charCodeAt(i);
            temp = '?';

            if (c1<0x80)
            {
                temp = String.fromCharCode(c1);
                i++;
            }
            else if( (c1>>5) ==    6) //2 bytes
            {
                c2 = utftext.charCodeAt(i+1);

                if( !((c2^0x80)&0xC0))
                    temp = String.fromCharCode(((c1&0x1F)<<6) | (c2&0x3F));
                i+=2;
            }
            else if( (c1>>4) == 0xE)  //3 bytes
            {
                c2 = utftext.charCodeAt(i+1);
                c3 = utftext.charCodeAt(i+2);

                if( !(((c2^0x80)|(c3^0x80))&0xC0) )
                    temp = String.fromCharCode(((c1&0xF)<<12) | ((c2&0x3F)<<6) | (c3&0x3F));
                i+=3;
            }
            else
                i++;
            plaintext += temp;
        }
        return plaintext;
    }

    function getContentWindow(modalId){
        var returnedObject = null;
        try{
            returnedObject =  window.frames[modalId].contentWindow.document;
        }catch(ex){
            returnedObject =  window.frames[modalId].document;
        }
        return returnedObject;
    }
    
    function executeWebService(ref,event,eventType,isKeyDown)//Bug 75527, Bug 75529
    {
        var name;
        if(ref.type=='radio')
            name=ref.name;
        else
            name= ref.id;
         if(window.webServicePreHook){
            if(!webServicePreHook(name)){
                return;
            }
        }
        if(typeof isKeyDown!="undefined"&&isKeyDown){
            var jsonArray=JSON.parse(decode_utf8(eventType));
            eventType="";
            for(var i=0;i<jsonArray.length;i++){
                if(event.keyCode==112){
                    if(jsonArray[i]=='KeyPressF1'){
                        eventType="KeyPressF1";
                        break;
                    }
                }
                else if(event.keyCode==113){
                    if(jsonArray[i]=='KeyPressF2'){
                        eventType="KeyPressF2";
                        break;
                    }
                }
                else if(event.keyCode==114){
                    if(jsonArray[i]=='KeyPressF3'){
                        eventType="KeyPressF3";
                        break;
                    }
                }
                else if(event.keyCode==115){
                    if(jsonArray[i]=='KeyPressF4'){
                        eventType="KeyPressF4";
                        break;
                    }
                }
                else if(event.keyCode==116){
                    if(jsonArray[i]=='KeyPressF5'){
                        eventType="KeyPressF5";
                        break;
                    }
                }
                else if(event.keyCode==117){
                    if(jsonArray[i]=='KeyPressF6'){
                        eventType="KeyPressF6";
                        break;
                    }
                }
                else if(event.keyCode==118){
                    if(jsonArray[i]=='KeyPressF7'){
                        eventType="KeyPressF7";
                        break;
                    }
                }
                else if(event.keyCode==119){
                    if(jsonArray[i]=='KeyPressF8'){
                        eventType="KeyPressF8";
                        break;
                    }
                }
                else if(event.keyCode==120){
                    if(jsonArray[i]=='KeyPressF9'){
                        eventType="KeyPressF9";
                        break;
                    }
                }
                else if(event.keyCode==121){
                    if(jsonArray[i]=='KeyPressF10'){
                        eventType="KeyPressF10";
                        break;
                    }
                }
                else if(event.keyCode==122){
                    if(jsonArray[i]=='KeyPressF11'){
                        eventType="KeyPressF11";
                        break;
                    }
                }
                else if(event.keyCode==123){
                    if(jsonArray[i]=='KeyPressF12'){
                        eventType="KeyPressF12";
                        break;
                    }
                }
                else{
                    if(jsonArray[i]=='KeyDown'){
                        eventType="KeyDown";
                        break;
                    }
                }
            }
        }
        if(eventType!=""){
            var url = "webservice.jsp";
            var requestString = "pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&controlId="+encode_utf8(name)+"&eventType="+encode_utf8(eventType);//Bug 75527, Bug 75529
            var contentLoaderRef = new net.ContentLoader(url, WSResponseHandler, formErrorHandler, "POST", requestString, true);
        }

    }

    function getQueryStringValue (key) {  
      return decodeURIComponent(window.location.search.replace(new RegExp("^(?:.*[&\\?]" + encodeURIComponent(key).replace(/[\.\+\*]/g, "\\$&") + "(?:\\=([^&]*))?)?.*$", "i"), "$1"));  
    }  

    function openModal(controlId,header,batchSize,isListViewModal,rowId,colId)
    {
        document.getElementById("picklistHeader").innerHTML=header;
        if(window.picklistPreHook){//Bug 82813 Start
            var control = document.getElementById(controlId);
	        if(useCustomIdAsControlName && (control==null || control==undefined)){
	            control = document.getElementsByName(controlId)[0];
	            if(control != null && control != undefined)
	               controlId = control.getAttribute("id");
	        }
            if(!window.picklistPreHook(controlId))
                return;
        }//Bug 82813 End
        CreateIndicator("application");        
        var isListViewModalPickList=typeof isListViewModal=="undefined"?false:true;
        //Bug 81682 - Picklist functionality not working on nested complex variable
        var sid = jQuery("#sid").val();
        var context = '/' + window.location.pathname.split("/")[1];
        var url = context + "/components/viewer/picklistview.jsp";
        var reqTok = iforms.ajax.processRequest("formuri="+encode_utf8(url), context+"/GetReqToken");
        var requestString;
        if((mobileMode=="ios"||mobileMode=="android")){
            if(document.getElementById("mobileSubFormModal")!=null && document.getElementById("mobileSubFormModal")!=undefined &&  $('#mobileSubFormModal').find('#'+controlId).length != 0 ){
                 var buttonId = encode_ParamValue(document.getElementById("subFormId").value);
                 requestString="picklistview.jsp?controlId="+controlId+"&rowId="+rowId+"&colId="+colId+"&batchSize="+batchSize+"&fid="+fid+"&buttonId="+buttonId+"&WD_SID=" + sid + "&WD_RID="+reqTok;
            } else{
                requestString="picklistview.jsp?controlId="+controlId+"&rowId="+rowId+"&colId="+colId+"&batchSize="+batchSize+"&fid="+fid+"&buttonId="+getQueryStringValue("buttonId")+"&WD_SID=" + sid + "&WD_RID="+reqTok;//Bug 75468
            }
        } 
        else {
            requestString="picklistview.jsp?controlId="+controlId+"&rowId="+rowId+"&colId="+colId+"&batchSize="+batchSize+"&fid="+fid+"&buttonId="+getQueryStringValue("buttonId")+"&WD_SID=" + sid + "&WD_RID="+reqTok;//Bug 75468
        }
        
        if(isListViewModalPickList)
            requestString+="&isListModal=1";
        //document.getElementById("iFrameSearchModal").src=requestString;
        IframeRequestWithPost(requestString, document.getElementById("iFrameSearchModal"));
        $("#searchModal").modal();
    }
    
    function setPickListHeader(controlId,header){
         document.getElementById("picklistHeader").innerHTML=header;
    }
    
    function pickListClear(controlId)
    {
        var control = document.getElementById(controlId);
	        if(useCustomIdAsControlName && (control==null || control==undefined)){
	            control = document.getElementsByName(controlId)[0];
	            if(control != null && control != undefined)
	              controlId = control.getAttribute("id");
	        }
        document.getElementById(controlId).value="";
        ctrOnchangeHandler(document.getElementById(controlId),1);
        if(window.clearPicklistPostHook)
            clearPicklistPostHook(controlId);
    }
    function encode_ParamValue(param)
    {
            return param;
    }
    
    function WSResponseHandler()
{
    var WSResponse = this.req.getResponseHeader("WSResponseJSON");
    if (WSResponse == null) {
        var output = decode_utf8(this.req.responseText);
        var message = this.req.getResponseHeader("message");
        var WSControlId = getQueryVariable(this.params, "controlId");
        if (typeof message != "undefined" && message != "" && message != null)//Bug 82907
            //showMessage("", message, "error");
                showSplitMessage("", message,ERROR_TITLE, "error");
        try {
            var outputArray = JSON.parse(output);
//        var outputArray = responseJSON.responseData;
//        if(responseJSON.APIData!=null)
//            renderExecuteServerEventAPIData(responseJSON.APIData);
            for (var i = 0; i < outputArray.length; i++)
            {
                for (var j = 0; j < outputArray[i].length; j++) {
                    try {
                        var dataArray = outputArray[i][j];
                        var controlId = dataArray.id;
                        var type = dataArray.type;
                        var dataValue = dataArray.value.value;
                        //Bug No:98952
                        //if(type !== "table")
                         //   dataValue=decode_utf8(dataValue);
                        if (type == "textarea" || type == "textbox" || type == "label" || type == "combo" || type == "checkbox"
                                || type == "radio" || type == "datepick") {
                            setValue(controlId, dataValue);
                        } else if (type == "table") {
                            if (dataArray.isAppendData) {
                                $("#" + controlId + " tbody").html($("#" + controlId + " tbody").html() + encode_ParamValue(dataValue));
                            } else {
                                $("#" + controlId + " tbody").html(encode_ParamValue(dataValue));
                            }
                            //$("#"+controlId+ " tbody").appendChild(dataValue);
                            $("#" + controlId).floatThead('reflow');
                            //var dgroupColumns = this.req.getResponseHeader("dgroupColumns");
                            //var maskedLabels = this.req.getResponseHeader("maskedLabels");
                            checkTableHeight(controlId);
                            /*
                             for(var i=0;i<dgroupColumns.split(",").length;i++){
                             var className = "dgroup_"+controlId+"_"+dgroupColumns.split(",")[i];
                             //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);
                             
                             $('.'+className).each(function() {
                             var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
                             var dec = '0';
                             if(jQuery(this).attr('typeofvalue')=='Float')
                             dec = jQuery(this).attr('Precision');
                             jQuery(this).autoNumeric('init',{
                             dGroup: digitGroup,
                             mDec: dec
                             }); 
                             });
                             }
                             */
                            $('.listviewlabel').each(function () {
                                var typeofvalue = typeof this.getAttribute("typeofvalue") == 'undefined' ? '' : this.getAttribute("typeofvalue");
                                if ((this.getAttribute("maskingpattern") != "nomasking" && this.getAttribute("maskingpattern") != "")
                                        || (typeofvalue == 'Float' && this.getAttribute("maskingpattern") == "nomasking"))
                                {
                                    maskfield(this, 'savedlabel');
                                }

                            });
                            $('.tabletextbox').each(function () {
                                var typeofvalue = typeof this.getAttribute("typeofvalue") == 'undefined' ? '' : this.getAttribute("typeofvalue");
                                if ((this.getAttribute("maskingpattern") != "nomasking" && this.getAttribute("maskingpattern") != "")
                                        || (typeofvalue == 'Float' && this.getAttribute("maskingpattern") == "nomasking"))
                                {
                                    maskfield(this, 'input');
                                }

                            });
                            var totalValueElements = document.getElementById('totallabel_' + controlId).innerHTML.split(",!,");
                            for (var k = 0; k < totalValueElements.length; k++) {
                                //var controlRef = document.getElementById('label'+'_'+controlId+'_'+maskedLabels.split(",")[i]);
                                if (totalValueElements[k] != '') {
                                    $(document.getElementsByClassName(totalValueElements[k].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function () {
                                        var typeofvalue = typeof this.getAttribute("typeofvalue") == 'undefined' ? '' : this.getAttribute("typeofvalue");
                                        if ((this.getAttribute("maskingpattern") != "nomasking" && this.getAttribute("maskingpattern") != "")
                                                || (typeofvalue == 'Float' && this.getAttribute("maskingpattern") == "nomasking"))
                                        {
                                            maskfield(this, 'label');
                                        }
                                    });
                                }
                                showTotal('', totalValueElements[k]);
                            }

                            if (window.addRowPostHook)
                            {
                                addRowPostHook(controlId);
                            }
                        }
                    } catch (ex) {

                    }
                }
            }

        } catch (ex) {
        }
    } else {
        var control = getQueryVariable(this.params, "controlId");
        try {
            WSResponse = JSON.parse(WSResponse);
            for (var key in WSResponse)
            {
                setValue(decode_utf8(key), decode_utf8(WSResponse[key]));
            }

        } catch (ex) {
        }
    }
    var wsControlID = getQueryVariable(this.params, "controlId");
    if (window.webServicePostHook) {
        webServicePostHook(wsControlID);
    }
}
    function closeSubForm(){
        if(totalSubWindows>0){
            console.log("close window here..");
            allWindows[totalSubWindows].close();
        }
    }
    function scrollMainForm(){
     $("#moveToTopMainForm").click(function(){
            $("#oforms_iform").animate({
                    scrollTop: 0
            }); 
        });   
//        $('#moveToTopMainForm').hide();

        $("#oforms_iform").scroll(function(){
        if ($(this).scrollTop() > 10) {
            $('#moveToTopMainForm').show().fadeIn();
        } else {
            $('#moveToTopMainForm').fadeOut().hide();
        }
    });
    }
    function moveToTop(){
        var div = document.createElement("div");
        var button = document.createElement("button");
        button.innerHTML = '<svg width="18px" height="10px" viewBox="0 0 18 10" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"><title>Path 11 Copy</title><g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd"><path d="M4,3 C4.51283584,3 4.93550716,3.38604019 4.99327227,3.88337887 L5,4 L5,13 L14,13 C14.5128358,13 14.9355072,13.3860402 14.9932723,13.8833789 L15,14 C15,14.5128358 14.6139598,14.9355072 14.1166211,14.9932723 L14,15 L4,15 C3.48716416,15 3.06449284,14.6139598 3.00672773,14.1166211 L3,14 L3,4 C3,3.44771525 3.44771525,3 4,3 Z" id="Path-11-Copy" fill="#FFFFFF" fill-rule="nonzero" transform="translate(9.000000, 9.000000) rotate(-225.000000) translate(-9.000000, -9.000000) "></path></g></svg>';
        button.id = 'moveToTopBtn';
        div.appendChild(button);
        var fcdiv = $($(".fragmentContainer")[0]);
        fcdiv.append(div);
        $("#moveToTopBtn").css("background-color","#0072C6");
        $("#moveToTopBtn").click(function(){
            $("#fragmentContainer").animate({
                    scrollTop: 0
            }); 
        });
        
//        $('#moveToTopBtn').hide();

        $("#fragmentContainer").scroll(function(){
        if ($(this).scrollTop() > 10) {
            $('#moveToTopBtn').show().fadeIn();
        } else {
            $('#moveToTopBtn').fadeOut().hide();
        }
    });
    }

    function picklistLoad(controlId){
        document.getElementById("searchBox").focus();
        if(window.parent.onPicklistLoad){
            window.parent.onPicklistLoad(controlId);
        }
    }
        function dynamicManifest()
    {
        var dynamicManifest = {
  "name": "LandMarkBankMobileApp",
  "short_name": "LandMarkBankMobileApp",
  "icons": [{
  "src": window.location.protocol + window.location.host +contextPath+"/pwalogo/icon-72x72.png",
      "sizes": "72x72",
      "type": "image/png"
    },
    {
  "src": window.location.protocol + window.location.host +contextPath+"/pwalogo/icon-96x96.png",
      "sizes": "96x96",
      "type": "image/png"
    },{
    "src": window.location.protocol + window.location.host +contextPath+"/pwalogo/icon-128x128.png",
      "sizes": "128x128",
      "type": "image/png"
    }, {
      "src": window.location.protocol + window.location.host +contextPath+"/pwalogo/icon-144x144.png",
      "sizes": "144x144",
      "type": "image/png"
    }, {
      "src": window.location.protocol + window.location.host +contextPath+"/pwalogo/icon-152x152.png",
      "sizes": "152x152",
      "type": "image/png"
    }, {
      "src": window.location.protocol + window.location.host +contextPath+"/pwalogo/icon-192x192.png",
      "sizes": "192x192",
      "type": "image/png"
    }, {
      "src": window.location.protocol + window.location.host +contextPath+"/pwalogo/icon-384x384.png",
      "sizes": "384x384",
      "type": "image/png"
    }, {
      "src": window.location.protocol + window.location.host +contextPath+"/pwalogo/icon-256x256.png",
      "sizes": "256x256",
      "type": "image/png"
    }, {
      "src": window.location.protocol + window.location.host +contextPath+"/pwalogo/icon-512x512.png",
      "sizes": "512x512",
      "type": "image/png"
    }],
  "start_url": window.location.protocol + window.location.host +contextPath+"/components/viewer/portal/initializePortal.jsp?QueryString="+queryString,
  "display": "standalone",
  "background_color": "#FFFFFF",
  "theme_color": "#0072c6"
}
var stringManifest = JSON.stringify(dynamicManifest);
var blob = new Blob([stringManifest], {type: 'application/json'});
var manifestURL = URL.createObjectURL(blob);
document.querySelector('#dynaimcManifest').setAttribute('href', manifestURL);
    }
    function doInit(loadType,subFormButtonId){
        if (applicationName && applicationName != '')
            sessionTimeChecker() ;
        if(queryString != 'null' && queryString != null && !(queryString=='')){
            if( document.getElementById("dynaimcManifest") != null )
                dynamicManifest();
        }
//        if(mobileMode=="ios"||mobileMode=="android"){
//            if(document.getElementById("moveToTopBtn") == null || document.getElementById("moveToTopBtn") == 'undefined')
//            moveToTop();
//         $('#moveToTopMainForm').hide();
//        }else{
            scrollMainForm();
//        }
        setDocListClass();
        setSliderCss();
        //setDocListCss();
        alignNavigationContainerStyle();
        alignStepsCSS();
        initialiseSlider(0, '');
        updateSlider();
        if(getDeviceType()){
            if($(".fragmentFooter")!=null && $(".fragmentFooter")!=undefined){
            $(".navigationNextBtn").css({'width':'100%'});
            $(".navigationBackBtn").css({'width':'100%'});
            $(".navSavBtn").css({'width':'100%'});
            if($("#footerDropdown").children().length ==0)
            {       
                $("#footerDropdowntoggle").css("display","none");
            }
            else
            {
                $("#footerDropdowntoggle").css("display","block");
            }
            //$(".finishBtn").css({'width':'100%'});
            //$(".navigationContinueBtn").css({"margin-right": "0%","float": "left"});
            //$(".navigationBackBtn").css({"margin-right": "2%", "float": "none"});
            }
        }
        try{
            if(typeof window.parent != 'undefined'){
                if(typeof window.parent.NGF_NotifyDataLoaded != 'undefined'){
                    window.parent.NGF_NotifyDataLoaded();
                }
            }
            }
            catch(ex){}
        
        try{

            $('.textbox').each(function() {
                var max=this.getAttribute("rangemax");
                var min=this.getAttribute("rangemin");
                var minValue = '-999999999999999999.99';
                var controlId = this.getAttribute("id");
                if(this.getAttribute("minvalue")!=null && this.getAttribute("minvalue")!=undefined){
                    minValue=this.getAttribute("minvalue");
                }
                var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
                var precision=typeof this.getAttribute("Precision")=='undefined'?'2':this.getAttribute("Precision");
                var decimal='2';
                if(window.removePrecision){
                    if(removePrecision(this.id)){
                        precision='0';
                    }
                }
				if(typeofvalue =="Text")
                     decimal = (window.allowPrecisionInText)? allowPrecisionInText():'0';
                if(typeofvalue =="Float")
                    decimal=(window.allowPrecisionInFloat && (allowPrecisionInFloat(controlId)<precision))?allowPrecisionInFloat(controlId):precision;
                if(typeofvalue =="Integer")
                    decimal='0';
                if(typeofvalue =="Long")
                    decimal='0';

                if(this.getAttribute("maskingPattern").toString()!='nomasking'&&this.getAttribute("maskingPattern").toString()!=''){
                if(this.getAttribute("maskingPattern").toString()!='currency_rupees' && this.getAttribute("maskingPattern").toString()!=='currency_bahamas' && this.getAttribute("maskingPattern").toString()!=='currency_dollar' && this.getAttribute("maskingPattern").toString()!=='currency_naira' && this.getAttribute("maskingPattern").toString()!=='currency_yen' && this.getAttribute("maskingPattern").toString()!=='currency_euro' && this.getAttribute("maskingPattern").toString()!=='currency_french' && this.getAttribute("maskingPattern").toString()!=='currency_greek' && this.getAttribute("maskingPattern").toString()!=='' && this.getAttribute("maskingPattern").toString()!=='percentage'){
                        var placeholder;
                        if(this.getAttribute("maskingPattern").toString().charAt(this.getAttribute("maskingPattern").toString().length-1)!='$'){
                            if(this.getAttribute("maskingPattern").toString()=='dgroup3' || this.getAttribute("maskingPattern").toString()=='dgroup2'){
                                var digitGroup = parseInt(this.getAttribute("maskingPattern").charAt(this.getAttribute("maskingPattern").length-1));
                                jQuery(this).autoNumeric('init',{
                                    dGroup: digitGroup,
                                    mDec: decimal,
                                    vMin: minValue                              

                                });
                                var cleanValue=getValue(this.id);
                                if(cleanValue!=='')
                                    jQuery(this).autoNumeric('set', cleanValue);
                            }
                            else{
                                if(typeofvalue=='Float'&&this.getAttribute("maskingPattern").toString()=='NZP'){
                                jQuery(this).autoNumeric('init',{
                                    aSep : '',  
                                    aDec: '.', 
                                    mDec: decimal,
                                    aPad: false,
                                    vMin: minValue
                                });
                            }
                            else{
                                placeholder=this.getAttribute("maskingPattern").replace(/[A-Za-z0-9*#]/mg , "_");
                                jQuery(this).mask(this.getAttribute("maskingPattern"), {
                                    placeholder: placeholder
                                }, {
                                    clearIfNotMatch: true
                                });
                                return true;//Bug 79052
                            }
                            }
                        }
                    }

                    else{
                        var asign='';
                        var dgroup='';
                        var psign='p';
                    var adec='.';
                    var asep=',';
                        if(this.getAttribute("maskingPattern").toString()==='currency_rupees'){
                            asign='Rs ';
                            dgroup=2;
                        //                    jQuery(this).autoNumeric('init',{aSign: 'Rs ', dGroup: 2 , vMax: max, vMin: min});
                        }
                        else if(this.getAttribute("maskingPattern").toString()==='currency_dollar'){
                            asign='$ ';
                            dgroup=3;
                        //                        psign='s';
                        //                    jQuery(this).autoNumeric('init',{aSign: ' $', dGroup: 3,pSign: 's' ,vMax: max, vMin: min});
                        }
                        else if(this.getAttribute("maskingPattern").toString()==='currency_naira'){
                        asign='₦ ';
                        dgroup=3;
                        }
                        else if(this.getAttribute("maskingPattern").toString()==='currency_yen'){
                            asign='¥ ';
                            dgroup=3;
                        //                    jQuery(this).autoNumeric('init',{aSign: '¥ ', dGroup: 3, vMax: max, vMin: min});
                        }
                        else if(this.getAttribute("maskingPattern").toString()==='currency_euro'){
                            asign='€ ';
                            dgroup=3;
                        //                    jQuery(this).autoNumeric('init',{aSign: '€ ', dGroup: 3, vMax: max, vMin: min});
                        }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_french'){
//                        asign=' CHF';
                        dgroup=3;
                        adec = ',';
                        asep = ' ';
                        psign= 's';
                    //                    jQuery(this).autoNumeric('init',{aSign: '€ ', dGroup: 3, vMax: max, vMin: min});
                    }
                     else if(this.getAttribute("maskingPattern").toString()==='currency_greek'){
                        dgroup=3;
                        adec = ',';
                        asep = '.';
                        psign= 's';
                    //                    jQuery(this).autoNumeric('init',{aSign: '€ ', dGroup: 3, vMax: max, vMin: min});
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_bahamas'){
                        asign='B$ ';
                        dgroup=3;
                    //                    jQuery(this).autoNumeric('init',{aSign: '€ ', dGroup: 3, vMax: max, vMin: min});
                    }
                        if(this.getAttribute("maskingPattern").toString()!=='percentage' && this.getAttribute("maskingPattern").toString() !=='currency_yen' ){
                            if(max===null)
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign,
                                    mDec: decimal,
                                aNeg:true,
                                aDec: adec,
                                aSep: asep,
                                vMin: minValue
                                });
                            else{
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign, 
                                mDec: decimal,
                                aDec: adec,
                                aSep: asep,
                                vMin: minValue
                                });
                            }
                        }
                        else if(this.getAttribute("maskingPattern").toString() =='currency_yen'){
                            if(max===null)
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign,
                                    mDec: "0",
                                aNeg:true,
                                aDec: adec,
                                aSep: asep,
                                vMin: minValue
                                });
                            else{
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign, 
                                mDec: "0",
                                aDec: adec,
                                aSep: asep,
                                vMin: minValue
                                });
                            }
                        }

                        else{
                            jQuery(this).autoNumeric('init',{
                                aSign: " %", 
                                pSign:'s',
                                mDec: decimal,
                                vMin: minValue
                            });//Bug 81106
                        }
                        var cleanValue=getValue(this.id);
                        if(cleanValue!=='')
                            jQuery(this).autoNumeric('set', cleanValue);
                    }

                }
                if((typeofvalue=='Float' || typeofvalue=='Integer' ||typeofvalue=='Long')  && this.getAttribute("maskingPattern") && this.getAttribute("maskingPattern").toString()=='nomasking'){
                    jQuery(this).autoNumeric('init',{
                                aSep : '',  
                                aDec: '.', 
                                mDec: decimal
                            });
                }
                if(this.hasAttribute("disabled"))
                {
                    this.title=this.value;
                }
            });
        } catch(e){        
        }

        //  $('.tabletextbox').each(function() {
        //     if(this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="" ){
        //         maskfield(this,'input');
        //     }

        // });

                applyFormattingGrid();
//        $('.tabletextbox').each(function()
//        {
//            if(this.getAttribute("maskingpattern")!=null && this.getAttribute("maskingpattern")!=undefined && this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="" )
//            {
//            if(this.getAttribute("maskingPattern").toString()!='currency_rupees' && this.getAttribute("maskingPattern").toString()!=='currency_dollar' && this.getAttribute("maskingPattern").toString()!=='currency_yen' && this.getAttribute("maskingPattern").toString()!=='currency_euro'  && this.getAttribute("maskingPattern").toString()!=='currency_french' && this.getAttribute("maskingPattern").toString()!=='currency_greek' && this.getAttribute("maskingPattern").toString()!=='percentage' && this.getAttribute("maskingPattern").toString()!=='dgroup2' && this.getAttribute("maskingPattern").toString()!=='dgroup3' && this.getAttribute("maskingPattern").toString()!=='email' && this.getAttribute("maskingPattern").toString()!=='NZP')
//                {
//                    var placeholder;
//                    placeholder = this.getAttribute("maskingpattern").replace(/[A-Za-z0-9*#]/mg, "_");
//                    jQuery(this).mask(this.getAttribute("maskingpattern"), {
//                        placeholder: placeholder
//                    }, {
//                        clearIfNotMatch: true
//                    });
//                }
//                else{
//                            maskfield(this,'input');
//            }
//            }
//             var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
//            if(typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking")
//            {
//                maskfield(this,'input');
//            }
//        });
        
        $('.openPickerClass').each(function()
        {
            if(this.getAttribute("maskingPattern")!=null && this.getAttribute("maskingPattern")!=undefined && this.getAttribute("maskingPattern")!="" )
            {
                maskfield(this,'input');
            }
        });

        $('.maskedText').each(function(){
            var digitGroup  = parseInt(this.getAttribute("dgroup"));
            var dec = '0';
            if(jQuery(this).attr('typeofvalue')=='Float')
                dec = jQuery(this).attr('Precision');
            jQuery(this).autoNumeric('init',{
                dGroup: digitGroup,
                mDec: dec
            });
        });

        $('.maskedTotal').each(function(){
            var max=this.getAttribute("rangemax");
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            var precision=typeof this.getAttribute("Precision")=='undefined'?'2':this.getAttribute("Precision");
            var decimal='2';
            if(window.removePrecision){
                    if(removePrecision(this.id)){
                        precision='0';
                    }
                }
                if(typeofvalue =="Float")
                    decimal=precision;
                if(typeofvalue =="Integer")
                    decimal='0';
                if(typeofvalue =="Long")
                    decimal='0';
            if(this.getAttribute("maskingPattern").toString()!='nomasking'&&this.getAttribute("maskingPattern").toString()!=''){
                if(this.getAttribute("maskingPattern").toString()!='currency_rupees' && this.getAttribute("maskingPattern").toString()!=='currency_dollar' && this.getAttribute("maskingPattern").toString()!=='currency_naira' && this.getAttribute("maskingPattern").toString()!=='currency_yen' && this.getAttribute("maskingPattern").toString()!=='currency_euro' && this.getAttribute("maskingPattern").toString()!=='currency_french' && this.getAttribute("maskingPattern").toString()!=='currency_greek' && this.getAttribute("maskingPattern").toString()!=='currency_bahamas' && this.getAttribute("maskingPattern").toString()!=='' && this.getAttribute("maskingPattern").toString()!=='percentage'){
                        var placeholder;
                        if(this.getAttribute("maskingPattern").toString().charAt(this.getAttribute("maskingPattern").toString().length-1)!='$'){
                            if(this.getAttribute("maskingPattern").toString()=='dgroup3' || this.getAttribute("maskingPattern").toString()=='dgroup2'){
                                var digitGroup = parseInt(this.getAttribute("maskingPattern").charAt(this.getAttribute("maskingPattern").length-1));
                                jQuery(this).autoNumeric('init',{
                                    dGroup: digitGroup,
                                    mDec: decimal                                

                                });
                                 if(this.value!==''&&this.value!==undefined)
                                    jQuery(this).autoNumeric('set', this.value);
                            }
                            else{
                                if(typeofvalue=='Float'&&this.getAttribute("maskingPattern").toString()=='NZP'){
                                jQuery(this).autoNumeric('init',{
                                    aSep : '',  
                                    aDec: '.', 
                                    mDec: decimal,
                                    aPad: false
                                });
                            }
                            else{
                                placeholder=this.getAttribute("maskingPattern").replace(/[A-Za-z0-9*#]/mg , "_");
                                jQuery(this).mask(this.getAttribute("maskingPattern"), {
                                    placeholder: placeholder
                                }, {
                                    clearIfNotMatch: true
                                });
                                return true;//Bug 79052
                            }
                            }
                        }
                    }

                    else{
                        var asign='';
                        var dgroup='';
                        var psign='p';
                    var adec='.';
                    var asep=',';
                        if(this.getAttribute("maskingPattern").toString()==='currency_rupees'){
                            asign='Rs ';
                            dgroup=2;
                        //                    jQuery(this).autoNumeric('init',{aSign: 'Rs ', dGroup: 2 , vMax: max, vMin: min});
                        }
                        else if(this.getAttribute("maskingPattern").toString()==='currency_dollar'){
                            asign='$ ';
                            dgroup=3;
                        //                        psign='s';
                        //                    jQuery(this).autoNumeric('init',{aSign: ' $', dGroup: 3,pSign: 's' ,vMax: max, vMin: min});
                        }
                        else if(this.getAttribute("maskingPattern").toString()==='currency_naira'){
                            asign='₦ ';
                            dgroup=3;
                        //                        psign='s';
                        //                    jQuery(this).autoNumeric('init',{aSign: ' $', dGroup: 3,pSign: 's' ,vMax: max, vMin: min});
                        }
                        else if(this.getAttribute("maskingPattern").toString()==='currency_yen'){
                            asign='¥ ';
                            dgroup=3;
                        //                    jQuery(this).autoNumeric('init',{aSign: '¥ ', dGroup: 3, vMax: max, vMin: min});
                        }
                        else if(this.getAttribute("maskingPattern").toString()==='currency_euro'){
                            asign='€ ';
                            dgroup=3;
                        //                    jQuery(this).autoNumeric('init',{aSign: '€ ', dGroup: 3, vMax: max, vMin: min});
                        }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_french'){
//                        asign=' CHF';
                        dgroup=3;
                        adec = ',';
                        asep = ' ';
                        psign= 's';
                    //                    jQuery(this).autoNumeric('init',{aSign: '€ ', dGroup: 3, vMax: max, vMin: min});
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_greek'){
                        dgroup=3;
                        adec = ',';
                        asep = '.';
                        psign= 's';
                    //                    jQuery(this).autoNumeric('init',{aSign: '€ ', dGroup: 3, vMax: max, vMin: min});
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_bahamas'){
                        asign='B$ ';
                        dgroup=3;
                //                    jQuery(this).autoNumeric('init',{aSign: '€ ', dGroup: 3, vMax: max, vMin: min});
                    }
                        if(this.getAttribute("maskingPattern").toString()!=='percentage' && this.getAttribute("maskingPattern").toString() !=='currency_yen' ){
                            if(max===null)
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign,
                                    mDec: decimal,
                                aNeg:true,
                                aDec: adec,
                                aSep: asep
                                });
                            else{
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign, 
                                mDec: decimal,
                                aDec: adec,
                                aSep: asep
                                });
                            }
                        }
                        else if(this.getAttribute("maskingPattern").toString() =='currency_yen'){
                            if(max===null)
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign,
                                    mDec: "0",
                                aNeg:true,
                                aDec: adec,
                                aSep: asep
                                });
                            else{
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign, 
                                mDec: "0",
                                aDec: adec,
                                aSep: asep
                                });
                            }
                        }

                        else{
                            jQuery(this).autoNumeric('init',{
                                aSign: " %", 
                                pSign:'s',
                                mDec: decimal
                            });//Bug 81106
                        }
                       if(this.value!=='' && this.value != undefined)
                            jQuery(this).autoNumeric('set', this.value);
                    }

                }
        });
        setWidthForTabStyle4();
        $('.iformTabUL.scrollingTabCSS').each(function(){
            $(this).removeClass("scrollingTabCSS");
            $(this).scrollingTabs({
                disableScrollArrowsOnFullyScrolled :true,
                enableRtlSupport :true,
                enableSwiping:true

            });
        });

        attachDatePicker();    
        initFloatingMessagesForPrimitiveFields('.errorMessageHoverDiv');
        initFloatingMessagesForTableCells();
        initFloatingMessagesForPrimitiveFields('.controlCustomCss');
        $(document).ready(function() {
            var $input = $('.form-input');

            var $textarea = $('.form-textarea');
            var $combo = $('select');
            if($input.attr("datatype")=="date"){
                $(this).focus({

                    })
            }
            $input.focusout(function() {
                if($(this).val().length > 0) {
                    $(this).addClass('input-focus');
                    $(this).next('.form-label').addClass('input-focus-label');
                }
                else {
                    $(this).removeClass('input-focus');
                    $(this).next('.form-label').removeClass('input-focus-label');

                }
            });


            $textarea.focusout(function() {
                if($(this).val().length > 0) {
                    $(this).addClass('textarea-focus');
                    $(this).next('.form-label').addClass('textarea-focus-label');
                }
                else {
                    $(this).removeClass('textarea-focus');
                    $(this).next('.form-label').removeClass('textarea-focus-label');

                }
            });
            $('.mdb-select').multiselect({
                buttonWidth: '100%',
                includeSelectAllOption: true
            });
            setListBoxStyle(); 
            $(document).bind("contextmenu", function(e) {
                if(!$(e.target).is('input') && !$(e.target).is('textarea')){
                    return false;
                }
            });
        });


        if(!isListviewOpened){
        /*    $('.richtexteditor').each(function() {
                $( this ).Editor();           
            });*/
        }

        var editors = document.getElementsByClassName("Editor-editor");

        for(var i=0;i<editors.length;i++){

            editors[i].onblur = function(){
                var txtobj = jQuery(this.parentNode.parentNode).find("[datatype='textarea']");
                txtobj.val(txtobj.Editor("getText"));

                ctrOnchangeHandler(txtobj.get(0),1);
            };       

        }
      
        setFrameScroll();
        if(loadType==="form" && applicationName && applicationName!='')
        checkForExistingApplication();
        

        setTimeout(function(){
         var scrollerWidthofBrowser = getBrowserScrollSize().width;
        //Bug 77001 Start
        if(document.getElementById("affix_padding") && document.getElementById("headerDiv")){
            if(parseInt(window.innerHeight)>parseInt(document.getElementById("headerDiv").clientHeight*3)){
                document.getElementById("affix_padding").style.paddingTop= (parseInt(document.getElementById("headerDiv").clientHeight))+"px";
                if(document.getElementById("headerDiv").style.position!='fixed'){
                    document.getElementById("headerDiv").style.position='fixed';
                    document.getElementById("headerDiv").style.top='0';
                    document.getElementById("headerDiv").style.width=($(window).width() - scrollerWidthofBrowser )+"px";
                    }
            }
            else{
                document.getElementById("headerDiv").style.position='';
                document.getElementById("headerDiv").style.top='';
        //                    document.getElementById("headerDiv").classList.remove("affix");
                document.getElementById("headerDiv").style.width=($(window).width() - scrollerWidthofBrowser )+"px";
            }
        }},200);
        //Bug 77001 End

        $('.listviewlabel').each(function() {
            
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'savedlabel');
            }


        });

        $('.totalLabel').each(function() {
            
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'savedlistviewlabel');
            }

        });

//            document.addEventListener(
//            'scroll',
//            function(event){
//                jQuery('.myjquerydatepicker').datetimepicker('hide');
//                jQuery('.myjquerydatetimepicker').datetimepicker('hide');
////                jQuery('.mydatepicker').datetimepicker1('hide');
////                jQuery('.mydatetimepicker').datetimepicker1('hide');
//            },
//            true 
//            );  

        if ( mobileMode=="ios" ){
            var inputs = jQuery("input");
            $(document).on('touchstart','body', function (evt) {
                var targetTouches = event.targetTouches;
                if (!inputs.is(targetTouches)){
                    inputs.context.activeElement.blur();
                }
            });
        }
         window.addEventListener('keydown', function (e) {
            if(document.getElementById("searchModal")!=null&&document.getElementById("searchModal").className==="modal in"){
                 var evtObj = window.event || e;
                 var keycode = evtObj.keyCode || evtObj.which;
                if(keycode=="40" || keycode=="38"){
                    cancelBubble(e);
                    togglePicklistRow(e,false);     
                }
                if(keycode=="9"){
                    cancelBubble(e);
                    handleTabKeyEvent(false);
                }
            }
        });
        
         makeStickyTabs();
         moveScroller();//Bug 85154
         
         if(typeof window.parent != 'undefined'){
             try{
                 if(typeof window.parent.formPopulatedEx != 'undefined')
                    window.parent.formPopulatedEx(window);
             } catch(e){                 
             }
         }
         setTileHeight();
         if(loadType!=null && loadType!=undefined && loadType=="form"){
            executeLoadEvents('1');
            if(window.formLoad)
                formLoad();
           // RemoveIndicator("application");
        }
        if(loadType!=null && loadType!=undefined && loadType=="fragment"){
            if(window.fragmentload)
             fragmentload(subFormButtonId);
        }
        if(loadType!=null && loadType!=undefined && loadType=="subForm"){
            try{
                if( window.opener && window.opener.document.getElementById("rid_IfHandler") != null)
                    window.opener.document.getElementById("rid_IfHandler").value = document.getElementById("rid_IfHandler").value;
                if( window.opener && window.opener.document.getElementById("rid_Action") != null)
                    window.opener.document.getElementById("rid_Action").value = document.getElementById("rid_Action").value;
                if( window.opener && window.opener.document.getElementById("rid_ActionAPI") != null)
                    window.opener.document.getElementById("rid_ActionAPI").value = document.getElementById("rid_ActionAPI").value;
                if( window.opener && window.opener.document.getElementById("rid_listviewmodal") != null)
                    window.opener.document.getElementById("rid_listviewmodal").value = document.getElementById("rid_listviewmodal").value;  
                if( window.opener &&  window.opener.document.getElementById("rid_advancelistviewmodal")!=null )
                    window.opener.document.getElementById("rid_advancelistviewmodal").value = document.getElementById("rid_advancelistviewmodal").value;
                if( window.opener && window.opener.document.getElementById("rid_picklistview") != null)
                    window.opener.document.getElementById("rid_picklistview").value = document.getElementById("rid_picklistview").value;
                if( window.opener && window.opener.document.getElementById("rid_webservice")!=null )
                    window.opener.document.getElementById("rid_webservice").value = document.getElementById("rid_webservice").value;
                if( window.opener && window.opener.document.getElementById("rid_appTask")!=null)
                    window.opener.document.getElementById("rid_appTask").value = document.getElementById("rid_appTask").value;
            }
            catch(ex){

            }
            executeLoadEvents('2',subFormButtonId);
            if(window.subFormLoad)
                subFormLoad(subFormButtonId);
            //RemoveIndicator("application");
        }
         if(window.enableNavigationStepClickHook)
        {
           var enableNavigationStepClick = false;
           enableNavigationStepClick = enableNavigationStepClickHook();
           if(enableNavigationStepClick){
               enableNavigationOnStepClick();
           } else {
               disableNavigationStepClick();
           }
        }
		RemoveIndicator("application");
         }
    function setTileHeight(){
        var maxTileHeight = 0;
        var getHeight = 0;
        var getWidth = 0;
        if( $('.card-image').css('width') != undefined ){
        getWidth = parseInt($('.card-image').css('width').split('px')[0]);
        $('.card-image').each(function(i,elem){
            if($(elem).find('img').height() > 170 || $(elem).find('img').width() > getWidth || getDeviceType()){
                $(elem).css('background-size' , 'contain' );
            }
        });
        }
        if($('.tilePointsHeight') != undefined && $('.tile')!= undefined) {
        $('.tilePointsHeight').each(function(i,elem){
        getHeight =  parseInt($(elem).css('height').split('px')[0]);
        if(getHeight>maxTileHeight){
        maxTileHeight = getHeight;
        }
        });
        $('.tilePointsHeight').each(function(i,elem){
         if(maxTileHeight != 0)
         $(elem).css('height',maxTileHeight);
        });
        maxTileHeight = 0;
        getHeight = 0;
        $('.tile').each(function(i,elem){
        getHeight = parseInt($(elem).css('height').split('px')[0]);
        if(getHeight>maxTileHeight){
        maxTileHeight = getHeight;
        }
        });
        $('.tile').each(function(i,elem){
        if(maxTileHeight != 0)
        $(elem).css('height',maxTileHeight);
         if($(elem).hasClass("tileInvisibleOnMobile"))
             $(elem).parent().css('display',"none");
         $(elem).parent().css('height',maxTileHeight+30);
        });
        }
        if(enableMobileViewForTiles && getDeviceType()){
            tileSwiping();
        }
        setDocListClass();
    }
    function togglePicklistRow(e,isPicklistScope){
        var evtObj = window.event || e;
        var keycode = evtObj.keyCode || evtObj.which;
        var dataTable;
        if(isPicklistScope)
            dataTable = document.getElementById("myTable");
        else{
            try{
                dataTable = window.frames["iFrameSearchModal"].contentWindow.document.getElementById("myTable");
            }
            catch(ex){
                dataTable = window.frames["iFrameSearchModal"].document.getElementById("myTable"); 
            }
        }

        var dataTableRows = dataTable.tBodies[0].getElementsByTagName("tr");
        var selectedRowIndex=0;
        for(var i=0;i<dataTableRows.length;i++){
            if(dataTableRows[i].classList.contains("info")){
                selectedRowIndex=i;
                break;
            }
        }
        if(keycode=="40"){
            if(selectedRowIndex<dataTableRows.length-1){
                dataTableRows[selectedRowIndex].classList.remove("info");
                dataTableRows[selectedRowIndex+1].classList.add("info");
            }
        }
        else if(keycode=="38"){
            if(selectedRowIndex>0){
                dataTableRows[selectedRowIndex].classList.remove("info");
                dataTableRows[selectedRowIndex-1].classList.add("info");
            }
        }
    }
    
    function handleTabKeyEvent(isPicklistScope){
        var picklistOkBtn;
        var picklistCancelBtn;
        var picklistNextBtn = document.getElementById("picklistNext");
        var picklistPreviousBtn = document.getElementById("picklistPrevious");
        var columnList;
        var searchBox;
        if(isPicklistScope){
            try{
                picklistOkBtn = window.parent.frames["iFrameSearchModal"].parentNode.parentNode.parentNode.getElementsByClassName("btn-success")[0]
                picklistCancelBtn = window.parent.frames["iFrameSearchModal"].parentNode.parentNode.parentNode.getElementsByClassName("btn-danger")[0];
                columnList = document.getElementById("selectedColumn");
                searchBox = document.getElementById("searchBox");
            }
            catch(ex){
                picklistOkBtn = window.parent.document.getElementById("picklistOk");
                picklistCancelBtn = window.parent.document.getElementById("picklistOk");
                columnList = document.getElementById("selectedColumn");
                searchBox = document.getElementById("searchBox");
            }
        }
        else{
            picklistOkBtn = document.getElementById("picklistOk");
            picklistCancelBtn = document.getElementById("picklistCancel");
            if(picklistOkBtn==null || picklistOkBtn==undefined){
                picklistOkBtn = window.frames["iFrameSearchModal"].parentNode.parentNode.parentNode.getElementsByClassName("btn-success")[0];
            }
            if(picklistCancelBtn==null || picklistCancelBtn==undefined){
                picklistCancelBtn = window.frames["iFrameSearchModal"].parentNode.parentNode.parentNode.getElementsByClassName("btn-danger")[0];
            }
            try{
            columnList=window.frames["iFrameSearchModal"].contentWindow.document.getElementById("selectedColumn");
            searchBox=window.frames["iFrameSearchModal"].contentWindow.document.getElementById("searchBox");
            }
            catch(ex){
                columnList=window.frames["iFrameSearchModal"].document.getElementById("selectedColumn");
                searchBox=window.frames["iFrameSearchModal"].document.getElementById("searchBox");
            }
        }
       
        if(document.activeElement.id=="" || document.activeElement.id=="searchModal" || document.activeElement.tagName=="BODY"){
            picklistOkBtn.focus();
        }
        else if(document.activeElement==picklistOkBtn){
            picklistCancelBtn.focus();
        }
        else if(document.activeElement==picklistCancelBtn){
            if(picklistNextBtn.disabled==false){
                picklistNextBtn.focus();
            }
            else{
                if(picklistPreviousBtn.disabled==false){
                    picklistPreviousBtn.focus();
                }
                else{
                    columnList.focus();
                }
            }
        }
        else if(document.activeElement==picklistNextBtn){
            if(picklistPreviousBtn.disabled==false){
                picklistPreviousBtn.focus();
            }
            else{
                columnList.focus();
            }
        }
        else if(document.activeElement==picklistPreviousBtn){
            columnList.focus();
        }
        else if(document.activeElement==columnList){
            searchBox.focus();
        }
        else if(document.activeElement==searchBox){
            picklistOkBtn.focus();
        }
}
    function showSelectedRow(){
        var picklistTable;
        try{
            picklistTable = window.parent.frames["iFrameSearchModal"].contentWindow.document.getElementById("myTable");
        }
        catch(ex){
            if(window.parent.frames["iFrameSearchModal"]!=null && window.parent.frames["iFrameSearchModal"]!=undefined){
                if(window.parent.frames["iFrameSearchModal"].document!=null && window.parent.frames["iFrameSearchModal"].document!=undefined){
                    picklistTable = window.parent.frames["iFrameSearchModal"].document.getElementById("myTable");
                }
            else{
                    picklistTable = window.parent.frames["iFrameSearchModal"].contentWindow.document.getElementById("myTable");
                }
            }
            else{
                if(window.frames["iFrameSearchModal"].document!=null && window.frames["iFrameSearchModal"].document!=undefined){
                    picklistTable = window.frames["iFrameSearchModal"].document.getElementById("myTable");
                }
                else{
                    picklistTable = window.frames["iFrameSearchModal"].contentWindow.document.getElementById("myTable");
                }
                 
            }
            
        }
        var firstRowRef = picklistTable.tBodies[0].getElementsByTagName("tr")[0];
        firstRowRef.click();
    }
    
    function makeStickyTabs(isListView){
    var headerDiv = document.getElementById("headerDiv");
    var tabLists = document.getElementsByClassName("fixed-tabmenu");
    if(isListView)
        tabLists = document.getElementById("iFrameAdvancedListViewModal").getElementsByClassName("fixed-tabmenu");
    if(document.getElementById("affix_padding") && headerDiv){
        if(parseInt(window.innerHeight)>parseInt(headerDiv.clientHeight*3)){
            for(var i=0;i<tabLists.length;i++){
                if(tabLists[i].classList.contains("listviewTab"))
                    tabLists[i].style.top = "0px";
                else
                    tabLists[i].style.top = (headerDiv.clientHeight-5)+"px";
            }
        }
        else{
            for(var i=0;i<tabLists.length;i++){
                tabLists[i].style.top = "0px";
            }
        }
    }
    else{
        for(var i=0;i<tabLists.length;i++){
            tabLists[i].style.top = "0px";
        }
    }
    
}

    function listViewInit(controlId,action){//Bug 80908
        $('.tableControl.textbox').each(function() {
            var max=this.getAttribute("rangemax");
            var min=this.getAttribute("rangemin");
            var controlId = this.getAttribute("id");
             var minValue = '-999999999999999999.99';
             if(this.getAttribute("minvalue")!=null && this.getAttribute("minvalue")!=undefined){
                minValue=this.getAttribute("minvalue");
            }
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            var precision=typeof this.getAttribute("Precision")=='undefined'?'2':this.getAttribute("Precision");
            var decimal='2';
            if(typeofvalue =="Text")
                 decimal = (window.allowPrecisionInText)? allowPrecisionInText():'0';
            if(typeofvalue =="Float")
                decimal=(window.allowPrecisionInFloat && (allowPrecisionInFloat(controlId)<precision))?allowPrecisionInFloat(controlId):precision;
            if(typeofvalue =="Integer")
                decimal='0';
            if(typeofvalue =="Long")
                decimal='0';

            if(this.getAttribute("maskingPattern").toString()!='nomasking'){
            if(this.getAttribute("maskingPattern").toString()!='currency_rupees' && this.getAttribute("maskingPattern").toString()!=='currency_dollar' && this.getAttribute("maskingPattern").toString()!=='currency_naira' && this.getAttribute("maskingPattern").toString()!=='currency_yen' && this.getAttribute("maskingPattern").toString()!=='currency_euro' && this.getAttribute("maskingPattern").toString()!=='currency_french' && this.getAttribute("maskingPattern").toString()!=='currency_bahamas' &&  this.getAttribute("maskingPattern").toString()!=='currency_greek' && this.getAttribute("maskingPattern").toString()!=='' && this.getAttribute("maskingPattern").toString()!=='percentage'){
                    var placeholder;
                    if(this.getAttribute("maskingPattern").toString().charAt(this.getAttribute("maskingPattern").toString().length-1)!='$'){
                        if(this.getAttribute("maskingPattern").toString()=='dgroup3' || this.getAttribute("maskingPattern").toString()=='dgroup2'){
                            var digitGroup = parseInt(this.getAttribute("maskingPattern").charAt(this.getAttribute("maskingPattern").length-1));
                            jQuery(this).autoNumeric('init',{
                                dGroup: digitGroup,
                                mDec: decimal,
                                vMin: minValue                               

                            });
                        }
                        else{
                        if(typeofvalue=='Float' && this.getAttribute("maskingPattern") && this.getAttribute("maskingPattern").toString()=='NZP'){
                            jQuery(this).autoNumeric('init',{
                                aSep : '',  
                                aDec: '.', 
                                mDec: decimal,
                                aPad: false,
                                vMin: minValue
                            });
                        }
                        else{
                            placeholder=this.getAttribute("maskingPattern").replace(/[A-Za-z0-9*#]/mg , "_");
                            jQuery(this).mask(this.getAttribute("maskingPattern"), {
                                placeholder: placeholder
                            }, {
                                clearIfNotMatch: true
                            });
                            return true;//Bug 79052
                        }
                    }
                    }
                }

                else{
                    var asign='';
                    var dgroup='';
                    var psign='p';
                var adec='.';
                var asep=',';
                    if(this.getAttribute("maskingPattern").toString()==='currency_rupees'){
                        asign='Rs ';
                        dgroup=2;
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_dollar'){
                        asign='$ ';
                        dgroup=3;
                    }
                     else if(this.getAttribute("maskingPattern").toString()==='currency_naira'){
                            asign='₦ ';
                            dgroup=3;
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_yen'){
                        asign='¥ ';
                        dgroup=3;
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_euro'){
                        asign='€ ';
                        dgroup=3;
                    }
                else if(this.getAttribute("maskingPattern").toString()==='currency_french'){
//                    asign=' CHF';
                    dgroup=3;
                    adec = ',';
                    asep = ' ';
                    psign= 's';
                }
                else if(this.getAttribute("maskingPattern").toString()==='currency_bahamas'){
                        asign='B$ ';
                        dgroup=3;
                }
                else if(this.getAttribute("maskingPattern").toString()==='currency_greek'){
                    dgroup=3;
                    adec = ',';
                    asep = '.';
                    psign= 's';
                }
                    if(this.getAttribute("maskingPattern").toString()!=='percentage' && this.getAttribute("maskingPattern").toString() !=='currency_yen' ){
                        if(max===null)
                            jQuery(this).autoNumeric('init',{
                                aSign: asign, 
                                dGroup: dgroup,
                                pSign:psign,
                                mDec: decimal,
                            aNeg:true,
                            aDec: adec,
                            aSep: asep,
                            vMin: minValue
                            });
                        else{
                            jQuery(this).autoNumeric('init',{
                                aSign: asign, 
                                dGroup: dgroup,
                                pSign:psign, 
                            mDec: decimal,
                            aDec: adec,
                            aSep: asep,
                            vMin: minValue
                            });

                        }
                    }
                     else if(this.getAttribute("maskingPattern").toString() =='currency_yen'){
                            if(max===null)
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign,
                                    mDec: "0",
                                aNeg:true,
                                aDec: adec,
                                aSep: asep,
                                vMin: minValue
                                });
                            else{
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign, 
                                mDec: "0",
                                aDec: adec,
                                aSep: asep,
                                vMin: minValue
                                });
                            }
                        }
                    else
                        jQuery(this).autoNumeric('init',{
                            aSign: " %", 
                            pSign:'s',
                            mDec: decimal,
                            vMin: minValue
                        });
                }

            }
            if(typeofvalue=='Float' && this.getAttribute("maskingPattern") && this.getAttribute("maskingPattern").toString()=='nomasking'){
            jQuery(this).autoNumeric('init',{
                aSep : '',  
                aDec: '.', 
                mDec: decimal,
                vMin: minValue
            });
        }

            //if(this.value!=='')
              //  jQuery(this).autoNumeric('set', this.value)
        });
        initFloatingMessagesForPrimitiveFields('.errorMessageHoverDiv.tableControlDiv');
        initFloatingMessagesForTableCells();
        $('.tableControl.maskedText').each(function(){
            var digitGroup  = parseInt(this.getAttribute("dgroup"));
            var dec = '2';
            if(jQuery(this).attr('typeofvalue')=='Float')
                dec = jQuery(this).attr('Precision');
            jQuery(this).autoNumeric('init',{
                dGroup: digitGroup,
                mDec: dec
            });
        });
        
        $('.openPickerClass').each(function()
        {
            if(this.getAttribute("maskingPattern")!=null && this.getAttribute("maskingPattern")!=undefined && this.getAttribute("maskingPattern")!="" )
            {
                maskfield(this,'input');
            }
        });
        
        attachDatePicker();
        executeLoadEvents('3',controlId);
        clearComponentMap("listview");
        clearServerComponentMap("listview");
        disableListViewControls(controlId);
        listViewLoad(controlId,action);//Bug 80908
       
    }

    function modifyClass(ref){
        document.getElementById(ref.id+"_label").classList.add('input-focus-label');
    }

    function openDatePicker(controlId){
        if($('#'+controlId).data("DateTimePicker") !=undefined){
            $('#'+controlId).data("DateTimePicker").show();
            attachDateRange(document.getElementById(controlId));
        }
        else {
            $('#'+controlId).datetimepicker('show');
            attachDateRange(document.getElementById(controlId));
        }
    }

    function openTableDatePicker(ref){
        //Bug 76652 Start
        if($($(ref).parent().parent().children().first()).data("DateTimePicker") !=undefined){
            $($(ref).parent().parent().children().first()).data("DateTimePicker").show();
            //attachDateRange($($(ref).parent().parent().children().first()));
        }
        else{
            $($(ref).parent().parent().children().first()).datetimepicker('show');
            //attachDateRange($($(ref).parent().parent().children().first()));
        }
        //Bug 76652 End
        //$($(ref).parent().parent().children().first()).datepicker().data('datepicker').show();
    }

    function validateEmail(ref) {
        //var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        var re= /^[\w!#$%&’*+/=?`{|}~^-]+(?:\.[\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}$/
        if(!re.test(ref.value) && ref.value!='')
            {
                var key = ref.getAttribute("dataClass");//Bug 89678 Start
                if(key==="")
                    key = ref.name;
                if(key==="")
                    key = ref.id;
                var validateMapKey=ref.id;
                if( ref.getAttribute("type") && (ref.getAttribute("type")==='text' || ref.getAttribute("type")==='email')){
                    ComponentValidatedMap[validateMapKey]=false;//Bug 83970
                    valueChanged=false;                        		
                }//Bug 89678 End
                jQuery("#"+ref.id+"_patternMsg").text(INVALID_EMAIL);
                toggleErrorTooltip(ref,null,document.getElementById(ref.id+"_patternMsg"),false,1);
                //jQuery("#"+ref.id+"_patternMsg").css("display","block");
                return false;    
            }else
            {
                delete ComponentValidatedMap[ref.id];
                toggleErrorTooltip(ref,null,document.getElementById(ref.id+"_patternMsg"),false,0);
                //jQuery("#"+ref.id+"_patternMsg").css("display","none");
                return true;
            }

    }

    function tablevalidateEmail(ref) {
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        if(!re.test(ref.value) && ref.value!='')
            {
                ref.value="";
                return false;    
            }else
            {
                return true;
            }

    }

    //function validatePhone(ref){
    //   var re = /^\(\d{2}\) \d{10}$/;
    //   if(ref.value!='' && !re.test(ref.value))
    //        {
    //            jQuery("#"+ref.id+"_patternMsg").text(INVALID_PHONE);
    //            jQuery("#"+ref.id+"_patternMsg").css("display","block");
    //            ref.value="";
    //            return false;    
    //        }else
    //        {
    //            jQuery("#"+ref.id+"_patternMsg").css("display","none");
    //            return true;
    //        }
    //}

    function setModifiedData(ref,controlId){
        var rowIndex = $(ref).closest('tr').index();
        var colIndex = parseInt($(ref).closest('td').index())-1;
        var type=jQuery(ref).attr("datatype");
        //Bug 75125
        if(!validateTableCellValue(ref,controlId,colIndex))
        {
            ComponentValidatedMap[controlId]=false;
            //Bug 88289 
            cancelBubble(event);
            setTimeout(function() { 
                ref.focus();     
            }, 300);
                  
            return;        
        }
        else{
            delete ComponentValidatedMap[controlId];
        }
        setTableModifiedFlag(controlId);
        var cellValue = ref.value;
        if(ref.className.indexOf("dgroup")!=-1){
           cellValue = jQuery(ref).autoNumeric('get');
        }
        if(ref.getAttribute("maskingPattern")!=null && ref.getAttribute("maskingPattern")!=undefined && ref.getAttribute("maskingPattern")!='' && ref.getAttribute("maskingPattern")!='nomasking' && ref.getAttribute("maskingPattern")!='email'){
    	if(ref.getAttribute("maskingPattern").toString()==='currency_rupees' || ref.getAttribute("maskingPattern").toString()==='currency_dollar' || ref.getAttribute("maskingPattern").toString()==='currency_naira' || ref.getAttribute("maskingPattern").toString()==='currency_yen' || ref.getAttribute("maskingPattern").toString()==='currency_euro' || ref.getAttribute("maskingPattern").toString()==='currency_french' || ref.getAttribute("maskingPattern").toString()==='currency_greek' || ref.getAttribute("maskingPattern").toString()==='currency_bahamas'  || ref.getAttribute("maskingPattern").toString()==='percentage'|| ref.getAttribute("maskingPattern").toString()==='dgroup2'|| ref.getAttribute("maskingPattern").toString()==='dgroup3'||ref.getAttribute("maskingPattern").toString()==='NZP')
                    cellValue = jQuery(ref).autoNumeric('get');
            else
            {
                 if(ref.getAttribute("controltype") != "date")
                    cellValue = jQuery(ref).cleanVal();
                 else{
                    validateDateValue(ref);
                    cellValue = ref.value;
                }
            }
                    
        }
        $(ref).attr("title",ref.value);
        var requestString="";
        var url = "action.jsp";
        requestString=  "controlId="+encode_utf8(controlId) +"&rowIndex="+rowIndex+"&colIndex="+colIndex +"&cellData=" + encode_utf8(cellValue) + "&modifyFlag=yes";  
        var contentLoaderRef = new net.ContentLoader(url, listviewResponseHandler, ajaxFormErrorHandler, "POST", requestString, false);
        if(window.onTableCellChange){//Bug 84437 Start
            onTableCellChange(rowIndex,colIndex,ref,controlId);
        }//Bug 84437 End
    }

    function checkDuplicates(ref){
        var duplicates = false;
        if(ref.className.indexOf("noDuplicate")!=-1){
            var contents = {};
            var refclass = ref.className.substring(ref.className.indexOf("noDuplicate"));
            var tableCells = document.getElementsByClassName(refclass);
            if(ref.type=="radio"){
                for(var i=0;i<tableCells.length;i++){
                    var tdContent = tableCells[i].value.toLowerCase();
                    if (tableCells[i].checked && contents[tdContent]==true) {
                        duplicates = true;
                        break;
                    }
                    if(tableCells[i].checked)
                        contents[tdContent] = true;

                }
            } else {
                for(var i=0;i<tableCells.length;i++){
                    var tdContent = tableCells[i].value.toLowerCase();
                    if (tdContent!="" && contents[tdContent]==true) {
                        duplicates = true;
                        break;
                    }
                    contents[tdContent] = true;

                }
            }
            if (duplicates){
                showSplitMessage(ref,"Duplicate values not allowed",DATA_TITLE,"error");
                //            $('.ui-dialog-titlebar-close').html("X");
                //            document.getElementById("pnlDialog").overflow = "unset !important";
                if(ref.type=="radio")
                    ref.checked = false;
                else
                    ref.value = "";

                return true;
            }
            else{
                return false;
            }
        }
    }

    function getTextBox(ref,controlId){
        if(window.formChangeHook)
            formChangeHook(ref);
        var txtboxvalue = ref.textContent;
        //    var txtbox = document.createElement("input");
        //    txtbox.value = txtboxvalue.trim();
        //    ref.innerHTML = "";
        //    txtbox.style.width="100%";
        //    txtbox.style.paddingRight = "0px";
        //    txtbox.style.paddingLeft = "0px";
        //    txtbox.className = "inputStyle form-control1";
        //    txtbox.style.height="100%";
        //        txtboxvalue = this.value;
        //        var txtboxparent = this.parentNode;
        //        txtboxparent.innerHTML = txtboxvalue;
        var isValid=true;
        if(checkDuplicates(ref) || (ref.getAttribute("maskingpattern")!=null && ref.getAttribute("maskingpattern")!=undefined && ref.getAttribute("maskingpattern")=="email" && !tablevalidateEmail(ref)))
        {
            isValid=false;
        }
        if(isValid){
            setModifiedData(ref,controlId); 
        }
    //    return txtbox; 
    }

    function getTextArea(ref,controlId){
        if(window.formChangeHook)
            formChangeHook(ref);
        var txtAreaValue = ref.textContent;
        //    var txtArea = document.createElement("textarea");
        //    txtArea.value = txtAreaValue.trim();
        //    ref.innerHTML = "";
        //    txtArea.style.width="100%";
        //    txtArea.style.paddingRight = "0px";
        //    txtArea.style.paddingLeft = "0px";
        //    txtArea.className = "inputStyle form-control1";
        //    txtArea.style.height="100%";
        //    ref.onblur = function(){
        //        txtAreaValue = this.value;
        //        var txtAreaParent = this.parentNode;
        //        
        //        txtAreaValue.innerHTML = txtAreaValue;
        var isDuplicate = checkDuplicates(ref);
        if(!isDuplicate){
            setModifiedData(ref,controlId); 
        }

    //    };
    //    return txtArea;
    }

    function getLabel(ref,controlId){
        var labelValue = "label";
        //    var label = document.createElement("label");
        //    label.value = labelValue.trim();
        //    ref.innerHTML = "";
        //    label.className = "control-label labelStyle fmarginbottom0";
        //    label.style.height="100%";
        //    label.style.background = "inherit";
        //    label.innerHTML = "label";
        //    ref.onblur = function(){
        //        labelValue = this.value;
        //        var labelParent = this.parentNode;
        //        
        //        labelValue.innerHTML = labelValue;
        setModifiedData(ref,controlId)
    //    }
    //    return label; 
    }

    function getButton(ref,controlId){
        var buttonValue = "button";
        //    var button = document.createElement("button");
        //    button.value = buttonValue.trim();
        //    ref.innerHTML = "";
        //    button.className = "btn btn-sm";
        //    button.innerHTML = "button";
        //    ref.onblur = function(){
        //        buttonValue = this.value;
        //        var buttonParent = this.parentNode;
        //        
        //        buttonValue.innerHTML = buttonValue;
        setModifiedData(ref,controlId)
        //    }
        return button;
    }

    function getCheckbox(ref,controlId){
        if(window.formChangeHook)
            formChangeHook(ref);
        //    var checkboxValue = "checkbox";
        ref.value=ref.checked;
        setModifiedData(ref,controlId);
    }

    function getRadio(ref,controlId){
        if(window.formChangeHook)
            formChangeHook(ref);
        //    var radioValue = "radio";
        ref.value=ref.checked;
        var isDuplicate = checkDuplicates(ref);
        if(!isDuplicate){
            setModifiedData(ref,controlId); 
        }
    }
    
    function getRadioGroup(ref,controlId){
        if(window.formChangeHook)
            formChangeHook(ref);
        changeToggleColorInTable(ref);
        var isDuplicate = checkDuplicates(ref);
        if(!isDuplicate){
            setModifiedData(ref,controlId); 
        }
    }
    
    function changeToggleColorInTable(ref) {
      if (ref.parentElement) {
          if (jQuery(ref.parentElement).hasClass('radioThree')) {
              if (ref.parentElement.parentElement) {
                  var parentDiv = ref.parentElement.parentElement;
                  for (var i = 0; i < parentDiv.childElementCount; i++) {
                      var c = parentDiv.children[i];
                      if (jQuery(c).hasClass('active')) {
                          $(c).attr("style", "color:#ffffff !important");
                      } else {
                          c.removeAttribute('style', 'color');
                      }
                  }
              }
          }

      }
    }

    var lastJQueryTS = 0   
    function getDatePicker(ref,controlId){
        var send = true;
        if (typeof(event) === 'object'){
            if (event.timeStamp - lastJQueryTS < 300){
                send = false;
            }
            lastJQueryTS = event.timeStamp;
        }
        if( send )
        {
            if(window.formChangeHook)
                formChangeHook(ref);
            validateDateValue(ref);
            var txtboxvalue = ref.textContent;
            var isDuplicate = checkDuplicates(ref);
            if(!isDuplicate){
                setModifiedData(ref,controlId); 
            }
        }
    //setModifiedData(ref,controlId);
    //    var datePicker = document.createElement("input");
    //    datePicker.value = txtboxvalue.trim();
    //    ref.innerHTML = "";
    //    datePicker.style.width="100%";
    //    datePicker.style.paddingRight = "0px";
    //    datePicker.style.paddingLeft = "0px";
    //    datePicker.className = "form-control mydatepicker inputStyle fpadding3";
    //    datePicker.style.height="100%";
    //    datePicker.onkeydown = "return false";
    //    datePicker.id = "datepick";
    //    datePicker.type = "text";
    //    datePicker.readOnly='true'


    //    ref.onblur = function(){
    //        ref = this.value;
    //        var radioParent = this.parentNode;
    //        setModifiedData(ref,controlId)
    //    }
    //    ref.onclick = function() {
    //    $(".mydatepicker").datepicker(
    //    {
    //        autoclose: true, 
    //        todayHighlight: true, 
    //        disableTouchKeyboard: true,
    //        clearBtn: true
    //    });
    //    }
    //    return datePicker;
    }

    function getComboBox(ref,controlId){
        if(window.formChangeHook)
            formChangeHook(ref);
        //   var selectedItem = selectCtrl.options[selectCtrl.selectedIndex];
        ref.title = ref.options[ref.selectedIndex].label;
        var isDuplicate = checkDuplicates(ref);
        if(!isDuplicate){
            setModifiedData(ref,controlId); 
        }
    }

    function createTextBox(ref,createTextBox,controlId){  
        if( ref.children.length == 0 ){
            if(createTextBox == 1 ) {
                var txtbox = getLabel(ref,controlId);
            }
            if(createTextBox == 2 ) {
                var txtbox = getTextBox(ref,controlId);
            }
            if(createTextBox == 3 ) {
                var txtbox = getTextArea(ref,controlId);
            }
            if(createTextBox == 4 ) {
                var txtbox = getButton(ref,controlId);
            }
            if(createTextBox == 5 ) {
                var txtbox = getCheckbox(ref,controlId);
            }
            if(createTextBox == 6 ) {
                var txtbox = getRadio(ref,controlId);
            }
            if(createTextBox == 7){
                var txtbox = getDatePicker(ref,controlId);
            }
            //        if(createTextBox == 8){
            //            var txtbox = getImage(ref,controlId);
            //        }
            //        if(createTextBox == 9){
            //            var txtbox = getIframe(ref,controlId);
            //        }
            ref.appendChild(txtbox);
            var temp=jQuery(txtbox).val();
            if(createTextBox == 5){
                var label = document.createElement('label')
                label.htmlFor = "id";
                label.style = "font-weight:normal;padding-left:8px;";
                label.appendChild(document.createTextNode('checkbox'));
                ref.appendChild(label);

            }
            if(createTextBox == 6){
                var label = document.createElement('label')
                label.htmlFor = "id";
                label.style = "font-weight:normal;padding-left:8px;";
                label.appendChild(document.createTextNode('radio'));
                ref.appendChild(label);

            }
            jQuery(txtbox).val('');
            jQuery(txtbox).val(temp.trim());
            jQuery(txtbox).focus();


        }
    }

    function deleteTableRow(ref, controlId){
        if(window.tableOperation){
            if(tableOperation(controlId,"DeleteRow") == false)
                return;
        }
        var rowIndex = $(ref).closest('tr').index();
        var url = "action.jsp";
        var requestString=  "controlId="+controlId +"&rowIndex="+rowIndex+"&deleteFlag=yes";  
        var contentLoaderRef = new net.ContentLoader(url, tableRowDeleteResponseHandler, ajaxFormErrorHandler, "POST", requestString, false);
    }


    function selectAllRows(ref,controlId){
        var rowChecks = document.getElementById(controlId).getElementsByClassName("selectRow");
        for(var i=0;i<rowChecks.length;i++){
            if( jQuery(rowChecks[i]).attr("disabled") !== "disabled"){
                rowChecks[i].checked = ref.checked;
                if(!rowChecks[i].checked)
                    jQuery(rowChecks[i]).parents("tr").removeClass("highlightedRow");
                else
                    jQuery(rowChecks[i]).parents("tr").addClass("highlightedRow");
            }
        }
        enableDeleteBtn(ref,controlId);
    }
    function setRowColorInListView(controlId,rowIndex ,colorCode)
    {
        //var rowcheck = document.getElementById("select_"+controlId);
        var table = document.getElementById(controlId);
        if (table != null && table != undefined)
        {
            var row = table.tBodies[0].getElementsByTagName('tr')[rowIndex];
            var setColor = "#"+colorCode+" !important";
            row.style.cssText='background:'+setColor;         
        }
    }
    
    function setRowSelectionInListView(controlId,rowIndices)
    {
        var table = document.getElementById(controlId);
        if (table != null && table != undefined)
        {
            var rows = table.tBodies[0].getElementsByTagName("tr");
            if(rowIndices[0] == -1 ){
                for(var i=0;i<rows.length;i++)
                    rows[i].getElementsByClassName('selectRow')[0].checked = false;
            }
            else{
                for(var i=0;i<rowIndices.length;i++)
                    rows[rowIndices[i]].getElementsByClassName('selectRow')[0].checked = true;
            }
        }
    }
    
    function enableDeleteBtn(ref,controlId){
        var maincheck = document.getElementById("select_"+controlId);
//BUG 75696 starts
        //var allchecked = true;
        var allchecked = false;
        //BUG 75696 : ends
        if(ref!=undefined){
            if(!ref.checked) maincheck.checked = false;
        }
        var selectedRows=[];
        var checks = document.getElementById(controlId).getElementsByClassName('selectRow');
        var deleteFlag = false;
        for(var i=0;i<checks.length;i++){
            if(checks[i].checked){
                deleteFlag = true;
                break;
            }
        }
        for(var i=0;i<checks.length;i++){
            if(checks[i].checked){
                selectedRows.push(i);
            }
        }
        ////BUG 75696 starts
        /*for(var i=0;i<checks.length;i++){
            if(!checks[i].checked){
                allchecked = false;
                break;
            }
        }*/
        var countTicks = 0;
        for(var i=0;i<checks.length;i++){
            if(checks[i].checked){
                countTicks++;
            }
        }
        if(countTicks === checks.length && checks.length >= 1){
            allchecked = true;
        } 
        //BUG 75696 ends
        if(allchecked==true){
            maincheck.checked = true;
        }
        else{
            maincheck.checked = false;
        }

        if(deleteFlag==true){
            document.getElementById("delete_"+controlId).disabled = false;
            if(document.getElementById("delete_"+controlId).childNodes[0].childElementCount > 0)
                document.getElementById("delete_"+controlId).childNodes[0].childNodes[0].setAttribute("src","./resources/images/DeleteIconEnabled.png");
        }
        else{
            document.getElementById("delete_"+controlId).disabled = true;
            if(document.getElementById("delete_"+controlId).childNodes[0].childElementCount > 0)
                document.getElementById("delete_"+controlId).childNodes[0].childNodes[0].setAttribute("src","./resources/images/DeleteIconDisabled.png");
        }
        if(window.selectRowHook){
            selectRowHook(controlId,selectedRows,allchecked);
        }
    }

    function fetchBatch(ref,controlId,action,controlType)
    {
        var isDisabled=document.getElementById(controlId).classList.contains("disabledTable");
        var url = "action.jsp";
        var requestString=  "controlId="+encode_utf8(controlId) +"&Action="+action+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&controlType="+controlType+"&isDisabled="+isDisabled;  
        var contentLoaderRef="";
        var tableDataChangeFlag=false;
        for(var i=0;i<tableDataChangeArray.length;i++){
            var jsonObject=tableDataChangeArray[i];
            if(jsonObject.controlId==controlId){
                tableDataChangeFlag=true;
                tableDataChangeArray.splice(i, 1);
                break;
            }
        }
        if(!tableDataChangeFlag)
        {
            contentLoaderRef = new net.ContentLoader(url, batchHandler, ajaxFormErrorHandler, "POST", requestString, false);        
            return;
        }

        var strMSg = BATCH_MSG;

        var buttons = {
            confirm: {
                label: YES,
                className: 'btn-success'

            },
            cancel: {
                label: NO,
                className: 'btn-danger'

            }
        }

        var callback = function(result){
            if(result == true){
                if((typeof applicationName!='undefined' && applicationName!=null && applicationName=='')){
                  requestString=requestString+"&SaveCurrentBatch=Y";
                  contentLoaderRef = new net.ContentLoader(url, saveBatchHandler, ajaxFormErrorHandler, "POST", requestString, false);
                } else if(window.opener!=null && typeof window.opener.applicationName!='undefined' && window.opener.applicationName!=null && window.opener.applicationName==''){
                    requestString=requestString+"&SaveCurrentBatch=Y";
                  contentLoaderRef = new net.ContentLoader(url, saveBatchHandler, ajaxFormErrorHandler, "POST", requestString, false);
                } else {
                    saveForm('SF');
                    requestString=requestString+"&ClearPreviousOper=Y";
                    contentLoaderRef = new net.ContentLoader(url, batchHandler, ajaxFormErrorHandler, "POST", requestString, false);
                }
               // jQuery(this).dialog("close");
            }
            else if(isBootboxCloseClicked==false && result == false){
                 contentLoaderRef = new net.ContentLoader(url, batchHandler, ajaxFormErrorHandler, "POST", requestString, false);
  
                // jQuery(this).dialog("close");
            }
            if(isBootboxCloseClicked==true) isBootboxCloseClicked=false;
        }
    //        
    //    var buttons = 
    //    {
    //        Yes: function()
    //        {                        
    //            requestString=requestString+"&SaveCurrentBatch=Y";
    //            var contentLoaderRef = new net.ContentLoader(url, batchHandler, ajaxFormErrorHandler, "POST", requestString, false);        
    //            jQuery(this).dialog("close");
    //        },
    //        No: function() 
    //        { 
    //            var contentLoaderRef = new net.ContentLoader(url, batchHandler, ajaxFormErrorHandler, "POST", requestString, false);        
    //            jQuery(this).dialog("close" );
    //        }
    //    };

         showConfirmDialog(strMSg,buttons,callback); 

        // tableDataChangeFlag=false;
    }

    function showConfirmDialog(msg,btns,callback,isClose,dialogType){
        if(dialogType == undefined || dialogType.toLowerCase() == "info" ){
        msg="<div class='typeInfo' style='color:#0072c6;margin-bottom: 10px;'><span style='font-size:35px;padding-right: 10px;' class='glyphicon glyphicon-info-sign'></span><span style='font-size:16px;'>"+INFO+"</span></div>"+msg;
        }
        else if(dialogType.toLowerCase() == "warning")
        {
         msg="<div class='typeWarning' style='color:#b36106;margin-bottom: 10px;'><span style='font-size:27px;padding-right: 10px;'><svg width=\"27px\" height=\"27px\" viewBox=\"0 0 27 27\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><title>Group 6</title><g id=\"Symbols\" stroke=\"none\" stroke-width=\"1\" fill=\"none\" fill-rule=\"evenodd\"><g id=\"Group-6\"><circle id=\"Oval-Copy\" fill=\"#B36106\" cx=\"13.5\" cy=\"13.5\" r=\"13.5\"></circle><path d=\"M13.5,18.5 C14.3284271,18.5 15,19.1715729 15,20 C15,20.8284271 14.3284271,21.5 13.5,21.5 C12.6715729,21.5 12,20.8284271 12,20 C12,19.1715729 12.6715729,18.5 13.5,18.5 Z M15,6 L14.5,17.5 L12.5,17.5 L12,6 L15,6 Z\" id=\"Combined-Shape-Copy\" fill=\"#FFFFFF\"></path></g></g></svg></span><span style='font-size:16px;font-weight: bold;font-family:Open Sans;'>Warning</span></div>"+msg;   
        }
		if( isClose == undefined ){
            isClose = true;
        }
        bootbox.confirm({
        message: msg,
        buttons: btns,
        closeButton: isClose, 
        callback: callback,
        size: "medium"
    });
     $('.bootbox-close-button').on("click",function(){
       isBootboxCloseClicked=true; 
    });
    }




    function saveBatchHandler()
    {
      //document.getElementById(this.req.getResponseHeader("tableControlId")).tBodies[0].remove();  
      var tableId=getQueryVariable(this.params, "controlId");
      
      if(!isNaN(this.req.responseText.trim())){
          var code = parseInt(this.req.responseText.trim());
          if( code !== 0){
               showSplitMessage("", "Error in Saving Data.",SAVE_TITLE,"error");
               return;
          }
      }
      $("#"+tableId+" tbody").empty();
      $("#"+tableId+ " tbody").append(this.req.responseText);
      $("#"+tableId).floatThead('reflow');
      checkTableHeight(tableId);
      var preEnabled=this.req.getResponseHeader("preEnabled");
      var nextEnabled=this.req.getResponseHeader("nextEnabled");
      var batchCounter=this.req.getResponseHeader("batchCounter");
      var sortOrder=this.req.getResponseHeader("SortOrder");
      if(preEnabled=="true"){
        $("#pre_"+tableId).prop("disabled", false);
        document.getElementById("preimage_"+tableId).src = "./resources/images/PaginationLeftEnabled.png";
      }
      else{
        $("#pre_"+tableId).prop("disabled", true);
        document.getElementById("preimage_"+tableId).src = "./resources/images/PaginationLeftDisabled.png";
      }
      if(nextEnabled=="true"){
        $("#next_"+tableId).prop("disabled", false);
        document.getElementById("nextimage_"+tableId).src = "./resources/images/PaginationRightEnabled.png";
      }
      else{
        $("#next_"+tableId).prop("disabled", true);
         document.getElementById("nextimage_"+tableId).src = "./resources/images/PaginationRightDisabled.png";
        }
        
      applyFormattingGrid();
      var totalValueElements=document.getElementById('totallabel_'+tableId).innerHTML.split(",!,");
      for(var i=0;i<totalValueElements.length;i++){
        if(totalValueElements[i]!=''){
         $(document.getElementsByClassName(totalValueElements[i].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
        var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
            maskfield(this,'label');
            }
        });
        }
        showTotal('',totalValueElements[i]);
        reshuffleIndices(tableId,sortOrder,batchCounter);
     }
     try{
            $($('#'+tableId).get(0).parentNode.parentNode).find('th.tableStyle').removeAttr("SortOrder");    
            $($('#'+tableId).get(0).parentNode.parentNode).find('th.tableStyle').css({
                "background-repeat":"",
                "background-position":"",
                "background-image":""
            });
            if (sortOrder != null && this.req.getResponseHeader("ColumnIndex")!=null) {
                var thRef = $($('#' + tableId).get(0).parentNode.parentNode).find('th.tableStyle').get(parseInt(this.req.getResponseHeader("ColumnIndex")) + 1);
                thRef.style.backgroundRepeat = "no-repeat";
                thRef.style.backgroundPosition = "center right";
                var imageName = "";
                if (sortOrder == "D") {
                    thRef.setAttribute("SortOrder", "A");
                    imageName = "lvwUp.png";
                } else if (sortOrder == "A") {
                    thRef.setAttribute("SortOrder", "D");
                    imageName = "lvwDown.png";
                } else if (sortOrder == "N") {
                    thRef.removeAttribute("SortOrder");
                }
                thRef.style.backgroundImage = (imageName != "") ? "url('resources/images/" + imageName + "')" : "";
            }
        }
        catch(ex){}
    }
     function batchHandler()
    {
      //document.getElementById(this.req.getResponseHeader("tableControlId")).tBodies[0].remove();  
      var tableId=getQueryVariable(this.params, "controlId");      
      
      $("#"+tableId+" tbody").empty();
      $("#"+tableId+ " tbody").append(this.req.responseText);
      $("#"+tableId).floatThead('reflow');
      checkTableHeight(tableId);
      var preEnabled=this.req.getResponseHeader("preEnabled");
      var nextEnabled=this.req.getResponseHeader("nextEnabled");
      var batchCounter=this.req.getResponseHeader("batchCounter");
      var sortOrder=this.req.getResponseHeader("SortOrder");
      if(preEnabled=="true"){
        $("#pre_"+tableId).prop("disabled", false);
        document.getElementById("preimage_"+tableId).src = "./resources/images/PaginationLeftEnabled.png";
      }
      else{
        $("#pre_"+tableId).prop("disabled", true);
        document.getElementById("preimage_"+tableId).src = "./resources/images/PaginationLeftDisabled.png";
      }
      if(nextEnabled=="true"){
        $("#next_"+tableId).prop("disabled", false);
        document.getElementById("nextimage_"+tableId).src = "./resources/images/PaginationRightEnabled.png";
      }
      else{
        $("#next_"+tableId).prop("disabled", true);
         document.getElementById("nextimage_"+tableId).src = "./resources/images/PaginationRightDisabled.png";
        }
      applyFormattingGrid();
      var totalValueElements=document.getElementById('totallabel_'+tableId).innerHTML.split(",!,");
      for(var i=0;i<totalValueElements.length;i++){
        if(totalValueElements[i]!=''){
         $(document.getElementsByClassName(totalValueElements[i].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
        var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
            maskfield(this,'label');
            }
        });
        }
        showTotal('',totalValueElements[i]);         
        reshuffleIndices(tableId,sortOrder,batchCounter);
     }


     var dgroupColumns = this.req.getResponseHeader("dgroupColumns");
     if(dgroupColumns!=null && dgroupColumns!=undefined){
     for(var i=0;i<dgroupColumns.split(",").length;i++){
        var className = "dgroup_"+tableId+"_"+dgroupColumns.split(",")[i];
        //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);

        $('.'+className).each(function() {
            var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
            jQuery(this).autoNumeric('init',{
                dGroup: digitGroup,
                mDec: '0'
            }); 
        });
     }
     }
     try{
            $($('#'+tableId).get(0).parentNode.parentNode).find('th.tableStyle').removeAttr("SortOrder");    
            $($('#'+tableId).get(0).parentNode.parentNode).find('th.tableStyle').css({
                "background-repeat":"",
                "background-position":"",
                "background-image":""
            });
            if (sortOrder != null && this.req.getResponseHeader("ColumnIndex")!=null) {
                var thRef = $($('#' + tableId).get(0).parentNode.parentNode).find('th.tableStyle').get(parseInt(this.req.getResponseHeader("ColumnIndex")) + 1);
                thRef.style.backgroundRepeat = "no-repeat";
                thRef.style.backgroundPosition = "center right";
                var imageName = "";
                if (sortOrder == "D") {
                    thRef.setAttribute("SortOrder", "A");
                    imageName = "lvwUp.png";
                } else if (sortOrder == "A") {
                    thRef.setAttribute("SortOrder", "D");
                    imageName = "lvwDown.png";
                } else if (sortOrder == "N") {
                    thRef.removeAttribute("SortOrder");
                }
                thRef.style.backgroundImage = (imageName != "") ? "url('resources/images/" + imageName + "')" : "";
            }
        }
        catch(ex){}
        if(window.nextPreviousBatchHook)
        {
            nextPreviousBatchHook(tableId);
        }
    }
    function deleteTableRows(ref, controlId, deletingRowIndices){
        if(window.tableOperation){
            if(tableOperation(controlId,"DeleteRow") == false)
                return;
        }
		var isConfirmRequired=false;
        if(window.showConfirmBeforeDelete){
            isConfirmRequired=showConfirmBeforeDelete(controlId);
           }
        if(isConfirmRequired == true)
        {
            var strMSg = DELETE_Table_MSG;

            var buttons = {
                confirm: {
                    label: YES,
                    className: 'btn-success'

                },
                cancel: {
                    label: NO,
                    className: 'btn-danger'

                }
            }
            var callback = function(result){
                if(result == true){
                    deleteTableRowsOperation(ref, controlId, deletingRowIndices);
                }
            }
            showConfirmDialog(strMSg,buttons,callback,false,'warning');
        }
        else
        {
            deleteTableRowsOperation(ref, controlId, deletingRowIndices);
        }
        
    }
    function deleteTableRowsOperation(ref, controlId, deletingRowIndices){
        var rowIndices = "";
        var deleterow = "";
        var rowChecks = document.getElementById(controlId).getElementsByClassName("selectRow");
        if(deletingRowIndices!=null && deletingRowIndices!=undefined){
            for(var i=0;i<deletingRowIndices.length;i++){
                    rowIndices += deletingRowIndices[i]+",";
            }
        }
        else{
            for(var i=0;i<rowChecks.length;i++){
                if(rowChecks[i].checked){
                    rowIndices += i+",";
                }
            }
        }
        for(var i=rowIndices.split(",").length-1;i>=0;i--){
            var rowIndex = rowIndices.split(",")[i];
            if(rowIndex!=""){
                /*if(document.getElementById(controlId).tBodies[0].children[rowIndex].className=='rowinhtml')
                    deleterow+="y,"
                else*/
                deleterow+="y,";
            }
        }
        document.getElementById("select_"+controlId).checked = false;
        var url = "action.jsp";
        setTableModifiedFlag(controlId);
        //    var requestString=  "controlId="+controlId +"&rowIndex="+rowIndex+"&deleteFlag=yes";  
        var requestString=  "controlId="+encode_utf8(controlId) +"&rowIndices="+rowIndices+"&deleteFlag=yes&deleterow="+deleterow;  
        var contentLoaderRef = new net.ContentLoader(url, tableRowsDeleteResponseHandler, ajaxFormErrorHandler, "POST", requestString, false);
        
    }

    function setTableModifiedFlag(controlId)
    {
        var idfound=false;
        for(var i=0;i<tableDataChangeArray.length;i++)
        {
            if(controlId==tableDataChangeArray[i].controlId)
            {
               idfound=true;
               break;
            }           
        }
        if(!idfound)
        {
            var jsonObject={};
            jsonObject.controlId=controlId;
            jsonObject.value=true;
            tableDataChangeArray.push(jsonObject);
        }
    }

    function tableRowsDeleteResponseHandler(){
        var tableControlId=getQueryVariable(this.params, "controlId");
        jQuery('#delete_'+tableControlId).blur(); //Bug 90080 Start
        jQuery('#delete_'+tableControlId).attr('disable',true); //Bug 90080 End
        jQuery('#delete_'+tableControlId).removeClass("hightlightAddDeleteRow")
        var rowIndices = getQueryVariable(this.params, "rowIndices");
        var batchCounter=this.req.getResponseHeader("batchCounter");  
        deleteRowsFromGridAction(tableControlId,rowIndices,this.req.getResponseHeader("altrowcolor"),batchCounter);//Bug 85784
    }

    function reshuffleIndices(tableId,sortOrder,batchCounter){
        try{
            var x;
            if(batchCounter==undefined ||batchCounter!=""){
                x=batchCounter;
            }
            var table = document.getElementById(tableId);
            var children = table.parentNode.parentNode.childNodes;
            var theads = children[0].getElementsByTagName("th");
            var autoIncrementColumnsIndices = [];
            var rows = document.getElementById(tableId).tBodies[0].getElementsByTagName("tr");
            for(var i=1;i<theads.length;i++){
                if(theads[i].classList.contains("autoIncrementLabel")){
                    autoIncrementColumnsIndices[i]=i;
                }
            }
            if(sortOrder==undefined || (sortOrder==""||sortOrder=="A" || sortOrder=="N")){
                for(var i=1;i<=getGridRowCount(tableId);i++){
                    for(var j=1;j<=rows[i-1].getElementsByTagName("td").length;j++){
                        if(autoIncrementColumnsIndices[j]!=null && autoIncrementColumnsIndices[j]!=undefined)
                            if(batchCounter!=undefined && batchCounter!=""){
                                rows[i-1].getElementsByTagName("td")[autoIncrementColumnsIndices[j]].getElementsByClassName("control-class")[0].innerHTML = x;
                                x++;
                            }
                        else{
                            rows[i-1].getElementsByTagName("td")[autoIncrementColumnsIndices[j]].getElementsByClassName("control-class")[0].innerHTML = i;
                        }
                    }
                }
            }
            else{
                for(var i=1;i<=getGridRowCount(tableId);i++){
                    for(var j=1;j<=rows[getGridRowCount(tableId)-i].getElementsByTagName("td").length;j++){
                        if(autoIncrementColumnsIndices[j]!=null && autoIncrementColumnsIndices[j]!=undefined)
                            if(batchCounter!=undefined&& batchCounter!=""){
                            rows[getGridRowCount(tableId)-i].getElementsByTagName("td")[autoIncrementColumnsIndices[j]].getElementsByClassName("control-class")[0].innerHTML = x;
                            x++;
                            }
                        else{
                            rows[getGridRowCount(tableId)-i].getElementsByTagName("td")[autoIncrementColumnsIndices[j]].getElementsByClassName("control-class")[0].innerHTML = i;
                        }
                    }
                } 
            }
        }
        catch(ex){}
    }

    function highlightRow(ref, controlId,event){
        var rowIndex = $(ref).closest('tr').index();
        var url = "action.jsp";
        var requestString=  "controlId="+controlId +"&rowIndex="+rowIndex+"&deleteFlag=no";  
        if(event=='hover'){
            //        ref.style.backgroundColor='#ff7f7f';
            var contentLoaderRef = new net.ContentLoader(url, tableRowHighlight, ajaxFormErrorHandler, "POST", requestString, false);
        }
        else{
            //        ref.style.backgroundColor='#fff';
            var contentLoaderRef = new net.ContentLoader(url, tableRowHighlightRemove, ajaxFormErrorHandler, "POST", requestString, false);
        }
    }
    function tableRowHighlight(){
        var tableControlId=getQueryVariable(this.params, "controlId");
        var rowIndex=getQueryVariable(this.params, "rowIndex");
        document.getElementById(tableControlId).tBodies[0].children[rowIndex].style.border="2px solid #ff7f7f ";
    }
    function tableRowHighlightRemove(){
        var tableControlId=getQueryVariable(this.params, "controlId");
        var rowIndex=getQueryVariable(this.params, "rowIndex");
        if(document.getElementById(tableControlId).tBodies[0].children[rowIndex])
            document.getElementById(tableControlId).tBodies[0].children[rowIndex].style.border="1px #ffffff";
    }
    function tableRowDeleteResponseHandler(){
        var tableControlId=getQueryVariable(this.params, "controlId");
        var rowIndex=getQueryVariable(this.params, "rowIndex");
        if(document.getElementById(tableControlId).tBodies[0].children[rowIndex].className=='rowinhtml')
            document.getElementById(tableControlId).tBodies[0].deleteRow(rowIndex);
        else
            document.getElementById(tableControlId).tBodies[0].children[rowIndex].style.display="none";
    }

    function toggleSection(ref){
        updateSessionTimeout();
        if(document.getElementById('fade')!=null)
            document.getElementById('fade').style.display="block";
        jQuery(ref).siblings().toggle(450,function(){
            jQuery(ref).find("polyline").attr("transform",jQuery(ref).siblings().is(":visible") ? "translate(8.000000, 8.000000) scale(-1, -1) translate(-8.000000, -8.000000)" : "");
            if((jQuery(ref).siblings().is(":visible")))
            {
                try{
                //ref.parentNode.scrollIntoView(false);     //Bug 83346
                }
                catch(ex){}

            }
            var sectionState;
            if(jQuery(ref).attr("state") == "collapsed")
            {
               sectionState="expanded";
               jQuery(ref).attr("state","expanded");  
            }
            else
            {
                sectionState="collapsed";
                jQuery(ref).attr("state","collapsed");            
            }  
            if( jQuery(ref).attr("painted")!= undefined){   //Bug 85630,85657
            if(window.onChangeSectionState)
                window.onChangeSectionState(jQuery(ref).parent().attr("id"),sectionState);
            }
            //$("#"+jQuery(ref).parent().attr("id")+" .iform-table").floatThead('reflow');
            var element=document.getElementById(jQuery(ref).parent().attr("id"));
            $($(element).find(".iform-table")).floatThead('reflow');
            if(document.getElementById('fade')!=null)
               document.getElementById('fade').style.display="none";
            $(".iform-table").floatThead('reflow');
//           if(document.activeElement!=null){
//                var activeElement= document.activeElement;
//                $(activeElement).blur();
//            }
        }
        );
        var url = "action_API.jsp";
        requestString = "frameId="+jQuery(ref).parent().attr("id")+"&frameState="+jQuery(ref).attr("state")+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid);
        if( jQuery(ref).attr("painted")== undefined)
            new net.ContentLoader(url, frameResponseHandler, frameErrorHandler, "POST", requestString, true);
    }
    function frameResponseHandler(){
        $(".iform-table").floatThead('reflow');
        var framehtml = this.req.responseText.trim();
        var frameid = getQueryVariable(this.params, "frameId");
        //   HandleProgressBar(data);
        var parentNode=document.getElementById(frameid).parentNode;
        if(framehtml!=""){
          parentNode.innerHTML="";
          jQuery(parentNode).html(framehtml);
        }
        doInit();
        var jsElm = document.createElement("script");
        var jsSrc="resources/scripts/"+processName+"/"+formName+"/"+frameid +".js";
        jsElm.type = "application/javascript";
        jsElm.src = jsSrc;
        if(!isScriptAlreadyIncluded(jsSrc))//Bug 84916
            document.getElementsByTagName("head")[0].appendChild(jsElm);
        executeLoadEvents('4',frameid);
        if(window.onLoadSection)
            window.onLoadSection(frameid);
         //Bug 85630
        var sectionState = "";
        if(document.getElementById(frameid)!=null && document.getElementById(frameid).childNodes[0]!=null){
            sectionState = document.getElementById(frameid).childNodes[0].getAttribute("state");
        }
         if(window.onChangeSectionState)
                window.onChangeSectionState(frameid,sectionState);
         if(document.getElementById('fade')!=null)
               document.getElementById('fade').style.display="none";
    }
    
    function isScriptAlreadyIncluded(src){//Bug 84916 Start
        var scripts = document.getElementsByTagName("script");
        for(var i = 0; i < scripts.length; i++) 
        if(scripts[i].getAttribute('src') == src) return true;
        return false;
    }//Bug 84916 End

    function frameErrorHandler(){
        if(document.getElementById('fade')!=null)
          document.getElementById('fade').style.display="none";
    }

    $(document).ready(function(){
        var loaded = 0;

        if($('.embed-responsive-item').length>1){
            document.getElementById("fade").style.display="block";
            CreateIndicator("application");
        }
       
//        $('.embed-responsive-item').on( 'load',function (){
//
//            if($('.embed-responsive-item').length-1==++loaded){
//                document.getElementById("fade").style.display="none";
//                RemoveIndicator("application");
//            }
//        });
//        setTimeout(function() { 
//            if($('#application').length>0){
//                document.getElementById("fade").style.display="none";
//                RemoveIndicator("application");
//            }
//        }, 100);
    });
    
    function setTabStyleByName(tabID,sheetName,attributeName, attributeValue,showHideAddDelete){
        var tab = document.getElementById(tabID);
        if (tab != null && tab != undefined)
        {
            var sheetList = tab.getElementsByTagName("li");
            var index=0;
            for(var i=0;i<sheetList.length;i++){
                if(sheetList[i].innerText==sheetName){
                    index=i;
                    break;
                }
            }
            setTabStyle(tabID,index,attributeName, attributeValue,showHideAddDelete);
           
        }
    }
    
    function setTabStyle(tabID,index,attributeName, attributeValue,showHideAddDelete)
    {
        var tab = document.getElementById(tabID);
        if (tab != null && tab != undefined)
        {
            var tabli = tab.getElementsByTagName("li")[index];
            var sheet = tab.getElementsByClassName("tab-pane fade")[index];
            var controls = sheet.getElementsByClassName("control-class");
            var tableControls = sheet.getElementsByClassName("iform-table");
            var frameControls = sheet.getElementsByClassName("FrameControl");
            //    console.log(tabli);
            //    console.log(sheetconsole.log);
            //    console.log(controls);

            //    var tabitem = document.getElementById(tabID+"_"+index);
            //    var sheetID = tabID+"_sheet"+index;

            if (attributeName.toLowerCase() == "backcolor") {
                jQuery("#" + sheet.id + " .control-class").css("background-color", attributeValue);
            } else if (attributeName.toLowerCase() == "fontcolor") {
                jQuery("#" + sheet.id + " .control-class").css("color", attributeValue);
            } else if (attributeName.toLowerCase() == "visible")
            {
                if (attributeValue.toLowerCase() == "true") {
                    tabli.style.display = "inline";
                    sheet.style.display = "";
                    //jQuery("#"+sheetID).css("display","");
                    var allSheetHide=true;
                    for(var i=0;i<tab.getElementsByClassName("tab-pane fade").length;i++){
                           var t=tab.getElementsByClassName("tab-pane fade")[i];
                           if(t.style.display!="none"){
                               allSheetHide=false;
                           }
                    }
                    if(!allSheetHide){
                        jQuery(sheet).parent().css("display","");
                    }
                } else if (attributeValue.toLowerCase() == "false") {
                    tabli.style.display = "none";
                    sheet.style.display = "none";
                    //jQuery("#"+sheetID).css("display","none");
                    var allSheetHide=true;
                    for(var i=0;i<tab.getElementsByClassName("tab-pane fade").length;i++){
                           var t=tab.getElementsByClassName("tab-pane fade")[i];
                           if(t.style.display!="none"){
                               allSheetHide=false;
                           }
                    }
                    if(allSheetHide){
                        jQuery(sheet).parent().css("display","none");
                    }
                }
                jQuery("#"+tabID +" .tabtheme4.iformTabUL").each(function(){
                    $(this).scrollingTabs('refresh');
                });
            
            } else if (attributeName.toLowerCase() == "disable")
            {
                if (attributeValue.toLowerCase() == "true") {
                    for (var i = 0; i < controls.length; i++) {
                        //controls[i].disabled = "true";
                        if(!controls[i].classList.contains("listviewlabel"))
                          setStyle(controls[i].id, "disable", "true");
                    }
                    for (var j = 0; j < tableControls.length; j++) 
                        setStyle(tableControls[j].id, "disable", "true");
                    for (var k = 0; k < frameControls.length; k++) 
                        setStyle(frameControls[k].id, "disable", "true");
                    
                        disableDoclistControl(sheet.id,true);
                    //$("#"+sheetID+' .control-class').attr('disabled', true);
                }else if (attributeValue.toLowerCase() == "false") {
                    for (var i = 0; i < controls.length; i++) {
                        //controls[i].removeAttribute("disabled");
                        if(!controls[i].classList.contains("listviewlabel"))
                        setStyle(controls[i].id, "disable", "false",showHideAddDelete);
                    }
                    for (var j = 0; j < tableControls.length; j++) 
                        setStyle(tableControls[j].id, "disable", "false");
                    for (var k = 0; k < frameControls.length; k++) 
                        setStyle(frameControls[k].id, "disable", "false",showHideAddDelete);
                    //$("#"+sheetID+' .control-class').attr('disabled', false);
                         disableDoclistControl(sheet.id,false);
                }
            } else if (attributeName.toLowerCase() == "readonly")
            {
                if (attributeValue.toLowerCase() == "true") {
                    for (var i = 0; i < controls.length; i++) {
                        //controls[i].readOnly = "true";
                        setStyle(controls[i].id, "readonly", "true");
                    }
                } else if (attributeValue.toLowerCase() == "false") {
                    for (var i = 0; i < controls.length; i++) {
                        //controls[i].removeAttribute("readOnly   ");
                        setStyle(controls[i].id, "readonly", "false");
                    }
                }
            } else if (attributeName.toLowerCase() == "fontfamily")
                jQuery("#" + sheet.id + " .control-class").css("font-family", attributeValue);
            else if (attributeName.toLowerCase() == "fontweight")
                jQuery("#" + sheet.id + " .control-class").css("font-weight", attributeValue);
            else if (attributeName.toLowerCase() == "fontstyle")
                jQuery("#" + sheet.id + " .control-class").css("font-style", attributeValue);
            else if (attributeName.toLowerCase() == "fontsize")
                jQuery("#" + sheet.id + " .control-class").css("font-size", attributeValue);
        }
    }

    //function setTabStyle(tabID,index,attributeName, attributeValue){
    //    var tab = document.getElementById(tabID);
    //    var tabli = tab.getElementsByTagName("li")[index];
    //    var sheet = tab.getElementsByClassName("tab-pane fade")[index];
    //
    //    if( attributeName.toLowerCase() == "backcolor"){
    //        jQuery("#"+sheetID+" .control-class").css("background-color",attributeValue);
    //    }
    //    else if( attributeName.toLowerCase() == "fontcolor"){
    //        jQuery("#"+sheetID+" .control-class").css("color",attributeValue);
    //    }
    //    else if( attributeName.toLowerCase() == "visible")
    //    {
    //        if( attributeValue.toLowerCase() == "true"){
    //             tabli.style.display = "inline";
    //             sheet.style.display = "";
    //        }
    //        else if( attributeValue.toLowerCase() == "false"){
    //            tabli.style.display = "none";
    //            sheet.style.display = "none";
    //        }
    //    }
    //     else if( attributeName.toLowerCase() == "disable")
    //    {
    //        if( attributeValue.toLowerCase() == "true"){
    //            for(var i=0;i<controls.length;i++){
    //                controls[i].disabled = "true";
    //            }
    //        }
    //        else if( attributeValue.toLowerCase() == "false"){
    //            for(var i=0;i<controls.length;i++){
    //                controls[i].removeAttribute("disabled")
    //            }
    //        }
    //    }
    //    else if( attributeName.toLowerCase() == "readonly")
    //    {
    //        if( attributeValue.toLowerCase() == "true"){
    //            for(var i=0;i<controls.length;i++){
    //                controls[i].readOnly = "true";
    //            }
    //        }
    //        else if( attributeValue.toLowerCase() == "false"){
    //            for(var i=0;i<controls.length;i++){
    //                controls[i].removeAttribute("readOnly")
    //            }
    //        }
    //    }
    //    
    //    else if( attributeName.toLowerCase() == "fontfamily")
    //        jQuery("#"+sheetID+" .control-class").css("font-family",attributeValue);
    //    else if( attributeName.toLowerCase() == "fontweight")
    //        jQuery("#"+sheetID+" .control-class").css("font-weight",attributeValue);
    //    else if( attributeName.toLowerCase() == "fontstyle")
    //        jQuery("#"+sheetID+" .control-class").css("font-style",attributeValue);
    //    else if( attributeName.toLowerCase() == "fontsize")
    //        jQuery("#"+sheetID+" .control-class").css("font-size",attributeValue);
    //}

    //Custom function
    /*function tableOperation(tableId,operationType){
        if( tableId == "table1"){
            if( operationType == "AddRow"){
                if(getValue("textbox1")=="aman"){
                    setFocus("textarea2")
                    return false;
                }
            }
             if( operationType == "DeleteRow"){
                if(getValue("textarea1")=="22"){
                    setCellDisabled(tableId,"2",1,"true")
                    return false;
                }
            }
        }
    }
    */

    function getWorkItemData(property){
        if(property.toLowerCase()=="processinstanceid"){
            return pid;
        }
        else if(property.toLowerCase() == "workitemid"){
            return wid;
        }

        else if(property.toLowerCase() == "taskid"){
            return tid;
        }

        else if(property.toLowerCase() == "activityname"){
            return activityName;
        }

        else if(property.toLowerCase() == "appserverip"){
            return appServerIp;
        }

        else if(property.toLowerCase() == "appserverport"){
            return appServerPort;
        }

        else if(property.toLowerCase() == "username"){
            return userName;
        }

        else if(property.toLowerCase() == "sessionid"){
            var dbid = "";
            var url = "portal/appTask.jsp";
            var queryString = "oper=GetDBId&pid=" + encode_utf8(pid) + "&wid=" + encode_utf8(wid) + "&tid=" + encode_utf8(tid) + "&fid=" + encode_utf8(fid);
            dbid = iforms.ajax.processRequest(queryString, url);
            return dbid.trim();
        }
        else if(property.toLowerCase() == "cabinetname"){
            return cabinetName;
        }

        else if(property.toLowerCase() == "processname"){
            return processName;
        }

        else if(property.toLowerCase() == "subtaskid"){
            return subTaskId;
        }

        else if(property.toLowerCase() == "taskname"){
            return taskName;
        }
        else if(property.toLowerCase() == "assignedto"){
            return assignedTo;
        }

        else if(property.toLowerCase() == "prioritylevel"){
            return priorityLevel;
        }
        
        else if(property.toLowerCase() == "lockedbyname"){
            return lockedByName;
        }
        
        else if(property.toLowerCase() == "processedby"){
            return processedBy;
        }
        
        else if(property.toLowerCase() == "introductiondatetime"){
            return introductionDateTime;
        }
        
        else if(property.toLowerCase() == "introducedat"){
            return introducedAt;
        }
        
        else if(property.toLowerCase() == "queueid"){
            return queueId;
        }
        
        else if(property.toLowerCase() == "lockstatus"){
            return lockStatus;
        }
        
        else if(property.toLowerCase() == "useremailid"){
            return userEmailId;
        }
        
        else if(property.toLowerCase() == "userpersonalname"){
            return userPersonalName;
        }
        
        else if(property.toLowerCase() == "userfamilyname"){
            return userFamilyName;
        }
        
        else if(property.toLowerCase() == "userindex"){
            return userIndex;
        }
		else if(property.toLowerCase() == "queuename"){
            return queueName;
        }

    }

    function setRowsDisabled(tableId,rowIndices,disableSelectCheckbox)
    {
        disableSelectCheckbox=typeof disableSelectCheckbox =="undefined"?false:disableSelectCheckbox;
        var table = document.getElementById(tableId);
        if (table != null && table != undefined)
        {
            var rows = table.tBodies[0].getElementsByTagName('tr');
            for (var i = 0; i < rowIndices.length; i++) 
            {
                var controls = rows[rowIndices[i] ].getElementsByClassName("control-class");
                for (var j = 0; j < controls.length; j++) 
                {
                    controls[j].setAttribute("disabled", "true");
                    if(jQuery(controls[j]).hasClass("radio-group")==true){
                         jQuery(controls[j]).parent().css("pointerEvents","none");
                         jQuery(controls[j]).parent().css("cursor","default");
                         var childElement = controls[j].children; 
                         for(var k=0;k<childElement.length;k++){
                             var c = childElement[k].children[0];
                              c.disabled ="true";
                         }
                    }
                    var cellRef = rows[rowIndices[i] ].getElementsByTagName("td")[j + 1];
                    $(cellRef).prop('onclick', null).off('click');

                }
                if(disableSelectCheckbox){
                    var selectcheckbox = rows[rowIndices[i] ].getElementsByClassName("selectRow");
                    if(selectcheckbox.length>0){
                        selectcheckbox[0].disabled=true;
                    }
                }
            }
        }
    }

    function setRowStyle(tableId,rowIndex,attributeName,attributeValue)
    {
        var table = document.getElementById(tableId);
        if (table != null && table != undefined)
        {
            var rows = table.tBodies[0].getElementsByTagName('tr');
            if (attributeName.toLowerCase() == "visible") {
                if (attributeValue.toLowerCase() == "true") {
                    rows[rowIndex].style.display = "";
                } else if (attributeValue.toLowerCase() == "false" && rows != null && rows[rowIndex] != null) {
                    rows[rowIndex].style.display = "none";
                }
            }else if (attributeName.toLowerCase() == "disable") {
                var controls = rows[rowIndex].getElementsByClassName("control-class");
                for (var i = 0; i < controls.length; i++) {
                    if (attributeValue.toLowerCase() == "true") {
                        if(jQuery(controls[i]).hasClass('radio-group')){
                            jQuery(controls[i]).parent().css("pointerEvents","none");
                            jQuery(controls[i]).parent().css("cursor","default");
                        }
                        controls[i].disabled = "true";
                    } else if (attributeValue.toLowerCase() == "false") {
                        if(jQuery(controls[i]).hasClass('radio-group')){
                            jQuery(controls[i]).parent().css("pointerEvents","auto");
                            jQuery(controls[i]).parent().css("cursor","");
                        }
                        controls[i].removeAttribute("disabled");
                    }
                }
            }
        }
        var totalValueElements=document.getElementById('totallabel_'+tableId).innerHTML.split(",!,");
            for(var i=0;i<totalValueElements.length;i++){
             //var controlRef = document.getElementById('label'+'_'+controlId+'_'+maskedLabels.split(",")[i]);
             if(totalValueElements[i]!=''){
             $(document.getElementsByClassName(totalValueElements[i].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
                 var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                    maskfield(this,'label');
            }
         });
    }
                showTotal('',totalValueElements[i]);
            }
    }
    //    
    //function showRowInTable(tableId,rowIndex,flag){
    //    var table = document.getElementById(tableId);
    //    var rows = table.getElementsByClassName('rowinhtml');
    //    if(flag=="false"){
    //        rows[rowIndex].style.display = "none";
    //    }
    //    else if(flag=="true"){
    //        rows[rowIndex].style.display = "";
    //    }
    //}

    //function getValueFromTable(tableId)
    //{
    //    var table = document.getElementById(tableId);
    //    //var tablerows = table.getElementsByClassName("rowinhtml");
    //    if (table != null && table != undefined)
    //    {
    //        var tablerows = table.getElementsByTagName("tr");
    //        var theadrow = table.parentNode.parentNode.childNodes[0].getElementsByTagName("th");
    //        //var doc = document.implementation.createDocument(null,"Table");
    //        //var rows = document.createElement("Rows");
    //        //var row = document.createElement("Row");
    //
    //        var XMLString = "";
    //        XMLString += "<Rows>\n<Row>\n";
    //        for (var i = 1; i < theadrow.length; i++) {
    //            var header = theadrow[i].textContent;
    //            //var headerNode = document.createElement("Header");
    //            //headerNode.appendChild(document.createTextNode(header));
    //            // row.appendChild(headerNode);
    //            //rows.appendChild(row);
    //            XMLString += "<header>" + header + "</header>\n";
    //        }
    //        //doc.documentElement.appendChild(rows);
    //        XMLString += "</Row>\n";
    //
    //        for (i = 1; i < tablerows.length; i++) {
    //            var row = tablerows[i];
    //            var controls = row.getElementsByClassName("control-class");
    //            XMLString += "<Row>\n"
    //            //row = document.createElement("Row");
    //            for (var j = 1; j <= controls.length; j++) {
    //                var control = controls[j - 1];
    //                var value = "";
    //                if (control.type == 'text' || control.type == 'select-one' || control.type == 'ComboBox' || control.type == 'textarea') {
    //                    value = control.value;
    //
    //                } else if (control.tagName == 'LABEL') {
    //                    value = control.innerHTML;
    //                } else if (control.type == "radio" || control.id.indexOf('radio') != -1)
    //                {
    //                    value = control.checked;
    //                } else if (control.type == 'checkbox')
    //                {
    //                    value = control.checked;
    //                }
    //                // var headName = theadrow[j].textContent.replaceAll(" ","");
    //                //  var colNode = document.createElement(headName);
    //                //  colNode.appendChild(document.createTextNode(value));
    //                //  row.appendChild(colNode);
    //                //   rows.appendChild(row);
    //                //  doc.documentElement.appendChild(rows);
    //
    //                XMLString += "<" + theadrow[j].textContent + ">" + value + "</" + theadrow[j].textContent + ">\n";
    //            }
    //
    //            XMLString += "</Row>\n"
    //        }
    //        XMLString += "</Rows>";
    //        //console.log(doc);
    //        return XMLString;
    //    }
    //}

    function getValueFromTable(tableId){
        if(tableId!=null && tableId!=undefined){

            var table = document.getElementById(tableId);
            var rows = table.tBodies[0].getElementsByTagName("tr");
            if(rows.length>=1){
                var XMLString = "";
                XMLString += "<ListItems>\n";
                var numOfRows = rows.length;
                var numOfColumns = rows[0].getElementsByClassName("control-class").length;
                for(var i=0;i<numOfRows;i++){
                    XMLString += "<ListItem>\n";
                    for(var j=0;j<numOfColumns;j++){
                        XMLString += "<SubItem>";
                        var cellValue = getValueFromTableCell(tableId,i,j);
                        cellValue =cellValue.toString().replace(/&/g,"&amp;").replace(/>/g, "&gt;").replace(/</g, "&lt;");
                        XMLString += cellValue;
                        XMLString += "</SubItem>\n";
                    }
                    XMLString += "</ListItem>\n";
                }
                XMLString += "</ListItems>\n";
                return XMLString;
            }
        }
    }

    function clearValue(controlId,sync)
    {
        var control = document.getElementById(controlId);
        if (control == null)
            control = document.getElementsByName(controlId)[0];
        if (control != null && control != undefined)
        {
            var nodes = [];
            nodes.push(control);
            var children = control.getElementsByClassName("control-class");
            for (var i = 0; i < children.length; i++) {
                nodes.push(children[i]);
            }
            // console.log(nodes);
            for (var i = 0; i < nodes.length; i++) {
                jsonObj = {};
                if (nodes[i].type == 'text' || nodes[i].type == 'select-one' || nodes[i].type == 'ComboBox' || nodes[i].type == 'textarea' || nodes[i].type == 'date') {
                    jsonObj[controlId] = "";
                } else if (nodes[i].tagName == 'LABEL') {
                    //nodes[i].innerHTML="";
                    jsonObj[controlId] = "";
                } else if (nodes[i].type == "radio" || nodes[i].id.indexOf('radio') != -1)
                {
                    //nodes[i].checked = false;
                    jsonObj[controlId] = "false";
                } else if (nodes[i].type == 'checkbox')
                {
                    //nodes[i].checked = false;
                    jsonObj[controlId] = "false";
                }
                else if (nodes[i].type == 'select-multiple'){
                     jsonObj[controlId] = [];
                }
                var patternRef = document.getElementById(controlId+"_patternMsg");
                var msgRef = document.getElementById(controlId+"_msg");
//                if(control.getAttribute("required"))
//                    toggleErrorTooltip(control,null,patternRef,true,0);
//                else
//                    toggleErrorTooltip(control,null,patternRef,false,0);      
                setValues(jsonObj, sync);
            }
        }
    }

    function setValues(jsonObj,sync){
        var url = "action_API.jsp";

        var attrTypes ={};
        var customJSON = {};
        for(key in jsonObj){           
            var control = document.getElementById(key);
            if(control == null || control.classList.contains("iform-radio")){
                control = document.getElementsByName(key)[0];
            }
            if(control!=null && $(control).hasClass("colorRange")){
             var id = $(control).parents().eq(1).find(".slider2").attr("id");
             control = document.getElementById(id);
            }
            var attrName = control.getAttribute("dataClass");
            if(attrName=="")
                attrName = control.name;
            if(attrName=="")
                attrName = control.id;
            if(control.type=='select-multiple'){
            setValue(key,jsonObj[key]);
            var lbJSON = {};
            for(var i=0;i<jsonObj[key].length;i++){
                lbJSON[i] = jsonObj[key][i];
            }
            customJSON[attrName] = lbJSON;
            attrTypes[attrName] = control.getAttribute("combotype");
        }
        else{
            jsonObj[key]=setValue(key,jsonObj[key]);//Bug 81232
            customJSON[attrName] = jsonObj[key];
            attrTypes[attrName] = control.getAttribute("datatype");
        }
        }
        var jsonString = JSON.stringify(customJSON);
        var attrTypesJSONString =  JSON.stringify(attrTypes);
        requestString = "setValuesFlag=yes&jsonString="+encode_utf8(jsonString)+"&attrTypesJSONString="+encode_utf8(attrTypesJSONString)+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid);
        if(sync==false)
            new net.ContentLoader(url, setValuesHandler, setValuesErrorHandler, "POST", requestString, true);
        else if(sync==true){
                iforms.ajax.processRequest(requestString, url);
        }
    }

    function setValuesHandler(){
        /*var controlsHeader = this.req.getResponseHeader("controls");
        var valuesHeader = this.req.getResponseHeader("values");
        var values = valuesHeader.split(",");
        var controls = controlsHeader.split(",");
        for(var i=0;i<controls.length;i++){
            var controlId  = controls[i].replace(/\./g, '_');
            setValue(controlId, values[i]);
        }*/
    }

    function setValuesErrorHandler(){
    }


    function setDataInControlsFromDB(query,controls,sync){  
        var url = "action_API.jsp";
        query = "controls="+controls.toString()+"&setdata="+query+"&syncFlag="+sync;
        if(sync==false)
            var contentLoaderRef = new net.ContentLoader(url, setDataInControlsHandler, DBErrorHandler, "POST", query, true);
        else if(sync==true){
            try{
                var jsonObj = JSON.parse(iforms.ajax.processRequest(query,url));
                var json={};
                if(jsonObj[0]==null){
                    for(var i=0;i<controls.split(",").length;i++){
                        var objComp = document.getElementById(controls.split(",")[i]);
                        if(objComp==null)
                        {
                            objComp =  document.getElementsByName(controls.split(",")[i])[0];
                        }
                        if((objComp.type=='text' && objComp.classList.contains("editableCombo")) || objComp.type=='select-one' || objComp.type=='ComboBox' || objComp.type=='select-multiple'){
                            populateComboValuesfromString(controls.split(",")[i],{});
                        }
                        else{
                            json[controls.split(",")[i]] =  "";
                        }
                    }
                    setValues(json,true);
                    return;
                }
                //Bug 75527 Start
                if(controls.split(",").length==1&&Object.keys(jsonObj).length==2){
                    populateComboValuesfromString(controls.split(",")[0],jsonObj[0],jsonObj[1]);
                }
                else
                {
                    for(var i=0;i<controls.split(",").length;i++)
                    {
                        var objComp = document.getElementById(controls.split(",")[i]);
                        if(objComp==null)
                        {
                            objComp =  document.getElementsByName(controls.split(",")[i])[0];
                        }
                        if((objComp.type=='text' && objComp.classList.contains("editableCombo")) ||objComp.type=='select-one' || objComp.type=='ComboBox' || objComp.type=='select-multiple'){
                            populateComboValuesfromString(controls.split(",")[i],jsonObj[i]);
                        }
                        else{
                            json[controls.split(",")[i]] =  jsonObj[i][0];
                        }
                    }
                    //Bug 75527 End
                    setValues(json,true);
                }
            }
            catch(ex){}
        }
    }

    function setDataInControlsHandler(){
        var jsonString = this.req.responseText;
        var controls = getQueryVariable(this.params, "controls");
        try{
            var jsonObj = JSON.parse(jsonString);
            var json = {};
            if(jsonObj[0]==null){
                for(var i=0;i<controls.split(",").length;i++){
                    var objComp = document.getElementById(controls.split(",")[i]);
                    if(objComp==null)
                    {
                        objComp =  document.getElementsByName(controls.split(",")[i])[0];
                    }
                    if((objComp.type=='text' && objComp.classList.contains("editableCombo")) || objComp.type=='select-one' || objComp.type=='ComboBox' || objComp.type=='select-multiple'){
                        populateComboValuesfromString(controls.split(",")[i],{});
                    }
                    else{
                        json[controls.split(",")[i]] =  "";
                    }
                }
                setValues(json,true);
                return;
            }
            //Bug 75527 Start
            if(controls.split(",").length==1&&Object.keys(jsonObj).length==2){
                populateComboValuesfromString(controls.split(",")[0],jsonObj[0],jsonObj[1]);
            }
            else{
                for(var i=0;i<controls.split(",").length;i++){
                    if(jsonObj[i]==null)
                        break;
                    var objComp = document.getElementById(controls.split(",")[i]);
                    if(objComp==null)
                    {
                        objComp =  document.getElementsByName(controls.split(",")[i])[0];
                    }
                    if((objComp.type=='text' && objComp.classList.contains("editableCombo")) || objComp.type=='select-one' || objComp.type=='ComboBox' || objComp.type=='select-multiple'){
                        populateComboValuesfromString(controls.split(",")[i],jsonObj[i]);
                    }
                    else{
                        json[controls.split(",")[i]] =  jsonObj[i][0];
                    }
                }
                //Bug 75527 End
                setValues(json,true);
            }
        }
        catch(ex){}
    //    for(var i=0;i<controls.split(",").length;i++){
    //        json[controls.split(",")[i]] =  jsonObj[i];
    //    }
    //    setValues(json,false);
    }

    function DBErrorHandler(){
    }

    function showMessage(control,msg,dialogType, isClose){
//        if(dialogType.toLowerCase() == "error")
//        {
//            msg="<div class='typeError' style='color:#ba3212;margin-bottom: 10px;'><span style='font-size:35px;padding-right: 10px;' class='glyphicon glyphicon-remove-sign'></span><span  style='font-size:16px;font-weight: bold;'>Something went wrong</span></div>"+msg;
//        }
//        if(dialogType.toLowerCase() == "warning")
//        {
//            msg="<div class='typeWarning' style='color:#b36106;margin-bottom: 10px;'><span style='font-size:35px;padding-right: 10px;' class='glyphicon glyphicon-exclamation-sign'></span><span style='font-size:16px;font-weight: bold;'>Warning</span></div>"+msg;
//            dialogType="error";
//        }
//        if(dialogType.toLowerCase() == "success")
//        {
//            msg="<div class='typeSuccess' style='color:#268844;margin-bottom: 10px;'><span style='font-size:35px;padding-right: 10px;' class='glyphicon glyphicon-ok-sign'></span><span style='font-size:16px;'>Success</span></div>"+msg;
//        }
//        if(dialogType.toLowerCase() == "info")
//        {
//            msg="<div class='typeInfo' style='color:#0072c6;margin-bottom: 10px;'><span style='font-size:35px;padding-right: 10px;' class='glyphicon glyphicon-info-sign'></span><span style='font-size:16px;'>Info</span></div>"+msg;
//        }
        
        if( isClose == undefined ){
            isClose = true;
        }
        if(dialogType.toLowerCase() == "error"){
            //showError(control,msg,"error");
            showBootBox(control, msg, "error",isClose)
        }
        else if(dialogType.toLowerCase() == "confirm"){
            //showError(control,msg,"confirm");
            showBootBox(control, msg, "confirm", isClose)
        }
    }
 
    function showBootBox(control,msg,type,isClose){
        if(type=='error'){
            bootbox.alert({ 
                size: "small",
                message: msg,
                closeButton: isClose, 
                callback: function(){
                    if(window.okOperation){
                        okOperation(control);
                    }
                    else{
                        if(control!=null)
                        {
                            this.modal('hide');
                            setFocus(control);
                        }
                    }
                }
            })

        }

        else if(type=='confirm'){
            bootbox.confirm({ 
                size: "small",
                message: msg, 
                buttons:{
                    cancel:{
                        label:'Cancel'
                    },
                    confirm:{
                        label:'Yes, Exit'
                    }
                },
                closeButton: isClose,  
                callback: function(result){
                    if(result==true){
                       if(window.okOperation){
                        okOperation(control);
                    }
                    else{
                        if(control!=null)
                        {
                            this.modal('hide');
                            setFocus(control);
                        }
                    } 
                    }
                    else{
                        if(window.cancelOperation){
                        cancelOperation(control);
                    }
                    else{
                        if(control!=null)
                        {
                             this.modal('hide');
                            setFocus(control);
                        }
                    } 
                    }

                }
            })
        }

    }

    //User defined custom code
    /*function okOperation(pComp){
        jQuery("#pnlDialog").dialog("close");
        if(pComp!=null)
        {
             clearTable("table1",true);
        }
    }

    function cancelOperation(pComp){
        jQuery("#pnlDialog").dialog("close");
        if(pComp!=null)
        {
             setValueInTableFromDB("ads","table1",true);
        }
    }
    */

    //function showConfirmDialog(pMsg,pButtons)
    //{            
    //    var objDialog = jQuery("#dlgContent");
    //    objDialog.html(pMsg);
    //    jQuery("#pnlDialog").dialog(
    //    {                
    //        resizable: false,
    //        height:160,
    //        modal: true,
    //		dialogClass:"oforms_dialog",
    //        buttons: pButtons,
    //        zIndex:9999,
    //        close:function( event, ui )
    //        {
    //            jQuery(".ui-widget-overlay").css("display","none");                        
    //        }
    //    });
    //    jQuery(".ui-dialog .ui-icon-closethick").attr("title", "Close");
    //    jQuery(".ui-dialog").css("z-index",9999);
    //    var arry = jQuery(".ui-button-text");  
    //    if( arry.length > 1 ){
    //        for( var p=0;p < arry.length; p++ ){
    //            if( arry.get(0).innerHTML == "Yes"){
    //            //arry.get(0).innerHTML = ALERT_YES;
    //            }
    //            else if( arry.get(0).innerHTML == "No"){
    //        //arry.get(0).innerHTML = ALERT_NO;
    //        }
    //        }
    //    }
    //    else{
    ////jQuery(".ui-button-text").text(ALERT_OK);
    //}
    //}

    //function showError(pComp,pMsg,dialogType){    
    //    if(pMsg==null || pMsg=="")
    //        return;
    //    if(dialogType == "error"){
    //        var buttons = 
    //        {
    //            Ok: function()
    //            {        
    //                if(window.okOperation){
    //                    okOperation(pComp);
    //                }
    //                else{
    //                    jQuery("#pnlDialog").dialog("close");
    //                    if(pComp!=null)
    //                    {
    //                        setFocus(pComp);
    //                    }
    //                }
    //            }
    //        };
    //    }
    //    else if(dialogType == "confirm"){
    //        var buttons = 
    //        {
    //            Ok: function()
    //            {
    //                if(window.okOperation){
    //                    okOperation(pComp);
    //                }
    //                else{
    //                    jQuery("#pnlDialog").dialog("close");
    //                    if(pComp!=null)
    //                    {
    //                        setFocus(pComp);
    //                    }
    //                }
    //                
    //            },
    //            Cancel: function()
    //            {   
    //                if(window.cancelOperation){
    //                    cancelOperation(pComp);
    //                }
    //                else{
    //                    jQuery("#pnlDialog").dialog("close");
    //                    if(pComp!=null)
    //                    {
    //                        setFocus(pComp);
    //                    }
    //                }
    //            }
    //        };
    //    }
    //            
    //    var origMsg = pMsg;
    //    if(window.getMessage)
    //    {
    //        if( typeof pComp == "string" ) 
    //            pMsg = getMessage(pComp,pMsg);
    //        else if( pComp instanceof jQuery)
    //            pMsg = getMessage(pComp.attr("id"),pMsg);
    //        else     
    //            pMsg = getMessage(pComp.id,pMsg);
    //    }
    //    if(pMsg == null || jQuery.trim(pMsg).length == 0)
    //        pMsg = origMsg;
    //    if( pMsg != "DONOTSHOWERRORMSG")
    //        showConfirmDialog(pMsg,buttons);
    //    if(window.removeMasking)
    //    {
    //        try
    //        {
    //            window.parent.hideProcessing();
    //        }
    //        catch(ex)
    //        {
    //                            
    //        }
    //        removeMasking();
    //    }
    ////alert(pMsg);
    //                            
    //}

    function getValueFromTableCell(tableId,rowIndex,colIndex)
    {
        var table = document.getElementById(tableId);
        //var row= table.getElementsByClassName("rowinhtml")[rowIndex];
        if (table != null && table != undefined)
        {
            //Bug 81548 
            //var row = table.getElementsByTagName("tr")[rowIndex + 1];
            var row = table.tBodies[0].getElementsByTagName("tr")[rowIndex];
            if(row != null && row != undefined)
            { 
		        var immidiateTD = jQuery(row).find(">td");
                var cell = immidiateTD[colIndex + 1];  
               // var cell = row.getElementsByTagName("td")[colIndex + 1];
                var control = cell.getElementsByClassName("control-class")[0];
                if( control === undefined ){
                    return cell.innerHTML;
                }
                if (cell != null && cell != undefined)
                {
                    if(jQuery(control).hasClass("radio-group")==true){
                         var childElement = control.children; 
                         for(var i=0;i<childElement.length;i++){
                             if(jQuery(childElement).hasClass("radioThree")){
                                var c = childElement[i];
                                if(jQuery(c).hasClass("active")){
                                    return c.children[0].value;
                                }
                             } else {
                                 var c = childElement[i].children[0];
                                 if(c.checked){
                                   return c.value;
                                 }
                             }
                             
                         }
                         return "";
                    }
                    if (control.type != undefined && control.type == "checkbox" || control.type == "radio")
                    {
                        //alert(control.checked);
                        return control.checked;
                    }
                    else if((table.getAttribute("type") == "Table" ||table.getAttribute("type") == "ListView") && (control.getAttribute("typeofvalue") == "Text" ||control.getAttribute("typeofvalue") == "Float"))//Bug 85502
                    {
                    if (control.getAttribute("maskingPattern") != null && control.getAttribute("maskingPattern") != undefined && control.getAttribute("maskingPattern") != '' && control.getAttribute("maskingPattern") != 'nomasking' && control.getAttribute("maskingPattern") != 'email') 
                    {
                        if (control.getAttribute("maskingPattern").toString() === 'currency_rupees' || control.getAttribute("maskingPattern").toString() === 'currency_dollar' || control.getAttribute("maskingPattern").toString() === 'currency_naira' || control.getAttribute("maskingPattern").toString() === 'currency_yen' || control.getAttribute("maskingPattern").toString() === 'currency_euro' || control.getAttribute("maskingPattern").toString() === 'currency_french' || control.getAttribute("maskingPattern").toString() === 'currency_greek' || control.getAttribute("maskingPattern").toString() === 'percentage' || control.getAttribute("maskingPattern").toString() === 'dgroup2' || control.getAttribute("maskingPattern").toString() === 'currency_bahamas' || control.getAttribute("maskingPattern").toString() === 'dgroup3' || control.getAttribute("maskingPattern").toString() === 'NZP')
                            return jQuery(control).autoNumeric('get');
                        else {
                            if (!control.getAttribute("datatype") == "date")
                                return jQuery(control).cleanVal();
                            else
                            {
                                if (control.tagName == "LABEL" || control.tagName == "A")
                                    return jQuery(control).cleanVal();
                                else
                                    return control.value;
                            }
                        }
                    }
                    else{
                        if (control.tagName == "LABEL" || control.tagName == "A")
                            return control.textContent;
                        else
                            return control.value;
                    }
                    }
                    else if (control.tagName == "LABEL" || control.tagName == "A"){
                        //alert(control.checked);
                        return control.textContent;
                    }
                    else if(control.tagName=="IMG"){
                           return control.src;
                    }
                    else{
                        //alert(control.value);
                        return control.value;
                    }
                }
            }
        }
    }


function getValueFromColumnName(tableId,rowIndex,colName)
    {
        var table = document.getElementById(tableId);
        if (table != null && table != undefined)
        {
            var children = table.parentNode.parentNode.childNodes;
            var theads = children[0].getElementsByTagName("th");
            var ColIndex = 0;
            for(var i=0;i<theads.length;i++){
                if(theads[i].innerText == colName)
                {
                    colIndex = i-1;
                    break;
                }
            }
            return getValueFromTableCell(tableId,rowIndex,colIndex);
        }
    }

    function setColumnVisible(tableId,colIndex,visibleFlag,sync)
    {
        // var table = document.getElementById(tableId);
        if (document.getElementById(tableId) != null && document.getElementById(tableId) != undefined)
        {
            var table = document.getElementById(tableId);
            var children = table.parentNode.parentNode.childNodes;
            var theads = children[0].getElementsByTagName("th");
                var url = "action_API.jsp";
            if(colIndex==''){

                var requestString = "tableId=" + tableId + "&colIndex=" + colIndex + "&visibleFlag=" + visibleFlag;
                if (sync === false) {
                    new net.ContentLoader(url, setColumnVisibleHandler, setColumnVisibleErrorHandler, "POST", requestString, true);
                } else if (sync === true) {
                    iforms.ajax.processRequest(requestString, url);
                    setColumnVisibleHelper(tableId, colIndex, visibleFlag);
                }
            }
            else if(colIndex <= (theads.length-2) && colIndex >= 0)
            {
                var requestString = "tableId=" + tableId + "&colIndex=" + colIndex + "&visibleFlag=" + visibleFlag;
                if (sync === false) {
                    new net.ContentLoader(url, setColumnVisibleHandler, setColumnVisibleErrorHandler, "POST", requestString, true);
                } else if (sync === true) {
                    iforms.ajax.processRequest(requestString, url);
                    setColumnVisibleHelper(tableId, colIndex, visibleFlag);
        }
    }
        }
    }


    function setColumnVisibleHandler(){
        var tableId = getQueryVariable(this.params, "tableId");
        var colIndex = getQueryVariable(this.params, "colIndex");
        var visibleFlag = getQueryVariable(this.params, "visibleFlag");
        setColumnVisibleHelper(tableId,parseInt(colIndex),visibleFlag);
    }

    function setColumnVisibleErrorHandler(){

    }

    function setColumnVisibleHelper(tableId,colIndex,flag)
    {
        var tds = document.getElementById(tableId).getElementsByTagName("td");
        var table = document.getElementById(tableId);
 
        if(table != null && table != undefined)
        { 
            var col=[]; 
            var children = table.parentNode.parentNode.childNodes;
            var theads = children[0].getElementsByTagName("th");
            var innerTheadsLength=0;
            var innerTheads="";
            if(table.tHead!=null && table.tHead!=undefined){
             innerTheads = table.tHead.getElementsByTagName("th");
             innerTheadsLength = innerTheads.length;
            }
            col.push(theads[colIndex+1]);
            var j=0;
            for(var i=0;i<colIndex+1;i++){
                if(theads[i].style.display!="none")
                    j++;
            }
            if(flag=="false" && innerTheadsLength>j)
                col.push(innerTheads[j]);
            var rows = table.tBodies[0].getElementsByTagName("tr");
            var cells=[];  
 
            for (var i = 0; i < rows.length; i++)
            {
                // if(<=theads.length)
                if (rows[i].getElementsByTagName("td").length > 0)
                    cells.push(rows[i].getElementsByTagName("td")[colIndex + 1]);
                if (rows[i].getElementsByTagName("th").length > 0){
                    cells.push(rows[i].getElementsByTagName("th")[colIndex + 1]);
                }
            }
            if(table.tFoot!=null && table.tFoot!=undefined){
            var rows = table.tFoot.getElementsByTagName("tr");
            
             for (var i = 0; i < rows.length; i++){
            
                if (rows[i].getElementsByTagName("th").length > 0){
                    cells.push(rows[i].getElementsByTagName("th")[colIndex + 1]);
                }
            }
            }
            
            for (var i = 0; i < cells.length; i++)
                col.push(cells[i]);
 
            // console.log(col);
            if (flag === "false" || flag == false) {
                for (var i = 0; i < col.length; i++) {
                    if (col[i] != null)
                        col[i].style.display = "none";
                }
            } else if (flag === "true" || flag == true) {
                for (var i = 0; i < col.length; i++) {
                    col[i].style.display = "";
                }
            }
 
            $("#" + tableId).floatThead('reflow');
 
        }
    }


    function setColumnDisable(tableId,colIndex,disableFlag,sync){
        var control = document.getElementById(tableId);
        var dateicons = control.getElementsByClassName("glyphicon-calendar");
        var url = "action_API.jsp";
        var requestString = "tableId="+tableId+"&colIndex="+colIndex+"&disableFlag="+disableFlag;
        if(sync===false){
            new net.ContentLoader(url, setColumnDisableHandler, setColumnDisableErrorHandler, "POST", requestString, true);
        }
        else if(sync===true){
 //           if(isDatePicker=="N")
 //           {
 //               for(var i=0;i<dateicons.length;i++)
 //               {
 //                   if(disableFlag == "true")
 //                       dateicons[i].style.visibility = "hidden";
 //                   else
 //                       dateicons[i].style.visibility = "";
 //               }
 //           }
                iforms.ajax.processRequest(requestString,url);
            setColumnDisableHelper(tableId,colIndex,disableFlag);
        }
    }
    
    function setMultiColumnDisable(tableId,colIndex,disableFlag,sync){
        var control = document.getElementById(tableId);
        var dateicons = control.getElementsByClassName("glyphicon-calendar");
        var url = "action_API.jsp";
        var requestString = "tableId="+tableId+"&colIndex="+colIndex+"&multi=Y&disableFlag="+disableFlag;
        if(sync===false){
            new net.ContentLoader(url, setMultiColumnDisableHandler, setColumnDisableErrorHandler, "POST", requestString, true);
        }
        else if(sync===true){
            if(isDatePicker=="Y")
            {
                for(var i=0;i<dateicons.length;i++)
                {
                    if(disableFlag == "true")
                        dateicons[i].style.visibility = "hidden";
                    else
                        dateicons[i].style.visibility = "";
                }
            }
            iforms.ajax.processRequest(requestString,url);
            setMultiColumnDisableHandler(tableId,colIndex,disableFlag);
        }
    }
    
    function setMultiColumnDisableHandler(){
        var tableId = getQueryVariable(this.params, "tableId");
        var colIndex = getQueryVariable(this.params, "colIndex");
        var disableFlag = getQueryVariable(this.params, "disableFlag");
        var control = document.getElementById(tableId);
        var dateicons = control.getElementsByClassName("glyphicon-calendar");
        if(isDatePicker=="Y")
        {
            for (var i = 0; i < dateicons.length; i++)
            {
                if (disableFlag == "true")
                    dateicons[i].style.visibility = "hidden";
                else
                    dateicons[i].style.visibility = "";
            }
        }
        
        var cols = colIndex.split(",")
        for(var p=0;p<cols.length;p++ ){
            var coli = cols[p].trim();
            setColumnDisableHelper(tableId,parseInt(coli),disableFlag);
        }
    }

    function setColumnDisableHandler(){
        var tableId = getQueryVariable(this.params, "tableId");
        var colIndex = getQueryVariable(this.params, "colIndex");
        var disableFlag = getQueryVariable(this.params, "disableFlag");
            var control = document.getElementById(tableId);
        var dateicons = control.getElementsByClassName("glyphicon-calendar");
 //       if(isDatePicker=="N")
 //       {
 //           for (var i = 0; i < dateicons.length; i++)
 //           {
 //               if (disableFlag == "true")
 //                   dateicons[i].style.visibility = "hidden";
 //               else
 //                   dateicons[i].style.visibility = "";
 //           }
 //       }
        setColumnDisableHelper(tableId,parseInt(colIndex),disableFlag);
    }

    function setColumnDisableHelper(tableId,colIndex,flag){
        var tds = document.getElementById(tableId).getElementsByTagName("td");
        var col=[];
        var table = document.getElementById(tableId);
        var children = table.parentNode.parentNode.childNodes;
        var theads = children[0].getElementsByTagName("th");
        var innerTheads = table.getElementsByTagName("th");
        //col.push(theads[colIndex+1]);
        //col.push(innerTheads[colIndex+1]);
        var rows = table.tBodies[0].getElementsByTagName("tr");
        var cells=[];
        for(var i=0;i<rows.length;i++){
	if(isDatePicker=="Y"){
             var tdd = rows[i].getElementsByTagName("td");
           
                var obj = jQuery(tdd[colIndex]).find(".glyphicon-calendar")[0];
				if(obj!=undefined){
                 if(flag == "true"){
                        obj.style.visibility = "hidden";
                    }  
                    else{
                        obj.style.visibility = "";
                    }
                }  
          }
            cells.push(rows[i].getElementsByTagName("td")[colIndex+1]);
        }
        for(var i=0;i<cells.length;i++)
            col.push(cells[i]);

        // console.log(col);
        if(flag==="false" || flag==false){
            for(var i=0;i<col.length;i++){
                if( col[i].getElementsByClassName("control-class")[0]!=undefined){
                    col[i].getElementsByClassName("control-class")[0].disabled = false;
					col[i].getElementsByClassName("control-class")[0].classList.remove("disabledTableFont");
                    var type = col[i].getElementsByClassName("control-class")[0].tagName;
                    if(type=="A" || type=="IMG"){
                        col[i].getElementsByClassName("control-class")[0].removeAttribute("disabled");
                        col[i].getElementsByClassName("control-class")[0].style.pointerEvents = "";
                        col[i].getElementsByClassName("control-class")[0].style.cursor = "";
                    }
                    if(jQuery(col[i].children[0]).hasClass("radio-group")==true){
                        var cn= col[i].children[0];
                        var childElement = cn.children; 
                         for(var k=0;k<childElement.length;k++){
                             var c = childElement[k].children[0];
                              c.removeAttribute('disabled');

                         }
                    }
                }
            }
        }
        else if(flag==="true" || flag==true){
            for(var i=0;i<col.length;i++){
                if( col[i].getElementsByClassName("control-class")[0]!=undefined){
                    col[i].getElementsByClassName("control-class")[0].disabled = true;
                    col[i].getElementsByClassName("control-class")[0].classList.add("disabledTableFont");
                    var type = col[i].getElementsByClassName("control-class")[0].tagName;
                     if(type=="A" || type=="IMG"){
                        col[i].getElementsByClassName("control-class")[0].style.pointerEvents = "none";
                        col[i].getElementsByClassName("control-class")[0].style.cursor = "default";
                    }
                    if(jQuery(col[i].children[0]).hasClass("radio-group")==true){
                        var cn= col[i].children[0];
                        var childElement = cn.children; 
                         for(var k=0;k<childElement.length;k++){
                             var c = childElement[k].children[0];
                              c.disabled=true;

                         }
                    }
                   
                }
            }
        }

    //    $("#"+tableId).floatThead('reflow');
    }

    function setColumnDisableErrorHandler(){

    }

    function setTableCellColor(tableId,rowIndex,colIndex,color)
    {
        var table = document.getElementById(tableId);
        // var row= table.getElementsByClassName("rowinhtml")[rowIndex];
        if(table != null && table !=undefined)
        {
        var row = table.tBodies[0].getElementsByTagName("tr")[rowIndex];
        var col = row.getElementsByTagName("td")[colIndex+1];
        col.style.backgroundColor = "#"+color;
        }
    }

    function setCellDisabled(tableId,rowIndex,colIndex,flag)
    {
        var table = document.getElementById(tableId);
        if (table != null && table != undefined)
        {
            // var row= table.getElementsByClassName("rowinhtml")[rowIndex];
            var row = table.tBodies[0].getElementsByTagName("tr")[rowIndex];
            var col = row.getElementsByTagName("td")[colIndex + 1];
            if(row != null && row != undefined )
            {
                var control = col.getElementsByClassName("control-class")[0];
            if (col != null && col != undefined)
            {
                if (flag == "true"){
                    control.disabled = "true";
                    jQuery(control).parent().css("pointerEvents","none");
					jQuery(control).css("pointerEvents","none");
                    jQuery(control).parent().css("cursor","default");
                    if(control.classList.contains("btn")){
                        control.classList.add("disabledCellBtn")
                    }
                    if(jQuery(control).hasClass("radio-group")==true){
                         var childElement = control.children; 
                         for(var k=0;k<childElement.length;k++){
                             var c = childElement[k].children[0];
                              c.disabled ="true";
                         }
                    }
                    var dateicons = document.getElementById(tableId).getElementsByClassName("glyphicon-calendar");
                    if(isDatePicker=="Y")
                    {
                        for (var i = 0; i < dateicons.length; i++) {
                            dateicons[i].style.visibility = "hidden";
                        }
                    }else{
                        for (var i = 0; i < dateicons.length; i++) {
                            dateicons[i].style.visibility = "";
                        }
                    }
//                    if(control.tagName=="A" || control.tagName=="IMG")
//                    {
//                        
//                        control.style.pointerEvents = "none";
//                        control.style.cursor = "default";
//                    }
                }
                else if (flag == "false") {
                    control.removeAttribute("disabled");
                    jQuery(control).parent().css("pointerEvents","auto");
                    jQuery(control).parent().css("cursor","");
                    jQuery(control).css("pointerEvents","auto");
                    jQuery(control).css("cursor","");
                    if(control.classList.contains("btn")){
                        control.classList.remove("disabledCellBtn")
                    }
                    if(jQuery(control).hasClass("radio-group")==true){
                         var childElement = control.children; 
                         for(var k=0;k<childElement.length;k++){
                             var c = childElement[k].children[0];
                              c.removeAttribute("disabled");
                         }
                    }
                    var dateicons = document.getElementById(tableId).getElementsByClassName("glyphicon-calendar");
                    if(isDatePicker=="Y")
                    {
                        for (var i = 0; i < dateicons.length; i++) {
                            dateicons[i].style.visibility = "";
                        }
                    }
//                    if(control.tagName=="A"|| control.tagName=="IMG")
//                    {
//                        control.style.pointerEvents = "";
//                        control.style.cursor = "";
//                    }
                }
            }
            }
        }
    }

    function removeRow(tableId,rowIndex)
    {
        var table = document.getElementById(tableId);
        if (table != null && table != undefined )
        {
            //var rows = table.getElementsByClassName('rowinhtml'); 
            var rows = table.tBodies[0].getElementsByTagName('tr');
            if(rows != null && rows != undefined && rows[rowIndex] != null && rows.length != null)
            {
                rows[rowIndex].remove();
            }
        }
    }



    function setTableData(tableId,jsonObj,sync){
        var url = "action_API.jsp";
            var rowIndex,colIndex,cellData;
        var jsonString = JSON.stringify(jsonObj);
        var requestString = "setTableDataFlag=yes&tableId="+tableId+"&jsonString="+encode_utf8(jsonString)+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid);

        if(sync=="false" || sync==false){
            new net.ContentLoader(url, setTableDataHandler, formErrorHandler, "POST", requestString, true);
        }
        else if(sync=="true" || sync==true){
            iforms.ajax.processRequest(requestString, url);
            for(key in jsonObj){
                rowIndex = key.split(",")[0];
                colIndex = key.split(",")[1];
                cellData = jsonObj[key];
                setTableDataHelper(tableId, rowIndex, colIndex, cellData);
            }
        }
    }

    function setTableDataHandler(){
        var rowIndices = this.req.getResponseHeader("rowIndices");
        var colIndices = this.req.getResponseHeader("colIndices");
        var cellDatas = this.req.getResponseHeader("cellDatas");
        var tableId = getQueryVariable(this.params, "tableId");
        for(var i=0;i<rowIndices.length;i++){
            setTableDataHelper(tableId, rowIndices.split(",")[i], colIndices.split(",")[i], cellDatas.split(",")[i]);
        }
    }

    function setTableDataHelper(tableId,rowIndex,colIndex,cellData){
        var table = document.getElementById(tableId);
        if(table==null)
        {
            table =  document.getElementsByName(tableId)[0];
        }
        var row;
       if(table.getElementsByTagName("tr")[parseInt(rowIndex)+1]){
            row = table.getElementsByTagName("tr")[parseInt(rowIndex)+1];
            if(row.style.display=="none"){
                     row = table.getElementsByClassName("rowinhtml")[parseInt(rowIndex)];
            }
        }
        else
            return;
        var col;
        if(row.getElementsByTagName("td")[parseInt(colIndex)+1])
            col = row.getElementsByTagName("td")[parseInt(colIndex)+1];
        else
            return;
        var control = col.getElementsByClassName("control-class")[0];
        if(control.type=='text'||control.type=="textarea"){
            control.value=cellData;
        }//Bug 76737 End
        if(control.type=='select-one' || control.type=='ComboBox')
        {   
            control.value=cellData;   
        }
        else if(control.tagName=='LABEL')
        {
            control.innerHTML=escapeStringForHTML(cellData);
        }
        else if(control.type=='radio')
        {
            if(cellData=="true")
                control.checked=true;
            else
                control.checked=false;
        }
        else if(control.type=='checkbox')
        {
            if(cellData.toLowerCase()=="true")
            {
                control.checked=true;
            }
            else
            {
                control.checked=false;
            }
        }
        else
            control.value = cellData;
    }


    function getSelectedIndex(controlId){
        return $("select[id="+controlId+"] option:selected").index();
    }


    function saveWorkItem(bshowMsg) {
        if (bshowMsg == undefined)
            bshowMsg = true;
        if (typeof applicationName != 'undefined' && (applicationName == null || applicationName == ''))
        {
            if (mobileMode == "ios" || mobileMode == "android") {
                saveForm('S', true);
            } else {
                if (window.parent && window.parent.WFSave) {
                    window.parent.WFSave(bshowMsg)
                }
            }
        } 
        else{
            saveForm("SF", true);
        }
    }

    function completeWorkItem(){
        if(mobileMode=="ios"||mobileMode=="android"){
            saveForm('D',false);
        }
        else{
           if( window.parent &&  window.parent.WFDone){
              window.parent.WFDone();
           }
        }
    }

    function getStyle(controlId,styleName){
        if(styleName.toLowerCase()=="backcolor"){
            return jQuery('#'+controlId).css("background-color");
        }

        else if(styleName.toLowerCase()=="visibility"){
            return jQuery('#'+controlId).css("background-color");
        }

        else if(styleName.toLowerCase()=="islocked"){
            return $('#'+controlId).is('[readonly]');
        }

        else if(styleName.toLowerCase()=="isdisabled"){
            return $('#'+controlId).is('[disabled]');
        }

    }


    function getValue(controlId)
    {
        var control = document.getElementById(controlId);
        if(useCustomIdAsControlName && (control==null || control==undefined)){
	            control = document.getElementsByName(controlId)[0];
	            if(control != null && control != undefined)
	              controlId = control.getAttribute("id");
	        }
        if (control != null && control != undefined)
        {
            if (control != undefined && control.classList.contains("iform-radio")) {
                var buttons = document.getElementsByName(controlId);
                for (var i = 0; i < buttons.length; i++) {
                    if (buttons[i].checked) {
                        return buttons[i].value;
                        break;
                    }
                }
            }
            if($(control).hasClass("colorRange")){
                return $(control).parents().eq(1).find(".slider2").attr('value');
            } if($(control).hasClass("slider2")){
                return $(control).attr('value');
            }
            if (control.type == "textarea" || control.type == "text" || control.type == "date") {
                if(control.getAttribute("AutoCompleteValue")!=null && control.getAttribute("AutoCompleteValue")!=undefined){
                    return control.getAttribute("AutoCompleteValue");
                }
                if(control.getAttribute("datatype") == "combobox"){
                    var ele = control.parentNode.getElementsByClassName("es-visible");
                    if( ele != null && ele > 0 ){
                        //if(ele[0].getAttribute("value")==null || ele[0].getAttribute("value")==undefined) return "";
                        return ele[0].getAttribute("originalValue");
                    }
                }
                return getControlValue(control);//Bug 81189
            } else if (control.type == "checkbox") {
                return control.checked;
            } else if (control.type == "select-one") {
                if(control.selectedIndex>=0)
                    return control.options[control.selectedIndex].value;
            }
            //Bug 83359 starts
            else if (control.type == "select-multiple" || control.type == "email") {
                return jQuery(control).val();
            }
            //Bug 83359 ends
            
        else if(control.tagName == "LABEL")
        {
            return control.innerHTML;
        }
    }
}

    function getSelectedItemLabel(comboId)
    {
        var combo = document.getElementById(comboId);
        if( useCustomIdAsControlName && (combo==null || combo==undefined)){
            combo = document.getElementsByName(comboId)[0];
            comboId = combo.getAttribute("id");
        }
        if (combo != null && combo != undefined)
        {
            if (combo.getAttribute("datatype") == "combobox") {

            if (combo.type == "text") {
                var i = 0;
                var ele = combo.parentNode.getElementsByClassName("es-visible");
                var listItems = jQuery(combo.parentNode).find("li");
                if (ele != null && ele.length > 0) {
                    for (var p = 0; p < listItems.length; p++) {
                        if (jQuery(listItems[p]).hasClass("es-visible") && jQuery(listItems[p]).hasClass("es-visible") == combo.value)
                            return listItems[p].innerHTML;
                    }
                }
                return combo.value;
            }
            else {
                var selectedText = combo.options[combo.selectedIndex].text;
                return selectedText;
            }

        }
        }
    }
	
    function getItemLabel(comboId,index)
    {
        var combo = document.getElementById(comboId);
        if( useCustomIdAsControlName && (combo==null || combo==undefined)){
            combo = document.getElementsByName(comboId)[0];
            if(combo != null && combo != undefined)
              comboId = combo.getAttribute("id");
        }
        if (combo != null && combo != undefined)
        {
            return combo.options[index].text;
        }
    }

    function addItemInCombo(comboId,label,value,tooltip,optionControlId,isReload)
    {   
        try{
        var combo = document.getElementById(comboId);
        if( useCustomIdAsControlName && (combo==null || combo==undefined)){
            combo = document.getElementsByName(comboId)[0];
            if(combo != null && combo != undefined)
              comboId = combo.getAttribute("id");
        }
        if(combo.tagName!="SELECT"){
            var fieldElements = document.getElementsByName(comboId);
            for(var i=0;i<fieldElements.length;i++)
            {
                if(fieldElements[i].tagName == "SELECT")
                    combo=document.getElementsByName(comboId)[i];
            }
        }
        var option;
		var allowDuplicates = false;
        if(window.allowDuplicateInDropDown){
            allowDuplicates = allowDuplicateInDropDown(comboId);
        }
        if (combo != null && combo != undefined)
        {
            //Bug 81099 If a field is mapped , the mapped field is coming twice in a dropdown
            var selectedValue=combo.value;
			if(!allowDuplicates){
            if (combo.tagName == 'SELECT') {
                for( var len = combo.options.length-1 ; len >= 0 ; len-- ){
                    if( combo.options[len].innerText === label ){
                        combo.remove(len);
                }   
                }
            }
            else{//Bug 83222 Start
                var ul = combo.parentNode.childNodes[2];
                for(var i=ul.childNodes.length-1;i>=0;i--){
                    if(ul.childNodes[i].innerHTML==label)
                        ul.removeChild(ul.childNodes[i]);
                }
            }//Bug 83222 End
            } 
    //        var hasOption = $('#'+comboId+' option:contains(' + label + ')');        
    //        if (hasOption.length> 0) {
    //            combo.remove(hasOption.index());
    //        }   
            option = document.createElement('option');        
            if (combo.tagName == 'SELECT') {
                if( typeof optionControlId != "undefined" ){
                    option.id = optionControlId;
                }
                if (typeof label != "undefined" && typeof value == "undefined" && typeof tooltip == "undefined") {
                    option.text = label;
                    option.value = label;//Bug 84292
                    combo.add(option);
                }
                if (typeof label != "undefined" && typeof value != "undefined" && typeof tooltip == "undefined") {
                    option.text = label;
                    option.value = value;
                    combo.add(option);
                }

                if (typeof label != "undefined" && typeof value != "undefined" && typeof tooltip !== "undefined") {
                    option.text = label;
                    option.value = value;
                    option.setAttribute("data-toggle", "tooltip");
                    option.title = tooltip;
                    combo.add(option);                              
                }
                if(combo.multiple){
                    reloadListBoxLayout(comboId);
                }
				if(isReload !=="false"){
                    if (combo.multiple) {
                        reloadListBoxLayout(comboId);
                    }
				}
            } else {
                var liElem = document.createElement('li');
                jQuery(liElem).addClass("es-visible");
                if( typeof optionControlId != "undefined" ){
                    liElem.id = optionControlId;
                    liElem.setAttribute("originalValue","");
                }
                if (typeof label != "undefined" && typeof value == "undefined" && typeof tooltip == "undefined") {
                    liElem.appendChild(document.createTextNode(label));
                    liElem.setAttribute("originalValue","");
                }
                if (typeof label != "undefined" && typeof value != "undefined" && typeof tooltip == "undefined") {
                    liElem.appendChild(document.createTextNode(label));
                    liElem.setAttribute("value", value);
                    liElem.setAttribute("originalValue",value);
                }

                if (typeof label != "undefined" && typeof value != "undefined" && typeof tooltip !== "undefined") {
                    liElem.appendChild(document.createTextNode(label));
                    liElem.setAttribute("value", value);
                    liElem.setAttribute("originalValue",value);
                    liElem.title = tooltip;
                }
                var ul = combo.parentNode.childNodes[2];
                liElem.style.display = "block";
                ul.appendChild(liElem);
            }
            //Bug 81099 If a field is mapped , the mapped field is coming twice in a dropdown
            combo.value=selectedValue;
        }
        }
        catch(ex){}
    }

    function reloadListBoxLayout(listboxId){
        var listBox = document.getElementById(listboxId);
        jQuery(listBox).multiselect('rebuild');
        $(listBox).siblings().find('.multiselect-container .checkbox').addClass('inputStyle');
        $(listBox).siblings().find('.multiselect-container .checkbox').css("border","0px");
        $(listBox).siblings().find('.dropdown-toggle').addClass('inputStyle');   
        //Bug 81918 - setStyle() API not working on multiselect 
        if(listBox.disabled){
            $(listBox).siblings().find('.dropdown-toggle').addClass('disabledBGColor');
            $(listBox).siblings().find('.dropdown-toggle').attr("disabled",true);
        }else{
            $(listBox).siblings().find('.dropdown-toggle').removeClass('disabledBGColor');
            $(listBox).siblings().find('.dropdown-toggle').removeAttr("disabled");
        }          
        $(listBox).siblings().find('.dropdown-toggle .caret').css('float',"right"); 
        $(listBox).siblings().find('.dropdown-toggle').attr("tooltip",jQuery(listBox).attr("tooltip"));
        $(listBox).siblings().find('.multiselect-container .checkbox').css("text-align",$(listBox).css("text-align"));
        $(listBox).siblings().find('.multiselect-container .checkbox').css("font-size",$(listBox).css("font-size"));
        $(listBox).siblings().find('.multiselect-container .checkbox').css("font-weight",$(listBox).css("font-weight"));
        $(listBox).siblings().find('.multiselect-container .checkbox').css("font-style",$(listBox).css("font-style"));
        $(listBox).siblings().find('.multiselect-container .checkbox').css("font-family",$(listBox).css("font-family"));
        $(listBox).siblings().find('.multiselect-container .checkbox').css("background-color",$(listBox).css("background-color"));
        $(listBox).siblings().find('.multiselect-container .checkbox').css("color",$(listBox).css("color"));       
        $(listBox).siblings().find('.dropdown-toggle').css("text-align",$(listBox).css("text-align"));
        $(listBox).siblings().find('.dropdown-toggle').css("font-size",$(listBox).css("font-size"));
        $(listBox).siblings().find('.dropdown-toggle').css("font-weight",$(listBox).css("font-weight"));
        $(listBox).siblings().find('.dropdown-toggle').css("font-style",$(listBox).css("font-style"));
        $(listBox).siblings().find('.dropdown-toggle').css("font-family",$(listBox).css("font-family"));
        $(listBox).siblings().find('.dropdown-toggle').css("background-color",$(listBox).css("background-color"));
        $(listBox).siblings().find('.dropdown-toggle').css("color",$(listBox).css("color"));        
    }

    function getItemCountInCombo(comboId){
        var combo = document.getElementById(comboId);
        if( useCustomIdAsControlName && (combo==null || combo==undefined)){
            combo = document.getElementsByName(comboId)[0];
            if(combo != null && combo != undefined)
              comboId = combo.getAttribute("id");
        }
        if (combo != null && combo != undefined)
        {
            return combo.length;
        }
    }

    function getSheetIndex(tabId){

        return $('#'+tabId+' .iformTabUL .active').index();
    }

    function removeItemFromCombo(comboId,itemIndex)
    {
        var combo = document.getElementById(comboId);
        if( useCustomIdAsControlName && (combo==null || combo==undefined)){
            combo = document.getElementsByName(comboId)[0];
            if(combo != null && combo != undefined)
              comboId = combo.getAttribute("id");
        }
        if (combo != null && combo != undefined)
        {
            combo.remove(itemIndex);
    //    for(var i=0;i<combo.length;i++){
    //        if(combo.options[i].label==label){
    //            combo.remove(i);
    //        }
    //    }
        }
        try{
        if(combo.multiple){
            reloadListBoxLayout(comboId);
        }
    }catch(ex){}
    }
    
    function selectSheetByName(tabID, sheetName) {
        if (document.getElementById(tabID) != null && document.getElementById(tabID) != undefined)
        {
            var index=0;
            var links = document.getElementById(tabID).getElementsByTagName("a");
            for(var i=0;i<links.length;i++){
                if(links[i].innerText==sheetName){
                    index=i;
                    break;
                }
            }
            links[index].click();
        }
    }
    
    function selectSheet(tabID, index) {
        if (document.getElementById(tabID) != null && document.getElementById(tabID) != undefined)
        {
            var links = document.getElementById(tabID).getElementsByTagName("a");
            links[index].click();
        }
    }

    function setFocus(controlId,isListView){
        isListView=typeof isListView =="undefined"?false:isListView;
        var control = document.getElementById(controlId);
        if(useCustomIdAsControlName && (control==null || control==undefined)){
            control = document.getElementsByName(controlId)[0];
            if(control != null && control != undefined)
              controlId = control.getAttribute("id");
        }
        var checkedRadio;
        if(control!=null && control != undefined )
        {
           if(!isListView){
            var tab = control;
            var isTab = false;
            while(tab!=null && tab.classList!=null&&!tab.classList.contains("iformTabControl")){
                tab = tab.parentNode;
            }
            if(tab!=null && tab.classList!=null&&tab.classList.contains("iformTabControl")){
                isTab=true;
            }
            var section=control;
            var isSection=false;
            while(section!=null&&section.classList!=null&&!section.classList.contains("FrameControl")){
                section=section.parentNode;
            }
            if(section!=null &&section.classList!=null&& section.classList.contains("FrameControl")){
                isSection=true;
            }
            var ref;
            if(isSection){
                if(section.firstChild!=null&&section.firstChild.getAttribute("state")!=null&&section.firstChild.getAttribute("state")=="collapsed"){
                   ref=section.firstChild;
                   jQuery(ref).siblings().toggle(450,function(){
                        jQuery(ref).find("img").attr("src","resources/images/Arrows-"+(jQuery(ref).siblings().is(":visible") ? "Up" : "Down")+"-4-icon.png");
                        if((jQuery(ref).siblings().is(":visible")))
                        {
                            ref.parentNode.scrollIntoView(false);  
                        }
                        var sectionState;
                        if(jQuery(ref).attr("state") == "collapsed")
                        {
                        sectionState="expanded";
                        jQuery(ref).attr("state","expanded"); 
                        }
                        else
                        {
                            sectionState="collapsed";
                            jQuery(ref).attr("state","collapsed");           
                        } 
                        if(window.onChangeSectionState)
                            window.onChangeSectionState(jQuery(ref).parent().attr("id"),sectionState);
                        var secondSection=section.parentNode;
                        var isSecondSection=false;
                        while(secondSection!=null&&secondSection.classList!=null&&!secondSection.classList.contains("FrameControl")){
                            secondSection=secondSection.parentNode;
                        }
                        if(secondSection!=null &&secondSection.classList!=null&& secondSection.classList.contains("FrameControl")){
                            isSecondSection=true;
                        }
                        if(isSecondSection){
                            if(secondSection.firstChild!=null&&secondSection.firstChild.getAttribute("state")!=null&&secondSection.firstChild.getAttribute("state")=="collapsed"){
                                ref=secondSection.firstChild;
                                jQuery(ref).siblings().toggle(450,function(){
                                    jQuery(ref).find("img").attr("src","resources/images/Arrows-"+(jQuery(ref).siblings().is(":visible") ? "Up" : "Down")+"-4-icon.png");
                                    if((jQuery(ref).siblings().is(":visible")))
                                    {
                                        ref.parentNode.scrollIntoView(false);  
                                    }
                                    var sectionState;
                                    if(jQuery(ref).attr("state") == "collapsed")
                                    {
                                    sectionState="expanded";
                                    jQuery(ref).attr("state","expanded"); 
                                    }
                                    else
                                    {
                                        sectionState="collapsed";
                                        jQuery(ref).attr("state","collapsed");           
                                    } 
                                    if(window.onChangeSectionState)
                                        window.onChangeSectionState(jQuery(ref).parent().attr("id"),sectionState);
                                    if(isTab==true){
                                        var tabli = control;
                                        while(!jQuery(tabli).hasClass("tab-pane") ){
                                            tabli = tabli.parentNode;
                                        }
                                        try{
                                        var children = tab.getElementsByTagName("a");
                                        for(var i=0;i<children.length;i++){
                                            if(children[i].href.indexOf("#"+tabli.id)!=-1 && children[i].parentElement.style.display!="none"){
                                                break;
                                            }
                                        }
                                        }
                                        catch(ex){}
                                        $("[href='#"+tabli.id+"']").on('shown.bs.tab', function (e) {
                                            if(control!=null && control.tagName == "BUTTON"){
                                                control.setAttribute("autofocus","");
                                                if(control.getAttribute("combotype")=="listbox"){
                                                    jQuery(control).parent().find("button").get(0).focus();
                                                }
                                                else
                                                {
                                                    control.focus();
                                                }
                                            }
                                            else if(control.classList.contains("iform-radio")){
                                                checkedRadio =  getCheckedRadioRef(control);
                                                checkedRadio.focus();
                                            }
                                            else{
                                                if(control.getAttribute("combotype")=="listbox"){
                                                    jQuery(control).parent().find("button").get(0).focus();
                                                }
                                                else
                                                {
                                                    control.focus();
                                                }
                                            }
                                            //control.focus();
                                        });
                                        $("[href='#"+tabli.id+"']").off('shown.bs.tab');
                                        selectSheet(tab.id,i);
                                        $("[href='#"+$("#"+tab.id).find(".tab-pane")[i].getAttribute("id")+"']").on('shown.bs.tab', function (e) {
                                            validateMandatoryFields();
                                         });
                                    }
                                    if(control!=null && control.tagName == "BUTTON"){
                                        control.setAttribute("autofocus","");
                                        control.focus();
                                    }
                                    else if(control.classList.contains("iform-radio")){
                                        checkedRadio =  getCheckedRadioRef(control);
                                        checkedRadio.focus();
                                    }
                                    else{
                                        if(control.getAttribute("combotype")=="listbox")
                                        {
                                            jQuery(control).parent().find("button").get(0).focus();
                                        }
                                        else
                                        {
                                            control.focus();
                                        }
                                    }
                                }
                                );
                                var url = "action_API.jsp";
                                requestString = "frameId="+jQuery(ref).parent().attr("id")+"&frameState="+jQuery(ref).attr("state")+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid);
                                if( jQuery(ref).attr("painted")== undefined)
                                    new net.ContentLoader(url, frameResponseHandler, frameErrorHandler, "POST", requestString, true);

                                return;
                            }
                            //toggleSection(secondSection.firstChild);
                        }
                        if(isTab==true){
                            var tabli = control;
                            while(!jQuery(tabli).hasClass("tab-pane") ){
                                tabli = tabli.parentNode;
                            }
							var tabsheetid="#"+tabli.id;
                            var children = tab.getElementsByTagName("a");
                            var tabsheetid="#"+tabli.id;
                            for(var i=0;i<children.length;i++){
                                if(children[i].hash==tabsheetid && children[i].parentElement.style.display!="none"){
                                break;
				}
                            }
                            $("[href='#"+tabli.id+"']").on('shown.bs.tab', function (e) {
                                if(control!=null && control.tagName == "BUTTON"){
                                        control.setAttribute("autofocus","");
                                        control.focus();
                                    }
                                    else if(control.classList.contains("iform-radio")){
                                        checkedRadio =  getCheckedRadioRef(control);
                                        checkedRadio.focus();
                                    }
                                    else{
                                        if(control.getAttribute("combotype")=="listbox"){
                                                    jQuery(control).parent().find("button").get(0).focus();
                                                }
                                                else
                                                {
                                                    control.focus();
                                                }
                                    }
                                if(control.getAttribute("combotype")=="listbox"){
                                    jQuery(control).parent().find("button").get(0).focus();
                                }
                                else
                                {
                                    control.focus();
                                }
                            });
                            $("[href='#"+tabli.id+"']").off('shown.bs.tab');
                            selectSheet(tab.id,i);
							$("[href='#"+$("#"+tab.id).find(".tab-pane")[i].getAttribute("id")+"']").on('shown.bs.tab', function (e) {
                                            validateMandatoryFields();
                                         });
                        }
                        if(control!=null && control.tagName == "BUTTON"){
                            control.setAttribute("autofocus","");
                            control.focus();
                        }
                        else if(control.classList.contains("iform-radio")){
                            checkedRadio =  getCheckedRadioRef(control);
                            checkedRadio.focus();
                        }
                        else{
                            if(control.getAttribute("combotype")=="listbox")
                                        {
                                            jQuery(control).parent().find("button").get(0).focus();
                                        }
                                        else
                                        {
                                            control.focus();
                                        }
                        }
                    }
                    );
                    var url = "action_API.jsp";
                    requestString = "frameId="+jQuery(ref).parent().attr("id")+"&frameState="+jQuery(ref).attr("state")+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid);
                    if( jQuery(ref).attr("painted")== undefined)
                        new net.ContentLoader(url, frameResponseHandler, frameErrorHandler, "POST", requestString, true);
                    return;
                }
                else if(section.firstChild!=null&&section.firstChild.getAttribute("state")!=null&&section.firstChild.getAttribute("state")=="expanded"){
                    var secondSection=section.parentNode;
                    var isSecondSection=false;
                    while(secondSection!=null&&secondSection.classList!=null&&!secondSection.classList.contains("FrameControl")){
                        secondSection=secondSection.parentNode;
                    }
                    if(secondSection!=null &&secondSection.classList!=null&& secondSection.classList.contains("FrameControl")){
                        isSecondSection=true;
                    }
                    if(isSecondSection){
                        if(secondSection.firstChild!=null&&secondSection.firstChild.getAttribute("state")!=null&&secondSection.firstChild.getAttribute("state")=="collapsed"){
                            ref=secondSection.firstChild;
                            jQuery(ref).siblings().toggle(450,function(){
                                jQuery(ref).find("img").attr("src","resources/images/Arrows-"+(jQuery(ref).siblings().is(":visible") ? "Up" : "Down")+"-4-icon.png");
                                if((jQuery(ref).siblings().is(":visible")))
                                {
                                    ref.parentNode.scrollIntoView(false);  
                                }
                                var sectionState;
                                if(jQuery(ref).attr("state") == "collapsed")
                                {
                                sectionState="expanded";
                                jQuery(ref).attr("state","expanded"); 
                                }
                                else
                                {
                                    sectionState="collapsed";
                                    jQuery(ref).attr("state","collapsed");           
                                } 
                                if(window.onChangeSectionState)
                                    window.onChangeSectionState(jQuery(ref).parent().attr("id"),sectionState);
                                if(isTab==true){
                                    var tabli = control;
                                    while(tabli.id.indexOf("sheet")==-1){
                                        tabli = tabli.parentNode;
                                    }
                                    var children = tab.getElementsByTagName("a");
                                    for(var i=0;i<children.length;i++){
                                        if(children[i].href.indexOf("#"+tabli.id)!=-1){
                                            break;
                                        }
                                    }
                                    $("[href='#"+tabli.id+"']").on('shown.bs.tab', function (e) {
                                        if(control!=null && control.tagName == "BUTTON"){
                                            control.setAttribute("autofocus","");
                                            control.focus();
                                        }
                                        else if(control.classList.contains("iform-radio")){
                                            checkedRadio =  getCheckedRadioRef(control);
                                            checkedRadio.focus();
                                        }
                                        else{
                                           if(control.getAttribute("combotype")=="listbox"){
                                                jQuery(control).parent().find("button").get(0).focus();
                                            }
                                            else
                                            {
                                                control.focus();
                                            }
                                        }
                                        //control.focus();
                                    });
                                    $("[href='#"+tabli.id+"']").off('shown.bs.tab');
                                    selectSheet(tab.id,i);
									$("[href='#"+$("#"+tab.id).find(".tab-pane")[i].getAttribute("id")+"']").on('shown.bs.tab', function (e) {
                                            validateMandatoryFields();
                                         });
                                }
                                if(control!=null && control.tagName == "BUTTON"){
                                    control.setAttribute("autofocus","");
                                    control.focus();
                                }
                                else if(control.classList.contains("iform-radio")){
                                   checkedRadio =  getCheckedRadioRef(control);
                                   checkedRadio.focus();
                                }
                                else{
                                    if(control.getAttribute("combotype")=="listbox"){
                                        jQuery(control).parent().find("button").get(0).focus();
                                    }
                                    else
                                    {
                                        control.focus();
                                    }
                                }
                            }
                            );
                            var url = "action_API.jsp";
                            requestString = "frameId="+jQuery(ref).parent().attr("id")+"&frameState="+jQuery(ref).attr("state")+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid);
                            if( jQuery(ref).attr("painted")== undefined)
                                new net.ContentLoader(url, frameResponseHandler, frameErrorHandler, "POST", requestString, true);

                            return;
                        }
                        //toggleSection(secondSection.firstChild);
                    }
                    if(isTab==true){
                        var tabli = control;
                        while(tabli.id.indexOf("sheet")==-1){
                            tabli = tabli.parentNode;
                        }
                        var children = tab.getElementsByTagName("a");
                        for(var i=0;i<children.length;i++){
                            if(children[i].href.indexOf("#"+tabli.id)!=-1){
                                break;
                            }
                        }
                        $("[href='#"+tabli.id+"']").on('shown.bs.tab', function (e) {
                            if(control!=null && control.tagName == "BUTTON"){
                                    control.setAttribute("autofocus","");
                                    control.focus();
                                }
                                else if(control.classList.contains("iform-radio")){
                                    checkedRadio =  getCheckedRadioRef(control);
                                    checkedRadio.focus();
                                }
                                else{
                                    if(control.getAttribute("combotype")=="listbox"){
                                        jQuery(control).parent().find("button").get(0).focus();
                                    }
                                    else
                                    {
                                        control.focus();
                                    }
                                }
                        });
                        $("[href='#"+tabli.id+"']").off('shown.bs.tab');
                        selectSheet(tab.id,i);
						$("[href='#"+$("#"+tab.id).find(".tab-pane")[i].getAttribute("id")+"']").on('shown.bs.tab', function (e) {
                                            validateMandatoryFields();
                                         });
                        $("[href='#"+tabli.id+"']").on('shown.bs.tab', function (e) {
                            if(Object.keys(ComponentValidatedMap).length>0){
                                document.getElementById(Object.keys(ComponentValidatedMap)[0]).focus();
                                return false;
                            }else{
                                control.focus();
                            }
                        });
                        
                    }
                    if(control!=null && control.tagName == "BUTTON"){
                        control.setAttribute("autofocus","");
                        control.focus();
                    }
                    else if(control.classList.contains("iform-radio")){
                        checkedRadio =  getCheckedRadioRef(control);
                        checkedRadio.focus();
                    }
                    else{
                        if(control.getAttribute("combotype")=="listbox"){
                            jQuery(control).parent().find("button").get(0).focus();
                        }
                        else
                        {
                            control.focus();
                        }
                    }
                    return;
                }
                //toggleSection(section.firstChild);
            }
            if(isTab==true){
                var tabli = control;
                while(tabli.id.indexOf("sheet")==-1){
                    tabli = tabli.parentNode;
                }
                try{
                var children = tab.getElementsByTagName("a");
                for(var i=0;i<children.length;i++){
                    if(children[i].href.indexOf("#"+tabli.id)!=-1){
                        break;
                    }
                }
                }
                catch(ex){}
                $("[href='#"+tabli.id+"']").on('shown.bs.tab', function (e) {
                    if(control!=null && control.tagName == "BUTTON"){
                        control.setAttribute("autofocus","");
                        control.focus();
                    }
                    else if(control.classList.contains("iform-radio")){
                        checkedRadio =  getCheckedRadioRef(control);
                        checkedRadio.focus();
                    }
                    else{
                        if(control.getAttribute("combotype")=="listbox"){
                            jQuery(control).parent().find("button").get(0).focus();
                        }
                        else
                        {
                            control.focus();
                        }
                    }
                });
                $("[href='#"+tabli.id+"']").off('shown.bs.tab');
                selectSheet(tab.id,i);
                $("[href='#"+$("#"+tab.id).find(".tab-pane")[i].getAttribute("id")+"']").on('shown.bs.tab', function (e) {
                    validateMandatoryFields();
                });
            }
        }



        if(control!=null && control.tagName == "BUTTON"){
            control.setAttribute("autofocus","");
            control.focus();
        }
        else if(control.classList.contains("iform-radio")){
            checkedRadio =  getCheckedRadioRef(control);
            checkedRadio.focus();
        }
        else{
            if(control.getAttribute("combotype")=="listbox"){
                jQuery(control).parent().find("button").get(0).focus();
            }
            else
            {
                control.focus();
            }
        }
        }
    }
    function getCheckedRadioRef(radioRef){
        var buttons = document.getElementsByName(radioRef.id);
        var returnRef=buttons[0];
        for(var i=0;i<buttons.length;i++){
            if(buttons[i].checked){
                returnRef = buttons[i];
                break;
            }
        }
        return returnRef;
    }

    function clearTable(controlId,sync)
    {
        var url = "action_API.jsp";
        var table = document.getElementById(controlId);
        var deleterow = "";
        //table.innerHTML = "";
        if (table != null && table != undefined)
        {
            var rows = table.tBodies[0].children;
            var l = rows.length;
            while (l--) {
                deleterow += "y,";
            }
            var requestString = "controlId=" + controlId + "&deleteTableFlag=yes&deleterow=" + deleterow;
            if (sync == false)
                new net.ContentLoader(url, clearTableResponseHandler, ajaxFormErrorHandler, "POST", requestString, true);
            else if (sync == true) {
                clearTableHelper(controlId);
                iforms.ajax.processRequest(requestString, url);
            }
        }
    }

    function clearTableResponseHandler(){
        var tableId = getQueryVariable(this.params, "controlId");
        clearTableHelper(tableId);
    }

    function clearTableHelper(tableId){
        var table = document.getElementById(tableId);
        //table.innerHTML = "";
        var rows = table.tBodies[0].children;
        var l = rows.length;
        while(l--){
            table.tBodies[0].deleteRow(l);
        }
        var totalValueElements=document.getElementById('totallabel_'+tableId).innerHTML.split(",!,");
            for(var i=0;i<totalValueElements.length;i++){
            if(totalValueElements[i]!=''){
             $(document.getElementsByClassName(totalValueElements[i].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
                var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="") || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                    maskfield(this,'label');
            }
            });
            }
                showTotal('',totalValueElements[i]);
            }
    }
    function setFrameScroll(){
        try{
            if(window.parent.document.getElementById("theme:scrollPosFrame")!==null && window.parent.document.getElementById("theme:scrollPosFrame")){
                var scrollTop=parseInt(window.parent.document.getElementById("theme:scrollPosFrame").value);
                if (window.parent.document.getElementById('previewThemeFrame')!=='undefined' && window.parent.document.getElementById('previewThemeFrame')!==null)
                    window.parent.document.getElementById('previewThemeFrame').contentWindow.scrollTo(0,scrollTop);
            }
        }
        catch(e){}
    }


    //Bug 75529 Start
    function executeCommand(event,obj,jsonData){
        try{
            var jsonArray=JSON.parse(decode_utf8(jsonData));
            for(var i=0;i<jsonArray.length;i++){
                var jsonObject=jsonArray[i];
               
                if(jsonObject.command=='WFSave'){
                     if(mobileMode=="ios"||mobileMode=="android"){
                        window.parent.postMessage('SF', "*");
                    }
                    else{
                        if(window.parent && window.parent.WFSave){
                            window.parent.WFSave()
                        }
                    }
                }
                else if(jsonObject.command=='WFDone'){
                     if(mobileMode=="ios"||mobileMode=="android"){
                        window.parent.postMessage('DF', "*");
                    }
                    else{
                        if(window.parent && window.parent.WFDone){
                            window.parent.WFDone()
                        }
                    }
                }
                else if(jsonObject.command=='WFClose'){
                    if(mobileMode=="ios"||mobileMode=="android"){
                        window.parent.postMessage('CF', "*");
                    }
                    else{
                        if( window.parent &&  window.parent.WFClose){
                            window.parent.WFClose();
                        }
                    }
                }
                else if(jsonObject.command=='NGExportToPDF'){
                    var formStyleObject = document.getElementById("oforms_iform").style;
                    var formMinHeight = formStyleObject.minHeight;
                    var formHeight = formStyleObject.height;
                    var formMargin = formStyleObject.margin;
                    var formOverflow = formStyleObject.overflow;
                    formStyleObject.minHeight="";
                    formStyleObject.height="";
                    formStyleObject.margin="";
                    formStyleObject.overflow="";
					$(".iform-table").not('.floatThead-table').each(function(){
                       $(this).floatThead('destroy'); 
                       $(this).parent().css("overflow","hidden");
                    });
                    html2canvas(parseInt(jQuery("#oforms_iform").height() ) > 0 ? jQuery("#oforms_iform").get(0) : document.body, 
                                {
                                    onrendered: function(canvas) 
                                    {
                                        var ngForm=jQuery("#oforms_iform");
                                        //Bug 100264                               
                                       // var objForm=document.createElement("form");
                                       // objForm.style.display="none";
                                        //Bug 56066 - exportPDf should store the pdf at particular location in server 
                                        var location="";
                                        if( location == undefined || location == null || location == "undefined" )
                                            location = "";
                                        var currDateTime = getCurrentDateTime(true).replaceAll("/","_").replaceAll(":","_").replaceAll(" ","_"); 
                                        var pFileName = encode_utf8(pid+"_"+currDateTime);
                                        //Bug 100264
                                        var url="../../DownloadPDF?FormName="+pFileName+"&tiffFlag="+"false"+"&location="+encodeURIComponent(location)+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid);
                                        var popup;   
                                        var ifrm=document.getElementById("iFrameDownloadPDF");
                                        popup = (ifrm.contentWindow) ? ifrm.contentWindow : (ifrm.contentDocument.document) ? ifrm.contentDocument.document : ifrm.contentDocument;     
                                        popup.document.open();     
                                        popup.document.write("<HTML><HEAD><TITLE></TITLE></HEAD><BODY>");     
                                        popup.document.write("<form id='postSubmit' method='post' action='"+url+"' enctype='application/x-www-form-urlencoded'>");  
                                        popup.document.write("<input type='hidden' id='pdfCont' name='pdfCont'/>");             
                                        popup.document.getElementById('pdfCont').value=canvas.toDataURL();
                                        popup.document.write("<input type='hidden' id='formWidth' name='formWidth'/>");             
                                        popup.document.getElementById('formWidth').value=parseInt(jQuery("#oforms_iform").width())+"px";
                                        popup.document.write("<input type='hidden' id='formHeight' name='formHeight'/>");             
                                        popup.document.getElementById('formHeight').value= ( parseInt(jQuery("#oforms_iform").height() ) > 0 ? parseInt(jQuery("#oforms_iform").height()) : parseInt(document.body.clientHeight )) +"px";
                                        popup.document.write("</form></BODY></HTML>");      
                                        popup.document.close();      
                                        popup.document.forms[0].submit(); 
                                        //objForm.setAttribute("action","../../DownloadPDF?FormName="+pFileName+"&tiffFlag="+"false"+"&location="+encodeURIComponent(location)+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid));
                                        /*objForm.setAttribute("target","downloadPDF");
                                        objForm.method  = "post";
                                        objForm.name = "newForm";  

                                        var pdfCont=document.createElement("textarea");
                                        pdfCont.setAttribute("type","text");
                                        pdfCont.setAttribute("name","pdfCont")
                                        pdfCont.style.display="none";
                                        pdfCont.value=canvas.toDataURL();
                                        objForm.appendChild(pdfCont);

                                        var formWidth=document.createElement("input");
                                        formWidth.setAttribute("type","text");
                                        formWidth.setAttribute("name","formWidth")
                                        formWidth.style.display="none";

                                        //Bugzilla – Bug 47543                                
        //                                formWidth.value=parseInt(parseInt(ngForm.css("width"))+parseInt(jQuery(window).width())/10)+"px";
                                        formWidth.value=parseInt(jQuery("#oforms_iform").width())+"px";

                                        objForm.appendChild(formWidth);

                                        var formHeight=document.createElement("input");
                                        formHeight.setAttribute("type","text");
                                        formHeight.setAttribute("name","formHeight")
                                        formHeight.style.display="none";
                                        formHeight.value= ( parseInt(jQuery("#oforms_iform").height() ) > 0 ? parseInt(jQuery("#oforms_iform").height()) : parseInt(document.body.clientHeight )) +"px";
                                        objForm.appendChild(formHeight);

                                        document.body.appendChild(objForm);
                                        objForm.submit();*/
                                        
                                        $(".iform-table").not('.floatThead-table').each(function(){
                                            $(this).floatThead('reflow'); 
                                            $(this).parent().css("overflow","auto");
										});
					formStyleObject.minHeight=formMinHeight;
                                        formStyleObject.height=formHeight;
                                        formStyleObject.margin=formMargin;
                                        formStyleObject.overflow=formOverflow;

                                    }
                                });
                }
                else if(jsonObject.command=='NGLaunchURL'){
                    window.open(jsonObject.parameters.URL, jsonObject.parameters.windowName, jsonObject.parameters.windowDecoration);
                }
                else if(jsonObject.command=='GetPickList'&&event.keyCode==114){
                    event.keyCode=0;
                    if(event.stopPropagation)
                    {
                        event.stopPropagation();
                        event.preventDefault();
                    }
                    else
                    {
                        event = window.event;
                        event.cancelBubble = true;
                    }
                    if(document.getElementById(obj.id+"_pickListbtn")!=null){
                        document.getElementById(obj.id+"_pickListbtn").click();
                    }
                }
                else if(jsonObject.command=='AutoComplete'){
                    if(obj.getAttribute('autocompleteenable')==='false')
                        return;
                    var parameters=jsonObject.query;
                    getAutoCompleteData(parameters.query,parameters.cache,obj.id,parameters.likesearch,parameters.keycount);//Bug 76751
                }
                else if(jsonObject.command=='WFRefreshInterfaces'){
                    if(window.parent.ReloadInterfaces)
                        window.parent.ReloadInterfaces();
                }
                else if(jsonObject.command=='RouteCriteria'){
                    routeToNextForm(obj, jsonObject.parameters);
                }
                else if(jsonObject.command=='RouteJourney'){
                    if(window.routeJourneyHook)
                    {
                        if(routeJourneyHook(event.currentTarget.id))
                            routeToNavigation(obj, jsonObject.parameters);
                        else
                            return false;
                    }
                    else
                    {
                        routeToNavigation(obj, jsonObject.parameters);
                    }
                }
                else if(jsonObject.command=='ValidateOTP'){
                    validateOTP(obj, jsonObject.parameters);
                }
                else if(jsonObject.command=='GetOTP'){
                    routeToOTP(obj, jsonObject.parameters,"1");
                }
                else if(jsonObject.command=='OpenTransaction'){
                    openTransaction(obj, jsonObject.parameters);
                }
                else if(jsonObject.command=='CreateTransaction'){
                    createTransaction(obj, jsonObject.parameters);
                }
            }
        }
        catch(ex){}
    }
    //Bug 75529 End
    //Bug 76751 Start
	var validator = {};
    function getAutoCompleteData(query,cache,controlId,likesearch,keycount){
        if(isTextSelected(document.getElementById(controlId)) || validator[controlId] == document.getElementById(controlId).value){
            return;
        }
		likesearch=typeof likesearch=="undefined"?"true":likesearch;
        keycount=typeof keycount=="undefined"?"1":keycount;
        var url = "action_API.jsp";
        var inputValue=document.getElementById(controlId).value;
        var isCache=(cache=="true");
        if(isCache){
            query = "getcachedquery=&syncFlag="+"false";
        }
        else{
            query="&getquery=&syncFlag="+"false";
        }
        query=query+"&controlId="+controlId+"&likeSearch="+likesearch+"&keyCount="+keycount+"&inputValue="+inputValue;
        if(document.getElementById(controlId).value.length >= keycount){
            validator[controlId] = document.getElementById(controlId).value;
            if(window.includeControlInAutoComplete)
            {
                includeControlInAutoComplete(controlId);
            }
           //var jsonString = iforms.ajax.processRequest(query,url);
           //getAutoCompleteDataHelper(jsonString,controlId,likesearch,keycount);
           new net.ContentLoader(url, getAutoCompleteDataResponse, DBErrorHandler, "POST", query, true);
        }
    }
    function getCurrentDateTime(timeFlag){
        var currentMonth = new Date().getMonth()+1;
        if((currentMonth.toString()).length == 1)
            currentMonth = "0"+currentMonth;
        var currentDate=((new Date().getDate().toString().length==1)?"0"+new Date().getDate():new Date().getDate())+ "/" +currentMonth+ "/"+new Date().getFullYear();
        if(timeFlag){
            var currentTime=((new Date().getHours().toString().length==1)?"0"+new Date().getHours():new Date().getHours())+":"+((new Date().getMinutes().toString().length==1)?"0"+new Date().getMinutes():new Date().getMinutes())+":"+((new Date().getSeconds().toString().length==1)?"0"+new Date().getSeconds():new Date().getSeconds());
            currentDate = currentDate+" "+currentTime;
        }
        return currentDate;
               
}
   function getAutoCompleteDataHelper(jsonString,controlId,likeSearch,keyCount){
        try{
            var jsonObj = JSON.parse(jsonString);
            var dataArr=new Array();
            if(jsonObj[0]){
                for(var j=0;j<jsonObj[0].length;j++){
                    var textData = jsonObj[0][j];
                    for(var i=0;i<Object.keys(jsonObj).length-1;i++){
                        textData = textData+" | "+ jsonObj[i+1][j];
                    }
                    dataArr.push(textData);
                }
                if(likeSearch=="true"){
                    jQuery('#'+controlId ).autocomplete(
                    {
                        autoFocus : true ,
                        source:dataArr,
                        minLength:keyCount,
                        change: function( event, ui )
                        {
                            if(dataArr.indexOf($(this).val())==-1){
                                if(window.isAutoCompleteClearPreHook)
                                {
                                    if(isAutoCompleteClearPreHook(controlId))
                                    {
                                        $(this).val("");
                                    }
                                }
                                else
                                    $(this).val("");
                                isAutoCompleteSelected=true;
                                $(this).trigger("change");
                            }
                        },
                        close: function (event, ui)
                        {
                             $(this).val(event.target.value.split(" | ")[0]);
                        },
                        select: function(event, ui){
                            $(this).val(ui.item.value.split(" | ")[0]);
                            $(this).attr("AutoCompleteValue",ui.item.value);
                            isAutoCompleteSelected=true;
                            $(this).trigger("change");
                        },
                        open:function(event,ui){//Bug 82878 Start
                            $('.iform-table').not('.floatThead-table').each(function() {
                                $(this).floatThead('reflow');
                            });
                        }//Bug 82878 End
                    });
                }
                else{
                    jQuery('#'+controlId ).autocomplete(
                    {
                        autoFocus : true ,
                        source:function( request, response ) {
                            var matcher = new RegExp( "^" + jQuery.ui.autocomplete.escapeRegex( request.term ), "i" );
                            response( jQuery.grep( dataArr, function( item ){
                                return matcher.test( item );
                            }) );
                            dataArr=jQuery.grep( dataArr, function( item ){
                                return matcher.test( item );
                            });
                        },
                        minLength:keyCount,
                        change: function( event, ui )
                        {
                            if(dataArr.indexOf($(this).val())==-1){
                                if(window.isAutoCompleteClearPreHook)
                                {
                                    if(isAutoCompleteClearPreHook(controlId))
                                    {
                                        $(this).val("");
                                    }
                                }
                                else
                                    $(this).val("");
                                isAutoCompleteSelected=true;
                                $(this).trigger("change");
                            }
                        },
                        close: function (event, ui)
                        {
                             $(this).val(event.target.value.split(" | ")[0]);
                        },
                        select: function(event, ui){
                            $(this).val(ui.item.value.split(" | ")[0]);
                             $(this).attr("AutoCompleteValue",ui.item.value);
                            isAutoCompleteSelected=true;
                            $(this).trigger("change");
                        },
                        open:function(event,ui){//Bug 82878 Start
                            $('.iform-table').not('.floatThead-table').each(function() {
                                $(this).floatThead('reflow');
                            });
                        }//Bug 82878 End
                    });
                }
            }
        }
        catch(ex){}
    }
    
    function autoCompleteAPI(enable,controlId)
    {
        if(enable=='false')
        {
            try
            {
                $(document.getElementById(controlId)).autocomplete('destroy');
                document.getElementById(controlId).setAttribute("autocompleteenable","false");
            }
            catch (ex)
            {
                document.getElementById(controlId).setAttribute("autocompleteenable","false");
            }

        }  
        else
        {
            document.getElementById(controlId).removeAttribute("autocompleteenable");  
        }    
    }
    
    function getAutoCompleteDataResponse(){
        var jsonString = this.req.responseText;
        var controlId=getQueryVariable(this.params, "controlId");
        var likeSearch=getQueryVariable(this.params, "likeSearch");
        var keyCount=getQueryVariable(this.params, "keyCount");
        getAutoCompleteDataHelper(jsonString, controlId, likeSearch, keyCount);
    }
    function setTileDataFromDB(tileID , jsonObject){
        var value = "";
        if(tileDataModified == false){
            for(var i = 0 ; i < Object.keys(jsonObject).length ; i++){
               for(var j = 0 ; j < jsonObject[i].length ; j++){
                   value = jsonObject[i][j];
                   jQuery('#' + tileID).find(".tile-list").append('<li class="tile-list-item">' + value + '</li>');
               }
            }
        }
        tileDataModified = true;
    }
    
    //Bug 76751 End
    //Bug 75527 Start
    //Bug 76765
    function getDBLinkingEventData(event,controls,sync,index,controlId){
        var url = "action_API.jsp";
        var query="";
        /*if(isCaching){
            query = "getcachedquery="+cacheRequestString+"&syncFlag="+sync;
        }
        else{
            query="&getquery="+cacheRequestString+"&syncFlag="+sync;
        }
        */
        query="DBLinkingEvent="+event+"&DBLinkingControl="+encode_utf8(controlId)+"&Index="+index+"&syncFlag="+sync;
        //Bug 76765
        query=query+"&controls="+controls.toString();//+"&CS="+checksum;
        if(sync==false)
            var contentLoaderRef = new net.ContentLoader(url, setDataInControlsHandler, DBErrorHandler, "POST", query, true);
        else if(sync==true){
            try{
                var jsonObj = JSON.parse(iforms.ajax.processRequest(query,url));
                var json={};
                if(jsonObj[0]==null){
                    for(var i=0;i<controls.split(",").length;i++){
                        var objComp = document.getElementById(controls.split(",")[i]);
                        if(objComp==null)
                        {
                            objComp =  document.getElementsByName(controls.split(",")[i])[0];
                        }
                        if((objComp.type=='text' && objComp.classList.contains("editableCombo")) || objComp.type=='select-one' || objComp.type=='ComboBox' || objComp.type=='select-multiple'){
                            populateComboValuesfromString(controls.split(",")[i],{});
                        }
                        else{
                            json[controls.split(",")[i]] =  "";
                        }
                    }
                    setValues(json,true);                
                }
                if(controls.split(",").length==1 && Object.keys(jsonObj).length==2){
                        populateComboValuesfromString(controls.split(",")[0],jsonObj[0],jsonObj[1]);
                }
                else{
                    for(var i=0;i<controls.split(",").length;i++){
                        if(jsonObj[i]==null)
                            break;
                        var objComp = document.getElementById(controls.split(",")[i]);
                        if(objComp==null)
                        {
                            objComp =  document.getElementsByName(controls.split(",")[i])[0];
                        }
                        if((objComp.type=='text' && objComp.classList.contains("editableCombo")) || objComp.type=='select-one' || objComp.type=='ComboBox' || objComp.type=='select-multiple'){
                            populateComboValuesfromString(controls.split(",")[i],jsonObj[i]);
                        } else if ( jQuery('#'+controls.split(",")[i]).attr("type")=='ListView' || jQuery('#'+controls.split(",")[i]).attr("type")=='Table' ){
                            if(jQuery('#'+controls.split(",")[i]).find("tbody").children()!=undefined && jQuery('#'+controls.split(",")[i]).find("tbody").children().length==0)
                               renderExecuteServerEventAPIData(jsonObj);
                        } else if ( jQuery('#'+controls.split(",")[i]).attr("type")=='tile' ){
                            setTileDataFromDB(controls.split(",")[i],jsonObj);
                        } else {
                            json[controls.split(",")[i]] =  jsonObj[i][0];
                        }
                    }
                    setValues(json,true);
                }
            }
            catch(ex){}

            if( window.postHookDBLink ){ 
                        var control = document.getElementById(controlId);
                        if(useCustomIdAsControlName && (control==null || control==undefined)){
                            control = document.getElementsByName(controlId)[0];
                            if(control != null && control != undefined)
                              controlId = control.getAttribute("id");
                        }       
	                postHookDBLink(controlId , event , index ,controlId);
	            }
            }
        }

    function executeDBLinking(event,controlId,jsonData,isKeyDown){
        try{
            var jsonArray=JSON.parse(decode_utf8(jsonData));
            if(isKeyDown){
                if(event.keyCode==112){
                    for(var i=0;i<jsonArray.length;i++){
                        var jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF1"){
                            var tempJsonArray=jsonObj.DBLinkingArray;
                            for(var j=0;j<tempJsonArray.length;j++){
                                var jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else if(event.keyCode==113){
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF2"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else if(event.keyCode==114){
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF3"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else if(event.keyCode==115){
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF4"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else if(event.keyCode==116){
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF5"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else if(event.keyCode==117){
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF6"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else if(event.keyCode==118){
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF7"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else if(event.keyCode==119){
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF8"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else if(event.keyCode==120){
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF9"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else if(event.keyCode==121){
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF10"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else if(event.keyCode==122){
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF11"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else if(event.keyCode==123){
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyPressF12"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
                else{
                    for(i=0;i<jsonArray.length;i++){
                        jsonObj=jsonArray[i];
                        if(jsonObj.eventType=="KeyDown"){
                            tempJsonArray=jsonObj.DBLinkingArray;
                            for(j=0;j<tempJsonArray.length;j++){
                                jsonObjtemp=tempJsonArray[j];
                                //Bug 76765
                                getDBLinkingEventData(jsonObjtemp.event,jsonObjtemp.controls,true,jsonObjtemp.index,controlId);
                            }
                            break;
                        }
                    }
                }
            }
            else{
            for(i=0;i<jsonArray.length;i++){
                jsonObj=jsonArray[i];
                //Bug 76765
                if(jsonObj.event=="GotFocus"){
                    var control = document.getElementById(controlId);
                     if(useCustomIdAsControlName && (control==null || control==undefined)){
                         control = document.getElementsByName(controlName)[0];
                     }
                     if(control!=null)
                     control.value="";
                }
                getDBLinkingEventData(jsonObj.event,jsonObj.controls,true,jsonObj.index,controlId);
            }
            }
        }
        catch(ex){}
    }

     function populateComboValuesfromString(controlName,controlLabel,controlValue, isformLoad ){
         var originalValue = getValue(controlName);
        var control = document.getElementById(controlName);
        if(useCustomIdAsControlName && (control==null || control==undefined)){
            control = document.getElementsByName(controlName)[0];
            if(control != null && control != undefined)
               controlId = control.getAttribute("id");
        }
        var isClear = true;
        var selectedValue = jQuery('#'+controlName).val(); //Bug 88930
        if((jQuery('#'+controlName).is(':visible')) && selectedValue==undefined){
            selectedValue = "";                            //Bug 88930
        }
        if(window.clearComboItems && !window.clearComboItems(controlName)){
            isClear=false;
        }
        var clearComboOnLoad = false;
        if( window.clearComboOnLoad && !window.clearComboOnLoad(controlName) ){
            clearComboOnLoad = true;
        }
        
        if( (typeof isformLoad=='undefined') || clearComboOnLoad ){
        if(isClear)
            clearValue(controlName, true);
        if(isClear){
        if(document.getElementById(controlName).type== "text"){
            var ul = document.getElementById(controlName).parentNode.childNodes[2];
            ul.innerHTML='';
        }
        else
            document.getElementById(controlName).options.length = 0;
        }
    }
        var i;
        if(isClear && !(document.getElementById(controlName).type=='select-multiple')){
            addItemInCombo(controlName,SELECT,"");
        }
        if(typeof controlValue=='undefined'){
            for(i=0;i<controlLabel.length;i++){
                addItemInCombo(controlName, controlLabel[i],controlLabel[i],controlLabel[i]);
            }
        }
        else{
            for(i=0;i<controlLabel.length;i++){
                addItemInCombo(controlName, controlLabel[i],controlValue[i],controlLabel[i]);
            }
        }
        if(isClear && document.getElementById(controlName).type!="select-multiple" && !isformLoad && selectedValue=="")
            document.getElementById(controlName).value= document.getElementById(controlName).options[0].value;
        else{
            var exists = false;
            if(control !=null && control !=undefined){
                for(var i = 0, opts = control.options; i < opts.length; ++i){
                if( opts[i].value === selectedValue )
                {
                   exists = true; 
                   break;
                }
            }}
            if(exists)
                jQuery('#'+controlName).val(selectedValue);      //Bug 88930
            else{
                if(document.getElementById(controlName).type!="select-multiple"){
                    document.getElementById(controlName).value= document.getElementById(controlName).options[0].value; 
                }
            }
        }
        if(isformLoad){
            if(originalValue!=''){
                setValue(controlName,originalValue);
            }
        } 
    }
    //Bug 75527 End
    function showTotal(ref,elementClass){
        //jQuery('.'+elementClass+'_total').autoNumeric('destroy');
        var total=0;
        var isDGroup=false;
        var digitGroup="";
        var maskingPattern="";
        elementClass=elementClass.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&');
        var precision;
        if(elementClass!=""){
            $(document.getElementsByClassName(elementClass)).each(function() {
                var val = this.value;
                if(val==undefined)
                    val = this.innerHTML;
                digitGroup = parseInt(this.className.substring(this.className.indexOf("dgroup_")).split("_")[3]);
                if(this.className.indexOf('dgroup')!=-1){
                    isDGroup=true;
                    val = val.replace (/,/g, "");
                }
                if(this.getAttribute("maskingpattern")!=undefined && this.getAttribute("maskingpattern")!='' && this.getAttribute("maskingpattern")!='nomasking' ){
                    val = jQuery(this).autoNumeric('get');
                }
                var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
                || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
                {
                val = jQuery(this).autoNumeric('get');
            }
                 if( this.parentNode.parentNode.style.display!='none'){
                    if(this.innerHTML){
                        if(parseFloat(val))
                            total=total+parseFloat(val);
                    }
                    else{
                        if(parseFloat(val))
                            total=total+parseFloat(val);
                    }
                }
                if(this.getAttribute("precision")!=null && this.getAttribute("precision")!=undefined && this.getAttribute("precision")!='')
                    precision = this.getAttribute("precision");
                maskingPattern = this.getAttribute("maskingpattern");
            });
            var totalElement = document.getElementsByClassName(elementClass+'_total')[0];
            if(precision!=undefined && (parseInt(precision) > 0) && (maskingPattern!='NZP' || isFloat(total)))
                total = total.toFixed(parseInt(precision));
            totalElement.innerHTML = total;


            if(isDGroup){
                jQuery('.'+elementClass+'_total').autoNumeric('init',{
                    dGroup: digitGroup,
                    mDec: '2'
                }); 
               jQuery('.'+elementClass+'_total').autoNumeric('destroy');

            }
            
            var typeofvalue=typeof totalElement.getAttribute("typeofvalue")=='undefined'?'':totalElement.getAttribute("typeofvalue");
            if((totalElement.getAttribute("maskingpattern")!="nomasking" && totalElement.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && totalElement.getAttribute("maskingpattern")=="nomasking"))
            {
                if(!totalElement.getAttribute("maskingpattern")!="") 
                    totalElement.setAttribute("maskingpattern",maskingPattern);
                maskfield(totalElement,'label');
            }


        //var num = jQuery(total).replace(/,/gi, "");
        //        if(isDGroup){
        //            var groupedVal="";
        //           if(digitGroup==2){
        //                groupedVal = total.toString().split(/(?=(?:\d{2})+$)/).join(",");
        //           }
        //           else{
        //                 groupedVal = total.toString().split(/(?=(?:\d{3})+$)/).join(",");
        //           }
        //            
        //             document.getElementsByClassName(elementClass+'_total')[0].innerHTML=groupedVal;
        //        }
        //         else{
        //              document.getElementsByClassName(elementClass+'_total')[0].innerHTML=total
        //         }


    }
    }
    
function isFloat(n){
    return Number(n) === n && n % 1 !== 0;
}

function makeComboEditable(ref)
{
    //Bug 76778
    if(isEditableComboOnLoad=="true"){
        $(ref).editableSelect({
            filter: true
        });
    }
    else{
        var onchangeAttribute = ref.getAttribute("onchange");
        $(ref).editableSelect({
            filter: true
        }).on('select.editable-select', function (e) {
            if(e.target.tagName=="SELECT"){
               // eval(onchangeAttribute);
               window[onchangeAttribute]();
            }
        });
    }
}
 
    //Bug 76775 End

    function clearCombo(ref){
        if(ref.value==SELECT){
            $(ref).val("");
            $(ref).editableSelect('filter');
        }
    }

    function setDefaultComboValue(ref){
         if(ref.value==""){
            $(ref).val("Select");
            //$(ref).editableSelect('filter');
        }
    }

    function executeCustomEvent(event,obj,jsonData){
        try{
            var jsonArray=JSON.parse(decode_utf8(jsonData));
            if(event.keyCode==112){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF1"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
            else if(event.keyCode==113){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF2"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
            else if(event.keyCode==114){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF3"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                            event.preventDefault();
                        }
                        break;
                    }
                }
            }
            else if(event.keyCode==115){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF4"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
            else if(event.keyCode==116){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF5"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
            else if(event.keyCode==117){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF6"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
            else if(event.keyCode==118){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF7"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
            else if(event.keyCode==119){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF8"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
            else if(event.keyCode==120){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF9"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
            else if(event.keyCode==121){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF10"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
            else if(event.keyCode==122){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF11"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
            else if(event.keyCode==123){
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyPressF12"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
            else{
                for(var i=0;i<jsonArray.length;i++){
                    var jsonObj=jsonArray[i];
                    if(jsonObj.eventType=="KeyDown"){
                        if(typeof jsonObj.ServerEvent !="undefined"){
                            makeAjaxCall(obj.id,jsonObj.eventType);
                        }
                        if(typeof jsonObj.customString !="undefined"){
                            if(window[jsonObj.customString]){
                                window[jsonObj.customString](obj,event);
                            }
                        }
                        break;
                    }
                }
            }
        }
        catch(ex){}
    }
    //Bug 76775 End

   function onTabClick(sheetID,ref,tabId,sheetindex,eventCall)
    {
        updateSessionTimeout();
        if( eventCall === 1 && !ref.parentNode.classList.contains("active"))
            jQuery("#"+tabId).find(".tab-content").children().removeClass("active").removeClass("in");
	if(ref.getAttribute("collapsed")!=null){
            LoadTab(ref,tabId,sheetID,sheetindex,"N");
        }
        else{
        if(window.onTabClickEvent){
            window.onTabClickEvent(tabId,sheetindex,eventCall,'P');
        }
        if(window.onClickTab)
            window.onClickTab(tabId,sheetindex,eventCall);
        if(window.onClickTabByName){
            var tab = document.getElementById(tabId);
            var sheetList = tab.getElementsByTagName("li");
            window.onClickTabByName(tabId,sheetList[sheetindex].innerText,eventCall);
        }
    }
       setTimeout(setTileHeight,100);
    }
    
    function onClickTabByName(tabId,sheetName,eventCall){
        
    }

    function LoadTab(ref,tabID,sheetID,sheetIndex,reloadFlag){
        ref.removeAttribute("collapsed");
        var url = "action_API.jsp";
        var requestString = "tabID="+tabID+"&sheetID="+sheetID+"&sheetIndex="+sheetIndex+"&tabState=collapsed&reloadTab="+reloadFlag+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid);
        new net.ContentLoader(url, tabResponseHandler, frameErrorHandler, "POST", requestString, false);
    }
    function tabResponseHandler(){
        $(".iform-table").floatThead('reflow');
        var tabhtml = this.req.responseText.trim();
        var tabid = getQueryVariable(this.params, "tabID");
        var sheetId=getQueryVariable(this.params, "sheetID");
        var sheetIndex=getQueryVariable(this.params, "sheetIndex");
        //   HandleProgressBar(data);
        var parentNode=document.getElementById(sheetId);
        parentNode.innerHTML="";
        // Sometimes WD_RID set in HTTP Response Header  is not being returned in client response
		/*var rid = this.req.getResponseHeader("WD_RID");
		if(rid == null){
			var templateS = "<if_rid style='display:none'>";
			var templateE = "</if_rid>";
			var indexFrom = tabhtml.indexOf(templateS);
			if(indexFrom >= 0){				
				rid = tabhtml.substring(indexFrom+templateS.length, tabhtml.indexOf(templateE));
				jQuery("#rid_ActionAPI").val(rid);
			}
		}*/
		// -----------------
        jQuery(parentNode).html(tabhtml);   
        // Sometimes WD_RID set in HTTP Response Header  is not being returned in client response
		/*var templateRef = document.getElementsByTagName("if_rid");
		if(templateRef && templateRef.length > 0){
			templateRef = templateRef[0];
			if(templateRef.parentNode){
				templateRef.parentNode.removeChild(templateRef);
			}
		}*/
		// -----------------
        doInit();
        if(window.onClickTab)
            window.onClickTab(tabid,sheetIndex);
        if(window.onTabClickEvent){
            window.onTabClickEvent(tabid,sheetIndex,event,'D');
        }
    }

    function saveSection(sectionId)
    {
        if (document.getElementById(sectionId) != null && document.getElementById(sectionId) != undefined)
        {
            var url = "action_API.jsp";
            var requestString = "frameId=" + sectionId + "&frameState=expanded&pid=" + encode_utf8(pid) + "&wid=" + encode_utf8(wid) + "&tid=" + encode_utf8(tid) + "&fid=" + encode_utf8(fid);
            saveRichTextEditorData();
            var responseData = iforms.ajax.processRequest(requestString, url).trim();
            if(responseData!="0" && responseData!="")
               showSplitMessage("", responseData, SAVE_TITLE,"error");
            return responseData;
        }
    }
       function floatLabel(ref){

           if($(ref).attr('datatype')=='date' ||$(ref).attr('datatype')=='Text'  ){

            if($(ref).val().length > 0) {
                $(ref).addClass('input-focus');
                $(ref).next('.form-label').addClass('input-focus-label');
            }
            else {
                $(ref).removeClass('input-focus');
                $(ref).next('.form-label').removeClass('input-focus-label');

            }
           }

           else if($(ref).attr('datatype')=='textarea' ){

            if($(ref).val().length > 0) {
                $(ref).addClass('textarea-focus');
                $(ref).next('.form-label').addClass('textarea-focus-label');
            }
            else {
                $(ref).removeClass('textarea-focus');
                $(ref).next('.form-label').removeClass('textarea-focus-label');

            }
           }
        }
        function formatJSONValue(value){
            try{
                return value.replace(/\\"/g,'\\"');
            }
            catch(ex){
                return value;
            }
        }


       function cancelBubbling(e) {
        if (!e) e = window.event;
        e.cancelBubble = true;
        if (e.stopPropagation) e.stopPropagation();
        }

    
	function setTableCellDataHelper(tableId,rowIndex,colIndex,cellData)
	{
                if(getValueFromTableCell(tableId, rowIndex, colIndex)!= cellData){
                     setTableModifiedFlag(tableId);
                }
		var table = document.getElementById(tableId);
		if(table==null)
		{
			table = document.getElementsByName(tableId)[0];
		}
		var row = table.tBodies[0].getElementsByTagName('tr')[rowIndex];
		
		
		var col;

		if(row.getElementsByTagName("td")[parseInt(colIndex)+1])
		{
			col = row.getElementsByTagName("td")[parseInt(colIndex)+1];
		}
		else
			return;
		
		var control = col.getElementsByClassName("control-class")[0];
                if( control == undefined ){
                    col.innerHTML = encode_ParamValue(cellData);
                }
	  try{	
            if(control.classList.contains('listviewlabel')){
                    if(control.getAttribute("maskingPattern")!=null && control.getAttribute("maskingPattern")!='undefined'
                                && control.getAttribute("maskingPattern").toString()!='nomasking'&&control.getAttribute("maskingPattern").toString()!=''){ //85746
                                applyMaskingValue(control,cellData);
                    }
                    else
                            control.innerHTML = encode_ParamValue(escapeStringForHTML(cellData));
		}
		else{
			if (jQuery(control).hasClass("radio-group") == true) {
                             var childElement = control.children;
                             for (var i = 0; i < childElement.length; i++) {
                                 if(jQuery(childElement[i]).hasClass('radioThree')){
                                    var c = childElement[i];
                                    if (c.children[0].value == cellData) {
                                        jQuery(c).addClass('active');
                                        $(c).attr("style", "color:#ffffff !important");
                                     } else {
                                        jQuery(c).removeClass('active');
                                        c.removeAttribute('style', 'color');
                                     }
                                 } else {
                                     var c = childElement[i].children[0];
                                     if (c.value == cellData) {
                                       c.setAttribute("checked", true);
                                     } else {
                                        c.removeAttribute("checked");
                                     }
                                 }                                
                             }
                        }           
			if(control.getAttribute("type")!=null && control.getAttribute("type")=="checkbox" || control.getAttribute("type")=="radio"){
				control.checked = cellData;
			}
			else if(control.tagName=="LABEL" || control.tagName=="A" || control.tagName=="TEXTAREA"){//Bug 83906 
				control.innerHTML = encode_ParamValue(escapeStringForHTML(cellData));
				if(control.tagName=="TEXTAREA")
                control.value = encode_ParamValue(escapeStringForHTML(cellData));
				control.title = encode_ParamValue(escapeStringForHTML(cellData));
			}
                        else if(control.tagName=="IMG"){
                           var requestString = "pid=" + encode_utf8(pid) + "&wid=" + encode_utf8(wid) + "&tid=" + encode_utf8(tid) + "&fid=" + encode_utf8(fid) 
                                        + "&imageName=" + encode_utf8(cellData)+ "&getImagePath=Y";
                                var url = "action.jsp";
                                var responseText = iforms.ajax.processRequest(requestString, url);
                                control.src = responseText;
                                //control.src= "/iforms/GetImage?imagePath="+encode_utf8("IFormDirectory/Images/"+cellData);
                        }
                        else if(control.getAttribute("maskingPattern")!=null && control.getAttribute("maskingPattern")!='undefined'
                                && control.getAttribute("maskingPattern").toString()!='nomasking'&&control.getAttribute("maskingPattern").toString()!=''){ //85746
                                applyMaskingValue(control,cellData);
                        }
			else{
				control.value = cellData;
				control.title = cellData;
                        }
		}
            }catch(ex){}
		var totalValueElements = document.getElementById('totallabel_'+tableId).innerHTML.split(",!,");
		for(var i=0;i<totalValueElements.length;i++)
		{

			showTotal('',totalValueElements[i]);
		}

	}


    function setTableCellData(tableId,rowIndex,colIndex,cellData,sync)
    {
        // var table = document.getElementById(tableId);
        if (document.getElementById(tableId) != null && document.getElementById(tableId) != undefined)
        {
            if(colIndex >= 0 )
            { 
                var url = "action_API.jsp";
                var requestString = "setTableCellDataFlag=yes&tableId=" + tableId + "&rowIndex=" + rowIndex + "&colIndex=" + colIndex + "&cellData=" + encode_utf8(cellData)+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid);//Bug 84470

                if (sync == false) {
                    new net.ContentLoader(url, setTableCellDataHandler, setTableCellDataErroHandler, "POST", requestString, true);
                } else if (sync == true) {
                    iforms.ajax.processRequest(requestString, url);
                    setTableCellDataHelper(tableId, rowIndex, colIndex, cellData);
                }
            }
        }
    }

    function setTableCellDataHandler(){
         var rowIndex = getQueryVariable(this.params, "rowIndex");//Bug 81231
        var colIndex = getQueryVariable(this.params, "colIndex");//Bug 81231
        var cellData = getQueryVariable(this.params, "cellData");//Bug 81231
        var tableId = getQueryVariable(this.params, "tableId");
        setTableCellDataHelper(tableId,parseInt(rowIndex),parseInt(colIndex),cellData);

    }


    function setTableCellDataErroHandler(){

    }
    
    function setMultipleTableCellData(tableId,dataObj,sync)
    {
        // var table = document.getElementById(tableId);
        if (document.getElementById(tableId) != null && document.getElementById(tableId) != undefined)
        {
//            if(colIndex >= 0 )
//            { 
                var url = "action_API.jsp";
                var requestString = "setTableCellDataFlag=yes&tableId=" + tableId + "&multipleData="+encode_utf8(JSON.stringify(dataObj))+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&multi=Y";//Bug 84470

                if (sync == false) {
                    new net.ContentLoader(url, setMultipleTableCellDataHandler, setTableCellDataErroHandler, "POST", requestString, true);
                } else if (sync == true) {
                    iforms.ajax.processRequest(requestString, url);
                    for(var i=0;i<dataObj.length;i++){
                       var rowIndex = parseInt(dataObj[i].rowIndex);
                       var colIndex = parseInt(dataObj[i].colIndex);
                       var cellData = dataObj[i].cellData; 
                       setTableCellDataHelper(tableId, rowIndex, colIndex, cellData);
                    }
                }
//            }
        }
    }
    
    
    function setMultipleTableCellDataHandler(){
        var dataObj = JSON.parse(getQueryVariable(this.params, "multipleData"));//Bug 81231  
        var tableId = getQueryVariable(this.params, "tableId");
        for(var i=0;i<dataObj.length;i++){
            var rowIndex = parseInt(dataObj[i].rowIndex);
            var colIndex = parseInt(dataObj[i].colIndex);
            var cellData = dataObj[i].cellData;           
            setTableCellDataHelper(tableId,rowIndex,colIndex,cellData);
        }
        

    }
    
    function setCustomMandatoryMsg(controlId,customMsg){
        var controlMsgRef = document.getElementById(controlId+"_msg");
        if(controlMsgRef!=null && controlMsgRef!=undefined){
            controlMsgRef.innerHTML = customMsg;
        }
    }
    function clearComboOptions(controlName,isClear){
        var control=document.getElementById(controlName);
        if(useCustomIdAsControlName && (control==null || control==undefined )){
            control = document.getElementsByName(controlName)[0];
            if( control != null && control != undefined )
              controlName = control.getAttribute("id");
        }
        if(control!=null && control!=undefined && control.tagName!="SELECT"){
            var fieldElements = document.getElementsByName(controlName);
            for(var i=0;i<fieldElements.length;i++)
            {
                if(fieldElements[i].tagName == "SELECT")
                    control=document.getElementsByName(controlName)[i];
            }
        }  
        try{
        if(control!=null && control !=undefined)
        {
            if(control.type=='select-one' || control.type=='ComboBox' || control.type=='select-multiple' || control.getAttribute("datatype") == "combobox"){
                if(isClear==null || isClear==undefined || isClear==true)
                    clearValue(controlName, true);
              //  control.options.length = 0;
                if(control.type== "text"){
                     var ul = control.parentNode.childNodes[2];
                     ul.innerHTML='';
                }else
                     control.options.length = 0;
                if(control.type!="select-multiple")
                    addItemInCombo(controlName,"Select","");
                else{
                    reloadListBoxLayout(controlName);
                   
                }
                if(control.type!="select-multiple" && control.type!="text")
                    control.value= control.options[0].value;
                 if(control.type== "text"){
                     control.title=ul.childNodes[0].innerHTML;
                     control.value=ul.childNodes[0].innerHTML;
                 }            
            }
        }
        }
        catch(ex){}
    }

    function openSubForms(buttonId){
        if(window.subFormPreHook){
            if(!subFormPreHook(buttonId)){
                return;
            }
        }
        try{
        if(mobileMode=="ios"||mobileMode=="android"){
        var sid = jQuery("#sid").val();   //Bug 89303 - Clicking on button, Subform functionality is not working.
        var context = '/' + window.location.pathname.split("/")[1];
        var url = context + "/components/viewer/subFormViewer.jsp";
        var reqTok = iforms.ajax.processRequest("formuri="+encode_utf8(url), context+"/GetReqToken");
        var requestString = "buttonId="+encode_utf8(buttonId) +"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&WD_SID=" + sid + "&WD_RID="+reqTok;
        var contentLoaderRef = new net.ContentLoader(url, subformResponseHandler, subformErrorHandler, "POST", requestString, false);
        var subformModalDiv =document.getElementById("iFrameSubFormModal");
        jQuery(subformModalDiv).html(contentLoaderRef.req.responseText);
		doInit('subForm',buttonId);
        }
        else{
        var ScreenHeight=screen.height;
        var ScreenWidth=screen.width;
        var windowH=450;
        var windowW=950;
        if(window.resizeSubForm){
            var dimension = resizeSubForm(buttonId);
            windowH = dimension["Height"];
            windowW = dimension["Width"];
        }
        var WindowHeight=windowH-100;
        var WindowWidth=windowW;
        var WindowLeft=parseInt(ScreenWidth/2)-parseInt(WindowWidth/2);
        var WindowTop=parseInt(ScreenHeight/2)-parseInt(WindowHeight/2)-50;
        var sid = jQuery("#sid").val();   //Bug 89303 - Clicking on button, Subform functionality is not working.
        var context = '/' + window.location.pathname.split("/")[1];
        var url = context + "/components/viewer/subFormViewer.jsp";
        var reqTok = iforms.ajax.processRequest("formuri="+encode_utf8(url), context+"/GetReqToken");
        var wiWindowRef;
		var subFormWinName = 'SubFormPreview'; 
		if(window.openMultipleSubFormWindow){
			if(window.openMultipleSubFormWindow()){
				subFormWinName = subFormWinName+encode_utf8(buttonId);
			}
                }
        if(window.openWindowInFull){
            if(openWindowInFull(buttonId)){
                wiWindowRef = window.open("../viewer/subFormViewer.jsp?buttonId="+encode_utf8(buttonId)+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&WD_SID=" + sid + "&WD_RID="+reqTok, subFormWinName, 'scrollbars=yes','fullscreen=yes');
            }      
        }
        else
            wiWindowRef = window.open("../viewer/subFormViewer.jsp?buttonId="+encode_utf8(buttonId)+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&WD_SID=" + sid + "&WD_RID="+reqTok, subFormWinName, 'scrollbars=yes,left='+WindowLeft+',top='+WindowTop+',height='+windowH+',width='+windowW+',resizable=yes')
    //    wiWindowRef.document.write('<title>'+buttonId+'</title>');
        wiWindowRef.document.title = buttonId
        if(wiWindowRef!=null){
             addWindowObject(wiWindowRef);             
             wiWindowRef.focus();
         }
        }
        }
        catch(ex){}
    }
	
//Bug 82663 starts	
function addWindowObject(win){
    allWindows[totalSubWindows] = win;
    totalSubWindows += 1; 
}
//Bug 82663 edns


function closeSubForm(){
    while(totalSubWindows>0){
        allWindows[--totalSubWindows].close();
    }
}

    function subformResponseHandler(){}
    function subformErrorHandler(){}

    function checkTableHeight(controlId){
        if(document.getElementById(controlId+"div_pad")!=null){
            var tableControl=document.getElementById(controlId);
            var tablePadDiv=document.getElementById(controlId+"div_pad");
    //        if(tableControl.getElementsByTagName("tbody")[0].getElementsByTagName("tr").length>2){
    //            tablePadDiv.style.display="none";
    //        }
    //        else if(tableControl.getElementsByTagName("tbody")[0].getElementsByTagName("tr").length>1){
    //            tablePadDiv.style.display="";
    //            if(tablePadDiv.getAttribute("type")=="ListView")
    //                tablePadDiv.style.height="30px";
    //            else
    //                tablePadDiv.style.height="35px";
    //            tablePadDiv.firstChild.style.display="none";
    //        }
            if(tableControl.getElementsByTagName("tbody")[0].getElementsByTagName("tr").length>0){
                tablePadDiv.style.display="none";
    //            if(tablePadDiv.getAttribute("type")=="ListView")
    //                tablePadDiv.style.height="60px";
    //            else
    //                tablePadDiv.style.height="70px";
    //            tablePadDiv.firstChild.style.display="none";
            }    
            else{
                tablePadDiv.style.display="";
    //            if(tablePadDiv.getAttribute("type")=="ListView")
    //                tablePadDiv.style.height="90px";
    //            else
    //                tablePadDiv.style.height="105px";
    //            tablePadDiv.firstChild.style.display="";
            }
            checkPadDivWidth(controlId);
        }
    }
    function checkPadDivWidth(controlId){
        if(document.getElementById(controlId+"div_pad")!=null){
            var tablePadDiv=document.getElementById(controlId+"div_pad");
            tablePadDiv.style.minWidth=tablePadDiv.previousSibling.previousSibling.style.minWidth;
        }
    }

    function addBlankRowToTable(tableId){
        if(document.getElementById(tableId)!=null){
            if(document.getElementById(tableId).getAttribute("type")==="Table"){
                executeListView(tableId,"click","");
            }
            else{
                var json={};
                executeListView(tableId,"click",JSON.stringify(json));
            }
        }
    }

    function getSelectedRowsIndexes(tableName){
        var rowIndices = new Array();
        var rowChecks = document.getElementById(tableName).getElementsByClassName("selectRow");
        for(var i=0;i<rowChecks.length;i++){
            if(rowChecks[i].checked){
                rowIndices.push(i);
            }
        }
        return rowIndices;
    }    

    function getSelectedRowsDataFromTable(tableId){
        var table = document.getElementById(tableId);
        var selectedRows = getSelectedRowsIndexes(tableId);
        var dataArray =new Array(selectedRows.length);
        if (table !== null && table !== undefined)
        {
            var tablerows = table.getElementsByTagName("tr");

            for (i = 0; i < selectedRows.length; i++) {            
                 var row = tablerows[parseInt(selectedRows[i])+1];
                var controls = row.getElementsByClassName("control-class");
                dataArray[i] = new Array(controls.length);
                for (var j = 1; j <= controls.length; j++) {
                    var control = controls[j - 1];
                    var value = "";
                    if (control.type == 'text' || control.type == 'select-one' || control.type == 'ComboBox' || control.type == 'textarea') {
                        value = control.value;

                    } else if (control.tagName == 'LABEL') {
                        value = control.innerText;
                    } else if (control.type == "radio" || control.id.indexOf('radio') != -1)
                    {
                        if (jQuery(control).hasClass("radio-group") == true) {
                        var childElement = control.children;
                         for (var k = 0; k < childElement.length; k++) {
                            if(jQuery(childElement).hasClass("radioThree")){
                                 var c = childElement[k];
                                 if(jQuery(c).hasClass("active")){
                                    value= c.children[0].value;
                                 }
                             } else {
                                 var c = childElement[k].children[0];
                                 if (c.checked) {
                                   value = c.value;
                                 }
                             }
                            
                         }
                       } else {
                          value = control.checked;
                       } 
                    } else if (control.type == 'checkbox')
                    {
                        value = control.checked;
                    }

                    dataArray[i][j-1]= value;
                }
            }
        }
        return dataArray;
    }

    function addDataToGrid(tableId,jsonData){
        var dateicons = document.getElementById(tableId).getElementsByClassName("glyphicon-calendar");
        if(Object.keys(jsonData).length > 0){
            var gridType;
            if(document.getElementById(tableId).getAttribute("type")==="Table")
                gridType = "table";
            else
                gridType = "listview";
            var isDisabled=document.getElementById(tableId).classList.contains("disabledTable");
            var url = "action_API.jsp";
            var requestString=  "tableId="+tableId +"&addgriddata=yes"+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&RowId="+rowId+"&jsonData="+encode_utf8(JSON.stringify(jsonData))+"&gridType="+gridType+"&isDisabled="+isDisabled;
            var contentLoaderRef = new net.ContentLoader(url, addGridDataResponseHandler, ajaxFormErrorHandler, "POST", requestString, false);
            if(isDatePicker=="Y")
            {
                for (var i = 0; i < dateicons.length; i++) {
                    dateicons[i].style.visibility = "hidden";
                }
            }
            setTableModifiedFlag(tableId);
            attachDatePicker();
        }
    }   

    function addGridDataResponseHandler(){
        var controlId = getQueryVariable(this.params, "tableId");
         $("#"+controlId+ " tbody").append(this.req.responseText);
        $("#"+controlId).floatThead('reflow');
        
        var dgroupColumns = this.req.getResponseHeader("dgroupColumns");
        var maskedLabels = this.req.getResponseHeader("maskedLabels");
        checkTableHeight(controlId);
//        for(var i=0;i<dgroupColumns.split(",").length;i++){
//            var className = "dgroup_"+controlId+"_"+dgroupColumns.split(",")[i];
//            //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);
//
//            $('.'+className).each(function() {
//                var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
//                var dec = '0';
//                if(jQuery(this).attr('typeofvalue')=='Float')
//                    dec = jQuery(this).attr('Precision');
//                jQuery(this).autoNumeric('init',{
//                    dGroup: digitGroup,
//                    mDec: dec
//                }); 
//            });
//        }
        $('.listviewlabel').each(function() {
            
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'savedlabel');
            }

        });
            //Bug 82476 Start
        $('.tabletextbox').each(function() {
            
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'input');
            }

        });
            //Bug 82476 End
         var totalValueElements=document.getElementById('totallabel_'+controlId).innerHTML.split(",!,");
            for(var i=0;i<totalValueElements.length;i++){
             //var controlRef = document.getElementById('label'+'_'+controlId+'_'+maskedLabels.split(",")[i]);
             if(totalValueElements[i]!=''){
             $(document.getElementsByClassName(totalValueElements[i].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
                var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                    maskfield(this,'label');
            }
         });
             }
                showTotal('',totalValueElements[i]);
            }
            initFloatingMessagesForTableCells();
        reshuffleIndices(controlId);
       if(window.addRowPostHook)
       {
                addRowPostHook(controlId);
       }
    }

    function deleteRowsFromGrid(tableId,rowIndices){
        deleteTableRows("", tableId, rowIndices);
    }

    function setSelectedRow()
    {
        var myTrArray =  getContentWindow('iFrameSearchModal').getElementsByClassName("info");
        var textBoxValue = "";
        if(typeof myTrArray[0] != "undefined" && typeof myTrArray[0] != null){
            if($(myTrArray[0]).find("td:first").get(0) != null || $(myTrArray[0]).find("td:first") != null)
                textBoxValue = $(myTrArray[0]).find("td:first").get(0).innerText.trim();
        }

       //Bug 80094 Start
       var ctrlId = encode_ParamValue(getContentWindow('iFrameSearchModal').getElementById("controlId").value);
       var rowId = getContentWindow('iFrameSearchModal').getElementById("rowId").value;
       var colId = getContentWindow('iFrameSearchModal').getElementById("colId").value;
        if( typeof myTrArray[0] != "undefined"&&!pickListOkClicked(myTrArray[0],ctrlId,rowId,colId)){
            var ref= getContentWindow('iFrameSearchModal').getElementById("controlId").value;
            if(document.getElementById(ctrlId).type=="text"){
                document.getElementById(ref).value=textBoxValue;
                jQuery(document.getElementById(ref)).trigger("change");
                if(textBoxValue!="")
                    document.getElementById(ref).focus();
            }
            else if(document.getElementById(ctrlId).getAttribute("type")=="Table"){
                setTableCellData(ctrlId, parseInt(rowId), parseInt(colId), textBoxValue, true);
            }
        }
        //Bug 80094 End
        document.getElementById("picklistNext").disabled= false;
        document.getElementById("picklistPrevious").disabled = true;
    }

    //Bug 80094 Start
     function pickListOkClicked(rowArray,controlId,rowId,colId){
        var cells = rowArray.cells;
        var i=0;
        var columns = [];
        for(i=0;i<cells.length;i++){
            columns[i] = cells[i].innerText;
        }
        if(window.postHookPickListOk ){
            var control = document.getElementById(controlId);
	        if(useCustomIdAsControlName && (control==null || control==undefined)){
	            control = document.getElementsByName(controlId)[0];
	            if( control != null && control != undefined )
	               controlId = control.getAttribute("id");
	        }
            return postHookPickListOk(columns,controlId,rowId,colId);
        }
        return false;
    }
    //Bug 80094 End


    function executeServerEvent(controlName, eventType , stringifyData , sync ){    
            updateSessionTimeout();
            var url = "action_API.jsp";
            var requestString = "pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&fromEvent=executeServerEvent&controlName="+encode_utf8(controlName)+"&eventType="+encode_utf8(eventType)+"&eventData=" + encode_utf8(stringifyData);

            if (!sync) {
                new net.ContentLoader(url, serverEventHandler, serverEventErrorHandler, "POST", requestString, true);
            } else{
                var responseData = iforms.ajax.processRequest(requestString, url);
            var serverEventResponseData="";
            try{
                var responseObject=JSON.parse(responseData);
                serverEventResponseData=responseObject.responseData.trim();
                if(responseObject.APIData!=null)//Bug 84292
                    renderExecuteServerEventAPIData(responseObject.APIData);//Bug 84292
            }
            catch(ex){}
                if( window.postServerEventHandler )
                postServerEventHandler(controlName, eventType , serverEventResponseData);
            if(responseObject!=undefined && responseObject.error!=null && responseObject.error=="true"){
                var msg = SERVER_ERROR;
                if(window.showCustomMessage){
                    msg = showCustomMessage();
                }
                showMessage("",msg,"error");
            }
            return serverEventResponseData;
            }
    }

function setControlsInControlsFromSetValue(dataArray){//Bug 84292
    var controlId=dataArray.id;
    var type=dataArray.type;

    if(type=="textarea"||type=="textbox"||type=="label"||type=="combo"||type=="checkbox"
        ||type=="radio"||type=="datepick"){
        var dataValue=dataArray.value;
        setValue(controlId, dataValue);
    }
    else if(type=="table"||type=="ListView"){
        if(dataArray.operation=="addDataToGrid"){
             //$("#"+controlId+ " tbody").append(dataArray.value);
			 var error = dataArray.Error;
		    	if(error != undefined && error == "Y") {
		    	showMessage(controlId,"Issue in adding data in " + controlId + " grid. Kindly save the Workitem and open again to process","error");
		    	ComponentValidatedMap[controlId] = false;
		    	return;
		    	} else {
		    	delete ComponentValidatedMap[controlId];
		    	}
            var tableRef = document.getElementById(controlId).tBodies[0];
            $(tableRef).append(dataArray.value);
            checkTableHeight(controlId);//Bug 89195 start
            attachDatePicker(); //Bug 89195 end
        }
        else if(dataArray.operation=="setTableCellValue"){
            setTableCellDataHelper(controlId,dataArray.rowIndex,dataArray.colIndex,dataArray.value);
        }
        else if(dataArray.operation=="clearTable"){
            $("#"+controlId+ " tbody").html("");
            checkTableHeight(controlId);
        }
        $("#"+controlId).floatThead('reflow');
        $('.listviewlabel').each(function() {
                var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
        if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
        || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
        {
                maskfield(this,'savedlabel');
            }
        });
        $('.tabletextbox').each(function() {
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
        if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
        || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
        {
                maskfield(this,'input');
            }
        });
        var totalValueElements=document.getElementById('totallabel_'+controlId).innerHTML.split(",!,");
        for(var j=0;j<totalValueElements.length;j++){
            if(totalValueElements[j]!=''){
                $(document.getElementsByClassName(totalValueElements[j].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
                    var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
                    if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
                        || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
                        {
                        maskfield(this,'label');
                    }
                });
            }
            showTotal('',totalValueElements[j]);
        }
        initFloatingMessagesForTableCells();
        reshuffleIndices(controlId);
        if(dataArray.operation=="addDataToGrid"){
            if(window.addRowPostHook)
            {
                addRowPostHook(controlId);
            }
        }
    }
}

function setStyleInControlsFromServer(dataArray){//Bug 84292
        var controlId = dataArray.controlid;
        var attributeName = dataArray.attributename;
        var attributeValue = dataArray.attributevalue;
        var showHideAddDelete = dataArray.ShowHideAddDelete;
        var showCheckBoxColumn = dataArray.ShowCheckBoxColumn ;
        try{
        setStyle(controlId,attributeName,attributeValue,showHideAddDelete, showCheckBoxColumn);
        }
        catch(ex){}
}
function setTabStyleInControlsFromServer(dataArray){//Bug 84292
        var tabId = dataArray.controlid;
        var sheetIndex=dataArray.sheetindex;
        var attributeName = dataArray.attributename;
        var attributeValue = dataArray.attributevalue;
        var showHideAddDelete = dataArray.ShowHideAddDelete; 
        try{
           setTabStyle(tabId,sheetIndex,attributeName,attributeValue,showHideAddDelete);
        }
        catch(ex){}
}

function setDoclistRow(apiObj){
    var doclistId = apiObj.id;
    var rowHtml = encode_ParamValue(apiObj.value);
    var doclistContainer = $('#'+doclistId).parents().eq('2');
    if(rowHtml.trim()!=""){
    $(doclistContainer).html('');
    $(doclistContainer).append(rowHtml);
//    $('#'+doclistId).append(rowHtml);
    setDocListClass();
    //setDocListCss();
    }
}

function setSectionThemeCss(apiObj){
    var css = encode_ParamValue(apiObj.value);
    if(css.trim()!=""){
        $('head').append(css);
    }
}

function setNavigationBarHtml(json){
    var html = encode_ParamValue(json.value);
    if(html.trim()!=""){
    CreateIndicator("Application");
    if(getDeviceType()){
        if(document.getElementById("menuContainer")!=null){
            if(document.getElementsByClassName("appendmenumodal")[0]!=null){
                if(json.mobNavStyle1 != null){
                    $(".appendmenumodal").html(json.mobNavStyle1);
                }
            }
            $("#menuContainer").remove();
            $("#oforms_iform").find("#menuModal").before(html);
        }
        else if(document.getElementById("submenu")!=null){
            if(document.getElementsByClassName("stepName")[0]!=null){
              $(".stepName").remove();   
            }
            $("#submenu").parent().remove();
            $("#oforms_iform").append(html);
        }
        
    } else {
        if(document.getElementsByClassName("stepNavigationContainerParent")[0]!=null){
            if(document.getElementById("verNavFragParent")!=null && document.getElementById("verNavFragParent")!=undefined){
                document.getElementById("verNavFragParent").remove();
            }
            document.getElementsByClassName("stepNavigationContainerParent")[0].remove();
            $("#oforms_iform").append(html);
        } else if(document.getElementsByClassName("sideMenuNavigationBarParent")[0]!=null){
            $($('.sideMenuNavigationBarParent')[0]).parent().remove();
            $("#oforms_iform").append(html);
        }
    }    
    doInit();
    RemoveIndicator("Application");
    }
}
//Bug 84292 Start
function renderExecuteServerEventAPIData(outputArray){
    for (var i = 0; i < outputArray.length; i++){
        try{
        var apiObj=outputArray[i];
        if(apiObj.API==="setData"){
            if(apiObj.id!=null && apiObj.id!=undefined){
                setControlsInControlsFromSetValue(apiObj);
            }
        }
        else if(apiObj.API==="populateTile"){
            setTile(apiObj);
        }
        else if(apiObj.API==="setStyleRadio"){
            setStyleRadioOption(apiObj);
        }
        else if(apiObj.API==="setNavigationBarData"){
            setNavigationBarHtml(apiObj);
        }
        else if(apiObj.API==="setDoclistData"){
            setDoclistRow(apiObj);
        }
        else if(apiObj.API==="appendSectionTheme"){
            setSectionThemeCss(apiObj);
        }
        else if(apiObj.API==="setStyle"){
            setStyleInControlsFromServer(apiObj);
        }
        else if(apiObj.API==="addItemInCombo"){
            addItemInCombo(apiObj.id, apiObj.label, apiObj.value, apiObj.tooltip, apiObj.optionId, apiObj.isReload);
        }
        else if(apiObj.API==="removeItemFromCombo"){
            removeItemFromCombo(apiObj.id, apiObj.index);
        }
        else if(apiObj.API==="clearCombo"){
            clearComboOptions(apiObj.id,false);
        }
        else if(apiObj.API==="addItemInTableCellCombo"){
            addItemInTableCellCombo(apiObj.id, apiObj.rowIndex, apiObj.colIndex, apiObj.label, apiObj.value, apiObj.tooltip, apiObj.optionId);
        }
        else if(apiObj.API==="removeItemFromTableCellCombo"){
            removeItemFromTableCellCombo(apiObj.id, apiObj.rowIndex, apiObj.colIndex, apiObj.index);
        }
        else if(apiObj.API==="clearTableCellCombo"){
            clearTableCellCombo(apiObj.id, apiObj.rowIndex, apiObj.colIndex);
        }
        else if(apiObj.API==="setTabStyle"){
           setTabStyleInControlsFromServer(apiObj);
        }
        else if(apiObj.API==="addZone"){//Bug 85226 Start
           addZone(apiObj.zoneName,apiObj.top,apiObj.left,apiObj.width,apiObj.height,apiObj.id);
        }//Bug 85226 End
        else if(apiObj.API==="deleteRowsFromGrid"){//Bug 85784 Start
            deleteRowsFromGridAction(apiObj.id,apiObj.rowIndices,apiObj.altrowcolor);
            calculateTotalForGrid(apiObj.id);
            setTableModifiedFlag(apiObj.id);
        }//Bug 85784 End
        else if(apiObj.API==="openPickList"){
            openModal(apiObj.controlId,apiObj.header,apiObj.batchSize,apiObj.isListViewModal,apiObj.rowId,apiObj.colId);
        } else if(apiObj.API==="showHideDocumentTypes") {
            showHideDocType(apiObj.DocControlId,apiObj.DocumentType,apiObj.ShowHideFlag);
        } else if(apiObj.API==="setStatusTag"){
            setStatusTag(apiObj.ControlId,apiObj.StatusMsg,apiObj.Status);
        }else if(apiObj.API==="setColumnVisible"){
            setColumnVisibleHelper(apiObj.tableId,apiObj.colIndex,apiObj.visibleFlag);
        }else if(apiObj.API==="setColumnDisable"){
            setColumnDisableHelper(apiObj.tableId,apiObj.colIndex,apiObj.disableFlag);
        }
    } 
    catch(ex){

    }
    }
}
//Bug 84292 End
    function serverEventHandler(){
            var controlName = getQueryVariable(this.params, "controlName");
            var eventType = getQueryVariable(this.params, "eventType");
        var responseData = this.req.responseText;    
        var serverEventResponseData="";
        try{
            var responseObject=JSON.parse(responseData);
            serverEventResponseData=responseObject.responseData;
            if(responseObject.APIData!=null)//Bug 84292
                renderExecuteServerEventAPIData(responseObject.APIData);//Bug 84292
        }
        catch(ex){}
        if( window.postServerEventHandler ){
            postServerEventHandler(controlName, eventType , serverEventResponseData );
        }
        if(responseObject!=undefined && responseObject.error!=null && responseObject.error=="true"){
        var msg = SERVER_ERROR;
        if(window.showCustomMessage){
            msg = showCustomMessage();
        }
        showMessage("",msg,"error");
    }
}


    function serverEventErrorHandler(){

    }
   function callCustomRowLinkMethod(ref,functionName,controlId){
        //84526 start
        if(ref.getAttribute('disabled')!=='true' && $(ref).attr("disabled")!="disabled"){
            var trs = $("#"+controlId).find("tbody>tr");
            var row=ref.parentNode.parentNode;
            var rowIndex=$(trs).index(row);
            //84526 end
            window[functionName](controlId,rowIndex);
        }
    }

    function searchPicklistData(){
        var contrlid,batchsize,isModal,searchString,columnName;
         document.getElementById("rid_Action").value = window.parent.document.getElementById("rid_Action").value;
        try
        {
            contrlid = document.getElementById("controlId").value;
            batchsize= document.getElementById("batchSize").value;
            isModal=document.getElementById("isModal").value;
            searchString=document.getElementById("searchBox").value;
            columnName=document.getElementById("selectedColumn").options[document.getElementById("selectedColumn").selectedIndex].value;
        }
        catch(ex){
            contrlid = window.frames["iFrameSearchModal"].document.getElementById("controlId").value;
            batchsize= window.frames["iFrameSearchModal"].document.getElementById("batchSize").value;
            isModal= window.frames["iFrameSearchModal"].document.getElementById("isModal").value;
            searchString=window.frames["iFrameSearchModal"].document.getElementById("searchBox").value;
            columnName=window.frames["iFrameSearchModal"].document.getElementById("selectedColumn").options[window.frames["iFrameSearchModal"].document.getElementById("selectedColumn").selectedIndex].value;
        }
        var url = "action.jsp";
        var enablelkstar = false;
        if(window.parent.enableLikeSearchWithAsterisk){
            enablelkstar = window.parent.enableLikeSearchWithAsterisk(contrlid);
        }
        requestString=  "controlId="+contrlid +"&lks="+(enablelkstar?"Y":"N")+"&from=search"+"&isListModal="+isModal+"&searchString="+encodeURIComponent(searchString)+"&columnName="+encodeURIComponent(columnName);               
        var contentLoaderRef = new net.ContentLoader(url, searchPicklistHandler, picklisterrorHandler, "POST", requestString, true);
    }

    function searchPicklistHandler(){
        try
        {
            if(this.req.getResponseHeader("Next")=="false"){
                window.parent.document.getElementById("picklistNext").disabled= true;
            }
            else if(this.req.getResponseHeader("Next")=="true"){
                window.parent.document.getElementById("picklistNext").disabled= false;
            }
            if(this.req.getResponseHeader("Previous")=="false"){
                window.parent.document.getElementById("picklistPrevious").disabled = true;
            }else if(this.req.getResponseHeader("Previous")=="true"){
                window.parent.document.getElementById("picklistPrevious").disabled= false;
            }
            //Bug 83107 Start
            if((this.req.responseText).trim()==""){
                $("#myTable tbody").html("<b>"+NO_DATA_FOUND+"</b>");
            }else{
                $("#myTable tbody").html(this.req.responseText);
            }
            $("#myTable").floatThead('reflow');
            window.parent.document.getElementById("rid_Action").value= document.getElementById("rid_Action").value;
            //Bug 83107 End
            //document.getElementById("fetchedData").innerHTML = this.req.responseText;
        }
        catch(ex){
            //document.getElementById("fetchedData").innerHTML = this.req.responseText;
            //Bug 83107 Start
            $("#myTable tbody").html(this.req.responseText);
            $("#myTable").floatThead('reflow');
            //Bug 83107 End
        }
        
        showSelectedRow();
    }

    function openAdvancedListViewModel(controlId,eventType,reqString){
        if(window.openOverLay)
        {   
            if( !window.openOverLay(controlId)){
                cancelBubble(); 
                return;
            }
        }

        document.getElementById('advancedListview_id').value=controlId;
        var url = "advancedListViewModal.jsp";
        var requestString = "&controlId="+encode_utf8(controlId) +"&EventType="+eventType+"&tabledata=yes&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&RowId="+rowId+"&Operation=add";
        if(reqString && reqString!=='' && reqString!=null)
            requestString=reqString;
        var contentLoaderRef = new net.ContentLoader(url, openAdvancedListviewhandler, ajaxFormErrorHandler, "POST", requestString, false);
        var tableModalDiv =document.getElementById("iFrameAdvancedListViewModal");
        var tableAddModify =document.getElementById("advancedListViewModal");
        var crossIcon=document.getElementById("closeButton");
        crossIcon.removeAttribute("state");
        try{
        jQuery(tableModalDiv).html(contentLoaderRef.req.responseText);
        } catch(e){
            
        }
        //tableModalDiv.innerHTML=contentLoaderRef.req.responseText;
        if(!reqString){
            document.getElementById("AdvancedListviewlistPrevious").disabled = true;
            document.getElementById("AdvancedListviewlistNext").disabled= true;
        }
        if(typeof reqString=="undefined"){
            tableAddModify.setAttribute("action","A");
            advancedListviewInit(controlId,'A');
        }    
        else{
            tableAddModify.setAttribute("action","M");
            advancedListviewInit(controlId,'M');
        }    

    }
    function clearAdvancedListviewMap(action,crossState){
        var url = "action.jsp";
        var requestString="&clearListviewMap=yes"+"&tableId="+document.getElementById("advancedListview_id").value;
        if(crossState=="close")
        {
            var contentLoaderRef = new net.ContentLoader(url,openAdvancedListviewhandler , ajaxFormErrorHandler, "POST", requestString, false);
        }
        else{
            if(action=="A" && CleanMapOnCloseModal !="Y"){        
                var contentLoaderRef = new net.ContentLoader(url,clearAdvancedListviewMaphandler , ajaxFormErrorHandler, "POST", requestString, false);           
            }
            else
                var contentLoaderRef = new net.ContentLoader(url,modifyAdvancedListviewhandler , ajaxFormErrorHandler, "POST", requestString, false);
        } 
        isAdvanceNext=false;
    }
    
    function clearAdvancedListviewMaphandler(){
        var controlId =getQueryVariable(this.params, "tableId");
        if(window.addRowPostHook)
        {
            addRowPostHook(controlId);
        }
    }
    function openAdvancedListviewhandler()
    {
        
    }
    function modifyAdvancedListviewhandler()
    {
        var controlId =getQueryVariable(this.params, "tableId");
        if(window.modifyRowPostHook)
        {
            modifyRowPostHook(controlId);
        }       
    }
    function advancedListviewResponseHandler(){
        $("#"+this.req.getResponseHeader("TableId")+ " tbody").append(this.req.responseText);
        $("#"+this.req.getResponseHeader("TableId")).floatThead('reflow');
    }

    function addRowToAdvancedListview(controlId,copyRowFlag,isNext){
        if(Object.keys(ComponentValidatedMap).length!=0){                
            if(document.getElementById("addAdvancedListviewrow_"+controlId)!=null)//Bug 84293
                document.getElementById("addAdvancedListviewrow_"+controlId).removeAttribute("data-dismiss");
            if(document.getElementById("addAdvancedListviewrowNext_"+controlId)!=null)
                document.getElementById("addAdvancedListviewrowNext_"+controlId).removeAttribute("data-dismiss");   
            return false;
        }
        isNext=typeof isNext =='undefined'?false:isNext;
        if(isNext==true)
            isAdvanceNext=true;
        else
            isAdvanceNext=false;
        copyRowFlag=typeof copyRowFlag =='undefined'?false:copyRowFlag;//issue with copy row in advanced listview
        var valid = validateMandatoryFields();
        var len = 0;
        for(var count in ComponentValidatedMap) 
            len++;
        if(valid)
            valid = fetchCollapsedFrameHTML(controlId);
        if(isServerValidation=="true" && valid)
            valid = validateServerListviewDataType();
        if(!valid||len>0){
            if(document.getElementById("duplicateAdvancedListviewchanges_"+controlId)!=null)
               document.getElementById("duplicateAdvancedListviewchanges_"+controlId).removeAttribute("data-dismiss");
            if(document.getElementById("addAdvancedListviewrow_"+controlId)!=null)//Bug 84293
            {
                document.getElementById("addAdvancedListviewrow_"+controlId).removeAttribute("data-dismiss");
                document.getElementById("addAdvancedListviewrowNext_"+controlId).removeAttribute("data-dismiss");
            }
            return false;
        }
        var customListViewValid ;
        if(window.customListViewValidation){
            customListViewValid = customListViewValidation(controlId,"A");
            if(!customListViewValid){
                if(document.getElementById("addAdvancedListviewrow_"+controlId)!=null)//Bug 84293
                {
                    document.getElementById("addAdvancedListviewrow_"+controlId).removeAttribute("data-dismiss");
                    document.getElementById("addAdvancedListviewrowNext_"+controlId).removeAttribute("data-dismiss");
                }                   
                return false;
            }
            else{
                if(document.getElementById("addAdvancedListviewrow_"+controlId)!=null)//Bug 84293
                {
                    document.getElementById("addAdvancedListviewrow_"+controlId).setAttribute("data-dismiss","modal");
                    document.getElementById("addAdvancedListviewrowNext_"+controlId).setAttribute("data-dismiss","modal");
                }
                    
            }
        }

        var dataValue={};
        var elementsArray=document.getElementsByClassName('advancedListviewControl');
        var invalidControls=[];
        var nullElements=[];
        $(elementsArray).each(function(i) {
            if(this.tagName=='TABLE')
                return true;
            if((this.className.indexOf("denyNull")!=-1)&&(this.value==""||this.value==null)){
                nullElements.push(this.className.split("_")[1]);
            }

            if(this.getAttribute("typeofvalue") && (this.getAttribute("typeofvalue")==='Boolean' || this.getAttribute("typeofvalue")==='Integer' || this.getAttribute("typeofvalue")==='Float' || this.getAttribute("typeofvalue")==='Long')){
                if(!validateTypeOfValue(this))
                {
                    invalidControls.push(this);
                }
            }
            else{
                var type=jQuery(this).attr("datatype");
                if(!validateValue(this,type))
                {
                    invalidControls.push(this);
                }
            }

    //        var value=this.value?this.value:this.innerHTML;
    //        if(this.getAttribute("maskingPattern") && (this.getAttribute("maskingPattern").toString()==='currency_rupees' || this.getAttribute("maskingPattern").toString()==='currency_dollar' || this.getAttribute("maskingPattern").toString()==='currency_yen' || this.getAttribute("maskingPattern").toString()==='currency_euro' || this.getAttribute("maskingPattern").toString()==='percentage'|| this.getAttribute("maskingPattern").toString()==='dgroup2'|| this.getAttribute("maskingPattern").toString()==='dgroup3'))
    //        {
    //            value =  getControlValue(this);
    //        }
    //        if(this.type==='select-one')
    //            value=this.value===''?'':this.value;
    //        if(this.type && (this.type==="checkbox" || this.type==="radio"))
    //            value=this.checked;
    //        dataValue[formatJSONValue(this.getAttribute("labelName"))]=formatJSONValue(value);

        });
        var invalidControl;
        for(var j=0;j<elementsArray.length;j++){
            if(elementsArray[j].tagName=="TABLE")
                continue;
            if(!validateColumnValue(elementsArray[j],controlId,false)){
                invalidControl=elementsArray[j];
                break;

            }
        }
        if(invalidControls.length>0){
            if(document.getElementById("duplicateAdvancedListviewchanges_"+controlId)!=null)
               document.getElementById("duplicateAdvancedListviewchanges_"+controlId).removeAttribute("data-dismiss");
             
             if(document.getElementById("addAdvancedListviewrow_"+controlId)!=null)//Bug 84293
                document.getElementById("addAdvancedListviewrow_"+controlId).removeAttribute("data-dismiss");
             return false;
        }
    //    if(nullElements.length>0){
    //        document.getElementById("addrow_"+controlId).removeAttribute("data-dismiss");
    //        showMessage("","Null values not allowed in "+nullElements,"error");
    //        return false;
    //    }

    //    if(invalidControl!=undefined || invalidControl!=null){
    //        document.getElementById("addrow_"+controlId).removeAttribute("data-dismiss");
    //        var validationmsg = document.getElementById(controlId+"_"+invalidControl.getAttribute("labelName")+"_msg").innerHTML;
    //        showMessage(invalidControl,validationmsg +":"+'<strong>'+invalidControl.getAttribute("labelName")+'</strong>',"error");
    //      
    //        return false;
    //    }
            if(document.getElementById("duplicateAdvancedListviewchanges_"+controlId)!=null)
               document.getElementById("duplicateAdvancedListviewchanges_"+controlId).setAttribute("data-dismiss","modal");
            
            if(document.getElementById("addAdvancedListviewrow_"+controlId)!=null)//Bug 84293
                document.getElementById("addAdvancedListviewrow_"+controlId).setAttribute("data-dismiss","modal");
  
            dataValue = saveRichTextEditorData('iFrameAdvancedListViewModal',dataValue);
            executeListView(document.getElementById('advancedListview_id').value,'click',JSON.stringify(dataValue),copyRowFlag);//issue with copy row in advanced listview

            var totalValueElements=document.getElementById('totallabel_'+controlId).innerHTML.split(",!,");
            for(var i=0;i<totalValueElements.length;i++){
             //var controlRef = document.getElementById('label'+'_'+controlId+'_'+maskedLabels.split(",")[i]);
             if(totalValueElements[i]!=''){
             $(document.getElementsByClassName(totalValueElements[i].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
                 var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                    maskfield(this,'label');
            }
         });
             }
                showTotal('',totalValueElements[i]);
            }        
            if(typeof isNext!="undefined"&&isNext){
                if(document.getElementById("addrowandnext_"+controlId)!=null)//Bug 84293
                    document.getElementById("addrowandnext_"+controlId).setAttribute("data-dismiss","modal");
                openAdvancedListViewModel(controlId,'click'); 
            }
            setTableModifiedFlag(controlId);
    }
    function setListBoxStyle()
    {
      
        $( "select[combotype='listbox']").each(function() {          
              $(this).siblings().find('.multiselect-container .checkbox').css("text-align",$(this).css("text-align"));
              $(this).siblings().find('.multiselect-container .checkbox').css("font-size",$(this).css("font-size"));
              $(this).siblings().find('.multiselect-container .checkbox').css("font-weight",$(this).css("font-weight"));
              $(this).siblings().find('.multiselect-container .checkbox').css("font-style",$(this).css("font-style"));
              $(this).siblings().find('.multiselect-container .checkbox').css("font-family",$(this).css("font-family"));
              $(this).siblings().find('.multiselect-container .checkbox').css("background-color",$(this).css("background-color"));
              $(this).siblings().find('.multiselect-container .checkbox').css("color",$(this).css("color"));   
              $(this).siblings().find('.dropdown-toggle').css("text-align",$(this).css("text-align"));
              $(this).siblings().find('.dropdown-toggle').css("font-size",$(this).css("font-size"));
              $(this).siblings().find('.dropdown-toggle').css("font-weight",$(this).css("font-weight"));
              $(this).siblings().find('.dropdown-toggle').css("font-style",$(this).css("font-style"));
              $(this).siblings().find('.dropdown-toggle').css("font-family",$(this).css("font-family"));
              $(this).siblings().find('.dropdown-toggle').css("background-color",$(this).css("background-color"));
              $(this).siblings().find('.dropdown-toggle').css("color",$(this).css("color"));    
              //Bug 82077 - CSS of Multiselect should be same as Combo Box in iForms
              if($(this).hasClass('Style2')){
                  $(this).siblings().find('.dropdown-toggle').css("border-left","0px");
                  $(this).siblings().find('.dropdown-toggle').css("border-right","0px");
                  $(this).siblings().find('.dropdown-toggle').css("border-top","0px");
                  $(this).siblings().find('.dropdown-toggle').css("border-radius","0px");
                  $(this).siblings().find('.dropdown-toggle').css("padding-left","0px");
                  $(this).siblings().find('.dropdown-toggle').css("padding-right","0px");
                  $(this).siblings().find('.dropdown-toggle').addClass('form-control');
                  $(this).siblings().find('.dropdown-toggle').css("overflow","hidden"); //Bug 84710 
                  $(this).siblings().find('.dropdown-toggle').css("white-space","nowrap");
                  $(this).siblings().find('.dropdown-toggle').css("text-overflow","ellipsis");
              }else if($(this).hasClass('Style3')){
                   $(this).siblings().find('.dropdown-toggle').addClass('form-control1');
                   $(this).siblings().find('.dropdown-toggle').css("height","30px");
              }else{
                  $(this).siblings().find('.dropdown-toggle').addClass('form-control1');
              }
            });
            $('.dropdown-toggle').removeClass('btn');
            $('.dropdown-toggle').removeClass('btn-default');
            $('.multiselect-container').css("border-color","#66afe9");        
            $('.multiselect-container .checkbox').addClass('inputStyle');
            $('.multiselect-container .checkbox').css("border","0px");
            $('.dropdown-toggle').addClass('inputStyle');     
            //Bug 82173
            $('.dropdown-toggle').addClass('control-class');  
            $('.dropdown-toggle').css('padding','2px 8px');       
            $('.dropdown-toggle .caret').addClass("pull-right"); 
            
    }
    function advancedListviewInit(controlId,action){

     $('.tabletextbox').each(function() {
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'input');
            }

        });	
        
        $('.openPickerClass').each(function()
        {
            if(this.getAttribute("maskingPattern")!=null && this.getAttribute("maskingPattern")!=undefined && this.getAttribute("maskingPattern")!="" )
            {
                maskfield(this,'input');
            }
        });

    $('.advancedListviewControl.textbox').each(function() {
            var max=this.getAttribute("rangemax");
            var min=this.getAttribute("rangemin");
            var controlId = this.getAttribute("id");
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            var precision=typeof this.getAttribute("Precision")=='undefined'?'2':this.getAttribute("Precision");
            var decimal='2';
            if(typeofvalue =="Float")
                decimal=(window.allowPrecisionInFloat && (allowPrecisionInFloat(controlId)<precision))?allowPrecisionInFloat(controlId):precision;
            if(typeofvalue =="Integer")
                decimal='0';
            if(typeofvalue =="Long")
                decimal='0';

            if(this.getAttribute("maskingPattern").toString()!='nomasking'){
            if(this.getAttribute("maskingPattern").toString()!='currency_rupees' && this.getAttribute("maskingPattern").toString()!=='currency_dollar' && this.getAttribute("maskingPattern").toString()!=='currency_naira' && this.getAttribute("maskingPattern").toString()!=='currency_yen' && this.getAttribute("maskingPattern").toString()!=='currency_euro' && this.getAttribute("maskingPattern").toString()!=='currency_french' && this.getAttribute("maskingPattern").toString()!=='currency_greek' && this.getAttribute("maskingPattern").toString()!=='currency_bahamas' &&  this.getAttribute("maskingPattern").toString()!=='' && this.getAttribute("maskingPattern").toString()!=='percentage'){
                    var placeholder;
                    if(this.getAttribute("maskingPattern").toString().charAt(this.getAttribute("maskingPattern").toString().length-1)!='$'){
                        if(this.getAttribute("maskingPattern").toString()=='dgroup3' || this.getAttribute("maskingPattern").toString()=='dgroup2'){
                            var digitGroup = parseInt(this.getAttribute("maskingPattern").charAt(this.getAttribute("maskingPattern").length-1));
                            jQuery(this).autoNumeric('init',{
                                dGroup: digitGroup,
                                mDec: decimal                                

                            });
                        }
                        else{
                        if(typeofvalue=='Float'&&this.getAttribute("maskingPattern").toString()=='NZP'){
                            jQuery(this).autoNumeric('init',{
                                aSep : '',  
                                aDec: '.', 
                                mDec: decimal,
                                aPad: false
                            });
                        }
                        else{
                            placeholder=this.getAttribute("maskingPattern").replace(/[A-Za-z0-9*#]/mg , "_");
                            jQuery(this).mask(this.getAttribute("maskingPattern"), {
                                placeholder: placeholder
                            }, {
                                clearIfNotMatch: true
                            });
                            return true;//Bug 79052
                        }
                    }
                    }
                }

                else{
                    var asign='';
                    var dgroup='';
                    var psign='p';
                var adec='.';
                var asep=',';
                    if(this.getAttribute("maskingPattern").toString()==='currency_rupees'){
                        asign='Rs ';
                        dgroup=2;
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_dollar'){
                        asign='$ ';
                        dgroup=3;
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_naira'){
                            asign='₦ ';
                            dgroup=3;
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_yen'){
                        asign='¥ ';
                        dgroup=3;
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_euro'){
                        asign='€ ';
                        dgroup=3;
                    }
                else if(this.getAttribute("maskingPattern").toString()==='currency_french'){
//                    asign=' CHF';
                    dgroup=3;
                    adec = ',';
                    asep = ' ';
                    psign= 's';
                }
                else if(this.getAttribute("maskingPattern").toString()==='currency_greek'){
                    dgroup=3;
                    adec = ',';
                    asep = '.';
                    psign= 's';
                }
                else if(this.getAttribute("maskingPattern").toString()==='currency_bahamas'){
                        asign='B$ ';
                        dgroup=3;
                }
                    if(this.getAttribute("maskingPattern").toString()!=='percentage' && this.getAttribute("maskingPattern").toString() !=='currency_yen' ){
                        if(max===null)
                            jQuery(this).autoNumeric('init',{
                                aSign: asign, 
                                dGroup: dgroup,
                                pSign:psign,
                                mDec: decimal,
                            aNeg:true,
                            aDec: adec,
                            aSep: asep
                            });
                        else{
                            jQuery(this).autoNumeric('init',{
                                aSign: asign, 
                                dGroup: dgroup,
                                pSign:psign, 
                            mDec: decimal,
                            aDec: adec,
                            aSep: asep
                            });

                        }
                    }
                    else if(this.getAttribute("maskingPattern").toString() =='currency_yen'){
                            if(max===null)
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign,
                                    mDec: "0",
                                aNeg:true,
                                aDec: adec,
                                aSep: asep
                                });
                            else{
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign, 
                                mDec: "0",
                                aDec: adec,
                                aSep: asep
                                });
                            }
                        }
                    else
                        jQuery(this).autoNumeric('init',{
                            aSign: " %", 
                            pSign:'s',
                            mDec: decimal
                        });
                }

            }
            if(typeofvalue=='Float' && this.getAttribute("maskingPattern") && this.getAttribute("maskingPattern").toString()=='nomasking'){
            jQuery(this).autoNumeric('init',{
                aSep : '',  
                aDec: '.', 
                mDec: decimal
            });
         
        }

            //if(this.value!=='')
              //  jQuery(this).autoNumeric('set', this.value)
        });
        $('.advancedListviewControl.maskedText').each(function(){
            var digitGroup  = parseInt(this.getAttribute("dgroup"));
            var dec = '0';
            if(jQuery(this).attr('typeofvalue')=='Float')
                dec = jQuery(this).attr('Precision');
            jQuery(this).autoNumeric('init',{
                dGroup: digitGroup,
                mDec: dec
            });
        });
         $('.listviewlabel').each(function() {
                
        var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
        if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
            maskfield(this,'label');
        }

        });
        $('.totalLabel').each(function() {
            
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
                maskfield(this,'label');
            }

        });
        initFloatingMessagesForPrimitiveFields('.errorMessageHoverDiv.advancedListviewControlDiv');
        initFloatingMessagesForTableCells();
        setWidthForTabStyle4();
        $('.listviewTab.tabtheme4.iformTabUL.scrollingTabCSS').each(function(){
            $(this).removeClass("scrollingTabCSS");
            $(this).scrollingTabs({
                disableScrollArrowsOnFullyScrolled :true,
                enableRtlSupport :true,
                enableSwiping:true
      
            });
        });
        
       
        doInit();
        attachDatePicker();
        executeLoadEvents('3',controlId);
        clearComponentMap("advancedlistview");
        clearServerComponentMap("advancedlistview");
        disableAdvancedListViewControls(controlId);
        listViewLoad(controlId,action);
        makeStickyTabs(true);
        setListBoxStyle();
    }

    function setButtonDropDown(tabId,jsonObj){
       // var isSaveOptionsEnabled=false;
        var tabControl = document.getElementById(tabId);
        var oldButton = tabControl.getElementsByTagName("ul")[0].getElementsByClassName("tabbuttonClass")[0];
        if(oldButton!=null && oldButton!=undefined)
            //oldButton.remove();
            tabControl.getElementsByTagName("ul")[0].removeChild(oldButton);
//        if(!$(tabControl.getElementsByTagName("ul")[0].parentNode).hasClass("iformTabControl")&&$(tabControl.getElementsByTagName("ul")[0]).hasClass("tabtheme4"))
//            isSaveOptionsEnabled=true;
        if($(tabControl.getElementsByTagName("ul")[0]).hasClass("tabtheme4") && jQuery(".tabButtonsDiv").length==0 ){
            var fixVar=jQuery("#"+tabId).find(".scrtabs-tabs-fixed-container");
            var st=fixVar.attr("style")+";overflow:visible;";
            fixVar.attr("style",st);
            
             var liElem =  document.createElement("li");
        liElem.classList.add("tabbuttonClass");
            liElem.style.maxWidth = "100%";
            liElem.style.cssFloat = "right";
            liElem.style.zIndex = 201;

            var imgElem = document.createElement("img");
            imgElem.src = "./resources/images/hamburger.png";
            imgElem.style.height = "20px"
            imgElem.style.width = "30px"
            imgElem.style.marginTop="5px";

            var buttonElem = document.createElement("button");
            buttonElem.style.border="0px";
            buttonElem.style.background="inherit";
            buttonElem.id=tabId+"_tabButton";

            buttonElem.appendChild(imgElem);
            liElem.appendChild(buttonElem);

            tabControl.getElementsByTagName("ul")[0].appendChild(liElem);
        }
        else if( $(tabControl.getElementsByTagName("ul")[0]).hasClass("tabtheme4")){
            
            var fixVar=jQuery("#"+tabId).find(".tabButtonsDiv");
            var st=fixVar.attr("style")+";overflow:visible;"+";width:210px;";
            fixVar.attr("style",st);
            
  
            var imgElem = document.createElement("img");
            imgElem.src = "./resources/images/hamburger.png";
            imgElem.style.height = "20px"
            imgElem.style.width = "30px"
            imgElem.style.marginTop="5px";

            var buttonElem = document.createElement("button");
            buttonElem.style.border="0px";
            buttonElem.style.background="inherit";
            buttonElem.id=tabId+"_tabButton";
        
            buttonElem.appendChild(imgElem);
            fixVar.get(0).appendChild(buttonElem);
            setWidthForTabStyle4();
        }        
       else{
            var liElem =  document.createElement("li");
            liElem.classList.add("tabbuttonClass");
            liElem.style.maxWidth = "100%";
            liElem.style.cssFloat = "right";
            liElem.style.zIndex = 201;

            var imgElem = document.createElement("img");
            imgElem.src = "./resources/images/hamburger.png";
            imgElem.style.height = "20px"
            imgElem.style.width = "30px"
            imgElem.style.marginTop="5px";

            var buttonElem = document.createElement("button");
            buttonElem.style.border="0px";
            buttonElem.style.background="inherit";
            buttonElem.id=tabId+"_tabButton";
        
            buttonElem.appendChild(imgElem);
            liElem.appendChild(buttonElem);

            tabControl.getElementsByTagName("ul")[0].appendChild(liElem);
        }
        
        var dropDownDiv = document.createElement("div");
        dropDownDiv.classList.add("dropdown");
        dropDownDiv.classList.add("pull-right");

        var button = document.getElementById(tabId+"_tabButton");
//        if(!isSaveOptionsEnabled)
//            button.parentNode.appendChild(dropDownDiv);
//        else{
//            tabControl.getElementsByTagName("ul")[0].parentNode.nextSibling.appendChild(dropDownDiv);
//        }
        button.parentNode.appendChild(dropDownDiv);
        dropDownDiv.appendChild(button);

        var contentDiv = document.createElement("div");
        contentDiv.classList.add("dropdown-content");
        contentDiv.style.cssFloat = "right";
        contentDiv.style.right = 0;
        contentDiv.style.zIndex = 99999;
        for(key in jsonObj){
            var anchor = document.createElement('a');
            anchor.innerHTML = key;
            anchor.setAttribute("onclick",jsonObj[key]+"(this,"+"'"+key+"'"+")");
            contentDiv.appendChild(anchor);
        }
        button.parentNode.appendChild(contentDiv);
    }

    function onCustomLinkClick(ref,controlId){
        if(window.onClickCustomLink){
            var control = document.getElementById(controlId);
	        if(useCustomIdAsControlName && (control==null || control==undefined)){
	            control = document.getElementsByName(controlId)[0];
	            if( control != null && control != undefined )
	              controlId = control.getAttribute("id");
	        }
            onClickCustomLink(ref,controlId);
        }
    }

    function filterTableData(ref, filterType , colIndex, controlId , controlType ){

        //sortingHandler(colIndex,controlId,controlType );
        var searchString ='';
        if( document.getElementById(controlId+"_searchBox")!=null)
             searchString=document.getElementById(controlId+"_searchBox").value;
        if( filterType == 'Search'){
            colIndex = document.getElementById(controlId+"_selectedColumn").options[document.getElementById(controlId+"_selectedColumn").selectedIndex].value;

        }
        try{
            var thRef = $($('#'+controlId).get(0).parentNode.parentNode).find('th.tableStyle').get(parseInt(colIndex)+1);
            var isDisabled=document.getElementById(controlId).classList.contains("disabledTable");
            var sortOrder = thRef.getAttribute("SortOrder");  
            if( !sortOrder ){
                sortOrder = "A";
            }

            if(filterType == "Search"){
                sortOrder = "";
            }
        }
        catch(ex){

        }
        var url = "action.jsp";
        var requestString=  "controlId="+encode_utf8(controlId) + "&FilterType="+filterType+ "&SearchCriterion="+encode_utf8(searchString) + "&SortCriterion="+ sortOrder +"&ColumnIndex="+ colIndex+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&controlType="+controlType+"&isDisabled="+isDisabled;  
        if(isGridBatchingEnabled=="true" && (filterType=="Search" || filterType=="Sort")){
            var tableDataChangeFlag=isTableDataChanged(controlId);
            
            if(!tableDataChangeFlag)
            {
                new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false);  
                return;
            }

            var strMSg = BATCH_MSG;

            var buttons = {
                confirm: {
                    label: YES,
                    className: 'btn-success'

                },
                cancel: {
                    label: NO,
                    className: 'btn-danger'

                }
            }

            var callback = function(result){
                if(result == true){
                    if(applicationName!=null && applicationName==''){
                     requestString=requestString+"&SaveCurrentBatch=Y";
                     new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false); 
                    } else {
                       
                       if(window.opener !=null)
                       {
                          window.opener.saveWorkItem();
                       }
                       else
                       {
                           saveForm('SB'); 
                       }
                       new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false);  
                    }
                // jQuery(this).dialog("close");
                }
                else if(result == false){
                    new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false); 
                    // jQuery(this).dialog("close");
                }
            }
            showConfirmDialog(strMSg,buttons,callback);
        }
        else{
            if(isGridBatchingEnabled=="false" && colIndex==-1){
                if(isTableDataChanged(controlId)){
                var strMSg = BATCH_MSG;
                var buttons = {
                    confirm: {
                        label: YES,
                        className: 'btn-success'

                    },
                    cancel: {
                        label: NO,
                        className: 'btn-danger'

                    }
                }

               var callback = function(result) {
                    if (result == true) {
                        requestString = requestString + "&SaveCurrentBatch=Y";
                        var contentLoaderRef = new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false);
                    }
                    else {
                        var contentLoaderRef = new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false);
                    }
                }
                showConfirmDialog(strMSg, buttons, callback);
            } else {
                var contentLoaderRef = new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false);
            }
        }
        else{
           if(filterType=="Search" || filterType=="Sort"){
                
            if(isTableDataChanged(controlId)){
                var strMSg = BATCH_MSG;
                var buttons = {
                    confirm: {
                        label: YES,
                        className: 'btn-success'

                    },
                    cancel: {
                        label: NO,
                        className: 'btn-danger'

                    }
                }

                var callback = function(result){
                    if(result == true){
                        if(applicationName!=null && applicationName==''){
                         requestString=requestString+"&SaveCurrentBatch=Y";
                         var contentLoaderRef = new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false);
                        } else {
                            
                             if(window.opener !=null)
                       {
                          window.opener.saveWorkItem();
                       }
                       else
                       {
                           saveForm('SB');
                       }
                            var contentLoaderRef = new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false);
                        }
                    }
                    else{
                        var contentLoaderRef = new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false);
                    }
                }
                showConfirmDialog(strMSg,buttons,callback);
            }
            else{
                var contentLoaderRef = new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false);
            }
        }
        else{
            var contentLoaderRef = new net.ContentLoader(url, filterHandler, ajaxFormErrorHandler, "POST", requestString, false);
        }
      }   
    } 

 }

function isTableDataChanged(tableId){
        var tableDataChangeFlag=false;
        for(var i=0;i<tableDataChangeArray.length;i++){
            var jsonObject=tableDataChangeArray[i];
            if(jsonObject.controlId==tableId){
                tableDataChangeFlag=true;
                tableDataChangeArray.splice(i, 1);
                break;
            }
        }
        return tableDataChangeFlag;
    }
    
    function filterHandler(){
        if(this.req.responseText.trim()!="" && !c_isNaN(this.req.responseText.trim())){   ////Bug 90101
        var code = parseInt(this.req.responseText.trim());
            if( code !== 0){
                showSplitMessage("", "Error in Saving Data.",SAVE_TITLE,"error");
                return;
            }
        }
        var tableId=getQueryVariable(this.params, "controlId");
      $("#"+tableId+" tbody").empty();
      $("#"+tableId+ " tbody").append(this.req.responseText);
      $("#"+tableId).floatThead('reflow');
      var colIndex = getQueryVariable(this.params, "ColumnIndex");
      var sortOrder = this.req.getResponseHeader("SortOrder");  
      var batchCounter=this.req.getResponseHeader("batchCounter");  
      var thRef = null;

        try{ 
            $($('#'+tableId).get(0).parentNode.parentNode).find('th.tableStyle').removeAttr("SortOrder");    
            $($('#'+tableId).get(0).parentNode.parentNode).find('th.tableStyle').css({"background-repeat":"","background-position":"","background-image":""});
            thRef = $($('#'+tableId).get(0).parentNode.parentNode).find('th.tableStyle').get(parseInt(colIndex)+1);
            thRef.style.backgroundRepeat = "no-repeat";
            thRef.style.backgroundPosition = "center right";

              var imageName = "";
              if( sortOrder == "D"){
                  thRef.setAttribute("SortOrder" , "A");
                  imageName = "lvwUp.png";
              }
              else if( sortOrder == "A"){
                  thRef.setAttribute("SortOrder" , "D");
                  imageName = "lvwDown.png";
              }
              else if( sortOrder == "N"){
                  thRef.removeAttribute("SortOrder");      
              }
            thRef.style.backgroundImage = ( imageName != "") ? "url('resources/images/" + imageName + "')" : "";
            reshuffleIndices(tableId,sortOrder,batchCounter);
        }catch(ex){}


      checkTableHeight(tableId);
      initFloatingMessagesForTableCells();
      var preEnabled=this.req.getResponseHeader("preEnabled");
      var nextEnabled=this.req.getResponseHeader("nextEnabled");
      if(preEnabled=="true"){
        $("#pre_"+tableId).prop("disabled", false);
        document.getElementById("preimage_"+tableId).src = "./resources/images/PaginationLeftEnabled.png";
      } else {
        $("#pre_"+tableId).prop("disabled", true);
        document.getElementById("preimage_"+tableId).src = "./resources/images/PaginationLeftDisabled.png";
      }  
      if(nextEnabled=="true") {
        $("#next_"+tableId).prop("disabled", false);
         document.getElementById("nextimage_"+tableId).src = "./resources/images/PaginationRightEnabled.png";
      } else {
        $("#next_"+tableId).prop("disabled", true);
        document.getElementById("nextimage_"+tableId).src = "./resources/images/PaginationRightDisabled.png";
      }  
    $("#"+tableId+ " .listviewlabel").each(function() {
        var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
        if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
            maskfield(this,'savedlabel');
        }

    });
    $("#"+tableId+ " .tabletextbox").each(function() {
        var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
        if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
            maskfield(this,'input');
        }

    });
      var totalValueElements=document.getElementById('totallabel_'+tableId).innerHTML.split(",!,");
      for(var i=0;i<totalValueElements.length;i++){
        if(totalValueElements[i]!=''){
         $(document.getElementsByClassName(totalValueElements[i].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
        var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
            maskfield(this,'label');
            }
        });
        }
        showTotal('',totalValueElements[i]);
     }

     var dgroupColumns = this.req.getResponseHeader("dgroupColumns");
     if(dgroupColumns!=null && dgroupColumns!=undefined){
     for(var i=0;i<dgroupColumns.split(",").length;i++){
        var className = "dgroup_"+tableId+"_"+dgroupColumns.split(",")[i];
        //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);

        $('.'+className).each(function() {
            var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
            jQuery(this).autoNumeric('init',{
                dGroup: digitGroup,
                mDec: '0'
            }); 
        });
     }
    }
    if(window.searchGridPostHook)
    {
        searchGridPostHook(tableId);
    }
    }
    function getGridRowCount(tableId){
        var control = document.getElementById(tableId);
        var selectRowChecks = control.getElementsByClassName("selectRow");
        return selectRowChecks.length;
    }
    function isSectionCompleted(frameId){
        var frame=document.getElementById(frameId);
        if(frame!=null){
            if(frame.children[1]!=null&&frame.children[1].children[0]!=null){
                if(frame.children[1].children[0].innerHTML!=""){
                   return isSectionMandatoryLeft(frameId);
                }
                else
                    return false;
            }
            else if(frame.children[0]!="undefined"){
                return isSectionMandatoryLeft(frameId);
            }
        }
        return false;
    }
    function isSectionMandatoryLeft(frameId){
         var mandatoryFields=$("#"+frameId+" [required=''] ");
        for(var i=0;i<mandatoryFields.length;i++){
            var value;
            var blankField=false;
            var blankFieldControl;
            var control=jQuery(mandatoryFields[i]);
            var ctrlType=control.attr("type");
            var iscontrolvisible = isControlVisible(control.get(0),"",blankField,blankFieldControl);
            if(iscontrolvisible){
            if(!(document.getElementById(control.attr("id")).style.display==="none")){
                if(ctrlType=="text" || ctrlType=="textarea"|| typeof ctrlType=="undefined")
                {
                    value=getControlValue(document.getElementById(control.attr("id")));
                    if(typeof ctrlType=="undefined" && value=="")
                        value=jQuery(control).val();
                    if(value=="" || value==null)
                    {
                        return false;
                    }
                }
                else if(ctrlType=="radio")
                {
                    if(document.querySelector('input[name="'+control.prop("name")+'"]:checked') == null)
                    {
                        return false;
                    }
                }
                else if(ctrlType=="checkbox")
                {
                    value=control.prop("checked");
                    if(!value)
                    {
                        return false;
                    }
                }
            }
        }
        }
        return true;
    }
    //Bug 81232 Start
    function applyMaskingValue(ref,value){
        if(ref.getAttribute("maskingPattern").toString()!='nomasking'&&ref.getAttribute("maskingPattern").toString()!=''&&ref.getAttribute("maskingPattern").toString()!='email'){
        if(ref.getAttribute("maskingPattern").toString()!='currency_rupees' && ref.getAttribute("maskingPattern").toString()!=='currency_dollar' && ref.getAttribute("maskingPattern").toString()!=='currency_naira' && ref.getAttribute("maskingPattern").toString()!=='currency_yen' && ref.getAttribute("maskingPattern").toString()!=='currency_euro' && ref.getAttribute("maskingPattern").toString()!=='currency_french' && ref.getAttribute("maskingPattern").toString()!=='currency_greek' && ref.getAttribute("maskingPattern").toString()!=='currency_bahamas' && ref.getAttribute("maskingPattern").toString()!=='' && ref.getAttribute("maskingPattern").toString()!=='percentage'&& ref.getAttribute("maskingPattern").toString()!=='NZP'){
                var placeholder;
                if(ref.getAttribute("maskingPattern").toString().charAt(ref.getAttribute("maskingPattern").toString().length-1)!='$'){
                    if(ref.getAttribute("maskingPattern").toString()=='dgroup3' || ref.getAttribute("maskingPattern").toString()=='dgroup2'){
                        if(value!=='')
                            jQuery(ref).autoNumeric('set', value);
                        return jQuery(ref).autoNumeric('get');
                    }
                    else{  
                    jQuery(ref).val(value).trigger("keyup");
                    if(ref.getAttribute("datatype") == "date")
                    {
                        value = ref.value;
                    }
                    else
                        return jQuery(ref).cleanVal();//Bug 79052
                    }
                }
            }
            else{
                if(value!=='')
                    jQuery(ref).autoNumeric('set', value);
                return jQuery(ref).autoNumeric('get');
            }
        }
        return value;
    }
    //Bug 81232 End
    function executeLoadEvents(type,controlId){
        if($(".formEvent").length>0 || type==='3' || type==='4' || type==='2'){
        var url = "action_API.jsp";
        var requestString="executeFormLoadEvent=yes&type="+type;
        if(typeof controlId!="undefined"){    
            requestString += "&FormLoadEventControlId="+encode_utf8(controlId);
        }
        requestString+="&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid);
        new net.ContentLoader(url, executeLoadEventHandler, frameErrorHandler, "POST", requestString, true);
    }
    }
    
    function executeLoadEventHandler(){
        try{
            var jsonObj = JSON.parse(this.req.responseText);
            var customAction=jsonObj.customAction;
            var dbLinkingArray=jsonObj.dbLinking;
            if(dbLinkingArray!=null&&dbLinkingArray!=undefined&&dbLinkingArray[0]!=null){//Bug 84017
                for(var j=0;j<dbLinkingArray.length;j++){
                    var dbLinkingObject=dbLinkingArray[j];
                    var controls=dbLinkingObject.controls;
                    var data=dbLinkingObject.data;
                    if(data[0]==null){
                        for(var i=0;i<controls.split(",").length;i++){
                            var controlId=controls.split(",")[i].trim();
                            var objComp = document.getElementById(controlId);
                            if(objComp==null)
                            {
                                objComp =  document.getElementsByName(controlId)[0];
                            }
                            if(objComp!=null){
                            if((objComp.type=='text' && objComp.classList.contains("editableCombo")) || objComp.type=='select-one' || objComp.type=='ComboBox' || objComp.type=='select-multiple'){
                                populateComboValuesfromString(controls.split(",")[i],{}, {} , true);
                            }
                            else{
                                 setValue(controls.split(",")[i], "");
                                //json[controls.split(",")[i]] =  "";
                            }
                        }
                        }
                        //setValues(json,true);
                        continue;//Bug 84017
                    }
                    if(controls.split(",").length==1 && Object.keys(data).length==2){
                            populateComboValuesfromString(controls.split(",")[0],data[0],data[1] , true);
                    } 
                    else{
                        for(var i=0;i<controls.split(",").length;i++){
                            if(data[i]==null)
                                break;
                            var objComp = document.getElementById(controls.split(",")[i]);
                            if(objComp==null)
                            {
                                objComp =  document.getElementsByName(controls.split(",")[i])[0];
                            }
                            if(jQuery('#'+ controls.split(",")[i]).attr("type")=='tile'){
                                setTileDataFromDB(controls.split(",")[i],data);
                            } 
                            else if((objComp.type=='text' && objComp.classList.contains("editableCombo")) || objComp.type=='select-one' || objComp.type=='ComboBox' || objComp.type=='select-multiple'){
                                populateComboValuesfromString(controls.split(",")[i],data[i] ,data[i], true);
                            }
                            else{
                                setValue(controls.split(",")[i], data[i][0]);
                                //json[controls.split(",")[i]] =  data[i][0];
                            }
                        }
                        //setValues(json,true);
                    }
                }
            } else if ( jsonObj != null ){
                renderExecuteServerEventAPIData(jsonObj);
            }
            if(window[customAction]){
                window[customAction]();
            }
        }catch(ex){}
    }
    //Bug 81232 End

    function getSessionCleanURL() {
        if (window.closeWorkitemHook) {
            closeWorkitemHook();
        }

        closeSubForm();
        var sid = jQuery("#sid").val();
        var url = contextPath + "/components/viewer/cleanSession.jsp";
        //var reqTok = iforms.ajax.processRequest("formuri=" + encode_utf8(url), context + "/GetReqToken");
        var requestString = "pid=" + pid + "&wid=" + wid + "&taskid=" + tid + "&fid=" + fid + "&WD_SID=" + sid;
        if (typeof isProcessSpecific != "undefined") {
            requestString += "&ps=Y";
        }

        return url + "?" + requestString;

    }

    function clearNGHTMLViewMap(){
        if(window.closeWorkitemHook){
        closeWorkitemHook();
    }
        closeSubForm();
        var sid = jQuery("#sid").val();
        var context = '/' + window.location.pathname.split("/")[1];
        var url = context + "/components/viewer/cleanSession.jsp";
        var reqTok = iforms.ajax.processRequest("formuri="+encode_utf8(url), context+"/GetReqToken");
        var url="cleanSession.jsp";
        var requestString="pid="+pid+"&wid="+wid+"&taskid="+tid+"&fid="+fid+"&WD_SID=" + sid + "&WD_RID="+reqTok;
        
    if(typeof isProcessSpecific !="undefined"){
        requestString+="&ps=Y";
    }
    
    new net.ContentLoader(url, formHandler, formErrorHandler, "POST", requestString, false);
}

    function saveAndNextTab(tabId){
        var preHook = true;
        saveWorkItem();
        if( window.saveAndNextPreHook ){
            preHook = window.saveAndNextPreHook(tabId);
        }
        if(preHook){           
            var tabLinks=jQuery("#"+tabId).find("li");
            for(var i=0;i<tabLinks.length;i++){          
                if(tabLinks[i].classList.contains("tablink")&&tabLinks[i].classList.contains("active")&&i!=tabLinks.length-1 ){
                    for(var j=i+1;j<tabLinks.length;j++)
                    {
                        if(tabLinks[j].style.display != 'none')
                        {
                            $(tabLinks[j].firstChild).trigger("click");
                            jQuery("#" + tabId + " .iformTabUL").each(function () {
                                $(this).scrollingTabs('refresh');
                            });
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }
    function executeCustomWebService(obj,event,eventType){
        if(window.customWebServicePreHook){
            if(!customWebServicePreHook(obj.id)){
                return;
            }
        }
        var listviewOpened="N";//Bug 82812 Start
        var advancedListviewOpened="N";
        var sid = jQuery("#sid").val(); 
        var context = '/' + window.location.pathname.split("/")[1];
        var pageURL = context + "/components/viewer/webservice.jsp";
        var reqTok = iforms.ajax.processRequest("formuri="+encode_utf8(pageURL), context+"/GetReqToken");
        if(document.getElementById("listViewModal")!=null&&document.getElementById("listViewModal").className==="modal in")
            listviewOpened="NG";
        if(document.getElementById("advancedListViewModal")!=null&&document.getElementById("advancedListViewModal").className==="modal in")
            advancedListviewOpened="AG";//Bug 82812 End
        if(eventType!=""){
            //var url = "webservice.jsp";
            var requestString = "pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&controlId="+encode_utf8(obj.id)+"&eventType="+encode_utf8(eventType)+"&webServiceType=custom"+"&WD_SID=" + sid + "&WD_RID="+reqTok;//Bug 75527, Bug 75529
            if(listviewOpened!="N")//Bug 82812 Start
                requestString+="&listviewOpened="+listviewOpened;
            if(advancedListviewOpened!="N")
                requestString+="&advancedListviewOpened="+advancedListviewOpened;//Bug 82812 End
            var contentLoaderRef = new net.ContentLoader(pageURL, customWSResponseHandler, formErrorHandler, "POST", requestString, true);
        }
    }

    function customWSResponseHandler(){
        var output=this.req.responseText;
        var message=this.req.getResponseHeader("message");
        var ResponseCode = this.req.getResponseHeader("ResponseCode");
        var WSControlId=getQueryVariable(this.params, "controlId");
        if(typeof message !="undefined"&&message !="")//Bug 82907
            //showMessage("", message, "error");
              if(ResponseCode!="0"){
                showSplitMessage("", message,ERROR_TITLE, "error");
            }else{
                showSplitMessage("", message,SUCCESS_TITLE, "success");
            }
        try{
        var responseJSON=JSON.parse(output);
        var outputArray = responseJSON.responseData;
        if(responseJSON.APIData!=null)
            renderExecuteServerEventAPIData(responseJSON.APIData);
        for (var i = 0; i < outputArray.length; i++) 
        {
            for(var j=0;j<outputArray[i].length;j++){
                try{
                var dataArray=outputArray[i][j];
                var controlId=dataArray.id;
                var type=dataArray.type;
                var dataValue=dataArray.value.value;
                if(type=="textarea"||type=="textbox"||type=="label"||type=="combo"||type=="checkbox"
                    ||type=="radio"||type=="datepick"){
                    setValue(controlId, dataValue);
                }
                else if(type=="table"){
                    if(dataArray.isAppendData){
                        $("#"+controlId+ " tbody").html($("#"+controlId+ " tbody").html()+encode_ParamValue(dataValue));
                    }
                    else{
                        $("#"+controlId+ " tbody").html(encode_ParamValue(dataValue));
                    }
                    //$("#"+controlId+ " tbody").appendChild(dataValue);
                    $("#"+controlId).floatThead('reflow');
                    //var dgroupColumns = this.req.getResponseHeader("dgroupColumns");
                    //var maskedLabels = this.req.getResponseHeader("maskedLabels");
                    checkTableHeight(controlId);
                    /*
                    for(var i=0;i<dgroupColumns.split(",").length;i++){
                        var className = "dgroup_"+controlId+"_"+dgroupColumns.split(",")[i];
                        //var dgroupCells = document.getElementsByClassName("dgroup_"+controlId+"_"+dgroupColumns.split(",")[i]);

                        $('.'+className).each(function() {
                            var digitGroup = parseInt(dgroupColumns.split(",")[i].split("_")[1]);
                            var dec = '0';
                            if(jQuery(this).attr('typeofvalue')=='Float')
                                dec = jQuery(this).attr('Precision');
                            jQuery(this).autoNumeric('init',{
                                dGroup: digitGroup,
                                mDec: dec
                            }); 
                        });
                    }
                    */
                    $('.listviewlabel').each(function() {
                        var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
                        if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
                            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
                            {
                            maskfield(this,'savedlabel');
                        }

                    });
                $('.tabletextbox').each(function() {
                        var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
                        if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
                            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
                            {
                        maskfield(this,'input');
                    }

                });
                    var totalValueElements=document.getElementById('totallabel_'+controlId).innerHTML.split(",!,");
                        for(var k=0;k<totalValueElements.length;k++){
                        //var controlRef = document.getElementById('label'+'_'+controlId+'_'+maskedLabels.split(",")[i]);
                        if(totalValueElements[k]!=''){
                        $(document.getElementsByClassName(totalValueElements[k].replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&amp;/g, '&'))).each(function() {
                            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
                                if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
                                    || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
                                    {
                                maskfield(this,'label');
                                }
                    });
                        }
                            showTotal('',totalValueElements[k]);
                        }

                if(window.addRowPostHook)
                {
                            addRowPostHook(controlId);
                }
                }
            }
              catch(ex){
                    
                }
            }
        }
        if(window.customWebServicePostHook){
            customWebServicePostHook(WSControlId);
        }
        }
        catch(ex){}
    }

    function openLinkModal(ref){
        $("#iFrameLinkModal").attr('src', $(ref).attr("linkurl"));
        $("#LinkModal").dialog({
           width: $(window).width(),
            height: $(window).height(),
            modal: true,
            zIndex: 9999,
            resizeable: true,
            draggable: false,
            resize: "auto",
            close: function () {
                $("#iFrameLinkModal").attr('src', "about:blank");
            }
        });
        return false;
    }



function subformDone(buttonId){
    var valid;
    if(mobileMode=="ios"||mobileMode=="android")
        valid = validateMandatoryFields("iFrameSubFormModal");
    else
        valid = validateMandatoryFields();
    if(!valid){
        if(mobileMode=="ios"||mobileMode=="android")
            document.getElementById("SubFormDone").removeAttribute("data-dismiss");
        return false;
    }
    if(mobileMode=="ios"||mobileMode=="android")
        document.getElementById("SubFormDone").setAttribute("data-dismiss","modal");
    saveRichTextEditorData();
    var bool;
    if(window.subformDoneClick) {
        bool =  subformDoneClick(buttonId);
    }
    if(bool || bool == undefined) {
         if(mobileMode=="ios"||mobileMode=="android"){
             document.getElementById("SubFormDone").setAttribute("data-dismiss","modal");
         }
         else{
            window.close();
         }
    }
    else if(bool==false &&(mobileMode=="ios"||mobileMode=="android")) {
        document.getElementById("SubFormDone").removeAttribute("data-dismiss");
    }
    if(window.opener !=null)
    {
        window.opener.saveWorkItem();
    }
}
//Bug 82814 Start
function bodykeyDown(event){
    if(window.handleCustomKeyEvent)
        handleCustomKeyEvent(event);
    /*
     * var keyCode = event.which || event.keyCode;// to get event key code
     * to check for if key is pressed with ctrl or shift or alt you can check by using given check
     * event.altKey for alt key check
     * event.ctrlKey for ctrl key check
     * event.shiftKey for shift key check
     * 
     * Ex for CTRL+S
     * var keyCode = event.which || event.keyCode;
     * if(keyCode==83&&event.ctrlKey){
     * 
     * }
     */
}//Bug 82814 End

function addDataToAdvancedGrid(tableId,jsonData){
    if(Object.keys(jsonData).length > 0){
        var url = "action_API.jsp";
        var requestString=  "tableId="+tableId +"&addAdvancedGridData=yes"+"&pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid)+"&RowId="+rowId+"&jsonData="+encode_utf8(JSON.stringify(jsonData));
        var contentLoaderRef = new net.ContentLoader(url, addAdvancedGridDataResponseHandler, ajaxFormErrorHandler, "POST", requestString, false);
        setTableModifiedFlag(tableId);
        attachDatePicker();
    }
    
}

function addAdvancedGridDataResponseHandler(){
    var controlId=getQueryVariable(this.params, "tableId");
    $("#"+controlId+ " tbody").append(this.req.responseText);
    $("#"+controlId).floatThead('reflow');
    $('.listviewlabel').each(function() {
        var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
            maskfield(this,'savedlabel');
        }

    });
    $('.tabletextbox').each(function() {
        var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            if((this.getAttribute("maskingpattern")!="nomasking" && this.getAttribute("maskingpattern")!="")
            || (typeofvalue=='Float' && this.getAttribute("maskingpattern")=="nomasking"))
            {
            maskfield(this,'input');
        }

    });
    
    $('.openPickerClass').each(function()
        {
            if(this.getAttribute("maskingPattern")!=null && this.getAttribute("maskingPattern")!=undefined && this.getAttribute("maskingPattern")!="" )
            {
                maskfield(this,'input');
            }
        });

    checkTableHeight(controlId);
}
//Bug 83311 Start

function titleMaskingValidation(e,controlId)
{
    control=document.getElementById(controlId);
    var evtObj = window.event || e;
    var keycode = evtObj.keyCode || evtObj.which;
    var max = "";
    if(control !=null){
        if(control.getAttribute("typeofvalue") == "Float" && control.getAttribute("FloatLength") != null 
            && control.getAttribute("FloatLength")!= undefined){
            var dotIndex =control.value.indexOf(".");
//            var decimalValLength = control.getAttribute("floatlength")- control.getAttribute("precision");
            if( dotIndex<0 && keycode != 110 && keycode != 190 ){
                max=control.getAttribute("floatlength")- control.getAttribute("precision");
            }
            else
                max=control.getAttribute("floatlength");
        }
        else if(control.getAttribute("maxlength") != null && control.getAttribute("maxlength")!= undefined){
            max=control.getAttribute("maxlength");
        }
        if(max != null && max !='')
        {
           var ctrlDown = e.ctrlKey||e.metaKey;
        if(( e.key==="Delete"|| keycode==46 ) || (keycode==37 || keycode==39 || keycode==8||keycode==9) || ctrlDown && (keycode==86 ||keycode==67 || keycode==88|| keycode==65))
           return true;
            var value = jQuery("#"+controlId).val();
            var len = value.replace(/[^0-9]/g, "").length;
            if (len >= max && !isTextSelected(control)) {
                e.preventDefault();
                return false;
            }
        }
    }
    return true;
}

function isTextSelected(input) {
    var selecttxt = '';
    if (window.getSelection) {
        selecttxt = window.getSelection();
    } else if (document.getSelection) {
        selecttxt = document.getSelection();
    } else if (document.selection) {
        selecttxt = document.selection.createRange().text;
    }
 
    if (selecttxt == '') {
        return false;
    }
    return true;
 
}

function TitleCharacterValidation(e,option,textBoxId){
    var ctrlDown = e.ctrlKey||e.metaKey;
    var evtObj = window.event || e;
    var key = evtObj.key;
    if(key == "Decimal"){
        key = ".";
    }
    if(document.getElementById(textBoxId).getAttribute("maskingpattern")=="email" &&(evtObj.code=="Space" || key==" "))
        return false;
    var language = (typeof iformLocale == "undefined")? 'en_us': iformLocale;
    var patternStringRef=document.getElementById(textBoxId+"_patternString");
    var validString="";
    if(document.getElementById(textBoxId+"_validationString")!=undefined && document.getElementById(textBoxId+"_validationString")!=null)
        validString=document.getElementById(textBoxId+"_validationString").innerHTML;
    var dataType=document.getElementById(textBoxId).getAttribute("datatype");
    language=language.toLowerCase();
    if(window.handleCustomKeyEventOnTextControl)
    {
        if(!handleCustomKeyEventOnTextControl(event))
        return false;
    }
    var keycode = evtObj.keyCode || evtObj.which;
    if(keycode==13 && jQuery(".form-group").find(":password").length==1 ){
       if(window.triggerEventFromField)
        {
            var id=triggerEventFromField(textBoxId);
            if(id!=="")
            {
                document.getElementById(id).click();
                cancelBubble(e);
            }
            else
            {
                jQuery(".form-group :button").click(); 
            }
        }
        else
        {
            if (applicationName && applicationName != '') {
                jQuery(".form-group :button").click();
            }
        }
    }
    if(( e.key==="Delete"|| keycode==46 ) || (ctrlDown && (keycode==86 ||keycode==67 || keycode==88|| keycode==65)))
        return true;
    if(option=="2" && (language===""||language.startsWith("en")))
    {
        
            var alpha =patternStringRef.getAttribute("allowAlphabets");
            var space =patternStringRef.getAttribute("allowSpaces");
            var control =document.getElementById(textBoxId);
            var min=control.getAttribute("minvalue");
            var max=control.getAttribute("maxvalue");
            if(alpha==='false'&&space==='false')
            {
                //Bug 91919
                var alphaspaceregex = new RegExp("^[a-zA-Z ]*$");
                if (keycode!=8 && keycode!=9 && keycode!=37 && keycode!=39 && (alphaspaceregex.test(key))) {
                    e.preventDefault();
                    return false;
                }
                //Bug 91919
                if(patternStringRef.getAttribute("specialcharacters").indexOf(key)!==-1)
                {
                    //showMessage(control,key+IS_NOT_ALLOWED,"error"); //Bug 91919
                    return false;
                }
                else if(validString.indexOf(key)===-1)
                {   
                    
                    if(control.value!=""&&key==='-')
                    {
                        showSplitMessage(control,INVALID_POSITION+key,DATA_TITLE,"error");
                        return false; 
                    }    
                    else
                    {   
                        if(control.value.indexOf(key)!==-1&&isNaN(key))
                        {
                            showSplitMessage(control,CANNOT_USE+key+" again",DATA_TITLE,"error");
                            return false; 
                        }     
                        //var value=control.value+""+key;    
                        //if(!isNaN(value))
                        //{
                        //    if(parseFloat(value)<parseFloat(min)&&min!==null)
                        //    {
                        //        showSplitMessage(control,MIN_VALUE_ERROR+min,DATA_TITLE,"error");
                        //        return false;
                        //    }
                        //    if(parseFloat(value)>parseFloat(max)&&max!==null)
                        //    {
                        //        showSplitMessage(control,MAX_VALUE_ERROR+max,DATA_TITLE,"error");
                        //        return false;
                        //    }
                        // }
                    }
                } 
                return true;       
            }
        $('#'+textBoxId)[0].onkeypress = function (e) {
            var regex = new RegExp("^[a-zA-Z0-9 ]+$");
	    var key = String.fromCharCode(!e.charCode ? e.which : e.charCode);
            if(e.charCode == 8 || e.which==8 || e.charCode == 9 || e.which==9 || e.charCode == 127 || e.which==127 ){  //Bug 88287 
                return true;
            }
            try {
                    if(patternStringRef.getAttribute("specialcharacters").indexOf(key)==-1) {
			return true;
                    }
				
            } catch (ex) {
            }
            if (!regex.test(key)) {
                e.preventDefault();
                return false;
            }
        }
    }
    if(language===""||language.startsWith("en")){
        if(patternStringRef!=null && patternStringRef!=undefined && (dataType.toLowerCase() == 'text' || dataType == 'textarea')){
			if(patternStringRef.getAttribute("specialcharacters").indexOf(key)!==-1)
                return false;
            if(patternStringRef!=null && patternStringRef!=undefined && (patternStringRef.getAttribute("allowSpaces")==='false' || patternStringRef.getAttribute("allowNumbers")==='false' || patternStringRef.getAttribute("allowAlphabets")==="false")){
                var KeyID = evtObj.keyCode || evtObj.which;
                if(patternStringRef.getAttribute("allowSpaces")==='false'){
                    if(KeyID==32)
                        return false;
                }
                else{
                    if(KeyID==32)
                        return true;
                }
                
                if(patternStringRef.getAttribute("allowNumbers")==='false'){
                    if(KeyID>=48&&KeyID<=57&&!e.shiftKey)
                        return false;
                    if(KeyID>=96&&KeyID<=105&&!e.shiftKey)
                        return false;
                }
                else{
                    if(KeyID>=48&&KeyID<=57)
                        return true;
                }
                if(patternStringRef.getAttribute("allowAlphabets")==='false'){
                    if(KeyID>=65&&KeyID<=90)
                        return false;
                }
            }
        }
    }
    return true;
}

if (!String.prototype.startsWith) {
    String.prototype.startsWith = function(searchString, position){
      position = position || 0;
      return this.substr(position, searchString.length) === searchString;
  };
}

//Bug 83311 End

function setCustomPattern(controlId,pattern)
{
    var control=document.getElementById(controlId);
    if(useCustomIdAsControlName && (control==null || control==undefined)){
            control = document.getElementsByName(controlId)[0];
             if( control != null && control != undefined )
               controlId = control.getAttribute("id");
        }
    if(control !=null)
    {
//        control.setAttribute("custompattern",pattern);
        document.getElementById(controlId+"_patternString").setAttribute("custompattern",pattern);
        document.getElementById(controlId+"_patternString").innerHTML = pattern;
    }
}
function setCustomMasking(controlId,dgroup,adec,asep,asign,psign,decimal)
{
    var control = document.getElementById(controlId);
    if(useCustomIdAsControlName && (control==null || control==undefined)){
            control = document.getElementsByName(controlId)[0];
            if( control != null && control != undefined )
              controlId = control.getAttribute("id");
        }
    if(control != null) {
        control.setAttribute('custommasking','true');
    } else {
        return;
    }
    $(control).autoNumeric('destroy');
    jQuery(control).autoNumeric('init',{
        aSign: asign, 
        dGroup: dgroup,
        pSign:psign, 
        mDec: decimal,
        aDec: adec,
        aSep: asep
    });
    control.title=control.value;
}

function setMaskingPattern(controlId,pattern){
    var control=document.getElementById(controlId);
    if(useCustomIdAsControlName && (control==null || control==undefined)){
            control = document.getElementsByName(controlId)[0];
            if( control != null && control != undefined )
              controlId = control.getAttribute("id");
        }
    if(control!=null){
        var newPattern=pattern;
        if(pattern==="Pincode"){
            newPattern="000-000";
        }
        else if(pattern==="Email"){
            newPattern="email";
        }
        else if(pattern==="Dollar"){
            newPattern="currency_dollar";
        }
        else if(pattern==="Naira"){
           newPattern="currency_naira";
        }
        else if(pattern==="Rupees"){
            newPattern="currency_rupees";
        }
        else if(pattern==="Yen"){
            newPattern="currency_yen";
        }
        else if(pattern==="Euro"){
            newPattern="currency_euro";
        }
        else if(pattern==="French"){
            newPattern="currency_french";
        }
        else if(pattern==="Bahamian"){
            newPattern="currency_bahamas";
        }
        else if(pattern==="Greek"){
            newPattern="currency_greek";
        }
        else if(pattern==="Percentage"){
            newPattern="percentage";
        }
        else if(pattern==="USFormat"){
            newPattern="dgroup3";
        }
        else if(pattern==="IndianFormat"){
            newPattern="dgroup2";
        }
        else if(pattern==="Mobile"){
            newPattern="(00) 0000000000";
        }
        else if(pattern==="No Zero Padding"){
            newPattern="NZP";
        }
        else if(pattern===""||pattern==="Clear"){
            newPattern="nomasking";
        }
        var cleanValue=getValue(controlId);
        control.setAttribute("maskingPattern", newPattern);
        try{
            $(control).autoNumeric('destroy');
        }
        catch(ex){}
        try{
            $(control).unmask();
        }
        catch(ex){}
        $(control).each(function() {
            var max=this.getAttribute("rangemax");
            var min=this.getAttribute("rangemin");
            var typeofvalue=typeof this.getAttribute("typeofvalue")=='undefined'?'':this.getAttribute("typeofvalue");
            var precision=typeof this.getAttribute("Precision")=='undefined'?'2':this.getAttribute("Precision");
            var decimal='2';
            if(typeofvalue =="Float")
                decimal=precision;
            if(typeofvalue =="Integer")
                decimal='0';
            if(typeofvalue =="Long")
                decimal='0';

            if(this.getAttribute("maskingPattern").toString()!='nomasking'&&this.getAttribute("maskingPattern").toString()!=''){
            if(this.getAttribute("maskingPattern").toString()!='currency_rupees' && this.getAttribute("maskingPattern").toString()!=='currency_dollar' && this.getAttribute("maskingPattern").toString()!=='currency_naira' && this.getAttribute("maskingPattern").toString()!=='currency_yen' && this.getAttribute("maskingPattern").toString()!=='currency_euro' && this.getAttribute("maskingPattern").toString()!=='currency_french' && this.getAttribute("maskingPattern").toString()!=='currency_bahamas' && this.getAttribute("maskingPattern").toString()!=='currency_greek' && this.getAttribute("maskingPattern").toString()!=='' && this.getAttribute("maskingPattern").toString()!=='percentage'){
                    var placeholder;
                    if(this.getAttribute("maskingPattern").toString().charAt(this.getAttribute("maskingPattern").toString().length-1)!='$'){
                        if(this.getAttribute("maskingPattern").toString()=='dgroup3' || this.getAttribute("maskingPattern").toString()=='dgroup2'){
                            var digitGroup = parseInt(this.getAttribute("maskingPattern").charAt(this.getAttribute("maskingPattern").length-1));
                            jQuery(this).autoNumeric('init',{
                                dGroup: digitGroup,
                                mDec: decimal                                

                            });
                            if(cleanValue!=='')
                                jQuery(this).autoNumeric('set', cleanValue);
                        }
                        else{
                            if(typeofvalue=='Float' && this.getAttribute("maskingPattern").toString()=='NZP'){
                                jQuery(this).autoNumeric('init',{
                                    aSep : '',  
                                    aDec: '.', 
                                    mDec: decimal,
                                    aPad: false
                                });
                                if(cleanValue!=='')
                                    jQuery(this).autoNumeric('set', cleanValue);
                            }
                            else{
                                placeholder=this.getAttribute("maskingPattern").replace(/[A-Za-z0-9*#]/mg , "_");
                                jQuery(this).mask(this.getAttribute("maskingPattern"), {
                                    placeholder: placeholder
                                }, {
                                    clearIfNotMatch: true
                                });
                                setValue(controlId, cleanValue);
                                ctrOnchangeHandler(control,1);
                                return true;//Bug 79052
                            }
                        }
                    }
                }

                else{
                    var asign='';
                    var dgroup='';
                    var psign='p';
                var adec='.';
                var asep=',';
                    if(this.getAttribute("maskingPattern").toString()==='currency_rupees'){
                        asign='Rs ';
                        dgroup=2;
                    //                    jQuery(this).autoNumeric('init',{aSign: 'Rs ', dGroup: 2 , vMax: max, vMin: min});
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_dollar'){
                        asign='$ ';
                        dgroup=3;
                    //                        psign='s';
                    //                    jQuery(this).autoNumeric('init',{aSign: ' $', dGroup: 3,pSign: 's' ,vMax: max, vMin: min});
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_naira'){
                            asign='₦ ';
                            dgroup=3;
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_yen'){
                        asign='¥ ';
                        dgroup=3;
                    //                    jQuery(this).autoNumeric('init',{aSign: '¥ ', dGroup: 3, vMax: max, vMin: min});
                    }
                    else if(this.getAttribute("maskingPattern").toString()==='currency_euro'){
                        asign='€ ';
                        dgroup=3;
                    //                    jQuery(this).autoNumeric('init',{aSign: '€ ', dGroup: 3, vMax: max, vMin: min});
                    }
                else if(this.getAttribute("maskingPattern").toString()==='currency_french'){
                    asign='';
                    dgroup=3;
                    adec = ',';
                    asep = ' ';
                    psign= 's';
                //                    jQuery(this).autoNumeric('init',{aSign: '€ ', dGroup: 3, vMax: max, vMin: min});
                }
                else if(this.getAttribute("maskingPattern").toString()==='currency_bahamas'){
                        asign='B$ ';
                        dgroup=3;
                //                    jQuery(this).autoNumeric('init',{aSign: '¥ ', dGroup: 3, vMax: max, vMin: min});
                }
                else if(this.getAttribute("maskingPattern").toString()==='currency_greek'){
                    dgroup=3;
                    adec = ',';
                    asep = '.';
                    psign= 's';
                //                    jQuery(this).autoNumeric('init',{aSign: '€ ', dGroup: 3, vMax: max, vMin: min});
                }
                    if(this.getAttribute("maskingPattern").toString()!=='percentage' && this.getAttribute("maskingPattern").toString() !=='currency_yen' ){
                        if(max===null)
                            jQuery(this).autoNumeric('init',{
                                aSign: asign, 
                                dGroup: dgroup,
                                pSign:psign,
                                mDec: decimal,
                            aNeg:true,
                            aDec: adec,
                            aSep: asep
                            });
                        else{
                            jQuery(this).autoNumeric('init',{
                                aSign: asign, 
                                dGroup: dgroup,
                                pSign:psign, 
                            mDec: decimal,
                            aDec: adec,
                            aSep: asep
                            });
                        }
                    }
                    else if(this.getAttribute("maskingPattern").toString() =='currency_yen'){
                            if(max===null)
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign,
                                    mDec: "0",
                                aNeg:true,
                                aDec: adec,
                                aSep: asep
                                });
                            else{
                                jQuery(this).autoNumeric('init',{
                                    aSign: asign, 
                                    dGroup: dgroup,
                                    pSign:psign, 
                                mDec: "0",
                                aDec: adec,
                                aSep: asep
                                });
                            }
                        }

                    else{
                        jQuery(this).autoNumeric('init',{
                            aSign: " %", 
                            pSign:'s',
                            mDec: decimal
                        });//Bug 81106
                    }
                    if(cleanValue!=='')
                        jQuery(this).autoNumeric('set', cleanValue);
                }

            }
            
            setValue(controlId, cleanValue);
            ctrOnchangeHandler(control,1);
        });
    }
}
function attachZoneBehaviour(ref ,zName,zLeft, zoneTop, zWidth, zHeight){
    var final_width = parseInt(zLeft) + parseInt(zWidth);
    var final_height = parseInt(zoneTop) + parseInt(zHeight);
    if( window.preHookZoneGotFocus){
        var arr = preHookZoneGotFocus(zName,zLeft, zoneTop, final_width, final_height,ref.id);
        zLeft = arr[0];
        zoneTop = arr[1];
        final_width = arr[2];
        final_height = arr[3];
    }
    try{
        window.parent.ZoneGotFocus(zLeft, zoneTop, final_width, final_height,zName); 
    }
    catch(ex){}
}
 
 function deAttachZone()
{
    try{
        window.parent.ZoneLostFocus(); 
    }
    catch(ex){}
}

function removeFeatureFromRichTextEditor(){
    var selListJson = selectFeatureToBeIncludedInRichText();
    var commaSeparatedList = "";
    for( var key in selListJson ){
        if(!selListJson[key] ){
            commaSeparatedList += key +",";
        }
    }    
    commaSeparatedList = commaSeparatedList.substring(0,commaSeparatedList.length-1);   
    return commaSeparatedList;
}

function displayTableCells(controlId,rowIndex,colIndices,displayFlag){
    var tableRef = document.getElementById(controlId);
    var row = tableRef.tBodies[0].getElementsByTagName("tr")[rowIndex];
    var cells = row.getElementsByTagName("td");
    for(var i=1;i<=cells.length;i++){
        for(var j=0;j<colIndices.length;j++){
            if((i-1)==colIndices[j])
                cells[i].style.display=displayFlag?"":"none";
        }
    }
    $("#"+controlId).floatThead('reflow');
}
//Bug 83424 Start
function getCellControl(tableId,rowIndex,colIndex){
    var tableRef = document.getElementById(tableId);
    var row = tableRef.tBodies[0].getElementsByTagName("tr")[rowIndex];
    if(row != null && row != undefined)
    { 
        var cell = row.getElementsByTagName("td")[colIndex + 1];
        if (cell != null && cell != undefined){
            return cell.getElementsByClassName("control-class")[0];
        }
    }
}

function addItemInTableCellCombo(tableId,rowIndex,colIndex,label,value,tooltip,optionControlId){
    try{
    var combo=getCellControl(tableId,rowIndex,colIndex);
    if(combo!=null&&combo!=undefined&&combo.type=='select-one' || combo.type=='ComboBox')
    {
        var option;
        var selectedValue=combo.value;
        if (combo.tagName == 'SELECT') {
            for( var len = combo.options.length-1 ; len >= 0 ; len-- ){
                if( combo.options[len].text === label ){
                    combo.remove(len);
            }   
            }
        }
        else{//Bug 83222 Start
            var ul = combo.parentNode.childNodes[2];
            for(var i=ul.childNodes.length-1;i>=0;i--){
                if(ul.childNodes[i].innerHTML==label)
                    ul.removeChild(ul.childNodes[i]);
            }
        }//Bug 83222 End 
        option = document.createElement('option');        
        if (combo.tagName == 'SELECT') {
            if( typeof optionControlId != "undefined" ){
                option.id = optionControlId;
            }
            if (typeof label != "undefined" && typeof value == "undefined" && typeof tooltip == "undefined") {
                option.text = label;
                combo.add(option);
            }
            if (typeof label != "undefined" && typeof value != "undefined" && typeof tooltip == "undefined") {
                option.text = label;
                option.value = value;
                combo.add(option);
            }

            if (typeof label != "undefined" && typeof value != "undefined" && typeof tooltip !== "undefined") {
                option.text = label;
                option.value = value;
                option.setAttribute("data-toggle", "tooltip");
                option.title = tooltip;
                combo.add(option);                              
            }
            /*if(combo.multiple){
                reloadListBoxLayout(comboId);
            }*/
        } else {
            var liElem = document.createElement('li');
            if( typeof optionControlId != "undefined" ){
                liElem.id = optionControlId;
            }
            if (typeof label != "undefined" && typeof value == "undefined" && typeof tooltip == "undefined") {
                liElem.appendChild(document.createTextNode(label));
            }
            if (typeof label != "undefined" && typeof value != "undefined" && typeof tooltip == "undefined") {
                liElem.appendChild(document.createTextNode(label));
                liElem.setAttribute("value", value);
            }

            if (typeof label != "undefined" && typeof value != "undefined" && typeof tooltip !== "undefined") {
                liElem.appendChild(document.createTextNode(label));
                liElem.setAttribute("value", value);
                liElem.title = tooltip;
            }
            var ul = combo.parentNode.childNodes[2];
            liElem.style.display = "block";
            ul.appendChild(liElem);
        }
        //Bug 81099 If a field is mapped , the mapped field is coming twice in a dropdown
        combo.value=selectedValue;
    }
    }
    catch(ex){}
}
function clearTableCellCombo(tableId,rowIndex,colIndex){
    try{
    var combo=getCellControl(tableId,rowIndex,colIndex);
    if(combo!=null&&combo!=undefined&&combo.type=='select-one' || combo.type=='ComboBox')
    {
       // setTableCellData(tableId,rowIndex,colIndex,"",false);
        if(combo.type== "text"){
            var ul = combo.parentNode.childNodes[2];
            ul.innerHTML='';
        }else
            combo.options.length = 0;
        addItemInTableCellCombo(tableId,rowIndex,colIndex,"Select","");
        if(combo.type!="select-multiple" && combo.type!="text")
            combo.value= combo.options[0].value;
        else if(combo.type== "text"){
            combo.title=ul.childNodes[0].innerHTML;
            combo.value=ul.childNodes[0].innerHTML;
        }
    }
    }
    catch(ex)
    {}
}

function removeItemFromTableCellCombo(tableId,rowIndex,colIndex,itemIndex){
    var combo=getCellControl(tableId,rowIndex,colIndex);
    if (combo != null && combo != undefined)
    {
        combo.remove(itemIndex);
    }
    /*if(combo.multiple){
        reloadListBoxLayout(comboId);
    }*/
}
//Bug 83424 End
function getBrowserScrollSize(){

    var css = {
        "border":  "none",
        "height":  "200px",
        "margin":  "0",
        "padding": "0",
        "width":   "200px"
    };

    var inner = $("<div>").css($.extend({}, css));
    var outer = $("<div>").css($.extend({
        "left":       "-1000px",
        "overflow":   "scroll",
        "position":   "absolute",
        "top":        "-1000px"
    }, css)).append(inner).appendTo("body")
    .scrollLeft(1000)
    .scrollTop(1000);

    var scrollSize = {
        "height": (outer.offset().top - inner.offset().top) || 0,
        "width": (outer.offset().left - inner.offset().left) || 0
    };

    outer.remove();
    return scrollSize;
}
function setWidthForTabStyle4(){
    $(".iformTabControl").each(function(){
        var buttonsWidth=0;
        var buttonsDiv=$(this).find('.tabButtonsDiv');
        if(buttonsDiv[0]!=undefined){
            var child=buttonsDiv[0].children;
            for(var j=0;j<child.length;j++){
                buttonsWidth+=child[j].clientWidth+6;
            }
            //Bug 100087,Bug 100088 
            if(buttonsWidth===12){
              buttonsWidth=  buttonsDiv[0].style.width;
            }
            buttonsDiv[0].style.width=buttonsWidth+"px";
            var tabDiv=$(this).find('.tabDiv');
            tabDiv[0].style.width="calc(100% - "+buttonsWidth+"px)";
        }
    });
}

function multiSelectListView(ref,controlId)
    {
        var rowIndex = $(ref).closest('tr').index();
        var table = document.getElementById(controlId);
        if (table != null && table != undefined)
        {
            if(table.rows[rowIndex+1].classList.contains("highlightedRow"))
                table.rows[rowIndex+1].classList.remove("highlightedRow");
            else
                table.rows[rowIndex+1].classList.add("highlightedRow");
//            if (document.getElementById(controlId).getAttribute("ismultiselect") == "no")
            if(document.getElementById("select_"+controlId).style.display == "none")
            {
                for(var i=1;i<table.rows.length;i++)
                {
                    if(i!=rowIndex+1)
                    {
                        table.rows[i].classList.remove("highlightedRow");
                    }
                }
                var checks = document.getElementById(controlId).getElementsByClassName('selectRow');
                for(var i=0;i<checks.length;i++)
                {
                    if(i!=parseInt(rowIndex) && checks[i].checked){
                        checks[i].checked = false;
                    }
                }
                
            }
        }
		if(window.getGridSelectedIndex){
            getGridSelectedIndex(controlId, rowIndex);
        }
}

function initFloatingMessagesForPrimitiveFields(controlClass){
    $(controlClass).tooltipster({
        repositionOnScroll:true,
        debug:false,
        functionBefore:function(instance,helper){
            if(($(helper.origin).find('.mndErrorMsgDiv')[0]!=null&&$(helper.origin).find('.mndErrorMsgDiv')[0].hasAttribute("showMessage"))
            ||($(helper.origin).find('.ptrnErrorMsgDiv')[0]!=null&&$(helper.origin).find('.ptrnErrorMsgDiv')[0].hasAttribute("showMessage"))){
                var newContent="";
                if($(helper.origin).find('.mndErrorMsgDiv')[0]!=null&&$(helper.origin).find('.mndErrorMsgDiv')[0].hasAttribute("showMessage"))
                    newContent=$(helper.origin).find('.mndErrorMsgDiv')[0].innerHTML;
                else if($(helper.origin).find('.ptrnErrorMsgDiv')[0]!=null&&$(helper.origin).find('.ptrnErrorMsgDiv')[0].hasAttribute("showMessage"))
                    newContent=$(helper.origin).find('.ptrnErrorMsgDiv')[0].innerHTML;
                instance.content(newContent);
            }
            else{
                return false;
            }
        },
        functionAfter:function(instance, helper){
            if(controlClass == ".controlCustomCss"){
            jQuery(".tooltipster-content").removeClass("customControlTitle");
            jQuery(".tooltipster-arrow").removeClass("customControl-arrow");
            }
        },
        functionReady:function(instance,helper){
            if(jQuery(helper.origin).find("select") != undefined && jQuery(helper.origin).find("select").attr("combotype")=="listbox" 
                && controlClass == ".errorMessageHoverDiv"){
                jQuery(".tooltipster-content").addClass("customControlTitle");
                jQuery(".tooltipster-arrow").addClass("customControl-arrow");
            }
            if (instance._$tooltip) {
                var boundingRect = helper.origin.getBoundingClientRect();
                var tTop = instance.__lastPosition.coord.top;
                var tHeight = instance._$tooltip[0].clientHeight;
                var tBottom = tTop + tHeight;
                var topDiff = tBottom - boundingRect.top;
                if (topDiff > 0) {
                    instance._$tooltip[0].style.top = tTop - topDiff + "px";
                }
            }
        },
        functionPosition:function(instance,helper,position){
            var boundingRect=helper.origin.getBoundingClientRect();
            position.coord.left=boundingRect.left+10;
            position.target=boundingRect.left+10;
            if(boundingRect.width-10<position.size.width)
                position.size.width=boundingRect.width-10;
            if(position.coord.top>boundingRect.bottom-1)
                position.coord.top=boundingRect.bottom-1;
            position.size.height="";
            return position;
        }
    });
}
function initFloatingMessagesForTableCells(){
    $(".tabletextbox").each(function() {
        $(this).closest("td").tooltipster({
            repositionOnScroll:true,
            debug:false,
        functionBefore:function(instance,helper){
            if($(helper.origin).find('.icon-errorMessageIconClass')[0]==null){
                return false;
            }
        },
        functionReady:function(instance,helper){
            if (instance._$tooltip) {
                var boundingRect = helper.origin.getBoundingClientRect();
                var tTop = instance.__lastPosition.coord.top;
                var tHeight = instance._$tooltip[0].clientHeight;
                var tBottom = tTop + tHeight;
                var topDiff = tBottom - boundingRect.top;
                if (topDiff > 0) {
                    instance._$tooltip[0].style.top = tTop - topDiff + "px";
                }
            }
        },
        functionPosition:function(instance,helper,position){
            var boundingRect=helper.origin.getBoundingClientRect();
            position.coord.left=boundingRect.left+10;
            position.target=boundingRect.left+10;
            if(boundingRect.width-10<position.size.width)
                position.size.width=boundingRect.width-10;
            if(position.coord.top>boundingRect.bottom-1)
                position.coord.top=boundingRect.bottom-1;
            position.size.height="";
            return position;
        }
        });
    });
}

function getFeatureForRichTextEditor(){
    var selListJson = selectFeatureToBeIncludedInRichText();
    var commaSeparatedList = [];
    for( var key in selListJson ){
        if(selListJson[key] ){
            //commaSeparatedList +="'"+ key +"',";
            commaSeparatedList.push(key);
        }
    }    
    //commaSeparatedList = '['+commaSeparatedList.substring(0,commaSeparatedList.length-1)+']';   
    
    return commaSeparatedList;
}

function clearComponentMap(listViewType){
     var listViewControls = document.getElementsByClassName('tableControl');
     var advancedListviewControls=document.getElementsByClassName('advancedListviewControl');
     var componentmap=Object.keys(ComponentValidatedMap);
        if(componentmap.length!=0){
             if(listViewType=="listview"){
                for(var j=0;j<listViewControls.length;j++){
                    for(var k=0;k<componentmap.length;k++){
                     if(componentmap[k]==listViewControls[j].id){                  
                         delete ComponentValidatedMap[componentmap[k]];
                     }
                     }                   
                }
             }
            else if(listViewType=="advancedlistview"){
                for(var j=0;j<advancedListviewControls.length;j++){
                    for(var k=0;k<componentmap.length;k++){
                     if(componentmap[k]==advancedListviewControls[j].id){                    
                         delete ComponentValidatedMap[componentmap[k]];
                     }
                     }                     
                }        
            }        
        }
}

function clearServerComponentMap(listViewType){
     var listViewControls = document.getElementsByClassName('tableControl');
     var advancedListviewControls=document.getElementsByClassName('advancedListviewControl');
     var componentmap=Object.keys(serverValidationMap);
        if(componentmap.length!=0){
             if(listViewType=="listview"){
                for(var j=0;j<listViewControls.length;j++){
                    for(var k=0;k<componentmap.length;k++){
                     if(componentmap[listViewControls[j].id]){                  
                         delete serverValidationMap[listViewControls[j].id];
                     }
                     }                   
                }
             }
            else if(listViewType=="advancedlistview"){
                for(var j=0;j<advancedListviewControls.length;j++){
                    for(var k=0;k<componentmap.length;k++){
                     if(componentmap[advancedListviewControls[j].id]){                    
                         delete serverValidationMap[advancedListviewControls[j].id];
                     }
                     }                     
                }        
            }        
        }
}

function constraintOnPaste(ref,evt)
{
    var max = ref.getAttribute("maxlength");
    if(max == null)
        max = ref.getAttribute("floatlength");
    var typeofvalue = ref.getAttribute("typeofvalue");
	if(typeofvalue != null)
    typeofvalue = typeofvalue.toUpperCase();	
    setTimeout(function () {
         if(!fieldValidation(ref,ref.value,true)){
            ref.value="";
        }
    }, 100);
    if (max != null)
    {
        if (evt.clipboardData && ref.getAttribute("precision").length > 0 && typeofvalue ==='FLOAT')
        {

            var strClipBoardData = removeLeadingAndTrailingZero(evt.clipboardData.getData('text/plain'));
			strClipBoardData = strClipBoardData.trim();
            if (strClipBoardData.indexOf('.') != -1)
            {
                var clipboardprecis_len = strClipBoardData.substring(strClipBoardData.indexOf('.') + 1).length;
                var clipboardfloat_len = strClipBoardData.substring(0, strClipBoardData.indexOf('.')).length;
            } else
            {
                clipboardfloat_len = strClipBoardData.length;
                clipboardprecis_len = 0;
            }
            var floatprecision = ref.getAttribute("precision");

            if (clipboardprecis_len > floatprecision || (clipboardfloat_len > max - floatprecision))
            {
                showSplitMessage(ref, CORRECT_VALUE + ' > ' + strClipBoardData,DATA_TITLE,"error");
                cancelBubble();
                ref.value = "";
            }
        } else if (window.clipboardData && ref.getAttribute("precision").length > 0 && typeofvalue ==='FLOAT')
        {

            var strClipBoardData = removeLeadingAndTrailingZero(window.clipboardData.getData('text'));
			strClipBoardData = strClipBoardData.trim();
            if (strClipBoardData.indexOf('.') != -1)
            {
                var clipboardprecis_len = strClipBoardData.substring(strClipBoardData.indexOf('.') + 1).length;
                var clipboardfloat_len = strClipBoardData.substring(0, strClipBoardData.indexOf('.')).length;
            } else
            {
                clipboardfloat_len = strClipBoardData.length;
                clipboardprecis_len = 0;
            }

            var floatprecision = ref.getAttribute("precision");

            if (clipboardprecis_len > floatprecision || (clipboardfloat_len > max - floatprecision))
            {
               showSplitMessage(ref, CORRECT_VALUE + ' > ' + strClipBoardData,DATA_TITLE, "error");
                cancelBubble();
                ref.value = "";
            }
        } else if (evt.clipboardData && (evt.clipboardData.getData('text/plain').length > max))
        {
            showSplitMessage(ref, ONLY_FIRST + max + ONPASTE_MSG + max,DATA_TITLE, "error");
        } else if (window.clipboardData && (window.clipboardData.getData('text').length > max))
        {
            showSplitMessage(ref, ONLY_FIRST + max + ONPASTE_MSG + max,DATA_TITLE, "error");
        }
    }
}

function removeLeadingAndTrailingZero(floatVal) // this function removes leading and trailing 0s in float
{
    var regEx1 = /^[0]+/;
    var regEx2 = /[0]+$/;
    var regEx3 = /[.]$/;
    var after = '';
    after = floatVal.replace(regEx1,'');  // Remove leading 0's
    if (after.indexOf('.')>-1){
        after = after.replace(regEx2,'');  // Remove trailing 0's
    }
    after = after.replace(regEx3,''); 
    return after;
}

function TableCellCharacterValidation(ref,event){
    var evtObj = window.event || event;
    var language = (typeof iformLocale == "undefined")? 'en_us': iformLocale;
    var allowAlphabets = ref.getAttribute("alphanum").split("-")[0];
    var allowSpaces = ref.getAttribute("alphanum").split("-")[1];
    var allowNumbers = ref.getAttribute("alphanum").split("-")[2];
    var allowSpecialchars = ref.getAttribute("alphanum").split("-")[3];
    language=language.toLowerCase();
    if(allowSpecialchars=='N')
    {
        $(ref).on('keypress', function (e) {
            var regex = new RegExp("^[a-zA-Z0-9 ]+$");
            var key = String.fromCharCode(!e.charCode ? e.which : e.charCode);
            if (!regex.test(key)) {
                e.preventDefault();
                return false;
            }
        });
    }
    if(language===""||language.startsWith("en")){
        
        if(allowAlphabets==='N' || allowSpaces==='N' || allowNumbers==="N"){
            var KeyID = evtObj.keyCode || evtObj.which;
            if(allowSpaces==='N'){
                if(KeyID==32)
                    return false;
            }
            else{
                if(KeyID==32)
                    return true;
            }
                
            if(allowNumbers==='N'){
                if(KeyID>=48&&KeyID<=57&&!event.shiftKey)
                    return false;
                if(KeyID>=96&&KeyID<=105&&!event.shiftKey)
                        return false;
            }
            else{
                if(KeyID>=48&&KeyID<=57)
                    return true;
            }
            if(allowAlphabets==='N'){
                if(KeyID>=65&&KeyID<=90)
                    return false;
            }
            else{
                if(KeyID>=65&&KeyID<=90)
                    return true;
            }
           
        }
       
    }
    return true;
}

function getSectionList()
{
    var sectionList=document.getElementsByClassName("FrameControl");
    var jsonobj={};
    for(var i=0;i<sectionList.length;i++)
        jsonobj[sectionList[i].id]=sectionList[i].childNodes[0].getAttribute("state");
    return jsonobj;
}
//Bug 84965 
function stopFormRefreshing(e){
        var kc = (e.charCode) ? e.charCode : ((e.which) ? e.which : e.keyCode);
        if(kc==123&&e.key==="F12"){
            cancelBubble(e);
        }
        if(kc==8 )
        {   
            var ele = e.srcElement;
            if( ele == null )
                ele = e.target;
            if( ( ele.tagName != "INPUT" &&  ele.tagName != "TEXTAREA" ) || ele.readOnly || ele.getAttribute("disabled") == "disabled" )
               cancelBubble(e);
        }
}
function cancelBubble(e) 
{
        var evt = e ? e:window.event;
        if (evt.stopPropagation)
        {
            evt.stopPropagation();
            evt.preventDefault();
        }
        if (evt.cancelBubble!=null || evt.cancelBubble!=true)
        {
            evt.cancelBubble = true;
            evt.returnValue = false;
        }
}

function showFullScreen(iFrameId){
    var iframe = document.getElementById(iFrameId);
    iframe.style.position = "fixed";
    iframe.style.bottom = "0px";
    iframe.style.right = "0px";
    iframe.style.border = "none";
    iframe.style.margin = "0";
    iframe.style.padding = "0";
    iframe.style.overflow = "hidden";
    iframe.style.zIndex = "5999";
    iframe.style.height = "100%";
    iframe.style.top = "25px";
    
    var fsImage = document.getElementById("fullScreenImage_"+iFrameId);
    var fsDiv = document.getElementById("fullScreenDiv_"+iFrameId);
    fsImage.classList.remove("glyphicon-fullscreen");
    fsImage.classList.add("glyphicon-resize-small");
    fsImage.style.border="1px solid";
    fsImage.style.fontSize="20px";
    
    fsImage.onclick = function() {escapeFullScreen(iFrameId);}
    

    fsDiv.style.left="0px";
    fsDiv.style.top="0px";
    fsDiv.style.width="100%";
    fsDiv.style.position="fixed";
    fsDiv.style.zIndex="6000";
    fsDiv.style.background="white";
}
function escapeFullScreen(iFrameId){
    var iframe = document.getElementById(iFrameId);
    iframe.style.position = "";
    iframe.style.bottom = "";
    iframe.style.right = "";
    iframe.style.margin = "";
    iframe.style.padding = "";
    iframe.style.overflow = "";
    iframe.style.zIndex = "";
    iframe.style.height = "";
    iframe.style.top = "";
     
    var fsImage = document.getElementById("fullScreenImage_"+iFrameId);
    var fsDiv = document.getElementById("fullScreenDiv_"+iFrameId);
    fsImage.classList.remove("glyphicon-resize-small");
    fsImage.classList.add("glyphicon-fullscreen");
    fsImage.style.border="";
    fsImage.style.fontSize="";
    fsImage.onclick = function() {showFullScreen(iFrameId);}
    
    fsDiv.style.width="";
    fsDiv.style.top="";
    fsDiv.style.left="";    
    fsDiv.style.position="";
    fsDiv.style.zIndex="";
    fsDiv.style.background="";
}

function c_isNaN(character){
    if(character==' ' || character==" " || character==""||character=='')
        return true;
    return isNaN(character);
}

function authorizePortalLogin(ref,evt){
    var requestString = "pid="+encode_utf8(pid)+"&wid="+encode_utf8(wid)+"&tid="+encode_utf8(tid)+"&fid="+encode_utf8(fid);       
    var url="auth.jsp";
    var contentLoaderRef = new net.ContentLoader(url, formHandler, formErrorHandler, "POST", requestString, false);
}

function highlightSel(ref)
{
    if(ref.closest('td').classList.contains("highlightedCell"))
        ref.closest('td').classList.remove("highlightedCell");
    else
        ref.closest('td').classList.add("highlightedCell");
}

function highlightAddDelRow(ref)
{
    if(ref.classList.contains("hightlightAddDeleteRow"))
        ref.classList.remove("hightlightAddDeleteRow");
    else
        ref.classList.add("hightlightAddDeleteRow");
}
//Bug 91554 
function showCustomCtrlToolTip(ref){
    var id=ref.firstElementChild.id;
    var str="";
    var controls=jQuery(ref).find(".active");
    jQuery(ref).find("button").removeAttr("title");
    if(controls.length=== 0)
        return;
    for(var i=0;i<controls.length;i++){
        var option=controls[i];
        var value=jQuery(option).find("label").text();
        
        if(i=== controls.length-1){
            str +=value;
        }
        else{
           if(value.trim()!="Select all")
            str +=value+", ";
        }
    }
    
    document.getElementById(id+"_msg").innerHTML=str;
    document.getElementById(id +"_msg").setAttribute("showMessage","true");
    jQuery(".tooltipster-box").addClass("customControlTitle");
}
function removeControlTooltip(ref){
    var id=ref.firstElementChild.id;
    if(id != undefined && id != null && document.getElementById(id +"_msg")!= undefined && document.getElementById(id +"_msg")!= null){
        jQuery(".tooltipster-box").removeClass("customControlTitle");
    }
}

function tileImageClick(ref)
{
    var clicked = ref.classList.contains("clicked");
    var tileId = ref.parentNode.parentNode.id;
    if(clicked==true){
        document.getElementById(tileId+'-false').click();
        ref.classList.remove("clicked");
    }    
    else  
    {
       document.getElementById(tileId+'-true').click();
       ref.classList.add("clicked"); 
    }
}

function IframeRequestWithPost(url,ifrm){     
    var actionURL=getActionUrlFromURL(url);
    var listParam=getInputParamListFromURL(url);     
    var popup;     
    popup = (ifrm.contentWindow) ? ifrm.contentWindow : (ifrm.contentDocument.document) ? ifrm.contentDocument.document : ifrm.contentDocument;     
    popup.document.open();     
    popup.document.write("<HTML><HEAD><TITLE></TITLE></HEAD><BODY>");     
    popup.document.write("<form id='postSubmit' method='post' action='"+actionURL+"' enctype='application/x-www-form-urlencoded'>");     
    for(var iCount=0;iCount<listParam.length;iCount++)      {             
        var param=listParam[iCount];
        popup.document.write("<input type='hidden' id='"+param[0]+"' name='"+param[0]+"'/>");             
        popup.document.getElementById(param[0]).value=param[1];
        //handle single quotes etc      
        }      
        popup.document.write("</FORM></BODY></HTML>");      
        popup.document.close();      
        popup.document.forms[0].submit(); 
    }

function getActionUrlFromURL(sURL) {    
    var ibeginingIndex=sURL.indexOf("?");     
    if (ibeginingIndex == -1)         
        return sURL;     
    else         
        return sURL.substring(0,ibeginingIndex); 
}

function getInputParamListFromURL(sURL) {     
    var ibeginingIndex=sURL.indexOf("?");     
    var listParam=new Array();     
    if (ibeginingIndex == -1){         
        return listParam;    
    }     
    var tempList = sURL.substring(ibeginingIndex+1,sURL.length);
    if(tempList.length>0)     {         
        var arrValue =tempList.split("&");         
        for(var iCount=0;iCount<arrValue.length;iCount++)         {             
            var arrTempParam=arrValue[iCount].split("=");            
            try             {                 
                listParam.push(new Array(decode_ParamValue(arrTempParam[0]),decode_ParamValue(arrTempParam[1])));             
            }catch(ex)             
            {             
            }         
        }     
    }     
    return listParam; 
}
function decode_ParamValue(param)
{
    var tempParam =param.replace(/\+/g,' ');
    tempParam = decodeURIComponent(tempParam);
 
    return tempParam;
}
function setStyleRadioOption(dataArray)
{
    var radioid=dataArray.controlid;
    radioid="#"+radioid;
    var length=$(radioid).find(".inputStyle").length;
    for (var i = 0; i < length; i++)
    {
       var optlabel=$(radioid).find(".inputStyle")[i].innerHTML.trim();
       if(optlabel.toLowerCase() == dataArray.OptionLabel && dataArray.Visibility == "false")
       {
           $($($(radioid).find(".inputStyle")[i]).parent()).css("display","none");
           break;
       }
       else if(optlabel.toLowerCase() == dataArray.OptionLabel && dataArray.Visibility == "true")
       {
           var align=$($($(radioid).find(".inputStyle")[i]).parent()).attr('allignment');
           if(align != undefined && align != null && align == "horizontal")
           {
               $($($(radioid).find(".inputStyle")[i]).parent()).css("display","inline-block");
               break;
           }
           else
           {
               var style1= $($($(radioid).find(".inputStyle")[i]).parent()).prop('style');
               style1.removeProperty('display');
               break;
           }
       }
       
    }
}
function setTile(dataArray)
{
    var tileid=dataArray.id;
    tileid='#'+tileid;
    var value=dataArray.value;
//    ($(tileid)).parent().html("");
//    var style1=$(tileid).prop('style');
//    style1.removeProperty('height');
//    //$(tileid).removeAttr('style','height');
//    $(tileid).append(value);
    var parent=$(tileid).parent();
    parent.html("");
    parent.append(value);
    setTileHeight();  
}

function expandTextArea(ref)
{
    if($(ref).parent().hasClass('Read-less-span'))
    {
        $(ref).parent().css('display','none');
        $($($(ref).parent().parent())).find('.Read-more-Span').css('display','block');
    }   
    else
    {
        $(ref).parent().css('display','none');
        $($($(ref).parent().parent())).find('.Read-less-span').css('display','block');
    }
}

function tileSwiping(){
    var tileGroupIds = [];
    $(".tile").find(".tilebtnstyle").each(
     function(i,elem){
        if(elem.hasAttribute("groupname") && elem.getAttribute("groupname")!=""){
            tileGroupIds.push(elem.getAttribute("groupname"));
        }
     }  
    );
     var distinctIds = tileGroupIds.filter(function(v, i, self) 
            { 
                return i == self.indexOf(v); 
            }); 
     var tilesHtml = "";
     var len = document.getElementsByClassName("tilebtnstyle").length;
     var i,j,k,end=0;
     var index;
     for ( i = 0; i < distinctIds.length; i++) {
        var arr = [];
        tilesHtml= "";
        for ( j = end; j < len; j++) {
            var elem = document.getElementsByClassName("tilebtnstyle")[j];
            if (elem.hasAttribute("groupname") && elem.getAttribute("groupname") != "" && elem.getAttribute("groupname") == distinctIds[i]) {
                tilesHtml += elem.parentElement.parentElement.parentElement.parentElement.outerHTML;
                $(elem).parents().eq("3").addClass("removeTile");
                arr.push(j);
            }
        }
        var begin = arr[0];
        var end = arr[arr.length-1];
        var elem = $($(".tilebtnstyle")[begin]);
        var parent = $(elem).parents().eq("4");
        var index =$(elem).parents().eq("4").children().index($(elem).parents().eq("3"));
        if($(parent).children().eq(index)!=null && $(parent).children().eq(index)!=undefined)
            $(parent).children().eq(index).before("<div class='SlideTile"+i+"' ></div>");
        else
            $(parent).append("<div class='SlideTile"+i+"' ></div>");

        $(".removeTile").remove();
        $(".SlideTile"+i).html(tilesHtml);
        end++;
        $(".SlideTile"+i).slick({
            infinite: false,
            vertical: false,
            dots: false,
            arrows: false,
            slidesToShow: 1,
            slidesToScroll: 1,
            centerMode: true,
            focusOnSelect: false,
            centerPadding: '40px'
        }
        );
    }
}
function disableDoclistControl(controlId, disableDoclist) {
    if (disableDoclist) {
        if ($("#" + controlId).find(".unit-attach-selected") != null && $("#" + controlId).find(".unit-attach-selected") != undefined) {
            $("#" + controlId).find(".unit-attach-selected").each(function() {
                $(this).css("display", "none");
            });
        }
        if ($("#" + controlId).find(".doc-unit-delete") != null && $("#" + controlId).find(".doc-unit-delete") != undefined) {
            $("#" + controlId).find(".doc-unit-delete").each(function() {
                $(this).css("display", "none");
            });
        }
        if ($("#" + controlId).find(".doc-unit-comments") != null && $("#" + controlId).find(".doc-unit-comments") != undefined) {
            $("#" + controlId).find(".doc-unit-comments").each(function() {
                if ($(this).children()[0] != null && $(this).children()[0] != undefined) {
                    $($(this).children()[0]).addClass("disabledBGColor");
                    $($(this).children()[0]).prop("disabled", true);
                }
            });
        }
    } else {
        if ($("#" + controlId).find(".unit-attach-selected") != null && $("#" + controlId).find(".unit-attach-selected") != undefined) {
            $("#" + controlId).find(".unit-attach-selected").each(function() {
                $(this).css("display", "");
            });
        }
        if ($("#" + controlId).find(".doc-unit-delete") != null && $("#" + controlId).find(".doc-unit-delete") != undefined) {
            $("#" + controlId).find(".doc-unit-delete").each(function() {
                $(this).css("display", "");
            });
        }
        if ($("#" + controlId).find(".doc-unit-comments") != null && $("#" + controlId).find(".doc-unit-comments") != undefined) {
            $("#" + controlId).find(".doc-unit-comments").each(function() {
                if ($(this).children()[0] != null && $(this).children()[0] != undefined) {
                    $($(this).children()[0]).removeClass("disabledBGColor");
                    $($(this).children()[0]).prop("disabled", false);
                }
            });
        }
    }
}

function updateSessionTimeout() {
    if( window.opener ){
        if(window.opener.sessionTimeOut && typeof window.opener.parent.updateWebSessionTime != 'undefined')
        window.opener.parent.updateWebSessionTime();
        iforms.ajax.processRequest("", contextPath+"/updatesessiontime?time=120&RID="+Math.random());
        sessionTimeOut = false;
    }
    else if (sessionTimeOut && typeof window.parent.updateWebSessionTime != 'undefined') {
        window.parent.updateWebSessionTime();
        iforms.ajax.processRequest("", contextPath+"/updatesessiontime?time=120&RID="+Math.random());
        sessionTimeOut = false;
    }
}


function renderCaptchaImage(ref) {
    var controlid=ref.getAttribute("controlid")
    if(ref) {
        var captchaimgref=document.getElementById(controlid+'_img');
        captchaimgref.src ='/iforms/getcaptcha?rid=' + Math.random();
    }
    
    var captchtxtref = document.getElementById(controlid+'_txt');
    if(captchtxtref) {
        captchtxtref.value = '';
    }
    var url = "portal/appTask.jsp";
    var queryString = "oper=RefreshCaptchaBtn&attrname=" + encode_utf8(controlid) +"&pid=" + encode_utf8(pid) + "&wid=" + encode_utf8(wid) + "&tid=" + encode_utf8(tid) + "&fid=" + encode_utf8(fid);
    var captchcontrolHTML = iforms.ajax.processRequest(queryString, url);
    document.getElementById(controlid).innerHTML=captchcontrolHTML;
}
function printLog(data) {
    var url = "ifhandler.jsp";
    var requestString = "pid=" + encode_utf8(pid) + "&wid=" + encode_utf8(wid) + "&tid=" + encode_utf8(tid) + "&fid=" + encode_utf8(fid) + "&op=10&AttribXML=" + encode_utf8(data);
    iforms.ajax.processRequest(requestString, url);
}
 function setToolTip(ref,li){
    $('#'+ref.id).prop("title",li[0].getAttribute("originalvalue"));
 }
 
 //Bug 100264
 function showDownloadPDFMsg(status){
     if(status==0)
        showSplitMessage("", PDF_FILE_DOWNLOAD_SUCCESS,SUCCESS_TITLE, "success");    
     else 
        showSplitMessage("", PDF_FILE_DOWNLOAD_FAILURE,ERROR_TITLE, "error");     
 }
 
 function sessionTimeChecker() {
    EFF_SESSION_TIME = 0;
    var appURL = "../../GetSessionTimeout";
    
    
    new net.ContentLoader(appURL, sessionTimeCheckerCallBack, sessionTimeCheckerError, "POST", "", true);
    sessionTimeReqSent++;

    function sessionTimeCheckerCallBack() {
        sessionTimeReqRecv++;
        var returnxml = this.req.responseText;

        if (returnxml.length > 0  && !isNaN(returnxml)) {
            if ((returnxml - 0) > 0) {
                var appSessionTime = (returnxml - 0);
                if (appSessionTime > EFF_SESSION_TIME) {
                    EFF_SESSION_TIME = appSessionTime;
                }
            }
        }

        if (sessionTimeReqSent == sessionTimeReqRecv) {
            setSessionCheckTimeout(EFF_SESSION_TIME);
        }
    }

    function sessionTimeCheckerError() {
        sessionTimeReqRecv++;
        if (sessionTimeReqSent == sessionTimeReqRecv) {
            setSessionCheckTimeout(EFF_SESSION_TIME);
        }
    }
}


function setSessionCheckTimeout(time) {
    
    var sessionExpireWarnTimeInSeconds = (SessionExpireWarnTime-0)*60;
    if(sessionExpireWarnVisible) {
        hideWebSessionTimer();
    }
    if(time > 0 && time > sessionExpireWarnTimeInSeconds) {
        UpdateAppsessionTime(time);
        var timeoutSeconds = time - sessionExpireWarnTimeInSeconds;
         clearTimeout(appSessionCheckTimer);
         appSessionCheckTimer = setTimeout("sessionTimeChecker()", timeoutSeconds*1000);
         setTimeout("sessionTimeChecker()", timeoutSeconds*1000);
    } else if(time <= sessionExpireWarnTimeInSeconds){
        if ( SessionExpireWarnTime > 0 && !sessionExpireWarnVisible) {
            clearTimeout(appSessionCheckTimer);
            UpdateAppsessionTime(time);
            sessionTimeoutNotifier(time);
        }  else {
            invalidateSessionTime();
        }
    }
}

function invalidateSessionTime() {
    window.location = "../../components/error/sessionInvalid.jsp";
}

function sessionTimeoutNotifier(time)
{
    var nSessionExpireWarnTime = (SessionExpireWarnTime - 0) * 60;
    if(typeof time == 'undefined' || time == null || time > nSessionExpireWarnTime) {
        time = nSessionExpireWarnTime;
    }
    showSessionExpireWarning(time);
}

function showSessionExpireWarning(remainingtime)
{                
    if(!sessionExpireWarnVisible) {   
        addToSessionTimer(window);
        notifyOAPSessionTimer(remainingtime);
    }
}

function addToSessionTimer(windowRef) {
    if(sessionTimerWindows.length == 0) {
        sessionTimerWindows[0] = windowRef;
    } else {
        var bWindowExistFlag = false;
        for(var i = 0; i < sessionTimerWindows.length; i++) {
            if(typeof sessionTimerWindows[i] == 'object' && sessionTimerWindows[i].name == windowRef.name) {
                bWindowExistFlag = true;
                break;
            }
        }
        
        if(!bWindowExistFlag) {
            sessionTimerWindows[sessionTimerWindows.length] = windowRef;
        }
    }

    if (sessionExpireWarnVisible) {
        var remainingtime = getOAPTimerInSeconds();
        if (typeof windowRef == 'object') {
            try {
                windowRef.showSessionTimer(remainingtime - 1);
            } catch (e) {
                sessionTimerWindows.splice(sessionTimerWindows.length - 1, 1);
            }
        }
    }
}

function notifyOAPSessionTimer(remainingtime) {
    for (var i = 0; i < sessionTimerWindows.length; i++) {
        if (typeof sessionTimerWindows[i] == 'object') {
            if(sessionTimerWindows[i].closed) {
                sessionTimerWindows.splice(i--, 1);
            } else {
                try {
                    sessionTimerWindows[i].showSessionTimer(remainingtime);
                } catch (e) {
                    sessionTimerWindows.splice(i--, 1);
                }
            }
        }
    }
}

function UpdateAppsessionTime(time) {
    time = (time - 0);
    
    var appURL = "../../GetSessionTimeout?time=" + time + "&Action=update";
   
    new net.ContentLoader(appURL, UpdateAppSessionTimeCallBack, UpdateAppSessionTimeError, "POST", "", true);
    sessionTimeReqSent++;

    function UpdateAppSessionTimeCallBack() {
        sessionTimeReqRecv++;
        var returnxml = this.req.responseText;
        //var strResponseAppName = getContextPath(this.req.responseAppName);
        
        if(((returnxml-0) <= 0 || isNaN(returnxml))) {
            invalidateSessionTime();
        } else {
            if(returnxml.length>0 && !isNaN(returnxml)){
                if((returnxml-0)>0){
                    var appSessionTime = (returnxml-0);
                    if(appSessionTime > EFF_UPDATE_SESSION_TIME){
                        EFF_UPDATE_SESSION_TIME = appSessionTime;
                    }
                }            
            }
        
            if (sessionTimeReqSent == sessionTimeReqRecv) {
                if(EFF_UPDATE_SESSION_TIME == 0) {
                    invalidateSessionTime();
                }
            }
        }
    }

    function UpdateAppSessionTimeError() {
        sessionTimeReqRecv++;
        var returnxml = this.req.responseText;
        //var strResponseAppName = getContextPath(this.req.responseAppName);
        
        if(((returnxml-0) <= 0 || isNaN(returnxml))) {
            invalidateSessionTime();
        } else {
            if (sessionTimeReqSent == sessionTimeReqRecv) {
                if(EFF_UPDATE_SESSION_TIME == 0) {
                    invalidateSessionTime();
                }
            }
        }
    }
}

function extendTimeYesClick()
{
    var time = (SessionWarnExtendTime-0)*60;
    hideWebSessionTimer();
    setSessionCheckTimeout(time);
  //  oapExtendSessionPostHook(time);
}

function hideWebSessionTimer() {
    for (var i = 0; i < sessionTimerWindows.length; i++) {
        if (typeof sessionTimerWindows[i] == 'object') {
            if(sessionTimerWindows[i].closed) {
                sessionTimerWindows.splice(i--, 1);
            } else {
                try {
                    sessionTimerWindows[i].hideSessionTimer();
                } catch (e) {
                    sessionTimerWindows.splice(i--, 1);
                }
            }
        }
    }
}

