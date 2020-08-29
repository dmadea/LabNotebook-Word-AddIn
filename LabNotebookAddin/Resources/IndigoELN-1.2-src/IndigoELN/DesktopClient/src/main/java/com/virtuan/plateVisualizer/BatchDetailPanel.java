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
package com.virtuan.plateVisualizer;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.MonomerBatchModel;
import com.chemistry.enotebook.domain.PlateWell;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.utils.Decoder;

import javax.swing.*;
import java.awt.*;

public class BatchDetailPanel extends JPanel {
	
	private static final long serialVersionUID = 4363814736297353030L;
	
	public static final Font LABEL_FONT = new java.awt.Font("sansserif", 1, 11);
	
	public BatchDetailPanel(PlateWell<BatchModel> well, String plateBarcode) {
		BatchModel batchModel = well.getBatch();
		this.add(this.getPanel(batchModel, plateBarcode, well.getWellNumber()), BorderLayout.CENTER);
	}

	/**
	 * Get the batch molecular diagram.
	 * @return
	 */
	private JPanel getPanel(BatchModel batchModel, String barcode, String wellnum) {
		JPanel panel = new JPanel(new BorderLayout());
		ChemistryPanel chime = new ChemistryPanel(); //ChimeRenderer.getInstance();
		String stringSketch = "";
		String id = "";
		if (batchModel == null) { // empty well
			JLabel barcodeLabel = new JLabel(barcode, JLabel.CENTER);
			//barcodeLabel.setFont(LABEL_FONT);
			JLabel wellnumLabel = new JLabel("Well " + wellnum, JLabel.CENTER);
			//wellnumLabel.setFont(LABEL_FONT);
			panel.add(barcodeLabel, BorderLayout.NORTH);
			panel.add(wellnumLabel);
			return panel;
		} else if (batchModel instanceof ProductBatchModel) { // This is a product batch
			stringSketch = batchModel.getCompound().getStringSketchAsString();
			stringSketch = Decoder.decodeString(stringSketch);
			id = ((ProductBatchModel) batchModel).getBatchNumber().getBatchNumber();
		} else if (batchModel instanceof MonomerBatchModel){ // This is a monomer batch
			stringSketch = batchModel.getCompound().getMolfile();
			id = ((MonomerBatchModel) batchModel).getMonomerId();
		} else { // should not happen
			return panel;
		}
			
		JLabel idLabel = new JLabel(id, JLabel.CENTER);
		idLabel.setFont(LABEL_FONT);
		panel.add(idLabel, BorderLayout.NORTH);
		chime.setMolfileData(stringSketch);
		panel.add(chime);
		return panel;
	}
	
}
