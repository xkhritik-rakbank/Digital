/*
---------------------------------------------------------------------------------------------------------
                  NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group                   : Application - Projects
Project/Product			: CAS
Application				: CAS Document Utility
Module					: Falcon Document
File Name				: FalconDocument.java
Author 					: Sajan
Date (DD/MM/YYYY)		: 05/12/2019

---------------------------------------------------------------------------------------------------------
                 	CHANGE HISTORY
---------------------------------------------------------------------------------------------------------

Problem No/CR No        Change Date           Changed By             Change Description
---------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------
*/


package com.newgen.DigitalCC.ApplicationForm;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.*;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;

public class DCCGenerateAppForm implements Runnable{	
	
	private static  String cabinetName;
	private static  String jtsIP;
	private static  String jtsPort;
	private static  String smsPort;
	private  String [] attributeNames;	
	private static String ExternalTable;
	private static String destFilePath;
	private static String ErrorFolder;
	private static String volumeID;
	private static String MaxNoOfTries;
	private static int TimeIntervalBetweenTrialsInMin;
	static String lastWorkItemId = "";
	static String lastProcessInstanceId = "";
	private  int mainCode;
	Date now=null;
	public static String sdate="";
	public static String source=null;
	public static String dest=null;
	public static String TimeStamp="";
	public static String newFilename=null;
	private static String sessionId;
	public static int sessionCheckInt=0;
	public static int waitLoop=50;
	public static int loopCount=50;
	String loanType = "";
	
	static Map<String, String> falconDocumentCofigParamMap= new HashMap<String, String>();
	private static NGEjbClient ngEjbClientFalconDocument;	
	URL url;
    URLConnection connection;
	
	public void run()
	{
		int sleepIntervalInMin=0;
		try
		{
			DCCAppFormLog.setLogger();
			ngEjbClientFalconDocument = NGEjbClient.getSharedInstance();

			DCCAppFormLog.mLogger.debug("Connecting to Cabinet.");

			int configReadStatus = readConfig();

			DCCAppFormLog.mLogger.debug("configReadStatus "+configReadStatus);
			if(configReadStatus !=0)
			{
				DCCAppFormLog.mLogger.error("Could not Read Config Properties [FalconDocument]");
				return;
			}

			cabinetName = CommonConnection.getCabinetName();
			DCCAppFormLog.mLogger.debug("Cabinet Name: " + cabinetName);

			jtsIP = CommonConnection.getJTSIP();
			DCCAppFormLog.mLogger.debug("JTSIP: " + jtsIP);

			jtsPort = CommonConnection.getJTSPort();
			DCCAppFormLog.mLogger.debug("JTSPORT: " + jtsPort);

			smsPort = CommonConnection.getsSMSPort();
			DCCAppFormLog.mLogger.debug("SMSPort: " + smsPort);			

			sleepIntervalInMin=Integer.parseInt(falconDocumentCofigParamMap.get("SleepIntervalInMin"));
			DCCAppFormLog.mLogger.debug("SleepIntervalInMin: "+sleepIntervalInMin);

			attributeNames=falconDocumentCofigParamMap.get("AttributeNames").split(",");
			DCCAppFormLog.mLogger.debug("AttributeNames: " + attributeNames);

			ExternalTable=falconDocumentCofigParamMap.get("ExtTableName");
			DCCAppFormLog.mLogger.debug("ExternalTable: " + ExternalTable);

			destFilePath=falconDocumentCofigParamMap.get("destFilePath");
			DCCAppFormLog.mLogger.debug("destFilePath: " + destFilePath);

			ErrorFolder=falconDocumentCofigParamMap.get("failDestFilePath");
			DCCAppFormLog.mLogger.debug("ErrorFolder: " + ErrorFolder);

			volumeID=falconDocumentCofigParamMap.get("VolumeID");
			DCCAppFormLog.mLogger.debug("VolumeID: " + volumeID);

			MaxNoOfTries=falconDocumentCofigParamMap.get("MaxNoOfTries");   //Not getting used anywhere Sajan
			DCCAppFormLog.mLogger.debug("MaxNoOfTries: " + MaxNoOfTries);

			//TimeIntervalBetweenTrialsInMin=Integer.parseInt(falconDocumentCofigParamMap.get("TimeIntervalBetweenTrialsInMin"));
			//DCCAppFormLog.mLogger.debug("TimeIntervalBetweenTrialsInMin: " + TimeIntervalBetweenTrialsInMin);
			
			sessionId = CommonConnection.getSessionID(DCCAppFormLog.mLogger, false);
			if(sessionId.trim().equalsIgnoreCase(""))
			{
				DCCAppFormLog.mLogger.debug("Could Not Connect to Server!");
			}
			else
			{
				DCCAppFormLog.mLogger.debug("Session ID found: " + sessionId);
				while(true)
				{
					startFalconDocumentUtility();
					System.out.println("No More workitems to Process, Sleeping!");
					Thread.sleep(sleepIntervalInMin*60*1000);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			DCCAppFormLog.mLogger.error("Exception Occurred in FALCON Document Document Thread: "+e);
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			DCCAppFormLog.mLogger.error("Exception Occurred in FALCON Document Thread : "+result);
		}
	}
	
	private int readConfig()
	{
		Properties p = null;
		try 
		{
			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "ConfigFiles"+ File.separator+ "Falcon_AppForm_Config.properties")));
			Enumeration<?> names = p.propertyNames();
			while (names.hasMoreElements())
			{
			    String name = (String) names.nextElement();
			    falconDocumentCofigParamMap.put(name, p.getProperty(name));
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception in Read INI: "+ e.getMessage());
			DCCAppFormLog.mLogger.error("Exception has occured while loading properties file "+e.getMessage());
			return -1 ;			
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	private void startFalconDocumentUtility() throws Exception
	{
		DCCAppFormLog.mLogger.info("ProcessWI function for Falcon Document Utility started");

		sessionId  = CommonConnection.getSessionID(DCCAppFormLog.mLogger, false);

		if(sessionId==null || sessionId.equalsIgnoreCase("") || sessionId.equalsIgnoreCase("null"))
		{
			DCCAppFormLog.mLogger.error("Could Not Get Session ID "+sessionId);
			return;
		}

		List<String> wiList = new ArrayList<String>();
		try
		{
			wiList = loadWorkItems(sessionId);
		}
		catch (NumberFormatException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		//getDataForCRNECRN();
		//generateCRNECRN();
		
		if (wiList != null)
		{
			for (String wi : wiList)
			{
				String docStatus="";
				String outputDoc="";
				String docIndex="";
				String statusFinal="";
				String mailStatus="";
				String docName ="";
				String portal_no ="";
				if(wi.contains("#")){
					String wi_portal[] = wi.split("#");
					wi = wi_portal[0];
					portal_no = wi_portal[1];
				}
				
				String attrbList = getAttriblist(wi);
				if ("Conventional".equalsIgnoreCase(loanType)){
					docName = "Application_Form_CDOB_C";
				}else{
					docName = "Application_Form_CDOB_I";
				}
				//attrbList = URLEncoder.encode(attrbList, "UTF-8");
				//url = new URL("https://10.15.11.147:9443/webdesktop/custom/CustomJSP/Generate_Template.jsp?attrbList="+attrbList+"&wi_name="+wi+"&docName="+docName+"&sessionId="+sessionId);
				//url.openConnection();
				String status = makeSocketCall(attrbList, wi, docName, sessionId, jtsIP, Integer.parseInt(jtsPort), "", "", "",portal_no);
				DCCAppFormLog.mLogger.info("after socket call app form"+status );
				String statusarr[] = status.split("~"); 
				for(int i=0;i<statusarr.length;i++)
				{
					if(i==0)
						docStatus=statusarr[i];
					if(i==1)
						outputDoc=statusarr[i];
					if(i==2)
						docIndex=statusarr[i];
					if(i==3)
						mailStatus=statusarr[i];
				}
				DCCAppFormLog.mLogger.info("after socket call"+outputDoc );
			System.out.println("Generate_Template.jsp: outputDoc   "+docStatus);

			statusFinal = outputDoc+"~"+docIndex;
			if("Success".equalsIgnoreCase(docStatus)){
				if ("Conventional".equalsIgnoreCase(loanType)){
					docName = "Cheque_Form_C";
				}else{
					docName = "Cheque_Form_I";
				}
				status = makeSocketCall(attrbList, wi, docName, sessionId, jtsIP, Integer.parseInt(jtsPort), "", "", "",portal_no);
				DCCAppFormLog.mLogger.info("after socket call cheque form"+status );
				String statusarrcheque[] = status.split("~"); 
				for(int i=0;i<statusarrcheque.length;i++)
				{
					if(i==0)
						docStatus=statusarrcheque[i];
					if(i==1)
						outputDoc=statusarrcheque[i];
					if(i==2)
						docIndex=statusarrcheque[i];
					if(i==3)
						mailStatus=statusarrcheque[i];
				}
				DCCAppFormLog.mLogger.info("after socket call"+outputDoc );
			/*System.out.println("Generate_Template.jsp: outputDoc   "+docStatus);
			if ("Success".equalsIgnoreCase(docStatus)){
				if ("Conventional".equalsIgnoreCase(loanType)){
					docName = "CoverLetter_C";
				}else{
					docName = "CoverLetter_I";
				}
				status = makeSocketCall(attrbList, wi, docName, sessionId, jtsIP, Integer.parseInt(jtsPort), "", "", "");
			}*/
				loanType = "";
			}

				updateExternalTable("NG_DOB_EXTTABLE", "APP_FORM_STATUS", "'D'" ,"CC_WI_NAME = '"+wi+"'");
			}
		}
		DCCAppFormLog.mLogger.info("exiting ProcessWI function FALCON Document Utility");
	}

	public String getMasterData(String Query, String code ){
		String sQry="";
		String queryOutput="";
		if(Query.equalsIgnoreCase("OfficeCountry") || Query.equalsIgnoreCase("ResidenceCountry") || Query.equalsIgnoreCase("HomeCountry") || Query.equalsIgnoreCase("Nationality"))
		{
			sQry="SELECT isnull(Description,'') as Description FROM NG_MASTER_Country with (nolock) WHERE code = '" +code+"'";
		}
		
		else if(Query.equalsIgnoreCase("Designation"))
		{
			sQry="SELECT isnull(Description,'') as Description FROM NG_MASTER_Designation with (nolock) WHERE code  = '" +code+"'";
		}
		else if(Query.equalsIgnoreCase("OfficeCity") || Query.equalsIgnoreCase("ResidenceCity") || Query.equalsIgnoreCase("HomeCity"))
		{
			sQry="SELECT isnull(Description,'') as Description FROM NG_MASTER_City with (nolock) WHERE code  = '" +code+"'";
		} else if(Query.equalsIgnoreCase("FetchArabic")){
			sQry="select isnull(EngTemplate,'') as Description from ng_master_NoTinReason where EnglishDesc  = '" +code+"'";
		}
			
		String strOutputQry="";
		String commonMethod=CommonMethods.apSelectWithColumnNames(sQry, cabinetName, sessionId);
		try {
			strOutputQry = WFNGExecute(commonMethod, jtsIP, Integer.parseInt(jtsPort), 1);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		XMLParser xmlQueryParser =new XMLParser(strOutputQry);
		queryOutput =xmlQueryParser.getValueOf("Description");
		return queryOutput;
	}
	
	
	private String getAttriblist(String wi) { 
		String attrbList="";
		/*Foru tables are ng_dob_exttable,ng_RLOS_Customer,ng_RLOS_AltContactDetails,ng_RLOS_EmpDetails*/
		String  SixTablesDataFetch = "Select b.title,b.emirateID,b.PassportNo,b.dob,b.motherName,b.lastname,b.firstname,b.middlename,b.Nationality,a.PORTAL_REF_NUMBER,a.CUSTOMERNAME ,a.card_product,a.loan_type,c.MobileNo_pri,"
				+ "c.HomeCountryNo,c.Email1_pri,c.skyward_number,d.desigstatus,d.EmploymentType,d.Employer_Name,d.Emplyer_Idustry_Macro,e.iban_Number,e.FTS_From_Date,e.FTS_To_Date,f.avgNetSal,g.cardEmbossingName,g.amount from ng_dob_exttable a INNER JOIN "
				+ "ng_RLOS_Customer b ON  a.CC_Wi_Name=b.wi_name INNER JOIN ng_RLOS_AltContactDetails c ON c.wi_name = a.cc_wi_name"
				+ " INNER JOIN ng_RLOS_EmpDetails d ON a.cc_wi_name = d.wi_name INNER JOIN ng_RLOS_Liability_New e ON a.cc_wi_name= e.wi_name INNER JOIN ng_RLOS_IncomeDetails f ON a.cc_wi_name= f.wi_name INNER JOIN ng_rlos_cardDetails g ON a.cc_wi_name= g.winame WHERE a.cc_wi_name = '"+wi+"'";
		//String queryexternal = "Select PORTAL_REF_NUMBER,CUSTOMERNAME from ng_dob_exttable where cc_wi_name ='"+wi+"'";
		//String queryCustomer ="Select title,emirateID,PassportNo,dob,motherName,lastname,firstname,middlename from ng_RLOS_Customer where cc_wi_name ='"+wi+"'";
		String addressGridhome = "Select HouseNo,BuildingName,StreetName,PO_Box,City,Country,PrefferedAddress from ng_RLOS_GR_AddressDetails where addressType = 'HOME' and addr_wi_name='"+wi+"' and customerType like 'P-%'";
		String addressGridresidence = "Select HouseNo,BuildingName,StreetName,PO_Box,City,Country,PrefferedAddress from ng_RLOS_GR_AddressDetails where addressType = 'RESIDENCE' and addr_wi_name='"+wi+"' and customerType like 'P-%'";
		String addressGridoffice = "Select HouseNo,BuildingName,StreetName,PO_Box,City,Country,PrefferedAddress from ng_RLOS_GR_AddressDetails where addressType = 'OFFICE' and addr_wi_name='"+wi+"' and customerType like 'P-%'";
		String addressGridMailing = "Select top 1  HouseNo,BuildingName,StreetName,PO_Box,City,Country,PrefferedAddress from ng_RLOS_GR_AddressDetails where  addr_wi_name='"+wi+"' and customerType like 'P-%' and PrefferedAddress='true'";

		//String altcontactgrid = "Select MobileNo_pri,HomeCountryNo,Email1_pri,skyward_number from ng_RLOS_AltContactDetails where wi_name='"+wi+"'";
		String oecdquery = "Select CountryTaxResd,TinNumber,TinReason,CountryOfBirth,CityOfBirth from ng_rlos_GR_OecdDetails where oecd_wi_name = '"+wi+"' and customerType like 'P-%' ";
		String referencequery = "select top 1 ReferenceName,ReferenceMobile from ng_rlos_GR_ReferenceDetails where reference_wi_name = '"+wi+"' order by insertionOrderId desc";
		//String empdetails = "Select from ng_RLOS_EmpDetails where wi_name ='"+wi+"'";
		String fatcaquery =	"select  CustomerType from NG_RLOS_GR_FATCA where FATCA_wi_name = '"+wi+"' and customerType like 'P-%' order by insertionOrderId desc";
		String cardtypeQuery = "Select description from ng_master_carddescription where code = (select cardproduct from ng_rlos_gr_product where Prod_winame = '"+wi+"')";
		String amountquery = "select cast(Amount as numeric) as Amount from NG_RLOS_gr_CardDetails where CardDetailsGR_Winame= '"+wi+"'";
		
		String sInput5Tables=CommonMethods.apSelectWithColumnNames(SixTablesDataFetch, cabinetName, sessionId);
		String sInputhomeaddress=CommonMethods.apSelectWithColumnNames(addressGridhome, cabinetName, sessionId);
		String sInputResidence=CommonMethods.apSelectWithColumnNames(addressGridresidence, cabinetName, sessionId);
		String sInputoffice=CommonMethods.apSelectWithColumnNames(addressGridoffice, cabinetName, sessionId);
		String sInputReference=CommonMethods.apSelectWithColumnNames(referencequery, cabinetName, sessionId);
		String sInputOecd=CommonMethods.apSelectWithColumnNames(oecdquery, cabinetName, sessionId);
		String sInputFatca=CommonMethods.apSelectWithColumnNames(fatcaquery, cabinetName, sessionId);
		String sInputMailing=CommonMethods.apSelectWithColumnNames(addressGridMailing, cabinetName, sessionId);
		String sInputcardproduct=CommonMethods.apSelectWithColumnNames(cardtypeQuery, cabinetName, sessionId);
		String sInputamount=CommonMethods.apSelectWithColumnNames(amountquery, cabinetName, sessionId);

		try{
			
		String strOutputcardprod =WFNGExecute(sInputcardproduct, jtsIP, Integer.parseInt(jtsPort), 1);
		XMLParser xmlParsercardprod=new XMLParser(strOutputcardprod);
		
		String strOutputamount =WFNGExecute(sInputamount, jtsIP, Integer.parseInt(jtsPort), 1);
		XMLParser xmlParseramount=new XMLParser(strOutputamount);
		
		String strOutput5tables =WFNGExecute(sInput5Tables, jtsIP, Integer.parseInt(jtsPort), 1);
		XMLParser xmlParser=new XMLParser(strOutput5tables);
		loanType = xmlParser.getValueOf("loan_type");
		attrbList =attrbList+"&<portal_ref>&"+xmlParser.getValueOf("PORTAL_REF_NUMBER")+"@10";
		attrbList =attrbList+"&<portal_ref_CL>&"+"Serial No.  -  "+xmlParser.getValueOf("PORTAL_REF_NUMBER")+"@10";
		attrbList =attrbList+"&<customer_name>&"+xmlParser.getValueOf("CUSTOMERNAME")+"@10";
		attrbList =attrbList+"&<card_type>&"+xmlParsercardprod.getValueOf("description")+"@10";
		attrbList =attrbList+"&<title>&"+xmlParser.getValueOf("title")+"@10";
		//Below code added by shweta 
		String outputNationality =getMasterData( "Nationality",xmlParser.getValueOf("Nationality"));
		attrbList = attrbList+"&<Nationality>&"+outputNationality+"@10";
		//Above code added by shweta 
		attrbList =attrbList+"&<emirates_id>&"+xmlParser.getValueOf("emirateID")+"@10";
		attrbList =attrbList+"&<passport_no>&"+xmlParser.getValueOf("PassportNo")+"@10";
		String[] dobStr = xmlParser.getValueOf("dob").substring(0,10).split("-");
		String dobPIDOB=dobStr[2]+"/"+dobStr[1]+"/" +dobStr[0];
		String dateOfBirth=dobStr[2]+dobStr[1] +dobStr[0];

		attrbList =attrbList+"&<PIDOB>&"+dobPIDOB+"@10";
		attrbList =attrbList+"&<dateofbirth>&"+dateOfBirth+"@10";	
		attrbList =attrbList+"&<MotherName>&"+xmlParser.getValueOf("motherName")+"@10";
		attrbList =attrbList+"&<lastname>&"+xmlParser.getValueOf("lastname")+"@10";
		attrbList =attrbList+"&<Fname>&"+xmlParser.getValueOf("firstname")+"@10";
		attrbList =attrbList+"&<Mname>&"+xmlParser.getValueOf("middlename")+"@10";
		
		if(null!=xmlParser.getValueOf("MobileNo_pri") && xmlParser.getValueOf("MobileNo_pri").length()>6){
			attrbList =attrbList+"&<MobileNo1_part1>&"+xmlParser.getValueOf("MobileNo_pri").substring(0, 5)+"@10";
			attrbList =attrbList+"&<MobileNo1_part2>&"+xmlParser.getValueOf("MobileNo_pri").substring(5)+"@10";
		}
		if(null!=xmlParser.getValueOf("HomeCountryNo") && xmlParser.getValueOf("HomeCountryNo").length()>6){
			attrbList =attrbList+"&<HomeCOuntryNo_part1>&"+xmlParser.getValueOf("HomeCountryNo").substring(0, 5)+"@10";
			attrbList =attrbList+"&<HomeCOuntryNo_part2>&"+xmlParser.getValueOf("HomeCountryNo").substring(5)+"@10";
		}
		
		attrbList =attrbList+"&<skyward_no>&"+xmlParser.getValueOf("skyward_number")+"@10";
		attrbList =attrbList+"&<nameoncard>&"+xmlParser.getValueOf("cardEmbossingName")+"@10";//done by shweta
		
		//Below code added by shweta 
		String outputDesig =getMasterData( "Designation",xmlParser.getValueOf("desigstatus"));
		attrbList = attrbList+"&<Designation>&"+outputDesig+"@10";
		//Above code added by shweta 
				
		attrbList =attrbList+"&<Email1>&"+xmlParser.getValueOf("Email1_pri")+"@10";
		attrbList =attrbList+"&<iban>&"+xmlParser.getValueOf("iban_Number").replaceAll("AE", "")+"@10";
		String[] FTS_From_Date_Arr = xmlParser.getValueOf("FTS_From_Date").substring(0,10).split("-");
		String  FTS_From_Date=FTS_From_Date_Arr[2]+"/"+FTS_From_Date_Arr[1]+"/" +FTS_From_Date_Arr[0];
		String[] FTS_To_Date_Arr = xmlParser.getValueOf("FTS_To_Date").substring(0,10).split("-");
		String  FTS_To_Date=FTS_To_Date_Arr[2]+"/"+FTS_To_Date_Arr[1]+"/" +FTS_To_Date_Arr[0];
		
		attrbList =attrbList+"&<statement_period>&"+ FTS_From_Date+" to "+FTS_To_Date+"@10";
	
		String emptype = xmlParser.getValueOf("EmploymentType");
		if (emptype.equalsIgnoreCase("Salaried") || emptype.equalsIgnoreCase("S")){
			emptype = "SALARIED";
		}else{
			emptype = "PENSION";
		}
		attrbList += "&<employment_type>&" + emptype+"@10";
		attrbList =attrbList+"&<EmployerName>&"+xmlParser.getValueOf("Employer_Name")+"@10";
		attrbList =attrbList+"&<Macro>&"+xmlParser.getValueOf("Emplyer_Idustry_Macro")+"@10";
		attrbList =attrbList+"&<Net_Salary>&"+xmlParser.getValueOf("avgNetSal")+"@10";//added by shweta
		System.out.println(xmlParseramount.getValueOf("Amount"));
		//System.out.println(numberToWord(Integer.parseInt(xmlParseramount.getValueOf("Amount"))));
		try{
			if (xmlParseramount.getValueOf("Amount")==null || xmlParseramount.getValueOf("Amount").equalsIgnoreCase("null")){
				attrbList =attrbList+"&<amount>&"+"0@10";
				attrbList =attrbList+"&<amount_words>&"+"ZERO@10";
			}
			
			else{
				String number = xmlParseramount.getValueOf("Amount");
				double amount = Double.parseDouble(number);
				DecimalFormat formatter = new DecimalFormat("#,###.00");
				number = formatter.format(amount);
				System.out.println("Converted Amount is "+number);
				attrbList =attrbList+"&<amount>&"+number+"@10";
				attrbList =attrbList+"&<amount_words>&"+numberToWord(Integer.parseInt(xmlParseramount.getValueOf("Amount")))+" DIRHAMS "+"@10";		
			}
		}
		catch(Exception e){
			DCCAppFormLog.mLogger.debug("Exception occured while converting amount into no" + e.getMessage());
			System.out.println("Converted Amount is "+e.getMessage());
		}
		attrbList =attrbList+"&<passp>&PASSPORT"+"@10";

		String strOutputMailing=WFNGExecute(sInputMailing, jtsIP, Integer.parseInt(jtsPort), 1);
		XMLParser xmlParserMailing =new XMLParser(strOutputMailing);
		attrbList = attrbList+"&<MailingAdd_building_name>&"+xmlParserMailing.getValueOf("BuildingName")+"@10";
		attrbList = attrbList+"&<street_home>&"+xmlParserMailing.getValueOf("StreetName")+"@10";
		attrbList = attrbList+"&<MailingAdd_zip_code>&"+xmlParserMailing.getValueOf("PO_Box")+"@10";
		String outputMailCity =getMasterData( "HomeCity",xmlParserMailing.getValueOf("City"));
		attrbList = attrbList+"&<MailingAdd_city>&"+outputMailCity+"@10";	
		String outputMailCountry =getMasterData( "HomeCountry",xmlParserMailing.getValueOf("Country"));
		attrbList = attrbList+"&<MailingAdd_country>&"+outputMailCountry+"@10";
		//above code added by shweta
		String strOutputhomeaddress=WFNGExecute(sInputhomeaddress, jtsIP, Integer.parseInt(jtsPort), 1);
		XMLParser xmlParserhomeaddress =new XMLParser(strOutputhomeaddress);

		
		attrbList = attrbList+"&<flat_home>&"+xmlParserhomeaddress.getValueOf("HouseNo")+"@10";
		attrbList = attrbList+"&<building_home>&"+xmlParserhomeaddress.getValueOf("BuildingName")+"@10";
		attrbList = attrbList+"&<street_home>&"+xmlParserhomeaddress.getValueOf("StreetName")+"@10";
		attrbList = attrbList+"&<pobox_home>&"+xmlParserhomeaddress.getValueOf("PO_Box")+"@10";
		//Below code added by shweta 
		String outputHomeCity =getMasterData( "HomeCity",xmlParserhomeaddress.getValueOf("City"));
		attrbList = attrbList+"&<city_home>&"+outputHomeCity+"@10";	
		String outputHomeCountry =getMasterData( "HomeCountry",xmlParserhomeaddress.getValueOf("Country"));
		attrbList = attrbList+"&<home_ctry>&"+outputHomeCountry+"@10";
		//above code added by shweta
		
		String strOutputResidence=WFNGExecute(sInputResidence, jtsIP, Integer.parseInt(jtsPort), 1);
		XMLParser xmlParserResidence =new XMLParser(strOutputResidence);
		attrbList = attrbList+"&<flat_resi>&"+xmlParserResidence.getValueOf("HouseNo")+"@10";
		attrbList = attrbList+"&<building_resi>&"+xmlParserResidence.getValueOf("BuildingName")+"@10";

		attrbList = attrbList+"&<street_resi>&"+xmlParserResidence.getValueOf("StreetName")+"@10";
		attrbList = attrbList+"&<pobox_resi>&"+xmlParserResidence.getValueOf("PO_Box")+"@10";
		//Below code added by shweta 
		String outputResiCity =getMasterData( "ResidenceCity",xmlParserResidence.getValueOf("City"));
		attrbList = attrbList+"&<city_resi>&"+outputResiCity+"@10";	
		String outputResiCntry =getMasterData( "ResidenceCountry",xmlParserResidence.getValueOf("Country"));
		attrbList = attrbList+"&<res_ctry>&"+outputResiCntry+"@10";
		//above code added by shweta
		
		String strOutputOffice=WFNGExecute(sInputoffice, jtsIP, Integer.parseInt(jtsPort), 1);
		XMLParser xmlParserOffice =new XMLParser(strOutputOffice);
		attrbList = attrbList+"&<pobox_ofc>&"+xmlParserOffice.getValueOf("PO_Box")+"@10";
		//Below code added by shweta 
		String outputOffCity =getMasterData( "OfficeCity",xmlParserOffice.getValueOf("City"));
		attrbList = attrbList+"&<city_ofc>&"+outputOffCity+"@10";	
		String outputOffCntry =getMasterData( "OfficeCountry",xmlParserResidence.getValueOf("Country"));
		attrbList = attrbList+"&<ctry_ofc>&"+outputOffCntry+"@10";
		//above code added by shweta
		attrbList = attrbList+"&<cover_desc>&"+xmlParserOffice.getValueOf("BuildingName")+" "+xmlParserOffice.getValueOf("StreetName")+
				" "+outputOffCity+" "+xmlParserOffice.getValueOf("PO_Box")+"@10";

		String strOutputReference=WFNGExecute(sInputReference, jtsIP, Integer.parseInt(jtsPort), 1);
		XMLParser xmlParserReference =new XMLParser(strOutputReference);
		attrbList = attrbList+"&<Friend1_Name>&"+xmlParserReference.getValueOf("ReferenceName")+"@10";
		if(xmlParserReference.getValueOf("ReferenceMobile").substring(0,5).equals("00971")){
			attrbList = attrbList+"&<Friend1_Mobile_part1>&"+xmlParserReference.getValueOf("ReferenceMobile").substring(0,5)+"@10";
			attrbList = attrbList+"&<Friend1_Mobile_part2>&"+xmlParserReference.getValueOf("ReferenceMobile").substring(5)+"@10";
		}else if (xmlParserReference.getValueOf("ReferenceMobile").substring(0,2).equals("05")){
			attrbList = attrbList+"&<Friend1_Mobile_part1>&00971@10";
			attrbList = attrbList+"&<Friend1_Mobile_part2>&"+xmlParserReference.getValueOf("ReferenceMobile").substring(1)+"@10";
		}
		
		
		String strOutputOecd=WFNGExecute(sInputOecd, jtsIP, Integer.parseInt(jtsPort), 1);
		XMLParser xmlParserOecd =new XMLParser(strOutputOecd);
		String ctryTaxRes = xmlParserOecd.getValueOf("CountryTaxResd");
		ctryTaxRes = getMasterData("HomeCountry", ctryTaxRes);
		attrbList = attrbList+"&<CTR1>&"+ctryTaxRes+"@10";
		attrbList = attrbList+"&<TINNo1>&"+xmlParserOecd.getValueOf("TinNumber")+"@10";
		//Below code added by shweta 
		String outputTINReason =getMasterData( "FetchArabic",xmlParserOecd.getValueOf("TinReason"));
		attrbList = attrbList+"&<NOTINEng1>&"+outputTINReason+"@10";
		//Above code added by shweta
		String picob = xmlParserOecd.getValueOf("CountryOfBirth");
		picob = getMasterData("HomeCountry", xmlParserOecd.getValueOf("CountryOfBirth"));
		attrbList = attrbList+"&<PICOB>&"+picob+"@10";
		attrbList = attrbList+"&<PITOB>&"+xmlParserOecd.getValueOf("CityOfBirth")+"@10";
		//below code added by shweta 
		String strFatcaOutput =WFNGExecute(sInputFatca, jtsIP, Integer.parseInt(jtsPort), 1);
		XMLParser xmlParserFatca=new XMLParser(strFatcaOutput);
		if(xmlParserFatca!=null && xmlParserFatca.getValueOf("customerType")!=null ){
			attrbList = attrbList+"&<fatcaCheck>&"+"YES"+"@10";
		} else {
			attrbList = attrbList+"&<fatcaCheck>&"+"NO"+"@10";
		}
		//above code added by shweta 

		
		
		Date date = new Date();
		SimpleDateFormat mmddyyyy = new SimpleDateFormat("dd/MM/yyyy");
		String today = mmddyyyy.format(date);
		attrbList = attrbList+"&<date_full>&"+today+"@10";
		attrbList = attrbList+"&<date_today>&"+today.replaceAll("/", "")+"@10";
        
		}catch(Exception e){
			
		}
		return attrbList;
	}
	private String numberToWord(Integer number) {
		try{

			// variable to hold string representation of number 
			String words = "";
			String unitsArray[] = { "zero", "one", "two", "three", "four", "five", "six", 
					"seven", "eight", "nine", "ten", "eleven", "twelve",
					"thirteen", "fourteen", "fifteen", "sixteen", "seventeen", 
					"eighteen", "nineteen" };
			String tensArray[] = { "zero", "ten", "twenty", "thirty", "forty", "fifty",
					"sixty", "seventy", "eighty", "ninety" };

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
			DCCAppFormLog.mLogger.debug("Exception occured while converting amount in numberToWord method" + e.getMessage());
			System.out.println("Converted Amount is numberToWord method "+e.getMessage());
			return "";
		}
	}

	public static void waiteloopExecute(long wtime) {
		try {
			for (int i = 0; i < 10; i++) {
				Thread.yield();
				Thread.sleep(wtime / 10);
			}
		} catch (InterruptedException e) {
		}
	}
	
	private void updateExternalTable(String tablename, String columnname,String sMessage, String sWhere)
	{
		sessionCheckInt=0;

		while(sessionCheckInt<loopCount)
		{
			try
			{
				XMLParser objXMLParser = new XMLParser();
				String inputXmlcheckAPUpdate = CommonMethods.getAPUpdateIpXML(tablename,columnname,sMessage,sWhere,cabinetName,sessionId);
				DCCAppFormLog.mLogger.debug("inputXmlcheckAPUpdate : " + inputXmlcheckAPUpdate);
				String outXmlCheckAPUpdate=null;
				outXmlCheckAPUpdate=WFNGExecute(inputXmlcheckAPUpdate,jtsIP,Integer.parseInt(jtsPort),1);
				DCCAppFormLog.mLogger.info("outXmlCheckAPUpdate : " + outXmlCheckAPUpdate);
				objXMLParser.setInputXML(outXmlCheckAPUpdate);
				String mainCodeforCheckUpdate = null;
				mainCodeforCheckUpdate=objXMLParser.getValueOf("MainCode");
				if (!mainCodeforCheckUpdate.equalsIgnoreCase("0"))
				{
					DCCAppFormLog.mLogger.error("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
					System.out.println("Exception in ExecuteQuery_APUpdate updating "+tablename+" table");
				}
				else
				{
					DCCAppFormLog.mLogger.error("Succesfully updated "+tablename+" table");
					System.out.println("Succesfully updated "+tablename+" table");
				}
				mainCode=Integer.parseInt(mainCodeforCheckUpdate);
				if (mainCode == 11)
				{
					sessionId  = CommonConnection.getSessionID(DCCAppFormLog.mLogger, false);
				}
				else
				{
					sessionCheckInt++;
					break;
				}

				if (outXmlCheckAPUpdate.equalsIgnoreCase("") || outXmlCheckAPUpdate == "" || outXmlCheckAPUpdate == null)
					break;

			}
			catch(Exception e)
			{
				DCCAppFormLog.mLogger.error("Inside create validateSessionID exception"+e);
			}
		}
	}
	
	
	
	

	
	public static String get_timestamp()
	{
		Date present = new Date();
		Format pformatter = new SimpleDateFormat("dd-MM-yyyy-hhmmss");
		TimeStamp=pformatter.format(present);
		return TimeStamp;
	}
	
	

	public static boolean deleteDir(File dir) throws Exception {
		if (dir.isDirectory()) {
			String[] lstrChildren = dir.list();
			for (int i = 0; i < lstrChildren.length; i++) {
				boolean success = deleteDir(new File(dir, lstrChildren[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}
	
	public String makeSocketCall( String argumentString, String wi_name, String docName, String sessionId,  String gtIP, int gtPort,String prequired, String pvalue,String userEmail,String portal_no)
	{
		String socketParams=argumentString+"~"+wi_name+"~"+docName+"~"+sessionId+"~"+prequired+"~"+pvalue+"~"+userEmail+"~"+portal_no;
	
	System.out.println("socketParams -- " + socketParams);

		Socket template_socket = null;
		DataOutputStream template_dout=null;
		DataInputStream template_in=null;
		String result="";
		try {
			//Socket write code started
			template_socket  = new Socket(gtIP.replace("/", ""), 6688);
			template_dout=new DataOutputStream(template_socket.getOutputStream());
			if (socketParams != null && socketParams.length() > 0) 
			{
				template_dout.write(socketParams.getBytes("UTF-8"));
				template_dout.flush();
			} else {
				notify();
			}
			//Socket write code ended and read code started
			template_socket.setSoTimeout(60*1000);
			template_in = new DataInputStream (new BufferedInputStream(template_socket.getInputStream()));
			byte[] readBuffer = new byte[50000];
			int num = template_in.read(readBuffer);
			if (num > 0) {
				byte[] arrayBytes = new byte[num];
				System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
				result = new String(arrayBytes, "UTF-8");
			}
		} 

		catch (SocketException se) {
			se.printStackTrace();
		} 
		catch (IOException i) {	
			i.printStackTrace();
		}
		catch (Exception io) {
			io.printStackTrace();
		}
		finally{
			try{
				if(template_dout != null){
					template_dout.close();
					template_dout=null;
				}
				if(template_in != null){
					template_in.close();
					template_in=null;
				}
				if(template_socket != null){
					if(!template_socket.isClosed()){
						template_socket.close();
					}
					template_socket=null;
				}
			}catch(Exception e)
			{e.printStackTrace();}
		}
		return result;
	}
	
	private List<String> loadWorkItems(String sessionId) throws NumberFormatException, IOException, Exception
	{
		//DCCAppFormLog.mLogger.info("Starting loadWorkitem function for queueID -->"+queueID);
		List<String> workItemList = new ArrayList<String>();
		String workItemListInputXML="";
		sessionCheckInt=0;
		String workItemListOutputXML="";
		DCCAppFormLog.mLogger.info("loopCount aa:" + loopCount);
		DCCAppFormLog.mLogger.info("lastWorkItemId aa:" + lastWorkItemId);
		DCCAppFormLog.mLogger.info("lastProcessInstanceId aa:" + lastProcessInstanceId);
		while(sessionCheckInt<loopCount)
		{
			DCCAppFormLog.mLogger.info("123 cabinet name..."+cabinetName);
			DCCAppFormLog.mLogger.info("123 session id is..."+sessionId);
			String strQuery="SELECT CC_WI_NAME,PORTAL_REF_NUMBER FROM NG_DOB_EXTTABLE with (nolock) WHERE APP_FORM_STATUS='R'";
			workItemListInputXML=CommonMethods.apSelectWithColumnNames(strQuery, cabinetName, sessionId);
			
			DCCAppFormLog.mLogger.info("workItemListInputXML aa:" + workItemListInputXML);
			try
			{
				workItemListOutputXML=WFNGExecute(workItemListInputXML,jtsIP,Integer.parseInt(jtsPort),1);
			}
			catch(Exception e)
			{
				DCCAppFormLog.mLogger.error("Exception in Execute : " + e);
				sessionCheckInt++;
				waiteloopExecute(waitLoop);
				sessionId  = CommonConnection.getSessionID(DCCAppFormLog.mLogger, false);
				continue;
			}

			DCCAppFormLog.mLogger.info("workItemListOutputXML : " + workItemListOutputXML);
			if (CommonMethods.getTagValues(workItemListOutputXML,"MainCode").equalsIgnoreCase("11"))
			{
				sessionId  = CommonConnection.getSessionID(DCCAppFormLog.mLogger, false);
			}
			else
			{
				sessionCheckInt++;
				break;
			}
		}

		int i = 0;
			if (CommonMethods.getMainCode(workItemListOutputXML) == 0)
			{
				int TotalRetrieved=Integer.parseInt(CommonMethods.getTagValues(workItemListOutputXML, "TotalRetrieved"));
				XMLParser xmlobj=new XMLParser(workItemListOutputXML);
				String workitem="";
				String portal_no="";
				if (TotalRetrieved > 0) 
				{
					for (int p = 0; p < TotalRetrieved; p++)
					{
						String subXML= xmlobj.getNextValueOf("Record");
						XMLParser objXmlParser = new XMLParser(subXML);
						workitem=objXmlParser.getValueOf("CC_WI_NAME");
						portal_no = objXmlParser.getValueOf("PORTAL_REF_NUMBER");
						workItemList.add(workitem+"#"+portal_no);
					}
				}
			}
			else
			{
				i++;
				lastProcessInstanceId = "";
				lastWorkItemId = "";
			}
		return workItemList;
	} 
	public static String WFNGExecute(String ipXML, String serverIP,
				int serverPort, int flag) throws IOException, Exception 
	{
		String jtsPort=""+serverPort;
		if (jtsPort.startsWith("33"))
			return WFCallBroker.execute(ipXML, serverIP, serverPort, flag);
		else
			return ngEjbClientFalconDocument.makeCall(serverIP, serverPort + "", "WebSphere",
						ipXML);
	}
}
