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
package com.chemistry.enotebook.client.gui.page.experiment.table;

/**
 * 
 *
 */

import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.experiment.common.MolString;
import com.chemistry.enotebook.utils.DefaultPreferences;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PcenPlateSummaryTableViewCellRendere implements TableCellRenderer {
	private Color foreground = DefaultPreferences.getRowForegroundColor(); //new Color(30, 64, 124);
	private Color background = DefaultPreferences.getEditableRowBackgroundColor(); //new Color(255, 255, 245);
	private Color selectedForeground = DefaultPreferences.getSelectedRowForegroundColor(); //new Color(255, 255, 245);
	private Color selectedBackground = DefaultPreferences.getSelectedRowBackgroundColor(); //new Color(100, 100, 100);

	private Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	private JLabel processingLabel = new JLabel("Processing ... ");
	private JLabel naLabel = new JLabel("N/A");
	private Border bkgrnd = null;
	protected boolean m_selected;
	private BufferedImage spotFire = null;
	private JCheckBox chkBox = new JCheckBox();
	private JPanel chkBoxPanel = new JPanel();

	// AmountCellRenderer renderer = new AmountCellRenderer();

	public PcenPlateSummaryTableViewCellRendere() {
		naLabel.setFont(DefaultPreferences.getStandardTableCellFont());
		naLabel.setHorizontalAlignment(SwingConstants.CENTER);
		naLabel.setOpaque(false);
		naLabel.setOpaque(true);
		naLabel.setBorder(noFocusBorder);
		setup();
	}

	private void setup() {

		chkBoxPanel.add(chkBox);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		PCeNTableView tableView = (PCeNTableView) table;
		// before all, put default color
		JLabel label = new JLabel();
		if (value instanceof MolString) {
			ChimeRenderingPanel chimeRenderingPanel = new ChimeRenderingPanel();
			MolString molString = (MolString) value;
			String molData = molString.getMolString();
			chimeRenderingPanel.setRendererData(molData);
			if (isSelected) {
				chimeRenderingPanel.setForeground(selectedForeground);
				chimeRenderingPanel.setBackground(selectedBackground);
			} else {
				chimeRenderingPanel.setForeground(foreground);
				chimeRenderingPanel.setBackground(background);
			}
			int a = molString.getIndex() + 1;
			if (a != 0)
				chimeRenderingPanel.setLabel(a + "");
			// deal with selection colors
			else
				chimeRenderingPanel.setImage(null);
			chimeRenderingPanel.setBorder(noFocusBorder);
			// chimeRenderingPanel.setPropertiesToolTip((Hashtable)(collection.getProperties().elementAt(realIndex)));
			return chimeRenderingPanel;
			// } else if (value instanceof Amount) {
			// return renderer.getTableCellRendererComponent(tableView, value, isSelected, hasFocus, row, column);
			/*
			 * } else if (value instanceof RackFlagType) { RackFlagType mRackFlagType = (RackFlagType) value; label.setText("haha");
			 * if (isSelected) { label.setForeground(selectedForeground); label.setBackground(selectedBackground); } else {
			 * label.setForeground(foreground); label.setBackground(background); } return label;
			 */

		} else if (value instanceof String) {
			String s = (String) value;

			// //System.out.println(s);
			if (s.length() < 21) {
				// JLabel label = new JLabel();
				label.setOpaque(false);
				label.setOpaque(true);
				label.setBorder(noFocusBorder);
				label.setText(s);
				label.setToolTipText(s);
				label.setFont(DefaultPreferences.getStandardTableCellFont());
				label.setHorizontalAlignment(SwingConstants.CENTER);
				if (isSelected) {
					label.setForeground(selectedForeground);
					label.setBackground(selectedBackground);
				} else {
					label.setForeground(foreground);
					label.setBackground(background);
				}
				if (hasFocus) {
					label.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
					if (table.isCellEditable(row, column)) {
						label.setForeground(UIManager.getColor("Table.focusCellForeground"));
						label.setBackground(UIManager.getColor("Table.focusCellBackground"));
					}
				} else {
					label.setBorder(noFocusBorder);
				}
				return label;
			} else {// text area
				JTextArea ta = new JTextArea();
				ta.setLineWrap(true);
				ta.setWrapStyleWord(true);
				ta.setHighlighter(null);
				ta.setEditable(false);
				JScrollPane scroll = new JScrollPane(label);
				scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
				scroll.setBorder(null);
				ta.setText(s);
				ta.setToolTipText(s);
				ta.setFont(DefaultPreferences.getStandardTableCellFont());
				if (isSelected) {
					ta.setForeground(selectedForeground);
					ta.setBackground(selectedBackground);
				} else {
					ta.setForeground(foreground);
					ta.setBackground(background);
				}
				if (hasFocus) {
					ta.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
					if (table.isCellEditable(row, column)) {
						ta.setForeground(UIManager.getColor("Table.focusCellForeground"));
						ta.setBackground(UIManager.getColor("Table.focusCellBackground"));
					}
				} else {
					ta.setBorder(noFocusBorder);
				}
				return ta;
			}
		} else if (value instanceof Boolean) {
			Boolean checked = (Boolean) value;
			JCheckBox chkBox0 = new JCheckBox();
			JPanel chkBoxPanel0 = new JPanel();
			chkBoxPanel0.add(chkBox0);
			if (isSelected) {
				chkBoxPanel0.setForeground(selectedForeground);
				chkBoxPanel0.setBackground(selectedBackground);
			} else {
				chkBoxPanel0.setForeground(foreground);
				chkBoxPanel0.setBackground(background);
			}
			chkBox0.setSelected(checked.booleanValue());

			return chkBoxPanel0;
			/*
			 * } else if (value instanceof RackFlagType) { RackFlagType mRackFlagType = (RackFlagType) value; JComboBox combo = new
			 * JComboBox(mRackFlagType.getRackPlateFlagValues()); combo.setSelectedIndex(mRackFlagType.getSelected()); JPanel
			 * comboPanel = new JPanel(); comboPanel.add(combo); if (isSelected) { comboPanel.setForeground(selectedForeground);
			 * comboPanel.setBackground(selectedBackground); } else { comboPanel.setForeground(foreground);
			 * comboPanel.setBackground(background); } return comboPanel;
			 */
		} else {
			if (isSelected) {
				naLabel.setForeground(selectedForeground);
				naLabel.setBackground(selectedBackground);
			} else {
				naLabel.setForeground(foreground);
				naLabel.setBackground(background);
			}
			return naLabel;
		}
	} // end method
}
