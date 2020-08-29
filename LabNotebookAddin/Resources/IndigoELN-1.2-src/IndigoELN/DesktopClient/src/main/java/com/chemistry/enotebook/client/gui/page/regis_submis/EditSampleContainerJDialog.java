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

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.AmountComponent;
import com.chemistry.enotebook.compoundmanagement.classes.BarcodeValidationVO;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchSubmissionContainerInfo;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;
import com.chemistry.enotebook.utils.CeNDialog;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class EditSampleContainerJDialog extends CeNDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5377273856638895071L;
	private JPanel jPanelOther;
	private JPanel jPanelValidate;
	private JButton jButtonValidate;
	private JPanel jPanelContainer;
	private JButton jButtonCancel;
	private JPanel jPanelMain;
	private JButton jButtonOK;
	private JPanel jPanelOK;
	private JPanel jPanelTitle;
	private JTextField jTextFieldBarcode;
//	private JComboBox jComboBoxContainerSite;
	private AmountComponent amountComp;
	private String quanValueString = "";
	private SampleSubSumContainer sampleSubSumContainer;
	private BarcodeValidationVO barcodeValidationVO = new BarcodeValidationVO();
	public static final String VALID_STRING = "Valid barcode";
	public static final String NOT_VALID_STRING = "Invalid barcode";
	public static final String VALID_BUT_IN_USE_STRING = "Valid barcode but in use";
	private RegSubHandler regSubHandler = new RegSubHandler();

	public EditSampleContainerJDialog(JFrame owner, SampleSubSumContainer parent) {
		super(owner);
		sampleSubSumContainer = parent;
		initGUI();
	}

	public EditSampleContainerJDialog() {
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jPanelMain = new JPanel();
			jPanelTitle = new JPanel();
			jPanelContainer = new JPanel();
			jPanelOK = new JPanel();
			jPanelValidate = new JPanel();
			jButtonValidate = new JButton();
			jPanelOther = new JPanel();
			jButtonOK = new JButton();
			jButtonCancel = new JButton();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setTitle("Add SampleContainer");
			this.setSize(new java.awt.Dimension(300, 120));
			BorderLayout jPanelMainLayout = new BorderLayout();
			jPanelMain.setLayout(jPanelMainLayout);
			jPanelMainLayout.setHgap(0);
			jPanelMainLayout.setVgap(0);
			this.getContentPane().add(jPanelMain, BorderLayout.CENTER);
			BorderLayout jPanelTitleLayout = new BorderLayout();
			jPanelTitle.setLayout(jPanelTitleLayout);
			jPanelTitleLayout.setHgap(0);
			jPanelTitleLayout.setVgap(0);
			jPanelTitle.setVisible(true);
			jPanelMain.add(jPanelTitle, BorderLayout.NORTH);
			BorderLayout jPanelContainerLayout = new BorderLayout();
			jPanelContainer.setLayout(jPanelContainerLayout);
			jPanelContainerLayout.setHgap(0);
			jPanelContainerLayout.setVgap(0);
			jPanelContainer.setPreferredSize(new java.awt.Dimension(355, 48));
			jPanelContainer.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelMain.add(jPanelContainer, BorderLayout.CENTER);
			BorderLayout jPanelOKLayout = new BorderLayout();
			jPanelOK.setLayout(jPanelOKLayout);
			jPanelOKLayout.setHgap(0);
			jPanelOKLayout.setVgap(0);
			jPanelOK.setVisible(true);
			jPanelOK.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelMain.add(jPanelOK, BorderLayout.SOUTH);
			jPanelOK.add(jPanelValidate, BorderLayout.WEST);
			jButtonValidate.setText("Validate Barcode");
			jPanelValidate.add(jButtonValidate);
			jButtonValidate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonValidateActionPerformed(evt);
				}
			});
			jPanelOK.add(jPanelOther, BorderLayout.EAST);
			jButtonOK.setText("OK");
			jButtonOK.setVisible(true);
			jPanelOther.add(jButtonOK);
			jButtonOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOKActionPerformed(evt);
				}
			});
			jButtonCancel.setText("Cancel");
			jButtonCancel.setVisible(true);
			jPanelOther.add(jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed(evt);
				}
			});
			this.addWindowListener(new WindowAdapter() {
				public void windowActivated(WindowEvent e) {
					jTextFieldBarcode.requestFocus();
				}

				public void windowClosing(WindowEvent e) {
					EditSampleContainerJDialog.this.setVisible(false);
					EditSampleContainerJDialog.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					EditSampleContainerJDialog.this.removeAll();
					EditSampleContainerJDialog.this.setVisible(false);
				}
			});
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
		resetContainerPanelLayout();
		// reposition JDialog to the center of the screen
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = this.getPreferredSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));
	}

	public void resetContainerPanelLayout() {
		double border = 4;
		double size[][] = {{ border, 0.40, 0.02, 0.4, border }, // Columns
						   { 20, 20 } }; // Rows

		jPanelContainer.setLayout(new TableLayout(size));
//		JLabel operatorLabel = new JLabel("MM Site");
//		jPanelContainer.add(operatorLabel, "1, 0");
		JLabel barcodeLabel = new JLabel("Container Barcode");
		jPanelContainer.add(barcodeLabel, "1, 0");
		JLabel valueLabel = new JLabel("Amount");
		jPanelContainer.add(valueLabel, "3, 0");

//		jComboBoxContainerSite = new JComboBox();
//		jPanelContainer.add(jComboBoxContainerSite, "1, 1");
//		
//		//		populate site code/name
//		CodeTableUtils.fillComboBoxWithSites(jComboBoxContainerSite);
//		jComboBoxContainerSite.setSelectedItem(getSiteDecrFromCode(getSampleSubSumContainer().getSiteName()));

		jTextFieldBarcode = new JTextField();
		jTextFieldBarcode.setText("");
		jTextFieldBarcode.setEditable(true);
		jPanelContainer.add(jTextFieldBarcode, "1, 1");

		amountComp = new AmountComponent();
		amountComp.setEditable(true);
		amountComp.setEnabled(true);
		amountComp.setValueSetTextColor(null);
		amountComp.setValue(new Amount(UnitType.MASS));
		amountComp.setBorder(BorderFactory.createLoweredBevelBorder());
		jPanelContainer.add(amountComp, "3, 1");
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
			EditSampleContainerJDialog inst = new EditSampleContainerJDialog();
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonOKActionPerformed(ActionEvent evt) {
		if (barcodeValidationVO.getBarcode().equals("")
				|| !barcodeValidationVO.getBarcode().equals(jTextFieldBarcode.getText().trim()))
			validateBarcode();
		if (barcodeValidationVO.getBarcodeStatus().equals(BarcodeValidationVO.VALID_STRING)) {
			// may need to validate first
			if (validateContainerInfo()) {
				BatchSubmissionContainerInfo containerInfo = new BatchSubmissionContainerInfo();
//				containerInfo.setSiteCode(getSiteCodeFromDecr(jComboBoxContainerSite.getSelectedItem().toString()));
				containerInfo.setBarCode(jTextFieldBarcode.getText());
				containerInfo.setAmountValue(amountComp.getValue());
				containerInfo.setAmountUnit(amountComp.getAmount().getUnit().getCode());
				sampleSubSumContainer.addContainerInfo(containerInfo);
				sampleSubSumContainer.updateContainerDisplay();
//				sampleSubSumContainer.updateUserSite(containerInfo.getSiteCode());
				setVisible(false);
				dispose();
			} else {
				JOptionPane.showMessageDialog(this, "SampleContainer Amount Value cannot be empty.");
			}
		} else if (barcodeValidationVO.getBarcodeStatus().equals(BarcodeValidationVO.NOT_VALID_STRING)) {
			JOptionPane.showMessageDialog(this, "Please provide a valid container barcode.");
		} else if (barcodeValidationVO.getBarcodeStatus().equals(BarcodeValidationVO.VALID_BUT_IN_USE_STRING)) {
			JOptionPane.showMessageDialog(this, "The container barcode supplied is in use, please provide another one.");
		}
	}

	private boolean validateContainerInfo() {
		if (amountComp == null || amountComp.getValue() == 0)
			return false;
		else
			return true;
	}

	/**
	 * @return Returns the quanValueString.
	 */
	public String getQuanValueString() {
		return quanValueString;
	}

	/**
	 * @param quanValueString
	 *            The quanValueString to set.
	 */
	public void setQuanValueString(String quanValueString) {
		this.quanValueString = quanValueString;
	}

	/**
	 * @return Returns the jDialogEditCompRegInfo.
	 */
	public SampleSubSumContainer getSampleSubSumContainer() {
		return sampleSubSumContainer;
	}

	/**
	 * @param dialogEditCompRegInfo
	 *            The jDialogEditCompRegInfo to set.
	 */
	public void setJDialogEditCompRegInfo(SampleSubSumContainer dialogEditCompRegInfo) {
		sampleSubSumContainer = dialogEditCompRegInfo;
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed(ActionEvent evt) {
		this.setVisible(false);
		this.dispose();
	}

	/** Auto-generated event handler method */
	protected void jButtonValidateActionPerformed(ActionEvent evt) {
		validateBarcode();
		if (barcodeValidationVO.getBarcodeStatus().equals(BarcodeValidationVO.VALID_STRING)) {
			JOptionPane.showMessageDialog(this, "The container barcode is valid.");
		} else if (barcodeValidationVO.getBarcodeStatus().equals(BarcodeValidationVO.NOT_VALID_STRING)) {
			JOptionPane.showMessageDialog(this, "Please provide a valid container barcode.");
		} else if (barcodeValidationVO.getBarcodeStatus().equals(BarcodeValidationVO.VALID_BUT_IN_USE_STRING)) {
			JOptionPane.showMessageDialog(this, "The container barcode supplied is in use, please provide another one.");
		}
	}

	public void validateBarcode() {
		jTextFieldBarcode.setText(jTextFieldBarcode.getText().toUpperCase().trim());
		if (jTextFieldBarcode.getText().length() <= 0)
			JOptionPane.showMessageDialog(this, "Please provide a container barcode.");
		else {
			barcodeValidationVO.setBarcode(jTextFieldBarcode.getText());
//			barcodeValidationVO.setSiteCode(getSiteCodeFromDecr(jComboBoxContainerSite.getSelectedItem().toString()));
			barcodeValidationVO = regSubHandler.validateBarcode(barcodeValidationVO);
		}
	}

//	private String getSiteCodeFromDecr(String decr) {
//		String codeString = "";
//		try {
//			codeString = CodeTableCache.getCache().getSiteCode(decr);
//		} catch (CodeTableCacheException e) {
//			CeNErrorHandler.getInstance().logExceptionMsg(this, "Look up code failed.", e);
//		}
//		return codeString;
//	}
//
//	private String getSiteDecrFromCode(String code) {
//		String decrString = "";
//		try {
//			decrString = CodeTableCache.getCache().getSiteDescription(code);
//		} catch (CodeTableCacheException e) {
//			CeNErrorHandler.getInstance().logExceptionMsg(this, "Look up description failed.", e);
//		}
//		return decrString;
//	}

	public void dispose() {
		sampleSubSumContainer = null;
		barcodeValidationVO = null;
		regSubHandler = null;
		if (amountComp != null)
			amountComp.dispose();
		amountComp = null;
		super.dispose();
	}
}