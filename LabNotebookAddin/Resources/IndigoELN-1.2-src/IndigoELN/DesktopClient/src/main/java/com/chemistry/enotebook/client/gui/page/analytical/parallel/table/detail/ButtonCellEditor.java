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
package com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail;

import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

public class ButtonCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = -7626356106268413809L;
	
	protected JButton button;
	private String label;
	private final AnalyticalDetailTableView table;

	public ButtonCellEditor(AnalyticalDetailTableView table) {
		super(new JCheckBox());
		this.table = table;
		this.init();
	}

	private void init() {
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = button.getText();
				
				if (StringUtils.isBlank(text)) {
					return;
				}
				
				String sampleRef = table.getValueAt(table.getSelectedRow(), table.getColIndex(PCeNTableView.NBK_BATCH_NUM)).toString();
				   
				fireEditingStopped();
				
				if (StringUtils.equals(text, "Q")) {
					table.performQuickLink(sampleRef);
				} else if (StringUtils.equals(text, "P")) {
					table.performPurificationServiceDataSearch(sampleRef);
				}
			}
		});
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		label = (value == null) ? "" : value.toString();
		button.setText(label);
		button.setBackground(table.getBackground());
		button.setForeground(Color.GREEN);
		CeNGUIUtils.styleComponentText(Font.BOLD, button);
		return button;
	}

	public Object getCellEditorValue() {
		return label;
	}

	public void cancelCellEditing() {
	}

	public boolean isCellEditable(EventObject e) {
		return true;
	}

	public boolean shouldSelectCell(EventObject e) {
		return true;
	}

	public void addCellEditorListener(CellEditorListener e) {
	}

	public void removeCellEditorListener(CellEditorListener e) {
	}

	public boolean stopCellEditing() {
		return true;
	}
}
