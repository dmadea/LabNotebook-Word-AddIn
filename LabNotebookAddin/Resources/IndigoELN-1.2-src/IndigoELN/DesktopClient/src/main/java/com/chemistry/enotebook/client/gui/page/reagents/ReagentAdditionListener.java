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
 * ReagentAdditionListener.java
 * 
 * Created on Nov 9, 2004
 *
 * 
 */
package com.chemistry.enotebook.client.gui.page.reagents;

import com.chemistry.enotebook.domain.MonomerBatchModel;

import java.util.List;

/**
 * 
 * @date Nov 9, 2004
 */
public interface ReagentAdditionListener {
	/**
	 * List will be of ReagentBatch objects
	 * 
	 * @param reagentsToAdd
	 */
	public void addReagentsFromList(List<MonomerBatchModel> reagentsToAdd);
	public void addSolventsFromList(List<MonomerBatchModel> reagentsToAdd);
}
