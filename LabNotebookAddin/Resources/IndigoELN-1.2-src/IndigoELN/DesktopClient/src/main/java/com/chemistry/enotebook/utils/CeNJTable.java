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
 * CeNJTable.java
 * 
 * Created on Feb 10, 2005
 *
 * 
 */
package com.chemistry.enotebook.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 
 * @date Feb 10, 2005
 */
public class CeNJTable extends SimpleJTable implements MouseListener {

	private static final long serialVersionUID = -1466905222480437996L;

	public CeNJTable() {
		super();
		setRowHeight(18);
	}

	/**
	 * Popup menu to assign to this table
	 * 
	 * @param menu
	 */
	public CeNJTable(JPopupMenu menu) {
		super(menu);
		setRowHeight(18);
	}

	public void stopEditing() {
		// Stop editing if we are not over the cell we are editing.
		if (isEditing()) {
			getCellEditor(getEditingRow(), getEditingColumn()).stopCellEditing();
		}
	}

	//
	// Mouse Listener
	//
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		showPopupMenu(e);
	}

	public void mouseReleased(MouseEvent evt) {
		// Stop editing if we are not over the cell we are editing.
		stopEditing();
		if (evt.getButton() == MouseEvent.BUTTON3) {
			// left-click so select current row
			clearSelection();
			Point p = evt.getPoint();
			int row = rowAtPoint(p);
			if (row >= 0) {
				addRowSelectionInterval(row, row);
			}
		}
		showPopupMenu(evt);
	}
	
	private void showPopupMenu(MouseEvent e) {
		if (e.isPopupTrigger()) {
			if (popupMenu != null) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}
