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
package com.chemistry.enotebook.client.gui.page.experiment.stoich;

import com.chemistry.enotebook.domain.StoicModelInterface;

import java.util.EventObject;

public class RxnRoleChangedEvent extends EventObject {
	
	private static final long serialVersionUID = 8068127638583421714L;
	
	StoicModelInterface stoicmodel;

	public RxnRoleChangedEvent(Object source, StoicModelInterface stoicI) {
		super(source);
		this.stoicmodel = stoicI;
	}

	public StoicModelInterface getStoicmodel() {
		return stoicmodel;
	}
}