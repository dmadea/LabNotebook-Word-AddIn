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
package com.chemistry.enotebook.client.gui.tablepreferences;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ScrollableMenu extends JMenu implements Scrollable, MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = -1238960634084484859L;
	
	public static final int VERTICAL = 0;
	public static final int HORIZONTAL_WRAP = 2;
	public static final int VERTICAL_WRAP = 1;
	private int fixedCellWidth = -1;
	private int fixedCellHeight = -1;
	private int horizontalScrollIncrement = -1;
	private Object prototypeCellValue;
	private int layoutOrientation;

	public ScrollableMenu() {
	}

	public ScrollableMenu(String name) {
		super(name);
	}

	// Mouse Listener Implementation
	public void mouseClicked(MouseEvent e) {
	};

	public void mousePressed(MouseEvent e) {
	};

	public void mouseReleased(MouseEvent e) {
	};

	public void mouseEntered(MouseEvent e) {
		AbstractButton b = (AbstractButton) e.getSource();
		ButtonModel model = b.getModel();
		if (b.isRolloverEnabled()) {
			model.setRollover(true);
		}
		if (model.isPressed())
			model.setArmed(true);
	};

	public void mouseExited(MouseEvent e) {
		AbstractButton b = (AbstractButton) e.getSource();
		ButtonModel model = b.getModel();
		if (b.isRolloverEnabled()) {
			model.setRollover(false);
		}
		model.setArmed(false);
	};

	// MouseMotionListener Implementation
	public void mouseMoved(MouseEvent me) {
		if (me.getID() == MouseEvent.MOUSE_EXITED) {
			// ((JMenu)e.getSource()).setVisible(false);
			// ((JMenu)e.getSource()).setArmed(false);
			if (me.getSource() instanceof JMenu)
				((JMenu) me.getSource()).setEnabled(false);
		}
	}// end method

	public void mouseDragged(MouseEvent me) {
	}

	// Scrollable Interface Implementation
	public Dimension getMaxSize() {
		ComponentUI ui = getUI();
		return ui.getMaximumSize(this);
	}

	public Dimension getPreferredScrollableViewportSize() {
		if (getLayoutOrientation() != VERTICAL) {
			return getSize();
		}
		Insets insets = getInsets();
		int dx = insets.left + insets.right;
		int dy = insets.top + insets.bottom;
		int width;
		int height;
		Dimension size = getSize();
		if (size != null) {
			height = size.height + dy;
			width = size.width + dx;
		} else {
			// Will only happen if UI null, shouldn't matter what we return
			height = 1;
			width = 1;
		}
		return new Dimension(width, height);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		int blockIncrement = 15;
		return blockIncrement;
	}

	public boolean getScrollableTracksViewportHeight() {
		if (getLayoutOrientation() == VERTICAL_WRAP && this.getSize().getHeight() <= 0.0) {
			return true;
		}
		if (getParent() instanceof JViewport) {
			return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
		}
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		if (getLayoutOrientation() == HORIZONTAL_WRAP && this.getSize().getWidth() <= 0) {
			return true;
		}
		if (getParent() instanceof JViewport) {
			return (((JViewport) getParent()).getWidth() > getPreferredSize().width);
		}
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		checkScrollableParameters(visibleRect, orientation);
		if (orientation == SwingConstants.HORIZONTAL) {
			Font f = getFont();
			return (f != null) ? f.getSize() : 1;
		} else {
			Rectangle r = new Rectangle(this.getPreferredSize());
			return (r == null) ? 0 : r.height - (visibleRect.y - r.y);
		}
	}

	public int getFixedCellHeight() {
		return fixedCellHeight;
	}

	public void setFixedCellHeight(int fixedCellHeight) {
		this.fixedCellHeight = fixedCellHeight;
	}

	public int getFixedCellWidth() {
		return fixedCellWidth;
	}

	public void setFixedCellWidth(int fixedCellWidth) {
		this.fixedCellWidth = fixedCellWidth;
	}

	public int getHorizontalScrollIncrement() {
		return horizontalScrollIncrement;
	}

	public void setHorizontalScrollIncrement(int horizontalScrollIncrement) {
		this.horizontalScrollIncrement = horizontalScrollIncrement;
	}

	public int getLayoutOrientation() {
		return layoutOrientation;
	}

	public void setLayoutOrientation(int layoutOrientation) {
		this.layoutOrientation = layoutOrientation;
	}

	public Object getPrototypeCellValue() {
		return prototypeCellValue;
	}

	public void setPrototypeCellValue(Object prototypeCellValue) {
		this.prototypeCellValue = prototypeCellValue;
	}

	private void checkScrollableParameters(Rectangle visibleRect, int orientation) {
		if (visibleRect == null) {
			throw new IllegalArgumentException("visibleRect must be non-null");
		}
		switch (orientation) {
			case SwingConstants.VERTICAL:
			case SwingConstants.HORIZONTAL:
				break;
			default:
				throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
		}
	}
}
