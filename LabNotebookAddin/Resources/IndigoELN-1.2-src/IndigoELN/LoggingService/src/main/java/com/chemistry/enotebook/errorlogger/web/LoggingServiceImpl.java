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
package com.chemistry.enotebook.errorlogger.web;

import com.chemistry.enotebook.errorlogger.delegate.LoggerProducer;
import com.chemistry.enotebook.errorlogger.interfaces.LoggingService;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;

public class LoggingServiceImpl implements LoggingService {

	private static final Log log = LogFactory.getLog(LoggingServiceImpl.class);

    @Override
    public void log(String message) {
        try {
            StringReader reader = new StringReader(message);
            SAXBuilder builder = new SAXBuilder();

            Document doc;
            doc = builder.build(reader);

            Element root = doc.getRootElement();
            Element msgTypeElement = (Element) XPath.selectSingleNode(root, "/CeN_Log/Log_Type");

            log.debug("LoggingService -> msgTypeElement:" + msgTypeElement);

            if (msgTypeElement.getTextTrim().equals(LoggerProducer.METRICS)) {
                writeMetricsLogToDB(root);
            } else if (msgTypeElement.getTextTrim().equals(LoggerProducer.EXCEPTION)
                    || msgTypeElement.getTextTrim().equals(LoggerProducer.INFO)
                    || msgTypeElement.getTextTrim().equals(LoggerProducer.WARNING)) {
                writeExceptionLogToDB(message);
            } else {
                log.debug(DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL).format(new Date())
                        + " LoggingService -> log(): Failed to recognize message type: \n"
                        + message);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing Log Message to Database: " + e.getMessage(), e);
        }
    }

	private void writeMetricsLogToDB(Element root) {
		Connection con = null;
		PreparedStatement pst = null;

		try {
			Element appNameElement = (Element) XPath.selectSingleNode(root,
					"/CeN_Log/Log_Body/Application_Name");
			Element subFuncElement = (Element) XPath.selectSingleNode(root,
					"/CeN_Log/Log_Body/Sub_Function_Name");
			Element userElement = (Element) XPath.selectSingleNode(root,
					"/CeN_Log/Log_Body/Account_Name");
			Element siteCodeElement = (Element) XPath.selectSingleNode(root,
					"/CeN_Log/Log_Body/Site_Code");

			// prepare statement
			String query = "insert into CEN_METRICS (APPLICATION_NAME, SUB_FUNCTION_NAME, WHEN_USED, ACCOUNT_NAME, SITE_CODE) values (?, ?, ?, ?, ?)";

			con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI))).getConnection();

			pst = con.prepareStatement(query);
			pst.setString(1, appNameElement.getTextTrim());
			pst.setString(2, subFuncElement.getTextTrim());
			pst.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			pst.setString(4, userElement.getTextTrim());
			pst.setString(5, siteCodeElement.getTextTrim());

			pst.executeUpdate();
		} catch (Exception ex) {
			throw new RuntimeException("Error writing Metrics to Database: " + ex.getMessage(), ex);
		} finally {
			try {
				if (pst != null)
					pst.close();
			} catch (SQLException ignored) {
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException ignored) {
			}
		}
	}

	/**
	 * This method is sending the message to the database.
	 * 
	 */
	private void writeExceptionLogToDB(String message) {
		Connection con = null;
		PreparedStatement pst = null;
        
		log.debug("writeExceptionLogToDB.enter");
		
        try {
			String query = "insert into CEN_LOG (TIMESTAMP, MESSAGE) values (?, ?)";

			con = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI))).getConnection();

			pst = con.prepareStatement(query);
			pst.setTimestamp(1, new Timestamp(System.currentTimeMillis()));

			log.debug("attempting to retrieve db connection from: "	+ PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI));

			pst.setString(2, message);
			pst.executeUpdate();
            
			log.debug("writeExceptionLogToDB.exit");
		} catch (Exception ex) {
			try {
				if (con != null) {
					con.rollback();
				}
			} catch (Exception e) {
				log.error("Failed to rollback changes.", e);
			}
            throw new RuntimeException("Error writing Log Message to Database: " + ex.getMessage(), ex);
		} finally {
			try {
				if (pst != null) {
                    pst.close();
                }
			} catch (SQLException ignored) {
			}
			try {
				if (con != null) {
                    con.close();
                }
			} catch (SQLException ignored) {
			}
		}
	}
}
