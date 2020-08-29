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

import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.experiment.common.units.UnitType;

public class CeNFormatter {

	public static AmountModel formatMolecularWeight(double amount) {
		double dmw = Math.floor(amount);
		int imw = (int) dmw;
		int fixedFigs = 5; 				// #.###
		if (imw > 9) fixedFigs++; 		// ##.###
		if (imw > 99) fixedFigs++; 		// ###.###
		if (imw > 999) fixedFigs++; 	// ####.###
		AmountModel mwAmountModel = new AmountModel(UnitType.SCALAR);  
		mwAmountModel.setValue(amount);
		mwAmountModel.setSigDigits(fixedFigs - 1);
		mwAmountModel.setFixedFigs(fixedFigs);
		return mwAmountModel;
	}
}
