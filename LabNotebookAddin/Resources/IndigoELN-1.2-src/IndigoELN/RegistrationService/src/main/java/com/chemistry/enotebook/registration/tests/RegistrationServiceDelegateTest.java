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
 * Created on Oct 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.registration.tests;

import com.chemistry.enotebook.compoundregistration.classes.CompoundRegistrationJobStatus;
import com.chemistry.enotebook.registration.delegate.RegistrationServiceDelegate;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class RegistrationServiceDelegateTest extends TestCase {

	/**
	 * Important need to get a valid session token to perform the following all
	 * tests.
	 */

	private SessionIdentifier sessionID = null;

	public final void atestExecuteVnV() {
		try {
			RegistrationServiceDelegate reg = new RegistrationServiceDelegate();
			java.io.FileInputStream fis = new java.io.FileInputStream(
					"src/com/chemistry/enotebook/registration/tests/resources/1564472.sdf");

			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			String batchSDF = new String(buffer);
			String vnvResult = reg.executeVnV(batchSDF, "HSREG");
			System.out.println("The VnV result is: " + vnvResult);
			// assertEquals(reg.executeVnV();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final void sTestJobStatus() {
		ArrayList jobList = new ArrayList();
		jobList.add("111111");
		String j[] = (String[]) jobList.toArray(new String[] {});
		try {
			RegistrationServiceDelegate reg = new RegistrationServiceDelegate();
			CompoundRegistrationJobStatus jobs[] = reg.getRegisterJobStatus(j);
			if (jobs != null) {
				System.out.println("Jobs status size" + jobs.length);
			} else {
				System.out.println("Jobs status is null");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final void testHealthCheck() {
		try {
			RegistrationServiceDelegate reg = new RegistrationServiceDelegate();
			System.out.println("CompoundRegistration SOAP Health: "
					+ reg.checkCompoundRegistrationHealth());
			System.out.println("CompoundRegistration UC Health: "
					+ reg.checkUniquenessHealth());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		RegistrationServiceDelegateTest registrationServiceDelegateTest = new RegistrationServiceDelegateTest();
		registrationServiceDelegateTest.testHealthCheck();
	}
}
