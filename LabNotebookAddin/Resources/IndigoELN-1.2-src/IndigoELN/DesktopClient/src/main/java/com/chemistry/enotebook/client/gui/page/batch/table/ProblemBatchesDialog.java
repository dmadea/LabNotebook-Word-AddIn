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
package com.chemistry.enotebook.client.gui.page.batch.table;

import com.chemistry.enotebook.domain.BatchModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows dialog with the following message:
 * 
 * Sorry, unable to set the In Well/Tube Weight for these batches,
 * because the Total Weight is less than the In Well/Tube Weight:
 *                     Batch#
 *                     Batch#
 * Do you want to set this value for the other batches?
 *              Yes                 No
 */
public class ProblemBatchesDialog {

	private static final String messageOne = "<html>Sorry, unable to set the In Well/Tube Weight for these batches,<br>because the Total Weight is less than the In Well/Tube Weight:</html>";
	private static final String messageTwo = "Do you want to set this value for the other batches?";
	
	public static boolean confirmUpdateColumn(Component parent, List<? extends BatchModel> problemBatches) {
		List<String> problemBatchesNumbers = new ArrayList<String>();
		if (problemBatches != null) {
			for (BatchModel model : problemBatches) {
				problemBatchesNumbers.add(model.getBatchNumberAsString());
			}
		}
		JLabel messageOneLabel = new JLabel(messageOne);
		JLabel messageTwoLabel = new JLabel(messageTwo);
		JList problemBathesList = new JList(problemBatchesNumbers.toArray());
		JScrollPane scrollPane = new JScrollPane(problemBathesList);
		JComponent[] inputs = new JComponent[] { messageOneLabel, scrollPane, messageTwoLabel };
		int result = JOptionPane.showConfirmDialog(parent, inputs, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		return (result == JOptionPane.YES_OPTION) ? true : false;
	}
}
