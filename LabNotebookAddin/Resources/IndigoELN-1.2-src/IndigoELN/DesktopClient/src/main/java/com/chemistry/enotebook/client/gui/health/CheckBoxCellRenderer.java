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

package com.chemistry.enotebook.client.gui.health;

import com.chemistry.enotebook.session.DBHealthStatusVO;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {

	private static final long serialVersionUID = 4419412626594909689L;
	
	private static final Color RED = new Color(220, 0, 0);
	private static final Color GREEN = new Color(0, 220, 0);
	private static final Color YELLOW = new Color(220, 220, 0);
	private static final Color WHITE = Color.WHITE;
	
	public CheckBoxCellRenderer() {
		setBackground(Color.WHITE);
		setBorderPaintedFlat(true);
		setBorderPainted(false);
	}

	public Component getTableCellRendererComponent(JTable jTable, Object obj, boolean isSelected, boolean hasFocus, int row,
			int column) {
		if (obj instanceof DBHealthStatusVO) {
			if (((DBHealthStatusVO) obj).getHealthStatus().equals(ServiceHealthStatus.GOOD_STATUS)) {
				setBackground(GREEN);
				setSelected(true);
			} else if (((DBHealthStatusVO) obj).getHealthStatus().equals(ServiceHealthStatus.BAD_STATUS)) {
				setBackground(RED);
				setSelected(false);
			} else if (((DBHealthStatusVO) obj).getHealthStatus().equals(ServiceHealthStatus.MINIMAL_STATUS)) {
				setBackground(YELLOW);
				setSelected(false);
			} else {
				setBackground(WHITE);
				setSelected(false);
			}
		} else if (obj instanceof ServiceHealthStatus) {
			if (((ServiceHealthStatus) obj).getHealthStatus().equals(ServiceHealthStatus.GOOD_STATUS)) {
				setBackground(GREEN);
				setSelected(true);
			} else if (((ServiceHealthStatus) obj).getHealthStatus().equals(ServiceHealthStatus.BAD_STATUS)) {
				if (HealthCheckHandler.isMinimalService(((ServiceHealthStatus) obj).getServiceName()))
					setBackground(YELLOW);
				else
					setBackground(RED);
				setSelected(false);
			} else {
				setBackground(WHITE);
				setSelected(false);
			}
		}
		return this;
	}
}
