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
 * Created on Jul 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.session.tests;

import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.session.security.CompoundManagementEmployee;


/** 
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestGetCompoundManagementEmployeeID 
{
	public static void main(String[] args) {
		try {
			SessionTokenDelegate smObj = new SessionTokenDelegate();
			System.err.println("Successfully established contact...");
			
			String ntName = "asdf";
			CompoundManagementEmployee emp = smObj.getCompoundManagementEmployeeID(ntName);
			System.out.println("Employee ID for '" + ntName + "' is: " + emp.getEmployeeId());
			System.out.println("Full Name for '" + ntName + "' is: " + emp.getFullName());
			System.out.println("Site Code for '" + ntName + "' is: " + emp.getSiteCode());
			System.out.println("Status for '" + ntName + "' is: " + emp.getStatus());
        } catch (Exception e) {
          	e.printStackTrace();
        } 
	}
}
