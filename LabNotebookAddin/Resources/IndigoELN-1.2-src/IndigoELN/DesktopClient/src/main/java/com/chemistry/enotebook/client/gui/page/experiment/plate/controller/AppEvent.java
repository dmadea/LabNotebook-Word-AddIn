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
package com.chemistry.enotebook.client.gui.page.experiment.plate.controller;

public class AppEvent extends java.util.EventObject {
	
	private static final long serialVersionUID = 2476250872989543128L;
	
	public final static String NAV_EVENT = "NAV_EVENT";
	public final static String STATUS_EVENT = "STATUS_EVENT";
	public final static String DATA_EVENT = "DATA_EVENT";
	public final static String SOURCE_EVENT = "SOURCE_EVENT";
	public final static String SORT_EVENT = "SORT_EVENT";
	public final static String EXCLUDE_EVENT = "EXCLUDE_EVENT";
	public final static String TARGET_EVENT = "TARGET_EVENT";

	private String strMessage_;

	protected String strEventType_;

	public AppEvent(String strEventType) {
		super(strEventType);
		strEventType_ = strEventType;
	}

	public boolean isNavEvent() {
		if (strEventType_.equals(STATUS_EVENT))
			return true;
		else
			return false;
	}

	public boolean isStatusEvent() {
		if (strEventType_.equals(STATUS_EVENT))
			return true;
		else
			return false;
	}

	public boolean isDataEvent() {
		if (strEventType_.equals(DATA_EVENT))
			return true;
		else
			return false;
	}

	public boolean isSourceEvent() {
		if (strEventType_.equals(SOURCE_EVENT))
			return true;
		else
			return false;
	}

	public boolean isSortEvent() {
		if (strEventType_.equals(SORT_EVENT))
			return true;
		else
			return false;
	}

	public void setMessage(String strMsg) {
		strMessage_ = strMsg;
	}

	public String getMessage() {
		return strMessage_;
	}

	public boolean isExcludeEvent() {
		if (strEventType_.equals(EXCLUDE_EVENT))
			return true;
		else
			return false;
	}

	public boolean isTargetEvent() {
		if (strEventType_.equals(TARGET_EVENT))
			return true;
		else
			return false;
	}

}