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
 * Created on Aug 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * 
 *
 */

public class ZIPUtil {
	public ZIPUtil() {
	}

	public static byte[] zip(byte[] input) {
		ByteArrayOutputStream outStream = null;
		byte[] result = null;
		try {

			outStream = new ByteArrayOutputStream();
			DeflaterOutputStream compresser = new DeflaterOutputStream(
					outStream);
			compresser.write(input, 0, input.length);
			compresser.finish();
		} catch (IOException ex) {
			return null;
		}
		return outStream.toByteArray();
	}

	public static byte[] unZip(byte[] input) {
		ByteArrayInputStream inStream = new ByteArrayInputStream(input);
		InflaterInputStream decompresser = new InflaterInputStream(inStream);
		ByteArrayOutputStream outStream = null;
		try {
			byte[] data = new byte[2048];
			outStream = new ByteArrayOutputStream();
			while (true) {
				int count = decompresser.read(data, 0, 2048);
				if (count <= 0) {
					break;
				}
				outStream.write(data, 0, count);
			}
		} catch (IOException ex) {
			return null;
		}
		return outStream.toByteArray();
	}

	public static void main(String[] args) {
		String testFileName = "C:\\reagents_doc\\myreagents.xml";
		try {
			byte[] original = null;
			java.io.FileInputStream fis = new java.io.FileInputStream(
					testFileName);
			original = new byte[fis.available()];
			fis.read(original);

			System.out.println("Original length: " + original.length);
			byte[] compressed = ZIPUtil.zip(original);
			System.out.println("Compressed length:" + compressed.length);

			byte[] UnCompressed = ZIPUtil.unZip(compressed);
			System.out.println("UnCompressed length:" + UnCompressed.length);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}