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
package com.chemistry.enotebook.signature;

import com.chemistry.enotebook.signature.classes.SigDocumentVO;
import com.chemistry.enotebook.signature.classes.TemplateVO;
import com.chemistry.enotebook.signature.exceptions.ESignatureServiceException;
import com.chemistry.enotebook.signature.exceptions.PublishException;
import com.chemistry.enotebook.signature.exceptions.SignatureServiceUnavailableException;
import com.chemistry.enotebook.signature.exceptions.SignatureTokenInvalidException;

import java.util.List;
import java.util.Map;

/**
 * Interface for external Electronic Signature Service
 */
public interface ESignatureService {

	/**
	 * Check service health
	 * 
	 * @return true if service available, false if service unavaliable
	 */
	public boolean isServiceAvailable();

	/**
	 * Get signature templates for user
	 * 
	 * @param userId
	 *            username
	 * @return Signature templates for user
	 * @throws Exception
	 */
	public List<TemplateVO> getTemplates(String userId) throws Exception;

	/**
	 * Submit document for signature
	 * 
	 * @param document
	 *            document for submit
	 * @return Document info with document key from service
	 * @throws PublishException
	 * @throws SignatureServiceUnavailableException
	 * @throws ESignatureServiceException
	 * @throws SignatureTokenInvalidException
	 */
	public SigDocumentVO submitDocument(SigDocumentVO document)
			throws PublishException, SignatureServiceUnavailableException,
			ESignatureServiceException, SignatureTokenInvalidException;

	/**
	 * Get status on which status checks should be stopped. Typically SIGNED or ARCHIVED
	 * 
	 * @return
	 * @throws ESignatureServiceException
	 * @throws SignatureServiceUnavailableException
	 * @throws SignatureTokenInvalidException
	 */
	public SigningStatus getFinalStatus()
			throws ESignatureServiceException,
			SignatureServiceUnavailableException,
			SignatureTokenInvalidException;
	
	/**
	 * Get document signing status
	 * 
	 * @param key
	 *            document key
	 * @return Signing status for document
	 * @throws ESignatureServiceException
	 * @throws SignatureServiceUnavailableException
	 * @throws SignatureTokenInvalidException
	 */
	public SigningStatus getDocumentStatus(String key)
			throws ESignatureServiceException,
			SignatureServiceUnavailableException,
			SignatureTokenInvalidException;

	/**
	 * Get signing status for multiple documents
	 * 
	 * @param keys
	 *            document keys
	 * @return Signing statuses for documents
	 * @throws ESignatureServiceException
	 * @throws SignatureServiceUnavailableException
	 * @throws SignatureTokenInvalidException
	 */
	public Map<String, SigningStatus> getDocumentStatus(List<String> keys)
			throws ESignatureServiceException,
			SignatureServiceUnavailableException,
			SignatureTokenInvalidException;
	
	/**
	 * Check if user have submitted documents
	 * 
	 * @param email
	 *            user's email
	 * @return true if have documents to sign, false if not
	 * @throws ESignatureServiceException
	 * @throws SignatureServiceUnavailableException
	 * @throws SignatureTokenInvalidException
	 */
	public boolean haveThingsToSign(String username)
			throws ESignatureServiceException,
			SignatureServiceUnavailableException,
			SignatureTokenInvalidException;

	/**
	 * Retrieve document contents
	 * 
	 * @param key
	 *            document key
	 * @param fileStandard
	 *            file type (@See FileStandard)
	 * @return Document contents
	 * @throws SignatureTokenInvalidException
	 * @throws ESignatureServiceException
	 * @throws SignatureServiceUnavailableException
	 */
	public byte[] getFile(String key, FileStandard fileStandard)
			throws SignatureTokenInvalidException, ESignatureServiceException,
			SignatureServiceUnavailableException;

	/**
	 * Archive document after signing
	 * 
	 * @param email
	 *            user's email
	 * @param key
	 *            document key
	 * @param fileStandard
	 *            file type (@See FileStandard)
	 * @throws ESignatureServiceException
	 */
	public void archiveDocument(String email, String key, FileStandard fileStandard)
			throws ESignatureServiceException;

	public String getServiceUrl();
	
	/**
	 * Check if document is archived
	 * 
	 * @param document
	 *            document name (Notebook-Page-Version.pdf)
	 * @return true if document is archived, false if not
	 * @throws ESignatureServiceException
	 * @throws SignatureServiceUnavailableException
	 * @throws SignatureTokenInvalidException
	 */
	public boolean isDocumentArchived(String key)
			throws ESignatureServiceException,
			SignatureServiceUnavailableException,
			SignatureTokenInvalidException;

	/**
	 * File type
	 */
	public static enum FileStandard {
		FILE_SOURCE, // Original document submitted
		FILE_STANDARD, // Document containing signatures
		FILE_FLATTENED; // Document containing flat signatures
	}

	/**
	 * Status of signature process
	 */
	public static enum SigningStatus {
		STATUS_NEW, // Document is new, and has not been saved or published.
		STATUS_PUBLISHED, // Document has been published to SafeProfiler
		STATUS_SIGNED, // Document has one or more signatures applied
		STATUS_COMPLETE, // Document has all signatures applied
		STATUS_ARCHIVING, // Document is archiving
		STATUS_ARCHIVED, // Document has been archived
		STATUS_REJECTED; // Document has been rejected by a signer
	}
}
