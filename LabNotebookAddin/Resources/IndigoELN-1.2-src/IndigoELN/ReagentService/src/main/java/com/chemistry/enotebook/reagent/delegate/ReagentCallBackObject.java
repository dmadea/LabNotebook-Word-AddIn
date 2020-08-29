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
 * Created on Oct 6, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.delegate;

import javax.rmi.PortableRemoteObject;


/**
 * 
 *  
 */

public class ReagentCallBackObject extends PortableRemoteObject 
										implements ReagentCallBackInterface
{
	private static final long serialVersionUID = 6310122761563192725L;
	
	private boolean isReagentSearchCancelled = false;

	/**
	 * Default constructor for constructing the object of the Remote Object
	 * This object will be used for callback purpose
	 * 
	 * @exception RemoteException
	 *                translates the system exception (remote, runtime) to
	 *                application exceptions.
	 */

	public ReagentCallBackObject() 
		throws Exception
	{
	    super();
	}

	/**
	 * This method cancels the reagents search
	 * 
	 * @return boolean
	 * 
	 */
	public void setCancelReagentsSearch(boolean status)
	{
	    isReagentSearchCancelled = status;
	}

	/**
	 * This method is overriding from ReagentCallBackInterface.
	 * 
	 * @return boolean containing the status of the cancelSearch Operation
	 * @throws RemoteException
	 */
	public boolean isSearchCancelled() throws Exception
	{
	    return isReagentSearchCancelled;
	}
	
	
		
	
}