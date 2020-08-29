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
package com.chemistry.enotebook.client.gui.page.batch.events;

import java.util.EventListener;

public interface PlatesCreatedEventListener extends EventListener {
	public void newProductPlatesCreated(ProductBatchPlateCreatedEvent event);

	public void prodcutPlatesRemoved(ProductBatchPlateCreatedEvent event);

	public void newMonomerPlatesCreated(MonomerBatchPlateCreatedEvent event);

	public void monomerPlatesRemoved(MonomerBatchPlateCreatedEvent event);

	public void newRegisteredPlatesCreated(RegisteredPlateCreatedEvent event);

	public void registeredPlatesRemoved(RegisteredPlateCreatedEvent event);

}
