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

import com.chemistry.enotebook.utils.DefaultPreferences;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class PCeNStoich_MonomersTableViewCellRenderer implements TableCellRenderer{
	private Color foreground = DefaultPreferences.getRowForegroundColor(); //new Color(30, 64, 124);
	private Color background = DefaultPreferences.getEditableRowBackgroundColor(); //new Color(255, 255, 245);
	private Color selectedForeground = DefaultPreferences.getSelectedRowForegroundColor(); //new Color(255, 255, 245);
	private Color selectedBackground = DefaultPreferences.getSelectedRowBackgroundColor(); //new Color(100, 100, 100);

	private Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	private JLabel naLabel = new JLabel("N/A");
	protected boolean m_selected;
	
	public PCeNStoich_MonomersTableViewCellRenderer() {
		naLabel.setFont(new java.awt.Font("Dialog", 1, 14));
		naLabel.setHorizontalAlignment(SwingConstants.CENTER);
		naLabel.setOpaque(false);
		naLabel.setOpaque(true);
		naLabel.setBorder(noFocusBorder);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		PCeNStoich_MonomersTableView ceNStoich_MonomersTableView = (PCeNStoich_MonomersTableView) table;
		PCeNStoich_MonomersTableViewControllerUtility connector = (PCeNStoich_MonomersTableViewControllerUtility) ceNStoich_MonomersTableView.getConnector();
		// before all, put default color
		String coluName = table.getColumnName(column);
		 if (value instanceof String) {
			String s = (String) value;
			// //System.out.println(s);
			if (s.length() < 21) {
				JLabel label = new JLabel();
				//label.setOpaque(false);
				label.setOpaque(true);
				label.setBorder(noFocusBorder);
				
				if (StringUtils.equals(coluName, "Compound ID") ||
						StringUtils.equals(coluName, "Nbk Batch #") ||
						StringUtils.equals(coluName, "CAS Number"))
				{
					((PCeNStoich_MonomersTableView)table).setToolTipBatchModel(connector.getBatchModel(row));
					s = s!=null ? s : "";
					//TODO workaround						
					label.setToolTipText(s);
				}
				else
				{
					((PCeNStoich_MonomersTableView)table).setToolTipBatchModel(null);
					label.setToolTipText(s);
				}
				
				label.setText(s);
				//label.setFont(new java.awt.Font("Dialog", 1, 14));
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
				JTextArea label = new JTextArea();
				label.setLineWrap(true);
				//label.setWrapStyleWord(true);
				label.setHighlighter(null);
				label.setEditable(false);
				JScrollPane scroll = new JScrollPane(label);
				scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
				scroll.setBorder(null);

				label.setText(s);
				label.setToolTipText(s);
				//label.setFont(new java.awt.Font("Dialog", 1, 14));
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
			}
		} else if (value instanceof Boolean) {

			Boolean checked = (Boolean) value;
			if (coluName.equalsIgnoreCase("List")) {// Stoich table instance

				boolean yesno = checked.booleanValue();
				String yes = "YES";
				if (!yesno) {
					yes = "NO";
				}
				JLabel label = new JLabel();
				label.setOpaque(false);
				label.setOpaque(true);
				label.setBorder(noFocusBorder);
				label.setText(yes);
				label.setToolTipText(yes);
//				label.setFont(new java.awt.Font("Dialog", 1, 14));
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
			} else {

				JCheckBox chkBox0 = new JCheckBox();
				JPanel chkBoxPanel0 = new JPanel();
				FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 0, 0);
				chkBoxPanel0.setLayout(layout);
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
			}
		} 
		else {
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
