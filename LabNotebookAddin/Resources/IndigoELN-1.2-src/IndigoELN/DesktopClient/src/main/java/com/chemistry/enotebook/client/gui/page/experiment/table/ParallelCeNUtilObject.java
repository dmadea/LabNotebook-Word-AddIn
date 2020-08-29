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
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.common.MolString;
import com.chemistry.enotebook.utils.jtable.SortableVO;

/**
 * 
 * 
 */
public class ParallelCeNUtilObject implements SortableVO {
	
	private BatchModel batch;
	private MolString molStr;
	//private HashMap batchProperties;
	private PCeNTableModelConnector tableViewMVConnector;
	private int index;
	
	public ParallelCeNUtilObject(BatchModel batch, MolString molStr,PCeNTableModelConnector tableViewMVConnectore, int indexe) {
		
		this.batch = batch;
		this.molStr = molStr;
		this.tableViewMVConnector = tableViewMVConnectore;
		index = indexe;
		//this.batchProperties = this.batch.getBatchProperties();
		
	}

	public MolString getMolString() {
		return molStr;
	}

	public BatchModel getBatch() {
		return batch;
	}

	// Methods required to implement Sortable VO
	public int getNumColumns() {
		//return batchProperties.keySet().size() + 2;
		return tableViewMVConnector.getHeaderNames().length;
	}

	public Object getValueAt(int indexe) {
	
		if (index < 0)
			throw new IllegalArgumentException("Column Index out of Boundaries");
		if (index == 0) {
			return molStr;
		}
		else if (index == 1 && batch instanceof ProductBatchModel) {
			ProductBatchModel pBatch = (ProductBatchModel) batch;
			return pBatch.getBatchNumber().getBatchNumber();
		}
		else{
			return tableViewMVConnector.getValue(index, indexe);
		}
		/*
		if (index < 0 || index > batchProperties.size() + 2)
			throw new IllegalArgumentException("Column Index out of Boundaries");
		// batchProperties does not contain structure. we need to retrieve it
		if (index == 0) {
			return molStr;
		} // if column index is one return NBK Batch #
		else if (index == 1 && batch instanceof ProductBatchModel) {
			ProductBatchModel pBatch = (ProductBatchModel) batch;
			return pBatch.getBatchNumber().getBatchNumber();
		}
		String[] propertiesNames = new String[batchProperties.keySet().size() + 1];
		int indx = 0;
		propertiesNames[indx] = null;
		indx++;
		for (Iterator itr = batchProperties.keySet().iterator(); itr.hasNext();) {
			propertiesNames[indx] = (String) itr.next();
			indx++;
		}
		return batchProperties.get(propertiesNames[index]);
		
		*/
		
		
	}

	/**
	 * @return the batchProperties
	 */
	//public HashMap getBatchProperties() {
	//	return batchProperties;
	//}

	/**
	 * @param batchProperties
	 *            the batchProperties to set
	 */
	//public void setBatchProperties(HashMap batchProperties) {
	//	this.batchProperties = null;
	//	this.batchProperties = batchProperties;
	//}

	/**
	 * @param batch
	 *            the batch to set
	 */
	public void setBatch(BatchModel batch) {
		if (batch == null)
			throw new IllegalArgumentException("Received a Null reference in setBatch in ParallelCeNUitility object");
		this.batch = null;
		this.batch = batch;
	}
}
