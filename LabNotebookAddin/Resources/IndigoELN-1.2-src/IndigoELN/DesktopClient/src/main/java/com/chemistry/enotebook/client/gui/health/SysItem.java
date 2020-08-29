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
 * Created on 17-Dec-2004
 */
package com.chemistry.enotebook.client.gui.health;

/**
 * 
 */
public class SysItem {
	/**
	 * The class hold the infomation about a specific part of the CeN system for the health check dialog
	 */
	private String itemName; // name of the system item
	private boolean itemStatus; // status of the system item - true - ok, false
	// - dead
	private SysItemImpact _itemImpact; // system item impact on whole system?
	private SysItemType _itemType; // type of system item?

	public SysItem() {
		// super();
	}

	// sorry about the double _ 's - i'm a bad man
	public SysItem(String _itemName, boolean _itemStatus, SysItemImpact __itemImpact, SysItemType __itemType) {
		itemName = _itemName;
		itemStatus = _itemStatus;
		_itemImpact = __itemImpact;
		_itemType = __itemType;
	}

	/**
	 * @return Returns the _itemImpact.
	 */
	public SysItemImpact get_itemImpact() {
		return _itemImpact;
	}

	/**
	 * @param impact
	 *            The _itemImpact to set.
	 */
	public void set_itemImpact(SysItemImpact impact) {
		_itemImpact = impact;
	}

	/**
	 * @return Returns the _itemType.
	 */
	public SysItemType get_itemType() {
		return _itemType;
	}

	/**
	 * @param type
	 *            The _itemType to set.
	 */
	public void set_itemType(SysItemType type) {
		_itemType = type;
	}

	/**
	 * @return Returns the itemName.
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName
	 *            The itemName to set.
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return Returns the itemStatus.
	 */
	public boolean isItemStatus() {
		return itemStatus;
	}

	/**
	 * @param itemStatus
	 *            The itemStatus to set.
	 */
	public void setItemStatus(boolean itemStatus) {
		this.itemStatus = itemStatus;
	}
}
