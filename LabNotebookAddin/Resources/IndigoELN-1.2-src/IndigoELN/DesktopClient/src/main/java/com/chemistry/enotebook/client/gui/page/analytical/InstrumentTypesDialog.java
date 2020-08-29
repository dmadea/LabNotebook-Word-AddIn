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
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class InstrumentTypesDialog extends CeNDialog {
	
	private static final long serialVersionUID = 6347830251399833113L;
	
	private JButton cancelButton;
	private JButton okButton;
	private JCheckBox[] jCheckBoxSites;
	private List<String> al_SelectedInsTypes = new ArrayList<String>();
	private List<String> al_AllInsTypes = new ArrayList<String>();
	private boolean isClosed;

	private Dialog owner = null;
	
	public InstrumentTypesDialog(JDialog owner, List<String> al_AllInsTypes, List<String> selInstrTypes) {
		super(owner);
		this.owner = owner;
		this.al_AllInsTypes = al_AllInsTypes;
		setModal(true);
		initGUI();
		checkSelectedTypes(selInstrTypes);
	}

	private void checkSelectedTypes(List<String> selInstrTypes) {
		if (selInstrTypes != null && jCheckBoxSites != null) {
			for (String type : selInstrTypes) {
				for (JCheckBox box : jCheckBoxSites) {
					if (StringUtils.equals(StringUtils.trim(type), StringUtils.trim(box.getText()))) {
						if (!box.isSelected()) {
							box.doClick();							
						}
					}
				}
			}
		}
	}

	public void initGUI() {
		try {
			this.setSize(270, 270);
			JPanel instrumentsPanel = new JPanel();
			instrumentsPanel.setLayout(null);
			this.setTitle("Select Instruments");
			if (!al_AllInsTypes.contains("All")) {
				al_AllInsTypes.add("All");
			}
			jCheckBoxSites = new JCheckBox[al_AllInsTypes.size()];
			for (int i = 0, j = 25; i < al_AllInsTypes.size(); i++, j += 15) {
				Action action = createAction(al_AllInsTypes.get(i).toString());
				jCheckBoxSites[i] = new JCheckBox(action);
				jCheckBoxSites[i].setText(al_AllInsTypes.get(i).toString());
				CeNGUIUtils.styleComponentText(Font.BOLD, jCheckBoxSites[i]);
				jCheckBoxSites[i].setPreferredSize(new java.awt.Dimension(30, 20));
				if (i % 2 == 0) {
					jCheckBoxSites[i].setBounds(new java.awt.Rectangle(12, j, 100, 20));
				} else {
					jCheckBoxSites[i].setBounds(new java.awt.Rectangle(135, j - 15, 100, 20));
				}
				instrumentsPanel.add(jCheckBoxSites[i]);
			}
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(instrumentsPanel, BorderLayout.CENTER);
			JPanel buttonPanel = new JPanel();
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
			//jButton1.setBounds(new java.awt.Rectangle(41, 188, 60, 20));
			//this.getContentPane().add(jButton1);
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			CeNGUIUtils.styleComponentText(Font.BOLD, cancelButton);
			cancelButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					okCancelButtonsClicked(e);
				}				
			});
			cancelButton.setPreferredSize(new java.awt.Dimension(80, 20));
			//jButton2.setBounds(new java.awt.Rectangle(138, 187, 80, 20));
			//this.getContentPane().add(jButton2);
			buttonPanel.add(okButton);
			buttonPanel.add(cancelButton);
			this.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
			//this.pack();
			this.getRootPane().setDefaultButton(okButton);
			this.setResizable(false);
			this.setDefaultCloseOperation(InstrumentTypesDialog.DISPOSE_ON_CLOSE);
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
			private static final long serialVersionUID = 2739100616934030505L;
			// This method is called when the button is pressed
			public void actionPerformed(ActionEvent evt) {
				// Perform action
				JCheckBox cb = (JCheckBox) evt.getSource();
				// Determine status
				boolean isSel = cb.isSelected();
				if (isSel) {
					al_SelectedInsTypes.add(cb.getText());
				} else {
					// The checkbox is now deselected
					al_SelectedInsTypes.remove(cb.getText());
				}
			}
		};
		return action;
	}

	public String formText(List<String> al) {
		String s_return = "";
		if (al.contains("All")) {
			al_AllInsTypes.remove("All");
			s_return = al_AllInsTypes.toString();
			s_return = s_return.replace('[', ' ');
			s_return = s_return.replace(']', ' ');
		} else {
			s_return = al_SelectedInsTypes.toString();
			s_return = s_return.replace('[', ' ');
			s_return = s_return.replace(']', ' ');
		}
		return s_return.trim();
	}

	public boolean isDialogClosed() {
		return isClosed;
	}

	public String getInsTypesText() {
		return formText(al_SelectedInsTypes);
	}

	public void dispose() {
		super.dispose();
	}
}
