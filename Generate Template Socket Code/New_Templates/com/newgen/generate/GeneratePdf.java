
package com.newgen.generate;


import com.newgen.Populatepdf.Populatepdf;
import com.newgen.mvcbeans.model.WorkdeskModel;
import com.newgen.wfdesktop.session.WDSession;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.io.FileWriter;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;
import com.newgen.wfdesktop.xmlapi.WFInputXml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.*;
import org.jsoup.nodes.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.itextpdf.text.pdf.PdfReader;
//import com.lowagie.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.itextpdf.text.html.simpleparser.HTMLWorker;

import ISPack.ISUtil.JPDBRecoverDocData;
import ISPack.ISUtil.JPISException;
import ISPack.ISUtil.JPISIsIndex;
import ISPack.ISUtil.*;
import ISPack.*;

import com.newgen.generate.XMLGen;
import com.newgen.generate.XMLParser;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

public class GeneratePdf {

	
	static String sFilePath = "";
	static  int sessionCheckInt=0;
	static  int loopCount=50;
	static  int waitLoop=50;
	static String sessionID=null;
	static  NGEjbClient ngEjbClient;
	static XMLParser xmlparser;
	
	private static Logger logger = Logger.getLogger("consoleLogger");

	private static Logger loggerErr = Logger.getLogger("errorLogger");
	
 public static String replaceGRTemplateArgs(File targetFile, String argumentString) {
HashMap<String,String> HtemData=new HashMap<String,String>();

String sInputPath=System.getProperty("user.dir")+File.separator+"Templates"+File.separator+"ApplicationForm.pdf";

//String sOutputPath=System.getProperty("user.dir")+File.separator+"Output"+File.separator+"Output"+targetFile.getName();

//copytemplatefile(sInputPath,targetFile.getAbsolutePath().substring(0, targetFile.getAbsolutePath().lastIndexOf('.')) + ".pdf");
HtemData = parser(argumentString);
String[] hm=Populatepdf.ProcesspdfFile(true,sInputPath,HtemData,targetFile.getAbsolutePath());
System.out.println("Done");
        /**
         * templateFile -> Original Template File targetFile -> Output File
         * argumentString -> Argument String (with values and type)
         *  Hook to replace the arguments of generate response template.
        Write custom code here to replace the arguments from the template and write the output in the target file. Finally,
        after writing into target file, client need to return true from the hook.
        If client returns true from the hook then system will not replace the arguments
        from the template.
         */

		
        return "PDF Generated";
    }	

	
	   public boolean copytemplatefile(String inputfilepath, String targetFilePath){

		File inputfile =new File(inputfilepath);
	   if(inputfile.renameTo(new File(targetFilePath))){
		   System.out.println("copied----");
		return true;
	   }else{
		   System.out.println("Not copied");
		return false;
	   }
    }
	
	
public static HashMap<String,String> parser(String strArgList){
        HashMap<String,String> HtemData=new HashMap<String,String>();
         String[] values = strArgList.split("&");
        for(int i=1;i<values.length;i++){
            if(i%2==0){
                //StringBuilder sb = new StringBuilder(values[i]);
               // sb.deleteCharAt(0);
               // values[i] = sb.toString();
				
				values[i] = values[i].replace("@10", "");
            }
            //System.out.println(values[i]+" "+i);
        }
        
        for(int i=1;i<values.length-1;i++){
            HtemData.put(values[i],values[i+1]);
            i++;
        }
		
        return HtemData;
        
    }
	
	public static String generateHTML(File targetFile, String argumentString, String DocName, String wi_name, String cabinetname, String sessionId, String jtsIP )
	{
		String sInputPath=System.getProperty("user.dir")+File.separator+"Templates"+File.separator+DocName+".HTML";
		System.out.println("sInputPath---" + sInputPath);
		
		String cssInputPath=System.getProperty("user.dir")+File.separator+"Templates"+File.separator+"formatting.css";
		String outputPath=targetFile+File.separator+DocName+".pdf";
		executepdfmethod(argumentString, sInputPath,outputPath,wi_name, cabinetname, sessionId, jtsIP,cssInputPath);
		
		 return "HTML generated";
	}
	
	 public static void executepdfmethod(String inputParams, String tempatePath,String outputPath, String wi_name, String cabinetname, String sessionId, String jtsIP ,String cssInputPath){
         
          try {
        	  logger.debug("Inside executepdfmethod");
              FileInputStream inputStream = new FileInputStream(tempatePath) ;   
              Document document = new Document(PageSize.LETTER);
              PdfWriter pdfWriter = PdfWriter.getInstance
              (document, new FileOutputStream(outputPath));
              document.open();
              document.addAuthor("Newgen");
              document.addCreator("Newgen");
              document.addSubject("RAK Bank");
              document.addCreationDate();
              document.addTitle("");
              String str=convertFileToString(tempatePath);
			  String cssfile = convertFileToString(cssInputPath);
              String prefix="&amp;&lt;";
              String postfix="&gt;&amp;";
				ByteArrayInputStream cis =
                	    new ByteArrayInputStream(cssfile.toString().getBytes());
                Map<String,String> hm =parseArgumentString(inputParams);
			 
                // using for-each loop for iteration over Map.entrySet()
                for (Entry<String, String> entry : hm.entrySet()) {
                    if(str.contains(entry.getKey())){                    	
                    	str=str.replaceAll(prefix+entry.getKey()+postfix, entry.getValue());
                    }
                   
                }
				
               str = getDynamicGriddata( str, wi_name, cabinetname, sessionId, jtsIP);
			ByteArrayInputStream bis =
			new ByteArrayInputStream(str.toString().getBytes());
			//System.out.println("executepdfmethod str ---" + str);
              XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
             
              worker.parseXHtml(pdfWriter, document, bis,cis);
			  
			  String logoPath = System.getProperty("user.dir")+File.separatorChar+"Templates"+File.separatorChar+"RAK_Logo.png";
			  document.close();
              inputStream.close();
			  
			  PdfReader reader = new PdfReader(outputPath);
			//String opPath = System.getProperty("user.dir")+File.separatorChar+"TemplateOp";
			
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPath.substring(0,outputPath.lastIndexOf(File.separatorChar))+File.separatorChar+"NewCAMReport.pdf"));
			com.itextpdf.text.pdf.PdfContentByte content = stamper.getOverContent(1);
			
			Image image = Image.getInstance(logoPath);
			// scale the image to 50px height
			image.scaleAbsoluteHeight(50);
			image.scaleAbsoluteWidth((image.getWidth() * 50) / image.getHeight());
			
			image.setAbsolutePosition(10, 705);
			
			content.addImage(image);
			stamper.close();
			reader.close();
			
			  
        } catch (FileNotFoundException e) {
        	loggerErr.error("FileNotFoundException in executepdfmethod ():",e);		
        } catch (DocumentException e) {
        	loggerErr.error("DocumentException in executepdfmethod ():",e);		
        } catch (IOException e) {
        	loggerErr.error("IOException in executepdfmethod ():",e);		
        }catch (Exception e) {
        	loggerErr.error("Exception in executepdfmethod ():",e);		
        }
         
         
    }
	
	public static Map<String,String> parseArgumentString(String strArgList)
	{
        Map<String,String> HtemData=new HashMap<String,String>();

        strArgList=strArgList.replaceAll("<", "");
        strArgList= strArgList.replaceAll(">", "");
        String[] values = strArgList.split("&");
        for(int i=1;i<values.length;i++){
            if(i%2==0){
                values[i] = values[i].replace("@10", "");
            }
        }
      
        for(int i=1;i<values.length-1;i++){
            HtemData.put(values[i],values[i+1]);
            i++;
        }
      
        return HtemData;
      
    }
	
	  @SuppressWarnings("resource")
	public static String convertFileToString(String tempatePath) throws FileNotFoundException
	{      
           FileInputStream fis = new FileInputStream(tempatePath);
            String inputStreamString = new Scanner(fis,"UTF-8").useDelimiter("\\A").next();
            try {
				fis.close();
			} catch (IOException e) {
	        	loggerErr.error("IOException in convertFileToString ():",e);		
			}
            return inputStreamString;
           
    }
	
	
	public static String attachDocument(String filetobeaddedpath,String workItemName,String DocName,String jtsIP, String jtsPort, String cabinetName,  String volumeID, String userName, String password, String sessionID)
	{
		try
		{
			String wiAddDocumentOutputXML="";
			String docIndex="";
			if(sessionID==null)
			{
				sessionID=getSessionIDFirstTime(cabinetName,jtsIP, jtsPort,userName, password);
			}
			
			if(filetobeaddedpath.equalsIgnoreCase("")||workItemName.equalsIgnoreCase("")||DocName.equalsIgnoreCase(""))
			{
				return  "N";
			}
			XMLParser objXMLParser = new XMLParser();
			Integer intISIndex;
			int intVolumeId;
			JPISIsIndex IsIndex = new JPISIsIndex();
			JPDBRecoverDocData JPISDEC = new JPDBRecoverDocData();
			JPISDEC.m_cDocumentType = 'N';
			JPISDEC.m_sVolumeId = Short.parseShort(volumeID);
			File fppp = new File(filetobeaddedpath);
			String DocAttach="Y";
			String strDocISIndex = null;
			if(fppp.exists())
			{
				System.out.println("fpp exists : "+fppp.getPath());
			}
			else
			{
				System.out.println("fpp does not exists");
				DocAttach="N";
			}
			if(!DocAttach.equalsIgnoreCase("N"))
			{
				if(fppp.isFile())
				{
					System.out.println("fpp is file");
				}
				else
				{
					System.out.println("fpp is not file");
					DocAttach="N";
				}
			}
			if(!DocAttach.equalsIgnoreCase("N"))
			{
				System.out.println("Before AddDocument_MT Completion");
				try
				{
					System.out.println("aaaaaaaaa : "+jtsIP);
					System.out.println("aaaaaaaaa : "+jtsPort);
					System.out.println("aaaaaaaaa : "+cabinetName);
				
					if(jtsPort.startsWith("33"))
					{
						CPISDocumentTxn.AddDocument_MT(null,jtsIP,(short)(Integer.parseInt(jtsPort)),cabinetName,JPISDEC.m_sVolumeId, fppp.getPath(), JPISDEC, "",IsIndex);
					}	
					else
					{
						CPISDocumentTxn.AddDocument_MT(null,jtsIP,(short)(Integer.parseInt(jtsPort)),cabinetName,JPISDEC.m_sVolumeId, fppp.getPath(), JPISDEC, "","JNDI" ,IsIndex);
					}
					strDocISIndex = String.valueOf(IsIndex.m_nDocIndex) + "#" + String.valueOf(IsIndex.m_sVolumeId);
					System.out.println("Document added in SMS: strDocISIndex: "+ strDocISIndex);
					System.out.println("AddDocument_MT Completed successfully");
				}
				catch(Exception e)
				{
					System.out.println("Exception in CPISDocumentTxn");
					DocAttach="N";
				}
				catch(JPISException e)
				{
					System.out.println("Exception in CPISDocumentTxn : "+e);
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					System.out.println("Exception in CPISDocumentTxn 2 : "+sw);
					DocAttach="N";
				}
			}
			if(!DocAttach.equalsIgnoreCase("N"))
			{
				intISIndex=(int)IsIndex.m_nDocIndex;
				intVolumeId=(int)IsIndex.m_sVolumeId;

				String sQueryFoldIndex = "SELECT FOLDERINDEX FROM PDBFOLDER WHERE NAME ='" + workItemName + "'";
				String APSelectOutputFoldIndex="";
				sessionCheckInt=0;
				while(sessionCheckInt<loopCount)
				{
					String APSelectInputFoldIndex = ExecuteQuery_APSelect(sQueryFoldIndex,cabinetName,sessionID);
					//System.out.println("APSelectInputFoldIndex : " + APSelectInputFoldIndex);
					
					try
					{
						APSelectOutputFoldIndex=WFNGExecute(APSelectInputFoldIndex,jtsIP,"2809",1);
					}
					catch(Exception e)
					{
						System.out.println("Exception in Execute : " + e);
						sessionCheckInt++;
						waiteloopExecute(waitLoop);
						sessionID = getConnectInputXML(cabinetName,userName,password);
						continue;
					}
					//System.out.println("APSelectOutputFoldIndex : " + APSelectOutputFoldIndex);
					sessionCheckInt++;
					if (xmlparser.getTagValue(APSelectOutputFoldIndex,"MainCode").equalsIgnoreCase("11"))
					{
						sessionID = getConnectInputXML(cabinetName,userName,password);
					}
					else
					{
						sessionCheckInt++;
						break;
					}
				}	
				objXMLParser.setInputXML(APSelectOutputFoldIndex);
				String mainCodeAPSelectFoldIndex = "";
				mainCodeAPSelectFoldIndex=objXMLParser.getValueOf("MainCode");
				if (!mainCodeAPSelectFoldIndex.equalsIgnoreCase("0"))
				{
					System.out.println("Problem in APSELECT mainCodeAPSelectFoldIndex");
					DocAttach="N";
				}
				if(!DocAttach.equalsIgnoreCase("N"))
				{
					String folderIndex=objXMLParser.getValueOf("td");
					System.out.println("folderIndex : "+folderIndex);
					String APSelectOutputDocSize="";
					sessionCheckInt=0;
					while(sessionCheckInt<loopCount)
					{
						String sQueryDocSize = "select DOCSIZE from isdoc WHERE docindex ='" + intISIndex + "' and volumeid = '"+volumeID+"' order by docsize desc";
						String APSelectInputDocSize = ExecuteQuery_APSelect(sQueryDocSize,cabinetName,sessionID);
						//System.out.println("APSelectInputDocSize : " + APSelectInputDocSize);
						
						try
						{
							APSelectOutputDocSize=WFNGExecute(APSelectInputDocSize,jtsIP,"2809",1);
						}
						catch(Exception e)
						{
							System.out.println("Exception in Execute : " + e);
							sessionCheckInt++;
							waiteloopExecute(waitLoop);
							sessionID = getConnectInputXML(cabinetName,userName,password);
							continue;
						}					
						//System.out.println("APSelectOutputDocSize : " + APSelectOutputDocSize);
						sessionCheckInt++;
						if (xmlparser.getTagValue(APSelectOutputDocSize,"MainCode").equalsIgnoreCase("11"))
						{
							sessionID = getConnectInputXML(cabinetName,userName,password);
						}
						else
						{
							sessionCheckInt++;
							break;
						}
					}
					objXMLParser.setInputXML(APSelectOutputDocSize);
					String mainCodeAPSelectDocSize = "";
					mainCodeAPSelectDocSize=objXMLParser.getValueOf("MainCode");
					if (!mainCodeAPSelectDocSize.equalsIgnoreCase("0"))
					{
						System.out.println("Problem in APSELECT mainCodeAPSelectDocSize");					
						DocAttach="N";
					}
					if(!DocAttach.equalsIgnoreCase("N"))
					{
						String docSize="";
						
						docSize=objXMLParser.getValueOf("td");

						System.out.println("docSize : "+docSize);

						String strISIndex =String.valueOf(JPISDEC.m_nDocIndex)+ "#"+ String.valueOf(JPISDEC.m_sVolumeId)+ "#" ;
						System.out.println("strISIndex "+strISIndex);
						String intISIndexVolId = intISIndex.toString() +"#"+intVolumeId+"#";
						System.out.println("intISIndexVolId "+intISIndexVolId);
						
						sessionCheckInt=0;
						while(sessionCheckInt<loopCount)
						{
							String wiAddDocumentInputXML = get_NGOAddDocument_Input(cabinetName,sessionID,folderIndex,docSize,DocName,intISIndexVolId,volumeID, fppp.getPath());

							//System.out.println("wiAddDocumentInputXML "+wiAddDocumentInputXML);
							
							try
							{
								wiAddDocumentOutputXML = WFNGExecute(wiAddDocumentInputXML,jtsIP,"2809",1);
							}
							catch(Exception e)
							{
								System.out.println("Exception in Execute : " + e);
								sessionCheckInt++;
								waiteloopExecute(waitLoop);
								sessionID = getConnectInputXML(cabinetName,userName,password);
								continue;
							}					
							//System.out.println("wiAddDocumentOutputXML : "+wiAddDocumentOutputXML);
							sessionCheckInt++;
							if (xmlparser.getTagValue(wiAddDocumentOutputXML,"MainCode").equalsIgnoreCase("11"))
							{
								sessionID = getConnectInputXML(cabinetName,userName,password);
							}
							else
							{
								sessionCheckInt++;
								break;
							}
						}	
						objXMLParser.setInputXML(wiAddDocumentOutputXML);
						String mainCodeforCreateWI = "";
						mainCodeforCreateWI=objXMLParser.getValueOf("Status");
						if (!mainCodeforCreateWI.equalsIgnoreCase("0"))
						{
							DocAttach="N";						
						}
						else
						{
							docIndex=objXMLParser.getValueOf("DocumentIndex");
							DocAttach="Y";
							DocAttach = DocAttach+"~"+wiAddDocumentOutputXML+"~"+docIndex;						 
							
						}
					}
				}
			}
			return DocAttach;
		}
		catch(Exception e)
		{
			return  "N";
		}
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
	
	public static String ExecuteQuery_APSelect(String sQuery,String sEngineName,String sSessionId)
	{
		WFInputXml wfInputXml = new WFInputXml();

		wfInputXml.appendStartCallName("APSelect", "Input");
		wfInputXml.appendTagAndValue("Query",sQuery);
		wfInputXml.appendTagAndValue("EngineName",sEngineName);
		wfInputXml.appendTagAndValue("SessionId",sSessionId);
		wfInputXml.appendEndCallName("APSelect","Input");
		return wfInputXml.toString();
	}
	
	public static String get_NGOAddDocument_Input(String cabinetName, String sessionID, String folderIndex,String docSize,String DocumentName,String strISIndex, String volumeID, String filePath)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();
		
		ipXMLBuffer.append("<?xml version=\"1.0\"?>\n");
		ipXMLBuffer.append("<NGOAddDocument_Input>\n");
		ipXMLBuffer.append("<Option>NGOAddDocument</Option>\n");
		ipXMLBuffer.append("<CabinetName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</CabinetName>\n");
		ipXMLBuffer.append("<UserDBId>");
		ipXMLBuffer.append(sessionID);
		ipXMLBuffer.append("</UserDBId>\n");
		ipXMLBuffer.append("<GroupIndex>0</GroupIndex>\n");
		ipXMLBuffer.append("<ParentFolderIndex>");
		ipXMLBuffer.append(folderIndex);
		ipXMLBuffer.append("</ParentFolderIndex>\n");
		ipXMLBuffer.append("<DocumentName>");
		ipXMLBuffer.append(DocumentName);
		ipXMLBuffer.append("</DocumentName>\n");
		ipXMLBuffer.append("<CreatedByAppName>pdf</CreatedByAppName>\n");
		ipXMLBuffer.append("<Comment></Comment>\n");
		ipXMLBuffer.append("<VersionComment></VersionComment>\n");
		ipXMLBuffer.append("<VolumeIndex>");
		ipXMLBuffer.append(volumeID);
		ipXMLBuffer.append("</VolumeIndex>\n");
		ipXMLBuffer.append("<FilePath>");
		ipXMLBuffer.append(filePath);
		ipXMLBuffer.append("</FilePath>\n");
		ipXMLBuffer.append("<DataDefinition></DataDefinition>\n");
		ipXMLBuffer.append("<ISIndex>");
		ipXMLBuffer.append(strISIndex);
		ipXMLBuffer.append("</ISIndex>\n");
		ipXMLBuffer.append("<NoOfPages>32</NoOfPages>\n");
		ipXMLBuffer.append("<DocumentType>N</DocumentType>\n");
		ipXMLBuffer.append("<DocumentSize>");
		ipXMLBuffer.append(docSize);
		ipXMLBuffer.append("</DocumentSize>\n");
		ipXMLBuffer.append("</NGOAddDocument_Input>");
		   
		return ipXMLBuffer.toString();   
	}
	
	public static String getConnectInputXML(String cabinetName,	String username, String password)
	{
		StringBuffer ipXMLBuffer=new StringBuffer();
		
		ipXMLBuffer.append("<?xml version=\"1.0\"?>");
		ipXMLBuffer.append("<WMConnect_Input>");
		ipXMLBuffer.append("<Option>WMConnect</Option>");
		ipXMLBuffer.append("<EngineName>");
		ipXMLBuffer.append(cabinetName);
		ipXMLBuffer.append("</EngineName>\n");
		ipXMLBuffer.append("<ApplicationInfo></ApplicationInfo>\n");
		ipXMLBuffer.append("<Participant>\n");
		ipXMLBuffer.append("<Name>");
		ipXMLBuffer.append(username);
		ipXMLBuffer.append("</Name>\n");
		ipXMLBuffer.append("<Password>");
		ipXMLBuffer.append(password);
		ipXMLBuffer.append("</Password>\n");
		ipXMLBuffer.append("<Scope></Scope>\n");
		ipXMLBuffer.append("<UserExist>N</UserExist>\n");
		ipXMLBuffer.append("<Locale>en-us</Locale>\n");
		ipXMLBuffer.append("<ParticipantType>U</ParticipantType>\n");
		ipXMLBuffer.append("</Participant>");
		ipXMLBuffer.append("</WMConnect_Input>");
		
		return ipXMLBuffer.toString(); 
	}
	
	private static String WFNGExecute(String ipXML, String jtsServerIP, String serverPort,int flag) throws IOException,Exception
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
	
	
	public static String getDynamicGriddata(String htmlString,String wi_name,String cabinetname,String sesionId,String jtsIP){
		String formattedHtmlString = null;
		org.jsoup.nodes.Document doc =  Jsoup.parse(htmlString);
		Elements tables = doc.getElementsByTag("table");
		String logoPath = System.getProperty("user.dir")+File.separatorChar+"Templates"+File.separatorChar+"RAK_Logo.png";
		//System.out.println("initial html string=: "+htmlString);
		Map<String,String> tbodyToQuery = new HashMap<String,String>();
		for(Element table:tables) {
			if(!table.id().equals("")) {
			//System.out.println("table id=: "+table.id());
			String columns="";
			String query="";
			String tableId= table.id();
			Element headerRowTr = doc.getElementById(tableId+"_headerRow");
			//System.out.println(headerRowTr.html());
			for(Element column:headerRowTr.getAllElements()) {
				
				//System.out.println("column html: "+column.html());
				if(column.hasAttr("columnName")) {
				columns+=column.attr("columnName")+",";
				}
			}
			if(columns.endsWith(",")) {
				columns = columns.substring(0,columns.length()-1);
			}
			query = "select "+columns+" from "+table.attr("dbTablename")+ " where wi_name='"+wi_name+"'";
			tbodyToQuery.put(tableId+"_body", query);
		}
		}	
		for(Map.Entry<String,String> entry : tbodyToQuery.entrySet()) {
			String queryToexxecute = entry.getValue();
			
			String inputXml = ExecuteQuery_APSelectWithColumnNames(queryToexxecute,cabinetname,sesionId);
			//System.out.println("final html inputXml=: "+inputXml);
			String opXml=null;
			List<List<String>> results;
			try {
				opXml = WFNGExecute(inputXml,jtsIP,"2809",1);
				//System.out.println("final html inputXml=: "+opXml);
				
				opXml=opXml.replaceAll(">\\s+<", "><");
				//System.out.println("final opXML =: "+opXml);
				 results = xmlparser.getTagValueCAM(opXml);
				//System.out.println("final html results=: "+results);
				Element tbody = doc.getElementById(entry.getKey());
				String appendHtml="";
				if(results!=null) {
				for(List<String> row: results) {
					appendHtml+="<tr>";
					for(String element: row) {
						appendHtml+="<td><p>"+element+"</p></td>";
					}
					appendHtml+="</tr>";
				}
				}
				tbody.append(appendHtml);
				//tbody.append("<img src=\""+logoPath+"\" />");
				appendHtml="";
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		/*Element td = doc.getElementById("rak_logo_img");
		String img = "<img src=\""+logoPath+"\" />";
		td.append(img);*/
		formattedHtmlString = doc.html();
		//System.out.println("final html string=: "+formattedHtmlString);
		return formattedHtmlString;
	}
	
	
	
	
	
	private static String getSessionIDFirstTime(String cabName,String JTSIP, String JTSPORT,String userName, String password)
	{
		String sessionID="";
		int i = -9;
		String desc = null;
		try
		{
			String connectInput = getConnectInputXML(cabName, userName, password);

			System.out.println("Input XML for wmconnect : " + connectInput);
			
			String wmconnectOP="";
			int sessionCheckInt=loopCount-1;
			while(sessionCheckInt<loopCount)
			{
				try
				{
					wmconnectOP=WFNGExecute(connectInput,JTSIP,JTSPORT,1);
					break;
				}
				catch(Exception e)
				{
					System.out.println("Exception in Execute of getsession ID: " + e);
					sessionCheckInt++;
					waiteloopExecute(waitLoop);
					continue;
				}
			}

			System.out.println("wmconnect output: " + wmconnectOP);
			if (!(xmlparser.getTagValue(wmconnectOP,"Option").equalsIgnoreCase("WMConnect")))
			{
				return "Error Invalid Workflow Server IP and Port are registered.";
			}
			i = Integer.parseInt(xmlparser.getTagValue(wmconnectOP,"MainCode"));
			if (i == 0)
			{
				sessionID = xmlparser.getTagValue(wmconnectOP,"SessionId");
				return sessionID;
			}
			else
			{
				desc = xmlparser.getTagValue(wmconnectOP,"Description");

				i = Integer.parseInt(xmlparser.getTagValue(wmconnectOP,"SubErrorCode"));
			}
		}
		catch (Exception lExcp)
		{
			System.out.println("Execption occurred in getsessionid" + ": " + lExcp.toString());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			lExcp.printStackTrace(pw);
			System.out.println("Exception in getSessionID : "+sw);
		}
		return "Error " + desc;
	}
	
	private static void waiteloopExecute(long wtime)
	{
        try
		{
            for (int i = 0; i < 10; i++)
			{
                Thread.yield();
                Thread.sleep(wtime / 10);
            }
        }
		catch (InterruptedException e)
		{
           System.out.println(e.toString());
        }
    }
	
	

}   /*  End of the TestXIRR class. */