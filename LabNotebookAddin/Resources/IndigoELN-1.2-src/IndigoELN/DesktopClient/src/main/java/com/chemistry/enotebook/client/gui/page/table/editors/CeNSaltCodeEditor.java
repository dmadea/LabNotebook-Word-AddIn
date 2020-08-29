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
package com.chemistry.enotebook.client.gui.page.table.editors;

import com.chemistry.enotebook.client.gui.common.utils.CeNTableCellAlignedComponent;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.StoicModelInterface;
import com.chemistry.enotebook.utils.CodeTableUtils;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class CeNSaltCodeEditor extends AbstractCellEditor implements TableCellEditor {
	
	private static final long serialVersionUID = 1301832344575844666L;
	
	CeNTableCellAlignedComponent comboBoxPanel;
	JComboBox comboBox = new JComboBox();
    BatchAttributeComponentUtility batchUtility = null;
    public CeNSaltCodeEditor()
    {
    	batchUtility = BatchAttributeComponentUtility.getInstance();
    	comboBoxPanel = new CeNTableCellAlignedComponent(comboBox);
        CodeTableUtils.fillComboBoxWithSaltCodes(comboBox);
    }
    
	public Component getTableCellEditorComponent(JTable arg0, Object arg1,
			boolean arg2, int arg3, int arg4) {
		PCeNTableView table = (PCeNTableView) arg0;
		StoicModelInterface productBatchModel = (StoicModelInterface) table.getBatchAt(arg3);
		if (productBatchModel != null) {
			{
				String code = productBatchModel.getStoicBatchSaltForm().getCode();
				String desc = (code.equals("00"))? "Parent Structure" : productBatchModel.getStoicBatchSaltForm().getDescription();
				comboBox.setSelectedItem(code + " - " + desc);
			}
		}
		return comboBoxPanel.getCellComponent();
	}

	public Object getCellEditorValue() {
        return comboBox.getSelectedItem(); // should not appear
    }
}
