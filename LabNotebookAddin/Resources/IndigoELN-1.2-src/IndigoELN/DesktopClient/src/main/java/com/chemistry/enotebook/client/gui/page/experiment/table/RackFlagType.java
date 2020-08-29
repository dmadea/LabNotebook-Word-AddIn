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

/**
 * 
 * 
 */
public class RackFlagType {
	private Object[] rackPlateFlagValues;// = new String[]
	// {"Intermediate", "Crude
	// Product"};
	private int selected = 0;

	public RackFlagType(int a, Object[] obj) {
		selected = a;
		rackPlateFlagValues = obj;
	}

	/**
	 * @return the rackPlateFlagValues
	 */
	public Object[] getRackPlateFlagValues() {
		return rackPlateFlagValues;
	}

	/**
	 * @param rackPlateFlagValues
	 *            the rackPlateFlagValues to set
	 */
	public void setRackPlateFlagValues(Object[] rackPlateFlagValues) {
		this.rackPlateFlagValues = rackPlateFlagValues;
	}

	/**
	 * @return the selected
	 */
	public int getSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(int selected) {
		this.selected = selected;
	}
}
