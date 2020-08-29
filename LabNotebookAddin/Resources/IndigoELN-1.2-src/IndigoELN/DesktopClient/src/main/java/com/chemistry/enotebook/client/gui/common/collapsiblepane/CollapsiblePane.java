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
package com.chemistry.enotebook.client.gui.common.collapsiblepane;

import com.l2fprod.common.swing.JTaskPaneGroup;

import javax.swing.*;
import java.awt.*;

public class CollapsiblePane extends JTaskPaneGroup implements SwingConstants,
		WindowConstants {
	public static final int DROPDOWN_STYLE = 0;
	public static final int TREE_STYLE = 1;
	public static final int PLAIN_STYLE = 2;

	private static final long serialVersionUID = 12179432174312L;

	private JComponent contentPane;

	public CollapsiblePane() {
		this("");
	}

	public CollapsiblePane(String title) {
		super();
		this.setTitle(title);

		this.setBorder(new WindowsCustomTaskPaneGroupUI().createPaneBorder());
	}

	public Component add(Component paramComponent) {
		return this.getContentPane().add(paramComponent);
	}

	public Component add(String paramString, Component paramComponent) {
		return this.getContentPane().add(paramString, paramComponent);
	}

	public Component add(Component paramComponent, int paramInt) {
		return this.getContentPane().add(paramComponent, paramInt);
	}

	public void addCollapsiblePaneListener(
			CollapsiblePaneListener paramCollapsiblePaneListener) {
		this.addComponentListener(paramCollapsiblePaneListener);
	}

	public JComponent getContentPane() {
		if (contentPane != null) {
			return contentPane;
		} else {
			return (JComponent) super.getContentPane();
		}
	}

	public int getContentPaneHeight() {
		return this.getHeight();
	}

	public void setCollapsed(boolean paramBoolean) {
		this.setExpanded(!paramBoolean);
	}

	public void setContentPane(JComponent contentPane) {
		this.getContentPane().add(contentPane);
		if (this.contentPane != null) {
			this.remove(this.contentPane);
		}
		this.add(contentPane);
		this.contentPane = contentPane;
	}

	public void setContentPaneHeight(int height) {
		this.setSize(this.getWidth(), height);
	}

	public void setStepDelay(int paramInt) {
	}

	public void setSteps(int paramInt) {
	}

	public void setStyle(int paramInt) {
	}

	public void setIconTextGap(int paramInt) {		
	}
}
