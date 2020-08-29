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

import com.chemistry.enotebook.domain.ParentCompoundModel;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.ChemUtilInitException;
import com.chemistry.enotebook.sdk.ChemistryProperties;
import com.chemistry.enotebook.sdk.PictureProperties;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;

public class StructureLoadAndConversionUtil {

	public static final int SDFILE = 0;
	public static final int ISISDRAW = 1;
	public static final int CHEMDRAW = 2;
	
	private static final String SDFILESTR = "SD File";
	private static final String ISISDRAWSTR = "ISIS Sketch";
	private static final String CHEMDRAWSTR = "ChemDraw";
	
	private StructureLoadAndConversionUtil() {		
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
	public static void loadSketch(byte[] nativeSkc, int format, boolean createdByNbk, ParentCompoundModel model) throws ChemUtilInitException, ChemUtilAccessException {
		model.setNativeSketch(nativeSkc);

		// Don't set this here because we don't know how we are being called.
		// createdByNotebook = createdByNbk;
		switch (format) {
			case ISISDRAW:
				model.setNativeSketchFormat(ISISDRAWSTR);
				break;
			case CHEMDRAW:
				model.setNativeSketchFormat(CHEMDRAWSTR);
				break;
			case SDFILE:
				model.setNativeSketchFormat(SDFILESTR);
				break;
			default:
				model.setNativeSketchFormat(SDFILESTR);
				break;
		}

		if (model.getNativeSketch() != null && model.getNativeSketch().length > 0) {
			ChemistryDelegate chemDel = new ChemistryDelegate();
			if (model.getNativeSketch() != null && model.getNativeSketch().length > 0 && createdByNbk) {
				model.setStringSketch(chemDel.convertChemistry(model.getNativeSketch(), null, "Chemistry"));
			}
			byte[] pic = null;
			pic = chemDel.convertStructureToPicture(model.getNativeSketch(), PictureProperties.PNG, 1000, 1000, 1.0, 0.26458);
			model.setViewSketch(pic);

			setSketchDependentVars(chemDel.getMolecularInformation(model.getNativeSketch()),model);
		} else {
			// Clear other values as there is nothing to represent
			clearDependentSketches(model);
		}

		model.setModelChanged(createdByNbk);
	}

	
	/**
	 * If the structure is not to be saved use this version of load and set createdByNotebook = false
	 * This is not for String, it is for MDL MolFile.
	 * 
	 * @param nativeSketch -
	 *            the sketch to be used to produce all three sketches
	 * @param format -
	 *            integer representation of the format based on the static variables from this object
	 * @throws ChemUtilAccessException -
	 *             will be thrown if there is a problem creating the view or search sketches from the input.
	 */
	public static void loadSketch(byte[] nativeSkc, int format, boolean createdByNbk, String MDLMolfile,ParentCompoundModel model) throws ChemUtilInitException, ChemUtilAccessException {
		model.setNativeSketch(nativeSkc);

		// Don't set this here because we don't know how we are being called.
		// createdByNotebook = createdByNbk;
		switch (format) {
			case ISISDRAW:
				model.setNativeSketchFormat(ISISDRAWSTR);
				break;
			case CHEMDRAW:
				model.setNativeSketchFormat(CHEMDRAWSTR);
				break;
			case SDFILE:
				model.setNativeSketchFormat(SDFILESTR);
				break;
			default:
				model.setNativeSketchFormat(SDFILESTR);
				break;
		}

		if (model.getNativeSketch() != null && model.getNativeSketch().length > 0) {
			ChemistryDelegate chemDel = new ChemistryDelegate();
			if (chemDel != null) {
				if (model.getNativeSketch() != null && model.getNativeSketch().length > 0 && createdByNbk) {
					model.setStringSketch( chemDel.convertChemistry(model.getNativeSketch(), "", MDLMolfile));
				}
				byte[] pic = null;

				pic = chemDel.convertStructureToPicture(model.getNativeSketch(), PictureProperties.PNG, 1000, 1000, 1.0, 0.26458);
				model.setViewSketch(pic);

				setSketchDependentVars(chemDel.getMolecularInformation(model.getNativeSketch()),model);

			} else {
				// Clear other values as we cannot guarantee that what is set is what was here
				clearDependentSketches(model);
			}
		} else {
			// Clear other values as there is nothing to represent
			clearDependentSketches(model);
		}

		model.setModelChanged(createdByNbk);
	}
	
	/**
	 * Assumes no format available and that the sketches are retrieved from sources outside the notebook experiment.
	 * 
	 * @param sketch -
	 *            could be bytes from an sdfile or cdx file, etc.
	 * @param format -
	 *            integer representation of the format based on the static variables from this object
	 */
	public static void loadSketch(byte[] nativeSketch, int format,ParentCompoundModel model) throws ChemUtilInitException, ChemUtilAccessException {
		loadSketch(nativeSketch, format, false,model);
	}

	private static void setSketchDependentVars(ChemistryProperties cp,ParentCompoundModel model) {
		if (cp != null) {
			model.setMolWgt(cp.MolecularWeight);
			model.setMolFormula(cp.MolecularFormula);
			model.setExactMass(cp.ExactMolecularWeight);
			if (cp.Name != null && cp.Name.length() > 0) {
				model.setChemicalName(cp.Name);
			}
		} else {
			model.setMolWgt(0.0);
			model.setMolFormula("");
			model.setExactMass(0.0);
			model.setChemicalName("");
		}
	}
	
	private static void clearDependentSketches(ParentCompoundModel model) {
		model.setStringSketch(null);
		model.setViewSketch(null);
		setSketchDependentVars(null,model);
	}
}
