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

package com.chemistry.enotebook.vcr.auxiliary;

import java.io.Serializable;

public class VcrStatus implements Serializable {
	
	private static final long serialVersionUID = 4711018360612488037L;
	
	public static final int _PASSED = 0;
	public static final VcrStatus PASSED = new VcrStatus(_PASSED);
	
	public static final int _FAILED = 1;
	public static final VcrStatus FAILED = new VcrStatus(_FAILED);
	
	public static final int _RUNNING = 2;
	public static final VcrStatus RUNNING = new VcrStatus(_RUNNING);
	
	public static final int _UNKNOWN = 3;
	public static final VcrStatus UNKNOWN = new VcrStatus(_UNKNOWN);
	
	public static final int _PASS_LOAD = 4;
	public static final VcrStatus PASS_LOAD = new VcrStatus(_PASS_LOAD);
	
	public static final int _PASS_VALIDATION = 5;
	public static final VcrStatus PASS_VALIDATION = new VcrStatus(_PASS_VALIDATION);
	
	public static final int _PASS_VNV = 6;
	public static final VcrStatus PASS_VNV = new VcrStatus(_PASS_VNV);
	
	public static final int _PASS_UC = 7;
	public static final VcrStatus PASS_UC = new VcrStatus(_PASS_UC);
	
	public static final int _PASS_VGSD = 8;
	public static final VcrStatus PASS_VGSD = new VcrStatus(_PASS_VGSD);
	
	public static final int _PASS_ETL = 9;
	public static final VcrStatus PASS_ETL = new VcrStatus(_PASS_ETL);
	
	public static final int _PASS_GCD = 10;
	public static final VcrStatus PASS_GCD = new VcrStatus(_PASS_GCD);
	
	public static final int _FAIL_ETL = 11;
	public static final VcrStatus FAIL_ETL = new VcrStatus(_FAIL_ETL);
	
	public static final int _FAIL_GCD = 12;
	public static final VcrStatus FAIL_GCD = new VcrStatus(_FAIL_GCD);

	private int value;

	public int getValue() {
		return value;
	}

	protected VcrStatus(int value) {
		this.value = value;
	}
}
