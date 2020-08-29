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
package com.chemistry.enotebook.client.gui.page.attachements;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import com.chemistry.enotebook.utils.CeNDialog;
import com.cloudgarden.layout.AnchorLayout;
import com.lowagie.text.Font;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class AttachDocumentJDialog extends CeNDialog {
	
	private static final long serialVersionUID = 456914636502373966L;
	
	private JEditorPane jEditorPaneDesc;
	private JScrollPane jScrollPane1;
	private JButton jButtonBrowse;
	private JButton jButtonCancel;
	private JButton jButtonOK;
	private JTextField jTextFieldDocName;
	private JTextField jTextFieldDisplayName;
	private JLabel jLabelFile;
	private JLabel jLabelDescription;
	private JLabel jLabelName;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private AbstractTableModel dataModel;
	private File attachedFile;
//	private String nbRef;
	private boolean blnEdit;
	private int tableRow;
	
	private Frame owner = null;

	public AttachDocumentJDialog(Frame owner, AbstractTableModel dataModel, String nbRef, boolean blnEdit, int row) {
		super(owner);
		this.owner = owner;
		this.dataModel = dataModel;
//		this.nbRef = nbRef;
		this.blnEdit = blnEdit;
		this.tableRow = row;
		initGUI();
		if (blnEdit) {
			jTextFieldDocName.setEditable(false);
			jButtonBrowse.setEnabled(false);
			jTextFieldDisplayName.setText((String) dataModel.getValueAt(row, 1));
			jEditorPaneDesc.setText((String) dataModel.getValueAt(row, 5));
			jTextFieldDocName.setText((String) dataModel.getValueAt(row, 6));
		}
	}

	/**
	 * Allow override of title.
	 * 
	 * @param owner
	 * @param title
	 * @param dataModel
	 * @param nbRef
	 * @param blnEdit
	 * @param row
	 */
	public AttachDocumentJDialog(Frame owner, String title, AbstractTableModel dataModel, String nbRef, boolean blnEdit, int row) {
		super(owner);
		this.owner = owner;
		this.dataModel = dataModel;
//		this.nbRef = nbRef;
		this.blnEdit = blnEdit;
		this.tableRow = row;
		initGUI();
		setTitle(title);
		if (blnEdit) {
			jTextFieldDocName.setEditable(false);
			jButtonBrowse.setEnabled(false);
			jTextFieldDisplayName.setText((String) dataModel.getValueAt(row, 1));
			jEditorPaneDesc.setText((String) dataModel.getValueAt(row, 5));
			jTextFieldDocName.setText((String) dataModel.getValueAt(row, 6));
		}
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {			
			jPanel1 = new JPanel();
			jLabelName = new JLabel();
			jLabelDescription = new JLabel();
			jLabelFile = new JLabel();
			jTextFieldDisplayName = new JTextField();
			jTextFieldDocName = new JTextField();
			jButtonBrowse = new JButton();
			jScrollPane1 = new JScrollPane();
			jEditorPaneDesc = new JEditorPane();
			jPanel2 = new JPanel();
			jButtonOK = new JButton();
			jButtonCancel = new JButton();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setTitle("Add New Document");
			this.setModal(true);
			this.setSize(new java.awt.Dimension(487, 244));
			//this.setLocation(new java.awt.Point(300, 300));
			jPanel1.setLayout(null);
			CeNGUIUtils.styleComponentText(Font.BOLD, jPanel1);
			jPanel1.setPreferredSize(new java.awt.Dimension(480, 172));
			jPanel1.setLocation(new java.awt.Point(150, 150));
			this.getContentPane().add(jPanel1, BorderLayout.NORTH);
			AnchorLayout jLabelNameLayout = new AnchorLayout();
			jLabelName.setLayout(jLabelNameLayout);
			jLabelName.setText("Display Name:");
			jLabelName.setHorizontalAlignment(SwingConstants.RIGHT);
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelName);
			jLabelName.setPreferredSize(new java.awt.Dimension(85, 15));
			jLabelName.setBounds(new java.awt.Rectangle(15, 17, 85, 15));
			jPanel1.add(jLabelName);
			jLabelDescription.setText("Description:");
			jLabelDescription.setHorizontalAlignment(SwingConstants.RIGHT);
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelDescription);
			jLabelDescription.setPreferredSize(new java.awt.Dimension(71, 15));
			jLabelDescription.setBounds(new java.awt.Rectangle(29, 49, 71, 15));
			jPanel1.add(jLabelDescription);
			jLabelFile.setText("<html>File / Html<br> to add:</html>");
			jLabelFile.setHorizontalAlignment(SwingConstants.RIGHT);
			CeNGUIUtils.styleComponentText(Font.BOLD, jLabelFile);
			jLabelFile.setPreferredSize(new java.awt.Dimension(100, 28));
			jLabelFile.setBounds(new java.awt.Rectangle(0, 126, 100, 28));
			jPanel1.add(jLabelFile);
			jTextFieldDisplayName.setPreferredSize(new java.awt.Dimension(349, 21));
			jTextFieldDisplayName.setSize(new java.awt.Dimension(349, 21));
			jTextFieldDisplayName.setBounds(new java.awt.Rectangle(110, 15, 349, 21));
			jPanel1.add(jTextFieldDisplayName);
			jTextFieldDocName.setPreferredSize(new java.awt.Dimension(350, 20));
			jTextFieldDocName.setSize(new java.awt.Dimension(350, 20));
			jTextFieldDocName.setBounds(new java.awt.Rectangle(110, 127, 350, 20));
			jPanel1.add(jTextFieldDocName);
			jButtonBrowse.setText("Browse ...");
			jButtonBrowse.setPreferredSize(new java.awt.Dimension(75, 20));
			jButtonBrowse.setBounds(new java.awt.Rectangle(385, 149, 75, 20));
			jPanel1.add(jButtonBrowse);
			jButtonBrowse.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evt) {
					jButtonBrowseMouseReleased();
				}
			});
			jScrollPane1.setPreferredSize(new java.awt.Dimension(350, 55));
			jScrollPane1.setBounds(new java.awt.Rectangle(110, 54, 350, 55));
			jPanel1.add(jScrollPane1);
			jEditorPaneDesc.setPreferredSize(new java.awt.Dimension(346, 61));
			jEditorPaneDesc.setSize(new java.awt.Dimension(346, 61));
			jScrollPane1.add(jEditorPaneDesc);
			jScrollPane1.setViewportView(jEditorPaneDesc);
			FlowLayout jPanel2Layout = new FlowLayout();
			jPanel2.setLayout(jPanel2Layout);
			jPanel2Layout.setAlignment(FlowLayout.CENTER);
			jPanel2Layout.setHgap(80);
			jPanel2Layout.setVgap(5);
			jPanel2.setVisible(true);
			jPanel2.setPreferredSize(new java.awt.Dimension(480, 39));
			this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
			jButtonOK.setText("Save");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonOK);
			jButtonOK.setPreferredSize(new java.awt.Dimension(65, 25));
			jButtonOK.setDoubleBuffered(true);
			jPanel2.add(jButtonOK);
			jButtonOK.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evt) {
					jButtonOKMouseReleased();
				}
			});
			jButtonCancel.setText("Cancel");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonCancel);
			jButtonCancel.setPreferredSize(new java.awt.Dimension(65, 25));
			jPanel2.add(jButtonCancel);
			jButtonCancel.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evt) {
					jButtonCancelMouseReleased();
				}
			});
			
			setResizable(false);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			setLocationRelativeTo(owner);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	protected void defaultApplyAction() {
		jButtonOKMouseReleased();
	}

	protected void defaultCancelAction() {
		jButtonCancelMouseReleased();
	}	

	/**
	 * This static method creates a new instance of this class and shows it inside a new JFrame, (unless it is already a JFrame).
	 * 
	 * It is a convenience method for showing the GUI, but it can be copied and used as a basis for your own code. * It is
	 * auto-generated code - the body of this method will be re-generated after any changes are made to the GUI. However, if you
	 * delete this method it will not be re-created.
	 */
	public static void showGUI(Frame owner, String title, PCeNAttachmentsTableModel dataModel, String nbRef, boolean blnEdit, int row) {
		try {
			AttachDocumentJDialog inst = new AttachDocumentJDialog(owner, title, dataModel, nbRef, blnEdit, row);
			inst.setVisible(true);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonBrowseMouseReleased() {
		if (jButtonBrowse.isEnabled()) {
			CustomCENJFileChooser jfc = new CustomCENJFileChooser();
			try {
				String sDir = MasterController.getUser().getPreference(NotebookUser.PREF_PATH_ATTACHEMENT_UPLOAD);
				if (sDir != null && sDir.length() > 0)
					jfc.setCurrentDirectory(new File(sDir));
			} catch (UserPreferenceException e1) {
				CeNErrorHandler.getInstance().logExceptionMsg(null, e1);
			}
			jfc.setMultiSelectionEnabled(false);
			int result = jfc.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				attachedFile = jfc.getSelectedFile();
				if (attachedFile != null) {
					this.jTextFieldDocName.setText(attachedFile.getAbsolutePath());
				}
			}
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonOKMouseReleased() {
		if (blnEdit) {
			((PCeNAttachmentsTableModel) dataModel).setValueAt(jTextFieldDisplayName.getText(), tableRow, 1);
			((PCeNAttachmentsTableModel) dataModel).setValueAt(jEditorPaneDesc.getText(), tableRow, 5);
			this.setVisible(false);
			this.dispose();
		} else {
			if ((!jTextFieldDocName.getText().trim().equals("") && !jTextFieldDisplayName.getText().equals("") || blnEdit)) {
				// Check valid attachment
				if (!jTextFieldDocName.getText().startsWith("http://")) {
					try {
						File testFile = new File(jTextFieldDocName.getText().trim());
						if (!testFile.exists()) {
							JOptionPane.showMessageDialog(this, "The attachment is invalid, file not found", "Invalid Attachment",
									JOptionPane.PLAIN_MESSAGE);
						}
					} catch (Exception ioe) {
						JOptionPane.showMessageDialog(this, "The attachment is invalid, file not found", "Message Dialog",
								JOptionPane.PLAIN_MESSAGE);
					}
				}
				((PCeNAttachmentsTableModel) dataModel).createAttachment(jTextFieldDisplayName.getText(), jEditorPaneDesc.getText(),
						jTextFieldDocName.getText());

				try {
					MasterController.getUser().setPreference(NotebookUser.PREF_PATH_ATTACHEMENT_UPLOAD, jTextFieldDocName.getText());
				} catch (UserPreferenceException e1) {
					CeNErrorHandler.getInstance().logExceptionMsg(null, e1);
				}

				this.setVisible(false);
				this.dispose();
			} else {
				JOptionPane.showMessageDialog(this, "A value must be specified for Display Name and File/Html", "Message Dialog",
						JOptionPane.PLAIN_MESSAGE);
			}
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonCancelMouseReleased() {
		this.setVisible(false);
		this.dispose();
	}

	public void dispose() {
		this.dataModel = new DefaultTableModel();
		this.dataModel = null;
		super.dispose();
	}
}
