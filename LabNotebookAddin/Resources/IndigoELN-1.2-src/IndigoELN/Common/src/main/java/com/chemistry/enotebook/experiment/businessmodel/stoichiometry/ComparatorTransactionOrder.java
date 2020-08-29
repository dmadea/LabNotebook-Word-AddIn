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
package com.chemistry.enotebook.experiment.businessmodel.stoichiometry;

import com.chemistry.enotebook.experiment.datamodel.batch.AbstractBatch;

import java.util.Comparator;

/**
 * 
 * Created on: Aug 25, 2005
 * 
 * 
 */
public class ComparatorTransactionOrder implements Comparator {

    /**
     * 
     */
    public ComparatorTransactionOrder() {
        super();
    }

    public int compare(Object o1, Object o2) {
        int result = 0;
        if (o1 != null && o1 instanceof AbstractBatch) {
            result = 1;
            if (o2 != null && o2 instanceof AbstractBatch) {
                result = ((AbstractBatch) o1).getTransactionOrder() - ((AbstractBatch) o2).getTransactionOrder();
            }
        } else if (o2 != null && o2 instanceof AbstractBatch) 
            result = -1;
        
        return result;
    }
}
