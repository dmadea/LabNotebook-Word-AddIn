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
package com.chemistry.enotebook.experiment.utils;

import com.chemistry.enotebook.domain.AmountModel;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;

public class BatchUtils {

	public static boolean isUnitOnlyChanged(Amount from, Amount to) {
		boolean result = false;
		result = (CeNNumberUtils.doubleEquals(from.GetValueInStdUnitsAsDouble(), to.GetValueInStdUnitsAsDouble()) && !(from
				.getUnit().equals(to.getUnit())));

		return result;
	}

	public static double calcMolesWithEquivalents(Amount reagentMoles, Amount equiv) {
		if (reagentMoles.GetValueInStdUnitsAsDouble() == 0.0) {
			return reagentMoles.GetValueInStdUnitsAsDouble();
		}
		double newMoles = reagentMoles.GetValueInStdUnitsAsDouble() * equiv.GetValueInStdUnitsAsDouble();
		return newMoles;

	}

	public static double calcEquivalentsWithMoles(Amount moles, Amount reagentMoles) {
		if (reagentMoles.GetValueInStdUnitsAsDouble() == 0.0) {
			return moles.GetValueInStdUnitsAsDouble();
		}
		double newEquiv = moles.GetValueInStdUnitsAsDouble() / reagentMoles.GetValueInStdUnitsAsDouble();
		return newEquiv;

	}
	
	public static boolean isUnitOnlyChanged(AmountModel from, AmountModel to) {
		boolean result = false;
		result = (CeNNumberUtils.doubleEquals(from.GetValueInStdUnitsAsDouble(), to.GetValueInStdUnitsAsDouble()) && !(from
				.getUnit().equals(to.getUnit())));

		return result;
	}

	public static double calcMolesWithEquivalents(AmountModel reagentMoles, AmountModel equiv) {
		if (reagentMoles.GetValueInStdUnitsAsDouble() == 0.0) {
			return reagentMoles.GetValueInStdUnitsAsDouble();
		}
		double newMoles = reagentMoles.GetValueInStdUnitsAsDouble() * equiv.GetValueInStdUnitsAsDouble();
		return newMoles;

	}

	public static double calcEquivalentsWithMoles(AmountModel moles, AmountModel reagentMoles) {
		if (reagentMoles.GetValueInStdUnitsAsDouble() == 0.0) {
			return moles.GetValueInStdUnitsAsDouble();
		}
		double newEquiv = moles.GetValueInStdUnitsAsDouble() / reagentMoles.GetValueInStdUnitsAsDouble();
		return newEquiv;

	}

}
