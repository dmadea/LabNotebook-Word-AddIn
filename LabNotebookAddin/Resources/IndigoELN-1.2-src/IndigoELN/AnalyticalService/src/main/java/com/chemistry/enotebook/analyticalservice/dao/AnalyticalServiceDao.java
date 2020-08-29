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
 * Created on Jun 21, 2005
 *
 * 
 */
package com.chemistry.enotebook.analyticalservice.dao;

import com.chemistry.enotebook.experiment.utils.CeNXMLParser;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnalyticalServiceDao {

	final static String  DATA_SOURCE_NAME = ServiceLocator.CEN_DS_JNDI;
	
	final static String XML_METADATA_QUERY = "select xml_metadata from cen_properties where site_code = 'GBL'";
	
	public boolean canMarkAsLinked()
	{
		boolean result = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try	{
			conn = getCeNConnection();
			stmt = conn.prepareStatement(XML_METADATA_QUERY);
			rs = stmt.executeQuery();
			if (rs.next()) {
				String linkFlag = CeNXMLParser.getXmlProperty(rs.getString("XML_METADATA"), CeNSystemXmlProperties.PROP_ANALYTICAL_SERVICE_CAN_MARK_AS_LINK);
				if (linkFlag != null && linkFlag.equals("true"))
					result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();  //do nothing. This is a grace method
		} finally {
			cleanUp(conn, stmt, rs);
		}
		return result;
	}
	
	
	/**
	 * @return
	 */
	public int getNumberOfTablesToSearch() 
	{
		int noOfTables = 2;		// Default
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try	{
			conn = getCeNConnection();
			stmt = conn.prepareStatement(XML_METADATA_QUERY);
			rs = stmt.executeQuery();
			if (rs.next()) {
				String s_noOfTables = CeNXMLParser.getXmlProperty(rs.getString("XML_METADATA"), CeNSystemXmlProperties.PROP_ANALYTICAL_SERVICE_NO_OF_TABLES_SEARCH);
				if (s_noOfTables != null && s_noOfTables.length() > 0)
					noOfTables = Integer.parseInt(s_noOfTables);
			}
		} catch (Exception e) {
			e.printStackTrace();  //do nothing 
		} finally {
			cleanUp(conn, stmt, rs);
		}
		return noOfTables;
	}

	
	/**
	 * @return
	 */
	public int getMaxNumberOfResults() 
	{
		int maxNoResults = 50;		// Default
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try	{
			conn = getCeNConnection();
			stmt = conn.prepareStatement(XML_METADATA_QUERY);
			rs = stmt.executeQuery();
			if (rs.next()) {
				String s_maxNoResults = CeNXMLParser.getXmlProperty(rs.getString("XML_METADATA"), CeNSystemXmlProperties.PROP_ANALYTICAL_SERVICE_MAX_RESULTS);
				if (s_maxNoResults != null && s_maxNoResults.length() > 0)
					maxNoResults = Integer.parseInt(s_maxNoResults);
			}
		} catch (Exception e) {
			e.printStackTrace();  //do nothing 
		} finally {
			cleanUp(conn, stmt, rs);
		}
		return maxNoResults;
	}

	/**
	 * @return Connection
	 * @throws NamingException
	 * @throws SQLException
	 */
	private Connection getCeNConnection()
		throws NamingException, SQLException, PropertyException
	{
		DataSource ds = ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(DATA_SOURCE_NAME));
		return ds.getConnection();
	}
	
	/**
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	private void cleanUp(Connection conn, Statement stmt, ResultSet rs)
	{
		try { if (rs != null)   rs.close();   } catch (Exception e) { }
		try { if (stmt != null) stmt.close(); } catch (Exception e) { }
		try { if (conn != null) conn.close(); } catch (Exception e) { }
	}
	
	/**
	 * @return the instrumentInfoNodeList
	 */
	public List<String> getInstrumentTypesSupported() {
		List<String> toReturn = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try	{
			conn = getCeNConnection();
			stmt = conn.prepareStatement(XML_METADATA_QUERY);
			rs = stmt.executeQuery();
			if (rs.next()) {
				String xml_nodeList_InsTypes = CeNXMLParser.getXmlPropertyAsXml(rs.getString("XML_METADATA"), CeNSystemXmlProperties.PROP_ANALYTICAL_SERVICE_INS_TYPES);
				toReturn = getInstrumentTypesFromXMLNodeString(xml_nodeList_InsTypes);
			}
		} catch (Exception e) {
			e.printStackTrace();  //do nothing 
		} finally {
			cleanUp(conn, stmt, rs);
		}
		
		return toReturn; 
	}	
	
	/**
	 * @param xmlString
	 * @throws IllegalArgumentException
	 */
	private List<String> getInstrumentTypesFromXMLNodeString(String xmlString) 
	{
		List<String> instrumentTypes = new ArrayList<String>();
		if (StringUtils.isBlank(xmlString)) {
			throw new IllegalArgumentException("No AnalyticalService Instrument Types found in CeN_Properties string: " + xmlString);
		}
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
			xmlString = xmlString.replaceAll("\n  ", "");
			xmlString = xmlString.replaceAll("\n", "");
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(xmlString)));
			document.getDocumentElement().normalize();
			Element rootElement = document.getDocumentElement();
			NodeList l = rootElement.getChildNodes();
			for (int i = 0; i < l.getLength(); i++) {
				Node n = l.item(i);
				if (n != null) {
					NamedNodeMap map = n.getAttributes();
					if (map != null) {
						Node n1 = map.getNamedItem("Code");
						if (n1 != null) {
							instrumentTypes.add(n1.getNodeValue());
						}
					}
				}
			}
		} catch (Throwable e) {
			throw new IllegalArgumentException("Failed to load the AnalyticalService Instrument Types from xml node string. Perhaps missing 'Code' element?\n" + xmlString, e);
		}
		return instrumentTypes;
	}
}
