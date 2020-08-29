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
package com.hexidec.ekit;

import com.chemistry.enotebook.client.gui.common.ekit.ProcedureContainer;
import com.hexidec.ekit.action.FormatAction;
import com.hexidec.util.Translatrix;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EkitCoreCen extends EkitCore {
	private static final long serialVersionUID = 1030755458219380412L;
	private Map<String, Action> actions;
//	private Map<String, Action> headingActions;
	
	private ProcedureContainer procedureContainer;
	
	public EkitCoreCen(boolean paramBoolean1, String paramString1, String paramString2, String paramString3, StyledDocument paramStyledDocument, URL paramURL, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, String paramString4, String paramString5, boolean paramBoolean6, boolean paramBoolean7, boolean paramBoolean8, boolean paramBoolean9, String paramString6, boolean paramBoolean10)
	{
		super(paramBoolean1, paramString1, paramString2, paramString3, paramStyledDocument, paramURL, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5, paramString4, paramString5, paramBoolean6, paramBoolean7, paramBoolean8, paramBoolean9, paramString6, false, paramBoolean10);
		
		actions = new HashMap<String,Action>();
		Action[] arrayOfAction = this.getTextPane().getActions();
		for (Action localObject3 : arrayOfAction) {
			actions.put((String)localObject3.getValue("Name"), localObject3);
		}
		
		actions.put("Heading1", new FormatAction(this, Translatrix.getTranslationString("Heading1"), HTML.Tag.H1));
		actions.put("Heading2", new FormatAction(this, Translatrix.getTranslationString("Heading2"), HTML.Tag.H2));
		actions.put("Heading3", new FormatAction(this, Translatrix.getTranslationString("Heading3"), HTML.Tag.H3));
		actions.put("Heading4", new FormatAction(this, Translatrix.getTranslationString("Heading4"), HTML.Tag.H4));
		actions.put("Heading5", new FormatAction(this, Translatrix.getTranslationString("Heading5"), HTML.Tag.H5));
		actions.put("Heading6", new FormatAction(this, Translatrix.getTranslationString("Heading6"), HTML.Tag.H6));
	}
	
	public void setProcedureContainer(ProcedureContainer procedureContainer) {
		this.procedureContainer = procedureContainer;
	}
	
	@Override
	public void refreshOnUpdate() {
		super.refreshOnUpdate();
		if (procedureContainer != null) {
			procedureContainer.editorUpdated();
		}
	}
	
	public void performUndoAction() {
		super.undoAction.actionPerformed(new ActionEvent(new Object(), 0, ""));
	}
	
	public void performSelectAllAction() {
		performAction("select-all");
	}
	
	public void performAction(String format) {
		actions.get(format).actionPerformed(new ActionEvent(new Object(), 0, ""));
	}
}
