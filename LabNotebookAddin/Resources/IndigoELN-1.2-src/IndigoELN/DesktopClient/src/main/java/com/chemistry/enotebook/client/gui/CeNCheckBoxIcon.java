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
package com.chemistry.enotebook.client.gui;

import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class CeNCheckBoxIcon implements Icon {

	private static final Icon selectedIcon = CenIconFactory.getImageIcon(CenIconFactory.General.SELECTED_CHECKBOX);
	private static final Icon deselectedIcon = CenIconFactory.getImageIcon(CenIconFactory.General.DESELECTED_CHECKBOX);

	private ArrayList<ActionListener> listenerList = new ArrayList<ActionListener>();
	private transient Rectangle position = null;
	private JTabbedPane pane = null;
	private Icon currentIcon = null;
	private String command = null;

	public CeNCheckBoxIcon(boolean selected, String command) {
		setSelected(selected);
		setCommand(command);
	}

	public CeNCheckBoxIcon(String command) {
		this(false, command);
	}

	public CeNCheckBoxIcon() {
		this(false, "");
	}

	public int getIconHeight() {
		return currentIcon.getIconHeight();
	}

	public int getIconWidth() {
		return currentIcon.getIconWidth();
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (pane == null) {
			pane = (JTabbedPane) c;
			pane.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (!e.isConsumed()	&& position.contains(e.getX(), e.getY())) {
						setSelected(!isSelected());
						pane.repaint();
						fireStateChanged();
					}
				}
			});
		}
		position = new Rectangle(x, y, getIconWidth(), getIconHeight());
		currentIcon.paintIcon(c, g, x, y);
	}

	public boolean isSelected() {
		return (currentIcon == selectedIcon);
	}

	public void setSelected(boolean selected) {
		currentIcon = (selected ? selectedIcon : deselectedIcon);
	}

	public void addActionListener(ActionListener l) {
		listenerList.add(l);
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	private void fireStateChanged() {
		for (int i = 0; i < listenerList.size(); ++i) {
			ActionListener listener = listenerList.get(i);
			ActionEvent event = new ActionEvent(this, i, command);
			listener.actionPerformed(event);
		}
	}
	
	public Rectangle getPosition() {
		return position;
	}
}
