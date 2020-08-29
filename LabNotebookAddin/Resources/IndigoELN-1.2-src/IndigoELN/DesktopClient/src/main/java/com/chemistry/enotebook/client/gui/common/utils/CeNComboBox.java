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

package com.chemistry.enotebook.client.gui.common.utils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * Workaround for popup width, based on old CeNComboBox class and
 * http://www.jroller.com/santhosh/entry/make_jcombobox_popup_wide_enough (He
 * got this workaround from the following bug:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4618607)
 */
public class CeNComboBox extends JComboBox {

	private static final long serialVersionUID = -6969960944397193619L;

	private static final int MAX_SIZE = 500;

	private boolean unknownItemsAllowed = false;

	public CeNComboBox() {
	}

	public CeNComboBox(final Object items[]) {
		super(items);
	}

	public CeNComboBox(List<?> items) {
		super(items.toArray());
	}

	public CeNComboBox(Vector<?> items) {
		super(items);
	}

	public CeNComboBox(ComboBoxModel aModel) {
		super(aModel);
	}

	private boolean layingOut = false;

	public void doLayout() {
		try {
			layingOut = true;
			super.doLayout();
		} finally {
			layingOut = false;
		}
	}

	public Dimension getSize() {
		Dimension dim = super.getSize();
		if (!layingOut)
			dim.width = getComboPopupWidth();
		return dim;
	}

	public void setSelectedItem(Object item) {
		if (unknownItemsAllowed) {
			super.setSelectedItem(item);
			if (getSelectedIndex() <= 0) {
				addItem(item);
				super.setSelectedItem(item);
			}
		} else {
			super.setSelectedItem(item);
		}
		
		if (getSelectedItem() != null)
			setToolTipText(getSelectedItem().toString());
	}

	private int getComboPopupWidth() {
		FontMetrics fm = getFontMetrics(getFont());
		Container popup = (Container) getUI().getAccessibleChild(this, 0);

		if (popup == null)
			return 0;

		int size = (int) getPreferredSize().getWidth();
		int minSize = getWidth();

		for (int i = 0; i < getItemCount(); i++) {
			if (getItemAt(i) instanceof String) {
				String str = (String) getItemAt(i);
				if (str != null && size < fm.stringWidth(str)) {
					size = fm.stringWidth(str);
				}
			}
		}

		Component comp = popup.getComponent(0);// JScrollPane
		JScrollPane scrollpane = null;
		int offset = 0;

		if (comp instanceof JScrollPane) {
			scrollpane = (JScrollPane) comp;
			if (scrollpane.getVerticalScrollBar().isVisible()) {
				offset += scrollpane.getVerticalScrollBar().getWidth();
				minSize = minSize - offset - 3;
			}
		}

		if (size > MAX_SIZE)
			size = MAX_SIZE;

		if (size < minSize)
			size = minSize;

		return (size + offset + 3);
	}

	public boolean isUnknownItemsAllowed() {
		return unknownItemsAllowed;
	}

	public void setUnknownItemsAllowed(boolean unknownItemsAllowed) {
		this.unknownItemsAllowed = unknownItemsAllowed;
	}
}
