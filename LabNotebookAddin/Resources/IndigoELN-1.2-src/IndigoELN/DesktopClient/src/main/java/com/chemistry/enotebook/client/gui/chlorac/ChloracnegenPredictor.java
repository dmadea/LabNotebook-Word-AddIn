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
package com.chemistry.enotebook.client.gui.chlorac;

import com.chemistry.enotebook.chloracnegen.classes.Structure;
import com.chemistry.enotebook.chloracnegen.delegate.ChloracnegenDelegateException;
import com.chemistry.enotebook.chloracnegen.delegate.ChloracnegenServiceDelegate;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ChloracnegenPredictor {

	private static final Log log = LogFactory.getLog(ChloracnegenPredictor.class);
	
	private static ChloracnegenPredictor _instance = null;
	// url for more info (db prop)
	// flag wether to launch chloracnegen test at all or not to (DB prop)
	private static boolean runChloracnegenTest = true;
	static {
		try {
			runChloracnegenTest = Boolean.valueOf(CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_ALERT_ENABLED)).booleanValue();
		} catch (Exception e) {
			log.error("Failed to initialize chloracnegenURL. System properties are not currently available from server.", e);
		}
	}

	public static ChloracnegenPredictor getInstance() {
		if (_instance == null)
			createInstance();
		return _instance;
	}

	// form of double-checked locking
	private static synchronized void createInstance() {
		if (_instance == null)
			_instance = new ChloracnegenPredictor();
	}

	private ChloracnegenPredictor() {}

	/**
	 * This method runs Chloracnegen test for a structure(mol string format).Also returns a
	 * com.chemistry.enotebook.calculation.Structure which has valid test results. Runs Sync on the serverside.
	 */
	public Structure checkChloracnegen(String stuctureMol) {
		log.debug("ChloracnegenPredictor.runCloracnegenProtocolTest().enter");
		// create Structure object the service expects
		Structure struct = null; // Object to hold structure and results
		// get list of chloracnegen protocols
		struct = new Structure(stuctureMol);
		try {
			// create CaluclationDelegate
			ChloracnegenServiceDelegate csd = new ChloracnegenServiceDelegate();
			// call the service using delegate
			struct = csd.checkChloracnegen(struct, MasterController.getUser().getSessionIdentifier());
		} catch (ChloracnegenDelegateException cde) {
			log.error("Failed to get Chloracnegen test structure because the service was unavailable.", cde);
		} catch (Exception e) {// catching generic high level exception
			log.error("Failed to get Chloracnegen test structure.\n" + e.toString(), e); // for debug on client side
			CeNErrorHandler.getInstance().logExceptionMsg(null,
					"Error running Calculation serivce.\nCould not make Chloracnegen test for structure.", e);
		}
		log.debug("ChloracnegenPredictor.runCloracnegenProtocolTest().exit");
		return struct;
	}
}
