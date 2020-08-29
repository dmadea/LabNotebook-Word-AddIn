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
 * Created on 23-Aug-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datahandlers;

import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class XMLDataHandler {
	private Document objDoc = null;

	private StringBuffer xmlBuffer = null;

	private static XPathAPI xpathAPI = null;

	private CeNXMLParser xmlParser = null;

	static {
		xpathAPI = new XPathAPI();
	}

	public XMLDataHandler(String strXML) throws Exception {

		try {
			xmlBuffer = new StringBuffer();
			// Create a builder factory
			SAXBuilder builder = new SAXBuilder();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);

			// Create the builder and parse the file
			if (!strXML.equals("")) {
				org.jdom.Document tempDoc = builder.build(new StringReader(strXML));
				// objDoc = (Document)tempDoc;
				DOMOutputter outputter = new DOMOutputter();
				objDoc = outputter.output(tempDoc);
				// objDoc = factory.newDocumentBuilder().parse(new
				// ByteArrayInputStream(strXML.getBytes()));
			} else {
				objDoc = factory.newDocumentBuilder().newDocument();
			}
		} catch (Exception e) {
			// A parsing error occurred; the xml input is not valid
			throw new Exception("Unable to parse the XML data", e);
		}
	}

	public NodeList getChildNodes(String xPath) throws Exception {
		Node startNode = xpathAPI.selectNodeList(objDoc, xPath).item(0);
		return getChildNodes(startNode);
	}

	public NodeList getChildNodes(Node node) throws Exception {
		NodeList nodes;

		try {
			nodes = node.getChildNodes();
		} catch (Exception e) {
			throw new Exception("Unable to find node element in XML", e);
		}
		return nodes;

	}

	public Hashtable getChildNodes() {
		Hashtable nodeMap = new Hashtable();

		NodeList nodes = objDoc.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			nodeMap.put(nodes.item(i), getAbsolutePath((Element) nodes.item(i)));
		}
		return nodeMap;
	}

	public Node getNode(String xPath) {
		Node node = null;
		try {
			node = xpathAPI.selectSingleNode(objDoc, xPath);
		} catch (TransformerException tExc) {
			tExc.printStackTrace();
		}
		return node;
	}

	public Node getNode(String xPath, int index) throws Exception {
		try {
			NodeList nodeList = xpathAPI.selectNodeList(objDoc, xPath);
			if (nodeList.getLength() > 0) {
				return nodeList.item(index);
			}
		} catch (Exception e) {
			throw new Exception("Unable to find node element in XML", e);
		}
		return null;
	}

	public String getNodeValue(String xPath) throws Exception {
		String strValue = null;

		try {
			Node node = xpathAPI.selectNodeList(objDoc, xPath).item(0);
			if (node.getFirstChild() != null) {
				strValue = node.getFirstChild().getNodeValue();
			}
		} catch (Exception e) {
			throw new Exception("Unable to find node element in XML", e);
		}
		return strValue;
	}

	public ArrayList getAttributes(String xPath, int itemIndex) throws Exception {
		ArrayList attList = new ArrayList();

		try {
			Node node = xpathAPI.selectNodeList(objDoc, xPath).item(itemIndex);
			NamedNodeMap attributeMap = node.getAttributes();
			for (int i = 1; i <= attributeMap.getLength(); i++) {
				attList.add(attributeMap.item(i).getNodeName());
			}
		} catch (Exception e) {
			throw new Exception("Unable to find node element in XML", e);
		}
		return attList;
	}

	public String getAttributeValue(String xPath, int itemIndex, String attributeName) throws Exception {
		String strValue = "";

		try {
			Node node = xpathAPI.selectNodeList(objDoc, xPath).item(itemIndex);
			Node attributeNode = node.getAttributes().getNamedItem(attributeName);
			if (attributeNode != null) {
				strValue = attributeNode.getNodeValue();
			}
		} catch (Exception e) {
			throw new Exception("Unable to find node element in XML", e);
		}
		return strValue;
	}

	public String getAttributeValue(Node node, String attributeName) throws Exception {
		String strValue = "";

		try {
			Node attributeNode = node.getAttributes().getNamedItem(attributeName);
			if (attributeNode != null) {
				strValue = attributeNode.getNodeValue();
			}
		} catch (Exception e) {
			throw new Exception("Unable to find node attribute " + attributeName + " in XML", e);
		}
		return strValue;
	}

	public String getAbsolutePath(Element e) {
		String path = "/" + e.getTagName();
		if (e.hasAttribute("id"))
			path += "[@id='" + e.getAttribute("id") + "']";

		Node parent = e.getParentNode();
		if (parent.getNodeType() == Node.ELEMENT_NODE)
			return getAbsolutePath((Element) parent) + path;
		else
			return path;
	}

	/**
	 * @param parentXML
	 * @param path
	 * @param value
	 * @return
	 */
	public boolean createNewElement(StringBuffer parentXML, String path, String value, boolean forceDuplicateElement) {
		StringBuffer xPath = new StringBuffer();
		String previousPath = null;
		String xmlText = parentXML.toString();
		int index = 0;

		if (value == null)
			value = "";
		StringTokenizer token = new StringTokenizer(path, "/");

		while (token.hasMoreTokens()) {
			String nodeName = (String) token.nextElement();
			if (xPath.length() > 0)
				xPath.append("/");
			xPath.append(nodeName);
			// setting initial path
			if (previousPath == null)
				previousPath = xPath.toString();
			if (CeNXMLParser.isPathAvailable(xmlText, xPath.toString())) {
				// If duplicates allowed then it try to insert a new tag
				// with same name but different values
				if (forceDuplicateElement) {
					// check if the full path has been reached
					if (xPath.toString().equals(path)) {
						// For force duplicate insert, checking for duplicate entry for the node,
						// if exists then gets the last index to insert the new node
						// else it would insert as a first element
						if (CeNXMLParser.isPathAvailable(xmlText, xPath.toString()))
							index = CeNXMLParser.getIndexForDuplicateNodesEntry(xmlText, xPath.toString());
						else
							index = CeNXMLParser.getInsertIndexToPath(xmlText, previousPath);

						CeNXMLParser.insertNodeAtIndex(parentXML, nodeName, value, index);
					}
				}
				previousPath = xPath.toString();
				continue;
			} else {
				index = CeNXMLParser.getInsertIndexToPath(xmlText, previousPath);
				CeNXMLParser.insertNodeAtIndex(parentXML, nodeName, value, index);
			}
			xmlText = parentXML.toString();
			previousPath = xPath.toString();
		}
		return true;
	}

	public boolean insertNewXMLAttribute(StringBuffer xmlBuffer, String xpath, String attributeName, String attributeValue) {
		String xmlText = xmlBuffer.toString();
		String attributeText = " " + attributeName + "=\"" + attributeValue + "\"";
		if (CeNXMLParser.isPathAvailable(xmlText, xpath)) {
			int index = CeNXMLParser.getIndexToPath(xmlText, xpath);
			// index returns afte the closing tag of the last xpath element
			// index need to reduce to add attribute
			index--;
			xmlBuffer.insert(index, attributeText);
		} else
			return false;

		return true;
	}

	public Node createNewElement(Node parentNode, String path, String value) throws Exception {
		StringBuffer xPath = new StringBuffer();
		Node previousNode = null;
		// //System.out.println("ParentNode : "+parentNode);
		if (value == null)
			value = "";

		try {
			StringTokenizer token = new StringTokenizer(path, "/");
			while (token.hasMoreTokens()) {
				if (xPath.length() > 0)
					xPath.append("/");
				String nodeName = (String) token.nextElement();
				xPath.append(nodeName);
				Node node = null;
				if (parentNode.hasChildNodes())
					node = xpathAPI.selectSingleNode(parentNode, xPath.toString());

				// if node is null then create the element
				if (node == null || !token.hasMoreTokens()) {
					Node newNode = objDoc.createElement(nodeName);

					// if this is the last element of the path set the value
					if (!token.hasMoreTokens())
						newNode.appendChild(objDoc.createTextNode(value));

					if (previousNode != null)
						previousNode.appendChild(newNode);
					else
						parentNode.appendChild(newNode);
					previousNode = newNode;
				} else
					previousNode = node;
			}

			return previousNode;
		} catch (Exception e) {
			throw new Exception("Unable to create XML", e);
		}
	}

	public Node createNewElement(String elementPath, String elementValue) throws Exception {
		return createNewElement(objDoc, elementPath, elementValue);
	}

	public boolean createNewXMLElement(String elementPath, String elementValue, boolean forceDuplicateElement) throws Exception {
		return createNewElement(this.xmlBuffer, elementPath, elementValue, forceDuplicateElement);
	}

	public boolean createNewXMLAttribute(String xPath, String attributeName, String attributeValue) throws Exception {
		return insertNewXMLAttribute(this.xmlBuffer, xPath, attributeName, attributeValue);
	}

	public void createNewAttribute(String xPath, String attributeName, String attributeValue) throws Exception {
		try {
			Element node = (Element) xpathAPI.selectSingleNode(objDoc, xPath);
			// if node is not null then create the attribute
			if (node != null)
				node.setAttribute(attributeName, attributeValue);
		} catch (Exception e) {
			throw new Exception("Unable to create XML", e);
		}
	}

	public void createNewAttribute(Node node, String attributeName, String attributeValue) throws Exception {
		try {
			// if node is not null then create the attribute
			if (node != null)
				((Element) node).setAttribute(attributeName, attributeValue);
		} catch (Exception e) {
			throw new Exception("Unable to create XML", e);
		}
	}

	public String getXMLString() throws Exception {
		return getXMLString(true);
	}

	public String getXMLFormattedString() {
		// //System.out.println("*****************");
		// //System.out.println(xmlBuffer.toString());
		return xmlBuffer.toString();
	}

	public String getXMLString(boolean includeVersionInfo) throws Exception {
		try {
			// Serialize DOM
			OutputFormat format = new OutputFormat(objDoc, "UTF-8", true);
			if (!includeVersionInfo)
				format.setOmitXMLDeclaration(true);

			// as a String
			StringWriter stringOut = new StringWriter();
			XMLSerializer serial = new XMLSerializer(stringOut, format);
			serial.serialize(objDoc.getDocumentElement());
			return stringOut.toString();
		} catch (IOException ioe) {
			throw new Exception("Unable to create XML", ioe);
		}

	}// end method
}