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
package com.chemistry.enotebook.publisher.clean;

import com.chemistry.enotebook.publisher.PublisherService;
import com.chemistry.enotebook.publisher.classes.PublishEntityInfo;
import com.chemistry.enotebook.publisher.exceptions.PublisherException;

import java.util.List;

public class PublisherServiceCleanImpl implements PublisherService {

	public boolean isAvailable() {
		return true;
	}

	public void publish(List<PublishEntityInfo> strucInsertOrUpdateList,
			EntityType targetType) throws PublisherException {
		// do nothing
	}
}
