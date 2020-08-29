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
package com.chemistry.enotebook.client.gui.page.conception;

import com.chemistry.enotebook.client.gui.page.table.PCeNTableModel;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableModelConnector;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableViewPopupMenuManager;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import org.apache.commons.lang.StringUtils;

import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ConceptionProductTableView extends PCeNTableView {
	
	private static final long serialVersionUID = -2753775532414739289L;
	
	private PCeNTableModel tableModel = null;
	
	public ConceptionProductTableView(PCeNTableModel model, int rowHeight, PCeNTableModelConnector connectore, PCeNTableViewPopupMenuManager tableViewPopupMenuManager) {
		super(model, rowHeight, connectore, tableViewPopupMenuManager);
		this.tableModel = model;
	}

	public ConceptionProductTableView(PCeNTableModel model, int rowHeight, PCeNTableModelConnector connectore, PCeNTableViewPopupMenuManager tableViewPopupMenuManager, NotebookPageModel pageModel) {
		super(model, rowHeight, connectore, tableViewPopupMenuManager, pageModel);
		this.tableModel = model;
	}

	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		
		ProductBatchModel rowBatch = (ProductBatchModel) tableModel.getStoicElementAt(row);
		String columnName = getColumnName(column);
			
		if (StringUtils.equals(columnName, PCeNTableView.NBK_BATCH_NUM)) {
			if (rowBatch != null && rowBatch.isChloracnegen()) {
				c.setBackground(Color.red);
			} else {
				c.setBackground(Color.white);
			}
		}
		
						
		return c;
	}
}
