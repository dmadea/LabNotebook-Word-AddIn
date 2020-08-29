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
package com.chemistry.enotebook.client.gui.query_search;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ImageIconRenderer implements TableCellRenderer {
	public JLabel label = new JLabel();
	
	public Component getTableCellRendererComponent(
			JTable jTable, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {

		ImageIcon imageIcon = (ImageIcon)obj;
		if (imageIcon == null) {
			label.setIcon(null);
			return label;
		}
		Image img = imageIcon.getImage();
		
		double imgRatio = (double)imageIcon.getIconWidth() / (double)imageIcon.getIconHeight(); 

		Rectangle cellRect = jTable.getCellRect(row, column, true);		
		double cellRatio = (double)cellRect.width / (double)cellRect.height;
		Image newimg = null;
		
		if (imgRatio > cellRatio) {
			newimg = img.getScaledInstance(cellRect.width, (int)(cellRect.width / imgRatio), java.awt.Image.SCALE_SMOOTH);	
		} else {
			newimg = img.getScaledInstance((int)(cellRect.height * imgRatio), cellRect.height, java.awt.Image.SCALE_SMOOTH);
		}
		ImageIcon newIcon = new ImageIcon(newimg);  
		label.setIcon(newIcon);
		
		return label;
	}
}
