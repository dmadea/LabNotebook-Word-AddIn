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
package com.chemistry.enotebook.client.gui.page.stoichiometry;

import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.experiment.utils.SystemPropertyException;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.utils.BrowserLauncher;

/**
 * 
 * 
 */
public class MSDSSearchLauncher {
	private static String msdsUrl = null;
	// Following 2 variables combines with a search key to form full search
	// URL
	private final static String MSDS_SEARCH_URL_FIRST_PART = "?a=Search&key=";
	private final static String MSDS_SEARCH_URL_SECOND_PART = "&searchby=c&query_form=simple&p=1&rlen=100&param_reg=0&param_lang=";
	private final static String MSDS_SEARCH_URL_THIRD_PART = "&param_multi_lang=";
	private final static String MSDS_SEARCH_URL_FOURTH_PART = "&indexstatus=Y";
	// Default URL for MSDS Search
	private final static String MSDS_SEARCH_URL_DEFAULT = "/cgi-bin/msds_search.cgi.stub?There_is_fake_implementaion_of_service:/Notebook_Properties/Services/Ties/MSDSSearchUrl::msds_search";

	public MSDSSearchLauncher() {
		super();
		init();
	}

	private static void init() {
		String serverURL = PropertyReader.getHttpServiceUrl();
		
		try {
			msdsUrl = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_MSDS_SEARCH);
		} catch (SystemPropertyException error) {
			msdsUrl = MSDS_SEARCH_URL_DEFAULT;
		}

		msdsUrl = serverURL + msdsUrl;
	}

	public static void launchMSDSSearch(String searchString, String searchLang) {
		String finalUrl = null;
		init();
		if (searchString == null || searchString.equals("")) {
			BrowserLauncher.launchBrowser(msdsUrl);
		} else {
			finalUrl = msdsUrl + MSDS_SEARCH_URL_FIRST_PART + generateSearchKey(searchString) + MSDS_SEARCH_URL_SECOND_PART
					+ searchLang + MSDS_SEARCH_URL_THIRD_PART + searchLang + MSDS_SEARCH_URL_FOURTH_PART;
			// System.out.println("Launching IE with Following search link\n\n" + finalUrl);
			BrowserLauncher.launchBrowser(finalUrl);
			if (searchLang.equals("JA")) {
				finalUrl = msdsUrl + MSDS_SEARCH_URL_FIRST_PART + generateSearchKey(searchString) + MSDS_SEARCH_URL_SECOND_PART
						+ "EN" + MSDS_SEARCH_URL_THIRD_PART + "EN" + MSDS_SEARCH_URL_FOURTH_PART;
				try {
					Thread.sleep(500);
				} catch (InterruptedException error) { /* Do Nothing */ }
				// System.out.println("Launching IE with Following search link\n\n" + finalUrl);
				BrowserLauncher.launchBrowser(finalUrl);
			}
		}
	}

	/*
	 * A string value under 1000 characters in length. The string is represented in UTF8 encoding using the following format: x{UTF8
	 * code}x{UTF8 code}x{UTF8 code} For example to pass the keyword ethanol whose UTF encoding looks like this:
	 * x101x116x104x97x110x111x108
	 */
	private static String generateSearchKey(String searchText) {
		StringBuffer searchKey = new StringBuffer();
		char srcChar;
		int decimalValue = 0;
		char[] srcCharArray = searchText.toCharArray();
		for (int i = 0; i < srcCharArray.length; i++) {
			srcChar = srcCharArray[i];
			decimalValue = srcChar;
			searchKey.append("x" + decimalValue);
		}
		return searchKey.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		MSDSSearchLauncher.launchMSDSSearch("50-00-0", "EN");
	}
}