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
package com.chemistry.enotebook.storage.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;

public class StorageUtils {

	private static final Log log = LogFactory.getLog(StorageUtils.class);
	private static String dbOwner = null;
	
//	private static byte[] getMoleFileFormat(String structure) throws Exception{
//		String molfile = null;
//		//System.out.println("MolFile before decoding: "+structure);
//		String decodedString = Decoder.decodeString(structure);
//		int index = decodedString.indexOf("M  END");
//		if (index >= 0) {
//			//index > 0 indicates decoded string is in MDL Molefile format
//			molfile = decodedString;
//		} else {
//			//Else its a Chime String, using the blow code
//			molfile = mdli.mt.csinline.a.d(decodedString);
//		}
//		
//
////        molfile = molfile.substring(molfile.indexOf("\n"));
////        molfile = molfile.substring(0, molfile.indexOf(">  <"));
////        molfile +="$$$$\n";
//		log.debug("MolFile After convertion: "+molfile);
//		return molfile.getBytes();
//	}
	
	public static byte[] toBytes(Blob blobData) {
		byte[] bytes = new byte[0];
		try {
			if (blobData != null)
				bytes = blobData.getBytes(((long) 1), (int) blobData.length());
		} catch (SQLException error) {
			error.printStackTrace();
		}
		return bytes;
	}
		
	public String getMulitpleStringValuesAsOracleIn(String strArray[])
	{
		String inStr = "";
		if (!(strArray != null && strArray.length > 0)) return "";
		int size = strArray.length;
		for(int i = 0;i <size ; i ++)
		{
			if(i == (size -1)) {inStr = inStr + ",'" + strArray[i] + "'";}
			else
			inStr = inStr + ",'" + strArray[i] + "',";
		}
		return inStr;
	}
	
	
	public static String getOwnerSchemaName(Connection dbCon){
		String dbUser = null;
		
		if(dbOwner == null){
			try{
				dbUser = dbCon.getMetaData().getUserName();
				dbOwner = dbUser.replaceFirst("USER", "OWNER");
				log.debug("Owner Scheme Name returned   :" +dbOwner);
			}catch (Exception error){
				error.printStackTrace();
			}
		} 
		return dbOwner;
	}
	
	public static String getMessageSubstring(String input, int size)
	{
		if(input == null || input.equals(""))
		{
			return input;
		}else if(input.length() > size)
		{
			return input.substring(0,size);
		}else
		{
			return input;
		}
	}
	
}
