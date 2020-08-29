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
 * DeepCopy.java
 * 
 * Created on Nov 22, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.common.interfaces;

/**
 * 
 * @date Nov 22, 2004
 */
public interface DeepCopy {
	/**
	 * Performs a clone on the object and all subobjects such that there is no reference to the object being cloned.
	 * 
	 * Does not set Modified flag to true because no one is listening at the time of the clone. Calling object's responsibility to
	 * set modified flag.
	 * 
	 * @return Object of the same type as that being cloned
	 */
	public void deepCopy(Object resource);
}
