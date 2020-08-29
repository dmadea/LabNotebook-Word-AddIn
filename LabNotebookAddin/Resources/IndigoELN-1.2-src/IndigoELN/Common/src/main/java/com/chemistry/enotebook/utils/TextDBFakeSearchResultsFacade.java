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
package com.chemistry.enotebook.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextDBFakeSearchResultsFacade {
	private TextDBFakeSearchResultsFacade() {
		results = new ArrayList<String>();
		InputStream is = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
		InputStreamReader reader = new InputStreamReader(is);
		int maxFileSize = (int)Math.pow(2, 16);
		char[] buf = new char[maxFileSize];
		try {
			reader.read(buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] entries =  (new String(buf)).split("<Reagent>|</Reagent>");
		for(String entry : entries) {
			entry = entry.trim();
			if(entry.contains(requiredInnerTag)) {
				results.add("<Reagent>" + entry + "</Reagent>");
			}
		}
	}
	
	public static TextDBFakeSearchResultsFacade getTextDBFakeSearchResultsFacade() {
		if (textDBFakeSearchResultsFacade == null) {
			textDBFakeSearchResultsFacade = new TextDBFakeSearchResultsFacade(); 
		}
		return textDBFakeSearchResultsFacade; 
	}
	
	public List<String> getResultsList() {
		return results;
	}
	
	private static TextDBFakeSearchResultsFacade textDBFakeSearchResultsFacade = null;
	private static String FILE_NAME = "data/short_text_search_results.txt";
	private static List<String> results = null;
	private static final String requiredInnerTag = "Fields";

}
