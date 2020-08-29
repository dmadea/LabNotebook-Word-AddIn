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
package com.chemistry.enotebook.compoundaggregation.classes;

import java.io.Serializable;

public class ScreenPanel implements Serializable {
	
	private static final long serialVersionUID = 7250396398530590823L;

	private final static float DEFAULT_MIN_PANEL_AMOUNT = 0.01f;
	
	private long key = 0;
	private String name = "";
	private float minPanelAmount = DEFAULT_MIN_PANEL_AMOUNT;

	private Object informativeObject;
	
	public ScreenPanel(long key, String name, float minPanelAmount, Object informativeObject) {
		this.key = key;
		this.name = name;
		this.minPanelAmount = minPanelAmount;
		this.informativeObject = informativeObject;
	}
	public ScreenPanel(Object informativeObject) {
		this.informativeObject = informativeObject;
	}
	public Object getInformativeObject() {
		return informativeObject;
	}
	public void setInformativeObject(Object informativeObject) {
		this.informativeObject = informativeObject;
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getMinPanelAmount() {
		return minPanelAmount;
	}
	public void setMinPanelAmount(float minPanelAmount) {
		this.minPanelAmount = minPanelAmount;
	}
}
