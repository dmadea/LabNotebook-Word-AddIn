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
 * Created on 17-Dec-2004
 */
package com.chemistry.enotebook.client.gui.health;

/**
 * 
 */
public class SysItemType {
	/**
	 * Class to hold the enumerated types for system SysItemImpact
	 */
	// some enumerated types for item impact
	// Database connection
	public static final SysItemType DB_CONN = new SysItemType();
	// Service component
	public static final SysItemType SERVICE_COMP = new SysItemType();

	protected SysItemType() {
		// super();
	}
}
