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
 * Created on 28-May-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.chemistry.enotebook.experiment.utils.xml;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * 
 * 
 * To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 * Comments
 */
public class XMLConstruct {

	public DocumentBuilderFactory DOMfactory;

	public XMLConstruct() {
		DOMfactory = DocumentBuilderFactory.newInstance();
	}

	public Document getDocumentfromXML(String XMLInputString) throws ParserConfigurationException, IOException, SAXException {
		Document document = null;

		DocumentBuilder builder = DOMfactory.newDocumentBuilder();
		document = builder.parse(new InputSource(new StringReader(XMLInputString)));

		return document;
	}
}
