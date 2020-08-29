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
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.enotebook.client.gui.page.experiment.plate.ProductPlateContainer;
import com.chemistry.enotebook.domain.ProductPlate;

import javax.swing.*;

public class ProductPlateTableViewPanel extends JPanel implements
		ProductPlateContainer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 699721725712064336L;
	private ProductPlate productPlate = null;
	
	public ProductPlateTableViewPanel(ProductPlate productPlate) {
		this.productPlate = productPlate;	
	}

	public ProductPlate getProductPlate() {
		return productPlate;
	}


	public void setProductPlate(ProductPlate productPlate) {
		this.productPlate = productPlate;
	}

}
