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
package com.chemistry.enotebook.client.gui.page.stoichiometry.search;

import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.stoichiometry.FractionCellRenderer;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

/**
 * 
 * 
 *
 */
public class StructureLookupCriteriaViewer extends JTable implements Observer {
	
	private static final long serialVersionUID = -3885026280342665155L;
	
	protected JPopupMenu popupMenu = null;

	public StructureLookupCriteriaViewer() {
		popupMenu = new JPopupMenu();
		init();
	}

	/**
	 * @param menu
	 */
	public StructureLookupCriteriaViewer(JPopupMenu menu) {
		popupMenu = menu;
		init();
		addMouseListener(new MouseInputAdapter() {
			public void mouseReleased(MouseEvent e) {
				// left-click so select current row
				if (e.isPopupTrigger() && popupMenu != null) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	public void dispose() {
		super.setModel(new StructureLookupCriteriaTableModel(null));
	}

	private void init() {
		setModel(new StructureLookupCriteriaTableModel(null));
	}

	public void refresh() {
		JTableHeader th = getTableHeader();
		th.setReorderingAllowed(false);
		setDefaultRenderer(java.lang.Number.class, new FractionCellRenderer(10, 6, SwingConstants.RIGHT));
		// Header Bolding
		CeNGUIUtils.styleComponentText(Font.BOLD, getTableHeader());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		DefaultTableCellRenderer strRendr = (DefaultTableCellRenderer) this.getDefaultRenderer(String.class);
		strRendr.setHorizontalAlignment(SwingConstants.CENTER);
		initColWidths();
		// L&F for table SearchCriteria
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);
	}

	public void initColWidths() {
		TableColumnModel tcm = getColumnModel();
		StructureLookupCriteriaColumnModel rcm = StructureLookupCriteriaColumnModel.getInstance();
		TableColumn tCol;
		for (int i = 0; i < rcm.getColumnCount(); i++) {
			tCol = tcm.getColumn(i);
			tCol.setPreferredWidth(rcm.getColumnPreferredWidth(i));
			tCol.setMinWidth(rcm.getColumnMinWidth(i));
		}
	}

	// 
	// Overrides
	//
	public void setModel(TableModel arg0) {
		// Prevent the rebuilding of the TableColumnModels every time
		// the data is refreshed. Needs to happen only once.
		super.setModel(arg0);
		if (arg0 instanceof StructureLookupCriteriaTableModel) {
			refresh();
			((StructureLookupCriteriaTableModel) arg0).fireTableStructureChanged();
		}
	}

	public Point getToolTipLocation(MouseEvent e) {
		return new Point(e.getX() + 5, e.getY() + 20);
	}

	public void setErrorMsg(int row, String msg) {
		StructureLookupCriteriaTableModel slctm = (StructureLookupCriteriaTableModel) getModel();
		if (row >= 0 && row < getRowCount()) {
			slctm.setErrorMsg(row, msg);
		}
	}

	public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
		Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
		if (c instanceof JComponent) {
			JComponent jc = (JComponent) c;
			String newText = null;
			if (rowIndex >= 0 && rowIndex < getRowCount() && !isEditing() && !popupMenu.isVisible())
				switch (vColIndex) {
					case (StructureLookupCriteriaColumnModel.INFO): {
						newText = ((StructureLookupCriteriaTableModel) getModel()).getToolTipText(rowIndex);
						break;
					}
					default:
						break;
				}
			jc.setToolTipText(newText);
		}
		return c;
	}

	//
	// Implements
	//
	public void update(Observable arg0, Object arg1) {
		refresh();
	}
}