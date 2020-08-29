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
 * BatchRegistrationListener.java
 * 
 * Created on May 6, 2005
 *
 * 
 */
package com.chemistry.enotebook.experiment.datamodel.batch;

import javax.swing.event.ChangeEvent;

/**
 * 
 * @date May 6, 2005
 */
public interface BatchRegistrationListener {
	public void batchRegistrationChanged(ChangeEvent e);
}
