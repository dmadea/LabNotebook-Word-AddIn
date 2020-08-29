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
 * Created on 25-Oct-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.storage.ejb;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class AuditLog {
	private Document auditLogDoc = null;
	private ArrayList nodeList = new ArrayList();
	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	// private CachedXPathAPI cachedXPath = null;
	private XPathAPI cachedXPath;

	public AuditLog() throws Exception {
		try {
			// Create a builder factory
			this.factory = DocumentBuilderFactory.newInstance();
			this.factory.setValidating(false);
			// cachedXPath = new CachedXPathAPI();
			this.auditLogDoc = this.factory.newDocumentBuilder().newDocument();
		} catch (Exception e) {
			throw new Exception("Error creating the audit log XML", e);
		}

	}

	public void addLogEntry(String strType, String strUser, String strPropertyName, String strNewValue, String strOldValue) {
		Node entryNode = this.auditLogDoc.createElement("Entry");
		this.nodeList.add(entryNode);
		entryNode.appendChild(this.newElement("Reason", "Record " + strType));
		entryNode.appendChild(this.newElement("TimeStamp", DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
				.format(Calendar.getInstance().getTime())));
		entryNode.appendChild(this.newElement("Username", strUser));
		if (!strPropertyName.equals("")) {
			entryNode.appendChild(this.newElement("PropertyName", strPropertyName));
			entryNode.appendChild(this.newElement("PreviousValue", strOldValue));
			entryNode.appendChild(this.newElement("NewValue", strNewValue));
		}
	}

	private Node newElement(String strName, String strValue) {
		Node newNode = this.auditLogDoc.createElement(strName);
		newNode.appendChild(this.auditLogDoc.createTextNode(strValue));
		return newNode;
	}

	public String toXMLString() throws Exception {
		try {
			// Serialize DOM
			OutputFormat format = new OutputFormat(this.auditLogDoc, "UTF-8", true);
			// as a String
			StringWriter stringOut = new StringWriter();
			XMLSerializer serial = new XMLSerializer(stringOut, format);
			serial.serialize(this.auditLogDoc.getDocumentElement());
			return stringOut.toString();
		} catch (IOException ioe) {
			throw new Exception("Unable to create Audit XML", ioe);
		}
	}

	public void setCurrentLogXML(String strXML) throws Exception {
		Document oldLogDoc;
		try {
			// Create the builder and parse the file
			if (!strXML.equals("")) {
				oldLogDoc = this.factory.newDocumentBuilder().parse(new ByteArrayInputStream(strXML.getBytes()));
			} else {
				oldLogDoc = this.factory.newDocumentBuilder().parse(
						new ByteArrayInputStream("<Audit_Log><Log></Log></Audit_Log>".getBytes()));
			}
			Node rootNode = oldLogDoc.getDocumentElement();
			// set modified date
			Node dateNode = oldLogDoc.createElement("Last_Modified");
			dateNode.appendChild(oldLogDoc.createTextNode(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
					.format(Calendar.getInstance().getTime())));

			NodeList nodes = XPathAPI.selectNodeList(oldLogDoc, "Audit_Log/Last_Modified");

			if (nodes.getLength() == 0) {
				rootNode.appendChild(dateNode);
			} else {
				rootNode.replaceChild(dateNode, nodes.item(0));
			}
			nodes = XPathAPI.selectNodeList(oldLogDoc, "Audit_Log/Log");
			if (nodes.getLength() == 0) {
				rootNode.appendChild(oldLogDoc.createElement("Log"));
			}
			// add the new nodes into the existing document
			this.mergeNodeList(rootNode);

		} catch (Exception e) {
			// A parsing error occurred; the xml input is not valid
			throw new Exception("Error creating the audit log XML", e);

		}
	}

	private void mergeNodeList(Node rootNode) throws Exception {
		Iterator nodeIterator = this.nodeList.iterator();

		Node logNode = this.auditLogDoc.importNode(rootNode, true);
		this.auditLogDoc.appendChild(logNode);
		logNode = XPathAPI.selectNodeList(this.auditLogDoc, "Audit_Log/Log").item(0);
		while (nodeIterator.hasNext()) {
			logNode.appendChild((Node) nodeIterator.next());
		}

	}
}
