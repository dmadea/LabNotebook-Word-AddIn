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

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.experiment.common.ExternalSupplier;
import com.chemistry.enotebook.experiment.common.Purity;
import com.chemistry.enotebook.experiment.common.SaltForm;
import com.chemistry.enotebook.experiment.common.units.Unit;
import com.chemistry.enotebook.experiment.common.units.Unit2;
import com.chemistry.enotebook.experiment.common.units.UnitType;
import com.chemistry.enotebook.experiment.datamodel.analytical.Analysis;
import com.chemistry.enotebook.experiment.datamodel.batch.AbstractBatch;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchRegistrationInfo;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchVnVInfo;
import com.chemistry.enotebook.experiment.datamodel.batch.ReagentBatch;
import com.chemistry.enotebook.experiment.datamodel.common.Amount;
import com.chemistry.enotebook.experiment.datamodel.compound.Compound;
import com.chemistry.enotebook.sdk.ChemUtilAccessException;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class CeN11To12ConversionUtils {
	private static final Log log = LogFactory.getLog(CeN11To12ConversionUtils.class);
	
	public static UnitModel convertUnitToUnitModel(Unit unit11)
	{
		UnitModel model12 = new UnitModel();
		unit11.getCode();
		model12.setDescription(unit11.getDescription());
		model12.setUnitType(unit11.getType());
		unit11.getDisplayValue();
		model12.setStdCode(unit11.getStdCode());
		unit11.getStdConversionFactor();
		unit11.getStdDisplayFigs();
		
		return model12;
	}
	

	public static Unit2 convertUnitToUnit2(Unit unit11)
	{
		Unit2 model12 = new Unit2(unit11.getCode(), unit11.getType(), unit11.getDisplayValue(), 
				unit11.getDescription(),unit11.getStdCode(), unit11.getStdConversionFactor(),
				unit11.getStdDisplayFigs());
		
		return model12;
	}
	
	public static Unit convertUnit2ToUnit(Unit2 unit12)
	{
		Unit model11 = new Unit();
		model11.setCode(unit12.getCode());
		model11.setDescription(unit12.getDescription());
		model11.setType(unit12.getType());
		model11.setDisplayValue(unit12.getDisplayValue());
		model11.setStdCode(unit12.getStdCode());
		model11.setStdConversionFactor(unit12.getStdConversionFactor());
		model11.setStdDisplayFigs(unit12.getStdDisplayFigs());
		
		return model11;
	}
	
	public static AmountModel convertAmountToAmountModel(Amount amount11)
	{
		AmountModel model12 = new AmountModel(amount11.getUnit().getType());
		model12.setValue(new Double(amount11.getValue()).doubleValue());
		model12.setDefaultValue(amount11.getDefaultValue());
		model12.setSigDigits(amount11.getSigDigits());
		model12.setCalculated(amount11.isCalculated());
		model12.setUnit(convertUnitToUnit2(amount11.getUnit()));
		return model12;
	}
	
	
	public static SaltFormModel convertSaltFormToSaltFormModel(SaltForm salt11)
	{
		SaltFormModel model12 = new SaltFormModel();
		model12.setCode(salt11.getCode());
		model12.setDescription(salt11.getDescription());
		model12.setFormula(salt11.getFormula());
		model12.setMolWgt(salt11.getMolWgt());
		return model12;
	}
	
	public static SaltForm convertSaltFormModelToSaltForm(SaltFormModel salt12)
	{
		SaltForm model11 = new SaltForm();
		model11.setCode(salt12.getCode());
		model11.setDescription(salt12.getDescription());
		model11.setFormula(salt12.getFormula());
		model11.setMolWgt(salt12.getMolWgt());
		return model11;
	}
	
	public static ParentCompoundModel convertCompoundToParentCompoundModel(Compound compound11)
	{
		ParentCompoundModel model12 = new ParentCompoundModel();
		model12.setViewSketch(compound11.getViewSketch());
		model12.setNativeSketch(compound11.getNativeSketch());
		model12.setNativeSketchFormat(compound11.getNativeSketchFormat());
		model12.setRegNumber(compound11.getRegNumber());
		model12.setChemicalName(compound11.getChemicalName());
		model12.setCompoundName(compound11.getCompoundName());
		model12.setCASNumber(compound11.getCASNumber());
		model12.setStereoisomerCode(compound11.getStereoisomerCode());
		model12.setMolFormula(compound11.getMolFormula());
		model12.setMolWgt(compound11.getMolWgt());
		model12.setExactMass(compound11.getExactMass());
		model12.setMeltingPt(convertAmountToAmountModel(compound11.getMeltingPt()));
		model12.setBoilingPt(convertAmountToAmountModel(compound11.getBoilingPt()));
		model12.setComments(compound11.getComments());
		model12.setHazardComments(compound11.getHazardComments());
		model12.setCreatedByNotebook(compound11.isCreatedByNotebook());
		model12.setStructureComments(compound11.getStructureComments());
		
		return model12;
	}
	
	public static Compound convertParentCompoundModelToCompound(ParentCompoundModel compound12)
	{
		Compound model11 = new Compound();
		model11.setViewSketch(compound12.getViewSketch());

		model11.setNativeSketch(extractNativeSketch(compound12));
		model11.setNativeSketchFormat(compound12.getNativeSketchFormat());
		
		model11.setRegNumber(compound12.getRegNumber());
		model11.setChemicalName(compound12.getChemicalName());
		model11.setCompoundName(compound12.getCompoundName());
		model11.setCASNumber(compound12.getCASNumber());
		model11.setStereoisomerCode(compound12.getStereoisomerCode());
		model11.setMolFormula(compound12.getMolFormula());
		model11.setMolWgt(compound12.getMolWgt());
		model11.setExactMass(compound12.getExactMass());
		//model12.setMeltingPt(convertAmountToAmountModel(compound11.getMeltingPt()));
		//model12.setBoilingPt(convertAmountToAmountModel(compound11.getBoilingPt()));
		model11.setComments(compound12.getComments());
		model11.setHazardComments(compound12.getHazardComments());
		model11.setCreatedByNotebook(compound12.isCreatedByNotebook());
		model11.setStructureComments(compound12.getStructureComments());
		
		return model11;
	}
	
	private static byte[] extractNativeSketch(ParentCompoundModel compound12) {
		byte[] nativeSketch = compound12.getNativeSketch();
		byte[] stringSketch = compound12.getStringSketch();
		if (nativeSketch == null || nativeSketch.length == 0 || (stringSketch != null && stringSketch.length > 0)) {
			try{
				String molStruct = Decoder.decodeString(compound12.getStringSketchAsString());
				byte[] structUC = molStruct.getBytes();
				ChemistryDelegate chemDel = new ChemistryDelegate();
				nativeSketch = chemDel.convertChemistry(structUC, "", compound12.getNativeSketchFormat());
			} catch (ChemUtilAccessException e) {
				log.error("Could not convert string sketch to native sketch");				
			}
		}
		return nativeSketch;
	}
	
	public static BatchModel convertAbstractBatchToBatchModel(AbstractBatch batch11)
	{
		BatchModel model12 = new BatchModel();
		model12.setBatchType(batch11.getType());
		model12.setBatchNumber(batch11.getBatchNumber());
		model12.setCompound(convertCompoundToParentCompoundModel(batch11.getCompound()));
		model12.setParentBatchNumber(batch11.getParentBatchNumber());
		model12.setConversationalBatchNumber(batch11.getConversationalBatchNumber());
		model12.setMolecularFormula(batch11.getMolecularFormula());
		model12.setMolecularWeightAmount(convertAmountToAmountModel(batch11.getMolecularWeightAmount()));
		model12.setMoleAmount(convertAmountToAmountModel(batch11.getMoleAmount()));
		model12.setWeightAmount(convertAmountToAmountModel(batch11.getWeightAmount()));
		model12.setLoadingAmount(convertAmountToAmountModel(batch11.getLoadingAmount()));
		model12.setVolumeAmount(convertAmountToAmountModel(batch11.getVolumeAmount()));
		model12.setDensityAmount(convertAmountToAmountModel(batch11.getDensityAmount()));
		model12.setMolarAmount(convertAmountToAmountModel(batch11.getMolarAmount()));
		//model12.setPreviousMolarAmount(convertAmountToAmountModel(batch11.getPreviousMolarAmount()));
		model12.setPurityAmount(convertAmountToAmountModel(batch11.getPurityAmount()));
		model12.setRxnEquivsAmount(convertAmountToAmountModel(batch11.getRxnEquivsAmount()));
		//model12.setMeltPointRange(convertAmountToAmountModel(batch11.getMeltPointRange()));
		model12.setSaltForm(convertSaltFormToSaltFormModel(batch11.getSaltForm()));
		model12.setSaltEquivs(batch11.getSaltEquivs());

		model12.setLimiting(batch11.isLimiting());
		model12.setIntermediate(batch11.isIntermediate());

		//model12.setCompoundState(batch11.getCompoundState());
		//model12.setProtectionCode(batch11.getProtectionCode());
		model12.setTransactionOrder(batch11.getTransactionOrder());
		// RegStatus should be false as it doesn't make sense to copy
		//model12.setComments(batch11.getComments());
		//model12.setStorageComments(batch11.getStorageComments());
		//model12.setHazardComments(batch11.getHazardComments());
		//vendorInfo = (ExternalSupplier) batch11.vendorInfo.deepClone();
		model12.setProjectTrackingCode(batch11.getProjectTrackingCode());
		model12.setPrecursors(batch11.getPrecursors());

		// copy chloracnegen info
		//model12.setHandlingComments(batch11.getHandlingComments());
		model12.setTestedForChloracnegen(batch11.getTestedForChloracnegen());
		model12.setChloracnegenFlag(batch11.getChloracnegenFlag());
		model12.setChloracnegenType(batch11.getChloracnegenType());
		
		return model12;
	}
	
	public static ReagentBatch convertBatchModelToReagentBatch(BatchModel batch12)
	{
		ReagentBatch model11 = new ReagentBatch();
		//Use same key for matching purposes
		model11.setKey(batch12.getKey());
		model11.setType(batch12.getBatchType());
		model11.setBatchNumber(batch12.getBatchNumber());
		model11.setCompound(convertParentCompoundModelToCompound(batch12.getCompound()));
		model11.setParentBatchNumber(batch12.getParentBatchNumber());
		model11.setConversationalBatchNumber(batch12.getConversationalBatchNumber());
		model11.setMolecularFormula(batch12.getMolecularFormula());
		model11.setMolecularWeightAmount(convertAmountModelToAmount(batch12.getMolecularWeightAmount()));
		model11.setMoleAmount(convertAmountModelToAmount(batch12.getMoleAmount()));
		model11.setWeightAmount(convertAmountModelToAmount(batch12.getWeightAmount()));
		model11.setLoadingAmount(convertAmountModelToAmount(batch12.getLoadingAmount()));
		model11.setVolumeAmount(convertAmountModelToAmount(batch12.getVolumeAmount()));
		model11.setDensityAmount(convertAmountModelToAmount(batch12.getDensityAmount()));
		model11.setMolarAmount(convertAmountModelToAmount(batch12.getMolarAmount()));
		//model12.setPreviousMolarAmount(convertAmountToAmountModel(batch11.getPreviousMolarAmount()));
		model11.setPurityAmount(convertAmountModelToAmount(batch12.getPurityAmount()));
		model11.setRxnEquivsAmount(convertAmountModelToAmount(batch12.getRxnEquivsAmount()));
		//model12.setMeltPointRange(convertAmountToAmountModel(batch12.getMeltPointRange()));
		model11.setSaltForm(convertSaltFormModelToSaltForm(batch12.getSaltForm()));
		model11.setSaltEquivs(batch12.getSaltEquivs());

		model11.setLimiting(batch12.isLimiting());
		model11.setIntermediate(batch12.isIntermediate());

		//model11.setCompoundState(batch12.getCompoundState());
		//model11.setProtectionCode(batch12.getProtectionCode());
		model11.setTransactionOrder(batch12.getTransactionOrder());
		// RegStatus should be false as it doesn't make sense to copy
		//model11.setComments(batch12.getComments());
		//model11.setStorageComments(batch12.getStorageComments());
		//model11.setHazardComments(batch12.getHazardComments());
		//vendorInfo = (ExternalSupplier) batch12.vendorInfo.deepClone();
		model11.setProjectTrackingCode(batch12.getProjectTrackingCode());
		model11.setPrecursors(batch12.getPrecursors());

		// copy chloracnegen info
		//model11.setHandlingComments(batch12.getHandlingComments());
		//model11.setTestedForChloracnegen(batch12.getTestedForChloracnegen());
		//model11.setChloracnegenFlag(batch12.getChloracnegenFlag());
		model11.setChloracnegenType(batch12.getChloracnegenType());
		
		return model11;
	}
	
	public static MonomerBatchModel convertReagentBatchToMonomerBatchModel(ReagentBatch batch11)
	{
		MonomerBatchModel model12 = new MonomerBatchModel(convertAbstractBatchToBatchModel(batch11));
		return model12;
	}
	
	public static ReagentBatch convertMonomerBatchModelToReagentBatch(BatchModel batch12)
	{
		ReagentBatch model11 = convertBatchModelToReagentBatch(batch12);
		return model11;
	}
	
	public static ArrayList<MonomerBatchModel> getReagentsListConverted(List<ReagentBatch> reaglist)
	{
		int size = reaglist.size();
		ArrayList<MonomerBatchModel> list = new ArrayList<MonomerBatchModel>(size);
		for(int i = 0; i< size ; i++)
		{
			ReagentBatch batch = reaglist.get(i);
			list.add(convertReagentBatchToMonomerBatchModel(batch));
		}
		
		return list;
	}
	
	public static void setModelChangedForMonBatchModels(List<MonomerBatchModel> listOfBatches)
	{
		if (listOfBatches != null ) {
			for(MonomerBatchModel model : listOfBatches) {
				model.setModelChanged(true);
			}
		}
	}
	
//	public static AbstractBatch  convertBatchModelToAbstractBatch(BatchModel batch12)
//	{
//		AbstractBatch model11 = null;
//		if(batch12 instanceof MonomerBatchModel)
//		{
//			model11 = new ReagentBatch();
//		}else
//		{
//			model11 = new ProductBatch();
//		}
//		model11.setCompound(convertParentCompoundModelToCompound(batch12.getCompound()));
//		return model11;
//	}
	
	public static PurityModel convertPurityToPurityModel(Purity purity) {
		PurityModel model = new PurityModel();
		model.setCode(purity.getCode());
		model.setComments(purity.getComments());
		model.setDate(purity.getDate());
		model.setDescription(purity.getDescription());
		model.setOperator(purity.getOperator());
		AmountModel amountModel = new AmountModel(UnitType.SCALAR);
		amountModel.setValue(purity.getPurityValue().GetValueInStdUnitsAsDouble());
		model.setPurityValue(amountModel);
		model.setRepresentativePurity(purity.isRepresentativePurity());
		model.setSourceFile(purity.getSourceFile());
		return model;
	}
	
	public static Purity convertPurityModelToPurity(PurityModel model) {
		Purity purity = new Purity();
		purity.setCode(model.getCode());
		purity.setComments(model.getComments());
		purity.setDate(model.getDate());
		purity.setDescription(model.getDescription());
		purity.setOperator(model.getOperator());
		Amount amount = new Amount(UnitType.SCALAR, "" + 0.0);
		amount.setValue(model.getPurityValue().getValue());
		purity.setPurityValue(amount);
		purity.setRepresentativePurity(model.isRepresentativePurity());
		purity.setSourceFile(model.getSourceFile());
		return purity;
	}
	
	public static ExternalSupplierModel convertExtSuppliertoExtSupplierModel(ExternalSupplier exSupplier) {
		ExternalSupplierModel model = new ExternalSupplierModel();
		model.setCode(ifNullReturnEmptyString(exSupplier.getCode()));
		model.setDescription(ifNullReturnEmptyString(exSupplier.getDescription()));
		model.setLoadedFromDB(false);
		model.setModelChanged(exSupplier.hasChanged());
		model.setSupplierCatalogRef(ifNullReturnEmptyString(exSupplier.getSupplierCatalogRef()));
		model.setSupplierName(ifNullReturnEmptyString(exSupplier.getSupplierName()));
		return model;
	}
	
	public static ExternalSupplier convertExtSupplierModelToExtSupplier(ExternalSupplierModel model) {
		ExternalSupplier extSupplier = new ExternalSupplier();
		extSupplier.setCode(ifNullReturnEmptyString(model.getCode()));
		extSupplier.setDescription(ifNullReturnEmptyString(model.getDescription()));
		extSupplier.setSupplierCatalogRef(ifNullReturnEmptyString(model.getSupplierCatalogRef()));
		extSupplier.setSupplierName(ifNullReturnEmptyString(model.getSupplierName()));
		return extSupplier;
	}
	
	public static BatchRegInfoModel convertRegInfoToRegInfoModel(BatchRegistrationInfo regInfo) 
		throws NumberFormatException, Exception 
	{
		BatchRegInfoModel model = new BatchRegInfoModel("Batch Key");
		model.setBatchTrackingId(Integer.parseInt(regInfo.getBatchTrackingId()));
		model.setBatchVnVInfo(convertVnVInfoToVnVInfoModel(regInfo.getBatchVnVInfo()));
		model.setCompoundSource(regInfo.getCompoundSource());
		model.setCompoundSourceDetail(regInfo.getCompoundSourceDetail());
		model.setErrorMsg(regInfo.getErrorMsg());
		model.setCompoundRegistrationHandlingCodes((ArrayList<String>) regInfo.getCompoundRegistrationHandlingCodes());
		model.setCompoundRegistrationHazardCodes((ArrayList<String>) regInfo.getCompoundRegistrationHazardCodes());
		model.setCompoundRegistrationStorageCodes((ArrayList<String>) regInfo.getCompoundRegistrationStorageCodes());
		model.setHitId(regInfo.getHitId());
		model.setJobId(regInfo.getJobId());
		model.setLoadedFromDB(false);
		model.setModelChanged(regInfo.isModified());
		//model.setRegistrationDate(regInfo.getRegistrationDate().toString());
		return model;
	}

	public static BatchRegistrationInfo convertRegInfoModelToRegInfo(BatchRegInfoModel model) 
		throws NumberFormatException, Exception {
		try {
			BatchRegistrationInfo regInfo = new BatchRegistrationInfo();
			regInfo.setBatchTrackingId("" + model.getBatchTrackingId());
			regInfo.setBatchVnVInfo(convertVnVInfoModelToVnVInfo(model.getBatchVnVInfo()));
			regInfo.setCompoundSource(model.getCompoundSource());
			regInfo.setCompoundSourceDetail(model.getCompoundSourceDetail());
			regInfo.setErrorMsg(model.getErrorMsg());
			regInfo.setCompoundRegistrationHandlingCodes(model.getCompoundRegistrationHandlingCodes());
			regInfo.setCompoundRegistrationHazardCodes(model.getCompoundRegistrationHazardCodes());
			regInfo.setCompoundRegistrationStorageCodes(model.getCompoundRegistrationStorageCodes());
			regInfo.setHitId(model.getHitId());
			regInfo.setJobId("" + model.getJobId());
			regInfo.setResidualSolventList(model.getResidualSolventList());
			regInfo.setSolubilitySolventList(model.getSolubilitySolventList());
			regInfo.setModified(model.isModelChanged());
			//regInfo.setRegistrationDate(model.getRegistrationDate());  vb ? what is the format
			regInfo.setRegistrationStatus(model.getRegistrationStatus());
			return regInfo;
		} catch (NumberFormatException e) {
			throw e;
		} catch (Exception e) {  // In case of null pointer
			throw e;
		}
	}
	public static BatchVnVInfoModel convertVnVInfoToVnVInfoModel(BatchVnVInfo info) {
		BatchVnVInfoModel model = new BatchVnVInfoModel();
		model.setErrorMsg(info.getErrorMsg());
		model.setLoadedFromDB(false);
		model.setModelChanged(info.hasChanged());
		model.setMolData(info.getMolData());
		model.setStatus(info.getStatus());
		model.setSuggestedSICList(info.getSuggestedSICList());
		return model;
	}
	
	public static BatchVnVInfo convertVnVInfoModelToVnVInfo(BatchVnVInfoModel model) {
		BatchVnVInfo info = new BatchVnVInfo();
		info.setErrorMsg(model.getErrorMsg());
		info.setModified(model.isModelChanged());
		info.setMolData(model.getMolData());
		info.setStatus(model.getStatus());
		info.setSuggestedSICList(model.getSuggestedSICList());
		return info;
	}

	
/*	public static List convertPurityListToPurityModelList(List purityList) {
		ArrayList purityModelList = new ArrayList();
		for (int i=0; i<purityList.size(); i++) {
			purityModelList.add(purityList.get(i));
		}
		return purityModelList;
	}*/
	
	public static List<Purity> convertPurityModelListToPurityList(List<PurityModel> purityModelList) {
		ArrayList<Purity> purityList = new ArrayList<Purity>();
		for (int i=0; i<purityModelList.size(); i++) {
			purityList.add(convertPurityModelToPurity((PurityModel) purityModelList.get(i)));
		}
		return purityList;
	}
	
//	public static ReagentBatch convertMonomerBatchModelIntoReagentBatch(StoicModelInterface stoicModel)
//	{
//		ReagentBatch batch = null;
//		if(stoicModel instanceof MonomerBatchModel)
//		{
//			MonomerBatchModel mnModel = (MonomerBatchModel)stoicModel;
//			
//			batch = (ReagentBatch)convertBatchModelToAbstractBatch(mnModel);
//			return batch;
//		}else
//		{
//			return batch;
//		}
//		
//	}
//	
	private static String ifNullReturnEmptyString(String s) {
		if (s == null) return "";
		else return s;
	}
	
	
	public static AnalysisModel convertAnlaysisToAnalysisModel(Analysis sModel)
	{
		AnalysisModel pModel = new AnalysisModel();
		pModel.setAnnotation(sModel.getAnnotation());
		pModel.setCenSampleRef(sModel.getCenSampleRef());
		pModel.setComments(sModel.getComments());
		pModel.setCyberLabDomainId(sModel.getCyberLabDomainId());
		pModel.setCyberLabFileId(sModel.getCyberLabFileId());
		pModel.setCyberLabLCDFId(sModel.getCyberLabLCDFId());
		pModel.setCyberLabUserId(sModel.getCyberLabUserId());
		pModel.setDomain(sModel.getDomain());
		pModel.setExperiment(sModel.getExperiment());
		pModel.setExperimentTime(sModel.getExperimentTime());
		pModel.setFileName(sModel.getFileName());
		pModel.setFileSize(sModel.getFileSize());
		pModel.setFileType(sModel.getFileType());
		pModel.setAnalyticalServiceSampleRef(sModel.getAnalyticalServiceSampleRef());
		pModel.setGroupId(sModel.getGroupId());
		pModel.setInstrument(sModel.getInstrument());
		pModel.setInstrumentType(sModel.getInstrumentType());
		pModel.setLinked(sModel.isLinked());
		//pModel.setLoadedFromDB(isLoadedFromDB);
		pModel.setModelChanged(sModel.isModelChanged());
		pModel.setServer(sModel.getServer());
		pModel.setSite(sModel.getSite());
		pModel.setUrl(sModel.getUrl());
		pModel.setUserId(sModel.getUserId());
		pModel.setVersion(sModel.getVersion());
		pModel.setIPRelated(sModel.isIPRelated());
		
		return pModel;
		
	}
	
	public static Analysis convertAnlaysisModelToAnalysis(AnalysisModel pModel)
	{
		Analysis sModel = new Analysis();
		sModel.setAnnotation(pModel.getAnnotation());
		sModel.setCenSampleRef(pModel.getCenSampleRef());
		sModel.setComments(pModel.getComments());
		sModel.setCyberLabDomainId(pModel.getCyberLabDomainId());
		sModel.setCyberLabFileId(pModel.getCyberLabFileId());
		sModel.setCyberLabLCDFId(pModel.getCyberLabLCDFId());
		sModel.setCyberLabUserId(pModel.getCyberLabUserId());
		sModel.setDomain(pModel.getDomain());
		sModel.setExperiment(pModel.getExperiment());
		sModel.setExperimentTime(pModel.getExperimentTime());
		sModel.setFileName(pModel.getFileName());
		sModel.setFileSize(pModel.getFileSize());
		sModel.setFileType(pModel.getFileType());
		sModel.setAnalyticalServiceSampleRef(pModel.getAnalyticalServiceSampleRef());
		sModel.setGroupId(pModel.getGroupId());
		sModel.setInstrument(pModel.getInstrument());
		sModel.setInstrumentType(pModel.getInstrumentType());
		sModel.setLinked(pModel.isLinked());
		//pModel.setLoadedFromDB(isLoadedFromDB);
		//sModel.setModelChanged(pModel.isModelChanged());
		sModel.setServer(pModel.getServer());
		sModel.setSite(pModel.getSite());
		sModel.setUrl(pModel.getUrl());
		sModel.setUserId(pModel.getUserId());
		sModel.setVersion(pModel.getVersion());
		sModel.setIPRelated(pModel.isIPRelated());
		return sModel;
	}
	
	public static Amount convertAmountModelToAmount(AmountModel amount12)
	{
		Amount model11 = new Amount();
		model11.setValue(amount12.getValue());
		model11.setDefaultValue(amount12.getDefaultValue());
		model11.setCalculated(amount12.isCalculated());
		model11.setUnit(convertUnit2ToUnit(amount12.getUnit()));
		return model11;
	}
	
	
	public static ArrayList<ReagentBatch> getMonomerBatchListConvertedToReagentsList(List<MonomerBatchModel> reaglist)
	{
		int size = reaglist.size();
		ArrayList<ReagentBatch> list = new ArrayList<ReagentBatch>(size);
		for(int i = 0; i< size ; i++)
		{
			MonomerBatchModel batch = (MonomerBatchModel)reaglist.get(i);
			list.add(convertMonomerBatchModelToReagentBatch(batch));
		}
		
		return list;
	}

	
//	public static AbstractBatch convertStoicModelInterfaceIntoReagentBatch(
//			StoicModelInterface stoicModel) {
//		ReagentBatch batch = null;
//		if(stoicModel == null) return null;
//		if(stoicModel instanceof MonomerBatchModel)
//		{
//			MonomerBatchModel mnModel = (MonomerBatchModel)stoicModel;
//			
//			batch = (ReagentBatch)convertBatchModelToAbstractBatch(mnModel);
//			return batch;
//		}else if(stoicModel instanceof BatchesList)
//		{
//			//if BatchesList has more than one model then we cannot convert
//			BatchesList stoicList = (BatchesList)stoicModel;
//			if(stoicList.getListSize() == 1)
//			{
//				if(stoicList.getBatchModels().get(0) instanceof MonomerBatchModel)
//				{
//					MonomerBatchModel mnModel = (MonomerBatchModel)stoicList.getBatchModels().get(0);
//					batch = (ReagentBatch)convertBatchModelToAbstractBatch(mnModel);
//					return batch;
//				}
//			}
//			return batch;
//		}else
//			return batch;	
//	}

}
