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

import com.chemistry.enotebook.session.SystemProperties;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.session.security.SessionToken;
import junit.framework.TestCase;


/** 
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestHealthNProps 
	extends TestCase
{
    static SessionTokenDelegate sessionObj = null;

    static String userID = "xxxxxx";		// Put username / password here TEMPORARILY FOR TEST
    static String password = "xxxxxxx";
    
    static SessionIdentifier sessionID = null;

    static
	{
    	boolean success = true;
    	
        // Setup initial context for test
        try {
        	sessionObj = new SessionTokenDelegate();
            SessionToken st = sessionObj.login(userID, password);
            sessionID = st.getSessionIdentifier();
        } catch (Exception e) {
        	System.err.println("Exception during EJB startup");
        	e.printStackTrace();
        	success = false;
        }

		if (success) 
			System.out.println("Successfully established contact...");
		else
			System.err.println("Failed to initialize connection.\n" +
							   "Is your webserver running?\n  Configured?");
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(TestHealthNProps.class);
    }


    /*
     * @see TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
      	assertTrue("Session Object missing!", sessionObj != null);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown()
        throws Exception
    {
        super.tearDown();
    }


    public void testValidateNotebook()
    {
		try {
//			SystemHealth healthIn = new SystemHealth();
//			assertTrue(sessionObj.checkSystemHealth(healthIn));
			
			SystemProperties props = sessionObj.getSystemProperties("SITE1");
	    	assertTrue(props != null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
