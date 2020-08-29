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
 * Created on 27-May-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.chemistry.enotebook.client.gui.common.utils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * 
 * 
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 * Comments
 */
public class CheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {
	
	private static final long serialVersionUID = 1245014377001767588L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean,
	 *      boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex,
			int vColIndex) {
		// TODO Auto-generated method stub
		// value is the value contained in the cell lacated at rowIndex,
		// vColIndex
		if (isSelected) {
			// cell(s) are selected
		}
		if (hasFocus) {
			// this cell is the anchur and the table has focus
		}
		// Display the value in the checkbox
		if (value.toString().matches("true")) {
			setSelected(true);
		} else {
			setSelected(false);
		}
		return this;
	}

	// must override these methods for performance reasons
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#firePropertyChange(java.lang.String, boolean, boolean)
	 */
	public void firePropertyChange(String arg0, boolean arg1, boolean arg2) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	protected void firePropertyChange(String arg0, Object arg1, Object arg2) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#revalidate()
	 */
	public void revalidate() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#validate()
	 */
	public void validate() {
	}
}
