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

import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * 
 * 
 *
 * 
 * Needs to analyze the entered amount to strip salt code and re-add after
 */
public class CompoundCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = -7207010252624315186L;
	
	//	private static CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	// This is the component that will handle the editing of the cell value
	EmptyBorder emptyBorder = new EmptyBorder(2, 3, 0, 0);
	LineBorder border = new LineBorder(Color.black, 1);
	int repaintBorderFlag = 1;

	public CompoundCellEditor() {
		super(new JTextField());
		editorComponent.setBorder(border);
	}

	// This method is called just before the cell value
	// is saved. If the value is not valid, false should be returned.
	public boolean stopCellEditing() {
		String newValue = (String) getCellEditorValue();
		boolean editFlag = false;
		try {
			if (newValue != null && !newValue.equals("")) {
//				int splitCount = 0;
//				String[] saltCode = newValue.split("-");
//				splitCount = saltCode.length;
				
//				if (splitCount == 2) { // We haven't salt
//					newValue = newValue + "-00";
//				} else if (splitCount > 3) {
//					newValue = saltCode[0] + "-" + saltCode[1] + "-" + saltCode[2];
//				}
				
//				newValue = UtilsDispatcher.getFormatter().formatCompoundNumber(newValue);
			
				editFlag = true;
			}
		} catch (Exception e) {
			CeNErrorHandler.showMsgOptionPane(editorComponent.getRootPane(), "Failed to format Compound ID.",
					"Failed to format value: " + newValue, JOptionPane.ERROR_MESSAGE);
		}
		((JTextField) editorComponent).setText(newValue);
		if (editFlag)
			editFlag = super.stopCellEditing();
		else
			cancelCellEditing();
		return editFlag;
	}
}