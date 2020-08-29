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
package com.chemistry.enotebook.client.gui.page.batch.events;

import java.util.EventObject;
import java.util.List;

public class RegisteredPlateCreatedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8809041921924139317L;
	List plates;

	public RegisteredPlateCreatedEvent(Object source, List plate) {
		super(source);
		plates = plate;

	}

	/**
	 * @return the plate
	 */
	public List getPlates() {
		return plates;
	}

	/**
	 * @param plate
	 *            the plate to set
	 */
	public void setPlates(List plate) {
		this.plates = plate;
	}

}
