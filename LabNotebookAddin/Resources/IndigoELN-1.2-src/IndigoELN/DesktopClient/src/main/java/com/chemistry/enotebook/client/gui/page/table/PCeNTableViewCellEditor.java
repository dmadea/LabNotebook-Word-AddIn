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
/**
 * 
 */
package com.chemistry.enotebook.client.gui.page.table;

//import java.awt.BorderLayout;

import com.chemistry.enotebook.client.gui.common.utils.*;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.AnalyticalDetailTableView;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.experiment.table.RackFlagType;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.chemistry.enotebook.utils.DefaultPreferences;
import com.chemistry.enotebook.utils.MonomerBatchStatusMapper;
import com.chemistry.enotebook.utils.ProductBatchStatusMapper;
import com.common.chemistry.codetable.CodeTableCache;
import com.common.chemistry.codetable.CodeTableCacheException;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseEvent;
//import java.util.EventObject;
//import java.util.Vector;
//import javax.swing.CellEditor;
//import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
//import com.chemistry.enotebook.experiment.datamodel.common.Amount;
//import com.chemistry.enotebook.utils.CodeTableUtils;

/**
 * 
 * 
 */
public class PCeNTableViewCellEditor extends DefaultCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8147436455162534793L;
	private JTextField ta;
	//private JCheckBox check = new JCheckBox();
	private JComboBox combo = new JComboBox();


	private Color foreground = DefaultPreferences.getRowForegroundColor(); //new Color(30, 64, 124);
	private Color background = DefaultPreferences.getEditableRowBackgroundColor(); //new Color(255, 255, 245);
	private Color nonEditableBackground = DefaultPreferences.getNonEditableRowBackgroundColor(); //new Color(255, 255, 245);
	private Color selectedForeground = DefaultPreferences.getSelectedRowForegroundColor(); //new Color(255, 255, 245);
	private Color selectedBackground = DefaultPreferences.getSelectedRowBackgroundColor(); //new Color(100, 100, 100);

	JPanel chkBoxPanel = new JPanel();
	JPanel comboPanel = new JPanel();
	StringCellEditor strEditor;
	AmountCellEditor amtEditor;
	CeNTableCellAlignedComponent componentPanel;
	
	private BatchAttributeComponentUtility batchUtility = BatchAttributeComponentUtility.getInstance();
	private Object renderer; 
	
	private RowNumber currentRow = new RowNumber();
	private RowNumber previousRow = new RowNumber();

	public PCeNTableViewCellEditor() {
		super(new JTextField());
		ta = (JTextField) super.getComponent();
		strEditor = new StringCellEditor();
		amtEditor = new AmountCellEditor();
		//BorderLayout border =new BorderLayout();
		//saltCodeComboPanel.setLayout(border);
	
		super.setClickCountToStart(2);
	}

	public PCeNTableViewCellEditor(JComponent component) {
		this();
		componentPanel = new CeNTableCellAlignedComponent(component);
		componentPanel.setBackground(background);
	}
	
	public PCeNTableViewCellEditor(CeNComboBox ceNComboBox,	TableCellRenderer renderer) {
		this(ceNComboBox);
		this.renderer = renderer;
	}

	// Implement the one method defined by TableCellEditor.
	public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, final int row, final int column) {
		if (value == null)
			value = "";
		//String coluName = table.getColumnName(column);
		// if (collection.getAnnotations().contains(coluName)) {
	
		if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.COMPOUND_STATE)) {
			JComboBox comboBox = (JComboBox) componentPanel.getCellInnerComponent(); 
			CodeTableUtils.fillComboBoxWithCompoundStates(comboBox);
			comboBox.insertItemAt("", 0);
			List li = null;
			try {
				li = CodeTableCache.getInstance().getCompoundStates();
			} catch (CodeTableCacheException e) {
				e.printStackTrace();
			}
			if (li != null)
			{
				value = BatchAttributeComponentUtility.getCodeAndDesc(value.toString(), (ArrayList)li, CodeTableCache.COMPOUND_STATE__COMPOUND_STATE_CODE, CodeTableCache.COMPOUND_STATE__COMPOUND_STATE_DESC);
				comboBox.setSelectedItem(value);
			}
			comboBox.addActionListener(new ChangeActionListener());
			return componentPanel;
		}
		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.SALT_CODE)) {
			PCeNTableView cenTable = (PCeNTableView) table;
			StoicModelInterface productBatchModel = cenTable.getBatchAt(row);
			if (productBatchModel != null) {
				{
					String code = productBatchModel.getStoicBatchSaltForm().getCode();
					String desc = (code.equals("00"))? "Parent Structure" : productBatchModel.getStoicBatchSaltForm().getDescription();
					JComboBox comboBox = (JComboBox) componentPanel.getCellInnerComponent();
					CodeTableUtils.fillComboBoxWithSaltCodes(comboBox);
					comboBox.setSelectedItem(code + " - " + desc);
					//comboBox.addActionListener(new ChangeActionListener());
				}
			}
			return componentPanel;
		}
		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.STATUS)) {
			JComboBox comboBox = (JComboBox) componentPanel.getCellInnerComponent();
			if (renderer != null)
				comboBox.setBackground(((CeNStatusComboBoxRenderer)renderer).getAssociatedComboBox().getBackground());
			BatchModel batchModel = null;
			if (table instanceof PCeNTableView) {
				PCeNTableView cenTable = (PCeNTableView) table;
				batchModel = cenTable.getBatchAt(row);
			} else if (table instanceof AnalyticalDetailTableView) {
				AnalyticalDetailTableView tableView = (AnalyticalDetailTableView) table;
				batchModel = tableView.getBatchAt(row);
			}
			if (batchModel != null && comboBox.getItemCount() == 0) {
				if (batchModel instanceof MonomerBatchModel)
					MonomerBatchStatusMapper.getInstance().fillComboBox(comboBox);
				else
					ProductBatchStatusMapper.getInstance().fillComboBox(comboBox);
			}
			// vb 10/21 to save selected item without waiting for user to click on another cell
			comboBox.addActionListener(new ChangeActionListener());
			comboBox.setSelectedItem(value);
			return componentPanel;
		}
		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.WELL_SOLVENT)) {
			JComboBox comboBox = (JComboBox) componentPanel.getCellInnerComponent();
			CodeTableUtils.fillComboBoxWithSolvents(comboBox);
			comboBox.setSelectedItem(value);
			return componentPanel;
		}

		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.SOURCE))
		{
			JComboBox comboBox = (JComboBox) componentPanel.getCellInnerComponent(); 
			batchUtility.fillCompoundSourceComboBox(comboBox);
			if (comboBox.getItemAt(0) != null)
				comboBox.insertItemAt(null, 0);
			if (comboBox.getItemListeners() == null)
			{
				comboBox.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent evt) {
							CeNTableCellAlignedComponentRenderer renderer =  (CeNTableCellAlignedComponentRenderer) table.getCellRenderer(row, (column + 1));
							JComboBox sourceDetailComboBox = (JComboBox) renderer.getCellInnerComponent();
							PCeNTableView cenTable = (PCeNTableView) table;
							ProductBatchModel productBatchModel = (ProductBatchModel) cenTable.getBatchAt(row);
							sourceDetailComboBox.setSelectedIndex(0);
							productBatchModel.getRegInfo().setCompoundSourceDetail("");
							if (productBatchModel != null) {
								batchUtility.updateComboSourceDetail((productBatchModel).getRegInfo().getCompoundSource(), sourceDetailComboBox, (productBatchModel).getRegInfo().getCompoundSourceDetail());
							}
							((PCeNTableModel)cenTable.getModel()).fireTableDataChanged();
							//batchUtility.fillSourceDetailComboBox(comboBoxCompoundSource, comboBoxSourceDetail);
						}
				});
			}

			//value = batchUtility.getCompoundSourceDisplayValue(value.toString());
			if (value.toString().equals(""))
				comboBox.setSelectedItem(null);
			else
				comboBox.setSelectedItem(value);
			comboBox.addActionListener(new ChangeActionListener());
			return componentPanel;
		}
		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.SOURCE_DETAILS))
		{
			JComboBox comboBox = (JComboBox) componentPanel.getCellInnerComponent();
			PCeNTableView cenTable = (PCeNTableView) table;
			ProductBatchModel productBatchModel = (ProductBatchModel) cenTable.getBatchAt(row);
			if (productBatchModel != null) {
				batchUtility.updateComboSourceDetail((productBatchModel).getRegInfo().getCompoundSource(), comboBox, (productBatchModel).getRegInfo().getCompoundSourceDetail());
			}
			comboBox.addActionListener(new ChangeActionListener());
			return componentPanel;
		}
		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.COMMENTS))
		{
			JTextArea textArea = (JTextArea) componentPanel.getCellInnerComponent();
			textArea.setText(value.toString());
			return textArea;
		}
		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.STRUCTURE_COMMENTS))
		{
			JTextArea textArea = (JTextArea) componentPanel.getCellInnerComponent();
			textArea.setText(value.toString());
			return textArea;
		}
		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.NBK_BATCH_NUM))
		{
			JTextField textField = (JTextField) componentPanel.getCellInnerComponent();
			textField.setText(value.toString());
			return componentPanel;
		} 
		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.DELIVERED_MONOMER))
		{
			JTextField textField = (JTextField) componentPanel.getCellInnerComponent();
			textField.setText(value.toString());
			return componentPanel;
		} 
		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.DELIVERED_MONOMER))
		{
			JTextField textField = (JTextField) componentPanel.getCellInnerComponent();
			textField.setText(value.toString());
			return componentPanel;
		} 
		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.BATCH_ANALYTICAL_COMMENTS))
		{
			JTextArea textArea = (JTextArea) componentPanel.getCellInnerComponent();
			textArea.setText(value.toString());
			return textArea;
		} 
		else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.COMPOUND_PROTECTION))
		{
			JComboBox comboBox = (JComboBox) componentPanel.getCellInnerComponent();
			PCeNTableView cenTable = (PCeNTableView) table;
			batchUtility.fillProtectionCodesComboBox(comboBox);
			ProductBatchModel productBatchModel = (ProductBatchModel) cenTable.getBatchAt(row);
			if (productBatchModel != null) {
				String code = productBatchModel.getProtectionCode();
				if (code == null || code.equals(""))
					comboBox.setSelectedIndex(0);//Select the None option
				else
					comboBox.setSelectedItem(BatchAttributeComponentUtility.getCompoundProtectionCodeAndDesc(code));
			}
			comboBox.addActionListener(new ChangeActionListener());
			return componentPanel;
		} else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.BARCODE)) {
			JTextField textField = (JTextField) componentPanel.getCellInnerComponent();
			textField.setText(value.toString());
			return componentPanel;
		} else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.STEREOISOMER)) {
            int selectedRow = table.getSelectedRow();
            ProductBatchModel model = ((ProductBatchModel)((PCeNTableModel)table.getModel()).getConnector().getBatchModel(selectedRow));
            final NotebookPageModel pageModel = ((PCeNTableView) table).getPageModel();
			final StereoIsomerCodeControl control = PCeNTableViewCellRenderer.createStereoIsomerCodeControl(model, pageModel);
			componentPanel = new CeNTableCellAlignedComponent(control);
			componentPanel.setOpaque(false);
			if (isSelected) {
				componentPanel.setForeground(selectedForeground);
				componentPanel.setBackground(selectedBackground);
			} else {
				componentPanel.setForeground(foreground);
				componentPanel.setBackground(background);
			}
			return componentPanel;
		} else if (table.getColumnName(column).equalsIgnoreCase(PCeNTableView.PRECURSORS)) {
			int selectedRow = table.getSelectedRow();
            ProductBatchModel model = ((ProductBatchModel)((PCeNTableModel)table.getModel()).getConnector().getBatchModel(selectedRow));
            
			JTextField cellComp = new JTextField();
			cellComp.setHorizontalAlignment(SwingConstants.LEFT);
			cellComp.setText(value.toString());
			cellComp.addActionListener(new PrecursorsChangeActionListener(table, model));
			return cellComp; 
		} else if (value instanceof String) {
			String strval = (String) value;
			JTextField cellComp = (JTextField) strEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
			cellComp.setHorizontalAlignment(SwingConstants.CENTER);
			cellComp.setText(strval);
			cellComp.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					PCeNTableView cenTable = (PCeNTableView) table;
					cenTable.valueChanged();
				}
				public void focusLost(FocusEvent e) {
				}
			});
			//cellComp.setCaretPosition(0);
			//cellComp.moveCaretPosition(cellComp.getDocument().getLength());
			//cellComp.setSelectionStart(0);
			//cellComp.setSelectionEnd(cellComp.getCaretPosition());
//			if (isSelected && !cellComp.hasFocus()) {
//				cellComp.setForeground(selectedForeground);
//				cellComp.setBackground(selectedBackground);
//			} else if (cellComp.hasFocus()) {
//				cellComp.setForeground(Color.BLACK);
//				cellComp.setBackground(Color.WHITE);
//			}
			return cellComp;
		} else if (value instanceof AmountModel) {
			//return new JPanel().add(amtEditor.getTableCellEditorComponent(table, value, isSelected, row, column));
			
			AmountModel mAmountModel =  (AmountModel)value;
			double va = mAmountModel.GetValueInStdUnitsAsDouble();
			String v = ""+va;
			JTextField cellComp = (JTextField) strEditor.getTableCellEditorComponent(table, v, isSelected, row, column);
			cellComp.setText(v);
			
			if (isSelected && !cellComp.hasFocus()) {
				cellComp.setForeground(selectedForeground);
				cellComp.setBackground(selectedBackground);
			} else if (cellComp.hasFocus()) {
				cellComp.setForeground(Color.BLACK);
				cellComp.setBackground(Color.WHITE);
			}
			return cellComp;
		
		} else if (value instanceof Boolean) {
			Boolean checked;
			JCheckBox check  = null;
			if(table.getColumnName(column).equalsIgnoreCase(PCeNTableView.SELECT_OPTION)) {
				return null;
			}

			checked = (Boolean) value;
			chkBoxPanel.setForeground(foreground);
			chkBoxPanel.setBackground(nonEditableBackground);//all other tables have non-editable background for check box.
			if (chkBoxPanel.getComponentCount() == 0) {
				check = new JCheckBox();
				chkBoxPanel.add(check);
			} else {
				check = (JCheckBox) chkBoxPanel.getComponent(0);
			}
			check.setSelected(checked.booleanValue());
			return chkBoxPanel;
		} else if (value instanceof RackFlagType) {
			RackFlagType mRackFlagType = (RackFlagType) value;
			((DefaultComboBoxModel) combo.getModel()).addElement("TEST ONE");
			((DefaultComboBoxModel) combo.getModel()).addElement("TEST TWO");
			// mRackFlagType.getRackPlateFlagValues()
			combo.setSelectedIndex(mRackFlagType.getSelected());
			comboPanel.add(combo);
			comboPanel.setForeground(foreground);
			comboPanel.setBackground(background);
			return comboPanel;
		} else
			return null;
		// }
	}

    private String convertToNotNull(String code) {
        if (code == null)
            code = "";
        return code;
    }

    public Object getCellEditorValue() {
		 JComponent component = null;
		 if (componentPanel != null)
		 {
			 component = componentPanel.getCellInnerComponent();

             if (component instanceof StereoIsomerCodeControl)
             {
                 StereoIsomerCodeControl control = (StereoIsomerCodeControl) component;
                 return control.getValue();
             }

			 if (component instanceof JComboBox)
			 {
				 JComboBox comboBox = (JComboBox) component;
				 return comboBox.getSelectedItem();
			 }
			 else if (component instanceof JTextArea)
			 {
				 JTextArea textArea = (JTextArea) component;
				 return textArea.getText();
			 }
			 else if (component instanceof JTextField)
			 {
				 JTextField textField = (JTextField) component;
				 return textField.getText();
			 }
			 else
				 return "";
		 }
		 else
			 return "";
	 }
	private class StringCellEditor extends DefaultCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2379505219649482680L;

		private StringCellEditor() {
			super(ta);
			ta.removeActionListener(delegate);
			delegate = new EditorDelegate() {
				/**
				 * 
				 */
				private static final long serialVersionUID = -2751445039442726484L;

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
	}
	
	/**
	 * vb 10/21/08 Trigger event to save the selected item if it has changed without waiting
	 * for user to click on another table cell.
	 */
	private class ChangeActionListener implements ActionListener {
		String oldItem = "";
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
			String selectedItem = (String) cb.getSelectedItem();
			if (selectedItem != null && !selectedItem.equals(oldItem)) {
				oldItem = selectedItem;
				fireEditingStopped();
			}
		}
	}
	
	private class RowNumber {
		private int rowNumber = -1;
		
		public int getRowNumber() {
			return rowNumber;
		}
		
		public void setRowNumber(int rowNumber) {
			this.rowNumber = rowNumber;
		}
	}
	
	private class PrecursorsChangeActionListener implements ActionListener {
		private JTable table;
		private ProductBatchModel batchModel;
		
		public PrecursorsChangeActionListener(JTable table, ProductBatchModel batchModel) {
			this.table = table;
			this.batchModel = batchModel;
		}
		
		public void actionPerformed(ActionEvent e) {
			NotebookPageModel pageModel = ((PCeNTableView) table).getPageModel();			
			JTextField cb = (JTextField) e.getSource();			
			String text = (String) cb.getText();
			pageModel.processSingletonPrecursorsUpdate(text, batchModel);
			fireEditingStopped();
			table.repaint();
		}
	}
}
