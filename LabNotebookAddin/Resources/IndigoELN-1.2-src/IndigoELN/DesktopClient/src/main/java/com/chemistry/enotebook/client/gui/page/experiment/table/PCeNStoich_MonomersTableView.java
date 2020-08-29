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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.page.reagents.ReagentAdditionListener;
import com.chemistry.enotebook.client.gui.page.stoichiometry.CompoundCellEditor;
import com.chemistry.enotebook.client.gui.page.table.*;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.utils.CeN11To12ConversionUtils;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.virtuan.plateVisualizer.BatchPropertyFrame;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class PCeNStoich_MonomersTableView extends PCeNTableView implements ReagentAdditionListener{
	
	private static final long serialVersionUID = 3042079796172125908L;
	
	int selectedRow;
	private ReactionStepModel intermediateStep;
	private BatchPropertyFrame frame = new BatchPropertyFrame();
	private StoicModelInterface batchModel;
	private JPanel stoichCollapsiblePane;
	
	private Point currentMousePos;
	private String currentColumnName;

	public PCeNStoich_MonomersTableView()
	{
		
	}
	
	public PCeNStoich_MonomersTableView (JPanel stoichCollapsiblePane, PCeNTableModel model, int rowHeight, PCeNTableModelConnector mvconnector, PCeNTableViewPopupMenuManager tableViewPopupMenuManager,ReactionStepModel intermediateStepe) {
		super();
		initGUI(stoichCollapsiblePane, model, rowHeight, mvconnector, tableViewPopupMenuManager, intermediateStepe);
	}
	
	public PCeNStoich_MonomersTableView (JPanel stoichCollapsiblePane, PCeNTableModel model, int rowHeight, PCeNTableModelConnector mvconnector, PCeNTableViewPopupMenuManager tableViewPopupMenuManager,ReactionStepModel intermediateStepe, NotebookPageModel pageModel) {
		super(model, rowHeight, mvconnector, tableViewPopupMenuManager, pageModel);
		initGUI(stoichCollapsiblePane, model, rowHeight, mvconnector, tableViewPopupMenuManager, intermediateStepe);
	}
	
	private void initGUI(JPanel stoichCollapsiblePane, PCeNTableModel model, int rowHeight, PCeNTableModelConnector mvconnector, PCeNTableViewPopupMenuManager tableViewPopupMenuManager, ReactionStepModel intermediateStepe) {	
		this.stoichCollapsiblePane = stoichCollapsiblePane;
		connector = mvconnector;
		mTableViewPopupMenuManager = tableViewPopupMenuManager;
		this.intermediateStep = intermediateStepe;
		if(mTableViewPopupMenuManager!=null)
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
		//this.setSelectionModel(createDefaultSelectionModel());
		this.setColumnSelectionAllowed(false);
		this.setCellSelectionEnabled(false);
		this.setRowSelectionAllowed(true);
		//this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				currentMousePos = new Point(
						(int) (getLocationOnScreen().getX() + e.getX()),
						(int) (getLocationOnScreen().getY() + e.getY()));
				currentColumnName = getColumnName(columnAtPoint(e.getPoint()));
			}
		});
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 1) {
					checkboxclicked(evt);
//					solventCellClicked(evt);
				}
			} // end mouseClicked
			public void mouseExited(MouseEvent event)
			{
				setToolTipBatchModel(null);
				hideDisplayStructureProperties();
			}
		});
		setTableViewCellRenderers();
		// vb 12/13 customizeTableHeader();
	}

	private void checkboxclicked(MouseEvent evt) {
		String columnName = getColumnName(this.getSelectedColumn());
		if (columnName.equalsIgnoreCase(PCeNStoich_MonomersTableViewControllerUtility.LIMITING_COL) && this.getConnector().getPageModel().isEditable()) {
			connector.setSelectValue(this.getSelectedRow());
			if (stoichCollapsiblePane != null) {
				stoichCollapsiblePane.repaint();
			} else {
				this.repaint();
			}
		}

	}
	
	//override the super class impl
	protected void setTableViewCellRenderers() {
		PCeNTableViewHeaderRenderer headerRenderer = new PCeNTableViewHeaderRenderer();  // vb 12/13
		// setDefaultRenderer(java.lang.Number.class, new FractionCellRenderer(10, 6, SwingConstants.RIGHT));
		PCeNStoich_MonomersTableViewCellRenderer cellRenderer = new PCeNStoich_MonomersTableViewCellRenderer();
		PCeNStoich_MonomersTableViewCellEditor cellEditor = new PCeNStoich_MonomersTableViewCellEditor();
		int colCount = getColumnCount();
		for (int i = 0; i < colCount; i++) {
			String name = getColumnName(i);
			TableColumn col = getColumn(name);
			col.setHeaderRenderer(headerRenderer); // vb 12/13
			
//			Set all amount column renderers
			if(name.equals(PCeNStoich_MonomersTableViewControllerUtility.WEIGHT_COL)||
					name.equals(PCeNStoich_MonomersTableViewControllerUtility.MOLES_COL) ||
					name.equals(PCeNStoich_MonomersTableViewControllerUtility.VOLUME_COL) ||
					name.equals(PCeNStoich_MonomersTableViewControllerUtility.RXN_EQUIVS_COL) ||
					name.equals(PCeNStoich_MonomersTableViewControllerUtility.MOL_WEIGHT_COL) ||
					name.equals(PCeNStoich_MonomersTableViewControllerUtility.DENSITY_COL) ||
					name.equals(PCeNStoich_MonomersTableViewControllerUtility.MOLARITY_COL) ||
					name.equals(PCeNStoich_MonomersTableViewControllerUtility.PURITY_COL) ||
					name.equals(PCeNStoich_MonomersTableViewControllerUtility.LOADING_COL) ||
					name.equals(PCeNStoich_MonomersTableViewControllerUtility.SALT_EQUIVS_COL))
					
			{
				col.setCellEditor(new PAmountCellEditor());
				col.setCellRenderer(new PAmountCellRenderer());
			}
			else if (name.equalsIgnoreCase(PCeNStoich_MonomersTableViewControllerUtility.SALT_CODE_COL)) {
				col.setCellEditor(saltCdeditor);
				col.setCellRenderer(cellRenderer);
			} else if (name.equalsIgnoreCase(PCeNStoich_MonomersTableViewControllerUtility.RXN_ROLE_COL)) {
				col.setCellEditor(reagentTypeEditor);
				col.setCellRenderer(cellRenderer);
			} else if(name.equalsIgnoreCase(PCeNStoich_MonomersTableViewControllerUtility.COMP_ID_COL))
			{
				col.setCellEditor(new CompoundCellEditor());
				col.setCellRenderer(cellRenderer);
			}
			else {
				col.setCellEditor(cellEditor);
				col.setCellRenderer(cellRenderer);
			}
		}// end for

	}// end 
	
	private void customizeTableHeader() {
		JTableHeader th = getTableHeader();
		Font ft = th.getFont();
		if (ft.getStyle() != Font.BOLD) {
			th.setFont(new Font(th.getFont().getFontName(), Font.BOLD, th.getFont().getSize()));
		}
	}
	
	public void addReagentsFromList(List reagentsToAdd) {

		if (reagentsToAdd == null ||
			reagentsToAdd.size() == 0)
			return;
		// convert the models and add to Step
		List<MonomerBatchModel> models = CeN11To12ConversionUtils.getReagentsListConverted(reagentsToAdd);
		// Set the batchModels to changed status when we first add
		CeN11To12ConversionUtils.setModelChangedForMonBatchModels(models);

		MonomerBatchModel model = models.get(0);
		// Force the batch added this way as SOLVENT
		model.setBatchType(BatchType.SOLVENT);

		// because these are reagents , we can always add to this list.
		intermediateStep.addMonomerBatchListForStoic(models);
		this.updateUI();
		// todo fireevent

	}

//	private void solventCellClicked(MouseEvent evt) {
//
//		// Check if this is not Right button click
//		if (evt.getButton() == MouseEvent.BUTTON3)
//			return;
//		String columnName = getColumnName(this.getSelectedColumn());
//		if (columnName.equalsIgnoreCase(PCeNStoich_MonomersTableViewControllerUtility.SOLVENT_COL) &&
//			this.getConnector().getPageModel().isEditable()) {
//			// JOptionPane.showMessageDialog(this, "Selected row"+this.getSelectedRow());
//			// Check to see if this row is a Reactant as a Solvent can be added only to a reagent
//
//			List list = connector.getStoicElementListInTransactionOrder();
//			StoicModelInterface stoicModel = (StoicModelInterface) list.get(this.getSelectedRow());
//			String solvBatchKey = stoicModel.getStoicSolventsAdded();
//			if (stoicModel.getStoicReactionRole().equals(BatchType.REACTANT.toString()) ||
//				stoicModel.getStoicReactionRole().equals(BatchType.REAGENT.toString())) {
//				if (solvBatchKey == null ||
//					solvBatchKey.equals("")) {
//					selectedRow = this.getSelectedRow();
//					ReagentsFrame.viewReagentsFrame(ReagentsFrame.MY_REAGENTS_TAB, this, "", true);
//				} else {
//					// Solvent Already Exists. Delete solvent to add new Solvent/Mixture
//					JOptionPane.showMessageDialog(	this,
//													"Solvent is already added to " +
//													stoicModel.getStoicReactionRole() +
//													".Please delete solvent to add new solvent.",
//													"Solvent already exists!",
//													JOptionPane.INFORMATION_MESSAGE);
//				}
//			}
//
//		}
//	}

	public void addSolventsFromList(List reagentsToAdd) {
		if (reagentsToAdd == null ||
			reagentsToAdd.size() == 0)
			return;

		if (reagentsToAdd.size() > 1) {
			// Cannot add more than one batch as solvent.Throw a Error Dialog.
			JOptionPane.showMessageDialog(	this,
											"Cannot add more than one batch as solvent.Please select one batch",
											"Multiple Solvent batches!",
											JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// convert the models and add to Step
		List<MonomerBatchModel> models = reagentsToAdd; //CeN11To12ConversionUtils.getReagentsListConverted(reagentsToAdd);
		// Set the batchModels to changed status when we first add
		CeN11To12ConversionUtils.setModelChangedForMonBatchModels(models);

		MonomerBatchModel model = models.get(0);
		// Force the batch added this way as SOLVENT
		model.setBatchType(BatchType.SOLVENT);

		if (this.selectedRow >= 0 &&
			this.selectedRow <= connector.getRowCount()) {
			List list = connector.getStoicElementListInTransactionOrder();
			StoicModelInterface stoicModel = (StoicModelInterface) list.get(this.selectedRow);
			// Check if solvent is already added. To change solvent delete the solvent and add again.
			if (stoicModel.getStoicSolventsAdded() == null ||
				stoicModel.getStoicSolventsAdded().equals("")) {
				stoicModel.setStoicSolventsAdded(model.getKey());
				// Also need to use the concentration of Solvent set to Reactant
				// because these are reagents , we can always add to this list.
				intermediateStep.addMonomerBatchListForStoic(models);
				this.updateUI();
				enableSaveButton();
			}
		}
		// todo fireevent
	}

	private void enableSaveButton() {
		// System.out.println("enableSaveButton called ConceptionDetailsContainer");

		MasterController.getGUIComponent().enableSaveButtons();
	}

	public JToolTip createToolTip() {
		if (batchModel != null)
			displayStructureProperties();
		else
			hideDisplayStructureProperties();
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
			String name = currentColumnName;
			if (StringUtils.equals(name, NBK_BATCH_NUM)) {
				id = batchModel.getStoicBatchNumberAsString();
				if (batchModel instanceof MonomerBatchModel) {
					bmodel = (MonomerBatchModel) batchModel;
				} else if (batchModel instanceof BatchesList<?>) {
					bmodel = ((BatchesList<MonomerBatchModel>) batchModel).getBatchModels().get(0);
				}
			} else if (StringUtils.equals(name, CAS_NUMBER)) {
				id = batchModel.getStoicBatchCASNumber();
			if (batchModel instanceof MonomerBatchModel) {
					bmodel = (MonomerBatchModel) batchModel;
				} else if (batchModel instanceof BatchesList<?>) {
					bmodel = ((BatchesList<MonomerBatchModel>) batchModel).getBatchModels().get(0);
				}
			} else {
				if (batchModel instanceof ProductBatchModel) {
				id = batchModel.getStoichCompoundModel().getCompoundName();
				if (id == null || ((String) id).length() == 0)
					id = "P" + batchModel.getStoicTransactionOrder();
					bmodel = (ProductBatchModel) batchModel;
				} else if (batchModel instanceof MonomerBatchModel) {
					id = batchModel.getStoicCompoundId();
				bmodel = (MonomerBatchModel) batchModel;
			} else if (batchModel instanceof BatchesList<?>) {
				id = batchModel.getStoicCompoundId();
				bmodel = ((BatchesList<MonomerBatchModel>) batchModel).getBatchModels().get(0);
			} else {
				// we shouldn't get here.  Log the error but continue so GUI doesn't stop responding
				log.error("Object type not expected in Monomer Table View: " + batchModel.getClass().getName() + "\n Expected: MonomerBatchModel or BatchesList<MonomerBatchModel>");
			}
			}
			
			if (StringUtils.isNotBlank(id) || StringUtils.equals(currentColumnName, COMPOUND_ID)) {
				frame.displayBatchProperties(bmodel, id);
			
				Point loc = currentMousePos;
				if (loc == null) {
					loc = getLocationOnScreen();
				}
			
				frame.setLocation((int) loc.getX(), (int) getLocationOnScreen().getY() - 225);
			frame.setVisible(true);
			} else {
				frame.setVisible(false);
			}
		}
	}

	public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
		Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
		MonomerBatchModel tool_tip_batch = null;
		if (c instanceof JComponent) {
			JComponent jc = (JComponent) c;
			List list = connector.getStoicElementListInTransactionOrder();
			if (rowIndex >= 0 &&
				rowIndex < list.size() && !isEditing()) {

				StoicModelInterface stoicModel = (StoicModelInterface) list.get(rowIndex);
				if (stoicModel instanceof MonomerBatchModel) {
					tool_tip_batch = (MonomerBatchModel) stoicModel;

					if ((getColumnName(vColIndex).equals(PCeNStoich_MonomersTableViewControllerUtility.CHEM_NAME_COL)) ||
						(getColumnName(vColIndex).equals(PCeNStoich_MonomersTableViewControllerUtility.FORMULA_COL)) ||
						(getColumnName(vColIndex).equals(PCeNStoich_MonomersTableViewControllerUtility.BATCH_NUM_COL)) ||
						(getColumnName(vColIndex).equals(PCeNStoich_MonomersTableViewControllerUtility.CAS_NUM_COL)) ||
						(getColumnName(vColIndex).equals(PCeNStoich_MonomersTableViewControllerUtility.COMP_ID_COL))) {

						// if Choloracnegen make cell color red and add type info to tool tip.
						if ((getColumnName(vColIndex).equals(PCeNStoich_MonomersTableViewControllerUtility.COMP_ID_COL)) &&
							tool_tip_batch.isChloracnegen()) {
							jc.setBackground(Color.red);
						}
						// jc.setBackground(Color.WHITE);
					}
				}
			}

		}
		return c;
	}
	
}
