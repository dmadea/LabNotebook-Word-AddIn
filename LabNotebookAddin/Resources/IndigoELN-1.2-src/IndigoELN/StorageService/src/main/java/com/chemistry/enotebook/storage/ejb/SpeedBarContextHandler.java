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

import com.chemistry.enotebook.storage.SpeedBarContext;
import com.chemistry.enotebook.storage.StorageContextInterface;
import com.chemistry.enotebook.storage.exceptions.StorageHandlerException;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class SpeedBarContextHandler extends ContextHandlerImpl {
	private SpeedBarContextDAO dao = null;

	public SpeedBarContextHandler() {
	}

	public SpeedBarContextHandler(StorageContextInterface ctx) {
		this.setContext(ctx);
	}

	public void beginQueryProcess() throws StorageHandlerException {
		try {
			this.dao = new SpeedBarContextDAO();
			if (this.dao.executeQuery((SpeedBarContext) this.ctx)) {
				this.RowSet = ((SpeedBarContext) this.ctx).getResults();
			}
		} catch (Exception e) {
			throw new StorageHandlerException("SpeedBarContextHandler:beginQueryProcess:: Failure", e);
		}
	}
}