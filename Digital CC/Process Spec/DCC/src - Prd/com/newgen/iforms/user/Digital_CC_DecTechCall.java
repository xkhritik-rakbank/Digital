package com.newgen.iforms.user;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.newgen.iforms.custom.IFormReference;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.wfdesktop.xmlapi.WFXmlResponse;

public class Digital_CC_DecTechCall extends Digital_CC_Common {

	LinkedHashMap<String, String> executeXMLMapMain = new LinkedHashMap<String, String>();
	public static String XMLLOG_HISTORY = "NG_DCC_XMLLOG_HISTORY";
	static int dds_count = 0;
	static int cheque_count = 0;

	public String onevent(IFormReference iformObj, String control, String StringData) throws IOException {
		String wiName = getWorkitemName(iformObj);
		String WSNAME = getActivityName(iformObj);
		String returnValue = "";
		String MQ_response = "";
		String cabinetName = getCabinetName(iformObj);
		/*
		 * String wi_name = getWorkitemName(iformObj); String ws_name =
		 * getActivityName(iformObj); String sessionID = getSessionId(iformObj);
		 * String userName = getUserName(iformObj);
		 */
		String decisionValue = "";
		String attributesTag = "";
		String socketServerIP = "";
		String socketServerPort = "";

		MQ_response = MQ_connection_response(iformObj, control, StringData);

		// return MQ_response;
		if (MQ_response.indexOf("<MessageStatus>") != -1)
			returnValue = MQ_response.substring(
					MQ_response.indexOf("<MessageStatus>") + "</MessageStatus>".length() - 1,
					MQ_response.indexOf("</MessageStatus>"));

		if (MQ_response.contains("INVALID SESSION"))
			returnValue = "INVALID SESSION";

		if ("Success".equalsIgnoreCase(returnValue))
			// returnValue = MQ_response;
			returnValue = "DECTECH CALL SUCCESS";
		// save response data start
		XMLParser xmlParserSocketDetails = new XMLParser(MQ_response);
		Digital_CC.mLogger.debug(" xmlParserSocketDetails : " + xmlParserSocketDetails);
		String SystemErrorCode = xmlParserSocketDetails.getValueOf("SystemErrorCode");
		Digital_CC.mLogger.debug("SystemErrorCode : " + SystemErrorCode + " for WI: " + wiName);
		String SystemErrorMessage = xmlParserSocketDetails.getValueOf("SystemErrorMessage");
		Digital_CC.mLogger.debug("SystemErrorMessage : " + SystemErrorMessage + " for WI: " + wiName);
		if (SystemErrorCode != null && !SystemErrorCode.equals("")) {
			decisionValue = "Failed";
			Digital_CC.mLogger.debug("Decision in else : " + decisionValue);
			attributesTag = "<Decision>" + decisionValue + "</Decision>";
		} else {
			decisionValue = "Success";
			Digital_CC.mLogger.debug("Decision in success: " + decisionValue);
			attributesTag = "<Decision>" + decisionValue + "</Decision>";
		}

		// all the below fields are in <Application> tag
		String Output_Decision = xmlParserSocketDetails.getValueOf("Output_Decision");
		Digital_CC.mLogger.debug("Output_Decision: " + Output_Decision + "WI: " + wiName);
		String Output_NSTP = xmlParserSocketDetails.getValueOf("Output_NSTP");
		Digital_CC.mLogger.debug("Output_NSTP: " + Output_NSTP + "WI: " + wiName);
		String Output_NSTP_Reason = xmlParserSocketDetails.getValueOf("Output_NSTP_Reason");
		Digital_CC.mLogger.debug("Output_NSTP_Reason: " + Output_NSTP_Reason + "WI: " + wiName);
		String Output_TAI = xmlParserSocketDetails.getValueOf("Output_TAI");
		Digital_CC.mLogger.debug("Output_TAI: " + Output_TAI + "WI: " + wiName);
		String Output_Final_DBR = xmlParserSocketDetails.getValueOf("Output_Final_DBR");
		Digital_CC.mLogger.debug("Output_Final_DBR: " + Output_Final_DBR + "WI: " + wiName);

		// final limit
		String Output_Eligible_Amount = xmlParserSocketDetails.getValueOf("Output_Eligible_Amount");
		Digital_CC.mLogger.debug("Output_Eligible_Amount: " + Output_Eligible_Amount + "WI: " + wiName);
		// Output_Affordable_Ratio
		String Output_Affordable_Ratio = xmlParserSocketDetails.getValueOf("Output_Affordable_Ratio");
		Digital_CC.mLogger.debug("Output_Affordable_Ratio: " + Output_Affordable_Ratio + "WI: " + wiName);
		// Output_Delegation_Authority
		String Output_Delegation_Authority = xmlParserSocketDetails.getValueOf("Output_Delegation_Authority");
		Digital_CC.mLogger.debug("Output_Delegation_Authority: " + Output_Delegation_Authority + "WI: " + wiName);

		String Output_Age = xmlParserSocketDetails.getValueOf("Output_Age");
		Digital_CC.mLogger.debug("Output_Age: "+Output_Age+ "WI: "+wiName);
		
		String Output_NoOf_AECBHistory = xmlParserSocketDetails.getValueOf("Output_NoOf_AECBHistory");
		Digital_CC.mLogger.debug("Output_NoOf_AECBHistory: "+Output_NoOf_AECBHistory+ "WI: "+wiName);
		
		Output_Age= validateValue(Output_Age);
		if (Output_Age.length() > 5){
			Output_Age=Output_Age.substring(0,Output_Age.lastIndexOf("."));
		}
		
		String is_stp = "";
		if (Output_NSTP != null && !"".equals(Output_NSTP)) {
			if (Output_NSTP.equals("Y"))
				is_stp = "N";
			else if (Output_NSTP.equals("N"))
				is_stp = "Y";
		}
		//[{"Reasons":"Name mismatch identified between statement and EFR"},{"Reasons":"Mismatch in Currency"}]
		//Name mismatch identified between statement and EFR,Mismatch in Currency
		
		String final_NSTP_Reason ="";
		if(Output_NSTP_Reason!=null && !"".equalsIgnoreCase(Output_NSTP_Reason))
		{
			String reasons[]=Output_NSTP_Reason.split(",");
			for(int i =0;i<reasons.length;i++)
			{
				String temp = reasons[i];
				if(i==0)
					final_NSTP_Reason += temp.substring(temp.indexOf(":")+2,temp.indexOf("}")-1);
				else
					final_NSTP_Reason += "~"+temp.substring(temp.indexOf(":")+2,temp.indexOf("}")-1);
			}
		}
		if (final_NSTP_Reason.contains("~")){
			final_NSTP_Reason = final_NSTP_Reason.replace("~", ",");
		}

		Digital_CC.mLogger.debug("Output_NSTP_Reason: " + Output_NSTP_Reason + "WI: " + wiName);
		Digital_CC.mLogger.debug("Output_NSTP_Reason: " + final_NSTP_Reason + "WI: " + wiName);

		iformObj.setValue("Is_STP", is_stp);
		iformObj.setValue("Dectech_Decision", Output_Decision);
		iformObj.setValue("FinalDBR", Output_Final_DBR);
		iformObj.setValue("FinalTAI", Output_TAI);
		iformObj.setValue("INCOME_AT_RULE_ENGINE", Output_TAI);
		iformObj.setValue("Dectech_Flag", "Y");
		iformObj.setValue("Non_STP_reason", final_NSTP_Reason);
		iformObj.setValue("Final_Limit", Output_Eligible_Amount);
		//Deepak changes done to populate Underwriting_Limit with updated value
		iformObj.setValue("Underwriting_Limit", Output_Eligible_Amount);
		iformObj.setValue("DBR_lifeStyle_expenses", Output_Affordable_Ratio);
		iformObj.setValue("delegation_authority", Output_Delegation_Authority);
		iformObj.setValue("AECB_history", Output_NoOf_AECBHistory);
		iformObj.setValue("Age", Output_Age);

		try {

			JSONArray jsonArray = new JSONArray();
			String PM_Reason_Codes_Data[] = new String[2];
			String ProcessManagerResponse = xmlParserSocketDetails.getValueOf("ProcessManagerResponse");

			Digital_CC.mLogger.debug("ProcessManagerResponse : " + ProcessManagerResponse);
			for (int j = 0; j < PM_Reason_Codes_Data.length; j++) {

				String str_PM_Reason_Codes_Data = xmlParserSocketDetails.getNextValueOf("PM_Reason_Codes_Data");

				Digital_CC.mLogger.debug("str_PM_Reason_Codes_Data : " + str_PM_Reason_Codes_Data);
				String PM_Reason_Codes[] = getTagValues(str_PM_Reason_Codes_Data, "PM_Reason_Codes").split("`");
				Digital_CC.mLogger.debug("length of PM_Reason_Codes" + PM_Reason_Codes.length);

				for (int i = 0; i < PM_Reason_Codes.length; i++) {
					JSONObject obj = new JSONObject();
					String Reason_Code = getTagValues(PM_Reason_Codes[i], "Reason_Code");
					Digital_CC.mLogger.debug("Reason_Code : " + Reason_Code);

					String Reason_Description = getTagValues(PM_Reason_Codes[i], "Reason_Description");
					//06122022 - Kamran
					if(Reason_Description.contains("&lt;") || Reason_Description.contains("&amp;") || Reason_Description.contains("&gt;")){
						Reason_Description = Reason_Description.replace("&lt;","<");
						Reason_Description = Reason_Description.replace("&gt;",">");
						Reason_Description = Reason_Description.replace("&amp;gt;","&>");
						Reason_Description = Reason_Description.replaceAll("&amp;","&");
					}
					//End
					Digital_CC.mLogger.debug("Reason_Description : " + Reason_Description);

					if ("A999".equalsIgnoreCase(Reason_Code)) {
						continue;
					}

					obj.put("Deviation Code", Reason_Code);
					obj.put("Deviation Description", Reason_Description);

					if (jsonArray.contains(obj)) {
						continue;
					}

					jsonArray.add(obj);

				}

			}

			Digital_CC.mLogger.debug("jsonArray : " + jsonArray);
			iformObj.addDataToGrid("Deviation_Desc_Grid", jsonArray);

		} catch (Exception e) {
			Digital_CC.mLogger.debug("error --" + e.getMessage());
		}

		// cheque dds code

		try {
			JSONArray jsonArray2 = new JSONArray();
			jsonArray2 = iformObj.getDataFromGrid("AdditionChequeDDS");
			Digital_CC.mLogger.debug("AdditionChequeDDS grid values : " + jsonArray2);
			String returnType_Data[] = new String[jsonArray2.size()];
			for (int j = 0; j < jsonArray2.size(); j++) {
				String returnTypeDDS = iformObj.getTableCellValue("AdditionChequeDDS", j, 0);
				returnType_Data[j] = returnTypeDDS;
			}
			
			Digital_CC.mLogger.debug("size of dds cheque grid : " + jsonArray2.size());
			for (int i = 0; i < returnType_Data.length; i++) {
				Digital_CC.mLogger.debug("list of return type values : " + returnType_Data[i]);
			}
			
			int count = 0;
			for (int i = 0; i < jsonArray2.size(); i++) {
				if ( returnType_Data[i].equalsIgnoreCase("DDS") ) {
					count++;
				}
			}

			 this.dds_count = count;
			 this.cheque_count = jsonArray2.size() - count;
			Digital_CC.mLogger.debug("count : " + count);
			Digital_CC.mLogger.debug("dds_count : " + dds_count);
			Digital_CC.mLogger.debug("cheque_count : " + cheque_count);
		} catch (Exception ex) {
			Digital_CC.mLogger.debug("error --" + ex.getMessage());
		}

		// cheque dds code

		Digital_CC.mLogger.debug("Dectech Response insert in exttable successfull... ");

		/*
		 * String columnNames =
		 * "Is_STP, Dectech_Decision, Non_STP_reason, FinalDBR, FinalTAI, Dectech_Flag"
		 * ; String columnValues = "'" + is_stp + "','"+ Output_Decision +"','"+
		 * Output_NSTP_Reason +"','"+ Output_Final_DBR +"','"+ Output_TAI
		 * +"','Y'"; String sWhereClause = "WI_NAME='" + wiName + "'"; String
		 * tableName = "NG_DCC_EXTTABLE"; String inputXML =
		 * Digital_CC_Common.apUpdateInput(getCabinetName(iformObj),
		 * getSessionId(iformObj), tableName, columnNames, columnValues,
		 * sWhereClause); Digital_CC.mLogger.debug(
		 * "Input XML for apUpdateInput for " + tableName + " Table : " +
		 * inputXML); String outputXml = Digital_CC_Common.WFNGExecute(inputXML,
		 * socketServerIP, socketServerPort, 1); Digital_CC.mLogger.debug(
		 * "Output XML for apUpdateInput for " + tableName + " Table : " +
		 * outputXml); XMLParser sXMLParserChild = new XMLParser(outputXml);
		 * String StrMainCode = sXMLParserChild.getValueOf("MainCode");
		 */

		// save response data end

		return returnValue;

	}

	public String MQ_connection_response(IFormReference iformObj, String control, String Data) {
		Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
				+ ", Inside MQ_connection_response function for Digital CC Dectech Call");
		final IFormReference iFormOBJECT;
		final WDGeneralData wdgeneralObj;
		Socket socket = null;
		OutputStream out = null;
		InputStream socketInputStream = null;
		DataOutputStream dout = null;
		DataInputStream din = null;
		String mqOutputResponse = null;
		String mqOutputResponse1 = null;
		String mqInputRequest = null;
		String cabinetName = getCabinetName(iformObj);
		String wi_name = getWorkitemName(iformObj);
		String ws_name = getActivityName(iformObj);
		String userName = getUserName(iformObj);
		String socketServerIP;
		int socketServerPort;
		wdgeneralObj = iformObj.getObjGeneralData();
		String sessionID = wdgeneralObj.getM_strDMSSessionId();
		String CIFNumber = "";
		String CallName = "";
		StringBuilder finalXml = new StringBuilder();

		if (control.equals("Fetch_Manual_Dectech")) {
			java.util.Date d1 = new Date();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
			String DateExtra2 = sdf1.format(d1) + "+04:00";

			Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
					+ ", Inside Digital CC DecTechCall control--");
			CallName = "DECTECH";
			Digital_CC.mLogger.debug(
					"DECTECH Call - WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj));

			finalXml = new StringBuilder(
					"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
							+ "<soap:Header>\n" + "<ServiceId>CallProcessManager</ServiceId>\n"
							+ "<ServiceType>ProductEligibility</ServiceType>\n"
							+ "<ServiceProviderId>DECTECH</ServiceProviderId>\n"
							+ "<ServiceChannelId>CAS</ServiceChannelId>\n" + "<RequestID>CASTEST</RequestID>\n"
							+ "<TimeStampyyyymmddhhmmsss>" + DateExtra2 + "</TimeStampyyyymmddhhmmsss>\n"
							+ "<RequestLifeCycleStage>CallProcessManagerRequest</RequestLifeCycleStage>\n"
							+ "<MessageStatus>Success</MessageStatus>\n" + "</soap:Header>\n" + "<soap:Body>\n"
							+ "<CallProcessManager xmlns=\"http://tempuri.org/\">\n" + "<applicationXML>\n"
							+ DecTechInputBodyXml(iformObj, control, Data) + "</applicationXML> \n"
							+ "</CallProcessManager>\n" + "</soap:Body>\n" + "</soap:Envelope>");

			// finalXml =
			// finalXml.append(DecTechInputBodyXml(iformObj,control,Data));
			mqInputRequest = getMQInputXML(sessionID, cabinetName, wi_name, ws_name, userName, finalXml);
			Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
					+ ", mqInputRequest for DecTech call" + mqInputRequest);
		}

		try {

			Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
					+ ", userName " + userName);
			Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
					+ ", sessionID " + sessionID);

			String sMQuery = "SELECT SocketServerIP,SocketServerPort FROM NG_BPM_MQ_TABLE with (nolock) where ProcessName = 'DCC' and CallingSource = 'Form'";
			List<List<String>> outputMQXML = iformObj.getDataFromDB(sMQuery);
			// CreditCard.mLogger.info("$$outputgGridtXML "+ "sMQuery " +
			// sMQuery);
			if (!outputMQXML.isEmpty()) {
				// CreditCard.mLogger.info("$$outputgGridtXML "+
				// outputMQXML.get(0).get(0) + "," + outputMQXML.get(0).get(1));
				socketServerIP = outputMQXML.get(0).get(0);
				Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: "
						+ getActivityName(iformObj) + ", socketServerIP " + socketServerIP);
				socketServerPort = Integer.parseInt(outputMQXML.get(0).get(1));
				Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: "
						+ getActivityName(iformObj) + ", socketServerPort " + socketServerPort);
				if (!("".equalsIgnoreCase(socketServerIP) && socketServerIP == null && socketServerPort == 0)) {
					Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: "
							+ getActivityName(iformObj) + ", Inside serverIP Port " + socketServerPort
							+ "-socketServerIP-" + socketServerIP);
					socket = new Socket(socketServerIP, socketServerPort);
					// new Code added by Deepak to set connection timeout
					int connection_timeout = 60;
					try {
						connection_timeout = 70;
					} catch (Exception e) {
						connection_timeout = 60;
					}

					socket.setSoTimeout(connection_timeout * 1000);
					out = socket.getOutputStream();
					socketInputStream = socket.getInputStream();
					dout = new DataOutputStream(out);
					din = new DataInputStream(socketInputStream);
					Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: "
							+ getActivityName(iformObj) + ", dout " + dout);
					Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: "
							+ getActivityName(iformObj) + ", din " + din);
					mqOutputResponse = "";

					if (mqInputRequest != null && mqInputRequest.length() > 0) {
						int outPut_len = mqInputRequest.getBytes("UTF-16LE").length;
						Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: "
								+ getActivityName(iformObj) + ", Final XML output len: " + outPut_len + "");
						mqInputRequest = outPut_len + "##8##;" + mqInputRequest;
						Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: "
								+ getActivityName(iformObj) + ", MqInputRequest" + "Input Request Bytes : "
								+ mqInputRequest.getBytes("UTF-16LE"));
						dout.write(mqInputRequest.getBytes("UTF-16LE"));
						dout.flush();
					}

					byte[] readBuffer = new byte[500];
					int num = din.read(readBuffer);
					if (num > 0) {

						byte[] arrayBytes = new byte[num];
						System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
						mqOutputResponse = mqOutputResponse + new String(arrayBytes, "UTF-16LE");
						Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: "
								+ getActivityName(iformObj) + ", mqOutputResponse/message ID :  " + mqOutputResponse);

						mqOutputResponse = getOutWtthMessageID("DECTECH", iformObj, mqOutputResponse);

						if (mqOutputResponse.contains("&lt;")) {
							mqOutputResponse = mqOutputResponse.replaceAll("&lt;", "<");
							mqOutputResponse = mqOutputResponse.replaceAll("&gt;", ">");

						}
					}
					socket.close();
					return mqOutputResponse;

				} else {
					Digital_CC.mLogger
							.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
									+ ", SocketServerIp and SocketServerPort is not maintained " + "");
					Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: "
							+ getActivityName(iformObj) + ", SocketServerIp is not maintained " + socketServerIP);
					Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: "
							+ getActivityName(iformObj) + ",  SocketServerPort is not maintained " + socketServerPort);
					return "MQ details not maintained";
				}
			} else {
				Digital_CC.mLogger
						.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
								+ ", SOcket details are not maintained in NG_BPM_MQ_TABLE table" + "");
				return "MQ details not maintained";
			}

		} catch (Exception e) {
			Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
					+ ", Exception Occured Mq_connection_CC" + e.getStackTrace());
			return "";
		} finally {
			try {
				if (out != null) {

					out.close();
					out = null;
				}
				if (socketInputStream != null) {

					socketInputStream.close();
					socketInputStream = null;
				}
				if (dout != null) {

					dout.close();
					dout = null;
				}
				if (din != null) {

					din.close();
					din = null;
				}
				if (socket != null) {
					if (!socket.isClosed()) {
						socket.close();
					}
					socket = null;
				}
			} catch (Exception e) {

				Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: "
						+ getActivityName(iformObj) + ", Final Exception Occured Mq_connection_CC" + e.getStackTrace());

			}
		}
	}

	private static String getMQInputXML(String sessionID, String cabinetName, String wi_name, String ws_name,
			String userName, StringBuilder final_xml) {
		// FormContext.getCurrentInstance().getFormConfig();
		Digital_CC.mLogger.debug("inside getMQInputXML function");
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("<APMQPUTGET_Input>");
		strBuff.append("<SessionId>" + sessionID + "</SessionId>");
		strBuff.append("<EngineName>" + cabinetName + "</EngineName>");
		strBuff.append("<XMLHISTORY_TABLENAME>" + XMLLOG_HISTORY + "</XMLHISTORY_TABLENAME>");
		strBuff.append("<WI_NAME>" + wi_name + "</WI_NAME>");
		strBuff.append("<WS_NAME>" + ws_name + "</WS_NAME>");
		strBuff.append("<USER_NAME>" + userName + "</USER_NAME>");
		strBuff.append("<MQ_REQUEST_XML>");
		strBuff.append(final_xml);
		strBuff.append("</MQ_REQUEST_XML>");
		strBuff.append("</APMQPUTGET_Input>");
		return strBuff.toString();
	}

	public String getOutWtthMessageID(String callName, IFormReference iformObj, String message_ID) {
		String outputxml = "";
		try {
			Digital_CC.mLogger.debug("getOutWtthMessageID - callName :" + callName);

			String wi_name = getWorkitemName(iformObj);
			String str_query = "select OUTPUT_XML from " + XMLLOG_HISTORY + " with (nolock) where MESSAGE_ID ='"
					+ message_ID + "' and WI_NAME = '" + wi_name + "'";
			Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
					+ ", inside getOutWtthMessageID str_query: " + str_query);
			List<List<String>> result = iformObj.getDataFromDB(str_query);
			// below code added by nikhil 18/10 for Connection timeout
			String Integration_timeOut = "100";
			int Loop_wait_count = 10;
			try {
				Loop_wait_count = Integer.parseInt(Integration_timeOut);
			} catch (Exception ex) {
				Loop_wait_count = 10;
			}

			for (int Loop_count = 0; Loop_count < Loop_wait_count; Loop_count++) {
				if (result.size() > 0) {
					outputxml = result.get(0).get(0);
					break;
				} else {
					Thread.sleep(1000);
					result = iformObj.getDataFromDB(str_query);
				}
			}

			if ("".equalsIgnoreCase(outputxml)) {
				outputxml = "Error";
			}
			Digital_CC.mLogger.debug("This is output xml from DB");
			String outputxmlMasked = outputxml;
			/*
			 * Digital_CC.mLogger.debug("The output XML is "+outputxml);
			 * outputxmlMasked =
			 * maskXmlogBasedOnCallType(outputxmlMasked,callName);
			 * Digital_CC.mLogger.debug("Masked output XML is "
			 * +outputxmlMasked);
			 */
			Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
					+ ", getOutWtthMessageID" + outputxmlMasked);
		} catch (Exception e) {
			Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
					+ ", Exception BTurred in getOutWtthMessageID" + e.getMessage());
			Digital_CC.mLogger.debug("WINAME : " + getWorkitemName(iformObj) + ", WSNAME: " + getActivityName(iformObj)
					+ ", Exception BTurred in getOutWtthMessageID" + e.getStackTrace());
			outputxml = "Error";
		}
		return outputxml;
	}

	public String DecTechInputBodyXml(IFormReference iformObj, String control, String Data) {
		final WDGeneralData wdgeneralObj;
		String cabinetName = getCabinetName(iformObj);
		String sessionId = getSessionId(iformObj);
		String userName = getUserName(iformObj);
		wdgeneralObj = iformObj.getObjGeneralData();
		sessionId = wdgeneralObj.getM_strDMSSessionId();
		String jtsIp = wdgeneralObj.getM_strJTSIP();
		String jtsPort = wdgeneralObj.getM_strJTSPORT();
		String wi_name = getWorkitemName(iformObj);
		String socketServerIP;
		int socketServerPort;
		String CIFNumber = "";
		String CallName = "";
		//commented for producion
		//String applicationXML = "<![CDATA[<ProcessManagerRequest><Application><Channel>CC</Channel><CallType>PM</CallType><ApplicationNumber>Str_ApplicationNumber</ApplicationNumber><Request_From>IBPS_UW</Request_From></Application><ApplicationDetails><full_eligibility_availed>Str_full_eligibility_availed</full_eligibility_availed><product_type>Str_product_type</product_type><app_category>Str_app_category</app_category><requested_product>Str_requested_product</requested_product><requested_limit>Str_requested_limit</requested_limit><sub_product>Str_sub_product</sub_product><requested_card_product>Str_requested_card_product</requested_card_product><application_type>NEWE</application_type><interest_rate>Str_interest_rate</interest_rate><customer_type>Str_customer_type</customer_type><final_limit>Str_final_limit</final_limit><emi>Str_emi</emi><manual_deviation>Str_manual_deviation</manual_deviation><application_date>Str_application_date</application_date></ApplicationDetails><String_ApplicantDetails><String_InternalBureauData><ExternalBureauData><String_ExternalBureau><String_BouncedCheques><String_Utilization24months><String_History_24months><String_CourtCase><String_ExternalBureauAccountDetails><String_ExternalBureauSalaryDetails><String_ExternalBureauIndividualProducts><String_ExternalBureauPipelineProducts></ExternalBureauData><String_Perfios></ProcessManagerRequest>]]>";
		
		  String applicationXML = "<![CDATA[<ProcessManagerRequest><Application><Channel>CC</Channel><CallType>PM</CallType><ApplicationNumber>Str_ApplicationNumber</ApplicationNumber><Request_From>IBPS_UW</Request_From></Application><ApplicationDetails><full_eligibility_availed>Str_full_eligibility_availed</full_eligibility_availed><product_type>Str_product_type</product_type><app_category>Str_app_category</app_category><requested_product>Str_requested_product</requested_product><requested_limit>Str_requested_limit</requested_limit><sub_product>Str_sub_product</sub_product><requested_card_product>Str_requested_card_product</requested_card_product><application_type>NEWE</application_type><interest_rate>Str_interest_rate</interest_rate><customer_type>Str_customer_type</customer_type><final_limit>Str_final_limit</final_limit><emi>Str_emi</emi><manual_deviation>Str_manual_deviation</manual_deviation><application_date>Str_application_date</application_date></ApplicationDetails><String_ApplicantDetails><String_InternalBureauData><ExternalBureauData><String_ExternalBureau><String_BouncedCheques><String_Utilization24months><String_History_24months><String_CourtCase><String_ExternalBureauIndividualProducts><String_ExternalBureauPipelineProducts></ExternalBureauData><String_Perfios></ProcessManagerRequest>]]>";
		

		java.util.Date d1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String AppDate = sdf1.format(d1);
		String Process = "DCC";
		String DateExtra2 = sdf1.format(new Date()) + "+04:00";

		String ApplicationDetailsXML = "<Application><Channel>" + Process
				+ "</Channel><CallType>PM</CallType><ApplicationNumber>" + getWorkitemName(iformObj)
				+ "</ApplicationNumber><Request_From>BPM</Request_From><application_date>" + AppDate
				+ "</application_date></Application>";

		String DBQuery = "SELECT Wi_Name, Application_Type, uw_income, CIF, Product, Product_Desc, Sub_Product, Card_Product, CUSTOMERNAME, PassportNo, EmirateID, MobileNo, employercode, "
				+ "Employer_Name, EmploymentType, EmploymentType_Desc, email_id, Final_Limit,Underwriting_Limit, VIPFlag, Title, Title_Desc, FirstName, MiddleName, LastName, dob, Age, Nationality, Nationality_Desc, Designation, Designation_Desc, Cust_Decl_Salary, "
				+ "Prospect_id, FinalDBR, FinalTAI, Passport_expiry, Gender_Code, Gender_Code, IndusSeg, IndusSeg_Desc, EligibleCardProduct, "
				+ "EligibleCardProduct_Desc, Date_Of_Joining, Selected_Card_Type, Prospect_Creation_Date, FIRCO_Flag, Visa_Expiry, "
				+ "Emirates_Visa, EmID_Expiry, Visa_Sponsor_Name,GCC_National, No_earning_members, Earning_members,"
				+ "Dependents, Cust_Decl_Salary,Net_Salary1,Net_Salary2,Net_Salary3,"
				+ "Net_Salary1,SUBSTRING(Net_salary1_date,1,10) as 'Net_salary1_date',Net_Salary2,Net_salary2_date,Net_Salary3,Net_salary3_date,"
				+ "Net_Salary4,Net_salary4_date,Net_Salary5,Net_salary5_date, Net_Salary6,Net_salary6_date,Net_Salary7,Net_salary7_date,"
				+ "Addn_Perfios_EMI_1,Addn_Perfios_EMI_2,Addn_Perfios_EMI_3,Addn_Perfios_EMI_4,"
				+ "Addn_Perfios_EMI_5,Addn_Perfios_EMI_6,Addn_Perfios_EMI_7,Addn_Perfios_EMI_8,"
				+ "Addn_Perfios_EMI_9,Addn_Perfios_EMI_10,Addn_Perfios_EMI_11,Addn_Perfios_EMI_12,"
				+ "Addn_Perfios_EMI_13,Addn_Perfios_EMI_14,Addn_Perfios_EMI_15,Addn_Perfios_EMI_16,"
				+ "Addn_Perfios_EMI_17,Addn_Perfios_EMI_18,Addn_Perfios_EMI_19,Addn_Perfios_EMI_20,Addn_Perfios_CC,"
				+ "Addn_Perfios_OD_Amt,Addn_OD_date,Joint_Acct,High_Value_Deposit,Credit_Amount,Stmt_chq_rtn_last_3mnts,"
				+ "Stmt_chq_rtn_cleared_in30_last_3mnts,Stmt_chq_rtn_last_1mnt,Stmt_chq_rtn_cleared_in30_last_1mnt,"
				+ "Stmt_DDS_rtn_last_3mnts,Stmt_DDS_rtn_cleared_in30_last_3mnts,Stmt_DDS_rtn_last_1mnt,Stmt_DDS_rtn_cleared_in30_last_1mnts,"
				+ "Pensioner,Name_match,FCU_indicator,UW_reqd, requested_limit, Industry, Sub_Industry, FTS_Ack_flg, EFR_NSTP,EStatementFlag"
				+ " FROM NG_DCC_EXTTABLE with(nolock) WHERE WI_NAME='" + wi_name + "'";

		Digital_CC.mLogger.debug("Select NG_DCC_EXTTABLE Query: " + DBQuery);

		String[] columns = { "Wi_Name", "Application_Type", "uw_income", "CIF", "Product", "Product_Desc", "Sub_Product",
				"Card_Product", "CUSTOMERNAME", "PassportNo", "EmirateID", "MobileNo", "employercode", "Employer_Name",
				"EmploymentType", "EmploymentType_Desc", "email_id", "Final_Limit","Underwriting_Limit", "VIPFlag", "Title", "Title_Desc",
				"FirstName", "MiddleName", "LastName", "dob", "Age", "Nationality", "Nationality_Desc", "Designation",
				"Designation_Desc", "Cust_Decl_Salary", "Prospect_id", "FinalDBR", "FinalTAI", "Passport_expiry",
				"Gender", "Gender_Code", "IndusSeg", "IndusSeg_Desc", "EligibleCardProduct", "EligibleCardProduct_Desc",
				"Date_Of_Joining", "Selected_Card_Type", "Prospect_Creation_Date", "FIRCO_Flag", "Visa_Expiry",
				"Emirates_Visa", "EmID_Expiry", "Visa_Sponsor_Name", "GCC_National", "No_earning_members",
				"Earning_members", "Dependents", "Cust_Decl_Salary", "Net_Salary1", "Net_salary1_date", "Net_Salary2",
				"Net_salary2_date", "Net_Salary3", "Net_salary3_date", "Net_Salary4", "Net_salary4_date", "Net_Salary5",
				"Net_salary5_date", "Net_Salary6", "Net_salary6_date", "Net_Salary7", "Net_salary7_date",
				"Addn_Perfios_EMI_1", "Addn_Perfios_EMI_2", "Addn_Perfios_EMI_3", "Addn_Perfios_EMI_4",
				"Addn_Perfios_EMI_5", "Addn_Perfios_EMI_6", "Addn_Perfios_EMI_7", "Addn_Perfios_EMI_8",
				"Addn_Perfios_EMI_9", "Addn_Perfios_EMI_10", "Addn_Perfios_EMI_11", "Addn_Perfios_EMI_12",
				"Addn_Perfios_EMI_13", "Addn_Perfios_EMI_14", "Addn_Perfios_EMI_15", "Addn_Perfios_EMI_16",
				"Addn_Perfios_EMI_17", "Addn_Perfios_EMI_18", "Addn_Perfios_EMI_19", "Addn_Perfios_EMI_20",
				"Addn_Perfios_CC", "Addn_Perfios_OD_Amt", "Addn_OD_date", "Joint_Acct", "High_Value_Deposit",
				"Credit_Amount", "Stmt_chq_rtn_last_3mnts", "Stmt_chq_rtn_cleared_in30_last_3mnts",
				"Stmt_chq_rtn_last_1mnt", "Stmt_chq_rtn_cleared_in30_last_1mnt", "Stmt_DDS_rtn_last_3mnts",
				"Stmt_DDS_rtn_cleared_in30_last_3mnts", "Stmt_DDS_rtn_last_1mnt",
				"Stmt_DDS_rtn_cleared_in30_last_1mnts", "Pensioner", "Name_match", "FCU_indicator", "UW_reqd",
				"requested_limit","Industry","Sub_Industry", "FTS_Ack_flg", "EFR_NSTP","EStatementFlag" };

		Map<String, String> ApplicantDetails_Map = getDataFromDB(DBQuery, cabinetName, sessionId, jtsIp, jtsPort,columns);
		
		//Added by Kamran 09012023--For Salary Details updated by UW WS
		
		String DBQuery_OldSalary ="";
		// Estatement flag: If false then below  Changed on 18.7.23
		
		if("false".equalsIgnoreCase(ApplicantDetails_Map.get("EStatementFlag"))){
			DBQuery_OldSalary ="SELECT TOP 1 wi_name, Net_Salary1, Net_Salary2, Net_Salary3 FROM NG_DCC_GR_NetSalaryDetails with(nolock) WHERE WI_NAME='" + wi_name + "'"
			+ " and (Workstep ='Sys_FTS_WI_Update')";
		}
		else if("true".equalsIgnoreCase(ApplicantDetails_Map.get("EStatementFlag"))){
			DBQuery_OldSalary = "SELECT wi_name, Net_Salary1, Net_Salary2, Net_Salary3 FROM NG_DCC_GR_NetSalaryDetails with(nolock) WHERE  WI_NAME ='" + wi_name + "'  and (Workstep ='Source_Refer')"+
			"union all "+"select Wi_Name, Net_Salary1, Net_Salary2, Net_Salary3 from NG_DCC_EXTTABLE with(nolock) where Wi_Name ='" + wi_name + "'";
		}
		Digital_CC.mLogger.debug("Select Old Salary Query: " + DBQuery_OldSalary);
		String[] columns_OldSalary = { "wi_name","Net_Salary1","Net_Salary2","Net_Salary3"};
		Map<String, String> ApplicationDetailsOldSalary_Map = getDataFromDB(DBQuery_OldSalary, cabinetName, sessionId, jtsIp, jtsPort,
				columns_OldSalary);
		//End

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder = stringBuilder.append(applicationXML);
		String requested_xml = stringBuilder.toString().replace(">Str_ApplicationNumber<", ">" + wi_name + "<");

		/** Application Details Tag **/
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
		
		requested_xml = requested_xml.replace(">str_TimeStampyyyymmddhhmmsss<", ">" + sdf2.format(new Date()) + "<");

		requested_xml = requested_xml.replace(">Str_full_eligibility_availed<", ">Select<")
				.replace(">Str_product_type<", ">" + validateValue(ApplicantDetails_Map.get("Product")) + "<")
				.replace(">Str_app_category<", ">BAU<").replace(">Str_requested_product<", ">CC<")
				.replace(">Str_requested_limit<",">" + validateValue(ApplicantDetails_Map.get("Underwriting_Limit")) + "<")
				.replace(">Str_sub_product<", ">Digital CC STP<")
				.replace(">Str_requested_card_product<",">" + validateValue(ApplicantDetails_Map.get("Selected_Card_Type")) + "<")
				.replace(">Str_interest_rate<", ">0.00<").replace(">Str_customer_type<", ">NTB<")
				.replace(">Str_final_limit<", ">" + validateValue(ApplicantDetails_Map.get("Underwriting_Limit")) + "<")
				.replace(">Str_emi<", ">0.00<").replace(">Str_manual_deviation<", ">N<")
				.replace(">Str_application_date<",">"+validateValue(ApplicantDetails_Map.get("Prospect_Creation_Date")+"T00:00:00")+"<");
		
		//Updated above line on 29112022 - Kamran
				

		requested_xml = requested_xml.replace(">Str_Wi_Name<", ">" + wi_name + "<");

		String app_details = sInputXmlApplicantDetails(ApplicantDetails_Map, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_ApplicantDetails>", app_details);
		Digital_CC.mLogger.debug("DCC sInputXmlApplicantDetails : " + requested_xml);

		/** internal Bureau TAG and sub-tag **/
		String internal_Bureau = sInputXmlInternalBureau(ApplicantDetails_Map);
		requested_xml = requested_xml.replace("<String_InternalBureauData>", internal_Bureau);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBureau : " + internal_Bureau);

		/** External Bureau sub-tag **/
		String external_Bureau = sInputXmlExternalBureau(ApplicantDetails_Map, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_ExternalBureau>", external_Bureau);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBureau : " + requested_xml);

		/** Cheque Bounce sub-Tag **/
		String bounced_Cheques = sInputXmlExternalBouncedCheques(wi_name, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_BouncedCheques>", bounced_Cheques);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBouncedCheques : " + requested_xml);

		/** utilization sub-Tag **/
		String utilization = sInputXmlExternalUtilization(wi_name, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_Utilization24months>", utilization);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBouncedCheques : " + requested_xml);

		/** utilization sub-Tag **/
		String history = sInputXmlExternalHistory(wi_name, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_History_24months>", history);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBouncedCheques : " + requested_xml);

		/** Court Cases sub-Tag **/
		String court_cases = sInputXmlExternalCourtCase(wi_name, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_CourtCase>", court_cases);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalCourtCase : " + requested_xml);
		
		/** ExternalBureau Account Details sub-Tag **/
		String ExternalBureauAccountDetails = sInputXmlExternalBureauAccountDetails(wi_name, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_ExternalBureauAccountDetails>",ExternalBureauAccountDetails);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBureauAccountDetails : "+ requested_xml);
		
		/** ExternalBureau Salary Details sub-Tag **/
		String ExternalBureauSalaryDetails = sInputXmlExternalBureauSalaryDetails(wi_name, cabinetName, sessionId, jtsIp, jtsPort);
		requested_xml = requested_xml.replace("<String_ExternalBureauSalaryDetails>",ExternalBureauSalaryDetails);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBureauSalaryDetails : "+ requested_xml);


		/** External Bureau Individual Products sub-Tag **/
		String individual_Products = sInputXmlExternalBureauIndividualProducts(wi_name, cabinetName, sessionId, jtsIp,
				jtsPort);
		requested_xml = requested_xml.replace("<String_ExternalBureauIndividualProducts>", individual_Products);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBureauIndividualProducts : " + requested_xml);

		/** External Bureau Pipeline Products sub-tag **/
		String pipeline_Products = sInputXmlExternalBureauPipelineProducts(wi_name, cabinetName, sessionId, jtsIp,
				jtsPort);
		requested_xml = requested_xml.replace("<String_ExternalBureauPipelineProducts>", pipeline_Products);
		Digital_CC.mLogger.debug("DCC sInputXmlExternalBureauPipelineProducts : " + requested_xml);

		/** External Bureau Pipeline Products sub-tag **/
		String perfios_details = sInputXmlPerfios(ApplicantDetails_Map,ApplicationDetailsOldSalary_Map);
		Digital_CC.mLogger.debug("DCC perfios_details : " + perfios_details);
		requested_xml = requested_xml.replace("<String_Perfios>", perfios_details);

		Digital_CC.mLogger.debug("DCC" + "Final XML : " + requested_xml);
		String integrationStatus = "Success";
		String attributesTag;
		String ErrDesc = "";
		StringBuilder finalString = new StringBuilder(requested_xml);

		return requested_xml;
	}

	private static Map<String, String> getDataFromDB(String query, String cabinetName, String sessionID, String jtsIP,
			String jtsPort, String... columns) {
		Digital_CC_Common sgetTagValue = new Digital_CC_Common();
		try {
			Digital_CC.mLogger.debug("Inside function getDataFromDB");
			Digital_CC.mLogger.debug("getDataFromDB query is: " + query);
			String InputXML = Digital_CC_Common.apSelectWithColumnNames(query, cabinetName, sessionID);
			Map<String, String> temp = null;
			String OutXml = WFNGExecute(InputXML, jtsIP, jtsPort, 1);
			OutXml = OutXml.replaceAll("&", "#andsymb#");
			Digital_CC.mLogger.debug("getDataFromDB output xml is: " + OutXml);
			Document recordDoc1 = MapXML.getDocument(OutXml);
			NodeList records1 = recordDoc1.getElementsByTagName("Records");
			if (records1.getLength() > 0) {
				temp = new HashMap<String, String>();
				for (String column : columns) {

					String value = sgetTagValue.getTagValue(OutXml, column).replaceAll("#andsymb#", "&");
					// String value= getTagValue(OutXml, column);
					Digital_CC.mLogger.debug("value from getTagValue function is:" + value);
					if (null != value && !"null".equalsIgnoreCase(value) && !"".equals(value)) {
						Digital_CC.mLogger.debug("Setting value of " + column + " as " + value);
						temp.put(column, value);
					} else {
						Digital_CC.mLogger.debug("Setting value of " + column + " as blank");
						temp.put(column, "");
					}
				}
				temp.put("TotalRetrieved", sgetTagValue.getTagValue(OutXml, "TotalRetrieved"));
			}
			return temp;
		} catch (Exception ex) {
			Digital_CC.mLogger.debug("Exception in getDataFromDB method + " + printException(ex));
			return null;
		}
	}

	public static String printException(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exception = sw.toString();
		return exception;
	}

	private static String sInputXmlApplicantDetails(Map<String, String> applicantDetails_Map, String cabinetName,
			String sessionID, String jtsIP, String jtsPort) {
		String industry_sector = "";
		String industry_macro = validateValue(applicantDetails_Map.get("Industry"));
		String industry_micro = validateValue(applicantDetails_Map.get("Sub_Industry"));
		String COMPANY_STATUS_CC = "";
		String COMPANY_STATUS_PL = "";
		String INCLUDED_IN_CC_ALOC = "";
		String INCLUDED_IN_PL_ALOC = "";
		String current_emp_catogery = "";
		String TYPE_OF_COMPANY = "";
		String EMPLOYER_CATEGORY_PL_EXPAT = "";
		String EMPLOYER_CATEGORY_PL_NATIONAL = "";
		String employercode = validateValue(applicantDetails_Map.get("employercode"));
		if (!employercode.equals("")) {
			String query = "select TOP 1 INDUSTRY_SECTOR, INDUSTRY_MACRO, INDUSTRY_MICRO, COMPANY_STATUS_CC,COMPANY_STATUS_PL, "
					+ "INCLUDED_IN_CC_ALOC, INCLUDED_IN_PL_ALOC, EMPLOYER_CATEGORY_PL, TYPE_OF_COMPANY, EMPLOYER_CATEGORY_PL_EXPAT, "
					+ "EMPLOYER_CATEGORY_PL_NATIONAL from NG_RLOS_ALOC_OFFLINE_DATA WITH(nolock) where EMPLOYER_CODE=main_Employer_code "
					+ "and main_Employer_code = '" + employercode + "'";
			try {
				String EMPLOYER_CATEGORY_PL = "";
				List<Map<String, String>> OutputXML_ref = getDataFromDBMap(query, cabinetName, sessionID, jtsIP,
						jtsPort);
				if (OutputXML_ref.size() > 0) {
					industry_sector = OutputXML_ref.get(0).get("INDUSTRY_SECTOR");
					
					if (!validateValue(OutputXML_ref.get(0).get("INDUSTRY_MACRO")).equals(""))
						industry_macro = OutputXML_ref.get(0).get("INDUSTRY_MACRO");

					if (!validateValue(OutputXML_ref.get(0).get("INDUSTRY_MICRO")).equals(""))
						industry_micro=OutputXML_ref.get(0).get("INDUSTRY_MICRO");
					
					COMPANY_STATUS_CC = OutputXML_ref.get(0).get("COMPANY_STATUS_CC");
					COMPANY_STATUS_PL = OutputXML_ref.get(0).get("COMPANY_STATUS_PL");
					INCLUDED_IN_CC_ALOC = OutputXML_ref.get(0).get("INCLUDED_IN_CC_ALOC");
					INCLUDED_IN_PL_ALOC = OutputXML_ref.get(0).get("INCLUDED_IN_PL_ALOC");
					EMPLOYER_CATEGORY_PL = OutputXML_ref.get(0).get("EMPLOYER_CATEGORY_PL");
					TYPE_OF_COMPANY = OutputXML_ref.get(0).get("TYPE_OF_COMPANY");
					EMPLOYER_CATEGORY_PL_EXPAT = OutputXML_ref.get(0).get("EMPLOYER_CATEGORY_PL_EXPAT");
					EMPLOYER_CATEGORY_PL_NATIONAL = OutputXML_ref.get(0).get("EMPLOYER_CATEGORY_PL_NATIONAL");
				}
				if (!"".equals("EMPLOYER_CATEGORY_PL")) {
					//String queryForEmpCat= "select Description from NG_MASTER_EmployerCategory_PL with(nolock) where Code='" + EMPLOYER_CATEGORY_PL+ "' and IsActive='Y'";
					//Kamran 23112022
					String queryForEmpCat= "select Code from NG_MASTER_EmployerCategory_PL with(nolock) where Code='" + EMPLOYER_CATEGORY_PL+ "' and IsActive='Y'";
					List<Map<String, String>> OutputXML4EmpCat = getDataFromDBMap(queryForEmpCat, cabinetName,
							sessionID, jtsIP, jtsPort);
					if (OutputXML4EmpCat.size() > 0) {
						current_emp_catogery = OutputXML4EmpCat.get(0).get("Code");
					}
				}
			} catch (Exception e) {
			}
		}
		String world_check = "N";
		if (validateValue(applicantDetails_Map.get("FIRCO_Flag")).equalsIgnoreCase("Y")) {
			world_check = "Y";
		}
		return "<ApplicantDetails>" + "" + "<applicant_id>" + applicantDetails_Map.get("Wi_Name") + "</applicant_id>"
				+ "" + "<primary_cif>" + validateValue(applicantDetails_Map.get("CIF")) + "</primary_cif>" + ""
				+ "<ref_no>" + applicantDetails_Map.get("Prospect_id") + "</ref_no>" + "" + "<wi_name>"
				+ applicantDetails_Map.get("Wi_Name") + "</wi_name>" + "" + "<cust_name>"
				+ validateValue(applicantDetails_Map.get("FirstName")) + " "
				+ validateValue(applicantDetails_Map.get("LastName")) + "</cust_name>" + "" + "<emp_type>"
				+ validateValue(applicantDetails_Map.get("EmploymentType")) + "</emp_type>" + "" + "<dob>"
				+ validateValue(applicantDetails_Map.get("dob")) + "</dob>" + "" + "<age>"
				+ validateValue(applicantDetails_Map.get("Age")) + "</age>" + "" + "<nationality>"
				+ validateValue(applicantDetails_Map.get("Nationality")) + "</nationality>" + ""
				+ "<resident_flag>Y</resident_flag>" + "" + "<world_check>" + world_check + "</world_check>" + ""
				+ "<no_of_cheque_bounce_int_3mon_Ind>" + cheque_count + "</no_of_cheque_bounce_int_3mon_Ind>" + "" // TODO
				+ "<no_of_dds_return_int_3mon_Ind>" + dds_count + "</no_of_dds_return_int_3mon_Ind>" + "" // TODO
				+ "<los>" + CalculatLOS(applicantDetails_Map.get("Date_Of_Joining")) + "</los>" + ""
				+ "<target_segment_code>DIG</target_segment_code>" + "" + "<current_emp_catogery>"
				+ current_emp_catogery + "</current_emp_catogery>" + ""

				+ "<visa_expiry_date>" + validateValue(applicantDetails_Map.get("Visa_Expiry")) + "</visa_expiry_date>"
				+ "" + "<passport_expiry_date>" + validateValue(applicantDetails_Map.get("EmID_Expiry"))
				+ "</passport_expiry_date>" + "" + "<emirates_visa>"
				+ validateValue(applicantDetails_Map.get("Emirates_Visa")) + "</emirates_visa>" + "" + "<designation>"
				+ validateValue(applicantDetails_Map.get("Designation")) + "</designation>" + "" + "<gender>"
				+ validateValue(applicantDetails_Map.get("Gender_Code")) + "</gender>" + "" + "<cust_mobile_no>"
				+ validateValue(applicantDetails_Map.get("MobileNo")) + "</cust_mobile_no>" + "" + "<emp_name>"
				+ validateValue(applicantDetails_Map.get("Employer_Name")) + "</emp_name>" + "" + "<emp_code>"
				+ validateValue(applicantDetails_Map.get("employercode")) + "</emp_code>" + "" + "<type_of_company>"
				+ TYPE_OF_COMPANY + "</type_of_company>" + ""

				+ "<industry_sector>" + industry_sector + "</industry_sector>" + "" + "<industry_macro>"
				+ industry_macro + "</industry_macro>" + "" + "<industry_micro>" + industry_micro + "</industry_micro>"
				+ "" + "<cc_employer_status>" + COMPANY_STATUS_CC + "</cc_employer_status>" + ""
				+ "<pl_employer_status>" + COMPANY_STATUS_PL + "</pl_employer_status>" + ""
				+ "<pl_employer_status_expat>" + EMPLOYER_CATEGORY_PL_EXPAT + "</pl_employer_status_expat>" + ""
				+ "<pl_employer_status_national>" + EMPLOYER_CATEGORY_PL_NATIONAL + "</pl_employer_status_national>"
				+ "" + "<included_pl_aloc>" + INCLUDED_IN_PL_ALOC + "</included_pl_aloc>" + "" + "<included_cc_aloc>"
				+ INCLUDED_IN_CC_ALOC + "</included_cc_aloc>" + "" + "<visa_sponsor>"
				+ validateValue(applicantDetails_Map.get("Visa_Sponsor_Name")) + "</visa_sponsor>" + ""
				+ "<country_of_residence>AE</country_of_residence>" + "" + "<gcc_national>"
				+ validateValue(applicantDetails_Map.get("GCC_National")) + "</gcc_national>" + ""
				+ "<employer_type>N</employer_type>" + "" + "<aecb_consent>Y</aecb_consent>" + "" + "<No_of_dependants>"
				+ validateValue(applicantDetails_Map.get("Dependents")) + "</No_of_dependants>" + ""
				+ "<Other_household_income>" + validateValue(applicantDetails_Map.get("Earning_members"))
				+ "</Other_household_income>" + "" + "<No_earning_members>"
				+ validateValue(applicantDetails_Map.get("No_earning_members")) + "</No_earning_members>" + ""
				+"<EFR_NSTP>"+validateValue(applicantDetails_Map.get("EFR_NSTP"))+"</EFR_NSTP>" + ""

				+ "<marketing_code>DIG</marketing_code>" + "" + "</ApplicantDetails>";
	}

	private static List<Map<String, String>> getDataFromDBMap(String query, String cabinetName, String sessionID,
			String jtsIP, String jtsPort) {
		try {
			Digital_CC.mLogger.debug("Inside function getDataFromDB");
			Digital_CC.mLogger.debug("getDataFromDB query is: " + query);
			String InputXML = apSelectWithColumnNames(query, cabinetName, sessionID);
			List<Map<String, String>> temp = new ArrayList<Map<String, String>>();
			String OutXml = WFNGExecute(InputXML, jtsIP, jtsPort, 1);
			OutXml = OutXml.replaceAll("&", "#andsymb#");
			Document recordDoc1 = MapXML.getDocument(OutXml);
			NodeList records1 = recordDoc1.getElementsByTagName("Record");
			if (records1.getLength() > 0) {
				for (int i = 0; i < records1.getLength(); i++) {
					Node n = records1.item(i);
					Map<String, String> t = new HashMap<String, String>();
					if (n.hasChildNodes()) {
						NodeList child = n.getChildNodes();
						for (int j = 0; j < child.getLength(); j++) {
							Node n1 = child.item(j);
							String column = n1.getNodeName();
							String value = n1.getTextContent().replaceAll("#andsymb#", "&");
							if (null != value && !"null".equalsIgnoreCase(value) && !"".equals(value)) {
								Digital_CC.mLogger
										.debug("getDataFromDBMap Setting value of " + column + " as " + value);
								t.put(column, value);
							} else {
								Digital_CC.mLogger.debug("getDataFromDBMap Setting value of " + column + " as blank");
								t.put(column, "");
							}
						}
					}
					temp.add(t);
				}
			}
			return temp;
		} catch (Exception ex) {
			Digital_CC.mLogger.debug("Exception in getDataFromDBMap method + " + printException(ex));
			return null;
		}
	}

	// Calculate DOJ formate should be YYYY-MM-DD
	public static Double CalculatLOS(String DOJ_Str) {
		Double LOS = 0.00;
		try {
			Integer year = Integer.parseInt(DOJ_Str.split("-")[0]);
			Integer month = Integer.parseInt(DOJ_Str.split("-")[1]);
			Integer day = Integer.parseInt(DOJ_Str.split("-")[2]);
			LocalDate DOJ = LocalDate.of(year, month, day);
			LocalDate CD = LocalDate.now();
			Period p = Period.between(DOJ, CD);
			System.out.println(p.getMonths());
			System.out.println(p.getYears());
			LOS += p.getYears();
			LOS = LOS + p.getMonths() / 100d;
		} catch (Exception e) {
			e.printStackTrace();
			return LOS;
		}

		System.out.println(LOS);
		return LOS;
	}


	public static String sInputXmlInternalBureau(Map<String, String> applicantDetails_Map) {
		Digital_CC.mLogger.debug("Exception in sInputXmlInternalBureau method + ");
		
		String internal_bureau = "";
		String uw_income = validateValue(applicantDetails_Map.get("uw_income"));
		
		if("".equalsIgnoreCase(uw_income) || uw_income.isEmpty()){
			internal_bureau = "<InternalBureauData>" + "" + "<InternalBureau>" + ""
					+ "<company_flag>N</company_flag>" + "" + "</InternalBureau>" + "" + "<InternalBouncedCheques>" + ""
					+ "<company_flag>N</company_flag>" + "" + "</InternalBouncedCheques>" + ""
					+ "<InternalBureauIndividualProducts>" + "" + "<company_flag>N</company_flag>" + ""
					+ "</InternalBureauIndividualProducts>" + "" + "<InternalBureauPipelineProducts>" + ""
					+ "<company_flag>N</company_flag>" + "" + "</InternalBureauPipelineProducts>" + ""
					+ "<InternalBureauDBRTAICalc>" + "" + "<basic>"
					+ validateValue(applicantDetails_Map.get("Cust_Decl_Salary")) + "</basic>" + "" + "<gross_salary>"
					+ validateValue(applicantDetails_Map.get("Cust_Decl_Salary")) + "</gross_salary>" + ""
					+ "<net_salary_mon1>" + validateValue(applicantDetails_Map.get("Net_Salary1")) + "</net_salary_mon1>"
					+ "" + "<net_salary_mon2>" + validateValue(applicantDetails_Map.get("Net_Salary2"))
					+ "</net_salary_mon2>" + "" + "<net_salary_mon3>"
					+ validateValue(applicantDetails_Map.get("Net_Salary3")) + "</net_salary_mon3>" + ""
					+ "</InternalBureauDBRTAICalc>" + "" + "</InternalBureauData>";
			
		}else{
		
			internal_bureau = "<InternalBureauData>" + "" + "<InternalBureau>" + ""
				+ "<company_flag>N</company_flag>" + "" + "</InternalBureau>" + "" + "<InternalBouncedCheques>" + ""
				+ "<company_flag>N</company_flag>" + "" + "</InternalBouncedCheques>" + ""
				+ "<InternalBureauIndividualProducts>" + "" + "<company_flag>N</company_flag>" + ""
				+ "</InternalBureauIndividualProducts>" + "" + "<InternalBureauPipelineProducts>" + ""
				+ "<company_flag>N</company_flag>" + "" + "</InternalBureauPipelineProducts>" + ""
				+ "<InternalBureauDBRTAICalc>" + "" + "<basic>"
				+ validateValue(applicantDetails_Map.get("Cust_Decl_Salary")) + "</basic>" + "" + "<gross_salary>"
				+ validateValue(applicantDetails_Map.get("Cust_Decl_Salary")) + "</gross_salary>" + ""
				+ "<net_salary_mon1>" + validateValue(applicantDetails_Map.get("Net_Salary1")) + "</net_salary_mon1>"
				+ "" + "<net_salary_mon2>" + validateValue(applicantDetails_Map.get("Net_Salary2"))
				+ "</net_salary_mon2>" + "" + "<net_salary_mon3>"
				+ validateValue(applicantDetails_Map.get("Net_Salary3")) + "</net_salary_mon3>" + ""
				+ "<Final_UW_Salary>"
				+ validateValue(applicantDetails_Map.get("uw_income")) + "</Final_UW_Salary>" + ""
				+ "</InternalBureauDBRTAICalc>" + "" + "</InternalBureauData>";
		}
		Digital_CC.mLogger.debug("Exception in sInputXmlInternalBureau method + "+internal_bureau);
		
		return internal_bureau;
	}

	
	private static String sInputXmlExternalBureau(Map<String, String> applicantDetails_Map, String cabinetName,
			String sessionID, String jtsIP, String jtsPort) {
		String Wi_Name = applicantDetails_Map.get("Wi_Name");

		String sQuery = "select top 1 CifId, fullnm,TotalOutstanding,TotalOverdue,NoOfContracts,Total_Exposure,WorstCurrentPaymentDelay,"
				+ "Worst_PaymentDelay_Last24Months,Worst_Status_Last24Months,Nof_Records,NoOf_Cheque_Return_Last3,Nof_DDES_Return_Last3Months,"
				+ "Nof_Cheque_Return_Last6,DPD30_Last6Months,(select max(ExternalWriteOffCheck) ExternalWriteOffCheck "
				+ "from ((select convert(int,isNULL(ExternalWriteOffCheck,0)) ExternalWriteOffCheck  from ng_dcc_cust_extexpo_CardDetails with(nolock) "
				+ "where Wi_Name  = '" + Wi_Name + "' and ProviderNo!='B01'  "

				+ "union all select convert(int,isNULL(ExternalWriteOffCheck,0)) ExternalWriteOffCheck "
				+ "from ng_dcc_cust_extexpo_LoanDetails where Wi_Name  = '" + Wi_Name + "' and ProviderNo!='B01' "

				+ "union all select convert(int,isNULL(ExternalWriteOffCheck,0)) ExternalWriteOffCheck from ng_dcc_cust_extexpo_AccountDetails "
				+ "where Wi_Name = '" + Wi_Name
				+ "' and ProviderNo!='B01')) as ExternalWriteOffCheck) as 'ExternalWriteOffCheck' ,(select count(*) "
				+ "from (select DisputeAlert from ng_dcc_cust_extexpo_LoanDetails with(nolock) where Wi_Name = '"
				+ Wi_Name + "' and DisputeAlert='1' "

				+ "union select DisputeAlert from ng_dcc_cust_extexpo_CardDetails with(nolock) where Wi_Name = '"
				+ Wi_Name + "' and DisputeAlert='1') "
				+ "as tempTable) as 'DisputeAlert'  from ng_dcc_cust_extexpo_Derived with (nolock) where Wi_Name  = '"
				+ Wi_Name + "' and Request_type= 'ExternalExposure'";

		String AecbHistQuery = "select isnull(max(AECBHistMonthCnt),0) as AECBHistMonthCnt from ( select MAX(cast(isnull(AECBHistMonthCnt,'0') as int)) as AECBHistMonthCnt  "
				+ "from ng_dcc_cust_extexpo_CardDetails with (nolock) where  Wi_Name  = '" + Wi_Name
				+ "' and cardtype not in ( '85','99','Communication Services',"
				+ "'TelCo-Mobile Prepaid','101','Current/Saving Account with negative Balance','58','Overdraft') and custroletype not in ('Co-Contract Holder','Guarantor') "

				+ "union all select Max(cast(isnull(AECBHistMonthCnt,'0') as int)) as AECBHistMonthCnt from ng_dcc_cust_extexpo_LoanDetails with (nolock) "
				+ "where Wi_Name  = '" + Wi_Name
				+ "' and loantype not in ('85','99','Communication Services','TelCo-Mobile Prepaid','101',"
				+ "'Current/Saving Account with negative Balance','58','Overdraft') and custroletype not in ('Co-Contract Holder','Guarantor')) as ext_expo";

		String add_xml_str = "";
		try {

			List<Map<String, String>> OutputXML = getDataFromDBMap(sQuery, cabinetName, sessionID, jtsIP, jtsPort);
			List<Map<String, String>> AecbHistMap = getDataFromDBMap(AecbHistQuery, cabinetName, sessionID, jtsIP,
					jtsPort);

			if (OutputXML.size() == 0) {
				String aecb_score = "";
				String range = "";
				String refNo = "";
				String query = "select top 1 ReferenceNo, AECB_Score,Range from ng_dcc_cust_extexpo_Derived with(nolock) where Wi_Name ='"
						+ Wi_Name + "' and Request_Type='ExternalExposure' ORDER BY enquiryDate desc";
				try {
					List<Map<String, String>> OutputXML_ref = getDataFromDBMap(query, cabinetName, sessionID, jtsIP,
							jtsPort);
					if (OutputXML_ref.size() > 0) {
						refNo = OutputXML_ref.get(0).get("ReferenceNo");
						aecb_score = OutputXML_ref.get(0).get("AECB_Score");
						range = OutputXML_ref.get(0).get("Range");
					}
				} catch (Exception e) {
				}

				add_xml_str += "<ExternalBureau>" + "";
				add_xml_str += "<applicant_id>" + validateValue(applicantDetails_Map.get("Wi_Name")) + "</applicant_id>"
						+ "";
				add_xml_str += "<bureauone_ref_no>" + refNo + "</bureauone_ref_no>" + "";
				add_xml_str += "<full_name>" + validateValue(applicantDetails_Map.get("FirstName")) + " "
						+ validateValue(applicantDetails_Map.get("LastName")) + "</full_name>" + ""; // ,
																										// MiddleName,
				add_xml_str += "<total_out_bal></total_out_bal>" + "";

				add_xml_str += "<total_overdue></total_overdue>" + "";
				add_xml_str += "<no_default_contract></no_default_contract>" + "";
				add_xml_str += "<total_exposure></total_exposure>" + "";
				add_xml_str += "<worst_curr_pay></worst_curr_pay>" + "";
				add_xml_str += "<worst_curr_pay_24></worst_curr_pay_24>" + "";

				add_xml_str += "<no_of_rec></no_of_rec>" + "";
				add_xml_str += "<cheque_return_3mon></cheque_return_3mon>" + "";
				add_xml_str += "<dds_return_3mon></dds_return_3mon>" + "";

				add_xml_str += "<no_months_aecb_history>" + AecbHistMap.get(0).get("AECBHistMonthCnt")
						+ "</no_months_aecb_history>" + "";
				add_xml_str += "<aecb_score>" + aecb_score + "</aecb_score>" + "";
				add_xml_str += "<range>" + range + "</range>" + "";
				add_xml_str += "<company_flag>N</company_flag></ExternalBureau>" + "";

				return add_xml_str;
			} else {
				for (Map<String, String> map : OutputXML) {
					String fullnm = validateValue(map.get("fullnm"));
					String TotalOutstanding = validateValue(map.get("TotalOutstanding"));
					String TotalOverdue = validateValue(map.get("TotalOverdue"));
					String NoOfContracts = validateValue(map.get("NoOfContracts"));
					String Total_Exposure = validateValue(map.get("Total_Exposure"));
					String WorstCurrentPaymentDelay = validateValue(map.get("WorstCurrentPaymentDelay"));
					String Worst_PaymentDelay_Last24Months = validateValue(map.get("Worst_PaymentDelay_Last24Months"));
					String Worst_Status_Last24Months = validateValue(map.get("Worst_Status_Last24Months"));
					String Nof_Records = validateValue(map.get("Nof_Records"));
					String NoOf_Cheque_Return_Last3 = validateValue(map.get("NoOf_Cheque_Return_Last3"));
					String Nof_DDES_Return_Last3Months = validateValue(map.get("Nof_DDES_Return_Last3Months"));
					String Nof_Cheque_Return_Last6 = validateValue(map.get("Nof_Cheque_Return_Last6"));
					String DPD30_Last6Months = validateValue(map.get("DPD30_Last6Months"));
					String dispute_alert = validateValue(map.get("tempTable"));

					String aecb_score = "";
					String range = "";
					String refNo = "";
					String EnquiryDate = "";

					if (!dispute_alert.equals("")) {
						try {
							if (Integer.parseInt(dispute_alert) > 0) {
								dispute_alert = "Y";
							} else {
								dispute_alert = "N";
							}
						} catch (NumberFormatException e) {
							dispute_alert = "N";
						}
					} else {
						dispute_alert = "N";
					}

					String Ref_query = "select ReferenceNo, AECB_Score,Range, EnquiryDate from ng_dcc_cust_extexpo_Derived with(nolock) where Wi_Name ='"
							+ Wi_Name + "' and Request_Type='ExternalExposure'";
					try {
						List<Map<String, String>> OutputXML_ref = getDataFromDBMap(Ref_query, cabinetName, sessionID,
								jtsIP, jtsPort);
						if (OutputXML_ref.size() > 0) {
							refNo = OutputXML_ref.get(0).get("ReferenceNo");
							aecb_score = OutputXML_ref.get(0).get("AECB_Score");
							range = OutputXML_ref.get(0).get("Range");
							EnquiryDate = OutputXML_ref.get(0).get("EnquiryDate");
							
							try{
								Date parseDateCC;

								if(EnquiryDate.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")){
									parseDateCC = new SimpleDateFormat("dd/MM/yyyy").parse(EnquiryDate);
									//CreditCard.mLogger.info("parseDate :" + parseDate);
								}
								else if(EnquiryDate.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})"))
								{
									parseDateCC = new SimpleDateFormat("dd-MM-yyyy").parse(EnquiryDate);
									//CreditCard.mLogger.info("parseDate :" + parseDate);
								}
								else if(EnquiryDate.matches("([0-9]{4})/([0-9]{2})/([0-9]{2})")){
									parseDateCC = new SimpleDateFormat("yyyy/MM/dd").parse(EnquiryDate);
									//CreditCard.mLogger.info("parseDate :" + parseDate);
								}
								else{
									parseDateCC = new SimpleDateFormat("yyyy-MM-dd").parse(EnquiryDate);
								}


								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
								//CreditCard.mLogger.info("formatter :" + formatter);

								EnquiryDate = formatter.format(parseDateCC);
								Digital_CC.mLogger.debug("EnquiryDate :" + EnquiryDate);
							}
							catch(Exception e){
								Digital_CC.mLogger.debug("Exception occured in conversion of EnquiryDate :" + e.getMessage());
							}

							

						}
					} catch (Exception e) {
					}

					add_xml_str += "<ExternalBureau>" + "";
					add_xml_str += "<applicant_id>" + validateValue(applicantDetails_Map.get("Wi_Name"))
							+ "</applicant_id>" + "";
					add_xml_str += "<bureauone_ref_no>" + refNo + "</bureauone_ref_no>" + "";
					add_xml_str += "<full_name>" + fullnm + "</full_name>" + "";
					add_xml_str += "<total_out_bal>" + TotalOutstanding + "</total_out_bal>" + "";

					add_xml_str += "<total_overdue>" + TotalOverdue + "</total_overdue>" + "";
					add_xml_str += "<no_default_contract>" + NoOfContracts + "</no_default_contract>" + "";
					add_xml_str += "<total_exposure>" + Total_Exposure + "</total_exposure>" + "";
					add_xml_str += "<worst_curr_pay>" + WorstCurrentPaymentDelay + "</worst_curr_pay>" + "";
					add_xml_str += "<worst_curr_pay_24>" + Worst_PaymentDelay_Last24Months + "</worst_curr_pay_24>"
							+ "";
					add_xml_str += "<worst_status_24>" + Worst_Status_Last24Months + "</worst_status_24>" + "";

					add_xml_str += "<no_of_rec>" + Nof_Records + "</no_of_rec>" + "";
					add_xml_str += "<cheque_return_3mon>" + NoOf_Cheque_Return_Last3 + "</cheque_return_3mon>" + "";
					add_xml_str += "<dds_return_3mon>" + Nof_DDES_Return_Last3Months + "</dds_return_3mon>" + "";
					add_xml_str += "<cheque_return_6mon>" + Nof_Cheque_Return_Last6 + "</cheque_return_6mon>" + "";
					add_xml_str += "<dds_return_6mon>" + DPD30_Last6Months + "</dds_return_6mon>" + "";
					add_xml_str += "<no_months_aecb_history>" + AecbHistMap.get(0).get("AECBHistMonthCnt")
							+ "</no_months_aecb_history>" + "";

					add_xml_str += "<aecb_score>" + aecb_score + "</aecb_score>" + "";
					add_xml_str += "<range>" + range + "</range>" + "";
					add_xml_str += "<AECB_Enquiry_date>" + EnquiryDate + "</AECB_Enquiry_date>" + "";

					add_xml_str += "<company_flag>N</company_flag>" + "";
					add_xml_str += "<dispute_alert>" + dispute_alert + "</dispute_alert></ExternalBureau>";

				}
				return add_xml_str;
			}
		}

		catch (Exception e) {
			return null;
		}
	}

	private static String sInputXmlExternalCourtCase(String Wi_Name, String cabinetName, String sessionID, String jtsIP,
			String jtsPort) {
		String court_cases = "";
		String QueryCaseDetails = "select CodOrganization, ProviderCaseNo, ReferenceDate, CaseCategoryCode,CaseOpenDate, isnull(CaseCloseDate,'') as CaseCloseDate, CaseStatusCode,"
				+ "InitialTotalClaimAmount from ng_dcc_cust_extexpo_CaseDetails where Wi_Name='" + Wi_Name + "'";
		List<Map<String, String>> list_map = getDataFromDBMap(QueryCaseDetails, cabinetName, sessionID, jtsIP, jtsPort);
		System.out.println("Total Retrieved Records: " + list_map.size());
		for (Map<String, String> map : list_map) {
			court_cases += "<CourtCase>" + "" + "<CodOrganization>" + validateValue(map.get("CodOrganization"))
					+ "</CodOrganization>" + "" + "<ProviderCaseNo>" + validateValue(map.get("ProviderCaseNo"))
					+ "</ProviderCaseNo>" + "" + "<ReferenceDate>" + validateValue(map.get("ReferenceDate"))
					+ "</ReferenceDate>" + "" + "<CaseCategoryCode>" + validateValue(map.get("CaseCategoryCode"))
					+ "</CaseCategoryCode>" + "" + "<OpenDate>" + validateValue(map.get("CaseOpenDate")) + "</OpenDate>"
					+ "" + "<CloseDate>" + validateValue(map.get("CaseCloseDate")) + "</CloseDate>" + ""
					+ "<CaseStatusCode>" + validateValue(map.get("CaseStatusCode")) + "</CaseStatusCode>" + ""
					+ "<InitialTotalClaimAmount>" + validateValue(map.get("InitialTotalClaimAmount"))
					+ "</InitialTotalClaimAmount>" + "" + "</CourtCase>";
		}
		return court_cases;
	}
	
	//Deepak change for ExternalBureauAccountDetails from AECB to Dectech
		private static  String sInputXmlExternalBureauAccountDetails(String Wi_Name, String cabinetName, String sessionID, String jtsIP, String jtsPort) {
			String ExternalBureauAccountDetails = "";
			try{
				String QueryCaseDetails ="select AccountType,Phase,IBAN,CBAccountNo,DPAccountNo,ProviderNo,StartDate,CloseDate,DateOfLastUpdate from ng_dcc_cust_extexpo_SalCreditDtls with(nolock) where Wi_Name= '"+Wi_Name+"'";
				Digital_CC.mLogger.debug("Select BureauAccountDetails Query: "+QueryCaseDetails);
				List<Map<String,String>> list_map = getDataFromDBMap(QueryCaseDetails, cabinetName, sessionID, jtsIP, jtsPort);
				Digital_CC.mLogger.debug("Total Retrieved Records: " + list_map.size());
				System.out.println("Total Retrieved Records: " + list_map.size());
				for (Map<String,String> map : list_map) {
					ExternalBureauAccountDetails += "<ExternalBureauAccountDetails>"+ ""
							+"<applicant_id>"+Wi_Name+"</applicant_id>"+ ""
							+"<Account_Type>"+validateValue(map.get("AccountType"))+"</Account_Type>"+ ""
							+"<Phase>"+validateValue(map.get("Phase"))+"</Phase>"+ ""
							+"<IBAN>"+validateValue(map.get("IBAN"))+"</IBAN>"+ ""
							+"<CBAccountNo>"+validateValue(map.get("CBAccountNo"))+"</CBAccountNo>"+ ""
							+"<DPAccountNo>"+validateValue(map.get("DPAccountNo"))+"</DPAccountNo>"+ ""
							+"<Provider_No>"+validateValue(map.get("ProviderNo"))+"</Provider_No>"+ ""
							+"<Start_Date>"+validateValue(map.get("StartDate"))+"</Start_Date>"+ ""
							+"<Closed_Date>"+validateValue(map.get("CloseDate"))+"</Closed_Date>"+ ""
							+"<Date_Of_Last_Update>"+validateValue(map.get("DateOfLastUpdate"))+"</Date_Of_Last_Update>"+ ""
							+"</ExternalBureauAccountDetails>";
				}
			}
			catch(Exception e){
				Digital_CC.mLogger.debug("Exception occured while extracting ExternalBureauAccountDetails: "+ e.getMessage());
			}
			return ExternalBureauAccountDetails;
		}
		//Deepak change for ExternalBureauSalaryDetails from AECB to Dectech 
		private static  String sInputXmlExternalBureauSalaryDetails(String Wi_Name, String cabinetName, String sessionID, String jtsIP, String jtsPort) {
			String ExternalBureauSalaryDetails = "";
			try{
				String QueryCaseDetails ="select CBAccountNo, TotalSalaryAmount, substring(ReferenceDate,0,CHARINDEX('-',ReferenceDate)) as year,"
						+ " substring(ReferenceDate, CHARINDEX('-',ReferenceDate)+1,LEN(ReferenceDate)) as month, NumberOfSalariesTransferred from ng_dcc_cust_extexpo_SalCreditHis with(nolock) where Wi_Name='"+Wi_Name+"'";
				Digital_CC.mLogger.debug("Select SalaryDetails Query: "+QueryCaseDetails);
				List<Map<String,String>> list_map = getDataFromDBMap(QueryCaseDetails, cabinetName, sessionID, jtsIP, jtsPort);
				Digital_CC.mLogger.debug("Total Retrieved Records: " + list_map.size());
				System.out.println("Total Retrieved Records: " + list_map.size());
				for (Map<String,String> map : list_map) {
					ExternalBureauSalaryDetails += "<ExternalBureauSalaryDetails>"+ ""
							+"<applicant_id>"+Wi_Name+"</applicant_id>"+ ""
							+"<CBAccountNo>"+validateValue(map.get("CBAccountNo"))+"</CBAccountNo>"+ ""
							+"<Year>"+validateValue(map.get("year"))+"</Year>"+ ""
							+"<Month>"+validateValue(map.get("month"))+"</Month>"+ ""
							+"<Total_Salary_Amount>"+validateValue(map.get("TotalSalaryAmount"))+"</Total_Salary_Amount>"+ ""
							+"<No_Of_Salary_Transferred>"+validateValue(map.get("NumberOfSalariesTransferred"))+"</No_Of_Salary_Transferred>"+ ""
							+"</ExternalBureauSalaryDetails>";
				}
			}
			catch(Exception e){
				Digital_CC.mLogger.debug("Exception occured while extracting ExternalBureauSalaryDetails: "+ e.getMessage());
			}
			
			return ExternalBureauSalaryDetails;
		}


	private static String sInputXmlExternalBouncedCheques(String wiName, String cabinetName, String sessionID,
			String jtsIP, String jtsPort) {
		String sQuery = "SELECT CifId,ChqType,number,amount,reasoncode,returndate,providerno FROM ng_dcc_cust_extexpo_ChequeDetails  with (nolock) "
				+ "where Wi_Name = '" + wiName + "' and Request_Type = 'ExternalExposure'";

		String add_xml_str = "";

		List<Map<String, String>> OutputXML = getDataFromDBMap(sQuery, cabinetName, sessionID, jtsIP, jtsPort);

		for (Map<String, String> map : OutputXML) {
			add_xml_str += "<ExternalBouncedCheques><applicant_id>" + wiName + "</applicant_id>" + "";
			add_xml_str += "<bounced_cheque>" + validateValue(map.get("ChqType")) + "</bounced_cheque>" + "";
			add_xml_str += "<cheque_no>" + validateValue(map.get("number")) + "</cheque_no>" + "";
			add_xml_str += "<amount>" + validateValue(map.get("amount")) + "</amount>" + "";
			add_xml_str += "<reason>" + validateValue(map.get("reasoncode")) + "</reason>" + "";
			add_xml_str += "<return_date>" + validateValue(map.get("returndate")) + "</return_date>" + "";
			add_xml_str += "<provider_no>" + validateValue(map.get("providerno"))
					+ "</provider_no><company_flag>N</company_flag></ExternalBouncedCheques>"; // to
		}
		return add_xml_str;
	}

	private static String sInputXmlExternalUtilization(String wiName, String cabinetName, String sessionID,
			String jtsIP, String jtsPort) {
		
		/*String sQuery = "select CardEmbossNum, Utilizations24Months as UtilizationsMonths from ng_dcc_cust_extexpo_CardDetails where Wi_Name='"
		+ wiName + "' and (History is not null or History!='') "
		+ "union all select AgreementId, Utilizations24Months as UtilizationsMonths from ng_dcc_cust_extexpo_LoanDetails where Wi_Name='"
		+ wiName + "' and (History is not null or History!='')";*/

		//Deepak - 2March23 Changes done for JIRA PDSC-281 Do not send monthly utilization for loan contracts from AECB; HD 3790532
		
		/*String sQuery = "select CardEmbossNum, Utilizations24Months as UtilizationsMonths from ng_dcc_cust_extexpo_CardDetails where Wi_Name='"
				+ wiName + "' and (History is not null or History!='')";*/
		//Deepak 28Sept2023 Changes done to exclude History!='' as history is ntext and !='' dosen't work on that. 
		String sQuery = "select CardEmbossNum, Utilizations24Months as UtilizationsMonths from ng_dcc_cust_extexpo_CardDetails where Wi_Name='"
				+ wiName + "' and (History is not null)";
		String add_xml_str = "";

		try {
			String extTabDataIPXML = apSelectWithColumnNames(sQuery, cabinetName, sessionID);
			Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
			String extTabDataOPXML = WFNGExecute(extTabDataIPXML, jtsIP, jtsPort, 1);
			Digital_CC.mLogger.debug("extTabDataOPXML: " + extTabDataOPXML);

			XMLParser xmlParserData = new XMLParser(extTabDataOPXML);
			int iTotalrec = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));

			if (xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec > 0) {
				String xmlDataExtTab = xmlParserData.getNextValueOf("Record");
				xmlDataExtTab = xmlDataExtTab.replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");

				NGXmlList objWorkList = xmlParserData.createList("Records", "Record");
				String Utilizations24Months = "";
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true)) {
					String agreementID = validateValue(objWorkList.getVal("CardEmbossNum"));
					String UtilizationTag = validateValue(objWorkList.getVal("UtilizationsMonths"));

					UtilizationTag = UtilizationTag.replaceAll("Utilizations24Months", "Month_Utilization");
					Utilizations24Months += UtilizationTag.replaceAll("<Month_Utilization>",
							"<Month_Utilization><CB_application_id>" + agreementID + "</CB_application_id>");
				}

				if (!Utilizations24Months.equals(""))
					add_xml_str = add_xml_str + "<Utilization24months>" + Utilizations24Months
							+ "</Utilization24months>";
			}
		} catch (Exception e) {
			Digital_CC.mLogger.debug("Utilization24months Exception : " + e.getMessage());
			e.printStackTrace();
		}
		Digital_CC.mLogger.debug("Utilization24months : " + add_xml_str);
		return add_xml_str;
	}

	private static String sInputXmlExternalHistory(String wiName, String cabinetName, String sessionID, String jtsIP,
			String jtsPort) {
		//Deepak 28Sept2023 Changes done to exclude History!='' as history is ntext and !='' dosen't work on that.
/*		String sQuery = "select CardEmbossNum, history as extHistory from ng_dcc_cust_extexpo_CardDetails where Wi_Name='"
				+ wiName + "' and (History is not null or History!='') "
				+ "union all select AgreementId, history as extHistory from ng_dcc_cust_extexpo_LoanDetails where Wi_Name='"
				+ wiName + "' and (History is not null or History!='')";
*/
		String sQuery = "select CardEmbossNum, history as extHistory from ng_dcc_cust_extexpo_CardDetails where Wi_Name='"
				+ wiName + "' and (History is not null) "
				+ "union all select AgreementId, history as extHistory from ng_dcc_cust_extexpo_LoanDetails where Wi_Name='"
				+ wiName + "' and (History is not null)";
		String add_xml_str = "";
		try {

			String extTabDataIPXML = apSelectWithColumnNames(sQuery, cabinetName, sessionID);
			Digital_CC.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);
			String extTabDataOPXML = WFNGExecute(extTabDataIPXML, jtsIP, jtsPort, 1);
			Digital_CC.mLogger.debug("extTabDataOPXML: " + extTabDataOPXML);

			XMLParser xmlParserData = new XMLParser(extTabDataOPXML);
			int iTotalrec = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));

			if (xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0") && iTotalrec > 0) {
				String xmlDataExtTab = xmlParserData.getNextValueOf("Record");
				xmlDataExtTab = xmlDataExtTab.replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");

				NGXmlList objWorkList = xmlParserData.createList("Records", "Record");
				String history = "";
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true)) {
					String agreementID = validateValue(objWorkList.getVal("CardEmbossNum"));
					String HistoryTag = validateValue(objWorkList.getVal("extHistory"));
					HistoryTag = HistoryTag.replaceAll("Key", "monthyear");

					history += HistoryTag.replaceAll("<History>",
							"<History><CB_application_id>" + agreementID + "</CB_application_id>");
				}

				if (!history.equals(""))
					add_xml_str = add_xml_str + "<History_24months>" + history + "</History_24months>";
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return add_xml_str;
	}

	private static String sInputXmlExternalBureauIndividualProducts(String wiName, String cabinetName, String sessionID,
			String jtsIP, String jtsPort) {
		String sQuery = "select CifId,AgreementId,LoanType,ProviderNo,LoanStat,CustRoleType,LoanApprovedDate,LoanMaturityDate,OutstandingAmt,TotalAmt,PaymentsAmt,"
				+ "TotalNoOfInstalments,RemainingInstalments,WriteoffStat,WriteoffStatDt,CreditLimit,OverdueAmt,NofDaysPmtDelay,MonthsOnBook,lastrepmtdt,IsCurrent,"
				+ "CurUtilRate,DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,'' as qc_Amnt,'' as Qc_emi,'' as Cac_indicator,Take_Over_Indicator,"
				+ "Consider_For_Obligations, case when IsDuplicate= '1' then 'Y' else 'N' end AS IsDuplicate,avg_utilization,DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount,"
				+ "Pmtfreq, MaxOverDueAmountDate from ng_dcc_cust_extexpo_LoanDetails with (nolock) where Wi_Name= '"
				+ wiName + "'  and LoanStat != 'Pipeline' "

				+ "union select CifId,CardEmbossNum,CardType,ProviderNo,CardStatus,CustRoleType,StartDate,ClosedDate,CurrentBalance,'' as col6,"
				+ "PaymentsAmount,NoOfInstallments,'' as col5,WriteoffStat,WriteoffStatDt,CashLimit as CreditLimit,OverdueAmount,NofDaysPmtDelay,MonthsOnBook,lastrepmtdt,IsCurrent,CurUtilRate,"
				+ "DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,qc_amt,qc_emi,CAC_Indicator,Take_Over_Indicator,Consider_For_Obligations,case when "
				+ "IsDuplicate= '1' then 'Y' else 'N' end AS IsDuplicate,avg_utilization,DPD5_Last12Months,DPD60Plus_Last12Months,MaximumOverDueAmount,Pmtfreq, MaxOverDueAmountDate from "
				+ "ng_dcc_cust_extexpo_CardDetails with (nolock) where Wi_Name = '" + wiName
				+ "' and cardstatus != 'Pipeline'   "

				+ "union select CifId,AcctId,AcctType,ProviderNo,AcctStat,CustRoleType,StartDate,ClosedDate,OutStandingBalance,TotalAmount,PaymentsAmount,'','',"
				+ "WriteoffStat,WriteoffStatDt,CreditLimit,OverdueAmount,"
				+ "NofDaysPmtDelay,MonthsOnBook,'',IsCurrent,CurUtilRate,DPD30_Last6Months,DPD60_Last12Months,AECBHistMonthCnt,DPD5_Last3Months,'','','','',"
				+ "isnull(Consider_For_Obligations,'true'),case when IsDuplicate= '1' then 'Y' else 'N' end AS IsDuplicate,'',DPD5_Last12Months,DPD60Plus_Last12Months,"
				+ "MaximumOverDueAmount,Pmtfreq, MaxOverDueAmountDate from ng_dcc_cust_extexpo_AccountDetails with (nolock)  where Wi_Name  =  '"
				+ wiName + "' "

				+ "union select CifId,ServiceID,ServiceType,ProviderNo,ServiceStat,CustRoleType,SubscriptionDt,SvcExpDt,'','','','','',WriteoffStat,WriteoffStatDt,'',OverDueAmount,"
				+ "NofDaysPmtDelay,MonthsOnBook,'',IsCurrent,CurUtilRate,'',DPD30_Last6Months,AECBHistMonthCnt,DPD5_Last3Months,'','','','',isnull(Consider_For_Obligations,'true')"
				+ ",case when IsDuplicate= '1' then 'Y' else 'N' end AS IsDuplicate,'',DPD5_Last12Months,DPD60Plus_Last12Months,'','','' from ng_dcc_cust_extexpo_ServicesDetails with (nolock)  "
				+ "where ServiceStat='Active' and wi_name  =  '" + wiName + "'";

		String add_xml_str = "";
		List<Map<String, String>> OutputXML = getDataFromDBMap(sQuery, cabinetName, sessionID, jtsIP, jtsPort);

		for (Map<String, String> map : OutputXML) {

			String ContractType = validateValue(map.get("LoanType"));
			String AgreementId = validateValue(map.get("AgreementId"));
			String phase = validateValue(map.get("LoanStat"));
			String CustRoleType = validateValue(map.get("CustRoleType"));
			String start_date = validateValue(map.get("LoanApprovedDate"));
			String close_date = validateValue(map.get("LoanMaturityDate"));
			String OutStanding_Balance = validateValue(map.get("OutstandingAmt"));
			String TotalAmt = validateValue(map.get("TotalAmt"));
			String PaymentsAmt = validateValue(map.get("PaymentsAmt"));
			String TotalNoOfInstalments = validateValue(map.get("TotalNoOfInstalments"));
			String RemainingInstalments = validateValue(map.get("RemainingInstalments"));
			String WorstStatus = validateValue(map.get("WriteoffStat"));
			String WorstStatusDate = validateValue(map.get("WriteoffStatDt"));
			String CreditLimit = validateValue(map.get("CreditLimit"));
			String OverdueAmt = validateValue(map.get("OverdueAmt"));
			String NofDaysPmtDelay = validateValue(map.get("NofDaysPmtDelay"));
			String MonthsOnBook = validateValue(map.get("MonthsOnBook"));
			String last_repayment_date = validateValue(map.get("lastrepmtdt"));
			String AECBHistMonthCnt = validateValue(map.get("AECBHistMonthCnt"));
			String DPD30Last6Months = validateValue(map.get("DPD30_Last6Months"));
			String currently_current = validateValue(map.get("IsCurrent"));
			String current_utilization = validateValue(map.get("CurUtilRate"));
			String delinquent_in_last_3months = validateValue(map.get("DPD5_Last3Months"));
			String CAC_Indicator = validateValue(map.get("Cac_indicator"));
			String TakeOverIndicator = validateValue(map.get("Take_Over_Indicator"));
			String consider_for_obligation = validateValue(map.get("Consider_For_Obligations"));
			String Duplicate_flag = validateValue(map.get("IsDuplicate"));
			String DPD60plus_last12month = validateValue(map.get("DPD60Plus_Last12Months"));
			String DPD5_last12month = validateValue(map.get("DPD5_Last12Months"));
			String MaximumOverDueAmount = validateValue(map.get("MaximumOverDueAmount"));
			String Pmtfreq = validateValue(map.get("Pmtfreq"));
			String MaxOverDueAmountDate = validateValue(map.get("MaxOverDueAmountDate"));

			if (!ContractType.equals("")) {
				try {
					String cardquery = "select code from ng_master_contract_type with (nolock) where description='"
							+ ContractType + "'";
					Map<String, String> cardqueryXML = getDataFromDB(cardquery, cabinetName, sessionID, jtsIP, jtsPort,
							"code");
					ContractType = cardqueryXML.get("code");
				} catch (Exception e) {
				}
			}

			phase = phase.startsWith("A") ? "A" : "C";

			if (!CustRoleType.equals("")) {
				String sQueryCustRoleType = "select code from ng_master_role_of_customer with(nolock) where Description='"
						+ CustRoleType + "'";
				Map<String, String> cardqueryXML = getDataFromDB(sQueryCustRoleType, cabinetName, sessionID, jtsIP,
						jtsPort, "code");
				try {
					if (cardqueryXML != null && cardqueryXML.size() > 0 && cardqueryXML.get("code") != null) {
						CustRoleType = cardqueryXML.get("code");
					}
				} catch (Exception e) {
				}
			}

			
			String MaxOverdueAmountDateFormat = MaxOverDueAmountDate;
			
			try{
				Date parseDate;

				if(MaxOverDueAmountDate.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")){
					parseDate = new SimpleDateFormat("dd/MM/yyyy").parse(MaxOverDueAmountDate);
					//CreditCard.mLogger.info("parseDate :" + parseDate);
				}
				else if(MaxOverDueAmountDate.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})"))
				{
					parseDate = new SimpleDateFormat("dd-MM-yyyy").parse(MaxOverDueAmountDate);
					//CreditCard.mLogger.info("parseDate :" + parseDate);
				}
				else if(MaxOverDueAmountDate.matches("([0-9]{4})/([0-9]{2})/([0-9]{2})")){
					parseDate = new SimpleDateFormat("yyyy/MM/dd").parse(MaxOverDueAmountDate);
					//CreditCard.mLogger.info("parseDate :" + parseDate);
				}
				else{
					parseDate = new SimpleDateFormat("yyyy-MM-dd").parse(MaxOverDueAmountDate);
				}

				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				//CreditCard.mLogger.info("formatter :" + formatter);

				MaxOverdueAmountDateFormat = formatter.format(parseDate);
				Digital_CC.mLogger.debug("MaxOverdueAmountDateFormat :" + MaxOverdueAmountDateFormat);
			}
			catch(Exception e){
				Digital_CC.mLogger.debug("Exception occured in conversion of MaxOverDueAmountDate :" + e.getMessage());
			}


			
			CAC_Indicator = "true".equalsIgnoreCase(CAC_Indicator) ? "Y" : "N";

			TakeOverIndicator = "true".equalsIgnoreCase(TakeOverIndicator) ? "Y" : "N";

			consider_for_obligation = "true".equalsIgnoreCase(consider_for_obligation) ? "Y" : "N";

			add_xml_str += "<ExternalBureauIndividualProducts><applicant_id>" + wiName + "</applicant_id>" + "";
			add_xml_str += "<external_bureau_individual_products_id>" + AgreementId	+ "</external_bureau_individual_products_id>" + "";//Deepak changes for Dectech call -- 25/10/22
			add_xml_str += "<contract_type>"+ContractType+"</contract_type>" + ""; // Default //Deepak changes for Dectech call -- 25/10/22
																			// to
																			// SerExp
			add_xml_str += "<provider_no>" + map.get("ProviderNo") + "</provider_no>" + "";
			add_xml_str += "<phase>"+phase+"</phase>" + ""; // Default //Deepak changes for Dectech call -- 25/10/22
			add_xml_str += "<role_of_customer>" + CustRoleType + "</role_of_customer>" + "";
			add_xml_str += "<start_date>" + start_date + "</start_date>" + "";

			add_xml_str += "<close_date>" + close_date + "</close_date>" + "";
			add_xml_str += "<outstanding_balance>" + OutStanding_Balance + "</outstanding_balance>" + "";
			add_xml_str += "<total_amount>" + TotalAmt + "</total_amount>" + "";
			add_xml_str += "<payments_amount>" + PaymentsAmt + "</payments_amount>" + "";
			add_xml_str += "<total_no_of_instalments>" + TotalNoOfInstalments + "</total_no_of_instalments>" + "";
			add_xml_str += "<no_of_remaining_instalments>" + RemainingInstalments + "</no_of_remaining_instalments>"
					+ "";
			add_xml_str += "<worst_status>" + WorstStatus + "</worst_status>" + "";
			add_xml_str += "<worst_status_date>" + WorstStatusDate + "</worst_status_date>" + "";
			add_xml_str += "<credit_limit>" + CreditLimit + "</credit_limit>" + "";

			add_xml_str += "<overdue_amount>" + OverdueAmt + "</overdue_amount>" + "";
			add_xml_str += "<no_of_days_payment_delay>" + NofDaysPmtDelay + "</no_of_days_payment_delay>" + "";
			add_xml_str += "<mob>" + MonthsOnBook + "</mob>" + "";
			add_xml_str += "<last_repayment_date>" + last_repayment_date + "</last_repayment_date>" + "";

			if (currently_current != null && "1".equalsIgnoreCase(currently_current)) {
				add_xml_str += "<currently_current>Y</currently_current>" + "";
			} else {
				add_xml_str += "<currently_current>N</currently_current>" + "";
			}

			add_xml_str += "<current_utilization>" + current_utilization + "</current_utilization>" + "";
			add_xml_str += "<dpd_5_in_last_12_mon>" + DPD5_last12month + "</dpd_5_in_last_12_mon>" + "";
			add_xml_str += "<dpd_30_last_6_mon>" + DPD30Last6Months + "</dpd_30_last_6_mon>" + "";
			add_xml_str += "<dpd_60p_in_last_12_mon>" + DPD60plus_last12month + "</dpd_60p_in_last_12_mon>" + "";
			add_xml_str += "<no_months_aecb_history>" + AECBHistMonthCnt + "</no_months_aecb_history>" + "";
			add_xml_str += "<maximum_overdue_amount>" + MaximumOverDueAmount + "</maximum_overdue_amount>" + "";// added
																												// by
																												// deppanshu
			add_xml_str += "<delinquent_in_last_3months>" + delinquent_in_last_3months + "</delinquent_in_last_3months>"
					+ "";
			add_xml_str += "<company_flag>N</company_flag>" + "";
			add_xml_str += "<consider_for_obligation>Y</consider_for_obligation>" + "";
			add_xml_str += "<duplicate_flag>" + Duplicate_flag + "</duplicate_flag>" + "";
			add_xml_str += "<payment_frequency>" + Pmtfreq + "</payment_frequency>" + "";
			add_xml_str += "<maximum_overdue_date>" + MaxOverdueAmountDateFormat + "</maximum_overdue_date>" + "";
			add_xml_str += "</ExternalBureauIndividualProducts>";

		}

		try {
			String Expense1 = "0", Expense2 = "0", Expense3 = "0", Expense4 = "0";

			String Expense_Query = "select isnull(Expense1,'0') as Expense1 ,isnull(Expense2,'0') as Expense2,isnull(Expense3,'0') as Expense3,isnull(Expense4,'0') as Expense4 from NG_DCC_EXTTABLE with(nolock) where WI_NAME ='"
					+ wiName + "'";

			List<Map<String, String>> OutputXML_Expense = getDataFromDBMap(Expense_Query, cabinetName, sessionID, jtsIP,
					jtsPort);

			Expense1 = OutputXML_Expense.get(0).get("Expense1");
			Expense2 = OutputXML_Expense.get(0).get("Expense2");
			Expense3 = OutputXML_Expense.get(0).get("Expense3");
			Expense4 = OutputXML_Expense.get(0).get("Expense4");

			//Updated 07122022 - Kamran Active to A
			String Lifestyle = "<ExternalBureauIndividualProducts>" + "<applicant_id>" + wiName + "</applicant_id>"
					+ "<external_bureau_individual_products_id>" + wiName + "</external_bureau_individual_products_id>"
					+ "<contract_type>L1</contract_type>" + "<phase>A</phase>" + "<payments_amount>" + Expense1
					+ "</payments_amount>" + "</ExternalBureauIndividualProducts>";

			Lifestyle += "<ExternalBureauIndividualProducts>" + "<applicant_id>" + wiName + "</applicant_id>"
					+ "<external_bureau_individual_products_id>" + wiName + "</external_bureau_individual_products_id>"
					+ "<contract_type>SerExp</contract_type>" + "<phase>A</phase>" + "<payments_amount>" + Expense2
					+ "</payments_amount>" + "</ExternalBureauIndividualProducts>";

			Lifestyle += "<ExternalBureauIndividualProducts>" + "<applicant_id>" + wiName + "</applicant_id>"
					+ "<external_bureau_individual_products_id>" + wiName + "</external_bureau_individual_products_id>"
					+ "<contract_type>AdLnTake</contract_type>" + "<phase>A</phase>" + "<payments_amount>"
					+ Expense3 + "</payments_amount>" + "</ExternalBureauIndividualProducts>";

			Lifestyle += "<ExternalBureauIndividualProducts>" + "<applicant_id>" + wiName + "</applicant_id>"
					+ "<external_bureau_individual_products_id>" + wiName + "</external_bureau_individual_products_id>"
					+ "<contract_type>AnOthExp</contract_type>" + "<phase>A</phase>" + "<payments_amount>"
					+ Expense4 + "</payments_amount>" + "</ExternalBureauIndividualProducts>";

			add_xml_str += Lifestyle;
		} catch (Exception e) {
		}

		return add_xml_str;
	}

	private static String sInputXmlExternalBureauPipelineProducts(String wiName, String cabinetName, String sessionID,
			String jtsIP, String jtsPort) {
		String sQuery = "select CifId, AgreementId,ProviderNo,LoanType,LoanDesc,CustRoleType,Datelastupdated,TotalAmt,TotalNoOfInstalments,CreditLimit,'' as col1,NoOfDaysInPipeline,"
				+ "isnull(Consider_For_Obligations,'true') as 'Consider_For_Obligations', case when IsDuplicate= '1' then 'Y' else 'N' end as 'IsDuplicate' from ng_dcc_cust_extexpo_LoanDetails with (nolock) "
				+ "where Wi_Name  =  '" + wiName + "' and LoanStat = 'Pipeline'"
				+ "union select CifId, CardEmbossNum,ProviderNo,CardType,CardTypeDesc, CustRoleType,LastUpdateDate,'' as col2,NoOfInstallments, '' as col3, TotalAmount, "
				+ "NoOfDaysInPipeLine,isnull(Consider_For_Obligations,'true') as 'Consider_For_Obligations',case when IsDuplicate= '1' then 'Y' else 'N' end as 'IsDuplicate' from ng_dcc_cust_extexpo_CardDetails "
				+ "with (nolock) where Wi_Name  =  '" + wiName + "' and cardstatus = 'Pipeline'";

		String add_xml_str = "";
		List<Map<String, String>> maps = getDataFromDBMap(sQuery, cabinetName, sessionID, jtsIP, jtsPort);

		for (Map<String, String> map : maps) {

			String contractType = validateValue(map.get("LoanType"));
			String role = validateValue(map.get("CustRoleType"));
			// String lastUpdateDate =
			// validateValue(map.get("Datelastupdated"));
			// String
			// consider_for_obligation=validateValue(map.get("Consider_For_Obligations"));

			if (!contractType.equals("")) {
				try {
					String cardquery = "select code from ng_master_contract_type with (nolock) where description='"
							+ contractType + "'";
					Map<String, String> cardqueryXML = getDataFromDB(cardquery, cabinetName, sessionID, jtsIP, jtsPort,
							"code");
					contractType = cardqueryXML.get("code");
				} catch (Exception e) {
				}
			}

			if (!role.equals("")) {
				String sQueryCustRoleType = "select code from ng_master_role_of_customer with(nolock) where Description='"
						+ role + "'";
				Map<String, String> cardqueryXML = getDataFromDB(sQueryCustRoleType, cabinetName, sessionID, jtsIP,
						jtsPort, "code");
				try {
					if (cardqueryXML != null && cardqueryXML.size() > 0 && cardqueryXML.get("code") != null) {
						role = cardqueryXML.get("code");
					}
				} catch (Exception e) {
				}
			}

			/*
			 * if (!"".equalsIgnoreCase(consider_for_obligation) &&
			 * "true".equalsIgnoreCase(consider_for_obligation)) {
			 * consider_for_obligation = "Y"; } else { consider_for_obligation =
			 * "N"; }
			 */

			add_xml_str += "<ExternalBureauPipelineProducts><applicant_ID>" + wiName + "</applicant_ID>" + "";
			add_xml_str += "<external_bureau_pipeline_products_id>" + validateValue(map.get("AgreementId"))
					+ "</external_bureau_pipeline_products_id>" + "";
			add_xml_str += "<ppl_provider_no>" + validateValue(map.get("ProviderNo")) + "</ppl_provider_no>" + "";
			add_xml_str += "<ppl_type_of_contract>" + contractType + "</ppl_type_of_contract>" + "";
			add_xml_str += "<ppl_type_of_product>" + validateValue(map.get("LoanDesc")) + "</ppl_type_of_product>" + "";
			add_xml_str += "<ppl_phase>" + "PIPELINE" + "</ppl_phase>" + "";
			add_xml_str += "<ppl_role>" + role + "</ppl_role>" + "";
			add_xml_str += "<ppl_date_of_last_update>" + validateValue(map.get("Datelastupdated"))
					+ "</ppl_date_of_last_update>" + "";
			// add_xml_str +="<ppl_total_amount>" +
			// validateValue(map.get("TotalAmt")) + "</ppl_total_amount>"+ "";
			add_xml_str += "<ppl_no_of_instalments>" + validateValue(map.get("TotalNoOfInstalments"))
					+ "</ppl_no_of_instalments>" + "";
			if (validateValue(map.get("LoanType")).toUpperCase().contains("LOAN")) {
				add_xml_str += "<ppl_total_amount>" + validateValue(map.get("TotalAmt")) + "</ppl_total_amount>" + "";
			} else {
				add_xml_str += "<ppl_credit_limit>" + validateValue(map.get("col1")) + "</ppl_credit_limit>" + "";
			}
			add_xml_str += "<ppl_no_of_days_in_pipeline>" + validateValue(map.get("NoOfDaysInPipeline"))
					+ "</ppl_no_of_days_in_pipeline>" + "";
			add_xml_str += "<company_flag>N</company_flag>" + "";
			add_xml_str += "<ppl_consider_for_obligation>Y</ppl_consider_for_obligation>" + "";
			add_xml_str += "<ppl_duplicate_flag>" + validateValue(map.get("IsDuplicate"))
					+ "</ppl_duplicate_flag></ExternalBureauPipelineProducts>";
		}
		return add_xml_str;
	}
	


	private static String sInputXmlPerfios(Map<String, String> applicantDetails_Map, Map<String, String> ApplicationDetailsOldSalary_Map) {
		String add_xml_str = "<Perfios>";
		//Deepak Changes done (30-March-23) for JIRA PDSC-342 - Net sal and Perfios 1 2 3 to be sent as per notify call
				//if(applicantDetails_Map.containsKey("FTS_Ack_flg") && "Y".equalsIgnoreCase(applicantDetails_Map.get("FTS_Ack_flg"))){
					add_xml_str=add_xml_str	+"<Stmt_Salary_1>"+ApplicationDetailsOldSalary_Map.get("Net_Salary1")+"</Stmt_Salary_1>"
							+"<Stmt_salary1_date>"+applicantDetails_Map.get("Net_salary1_date")+"</Stmt_salary1_date>"
							+"<Stmt_salary_2>"+ApplicationDetailsOldSalary_Map.get("Net_Salary2")+"</Stmt_salary_2>"
							+"<Stmt_salary2_date>"+applicantDetails_Map.get("Net_salary2_date")+"</Stmt_salary2_date>"
							+"<Stmt_salary_3>"+ApplicationDetailsOldSalary_Map.get("Net_Salary3")+"</Stmt_salary_3>"
							+"<Stmt_salary3_date>"+applicantDetails_Map.get("Net_salary3_date")+"</Stmt_salary3_date>";
			/*	}
				else{
					add_xml_str=add_xml_str	+"<Stmt_Salary_1>"+applicantDetails_Map.get("Net_Salary1")+"</Stmt_Salary_1>"
							+"<Stmt_salary1_date>"+applicantDetails_Map.get("Net_salary1_date")+"</Stmt_salary1_date>"
							+"<Stmt_salary_2>"+applicantDetails_Map.get("Net_Salary2")+"</Stmt_salary_2>"
							+"<Stmt_salary2_date>"+applicantDetails_Map.get("Net_salary2_date")+"</Stmt_salary2_date>"
							+"<Stmt_salary_3>"+applicantDetails_Map.get("Net_Salary3")+"</Stmt_salary_3>"
							+"<Stmt_salary3_date>"+applicantDetails_Map.get("Net_salary3_date")+"</Stmt_salary3_date>";
				}*/
				/*+"<Stmt_Salary_1>"+ApplicationDetailsOldSalary_Map.get("Net_Salary1")+"</Stmt_Salary_1>"
				+"<Stmt_salary1_date>"+applicantDetails_Map.get("Net_salary1_date")+"</Stmt_salary1_date>"
				+"<Stmt_salary_2>"+ApplicationDetailsOldSalary_Map.get("Net_Salary2")+"</Stmt_salary_2>"
				+"<Stmt_salary2_date>"+applicantDetails_Map.get("Net_salary2_date")+"</Stmt_salary2_date>"
				+"<Stmt_salary_3>"+ApplicationDetailsOldSalary_Map.get("Net_Salary3")+"</Stmt_salary_3>"
				+"<Stmt_salary3_date>"+applicantDetails_Map.get("Net_salary3_date")+"</Stmt_salary3_date>"*/
				add_xml_str=add_xml_str	
				+"<Stmt_salary_4>"+applicantDetails_Map.get("Net_Salary4")+ "</Stmt_salary_4>" 
				+ "<Stmt_salary4_date>" + applicantDetails_Map.get("Net_salary4_date")
				+ "</Stmt_salary4_date>" + "<Stmt_salary_5>" + applicantDetails_Map.get("Net_Salary5")
				+ "</Stmt_salary_5>" + "<Stmt_salary5_date>" + applicantDetails_Map.get("Net_salary5_date")
				+ "</Stmt_salary5_date>" + "<Stmt_salary_6>" + applicantDetails_Map.get("Net_Salary6")
				+ "</Stmt_salary_6>" + "<Stmt_salary6_date>" + applicantDetails_Map.get("Net_salary6_date")
				+ "</Stmt_salary6_date>" + "<Stmt_salary_7>" + applicantDetails_Map.get("Net_Salary7")
				+ "</Stmt_salary_7>" + "<Stmt_salary7_date>" + applicantDetails_Map.get("Net_salary7_date")
				+ "</Stmt_salary7_date>" + "<Addn_Perfios_EMI_1>" + applicantDetails_Map.get("Addn_Perfios_EMI_1")
				+ "</Addn_Perfios_EMI_1>" + "<Addn_Perfios_EMI_2>" + applicantDetails_Map.get("Addn_Perfios_EMI_2")
				+ "</Addn_Perfios_EMI_2>" + "<Addn_Perfios_EMI_3>" + applicantDetails_Map.get("Addn_Perfios_EMI_3")
				+ "</Addn_Perfios_EMI_3>" + "<Addn_Perfios_EMI_4>" + applicantDetails_Map.get("Addn_Perfios_EMI_4")
				+ "</Addn_Perfios_EMI_4>" + "<Addn_Perfios_EMI_5>" + applicantDetails_Map.get("Addn_Perfios_EMI_5")
				+ "</Addn_Perfios_EMI_5>" + "<Addn_Perfios_EMI_6>" + applicantDetails_Map.get("Addn_Perfios_EMI_6")
				+ "</Addn_Perfios_EMI_6>" + "<Addn_Perfios_EMI_7>" + applicantDetails_Map.get("Addn_Perfios_EMI_7")
				+ "</Addn_Perfios_EMI_7>" + "<Addn_Perfios_EMI_8>" + applicantDetails_Map.get("Addn_Perfios_EMI_8")
				+ "</Addn_Perfios_EMI_8>" + "<Addn_Perfios_EMI_9>" + applicantDetails_Map.get("Addn_Perfios_EMI_9")
				+ "</Addn_Perfios_EMI_9>" + "<Addn_Perfios_EMI_10>" + applicantDetails_Map.get("Addn_Perfios_EMI_10")
				+ "</Addn_Perfios_EMI_10>" + "<Addn_Perfios_EMI_11>" + applicantDetails_Map.get("Addn_Perfios_EMI_11")
				+ "</Addn_Perfios_EMI_11>" + "<Addn_Perfios_EMI_12>" + applicantDetails_Map.get("Addn_Perfios_EMI_12")
				+ "</Addn_Perfios_EMI_12>" + "<Addn_Perfios_EMI_13>" + applicantDetails_Map.get("Addn_Perfios_EMI_13")
				+ "</Addn_Perfios_EMI_13>" + "<Addn_Perfios_EMI_14>" + applicantDetails_Map.get("Addn_Perfios_EMI_14")
				+ "</Addn_Perfios_EMI_14>" + "<Addn_Perfios_EMI_15>" + applicantDetails_Map.get("Addn_Perfios_EMI_15")
				+ "</Addn_Perfios_EMI_15>" + "<Addn_Perfios_EMI_16>" + applicantDetails_Map.get("Addn_Perfios_EMI_16")
				+ "</Addn_Perfios_EMI_16>" + "<Addn_Perfios_EMI_17>" + applicantDetails_Map.get("Addn_Perfios_EMI_17")
				+ "</Addn_Perfios_EMI_17>" + "<Addn_Perfios_EMI_18>" + applicantDetails_Map.get("Addn_Perfios_EMI_18")
				+ "</Addn_Perfios_EMI_18>" + "<Addn_Perfios_EMI_19>" + applicantDetails_Map.get("Addn_Perfios_EMI_19")
				+ "</Addn_Perfios_EMI_19>" + "<Addn_Perfios_EMI_20>" + applicantDetails_Map.get("Addn_Perfios_EMI_20")
				+ "</Addn_Perfios_EMI_20>" + "<Addn_Perfios_CC>" + applicantDetails_Map.get("Addn_Perfios_CC")
				+ "</Addn_Perfios_CC>" + "<Addn_Perfios_OD_Amt>" + applicantDetails_Map.get("Addn_Perfios_OD_Amt")
				+ "</Addn_Perfios_OD_Amt>" + "<Addn_OD_date>" + applicantDetails_Map.get("Addn_OD_date")
				+ "</Addn_OD_date>" + "<Joint_Acct>" + applicantDetails_Map.get("Joint_Acct") + "</Joint_Acct>"
				+ "<High_value_deposit>" + applicantDetails_Map.get("High_Value_Deposit") + "</High_value_deposit>"
				+ "<Credit_amount>" + applicantDetails_Map.get("Credit_Amount") + "</Credit_amount>"
				+ "<Stmt_chq_rtn_last_3mnts>" + applicantDetails_Map.get("Stmt_chq_rtn_last_3mnts")
				+ "</Stmt_chq_rtn_last_3mnts>" + "<Stmt_chq_rtn_cleared_in30_last_3mnts>"
				+ applicantDetails_Map.get("Stmt_chq_rtn_cleared_in30_last_3mnts")
				+ "</Stmt_chq_rtn_cleared_in30_last_3mnts>" + "<Stmt_chq_rtn_last_1mnt>"
				+ applicantDetails_Map.get("Stmt_chq_rtn_last_1mnt") + "</Stmt_chq_rtn_last_1mnt>"
				+ "<Stmt_chq_rtn_cleared_in30_last_1mnt>"
				+ applicantDetails_Map.get("Stmt_chq_rtn_cleared_in30_last_1mnt")
				+ "</Stmt_chq_rtn_cleared_in30_last_1mnt>" + "<Stmt_DDS_rtn_last_3mnts>"
				+ applicantDetails_Map.get("Stmt_DDS_rtn_last_3mnts") + "</Stmt_DDS_rtn_last_3mnts>"
				+ "<Stmt_DDS_rtn_cleared_in30_last_3mnts>"
				+ applicantDetails_Map.get("Stmt_DDS_rtn_cleared_in30_last_3mnts")
				+ "</Stmt_DDS_rtn_cleared_in30_last_3mnts>" + "<Stmt_DDS_rtn_last_1mnt>"
				+ applicantDetails_Map.get("Stmt_DDS_rtn_last_1mnt") + "</Stmt_DDS_rtn_last_1mnt>"
				+ "<Stmt_DDS_rtn_cleared_in30_last_1mnts>"
				+ applicantDetails_Map.get("Stmt_DDS_rtn_cleared_in30_last_1mnts")
				+ "</Stmt_DDS_rtn_cleared_in30_last_1mnts>" + "<Pensioner>" + applicantDetails_Map.get("Pensioner")
				+ "</Pensioner>" + "<Name_match>" + applicantDetails_Map.get("Name_match") + "</Name_match>"
				+ "<FCU_indicator>" + applicantDetails_Map.get("FCU_indicator") + "</FCU_indicator>" + "<UW_reqd>"
				+ applicantDetails_Map.get("UW_reqd") + "</UW_reqd>" + "</Perfios>";
		return add_xml_str;
	}

	private static String validateValue(String value) {
		if (value != null && !value.equals("") && !value.equalsIgnoreCase("null")) {
			return value.toString();
		}
		return "";
	}

	private static String getTagValues(String sXML, String sTagName) {
		String sTagValues = "";
		String sStartTag = "<" + sTagName + ">";
		String sEndTag = "</" + sTagName + ">";
		String tempXML = sXML;
		try {

			for (int i = 0; i < sXML.split(sEndTag).length; i++) {
				if (tempXML.indexOf(sStartTag) != -1) {
					sTagValues += tempXML.substring(tempXML.indexOf(sStartTag) + sStartTag.length(),
							tempXML.indexOf(sEndTag));
					tempXML = tempXML.substring(tempXML.indexOf(sEndTag) + sEndTag.length(), tempXML.length());
				}
				if (tempXML.indexOf(sStartTag) != -1) {
					sTagValues += "`";
					// System.out.println("sTagValues"+sTagValues);
				}
				// System.out.println("sTagValues"+sTagValues);
			}
			// System.out.println(" Final sTagValues"+sTagValues);
		} catch (Exception e) {
		}
		return sTagValues;
	}

}
