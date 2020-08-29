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
 * Created on Jan 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.stoichiometry.search.dbsetup;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class CheckNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7800812700230851241L;
	public final static int SINGLE_SELECTION = 0;
	public final static int DIG_IN_SELECTION = 4;
	protected int selectionMode;
	protected boolean isSelected;

	public CheckNode() {
		this(null);
	}

	public CheckNode(Object userObject) {
		this(userObject, true, false);
	}

	public CheckNode(Object userObject, boolean allowsChildren, boolean isSelected) {
		super(userObject, allowsChildren);
		this.isSelected = isSelected;
		setSelectionMode(SINGLE_SELECTION);
	}

	public void setSelectionMode(int mode) {
		selectionMode = mode;
	}

	public int getSelectionMode() {
		return selectionMode;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {
			Enumeration enumx = children.elements();
			while (enumx.hasMoreElements()) {
				CheckNode node = (CheckNode) enumx.nextElement();
				node.setSelected(isSelected);
			}
		}
	}

	public boolean isSelected() {
		return isSelected;
	}
}
