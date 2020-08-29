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
package com.chemistry.enotebook.client.gui.page.experiment;

import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.domain.container.Container;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class ContainerTreeCellRederer extends DefaultTreeCellRenderer {
	
	private static final long serialVersionUID = -4282977300646337190L;

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		ImageIcon sbIcon = null;
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
		Object usrObj = treeNode.getUserObject();
		//if (usrObj instanceof MonomerPlate) {
		//	sbIcon = CenIconFactory.getImageIcon(CenIconFactory.ContainerTree.MONO_PLATE);
		//} //else if (usrObj instanceof ProductPlate) {
			sbIcon = CenIconFactory.getImageIcon(CenIconFactory.ContainerTree.PROD_PLATE);
		//} else if (usrObj instanceof RegisteredPlate) {
		//	sbIcon = CenIconFactory.getImageIcon(CenIconFactory.ContainerTree.PROD_PLATE);
		//} else 
		if (usrObj instanceof Container) {
			sbIcon = CenIconFactory.getImageIcon(CenIconFactory.ContainerTree.CONTAINER);
		} else if (usrObj instanceof CenIconFactory) {
			sbIcon = CenIconFactory.getImageIcon(CenIconFactory.ContainerTree.CONTAINER);
		} else if (usrObj instanceof String) {
			if (expanded) {
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.ContainerTree.OPEN_FOLDER);
			} else {
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.ContainerTree.CLOSE_FOLDER);
			}
		}
		setIcon(sbIcon);
		return this;
	}
}
