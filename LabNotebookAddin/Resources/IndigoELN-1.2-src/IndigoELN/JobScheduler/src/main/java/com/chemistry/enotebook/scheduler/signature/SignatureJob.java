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
package com.chemistry.enotebook.scheduler.signature;

import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.esig.delegate.SignatureDelegate;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.signature.ESignatureService.FileStandard;
import com.chemistry.enotebook.signature.ESignatureService.SigningStatus;
import com.chemistry.enotebook.storage.SignaturePageVO;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignatureJob {

	private static final Log log = LogFactory.getLog(SignatureJob.class);
	
	private StorageDelegate storageDelegate;
	private SignatureDelegate signatureDelegate;
	
	private String finalStatus;
	
    @Scheduled(fixedRate = 1L * 60L * 1000L)
	public void execute() {
		log.debug("execute(JobExecutionContext) start");
        
		try {
			init();

			List<SignaturePageVO> vos = storageDelegate.getExperimentsBeingSigned(null);

			executeJob(vos);			
		} catch (Exception e) {
            log.error("Error executing Signature Job: ", e);
		}
        
		log.debug("execute(JobExecutionContext) end");
	}

	private void init() throws Exception {
		try {
			 storageDelegate = new StorageDelegate();
			 signatureDelegate = new SignatureDelegate();
		} catch (Exception e) {
			log.error("Error initializing EJB delegates: ", e);
			throw new Exception(e.getMessage(), e);
		}
	}
	
	private void updateFinalStatus() {
		try {
			finalStatus = signatureDelegate.convertSigningStatusToPageStatus(signatureDelegate.getFinalStatus());
		} catch (Exception e) {
			log.warn("Unable to get Signature Final Status. It will be set to " + CeNConstants.PAGE_STATUS_SIGNED, e);
			finalStatus = CeNConstants.PAGE_STATUS_SIGNED;
		}
        
		log.info("Signature final status: " + finalStatus);
	}
	
	private void executeJob(List<SignaturePageVO> vos) {
		if (vos == null) {
            return;
        }
		
		if (vos.size() < 1) {
            return;
        }
		
		updateFinalStatus();
		
		List<String> keys = new ArrayList<String>();
		
		try {			
			for (SignaturePageVO vo : vos) {
				keys.add(vo.getUssiKey());
			}
			
			Map<String, SigningStatus> statuses = signatureDelegate.getDocumentStatus(keys);
			
			if (statuses.size() < 1)
				return;
			
			for (String key : statuses.keySet()) {
				SigningStatus status = statuses.get(key);
				SignaturePageVO vo = getVoByKey(key, vos);
				
				if (vo != null) {
					String siteCode = vo.getSiteCode();
					int version = vo.getVersion();
					
					NotebookRef nbRef = new NotebookRef(vo.getNotebook(), vo.getExperiment());
					nbRef.setVersion(version);
					
					String notebookRef = nbRef.getNotebookRef();
					String currentPageStatus = storageDelegate.getNotebookPageStatus(siteCode, notebookRef, version).getStatus();
					String newPageStatus = signatureDelegate.convertSigningStatusToPageStatus(status);
										
					if (StringUtils.equals(newPageStatus, finalStatus) || StringUtils.equals(newPageStatus, CeNConstants.PAGE_STATUS_SUBMIT_FAILED)) {
						byte[] pdf = signatureDelegate.getFile(key, FileStandard.FILE_STANDARD);
						storageDelegate.storeExperimentPDF(siteCode, notebookRef, version, pdf);
					}
					
					if (!StringUtils.isBlank(newPageStatus) && !StringUtils.equals(currentPageStatus, newPageStatus)) {
						storageDelegate.updateNotebookPageStatus(siteCode, notebookRef, version, newPageStatus);
					}
				}
			}			
		} catch (Exception e) {
			log.error("Error checking document signature status for Keys: " + keys + ": ", e);
		}
	}
	
	private SignaturePageVO getVoByKey(String key, List<SignaturePageVO> vos) {
		for (SignaturePageVO vo : vos) {
			if (StringUtils.equals(vo.getUssiKey(), key)) {
				return vo;
			}
		}
		return null;
	}
}
