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
package com.chemistry.enotebook.experiment.datahandlers.utils;

import com.chemistry.enotebook.domain.CeNConstants;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public final class HTMLEncoder {
	public static XPathAPI xpathAPI = null;

	static {
		xpathAPI = new XPathAPI();
	}

	public HTMLEncoder() {
	}

	/**
	 * decodes from Base64 into an HTML string
	 * 
	 * @param strencodedHTML
	 * @return HTML string
	 */
	public synchronized static String decodeHTML(String strEncodedHTML) throws Exception {
		try {
			// remove the namespace attribute from this tag as this will cause problems later
			// Find all image tags and add a </img> to the end
			Pattern pattern = Pattern.compile("xmlns=[\\S\\s\\d.]*?(>)");
			Matcher matcher = pattern.matcher(strEncodedHTML);
			if (matcher.find())
				strEncodedHTML = matcher.replaceFirst(">");

			SAXBuilder builder = new SAXBuilder();
			org.jdom.Document doc = builder.build(new StringReader(strEncodedHTML.replaceAll("\u00a0", "\u00A0").replaceAll(
					"&nbsp;", "\u00A0")));

			// get the image string for decoding
			List nodes = getJDomChildNodes(doc, "//img");
			for (int i = 0; i < nodes.size(); i++) {
				// get the filetype attribute
				String strFiletype = (((org.jdom.Element) nodes.get(i)).getAttribute("imagetype")).getValue();
				org.jdom.Element imgSourceNode = ((org.jdom.Element) nodes.get(i));

				String strEncodedImage = "";
				if (imgSourceNode != null)
					strEncodedImage = imgSourceNode.getValue();
				else
					strEncodedImage = (((org.jdom.Element) nodes.get(i)).getAttribute("src")).getValue();
				imgSourceNode.setText("");

				// Decode this string to bytes
				byte[] imgBytes = new BASE64Decoder().decodeBuffer(strEncodedImage);
				File tempFile = File.createTempFile(CeNConstants.PROGRAM_NAME, strFiletype);
				tempFile.deleteOnExit();

				// output the file
				writeTempFile(tempFile.getAbsolutePath(), imgBytes);

				// insert the filename in the html
				((org.jdom.Element) nodes.get(i)).setAttribute("src", "file:/" + tempFile.getAbsolutePath());
			}

			// return the html
			return (new XMLOutputter()).outputString(doc);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * parses HTML strings to find embedded image tags. reads image file from disk and encodes this using Base64.
	 * 
	 * @param strHTML
	 * @return encoded HTML string
	 */
	public synchronized static String encodeHTML(String strHTML) throws Exception {
		try {
			Tidy tidy = new Tidy();
			tidy.setShowWarnings(false);
			tidy.setOnlyErrors(true);
			tidy.setQuiet(true);
			// tidy.setMakeClean(true);
			tidy.setQuoteAmpersand(true);
			tidy.setQuoteNbsp(true);
			tidy.setXmlSpace(true);
			tidy.setCharEncoding(org.w3c.tidy.Configuration.UTF8);

			Document objDoc = (Document) tidy.parseDOM(StringToInputStream(strHTML), null);

			// find the IMG tags
			NodeList nodes = getChildNodes(objDoc, "//img");

			// get the image files for encoding
			for (int i = 0; i < nodes.getLength(); i++) {
				Node imgSourceNode = nodes.item(i).getAttributes().getNamedItem("src");
				Node imageNewNode = objDoc.createElement("img");
				String strImageFile = imgSourceNode.getNodeValue();
				// remove file:/
				if (strImageFile.substring(0, 6).equals("file:/")) {
					strImageFile = strImageFile.substring(6);
					// remove any encoded spaces
					strImageFile = strImageFile.replaceAll("%20", " ");
				}

				// Base64 encode the image
				String strBase64 = new BASE64Encoder().encode(getFileAsBytes(strImageFile, false));

				// insert the image back in the html
				imageNewNode.appendChild(objDoc.createTextNode(strBase64));

				// Get the file type
				String strFileType = "";
				int intFiletypeStart = strImageFile.indexOf(".", strImageFile.length() - 5);
				if (intFiletypeStart > -1)
					strFileType = strImageFile.substring(intFiletypeStart);

				((Element) imageNewNode).setAttribute("imagetype", strFileType);
				Node imageParent = nodes.item(i).getParentNode();
				imageParent.removeChild(nodes.item(i));
				imageParent.appendChild(imageNewNode);
			}

			// return the html
			return getXMLString(objDoc);
		} catch (Exception e) {
			// there was a problem parsing the HTML
			// return to the user
			throw new Exception(e.getMessage(), e);
		}
	}

	private static NodeList getChildNodes(Document objDoc, String xpath) throws Exception {
		NodeList nodelist = null;
		try {
			// Get the matching elements
			nodelist = xpathAPI.selectNodeList(objDoc, xpath);
		} catch (javax.xml.transform.TransformerException e) {
			throw e;
		}

		// return the resulting nodelist
		return nodelist;
	}

	private static List getJDomChildNodes(org.jdom.Document objDoc, String xpath) throws Exception {
		List nodelist = null;
		try {
			// Get the matching elements
			nodelist = org.jdom.xpath.XPath.selectNodes(objDoc.getRootElement(), xpath);
		} catch (JDOMException e) {
			throw e;
		}

		// return the resulting nodelist
		return nodelist;
	}

	private static byte[] getFileAsBytes(String strFilename, boolean blnGZip) throws Exception {
		// check filename in case of relative file path
		try {
			File file = new File(strFilename);
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			int bytes = (int) file.length();
			byte[] buffer = new byte[bytes];
			int readBytes = bis.read(buffer);
			if (blnGZip) {
				Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
				compressor.setInput(buffer);
				compressor.finish();
				ByteArrayOutputStream bos = new ByteArrayOutputStream(buffer.length);
				buffer = new byte[1024];

				while (!compressor.finished()) {
					int count = compressor.deflate(buffer);
					bos.write(buffer, 0, count);
				}
				bos.close();
			}
			return buffer;
		} catch (FileNotFoundException fe) {
			throw new Exception(fe.getMessage(), fe);
		} catch (IOException ioe) {
			throw new Exception(ioe.getMessage(), ioe);
		}
	}

	private static String getXMLString(Document objDoc) throws Exception {
		// Remove title node
		NodeList nodes = getChildNodes(objDoc, "//title");
		for (int i = 0; i < nodes.getLength(); i++) {
			Node titleNode = nodes.item(i);
			titleNode.getParentNode().removeChild(titleNode);
		}

		// Serialize DOM
		OutputFormat format = new OutputFormat(objDoc, "UTF-8", true);
		format.setPreserveSpace(true);

		// as a String
		StringWriter stringOut = new StringWriter();
		XMLSerializer serial = new XMLSerializer(stringOut, format);
		serial.serialize(objDoc.getDocumentElement());

		return stringOut.toString();
	}

	private static String getHTMLString(Document objDoc) throws Exception {
		// Output as html using tidy
		Tidy tidy = new Tidy();
		tidy.setShowWarnings(false);
		tidy.setOnlyErrors(true);
		tidy.setQuiet(true);
		tidy.setMakeClean(true);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		tidy.pprint(objDoc, bos);

		return bos.toString();
	}

	private static ByteArrayInputStream StringToInputStream(String inputString) {
		byte[] bytes = null;

		try {
			bytes = inputString.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		return bais;
	}

	private static void writeTempFile(String strFile, byte[] byteArray) throws IOException {
		try {
			FileOutputStream fos = new FileOutputStream(strFile);
			fos.write(byteArray);
			fos.flush();
			fos.close();
		} catch (IOException ioe) {
			throw ioe;
		}
	}
}