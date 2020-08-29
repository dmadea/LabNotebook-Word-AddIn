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
package com.virtuan.plateVisualizer;

/**
 * Title: WellProperty Description: Class representing a property for a well
 */
public class WellProperty implements Cloneable, Comparable {
	private String _name;
	private String _value;
	private boolean _isDisplayed;
	private boolean _isEditable;  
	private int _order;

	/**
	 * parameterized constructor
	 * 
	 * @param name
	 * @param value
	 * @param isDisplayed
	 * @param order
	 *            int for use in sorting properties for display
	 */
	public WellProperty(String name, String value, boolean isDisplayed, int order) {
		_name = name;
		_value = value;
		_isDisplayed = isDisplayed;
		_order = order;
		_isEditable = false; // vb 5/29
	}

	/**
	 * parameterized constructor  // vb 5/29
	 * 
	 * @param name
	 * @param value
	 * @param isDisplayed
	 * @param order int for use in sorting properties for display
	 * @param isEditable
	 *            
	 */
	public WellProperty(String name, String value, boolean isDisplayed, int order, boolean isEditable) {
		_name = name;
		_value = value;
		_isDisplayed = isDisplayed;
		_order = order;
		_isEditable = isEditable; 
	}
	
	/**
	 * method to return the property name
	 * 
	 * @return String value of the name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * method to retun the property value
	 * 
	 * @return
	 */
	public String getValue() {
		return _value;
	}

	/**
	 * method to return whether the property is displayed
	 * 
	 * @return
	 */
	public boolean getIsDisplayed() {
		return _isDisplayed;
	}

	/**
	 * method to return whether the property is editable
	 * 
	 * @return
	 */
	public boolean getIsEditable() {
		return _isEditable;
	}
	
	/**
	 * method to return the sort order
	 * 
	 * @return int
	 */
	public int getOrder() {
		return _order;
	}

	/**
	 * method to set the name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		_name = name;
	}

	/**
	 * method to set the value
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		_value = value;
	}

	/**
	 * method to set display status
	 * 
	 * @param isDisplayed
	 */
	public void setIsDisplayed(boolean isDisplayed) {
		_isDisplayed = isDisplayed;
	}

	/**
	 * method to set editable status
	 * 
	 * @param isDisplayed
	 */
	public void setIsEditable(boolean isEditable) {
		_isEditable = isEditable;
	}
	
	/**
	 * method to set the sort order
	 * 
	 * @param order
	 */
	public void setOrder(int order) {
		_order = order;
	}

	/**
	 * method to clone the property
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	
	/**
	 * implementation of the comparator interface
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public int compareTo(Object o1) {
		if (o1.getClass().getName().equals(this.getClass().getName())) {
			WellProperty w1 = (WellProperty) o1;
			return this.getOrder() - w1.getOrder();
		} else
			return 0;
	}
}
