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
package com.chemistry.enotebook.client.gui.page.stoichiometry;

import com.chemistry.enotebook.experiment.common.units.UnitType;

/**
 * 
 * 
 *
 */
public class ColumnProperties {
	public int modelIndex = 0;
	public String colName = "Column Name";
	public Class<?> classType = String.class;
	public int prefWidth = 70;
	public int maxWidth = 100;
	public int minWidth = 50;
	public boolean visible = true;
	public UnitType type = null;

	public ColumnProperties(int columnIndex) {
		modelIndex = columnIndex;
	}

	public ColumnProperties(int modelIndex, String colName, Class<?> colClass) {
		this.modelIndex = modelIndex;
		this.colName = colName;
		classType = colClass;
	}

	public ColumnProperties(int modelIndex, String colName, Class<?> colClass, int prefWidth, int maxWidth, int minWidth) {
		this(modelIndex, colName, colClass);
		this.prefWidth = prefWidth;
		this.maxWidth = maxWidth;
		this.minWidth = minWidth;
	} // end constructor

	public ColumnProperties(int modelIndex, String colName, Class<?> colClass, int prefWidth, int maxWidth, int minWidth, UnitType type) {
		this(modelIndex, colName, colClass);
		this.prefWidth = prefWidth;
		this.maxWidth = maxWidth;
		this.minWidth = minWidth;
		this.type = type;
	}
}
