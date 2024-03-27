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

public class Digital_CC_ViewAECB extends Digital_CC_Common {
	
	public String onevent(IFormReference iformObj, String control, String StringData) {
		String returnValue="";
		List lstReportUrls = iformObj.getDataFromDB("SELECT top 1 ReportURL FROM ng_dcc_cust_extexpo_Derived WITH(NOLOCK) WHERE Wi_Name='"+getWorkitemName(iformObj)+"'");
		
		String value="";
		for(int i=0;i<lstReportUrls.size();i++)
		{
			List<String> arr1=(List)lstReportUrls.get(i);
			value=arr1.get(0);
			Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", value : "+value);
			returnValue=value;
		}
		
		return returnValue;
	}

}
