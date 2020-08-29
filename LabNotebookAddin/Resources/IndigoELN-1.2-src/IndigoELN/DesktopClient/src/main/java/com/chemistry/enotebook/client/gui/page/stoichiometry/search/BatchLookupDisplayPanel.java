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

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.stoichiometry.AbstractBatchColumnModel;
import com.chemistry.enotebook.client.gui.page.stoichiometry.FractionCellRenderer;
import com.chemistry.enotebook.client.gui.page.stoichiometry.interfaces.BatchLookupDisplayClient;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import com.chemistry.viewer.ChemistryViewer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 * 
 * Revamped from Jigloo impl.
 * 
 * Use getSelectedBatch to get the value the user chose. If null then nothing was selected.
 * 
 */
public class BatchLookupDisplayPanel extends javax.swing.JPanel {
	
	private static final long serialVersionUID = -4525529982693597017L;
	
	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private final String TITLE = "Selected Structure";
	private final int PREF_WIDTH = 550;
	private final int PREF_COMPONENT_HEIGHT = 150;
	private final int PREF_COMPONENT_WIDTH = 275;
	private final int PREF_PANEL_HEIGHT = 2 * PREF_COMPONENT_HEIGHT + 10;
	// GUI setup
	private JEditorPane jEditorPaneTextDetailData;
	private JPanel jPanelStructureViewer;
	private JTable jTableSearchResults;
	private JPanel jPanelDetails;
	private JScrollPane jScrollPaneSearchResults;
	private ChemistryViewer structDisplayCanvas;
	// Business logic part
	private BatchModel selectedBatch = null;
	private ArrayList<BatchLookupDisplayClient> listeners = new ArrayList<BatchLookupDisplayClient>();

	// Constructors
	public BatchLookupDisplayPanel() {
		this(null);
	}

	public BatchLookupDisplayPanel(List<BatchModel> batches) {
		initGUI(batches);
	}

	/**
	 * Initializes the GUI.
	 */
	public void initGUI(List<BatchModel> batches) {
		jScrollPaneSearchResults = new JScrollPane();
		jTableSearchResults = new JTable();
		jPanelDetails = new JPanel();
		jPanelStructureViewer = new JPanel();
		jEditorPaneTextDetailData = new JEditorPane();
		BorderLayout thisLayout = new BorderLayout();
		setLayout(thisLayout);
		thisLayout.setHgap(0);
		thisLayout.setVgap(0);
		setSize(new java.awt.Dimension(PREF_WIDTH, PREF_PANEL_HEIGHT));
		jScrollPaneSearchResults.setPreferredSize(new java.awt.Dimension(PREF_WIDTH, PREF_COMPONENT_HEIGHT));
		int minHeight = (jTableSearchResults.getRowHeight() * 3); // Header,
		// a row
		// and a
		// horizontal
		// scroll
		// bar
		jScrollPaneSearchResults.setMinimumSize(new java.awt.Dimension(PREF_WIDTH, minHeight));
		add(jScrollPaneSearchResults, BorderLayout.CENTER);
		ReagentLookupTableModel rltm = new ReagentLookupTableModel(batches);
		jTableSearchResults.setModel(rltm);
		ListSelectionModel lsm = jTableSearchResults.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				boolean isAdjusting = e.getValueIsAdjusting();
				// Find out which indexes are selected.
				int minIndex = lsm.getMinSelectionIndex();
				if (minIndex >= 0 && !isAdjusting) {
					// get the batch info from the table model and
					// display the details and the structure
					setSelectedBatch(); // use the tablemodel to tell us which
					// batch is selected.
					notifySelectionChangedClients();
					updateDisplay();
				}
			}
		});
		jScrollPaneSearchResults.add(jTableSearchResults);
		jScrollPaneSearchResults.setViewportView(jTableSearchResults);
		jScrollPaneSearchResults.setMinimumSize(new java.awt.Dimension(PREF_WIDTH, jTableSearchResults.getRowHeight() * 3)); // Header,
		// row
		// and
		// horizontal
		// scroll
		// bar
		jTableSearchResults.setPreferredScrollableViewportSize(new java.awt.Dimension(PREF_WIDTH, PREF_COMPONENT_HEIGHT));
		BoxLayout jPanelDetailsLayout = new BoxLayout(jPanelDetails, BoxLayout.X_AXIS);
		jPanelDetails.setLayout(jPanelDetailsLayout);
		jPanelDetails.setBorder(new LineBorder(Color.BLACK));
		jPanelDetails.setPreferredSize(new java.awt.Dimension(PREF_WIDTH, PREF_COMPONENT_HEIGHT));
		// Add the structure viewer
		jPanelStructureViewer.setToolTipText("Structure Viewer");
		// Need to remove Hgap and Vgap from layout before setting viewer
		BorderLayout structLayout = new BorderLayout();
		structLayout.setHgap(0);
		structLayout.setVgap(0);
		jPanelStructureViewer.setLayout(structLayout);
		jPanelStructureViewer.setPreferredSize(new java.awt.Dimension(PREF_COMPONENT_WIDTH, PREF_COMPONENT_HEIGHT));
		// Now we can set the viewer.
		try {
			structDisplayCanvas = new ChemistryViewer(TITLE, "Structure in " + TITLE);
			structDisplayCanvas.setReadOnly(true);
		} catch (Exception e) {
			ceh.logExceptionMsg(this, "Could not initialize the Chemistry Viewer for Structure Validation Lookup", e);
		}
		jPanelStructureViewer.add(structDisplayCanvas, BorderLayout.CENTER);
		jPanelDetails.add(jPanelStructureViewer);
		// Add the batch detail area
		jEditorPaneTextDetailData.setText(""); // Use \n to indicate new line
		// in display of the detail
		jEditorPaneTextDetailData.setFocusable(false);
		jEditorPaneTextDetailData.setEditable(false);
		jEditorPaneTextDetailData.setPreferredSize(new java.awt.Dimension(PREF_COMPONENT_WIDTH, PREF_COMPONENT_HEIGHT));
		jPanelDetails.add(jEditorPaneTextDetailData);
		add(jPanelDetails, BorderLayout.SOUTH);
		refreshTable();
	}

	public void refreshTable() {
		// Need to bold the headers
		JTableHeader th = jTableSearchResults.getTableHeader();
		th.setReorderingAllowed(false);
		CeNGUIUtils.styleComponentText(Font.BOLD, th);
		// Renderers
		DefaultTableCellRenderer strRendr = (DefaultTableCellRenderer) jTableSearchResults.getDefaultRenderer(String.class);
		strRendr = (DefaultTableCellRenderer) jTableSearchResults.getDefaultRenderer(String.class);
		strRendr.setHorizontalAlignment(SwingConstants.CENTER);
		jTableSearchResults.setDefaultRenderer(java.lang.Number.class, new FractionCellRenderer(10, 6, SwingConstants.CENTER));
		setTableColumnSizes(jTableSearchResults.getColumnModel(), ReagentLookupColumnModel.getInstance());
		jTableSearchResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTableSearchResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jTableSearchResults.sizeColumnsToFit(JTable.AUTO_RESIZE_OFF);
		jTableSearchResults.setRowSelectionAllowed(true);
		jTableSearchResults.setColumnSelectionAllowed(false);
	}

	public void setTableColumnSizes(TableColumnModel tcm, AbstractBatchColumnModel abcm) {
		TableColumn tCol;
		for (int i = 0; i < abcm.getColumnCount(); i++) {
			tCol = tcm.getColumn(i);
			tCol.setPreferredWidth(abcm.getColumnPreferredWidth(i));
			tCol.setMinWidth(abcm.getColumnMinWidth(i));
		}
	}

	public void dispose() {
		if (jTableSearchResults != null)
			jTableSearchResults.setModel(null);
		jTableSearchResults = null;
		jPanelStructureViewer = null;
		structDisplayCanvas = null;
	}

	public void setSelectedRow(int row) {
		if (row >= 0 && row < jTableSearchResults.getRowCount()) {
			selectedBatch = ((ReagentLookupTableModel) jTableSearchResults.getModel()).getBatchAt(row);
			jTableSearchResults.setRowSelectionInterval(row, row);
		}
	}

	/**
	 * Sets the batch currently selected in the table to the selectedBatch variable.
	 * 
	 */
	public void setSelectedBatch() {
		selectedBatch = null;
		if (jTableSearchResults.getModel() instanceof ReagentLookupTableModel) {
			ReagentLookupTableModel rtm = (ReagentLookupTableModel) jTableSearchResults.getModel();
			if (jTableSearchResults.getSelectedRow() >= 0)
				selectedBatch = rtm.getBatchAt(jTableSearchResults.getSelectedRow());
			// else don't select as it might not be the wish of the user.
		}
	}

	/**
	 * Given a batch, that batch is then matched to one in the table and the corresponding row is selected for that batch. If the
	 * batch is not matched, no row is selected.
	 * 
	 * @param batch -
	 *            batch to match to those in the current model
	 */
	public void setSelectedBatch(BatchModel batch) {
		selectedBatch = null;
		if (batch != null && jTableSearchResults.getModel() instanceof ReagentLookupTableModel) {
			ReagentLookupTableModel rtm = (ReagentLookupTableModel) jTableSearchResults.getModel();
			int row = -1;
			boolean found = false;
			for (int i = 0; i < rtm.getRowCount() && !found; i++) {
				row = i;
				found = rtm.getBatchAt(i).equals(batch);
			}
			setSelectedRow(row);
		}
	}

	public void selectFirstBatchInTable() {
		if (jTableSearchResults.getModel() instanceof ReagentLookupTableModel) {
			ReagentLookupTableModel rtm = (ReagentLookupTableModel) jTableSearchResults.getModel();
			if (jTableSearchResults.getRowCount() > 0) {
				jTableSearchResults.setRowSelectionInterval(0, 0);
				selectedBatch = rtm.getBatchAt(0);
			}
		}
	}

	public BatchModel getSelectedBatch() {
		return selectedBatch;
	}

	public void setModel(ReagentLookupTableModel rltm) {
		jTableSearchResults.setModel(rltm);
		refreshTable();
		setSelectedBatch();
		updateDisplay();
	}

	/**
	 * chemistry is null if compound is null
	 * 
	 * @param ab
	 */
	public void updateDisplay() {
		BatchModel ab = getSelectedBatch();
		try {
			if (ab == null || ab.getCompound() == null) {
				structDisplayCanvas.setChemistry(null);
			} else {
				structDisplayCanvas.setChemistry(ab.getCompound().getNativeSketch());
			}
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
			// Do nothing as nothing was given to us
		}
		updateBatchDetail(ab);
	}

	private void updateBatchDetail(BatchModel ab) {
		StringBuffer details = new StringBuffer();
		if (ab == null) {
			jEditorPaneTextDetailData.setText(""); // Use \n to indicate
			// new line in display
			// of the detail
		} else {
			ParentCompoundModel cp = ab.getCompound();
			details.append("Chemical Name: ");
			if (cp != null)
				if (!cp.getChemicalName().equals("null"))
					details.append(ab.getCompound().getChemicalName());
			details.append("\n");
			details.append("Compound ID: ");
			//TODO workaround for CENSTR 
			if (!cp.getRegNumber().startsWith(CeNConstants.CENSTR_ID_PREFIX)) {
				details.append(cp.getRegNumber());				
			}
			details.append("\n");
			details.append("Conv. Batch #: ");
			details.append(ab.getConversationalBatchNumber());
			details.append("\n");
			details.append("Nbk Batch #: ");
			details.append(ab.getBatchNumberAsString());
			details.append("\n");
			if (ab.getMolecularWeightAmount().isCalculated()) {
				details.append("Batch Mol. Wt. (calc'd): ");
				details.append(CeNNumberUtils.getBigDecimal(ab.getMolWgt(), 4).toString());
			} else {
				details.append("Batch Mol. Wt.: ");
				details.append(CeNNumberUtils.getBigDecimal(ab.getMolWgt(), 4).toString());
			}
			details.append("\n");
			if (ab.getMolecularFormula() != null && ab.getMolecularFormula().length() > 0) {
				details.append("Molecular Formula (calc'd): ");
				details.append(ab.getMolecularFormula());
			} else {
				details.append("Molecular Formula: ");
				details.append(ab.getMolecularFormula());
			}
			details.append("\n");
			details.append("Salt Code: ");
			details.append(ab.getSaltForm().getCode());
			details.append("\n");
			details.append("Salt Description: ");
			details.append(ab.getSaltForm().getDescription());
			details.append("\n");
			details.append("Salt Equivalents: ");
			if (ab.getSaltEquivsSet()) {
				details.append(CeNNumberUtils.getBigDecimal(ab.getSaltEquivs(), 2).toString());
			}
		}
		jEditorPaneTextDetailData.setText(details.toString()); // Use \n to indicate new line in display of the detail
	}

	public void addSelectionChangedListener(BatchLookupDisplayClient bldc) {
		if(listeners.contains(bldc) == false) {
			listeners.add(bldc);
		}
	}

	public void removeSelectionChangedListener(BatchLookupDisplayClient bldc) {
		listeners.remove(bldc);
	}

	private void notifySelectionChangedClients() {
		BatchModel ab = getSelectedBatch();
		for (BatchLookupDisplayClient client : listeners) {
			client.syncBatchSelected(ab);
		}
	}
}
