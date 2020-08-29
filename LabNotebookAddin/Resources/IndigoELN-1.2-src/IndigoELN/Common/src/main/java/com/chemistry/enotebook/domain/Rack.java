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
package com.chemistry.enotebook.domain;

/**
 * 
 */
public class Rack {

	private Tube[] tubes = new Tube[0];

	public Rack() {
	}

	public Tube[] getTubes() {
		return tubes;
	}

	public void setTubes(Tube[] tubes) {
		this.tubes = tubes;
	}

	public String[] getBatchIDs() {
		String[] result = new String[tubes.length];

		for (int i = 0; i < tubes.length; i++) {
			result[i] = tubes[i].getBatch().getCompound().getRegNumber();
		}
		return result;
	}
}
