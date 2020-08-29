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
 * Created on Jul 22, 2004
 *
 * Business delegate can decouple business components from the code that uses them. 
 * The Business Delegate pattern manages the complexity of distributed component 
 * lookup and exception handling, and may adapt the business component interface 
 * to a simpler interface for use by views. 
 * */

package com.chemistry.enotebook.registration.uniqueness;

import com.global.dri.compoundregistration.services.structure.StructureServices;
import com.global.dri.compoundregistration.services.structure.StructureUcDTO;


/**
 * 
 */
public class UniquenessCheckDelegate 
{
	/**
	 * Remote reference for session EJB
	 */
	private StructureServices serviceObj;


	private static String DEFAULT_URL = null;
		
    private static final String STRUCTURE_SERVICES_EJB = "com.global.dri.compoundregistration.services.structure.StructureServices";

    
	/**
	 * Constructor for session reference. The initialcontext will be instantiated for the
	 * very first time for all application delegates. The following instantiation of the 
	 * delegates will reuse the InitialContext.
	 * Delegate looks up home from ServiceLocator to locate an EJB Home Interface
	 * given the URL, JNDI name and Home Object class name.
	 * <p>
	 * 
	 * @param url
	 *            The URL to look at.
	 * @param jndi
	 *            The JNDI name of the service.
	 * @exception DelegateException
	 *                translates the system exception (remote, runtime) to
	 *                application exceptions.
	 */
	
	
	public UniquenessCheckDelegate() 
		throws UcInitException
	{
		if (DEFAULT_URL == null) 
		{
			try	{
				DEFAULT_URL = "localhost";//PropertyReader.getJndiServiceUrl("CompoundRegistrationUcService");
			} catch (Exception e) {
				throw new UcInitException("UniquenessCheckDelegate init failed", e);
			}
		}
		
		init();
	}
	
	/**
	 * Constructor for JUnit testing only
	 */
	public UniquenessCheckDelegate(String Url) 
		throws UcInitException
	{
		DEFAULT_URL = Url;
		init();
	}

	private void init()
		throws UcInitException
	{
		try {
//			StructureServicesHome home = (StructureServicesHome) ServiceLocator.locateEJBHome(
//							STRUCTURE_SERVICES_EJB, DEFAULT_URL, homeClass);
//			serviceObj = home.create();
			throw new Exception("NOT IMPLEMENTED!");
		} catch (Exception ex) {
			throw new UcInitException(ex.toString(), ex);
		}
	}
	
	public StructureUcDTO checkUniqueness(String molfile, String stereoisomerCode)
		throws UcAccessException
	{
		try {
			return serviceObj.executeUc(molfile, stereoisomerCode);
		} catch (Exception e) {
			throw new UcAccessException(e.getMessage(), e);
		}
	}
}