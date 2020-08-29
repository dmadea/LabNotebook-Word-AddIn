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

import com.chemistry.enotebook.utils.CeNJTable;

import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class JSortTable extends CeNJTable implements MouseListener {
	
	private static final long serialVersionUID = -1415708258118646637L;

	public JSortTable() {
		super();
		initSortHeader();
	}

	protected void initSortHeader() {
		JTableHeader header = getTableHeader();
		header.setDefaultRenderer(new SortHeaderRenderer());
		header.addMouseListener(this);
	}

	public void mouseReleased(MouseEvent event) {
		TableColumnModel colModel = getColumnModel();
		int index = colModel.getColumnIndexAtX(event.getX());
		sortColumn(index);
	}

	public void sortColumn(int modelIndex) {
		SortableTableModel model = (SortableTableModel) getModel();
		if (model.isSortable(modelIndex)) {
			// toggle ascension, if already sorted
			boolean ascending = model.isSortedColumnAscending();
			if (model.getSortedColumnIndex() == modelIndex) {
				ascending = !ascending;
			}
			model.sortColumn(modelIndex, ascending);
			tableChanged(new TableModelEvent(model));
		}
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseClicked(MouseEvent event) {
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}
}