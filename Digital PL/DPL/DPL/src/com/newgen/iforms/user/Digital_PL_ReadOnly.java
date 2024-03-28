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

public class Digital_PL_ReadOnly extends Digital_PL_Common {

	public String onevent(IFormReference iform, String controlName, String event, String data) {
		Digital_PL.mLogger.debug("onReadOnly:::::::::::::::::::::::::::::::::::::");
		String returnValue = "";
		String Workstep = iform.getActivityName();
		
		return returnValue;
	}

}
