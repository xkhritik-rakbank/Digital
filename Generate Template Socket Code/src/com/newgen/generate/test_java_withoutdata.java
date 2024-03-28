package com.newgen.generate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;
import com.newgen.wfdesktop.xmlapi.WFInputXml;

public class test_java_withoutdata {

	public static void main(String[] args) throws FileNotFoundException {
		try{
			String sInputPath=System.getProperty("user.dir")+File.separator+"Templates"+File.separator+"Cheque_Form_C"+".pdf";
			String cssInputPath=System.getProperty("user.dir")+File.separator+"Templates"+File.separator+"formatting.css";
			String target_file_loc=System.getProperty("user.dir")+File.separator+"GRTemplate"+File.separator+"123";				
			File file = new File(sInputPath);
			File file_out = new File(target_file_loc);
			String outputPath=file_out+File.separator+"RISK_RATING_REPORT"+".pdf";
			FileInputStream inputStream = new FileInputStream(file) ;   
	        Document document = new Document(PageSize.LETTER);
	        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
	        /*pdfWriter.setEncryption(userPassword.getBytes(), 
				    ownerPassword.getBytes(),
		                    PdfWriter.ALLOW_PRINTING, 
		                    PdfWriter.ENCRYPTION_AES_256);*/
	        document.open();
	        document.addAuthor("Newgen");
	        document.addCreator("Newgen");
	        document.addSubject("RAK Bank");
	        document.addCreationDate();
	        document.addTitle("");
			// TODO Auto-generated method stub
			 //FileInputStream inputStream = new FileInputStream(tempatePath) ;   
	         //String  inputParams = "&<portal_ref>&1229@10&<portal_ref_CL>&Serial No.  -  1229@10&<customer_name>&AIGUL KUDAIKULOVA@10&<card_type>&RAKBANK Emirates Skywards World Elite Credit Card@10&<title>&MS.@10&<Nationality>&Kazakhstan-KZ@10&<emirates_id>&784199087480242@10&<passport_no>&N12701716@10&<PIDOB>&31/12/1990@10&<dateofbirth>&31121990@10&<MotherName>&MUKHAMEJANOVA@10&<lastname>&KUDAIKULOVA@10&<Fname>&AIGUL@10&<Mname>&@10&<MobileNo1_part1>&00971@10&<MobileNo1_part2>&561619977@10&<HomeCOuntryNo_part1>&00770@10&<HomeCOuntryNo_part2>&17177721@10&<skyward_no>&00658760804@10&<nameoncard>&AIGUL KUDAIKULOVA@10&<Designation>&DIRECTOR@10&<Email1>&AIGULKUDAIKUL@GMAIL.COM@10&<iban>&660260000214244578401@10&<statement_period>&28/02/2020 to 28/06/2020@10&<employment_type>&SALARIED@10&<EmployerName>&YOUNG &amp; AMPAMP RUBICAM@10&<Macro>&MEDIA@10&<Net_Salary>&24,000.00@10&<amount>&72,000.00@10&<amount_words>&SEVENTY-TWO THOUSAND  DIRHAMS @10&<passp>&PASSPORT@10&<MailingAdd_building_name>&YOUNG &ampamp RUBICAM FZ-LLC@10&<street_home>&@10&<MailingAdd_zip_code>&14129@10&<MailingAdd_city>&DUBAI@10&<MailingAdd_country>&United Arab Emirates-AE@10&<flat_home>&BAISHESHEK 34@10&<building_home>&ALMATY@10&<street_home>&-@10&<pobox_home>&-@10&<city_home>&ALMATY@10&<home_ctry>&Kazakhstan-KZ@10&<flat_resi>&RESIDENCE 110@10&<building_resi>&DUBAI BUSINESS BAY@10&<street_resi>&APT 1502@10&<pobox_resi>&00000@10&<city_resi>&DUBAI@10&<res_ctry>&United Arab Emirates-AE@10&<pobox_ofc>&14129@10&<city_ofc>&DUBAI@10&<ctry_ofc>&United Arab Emirates-AE@10&<cover_desc>&YOUNG &ampamp RUBICAM FZ-LLC  DUBAI 14129@10&<Friend1_Name>&RIAD JOUMBLAT@10&<Friend1_Mobile_part1>&00971@10&<Friend1_Mobile_part2>&522434443@10&<CTR1>&Kazakhstan-KZ@10&<TINNo1>&@10&<NOTINEng1>&@10&<PICOB>&Kazakhstan-KZ@10&<PITOB>&ALMAYY@10&<fatcaCheck>&YES@10&<date_full>&08/09/2020@10&<date_today>&08092020@10~CDOB-0080000023-process~Cheque_Form_C~1430976429~~~~1229";
	         String  inputParams="&<portal_ref>&15250@10&<portal_ref_CL>&Serial No.  -  15250@10&<customer_name>&SUNIL NICHLANI@10&<card_type>&RAKBANK Emirates Skywards World Elite Credit Card@10&<title>&MR.@10&<Nationality>&India-IN@10&<emirates_id>&784197413639499@10&<passport_no>&R2352376@10&<PIDOB>&09/02/1974@10&<dateofbirth>&09021974@10&<MotherName>&KAVITA@10&<lastname>&NICHLANI@10&<Fname>&SUNIL@10&<Mname>&@10&<MobileNo1_part1>&00971@10&<MobileNo1_part2>&566531824@10&<HomeCOuntryNo_part1>&00919@10&<HomeCOuntryNo_part2>&004003574@10&<skyward_no>&00203421116@10&<nameoncard>&SUNIL@10&<Designation>&TEACHER@10&<Email1>&SUNIL9274@HOTMAIL.COM@10&<iban>&460030010037098132001@10&<statement_period>&12/01/2022 to 12/05/2022@10&<employment_type>&SALARIED@10&<EmployerName>&RAK MEDICAL AND HEALTH SC UNIV@10&<Macro>&EDUCATIONAL@10&<Net_Salary>&37,602.33@10&<amount>&187,400.00@10&<amount_words>&ONE HUNDRED EIGHTY-SEVEN THOUSAND FOUR HUNDRED  DIRHAMS @10&<passp>&PASSPORT@10&<MailingAdd_building_name>&RAK MEDICAL AND HEALTH SC UNIV@10&<street_home>&@10&<MailingAdd_zip_code>&11172@10&<MailingAdd_city>&RAS AL-KHAIMAH@10&<MailingAdd_country>&United Arab Emirates-AE@10&<flat_home>&2001 RUBY REGENCY TOWERS CHS ANAND NAGAR THANE W@10&<building_home>&MAHARASHTRA INDIA 400615@10&<street_home>&-@10&<pobox_home>&-@10&<city_home>&MAHARASHTRA@10&<home_ctry>&India-IN@10&<flat_resi>&117 RAK MEDICAL AND HEALTH SCIENCES UNIVERSITY@10&<building_resi>&RASALKHAIMAH UAE 11172@10&<street_resi>&FLAT 5 A BLOCK AL URAIBI BLDG ALNAKHEEL RAK UAE@10&<pobox_resi>&11172@10&<city_resi>&RAS AL-KHAIMAH@10&<res_ctry>&United Arab Emirates-AE@10&<pobox_ofc>&11172@10&<city_ofc>&RAS AL-KHAIMAH@10&<ctry_ofc>&United Arab Emirates-AE@10&<cover_desc>&RAK MEDICAL AND HEALTH SC UNIV  RAS AL-KHAIMAH 11172@10&<Friend1_Name>&NAGRAJ GANESAN@10&<Friend1_Mobile_part1>&00971@10&<Friend1_Mobile_part2>&503746879@10&<CTR1>&India-IN@10&<TINNo1>&AAQPN5180N@10&<NOTINEng1>&@10&<PICOB>&India-IN@10&<PITOB>&THANE@10&<fatcaCheck>&YES@10&<date_full>&28/05/2022@10&<date_today>&28052022@10~CDOB-0080000725-process~Cheque_Form_C~154370800~~~~15250";
	         //String  inputParams = "&<check1>&No@10&<check>&No@10&<check3>&No@10&<desc>& @10&<header>&SALARIED CREDIT CARD@10&<loan>&Loan@10&<desc1>& @10&<IncInPL>&Yes@10&<IncInCC>&Yes@10&<check1>&No@10&<check>&No@10&<check3>&No@10&<desc>& @10&<header>&SALARIED CREDIT CARD@10&<loan>&Loan@10&<desc1>& @10&<IncInPL>&Yes@10&<IncInCC>&Yes@10&<Remarks_Init>&ok@10&<Remarks_Ops>&ok@10&<Remarks_Feedback>&@10&<Remarks_CPV>&HR pending@10&<Remarks_Sales>&@10&<Remarks_CPV_Feed>&@10&<Remarks_CAD>&@10&<Remarks_CAD1>&Remarks CA@10&<Remarks_CAD2>&Remarks CAD@10&<Remarks_Compliance>&@10&<CC_Wi_name>&CC-0030035858-process@10&<company>&@10&<company_cifno>&@10&<trade>&@10&<comapny_lob>&@10&<passport_valid>&@10&<RM_Name>&@10&<gender>&Male@10&<MAritalStatus>&SINGLE@10&<Nationality>&CUBAN@10&<National>&No@10&<SalaryDay>&8@10&<Salary_transfer>&Yes@10&<EmpStatus>&PERMANENT@10&<Accprovided>&@10&<Confirmed>&YES@10&<Movement>&@10&<Cuurent_Designation>&DOCTOR@10&<Customer_cifno>&5000245@10&<EmpName>&GULF COMPANY FOR CONTRACTING  AND  GENERAL ENTERPRISES LLC@10&<EMpCode>&5482@10&<VisaSponser>&dsjfjshf@10&<LOS>&0.06@10&<FinalDBR>&3.62@10&<FinalTAI>&300000.00@10&<Entry_Date>&06/12/2018@10&<Account_Number>&@10&<Emp_Type>&Salaried@10&<targetSegCode>&EMPLOYEE ID SURROGATE SEGMENT @10&<marketcode>&BUSINESS AS USUAL@10&<Top_UP>&@10&<Micro>&BUILDING CONTRACTING@10&<Macro>&GENERAL CONTRACTING@10&<Strength>&batca1-25/12/2018 16:5-test@10&<Weakness>&batca1-25/12/2018 16:5-test@10&<Maturity_Age>&@10&<Total_Exposure>&450000.00@10&<PLST>&Open@10&<PLE>&@10&<PLN>&@10&<PLCAT>&CAT B@10&<PLCATC>&Visited Open@10&<ALOC_CC>&@10&<ALOC_PL>&@10&<High>&NO@10&<NepType>&@10&<Resident>&Resident@10&<Age>&27.02@10&<CROPS>&@10&<Bank_Name>& @10&<Acc_No>& @10&<AvgCR33>&@10&<avgbal3>&@10&<AvgCR6>&@10&<avgbal6>&@10&<Collection>&@10&<Classification>&AECB HISTORY LESS THAN 12 MONTHS@10&<FieldVisitedDone>&@10&<LoanMultiple>&@10&<Bank_From>&@10&<Bank_To>&@10&<MIS>&@10&<Channel>&@10&<Promo>&@10&<High>&@10&<source_code>&Branch_Init@10&<Net_Salary>&200,000.00@10&<application_cat>&Surrogate@10&<ECRN>&200634900@10&<CRN>&200634900@10&<customer_name>&Elizabeth  Vincent@10&<Current_date_time>&31/12/2018@10 19:34&<DBRNET>&5.43@10&<TAI>&300000.00@10&<InsuranceAmount>&@10&<EMI>&@10&<Dec_Remarks>&@10&<ReferTo>&Credit@10&<ReferReason>&@10&<Decision>&@10&<existing>&NO@10&<Tenor>&@10&<InterestRate>&@10&<MOL_Var>&@10&<IMAMOUNT>&450000@10&<FirstRepayDate>&@10&<Last_Repay>&@10&<Card_prod>&My RAK Card Expat@10&<score_grade>&@10&<Doc_name>&@10&<Doc_Sta>&@10&<Deferred>&@10&<Processing>&@10&<company_name>&@10&<Applicant_Category>&Normal@10&<Employer_Category>&CAT B@10&<comp_CIF>&@10&<TL_Expiry>&@10&<Indus_sector>&@10&<LOB>&@10&<DectechDecision>&Declined@10&<Product_type>&Conventional@10&<requested_limit>&450000@10&<subproduct>&Salaried Credit Card@10&<requested_product>&Credit Card@10&<applicant_type>&New@10&<Card_Product>&My RAK Card Expat@10&<Scheme>&@10&<WORSTL24>& @10&<TOTAL>&@10&<AECB>&@10&<WORSTD24>&NO@10&<authorize_name>&@10&<auth_passex>&@10&<auth_cif>&@10&<auth_dob>&@10&<auth_nat>&@10&<auth_visaex>&@10&<auth_visadob>&@10&<loan_limit>&15,000@10&<Remarks_1>& @10&<Remarks_2>& @10&<Remarks_3>& @10&<Remarks_4>& @10&<Remarks_5>& @10&<Remarks_6>& @10&<Remarks_7>& @10&<Remarks_8>& @10&<Remarks_9>& @10&<Remarks_10>& @10";
	           Map<String,String> hm =parseArgumentString(inputParams);
			 String str =convertFileToString(sInputPath);
			 String prefix="&amp;&lt;";
	         String postfix="&gt;&amp;";
	           // using for-each loop for iteration over Map.entrySet()
			/*for (Entry<String, String> entry : hm.entrySet()) {
				 System.out.println("key to replace: "+entry.getKey());
	             if(str.contains(entry.getKey())){                    	
	             	str=str.replaceAll(prefix+entry.getKey()+postfix, entry.getValue());
	             }
	            
	         }*/
			 str = getDynamicGriddata( str, "1235", "abc", "213546", "546879", "546213");
				ByteArrayInputStream bis =
				new ByteArrayInputStream(str.getBytes());
				System.out.println("executepdfmethod str ---" + str);
				 String cssfile = convertFileToString(cssInputPath);
	           XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
	           ByteArrayInputStream cis =
	           	    new ByteArrayInputStream(cssfile.getBytes());
	           worker.parseXHtml(pdfWriter, document, bis,cis);
	           document.close();
	             inputStream.close();
	              PdfReader reader = new PdfReader(outputPath);
	  			//String opPath = System.getProperty("user.dir")+File.separatorChar+"TemplateOp";
	  			
	  			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPath.substring(0,outputPath.lastIndexOf(File.separatorChar))+File.separatorChar+"NewCAMReport.pdf"));
	  			com.itextpdf.text.pdf.PdfContentByte content = stamper.getOverContent(1);
	  			stamper.close();
	  			reader.close();
	  			System.out.println("generated");
		}
		catch(Exception e){
			System.out.println("Exception occured "+ e.getMessage());
		}
		
           }
	public static String convertFileToString(String tempatePath) throws FileNotFoundException
	{      
		  	String inputStreamString="";
			FileInputStream fis=null;
			try {
			fis = new FileInputStream(tempatePath);
			inputStreamString= new Scanner(fis,"UTF-8").useDelimiter("\\A").next();
			fis.close();
            } catch (IOException e) {
            	System.out.println("IOException in convertFileToString ():");		
			}
            finally {
            	try {
            		if(null!=fis) {
            			fis.close();
            		}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("Exception occurred: ");
				}
            }
            return inputStreamString;
           
    }
	public static String getDynamicGriddata(String htmlString,String wi_name,String cabinetname,String sesionId,String jtsIP, String serverPort){
		String formattedHtmlString = null;
		org.jsoup.nodes.Document doc =  Jsoup.parse(htmlString);
		Elements tables = doc.getElementsByTag("table");
		String logoPath = System.getProperty("user.dir")+File.separatorChar+"Templates"+File.separatorChar+"RAK_Logo.png";
		
		Map<String,String> tbodyToQuery = new HashMap<String,String>();
		for(Element table:tables) {
			if(!table.id().equals("")) {

				String columns="";
				String query="";
				String tableId= table.id();
				Element headerRowTr = doc.getElementById(tableId+"_headerRow");
				System.out.println("getDynamicGriddata() tableId:"+tableId);
				for(Element column:headerRowTr.getAllElements()) {
					if(column.hasAttr("columnName")) {
						columns+=column.attr("columnName")+",";
					}
				}
				if(columns.endsWith(",")) {
					columns = columns.substring(0,columns.length()-1);
				}
				System.out.println("getDynamicGriddata() columns:"+columns);
				//new cosde by saurabh to handle efms alert status is deviations grid. on 7th Jan
				if("deviation_det".equals(tableId)) {
					if(wi_name.contains("CC-")) {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " with (nolock) where wi_name='"+wi_name+"' union all select isnull(replace(Manual_Deviation_Reason,'#',','),'NA'),'Manual Deviation' from ng_rlos_decisionHistory where wi_name='"+wi_name+"' union all select case when EFMS_AlertStatusFlag is null or EFMS_AlertStatusFlag='NULL' or EFMS_AlertStatusFlag='' then (case when EFMS_IS_Alerted is null or EFMS_IS_Alerted='NULL' or EFMS_IS_Alerted='' then 'Not Processed' else EFMS_IS_Alerted end) else EFMS_AlertStatusFlag end as 'status','EFMS Status' as AlertStatus from NG_CC_EXTTABLE with(nolock) where CC_Wi_Name = '"+wi_name+"'";
					}
					else if(wi_name.contains("PL-")) {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " with (nolock) where wi_name='"+wi_name+"' union all select isnull(replace(Manual_Deviation_Reason,'#',','),'NA'),'Manual Deviation' from ng_rlos_decisionHistory where wi_name='"+wi_name+"' union all select case when EFMS_AlertStatusFlag is null or EFMS_AlertStatusFlag='NULL' or EFMS_AlertStatusFlag='' then (case when EFMS_IS_Alerted is null or EFMS_IS_Alerted='NULL' or EFMS_IS_Alerted='' then 'Not Processed' else EFMS_IS_Alerted end) else EFMS_AlertStatusFlag end as 'status','EFMS Status' as AlertStatus from NG_PL_EXTTABLE with(nolock) where PL_wi_name = '"+wi_name+"'";
					}
					else{
						query = "select "+columns+" from "+table.attr("dbTablename")+ " with (nolock) where wi_name='"+wi_name+"'";
					}
				}
				else if("smart_check".equals(tableId)){
					if(table.attr("winamecol")!="") {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where "+table.attr("winamecol")+"='"+wi_name+"'";
					}
					else {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where wi_name='"+wi_name+"'";
					}
				}
				else {
				query = "select "+columns+" from "+table.attr("dbTablename")+ " with (nolock) where wi_name='"+wi_name+"'";
				}
				
				//query = "select "+columns+" from "+table.attr("dbTablename")+ " with (nolock) where wi_name='"+wi_name+"'";
				System.out.println("getDynamicGriddata() query:"+query);
				tbodyToQuery.put(tableId+"_body", query);
			}
		}	
		for(Map.Entry<String,String> entry : tbodyToQuery.entrySet()) {
			String queryToexxecute = entry.getValue();
			System.out.println("getDynamicGriddata() KeyValue:"+entry.getKey());
			System.out.println("Deepak Query to execute: "+queryToexxecute);
			String inputXml = ExecuteQuery_APSelectWithColumnNames(queryToexxecute,cabinetname,sesionId);
			System.out.println("Deepak input xml Query: "+inputXml);
			String opXml=null;
			List<List<String>> results;
			try {
				opXml = WFNGExecute(inputXml,jtsIP,serverPort,1);
				System.out.println("getDynamicGriddata() opXml:"+opXml);
				opXml=opXml.replaceAll(">\\s+<", "><");
				results = XMLParser.getTagValueCAM(opXml);
				//logger.info("getDynamicGriddata() results:"+results);
				Element tbody = doc.getElementById(entry.getKey());
				String appendHtml="";
				if(results!=null) {
				for(List<String> row: results) {
					appendHtml+="<tr>";
					for(String element: row) {
						appendHtml+="<td class=\"tdprops gridTd\"><p>"+element+"</p></td>";
					}
					appendHtml+="</tr>";
				}
				}
				System.out.println("getDynamicGriddata() appendHtml to be put:"+appendHtml);
				tbody.append(appendHtml);
				//tbody.append("<img src=\""+logoPath+"\" />");
				appendHtml="";
			} catch (Exception e) {

				System.out.println("Exception occurred: "+e);
				//e.printStackTrace();
			}

		}
		
		
		/*Element td = doc.getElementById("rak_logo_img");
		String img = "<img src=\""+logoPath+"\" />";
		td.append(img);*/
		formattedHtmlString = doc.html();
		
		return formattedHtmlString;
	}
	
	public static String ExecuteQuery_APSelectWithColumnNames(String sQuery,String sEngineName,String sSessionId)
	{
		WFInputXml wfInputXml = new WFInputXml();

		wfInputXml.appendStartCallName("APSelectWithColumnNames", "Input");
		wfInputXml.appendTagAndValue("Query",sQuery);
		wfInputXml.appendTagAndValue("EngineName",sEngineName);
		wfInputXml.appendTagAndValue("SessionId",sSessionId);
		wfInputXml.appendEndCallName("APSelectWithColumnNames","Input");
		return wfInputXml.toString();
	}
	private static String WFNGExecute(String ipXML, String jtsServerIP, String serverPort,int flag) throws IOException
	{
		
		try{
			
		if(serverPort.startsWith("33"))
			return WFCallBroker.execute(ipXML,jtsServerIP,Integer.parseInt(serverPort),1);
		else
		{
			return NGEjbClient.getSharedInstance().makeCall(jtsServerIP,serverPort,"WebSphere",ipXML); 
		}
			//
		}
		catch(Exception e){
			System.out.println("Exception Occured in WF NG Execute : "+e.getMessage());	
			return "Error";
		}
		
	}
	public static Map<String,String> parseArgumentString(String strArgList)
	{
		Map<String,String> HtemData=new HashMap<String,String>();
		try{
			//Deepak Code commented & updated to handle data with &
			/*strArgList=strArgList.replaceAll("<", "");
	        strArgList= strArgList.replaceAll(">", "");
	        String[] values = strArgList.split("&");*/
			
			strArgList=strArgList.replaceAll("&<", "splitstr_##8##;");
	        strArgList= strArgList.replaceAll(">&", "splitstr_##8##;");
	        String[] values = strArgList.split("splitstr_##8##;");
	        for(int i=1;i<values.length;i++){
	            if(i%2==0){
	                values[i] = values[i].replace("@10", "");
					values[i] = values[i].replaceAll("SynPlus", "\\+");
				   values[i] = values[i].replaceAll("SynPerc", "\\%");
				   values[i] = values[i].replaceAll("<NEXT LINE>", ". ");
				   if(values[i].contains("<")&&values[i].contains(">")){
					   values[i] = values[i].replaceAll("<", " ");
					   values[i] = values[i].replaceAll(">", " ");
				   }
				}
	        }
	      
		  for(int i=1;i<values.length;i++){
	            if(i%2==0){
	                values[i] = values[i].replace("--Select--", "");
	               
	            }
	        }
		  
	        for(int i=1;i<values.length-1;i++){
	            HtemData.put(values[i],values[i+1]);
	            i++;
	        }
		}
		catch(Exception e){
			System.out.println("Exception occured in parseArgumentString" + e.getMessage());
		}
        return HtemData;
      
    }
}