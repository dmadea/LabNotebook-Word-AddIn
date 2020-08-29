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
 * AllTests.java
 * 
 * Created on May 25, 2004
 *
 * 
 */
package com.chemistry.enotebook.storage.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @date May 25, 2004
 */
public class AllTests {
	public static void main(String[] args) {
		// TODO: figure out why this isn't right.
		junit.swingui.TestRunner.run(TestSpeedBarContext.class);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.chemistry.enotebook.storage.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(TestNotebookContext.class);
		suite.addTestSuite(TestSpeedBarContext.class);
		suite.addTestSuite(TestNotebookValidate.class);
		// $JUnit-END$
		return suite;
	}
}
