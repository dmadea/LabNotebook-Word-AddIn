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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.domain.ExternalSupplierModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.utils.CeNDialog;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Properties;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class ExtSuplInfoJDialog extends CeNDialog implements ItemListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1659682602393810918L;
	private ProductBatchModel ab;
	private JComponent jComponent;
	private List<Properties> al;
	private JButton jButtonCancel;
	private JButton jButtonOk;
	private CeNComboBox jComboBoxSuppliers;
	private JLabel jLabelExtSuplName;
	private JTextField jTextFieldSuplCatalog;
	private JLabel jLabelExtSuplCatalog;
	private JLabel jLabelHeader;
	private JTextField jTextFieldSupplierCode;
	private JTextField jTextFieldSupplierName;
	private NotebookPageModel pageModel;

	public ExtSuplInfoJDialog(Frame owner, ProductBatchModel ab, List<Properties> al, JComponent jComponent, NotebookPageModel pageModel) {
		super(owner);
		this.pageModel = pageModel;
		this.ab = ab;
		this.al = al;
		this.jComponent = jComponent;
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			jLabelHeader = new JLabel();
			jLabelExtSuplName = new JLabel();
			jLabelExtSuplCatalog = new JLabel();
			jTextFieldSuplCatalog = new JTextField();
			jButtonOk = new JButton();
			jButtonCancel = new JButton();
			getContentPane().setLayout(null);
			setFont(new java.awt.Font("Dialog", 0, 14));
			setSize(new java.awt.Dimension(500, 200));
			setTitle("External Supplier Information");
			setLocation(600, 450);
			jLabelHeader.setText("External Supplier Information");
			jLabelHeader.setFont(new java.awt.Font("Dialog", 1, 14));
			jLabelHeader.setPreferredSize(new java.awt.Dimension(300, 35));
			jLabelHeader.setBounds(new java.awt.Rectangle(8, 6, 300, 35));
			getContentPane().add(jLabelHeader);
			jLabelExtSuplName.setText("External Supplier Code & Name: ");
			jLabelExtSuplName.setPreferredSize(new java.awt.Dimension(250, 20));
			jLabelExtSuplName.setBounds(new java.awt.Rectangle(30, 50, 250, 20));
			getContentPane().add(jLabelExtSuplName);
			if (al.size() > 0) {
				jComboBoxSuppliers = new CeNComboBox();
				jComboBoxSuppliers.setPreferredSize(new java.awt.Dimension(250, 20));
				jComboBoxSuppliers.setBounds(new java.awt.Rectangle(200, 50, 250, 20));
				getContentPane().add(jComboBoxSuppliers);
				jComboBoxSuppliers.addItemListener(this);
			} else {
				jLabelExtSuplName.setText("External Supplier Code: ");
				jTextFieldSupplierCode = new JTextField();
				jTextFieldSupplierName = new JTextField();
				jTextFieldSupplierCode.setPreferredSize(new java.awt.Dimension(70, 20));
				jTextFieldSupplierCode.setBounds(new java.awt.Rectangle(200, 50, 70, 20));
				getContentPane().add(jTextFieldSupplierCode);
				JLabel jLabel = new JLabel();
				jLabel.setText("Name: ");
				jLabel.setPreferredSize(new java.awt.Dimension(30, 20));
				jLabel.setBounds(new java.awt.Rectangle(280, 50, 50, 20));
				getContentPane().add(jLabel);
				jTextFieldSupplierName = new JTextField();
				jTextFieldSupplierName.setPreferredSize(new java.awt.Dimension(150, 20));
				jTextFieldSupplierName.setBounds(new java.awt.Rectangle(320, 50, 150, 20));
				getContentPane().add(jTextFieldSupplierName);
			}
			jLabelExtSuplCatalog.setText("Supplier Catalog/Registry#: ");
			jLabelExtSuplCatalog.setPreferredSize(new java.awt.Dimension(250, 20));
			jLabelExtSuplCatalog.setBounds(new java.awt.Rectangle(30, 70, 250, 20));
			getContentPane().add(jLabelExtSuplCatalog);
			jTextFieldSuplCatalog.setPreferredSize(new java.awt.Dimension(250, 20));
			jTextFieldSuplCatalog.setBounds(new java.awt.Rectangle(200, 70, 250, 20));
			getContentPane().add(jTextFieldSuplCatalog);
			jButtonOk.setText("OK");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonOk);
			jButtonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed();
				}
			});
			jButtonOk.setPreferredSize(new java.awt.Dimension(60, 20));
			jButtonOk.setBounds(new java.awt.Rectangle(180, 130, 60, 20));
			getContentPane().add(jButtonOk);
			jButtonCancel.setText("Cancel");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed();
				}
			});
			jButtonCancel.setPreferredSize(new java.awt.Dimension(75, 20));
			jButtonCancel.setBounds(new java.awt.Rectangle(270, 130, 75, 20));
			getContentPane().add(jButtonCancel);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					ExtSuplInfoJDialog.this.setVisible(false);
					ExtSuplInfoJDialog.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					ExtSuplInfoJDialog.this.removeAll();
					ExtSuplInfoJDialog.this.setVisible(false);
				}
			});
			postInitGUI();
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	protected void defaultApplyAction() {
		jButtonOkActionPerformed();
	}

	protected void defaultCancelAction() {
		jButtonCancelActionPerformed();
	}

	/** Add your pre-init code in here */
	public void preInitGUI() {
	}

	/** Add your post-init code in here */
	public void postInitGUI() {
		setModal(true);
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dlgSize = this.getSize();
		setLocation(screenSize.width / 2 - (dlgSize.width / 2), screenSize.height / 2 - (dlgSize.height / 2));
		fillInComboBox();
		setValues();// set if batch has extsplinfo already
	}

	/**
	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
	 * delete this method it will not be re-created.
	 * @param pageModel 
	 */
	public static void showGUI(Frame owner, ProductBatchModel ab, List<Properties> al, JComponent jComponent, NotebookPageModel pageModel) {
		try {
			ExtSuplInfoJDialog inst = new ExtSuplInfoJDialog(owner, ab, al, jComponent, pageModel);
			inst.setVisible(true);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelActionPerformed() {
		setVisible(false);
		this.dispose();
	}

	/** Auto-generated event handler method */
	protected void jButtonOkActionPerformed() {
		if (al != null && al.size() > 0 && jComboBoxSuppliers != null) {
			String vendorName = jComboBoxSuppliers.getSelectedItem() == null ? "" : jComboBoxSuppliers.getSelectedItem().toString();
			if (ab == null || vendorName.equals("")) {
				setVisible(false);
				this.dispose();
				return;
			}
			ExternalSupplierModel vendor = null;
			vendor = new ExternalSupplierModel();
			if (!vendorName.equals("-None-"))
			{
				String codeAndName = jComboBoxSuppliers.getSelectedItem().toString();
				String code = "";
				String name = "";
				if (codeAndName == null)
					codeAndName = "";
				int i = codeAndName.indexOf("-");
				if (i > 0 && i < codeAndName.length()) {
					code = codeAndName.substring(0, i).trim();
					name = codeAndName.substring(i + 1, codeAndName.length()).trim();
				}
				vendor.setCode(code);
				vendor.setSupplierName(name);
				vendor.setSupplierCatalogRef(jTextFieldSuplCatalog.getText());
			}
			JTextArea jep = (JTextArea) jComponent;
			jep.setText(vendor.toString());
			ab.setVendorInfo(vendor);
			ab.setModelChanged(true);
			pageModel.setModelChanged(true);			
			setVisible(false);
			this.dispose();
		} else if (jTextFieldSupplierCode != null) {
			String code = jTextFieldSupplierCode.getText();
			String name = jTextFieldSupplierName.getText();
			if (StringUtils.isBlank(code)) {
				setVisible(false);
				return;
			}
			ExternalSupplierModel vendor = new ExternalSupplierModel();
			vendor.setCode(code);
			vendor.setSupplierName(name);
			vendor.setSupplierCatalogRef(jTextFieldSuplCatalog.getText());
			JTextArea jep = (JTextArea) jComponent;
			jep.setText(vendor.toString());
			ab.setVendorInfo(vendor);
			ab.setModelChanged(true);
			pageModel.setModelChanged(true);
			setVisible(false);
			this.dispose();
		}
	}

	public void setValues() {
		if (ab != null) {
			if (ab.getVendorInfo().getCode() != null) {
				if (jComboBoxSuppliers != null) {
					jComboBoxSuppliers.setSelectedItem(ab.getVendorInfo().getCode() + " - " + ab.getVendorInfo().getSupplierName());
				}
				jTextFieldSuplCatalog.setText(ab.getVendorInfo().getSupplierCatalogRef());
			}
			else
				jTextFieldSuplCatalog.setEnabled(false);
		}
	}

	/**
	 * fills the combo box Code & Name
	 * 
	 */
	public void fillInComboBox() {
		jComboBoxSuppliers.addItem("-None-");
		for (int i = 0; i < al.size(); i++) {
			Properties alInner = al.get(i);
			String code = (String) alInner.get(CodeTableCache.VENDOR__SUPPLIER_CODE);// Code
			String name = (String) alInner.get(CodeTableCache.VENDOR__SUPPLIER_DESC);// C
			if (name == null)
				name = "";
			jComboBoxSuppliers.addItem(code + " - " + name);
		}
	}

	public void dispose() {
		this.ab = null;
		this.ab = null;
		this.jComponent.removeAll();
		this.jComponent = null;
		super.dispose();
	}

	public void itemStateChanged(ItemEvent arg0) {
		if (jComboBoxSuppliers.getSelectedIndex() > 0)
			jTextFieldSuplCatalog.setEnabled(true);
		else
		{
			jTextFieldSuplCatalog.setText("");
			jTextFieldSuplCatalog.setEnabled(false);
		}
	}
}
