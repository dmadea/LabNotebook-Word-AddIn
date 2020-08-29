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
 * Created on Sep 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagentmgmtservice.tests;


/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReagentDBXMLDaoTest {

	public static void main(String[] args) throws Exception {
		
		String columnName = "COMPOUND_MANAGEMENT_BATCH.COMPOUND_NUMBER";
		int dotPosition = columnName.indexOf(".");
		
		System.out.println("The sub string is: " + columnName.substring(dotPosition + 1,columnName.length()));
//		try {
//			ReagentDBXMLDao test = new ReagentDBXMLDao();
//	
//			java.io.FileInputStream fis = new java.io.FileInputStream(
//					"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagents-db.xml");
//	
//			byte[] buffer = new byte[fis.available()];
//			fis.read(buffer);
//			fis.close();
//			String myReagents = new String(buffer);
//	
//			test.updateReagentDBXML("GBL", myReagents);
//			String reagentList = test.getReagentDBXML("GBL");
//	
//			java.io.File file = new java.io.File(
//					"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagentDBXML.xml");
//			if (!file.exists())
//				file.createNewFile();
//			java.io.FileOutputStream outputStream = new java.io.FileOutputStream(
//					file);
//			outputStream.write(reagentList.getBytes());
//			outputStream.close();
//	
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	
	}

}
