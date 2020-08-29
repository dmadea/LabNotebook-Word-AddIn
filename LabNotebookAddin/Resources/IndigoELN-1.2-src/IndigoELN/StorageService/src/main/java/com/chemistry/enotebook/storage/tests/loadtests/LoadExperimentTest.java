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
package com.chemistry.enotebook.storage.tests.loadtests;

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;

/**
 * 
 */

/**
 * 
 *
 */
public class LoadExperimentTest {

	private static final int times = 10;
	private static final int users = 1;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		LoadExperimentTest client = new LoadExperimentTest();
		for(int i=0;i<users;i++) {
			client.launchSwingWorker(i);
		}

	}
	
	
	public void launchSwingWorker(int user) throws Exception{
		NotebookRef nbref = new NotebookRef();
		nbref.setNbNumber("27071975");
		nbref.setNbPage("0315");
		for(int i=0;i<times;i++) {
			startWorker(nbref,"User-"+user,"Worker-"+i);
		}
	}
	
	protected void startWorker(final NotebookRef nbref,final String userId, final String threadId)
	{
		final Thread worker = new Thread() {
			public void run() {
				try{
					long starttime = System.currentTimeMillis();
					System.out.println(userId+":"+threadId+">>Loading Started.");
					SessionIdentifier sidentifier = new SessionIdentifier("SITE1", "USER", "TvBUAnOdCip8BJoNkSuFFX8/dZXMjWXs", true);
					StorageDelegate ssi = new StorageDelegate();
					NotebookPageModel page = ssi.getNotebookPageExperimentInfo(nbref, sidentifier.getSiteCode(),null);//getNotebookPageExperimentInfo(nbref, sidentifier);
					System.out.println(userId+":"+threadId+">>Loaded Page "+page.getUserName()+" : "+page.getNbRef().getNbRef());
					//CommonUtils.printNBPageData(page);
					long endtime = (System.currentTimeMillis()-starttime)/1000;
					System.out.println(userId+":"+threadId+">>Loading Completed :"+endtime+"secs");
				}catch(Exception error){
					error.printStackTrace();
				}
			}

		};

		worker.start();

	}	  


}
