package com.newgen.encryption;

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
import javax.crypto.Cipher;

import com.newgen.DigitalCC.Document.DCCDocument;

/*Written by Kamran for Decrypting Document 20/03/2023*/
public class DecryptDoc {
	
	public  String decrypt( String cipherText) throws  NoSuchProviderException, IOException {
		cipherText=cipherText.replaceAll(System.lineSeparator(),"").replaceAll("\\s+","");
		
  		PublicKey publicKey = getPublickey();
		Cipher cipher;
		String decryptVal = "";
		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			decryptVal = new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
			System.out.println("Decrypted value --->"+decryptVal);
		} catch (Exception e) {
			//e.printStackTrace();
			e.printStackTrace();
		}
		return decryptVal;

	}

	public  PublicKey getPublickey() throws IOException  {
	    String staticPublicKey = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+ File.separator + "ConfigFiles"+ File.separator+ "public.pem")));
	    staticPublicKey = staticPublicKey.replaceAll(System.lineSeparator(),"");
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
	
	public String decryptDocument (String statement_key) throws NoSuchProviderException, IOException{
		DecryptDoc decryptDoc = new DecryptDoc();
		return decryptDoc.decrypt(statement_key);
	}
	
	public static void main(String[] args) throws NoSuchProviderException, IOException{
		DecryptDoc decryptDoc = new DecryptDoc();
		String abc = decryptDoc.decryptDocument("qGhJO/GqftgKNpL8kzDWu8FjTkNfRnZ7iG66L1TCLF4sJvBm3iAJL7VDoVJMzVQo+g5fw+KRoV8hQYmHzo5tN8xLvZUqFnOFOjJ8XVtY/ZhvYr2ULP+uV+dihmLouahp 8+eOL5oc/MuzNSulVghGZco02GheWh0EbaxyKFhzsHT8LrYrx9P4f+f803B84tpTmgUOSUZeC1p+to0hzLWg+sKGwpggA5O7cSI8u0vjsoaWwStoAuASeE+KRlrygJr0atKX +Cub6Eyj2uUKLUwDOyVmYQEZqkf5UvTKMHhYnHQAeQLRw4U1K14fy11OQ/5GTp7000CfkdEUQFh5SRRLug==");
		System.out.println(abc);
	}
 

}
