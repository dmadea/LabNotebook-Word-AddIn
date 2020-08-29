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
import com.l2fprod.common.swing.plaf.basic.BasicTaskPaneGroupUI;
import com.l2fprod.common.swing.plaf.windows.WindowsClassicTaskPaneGroupUI;

import javax.swing.border.Border;
import java.awt.*;

public class WindowsCustomTaskPaneGroupUI extends WindowsClassicTaskPaneGroupUI {
//	public static ComponentUI createUI(JComponent c) {
//		return new WindowsCustomTaskPaneGroupUI();
//	}
//
//	protected void installDefaults() {
//		super.installDefaults();
//		this.group.setOpaque(false);
//	}

	public Border createPaneBorder() {
		return new CustomPaneBorder();
	}

	class CustomPaneBorder extends BasicTaskPaneGroupUI.PaneBorder {
		private Color cenColor = new Color(122, 194, 174);
		
		CustomPaneBorder() {
			titleBackgroundGradientStart = cenColor;
			titleBackgroundGradientEnd = cenColor;
			titleOver = cenColor;
		}

		protected void paintExpandedControls(JTaskPaneGroup group, Graphics g,
				int x, int y, int width, int height) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			paintRectAroundControls(group, g, x, y, width, height, Color.white,
					Color.gray);

			g.setColor(getPaintColor(group));
			paintChevronControls(group, g, x, y, width, height);

			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}
}
