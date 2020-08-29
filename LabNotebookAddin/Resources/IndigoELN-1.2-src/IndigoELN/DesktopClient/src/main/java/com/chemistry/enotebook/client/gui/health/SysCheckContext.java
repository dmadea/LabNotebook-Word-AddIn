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
/*
 * Created on 15-Dec-2004
 */
package com.chemistry.enotebook.client.gui.health;

/**
 * 
 */
public class SysCheckContext {
	/**
	 * This class holds all the information that is needed by the Health Check Dlg to show how healty the CeN system is needs to be
	 * retrived populated from the session service
	 */
	private String _errMess; // meaningful system error message
	private SysItem[] _sysItems; // array of system items
	private boolean _sysOperational; // Overall is system operational, true

	// operational, false dead
	public SysCheckContext() {
		// super();
		_errMess = "";
		_sysItems = new SysItem[0];
		_sysOperational = false;
	}

	public SysCheckContext(SysItem[] sysItems, String errMess, boolean sysOperationl) {
		_sysItems = sysItems;
		_errMess = errMess;
		_sysOperational = sysOperationl;
	}

	/**
	 * @return Returns the _errMess.
	 */
	public String get_errMess() {
		return _errMess;
	}

	/**
	 * @param mess
	 *            The _errMess to set.
	 */
	public void set_errMess(String mess) {
		_errMess = mess;
	}

	/**
	 * @return Returns the _sysItems.
	 */
	public SysItem[] get_sysItems() {
		return _sysItems;
	}

	/**
	 * @param items
	 *            The _sysItems to set.
	 */
	public void set_sysItems(SysItem[] items) {
		_sysItems = items;
	}

	/**
	 * @return Returns the _sysOperational.
	 */
	public boolean is_sysOperational() {
		return _sysOperational;
	}

	/**
	 * @param operational
	 *            The _sysOperational to set.
	 */
	public void set_sysOperational(boolean operational) {
		_sysOperational = operational;
	}
}
