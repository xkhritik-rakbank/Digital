/*
---------------------------------------------------------------------------------------------------------
                  NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group                   : Application - Projects
Project/Product			: STP (V1.0) 
Application				: STP Hold
Module					: STP Hold Case Processing  
File Name				: XMLGen.java
Author 					: Ajay Kumar
Date (DD/MM/YYYY)		: 29/06/2009
Description 			: Contains various methods, each return a String containg the INput XML for calls.
---------------------------------------------------------------------------------------------------------
                 	CHANGE HISTORY
---------------------------------------------------------------------------------------------------------

Problem No/CR No        Change Date           Changed By             Change Description
---------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------
*/


package com.newgen.generate;

import java.util.ArrayList;

import com.newgen.wfdesktop.xmlapi.WFInputXml;

/**
 * <p>Title: STP</p>
 * <p>Description: Contains various methods, each return a String containg the INput XML for calls.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Newgen Software Technologies Ltd.</p>
 * @author Garima Agarwal
 * @version 1.0
 */
public class XMLGen {
	
	
	
	public XMLGen() {
	}
	
	/**
	 * Method Discription
	 * @param strEngineName
	 * @param strSessionId
	 * @param strProcessInstanceId
	 * @param strWorkItemId
	 * @return : String
	 */
	public static String WMGetWorkItem(String strEngineName,
			String strSessionId,
			String strProcessInstanceId,
			String strWorkItemId) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("WMGetWorkItem", "Input");
		wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
		wfInputXml.appendTagAndValue("ProcessInstanceId", strProcessInstanceId);
		wfInputXml.appendTagAndValue("WorkItemId", strWorkItemId);
		wfInputXml.appendEndCallName("WMGetWorkItem", "Input");
		return wfInputXml.toString();
	}
	
	/**
	 * @param strEngineName
	 * @param strSessionId
	 * @param strTableName
	 * @param strColName
	 * @param strValues
	 * @param strUserId
	 * @return
	 */
	public static String APInsert(String strEngineName,
			String strSessionId, 
			String strTableName,
			String strColName,
			String strValues,
			String strUserId) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("APInsert", "Input");
		wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
		wfInputXml.appendTagAndValue("TableName", strTableName);
		wfInputXml.appendTagAndValue("ColName", strColName);
		wfInputXml.appendTagAndValue("Values", strValues);
		wfInputXml.appendTagAndValue("UserIndex", strUserId);
		wfInputXml.appendEndCallName("APInsert", "Input");
		return wfInputXml.toString();
	}
	
	
	/**
	 * @param strEngineName
	 * @param strSessionId
	 * @param strProcessInstanceId
	 * @return
	 */
	public static String WMStartProcess(String strEngineName,
			String strSessionId,
			String strProcessInstanceId) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("WMStartProcess", "Input");
		wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
		wfInputXml.appendTagAndValue("ProcessInstanceID", strProcessInstanceId);
		wfInputXml.appendEndCallName("WMStartProcess", "Input");
		return wfInputXml.toString();
	}
	
	
	/**
	 * @param cabinetName
	 * @param sessionID
	 * @return
	 */
	public static String get_WMFetchProcessDefinitions_Input(String pstrCabinetName, String pstrSessionID) {
		return "<?xml version=\"1.0\"?>\n"
				+ "<WMFetchProcessDefinitions_Input>\n"
				+ "<Option>WMFetchProcessDefinitions</Option>\n"
				+ "<EngineName>" + pstrCabinetName  + "</EngineName>\n"
				+ "<SessionID>" + pstrSessionID  + "</SessionID>\n"
				+ "<CountFlag>Y</CountFlag>\n"
				+ "<BatchInfo>\n"
				+ "<NoOfRecordsToFetch></NoOfRecordsToFetch>\n"
				+ "<LastValue>0</LastValue>\n"
				+ "</BatchInfo>\n"
				+ "</WMFetchProcessDefinitions_Input>";
	}
	
	/**
	 * @param strEngineName
	 * @param strSessionId
	 * @param strDataDefIndex
	 * @return
	 */
	public static String get_NGOGetDataDefProperty(String strEngineName, String strSessionId, String strDataDefIndex) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("NGOGetDataDefProperty", "Input");
		wfInputXml.appendTagAndValue("CabinetName", strEngineName);
		wfInputXml.appendTagAndValue("UserDBId", strSessionId);
		wfInputXml.appendTagAndValue("DataDefIndex", strDataDefIndex);
		wfInputXml.appendEndCallName("NGOGetDataDefProperty", "Input");
		return wfInputXml.toString();
	}
	
	
	/**
	 * @param strEngineName
	 * @param strUserDBId
	 * @param strDataDefName
	 * @return
	 */
	public static String get_NGOGetDataDefIdForName(String strEngineName, String strUserDBId, String strDataDefName) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("NGOGetDataDefIdForName", "Input");
		wfInputXml.appendTagAndValue("CabinetName", strEngineName);
		wfInputXml.appendTagAndValue("UserDBId", strUserDBId);
		wfInputXml.appendTagAndValue("DataDefName", strDataDefName);
		wfInputXml.appendEndCallName("NGOGetDataDefIdForName", "Input");
		return wfInputXml.toString();
	}
	
	/**
	 * @param strEngineName
	 * @param strSessionId
	 * @param strProcessId
	 * @return
	 */
	public static String WMCreateProcessInstance(String strEngineName,
			String strSessionId,
			String strProcessId) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("WMCreateProcessInstance", "Input");
		wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
		wfInputXml.appendTagAndValue("ProcessDefinitionID", strProcessId);
		wfInputXml.appendEndCallName("WMCreateProcessInstance", "Input");
		return wfInputXml.toString();
	}
	
	/**
	 * @param strEngineName
	 * @param strSessionId
	 * @param strProcessInstanceId
	 * @return
	 */
	public static String WMTerminateProcessInstance(String strEngineName,
			String strSessionId,
			String strProcessInstanceId) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("WMTerminateProcessInstance", "Input");
		wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
		wfInputXml.appendTagAndValue("ProcessInstanceID", strProcessInstanceId);
		wfInputXml.appendEndCallName("WMTerminateProcessInstance", "Input");
		return wfInputXml.toString();
	}
	
	
	/**
	 * @param strEngineName
	 * @param strUserDBId
	 * @param strFolderName
	 * @param strParentFolderIndex
	 * @return
	 */
	public static String get_NGOAddFolder(String strEngineName,
			String strUserDBId,
			String strFolderName,
			String strParentFolderIndex) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("NGOAddFolder", "Input");
		wfInputXml.appendTagAndValue("CabinetName", strEngineName);
		wfInputXml.appendTagAndValue("UserDBId", strUserDBId);
		wfInputXml.appendTagAndValue("FolderName", strFolderName);
		wfInputXml.appendTagAndValue("ParentFolderIndex", strParentFolderIndex);
		wfInputXml.appendEndCallName("NGOAddFolder", "Input");
		return wfInputXml.toString();
	}
	
	/**
	 * @param strEngineName
	 * @param strSessionId
	 * @param strProcessInstanceId
	 * @param strWorkItemId
	 * @param strAttributeData
	 * @return
	 */
	public static String WFSetAttributes(String strEngineName,
			String strSessionId,
			String strProcessInstanceId,
			String strWorkItemId,
			String strAttributeData) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("WMAssignWorkItemAttributes", "Input");
		wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
		wfInputXml.appendTagAndValue("ProcessInstanceId", strProcessInstanceId);
		wfInputXml.appendTagAndValue("WorkItemId", strWorkItemId);
		wfInputXml.appendTagAndValue("Attributes", strAttributeData);
		wfInputXml.appendEndCallName("WMAssignWorkItemAttributes", "Input");
		return wfInputXml.toString();
	}
	
	
	/**
	 * @param strEngineName
	 * @param strSessionId
	 * @param strProcessInstanceId
	 * @param strWorkItemId
	 * @param strAuditStatus
	 * @return
	 */
	public static String WMCompleteWorkItem(String strEngineName,
			String strSessionId,
			String strProcessInstanceId,
			String strWorkItemId,
			String strAuditStatus) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("WMCompleteWorkItem", "Input");
		wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
		wfInputXml.appendTagAndValue("ProcessInstanceId", strProcessInstanceId);
		wfInputXml.appendTagAndValue("WorkItemId", strWorkItemId);
		wfInputXml.appendTagAndValue("AuditStatus", strAuditStatus);
		wfInputXml.appendEndCallName("WMCompleteWorkItem", "Input");
		return wfInputXml.toString();
	}
	
	
	/**
	 * @param strEngineName
	 * @param strSessionId
	 * @param strProcessInstanceId
	 * @param strWorkItemId
	 * @return
	 */
	public static String WMUnlockWorkItem(String strEngineName,
			String strSessionId,
			String strProcessInstanceId,
			String strWorkItemId) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("WMUnlockWorkItem", "Input");
		wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
		wfInputXml.appendTagAndValue("ProcessInstanceID", strProcessInstanceId);
		wfInputXml.appendTagAndValue("WorkItemID", strWorkItemId);
		wfInputXml.appendEndCallName("WMUnlockWorkItem", "Input");
		return wfInputXml.toString();
	}
	
	/**
	 * @param pstrEngineName
	 * @param pstrSessionId
	 * @param pstrQuery
	 * @return
	 */
	public static String APSelect(String pstrEngineName,
			String pstrSessionId, 
			String pstrQuery) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("APSelect", "Input");
		wfInputXml.appendTagAndValue("EngineName", pstrEngineName);
		wfInputXml.appendTagAndValue("SessionId", pstrSessionId);
		wfInputXml.appendTagAndValue("Query", pstrQuery);
		wfInputXml.appendEndCallName("APSelect", "Input");
		return wfInputXml.toString();
	}
	/**
	 * @param pstrEngineName
	 * @param pstrSessionId
	 * @param pstrTableName
	 * @param pstrColNames
	 * @param pstrValues
	 * @param pstrWhereClause
	 * @return
	 */
	public static String APUpdate(String pstrEngineName,
			String pstrSessionId, 
			String pstrTableName,
			String pstrColNames,
			String pstrValues,
			String pstrWhereClause) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("APUpdate", "Input");
		wfInputXml.appendTagAndValue("EngineName", pstrEngineName);
		wfInputXml.appendTagAndValue("SessionId", pstrSessionId);
		wfInputXml.appendTagAndValue("TableName", pstrTableName);
		wfInputXml.appendTagAndValue("ColName", pstrColNames);
		wfInputXml.appendTagAndValue("Values", pstrValues);
		wfInputXml.appendTagAndValue("WhereClause", pstrWhereClause);
		wfInputXml.appendEndCallName("APUpdate", "Input");
		return wfInputXml.toString();
	}
	
	
	/**
	 * @param pstrCabinetName
	 * @param pstrSessionId
	 * @param pstrTableName
	 * @param pstrWhereClause
	 * @return
	 */
	public static String getAPDeleteInputXML(String pstrCabinetName,
			String pstrSessionId, 
			String pstrTableName,
			String pstrWhereClause) {
		return
		"<?xml version=\"1.0\"?>\n"
			+ "<WMTestSelect_Input>\n"
			+ "<Option>APDelete</Option>\n"
			+ "<TableName>" + pstrTableName + "</TableName>\n"
			+ "<WhereClause>" + pstrWhereClause + "</WhereClause>\n"
			+ "<EngineName>" + pstrCabinetName + "</EngineName>\n"
			+ "<SessionId>" + pstrSessionId + "</SessionId>\n"
			+ "</WMTestSelect_Input>";
	}
	
	/**
	 * @param pstrCabinetName
	 * @param pstrSessionId
	 * @param pstrProcedureName
	 * @param pstrParams
	 * @return
	 */
	public static String getAPProcedureInputXML(String pstrCabinetName,
			String pstrSessionId, 
			String pstrProcedureName,
			String pstrParams) {
		return
			"<?xml version=\"1.0\"?>\n"
			+ "<APProcedure_Input>\n"
			+ "<Option>APProcedure</Option>\n"
			+ "<SessionId>" + pstrSessionId + "</SessionId>\n"
			+ "<ProcName>" + pstrProcedureName + "</ProcName>\n"
			+ "<Params>" + pstrParams + "</Params>\n"
			+ "<EngineName>" + pstrCabinetName + "</EngineName>\n"
			+ "</APProcedure_Input>";
	}
	
	/**
	 * @param pstrCabinetName
	 * @param pstrUserName
	 * @param pstrPassword
	 * @param pstrForceful
	 * @return
	 */
	public static String get_WMConnect_Input(String pstrCabinetName,
			String pstrUserName, 
			String pstrPassword,
			String pstrForceful) {
		return
		"<?xml version=\"1.0\"?>\n"
		+ "<WMConnect_Input>\n"
		+ "<Option>WMConnect</Option>\n"
		+ "<UserExist>" + pstrForceful  + "</UserExist>\n"
		+ "<EngineName>" + pstrCabinetName  + "</EngineName>\n"
		+ "<Particpant>\n"
		+ "<Name>" + pstrUserName  + "</Name>\n"
		+ "<Password>" + pstrPassword  + "</Password>\n"
		+ "<Scope>USER</Scope>\n"
		+ "<ParticipantType>U</ParticipantType>\n"
		+ "</Participant>\n"
		+ "</WMConnect_Input>";
	}
	
	/**
	 * @param pstrCabinetName
	 * @param pstrUserName
	 * @param pstrPassword
	 * @param pstrForceful
	 * @return
	 */
	public static String get_NGOConnectCabinet_Input(String pstrCabinetName,
			String pstrUserName, 
			String pstrPassword,
			String pstrForceful) {
		return "<?xml version=\"1.0\"?>\n"
		+ "<NGOConnectCabinet_Input>\n"
		+ "<Option>NGOConnectCabinet</Option>\n"
		+ "<UserExist>" + pstrForceful  + "</UserExist>\n"
		+ "<CabinetName>" + pstrCabinetName  + "</CabinetName>\n"
		+ "<UserName>" + pstrUserName  + "</UserName>\n"
		+ "<UserPassword>" + pstrPassword  + "</UserPassword>\n"
		+ "</NGOConnectCabinet_Input>";
	}
	
	/**
	 * @param pstrCabinetName
	 * @param pstrSessionID
	 * @return
	 */
	public static String get_WMDisConnect_Input(String pstrCabinetName, String pstrSessionID) {
		return "<?xml version=\"1.0\"?>\n"
		+ "<WMDisConnect_Input>\n"
		+ "<Option>WMDisConnect</Option>\n"
		+ "<EngineName>" + pstrCabinetName  + "</EngineName>\n"
		+ "<SessionID>" + pstrSessionID  + "</SessionID>\n"
		+ "</WMDisConnect_Input>";
	}
	
	
	/**
	 * @return
	 */
	public static String get_NGOGetListOfCabinets_Input() {
		return "<?xml version=\"1.0\"?>\n"
		+ "<NGOGetListOfCabinets_Input>\n"
		+ "<Option>NGOGetListOfCabinets</Option>\n"
		+ "</NGOGetListOfCabinets_Input>";
	}
	
	/**
	 * @param pstrCabinetName
	 * @return
	 */
	public static String get_NGOISGetSitesList_Input(String pstrCabinetName) {
		return "<?xml version=\"1.0\"?>\n"
		+ "<NGOISGetSitesList_Input>\n"
		+ "<Option>NGOISGetSitesList</Option>\n"
		+ "<CabinetName>" + pstrCabinetName  + "</CabinetName>\n"
		+ "</NGOISGetSitesList_Input>";
	}
	
	/**
	 * @param pstrCabinetName
	 * @return
	 */
	public static String get_NGOISGetVolumesList_Input(String pstrCabinetName) {
		return "<?xml version=\"1.0\"?>\n"
		+ "<NGOISGetVolumesList_Input>\n"
		+ "<Option>NGOISGetVolumesList</Option>\n"
		+ "<CabinetName>" + pstrCabinetName  + "</CabinetName>\n"
		+ "</NGOISGetVolumesList_Input>";
	}
	
	
	/**
	 * @param pstrCabinetName
	 * @param pstrName
	 * @return
	 */
	public static String get_WMConnectCS_Input(String pstrCabinetName, String pstrName) {
		return "<?xml version=\"1.0\"?>\n"
		+ "<WMConnect_Input>\n"
		+ "<Option>WMConnect</Option>\n"
		+ "<EngineName>" + pstrCabinetName  + "</EngineName>\n"
		+ "<Name>" + pstrName + "</Name>\n"
		+ "<ParticipantType>C</ParticipantType>\n"
		+ "</WMConnect_Input>";
	}
	

	/**
	 * @param pstrCabinetName
	 * @param pstrSessionID
	 * @return
	 */
	public static String get_WMGetProcessList_Input(String pstrCabinetName, String pstrSessionID) {
        return "<?xml version=\"1.0\"?>" +
        		"<WMGetProcessList_Input>\n" +
        		"<Option>WMGetProcessList</Option>\n" +
        		"<EngineName>" + pstrCabinetName + "</EngineName>\n" +
        		"<SessionID>" + pstrSessionID + "</SessionID>\n" +
        		"<DataFlag>N</DataFlag>\n" +
        		"<LatestVersionFlag>N</LatestVersionFlag>\n" +
        		"<OrderBy>2</OrderBy>\n<BatchInfo>" +
        		"<NoOfRecordsToFetch>9999</NoOfRecordsToFetch>" +
        		"</BatchInfo>\n<" +
        		"/WMGetProcessList_Input>";
    }
	
	/**
	 * @param pstrCabinetName
	 * @param pstrSessionID
	 * @param pstrProcessDefID
	 * @return
	 */
	public static String get_WMGetActivityList_Input(String pstrCabinetName,
			String pstrSessionID, 
			String pstrProcessDefID) {
		return "<?xml version=\"1.0\"?>"
		+ "<WMGetActivityList_Input>\n"
		+ "<Option>WMGetActivityList</Option>\n"
		+ "<EngineName>" + pstrCabinetName  + "</EngineName>\n"
		+ "<SessionId>" + pstrSessionID  + "</SessionId>\n"
		+ "<ProcessDefinitionID>" + pstrProcessDefID  + "</ProcessDefinitionID>\n"
		+ "<BatchInfo>\n"
		+ "<SortOrder>A</SortOrder>\n"
		+ "<OrderBy>2</OrderBy>\n"
		+ "</BatchInfo>\n"
		+ "</WMGetActivityList_Input>";
	}
	
	/**
	 * @param pstrCabinetName
	 * @param pstrSessionID
	 * @param pstrProcessDefID
	 * @param pstrProcessName
	 * @param pstrActivityID
	 * @param pstrActivityType
	 * @return
	 */
	public static String get_WFGetProcessVariables_Input(String pstrCabinetName,
			String pstrSessionID,
			String pstrProcessDefID,
			String pstrProcessName,
			String pstrActivityID,
			String pstrActivityType) {
		return "<?xml version=\"1.0\"?>\n"
		+ "<WFGetProcessVariables_Input>\n"
		+ "<Option>WFGetProcessVariables</Option>\n"
		+ "<EngineName>" + pstrCabinetName  + "</EngineName>\n"
		+ "<SessionID>" + pstrSessionID  + "</SessionID>\n"
		+ "<ProcessDefinitionId>" + pstrProcessDefID  + "</ProcessDefinitionId>\n"
		+ "<ProcessName>" + pstrProcessName  + "</ProcessName>\n"
		+ "<ActivityId>" + pstrActivityID  + "</ActivityId>\n"
		+ "<ActivityType>" + pstrActivityType  + "</ActivityType>\n"
		+ "</WFGetProcessVariables_Input>";
		
	}
	
	/**
	 * @param pstrCabinetName
	 * @param pstrSessionID
	 * @param pstrProcessDefID
	 * @param pstrActivityID
	 * @param pstrUserID
	 * @return
	 */
	public static String get_WFGetUserQueueDetails_Input(String pstrCabinetName,
			String pstrSessionID,
			String pstrProcessDefID,
			String pstrActivityID,
			String pstrUserID) {
		return "<?xml version=\"1.0\"?>\n"
		+ "<WFGetUserQueueDetails_Input>\n"
		+ "<Option>WFGetUserQueueDetails</Option>\n"
		+ "<EngineName>" + pstrCabinetName  + "</EngineName>\n"
		+ "<SessionId>" + pstrSessionID  + "</SessionId>\n"
		+ "<UserId>" + pstrUserID  + "</UserId>\n"
		+ "<ProcessDefinitionId>" + pstrProcessDefID  + "</ProcessDefinitionId>\n"
		+ "<ActivityId>" + pstrActivityID  + "</ActivityId>\n"
		+ "<DataFlag>Y</DataFlag>\n"
		+ "</WFGetUserQueueDetails_Input>";
	}
	
	/**
	 * @param pstrCabinetName
	 * @param pstrSessionID
	 * @param pstrProcessDefID
	 * @param pstrObjectType
	 * @param pstrObjectName
	 * @return
	 */
	public static String get_WFGetIdforName_Input(String pstrCabinetName,
			String pstrSessionID,
			String pstrProcessDefID,
			String pstrObjectType,
			String pstrObjectName) {
		return "<?xml version=\"1.0\"?>\n"
		+ "<WFGetIdforName_Input>\n"
		+ "<Option>WFGetIdforName</Option>\n"
		+ "<EngineName>" + pstrCabinetName  + "</EngineName>\n"
		+ "<SessionId>" + pstrSessionID + "</SessionId>\n"
		+ "<ObjectType>" + pstrObjectType + "</ObjectType>\n"
		+ "<ObjectName>" + pstrObjectName + "</ObjectName>\n"
		+ "<ProcessDefID>" + pstrProcessDefID + "</ProcessDefID>\n"
		+ "</WFGetIdforName_Input>";
		
	}
	
	
//	added methods  
//	********************************************************************************************************************
	
	/**
	 * @param pstrCabinetName
	 * @param pstrUserDBId
	 * @param pstrMainGroupIndex
	 * @param pstrGroupName
	 * @param pstrCreationDateTime
	 * @param pstrExpiryDateTime
	 * @param pstrPrivileges
	 * @param pstrComment
	 * @param pstrGroupType
	 * @return
	 */
	public static String AddGroupXml(String pstrCabinetName,
			String pstrUserDBId,
			String pstrMainGroupIndex,
			String pstrGroupName,
			String pstrCreationDateTime,
			String pstrExpiryDateTime,
			String pstrPrivileges,
			String pstrComment,
			String pstrGroupType)
	{
		String str_inxml=
			"<?xml version=\"1.0\"?>\n"
			+ "<NGOAddGroup_Input>\n"
			+ "<Option>NGOAddGroup</Option>\n"
			+ "<CabinetName>" + pstrCabinetName + "</CabinetName>\n"
			+ "<UserDBId>" + pstrUserDBId + "</UserDBId>\n"
			+ "<Group>\n"
			+ "<MainGroupIndex>0</MainGroupIndex>\n"
			+ "<GroupName>" + pstrGroupName + "</GroupName>\n"
			+ "<CreationDateTime>" + pstrCreationDateTime + "</CreationDateTime>\n"
			+ "<ExpiryDateTime>" + pstrExpiryDateTime + "</ExpiryDateTime>\n"
			+ "<Privileges>" + pstrPrivileges + "</Privileges>\n"
			+ "<Comment>" + pstrComment + "</Comment>\n"
			+ "<GroupType>" + pstrGroupType + "</GroupType>\n"
			+ "</Group>\n"
			+ "</NGOAddGroup_Input>" ;
		return str_inxml;
	}
	
	//Queue Creation
	/**
	 * @param pstrEngine
	 * @param pstrSessionId
	 * @param pstrQueuename
	 * @param pstrQueueType
	 * @return
	 */
	public static String AddQueueXml(String pstrEngine,
			String pstrSessionId,
			String pstrQueuename,
			String pstrQueueType
	)
	{
		String str_inxml = "<?xml version=\"1.0\"?>\n"
			
			+ "<WMAddQueue_Input>\n"
			+ "<Option>WMAddQueue</Option>\n"
			+ "<EngineName>" + pstrEngine + "</EngineName>\n"
			+ "<SessionID>" + pstrSessionId + "</SessionID>\n"
			+ "<QueueName>" + pstrQueuename + "</QueueName>\n"
			+ "<Description></Description>\n"
			+ "<QueueType>" + pstrQueueType + "</QueueType>\n"
			+ "<AllowReassignment></AllowReassignment>\n"
			+ "<OrderBy></OrderBy>\n"
			+ "<FilterValue></FilterValue>\n"
			+ "<FilterOption></FilterOption>\n"
			+ "<StreamList></StreamList>"
			+ "</WMAddQueue_Input>";
		return str_inxml;
		
	}
	
	/**
	 * ArrayList contain Group-Queue association information. 
	 * each element is of String type contains 2 differnt values separated by '~' with following sequence:
	 *	GroupId~GroupName
	 *	@author: Garima Agarwal
	 * @param pstrEngine
	 * @param pstrSessionId
	 * @param pstrQueueId
	 * @param pstrQueueName
	 * @param pobjArrayList
	 * @return
	 * @throws Exception
	 */
	public static String AssignGroupToQueueXml( String pstrEngine,
			String pstrSessionId,
			String pstrQueueId,
			String pstrQueueName,
			ArrayList pobjArrayList) throws Exception
	{
		StringBuffer str_inxml = new StringBuffer("");
		try{
			str_inxml.append("<?xml version=\"1.0\"?>\n"
					+ "<WMChangeQueuePropertyEx_Input>\n"
					+ "<Option>WMChangeQueuePropertyEx</Option>\n"
					+ "<EngineName>" + pstrEngine + "</EngineName>\n"
					+ "<SessionId>" + pstrSessionId + "</SessionId>\n"
					+ "<QueueId>" + pstrQueueId + "</QueueId>\n"
					+ "<FilterOption>1</FilterOption>\n"
					+ "<FilterValue></FilterValue>\n"
					+ "<GroupOperation>\n"
					+ "<GroupList>\n");
			for(int i=0; i<pobjArrayList.size(); i++){
				String lstrGroupInfo = (String)pobjArrayList.get(i);
				String[] lstrLstGroupInfo = lstrGroupInfo.split("~");
				str_inxml.append("<GroupInfo>\n"
						+ "<ID>" + lstrLstGroupInfo[0] + "</ID>\n"
						+ "<GroupName>" + lstrLstGroupInfo[1] + "</GroupName>\n"
						+ "<Operation>I</Operation>\n"
						+ "</GroupInfo>\n");
				
			}	
			str_inxml.append("</GroupList></GroupOperation>\n"
					+ "<AllowReassignment>N</AllowReassignment>\n"
					+ "</WMChangeQueuePropertyEx_Input>");
		}catch(Exception lExcp){
			throw(lExcp);
		}
		return str_inxml.toString();
	}
	
	
	/**
	 * @param pstrEngine
	 * @param pstrSessionId
	 * @param pstrQueueId
	 * @param pstrQueueName
	 * @param pstrQueueType
	 * @param pstrGroupName
	 * @param pstrGroupId
	 * @return
	 */
	public static String AssignGroupToQueueXml(  String pstrEngine,
			String pstrSessionId,
			String pstrQueueId,
			String pstrQueueName,
			String pstrQueueType,
			String pstrGroupName,
			String pstrGroupId) 
	{
		String str_inxml = "<?xml version=\"1.0\"?>\n"
			+ "<WMChangeQueuePropertyEx_Input>\n"
			+ "<Option>WMChangeQueuePropertyEx</Option>\n"
			+ "<EngineName>" + pstrEngine + "</EngineName>\n"
			+ "<SessionId>" + pstrSessionId + "</SessionId>\n"
			+ "<QueueId>" + pstrQueueId + "</QueueId>\n"
			+ "<FilterOption>1</FilterOption>\n"
			+ "<FilterValue></FilterValue>\n"
			+ "<QueueDetail>\n"
			+ "<Description>" + pstrQueueName + "</Description>\n"
			+ "<QueueType>" + pstrQueueType + "</QueueType>\n"
			+ "</QueueDetail>\n"
			+ "<GroupOperation>"
			+ "<GroupList>\n"
			+ "<GroupInfo>\n"
			+ "<ID>" + pstrGroupId + "</ID>\n"
			+ "<GroupName>" + pstrGroupName + "</GroupName>\n"
			+ "<Operation>I</Operation>\n"
			+ "</GroupInfo>\n"
			+ "</GroupList>\n"
			+ "</GroupOperation>\n"
			+ "<AllowReassignment>N</AllowReassignment>\n"
			+ "</WMChangeQueuePropertyEx_Input>";
		return str_inxml;
	}
	
	
	/**
	 * @param pstrEngine
	 * @param pstrSessionId
	 * @param pstrQueueId
	 * @param pobjArrayList
	 * @return
	 * @throws Exception
	 */
	public static String AssignUserToQueueXml(  String pstrEngine,
			String pstrSessionId,
			String pstrQueueId,
			ArrayList pobjArrayList
	) throws Exception
	{
		String lExceptionId = new String("com.newgen.srvr.xml.XMLGen.AssignUserToQueueXml");
		StringBuffer str_inxml = new StringBuffer("");
		try{
			str_inxml.append("<?xml version=\"1.0\"?>\n"
					+ "<WMChangeQueuePropertyEx_Input>\n"
					+ "<Option>WMChangeQueuePropertyEx</Option>\n"
					+ "<EngineName>" + pstrEngine + "</EngineName>\n"
					+ "<SessionId>" + pstrSessionId + "</SessionId>\n"
					+ "<QueueId>" + pstrQueueId + "</QueueId>\n"
					+ "<FilterOption>1</FilterOption>\n"
					+ "<FilterValue></FilterValue>\n"
					+ "<UserOperation>\n"
					+ "<UserList>\n");
			for(int i=0; i<pobjArrayList.size(); i++){
				String lstrUserInfo = (String)pobjArrayList.get(i);
				String[] lstrLstUserInfo = lstrUserInfo.split("~");
				str_inxml.append("<UserInfo>\n"
						+ "<ID>" + lstrLstUserInfo[0] + "</ID>\n"
						+ "<UserName>" + lstrLstUserInfo[1] + "</UserName>\n"
						+ "<Operation>I</Operation>\n"
						+ "</UserInfo>\n");
				
			}	
			str_inxml.append("</UserList></UserOperation>\n"
					+ "<AllowReassignment>N</AllowReassignment>\n"
					+ "</WMChangeQueuePropertyEx_Input>");
		}catch(Exception lExcp){
			
			throw(lExcp);
		}
		return str_inxml.toString();
	}
	
	
	/**
	 * @param pstrEngine
	 * @param pstrSessionId
	 * @param pstrQueueId
	 * @param pobjArrayList : ArrayList, contains User-Queue association information. 
	 * 			each element is of String type contains 3 differnt values separated by '~' with following sequence:
	 * 			UserIndex~UserId~QueryFilter
	 * @return
	 * @throws Exception
	 */
	public static String AssignUserToQueueXmlWithFilter(  String pstrEngine,
			String pstrSessionId,
			String pstrQueueId,
			ArrayList pobjArrayList
	) throws Exception
	{
		String lExceptionId = new String("com.newgen.srvr.xml.XMLGen.AssignUserToQueueXml");
		StringBuffer str_inxml = new StringBuffer("");
		try{
			str_inxml.append("<?xml version=\"1.0\"?>\n"
					+ "<WMChangeQueuePropertyEx_Input>\n"
					+ "<Option>WMChangeQueuePropertyEx</Option>\n"
					+ "<EngineName>" + pstrEngine + "</EngineName>\n"
					+ "<SessionId>" + pstrSessionId + "</SessionId>\n"
					+ "<QueueId>" + pstrQueueId + "</QueueId>\n"
					+ "<FilterOption>1</FilterOption>\n"
					+ "<FilterValue></FilterValue>\n"
					+ "<UserOperation>\n"
					+ "<UserList>\n");
			for(int i=0; i<pobjArrayList.size(); i++){
				String lstrUserInfo = (String)pobjArrayList.get(i);
				String[] lstrLstUserInfo = lstrUserInfo.split("~");
				str_inxml.append("<UserInfo>\n"
						+ "<ID>" + lstrLstUserInfo[0] + "</ID>\n"
						+ "<UserName>" + lstrLstUserInfo[1] + "</UserName>\n"
						+ "<Operation>I</Operation>\n"
						+ "<QueryFilter>" + lstrLstUserInfo[2] + "</QueryFilter>\n"
						+ "</UserInfo>\n");
				
			}	
			str_inxml.append("</UserList></UserOperation>\n"
					+ "<AllowReassignment>N</AllowReassignment>\n"
					+ "</WMChangeQueuePropertyEx_Input>");
		}catch(Exception lExcp){
			
			throw(lExcp);
		}
		return str_inxml.toString();
	}
	
	
	
//	Change QueueProperty: Associating/deassociation of Workstep with queue
	/**
	 * @param str_engine
	 * @param sessionId
	 * @param queueId
	 * @param processDefinitionID
	 * @param queueType
	 * @param streamId
	 * @param streamOperation
	 * @param streamName
	 * @param ActivityId
	 * @return
	 */
	public static String AssignStreamToQueueXml( String str_engine,
			String sessionId,
			String queueId,
			String processDefinitionID,
			String queueType,
			String streamId,
			String streamOperation,
			String streamName,
			String ActivityId )
	{
		String str_inxml= "<?xml version=\"1.0\"?>\n"
			+ "<WMChangeQueuePropertyEx_Input>"
			+ "<Option>WMChangeQueuePropertyEx</Option>"
			+ "<EngineName>" + str_engine + "</EngineName>"
			+ "<SessionId>" + sessionId + "</SessionId>"
			+ "<QueueId>" + queueId + "</QueueId>"
			+ "<FilterOption>1</FilterOption>"
			+ "<FilterValue></FilterValue>"
			+ "<QueueDetail><Description></Description>"
			+ "<QueueType>" + queueType + "</QueueType>"
			+ "</QueueDetail>"
			+ "<StreamOperation>"
			+ "<StreamList>"
			+ "<StreamInfo>"
			+ "<ID>" + streamId + "</ID>"
			+ "<Operation>" + streamOperation + "</Operation>"
			+ "<StreamName>" + streamName + "</StreamName>"
			+ "<ProcessDefinitionID>" + processDefinitionID + "</ProcessDefinitionID>"
			+ "<Activityid>" + ActivityId + "</Activityid>"
			+ "</StreamInfo>"
			+ "</StreamList>"
			+ "</StreamOperation>"
			+ "<AllowReassignment>N</AllowReassignment>"
			+ "</WMChangeQueuePropertyEx_Input>";
		return str_inxml;
	}
	
	
//	Change QueueProperty: Associating/deassociation of Workstep with queue
	/*
	 *ArrayList contain Stream-Queue association information. each element is of String type contains 4 differnt values by '~' separated with following sequence:
	 *	StreamId~StreamOPeration(I/D)~StreamName~ActivityId
	 *	@author: Garima Agarwal
	 */
	/**
	 * @param pstrEngine
	 * @param pstrSessionId
	 * @param pstrQueueId
	 * @param pstrProcessDefinitionID
	 * @param pobjArrayList
	 * @return
	 * @throws Exception
	 */
	public static String AssignStreamToQueueXml( String pstrEngine,
			String pstrSessionId,
			String pstrQueueId,
			String pstrProcessDefinitionID,
			ArrayList pobjArrayList ) throws Exception
	{
		StringBuffer str_inxml= new StringBuffer("");
		try{
			str_inxml.append("<?xml version=\"1.0\"?>\n"
					+ "<WMChangeQueuePropertyEx_Input>\n"
					+ "<Option>WMChangeQueuePropertyEx</Option>\n"
					+ "<EngineName>" + pstrEngine + "</EngineName>\n"
					+ "<SessionId>" + pstrSessionId + "</SessionId>\n"
					+ "<QueueId>" + pstrQueueId + "</QueueId>\n"
					+ "<FilterOption>1</FilterOption>\n"
					+ "<FilterValue></FilterValue>\n"
					+ "<StreamOperation>\n"
					+ "<StreamList>\n");
			for(int i=0; i<pobjArrayList.size(); i++){
				String lstrStreamInfo = (String)pobjArrayList.get(i);
				String[] lstrLstStreamInfo = lstrStreamInfo.split("~");
				str_inxml.append("<StreamInfo>\n"
						+ "<ID>" + lstrLstStreamInfo[0] + "</ID>\n"
						+ "<Operation>" + lstrLstStreamInfo[1] + "</Operation>\n"
						+ "<StreamName>" + lstrLstStreamInfo[2] + "</StreamName>\n"
						+ "<ProcessDefinitionID>" + pstrProcessDefinitionID + "</ProcessDefinitionID>\n"
						+ "<Activityid>" + lstrLstStreamInfo[3] + "</Activityid>\n"
						+ "</StreamInfo>\n");
				
			}											
			str_inxml.append("</StreamList>\n"
					+ "</StreamOperation>\n"
					+ "<AllowReassignment>N</AllowReassignment>\n"
					+ "</WMChangeQueuePropertyEx_Input>\n");
		}
		catch(Exception lExcp){
			throw(lExcp);
		}
		return str_inxml.toString();
	}
	
	public static String GetStreamList_Input(String str_engine,
			String sessionId,
			String  processDefinitionID
	)
	{
		return  "<?xml version=\"1.0\"?>\n"
				+ "<WMGetStreamList_Input>\n"
				+ "<Option>WMGetStreamList</Option>\n"
				+ "<EngineName>" + str_engine + "</EngineName>\n"
				+ "<SessionID>" + sessionId + "</SessionID>\n"
				+ "<ProcessDefinitionID>" + processDefinitionID + "</ProcessDefinitionID>\n"							
				+ "<BatchInfo> <SortOrder>A</SortOrder><OrderBy>2</OrderBy>\n"
				+ "<NoOfRecordsToFetch>100</NoOfRecordsToFetch></BatchInfo>\n"
				+ "</WMGetStreamList_Input>" ;
		
	}
	
	/**
	 * @param pstrEngine
	 * @param pstrSessionId
	 * @param pstrProcessDefinitionID
	 * @return
	 */
	public static String GetQueueList_Input( String pstrEngine,
			String pstrSessionId,
			String pstrProcessDefinitionID
	)
	{
		return  "<?xml version=\"1.0\"?>\n"
				+ "<WMGetQueueList_Input>\n"
				+ "<Option>WMGetQueueList</Option>\n"
				+ "<EngineName>" + pstrEngine + "</EngineName>\n"
				+ "<SessionID>" + pstrSessionId + "</SessionID>\n"
				+ "<Filter>\n"
				+ "<ProcessDefinitionID>" + pstrProcessDefinitionID + "</ProcessDefinitionID>\n"
				+ "<QueueAssociation>0</QueueAssociation>\n"
				+ "<QueueType>S,D,F,I,N</QueueType>\n"
				+ "<QueuePrefix></QueuePrefix>\n"
				+ "</Filter>\n"
				+ "<BatchInfo><SortOrder>A</SortOrder><OrderBy>2</OrderBy>\n"
				+ "<NoOfRecordsToFetch>3000</NoOfRecordsToFetch></BatchInfo>\n"
				+ "<DataFlag>N</DataFlag>\n"
				+ "</WMGetQueueList_Input>";
		
	}
	
	/**
	 * WMGetQueueList_Input with filter option for 'Queue Prefix' and 'UserId'
	 * @param pstrEngine
	 * @param pstrSessionId
	 * @param pstrProcessDefinitionID
	 * @param pstrQueuePrefix
	 * @param pstrUserId
	 * @param pstrLastValue
	 * @param pstrLastIndex
	 * @return
	 */
	public static String GetQueueList_Input( String pstrEngine,
			String pstrSessionId,
			String pstrProcessDefinitionID,
			String pstrQueuePrefix,
			String pstrUserId,
			String pstrLastValue,
			String pstrLastIndex
	)
	{
		return  "<?xml version=\"1.0\"?>\n"
				+ "<WMGetQueueList_Input>\n"
				+ "<Option>WMGetQueueList</Option>\n"
				+ "<EngineName>" + pstrEngine + "</EngineName>\n"
				+ "<SessionID>" + pstrSessionId + "</SessionID>\n"
				+ "<Filter>\n"
				+ "<ProcessDefinitionID>" + pstrProcessDefinitionID + "</ProcessDefinitionID>\n"
				+ "<QueueAssociation>0</QueueAssociation>\n"
				+ "<QueueType>S,D,F,I,N</QueueType>\n"
				+ "<QueuePrefix>" + pstrQueuePrefix + "</QueuePrefix>\n"
				+ "<UserId>" + pstrUserId + "</UserId>"
				+ "</Filter>\n"
				+ "<BatchInfo>\n"
				+ "<SortOrder>A</SortOrder>\n"
				+ "<OrderBy>2</OrderBy>\n"
				+ "<LastValue>" + pstrLastValue + "</LastValue>\n"
				+ "<LastIndex>" + pstrLastIndex + "</LastIndex>\n"
				+ "<NoOfRecordsToFetch>200</NoOfRecordsToFetch>\n"
				+ "</BatchInfo>\n"
				+ "<DataFlag>Y</DataFlag>\n"
				+ "</WMGetQueueList_Input>";
		
	}
	
	/**
	 * @param pstrEngine
	 * @param pstrSessionId
	 * @param pstrProcessDefId
	 * @param pstrActivityId
	 * @param pstrQueueId
	 * @param pstrLastValue
	 * @param pstrLastIndex
	 * @return
	 */
	public static String getUserListExt_InputXML(String pstrEngine,
			String pstrSessionId,
			String pstrProcessDefId,
			String pstrActivityId,
			String pstrQueueId,
			String pstrLastValue,
			String pstrLastIndex){
		return "<?xml version=\"1.0\"?>\n"
				+ "<WMGetUserList_Input>\n"
				+ "<Option>WMGetUserList</Option>\n"
				+ "<EngineName>" + pstrEngine + "</EngineName>\n"
				+ "<SessionId>" + pstrSessionId + "</SessionId>\n"
				+ "<DataFlag>N</DataFlag>\n"
				+ "<Filter>\n"
				+ "<ProcessDefinitionID>" + pstrProcessDefId + "</ProcessDefinitionID>\n"
				+ "<ActivityId>" + pstrActivityId + "</ActivityId>\n"
				+ "<QueueId>" + pstrQueueId + "</QueueId>\n"
				+ "<UserStatus></UserStatus> \n"
				+ "<DateRange>\n"
				+ "<From></From>\n"
				+ "<To></To>\n"
				+ "</DateRange>\n"
				+ "</Filter>\n"
				+ "<BatchInfo> \n"
				+ "<SortOrder>D</SortOrder>\n"
				+ "<OrderBy>1</OrderBy>\n"
				+ "<LastValue>" + pstrLastValue + "</LastValue>\n"
				+ "<LastIndex>" + pstrLastIndex + "</LastIndex>\n"
				+ "<NoOfRecordsToFetch>200</NoOfRecordsToFetch>"
				+ "</BatchInfo>"
				+ "</WMGetUserList_Input>";
	}
	
	/**
	 * @param pstrEngine
	 * @param pstrSessionId
	 * @param pstrQueueId
	 * @return
	 */
	public static String getQueueProperty_Input(  String pstrEngine,
			String pstrSessionId,
			String pstrQueueId){
		
		return "<?xml version=\"1.0\"?>\n"
				+ "<WFGetQueueProperty_Input>\n"
				+ "<Option>WFGetQueueProperty</Option>\n"
				+ "<EngineName>" + pstrEngine + "</EngineName>\n"
				+ "<SessionId>" + pstrSessionId + "</SessionId>\n"
				+ "<QueueID>" + pstrQueueId + "</QueueID>\n"
				+ "<DataFlag>Y</DataFlag>\n"
				+ "</WFGetQueueProperty_Input>";
	}
	
	/**
	 * @param pstrCabinetName
	 * @param pstrSessionId
	 * @param pstrUserName
	 * @param pstrPassword
	 * @param pstrPersonalName
	 * @param pstrCreationDate
	 * @param pstrComment
	 * @param pstrExpiryDateTime
	 * @return
	 */
	public static String getNGOAddUser_Input(String pstrCabinetName,
			String pstrSessionId,
			String pstrUserName,
			String pstrPassword,
			String pstrPersonalName,
			String pstrCreationDate,
			String pstrComment,
			String pstrExpiryDateTime){
		return "<?xml version=\"1.0\"?>\n"
				+ "<NGOAddUser_Input>\n"
				+ "<Option>NGOAddUser</Option>\n"
				+ "<CabinetName>" + pstrCabinetName + "</CabinetName>\n"
				+ "<UserDBId>" + pstrSessionId + "</UserDBId>\n"
				+ "<User>\n"
				+ "<Name>" + pstrUserName + "</Name>\n"
				+ "<Password>" + pstrPassword + "</Password>\n"
				+ "<PersonalName>" + pstrPersonalName + "</PersonalName>\n"
				+ "<FamilyName></FamilyName>\n"
				+ "<CreationDateTime>" + pstrCreationDate + "</CreationDateTime>"
				+ "<Account>0</Account>\n"
				+ "<Comment>" + pstrComment + "</Comment>\n"
				+ "<ExpiryDateTime>" + pstrExpiryDateTime + "</ExpiryDateTime>\n"
				+ "<PasswordNeverExpire>Y</PasswordNeverExpire>\n"
				+ "</User>\n"
				+ "</NGOAddUser_Input";
	}
	
	/**
	 * @param pstrCabinetName
	 * @param pstrSessionId
	 * @param pstrPrevIndex
	 * @return
	 */
	public static String getGroupListExt_InputXML(String pstrCabinetName,
			String pstrSessionId,
			String pstrPrevIndex)
	{
		return "<?xml version=\"1.0\"?>\n"
				+ "<NGOGetGroupListExt_Input>\n"
				+ "<Option>NGOGetGroupListExt</Option>\n"
				+ "<CabinetName>" + pstrCabinetName + "</CabinetName>\n"
				+ "<UserDBId>" + pstrSessionId + "</UserDBId>\n"
				+ "<UserIndex></UserIndex>\n"
				+ "<OrderBy>2</OrderBy>\n"
				+ "<SortOrder>A</SortOrder>\n"
				+ "<PreviousIndex>" + pstrPrevIndex + "</PreviousIndex>\n"
				+ "<LastSortField></LastSortField>\n"
				+ "<NoOfRecordsToFetch>200</NoOfRecordsToFetch>"
				+ "<MainGroupIndex>0</MainGroupIndex>"
				+ "</NGOGetGroupListExt_Input>";
	}
	
	/*
	 *ArrayList contain User-Group association information.
	 * each element is of String type contains 2 differnt values separated by '~' with following sequence:
	 *	UserIndex~RoleIndex
	 *	@author: Garima Agarwal
	 */
	/**
	 * @param pstrEngine
	 * @param pstrSessionId
	 * @param pstrGroupIndex
	 * @param pobjArrayList
	 * @return
	 * @throws Exception
	 */
	public static String AssignUsersToGroupXml( String pstrEngine,
			String pstrSessionId,
			String pstrGroupIndex,
			ArrayList pobjArrayList) throws Exception
	{
		StringBuffer str_inXML = new StringBuffer("");
		try{
			str_inXML.append("<?xml version=\"1.0\"?>\n"
					+ "<NGOAddMemberToGroup_Input>\n"
					+ "<Option>NGOAddMemberToGroup</Option>\n"
					+ "<CabinetName>" + pstrEngine + "</CabinetName>\n"
					+ "<UserDBId>" + pstrSessionId + "</UserDBId>\n"
					+ "<GroupIndex>" + pstrGroupIndex + "</GroupIndex> \n"
					+ "<Users>\n");
			for(int i=0; i<pobjArrayList.size(); i++){
				String lstrUserInfo = (String)pobjArrayList.get(i);
				String[] lstrLstUserInfo = lstrUserInfo.split("~");
				str_inXML.append("<User>\n"
						+ "<UserIndex>" + lstrLstUserInfo[0] + "</UserIndex>\n"
						+ "<RoleIndex>" + lstrLstUserInfo[1] + "</RoleIndex>\n"
						+ "</User>\n");
			}	
			str_inXML.append("</Users>\n"
					+ "</NGOAddMemberToGroup_Input>");
		}catch(Exception lExcp){
			throw(lExcp);
		}
		return str_inXML.toString();
	}
	
	/**
	 * @param pstrEngineName
	 * @param pstrSessionId
	 * @param pstrCountFlag
	 * @param pstrQueueId
	 * @param pstrType
	 * @param pstrComparison
	 * @param pstrAttributeName
	 * @param pstrFilterString
	 * @param pstrLength
	 * @param pstrNoOfRecordsToFetch
	 * @param pstrOrderBy
	 * @param pstrSortOrder
	 * @param pstrLastValue
	 * @param pstrLastProcessInstance
	 * @param pstrLastWorkItemId
	 * @param pstrDataFlag
	 * @return
	 * @throws Exception
	 */
	public static String getFetchInstrumentsList_Input(String pstrEngineName,
									String pstrSessionId,
									String pstrCountFlag,
									String pstrQueueId,
									String pstrType,
									String pstrComparison,
									String pstrAttributeName,
									String pstrFilterString,
									String pstrLength,
									String pstrNoOfRecordsToFetch,
									String pstrOrderBy,
									String pstrSortOrder,
									String pstrLastValue,
									String pstrLastProcessInstance,
									String pstrLastWorkItemId,
									String pstrDataFlag) throws Exception {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("WMFetchWorkList", "Input");
		wfInputXml.appendTagAndValue("EngineName", pstrEngineName);
		wfInputXml.appendTagAndValue("SessionId", pstrSessionId);
		wfInputXml.appendTagAndValue("CountFlag", pstrCountFlag);
		wfInputXml.appendTagAndValue("DataFlag", pstrDataFlag);
		wfInputXml.appendTagAndValue("ZipBuffer", "N");
		wfInputXml.appendTagStart("Filter");
		wfInputXml.appendTagAndValue("QueueId", pstrQueueId);
		wfInputXml.appendTagAndValue("Type", pstrType);
		wfInputXml.appendTagAndValue("Comparison", pstrComparison);
		wfInputXml.appendTagAndValue("AttributeName", pstrAttributeName);
		wfInputXml.appendTagAndValue("FilterString", pstrFilterString);
		wfInputXml.appendTagAndValue("Length", pstrLength);
		wfInputXml.appendTagEnd("Filter");
		wfInputXml.appendTagStart("BatchInfo");
		wfInputXml.appendTagAndValue("NoOfRecordsToFetch", pstrNoOfRecordsToFetch);
		wfInputXml.appendTagAndValue("OrderBy", pstrOrderBy);
		wfInputXml.appendTagAndValue("SortOrder", pstrSortOrder);
		wfInputXml.appendTagAndValue("LastValue", pstrLastValue);
		wfInputXml.appendTagAndValue("LastProcessInstance", pstrLastProcessInstance);
		wfInputXml.appendTagAndValue("LastWorkItem", pstrLastWorkItemId);
		wfInputXml.appendTagEnd("BatchInfo");
		wfInputXml.appendEndCallName("WMFetchWorkList", "Input");
		return wfInputXml.toString();
	}
	
	
	/**
	 * @param pstrEngineName
	 * @param pstrSessionId
	 * @param pstrParentFID
	 * @param pstrDocumentName
	 * @param pstrISIndex
	 * @param pstrDocSize
	 * @param pstrNoOfPages
	 * @param pstrDocType
	 * @param pstrCreatedByAppName
	 * @param pstrComment
	 * @return
	 */
	public static String NGOAddDocument(String pstrEngineName,
			String pstrSessionId,
			String pstrParentFID,
			String pstrDocumentName,
			String pstrISIndex,
			String pstrDocSize,
			String pstrNoOfPages,
			String pstrDocType,
			String pstrCreatedByAppName,
			String pstrComment) {
		return "<?xml version=\"1.0\"?>\n" +
			"<NGOAddDocument_Input>\n" +
			"<Option>NGOAddDocument</Option>\n" +
			"<CabinetName>" + pstrEngineName + "</CabinetName>\n" +
			"<UserDBId>" + pstrSessionId + "</UserDBId>\n" +
			"<Document>\n" +
			"<ParentFolderIndex>" + pstrParentFID + "</ParentFolderIndex>\n" +
			"<DocumentName>" +  pstrDocumentName + "</DocumentName>\n" +
			"<DocumentType>" + pstrDocType + "</DocumentType>\n"+
			"<CreatedByAppName>" + pstrCreatedByAppName + "</CreatedByAppName>\n" +
			"<VersionFlag>Y</VersionFlag>\n" +
			"<ISIndex>" + pstrISIndex + "</ISIndex>\n" +
			"<DocumentSize>" + pstrDocSize + "</DocumentSize>\n" +
			"<NoOfPages>" + pstrNoOfPages + "</NoOfPages>\n" +
			"<Comment>" + pstrComment + "</Comment>\n" + 
			"</Document>\n"+
			"</NGOAddDocument_Input>";
	}

	
	/**
	 * @param pstrEngineName
	 * @param pstrSessionId
	 * @param pobjDocInfoArrayList
	 * @return
	 * @throws Exception
	 */
	public static String NGOAddDocument(  String pstrEngineName,
			String pstrSessionId,
			ArrayList pobjDocInfoArrayList
	) throws Exception
	{
		String lExceptionId = new String("com.newgen.srvr.xml.XMLGen.NGOAddDocument");
		StringBuffer lobjSBInXML = new StringBuffer("");
		try{
			lobjSBInXML.append("<?xml version=\"1.0\"?>\n"
					+ "<NGOAddDocument_Input>\n"
					+ "<Option>NGOAddDocument</Option>\n"
					+ "<CabinetName>" + pstrEngineName + "</CabinetName>\n"
					+ "<UserDBId>" + pstrSessionId + "</UserDBId>\n"
					+ "<Documents>\n");
			for(int i=0; i<pobjDocInfoArrayList.size(); i++){
				String lstrDocInfo = (String)pobjDocInfoArrayList.get(i);
				String[] lstrLstDocInfo = lstrDocInfo.split("~");
				lobjSBInXML.append("<Document>\n"
						+ "<ParentFolderIndex>" + lstrLstDocInfo[0] + "</ParentFolderIndex>\n"
						+ "<DocumentName>" +  lstrLstDocInfo[1] + "</DocumentName>\n"
						+ "<DocumentType>" + lstrLstDocInfo[2] + "</DocumentType>\n"
						+ "<CreatedByAppName>" + lstrLstDocInfo[3] + "</CreatedByAppName>\n"
						+ "<VersionFlag>Y</VersionFlag>\n"
						+ "<ISIndex>" + lstrLstDocInfo[4] + "</ISIndex>\n"
						+ "<DocumentSize>" + lstrLstDocInfo[5] + "</DocumentSize>\n"
						+ "<NoOfPages>" + lstrLstDocInfo[6] + "</NoOfPages>\n"
						+ "<Comment>" + lstrLstDocInfo[7] + "</Comment>\n" 
						+ "</Document>\n");
			}	
			lobjSBInXML.append("</Documents>\n"
					+ "<AllowReassignment>N</AllowReassignment>\n"
					+ "</NGOAddDocument_Input>");
		}catch(Exception lExcp){
			
			throw(lExcp);
		}
		return lobjSBInXML.toString();
	}

	/**
	 * @param pstrEngineName
	 * @param pstrSessionId
	 * @param pstrTableName
	 * @param pstrWhereClause
	 * @return
	 */
	public static String APDelete(String pstrEngineName,
			String pstrSessionId, String pstrTableName,
			String pstrWhereClause) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("APDelete", "Input");
		wfInputXml.appendTagAndValue("EngineName", pstrEngineName);
		wfInputXml.appendTagAndValue("SessionId", pstrSessionId);
		wfInputXml.appendTagAndValue("TableName", pstrTableName);
		wfInputXml.appendTagAndValue("WhereClause", pstrWhereClause);
		wfInputXml.appendEndCallName("APDelete", "Input");
		return wfInputXml.toString();
	}
	
	
	/**
	 * @param pstrEngineName
	 * @param pstrSessionId
	 * @param pstrProcessInstanceId
	 * @param pstrWorkItemId
	 * @param pstrAttribute
	 * @return
	 */
	public static String WMGetWorkItemAttributeValue(String pstrEngineName,
			String pstrSessionId,
			String pstrProcessInstanceId,
			String pstrWorkItemId,
			String pstrAttribute) {
		return "<?xml version=\"1.0\"?>" +
			"<WMGetWorkItemAttributeValue_Input>" +
			"<Option>WMGetWorkItemAttributeValue</Option>" +
			"<EngineName>" + pstrEngineName + "</EngineName>" +
			"<SessionId>" + pstrSessionId + "</SessionId>" +
			"<ProcessInstanceId>" + pstrProcessInstanceId + "</ProcessInstanceId>" +
			"<WorkItemID>" + pstrWorkItemId + "</WorkItemID>" +
			"<Name>" + pstrAttribute + "</Name>" +
			"</WMGetWorkItemAttributeValue_Input>";
	}
	
	public static String getWFUploadWorkItem_Input(String pstrEngineName,
			String pstrSessionId,
			String pstrProcessDefID,
			String pstrAttributes,
			String pstrDocuments) {
		return "<?xml version=\"1.0\"?>" +
	    	"<WFUploadWorkItem_Input>" +
	    	"<Option>WFUploadWorkItem</Option>" +
	    	"<EngineName>" + pstrEngineName + "</EngineName>" +
	    	"<SessionId>" + pstrSessionId + "</SessionId>" +
	    	"<ValidationRequired></ValidationRequired>" +
	    	"<ProcessDefId>" + pstrProcessDefID + "</ProcessDefId>" +
	    	"<DataDefName></DataDefName>" +
	    	"<Documents>" + pstrDocuments + "</Documents>" +
	    	"<Attributes>" + pstrAttributes + "</Attributes>" +
	    	"</WFUploadWorkItem_Input>";
	}
	
	
	public static String getIGGetData(String pstrEngineName, String pstrQuery, int pintColumnName){
		return "<IGGetData>" +
				"<Option>IGGetData</Option>" +
				"<EngineName>" + pstrEngineName + "</EngineName>" +
				"<QueryString>" + pstrQuery + "</QueryString>" +
				"<ColumnNo>" + pintColumnName + "</ColumnNo>" +
				"</IGGetData>";
	}
	
	
	public static String getIGSetData(String pstrEngineName, String pstrQuery){
			return "<IGSetData>" +
					"<Option>IGSetData</Option>" +
					"<EngineName>" + pstrEngineName + "</EngineName>" +
					"<Query>" + pstrQuery + "</Query>" +
					"</IGSetData>";
	}
	
	
	/**
	 * mLogger
	 * @return Logger
	 */

	
}


