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
package com.chemistry.enotebook.utils;

import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.sdk.PictureProperties;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;

/**
 * 
 * 
 */
public class ParallelUtilChemistryConverter {
	public static final int ISISDRAW = 1;
	public static final int CHEMDRAW = 2;
	// Note: sdFiles and molFiles should be passed to the setNativeSketch()
	// which will
	// fit out the rest of the information
	private byte[] viewSketch; // Displayed version for speedbar - JPG
	private byte[] searchSketch; // To be passed to and from component -
	// Cartridge format
	private byte[] nativeSketch; // To be passed to and from component -
	// ISIS/Draw or ChemDraw
	private String nativeSketchFormat; // ISISDraw or ChemDraw or SDFile
	private String dspStructure; // compressed structure from DSP

	public ParallelUtilChemistryConverter() {
	}

	/**
	 * Assumes no format available and that the sketches are retrieved from sources outside the notebook experiment.
	 * 
	 * @param sketch -
	 *            could be bytes from an sdfile or cdx file, etc.
	 * @param format -
	 *            integer representation of the format based on the static variables from this object
	 */
	public void loadSketch(byte[] nativeSketch, int format) throws ChemUtilInitException, ChemUtilAccessException {
		loadSketch(nativeSketch, format, false);
	}

	/**
	 * If the structure is not to be saved use this version of load and set createdByNotebook = false
	 * 
	 * @param nativeSketch -
	 *            the sketch to be used to produce all three sketches
	 * @param format -
	 *            integer representation of the format based on the static variables from this object
	 * @throws ChemUtilAccessException -
	 *             will be thrown if there is a problem creating the view or search sketches from the input.
	 */
	public void loadSketch(byte[] nativeSkc, int format, boolean createdByNbk) throws ChemUtilInitException,
			ChemUtilAccessException {
		nativeSketch = nativeSkc;
		// Don't set this here because we don't know how we are being called.
		// createdByNotebook = createdByNbk;
		switch (format) {
			case ISISDRAW:
				nativeSketchFormat = "ISIS Sketch";
				break;
			case CHEMDRAW:
				nativeSketchFormat = "ChemDraw";
				break;
			default:
				nativeSketchFormat = "SD File";
				break;
		}
		if (nativeSketch != null && nativeSketch.length > 0) {
			ChemistryDelegate chemDel = new ChemistryDelegate();
			if (chemDel != null) {
				if (nativeSketch != null && nativeSketch.length > 0 && createdByNbk) {
					searchSketch = chemDel.convertChemistry(nativeSketch, null, "Chemistry");
				}
				byte[] pic = null;
				pic = chemDel.convertStructureToPicture(nativeSketch, PictureProperties.PNG, 30, 30, 1.0, 0.26458);
				viewSketch = pic;
				// setSketchDependentVars(chemDel.getMolecularInformation(getNativeSketch()));
			} else {
				// Clear other values as we cannot guarantee that what is set is
				// what was here
				// clearDependentSketches();
			}
		} else {
			// Clear other values as there is nothing to represent
			// clearDependentSketches();
		}
		// setModified(createdByNbk);
	}

	/**
	 * @return the nativeSketch
	 */
	public byte[] getNativeSketch() {
		return nativeSketch;
	}

	/**
	 * @param nativeSketch
	 *            the nativeSketch to set
	 */
	public void setNativeSketch(byte[] nativeSketch) {
		this.nativeSketch = nativeSketch;
	}
}
