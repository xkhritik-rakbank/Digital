
package com.newgen.generate;


import com.newgen.Populatepdf.Populatepdf;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;
import com.newgen.wfdesktop.xmlapi.WFInputXml;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.itextpdf.text.pdf.PdfReader;
//import com.lowagie.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.Image;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import ISPack.ISUtil.JPDBRecoverDocData;
import ISPack.ISUtil.JPISException;
import ISPack.ISUtil.JPISIsIndex;
import ISPack.*;

import com.newgen.generate.XMLParser;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import java.text.DateFormat;
import com.itextpdf.text.Paragraph;
import java.util.Date;


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
	
	public static String replaceGRTemplateArgs(File targetFile, String argumentString,String Docname,String passReq,String passVal,String ownerPwd) {
		HashMap<String,String> HtemData=new HashMap<String,String>();
		try {
			
			String sInputPath=System.getProperty("user.dir")+File.separator+"Templates"+File.separator+Docname+".pdf";
			String userPassword = passVal;
			String ownerPassword = ownerPwd;

			//String sOutputPath=System.getProperty("user.dir")+File.separator+"Output"+File.separator+"Output"+targetFile.getName();

			//copytemplatefile(sInputPath,targetFile.getAbsolutePath().substring(0, targetFile.getAbsolutePath().lastIndexOf('.')) + ".pdf");
			HtemData = parser(argumentString);
			
			String[] hm=Populatepdf.ProcesspdfFile(true,sInputPath,HtemData,targetFile.getAbsolutePath());
			logger.debug("Done");
			
			if("Y".equals(passReq)) {
				String logoPath = System.getProperty("user.dir")+File.separatorChar+"Templates"+File.separatorChar+"RAK_Logo.png";
				String outputPath = targetFile.getAbsolutePath();
				//logger.debug("value of outputPath: "+outputPath);
				
				File files[] = new File(outputPath).listFiles();
				/*for(int noFile=0;noFile<files.length;noFile++)
				{
					logger.debug("value of outputPath+File.separatorChar+files[0].getName()+\".pdf\" : "+outputPath +" : "+noFile +" : " +File.separatorChar+files[noFile].getName());
				}*/
				logger.debug("value of outputPath+File.separatorChar+files[0].getName()+\".pdf\": "+outputPath+File.separatorChar+files[0].getName()+".pdf");
				PdfReader reader = new PdfReader(outputPath+File.separatorChar+files[0].getName());
				PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPath+File.separatorChar+Docname+".pdf"));
				stamper.setEncryption(userPassword.getBytes(), ownerPassword.getBytes(),PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_256);
				/*com.itextpdf.text.pdf.PdfContentByte content = stamper.getOverContent(1);

				Image image = Image.getInstance(logoPath);
				// scale the image to 50px height
				image.scaleAbsoluteHeight(50);
				image.scaleAbsoluteWidth((image.getWidth() * 50) / image.getHeight());

				image.setAbsolutePosition(10, 750);

				content.addImage(image);*/
				stamper.close();
				reader.close();				
	//Need to verify this code
				File files1[] = new File(outputPath).listFiles();
				for(int i=0;i<files1.length;i++) {
					//logger.debug("value of files1[i].getName(): "+files1[i].getName());
					if(!files1[i].getName().contains("Welcome")) {
						files1[i].delete();
					}
				}
			}
			return "PDF Generated";
		}
		catch(Exception ex) {
			loggerErr.error("Exception in replaceGRTemplateArgs ():",ex);	
			return "PDF Generation Failed";
		}
		/**
		 * templateFile -> Original Template File targetFile -> Output File
		 * argumentString -> Argument String (with values and type)
		 *  Hook to replace the arguments of generate response template.
        Write custom code here to replace the arguments from the template and write the output in the target file. Finally,
        after writing into target file, client need to return true from the hook.
        If client returns true from the hook then system will not replace the arguments
        from the template.
		 */


		
	}	
	public static String generate_Password(File targetFile, String Docname,String passReq,String passVal,String ownerPwd) {
		try {
			String userPassword = passVal;
			String ownerPassword = ownerPwd;
			
			if("Y".equals(passReq)) {
				String logoPath = System.getProperty("user.dir")+File.separatorChar+"Templates"+File.separatorChar+"RAK_Logo.png";
				String outputPath = targetFile.getAbsolutePath();
				//logger.debug("value of outputPath: "+outputPath);
				
				File files[] = new File(outputPath).listFiles();
				/*for(int noFile=0;noFile<files.length;noFile++)
				{
					logger.debug("value of outputPath+File.separatorChar+files[0].getName()+\".pdf\" : "+outputPath +" : "+noFile +" : " +File.separatorChar+files[noFile].getName());
				}*/
				logger.debug("value of outputPath+File.separatorChar+files[0].getName()+\".pdf\": "+outputPath+File.separatorChar+files[0].getName()+".pdf");
				PdfReader reader = new PdfReader(outputPath+File.separatorChar+files[0].getName());
				PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPath+File.separatorChar+Docname+".pdf"));
				stamper.setEncryption(userPassword.getBytes(), ownerPassword.getBytes(),PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_256);
				stamper.close();
				reader.close();				
			}
			return "PDF Generated";
		}
		catch(Exception ex) {
			loggerErr.error("Exception in replaceGRTemplateArgs ():",ex);	
			return "PDF Generation Failed";
		}
		/**
		 * templateFile -> Original Template File targetFile -> Output File
		 * argumentString -> Argument String (with values and type)
		 *  Hook to replace the arguments of generate response template.
        Write custom code here to replace the arguments from the template and write the output in the target file. Finally,
        after writing into target file, client need to return true from the hook.
        If client returns true from the hook then system will not replace the arguments
        from the template.
		 */
	}
	
	   public boolean copytemplatefile(String inputfilepath, String targetFilePath){

		File inputfile =new File(inputfilepath);
	   if(inputfile.renameTo(new File(targetFilePath))){
		  logger.debug("copied----");
		return true;
	   }else{
		   logger.debug("Not copied");
		return false;
	   }
    }
	
	
public static HashMap<String,String> parser(String strArgList){
        HashMap<String,String> HtemData=new HashMap<String,String>();

        // String[] values = strArgList.split("&");
        //Changes done by shivang to handle & scenario in App form- Prod Bug
        strArgList=strArgList.replaceAll("&<", "splitstr_##8##;<");
        strArgList= strArgList.replaceAll(">&", ">splitstr_##8##;");
        String[] values = strArgList.split("splitstr_##8##;");
        for(int i=1;i<values.length;i++){
            if(i%2==0){
                //StringBuilder sb = new StringBuilder(values[i]);
               // sb.deleteCharAt(0);
               // values[i] = sb.toString();
				
				values[i] = values[i].replace("@10", "");
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
		
        return HtemData;
        
    }
	
	public static String generateHTML(File targetFile, String argumentString, String DocName, String wi_name, String cabinetname, String sessionId, String jtsIP, String serverPort)
	{
		try{
			if(DocName!=null && !DocName.equalsIgnoreCase("")){
				String sInputPath=System.getProperty("user.dir")+File.separator+"Templates"+File.separator+DocName+".HTML";
				logger.debug("sInputPath---" + sInputPath);
				String cssInputPath=System.getProperty("user.dir")+File.separator+"Templates"+File.separator+"formatting.css";
				String outputPath=targetFile+File.separator+DocName+".pdf";
				executepdfmethod(argumentString, sInputPath,outputPath,wi_name, cabinetname, sessionId, jtsIP,cssInputPath, serverPort);
			}
			else{
				logger.debug("Inside generateHTML: Document name received is incorret: ---" + DocName);
			}
		}
		catch(Exception e){
			logger.debug("Exception occured inside generateHTML---" + e.getMessage());
		}
		return "HTML generated";
	}
	
	 public static void executepdfmethod(String inputParams, String tempatePath,String outputPath, String wi_name, String cabinetname, String sessionId, String jtsIP ,String cssInputPath, String serverPort){
         
          try {
        	  
        	  logger.debug("Inside executepdfmethod : "+ inputParams);
              FileInputStream inputStream = new FileInputStream(tempatePath) ;   
              Document document = new Document(PageSize.LETTER);
              PdfWriter pdfWriter = PdfWriter.getInstance
              (document, new FileOutputStream(outputPath));
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
              String str=convertFileToString(tempatePath);
              logger.debug(" str to replace value ---" + str);
			  String cssfile = convertFileToString(cssInputPath);
              String prefix="&amp;&lt;";
              String postfix="&gt;&amp;";
				ByteArrayInputStream cis =
                	    new ByteArrayInputStream(cssfile.getBytes());
                Map<String,String> hm =parseArgumentString(inputParams);
			 
                // using for-each loop for iteration over Map.entrySet()
                for (Entry<String, String> entry : hm.entrySet()) {
                    if(str.contains(entry.getKey())){                    	
                    	str=str.replaceAll(prefix+entry.getKey()+postfix, entry.getValue());
                    }
                   
                }
				
               str = getDynamicGriddata( str, wi_name, cabinetname, sessionId, jtsIP, serverPort,hm);
			ByteArrayInputStream bis =
			new ByteArrayInputStream(str.getBytes());
			logger.debug("executepdfmethod str --- " + str);
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
			logger.debug("Exception occured in parseArgumentString" + e.getMessage());
		}
        return HtemData;
      
    }
	
	  @SuppressWarnings("resource")
	public static String convertFileToString(String tempatePath) throws FileNotFoundException
	{      
		  	String inputStreamString="";
			FileInputStream fis=null;
			try {
			fis = new FileInputStream(tempatePath);
			inputStreamString= new Scanner(fis,"UTF-8").useDelimiter("\\A").next();
			fis.close();
            } catch (IOException e) {
	        	loggerErr.error("IOException in convertFileToString ():",e);		
			}
            finally {
            	try {
            		if(null!=fis) {
            			fis.close();
            		}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					loggerErr.error("Exception occurred: ",e);
				}
            }
            return inputStreamString;
           
    }
	
	
	public static String attachDocument(String filetobeaddedpath,String workItemName,String DocName,String jtsIP, String jtsPort, String cabinetName,  String volumeID, String userName, String password, String sessionID, String serverPort)
	{
		try
		{
			String wiAddDocumentOutputXML="";
			String docIndex="";
			if(sessionID==null)
			{
				//sessionID=getSessionIDFirstTime(cabinetName,jtsIP, jtsPort,userName, password);
				return  "N";
			}
			
			if("".equalsIgnoreCase(filetobeaddedpath)||"".equalsIgnoreCase(workItemName)||"".equalsIgnoreCase(DocName))
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
				logger.debug("fpp exists : "+fppp.getPath());
			}
			else
			{
				logger.debug("fpp does not exists");
				DocAttach="N";
			}
			if(!"N".equalsIgnoreCase(DocAttach))
			{
				if(fppp.isFile())
				{
					logger.debug("fpp is file");
				}
				else
				{
					logger.debug("fpp is not file");
					DocAttach="N";
				}
			}
			if(!"N".equalsIgnoreCase(DocAttach))
			{
				logger.debug("Before AddDocument_MT Completion");
				try
				{
					logger.debug("aaaaaaaaa : "+jtsIP);
					logger.debug("aaaaaaaaa : "+jtsPort);
					logger.debug("aaaaaaaaa : "+cabinetName);
				
					if(jtsPort.startsWith("33"))
					{
						CPISDocumentTxn.AddDocument_MT(null,jtsIP,(short)(Integer.parseInt(jtsPort)),cabinetName,JPISDEC.m_sVolumeId, fppp.getPath(), JPISDEC, "",IsIndex);
					}	
					else
					{
						CPISDocumentTxn.AddDocument_MT(null,jtsIP,(short)(Integer.parseInt(jtsPort)),cabinetName,JPISDEC.m_sVolumeId, fppp.getPath(), JPISDEC, "","JNDI" ,IsIndex);
					}
					strDocISIndex = String.valueOf(IsIndex.m_nDocIndex) + "#" + String.valueOf(IsIndex.m_sVolumeId);
					logger.debug("Document added in SMS: strDocISIndex: "+ strDocISIndex);
					logger.debug("AddDocument_MT Completed successfully");
				}
				catch(Exception e)
				{
					logger.debug("Exception in CPISDocumentTxn");
					DocAttach="N";
				}
				catch(JPISException e)
				{
					logger.debug("Exception in CPISDocumentTxn : "+e);
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					logger.debug("Exception in CPISDocumentTxn 2 : "+sw);
					DocAttach="N";
				}
			}
			if(!"N".equalsIgnoreCase(DocAttach))
			{
				intISIndex=(int)IsIndex.m_nDocIndex;
				intVolumeId=(int)IsIndex.m_sVolumeId;

				String sQueryFoldIndex = "SELECT FOLDERINDEX FROM PDBFOLDER with(nolock) WHERE NAME ='" + workItemName + "'";
				String APSelectOutputFoldIndex="";
				sessionCheckInt=0;
				while(sessionCheckInt<loopCount)
				{
					String APSelectInputFoldIndex = ExecuteQuery_APSelect(sQueryFoldIndex,cabinetName,sessionID);					
					
					try
					{
						APSelectOutputFoldIndex=WFNGExecute(APSelectInputFoldIndex,jtsIP,serverPort,1);
					}
					catch(Exception e)
					{
						logger.debug("Exception in Execute : " + e);
						sessionCheckInt++;
						waiteloopExecute(waitLoop);
						
						continue;
					}
					
					sessionCheckInt++;
					if ("11".equalsIgnoreCase(XMLParser.getTagValue(APSelectOutputFoldIndex,"MainCode")))
					{
						
						logger.debug("Session id blank in sQueryFoldIndex call : " );
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
				if (!"0".equalsIgnoreCase(mainCodeAPSelectFoldIndex))
				{
					logger.debug("Problem in APSELECT mainCodeAPSelectFoldIndex");
					DocAttach="N";
				}
				if(!"N".equalsIgnoreCase(DocAttach))
				{
					String folderIndex=objXMLParser.getValueOf("td");
					logger.debug("folderIndex : "+folderIndex);
					String APSelectOutputDocSize="";
					sessionCheckInt=0;
					while(sessionCheckInt<loopCount)
					{
						String sQueryDocSize = "select DOCSIZE from isdoc with (nolock) WHERE docindex ='" + intISIndex + "' and volumeid = '"+volumeID+"' order by docsize desc";
						String APSelectInputDocSize = ExecuteQuery_APSelect(sQueryDocSize,cabinetName,sessionID);
						
						
						try
						{
							APSelectOutputDocSize=WFNGExecute(APSelectInputDocSize,jtsIP,serverPort,1);
						}
						catch(Exception e)
						{
							logger.debug("Exception in Execute : " + e);
							sessionCheckInt++;
							waiteloopExecute(waitLoop);
							
							continue;
						}					
						
						sessionCheckInt++;
						if ("11".equalsIgnoreCase(XMLParser.getTagValue(APSelectOutputDocSize,"MainCode")))
						{
							
							logger.debug("Session id blank in sQueryDocSize call : " );
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
					if (!"0".equalsIgnoreCase(mainCodeAPSelectDocSize))
					{
						logger.debug("Problem in APSELECT mainCodeAPSelectDocSize");					
						DocAttach="N";
					}
					if(!"N".equalsIgnoreCase(DocAttach))
					{
						String docSize="";
						
						docSize=objXMLParser.getValueOf("td");

						logger.debug("docSize : "+docSize);

						String strISIndex =String.valueOf(JPISDEC.m_nDocIndex)+ "#"+ String.valueOf(JPISDEC.m_sVolumeId)+ "#" ;
						logger.debug("strISIndex "+strISIndex);
						String intISIndexVolId = intISIndex.toString() +"#"+intVolumeId+"#";
						logger.debug("intISIndexVolId "+intISIndexVolId);
						
						sessionCheckInt=0;
						while(sessionCheckInt<loopCount)
						{
							String wiAddDocumentInputXML = get_NGOAddDocument_Input(cabinetName,sessionID,folderIndex,docSize,DocName,intISIndexVolId,volumeID, fppp.getPath());

							
							
							try
							{
								wiAddDocumentOutputXML = WFNGExecute(wiAddDocumentInputXML,jtsIP,serverPort,1);
							}
							catch(Exception e)
							{
								logger.debug("Exception in Execute : " + e);
								sessionCheckInt++;
								waiteloopExecute(waitLoop);
								
								continue;
							}					
							
							sessionCheckInt++;
							if ("11".equalsIgnoreCase(XMLParser.getTagValue(wiAddDocumentOutputXML,"MainCode")))
							{
								
								logger.debug("Session id blank in get_NGOAddDocument_Input call : " );
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
						if (!"0".equalsIgnoreCase(mainCodeforCreateWI))
						{
							DocAttach="N";						
						}
						else
						{
							docIndex=objXMLParser.getValueOf("DocumentIndex");
							DocAttach="Y";
							DocAttach = DocAttach+"~"+wiAddDocumentOutputXML+"~"+docIndex+"~"+strDocISIndex;						 
							
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
	
	public static String attachMail(String cabinetName,String sessionId,String mailFrom,String strDocISIndex,String DocName, String wi_name, String jtsIP,String serverPort,String userMail)
	{
		String mailStatus="";
		String mailSubject = "";
		String mailMessage = "";
		String outputXmlMail ="";
		String mailTo=userMail;
		String attachmentName = DocName+".pdf";
		
		XMLParser objXMLParser = new XMLParser();
		
		//logger.debug("SendMail strDocISIndex -- "+ strDocISIndex);
		
		mailSubject = "Welcome Letter Generated for Application NO. - "+wi_name;
		mailMessage = "Dear Concern,\n\n This is the welcome letter for Application No. "+wi_name+".\nThis is system generated mail. Kindly do not reply to it.\n\nRegards,\nSystemAdmin \n\n\n\n";
		String inputXmlMail = sendMail(cabinetName,sessionId,mailFrom,mailTo,mailSubject,mailMessage,strDocISIndex,attachmentName);
		
		//logger.debug("inputXmlMail -- "+ inputXmlMail);
		
		try
		{
			outputXmlMail = WFNGExecute(inputXmlMail,jtsIP,serverPort,1);
			objXMLParser.setInputXML(outputXmlMail);
			String mainCodeSendMail = "";
			mainCodeSendMail=objXMLParser.getValueOf("MainCode");
			//logger.debug("SendMail maincode -- "+ mainCodeSendMail);
			if (!"0".equalsIgnoreCase(mainCodeSendMail))
			{
				logger.debug("Problem in sendMail mainCodeSendMail");
			}
			else
			{
				logger.debug("Mail send successfully");
				mailStatus="Y";
			}
			
			return mailStatus	;
			
		}
		catch(Exception e)
		{
			logger.debug("Exception in Execute : " + e);
		}	
		return mailStatus;
	}
	
	public static String sendMail(String cabinetname,String sessionID,String mailFrom, String mailTo, String mailSubject,String mailMessage,String attachmentIndex, String attachmentName)
	  {

		  if(attachmentIndex==null)

			  attachmentIndex="";

		  if(attachmentName==null)

			  attachmentName="";



		  WFInputXml wfInputXml = new WFInputXml();



		  wfInputXml.appendStartCallName("WFAddToMailQueue", "Input");

		  wfInputXml.appendTagAndValue("EngineName",cabinetname);

		  wfInputXml.appendTagAndValue("SessionId",sessionID);

		  wfInputXml.appendTagAndValue("MailFrom",mailFrom);

		  wfInputXml.appendTagAndValue("MailTo",mailTo);

		  wfInputXml.appendTagAndValue("MailCC","");

		  wfInputXml.appendTagAndValue("MailSubject",mailSubject);

		  wfInputXml.appendTagAndValue("MailMessage",mailMessage);

		  wfInputXml.appendTagAndValue("AttachmentISIndex",attachmentIndex);

		  wfInputXml.appendTagAndValue("AttachmentNames",attachmentName);

		  wfInputXml.appendTagAndValue("AttachmentExts","");

		  wfInputXml.appendEndCallName("WFAddToMailQueue","Input");



		  return wfInputXml.toString();                    

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
		String noOfPage = "32";
		if(DocumentName.contains("CAM_REPORT")){
			noOfPage = "3";
		}
		String xml = "<?xml version=\"1.0\"?>" + "\n" +
		"<NGOAddDocument_Input>" + "\n" +
		"<Option>NGOAddDocument</Option>" + "\n" +
		"<CabinetName>" + cabinetName + "</CabinetName>" + "\n" +
		"<UserDBId>" + sessionID + "</UserDBId>" + "\n" +
		"<GroupIndex>0</GroupIndex>" + "\n" +
		"<ParentFolderIndex>" + folderIndex + "</ParentFolderIndex>" + "\n" +
		"<DocumentName>" + DocumentName + "</DocumentName>" + "\n" +
		"<CreatedByAppName>pdf</CreatedByAppName>" + "\n" +
		"<Comment></Comment>" + "\n" +
		"<VersionComment></VersionComment>" + "\n" +
		"<VolumeIndex>" + volumeID + "</VolumeIndex>" + "\n" +
		"<FilePath>" + filePath + "</FilePath>" + "\n" +
		"<DataDefinition></DataDefinition>" + "\n" +
		"<ISIndex>" + strISIndex + "</ISIndex>" + "\n" +
		"<NoOfPages>"+noOfPage+"</NoOfPages>" + "\n" +
		"<DocumentType>N</DocumentType>" + "\n" +
		"<DocumentSize>" + docSize + "</DocumentSize>" + "\n" +
		"</NGOAddDocument_Input>";
		   
		return xml;   
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
			logger.info("Exception Occured in WF NG Execute : "+e.getMessage());	
			return "Error";
		}
		
	}
	
	
	public static String getDynamicGriddata(String htmlString,String wi_name,String cabinetname,String sesionId,String jtsIP, String serverPort, Map<String, String> hm){
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
				logger.info("getDynamicGriddata() tableId:"+tableId);
				for(Element column:headerRowTr.getAllElements()) {
					if(column.hasAttr("columnName")) {
						columns+=column.attr("columnName")+",";
					}
				}
				if(columns.endsWith(",")) {
					columns = columns.substring(0,columns.length()-1);
				}
				logger.info("getDynamicGriddata() columns:"+columns);
				//new cosde by saurabh to handle efms alert status is deviations grid. on 7th Jan
				if("deviation_det".equals(tableId)) {
					if(wi_name.contains("CC-")||wi_name.contains("CreditCard-")||wi_name.contains("CDOB-")) {
						//query = "select "+columns+" from "+table.attr("dbTablename")+ " with (nolock) where wi_name='"+wi_name+"' union all select isnull(replace(Manual_Deviation_Reason,'#',','),'NA'),'Manual Deviation' from ng_rlos_decisionHistory where wi_name='"+wi_name+"' union all select case when EFMS_AlertStatusFlag is null or EFMS_AlertStatusFlag='NULL' or EFMS_AlertStatusFlag='' then (case when EFMS_IS_Alerted is null or EFMS_IS_Alerted='NULL' or EFMS_IS_Alerted='' then 'Not Processed' else EFMS_IS_Alerted end) else EFMS_AlertStatusFlag end as 'status','EFMS Status' as AlertStatus from NG_CC_EXTTABLE with(nolock) where CC_Wi_Name = '"+wi_name+"'";
						query = "select "+columns+" from "+table.attr("dbTablename")+ " with (nolock) where wi_name='"+wi_name+"' union all select isnull(replace(Manual_Deviation_Reason,'#',','),''),'Manual Deviation' from ng_rlos_decisionHistory where wi_name='"+wi_name+"' and Manual_Deviation_Reason is not null and Manual_Deviation_Reason not in ('NO_CHANGE')";//Reverted changes of PCASP-2381
					}
					else if(wi_name.contains("PL-")) {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " with (nolock) where wi_name='"+wi_name+"' union all select isnull(replace(Manual_Deviation_Reason,'#',','),''),'Manual Deviation' from ng_rlos_decisionHistory where wi_name='"+wi_name+"' and Manual_Deviation_Reason is not null  and Manual_Deviation_Reason not in ('[object Window]')";
					}
					else{
						query = "select "+columns+" from "+table.attr("dbTablename")+ " with (nolock) where wi_name='"+wi_name+"'";
					}
				}
				else if("deviation_det_dcc".equals(tableId)){
				
					query = "select "+columns+" from "+table.attr("dbTablename")+ " where wi_name='"+wi_name+"'";
					logger.info("query deep :"+ query);
					
				}
				
				else if("smart_check".equals(tableId)){
					if(table.attr("winamecol")!="") {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where "+table.attr("winamecol")+"='"+wi_name+"'";
					}
					else {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where wi_name='"+wi_name+"'";
					}
				}
				else if("cc_disb".equals(tableId)){
					if(table.attr("winamecol")!="") {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where "+table.attr("winamecol")+"='"+wi_name+"'";
						logger.info("testing cc disb");
					}
					else {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where wi_name='"+wi_name+"'";
						logger.info("testing cc disb1");
					}
				}
				else if("cc_disb_2".equals(tableId)){
					if(table.attr("winamecol")!="") {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where "+table.attr("winamecol")+"='"+wi_name+"'";
						logger.info("testing cc disb");
					}
					else {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where wi_name='"+wi_name+"'";
						logger.info("testing cc disb1");
					}
				}
				else if("last_loan".equals(tableId)){
					if(table.attr("winamecol")!="") {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where "+table.attr("winamecol")+"='"+wi_name+"'";
						logger.info("testing last loan disb");
					}
					else {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where wi_name='"+wi_name+"'";
						logger.info("testing else last loan disb");
					}
					
				}
				else if("loan_rej".equals(tableId)){
					if(table.attr("winamecol")!="") {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where "+table.attr("winamecol")+"='"+wi_name+"'";
					}
					else {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where wi_name='"+wi_name+"'";
					}
					
				}
				else if("comp_mis".equals(tableId)){
					if(table.attr("winamecol")!="") {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where "+table.attr("winamecol")+"='"+wi_name+"'";
					}
					else {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where wi_name='"+wi_name+"'";
					}
					
				}
				
				else if("efms_det".equals(tableId))
				{
					try
					{
						String App_No=wi_name.substring(wi_name.indexOf("-")+1,wi_name.lastIndexOf("-"));
						query ="(select 'Initial status 'as descrip ,APPLICATION_STATUS from (select top 1 APPLICATION_STATUS from NG_EFMS_RESPONSE where APPLICATION_NUMBER='"+App_No+"' order by  APPLICATION_STATUS) as EFMS_APPLICATION_STATUS "+
								" union all " 
								+"select 'Final Status ' as decrip,CASE_STATUS from (select top 1 case when CASE_STATUS='Confirmed Fraud' then 'Negative case' when CASE_STATUS='Closed' then 'False Positive' else CASE_STATUS end as CASE_STATUS from NG_EFMS_RESPONSE where APPLICATION_NUMBER='"+App_No+"' order by  CASE_STATUS desc) as EFMS_CASE_STATUS)";
						/*String CountQuery="SELECT count(*) as count FROM NG_EFMS_RESPONSE WHERE APPLICATION_NUMBER='"+App_No+"'";
						String countInputXml=ExecuteQuery_APSelectWithColumnNames(CountQuery,cabinetname,sesionId);
						String countOutputXml= WFNGExecute(countInputXml,jtsIP,serverPort,1);
						if(Integer.parseInt(XMLParser.getTagValue(countOutputXml,"count"))>1)
						{
							query = "(select * from (select  top 1 'Initial status (Post DDVT)'as descrip,APPLICATION_STATUS from NG_EFMS_RESPONSe where APPLICATION_NUMBER='"+App_No+"' order by SNO) as a union ALL select * from (select top 1 'Final Status (Post DDVT)' as decrip,CASE_STATUS from NG_EFMS_RESPONSe where APPLICATION_NUMBER='"+App_No+"' order by SNO) as b) union all(select * from (select  top 1 'Initial status (Post CAD)' as decrip,APPLICATION_STATUS from NG_EFMS_RESPONSe where APPLICATION_NUMBER='"+App_No+"' order by SNO desc) as c union all select * from (select top 1 'Final Status (Post CAD)' as descrip,CASE_STATUS from NG_EFMS_RESPONSe where APPLICATION_NUMBER='"+App_No+"' order by SNO desc) as d)";
						}
						else
						{
							query="(select * from (select  top 1 'Initial status (Post DDVT)'as descrip,APPLICATION_STATUS from NG_EFMS_RESPONSe where APPLICATION_NUMBER='"+App_No+"' order by SNO) as a union ALL select * from (select top 1 'Final Status (Post DDVT)' as decrip,CASE_STATUS from NG_EFMS_RESPONSe where APPLICATION_NUMBER='"+App_No+"' order by SNO) as b) union all(select * from (select  top 1 'Initial status (Post CAD)' as decrip,'' as CASE_STATUS from NG_EFMS_RESPONSe where APPLICATION_NUMBER='"+App_No+"' order by SNO desc) as c union all select * from (select top 1 'Final Status (Post CAD)' as descrip,'' AS CASE_STATUS from NG_EFMS_RESPONSe where APPLICATION_NUMBER='"+App_No+"' order by SNO desc) as d)";
						}*/
					}
					catch (Exception e) {
						// TODO: handle exception
					}
				}
				else if("auth_sig".equals(tableId)){
					if(wi_name.contains("CC-")||wi_name.contains("CreditCard-")) {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where wi_name='"+wi_name+"' and (AuthSignCIFNo=(select CIF_ID from ng_RLOS_Customer where wi_name='"+wi_name+"' and DesignationAsPerVisa='--Select--') or AuthSignCIFNo!=(select CIF_ID from ng_RLOS_Customer where wi_name='"+wi_name+"'))";
					}
					else {
						query = "select "+columns+" from "+table.attr("dbTablename")+ " where wi_name='"+wi_name+"'";
					}
					logger.info("Age from Map:"+hm.get("Age"));
					query = query.replace("AuthSignDOB",hm.get("Age")+" as 'AuthSignDOB'");
				}
				else {
				query = "select "+columns+" from "+table.attr("dbTablename")+ " with (nolock) where wi_name='"+wi_name+"'";
				}
				logger.info("changes not ref");
				//query = "select "+columns+" from "+table.attr("dbTablename")+ " with (nolock) where wi_name='"+wi_name+"'";
				logger.info("getDynamicGriddata() query:"+query);
				tbodyToQuery.put(tableId+"_body", query);
			}
		}	
		for(Map.Entry<String,String> entry : tbodyToQuery.entrySet()) {
			String queryToexxecute = entry.getValue();
			logger.info("getDynamicGriddata() KeyValue:"+entry.getKey());
			logger.info("Deepak Query to execute: "+queryToexxecute);
			String inputXml = ExecuteQuery_APSelectWithColumnNames(queryToexxecute,cabinetname,sesionId);
			logger.info("Deepak input xml Query: "+inputXml);
			String opXml=null;
			List<List<String>> results;
			try {
				opXml = WFNGExecute(inputXml,jtsIP,serverPort,1);
				logger.info("getDynamicGriddata() opXml:"+opXml);
				opXml=opXml.replaceAll(">\\s+<", "><");

				results = XMLParser.getTagValueCAM(opXml);
				logger.info("getDynamicGriddata() results:"+results);
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
				
				String Subproduct = hm.containsKey("subproduct")?hm.get("subproduct"):"";
			
				logger.info("Deepak Changes for IM product:"+Subproduct);
				if(wi_name.contains("PL-") ||("Instant Money".equalsIgnoreCase(Subproduct))){
					if (entry.getKey().contains("exposure_det")){
						//get cad2 approval date
						String Cad_desc="";
						if(hm.containsKey("CAD_Decision_app")){
							logger.info("Deepak decision: "+hm.get("CAD_Decision_app"));
							Cad_desc = hm.get("CAD_Decision_app");
						}
						else if("Instant Money".equalsIgnoreCase(Subproduct)){
							logger.info("Deepak inside Instant Money decision: ");
							logger.info("Deepak inside Instant Money decision: "+hm.get("CAD1_Decision"));
							Cad_desc= hm.containsKey("CAD1_Decision")?hm.get("CAD1_Decision"):"";
						}
						String cad2ApprovalDate = "";
						//queryToexxecute = "select top 1 dateLastChanged as approvalDate from NG_RLOS_GR_DECISION where workstepname in ('Cad_Analyst1', 'CAD_Analyst1')  and Decision='Approve' and dec_wi_name" +" = '"+wi_name+"'  order by dateLastChanged desc";
						if("Approve".equalsIgnoreCase(Cad_desc)){
							if("Instant Money".equalsIgnoreCase(Subproduct)){
								logger.info("Deepak inside Instant Money decision for approval date: ");
								queryToexxecute = "select format(GETDATE(),'dd-MM-yyyy') as approvalDate";
								logger.info("Deepak inside Instant Money decision for approval date: "+queryToexxecute);
							}
							else{
								queryToexxecute = "select format(GETDATE(),'dd-MM-yyyy hh:mm') as approvalDate";
							}
							
							String inputXmlcad2Date = ExecuteQuery_APSelectWithColumnNames(queryToexxecute,cabinetname,sesionId);
							String opXmlcurrentcad2Date=null;
							List<List<String>> resultscurrentcad2Date;
							opXmlcurrentcad2Date = WFNGExecute(inputXmlcad2Date,jtsIP,serverPort,1);
							logger.info("opXmlcurrentcad2Date"+opXmlcurrentcad2Date);
							opXmlcurrentcad2Date=opXmlcurrentcad2Date.replaceAll(">\\s+<", "><");
							resultscurrentcad2Date = XMLParser.getTagValueCAM(opXmlcurrentcad2Date);
							logger.info("resultscurrentcad2Date"+resultscurrentcad2Date);
							if(resultscurrentcad2Date!=null && resultscurrentcad2Date.size()>0) {
								for(List<String> row: resultscurrentcad2Date) {
									logger.info("row at 881"+row);
									cad2ApprovalDate = row.get(0);
								}
							}

						}	

						//cad2 approval date ends

						//code to add current pl into exposure
						queryToexxecute = "select isnull(loan_amount,'') as loan_amount, isnull(loanemi,'') as loan_emi from NG_RLOS_LoanDetails where winame = '"+wi_name+"'";
						String inputXmlcurrentExposure = ExecuteQuery_APSelectWithColumnNames(queryToexxecute,cabinetname,sesionId);
						String opXmlcurrentExposure=null;
						List<List<String>> resultscurrentExposure;
						opXmlcurrentExposure = WFNGExecute(inputXmlcurrentExposure,jtsIP,serverPort,1);
						logger.info("getDynamicGriddata() opXmlcurrentExposure:"+opXmlcurrentExposure);
						opXmlcurrentExposure=opXmlcurrentExposure.replaceAll(">\\s+<", "><");
						String EMI = XMLParser.getTagValue(opXmlcurrentExposure, "loan_emi");
						String loanAmt = XMLParser.getTagValue(opXmlcurrentExposure, "loan_amount");
						resultscurrentExposure = XMLParser.getTagValueCAM(opXmlcurrentExposure);
						//logger.info("getDynamicGriddata() results:"+results);
						//Element tbodycurrentExposure = doc.getElementById(entry.getKey());
						//String appendHtml="";
						logger.info("resultscurrentExposure"+resultscurrentExposure);
						if(resultscurrentExposure!=null && resultscurrentExposure.size()>0) {
							appendHtml+="<tr>";
							if("Instant Money".equalsIgnoreCase(Subproduct)){
								appendHtml+= "<td class=\"tdprops gridTd\"><p>Credit Card-Instant Money</p></td>";
							}
							else{
								appendHtml+= "<td class=\"tdprops gridTd\"><p>Personal Loans</p></td>";
							}
							
							appendHtml+= "<td class=\"tdprops gridTd\"><p>Individual_CIF</p></td>";
							appendHtml+= "<td class=\"tdprops gridTd\"><p>"+wi_name+"</p></td>";
							//appendHtml+= "<td class=\"tdprops gridTd\"><p>Individual_CIF</p></td>";
							for(List<String> row: resultscurrentExposure) {
								for(String element: row) {
									queryToexxecute = "select isnull(EMI,'') as EMI,Final_Limit as amount from ng_rlos_EligAndProdInfo where wi_name = '"+wi_name+"'";
									String inputXmlEMI = ExecuteQuery_APSelectWithColumnNames(queryToexxecute,cabinetname,sesionId);
									String opXmlcurrentEMI=null;
									opXmlcurrentEMI = WFNGExecute(inputXmlEMI,jtsIP,serverPort,1);
									logger.info("getDynamicGriddata() opXmlcurrentExposure:"+opXmlcurrentEMI);
									opXmlcurrentEMI=opXmlcurrentEMI.replaceAll(">\\s+<", "><");
									String EMI_IM = XMLParser.getTagValue(opXmlcurrentEMI, "EMI");
									String amount = XMLParser.getTagValue(opXmlcurrentEMI, "amount");
									if("Instant Money".equalsIgnoreCase(Subproduct) && (element.isEmpty() || element == null)){
										appendHtml+="<td class=\"tdprops gridTd\"><p>"+EMI_IM+"</p></td>";
									}else if("Instant Money".equalsIgnoreCase(Subproduct)){
										appendHtml+="<td class=\"tdprops gridTd\"><p>"+amount+"</p></td>";
									}else{
										appendHtml+="<td class=\"tdprops gridTd\"><p>"+element+"</p></td>";
									}
								}

								//Deepak Commented on 24 OCT for PPCT 13
								/*
								logger.info("resultscurrentExposure row "+row);
								logger.info("resultscurrentExposure length "+row.size());
								for(String element: row) {
									logger.info("element  "+element);
									if("Instant Money".equalsIgnoreCase(Subproduct) && (element.isEmpty() || element == null)){
										BigDecimal bd = new BigDecimal(0.030);
										bd = bd.setScale(3, RoundingMode.HALF_UP);
										bd = bd.multiply(BigDecimal.valueOf(Double.valueOf(loanAmt)));
										bd = bd.setScale(3, RoundingMode.HALF_UP);
										logger.info("Inside if EMI value  "+bd);
										appendHtml+="<td class=\"tdprops gridTd\"><p>"+bd+"</p></td>";
									}else{
										appendHtml+="<td class=\"tdprops gridTd\"><p>"+element+"</p></td>";
									}
								}*/
							}
							

							appendHtml+= "<td class=\"tdprops gridTd\"><p>true</p></td>";
							appendHtml+= "<td class=\"tdprops gridTd\"><p>"+cad2ApprovalDate+"</p></td>";
							appendHtml+= "<td class=\"tdprops gridTd\"><p>False</p></td>";
							appendHtml+="</tr>";
							logger.info("appendHtml for PL"+appendHtml);
						}	
						//code to add current pl into exposure

						//code to add current bundled cc into exposure
						queryToexxecute = "select isnull(a.efchidden,'') as cc_limit, isnull(b.is_cc_waiver_require,'') as is_cc_waiver_require from ng_rlos_EligAndProdInfo  a join" 
								+" NG_PL_EXTTABLE b on a.wi_name=b.PL_wi_name where b.PL_wi_name  = '"+wi_name+"'";
						
						String inputXmlcurrentExposureCC = ExecuteQuery_APSelectWithColumnNames(queryToexxecute,cabinetname,sesionId);
						String opXmlcurrentExposureCC=null;
						List<List<String>> resultscurrentExposureCC;
						opXmlcurrentExposureCC = WFNGExecute(inputXmlcurrentExposureCC,jtsIP,serverPort,1);
						logger.info("opXmlcurrentExposureCC"+opXmlcurrentExposureCC);
						opXmlcurrentExposureCC=opXmlcurrentExposureCC.replaceAll(">\\s+<", "><");
						resultscurrentExposureCC = XMLParser.getTagValueCAM(opXmlcurrentExposureCC);
						logger.info("resultscurrentExposureCC"+resultscurrentExposureCC);
						if(resultscurrentExposureCC!=null && resultscurrentExposureCC.size()>0) {
							String addtionalLim = "",isCCWaiverReq= "";
							for(List<String> row: resultscurrentExposureCC) {
								logger.info("resultscurrentExposureCC row "+row);
								addtionalLim = row.get(0);
								isCCWaiverReq = row.get(1);
							}
							if(isCCWaiverReq.equalsIgnoreCase("N")){
								BigDecimal bd = new BigDecimal(0.030);
								bd = bd.setScale(3, RoundingMode.HALF_UP);
								bd = bd.multiply(BigDecimal.valueOf(Double.valueOf(addtionalLim)));
								bd = bd.setScale(3, RoundingMode.HALF_UP);
								appendHtml+="<tr>";
								appendHtml+= "<td class=\"tdprops gridTd\"><p>Bundle Credit Card</p></td>";
								appendHtml+= "<td class=\"tdprops gridTd\"><p>Individual_CIF</p></td>";
								appendHtml+= "<td class=\"tdprops gridTd\"><p>"+wi_name+"</p></td>";
								appendHtml+="<td class=\"tdprops gridTd\"><p>"+addtionalLim+"</p></td>";
								appendHtml+="<td class=\"tdprops gridTd\"><p>"+bd+"</p></td>";
								appendHtml+= "<td class=\"tdprops gridTd\"><p>true</p></td>";
								appendHtml+= "<td class=\"tdprops gridTd\"><p>"+cad2ApprovalDate+"</p></td>";
								appendHtml+= "<td class=\"tdprops gridTd\"><p>False</p></td>";
								appendHtml+="</tr>";
							}
							logger.info("appendHtml 934"+appendHtml);
						}		
						//code to add current bundled cc into exposure

					}
				}
				logger.info("getDynamicGriddata() appendHtml to be put:"+appendHtml);
				tbody.append(appendHtml);
				//tbody.append("<img src=\""+logoPath+"\" />");
				appendHtml="";
			} catch (Exception e) {

				loggerErr.error("Exception occurred: ",e);
				//e.printStackTrace();
			}

		}
		
		
		/*Element td = doc.getElementById("rak_logo_img");
		String img = "<img src=\""+logoPath+"\" />";
		td.append(img);*/
		formattedHtmlString = doc.html();
		
		return formattedHtmlString;
	}
	
	 public static String createPdf_withPass(String pdfPath,String str,String flag,String Password)
		{
			File sfile = null;
	        try 
			{
	        	logger.info("Inside GeneratePDF file to create pdf....");
	        	logger.info("accNo :"+Password);
				byte[] UserPassword= Password.getBytes();    
			 	byte[] OwnerPassword = "Admin".getBytes();
			 	logger.info("pdfPath :"+pdfPath);
				sfile = new File(pdfPath);
				OutputStream file = new FileOutputStream(sfile);
				Document document = new Document();   
				PdfWriter EncryptPDF= PdfWriter.getInstance(document, file);
				
				logger.info("flag ::"+flag);
				if(Password!=null && !Password.equals("") && flag.equalsIgnoreCase("Y"))
				{
					logger.info("Inside encrypt pdf");
					EncryptPDF.setEncryption(UserPassword, OwnerPassword,PdfWriter.ALLOW_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);
				}	
				logger.info("11");
				document.open();          
				document.add(new Paragraph(str)); 
				logger.info("12");
				Date nowdate = new Date();
				DateFormat	fullDf = DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL);
				String msgdate=fullDf.format(nowdate);			
				logger.info("Date for sending mail in mail message:"+msgdate);			
				document.add(new Paragraph(msgdate)); 
				document.close();        
				file.close();
			}
			catch (Exception e) 
			{
				logger.info("Exception while creating pdf");
				final Writer result = new StringWriter();
				final PrintWriter printWriter = new PrintWriter(result);
				e.printStackTrace(printWriter);
				logger.info("error while creating pdf : "+result.toString());
			}
			return sfile.getPath();
			
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
           logger.debug(e.toString());
		   Thread.currentThread().interrupt();
        }
    }
	
	

}   /*  End of the TestXIRR class. */