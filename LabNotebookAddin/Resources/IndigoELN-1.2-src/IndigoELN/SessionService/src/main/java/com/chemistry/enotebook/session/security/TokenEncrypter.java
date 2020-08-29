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
 * Created on Jul 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.session.security;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Calendar;

public class TokenEncrypter 
{
	private static Cipher ecipher;
    private static Cipher dcipher;
    private static SecretKey key;
    
    static {
    	try {
	    	ecipher = Cipher.getInstance("DES");
	        dcipher = Cipher.getInstance("DES");
	        if(key == null) key = KeyGenerator.getInstance("DES").generateKey();
	        ecipher.init(Cipher.ENCRYPT_MODE, key);
	        dcipher.init(Cipher.DECRYPT_MODE, key);
    	} catch(Exception e)	{
    		e.printStackTrace();
		}
    }

    public static String encrypt(String str) 
    {
        try {
        	// Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return new BASE64Encoder().encode(enc);
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e){  
        } catch (Exception e) {
        }
        return null;
    }

    public static String decrypt(String str) 
    {
        try {
        	// Decode base64 to get bytes
            byte[] dec = new BASE64Decoder().decodeBuffer(str);

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        }catch (Exception e) {
        }
        return null;
    }
 
	public static void main(String[] args) 
	{
		try {
		    // Generate a temporary key. In practice, you would save this key.
		    // See also e464 Encrypting with DES Using a Pass Phrase.

		    // Create encrypter/decrypter class
			//TokenEncrypter te = new TokenEncrypter();
		    String ntName= "user";
		    System.out.println("NT User				" + ntName);
		    long l  = Calendar.getInstance().getTimeInMillis();
		    System.out.println("Timestamp			"+l);
		    System.out.println("Token in plain text		" + ntName+l);
		    // Encrypt
		    String encrypted = TokenEncrypter.encrypt(ntName+l);
		    System.out.println("Encrypted Token			" + encrypted);

		    // Decrypt
		    String decrypted = TokenEncrypter.decrypt(encrypted);
		    System.out.println("Decrypted Token			" + decrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
