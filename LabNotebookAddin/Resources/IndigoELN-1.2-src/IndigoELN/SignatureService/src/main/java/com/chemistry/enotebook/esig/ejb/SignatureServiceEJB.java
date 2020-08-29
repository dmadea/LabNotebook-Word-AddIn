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
/**
 * 
 */
package com.chemistry.enotebook.esig.ejb;

import com.chemistry.enotebook.LoadServiceException;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.esig.exceptions.SignatureDelegateException;
import com.chemistry.enotebook.esig.interfaces.SignatureRemote;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.signature.ESignatureService;
import com.chemistry.enotebook.signature.ESignatureService.FileStandard;
import com.chemistry.enotebook.signature.ESignatureService.SigningStatus;
import com.chemistry.enotebook.signature.ESignatureServiceFactory;
import com.chemistry.enotebook.signature.classes.SigDocumentVO;
import com.chemistry.enotebook.signature.classes.TemplateVO;
import com.chemistry.enotebook.signature.exceptions.*;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SignatureServiceEJB implements SignatureRemote {
	
	private static final Log log = LogFactory.getLog(SignatureServiceEJB.class);

	@Override
	public List<TemplateVO> getTemplates(String userId) throws SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		log.info("getTemplates: " + userId);
        
		if (!isServiceAvailable()) {
			throw new SignatureServiceUnavailableException();
		}
		
		ESignatureService service;
		
        try {
			service = ESignatureServiceFactory.getService();
		} catch (LoadServiceException e) {
			throw new SignatureServiceUnavailableException(e.getMessage(), e);
		}		
        
		try {
			return service.getTemplates(userId);
		}catch (Exception e) {
			throw new ESignatureServiceException(e.getMessage(), e);
		}
	}

	@Override
	public SigDocumentVO submitDocument(SigDocumentVO document) throws InvalidUserException, InvalidTemplateException, PublishException, SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		try {
			if (!areSubmissionsAllowed()) {
                throw new SignatureServiceUnavailableException();
            }
		} catch (Exception e1) {
			throw new SignatureServiceUnavailableException(e1.getMessage(), e1);
		}

		if (document == null) {
			throw new PublishException("Submission data missing");
		}
		if (StringUtils.isBlank(document.getEmail())) {
			throw new InvalidUserException("Email address value is invalid! No value was returned.");
		}
		if (document.getTemplate() == null) {
			throw new InvalidTemplateException("Template value is invalid! No value was returned.");
		}
		if (document.getDocument() == null) {
            throw new PublishException("Document missing");
        }
		
		ESignatureService service;
        
		try {
			service = ESignatureServiceFactory.getService();
		} catch (LoadServiceException e) {
			throw new SignatureServiceUnavailableException(e.getMessage(), e);
		}

        return service.submitDocument(document);
	}

	@Override
	public Map<String, SigningStatus> getDocumentStatus(List<String> keys) throws SignatureServiceUnavailableException, SignatureTokenInvalidException,  ESignatureServiceException {
		if (!isServiceAvailable()) {
            throw new SignatureServiceUnavailableException();
        }
		
		ESignatureService service;
        
		try {
			service = ESignatureServiceFactory.getService();
		} catch (LoadServiceException e) {
			throw new SignatureServiceUnavailableException(e.getMessage(), e);
		}		
		
		return service.getDocumentStatus(keys);
	}

	@Override
	public SigningStatus getDocumentStatus(String key) throws InvalidDocumentKeyException, SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		if (!isServiceAvailable()) {
            throw new SignatureServiceUnavailableException();
        }
		
		ESignatureService service;
        
		try {
			service = ESignatureServiceFactory.getService();
		} catch (LoadServiceException e) {
			throw new SignatureServiceUnavailableException(e.getMessage(), e);
		}
        
		return service.getDocumentStatus(key);
	}

	@Override
	public SigningStatus getFinalStatus() throws SignatureDelegateException, SignatureServiceUnavailableException, SignatureTokenInvalidException {
		if (!isServiceAvailable()) {
            throw new SignatureServiceUnavailableException();
        }
		
		ESignatureService service;
        
		try {
			service = ESignatureServiceFactory.getService();
		} catch (LoadServiceException e) {
			throw new SignatureServiceUnavailableException(e.getMessage(), e);
		}
        
		try {
			return service.getFinalStatus();
		} catch (ESignatureServiceException e) {
			throw new SignatureDelegateException(e.getMessage(), e);
		}
	}

	@Override
	public byte[] getFile(String key, FileStandard fileType) throws  InvalidDocumentKeyException, InvalidFileTypeException, SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		if (!isServiceAvailable()) {
            throw new SignatureServiceUnavailableException();
        }

		ESignatureService service;
        
		try {
			service = ESignatureServiceFactory.getService();
		} catch (LoadServiceException e) {
			throw new SignatureServiceUnavailableException(e.getMessage(), e);
		}
        
		return service.getFile(key, fileType);
	}

	@Override
	public void archiveFile(String username, String key, NotebookPageModel page, NotebookUser user) throws InvalidDocumentKeyException, InvalidUserException, ArchiveException, SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		if (!isServiceAvailable()) {
            throw new SignatureServiceUnavailableException();
        }
		
		if (StringUtils.isBlank(username)) {
			throw new InvalidUserException("Email address value is invalid");
		}

		// Request document be archived
		try {
			ESignatureService service = ESignatureServiceFactory.getService();
			service.archiveDocument(username, key, FileStandard.FILE_STANDARD);
		} catch (ESignatureServiceException e) {
			throw new ArchiveException("Failed to Archive document", e);
		} catch (LoadServiceException e) {
			throw new SignatureServiceUnavailableException(e.getMessage(), e);
		} 
	}

	@Override
	public boolean haveThingsToSign(String username) throws InvalidUserException, SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
        if (!isServiceAvailable()) {
            throw new SignatureServiceUnavailableException();
        }

		if (StringUtils.isBlank(username))
			throw new InvalidUserException("Username value is invalid");

		ESignatureService service;
        
		try {
			service = ESignatureServiceFactory.getService();
		} catch (LoadServiceException e) {
			throw new SignatureServiceUnavailableException(e.getMessage(), e);
		}
        
		return service.haveThingsToSign(username);
	}

	@Override
	public boolean isDocumentArchived(String key) throws  ArchiveException,	SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
        if (!isServiceAvailable()) {
            throw new SignatureServiceUnavailableException();
        }

		ESignatureService service;
		
        try {
			service = ESignatureServiceFactory.getService();
		} catch (LoadServiceException e) {
			throw new SignatureServiceUnavailableException(e.getMessage(), e);
		}		
        
		return service.isDocumentArchived(key);
	}

	@Override
	public boolean isServiceAvailable() {
		ESignatureService service;
        
		try {
			service = ESignatureServiceFactory.getService();
		} catch (LoadServiceException e) {
            log.warn("Signature Service unavailable: ", e);
			return false;
		}		
        
		return service.isServiceAvailable();
	}

	@Override
	public boolean areSubmissionsAllowed() throws Exception {
		boolean result = isServiceAvailable();

		if (result) {
			try {
				// Check for CeN system override
				String val = CeNSystemXmlProperties.getCeNProperty(CeNSystemXmlProperties.PROP_ESIG_SUBMIT_AVAIL, null);
				result = Boolean.valueOf(val);
			} catch (Throwable e) {
				log.warn("Signature Service sumissions are unavailable - CeN Property " + CeNSystemXmlProperties.PROP_ESIG_SUBMIT_AVAIL + " unreachable.");
			}
		}
        
		return result;
	}

	@Override
	public String publishDocument(NotebookPageModel page, byte[] buffer, String docName, Date modDate, TemplateVO template, NotebookUser user) throws Exception {
		String result = null;
		SigDocumentVO vo = new SigDocumentVO();
		
		vo.setDocumentName(docName);
		vo.setDocument(buffer);
		vo.setKey(page.getUssiKey());
		vo.setNtUserId(user.getNTUserID());
		vo.setEmail(user.getUserSessionToken().getSmtpEmail());
		vo.setSize(buffer.length);
		vo.setCreateDate(modDate);
		vo.setTemplate(template);
		
		vo.addAttribute("Name", docName);
        
		if (page.getPageHeader().getUserName().equals(user.getNTUserID())) {
			vo.addAttribute("Authors", user.getDisplayName());
		} else {
			vo.addAttribute("Authors", user.getFullName() + ", " + user.getDisplayName());
		}
		
		vo.addAttribute("Keywords", "Chemistry, Reaction, CeN, Notebook, Experiment, " + page.getNotebookRefAsString());
		vo.addAttribute("Title", page.getNotebookRefAsString());
		vo.addAttribute("Subject", (page.getSubject() == null ? "" :  page.getSubject()));
		vo.addAttribute("Folder", page.getNbRef().getNbNumber());
		vo.addAttribute("ArchiveType", "With Digital Signatures");
		
		SigDocumentVO voResult = null;
		
		try {
			voResult = submitDocument(vo);
		} catch (Throwable e) {
			log.error("Failed document submission for document: " + docName, e);
		}
		
		if (voResult != null) {
			page.setUssiKey(voResult.getKey());
			
			// Check if there is an Url we can launch to sign this document
			if (CommonUtils.isNotNull(voResult.getUrl())) {
				String urlString = CommonUtils.replaceAmpersandSymbol(voResult.getUrl().toString());
				page.setSignatureUrl(urlString);
			}
			
			page.setStatus(CeNConstants.PAGE_STATUS_SUBMITTED);
			page.getPageHeader().setModelChanged(true);
			
			result = page.getPageHeader().toXML();			
		}
        
		return result;
	}

	@Override
	public String getServiceUrl() {
		String result = "";
		
		if (isServiceAvailable()) {

			ESignatureService service;
			try {
				service = ESignatureServiceFactory.getService();
				result = service.getServiceUrl();
			} catch (LoadServiceException e) {
                log.warn("Error returning service URL: ", e);
			}			
		}

		if (StringUtils.isBlank(result)) {
			result = "http://localhost:8080/signatureservice";
		}
		
		return result;
	}
}
