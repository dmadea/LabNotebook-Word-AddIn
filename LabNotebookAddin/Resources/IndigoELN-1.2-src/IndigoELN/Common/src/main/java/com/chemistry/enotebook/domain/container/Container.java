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
package com.chemistry.enotebook.domain.container;

import com.chemistry.enotebook.domain.CeNAbstractModel;
import com.chemistry.enotebook.experiment.utils.GUIDUtil;
import com.chemistry.enotebook.utils.CommonUtils;

import java.util.ArrayList;

/**
 * This abstract class represents a generic container which can be a plate,vial,tube etc
 * 
 * 
 * 
 */
public class Container extends CeNAbstractModel{

	/*
	 * Sample layout/definition of a container type plate
	 * 
	 * A B C D E F G H I J K L
	 * 1 # # # # # # # # # # # #
	 * 
	 * 2 # # # # # # # # # # # #
	 * 
	 * 3 # # # # # # # # # # # #
	 * 
	 * 4 # # # # # # # # # # # #
	 * 
	 * 5 # # # # # # # # # # # #
	 * 
	 * 6 # # # # # # # # # # # #
	 * 
	 * 7 # # # # # # # # # # # #
	 * 
	 * 8 # # # # # # # # # # # #
	 *  # represents a well 12 X 8 plate with Major axis Y X Positions -- 12 ( A to L ) Y Positions -- 8 ( 1 to 8 )
	 * 
	 */

	private static final long serialVersionUID = 229231338470202859L;
	
	private String containerCode = null; // unique code to identify a container
	private int xPositions = -1; // no of columns.
	private int yPositions = -1; // no of rows
	private String creatorId = null;// Owner id(username)
	private String containerName = null; //Name of the container
	private String majorAxis = null; // Axis to start numbering
	private String containerType = null; // Actual type of container like Plate,Rack,Vial(tube),Tote etc
	private boolean isUserDefined = false; // boolean indicating if container is user defined
	private String siteCode = null;// siteCode for holding only site for CompoundManagement Container will not be used for user defined container
	//list of well numbers like 1,2,3 etc
	private ArrayList skippedWellPositions = new ArrayList();
	//list of Alpha numeric like A1,A2 etc 
	private ArrayList skippedWellNumbers = new ArrayList();
	
	public Container(){
		this.key = GUIDUtil.generateGUID(this);
	}
	
	public Container(String key) {
		this.key = key;
	}

	public static Container prepareNewUSerContainer(String containerName , 
			String containerType, String creatorId, 
			int xPositions, int yPositions, String majorAxis ) {
		Container c = new Container();
		c.containerCode = CommonUtils.getUserContainerCode();
		c.xPositions = xPositions;
		c.yPositions = yPositions;
		c.majorAxis = majorAxis;
		c.containerType = containerType;
		c.creatorId = creatorId;
		c.containerName = containerName;
		c.isUserDefined = true;
		
		return c;

	}
	
	public Container(String key, String containerCode, String containerName , 
			String containerType, String creatorId, 
			int xPositions, int yPositions, String majorAxis,
			boolean isUserDefined ) {
		this(key);
		this.containerCode = containerCode;
		this.xPositions = xPositions;
		this.yPositions = yPositions;
		this.majorAxis = majorAxis;
		this.containerType = containerType;
		this.creatorId = creatorId;
		this.containerName = containerName;
		this.isUserDefined = isUserDefined;

	}
	
	public static Container prepareNewCompoundManagementContainer(String containerCode, String containerName , 
			String containerType, String creatorId, 
			int xPositions, int yPositions, String majorAxis) {
		Container c = new Container();
		c.containerCode = containerCode;
		c.xPositions = xPositions;
		c.yPositions = yPositions;
		c.majorAxis = majorAxis;
		c.containerType = containerType;
		c.creatorId = creatorId;
		c.containerName = containerName;
		c.isUserDefined = false;
		
		return c;

	}

	public String getContainerCode() {
		return containerCode;
	}

	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}

	public String getMajorAxis() {
		return majorAxis;
	}

	public void setMajorAxis(String majorAxis) {
		this.majorAxis = majorAxis;
	}

	public int getXPositions() {
		return xPositions;
	}

	public void setXPositions(int positions) {
		xPositions = positions;
	}

	public int getYPositions() {
		return yPositions;
	}

	public void setYPositions(int positions) {
		yPositions = positions;
	}

	public boolean isUserDefined() {
		return this.isUserDefined;
	}
	
	public String getCreatorId() {
		return this.creatorId;
	}

	/**
	 * @return the containerName
	 */
	public String getContainerName() {
		return containerName;
	}

	/**
	 * @param containerName the containerName to set
	 */
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	/**
	 * @param isUserDefined the isUserDefined to set
	 */
	public void setUserDefined(boolean isUserDefined) {
		this.isUserDefined = isUserDefined;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String toDetail() {
		return "ContainerName = "+ this.containerName +
				" ContainerType = "+this.containerType +
				" CreatorId = "+this.creatorId;
	}

	public String toString() {
		return  this.containerName;
	}
	public int getSize() {
		return xPositions * yPositions;
	}
	
	public String toXML(){
		return "";
	}

	/**
	 * @return the siteCode
	 */
	public String getSiteCode() {
		return siteCode;
	}

	/**
	 * @param siteCode the siteCode to set
	 */
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public ArrayList getSkippedWellNumbers() {
		return skippedWellNumbers;
	}

	public double getUnSkippedWellsSize()
	{
		return xPositions * yPositions - getSkippedWellPositions().size();
	}
	
	public void setSkippedWellNumbers(ArrayList skippedWellNumbers) {
		this.skippedWellNumbers = skippedWellNumbers;
	}

	public ArrayList getSkippedWellPositions() {
		return skippedWellPositions;
	}

	public void setSkippedWellPositions(ArrayList skippedWellPositions) {
		this.skippedWellPositions = skippedWellPositions;
	}
	
	
}
