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
package com.chemistry.enotebook.scheduler.externalcollaborator;

import com.chemistry.enotebook.extcol.ExternalCollaboratorService;
import com.chemistry.enotebook.extcol.ExternalCollaboratorServiceFactory;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

public class ExternalCollaboratorJob {

	private static Log log = LogFactory.getLog(ExternalCollaboratorJob.class);

    @Scheduled(fixedRate = 3L * 60L * 60L * 1000L)
	public void execute() {
		log.debug("execute(JobExecutionContext) start -- jobId = ExternalCollaborator");

		try {
			String startDate = DateConversionUtil.getPreviousWeekDayInExternalCollaboratorFormat(new Date());
			String endDate = DateConversionUtil.getDateInExternalCollaboratorFormat(new Date());
			 
			log.info("Start date: " + startDate);
			log.info("End date:   " + endDate);
			 
			ExternalCollaboratorService externalCollaboratorservices = ExternalCollaboratorServiceFactory.getService();
			 
			int[] requestIDs = externalCollaboratorservices.getExternalColaboratorsRequestIDs("C", startDate, endDate);
			
			if (!ArrayUtils.isEmpty(requestIDs)) {
				log.info("Got " + requestIDs.length + " requests from ExternalCollaborator");
				 
				ExternalCollaboratorExecutor producer = new ExternalCollaboratorExecutor();
				producer.postMessageToExternalCollaboratorQueue(requestIDs);
			} else {
				log.info("No RequestID returned from ExternalCollaborator for startdate: " + startDate + " End date: " + endDate); 
    		}
		} catch (Exception e) {
			log.error("Error executing ExternalCollaborator job: ", e);
		}        

		log.debug("execute(JobExecutionContext) end");
    }
}
