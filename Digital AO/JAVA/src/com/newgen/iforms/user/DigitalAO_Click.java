package com.newgen.iforms.user;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.newgen.iforms.custom.IFormReference;

public class DigitalAO_Click extends DigitalAO_Common {

	public String clickEvent(IFormReference iform, String controlName, String data) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
		DigitalAO.mLogger.debug("DigitalAO_Clicks");
		DigitalAO.mLogger.debug("WINAME : " + getWorkitemName(iform) + ", WSNAME: " + getActivityName(iform) + ", controlName " + controlName + ", data " + data);
		DigitalAO.mLogger.debug("controlName" + controlName);
		String successIndicator = "FAIL";
		
		if (controlName.equalsIgnoreCase("template_generate")) 
		{
			return new DAOTemplate().clickEvent(iform, "template_generate", data);
			
		} else if (controlName.equalsIgnoreCase("Risk_score_trigger")) 
		{
			DigitalAO.mLogger.debug("RISK_SCORE_DETAILS : Click: ");
			String currDate = new SimpleDateFormat("dd/MM/YYYY").format(new Date());
			DigitalAO.mLogger.debug("currDate KYC : " + currDate);
			setControlValue("KYC_review_Date", currDate, iform);

			return new DigitalAO_Integration().onclickevent(iform, controlName, data);
			
		} else if (controlName.equalsIgnoreCase("sign_upload")) 
		{
			return new DigitalAO_Integration().onclickevent(iform, controlName, data);
		}
		else if (controlName.equalsIgnoreCase("CIF_update")) 
		{
			return new DigitalAO_Integration().onclickevent(iform, controlName, data);
		}else if(controlName.equalsIgnoreCase("Generate_firco_temp"))
		{
			return new DAOTemplate().clickEvent(iform, "Generate_firco_temp", data);
		}
		//added by gaurav for dedupe template
		else if(controlName.equalsIgnoreCase("template_generate_dedupe"))
		{
			return new DAOTemplate().clickEvent(iform, "template_generate_dedupe", data);
		}
		
		else if(controlName.equalsIgnoreCase("Generate_RiskScore")){
			return new DAOTemplate().clickEvent(iform, "Generate_RiskScore", data);
		}
		
		//vinayak chnage starts internal exposure		
		else if(controlName.equalsIgnoreCase("InternalExposure"))
		{
			return new DigitalAO_InternalExposure().onclickevent(iform, "InternalExposure", data);
		}
		//vinayak chnage starts internal exposure

		return successIndicator;
	}
}
