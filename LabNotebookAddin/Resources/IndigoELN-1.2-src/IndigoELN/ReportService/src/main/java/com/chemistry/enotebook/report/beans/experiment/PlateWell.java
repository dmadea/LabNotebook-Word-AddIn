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
package com.chemistry.enotebook.report.beans.experiment;


public class PlateWell {

	private String plateKey = "";
	private String position = "";
	private String column = "";
	
	public String getPlateKey() {
		return plateKey;
	}
	public void setPlateKey(String plateKey) {
		this.plateKey = plateKey;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}

	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String toXml() {
		StringBuffer buff = new StringBuffer();
		buff.append("<plateKey>").append(this.getPlateKey()).append("</plateKey>");
		buff.append("<position>").append(this.getPosition()).append("</position>");
		buff.append("<column>").append(this.getColumn()).append("</column>");
//		try {
//			Class c = Class.forName(this.getClass().getName());
//			Object me = this;
//			Method[] allMethods = c.getDeclaredMethods();
//			for (int i=0; i<allMethods.length; i++) {
//				Method method = allMethods[i];
//				String methodName = method.getName();
//				if (methodName.startsWith("get")) {
//					String fieldName = methodName.substring(3);
//					buff.append("<").append(fieldName).append(">");
//					try {
//						buff.append((method.invoke(me, null)).toString());
//					} catch (RuntimeException e) {
//						buff.append("");
//					}
//					buff.append("</").append(fieldName).append(">");
//				}
//			}
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return buff.toString();
	}
	
}
