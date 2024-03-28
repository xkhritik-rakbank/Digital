package com.newgen.iforms.user;

import com.newgen.iforms.custom.IFormReference;
import com.newgen.mvcbeans.model.wfobjects.WDGeneralData;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;

public class Digital_CC_ReadOnly extends Digital_CC_Common {

	public String onevent(IFormReference iform, String controlName, String event, String data) {
		Digital_CC.mLogger.debug("onReadOnly:::::::::::::::::::::::::::::::::::::");
		String returnValue = "";
		String Workstep = iform.getActivityName();
		try {
			if ("DCC_Form".equalsIgnoreCase(controlName)) {
				iform.setStyle("DCC_EmploymentDetails", "visible", "true");
				iform.setStyle("DCC_BankingDetails", "visible", "true");
				iform.setStyle("DCC_AECB_Exposure", "visible", "true");
				iform.setStyle("DCC_AECBPipelines", "visible", "true");
				iform.setStyle("DCC_RunPolicy", "visible", "true");
				iform.setStyle("DeviationDescription", "visible", "false");
				// sections
				iform.setStyle("DCC_PersonalDetails", "disable", "true");
				iform.setStyle("DCC_EmploymentDetails", "disable", "true");
				iform.setStyle("DCC_BankingDetails", "disable", "true");
				iform.setStyle("DCC_RunPolicy", "disable", "true");
				iform.setStyle("DCC_SuppCardDetails", "disable", "true");
				iform.setStyle("DCC_LiabilityAddition", "disable", "true");
				iform.setStyle("DCC_AECBPipelines", "disable", "true");
				iform.setStyle("DCC_AdditionofChecque", "disable", "true");
				iform.setStyle("DocTypeSection", "disable", "true");
				iform.setStyle("Decision_DCC", "disable", "true");
				//iform.setStyle("RM_Code", "disable", "true");
				// AECB Grid
				iform.setStyle("wrost_status_date", "disable", "true");
				iform.setStyle("external_charge_of", "disable", "true");
				iform.setStyle("AECB_history", "disable", "true");
				iform.setStyle("cheque_dds_return", "disable", "true");
				iform.setStyle("Aecb_score", "disable", "true");
				iform.setStyle("Score_range", "disable", "true");
				iform.setStyle("bureau_reference_number", "disable", "true");
				iform.setStyle("DCC_AECB_Exposure_Grid", "disable", "true");
				//added by om on 19/10/22
				iform.setStyle("Final_Limit", "visible", "true");
				iform.setStyle("Final_Limit", "disable", "true");
				iform.setStyle("Decision", "disable", "true");
				iform.setStyle("Remarks","disable", "true");
				
				// Hritik 03.01.24 PDSC-1300
				iform.setStyle("DCC_Internal_Exposure", "visible", "true");
				iform.setStyle("Internal_Exposure", "visible", "true");
				iform.setStyle("DCC_Internal_Exposure", "disable", "true");
				iform.setStyle("Internal_Exposure", "disable", "true");
				
				if ("Exceptions".equalsIgnoreCase(Workstep.trim()) ) {
					iform.setStyle("DeviationDescription", "visible", "false");
					iform.setStyle("Active_cards","visible", "true");
					iform.setStyle("Active_card", "visible", "true");
					iform.setStyle("Active_card", "disable", "true");
				}
				else if(Workstep.equalsIgnoreCase("Source_Refer"))
				{
					iform.setStyle("DeviationDescription", "visible", "true");
					iform.setStyle("DeviationDescription", "disable", "true");
				}
				else{
					iform.setStyle("DeviationDescription", "visible", "false");
				}
			}
		} catch (Exception exc) {
			Digital_CC.printException(exc);
			Digital_CC.mLogger.debug("onReadOnly:::::::::::::::::: Exception :::::::::::::::::::" + exc);
		}

		return returnValue;
	}

}