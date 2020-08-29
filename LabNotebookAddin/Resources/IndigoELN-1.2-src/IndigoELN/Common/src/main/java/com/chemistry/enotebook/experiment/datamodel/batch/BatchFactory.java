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
package com.chemistry.enotebook.experiment.datamodel.batch;

/**
 * 
 * 
 *
 */
public class BatchFactory {
	private BatchFactory() {
	}

	public static AbstractBatch getBatch(BatchType batchType) 
		throws InvalidBatchTypeException 
	{
		return getBatch(batchType, false);
	}
	
	public static AbstractBatch getBatch(BatchType batchType, boolean loading) 
		throws InvalidBatchTypeException
	{
		AbstractBatch result = null;
		// Need to catch those areas that were setup as Starting Materials
		if (batchType.equals(BatchType.START_MTRL)) {
			batchType = BatchType.REACTANT;
		}
		if (batchType.equals(BatchType.REAGENT) || batchType.equals(BatchType.SOLVENT) || batchType.equals(BatchType.REACTANT)) {
			result = new ReagentBatch();
			result.setType(batchType);
		}
		if (batchType.equals(BatchType.INTENDED_PRODUCT) || batchType.equals(BatchType.ACTUAL_PRODUCT)) {
			result = new ProductBatch();
			if (batchType.equals(BatchType.ACTUAL_PRODUCT)) {
				result.setType(batchType);
				result.getWeightAmount().setCalculated(true);
			}
		}
		if (!loading) 
			result.setSaltEquivsSet(false);
		if (result == null)
			throw new InvalidBatchTypeException("BatchType value not found: " + batchType);
		return result;
	}
}
