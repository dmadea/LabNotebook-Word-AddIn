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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlateRow {

	private String rowId = "";
	private String plateKey = "";
	private List<PlateWell> wells = new ArrayList<PlateWell>();
	
	public void dispose() {
		if (wells != null) {
			for (PlateWell w : wells) {
				w.dispose();
			}
			wells.clear();
		}
	}

	public String getRowId() {
		return rowId;
	}
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	public String getPlateKey() {
		return plateKey;
	}
	public void setPlateKey(String plateKey) {
		this.plateKey = plateKey;
	}
	public void addWell(PlateWell well) {
		wells.add(well);
	}
	public List getWells() {
		return wells;
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer("<plateRow>");
		try {
			Class c = Class.forName(this.getClass().getName());
			Object me = this;
			Method[] allMethods = c.getDeclaredMethods();
			for (int i=0; i<allMethods.length; i++) {
				Method method = allMethods[i];
				String methodName = method.getName();
				if (methodName.startsWith("get")) {
					String fieldName = methodName.substring(3);
					if (fieldName.equalsIgnoreCase("wells"))
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
			buff.append("<wells>");
			for (Iterator it = wells.iterator(); it.hasNext();) {
				//////////////SynthesisPlateWell well = (SynthesisPlateWell) it.next();
				PlateWell well = (PlateWell) it.next();
				//buff.append("<well>");
				buff.append(well.toXml());
				//buff.append("</well>");
			}
			buff.append("</wells>");			
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
		buff.append("</plateRow>");
		System.out.println(buff.toString());
		return buff.toString();
	}	
}
