package com.newgen.encryption;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import java.util.*;


public class PropertiesEncryption {
	public static String key="odGmu5LQzyGBqptMi4zNuQ==";
	public static String encrypt(String plainText) {
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			SecretKey desKey = skf.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, desKey);
	        byte[] cipherText = cipher.doFinal(plainText.getBytes(Charset.forName("UTF-8")));
	        
	       // byte[] encode = BASE64EncoderStream.encode(cipherText);
	        byte[] encode = Base64.getEncoder().encode(cipherText);
	        cipherText=encode;
	        return new String(cipherText);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public static String decrypt(String encryptedText) {
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
			SecretKey desKey = skf.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, desKey);
	        //byte[] cipherText=new byte[64];
	        //byte[] cipherText = BASE64DecoderStream.decode(encryptedText.getBytes(Charset.forName("UTF-8")));
	        byte[] cipherText = Base64.getDecoder().decode(encryptedText.getBytes(Charset.forName("UTF-8")));
	        System.out.println(cipherText.length);
	        String decryptedString = new String(cipher.doFinal(cipherText),"UTF-8");
	        return decryptedString;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException{
		String abc = "FsNs5g6zapl/yQPuThN/BgnhlsW8Twz9v45sNg03pFVoEnMtfY1zqe1qJ3hkOtVZQs7R5Hhlslr60sdfgSedIhnPX1x2PxCD8PUQie89ypkWd0q06sVSU3CKLEYRTGpVMNTMtH2LBkuuMbr320B2tNNFhQX+hJJxkPv5UlPhRATX807snyC/jVFq6J3dfR75OsI3DxMeDq5cTi+8uf6u5OPCquXeJ2a2IvFCI7Wfpum7GR1ZGbq7OjT3lJs2yGCxFTRtfabaEL4ar5N19YzDxPzpIMVetMBN1A3nnQlTEZD66JnL0268C/wFblgfrQFqnZlZNIA+K1m38kZE2lnCpcB8exIteKOqjYZ6mCnBnnl0A6xWdyNoOW9WuM5VxYsL/R3VWlBMO2mw0yqTq2iRblRcSI3GctD0L5bhy23hykWgoadnh6p23Jh1u0Hsb+EhFcpVZUQLXhlMWgmpBm9YDhLjbP+moLs7X+5AmulYH3sYXX6F0bQ5ifRawMBAZRB8fDwIekDsKroRrp1pcOJGjPAYAbwzwlqd8V6wrqVg3KdQ7tBMNfROCHqnCAwFUXOiLO1CSnCk23WCOmdLoXyDDaKriKmUuzbGOTgtXtGwTRoH+1fTh3/9xFSoDnpqCOt4RJAPAxrz5SPEOnDaTW0NzJ1pMieobQM3G+2uUcAvpZAK/3rShRZ745sjojeRC7HbAWx3jTdhFJvVMUyaiWkJY2806KU3T1DXOFisqrkqgD4uhXLDXzXCQplaMOPRMkIKWwgm7RcTXemgQDpaWyBSxmBR1nlto7NPLvh/vpHOErM/8p+ZcoOfC49BHwuSLCvDew==";
		PropertiesEncryption abc1 = new PropertiesEncryption();
		System.out.println(abc1.decrypt("fpqNJePE9odiIssgTZvcZg=="));
		
	}
	
}
