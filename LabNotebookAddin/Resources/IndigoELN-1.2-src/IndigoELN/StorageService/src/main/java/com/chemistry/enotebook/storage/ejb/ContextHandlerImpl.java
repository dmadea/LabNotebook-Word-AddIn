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
 * Created on Jul 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.storage.ejb;

import com.chemistry.enotebook.storage.StorageContextInterface;
import com.chemistry.enotebook.storage.StorageVO;
import com.chemistry.enotebook.storage.exceptions.StorageHandlerException;

/**
 * 
 * 
 * TODO Add Class Information
 */
public abstract class ContextHandlerImpl implements ContextHandlerInterface {
	StorageVO RowSet = null;
	StorageContextInterface ctx = null;

	public StorageContextInterface getContext() {
		return this.ctx;
	}

	public void setContext(StorageContextInterface ctx) {
		this.ctx = ctx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemistry.enotebook.storage.ejb.ContextHandlerInterface#beginQueryProcess()
	 */
	public abstract void beginQueryProcess() throws StorageHandlerException;

	public StorageContextInterface storeData(String strUser) throws StorageHandlerException {
		throw new StorageHandlerException(this.getClass().getName() + ":storeData:: Data Storage not supported");
	}
}
