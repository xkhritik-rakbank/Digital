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

public class test_java {

	public static void main(String[] args) throws FileNotFoundException {
		try{
			String sInputPath=System.getProperty("user.dir")+File.separator+"Templates"+File.separator+"DIB"+File.separator+"Memo(Others)_1"+".HTML";
			String cssInputPath=System.getProperty("user.dir")+File.separator+"Templates"+File.separator+"DIB"+File.separator+"formatting.css";
			String target_file_loc=System.getProperty("user.dir")+File.separator+"GRTemplate"+File.separator+"DIB_out";				
			File file = new File(sInputPath);
			File file_out = new File(target_file_loc);
			String outputPath=file_out+File.separator+"Memo(Others)_1"+".pdf";
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
	       //  String  inputParams = "&<header>&FPU REPORT@10&<Current_date_time>&28/05/2019 11:57@10&<Mobile1>&00971547246546@10&<Mobile2>&00971547246546@10&<Contacted_on>&Yes@10&<Mobile>&@10&<cust_verifiers_name>&@10&<cust_verifiers>&@10&<cust_Designation>&IT SPECIALIST@10&<cust_DOJ>&01/06/2017@10&<cust_employment_status>&PERMANENT@10&<cust_salary>&10,250@10&<salary_payment_mode>&5001006@10&<yrs_uae>&2.00@10&<previous_employer>&@10&<cust_fpu_remarks>&@10&<ofc_tel_number>&00971473556545@10&<landline_number>&@10&<emp_verifiers_name>&@10&<emp_verifiers>&@10&<application_type>&Salaried@10&<emp_Designation>&IT SPECIALIST@10&<emirate>&DUBAI@10&<emp_DOJ>&01/06/2017@10&<emp_employement_status>&PERMANENT@10&<emp_salary>&10,250@10&<emp_salary>&10,250@10&<emp_salary_break>&@10&<emp_salary_paymode>&5001006@10&<loan_deduction>&@10&<positive_financial_strength>&@10&<emp_fpu_remarks>&hh@10&<Date>&@10&<field_visit_initiated>&@10&<field_visit_initiated_date>&@10&<field_visit_initiated_time>&@10&<field_visit_report>&@10&<bank_acc_details>&@10&<salary_credits>&@10&<acc_conduct>&@10&<liability_check>&@10&<repayment_conduct>&@10&<creditcard_stat_verifi>&@10&<acc_stat_verifi>&@10&<nlc_verifi>&@10&<company_list_check>&@10&<Aecb_Mismatch>&@10~CC-0030036583-process~FPU_REPORT_SALARIED~-427598500~~~";
			//String  inputParams = "&<check1>&No@10&<check>&No@10&<check3>&No@10&<desc>& @10&<header>&SALARIED CREDIT CARD@10&<loan>&Loan@10&<desc1>& @10&<IncInPL>&Yes@10&<IncInCC>&Yes@10&<check1>&No@10&<check>&No@10&<check3>&No@10&<desc>& @10&<header>&SALARIED CREDIT CARD@10&<loan>&Loan@10&<desc1>& @10&<IncInPL>&Yes@10&<IncInCC>&Yes@10&<Remarks_Init>&ok@10&<Remarks_Ops>&ok@10&<Remarks_Feedback>&@10&<Remarks_CPV>&HR pending@10&<Remarks_Sales>&@10&<Remarks_CPV_Feed>&@10&<Remarks_CAD>&@10&<Remarks_CAD1>&Remarks CA@10&<Remarks_CAD2>&Remarks CAD@10&<Remarks_Compliance>&@10&<CC_Wi_name>&CC-0030035858-process@10&<company>&@10&<company_cifno>&@10&<trade>&@10&<comapny_lob>&@10&<passport_valid>&@10&<RM_Name>&@10&<gender>&Male@10&<MAritalStatus>&SINGLE@10&<Nationality>&CUBAN@10&<National>&No@10&<SalaryDay>&8@10&<Salary_transfer>&Yes@10&<EmpStatus>&PERMANENT@10&<Accprovided>&@10&<Confirmed>&YES@10&<Movement>&@10&<Cuurent_Designation>&DOCTOR@10&<Customer_cifno>&5000245@10&<EmpName>&GULF COMPANY FOR CONTRACTING  AND  GENERAL ENTERPRISES LLC@10&<EMpCode>&5482@10&<VisaSponser>&dsjfjshf@10&<LOS>&0.06@10&<FinalDBR>&3.62@10&<FinalTAI>&300000.00@10&<Entry_Date>&06/12/2018@10&<Account_Number>&@10&<Emp_Type>&Salaried@10&<targetSegCode>&EMPLOYEE ID SURROGATE SEGMENT @10&<marketcode>&BUSINESS AS USUAL@10&<Top_UP>&@10&<Micro>&BUILDING CONTRACTING@10&<Macro>&GENERAL CONTRACTING@10&<Strength>&batca1-25/12/2018 16:5-test@10&<Weakness>&batca1-25/12/2018 16:5-test@10&<Maturity_Age>&@10&<Total_Exposure>&450000.00@10&<PLST>&Open@10&<PLE>&@10&<PLN>&@10&<PLCAT>&CAT B@10&<PLCATC>&Visited Open@10&<ALOC_CC>&@10&<ALOC_PL>&@10&<High>&NO@10&<NepType>&@10&<Resident>&Resident@10&<Age>&27.02@10&<CROPS>&@10&<Bank_Name>& @10&<Acc_No>& @10&<AvgCR33>&@10&<avgbal3>&@10&<AvgCR6>&@10&<avgbal6>&@10&<Collection>&@10&<Classification>&AECB HISTORY LESS THAN 12 MONTHS@10&<FieldVisitedDone>&@10&<LoanMultiple>&@10&<Bank_From>&@10&<Bank_To>&@10&<MIS>&@10&<Channel>&@10&<Promo>&@10&<High>&@10&<source_code>&Branch_Init@10&<Net_Salary>&200,000.00@10&<application_cat>&Surrogate@10&<ECRN>&200634900@10&<CRN>&200634900@10&<customer_name>&Elizabeth  Vincent@10&<Current_date_time>&31/12/2018@10 19:34&<DBRNET>&5.43@10&<TAI>&300000.00@10&<InsuranceAmount>&@10&<EMI>&@10&<Dec_Remarks>&@10&<ReferTo>&Credit@10&<ReferReason>&@10&<Decision>&@10&<existing>&NO@10&<Tenor>&@10&<InterestRate>&@10&<MOL_Var>&@10&<IMAMOUNT>&450000@10&<FirstRepayDate>&@10&<Last_Repay>&@10&<Card_prod>&My RAK Card Expat@10&<score_grade>&@10&<Doc_name>&@10&<Doc_Sta>&@10&<Deferred>&@10&<Processing>&@10&<company_name>&@10&<Applicant_Category>&Normal@10&<Employer_Category>&CAT B@10&<comp_CIF>&@10&<TL_Expiry>&@10&<Indus_sector>&@10&<LOB>&@10&<DectechDecision>&Declined@10&<Product_type>&Conventional@10&<requested_limit>&450000@10&<subproduct>&Salaried Credit Card@10&<requested_product>&Credit Card@10&<applicant_type>&New@10&<Card_Product>&My RAK Card Expat@10&<Scheme>&@10&<WORSTL24>& @10&<TOTAL>&@10&<AECB>&@10&<WORSTD24>&NO@10&<authorize_name>&@10&<auth_passex>&@10&<auth_cif>&@10&<auth_dob>&@10&<auth_nat>&@10&<auth_visaex>&@10&<auth_visadob>&@10&<loan_limit>&15,000@10&<Remarks_1>& @10&<Remarks_2>& @10&<Remarks_3>& @10&<Remarks_4>& @10&<Remarks_5>& @10&<Remarks_6>& @10&<Remarks_7>& @10&<Remarks_8>& @10&<Remarks_9>& @10&<Remarks_10>& @10";
	          // Map<String,String> hm =parseArgumentString(inputParams);
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
			 //str = getDynamicGriddata( str, "1235", "abc", "213546", "546879", "546213");
				ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes());
				System.out.println("executepdfmethod str ---" + str);
				 String cssfile = convertFileToString(cssInputPath);
	           XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
	           ByteArrayInputStream cis = new ByteArrayInputStream(cssfile.getBytes());
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
				if(columns.endsWith(","))  {
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