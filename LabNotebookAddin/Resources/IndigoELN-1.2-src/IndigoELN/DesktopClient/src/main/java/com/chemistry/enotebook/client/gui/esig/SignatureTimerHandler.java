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
/**
 * 
 */
package com.chemistry.enotebook.client.gui.esig;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.esig.delegate.SignatureDelegate;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.storage.SignaturePageVO;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.utils.SwingWorker;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 */
public class SignatureTimerHandler 
{
    private static final Log log = LogFactory.getLog(SignatureHandler.class);

    private static final int initial = (60 * 1000) * 2;		// 2 minute delayed start
	
    private int interval = (60 * 1000) * 10;   				// every 10 minutes

    private Timer checkTimer = null;	
	private static SignatureTimerHandler sigTimer = null;
	
	private static boolean tellmelater = false;
	
	private boolean executeImmediate = false;
	
	
	private SignatureTimerHandler(boolean runNow)
	{
		interval = (60 * 1000) * 10;
		try {
			String val = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_ESIG_TIMER);
			if (val != null && val.length() > 0) interval = Integer.parseInt(val) * 1000 * 60;
		} catch (Exception e) { /* Ignored */ }
		
		checkTimer = new Timer(interval, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				timerActionPerformed();
			}
		});
		
		executeImmediate = runNow;
		if (executeImmediate)
			checkTimer.setInitialDelay(1000);   // 1 second wait
		else
			checkTimer.setInitialDelay(initial);
		checkTimer.setDelay(interval);
		
		checkTimer.start();
	}
	
	private void stopTimer()
	{
		checkTimer.stop();
	}

	private void timerActionPerformed()
	{
		// disable the timer so no more will fire while we process
		checkTimer.stop();
		checkTimer.setInitialDelay(interval);
    			
      	SwingWorker worker = new SwingWorker() {
     		public Object construct() {
				boolean result = false;
				
      			// Check for things to sign
     			if (!executeImmediate && MasterController.getUser().isEsigEnabled() && !tellmelater) {
     				checkForThingsToSign();
     			}
     	     	executeImmediate = false;

     			try {
					// Need to determine if there are any experiments that need to be processed
					StorageDelegate cenDel = ServiceController.getStorageDelegate(MasterController.getUser().getSessionIdentifier());
					List<SignaturePageVO> sigInfo = new ArrayList<SignaturePageVO>();
					List<SignaturePageVO> list = cenDel.getExperimentsBeingSigned(MasterController.getUser().getNTUserID());
					if (list != null && list.size() > 0) {
						sigInfo.addAll(list);
					}
					// If we are a supervisor we have responsibility for others getting their experiments signed.
					for (Iterator<String> it = getListOfReportingChemists(); it.hasNext(); ) {
						list = cenDel.getExperimentsBeingSigned(it.next());
						if (list != null && list.size() > 0) {
							sigInfo.addAll(list);
						}
					}
					
					if (sigInfo != null && sigInfo.size() > 0) {
						int count = sigInfo.size();
						if (count > 0) {
							result = true;
						}
					}
				} catch (Exception e) {
					CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), 
								"An error occured while attempting to check the signing status of your experiments.\n" +
								"This check will now be disabled until your next login to prevent further error reporting.", e);
					result = false;
				}
				
				return new Boolean(result);
     		}
     	    
     		public void finished() {
    			try {
     				Boolean b = (Boolean)get();
     				if (MasterController.getUser().isEsigEnabled() && b.booleanValue())
     					checkTimer.start();
     				else
     					stopSignatureTimer();
     			} catch (Exception e) { }
     	    }
     	};
     	worker.start();
	}
	
	private Iterator<String> getListOfReportingChemists() {
		ArrayList<String> list = new ArrayList<String>();
		String chemists = null;
		try {
			chemists = MasterController.getUser().getPreference(NotebookUser.PREF_SupervisorFor);
		} catch (Exception e) {
			log.warn("Failure retrieving reporting chemists in signature timer.", e);
		}
		if (StringUtils.isNotBlank(chemists)) {
			String[] sList = chemists.split(",");
			if (sList != null)
				for (int i=0; i < sList.length; i++) {
					list.add(sList[i].trim());
				}
		}
		return list.iterator();
	}
	
	private void checkForThingsToSign()
	{
		try {
			SignatureDelegate ussiDel = new SignatureDelegate();
			
			SignatureHandler sigHandler = new SignatureHandler();
			if (ussiDel.haveThingsToSign(MasterController.getUser().getNTUserID())) {
				Object[] options = { "Tell Me Later", "Goto Signature Service" };
				int result = JOptionPane.showOptionDialog(MasterController.getGUIComponent(),
				                                          "There are documents requiring your Signature.\n", "Documents Ready to Sign", 
				                                          JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, null);
				if (result == JOptionPane.NO_OPTION) {
					sigHandler.launchUssiSignatureQueue();
					tellmelater = true;
				} else if (result == JOptionPane.YES_OPTION) { 
					tellmelater = true;
				}
			}
		} catch (Throwable e) {
			CeNErrorHandler.getInstance().logExceptionMsg(MasterController.getGUIComponent(), "An error occured while attempting to check for experiments that you need to sign.\nThis check will now be disabled until your next login to prevent further error reporting.", e);
			tellmelater = true;
		}
	}
	
	public static synchronized void startSignatureTimer(boolean executeImmediate)
	{
		if (!MasterController.getUser().isEsigEnabled() || 
			(sigTimer != null && !executeImmediate)) 
			return;
		
		try {
			SignatureDelegate sigDelegate = new SignatureDelegate();
			if (!sigDelegate.isServiceAvailable()) return;
		} catch (Exception e) { }

		if (sigTimer != null) stopSignatureTimer();
		
		sigTimer = new SignatureTimerHandler(executeImmediate);
	}
	
	public static synchronized void stopSignatureTimer()
	{
		if (sigTimer == null) return;
		
		sigTimer.stopTimer();
		sigTimer = null;
	}
}
