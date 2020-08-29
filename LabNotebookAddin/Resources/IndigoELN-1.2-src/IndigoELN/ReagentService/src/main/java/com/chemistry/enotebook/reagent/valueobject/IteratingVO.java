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
 * Created on Aug 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.valueobject;

import java.io.Serializable;

/**
 * 
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IteratingVO 
	implements Serializable
{
	static final long serialVersionUID = -3556025260300782571L;
	
	private int lastPosition = -1;
	private int reagentsTotal = -1;
	public int iteratingSize = 50;
	private boolean hasMore = false;
	
	public boolean ifHasMore() {
		if (reagentsTotal - lastPosition > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public int calculateLastPos() {
		if (reagentsTotal - lastPosition - iteratingSize > 0) {
			return lastPosition + iteratingSize;
		} else {
			return reagentsTotal;
		}
	}
	
	public void setLastPosition(int lPos) {
		lastPosition = lPos;
	}
	
	public void setIteratingSize(int iSize) {
		iteratingSize = iSize;
	}
	
	public void setReagentsTotal( int rTotal) {
		reagentsTotal = rTotal;
	}
	
	public void setHasMore(boolean more) {
		hasMore = more;
	}
	
	public int getLastPosition() {
		return lastPosition;
	}
	
	public int getReagentsTotal( ) {
		return reagentsTotal;
	}
	
	public int getIteratingSize( ) {
		return iteratingSize;
	}
	
	public boolean getHasMore( ) {
		return hasMore;
	}
}
