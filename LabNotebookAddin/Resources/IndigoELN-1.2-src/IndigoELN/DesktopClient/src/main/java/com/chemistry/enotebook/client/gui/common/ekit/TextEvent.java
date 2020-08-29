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
package com.chemistry.enotebook.client.gui.common.ekit;

import java.util.EventObject;

public class TextEvent extends EventObject {
	public static final int CUSTOM_ACTION = 82;
	public static final int LINK_CLICKED = 234;
	public static final int REMOVE_HYPERLINK_ACTION = 242;
	public static final int BACKGROUND_SPELL_CHECKING = 243;

	private int actionName;
	private Object extraObject;
	private int extraInt;
	private boolean handled = false;

	public TextEvent(Object source, int action, Object extraS, int extraI) {
		super(source);
		this.actionName = action;
		this.extraObject = extraS;
		this.extraInt = extraI;
	}

	public int getActionCommand() {
		return this.actionName;
	}

	public String getExtraString() {
		if (this.extraObject instanceof String) {
			return ((String) this.extraObject);
		}
		return null;
	}

	public Object getExtraObject() {
		return this.extraObject;
	}

	public int getExtraInt() {
		return this.extraInt;
	}

	public boolean isHandled() {
		return this.handled;
	}

	public void setHandled(boolean b) {
		this.handled = b;
	}
}