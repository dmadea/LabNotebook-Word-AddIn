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
 * Created on May 24, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.storage.ejb;

import com.chemistry.enotebook.storage.StorageContextInterface;
import com.chemistry.enotebook.storage.exceptions.StorageHandlerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class ContextHandlerFactory {
	// Can't instantiate and Object of this type.
	private static final long serialVersionUID = 1614364966218642822L;

	private static final Log log = LogFactory.getLog(ContextHandlerFactory.class);
	private ContextHandlerFactory() {
	}

	public static ContextHandlerInterface createHandler(StorageContextInterface context) throws StorageHandlerException {
		ContextHandlerInterface result = null;

		String hndlrName = context.getClass().getName();
		int lastDot = hndlrName.lastIndexOf('.');
		if (lastDot == -1) {
			hndlrName = "ejb." + hndlrName + "Handler";
		} else {
			hndlrName = hndlrName.substring(0, lastDot) + ".ejb" + hndlrName.substring(lastDot) + "Handler";
		}

		try {
			Class hndlrClass = Class.forName(hndlrName);
			log.debug("Created instance of handler name:"+hndlrName);
			result = (ContextHandlerInterface) hndlrClass.newInstance();

			result.setContext(context);

			// Class.newInstance can be used only if there is a no-arg constructor ;
			// otherwise, use Class.getConstructor and Constructor.newInstance.
			// Class[] types = { javax.servlet.ServletConfig.class };
			// java.lang.reflect.Constructor constructor = storageClass.getConstructor(types);
			// Object[] params = { aConfig };
			// result = (DAOFactory) constructor.newInstance( params );
		} catch (Exception e) {
			throw new StorageHandlerException(
					"ContextHandlerFactory:createHandler:: Couldn't create Storage Handler: " + hndlrName, e);
		}

		return result;
	}
}
