/*
Group						: Application Projects 2
Product / Project			: ECO Bank
Module                      : Java File
File Name					: ChequeBook.java
Author                      : Sachin Gupta
Date written (DD/MM/YYYY)   : 10-March-2010
Date modified (DD/MM/YYYY)  : 13-March-2010
Description                 : Java file to Cheque Book Upload.
-------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------------------------
CHANGE HISTORY 
-------------------------------------------------------------------------------------------------------------------
BUG ID                Date			Change By			Change Description 

-------------------------------------------------------------------------------------------------------------------
*/
package com.newgen.generate;

import java.io.File;
import java.io.FileInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class User 
{
	private String username="";
	private String password="";
	
	public String getUsername() 
	{
		return username;
	}
	
	public String getPassword() 
	{
		return password;
	}
	
	public User() 
	{
		try 
		{
			File desFile = new File("Config"+File.separator+"np.dat");
			FileInputStream fis;
			CipherInputStream cis;
			// Read the cipher settings
			File KEY_FILE=new File("Config"+File.separator+"key.data");
			@SuppressWarnings("resource")
			FileInputStream eksis = new FileInputStream( KEY_FILE );
			byte[] temp = new byte[ (int)KEY_FILE.length()];
			int bytesRead = eksis.read(temp);
						   
			byte[] encodedKeySpec = new byte[bytesRead];
			System.arraycopy(temp, 0, encodedKeySpec, 0, bytesRead);

			// Recreate the secret/symmetric key
			SecretKeySpec secretKey = new SecretKeySpec( encodedKeySpec, "DES");
			// Creation of Cipher objects
			Cipher decrypt =
			Cipher.getInstance("DES/ECB/PKCS5Padding");
			decrypt.init(Cipher.DECRYPT_MODE, secretKey);
			// Open the Encrypted file
			fis = new FileInputStream(desFile);
			cis = new CipherInputStream(fis, decrypt);
			byte[] b = new byte[8];
			int i = cis.read(b);
			String sdata=null;
			while (i != -1) 
			{
				sdata=sdata+(new String(b));
				i = cis.read(b);
			}
			String sdataUpper=sdata.toUpperCase();
			String pwdInt="",pwdEnd="",unInt="",unEnd="";
			
			
			pwdInt="<PASSWORD>";
			pwdEnd="</PASSWORD>";
			unInt="<USERNAME>";
			unEnd="</USERNAME>";
			cis.close();
			fis.close();
			
			int m=sdataUpper.indexOf(pwdInt)+pwdInt.length();
			int n=sdataUpper.indexOf(pwdEnd);
			password=sdata.substring(m,n);
		 	int o=sdataUpper.indexOf(unInt)+unInt.length();
			int p=sdataUpper.indexOf(unEnd);
			username=sdata.substring(o,p);
		}
		catch(Exception e)
		{
			System.out.println("Error during reading files = " + e);
			e.printStackTrace();				
		}
	}
}
