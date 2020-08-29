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

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.page.reagents.ProgressBarDialog;
import com.chemistry.enotebook.client.gui.page.regis_submis.RegCodeMaps;
import com.chemistry.enotebook.client.gui.page.regis_submis.uc.JDialogUniquenessCheck;
import com.chemistry.enotebook.client.gui.page.regis_submis.uc.UniquenessCheckTableModel;
import com.chemistry.enotebook.domain.BatchVnVInfoModel;
import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.utils.StructureLoadAndConversionUtil;
import com.chemistry.enotebook.utils.SwingWorker;
import com.chemistry.enotebook.vnv.classes.UcCompoundInfo;

import javax.swing.*;
import java.util.HashMap;
public class NewNoveltyCheckUI {

	
	private ProgressBarDialog progressBarDialog = null;
	private HashMap sicMap =  null;
	public NewNoveltyCheckUI(ProgressBarDialog progressBarDialog){
		this.progressBarDialog = progressBarDialog;//new ProgressBarDialog(MasterController.getGUIComponent());
		sicMap = RegCodeMaps.getInstance().getStereoisomerCodeMap();
	}
	
	
	
	public void SubmitForUniquenessCheck(final ProductBatchModel pBatch,final BatchVnVInfoModel batchVnvInfo,final BatchEditPanel bpanel) {
		final SwingWorker noveltyWorker = new SwingWorker() {
			UniquenessCheckTableModel model = null;
			public Object construct() {
				
				
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						progressBarDialog.setTitle("Performing Novelty Check, Please Wait ...");
					}
				});
				ParentCompoundModel cp = pBatch.getCompound();
				//pBatch.getRegInfo().getBatchVnVInfo().getMolData();
				model = new UniquenessCheckTableModel(batchVnvInfo.getMolData().getBytes(),
						batchVnvInfo.getAssignedStereoIsomerCode(), cp.getMolWgt(), cp.getMolFormula(), cp.getComments(), true);
				
				return model;
			}

			public void finished() {
				UniquenessCheckTableModel model = (UniquenessCheckTableModel) get();
				progressBarDialog.setVisible(false);
				progressBarDialog.dispose();
				UcCompoundInfo result = null;
				if (model != null && model.getUcResults() != null) {
					JDialogUniquenessCheck dlg = new JDialogUniquenessCheck(MasterController.getGUIComponent());
					result = dlg.displayDialog(model);
					//dlg.setVisible(true);
				}
				// update others
				if (result != null) {
					if (result.getRegNumber() != null && result.getRegNumber().length() > 0
							&& !result.getRegNumber().equalsIgnoreCase("drawn structure")) {
						//if (isValidUcIsomerCode(result.getIsomerCode())) {
							pBatch.getCompound().setStereoisomerCode(result.getIsomerCode());
							//jComboBoxSICBatch.setSelectedItem(result.getIsomerCode() + " - " + sicMap.get(result.getIsomerCode()));
							// selectedBatch.getRegInfo().setCompoundSource("KNOWN");
							// selectedBatch.getRegInfo().setCompoundSourceDetail("KNWNT");
							bpanel.jTextFieldConvBatchNumValue.setText(result.getRegNumber());
//							if (result.getComments() != null && result.getComments().length() > 0) {
//								jTextAreaStruComments.setText(result.getComments());
//								jTextAreaStruComments.setCaretPosition(0);
//							}
							// Copy the structure over to the batch record
							try {
								int fmt = pBatch.getCompound().getFormat();
								byte[] structUC = result.getMolStruct().getBytes();
								byte[] structBatch = null;
								ChemistryDelegate chemDel = new ChemistryDelegate();
								if ((fmt == Compound.ISISDRAW || fmt == Compound.CHEMDRAW) && structUC != null
										&& structUC.length > 0) {
									structBatch = chemDel.convertChemistry(structUC, "", pBatch.getCompound()
											.getNativeSketchFormat());
								} else
									structBatch = structUC;
								if (structBatch != null && structBatch.length > 0) {
									if ((!chemDel.areMoleculesEqual(pBatch.getCompound().getNativeSketch(), structBatch)))
										JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
												"Batch Structure will be modified based on your Novelty Check selection.");
									
									StructureLoadAndConversionUtil.loadSketch(structBatch, fmt, true, "MDL Molfile",pBatch.getCompound());
									
//									structQueryCanvas.setChemistry(pBatch.getCompound().getNativeSketch());
//									performVnV(false);
								}
							} catch (ChemUtilInitException e) {
								CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(),
										"Could not load Sketch due to a failure to initialize the Chemistry Service", e);
							} catch (ChemUtilAccessException e) {
								CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(),
										"Could not access Chemistry Service.  Failed to load sketch changes.", e);
							}
						} else {
							bpanel.jTextFieldConvBatchNumValue.setText("");
						}
					} else {
						bpanel.jTextFieldConvBatchNumValue.setText("");
					//jTextFieldPFNumActionPerformed(null);
					}	
			}
		};
		noveltyWorker.start();
		progressBarDialog.setVisible(true);
	}

}
