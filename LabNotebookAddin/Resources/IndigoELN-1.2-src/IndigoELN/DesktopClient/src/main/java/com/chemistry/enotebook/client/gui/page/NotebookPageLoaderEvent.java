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
 * Created on Sep 17, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.client.gui.page;

import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;

import java.util.EventObject;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class NotebookPageLoaderEvent extends EventObject {
	
	private static final long serialVersionUID = 7351986717573324291L;
	
	private NotebookRef nbRef = null;
	private NotebookPage model = null;
	private NotebookPageGuiInterface gui = null;

	public NotebookPageLoaderEvent(Object source, NotebookRef nbRef) {
		super(source);
		this.nbRef = nbRef;
	}

	public NotebookPageLoaderEvent(Object source, NotebookRef nbRef, NotebookPage model) {
		super(source);
		this.nbRef = nbRef;
		this.model = model;
	}

	public NotebookPageLoaderEvent(Object source, NotebookRef nbRef, NotebookPageGuiInterface gui) {
		super(source);
		this.nbRef = nbRef;
		this.gui = gui;
	}

	public NotebookPageLoaderEvent(Object source, NotebookRef nbRef, NotebookPage model, NotebookPageGuiInterface gui) {
		super(source);
		this.nbRef = nbRef;
		this.gui = gui;
		this.model = model;
	}

	public NotebookRef getNotebookRef() {
		return nbRef;
	}

	public NotebookPage getModel() {
		return model;
	}

	public NotebookPageGuiInterface getGui() {
		return gui;
	}
}
