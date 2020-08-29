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
 * Created on Oct 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.client.gui;

import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePane;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePaneAdapter;
import com.chemistry.enotebook.client.gui.common.collapsiblepane.CollapsiblePaneEvent;
import com.chemistry.enotebook.client.gui.common.utils.CenIconFactory;
import com.l2fprod.common.swing.JCollapsiblePane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class CeNCollapsiblePane extends CollapsiblePane implements MouseListener {
	
	private static final long serialVersionUID = 3605210913098578569L;
	
	private JPanel pnlOptions = new JPanel();
	private boolean isMaximized = false;
	private int contentHeight = 0;
	private JLabel lblMaximize = null;

	public CeNCollapsiblePane(String title) {
		this(title, false, null, null);
	}

	public CeNCollapsiblePane(String title, boolean isMaximizable) {
		this(title, isMaximizable, null, null);
	}

	public CeNCollapsiblePane(String title, boolean isMaximizable, final JPopupMenu optionsPopupMenu) {
		this(title, isMaximizable, optionsPopupMenu, null);
	}

	public CeNCollapsiblePane(String title, boolean isMaximizable, final JPopupMenu optionsPopupMenu, final JLabel lblMessage) {
		super(title);
		if (isMaximizable) {
			isMaximized = false;
			lblMaximize = new JLabel();
			lblMaximize.setToolTipText("Maximize");
			lblMaximize.setIcon(CenIconFactory.getImageIcon(CenIconFactory.General.MAXIMIZE));
			pnlOptions.add(lblMaximize);
			lblMaximize.addMouseListener(this);
		}
		if (optionsPopupMenu != null) {
			JButton btnOptions = new JButton("Options");
			btnOptions.setPreferredSize(new Dimension(70, 24));
			// btnOptions.setBorder(new TitledBorder(null, "",
			// TitledBorder.LEADING, TitledBorder.TOP, new java.awt.Font("MS
			// Sans Serif",0,11), new java.awt.Color(0,0,0)));
			btnOptions.setBorder(BorderFactory.createEmptyBorder());
			btnOptions.setIcon(CenIconFactory.getImageIcon(CenIconFactory.General.DROP_DOWN));
			btnOptions.setHorizontalAlignment(SwingConstants.LEFT);
			btnOptions.setFocusable(false);
			pnlOptions.add(btnOptions);
			btnOptions.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent evt) {
					optionsPopupMenu.show(evt.getComponent(), 0, (evt.getComponent().getY() + evt.getComponent().getHeight() - 6));
					// //System.out.println(evt.getComponent().getWidth() +
					// " " + evt.getComponent().getHeight());
				}
			});
		}
		if (lblMessage != null) {
			pnlOptions.add(lblMessage);
		}
		if (isMaximizable || optionsPopupMenu != null || lblMessage != null) {
			FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
			layout.setHgap(10);
			pnlOptions.setLayout(layout);
			pnlOptions.setOpaque(false);
			pnlOptions.setSize((lblMessage != null) ? 400 : 200, 100);
			Component[] c = getComponents();
			JCollapsiblePane tp = (JCollapsiblePane) c[1];
			Component[] c1 = tp.getComponents();
			LayoutManager lm = tp.getLayout();
			lm.layoutContainer(this);
			tp.add(pnlOptions);
			JLabel lblTitle = (JLabel) c1[1];
			FontMetrics fm = getFontMetrics(lblTitle.getFont());
			int pnlLocation = lblTitle.getX() + fm.stringWidth(lblTitle.getText()) + 25;
			if (optionsPopupMenu != null)
				pnlOptions.setLocation(pnlLocation, -8);
			else
				pnlOptions.setLocation(pnlLocation, -3);
			final CeNCollapsiblePane pane = this;
			addCollapsiblePaneListener(new CollapsiblePaneAdapter() {
				public void paneCollapsing(CollapsiblePaneEvent evt) {
					if (pane.getContentPane() != null)
						pane.getContentPane().setVisible(false);
					pnlOptions.setVisible(false);
				}

				public void paneCollapsed(CollapsiblePaneEvent evt) {
				}

				public void paneExpanding(CollapsiblePaneEvent evt) {
				}

				public void paneExpanded(CollapsiblePaneEvent evt) {
					pnlOptions.setVisible(true);
					if (pane.getContentPane() != null)
						pane.getContentPane().setVisible(true);
				}
			});
		}
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
		if (isMaximized) {
			// code here expand content up
			setContentPaneHeight(contentHeight);
			isMaximized = false;
			lblMaximize.setToolTipText("Maximize");
			lblMaximize.setIcon(CenIconFactory.getImageIcon(CenIconFactory.General.MAXIMIZE));
		} else {
			// code here scroll up so title is at top then expand content
			// down
			contentHeight = getContentPaneHeight();
			JViewport jv = (JViewport) getParent().getParent();
			Dimension d = jv.getExtentSize();
			jv.setViewPosition(new Point(0, getY()));
			setContentPaneHeight(d.height - 25); // 25 = height of pane
			// header
			isMaximized = true;
			lblMaximize.setToolTipText("Restore");
			lblMaximize.setIcon(CenIconFactory.getImageIcon(CenIconFactory.General.RESTORE));
		}
	}
}
