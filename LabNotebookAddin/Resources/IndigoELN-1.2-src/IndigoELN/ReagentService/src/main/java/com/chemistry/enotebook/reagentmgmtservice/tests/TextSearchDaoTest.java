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

import com.chemistry.enotebook.reagent.dao.TextSearchDao;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.StringReader;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TextSearchDaoTest {

	public static void main(String[] args) throws Exception {
	
		java.io.FileInputStream fis = new java.io.FileInputStream(
				"C:\\reagents_doc\\reagents-db.xml");
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);
		fis.close();
	
		StringReader reader = null;
	
		reader = new StringReader(new String(buffer));
	
		SAXBuilder builder = new SAXBuilder();
	
		Document doc = builder.build(reader);
		Element root = doc.getRootElement();
	
		TextSearchDao test = new TextSearchDao();
		test.setReagentDBXMLRoot(root);
	
		java.io.FileInputStream fisb = new java.io.FileInputStream(
				"C:\\reagents_doc\\reagents-parameters.xml");
		byte[] bufferb = new byte[fisb.available()];
		fisb.read(bufferb);
		fisb.close();
		String searchParamsXML = new String(bufferb);
	
		try {
			String reagentList = test.doReagentsSearchByText(searchParamsXML);
	
			System.out.println("The reagent list is: " + reagentList);
			java.io.File file = new java.io.File(
					"C:\\reagents_doc\\test\\RS-000007_tx.xml");
			if (!file.exists())
				file.createNewFile();
			java.io.FileOutputStream outputStream = new java.io.FileOutputStream(
					file);
			outputStream.write(reagentList.getBytes());
			outputStream.close();
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		java.io.FileInputStream fisbb = new java.io.FileInputStream(
				"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagents-parameters_a.xml");
		byte[] bufferbb = new byte[fisbb.available()];
		fisbb.read(bufferbb);
		fisbb.close();
		String searchParamsXMLb = new String(bufferbb);
	
		try {
			String reagentListb = test.doReagentsSearchByText(searchParamsXMLb);
	
			System.out.println("The second reagent list is: " + reagentListb);
			java.io.File fileb = new java.io.File(
					"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/RS-000007_tx_second.xml");
			if (!fileb.exists())
				fileb.createNewFile();
			java.io.FileOutputStream outputStreamb = new java.io.FileOutputStream(
					fileb);
			outputStreamb.write(reagentListb.getBytes());
			outputStreamb.close();
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

}
