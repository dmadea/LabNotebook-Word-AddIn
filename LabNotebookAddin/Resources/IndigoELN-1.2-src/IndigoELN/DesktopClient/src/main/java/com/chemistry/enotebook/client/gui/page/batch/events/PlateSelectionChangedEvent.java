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

import com.chemistry.enotebook.domain.ProductPlate;

import java.util.EventObject;
import java.util.List;

public class PlateSelectionChangedEvent extends EventObject {
	
	private static final long serialVersionUID = -5650731282552013262L;
	
	private ProductPlate plate;
	private List<ProductPlate> allSelectedPlates;

	public PlateSelectionChangedEvent(Object source, ProductPlate platee, List<ProductPlate> platese) {
		super(source);
		plate = platee;
		allSelectedPlates = platese;
	}

	public ProductPlate getProductPlate() {
		return plate;
	}

	public List<ProductPlate> getAllSelectedPlates() {
		return allSelectedPlates;
	}
}

