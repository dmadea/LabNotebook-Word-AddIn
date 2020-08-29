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
 * Created on Mar 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.regis_submis.uc;

import com.chemistry.enotebook.vnv.classes.UcCompoundInfo;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class UcPropertiesAreaRenderer extends JTable implements TableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -921813417832183700L;
	private PropertyModel model = null;

	public UcPropertiesAreaRenderer() {
		model = new PropertyModel();
		setModel(model);
		TableColumnModel vColumModel = getColumnModel();
		TableColumn column = vColumModel.getColumn(0);
		column.setPreferredWidth(30);
		column.setCellRenderer(new TableCellColorRenderer());
		column = vColumModel.getColumn(1);
		column.setPreferredWidth(130);
		column.setCellRenderer(new TableCellColorRenderer());
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
	}

	public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		if (value != null) {
			model.setDataModel((UcCompoundInfo) value);
			model.fireTableDataChanged();
		}
		return this;
	}

	class PropertyModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 787886901458585699L;
		private UcCompoundInfo ucInfo = null;

		public String getColumnName(int col) {
			return "";
		}

		public int getRowCount() {
			return 5;
		}

		public int getColumnCount() {
			return 2;
		}

		public Object getValueAt(int row, int col) {
			if (col < 0 || col >= getColumnCount() || row < 0 || row >= getRowCount())
				return "";
			if (col == 0) {
				switch (row) {
					case 0:
						return "Structure ID:";
					case 1:
						return "MF:";
					case 2:
						return "MW:";
					case 3:
						return "Stereoisomer Code:";
					case 4:
						return "Comment:";
				}
				return "";
			} else {
				switch (row) {
					case 0:
						return ucInfo.getRegNumber();
					case 1:
						return ucInfo.getMolFormula();
					case 2:
						return ucInfo.getMolWgt();
					case 3:
						return ucInfo.getIsomerCode();
					case 4:
						return ucInfo.getComments();
				}
				return "";
			}
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public void setValueAt(Object value, int row, int col) {
		}

		public void setDataModel(UcCompoundInfo value) {
			ucInfo = value;
		}
	};

	class TableCellColorRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6919716309961384038L;

		public final Component getTableCellRendererComponent(JTable table, Object object, boolean isSelected, boolean hasFocus,
				int row, int column) {
			super.getTableCellRendererComponent(table, object, isSelected, hasFocus, row, column);
			setBackground(((row % 2) == 0) ? new Color(231, 211, 231) : table.getBackground());
			return this;
		}
	}
}
