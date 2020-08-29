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
package com.chemistry.enotebook.publisher;

import com.chemistry.enotebook.publisher.classes.PublishEntityInfo;
import com.chemistry.enotebook.publisher.exceptions.PublisherException;

import java.util.List;

/**
 * Interface for external Publisher service
 */
public interface PublisherService {

	/**
	 * Check service health
	 * 
	 * @return true if service available, false if service unavailable
	 */
	public boolean isAvailable();

	/**
	 * Publish given structures
	 * 
	 * @param strucInsertOrUpdateList
	 *            structures list
	 * @param targetType
	 *            reaction or structure
	 * @throws PublisherException
	 */
	public void publish(List<PublishEntityInfo> strucInsertOrUpdateList,
			EntityType targetType) throws PublisherException;

	/**
	 * Type of structures to publish: Reaction (RXN) or Structure (STR)
	 */
	public static enum EntityType {
		RXN, STR
	}
}
