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
import com.chemistry.enotebook.utils.CeNDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SitesDialog extends CeNDialog {
	
	private static final long serialVersionUID = 7617253828720008031L;
	
	private JButton cancelButton;
	private JButton okButton;
	private JCheckBox[] sitesCheckBox;
	private List<String> al_SelectedSites = new ArrayList<String>();
	private List<String> al_AllSites = new ArrayList<String>();
	private boolean isClosed;

	private Dialog owner = null;
	
	public SitesDialog(JDialog owner, Iterator<String> compoundManagementSiteDispalyNameItr) {
		super(owner);
		this.owner = owner;
		while(compoundManagementSiteDispalyNameItr.hasNext()) {
			al_AllSites.add(compoundManagementSiteDispalyNameItr.next());
		}
		this.isClosed = false;
		setModal(true);
		initGUI();
	}

	public void initGUI() {
		try {
			this.getContentPane().setLayout(null);
			this.setTitle("Select AnalyticalService Data Source");
			this.setSize(new java.awt.Dimension(330,330));
			if (!al_AllSites.contains("All")) {
				al_AllSites.add("All");
			}
			sitesCheckBox = new JCheckBox[al_AllSites.size()];
			for (int i = 0, j = 25; i < al_AllSites.size(); i++, j += 15) {
				Action action = createAction(al_AllSites.get(i).toString());
				sitesCheckBox[i] = new JCheckBox(action);
				sitesCheckBox[i].setText(al_AllSites.get(i).toString());
				CeNGUIUtils.styleComponentText(Font.BOLD, sitesCheckBox[i]);
				sitesCheckBox[i].setPreferredSize(new java.awt.Dimension(75,20));
				if(i%2 ==0) {
					sitesCheckBox[i].setBounds(new java.awt.Rectangle(12,j,130,20));
				} else {
					sitesCheckBox[i].setBounds(new java.awt.Rectangle(165,j-15,130,20));
				}
				this.getContentPane().add(sitesCheckBox[i]);
			}
			okButton = new JButton();
			okButton.setText("OK");
			CeNGUIUtils.styleComponentText(Font.BOLD, okButton);
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					isClosed = true;
					okCancelButtonsClicked(e);
				}				
			});
			okButton.setPreferredSize(new java.awt.Dimension(60, 20));
			okButton.setBounds(new java.awt.Rectangle(60, 270, 60, 20));
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
			cancelButton.setBounds(new java.awt.Rectangle(180, 270, 80, 20));
			this.getContentPane().add(cancelButton);
			this.getRootPane().setDefaultButton(okButton);
			this.setResizable(false);
			this.setDefaultCloseOperation(SitesDialog.DISPOSE_ON_CLOSE);
			this.setLocationRelativeTo(owner);
		} catch (Exception e) {
			CeNErrorHandler.getInstance().logExceptionMsg(this, e);
		}
	}

	protected void okCancelButtonsClicked(ActionEvent e) {
		this.setVisible(false);
		this.dispose();
	}

	protected Action createAction(String label) {
		Action action = new AbstractAction(label) {
			private static final long serialVersionUID = -4029690833067100283L;
			// This method is called when the button is pressed
			public void actionPerformed(ActionEvent evt) {
				// Perform action
				JCheckBox cb = (JCheckBox) evt.getSource();
				// Determine status
				boolean isSel = cb.isSelected();
				if (isSel) {
					al_SelectedSites.add(cb.getText());
				} else {
					// The checkbox is now deselected
					al_SelectedSites.remove(cb.getText());
				}
			}
		};
		return action;
	}

	public String formText(List<String> al) {
		String s_return = "";
		if (al.contains("All")) {
			al_AllSites.remove("All");
			s_return = al_AllSites.toString();
			s_return = s_return.replace('[', ' ');
			s_return = s_return.replace(']', ' ');
		} else {
			s_return = al.toString();
			s_return = s_return.replace('[', ' ');
			s_return = s_return.replace(']', ' ');
		}
		return s_return.trim();
	}

	public boolean isDialogClosed() {
		return isClosed;
	}

	public String getSitesText() {
		return formText(al_SelectedSites);
	}

	public void dispose() {
		super.dispose();
	}
}
