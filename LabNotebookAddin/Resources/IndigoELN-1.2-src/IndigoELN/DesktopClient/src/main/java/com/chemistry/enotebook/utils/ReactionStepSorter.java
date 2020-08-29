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

import com.chemistry.enotebook.domain.ReactionStepModel;

import java.util.Comparator;

public class ReactionStepSorter implements Comparator<ReactionStepModel> {


	public int compare(ReactionStepModel m1, ReactionStepModel m2)
	{
		int result = 0;
//		if((o1 instanceof ReactionStepModel) && (o2 instanceof ReactionStepModel))
//		{
//			ReactionStepModel m1 = (ReactionStepModel) o1;
//			ReactionStepModel m2 = (ReactionStepModel) o2;
			if(m1.getStepNumber() > m2.getStepNumber())
				result = 1;
			else
				if(m2.getStepNumber() > m1.getStepNumber())
					result = -1;
//		}
		return result;
	}

}
