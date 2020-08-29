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
package com.chemistry.enotebook.formatter;

/**
 * Interface for Compound Number formatting service
 */
public interface Formatter {

	/**
	 * Format given compound number to needed format
	 * 
	 * @param s
	 *            compound number
	 * @return formatted compound number
	 */
	public String formatCompoundNumber(String s);

	/**
	 * Format given compound number to needed format using or not using salt
	 * code
	 * 
	 * @param s
	 *            compound number
	 * @param withSalt
	 *            use salt code or not
	 * @return formatted compound number
	 */
	public String formatCompoundNumber(String s, boolean withSalt);

	/**
	 * Remove salt code from given compound number
	 * 
	 * @param s
	 *            compound number
	 * @return compound number with removed salt code
	 */
	public String removeSaltCode(String s);

	/**
	 * Get salt code from given compound number
	 * 
	 * @param s
	 *            compound number
	 * @return salt code
	 */
	public String getSaltCode(String s);
}
