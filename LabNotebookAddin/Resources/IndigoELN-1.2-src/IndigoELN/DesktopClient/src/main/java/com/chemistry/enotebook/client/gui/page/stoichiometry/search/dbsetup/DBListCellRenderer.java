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
/*
 * Created on Jan 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.stoichiometry.search.dbsetup;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class DBListCellRenderer extends JPanel implements TreeCellRenderer {
	
	private static final long serialVersionUID = -6165565365808942655L;
	
	private static final String DISPLAY_NAME = "displayName";
	protected JCheckBox check;
	protected JLabel label;

	public DBListCellRenderer() {
		setLayout(null);
		add(check = new JCheckBox());
		add(label = new JLabel());
		setPreferredSize(new Dimension(300, 20));
		check.setBackground(Color.WHITE);
		setBackground(Color.WHITE);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		String stringValue = ((CheckNode) value).getUserObject() instanceof Node ? getValueLabel((Node) ((CheckNode) value).getUserObject()) : (String)((CheckNode) value).getUserObject();
		setEnabled(tree.isEnabled());
		check.setSelected(((CheckNode) value).isSelected());
		label.setFont(new Font("Helvetica", Font.BOLD, 12));
		label.setPreferredSize(new Dimension(300, 20));
		check.setPreferredSize(new Dimension(20, 20));
		label.setText(stringValue);
		if (leaf) {
			label.setIcon(UIManager.getIcon("Tree.leafIcon"));
		} else if (expanded) {
			label.setIcon(UIManager.getIcon("Tree.openIcon"));
		} else {
			label.setIcon(UIManager.getIcon("Tree.closedIcon"));
		}
		if (check.isSelected()) {
			label.setForeground(Color.BLACK);
		} else {
			label.setForeground(Color.GRAY);
		}
		return this;
	}

	// returns a label for the treeNode, defaults to name of the element. If the
	// element has a 'displayName' attribute, then that becomes the label.
	// Values of any other attributes
	// found are appended inside parentheses.
	private String getValueLabel(Node value) {
		StringBuffer result = new StringBuffer(value.getNodeName());

		NamedNodeMap elementAttributes = value.getAttributes();
		if (elementAttributes != null) {
			Node attr = elementAttributes.getNamedItem(DISPLAY_NAME);

			if (attr != null) {
				result = new StringBuffer(attr.getNodeValue());
			}

			if (elementAttributes.getLength() > 1) {
				result.append(" (");
				int numAttributes = elementAttributes.getLength();
				for (int i = 0; i < numAttributes; i++) {
					Node attribute = elementAttributes.item(i);
					if (i > 1) {
						result.append(", ");
					}
					if (DISPLAY_NAME.equalsIgnoreCase(attribute.getNodeName())) {
						continue;
					}
					result.append(attribute.getNodeValue());
				}
				result.append(")");
			}
		}
		return result.toString();
	}

	public void doLayout() {
		Dimension d_check = check.getPreferredSize();
		Dimension d_label = label.getPreferredSize();
		int y_check = 0;
		int y_label = 0;
		if (d_check.height < d_label.height) {
			y_check = (d_label.height - d_check.height) / 2;
		} else {
			y_label = (d_check.height - d_label.height) / 2;
		}
		check.setLocation(0, y_check);
		check.setBounds(0, y_check, d_check.width, d_check.height);
		label.setLocation(d_check.width, y_label);
		label.setBounds(d_check.width, y_label, d_label.width, d_label.height);
	}
}
