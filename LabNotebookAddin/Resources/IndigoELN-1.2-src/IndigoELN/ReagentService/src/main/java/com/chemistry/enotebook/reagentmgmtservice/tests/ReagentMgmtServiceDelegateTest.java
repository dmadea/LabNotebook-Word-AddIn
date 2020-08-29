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


import com.chemistry.enotebook.reagent.delegate.ReagentCallBackObject;
import com.chemistry.enotebook.reagent.delegate.ReagentMgmtServiceDelegate;
import com.chemistry.enotebook.reagent.util.ZIPUtil;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReagentMgmtServiceDelegateTest //extends TestCase 
{
	
	/*
	 * Class under test for void ReagentMgmtServiceDelegate(String)
	 */
	public final void testReagentMgmtServiceDelegateString() {
		//TODO Implement ReagentMgmtServiceDelegate().
	}

	/*
	 * Class under test for void ReagentMgmtServiceDelegate()
	 */
	public final void testReagentMgmtServiceDelegate() {
		//TODO Implement ReagentMgmtServiceDelegate().
	}

	public final void testGetMyReagentList() {
		//TODO Implement getMyReagentList().
	}

	public final void testUpdateMyReagentList() {
		//TODO Implement UpdateMyReagentList().
	}

	public final void testGetDBInfo() throws Exception {
		ReagentMgmtServiceDelegate test;
		
		test = new ReagentMgmtServiceDelegate();		
		//		update reagents-db.xml
		java.io.FileInputStream fis = new java.io.FileInputStream(
				"src/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagents-db.xml");

		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);
		fis.close();
		String myReagents = new String(buffer);
		try {
			test.updateReagentDBXML("GBL", myReagents);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String reagentList = "";
		try {
			reagentList = test.getDBInfo("GBL");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		java.io.File file = new java.io.File(
				"src/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagentDBXML.xml");
		if (!file.exists())
			file.createNewFile();
		java.io.FileOutputStream outputStream = new java.io.FileOutputStream(
				file);
		outputStream.write(reagentList.getBytes());
		outputStream.close();
		System.out.println("The updated one is: " + reagentList);
	}

	public final void testDoReagentsSearch() {
		//TODO Implement doReagentsSearch().
	}

	public static void main(String[] args) throws Exception {
		
		/**
		 * Important
		 * need to get a valid session token to perform the following all tests.
		 */
//		SessionIdentifier sessionID = null;
		
		ReagentMgmtServiceDelegate test = new ReagentMgmtServiceDelegate();
		
//		update reagents-db.xml
//		java.io.FileInputStream fis = new java.io.FileInputStream(
//				"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagents-db.xml");
//
//		byte[] buffer = new byte[fis.available()];
//		fis.read(buffer);
//		fis.close();
//		String myReagents = new String(buffer);
//
//		test.updateReagentDBXML("GBL", myReagents);
		/*String reagentList = test.getDBInfo("GBL");

		java.io.File file = new java.io.File(
				"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagentDBXML.xml");
		if (!file.exists())
			file.createNewFile();
		java.io.FileOutputStream outputStream = new java.io.FileOutputStream(
				file);
		outputStream.write(reagentList.getBytes());
		outputStream.close();
		System.out.println("The updated one is: " + reagentList); */
		
//		String configXML = test.getDBInfo("GBL");
//		System.out.println("The configXML is: " + configXML );
//		
//		configXML = test.getDBInfo("GBL");
//		System.out.println("The configXML is: " + configXML );
//	
//		java.io.FileInputStream fisb = new java.io.FileInputStream(
//				"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagents-parameters.xml");
//		byte[] bufferb = new byte[fisb.available()];
//		fisb.read(bufferb);
//		fisb.close();
//		String searchParamsXML = new String(bufferb);
//	
//		ArrayList testList = new ArrayList();
//	
//		
//	
//		try {
//			
//			byte[] reagentList3 = test.doReagentsSearch(searchParamsXML);
//			testList.add(reagentList3);
//			
//	
//			for (int i = 0; i < testList.size(); i++) {
//				java.io.File file = new java.io.File(
//						"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagent_test" + i + ".xml");
//				if (!file.exists())
//					file.createNewFile();
//				java.io.FileOutputStream outputStream = new java.io.FileOutputStream(
//						file);
//				outputStream.write(ZIPUtil.unZip((byte[]) testList.get(i)));
//				outputStream.close();
//			}
//	
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		ArrayList testListb = new ArrayList();
//		java.io.FileInputStream fisbb = new java.io.FileInputStream(
//		"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagents-parameters_a.xml");
//		byte[] bufferbb = new byte[fisbb.available()];
//		fisbb.read(bufferbb);
//		fisbb.close();
//		String searchParamsXMLb = new String(bufferbb);
//	
//		try {
//			byte[] reagentList3 = test.doReagentsSearch(searchParamsXMLb);
//			testListb.add(reagentList3);
//			
//	
//			for (int i = 0; i < testListb.size(); i++) {
//				java.io.File file = new java.io.File(
//						"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagent_test_a" + i + ".xml");
//				if (!file.exists())
//					file.createNewFile();
//				java.io.FileOutputStream outputStream = new java.io.FileOutputStream(
//						file);
//				outputStream.write(ZIPUtil.unZip((byte[]) testListb.get(i)));
//				outputStream.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	
		String searchParamsXML = "<ReagentsLookupParams><TextDatabases DoTextSearch=\"True\"><Database Name=\"CPI\"><SearchFields><Field ColumnName=\"CHEMICAL.CH_CASRN\" Criteria=\"Equals\" Value=\"603-32-7\"/></SearchFields></Database></TextDatabases><StructureDatabases DoStructureSearch=\"False\"/><Iterating LastPosition=\"-1\" ChunkSize=\"50\"/></ReagentsLookupParams>";
		byte[] result = null;
		ReagentCallBackObject callBackObj = new ReagentCallBackObject(); 
		callBackObj.setCancelReagentsSearch(false);
		
		try {
			byte[] originalBytes = test.doReagentsSearch(searchParamsXML, callBackObj);
			if (originalBytes != null) {
				byte[] bytes = ZIPUtil.unZip(originalBytes);
				String reagentsList = new String(bytes);
				System.out.println("Search Result:"+reagentsList);
				
			}
		} catch (Exception e1) {
			e1.printStackTrace();	
		}
	}
}
