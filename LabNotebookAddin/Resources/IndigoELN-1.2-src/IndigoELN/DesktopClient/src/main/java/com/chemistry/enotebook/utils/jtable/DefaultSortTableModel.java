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
package com.chemistry.enotebook.utils.jtable;

import javax.swing.table.DefaultTableModel;
import java.util.Collections;
import java.util.Vector;

/**
 * 
 * 
 */
public class DefaultSortTableModel extends DefaultTableModel implements SortableTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2275855280877088623L;
	protected int sortedColumnIndex = -1;
	protected boolean sortedColumnAscending = true;

	public DefaultSortTableModel() {
	}

	public DefaultSortTableModel(int rows, int cols) {
		super(rows, cols);
	}

	public DefaultSortTableModel(Object[][] data, Object[] names) {
		super(data, names);
	}

	public DefaultSortTableModel(Object[] names, int rows) {
		super(names, rows);
	}

	public DefaultSortTableModel(Vector names, int rows) {
		super(names, rows);
	}

	public DefaultSortTableModel(Vector data, Vector names) {
		super(data, names);
	}

	public boolean isSortable(int col) {
		return true;
	}

	public void sortColumn(int col, boolean ascending) {
		sortedColumnIndex = col;
		sortedColumnAscending = ascending;
		Collections.sort(getDataVector(), new ColumnComparator(col, ascending));
	}

	public int getSortedColumnIndex() {
		return sortedColumnIndex;
	}

	public void setSortedColumnIndex(int sortedColumnIndex) {
		this.sortedColumnIndex = sortedColumnIndex;
	}

	public boolean isSortedColumnAscending() {
		return sortedColumnAscending;
	}

	public void setSortedColumnAscending(boolean sortedColumnAscending) {
		this.sortedColumnAscending = sortedColumnAscending;
	}
}