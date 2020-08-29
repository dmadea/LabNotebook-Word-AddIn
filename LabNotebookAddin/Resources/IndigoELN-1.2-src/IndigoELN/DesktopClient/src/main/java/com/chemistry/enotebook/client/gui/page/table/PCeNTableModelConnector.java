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
package com.chemistry.enotebook.client.gui.page.table;

import com.chemistry.enotebook.client.gui.page.experiment.stoich.StoichDataChangesListener;
import com.chemistry.enotebook.client.gui.tablepreferences.TableColumnInfo;
import com.chemistry.enotebook.client.gui.tablepreferences.TablePreferenceDelegate;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.StoicModelInterface;

import java.util.List;

/**
 * 
 * 
 */
public interface PCeNTableModelConnector {
	String[] getHeaderNames();

	Object getValue(int rowIndex, int colIndex);

	boolean isCellEditable(int rowIndex, int colIndex);

	void setValue(Object value, int rowIndex, int colIndex);

	boolean isStructureHidden();

	//List getListOfBatchesWithMolStrings();

	List getAbstractBatches();

	String getTableHeaderTooltip(String headerName);
	
	boolean isColumnEditable(String columnName);
	
	public int getRowCount();
	
	List<StoicModelInterface> getStoicElementListInTransactionOrder();
	public boolean isSortable(int colIndex) ;
	
	void updateColumn(String cloumnname, Object newValue);
	
	void setSelectValue(int selectedRow);  // vb 8/16
	
	void sortBatches(int colIndex, boolean ascending); // vb 11/5

	void addStoichDataChangesListener(StoichDataChangesListener stoichDataChangesListener) throws Exception;  // vb 1/17

	StoicModelInterface getBatchModel(int selectedRow);
	
	TableColumnInfo getColumnInfoFromModelIndex(int modelIndex);
	
	TablePreferenceDelegate getTablePreferenceDelegate();

	NotebookPageModel getPageModel();
	
	
}
