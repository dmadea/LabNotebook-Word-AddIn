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


public class MonomerPlateWell {

	String plateKey = "";
	String position = "";
	String column = "";
	String compoundId = "";
	String deliveredCompoundId = "";
	String weight = "";
	String mMoles = "";
	String molecularWeight = "";
	String molecularFormula = "";
	String imageUri = "";
	// for plate view in report
	String rowid = "";
	String colid = "";
	
	public void dispose() {
		
	}
	
	public MonomerPlateWell() {
	}
	
	public MonomerPlateWell(String plateKey, String position,
			String compoundId, String weight, String moles,
			String molecularWeight, String molecularFormula, String imageUri) {
		super();
		this.plateKey = plateKey;
		this.position = position;
		this.compoundId = compoundId;
		this.weight = weight;
		mMoles = moles;
		this.molecularWeight = molecularWeight;
		this.molecularFormula = molecularFormula;
		this.imageUri = imageUri;
	}

	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getCompoundId() {
		return compoundId;
	}
	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getMMoles() {
		return mMoles;
	}
	public void setMMoles(String moles) {
		mMoles = moles;
	}
	public String getMolecularWeight() {
		return molecularWeight;
	}
	public void setMolecularWeight(String molecularWeight) {
		this.molecularWeight = molecularWeight;
	}
	
	public String getMolecularFormula() {
		return molecularFormula;
	}

	public void setMolecularFormula(String molecularFormula) {
		this.molecularFormula = molecularFormula;
	}
	
	public String getImageUri() {
		return imageUri;
	}

	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}

	public String getPlateKey() {
		return plateKey;
	}

	public void setPlateKey(String plateKey) {
		this.plateKey = plateKey;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}
	public String getRowid() {
		return rowid;
	}

	public void setRowid(String rowid) {
		this.rowid = rowid;
	}
	public String getColid() {
		return colid;
	}

	public void setColid(String colid) {
		this.colid = colid;
	}

	public String getDeliveredCompoundId() {
		return deliveredCompoundId;
	}

	public void setDeliveredCompoundId(String deliveredCompoundId) {
		this.deliveredCompoundId = deliveredCompoundId;
	}

	public String toXml() {
//		StringBuffer buff = new StringBuffer("<monomerPlateWell>");
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
//
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
//		buff.append("</monomerPlateWell>");
//		System.out.println(buff.toString());
//		return buff.toString();


		StringBuffer buff = new StringBuffer("<monomerPlateWell>");
		buff.append("<plateKey>").append(this.getPlateKey()).append("</plateKey>");
		buff.append("<compoundId>").append(this.getCompoundId()).append("</compoundId>");
		buff.append("<deliveredCompoundId>").append(this.getCompoundId()).append("</deliveredCompoundId>");
		buff.append("<mMoles>").append(this.getMMoles()).append("</mMoles>");
		buff.append("<molecularWeight>").append(this.getMolecularWeight()).append("</molecularWeight>");
		buff.append("<molecularFormula>").append(this.getMolecularFormula()).append("</molecularFormula>");
		buff.append("<position>").append(this.getPosition()).append("</position>");
		buff.append("<weight>").append(this.getWeight()).append("</weight>");
		buff.append("<imageUri>").append(this.getImageUri()).append("</imageUri>");
		buff.append("<rowid>").append(this.getRowid()).append("</rowid>");
		buff.append("<colid>").append(this.getColid()).append("</colid>");
		buff.append("</monomerPlateWell>");
		return buff.toString();
	}
}

