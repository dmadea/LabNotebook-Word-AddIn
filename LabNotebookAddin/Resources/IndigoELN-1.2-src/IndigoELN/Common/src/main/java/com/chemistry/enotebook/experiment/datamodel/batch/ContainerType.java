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
package com.chemistry.enotebook.experiment.datamodel.batch;

/**
 * 
 * 
 */
public class ContainerType {
	public static final int HORIZONTAL_DIRECTION = 0;
	public static final int VERTICAL_DIRECTION = 1;
	public static final ContainerType container = new ContainerType(2, 2, HORIZONTAL_DIRECTION, "xxx");
	private int[] plateBSequence = new int[2];
	private int numRows;
	private int numCols;
	private int direction;
	private int size;
	private String name;

	public ContainerType(int numRows, int numCols, int direction, String name) {
		this.name = name;
		if (numRows <= 0 || numCols <= 0)
			throw new IllegalArgumentException("Container Type have received invalid col or row size");
		size = numRows * numCols;
		this.numRows = numRows;
		this.numCols = numCols;
		if (direction == HORIZONTAL_DIRECTION) {
			plateBSequence[0] = numRows;
			plateBSequence[1] = numCols;
		} else if (direction == VERTICAL_DIRECTION) {
			plateBSequence[0] = numCols;
			plateBSequence[1] = numRows;
		} else
			throw new IllegalArgumentException("Container Type have received invalid direction");

	}

	public static ContainerType getDefaultContainerType() {
		return container;
	}

	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * @return the numCols
	 */
	public int getNumCols() {
		return numCols;
	}

	/**
	 * @param numCols
	 *            the numCols to set
	 */
	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}

	/**
	 * @return the numRows
	 */
	public int getNumRows() {
		return numRows;
	}

	/**
	 * @param numRows
	 *            the numRows to set
	 */
	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	/**
	 * @return the plateBSequence
	 */
	public int[] getPlateBSequence() {
		return plateBSequence;
	}

	/**
	 * @param plateBSequence
	 *            the plateBSequence to set
	 */
	public void setPlateBSequence(int[] plateBSequence) {
		this.plateBSequence = plateBSequence;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	public String toString() {
		// return name+"("+numRows+", "+numCols+"0";
		return name;
	}

	public String getName() {
		return name;
	}
}
