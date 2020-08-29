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

import java.util.LinkedHashMap;

/**
 * 
 * 
 *
 */
public abstract class AbstractBatchColumnModel {
	/**
	 * Holds a class ColumnWidth that contains the colIndex and all size values. The linked hashmap will keep things in order and
	 * return them in the same order that they were entered into the map.
	 */
	protected LinkedHashMap<Integer, ColumnProperties> colPropMap = new LinkedHashMap<Integer, ColumnProperties>();

	// Uses default constructor;
	public UnitType getAmountUnitType(int colModelIndex) {
		return colPropMap.get(new Integer(colModelIndex)).type;
	}

	public Class<?> getColumnClass(int colModelIndex) {
		return colPropMap.get(new Integer(colModelIndex)).classType;
	}

	public String getColumnName(int colModelIndex) {
		return colPropMap.get(new Integer(colModelIndex)).colName;
	}

	public int getColumnCount() {
		return colPropMap.size();
	}

	// public abstract List getRowFromBatch(AbstractBatch batch);
	public int getColumnPreferredWidth(int colModelIndex) {
		return colPropMap.get(new Integer(colModelIndex)).prefWidth;
	}

	public int getColumnMaxWidth(int colModelIndex) {
		return colPropMap.get(new Integer(colModelIndex)).maxWidth;
	}

	public int getColumnMinWidth(int colModelIndex) {
		return colPropMap.get(new Integer(colModelIndex)).minWidth;
	}
}
