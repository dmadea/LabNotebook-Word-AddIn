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
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.domain.MonomerBatchModel;

import java.util.List;

public class BatchesPlatesLookupUtil {
	
	public static MonomerBatchModel getMatchingMonomerBatch(String batchKey ,List monomersList)
	{
		if(monomersList == null ) return null;
		int size = monomersList.size();
		for(int i = 0 ; i < size ; i ++)
		{
			MonomerBatchModel model = (MonomerBatchModel) monomersList.get(i);
			if(model.getKey().compareToIgnoreCase(batchKey) == 0)
			{
			  return model;
			}
		}
		
		return null;
	}

}
