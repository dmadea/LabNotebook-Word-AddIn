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

import com.chemistry.enotebook.reagent.dao.StructureDao;
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
public class StructureDaoTest {

	public static void main(String[] args) throws Exception {
	
		java.io.FileInputStream fis = new java.io.FileInputStream(
				"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagents-db.xml");
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);
		fis.close();
	
		StringReader reader = null;
	
		reader = new StringReader(new String(buffer));
	
		SAXBuilder builder = new SAXBuilder();
	
		Document doc = builder.build(reader);
		Element root = doc.getRootElement();
	
		StructureDao test = new StructureDao();
		test.setReagentDBXMLRoot(root);
	
		//get string from a mol file
		java.io.FileInputStream fisb = new java.io.FileInputStream(
				"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/reagents-parameters.xml");
		byte[] bufferb = new byte[fisb.available()];
		fisb.read(bufferb);
		fisb.close();
		String searchParamsXML = new String(bufferb);
		
	}

}
