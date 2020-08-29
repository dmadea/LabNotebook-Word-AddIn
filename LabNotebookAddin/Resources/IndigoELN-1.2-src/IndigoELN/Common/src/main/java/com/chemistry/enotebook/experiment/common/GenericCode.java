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
 * GenericCode.java
 * 
 * Created on Dec 2, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.common;

import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;

/**
 * 
 * @date Dec 2, 2004
 * 
 *       Used to help map to CDT tables as a generic means of creating and
 *       storing codes and their descriptions
 */
public class GenericCode extends ObservableObject implements DeepCopy,
		DeepClone {
	private static final long serialVersionUID = -6038505473833465183L;

	private static final int HASH_PRIME = 37967;

	protected String code = "";
	protected String description = "";

	//
	// Constructor
	//
	public GenericCode() {
	}

	/**
	 * @param code
	 * @param descr
	 */
	public GenericCode(String code, String descr) {
		super();
		this.code = code;
		if (descr != null)
			description = descr;
	}

	//
	// Getters/Setters
	//
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		if (!this.code.equals(code)) {
			if (!code.equals("") || code.equals("00"))
				this.code = code;
			else {
				this.code = "";
				description = "";
			}
			setModified(true);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String descr) {
		if (!description.equals(descr)) {
			description = descr;
			setModified(true);
		}
	}

	//
	// Implements
	//

	public void deepCopy(Object resource) {
		if (resource instanceof GenericCode) {
			GenericCode genCode = (GenericCode) resource;
			code = genCode.code;
			description = genCode.description;
			if (!genCode.isLoading()) {
				setModified(true);
			}
		}
	}

	public Object deepClone() {
		return new GenericCode(code, description);
	}

	//
	// Overrides
	//

	public boolean equals(Object value) {
		boolean result = false;
		if (value instanceof GenericCode) {
			GenericCode gCode = (GenericCode) value;
			result = this.hashCode() == gCode.hashCode();
		}
		return result;
	}

	public int hashCode() {
		return HASH_PRIME + HASH_PRIME
				* (code.hashCode() + description.hashCode());
	}

}
