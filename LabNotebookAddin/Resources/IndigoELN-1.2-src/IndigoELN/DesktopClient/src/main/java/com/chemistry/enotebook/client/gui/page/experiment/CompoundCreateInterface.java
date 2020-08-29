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

import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationEvent;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.ProductBatchModel;

public interface CompoundCreateInterface {
	
	public void createCompound();

	public void duplicateCompound(ProductBatchModel batch);
	
	public void deleteCompound();
	
	public void deleteCompound(ProductBatchModel batch);
	
	public void syncIntendedProducts();
	
	public void updateSyncWithIntendedProductsActionState();
	
	public void fireCompoundRemoved(CompoundCreationEvent event);

	public void batchSelectionChanged(BatchModel batchModel);
	
	public void setVnvButtonEnabled(boolean b);
}
