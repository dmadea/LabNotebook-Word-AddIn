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
package com.chemistry.enotebook.client.gui.page.analytical;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.AdvancedLinkJDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This code was generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is
 * being used commercially (ie, by a for-profit company or business) then you should purchase a license - please visit
 * www.cloudgarden.com for details.
 */
public class FileTypesDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = 3926170977222732497L;
	private JButton jButton2;
	private JButton jButton1;
	private JCheckBox[] jCheckBoxSites;
	private ArrayList<String> al_SelectedFileTypes = new ArrayList<String>();
	private ArrayList<String> al_AllFileTypes = new ArrayList<String>();

	public FileTypesDialog(JDialog owner, List<String> al_AllFileTypes) {
		super(owner);
		setModal(true);
		this.al_AllFileTypes = new ArrayList<String>(al_AllFileTypes);
		initGUI();
	}

	/**
	 * Initializes the GUI. Auto-generated code - any changes you make will disappear.
	 */
	public void initGUI() {
		try {
			preInitGUI();
			this.getContentPane().setLayout(null);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setTitle("Select File Types");
			this.setSize(new java.awt.Dimension(270, 270));
			setLocation(getParent().getX() + (getParent().getWidth() - getWidth()) / 2, getParent().getY() + 50);
			if (!al_AllFileTypes.contains("All"))
				al_AllFileTypes.add("All");
			jCheckBoxSites = new JCheckBox[al_AllFileTypes.size()];
			for (int i = 0, j = 25; i < al_AllFileTypes.size(); i++, j += 15) {
				Action action = createAction(al_AllFileTypes.get(i).toString());
				jCheckBoxSites[i] = new JCheckBox(action);
				CeNGUIUtils.styleComponentText(Font.BOLD, jCheckBoxSites[i]);
				jCheckBoxSites[i].setText(al_AllFileTypes.get(i).toString());
				jCheckBoxSites[i].setPreferredSize(new java.awt.Dimension(30, 20));
				if (i % 2 == 0)
					jCheckBoxSites[i].setBounds(new java.awt.Rectangle(12, j, 100, 20));
				else
					jCheckBoxSites[i].setBounds(new java.awt.Rectangle(135, j - 15, 100, 20));
				this.getContentPane().add(jCheckBoxSites[i]);
			}
			jButton1 = new JButton();
			jButton1.setText("OK");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButton1);
			jButton1.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					try {
						String s_text = formText(al_SelectedFileTypes);
						AdvancedLinkJDialog.setFileTypesText(s_text);
					} catch (Exception e) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, e);
					}
					jButtonCancelMouseClicked(evt);
				}
			});
			jButton1.setPreferredSize(new java.awt.Dimension(60, 20));
			jButton1.setBounds(new java.awt.Rectangle(41, 188, 60, 20));
			this.getContentPane().add(jButton1);
			jButton2 = new JButton();
			jButton2.setText("Cancel");
			CeNGUIUtils.styleComponentText(Font.BOLD, jButton2);
			jButton2.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent evt) {
					jButtonCancelMouseClicked(evt);
				}
			});
			jButton2.setPreferredSize(new java.awt.Dimension(80, 20));
			jButton2.setBounds(new java.awt.Rectangle(138, 187, 80, 20));
			this.getContentPane().add(jButton2);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					FileTypesDialog.this.setVisible(false);
					FileTypesDialog.this.dispose();
				}

				public void windowClosed(WindowEvent e) {
					FileTypesDialog.this.removeAll();
					FileTypesDialog.this.setVisible(false);
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
	}

	protected void jButtonCancelMouseClicked(MouseEvent evt) {
		this.setVisible(false);
		this.dispose();
	}

	protected Action createAction(String label) {
		Action action = new AbstractAction(label) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -6111155449867416819L;

			// This method is called when the button is pressed
			public void actionPerformed(ActionEvent evt) {
				// Perform action
				JCheckBox cb = (JCheckBox) evt.getSource();
				// Determine status
				boolean isSel = cb.isSelected();
				if (isSel) {
					al_SelectedFileTypes.add(cb.getText());
				} else {
					// The checkbox is now deselected
					al_SelectedFileTypes.remove(cb.getText());
				}
			}
		};
		return action;
	}

	public String formText(ArrayList al) {
		String s_return = "";
		if (al.contains("All")) {
			al_AllFileTypes.remove("All");
			s_return = al_AllFileTypes.toString();
			s_return = s_return.replace('[', ' ');
			s_return = s_return.replace(']', ' ');
		} else {
			s_return = al.toString();
			s_return = s_return.replace('[', ' ');
			s_return = s_return.replace(']', ' ');
		}
		return s_return.trim();
	}

	public void dispose() {
		super.dispose();
	}
}
