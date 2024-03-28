package com.newgen.iforms.user;

import java.io.FileNotFoundException;

import com.itextpdf.io.exceptions.IOException;
import com.newgen.iforms.custom.IFormReference;

public class FetchDectech extends Digital_CC_Common{
	
	public String onclickevent(IFormReference iformObj, String control, String StringData) throws FileNotFoundException, IOException{
		String returnValue = "";
		
		iformObj.setValue("CRN", "Test123");
		return "Success";
	}

}
