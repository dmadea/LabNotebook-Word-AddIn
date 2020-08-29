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
package com.chemistry.enotebook.client.gui.page.experiment.plate.view;

import com.chemistry.enotebook.client.gui.page.experiment.plate.controller.AppEvent;
import com.chemistry.enotebook.client.gui.page.experiment.plate.controller.TabbedGUIPaneController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TabbedGUIPane extends javax.swing.JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6775219479541811774L;
	TabbedGUIPaneController controller;
	private SourceGUIPane sourcePane_;
	private SortGUIPane sortPane_;
	private ExcludeGUIPane excludePane_;
	private TargetGUIPane targetPane_;

	public TabbedGUIPane() {

	}

	public void init() {
		setLayout(new GridLayout(1, 1));

		sourcePane_ = new SourceGUIPane();
		sourcePane_.setController(controller);

		sortPane_ = new SortGUIPane();
		sortPane_.setController(controller);

		excludePane_ = new ExcludeGUIPane();
		excludePane_.setController(controller);
		excludePane_.init();

		targetPane_ = new TargetGUIPane();
		targetPane_.setController(controller);

		JTabbedPane tabbedPane = new JTabbedPane();

		JComponent panel1 = makeTextPanel("Source");
		tabbedPane.addTab("<html><body leftmargin=30 topmargin=8 marginwidth=30 marginheight=5>Source</body></html>", sourcePane_);

		JComponent panel2 = makeTextPanel("Sort");
		tabbedPane.addTab("<html><body leftmargin=30 topmargin=8 marginwidth=30 marginheight=5>Sort</body></html>", sortPane_);

		JComponent panel3 = makeTextPanel("Exclude");
		tabbedPane
				.addTab("<html><body leftmargin=30 topmargin=8 marginwidth=30 marginheight=5>Exclude</body></html>", excludePane_);

		JComponent panel4 = makeTextPanel("Target");
		tabbedPane.addTab("<html><body leftmargin=30 topmargin=8 marginwidth=30 marginheight=5>Target</body></html>", targetPane_);

		// Add the tabbed pane to this panel.
		add(tabbedPane);

		// The following line enables to use scrolling tabs.
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	protected JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
	}

	public void setController(TabbedGUIPaneController ctlr) {
		controller = ctlr;
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public void handleAppEvent(AppEvent evt) {

	}

	public SortGUIPane getSortPane_() {
		return sortPane_;
	}

	public void setSortPane_(SortGUIPane sortPane_) {
		this.sortPane_ = sortPane_;
	}

}
