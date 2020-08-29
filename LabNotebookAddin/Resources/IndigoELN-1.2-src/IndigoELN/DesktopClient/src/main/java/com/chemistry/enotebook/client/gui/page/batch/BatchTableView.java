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
package com.chemistry.enotebook.client.gui.page.batch;

import javax.swing.*;
import javax.swing.table.TableModel;

public class BatchTableView extends JTable {

	private static final long serialVersionUID = -234069323170128137L;

	public BatchTableView() {
		super();
	}

	public BatchTableView(TableModel model) {
		super(model);
	}

	public BatchTableView(int rowCount, int columnCount) {
		super(rowCount, columnCount);
	}
}
