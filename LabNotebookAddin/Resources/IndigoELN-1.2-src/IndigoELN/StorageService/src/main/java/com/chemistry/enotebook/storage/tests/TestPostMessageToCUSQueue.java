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
package com.chemistry.enotebook.storage.tests;

//import com.chemistry.enotebook.design.DesignServiceTestClient;

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.storage.utils.JMSUtils;
import com.chemistry.enotebook.utils.ParallelExpModelUtils;
import junit.framework.TestCase;

public class TestPostMessageToCUSQueue extends TestCase {

	public void testPostMessageToCUSQWithTestData() {
		try {
			NotebookPageModel pageModel = null;
			JMSUtils jmsUtil = new JMSUtils();
			//Should not be using this class here . But for just for quick testing 
			//will cause compile issues on Continious and Nightly build
			//DesignServiceTestClient dsClient = new DesignServiceTestClient();
			//pageModel = dsClient.getNotebooPageFromSerFile();
			ParallelExpModelUtils utils = new ParallelExpModelUtils(pageModel);
			jmsUtil.postMessageToCUSQueue(utils.getCUSInfo(pageModel));
			for(int i = 0 ; i< 5 ; i ++)
			{
				jmsUtil.postMessageToCUSQueue(utils.getCUSInfo(pageModel));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TestPostMessageToCUSQueue.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
