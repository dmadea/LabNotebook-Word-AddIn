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
package com.chemistry.enotebook.client.gui.common.utilsui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class VerticalLabelUI extends BasicLabelUI {
	protected boolean clockwise;
	private static Rectangle a;
	private static Rectangle b;
	private static Rectangle c;
	private static Insets d;

	public VerticalLabelUI(boolean paramBoolean) {
		this.clockwise = paramBoolean;
	}

	public Dimension getPreferredSize(JComponent paramJComponent) {
		Dimension localDimension = super.getPreferredSize(paramJComponent);
		return new Dimension(localDimension.height, localDimension.width);
	}

	public void paint(Graphics paramGraphics, JComponent paramJComponent) {
		int k = 0;
		JLabel localJLabel = (JLabel) paramJComponent;
		String str1 = localJLabel.getText();
		if (k == 0)
			;
		Icon localIcon = (localJLabel.isEnabled()) ? localJLabel.getIcon()
				: localJLabel.getDisabledIcon();
		if ((localIcon == null) && (str1 == null))
			return;
		FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
		d = paramJComponent.getInsets(d);
		c.x = d.left;
		c.y = d.top;
		c.height = (paramJComponent.getWidth() - (d.left + d.right));
		c.width = (paramJComponent.getHeight() - (d.top + d.bottom));
		a.x = (a.y = a.width = a.height = 0);
		b.x = (b.y = b.width = b.height = 0);
		String str2 = layoutCL(localJLabel, localFontMetrics, str1, localIcon,
				c, a, b);
		Graphics2D localGraphics2D = (Graphics2D) paramGraphics;
		AffineTransform localAffineTransform = localGraphics2D.getTransform();
		if (k == 0) {
			if (this.clockwise) {
				localGraphics2D.rotate(1.570796326794897D);
				localGraphics2D.translate(0, -paramJComponent.getWidth());
				if (k == 0) {
					localIcon.paintIcon(paramJComponent, paramGraphics, a.x, a.y);
				}
			}
			localGraphics2D.rotate(-1.570796326794897D);
		}
		localGraphics2D.translate(-paramJComponent.getHeight(), 0);
		if ((k != 0) || (localIcon != null)) {
			localIcon.paintIcon(paramJComponent, paramGraphics, a.x, a.y);
		}
		if (str1 != null) {
			int i = b.x;
			int j = b.y + localFontMetrics.getAscent();
			if (k == 0) {
				if (!(localJLabel.isEnabled())) {
					paintDisabledText(localJLabel, paramGraphics, str2, i, j);
				}
				paintEnabledText(localJLabel, paramGraphics, str2, i, j);
			}
			if (k != 0) {
				paintDisabledText(localJLabel, paramGraphics, str2, i, j);
			}
		}
		localGraphics2D.setTransform(localAffineTransform);
	}

	static {
		labelUI = new VerticalLabelUI(false);
		a = new Rectangle();
		b = new Rectangle();
		c = new Rectangle();
		d = new Insets(0, 0, 0, 0);
	}
}
