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

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.page.SingletonNotebookPageGUI;
import com.chemistry.enotebook.client.gui.page.reagents.ProgressBarDialog;
import com.chemistry.enotebook.client.gui.page.regis_submis.uc.JDialogUniquenessCheck;
import com.chemistry.enotebook.client.gui.page.regis_submis.uc.UniquenessCheckTableModel;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.formatter.UtilsDispatcher;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.StructureLoadAndConversionUtil;
import com.chemistry.enotebook.utils.SwingWorker;
import com.chemistry.enotebook.utils.sdf.SdUnit;
import com.chemistry.enotebook.vnv.classes.UcCompoundInfo;
import com.chemistry.viewer.ChemistryViewer;
import com.cloudgarden.layout.AnchorConstraint;
import com.cloudgarden.layout.AnchorLayout;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class StructureVnVContainer extends javax.swing.JScrollPane {
	
	private static final long serialVersionUID = -1666289268405860774L;
	
	private JTextField jTextFieldPFNum;
	private JLabel jLabelPFNum;
	private JTextField jTextFieldSaltEqul;
	private JLabel jLabelSaltEqual;
	private JTextField jTextFieldParent;
	private JLabel jLabelParent;
	private JTextField jTextFieldBatch;
	private JLabel jLabel6;
	private ChemistryPanel VnVchimeProSwing;
	private JLabel jLabelErrorMsg;
	private JTextArea jTextAreaErrorMessage;
	private CeNComboBox jComboBoxSICBatch;
	private CeNComboBox jComboBoxVnVSaltCode;
	private JLabel jLabel9;
	private JLabel jLabel8;
	private JTextArea jTextAreaStruComments;
	private JLabel jLabel5;
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JPanel jPanelBatchStructure;
	private JPanel jPanelVnvStructure;
	private JButton jButtonVnV;
	private JLabel jLabel4;
	private CeNComboBox jComboBoxStereoIsoCodeResult;
	private JPanel jPanel2;
	private ChemistryViewer structQueryCanvas;
	private HashMap<String, String> sicMap;
	private Map<String, String> saltCodeMap;
	private RegSubHandler regSubHandler = new RegSubHandler();
	private NotebookPageGuiInterface parentDialog;
	private ProductBatchModel selectedBatch;
	public static String SELECT_SALT_CODE_ALERT = "Batch structure, MF, MW different from parent structure, MW, MF.";
	private VnVResultVO vnVResultVO;
	// private HashMap vnvStructures = new HashMap();
	private ProgressBarDialog progressBarDialog = null;
	private boolean isInit = false;

	public StructureVnVContainer() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanel2 = new JPanel();
			jTextFieldPFNum = new JTextField();
			jLabelPFNum = new JLabel();
			jTextFieldSaltEqul = new JTextField();
			jLabelSaltEqual = new JLabel();
			jTextFieldParent = new JTextField();
			jLabelParent = new JLabel();
			jTextFieldBatch = new JTextField();
			jLabelErrorMsg = new JLabel();
			jTextAreaErrorMessage = new JTextArea();
			jComboBoxStereoIsoCodeResult = new CeNComboBox();
			jLabel4 = new JLabel();
			jButtonVnV = new JButton();
			jPanelVnvStructure = new JPanel();
			VnVchimeProSwing = new ChemistryPanel();
			jPanelBatchStructure = new JPanel();
			jLabel2 = new JLabel();
			jLabel1 = new JLabel();
			jTextAreaStruComments = new JTextArea();
			jLabel8 = new JLabel();
			jLabel9 = new JLabel();
			jComboBoxVnVSaltCode = new CeNComboBox();
			jComboBoxSICBatch = new CeNComboBox();
			jLabel6 = new JLabel();
			jLabel5 = new JLabel();
			this.setPreferredSize(new java.awt.Dimension(540, 405));
			AnchorLayout jPanel2Layout = new AnchorLayout();
			jPanel2.setLayout(jPanel2Layout);
			jPanel2.setPreferredSize(new java.awt.Dimension(536, 401));
			this.add(jPanel2);
			this.setViewportView(jPanel2);
			jTextFieldPFNum.setVisible(true);
			jTextFieldPFNum.setEnabled(false);
			jTextFieldPFNum.setPreferredSize(new java.awt.Dimension(121, 22));
			jTextFieldPFNum.setBounds(new java.awt.Rectangle(390, 359, 121, 22));
			jPanel2.add(jTextFieldPFNum, new AnchorConstraint(896, 954, 951, 728, 1, 1, 1, 1));
			jTextFieldPFNum.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jTextFieldPFNumActionPerformed(evt);
				}
			});
			jLabelPFNum.setText("Compound #:");
			jLabelPFNum.setVisible(true);
			jLabelPFNum.setFont(new java.awt.Font("Sansserif", 1, 11));
			jLabelPFNum.setPreferredSize(new java.awt.Dimension(120, 20));
			jLabelPFNum.setBounds(new java.awt.Rectangle(281, 358, 120, 20));
			jPanel2.add(jLabelPFNum, new AnchorConstraint(894, 700, 943, 525, 1, 1, 1, 1));
			jTextFieldSaltEqul.setEditable(false);
			jTextFieldSaltEqul.setEnabled(false);
			jTextFieldSaltEqul.setVisible(false);
			jTextFieldSaltEqul.setPreferredSize(new java.awt.Dimension(119, 20));
			jTextFieldSaltEqul.setSize(new java.awt.Dimension(119, 20));
			jTextFieldSaltEqul.setBounds(new java.awt.Rectangle(392, 374, 119, 20));
			jPanel2.add(jTextFieldSaltEqul, new AnchorConstraint(933, 954, 983, 732, 1, 1, 1, 1));
			jLabelSaltEqual.setText("# Equivalents of Salt:");
			jLabelSaltEqual.setVisible(false);
			jLabelSaltEqual.setFont(new java.awt.Font("Sansserif", 1, 11));
			jLabelSaltEqual.setPreferredSize(new java.awt.Dimension(99, 20));
			jLabelSaltEqual.setBounds(new java.awt.Rectangle(280, 370, 99, 20));
			jPanel2.add(jLabelSaltEqual, new AnchorConstraint(923, 708, 973, 523, 1, 1, 1, 1));
			jTextFieldParent.setEditable(false);
			jTextFieldParent.setVisible(true);
			jTextFieldParent.setPreferredSize(new java.awt.Dimension(120, 20));
			jTextFieldParent.setSize(new java.awt.Dimension(120, 20));
			jTextFieldParent.setBounds(new java.awt.Rectangle(390, 282, 120, 20));
			jPanel2.add(jTextFieldParent, new AnchorConstraint(704, 952, 754, 728, 1, 1, 1, 1));
			jLabelParent.setText("Parent MW, MF:");
			jLabelParent.setVisible(true);
			jLabelParent.setFont(new java.awt.Font("Sansserif", 1, 11));
			jLabelParent.setPreferredSize(new java.awt.Dimension(82, 20));
			jLabelParent.setBounds(new java.awt.Rectangle(281, 283, 82, 20));
			jPanel2.add(jLabelParent, new AnchorConstraint(706, 678, 756, 525, 1, 1, 1, 1));
			jTextFieldBatch.setEditable(false);
			jTextFieldBatch.setVisible(true);
			jTextFieldBatch.setPreferredSize(new java.awt.Dimension(142, 20));
			jTextFieldBatch.setBounds(new java.awt.Rectangle(110, 288, 142, 20));
			jPanel2.add(jTextFieldBatch, new AnchorConstraint(719, 471, 769, 206, 1, 1, 1, 1));
			jLabelErrorMsg.setText(" VnV Results / Error Messages:");
			jLabelErrorMsg.setVisible(true);
			jLabelErrorMsg.setFont(new java.awt.Font("Sansserif", 1, 11));
			jLabelErrorMsg.setPreferredSize(new java.awt.Dimension(229, 20));
			jLabelErrorMsg.setRequestFocusEnabled(false);
			jLabelErrorMsg.setSize(new java.awt.Dimension(229, 20));
			jLabelErrorMsg.setBounds(new java.awt.Rectangle(10, 310, 229, 20));
			jPanel2.add(jLabelErrorMsg, new AnchorConstraint(774, 446, 824, 19, 1, 1, 1, 1));
			jTextAreaErrorMessage.setEditable(false);
			jTextAreaErrorMessage.setVisible(true);
			jTextAreaErrorMessage.setLineWrap(true);
			jTextAreaErrorMessage.setPreferredSize(new java.awt.Dimension(244, 59));
			jTextAreaErrorMessage.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jTextAreaErrorMessage.setBounds(new java.awt.Rectangle(10, 334, 244, 59));
			JScrollPane resultScrollingArea = new JScrollPane(jTextAreaErrorMessage);
			resultScrollingArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jPanel2.add(resultScrollingArea, new AnchorConstraint(834, 474, 981, 19, 1, 1, 1, 1));
			// jPanel2.add(jTextAreaErrorMessage, new
			// AnchorConstraint(834,474, 981, 19, 1, 1, 1, 1));
			jComboBoxStereoIsoCodeResult.setVisible(true);
			jComboBoxStereoIsoCodeResult.setPreferredSize(new java.awt.Dimension(121, 21));
			jComboBoxStereoIsoCodeResult.setBounds(new java.awt.Rectangle(390, 253, 121, 21));
			jPanel2.add(jComboBoxStereoIsoCodeResult, new AnchorConstraint(632, 954, 684, 728, 1, 1, 1, 1));
			jComboBoxStereoIsoCodeResult.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					jComboBoxStereoIsoCodeResultItemStateChanged(evt);
				}
			});
			jLabel4.setText("Batch MW, MF:");
			jLabel4.setVisible(true);
			jLabel4.setFont(new java.awt.Font("Sansserif", 1, 11));
			jLabel4.setPreferredSize(new java.awt.Dimension(76, 21));
			jLabel4.setRequestFocusEnabled(false);
			jLabel4.setBounds(new java.awt.Rectangle(9, 286, 76, 21));
			jPanel2.add(jLabel4, new AnchorConstraint(714, 159, 766, 17, 1, 1, 1, 1));
			jButtonVnV.setText("<HTML><p align='center'><span style='font-size:7.0pt;font-family:Sansserif'><font color='green'><b>VnV<BR>&<BR>Novelty Check</b></font></span></p></HTML>");
			jButtonVnV.setVisible(true);
			jButtonVnV.setOpaque(false);
			jPanel2.add(jButtonVnV, new AnchorConstraint(405, 545, 585, 435, 1, 1, 1, 1));
			jButtonVnV.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonVnVActionPerformed(evt);
				}
			});
			BorderLayout jPanelVnvStructureLayout = new BorderLayout();
			jPanelVnvStructure.setLayout(jPanelVnvStructureLayout);
			jPanelVnvStructureLayout.setHgap(0);
			jPanelVnvStructureLayout.setVgap(0);
			jPanelVnvStructure.setVisible(true);
			jPanelVnvStructure.setBackground(new java.awt.Color(255, 255, 255));
			jPanelVnvStructure.setPreferredSize(new java.awt.Dimension(236, 219));
			jPanelVnvStructure.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelVnvStructure.setSize(new java.awt.Dimension(236, 214));
			jPanelVnvStructure.setBounds(new java.awt.Rectangle(276, 24, 236, 214));
			jPanel2.add(jPanelVnvStructure, new AnchorConstraint(62, 956, 596, 515, 1, 1, 1, 1));
			jPanelVnvStructure.add(VnVchimeProSwing, BorderLayout.CENTER);
			BorderLayout jPanelBatchStructureLayout = new BorderLayout();
			jPanelBatchStructure.setLayout(jPanelBatchStructureLayout);
			jPanelBatchStructureLayout.setHgap(0);
			jPanelBatchStructureLayout.setVgap(0);
			jPanelBatchStructure.setVisible(true);
			jPanelBatchStructure.setBackground(new java.awt.Color(255, 255, 255));
			jPanelBatchStructure.setPreferredSize(new java.awt.Dimension(237, 219));
			jPanelBatchStructure.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelBatchStructure.setBounds(new java.awt.Rectangle(10, 26, 237, 215));
			jPanel2.add(jPanelBatchStructure, new AnchorConstraint(67, 461, 601, 19, 1, 1, 1, 1));
			jLabel2.setText(" CompoundRegistration-Validated Parent Structure:"); // ,
			// Salt
			// Code/Name:");
			jLabel2.setVisible(true);
			jLabel2.setFont(new java.awt.Font("Dialog", 1, 10));
			jLabel2.setPreferredSize(new java.awt.Dimension(281, 25));
			jLabel2.setBounds(new java.awt.Rectangle(270, 1, 281, 25));
			jPanel2.add(jLabel2, new AnchorConstraint(3, 1028, 66, 504, 1, 1, 1, 1));
			jLabel1.setText(" Validate & Verify Your Product Batch Structure:");
			jLabel1.setVisible(true);
			jLabel1.setFont(new java.awt.Font("Dialog", 1, 10));
			jLabel1.setPreferredSize(new java.awt.Dimension(249, 27));
			jLabel1.setBounds(new java.awt.Rectangle(0, 0, 249, 27));
			jPanel2.add(jLabel1, new AnchorConstraint(1, 465, 69, 0, 1, 1, 1, 1));
			jTextAreaStruComments.setPreferredSize(new java.awt.Dimension(120, 128));
			jTextAreaStruComments.setAutoscrolls(false);
			jTextAreaStruComments.setLineWrap(true);
			jTextAreaStruComments.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jTextAreaStruComments.setSize(new java.awt.Dimension(120, 64));
			jTextAreaStruComments.setBounds(new java.awt.Rectangle(390, 311, 120, 64));
			JScrollPane scrollingArea = new JScrollPane(jTextAreaStruComments);
			scrollingArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jPanel2.add(scrollingArea, new AnchorConstraint(776, 952, 854, 728, 1, 1, 1, 1));
			jTextAreaStruComments.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					if (!isInit && selectedBatch != null && selectedBatch.getCompound() != null)
						selectedBatch.getCompound().setStructureComments(jTextAreaStruComments.getText().trim());
				}

				public void removeUpdate(DocumentEvent e) {
					if (!isInit && selectedBatch != null && selectedBatch.getCompound() != null)
						selectedBatch.getCompound().setStructureComments(jTextAreaStruComments.getText().trim());
				}

				public void changedUpdate(DocumentEvent e) {
					if (!isInit && selectedBatch != null && selectedBatch.getCompound() != null)
						selectedBatch.getCompound().setStructureComments(jTextAreaStruComments.getText().trim());
				}
			});
			jLabel8.setText("Stereoisomer Code:");
			jLabel8.setFont(new java.awt.Font("Sansserif", 1, 11));
			jLabel8.setPreferredSize(new java.awt.Dimension(120, 20));
			jLabel8.setBounds(new java.awt.Rectangle(280, 256, 120, 20));
			jPanel2.add(jLabel8, new AnchorConstraint(639, 722, 689, 523, 1, 1, 1, 1));
			jLabel9.setText("Salt code/name:");
			jLabel9.setVisible(false);
			jLabel9.setFont(new java.awt.Font("Sansserif", 1, 11));
			jLabel9.setPreferredSize(new java.awt.Dimension(97, 21));
			jLabel9.setBounds(new java.awt.Rectangle(280, 348, 97, 21));
			jPanel2.add(jLabel9, new AnchorConstraint(869, 704, 921, 523, 1, 1, 1, 1));
			jComboBoxVnVSaltCode.setEnabled(true);
			jComboBoxVnVSaltCode.setEditable(true);
			jComboBoxVnVSaltCode.setVisible(false);
			jComboBoxVnVSaltCode.setPreferredSize(new java.awt.Dimension(120, 20));
			jComboBoxVnVSaltCode.setSize(new java.awt.Dimension(120, 20));
			jComboBoxVnVSaltCode.setBounds(new java.awt.Rectangle(391, 348, 120, 20));
			jPanel2.add(jComboBoxVnVSaltCode, new AnchorConstraint(869, 954, 918, 730, 1, 1, 1, 1));
			jComboBoxSICBatch.setVisible(true);
			jComboBoxSICBatch.setPreferredSize(new java.awt.Dimension(142, 20));
			jComboBoxSICBatch.setSize(new java.awt.Dimension(142, 21));
			jComboBoxSICBatch.setBounds(new java.awt.Rectangle(110, 257, 142, 21));
			jPanel2.add(jComboBoxSICBatch, new AnchorConstraint(642, 472, 694, 206, 1, 1, 1, 1));
			jComboBoxSICBatch.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent evt) {
					jComboBoxSICBatchItemStateChanged(evt);
				}
			});
			jLabel6.setText("Stereoisomer Code:");
			jLabel6.setVisible(true);
			jLabel6.setFont(new java.awt.Font("Sansserif", 1, 11));
			jLabel6.setPreferredSize(new java.awt.Dimension(120, 21));
			jLabel6.setRequestFocusEnabled(false);
			jLabel6.setBounds(new java.awt.Rectangle(9, 256, 120, 21));
			jPanel2.add(jLabel6, new AnchorConstraint(639, 215, 692, 17, 1, 1, 1, 1));
			jLabel5.setText(" Structure Comments:");
			jLabel5.setFont(new java.awt.Font("Sansserif", 1, 11));
			jLabel5.setPreferredSize(new java.awt.Dimension(120, 20));
			jLabel5.setBounds(new java.awt.Rectangle(278, 317, 120, 20));
			jPanel2.add(jLabel5, new AnchorConstraint(791, 720, 841, 519, 1, 1, 1, 1));
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
		try {
			structQueryCanvas = new ChemistryViewer(MasterController.getGUIComponent().getTitle(), "Structure");
			structQueryCanvas.setReadOnly(true);
			jPanelBatchStructure.add(structQueryCanvas, BorderLayout.CENTER);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		// populate drop downs
		setSaltCodeMap(RegCodeMaps.getInstance().getSaltCodeMap());
		sicMap = RegCodeMaps.getInstance().getStereoisomerCodeMap();
		populateDropDowns();
	}

	public void populateDropDowns() {
		// populate salt code / name drop downs
		// jComboBoxVnVSaltCode.removeAllItems();
		// Iterator scIterator = saltCodeMap.keySet().iterator();
		// while(scIterator.hasNext()){
		// String saltCode = (String)scIterator.next();
		// String saltName = (String)saltCodeMap.get(saltCode);
		// jComboBoxVnVSaltCode.addItem(saltCode + " - " + saltName);
		// }
		// populate stereoisomer code / name drop downs
		CodeTableUtils.fillComboBoxWithIsomers(jComboBoxSICBatch);
	}

	public String getSelectedSIC() {
		String selectedItem = (String) jComboBoxSICBatch.getSelectedItem();
		return (selectedItem == null) ? "" : selectedItem.substring(0, 5);
	}

	public void updateBatchDisplay() {
		isInit = true;
		boolean isEditable = isEditable();
		jComboBoxSICBatch.setEnabled(isEditable);
		jComboBoxStereoIsoCodeResult.setEnabled(isEditable);
		jTextFieldBatch.setEnabled(isEditable);
		jTextFieldParent.setEnabled(isEditable);
		jTextAreaErrorMessage.setEnabled(isEditable);
		jTextAreaStruComments.setEnabled(isEditable);
		jButtonVnV.setEnabled(isEditable);
		// show structure
		structQueryCanvas.setChemistry(selectedBatch.getCompound().getNativeSketch());
		ParentCompoundModel c = selectedBatch.getCompound();
		if (StringUtils.isNotBlank(c.getStereoisomerCode())) {
			jComboBoxSICBatch.setSelectedItem(c.getStereoisomerCode() + " - " + sicMap.get(c.getStereoisomerCode()));
		} else {
			jComboBoxSICBatch.setSelectedItem("HSREG" + " - " + sicMap.get("HSREG"));
		}
		// show batch MW nd MF
		if (selectedBatch.getMolWgt() == 0 && StringUtils.isBlank(selectedBatch.getMolecularFormula())) {
			jTextFieldBatch.setText("");
		} else {
			NumberFormat formatter = NumberFormat.getInstance();
			formatter.setMaximumFractionDigits(3);
			jTextFieldBatch.setText(formatter.format(selectedBatch.getMolWgt()) + ", " + selectedBatch.getMolecularFormula());
		}
		// set structure comments
		if (StringUtils.isNotBlank(selectedBatch.getCompound().getStructureComments())) {
			jTextAreaStruComments.setText(selectedBatch.getCompound().getStructureComments());
		} else {
			jTextAreaStruComments.setText("");
		}
		jTextAreaStruComments.setCaretPosition(0);
		// Jags_todo... Check the VnV status that gets assigned from server side and update below code.
		if (selectedBatch.getRegInfo().getBatchVnVInfo().isPassed()) {
			// update structure
			if (StringUtils.isNotBlank(selectedBatch.getRegInfo().getBatchVnVInfo().getMolData())) {
				VnVchimeProSwing.setMolfileData(selectedBatch.getRegInfo().getBatchVnVInfo().getMolData());
			} else {
				VnVchimeProSwing.setMolfileData("");
			}
			jComboBoxStereoIsoCodeResult.removeAllItems();
			ArrayList<String> sicList = selectedBatch.getRegInfo().getBatchVnVInfo().getSuggestedSICList();
			if (sicList.size() > 0) {
				int selectedSIC = -1;
				String sicode = "";
				for (int i = 0; i < sicList.size(); i++) {
					sicode = sicList.get(i);
					if (sicode.equals(selectedBatch.getCompound().getStereoisomerCode())) {
						selectedSIC = i;
					}
					jComboBoxStereoIsoCodeResult.addItem(sicode + " - " + sicMap.get(sicode));
				}
				jComboBoxStereoIsoCodeResult.setSelectedIndex(selectedSIC);
				jComboBoxSICBatch.setSelectedItem((String) jComboBoxStereoIsoCodeResult.getSelectedItem());
			} else {
				jComboBoxStereoIsoCodeResult.addItem(selectedBatch.getCompound().getStereoisomerCode() + " - "
						+ sicMap.get(selectedBatch.getCompound().getStereoisomerCode()));
				jComboBoxSICBatch.setSelectedItem((String) jComboBoxStereoIsoCodeResult.getSelectedItem());
			}
			if (selectedBatch.getCompound().getMolWgt() == 0 && StringUtils.isBlank(selectedBatch.getCompound().getMolFormula())) {
				jTextFieldParent.setText("");
			} else {
				NumberFormat formatter = NumberFormat.getInstance();
				formatter.setMaximumFractionDigits(3);
				jTextFieldParent.setText(formatter.format(selectedBatch.getCompound().getMolWgt()) + ", "
						+ selectedBatch.getCompound().getMolFormula());
			}
			jTextAreaErrorMessage.setText(selectedBatch.getRegInfo().getBatchVnVInfo().getErrorMsg());
			jTextAreaErrorMessage.setCaretPosition(0);
		} else {
			jComboBoxStereoIsoCodeResult.removeAllItems();
			jTextAreaErrorMessage.setText("");
			jTextFieldParent.setText("");
			VnVchimeProSwing.setMolfileData("");
		}
		if (StringUtils.isNotBlank(selectedBatch.getCompound().getRegNumber())) {
			jTextFieldPFNum.setText(selectedBatch.getCompound().getRegNumber());
		} else {
			jTextFieldPFNum.setText("");
		}
		jTextFieldBatch.requestFocus();
		jButtonVnV.requestFocus();
		isInit = false;
	}

	public boolean showVnVResult() {
		boolean result = true;
		// update structure
		VnVchimeProSwing.setMolfileData(vnVResultVO.getResultStructureString());
		jTextFieldBatch.requestFocus();
		jButtonVnV.requestFocus();
		// update sic
		jComboBoxStereoIsoCodeResult.removeAllItems();
		ArrayList<String> sicList = vnVResultVO.getSicList();
		if (sicList.size() > 0) {
			int selectedSIC = -1;
			int defaultSIC = -1;
			String sicode = "";
			for (int i = 0; i < sicList.size(); i++) {
				sicode = sicList.get(i);
				if (!getSelectedSIC().equals("HSREG")) {
					if (sicode.endsWith(getSelectedSIC()))
						selectedSIC = i;
					if (sicode.endsWith(vnVResultVO.getValidSIC()))
						defaultSIC = i;
				} else if (!vnVResultVO.getValidSIC().startsWith("ERROR")) {
					if (sicode.endsWith(vnVResultVO.getValidSIC()))
						selectedSIC = i;
				}
				jComboBoxStereoIsoCodeResult.addItem(sicode + " - " + sicMap.get(sicode));
			}
			if (selectedSIC == -1 && defaultSIC > 0) {
				jComboBoxStereoIsoCodeResult.setSelectedIndex(defaultSIC);
			} else {
				jComboBoxStereoIsoCodeResult.setSelectedIndex(selectedSIC);
			}
			jComboBoxSICBatch.setSelectedItem((String) jComboBoxStereoIsoCodeResult.getSelectedItem());
			result = !vnVResultVO.getValidSIC().startsWith("ERROR");
		} else {
			if (StringUtils.isNotBlank(vnVResultVO.getValidSIC())) {
				// if( !getSelectedSIC().equals("HSREG")){
				// jComboBoxStereoIsoCodeResult.addItem(getSelectedSIC() + " - "
				// + sicMap.get(getSelectedSIC()));
				// jComboBoxSICBatch.setSelectedItem((String)jComboBoxStereoIsoCodeResult.getSelectedItem());
				// }else{
				jComboBoxStereoIsoCodeResult.addItem(vnVResultVO.getValidSIC() + " - " + sicMap.get(vnVResultVO.getValidSIC()));
				jComboBoxSICBatch.setSelectedItem((String) jComboBoxStereoIsoCodeResult.getSelectedItem());
				// }
			}
		}
		// update MF, MW
		jTextFieldParent.setText(vnVResultVO.getMolWeight() + ", " + vnVResultVO.getMolFormula());
		// update message
		// check if VnV structure, or Mw, or MF are different than that of batch
		if (compareBatchVnV()) {
			jTextAreaErrorMessage.setText(vnVResultVO.getErrorMessage() + "\n" + StructureVnVContainer.SELECT_SALT_CODE_ALERT);
		} else {
			jTextAreaErrorMessage.setText(vnVResultVO.getErrorMessage());
		}
		jTextAreaErrorMessage.setCaretPosition(0);
		return result;
	}

	public boolean compareBatchVnV() {
		ParentCompoundModel c = selectedBatch.getCompound();
		return Math.round(vnVResultVO.getMolWeight() * 100) != Math.round(c.getMolWgt() * 100)
				|| !removeWhites(vnVResultVO.getMolFormula()).equals(removeWhites(c.getMolFormula()));
	}

	private String removeWhites(String s) {
		StringBuffer stringbuffer = new StringBuffer();
		char ac[] = s.toCharArray();
		for (int i = 0; i < ac.length; i++)
			if (ac[i] > ' ')
				stringbuffer.append(ac[i]);
		return stringbuffer.toString();
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
			StructureVnVContainer inst = new StructureVnVContainer();
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
	 * @return Returns the saltCodeMap.
	 */
	public Map<String, String> getSaltCodeMap() {
		return saltCodeMap;
	}

	/**
	 * @param saltCodeMap
	 *            The saltCodeMap to set.
	 */
	public void setSaltCodeMap(Map<String, String> saltCodeMap) {
		this.saltCodeMap = saltCodeMap;
	}

	/** Auto-generated event handler method */
	protected void jButtonVnVActionPerformed(ActionEvent evt) {
		if (!isEditable()) {
			JOptionPane.showMessageDialog((JInternalFrame) parentDialog, "The batch is currently not editable.");
		} else if (getSelectedBatch() != null) {
			if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMISION_PENDING)) {
				JOptionPane.showMessageDialog((JInternalFrame) parentDialog,
						"The selected batch is currently in the process of registration.");
			} else if (selectedBatch.getRegInfo().getCompoundRegistrationStatus().equals(CeNConstants.REGINFO_SUBMISION_PASS)) {
				JOptionPane.showMessageDialog((JInternalFrame) parentDialog, "The selected batch is already registered.");
			} else {
				if (structQueryCanvas.getChemistry() == null) {
					JOptionPane.showMessageDialog((JInternalFrame) parentDialog, "A structure is required to perform VnV.");
				} else {
					progressBarDialog = new ProgressBarDialog(MasterController.getGUIComponent());
					progressBarDialog.setTitle("Performing VnV, please wait ...");
					performVnV(true);
					progressBarDialog.setVisible(true);
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please select a batch first.");
		}
	}

	public void performVnV(final boolean doUC) {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				Boolean result = new Boolean(false);
				Object objLatch = new Object();
				try {
					String vnvSDFile = buildVnVSDFIle();
					if (StringUtils.isNotBlank(vnvSDFile)) {
						String vnvResult = regSubHandler.doVnV(vnvSDFile, getSelectedSIC());
						// System.out.println("The vnv result is: " + vnvResult);
						if (vnvResult != null) {
							String vnvErrorMsg = regSubHandler.ifVnVFailed(vnvResult);
							if (vnvErrorMsg.equals("NO")) {
								String vnvResultCheck = regSubHandler.ifVnVValid(vnvResult);
								if (vnvResultCheck.equals("")) {
									StructureVnVContainer.this.setVnVResultVO(regSubHandler.processVnvResult(vnvResult));
									// updateBatchStructure();
									// update BatchRegistrationInfo
									StructureVnVContainer.this.updateRegInfo(vnVResultVO);
									javax.swing.SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											// update registration
											// info display
											((SingletonNotebookPageGUI) StructureVnVContainer.this.getParentDialog())
													.getCompReg_cont().setSelectedBatch(
															StructureVnVContainer.this.getSelectedBatch());
											((SingletonNotebookPageGUI) StructureVnVContainer.this.getParentDialog())
													.getCompReg_cont().updateBatchList(
															StructureVnVContainer.this.getSelectedBatch());
											boolean stillDoUC = showVnVResult();
											if (doUC && stillDoUC)
												SubmitForUniquenessCheck();
											else {
												progressBarDialog.setVisible(false);
												progressBarDialog.dispose();
												if (!stillDoUC)
													JOptionPane.showMessageDialog(
																	StructureVnVContainer.this,
																	"VnV result: Could not determine a default Stereoisomer Code.\n"
																			+ "                   See VnV Results / Error Messages for details.\n"
																			+ "                   You may need to specify an actual SIC instead of HSREG.");
											}
										}
									});
									result = new Boolean(true);
								} else {
									progressBarDialog.setVisible(false);
									progressBarDialog.dispose();
									JOptionPane.showMessageDialog(StructureVnVContainer.this,
											"VnV result: Structure failed validation.\n" + "                   " + vnvResultCheck);
								}
							} else {
								progressBarDialog.setVisible(false);
								progressBarDialog.dispose();
								JOptionPane.showMessageDialog(StructureVnVContainer.this, vnvErrorMsg);
							}
						}
					}
				} catch (Exception e) {
					progressBarDialog.setVisible(false);
					progressBarDialog.dispose();
					CeNErrorHandler.getInstance().logErrorMsg(null, e.toString(), "External service is down.",
							JOptionPane.INFORMATION_MESSAGE);
				} finally {
					synchronized (objLatch) {
						objLatch.notify();
					}
				}
				return result;
			}

			public void finished() {
				Boolean result = (Boolean) get();
				if (!result.booleanValue()) {
					progressBarDialog.setVisible(false);
					progressBarDialog.dispose();
				}
				updateBatchDisplay();
				// /** TODO Bo Yang 3/14/07
				((SingletonNotebookPageGUI) StructureVnVContainer.this.getParentDialog()).getCompReg_cont().updateDisplay();
				// */
			}
		};
		worker.start();
	}

	private void SubmitForUniquenessCheck() {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				UniquenessCheckTableModel model = null;
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						progressBarDialog.setTitle("Performing Novelty Check, Please Wait ...");
					}
				});
				ParentCompoundModel cp = getSelectedBatch().getCompound();
				model = new UniquenessCheckTableModel(getSelectedBatch().getRegInfo().getBatchVnVInfo().getMolData().getBytes(), cp
						.getStereoisomerCode(), cp.getMolWgt(), cp.getMolFormula(), cp.getComments(), true);
				return model;
			}

			public void finished() {
				UniquenessCheckTableModel model = (UniquenessCheckTableModel) get();
				progressBarDialog.setVisible(false);
				progressBarDialog.dispose();
				UcCompoundInfo result = null;
				if (model != null && model.getUcResults() != null) {
					JDialogUniquenessCheck dlg = new JDialogUniquenessCheck(MasterController.getGUIComponent());
					result = dlg.displayDialog(model);
				} // else {
				// JOptionPane.showMessageDialog(StructureVnVContainer.this, "Failed to perform Novelty Check.");
				// }
				// update others
				if (result != null) {
					if (StringUtils.isNotBlank(result.getRegNumber())
							&& !result.getRegNumber().equalsIgnoreCase("drawn structure")) {
						if (isValidUcIsomerCode(result.getIsomerCode())) {
							selectedBatch.getCompound().setStereoisomerCode(result.getIsomerCode());
							jComboBoxStereoIsoCodeResult.setSelectedItem(result.getIsomerCode() + " - "
									+ sicMap.get(result.getIsomerCode()));
							jComboBoxSICBatch.setSelectedItem(result.getIsomerCode() + " - " + sicMap.get(result.getIsomerCode()));
							// selectedBatch.getRegInfo().setCompoundSource("KNOWN");
							// selectedBatch.getRegInfo().setCompoundSourceDetail("KNWNT");
							jTextFieldPFNum.setText(result.getRegNumber());
							if (StringUtils.isNotBlank(result.getComments())) {
								jTextAreaStruComments.setText(result.getComments());
								jTextAreaStruComments.setCaretPosition(0);
							}
							// Copy the structure over to the batch record
							try {
								int fmt = selectedBatch.getCompound().getFormat();
								byte[] structUC = result.getMolStruct().getBytes();
								byte[] structBatch = null;
								ChemistryDelegate chemDel = new ChemistryDelegate();
								if ((fmt == Compound.ISISDRAW || fmt == Compound.CHEMDRAW) && structUC != null
										&& structUC.length > 0) {
									structBatch = chemDel.convertChemistry(structUC, "", selectedBatch.getCompound()
											.getNativeSketchFormat());
								} else
									structBatch = structUC;
								if (structBatch != null && structBatch.length > 0) {
									if ((!chemDel.areMoleculesEqual(selectedBatch.getCompound().getNativeSketch(), structBatch)))
										JOptionPane.showMessageDialog(StructureVnVContainer.this,
												"Batch Structure will be modified based on your Novelty Check selection.");
									StructureLoadAndConversionUtil.loadSketch(structBatch, fmt, true, "MDL Molfile", selectedBatch.getCompound());
									structQueryCanvas.setChemistry(selectedBatch.getCompound().getNativeSketch());
									performVnV(false);
								}
							} catch (ChemUtilInitException e) {
								CeNErrorHandler.getInstance().logExceptionMsg(StructureVnVContainer.this,
										"Could not load Sketch due to a failure to initialize the Chemistry Service", e);
							} catch (ChemUtilAccessException e) {
								CeNErrorHandler.getInstance().logExceptionMsg(StructureVnVContainer.this,
										"Could not access Chemistry Service.  Failed to load sketch changes.", e);
							}
						} else {
							jTextFieldPFNum.setText("");
							JOptionPane.showMessageDialog(StructureVnVContainer.this,
									"Based on VnV results (right side), stereoisomer code '" + result.getIsomerCode()
											+ "' is not valid.\nPlease re-run VnV with HSREG stereoisomer code (left side).");
						}
					} else
						jTextFieldPFNum.setText("");
					jTextFieldPFNumActionPerformed(null);
				}
			}
		};
		worker.start();
	}

	public void updateRegInfo(VnVResultVO vnVResultVO) {
		selectedBatch.getCompound().setMolFormula(vnVResultVO.getMolFormula());
		selectedBatch.getCompound().setMolWgt(vnVResultVO.getMolWeight());
		// selectedBatch.getCompound().setStereoisomerCode(vnVResultVO.getValidSIC());
		// update vnv info
		selectedBatch.getRegInfo().getBatchVnVInfo().setSuggestedSICList(vnVResultVO.getSicList());
		selectedBatch.getRegInfo().getBatchVnVInfo().setErrorMsg(vnVResultVO.getErrorMessage());
		selectedBatch.getRegInfo().getBatchVnVInfo().setStatus(BatchVnVInfoModel.VNV_PASS);
		selectedBatch.getRegInfo().getBatchVnVInfo().setMolData(vnVResultVO.getResultStructureString());
	}

	public String buildVnVSDFIle() {
		if (getSelectedBatch() != null) {
			// vNv SDFile needs limited info for a batch
			try {
				ChemistryDelegate chemDel = new ChemistryDelegate();
				byte[] molFile = chemDel.convertChemistry(getSelectedBatch().getCompound().getNativeSketch(), "", "MDL Molfile");
				SdUnit sDunit;
				if (molFile != null)
					sDunit = new SdUnit(new String(molFile), true);
				else
					sDunit = new SdUnit();
				// //System.out.println("The built sd file is: " +
				// sDunit.toString());
				return sDunit.toString();
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e);
			}
		}
		return null;
	}

	public void updateBatchStructure() {
		try {
			ProductBatchModel ab = getSelectedBatch();
			ParentCompoundModel c = null;
			if (ab.getCompound() == null) {
				c = new ParentCompoundModel();
				ab.setCompound(c);
			} else {
				c = ab.getCompound();
			}
			c.setCreatedByNotebook(true);
			StructureLoadAndConversionUtil.loadSketch(structQueryCanvas.getChemistry(), structQueryCanvas.getEditorType(), true, "MDL Molfile",c);
			c.setCreatedByNotebook(true);
		} catch (ChemUtilInitException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this,
					"Could not load Sketch due to a failure to initialize the Chemistry Service", e);
		} catch (ChemUtilAccessException e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this,
					"Could not access Chemistry Service. Failed to load sketch changes.", e);
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
	}

	/**
	 * @return Returns the vnVResultVO.
	 */
	public VnVResultVO getVnVResultVO() {
		return vnVResultVO;
	}

	/**
	 * @param vnVResultVO
	 *            The vnVResultVO to set.
	 */
	public void setVnVResultVO(VnVResultVO vnVResultVO) {
		this.vnVResultVO = vnVResultVO;
	}

	protected void jComboBoxSICBatchItemStateChanged(ItemEvent evt) {
		if (!isInit) {
			String selectedISCode = (String) jComboBoxSICBatch.getSelectedItem();
			if (selectedISCode != null && evt.getStateChange() == ItemEvent.SELECTED && selectedBatch != null
					&& selectedBatch.getCompound() != null) {
				// String descr =
				// (String)sicMap.get(selectedISCode.substring(0,5));
				// if (descr != null && selectedBatch != null &&
				// selectedBatch.getCompound() != null) {
				// if (descr.startsWith("Other")) {
				// if (jTextArea1.getText().trim() != null &&
				// jTextArea1.getText().trim().length() > 0 ) {
				// selectedBatch.getCompound().setStereoisomerCode(selectedISCode.substring(0,5));
				// } else {
				// JOptionPane.showMessageDialog(this,
				// "Compound with this Stereoisomer Code requires structure
				// comments.");
				// String oldCode =
				// selectedBatch.getCompound().getStereoisomerCode();
				// jComboBoxSICBatch.setSelectedItem(oldCode + " - " +
				// sicMap.get(oldCode));
				// }
				// } else {
				selectedBatch.getCompound().setStereoisomerCode(selectedISCode.substring(0, 5));
				// }
				// }
			}
		}
	}

	/** Auto-generated event handler method */
	protected void jComboBoxStereoIsoCodeResultItemStateChanged(ItemEvent evt) {
		if (!isInit) {
			String selectedISCode = (String) jComboBoxStereoIsoCodeResult.getSelectedItem();
			if (selectedISCode != null && evt.getStateChange() == ItemEvent.SELECTED
					&& !selectedISCode.substring(0, 5).equals("ERROR")) {
				// need to check if structure comments is not null if SIC code's
				// description starts with "Other"
				if (((String) sicMap.get(selectedISCode.substring(0, 5))).startsWith("Other")) {
					if (jTextAreaStruComments.getText().trim() != null && jTextAreaStruComments.getText().trim().length() > 0) {
						selectedBatch.getCompound().setStereoisomerCode(selectedISCode.substring(0, 5));
						// set structure comments to compound
						// selectedBatch.getCompound().setStructureComments(jTextArea1.getText().trim());
						jComboBoxSICBatch.setSelectedItem(selectedISCode);
						// } else {
						// JOptionPane.showMessageDialog(this,
						// "Compound with this Stereoisomer Code requires
						// structure comments.");
						// jComboBoxStereoIsoCodeResult.setSelectedItem(vnVResultVO.getValidSIC()
						// + " - " + sicMap.get(vnVResultVO.getValidSIC()));
					}
				} else {
					selectedBatch.getCompound().setStereoisomerCode(selectedISCode.substring(0, 5));
					jComboBoxSICBatch.setSelectedItem(selectedISCode);
				}
			}
		}
	}

	// /** Auto-generated event handler method */
	// protected void jTextPane1PropertyChange(PropertyChangeEvent evt){
	// if( selectedBatch != null){
	// selectedBatch.getCompound().setComments(jTextArea1.getText());
	// //
	// getParentDialog().getCompReg_cont().setSelectedBatch(getSelectedBatch());
	// getParentDialog().getCompReg_cont().updateBatchList(getSelectedBatch());
	// }
	// }
	//
	// /** Auto-generated event handler method */
	// protected void jTextPane1FocusLost(FocusEvent evt){
	// //set structure comments to compound
	// selectedBatch.getCompound().setStructureComments(jTextArea1.getText().trim());
	// this.getParentDialog().getRegSubSum_cont().updateBatchList(selectedBatch);
	// }
	/** Auto-generated event handler method */
	protected void jTextFieldPFNumActionPerformed(ActionEvent evt) {
		try {
			String regNum = jTextFieldPFNum.getText().trim();
			// attempt to format
			if (StringUtils.isNotBlank(regNum)) {
				regNum = UtilsDispatcher.getFormatter().formatCompoundNumber(regNum);
//				regNum = CNFHelper.formatCompoundNumber(regNum);
			}
			selectedBatch.getCompound().setRegNumber(regNum);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
		((SingletonNotebookPageGUI) StructureVnVContainer.this.getParentDialog()).getRegSubSum_cont()
				.updateBatchList(selectedBatch);
	}

	private boolean isValidUcIsomerCode(String isomerCode) {
		boolean result = false;
		for (int i = 0; i < jComboBoxStereoIsoCodeResult.getItemCount() && !result; i++) {
			String sicode = (String) jComboBoxStereoIsoCodeResult.getItemAt(i);
			if (sicode.substring(0, isomerCode.length()).equals(isomerCode))
				result = true;
		}
		return result;
	}

	private boolean isEditable() {
		NotebookPageModel notebookPageModel = parentDialog.getPageModel();
		return (selectedBatch != null && parentDialog != null && notebookPageModel != null && CommonUtils.getProductBatchModelEditableFlag(selectedBatch, notebookPageModel));
	}
}
