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
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.experiment.utils.CeNNumberUtils;
import com.chemistry.enotebook.utils.CeNDialog;
import com.chemistry.viewer.ChemistryViewer;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class CompoundIDLookupDialog extends CeNDialog {
	
	private static final long serialVersionUID = -8165306432289275125L;
	
	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	public static int CANCELLED = -1;
	public static int SAVE = 1;
	private static String TITLE = "Structure Validation";
	// GUI setup
	private JEditorPane jEditorPaneTextDetailData;
	private JPanel jPanelStructureViewer;
	private JTable jTableSearchResults;
	private JPanel jPanelDetails;
	private JScrollPane jScrollPaneSearchResults;
	private JPanel jPanelSearchResultsCenter;
	private JLabel jlabelSearchCompNo;
	private JLabel jLabelSearchTitle;
	private JPanel jPanelFiller;
	private JButton jButtonCancel;
	private JButton jButtonUseThisStruc;
	private JPanel jPanelLabelsNorth;
	private JPanel jPanelButtons;
	private ChemistryViewer structDisplayCanvas;
	private int status = CANCELLED;
	// Business logic part
	private BatchModel selectedBatch = null;

	// Constructors
	public CompoundIDLookupDialog(Frame owner) {
		this(owner, "-none", null);
	}

	public CompoundIDLookupDialog(Frame owner, String searchString, List<BatchModel> batches) {
		super(owner);
		initGUI(searchString, batches);
		setModal(true);
		// Center dialog on parent
		if (owner != null) {
			Point p = owner.getLocation();
			setLocation(p.x + (owner.getWidth() - getWidth()) / 2, p.y + (owner.getHeight() - getHeight()) / 2);
		}
	}

	/**
	 * Initializes the GUI.
	 */
	public void initGUI(String searchString, List<BatchModel> batches) {
		jPanelButtons = new JPanel();
		jButtonUseThisStruc = new JButton();
		jPanelFiller = new JPanel();
		jButtonCancel = new JButton();
		jPanelLabelsNorth = new JPanel();
		jLabelSearchTitle = new JLabel();
		jlabelSearchCompNo = new JLabel();
		jPanelSearchResultsCenter = new JPanel();
		jScrollPaneSearchResults = new JScrollPane();
		jTableSearchResults = new JTable();
		jPanelDetails = new JPanel();
		jPanelStructureViewer = new JPanel();
		jEditorPaneTextDetailData = new JEditorPane();
		BorderLayout thisLayout = new BorderLayout();
		getContentPane().setLayout(thisLayout);
		thisLayout.setHgap(0);
		thisLayout.setVgap(0);
		setTitle(TITLE);
		setModal(true);
		setSize(new java.awt.Dimension(558, 363));
		jPanelLabelsNorth.setLayout(new FlowLayout(FlowLayout.LEADING));
		getContentPane().add(jPanelButtons, BorderLayout.SOUTH);
		jButtonUseThisStruc.setText("Use this structure");
		jButtonUseThisStruc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonUseThisStrucActionPerformed(evt);
			}
		});
		jPanelButtons.add(jButtonUseThisStruc);
		jPanelFiller.setVisible(true);
		jPanelFiller.setPreferredSize(new java.awt.Dimension(61, 10));
		jPanelButtons.add(jPanelFiller);
		jButtonCancel.setText("Cancel");
		jButtonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonCancelActionPerformed(evt);
			}
		});
		jPanelButtons.add(jButtonCancel);
		getContentPane().add(jPanelLabelsNorth, BorderLayout.NORTH);
		jLabelSearchTitle.setText("Search Query is: ");
		CeNGUIUtils.styleComponentText(Font.BOLD, jLabelSearchTitle);
		jLabelSearchTitle.setPreferredSize(new java.awt.Dimension(125, 22));
		jPanelLabelsNorth.add(jLabelSearchTitle);
		jlabelSearchCompNo.setText(searchString);
		// Need to bold the entered value field
		CeNGUIUtils.styleComponentText(Font.BOLD, jlabelSearchCompNo);
		jlabelSearchCompNo.setVisible(true);
		jPanelLabelsNorth.add(jlabelSearchCompNo);
		BoxLayout jPanelSearchResultsCenterLayout = new BoxLayout(jPanelSearchResultsCenter, 1);
		jPanelSearchResultsCenter.setLayout(jPanelSearchResultsCenterLayout);
		getContentPane().add(jPanelSearchResultsCenter, BorderLayout.CENTER);
		jScrollPaneSearchResults.setPreferredSize(new java.awt.Dimension(551, 162));
		jPanelSearchResultsCenter.add(jScrollPaneSearchResults);
		jScrollPaneSearchResults.add(jTableSearchResults);
		jScrollPaneSearchResults.setViewportView(jTableSearchResults);
		jTableSearchResults.setModel(new ReagentLookupTableModel(batches));
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
					setSelectedBatch();
					updateDisplay();
				}
			}
		});
		BoxLayout jPanelDetailsLayout = new BoxLayout(jPanelDetails, 0);
		jPanelDetails.setLayout(jPanelDetailsLayout);
		jPanelDetails.setPreferredSize(new java.awt.Dimension(551, 135));
		jPanelSearchResultsCenter.add(jPanelDetails);
		// Add the structure viewer
		jPanelStructureViewer.setToolTipText("Structure Viewer");
		// Need to remove Hgap and Vgap from layout before setting viewer
		BorderLayout structLayout = new BorderLayout();
		structLayout.setHgap(0);
		structLayout.setVgap(0);
		jPanelStructureViewer.setLayout(structLayout);
		jPanelStructureViewer.setPreferredSize(new java.awt.Dimension(213, 126));
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
		jEditorPaneTextDetailData.setPreferredSize(new java.awt.Dimension(194, 126));
		jPanelDetails.add(jEditorPaneTextDetailData);
		postInitGUI();
	}

	public void postInitGUI() {
		// Need to bold the headers
		JTableHeader th = jTableSearchResults.getTableHeader();
		// th.setReorderingAllowed(false);
		CeNGUIUtils.styleComponentText(Font.BOLD, th);
		// Column Setup
		ReagentLookupColumnModel rlcm = ReagentLookupColumnModel.getInstance();
		TableColumnModel tcm = jTableSearchResults.getColumnModel();
		TableColumn tCol = tcm.getColumn(ReagentLookupColumnModel.FORMULA);
		tCol.setPreferredWidth(rlcm.getColumnPreferredWidth(ReagentLookupColumnModel.FORMULA));
		tCol.setMinWidth(rlcm.getColumnMinWidth(ReagentLookupColumnModel.FORMULA));
		tCol = tcm.getColumn(ReagentLookupColumnModel.COMPOUND_ID);
		tCol.setPreferredWidth(rlcm.getColumnPreferredWidth(ReagentLookupColumnModel.COMPOUND_ID));
		tCol.setMinWidth(rlcm.getColumnMinWidth(ReagentLookupColumnModel.COMPOUND_ID));
		tCol = tcm.getColumn(ReagentLookupColumnModel.BATCH_LOT_NUMBER);
		tCol.setPreferredWidth(rlcm.getColumnPreferredWidth(ReagentLookupColumnModel.BATCH_LOT_NUMBER));
		tCol.setMinWidth(rlcm.getColumnMinWidth(ReagentLookupColumnModel.BATCH_LOT_NUMBER));
		tCol = tcm.getColumn(ReagentLookupColumnModel.MOLECULAR_WEIGHT);
		tCol.setPreferredWidth(rlcm.getColumnPreferredWidth(ReagentLookupColumnModel.MOLECULAR_WEIGHT));
		tCol.setMinWidth(rlcm.getColumnMinWidth(ReagentLookupColumnModel.MOLECULAR_WEIGHT));
		// tCol = tcm.getColumn(ReagentLookupColumnModel.SALT_CODE);
		// tCol.setPreferredWidth(rlcm.getColumnPreferredWidth(ReagentLookupColumnModel.SALT_CODE));
		// tCol.setMinWidth(rlcm.getColumnMinWidth(ReagentLookupColumnModel.SALT_CODE));
		//        
		// tCol = tcm.getColumn(ReagentLookupColumnModel.SALT_EQUIVS);
		// tCol.setPreferredWidth(rlcm.getColumnPreferredWidth(ReagentLookupColumnModel.SALT_EQUIVS));
		// tCol.setMinWidth(rlcm.getColumnMinWidth(ReagentLookupColumnModel.SALT_EQUIVS));
		//
		// initColumnSizes();
		jTableSearchResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jTableSearchResults.sizeColumnsToFit(JTable.AUTO_RESIZE_OFF);
		jTableSearchResults.setRowSelectionAllowed(true);
		jTableSearchResults.setColumnSelectionAllowed(false);
		// If there is data to show, select the first item
		if (jTableSearchResults.getRowCount() > 0)
			jTableSearchResults.setRowSelectionInterval(0, 0);
		// need to run this as we won't get a selection changed notification
		// from the table
		setSelectedBatch();
		updateDisplay();
	}

	protected void jButtonUseThisStrucActionPerformed(ActionEvent evt) {
		// Take currently selected batch and use copy to get the values back
		status = SAVE;
		setSelectedBatch();
		setVisible(false);
		// Calling entity needs to dispose of this object
	}

	/**
	 * Calling entity can pick up selected batch from the getSelectedBatch() method Must call dispose() when done with the dialog or
	 * it will stick around.
	 * 
	 * @param evt =
	 *            not used.
	 */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		// No change to current batch.
		status = CANCELLED;
		setVisible(false);
		// Calling entity needs to dispose of this object
	}

	private void setSelectedBatch() {
		selectedBatch = null;
		if (jTableSearchResults.getModel() instanceof ReagentLookupTableModel) {
			ReagentLookupTableModel rtm = (ReagentLookupTableModel) jTableSearchResults.getModel();
			selectedBatch = rtm.getBatchAt(jTableSearchResults.getSelectedRow());
		}
	}

	public BatchModel getSelectedBatch() {
		return selectedBatch;
	}

	/**
	 * chemistry is null if compound is null
	 * 
	 * @param ab
	 */
	public void updateDisplay() {
		BatchModel ab = getSelectedBatch();
		if (ab != null) {
			try {
				if (ab.getCompound() == null) {
					structDisplayCanvas.setChemistry(null);
				} else {
					structDisplayCanvas.setChemistry(ab.getCompound().getNativeSketch());
				}
			} catch (Exception e) {
				// Do nothing as nothing was given to us
				CeNErrorHandler.getInstance().logExceptionMsg(this, e);
			}
			updateBatchDetail(ab);
		}
	}

	private void updateBatchDetail(BatchModel ab) {
		StringBuffer details = new StringBuffer();
		if (ab == null) {
			jEditorPaneTextDetailData.setText(""); // Use \n to indicate new line in display of the detail
		} else {
			ParentCompoundModel cp = ab.getCompound();
			details.append("Chemical Name: ");
			if (cp != null)
				if (!cp.getChemicalName().equals("null"))
					details.append(ab.getCompound().getChemicalName());
			details.append("\n");
			details.append("Compound ID: ");
			details.append(cp.getRegNumber());
			details.append("\n");
			details.append("Conv. Batch #: ");
			details.append(ab.getConversationalBatchNumber());
			details.append("\n");
			details.append("Nbk Batch #: ");
			if (StringUtils.isNotBlank(ab.getBatchNumberAsString()))
				details.append(ab.getBatchNumberAsString());
			else
				details.append(ab.getOriginalBatchNumber());
			details.append("\n");
			details.append("Batch Mol. Wt. (calc'd):");
			details.append(CeNNumberUtils.getBigDecimal(ab.getMolWgt(), 4).toString());
			details.append("\n");
			details.append("Molecular Formula (calc'd):");
			details.append(ab.getMolecularFormula());
			details.append("\n");
			details.append("Salt Code: ");
			details.append(ab.getSaltForm().getCode());
			details.append("\n");
			details.append("Salt Description: ");
			details.append(ab.getSaltForm().getDescription());
			details.append("\n");
			if (ab.getSaltEquivsSet()) {
				details.append("Salt Equivalents: ");
				details.append(CeNNumberUtils.getBigDecimal(ab.getSaltEquivs(), 2).toString());
			}
		}
		jEditorPaneTextDetailData.setText(details.toString()); // Use \n to indicate new line in display of the detail
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
