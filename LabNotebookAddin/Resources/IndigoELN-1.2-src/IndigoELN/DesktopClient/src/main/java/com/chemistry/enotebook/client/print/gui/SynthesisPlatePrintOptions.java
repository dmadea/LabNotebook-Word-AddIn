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
package com.chemistry.enotebook.client.print.gui;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class SynthesisPlatePrintOptions {

	private static String YES = "YES";
	private static String NO = "NO";
	
	boolean includeExperiment = false;
	boolean includeSynthesisPlates = false;
	boolean includeSynthesisPlatesAllSteps = false;
	boolean includeSynthesisPlatesFinalStep = false;
	boolean includeSynthesisPlatesSelectSteps = false;

	String synthesisPlatesSelectedStepsDesc = "";

	public Map<String, String> getOptions() {
		Map<String, String> optionsMap = new HashMap<String, String>();
		
		optionsMap.put(PrintSetupConstants.INCLUDE_EXPERIMENT, this.includeExperiment ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_NO_SYNTHESIS_PLATES, this.includeSynthesisPlates ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_SYNTHESIS_PLATES_FOR_ALL_STEPS, this.includeSynthesisPlatesAllSteps ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_FINAL_STEP_SYNTHESIS_PLATES, this.includeSynthesisPlatesFinalStep ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_SYNTHESIS_PLATES_STEPS, this.includeSynthesisPlatesSelectSteps ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDED_SYNTHESIS_PLATES_STEPS, this.synthesisPlatesSelectedStepsDesc);
		
		return optionsMap;
	}
	
	public void loadOptions() throws UserPreferenceException {
		String val = null;
		NotebookUser user = MasterController.getUser();
		
		val = user.getPreference(NotebookUser.PREF_PrintTypesExperiment);
		includeExperiment = StringUtils.equals(val, YES);
		
		val = user.getPreference(NotebookUser.PREF_PrintTypesSynthPlates);
		includeSynthesisPlates = StringUtils.equals(val, YES);
				
		val = user.getPreference(NotebookUser.PREF_PrintTypesSynthPlatesSteps);
		
		if (StringUtils.equals(val, NotebookUser.PREF_PrintTypesSynthPlatesAllSteps)) {
			includeSynthesisPlatesAllSteps = true;
		} else {
			includeSynthesisPlatesAllSteps = false;
		}
		
		if (StringUtils.equals(val, NotebookUser.PREF_PrintTypesSynthPlatesSelSteps)) {
			includeSynthesisPlatesSelectSteps = true;
		} else {
			includeSynthesisPlatesSelectSteps = false;
		}
		
		if (StringUtils.equals(val, NotebookUser.PREF_PrintTypesSynthPlatesFinalStep)) {
			includeSynthesisPlatesFinalStep = true;
		} else {
			includeSynthesisPlatesFinalStep = false;
		}
		
		if (!includeSynthesisPlatesSelectSteps && !includeSynthesisPlatesFinalStep) {
			includeSynthesisPlatesAllSteps = true;
		}
		
		val = user.getPreference(NotebookUser.PREF_PrintTypesSynthPlatesStepsText);
		synthesisPlatesSelectedStepsDesc = val;
	}
	
	public void saveOptions() throws UserPreferenceException {
		String val = null;
		NotebookUser user = MasterController.getUser();
		
		val = includeExperiment ? YES : NO;
		user.setPreference(NotebookUser.PREF_PrintTypesExperiment, val);
		
		val = includeSynthesisPlates ? YES : NO;
		user.setPreference(NotebookUser.PREF_PrintTypesSynthPlates, val);

		if (includeSynthesisPlatesAllSteps) {
			user.setPreference(NotebookUser.PREF_PrintTypesSynthPlatesSteps, NotebookUser.PREF_PrintTypesSynthPlatesAllSteps);
		}
		
		if (includeSynthesisPlatesSelectSteps) {
			user.setPreference(NotebookUser.PREF_PrintTypesSynthPlatesSteps, NotebookUser.PREF_PrintTypesSynthPlatesSelSteps);
		}
		
		if (includeSynthesisPlatesFinalStep) {
			user.setPreference(NotebookUser.PREF_PrintTypesSynthPlatesSteps, NotebookUser.PREF_PrintTypesSynthPlatesFinalStep);
		}
		
		if (!includeSynthesisPlatesSelectSteps && !includeSynthesisPlatesFinalStep) {
			user.setPreference(NotebookUser.PREF_PrintTypesSynthPlatesSteps, NotebookUser.PREF_PrintTypesSynthPlatesAllSteps);
		}
		
		val = synthesisPlatesSelectedStepsDesc;
		user.setPreference(NotebookUser.PREF_PrintTypesSynthPlatesStepsText, val);
		
//		Need to update prefs once in PrintExperimentSetup
//		user.updateUserPrefs();
	}
}
