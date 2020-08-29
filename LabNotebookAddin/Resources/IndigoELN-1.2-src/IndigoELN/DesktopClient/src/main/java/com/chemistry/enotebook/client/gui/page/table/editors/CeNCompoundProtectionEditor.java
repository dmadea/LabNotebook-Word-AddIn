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

import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.ProductBatchModel;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class CeNCompoundProtectionEditor extends AbstractCellEditor
		implements TableCellEditor {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7113702350984762819L;
	JComboBox comboBox;
    BatchAttributeComponentUtility batchUtility = null;
    public CeNCompoundProtectionEditor()
    {
    	batchUtility = BatchAttributeComponentUtility.getInstance();
        comboBox = new JComboBox();
        batchUtility.fillProtectionCodesComboBox(comboBox);
    }
    
	public Component getTableCellEditorComponent(JTable arg0, Object arg1,
			boolean arg2, int arg3, int arg4) {
		PCeNTableView table = (PCeNTableView) arg0;
		ProductBatchModel productBatchModel = (ProductBatchModel) table.getBatchAt(arg3);
		if (productBatchModel != null) {
				String code = productBatchModel.getProtectionCode();
			if (code == null || code.equals(""))
				comboBox.setSelectedIndex(0);//Select the None option
			else
				comboBox.setSelectedItem(batchUtility.getCompoundProtectionCodeAndDesc(code));
		}
		return comboBox;
	}

	public Object getCellEditorValue() {
        return comboBox.getSelectedItem(); // should not appear
    }

}
