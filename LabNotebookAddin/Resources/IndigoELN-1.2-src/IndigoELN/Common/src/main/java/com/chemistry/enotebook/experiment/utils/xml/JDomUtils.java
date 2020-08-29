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
package com.chemistry.enotebook.experiment.utils.xml;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 *
 * 
 * Purpose: To allow the traversal, retreieval of data as well as update and creation of JDom XML documents with XPath statements.
 * 
 * Warning: Currently handles String values for preferences only.
 */
public class JDomUtils {
	public static String getPrefFromDoc(Document doc, String xpathVal) throws JDomUtilException {
		String result = null;

		if (xpathVal.indexOf("@") > 0) {
			try {
				String[] args = xpathVal.split("@");

				if (args.length == 2) {
					result = JDomUtils.getAttributeValue(doc, args[0], args[1]);
				} else {
					result = "-- Failed Access --";
				}
			} catch (JDOMException e) {
				result = "XPath: " + xpathVal + " failed lookup";
				throw new JDomUtilException(result, e);
			}
		} else {
			try {
				result = JDomUtils.getElementValue(doc, xpathVal);
			} catch (JDOMException e) {
				result = "XPath: " + xpathVal + " failed lookup";
				throw new JDomUtilException(result, e);
			}
		}

		return result;
	}

	public static void setPrefInDoc(Document doc, String xpathVal, String val) throws JDomUtilException {
		if (xpathVal.indexOf("@") < 0) {
			try {
				JDomUtils.setElementValue(doc, xpathVal, val);
			} catch (JDOMException e) {
				throw new JDomUtilException("XPath: " + xpathVal + " failed to set element value.", e);
			}
		} else {
			try {
				JDomUtils.setAttributeValue(doc, xpathVal, val);
			} catch (JDOMException e) {
				throw new JDomUtilException("XPath: " + xpathVal + "\nfailed to set attribute value.", e);
			}
		}
	}

	/**
	 * 
	 * @param doc =
	 *            org.jdom.Document
	 * @param xpathToElement =
	 *            fully deliniated path. Always traverses from Document root.
	 * @return String value at element
	 * @throws JDOMException
	 *             when xpath is inaccurate. Getter will not create a path.
	 */
	public static String getElementValue(Document doc, String xpathToElement) throws JDOMException {
		if (doc != null) {
			XPath query = XPath.newInstance(xpathToElement);
			Element elem = (Element) query.selectSingleNode(doc);

			if (elem != null)
				if (elem.getChildren().size() > 0) {
					try {
						Format fmt = Format.getPrettyFormat();
						fmt.setOmitDeclaration(true);

						XMLOutputter outputter = new XMLOutputter(fmt);
						ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
						outputter.output(elem, out);
						return out.toString();
					} catch (Exception ioe) {
						throw new JDOMException("Failed to convert element to XML");
					}
				} else
					return elem.getText();
		}

		return null;
	}

	/**
	 * 
	 * @param doc =
	 *            org.jdom.Document
	 * @param xpathToElement =
	 *            fully deliniated path. Always traverses from Document root.
	 * @param val -
	 *            string representation of value to place in element
	 * @throws JDOMException
	 */
	public static void setElementValue(Document doc, String xpathToElement, String val) throws JDOMException {
		if (doc != null) {
			// Try the quick way
			XPath query = XPath.newInstance(xpathToElement);
			Element element = (Element) query.selectSingleNode(doc);

			if (element == null) { // need to create the path
				element = getElementAt(doc, xpathToElement);
			}

			element.setText(val);
		}
	}

	/**
	 * 
	 * @param doc =
	 *            org.jdom.Document
	 * @param xpathToElement =
	 *            fully deliniated path. Always traverses from Document root.
	 * @return List of child elements
	 * @throws JDOMException
	 *             when xpath is inaccurate. Getter will not create a path.
	 */
	public static List getChildNodes(Document doc, String xpathToElement) throws JDOMException {
		List childNodes = new ArrayList();
		if (doc != null) {
			XPath query = XPath.newInstance(xpathToElement);
			List elements = XPath.selectNodes(doc, xpathToElement);
			Iterator iter = elements.iterator();
			while (iter.hasNext()) {
				Element elem = (Element) iter.next();
				if (elem != null) {
					if (elem.getChildren().size() > 0) {
						try {
							
//							Format fmt = Format.getPrettyFormat();
//							fmt.setOmitDeclaration(true);
//
//							XMLOutputter outputter = new XMLOutputter(fmt);
//							ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
//							outputter.output(elem, out);
							childNodes.add(elem);
						} catch (Exception ioe) {
							throw new JDOMException("Failed to convert element to XML");
						}
					} else
						childNodes.add(elem);
				}
			}
		}
		return childNodes;
	}


	/**
	 * 
	 * @param doc =
	 *            org.jdom.Document
	 * @param xpathToAttribute -
	 *            fully qualified XPath statement to attribute. Always traverses from doc root.
	 * @return String value of attribute if it exists
	 * @throws JDOMException -
	 *             thrown when xpath is invalid. Getter will not create path.
	 */
	public static String getAttributeValue(Document doc, String xpathToAttribute) throws JDOMException {
		String[] result = xpathToAttribute.split("@");

		if (result.length != 2) {
			throw new JDOMException("Path passed in has no attribute, or has too many: " + xpathToAttribute);
		}

		return getAttributeValue(doc, result[0], result[1]);
	}

	/**
	 * 
	 * @param doc =
	 *            org.jdom.Document
	 * @param xpathToElement -
	 *            fully qualified XPath statement to element containing attribute of interest. Always traverses from doc root.
	 * @param attributeName -
	 *            name of attribute whose value is desired
	 * @return String value of attribute if it exists
	 * @throws JDOMException -
	 *             thrown when xpath is invalid. Getter will not create path.
	 */
	public static String getAttributeValue(Document doc, String xpathToElement, String attributeName) throws JDOMException {
		if (doc != null) {
			Element element = (Element) XPath.selectSingleNode(doc.getRootElement(), xpathToElement);

			return element.getAttributeValue(attributeName);
		}

		return null;
	}

	/**
	 * 
	 * @param doc =
	 *            org.jdom.Document
	 * @param xpathToAttribute -
	 *            fully qualified XPath statement to attribute. Always traverses from doc root.
	 * @param val -
	 *            string representation of value to place in attribute
	 * @throws JDOMException
	 */
	public static void setAttributeValue(Document doc, String xpathToAttribute, String val) throws JDOMException {
		String[] result = xpathToAttribute.split("@");

		if (result.length != 2) {
			throw new JDOMException("Path passed in has no attribute, or has too many: " + xpathToAttribute);
		}

		setAttributeValue(doc, result[0], result[1], val);
	}

	/**
	 * 
	 * @param doc =
	 *            org.jdom.Document
	 * @param xpathToElement -
	 *            fully qualified XPath statement to element containing attribute of interest. Always traverses from doc root.
	 * @param attributeName -
	 *            name of attribute whose value is desired
	 * @param val -
	 *            string representation of value to place in attribute
	 * @throws JDOMException
	 */
	public static void setAttributeValue(Document doc, String xpathToElement, String attributeName, String val)
			throws JDOMException {
		if (doc != null) {
			Element element = (Element) XPath.selectSingleNode(doc, xpathToElement);

			if (element == null) { // need to create the path
				element = getElementAt(doc, xpathToElement);
			}

			element.setAttribute(attributeName, val);
		}
	}

	/**
	 * 
	 * @param doc =
	 *            org.jdom.Document
	 * @param xpath -
	 *            fully qualified XPath statement to element. Always traverses from doc root.
	 * @return element either created or found.
	 */
	public static Element getElementAt(Document doc, String xpath) {
		String[] result = xpath.split("/");

		// Check for a root element
		Element elem = doc.getRootElement();

		if (elem == null) // create root element
		{
			if (xpath.indexOf("/") == 0) {
				elem = new Element(result[1]);
			} else {
				elem = new Element(result[0]);
			}

			doc.setRootElement(elem);
		}

		int start = 2;

		if (xpath.indexOf("/") > 0) {
			start = 1;
		}

		for (int i = start; i < result.length; i++) {
			if (elem.getChild(result[i]) == null) { // create child
				elem.addContent(new Element(result[i]));
			}

			elem = elem.getChild(result[i]);
		}

		return elem;
	}

	/**
	 * Use only if there is one instance of the target you are going after. Otherwise look into using findElement(Element elem,
	 * String targetName)
	 * 
	 * @param doc -
	 *            the xml org.wc3.dom.Document
	 * @param targetName -
	 *            Doesn't require XPath just the final target if there is only one.
	 * @return Element if one is found or null if not.
	 */
	public static Element findElement(Document doc, String targetName) {
		Element result = null;
		// Check for a root element
		Element root = doc.getRootElement();

		if (root != null) // search for node
			result = findSubElement(targetName, root);
		return result;
	}

	/**
	 * Use to recurse down one segment of a tree
	 * 
	 * @param doc -
	 *            the xml org.wc3.dom.Document
	 * @param targetName -
	 *            Doesn't require XPath just the final target if there is only one.
	 * @return Element if one is found or null if not.
	 */
	public static Element findElement(Element elem, String targetName) {
		Element result = null;
		if (elem != null) // search for node
			result = findSubElement(targetName, elem);
		return result;
	}

	// 
	// The following is an extract from
	// http://java.sun.com/j2ee/1.4/docs/tutorial/doc/JAXPDOM7.html#wp75112
	//
	/**
	 * Find the named subnode in a node's sublist.
	 * <li>Ignores comments and processing instructions.
	 * <li>Ignores TEXT nodes (likely to exist and contain ignorable whitespace, if not validating.
	 * <li>Ignores CDATA nodes and EntityRef nodes.
	 * <li>Examines element nodes to find one with the specified name.
	 * </ul>
	 * 
	 * @param name
	 *            the tag name for the element to find
	 * @param node
	 *            the element node to start searching from
	 * @return the Node found
	 */
	public static Element findSubElement(String name, Element element) {
		if (element == null) {
			return null;
		}

		List list = element.getChildren();

		for (int i = 0; i < list.size(); i++) {
			Element subElement = (Element) list.get(i);

			if (subElement.getName().equalsIgnoreCase(name)) {
				return subElement;
			} else if (subElement.getChildren().size() > 0) {
				Element result = findSubElement(name, subElement);
				if (result != null)
					return result;
			}
		}

		return null;
	}

	public static String getText(Element elem, String target) {
		return JDomUtils.getText(JDomUtils.findElement(elem, target));
	}

	public static String getText(Document doc, String element) {
		return JDomUtils.getText(JDomUtils.findElement(doc, element));
	}

	// 
	// ibid: http://java.sun.com/j2ee/1.4/docs/tutorial/doc/JAXPDOM7.html#wp75112
	//
	/**
	 * Return the text that a node contains. This routine:
	 * <ul>
	 * <li>Ignores comments and processing instructions.
	 * <li>Concatenates TEXT nodes, CDATA nodes, and the results of recursively processing EntityRef nodes.
	 * <li>Ignores any element nodes in the sublist. (Other possible options are to recurse into element sublists or throw an
	 * exception.)
	 * </ul>
	 * 
	 * @param node
	 *            a DOM node
	 * @return a String representing its contents
	 */
	public static String getText(Element element) {
		if (element == null) {
			return "";
		}
		return element.getText();
	}

	public static Document getDocFromString(String xmlString) throws IOException, JDOMException {
		if (xmlString != null && xmlString.length() > 0) {
			StringReader reader = new StringReader(xmlString);
			SAXBuilder builder = new SAXBuilder();
			return builder.build(reader);
		} else {
			throw new JDOMException("JDomUtils::getDocFromString: Empty xml String.");
		}
	}

	/**
	 * returns the List of child attribute values
	 * 
	 * @param doc
	 * @param xPathVal
	 * @return
	 * @throws JDOMException
	 */
	public static List getAttributeValues(Document doc, String childElementName, String xPathVal) throws JDOMException {
		XPath x = XPath.newInstance(xPathVal);
		Element element = (Element) x.selectSingleNode(doc);
		if (element == null) { // need to create the path
			element = getElementAt(doc, xPathVal);
		}
		List children = element.getChildren();
		ArrayList a_return = new ArrayList();
		if (children != null) {
			Iterator i = children.iterator();
			while (i.hasNext()) {
				Element e = (Element) i.next();
				if (e.getName().equals(childElementName)) {
					a_return.add(e.getValue());
				}
			}
		}
		return a_return;
	}

	/**
	 * 
	 * @param doc =
	 *            org.jdom.Document
	 * @param xpathToElement -
	 *            fully qualified XPath statement to element containing attribute of interest. Always traverses from doc root.
	 * @param attributeName -
	 *            name of attribute whose value is desired
	 * @param val -
	 *            string representation of value to place in attribute
	 * @throws JDOMException
	 */
	public static void setAttributeValues(Document doc, String xpathToElement, String childElementName, List val)
			throws JDOMException {
		if (doc != null) {
			XPath query = XPath.newInstance(xpathToElement);
			Element element = (Element) XPath.selectSingleNode(doc, xpathToElement);
			if (element == null) { // need to create the path
				element = getElementAt(doc, xpathToElement);
			}
			element.setText("");// Clear the OLD Values;
			Iterator i = val.iterator();
			ArrayList a = new ArrayList();
			while (i.hasNext()) {
				Element a1 = new Element(childElementName);
				a1.setText((String) i.next());
				a.add(a1);
			}
			element.addContent(a);
		}

	}// end method

	/**
	 * This function returns a single element and it text content as xml string
	 * 
	 * @param doc =
	 *            org.jdom.Document
	 * @param xpathToElement =
	 *            fully deliniated path. Always traverses from Document root.
	 * @return String value at element
	 * @throws JDOMException
	 *             when xpath is inaccurate. Getter will not create a path.
	 */
	public static String getSingleElementAsXMLString(Document doc, String xpathToElement) throws JDOMException {
		if (doc != null) {
			XPath query = XPath.newInstance(xpathToElement);
			Element elem = (Element) query.selectSingleNode(doc);

			if (elem != null)
				try {
					Format fmt = Format.getPrettyFormat();
					fmt.setOmitDeclaration(true);

					XMLOutputter outputter = new XMLOutputter(fmt);
					ByteArrayOutputStream out = new ByteArrayOutputStream(500);
					outputter.output(elem, out);
					return out.toString();
				} catch (Exception ioe) {
					throw new JDOMException("Failed to convert element to XML");
				} // end try
		} // end if

		return null;
	}

}
