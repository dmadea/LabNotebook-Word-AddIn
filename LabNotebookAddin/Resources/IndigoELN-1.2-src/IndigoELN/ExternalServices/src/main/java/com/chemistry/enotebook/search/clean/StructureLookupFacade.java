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
package com.chemistry.enotebook.search.clean;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class StructureLookupFacade {
	
	private StructureLookupFacade() {
		STRUCTURES = new Hashtable<String, String>();
		InputStream is = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
		InputStreamReader reader = new InputStreamReader(is);
		int maxFileSize = (int)Math.pow(2, 16);
		char[] buf = new char[maxFileSize];
		try {
			reader.read(buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] strs =  (new String(buf)).split("\\$\\$\\$\\$");
		for(String structure:strs) {
			structure = structure.trim();
			String[] splitted = structure.split(" |\t|\n|\r|\f");
			String structureName = splitted[0];
			STRUCTURES.put(structureName, structure);
		}
	}
	
	public String getSessionToken()
	{
		return "SES__wls11_103p321__13720";
	}
	
	public void releaseSessionToken(String s) {}
	
	public String getStructureString(String compoundID) {

		String structure = (String)STRUCTURES.get(getExistingKey(compoundID));
		return structure;
	}

	public String[] getStructuresString(String[] compoundIDs){
		System.err.println("StructureLookupFacadeFakeImpl - getStructuresNoBatching: " + compoundIDs.toString());
		String[] structures = new String[compoundIDs.length];
		for (int i = 0; i < compoundIDs.length; i++) {
			structures[i] = (String)STRUCTURES.get(getExistingKey(compoundIDs[i]));
		}
		return structures;
	}

	public static StructureLookupFacade getStructureLookupFacade() {
		if (structureLookupFacade == null) {
			structureLookupFacade = new StructureLookupFacade(); 
		}
		return structureLookupFacade; 
	}
	
	private String getExistingKey(String key) {
		if(!STRUCTURES.containsKey(key)) {
			//just something
			key = (String)STRUCTURES.keys().nextElement();
		}
		return key;
	}
	
	private static StructureLookupFacade structureLookupFacade = null;
	private static String FILE_NAME = "data/structures.txt";
	private static Hashtable<String, String> STRUCTURES = null;
}
