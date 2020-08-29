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
package com.chemistry.enotebook.client.gui.page.table.amount;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.experiment.common.units.Unit;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;
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
import java.util.Vector;

/**
 * 
 * 
 *
 */
public class AmountCellEditor extends AmountComponent implements TableCellEditor, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5678569245501404668L;
	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	// This is the component that will handle the editing of the cell value
	protected transient Vector listeners;

	public AmountCellEditor() {
		super();
		setEditable(true);
		LineBorder border = new LineBorder(Color.black, 1);
		valueView.setBorder(border);
		valueView.addMouseListener(this);
		listeners = new Vector();
		comboUnitView.addPopupMenuListener(this);
	}

	// Implementation of TableCellRenderer interface
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (!(value == null || table == null)) {
			if (debug) // System.out.println("AmountCellEditor.getTableCellEditorComponent():
				// calling setValue()");
				setValue(value);
			setEditable(true);
		}
		return this;
	}

	public double getValue() throws NumberFormatException {
		// if (debug) //System.out.println("AmountCellEditor.getValue()
		// currentAmt.getValue() = " + currentAmt.doubleValue());
		return currentAmt.doubleValue();
	}

	public void setValue(Object value) {
		if (debug) // System.out.println("AmountCellEditor.setValue(Object)
			// setting object of type: " + value.getClass());
			// //System.out.println("inEditor--sigfigs--:"+((Amount)value).getSigDigits());
			super.setValue(value);
	}

	public Object getCellEditorValue() {
		// //System.out.println("getCellEditorValue---:"+currentAmt.getSigDigits());
		return currentAmt.deepClone();
	}

	// This method is called when editing is completed.
	// It must return the new value to be stored in the cell.
	public Object getCellEditorDisplayedValue() {
		Amount tmpAmt = null;
		try {
			if (comboUnitView.getSelectedItem() instanceof Unit)
				tmpAmt = new Amount(getBigDecimal(valueView.getText()).doubleValue(), (Unit) comboUnitView.getSelectedItem());
			else
				tmpAmt = new Amount(getBigDecimal(valueView.getText()).doubleValue(), getUnit());
		} catch (NumberFormatException e) {
			ceh.logInformationMsg(this,
					"AmountCellEditor.getCellEditorValue(): Value entered for an amount must be a valid number.");
		}
		// //System.out.println("TempAmt---Editor--:"+tmpAmt);
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
				curWidth = tc.getWidth(); // + tcm.getColumnMargin();
			}
			Component[] amtComps = comboUnitView.getComponents();
			int curComboButtonXPosition = curWidth - new Double(amtComps[0].getPreferredSize().getWidth()).intValue();
			// //System.out.println("curComboButtonXPosition = " +
			// curComboButtonXPosition);
			if ((curXTransform - curComboButtonXPosition) > 0)
				result = super.isEditable();
		}
		return result;
	}

	public boolean shouldSelectCell(EventObject eo) {
		return true;
	}

	public void addCellEditorListener(CellEditorListener cel) {
		if (!listeners.contains(cel))
			listeners.addElement(cel);
		// //System.out.println("Listeners*** "+listeners.size());
	}

	public void removeCellEditorListener(CellEditorListener cel) {
		listeners.removeElement(cel);
	}

	public void cancelCellEditing() {
		cancelEditing();
	}

	public boolean stopCellEditing() {
		return stopEditing();
		// return true;
	}

	protected void fireEditingCanceled() {
		debugState("fireEditingCanceled before resetting values");
		ChangeEvent ce = new ChangeEvent(this);
		debugState("fireEditingCanceled before notifying listeners");
		for (int i = listeners.size() - 1; i >= 0; i--)
			((CellEditorListener) listeners.elementAt(i)).editingCanceled(ce);
		debugState("fireEditingCanceled after notifying listeners");
	}

	protected void fireEditingStopped() {
		ChangeEvent ce = new ChangeEvent(currentAmt);
		debugState("fireEditingStopped before notifying listeners");
		// TableModel is notified here.
		for (int i = listeners.size() - 1; i >= 0; i--) {
			((CellEditorListener) listeners.elementAt(i)).editingStopped(ce);
		}
		debugState("fireEditingStopped after notifying listeners");
	}

	protected void debugState(String source) {
		// TODO: change to use ceh.debugInfo
		if (debug) {
			Amount newAmount = (Amount) getCellEditorValue();
			// System.out.println("AmountCellEditor.debugState() calling
			// super.debugState( " + source + " )");
			super.debugState(source);
			// System.out.println("AmountCellEditor.debugState(): oldAmount
			// value = " + currentAmt.getValue() + " unit = " +
			// currentAmt.getUnit().getCode());
			// System.out.println("AmountCellEditor.debugState(): exiting
			// debugState()");
		}
	}

	public void updateDisplay() {
		super.updateDisplay();
		// valueView.setText(currentAmt.getValue());
		refresh();
	}

	//
	// Mouse Listener
	//
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void mouseReleased(MouseEvent evt) {
		if (debug) // System.out.println("AmountCellEditor mouseReleased:
			// getParent() = " + getParent());
			if (evt.isPopupTrigger() && getParent() instanceof CeNJTable) {
				((CeNJTable) getParent()).mouseReleased(evt);
			}
	}
}