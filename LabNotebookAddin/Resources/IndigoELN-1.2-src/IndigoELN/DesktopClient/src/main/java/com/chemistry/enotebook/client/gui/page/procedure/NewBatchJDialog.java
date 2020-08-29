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
package com.chemistry.enotebook.client.gui.page.procedure;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.ekit.ProcedureContainer;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationEvent;
import com.chemistry.enotebook.client.gui.page.batch.CompoundCreationHandler;
import com.chemistry.enotebook.client.utils.ReactionStepModelUtils;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchNumber;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchTypeFactory;
import com.chemistry.enotebook.experiment.datamodel.batch.InvalidBatchTypeException;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.utils.CeNDialog;
import com.chemistry.enotebook.utils.ParallelExpModelUtils;
import com.hexidec.ekit.EkitCore;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class NewBatchJDialog extends CeNDialog {

	private static final long serialVersionUID = -7167615150310757927L;
	
	private JButton jButtonCancel;
	private JButton jButtonOk;
	private JTextArea jTextAreaComments;
	private CeNComboBox jComboBoxBatchTypes;
	private JCheckBox jCheckBoxProductFlag;
	private JLabel jLabelBatchType;
	private JLabel jLabelComments;
	private JTextField jTextFieldLot;
	private JLabel jLabelNBRef;
	private JLabel jLabelLotNo;
	private EkitCore ekit;
	private NotebookPageModel pageModel;
	
	private CompoundCreationHandler compoundCreationHandler = null;
	
	public NewBatchJDialog() {
		initGUI();
	}
	
	public NewBatchJDialog(Frame component, NotebookPageModel _pageModel, EkitCore _edit, String _nbRef, CompoundCreationHandler handler) {
		this(component, _pageModel, _edit, _nbRef);
		compoundCreationHandler = handler;
	}
	
	public NewBatchJDialog(Frame component, NotebookPageModel _pageModel, EkitCore _ekit, String _nbRef) {
		super(component);
		setModal(true);
		pageModel = _pageModel;
		ekit = _ekit;
		Point loc = MasterController.getGuiController().getGUIComponent().getLocation();
		Dimension dim = MasterController.getGuiController().getGUIComponent().getSize();
		setLocation((dim.width - getSize().width) / 2, loc.y + (dim.height - getSize().height) / 2);
		initGUI();
	}
	
	public void initGUI() {
		try {
			preInitGUI();
			jLabelLotNo = new JLabel();
			jLabelNBRef = new JLabel();
			jTextFieldLot = new JTextField();
			jLabelComments = new JLabel();
			jLabelBatchType = new JLabel();
			jComboBoxBatchTypes = new CeNComboBox();
			jTextAreaComments = new JTextArea();
			jCheckBoxProductFlag = new JCheckBox("Product");
			jButtonOk = new JButton();
			jButtonCancel = new JButton();
			this.getContentPane().setLayout(null);
			this.setTitle("New Nbk Batch Entry");
			this.setResizable(true);
			this.setSize(new java.awt.Dimension(465, 195));
			jLabelLotNo.setText("Nbk Batch #:");
			jLabelLotNo.setFont(new java.awt.Font("sansserif", 1, 11));
			jLabelLotNo.setPreferredSize(new java.awt.Dimension(150, 20));
			jLabelLotNo.setBounds(new java.awt.Rectangle(210, 10, 150, 20));
			this.getContentPane().add(jLabelLotNo);
			jLabelNBRef.setText("");
			jLabelNBRef.setPreferredSize(new java.awt.Dimension(80, 20));
			jLabelNBRef.setBounds(new java.awt.Rectangle(285, 12, 80, 20));
			this.getContentPane().add(jLabelNBRef);
			jTextFieldLot.setText("");
			jTextFieldLot.setPreferredSize(new java.awt.Dimension(60, 20));
			jTextFieldLot.setBounds(new java.awt.Rectangle(375, 12, 60, 20));
			this.getContentPane().add(jTextFieldLot);
			jTextFieldLot.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					jTextFieldLot.setSelectionEnd(jTextFieldLot.getText().length());
					jTextFieldLot.setSelectionStart(0);
				}
			});
			// limit the characters of the lot number
			jTextFieldLot.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent evt) {
					char keyEntered = evt.getKeyChar();
					if (keyEntered == 22) { // Ctrl-V
						boolean invalid = false;
						byte[] bytes = jTextFieldLot.getText().getBytes();
						for (int i=0; i < bytes.length; i++) {
							if ((bytes[i] >= 32 && bytes[i] <= 47)||
									(bytes[i] >= 58 && bytes[i] <= 64)||
									(bytes[i] >= 91 && bytes[i] <= 96)||
									(bytes[i] >= 123 && bytes[i] <= 126)) {
								invalid = true;
								bytes[i] = 'X';  
							} else if (bytes[i] >= 'a' && bytes[i] <= 'z') {
								bytes[i] = (byte)Character.toUpperCase((char)bytes[i]);  
							}
						}
						if (invalid) {
							JOptionPane.showMessageDialog(jTextFieldLot, "Invalid Lot # entered (" + jTextFieldLot.getText() + "), please re-enter.",
									"Lot # Input Error", JOptionPane.ERROR_MESSAGE);
						}
						jTextFieldLot.setText(new String(bytes));
					} else if (keyEntered >= 'a' && keyEntered <= 'z')
						evt.setKeyChar(Character.toUpperCase(keyEntered));
					
					if ((keyEntered >= 32 && keyEntered <= 47)||
							  (keyEntered >= 58 && keyEntered <= 64)||
							  (keyEntered >= 91 && keyEntered <= 96)||
							  (keyEntered >= 123 && keyEntered <= 126)){
					    evt.consume();
					}
					if (jTextFieldLot.getText().length() >= NotebookPageUtil.NB_BATCH_LOT_MAX_LENGTH &&
						keyEntered != '\b') {
						evt.consume();
					}
				}
			});
			jCheckBoxProductFlag.setBounds(new java.awt.Rectangle(20, 12, 75, 20));
			jCheckBoxProductFlag.setFont(new java.awt.Font("sansserif", 1, 11));
			jCheckBoxProductFlag.setMnemonic(KeyEvent.VK_P);
			jCheckBoxProductFlag.setSelected(false);
			jCheckBoxProductFlag.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			this.getContentPane().add(jCheckBoxProductFlag);
			jLabelComments.setText("Batch Comments:");
			jLabelComments.setFont(new java.awt.Font("sansserif", 1, 11));
			jLabelComments.setPreferredSize(new java.awt.Dimension(130, 20));
			jLabelComments.setBounds(new java.awt.Rectangle(25, 50, 130, 20));
			this.getContentPane().add(jLabelComments);
			jLabelBatchType.setText("Batch Structure:");
			jLabelBatchType.setFont(new java.awt.Font("sansserif", 1, 11));
			jLabelBatchType.setPreferredSize(new java.awt.Dimension(100, 20));
			jLabelBatchType.setBounds(new java.awt.Rectangle(185, 45, 100, 20));
			this.getContentPane().add(jLabelBatchType);
			jComboBoxBatchTypes.setPreferredSize(new java.awt.Dimension(150, 20));
			jComboBoxBatchTypes.setFocusable(false);
			jComboBoxBatchTypes.setBounds(new java.awt.Rectangle(285, 45, 150, 20));
			this.getContentPane().add(jComboBoxBatchTypes);
			jTextAreaComments.setPreferredSize(new java.awt.Dimension(420, 50));
			jTextAreaComments.setBorder(new EtchedBorder(BevelBorder.LOWERED, null, null));
			jTextAreaComments.setBounds(new java.awt.Rectangle(15, 80, 420, 50));
			this.getContentPane().add(jTextAreaComments);
			jTextAreaComments.setLineWrap(true);
			jButtonOk.setText("OK");
			jButtonOk.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonOk.setPreferredSize(new java.awt.Dimension(60, 20));
			jButtonOk.setBounds(new java.awt.Rectangle(140, 140, 60, 20));
			this.getContentPane().add(jButtonOk);
			jButtonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed(evt);
				}
			});
			jButtonCancel.setText("Cancel");
			jButtonCancel.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonCancel.setPreferredSize(new java.awt.Dimension(75, 20));
			jButtonCancel.setBounds(new java.awt.Rectangle(250, 140, 75, 20));
			this.getContentPane().add(jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					NewBatchJDialog.this.setVisible(false);
					NewBatchJDialog.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					NewBatchJDialog.this.removeAll();
					NewBatchJDialog.this.setVisible(false);
				}
			});
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	protected void jButtonOkActionPerformed(ActionEvent evt) {
		BatchNumber batchNumber = createBatchNumber();
		if (batchNumber != null) {
			String abNew = null;
			if (jComboBoxBatchTypes.getSelectedIndex() == 0) {
				ProductBatchModel newBatch = new ProductBatchModel();
				abNew = processNewBatch(batchNumber, newBatch);
			} else {
				ProductBatchModel selectedBatch = ((NewBatchJDialog.IntendedProductComboBoxItem)jComboBoxBatchTypes.getItemAt(jComboBoxBatchTypes.getSelectedIndex())).getIntendedProductBatchModel();
				ProductBatchModel newBatch = new ProductBatchModel();
				newBatch.setMolecularFormula(selectedBatch.getMolecularFormula());
				BatchAttributeComponentUtility.syncParentCompoundModel(selectedBatch.getCompound(), newBatch);
				abNew = processNewBatch(batchNumber, newBatch);
			}
			if (abNew != null) {
				String insertString = "&nbsp;<A ephoxclickable=" + '"' + '"' + " href=" + '"' + "cen://"
						+ abNew + '"' + ">" + abNew + "</A>&nbsp;";
				if(ekit != null) {
					ProcedureContainer.insertHTMLAtCursor(ekit, insertString);
				}
			}
			setVisible(false);
			dispose();
		}
	}

	private String processNewBatch(BatchNumber batchNumber, ProductBatchModel newBatch) {
		newBatch.setBatchNumber(batchNumber);
		
		try {
			newBatch.setBatchType(BatchTypeFactory.getBatchType(CeNConstants.BATCH_TYPE_ACTUAL));
		} catch (InvalidBatchTypeException e1) {
			e1.printStackTrace();
		}
		
		newBatch.setUserAdded(true);
		newBatch.setOwner(pageModel.getBatchOwner());
		newBatch.setSynthesizedBy(pageModel.getPageHeader().getUserName());
		newBatch.getCompound().setCreatedByNotebook(true);
		newBatch.setLoadedFromDB(false);
		newBatch.setUserAdded(true);
		newBatch.setComments(jTextAreaComments.getText());
		newBatch.setProductFlag(jCheckBoxProductFlag.isSelected());
		
		StoicModelInterface stoichModel = ReactionStepModelUtils.findLimitingReagent(pageModel.getLastStep());
		if (stoichModel != null) {
			newBatch.setTheoreticalMoleAmountFromStoicAmount(stoichModel.getStoicMoleAmount());
		}
		
		BatchesList<ProductBatchModel> batchesList = pageModel.getUserAddedBatchesList();
		batchesList.addBatch(newBatch);
		
		pageModel.getLastStep().setModelChanged(true);
		pageModel.getPseudoProductPlate(false).addNewBatch(newBatch);
		pageModel.getAllProductBatchesAndPlatesMap(true);// Refresh the map
		pageModel.setModelChanged(true); // To enable the save buttons
		
		new ParallelExpModelUtils(pageModel).setOrRefreshGuiPseudoProductPlate();
		
		if (compoundCreationHandler != null) {
			CompoundCreationEvent event = new CompoundCreationEvent(this, newBatch, pageModel.getLastStep());
			compoundCreationHandler.fireNewCompoundCreated(event);
		}
		
		return newBatch.getBatchNumberAsString();
	}

	private BatchNumber createBatchNumber() {
		try {
			String batchNo = jLabelNBRef.getText() + jTextFieldLot.getText();
			List<ProductBatchModel> list = pageModel.getAllProductBatchModelsInThisPage();
			for (ProductBatchModel model : list) {
				String batchNoInList = model.getBatchNumberAsString();
				if (batchNoInList.trim().toUpperCase().equals(batchNo.trim().toUpperCase())) {
					JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "This batch number already exists. Please enter another number.");
					return null;
				}
			}
			return new BatchNumber(batchNo);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		setVisible(false);
		dispose();
	}

	public void preInitGUI() {
	}

	public void postInitGUI() {
		refresh_This();
	}

	public static void showGUI(NotebookPageModel _pageModel, EkitCore _edit, String _nbRef, CompoundCreationHandler handler) {
		try {
			NewBatchJDialog inst = new NewBatchJDialog(MasterController.getGuiController().getGUIComponent(), _pageModel, _edit,_nbRef);	
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void refresh_This() {
		try {
			BatchNumber batchNumber = null;
			if (pageModel.isParallelExperiment()) {
				batchNumber = new BatchNumber(pageModel.getNextBatchNumberForProductBatch());
			} else {
				batchNumber = pageModel.getNextBatchNumberForSingletonProductBatch();
				fillJComboBoxBatchTypes();
			}
			jLabelNBRef.setText(pageModel.getNotebookRefWithoutVersion() + "-");
			jTextFieldLot.setText(batchNumber.getLotNumber());
			jCheckBoxProductFlag.setSelected(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void fillJComboBoxBatchTypes() {
		List<ProductBatchModel> productBatchesList = pageModel.getSingletonBatchs();
		jComboBoxBatchTypes.addItem("New");
		
		IntendedProductComboBoxItem firstProductItem = null;
		
		for(ProductBatchModel intendedProductBatchModel: productBatchesList) {
			if(BatchType.INTENDED_PRODUCT.equals(intendedProductBatchModel.getBatchType())) {
				if (firstProductItem == null) {
					firstProductItem = new IntendedProductComboBoxItem(intendedProductBatchModel);
					jComboBoxBatchTypes.addItem(firstProductItem);
				} else {
					jComboBoxBatchTypes.addItem(new IntendedProductComboBoxItem(intendedProductBatchModel));
				}
			}
		}
		if (firstProductItem != null) {
			jComboBoxBatchTypes.setSelectedItem(firstProductItem);
		}
	}
	
	private class IntendedProductComboBoxItem {
		private ProductBatchModel intendedProductBatchModel;
		
		public IntendedProductComboBoxItem(ProductBatchModel intendedProductBatchModel) {
			this.intendedProductBatchModel = intendedProductBatchModel;
		}
		
		public ProductBatchModel getIntendedProductBatchModel() {
			return intendedProductBatchModel;
		}
		
		public String toString() {
			return "Intended Product P" + intendedProductBatchModel.getTransactionOrder() + " " + intendedProductBatchModel.getCompound().getMolFormula();
		}
	}
}

