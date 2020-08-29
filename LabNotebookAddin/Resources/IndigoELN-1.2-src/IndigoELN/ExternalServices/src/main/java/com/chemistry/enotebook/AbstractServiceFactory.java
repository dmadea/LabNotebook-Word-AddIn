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
package com.chemistry.enotebook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory for load implementations of external services
 */
public abstract class AbstractServiceFactory {

	private static final Log log = LogFactory
			.getLog(AbstractServiceFactory.class);

	protected AbstractServiceFactory() {
	}

	/**
	 * Create service implementation using class name from
	 * service_implementation.properties
	 * 
	 * @param serviceClassName
	 *            implementation class name
	 * @return Implementation class instance
	 * @throws LoadServiceException
	 */
	public static Object createServiceImpl(String serviceClassName)
			throws LoadServiceException {
		Object newInstance = null;

		try {
			String serviceImplClassName = PropertyReader
					.getServiceImpl(serviceClassName);

			Class<?> implClazz = Thread.currentThread().getContextClassLoader()
					.loadClass(serviceImplClassName);
			newInstance = implClazz.newInstance();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			throw new LoadServiceException(
					"Unable to load implementation of the service: "
							+ serviceClassName, ex);
		}

		return newInstance;
	}
}
