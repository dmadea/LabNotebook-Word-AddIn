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
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.SingletonNotebookPageGUI;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.PurityModel;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchRegistrationResidualSolvent;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchRegistrationSolubilitySolvent;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.utils.CommonUtils;
import com.common.chemistry.codetable.CodeTableCache;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class CompoundRegInfoContainer extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3082802986445702414L;
	private JLabel extSupplier;
	private JLabel hit_id;
	private JLabel precursor;
	private JLabel parentMF;
	private JLabel batchMF;
	private JLabel labSite;
	private JLabel jLabelLabSite;
	private JLabel jLabelExtSupRef;
	private JLabel solubility;
	private JLabel jLabelCalcParentMF;
	private JLabel noEquivOfSalt;
	private JLabel jLabelNoEquivSalt;
	private JLabel meltingPoint;
	private JLabel jLabelMeltingPoint;
	private JLabel calcParentMW;
	private JLabel jLabelCalcParentMW;
	private JLabel synthBy;
	private JLabel jLabelSynBy;
	private JLabel batchOwner;
	private JLabel jLabelBatchOwner;
	private JLabel sourceDetail;
	private JLabel jLabelSourceDetail;
	private JLabel compSource;
	private JLabel jLabelCompSource;
	private JPanel jPanel8;
	private JPanel jPanel6;
	private JPanel jPanel5;
	private JPanel jPanel4;
	private JPanel jPanel2;
	private JLabel compState;
	private JLabel jLabelCompState;
	private JLabel calcBatchMW;
	private JLabel jLabelCalcBatchMW;
	private JLabel purity;
	private JLabel jLabelPurity;
	private JLabel amountMade;
	private JLabel jLabelAmountMade;
	private JLabel batch_Lot;
	private JLabel jLabelBatchLot;
	private JLabel ta_area;
	private JLabel jLabelTAarea;
	private JLabel jLabelProjectCode;
	private JLabel projectCode;
	private JLabel jLabelBatchComment;
	private JLabel batchComments;
	private JLabel jLabelSaltCodeName;
	private JLabel saltCode;
	private JLabel jLabelResSolvents;
	private JLabel jLabelCalcBatchMF;
	private JLabel residualSolvents;
	private JLabel jLabelSolSolu;
	private JLabel jLabelReactantID;
	private JLabel jLabelHitID;
	private JPanel jPanel3;
	private JScrollPane jScrollPane3;
	private JPanel jPanel1;
	private NotebookPageGuiInterface parentDialog;
	// private BatchRegistrationInfo registrationInfoVO = new
	// BatchRegistrationInfo();
	private JDialogEditCompRegInfo jDialogEditCompRegInfo;
	private ProductBatchModel selectedBatch = null;
	private JToolBar jToolBarOptions;
	private JButton jButtonEditInfo;

	public CompoundRegInfoContainer() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanel1 = new JPanel();
			jScrollPane3 = new JScrollPane();
			jPanel3 = new JPanel();
			jPanel8 = new JPanel();
			jPanel5 = new JPanel();
			jLabelBatchLot = new JLabel();
			jLabelCompSource = new JLabel();
			jLabelSourceDetail = new JLabel();
			jLabelBatchOwner = new JLabel();
			jLabelSynBy = new JLabel();
			jLabelLabSite = new JLabel();
			jLabelAmountMade = new JLabel();
			jLabelCompState = new JLabel();
			jLabelCalcBatchMW = new JLabel();
			jLabelCalcParentMW = new JLabel();
			jLabelPurity = new JLabel();
			jLabelMeltingPoint = new JLabel();
			jPanel6 = new JPanel();
			batch_Lot = new JLabel();
			compSource = new JLabel();
			sourceDetail = new JLabel();
			batchOwner = new JLabel();
			synthBy = new JLabel();
			labSite = new JLabel();
			amountMade = new JLabel();
			compState = new JLabel();
			calcBatchMW = new JLabel();
			calcParentMW = new JLabel();
			purity = new JLabel();
			meltingPoint = new JLabel();
			jPanel2 = new JPanel();
			jLabelBatchComment = new JLabel();
			jLabelTAarea = new JLabel();
			jLabelProjectCode = new JLabel();
			jLabelSaltCodeName = new JLabel();
			jLabelNoEquivSalt = new JLabel();
			jLabelResSolvents = new JLabel();
			jLabelSolSolu = new JLabel();
			jLabelCalcBatchMF = new JLabel();
			jLabelCalcParentMF = new JLabel();
			jLabelReactantID = new JLabel();
			jLabelHitID = new JLabel();
			jLabelExtSupRef = new JLabel();
			jPanel4 = new JPanel();
			batchComments = new JLabel();
			ta_area = new JLabel();
			projectCode = new JLabel();
			saltCode = new JLabel();
			noEquivOfSalt = new JLabel();
			residualSolvents = new JLabel();
			solubility = new JLabel();
			batchMF = new JLabel();
			parentMF = new JLabel();
			precursor = new JLabel();
			hit_id = new JLabel();
			extSupplier = new JLabel();
			BorderLayout thisLayout = new BorderLayout();
			this.setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setPreferredSize(new java.awt.Dimension(540, 351));
			BoxLayout jPanel1Layout = new BoxLayout(jPanel1, 1);
			jPanel1.setLayout(jPanel1Layout);
			jPanel1.setPreferredSize(new java.awt.Dimension(540, 781));
			this.add(jPanel1, BorderLayout.CENTER);
			jScrollPane3.setPreferredSize(new java.awt.Dimension(540, 267));
			jPanel1.add(jScrollPane3);
			BorderLayout jPanel3Layout = new BorderLayout();
			jPanel3.setLayout(jPanel3Layout);
			jPanel3Layout.setHgap(0);
			jPanel3Layout.setVgap(0);
			jPanel3.setPreferredSize(new java.awt.Dimension(536, 347));
			jScrollPane3.add(jPanel3);
			jScrollPane3.setViewportView(jPanel3);
			GridLayout jPanel8Layout = new GridLayout(1, 1);
			jPanel8.setLayout(jPanel8Layout);
			jPanel8Layout.setRows(1);
			jPanel8Layout.setHgap(5);
			jPanel8Layout.setVgap(0);
			jPanel8Layout.setColumns(1);
			jPanel3.add(jPanel8, BorderLayout.CENTER);
			jPanel8.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jPanel8MouseClicked(evt);
				}
			});
			GridLayout jPanel5Layout = new GridLayout(13, 1);
			jPanel5.setLayout(jPanel5Layout);
			jPanel5Layout.setRows(13);
			jPanel5Layout.setHgap(0);
			jPanel5Layout.setVgap(0);
			jPanel5Layout.setColumns(1);
			jPanel5.setPreferredSize(new java.awt.Dimension(128, 179));
			jPanel8.add(jPanel5);
			jLabelBatchLot.setText("Nbk Batch #:");
			jLabelBatchLot.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelBatchLot.setPreferredSize(new java.awt.Dimension(126, 17));
			jLabelBatchLot.setFocusTraversalKeysEnabled(true);
			jPanel5.add(jLabelBatchLot);
			jLabelCompSource.setText("Compound Source:");
			jLabelCompSource.setFont(new java.awt.Font("Sansserif", 1, 12));
			jPanel5.add(jLabelCompSource);
			jLabelSourceDetail.setText("Source Detail:");
			jLabelSourceDetail.setFont(new java.awt.Font("Sansserif", 1, 12));
			jPanel5.add(jLabelSourceDetail);
			jLabelBatchOwner.setText("Batch Owner:");
			jLabelBatchOwner.setFont(new java.awt.Font("Sansserif", 1, 12));
			jPanel5.add(jLabelBatchOwner);
			jLabelSynBy.setText("Synthesized By:");
			jLabelSynBy.setFont(new java.awt.Font("Sansserif", 1, 12));
			jPanel5.add(jLabelSynBy);
			jLabelLabSite.setText("Lab Site:");
			jLabelLabSite.setFont(new java.awt.Font("Sansserif", 1, 12));
			jPanel5.add(jLabelLabSite);
			jLabelAmountMade.setText("Amount Made:");
			jLabelAmountMade.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelAmountMade.setPreferredSize(new java.awt.Dimension(126, 17));
			jLabelAmountMade.setFocusTraversalKeysEnabled(true);
			jPanel5.add(jLabelAmountMade);
			jLabelCompState.setText("Compound State:");
			jLabelCompState.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelCompState.setPreferredSize(new java.awt.Dimension(126, 17));
			jLabelCompState.setFocusTraversalKeysEnabled(true);
			jPanel5.add(jLabelCompState);
			jLabelCalcBatchMW.setText("Calculated Batch MW:");
			jLabelCalcBatchMW.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelCalcBatchMW.setPreferredSize(new java.awt.Dimension(126, 17));
			jLabelCalcBatchMW.setFocusTraversalKeysEnabled(true);
			jPanel5.add(jLabelCalcBatchMW);
			jLabelCalcParentMW.setText("Calculated Parent MW:");
			jLabelCalcParentMW.setFont(new java.awt.Font("Sansserif", 1, 12));
			jPanel5.add(jLabelCalcParentMW);
			jLabelPurity.setText("Purity (%):");
			jLabelPurity.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelPurity.setPreferredSize(new java.awt.Dimension(126, 17));
			jLabelPurity.setFocusTraversalKeysEnabled(true);
			jPanel5.add(jLabelPurity);
			jLabelMeltingPoint.setText("Melting Point Range:");
			jLabelMeltingPoint.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelMeltingPoint.setPreferredSize(new java.awt.Dimension(130, 18));
			jPanel5.add(jLabelMeltingPoint);
			GridLayout jPanel6Layout = new GridLayout(13, 1);
			jPanel6.setLayout(jPanel6Layout);
			jPanel6Layout.setRows(13);
			jPanel6Layout.setHgap(0);
			jPanel6Layout.setVgap(0);
			jPanel6Layout.setColumns(1);
			jPanel6.setPreferredSize(new java.awt.Dimension(114, 260));
			jPanel8.add(jPanel6);
			batch_Lot.setFont(new java.awt.Font("Sansserif", 0, 12));
			batch_Lot.setPreferredSize(new java.awt.Dimension(126, 17));
			batch_Lot.setFocusTraversalKeysEnabled(true);
			jPanel6.add(batch_Lot);
			compSource.setFont(new java.awt.Font("Sansserif", 0, 11));
			compSource.setToolTipText("See QA document in the help menu for proper use of Source codes.");
			jPanel6.add(compSource);
			sourceDetail.setFont(new java.awt.Font("Sansserif", 0, 11));
			sourceDetail.setToolTipText("See QA document in the help menu for proper use of Source Detail codes.");
			jPanel6.add(sourceDetail);
			batchOwner.setFont(new java.awt.Font("Sansserif", 0, 12));
			jPanel6.add(batchOwner);
			synthBy.setFont(new java.awt.Font("Sansserif", 0, 12));
			jPanel6.add(synthBy);
			labSite.setFont(new java.awt.Font("Sansserif", 0, 12));
			jPanel6.add(labSite);
			amountMade.setFont(new java.awt.Font("Sansserif", 0, 12));
			amountMade.setPreferredSize(new java.awt.Dimension(126, 17));
			amountMade.setFocusTraversalKeysEnabled(true);
			jPanel6.add(amountMade);
			compState.setFont(new java.awt.Font("Sansserif", 0, 12));
			compState.setPreferredSize(new java.awt.Dimension(126, 17));
			compState.setFocusTraversalKeysEnabled(true);
			jPanel6.add(compState);
			calcBatchMW.setFont(new java.awt.Font("Sansserif", 0, 12));
			calcBatchMW.setPreferredSize(new java.awt.Dimension(126, 17));
			calcBatchMW.setFocusTraversalKeysEnabled(true);
			jPanel6.add(calcBatchMW);
			calcParentMW.setFont(new java.awt.Font("Sansserif", 0, 12));
			jPanel6.add(calcParentMW);
			purity.setFont(new java.awt.Font("Sansserif", 0, 12));
			purity.setPreferredSize(new java.awt.Dimension(126, 17));
			purity.setFocusTraversalKeysEnabled(true);
			jPanel6.add(purity);
			meltingPoint.setFont(new java.awt.Font("Sansserif", 0, 12));
			jPanel6.add(meltingPoint);
			GridLayout jPanel2Layout = new GridLayout(14, 1);
			jPanel2.setLayout(jPanel2Layout);
			jPanel2Layout.setRows(14);
			jPanel2Layout.setHgap(0);
			jPanel2Layout.setVgap(0);
			jPanel2Layout.setColumns(1);
			jPanel2.setPreferredSize(new java.awt.Dimension(126, 244));
			jPanel8.add(jPanel2);
			jLabelBatchComment.setText("Nbk Batch Comments:");
			jLabelBatchComment.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelBatchComment.setPreferredSize(new java.awt.Dimension(126, 17));
			jPanel2.add(jLabelBatchComment);
			jLabelTAarea.setText("Therapeutic Area:");
			jLabelTAarea.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelTAarea.setPreferredSize(new java.awt.Dimension(126, 17));
			jLabelTAarea.setFocusTraversalKeysEnabled(true);
			jPanel2.add(jLabelTAarea);
			jLabelProjectCode.setText("Project Code/Name:");
			jLabelProjectCode.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelProjectCode.setPreferredSize(new java.awt.Dimension(126, 17));
			jPanel2.add(jLabelProjectCode);
			jLabelSaltCodeName.setText("Salt Code/Name:");
			jLabelSaltCodeName.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelSaltCodeName.setPreferredSize(new java.awt.Dimension(126, 17));
			jPanel2.add(jLabelSaltCodeName);
			jLabelNoEquivSalt.setText("# Equivalents of Salt:");
			jLabelNoEquivSalt.setFont(new java.awt.Font("Sansserif", 1, 12));
			jPanel2.add(jLabelNoEquivSalt);
			jLabelResSolvents.setText("Residual Solvents:");
			jLabelResSolvents.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelResSolvents.setPreferredSize(new java.awt.Dimension(126, 17));
			jPanel2.add(jLabelResSolvents);
			jLabelSolSolu.setText("Solubility In Solvents:");
			jLabelSolSolu.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelSolSolu.setPreferredSize(new java.awt.Dimension(126, 17));
			jPanel2.add(jLabelSolSolu);
			// jLabelCompoundColor.setText("Compound Colour:");
			// jLabelCompoundColor.setFont(new
			// java.awt.Font("Sansserif",1,12));
			// jLabelCompoundColor.setPreferredSize(new
			// java.awt.Dimension(130,24));
			// jLabelCompoundColor.setFocusTraversalKeysEnabled(true);
			// jPanel2.add(jLabelCompoundColor);
			jLabelCalcBatchMF.setText("Calculated Batch MF:");
			jLabelCalcBatchMF.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelCalcBatchMF.setPreferredSize(new java.awt.Dimension(126, 17));
			jPanel2.add(jLabelCalcBatchMF);
			jLabelCalcParentMF.setText("Calculated Parent MF:");
			jLabelCalcParentMF.setFont(new java.awt.Font("Sansserif", 1, 12));
			jPanel2.add(jLabelCalcParentMF);
			jLabelReactantID.setText("Precursor/Reactant ID(s):");
			jLabelReactantID.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelReactantID.setPreferredSize(new java.awt.Dimension(126, 17));
			jPanel2.add(jLabelReactantID);
			jLabelHitID.setText("Hit-ID (plate Id-well):");
			jLabelHitID.setFont(new java.awt.Font("Sansserif", 1, 12));
			jLabelHitID.setPreferredSize(new java.awt.Dimension(126, 17));
			jPanel2.add(jLabelHitID);
			jLabelExtSupRef.setText("External Supplier Ref:");
			jLabelExtSupRef.setFont(new java.awt.Font("Sansserif", 1, 12));
			jPanel2.add(jLabelExtSupRef);
			GridLayout jPanel4Layout = new GridLayout(14, 1);
			jPanel4.setLayout(jPanel4Layout);
			jPanel4Layout.setRows(14);
			jPanel4Layout.setHgap(0);
			jPanel4Layout.setVgap(0);
			jPanel4Layout.setColumns(1);
			jPanel4.setPreferredSize(new java.awt.Dimension(130, 244));
			jPanel8.add(jPanel4);
			batchComments.setFont(new java.awt.Font("Sansserif", 0, 12));
			batchComments.setPreferredSize(new java.awt.Dimension(126, 17));
			jPanel4.add(batchComments);
			ta_area.setFont(new java.awt.Font("Sansserif", 0, 12));
			ta_area.setPreferredSize(new java.awt.Dimension(130, 24));
			ta_area.setFocusTraversalKeysEnabled(true);
			jPanel4.add(ta_area);
			projectCode.setFont(new java.awt.Font("Sansserif", 0, 12));
			projectCode.setPreferredSize(new java.awt.Dimension(126, 17));
			jPanel4.add(projectCode);
			saltCode.setFont(new java.awt.Font("Sansserif", 0, 12));
			saltCode.setPreferredSize(new java.awt.Dimension(126, 17));
			saltCode.setRequestFocusEnabled(true);
			jPanel4.add(saltCode);
			noEquivOfSalt.setFont(new java.awt.Font("Sansserif", 0, 12));
			noEquivOfSalt.setRequestFocusEnabled(false);
			jPanel4.add(noEquivOfSalt);
			residualSolvents.setText("-none-");
			residualSolvents.setFont(new java.awt.Font("Sansserif", 0, 12));
			residualSolvents.setPreferredSize(new java.awt.Dimension(126, 17));
			jPanel4.add(residualSolvents);
			solubility.setText("-none-");
			solubility.setFont(new java.awt.Font("Sansserif", 0, 12));
			jPanel4.add(solubility);
			// compoundColor.setText("-none-");
			// compoundColor.setFont(new java.awt.Font("Sansserif",0,12));
			// compoundColor.setPreferredSize(new
			// java.awt.Dimension(130,17));
			// jPanel4.add(compoundColor);
			batchMF.setFont(new java.awt.Font("Sansserif", 0, 12));
			batchMF.setPreferredSize(new java.awt.Dimension(130, 24));
			jPanel4.add(batchMF);
			parentMF.setFont(new java.awt.Font("Sansserif", 0, 12));
			parentMF.setRequestFocusEnabled(false);
			jPanel4.add(parentMF);
			precursor.setFont(new java.awt.Font("Sansserif", 0, 12));
			precursor.setPreferredSize(new java.awt.Dimension(130, 24));
			jPanel4.add(precursor);
			hit_id.setFont(new java.awt.Font("Sansserif", 0, 12));
			hit_id.setPreferredSize(new java.awt.Dimension(130, 24));
			jPanel4.add(hit_id);
			extSupplier.setFont(new java.awt.Font("Sansserif", 0, 12));
			jPanel4.add(extSupplier);
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		double toolBarSize[][] = {
				{ 20, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP,
						CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN,
						CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN, CeNGUIUtils.HORIZ_GAP, CeNGUIUtils.MIN }, { 20, 2 } };
		jToolBarOptions = new JToolBar();
		TableLayout jToolBarLayout = new TableLayout(toolBarSize);
		jToolBarOptions.setLayout(jToolBarLayout);
		jToolBarOptions.setBorderPainted(false);
		jToolBarOptions.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
		add(jToolBarOptions, BorderLayout.NORTH);
		// Add buttons to toolbar
		jButtonEditInfo = new JButton();
		jButtonEditInfo.setText("Edit Registration Information");
		jButtonEditInfo.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font(
				"MS Sans Serif", 0, 11), new java.awt.Color(0, 0, 0)));
		CeNGUIUtils.styleComponentText(Font.BOLD, jButtonEditInfo);
		jToolBarOptions.add(jButtonEditInfo, "2,0");
		jButtonEditInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				processMouseRightClicked(null);
			}
		});
	}

	public void updateDisplay() {
		String val;

		NotebookPageModel page = (NotebookPageModel) getParentDialog().getPageModel();

		// batch id
		batch_Lot.setText(getSelectedBatch().getBatchNumberAsString());
		batch_Lot.setToolTipText(batch_Lot.getText());
		// compound source
		val = "";
		try {
			if (!getSelectedBatch().getRegInfo().getCompoundSource().equals("")) {
				val = CodeTableCache.getCache().getSourceDescription(getSelectedBatch().getRegInfo().getCompoundSource());
			} else if (selectedBatch.getCompound().getRegNumber() != null
					&& selectedBatch.getCompound().getRegNumber().trim().length() > 0) {
				try {
					val = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_SRC_KNWN_DEF);
				} catch (Exception e) { /* Ignored */
				}
				if (val == null)
					val = "";
				getSelectedBatch().getRegInfo().setCompoundSource(val);
				val = CodeTableCache.getCache().getSourceDescription(val);
			} else {
				try {
					val = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_SRC_UNKNWN_DEF);
				} catch (Exception e) { /* Ignored */
				}
				if (val == null)
					val = "";
				getSelectedBatch().getRegInfo().setCompoundSource(val);
				val = CodeTableCache.getCache().getSourceDescription(val);
			}
		} catch (Exception e) { /* Ignored */
		}
		compSource.setText(val);
		compSource.setToolTipText(compSource.getText());
		// source detail
		val = "";
		try {
			if (!getSelectedBatch().getRegInfo().getCompoundSourceDetail().equals("")) {
				val = CodeTableCache.getCache().getSourceDetailDescription(
						getSelectedBatch().getRegInfo().getCompoundSourceDetail());
			} else if (selectedBatch.getCompound().getRegNumber() != null
					&& selectedBatch.getCompound().getRegNumber().trim().length() > 0) {
				try {
					val = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_SRCDTL_KNWN_DEF);
				} catch (Exception e) { /* Ignored */
				}
				if (val == null)
					val = "";
				getSelectedBatch().getRegInfo().setCompoundSourceDetail(val);
				val = CodeTableCache.getCache().getSourceDetailDescription(val);
			} else {
				try {
					val = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_COMPOUND_REGISTRATION_SRCDTL_UNKNWN_DEF);
				} catch (Exception e) { /* Ignored */
				}
				if (val == null)
					val = "";
				getSelectedBatch().getRegInfo().setCompoundSourceDetail(val);
				val = CodeTableCache.getCache().getSourceDetailDescription(val);
			}
		} catch (Exception e) { /* Ignored */
		}
		sourceDetail.setText(val);
		sourceDetail.setToolTipText(sourceDetail.getText());
		// batch owner
		String name = MasterController.getGuiController().getUsersFullName(selectedBatch.getOwner());
		if (name == null || name.length() == 0)
			name = selectedBatch.getOwner();
		batchOwner.setText(name);
		// synthesized by
		name = MasterController.getGuiController().getUsersFullName(selectedBatch.getSynthesizedBy());
		if (name == null || name.length() == 0)
			name = selectedBatch.getSynthesizedBy();
		synthBy.setText(name);
		// lab site
		val = page.getSiteCode();
		try {
			val = CodeTableCache.getCache().getSiteDescription(page.getSiteCode());
		} catch (Exception e) {
		}
		labSite.setText(val);
		// batch amount made
		// amountMade.setText((new
		// Double(getSelectedBatch().getWeightAmount().getValue())).toString() +
		// " " + getSelectedBatch().getWeightAmount().getUnit());
		amountMade.setText(getSelectedBatch().getWeightAmount().GetValueForDisplay() + " "
				+ getSelectedBatch().getWeightAmount().getUnit());
		// compound state
		compState.setText(getSelectedBatch().getCompoundState());
		NumberFormat formatter = NumberFormat.getInstance();
		formatter.setMaximumFractionDigits(3);
		// batch MW
		calcBatchMW.setText(formatter.format(selectedBatch.getMolWgt()));
		// parent MW
		calcParentMW.setText(formatter.format(getSelectedBatch().getCompound().getMolWgt()));
		// purity
		val = getPurityString(getSelectedBatch());
		if (val.equals(""))
			val = "-none-";
		purity.setText(val);
		if (purity.getText().equals("-none-"))
			purity.setToolTipText(null);
		else
			purity.setToolTipText(purity.getText());
		// melting point
		val = "-none-";
		if (getSelectedBatch().getMeltPointRange() != null
				&& !(getSelectedBatch().getMeltPointRange().getLower() == 0.0 && getSelectedBatch().getMeltPointRange().getUpper() == 0.0)) {
			if (getSelectedBatch().getMeltPointRange().getLower() == getSelectedBatch().getMeltPointRange().getUpper())
				val = (new Double(getSelectedBatch().getMeltPointRange().getLower())).toString() + " deg C";
			else
				val = "between " + (new Double(getSelectedBatch().getMeltPointRange().getLower())).toString() + " and "
						+ (new Double(getSelectedBatch().getMeltPointRange().getUpper())).toString() + " deg C";
			if (getSelectedBatch().getMeltPointRange().getComment() != null
					&& getSelectedBatch().getMeltPointRange().getComment().length() > 0)
				val += ", " + getSelectedBatch().getMeltPointRange().getComment();
		}
		meltingPoint.setText(val);
		if (meltingPoint.getText().equals("-none-"))
			meltingPoint.setToolTipText(null);
		else
			meltingPoint.setToolTipText(meltingPoint.getText());
		// batch comments
		val = "-none-";
		if (getSelectedBatch().getComments() != null && getSelectedBatch().getComments().length() > 0)
			val = getSelectedBatch().getComments();
		batchComments.setText(val);
		if (batchComments.getText().equals("-none-"))
			batchComments.setToolTipText(null);
		else
			batchComments.setToolTipText(batchComments.getText());
		// therapeutic area
		val = "";
		try {
			if (page.getTaCode() != null && !page.getTaCode().equals(""))
				val = CodeTableCache.getCache().getTAsDescription(page.getTaCode());
		} catch (Exception e) {
		}
		ta_area.setText(val);
		ta_area.setToolTipText(ta_area.getText());
		// project code, name
		val = "";
		if (page.getProjectCode() != null && !page.getProjectCode().equals("")) {
			val = page.getProjectCode();
			try {
				String val2 = CodeTableCache.getCache().getProjectsDescription(val);
				if (val2 != null && val2.trim().length() > 0)
					val = val + " - " + val2;
			} catch (Exception e) {/* Ignored */
			}
		}
		if (page.getProjectAlias() != null && !page.getProjectAlias().equals("")) {
			if (!val.equals(""))
				val += " - ";
			val += "(" + page.getProjectAlias() + ")";
		}
		projectCode.setText(val);
		projectCode.setToolTipText(projectCode.getText());
		// salt code, name
		val = "";
		if (getSelectedBatch().getSaltForm().getCode() == null || getSelectedBatch().getSaltForm().getCode().equals("00"))
			val = "00";
		else
			val = getSelectedBatch().getSaltForm().getCode() + " - " + getSelectedBatch().getSaltForm().getDescription();
		saltCode.setText(val);
		if (saltCode.getText().equals("00"))
			saltCode.setToolTipText(null);
		else
			saltCode.setToolTipText(saltCode.getText());
		// equivalents of salt
		if (val.equals("00"))
			val = "";
		else
			val = (new Double(getSelectedBatch().getSaltEquivs())).toString();
		noEquivOfSalt.setText(val);
		// solubility in solvents
		ArrayList list = getSelectedBatch().getRegInfo().getSolubilitySolventList();
		if (list.size() != 0) {
			String solubilityString = new String();
			for (int i = 0; i < list.size(); i++) {
				BatchRegistrationSolubilitySolvent solubilitySolventVO = (BatchRegistrationSolubilitySolvent)list.get(i);
				if (solubilitySolventVO.isQuantitative()) {
					if (solubilityString.length() > 0) solubilityString += ", ";
					if (solubilitySolventVO.getOperator().equals("BT"))
						solubilityString += solubilitySolventVO.getOperator() + " "  + solubilitySolventVO.getSolubilityValue() + " "  + solubilitySolventVO.getSolubilityUnit() + " and " + solubilitySolventVO.getSolubilityUpperValue() + " in " + solubilitySolventVO.getCodeAndName();
					else
						solubilityString += solubilitySolventVO.getOperator() + " "  + solubilitySolventVO.getSolubilityValue() + " "  + solubilitySolventVO.getSolubilityUnit() + " in " + solubilitySolventVO.getCodeAndName();
				} else {
					if (solubilityString.length() > 0) solubilityString += ", ";
					solubilityString += solubilitySolventVO.getQualiString() + " in " + solubilitySolventVO.getCodeAndName();
				}				
			}
			solubility.setText(solubilityString);
			solubility.setToolTipText(solubility.getText());
		} else {
			solubility.setText("-none-");
			solubility.setToolTipText(null);
		}
		// residual solvents
		list = getSelectedBatch().getRegInfo().getResidualSolventList();
		if (list.size() != 0) {
			String residualString = new String();
			for (int i = 0; i < list.size(); i++) {
				BatchRegistrationResidualSolvent residualSolventVO = (BatchRegistrationResidualSolvent) list.get(i);
				if (residualString.length() > 0)
					residualString += ", ";
				residualString += residualSolventVO.getEqOfSolvent() + " mols of " + residualSolventVO.getResidualDescription();
			}
			residualSolvents.setText(residualString);
			residualSolvents.setToolTipText(residualSolvents.getText());
		} else {
			residualSolvents.setText("-none-");
			residualSolvents.setToolTipText(null);
		}
		// //compound color
		// if(registrationInfoVO.getCompoundColor() != null &&
		// !registrationInfoVO.getCompoundColor().equals("")) {
		// compoundColor.setText(registrationInfoVO.getCompoundColor());
		// } else {
		// compoundColor.setText("-none-");
		// }
		// compoundColor.setText("-none-");
		// batch MF
		batchMF.setText(getSelectedBatch().getMolecularFormula());
		batchMF.setToolTipText(batchMF.getText());
		// parent MF
		parentMF.setText(getSelectedBatch().getCompound().getMolFormula());
		parentMF.setToolTipText(parentMF.getText());
		// precursors
		List<String> precursorList = getSelectedBatch().getPrecursors();
		String precursorString = new String();
		if (precursorList != null) {
			if (precursorList.size() > 0) {
				for (int i = 0; i < precursorList.size() - 1; i++)
					precursorString += precursorList.get(i) + ", ";
				precursorString += precursorList.get(precursorList.size() - 1);
			}
		}
		if (precursorString.equals("")) {
			precursor.setText("-none-");
			precursor.setToolTipText(null);
		} else {
			precursor.setText(precursorString);
			precursor.setToolTipText(precursor.getText());
		}
		// hit-id
		if (getSelectedBatch().getRegInfo().getHitId() != null && !getSelectedBatch().getRegInfo().getHitId().equals("")) {
			hit_id.setText(getSelectedBatch().getRegInfo().getHitId());
			hit_id.setToolTipText(hit_id.getText());
		} else {
			hit_id.setText("-none-");
			hit_id.setToolTipText(null);
		}
		// external suppliers
		if (getSelectedBatch().getVendorInfo() != null && getSelectedBatch().getVendorInfo().toString().length() > 0) {
			extSupplier.setText(getSelectedBatch().getVendorInfo().toString());
			extSupplier.setToolTipText(extSupplier.getText());
		} else {
			extSupplier.setText("-none-");
			extSupplier.setToolTipText(null);
		}
	} // end updateDisplay

	private String getPurityString(ProductBatchModel selectedBatch) {
		String purityString = new String();
		if (selectedBatch.getAnalyticalPurityList() != null && selectedBatch.getAnalyticalPurityList().size() > 0) {
			String operatorString = new String();
			String valueString = new String();
			String codeString = new String();
			for (int i = 0; i < selectedBatch.getAnalyticalPurityList().size(); i++) {
				PurityModel pur = (PurityModel) selectedBatch.getAnalyticalPurityList().get(i);
				if (i != 0) {
					operatorString = pur.getOperator();
					valueString = pur.getPurityValue().toString();
					codeString = pur.getCode();
					purityString += "; " + codeString + " " + operatorString + " " + valueString;
				} else {
					operatorString = pur.getOperator();
					valueString = pur.getPurityValue() + "";
					codeString = pur.getCode();
					purityString += codeString + " " + operatorString + " " + valueString;
				}
			}
		}
		return purityString;
	}

	/** Auto-generated main method */
	public static void main(String[] args) {
		showGUI();
	}

	/**
	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
	 * delete this method it will not be re-created.
	 */
	public static void showGUI() {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			CompoundRegInfoContainer inst = new CompoundRegInfoContainer();
			frame.setContentPane(inst);
			frame.getContentPane().setSize(inst.getSize());
			frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Returns the parentDialog.
	 */
	public NotebookPageGuiInterface getParentDialog() {
		return parentDialog;
	}

	/**
	 * @param parentDialog
	 *            The parentDialog to set.
	 */
	public void setParentDialog(NotebookPageGuiInterface parentDialog) {
		this.parentDialog = parentDialog;
	}

	/** Auto-generated event handler method */
	protected void jPanel8MouseClicked(MouseEvent evt) {
		// if right click, then launch edit dialog
		if (evt.getClickCount() == 2 || evt.getButton() == MouseEvent.BUTTON3)
			processMouseRightClicked(evt);
	}

	private void processMouseRightClicked(MouseEvent evt) {
		if (isEditable()) {
			if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.COMPOUND_REGISTRATION_JOB_STATUS_PENDING)) {
				JOptionPane.showMessageDialog((JInternalFrame)parentDialog, "The selected batch is performing the registration process.");
			} else if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMITTED + " - " + CeNConstants.REGINFO_PASSED)) {
				JOptionPane.showMessageDialog((JInternalFrame)parentDialog, "The selected batch is already registered.");
			} else {
				jDialogEditCompRegInfo = new JDialogEditCompRegInfo(MasterController.getGuiController().getGUIComponent());
				Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
				Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
				jDialogEditCompRegInfo.setLocation(loc.x + (dim.width - jDialogEditCompRegInfo.getSize().width) / 2, loc.y
						+ (dim.height - jDialogEditCompRegInfo.getSize().height) / 2);
				jDialogEditCompRegInfo.setSelectedBatch(selectedBatch);
				jDialogEditCompRegInfo.updateDisplay();
				jDialogEditCompRegInfo.setCompoundRegInfoContainer(this);
				jDialogEditCompRegInfo.setVisible(true);
			}
		} else {
			JOptionPane.showMessageDialog(MasterController.getGuiController().getGUIComponent(), "The batch is currently not editable.");
		}
	}

	/**
	 * @return Returns the selectedBatch.
	 */
	public ProductBatchModel getSelectedBatch() {
		return selectedBatch;
	}

	/**
	 * @param selectedBatch
	 *            The selectedBatch to set.
	 */
	public void setSelectedBatch(ProductBatchModel selectedBatch) {
		this.selectedBatch = selectedBatch;
		jButtonEditInfo.setEnabled(getSelectedBatch() != null && isEditable());
	}

	public void updateBatchList(ProductBatchModel sBatch) {
		SingletonRegistration_SummaryViewContainer singletonRegistration_SummaryViewContainer = ((SingletonNotebookPageGUI)getParentDialog()).getRegSubSum_cont();
		singletonRegistration_SummaryViewContainer.updateBatchList(sBatch);
	}

	private boolean isEditable() {
		NotebookPageModel page = (NotebookPageModel) parentDialog.getPageModel();
		return CommonUtils.getProductBatchModelEditableFlag(selectedBatch, page);
	}
}