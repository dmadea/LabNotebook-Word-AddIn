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
package com.ggasoftware.indigo.eln.services.signature;

import com.chemistry.enotebook.signature.ESignatureService;
import com.chemistry.enotebook.signature.classes.SigDocumentVO;
import com.chemistry.enotebook.signature.classes.TemplateVO;
import com.chemistry.enotebook.signature.exceptions.ESignatureServiceException;
import com.chemistry.enotebook.signature.exceptions.PublishException;
import com.chemistry.enotebook.signature.exceptions.SignatureServiceUnavailableException;
import com.chemistry.enotebook.signature.exceptions.SignatureTokenInvalidException;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.URL;
import java.util.*;

public class SignatureServiceImpl implements ESignatureService {
	
    private static final Log log = LogFactory.getLog(SignatureServiceImpl.class);
    
	private SignatureDao dao;
	private String finalStatus;
	private Map<Long, SigningStatus> statuses;
	
	public SignatureServiceImpl() {
		dao = SignatureDao.getInstance();
		getStatuses();
	}
	
	@Override
	public boolean isServiceAvailable() {
		return dao.isServiceAvailable();
	}

	@Override
	public List<TemplateVO> getTemplates(String userId) throws Exception {
		JSONParser parser = new JSONParser();
		List<TemplateVO> result = new ArrayList<TemplateVO>();
		
		if (StringUtils.isBlank(userId))
			return result;
		
		// GET REASONS
		
		long authorReason = -1;
		
		String reasonsJSON = dao.getReasons();
		
		if (StringUtils.isBlank(reasonsJSON))
			return result;
		
		JSONObject obj = (JSONObject) parser.parse(reasonsJSON);
		
		List<JSONObject> reasons = (List<JSONObject>) obj.get("Reasons");
		for (JSONObject reason : reasons) {
			if (StringUtils.contains(((String)reason.get("name")).toLowerCase(), "author")) {
				authorReason = (Long) reason.get("id");
			}
		}
				
		// GET TEMPLATES
		
		String templatesJSON = dao.getTemplates(userId);
		
		if (StringUtils.isBlank(templatesJSON))
			return result;
		
		obj = (JSONObject) parser.parse(templatesJSON);
		List<JSONObject> templates = (List<JSONObject>) obj.get("Templates");
		
		for (JSONObject template : templates) {
			List<String> blockNames = new ArrayList<String>();
			TemplateVO vo = new TemplateVO();
			
			vo.setTemplateId((String) template.get("id"));
			vo.setTemplateName((String) template.get("name"));
			
			List<JSONObject> blocks = (List<JSONObject>) template.get("signatureBlocks");
			for (JSONObject block : blocks) {
				long reason = (Long) block.get("reason");
				if (reason == authorReason) {
					blockNames.add("Author");
				} else {
					blockNames.add("Another reason");
				}
			}
			
			vo.setSignatureBlockNames(blockNames.toArray(new String[0]));
			
			result.add(vo);
		}
		
		return result;
	}

	@Override
	public SigDocumentVO submitDocument(SigDocumentVO document) throws PublishException, SignatureServiceUnavailableException, ESignatureServiceException, SignatureTokenInvalidException {
		try {
			String documentJSON = dao.uploadDocument(document.getNtUserId(), document.getTemplate().getTemplateId(), document.getDocumentName(), document.getDocument());
			
			if (StringUtils.isNotBlank(documentJSON)) {
				document.setKey((String) ((JSONObject)new JSONParser().parse(documentJSON)).get("id"));
				document.setUrl(new URL(dao.getServiceURL()));
			}
			
			return document;
		} catch (Exception e) {
			throw new ESignatureServiceException(e.getMessage(), e);
		}
	}

	@Override
	public SigningStatus getDocumentStatus(String key) throws ESignatureServiceException, SignatureServiceUnavailableException, SignatureTokenInvalidException {
		getStatuses();
		
		SigningStatus result = SigningStatus.STATUS_REJECTED;
		
		try {
			String documentJSON = dao.getDocumentInfo(key);
			
			if (StringUtils.isBlank(documentJSON))
				return result;
			
			Long status = (Long) ((JSONObject) new JSONParser().parse(documentJSON)).get("status");
			
			if (statuses.get(status) != null) {
				result = statuses.get(status);
			}
		} catch (Exception e) {
			throw new ESignatureServiceException(e.getMessage(), e);
		}
		
		return result;
	}

	public synchronized void getStatuses() {
		if (statuses == null) {
			try {
				statuses = mapSigningStasuses(dao.getStatuses());
			} catch (Exception e) {
                log.warn("Error returning statuses: ", e);
				statuses = null;
			}
		}
	}
	
	public synchronized SigningStatus getFinalStatus() throws ESignatureServiceException, SignatureServiceUnavailableException, SignatureTokenInvalidException {
		if (finalStatus == null) {		
			try {
				finalStatus = dao.getFinalStatus();
			} catch (Exception e) {
                log.warn("Error returning final status: ", e);
				finalStatus = null;
			}
		}
		
		if (finalStatus != null) {
			return SigningStatus.valueOf("STATUS_" + finalStatus);
		}
		
		return SigningStatus.STATUS_SIGNED;
	}
	
	@Override
	public Map<String, SigningStatus> getDocumentStatus(List<String> keys) throws ESignatureServiceException, SignatureServiceUnavailableException, SignatureTokenInvalidException {
		getStatuses();
		
		Map<String, SigningStatus> result = new HashMap<String, SigningStatus>();
		
		try {
			List<String> filteredKeys = new ArrayList<String>();
			for (String key : keys) {
				if (!StringUtils.isBlank(key) && isUUID(key)) {
					filteredKeys.add(key);
				}
			}
			
			String documentsJSON = dao.getDocumentInfos(filteredKeys);
			
			if (StringUtils.isBlank(documentsJSON))
				return result;
			
			List<JSONObject> docs = (List<JSONObject>) ((JSONObject) new JSONParser().parse(documentsJSON)).get("Documents");
			
			if (docs != null) {
				for (int i = 0; i < docs.size(); ++i) {
					JSONObject doc = docs.get(i);
					if (doc != null) {
						Long status = (Long) doc.get("status");
						
						if (statuses.get(status) != null) {
							result.put((String) doc.get("id"), statuses.get(status));
						}	
					}
				}			
			}
		} catch (Exception e) {
			throw new ESignatureServiceException(e);
		}
		
		return result;
	}

	@Override
	public boolean haveThingsToSign(String username) throws ESignatureServiceException, SignatureServiceUnavailableException, SignatureTokenInvalidException {
		try {
			String documentsJSON = dao.getDocuments(username);
			
			if (StringUtils.isBlank(documentsJSON))
				return false;
			
			List<JSONObject> docs = (List<JSONObject>) ((JSONObject) new JSONParser().parse(documentsJSON)).get("Documents");
			
			for (JSONObject doc : docs) {
				if (((Boolean) doc.get("actionRequired")).booleanValue()) {
					return true;
				}
			}
			
			return false;
		} catch (Exception e) {
			throw new ESignatureServiceException(e);
		}
	}

	@Override
	public byte[] getFile(String key, FileStandard fileStandard) throws SignatureTokenInvalidException, ESignatureServiceException, SignatureServiceUnavailableException {
		try {
			return dao.downloadDocument(key);
		} catch (HttpException e) {
			throw new ESignatureServiceException(e);
		}
	}

	@Override
	public void archiveDocument(String username, String key, FileStandard fileStandard) throws ESignatureServiceException {
		// Not implemented
	}

	@Override
	public boolean isDocumentArchived(String key) throws ESignatureServiceException, SignatureServiceUnavailableException, SignatureTokenInvalidException {
		try {
			return SigningStatus.STATUS_ARCHIVED.equals(getDocumentStatus(key));			
		} catch (Exception e) {
			throw new ESignatureServiceException(e);
		}
	}
	
	private Map<Long, SigningStatus> mapSigningStasuses(String statusesJSON) throws ParseException {
		Map<Long, SigningStatus> result = new HashMap<Long, SigningStatus>();
		
		if (StringUtils.isBlank(statusesJSON))
			return result;
		
		JSONObject statuses = (JSONObject) new JSONParser().parse(statusesJSON);
		List<JSONObject> list = (List<JSONObject>) statuses.get("Statuses");
		
		for (JSONObject status : list) {
			Long id = Long.parseLong(status.get("id").toString());
			SigningStatus value = SigningStatus.STATUS_REJECTED;
			
			switch (id.intValue()) {
			case 1:
				value = SigningStatus.STATUS_NEW;
				break;
			case 2:
				value = SigningStatus.STATUS_PUBLISHED;
				break;
			case 3:
				value = SigningStatus.STATUS_SIGNED;
				break;
			case 4:
				value = SigningStatus.STATUS_REJECTED;
				break;
			case 7:
				value = SigningStatus.STATUS_SIGNED;
				break;
			case 8:
				value = SigningStatus.STATUS_ARCHIVED;
				break;
			default:
				break;
			}
			
			result.put(id, value);
		}

		return result;
	}

	@Override
	public String getServiceUrl() {
		return dao.getServiceURL();
	}

    private boolean isUUID(String s) {
        try {
            UUID.fromString(s);
            return true;
        } catch (Throwable t) {
            log.warn("Invalid UUID: ", t);
            return false;
        }
    }
}
