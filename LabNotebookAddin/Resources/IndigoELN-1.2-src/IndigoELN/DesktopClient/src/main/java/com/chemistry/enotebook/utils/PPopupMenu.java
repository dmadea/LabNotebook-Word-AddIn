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
package com.chemistry.enotebook.utils;

import javax.swing.*;
import java.awt.*;

/**
 * 
 * 
 * Slightly smarter version of JPopupMenu that makes sure the entire menu remains visble on the screen, regardless of where it was
 * popped up.
 */
public class PPopupMenu extends JPopupMenu {
	
	private static final long serialVersionUID = 5677564242258237416L;
	
	protected static Insets screenInsets = null;
	protected static Dimension screenDim = null;

	/**
	 * @param c
	 * @return the pixel dimensions of the screen
	 */
	public static Dimension getScreenDimension(Component c) {
		if (screenDim == null) {
			initScreenAttrs(c);
		}
		return screenDim;
	}

	/**
	 * @param c
	 * @return the insets for the display screen, if any
	 */
	public static Insets getScreenInsets(Component c) {
		if (screenInsets == null) {
			initScreenAttrs(c);
		}
		return screenInsets;
	}

	/**
	 * @param c
	 *            a component to access the ToolKit
	 */
	public static void initScreenAttrs(Component c) {
		screenDim = c.getToolkit().getScreenSize();
		screenInsets = new Insets(0, 0, 25, 0);
		// TODO: JDK 1.4.x supports the following. Will accommodate the
		// Windows task bar.
		// screenInsets =
		// c.getToolkit().
		// getScreenInsets(c.getGraphicsConfiguration());
	}

	/**
	 * 
	 */
	public PPopupMenu() {
		super();
	}

	/**
	 * @param label
	 */
	public PPopupMenu(String label) {
		super(label);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JPopupMenu#show(java.awt.Component, int, int)
	 */
	public void show(Component invoker, int x, int y) {
		Dimension menuSize = getPreferredSize();
		Dimension screenSize = getScreenDimension(this);
		Insets screenInsets = getScreenInsets(this);
		Point compLoc = invoker.getLocationOnScreen();
		Point screenLoc = new Point(compLoc.x + x, compLoc.y + y);
		int adjX = x, adjY = y;
		// Compute the menu width against the menu location
		if (screenLoc.x + menuSize.getWidth() > screenDim.width - screenInsets.right) {
			adjX = (screenSize.width - screenInsets.right) - menuSize.width - compLoc.x;
		} else if (x < screenInsets.left) {
			adjX = screenInsets.left + compLoc.x;
		}
		// Compute the menu height against the menu location
		if (screenLoc.y + menuSize.height > screenDim.height - screenInsets.bottom) {
			adjY = (screenSize.height - screenInsets.bottom) - menuSize.height - compLoc.y;
		} else if (y < screenInsets.top) {
			adjY = screenInsets.top + compLoc.y;
		}
		// Display the menu with the adjusted coordinates
		super.show(invoker, adjX, adjY);
	}
}
