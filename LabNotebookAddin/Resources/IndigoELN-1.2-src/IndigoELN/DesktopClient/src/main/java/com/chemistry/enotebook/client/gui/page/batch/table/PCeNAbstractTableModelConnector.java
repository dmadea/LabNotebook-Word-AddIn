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
package com.chemistry.enotebook.client.gui.page.batch.table;

import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.NotebookPageModel;

public class PCeNAbstractTableModelConnector {
	
	protected String[] columnNames = {
			PCeNTableView.STRUCTURE,
			PCeNTableView.NBK_BATCH_NUM,
			PCeNTableView.TOTAL_WEIGHT,
			PCeNTableView.TOTAL_VOLUME,
			PCeNTableView.WELL_POSITION,
			PCeNTableView.PLATE,
			PCeNTableView.THEORETICAL_WEIGHT,
			PCeNTableView.THEORETICAL_MMOLES,
			PCeNTableView.PERCENT_YIELD,
			PCeNTableView.COMPOUND_STATE,
			PCeNTableView.SALT_CODE,
			PCeNTableView.SALT_EQ,
			PCeNTableView.ANALYTICAL_PURITY,
			PCeNTableView.EXT_SUPPLIER,
			PCeNTableView.MOL_WEIGHT,
			PCeNTableView.MOL_FORMULA,
			PCeNTableView.CONVERSATIONAL_BATCH,
			PCeNTableView.VIRTUAL_COMP_NUM,
			PCeNTableView.STEREOISOMER,
			PCeNTableView.BARCODE,
			PCeNTableView.STATUS,
			PCeNTableView.SOURCE,
			PCeNTableView.SOURCE_DETAILS,
			PCeNTableView.COMMENTS,
			PCeNTableView.PRECURSORS,
			PCeNTableView.HAZARD_COMMENTS,
			PCeNTableView.COMPOUND_PROTECTION
	};
	
	protected String[] headerNames;
	
	protected boolean structureHidden = false;

	private NotebookPageModel pageModel;
	
	protected PCeNAbstractTableModelConnector(NotebookPageModel pageModel)
	{
		this.pageModel = pageModel;
	}

	protected void createHeaderNames() {
		headerNames = columnNames;
	}
	public String[] getHeaderNames() {
		return headerNames;
	}
	
	public boolean isCellEditable(int rowIndex, int colIndex) {
	    if (headerNames[colIndex].equalsIgnoreCase(PCeNTableView.STATUS) 
			|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.COMMENTS)
			|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.TOTAL_WEIGHT)
	    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.TOTAL_VOLUME)
	    	|| headerNames[colIndex].equalsIgnoreCase(PCeNTableView.NBK_BATCH_NUM))
			return true;
		else
			return false;
	}

	public boolean isColumnEditable(String columnName) {
		if 	(columnName.equalsIgnoreCase(PCeNTableView.THEORETICAL_WEIGHT)
				|| columnName.equalsIgnoreCase(PCeNTableView.THEORETICAL_MMOLES)
				|| columnName.equalsIgnoreCase(PCeNTableView.MOL_WEIGHT)
				|| columnName.equalsIgnoreCase(PCeNTableView.MOL_FORMULA)
				|| columnName.equalsIgnoreCase(PCeNTableView.CONVERSATIONAL_BATCH)
				|| columnName.equalsIgnoreCase(PCeNTableView.VIRTUAL_COMP_NUM)
				|| columnName.equalsIgnoreCase(PCeNTableView.STRUCTURE)
				|| columnName.equalsIgnoreCase(PCeNTableView.BARCODE)
				|| columnName.equalsIgnoreCase(PCeNTableView.TA)
				|| columnName.equalsIgnoreCase(PCeNTableView.PROJECT)
				//|| columnName.equalsIgnoreCase(PCeNTableView.STATUS)  // vbtodo fix!
				//|| columnName.equalsIgnoreCase(PCeNTableView.PRECURSORS)
				//|| columnName.equalsIgnoreCase(PCeNTableView.NBK_BATCH_NUM)
				|| columnName.equalsIgnoreCase(PCeNTableView.WELL_POSITION)
				|| columnName.equalsIgnoreCase(PCeNTableView.PLATE)
				|| columnName.equalsIgnoreCase(PCeNTableView.PERCENT_YIELD)
				|| columnName.equalsIgnoreCase(PCeNTableView.AMT_REMAINING_WEIGHT)             
				|| columnName.equalsIgnoreCase(PCeNTableView.AMT_REMAINING_VOLUME)
		)
			return false;
		else
			return true;		
	}
	

	public NotebookPageModel getPageModel() {
		return pageModel;
	}
}
