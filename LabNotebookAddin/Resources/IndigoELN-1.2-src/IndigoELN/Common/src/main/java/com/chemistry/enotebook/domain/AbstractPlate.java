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

import com.chemistry.enotebook.domain.container.Container;

import java.sql.Timestamp;
import java.util.ArrayList;

abstract public class AbstractPlate<E extends BatchModel> extends SampleContainer {

	private static final long serialVersionUID = 8303243612019347862L;

	protected PlateWell<E>[] wells;

	private Container container;

	private String plateNumber = "";

	private Timestamp registeredDate;

	private String plateType = ""; //WET, DRY etc
	
	private String plateComments = "";
	
	private String plateBarCode = "";
	
	//Stores info if a plate was created from excel,synthesisPlan design or from other order
	private String plateCreationSource = "";
	

	/**
	 * @return the plateBarCode
	 */
	public String getPlateBarCode() {
		return plateBarCode;
	}

	/**
	 * @param plateBarCode the plateBarCode to set
	 */
	public void setPlateBarCode(String plateBarCode) {
		if (plateBarCode != null)
		{
			this.plateBarCode = plateBarCode;
			if (wells != null) {
				for (int i = 0; i < wells.length; ++i) {
					if (wells[i] != null) {
						wells[i].setBarCode(plateBarCode);
					}
				}
			}
			this.modelChanged = true;
		}
	}
	
	/**
	 * @return the registeredDate
	 */
	public Timestamp getRegisteredDate() {
		return registeredDate;
	}

	/**
	 * @param registeredDate
	 *            the registeredDate to set
	 */
	public void setRegisteredDate(Timestamp registeredDate) {
		this.registeredDate = registeredDate;
		this.modelChanged = true;
	}

	/**
	 * @return the plateNumber
	 */
	public String getPlateNumber() {
		return plateNumber;
	}

	/**
	 * @param plateNumber
	 *            the plateNumber to set
	 */
	public void setPlateNumber(String plateNumber) {
		if (plateNumber != null)
		{
			this.plateNumber = plateNumber;
			this.modelChanged = true;
		}
	}

	public PlateWell<E>[] getWells() {
		return wells;
	}

	public void setWells(PlateWell<E>[] wells) {
		this.wells = wells;
		this.modelChanged = true;
	}

	public int getxPositions() {
		return container.getXPositions();
	}

	public void setxPositions(int xPositions) {
		container.setXPositions(xPositions);
	}

	public int getyPositions() {
		return container.getYPositions();
	}

	public void setyPositions(int yPositions) {
		container.setYPositions(yPositions);
	}

	
	public String[] getBatchIDs() {
		String[] result = new String[wells.length];

		for (int i = 0; i < wells.length; i++) {
			result[i] = wells[i].getBatch().getCompound().getRegNumber();
		}
		return result;
	}
    
	
	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
		this.modelChanged = true;
	}

	
	public String getDescription() {
		return container.getContainerName();
	}

	public void setDescription(String description) {
		container.setContainerName(description);
		
	}
	
	/**
	 * @return the plateType
	 */
	public String getPlateType() {
		return plateType;
	}

	/**
	 * @param plateType
	 *            the plateType to set
	 */
	public void setPlateType(String plateType) {
		if (plateType != null)
		{
			this.plateType = plateType;
			this.modelChanged = true;
		}
	}

	public PlateWell<E>[] getAllModifedWellsClone()
	{
		ArrayList<PlateWell<E>> modifedWells = new ArrayList<PlateWell<E>>();
		
		if (!(wells != null && wells.length > 0)) return null;
		int size = wells.length;
		for(int i = 0;i <size ; i ++)
		{
		 if(wells[i] == null) continue;	
		 if(wells[i].isModelChanged())
		 {
			 modifedWells.add(wells[i].deepClone()) ;
		 }
		}
		
		return modifedWells.toArray(new PlateWell[]{});
	}
	
	public PlateWell<E>[] getAllWellsCopy() {
		ArrayList<PlateWell<E>> modifedWells = new ArrayList<PlateWell<E>>();
		
		if (!(wells != null && wells.length > 0)) return null;
		int size = wells.length;
		for(int i = 0;i <size ; i ++)
		{
		 if(wells[i] == null) continue;	
		 
		 PlateWell<E> well = new PlateWell<E>();
		 well.deepCopy(wells[i]);
		 modifedWells.add(well) ;
		}
		
		return modifedWells.toArray(new PlateWell[]{});
	}

	public String getPlateComments() {
		return plateComments;
	}

	public void setPlateComments(String plateComments) {
		if (plateComments != null)
		{
			this.plateComments = plateComments;
			this.setModified(true);
		}
	}

	public String getPlateCreationSource() {
		return plateCreationSource;
	}

	public void setPlateCreationSource(String plateCreationSource) {
		if (plateCreationSource != null)
		{
			this.plateCreationSource = plateCreationSource;
		}
	}

	public PlateWell<E> getPlateWellByPosition(int wellpos)
	{
				
		if (!(wells != null && wells.length > 0)) return null;
		int size = wells.length;
		for(int i = 0;i <size ; i ++)
		{
		 if(wells[i] == null) continue;	
		 if(wells[i].getWellPosition() == wellpos)
		 {
			return  wells[i];
		 }
		}
		return null;
		
	}
	
	public PlateWell<E> getPlateWellByNumber(String wellno)
	{
				
		if (!(wells != null && wells.length > 0)) return null;
		int size = wells.length;
		for(int i = 0;i <size ; i ++)
		{
		 if(wells[i] == null) continue;	
		 if(wells[i].getWellNumber() == wellno)
		 {
			return  wells[i];
		 }
		}
		return null;
		
	}
		
	
}
