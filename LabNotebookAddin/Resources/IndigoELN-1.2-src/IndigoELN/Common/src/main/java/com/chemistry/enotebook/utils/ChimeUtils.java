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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * As long as the ChemistryPanel object is in this class do not allow it to be used 
 * in a headless (no GUI = server) environment.
 *
 */
public class ChimeUtils {

	private static final Log log = LogFactory.getLog(ChimeUtils.class);
	
    /**
     * Using internal method from ChimePro.jar to prevent GUI initialize from ChemistryPanel
     * 
     * @param structure
     * @return
     */
    private static String getMolFileDataFromChimeString(String structure) {
    	if(CommonUtils.isNotNull(structure)) {
    		throw new UnsupportedOperationException("Conversion from chime string to mol file is not supported");
    	}
    	return structure;
    }

	/**
	 * Converts given structures to MOLFILE format. 
	 * @param structure
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static byte[] toMoleFileFormatForCompound(String structure, String id) throws Exception{
		String molfile = null;
		if(StringUtils.isBlank(structure)) {
			log.debug(" Unable to Decode STRUCTURE, provided string had no information");
			return new byte[0];
		}
		StringBuffer structBuf = new StringBuffer();
		String decodedString = Decoder.decodeString(structure);
		int index = decodedString.indexOf("END");
		if (index >= 0) {
			//index > 0 indicates decoded string is in MDL Molefile format
			molfile = decodedString;
		} else {
			//Else its a Chime String, using the blow code
			molfile = getMolFileDataFromChimeString(structure);
		}
		index = molfile.indexOf("$$$$");
		if (index == -1) {
			structBuf.append(molfile);
			structBuf.append("$DTYPE VENDOR_ID\n");
			structBuf.append("$DATUM "+id+"\n");
		} else {
			structBuf.append(molfile.substring(0, index));
			structBuf.append(">  <VENDOR_ID> \n"+id+"\n");
			structBuf.append("\n$$$$");
		}
		return structBuf.toString().getBytes();
	}
	
	public static byte[] toMoleFileFormatForReaction(String structure,String id) throws Exception{
		String molfile = null;
		
		if(StringUtils.isBlank(structure)) {
			log.debug(" Unable to Decode REACTION, supplied string had no information");
			return new byte[0];
		}

		if (structure.indexOf("M  END") >= 0)
			//index > 0 indicates the string is in MDL Molefile format
			return structure.getBytes();
		
		String decodedString = Decoder.decodeString(structure);
		int index = decodedString.indexOf("M  END");
		if (index >= 0) {
			//index > 0 indicates decoded string is in MDL Molefile format
			molfile = decodedString;
		} else {
			//Else its a Chime String, using the blow code
			molfile = getMolFileDataFromChimeString(structure);
		}

		return molfile.getBytes();
	}

	//	Converts given structures to MOLFILE format. 
	public static String toMoleFileFormatFromChime(String chimeStr) throws Exception{
		String molfile = null;
		if(StringUtils.isBlank(chimeStr)) {
			log.debug(" Unable to Decode STRUCTURE, chimeStr had no information");
			return "";
		}
		
		String decodedString = Decoder.decodeString(chimeStr);
		int index = decodedString.indexOf("END");
		if (index >= 0) {
			//index > 0 indicates decoded string is in MDL Molefile format
			molfile = decodedString;
		} else {
			//Else its a Chime String, using the blow code
			molfile = getMolFileDataFromChimeString(chimeStr);
		}
		
		return molfile;
	}
	
	
	
}
