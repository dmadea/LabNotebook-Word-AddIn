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
 * Created on 03-Aug-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.storage.ejb;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class RowModifier {

	private static final int _ORACLE_DB_XMLTYPE = 2007;
	private static final int _ORACLE_DB_CLOBTYPE = 2005;
	private static final int _ORACLE_DB_BLOBTYPE = 2004;
	private static final int _ORACLE_DB_TIMESTAMPTZ = -101;

	String modType;
	String userID;

	boolean blnAuditFlag;

	ArrayList parameterValues = new ArrayList();

	AuditLog auditLog = null;

	public RowModifier(String strType, boolean auditFlag, String strUserID) throws Exception {
		this.modType = strType;
		this.userID = strUserID;
		this.blnAuditFlag = auditFlag;
		this.auditLog = new AuditLog();
		if (!this.blnAuditFlag) {
			// no detailed audit, just log the fact there has been some change to this row
			this.auditLog.addLogEntry(this.modType, strUserID, "", "", "");
		}
	}

	public void addParameter(String strName, String strSize, String strType, Object strValue, Object strPreviousValue)
			throws Exception {

		Object strAuditValue = strValue;
		// if the auditFlag is set audit all changes in the row
		if (!strName.equals("AUDIT_LOG")) {
			if (this.blnAuditFlag) {
				if (strType.equals(Integer.toString(_ORACLE_DB_CLOBTYPE))) {
					Clob tempClob;
					if (strValue instanceof Clob) {
						tempClob = (Clob) strValue;
						strAuditValue = tempClob.getSubString(0, (int) tempClob.length());
					}
					if (strPreviousValue instanceof Clob) {
						tempClob = (Clob) strPreviousValue;
						strPreviousValue = tempClob.getSubString(0, (int) tempClob.length());
					}
				} else if (strType.equals(Integer.toString(_ORACLE_DB_BLOBTYPE))) {
					Blob tempBlob;
					if ((strValue instanceof Blob)) {
						tempBlob = (Blob) strValue;
						strAuditValue = tempBlob.getBytes(0, (int) tempBlob.length());
					}
					if (strPreviousValue instanceof Blob) {
						tempBlob = (Blob) strPreviousValue;
						strPreviousValue = tempBlob.getBytes(0, (int) tempBlob.length());
					}
				}
				if (strAuditValue == null) {
					strAuditValue = "";
				}
				if (strPreviousValue == null) {
					strPreviousValue = "";
				}
				if (!strAuditValue.equals(strPreviousValue)) {
					if (strName.indexOf("XML") > -1) {
						SAXBuilder builder = new SAXBuilder();
						// builder.setFactory(new DomFactory());
						Document doc1 = builder.build(new InputSource(new StringReader((String) strPreviousValue)));
						Document doc2 = builder.build(new InputSource(new StringReader((String) strAuditValue)));
						
						// Element xmlDiff = vmj.generateDiffs(doc1.getRootElement(), doc2.getRootElement());
						// XMLOutputter xmlOut = new XMLOutputter();
						// strAuditValue = xmlOut.outputString(xmlDiff);
					}
					this.auditLog.addLogEntry(this.modType, this.userID, strName, strAuditValue.toString(), strPreviousValue
							.toString());
				}
			}
		} else {
			// this is the audit log field so add the current value to the new log
			// then get the amended value to write back to the database
			if (strValue instanceof Clob) {
				Clob tempClob = (Clob) strValue;
				strValue = tempClob.getSubString(0, (int) tempClob.length());
			}
			this.auditLog.setCurrentLogXML((String) strValue);
			strValue = this.auditLog.toXMLString();
		}
		if ((strName.indexOf("XML") > -1) || strName.equals("AUDIT_LOG")) {
			strType = Integer.toString(_ORACLE_DB_XMLTYPE);
		} else if (strName.indexOf("DATE") > -1) {
			strType = Integer.toString(_ORACLE_DB_TIMESTAMPTZ);
		}
		ArrayList values = new ArrayList();
		values.add(strName);
		values.add(strSize);
		values.add(strType);
		values.add(strValue);
		this.parameterValues.add(values);
	}

	public Iterator getRowIterator() {
		return this.parameterValues.iterator();
	}

	public int getNumParams() {
		return this.parameterValues.size();
	}

	public ArrayList getParameters(int listIndex) {
		return (ArrayList) this.parameterValues.get(listIndex);
	}
}
