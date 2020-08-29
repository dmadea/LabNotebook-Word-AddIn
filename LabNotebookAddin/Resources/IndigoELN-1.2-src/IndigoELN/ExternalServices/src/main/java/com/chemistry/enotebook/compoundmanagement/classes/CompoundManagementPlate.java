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
package com.chemistry.enotebook.compoundmanagement.classes;

import java.io.Serializable;

public class CompoundManagementPlate implements Serializable {
	
	private static final long serialVersionUID = 8834489012170226994L;
	
	private String plateBarCode;
	private String containerCode;
	private String locationCode;
	private String projectTrackingCode;
	private CompoundManagementPlateWell[] plateWells;
	private String containerTypeCode;

	public CompoundManagementPlate(String plateBarCode, String containerCode,
			String locationCode, String projectTrackingCode) {
		this.plateBarCode = plateBarCode;
		this.containerCode = containerCode;
		this.locationCode = locationCode;
		this.projectTrackingCode = projectTrackingCode;
	}

	public String getPlateBarCode() {
		return plateBarCode;
	}

	public void setPlateBarCode(String plateBarCode) {
		this.plateBarCode = plateBarCode;
	}

	public String getContainerCode() {
		return containerCode;
	}

	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getProjectTrackingCode() {
		return projectTrackingCode;
	}

	public void setProjectTrackingCode(String projectTrackingCode) {
		this.projectTrackingCode = projectTrackingCode;
	}

	public CompoundManagementPlateWell[] getPlateWells() {
		return plateWells;
	}

	public void setPlateWells(CompoundManagementPlateWell[] plateWells) {
		this.plateWells = plateWells;
	}

	public void setContainerTypeCode(String containerTypeCode) {
		this.containerTypeCode = containerTypeCode;
	}

	public String getContainerTypeCode() {
		return containerTypeCode;
	}

	public static class CompoundManagementPlateWell {
		public CompoundManagementPlateWell(String solventCode,
				double containedVolumeAmount, String containedVolumeUnit,
				double containedWeightAmount, String containedWeightUnit,
				double molarity, String containedMolarityUnit,
				long batchTrackingId, String wellNumber, int wellPosition,
				String barCode, String locationCode, String containerTypeCode,
				String globalBarCode, String batchCompoundParent,
				String batchCompoundHazardComment, String batchComment,
				String batchNumber, String batchHandlingComment,
				String batchOwnerId, String projectTrackingCode,
				String batchSaltCode) {
			this.solventCode = solventCode;
			this.containedVolumeAmount = containedVolumeAmount;
			this.containedVolumeUnit = containedVolumeUnit;
			this.containedWeightAmount = containedWeightAmount;
			this.containedWeightUnit = containedWeightUnit;
			this.molarity = molarity;
			this.containedMolarityUnit = containedMolarityUnit;
			this.batchTrackingId = batchTrackingId;
			this.wellNumber = wellNumber;
			this.wellPosition = wellPosition;
			this.barCode = barCode;
			this.locationCode = locationCode;
			this.containerTypeCode = containerTypeCode;
			this.globalBarCode = globalBarCode;
			this.batchCompoundParent = batchCompoundParent;
			this.batchCompoundHazardComment = batchCompoundHazardComment;
			this.batchComment = batchComment;
			this.batchNumber = batchNumber;
			this.batchHandlingComment = batchHandlingComment;
			this.batchOwnerId = batchOwnerId;
			this.projectTrackingCode = projectTrackingCode;
			this.batchSaltCode = batchSaltCode;
		}

		public CompoundManagementPlateWell(String solventCode,
				double containedVolumeAmount, String containedVolumeUnit,
				double containedWeightAmount, String containedWeightUnit,
				double molarity, String containedMolarityUnit,
				long batchTrackingId, String wellNumber, int wellPosition,
				String barCode, String locationCode, String containerTypeCode) {
			this.solventCode = solventCode;
			this.containedVolumeAmount = containedVolumeAmount;
			this.containedVolumeUnit = containedVolumeUnit;
			this.containedWeightAmount = containedWeightAmount;
			this.containedWeightUnit = containedWeightUnit;
			this.molarity = molarity;
			this.containedMolarityUnit = containedMolarityUnit;
			this.batchTrackingId = batchTrackingId;
			this.wellNumber = wellNumber;
			this.wellPosition = wellPosition;
			this.barCode = barCode;
			this.locationCode = locationCode;
			this.containerTypeCode = containerTypeCode;
		}

		public CompoundManagementPlateWell(String solventCode,
				double containedVolumeAmount, String containedVolumeUnit,
				double containedWeightAmount, String containedWeightUnit,
				String barCode, String locationCode, String containerTypeCode) {
			this.solventCode = solventCode;
			this.containedVolumeAmount = containedVolumeAmount;
			this.containedVolumeUnit = containedVolumeUnit;
			this.containedWeightAmount = containedWeightAmount;
			this.barCode = barCode;
			this.locationCode = locationCode;
			this.containerTypeCode = containerTypeCode;
		}

		public double getContainedWeightAmount() {
			return containedWeightAmount;
		}

		public void setContainedWeightAmount(double containedWeightAmount) {
			this.containedWeightAmount = containedWeightAmount;
		}

		public int getWellPosition() {
			return wellPosition;
		}

		public void setWellPosition(int wellPosition) {
			this.wellPosition = wellPosition;
		}

		public String getSolventCode() {
			return solventCode;
		}

		public void setSolventCode(String solventCode) {
			this.solventCode = solventCode;
		}

		public double getContainedVolumeAmount() {
			return containedVolumeAmount;
		}

		public void setContainedVolumeAmount(double containedVolumeAmount) {
			this.containedVolumeAmount = containedVolumeAmount;
		}

		public double getMolarity() {
			return molarity;
		}

		public void setMolarity(double molarity) {
			this.molarity = molarity;
		}

		public long getBatchTrackingId() {
			return batchTrackingId;
		}

		public void setBatchTrackingId(long batchTrackingId) {
			this.batchTrackingId = batchTrackingId;
		}

		public String getBarCode() {
			return barCode;
		}

		public void setBarCode(String barCode) {
			this.barCode = barCode;
		}

		public String getLocationCode() {
			return locationCode;
		}

		public void setLocationCode(String locationCode) {
			this.locationCode = locationCode;
		}

		public String getContainerTypeCode() {
			return containerTypeCode;
		}

		public void setContainerTypeCode(String containerTypeCode) {
			this.containerTypeCode = containerTypeCode;
		}

		public String getContainedVolumeUnit() {
			return containedVolumeUnit;
		}

		public void setContainedVolumeUnit(String containedVolumeUnit) {
			this.containedVolumeUnit = containedVolumeUnit;
		}

		public String getContainedWeightUnit() {
			return containedWeightUnit;
		}

		public void setContainedWeightUnit(String containedWeightUnit) {
			this.containedWeightUnit = containedWeightUnit;
		}

		public String getContainedMolarityUnit() {
			return containedMolarityUnit;
		}

		public void setContainedMolarityUnit(String containedMolarityUnit) {
			this.containedMolarityUnit = containedMolarityUnit;
		}

		public String getGlobalBarCode() {
			return globalBarCode;
		}

		public void setGlobalBarCode(String globalBarCode) {
			this.globalBarCode = globalBarCode;
		}

		public String getBatchCompoundHazardComment() {
			return batchCompoundHazardComment;
		}

		public void setBatchCompoundHazardComment(
				String batchCompoundHazardComment) {
			this.batchCompoundHazardComment = batchCompoundHazardComment;
		}

		public String getBatchComment() {
			return batchComment;
		}

		public void setBatchComment(String batchComment) {
			this.batchComment = batchComment;
		}

		public String getBatchNumber() {
			return batchNumber;
		}

		public void setBatchNumber(String batchNumber) {
			this.batchNumber = batchNumber;
		}

		public String getBatchHandlingComment() {
			return batchHandlingComment;
		}

		public void setBatchHandlingComment(String batchHandlingComment) {
			this.batchHandlingComment = batchHandlingComment;
		}

		public String getBatchOwnerId() {
			return batchOwnerId;
		}

		public void setBatchOwnerId(String batchOwnerId) {
			this.batchOwnerId = batchOwnerId;
		}

		public String getProjectTrackingCode() {
			return projectTrackingCode;
		}

		public void setProjectTrackingCode(String projectTrackingCode) {
			this.projectTrackingCode = projectTrackingCode;
		}

		public String getBatchSaltCode() {
			return batchSaltCode;
		}

		public void setBatchSaltCode(String batchSaltCode) {
			this.batchSaltCode = batchSaltCode;
		}

		public void setBatchCompoundParent(String batchCompoundParent) {
			this.batchCompoundParent = batchCompoundParent;
		}

		public String getBatchCompoundParent() {
			return batchCompoundParent;
		}

		public void setWellNumber(String wellNumber) {
			this.wellNumber = wellNumber;
		}

		public String getWellNumber() {
			return wellNumber;
		}

		private String solventCode;
		private double containedVolumeAmount;
		private String containedVolumeUnit;
		private double containedWeightAmount;
		private String containedWeightUnit;
		private double molarity;
		private String containedMolarityUnit;
		private long batchTrackingId;
		private String wellNumber;
		private int wellPosition;
		private String barCode;
		private String locationCode;
		private String containerTypeCode;
		private String globalBarCode;
		private String batchCompoundParent;
		private String batchCompoundHazardComment;
		private String batchComment;
		private String batchNumber;
		private String batchHandlingComment;
		private String batchOwnerId;
		private String projectTrackingCode;
		private String batchSaltCode;
	}
}
