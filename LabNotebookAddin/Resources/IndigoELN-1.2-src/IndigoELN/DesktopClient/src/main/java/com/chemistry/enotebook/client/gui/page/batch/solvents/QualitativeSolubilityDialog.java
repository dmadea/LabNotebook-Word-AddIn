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
package com.chemistry.enotebook.client.gui.page.batch.solvents;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.regis_submis.cacheobject.RegQualitativeCache;
import info.clearthought.layout.TableLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class QualitativeSolubilityDialog extends javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3081077192586178109L;
	private JPanel jPanelMain;
	private JButton jButtonOK;
	private JPanel jPanelOK;
	private JLabel jLabelTitle;
	private JPanel jPanelValue;
	private JPanel jPanelTitle;
	private String quanValueString = "";
	private JComboBox jComboBoxSolubilityValue;
	//private JDialogEditCompRegInfo jDialogEditCompRegInfo;
	private EditSolubilityInSolventsDialog owner;
	private List<RegQualitativeCache> solventDescList = new ArrayList<RegQualitativeCache>();

//	public QualitativeSolubilityJDialog(JDialogEditCompRegInfo owner) {
//		super(owner);
//		jDialogEditCompRegInfo = owner;
//		initGUI();
//	}

	public QualitativeSolubilityDialog(EditSolubilityInSolventsDialog owner) {
		super(owner);
		this.owner = owner;
		//jDialogEditCompRegInfo = owner;
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
			jPanelValue = new JPanel();
			jPanelOK = new JPanel();
			jButtonOK = new JButton();
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
			thisLayout.setHgap(0);
			thisLayout.setVgap(0);
			this.setSize(new java.awt.Dimension(468, 142));
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
			jPanelTitle.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelMain.add(jPanelTitle, BorderLayout.NORTH);
			jLabelTitle.setText("Qualitative Solubility");
			jLabelTitle.setFont(new java.awt.Font("sansserif", 1, 12));
			jPanelTitle.add(jLabelTitle, BorderLayout.CENTER);
			BorderLayout jPanelValueLayout = new BorderLayout();
			jPanelValue.setLayout(jPanelValueLayout);
			jPanelValueLayout.setHgap(0);
			jPanelValueLayout.setVgap(0);
			jPanelValue.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelMain.add(jPanelValue, BorderLayout.CENTER);
			FlowLayout jPanelOKLayout = new FlowLayout();
			jPanelOK.setLayout(jPanelOKLayout);
			jPanelOKLayout.setAlignment(FlowLayout.CENTER);
			jPanelOKLayout.setHgap(5);
			jPanelOKLayout.setVgap(5);
			jPanelOK.setVisible(true);
			jPanelOK.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			jPanelMain.add(jPanelOK, BorderLayout.SOUTH);
			jButtonOK.setText("OK");
			jButtonOK.setFont(new java.awt.Font("sansserif", 1, 11));
			jButtonOK.setPreferredSize(new java.awt.Dimension(70, 26));
			jPanelOK.add(jButtonOK);
			jButtonOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					jButtonOKActionPerformed(evt);
				}
			});
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					QualitativeSolubilityDialog.this.setVisible(false);
					QualitativeSolubilityDialog.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					QualitativeSolubilityDialog.this.removeAll();
					QualitativeSolubilityDialog.this.setVisible(false);
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
		// this.setSolventDescList(CodeTableCache.getCache().)
		// reposition JDialog to the center of the screen
		Point loc = this.owner.getLocation(); //jDialogEditCompRegInfo.getLocation();
		Dimension dim = new Dimension(200, 300); //jDialogEditCompRegInfo.getSize();
		setLocation(loc.x + (dim.width - getSize().width) / 2, loc.y + (dim.height - getSize().height) / 2);
	}

	public void resetSolubilityValuePanelLayout() {
		jPanelValue.removeAll();
		double border = 4;
		double size[][] = { { border, TableLayout.FILL, border }, // Columns
				{ 20, 20 } }; // Rows
		jPanelValue.setLayout(new TableLayout(size));
		JLabel operatorLabel = new JLabel("Please select one.");
		jPanelValue.add(operatorLabel, "1, 0");
		jComboBoxSolubilityValue = new JComboBox();
		for (int i = 0; i < this.getSolventDescList().size(); i++) {
			jComboBoxSolubilityValue.addItem(((RegQualitativeCache) this.getSolventDescList().get(i)).getDescription());
		}
		jPanelValue.add(jComboBoxSolubilityValue, "1, 1");
		jPanelValue.repaint();
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
			QualitativeSolubilityDialog inst = new QualitativeSolubilityDialog(null);
			inst.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Auto-generated event handler method */
	protected void jButtonOKActionPerformed(ActionEvent evt) {
		this.quanValueString = (String) this.jComboBoxSolubilityValue.getSelectedItem();
		this.owner.setSolubilityValueString(quanValueString);
		this.owner.resetSolubilityValue();
		this.setVisible(false);
		this.dispose();
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
	 * @return Returns the solventDescList.
	 */
	public List<RegQualitativeCache> getSolventDescList() {
		return solventDescList;
	}

	/**
	 * @param solventDescList
	 *            The solventDescList to set.
	 */
	public void setSolventDescList(List<RegQualitativeCache> solventDescList) {
		this.solventDescList = solventDescList;
	}

//	public void dispose() {
//		//jDialogEditCompRegInfo = new JDialogEditCompRegInfo(null);
//		//jDialogEditCompRegInfo = null;
//		//super.dispose();
//	}
}
