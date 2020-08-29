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
package com.chemistry.enotebook.client.gui.page.experiment.table;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.datamodel.common.Amount2;
import com.chemistry.enotebook.utils.CeNJTable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

/**
 * 
 * 
 *
 */
public class PAmountCellEditor extends PAmountComponent implements TableCellEditor, MouseListener {

	private static final long serialVersionUID = 1165571992113118427L;

	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	
	// This is the component that will handle the editing of the cell value
	protected transient List<CellEditorListener> listeners;

	public PAmountCellEditor() {
		super();
		setEditable(true);
		LineBorder border = new LineBorder(Color.black, 1);
		valueView.setBorder(border);
		valueView.addMouseListener(this);
		listeners = new Vector<CellEditorListener>();
		comboUnitView.addPopupMenuListener(this);
	}

	// Implementation of TableCellRenderer interface
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (!(value == null || table == null)) {
			setValue(value);
			setEditable(true);
		}
		return this;
	}

	public double getValue() throws NumberFormatException {
		return currentAmt.doubleValue();
	}

	public void setValue(Object value) {
			super.setValue(value);
	}

	public Object getCellEditorValue() {
		return currentAmt.deepClone();
	}

	// This method is called when editing is completed.
	// It must return the new value to be stored in the cell.
	public Object getCellEditorDisplayedValue() {
		Amount2 tmpAmt = null;
		try {
			if (comboUnitView.getSelectedItem() instanceof Unit2) {
				tmpAmt = new Amount2(getBigDecimal(valueView.getText()).doubleValue(), (Unit2) comboUnitView.getSelectedItem());
			} else {
				tmpAmt = new Amount2(getBigDecimal(valueView.getText()).doubleValue(), getUnit());
			}
		} catch (NumberFormatException e) {
			ceh.logInformationMsg(this, "AmountCellEditor.getCellEditorValue(): Value entered for an amount must be a valid number.");
		}
		return tmpAmt;
	}

	public boolean isCellEditable(EventObject eo) {
		boolean result = isEditable(eo);
		if (eo instanceof MouseEvent) {
			// translate point to cell coordinates.
			MouseEvent meo = (MouseEvent) eo;
			JTable table = (JTable) meo.getComponent();
			// JTableHeader th = table.getTableHeader();
			TableColumnModel tcm = table.getColumnModel();
			Point containerPoint = meo.getPoint();
			int curXTransform = (new Double(containerPoint.getX())).intValue();
			int curWidth = 0;
			// Find the column
			for (int i = 0; i < tcm.getColumnCount() && (curXTransform > curWidth); i++) {
				TableColumn tc = tcm.getColumn(i);
				curXTransform = curXTransform - curWidth;
				curWidth = tc.getWidth(); 
			}
			Component[] amtComps = comboUnitView.getComponents();
			int curComboButtonXPosition = curWidth - new Double(amtComps[0].getPreferredSize().getWidth()).intValue();
			if ((curXTransform - curComboButtonXPosition) > 0) {
				result = super.isEditable();
			}
		}
		return result;
	}

	public boolean shouldSelectCell(EventObject eo) {
		return true;
	}

	public void addCellEditorListener(CellEditorListener cel) {
		if (!listeners.contains(cel)) {
			listeners.add(cel);
		}
	}

	public void removeCellEditorListener(CellEditorListener cel) {
		listeners.remove(cel);
	}

	public void cancelCellEditing() {
		cancelEditing();
	}

	public boolean stopCellEditing() {
		return stopEditing();
	}

	protected void fireEditingCanceled() {
		debugState("fireEditingCanceled before resetting values");
		ChangeEvent ce = new ChangeEvent(this);
		debugState("fireEditingCanceled before notifying listeners");
		for (int i = listeners.size() - 1; i >= 0; i--) {
			((CellEditorListener) listeners.get(i)).editingCanceled(ce);
		}
		debugState("fireEditingCanceled after notifying listeners");
	}

	protected void fireEditingStopped() {
		ChangeEvent ce = new ChangeEvent(currentAmt);
		debugState("fireEditingStopped before notifying listeners");
		// TableModel is notified here.
		for (int i = listeners.size() - 1; i >= 0; i--) {
			((CellEditorListener) listeners.get(i)).editingStopped(ce);
		}
		debugState("fireEditingStopped after notifying listeners");
	}

	public void updateDisplay() {
		super.updateDisplay();
		refresh();
	}

	public void mouseClicked(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent evt) {
		if (evt.isPopupTrigger() && getParent() instanceof CeNJTable) {
			((CeNJTable) getParent()).mouseReleased(evt);
		}
	}

	public void requestFocusValueField() {
		valueView.requestFocusInWindow();		
	}
}
