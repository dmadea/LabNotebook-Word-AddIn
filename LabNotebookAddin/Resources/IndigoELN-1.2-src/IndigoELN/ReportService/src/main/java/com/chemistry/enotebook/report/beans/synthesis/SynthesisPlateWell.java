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
package com.chemistry.enotebook.report.beans.synthesis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SynthesisPlateWell extends PlateWell {

	private static final Log log = LogFactory.getLog(SynthesisPlateWell.class);
	
//	private String plateKey = "";
//	private String position = "";
	private String batchNumber = "";
	private String vcNumber = "";
	private String molecularWeight = "";
	private String rowId = "";
	private List precursors = new ArrayList();
	
//	public String getPlateKey() {
//		return plateKey;
//	}
//	public void setPlateKey(String plateKey) {
//		this.plateKey = plateKey;
//	}
	public String getVcNumber() {
		return vcNumber;
	}
	public void setVcNumber(String vcNumber) {
		this.vcNumber = vcNumber;
	}
	public String getMolecularWeight() {
		return molecularWeight;
	}
	public void setMolecularWeight(String molecularWeight) {
		this.molecularWeight = molecularWeight;
	}
	public void addPrecursor(SynthesisPrecursor precursor) {
		this.precursors.add(precursor);
	}
//	public String getPosition() {
//		return position;
//	}
//	public void setPosition(String position) {
//		this.position = position;
//	}
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public String getRowId() {
		return rowId;
	}
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	public List getPrecursors() {
		return precursors;
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer("<synthesisPlateWell>");
		buff.append(super.toXml());
		try {
			Class c = Class.forName(this.getClass().getName());
			Object me = this;
			Method[] allMethods = c.getDeclaredMethods();
			for (int i=0; i<allMethods.length; i++) {
				Method method = allMethods[i];
				String methodName = method.getName();
				if (methodName.startsWith("get")) {
					String fieldName = methodName.substring(3);
					if (fieldName.equalsIgnoreCase("precursors"))
						continue;
					buff.append("<").append(fieldName).append(">");
					try {
						buff.append((method.invoke(me, (Object[])null)).toString());
					} catch (RuntimeException e) {
						buff.append("");
					}
					buff.append("</").append(fieldName).append(">");
				}
			}
			// now get the well xml
			buff.append("<precursors>");
			for (Iterator it = precursors.iterator(); it.hasNext();) {
				SynthesisPrecursor well = (SynthesisPrecursor) it.next();
				buff.append(well.toXml());
			}
			buff.append("</precursors>");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buff.append("</synthesisPlateWell>");
		log.debug(buff.toString());
		return buff.toString();
	}	
	
}
