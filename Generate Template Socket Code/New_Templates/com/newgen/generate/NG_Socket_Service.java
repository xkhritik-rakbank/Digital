
package com.newgen.generate;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.util.HashMap;
import java.util.Map;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;

public class NG_Socket_Service
{
 private static int port, maxConnections=10;
 public static String RunMode, s, err;
 private static Logger logger = Logger.getLogger("consoleLogger");

 static
    {
		 PropertyConfigurator.configure(System.getProperty("user.dir")+File.separator+"Config"+File.separator+"log4j_WebServiceWrapper.properties");
		 
		// **********Read the attributes from NG_SERVICE.xml****************
      
	}
	
 public static void main(String[] args) 
 {
	 try{
	Properties propConfig = new Properties(); 
	 propConfig.load(new FileInputStream(System.getProperty("user.dir")+File.separator+"Config"+File.separator+"Config.properties"));
	 Set<Object> keys = propConfig.keySet();
	 for(Object k:keys)
		{
				//configPropertyMap.put((String)k,propConfig.getProperty((String)k));
				if("Utility_port".equals((String)k)){
					port= Integer.parseInt(propConfig.getProperty((String)k));
					break;
				}
		}
	}catch(Exception ex){
	ex.printStackTrace();
	}
    int k=0;
	ServerSocket listener=null;
	Socket server=null;
	//******* Establish connection *************
    try
	{
      listener = new ServerSocket(port);      
		System.out.println(System.getProperty("user.dir"));
		while(true)
		{
			k=k+1;
	        //doComms connection;
			logger.info("Waiting for Request Count--" + k);
			System.out.println("Waiting for Request Count--" + k);
	        server = listener.accept();
	        doComms conn_c= new doComms(server);
	        Thread t = new Thread(conn_c);
	        t.start();
		}
    } 
	catch (IOException ioe) 
	{
	  logger.info("IOException on socket listen:11111111 " + ioe);
      System.out.println("Catch  3 IOException on socket listen:11111111 " + ioe);
      ioe.printStackTrace();	 
    }
	finally
	{
		try
		{
			if (listener!=null)
			{
				listener.close();
				listener=null;
				System.out.println("Closing Listener");
				logger.info("Closing Listener");
			}
			if (server!=null)
			{
				server.close();
				server=null;
				System.out.println("Closing Server Socket");
				logger.info("Closing Server Socket");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.info("Exception " + e.toString());
			System.out.println("Exception " + e.toString());
		}
	}
  }

}

//--------------------------------------------------------------------------------------------------
//********** Communicating Class I/O *****************
class doComms implements Runnable
{
    private Socket server;
    private String input;
	private static Logger loggern = Logger.getLogger("consoleLoggerdocomms");
	Properties property = new Properties();
	static String XMLFileName 	 = null;
	static String XSDFileName 	 = null;
	static String XSDFileNameArray[]	= null;
	static int flag=0;
	NG_Socket_Service N= new NG_Socket_Service();

	static String docIndex= null;
	 static String strPropertyPath;
	 static Properties propConfig;
	 static String cabinetName;
	 static String jtsPort;
	 static String jtsIP;
	 static String volId;
	 static String sessionID;
	 static String userName;
	 static String password;
 
 static Map <String,String> configPropertyMap = new HashMap<String, String>();
 

	//DESedeEncryption D= new DESedeEncryption();

    doComms(Socket server) 
	{
      this.server=server;
    }
	
	static
    {	 		 
		// **********Read the attributes from NG_SERVICE.xml****************
		
	}
	
	
	public void readPropertyFile()
	{
		FileInputStream proConfigFileStream;
       try 
		{
			
			strPropertyPath=System.getProperty("user.dir")+File.separator+"Config"+File.separator+"Config.properties";
			proConfigFileStream = new FileInputStream(strPropertyPath);
			propConfig = new Properties();
			propConfig.load(proConfigFileStream);
			
			configPropertyMap.clear();
			Set<Object> keys = propConfig.keySet();
			
			for(Object k:keys)
			{
				configPropertyMap.put((String)k,propConfig.getProperty((String)k));
			}
			
			strPropertyPath=null;proConfigFileStream=null;propConfig=null;keys=null;
			
			jtsIP = configPropertyMap.get("JTSIP");
			jtsPort = configPropertyMap.get("JTSPORT");
			cabinetName = configPropertyMap.get("CabinetName");
			userName = configPropertyMap.get("UserName");
			password = configPropertyMap.get("Password");
			volId = configPropertyMap.get("VolumeId");
			
//			userPwd=new User("main");
//			sUserName=userPwd.getUsername();
//			sPassword=userPwd.getPassword();
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("Config.properties not found. "+e);
		}
		catch (IOException e) 
		{
			System.out.println("Config.properties Load/Read Failed with IOException. "+e);
		}
	}
	public void writeData(DataOutputStream xmlDataOutputStream,String input) throws IOException
    {	
		 try {
			
			DataOutputStream dout=new DataOutputStream(server.getOutputStream());  
			
			String return_message = input;
			
			 if (return_message != null && return_message.length() > 0) {
				// synchronized (socket) 
				 {
				 //UTF-16LE
					
					dout.write(return_message.getBytes("UTF-16LE"));
					dout.flush();
				 }	
			 };
			 
		 } catch (IOException i) {
			 i.printStackTrace();	
		 } catch (Exception ie) {
			 ie.printStackTrace();
		 }	
         
    }

	public String readData(DataInputStream xmlDataInputStream) throws IOException
    {
       
		String recvedMessage="";
		try {
			
            byte[] readBuffer = new byte[20000];
            int num = xmlDataInputStream.read(readBuffer);
            if (num > 0) {
                byte[] arrayBytes = new byte[num];
                System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
                recvedMessage = new String(arrayBytes, "UTF-16LE");
            } 
			else {

                notify();
            }
            ;
            
        } catch (SocketException se) {
            System.exit(0);

        } catch (IOException i) {	
            i.printStackTrace();
        }
		 return recvedMessage;
    }
 
	//******* Get a particaular tag value from input xml *****************
	public String getvaluebytag(String xml,String TagName)
	{
		//System.out.println("xml= "+xml);
		//System.out.println("TagName"+TagName);
		String retval="";
        try 
		{
			retval=(xml.substring(
				xml.indexOf("<"+toUpperCase(TagName, 0, 0) +">") +TagName.length() + 2,
				xml.indexOf("</"+toUpperCase(TagName, 0, 0) +">")));
        } 
		catch (StringIndexOutOfBoundsException ex) 
		{
			ex.printStackTrace();
			System.out.println("Exception " + ex.toString());
			N.err=ex.toString();
            return "";
        }
		return retval;
	}
	
	//********** Convert string to upper case ****************
	public String toUpperCase(String str, int begin, int end) throws StringIndexOutOfBoundsException 
	{
        String returnStr = "";
        try 
		{
            int count = str.length();
            char strChar[] = new char[count];
            str.getChars(0, count, strChar, 0);
            while (count-- > 0) 
			{
                strChar[count] = Character.toUpperCase(strChar[count]);
            }
            returnStr = new String(strChar);
        } 
		catch (ArrayIndexOutOfBoundsException e) 
		{
			e.printStackTrace();
			System.out.println("Exception " + e.toString());
			N.err=e.toString();
			loggern.info("Exception " + e.toString());
		}
        return returnStr;
    }
	
	//*********** Read a file (batch mode) ***************	
	public String readConfig(String fileName) 
      	{
                  String str = "";
                  try 
                  {
					loggern.info("XML FileName and Path Read = "+fileName);
                	System.out.println("XML FileName and Path Read = "+fileName);
                    FileReader fr = new FileReader(fileName);
                    BufferedReader br = new BufferedReader(fr);
                    String Record = "";
                    while ( (Record = br.readLine()) != null) 
                    {
                      str = str + Record;
                    }
                    System.out.println("String Converted from XML = "+str);
					loggern.info("String Converted from XML = "+str);
                    fr.close();
                  }

                  catch (FileNotFoundException e) 
                  {
                	  e.printStackTrace();
                  	System.out.println("File not found exception in Read Function");
					loggern.info("File not found exception in Read Function");
					N.err=e.toString();
                  }

                  catch (Exception e) 
                  {
                	  e.printStackTrace();
					loggern.info("Exception in Read Function = "+e);
                    System.out.println("Exception in Read Function = "+e);
					N.err=e.toString();
                  }

                 return str;

      	}
		
	//*************** Validate XML **************	
	public void XmlValidator()
	{
		
							
			//XML file name that needs to be validated.
			XMLFileName = getvaluebytag(input,"xmlFilename");
			
			//Split the filename separated by comma in case multiple XSD filenames occured.
			XSDFileNameArray = XSDFileName.split(",");
			
			System.out.println("XMLFileName  = "+ XMLFileName);
			loggern.info("XMLFileName  = "+ XMLFileName);

			for(int i= 0; i< XSDFileNameArray.length; i++)
			{
				System.out.println("XSDFileName"+i+" = "+XSDFileNameArray[i]);
				loggern.info("XSDFileName"+i+" = "+XSDFileNameArray[i]);
			}
			
	}
				
    //************** Process according to input options *************
	public void run () 
		{
	
	    input="";
		System.out.println("==========Start Listening for local Port==========:"+server.getRemoteSocketAddress());
		loggern.info("==========Start Listening for local Port==========:"+server.getRemoteSocketAddress());
		
		try 
		{
				// Get input from the client
				DataInputStream in = new DataInputStream (new BufferedInputStream(server.getInputStream()));
				DataOutputStream out = new DataOutputStream(new DataOutputStream (server.getOutputStream()));
				input=readData(in);
				
				//System.out.println(input);
				readPropertyFile();
				
				String[] tempArr=input.split("~");
				String attList="";
				String wi_name="";
				String DocName="";
				String sessionId="";
				File dir= null;
				String statusAttach="";
				String sFilePath="";
				String docStatus="";
				String outputDoc="";
				String docIndex="";
				String generateStatus="";
				String fileOut="";
				int noFile=0;
				 for(int i=0;i<tempArr.length;i++)
				 {
					 if(i==0)
					   attList=tempArr[i];
					   if(i==1)
					   wi_name=tempArr[i];
				    if(i==2)
					   DocName=tempArr[i];
				   if(i==3)
					   sessionId=tempArr[i];
				 }
								
				String target_file_loc=System.getProperty("user.dir")+File.separator+"GRTemplate"+File.separator+wi_name;				
				
				File file = new File(target_file_loc);
				if (!file.exists()) 
				{
					file.mkdir();
				}
				if(DocName.equalsIgnoreCase("Application_Form"))
				{
					generateStatus=GeneratePdf.replaceGRTemplateArgs(file,attList);
					System.out.println("generateStatus --- " + generateStatus);
				}
				else
				{
					generateStatus=GeneratePdf.generateHTML(file,attList,DocName, wi_name, cabinetName,sessionId, jtsIP);
					System.out.println("generateStatus --- " + generateStatus);
				}
				if (generateStatus.equalsIgnoreCase("PDF Generated") || generateStatus.equalsIgnoreCase("HTML generated"))
				{
					System.out.println("inside statusPdf --- " );
					File files[] = file.listFiles();
					if(files==null)
					{
						System.out.println("NO pdf to attach" );
						
					}
					else
					{
						System.out.println("Pdf to attach --- " +files.length);

					}
					for(noFile=0;noFile<files.length;noFile++)
					{
						/*if(files[noFile].getName().contains("NewCAMReport")){
						fileOut = files[noFile].getName();
						}*/
						
						fileOut = files[noFile].getName();
					}
							
					sFilePath = ""+target_file_loc+"\\"+fileOut+"";							
					
					statusAttach = GeneratePdf.attachDocument( sFilePath, wi_name, DocName, jtsIP, jtsPort, cabinetName, volId, userName,password,sessionId);

					String[] statusAttachArr =statusAttach.split("~"); 
					for(int k=0;k<statusAttachArr.length;k++)
					{
						if(k==0)
							docStatus=statusAttachArr[k];
					    if(k==1)
							outputDoc=statusAttachArr[k];
						if(k==2)
							docIndex=statusAttachArr[k];
					}
					if(docStatus.equalsIgnoreCase("Y"))
					{
						System.out.println("document attached successfully --- " );
						try
						{
							File lobjFileTemp = new File(sFilePath);
							File deleteFolder = new File(target_file_loc);
							if (lobjFileTemp.exists()) 
							{
								System.out.println("inside folder delete" );
								if (!lobjFileTemp.isDirectory()) 
								{
									boolean test2=lobjFileTemp.delete();
									deleteDir(deleteFolder);
									System.out.println("File deleted: " );
								} 
								else 
								{
									deleteDir(lobjFileTemp);
									System.out.println("folder deleted: " );
								}
							} 
							else 
							{
								System.out.println("No file/folder to delete: " );
								lobjFileTemp = null;
							//////////////
							}
						}
						catch (Exception lobjExp) {
						System.out.println("Exception occurred while deleting  + ");
						} 
						input = "Success"+"~"+outputDoc+"~"+docIndex;	
						//input = "Success";
						
						System.out.println("input with outputDoc--- " + input);
						
					}
					
				}
				else
					input = "Failure";
			// ************ Now write to the client ********************
				
				writeData(out,input);
	
					// server.close();
			}
		  
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
			DataOutputStream out=null;
			try
			{
				out = new DataOutputStream(new DataOutputStream (server.getOutputStream()));
			}
			catch(Exception e)
			{
				System.out.println("Data Output Stream initialization error");
				N.err="Data Output Stream initialization error"+e.toString();
				e.printStackTrace();
			}
	        System.out.println("Catch 19 IOException on socket listen: " + ioe);
			N.err="IOException on socket listen: "+ioe;
			//input="<Status>FAILURE</Status>"+ N.err;
			input=N.err;
			//System.out.println("		Returning " + input);
			//loggern.info("		Returning " + input);
			try
			{
				writeData(out,input);
			}
			catch(Exception e)
			{
				N.err="Write Data Exception "+e.toString();
			}
			loggern.info("IOException on socket listen: " + ioe);
	        ioe.printStackTrace();		
	    }
		  
		finally
			{
				try
				{
					if (server!=null)
					{	
						System.out.println("====Closing local Cient Socket======="+server.getRemoteSocketAddress());
						loggern.info("====Closing local Cient Socket======="+server.getRemoteSocketAddress());
						server.close();
						server=null;
						System.out.println("====Successfuly Closed=======");
						loggern.info("====Successfuly Closed=======");
					
					}
				 }
				catch(Exception e)
				{
					System.out.println("Exception " + e.toString());
					loggern.info("Exception " + e.toString());
					N.err=e.toString();
					//input="<Status>FAILURE</Status>"+ N.err;
					input=N.err;
				}
			}
	 }
	 
/*	  public boolean deleteDir(File dir) throws Exception 
	 {
	        if (dir.isDirectory()) 
	        {
	            String[] lstrChildren = dir.list();
	            for (int i = 0; i < lstrChildren.length; i++) 
	            {
	                boolean success = deleteDir(new File(dir, lstrChildren[i]));
	                if (!success) 
	                {
	                    return false;
	                }
	            }
	        }
	        return dir.delete();
	 }*/
}

//----------------------------------------------------------------------------------------------------------------

