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
/**
 * 
 */
package com.chemistry.enotebook.client.gui.page.batch.events;

import com.chemistry.enotebook.domain.PlateWell;

import java.util.EventObject;

/**
 * 
 * 
 */
public class ProductBatchDetailsContainerEvent extends EventObject {
	
	private static final long serialVersionUID = 6568026998184108857L;
	
	private PlateWell<?> well;

	public ProductBatchDetailsContainerEvent(Object source, PlateWell<?> p) {
		super(source);
		well = p;
	}

	public PlateWell<?> getWell() {
		return well;
	}

	public void setWell(PlateWell<?> well) {
		this.well = well;
	}
}

