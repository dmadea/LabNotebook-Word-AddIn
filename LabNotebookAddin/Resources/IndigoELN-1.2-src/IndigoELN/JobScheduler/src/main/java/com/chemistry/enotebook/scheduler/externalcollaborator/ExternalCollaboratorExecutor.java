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
package com.chemistry.enotebook.scheduler.externalcollaborator;

import com.chemistry.enotebook.domain.CROPageInfo;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.extcol.ExternalCollaboratorService;
import com.chemistry.enotebook.extcol.ExternalCollaboratorServiceFactory;
import com.chemistry.enotebook.extcol.RequestDTOInfo;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.ValidationInfo;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

public class ExternalCollaboratorExecutor {
	
	private static final Log log = LogFactory.getLog(ExternalCollaboratorExecutor.class);
    
    public ExternalCollaboratorExecutor() {
    }
        
	public void postMessageToExternalCollaboratorQueue(int[] requestIDs) throws Exception {
		log.info("postMessageToExternalCollaboratorQueue(int[]) start");
		
    	if (!ArrayUtils.isEmpty(requestIDs)) {
    		for (int i = 0 ; i < requestIDs.length; ++i) {
    			onMessage(requestIDs[i]);
    		}
    		log.info("Total RequestID messages to Q: " + requestIDs.length);
    	} else {
    		log.info("Request ID array is empty");	
    	}
    	
    	log.info("postMessageToExternalCollaboratorQueue(int[]) end");
    }
        
	public void onMessage(int externalRequestId) {
		log.debug("onMessage(Message) start");

		int requestID = -1;

		try {
			requestID = externalRequestId;
			log.debug("requestID = " + requestID);

			StorageDelegate storageLocal = getStorageServiceEJBLocal();
			ExternalCollaboratorService externalCollaboratorServcies = ExternalCollaboratorServiceFactory.getService();

			Date cenDate = storageLocal.getCROModifiedDate(Integer.toString(requestID), null);

			if (cenDate == null) {
				createNotebookPageInCeN(requestID);
			} else if (cenDate != null) {
				RequestDTOInfo info = externalCollaboratorServcies.getRequestDTOInfoForRequest(requestID);

				if (cenDate.getTime() < info.getLastUpdateDate()) {
					createNotebookPageInCeN(requestID);
				} else {
					log.debug("This request data is already in CeN and hasn't changed since in ExternalCollaborator");
				}
			}

		} catch (Exception e) {
			log.error("Error processing request " + requestID + ": ", e);
		}

		log.debug("onMessage(Message) end");
	}

	public void createNotebookPageInCeN(int requestID) throws Exception {
		log.debug("createNotebookPageInCeN(int) start");

		ExternalCollaboratorService externalCollaboratorServices = ExternalCollaboratorServiceFactory.getService();

		log.debug("ExternalCollaboratorServicesImpl created");

		NotebookPageModel[] pageModelArray = externalCollaboratorServices.getNotebookPagesForRequest(requestID);

		if (pageModelArray != null && pageModelArray.length > 0) {
			StorageDelegate storageLocal = getStorageServiceEJBLocal();

			for (NotebookPageModel model : pageModelArray) {
				CROPageInfo croInfo = model.getCroInfo();
				String userId = croInfo.getCroChemistInfo().getCroChemistID().toUpperCase();
				String siteCode = model.getSiteCode();
				String notebook = model.getNbRef().getNbNumber();

				// First - create notebook
				try {
					storageLocal.createNotebook(siteCode, userId, notebook, null);
				} catch (Exception e) {
					// If notebook already exists, ignore the exception
					log.error("Error creating notebook " + notebook + ": ", e);
				}

				// Second - check if user have this notebook
				ValidationInfo vi = storageLocal.getNotebookInfo(siteCode, notebook);
				
				if (vi != null && StringUtils.equals(vi.creator, userId)) {				
					// Third - remove existing CRO info from DB				
					storageLocal.removeCRORequestId(Integer.toString(requestID));
				
					// Fourth - if experiment exists - delete it
					try {
						String pageStatus = storageLocal.getNotebookPageCompleteStatus(siteCode, model.getNbRef().getNbRef(), model.getNbRef().getVersion());
						if (!StringUtils.isBlank(pageStatus)) {
							// Create fake sessionID to allow deleting experiment
							storageLocal.deleteExperiment(siteCode, notebook, model.getNbRef().getNbPage(), model.getNbRef().getVersion(), new SessionIdentifier("", "", "", true));
						}
					} catch (Exception e) {
						log.error("Cannot delete page " + model.getNbRef().getNbRef() + ": ", e);
					}
					
					// Fifth - create experiment
					try {
						storageLocal.createCeNExperiment(model);
					} catch (Exception e) {
						log.error("Cannot create page " + model.getNbRef().getNbRef() + ": ", e);
					}
				}
			}
		} else {
			log.debug("No NotebookPageModels prepared from ExternalCollaborator2CeN service for requestID " + requestID);
		}

		log.debug("createNotebookPageInCeN(int) end");
	}
	
	private StorageDelegate getStorageServiceEJBLocal() {
		try {
			return new StorageDelegate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
