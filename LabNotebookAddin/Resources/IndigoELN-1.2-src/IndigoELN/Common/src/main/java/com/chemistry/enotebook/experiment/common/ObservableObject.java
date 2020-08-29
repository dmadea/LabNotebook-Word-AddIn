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
 * Created on Nov 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.experiment.common;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 
 * 
 * TODO Add Class Information
 */
public class ObservableObject extends Observable implements Observer,java.io.Serializable {
	
	private static final long serialVersionUID = 4121854823514769532L;
	
	private transient boolean _loading = false;
	private transient boolean _changing = false;
	private transient boolean _modified = false;
	private transient boolean _subObjModified = false;

	public boolean isModelChanged() {
		return (isModified() || subObjectModified());
	}

	public boolean isModified() {
		return _modified;
	}

	public boolean subObjectModified() {
		return _subObjModified;
	}

	public void dispose() throws Throwable {
		deleteObservers();
	}

	/**
	 * Method to determine if this object is currently being loaded.
	 * 
	 * @return true/false
	 */
	public boolean isLoading() {
		return _loading;
	}

	/**
	 * Method to set state of this object whether it is loading or not.
	 * 
	 * @param flag
	 */
	public void setLoading(boolean flag) {
		_loading = flag;
	}

	/**
	 * 
	 * @return boolean indicating whether or not the object is in the process of changing values
	 */
	public boolean isChanging() {
		return _changing;
	}

	/**
	 * Use when performing changes to multiple ObserableObjects to prevent the modified flag from throwing an event.
	 * 
	 * @param flag
	 *            indicate changing status of the instance
	 */
	public void setChanging(boolean flag) {
		_changing = flag;
	}

	/**
	 * Flag set to let others know some value in this object has changed. Should use a combination of subObjectModified and
	 * isModified to indicate what has been modified. If isChanging = true. No observers of this object will be notified, but the
	 * modified flag can be set. If isLoading = true then the modified flag will not be set, nor will any observers be notified.
	 * 
	 * @param modifiedFlag -
	 *            The object that has it's isModified() flag set.
	 */
	public void setModified(boolean modifiedFlag) {
		if (!isLoading()) {
			if (this instanceof PersistableObject) {
				this._modified = modifiedFlag;
			}
			if (modifiedFlag && !isChanging()) {
				setChanged();
				notifyObservers(this);
			}
		}
	}

	/**
	 * Uses notifyObservers(obj) to pass the modifiedObj up to interested objects. Sets the subObjectModified flag in this object to
	 * let others know something contained in this object has changed.
	 * 
	 * To test for modification of values for this object use isModified()
	 * 
	 * @param subObjModified -
	 *            boolean flag indicating that one or more objects contained by this object have been modified.
	 * @param modifiedObj -
	 *            The object that has it's isModified() flag set.
	 */
	public void setSubObjectModified(Object modifiedObj) {
		if (modifiedObj instanceof ObservableObject) {
			if (((ObservableObject) modifiedObj).isModified()) {
				this._subObjModified = true;
				setChanged();
				this.notifyObservers(modifiedObj);
			}
		}
	}

	public void setSubObjectModified(boolean flag) {
		this._subObjModified = flag;
	}

	/**
	 * Effectively clears a list of all objects. Side-effect is that this object is removed from all ObservableObject types in the
	 * targetList.
	 * 
	 * PreCondition: targetList must exist
	 * 
	 * @param targetList -
	 *            ObservableObject types
	 */
	public void removeAllObservablesFromList(List targetList) {
		if (targetList != null) {
			for (Iterator it = targetList.iterator(); it.hasNext();) {
				Object obj = it.next();
				if (obj instanceof ObservableObject) {
					ObservableObject observable = (ObservableObject) obj;
					observable.deleteObserver(this);
				}
			}
			targetList.clear();
		}
	}

	/**
	 * Removes this object from all ObservableObject types in the targetList.
	 * 
	 * PreCondition: targetList must exist
	 * 
	 * @param targetList -
	 *            ObservableObject types
	 */
	public void removeThisObserverFromList(List targetList) {
		if (targetList != null) {
			for (Iterator it = targetList.iterator(); it.hasNext();) {
				Object obj = it.next();
				if (obj instanceof ObservableObject) {
					ObservableObject observable = (ObservableObject) obj;
					observable.deleteObserver(this);
				}
			}
		}
	}

	/**
	 * Adds all Objects from the sourceList to the targetList while adding this object as an observer of each ObservableObject type.
	 * 
	 * PreCondition: both source and target lists must exist.
	 * 
	 * @param sourceList -
	 *            ObservableObject types
	 * @param targetList -
	 *            ObservableObject types
	 */
	public void addAllObservablesToList(List sourceList, List targetList) {
		if (sourceList != null && targetList != null) {
			for (Iterator it = sourceList.iterator(); it.hasNext();) {
				Object obj = it.next();
				if (obj instanceof ObservableObject) {
					ObservableObject observable = (ObservableObject) obj;
					observable.addObserver(this);
				}
				targetList.add(obj);
			}
		}
	}

	/**
	 * Adds this object as an observer to all ObservableObject types in the list.
	 * 
	 * PreCondition: targetList must exist
	 * 
	 * @param targetList -
	 *            ObservableObject types
	 */
	public void addThisObserverToList(List targetList) {
		if (targetList != null) {
			for (Iterator it = targetList.iterator(); it.hasNext();) {
				Object obj = it.next();
				if (obj instanceof ObservableObject) {
					ObservableObject observable = (ObservableObject) obj;
					observable.addObserver(this);
				}
			}
		}
	}

	/**
	 * Calls setSubObjectModified which passes the observed object back as the modified object.
	 * 
	 * @param observed -
	 *            object that has been modified: i.e. isModified() returns true
	 */
	public void update(Observable observed) {
		if (observed instanceof ObservableObject && ((ObservableObject) observed).isModified())
			this.setSubObjectModified(observed);
	}

	/**
	 * Override this method if you are trying to modify behavior. This update sets subObjectModified to true and passes the sub
	 * object along to the next level indicating that it is the modified object if it is an instance of PersistableObject. Otherwise
	 * it sets the modified flag of this object which will pass this object back through notifyObservers(obj)
	 * 
	 * @param observed -
	 *            The object performing the notification
	 * @param obj -
	 *            if present, the object that has been modified: i.e. isModified() returns true;
	 */
	public void update(Observable observed, Object obj) {
		// determine if we are passing a modified PersistableObject
		// up the chain.
		if (obj instanceof ObservableObject && ((ObservableObject) obj).isModified()) {
			this.setSubObjectModified(obj);
		} else {
			// We are changing and need to reflect that upwards
			this.setModified(true);
		}
	}
}
