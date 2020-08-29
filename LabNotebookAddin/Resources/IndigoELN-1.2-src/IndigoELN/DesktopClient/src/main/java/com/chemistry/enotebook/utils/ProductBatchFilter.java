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
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.PlateWell;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.ProductPlate;

import java.util.*;

public class ProductBatchFilter {
	
	private NotebookPageModel pageModel;

	public ProductBatchFilter(NotebookPageModel pageModel) {
		this.pageModel = pageModel;
	}
	
	public List<ProductBatchModel> getPlatedBatches() {
		Set<ProductBatchModel> tempSet = new HashSet<ProductBatchModel>();
		List<ProductBatchModel> list = new ArrayList<ProductBatchModel>();
		List<ProductPlate> plateList = pageModel.getAllProductPlatesAndRegPlates();
		for (Iterator<ProductPlate> it = plateList.iterator(); it.hasNext();) {
			ProductPlate plate = it.next();//Same batch may be in more than one plate, like Reg plate and Product Plate.
			PlateWell<ProductBatchModel>[] wells = plate.getWells();
			for (int i=0; i<wells.length; i++) {
				PlateWell<ProductBatchModel> well = wells[i];
				ProductBatchModel batch  = well.getBatch();
				if (batch != null) {
					tempSet.add(batch);
				}
			}
		}
		list.addAll(tempSet);//To avoid duplicate batches.
		return list;
	}
	
	public List<ProductBatchModel> getAllProductBatches() {
		List<ProductBatchModel> batchModels = pageModel.getAllProductBatchModelsInThisPage();
		return batchModels;
	}
	
	public List<ProductBatchModel> getNonPlatedBatches() {
		List<ProductBatchModel> allProductBatches = this.getAllProductBatches();
		List<ProductBatchModel> platedBatches = this.getPlatedBatches();
		List<ProductBatchModel> nonPlatedBatches = new ArrayList<ProductBatchModel>();
		for (Iterator<ProductBatchModel> it = allProductBatches.iterator(); it.hasNext();) {
			ProductBatchModel batch = it.next();
			if (! platedBatches.contains(batch) && ! nonPlatedBatches.contains(batch)) // batches may be duplicated!!! 
				nonPlatedBatches.add(batch);
		}
		return nonPlatedBatches;
	}
}
