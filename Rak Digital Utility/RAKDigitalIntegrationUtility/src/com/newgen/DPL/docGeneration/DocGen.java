package com.newgen.DPL.docGeneration;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import com.newgen.DCC.EFMS.DCC_MurabahaDealIntegration;
import com.newgen.DCC.Update_AssignCIF.DCC_DocumentGeneration;
import com.newgen.DCC.Update_AssignCIF.DCC_UpdateAssignCIFLog;
import com.newgen.DCC.Update_AssignCIF.DCC_Update_Assign_CIF_SysIntegration;
import com.newgen.DPL.Digital_PL_Log;
import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.omni.wf.util.excp.NGException;

public class DocGen{

	private static final Category DPL_DocumentGenerationLog = null;
	static NGEjbClient ngEjbClient;
	private static org.apache.log4j.Logger logger;

	private static String jtsIP;
	private static String jtsPort;
	private static String ActivityType;
	private static String ProcessDefId;
	private static String ActivityName;
	private static String ActivityID;
	private static  String cabinetName;
	
	private String sessionID = "";
	private int integrationWaitTime=0;
	private int socketConnectionTimeout=0;
	

	public DocGen() throws NGException {
		Digital_PL_Log.setLogger(getClass().getSimpleName());
		this.ngEjbClient = NGEjbClient.getSharedInstance();
		logger = Digital_PL_Log.getLogger(getClass().getSimpleName());
	}
	

	public void startDPLDocumentUtility(String cabinetName, String sJtsIp, String iJtsPort, String sessionId,
			String queueID, HashMap<String, String> socketDetailsMap, Map<String, String> ConfigParamMap) {

		try {
			final String ws_name="Doc_Generation";  //ConfigParamMap.get("WS_NAME");
			final String Queuename="Digital_PL_Doc_Generation";//ConfigParamMap.get("QueueName");
			integrationWaitTime=Integer.parseInt(ConfigParamMap.get("INTEGRATION_WAIT_TIME"));
			logger.debug("IntegrationWaitTime: "+integrationWaitTime);

			socketConnectionTimeout=Integer.parseInt(ConfigParamMap.get("MQ_SOCKET_CONNECTION_TIMEOUT"));
			logger.debug("SocketConnectionTimeOut: "+socketConnectionTimeout);

			// Validate Session ID
			sessionID = CommonConnection.getSessionID(logger, false);
			if (sessionId == null || sessionId.equalsIgnoreCase("") || sessionId.equalsIgnoreCase("null")) {
				logger.error("Could Not Get Session ID " + sessionId);
				return;
			}

			// Fetch all Work-Items on given queueID.
			logger.debug("Fetching all Workitems on " + Queuename + " queue");
			System.out.println("Fetching all Workitems on " + Queuename + " queue");
			
			String fetchWorkitemListInputXML = CommonMethods.fetchWorkItemsInput(cabinetName, sessionId, queueID);
			logger.debug("InputXML for fetchWorkList Call: " + fetchWorkitemListInputXML);
			System.out.print("");
			String fetchWorkitemListOutputXML = CommonMethods.WFNGExecute(fetchWorkitemListInputXML, sJtsIp, iJtsPort,1);
			logger.debug("WMFetchWorkList OutputXML: " + fetchWorkitemListOutputXML);

			XMLParser xmlParserFetchWorkItemlist = new XMLParser(fetchWorkitemListOutputXML);

			String fetchWorkItemListMainCode = xmlParserFetchWorkItemlist.getValueOf("MainCode");
			logger.debug("FetchWorkItemListMainCode: " + fetchWorkItemListMainCode);

			int fetchWorkitemListCount = Integer.parseInt(xmlParserFetchWorkItemlist.getValueOf("RetrievedCount"));
			logger.debug("RetrievedCount for WMFetchWorkList Call: " + fetchWorkitemListCount);

			logger.debug("Number of workitems retrieved on CIF_Update_Initial: " + fetchWorkitemListCount);

			System.out.println("Number of workitems retrieved on CIF_Update_Initial: " + fetchWorkitemListCount);

			if (fetchWorkItemListMainCode.trim().equals("0") && fetchWorkitemListCount > 0) {
				for (int i = 0; i < fetchWorkitemListCount; i++) {
					String fetchWorkItemlistData = xmlParserFetchWorkItemlist.getNextValueOf("Instrument");
					fetchWorkItemlistData = fetchWorkItemlistData.replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");

					logger.debug("Parsing <Instrument> in WMFetchWorkList OutputXML: " + fetchWorkItemlistData);
					XMLParser xmlParserfetchWorkItemData = new XMLParser(fetchWorkItemlistData);

					String processInstanceID = xmlParserfetchWorkItemData.getValueOf("ProcessInstanceId");
					logger.debug("Current ProcessInstanceID: " + processInstanceID);

					logger.debug("Processing Workitem: " + processInstanceID);
					System.out.println("\nProcessing Workitem: " + processInstanceID);

					String WorkItemID = xmlParserfetchWorkItemData.getValueOf("WorkItemId");
					logger.debug("Current WorkItemID: " + WorkItemID);

					String entryDateTime = xmlParserfetchWorkItemData.getValueOf("EntryDateTime");
					logger.debug("Current EntryDateTime: " + entryDateTime);

					ActivityName = xmlParserfetchWorkItemData.getValueOf("ActivityName");
					logger.debug("ActivityName: " + ActivityName);

					ActivityID = xmlParserfetchWorkItemData.getValueOf("WorkStageId");
					logger.debug("ActivityID: " + ActivityID);
					ActivityType = xmlParserfetchWorkItemData.getValueOf("ActivityType");
					logger.debug("ActivityType: " + ActivityType);
					ProcessDefId = xmlParserfetchWorkItemData.getValueOf("RouteId");
					logger.debug("ProcessDefId: " + ProcessDefId);

					String DB_Query = "SELECT a.IsNTB,a.ProductType,a.IsFIRCOHit,a.EFMS_Status,a.IsFTSReq,a.isstp,a.DectechDecision,a.ProductName,a.PreferredLanguage,a.Nationality,b.TIN FROM NG_DPL_EXTTABLE a  with(nolock) inner join NG_DPL_GR_FATCA_CRS_details b with(nolock) on a.WINAME=b.WIName  WHERE a.WINAME='"
							+ processInstanceID + "'";

					String extTabDataINPXML = CommonMethods.apSelectWithColumnNames(DB_Query,
							CommonConnection.getCabinetName(), CommonConnection.getSessionID(logger, false));
					logger.debug("extTabDataIPXML: " + extTabDataINPXML);
					String extTabDataOUPXML = CommonMethods.WFNGExecute(extTabDataINPXML, CommonConnection.getJTSIP(),
							CommonConnection.getJTSPort(), 1);
					logger.debug("extTabDataOPXML: " + extTabDataOUPXML);

					XMLParser xmlParserData = new XMLParser(extTabDataOUPXML);
					int iTotalrec = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));

					String decisionValue = "";
					String attributesTag = "";
					String updateCIFIntegrationStatus = "";
					String ErrDesc = "";

					if (!xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec == 0) {
						decisionValue = "Failed";
						ErrDesc = "apselect for Fetching WI details failed";
						attributesTag = "<Decision>" + decisionValue + "</Decision>";
						logger.debug("apselect for Fetching WI details failed");
						doneWI(processInstanceID, WorkItemID, decisionValue, entryDateTime, ErrDesc, attributesTag,
								sessionId);
						continue;
					}

					String NTB = xmlParserData.getValueOf("IsNTB");

					

//					if (!"Y".equalsIgnoreCase(CardOps_Reschedule) && !"Y".equalsIgnoreCase(Is_CIF_UPDATED)
//							&& "Y".equalsIgnoreCase(IS_virtual_Card_Created) && "true".equalsIgnoreCase(NTB)) {
//						logger.error("Inside UPDATE CIF");
//			
//						updateCIFIntegrationStatus = customIntegration(cabinetName, sessionId, sJtsIp,
//								iJtsPort, processInstanceID, ws_name, integrationWaitTime, socketConnectionTimeout,
//								socketDetailsMap);
//						if (!"Success".equalsIgnoreCase(updateCIFIntegrationStatus)) {
//							ErrDesc = updateCIFIntegrationStatus.replace("~", ",").replace("|", "\n");
//							decisionValue = "Failed";
//							logger.debug("Decision" + decisionValue);
//							attributesTag = "<Decision>" + decisionValue + "</Decision>";
//							doneWI(processInstanceID, WorkItemID, decisionValue, entryDateTime, ErrDesc, attributesTag,
//									sessionId);
//							continue;
//						} else {
//							/** Update Is_CIF_UPDATED value in exttable to Y **/
//							if (updateFlagInExtTable("Y", "Is_CIF_UPDATED", processInstanceID, entryDateTime,
//									sessionId)) {
//								Is_CIF_UPDATED = "Y";
//							} else
//								continue;
//						}
//					}

//					if ((!"Y".equalsIgnoreCase(CardOps_Reschedule) && "Y".equalsIgnoreCase(Is_CIF_UPDATED)
//							&& !"Y".equalsIgnoreCase(Is_CIF_ASSIGNED) && "Y".equalsIgnoreCase(IS_virtual_Card_Created)
//							&& "true".equalsIgnoreCase(NTB))
//							|| (!"Y".equalsIgnoreCase(CardOps_Reschedule) && !"Y".equalsIgnoreCase(Is_CIF_UPDATED)
//									&& !"Y".equalsIgnoreCase(Is_CIF_ASSIGNED)
//									&& !"Y".equalsIgnoreCase(IS_virtual_Card_Created)
////									&& !"Y".equalsIgnoreCase(Is_CIF_ASSIGNED) && "true".equalsIgnoreCase(NTB))) {
//						logger.error("Inside ASSIGN CIF");
//						DCC_Assign_CIF objAssignCIF = new DCC_Assign_CIF(logger);
//
//						String firco = xmlParserData.getValueOf("FIRCO_Flag");
//						String fircoAction = xmlParserData.getValueOf("FircoUpdateAction");
//						String efms = xmlParserData.getValueOf("EFMS_Status");
//						String fts = xmlParserData.getValueOf("FTS_Ack_flg");
//						String stp = xmlParserData.getValueOf("is_stp");
//						String DectechDecision = xmlParserData.getValueOf("Dectech_Decision");
//						String UWDecision = xmlParserData.getValueOf("UW_Decision");
//
//						logger.error("Inside ASSIGN CIF");
//
//						if ("Y".equalsIgnoreCase(IS_virtual_Card_Created)) {
//							// To be changes as part of EFMS change 22112023
//							// PDSC-1073
//							if ("Reject".equalsIgnoreCase(UWDecision) || "D".equalsIgnoreCase(DectechDecision)
//									|| "CB".equalsIgnoreCase(firco) || "Confirmed Fraud".equalsIgnoreCase(efms)
//									|| "Negative".equalsIgnoreCase(efms)
//									|| ("Y".equalsIgnoreCase(stp) && "R".equalsIgnoreCase(DectechDecision))
//									|| "D".equalsIgnoreCase(fts) || "Decline".equalsIgnoreCase(fircoAction)) {
//								String NOTIFY_DEH_IDENTIFIER = "Decline_Prospect";
//								if ("D".equalsIgnoreCase(fts) || "Decline".equalsIgnoreCase(fircoAction))
//									NOTIFY_DEH_IDENTIFIER = "Expire_Prospect";
//								decisionValue = "Reject";
//								attributesTag = "<Decision>" + decisionValue + "</Decision>" + "<NOTIFY_DEH_IDENTIFIER>"
//										+ NOTIFY_DEH_IDENTIFIER + "</NOTIFY_DEH_IDENTIFIER>";
//								ErrDesc = "CIF Update Done Successfully";
//								doneWI(processInstanceID, WorkItemID, decisionValue, entryDateTime, ErrDesc,
//										attributesTag, sessionId);
//								continue;
//							}
//						}
//
//						/*
//						 * String assignCIFIntegrationStatus=objAssignCIF.
//						 * DCC_Assign_CIF_Integration(cabinetName,jtsIP,jtsPort,
//						 * sessionId,processInstanceID,ws_name,
//						 * integrationWaitTime,socketConnectionTimeout,
//						 * socketDetailsMap);
//						 * logger.debug("assignCIFIntegrationStatus"
//						 * +assignCIFIntegrationStatus); String statuses [] =
//						 * null;
//						 * 
//						 * if (assignCIFIntegrationStatus != null) statuses =
//						 * assignCIFIntegrationStatus.split("~");
//						 * 
//						 * if (statuses != null && statuses.length > 0 &&
//						 * statuses[0].equalsIgnoreCase("0000")) { //Update
//						 * Is_CIF_ASSIGNED value in exttable to Y if
//						 * (updateFlagInExtTable("Y", "Is_CIF_ASSIGNED",
//						 * processInstanceID, entryDateTime, sessionId)){
//						 * Is_CIF_ASSIGNED = "Y"; } else continue; } else { if
//						 * (statuses != null && statuses.length > 0) { ErrDesc =
//						 * "Assign CIF Failed " + statuses[0] + ":"; if
//						 * (statuses.length > 1) ErrDesc =ErrDesc +statuses[1];
//						 * } else { ErrDesc = "Assign CIF Failed "; }
//						 * decisionValue = "Failed"; logger.debug("Decision"
//						 * +decisionValue);
//						 * attributesTag="<Decision>"+decisionValue+
//						 * "</Decision>";
//						 * doneWI(processInstanceID,WorkItemID,decisionValue,
//						 * entryDateTime,ErrDesc,attributesTag,sessionId);
//						 * continue; }
//						 */
//					}

					// VINAYAK CHNGAES

					// chnages to execute service maintenance call only for ntb
					// cases
//					if ("true".equalsIgnoreCase(NTB)) {
//						Is_CIF_ASSIGNED = "Y";
//					}
//					if ("Y".equalsIgnoreCase(Is_CIF_ASSIGNED) && !"Y".equalsIgnoreCase(CardOps_Reschedule)
////							&& "true".equalsIgnoreCase(NTB)) {
//
//						// is_service_main
//						DCC_Service_Maintenance objservice = new DCC_Service_Maintenance(logger);
//
//						String serviceRequestIntegrationStatus = objservice.DCC_Service_Maintenance_Integration(
//								cabinetName, jtsIP, jtsPort, sessionId, processInstanceID, ws_name, integrationWaitTime,
//								socketConnectionTimeout, socketDetailsMap);
//						logger.debug("serviceRequestIntegrationStatus" + serviceRequestIntegrationStatus);
//						String statuses[] = null;
//
//						if (serviceRequestIntegrationStatus != null)
//							statuses = serviceRequestIntegrationStatus.split("~");
//
//						if (statuses != null && statuses.length > 0 && statuses[0].equalsIgnoreCase("0000")) {
//							/**
//							 * Update Is_CIF_ASSIGNED value in exttable to Y
//							 **/
//							if (updateFlagInExtTable("Y", "is_service_main", processInstanceID, entryDateTime,
//									sessionId)) {
//								String is_service_main = "Y";
//							} else
//								continue;
//
//						} else {
//							if (statuses != null && statuses.length > 0) {
//								ErrDesc = "Service Maintenance Failed " + statuses[0] + ":";
//								if (statuses.length > 1)
//									ErrDesc = ErrDesc + statuses[1];
//							} else {
//								ErrDesc = "Service Maintenance Failed ";
//							}
//							decisionValue = "Failed";
//							logger.debug("Decision" + decisionValue);
//							attributesTag = "<Decision>" + decisionValue + "</Decision>";
//							doneWI(processInstanceID, WorkItemID, decisionValue, entryDateTime, ErrDesc, attributesTag,
//									sessionId);
//							continue;
//						}
//
//						// vinayak changes ends
//					}

					// Updated 14122022
					if (!"true".equalsIgnoreCase(NTB)) {

						String Product = xmlParserData.getValueOf("ProductType");
						String nationality = xmlParserData.getValueOf("Nationality");
						//String Preferred_Language = xmlParserData.getValueOf("PreferredLanguage");
						String TIN = xmlParserData.getValueOf("TIN");

						//DCC_DocumentGeneration objDocGen = new DCC_DocumentGeneration(logger);
						String docToBeGen = "";
//						if ("ISL".equalsIgnoreCase(ProductType)) {
//							docToBeGen = "MRBH_Agency_Agreement";
//							if ("AR".equalsIgnoreCase(Preferred_Language))
//								docToBeGen = docToBeGen + "~Customer_Consent_Form_Islamic-Arabic";
//							else
//								docToBeGen = docToBeGen + "~Customer_Consent_Form_Islamic-English";
//						} else {
//							if ("AR".equalsIgnoreCase(Preferred_Language)) {
//								docToBeGen = "Customer_Consent_Form_Conv-Arabic";
//							} else {
//								docToBeGen = "Customer_Consent_Form_Conv-English";
//							}
//						}

						if ("US".equalsIgnoreCase(nationality)) {
							docToBeGen = docToBeGen + "~DPL_W9Form";
						} else if (!"US".equalsIgnoreCase(nationality) && TIN != null && !"".equalsIgnoreCase(TIN)) {
							docToBeGen = docToBeGen + "~DPL_W8-Form";
							docToBeGen = docToBeGen + "~DPL_Security_Cheque";
							docToBeGen = docToBeGen + "~DPL_SalamContract";
							docToBeGen = docToBeGen + "~DPL_AGENCY_LETTER";
							
							
						}
//						docToBeGen = docToBeGen + "~Security_Cheque";
//						docToBeGen = docToBeGen + "~Agency_Letter";
//						docToBeGen = docToBeGen + "~Salam_Contract";

						String docGenStatus = generate_Document_Customer_Consent(docToBeGen,
								processInstanceID, sessionId);

						logger.debug("docGenStatus:--" + docGenStatus);
						if (docGenStatus == null || docGenStatus.contains("Error")) {
							decisionValue = "Failed";
							ErrDesc = "Doc Genration Failed";
							String err[] = docGenStatus.split("~");
							if (err.length > 1)
								ErrDesc = "Doc Genration Failed for document " + err[1];
						} else if (docGenStatus.contains("Success")) {
							decisionValue = "Success";
							
							// Hritik --- 19.7.23 -- ETB Case
//							if (!"true".equalsIgnoreCase(NTB)) {
//								insert_ng_digital_awb_status_DCC(processInstanceID, cabinetName, sessionID, sJtsIp,
//										iJtsPort, ActivityName);
//							}
						}
						logger.debug("Decision" + decisionValue);
						attributesTag = "<Decision>" + decisionValue + "</Decision>";
						doneWI(processInstanceID, WorkItemID, decisionValue, entryDateTime, ErrDesc, attributesTag,
								sessionId);
						continue;
					}

//					if (!"true".equalsIgnoreCase(NTB)) {
//						DCC_DocumentGeneration objDocGen = new DCC_DocumentGeneration(logger);
//						String Preferred_Language = xmlParserData.getValueOf("PreferredLanguage");
//						String Product = xmlParserData.getValueOf("ProductType");
//						String nationality = xmlParserData.getValueOf("Nationality");
//						String TIN = xmlParserData.getValueOf("TIN");
//
//						String doc_DB_Query = "select Doc_Name from NG_DCC_GR_DOCUMENT_NAME with(nolock) WHERE WI_NAME='"
//								+ processInstanceID + "'";
//
//						String docGRDataINPXML = CommonMethods.apSelectWithColumnNames(doc_DB_Query,
//								CommonConnection.getCabinetName(), CommonConnection.getSessionID(logger, false));
//						logger.debug("docGRDataINPXML: " + docGRDataINPXML);
//						String docGRDataOUPXML = CommonMethods.WFNGExecute(docGRDataINPXML, CommonConnection.getJTSIP(),
//								CommonConnection.getJTSPort(), 1);
//						//logger.debug("docGRDataOUPXML: " + docGRDataOUPXML);
//
//						XMLParser xmlParserData1 = new XMLParser(docGRDataOUPXML);
//						int iTotalrec1 = Integer.parseInt(xmlParserData1.getValueOf("TotalRetrieved"));
//
//						String decisionValue1 = "";
//						String attributesTag1 = "";
//						String ErrDesc1 = "";
//						String docToBeGen1 = "";
//
//						if (!xmlParserData1.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec1 == 0) {
//							decisionValue1 = "Failed";
//							ErrDesc1 = "apselect for Fetching WI details failed";
//							attributesTag1 = "<Decision>" + decisionValue1 + "</Decision>";
//							logger.debug("apselect for Fetching WI doc name failed");
//							doneWI(processInstanceID, WorkItemID, decisionValue, entryDateTime, ErrDesc1,
//									attributesTag1, sessionId);
//							continue;
//						}
//
//						if (xmlParserData1.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec1 > 0) {
//
//							for (int k = 0; k < iTotalrec1; k++) {
//								logger.debug("----Reschedule flag document Name -- " + k);
//								String doc_name_record = xmlParserData1.getNextValueOf("Record");
//								doc_name_record = doc_name_record.replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");
//								XMLParser docxmlParser = new XMLParser(doc_name_record);
//								String doc_name = docxmlParser.getValueOf("doc_name");
//
//								if (doc_name.equalsIgnoreCase("MRBH_Agency_Agreement")) {
//									docToBeGen1 = docToBeGen1 + "~MRBH_Agency_Agreement";
//								}
//
//								if (doc_name.equalsIgnoreCase("Customer_Consent_Form")) {
//									if ("ISL".equalsIgnoreCase(Product)) {
//										if ("AR".equalsIgnoreCase(Preferred_Language)) {
//											docToBeGen1 = docToBeGen1 + "~Customer_Consent_Form_Islamic-Arabic";
//
//										} else {
//											docToBeGen1 = docToBeGen1 + "~Customer_Consent_Form_Islamic-English";
//										}
//
//									} else {
//										if ("AR".equalsIgnoreCase(Preferred_Language)) {
//											docToBeGen1 = "Customer_Consent_Form_Conv-Arabic";
//										} else {
//											docToBeGen1 = "Customer_Consent_Form_Conv-English";
//										}
//									}
//								}
//
//								if (doc_name.equalsIgnoreCase("US") || doc_name.equalsIgnoreCase("W8/W9")) {
//
//									if ("US".equalsIgnoreCase(nationality)) {
//										docToBeGen1 = docToBeGen1 + "~W-9_Form";
//									} else if (!"US".equalsIgnoreCase(nationality) && TIN != null
//											&& !"".equalsIgnoreCase(TIN)) {
//										docToBeGen1 = docToBeGen1 + "~W-8_Form";
//									}
//								}
//
//								if (doc_name.equalsIgnoreCase("W-9_Form") || doc_name.equalsIgnoreCase("W-9")
//										|| doc_name.equalsIgnoreCase("W9")) {
//
//									docToBeGen1 = docToBeGen1 + "~W-9_Form";
//
//								}
//								if (doc_name.equalsIgnoreCase("W-8_Form") || doc_name.equalsIgnoreCase("W-8")
//										|| doc_name.equalsIgnoreCase("W8")) {
//
//									docToBeGen1 = docToBeGen1 + "~W-8_Form";
//
//								}
//
//								if (doc_name.equalsIgnoreCase("Security_Cheque")) {
//
//									docToBeGen1 = docToBeGen1 + "~Security_Cheque";
//								}
//
//								logger.debug("----Reschedule flag document Name -- Final Doc to be generated 123 "
//										+ docToBeGen1);
//
//							}
//							logger.debug(
//									"----Reschedule flag document Name -- Final Doc to be generated " + docToBeGen1);
//							String docGenStatus = generate_Document_Customer_Consent(docToBeGen1,
//									processInstanceID, sessionId);
//
//							logger.debug("docGenStatus:--" + docGenStatus);
//							if (docGenStatus == null || docGenStatus.contains("Error")) {
//								decisionValue = "Failed";
//								ErrDesc = "Doc Genration Failed";
//								String err[] = docGenStatus.split("~");
//								if (err.length > 1)
//									ErrDesc = "Doc Genration Failed for document " + err[1];
//							} else if (docGenStatus.contains("Success")) {
//								decisionValue = "Success";
//
//							}
//							logger.debug("Decision" + decisionValue);
//							attributesTag = "<Decision>" + decisionValue + "</Decision>";
//							doneWI(processInstanceID, WorkItemID, decisionValue, entryDateTime, ErrDesc, attributesTag,
//									sessionId);
//							continue;
//						}
//
//						if (xmlParserData1.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec1 == 0) {
//							Product = xmlParserData.getValueOf("ProductType");
//							nationality = xmlParserData.getValueOf("Nationality");
//							Preferred_Language = xmlParserData.getValueOf("PreferredLanguage");
//							TIN = xmlParserData.getValueOf("TIN");
//
//							//objDocGen = new DCC_DocumentGeneration(logger);
//							String docToBeGen = "";
//							if ("ISL".equalsIgnoreCase(Product)) {
//								docToBeGen = "MRBH_Agency_Agreement";
//								if ("AR".equalsIgnoreCase(Preferred_Language))
//									docToBeGen = docToBeGen + "~Customer_Consent_Form_Islamic-Arabic";
//								else
//									docToBeGen = docToBeGen + "~Customer_Consent_Form_Islamic-English";
//							} else {
//								if ("AR".equalsIgnoreCase(Preferred_Language)) {
//									docToBeGen = "Customer_Consent_Form_Conv-Arabic";
//								} else {
//									docToBeGen = "Customer_Consent_Form_Conv-English";
//								}
//							}
//
//							if ("US".equalsIgnoreCase(nationality)) {
//								docToBeGen = docToBeGen + "~W-9_Form";
//							} else if (!"US".equalsIgnoreCase(nationality) && TIN != null
//									&& !"".equalsIgnoreCase(TIN)) {
//								docToBeGen = docToBeGen + "~W-8_Form";
//							}
//							docToBeGen = docToBeGen + "~Security_Cheque";
//
//							String docGenStatus = generate_Document_Customer_Consent(docToBeGen,
//									processInstanceID, sessionId);
//
//							logger.debug("docGenStatus:--" + docGenStatus);
//							if (docGenStatus == null || docGenStatus.contains("Error")) {
//								decisionValue = "Failed";
//								ErrDesc = "Doc Genration Failed";
//								String err[] = docGenStatus.split("~");
//								if (err.length > 1)
//									ErrDesc = "Doc Genration Failed for document " + err[1];
//							} else if (docGenStatus.contains("Success")) {
//								decisionValue = "Success";
//							}
//							logger.debug("Decision" + decisionValue);
//							attributesTag = "<Decision>" + decisionValue + "</Decision>";
//							doneWI(processInstanceID, WorkItemID, decisionValue, entryDateTime, ErrDesc, attributesTag,
//									sessionId);
//							continue;
//						}
//					}
					//
				}

			}
		} catch (Exception e) {
			logger.debug("Exception: " + e.getMessage());
		}

	}

	private void doneWI(String processInstanceID, String WorkItemID, String decisionValue, String entryDateTime,
			String ErrDesc, String attributesTag, String sessionId) {
		try {
			// Lock Workitem.
			String getWorkItemInputXML = CommonMethods.getWorkItemInput(CommonConnection.getCabinetName(), sessionId, processInstanceID,
					WorkItemID);
			String getWorkItemOutputXml = CommonMethods.WFNGExecute(getWorkItemInputXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 1);
			logger.debug("Output XML For WmgetWorkItemCall: " + getWorkItemOutputXml);

			XMLParser xmlParserGetWorkItem = new XMLParser(getWorkItemOutputXml);
			String getWorkItemMainCode = xmlParserGetWorkItem.getValueOf("MainCode");
			logger.debug("WmgetWorkItemCall Maincode:  " + getWorkItemMainCode);

			if (getWorkItemMainCode.trim().equals("0")) {
				logger.debug("WMgetWorkItemCall Successful: " + getWorkItemMainCode);

				String assignWorkitemAttributeInputXML = "<?xml version=\"1.0\"?><WMAssignWorkItemAttributes_Input>"
						+ "<Option>WMAssignWorkItemAttributes</Option>" + "<EngineName>" + CommonConnection.getCabinetName() + "</EngineName>"
						+ "<SessionId>" + sessionId + "</SessionId>" + "<ProcessInstanceId>" + processInstanceID
						+ "</ProcessInstanceId>" + "<WorkItemId>" + WorkItemID + "</WorkItemId>" + "<ActivityId>"
						+ ActivityID + "</ActivityId>" + "<ProcessDefId>" + ProcessDefId + "</ProcessDefId>"
						+ "<LastModifiedTime></LastModifiedTime>" + "<ActivityType>" + ActivityType + "</ActivityType>"
						+ "<complete>D</complete>" + "<AuditStatus></AuditStatus>" + "<Comments></Comments>"
						+ "<UserDefVarFlag>Y</UserDefVarFlag>" + "<Attributes>" + attributesTag + "</Attributes>"
						+ "</WMAssignWorkItemAttributes_Input>";

				logger.debug("InputXML for assignWorkitemAttribute Call: " + assignWorkitemAttributeInputXML);

				String assignWorkitemAttributeOutputXML = CommonMethods.WFNGExecute(assignWorkitemAttributeInputXML,
						CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 1);

				logger.debug("OutputXML for assignWorkitemAttribute Call: " + assignWorkitemAttributeOutputXML);

				XMLParser xmlParserWorkitemAttribute = new XMLParser(assignWorkitemAttributeOutputXML);
				String assignWorkitemAttributeMainCode = xmlParserWorkitemAttribute.getValueOf("MainCode");
				logger.debug("AssignWorkitemAttribute MainCode: " + assignWorkitemAttributeMainCode);

				if (assignWorkitemAttributeMainCode.trim().equalsIgnoreCase("0")) {
					logger.debug("AssignWorkitemAttribute Successful: " + assignWorkitemAttributeMainCode);
					if ("0".trim().equalsIgnoreCase("0")) {
						System.out.println(processInstanceID + "Complete Succesfully with status " + decisionValue);

						logger.debug("WorkItem moved to next Workstep.");

						SimpleDateFormat inputDateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
						SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");

						Date entryDatetimeFormat = inputDateformat.parse(entryDateTime);
						String formattedEntryDatetime = outputDateFormat.format(entryDatetimeFormat);
						logger.debug("FormattedEntryDatetime: " + formattedEntryDatetime);

						Date actionDateTime = new Date();
						String formattedActionDateTime = outputDateFormat.format(actionDateTime);
						logger.debug("FormattedActionDateTime: " + formattedActionDateTime);

						// Insert in WIHistory Table.
						String columnNames="WI_NAME,Decision_Date_Time,WORKSTEP,USERNAME,DECISION,ENTRY_DATE_TIME,REMARKS";
						String columnValues="'"+processInstanceID+"','"+formattedActionDateTime+"','"+ActivityName+"','"
								+CommonConnection.getUsername()+"','"+decisionValue+"','"+formattedEntryDatetime+"','"+ErrDesc+"'";

						String apInsertInputXML = CommonMethods.apInsert(cabinetName, sessionId, columnNames,
								columnValues, "NG_DPL_GR_DECISION_HISTORY");
						logger.debug("APInsertInputXML: " + apInsertInputXML);

						String apInsertOutputXML = CommonMethods.WFNGExecute(apInsertInputXML, jtsIP, jtsPort, 1);
						logger.debug("APInsertOutputXML: " + apInsertInputXML);

						XMLParser xmlParserAPInsert = new XMLParser(apInsertOutputXML);
						String apInsertMaincode = xmlParserAPInsert.getValueOf("MainCode");
						logger.debug("Status of apInsertMaincode  " + apInsertMaincode);

						logger.debug("Completed On " + ActivityName);

						if (apInsertMaincode.equalsIgnoreCase("0")) {
							logger.debug("ApInsert successful: " + apInsertMaincode);
							logger.debug("Inserted in WiHistory table successfully.");
						} else {
							logger.debug("ApInsert failed: " + apInsertMaincode);
						}
					} else {
						// completeWorkitemMaincode="";
						// logger.debug("WMCompleteWorkItem failed:
						// "+completeWorkitemMaincode);
					}
				} else if ("11".equalsIgnoreCase(assignWorkitemAttributeMainCode)) {

					sessionID = CommonConnection.getSessionID(logger, false);
					doneWI(processInstanceID, WorkItemID, decisionValue, entryDateTime, ErrDesc, attributesTag,
							sessionID);
				} else {
					assignWorkitemAttributeMainCode = "";
					logger.debug("AssignWorkitemAttribute failed: " + assignWorkitemAttributeMainCode);
				}
			} else {
				getWorkItemMainCode = "";
				logger.debug("WmgetWorkItem failed: " + getWorkItemMainCode);
			}
		}

		catch (Exception e) {
			logger.debug("DoneWI Exception: " + e.toString());
		}
	}

//	public String executeMurahabhaCalls(String processInstanceID, String ws_name, String WorkItemID,
//			String entryDateTime, String ActivityType) {
//		String status = "";
//		String errorDesc = "";
//		try {
//			String islamicflag = isIslamic(processInstanceID);
//			if ("Y".equalsIgnoreCase(islamicflag)) {
//				logger.error("Execute Murabha calls--");
//				String query = "select ResponseFlag from NG_DCC_MURABAHA_RESPONSE_DATA  with(nolock) where wi_name='"
//						+ processInstanceID + "'";
//
//				String MURABAHAIPXML = CommonMethods.apSelectWithColumnNames(query, cabinetName, sessionID);
//				logger.debug("extTabDataIPXML: " + MURABAHAIPXML);
//				String MURABAHAIPXMLOPXML = CommonMethods.WFNGExecute(MURABAHAIPXML, jtsIP, jtsPort, 1);
//				logger.debug("extTabDataOPXML: " + MURABAHAIPXMLOPXML);
//
//				XMLParser xmlParserData = new XMLParser(MURABAHAIPXMLOPXML);
//				int iTotalrec = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));
//				String mainCode = xmlParserData.getValueOf("MainCode");
//				String responseflag = xmlParserData.getValueOf("ResponseFlag");
//				if ("0".equalsIgnoreCase(mainCode) && (iTotalrec == 0 || !"SUCCESS".equalsIgnoreCase(responseflag))) {
//					DCC_MurabahaDealIntegration MurahabaObj = new DCC_MurabahaDealIntegration(
//							logger);
//					String MurabhaCallsStatus = MurahabaObj.MurabahaReqDeal(cabinetName, jtsIP, jtsPort, sessionID,
//							processInstanceID, socketConnectionTimeout, integrationWaitTime, socketDetailsMap,
//							TrialTime, ErrorCount, ws_name, "1");
//					if ("Success".equalsIgnoreCase(MurabhaCallsStatus)) {
//						return "Success";
//					} else {
//						status = "F";
//					}
//				} else if (iTotalrec > 0 && "SUCCESS".equalsIgnoreCase(responseflag)) {
//					return "Success";
//				} else {
//					logger
//							.error("Some error occured in getting Murahabha Response flag--" + mainCode);
//					status = "F";
//				}
//			} else if ("N".equalsIgnoreCase(islamicflag)) {
//				logger.error("Case is not islamic!");
//				return "Success";
//			} else {
//				logger
//						.error("Some error occured in getting ISLAMIC flag-");
//				status = "F";
//			}
//
//		} catch (Exception e) {
//			logger
//					.error("Exception in executing Murahabha calls-" + e.toString());
//			status = "F";
//		}
//
//		if ("F".equalsIgnoreCase(status)) {
//			try {
//				
//				routeToErrorHandling(cabinetName, jtsIP, jtsPort, sessionID, processInstanceID, WorkItemID,
//						entryDateTime, ActivityID, ActivityType, ProcessDefId, "Error in Murabha Execution");
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				logger
//						.error("Exception in sending mail-" + e.toString());
//			}
//			return "Error";
//		}
//		return "Success";
//	}

	
//	public	String customIntegration(String cabinetName,String sessionId,String sJtsIp, String iJtsPort , String processInstanceID,
//			String ws_name, int socket_connection_timeout,int integrationWaitTime,
//			HashMap<String, String> socketDetailsMap)
//	{
//		String FinalStatus = "";
//		try
//		{
//			
//			
//			String DBQuery = "SELECT CIF, FATCA_Tin_Number,Tin_reason,FinalTAI,nationality,FirstName,MiddleName,LastName,EFR_NSTP FROM NG_DCC_EXTTABLE with(nolock) WHERE WI_NAME='"+processInstanceID+"'";
//
//			String extTabDataIPXML = CommonMethods.apSelectWithColumnNames(DBQuery,CommonConnection.getCabinetName(), CommonConnection.getSessionID(D_CIFUpdateLog, false));
//			logger.debug("CIF Update data input223: "+ extTabDataIPXML);
//			String extTabDataOPXML = CommonMethods.WFNGExecute(extTabDataIPXML,CommonConnection.getJTSIP(),CommonConnection.getJTSPort(),1);
//			logger.debug("CIF Update data output223: "+ extTabDataOPXML);
//
//			XMLParser xmlParserData= new XMLParser(extTabDataOPXML);						
//			int iTotalrec = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));
//
//			
//			if(iTotalrec == 0)
//				return "Success";
//			
//			if(xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0")&& iTotalrec>0)
//			{
//
//				String xmlDataExtTab=xmlParserData.getNextValueOf("Record");
//				xmlDataExtTab =xmlDataExtTab.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");
//				
//				//XMLParser xmlParserExtTabDataRecord = new XMLParser(xmlDataExtTab);
//				NGXmlList objWorkList=xmlParserData.createList("Records", "Record");
//
//				HashMap<String, String> CheckGridDataMap = new HashMap<String, String>();
//											
//				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true))
//				{		
//					CheckGridDataMap.put("CIF_ID", objWorkList.getVal("CIF"));
//					CheckGridDataMap.put("FATCA_Tin_Number", objWorkList.getVal("FATCA_Tin_Number"));
//					CheckGridDataMap.put("Tin_reason", objWorkList.getVal("Tin_reason"));
//					CheckGridDataMap.put("FinalTAI", objWorkList.getVal("FinalTAI"));
//					CheckGridDataMap.put("nationality", objWorkList.getVal("nationality"));
//					CheckGridDataMap.put("FirstName", objWorkList.getVal("FirstName"));
//					CheckGridDataMap.put("MiddleName", objWorkList.getVal("MiddleName"));
//					CheckGridDataMap.put("LastName", objWorkList.getVal("LastName"));
//					CheckGridDataMap.put("EFR_NSTP", objWorkList.getVal("EFR_NSTP"));
//					
//		
//					for(Map.Entry<String, String> map : CheckGridDataMap.entrySet())
//					{
//						logger.debug("CheckGridDataMap map key: " +map.getKey()+" map value :"+map.getValue());
//					}
//					
//					
//						logger.debug("WINAME : "+processInstanceID);
//
//						//String integrationStatus=CIFUpdateCall(CommonConnection.getCabinetName(),CommonConnection.getUsername(),sessionId, CommonConnection.getJTSIP(),
//								//CommonConnection.getJTSPort(),processInstanceID,ws_name,integrationWaitTime,socket_connection_timeout, socketDetailsMap, CheckGridDataMap);
//
////						logger.debug("CIF Update integrationStatus: " +integrationStatus);
////						String statuses [] = integrationStatus.split("~");
////						if(statuses[0].equalsIgnoreCase("0000"))
////						{
////							FinalStatus = "Success";
////							return FinalStatus;
////						} 
////						else
////						{
////							FinalStatus = "Failure~ For CIF: "+CheckGridDataMap.get("CIF_ID")+"~ MsgStatus: "+statuses[1]+"~ MsgId: "+statuses[2];
////							return FinalStatus;
////						}	
//					
//				}
//			
//			}
//			else
//			{
//				FinalStatus = "Failure";
//			}
//
//
//		}
//		catch(Exception e)
//		{
//			return "Exception";
//		}
//		return FinalStatus;
//	}
	public String generate_Document_Customer_Consent(String pdfName, String processInstanceID, String sessionId)
	throws IOException, Exception {
		
		String attrbList = "";
		String Output = "";
		logger.debug("Inside the generate_template Method: ");
		
		String prop_file_loc = System.getProperty("user.dir") + System.getProperty("file.separator") + "ConfigFiles"
		+ System.getProperty("file.separator") + "DCC_CAMGen_Config.properties";
		logger.debug("prop_file_loc: " + prop_file_loc);
		
		File file = new File(prop_file_loc);
		FileInputStream fileInput = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(fileInput);
		fileInput.close();
		
		String gtIP = properties.getProperty("gtIP");
		logger.debug("gtIP: " + gtIP);
		
		String gtPortProperty = properties.getProperty("gtPort");
		logger.debug("gtPortProperty: " + gtPortProperty);
		
		int gtPort = Integer.parseInt(gtPortProperty);
		logger.debug("gtPort: " + gtPort);
		
		// for current date time 
		Date d = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String CurrentDateTime = dateFormat.format(d);
		
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
		String wiCreatedDate = dateFormat1.format(d);
		
	
		String tbQuery = "select a.Final_Limit as Amount,a.Nationality,a.EmiratesID,"+
				"a.ProspectID,a.DOB,b.TIN,a.WINAME,a.WICreatedDate as Prospect_Creation_Date,"+
				"CONCAT(a.FirstName,' ',a.MiddleName,' ',a.LastName) AS CustomerName "+
				"from NG_DPL_EXTTABLE a with (NOLOCK) inner join  NG_DPL_GR_FATCA_CRS_details b with (NOLOCK) on a.WINAME=b.WIName  where a.WIName ='"
					+ processInstanceID + "'"; 
		// second for normal query
		logger.debug("tbQuery : " + tbQuery);
		
		String extTabDataIPXML = CommonMethods.apSelectWithColumnNames(tbQuery, CommonConnection.getCabinetName(),sessionId);
		logger.debug("extTabDataIPXML template: " + extTabDataIPXML);
		String extTabDataOPXML = CommonMethods.WFNGExecute(extTabDataIPXML, CommonConnection.getJTSIP(),
				CommonConnection.getJTSPort(), 1);
		logger.debug("extTabDataOPXML template: " + extTabDataOPXML);

		XMLParser xmlParserData = new XMLParser(extTabDataOPXML);
		//logger.debug("xmlParserData template: " + xmlParserData);
		
		String strMainCode = xmlParserData.getValueOf("MainCode");
		logger.debug("apSelectWithColumnNames for main code: "+strMainCode);
		
		String RetrievedCount = xmlParserData.getValueOf("TotalRetrieved");
		logger.debug("RetrievedCount for apSelectWithColumnNames Call for retr: "+RetrievedCount);

		//if condition
		try {
			if ("0".equalsIgnoreCase(strMainCode) && !(RetrievedCount==null || "".equalsIgnoreCase(RetrievedCount)) && Integer.parseInt(RetrievedCount) >0){
				String CUSTOMERNAMETemplate = xmlParserData.getValueOf("CUSTOMERNAME");
				String NationalityTemplate = xmlParserData.getValueOf("Nationality");
				String EmirateIDTemplate = xmlParserData.getValueOf("EmiratesID");
				//String Card_ProductTemplate = xmlParserDataCardDescription.getValueOf("card_type_desc");
				String WI_Creation_date = xmlParserData.getValueOf("Prospect_Creation_Date");
				String Date_Of_Birth = xmlParserData.getValueOf("dob");
				String Tin_Number = xmlParserData.getValueOf("TIN");			
				//String preferred_address = xmlParserDataMRBH.getValueOf("Address_Type");
				
//				logger.debug("pref_add EXT TABLE "+pref_add);
//				logger.debug("preferred_address ADD GR "+preferred_address);
				
				// Hritik - PDSC-978
				String MrbQ= "select top 1 HouseApartmentNo,BuildingApartmentName,StreetLocation,CityTown AS City,PostcodeZipCode AS PO_Box_Address,AddressType from NG_DPL_GR_DemographicDetails with(nolock) " +
						"where winame = '" + processInstanceID +"'";
				
				logger.debug("MrbQ : " + MrbQ);
				String extTabDataIPXMLMrbQ = CommonMethods.apSelectWithColumnNames(MrbQ, CommonConnection.getCabinetName(),sessionId);
				logger.debug("extTabDataIPXMLMrbQ : " + extTabDataIPXMLMrbQ);
				
				String extTabDataOPXMLMrbQ = CommonMethods.WFNGExecute(extTabDataIPXMLMrbQ, CommonConnection.getJTSIP(),CommonConnection.getJTSPort(), 1);
				logger.debug("extTabDataOPXMLMrbQ template: " + extTabDataOPXMLMrbQ);
				
				XMLParser xmlParserDataMrbQ = new XMLParser(extTabDataOPXMLMrbQ);
				
				String PO_Box  = "";
				String PO_Box_Other = "";
				String Emirates_oF_residence = "";
				String Emirates_oF_residence_Other = "";
				String Address_Line1 = "";
				String Address_Line2 = "";
				String Address_Line3 = "";
				String Address_Line_Othetr = "";
				String Address_Line_Other2 = "";
				String  Address_Line_Other3 = "";
				String city ="";
				String State = "";
				String city_Other ="";
				String State_Other = "";
				String Country  = "";
				String Country_Other = "";
				String portal_no="";
				String HouseApartmentNo="";
				String addressType="";
				//Added by kamran 13012023
				String city_emirates = "";
				//Added by Kamran 09052023
				String Wi_No = xmlParserData.getValueOf("WIName");
				
				String PO_Box_Mrb="";
				String Emirate_Mrb="";
				String Street="";
				String BuildingApartmentName="";
				PO_Box_Mrb= xmlParserDataMrbQ.getValueOf("PO_Box_Address");
				Emirate_Mrb= xmlParserDataMrbQ.getValueOf("City");
				
				city= xmlParserDataMrbQ.getValueOf("City");
				HouseApartmentNo=xmlParserDataMrbQ.getValueOf("HouseApartmentNo");
				Street= xmlParserDataMrbQ.getValueOf("StreetLocation");
				BuildingApartmentName= xmlParserDataMrbQ.getValueOf("BuildingApartmentName");
				
				Address_Line1=HouseApartmentNo+" "+Street+" "+BuildingApartmentName;
				Address_Line2=city+" "+PO_Box_Mrb;
				
				addressType= xmlParserDataMrbQ.getValueOf("AddressType");
				
				attrbList += "&<CurrentDate>&" + CurrentDateTime+"@10";
				attrbList += "&<CustomerName>&" + CUSTOMERNAMETemplate+"@10";
				attrbList += "&<Nationality>&" + NationalityTemplate+"@10";
//				
				attrbList += "&<Date>&" + CurrentDateTime+"@10";
				attrbList += "&<Date_Islamic>&" + CurrentDateTime+"@10";
				
				attrbList += "&<WI_NO>&" + Wi_No+"@10";
				attrbList += "&<Customer_Name>&" + CUSTOMERNAMETemplate+"@10";				
				attrbList += "&<Customer_Name_Islamic>&" + CUSTOMERNAMETemplate+"@10";
				attrbList += "&<EID_Number>&" + EmirateIDTemplate+"@10";
				attrbList += "&<EID_Number_Islamic>&" + EmirateIDTemplate+"@10";
				//attrbList += "&<Card_Name>&" + Card_ProductTemplate+"@10";
				//attrbList += "&<Card_Name_Islamic>&" + Card_ProductTemplate+"@10";
				attrbList += "&<Nationality>&" + NationalityTemplate+"@10";
				attrbList += "&<Nationality_Islamic>&" + NationalityTemplate+"@10";
				
				
				attrbList += "&<Date_Floor_Limit>&" + wiCreatedDate+"@10";
				attrbList += "&<Customer_Name_OCR>&" + CUSTOMERNAMETemplate+"@10"; 
				// if condition for getting the pob and other details  for preffered address
				if ("Residence".equalsIgnoreCase(addressType)) {	
					attrbList += "&<PO_Box>&" + PO_Box+"@10";
					attrbList += "&<Address>&" + city_emirates+"@10";
				} else {	
					attrbList += "&<PO_Box>&" + PO_Box_Other+"@10";
					attrbList += "&<Address>&" + city_emirates+"@10";
				}
				
				// attribute for W8
				attrbList += "&<Customer_Name_W8>&" + CUSTOMERNAMETemplate+"@10";
				attrbList += "&<Nationality_desc_W8>&" + NationalityTemplate+"@10";
				attrbList += "&<Date_Of_Birth_W8>&" + Date_Of_Birth+"@10";
				attrbList += "&<Current_Date_W8>&" + CurrentDateTime+"@10";
				attrbList += "&<Tin_Number_W8>&" + Tin_Number+"@10";
				
				// if condition for getting the address details  for preffered address
				if ("Residence".equalsIgnoreCase(addressType)) {	
					attrbList += "&<Home_Country_Address_W8>&" + Address_Line1+"@10";
					attrbList += "&<Home_country_City_W8>&" + city+"@10";
					attrbList += "&<Home_Country_W8>&" + Country+"@10";		
				} else {	
					attrbList += "&<Home_Country_Address_W8>&" + Address_Line1+"@10";
					attrbList += "&<Home_country_City_W8>&" + city+"@10";
					attrbList += "&<Home_Country_W8>&" + Country_Other+"@10";
				}
				// attribute for w9
				attrbList += "&<Customer_Name_W9>&" + CUSTOMERNAMETemplate+"@10";
				attrbList += "&<Current_Date_W9>&" + CurrentDateTime+"@10";
				attrbList += "&<Tin_Number_W9>&" + Tin_Number+"@10";
				
				if ("Residence".equalsIgnoreCase(addressType)){
					attrbList += "&<StreetAptNumber>&" + Address_Line1+"@10";
					attrbList += "&<CityTownPinCode>&" + Address_Line2+"@10";		
				} else{
					attrbList += "&<StreetAptNumber>&" + Address_Line1+"@10";
					attrbList += "&<CityTownPinCode>&" + Address_Line2+"@10";
				}
				
				try{
					attrbList += "&<portal_ref>&" + xmlParserData.getValueOf("ProspectId")+"@10";
					attrbList += "&<customer_name>&" + CUSTOMERNAMETemplate+"@10";
					Date date = new Date();
					SimpleDateFormat mmddyyyy = new SimpleDateFormat("dd/MM/yyyy");
					String today = mmddyyyy.format(date);
					attrbList = attrbList+"&<date_today>&"+today.replaceAll("/", "")+"@10";
					String amountStr =xmlParserData.getValueOf("Amount");
					if (amountStr==null || "".equalsIgnoreCase(amountStr) || "null".equalsIgnoreCase(amountStr)){
						attrbList =attrbList+"&<Amount>&"+"0@10";
						attrbList =attrbList+"&<AmountInWords>&"+"ZERO";
					}

					else{
						String number = xmlParserData.getValueOf("Amount");
						double amount = Double.parseDouble(number);
						DecimalFormat formatter = new DecimalFormat("#,###.00");
						number = formatter.format(amount);
						System.out.println("Converted Amount is "+number);
						attrbList =attrbList+"&<Amount>&"+number;
						if(amountStr.contains("."))
							amountStr=amountStr.substring(0,amountStr.indexOf("."));
						if(!amountStr.matches("[0-9]+")){
							logger.debug("Not a valid amount--" + amountStr);
							attrbList =attrbList+"&<AmountInWords>&"+amountStr+" DIRHAMS ";
						}
						else
						attrbList =attrbList+"&<AmountInWords>&"+numberToWord(Integer.parseInt(amountStr))+" DIRHAMS "+"@10";		
					}
				}
				catch(Exception e){
					logger.debug("Exception occured while converting amount into no" + e.getMessage());
					System.out.println("Converted Amount is "+e.getMessage());
				}
				
				logger.debug("attrbList" + attrbList);
				logger.debug("doc list to be genrated" + pdfName);
				
				String docList[] = pdfName.split("~");
				String finalDocList="";
				for(String tempName:docList){
					if(!tempName.equals("")){
						logger.debug("Temp Name-- "+tempName); 
						
						
//						if("MRBH_Agency_Agreement".equalsIgnoreCase(tempName)){
//							attrbList += "&<PO_Box>&" + PO_Box_Mrb+"@10";
//							attrbList += "&<Address>&" + Emirate_Mrb+"@10";
//							logger.debug("attrbList MRBH_Agency_Agreement: " + attrbList);
//						}
						
						Output= makeSocketCall(attrbList, processInstanceID, tempName, sessionId, gtIP, gtPort, "", "", "",portal_no);
						logger.debug("output for template "+tempName+":-" + Output);
						if(Output==null || !Output.contains("Success~")){
							return "Error~"+tempName;
						}
						else if(Output!=null && Output.contains("Success~")){
							String str[] = Output.split("~");
							if(str.length>1)
							{
								String addDocXML = str[1];
								XMLParser xmlParseraddDocXML= new XMLParser(addDocXML);
								String docTypeName = xmlParseraddDocXML.getValueOf("DocumentName");
								String ISIndex = xmlParseraddDocXML.getValueOf("ISIndex");
								if(ISIndex!=null && ISIndex.contains("#")){
									ISIndex=ISIndex.substring(0,ISIndex.indexOf("#"));
								}
								if(docTypeName!=null){
									if("".equalsIgnoreCase(finalDocList))
										finalDocList=docTypeName;
									else
										finalDocList=finalDocList+"\n"+docTypeName;
								}
							}
						}
					}
					else{
						
					}
				}
				String columnNames="GeneratedDocumentList";
				String columnValues="'"+finalDocList+"'";
				String sWhereClause = "WINAME='" + processInstanceID + "'";
		    	String tableName = "NG_DPL_EXTTABLE";
		        String inputXML = CommonMethods.apUpdateInput(CommonConnection.getCabinetName(), sessionId, tableName, columnNames, columnValues, sWhereClause);
		        logger.debug("Input XML for apUpdateInput for " + tableName + " Table : " + inputXML);
		        String outputXml = CommonMethods.WFNGExecute(inputXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 1);
		        logger.debug("Output XML for apUpdateInput for " + tableName + " Table : " + outputXml);
		        XMLParser sXMLParserChild = new XMLParser(outputXml);
		        String StrMainCode = sXMLParserChild.getValueOf("MainCode");
		        //String RetStatus = null;
		        if (StrMainCode.equals("0")){
		        	return "Success";
		        }
		        else{
		        	logger.debug("Error in Executing apUpdateInput for genearted documents: " + outputXml);
		        	return "Error~DocListupdate";
		        }
			}
			else{
				logger.debug("main code is not 0 try again :");	
			}
			
		}
		catch (Exception e){
			logger.debug("Exception: "+e.getMessage());
		}
	return Output;
	}
	public String makeSocketCall(String argumentString, String wi_name, String docName, String sessionId, String gtIP,
			int gtPort,String prequired, String pvalue,String userEmail,String portal_no) {
		String socketParams = argumentString + "~" + wi_name + "~" + docName + "~" + sessionId+"~"+prequired+"~"+pvalue+"~"+userEmail+"~"+portal_no;

		System.out.println("socketParams -- " + socketParams);
		logger.debug("socketParams:-\n" + socketParams);

		Socket template_socket = null;
		DataOutputStream template_dout = null;
		DataInputStream template_in = null;
		String result = "";
		try {
			// Socket write code started
			template_socket = new Socket(gtIP, gtPort);
			logger.debug("template_socket" + template_socket);

			template_dout = new DataOutputStream(template_socket.getOutputStream());
			logger.debug("template_dout" + template_dout);

			if (socketParams != null && socketParams.length() > 0) {
				int outPut_len = socketParams.getBytes("UTF-8").length;
				logger.debug("outPut_len" + outPut_len);
				// CreditCard.mLogger.info("Final XML output len:
				// "+outPut_len +
				// "");
				socketParams = outPut_len + "##8##;" + socketParams;
				logger.debug("socketParams--" + socketParams);
				// CreditCard.mLogger.info("MqInputRequest"+"Input Request
				// Bytes : "+
				// mqInputRequest.getBytes("UTF-16LE"));

				template_dout.write(socketParams.getBytes("UTF-8"));
				template_dout.flush();
			} else {
				notify();
			}
			// Socket write code ended and read code started
			template_socket.setSoTimeout(60 * 1000);
			template_in = new DataInputStream(new BufferedInputStream(template_socket.getInputStream()));
			byte[] readBuffer = new byte[50000];
			int num = template_in.read(readBuffer);
			if (num > 0) {
				byte[] arrayBytes = new byte[num];
				System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
				result = new String(arrayBytes, "UTF-8");
				logger.debug("result--" + result);
			}
		}

		catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (Exception io) {
			io.printStackTrace();
		} finally {
			try {
				if (template_dout != null) {
					template_dout.close();
					template_dout = null;
				}
				if (template_in != null) {
					template_in.close();
					template_in = null;
				}
				if (template_socket != null) {
					if (!template_socket.isClosed()) {
						template_socket.close();
					}
					template_socket = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private String numberToWord(Integer number) {
		try{

			// variable to hold string representation of number 
			String words = "";
			String unitsArray[] = { "zero", "one", "two", "three", "four", "five", "six", 
					"seven", "eight", "nine", "ten", "eleven", "twelve",
					"thirteen", "fourteen", "fifteen", "sixteen", "seventeen", 
					"eighteen", "nineteen" };
			String tensArray[] = { "zero", "ten", "twenty", "thirty", "forty", "fifty","sixty", "seventy", "eighty", "ninety" };

			if (number == 0) {
				return "zero";
			}
			// add minus before conversion if the number is less than 0
			if (number < 0) { 
				// convert the number to a string
				String numberStr = "" + number; 
				// remove minus before the number 
				numberStr = numberStr.substring(1); 
				// add minus before the number and convert the rest of number 
				return "minus " + numberToWord(Integer.parseInt(numberStr)); 
			} 
			// check if number is divisible by 1 million
			if ((number / 1000000) > 0) {
				words += numberToWord(number / 1000000) + " million ";
				number %= 1000000;
			}
			// check if number is divisible by 1 thousand
			if ((number / 1000) > 0) {
				words += numberToWord(number / 1000) + " thousand ";
				number %= 1000;
			}
			// check if number is divisible by 1 hundred
			if ((number / 100) > 0) {
				words += numberToWord(number / 100) + " hundred ";
				number %= 100;
			}
			if (number > 0) {
				// check if number is within teens
				if (number < 20) { 
					// fetch the appropriate value from unit array
					words += unitsArray[number];
				} else { 
					// fetch the appropriate value from tens array
					words += tensArray[number / 10]; 
					if ((number % 10) > 0) {
						words += "-" + unitsArray[number % 10];
					}  
				}
			}
			return words.toUpperCase();
		}
		catch(Exception e){
			logger.debug("Exception occured while converting amount in numberToWord method" + e.getMessage());
			System.out.println("Converted Amount is numberToWord method "+e.getMessage());
			return "";
		}
	}

	
}
