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
package com.chemistry.enotebook.client.gui.page.analytical;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * Description:
 * 
 * Creation Date: Dec 16, 2004
 * 
 * 
 * 
 */
public class AnalyticalSampleReferencesTreeModel extends DefaultTreeModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7860489157034882832L;

	/**
	 * Constructor for AnalyticalSampleReferencesTreeModel.
	 * 
	 * @param treeNode
	 */
	public AnalyticalSampleReferencesTreeModel(TreeNode treeNode) {
		super(treeNode);
	}

	/**
	 * to clear the current tree nodes
	 * 
	 */
	public void clear() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
		root.removeAllChildren();
	}

	public static DefaultMutableTreeNode createRootNode() {
		return new DefaultMutableTreeNode("");
	}
}
