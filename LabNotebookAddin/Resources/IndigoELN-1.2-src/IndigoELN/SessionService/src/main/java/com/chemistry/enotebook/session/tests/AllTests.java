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
 
package com.chemistry.enotebook.session.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

 
public class AllTests {
	public static void main(String[] args) {
		// TODO: figure out why this isn't right.
		junit.swingui.TestRunner.run(TestSessionManagerServiceInterface.class);
	}
	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for com.chemistry.enotebook.session.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestSessionManagerServiceInterface.class);
		suite.addTestSuite(TestHealthNProps.class);
		//$JUnit-END$
		return suite;
	}
}
