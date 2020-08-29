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

import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PageListView extends JScrollPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1130412159459725687L;
	private List<String> selectedPages = null;

	public PageListView() {
		super();
		selectedPages = new ArrayList<String>();
	}

	public PageListView(int vsbPolicy, int hsbPolicy) {
		super(vsbPolicy, hsbPolicy);
	}

	public PageListView(Component view) {
		super(view);
	}

	public PageListView(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);
	}

	public void populateList(List<String> pages) {
		int pagesLength = pages.size();
		JCheckBox pageEntry = null;
		JPanel panel = null;
		String entry = null;
		selectedPages = pages;
		if (pages != null || pages.size() != 0) {
			panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			for (int i = 0; i < pagesLength; i++) {
				entry = (String) pages.get(i);
				pageEntry = new JCheckBox();
				pageEntry.setText(entry);
				pageEntry.setSelected(true);
				pageEntry.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						handleActionForPages(evt);
					}
				});
				panel.add(pageEntry);
			}
			this.setViewportView(panel);
		}
	}

	public void handleActionForPages(ActionEvent evt) {
		JCheckBox pageBox = (JCheckBox) evt.getSource();
		
		if (pageBox.isSelected()) {
			String pageName = pageBox.getText();
			if (!selectedPages.contains(pageName)) {
				selectedPages.add(pageName);
			}
		} else {
			String pageName = pageBox.getText();
			if (selectedPages.contains(pageName)) {
				selectedPages.remove(pageName);
			}
		}
		
		updateSaveButton();
	}

	private void updateSaveButton() {
		// Find the "Save Selected" button in JOptionPane dialog and enable/disable it
		Container parent = getParent();
		
		while (parent != null && !(parent instanceof JOptionPane)) { 
			parent = parent.getParent();
		}
		
		if (parent == null || !(parent instanceof JOptionPane)) {
			return;
		}
		
		Component[] components = parent.getComponents();
		
		for (Component comp : components) {
			if (comp instanceof JPanel) {
				Component[] buttons = ((JPanel) comp).getComponents();
				for (Component button : buttons) {
					if (button instanceof JButton) {
						if (StringUtils.equals(((JButton) button).getText(), "Save Selected")) {
							if (selectedPages.size() > 0) {
								button.setEnabled(true);
							} else {
								button.setEnabled(false);
							}
							return;
						}
					}
				}
			}
		}
	}

	public List<String> getSelectedPages() {
		return selectedPages;
	}

//	public static void main(String[] args) {
//		PageListView pagesView = new PageListView(PageListView.VERTICAL_SCROLLBAR_AS_NEEDED, PageListView.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		ArrayList<String> list = new ArrayList<String>();
//		list.add("One");
//		list.add("Two");
//		list.add("Three");
//		list.add("Four");
//		pagesView.populateList(list);
//		Object[] options = { "Save Selected", "DiscardAll", "Cancel" };
//		JOptionPane.showOptionDialog(null, pagesView, "Select experiments to save", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
//	}

}
