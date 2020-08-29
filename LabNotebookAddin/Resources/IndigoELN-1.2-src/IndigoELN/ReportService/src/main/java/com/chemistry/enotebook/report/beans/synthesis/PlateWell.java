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


public class PlateWell {

	private String plateKey = "";
	private String position = "";
	private String column = "";
	
	public void dispose() {
		
	}
	
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
		return buff.toString();
	}
	
}
