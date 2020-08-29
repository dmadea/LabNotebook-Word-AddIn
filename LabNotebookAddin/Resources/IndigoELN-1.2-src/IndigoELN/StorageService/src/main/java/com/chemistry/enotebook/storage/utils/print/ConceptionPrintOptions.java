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
/*
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.storage.utils.print;

import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.datamodel.user.UserPreferenceException;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 *
 * This handles saving the user selected options for the next time he opens the dialog; 
 * saves between sessions also
 * 
 * TODO Add Class Information
 */
public class ConceptionPrintOptions implements PrintOptions
{
	boolean includeConceptDetails = false;
	boolean includeReactionTarget = false;
	boolean includeUtilityInventionNotes = false;
	boolean includePreferredCompounds = false;
	boolean includeIpRelatedAttachments = false;
	boolean includeSignatureFooter = false;
	
	public static boolean NOPREVIEW = false;
	public static boolean PREVIEW = true;
	private static String YES = "YES";
	private static String NO = "NO";

	public NotebookUser loadOptions(NotebookUser usr)
		throws UserPreferenceException
	{
		String val = usr.getPreference(NotebookUser.PREF_PrintConceptionConceptDetails);
		includeConceptDetails = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintConceptionReactionTarget);
		includeReactionTarget = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintConceptionUtilityInventionNotes);
		includeUtilityInventionNotes = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintConceptionPreferredCompounds);
		includePreferredCompounds = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintConceptionIPRelatedAttachments);
		includeIpRelatedAttachments = (val != null && val.equals(YES));

		val = usr.getPreference(NotebookUser.PREF_PrintConceptionSignatureFooter);
		includeSignatureFooter = (val != null && val.equals(YES));
		
		return usr;
	}
	
	public NotebookUser saveOptions(NotebookUser usr) 
		throws UserPreferenceException
	{
		String val = (includeConceptDetails) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintConceptionConceptDetails, val);

		val = (includeReactionTarget) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintConceptionReactionTarget, val);

		val = (includeUtilityInventionNotes) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintConceptionUtilityInventionNotes, val);

		val = (includePreferredCompounds) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintConceptionPreferredCompounds, val);

		val = (includeIpRelatedAttachments) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintConceptionIPRelatedAttachments, val);

		val = (includeSignatureFooter) ? YES : NO;
		usr.setPreference(NotebookUser.PREF_PrintConceptionSignatureFooter, val);

		usr.updateUserPrefs();
		
		return usr;
	}
	
	public void setOptionsRequiredForIP() {
		this.includeConceptDetails = true;
		this.includeReactionTarget = true;
		this.includeIpRelatedAttachments = true;
		this.includePreferredCompounds = true;
		this.includeUtilityInventionNotes = true;
//		this.includeSignatureFooter = true;
	}

	public Map getOptions() {
		Map optionsMap = new HashMap();
		optionsMap.put(PrintSetupConstants.INCLUDE_DETAILS, this.includeConceptDetails ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_REACTION, this.includeReactionTarget ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_ATTACHMENTS, this.includeIpRelatedAttachments ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_COMPOUNDS, this.includePreferredCompounds ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_FOOTER, this.includeSignatureFooter ? "true" : "false");
		optionsMap.put(PrintSetupConstants.INCLUDE_PROCEDURE, this.includeUtilityInventionNotes ? "true" : "false");
		return optionsMap;
	}
}
