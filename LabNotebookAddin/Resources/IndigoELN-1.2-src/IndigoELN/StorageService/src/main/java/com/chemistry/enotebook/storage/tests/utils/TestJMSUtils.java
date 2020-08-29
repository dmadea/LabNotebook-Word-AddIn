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
package com.chemistry.enotebook.storage.tests.utils;

import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.publisher.classes.PublishEntityInfo;
import com.chemistry.enotebook.storage.utils.JMSUtils;

import java.util.ArrayList;

public class TestJMSUtils {
	
	public static void main(String args[])
	{
		PublishEntityInfo cusObj = new PublishEntityInfo("dsjfklsdfjsdklfjsdl"+CeNConstants.PUBLISHER_KEY_SPERATOR+"sdfsfsdfsdf",
				  null,CeNConstants.PUBLISHER_STRUCTURE_TYPE_REACTION,"mol file here","sdfsdfsdfsd");
		
		try {
			JMSUtils util = new JMSUtils();
			ArrayList cusInfoList = new ArrayList();
			cusInfoList.add(cusObj);
			util.postMessageToCUSQueue(cusInfoList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
