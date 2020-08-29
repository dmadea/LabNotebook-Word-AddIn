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
/**
 * 
 */
package com.chemistry.enotebook.utils.jtable;

import javax.swing.*;
import java.awt.*;

public class SortArrowIcon implements Icon {
	public static final int NONE = 0;
	public static final int DECENDING = 1;
	public static final int ASCENDING = 2;
	protected int direction;
	protected int width = 8;
	protected int height = 8;

	public SortArrowIcon(int direction) {
		this.direction = direction;
	}

	public int getIconWidth() {
		return width;
	}

	public int getIconHeight() {
		return height;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Color bg = c.getBackground();
		Color light = bg.brighter();
		Color shade = bg.darker();
		int w = width;
		int h = height;
		int m = w / 2;
		if (direction == ASCENDING) {
			g.setColor(shade);
			g.drawLine(x + m, y, x, y + h);
			g.setColor(light);
			g.drawLine(x, y + h, x + w, y + h);
			g.drawLine(x + m, y, x + w, y + h);
		} else if (direction == DECENDING) {
			g.setColor(shade);
			g.drawLine(x, y, x + w, y);
			g.drawLine(x, y, x + m, y + h);
			g.setColor(light);
			g.drawLine(x + w, y, x + m, y + h);
		}
	}
}