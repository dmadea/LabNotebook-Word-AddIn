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
 * Created on Jan 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.client.gui.page.stoichiometry.search.dbsetup;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * Given a String of XML Data, this class generates a JTree representing the XML
 * structure contained in the file or stream. Parses with DOM then copies the
 * tree structure (minus text and comment nodes).
 */
public class DBListTree extends JTree {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5069844109407030305L;

	public DBListTree(String xmlString) {
		super(makeRootNode(xmlString));
	}

	// This method needs to be static so that it can be called
	// from the call to the parent constructor (super), which
	// occurs before the object is really built.
	private static CheckNode makeRootNode(String xmlString) {
		try {
			// Use JAXP's DocumentBuilderFactory so that there
			// is no code here that is dependent on a particular
			// DOM parser. Use the system property
			// javax.xml.parsers.DocumentBuilderFactory (set either
			// from Java code or by using the -D option to "java").
			// or jre_dir/lib/jaxp.properties to specify this.
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			// Standard DOM code from hereon. The "parse"
			// method invokes the parser and returns a fully parsed
			// Document object. We'll then recursively descend the
			// tree and copy non-text nodes into JTree nodes.
			Document document = builder.parse(new InputSource(new StringReader(xmlString)));
			document.getDocumentElement().normalize();
			Element rootElement = document.getDocumentElement();
			CheckNode rootTreeNode = buildTree(rootElement);
			return (rootTreeNode);
		} catch (Exception e) {
			String errorMessage = "Error making root node: " + e;
			return (new CheckNode(errorMessage));
		}
	}

	private static CheckNode buildTree(Element rootElement) {
		// Make a JTree node for the root, then make JTree
		// nodes for each child and add them to the root node.
		// The addChildren method is recursive.
		CheckNode rootTreeNode = new CheckNode(rootElement);
		addChildren(rootTreeNode, rootElement);
		CheckNode node  = new CheckNode("CEN db");
		rootTreeNode.add(node);		
		CheckNode childNode = new CheckNode("Reactions/Structures");
		node.add(childNode);
		return (rootTreeNode);
	}

	private static void addChildren(CheckNode parentTreeNode,
			Node parentXMLElement) {
		// Recursive method that finds all the child elements
		// and adds them to the parent node. We have two types
		// of nodes here: the ones corresponding to the actual
		// XML structure and the entries of the graphical JTree.
		// The convention is that nodes corresponding to the
		// graphical JTree will have the word "tree" in the
		// variable name. Thus, "childElement" is the child XML
		// element whereas "childTreeNode" is the JTree element.
		// This method just copies the non-text and non-comment
		// nodes from the XML structure to the JTree structure.
		NodeList childElements = parentXMLElement.getChildNodes();
		for (int i = 0; i < childElements.getLength(); i++) {
			Node childElement = childElements.item(i);
			if (!(childElement instanceof Text || childElement instanceof Comment)) {
				CheckNode childTreeNode = new CheckNode(
						childElement);
				parentTreeNode.add(childTreeNode);
				addChildren(childTreeNode, childElement);
			}
		}
	}

	
}
