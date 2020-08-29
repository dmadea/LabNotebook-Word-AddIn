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
package com.chemistry.enotebook.client.gui.page.stoichiometry;

import javax.swing.table.DefaultTableCellRenderer;
import java.text.NumberFormat;

// A holder for data and an associated icon
public class FractionCellRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = -20401798825877814L;
	
	public FractionCellRenderer(int integer, int fraction, int align) {
		this.integer = integer; // maximum integer digits
		this.fraction = fraction; // exact number of fraction digits
		this.align = align; // alignment (LEFT, CENTER, RIGHT)
	}

	protected void setValue(Object value) {
		if (value != null && value instanceof Number) {
			formatter.setMaximumIntegerDigits(integer);
			formatter.setMaximumFractionDigits(fraction);
			formatter.setMinimumFractionDigits(fraction);
			setText(formatter.format(((Number) value).doubleValue()));
		} else {
			super.setValue(value);
		}
		setHorizontalAlignment(align);
	}

	protected int integer;
	protected int fraction;
	protected int align;
	protected static NumberFormat formatter = NumberFormat.getInstance();
}