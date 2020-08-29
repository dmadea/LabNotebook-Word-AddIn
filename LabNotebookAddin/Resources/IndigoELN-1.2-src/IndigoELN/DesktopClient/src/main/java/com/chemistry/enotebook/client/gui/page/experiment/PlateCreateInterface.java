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
package com.chemistry.enotebook.client.gui.page.experiment;

import com.chemistry.enotebook.domain.container.Container;

import java.util.Vector;

public interface PlateCreateInterface {
	public void searchContainerTypes();

	public void loadOrdersFromFile();// load from file, file contains

	// COntainer infor
	public void loadOrderNoContainerFromFile(Container mContainer);

	public void defineCustomerContainter();

//	public void loadCompoundManagementOrderOptions();

	public void createMonomerPlateFromSynthesisPlan(Container mContainer);

	public void createProductPlateFromSynthesisPlan(Container mContainer);
	
	public void createProductPlateFromMonomerPlate(Container mContainer, String plateNumber);
	
	public void setSelectedContainer(Container mContainer);  // vb 5/23
	
	public void createMonomerPlateFromOtherOrder(Container mContainer);

	public void setSelectedContainerList(Vector siblingList);
	
	public void deleteMonomerPlate();
	
	public void deleteProductPlate();
}
