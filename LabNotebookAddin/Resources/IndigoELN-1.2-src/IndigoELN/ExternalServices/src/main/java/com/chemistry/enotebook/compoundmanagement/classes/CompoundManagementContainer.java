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
package com.chemistry.enotebook.compoundmanagement.classes;

import java.io.Serializable;

public class CompoundManagementContainer implements Serializable {
	
	private static final long serialVersionUID = -2879947957281068004L;

	public CompoundManagementContainer(String description, int xPositions,
			int yPositions, String majorAxis) {
		this.description = description;
		this.xPositions = xPositions;
		this.yPositions = yPositions;
		this.majorAxis = majorAxis;
	}

	private String description;
	private int xPositions;
	private int yPositions;
	private String majorAxis;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getXPositions() {
		return xPositions;
	}

	public void setXPositions(int xPositions) {
		this.xPositions = xPositions;
	}

	public int getYPositions() {
		return yPositions;
	}

	public void setYPositions(int yPositions) {
		this.yPositions = yPositions;
	}

	public String getMajorAxis() {
		return majorAxis;
	}

	public void setMajorAxis(String majorAxis) {
		this.majorAxis = majorAxis;
	}
}
