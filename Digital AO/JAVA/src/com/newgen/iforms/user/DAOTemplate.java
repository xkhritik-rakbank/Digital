package com.newgen.iforms.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;


import org.json.simple.JSONArray;
import org.xml.sax.SAXException;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.newgen.iforms.custom.IFormReference;

public class DAOTemplate extends DigitalAO_Common{
	
	private static final String Account_creation_date = null;
	private static String TemplatepathfromPrp;
	static Map<String, String> DAOConfigProperties = new HashMap<String, String>();
	
	public String clickEvent(IFormReference iform, String controlName, String data)
	throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
		DigitalAO.mLogger.debug("DigitalAO_Template");
		DigitalAO.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + getActivityName(iform)
				+ ", controlName " + controlName + ", data " + data); 
		String WINAME = getWorkitemName(iform);
		// code by deepanshu prashar for template generating
		
		if (controlName.equalsIgnoreCase("template_generate")) {
			
			DigitalAO.mLogger.debug("controlName" + controlName);
			String pdfName = "DAO_Template_kyc";
			return generate_kyc_temp(iform, WINAME, pdfName);
			
		}
		if (controlName.equalsIgnoreCase("Generate_firco_temp")) {
			
			DigitalAO.mLogger.debug("controlName" + controlName);
			String pdfName = "DAO_Firco_Template";
			return generate_firco_temp(iform, WINAME, pdfName);
		}
		//added by gaurav for Dedupe Pdf
		if (controlName.equalsIgnoreCase("template_generate_dedupe")) {
			
			DigitalAO.mLogger.debug("controlName" + controlName);
			String pdfName = "Dedupe_Pdf";
			return generate_dedupe_temp(iform, WINAME, pdfName);
		}
		//
		if (controlName.equalsIgnoreCase("Generate_RiskScore")) {
			
			String PdfName="Risk_Score_Details";
			String Status = null;
			try {
				Status = createPDF(iform,"Risk_Score",getWorkitemName(iform),PdfName);
			} catch (DocumentException e) {
				DigitalAO.mLogger.debug(" Catch Res"+e.getMessage());
			}
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Status : "+Status);
			String Res="";
			if(!Status.contains("Error"))
			{
				Res=AttachDocumentWithWI(iform,getWorkitemName(iform),PdfName);
				DigitalAO.mLogger.debug(" No Error in RISK PDF Gen :  Res"+Res);
				DigitalAO.mLogger.debug("No Error in RISK PDF Gen :  Res"+Res);
				return Res;
			}
			else
			{
				DigitalAO.mLogger.debug("Error in RISK PDF Gen :  Res"+Status);
				return Res=Status;
			}
		}
		return "";
	}
	
	public int readConfig() {
		Properties properties = null;
		try {
			DigitalAO.mLogger.debug("DigitalAO_Template.readConfig()");
			properties = new Properties();
			properties.load(new FileInputStream(new File(System.getProperty("user.dir") + File.separator + "ConfigProps"
					+ File.separator + "digitalAOConfig.Properties")));
			DigitalAO.mLogger.debug("properties :" + properties);
			Enumeration<?> names = properties.propertyNames();
			DigitalAO.mLogger.debug("names :" + names);

			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				DAOConfigProperties.put(name, properties.getProperty(name));
			}
		} catch (Exception e) {
			System.out.println("Exception in Read INI: " + e.getMessage());
			DigitalAO.mLogger.error("Exception has occured while loading properties file " + e.getMessage());
			return -1;
		}
		return 0;
	}
	
	//added by gaurav for Dedupe Pdf
	public String generate_dedupe_temp(IFormReference iform, String WINAME,String pdfName)
	{
		try
		{	
			DigitalAO.mLogger.debug("readConfig() generate_dedupe_temp");
			int configReadStatus = readConfig();
			
			DigitalAO.mLogger.debug("configReadStatus " + configReadStatus);
			if (configReadStatus != 0) {
				DigitalAO.mLogger.error("Could not Read Config Properties [properties]");
				return "";
			}
			
			TemplatepathfromPrp=DAOConfigProperties.get("TemplateFilePath");
			DigitalAO.mLogger.debug("TemplatepathfromPrp" + TemplatepathfromPrp);
			
			String response="";
			String TemplatePath = TemplatepathfromPrp+ WINAME + pdfName + ".pdf";
			DigitalAO.mLogger.debug("TemplatePath after talong from prop" + TemplatePath);
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, new FileOutputStream(TemplatePath));
			document.open();
			
			Font bold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			
			Date d = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String CurrentDateTime = dateFormat.format(d);
			
			String WorkItemName = String.valueOf(iform.getValue("WI_name"));
			String FirstName = (String)iform.getValue("Given_Name");
			String MiddleName = (String)iform.getValue("Middle_Name");
			String LastName = (String)iform.getValue("Surname");
			String CustomerPP="";
			if("".equalsIgnoreCase(MiddleName) || MiddleName == null){
				CustomerPP = FirstName + " " + LastName;
			}
			else{				 
				 CustomerPP = FirstName + " " +MiddleName+" "+ LastName;
			}
			
			String doB= (String)iform.getValue("DOB");
			String EmailId = (String)iform.getValue("email_id_1");
			String PhoneNumber = (String)iform.getValue("mobile_no_1");
			String PassportNo= (String)iform.getValue("Passport_No");
			String CIF = (String) iform.getValue("CIF");
			String Nationality1 = (String) iform.getValue("Nationality");
			String country_of_residence = (String) iform.getValue("country_of_residence");
			String Passport_issuing_country = (String) iform.getValue("Passport_issuing_country");
			
			
			
			
			String Gender = (String) iform.getValue("Gender");
			String Gender_Desc="";
			
			if(Gender.equals("M")){
				Gender_Desc="Male";
			}else if(Gender.equals("F"))
			{
				Gender_Desc="Female";
			}else{
				Gender_Desc="Other";
			}
			
			String Nationality_descptn  = "";
			String query_Nationality = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE WITH(NOLOCK) where CM_CODE='"
					+ Nationality1 + "'";
			DigitalAO.mLogger.debug("Desc_Country_Residenece_query: " + query_Nationality);
			List<List<String>> output_Nationality_query = iform.getDataFromDB(query_Nationality);
			DigitalAO.mLogger.debug("output_Desc_Country_Residenece_query: " + output_Nationality_query);

			if (!output_Nationality_query.isEmpty()) {
				DigitalAO.mLogger.debug("Inside output_Nationality_query: ");
				Nationality_descptn = output_Nationality_query.get(0).get(0);
				DigitalAO.mLogger.debug("Nationality_descptn: " + Nationality_descptn);
			} else {
				DigitalAO.mLogger.debug("Nationality_descptn is empty!!");
			}
			DigitalAO.mLogger.debug("Nationality_descptn : " + Nationality_descptn);
			
			String country_of_residence_desc = "";
			String query_country_of_residence = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE WITH(NOLOCK) where CM_CODE='"
					+ country_of_residence + "'";
			DigitalAO.mLogger.debug("Desc_Country_Residenece_query: " + query_country_of_residence);
			List<List<String>> output_country_of_residence = iform.getDataFromDB(query_country_of_residence);
			DigitalAO.mLogger.debug("output_Desc_Country_Residenece_query: " + output_country_of_residence);

			if (!output_country_of_residence.isEmpty()) {
				DigitalAO.mLogger.debug("Inside output_country_of_residence: ");
				country_of_residence_desc = output_country_of_residence.get(0).get(0);
				DigitalAO.mLogger.debug("output_country_of_residence: " + country_of_residence_desc);
			} else {
				DigitalAO.mLogger.debug("output_country_of_residence is empty!!");
			}
			DigitalAO.mLogger.debug("output_country_of_residence" + country_of_residence_desc);
			
			String Passport_issuing_country_Desc = "";
			String query_Passport_issuing_country = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE WITH(NOLOCK) where CM_CODE='"
					+ Passport_issuing_country + "'";
			DigitalAO.mLogger.debug("output_Passport_issuing_country: " + query_Passport_issuing_country);
			List<List<String>> output_Passport_issuing_country = iform.getDataFromDB(query_Passport_issuing_country);
			DigitalAO.mLogger.debug("output_Desc_Country_Residenece_query: " + output_Passport_issuing_country);

			if (!output_Passport_issuing_country.isEmpty()) {
				DigitalAO.mLogger.debug("Inside output_Passport_issuing_country: ");
				Passport_issuing_country_Desc = output_Passport_issuing_country.get(0).get(0);
				DigitalAO.mLogger.debug("output_Passport_issuing_country: " + country_of_residence_desc);
			} else {
				DigitalAO.mLogger.debug("output_Passport_issuing_country is empty!!");
			}
			DigitalAO.mLogger.debug("output_Passport_issuing_country" + Passport_issuing_country_Desc);

			
			int DEDUPE_Details_Grid = iform.getDataFromGrid("DEDUPE_Details_Grid").size();
			DigitalAO.mLogger.debug("DEDUPE_Details_Grid" + DEDUPE_Details_Grid);
			
			ArrayList<String> CIFID =new ArrayList<String>();
			ArrayList<String> FullName =new ArrayList<String>();
			ArrayList<String> DOB =new ArrayList<String>();
			ArrayList<String> Emirates_ID =new ArrayList<String>();
			ArrayList<String> Passport_No =new ArrayList<String>();
			ArrayList<String> Nationality=new ArrayList<String>();
			ArrayList<String> Phone =new ArrayList<String>();
			
			
			int rowCountForApplicantDEDUPE=0;
			
			for (int i = 0; i < DEDUPE_Details_Grid; i++)
			{
				DigitalAO.mLogger.debug("value of  i "+ i);
				
				CIFID.add(iform.getTableCellValue("DEDUPE_Details_Grid", i, 0));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n CIFID : "+CIFID);
				DigitalAO.mLogger.debug("row count complex table DEDUPE_Details : "+ rowCountForApplicantDEDUPE);
				
				FullName.add(iform.getTableCellValue("DEDUPE_Details_Grid", i, 1));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n FullName : "+FullName);
				DigitalAO.mLogger.debug("row count complex table DEDUPE_Details : "+ rowCountForApplicantDEDUPE);
				
				DOB.add(iform.getTableCellValue("DEDUPE_Details_Grid", i, 2));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n DOB : "+DOB);
				DigitalAO.mLogger.debug("row count complex table DEDUPE_Details : "+ rowCountForApplicantDEDUPE);
				
				Emirates_ID.add(iform.getTableCellValue("DEDUPE_Details_Grid", i, 3));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Emirates_ID : "+Emirates_ID);
				DigitalAO.mLogger.debug("row count complex table DEDUPE_Details : "+ rowCountForApplicantDEDUPE);

				Passport_No.add(iform.getTableCellValue("DEDUPE_Details_Grid", i, 4));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Passport_No : "+Passport_No);
				DigitalAO.mLogger.debug("row count complex table DEDUPE_Details : "+ rowCountForApplicantDEDUPE);
				
				Nationality.add(iform.getTableCellValue("DEDUPE_Details_Grid", i, 5));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Nationality : "+Nationality);
				DigitalAO.mLogger.debug("row count complex table DEDUPE_Details : "+ rowCountForApplicantDEDUPE);
				
				Phone.add(iform.getTableCellValue("DEDUPE_Details_Grid", i, 6));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Phone : "+Phone);
				DigitalAO.mLogger.debug("row count complex table DEDUPE_Details : "+ rowCountForApplicantDEDUPE);
				
				rowCountForApplicantDEDUPE++;
				
			 }
			
			DEDUPE_Details_Grid = rowCountForApplicantDEDUPE;
			DigitalAO.mLogger.debug("DEDUPE_Details_Grid value  :" +  DEDUPE_Details_Grid);
			 
			
			
			// for logo 
			 document.add(Image.getInstance(System.getProperty("user.dir") + File.separatorChar + "ConfigProps" + File.separatorChar + "Logo.png"));
			   
			 // heading 
			 Paragraph rakbankHeading = new Paragraph("\t"+"\t"+"\t"+"                                                                                     CUSTOMER DETAILS ");
			 document.add(rakbankHeading);
			 
			 //space
			 document.add(new Paragraph("\n"));
			 
			 // Wi name and Date 
			 
			 Paragraph wiName_date = new Paragraph(   "                       WI Name : " +" "+ WorkItemName+ "\n" + "                       Current Date : " + CurrentDateTime);
			 document.add(wiName_date);
			 
			 document.add(new Paragraph("\n"));
			 
			 document.add(new Paragraph("\n"));
			 PdfPTable personalInfoTable = new PdfPTable(2);
			 PdfPCell personalInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
			 personalInfoTable.addCell(personalInfoCell);
			 
			 personalInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(235, 235, 224));
			 personalInfoTable.addCell(personalInfoCell);
			 personalInfoTable.setHeaderRows(1);
			 
			 personalInfoTable.addCell("Customer Name As Per Passport");
			 personalInfoTable.addCell(CustomerPP);
			 personalInfoTable.addCell("First Name");
			 personalInfoTable.addCell(FirstName);
			 personalInfoTable.addCell("Middle Name");
			 personalInfoTable.addCell(MiddleName);
			 personalInfoTable.addCell("Last Name");
			 personalInfoTable.addCell(LastName);
			 personalInfoTable.addCell("Gender");
			 personalInfoTable.addCell(Gender_Desc);
			 personalInfoTable.addCell("Email ID");
			 personalInfoTable.addCell(EmailId);
			 personalInfoTable.addCell("Phone Number");
			 personalInfoTable.addCell(PhoneNumber);
			 personalInfoTable.addCell("Nationality");
			 personalInfoTable.addCell(Nationality_descptn);
			 personalInfoTable.addCell("Country of Residence");
			 personalInfoTable.addCell(country_of_residence_desc);
			 personalInfoTable.addCell("Date Of Birth");
			 personalInfoTable.addCell(doB);
			 personalInfoTable.addCell("Passport Number");
			 personalInfoTable.addCell(PassportNo);
			 personalInfoTable.addCell("CIF");
			 personalInfoTable.addCell(CIF);
			personalInfoTable.addCell("Passport Issuing Country");
			personalInfoTable.addCell(Passport_issuing_country_Desc); 
			 
			
			 
			document.add(personalInfoTable);
			
			document.add(new Paragraph("\n"));
			 
			Font fontRed = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD,new BaseColor(230, 0, 0));
			 
			Paragraph DEDUPE  = new Paragraph("                      Dedupe Details ", fontRed);
			document.add(DEDUPE);
			 
			document.add(new Paragraph("\n"));
				 
			int DEDUPE_Details_Grid_size = iform.getDataFromGrid("DEDUPE_Details_Grid").size();
			DigitalAO.mLogger.debug("sizeDEDUPE_Details_GridInfo" + DEDUPE_Details_Grid_size);
			
			PdfPTable DEDUPE_pdf = new PdfPTable(7);
			DigitalAO.mLogger.debug("DEDUPE_pdf :"+ DEDUPE_pdf);
			
            int[] columnWidths = {8,8,8,8,8,8,8};
            DEDUPE_pdf.setWidths(columnWidths);
            DEDUPE_pdf.setWidthPercentage(100); 
            
            DigitalAO.mLogger.debug("After width set");
			
            
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Before PdfPTable 1:");
            PdfPCell c1 = new PdfPCell(new Phrase("CIF ID"));
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable DEDUPE 1:");
				
			
            PdfPCell c2 = new PdfPCell(new Phrase("Full Name"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 2:");
            
			PdfPCell c3 = new PdfPCell(new Phrase("DOB"));
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 3:");
            
            PdfPCell c4 = new PdfPCell(new Phrase("Emirates ID"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 4:");
            
            PdfPCell c5 = new PdfPCell(new Phrase("Passport No"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable DEDUPE 5:");
            
            PdfPCell c6 = new PdfPCell(new Phrase("Nationality"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 6:");
            
            PdfPCell c7 = new PdfPCell(new Phrase("Phone"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 7:");
            
           
            
            try
			{
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable DEDUPE append");  
				DEDUPE_pdf.addCell(c1);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 1:");  
				DEDUPE_pdf.addCell(c2);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 2:");  
				DEDUPE_pdf.addCell(c3);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 3 :");  
				DEDUPE_pdf.addCell(c4);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 4:"); 
				DEDUPE_pdf.addCell(c5);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 5:"); 
				DEDUPE_pdf.addCell(c6);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 6:"); 
				DEDUPE_pdf.addCell(c7);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 7:"); 
			}
			catch(Exception e)
			{
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", In catch After DEDUPE_pdf DEDUPE : "+e.getMessage());
			}
	 		
			for (int j = 0; j < DEDUPE_Details_Grid_size; j++) {
				
				try{
					c1 = new PdfPCell(new Phrase(CIFID.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", CIFID "+CIFID.get(j));
					c1.setBackgroundColor(new BaseColor(255,251,240));
                    c1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					DEDUPE_pdf.addCell(c1);
					
					c2 = new PdfPCell(new Phrase(FullName.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", FullName "+FullName.get(j));
					c2.setBackgroundColor(new BaseColor(255,251,240));
                    c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					DEDUPE_pdf.addCell(c2);
					
					c3 = new PdfPCell(new Phrase(DOB.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", DOB "+DOB.get(j));
					c3.setBackgroundColor(new BaseColor(255,251,240));
                    c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					DEDUPE_pdf.addCell(c3);
					
					c4 = new PdfPCell(new Phrase(Emirates_ID.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Emirates_ID "+Emirates_ID.get(j));
					c4.setBackgroundColor(new BaseColor(255,251,240));
                    c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					DEDUPE_pdf.addCell(c4);
					
					c5 = new PdfPCell(new Phrase(Passport_No.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Passport_No "+Passport_No.get(j));
					c5.setBackgroundColor(new BaseColor(255,251,240));
					c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					DEDUPE_pdf.addCell(c5);
					
					c6 = new PdfPCell(new Phrase(Nationality.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Nationality "+Nationality.get(j));
					c6.setBackgroundColor(new BaseColor(255,251,240));
					c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					DEDUPE_pdf.addCell(c6);
					
					c7 = new PdfPCell(new Phrase(Phone.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Phone "+Phone.get(j));
					c7.setBackgroundColor(new BaseColor(255,251,240));
					c7.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					DEDUPE_pdf.addCell(c7);
					
				}catch(Exception e){
					DigitalAO.mLogger.debug("Exception DEDUPE_Details_Grid last :"+ e.getMessage());
				}
			}
			document.add(DEDUPE_pdf);
			document.add(new Paragraph("\n"));
			document.close();
			
			DigitalAO.mLogger.debug("generate_dedupe_temp " + document);
			response = AttachDocumentWithWI(iform,WINAME,pdfName );
			return response;
		}
		catch(Exception e){
			System.out.print("Exception generate_dedupe_temp : "+e.getMessage());
			DigitalAO.mLogger.debug("Exception generate_dedupe_temp ;" + e.getMessage());
			return "error";
		}
	}
	
	public String generate_firco_temp(IFormReference iform, String WINAME,String pdfName)
	{
		try
		{	
			DigitalAO.mLogger.debug("readConfig() generate_firco_temp");
			int configReadStatus = readConfig();
			
			DigitalAO.mLogger.debug("configReadStatus " + configReadStatus);
			if (configReadStatus != 0) {
				DigitalAO.mLogger.error("Could not Read Config Properties [properties]");
				return "";
			}
			
			TemplatepathfromPrp=DAOConfigProperties.get("TemplateFilePath");
			DigitalAO.mLogger.debug("TemplatepathfromPrp" + TemplatepathfromPrp);
			
			String response="";
			String TemplatePath = TemplatepathfromPrp+ WINAME + pdfName + ".pdf";
			DigitalAO.mLogger.debug("TemplatePath after talong from prop" + TemplatePath);
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, new FileOutputStream(TemplatePath));
			document.open();
			
			Font bold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			
			Date d = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String CurrentDateTime = dateFormat.format(d);
	
			String WorkItemName = String.valueOf(iform.getValue("WI_name"));
			String FirstName = (String)iform.getValue("Given_Name");
			String MiddleName = (String)iform.getValue("Middle_Name");
			String LastName = (String)iform.getValue("Surname");
			String CustomerPP="";
			if("".equalsIgnoreCase(MiddleName) || MiddleName == null){
				CustomerPP = FirstName + " " + LastName;
			}
			else{				 
				 CustomerPP = FirstName + " " +MiddleName+" "+ LastName;
			}
			
			String doB= (String)iform.getValue("DOB");
			String EmailId = (String)iform.getValue("email_id_1");
			String PhoneNumber = (String)iform.getValue("mobile_no_1");
			String Passport_No= (String)iform.getValue("Passport_No");
			String CIF = (String) iform.getValue("CIF");
			String Nationality = (String) iform.getValue("Nationality");
			String country_of_residence = (String) iform.getValue("country_of_residence");
			String Passport_issuing_country = (String) iform.getValue("Passport_issuing_country");
			
			
			
			
			String Gender = (String) iform.getValue("Gender");
			String Gender_Desc="";
			
			if(Gender.equals("M")){
				Gender_Desc="Male";
			}else if(Gender.equals("F"))
			{
				Gender_Desc="Female";
			}else{
				Gender_Desc="Other";
			}
			
			String Nationality_descptn  = "";
			String query_Nationality = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE WITH(NOLOCK) where CM_CODE='"
					+ Nationality + "'";
			DigitalAO.mLogger.debug("Desc_Country_Residenece_query: " + query_Nationality);
			List<List<String>> output_Nationality_query = iform.getDataFromDB(query_Nationality);
			DigitalAO.mLogger.debug("output_Desc_Country_Residenece_query: " + output_Nationality_query);

			if (!output_Nationality_query.isEmpty()) {
				DigitalAO.mLogger.debug("Inside output_Nationality_query: ");
				Nationality_descptn = output_Nationality_query.get(0).get(0);
				DigitalAO.mLogger.debug("Nationality_descptn: " + Nationality_descptn);
			} else {
				DigitalAO.mLogger.debug("Nationality_descptn is empty!!");
			}
			DigitalAO.mLogger.debug("Nationality_descptn : " + Nationality_descptn);
			
			String country_of_residence_desc = "";
			String query_country_of_residence = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE WITH(NOLOCK) where CM_CODE='"
					+ country_of_residence + "'";
			DigitalAO.mLogger.debug("Desc_Country_Residenece_query: " + query_country_of_residence);
			List<List<String>> output_country_of_residence = iform.getDataFromDB(query_country_of_residence);
			DigitalAO.mLogger.debug("output_Desc_Country_Residenece_query: " + output_country_of_residence);

			if (!output_country_of_residence.isEmpty()) {
				DigitalAO.mLogger.debug("Inside output_country_of_residence: ");
				country_of_residence_desc = output_country_of_residence.get(0).get(0);
				DigitalAO.mLogger.debug("output_country_of_residence: " + country_of_residence_desc);
			} else {
				DigitalAO.mLogger.debug("output_country_of_residence is empty!!");
			}
			DigitalAO.mLogger.debug("output_country_of_residence" + country_of_residence_desc);
			
			String Passport_issuing_country_Desc = "";
			String query_Passport_issuing_country = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE WITH(NOLOCK) where CM_CODE='"
					+ Passport_issuing_country + "'";
			DigitalAO.mLogger.debug("output_Passport_issuing_country: " + query_Passport_issuing_country);
			List<List<String>> output_Passport_issuing_country = iform.getDataFromDB(query_Passport_issuing_country);
			DigitalAO.mLogger.debug("output_Desc_Country_Residenece_query: " + output_Passport_issuing_country);

			if (!output_Passport_issuing_country.isEmpty()) {
				DigitalAO.mLogger.debug("Inside output_Passport_issuing_country: ");
				Passport_issuing_country_Desc = output_Passport_issuing_country.get(0).get(0);
				DigitalAO.mLogger.debug("output_Passport_issuing_country: " + country_of_residence_desc);
			} else {
				DigitalAO.mLogger.debug("output_Passport_issuing_country is empty!!");
			}
			DigitalAO.mLogger.debug("output_Passport_issuing_country" + Passport_issuing_country_Desc);

			
			int UID_Details_Grid = iform.getDataFromGrid("UID_Details_Grid").size();
			int UID_Details_Grid_2 = iform.getDataFromGrid("UID_Details").size();
			DigitalAO.mLogger.debug("UID_Details_Grid" + UID_Details_Grid);
			DigitalAO.mLogger.debug("UID_Details" + UID_Details_Grid_2);
			
			ArrayList<String> OFAC_ID =new ArrayList<String>();
			ArrayList<String> Matching_Text =new ArrayList<String>();
			ArrayList<String> Name =new ArrayList<String>();
			ArrayList<String> Origin =new ArrayList<String>();
			ArrayList<String> Designation =new ArrayList<String>();
			ArrayList<String> Date_of_Birth=new ArrayList<String>();
			ArrayList<String> User_Data_1 =new ArrayList<String>();
			ArrayList<String> Nationality1 =new ArrayList<String>();
			ArrayList<String> Passport =new ArrayList<String>();
			ArrayList<String> Additional_Info =new ArrayList<String>();
			ArrayList<String> Remarks =new ArrayList<String>();
			
			int rowCountForApplicantUID=0;
			
			for (int i = 0; i < UID_Details_Grid; i++)
			{
				DigitalAO.mLogger.debug("value of  i "+ i);
				
				OFAC_ID.add(iform.getTableCellValue("UID_Details_Grid", i, 0));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n OFAC_ID : "+OFAC_ID);
				DigitalAO.mLogger.debug("row count complex table UID_Details : "+ rowCountForApplicantUID);
				
				Matching_Text.add(iform.getTableCellValue("UID_Details_Grid", i, 1));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Matching_Text : "+Matching_Text);
				DigitalAO.mLogger.debug("row count complex table UID_Details : "+ rowCountForApplicantUID);
				
				Name.add(iform.getTableCellValue("UID_Details_Grid", i, 2));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Name : "+Name);
				DigitalAO.mLogger.debug("row count complex table UID_Details : "+ rowCountForApplicantUID);
				
				Origin.add(iform.getTableCellValue("UID_Details_Grid", i, 3));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Origin : "+Origin);
				DigitalAO.mLogger.debug("row count complex table UID_Details : "+ rowCountForApplicantUID);

				Designation.add(iform.getTableCellValue("UID_Details_Grid", i, 4));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Designation : "+Designation);
				DigitalAO.mLogger.debug("row count complex table UID_Details : "+ rowCountForApplicantUID);
				
				Date_of_Birth.add(iform.getTableCellValue("UID_Details_Grid", i, 5));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Date_of_Birth : "+Date_of_Birth);
				DigitalAO.mLogger.debug("row count complex table UID_Details : "+ rowCountForApplicantUID);
				
				User_Data_1.add(iform.getTableCellValue("UID_Details_Grid", i, 6));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n User_Data_1 : "+User_Data_1);
				DigitalAO.mLogger.debug("row count complex table UID_Details : "+ rowCountForApplicantUID);
				
				Nationality1.add(iform.getTableCellValue("UID_Details_Grid", i, 7));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Nationality1 : "+Nationality1);
				DigitalAO.mLogger.debug("row count complex table UID_Details : "+ rowCountForApplicantUID);
			
				Passport.add(iform.getTableCellValue("UID_Details_Grid", i, 8));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Passport : "+Passport);
				DigitalAO.mLogger.debug("row count complex table UID_Details : "+ rowCountForApplicantUID);
				
				Additional_Info.add(iform.getTableCellValue("UID_Details_Grid", i, 9));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Additional_Info : "+Additional_Info);
				DigitalAO.mLogger.debug("row count complex table UID_Details : "+ rowCountForApplicantUID);
				
				Remarks.add(iform.getTableCellValue("UID_Details_Grid", i, 10));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \n Remarks : "+Remarks);
				DigitalAO.mLogger.debug("row count complex table UID_Details : "+ rowCountForApplicantUID);
				
				rowCountForApplicantUID++;
				
			 }
			
			UID_Details_Grid = rowCountForApplicantUID;
			DigitalAO.mLogger.debug("UID_Details_Grid value  :" +  UID_Details_Grid);
			 
			
			
			// for logo 
			 document.add(Image.getInstance(System.getProperty("user.dir") + File.separatorChar + "ConfigProps" + File.separatorChar + "Logo.png"));
			   
			 // heading 
			 Paragraph rakbankHeading = new Paragraph("\t"+"\t"+"\t"+"                                                                                     CUSTOMER DETAILS ");
			 document.add(rakbankHeading);
			 
			 //space
			 document.add(new Paragraph("\n"));
			 
			 // Wi name and Date 
			 
			 Paragraph wiName_date = new Paragraph(   "                       WI Name : " +" "+ WorkItemName+ "\n" + "                       Current Date : " + CurrentDateTime);
			 document.add(wiName_date);
			 
			 document.add(new Paragraph("\n"));
			 
			 document.add(new Paragraph("\n"));
			 PdfPTable personalInfoTable = new PdfPTable(2);
			 PdfPCell personalInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
			 personalInfoTable.addCell(personalInfoCell);
			 
			 personalInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(235, 235, 224));
			 personalInfoTable.addCell(personalInfoCell);
			 personalInfoTable.setHeaderRows(1);
			 
			 personalInfoTable.addCell("Customer Name As Per Passport");
			 personalInfoTable.addCell(CustomerPP);
			 personalInfoTable.addCell("First Name");
			 personalInfoTable.addCell(FirstName);
			 personalInfoTable.addCell("Middle Name");
			 personalInfoTable.addCell(MiddleName);
			 personalInfoTable.addCell("Last Name");
			 personalInfoTable.addCell(LastName);
			 personalInfoTable.addCell("Gender");
			 personalInfoTable.addCell(Gender_Desc);
			 personalInfoTable.addCell("Email ID");
			 personalInfoTable.addCell(EmailId);
			 personalInfoTable.addCell("Phone Number");
			 personalInfoTable.addCell(PhoneNumber);
			 personalInfoTable.addCell("Nationality");
			 personalInfoTable.addCell(Nationality_descptn);
			 personalInfoTable.addCell("Country of Residence");
			 personalInfoTable.addCell(country_of_residence_desc);
			 personalInfoTable.addCell("Date Of Birth");
			 personalInfoTable.addCell(doB);
			 personalInfoTable.addCell("Passport Number");
			 personalInfoTable.addCell(Passport_No);
			 personalInfoTable.addCell("CIF");
			 personalInfoTable.addCell(CIF);
			personalInfoTable.addCell("Passport Issuing Country");
			personalInfoTable.addCell(Passport_issuing_country_Desc); 
			 
			
			 
			document.add(personalInfoTable);
			
			document.add(new Paragraph("\n"));
			 
			Font fontRed = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD,new BaseColor(230, 0, 0));
			 
			Paragraph UID  = new Paragraph("                      Alert Details ", fontRed);
			document.add(UID);
			 
			document.add(new Paragraph("\n"));
				 
			int UID_Details_Grid_size = iform.getDataFromGrid("UID_Details_Grid").size();
			DigitalAO.mLogger.debug("sizeAddressResidenceInfo" + UID_Details_Grid_size);
			
			PdfPTable UID_pdf = new PdfPTable(10);
			DigitalAO.mLogger.debug("UID_pdf :"+ UID_pdf);
			
            int[] columnWidths = {8,8,8,8,8,8,8,8,8,25};
            UID_pdf.setWidths(columnWidths);
            UID_pdf.setWidthPercentage(100); 
            
            DigitalAO.mLogger.debug("After width set");
            
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Before PdfPTable 1:");
            PdfPCell c1 = new PdfPCell(new Phrase("OFAC ID"));
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable UID 1:");
				
			
            PdfPCell c2 = new PdfPCell(new Phrase("Matching Text"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 2:");
            
			PdfPCell c3 = new PdfPCell(new Phrase("Name"));
			DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 3:");
            
            PdfPCell c4 = new PdfPCell(new Phrase("Origin"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 4:");
            
            PdfPCell c5 = new PdfPCell(new Phrase("Designation"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable UID 5:");
            
            PdfPCell c6 = new PdfPCell(new Phrase("Date of Birth"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 6:");
            
            PdfPCell c7 = new PdfPCell(new Phrase("User Data 1"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 7:");
            
            PdfPCell c8 = new PdfPCell(new Phrase("Nationality"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 8:");
            
            PdfPCell c9 = new PdfPCell(new Phrase("Passport"));
             DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 9:");
            
            PdfPCell c10 = new PdfPCell(new Phrase("Additional Info"));
            DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable UID 10:");
            
            try
			{
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable UID append");  
				UID_pdf.addCell(c1);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 1:");  
				UID_pdf.addCell(c2);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 2:");  
				UID_pdf.addCell(c3);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 3 :");  
				UID_pdf.addCell(c4);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 4:"); 
				UID_pdf.addCell(c5);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 5:"); 
				UID_pdf.addCell(c6);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 6:"); 
				UID_pdf.addCell(c7);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 7:"); 
				UID_pdf.addCell(c8);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 8:"); 
				UID_pdf.addCell(c9);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 8:"); 
				UID_pdf.addCell(c10);
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 8:"); 
			}
			catch(Exception e)
			{
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", In catch After UID_pdf UID : "+e.getMessage());
			}
	 		
			for (int j = 0; j < UID_Details_Grid_size; j++) {
				
				try{
					/*
					 * 
					ArrayList<String> Date_of_Birth=new ArrayList<String>();
					ArrayList<String> User_Data_1 =new ArrayList<String>();
					ArrayList<String> Nationality1 =new ArrayList<String>();
					ArrayList<String> Passport =new ArrayList<String>();
					ArrayList<String> Additional_Info =new ArrayList<String>();
					ArrayList<String> Remarks =new ArrayList<String>();
					*/
					c1 = new PdfPCell(new Phrase(OFAC_ID.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", OFAC_ID "+OFAC_ID.get(j));
					c1.setBackgroundColor(new BaseColor(255,251,240));
                    c1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					UID_pdf.addCell(c1);
					
					c2 = new PdfPCell(new Phrase(Matching_Text.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Matching_Text "+Matching_Text.get(j));
					c2.setBackgroundColor(new BaseColor(255,251,240));
                    c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					UID_pdf.addCell(c2);
					
					c3 = new PdfPCell(new Phrase(Name.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Name "+Name.get(j));
					c3.setBackgroundColor(new BaseColor(255,251,240));
                    c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					UID_pdf.addCell(c3);
					
					c4 = new PdfPCell(new Phrase(Origin.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Origin "+Origin.get(j));
					c4.setBackgroundColor(new BaseColor(255,251,240));
                    c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					UID_pdf.addCell(c4);
					
					c5 = new PdfPCell(new Phrase(Designation.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Designation "+Designation.get(j));
					c5.setBackgroundColor(new BaseColor(255,251,240));
					c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					UID_pdf.addCell(c5);
					
					c6 = new PdfPCell(new Phrase(Date_of_Birth.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Date_of_Birth "+Date_of_Birth.get(j));
					c6.setBackgroundColor(new BaseColor(255,251,240));
					c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					UID_pdf.addCell(c6);
					
					c7 = new PdfPCell(new Phrase(User_Data_1.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", User_Data_1 "+User_Data_1.get(j));
					c7.setBackgroundColor(new BaseColor(255,251,240));
					c7.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					UID_pdf.addCell(c7);
					
					c8 = new PdfPCell(new Phrase(Nationality1.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Nationality1 "+Nationality1.get(j));
					c8.setBackgroundColor(new BaseColor(255,251,240));
					c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					UID_pdf.addCell(c8);
					
					c9 = new PdfPCell(new Phrase(Passport.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Passport "+Passport.get(j));
					c9.setBackgroundColor(new BaseColor(255,251,240));
					c9.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					UID_pdf.addCell(c9);
					
					c10 = new PdfPCell(new Phrase(Additional_Info.get(j)));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Additional_Info "+Additional_Info.get(j));
					c10.setBackgroundColor(new BaseColor(255,251,240));
					c10.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
					UID_pdf.addCell(c10);
					
				}catch(Exception e){
					DigitalAO.mLogger.debug("Exception UID_Details_Grid last :"+ e.getMessage());
				}
			}
			document.add(UID_pdf);
			document.add(new Paragraph("\n"));
			document.close();
			
			DigitalAO.mLogger.debug("generate_firco_temp " + document);
			response = AttachDocumentWithWI(iform,WINAME,pdfName );
			return response;
		}
		catch(Exception e){
			System.out.print("Exception generate_firco_temp : "+e.getMessage());
			DigitalAO.mLogger.debug("Exception generate_firco_temp ;" + e.getMessage());
			return "error";
		}
	}
	
	

	public String generate_kyc_temp(IFormReference iform, String WINAME,
			String pdfName) {
		try{
			
			DigitalAO.mLogger.debug("readConfig() generate_kyc_temp");
			int configReadStatus = readConfig();
			
			DigitalAO.mLogger.debug("configReadStatus " + configReadStatus);
			if (configReadStatus != 0) {
				DigitalAO.mLogger.error("Could not Read Config Properties [properties]");
				return "";
			}
			TemplatepathfromPrp=DAOConfigProperties.get("TemplateFilePath");
			DigitalAO.mLogger.debug("TemplatepathfromPrp" + TemplatepathfromPrp);
			
			String TemplatePath = TemplatepathfromPrp+ WINAME + pdfName + ".pdf";
			DigitalAO.mLogger.debug("TemplatePath readConfig kyc " + TemplatePath);
			Document document = new Document(PageSize.A4.rotate());
			PdfWriter.getInstance(document, new FileOutputStream(TemplatePath));
			document.open();
			Font bold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			
			//assign variables
			// personal Information
			 String WorkItemName = String.valueOf(iform.getValue("WI_name"));
			 
			 Date d = new Date();
			 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 String CurrentDateTime = dateFormat.format(d);
			 
			 String FirstName = (String)iform.getValue("Given_Name");
			 String LastName = (String)iform.getValue("Surname");
			 String MiddleName = (String)iform.getValue("Middle_Name");
			 String CustomerPP = "";
			 
			 if("".equalsIgnoreCase(MiddleName) || MiddleName == null){
					CustomerPP = FirstName + " " + LastName;
				}
				else{				 
					 CustomerPP = FirstName + " " +MiddleName+" "+ LastName;
				}
				
			
			 String EmailId = (String)iform.getValue("email_id_1");
			 String PhoneNumber = (String)iform.getValue("mobile_no_1");
			 String PlceOfBirth = (String)iform.getValue("Country_of_birth");
			 String Place_of_birth_descptn = "";
			 String descPlaceOfBirth = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_BIRTH where CM_CODE='"+PlceOfBirth+"'";
			 DigitalAO.mLogger.debug("Desc_descPlaceOfBirth_query: "+descPlaceOfBirth);
			 
			 List<List<String>> output_PlaceOfBirth_query =  iform.getDataFromDB(descPlaceOfBirth);
			 DigitalAO.mLogger.debug("output_descPlaceOfBirth_query: "+output_PlaceOfBirth_query);
			 
			 if (!(output_PlaceOfBirth_query).isEmpty()) 
				{
					DigitalAO.mLogger.debug("Inside output_PlaceOfBirth_query: ");
					Place_of_birth_descptn = output_PlaceOfBirth_query.get(0).get(0);
					DigitalAO.mLogger.debug("output_PlaceOfBirth_query_descptn: "+output_PlaceOfBirth_query);
				}
				else{
					DigitalAO.mLogger.debug("Nationality_descptn is empty!!"); 
				}
			 // for dual nationality
			 
			 String nationality =  (String)iform.getValue("Nationality"); // add new
			 
			 String finalOutputNAtionality = "";
			 String nationality_Query = "select CD_DESC from NG_MASTER_DAO_NATIONALITY with (NOLOCK) where CM_CODE = '"+nationality+"'";
			 DigitalAO.mLogger.debug("nationality_Query :"+nationality_Query);
			 
			 List<List<String>> output_nationality_query =  iform.getDataFromDB(nationality_Query);
			 DigitalAO.mLogger.debug("output_nationality_query :"+output_nationality_query);
			
			 if (!(output_nationality_query).isEmpty()) 
				{
					DigitalAO.mLogger.debug("Inside output_nationality_query: ");
					finalOutputNAtionality = output_nationality_query.get(0).get(0);
					DigitalAO.mLogger.debug("finalOutputNAtionality: "+finalOutputNAtionality);
				}
				else{
					DigitalAO.mLogger.debug("Nationality_descptn is empty!!"); 
				}
			 
			 //added by deepanshu
			 
			 
			 
			 
			 String dualNationality= (String)iform.getValue("Secondary_Nationality");
			 
			 String finalOutputSecondaryNationality = "";
			 String secondaryNationality_query = "select CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE with (NOLOCK) where CM_CODE = '"+dualNationality+"'";
			 DigitalAO.mLogger.debug("secondaryNationality_query :"+secondaryNationality_query);
			 
			 List<List<String>> output_secondary_nationality_query =  iform.getDataFromDB(secondaryNationality_query);
			 DigitalAO.mLogger.debug("output_secondary_nationality_query: "+output_secondary_nationality_query);
			 
			 if (!(output_secondary_nationality_query).isEmpty()) 
				{
					DigitalAO.mLogger.debug("Inside output_secondary_nationality_query: ");
					finalOutputSecondaryNationality = output_secondary_nationality_query.get(0).get(0);
					DigitalAO.mLogger.debug("finalOutputSecondaryNationality: "+finalOutputSecondaryNationality);
				}
				else{
					finalOutputSecondaryNationality="NA";
					DigitalAO.mLogger.debug("Nationality_descptn is empty!!"); 
				}
			//raja
			 // add employement detail here 
			 String employer_name_as_per_visa = (String)iform.getValue("Employer_name_as_per_visa");
			 DigitalAO.mLogger.debug("employer_name_as_per_visa  : "+ employer_name_as_per_visa);
				
			 String doB= (String)iform.getValue("DOB");
			 String RiskScore = (String)iform.getValue("risk_score");
			// String FamilyMemberAssociation = (String)iform.getValue("Relation_Detail_w_PEP");
			 String PEP = (String)iform.getValue("PEP");
			 String PEP_Output = "";
			 
			 if(PEP.equalsIgnoreCase("Y")){
				 PEP_Output = "Yes";
			 }else{
				 PEP_Output = "No";
			 }
			 
			 // residence form filed ********************** grid**
			 
			// ResidenceAddresss = (String)iform.getValue("Secondary_Nationality");// do work on that
			 int residenceAddress = iform.getDataFromGrid("address_detail").size();
			 DigitalAO.mLogger.debug("residenceAddress : "+ residenceAddress);
			 
			 ArrayList<String> flatVillaNumber =new ArrayList<String>();
			 ArrayList<String> buildingVillaNumber =new ArrayList<String>();
			 ArrayList<String> streetLocation =new ArrayList<String>();
			 ArrayList<String> NearestLandmark =new ArrayList<String>();
			 ArrayList<String> poBox =new ArrayList<String>();
			
			 String emiratesCity =  "";
			
			 String country = "";
			 ArrayList<String> country_final_output_address_details = new ArrayList<String>();
			 
			 ArrayList<String> AddressType =new ArrayList<String>();
			 ArrayList<String> emirates_final_output_address_details = new ArrayList<String>();
			 String country_query = "";
			 String emirates_query = "";
			  
			
			 int rowCountForApplicantAdress = 0;
			 
			 // loop in grid
			 for (int i = 0; i < residenceAddress; i++){
				 
				 DigitalAO.mLogger.debug("value of  i "+ i);
				 
				 flatVillaNumber.add(iform.getTableCellValue("address_detail", i, 0));
				 DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nOFAC_ID : "+flatVillaNumber);
				 DigitalAO.mLogger.debug("row count complex table : "+ rowCountForApplicantAdress);
				 
				 buildingVillaNumber.add(iform.getTableCellValue("address_detail", i, 1));
				 DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nPositionHeld"+buildingVillaNumber);
					
				 streetLocation.add(iform.getTableCellValue("address_detail", i, 2));
				 DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nstreetLocation"+streetLocation);
					
				 NearestLandmark.add(iform.getTableCellValue("address_detail", i, 3));
				 DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nDateOfEmployement"+NearestLandmark);
					
				 poBox.add(iform.getTableCellValue("address_detail",i,4));
				 DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nwasNotEmployement"+poBox);
				
				 emiratesCity = (iform.getTableCellValue("address_detail",i,5));
				 DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nwasNotEmployement"+emiratesCity);
				
				 // grid value description 
				 
				 
				 country = (iform.getTableCellValue("address_detail",i,6));
				 DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nwasNotEmployement"+country);
				
				 AddressType.add(iform.getTableCellValue("address_detail",i,7));
				 DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nwasNotEmployement"+AddressType);
				 
				 rowCountForApplicantAdress++;
				 
				// for address type emirates case
				 emirates_query = "select CD_DESC from NG_MASTER_DAO_EMIRATES_CITY  WITH(NOLOCK) where CM_CODE = '"+ emiratesCity + "'";
				 DigitalAO.mLogger.debug("emirates_query : "+ emirates_query);
				 
				 List<List<String>> emirates_db_output = iform.getDataFromDB(emirates_query);
				 DigitalAO.mLogger.debug("compnay_grid : Cont_W_business_out : "+ emirates_db_output);
				 
				 if (!emirates_db_output.isEmpty()) {
						DigitalAO.mLogger.debug("Inside emirates_db_output: ");
						emirates_final_output_address_details.add(emirates_db_output.get(0).get(0));
						
						DigitalAO.mLogger.debug("emirates_final_output_address_details :"+emirates_final_output_address_details);
						
						
						
					} else {
						DigitalAO.mLogger.debug("emirates_db_output is empty!!");
					}
				
				 
				 // country address
				 country_query = "select CD_DESC from NG_MASTER_DAO_COUNTRY WITH(NOLOCK)where CM_CODE = '"+ country + "'";
				 
				 List<List<String>> country_db_output = iform.getDataFromDB(country_query);
				 DigitalAO.mLogger.debug("country_db_output: "+ country_db_output);
				 
				 if (!country_db_output.isEmpty()) {
						DigitalAO.mLogger.debug("Inside country_final_output_address_details: ");
						country_final_output_address_details.add(country_db_output.get(0).get(0));
						
						DigitalAO.mLogger.debug("country_final_output_address_details :"+country_final_output_address_details);
					
					} else {
						DigitalAO.mLogger.debug("country_final_output_address_details is empty!!");
					}
			 }
	 
			 // for country Address type
 
			 residenceAddress = rowCountForApplicantAdress;
			 DigitalAO.mLogger.debug("Last Address_Details info  : "+ residenceAddress);
			
//				**************************************end address grid
			 
			 //employement 
			 String employement_type = (String) iform.getValue("employement_type");
			 String EmployerName = (String)iform.getValue("Company_employer_name");
			 
			 DigitalAO.mLogger.debug("EmployerName  : "+ EmployerName);
			 String CurrentDesignation =(String)iform.getValue("current_visa_designation");
			 String ExpectedMonthlyIncome =(String)iform.getValue("gross_monthly_salary_income");
			 String employerCode  = (String)iform.getValue("employer_code");
			 
 
			 //purpose of account
			 String purposeOfAccount =(String)iform.getValue("Purpose_of_account");
			 // Transaction Parameters/ Info:
			 String MonthlyExpectedTurnoverCash =(String)iform.getValue("Monthly_expected_turnover_Cash");
			 String MonthlyExpectedTurnoverNonCash =(String)iform.getValue("Monthly_expected_turnover_non_cash");
			 // background info
			 
			 String investmentPortfolioIncludingVirtualAssest = (String)iform.getValue("investment_portfolio_including_virtual_asset");
			 if(investmentPortfolioIncludingVirtualAssest==null || investmentPortfolioIncludingVirtualAssest.equalsIgnoreCase("")){
				 investmentPortfolioIncludingVirtualAssest="NA";
			 }
			 String IncomeGenerate = (String)iform.getValue("income_generated");
			 if(investmentPortfolioIncludingVirtualAssest==null || investmentPortfolioIncludingVirtualAssest.equalsIgnoreCase("N")|| investmentPortfolioIncludingVirtualAssest.equalsIgnoreCase("No") || investmentPortfolioIncludingVirtualAssest.equalsIgnoreCase("NA")){
				 IncomeGenerate="NA";
			 }
			 String realEstateOwned = (String)iform.getValue("real_Est_owned");
			 if(realEstateOwned==null || realEstateOwned.equalsIgnoreCase("")){
				 realEstateOwned="NA";
			 }
			 String rented = (String)iform.getValue("rental_income");
			 if(realEstateOwned==null || realEstateOwned.equalsIgnoreCase("N") || realEstateOwned.equalsIgnoreCase("No") || realEstateOwned.equalsIgnoreCase("NA")){
				 rented="NA";
			 }
			 String otherSourceOfIncome =(String)iform.getValue("other_Source_of_income");
			 if(otherSourceOfIncome==null || otherSourceOfIncome.equalsIgnoreCase("")){
				 otherSourceOfIncome="NA";
			 }
			 String netMonthlyIncome =(String)iform.getValue("Net_Monthly_Income");
			 if(otherSourceOfIncome==null|| otherSourceOfIncome.equalsIgnoreCase("N") || otherSourceOfIncome.equalsIgnoreCase("No") || otherSourceOfIncome.equalsIgnoreCase("NA")){
				 netMonthlyIncome="NA";
			 }
			 String Description = (String)iform.getValue("Product_Service_dealing");
			 if(Description==null|| Description.equalsIgnoreCase("")){
				 Description="NA";
			 }
			 String Inheritance = (String)iform.getValue("Inheritance");// latest added by 12 july 
			 if(Inheritance==null || Inheritance.equalsIgnoreCase("")){
				 Inheritance="NA";
			 }
			 String Inheritance_income = (String)iform.getValue("Inheritance_income");
			 if(Inheritance.equalsIgnoreCase("N") || Inheritance.equalsIgnoreCase("No") || Inheritance.equalsIgnoreCase("NA")){
				 Inheritance_income="NA";
				 
			 }
			 
			 	//TODO********************** grid background information check with array**********************************background info
			 	int BackgroundComplexInfo = iform.getDataFromGrid("background_information").size();
			 	
			 	DigitalAO.mLogger.debug("background complex table : "+ BackgroundComplexInfo);
				
				int Radiocount=0;
				ArrayList<String> EmployerNamegrid=new ArrayList<String>();
				ArrayList<String> PositionHeld=new ArrayList<String>();	
				String country_bi = "";
				ArrayList<String> country_final_output_bi = new ArrayList<String>();
				ArrayList<String> DateOfEmployement=new ArrayList<String>();
				String country_query_bi ="";
				
				String high_risk = (String)iform.getValue("high_risk");
				int rowCountForApplicant = 0;
				
				for (int i = 0; i < BackgroundComplexInfo; i++){
					DigitalAO.mLogger.debug("value of  i "+ i);
					if(iform.getTableCellValue("background_information", i, 1)==null || iform.getTableCellValue("background_information", i, 1).equalsIgnoreCase("")){
						EmployerNamegrid.add("NA");
					}
					else{
						EmployerNamegrid.add(iform.getTableCellValue("background_information", i, 1));
					}
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nOFAC_ID : "+EmployerNamegrid);
					DigitalAO.mLogger.debug("row count complex table : "+ rowCountForApplicant);
					
					if(iform.getTableCellValue("background_information", i, 3)==null || iform.getTableCellValue("background_information", i, 3).equalsIgnoreCase("")){
						PositionHeld.add("NA");
					}
					else{
						PositionHeld.add(iform.getTableCellValue("background_information", i, 3));
					}
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nPositionHeld"+PositionHeld);
					
					if(iform.getTableCellValue("background_information", i, 4)==null || iform.getTableCellValue("background_information", i, 4).equalsIgnoreCase("")){
						country_bi="";
					}
					else{
						country_bi = (iform.getTableCellValue("background_information", i, 4));
					}
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncountry_bi"+country_bi);
				
					if(iform.getTableCellValue("background_information", i, 5)==null || iform.getTableCellValue("background_information", i, 5).equalsIgnoreCase("")){
						DateOfEmployement.add("NA");
					}
					else{
						DateOfEmployement.add(iform.getTableCellValue("background_information", i, 5));
					}
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nDateOfEmployement"+DateOfEmployement);
//						wasNotEmployement.add(iform.getTableCellValue("background_information",i,0));
//						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nwasNotEmployement"+wasNotEmployement);
					
			
					rowCountForApplicant++;
					country_query_bi =  "select CD_DESC from NG_MASTER_DAO_COUNTRY WITH(NOLOCK) where CM_CODE = '"+ country_bi + "'";

					 DigitalAO.mLogger.debug("country_query_bi :"+country_query_bi);
					 
					 List<List<String>> country_db_output_bi = iform.getDataFromDB(country_query_bi);
					 DigitalAO.mLogger.debug("country_db_output: "+ country_db_output_bi);
					 
					 if (!country_db_output_bi.isEmpty()) {
							DigitalAO.mLogger.debug("Inside country_final_output_bi: ");
							country_final_output_bi.add(country_db_output_bi.get(0).get(0));
							
							DigitalAO.mLogger.debug("country_final_output_address_details :"+country_final_output_bi);
						
						} else {
							DigitalAO.mLogger.debug("country_final_output_bi is empty!!");
							country_final_output_bi.add("NA");
						}
					 
				}
		
				BackgroundComplexInfo = rowCountForApplicant;
				DigitalAO.mLogger.debug("Last Background info  : "+ BackgroundComplexInfo);
				
		
//				 *************************************************end grid ************************************
				
				
//					*****************************self employed Grid************************************************
				
				int SelfBackgroundComplexInfo = iform.getDataFromGrid("company_detail").size();
			 	
			 	DigitalAO.mLogger.debug("self background complex table : "+ SelfBackgroundComplexInfo);
				
				
				ArrayList<String> CompanyNameg=new ArrayList<String>();
				ArrayList<String> PercentageShareholdingGrid=new ArrayList<String>();
				ArrayList<String> countryOfIncorporation=new ArrayList<String>();	
				ArrayList<String> annual_turnover=new ArrayList<String>();
				ArrayList<String> annual_profit_grid =new ArrayList<String>();
				ArrayList<String> Designation =new ArrayList<String>();
				
				
				String countryDealingWithgrid = "";
				ArrayList<String> industry_final_output = new ArrayList<String>();
				String Industry="";
				ArrayList<String> Cont_W_business_out_desc = new ArrayList<String>();
				String industry_out_desc = "";
				
				String Cont_W_business_Qry = "";
				ArrayList<String> final_countries_with_business = new ArrayList<String>();
				
				
				int rowCountForApplicantself = 0;
				
				for (int i = 0; i < SelfBackgroundComplexInfo; i++){
					DigitalAO.mLogger.debug("value of  i "+ i);
					
					CompanyNameg.add(iform.getTableCellValue("company_detail", i, 0));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nCompanyNameg : "+CompanyNameg);
					
					DigitalAO.mLogger.debug("row count complex table : "+ rowCountForApplicantself);
					
					PercentageShareholdingGrid.add(iform.getTableCellValue("company_detail", i, 5));	
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nPercentageShareholdingGrid"+PercentageShareholdingGrid);
					
					countryOfIncorporation.add(iform.getTableCellValue("company_detail", i, 4));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncountryOfIncorporation"+countryOfIncorporation);
					
					annual_turnover.add(iform.getTableCellValue("company_detail", i, 6));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nannual_turnover"+annual_turnover);
					
					annual_profit_grid.add(iform.getTableCellValue("company_detail", i, 7));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nannual_profit_grid"+annual_profit_grid);
					
					Designation.add(iform.getTableCellValue("company_detail", i, 1));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ntradeLicense"+Designation);
					
					countryDealingWithgrid = (iform.getTableCellValue("company_detail", i, 8));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncountryDealingWithgrid"+countryDealingWithgrid);
					
					Industry = (iform.getTableCellValue("company_detail", i,3));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nIndustry"+Industry);
					
					
					rowCountForApplicantself++;
					
					// country with business cound
					
					String[] countries_W_bus_split = countryDealingWithgrid.split(",");
					//String str = iform.getValue("countryDealingWith").toString();
					
					DigitalAO.mLogger.debug("countries_W_bus_split: "+ countries_W_bus_split[0]);
					
					String cd_desc = "";
					for (int b = 0; b < countries_W_bus_split.length; b++) {
						
						Cont_W_business_Qry = "select distinct CD_DESC from NG_MASTER_DAO_COUNTRY_OF_RESIDENCE where CM_CODE='"+ countries_W_bus_split[b] + "'";
						DigitalAO.mLogger.debug("Cont_W_business_Qry :"+ Cont_W_business_Qry);
						
						List<List<String>> Cont_W_business_output = iform.getDataFromDB(Cont_W_business_Qry);
						DigitalAO.mLogger.debug("Cont_W_business_output :" + Cont_W_business_output);
						
						if (!Cont_W_business_output.isEmpty()) {
							 DigitalAO.mLogger.debug("Inside Cont_W_business_out: ");
							 
							//Cont_W_business_out_desc.add(Cont_W_business_output.get(0).get(0)); 
							 if(cd_desc.equals("")){ 
								 cd_desc = Cont_W_business_output.get(0).get(0);
								 DigitalAO.mLogger.debug("cd_desc :"+cd_desc);
							 }
							 else{
								 cd_desc = cd_desc + ", " + Cont_W_business_output.get(0).get(0);
								 DigitalAO.mLogger.debug("cd_desc :"+cd_desc);
							 }
								DigitalAO.mLogger.debug("the final_countries_with_business is empty: ");
								//final_countries_with_business.add(Cont_W_business_output.get(0).get(0));
								
								DigitalAO.mLogger.debug("the final_countries_with_business : "+final_countries_with_business);
						
								DigitalAO.mLogger.debug("final_countries_with_business :"+final_countries_with_business);
							
							
						} else {
							DigitalAO.mLogger.debug("final_countries_with_business is empty!!");
						}
						
					}
					final_countries_with_business.add(cd_desc);
					
				
					// for industry of company details
					industry_out_desc = "select  description from ng_dao_RCC_Industry_master  WITH(NOLOCK) where code='"+ Industry + "'";
					List<List<String>> industry_desc_out = iform.getDataFromDB(industry_out_desc);
					
					if (!industry_desc_out.isEmpty()) {
						DigitalAO.mLogger.debug("Inside industry_desc_out: ");
						 industry_final_output.add(industry_desc_out.get(0).get(0)); 
						
						
					} else {
						DigitalAO.mLogger.debug("industry_desc_out is empty!!");
					}
					
				}
				
				 
				SelfBackgroundComplexInfo = rowCountForApplicantself;
				DigitalAO.mLogger.debug("Last SelfBackgroundComplexInfo info  : "+ SelfBackgroundComplexInfo);
				
				
//				 ********************************************end***************
			 // for logo 
			 document.add(Image.getInstance(System.getProperty("user.dir") + File.separatorChar + "ConfigProps" + File.separatorChar + "Logo.png"));
			   
			 // heading 
			 Paragraph rakbankHeading = new Paragraph("\t"+"\t"+"\t"+"                                                                                     DIGITAL KYC ");
			 document.add(rakbankHeading);
			 
			 //space
			 document.add(new Paragraph("\n"));
			 
			 // Wi name and Date 
			 
			 Paragraph wiName_date = new Paragraph(   "                       WI Name : " +" "+ WorkItemName+ "\n" + "                       Current Date : " + CurrentDateTime);
			 document.add(wiName_date);
			 
			 document.add(new Paragraph("\n"));
			 
			 // personal Information Heading
			 
			 Paragraph personalInfoHeading = new Paragraph("                       Personal Information ");
			 document.add(personalInfoHeading);
			 
			 //space
			 document.add(new Paragraph("\n"));
			 
			 // personal info table
			 PdfPTable personalInfoTable = new PdfPTable(2);
			 PdfPCell personalInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
			 personalInfoTable.addCell(personalInfoCell);
			 
			 personalInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
			 personalInfoTable.addCell(personalInfoCell);
			 personalInfoTable.setHeaderRows(1);
			 
			 personalInfoTable.addCell("Customer Name As Per Passport");
			 personalInfoTable.addCell(CustomerPP);
			 
			 personalInfoTable.addCell("First Name");
			 personalInfoTable.addCell(FirstName);			 
			 personalInfoTable.addCell("Middle Name");
			 personalInfoTable.addCell(MiddleName);			 
			 personalInfoTable.addCell("Last Name");
			 personalInfoTable.addCell(LastName);
			 personalInfoTable.addCell("Email Id");
			 personalInfoTable.addCell(EmailId);
			 personalInfoTable.addCell("Phone Number");
			 personalInfoTable.addCell(PhoneNumber);
			 personalInfoTable.addCell("Place Of Birth");
			 personalInfoTable.addCell(Place_of_birth_descptn);
			 
			 personalInfoTable.addCell("Date Of Birth");
			 personalInfoTable.addCell(doB);
			 
			 // added new by deepanshu 
			 personalInfoTable.addCell("Nationality");
			 personalInfoTable.addCell(finalOutputNAtionality);
			 
			 personalInfoTable.addCell("Secondary Nationality");
			 personalInfoTable.addCell(finalOutputSecondaryNationality); 
			 
			 
			 personalInfoTable.addCell("Risk Score");
			 personalInfoTable.addCell(RiskScore);
			 
			 personalInfoTable.addCell("PEP");
			 personalInfoTable.addCell(PEP_Output);
			 
			 personalInfoTable.addCell("Name of the Employer as per Visa");
			 personalInfoTable.addCell(employer_name_as_per_visa);
			
			 document.add(personalInfoTable);
			 
			 document.add(new Paragraph("\n"));
			 
			 //******************************address grid *********************************
			 document.add(new Paragraph("\n"));
				// Background information
			 Paragraph AddressResidence  = new Paragraph("                       Residence Addresss: ");
			 document.add(AddressResidence);
				//space 
			 document.add(new Paragraph("\n"));
				 
			 int sizeAddressResidenceInfo = iform.getDataFromGrid("address_detail").size();
			 DigitalAO.mLogger.debug("sizeAddressResidenceInfo" + sizeAddressResidenceInfo);
			 
			 PdfPTable residenceAddres = new PdfPTable(8);
		 		
		 		PdfPCell R1 = new PdfPCell(new Phrase("Flat/Villa Number"));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 2:");
				
				
				PdfPCell R2 = new PdfPCell(new Phrase("Building/Villa Name"));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 3:");
				//PdfPCell c1 = new PdfPCell(new Phrase("CIFID"));
				
				
				PdfPCell R3 = new PdfPCell(new Phrase("Street/Location"));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 4:");
				
				
				PdfPCell R4 = new PdfPCell(new Phrase("Nearest Landmark"));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 5:");
			 
				PdfPCell R5 = new PdfPCell(new Phrase("P.O Box"));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 2:");
				
				
				PdfPCell R6 = new PdfPCell(new Phrase("Emirates/City"));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 3:");
//					PdfPCell c1 = new PdfPCell(new Phrase("CIFID"));
				
				
				PdfPCell R7 = new PdfPCell(new Phrase("Country"));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 4:");
				
				
				PdfPCell R8 = new PdfPCell(new Phrase("Address Type"));
				DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 5:");
				
		 		//*************************************************
				try
				{
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append: deepanshu");  
					residenceAddres.addCell(R1);
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 1:");  
					residenceAddres.addCell(R2);
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 2:");  
					residenceAddres.addCell(R3);
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 3 :");  
					residenceAddres.addCell(R4);
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 4:"); 
					residenceAddres.addCell(R5);
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 5:"); 
					residenceAddres.addCell(R6);
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 6:"); 
					residenceAddres.addCell(R7);
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 7:"); 
					residenceAddres.addCell(R8);
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 8:"); 
					
				}
				catch(Exception e)
				{
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", In catch After image : "+e.getMessage());
				} 
				DigitalAO.mLogger.debug("hi hello bye :");
		 		
				for (int k = 0; k < sizeAddressResidenceInfo; k++) {
					try{
						
						
						R1 = new PdfPCell(new Phrase(flatVillaNumber.get(k)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", flatVillaNumber "+flatVillaNumber.get(k));
						R1.setBackgroundColor(new BaseColor(255,251,240));
						residenceAddres.addCell(R1);
						
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", after adding flatVillaNumber:"+flatVillaNumber.get(k));
						
						R2 = new PdfPCell(new Phrase(buildingVillaNumber.get(k)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", buildingVillaNumber "+buildingVillaNumber.get(k));
						R2.setBackgroundColor(new BaseColor(255,251,240));
						residenceAddres.addCell(R2);
						
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", after adding buildingVillaNumber:"+buildingVillaNumber.get(k));
						
						R3 = new PdfPCell(new Phrase(streetLocation.get(k)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", streetLocation "+streetLocation.get(k));
						R3.setBackgroundColor(new BaseColor(255,251,240));
						residenceAddres.addCell(R3);
						
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", after adding streetLocation:"+streetLocation.get(k));
						
						
						R4 = new PdfPCell(new Phrase(NearestLandmark.get(k)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", NearestLandmark "+NearestLandmark.get(k));
						R4.setBackgroundColor(new BaseColor(255,251,240));
						residenceAddres.addCell(R4);
						
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", after adding NearestLandmark:"+NearestLandmark.get(k));
						
						R5 = new PdfPCell(new Phrase(poBox.get(k)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", poBox "+poBox.get(k));
						R5.setBackgroundColor(new BaseColor(255,251,240));
						residenceAddres.addCell(R5);
						
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", after adding poBox:"+poBox.get(k));
						
						R6 = new PdfPCell(new Phrase(emirates_final_output_address_details.get(k)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", emiratesCity "+emirates_final_output_address_details.get(k));
						R6.setBackgroundColor(new BaseColor(255,251,240));
						residenceAddres.addCell(R6);
						
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", after adding emiratesCity:"+emirates_final_output_address_details.get(k));
						
						R7 = new PdfPCell(new Phrase(country_final_output_address_details.get(k)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", country "+country_final_output_address_details.get(k));
						R7.setBackgroundColor(new BaseColor(255,251,240));
						residenceAddres.addCell(R7);
						
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", after adding country:"+country_final_output_address_details.get(k));
						
						R8 = new PdfPCell(new Phrase(AddressType.get(k)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", AddressType "+AddressType.get(k));
						R8.setBackgroundColor(new BaseColor(255,251,240));
						residenceAddres.addCell(R8);
						
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", after adding AddressType:"+AddressType.get(k));
				
					
				}catch(Exception e){
					DigitalAO.mLogger.debug("Exception :"+ e.getMessage());
				}
				
				}
				
				document.add(residenceAddres);
				
				//*******************************************end******************
			 // *******************************Backgroung Grid ********************************
				employement_type = (String) iform.getValue("employement_type");
			 if(employement_type.trim().equalsIgnoreCase("Salaried"))
			 {
				 //****************************************Employement Information****************
				 
				 Paragraph employementDetails = new Paragraph("                       Employement Information ");
				 document.add(employementDetails);
				 
				 //space 
				 document.add(new Paragraph("\n"));
				 
				 //employement table rest do in if condition 
				 
				 PdfPTable employementInfoTable = new PdfPTable(2);
				 PdfPCell employementDetailsInfoCell = new PdfPCell(new Phrase(""));
				 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
				 employementInfoTable.addCell(employementDetailsInfoCell);
				 
				 employementDetailsInfoCell = new PdfPCell(new Phrase(""));
				 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
				 employementInfoTable.addCell(personalInfoCell);
				 employementInfoTable.setHeaderRows(1);
				 
				 employementInfoTable.addCell("Name of the Current Employer ");
				 employementInfoTable.addCell(EmployerName);
				 
				 employementInfoTable.addCell("Current Designation as per Visa ");
				 employementInfoTable.addCell(CurrentDesignation);
				 
				 employementInfoTable.addCell("Expected Monthly Income");
				 employementInfoTable.addCell(ExpectedMonthlyIncome);
				 
				 employementInfoTable.addCell("Employer Code");
				 employementInfoTable.addCell(employerCode);
				
				 document.add(employementInfoTable);
			 
				 document.add(new Paragraph("\n"));
			 }
		
			 // if condition of self employed
			 else if(employement_type.trim().equalsIgnoreCase("Self Employed")){
				 
				 	 document.add(new Paragraph("\n"));
				 	 // extra space
				 	 document.add(new Paragraph("\n"));
					// Background information
					 Paragraph selfBackgroundInfo  = new Paragraph("                       Self Employed Information: ");
					 document.add(selfBackgroundInfo);
					//space 
					 document.add(new Paragraph("\n"));
					 
					 int sizeSelfBackgroundInfo = iform.getDataFromGrid("company_detail").size();
					 DigitalAO.mLogger.debug("sizeSelfBackgroundInfo" + sizeSelfBackgroundInfo);
					 
//				 		
			 		PdfPTable SelfImployedInfotable = new PdfPTable(8);
			 		
			 		PdfPCell c1 = new PdfPCell(new Phrase("Company Name"));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 2:");
					
					
					PdfPCell c2 = new PdfPCell(new Phrase("Percentage Shareholder in Company"));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 3:");
					//PdfPCell c1 = new PdfPCell(new Phrase("CIFID"));
					
					
					PdfPCell c3 = new PdfPCell(new Phrase("Country of Incorporation"));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 4:");
					
					
					PdfPCell c4 = new PdfPCell(new Phrase("Annual Turnover"));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 5:");
				 
					PdfPCell c5 = new PdfPCell(new Phrase("Annual Profit"));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 2:");
					
					
					PdfPCell c6 = new PdfPCell(new Phrase("Designation"));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 3:");
//						PdfPCell c1 = new PdfPCell(new Phrase("CIFID"));
					
					
					PdfPCell c7 = new PdfPCell(new Phrase("Country(ies) with which business conducted"));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 4:");
					
					
					PdfPCell c8 = new PdfPCell(new Phrase("Industry"));
					DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 5:");
					
					
			 		//*************************************************
			 		
					try
					{
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append: deepanshu");  
						SelfImployedInfotable.addCell(c1);
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 1:");  
						SelfImployedInfotable.addCell(c2);
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 2:");  
						SelfImployedInfotable.addCell(c3);
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 3 :");  
						SelfImployedInfotable.addCell(c4);
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 4:"); 
						SelfImployedInfotable.addCell(c5);
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 5:"); 
						SelfImployedInfotable.addCell(c6);
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 6:"); 
						SelfImployedInfotable.addCell(c7);
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 7:"); 
						SelfImployedInfotable.addCell(c8);
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 8:"); 
							
					}
					catch(Exception e)
					{
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", In catch After image : "+e.getMessage());
					}
			 		
					DigitalAO.mLogger.debug("hi hello bye :");
			 		
					for (int j = 0; j < sizeSelfBackgroundInfo; j++) {
						
						try{
		
						c1 = new PdfPCell(new Phrase(CompanyNameg.get(j)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", CompanyNameg "+CompanyNameg.get(j));
						c1.setBackgroundColor(new BaseColor(255,251,240));
						SelfImployedInfotable.addCell(c1);
						
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", before adding PercentageShareholdingGrid:"+PercentageShareholdingGrid.get(j));
						
						c2 = new PdfPCell(new Phrase(PercentageShareholdingGrid.get(j)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr OFAC_ID for WINAME "+WINAME+" : "+PercentageShareholdingGrid.get(j));
						c2.setBackgroundColor(new BaseColor(255,251,240));
						c2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						SelfImployedInfotable.addCell(c2);
						
						
						
						c3 = new PdfPCell(new Phrase(countryOfIncorporation.get(j)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr MatchingText for WINAME "+WINAME+" : "+countryOfIncorporation.get(j));
						c3.setBackgroundColor(new BaseColor(255,251,240));
						c3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						SelfImployedInfotable.addCell(c3);
						
						c4 = new PdfPCell(new Phrase(annual_turnover.get(j)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr Name for WINAME "+WINAME+" : "+annual_turnover.get(j));
						c4.setBackgroundColor(new BaseColor(255,251,240));
						c4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						SelfImployedInfotable.addCell(c4);
						
						c5 = new PdfPCell(new Phrase(annual_profit_grid.get(j)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr Name for WINAME "+WINAME+" : "+annual_profit_grid.get(j));
						c5.setBackgroundColor(new BaseColor(255,251,240));
						c5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						SelfImployedInfotable.addCell(c5);
						
						c6 = new PdfPCell(new Phrase(Designation.get(j)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr Name for WINAME "+WINAME+" : "+Designation.get(j));
						c6.setBackgroundColor(new BaseColor(255,251,240));
						c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						SelfImployedInfotable.addCell(c6);
						
						c7 = new PdfPCell(new Phrase (final_countries_with_business.get(j)));
				
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr Name for WINAME "+WINAME+" : "+"final_countries_with_business: "+final_countries_with_business.get(j));
						c7.setBackgroundColor(new BaseColor(255,251,240));
						c7.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						SelfImployedInfotable.addCell(c7);
						
						c8 = new PdfPCell(new Phrase(industry_final_output.get(j)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr Name for WINAME "+WINAME+" : "+industry_final_output.get(j));
						c8.setBackgroundColor(new BaseColor(255,251,240));
						c8.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						SelfImployedInfotable.addCell(c8);
						
					}catch(Exception e){
						DigitalAO.mLogger.debug("Exception :"+ e.getMessage());
					}
				}
		            document.add(SelfImployedInfotable);								
//			            
					
		     
			 }else{
				 DigitalAO.mLogger.debug("else condition of salired");
			 }
			 //**************************condition end******************
			 
			 document.add(new Paragraph("\n"));
			// Purpose of Account
			 Paragraph PurposeOfAccount  = new Paragraph("                       Purpose Of Account ");
			 document.add(PurposeOfAccount);
			//space 
			 document.add(new Paragraph("\n"));
			 
			 //purpose of account table 
			 
			 PdfPTable purposeOfAccountInfoTable = new PdfPTable(2);
			 PdfPCell PurposeOfAccountDetailsInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
			 purposeOfAccountInfoTable.addCell(PurposeOfAccountDetailsInfoCell);
			 
			 PurposeOfAccountDetailsInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
			 purposeOfAccountInfoTable.addCell(personalInfoCell);
			 purposeOfAccountInfoTable.setHeaderRows(1);
			 
			 purposeOfAccountInfoTable.addCell("Purpose Of Account");
			 purposeOfAccountInfoTable.addCell(purposeOfAccount);
			 document.add(purposeOfAccountInfoTable);
			 
			 // space 
			 document.add(new Paragraph("\n"));
			 
			// Transaction Parameters/ Info:
			 Paragraph transactionParameter  = new Paragraph("                       Transaction Parameter ");
			 document.add(transactionParameter);
			//space 
			 document.add(new Paragraph("\n"));
			 
			 PdfPTable transactionParameterInfoTable = new PdfPTable(2);
			 PdfPCell transactionParameterInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
			 transactionParameterInfoTable.addCell(transactionParameterInfoCell);
			 
			 transactionParameterInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
			 transactionParameterInfoTable.addCell(personalInfoCell);
			 transactionParameterInfoTable.setHeaderRows(1);
			 
			 transactionParameterInfoTable.addCell("Monthly Expected Turnover Cash");
			 transactionParameterInfoTable.addCell(MonthlyExpectedTurnoverCash);
			 
			 transactionParameterInfoTable.addCell("Monthly Expected Turnover-Non Cash");
			 transactionParameterInfoTable.addCell(MonthlyExpectedTurnoverNonCash);	
			
			 document.add(transactionParameterInfoTable);
			 
			// space 
			 document.add(new Paragraph("\n"));
			 
			// Background information
			 Paragraph BackgroundInfo  = new Paragraph("                       Background Information ");
			 document.add(BackgroundInfo);
			//space 
			 document.add(new Paragraph("\n"));
			 document.add(new Paragraph("\n"));
			 
			 PdfPTable BackgroundInfoTable = new PdfPTable(2);
			 PdfPCell BackgroundInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
			 BackgroundInfoTable.addCell(BackgroundInfoCell);
			 DigitalAO.mLogger.debug("value of bi grid :"+BackgroundInfoCell );
			 
			 BackgroundInfoCell = new PdfPCell(new Phrase(""));
			 personalInfoCell.setBackgroundColor(new BaseColor(255,251,240));
			 BackgroundInfoTable.addCell(personalInfoCell);
			 BackgroundInfoTable.setHeaderRows(1);
			 
			 
			 BackgroundInfoTable.addCell("Crypto/NFTs");
			 BackgroundInfoTable.addCell(investmentPortfolioIncludingVirtualAssest);
			 
			 BackgroundInfoTable.addCell("Net Monthly income");
			 BackgroundInfoTable.addCell(IncomeGenerate);
			 BackgroundInfoTable.addCell("Real Estate Owned");
			 BackgroundInfoTable.addCell(realEstateOwned);
			 
			 BackgroundInfoTable.addCell("Rental Income");
			 BackgroundInfoTable.addCell(rented);
			 
			 BackgroundInfoTable.addCell("Other Source Of Income");
			 BackgroundInfoTable.addCell(otherSourceOfIncome);
			 
			 BackgroundInfoTable.addCell("Net Monthly Income");
			 BackgroundInfoTable.addCell(netMonthlyIncome);
			
			 BackgroundInfoTable.addCell("Inheritance"); // latest added by 12 july
			 BackgroundInfoTable.addCell(Inheritance);
			 
			 BackgroundInfoTable.addCell("Inheritance Income");
			 BackgroundInfoTable.addCell(Inheritance_income);
			 
			 BackgroundInfoTable.addCell("Description of Product and services company deals in");
			 BackgroundInfoTable.addCell(Description);
			 
			 document.add(BackgroundInfoTable);
			 
			 //**************************************grid  start******************
			 document.add(new Paragraph("\n"));
			 
			 // condition for High Risk  = Y
			 // if condition for having value in background information  TODO
			 int BackgroundComplexInfoCondition = iform.getDataFromGrid("background_information").size();
			 	
			 	DigitalAO.mLogger.debug("BackgroundComplexInfoCondition: "+ BackgroundComplexInfoCondition);
			 if(!iform.getDataFromGrid("background_information").isEmpty()){
				 
				 if (high_risk.equalsIgnoreCase("Y")){
					 
					 Paragraph BackgroundInfocmplxq  = new Paragraph("                       Background Information  ");
					 document.add(BackgroundInfocmplxq);
					 
					 document.add(new Paragraph("\n"));
					 // extra space
					 document.add(new Paragraph("\n"));
					 int sizeBackgroundInfo = iform.getDataFromGrid("background_information").size();
					 DigitalAO.mLogger.debug("sizeBackgroundInfo" + sizeBackgroundInfo);
					 
//						*********************************************
					 
					 	PdfPTable sizeBackgroundInfo_1 = new PdfPTable(4);
					 	DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable :");
					 
					 
					 	PdfPCell h1 = new PdfPCell(new Phrase("Employer Name"));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 2:");
						
						
						PdfPCell h2 = new PdfPCell(new Phrase("Position Held"));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 3:");
						//PdfPCell c1 = new PdfPCell(new Phrase("CIFID"));
						
						
						PdfPCell h3 = new PdfPCell(new Phrase("Country"));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 4:");
						
						
						PdfPCell h4 = new PdfPCell(new Phrase("Date of Employment"));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 5:");
					 
//							PdfPCell h5 = new PdfPCell(new Phrase("I was not in employment earlier"));
//							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable 6:");
					 
//						 **********************
						
						try
						{
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append: hrtk");  
							sizeBackgroundInfo_1.addCell(h1);
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 1:");  
							sizeBackgroundInfo_1.addCell(h2);
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 2:");  
							sizeBackgroundInfo_1.addCell(h3);
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 3 :");  
							sizeBackgroundInfo_1.addCell(h4);
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 4:"); 
//								sizeBackgroundInfo_1.addCell(h5);
//								DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", After PdfPTable append 5:"); 
							
						}
						catch(Exception e)
						{
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", In catch After image : "+e.getMessage());
						}
						
						
						
						DigitalAO.mLogger.debug("hi hello:");
			
						for (int j = 0; j < sizeBackgroundInfo; j++) {
							
							try{
							
							
							h1 = new PdfPCell(new Phrase(EmployerNamegrid.get(j)));
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", EmployerNamegrid "+EmployerNamegrid.get(j));
							h1.setBackgroundColor(new BaseColor(255,251,240));
							sizeBackgroundInfo_1.addCell(h1);
							
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", before adding PositionHeld:"+PositionHeld.get(j));
							
							h2 = new PdfPCell(new Phrase(PositionHeld.get(j)));
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr OFAC_ID for WINAME "+WINAME+" : "+PositionHeld.get(j));
							h2.setBackgroundColor(new BaseColor(255,251,240));
							h2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
							sizeBackgroundInfo_1.addCell(h2);
							
							
							
							h3 = new PdfPCell(new Phrase(country_final_output_bi.get(j)));
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr MatchingText for WINAME "+WINAME+" : "+country_final_output_bi.get(j));
							h3.setBackgroundColor(new BaseColor(255,251,240));
							h3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
							sizeBackgroundInfo_1.addCell(h3);
							
							h4 = new PdfPCell(new Phrase(DateOfEmployement.get(j)));
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr Name for WINAME "+WINAME+" : "+DateOfEmployement.get(j));
							h4.setBackgroundColor(new BaseColor(255,251,240));
							h4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
							sizeBackgroundInfo_1.addCell(h4);
							
//								h5 = new PdfPCell(new Phrase(wasNotEmployement.get(j)));
//								DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr Name for WINAME "+WINAME+" : "+wasNotEmployement.get(j));
//								h5.setBackgroundColor(new BaseColor(255,251,240));
//								h5.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
//								sizeBackgroundInfo_1.addCell(h5);
							
							
						}catch(Exception e){
							DigitalAO.mLogger.debug("Exception :"+ e.getMessage());
						}
			
					}
			            document.add(sizeBackgroundInfo_1);
				
//						 ***************************grid end here for background information**********************
				 }else{
					 DigitalAO.mLogger.debug("Come Check This High risk::");
				 }
				 
			 }
			 
		        
			 document.add(new Paragraph("\n"));
			 document.close();
			 
			 DigitalAO.mLogger.debug("document" + document);
			 String response = AttachDocumentWithWI(iform,WINAME,pdfName );
			 return response;
		}
		catch(Exception e){
			System.out.print("Exception generate_kyc_temp : "+e.getMessage());
			DigitalAO.mLogger.debug("Exception generate_kyc_temp ;" + e.getMessage());
			return "";
		}
	}
}

