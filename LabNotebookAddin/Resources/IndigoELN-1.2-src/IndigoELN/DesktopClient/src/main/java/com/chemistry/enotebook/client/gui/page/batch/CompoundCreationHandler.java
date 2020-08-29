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
package com.chemistry.enotebook.client.gui.page.batch;

import com.chemistry.enotebook.client.gui.page.batch.events.CompoundCreatedEventListener;

import java.util.Vector;

public class CompoundCreationHandler {
	transient Vector compoundListeners = new Vector();
	//private NotebookPageModel notebookPageModel = null;
	//private ReactionStepModel rxnStep;
	public synchronized void addCompoundCreatedEventListener(CompoundCreatedEventListener l) {
		compoundListeners.addElement(l);
	}

	public synchronized void removeCompoundCreatedEventListener(CompoundCreatedEventListener l) {
		compoundListeners.removeElement(l);
	}

	public void fireNewCompoundCreated(CompoundCreationEvent event) {
		if (compoundListeners != null) {
			Vector listeners = compoundListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++) {
				((CompoundCreatedEventListener) listeners.elementAt(i)).newCompoundCreated(event);
			}
		}
	}

	public void fireCompoundDeleted(CompoundCreationEvent event) {
		if (compoundListeners != null) {
			Vector listeners = compoundListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++) {
				((CompoundCreatedEventListener) listeners.elementAt(i)).compoundRemoved(event);
			}
		}
	}
	

	public void fireCompoundUpdated(CompoundCreationEvent event) {
		if (compoundListeners != null) {
			Vector listeners = compoundListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++) {
				((CompoundCreatedEventListener) listeners.elementAt(i)).compoundUpdated(event);
			}
		}
	}	
}
