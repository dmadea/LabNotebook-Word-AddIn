/****************************************************************************
 * Copyright (C) 2009-2015 EPAM Systems
 * 
 * This file is part of Indigo ELN.
 * 
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 * 
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ***************************************************************************/
/*
 * Created on 08-Oct-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EncryptionUtil {
    // 8-byte Salt
    static byte[] salt = {
        (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
        (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
    };

    // Iteration count
    static int iterationCount = 19;
	static String passPhrase = "CeN Page Encryption";
	
	public static Cipher ecipher;
	public static Cipher dcipher;
	
	public static boolean encryptObjectToFile(FileOutputStream fout, Object notebookObject) 
	{
		try {
			if (ecipher == null)  createCiphers();

			BufferedOutputStream bos = new BufferedOutputStream(fout);
			CipherOutputStream cos = new CipherOutputStream(bos, ecipher);
			ObjectOutputStream oos = new ObjectOutputStream(cos);
			
			// Write object
			oos.writeObject(notebookObject);
			oos.close();
		} catch (Exception e) {
			return false;
		}		
		return true;
	}
	
	public static Object decryptObjectFromFile(FileInputStream fis) 
	{
		try {
			if (dcipher == null)  createCiphers();

			// Create streams
			BufferedInputStream bis = new BufferedInputStream(fis);
			CipherInputStream cis = new CipherInputStream(bis, dcipher);
			ObjectInputStream ois = new ObjectInputStream(cis);
			
			// Read object
			Object notebookObject = ois.readObject();
			ois.close();
			
			return notebookObject;
		} catch (Exception e) {
			return null;
		}
	}
	
	private static void createCiphers() 
		throws Exception 
	{
		try {
			java.security.Security.addProvider(new com.sun.crypto.provider.SunJCE());
			
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            
            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());
            
            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            // Create the ciphers
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (java.security.InvalidAlgorithmParameterException e1) {
        } catch (java.security.spec.InvalidKeySpecException e2) {
        } catch (javax.crypto.NoSuchPaddingException e3) {
        } catch (java.security.NoSuchAlgorithmException e4) {
        } catch (java.security.InvalidKeyException e5) {
        } catch (Exception e6) {
			throw new Exception("Error creating cipher", e6);
		}
	}
	
	public static byte[] encryptBuffer(byte[] input) 
	{
		if (input == null || input.length == 0) return null;

		try {
			if (ecipher == null)  createCiphers();

			return ecipher.doFinal(input);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public static byte[] decryptBuffer(byte[] input) 
	{
		if (input == null || input.length == 0) return null;
		
		try {
			if (dcipher == null)  createCiphers();

			return dcipher.doFinal(input);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public static String encryptBufferBase64(byte[] input) 
	{
		String result = null;
		
		byte[] buffer = encryptBuffer(input);
		if (buffer != null && buffer.length > 0) {
			BASE64Encoder base64Encoder = new BASE64Encoder();
	
			result = base64Encoder.encode(buffer);
		}
			
		return result;
	}

	public static byte[] decryptBufferBase64(String input) 
	{
		byte[] result = null;
		
		if (input != null && input.length() > 0) {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			
			try {
				byte[] buffer = base64Decoder.decodeBuffer(input);
				if (buffer != null && buffer.length > 0)
					result = decryptBuffer(buffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) 
	{
		if (args.length > 0) {
			for (int i=0; i < args.length; i++) {
				String sVal = encryptBufferBase64(args[i].getBytes());
				System.out.println("'" + args[i] + "' encrypted is '" + sVal + 
						"', decrypted '" + new String(decryptBufferBase64(sVal)) + "'");
			}
		} else {
			System.out.println("No Argument provided, performing generic test.");
			
			byte[] val = encryptBuffer("Hello World".getBytes());
			System.out.println("encryptBuffer: " + new String(val));
			System.out.println("decryptBuffer: " + new String(decryptBuffer(val)));
	
			String sVal = encryptBufferBase64("Hello World Again!".getBytes());
			System.out.println("encryptBufferBase64: " + sVal);
			System.out.println("decryptBufferBase64: " + new String(decryptBufferBase64(sVal)));
		}
	}
}