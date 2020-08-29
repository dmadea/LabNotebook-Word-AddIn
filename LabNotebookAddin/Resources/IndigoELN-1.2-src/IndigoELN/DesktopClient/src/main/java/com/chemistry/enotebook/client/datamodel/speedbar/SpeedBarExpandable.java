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
public class SpeedBarExpandable implements SpeedBarNodeInterface {
	public final static int NOT_EXPANDED = 0;
	public final static int EXPANDING = 1;
	public final static int EXPANDED = 2;
	private int _expand = NOT_EXPANDED;

	public boolean isExpanded() {
		return _expand == EXPANDED;
	}

	public boolean isExpanding() {
		return _expand == EXPANDING;
	}

	public boolean isExpandable() {
		return !(isExpanded() || isExpanding());
	}

	public void setExpanding() {
		_expand = EXPANDING;
	}

	public void setExpanded() {
		_expand = EXPANDED;
	}
}
