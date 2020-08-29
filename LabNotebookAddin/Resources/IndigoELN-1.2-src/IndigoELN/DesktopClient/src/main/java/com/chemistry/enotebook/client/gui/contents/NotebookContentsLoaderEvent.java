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
package com.chemistry.enotebook.client.gui.contents;

import java.util.EventObject;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class NotebookContentsLoaderEvent extends EventObject {
	
	private static final long serialVersionUID = 3311498819888137805L;
	
	private NotebookContentsGUI contentsGui = null;
	private NotebookContentsTableModel contentsModel = null;

	public NotebookContentsLoaderEvent(Object source, NotebookContentsTableModel model) {
		super(source);
		contentsGui = null;
		contentsModel = model;
	}

	public NotebookContentsLoaderEvent(Object source, NotebookContentsTableModel model, NotebookContentsGUI gui) {
		super(source);
		contentsGui = gui;
		contentsModel = model;
	}

	public NotebookContentsGUI getContentsGui() {
		return contentsGui;
	}

	public NotebookContentsTableModel getModel() {
		return contentsModel;
	}
}
