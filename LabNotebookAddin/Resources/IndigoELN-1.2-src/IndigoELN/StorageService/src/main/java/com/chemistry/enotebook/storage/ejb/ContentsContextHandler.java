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
 * Created on May 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.storage.ejb;

import com.chemistry.enotebook.storage.ContentsContext;
import com.chemistry.enotebook.storage.StorageContextInterface;
import com.chemistry.enotebook.storage.StorageVO;
import com.chemistry.enotebook.storage.exceptions.StorageHandlerException;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class ContentsContextHandler extends ContextHandlerImpl {
	private ContentsContextDAO dao = null;
	private StorageVO[] RowSet = null;
	
	public ContentsContextHandler() {
	}

	public ContentsContextHandler(StorageContextInterface ctx) {
		this.setContext(ctx);
	}

	public void beginQueryProcess() throws StorageHandlerException {
		try {
			this.dao = new ContentsContextDAO();
			if (this.dao.executeQuery((ContentsContext) this.ctx)) {
				this.RowSet = ((ContentsContext) this.ctx).getResults();
			}
		} catch (Exception e) {
			throw new StorageHandlerException("ContentsContextHandler:beginQueryProcess:: Failure", e);
		}
	}
}
