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
package com.chemistry.enotebook.experiment.common;

import com.chemistry.enotebook.experiment.common.interfaces.IPersistableObject;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;

import java.util.Observable;

/**
 * 
 * 
 *
 */
public class PersistableObject extends ObservableObject implements IPersistableObject {

	private static final long serialVersionUID = 8839793476169060355L;

	private String _key;

	private transient boolean _existsInDB = false;
	private transient boolean _cachedLocally = false;
	private transient boolean _deleted = false;
	private transient boolean _saving = false;

	public PersistableObject() {
		_key = GUIDUtil.generateGUID(this);
	}

	public PersistableObject(String key) {
		this._key = key;
	}

	public String getKey() {
		return _key;
	}

	public void setKey(String key) {
		this._key = key;
	}

	public boolean existsInDB() {
		return _existsInDB;
	}

	public boolean isCachedLocally() {
		return _cachedLocally;
	}

	public boolean isDeleted() {
		return _deleted;
	}

	public boolean isSaving() {
		return _saving;
	}

	public void setExistsInDB(boolean existsInDB) {
		_existsInDB = existsInDB;
	}

	public void setCachedLocally(boolean isCached) {
		_cachedLocally = isCached;
	}

	public void setDeletedFlag(boolean isDeleted) {
		_deleted = isDeleted;
	}

	public void setSavingFlag(boolean isSaving) {
		_saving = isSaving;
	}

	public void dispose() throws Throwable {
		super.dispose();
	}

	//
	// Overrides and Implemented Methods
	//

	/**
	 * We need to trap persistable object changes so that we can have a valid list for save/restore.
	 * 
	 */
	public void setSubObjectModified(Object modifiedObj) {
		if (!(modifiedObj instanceof PersistableObject))
			setModified(true);
		else
			super.setSubObjectModified(modifiedObj);
	}

	/**
	 * Calls setSubObjectModified which passes the observed object back as the modified object.
	 * 
	 * @param observed -
	 *            object that has been modified: i.e. isModified() returns true
	 */
	public void update(Observable observed) {
		if (observed instanceof PersistableObject && ((PersistableObject) observed).isModified())
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
		if (obj instanceof PersistableObject && ((PersistableObject) obj).isModified()) {
			this.setSubObjectModified(obj);
		} else {
			// We are changing and need to reflect that upwards
			this.setModified(true);
		}
	}

	protected boolean areStringsDifferent(String arg1, String arg2) {
		if ((arg1 == null && arg2 != null) || (arg1 != null && arg2 == null))
			return true;

		if (arg1 != null && arg2 != null)
			return !arg1.equals(arg2);
		else
			return false;
	}
}
