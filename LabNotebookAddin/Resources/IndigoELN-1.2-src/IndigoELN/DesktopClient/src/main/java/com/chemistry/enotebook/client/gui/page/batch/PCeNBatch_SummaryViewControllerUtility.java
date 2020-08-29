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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.enotebook.client.gui.page.batch.events.PlateSelectionChangedEvent;
import com.chemistry.enotebook.client.gui.page.batch.events.PlateSelectionChangedListener;
import com.chemistry.enotebook.client.gui.tablepreferences.TableColumnInfo;
import com.chemistry.enotebook.client.gui.tablepreferences.TablePreferenceDelegate;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductPlate;
import com.chemistry.enotebook.domain.StoicModelInterface;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 1.2
 * 
 */
public class PCeNBatch_SummaryViewControllerUtility extends PCeNSummaryViewControllerUtility {
	public static final Log log = LogFactory.getLog(PCeNBatch_SummaryViewControllerUtility.class);

	public PCeNBatch_SummaryViewControllerUtility(List<ProductPlate> productPlates, NotebookPageModel pageModel) {
		super(productPlates, pageModel);
		headerNames.add(CPS_REGISTERED);
		headerNames.add(PLATE_COMMENTS);
	}
	
	public void dispose() {
		super.dispose();
	}

	public String[] getHeaderNames() {
		return (String[])headerNames.toArray(new String[headerNames.size()]);
	}

	public Object getValue(int rowIndex, int colIndex) {
		if (productPlates == null)
			return " ";
		ProductPlate plate = productPlates.get(rowIndex);
		if (colIndex == 7) {//"Compounds on registered" 7
			return plate.getRegisteredBatchesCount() + "";
		} 
		return super.getValue(rowIndex, colIndex);
	}

	public void setSelectValue(int rowIndex) {
		ProductPlate plate = productPlates.get(rowIndex);
		plate.setSelect(!plate.isSelect());
		
		List<ProductPlate> selectedPlates = getSelectedPlates();
		Collections.sort(selectedPlates);
		PlateSelectionChangedListener mPlateSelectionChangedListener = null;
		PlateSelectionChangedEvent mPlateSelectionChangedEvent = new PlateSelectionChangedEvent(this, plate, selectedPlates);
	
		for (Iterator<PlateSelectionChangedListener> it = this.plateSelectionChangedListeners.iterator(); it.hasNext();) {
			mPlateSelectionChangedListener = it.next();
			mPlateSelectionChangedListener.plateSelectionChanged(mPlateSelectionChangedEvent);
			// When a plate is selected, do not have to clean up the BatchEditPanel.
			if (plate.isSelect())
				break;
		}
	}
	
	public boolean isCellEditable(int rowIndex, int colIndex) {
		ProductPlate plate = productPlates.get(rowIndex);
		if (plate.isEditable() == false) {
			return false;
		}
		if (colIndex == 7) {
			return false;
		}
		String columnName = headerNames.get(colIndex);
		return super.isCellEditable(columnName);
	}
	
	protected List<ProductPlate> getSelectedPlates() {
		ArrayList<ProductPlate> selectedPlates = new ArrayList<ProductPlate>();
		if (productPlates != null) {
			for (ProductPlate plate : productPlates) {
				if (plate != null && plate.isSelect()) {
					selectedPlates.add(plate);
				}
			}
		}
		return selectedPlates;
	}

	public TableColumnInfo getColumnInfoFromModelIndex(int modelIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public TablePreferenceDelegate getTablePreferenceDelegate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public StoicModelInterface getProductBatchModel(int selectedRowIndex)
	{
	  return null;	
	}
}
