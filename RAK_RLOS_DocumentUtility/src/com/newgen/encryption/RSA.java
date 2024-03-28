package com.newgen.encryption;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {
	public static void getKeyPairInFile(String publicFileName,String privateFileName) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		KeyPair kp = kpg.genKeyPair();
		Key publicKey = kp.getPublic();
		Key privateKey = kp.getPrivate();
		KeyFactory keyFact = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec pub = keyFact.getKeySpec(publicKey, RSAPublicKeySpec.class);
		RSAPrivateKeySpec priv = keyFact.getKeySpec(privateKey, RSAPrivateKeySpec.class);
		try {
			saveToFile(publicFileName, pub.getModulus(), pub.getPublicExponent());
			saveToFile1(privateFileName, priv.getModulus(), priv.getPrivateExponent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveToFile(String fileName, BigInteger mod, BigInteger exp)
			throws FileNotFoundException, IOException {
		ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\Program Files\\Java\\jre6\\lib\\security\\"+fileName)));
		try {
			oout.writeObject(mod);
			oout.writeObject(exp);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		} finally {
			oout.close();
		}
	}

	public static void saveToFile1(String fileName, BigInteger mod, BigInteger exp)
			throws FileNotFoundException, IOException {
		ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\Program Files\\Java\\jre6\\lib\\security\\"+fileName)));
		try {
			oout.writeObject(mod);
			oout.writeObject(exp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			oout.close();
		}
	}

	public static PublicKey readFromPublicKeyFile(String keyFileName)
			throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
		File f = new File(keyFileName);
		FileInputStream in = new FileInputStream(f);
		ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
		BigInteger m = (BigInteger) oin.readObject();
		BigInteger e = (BigInteger) oin.readObject();
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
		KeyFactory fact = KeyFactory.getInstance("RSA");
		PublicKey pub = fact.generatePublic(keySpec);
		oin.close();
		return pub;
	}

	public static PrivateKey readFromPrivateKeyFile(String keyFileName)
			throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
		File f = new File(keyFileName);
		FileInputStream in = new FileInputStream(f);
		ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
		BigInteger m = (BigInteger) oin.readObject();
		BigInteger e = (BigInteger) oin.readObject();
		RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
		KeyFactory fact = KeyFactory.getInstance("RSA");
		PrivateKey priv = fact.generatePrivate(keySpec);
		oin.close();
		return priv;
	}

	public static byte[] rsaEncrypt(byte[] data)
			throws ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException, IOException,
			InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		PublicKey pubKey = readFromPublicKeyFile("public.pub");
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		byte[] cipherData = cipher.doFinal(data);
		return cipherData;
	}

	public static byte[] rsaDecrypt(byte[] data)
			throws ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException, IOException,
			InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		PrivateKey pubKey = readFromPrivateKeyFile("private.key");
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, pubKey);
		byte[] cipherData = cipher.doFinal(data);
		return cipherData;
	}
	public static void main(String args[])
	{
		try
		{
			getKeyPairInFile("public.pub","private.key");
			String input="Hello";
			byte[] input1=input.getBytes();
			byte[] encrypted=rsaEncrypt(input1);
			byte[] decrypted=rsaDecrypt(encrypted);
			System.out.println(new String(decrypted));
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
