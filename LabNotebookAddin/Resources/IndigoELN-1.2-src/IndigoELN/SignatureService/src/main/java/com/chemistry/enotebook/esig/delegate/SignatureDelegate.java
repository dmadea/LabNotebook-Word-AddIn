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
/*
 * Created on Jul 22, 2004
 *
 * Business delegate can decouple business components from the code that uses them. 
 * The Business Delegate pattern manages the complexity of distributed component 
 * lookup and exception handling, and may adapt the business component interface 
 * to a simpler interface for use by views. 
 * */

package com.chemistry.enotebook.esig.delegate;

import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.esig.exceptions.SignatureDelegateException;
import com.chemistry.enotebook.esig.interfaces.SignatureRemote;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import com.chemistry.enotebook.signature.ESignatureService.FileStandard;
import com.chemistry.enotebook.signature.ESignatureService.SigningStatus;
import com.chemistry.enotebook.signature.classes.SigDocumentVO;
import com.chemistry.enotebook.signature.classes.TemplateVO;
import com.chemistry.enotebook.signature.exceptions.*;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SignatureDelegate implements SignatureRemote {

	private SignatureRemote service;
	
	public SignatureDelegate() {
		service = ServiceLocator.getInstance().locateService("SignatureService", SignatureRemote.class);
	}
	
	@Override
	public List<TemplateVO> getTemplates(String userId) throws SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		return service.getTemplates(userId);
	}

	@Override
	public SigDocumentVO submitDocument(SigDocumentVO vo) throws InvalidUserException, InvalidTemplateException, PublishException, SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		return service.submitDocument(vo);
	}

	@Override
	public Map<String, SigningStatus> getDocumentStatus(List<String> keys) throws SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		return service.getDocumentStatus(keys);
	}

	@Override
	public SigningStatus getDocumentStatus(String key) throws InvalidDocumentKeyException, SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		return service.getDocumentStatus(key);
	}

	@Override
	public SigningStatus getFinalStatus() throws SignatureDelegateException, SignatureServiceUnavailableException, SignatureTokenInvalidException {
		return service.getFinalStatus();
	}

	@Override
	public byte[] getFile(String key, FileStandard fileType) throws InvalidDocumentKeyException, InvalidFileTypeException, SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		return service.getFile(key, fileType);
	}

	@Override
	public void archiveFile(String username, String key, NotebookPageModel page, NotebookUser user) throws InvalidDocumentKeyException, InvalidUserException, ArchiveException, SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		service.archiveFile(username, key, page, user);
	}

	@Override
	public boolean haveThingsToSign(String username) throws InvalidUserException, SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		return service.haveThingsToSign(username);
	}

	@Override
	public boolean isDocumentArchived(String key) throws ArchiveException, SignatureServiceUnavailableException, SignatureTokenInvalidException, ESignatureServiceException {
		return service.isDocumentArchived(key);
	}

	@Override
	public boolean isServiceAvailable() throws SignatureServiceUnavailableException {
		return service.isServiceAvailable();
	}

	@Override
	public boolean areSubmissionsAllowed() throws Exception {
		return service.areSubmissionsAllowed();
	}

	@Override
	public String publishDocument(NotebookPageModel page, byte[] buffer, String docName, Date modDate, TemplateVO template, NotebookUser user) throws Exception {
		return service.publishDocument(page, buffer, docName, modDate, template, user);
	}

	public static String convertStatusToString(SigningStatus statusCode) {
		if (statusCode != null) {
			switch (statusCode) {
			case STATUS_NEW:
				return "NEW";
			case STATUS_PUBLISHED:
				return "PUBLISHED";
			case STATUS_SIGNED:
				return "SIGNED";
			case STATUS_COMPLETE:
				return "COMPLETE";
			case STATUS_ARCHIVING:
				return "ARCHIVING";
			case STATUS_ARCHIVED:
				return "ARCHIVED";
			case STATUS_REJECTED:
				return "REJECTED";
			}
		}
		return "UNKNOWN";
	}

	public String convertSigningStatusToPageStatus(SigningStatus status) {
		String result = StringUtils.EMPTY;

		switch (status) {
		case STATUS_NEW:
			result = CeNConstants.PAGE_STATUS_SUBMITTED;
			break;
		case STATUS_PUBLISHED:
			result = CeNConstants.PAGE_STATUS_SIGNING;
			break;
		case STATUS_SIGNED:
			result = CeNConstants.PAGE_STATUS_SIGNED;
			break;
		case STATUS_COMPLETE:
			result = CeNConstants.PAGE_STATUS_SIGNED;
			break;
		case STATUS_ARCHIVING:
			result = CeNConstants.PAGE_STATUS_ARCHIVING;
			break;
		case STATUS_ARCHIVED:
			result = CeNConstants.PAGE_STATUS_ARCHIVED;
			break;
		case STATUS_REJECTED:
			result = CeNConstants.PAGE_STATUS_SUBMIT_FAILED;
			break;
		default:
			break;
		}

		return result;
	}

	@Override
	public String getServiceUrl() {
		return service.getServiceUrl();
	}
}
