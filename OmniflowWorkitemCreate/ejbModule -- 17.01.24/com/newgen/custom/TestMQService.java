package com.newgen.custom;

public class TestMQService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String message =  "<EE_EAI_MESSAGE><EE_EAI_HEADER><MsgFormat>CUSTOMER_SR</MsgFormat><MsgVersion>0000</MsgVersion><RequestorChannelId>EBC.RPA</RequestorChannelId><RequestorUserId>RAKUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>0000</ReturnDesc><MessageId>RPAG24345T2412</MessageId><Extra2>2018-09-10T12:37:09.822+05:30</Extra2></EE_EAI_HEADER><CreateWorkitemReq><ProcessName>RMT</ProcessName><SubProcess/><InitiateAlso>Y</InitiateAlso><Attributes><Attribute><Name>CHANNEL</Name><Value>EBC.RPA</Value></Attribute><Attribute><Name>CIFNUMBER</Name><Value></Value></Attribute><Attribute><Name>CUSTOMERTYPE</Name><Value>Individual</Value></Attribute><Attribute><Name>CUSTOMERCATEGORY</Name><Value>Resident Individual</Value></Attribute><Attribute><Name>FIRSTNAME</Name><Value>RATHINA RAJ SURESH</Value></Attribute><Attribute><Name>LASTNAME</Name><Value>KUMAR</Value></Attribute><Attribute><Name>NATIONALITY</Name><Value>IN</Value></Attribute><Attribute><Name>DATEOFBIRTH</Name><Value>1990-11-30</Value></Attribute><Attribute><Name>EMPLOYMENTTYPE</Name><Value>SELF-EMPLOYED</Value></Attribute><Attribute><Name>SEGMENT</Name><Value>PERSONAL BANKING</Value></Attribute><Attribute><Name>SUBSEGMENT</Name><Value>PB - NORMAL</Value></Attribute><Attribute><Name>INDUSTRY</Name><Value>ARMS DEALER</Value></Attribute><Attribute><Name>POLITICALLYEXPOSEDPERSON</Name><Value>N</Value></Attribute><Attribute><Name>EMIRATESID</Name><Value>784199053526200</Value></Attribute><Attribute><Name>EMIRATESIDEXPIRYDATE</Name><Value>2021-05-30</Value></Attribute><Attribute><Name>PASSPORTNUMBER</Name><Value>M2737474</Value></Attribute><Attribute><Name>PASSPORTEXPIRYDATE</Name><Value>2019-09-10</Value></Attribute><Attribute><Name>DEMOGRAPHIC</Name><Value>AS</Value></Attribute><Attribute><Name>PRODUCTTYPE</Name><Value>RMT Account</Value></Attribute><Attribute><Name>PRODUCTCURRENCY</Name><Value>AED</Value></Attribute><Attribute><Name>CURRENTRESIDENTADDRESS</Name><Value>Test April Dubai United Arab Emirates</Value></Attribute><Attribute><Name>MAILINGADDRESS</Name><Value>Sample Iws Ras Al Khaimah United Arab Emirates</Value></Attribute><Attribute><Name>CITYOFBIRTH</Name><Value>DXB</Value></Attribute><Attribute><Name>COUNTRYOFBIRTH</Name><Value>AE</Value></Attribute><Attribute><Name>CRSUNDOCUMENTEDFLAG</Name><Value>N</Value></Attribute><Attribute><Name>REPORTCOUNTRYDETAILS</Name><Value>AE~~A-NOT ISSUED~test||</Value></Attribute><Attribute><Name>UID</Name><Value>6a6af0c4eb9b32b7d85f16f1b93c5cf617beb359</Value></Attribute><Attribute><Name>EXTERNALREFERENCENO</Name><Value>4118491</Value></Attribute><Attribute><Name>FACIALSCORE</Name><Value>6</Value></Attribute><Attribute><Name>MOBILENO</Name><Value>528858575</Value></Attribute><Attribute><Name>EMAILID</Name><Value>rath@ui.jj</Value></Attribute></Attributes></CreateWorkitemReq></EE_EAI_MESSAGE>";
		//String message = "<EE_EAI_HEADER><EE_EAI_HEADER><MsgFormat>UPDATE_SR</MsgFormat><MsgVersion>0000</MsgVersion><RequestorChannelId>IB</RequestorChannelId><RequestorUserId>RAKUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>Success</ReturnDesc><MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId><Extra1>REQ ||IB.123</Extra1><Extra2>2011-02-08T15:31:38.818+05:30</Extra2></EE_EAI_HEADER><UpdateWorkitemReq><ProcessName>RMT</ProcessName><SubProcess/><Attributes><Attribute><Name>WINUMBER</Name><Value>RMT-0000000920-Process</Value></Attribute><Attribute><Name>UID2</Name><Value>1b606eaa2a42f67f6c74b1f70731e63a7dc3eaa7446dfe2d978885e5e617ed2d15336306797382</Value></Attribute><Attribute><Name>PASSPORTNUMBER</Name><Value>P123456</Value></Attribute><Attribute><Name>PASSPORTEXPIRYDATE</Name><Value>2300/01/01</Value></Attribute><Attribute><Name>VISANUMBER</Name><Value>2300/01/01</Value></Attribute><Attribute><Name>VISAISSUEDATE</Name><Value>2300-01-01</Value></Attribute><Attribute><Name>VISAEXPIRYDATE</Name><Value>2300-01-01</Value></Attribute></Attributes></UpdateWorkitemReq></EE_EAI_HEADER>";
		//String message="<EE_EAI_HEADER><MsgFormat>CUSTOMER_SR</MsgFormat><MsgVersion>0000</MsgVersion><RequestorChannelId>EBC.RPA</RequestorChannelId><RequestorUserId>RAKUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>0000</ReturnDesc><MessageId>ABCTESTDEDUP1</MessageId><Extra1>REQ||CORP6EID16.CORP6EID16</Extra1><Extra2>2018-10-15T09:46:17.251+05:30</Extra2></EE_EAI_HEADER><CreateWorkitemReq><ProcessName>RMT</ProcessName><SubProcess/><InitiateAlso>Y</InitiateAlso><Attributes><Attribute><Name>CHANNEL</Name><Value>EBC.RPA</Value></Attribute><Attribute><Name>CIFNUMBER</Name><Value>5000050</Value></Attribute><Attribute><Name>CUSTOMERTYPE</Name><Value>Individual</Value></Attribute><Attribute><Name>CUSTOMERCATEGORY</Name><Value>Resident Individual</Value></Attribute><Attribute><Name>FIRSTNAME</Name><Value>RAMAINA RAJ SMAESH</Value></Attribute><Attribute><Name>LASTNAME</Name><Value>MAMAR</Value></Attribute><Attribute><Name>NATIONALITY</Name><Value>IN</Value></Attribute><Attribute><Name>DATEOFBIRTH</Name><Value>1990-11-30</Value></Attribute><Attribute><Name>EMPLOYMENTTYPE</Name><Value>SALARIED</Value></Attribute><Attribute><Name>SEGMENT</Name><Value>PERSONAL BANKING</Value></Attribute><Attribute><Name>SUBSEGMENT</Name><Value>PB - NORMAL</Value></Attribute><Attribute><Name>INDUSTRY</Name><Value>EMPLOYED INDIVIDUAL</Value></Attribute><Attribute><Name>POLITICALLYEXPOSEDPERSON</Name><Value>N</Value></Attribute><Attribute><Name>EMIRATESID</Name><Value>784199053556200</Value></Attribute><Attribute><Name>EMIRATESIDEXPIRYDATE</Name><Value>2021-05-30</Value></Attribute><Attribute><Name>DEMOGRAPHIC</Name><Value>AE</Value></Attribute><Attribute><Name>ACCOUNTNUMBER</Name><Value>0025000050001</Value></Attribute><Attribute><Name>PRODUCTTYPE</Name><Value>RMT Account</Value></Attribute><Attribute><Name>PRODUCTCURRENCY</Name><Value>AED</Value></Attribute><Attribute><Name>CURRENTRESIDENTADDRESS</Name><Value>Test  Fujairah United Arab Emirates</Value></Attribute><Attribute><Name>MAILINGADDRESS</Name><Value>Test  Fujairah United Arab Emirates</Value></Attribute><Attribute><Name>CITYOFBIRTH</Name><Value>Test</Value></Attribute><Attribute><Name>COUNTRYOFBIRTH</Name><Value>AI</Value></Attribute><Attribute><Name>CRSUNDOCUMENTEDFLAG</Name><Value>N</Value></Attribute><Attribute><Name>REPORTCOUNTRYDETAILS</Name><Value>AE~~B-UNABLE GET TIN~Test||</Value></Attribute><Attribute><Name>UID</Name><Value>ae95230de534370946e1bcf669e9a2b52f2608ac30d0fdc4ba258e36d80ee5f515395822354854</Value></Attribute><Attribute><Name>EXTERNALREFERENCENO</Name><Value>4095310</Value></Attribute><Attribute><Name>FACIALSCORE</Name><Value>4</Value></Attribute><Attribute><Name>MOBILENO</Name><Value>546458486</Value></Attribute><Attribute><Name>EMAILID</Name><Value>ratqidid@idj.com</Value></Attribute></Attributes></CreateWorkitemReq></EE_EAI_MESSAGE>";
		//String message = "<EE_EAI_HEADER><EE_EAI_HEADER><MsgFormat>CUSTOMER_SR</MsgFormat><MsgVersion>0000</MsgVersion><RequestorChannelId>YAP</RequestorChannelId><RequestorUserId>RAKUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>Success</ReturnDesc><MessageId>msg46737</MessageId><Extra1>REQ || YAP.123</Extra1><Extra2>2011-02-08T15:31:38.818+05:30</Extra2></EE_EAI_HEADER><CreateWorkitemReq><ProcessName>RAOP</ProcessName><SubProcess>YAP</SubProcess><InitiateAlso>Y</InitiateAlso><Documents></Documents><Attributes><Attribute><Name>CHANNEL</Name><Value>YAP</Value></Attribute><Attribute><Name>IBANNUMBER</Name><Value>AE350260120550176640</Value></Attribute><Attribute><Name>TITLEPREFIX</Name><Value></Value></Attribute><Attribute><Name>CUSTOMERCATEGORY</Name><Value>Resident Individual</Value></Attribute><Attribute><Name>FIRSTNAME</Name><Value>pooja</Value></Attribute><Attribute><Name>MIDDLENAME</Name><Value>Kumari</Value></Attribute><Attribute><Name>LASTNAME</Name><Value>Singhal</Value></Attribute><Attribute><Name>NATIONALITY</Name><Value>AE</Value></Attribute><Attribute><Name>DATEOFBIRTH</Name><Value>1997-02-15</Value></Attribute><Attribute><Name>GENDER</Name><Value>F</Value></Attribute><Attribute><Name>MARITALSTATUS</Name><Value>M</Value></Attribute><Attribute><Name>EMPLOYMENTTYPE</Name><Value>MIGR</Value></Attribute><Attribute><Name>EMPLOYERNAME</Name><Value>Vimamra Agnihotri</Value></Attribute><Attribute><Name>OCCUPATION</Name><Value>ENGG</Value></Attribute><Attribute><Name>INDUSTRYSEGMENT</Name><Value>TSC</Value></Attribute><Attribute><Name>INDUSTRYSUBSEGMENT</Name><Value>AK</Value></Attribute><Attribute><Name>POLITICALLYEXPOSEDPERSON</Name><Value>NPEP</Value></Attribute><Attribute><Name>MOBILECNTRYCODE</Name><Value>+971</Value></Attribute><Attribute><Name>MOBILENO</Name><Value>25874122112</Value></Attribute><Attribute><Name>EMAILID</Name><Value>abc@gmail.com</Value></Attribute><Attribute><Name>EMIRATESID</Name><Value>7841901797</Value></Attribute><Attribute><Name>EMIRATESIDEXPIRYDATE</Name><Value>2300-01-01</Value></Attribute><Attribute><Name>PASSPORTNUMBER</Name><Value>P123456</Value></Attribute><Attribute><Name>PASSPORTEXPIRYDATE</Name><Value>2300-01-01</Value></Attribute><Attribute><Name>VISAFILENUMBER</Name><Value>7654</Value></Attribute><Attribute><Name>VISAUIDNUMBER</Name><Value>529811166</Value></Attribute><Attribute><Name>VISAISSUEDATE</Name><Value>2300-01-01</Value></Attribute><Attribute><Name>VISAEXPDATE</Name><Value>2300-01-01</Value></Attribute><Attribute><Name>DEMOGRAPHIC</Name><Value>IN</Value></Attribute><Attribute><Name>RESIDENCEADDRLINE1</Name><Value>D1</Value></Attribute><Attribute><Name>RESIDENCEADDRLINE2</Name><Value>nirma apartment</Value></Attribute><Attribute><Name>RESIDENCEADDRPOBOX</Name><Value>01254</Value></Attribute><Attribute><Name>RESIDENCEADDRCITY</Name><Value>RYD</Value></Attribute><Attribute><Name>RESIDENCEADDRCOUNTRY</Name><Value>DE</Value></Attribute><Attribute><Name>MAILINGADDRLINE1</Name><Value>Octa Phase</Value></Attribute><Attribute><Name>MAILINGADDRLINE2</Name><Value>Bita Residency</Value></Attribute><Attribute><Name>MAILINGADDRPOBOX</Name><Value>22635</Value></Attribute><Attribute><Name>MAILINGADDRCITY</Name><Value>SAK</Value></Attribute><Attribute><Name>MAILINGADDRCOUNTRY</Name><Value>BR</Value></Attribute><Attribute><Name>CITYOFBIRTH</Name><Value>AAP</Value></Attribute><Attribute><Name>COUNTRYOFBIRTH</Name><Value>AL</Value></Attribute><Attribute><Name>REPORTCOUNTRYDETAILS</Name><Value>USA~123445~XYZ476~remarks| UAE~5577~No tax number~remarks | IND~ABC12345~Reason B~ok</Value></Attribute><Attribute><Name>USRELATION</Name><Value>R</Value></Attribute><Attribute><Name>TINNUMBER</Name><Value>1458</Value></Attribute><Attribute><Name>FATCAREASON</Name><Value>US PLACE OF BIRTH</Value></Attribute><Attribute><Name>DOCUMENTSCOLLECTED</Name><Value>W8</Value></Attribute><Attribute><Name>SIGNEDDATE</Name><Value>2017-01-01</Value></Attribute><Attribute><Name>EXPECTEDMONTHLYCREDITTURNOVERAED</Name><Value>65465.22</Value></Attribute><Attribute><Name>MONTHLYCASHCREDITTURNOVERPERCENTAGE</Name><Value>66</Value></Attribute><Attribute><Name>MONTHLYNONCASHCREDITTURNOVERPERCENTAGE</Name><Value>15.50</Value></Attribute><Attribute><Name>HIGHESTCASHCREDITTRANSACTIONAED</Name><Value>456.456</Value></Attribute><Attribute><Name>HIGHESTNONCASHCREDITTRANSACTIONAED</Name><Value>654654.45</Value></Attribute></Attributes></CreateWorkitemReq></EE_EAI_HEADER>";
		//String message = "<EE_EAI_HEADER><EE_EAI_HEADER><MsgFormat>STATUS_SR</MsgFormat><MsgVersion>0000</MsgVersion><RequestorChannelId>ECB.INB</RequestorChannelId><RequestorUserId>RAKUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>Success</ReturnDesc><MessageId>msg46737</MessageId><Extra1>REQ || ECB.INB.123</Extra1><Extra2>2011-02-08T15:31:38.818+05:30</Extra2></EE_EAI_HEADER><GetWorkitemStatusReq><ProcessName>BAIS</ProcessName><SubProcess></SubProcess><Attributes><Attribute><Name>WorkitemNumber</Name><Value>BAIS-0000000352-Process</Value></Attribute></Attributes></GetWorkitemStatusReq></EE_EAI_HEADER>";
		//String message = "<EE_EAI_HEADER><EE_EAI_HEADER><MsgFormat>CUSTOMER_SR</MsgFormat><MsgVersion>0000</MsgVersion><RequestorChannelId>YAP</RequestorChannelId><RequestorUserId>RAKUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>Success</ReturnDesc><MessageId>msg46738</MessageId><Extra1>REQ || CMP.123</Extra1><Extra2>2011-02-08T15:31:38.818+05:30</Extra2></EE_EAI_HEADER><CreateWorkitemReq><ProcessName>CMP</ProcessName><SubProcess>API.YAP</SubProcess><InitiateAlso>Y</InitiateAlso><Documents></Documents><Attributes><Attribute><Name>CHANNEL</Name><Value>CMP</Value></Attribute><Attribute><Name>FIRSTNAME</Name><Value>Meena</Value></Attribute><Attribute><Name>MIDDLENAME</Name><Value>Kumari</Value></Attribute><Attribute><Name>LASTNAME</Name><Value>Singhal</Value></Attribute><Attribute><Name>EMIRATESID</Name><Value>7841901797</Value></Attribute><Attribute><Name>EMIRATESIDEXPIRYDATE</Name><Value>2300-01-01</Value></Attribute><Attribute><Name>MOBILENO</Name><Value>25874122112</Value></Attribute><Attribute><Name>MOBILECNTRYCODE</Name><Value>2587</Value></Attribute><Attribute><Name>NATIONALITY</Name><Value>AE</Value></Attribute><Attribute><Name>DATEOFBIRTH</Name><Value>1994-04-03</Value></Attribute><Attribute><Name>GENDER</Name><Value>F</Value></Attribute><Attribute><Name>EMAILID</Name><Value>abc@gmail.com</Value></Attribute><Attribute><Name>PASSPORTNUMBER</Name><Value>P123456</Value></Attribute><Attribute><Name>PASSPORTEXPIRYDATE</Name><Value>2300-01-01</Value></Attribute><Attribute><Name>RESIDENCEADDRLINE1</Name><Value>D1</Value></Attribute><Attribute><Name>RESIDENCEADDRLINE2</Name><Value>nirma apartment</Value></Attribute><Attribute><Name>RESIDENCEADDRPOBOX</Name><Value>01254</Value></Attribute><Attribute><Name>RESIDENCEADDRCITY</Name><Value>RYD</Value></Attribute><Attribute><Name>RESIDENCEADDRCOUNTRY</Name><Value>DE</Value></Attribute></Attributes></CreateWorkitemReq></EE_EAI_HEADER>";
		//String message = "<EE_EAI_MESSAGE><EE_EAI_HEADER><MsgFormat>CUSTOMER_SR</MsgFormat><MsgVersion>0000</MsgVersion><RequestorChannelId>YAP</RequestorChannelId><RequestorUserId>RAKUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>Success</ReturnDesc><MessageId>test123456</MessageId><Extra1>REQ || YAP.123</Extra1><Extra2>2011-02-08T15:31:38.818+05:30</Extra2></EE_EAI_HEADER><CreateWorkitemReq><ProcessName>CBP</ProcessName><SubProcess>API.YAP</SubProcess><InitiateAlso>Y</InitiateAlso><Documents></Documents><Attributes><Attribute><Name>CHANNEL</Name><Value>YAP</Value></Attribute><Attribute><Name>ACCOUNTNUMBER</Name><Value>456456456456</Value></Attribute><Attribute><Name>PASSPORTEXPIRYDATE</Name><Value>1998-02-19</Value></Attribute><Attribute><Name>PASSPORTISSUEDATE</Name><Value>2007-05-22</Value></Attribute><Attribute><Name>PASSPORTNUMBER</Name><Value>P873645673</Value></Attribute><Attribute><Name>VISAEXPDATE</Name><Value>2006-07-11</Value></Attribute><Attribute><Name>VISAFILENUMBER</Name><Value>57567576</Value></Attribute><Attribute><Name>VISAISSUEDATE</Name><Value>1887-02-14</Value></Attribute></Attributes></CreateWorkitemReq></EE_EAI_MESSAGE>";
		
		// wi create RAOP
		/*String message = "<EE_EAI_MESSAGE>"+
		  "<EE_EAI_HEADER>"+
		    "<MsgFormat>CUSTOMER_SR</MsgFormat>"+
		    "<MsgVersion>0000</MsgVersion>"+
		    "<RequestorChannelId>API.YAP</RequestorChannelId>"+
		    "<RequestorUserId>RAKUSER</RequestorUserId>"+
		    "<RequestorLanguage>E</RequestorLanguage>"+
		    "<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
		    "<ReturnCode>0000</ReturnCode>"+
		    "<ReturnDesc>0000</ReturnDesc>"+
		    "<MessageId>tttyxzz</MessageId>"+
		    "<Extra1/>"+
		    "<Extra2/>"+
		  "</EE_EAI_HEADER>"+
		  "<CreateWorkitemReq>"+
		    "<ProcessName>RAOP</ProcessName>"+
		    "<SubProcess>API.YAP</SubProcess>"+
		    "<InitiateAlso>Y</InitiateAlso>"+
		    "<Attributes>"+
		      "<Attribute>"+
		        "<Name>CHANNEL</Name>"+
		        "<Value>YAP</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>IBANNUMBER</Name>"+
		        "<Value>AE790400000188000001510</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>TITLEPREFIX</Name>"+
		        "<Value>MRS.</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>FIRSTNAME</Name>"+
		        "<Value>SHERIN VARGHESE MATH</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>LASTNAME</Name>"+
		        "<Value>VARGHESE</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>NATIONALITY</Name>"+
		        "<Value>AE</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>DATEOFBIRTH</Name>"+
		        "<Value>1988-10-08</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>GENDER</Name>"+
		        "<Value>F</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>POLITICALLYEXPOSEDPERSON</Name>"+
		        "<Value>NPEP</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>MOBILECNTRYCODE</Name>"+
		        "<Value>00971</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>MOBILENO</Name>"+
		        "<Value>564802474</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>EMAILID</Name>"+
		        "<Value>sherinvarghese07@gmail.com</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>EMIRATESID</Name>"+
		        "<Value>784198806906206</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>EMIRATESIDEXPIRYDATE</Name>"+
		        "<Value>2021-07-14</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>DEMOGRAPHIC</Name>"+
		        "<Value>AE|AE</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>RESIDENCEADDRLINE1</Name>"+
		        "<Value>Sheikh Mohammed Bin Zayed Road Al Warqaa First Dub</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>RESIDENCEADDRLINE2</Name>"+
		        "<Value>Sheikh Mohammed Bin Zayed Road</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>RESIDENCEADDRPOBOX</Name>"+
		        "<Value>00000</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>RESIDENCEADDRCITY</Name>"+
		        "<Value>DXB</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>RESIDENCEADDRCOUNTRY</Name>"+
		        "<Value>AE</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>MAILINGADDRLINE1</Name>"+
		        "<Value>Sheikh Mohammed Bin Zayed Road Al Warqaa First Dub</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>MAILINGADDRLINE2</Name>"+
		        "<Value>Sheikh Mohammed Bin Zayed Road</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>MAILINGADDRPOBOX</Name>"+
		        "<Value>00000</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>MAILINGADDRCITY</Name>"+
		        "<Value>DXB</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>MAILINGADDRCOUNTRY</Name>"+
		        "<Value>AE</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>REPORTCOUNTRYDETAILS</Name>"+
		        "<Value>USA~123445~~remarks|UAE~~No tax number~remarks |IND~ABC12345~Reason B~</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>USRELATION</Name>"+
		        "<Value>O</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>TINNUMBER</Name>"+
		        "<Value>4444</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>FATCAREASON</Name>"+
		        "<Value>CURRENT US ADDR</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>DOCUMENTSCOLLECTED</Name>"+
		        "<Value>ID DOC|LOSS OF NAT CERT|SELF-ATTEST FORM|W8|W9</Value>"+
		      "</Attribute>"+
		      "<Attribute>"+
		        "<Name>SIGNEDDATE</Name>"+
		        "<Value>2017-01-01</Value>"+
		      "</Attribute>"+
		    "</Attributes>"+
		  "</CreateWorkitemReq>"+
		"</EE_EAI_MESSAGE>";*/
		
		
		//For CDOB Update
		/*String message="<EE_EAI_MESSAGE>"+
			"<EE_EAI_HEADER>"+
				"<MsgFormat>UPDATE_SR</MsgFormat>"+
				"<MsgVersion>0000</MsgVersion>"+
				"<RequestorChannelId>DEH</RequestorChannelId>"+
				"<RequestorUserId>RAKUSER</RequestorUserId>"+
				"<RequestorLanguage>E</RequestorLanguage>"+
				"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
				"<ReturnCode>0000</ReturnCode>"+
				"<ReturnDesc>Success</ReturnDesc>"+
				"<MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>"+
				"<Extra1>REQ ||IB.123</Extra1>"+
				"<Extra2>2011-02-08T15:31:38.818+05:30</Extra2>"+
			"</EE_EAI_HEADER>"+
			"<UpdateWorkitemReq>"+
			"<ProcessName>CDOB</ProcessName>"+
			"<SubProcess/>"+
			    "<Attributes>"+
					"<Attribute>"+
						"<Name>OperationName</Name>"+
						"<Value>DataUpdate</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>IsDoneRequired</Name>"+
						"<Value>TRUE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
					"<Name>PortalReferenceNumber</Name>"+
					"<Value>6565665</Value>"+
				"</Attribute>"+
					"<Attribute>"+
						"<Name>IsDocsPresent</Name>"+
						"<Value>TRUE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>WINumber</Name>"+
						"<Value>CDOB-0000001245-process</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>Iban_number</Name>"+
						"<Value>54544848484845454</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>BankName</Name>"+
						"<Value>ADCB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>Genuine_Check</Name>"+
						"<Value>TRUE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>BiometricStatus</Name>"+
						"<Value>TRUE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>Delivery_status</Name>"+
						"<Value>TRUE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>Remarks</Name>"+
						"<Value>okokok</Value>"+
					"</Attribute>"+
			    "</Attributes>"+
			  "</UpdateWorkitemReq>"+
			"</EE_EAI_MESSAGE>";*/
		
		
		//For CDOB Create WI
		String CDOB_Wicreate_message="<EE_EAI_HEADER>"+
		"<EE_EAI_HEADER>"+
			  "<MsgFormat>CUSTOMER_SR</MsgFormat>"+
			  "<MsgVersion>0000</MsgVersion>"+
			  "<RequestorChannelId>DEH</RequestorChannelId>"+
			  "<RequestorUserId>RAKUSER</RequestorUserId>"+
			  "<RequestorLanguage>E</RequestorLanguage>"+
			  "<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
			  "<ReturnCode>0000</ReturnCode>"+
			  "<ReturnDesc>Success</ReturnDesc>"+
			  "<MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>"+
			  "<Extra1>REQ || DEH.123</Extra1>"+
			  "<Extra2>2011-02-08T15:31:38.818+05:30</Extra2>"+
		"</EE_EAI_HEADER>"+
		"<CreateWorkitemReq>"+
			"<ProcessName>CDOB</ProcessName>"+
			"<SubProcessName>CDOB</SubProcessName>"+
			"<InitiateAlso>N</InitiateAlso>"+
			"<Attributes>"+
				"<Attribute>"+
					"<Name>MobileNo_pri</Name>"+
					"<Value>00971521626485</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>Title</Name>"+
					"<Value>MR.</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>FirstName</Name>"+
					"<Value>AUGUSTIN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>MiddleName</Name>"+
					"<Value></Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>LastName</Name>"+
					"<Value>SALCEDO VALENZUELA</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>EmiratesID</Name>"+
					"<Value>784199009201203</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>dob</Name>"+
					"<Value>1990-11-12</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>Nationality</Name>"+
					"<Value>KW</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>PAssportNo</Name>"+
					"<Value>AE9201203</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>Emp_Type</Name>"+
					"<Value>Salaried</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>Basic_Sal</Name>"+
					"<Value>30000</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>Email_ID</Name>"+
					"<Value>deepak@test.com</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>Aecb_Consent_Received</Name>"+
					"<Value>TRUE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Attribute>"+
					"<Name>EmpCode</Name>"+
					"<Value>5559</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>Skyward_Number</Name>"+
					"<Value>SK1343434</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>NameOnCard</Name>"+
					"<Value>Deepak</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>Iban_number</Name>"+
					"<Value>AE328800000004532668892</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>BankName</Name>"+
					"<Value>ADCB</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>FTS_Consent</Name>"+
					"<Value>TRUE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>Product_Type</Name>"+
					"<Value>C-FALCON</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>Designation</Name>"+
					"<Value>LNLD</Value>"+
				"</Attribute>" +
				"<Attribute>" +
					"<Name>EmpName</Name>" +
					"<Value>ABU DHABI COMMERCIAL BANK</Value>" +
				"</Attribute>" +
				"<Attribute>" +
					"<Name>IndusSeg</Name>" +
					"<Value>COMPUTER ACCESSORIES</Value>" +
				"</Attribute>" +
				"<Attribute>" +
					"<Name>emirateOfWork</Name>"+
					"<Value>DXB</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>ReferenceName</Name>"+
					"<Value>Manish</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>ReferenceMobile</Name>"+
					"<Value>0097145455545</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>PortalApplicationNumber</Name>"+
					"<Value>909</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>ApprovedLimit</Name>"+
					"<Value>135000.0</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>HomeCountryHouseNo</Name>"+
					"<Value>B102,MUMBAI</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>OFFICEADDRESSPOBOX</Name>"+
					"<Value>1020</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>HomeCountryBuildingName</Name>"+
					"<Value>MUMBAI V</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>HomeCountryStreetName</Name>"+
					"<Value>BOM</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>HomeCountryNo</Name>"+
					"<Value>009153561518484</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>Mothersname</Name>"+
					"<Value>TestName</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>FinalDBR</Name>"+
					"<Value>14.3</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>FinalTAI</Name>"+
					"<Value>30000</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>EligibleCardProduct</Name>"+
					"<Value>Credit Card|Salaried Credit Card||135000.00||A||A999-System Approve|SR. MGR|0~Credit Card|Salaried Credit Card||135000.00||A||A999-System Approve|SR. MGR|0"+
					"</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>isNTB</Name>"+
					"<Value>TRUE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>MaritalStatus</Name>"+
					"<Value>O</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>EmploymentStatus</Name>"+
					"<Value>2</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>YearsInCurrAddress</Name>"+
					"<Value>1</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>EStatementFlag</Name>"+
					"<Value>TRUE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>CountryOfBirth</Name>"+
					"<Value>IN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>EmirateOfVisa</Name>"+
					"<Value>DXB</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>CountryOfTaxResidence</Name>"+
					"<Value>IN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>CityOfBirth</Name>"+
					"<Value>BOM</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>isTinAvailable</Name>"+
					"<Value>FALSE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>TINNumber</Name>"+
					"<Value>48545554</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>TinReason</Name>"+
					"<Value>A-NOT ISSUED</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>emirateOfResidence</Name>"+
					"<Value>DXB</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>ResidenceAddressHouseNo</Name>"+
					"<Value>hghghg</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>RESIDENCEADDRESSPOBOX</Name>"+
					"<Value>22222</Value>"+
			"</Attribute>"+
				"<Attribute>"+
					"<Name>ResidenceAddressBuildingName</Name>"+
					"<Value>Buling Name</Value>"+
				"</Attribute>"+
				"<Attribute>"+
					"<Name>ResidenceAddressStreetName</Name>"+
					"<Value>r Street Name</Value>"+
				"</Attribute>"+
			"</Attributes>"+
		"</CreateWorkitemReq>"+
	"</EE_EAI_HEADER>";
		
		//<EE_EAI_MESSAGE>
		
		//For CDOB Dedupe check
		
		/*String message="<EE_EAI_HEADER>" +
					"<MsgFormat>STATUS_SR</MsgFormat>" +
					"<MsgVersion>0000</MsgVersion>" +
					"<RequestorChannelId>DEH</RequestorChannelId>" +
					"<RequestorUserId>RAKUSER</RequestorUserId>" +
					"<RequestorLanguage>E</RequestorLanguage>" +
					"<RequestorSecurityInfo>secure</RequestorSecurityInfo>" +
					"<ReturnCode>0000</ReturnCode>" +
					"<ReturnDesc>Success</ReturnDesc>" +
					"<MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>" +
					"<Extra1>REQ ||IB.123</Extra1>" +
					"<Extra2>2011-02-08T15:31:38.818+05:30</Extra2>" +
				"</EE_EAI_HEADER>" +
				"<GetWorkitemStatusReq>" +
					"<ProcessName>CDOB</ProcessName>" +
					"<SubProcess>Status_Enquire</SubProcess>" +
					"<Attributes>" +
						"<Attribute>" +
							"<Name>FirstName</Name>" +
							"<Value>AjhjhjhjN</Value>" +
						"</Attribute>" +
						"<Attribute>" +
							"<Name>LastName</Name>" +
							"<Value>hjhjhjhj</Value>" +
						"</Attribute>" +
						"<Attribute>" +
							"<Name>dob</Name>" +
							"<Value>1940-05-05</Value>" +
						"</Attribute>" +
						"<Attribute>" +
							"<Name>Nationality</Name>" +
							"<Value>IN</Value>" +
						"</Attribute>" +
						"<Attribute>" +
							"<Name>PAssportNo</Name>" +
							"<Value>N23434sxdcd433</Value>" +
						"</Attribute>" +
						"<Attribute>" +
						"<Attribute>" +
							"<Name>EmiratesID</Name>" +
							"<Value>7841985</Value>" +
						"</Attribute>" +
						"<Attribute>" +
							"<Name>Email_ID</Name>" +
							"<Value>test@test.com</Value>" +
						"</Attribute>" +
						"<Attribute>" +
							"<Name>PortalReferenceNumber</Name>" +
							"<Value>DEH-12345</Value>" +
						"</Attribute>" +
						"<Attribute>" +
						"<Name>WINumber</Name>" +
						"<Value>CDOB-0000001245-process</Value>" +
					"</Attribute>" +
					"</Attributes>" +
				"</GetWorkitemStatusReq>" +
				"</EE_EAI_MESSAGE>";*/
		
		//For Digital_CC cas Dedupe check
		
				String message1="<EE_EAI_MESSAGE>"+
							"<EE_EAI_HEADER>" +
							"<MsgFormat>STATUS_SR</MsgFormat>" +
							"<MsgVersion>0000</MsgVersion>" +
							"<RequestorChannelId>EBC.WBA</RequestorChannelId>" +
							"<RequestorUserId>RAKUSER</RequestorUserId>" +
							"<RequestorLanguage>E</RequestorLanguage>" +
							"<RequestorSecurityInfo>secure</RequestorSecurityInfo>" +
							"<ReturnCode>0000</ReturnCode>" +
							"<ReturnDesc>Successful</ReturnDesc>" +
							"<MessageId>143300521454466446</MessageId>" +
							"<Extra1>REQ||LAXMANRET.LAXMANRET</Extra1>" +
							"<Extra2>2015-05-30T22:30:14.544+05:30</Extra2>" +
							"</EE_EAI_HEADER>" +
							"<GetWorkitemStatusReq>" +
								"<ProcessName>Digital_CC</ProcessName>" +
								"<SubProcess>Case_Dedupe</SubProcess>" +
								"<Attributes>" +
									"<Attribute>" +
										"<Name>EmiratesID</Name>" +
										"<Value>784198937698151</Value>" +
									"</Attribute>" +
								"</Attributes>" +
								"</GetWorkitemStatusReq>" +
							"</EE_EAI_MESSAGE>";
		
				//For DOA WI Creation
				String message_DOACeration="<EE_EAI_MESSAGE>"+
						"<EE_EAI_HEADER>"+
						"<MsgFormat>CREATE_WORKITEM</MsgFormat>"+
						"<MsgVersion>0000</MsgVersion>"+
						"<RequestorChannelId>EBC.WBA</RequestorChannelId>"+
						"<RequestorUserId>RAKUSER</RequestorUserId>"+
						"<RequestorLanguage>E</RequestorLanguage>"+
						"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
						"<ReturnCode>0000</ReturnCode>"+
						"<ReturnDesc>Success</ReturnDesc>"+
						"<MessageId>Test1933374445</MessageId>"+
						"<Extra1>REQ || DEH.123</Extra1>"+
						"<Extra2>2011-02-08T15:31:38.818+05:30</Extra2>"+
						"</EE_EAI_HEADER>"+
						"<CreateWorkitemReq>"+
						"<ProcessName>DigitalAO</ProcessName>"+
						"<SubProcessName>DigitalAO</SubProcessName>"+
						"<InitiateAlso>Y</InitiateAlso>"+
						"<Documents/>"+
						"<Attributes>"+
						"<Attribute>"+
						"<Name>Title</Name>"+
						"<Value>Mr</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Given_Name</Name>"+
						"<Value>PHIL</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Surname</Name>"+
						"<Value>JAMES</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Passport_Number</Name>"+
						"<Value>IN12345678</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Emirates_ID</Name>"+
						"<Value>789123456999</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Gender</Name>"+
						"<Value>Male</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Marital_Status</Name>"+
						"<Value>Single</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Mothers_Maiden_Name</Name>"+
						"<Value>MOM</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Country_of_residence</Name>"+
						"<Value>India</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>DOB</Name>"+
						"<Value>12/03/1987</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Identification_Document_Number</Name>"+
						"<Value>10102029</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Curr_Visa_Designation</Name>"+
						"<Value>SE</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>MANDATE_AND_DECLARATION</Name>"+
						"<Value>Y</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Sign</Name>"+
						"<Value>PJ</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Dual_Nationality</Name>"+
						"<Value>N</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>CRS</Name>"+
						"<Value>N</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Passport_Copies</Name>"+
						"<Value>0</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Previous_Designation</Name>"+
						"<Value>09</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Is_ntb</Name>"+
						"<Value>Y</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Relation_Detail_w_PEP</Name>"+
						"<Value>Relationship Details with PEP</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>PEP_Category</Name>"+
						"<Value>as</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Name_of_PEP</Name>"+
						"<Value>sdf</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>country_pep_hold_status</Name>"+
						"<Value>ae</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Emirate_pep_hold_status</Name>"+
						"<Value>dxb</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Currency</Name>"+
						"<Value>AED</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Prefer_Branch_Det</Name>"+
						"<Value>dubai</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Employment_Status</Name>"+
						"<Value/>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Scheme_Code</Name>"+
						"<Value>098</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Name_on_ChequeBk</Name>"+
						"<Value>ABCD</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Product_Service_dealing</Name>"+
						"<Value>ABCD</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Agent_Code</Name>"+
						"<Value>DBC122</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Agent_ID</Name>"+
						"<Value>RAK12</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Name_on_Debit_Card</Name>"+
						"<Value>Hen</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Industry</Name>"+
						"<Value>ABCDS</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>ChequeBk_Req</Name>"+
						"<Value>Y</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>AECB_Consent</Name>"+
						"<Value>N</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Do_you_have_TIN</Name>"+
						"<Value>N</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>CIF</Name>"+
						"<Value>7656783</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Passport_Expiry_Date</Name>"+
						"<Value>10/10/2029</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Passport_Issuing_Country</Name>"+
						"<Value>India</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Visa_File_Number</Name>"+
						"<Value>12345</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Visa_issue_Date</Name>"+
						"<Value>10/01/2015</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Visa_expiry_Date</Name>"+
						"<Value>10/01/2025</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Nationality</Name>"+
						"<Value>India</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Secondary_Nationality</Name>"+
						"<Value>India</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>New_Passport_Number</Name>"+
						"<Value>DB34512237</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Driving_License</Name>"+
						"<Value>DL1234235</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Industry_Segment</Name>"+
						"<Value>Macro</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Industry_Subsegment</Name>"+
						"<Value>Car</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>PEP</Name>"+
						"<Value>Y</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>PEP_Category</Name>"+
						"<Value>45</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Name_of_PEP</Name>"+
						"<Value>sdfd</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>country_pep_hold_status</Name>"+
						"<Value>sdf</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Emirate_pep_hold_status</Name>"+
						"<Value>dxb</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Previous_Designation</Name>"+
						"<Value>09</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Relation_Detail_w_PEP</Name>"+
						"<Value>sdfs</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Product_Name</Name>"+
						"<Value>AO</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Credit_grade</Name>"+
						"<Value>A</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>EmiratesID_Expiry_Date</Name>"+
						"<Value>01/06/2028</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Preferred_Mail_Address</Name>"+
						"<Value>Current residence address</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Company_Employer_Name</Name>"+
						"<Value>Newgen</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Employer_Code</Name>"+
						"<Value>009</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>employer_type</Name>"+
						"<Value>Self-employed</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Designation</Name>"+
						"<Value>SE</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Occupation</Name>"+
						"<Value>E</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Total_years_of_employment_Business</Name>"+
						"<Value>01</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>PO_Box_emp</Name>"+
						"<Value>456</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>EmirateCity_emp</Name>"+
						"<Value>Noida</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Country_emp</Name>"+
						"<Value>India</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Gross_Monthly_Salary_income</Name>"+
						"<Value>10000</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Monthly_Expected_Turnover_Cash</Name>"+
						"<Value>7845</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Monthly_Expected_Turnover_Non_Cash</Name>"+
						"<Value>1900</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Mobile_1</Name>"+
						"<Value>9877656621</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Email_Id_1</Name>"+
						"<Value>abc@gmail.com</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>US_Citizenship_Residency</Name>"+
						"<Value>Yes</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>W8Form</Name>"+
						"<Value>yes</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>W9Form</Name>"+
						"<Value>yes</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Country_Of_Birth</Name>"+
						"<Value>India</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>City_of_Birth</Name>"+
						"<Value>Delhi</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Purpose_of_Account</Name>"+
						"<Value>ABCD</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>DEL_CB_DC</Name>"+
						"<Value>Branch</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>CommunicationMode</Name>"+
						"<Value>SMS EMail</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Account_Number</Name>"+
						"<Value>56345721933</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>IBAN</Name>"+
						"<Value>78934623</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Product_Type</Name>"+
						"<Value>salaried</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Product_Currency</Name>"+
						"<Value>INR</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Duplicate_CIF_Found</Name>"+
						"<Value>N</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Blacklist_Found</Name>"+
						"<Value>N</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Risk_Score</Name>"+
						"<Value>23</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Prospect_id</Name>"+
						"<Value>12312315649765</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Sol_Id</Name>"+
						"<Value>033</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Channel</Name>"+
						"<Value>Branch</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>STP_NonSTP</Name>"+
						"<Value>Y</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>investment_portfolio_including_virtual_asset</Name>"+
						"<Value>No</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>income_generated</Name>"+
						"<Value>Yes</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>real_Est_owned</Name>"+
						"<Value>NO</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>rental_income</Name>"+
						"<Value>0</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>title_deed_attached</Name>"+
						"<Value>N</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>other_Source_of_income</Name>"+
						"<Value>No</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Desc_spec_prod_serv_your_cmpny_deals</Name>"+
						"<Value/>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>virtual_Card_issued</Name>"+
						"<Value>Yes</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Pasport_Issuing_Country</Name>"+
						"<Value>ind</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Year_of_Incorporation</Name>"+
						"<Value>2</Value>"+
						"</Attribute>"+
						"</Attributes>"+
						"<UIDDetials>"+
						"<Attribute>"+
						"<Name>Alertdetails</Name>"+
						"<Value>![CDATA[Suspect(s) detected by OFAC-Agent:9"+"\n"+
						 "SystemId:"+"\n"+
						 "Associate:"+"\n"+
						 "============================="+"\n"+
						" Suspect detected #1"+"\n"+

						 "OFAC ID:AS06990130"+"\n"+
						 "MATCH: 775.00"+"\n"+
						 "TAG: NAM"+"\n"+
						 "MATCHINGTEXT: IQBAL, NAVEED,"+"\n"+
						 "RESULT: (0)"+"\n"+
						 "NAME: IQBAL, NAVED"+"\n"+
						 "Synonyms: none"+"\n"+
						 "ADDRESS:"+"\n"+
						 "Synonyms: none"+"\n"+
						 "CITY:"+"\n"+
						 "Synonyms: none"+"\n"+
						 "COUNTRY: INDIA"+"\n"+
						 "Synonyms:"+"\n"+
						 "- BHARAT"+"\n"+
						 "- BHARATIYA GANARAJYA"+"\n"+
						 "- INDE"+"\n"+
						 "- INDIA"+"\n"+
						 "- INDIEN"+"\n"+
						 "- REPUBLIC OF INDIA"+"\n"+
						 "STATE:"+"\n"+
						 "Synonyms: none"+"\n"+
						 "ORIGIN:"+"\n"+
						 "EDD_ASIA_PACIFIC"+"\n"+
						 "DESIGNATION:"+"\n"+
						 "GWL"+"\n"+
						 "TYPE:"+"\n"+
						 "Individual"+"\n"+
						 "SEARCH CODES:"+"\n"+
						 "none"+"\n"+
						 "USER DATA 1:"+"\n"+
						 "none"+"\n"+
						 "USER DATA 2:"+"\n"+
						 "none"+"\n"+
						 "OFFICIAL REF:"+"\n"+
						 "2017-11-07 17:36:06 EDA"+"\n"+
						 "PASSPORT:"+"\n"+
						 "none"+"\n"+
						 "BIC CODES:"+"\n"+
						 "none"+"\n"+
						 "NATID:"+"\n"+
						 "none"+"\n"+
						 "PLACE OF BIRTH:"+"\n"+
						 "none"+"\n"+
						 "DATE OF BIRTH:"+"\n"+
						 "none"+"\n"+
						 "NATIONALITY:"+"\n"+
						 "none"+"\n"+
						 "ADDITIONAL INFOS:"+"\n"+
						 "List ID: 1106 / Create Date: 11/07/2017 17:36:06 / Last Update Date:"+"\n"+
						 "11/07/2017 17:36:06 / Org_PID: 8523805 / Title: ARRESTED FOR"+"\n"+
						 "BURGLARY - OCTOBER 30, 2017 / Gender: MALE / OtherInformation:"+"\n"+
						 "According to the timesofindia.indiatimes.com; October 30, 2017: On"+"\n"+
						 "October 30, 2017, Naved Iqbal was arrested for burglary. He and his"+"\n"+
						 "co-conspirators were arrested while planning to commit a robbery in"+"\n"+
						 "Khatauli region of the district. They were involved / Relationship:"+"\n"+
						 "Co-Defendant / OriginalID: 8523809"+"\n"+
						 "FML TYPE:"+"\n"+
						 "1"+"\n"+
						 "FML PRIORITY:"+"\n"+
						 "0"+"\n"+
						 "FML CONFIDENTIALITY:"+"\n"+
						 "0"+"\n"+
						 "FML INFO:"+"\n"+
						 "none"+"\n"+
						 "PEP-FEP:"+"\n"+
						 "0 0"+"\n"+
						 "KEYWORDS:"+"\n"+
						 "OS:ADVERSE_MEDIA NS:NAMESOURCE_WEBSITE ENTITYLEVEL:LEVEL_NA SC:BURGLARY"+"\n"+
						 "HYPERLINKS:"+"\n"+
						 "https://accuity.worldcompliance.com/signin.aspx?ent=ca870ce6-eaa5-4112-ba57-7b6dbeadf8b4"+"\n"+
						 "TYS: 1"+"\n"+
						 "ISN: 0</Value>"+"\n"+
						"</Attribute>"+
						"</UIDDetials>"+
						"<FATCA_CRS_GRID_details>"+
						"<Attribute>"+
						"<Name>Country_Tax_Residence</Name>"+
						"<Value>India</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>TIN</Name>"+
						"<Value>DB1234</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Reason_unable_tin</Name>"+
						"<Value>None</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>future_Free_field_1</Name>"+
						"<Value>free1</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>future_Free_field_2</Name>"+
						"<Value>free2</Value>"+
						"</Attribute>"+
						"</FATCA_CRS_GRID_details>"+
						"<Address_Details>"+
						"<Attribute>"+
						"<Name>Flat_Villa_No</Name>"+
						"<Value>789</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Building_Villa_Name</Name>"+
						"<Value>Tower A</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Street_Location</Name>"+
						"<Value>XYZ Rd</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Nearest_landmark</Name>"+
						"<Value>PQR Hotel</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>PO_Box_address</Name>"+
						"<Value>567</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Emirate_City_address</Name>"+
						"<Value>Delhi</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Country_address</Name>"+
						"<Value>India</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Address_type</Name>"+
						"<Value>Residence</Value>"+
						"</Attribute>"+
						"</Address_Details>"+
						"<Background_info_employer>"+
						"<Attribute>"+
						"<Name>Employer_Name</Name>"+
						"<Value></Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>other_employer</Name>"+
						"<Value>other</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Position_held</Name>"+
						"<Value>PM</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>country</Name>"+
						"<Value>India</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>dateofemployemnt</Name>"+
						"<Value>01/06/2012</Value>"+
						"</Attribute>"+
						"</Background_info_employer>"+
						"<company_Details>"+
						"<Attribute>"+
						"<Name>company_name</Name>"+
						"<Value>RAK</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Business_act_as_TL</Name>"+
						"<Value>dfsd</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Industry</Name>"+
						"<Value>edu</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>years_of_incorp</Name>"+
						"<Value>5</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>country_of_incorp</Name>"+
						"<Value>5</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>percent_sharehold_in_cmpny</Name>"+
						"<Value>26</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>annual_turnover</Name>"+
						"<Value>20000</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>Annual_profit</Name>"+
						"<Value>12345</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>trade_license</Name>"+
						"<Value>asdfsad</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>countries_W_business_conducted_1</Name>"+
						"<Value>Ind</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>countries_W_business_conducted_2</Name>"+
						"<Value>AE</Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>countries_W_business_conducted_3</Name>"+
						"<Value></Value>"+
						"</Attribute>"+
						"<Attribute>"+
						"<Name>countries_W_business_conducted_4</Name>"+
						"<Value></Value>"+
						"</Attribute>"+
						"</company_Details>"+
						"</CreateWorkitemReq>"+
						"</EE_EAI_MESSAGE>";

						
						
						

		//For CDOB workitem status check
			/*String message="<EE_EAI_HEADER>" +
			"<MsgFormat>STATUS_SR</MsgFormat>" +
			"<MsgVersion>0000</MsgVersion>" +
			"<RequestorChannelId>DEH</RequestorChannelId>" +
			"<RequestorUserId>RAKUSER</RequestorUserId>" +
			"<RequestorLanguage>E</RequestorLanguage>" +
			"<RequestorSecurityInfo>secure</RequestorSecurityInfo>" +
			"<ReturnCode>0000</ReturnCode>" +
			"<ReturnDesc>Success</ReturnDesc>" +
			"<MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>" +
			"<Extra1>REQ ||IB.123</Extra1>" +
			"<Extra2>2011-02-08T15:31:38.818+05:30</Extra2>" +
		"</EE_EAI_HEADER>" +
		"<GetWorkitemStatusReq>" +
			"<ProcessName>CDOB</ProcessName>" +
			"<SubProcess>Status_Enquire</SubProcess>" +
			"<Attributes>" +
				"<Attribute>" +
					"<Name>WINumber</Name>" +
					"<Value>CDOB-0000001212-process</Value>" +
				"</Attribute>" +
					"<Name>PortalReferenceNumber</Name>" +
					"<Value>DEH-12345</Value>" +
				"</Attribute>" +
			"</Attributes>" +
		"</GetWorkitemStatusReq>" +
		"</EE_EAI_MESSAGE>";*/
		
		
	/*String message="<EE_EAI_MESSAGE>" +
				"<EE_EAI_HEADER>" +
					"<MsgFormat>UPDATE_SR</MsgFormat>" +
					"<MsgVersion>0000</MsgVersion>" +
					"<RequestorChannelId>DEH</RequestorChannelId>" +
					"<RequestorUserId>RAKUSER</RequestorUserId>" +
					"<RequestorLanguage>E</RequestorLanguage>" +
					"<RequestorSecurityInfo>secure</RequestorSecurityInfo>" +
					"<ReturnCode>0000</ReturnCode>" +
					"<ReturnDesc>Success</ReturnDesc>" +
					"<MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>" +
					"<Extra1>REQ ||IB.123</Extra1>" +
					"<Extra2>2011-02-08T15:31:38.818+05:30</Extra2>" +
				"</EE_EAI_HEADER>" +
				"<UpdateWorkitemReq>" +
					"<ProcessName>CDOB</ProcessName>" +
					"<SubProcess></SubProcess>" +
					"<Attributes>" +
						"<Attribute>" +
							"<Name>OperationName</Name>" +
							"<Value>DataUpdate</Value>" +
						"</Attribute>" +
						"<Attribute>"+
							"<Name>IsDoneRequired</Name>"+
							"<Value>TRUE</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>IsDocsPresent</Name>"+
							"<Value>FALSE</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>WINumber</Name>"+
							"<Value>CDOB-0000001154-process</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Iban_number</Name>"+
							"<Value>AE830040000800014829100</Value>"+
						"</Attribute>"+
					"<Attribute>"+
						"<Name>PortalReferenceNumber</Name>"+
						"<Value>15485</Value>"+
					"</Attribute>"+
						"<Attribute>"+
							"<Name>BankName</Name>"+
							"<Value>ABNAMR</Value>"+
						"</Attribute>"+
					"</Attributes>"+
				"</UpdateWorkitemReq>"+
			"</EE_EAI_MESSAGE>";*/
		
		
		// RAOP Update Workitem
	/*	String message = "<EE_EAI_MESSAGE>"+
		"<EE_EAI_HEADER>"+
		"<MsgFormat>UPDATE_SR</MsgFormat>"+
		"<MsgVersion>0000</MsgVersion>"+
		"<RequestorChannelId>API.YAP</RequestorChannelId>"+
		"<RequestorUserId>RAKUSER</RequestorUserId>"+
		"<RequestorLanguage>E</RequestorLanguage>"+
		"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
		"<ReturnCode>9999</ReturnCode>"+
		"<ReturnDesc>Request</ReturnDesc>"+
		"<MessageId>Ab026s23</MessageId>"+
		"<Extra1/>"+
		"<Extra2/>"+
		"</EE_EAI_HEADER>"+
		"<UpdateWorkitemReq>"+
		"<ProcessName>RAOP</ProcessName>"+
		"<SubProcess>API.YAP</SubProcess>"+
		"<Attributes>"+
		"<Attribute>"+
		"<Name>WINUMBER</Name>"+
		"<Value>RAOP-0000010354-Process</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>CUSTOMERAUTHENTICATED</Name>"+
		"<Value>Y</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>MARITALSTATUS</Name>"+
		"<Value>M</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>EMPLOYMENTTYPE</Name>"+
		"<Value>A</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>EMPLOYERNAME</Name>"+
		"<Value>ABC Company</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>OCCUPATION</Name>"+
		"<Value>11</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>INDUSTRYSEGMENT</Name>"+
		"<Value>A</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>INDUSTRYSUBSEGMENT</Name>"+
		"<Value>KC</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>PASSPORTNUMBER</Name>"+
		"<Value>784456544999</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>PASSPORTISSUEDATE</Name>"+
		"<Value>2019-12-31</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>PASSPORTEXPIRYDATE</Name>"+
		"<Value>2022-12-31</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>VISAFILENUMBER</Name>"+
		"<Value>1234568</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>VISAISSUEDATE</Name>"+
		"<Value>2010-01-01</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>VISAEXPDATE</Name>"+
		"<Value>2019-12-31</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>VISAUIDNUMBER</Name>"+
		"<Value>1234568</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>EXPECTEDMONTHLYCREDITTURNOVERAED</Name>"+
		"<Value>50000</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>MONTHLYCASHCREDITTURNOVERPERCENTAGE</Name>"+
		"<Value>25.5</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>MONTHLYNONCASHCREDITTURNOVERPERCENTAGE</Name>"+
		"<Value>70.0</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>HIGHESTCASHCREDITTRANSACTIONAED</Name>"+
		"<Value>5000.00</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>HIGHESTNONCASHCREDITTRANSACTIONAED</Name>"+
		"<Value>10000.00</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>CITYOFBIRTH</Name>"+
		"<Value>DXB</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>COUNTRYOFBIRTH</Name>"+
		"<Value>PK</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>MONTHLYSALARY</Name>"+
		"<Value>12345</Value>"+
		"</Attribute>"+
		"</Attributes>"+
		"</UpdateWorkitemReq>"+
		"</EE_EAI_MESSAGE>";*/
		
		//For DAC process MONTHLYSALARY
		//String message = "<EE_EAI_MESSAGE><EE_EAI_HEADER><MsgFormat>CUSTOMER_SR</MsgFormat><MsgVersion>0000</MsgVersion><RequestorChannelId>IBPS</RequestorChannelId><RequestorUserId>RAKUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>Success</ReturnDesc><MessageId>msg46738</MessageId><Extra1>REQ || DAC.123</Extra1><Extra2>2011-02-08T15:31:38.818+05:30</Extra2></EE_EAI_HEADER><CreateWorkitemReq><ProcessName>DAC</ProcessName><SubProcess>DAC</SubProcess><InitiateAlso>Y</InitiateAlso><Documents></Documents><Attributes><Attribute><Name>CHANNEL</Name><Value>DAC</Value></Attribute><Attribute><Name>FIRSTNAME</Name><Value>Angad</Value></Attribute><Attribute><Name>MIDDLENAME</Name><Value>Kumar</Value></Attribute><Attribute><Name>LASTNAME</Name><Value>Shah</Value></Attribute><Attribute><Name>EMIRATESID</Name><Value>784190179745126</Value></Attribute><Attribute><Name>CORPORATECIF</Name><Value>2432620</Value></Attribute><Attribute><Name>MOBILENO</Name><Value>529811166</Value></Attribute><Attribute><Name>MOBILECNTRYCODE</Name><Value>971</Value></Attribute><Attribute><Name>UAERESIDENT</Name><Value>Y</Value></Attribute><Attribute><Name>DATEOFBIRTH</Name><Value>1994-04-03</Value></Attribute><Attribute><Name>GENDER</Name><Value>F</Value></Attribute><Attribute><Name>EMAILID</Name><Value>abc@gmail.com</Value></Attribute><Attribute><Name>PASSPORTNUMBER</Name><Value>P123456</Value></Attribute><Attribute><Name>NATIONALITY</Name><Value>IN</Value></Attribute><Attribute><Name>REQUESTBYCIF</Name><Value>0263401</Value></Attribute><Attribute><Name>REQUESTEDFORCIF</Name><Value>0100623</Value></Attribute><Attribute><Name>TITLEPREFIX</Name><Value>MR</Value></Attribute><Attribute><Name>VISAUIDNUMBER</Name><Value>6665656</Value></Attribute><Attribute><Name>MOTHERSMAIDENNAME</Name><Value>test</Value></Attribute><Attribute><Name>CARDEMBOSSINGNAME</Name><Value>Angad Kumar Shah</Value></Attribute></Attributes></CreateWorkitemReq></EE_EAI_MESSAGE>";
		
		/*String message ="<EE_EAI_MESSAGE>"+
				"<EE_EAI_HEADER>"+
				"<MsgFormat>CUSTOMER_SR</MsgFormat>"+
				"<MsgVersion>0000</MsgVersion>"+
				"<RequestorChannelId>EBC.WBA</RequestorChannelId>"+
				"<RequestorUserId>RAKUSER</RequestorUserId>"+
				"<RequestorLanguage>E</RequestorLanguage>"+
				"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
				"<ReturnCode>0000</ReturnCode>"+
				"<ReturnDesc>0000</ReturnDesc>"+
				"<MessageId>RPAKCK4V1584273987596</MessageId>"+
				"<Extra1>REQ||RAK.VTUSER</Extra1>"+
				"<Extra2>2020-03-15T16:06:27.596</Extra2>"+
				"</EE_EAI_HEADER>"+
				"<CreateWorkitemReq>"+
				"<ProcessName>BAIS</ProcessName>"+
				"<SubProcess>WBA</SubProcess>"+
				"<InitiateAlso>Y</InitiateAlso>"+
				"<Attributes>"+
				"<Attribute>"+
				"<Name>CHANNEL</Name>"+
				"<Value>WBA</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SOLID</Name>"+
				"<Value>0157</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TLNUMBER</Name>"+
				"<Value>555555555</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RAKTRACKNUMBER</Name>"+
				"<Value>NOLEAD</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ROCODE</Name>"+
				"<Value>ROCode</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ALLDOCUMENTSATTACHED</Name>"+
				"<Value>No</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>FINACLESRNUMBER</Name>"+
				"<Value>SR4459375</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIFID</Name>"+
				"<Value>2768686</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PRIORITY</Name>"+
				"<Value>Starter</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RISKSCORE</Name>"+
				"<Value>4.05</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CHANNELREFERRAL</Name>"+
				"<Value>None</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CUSTOMERNAME</Name>"+
				"<Value>jack</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RMCODE</Name>"+
				"<Value>RMCode</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SMCODE</Name>"+
				"<Value>SMCode</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EMAILID</Name>"+
				"<Value>ttt@tty.com</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>MOBILECNTRYCODE</Name>"+
				"<Value>+971</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>MOBILENO</Name>"+
				"<Value>565658941</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DNFBP</Name>"+
				"<Value>NA</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NOOFDOCUMENTS</Name>"+
				"<Value>2</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SALESAGENTINITIATED</Name>"+
				"<Value>angad</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EXPRESSCONSENT</Name>"+
				"<Value>Yes</Value>"+
				"</Attribute>"+
				"</Attributes>"+
				"</CreateWorkitemReq>"+
				"</EE_EAI_MESSAGE>";*/
		
		/*String message = "<EE_EAI_MESSAGE>"+
				"  <EE_EAI_HEADER>"+
				"    <MsgFormat>CUSTOMER_SR</MsgFormat>"+
				"    <MsgVersion>0000</MsgVersion>"+
				"    <RequestorChannelId>EBC.WBA</RequestorChannelId>"+
				"    <RequestorUserId>RAKUSER</RequestorUserId>"+
				"    <RequestorLanguage>E</RequestorLanguage>"+
				"    <RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
				"    <ReturnCode>0000</ReturnCode>"+
				"    <ReturnDesc>0000</ReturnDesc>"+
				"    <MessageId>RPAIF0MZ1585847605871</MessageId>"+
				"    <Extra1>REQ||RAK.VTUSER</Extra1>"+
				"    <Extra2>2020-04-02T21:13:25.875</Extra2>"+
				"  </EE_EAI_HEADER>"+
				"  <CreateWorkitemReq>"+
				"    <ProcessName>DOB</ProcessName>"+
				"    <SubProcess>EBC.WBA</SubProcess>"+
				"    <InitiateAlso>Y</InitiateAlso>"+
				"    <Attributes>"+
				"      <Attribute>"+
				"        <Name>Product_Type</Name>"+
				"        <Value>C-FALCON WORLD ELITE</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>MobileNo_pri</Name>"+
				"        <Value>00971509203039</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>Title</Name>"+
				"        <Value>MR.</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>FirstName</Name>"+
				"        <Value>Amy</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>LastName</Name>"+
				"        <Value>Baird</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>dob</Name>"+
				"        <Value>1985-07-08</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>Nationality</Name>"+
				"        <Value>IN</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>PAssportNo</Name>"+
				"        <Value>IN9205642</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>Email_ID</Name>"+
				"        <Value>XCVG55@GMAIL.COM</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>EmiratesID</Name>"+
				"        <Value>784198509205642</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>Designation</Name>"+
				"        <Value>ACCT</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>Basic_Sal</Name>"+
				"        <Value>60000</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>EmpCode</Name>"+
				"        <Value>929</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>EmpName</Name>"+
				"        <Value>RAK CEMENT CO</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>Emp_Type</Name>"+
				"        <Value>Salaried</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>Aecb_Consent_Received</Name>"+
				"        <Value>True</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>NameOnCard</Name>"+
				"        <Value>VIPUL</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>Iban_number</Name>"+
				"        <Value>AE160030010588175132001</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>isNTB</Name>"+
				"        <Value>true</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>EmirateOfVisa</Name>"+
				"        <Value>DXB</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>BankName</Name>"+
				"        <Value>ADCB</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>FTS_Consent</Name>"+
				"        <Value>True</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>IndusSeg</Name>"+
				"        <Value>CEMENT</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>emirateOfWork</Name>"+
				"        <Value>DXB</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>ReferenceName</Name>"+
				"        <Value>SINGHA</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>ReferenceMobile</Name>"+
				"        <Value>0579884710</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>PortalApplicationNumber</Name>"+
				"        <Value>204</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>ApprovedLimit</Name>"+
				"        <Value>200000.0</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>Mothersname</Name>"+
				"        <Value>MAAAAA</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>FinalDBR</Name>"+
				"        <Value>10.4</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>FinalTAI</Name>"+
				"        <Value>60000</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>EligibleCardProduct</Name>"+
				"        <Value>Credit Card|Salaried Credit Card||200000.00||A||A999-System Approve|SR. MGR|0~Credit Card|Salaried Credit Card||200000.00||A||A999-System Approve|SR. MGR|0</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>MaritalStatus</Name>"+
				"        <Value>O</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>EmploymentStatus</Name>"+
				"        <Value>2</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>YearsInCurrAddress</Name>"+
				"        <Value>1</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>EStatementFlag</Name>"+
				"        <Value>Y</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>HomeCountryHouseNo</Name>"+
				"        <Value>KJSVGIUS</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>HomeCountryBuildingName</Name>"+
				"        <Value>UIAGVEEBRFISSUAW</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>HomeCountryStreetName</Name>"+
				"        <Value>AKHUFOSSEU</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>HomeCountryNo</Name>"+
				"        <Value>009851687964596</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>CountryOfBirth</Name>"+
				"        <Value>IN</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>CityOfBirth</Name>"+
				"        <Value>PATNA</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>CountryOfTaxResidence</Name>"+
				"        <Value>IN</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>isTinAvailable</Name>"+
				"        <Value>false</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>TinReason</Name>"+
				"        <Value>B-UNABLE GET TIN</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>emirateOfResidence</Name>"+
				"        <Value>DXB</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>ResidenceAddressHouseNo</Name>"+
				"        <Value>GWSR</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>ResidenceAddressBuildingName</Name>"+
				"        <Value>JHJIAD</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>ResidenceAddressStreetName</Name>"+
				"        <Value>SUJHOVIUWS</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>RESIDENCEADDRESSPOBOX</Name>"+
				"        <Value>121212</Value>"+
				"      </Attribute>"+
				"      <Attribute>"+
				"        <Name>OFFICEADDRESSPOBOX</Name>"+
				"        <Value>123456</Value>"+
				"      </Attribute>"+
				"    </Attributes>"+
				"  </CreateWorkitemReq>"+
				"</EE_EAI_MESSAGE>"; */
		
		//String message = "<EE_EAI_MESSAGE><EE_EAI_HEADER><MsgFormat>CUSTOMER_SR</MsgFormat><MsgVersion>0000</MsgVersion><RequestorChannelId>YAP</RequestorChannelId><RequestorUserId>RAKUSER</RequestorUserId><RequestorLanguage>E</RequestorLanguage><RequestorSecurityInfo>secure</RequestorSecurityInfo><ReturnCode>0000</ReturnCode><ReturnDesc>Success</ReturnDesc><MessageId>test123456</MessageId><Extra1>REQ || YAP.123</Extra1><Extra2>2011-02-08T15:31:38.818+05:30</Extra2></EE_EAI_HEADER><CreateWorkitemReq><ProcessName>TT</ProcessName><SubProcess>YAP</SubProcess><InitiateAlso>Y</InitiateAlso><Documents /><Attributes><Attribute><Name>CHANNEL</Name><Value>YAP</Value></Attribute><Attribute><Name>PAYMENTORDERID</Name><Value>000023534641</Value></Attribute><Attribute><Name>DEBITACNUMBER</Name><Value>0302269347001</Value></Attribute><Attribute><Name>TRANSFERAMOUNT</Name><Value>8000.00</Value></Attribute><Attribute><Name>TRANSFERCURRENCY</Name><Value>AED</Value></Attribute><Attribute><Name>EXCHANGERATE</Name><Value>0.0530570000</Value></Attribute><Attribute><Name>REMITTINGCURRENCY</Name><Value>INR</Value></Attribute><Attribute><Name>CHARGES</Name><Value>OUR</Value></Attribute><Attribute><Name>TRANSACTIONTYPE</Name><Value>Individual</Value></Attribute><Attribute><Name>TRANSACTIONCODE</Name><Value>FAM</Value></Attribute><Attribute><Name>PURPOSEOFPAYMENT1</Name><Value>Family Support</Value></Attribute><Attribute><Name>PURPOSEOFPAYMENT2</Name><Value>Family Support2</Value></Attribute><Attribute><Name>PURPOSEOFPAYMENT3</Name><Value>Family Support3</Value></Attribute><Attribute><Name>BENEFICIARYNAME1</Name><Value>Betsy Narayan</Value></Attribute><Attribute><Name>BENEFICIARYCITY</Name><Value>Dubai</Value></Attribute><Attribute><Name>BENEFICIARYCOUNTRYOFRESIDENCEINCORPORATION</Name><Value>India</Value></Attribute><Attribute><Name>ACCOUNTNOIBAN</Name><Value>11111111111111111111</Value></Attribute><Attribute><Name>BENEFICIARYBANKNAME</Name><Value>RAK Bank</Value></Attribute><Attribute><Name>BENEFICIARYBANKBRANCH</Name><Value>RAK</Value></Attribute><Attribute><Name>BENEFICIARYBANKCITYSTATE</Name><Value>Dubai</Value></Attribute><Attribute><Name>BENEFICIARYBANKCOUNTRY</Name><Value>India</Value></Attribute><Attribute><Name>INTERMEDIARYBANKNAME</Name><Value>Standard Chartered</Value></Attribute><Attribute><Name>INTERMEDIARYBANKCOUNTRY</Name><Value>United Arab Emirates</Value></Attribute><Attribute><Name>INTERMEDIARYBANKBRANCH</Name><Value>BurDubai</Value></Attribute><Attribute><Name>INTERMEDIARYBANKCITYSTATE</Name><Value>Dubai</Value></Attribute><Attribute><Name>BENEFICIARYBANKCODETYPE</Name><Value>SWIFT Code</Value></Attribute><Attribute><Name>BENEFICIARYBANKCODEVALUE</Name><Value>UIBXX001</Value></Attribute><Attribute><Name>INTERMEDIARYBANKCODETYPE</Name><Value>IFSC Code</Value></Attribute><Attribute><Name>INTERMEDIARYBANKCODEVALUE</Name><Value>UIWW001</Value></Attribute></Attributes></CreateWorkitemReq></EE_EAI_MESSAGE>";
		
		/*String message = "<EE_EAI_MESSAGE>"+
				"<EE_EAI_HEADER>"+
				"<MsgFormat>UPDATE_SR</MsgFormat>"+
				"<MsgVersion>0000</MsgVersion>"+
				"<RequestorChannelId>API.YAP.AddInfo</RequestorChannelId>"+
				"<RequestorUserId>RAKUSER</RequestorUserId>"+
				"<RequestorLanguage>E</RequestorLanguage>"+
				"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
				"<ReturnCode>9999</ReturnCode>"+
				"<ReturnDesc>Request</ReturnDesc>"+
				"<MessageId>Ab026s23</MessageId>"+
				"<Extra1/>"+
				"<Extra2/>"+
				"</EE_EAI_HEADER>"+
				"<UpdateWorkitemReq>"+
				"<ProcessName>RAOP</ProcessName>"+
				"<SubProcess>API.YAP.AddInfo</SubProcess>"+
				"<Attributes>"+
				"<Attribute>"+
				"<Name>WINUMBER</Name>"+
				"<Value>RAOP-0000010058-Process</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISDOCPROVIDED</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>REMARKS</Name>"+
				"<Value>Required informations are provided</Value>"+
				"</Attribute>"+
				"</UpdateWorkitemReq>"+
				"</EE_EAI_MESSAGE>";*/
		
		/*String message = "<EE_EAI_MESSAGE>"+
		"<EE_EAI_HEADER>"+
		"<MsgFormat>UPDATE_SR</MsgFormat>"+
		"<MsgVersion>0000</MsgVersion>"+
		"<RequestorChannelId>API.YAP</RequestorChannelId>"+
		"<RequestorUserId>RAKUSER</RequestorUserId>"+
		"<RequestorLanguage>E</RequestorLanguage>"+
		"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
		"<ReturnCode>9999</ReturnCode>"+
		"<ReturnDesc>Request</ReturnDesc>"+
		"<MessageId>Ab026s23</MessageId>"+
		"<Extra1/>"+
		"<Extra2/>"+
		"</EE_EAI_HEADER>"+
		"<UpdateWorkitemReq>"+
		"<ProcessName>RAOP</ProcessName>"+
		"<SubProcess>API.YAP</SubProcess>"+
		"<Attributes>"+
		"<Attribute>"+
		"<Name>WINUMBER</Name>"+
		"<Value>RAOP-00000100190-Process</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>CUSTOMERAUTHENTICATED</Name>"+
		"<Value>N</Value>"+
		"</Attribute>"+
		"</Attributes>"+
		"</UpdateWorkitemReq>"+
		"</EE_EAI_MESSAGE>";*/
		
		// OECD
		/*String message1 = "<EE_EAI_HEADER>"+
				"<EE_EAI_HEADER>"+
				 "<MsgFormat>CREATE_WORKITEM</MsgFormat>"+
				 "<MsgVersion>0000</MsgVersion>"+
				 "<RequestorChannelId>IB</RequestorChannelId>"+
				 "<RequestorUserId>RAKUSER</RequestorUserId>"+
				 "<RequestorLanguage>E</RequestorLanguage>"+
				 "<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
				 "<ReturnCode>0000</ReturnCode>"+
				 "<ReturnDesc>Success</ReturnDesc>"+
				 "<MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>"+
				 "<Extra1>REQ||VIKASRET.VIKASRET</Extra1>"+
				 "<Extra2>2011-02-08T15:31:38.818+05:30(Requested Date)</Extra2>"+
			"</EE_EAI_HEADER>"+
			"<CreateWorkitemReq>"+
				"<ProcessName>OECD</ProcessName>"+
				"<SubProcess></SubProcess>"+
				"<InitiateAlso>Y</InitiateAlso>"+
				"<Documents>"+
				"</Documents>"+
				"<Attributes>"+
					"<Attribute>"+
						"<Name>FINACLESRNUMBER</Name>"+
						"<Value>SR123456</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>CHANNEL</Name>"+
						"<Value>EBC.IB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>INITIATORCIFNUMBER</Name>"+
						"<Value>0263401</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>INITIATORNAME</Name>"+
						"<Value>Ankur Jha</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>SUBMISSIONMODE</Name>"+
						"<Value>Online</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>CIFNUMBER</Name>"+
						"<Value>0263401</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RMCODE</Name>"+
						"<Value>xsangad</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>CIFTYPE</Name>"+
						"<Value>R</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>NAME</Name>"+
						"<Value>Amit Mishra</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>SEGMENT</Name>"+
						"<Value>PBD</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>SUBSEGMENT</Name>"+
						"<Value>PBN</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>NATIONALITY</Name>"+
						"<Value>AE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>DATEOFBIRTH</Name>"+
						"<Value>1990-02-26</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>COUNTRYOFINCORPORATION</Name>"+
						"<Value>AE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>NFETYPE</Name>"+
						"<Value>Active NFE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>PREFERREDADDRESS</Name>"+
						"<Value>test1</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>PERMANENTADDRLINE1</Name>"+
						"<Value>612</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>PERMANENTADDRLINE2</Name>"+
						"<Value>Mayur Vihar</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>PERMANENTADDRPOBOX</Name>"+
						"<Value>4534</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>PERMANENTADDRCITY</Name>"+
						"<Value>DEL</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>PERMANENTADDRSTATE</Name>"+
						"<Value>DEL</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>PERMANENTADDRCOUNTRY</Name>"+
						"<Value>IN</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRLINE1</Name>"+
						"<Value>611</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRLINE2</Name>"+
						"<Value>S H 1, DSO</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRPOBOX</Name>"+
						"<Value>54343</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRCITY</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRSTATE</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRCOUNTRY</Name>"+
						"<Value>AE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>USRELATIONMAIN</Name>"+
						"<Value>O</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>FINANCIALENTITY</Name>"+
						"<Value>LOCAL FINANCIAL ENTITY</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>GIIN</Name>"+
						"<Value>3467345345</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>FATCAENTITYTYPE</Name>"+
						"<Value>Corporation</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>NAMEOFSECURITYMARKET</Name>"+
						"<Value>Name Secure</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>NAMETRADEDCORPORATION</Name>"+
						"<Value>Name Trade</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>CONTROLLINGPERSONUSRELATIONSHIP</Name>"+
						"<Value>Yes</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>CITYOFBIRTH</Name>"+
						"<Value>Delhi</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>COUNTRYOFBIRTH</Name>"+
						"<Value>IN</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>CRSUNDOCUMENTEDFLAG</Name>"+
						"<Value>N</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>REPORTCOUNTRYDETAILS</Name>"+
						"<Value>US~123445~~| AE~~Reason B~No tax number | IN~ABC12345~~</Value>"+
					"</Attribute>"+
				"</Attributes>"+
				"<RelatedPartyDetails>"+	
					"<Attribute>"+
						"<Name>CIFID</Name>"+
						"<Value></Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>FINANCIALDETAILID</Name>"+
						"<Value>234567890</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>NAME</Name>"+
						"<Value>Rel Non 1</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RELATIONSHIPTYPE</Name>"+
						"<Value>Sole</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRLINE1</Name>"+
						"<Value>613</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRLINE2</Name>"+
						"<Value>DSO</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRPOBOX</Name>"+
						"<Value>354334</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRCITY</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRCOUNTRY</Name>"+
						"<Value>AE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRLINE1</Name>"+
						"<Value>614</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRLINE2</Name>"+
						"<Value>DSO</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRPOBOX</Name>"+
						"<Value>45645</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRCITY</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRSTATE</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRCOUNTRY</Name>"+
						"<Value>AE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>USRELATION</Name>"+
						"<Value>O</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>CONTROLLINGPERSONTYPE</Name>"+
						"<Value>Legal Person-Ownership</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>ReportCountryDetails</Name>"+
						"<Value>AE~45645~~| US~~Reason B~No tax number | IN~ABC1222345~~</Value>"+
					"</Attribute>"+
				"</RelatedPartyDetails>"+
				"<RelatedPartyDetails>"+	
					"<Attribute>"+
						"<Name>CIFID</Name>"+
						"<Value>9874561</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>FINANCIALDETAILID</Name>"+
						"<Value></Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>NAME</Name>"+
						"<Value>Rel CIF 1</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RELATIONSHIPTYPE</Name>"+
						"<Value>Any of us</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRLINE1</Name>"+
						"<Value>615</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRLINE2</Name>"+
						"<Value>DSO1</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRPOBOX</Name>"+
						"<Value>5645</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRCITY</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRCOUNTRY</Name>"+
						"<Value>AE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRLINE1</Name>"+
						"<Value>616</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRLINE2</Name>"+
						"<Value>DSO2</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRPOBOX</Name>"+
						"<Value>433434</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRCITY</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRSTATE</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRCOUNTRY</Name>"+
						"<Value>AE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>USRELATION</Name>"+
						"<Value>O</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>CONTROLLINGPERSONTYPE</Name>"+
						"<Value>Legal Person-Other Means</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>ReportCountryDetails</Name>"+
						"<Value>PK~12333445~~| IN~~Reason B~No tax number | AE~ABC1233346~~</Value>"+
					"</Attribute>"+
				"</RelatedPartyDetails>"+
				"<RelatedPartyDetails>"+	
					"<Attribute>"+
						"<Name>CIFID</Name>"+
						"<Value>7292298</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>FINANCIALDETAILID</Name>"+
						"<Value></Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>NAME</Name>"+
						"<Value>Rel CIF 2</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RELATIONSHIPTYPE</Name>"+
						"<Value>Sole</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRLINE1</Name>"+
						"<Value>233</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRLINE2</Name>"+
						"<Value>Al Nahda</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRPOBOX</Name>"+
						"<Value>4334</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRCITY</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRCOUNTRY</Name>"+
						"<Value>AE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRLINE1</Name>"+
						"<Value>645</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRLINE2</Name>"+
						"<Value>DSO9</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRPOBOX</Name>"+
						"<Value>32233</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRCITY</Name>"+
						"<Value>SHA</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRSTATE</Name>"+
						"<Value>SHA</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRCOUNTRY</Name>"+
						"<Value>AE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>USRELATION</Name>"+
						"<Value>N</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>CONTROLLINGPERSONTYPE</Name>"+
						"<Value>Legal Arrangement-Trust-Trustee</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>ReportCountryDetails</Name>"+
						"<Value>UK~12333445~~| AS~~Reason B~No tax number | AE~ABC14542340~~</Value>"+
					"</Attribute>"+
				"</RelatedPartyDetails>"+
				"<RelatedPartyDetails>"+	
					"<Attribute>"+
						"<Name>CIFID</Name>"+
						"<Value></Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>FINANCIALDETAILID</Name>"+
						"<Value>57896541</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>NAME</Name>"+
						"<Value>Non Rel CIF 2</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RELATIONSHIPTYPE</Name>"+
						"<Value>POA</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRLINE1</Name>"+
						"<Value>3453</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRLINE2</Name>"+
						"<Value>Burj</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRPOBOX</Name>"+
						"<Value>4564</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRCITY</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>RESIDENCEADDRCOUNTRY</Name>"+
						"<Value>AE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRLINE1</Name>"+
						"<Value>4564</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRLINE2</Name>"+
						"<Value>Dubai Land</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRPOBOX</Name>"+
						"<Value>456224</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRCITY</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRSTATE</Name>"+
						"<Value>DXB</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>MAILINGADDRCOUNTRY</Name>"+
						"<Value>AE</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>USRELATION</Name>"+
						"<Value>N</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>CONTROLLINGPERSONTYPE</Name>"+
						"<Value>Legal Arrangement-Protector</Value>"+
					"</Attribute>"+
					"<Attribute>"+
						"<Name>ReportCountryDetails</Name>"+
						"<Value>OM~12334445~~| BD~~Reason B~No tax number | NP~ABC1442347~~</Value>"+
					"</Attribute>"+
				"</RelatedPartyDetails>"+
			"</CreateWorkitemReq>"+
		"</EE_EAI_HEADER>";*/
		
		/*String message1 = "<EE_EAI_MESSAGE>"+
				"<EE_EAI_HEADER>"+
				"<MsgFormat>CUSTOMER_SR</MsgFormat>"+
				"<MsgVersion>0000</MsgVersion>"+
				"<RequestorChannelId>EBC.WBA</RequestorChannelId>"+
				"<RequestorUserId>RAKUSER</RequestorUserId>"+
				"<RequestorLanguage>E</RequestorLanguage>"+
				"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
				"<ReturnCode>0000</ReturnCode>"+
				"<ReturnDesc>0000</ReturnDesc>"+
				"<MessageId>RPAOKNJG1637733395645</MessageId>"+
				"<Extra1>REQ||RAK.RBLRO03</Extra1>"+
				"<Extra2>2021-11-24T09:56:35.652+05:30</Extra2>"+
				"</EE_EAI_HEADER>"+
				"<CreateWorkitemReq>"+
				"<ProcessName>iRBL</ProcessName>"+
				"<SubProcess>WBA</SubProcess>"+
				"<InitiateAlso>Y</InitiateAlso>"+
				"<Attributes>"+
				"<Attribute>"+
				"<Name>NAMEOFCOMPANY</Name>"+
				"<Value>ABCDEFG</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>FULLNAMEOFAPPLICANT</Name>"+
				"<Value>Tribes of Andaman and Nicobar Islands</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>MOBILENUMBERCOUNTRYCODE</Name>"+
				"<Value>971</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>MOBILENUMBER</Name>"+
				"<Value>399998888</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EMAILID</Name>"+
				"<Value>test11@rakbanktst.ae</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE1</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TLISSUEDATE</Name>"+
				"<Value>1990-01-01</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>BUSINESSCONSTITUTION</Name>"+
				"<Value>PART</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>VATEXEMPTIONFLAG</Name>"+
				"<Value>no</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NUMBEROFEMPLOYEES</Name>"+
				"<Value>25</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERCENTAGEWHOLESALE</Name>"+
				"<Value>50</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCITY</Name>"+
				"<Value>AAN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCOUNTRYCODE</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>INDUSTRYSTATUS</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RAKTRACKNUMBER</Name>"+
				"<Value>0aa2e8</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFFINANCE</Name>"+
				"<Value>RBL</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PRIORITYLOAN</Name>"+
				"<Value>Standard</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PRIORITY</Name>"+
				"<Value>Standard</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIFNUMBER</Name>"+
				"<Value>0259491</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NTBEXISTING</Name>"+
				"<Value>Existing</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CONVENTIONALISLAMIC</Name>"+
				"<Value>conventional</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RISKSCORE</Name>"+
				"<Value>1.7</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PEPSTATUS</Name>"+
				"<Value>NPEP</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TLNUMBER</Name>"+
				"<Value>A2N5452121</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TLVALIDTILL</Name>"+
				"<Value>2023-11-22</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISSUINGEMIRATE</Name>"+
				"<Value>28</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>FINACLESRNUMBER</Name>"+
				"<Value>SR4992552</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>IFNEWORTOPUP</Name>"+
				"<Value>New</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CHANNELSOURCE</Name>"+
				"<Value>Ineligible</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CHANNEL</Name>"+
				"<Value>WBA</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RO</Name>"+
				"<Value>rouser1</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RAKTRACKNUMBERBAIS</Name>"+
				"<Value>0aa2e8</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>WPSROUTEDFROM</Name>"+
				"<Value>Exchange House</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>OWNERSHIPOFPREMISES</Name>"+
				"<Value>T1</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFPREMISES</Name>"+
				"<Value>2</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TOTALELIGIBILITYAMOUNT</Name>"+
				"<Value>100000</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PROPOSEDTENOR</Name>"+
				"<Value>60</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>INTEREST</Name>"+
				"<Value>18</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PROCESSINGFEE</Name>"+
				"<Value>2.5</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREMISESEMIRATE</Name>"+
				"<Value>UAE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERCENTAGEANNUALSALES</Name>"+
				"<Value>0</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERCENTAGELOCALINTERNATIONAL</Name>"+
				"<Value>100</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERCENTAGERETAIL</Name>"+
				"<Value>50</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COUNTRYOFINCORPORATION</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DATEOFINCORPORATION</Name>"+
				"<Value>1990-01-01</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>REQUESTEDLOANAMOUNT</Name>"+
				"<Value>4000000</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TESSLOANOUTSNDRAK</Name>"+
				"<Value>34323</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NOOFEMIREPAIDTESSLOAN</Name>"+
				"<Value>11</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PRODUCTTYPE</Name>"+
				"<Value>conventional</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PRODUCTCURRENCY</Name>"+
				"<Value>AED</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIFSTATUS</Name>"+
				"<Value>Active</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIFUPDATEREQMAIN</Name>"+
				"<Value>Y</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AEDCURRENCY</Name>"+
				"<Value>AED</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DOCUMENTLIST</Name>"+
				"<Value>AECB_Consent_Form_Company|VAT_Return|AECB_Consent_Form_Others|CV_Personal_Background|Constitutional_Docs|Passport_Visa_EID|TL_On_Website|Telephonebill_Tenancycontract_WPS_Proofofstaff|KYC|BVR_RM|Risk_Score|Account_Opening_Application|BVR_Annexure_Form</Value>"+
				"</Attribute>"+
				"</Attributes>"+
				"<AdditionalAttributes>"+
				"<Attribute>"+
				"<Name>WHATSTHEREASON</Name>"+
				"<Value>100000</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DNFBPSTATUS</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PARTNERCODE</Name>"+
				"<Value>9160</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PROMOCODE</Name>"+
				"<Value>ABCD1234</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>USRELATION</Name>"+
				"<Value>Y</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AECBCONSENTSTATUSMAIN</Name>"+
				"<Value>Digital</Value>"+
				"</Attribute>"+
				"</AdditionalAttributes>"+
				"<FinancialEligibilityCheckDetails>"+
				"<Attribute>"+
				"<Name>TURNOVERAMOUNT</Name>"+
				"<Value>100000000.00</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TURNOVERTIMEPERIOD</Name>"+
				"<Value>12</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TURNOVERFROM</Name>"+
				"<Value>2020-11-23</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TURNOVERTO</Name>"+
				"<Value>2021-11-24</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TURNOVERDATEOFCHECK</Name>"+
				"<Value>2021-11-24</Value>"+
				"</Attribute>"+
				"<Attribute/>"+
				"<Attribute/>"+
				"<Attribute/>"+
				"<Attribute/>"+
				"<Attribute/>"+
				"<Attribute>"+
				"<Name>POSTURNOVERDATEOFCHECK</Name>"+
				"<Value>2021-11-24</Value>"+
				"</Attribute>"+
				"<Attribute/>"+
				"<Attribute/>"+
				"<Attribute>"+
				"<Name>VATTURNOVERDATEOFCHECK</Name>"+
				"<Value>2021-11-24</Value>"+
				"</Attribute>"+
				"<Attribute/>"+
				"<Attribute/>"+
				"<Attribute>"+
				"<Name>VATPERIODDATEOFCHECK</Name>"+
				"<Value>2021-11-24</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NETPROFIT</Name>"+
				"<Value>20.00</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TOTALMONTHLYCREDITS</Name>"+
				"<Value>8333333</Value>"+
				"</Attribute>"+
				"</FinancialEligibilityCheckDetails>"+
				"<IndustryCodeDetails>"+
				"<Attribute>"+
				"<Name>INDUSTRYCODE</Name>"+
				"<Value>MFG</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>INDUSTRYSUBCATEGORY</Name>"+
				"<Value>89</Value>"+
				"</Attribute>"+
				"</IndustryCodeDetails>"+
				"<EUMProposedDetails>"+
				"<Attribute>"+
				"<Name>EUMPROPOSED</Name>"+
				"<Value>Upfront</Value>"+
				"</Attribute>"+
				"</EUMProposedDetails>"+
				"<ConductHistoryRelatedPartiesDetails>"+
				"<Attribute>"+
				"<Name>RELATEDPARTYID</Name>"+
				"<Value>1469045</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COMPANYFLAG</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELATIONSHIPTYPE</Name>"+
				"<Value>Guarantor</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>FIRSTNAME</Name>"+
				"<Value>Nayif</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>MIDDLENAME</Name>"+
				"<Value>Shindakh Thamir</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>LASTNAME</Name>"+
				"<Value>Ghal</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DATEOFBIRTH</Name>"+
				"<Value>1989-01-01</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EMIRATESID</Name>"+
				"<Value>784198911469045</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISGOVERNMENTRELATION</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SIGNATORYFLAG</Name>"+
				"<Value>Yes</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFOWNERSHIP</Name>"+
				"<Value>10</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFPROOFPROVIDEDFORLOB</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SIGNATORYPOWERHELDSINCEDATE</Name>"+
				"<Value>2007-11-21</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SHAREHOLDINGPERCENTAGE</Name>"+
				"<Value>50</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AUTHORITYTYPE</Name>"+
				"<Value>10</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NATIONALITY</Name>"+
				"<Value>IN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>VISASPONSOR</Name>"+
				"<Value>SPONSOR2</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SISCOTLNO</Name>"+
				"<Value>Y</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>GUARANTORYCATEGORY</Name>"+
				"<Value>C</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISSUINGEMIRATE</Name>"+
				"<Value>28</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>GENDER</Name>"+
				"<Value>M</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COUNTRYOFRESIDENCE</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PASSPORTNUMBER</Name>"+
				"<Value>AE1469045</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COUNTRYOFBIRTH</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>FATHERNAME</Name>"+
				"<Value>PLUTO father UBO</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>MOTHERNAME</Name>"+
				"<Value>Mother of Partner One</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELMOBILENUMBERCOUNTRYCODE</Name>"+
				"<Value>971</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELMOBILENUMBER</Name>"+
				"<Value>444444444</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELEMAILID</Name>"+
				"<Value>tjuneja@rakbanktst.ae</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COBORROWERGUARANTORSTATUS</Name>"+
				"<Value>Yes</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIOD</Name>"+
				"<Value>3</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIF</Name>"+
				"<Value>1469045</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EXISTINGNTB</Name>"+
				"<Value>Existing</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TLNO</Name>"+
				"<Value>123456</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE1</Name>"+
				"<Value>003</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE2</Name>"+
				"<Value>Street name</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE3</Name>"+
				"<Value>102</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE4</Name>"+
				"<Value>Area or District</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDPOBOX</Name>"+
				"<Value>1423</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCITY</Name>"+
				"<Value>AAN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCOUNTRYCODE</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NAMEOFORGANIZATION</Name>"+
				"<Value>ABCDEFG</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DEPARTMENT</Name>"+
				"<Value>ABC</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>OCCUPATION</Name>"+
				"<Value>XYZ</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>USTAXPAYER</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIFUPDATEREQREL</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AECBCONSENTSTATUS</Name>"+
				"<Value>Digital</Value>"+
				"</Attribute>"+
				"</ConductHistoryRelatedPartiesDetails>"+
				"<ConductHistoryRelatedPartiesDetails>"+
				"<Attribute>"+
				"<Name>RELATEDPARTYID</Name>"+
				"<Value>1492396</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COMPANYFLAG</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELATIONSHIPTYPE</Name>"+
				"<Value>Guarantor</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>FIRSTNAME</Name>"+
				"<Value>Callixte</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>LASTNAME</Name>"+
				"<Value>Mbarushimana</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DATEOFBIRTH</Name>"+
				"<Value>1963-07-24</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISGOVERNMENTRELATION</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SIGNATORYFLAG</Name>"+
				"<Value>No</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFOWNERSHIP</Name>"+
				"<Value>14</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFPROOFPROVIDEDFORLOB</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>INVOLVEDINBUSINESS</Name>"+
				"<Value>Y</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SHAREHOLDINGPERCENTAGE</Name>"+
				"<Value>25</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AUTHORITYTYPE</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NATIONALITY</Name>"+
				"<Value>IN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>VISASPONSOR</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SISCOTLNO</Name>"+
				"<Value>Y</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISSISCOCOBORROWERGUARANTOR</Name>"+
				"<Value>No</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>GUARANTORYCATEGORY</Name>"+
				"<Value>C</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISSUINGEMIRATE</Name>"+
				"<Value>28</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>GENDER</Name>"+
				"<Value>M</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COUNTRYOFRESIDENCE</Name>"+
				"<Value>IN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PASSPORTNUMBER</Name>"+
				"<Value>AE1492396</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COUNTRYOFBIRTH</Name>"+
				"<Value>IN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>FATHERNAME</Name>"+
				"<Value>PLUTO father UBO</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>MOTHERNAME</Name>"+
				"<Value>Pluto Mother</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELMOBILENUMBERCOUNTRYCODE</Name>"+
				"<Value>971</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELMOBILENUMBER</Name>"+
				"<Value>333333333</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELEMAILID</Name>"+
				"<Value>test1@rakbanktst.ae</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COBORROWERGUARANTORSTATUS</Name>"+
				"<Value>Yes</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIOD</Name>"+
				"<Value>3</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIF</Name>"+
				"<Value>1492396</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EXISTINGNTB</Name>"+
				"<Value>Existing</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TLNO</Name>"+
				"<Value>123456</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE1</Name>"+
				"<Value>003</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE2</Name>"+
				"<Value>Street name</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE3</Name>"+
				"<Value>102</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE4</Name>"+
				"<Value>Area or District</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDPOBOX</Name>"+
				"<Value>1423</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCITY</Name>"+
				"<Value>AAN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCOUNTRYCODE</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NAMEOFORGANIZATION</Name>"+
				"<Value>ABCDEFG</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DEPARTMENT</Name>"+
				"<Value>ABC</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>OCCUPATION</Name>"+
				"<Value>XYZ</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>USTAXPAYER</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIFUPDATEREQREL</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AECBCONSENTSTATUS</Name>"+
				"<Value>Digital</Value>"+
				"</Attribute>"+
				"</ConductHistoryRelatedPartiesDetails>"+
				"<ConductHistoryRelatedPartiesDetails>"+
				"<Attribute>"+
				"<Name>RELATEDPARTYID</Name>"+
				"<Value>2717748</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COMPANYFLAG</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELATIONSHIPTYPE</Name>"+
				"<Value>Guarantor</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>FIRSTNAME</Name>"+
				"<Value>Abdul</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>LASTNAME</Name>"+
				"<Value>Haq</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DATEOFBIRTH</Name>"+
				"<Value>1971-10-10</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EMIRATESID</Name>"+
				"<Value>784197109314591</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISGOVERNMENTRELATION</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SIGNATORYFLAG</Name>"+
				"<Value>Yes</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFOWNERSHIP</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFPROOFPROVIDEDFORLOB</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SIGNATORYPOWERHELDSINCEDATE</Name>"+
				"<Value>2011-11-21</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SHAREHOLDINGPERCENTAGE</Name>"+
				"<Value>0</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AUTHORITYTYPE</Name>"+
				"<Value>14</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NATIONALITY</Name>"+
				"<Value>IN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>VISASPONSOR</Name>"+
				"<Value>SPONSOR4</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SISCOTLNO</Name>"+
				"<Value>Y</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISSISCOCOBORROWERGUARANTOR</Name>"+
				"<Value>No</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>GUARANTORYCATEGORY</Name>"+
				"<Value>C</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISSUINGEMIRATE</Name>"+
				"<Value>28</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>GENDER</Name>"+
				"<Value>O</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COUNTRYOFRESIDENCE</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PASSPORTNUMBER</Name>"+
				"<Value>IN9314591</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COUNTRYOFBIRTH</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>FATHERNAME</Name>"+
				"<Value>Father of Partner Two</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>MOTHERNAME</Name>"+
				"<Value>Pluto Mother UBO</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELMOBILENUMBERCOUNTRYCODE</Name>"+
				"<Value>971</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELMOBILENUMBER</Name>"+
				"<Value>523313121</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELEMAILID</Name>"+
				"<Value>wgshakia@rakbanktst.ae</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COBORROWERGUARANTORSTATUS</Name>"+
				"<Value>Yes</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIOD</Name>"+
				"<Value>3</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIF</Name>"+
				"<Value>2717748</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EXISTINGNTB</Name>"+
				"<Value>Existing</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TLNO</Name>"+
				"<Value>123456</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE1</Name>"+
				"<Value>003</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE2</Name>"+
				"<Value>Street name</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE3</Name>"+
				"<Value>102</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE4</Name>"+
				"<Value>Area or District</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDPOBOX</Name>"+
				"<Value>1423</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCITY</Name>"+
				"<Value>AAN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCOUNTRYCODE</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NAMEOFORGANIZATION</Name>"+
				"<Value>ABCDEFG</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DEPARTMENT</Name>"+
				"<Value>ABC</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>OCCUPATION</Name>"+
				"<Value>XYZ</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>USTAXPAYER</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIFUPDATEREQREL</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AECBCONSENTSTATUS</Name>"+
				"<Value>Digital</Value>"+
				"</Attribute>"+
				"</ConductHistoryRelatedPartiesDetails>"+
				"<ConductHistoryRelatedPartiesDetails>"+
				"<Attribute>"+
				"<Name>RELATEDPARTYID</Name>"+
				"<Value>8534-1</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COMPANYFLAG</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELATIONSHIPTYPE</Name>"+
				"<Value>Shareholder</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>FIRSTNAME</Name>"+
				"<Value>ULVA</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>LASTNAME</Name>"+
				"<Value>FARM</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DATEOFBIRTH</Name>"+
				"<Value>1914-07-20</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EMIRATESID</Name>"+
				"<Value>784191487777777</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISGOVERNMENTRELATION</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SIGNATORYFLAG</Name>"+
				"<Value>No</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFOWNERSHIP</Name>"+
				"<Value>4</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFPROOFPROVIDEDFORLOB</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>INVOLVEDINBUSINESS</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SHAREHOLDINGPERCENTAGE</Name>"+
				"<Value>20</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AUTHORITYTYPE</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NATIONALITY</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>VISASPONSOR</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SISCOTLNO</Name>"+
				"<Value>Y</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISSISCOCOBORROWERGUARANTOR</Name>"+
				"<Value>No</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>GENDER</Name>"+
				"<Value>O</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COUNTRYOFRESIDENCE</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PASSPORTNUMBER</Name>"+
				"<Value>BHS50048</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COUNTRYOFBIRTH</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELMOBILENUMBERCOUNTRYCODE</Name>"+
				"<Value>971</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELMOBILENUMBER</Name>"+
				"<Value>123456789</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELEMAILID</Name>"+
				"<Value>dummy@gmail.com</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COBORROWERGUARANTORSTATUS</Name>"+
				"<Value>No</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIOD</Name>"+
				"<Value>3</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EXISTINGNTB</Name>"+
				"<Value>NTB</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TLNO</Name>"+
				"<Value>123456</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE1</Name>"+
				"<Value>003</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE2</Name>"+
				"<Value>Street name</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE3</Name>"+
				"<Value>102</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE4</Name>"+
				"<Value>Area or District</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDPOBOX</Name>"+
				"<Value>1423</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCITY</Name>"+
				"<Value>AAN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCOUNTRYCODE</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NAMEOFORGANIZATION</Name>"+
				"<Value>ABCDEFG</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DEPARTMENT</Name>"+
				"<Value>ABC</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>OCCUPATION</Name>"+
				"<Value>XYZ</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>USTAXPAYER</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIFUPDATEREQREL</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AECBCONSENTSTATUS</Name>"+
				"<Value>Digital</Value>"+
				"</Attribute>"+
				"</ConductHistoryRelatedPartiesDetails>"+
				"<ConductHistoryRelatedPartiesDetails>"+
				"<Attribute>"+
				"<Name>RELATEDPARTYID</Name>"+
				"<Value>2128778</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COMPANYFLAG</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELATIONSHIPTYPE</Name>"+
				"<Value>Shareholder</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>FIRSTNAME</Name>"+
				"<Value>VIGILAR</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>MIDDLENAME</Name>"+
				"<Value>COLOMBIA</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>LASTNAME</Name>"+
				"<Value>LTDA</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DATEOFBIRTH</Name>"+
				"<Value>1925-12-12</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EMIRATESID</Name>"+
				"<Value>784</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISGOVERNMENTRELATION</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SIGNATORYFLAG</Name>"+
				"<Value>No</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFOWNERSHIP</Name>"+
				"<Value>10</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TYPEOFPROOFPROVIDEDFORLOB</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>INVOLVEDINBUSINESS</Name>"+
				"<Value>Y</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SHAREHOLDINGPERCENTAGE</Name>"+
				"<Value>5</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AUTHORITYTYPE</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NATIONALITY</Name>"+
				"<Value>US</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>VISASPONSOR</Name>"+
				"<Value>dummy</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>SISCOTLNO</Name>"+
				"<Value>Y</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ISSISCOCOBORROWERGUARANTOR</Name>"+
				"<Value>No</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>GENDER</Name>"+
				"<Value>F</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COUNTRYOFRESIDENCE</Name>"+
				"<Value>US</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PASSPORTNUMBER</Name>"+
				"<Value>VG8787H6</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COUNTRYOFBIRTH</Name>"+
				"<Value>US</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELMOBILENUMBERCOUNTRYCODE</Name>"+
				"<Value>95</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELMOBILENUMBER</Name>"+
				"<Value>7777777777</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>RELEMAILID</Name>"+
				"<Value>test5@rakbanktst.ae</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>COBORROWERGUARANTORSTATUS</Name>"+
				"<Value>No</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIOD</Name>"+
				"<Value>3</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIF</Name>"+
				"<Value>2128778</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EXISTINGNTB</Name>"+
				"<Value>Existing</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>TLNO</Name>"+
				"<Value>123456</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE1</Name>"+
				"<Value>003</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE2</Name>"+
				"<Value>Street name</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE3</Name>"+
				"<Value>102</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDADDRLINE4</Name>"+
				"<Value>Area or District</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDPOBOX</Name>"+
				"<Value>1423</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCITY</Name>"+
				"<Value>AAN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PREFERREDCOUNTRYCODE</Name>"+
				"<Value>AE</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NAMEOFORGANIZATION</Name>"+
				"<Value>ABCDEFG</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DEPARTMENT</Name>"+
				"<Value>ABC</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>OCCUPATION</Name>"+
				"<Value>XYZ</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>USTAXPAYER</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIFUPDATEREQREL</Name>"+
				"<Value>N</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>AECBCONSENTSTATUS</Name>"+
				"<Value>Digital</Value>"+
				"</Attribute>"+
				"</ConductHistoryRelatedPartiesDetails>"+
				"<SignatoryDetails>"+
				"<Attribute>"+
				"<Name>RELATEDPARTYID</Name>"+
				"<Value>1469045</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NAMEOFSIGNATORY</Name>"+
				"<Value>Nayif Shindakh Thamir Ghal</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NATIONALITY</Name>"+
				"<Value>IN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EFFECTIVELOB</Name>"+
				"<Value>168</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DATEOFBIRTH</Name>"+
				"<Value>1989-01-01</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIF</Name>"+
				"<Value>1469045</Value>"+
				"</Attribute>"+
				"</SignatoryDetails>"+
				"<SignatoryDetails>"+
				"<Attribute>"+
				"<Name>RELATEDPARTYID</Name>"+
				"<Value>2717748</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NAMEOFSIGNATORY</Name>"+
				"<Value>Abdul Haq</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NATIONALITY</Name>"+
				"<Value>IN</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>EFFECTIVELOB</Name>"+
				"<Value>120</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DATEOFBIRTH</Name>"+
				"<Value>1971-10-10</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CIF</Name>"+
				"<Value>2717748</Value>"+
				"</Attribute>"+
				"</SignatoryDetails>"+
				"<EvaluationChecksAECB>"+
				"<Attribute>"+
				"<Name>RELATEDPARTYID</Name>"+
				"<Value>1469045</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NAME</Name>"+
				"<Value>Nayif Shindakh Thamir Ghal</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CONSENTSTATUS</Name>"+
				"<Value>Digital</Value>"+
				"</Attribute>"+
				"</EvaluationChecksAECB>"+
				"<EvaluationChecksAECB>"+
				"<Attribute>"+
				"<Name>RELATEDPARTYID</Name>"+
				"<Value>2717748</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>NAME</Name>"+
				"<Value>Abdul Haq</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CONSENTSTATUS</Name>"+
				"<Value>Digital</Value>"+
				"</Attribute>"+
				"</EvaluationChecksAECB>"+
				"<EvaluationChecksFTSEvaluation>"+
				"<Attribute>"+
				"<Name>BANKNAME</Name>"+
				"<Value>XXX</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ACCOUNTNUMBER</Name>"+
				"<Value>AE456546464646454546545</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIODFROM</Name>"+
				"<Value>2021-05-26</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIODTO</Name>"+
				"<Value>2021-11-20</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CONSENTSTATUS</Name>"+
				"<Value>true</Value>"+
				"</Attribute>"+
				"</EvaluationChecksFTSEvaluation>"+
				"<EvaluationChecksFTSEvaluation>"+
				"<Attribute>"+
				"<Name>BANKNAME</Name>"+
				"<Value>MASREQ</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ACCOUNTNUMBER</Name>"+
				"<Value>AE456546464646454546550</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIODFROM</Name>"+
				"<Value>2021-05-26</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIODTO</Name>"+
				"<Value>2021-11-20</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CONSENTSTATUS</Name>"+
				"<Value>true</Value>"+
				"</Attribute>"+
				"</EvaluationChecksFTSEvaluation>"+
				"<EvaluationChecksFTSEvaluation>"+
				"<Attribute>"+
				"<Name>BANKNAME</Name>"+
				"<Value>XXX</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ACCOUNTNUMBER</Name>"+
				"<Value>AE456546464646454546545</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIODFROM</Name>"+
				"<Value>2021-05-26</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIODTO</Name>"+
				"<Value>2021-11-20</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CONSENTSTATUS</Name>"+
				"<Value>true</Value>"+
				"</Attribute>"+
				"</EvaluationChecksFTSEvaluation>"+
				"<EvaluationChecksFTSEvaluation>"+
				"<Attribute>"+
				"<Name>BANKNAME</Name>"+
				"<Value>MASREQ</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ACCOUNTNUMBER</Name>"+
				"<Value>AE456546464646454546550</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIODFROM</Name>"+
				"<Value>2021-05-26</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>PERIODTO</Name>"+
				"<Value>2021-11-20</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>CONSENTSTATUS</Name>"+
				"<Value>true</Value>"+
				"</Attribute>"+
				"</EvaluationChecksFTSEvaluation>"+
				"<DemographicDetails>"+
				"<Attribute>"+
				"<Name>DEMOGRAPHIC</Name>"+
				"<Value>US</Value>"+
				"</Attribute>"+
				"</DemographicDetails>"+
				"<AssetDetails>"+
				"<Attribute>"+
				"<Name>APPLICANTID</Name>"+
				"<Value>NTB</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>DOWNPAYMENT</Name>"+
				"<Value>10000</Value>"+
				"</Attribute>"+
				"<Attribute>"+
				"<Name>ASSETTYPE</Name>"+
				"<Value>Self</Value>"+
				"</Attribute>"+
				"</AssetDetails>"+
				"</CreateWorkitemReq>"+
				"</EE_EAI_MESSAGE>";*/
		
		/*String message1 = "<EE_EAI_MESSAGE>"+
				"<EE_EAI_HEADER>"+
				 "<MsgFormat>CUSTOMER_SR</MsgFormat>"+
				 "<MsgVersion>0000</MsgVersion>"+
				 "<RequestorChannelId>IB</RequestorChannelId>"+
				 "<RequestorUserId>RAKUSER</RequestorUserId>"+
				 "<RequestorLanguage>E</RequestorLanguage>"+
				 "<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
				 "<ReturnCode>0000</ReturnCode>"+
				 "<ReturnDesc>Success</ReturnDesc>"+
				 "<MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>"+
				 "<Extra1>REQ||VIKASRET.VIKASRET</Extra1>"+
				 "<Extra2>2011-02-08T15:31:38.818+05:30(Requested Date)</Extra2>"+
				"</EE_EAI_HEADER>"+
				"<CreateWorkitemReq>"+
					"<ProcessName>TWC</ProcessName>"+
					"<SubProcess>WBA</SubProcess>"+
					"<InitiateAlso>Y</InitiateAlso>"+
					"<Documents>"+
					"</Documents>"+
					"<Attributes>"+
						"<Attribute>"+
							"<Name>CHANNEL</Name>"+
							"<Value>WBA</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>CHANNELSUBGROUP</Name>"+
							"<Value>RM apply</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>SOLID</Name>"+
							"<Value>003</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>BAISWINUMBER</Name>"+
							"<Value> </Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>PRIORITY</Name>"+
							"<Value>Express</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>TWCABF</Name>"+
							"<Value>TWC</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>CIFID</Name>"+
							"<Value>2568121</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>RAKTRACKNUMBER</Name>"+
							"<Value>12345678910</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>CUSTOMERNAME</Name>"+
							"<Value>Siddharth V</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>ROCODE</Name>"+
							"<Value>ankit2</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>EMAILID</Name>"+
							"<Value>dummyemail@gmail.com</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>MOBILENUMBERISD</Name>"+
							"<Value>+123</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>MOBILENUMBER</Name>"+
							"<Value>6311569207</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>DOCUMENTLIST</Name>"+
							"<Value>ABF|AECB|Bank_Statements</Value>"+
						"</Attribute>"+
					"</Attributes>"+
				"</CreateWorkitemReq>"+
				"</EE_EAI_MESSAGE>";*/
				
				
				String message_DAO_WIcreation="<EE_EAI_MESSAGE>"+
						  "<EE_EAI_HEADER>"+
						    "<MsgFormat>CUSTOMER_SR</MsgFormat>"+
						    "<MsgVersion>0000</MsgVersion>"+
						    "<RequestorChannelId>EBC.WBA</RequestorChannelId>"+
						    "<RequestorUserId>RAKUSER</RequestorUserId>"+
						    "<RequestorLanguage>E</RequestorLanguage>"+
						    "<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
						    "<ReturnCode>0000</ReturnCode>"+
						    "<ReturnDesc>0000</ReturnDesc>"+
						    "<MessageId>RPAXLDO91672228206617</MessageId>"+
						    "<Extra1>REQ||RAK.VTUSER</Extra1>"+
						    "<Extra2>2022-12-28T15:50:06.617+05:30</Extra2>"+
						  "</EE_EAI_HEADER>"+
						  "<CreateWorkitemReq>"+
						    "<ProcessName>DigitalAO</ProcessName>"+
						    "<SubProcess>DigitalAO</SubProcess>"+
						    "<SubProcessName>DigitalAO</SubProcessName>"+
						    "<InitiateAlso>Y</InitiateAlso>"+
						    "<Attributes>"+
						      "<Attribute>"+
						        "<Name>Prospect_id</Name>"+
						        "<Value>53802</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Sol_Id</Name>"+
						        "<Value>NA</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Channel</Name>"+
						        "<Value>WBA</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Title</Name>"+
						        "<Value>MS.</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Given_Name</Name>"+
						        "<Value>TSONA STEFANOVNA</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Surname</Name>"+
						        "<Value>STEFANOVA</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Emirates_ID</Name>"+
						        "<Value>784198973752193</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Passport_Number</Name>"+
						        "<Value>387543513</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Gender</Name>"+
						        "<Value>F</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Marital_Status</Name>"+
						        "<Value>S</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Mothers_Maiden_Name</Name>"+
						        "<Value>Lubov</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Country_of_residence</Name>"+
						        "<Value>AE</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>DOB</Name>"+
						        "<Value>1989-06-20</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Country_Of_Birth</Name>"+
						        "<Value>RU</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>City_of_Birth</Name>"+
						        "<Value>RUS</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Passport_Expiry_Date</Name>"+
						        "<Value>2026-03-01</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Pasport_Issuing_Country</Name>"+
						        "<Value>BG</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Nationality</Name>"+
						        "<Value>BG</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>PEP</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Credit_grade</Name>"+
						        "<Value>High</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Product_Name</Name>"+
						        "<Value>ACCOUNTS</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>EmiratesID_Expiry_Date</Name>"+
						        "<Value>2024-06-27</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Preferred_Mail_Address</Name>"+
						        "<Value>RESIDENCE</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>employer_type</Name>"+
						        "<Value>Self employed</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Monthly_Expected_Turnover_Cash</Name>"+
						        "<Value>15000.00</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Monthly_Expected_Turnover_Non_Cash</Name>"+
						        "<Value>35000.00</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Gross_Monthly_Salary_income</Name>"+
						        "<Value>50000</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Mobile_1</Name>"+
						        "<Value>502098037</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Email_ID_1</Name>"+
						        "<Value>info.ibsconsultancy@gmail.com</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>US_Citizenship_Residency</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>W8form</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>W9form</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>STP_NonSTP</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Name_modify</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Purpose_of_Account</Name>"+
						        "<Value>Salary Transfer</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>CIF</Name>"+
						        "<Value>2877254</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Del_CB_DC</Name>"+
						        "<Value>C</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>CommunicationMode</Name>"+
						        "<Value>Email</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Product_Type</Name>"+
						        "<Value>ACNP1</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Product_Currency</Name>"+
						        "<Value>AED</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Duplicate_CIF_Found</Name>"+
						        "<Value>Y</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Blacklist_Found</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Risk_Score</Name>"+
						        "<Value>4.05</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Is_ntb</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Curr_Visa_Designation</Name>"+
						        "<Value>NA</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Name_on_Debit_Card</Name>"+
						        "<Value>TSONA STEFANOVA</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>MANDATE_AND_DECLARATION</Name>"+
						        "<Value>Y</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Sign</Name>"+
						        "<Value>Y</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>CRS</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Passport_Copies</Name>"+
						        "<Value>Y</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Dual_Nationality</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Net_monthly_income</Name>"+
						        "<Value>50000</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Statement_advice_communciation</Name>"+
						        "<Value>Email</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Mrkt_promo_offer</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>customer_segment</Name>"+
						        "<Value>PBD</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>customer_subsegment</Name>"+
						        "<Value>PBN</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>free_field_3</Name>"+
						        "<Value>IBINIL</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>free_field_4</Name>"+
						        "<Value>RAKEZ / IBS GENERAL TRADING FZ-LLC</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Currency</Name>"+
						        "<Value>AED</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Prefer_Branch_Det</Name>"+
						        "<Value>NA</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Employment_Status</Name>"+
						        "<Value>Employed</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Scheme_Code</Name>"+
						        "<Value>ACNP1</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Name_on_ChequeBk</Name>"+
						        "<Value>TSONA STEFANOVNA STEFANOVA</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Product_Service_dealing</Name>"+
						        "<Value>Business consultancy </Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>ChequeBk_Req</Name>"+
						        "<Value>Y</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>AECB_Consent</Name>"+
						        "<Value>Y</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Do_you_have_TIN</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>investment_portfolio_including_virtual_asset</Name>"+
						        "<Value>No</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>real_Est_owned</Name>"+
						        "<Value>No</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Inheritance</Name>"+
						        "<Value>No</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>other_Source_of_income</Name>"+
						        "<Value>Neotrade </Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>virtual_Card_issued</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>is_prime_req</Name>"+
						        "<Value>Y</Value>"+
						      "</Attribute>"+
												    "</Attributes>"+
												    "<Address_Details>"+
						      "<Attribute>"+
						        "<Name>Flat_Villa_No</Name>"+
						        "<Value>610</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Building_Villa_Name</Name>"+
						        "<Value>Tower 108</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Street_Location</Name>"+
						        "<Value>JVC</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>PO_Box_address</Name>"+
						        "<Value>10055</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Emirate_City_address</Name>"+
						        "<Value>DXB</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Address_type</Name>"+
						        "<Value>RESIDENCE</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Country_address</Name>"+
						        "<Value>AE</Value>"+
						      "</Attribute>"+
												    "</Address_Details>"+
												    "<company_Details>"+
						      "<Attribute>"+
						        "<Name>company_name</Name>"+
												        "<Value>IBS </Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Industry_category</Name>"+
						        "<Value>SER</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Industry</Name>"+
						        "<Value>CO05</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>years_of_incorp</Name>"+
						        "<Value>2021</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>country_of_incorp</Name>"+
						        "<Value>AE</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>percent_sharehold_in_cmpny</Name>"+
						        "<Value>100</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>annual_turnover</Name>"+
						        "<Value>900000</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Annual_profit</Name>"+
						        "<Value>500000</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Designation</Name>"+
						        "<Value>Manager </Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>countries_W_business_conducted_1</Name>"+
						        "<Value>AE</Value>"+
						      "</Attribute>"+
						    "</company_Details>"+
						  "</CreateWorkitemReq>"+
						"</EE_EAI_MESSAGE>";
		
				String message_DAO_WIUpdate = "<EE_EAI_HEADER>"+"\n"+
			            "     <EE_EAI_HEADER>"+"\n"+
			            "             <MsgFormat>UPDATE_SR</MsgFormat>"+"\n"+
			            "             <MsgVersion>0000</MsgVersion>"+"\n"+
			            "             <RequestorChannelId>IB</RequestorChannelId>"+"\n"+
			            "             <RequestorUserId>RAKUSER</RequestorUserId>"+"\n"+
			            "             <RequestorLanguage>E</RequestorLanguage>"+"\n"+
			            "             <RequestorSecurityInfo>secure</RequestorSecurityInfo>"+"\n"+
			            "             <ReturnCode>0000</ReturnCode>"+"\n"+
			            "             <ReturnDesc>Success</ReturnDesc>"+"\n"+
			            "             <MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>"+"\n"+
			            "             <Extra1>REQ || IB.123</Extra1>"+"\n"+
			            "             <Extra2>2011-02-08T15:31:38.818+05:30</Extra2>"+"\n"+
			            "     </EE_EAI_HEADER>"+"\n"+
			            "     <UpdateWorkitemReq>"+"\n"+
			            "           <ProcessName>DigitalAO</ProcessName>"+"\n"+
			            "           <SubProcess>DigitalAO</SubProcess>"+"\n"+
			            "           <Attributes>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>WINUMBER</Name>"+"\n"+
			            "                       <Value>DigitalAO-0000001659-process</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>Prospect_id</Name>"+"\n"+
			            "                       <Value>6327</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>Sol_Id</Name>"+"\n"+
			            "                       <Value>A7475</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>Event</Name>"+"\n"+
			            "                       <Value>Completed</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>Remarks</Name> "+
			            "                       <Value>Completed</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>Reject_reason</Name> "+
			            "                       <Value>Application</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>Previous_Designation</Name>"+"\n"+
			            "                       <Value>IDK</Value>"+"\n"+
			            "                 </Attribute>"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>Relation_Detail_w_PEP</Name>"+"\n"+
			            "                       <Value>Relations</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>Name_of_PEP</Name>"+"\n"+
			            "                       <Value>PEP</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>country_pep_hold_status</Name>"+"\n"+
			            "                       <Value>AE</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>Emirate_pep_hold_status</Name>"+"\n"+
			            "                       <Value>DXB</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>CIF</Name>"+"\n"+
			            "                       <Value>1212</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>Account_Number</Name>"+"\n"+
			            "                       <Value>1234</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>investment_portfolio_including_virtual_asset</Name>"+"\n"+
			            "                       <Value>4354</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>income_generated</Name>"+"\n"+
			            "                       <Value>Y</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>real_Est_owned</Name>"+"\n"+
			            "                       <Value>Y</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>rental_income</Name>"+"\n"+
			            "                       <Value>Y</Value>"+"\n"+
			            "                 </Attribute><Attribute>"+"\n"+
			            "                       <Name>title_deed_attached</Name>"+"\n"+
			            "                       <Value>T</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>other_Source_of_income</Name>"+"\n"+
			            "                       <Value>T</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>Desc_spec_prod_serv_your_cmpny_deals</Name>"+"\n"+
			            "                       <Value>R</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "                 <Attribute>"+"\n"+
			            "                       <Name>virtual_Card_issued</Name>"+"\n"+
			            "                       <Value>Y</Value>"+"\n"+
			            "                 </Attribute>"+"\n"+
			            "           </Attributes>"+"\n"+
			            "           <Additional_Document_Req>"+"\n"+
			                        "     <Attribute>"+"\n"+
			                        "     <Name>Document_name</Name>"+"\n"+
			                        "     <Value>PEP_Form</Value>"+"\n"+
			                        "     </Attribute>"+"\n"+
			                        "     <Attribute>"+"\n"+
			                        "           <Name>Status</Name>"+"\n"+
			                        "           <Value>Received</Value>"+"\n"+
			                        "     </Attribute>"+"\n"+
			                        "     <Attribute>"+"\n"+
			                        "           <Name>Remarks</Name>"+"\n"+
			                        "           <Value>yes</Value>"+"\n"+
			                        "     </Attribute>"+"\n"+
			            "           </Additional_Document_Req>"+"\n"+
			            "           <Additional_Document_Req>"+"\n"+
                        "     <Attribute>"+"\n"+
                        "     <Name>Document_name</Name>"+"\n"+
                        "     <Value>W9_form</Value>"+"\n"+
                        "     </Attribute>"+"\n"+
                        "     <Attribute>"+"\n"+
                        "           <Name>Status</Name>"+"\n"+
                        "           <Value>required</Value>"+"\n"+
                        "     </Attribute>"+"\n"+
                        "     <Attribute>"+"\n"+
                        "           <Name>Remarks</Name>"+"\n"+
                        "           <Value>yes</Value>"+"\n"+
                        "     </Attribute>"+"\n"+
            "           </Additional_Document_Req>"+"\n"+
			            "     </UpdateWorkitemReq>"+"\n"+
			            "</EE_EAI_HEADER>";
						
						String message_DCC=
				"<EE_EAI_MESSAGE>"+
					"<EE_EAI_HEADER>"+
						"<MsgFormat>CREATE_WORKITEM</MsgFormat>"+
						"<MsgVersion>0000</MsgVersion>"+
						"<RequestorChannelId>EBC.WBA</RequestorChannelId>"+
						"<RequestorUserId>RAKUSER</RequestorUserId>"+
						"<RequestorLanguage>E</RequestorLanguage>"+
						"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
						"<ReturnCode>0000</ReturnCode>"+
						"<ReturnDesc>Success</ReturnDesc>"+
						"<MessageId>Test1933374445</MessageId>"+
						"<Extra1>REQ||DEH.123</Extra1>"+
						"<Extra2>2011-02-08T15:31:38.818+05:30</Extra2>"+
					"</EE_EAI_HEADER>"+
					"<CreateWorkitemReq>"+
						"<ProcessName>Digital_CC</ProcessName>"+
						"<SubProcessName>Digital_CC</SubProcessName>"+
						"<InitiateAlso>Y</InitiateAlso>"+
						"<Documents>"+
						"</Documents>"+
						"<Attributes>"+
							"<Attribute>"+
								"<Name>MobileNo</Name>"+
								"<Value>00971509203039</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Title</Name>"+
								"<Value>MR</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FirstName</Name>"+
								"<Value>Amy</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>LastName</Name>"+
								"<Value>Baird</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>dob</Name>"+
								"<Value>1985-07-08</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>MiddleName</Name>"+
								"<Value>K</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Gender</Name>"+
								"<Value>Male</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Nationality</Name>"+
								"<Value>UAE</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>PassportNo</Name>"+
								"<Value>IN9205642</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>email_id</Name>"+
								"<Value>XCVG55@GMAIL.COM</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EmirateID</Name>"+
								"<Value>784191487777777</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Designation</Name>"+
								"<Value>ABCD21345</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Basic_Sal</Name>"+
								"<Value>50000</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>employercode</Name>"+
								"<Value>ABCD21345</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Employer_Name</Name>"+
								"<Value>EmployerName</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EmploymentType</Name>"+
								"<Value>SOFT</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Product</Name>"+
								"<Value>CC</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>AECB_CONSENT_HELD</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>NameOnCard</Name>"+
								"<Value>RAJ</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Skyward_Number</Name>"+
								"<Value>SK1343434</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Loyalty_Number</Name>"+
								"<Value>43</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>NTB</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FTS_Consent</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>IndusSeg</Name>"+
								"<Value></Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Prospect_id</Name>"+
								"<Value>564</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>ApprovedLimit</Name>"+
								"<Value>20000</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Supp_Card_Required</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FinalDBR</Name>"+
								"<Value>565</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FinalTAI</Name>"+
								"<Value>4</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EligibleCardProduct</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>MaritalStatus</Name>"+
								"<Value>M</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EStatementFlag</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EmID_Expiry</Name>"+
								"<Value>1999-07-08</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EmiD_Issue</Name>"+
								"<Value>1985-07-08</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Passport_expiry</Name>"+
								"<Value>1990-07-08</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Passport_issue</Name>"+
								"<Value>1985-07-08</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Is_STP</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EFMS_FLAG</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EFMS_Status</Name>"+
								"<Value>U</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FIRCO_Flag</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FIRCO_Status</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FTS_FLAG</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FTS_Ack_flg</Name>"+
								"<Value>REJ</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>AECB_REF_NUM</Name>"+
								"<Value>3453</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FTS_Salary</Name>"+
								"<Value>5000</Value>"+
							"</Attribute>"+
						"</Attributes>"+
						"<Address_Details>"+
							"<Attribute>"+
								"<Name>House_No</Name>"+
								"<Value>45464</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Building_Name</Name>"+
								"<Value>TowerA</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Street_Name</Name>"+
								"<Value>XYZRd</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Country_No</Name>"+
								"<Value>235334</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Emirate_Of_Residence</Name>"+
								"<Value>Delhi</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>PO_Box_Address</Name>"+
								"<Value>India</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Address_Type</Name>"+
								"<Value>Residence</Value>"+
							"</Attribute>"+
						"</Address_Details>"+
		            "</CreateWorkitemReq>"+
				"</EE_EAI_MESSAGE>";
						
						String DAO_UpdateWI_Disp = "<EE_EAI_MESSAGE>"+"\n"+
								   "<EE_EAI_HEADER>"+"\n"+
								      "<MsgFormat>UPDATE_SR</MsgFormat>"+"\n"+
								      "<MsgVersion>0000</MsgVersion>"+"\n"+
								      "<RequestorChannelId>IB</RequestorChannelId>"+"\n"+
								      "<RequestorUserId>RAKUSER</RequestorUserId>"+"\n"+
								      "<RequestorLanguage>E</RequestorLanguage>"+"\n"+
								      "<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+"\n"+
								      "<ReturnCode>0000</ReturnCode>"+"\n"+
								      "<ReturnDesc>Success</ReturnDesc>"+"\n"+
								      "<MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>"+"\n"+
								      "<Extra1>REQ || IB.123</Extra1>"+"\n"+
								      "<Extra2>2011-02-08T15:31:38.818+05:30</Extra2>"+"\n"+
								   "</EE_EAI_HEADER>"+"\n"+
								   "<UpdateWorkitemReq>"+"\n"+
								      "<ProcessName>DigitalAO</ProcessName>"+"\n"+
								      "<SubProcess>Dispatch</SubProcess>"+"\n"+
								      "<Attributes>"+"\n"+
								         "<Attribute>"+"\n"+
								            "<Name>WINUMBER</Name>"+"\n"+
								            "<Value>DigitalAO-0000002386-process</Value>"+"\n"+
								         "</Attribute>"+"\n"+
								         "<Attribute>"+"\n"+
								            "<Name>Delivery_Status</Name>"+"\n"+
								            "<Value>Delivered</Value>"+"\n"+
								         "</Attribute>"+"\n"+
								         "<Attribute>"+"\n"+
								            "<Name>Status_Code</Name>"+"\n"+
								            "<Value>1</Value>"+"\n"+
								        "</Attribute>"+"\n"+
								     "</Attributes>"+"\n"+
								      "<Emirates_ID_PING_data>"+"\n"+
								         "<Attribute>"+"\n"+
								            "<Name>emirates_id</Name>"+"\n"+
								            "<Value>784199343090168</Value>"+"\n"+
								         "</Attribute>"+"\n"+
								         "<Attribute>"+"\n"+
								            "<Name>expiry_date</Name>"+"\n"+
								           "<Value>2023-11-03</Value>"+"\n"+
								        "</Attribute>"+"\n"+
								         "<Attribute>"+"\n"+
								            "<Name>card_number</Name>"+"\n"+
								            "<Value></Value>"+"\n"+
								         "</Attribute>"+"\n"+
								         "<Attribute>"+"\n"+
								            "<Name>gender</Name>"+"\n"+
								            "<Value>F</Value>"+"\n"+
								        "</Attribute>"+"\n"+
								         "<Attribute>"+"\n"+
								            "<Name>name</Name>"+"\n"+
								            "<Value>lIAM DEM</Value>"+"\n"+
								         "</Attribute>"+"\n"+
								         "<Attribute>"+"\n"+
								            "<Name>nationality</Name>"+"\n"+
								            "<Value>India</Value>"+"\n"+
								        "</Attribute>"+"\n"+
								        "<Attribute>"+"\n"+
							            "<Name>Date_of_birth</Name>"+"\n"+
							            "<Value>1989-10-17</Value>"+"\n"+
							        "</Attribute>"+"\n"+
								     "</Emirates_ID_PING_data>"+"\n"+
								     "<Documents>"+"\n"+
								        "<Document>"+"\n"+
								           "<Attribute>"+"\n"+
								              "<Name>DocumentName</Name>"+"\n"+
								              "<Value>Proof_of_delivery</Value>"+"\n"+
								           "</Attribute>"+"\n"+
								           "<Attribute>"+"\n"+
								              "<Name>Document_type</Name>"+"\n"+
								              "<Value>jpeg</Value>"+"\n"+
								           "</Attribute>"+"\n"+
								           "<Attribute>"+"\n"+
								              "<Name>Document_url</Name>"+"\n"+
								              "<Value/>"+"\n"+
								           "</Attribute>"+"\n"+
								        "</Document>"+"\n"+
								        "<Document>"+"\n"+
								           "<Attribute>"+"\n"+
								              "<Name>DocumentName</Name>"+"\n"+
								              "<Value>single_pager</Value>"+"\n"+
								           "</Attribute>"+"\n"+
								           "<Attribute>"+"\n"+
								              "<Name>Document_type</Name>"+"\n"+
								              "<Value>jpeg</Value>"+"\n"+
								           "</Attribute>"+"\n"+
								           "<Attribute>"+"\n"+
								              "<Name>Document_url</Name>"+"\n"+
								              "<Value/>"+"\n"+
								              "<Attribute>"+"\n"+
								        "</Document>"+"\n"+
								     "</Documents>"+"\n"+
								  "</UpdateWorkitemReq>"+"\n"+
								"</EE_EAI_MESSAGE>";

						
	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rsDWm0NVtQ1rsDWbI1m1DXbA1rsDAzjXbAwM4ws0MTKMVyDGyDwyQ8PQk9jHbTy9CLABfI9PI9XzD3PIyMdMglFAIWoUiiFPKgAUAlAUiiKIohSKEolB59+QIihKIolAAAAoSgBKIsCwoAIqosgABZRFIKCLKqACCiKJQlKWCywEBSUABSACAooijyIAYsuKhEr1jPWXFlWWYzIx5CgAAWUAAHg9pSAp4PV8+TIIvm469XF6PYgAAmOsrx7AhZQAAAAAAAAABKIsLLBZRKIollABCpQAAAWksAAACyFlpLIKJZaBAPIlAYc2KvObBU9efeM95cOZfOPL5SsQ9evQxnk9yZjz4B78QuTx5L59wy+PXla8E9RDx6qr69YozYM+EyefcXxPXlPXmZTGUnu+T1MXoe/fkx+p5PUex585Ty8+T368w9ZJVCAAACUQFgoEsKABAoAAAAAAAAAAqyiCKlAqVChAPIlAa+xrWe2FWZhGTY1tmJizFw+sgw+sgwzOMeQMVyDHfYxevYwzOSLJcVyWzz5ylw3KRjyxWLKMNyDEyjFkox32McyjF690w3KMLMTF79Fw5PQxzKMVyCUgAAAACKICpRAWCgAAAAAAAAAAAAAAAopKglAs8CUollBCoKlAAAAoIAAELAVQAAgoAIAABYCwUCUJQBKABCpQQsCgAllEoiwWUgAFgqCgAAAAJQCwAAAAAKgooCUgK8WIsCwAFgAWUACggKACECgAsAAAoECUAAAWUAlQKBBZQACWC+PXg9pSoKlCCgQAAAAAFgAqBYFCAqUAAAAACliFloAICvAgAAACwoIooAAAIQAAAKAKCAoIgKAAAACglBAAUEABAoAAAoAIAAAgFAAAACgAgqwKIAAQKKCFKlAAD/2gAMAwEAAgADAAAAIftsktusogQsvsggQQogNtvvvuvqnvqgogYsiggQQMukihgsgkvPvvvPvvPPPPvvvpjSTTTTTRSQBRTXXSQRQhsiMogMshgQgQQQogRQXQTTZXaRVSQgSLtNvPPPPPPPvi9w304y++81z/51/wANP7re/OvdP/sPdcveX3l1nNnUkywRnXVl3HPss/vwVObj3XDnHXX31X1HnX20000GkU0E22lFV239d8tdMPevEFEgGBXaf53s88+stNuJLay7xbwFHF23F0EH21HnX103HWHEGEEEAEBEFGFFHFF+0AJUn0W00WFWHEGGEY4L7777zzzEEEXmEFEE0EAX2l32lUEEEMFEIIIAEEEEEEHETbYDFX3X1W20kMMIdZb7b7z7zzwA0EW00EEF0kEHW3101WFkEEEEAIEAEEEFEEFnJ+rZpWnn0X3UEEAMcIFZLb77zzz3H32G00lHU00kX3HXHE0AAEEEMcEEV03W1nnV2cmS4h5ykHW1GkBEIpsNZbzzz7z44TX302EFW32lFF33330EAEEEEEHHX2nX1G2/FXokwbqqrgR3GkEENaxIIDb7b7wLqgrEBHW0313X23X3n3323mn3002zTVH3kF5TqSShUm5RKSnFWk0MLrAIIELzzzLwTAxXz0GAVm21UEknEQHUV2Emlln23zyB22yQN/iFJHzywAQEjHnPcc8Yc+bjDIJgAxSICUFsnEW8s+8rvv8AyGKefTf3akqxrZZSVPgjXLr/APn1836x3x7pw4938y0/6wOGIMnshVQPbcINoYsgIGTdZQSeT2y72pKTwKX6YMMBUfdFgeVTZdbYWQaQgnviPjsBCEiAXATffeTVbSZTRffffSfcefbYsOPpTO2QSRfMmmFoBeTQSk17aQQHvvvPPIFfbRAANVadcffebfQUQOdeaUfJACAw3W3GG5yIAYAG2/tAPQRQQQQRglQfvsvvPGccSQAgAAYQQNYbffRAYAADTbZLMrAw0zQo/hFBRimQGNgAFYecQRIgggAMqgvvPAigAHggIkAAAACSNdRQTQAMefqS6r1130R7Uv4GesMXEvgEPbTSwAAARSQAdtvvvtvrjvmjvrggnjjAITDIEYBKAZZIfQFrA1Hb3EbW5ZLVuviuBceYRVT3XdTQQQcssgABjguskoPMtuNusvYNAIDNDN04Sj5OuyP7SgdfY7KADhhrhAPTbfcd/VafbXSYUc3+DY891Z7126yh0546tsnun1skvFtXPAGY8cA8xL7ka02Zb4/7/wC+cYq+uMDU8/fL4p7qLbKYqaDRxzjhEDWGHW3t3/sn7FfUUOtRdW7r94+YbbwwAH2UwCAB303H3VErLKL4I76Lb6ADDRgDTyiJRwhPEGMVqt330VvxqKmkHB1g66pAVT212n1XWlkEFaEb4oY4b7657oCQhCAASTBDyoTVfGzwEH1333WbbIAAVVP7LIABXXH3UnX3320EEFLqJZLrbb74AACGEAQjyYiQCUDUfD4ABX332W34q8g32HVpYAX33Hn10V2nFGEFEAG6p5pLJL76oAkAlDTyxcfCbY1/RjDQRW33333MEvknkpHbKY4G13H330EGEH0EEEErYaooIILaIIAiGTI5AvYy8pN/2gLCp331GG4ZW9pyec6Kb74A7333nn001X0kEEEIIoKI44Y7a5yxiT+Pq9MT7E7tnJ464ECFzr0MHklGkEQqxDpKRXHGGTn1n0n31O3TDW9DizFkUVPHtaqQsVTg8CWfsNumktP2eL2/P/qP/fMmAE3ijBWUIs2sxEV33vnjywCwACBX2n9+nCzWFXta1deP9lG2mFmNXIvFkXWW0202kSjDL4u+S2VqFIHC5tcJJoIY44LqAyR89i8pAf2HVhHDiKI57xWZMxrLb6rD4DBo677oD6ShCJUWkHV2088Y6Kb5aJq4QxX/AD4X7XbZYW/fISCO+qBlhzWu++e+A+gUaC+2+FyCAQBEBxt199N1+eKCyO2im68o7nAj38ankAn3OGC26Khs3++Ayy++C+GueK+WKQWAkMBABxNB9515+6C+2+uC2KsoBfAjbvpxAskAWuCCq2A06eWIiB2C+++2+O+qCAGKYNlAAA1t9dVB22W+Cu+Ge2OGA9nyLZ53s8JYe+uC++QXU2eKCAO++u6e+W2uIAI+OAQA1o9B15J9WCiCOiY8socksU1aeZjuIw5K2++O2+KZQ+yy2qeaqSCaOCCyyK+8oEGuOuRIRgJROOOYCsAY8wUsBv8Ay54VRdzcfXBGHlrjCacTJKOhlPOJFFLPIIMOIDvskNPMEnNCAKpGHi80xy9682mkJr1vBDXf3812Wzx30xyx7zwy5x38181825647w7y+8w0z813y/uhmOMOMDOAJOOMAGMEBIFskohhjrskgktphjsJigqiqHkHHkoooLFBOJCPCAnMMAglM94qEAAACAABCPAJEjIAhnlNPqgggnrgtsggktlqksqghijgluFBAPJqFsqCbQghP4yHSyc+x/7/ANifPF1WSPvu0vGN+99NTbJ4qp6YpYIJ4I4oIZL776pxDYoCTX0IJPDGMBCBACADjHbmB2TwCzyygiBGDBQB7IIKILL4p6YopKLb6pI77477574BIrX7KoY4oIIIAIYIZ7oKL6IYKLa7aIooJJIZoZ4IKpLar4K44IJIIJrZ77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</Value>"+"\n"+
				           
				
//Prd call
		String dcc_create_wi=
				"<EE_EAI_MESSAGE>"+
						  "<EE_EAI_HEADER>"+
						    "<MsgFormat>CUSTOMER_SR</MsgFormat>"+
						    "<MsgVersion>0000</MsgVersion>"+
						    "<RequestorChannelId>EBC.WBA</RequestorChannelId>"+
						    "<RequestorUserId>RAKUSER</RequestorUserId>"+
						    "<RequestorLanguage>E</RequestorLanguage>"+
						    "<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
						    "<ReturnCode>0000</ReturnCode>"+
						    "<ReturnDesc>0000</ReturnDesc>"+
						    "<MessageId>RPAORPGH1675747397827</MessageId>"+
						    "<Extra1>REQ||RAK.cntrlm</Extra1>"+
						    "<Extra2>2023-02-07T09:23:17.832+05:30</Extra2>"+
						  "</EE_EAI_HEADER>"+
						  "<CreateWorkitemReq>"+
						   "<ProcessName>Digital_CC</ProcessName>"+
						    "<SubProcess>Digital_CC</SubProcess>"+
						    "<SubProcessName>Digital_CC</SubProcessName>"+
						    "<InitiateAlso>Y</InitiateAlso>"+
						    "<Attributes>"+
						      "<Attribute>"+
						        "<Name>MobileNo</Name>"+
						        "<Value>555723012</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>No_earning_members</Name>"+
						        "<Value>1</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Preferred_Language</Name>"+
						        "<Value>EN</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Visa_Number</Name>"+
						        "<Value>201/2022/3/193205</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Target_Segment_Code</Name>"+
						        "<Value>DIG</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Market_Segment_Code</Name>"+
						        "<Value>DIG</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Title</Name>"+
						        "<Value>F</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>FirstName</Name>"+
						        "<Value>SAMEEN FATIMA</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>LastName</Name>"+
						        "<Value>NAQVI</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>DOB</Name>"+
						        "<Value>1989-01-17</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Nationality</Name>"+
						        "<Value>PK</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Nationality_Desc</Name>"+
						        "<Value>Pakistan</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>PassportNo</Name>"+
						        "<Value>AY9399232</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>email_id</Name>"+
						        "<Value>Sameenfatima@hotmail.com</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>EmirateID</Name>"+
						        "<Value>784198974838587</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Designation</Name>"+
						        "<Value>TECHR</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Designation_Desc</Name>"+
						        "<Value>TEACHER</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Cust_Decl_Salary</Name>"+
						        "<Value>8300</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Employercode</Name>"+
						        "<Value>801209</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Employer_Name</Name>"+
						        "<Value>EXCELLA MENA DMCC</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>EmploymentType</Name>"+
						        "<Value>S</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>EmploymentType_Desc</Name>"+
						        "<Value>Salaried</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Product</Name>"+
						        "<Value>CON</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Product_Desc</Name>"+
						        "<Value>Conventional</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>AECB_CONSENT_HELD</Name>"+
						        "<Value>true</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>NameOnCard</Name>"+
						        "<Value>SAMEEN NAQVI</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>NTB</Name>"+
						        "<Value>true</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>FTS_Consent</Name>"+
						        "<Value>true</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Prospect_id</Name>"+
						        "<Value>11111</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>ApprovedLimit</Name>"+
						        "<Value>8300</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>FinalDBR</Name>"+
						        "<Value>4</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>FinalTAI</Name>"+
						        "<Value>8300</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>MaritalStatus</Name>"+
						        "<Value>2</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>MaritalStatus_Desc</Name>"+
						        "<Value>MARRIED</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>EStatementFlag</Name>"+
						        "<Value>false</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>EmID_Expiry</Name>"+
						        "<Value>2032-04-06</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>EmiD_Issue</Name>"+
						        "<Value>2022-04-07</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Passport_expiry</Name>"+
						        "<Value>2023-11-28</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Passport_issue</Name>"+
						        "<Value>2018-11-29</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Supp_Card_Required</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>FIRCO_Flag</Name>"+
						        "<Value>Y</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Statement_Analyser_Flag</Name>"+
						        "<Value>F</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>AECB_REF_NUM</Name>"+
						        "<Value>1728459</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Requested_Limit</Name>"+
						        "<Value>8300</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Dependents</Name>"+
						        "<Value>2</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Household_Income</Name>"+
						        "<Value>73300</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Expense1</Name>"+
						        "<Value>1000</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Expense2</Name>"+
						        "<Value>500</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Expense3</Name>"+
						        "<Value>0</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Expense4</Name>"+
						        "<Value>0</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Virtual_Card_Limit</Name>"+
						        "<Value>500</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Credit_Shield_Flag</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>CIF</Name>"+
						        "<Value>3102108</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Name_On_Passport</Name>"+
						        "<Value>SAMEEN FATIMA NAQVI</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Name_On_EID</Name>"+
						        "<Value>SAMEEN FATIMA N HUSSAIN NAQVI</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Visa_Expiry</Name>"+
						        "<Value>2032-04-06</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Visa_Sponsor_Name</Name>"+
						        "<Value>SYED ADEEL HUSSAIN NAQVI</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Emirates_Visa</Name>"+
						        "<Value>DUBAI</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>GCC_National</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Prospect_Creation_Date</Name>"+
						        "<Value>2023-02-03</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>RM_Code</Name>"+
						        "<Value>88M8433</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Gender_Code</Name>"+
						        "<Value>F</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Gender_Desc</Name>"+
						        "<Value>FEMALE</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Selected_Card_Type</Name>"+
						        "<Value>TITANIUM_CARD</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Date_Of_Joining</Name>"+
						        "<Value>2022-08-29</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Age</Name>"+
						        "<Value>34</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>IBAN</Name>"+
						        "<Value>AE570260000215480092201</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>ECRN</Name>"+
						        "<Value>116548500</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>CRN</Name>"+
						        "<Value>116548500</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Card_Number</Name>"+
						        "<Value>5239267838547005</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Card_Limit</Name>"+
						        "<Value>500</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Card_Product_Sub_Type</Name>"+
						        "<Value>TITANIUM-EXPAT</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Virtual_Card_Assigned_Date</Name>"+
						        "<Value>2023-02-03</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Fatca</Name>"+
						        "<Value>N</Value>"+
						      "</Attribute>"+
						    "</Attributes>"+
						    "<Address_Details>"+
						      "<Attribute>"+
						        "<Name>Address_Type</Name>"+
						        "<Value>RESIDENCE</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>House_No</Name>"+
						        "<Value>601 </Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Building_Name</Name>"+
						        "<Value>Durat al Marsa tower </Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Street_Name</Name>"+
						        "<Value>Dubai marina  </Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>City</Name>"+
						        "<Value>DXB</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>City_Desc</Name>"+
						        "<Value>DXB</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Country</Name>"+
						        "<Value>AE</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Country_Desc</Name>"+
						        "<Value>UNITED ARAB EMIRATES</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>PO_Box_Address</Name>"+
						        "<Value>00000</Value>"+
						      "</Attribute>"+
						    "</Address_Details>"+
						    "<Address_Details>"+
						      "<Attribute>"+
						        "<Name>Address_Type</Name>"+
						        "<Value>Home</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>House_No</Name>"+
						        "<Value>206E </Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Building_Name</Name>"+
						        "<Value>Stat life housing society </Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Street_Name</Name>"+
						        "<Value>Na </Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Country_No</Name>"+
						        "<Value>123456</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>City</Name>"+
						        "<Value>LHE</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>City_Desc</Name>"+
						        "<Value>LAHORE</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Country</Name>"+
						        "<Value>PK</Value>"+
						      "</Attribute>"+
						      "<Attribute>"+
						        "<Name>Country_Desc</Name>"+
						        "<Value>PAKISTAN</Value>"+
						      "</Attribute>"+
						    "</Address_Details>"+
						  "</CreateWorkitemReq>"+
						"</EE_EAI_MESSAGE>";

				/*"<EE_EAI_MESSAGE>"+
					"<EE_EAI_HEADER>"+
						"<MsgFormat>CREATE_WORKITEM</MsgFormat>"+
						"<MsgVersion>0000</MsgVersion>"+
						"<RequestorChannelId>EBC.WBA</RequestorChannelId>"+
						"<RequestorUserId>RAKUSER</RequestorUserId>"+
						"<RequestorLanguage>E</RequestorLanguage>"+
						"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
						"<ReturnCode>0000</ReturnCode>"+
						"<ReturnDesc>Success</ReturnDesc>"+
						"<MessageId>CAS165536806634681</MessageId>"+
						"<Extra1>REQ||DEH.123</Extra1>"+
						"<Extra2>2017-04-17T11:05:30.000+04:00</Extra2>"+
					"</EE_EAI_HEADER>"+
					"<CreateWorkitemReq>"+
						"<ProcessName>Digital_CC</ProcessName>"+
						"<SubProcessName>Digital_CC</SubProcessName>"+
						"<InitiateAlso>Y</InitiateAlso>"+
						"<Documents>"+
						"</Documents>"+
						"<Attributes>"+
							"<Attribute>"+
								"<Name>MobileNo</Name>"+
								"<Value>00971540580922</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Title</Name>"+
								"<Value>1</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Title_Desc</Name>"+
								"<Value>MR</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FirstName</Name>"+
								"<Value>YASMIN</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>LastName</Name>"+
								"<Value>YACOUB</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>dob</Name>"+
								"<Value>1989-06-30</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Age</Name>"+
								"<Value>32.11</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>MiddleName</Name>"+
								"<Value>Kumar</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Gender</Name>"+
								"<Value>1</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Gender_Desc</Name>"+
								"<Value>Male</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Nationality</Name>"+
								"<Value>14</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Nationality_Desc</Name>"+
								"<Value>AE</Value>"+
							"</Attribute>"+
							"<Attribute>"+		
								"<Name>PassportNo</Name>"+
								"<Value>ER02488632</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>email_id</Name>"+
								"<Value>TEST11@RAKBANKTST.AE</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EmirateID</Name>"+
								"<Value>784198912579458</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Designation</Name>"+
								"<Value>156</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Designation_Desc</Name>"+
								"<Value>Sales</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Cust_Decl_Salary</Name>"+
								"<Value>50000</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>employercode</Name>"+
								"<Value>002</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Employer_Name</Name>"+
								"<Value>MINISTRY OF EDUCATION</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EmploymentType</Name>"+
								"<Value>52</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EmploymentType_Desc</Name>"+
								"<Value>Salaried</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Product</Name>"+
								"<Value>12</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Product_Desc</Name>"+
								"<Value>Credit Card</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>AECB_CONSENT_HELD</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>NameOnCard</Name>"+
								"<Value>RAJ</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Skyward_Number</Name>"+
								"<Value>SK1343434</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Loyalty_Number</Name>"+
								"<Value>43</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>NTB</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FTS_Consent</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>IndusSeg</Name>"+
								"<Value>56</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>IndusSeg_Desc</Name>"+
								"<Value>INDUS De</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Prospect_id</Name>"+
								"<Value>564</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>ApprovedLimit</Name>"+
								"<Value>20000</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FinalDBR</Name>"+
								"<Value>565</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FinalTAI</Name>"+
								"<Value>4</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EligibleCardProduct</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EligibleCardProduct_Desc</Name>"+
								"<Value>MCBWE-UAE</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>MaritalStatus</Name>"+
								"<Value>5</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>MaritalStatus_Desc</Name>"+
								"<Value>M</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EStatementFlag</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EmID_Expiry</Name>"+
								"<Value>1999-07-08</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EmiD_Issue</Name>"+
								"<Value>1985-07-08</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Passport_expiry</Name>"+
								"<Value>1990-07-08</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Passport_issue</Name>"+
								"<Value>1985-07-08</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Is_STP</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EFMS_FLAG</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>EFMS_Status</Name>"+
								"<Value>U</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FIRCO_Flag</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FIRCO_Status</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FIRCO_ECN_No</Name>"+
								"<Value>DCC-3000-2345223453436</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FTS_FLAG</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FTS_Ack_flg</Name>"+
								"<Value>REJ</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Supp_Card_Required</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Self_Supp_Card_Required</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Self_Supp_Card_Limit</Name>"+
								"<Value>500</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Self_Supp_Card_Embossing_Name</Name>"+
								"<Value>RAJ</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>AECB_REF_NUM</Name>"+
								"<Value>3453</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>FTS_Salary</Name>"+
								"<Value>5000</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Industry</Name>"+
								"<Value>IT</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Sub_Industry</Name>"+
								"<Value>DSFG</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Requested_Limit</Name>"+
								"<Value>2000</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>IPA_Limit</Name>"+
								"<Value>1000</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Dependents</Name>"+
								"<Value>NO</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Earning_members</Name>"+
								"<Value>2</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Expense1</Name>"+
								"<Value>DFG</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Expense2</Name>"+
								"<Value>SGSD</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Expense3</Name>"+
								"<Value>DFSG/Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Expense4</Name>"+
								"<Value>XCVB</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Expense5</Name>"+
								"<Value>555</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Expense6</Name>"+
								"<Value>666</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Virtual_Card_Limit</Name>"+
								"<Value>500</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Credit_Shield_Flag</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>RAK_Protect_Flag</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Acquistion_Flag</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>CIF</Name>"+
								"<Value>5465545</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Name_On_Passport</Name>"+
								"<Value>RAJ</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Name_On_EID</Name>"+
								"<Value>RAH</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Visa_Expiry</Name>"+
								"<Value>2030-01-01</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Visa_Sponsor_Name</Name>"+
								"<Value>SELF</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Emirates_Visa</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>GCC_National</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Attached</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Prospect_Creation_Date</Name>"+
								"<Value>Y</Value>"+
							"</Attribute>"+	
						"</Attributes>"+
						"<Address_Details>"+
							"<Attribute>"+
								"<Name>House_No</Name>"+
								"<Value>45464</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Building_Name</Name>"+
								"<Value>TowerA</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Street_Name</Name>"+
								"<Value>XYZRd</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Country_No</Name>"+
								"<Value>235334</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>City</Name>"+
								"<Value>1</Value>"+
							"</Attribute>"+
								"<Attribute>"+
								"<Name>City_Desc</Name>"+
								"<Value>Alwar</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>State</Name>"+
								"<Value>2</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>State_Desc</Name>"+
								"<Value>Rajasthan</Value>"+
							"</Attribute>"+
								"<Attribute>"+
								"<Name>Country</Name>"+
								"<Value>45</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Country_Desc</Name>"+
								"<Value>India</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>PO_Box_Address</Name>"+
								"<Value>India</Value>"+
							"</Attribute>"+
							"<Attribute>"+
								"<Name>Address_Type</Name>"+
								"<Value>Residence</Value>"+
							"</Attribute>"+
						"</Address_Details>"+
						"<Supp_Card_Details>"+
						"<Attribute>"+
							"<Name>Self_Required</Name>"+
							"<Value>NO</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Name_On_Card</Name>"+
							"<Value>RAJ</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Card_For_Relative</Name>"+
							"<Value>Y</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Relationship_type</Name>"+
							"<Value>Brother</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>UAE_Residency</Name>"+
							"<Value>Y</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>First_Name</Name>"+
							"<Value>RAJ</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Middle_Name</Name>"+
							"<Value></Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Last_Name</Name>"+
							"<Value>Kumar</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>DOB</Name>"+
							"<Value>1989-06-30</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Passport_Number</Name>"+
							"<Value>IN12345678</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Passport_Expiry</Name>"+
							"<Value>2030-07-08</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Nationality</Name>"+
							"<Value>23</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Nationality_Desc</Name>"+
							"<Value>United arab emirate</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>MobileNo</Name>"+
							"<Value>00972515466</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Email</Name>"+
							"<Value>raindra@rakbank.ae</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Limit_Required</Name>"+
							"<Value>2000</Value>"+
						"</Attribute>"+
						"<Attribute>"+
							"<Name>Card_Embossing_Name</Name>"+
							"<Value>DARHSEEL</Value>"+
						"</Attribute>"+
					"</Supp_Card_Details>"+
					"<UIDDetials>"+
		                "<Attribute>"+
		                "<Name>Alertdetails</Name>"+
		                "<Value>![CDATA[Suspect(s) detected by OFAC-Agent:9"+"\n"+
		                "SystemId:"+"\n"+
		                "Associate:"+"\n"+
		                "============================="+"\n"+
		                " Suspect detected #1"+"\n"+
		                "OFAC ID:AS06990130"+"\n"+
		                "MATCH: 775.00"+"\n"+
		                "TAG: NAM"+"\n"+
		                "MATCHINGTEXT: IQBAL, NAVEED,"+"\n"+
		                "RESULT: (0)"+"\n"+
		                "NAME: IQBAL, NAVED"+"\n"+
		                "Synonyms: none"+"\n"+
		                "ADDRESS:"+"\n"+
		                "Synonyms: none"+"\n"+
		                "CITY:"+"\n"+
		                "Synonyms: none"+"\n"+
		                "COUNTRY: INDIA"+"\n"+
		                "Synonyms:"+"\n"+
		                "- BHARAT"+"\n"+
		                "- BHARATIYA GANARAJYA"+"\n"+
		                "- INDE"+"\n"+
		                "- INDIA"+"\n"+
		                "- INDIEN"+"\n"+
		                "- REPUBLIC OF INDIA"+"\n"+
		                "STATE:"+"\n"+
		                "Synonyms: none"+"\n"+
		                "ORIGIN:"+"\n"+
		                "EDD_ASIA_PACIFIC"+"\n"+
		                "DESIGNATION:"+"\n"+
		                "GWL"+"\n"+
		                "TYPE:"+"\n"+
		                "Individual"+"\n"+
		                "SEARCH CODES:"+"\n"+
		                "none"+"\n"+
		                "USER DATA 1:"+"\n"+
		                "none"+"\n"+
		                "USER DATA 2:"+"\n"+
		                "none"+"\n"+
		                "OFFICIAL REF:"+"\n"+
		                "2017-11-07 17:36:06 EDA"+"\n"+
		                "PASSPORT:"+"\n"+
		                "none"+"\n"+
		                "BIC CODES:"+"\n"+
		                "none"+"\n"+
		                "NATID:"+"\n"+
		                "none"+"\n"+
		                "PLACE OF BIRTH:"+"\n"+
		                "none"+"\n"+
		                "DATE OF BIRTH:"+"\n"+
		                "none"+"\n"+
		                "NATIONALITY:"+"\n"+
		                "none"+"\n"+
		                "ADDITIONAL INFOS:"+"\n"+
		                "List ID: 1106 / Create Date: 11/07/2017 17:36:06 / Last Update Date:"+"\n"+
		                "11/07/2017 17:36:06 / Org_PID: 8523805 / Title: ARRESTED FOR"+"\n"+
		                "BURGLARY - OCTOBER 30, 2017 / Gender: MALE / OtherInformation:"+"\n"+
		                "According to the timesofindia.indiatimes.com; October 30, 2017: On"+"\n"+
		                "October 30, 2017, Naved Iqbal was arrested for burglary. He and his"+"\n"+
		                "co-conspirators were arrested while planning to commit a robbery in"+"\n"+
		                "Khatauli region of the district. They were involved / Relationship:"+"\n"+
		                "Co-Defendant / OriginalID: 8523809"+"\n"+
		                "FML TYPE:"+"\n"+
		                "1"+"\n"+
		                "FML PRIORITY:"+"\n"+
		                "0"+"\n"+
		                "FML CONFIDENTIALITY:"+"\n"+
		                "0"+"\n"+
		                "FML INFO:"+"\n"+
		                "none"+"\n"+
		                "PEP-FEP:"+"\n"+
		                "0 0"+"\n"+
		                "KEYWORDS:"+"\n"+
		                "OS:ADVERSE_MEDIA NS:NAMESOURCE_WEBSITE ENTITYLEVEL:LEVEL_NA SC:BURGLARY"+"\n"+
		                "HYPERLINKS:"+"\n"+
		                "https://accuity.worldcompliance.com/signin.aspx?ent=ca870ce6-eaa5-4112-ba57-7b6dbeadf8b4"+"\n"+
		                "TYS: 1"+"\n"+
		                "ISN: 0"+"\n"+
		                "============================="+"\n"+
		                " Suspect detected #2"+"\n"+
		                "OFAC ID:AS06990130"+"\n"+
		                "MATCH: 775.00"+"\n"+
		                "TAG: NAM"+"\n"+
		                "MATCHINGTEXT: IQBAL, NAVEED,"+"\n"+
		                "RESULT: (0)"+"\n"+
		                "NAME: IQBAL, NAVED"+"\n"+
		                "Synonyms: none"+"\n"+
		                "ADDRESS:"+"\n"+
		                "Synonyms: none"+"\n"+
		                "CITY:"+"\n"+
		                "Synonyms: none"+"\n"+
		                "COUNTRY: INDIA"+"\n"+
		                "Synonyms:"+"\n"+
		                "- BHARAT"+"\n"+
		                "- BHARATIYA GANARAJYA"+"\n"+
		                "- INDE"+"\n"+
		                "- INDIA"+"\n"+
		                "- INDIEN"+"\n"+
		                "- REPUBLIC OF INDIA"+"\n"+
		                "STATE:"+"\n"+
		                "Synonyms: none"+"\n"+
		                "ORIGIN:"+"\n"+
		                "EDD_ASIA_PACIFIC"+"\n"+
		                "DESIGNATION:"+"\n"+
		                "GWL"+"\n"+
		                "TYPE:"+"\n"+
		                "Individual"+"\n"+
		                "SEARCH CODES:"+"\n"+
		                "none"+"\n"+
		                "USER DATA 1:"+"\n"+
		                "none"+"\n"+
		                "USER DATA 2:"+"\n"+
		                "none"+"\n"+
		                "OFFICIAL REF:"+"\n"+
		                "2017-11-07 17:36:06 EDA"+"\n"+
		                "PASSPORT:"+"\n"+
		                "none"+"\n"+
		                "BIC CODES:"+"\n"+
		                "none"+"\n"+
		                "NATID:"+"\n"+
		                "none"+"\n"+
		                "PLACE OF BIRTH:"+"\n"+
		                "none"+"\n"+
		                "DATE OF BIRTH:"+"\n"+
		                "none"+"\n"+
		                "NATIONALITY:"+"\n"+
		                "none"+"\n"+
		                "ADDITIONAL INFOS:"+"\n"+
		                "List ID: 1106 / Create Date: 11/07/2017 17:36:06 / Last Update Date:"+"\n"+
		                "11/07/2017 17:36:06 / Org_PID: 8523805 / Title: ARRESTED FOR"+"\n"+
		                "BURGLARY - OCTOBER 30, 2017 / Gender: MALE / OtherInformation:"+"\n"+
		                "According to the timesofindia.indiatimes.com; October 30, 2017: On"+"\n"+
		                "October 30, 2017, Naved Iqbal was arrested for burglary. He and his"+"\n"+
		                "co-conspirators were arrested while planning to commit a robbery in"+"\n"+
		                "Khatauli region of the district. They were involved / Relationship:"+"\n"+
		                "Co-Defendant / OriginalID: 8523809"+"\n"+
		                "FML TYPE:"+"\n"+
		                "1"+"\n"+
		                "FML PRIORITY:"+"\n"+
		                "0"+"\n"+
		                "FML CONFIDENTIALITY:"+"\n"+
		                "0"+"\n"+
		                "FML INFO:"+"\n"+
		                "none"+"\n"+
		                "PEP-FEP:"+"\n"+
		                "0 0"+"\n"+
		                "KEYWORDS:"+"\n"+
		                "OS:ADVERSE_MEDIA NS:NAMESOURCE_WEBSITE ENTITYLEVEL:LEVEL_NA SC:BURGLARY"+"\n"+
		                "HYPERLINKS:"+"\n"+
		                "https://accuity.worldcompliance.com/signin.aspx?ent=ca870ce6-eaa5-4112-ba57-7b6dbeadf8b4"+"\n"+
		                "TYS: 1"+"\n"+
		                "ISN: 0"+"\n"+
		                "============================="+"\n"+
		                "*** INTERNAL OFAC DETAILS ***                              "+"\n"+
		                "HasSndRcvIn                                                "+"\n"+
		                "Limited: 0                                                 "+"\n"+
		                "|AS02251007|550.00|NAM|29|40|-1|-1|-1|-1|-1|-1|-1|-1|      "+"\n"+
		                "|BL00290657|720.00|NAM|50|51|-1|-1|-1|-1|-1|-1|-1|-1|      "+"\n"+
		                "]]</Value>"+"\n"+
		                "</Attribute>"+
	                "</UIDDetials>"+
		            "</CreateWorkitemReq>"+
				"</EE_EAI_MESSAGE>";*/
		
		

	String DAO_UpdateWI_Disp_1 = "<EE_EAI_MESSAGE>"+"\n"+
   "<EE_EAI_HEADER>"+"\n"+
      "<MsgFormat>UPDATE_SR</MsgFormat>"+"\n"+
      "<MsgVersion>0000</MsgVersion>"+"\n"+
      "<RequestorChannelId>IB</RequestorChannelId>"+"\n"+
      "<RequestorUserId>RAKUSER</RequestorUserId>"+"\n"+
      "<RequestorLanguage>E</RequestorLanguage>"+"\n"+
      "<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+"\n"+
      "<ReturnCode>0000</ReturnCode>"+"\n"+
      "<ReturnDesc>Success</ReturnDesc>"+"\n"+
      "<MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>"+"\n"+
      "<Extra1>REQ || IB.123</Extra1>"+"\n"+
      "<Extra2>2011-02-08T15:31:38.818+05:30</Extra2>"+"\n"+
   "</EE_EAI_HEADER>"+"\n"+
   "<UpdateWorkitemReq>"+"\n"+
   "<ProcessName>DigitalAO</ProcessName>"+"\n"+
   "<SubProcess>Dispatch</SubProcess>"+"\n"+
   "<Attributes>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>WINUMBER</Name>"+"\n"+
   "<Value>DigitalAO-0000002386-process</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>Delivery_Status</Name>"+"\n"+
   "<Value>Delivered</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>Status_Code</Name>"+"\n"+
   "<Value>1</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "</Attributes>"+"\n"+
   "<Emirates_ID_PING_data>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>emirates_id</Name>"+"\n"+
   "<Value>784197772697591</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>expiry_date</Name>"+"\n"+
   "<Value>2031-10-17</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>card_number</Name>"+"\n"+
   "<Value>10000000</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>gender</Name>"+"\n"+
   "<Value>M</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>name</Name>"+"\n"+
   "<Value>Jhon Smith</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>Nationality</Name>"+"\n"+
   "<Value>India</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "</Emirates_ID_PING_data>"+"\n"+
   "<Documents>"+"\n"+
   "<Document>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>DocumentName</Name>"+"\n"+
   "<Value>Emirates ID</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>Document_type</Name>"+"\n"+
   "<Value>jpg</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "<Attribute>"+"\n"+
   "<Name>Document_url</Name>"+"\n"+
   "<Value>https://teamex.s3.ap-southeast-1.amazonaws.com/2022/03/testawb001-1.jpg</Value>"+"\n"+
   "</Attribute>"+"\n"+
   "</Document>"+"\n"+
   "</Documents>"+"\n"+
   "</UpdateWorkitemReq>"+"\n"+
"</EE_EAI_MESSAGE>";

				
				

		String dcc_firco_udpate="<EE_EAI_MESSAGE>"+
				"<EE_EAI_HEADER>" +
				"<MsgFormat>UPDATE_SR</MsgFormat>" +
				"<MsgVersion>0000</MsgVersion>" +
				"<RequestorChannelId>EBC.WBA</RequestorChannelId>" +
				"<RequestorUserId>RAKUSER</RequestorUserId>" +
				"<RequestorLanguage>E</RequestorLanguage>" +
				"<RequestorSecurityInfo>secure</RequestorSecurityInfo>" +
				"<ReturnCode>0000</ReturnCode>" +
				"<ReturnDesc>Success</ReturnDesc>" +
				"<MessageId>CAS165536806634681</MessageId>" +
				"<Extra1>REQ||DEH.123</Extra1>" +
				"<Extra2>2015-05-30T22:30:14.544+05:30</Extra2>" +
				"</EE_EAI_HEADER>" +
				"<GetWorkitemStatusReq>" +
					"<ProcessName>Digital_CC</ProcessName>" +
					"<SubProcess>FIRCO_STATUS_UPDATE</SubProcess>" +
					"<Attributes>" +
						"<Attribute>" +
							"<Name>WINUMBER</Name>" +
							"<Value>DCC-0000000066-process</Value>" +
						"</Attribute>" +
						"<Attribute>" +
							"<Name>FIRCO_Status</Name>" +
							"<Value>False Positive</Value>" +
						"</Attribute>" +
						"<Attribute>" +
							"<Name>Reject_reason</Name>" +
							"<Value>Confirmed</Value>" +
						"</Attribute>" +
					"</Attributes>" +
					"</GetWorkitemStatusReq>" +
				"</EE_EAI_MESSAGE>";
		
		String message_DAO_WIUpdate_stp = "<EE_EAI_HEADER>"+"\n"+
		"<EE_EAI_HEADER>"+"\n"+
		"<MsgFormat>UPDATE_SR</MsgFormat>"+"\n"+
		"<MsgVersion>0000</MsgVersion>"+"\n"+
		"<RequestorChannelId>IB</RequestorChannelId>"+"\n"+
		"<RequestorUserId>RAKUSER</RequestorUserId>"+"\n"+
		"<RequestorLanguage>E</RequestorLanguage>"+"\n"+
		"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+"\n"+
		"<ReturnCode>0000</ReturnCode>"+"\n"+
		"<ReturnDesc>Success</ReturnDesc>"+"\n"+
		"<MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>"+"\n"+
		"<Extra1>REQ || IB.123</Extra1>"+"\n"+
		"<Extra2>2011-02-08T15:31:38.818+05:30</Extra2>"+"\n"+
		"</EE_EAI_HEADER>"+"\n"+
		"<UpdateWorkitemReq>"+"\n"+
		"<ProcessName>DigitalAO</ProcessName>"+"\n"+
		"<SubProcess>DigitalAO</SubProcess>"+"\n"+
		"<Attributes>"+"\n"+
		"<Attribute>"+"\n"+
		"<Name>WINUMBER</Name>"+"\n"+
		"<Value>DigitalAO-0000004101-process</Value>"+"\n"+
		"</Attribute>"+"\n"+
		"<Attribute>"+"\n"+
		"<Name>Prospect_id</Name>"+"\n"+
		"<Value>13618</Value>"+"\n"+
		"</Attribute>"+"\n"+
		"<Attribute>"+"\n"+
		"<Name>Sol_Id</Name>"+"\n"+
		"<Value>019</Value>"+"\n"+
		"</Attribute>"+"\n"+
		"<Attribute>"+"\n"+
		"<Name>CIF</Name>"+"\n"+
		"<Value>2996290</Value>"+"\n"+
		"</Attribute>"+"\n"+
		"<Attribute>"+"\n"+
		"<Name>Event</Name>"+"\n"+
		"<Value>Account Created</Value>"+"\n"+
		"</Attribute>"+"\n"+
		"<Attribute>"+"\n"+
		"<Name>Additional_document_received</Name>"+"\n"+
		"<Value>N</Value>"+"\n"+
		"</Attribute>"+"\n"+
		"<Attribute>"+"\n"+
		"<Name>Account_Number</Name>"+"\n"+
		"<Value>0312996290001</Value>"+"\n"+
		"</Attribute>"+"\n"+
		"<Attribute>"+"\n"+
		"<Name>ChequeBk_ref</Name>"+"\n"+
		"<Value>1565066</Value>"+"\n"+
		"</Attribute>"+"\n"+
		"<Attribute>"+"\n"+
		"<Name>PEP</Name>"+"\n"+
		"<Value>N</Value>"+"\n"+
		"</Attribute>"+"\n"+
		"<Attributes>"+"\n"+
		"</UpdateWorkitemReq>"+"\n"+
		"</EE_EAI_HEADER>";
		
		
		String message_DAO_WIUpdate_nstp = "<EE_EAI_HEADER>"+"\n"+
				"<EE_EAI_HEADER>"+"\n"+
				"<MsgFormat>UPDATE_SR</MsgFormat>"+"\n"+
				"<MsgVersion>0000</MsgVersion>"+"\n"+
				"<RequestorChannelId>IB</RequestorChannelId>"+"\n"+
				"<RequestorUserId>RAKUSER</RequestorUserId>"+"\n"+
				"<RequestorLanguage>E</RequestorLanguage>"+"\n"+
				"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+"\n"+
				"<ReturnCode>0000</ReturnCode>"+"\n"+
				"<ReturnDesc>Success</ReturnDesc>"+"\n"+
				"<MessageId>UNIQUE Identifier(RQ-DDMMYYYYHHMMSSS)</MessageId>"+"\n"+
				"<Extra1>REQ || IB.123</Extra1>"+"\n"+
				"<Extra2>2011-02-08T15:31:38.818+05:30</Extra2>"+"\n"+
				"</EE_EAI_HEADER>"+"\n"+
				"<UpdateWorkitemReq>"+"\n"+
				"<ProcessName>DigitalAO</ProcessName>"+"\n"+
				"<SubProcess>DigitalAO</SubProcess>"+"\n"+
				"<Attributes>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>WINUMBER</Name>"+"\n"+
				"<Value>DigitalAO-0000000945-process</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Prospect_id</Name>"+"\n"+
				"<Value>6058</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Sol_Id</Name>"+"\n"+
				"<Value>A7475</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Event</Name>"+"\n"+
				"<Value>Account Created</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Remarks_dec</Name> "+
				"<Value>Additional details provided</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Reject_reason</Name> "+
				"<Value>Application</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Previous_Designation</Name>"+"\n"+
				"<Value>IDK</Value>"+"\n"+
				"</Attribute>"+
				"<Attribute>"+"\n"+
				"<Name>Relation_Detail_w_PEP</Name>"+"\n"+
				"<Value>Relations</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Name_of_PEP</Name>"+"\n"+
				"<Value>PEP</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>country_pep_hold_status</Name>"+"\n"+
				"<Value>AE</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Emirate_pep_hold_status</Name>"+"\n"+
				"<Value>DXB</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>CIF</Name>"+"\n"+
				"<Value>1212</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>IBAN</Name>"+"\n"+
				"<Value>12123534645645</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Account_Number</Name>"+"\n"+
				"<Value>1234</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>investment_portfolio_including_virtual_asset</Name>"+"\n"+
				"<Value>4354</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>income_generated</Name>"+"\n"+
				"<Value>Y</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>real_Est_owned</Name>"+"\n"+
				"<Value>Y</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>rental_income</Name>"+"\n"+
				"<Value>Y</Value>"+"\n"+
				"</Attribute><Attribute>"+"\n"+
				"<Name>title_deed_attached</Name>"+"\n"+
				"<Value>T</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>other_Source_of_income</Name>"+"\n"+
				"<Value>T</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Additional_document_received</Name>"+"\n"+
				"<Value>N</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Desc_spec_prod_serv_your_cmpny_deals</Name>"+"\n"+
				"<Value>R</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>virtual_Card_issued</Name>"+"\n"+
				"<Value>Y</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"</Attributes>"+"\n"+
				"<Additional_Document_Req>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Document_name</Name>"+"\n"+
				"<Value>PEP_Form</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Status</Name>"+"\n"+
				"<Value>Received</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Remarks</Name>"+"\n"+
				"<Value>yes</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"</Additional_Document_Req>"+"\n"+
				"<Additional_Document_Req>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Document_name</Name>"+"\n"+
				"<Value>W9_form</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Status</Name>"+"\n"+
				"<Value>required</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"<Attribute>"+"\n"+
				"<Name>Remarks</Name>"+"\n"+
				"<Value>yes</Value>"+"\n"+
				"</Attribute>"+"\n"+
				"</Additional_Document_Req>"+"\n"+
				"</UpdateWorkitemReq>"+"\n"+
				"</EE_EAI_HEADER>";
		
		
		String DCC_FTS_update="<EE_EAI_MESSAGE>"+"\n"+
				"    <EE_EAI_HEADER>"+"\n"+
				"        <MsgFormat>UPDATE_SR</MsgFormat>"+"\n"+
				"        <MsgVersion>0000</MsgVersion>"+"\n"+
				"        <RequestorChannelId>EBC.WBA</RequestorChannelId>"+"\n"+
				"        <RequestorUserId>RAKUSER</RequestorUserId>"+"\n"+
				"        <RequestorLanguage>E</RequestorLanguage>"+"\n"+
				"        <RequestorSecurityInfo>secure</RequestorSecurityInfo>"+"\n"+
				"        <ReturnCode>911</ReturnCode>"+"\n"+
				"        <ReturnDesc>Issuer Timeout</ReturnDesc>"+"\n"+
				"        <MessageId>RPA7BYOX1683276941720</MessageId>"+"\n"+
				"        <Extra1>REQ||RAK.VTUSER</Extra1>"+"\n"+
				"        <Extra2>2023-05-05T12:55:41.732+05:30</Extra2>"+"\n"+
				"    </EE_EAI_HEADER>"+"\n"+
				"    <UpdateWorkitemReq>"+"\n"+
				"        <ProcessName>Digital_CC</ProcessName>"+"\n"+
				"        <SubProcess>STATEMENT_ANALYZED</SubProcess>"+"\n"+
				"        <Attributes>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>WINUMBER</Name>"+"\n"+
				"                <Value>DCC-0000006232-process</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Prospect_id</Name>"+"\n"+
				"                <Value>124868</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Stmt_Salary_1</Name>"+"\n"+
				"                <Value>8500.00</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Stmt_salary1_date</Name>"+"\n"+
				"                <Value>2023-03-06</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Stmt_salary_2</Name>"+"\n"+
				"                <Value>8500.00</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Stmt_salary2_date</Name>"+"\n"+
				"                <Value>2023-02-07</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Stmt_salary_3</Name>"+"\n"+
				"                <Value>8500.00</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Stmt_salary3_date</Name>"+"\n"+
				"                <Value>2023-01-09</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Addn_Perfios_EMI_1</Name>"+"\n"+
				"                <Value>2500.0</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Addn_Perfios_EMI_2</Name>"+"\n"+
				"                <Value>2500.0</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Addn_Perfios_EMI_3</Name>"+"\n"+
				"                <Value>8000.0</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Addn_Perfios_CC</Name>"+"\n"+
				"                <Value>N</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Joint_Acct</Name>"+"\n"+
				"                <Value>N</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Stmt_chq_rtn_last_3mnts</Name>"+"\n"+
				"                <Value>0</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Stmt_chq_rtn_cleared_in30_last_3mnts</Name>"+"\n"+
				"                <Value>0</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Stmt_chq_rtn_last_1mnt</Name>"+"\n"+
				"                <Value>0</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Stmt_chq_rtn_cleared_in30_last_1mnt</Name>"+"\n"+
				"                <Value>0</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Pensioner</Name>"+"\n"+
				"                <Value>N</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>Name_match</Name>"+"\n"+
				"                <Value>N</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>FCU_indicator</Name>"+"\n"+
				"                <Value>N</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"            <Attribute>"+"\n"+
				"                <Name>UW_reqd</Name>"+"\n"+
				"                <Value>Y</Value>"+"\n"+
				"            </Attribute>"+"\n"+
				"        </Attributes>"+"\n"+
				"    </UpdateWorkitemReq>"+"\n"+
				"</EE_EAI_MESSAGE>";

		String WI_Up="<EE_EAI_MESSAGE>"+
		"<EE_EAI_HEADER>"+
		"<MsgFormat>UPDATE_SR</MsgFormat>"+
		"<MsgVersion>0000</MsgVersion>"+
		"<RequestorChannelId>EBC.WBA</RequestorChannelId>"+
		"<RequestorUserId>RAKUSER</RequestorUserId>"+
		"<RequestorLanguage>E</RequestorLanguage>"+
		"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
		"<ReturnCode>9999</ReturnCode>"+
		"<ReturnDesc>Request</ReturnDesc>"+
		"<MessageId>RPAUKHIQ1695886498499</MessageId>"+
		"<Extra1>REQ||RAK.cntrlm</Extra1>"+
		"<Extra2>2023-09-28T11:34:58.499+05:30</Extra2>"+
		"</EE_EAI_HEADER>"+
		"<UpdateWorkitemReq>"+
		"<ProcessName>Digital_CC</ProcessName>"+
		"<SubProcess>UPDATE_CIF_CARD_Details</SubProcess>"+
		"<Attributes>"+
		"<Attribute>"+
		"<Name>WINUMBER</Name>"+
		"<Value>DCC-0000016092-process</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>Prospect_id</Name>"+
		"<Value>68434</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>CIF</Name>"+
		"<Value>3012807</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>ECRN</Name>"+
		"<Value>114496700</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>CRN</Name>"+
		"<Value>114496700</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>Card_Number</Name>"+
		"<Value>5363179919206005</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>Updated_CardType_Code</Name>"+
		"<Value>RED_CARD</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>Updated_CardType_Desc</Name>"+
		"<Value>REDCARD-UAEN</Value>"+
		"</Attribute>"+
		"</Attributes>"+
		"</UpdateWorkitemReq>"+
		"</EE_EAI_MESSAGE>";
		
		String WI_Up_no_act="<EE_EAI_MESSAGE>"+
		"<EE_EAI_HEADER>"+
		"<MsgFormat>UPDATE_SR</MsgFormat>"+
		"<MsgVersion>0000</MsgVersion>"+
		"<RequestorChannelId>EBC.WBA</RequestorChannelId>"+
		"<RequestorUserId>RAKUSER</RequestorUserId>"+
		"<RequestorLanguage>E</RequestorLanguage>"+
		"<RequestorSecurityInfo>secure</RequestorSecurityInfo>"+
		"<ReturnCode>9999</ReturnCode>"+
		"<ReturnDesc>Request</ReturnDesc>"+
		"<MessageId>RPAUKHIQ1695886498499</MessageId>"+
		"<Extra1>REQ||RAK.cntrlm</Extra1>"+
		"<Extra2>2023-09-28T11:34:58.499+05:30</Extra2>"+
		"</EE_EAI_HEADER>"+
		"<UpdateWorkitemReq>"+
		"<ProcessName>Digital_CC</ProcessName>"+
		"<SubProcess>CARD_DETAILS_NO_ACTN</SubProcess>"+
		"<Attributes>"+
		"<Attribute>"+
		"<Name>WINUMBER</Name>"+
		"<Value>DCC-0000016018-process</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>Prospect_id</Name>"+
		"<Value>68434</Value>"+
		"</Attribute>"+
		"<Attribute>"+
		"<Name>Card_Change_No_Action</Name>"+
		"<Value>Y</Value>"+
		"</Attribute>"+
		"</Attributes>"+
		"</UpdateWorkitemReq>"+
		"</EE_EAI_MESSAGE>";
		
		System.out.println("message : "+WI_Up_no_act);
		String response =new CreateWorkitem().getResponseOffshore(WI_Up_no_act);
		System.out.println("Response "+response);
	}
}
