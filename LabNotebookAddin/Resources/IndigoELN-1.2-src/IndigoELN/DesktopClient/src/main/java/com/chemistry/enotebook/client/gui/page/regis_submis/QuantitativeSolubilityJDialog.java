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
import com.chemistry.enotebook.client.gui.common.utils.JTextFieldFilter;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegOperatorCache;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegUnitCache;
import com.chemistry.enotebook.utils.CeNDialog;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class QuantitativeSolubilityJDialog extends CeNDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7533251816893833055L;
	private JPanel jPanelMain;
	private JButton jButtonOK;
	private JPanel jPanelOK;
	private JLabel jLabelTitle;
	private JPanel jPanelSolubilityValue;
	private JPanel jPanelTitle;
	private String quanValueString = "";
	private JComboBox jComboBoxOperator;
	private JTextField jTextFieldtest;
	private JComboBox jComboBoxUnit;
	private JDialogEditCompRegInfo jDialogEditCompRegInfo;
	private ArrayList unitList = new ArrayList();
	private ArrayList operatorList = new ArrayList();

	public QuantitativeSolubilityJDialog(JDialogEditCompRegInfo parent) {
		super(parent);
		jDialogEditCompRegInfo = parent;
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
			jLabelTitle = new JLabel();
			jPanelSolubilityValue = new JPanel();
			jPanelOK = new JPanel();
			jButtonOK = new JButton();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setSize(new java.awt.Dimension(477, 152));
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
			BorderLayout jLabelTitleLayout = new BorderLayout();
			jLabelTitle.setLayout(jLabelTitleLayout);
			jLabelTitleLayout.setHgap(0);
			jLabelTitleLayout.setVgap(0);
			jLabelTitle.setText("Quantitative Solubility");
			jLabelTitle.setFont(new java.awt.Font("sansserif", 1, 12));
			jLabelTitle.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelTitle.add(jLabelTitle, BorderLayout.CENTER);
			BorderLayout jPanelSolubilityValueLayout = new BorderLayout();
			jPanelSolubilityValue.setLayout(jPanelSolubilityValueLayout);
			jPanelSolubilityValueLayout.setHgap(0);
			jPanelSolubilityValueLayout.setVgap(0);
			jPanelSolubilityValue.setPreferredSize(new java.awt.Dimension(470, 61));
			jPanelSolubilityValue.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelMain.add(jPanelSolubilityValue, BorderLayout.CENTER);
			jPanelOK.setVisible(true);
			jPanelOK.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelMain.add(jPanelOK, BorderLayout.SOUTH);
			jButtonOK.setText("OK");
			jButtonOK.setFont(new java.awt.Font("sansserif", 1, 11));
			jPanelOK.add(jButtonOK);
			jButtonOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOKActionPerformed(evt);
				}
			});
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					QuantitativeSolubilityJDialog.this.setVisible(false);
					QuantitativeSolubilityJDialog.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					QuantitativeSolubilityJDialog.this.removeAll();
					QuantitativeSolubilityJDialog.this.setVisible(false);
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
		resetSolubilityValuePanelLayout();
		// reposition JDialog to the center of the screen
		Point loc = jDialogEditCompRegInfo.getLocation();
		Dimension dim = jDialogEditCompRegInfo.getSize();
		setLocation(loc.x + (dim.width - getSize().width) / 2, loc.y + (dim.height - getSize().height) / 2);
	}

	public void resetSolubilityValuePanelLayout() {
		jPanelSolubilityValue.removeAll();
		double border = 4;
		double size[][] = { { border, 0.30, 0.04, TableLayout.FILL, 0.04, 0.30, border }, // Columns
				{ 20, 20 } }; // Rows
		jPanelSolubilityValue.setLayout(new TableLayout(size));
		JLabel operatorLabel = new JLabel("Operator");
		jPanelSolubilityValue.add(operatorLabel, "1, 0");
		JLabel valueLabel = new JLabel("Value");
		jPanelSolubilityValue.add(valueLabel, "3, 0");
		JLabel unitLabel = new JLabel("Unit");
		jPanelSolubilityValue.add(unitLabel, "5, 0");
		jComboBoxOperator = new JComboBox();
		for (int i = 0; i < this.getOperatorList().size(); i++) {
			jComboBoxOperator.addItem(((RegOperatorCache) this.getOperatorList().get(i)).getDescription());
		}
		jPanelSolubilityValue.add(jComboBoxOperator, "1, 1");
		jTextFieldtest = new JTextField();
		jTextFieldtest.setText("1.0");
		jTextFieldtest.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
		jPanelSolubilityValue.add(jTextFieldtest, "3, 1");
		jComboBoxUnit = new JComboBox();
		for (int i = 0; i < this.getUnitList().size(); i++) {
			jComboBoxUnit.addItem(((RegUnitCache) this.getUnitList().get(i)).getDescription());
		}
		jPanelSolubilityValue.add(jComboBoxUnit, "5, 1");
		jPanelSolubilityValue.repaint();
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
			QuantitativeSolubilityJDialog inst = new QuantitativeSolubilityJDialog(null);
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonOKActionPerformed(ActionEvent evt) {
		if (validateQuanSolubility()) {
			this.quanValueString = this.jComboBoxOperator.getSelectedItem() + " " + this.jTextFieldtest.getText() + " "
					+ this.jComboBoxUnit.getSelectedItem();
			jDialogEditCompRegInfo.setSolubilityValueString(quanValueString);
			jDialogEditCompRegInfo.resetSolubilityValue();
			this.setVisible(false);
			this.dispose();
		} else {
			JOptionPane.showMessageDialog(this, "Quantitative Solubility Value cannot be empty.");
		}
	}

	private boolean validateQuanSolubility() {
		if (this.jTextFieldtest.getText().trim().length() == 0) {
			return false;
		} else {
			return true;
		}
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
	 * @return Returns the operatorList.
	 */
	public ArrayList getOperatorList() {
		return operatorList;
	}

	/**
	 * @param operatorList
	 *            The operatorList to set.
	 */
	public void setOperatorList(ArrayList operatorList) {
		this.operatorList = operatorList;
	}

	/**
	 * @return Returns the unitList.
	 */
	public ArrayList getUnitList() {
		return unitList;
	}

	/**
	 * @param unitList
	 *            The unitList to set.
	 */
	public void setUnitList(ArrayList unitList) {
		this.unitList = unitList;
	}

	public void dispose() {
		jDialogEditCompRegInfo = new JDialogEditCompRegInfo(null);
		jDialogEditCompRegInfo = null;
		super.dispose();
	}
}