package com.newgen.iforms.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;

import javax.crypto.Cipher;

import com.newgen.iforms.custom.IFormReference;

public class Digital_CC_GetDocPassword extends Digital_CC_Common {
	
	LinkedHashMap<String, String> executeXMLMapMain = new LinkedHashMap<String, String>();
	public static String XMLLOG_HISTORY = "NG_DCC_XMLLOG_HISTORY";
	static int dds_count = 0;
	static int cheque_count = 0;
	
	public String onevent(IFormReference iformObj, String control, String StringData) throws IOException {
		
		String returnValue = "";
	
		String encryptedKey ="";
		
		List encryptedKeyQry = iformObj.getDataFromDB("select statement_key from NG_DCC_EXTTABLE with (NOLOCK)  WHERE Wi_Name='"+getWorkitemName(iformObj)+"'");
		Digital_CC_GetDocPassword digital_CC_GetDocPassword = new Digital_CC_GetDocPassword();
		
		for(int i=0;i<encryptedKeyQry.size();i++)
		{
			List<String> arr1=(List)encryptedKeyQry.get(i);
			encryptedKey=arr1.get(0);
			Digital_CC.mLogger.debug("WINAME : "+getWorkitemName(iformObj)+", WSNAME: "+getActivityName(iformObj)+", value : "+encryptedKey);
			try {
				returnValue= digital_CC_GetDocPassword.decrypt(encryptedKey);
				 // strips off all non-ASCII characters
				returnValue = returnValue.replaceAll("[^\\x00-\\x7F]", "");
				 
				  // erases all the ASCII control characters
				returnValue = returnValue.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
				   
				  // removes non-printable characters from Unicode
				returnValue = returnValue.replaceAll("\\p{C}", "");
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				returnValue="";
			}
		}
		
		return returnValue;
	}
	
	
	//For Decrypting the Document//
	public  String decrypt(String cipherText) throws  NoSuchProviderException, IOException {
		cipherText=cipherText.replaceAll(System.lineSeparator(),"").replaceAll("\\s+","");
		Digital_CC.mLogger.debug("Getting Encrypted Key -- " + cipherText);
		PublicKey publicKey = getPublickey();
		Digital_CC.mLogger.debug("Public Key Received--->"+publicKey);
		Cipher cipher;
		String decryptVal = "";
		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			decryptVal = new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
			Digital_CC.mLogger.debug("Decrypted value --->"+decryptVal);
		} catch (Exception e) {
			Digital_CC.mLogger.debug("Decrypted value Error--->"+e.toString());
		}
		return decryptVal;

	}

	public  PublicKey getPublickey() throws IOException  {
	   //String staticPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1SU1LfVLPHCozMxH2Mo4lgOEePzNm0tRgeLezV6ffAt0gunVTLw7onLRnrq0/IzW7yWR7QkrmBL7jTKEn5u+qKhbwKfBstIs+bMY2Zkp18gnTxKLxoS2tFczGkPLPgizskuemMghRniWaoLcyehkd3qqGElvW/VDL5AaWTg0nLVkjRo9z+40RQzuVaE8AkAFmxZzow3x+VJYKdjykkJ0iT9wCS0DRTXu269V264Vf/3jvredZiKRkgwlL9xNAwxXFg0x/XFw005UWVRIkdgcKWTjpBP2dPwVZ4WWC+9aGVd+Gyn1o0CLelf4rEjGoXbAAEgAqeGUxrcIlbjXfbcmwIDAQAB";
	   String staticPublicKey = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+ File.separator + "")));
	   staticPublicKey=staticPublicKey.replaceAll(System.lineSeparator(),"");
	   Digital_CC.mLogger.debug("Public Key Path---  " +Paths.get(System.getProperty("user.dir")+ File.separator + File.separator + "DCC_public.key"));
	   Digital_CC.mLogger.debug("Public Key ---  "+(staticPublicKey));
		byte[] decoded;
		PublicKey publicKey = null;
		try {
			decoded = Base64.getDecoder().decode(staticPublicKey);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			publicKey = kf.generatePublic(spec);
		} catch (NoSuchAlgorithmException ae) {
			ae.printStackTrace();
		} catch (InvalidKeySpecException ke) {
			 ke.printStackTrace();
		}
		return publicKey;
	}
	
	
}
