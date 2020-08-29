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

import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewPopupMenuManager;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.virtuan.plateVisualizer.BatchPropertyFrame;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MedChemStoic_ProductsTableView extends PCeNTableView {

	private static final long serialVersionUID = 6539365978796802047L;
	
	private BatchPropertyFrame frame = new BatchPropertyFrame();
	private StoicModelInterface batchModel;
	private ReactionStepModel stepModel;
	/**
	 * Constructor
	 * 
	 * @param model
	 *            the table model
	 * @param rowHeight
	 *            initial height of the rows
	 * @param connector
	 *            the controller link between the model and the table view
	 */
	public MedChemStoic_ProductsTableView(PCeNTableModel model, 
	                                      int rowHeight, 
	                                      PCeNTableModelConnector mvconnector,
	                                      PCeNTableViewPopupMenuManager tableViewPopupMenuManager,
	                                      ReactionStepModel mstepModel)
	{
		super();
		connector = mvconnector;
		stepModel = mstepModel;
		mTableViewPopupMenuManager = tableViewPopupMenuManager;

		if (mTableViewPopupMenuManager != null)
			mTableViewPopupMenuManager.addMouseListener(this);
		this.setModel(model);
		ROW_HEIGHT = rowHeight;
		CodeTableUtils.fillComboBoxWithSaltCodes(jComboBoxSaltCd);
		// saltCodeComboPanel.add(jComboBoxSaltCd);
		// reagentTypePanel.add(jComboBoxReagentType);
		this.setAutoResizeMode(AUTO_RESIZE_OFF);
		setAutoCreateColumnsFromModel(true);
		this.setSize(new Dimension(650, 400));

		this.setRowHeight(ROW_HEIGHT);
		// this.setSelectionModel(createDefaultSelectionModel());
		this.setColumnSelectionAllowed(false);
		this.setCellSelectionEnabled(false);
		this.setRowSelectionAllowed(true);
		// this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					checkboxclicked(evt);
				}
			} // end mouseClicked

			public void mouseExited(MouseEvent event) {
				setToolTipBatchModel(null);
				hideDisplayStructureProperties();
			}
		});
		setTableViewCellRenderers();
		customizeTableHeader();
	}

	/**
	 * Handle the event when a row is selected.Vgfl2vctr
	 * 
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
		// setDefaultRenderer(java.lang.Number.class, new FractionCellRenderer(10, 6, SwingConstants.RIGHT));
		MedChemStoic_ProductsTableViewCellRenderer cellRenderer = new MedChemStoic_ProductsTableViewCellRenderer();
		MedChemStoic_ProductsTableViewCellEditor cellEditor = new MedChemStoic_ProductsTableViewCellEditor();
		int colCount = getColumnCount();
		for (int i = 0; i < colCount; i++) {
			String name = getColumnName(i);
			TableColumn col = getColumn(name);
			// Set all amount column renderers
			if (name.equals(MedChemStoic_ProductsTableControllerUtility.SALT_EQUIVS_COL) ||
					name.equals(MedChemStoic_ProductsTableControllerUtility.EXACT_MASS_COL) ||
					name.equals(MedChemStoic_ProductsTableControllerUtility.MOL_WEIGHT_COL) ||
					name.equals(MedChemStoic_ProductsTableControllerUtility.NUM_EQUIV_COL)) {
				col.setCellEditor(new PAmountCellEditor());
				col.setCellRenderer(new PAmountCellRenderer());
			} else if (name.equals(MedChemStoic_ProductsTableControllerUtility.THEO_MOLES_COL) || 
					name.equals(MedChemStoic_ProductsTableControllerUtility.THEO_WEIGHT_COL)) {
				PAmountCellEditorEx editor = new PAmountCellEditorEx();
				col.setCellEditor(editor);
				PAmountCellRenderer renderer = new PAmountCellRenderer();
				col.setCellRenderer(renderer);
			} else if (name.equalsIgnoreCase(MedChemStoic_ProductsTableControllerUtility.SALT_CODE_COL)) {
				col.setCellEditor(saltCdeditor);
				col.setCellRenderer(cellRenderer);
			} else {				
				col.setCellEditor(cellEditor);
				col.setCellRenderer(cellRenderer);
			}
		}// end for
	}

	private void customizeTableHeader() {
		JTableHeader th = getTableHeader();
		Font ft = th.getFont();
		if (ft.getStyle() != Font.BOLD) {
			th.setFont(new Font(th.getFont().getFontName(), Font.BOLD, th.getFont().getSize()));
		}
	}

	public JToolTip createToolTip() {
		if (batchModel != null) {
			displayStructureProperties();
		} else {
			hideDisplayStructureProperties();
		}
		return super.createToolTip();
	}

	public void setToolTipBatchModel(StoicModelInterface batchModel) {
		if(batchModel != null) {
			if(batchModel.equals(this.batchModel)) {
				this.batchModel = batchModel;			
			} else {
				//hideDisplayStructureProperties();
				this.batchModel = batchModel;
				//displayStructureProperties();
			}
		} else {
			this.batchModel = batchModel;			
			//hideDisplayStructureProperties();			
		}
	}

	protected void hideDisplayStructureProperties() {
		if (frame.isVisible())
			frame.setVisible(false);
	}

	protected void displayStructureProperties() {
		BatchModel bmodel = null;
		if (batchModel != null && batchModel.getStoichCompoundModel() != null) {
			String id = "";
			if (batchModel instanceof ProductBatchModel) {
				id = batchModel.getStoichCompoundModel().getCompoundName();
				if (id == null || ((String) id).length() == 0)
					id = "P" + batchModel.getStoicTransactionOrder();
				bmodel = (ProductBatchModel)batchModel;
			} else {
				id = batchModel.getStoicCompoundId();
				bmodel = (MonomerBatchModel)batchModel;
			}
			frame.displayBatchProperties(bmodel, id);
			frame.setLocation((int) this.getLocationOnScreen().getX(), (int) this.getLocationOnScreen().getY() - 225);
			frame.setVisible(true);
		}
	}

	public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
		Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
		List<ProductBatchModel>  prodList = stepModel.getAllIntendedProductBatchModelsInThisStep();
		
		if (c instanceof JComponent) {
			JComponent jc = (JComponent) c;
			if (rowIndex >= 0 && rowIndex < prodList.size() && !isEditing() ) {
				if ((getColumnName(vColIndex).equals(MedChemStoic_ProductsTableControllerUtility.CHEM_NAME_COL))
						|| (getColumnName(vColIndex).equals(MedChemStoic_ProductsTableControllerUtility.FORMULA_COL))) {
					ProductBatchModel tool_tip_batch = prodList.get(rowIndex);
					if (tool_tip_batch != null) {
						//newText = CeNGUIUtils.getCompoundToolTipText(tool_tip_batch.getCompound());
						// if Choloracnegen make cell color red.
						if ((getColumnName(vColIndex).equals(MedChemStoic_ProductsTableControllerUtility.CHEM_NAME_COL))
								&& tool_tip_batch.isChloracnegen()) {
							jc.setBackground(Color.red);
						} else {
							if (getColumnName(vColIndex).equals(MedChemStoic_ProductsTableControllerUtility.CHEM_NAME_COL)) {
								jc.setBackground(Color.white);
							}
						}
					}
				}
			}
			//jc.setToolTipText(newText);
		}
		return c;
	}

}
