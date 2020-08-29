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
package com.chemistry.enotebook.client.utils;

import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchNumberException;
import com.chemistry.enotebook.experiment.datamodel.batch.ProductBatch;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class MockDataModelUtils {
	
	
	public static ProductBatch getProductBatch()
	{
		ProductBatchModel model = new ProductBatchModel();
		ParentCompoundModel c = new ParentCompoundModel();
		c.setStringSketchFormat("SKETCH HERE");
		model.setCompound(c);
		c.setMolWgt(322.113);
		c.setMolFormula("H2O");
		HashMap props = new LinkedHashMap();
		props.put("REACTANT_A", "123");
		props.put("REACTANT_B", "456");
		props.put("RegisteredId", "5");
		props.put("Chem. Name", "Chemical");
		props.put("Mol. Formula", "H2O");
		props.put("Mol. Weight", "322.113");
		props.put("Salt Code", "00");
		model.setBatchProperties(props);
		try {
			model.setBatchNumber(new BatchNumber("11111111-0011-0001"));
			
			// need to add VCR id to the product batch

		} catch (InvalidBatchNumberException e) {
			// no need to really print any or rethrow
			// e.printStackTrace();
		}
		ProductBatch pb = new ProductBatch(model);
		
		return pb;
	}

}
