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
package com.chemistry.enotebook.experiment.utils;

import com.chemistry.enotebook.properties.CeNSystemXmlProperties;

import java.util.StringTokenizer;

/**
 * 
 * 
 */
public class CeNXMLParser {

	private final static String newLine = "\n";

	public CeNXMLParser() {

	}

	/**
	 * Checks of any child nodes for the given node in the xmlText
	 * 
	 * @param xmlText
	 *            an XML format
	 * @param node
	 *            a node within XML
	 * @return
	 */
	public static boolean hasChildNodes(String xmlText, String node) {
		String startNode = null;
		String startNodeWithArg = null;
		String endNode = null;
		String emptyNode = null;
		if (xmlText.indexOf(node) != -1) {
			// emptyNode
			emptyNode = "<" + node + "/>";
			// if empty tag found then node does not have children
			if (xmlText.indexOf(emptyNode) != -1)
				return false;
			// Node with/without attributes
			startNode = "<" + node + ">";
			startNodeWithArg = "<" + node + " ";
			// if xmlText contains startNode
			if (xmlText.indexOf(startNode) != -1 || xmlText.indexOf(startNodeWithArg) != -1) {
				endNode = "</" + node + ">";
				// substring between startNode and endNode
				node = xmlText.substring(xmlText.indexOf(startNode) + startNode.length(), xmlText.indexOf(endNode));
				// Check of child node starting tag within the node,
				if (node.indexOf("<") != -1)
					return true;
			}
		}
		return false;
	}

	/**
	 * Checks for the availability of node in the XML
	 * 
	 * @param xmlText
	 * @param node
	 * @return
	 */
	public static boolean isNodeAvailable(String xmlText, String node) {

		String startNode = null;
		String startNodeWithArg = null;
		String emptyNode = null;
		if (xmlText.indexOf(node) != -1) {
			// emptyNode
			emptyNode = "<" + node + "/>";
			// if empty tag found then node does not have children
			if (xmlText.indexOf(emptyNode) != -1)
				return true;
			// Node with/without attributes
			startNode = "<" + node + ">";
			startNodeWithArg = "<" + node + " ";
			// if xmlText contains startNode
			if (xmlText.indexOf(startNode) != -1 || xmlText.indexOf(startNodeWithArg) != -1)
				return true;
		}
		return false;
	}

	/**
	 * Checks for Path presence in the xml string..
	 * 
	 * @param xmlText
	 * @param path
	 * @return
	 */
	public static boolean isPathAvailable(String xmlText, String path) {
		String node = null;
		StringTokenizer token = new StringTokenizer(path, "/");
		while (token.hasMoreTokens()) {
			node = token.nextToken();
			xmlText = getXMLContainedInNode(xmlText, node);
			if (xmlText == null)
				return false;
		}

		return true;
	}

	/**
	 * For a given xml, it would return int which represents the position of the end ">" of the start tag of the node for ex: for
	 * node Meta-Data in the following XML would return 44, which is index of ">" in <Meta_Data>. <Batch_Properties Type="REAGENT">
	 * <Meta_Data>Test</Meta_Data> </Batch_Properties>
	 * 
	 * @param xmlText
	 * @param node
	 * @return
	 */
	public static int getIndexToNode(String xmlText, String node) {
		int index = -1;
		String startNode = null;
		String startNodeWithArg = null;
		String endNode = null;
		// Check if node is available in the subXMLText
		if (CeNXMLParser.isNodeAvailable(xmlText, node)) {
			startNode = "<" + node + ">";
			startNodeWithArg = "<" + node + " ";
			endNode = "</" + node + ">";
			// gets last index of the startNode in the given xmlText
			index = xmlText.lastIndexOf(startNode);
			if (index == -1) {
				index = xmlText.lastIndexOf(startNodeWithArg);
			}
			xmlText = xmlText.substring(index, xmlText.lastIndexOf(endNode));
			index += xmlText.indexOf(">") + 1;
		}
		return index;
	}

	/**
	 * For a given xml, it would return text between the start and end node of the node tag ex: for node Meta-Data in the following
	 * XML would return "Test". <Batch_Properties Type="REAGENT"> <Meta_Data>Test</Meta_Data> </Batch_Properties>
	 * 
	 * @param xmlText
	 * @param node
	 * @return
	 */
	public static String getXMLContainedInNode(String xmlText, String node) {
		String startNode = "<" + node + ">";
		String startNodeWithArg = "<" + node + " ";
		String endNode = "</" + node + ">";
		int index = xmlText.lastIndexOf(startNode);
		// return null if node is not found in the xmlText
		if (index == -1) {
			index = xmlText.lastIndexOf(startNodeWithArg);
			if (index == -1)
				return null;
		}
		xmlText = xmlText.substring(index, xmlText.lastIndexOf(endNode));
		index = xmlText.indexOf(">") + 1;
		return xmlText.substring(index);

	}

	/**
	 * Inserts a node in the XML along with value in the specified index
	 * 
	 * @param xmlText
	 * @param path
	 * @param node
	 * @param value
	 * @param index
	 * @return
	 */
	public static StringBuffer insertNodeAtIndex(StringBuffer xmlText, String node, String value, int index) {
		String startNode = "<" + node + ">";
		String endNode = "</" + node + ">";
		value = convertSpecialChar(value);
		String formattedNode = startNode + value + endNode + newLine;
		if (index == -1) {
			xmlText = xmlText.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			xmlText = xmlText.append(formattedNode);
		} else
			xmlText = xmlText.insert(index, formattedNode);

		return xmlText;
	}

	/**
	 * Handles if value contains following special charecters &amp; -> & &apos; -> ' &quot; -> " &lt; -> < &gt; -> >
	 * 
	 * @param value
	 * @return
	 */
	public static String convertSpecialChar(String value) {
		if (value.indexOf("&") != -1) {
			value = value.replaceAll("&", "&amp;");
		}
		if (value.indexOf("'") != -1) {
			value = value.replaceAll("'", "&apos;");
		}
		if (value.indexOf("\"") != -1) {
			value = value.replaceAll("\"", "&quot;");
		}
		if (value.indexOf("<") != -1) {
			value = value.replaceAll("<", "&lt;");
		}
		if (value.indexOf(">") != -1) {
			value = value.replaceAll(">", "&gt;");
		}

		return value;
	}

	/**
	 * Returns the index after the closing tag of the last element in the Path recognised by the tokenizer
	 * 
	 * @param xmlText
	 * @param path
	 * @return
	 */
	public static int getIndexToPath(String xmlText, String path) {
		int index = 0;
		int subindex = -1;
		String nodeName = null;
		StringTokenizer token = new StringTokenizer(path, "/");
		while (token.hasMoreElements()) {
			nodeName = token.nextToken();
			subindex = getIndexToNode(xmlText, nodeName);

			if (subindex == -1)
				return subindex;
			else
				index += subindex; // increasing the index for every pass, to get accurate index
			xmlText = getXMLContainedInNode(xmlText, nodeName);
		}
		return index;
	}

	/**
	 * Gets the index to insert, it tries to get the index before the closing element to insert, it tries to maintain the order of
	 * insert first cum first possition
	 * 
	 * @param xmlText
	 * @param path
	 * @return
	 */
	public static int getInsertIndexToPath(String xmlText, String path) {
		int index = 0;
		int subindex = -1;
		String nodeName = null;
		StringTokenizer token = new StringTokenizer(path, "/");
		while (token.hasMoreElements()) {
			nodeName = token.nextToken();
			subindex = getIndexToNode(xmlText, nodeName);
			if (subindex == -1)
				return subindex;
			else
				index += subindex; // increasing the index for every pass, to get accurate index
			xmlText = getXMLContainedInNode(xmlText, nodeName);
		}
		if (xmlText.length() > 0) {
			index += xmlText.length();
		}
		return index;
	}

	/**
	 * Returns an index inserting duplicate nodes in the same path
	 * 
	 * @param xmlText
	 * @param path
	 * @return
	 */
	public static int getIndexForDuplicateNodesEntry(String xmlText, String path) {
		int index = 0;
		int subindex = -1;
		String nodeName = null;
		StringTokenizer token = new StringTokenizer(path, "/");
		StringBuffer tempPath = new StringBuffer();
		String endNode = null;
		while (token.hasMoreElements()) {
			nodeName = token.nextToken();
			tempPath.append(nodeName);
			// Check if paths matched (if Duplicate), get the lastindex of endNode
			// 
			if (path.equals(tempPath.toString())) {
				endNode = "</" + nodeName + ">";
				index += xmlText.lastIndexOf(endNode);
				index += endNode.length();
				break; // breaking after finding last index of the duplicate node
			} else {
				subindex = getIndexToNode(xmlText, nodeName);
				if (subindex == -1)
					return subindex;
				else
					index += subindex; // increasing the index for every pass, to get accurate index
				xmlText = getXMLContainedInNode(xmlText, nodeName);
			}
			tempPath.append("/");
		}
		return index;
	}

	public static String getXmlProperty(String xmlMetadata, String propertyKey) {
		return CeNSystemXmlProperties.getProperty(xmlMetadata, propertyKey);
	}
	
	public static String getXmlPropertyAsXml(String xmlMetadata, String propertyKey) {
		return CeNSystemXmlProperties.getPropertyAsXml(xmlMetadata, propertyKey);
	}
	
	public static String updateXmlProperty(String xmlMetadata, String propertyKey, String value) {
		return CeNSystemXmlProperties.updateProperty(xmlMetadata, propertyKey, value);
	}
}
