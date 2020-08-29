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
 * Created on Nov 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.experiment.common;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ExternalSupplier extends GenericCode {

	private static final long serialVersionUID = 3567301078921729862L;
	
	private String supplierCatalogRef;

	public String getSupplierCatalogRef() {
		return supplierCatalogRef;
	}

	public void setSupplierCatalogRef(String supplierCatalogRef) {
		this.supplierCatalogRef = supplierCatalogRef;
		setModified(true);
	}

	public String getSupplierName() {
		return getDescription();
	}

	public void setSupplierName(String supplierName) {
		setDescription(supplierName);
	}

	public String toString() {
		if (getCode() == null || getCode().trim().equals(""))
			return "";
		else {
			String val = "<" + getCode();
			if (getSupplierName() != null && getSupplierName().length() > 0)
				val += " - " + getSupplierName();
			val += "> ";
			if (getSupplierCatalogRef() != null && getSupplierCatalogRef().length() > 0)
				val += getSupplierCatalogRef();
			return val;
		}
	}

	public void deepCopy(Object source) {
		if (source instanceof ExternalSupplier) {
			ExternalSupplier src = (ExternalSupplier) source;
			super.deepCopy(source);
			supplierCatalogRef = src.supplierCatalogRef;
		}
	}

	public Object deepClone() {
		ExternalSupplier ex = new ExternalSupplier();
		ex.deepCopy(this);
		return ex;
	}
}
