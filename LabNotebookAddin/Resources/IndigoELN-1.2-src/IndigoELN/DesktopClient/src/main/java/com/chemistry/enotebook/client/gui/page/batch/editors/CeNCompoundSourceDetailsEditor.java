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
package com.chemistry.enotebook.client.gui.page.batch.editors;

import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.domain.StoicModelInterface;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class CeNCompoundSourceDetailsEditor extends AbstractCellEditor
		implements TableCellEditor {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5257036235405362117L;
	JComboBox comboBox;
    BatchAttributeComponentUtility batchUtility = null;
    StoicModelInterface productBatchModel = null;

    public CeNCompoundSourceDetailsEditor()
    {
    	batchUtility = BatchAttributeComponentUtility.getInstance();
        this.comboBox = new JComboBox();
        //comboBox.addFocusListener(this);
    }

	public Object getCellEditorValue() {
        return comboBox.getSelectedItem(); // should not appear
    }

	public Component getTableCellEditorComponent(JTable arg0, Object arg1,
			boolean arg2, int arg3, int arg4) {
		PCeNTableView table = (PCeNTableView) arg0;
		productBatchModel = (StoicModelInterface) table.getBatchAt(arg3);
		if (productBatchModel != null) {
			batchUtility.updateComboSourceDetail(((ProductBatchModel)productBatchModel).getRegInfo().getCompoundSource(), comboBox, ((ProductBatchModel)productBatchModel).getRegInfo().getCompoundSourceDetail());
		}
		return comboBox;
	}

	
	
	
/*	public void focusGained(FocusEvent eventSource) {
		JComboBox comboBox = (JComboBox)eventSource.getSource();
		if (comboBox.getParent() instanceof PCeNBatchInfoTableView) {
			PCeNBatchInfoTableView table = (PCeNBatchInfoTableView) comboBox.getParent();
			//ProductBatchModel productBatchModel = (ProductBatchModel) table.getSelectedBatch();
			ProductBatchModel productBatchModel = (ProductBatchModel) table.getBatch(arg3);
			if (productBatchModel != null) {
				batchUtility.updateComboSourceDetail(productBatchModel.getRegInfo().getCompoundSource(), comboBox);
			}
		}
    //Make the renderer reappear.
        //fireEditingStopped();
	}
	public void focusLost(FocusEvent arg0) {
		
	}

	public void focusGained(FocusEvent eventSource) {
		if (productBatchModel != null) {
			batchUtility.updateComboSourceDetail(((ProductBatchModel)productBatchModel).getRegInfo().getCompoundSource(), comboBox);
		}
	}*/
}