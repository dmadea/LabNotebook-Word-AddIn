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
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;

public class CreateExperimentTest {
	private static final int times = 10;
	private static final int users = 1;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		CreateExperimentTest client = new CreateExperimentTest();
		for(int i=0;i<users;i++) {
			client.launchSwingWorker(i);
		}

	}
	
	
	public void launchSwingWorker(int user) throws Exception{
		for(int i=0;i<times;i++) {
			startWorker("SP0000000350v00","27071975","User-"+user,"Worker-"+i);
			//startWorker("SP0000000303v00","27071975","User-"+user,"Worker-"+i);
		}
	}
	
	protected void startWorker(final String spid, final String notebook,final String userId, final String threadId)
	{
		final Thread worker = new Thread() {
			public void run() {
				try{
					long starttime = System.currentTimeMillis();
					System.out.println(userId+":"+threadId+">>Create Started.");
					SessionIdentifier sidentifier = new SessionIdentifier("SITE1", "USER", "TvBUAnOdCip8BJoNkSuFFX8/dZXMjWXs", true);
					sidentifier.setThreadId(threadId);
					StorageDelegate ssi = new StorageDelegate();
					NotebookPageModel page = ssi.createParallelExperiment(spid,notebook,null);
					System.out.println(userId+":"+threadId+">>Created Page "+page.getNbRef().getNbRef());
					//CommonUtils.printNBPageData(page);
					long endtime = (System.currentTimeMillis()-starttime)/1000;
					System.out.println(userId+":"+threadId+">>Creating Completed :"+endtime+"secs");
				}catch(Exception error){
					error.printStackTrace();
				}
			}

		};

		worker.start();

	}	  
}
