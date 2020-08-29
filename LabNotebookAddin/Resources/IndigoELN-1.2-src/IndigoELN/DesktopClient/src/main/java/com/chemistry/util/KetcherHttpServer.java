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
package com.chemistry.util;

import com.chemistry.ChemistryEditorProxy;
import com.chemistry.KetcherProxy;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class KetcherHttpServer {
	private static KetcherHttpServer ketcher;
	private KetcherRequestsHandler ketcherRequestsHandler;

	private KetcherHttpServer() {
		try {
			InetSocketAddress addr = new InetSocketAddress(34851);
			HttpServer server = HttpServer.create(addr, 0);
			ketcherRequestsHandler = new KetcherRequestsHandler();

			server.createContext("/", ketcherRequestsHandler);
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
			System.out.println("Server is listening on port 34851");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void run() {
		getInstance();
	}

	public static KetcherHttpServer getInstance() {
		if (ketcher == null) {
			ketcher = new KetcherHttpServer();
		}
		return ketcher;
	}

	public static void main(String[] args) {
		KetcherHttpServer.getInstance();
	}

	public void setKetcherProxy(KetcherProxy ketcherProxy) {
		ketcherRequestsHandler.setKetcherProxy(ketcherProxy);
	}
}

class KetcherRequestsHandler implements HttpHandler {	
	private static final Map<String, KetcherProxy> proxyMap = new HashMap<String, KetcherProxy>();
	private static final String elnDelim = "[endElnProxyId]";
	
	public void handle(final HttpExchange exchange) throws IOException {
		byte[] output;
		String requestMethod = exchange.getRequestMethod();
		KetcherProxy ketcherProxy = null;	
		if (requestMethod.equalsIgnoreCase("GET")) {
			Headers responseHeaders = exchange.getResponseHeaders();
			String uri = exchange.getRequestURI().toString().toLowerCase();
			ketcherProxy = getProxy(uri);			
			
			if (uri.contains("getmolfile")) {
				responseHeaders.set("Content-Type", "text/plain");
				output = ketcherProxy.getChemistry(ChemistryEditorProxy.MOL_FORMAT);
				output = output != null ? output : "".getBytes();
			} else if (uri.contains("getinfo")) {
				responseHeaders.set("Content-Type", "text/plain");
				output = ketcherProxy.getInfo() != null ? ketcherProxy.getInfo().getBytes() : "".getBytes();
			} else if (uri.contains("knocknock")) {
				responseHeaders.set("Content-Type", "text/plain");
				output = "".getBytes();
			} else {
				String id = "";
				
				if (uri.contains("id=")) {
					id = uri.substring(uri.lastIndexOf('=') + 1, uri.length());
					uri = uri.substring(0, uri.lastIndexOf('/') + 1);
				}

				responseHeaders.set("Content-Type", getMimeType(uri));
				output = getBytesFromFile(getFileLocation(uri));
				
				if (uri.equals("/")) {
					String ketcherHtml = new String(output);
					String head = "<head>";
					String script = "\r\n\t\t<script type=\"text/javascript\">\r\n\t\t\tvar elnProxyId=\"" + id + "\";\r\n\t\t</script>";
					
					int afterHead = ketcherHtml.indexOf(head) + head.length();
					
					ketcherHtml = ketcherHtml.substring(0, afterHead) + script + ketcherHtml.substring(afterHead);
					output = ketcherHtml.getBytes();
				}
			}
		} else {//POST
			String molfile = openInputStreamAsString(exchange.getRequestBody());
			
			ketcherProxy = getProxy(molfile);
			
			if (molfile.contains("RETURN_TO_ELN")) {
				ketcherProxy.enableWindow();
			} else {
				molfile = molfile.substring(molfile.indexOf(elnDelim) + elnDelim.length());
				ketcherProxy.setChemistry(molfile.getBytes(), ChemistryEditorProxy.MOL_FORMAT);
			}
			
			output = "some response".getBytes();
		}
		
		exchange.sendResponseHeaders(200, output.length);
		OutputStream responseBody = exchange.getResponseBody();
		responseBody.write(output);
		responseBody.close();
	}

	private KetcherProxy getProxy(String uriOrMolfile) {
		String id = "";
		String idTag = "?id=";
		
		int afterId = uriOrMolfile.indexOf(idTag) + idTag.length();
		
		if (uriOrMolfile.contains(elnDelim))
			id = uriOrMolfile.substring(afterId, uriOrMolfile.indexOf(elnDelim));
		else
			id = uriOrMolfile.substring(afterId);
		
		return getKetcherProxy(id);
	}
	
	public synchronized KetcherProxy getKetcherProxy(String id) {
		return proxyMap.get(id);
	}
	
	public synchronized void setKetcherProxy(KetcherProxy ketcherProxy) {
		proxyMap.put(DigestUtils.md5Hex(ketcherProxy.toString()), ketcherProxy);
	}

	public static String openInputStreamAsString(InputStream inputSnream)
			throws IOException {
		BufferedReader buff = new BufferedReader(new InputStreamReader(
				inputSnream));
		StringBuffer strBuff = new StringBuffer();
		int c;

		while ((c = buff.read()) != -1) {
			strBuff.append((char) c);
		}

		return strBuff.toString();
	}

	public byte[] getBytesFromFile(String fileLocation) throws IOException {
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream(fileLocation);
			if (is == null)
				return new byte[0];
			
			//InputStream is = new FileInputStream(new File("C:/Pfizer/cenos10/CeNDesktopClient/resources/" + fileLocation));

			int maxFileSize = (int) Math.pow(2, 16) * 3;

			byte[] bytes = new byte[maxFileSize];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			
			is.close();

			return Arrays.copyOf(bytes, offset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	private String getMimeType(String uri) {
		String mimeType = "text/plain";
		if (uri.equals("/") || uri.contains(".html")) {
			mimeType = "text/html";
		} else if (uri.contains(".xml")) {
			mimeType = "application/xml";
		} else if (uri.contains(".css")) {
			mimeType = "text/css";
		} else if (uri.contains(".js")) {
			mimeType = "text/javascript";
		} else if (uri.contains(".png")) {
			mimeType = "image/png";
		} else if (uri.contains(".gif")) {
			mimeType = "image/gif";
		} else if (uri.contains(".ico")) {
			mimeType = "image/vnd.microsoft.icon";
		} else {
			// .gpl .properties .mol
			mimeType = "text/plain";
		}

		return mimeType;
	}

	private String getFileLocation(String uri) {
		return "ketcher" + (uri.equals("/") ? "/ketcher.html" : uri);
	}
}
