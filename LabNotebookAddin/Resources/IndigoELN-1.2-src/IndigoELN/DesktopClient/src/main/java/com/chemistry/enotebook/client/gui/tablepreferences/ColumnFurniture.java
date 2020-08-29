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
package com.chemistry.enotebook.client.gui.tablepreferences;

import javax.swing.table.TableColumn;
import java.util.Comparator;

public class ColumnFurniture extends TableColumn implements Comparable, Comparator {
	
	private static final long serialVersionUID = 6143815178816338974L;
	
	private boolean visible;
	private int columnIndex;
	private int savedMinWidth;
	private int savedMaxWidth;
	private int savedPreferredWidth;
	private String label;

	public ColumnFurniture() {
		super();
		visible = true;
		columnIndex = 0;
	}

	public ColumnFurniture(TableColumn passedColumn, boolean visible, int columnIndex, int preferredWidth) {
		super(passedColumn.getModelIndex(), preferredWidth, passedColumn.getCellRenderer(), passedColumn.getCellEditor());
		this.setMinWidth(passedColumn.getMinWidth());
		this.setMaxWidth(passedColumn.getMaxWidth());
		this.setHeaderValue(passedColumn.getHeaderValue());
		this.setPreferredWidth(preferredWidth);
		this.visible = visible;
		this.columnIndex = columnIndex;
		this.savedMinWidth = passedColumn.getMinWidth();
		this.savedMaxWidth = passedColumn.getMaxWidth();
		this.savedPreferredWidth = preferredWidth;
		this.label = "";
	}
	
	public ColumnFurniture(TableColumn passedColumn, boolean visible, int columnIndex, int preferredWidth, String label) {
		super(passedColumn.getModelIndex(), preferredWidth, passedColumn.getCellRenderer(), passedColumn.getCellEditor());
		this.setMinWidth(passedColumn.getMinWidth());
		this.setMaxWidth(passedColumn.getMaxWidth());
		this.setHeaderValue(passedColumn.getHeaderValue());
		this.setPreferredWidth(preferredWidth);
		this.visible = visible;
		this.columnIndex = columnIndex;
		this.savedMinWidth = passedColumn.getMinWidth();
		this.savedMaxWidth = passedColumn.getMaxWidth();
		this.savedPreferredWidth = preferredWidth;
		if (label != null)
			this.label = label;
		else
			this.label = "";
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int colIndex) {
		this.columnIndex = colIndex;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getSavedMaxWidth() {
		return savedMaxWidth;
	}

	public void setSavedMaxWidth(int savedMaxWidth) {
		this.savedMaxWidth = savedMaxWidth;
	}

	public int getSavedMinWidth() {
		return savedMinWidth;
	}

	public void setSavedMinWidth(int savedMinWidth) {
		this.savedMinWidth = savedMinWidth;
	}

	public int compareTo(Object other) {
		ColumnFurniture otherColumn = (ColumnFurniture) other;
		return columnIndex - otherColumn.columnIndex;
	}

	public int compare(Object obj1, Object obj2) {
		return ((ColumnFurniture) obj1).columnIndex - ((ColumnFurniture) obj2).columnIndex;
	}

	public int getSavedPreferredWidth() {
		return savedPreferredWidth;
	}

	public void setSavedPreferredWidth(int savedPreferedWidth) {
		this.savedPreferredWidth = savedPreferedWidth;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append(this.headerValue.toString()).append("  ");
		buff.append(this.savedPreferredWidth);
		return buff.toString();
	}
}
