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
package com.chemistry.enotebook.client.gui.page.analytical.parallel;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNGUIUtils;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.PAnalyticalAdvancedLinkDialog;
import com.chemistry.enotebook.utils.CeNDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PFileTypesDialog extends CeNDialog {
	
	private static final long serialVersionUID = -2447213083987448843L;
	
	private JButton cancelButton;
	private JButton okButton;
	private JCheckBox[] jCheckBoxSites;
	private List<String> al_SelectedFileTypes = new ArrayList<String>();
	private List<String> al_AllFileTypes = new ArrayList<String>();

	private Dialog owner = null;
	
	public PFileTypesDialog(JDialog owner, List<String> al_AllFileTypes) {
		super(owner);
		this.owner = owner;
		setModal(true);
		this.al_AllFileTypes = al_AllFileTypes;
		initGUI();
	}

	public void initGUI() {
		try {
			this.getContentPane().setLayout(null);
			this.setTitle("Select File Types");
			this.setSize(new java.awt.Dimension(270, 270));
			if (!al_AllFileTypes.contains("All")) {
				al_AllFileTypes.add("All");
			}
			jCheckBoxSites = new JCheckBox[al_AllFileTypes.size()];
			for (int i = 0, j = 25; i < al_AllFileTypes.size(); i++, j += 15) {
				Action action = createAction(al_AllFileTypes.get(i).toString());
				jCheckBoxSites[i] = new JCheckBox(action);
				CeNGUIUtils.styleComponentText(Font.BOLD, jCheckBoxSites[i]);
				jCheckBoxSites[i].setText(al_AllFileTypes.get(i).toString());
				jCheckBoxSites[i].setPreferredSize(new java.awt.Dimension(30, 20));
				if (i % 2 == 0) {
					jCheckBoxSites[i].setBounds(new java.awt.Rectangle(12, j, 100, 20));
				} else {
					jCheckBoxSites[i].setBounds(new java.awt.Rectangle(135, j - 15, 100, 20));
				}
				this.getContentPane().add(jCheckBoxSites[i]);
			}
			okButton = new JButton();
			okButton.setText("OK");
			CeNGUIUtils.styleComponentText(Font.BOLD, okButton);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						String s_text = formText(al_SelectedFileTypes);
						PAnalyticalAdvancedLinkDialog.setFileTypesText(s_text);
					} catch (Exception ex) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, ex);
					}
					okCancelButtonsClicked(e);
				}				
			});
			okButton.setPreferredSize(new java.awt.Dimension(60, 20));
			okButton.setBounds(new java.awt.Rectangle(41, 188, 60, 20));
			this.getContentPane().add(okButton);
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			CeNGUIUtils.styleComponentText(Font.BOLD, cancelButton);
			cancelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					okCancelButtonsClicked(e);
				}				
			});
			cancelButton.setPreferredSize(new java.awt.Dimension(80, 20));
			cancelButton.setBounds(new java.awt.Rectangle(138, 187, 80, 20));
			this.getContentPane().add(cancelButton);
			this.getRootPane().setDefaultButton(okButton);
			this.setResizable(false);
			this.setDefaultCloseOperation(PFileTypesDialog.DISPOSE_ON_CLOSE);
			this.setLocationRelativeTo(owner);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	protected void okCancelButtonsClicked(ActionEvent evt) {
		this.setVisible(false);
		this.dispose();
	}

	protected Action createAction(String label) {
		Action action = new AbstractAction(label) {
			private static final long serialVersionUID = -3234417976309377488L;
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

	public String formText(List<String> al) {
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
