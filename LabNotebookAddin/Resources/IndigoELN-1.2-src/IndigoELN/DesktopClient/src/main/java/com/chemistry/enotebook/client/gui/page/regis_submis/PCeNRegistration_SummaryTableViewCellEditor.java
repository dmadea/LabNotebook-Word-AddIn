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
package com.chemistry.enotebook.client.gui.page.regis_submis;


import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.ProductPlate;
import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

/**
 * 
 * 
 */
public class PCeNRegistration_SummaryTableViewCellEditor extends DefaultCellEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2527416468313298496L;

	private JTextField ta;

	private Color foreground = DefaultPreferences.getRowForegroundColor(); //new Color(30, 64, 124);
	private Color background = DefaultPreferences.getEditableRowBackgroundColor(); //new Color(255, 255, 245);
	private Color selectedForeground = DefaultPreferences.getSelectedRowForegroundColor(); //new Color(255, 255, 245);
	private Color selectedBackground = DefaultPreferences.getSelectedRowBackgroundColor(); //new Color(100, 100, 100);

	
	private JPanel comboPanel = new JPanel();
	//private StringCellEditor strEditor;
	//private AmountCellEditor amtEditor;
	private String[] rackPlateFlagValues = new String[] { "Intermediate", "Product", "By-Product" };
	//private String[] selectFlagValues = new String[] { "Select", "Not" };
	private JComboBox combo = new JComboBox(rackPlateFlagValues);
	private Component editingComponent;

	private PCeNTableView summaryTableView;

	/**
	 * Constructor sets the string editor and the text component
	 *
	 */
	public PCeNRegistration_SummaryTableViewCellEditor(PCeNTableView ceNRegistration_SummaryTableView) {
		super(new JTextField());
		ta = (JTextField) super.getComponent();
		//this.plateDetailsViewer = plateDetailsViewer;
		//strEditor = new StringCellEditor();
		//amtEditor = new AmountCellEditor();
		this.summaryTableView = ceNRegistration_SummaryTableView;
		setup();
		//super.setClickCountToStart(2);
	}

	private void setup() {
		comboPanel.add(combo);
		comboPanel.setForeground(foreground);
		comboPanel.setBackground(background);
	}

	/**
	 * Implement the one method defined by TableCellEditor.
	 */
	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, int row, int column) {
		if (value == null)
			value = "";
		String coluName = table.getColumnName(column);
		if (coluName.equalsIgnoreCase("Product Plate Flag") && value instanceof String) {
			if (isSelected) {
				combo.setForeground(selectedForeground);
				combo.setBackground(selectedBackground);
			} else {
				combo.setForeground(foreground);
				combo.setBackground(background);
			}
			editingComponent = combo;
			return combo;

		}//else if (coluName.equalsIgnoreCase("Select to View")) {
		else if (coluName.equalsIgnoreCase("View")) {
			Boolean checked = (Boolean) value;
			JCheckBox check = new JCheckBox();
			JPanel chkBoxPanel = new JPanel();
			chkBoxPanel.add(check);
			if (isSelected) {
				chkBoxPanel.setForeground(selectedForeground);
				chkBoxPanel.setBackground(selectedBackground);
			} else {
				chkBoxPanel.setForeground(foreground);
				chkBoxPanel.setBackground(background);
			}
			check.setSelected(checked.booleanValue());
			editingComponent = check;
			return chkBoxPanel;
		} 
		/*else if (coluName.equalsIgnoreCase("Plate Barcode")) {
			ProductPlate productPlate = (ProductPlate)value;
			JTextField plateBarCodeField = new JTextField(12);
			plateBarCodeField.setDocument(new JTextFieldLimit(12));
			plateBarCodeField.addFocusListener(new PlateBarcodeFieldFocuser(productPlate, plateBarCodeField));
			JButton generateBarCodeButton = new JButton("Generate");
			generateBarCodeButton.addActionListener(new PlateBarcodeGenerator(productPlate, plateBarCodeField));
			JPanel plateBarCodePanel = new JPanel();
			plateBarCodePanel.setLayout(new GridLayout(2,1));
			plateBarCodePanel.add(plateBarCodeField);
			plateBarCodePanel.add(generateBarCodeButton);
			if (isSelected) {
				plateBarCodePanel.setForeground(selectedForeground);
				plateBarCodePanel.setBackground(selectedBackground);
			} 
			else {
				plateBarCodePanel.setForeground(foreground);
				plateBarCodePanel.setBackground(background);
			}
			plateBarCodeField.setText(productPlate.getPlateBarCode());
			if (value instanceof PseudoProductPlate)
			{
				plateBarCodeField.setEnabled(false);
				generateBarCodeButton.setEnabled(false);
			}
			else if (productPlate.getContainer().isUserDefined())
				generateBarCodeButton.setEnabled(false);
			editingComponent = plateBarCodePanel;
			return plateBarCodePanel;
			}*/
			else {
				return super.getComponent();
		}
	}

	/**
	 * 
	 */
	public Object getCellEditorValue() {
		if (editingComponent instanceof JTextField) {
			return ((JTextField) editingComponent).getText();
			
		} else if (editingComponent instanceof JCheckBox) {
			return new Boolean(((JCheckBox) editingComponent).isSelected());

		} else if (editingComponent instanceof JComboBox) {
			return ((JComboBox) editingComponent).getSelectedItem();
		}
		return "uuu";
	}

	/**
	 * 
	 *
	 */
/*	private class StringCellEditor extends DefaultCellEditor {
		private StringCellEditor() {
			super(ta);
			ta.removeActionListener(delegate);
			delegate = new EditorDelegate() {
				public void setValue(Object value) {
					ta.setText((String) value);
				}

				public Object getCellEditorValue() {
					return ta.getText();
				}
			};
			ta.addActionListener(delegate);
			ta.setBorder(null);
			// ta.addKeyListener(l);
		}
	}*/

	class PlateBarcodeGenerator implements ActionListener
	{
		ProductPlate productPlate = null;
		JTextField plateBarCodeField = null;
		PlateBarcodeGenerator(ProductPlate productPlate, JTextField plateBarCodeField)
		{
			this.productPlate = productPlate; 
			this.plateBarCodeField = plateBarCodeField;
		}
		public void actionPerformed(ActionEvent event)
		{
			PCeNRegistration_SummaryViewControllerUtility connector = (PCeNRegistration_SummaryViewControllerUtility) summaryTableView.getConnector();
			ArrayList list = new ArrayList();
			list.add(productPlate);
			connector.getNewGBLPlateBarCodesFromCompoundManagement(list);
			plateBarCodeField.setText(productPlate.getPlateBarCode());
			PCeNTableModel model = (PCeNTableModel) summaryTableView.getModel();
			model.fireTableDataChanged();
			connector.getPageModel().setModelChanged(true);
		}
	}
	
	class PlateBarcodeFieldFocuser implements FocusListener
	{
		ProductPlate productPlate = null;
		JTextField plateBarCodeField = null;
		String oldBarcode = "";
		PlateBarcodeFieldFocuser(ProductPlate productPlate, JTextField plateBarCodeField)
		{
			this.productPlate = productPlate;
			this.plateBarCodeField = plateBarCodeField;
		}
		public void focusGained(FocusEvent arg0) {
			oldBarcode = plateBarCodeField.getText();
		}
		public void focusLost(FocusEvent arg0) {
			String newBarcode = plateBarCodeField.getText();
			if (newBarcode.equals(oldBarcode))
				return;
			if (isBarcodeValid(newBarcode))
			{
				PCeNRegistration_SummaryViewControllerUtility connector = (PCeNRegistration_SummaryViewControllerUtility) summaryTableView.getConnector();
				productPlate.setPlateBarCode(newBarcode);
				PCeNTableModel model = (PCeNTableModel) summaryTableView.getModel();
				model.fireTableDataChanged();
				connector.getPageModel().setModelChanged(true);
				plateBarCodeField.setText(newBarcode);
			}
			else
			{
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Plate barcode must be 12 characters long.", "Invalid plate Barcode", JOptionPane.ERROR_MESSAGE);
				plateBarCodeField.setText(oldBarcode);
			}
		}
		private boolean isBarcodeValid(String newBarcode) {
			if (newBarcode.length() > 0 && newBarcode.length() != 12)
				return false;
			else 
				return true;
		}
	}
	
	class JTextFieldLimit extends PlainDocument {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1738695040018580160L;
		private int limit;
	    // optional uppercase conversion
	    private boolean toUppercase = false;
	    
	    JTextFieldLimit(int limit) {
	        super();
	        this.limit = limit;
	    }
	    
	    JTextFieldLimit(int limit, boolean upper) {
	        super();
	        this.limit = limit;
	        toUppercase = upper;
	    }
	    
	    public void insertString
	            (int offset, String  str, AttributeSet attr)
	            throws BadLocationException {
	        if (str == null) return;
	        
	        if ((getLength() + str.length()) <= limit) {
	            if (toUppercase) str = str.toUpperCase();
	            super.insertString(offset, str, attr);
	        }
	    }
	}

}
