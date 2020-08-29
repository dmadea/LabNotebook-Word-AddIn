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

import com.chemistry.enotebook.reagent.dao.MyReagentsDao;
import com.chemistry.enotebook.reagent.valueobject.UserInfoVO;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MyReagentsDaoTest {

	public static void main(String[] args) throws Exception {
		try {
			MyReagentsDao test = new MyReagentsDao();
	
			java.io.FileInputStream fis = new java.io.FileInputStream(
					"build/com/chemistry/enotebook/reagentmgmtservice/tests/resources/myreagents.xml");
			//		java.io.FileInputStream fis = new java.io.FileInputStream(
			//		"C:\\reagents_doc\\reagents-parameters.xml");
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			String myReagents = new String(buffer);
			
			UserInfoVO myInfo = new UserInfoVO();
			myInfo.setUserName("USER");
			myInfo.setFullName("Name User");
			myInfo.setSiteCode("DOMAIN");
			myInfo.setStatus("VALID");
	
			test.UpdateMyReagentList(myInfo, myReagents);
			String reagentList = test.getMyReagentList("USER");
	
			java.io.File file = new java.io.File(
					"C:/reagents_doc/client_reagentlist.xml");
			if (!file.exists())
				file.createNewFile();
			java.io.FileOutputStream outputStream = new java.io.FileOutputStream(
					file);
			outputStream.write(reagentList.getBytes());
			outputStream.close();
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

}
