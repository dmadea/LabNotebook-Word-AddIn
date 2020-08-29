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
package com.chemistry.enotebook.signature.clean;

import com.chemistry.enotebook.signature.ESignatureService;
import com.chemistry.enotebook.signature.classes.SigDocumentVO;
import com.chemistry.enotebook.signature.classes.TemplateVO;
import com.chemistry.enotebook.signature.exceptions.ESignatureServiceException;
import com.chemistry.enotebook.signature.exceptions.PublishException;
import com.chemistry.enotebook.signature.exceptions.SignatureServiceUnavailableException;
import com.chemistry.enotebook.signature.exceptions.SignatureTokenInvalidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ESignatureServiceCleanImpl implements ESignatureService {
	
	public boolean isServiceAvailable() {
		return true;
	}

	public List<TemplateVO> getTemplates(String userId) throws Exception {
		return new ArrayList<TemplateVO>();
	}

	public SigDocumentVO submitDocument(SigDocumentVO document) throws SignatureTokenInvalidException, PublishException, SignatureServiceUnavailableException {
		return null;
	}

	public boolean haveThingsToSign(String username) throws SignatureTokenInvalidException, ESignatureServiceException, SignatureServiceUnavailableException {
		return false;
	}

	public byte[] getFile(String key, FileStandard fileStandard) throws SignatureTokenInvalidException, ESignatureServiceException, SignatureServiceUnavailableException {
		return null;
	}

	public SigningStatus getDocumentStatus(String key) throws SignatureTokenInvalidException, ESignatureServiceException, SignatureServiceUnavailableException {
		return null;
	}

	public Map<String, SigningStatus> getDocumentStatus(List<String> keys) throws ESignatureServiceException, SignatureServiceUnavailableException, SignatureTokenInvalidException {
		return null;
	}
	
	public void archiveDocument(String username, String key, FileStandard fileStandard) throws ESignatureServiceException {
		
	}

	public boolean isDocumentArchived(String key) throws SignatureTokenInvalidException, ESignatureServiceException, SignatureServiceUnavailableException {
		return true;
	}

	@Override
	public SigningStatus getFinalStatus() throws ESignatureServiceException,
			SignatureServiceUnavailableException,
			SignatureTokenInvalidException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceUrl() {
		return "";
	}
}
