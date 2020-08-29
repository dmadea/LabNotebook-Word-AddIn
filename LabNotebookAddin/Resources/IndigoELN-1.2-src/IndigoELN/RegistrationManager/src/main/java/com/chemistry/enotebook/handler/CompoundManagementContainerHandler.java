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
package com.chemistry.enotebook.handler;

import com.chemistry.enotebook.compoundmanagement.CompoundManagementServiceFactory;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementBarCodeReg;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementPlate;
import com.chemistry.enotebook.compoundmanagement.classes.CompoundManagementPlate.CompoundManagementPlateWell;
import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.CompoundManagementBarcodePrefixInfo;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompoundManagementContainerHandler {
	private static Log log = LogFactory.getLog(CompoundManagementContainerHandler.class);
	static final int maxBarcodesPerCall = 20;

	public CompoundManagementBarcodePrefixInfo[] getCompoundManagementBarcodePrefixes(String type, String site)
			throws Exception {
		log.debug("getCompoundManagementBarcodePrefixes().enter");
		ArrayList<CompoundManagementBarcodePrefixInfo> list = new ArrayList<CompoundManagementBarcodePrefixInfo>();
		try {
			CompoundManagementBarCodeReg codes[] = CompoundManagementServiceFactory.getService().getPlateBarCodeRegList();

			int size = codes.length;
			for (int i = 0; i < size; i++) {
				if (codes[i].getType().equals(type)) {
					CompoundManagementBarcodePrefixInfo info = new CompoundManagementBarcodePrefixInfo();
					info.setPrefix(codes[i].getPrefix());
					info.setSiteCode(codes[i].getSiteCode());
					info.setType(codes[i].getType());
					list.add(info);
				}
			}
		} catch (Exception e) {
			log.error("Failed to get Barcode prefixes from CompoundManagement :" + e.getMessage(), e);
			throw new Exception("Failed to get Barcode prefixes from CompoundManagement :" + e.getMessage(), e);
		} 
		log.debug("getCompoundManagementBarcodePrefixes().exit");
		return (CompoundManagementBarcodePrefixInfo[]) list.toArray(new CompoundManagementBarcodePrefixInfo[] {});
	}

	public String[] getNewGBLPlateBarCodesFromCompoundManagement(String barcodePrefix, int noOfBarcodes, String site) throws Exception {
		if (noOfBarcodes == 0) {
			noOfBarcodes = 1;
		} else if (noOfBarcodes > maxBarcodesPerCall) {
			noOfBarcodes = maxBarcodesPerCall;
		}
		String barcodeStrArray[] = new String[noOfBarcodes];
		try {
			for (int i = 0; i < noOfBarcodes; i++) {
				barcodeStrArray[i] = CompoundManagementServiceFactory.getService().getGlobalPlateBarCode(barcodePrefix);
			}
		} catch (Exception compoundManagementException) {
			log.error("Failed to get plate barcodes from CompoundManagement :" + compoundManagementException.getMessage(), compoundManagementException);
			throw new Exception("Failed to get plate barcodes from CompoundManagement :" + compoundManagementException.getMessage(), compoundManagementException);
		} 

		return barcodeStrArray;
	}

	public boolean registerNewPlate(ProductPlate plate, String site) throws Exception {
		log.info("registerNewPlate().enter");
		try {
			log.info("Creating Empty Plate in CompoundManagement with barcode:" +
						plate.getPlateBarCode() + 
						" and container code:" + 
						plate.getContainer().getContainerCode());
			//String locationCode = CodeTableCache.getCache().getLocationsBySite(site);
			String containerType = plate.getContainer().getContainerType().toLowerCase();
			String locationCode = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_CONTAINER_PLATE_LOCATION);
			if ("rack".equals(containerType)) {
				locationCode = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_CONTAINER_RACK_LOCATION);
			} else if ("tote".equals(containerType)) {
				locationCode = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_CONTAINER_TOTE_LOCATION);
			} else if ("vial".equals(containerType)) {
				locationCode = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_CONTAINER_VIAL_LOCATION);
			} else if ("u_d_plate".equals(containerType)) {
				locationCode = CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_CONTAINER_UDPLATE_LOCATION);
			}

			int totalWells = plate.getWells().length;
			log.info("Total wells in CeN plate:" + totalWells);

			CompoundManagementPlate compoundManagementPlate = new CompoundManagementPlate(plate.getPlateBarCode(), plate.getContainer().getContainerCode(), locationCode, plate.getProjectTrackingCode());
			
			PlateWell<ProductBatchModel>[] plateWells = plate.getAllWellsCopy();
			CompoundManagementPlateWell[] compoundManagementPlateWells = new CompoundManagementPlateWell[plateWells.length];
			for (int i = 0; i < plateWells.length; i++) {
				compoundManagementPlateWells[i] = new CompoundManagementPlateWell(
						plateWells[i].getSolventCode(), 
						plateWells[i].getContainedVolumeAmount().doubleValue(),
						plateWells[i].getContainedVolumeAmount().getUnit().getCode(),
						plateWells[i].getContainedWeightAmount().doubleValue(),
						plateWells[i].getContainedWeightAmount().getUnit().getCode(),
						plateWells[i].getMolarity().doubleValue(),
						plateWells[i].getMolarity().getUnit().getCode(),
						plateWells[i].getBatchTrackingId(),
						locationCode, plateWells[i].getWellPosition(),
						plateWells[i].getBarCode(),
						plateWells[i].getLocationCode(),
						plateWells[i].getContainerTypeCode());
			}
			compoundManagementPlate.setPlateWells(compoundManagementPlateWells);
			
			CompoundManagementServiceFactory.getService().registerNewPlate(compoundManagementPlateWells);

			log.info("Plate has been created in CompoundManagement");
			log.info("registerNewPlate().exit");
		} catch (Exception compoundManagementException) {
			// ensure error is logged before rollback to record actual error message.
			log.error("Failed to create plate in CompoundManagement and CompoundManagement session is rolledback. Barcode is:" + plate.getPlateBarCode(), compoundManagementException);
			// ensure rollback call doesn't obscure feedback to calling method.
			throw new Exception("Failed to create plate in CompoundManagement for plate bar code:" + plate.getPlateBarCode(), compoundManagementException);
		}
		return true;
	}

	public String[] getNewTubeBarCodesFromCompoundManagement(Date startDate, Date endDate, String site)
			throws Exception {
		List<String> tubeBarCodeList = new ArrayList<String>();
		try {
			
			tubeBarCodeList = CompoundManagementServiceFactory.getService().getTubeBarcodesByDateAndSiteCode(startDate, endDate);

		} catch (Exception compoundManagementException) {
			log.error("Failed to get tube barcodes from CompoundManagement :" + compoundManagementException.getMessage(), compoundManagementException);
			throw new Exception("Failed to get tube barcodes from CompoundManagement :" + compoundManagementException.getMessage(), compoundManagementException);
		} 

		return (String[]) tubeBarCodeList.toArray(new String[] {});
	}

	public boolean registerNewTubes(PlateWell<ProductBatchModel> cenWell, String site) throws Exception {
		log.info("registerNewTube().enter");
		try {
			if (cenWell == null) {
				return false;  // continue;
			}
			
			//check if this List of containers but not plate
			ProductBatchModel prodBatchModel = (ProductBatchModel)cenWell.getBatch();
			
			if (prodBatchModel != null && prodBatchModel.getRegInfo() != null) {
				List<BatchSubmissionContainerInfoModel> contList = prodBatchModel.getRegInfo().getSubmitContainerList();
				
				if(contList.size() == 0) {
					log.info("No standalone containers in the batch:" + prodBatchModel.getBatchNumberAsString());
				}else
				{
					registerStandAloneContainers(contList,site,(int)prodBatchModel.getRegInfo().getBatchTrackingId());
				}
			} else { 				
				CompoundManagementPlateWell plateWell = new CompoundManagementPlateWell(
						cenWell.getSolventCode(), 
						cenWell.getContainedVolumeAmount().GetValueInStdUnitsAsDouble(), 
						cenWell.getContainedVolumeAmount().getUnit().getCode(),
						cenWell.getContainedWeightAmount().doubleValue(),
						cenWell.getContainedWeightAmount().getUnit().getCode(),
						cenWell.getMolarity().doubleValue(),
						cenWell.getMolarity().getUnit().getCode(),
						cenWell.getBatchTrackingId(),
						cenWell.getWellNumber(),
						cenWell.getWellPosition(),
						cenWell.getBarCode(),
						cenWell.getLocationCode(),
						cenWell.getContainerTypeCode());
				CompoundManagementServiceFactory.getService().registerNewTubes(plateWell, site);
				log.info("Empty Tubes created in CompoundManagement is updated with compound info");
			}			
			
			log.info("Tube has been created in CompoundManagement");
			log.info("registerNewPlate().exit");
		} catch (Exception compoundManagementException) {
			// log first then rollback in case we encounter an unrecoverable problem.
			log.error("Failed to create Tube in CompoundManagement.", compoundManagementException);
			// don't disguise or lose track of the real reason for the error.
			throw new Exception("Failed to create Tube in CompoundManagement :" + compoundManagementException.getMessage(), compoundManagementException);
		} 
		return true;
	}

	private void registerStandAloneContainers(List<BatchSubmissionContainerInfoModel> contList, String site, int btid) throws Exception
	{
		log.info("registerStandAloneContainers(List<BatchSubmissionContainerInfoModel>, String, int) start");
		
		log.debug("registerStandAloneContainers: in");
		
		for (BatchSubmissionContainerInfoModel contModel : contList) {
			if (contModel != null && contModel.getContainerType() != null && !contModel.getContainerType().equalsIgnoreCase(CeNConstants.CONTAINER_TYPE_VIAL)) {
				log.debug("registerStandAloneContainers: tube found");
				log.debug("barcode: " + contModel.getBarCode() + ", location: " + contModel.getContainerLocation() + ", type: " + contModel.getContainerType());
				
				CompoundManagementPlateWell plateWell = new CompoundManagementPlateWell(
						contModel.getSolvent(), 
						contModel.getVolumeValue(), 
						contModel.getVolumeUnit(),
						contModel.getAmountValue(),
						contModel.getAmountUnit(),
						contModel.getBarCode(),
						contModel.getContainerLocation(),
						contModel.getContainerType());
				CompoundManagementServiceFactory.getService().registerNewTubes(plateWell, site);
				log.info("Empty Tubes created in CompoundManagement is updated with compound info");
			}
		}
		
		log.info("StandAloneContainers:Empty Tubes created in CompoundManagement is updated with compound info");
		log.info("registerStandAloneContainers(List<BatchSubmissionContainerInfoModel>, String, int) end");
	}
}
