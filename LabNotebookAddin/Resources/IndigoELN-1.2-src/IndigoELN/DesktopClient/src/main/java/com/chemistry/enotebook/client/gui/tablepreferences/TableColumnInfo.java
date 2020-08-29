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

public class TableColumnInfo {
	private int _modelIndex;
	private int _columnIndex;
	private int _preferredWidth;
	private boolean _visible;
	private String _label = "";

	public TableColumnInfo() {
		_modelIndex = 0;
		_columnIndex = 0;
		_preferredWidth = 0;
		_visible = true;
	}

	public TableColumnInfo(int modelIndex, int columnIndex, int preferedWidth, boolean visible) {
		this._modelIndex = modelIndex;
		this._columnIndex = columnIndex;
		this._preferredWidth = preferedWidth;
		this._visible = visible;
		this._label = "";
	}

	public TableColumnInfo(int modelIndex, int columnIndex, int preferedWidth, boolean visible, String label) {
		this._modelIndex = modelIndex;
		this._columnIndex = columnIndex;
		this._preferredWidth = preferedWidth;
		this._visible = visible;
		this._label = label;
	}
	
	public int getColumnIndex() {
		return _columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this._columnIndex = columnIndex;
	}

	public int getModelIndex() {
		return _modelIndex;
	}

	public void setModelIndex(int modelIndex) {
		this._modelIndex = modelIndex;
	}

	public boolean isVisible() {
		return _visible;
	}

	public boolean getVisible() {
		return _visible;
	}

	public void setVisible(boolean visible) {
		this._visible = visible;
	}

	public int getPreferredWidth() {
		return _preferredWidth;
	}

	public void setPreferredWidth(int preferredWidth) {
		this._preferredWidth = preferredWidth;
	}

	public String getModelIndexAsString() {
		Integer digit = new Integer(_modelIndex);
		return digit.toString();
	}

	public String getColumnIndexAsString() {
		Integer digit = new Integer(_columnIndex);
		return digit.toString();
	}

	public String getPreferredWidthAsString() {
		Integer digit = new Integer(_preferredWidth);
		return digit.toString();
	}

	public String getVisibleAsString() {
		Boolean predicate = new Boolean(_visible);
		return predicate.toString();
	}

	
	public String getLabel() {
		return _label;
	}

	public void setLabel(String label) {
		this._label = label;
	}

	public String toXml() {
		StringBuffer buff = new StringBuffer();
		buff.append("<Model_Index>").append(this.getModelIndex()).append("</Model_Index>");
		buff.append("<Column_Index>").append(this.getColumnIndex()).append("</Column_Index>");
		buff.append("<Preferred_Width>").append(this.getPreferredWidthAsString()).append("</Preferred_Width>");
		buff.append("<Visible>").append(this.getVisibleAsString()).append("</Visible>");
		buff.append("<Label>").append(this.getLabel()).append("</Label>");
		return buff.toString();
	}
	
	public String toString() {
		return this.toXml();
	}
}
