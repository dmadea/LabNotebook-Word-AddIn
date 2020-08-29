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
package com.chemistry.enotebook.storage.utils;

import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.publisher.PublisherFactory;
import com.chemistry.enotebook.publisher.PublisherService;
import com.chemistry.enotebook.publisher.PublisherService.EntityType;
import com.chemistry.enotebook.publisher.classes.PublishEntityInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for communication to java message Q.
 * 
 */

public class JMSUtils {
	
	private static final Log log = LogFactory.getLog(JMSUtils.class);
	
    public JMSUtils() {
    }
    
    public void postMessageToCUSQueue(List<PublishEntityInfo> cusList) throws Exception {
    	publishStructure(cusList);
    }
    
    private void publishStructure(List<PublishEntityInfo> cusList) throws NoClassDefFoundError, Exception{
		String notebookRef = "Warning: No Notebook Ref found in first object of list";
		if(cusList != null && cusList.isEmpty() == false && cusList.get(0) != null) {
			notebookRef = cusList.get(0).getNotebookRef();
		}
		log.debug("publishToCUS().enter notebook ref = " + notebookRef);
		PublisherService publisher = PublisherFactory.getService();
		if(publisher != null && publisher.isAvailable()){
			List<PublishEntityInfo> strucList = new ArrayList<PublishEntityInfo>();
			List<PublishEntityInfo> rxnList = new ArrayList<PublishEntityInfo>();
			StopWatch watch = new StopWatch();
			watch.start("UpdateServiceMDB.publishToCUS() for notebook reference: " + notebookRef);
			for(PublishEntityInfo cusObj :  cusList){
				if (cusObj != null) {
					if(cusObj.getStructureType().equals(CeNConstants.PUBLISHER_STRUCTURE_TYPE_BATCH)) {
						strucList.add(cusObj);
					} 
					else if(cusObj.getStructureType().equals(CeNConstants.PUBLISHER_STRUCTURE_TYPE_REACTION)) {
						rxnList.add(cusObj);
					}
				}
			}
			publisher.publish(strucList, EntityType.STR);
			log.debug(strucList.size() + " STRUCTURES published for nbRef = " + notebookRef);
			
			publisher.publish(rxnList, EntityType.RXN);			
			log.debug(rxnList.size() + " REACIONS published for nbRef = " + notebookRef);
			
			watch.stop();
		} else {
			log.debug("Publisher is down");
		}
		log.debug("publishStructure().exit");
	}
}
