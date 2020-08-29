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
package com.dtc.UCService;

import org.omg.CORBA.UserException;

// Referenced classes of package com.dtc.UCService:
//            UCExceptionHelper, CompoundInfo

public final class UCException extends UserException {

	private static final long serialVersionUID = -8536777159371403611L;
	
	public UCException() {
		super(UCExceptionHelper.id());
		ucError = "";
	}

	public UCException(String _reason, String ucError, int errCode,	CompoundInfo ciList[]) {
		super(UCExceptionHelper.id() + "" + _reason);
		this.ucError = "";
		this.ucError = ucError;
		this.errCode = errCode;
		this.ciList = ciList;
	}

	public UCException(String ucError, int errCode, CompoundInfo ciList[]) {
		this.ucError = "";
		this.ucError = ucError;
		this.errCode = errCode;
		this.ciList = ciList;
	}

	public String ucError;
	public int errCode;
	public CompoundInfo ciList[];
}
