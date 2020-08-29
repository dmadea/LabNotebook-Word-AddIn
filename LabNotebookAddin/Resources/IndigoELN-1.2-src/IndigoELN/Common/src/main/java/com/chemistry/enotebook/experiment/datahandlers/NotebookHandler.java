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
 * Created on 26-Aug-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.datahandlers;

import com.chemistry.enotebook.experiment.common.ObservableObject;
import com.chemistry.enotebook.experiment.datahandlers.utils.HTMLEncoder;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.RowSet;
import java.beans.PropertyDescriptor;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class NotebookHandler {
	public static final String XML_METADATA = "XML_METADATA";
	public static final String AUDIT_LOG = "AUDIT_LOG";
	public static final String PROPERTY_NOT_FOUND = "NOT_FOUND";
	public static final int ORACLE_CLOB_TYPE = 2005;
	public static final int ORACLE_BLOB_TYPE = 2004;
	public static final int ORACLE_TIMESTAMPZ_TYPE = -101;

	public String metaDataRoot;
	public HashMap propertyMappings = new HashMap();
	public ArrayList propertyExclusions = new ArrayList();
	private int recursionCount = 0;
	private String previousProperty = "";

	public synchronized boolean getRowsetData(ResultSet rs, Object notebookObj, boolean blnLoading) throws SQLException, Exception {
		ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			if (!rsmd.getColumnName(i).startsWith(XML_METADATA) && !rsmd.getColumnName(i).equals(AUDIT_LOG)) {
				String strFieldName = rsmd.getColumnName(i);
				Object objFieldValue = null;

				if (rs.getObject(i) != null) {
					if (rsmd.getColumnType(i) == ORACLE_BLOB_TYPE) {
						Object tempObj = rs.getObject(i);
						if (tempObj.getClass().getName().equals("[B")) {
							objFieldValue = (byte[]) tempObj;
						} else {
							Blob blobObj = (Blob) tempObj;
							long startPos = 0;
							int length = (int) blobObj.length();
							objFieldValue = blobObj.getBytes(startPos, length);
						}
					} else if (rsmd.getColumnType(i) == ORACLE_CLOB_TYPE) {
						Object tempObj = rs.getObject(i);
						// BufferedInputStream iStream = null;
						if (tempObj.getClass().getName().equals("java.lang.String")) {
							objFieldValue = (String) tempObj;
						} else {
							Clob clobObj = (Clob) tempObj;

							// long startPos = 0;
							int length = (int) clobObj.length();
							if (length > 0) {
								Reader rdr = clobObj.getCharacterStream();
								char[] chars = new char[length];
								for (int k = 0; k < length; k++) {
									rdr.read(chars);
								}
								objFieldValue = new String(chars);
							}
						}
						// need to check the string for any
						// encoded image html tags and extract them
						if (objFieldValue != null && !objFieldValue.equals("")) {
							try {
								objFieldValue = HTMLEncoder.decodeHTML((String) objFieldValue);
							} catch (Exception e) {
								// Just use what is there, user can fix if necessary
								// How do we report a problem though??
								objFieldValue = objFieldValue.toString();
								// //System.out.println("HTML Encode Error - " + e.getMessage());
							}
						}
					} else {
						objFieldValue = rs.getObject(i).toString();
					}
				}
				if (objFieldValue != null) {
					String mappedPathName = "";
					if (propertyMappings.containsKey(strFieldName)) {
						mappedPathName = (String) propertyMappings.get(strFieldName);
						StringTokenizer strToken = new StringTokenizer(mappedPathName, "/");
						strFieldName = (String) strToken.nextElement();
						mappedPathName = mappedPathName.substring(mappedPathName.indexOf("/") + 1);
					}
					populateBean(null, strFieldName, mappedPathName, objFieldValue, notebookObj, null, blnLoading);
				}
			}
		}
		return true;
	}

	public synchronized ArrayList updateRowsetData(RowSet rs, Object notebookObj, boolean blnInsert) throws SQLException, Exception {
		Object objFieldValue = null;
		ArrayList dbFields = new ArrayList();

		ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			if (!rsmd.getColumnName(i).equals(XML_METADATA) && !rsmd.getColumnName(i).equals(AUDIT_LOG)) {
				String strFieldName = rsmd.getColumnName(i);

				String mappedPathName = "";
				if (propertyMappings.containsKey(strFieldName)) {
					mappedPathName = (String) propertyMappings.get(strFieldName);
					StringTokenizer strToken = new StringTokenizer(mappedPathName, "/");
					strFieldName = (String) strToken.nextElement();
					if (strToken.hasMoreTokens()) {
						mappedPathName = mappedPathName.substring(mappedPathName.indexOf("/") + 1);
					} else {
						mappedPathName = "";
					}
				}
				String strProperty = fieldNameToProperty(strFieldName);
				objFieldValue = getValueFromBean(strProperty, mappedPathName, notebookObj);

				dbFields.add(strProperty);
				if (objFieldValue != null && !propertyExclusions.contains(strProperty)) {
					if (!objFieldValue.equals(PROPERTY_NOT_FOUND)) {
						// check for embedded images in Procedure CLOB
						if (rsmd.getColumnType(i) == ORACLE_CLOB_TYPE && ((String) objFieldValue).length() > 0) {
							objFieldValue = HTMLEncoder.encodeHTML((String) objFieldValue);
						}
						rs.updateObject(i, objFieldValue);
					} else if (blnInsert) {
						rs.updateNull(i);
					}
				} else {
					rs.updateNull(i);
				}
			} else if (blnInsert) {
				rs.updateObject(i, "");
			}
		}
		if (blnInsert) {
			rs.insertRow();
		} else {
			rs.updateRow();
		}
		return dbFields;

	}

	public synchronized void getDataFromXML(Object notebookObject, XMLDataHandler xmlHelper, Node node, String nodePath,
			boolean blnLoading) throws Exception {
		NodeList nodes;
		try {
			if (node == null) {
				nodes = xmlHelper.getChildNodes(nodePath);
			} else {
				nodes = xmlHelper.getChildNodes(node);
			}
			if (nodes.getLength() > 0) {
				for (int i = 0; i < nodes.getLength(); i++) {
					if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
						nodePath = xmlHelper.getAbsolutePath((Element) nodes.item(i));
					}
					getDataFromXML(notebookObject, xmlHelper, nodes.item(i), nodePath, blnLoading);
				}
			} else {
				String nodeValue = null;
				nodeValue = node.getNodeValue();
				if (nodeValue != null && !nodeValue.trim().equals("")) {
					// get the highest parent_level to find the true property name
					StringTokenizer strToken = new StringTokenizer(nodePath, "/");

					int intCount = 0;
					String parentNodeName = "";
					while (strToken.hasMoreTokens()) {
						// loop until we get to the metadata tag
						String tempNodeName = (String) strToken.nextElement();
						intCount++;
						if (intCount == 2 && !tempNodeName.equals("Meta_Data")) {
							parentNodeName = tempNodeName;
							break;
						} else if (intCount > 2) {
							parentNodeName = tempNodeName;
							break;
						}
					}

					// get the remainder of the nodepath
					nodePath = "";
					while (strToken.hasMoreElements()) {
						nodePath += (String) strToken.nextElement();
						if (strToken.hasMoreElements()) {
							nodePath += "/";
						}
					}

					String mappedNodeName = parentNodeName;
					if (propertyMappings.containsKey(parentNodeName))
						mappedNodeName = (String) propertyMappings.get(parentNodeName);
					populateBean(node.getParentNode(), mappedNodeName, nodePath, nodeValue, notebookObject, xmlHelper, blnLoading);
				}
			}
		} catch (Exception e) {
			throw new Exception("Error iterating through the XML data to populate object model", e);
		}
	}

	private void loadArrayList(Node node, ArrayList arrayList, String strFieldName, String strPathName, Object objFieldValue,
			XMLDataHandler xmlHelper, boolean blnLoading) throws Exception {
		Object arrayListObject = null;
		boolean primitive = false;
		try {
			if (strFieldName.equals("Entry") && strPathName.equals("Entry")) {
				primitive = true;
			} else {
				// check the class of this property
				String classType = xmlHelper.getAttributeValue(node, "class");
				if (classType != null && !classType.equals("")) {
					String strIndex = xmlHelper.getAttributeValue(node, "index");
					if (!strIndex.equals("")) {
						int intIndex = Integer.parseInt(strIndex);
						if (arrayList.size() <= intIndex) {
							Class c = Class.forName(classType);
							arrayListObject = c.newInstance();
							arrayList.add(arrayListObject);
						}
					}
				}
			}
			if (!primitive && arrayListObject == null && arrayList.size() > 0) {
				arrayListObject = arrayList.get(arrayList.size() - 1);
			}
			if (arrayListObject != null) {
				StringTokenizer strToken = new StringTokenizer(strPathName, "/");
				strFieldName = (String) strToken.nextElement();
				if (propertyMappings.containsKey(strFieldName)) {
					strPathName = (String) propertyMappings.get(strFieldName);
				}
				strPathName = strPathName.substring(strPathName.indexOf("/") + 1);
				populateBean(node, strFieldName, strPathName, objFieldValue, arrayListObject, xmlHelper, blnLoading);
			} else {
				arrayList.add(objFieldValue);
			}

		} catch (ClassNotFoundException cnfe) {
			throw new Exception("Could not find a class detailed in the XMLData", cnfe);
		} catch (IllegalAccessException iae) {
			throw new Exception("Could not create a class detailed in the XMLData", iae);
		} catch (Exception e) {
			throw new Exception("Could not find a class definition in the XMLData", e);
		}
	}

	private void populateBean(Node node, String strFieldName, String pathName, Object objFieldValue, Object dataObject,
			XMLDataHandler xmlHelper, boolean blnLoading) throws Exception {
		// Need to set flag to indicate the object is loading

		if (dataObject instanceof ObservableObject) {
			((ObservableObject) dataObject).setLoading(blnLoading);
		}
		PropertyDescriptor pd = null;
		try {
			String strProperty = fieldNameToProperty(strFieldName);
			pd = PropertyUtils.getPropertyDescriptor(dataObject, strProperty);
			if (pd != null) {
				Object[] params = new Object[1];
				if (pd.getPropertyType() == String.class) {
					params[0] = objFieldValue;
					pd.getWriteMethod().invoke(dataObject, params);
				} else if (pd.getPropertyType() == int.class) {
					params[0] = new Integer((String) objFieldValue);
					pd.getWriteMethod().invoke(dataObject, params);
				} else if (pd.getPropertyType() == double.class) {
					params[0] = new Double((String) objFieldValue);
					pd.getWriteMethod().invoke(dataObject, params);
				} else if (pd.getPropertyType() == long.class) {
					params[0] = new Long((String) objFieldValue);
					pd.getWriteMethod().invoke(dataObject, params);
				} else if (pd.getPropertyType() == boolean.class) {
					params[0] = new Boolean((String) objFieldValue);
					pd.getWriteMethod().invoke(dataObject, params);
				} else if (pd.getPropertyType() == byte[].class) {
					params[0] = (byte[]) objFieldValue; // .getBytes();
					pd.getWriteMethod().invoke(dataObject, params);
				} else if (pd.getPropertyType() == Date.class) {
					params[0] = NotebookPageUtil.getLocalDate((String) objFieldValue);
					pd.getWriteMethod().invoke(dataObject, params);
				} else {
					Object obj = pd.getReadMethod().invoke(dataObject, null);
					if (obj == null) {
						obj = pd.getPropertyType().newInstance();
						params[0] = obj;
						pd.getWriteMethod().invoke(dataObject, params);
					}
					// there are nested objects so get the next item in the path
					StringTokenizer strToken = new StringTokenizer(pathName, "/");
					strFieldName = (String) strToken.nextElement();
					pathName = pathName.substring(pathName.indexOf("/") + 1);
					if (propertyMappings.containsKey(strFieldName)) {
						pathName = (String) propertyMappings.get(strFieldName);
						strToken = new StringTokenizer(pathName, "/");
						strFieldName = (String) strToken.nextElement();
						pathName = pathName.substring(pathName.indexOf("/") + 1);
					}

					if (obj instanceof ArrayList) {
						if (!strProperty.equals(previousProperty)) {
							((ArrayList) obj).clear();
							previousProperty = strProperty;
						}
						// get the current node's parent as this will match the current property
						if (node != null)
							node = node.getParentNode();
						loadArrayList(node, (ArrayList) obj, strFieldName, pathName, objFieldValue, xmlHelper, blnLoading);
					} else {
						populateBean(node, strFieldName, pathName, objFieldValue, obj, xmlHelper, blnLoading);
					}
				}
			}
		} catch (ClassNotFoundException cnfe) {
			throw new Exception("Could not create a class detailed in the XMLData", cnfe);
		} catch (NoSuchMethodException nsme) {
			throw new Exception("Setter not found for property: " + pd.getWriteMethod().getName(), nsme);
		} catch (InvocationTargetException ite) {
			throw new Exception("Exception invoking/getting setter: " + pd.getWriteMethod().getName(), ite);
		}
		if (dataObject instanceof ObservableObject) {
			((ObservableObject) dataObject).setLoading(false);
		}
	}

	private Object getValueFromBean(String strProperty, String pathName, Object dataObject) throws Exception {
		PropertyDescriptor pd = null;
		Object objPropertyValue = null;

		try {
			pd = PropertyUtils.getPropertyDescriptor(dataObject, strProperty);
			if (pd != null) {
				if (pd.getPropertyType() == String.class) {
					objPropertyValue = (String) pd.getReadMethod().invoke(dataObject, null);
				} else if (pd.getPropertyType() == int.class) {
					objPropertyValue = ((Integer) pd.getReadMethod().invoke(dataObject, null)).toString();
				} else if (pd.getPropertyType() == double.class) {
					objPropertyValue = ((Double) pd.getReadMethod().invoke(dataObject, null)).toString();
				} else if (pd.getPropertyType() == boolean.class) {
					objPropertyValue = ((Boolean) pd.getReadMethod().invoke(dataObject, null)).toString();
				} else if (pd.getPropertyType() == byte[].class) {
					Object obj = pd.getReadMethod().invoke(dataObject, null);
					if (obj != null) {
						objPropertyValue = (byte[]) obj;
					}
				} else if (pd.getPropertyType() == Date.class) {
					objPropertyValue = pd.getReadMethod().invoke(dataObject, null);
					if (objPropertyValue != null) {
						// objPropertyValue = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM,
						// SimpleDateFormat.MEDIUM).format((Date)objPropertyValue);
						SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy HH:mm:ss z");
						objPropertyValue = df.format((Date) objPropertyValue);
					}
				} else {
					Object obj = pd.getReadMethod().invoke(dataObject, null);
					if (obj != null) {
						if (!pathName.equals("")) {
							// there are nested objects so get the next item in the path
							StringTokenizer strToken = new StringTokenizer(pathName, "/");
							strProperty = (String) strToken.nextElement();
							pathName = pathName.substring(pathName.indexOf("/") + 1);
							objPropertyValue = getValueFromBean(fieldNameToProperty(strProperty), pathName, obj);
						} else
							objPropertyValue = obj.toString();
					}
				}

			} else {
				objPropertyValue = PROPERTY_NOT_FOUND;
			}
		} catch (NoSuchMethodException nsme) {
			throw new Exception("Setter not found for: " + strProperty, nsme);
		} catch (Exception ex) {
			throw new Exception("Exception getting/invoking setter: " + pd.getReadMethod(), ex);
		}
		return objPropertyValue;
	}

	// private PropertyDescriptor getPropertyDescriptor(String strName, Object dataObject)
	// throws Exception
	// {
	// String strPropertyName = "";
	//		
	// //use the field name minus any underscores to call getter and setters
	// StringTokenizer sToken = new StringTokenizer(strName,"_");
	// while (sToken.hasMoreElements()) {
	// String strTemp = ((String) sToken.nextElement()).toLowerCase();
	// strTemp = Character.toUpperCase(strTemp.charAt(0)) + strTemp.substring(1);
	// strPropertyName += strTemp;
	// }
	// char firstChar = Character.toLowerCase(strPropertyName.charAt(0));
	// strPropertyName = new Character(firstChar).toString() + strPropertyName.substring(1);
	//
	// return PropertyUtils.getPropertyDescriptor(dataObject, strPropertyName);
	//
	// }

	public synchronized void createXMLFromModel(Object notebookObj, XMLDataHandler xmlHelper, String rootNode, Node objectNode)
			throws Exception {
		createXMLFromModel(notebookObj, xmlHelper, rootNode, objectNode, null);
	}

	public synchronized void createXMLFromModel(Object notebookObj, XMLDataHandler xmlHelper, String rootNode, Node objectNode,
			ArrayList savedProperties) throws Exception {
		try {
			recursionCount++;
			// get the properties from the Object
			PropertyDescriptor[] propArray = PropertyUtils.getPropertyDescriptors(notebookObj);
			for (int i = 0; i < propArray.length; i++) {
				PropertyDescriptor pd = propArray[i];

				// must have a getter and setter otherwise we're not interested in this property
				// also must not be a TRANSIENT property (ie. calculated in the GUI and not stored)
				if (pd.getWriteMethod() != null && pd.getReadMethod() != null) {
					String propertyName = pd.getName();

					// check this property isn't an ArrayList of exclusions
					if (!propertyExclusions.contains(propertyName)) {
						Field property = null;

						try {
							property = pd.getReadMethod().getDeclaringClass().getDeclaredField(propertyName);
						} catch (NoSuchFieldException nsfe) {
							// try the fieldname with a leading underscore
							try {
								property = pd.getReadMethod().getDeclaringClass().getDeclaredField("_" + propertyName);
							} catch (NoSuchFieldException nsfe2) {
								// the property isn't something we need to persist
							}
						}
						if (property != null && !Modifier.isTransient(property.getModifiers())) {
							if (savedProperties == null || (savedProperties != null && !savedProperties.contains(pd.getName()))) {
								if (pd.getPropertyType().isPrimitive() || pd.getPropertyType() == String.class
										|| pd.getPropertyType() == Date.class) {
									// output to XML
									String strValue = (String) getValueFromBean(propertyName, "", notebookObj);

									// we may have already passed in the node we want to use
									if (objectNode != null)
										xmlHelper.createNewElement(objectNode, rootNode + propertyToFieldName(pd.getName()) + "/",
												strValue);
									else
										xmlHelper.createNewXMLElement(rootNode + propertyToFieldName(pd.getName()) + "/", strValue,
												false);
									// --xmlHelper.createNewElement(rootNode + propertyToFieldName(pd.getName()) + "/", strValue);
								} else if (pd.getPropertyType().getName().startsWith("com")) {
									if (objectNode != null)
										xmlHelper.createNewElement(objectNode, rootNode + propertyToFieldName(propertyName), "");
									else
										xmlHelper.createNewXMLElement(rootNode + propertyToFieldName(propertyName), "", false);
									// --xmlHelper.createNewElement(rootNode + propertyToFieldName(propertyName), "");

									Object nestedObject = pd.getReadMethod().invoke(notebookObj, null);
									if (nestedObject != null)
										createXMLFromModel(nestedObject, xmlHelper, rootNode + propertyToFieldName(propertyName)
												+ "/", objectNode, savedProperties);
								} else if (pd.getPropertyType() == List.class || pd.getPropertyType() == ArrayList.class) {
									if (objectNode != null)
										xmlHelper.createNewElement(objectNode, rootNode + propertyToFieldName(propertyName), "");
									else
										xmlHelper.createNewXMLElement(rootNode + propertyToFieldName(propertyName), "", false);
									// --xmlHelper.createNewElement(rootNode + propertyToFieldName(propertyName), "");

									String arrayListNode = rootNode + propertyToFieldName(propertyName) + "/";
									ArrayList tempList = (ArrayList) pd.getReadMethod().invoke(notebookObj, null);
									Iterator listIterator = tempList.iterator();
									int intCount = 0;
									// Node arrayObjectNode;
									String entryNodePath = arrayListNode + "Entry";
									while (listIterator.hasNext()) {
										Object listItem = listIterator.next();
										if (listItem instanceof String || listItem instanceof Integer || listItem instanceof Float) {
											// output to XML
											// --arrayObjectNode = xmlHelper.createNewElement(arrayListNode + "Entry",
											// listItem.toString());
											xmlHelper.createNewXMLElement(entryNodePath, listItem.toString(), true);
										} else {
											// get a node
											// arrayObjectNode = xmlHelper.createNewElement(arrayListNode + "Entry", "");
											xmlHelper.createNewXMLElement(entryNodePath, "", true);
											xmlHelper.createNewXMLAttribute(entryNodePath, "index", String.valueOf(intCount));
											if (listItem != null)
												createXMLFromModel(listItem, xmlHelper, entryNodePath + "/", null, savedProperties);
											// --createXMLFromModel(listItem, xmlHelper, "", arrayObjectNode, savedProperties);
										}
										xmlHelper.createNewXMLAttribute(entryNodePath, "class", listItem.getClass().getName());
										// xmlHelper.createNewAttribute(arrayObjectNode, "class", listItem.getClass().getName());
										intCount++;
									}
								}
							}
						}
					}
				}
			}

			if (recursionCount == 1) {
				recursionCount--;

				// Look at the property mapping for any nested properties that need to be included
				// but have been missed as the object they're in was Transient.
				Iterator propertyIterator = propertyMappings.keySet().iterator();
				while (propertyIterator.hasNext()) {
					// these propertys have keys but elements are blank strings
					String nestedProperty = (String) propertyIterator.next();
					if (propertyMappings.get(nestedProperty).equals("")) {
						StringTokenizer strToken = new StringTokenizer(nestedProperty, "/");
						String strProperty = (String) strToken.nextElement();
						String pathName = nestedProperty.substring(nestedProperty.indexOf("/") + 1);
						String strValue = (String) getValueFromBean(strProperty, pathName, notebookObj);
						if (strValue != null && !strValue.equals("NOT_FOUND"))
							xmlHelper.createNewElement(metaDataRoot + nestedProperty + "/", strValue);
					}
				}
			}
		} catch (Exception e) {
			recursionCount = 0;
			throw new Exception("Unable to generate XML for this object: " + notebookObj.getClass().getName(), e);
		}
		recursionCount--;
	}

	private String fieldNameToProperty(String strName) throws Exception {
		String strPropertyName = "";

		// use the field name minus any underscores to call getter and setters
		StringTokenizer sToken = new StringTokenizer(strName, "_");
		while (sToken.hasMoreElements()) {
			String strTemp = ((String) sToken.nextElement());
			if (strTemp.equals(strTemp.toUpperCase())) {
				strTemp = strTemp.toLowerCase();
			}
			strTemp = Character.toUpperCase(strTemp.charAt(0)) + strTemp.substring(1);
			strPropertyName += strTemp;
		}
		char firstChar = Character.toLowerCase(strPropertyName.charAt(0));
		strPropertyName = new Character(firstChar).toString() + strPropertyName.substring(1);

		return strPropertyName;

	}

	private String propertyToFieldName(String strName) throws Exception {
		String strFieldName = "";
		int intPreviousCap = 0;

		// look through the string and put in any relevant underscores
		for (int i = 0; i < strName.length(); i++) {
			String ch = new Character(strName.charAt(i)).toString();
			if (ch.equals(ch.toUpperCase())) {
				if (intPreviousCap != i - 1) {
					strFieldName += "_";
					intPreviousCap = i;
				} else {
					intPreviousCap = i;
				}
			} else if (i == 0) {
				ch = ch.toUpperCase();
				intPreviousCap = i;
			}

			strFieldName += ch;
		}

		return strFieldName;
	}

}