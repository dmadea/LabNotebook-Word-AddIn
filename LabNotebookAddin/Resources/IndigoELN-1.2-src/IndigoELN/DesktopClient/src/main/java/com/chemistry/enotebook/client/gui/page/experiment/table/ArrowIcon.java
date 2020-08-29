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
package com.chemistry.enotebook.client.gui.page.experiment.table;

/**
 * 
 * 
 */

import javax.swing.*;
import java.awt.*;

public class ArrowIcon implements Icon {
	Color orgcolor;
	Color color;
	int d;
	// vb 11/6 set defaults and make directions constants
	private int iconWidth = 12;
	private int iconHeight = 12;
	public static int RIGHT_ARROW = 0;
	public static int LEFT_ARROW = 1;
	public static int UP_ARROW = 2;
	public static int DOWN_ARROW = 3;

	public ArrowIcon(Color c, int dir) {
		this.orgcolor = c;
		color = c;
		d = dir;
	}

	public void setColor(Color col) {
		this.color = col;
	}

	public void setDiableColor() {
		this.color = Color.gray;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Color oldColor = g.getColor();
		g.setColor(color);
		Polygon polygon = new Polygon();
		if (d == RIGHT_ARROW) {// right
			polygon.addPoint(x, y);
			polygon.addPoint(x, y + getIconHeight());
			polygon.addPoint(x + getIconWidth(), y + getIconHeight() / 2);
			g.drawPolygon(polygon);
			g.fillPolygon(polygon);
			g.setColor(Color.black);
			g.drawLine(x, y + getIconHeight(), x + getIconWidth(), y + getIconHeight() / 2);
		} else if (d == LEFT_ARROW) {// left
			polygon.addPoint(x, y + getIconHeight() / 2);
			polygon.addPoint(x + getIconWidth(), y);
			polygon.addPoint(x + getIconWidth(), y + getIconHeight());
			g.drawPolygon(polygon);
			g.fillPolygon(polygon);
			g.setColor(Color.black);
			g.drawLine(x, y + getIconHeight() / 2, x + getIconWidth(), y + getIconHeight());
		} else if (d == UP_ARROW) {// up
			polygon.addPoint(x + getIconWidth() / 2, y);
			polygon.addPoint(x, y + getIconHeight());
			polygon.addPoint(x + getIconWidth(), y + getIconHeight());
			g.drawPolygon(polygon);
			g.fillPolygon(polygon);
			g.setColor(Color.black);
			// g.drawLine(x,y+getIconHeight()/2,x+getIconWidth(),y+getIconHeight());
		} else if (d == DOWN_ARROW) {// down
			polygon.addPoint(x, y);
			polygon.addPoint(x + getIconWidth(), y);
			polygon.addPoint(x + getIconWidth() / 2, y + getIconHeight());
			g.drawPolygon(polygon);
			g.fillPolygon(polygon);
			g.setColor(Color.black);
			// g.drawLine(x,y+getIconHeight()/2,x+getIconWidth(),y+getIconHeight());
		}
		g.setColor(oldColor);
	}

	public int getIconWidth() {
		return iconWidth;
	}

	public int getIconHeight() {
		return iconHeight;
	}

	// vb 11/6 make these setable
	public void setIconWidth(int iconWidth) {
		this.iconWidth = iconWidth;
	}

	public void setIconHeight(int iconHeight) {
		this.iconHeight = iconHeight;
	}
	
	
}
