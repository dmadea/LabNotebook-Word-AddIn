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
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.batch.events.MonomerBatchPlateCreatedEvent;
import com.chemistry.enotebook.client.gui.page.batch.events.PlatesCreatedEventListener;
import com.chemistry.enotebook.client.gui.page.batch.events.ProductBatchPlateCreatedEvent;
import com.chemistry.enotebook.domain.MonomerPlate;
import com.chemistry.enotebook.domain.ProductPlate;
import com.chemistry.enotebook.domain.ReactionStepModel;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Vector;

public class PlateCreationHandler {
	
	private static final Log log = LogFactory.getLog(PlateCreationHandler.class);
	
	transient Vector<PlatesCreatedEventListener> productBatchPlateListeners = new Vector<PlatesCreatedEventListener>();
	//private NotebookPageModel notebookPageModel = null;
	//private ReactionStepModel rxnStep;
	private StorageDelegate storageDelegate = null;

	public PlateCreationHandler() {
		try {
			storageDelegate = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
		} catch (Exception e) {
			log.error("Failed to get a storage delegate instance.", e);
		}
	}
	
	public synchronized void addPlateCreatedEventListener(PlatesCreatedEventListener l) {
		productBatchPlateListeners.addElement(l);
	}

	public synchronized void removePlateCreatedEventListener(PlatesCreatedEventListener l) {
		productBatchPlateListeners.removeElement(l);
	}

	public void fireNewMonomerPlateCreated(MonomerBatchPlateCreatedEvent event) {
		
			// update data model
			List<MonomerPlate> newPlates = event.getPlates();
			ReactionStepModel rxnStep = event.ReactionStepModel();
		
			rxnStep.addMonomerPlates(newPlates);
		
		if (productBatchPlateListeners != null) {
			Vector listeners = productBatchPlateListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++) {
				((PlatesCreatedEventListener) listeners.elementAt(i)).newMonomerPlatesCreated(event);
			}
		}
	}

	public void fireMonomerPlateDeleted(MonomerBatchPlateCreatedEvent event) {
		
			// update data model
		try{
			List deletedPlates = event.getPlates();
			ReactionStepModel rxnStep = event.ReactionStepModel();
			String[] platekeys = new String[deletedPlates.size()];
			for(int i = 0;i<deletedPlates.size();i++){
				platekeys[i] = ((MonomerPlate)deletedPlates.get(i)).getKey();
			}
			storageDelegate.deletePlates(platekeys,MasterController.getUser().getSessionIdentifier());
			
		}
		catch(Exception ex){
			return;
		}
			// TODO
			 //rxnStep.//addMonomerPlates(newPlates);
		
		if (productBatchPlateListeners != null) {
			Vector listeners = productBatchPlateListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++) {
				((PlatesCreatedEventListener) listeners.elementAt(i)).monomerPlatesRemoved(event);
			}
		}
	}

	public void fireNewProductPlateCreated(ProductBatchPlateCreatedEvent event) {
		//if (notebookPageModel != null) {
			// update data model
			//List newPlates = event.getPlates();
			//int stepIndex = event.getStepIndex();
			//ReactionStepModel rxnStep = (ReactionStepModel) notebookPageModel.getReactionSteps().get(stepIndex);
			//rxnStep.addProductPlates(newPlates);
		//}
		if (productBatchPlateListeners != null) {
			Vector listeners = productBatchPlateListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++) {
				((PlatesCreatedEventListener) listeners.elementAt(i)).newProductPlatesCreated(event);
			}
		}
	}

	public void fireProdcutPlateDeleted(ProductBatchPlateCreatedEvent event) {
		
			// update data model
			
			// rxnStep.addProductPlates(newPlates);
			try{
				List deletedPlates = event.getPlates();
				//ReactionStepModel rxnStep = event.ReactionStepModel();
				String[] platekeys = new String[deletedPlates.size()];
				for(int i = 0;i<deletedPlates.size();i++){
					platekeys[i] = ((ProductPlate)deletedPlates.get(i)).getKey();
				}
				//storageDelegate.deletePlates(platekeys);
				
			}
			catch(Exception ex){
				return;
			}
	
		if (productBatchPlateListeners != null) {
			Vector listeners = productBatchPlateListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++) {
				((PlatesCreatedEventListener) listeners.elementAt(i)).prodcutPlatesRemoved(event);
			}
		}
	}
	
	public void dispose() {
		if (this.productBatchPlateListeners != null) {
			this.productBatchPlateListeners.clear();
			this.productBatchPlateListeners = null;
		}
	}

}
