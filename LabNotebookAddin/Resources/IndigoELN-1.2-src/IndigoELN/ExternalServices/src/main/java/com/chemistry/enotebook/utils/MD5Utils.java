package com.chemistry.enotebook.utils;

import java.security.MessageDigest;

public class MD5Utils {

	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static final String MD5 = "MD5";

	public static String md5Hex(String s) {
		String result = "";

		try {
			MessageDigest md = MessageDigest.getInstance(MD5);
			byte[] d = md.digest(s.getBytes());
			result = stringToHex(d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private static String stringToHex(byte[] buf) {
		char[] chars = new char[2 * buf.length];

		for (int i = 0; i < buf.length; ++i) {
			chars[2 * i] = HEX[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX[buf[i] & 0x0F];
		}

		return new String(chars);
	}
}
