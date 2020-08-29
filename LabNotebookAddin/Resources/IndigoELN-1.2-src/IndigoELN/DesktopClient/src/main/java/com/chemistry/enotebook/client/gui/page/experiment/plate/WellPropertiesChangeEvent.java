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
package com.chemistry.enotebook.client.gui.page.experiment.plate;

import java.util.EventObject;

public class WellPropertiesChangeEvent extends EventObject {

	private static final long serialVersionUID = -6319444125095510429L;
	
	Object subObject; 
	String command;

	public WellPropertiesChangeEvent(Object source, Object subObject, String command) {
		super(source);
		this.subObject = subObject;
		this.command = command;
	}

	/**
	 * @return the subObject
	 */
	public Object getSubObject() {
		return subObject;
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}
}
