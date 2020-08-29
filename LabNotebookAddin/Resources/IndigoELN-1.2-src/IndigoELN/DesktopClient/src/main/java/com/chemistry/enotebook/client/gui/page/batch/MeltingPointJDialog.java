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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.TemperatureRangeModel;
import com.chemistry.enotebook.utils.CeNDialog;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class MeltingPointJDialog extends CeNDialog {
	
	private static final long serialVersionUID = 1352963880920209446L;

	private JFrame owner = null;
	
	private BatchModel ab;
	private JButton jButtonCancel;
	private JButton jButtonOk;
	private JTextField jTextFieldComments;
	private JLabel jLabel2units;
	private JTextField jTextFieldUp;
	private JLabel jLabel1Units;
	private JTextField jTextFieldLow;
	private JLabel jLabelComments;
	private JLabel jLabelUpper;
	private JLabel jLabelLower;
	private JLabel jLabelMeltPtRange;
	private JLabel jLabelHeader;
	private JComponent jComponent;
	private JLabel jLabelDegreeC;
	private NotebookPageModel pageModel;

	public MeltingPointJDialog(JFrame owner, BatchModel ab, JComponent jComponent, JLabel jLabelDegreeC, NotebookPageModel pageModel) {
		super(owner);
		this.owner = owner;
		this.ab = ab;
		this.jComponent = jComponent;
		this.jLabelDegreeC = jLabelDegreeC;
		this.pageModel = pageModel;
		initGUI();
	}

	public void initGUI() {
		try {
			preInitGUI();
			jLabelHeader = new JLabel();
			jLabelMeltPtRange = new JLabel();
			jLabelLower = new JLabel();
			jLabelUpper = new JLabel();
			jLabelComments = new JLabel();
			jTextFieldLow = new JTextField();
			jLabel1Units = new JLabel();
			jTextFieldUp = new JTextField();
			jLabel2units = new JLabel();
			jTextFieldComments = new JTextField();
			jButtonOk = new JButton();
			jButtonCancel = new JButton();
			getContentPane().setLayout(null);
			
			getRootPane().setDefaultButton(jButtonOk);
			setFont(new Font("Dialog", 0, 14));
			setSize(new Dimension(600, 200));
			setTitle("Melting Point Information");
			setResizable(false);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			jLabelHeader.setText("Melting Point Information");
			jLabelHeader.setFont(new Font("Dialog", 1, 14));
			jLabelHeader.setPreferredSize(new Dimension(190, 35));
			jLabelHeader.setBounds(new Rectangle(8, 6, 190, 35));
			getContentPane().add(jLabelHeader);
			jLabelMeltPtRange.setText("Melting Point Range:");
			jLabelMeltPtRange.setPreferredSize(new Dimension(130, 20));
			jLabelMeltPtRange.setBounds(new Rectangle(30, 70, 130, 20));
			getContentPane().add(jLabelMeltPtRange);
			jLabelLower.setText("Lower");
			jLabelLower.setHorizontalAlignment(SwingConstants.CENTER);
			jLabelLower.setVerticalAlignment(SwingConstants.CENTER);
			jLabelLower.setPreferredSize(new Dimension(50, 20));
			jLabelLower.setVerifyInputWhenFocusTarget(true);
			jLabelLower.setBounds(new Rectangle(165, 50, 50, 20));
			jLabelLower.setFocusTraversalKeysEnabled(false);
			getContentPane().add(jLabelLower);
			jLabelUpper.setText("Upper");
			jLabelUpper.setHorizontalAlignment(SwingConstants.CENTER);
			jLabelUpper.setPreferredSize(new Dimension(50, 20));
			jLabelUpper.setBounds(new Rectangle(240, 50, 50, 20));
			getContentPane().add(jLabelUpper);
			jLabelComments.setText("Comments");
			jLabelComments.setHorizontalAlignment(SwingConstants.CENTER);
			jLabelComments.setHorizontalTextPosition(SwingConstants.TRAILING);
			jLabelComments.setPreferredSize(new Dimension(250, 20));
			jLabelComments.setBounds(new Rectangle(320, 50, 250, 20));
			getContentPane().add(jLabelComments);
			jTextFieldLow.setPreferredSize(new Dimension(40, 20));
			jTextFieldLow.setBounds(new Rectangle(165, 70, 40, 20));
			getContentPane().add(jTextFieldLow);
			jTextFieldLow.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					jTextFieldLow.setSelectionEnd(jTextFieldLow.getText().length());
					jTextFieldLow.setSelectionStart(0);
				}
			});
			jLabel1Units.setText("<html>&deg;C</html>");
			jLabel1Units.setFont(jLabelUpper.getFont());
			jLabel1Units.setPreferredSize(new Dimension(40, 20));
			jLabel1Units.setBounds(new Rectangle(205, 70, 40, 20));
			getContentPane().add(jLabel1Units);
			jTextFieldUp.setPreferredSize(new Dimension(40, 20));
			jTextFieldUp.setBounds(new Rectangle(240, 70, 40, 20));
			getContentPane().add(jTextFieldUp);
			jTextFieldUp.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					jTextFieldUp.setSelectionEnd(jTextFieldUp.getText().length());
					jTextFieldUp.setSelectionStart(0);
				}
			});
			jLabel2units.setText("<html>&deg;C</html>");
			jLabel2units.setFont(jLabelUpper.getFont());
			jLabel2units.setPreferredSize(new Dimension(40, 20));
			jLabel2units.setBounds(new Rectangle(280, 70, 40, 20));
			getContentPane().add(jLabel2units);
			jTextFieldComments.setPreferredSize(new Dimension(250, 20));
			jTextFieldComments.setBounds(new Rectangle(320, 70, 250, 20));
			getContentPane().add(jTextFieldComments);
			jButtonOk.setText("OK");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonOk);
			jButtonOk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOkActionPerformed();
				}
			});
			jButtonOk.setPreferredSize(new Dimension(60, 20));
			jButtonOk.setBounds(new Rectangle(220, 130, 60, 20));
			getContentPane().add(jButtonOk);
			jButtonCancel.setText("Cancel");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButtonCancel);
			jButtonCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonCancelActionPerformed();
				}
			});
			jButtonCancel.setPreferredSize(new Dimension(75, 20));
			jButtonCancel.setBounds(new Rectangle(310, 130, 75, 20));
			getContentPane().add(jButtonCancel);
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

	public void preInitGUI() {
		
	}

	public void postInitGUI() {
		setModal(true);
		setLocationRelativeTo(owner);
		setValues();
	}

	public static void showGUI(JFrame owner, BatchModel ab, JComponent jp, JLabel jLabelDegreeC, NotebookPageModel pageModel) {
		try {
			MeltingPointJDialog inst = new MeltingPointJDialog(owner, ab, jp, jLabelDegreeC, pageModel);
			inst.setVisible(true);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(null, e);
		}
	}

	protected void jButtonCancelActionPerformed() {
		dispose();
	}

	protected void jButtonOkActionPerformed() {
		if (ab == null) {
			dispose();
			return;
		}
		
		TemperatureRangeModel meltPt = new TemperatureRangeModel();
		
		double lower = 0;
		double upper = 0;
		
		String s_lower = jTextFieldLow.getText();
		String s_upper = jTextFieldUp.getText();
		
		if (StringUtils.isBlank(s_lower) && StringUtils.isBlank(s_upper)) {
			lower = upper = 0;  // allow user to clear melting point
			jTextFieldComments.setText("");
		} else {
			try {
				lower = Double.parseDouble(s_lower);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Lower Value is invalid.", "Input Error", JOptionPane.ERROR_MESSAGE);
				dispose();
				return;
			}
			try {
				upper = Double.parseDouble(s_upper);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Upper Value is invalid.", "Input Error", JOptionPane.ERROR_MESSAGE);
				dispose();
				return;
			}
  		
			if (lower > upper) {
				JOptionPane.showMessageDialog(this, "Lower cannot be equal or greater than Upper.", "Input Error", JOptionPane.ERROR_MESSAGE);
				dispose();
				return;
			}
		}
		
		meltPt.setLower(lower);
		meltPt.setUpper(upper);
		meltPt.setComment(jTextFieldComments.getText());
		
		ProductBatchModel pb = (ProductBatchModel) ab;
		pb.setMeltPointRange(meltPt);
		
		JTextArea jep = (JTextArea) jComponent;
		String val = meltPt.toString();
		jep.setText(val);
		
		jLabelDegreeC.setText("<html>&deg;C</html>");
		
		dispose();
	}

	public void setValues() {
		ProductBatchModel pb = (ProductBatchModel) ab;
		if (pb.getMeltPointRange() == null)
			return;
		jTextFieldLow.setText(pb.getMeltPointRange().getLower() + "");
		jTextFieldUp.setText(pb.getMeltPointRange().getUpper() + "");
		jTextFieldComments.setText(pb.getMeltPointRange().getComment());
		if (pageModel != null)
			pageModel.setModelChanged(true);
		MasterController.getGUIComponent().enableSaveButtons();
	}

	public void dispose() {
		ab = new ProductBatchModel();
		ab = null;
		jComponent.removeAll();
		jComponent = null;
		super.dispose();
	}
}
