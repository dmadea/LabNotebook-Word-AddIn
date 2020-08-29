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
 * Created on Nov 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.client.datamodel.speedbar;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class SpeedBarLoadable implements SpeedBarNodeInterface {
	public final static int NOT_LOADED = 0;
	public final static int LOADING = 1;
	public final static int LOADED = 2;
	private int _load = NOT_LOADED;

	public boolean isLoaded() {
		return _load == LOADED;
	}

	public boolean isLoading() {
		return _load == LOADING;
	}

	public boolean isLoadable() {
		return !(isLoaded() || isLoading());
	}

	public void setLoading() {
		_load = LOADING;
	}

	public void setLoaded() {
		_load = LOADED;
	}
}
