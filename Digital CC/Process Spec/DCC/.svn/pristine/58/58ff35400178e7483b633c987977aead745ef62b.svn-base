package com.newgen.iforms.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;


import org.json.simple.JSONArray;
import org.xml.sax.SAXException;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
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
/*
public class DAOTemplate extends DigitalAO_Common{
	public String clickEvent(IFormReference iform, String controlName, String data)
			throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
		DigitalAO.mLogger.debug("DigitalAO_Template");
		DigitalAO.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + getActivityName(iform)
				+ ", controlName " + controlName + ", data " + data); 
		
		
		String WINAME = getWorkitemName(iform);
		String pdfName = "DAO_Template_kyc";
		// code by deepanshu prashar for template generating

		if (controlName.equalsIgnoreCase("template_generate")) {
			DigitalAO.mLogger.debug("controlName" + controlName);
			
			 //XWPFDocument document = null;

			// for generate pdf in user location in form template
			try{
				
				String TemplatePath = "/ibm/IBM/WebSphere/AppServer/profiles/AppSrv01/installedApps/ant1casapps01Node01Cell/DAO_war.ear/DAO.war/PDFTemplates/DAO_Templates/"+ WINAME + pdfName + ".pdf";
				DigitalAO.mLogger.debug("TemplatePath" + TemplatePath);
				Document document = new Document(PageSize.A4.rotate());
				PdfWriter.getInstance(document, new FileOutputStream(TemplatePath));
				document.open();
				Font bold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
				
				//assign variables
				// personal Information
				 String WorkItemName = String.valueOf(iform.getValue("WI_name"));
				 String CurrentDateTime = (String)iform.getValue("CreatedDateTime");
				 
				
				 
				 String FirstName = (String)iform.getValue("Given_Name");
				 String LastName = (String)iform.getValue("Surname");
				 
				 String CustomerPP = FirstName + " " + LastName;
				// String CustomerNameAsPerPassport = (String)iform.getValue(CustomerPP);
				 
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
				
				 String doB= (String)iform.getValue("DOB");
				 String RiskScore = (String)iform.getValue("risk_score");
				// String FamilyMemberAssociation = (String)iform.getValue("Relation_Detail_w_PEP");
				 String PEP = (String)iform.getValue("PEP");
				 
				 //employement 
				 String EmployerName = (String)iform.getValue("Employment_Status");
				 String CurrentDesignation =(String)iform.getValue("current_visa_designation");
				 String ExpectedMonthlyIncome =(String)iform.getValue("gross_monthly_salary_income");
				 
				 
				 //purpose of account
				 String purposeOfAccount =(String)iform.getValue("Purpose_of_account");
				 // Transaction Parameters/ Info:
				 String MonthlyExpectedTurnoverCash =(String)iform.getValue("Monthly_expected_turnover_Cash");
				 String MonthlyExpectedTurnoverNonCash =(String)iform.getValue("Monthly_expected_turnover_non_cash");
				 // background info
				 
				 String investmentPortfolioIncludingVirtualAssest = (String)iform.getValue("investment_portfolio_including_virtual_asset");
				 String IncomeGenerate = (String)iform.getValue("income_generated");
				 String realEstateOwned = (String)iform.getValue("real_Est_owned");
				 String rented = (String)iform.getValue("Rented");
				 String otherSourceOfIncome =(String)iform.getValue("other_Source_of_income");
				 String Description = (String)iform.getValue("Description_of_product_and_services_company_deals_");
				 
				 
				 	//********************** grid background information check with array**********************************background info
				 	int BackgroundComplexInfo = iform.getDataFromGrid("background_information").size();
				 	
				 	DigitalAO.mLogger.debug("background complex table : "+ BackgroundComplexInfo);
					
					int Radiocount=0;
					ArrayList<String> EmployerNamegrid=new ArrayList<String>();
					ArrayList<String> PositionHeld=new ArrayList<String>();
					ArrayList<String> CountryGrid=new ArrayList<String>();	
					ArrayList<String> DateOfEmployement=new ArrayList<String>();
					int rowCountForApplicant = 0;
					
					for (int i = 0; i < BackgroundComplexInfo; i++){
						DigitalAO.mLogger.debug("value of  i "+ i);
						EmployerNamegrid.add(iform.getTableCellValue("background_information", i, 0));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nOFAC_ID : "+EmployerNamegrid);
						DigitalAO.mLogger.debug("row count complex table : "+ rowCountForApplicant);
						
						PositionHeld.add(iform.getTableCellValue("background_information", i, 2));	
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nPositionHeld"+PositionHeld);
						CountryGrid.add(iform.getTableCellValue("background_information", i, 3));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nCountryGrid"+CountryGrid);
						DateOfEmployement.add(iform.getTableCellValue("background_information", i, 4));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nDateOfEmployement"+DateOfEmployement);
				
						rowCountForApplicant++;
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
					ArrayList<String> tradeLicense =new ArrayList<String>();
					ArrayList<String> countryDealingWithgrid=new ArrayList<String>();
					ArrayList<String> Industry=new ArrayList<String>();
					
					
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
						
						tradeLicense.add(iform.getTableCellValue("company_detail", i, 5));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ntradeLicense"+tradeLicense);
						
						countryDealingWithgrid.add(iform.getTableCellValue("company_detail", i, 8));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \ncountryDealingWithgrid"+countryDealingWithgrid);
						
						Industry.add(iform.getTableCellValue("company_detail", i, 3));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", \nIndustry"+Industry);
				
						rowCountForApplicantself++;
					}
					SelfBackgroundComplexInfo = rowCountForApplicantself;
					DigitalAO.mLogger.debug("Last SelfBackgroundComplexInfo info  : "+ SelfBackgroundComplexInfo);
					
					
//				 ********************************************end***************
				 // for logo 
				 document.add(Image.getInstance(System.getProperty("user.dir") + File.separatorChar + "FormTemplate" + File.separatorChar + "Logo.png"));
				   
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
				 personalInfoTable.addCell("Last Name");
				 personalInfoTable.addCell(LastName);
				 personalInfoTable.addCell("Email Id");
				 personalInfoTable.addCell(EmailId);
				 personalInfoTable.addCell("Phone Number");
				 personalInfoTable.addCell(PhoneNumber);
				 personalInfoTable.addCell("Place Of Birth");
				// personalInfoTable.addCell(output_PlaceOfBirth_query);
				 personalInfoTable.addCell(Place_of_birth_descptn);
				 
				 
				 personalInfoTable.addCell("Date Of Birth");
				 personalInfoTable.addCell(doB);  // after added
   				 
				 personalInfoTable.addCell("Risk Score");
				 personalInfoTable.addCell(RiskScore);
				 personalInfoTable.addCell("PEP");
				 personalInfoTable.addCell(PEP);
				 document.add(personalInfoTable);
				 
				 document.add(new Paragraph("\n"));
				 
				 // employment details
				 String employement_type = (String) iform.getValue("employement_type");
				 if(employement_type.trim().equalsIgnoreCase("Salaried"))
				 {
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
					 
					 employementInfoTable.addCell("Employement Name");
					 employementInfoTable.addCell(EmployerName);
					 
					 employementInfoTable.addCell("Current Visa Designation");
					 employementInfoTable.addCell(CurrentDesignation);
					 
					 employementInfoTable.addCell("Expected Monthly Income");
					 employementInfoTable.addCell(ExpectedMonthlyIncome);
					
					 document.add(employementInfoTable);
				 
					 document.add(new Paragraph("\n"));
				 }
			
				 // if condition of self employed
				 else if(employement_type.trim().equalsIgnoreCase("Self Employed")){
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
						
						
						PdfPCell c6 = new PdfPCell(new Phrase("Trade License"));
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
							
							c6 = new PdfPCell(new Phrase(tradeLicense.get(j)));
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr Name for WINAME "+WINAME+" : "+tradeLicense.get(j));
							c6.setBackgroundColor(new BaseColor(255,251,240));
							c6.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
							SelfImployedInfotable.addCell(c6);
							
							c7 = new PdfPCell(new Phrase(countryDealingWithgrid.get(j)));
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr Name for WINAME "+WINAME+" : "+countryDealingWithgrid.get(j));
							c7.setBackgroundColor(new BaseColor(255,251,240));
							c7.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
							SelfImployedInfotable.addCell(c7);
							
							c8 = new PdfPCell(new Phrase(Industry.get(j)));
							DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr Name for WINAME "+WINAME+" : "+Industry.get(j));
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
				 
				 
				 BackgroundInfoTable.addCell("Investment Portfolio Including virtual Assist");
				 BackgroundInfoTable.addCell(investmentPortfolioIncludingVirtualAssest);
				 
				 BackgroundInfoTable.addCell("Income Generated");
				 BackgroundInfoTable.addCell(IncomeGenerate);
				 BackgroundInfoTable.addCell("Real Estate Owned");
				 BackgroundInfoTable.addCell(realEstateOwned);
				 BackgroundInfoTable.addCell("Income Generate");
				 BackgroundInfoTable.addCell(IncomeGenerate);
				 BackgroundInfoTable.addCell("Rented");
				 BackgroundInfoTable.addCell(rented);
				 BackgroundInfoTable.addCell("Other Source Of Income");
				 BackgroundInfoTable.addCell(otherSourceOfIncome);
				 BackgroundInfoTable.addCell("Description of Product and services company deals in");
				 BackgroundInfoTable.addCell(Description);
				 
				 document.add(BackgroundInfoTable);
				 //**************************************
				 document.add(new Paragraph("\n"));

				 
		         
			 // ******************************** grid ************
				 Paragraph BackgroundInfocmplxq  = new Paragraph("                       Background Information  ");
				 document.add(BackgroundInfocmplxq);
				 
				 document.add(new Paragraph("\n"));
				 int sizeBackgroundInfo = iform.getDataFromGrid("background_information").size();
				 DigitalAO.mLogger.debug("sizeBackgroundInfo" + sizeBackgroundInfo);
				 
//				*********************************************
				 
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
				 
					
				 
//				 **********************
					
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
						
						
						
						h3 = new PdfPCell(new Phrase(CountryGrid.get(j)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr MatchingText for WINAME "+WINAME+" : "+CountryGrid.get(j));
						h3.setBackgroundColor(new BaseColor(255,251,240));
						h3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						sizeBackgroundInfo_1.addCell(h3);
						
						h4 = new PdfPCell(new Phrase(DateOfEmployement.get(j)));
						DigitalAO.mLogger.debug("WINAME : "+getWorkitemName(iform)+", WSNAME: "+getActivityName(iform)+", Aftr Name for WINAME "+WINAME+" : "+DateOfEmployement.get(j));
						h4.setBackgroundColor(new BaseColor(255,251,240));
						h4.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
						sizeBackgroundInfo_1.addCell(h4);
						
						
					}catch(Exception e){
						DigitalAO.mLogger.debug("Exception :"+ e.getMessage());
					}
		
				}
	                document.add(sizeBackgroundInfo_1);
				 
				 
				 
				 
				 
//				 ***************************grid end here for background information**********************
	                
				
				 
				 document.add(new Paragraph("\n"));
				 
				 
				 
				 
				 document.close();
				 
				 DigitalAO.mLogger.debug("document" + document);
				 String response = AttachDocumentWithWI(iform,WINAME,pdfName );
				 return response;
			}catch(Exception e){
				System.out.print(e);
			}
		

		
		}
		return "";
	}
}
*/
