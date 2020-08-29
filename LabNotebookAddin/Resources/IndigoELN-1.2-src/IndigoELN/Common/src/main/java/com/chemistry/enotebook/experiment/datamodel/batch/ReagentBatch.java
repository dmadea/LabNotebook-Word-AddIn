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
 * IBatch.java
 * 
 * Created on Aug 17, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.datamodel.batch;


public class ReagentBatch extends AbstractBatch
{
	private static final long serialVersionUID = -2541772557855273924L;
	
	private BatchType type = BatchType.REAGENT;
	
	public boolean isEditable() { return !isLoading(); }
	
	public BatchType getType() { return type; }
	public void setType(BatchType newType) { 
	    if (!type.equals(newType) && BatchType.REACTANT_VALUES.contains(newType)) {
	        type = newType; 
	        setModified(true);
	    }//endif
	}

    public boolean canBeLimiting() {
        return !(getType().equals(BatchType.SOLVENT) || 
                (getVolumeAmount().doubleValue() > getVolumeAmount().getDefaultValue() && !isVolumeConnectedToMass()));
    }
	public int compareTo(Object o)
	{
		int result = 0;
		if (o != null) {
			result = hashCode() - o.hashCode();
			if (o instanceof ReagentBatch) {
			    ReagentBatch ab = (ReagentBatch) o;
				if ((type.getOrdinal() & BatchType.ALL_STOICH_COMPONENTS_ORDINAL) > 0) {
				    result = getTransactionOrder() - ab.getTransactionOrder();
				}
			}
		}
		return result;
	}
	

	/**
	 * Copies target batch's information over to the this batch.  Compound information
	 * is referenced and not copied.  Convenience method for copy(AbstractBatch, boolean).
	 * 
	 * @param target - batch to copy informtion to.
	 */
	public void deepCopy(Object source) { deepCopy(source, false); }
	
	/**
	 * Copies target batch's information over to the this batch.  If the Compound
	 * object's information is to be copied and not just referenced, pass a true to 
	 * deepCopy.
	 *
	 * If you want a whole new object to be returned try clone() or deepClone().
	 * 
 	 * @param target - batch to copy informtion to.
	 * @param deepCopy - true if the Compound object is to be cloned or false if Compound
	 * 					 is to be referenced 
	 */
	public void deepCopy(Object source, boolean deepCopy) {
	    if (source != null && source instanceof ReagentBatch && !((AbstractBatch) source).isDeleted()) {
		    super.deepCopy(source, deepCopy);
		    type = ((ReagentBatch)source).type;
	    }
	}

    /**
     * Produces an object just like this one with no ties to the original object.  All values are
     * independent.
     */
	public Object deepClone() { return cloneThis(true); }
	
	/** 
	 * Produces an object just like this one with a reference to the Compound object
	 */
	public Object clone() { return cloneThis(false); }
	
	private ReagentBatch cloneThis(boolean deepCloneCompound) 
	{
	    ReagentBatch target = null;
	    if (!isDeleted()) {
		    target = new ReagentBatch();
		    target.deepCopy(this, deepCloneCompound);
	    }
		return target;
	}
}
