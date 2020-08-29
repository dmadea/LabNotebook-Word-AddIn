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
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.batch.PCeNSummaryViewControllerUtility;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewHeaderRenderer;
import com.chemistry.enotebook.domain.ProductPlate;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

//import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNTableModel;
//import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNTableView;
//import com.chemistry.enotebook.client.gui.page.experiment.table.ParallelCeNTableViewHeaderRenderer;
//import com.chemistry.enotebook.client.gui.page.experiment.table.TableViewMVConnector;

/**
 * Class to implement the table containing the list of plates in the CeN registration
 * and sumbission tab.
 * 
 * 
 *
 */
public class PCeNRegistration_SummaryTableView extends PCeNTableView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7925946520364061995L;
	private PCeNRegistration_SummaryTableViewCellEditor mPcenRegistrationSummaryTableViewCellEditor;//= new PcenPlateSummaryTableViewCellEditor();
	private PCeNRegistration_SummaryTableViewCellRender mPcenRegistrationSummaryTableViewCellRender = new PCeNRegistration_SummaryTableViewCellRender();
	private PCeNRegistration_SummaryViewControllerUtility connector;
	private PCeNTableModel model;
	
	/**
	 * Constructor
	 * @param model the table model
	 * @param rowHeight initial height of the rows
	 * @param connector the controller link between the model and the table view
	 */
	public PCeNRegistration_SummaryTableView(PCeNTableModel model, int rowHeight, PCeNTableModelConnector connector) {
		super(model, rowHeight, connector, null);
		this.model = model;
		this.connector = (PCeNRegistration_SummaryViewControllerUtility) connector;
		mPcenRegistrationSummaryTableViewCellEditor = new PCeNRegistration_SummaryTableViewCellEditor(this);
		setTableViewCellRenderers();

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					checkboxclicked(evt);
				}
			} // end mouseClicked

		});

	}
	
	public List getSelectedPlates() {
		return ((PCeNRegistration_SummaryViewControllerUtility) this.getConnector()).getSelectedPlates();
	}

	/**
	 * Handle the event when a row is selected.Vgfl2vctr
	 * @param evt
	 */
	private void checkboxclicked(MouseEvent evt) {
		String columnName = getColumnName(this.getSelectedColumn());
		if (columnName.equalsIgnoreCase("View")) {
			connector.setSelectValue(this.getSelectedRow());
			this.repaint();
		}
	}

	/**
	 * Set non-default table cell renderer to the ParallelCeNTableViewHeaderRenderer.
	 */
	protected void setTableViewCellRenderers() {
		PCeNTableViewHeaderRenderer headerRenderer = new PCeNTableViewHeaderRenderer();
		int colCount = getColumnCount();
		for (int i = 0; i < colCount; i++) {
			String name = getColumnName(i);
			TableColumn col = getColumn(getColumnName(i));
			if (name.equalsIgnoreCase(PCeNSummaryViewControllerUtility.GENERATE_BARCODE)) {
				col.setCellRenderer(new BarcodeButtonCellRenderer());
				col.setCellEditor(new BarcodeButtonCellEditor(this));
			} else if (name.equalsIgnoreCase("Plate Barcode"))
			{
				col.setCellRenderer(mPcenRegistrationSummaryTableViewCellRender);
				
				JTextField plateBarCodeField = new JTextField(12);
				plateBarCodeField.setDocument(new JTextFieldLimit(12));
				plateBarCodeField.addFocusListener(new PlateBarcodeFieldFocuser(plateBarCodeField, this));
				TableCellEditor plateBarcodeCellEditor = new DefaultCellEditor(plateBarCodeField);
				col.setCellEditor(plateBarcodeCellEditor);
			} 
      else if (name.equalsIgnoreCase(PLATE_COMMENTS) || name.equalsIgnoreCase(PCeNSummaryViewControllerUtility.PLATE_COMMENTS)) {

		      col.setCellRenderer(cellRenderer);
		      col.setCellEditor(super.getCellEditor());
//				col.setCellRenderer(mPcenRegistrationSummaryTableViewCellRender);
//				col.setCellEditor(super.getCellEditor());
			}
			else
			{
				col.setCellRenderer(mPcenRegistrationSummaryTableViewCellRender);
				col.setCellEditor(mPcenRegistrationSummaryTableViewCellEditor);
			}
			col.setHeaderRenderer(headerRenderer);
		}// end for
	}
	
	
	public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        int realColumnIndex = convertColumnIndexToModel(colIndex);
/*	        if (realColumnIndex == 10) { 
            tip = connector.getToolTip(rowIndex, colIndex);
        } else { 
*/	        	//tip = connector.getValue(rowIndex, colIndex) + "";
        //}
        tip = connector.getToolTip(rowIndex, colIndex);
        return tip;
    }		
	
	class BarcodeButtonCellEditor  extends DefaultCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = 9034682083640771570L;
		protected JButton button;
		private String label;
		private JTable mytable;
		
		public BarcodeButtonCellEditor(JTable table) {
			super(new JCheckBox());
			mytable = table;
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (button.getText().equals(""))
						return;
					int i = mytable.getEditingRow();
					if (connector instanceof PCeNRegistration_SummaryViewControllerUtility) {
						List<ProductPlate> plates = ((PCeNRegistration_SummaryViewControllerUtility) connector).getProductPlates();
						ProductPlate selectedPlate = plates.get(i);
						ArrayList<ProductPlate> singlePlateList = new ArrayList<ProductPlate>();
						singlePlateList.add(selectedPlate);
						((PCeNRegistration_SummaryViewControllerUtility) connector).getNewGBLPlateBarCodesFromCompoundManagement(singlePlateList);
						model.fireTableDataChanged();
					}
				}
			});
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			label = (value == null) ? "" : value.toString();
			button.setText("Generate Barcode");
			button.setEnabled(table.isCellEditable(row, column));
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

		public boolean isCellEditable(EventObject arg0) {
			return true;
		}

		public boolean shouldSelectCell(EventObject arg0) {
			return true;
		}

		public void addCellEditorListener(CellEditorListener arg0) {
		}

		public void removeCellEditorListener(CellEditorListener arg0) {
		}

		public boolean stopCellEditing() {
			return true;
		}		

	}

	class BarcodeButtonCellRenderer extends JButton implements TableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -9113240571173972465L;

		public BarcodeButtonCellRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(Color.WHITE);
				setForeground(Color.black);
			}
			CeNGUIUtils.styleComponentText(Font.BOLD, this);
			//setText((value == null) ? "" : value.toString());
			setText("Generate Barcode");
			setEnabled(table.isCellEditable(row, column));
			return this;
		}

		public String getToolTipText() {
			return "Generate plate barcode";
		}
	}
	
	class JTextFieldLimit extends PlainDocument {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 5420764697907993382L;
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
	

	class PlateBarcodeFieldFocuser implements FocusListener
	{
		//ProductPlate productPlate = null;
		JTextField plateBarCodeField = null;
		String oldBarcode = "";
		JTable table = null;
		int i = -1;
		PlateBarcodeFieldFocuser(JTextField plateBarCodeField, JTable table)
		{
			//this.productPlate = productPlate;
			this.plateBarCodeField = plateBarCodeField;
			this.table = table;
		}
		public void focusGained(FocusEvent arg0) {
			i = table.getEditingRow();
			oldBarcode = plateBarCodeField.getText();
		}
		public void focusLost(FocusEvent arg0) {
			if (i == -1) return;
			String newBarcode = plateBarCodeField.getText();
			ArrayList plates = (ArrayList) ((PCeNRegistration_SummaryViewControllerUtility) connector).getProductPlates();
			ProductPlate selectedPlate = (ProductPlate) plates.get(i);

			if (newBarcode.equals(oldBarcode))
				return;
			if (isBarcodeValid(newBarcode))
			{
				PCeNRegistration_SummaryViewControllerUtility connector = (PCeNRegistration_SummaryViewControllerUtility) getConnector();
				selectedPlate.setPlateBarCode(newBarcode);
				PCeNTableModel model = (PCeNTableModel) getModel();
				model.fireTableDataChanged();
				connector.getPageModel().setModelChanged(true);
				connector.enableSaveButton();
				plateBarCodeField.setText(newBarcode);
			}
			else
			{
				plateBarCodeField.setText(oldBarcode);
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Plate barcode must be 12 characters long.", "Invalid plate Barcode", JOptionPane.ERROR_MESSAGE);
			}
		}
		private boolean isBarcodeValid(String newBarcode) {
			if (newBarcode.length() > 0 && newBarcode.length() != 12)
				return false;
			else 
				return true;
		}
	}
}
