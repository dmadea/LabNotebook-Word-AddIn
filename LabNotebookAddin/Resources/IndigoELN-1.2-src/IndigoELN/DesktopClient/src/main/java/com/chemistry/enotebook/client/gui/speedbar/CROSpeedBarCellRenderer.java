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
package com.chemistry.enotebook.client.gui.speedbar;

import com.chemistry.enotebook.client.datamodel.speedbar.CRONotebook;
import com.chemistry.enotebook.client.datamodel.speedbar.CRONotebookPage;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.chemistry.enotebook.domain.CROChemistInfo;
import com.chemistry.enotebook.domain.CROPageInfo;
import com.chemistry.enotebook.domain.CRORequestInfo;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;


public class CROSpeedBarCellRenderer extends DefaultTreeCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2467588328635380366L;

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		ImageIcon sbIcon = null;
		String sbToolTip = null;
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
		Object usrObj = treeNode.getUserObject();
		sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.CONTENTS);
		
		if (usrObj instanceof CROPageInfo) {
			sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.WORLD);
		} else if (usrObj instanceof CRONotebookPage) {
			sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_COMPLETE);
			// handle tooltip
			Object p = usrObj;
//			System.out.println();
		} else if (usrObj instanceof CROChemistInfo) {
			sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.USER);
		} else if (usrObj instanceof CRORequestInfo) {
			sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.PS_SINGLETON_OPEN);
		} else if (usrObj instanceof CRONotebook) {
			sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.BOOK_STATUS_CLOSED);
		} else {
			if (expanded)
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.WORLD);
			else
				sbIcon = CenIconFactory.getImageIcon(CenIconFactory.SpeedBar.WORLD);
		}
		
		setIcon(sbIcon);
	
		return this;
	}
}

