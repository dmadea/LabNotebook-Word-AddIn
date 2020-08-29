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

package com.chemistry.enotebook.sdk.delegate;

import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemistryProperties;
import com.chemistry.enotebook.sdk.ReactionProperties;
import com.chemistry.enotebook.sdk.interfaces.ChemistryService;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 */
public class ChemistryDelegate implements ChemistryService
{
	/**
	 * reference for Remote EJB
	 */
	private ChemistryService service;

	public static final Log log = LogFactory.getLog(ChemistryDelegate.class);
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
	public ChemistryDelegate() 
	{
		service = ServiceLocator.getInstance().locateService("ChemistryService", ChemistryService.class);
	}


	/**
	 * retrieveData method is used to retrieve compound number by
	 * given either batch number (NOTEBOOK field) or conversational batch number
	 * (BATCH_NUMBER field)
	 * <p>
	 * @param batchNo
	 *            The batch number as String type.
	 * @return compound number ompound number as String type.
	 * @throws Exception 
	 * @exception DelegateException
	 *                translates the system exception (remote, runtime) to
	 *                application exceptions.
	 */
	@Override
	public boolean isChemistryEmpty(byte[] chemistry) throws ChemUtilAccessException
	{
		return service.isChemistryEmpty(chemistry);
	}
	
	@Override
	public boolean isChemistryEqual(byte[] chemistry1, byte[] chemistry2) throws ChemUtilAccessException
	{
		return service.isChemistryEqual(chemistry1, chemistry2);
	}
	
	@Override
	public byte[] convertChemistry(byte[] inBuff, String inBuffType, String outBuffType)
		throws ChemUtilAccessException
	{
			return service.convertChemistry(inBuff, inBuffType, outBuffType);
	}

	@Override
	public boolean isReaction(byte[] sketch)
		throws ChemUtilAccessException
	{
		return service.isReaction(sketch);
	}

	@Override
	public ReactionProperties extractReactionComponents(ReactionProperties rxnProp)
		throws ChemUtilAccessException
	{
		return service.extractReactionComponents(rxnProp);
	}
	
	@Override
	public ReactionProperties combineReactionComponents(ReactionProperties rxnProp)
		throws ChemUtilAccessException
	{
		return service.combineReactionComponents(rxnProp);
	}

	@Override
	public boolean areMoleculesEqual(byte[] chemistry1, byte[] chemistry2)
		throws ChemUtilAccessException
	{
		return service.areMoleculesEqual(chemistry1, chemistry2);
	}
	
	@Override
	public ChemistryProperties getMolecularInformation(byte[] chemistry)
		throws ChemUtilAccessException
	{
		return service.getMolecularInformation(chemistry);
	}
	
	@Override
	public boolean isMoleculeChiral(byte[] chemistry)
		throws ChemUtilAccessException
	{
		return service.isMoleculeChiral(chemistry);
	}
	
	@Override
	public byte[] setChiralFlag(byte[] chemistry, boolean flag)
		throws ChemUtilAccessException
	{
		return service.setChiralFlag(chemistry, flag);
	}

	@Override
	public byte[] convertStructureToPicture(byte[] chemistry, int format, 
										    int height, int width, double loss, double pixelToMM)
		throws ChemUtilAccessException
	{
		return service.convertStructureToPicture(chemistry, format, height, width, loss, pixelToMM);
	}

	@Override
	public byte[] convertReactionToPicture(byte[] chemistry, int format, 
										   int height, int width, double loss, double pixelToMM)
		throws ChemUtilAccessException
	{
		return service.convertReactionToPicture(chemistry, format, height, width, loss, pixelToMM);
	}
	
	@Override
	public void setDebugFlag(boolean flag) {
		service.setDebugFlag(flag);	
	}
	
	@Override
	public boolean getDebugFlag() {
		return service.getDebugFlag();
	}
}
