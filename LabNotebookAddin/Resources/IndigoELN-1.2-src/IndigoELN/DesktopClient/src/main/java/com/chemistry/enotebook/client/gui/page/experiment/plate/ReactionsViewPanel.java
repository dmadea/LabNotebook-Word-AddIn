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
package com.chemistry.enotebook.client.gui.page.experiment.plate;

import com.chemistry.ChemistryPanel;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionEvent;
import com.chemistry.enotebook.client.gui.page.batch.BatchSelectionListener;
import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.utils.Decoder;
import com.chemistry.enotebook.utils.ExperimentPageUtils;
import com.chemistry.enotebook.utils.RxnUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ReactionsViewPanel extends ChemistryPanel implements BatchSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3054312057826244662L;
	private NotebookPageModel notebookPageModel = null;
	private ProductPlate productPlate = null;
	private ProductBatchModel productBatchModel = null;

	public ReactionsViewPanel(ProductPlate plate, NotebookPageModel pageModel) {
		try {
			this.productPlate = plate;
			this.notebookPageModel = pageModel;
			this.selectFirstWell();
			if (this.productBatchModel == null) {
				// handle error
				return;
			}
			this.buildPanel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ReactionsViewPanel(String chimestr) {
		//this.setBackground(Color.white);
		this.add(this.getChimePanel(chimestr), BorderLayout.CENTER);
	}

	private void buildPanel() {
		try {
			this.removeAll();
			List monomerBatchKeys = productBatchModel.getReactantBatchKeys();
			ExperimentPageUtils pageUtils = new ExperimentPageUtils();
			//String[] regnums = new String[monomerBatchKeys.size()];
			int index = 0;
			String structures[] = new String[monomerBatchKeys.size()];
			for (Iterator it = monomerBatchKeys.iterator(); it.hasNext();) {
				String monomerKey = (String) it.next();
				MonomerBatchModel monomerBatch = pageUtils.getMonomerBatchInTheExperiment(monomerKey, this.notebookPageModel);
				String monomerId = monomerBatch.getMonomerId();

				if (monomerId.indexOf(":") >= 0) { // vb 8/2  This should be made a utility method
					String conpressedStructureFromDSP = monomerBatch.getCompound().getStringSketchAsString();
					String decodedString = "";
					if (conpressedStructureFromDSP != null) {
						decodedString = Decoder.decodeString(conpressedStructureFromDSP);
						//System.out.println("decodedString with : "+i+" "+decodedString);
					}
					structures[index++] = decodedString;
				} else {

					try {
						structures[index++] = new CompoundMgmtServiceDelegate().getStructureByCompoundNo(monomerId).get(0);
					} catch (RuntimeException e) {
						CeNErrorHandler.getInstance().logExceptionMsg(null, "Error occurred while doing the CLS Lookup", e);
					}

				}

			}

			ArrayList monomerBatchStructures = new ArrayList();
			monomerBatchStructures.addAll(Arrays.asList(structures));
			ArrayList productBatchStructures = new ArrayList();
			String stringSketch = productBatchModel.getCompound().getStringSketchAsString();
			stringSketch = Decoder.decodeString(stringSketch);
			productBatchStructures.add(stringSketch);
			String chimestr1 = new RxnUtil().getMDLRxnString(monomerBatchStructures, productBatchStructures);
			setMolfileData(chimestr1);
			//this.setBackground(Color.white);
			//this.add(this.getChimePanel(chimestr1), BorderLayout.CENTER);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get the batch molecular diagram.
	 * @return
	 */
	private JPanel getChimePanel(String chimestr) {
		ChemistryPanel chime = new ChemistryPanel();
		chime.setMolfileData(chimestr);
		if (chimestr != null && chimestr.length() > 0)
			chime.setSize(500, 150);
		return chime;
	}

	/**
	 * A new well has been clicked so update the display.
	 */
	public void batchSelectionChanged(BatchSelectionEvent event) {
		Object obj = event.getSubObject();
		if (obj instanceof PlateWell) {
			PlateWell well = (PlateWell) obj;
			this.productBatchModel = (ProductBatchModel) well.getBatch();
			this.buildPanel();
			this.revalidate();
			this.repaint();

		} else { // user clicked on empty well
			this.removeAll();
			setMolfileData("");
		}
	}

	/**
	 * If this is a new plate view, set the detail well to the first well in the plate.
	 *
	 */
	private void selectFirstWell() {
		if (this.productPlate != null) {
			PlateWell[] wells = this.productPlate.getWells();
			if (wells != null && wells.length > 0) {
				this.productBatchModel = (ProductBatchModel) wells[0].getBatch();
			}
		}
	}
}
