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
package com.chemistry.enotebook.client.gui.common.errorhandler;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.errorlogger.delegate.LoggerProducer;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.util.ExceptionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CeNErrorHandler {

	private static final Log log = LogFactory.getLog(CeNErrorHandler.class);
	
	public static final byte LOG_TO_SERVER = 1;
	public static final byte LOG_TO_OPTIONPANE = 2;
	public static final byte LOG_TO_FILE = 4;
	public static final byte LOG_ALL = 7;

	private byte logErrorLevel = LOG_TO_OPTIONPANE | LOG_TO_SERVER;

	private static CeNErrorHandler _instance = null;

	private CeNErrorHandler() {
	}

	public static CeNErrorHandler getInstance() {
		if (_instance == null) {
			createInstance();
		}
		return _instance;
	}

	// form of double-checked locking
	private static synchronized void createInstance() {
		if (_instance == null) {
			_instance = new CeNErrorHandler();
		}
	}

	public void setErrorLevel(byte level) {
		logErrorLevel = level;
	}

	public void showMsgDialog(JFrame owner, String title, String msg) {
		MsgBox.showMsgBox(owner, title, msg);
	}

	public static void showMsgOptionPane(Component parentFrame, String title, String msg, int jOptionPaneConstant) {
		JOptionPane.showMessageDialog(parentFrame, msg, title, jOptionPaneConstant);
	}

	/**
	 * Ensure you have logged to local drive as this process will not do so
	 */
	public void logInformationMsg(Component comp, String msg) {
		this.logMsg(comp, msg, "CeN Informational Message");
	}

	/**
	 * Ensure you have logged to local drive as this process will not do so
	 */
	public void logMsg(Component comp, String msg, String title) {
		// if (msg.length() < 101)
		// if (!DEBUG)
		JOptionPane.showMessageDialog(getOwnerComponent(comp), msg, title, JOptionPane.INFORMATION_MESSAGE);
		// else
		// MsgBox.showMsgBox(getOwnerComponent(comp), title, msg);
		// System.out.println("CeNErrorHandler.logMsg: " + msg);
		if ((logErrorLevel & LOG_TO_SERVER) != 0)
			LoggerProducer.logMessage(putIntoXML(msg));
	}

	/**
	 * Logs error message to server.  Won't be capturing any stack trace for you.
	 */
	public void logErrorMsg(Component comp, String msg, String title) {
		this.logErrorMsg(comp, msg, title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Logs error message to server.  Won't be capturing any stack trace for you.
	 */
	public void logErrorMsg(Component comp, String msg, String title, int jOptionPaneOption) {
		// if (msg.length() < 101)
		// if (!DEBUG)
		JOptionPane.showMessageDialog(getOwnerComponent(comp), msg, title, jOptionPaneOption);
		// else
		// MsgBox.showMsgBox(getOwnerComponent(comp), title, msg);
		// System.out.println("CeNErrorHandler.logErrorMsg: " + msg);
		if ((logErrorLevel & LOG_TO_SERVER) != 0)
			LoggerProducer.logMessage(putIntoXML(msg));
	}

	/**
	 * Logs messages to server if activated.  Won't log meaningful exceptions to local drive.
	 */
	public void logErrorMsgWithoutDisplay(String msg, String title) {
		if ((logErrorLevel & LOG_TO_SERVER) != 0) {
			LoggerProducer.logMessage(putIntoXML(title + "\n" + msg));
		}
	}

	
	public void logExceptionMsg(Throwable e) {
		logExceptionMsg(null, e.getMessage(), e);
	}

	public void logExceptionMsg(Component comp, Throwable e) {
		logExceptionMsg(comp, e.getMessage(), e);
	}

	public void logExceptionMsg(Component comp, String msg, Throwable e) {
		StringBuilder displayErrMsg = new StringBuilder();

		if (!StringUtils.isBlank(msg)) {
			displayErrMsg.append(msg).append("\n");
		} else if (StringUtils.isBlank(msg) && e != null) {
			if (e.getLocalizedMessage() != null) {
				displayErrMsg.append(e.getLocalizedMessage());
				displayErrMsg.append("\n");
			} else if (e.getMessage() != null) {
				displayErrMsg.append(e.getMessage());
				displayErrMsg.append("\n");
			} else {
				displayErrMsg.append(e.getClass().getName());
				displayErrMsg.append("\n");
			}
		}

		if (displayErrMsg.length() > 0) {
			displayErrMsg.append("\n");
		}

		displayErrMsg
				.append("Ocurred at: ")
				.append((new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z")).format(new Date()))
				.append("\n");

		StringBuilder dbErrMsg = new StringBuilder(displayErrMsg.toString());

		if (MasterController.isValidSystemState()) {
			try {
				dbErrMsg
						.append("  ")
						.append(CeNSystemProperties.getRunMode())
						.append(" System ");
			} catch (Exception e2) {
				/* ignored */
			}
		}

		dbErrMsg
				.append("  ")
				.append(MasterController.getVersionInfoAsString())
				.append("\n");

		if (MasterController.isValidSystemState()) {
			try {
				dbErrMsg
						.append("  Active Frame: ")
						.append(MasterController.getGUIComponent().getActiveDesktopWindow().getTitle())
						.append("\n");
			} catch (Exception e3) {
				/* ignored */
			}
			if (MasterController.getUser() != null && !StringUtils.isBlank(MasterController.getUser().getNTUserID())) {
				dbErrMsg
						.append("  Problem Occurred for ")
						.append(MasterController.getUser().getNTUserID())
						.append(" (")
						.append(MasterController.getUser().getDisplayName())
						.append(")\n");
			}
		}

		dbErrMsg.append("Stack trace follows:\n");
		dbErrMsg.append(ExceptionUtils.getStackTrace(e));

		if ((logErrorLevel & LOG_TO_SERVER) != 0) {
			LoggerProducer.logMessage(putIntoXML(dbErrMsg.toString()));
		}
		if ((logErrorLevel & LOG_TO_OPTIONPANE) != 0) {
			JOptionPane.showMessageDialog(getOwnerComponent(comp), displayErrMsg.toString(), "An Error has Occurred", JOptionPane.ERROR_MESSAGE);
		}

		log.error("Reporting error", e); // Print to Screen in case console window is up
	}

	private Component getOwnerComponent(Component comp) {
		return (comp == null) ? MasterController.getGUIComponent() : comp;
	}

	private String putIntoXML(String msg) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			Element rootElement = doc.createElement("CeN_Log");
			doc.appendChild(rootElement);

			Element logType = doc.createElement("Log_Type");
			logType.appendChild(doc.createTextNode(LoggerProducer.EXCEPTION));
			rootElement.appendChild(logType);

			Element startTime = doc.createElement("CeN_StartTime");
			startTime.appendChild(doc.createTextNode(MasterController.CeNStartTime.toString()));
			rootElement.appendChild(startTime);

			Element memoryUsage = doc.createElement("CeN_Current_Memory_Usage");
			memoryUsage.appendChild(doc.createTextNode(String.valueOf(getUsedMemory())));
			rootElement.appendChild(memoryUsage);

			Element logBody = doc.createElement("Log_Body");
			logBody.appendChild(doc.createTextNode(msg));
			rootElement.appendChild(logBody);

			return xmlToString(doc);
		} catch (Exception e) {
			throw new RuntimeException("Error creating XML!", e);
		}
	}

	public void sendMetricsLogToServer(String funcName, String user, String siteCode) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			Element rootElement = doc.createElement("CeN_Log");
			doc.appendChild(rootElement);

			Element logType = doc.createElement("Log_Type");
			logType.appendChild(doc.createTextNode(LoggerProducer.METRICS));
			rootElement.appendChild(logType);

			Element startTime = doc.createElement("CeN_StartTime");
			startTime.appendChild(doc.createTextNode(MasterController.CeNStartTime.toString()));
			rootElement.appendChild(startTime);

			Element memoryUsage = doc.createElement("CeN_Current_Memory_Usage");
			memoryUsage.appendChild(doc.createTextNode(String.valueOf(getUsedMemory())));
			rootElement.appendChild(memoryUsage);

			Element logBody = doc.createElement("Log_Body");
			rootElement.appendChild(logBody);

			Element appName = doc.createElement("Application_Name");
			appName.appendChild(doc.createTextNode(CeNConstants.SHORT_PROGRAM_NAME));
			logBody.appendChild(appName);

			Element subFuncName = doc.createElement("Sub_Function_Name");
			subFuncName.appendChild(doc.createTextNode(funcName.toUpperCase()));
			logBody.appendChild(subFuncName);

			Element accountName = doc.createElement("Account_Name");
			accountName.appendChild(doc.createTextNode(user.toUpperCase()));
			logBody.appendChild(accountName);

			Element elemSiteCode = doc.createElement("Site_Code");
			elemSiteCode.appendChild(doc.createTextNode(siteCode.toUpperCase()));
			logBody.appendChild(elemSiteCode);

			LoggerProducer.logMessage(xmlToString(doc));
		} catch (Exception e) {
			throw new RuntimeException("Error creating XML!", e);
		}
	}

	private static String xmlToString(Document doc) {
		StringWriter sw = new StringWriter();

		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();

			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(sw));

			return sw.toString();
		} catch (Exception ex) {
			throw new RuntimeException("Error converting to String", ex);
		} finally {
			try {
				sw.close();
			} catch (IOException ignored) {
				// do nothing
			}
		}
	}

	private long getUsedMemory() {
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
	}

	/**
	 * Logs exceptions without GUI display
	 *  - log is time stamped and uploaded to db if available 
	 *  - will put message in debug statement if debug is being collected in log config.
	 */
	public void logExceptionWithoutDisplay(Throwable e, String msg) {
		StringBuilder displayErrMsg = new StringBuilder();

		if (!StringUtils.isBlank(msg)) {
			displayErrMsg.append(msg).append("\n");
		} else if (StringUtils.isBlank(msg) && e != null) {
			if (e.getLocalizedMessage() != null) {
				displayErrMsg.append(e.getLocalizedMessage());
				displayErrMsg.append("\n");
			} else if (e.getMessage() != null) {
				displayErrMsg.append(e.getMessage());
				displayErrMsg.append("\n");
			} else {
				displayErrMsg.append(e.getClass().getName());
				displayErrMsg.append("\n");
			}
		}

		if (displayErrMsg.length() > 0) {
			displayErrMsg.append("\n");
		}

		displayErrMsg
				.append("Ocurred at: ")
				.append((new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z")).format(new Date()))
				.append("\n");

		StringBuilder dbErrMsg = new StringBuilder(displayErrMsg.toString());

		if (MasterController.isValidSystemState()) {
			try {
				dbErrMsg
						.append("  ")
						.append(CeNSystemProperties.getRunMode())
						.append(" System ");
			} catch (Exception e2) {
				/* ignored */
			}
		}

		dbErrMsg
				.append("  ")
				.append(MasterController.getVersionInfoAsString())
				.append("\n");

		if (MasterController.isValidSystemState()) {
			try {
				dbErrMsg
						.append("  Active Frame: ")
						.append(MasterController.getGUIComponent().getActiveDesktopWindow().getTitle())
						.append("\n");
			} catch (Exception e3) {
			 	/* ignored */
			}
			if (!StringUtils.isBlank(MasterController.getUser().getNTUserID())) {
				dbErrMsg
						.append("  Problem Occurred for ").append(MasterController.getUser().getNTUserID())
						.append(" (")
						.append(MasterController.getUser().getDisplayName())
						.append(")\n");
			}
		}

		dbErrMsg.append("Stack trace follows:\n");
		dbErrMsg.append(ExceptionUtils.getStackTrace(e));

		LoggerProducer.logMessage(putIntoXML(dbErrMsg.toString()));
	}
}
