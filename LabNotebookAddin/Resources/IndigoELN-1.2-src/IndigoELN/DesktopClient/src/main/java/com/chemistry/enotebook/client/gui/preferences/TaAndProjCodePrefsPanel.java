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
package com.chemistry.enotebook.client.gui.preferences;

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.common.utils.CeNComboBox;
import com.chemistry.enotebook.utils.CodeTableUtils;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TaAndProjCodePrefsPanel extends JPanel {

	private static final long serialVersionUID = -5843647752504284683L;
	
	private JCheckBox taAndProjCodePrefsCheckBox;
	private CeNComboBox taComboBox;
	private CeNComboBox projCodeComboBox;

	public TaAndProjCodePrefsPanel() {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(6, 6, 6, 6));
		
		taAndProjCodePrefsCheckBox = new JCheckBox("Use this Therapeutic Area and Project Code for newly created experiments:");
		taAndProjCodePrefsCheckBox.setFont(taAndProjCodePrefsCheckBox.getFont().deriveFont(Font.BOLD));
		taAndProjCodePrefsCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				taAndProjCodePrefsCheckBoxActionPerformed(e);
			}
		});
		
		JPanel labelPanel = new JPanel(new GridLayout(2, 1, 6, 6));
		JPanel comboPanel = new JPanel(new GridLayout(2, 1, 6, 6));
		
		JPanel taAndProjCodePanel = new JPanel(new BorderLayout(6, 6));
		JLabel taLabel = new JLabel("Therapeutic Area:");
		JLabel projCodeLabel = new JLabel("Project code & Name:");
		
		taLabel.setFont(taLabel.getFont().deriveFont(Font.BOLD));
		projCodeLabel.setFont(projCodeLabel.getFont().deriveFont(Font.BOLD));
		
		labelPanel.add(taLabel);
		labelPanel.add(projCodeLabel);
		
		taComboBox = new CeNComboBox();
		projCodeComboBox = new CeNComboBox();
		
		taComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				taComboBoxActionPerformed(e);
			}
		});
				
		comboPanel.add(taComboBox);
		comboPanel.add(projCodeComboBox);
		
		taAndProjCodePanel.add(taAndProjCodePrefsCheckBox, BorderLayout.NORTH);
		taAndProjCodePanel.add(labelPanel, BorderLayout.WEST);
		taAndProjCodePanel.add(comboPanel, BorderLayout.CENTER);
		taAndProjCodePanel.setBorder(new EmptyBorder(6, 6, 6, 6));
		
		JPanel panelToLeft = new JPanel(new BorderLayout());
		panelToLeft.setBorder(new BevelBorder(BevelBorder.LOWERED));
		panelToLeft.add(taAndProjCodePanel, BorderLayout.WEST);
		
		JPanel panelToNorth = new JPanel(new BorderLayout());
		panelToNorth.add(panelToLeft, BorderLayout.WEST);
		
		add(panelToNorth, BorderLayout.NORTH);
		
		AutoCompleteDecorator.decorate(taComboBox);
		AutoCompleteDecorator.decorate(projCodeComboBox);
		
		CodeTableUtils.fillComboBoxWithTAs(taComboBox);
		CodeTableUtils.fillComboBoxWithProjects(projCodeComboBox, null);
		
		setUseTA(false);
	}

	private void taComboBoxActionPerformed(ActionEvent e) {
		CodeTableUtils.fillComboBoxWithProjects(projCodeComboBox, getTaCode());
	}

	private void taAndProjCodePrefsCheckBoxActionPerformed(ActionEvent e) {
		taComboBox.setEnabled(taAndProjCodePrefsCheckBox.isSelected());
		projCodeComboBox.setEnabled(taAndProjCodePrefsCheckBox.isSelected());
	}
	
	public boolean getUseTA() {
		return taAndProjCodePrefsCheckBox.isSelected();
	}
	
	public void setUseTA(boolean b) {
		taAndProjCodePrefsCheckBox.setSelected(b);
		taComboBox.setEnabled(b);
		projCodeComboBox.setEnabled(b);
	}
	
	public String getTaCode() {
		String result = "";
		if (taComboBox.getSelectedIndex() != 0) {
			try {
				result = CodeTableCache.getCache().getTAsCode((String) taComboBox.getSelectedItem());
				if (result == null) {
					result = (String) taComboBox.getSelectedItem();
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(this, e);
			}
		}
		return result;
	}
	
	public void setTaCode(String taCode) {
		if (StringUtils.isNotBlank(taCode)) {
			try {
				String descr = CodeTableCache.getCache().getTAsDescription(taCode);
				if (descr == null) {
					if (taCode.length() > 0) {
						taComboBox.setSelectedItem(taCode);
					} else {
						taComboBox.setSelectedIndex(0);
					}
				} else {
					taComboBox.setSelectedItem(descr);
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(this, e);
			}
			CodeTableUtils.fillComboBoxWithProjects(projCodeComboBox, getTaCode());
		} else {
			CodeTableUtils.fillComboBoxWithProjects(projCodeComboBox, null);
			taComboBox.setSelectedIndex(0);
		}
	}

	public String getProjectCode() {
		String result = "";
		String item = (String) projCodeComboBox.getSelectedItem();
		if (projCodeComboBox.getSelectedIndex() != 0 && item != null) {
			try {
				int pos = item.indexOf(" -");
				if (pos >= 0) {
					result = item.substring(0, pos);
				} else {
					result = item;
				}
				if (result == null) {
					result = "";
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(this, e);
			}
		}
		return result;
	}

	public void setProjectCode(String projCode) {
		if (StringUtils.isNotBlank(projCode)) {
			try {
				String descr = CodeTableCache.getCache().getProjectsDescription(projCode);
				if (descr == null) {
					if (projCode.length() > 0)
						projCodeComboBox.setSelectedItem(projCode);
					else {
						projCodeComboBox.setSelectedIndex(0);
					}
				} else {
					projCodeComboBox.setSelectedItem(projCode + " - " + descr);
				}
			} catch (Exception e) {
				CeNErrorHandler.getInstance().logExceptionMsg(this, e);
			}
		} else {
			projCodeComboBox.setSelectedIndex(0);
		}
	}
}
