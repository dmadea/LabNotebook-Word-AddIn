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

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class SignatureDao {

    private static final Log log = LogFactory.getLog(SignatureDao.class);
    
	private static SignatureDao _instance;
	
	private static final String PROPERTIES_FILE = "signature.properties";
	private static final String PROPERTIES_SERVICE_URL = "SERVICE_URL";
	private static final String PROPERTIES_USERNAME = "USERNAME";
	private static final String PROPERTIES_PASSWORD = "PASSWORD";
	
	private String serviceUrl;
	private String apiUrl;
	private String username;
	private String password;
	
	private String sessionId;
		
	private SignatureDao() throws IOException {
		loadProperties();
	}
	
	public static synchronized SignatureDao getInstance() {
		if (_instance == null) {
			try {
				_instance = new SignatureDao();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return _instance;
	}
	
	public String getServiceURL() {
		return serviceUrl;
	}
	
	public boolean isServiceAvailable() {
		try {
			getTemplates(username);
			return true;
		} catch (Throwable t) {
            log.warn("Indigo Signature Service unavailable: ", t);
			return false;
		}
	}

	public String getReasons() throws HttpException {
		return executeMethodAsString(new GetMethod(apiUrl + "/getReasons"));
	}

	public String getStatuses() throws HttpException {
		return executeMethodAsString(new GetMethod(apiUrl + "/getStatuses"));
	}
	
	public String getFinalStatus() throws HttpException {
		return executeMethodAsString(new GetMethod(apiUrl + "/getFinalStatus"));
	}
	
	public String getTemplates(String username) throws HttpException {
		if (StringUtils.isBlank(username))
			return StringUtils.EMPTY;
		
		GetMethod get = new GetMethod(apiUrl + "/getTemplates");
		get.setQueryString(new NameValuePair[] { new NameValuePair("username", username) });
		return executeMethodAsString(get);
	}
	
	public String uploadDocument(String username, String templateId, String fileName, byte[] file) throws Exception {
		if (StringUtils.isBlank(username) || StringUtils.isBlank(templateId))
			return StringUtils.EMPTY;
		
		MultipartPostMethod post = new MultipartPostMethod(apiUrl + "/uploadDocument");

		post.addParameter("username", username);
		post.addParameter("templateId", templateId);
		post.addPart(new FilePart("file", new ByteArrayPartSource(fileName, file)));
		
		return executeMethodAsString(post);
	}
		
	public String getDocumentInfo(String documentId) throws HttpException {
		if (StringUtils.isBlank(documentId))
			return StringUtils.EMPTY;
		
		GetMethod get = new GetMethod(apiUrl + "/getDocumentInfo");
		get.setQueryString(new NameValuePair[] { new NameValuePair("id", documentId) });
		return executeMethodAsString(get);
	}
	
	public String getDocumentInfos(List<String> documentIds) throws HttpException {
		if (documentIds == null || documentIds.size() < 1)
			return StringUtils.EMPTY;
		
		PostMethod post = new PostMethod(apiUrl + "/getDocumentsByIds");

		JSONObject o = new JSONObject();
		o.put("documentsIds", documentIds);
		
		post.setRequestEntity(new ByteArrayRequestEntity(o.toJSONString().getBytes()));
		
		return executeMethodAsString(post);
	}
	
	public String getDocuments(String username) throws HttpException {
		if (StringUtils.isBlank(username))
			return StringUtils.EMPTY;
		
		GetMethod get = new GetMethod(apiUrl + "/getDocuments");
		get.setQueryString(new NameValuePair[] { new NameValuePair("username", username) });
		return executeMethodAsString(get);
	}
	
	public byte[] downloadDocument(String documentId) throws HttpException {
		if (StringUtils.isBlank(documentId))
			return new byte[0];
		
		GetMethod get = new GetMethod(apiUrl + "/downloadDocument");
		get.setQueryString(new NameValuePair[] { new NameValuePair("id", documentId) });
		return executeMethodAsBytes(get);
	}
	
	private void loadProperties() throws IOException {
		Properties props = new Properties();
		props.load(SignatureDao.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
		
		serviceUrl = props.getProperty(PROPERTIES_SERVICE_URL);
		username = props.getProperty(PROPERTIES_USERNAME);
		password = props.getProperty(PROPERTIES_PASSWORD);
		
		apiUrl = serviceUrl + "/api";
	}
	
	private boolean authenticate() throws HttpException {
		sessionId = null;
		
		boolean result = false;
		PostMethod postMethod = null;
		
		try {
			JSONObject o = new JSONObject();
            
			o.put("username", username);
			o.put("password", password);
		
			postMethod = new PostMethod(serviceUrl + "/loginProcess");
		
			postMethod.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
			postMethod.setRequestEntity(new ByteArrayRequestEntity(o.toJSONString().getBytes()));
				
			int status = new HttpClient().executeMethod(postMethod);
		
			if (status == HttpStatus.SC_OK) {
				Header header = postMethod.getResponseHeader("Set-Cookie");
				
				String[] splitted = header.getValue().split(";");
				
				for (String s : splitted) {
					String[] map = s.split(",");
					for (String m : map) {
						if (m.contains("InternalSessionId")) {
							sessionId = m.split("=")[1];
						}
					}
				}
				
				if (sessionId != null) {
					result = true;
				}
			}
		} catch (Exception e) {
			throw new HttpException("Authentication error: ", e);
		} finally {
			if (postMethod != null) {
                postMethod.releaseConnection();
            }
		}
		
		return result;
	}
		
	private String executeMethodAsString(HttpMethod method) throws HttpException {
		try {
			return executeMethod(method).getResponseBodyAsString();
		} catch (Exception e) {
			throw new HttpException(e.getMessage(), e);
		} finally {
			method.releaseConnection();
		}
	}
	
	private byte[] executeMethodAsBytes(HttpMethod method) throws HttpException {
		try {
			return executeMethod(method).getResponseBody();
		} catch (Exception e) {
			throw new HttpException(e.getMessage(), e);
		} finally {
			method.releaseConnection();
		}
	}
	
	private HttpMethod executeMethod(HttpMethod method) throws HttpException {
		try {
            HttpClient httpClient = new HttpClient();
            
			method.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
			
			if (sessionId != null) {
				method.addRequestHeader("Cookie", "JSESSIONID=" + sessionId);
			}
			
			int status = httpClient.executeMethod(method);
			if (status == HttpStatus.SC_UNAUTHORIZED) {
				authenticate();
				
				if (sessionId != null) {
					method.addRequestHeader("Cookie", "JSESSIONID=" + sessionId);
				}
				
				status = httpClient.executeMethod(method);
			}
			
			if (status == HttpStatus.SC_OK) {
				return method;
			}
			
			throw new Exception("Status = " + status);
		} catch (Exception e) {
			throw new HttpException("Error executing HTTP method: ", e);
		}
	}
}
