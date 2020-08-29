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
package com.chemistry.enotebook.client.gui.page.experiment;

/**
 * 
 * 
 */

import javax.swing.*;
import java.awt.*;

public class RoundColorIcon implements Icon {
	//Color orgcolor;
	Color color;
	int radius =10;

	public RoundColorIcon(Color c, int radiuse) {
		this.color = c;
		//d = dir;
		radius = radiuse;
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
		

		g.fillOval(x ,y ,radius*2,radius*2);
		g.setColor(Color.black);
		g.drawOval(x , y , radius*2, radius*2);
		//
		//g.drawLine(x, y + getIconHeight(), x + getIconWidth(), y + getIconHeight() / 2);

		g.setColor(oldColor);
	}

	public int getIconWidth() {
		return radius*2;
	}

	public int getIconHeight() {
		return radius*2;
	}
	public void setRadius(int r){
		radius = r;
	}
}
