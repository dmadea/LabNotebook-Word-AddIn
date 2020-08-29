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
import com.chemistry.enotebook.util.Stopwatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Utility class to dupe and dedupe the batchlists among summary and intermediate steps for data optimization while
 * persisting to db and also for data serialization between client and server.
 * 
 * Batches lists in Summary step will be reused in the intermediate steps if this batchlist is used in that particular intermediate
 * step.
 * 
 * summary step -> A + B + C -> P(A:B:C) 
 * step1 -> A + B -> P'(A:B) 
 * step2 -> P'([A:B])+ C -> P([A:B]:C)
 * 
 * Batches lists for A,B,C,P will be persisted once.
 * Batches lists P'(as product), P'(as monomer) will be persisted.
 * step1 will re use A,B,P'(prod) batches List
 * step2 will re use P'(monomer),C ,P batches List
 * 
 * step1 only P'(prod) data is required from Design Service. ( A,B batches data from summary step ) step2 only P'(monomer) data is required
 * from Design Service ( C,P batches data from summary step)
 * 
 * Deduper should also match the positions of batchesList between summary steps and intermediates steps i.e A list in summary step
 * should be matched to monomers lists in Step1 to see if A list in step1 is same as A list in summary step. same case with
 * intermediate products that are used as monomers in following step.
 * 
 * Position Matching criteria:
 * 
 * For monomers: - Compare List size between Summary and intermediate batchesList first.if A is 10 in summary there should be same
 * size list in step1 possibly with same position A. - If List size doesn't match Get a monomer ID from the list in step 1 and find
 * which list it is present in summary step monomer lists. Match the position between both the matching batchesList.
 * 
 * 
 * For products: ( with library shaping )
 * For matching summary step products vs last interim step products don't compare size of the product batches
 * as library shaping can occur at summary or at intermediate steps.
 * 
 * So compare the smallest product list among summary and final intermediate step and see if the smallest set
 * is subset of the larger product list. IF yes choose the smallest list and assign the list to the other step
 * which has largest product set.
 * 
 * ex: if summary has 200 prod but step2 has 167 prod. See if 167 is subset of 200 and assign the 167 batches list
 * to summary step products. Vice versa if summary step has 167 and step 2 has 200. 
 * 
 */

public class ParallelExpDuperDeDuper {

	private static final Log log = LogFactory.getLog(ParallelExpDuperDeDuper.class);
	
	private ReactionStepModel summaryStep;
	
	NotebookPageModel pageModel;

	private int totalSteps = 1;

	private int totalIntermSteps = 0;

	public ParallelExpDuperDeDuper() {

	}

	/*
	 * Does the data optimization by sharing batches lists that are reused.
	 */
	public NotebookPageModel deDupeParallelExperiment(NotebookPageModel pageModel) {

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ParallelExpDuperDeDuper.deDupePllelExp()");
		// Check if model has data
		if (pageModel == null)
			return pageModel;

		// Get the total number of steps ( Summary + Intermediate )
		this.totalSteps = pageModel.getPageHeader().getTotalReactionSteps();
		this.pageModel = pageModel;

		// If there are no steps or if it is only summary step no dedupe
		// required
		// Also total steps can be never 2(i.e 1 sum step and 1 interm step is
		// not valid).
		// Always there need to be two or more intermediate steps.(i.e
		// totalSteps can be 1 or >= 3)
		/*
		 * select r.syn ,r.cnt from (select p.SYNTH_PLAN_ID syn ,count(*) cnt from dsp_owner.DSP_PLAN_INSTANCE p group by
		 * p.SYNTH_PLAN_ID ) r where (r.cnt = 1 or r.cnt >= 3)
		 * 
		 * select r.syn ,r.cnt from (select p.SYNTH_PLAN_ID syn ,count(*) cnt from dsp_owner.DSP_PLAN_INSTANCE p group by
		 * p.SYNTH_PLAN_ID ) r where (r.cnt = 2)
		 */
		if (totalSteps == 0 || totalSteps == 1) {
			log.info("No intermediate steps to dedupe experiment with spid:" + pageModel.getSPID());
			return pageModel;
		}

		// get summary step and set it as it will be referenced later many times
		this.summaryStep = pageModel.getSummaryReactionStep();
		// get intermedaite steps
		ArrayList<ReactionStepModel> intermSteps = pageModel.getIntermediateReactionSteps();
		// set total IntermSteps.It should always be >= 2
		this.totalIntermSteps = intermSteps.size();
		log.info("Total no.  of intermediate steps:" + this.totalIntermSteps);
		ArrayList<ReactionStepModel> dedupedIntermSteps = new ArrayList<ReactionStepModel>();
		log.info("Summary step total monomer lists:" + this.summaryStep.getMonomers().size());
		for (int i = 0; i < totalIntermSteps; i++) {
			ReactionStepModel interStep = (ReactionStepModel) intermSteps.get(i);
			// send for dedupe
			ReactionStepModel dedupedStep = dedupeIntermediateStep(interStep);
			// add to dedupe list
			dedupedIntermSteps.add(dedupedStep);
		}

		// Add the summaryStep to Page Model
		dedupedIntermSteps.add(this.summaryStep);
		// Set back deduped list to pageModel
		pageModel.setReactionSteps(dedupedIntermSteps);

		stopwatch.stop();
		return pageModel;
	}

	// Has the actual logic to dedupe
	private ReactionStepModel dedupeIntermediateStep(ReactionStepModel interimStep) {
		log.debug("--------- Deduping intermediate step:" + interimStep.getStepNumber());
		// Match and deduped MonomerLists
		ArrayList<BatchesList<MonomerBatchModel>> intermStepMonomerDeDupedList = new ArrayList<BatchesList<MonomerBatchModel>>();
		// Get List of intermStep monomers
		ArrayList<BatchesList<MonomerBatchModel>> intermStepMonomerList = interimStep.getMonomers();
		log.debug("Intermediate step total monomer lists:" + intermStepMonomerList.size());
		if (intermStepMonomerList != null && intermStepMonomerList.size() > 0) {
			int stepMonSize = intermStepMonomerList.size();
			for (int i = 0; i < stepMonSize; i++) {
				BatchesList<MonomerBatchModel> dedupedList = dedupeMonomerBatchesList(intermStepMonomerList.get(i), interimStep.getStepNumber());
				intermStepMonomerDeDupedList.add(dedupedList);
			}
			// set back these list to Step monomers
			interimStep.setMonomers(intermStepMonomerDeDupedList);
			log.debug("Intermediate step total monomer lists after dedupe:" + intermStepMonomerDeDupedList.size());
		}

		// Match product Lists only if this intermediate step is the last step of intermediate steps
		if (interimStep.getStepNumber() == this.totalIntermSteps) {
			// Now match the product in the interm step with summary step products
			// Usually it is 1:1

			ArrayList<BatchesList<ProductBatchModel>> intermStepProdDeDupedList = new ArrayList<BatchesList<ProductBatchModel>>();
			// Get List of intermStep Products
			ArrayList<BatchesList<ProductBatchModel>> intermStepProdList = interimStep.getProducts();
			log.debug("Intermediate step total product lists:" + intermStepProdList.size());
			if (intermStepProdList != null && intermStepProdList.size() > 0) {
				int stepProdSize = intermStepProdList.size();
				for (int i = 0; i < stepProdSize; i++) {
					BatchesList<ProductBatchModel> dedupedList = dedupeProductBatchesList(intermStepProdList.get(i));
					intermStepProdDeDupedList.add(dedupedList);
				}
				// set back these list to Step products
				interimStep.setProducts(intermStepProdDeDupedList);
				log.debug("Intermediate step total product lists after dedupe:" + intermStepProdList.size());
			}

		} else {
			log.debug("Intermediate step" + interimStep.getStepNumber() + " Products not matched");
		}
		return interimStep;
	}

	private BatchesList<MonomerBatchModel> dedupeMonomerBatchesList(BatchesList<MonomerBatchModel> inputList, int currStep) {
		// Get List of summary monomers
		ArrayList<BatchesList<MonomerBatchModel>> sumStepMonomerList = this.summaryStep.getMonomers();
		boolean isDeduped = false;
		// input list size
		int ilBatchSize = inputList.getBatchModels().size();
		// No batch models in this List. No need to dedupe
		if (ilBatchSize == 0)
			return inputList;
		String ilPosition = inputList.getPosition();
		String ilMonID1 = inputList.getBatchModels().get(0).getMonomerId();
		log.debug("Monomer InputList Pos:" + ilPosition + 
		          " monomer id used:" + ilMonID1 + 
		          " Batches size:" + ilBatchSize);

		// if inputList id is intermediate product don't need to dedupe
		if (ilMonID1.startsWith("[") || ilMonID1.endsWith("]")) {
			log.debug("This Batcheslist is Intrm product in prev step and participating as monomer in this step." +
					  "No need to dedupe" + ilMonID1);
			//Copy over the previous step intermediate products structures to this mononers list
			//Since Design Service doesn't provide the structures to monomers and we should be using VCR id 
			//to lookup structure for monomers which are intermediate products in previous step
			return copyOverStructures(inputList,currStep);
			
		}
		// Look through all monomerLists in summaryStep
		for (BatchesList<MonomerBatchModel> sourceList : sumStepMonomerList) {
			// source MonomerList size
			int slBatchSize = sourceList.getBatchModels().size();
			String slPosition = sourceList.getPosition();
			String slKey = sourceList.getListKey();
			// If there are not batches in SList continue with others
			if (slBatchSize == 0)
				continue;
			// get one monomer id in the list
			log.debug("Monomer SourceList Pos:" + slPosition + "Batches size:" + slBatchSize);
			// see if size of list matches first
			if (slBatchSize == ilBatchSize) {
				log.debug("SourceList and InputList size matches.");
				// Check for monomer id match
				if (hasAMatch(ilMonID1, sourceList.getMonomerIDList())) {
					// if there is a match assign key as sourcelist key and set
					// boolean variable to true
					isDeduped = true;
					inputList = new BatchesList<MonomerBatchModel>(slKey, ilPosition, sourceList.getBatchModels(), isDeduped);
					if (!slPosition.equals(ilPosition)) {
						log.debug("Positions of the Lists don't match");
					}
					log.debug("Step monomer BatchesList(" + ilPosition
							+ ") was matched and deduped with summary monomer list (" + slPosition + ")");
					break;
				} else {
					continue;
				}
			} else {
				continue;
			}
		}
		return inputList;
	}

	public boolean hasAMatch(String id, ArrayList<String> idList) {
		boolean hasAHit = false;

		int size = idList.size();
		for (int i = 0; i < size; i++) {
			String source = (String) idList.get(i);
			// System.out.println("ID:"+id+" -- S:"+source);
			if (id.equalsIgnoreCase(source)) {
				log.debug("Has Matching pair ID:" + id + " -- S:" + source);
				hasAHit = true;
				break;
			}
		}

		return hasAHit;
	}

	//This method should be called only for the last step of intermediate steps
	private BatchesList<ProductBatchModel> dedupeProductBatchesList(BatchesList<ProductBatchModel> inputList) {
		// Get List of summary products
		ArrayList<BatchesList<ProductBatchModel>> sumStepProductList = summaryStep.getProducts();
		boolean isDeduped = false;
		// input list size
		int ilBatchSize = inputList.getBatchModels().size();
		// No batch models in this List. No need to dedupe. Need to assign summary products to this
		//if (ilBatchSize == 0)
		//	return inputList;
		String ilPosition = inputList.getPosition();
		String ilProdID1 = inputList.getBatchModels().get(0).getProductId();
		log.debug("Interm step Products Pos:" + ilPosition + " product id used:" + ilProdID1 + " Batches size:" + ilBatchSize);

		// Look through all productLists in summaryStep
		for (BatchesList<ProductBatchModel> sourceList : sumStepProductList) {
			// source productList size
			int slBatchSize = sourceList.getBatchModels().size();
			
			if(slBatchSize == 0 || ilBatchSize == 0) {
				continue;	
			}
			
			String sumProdID1 = sourceList.getBatchModels().get(0).getProductId();
			String slPosition = sourceList.getPosition();
			String slKey = sourceList.getListKey();
			
			// get one monomer id in the list
			log.debug("Summ step Product Pos:" + slPosition + " Batches size:" + slBatchSize);
			
			// see if size of lists matches first
			if (slBatchSize == ilBatchSize) {
				log.debug("summary step prod List and interim step products size matches.");
				// Check for monomer id match
				if (hasASpecialMatch(ilProdID1, sourceList.getProductIDList())) {
					// if there is a match assign key as sourcelist key and set
					// boolean variable to true
					isDeduped = true;
					inputList = new BatchesList<ProductBatchModel>(slKey, ilPosition, sourceList.getBatchModels(), isDeduped);
					if (!slPosition.equals(ilPosition)) {
						log.debug("Positions of the Lists don't match");
					}
					log.debug("Step product BatchesList(" + ilPosition + ") was matched and deduped with summary product list (" + slPosition + ")");
					break; //if there is match break the loop
				}
			}			
			//if summaryStep prod size less than(<) final step product size.Library shaping has occured in summary step.
			else if (slBatchSize < ilBatchSize) {
				log.debug("Summary step prod List is smaller than interim step products size.");
				//check if product id in the small list matches in the large set.
				if (hasASpecialMatch(sumProdID1, inputList.getProductIDList())) {
					// if there is a match assign key as sourcelist key and set
					// boolean variable to true
					isDeduped = true;
					//Assign the summaryStep prod list to this interm step prod list
					inputList = new BatchesList<ProductBatchModel>(slKey, ilPosition, sourceList.getBatchModels(), isDeduped);
				}				
			}
			//if final step product size less than(<) summary step prods size.Intermedaite step library shapping occured
			else if (ilBatchSize < slBatchSize) {
				log.debug("Interim step products size smaller than summary step prod List.");
				//check if product id in the small list matches in the large set.
				if (hasASpecialMatch(ilProdID1, sourceList.getProductIDList())) {
					// if there is a match assign key as sourcelist key and set
					// boolean variable to true
					isDeduped = true;
					inputList = new BatchesList<ProductBatchModel>(slKey, ilPosition, inputList.getBatchModels(), isDeduped);
					//Assign the interim step prod list to summary prod list
					sourceList = new BatchesList<ProductBatchModel>(slKey, ilPosition, inputList.getBatchModels(), false);
				}
			} else {
				continue;	
			}
		}
		return inputList;
	}

	public boolean hasASpecialMatch(String id, ArrayList<String> idList) {
		boolean hasAHit = false;
		// Id will be [A:B]:C in final interm step product and A:B:C in summary step product
		id = id.replaceAll("\\[", "");
		id = id.replaceAll("\\]", "");
		int size = idList.size();
		for (int i = 0; i < size; i++) {
			String source = (String) idList.get(i);
			source = source.replaceAll("\\[", "");
			source = source.replaceAll("\\]", "");
			//log.debug("ID:" + id + " -- S:" + source);
			if (id.equalsIgnoreCase(source)) {
				log.debug("Has Matching pair ID:" + id + " -- S:" + source);
				hasAHit = true;
				break;
			}
		}

		return hasAHit;
	}

	/*
	 * Does the duplication of shared batches lists so that each step has its own copy of batch lists for Client side GUI.
	 */
	public void dupeParallelExperiment() {

	}
	
	private BatchesList<MonomerBatchModel> copyOverStructures(BatchesList<MonomerBatchModel> inputList, int currStep)
	{
		//From step 2
		if(currStep> 1)
		{
			//get previous step(for products)
			ReactionStepModel reacModel = this.pageModel.getReactionStep((currStep-1));

			if (reacModel.getProducts() != null && reacModel.getProducts().size() > 0)
			{
				//Assuming only one intermediate product
				BatchesList<ProductBatchModel> products = reacModel.getProducts().get(0);
				if(products != null)
				{
					if(products.getBatchModels().size() == inputList.getBatchModels().size())
					{
						List<ProductBatchModel> prodList = products.getBatchModels();
						for(MonomerBatchModel mModel : inputList.getBatchModels()) {
							ProductBatchModel pModel = getMatchingProduct(mModel.getMonomerId(),prodList);
							if (pModel != null) {
								mModel.getCompound().setStringSketchFormat(pModel.getCompound().getStringSketchFormat());
								mModel.getCompound().setStringSketch(pModel.getCompound().getStringSketch());
							}
						}
						
					} else {
						log.debug("intermediate products and monomers in next step doesn't match");
					}
				}
			}
		}
		return inputList;
	}
	
	
	private ProductBatchModel getMatchingProduct(String id , List<ProductBatchModel> prodList)
	{
		id = id.replaceAll("\\[", "");
		id = id.replaceAll("\\]", "");
		//System.out.print("Input id:"+id);
		for(ProductBatchModel pModel : prodList) {
			//System.out.print("product id:"+pModel.getProductId());
			String prodId = pModel.getProductId();
			prodId = prodId.replaceAll("\\[", "");
			prodId = prodId.replaceAll("\\]", "");
			if(prodId.equalsIgnoreCase(id))
			{
				//System.out.println("Matching id found:"+id+ "---"+pModel.getProductId());
				return pModel;
			}
		}
		
		return null;
	}
	
	
	public NotebookPageModel dedupeParallelExpAfterPartialLoadFromDB(NotebookPageModel pageModel)
	{
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ParallelExpDuperDeDuper.dedupeParallelExpAfterPartialLoadFromDB()");
		int intermStepSize = pageModel.getIntermediateReactionSteps().size();
		//If there are no intermediate steps return the same
		if(pageModel != null &&  intermStepSize < 2)
		{
			return pageModel;	
		}
		
		ArrayList<BatchesList<MonomerBatchModel>> sumMonomers = pageModel.getSummaryReactionStep().getMonomers();
		ArrayList<BatchesList<ProductBatchModel>> sumProducts = pageModel.getSummaryReactionStep().getProducts();
		//For each step
		for(int i=0;i<intermStepSize ; i ++)
		{
			//Since interm step starts from 1
			int stepNo = i+ 1;
			ReactionStepModel step = pageModel.getReactionStep(stepNo);
			ArrayList<BatchesList<MonomerBatchModel>> stepMonomers = step.getMonomers();
			ArrayList<BatchesList<ProductBatchModel>> stepProducts = step.getProducts();
			
			// Match Monomers
			for (BatchesList<MonomerBatchModel> monBList : stepMonomers) {
//				matchBatchesWithListKey(monBList, sumMonomers);
				String inputListKey =  monBList.getListKey();
				for(BatchesList<MonomerBatchModel> compareTo : sumMonomers) {
					if(inputListKey.equalsIgnoreCase(compareTo.getListKey()))
					{
						//There is a match
						monBList.setDedupedList(true);
						monBList.setBatchModels(compareTo.getBatchModels());
						log.debug("Dedupe match monomer for interm step pos:" + monBList.getPosition() + 
						          " with sum step pos:" + compareTo.getPosition());
						break;
					}
				}
				log.debug("After matching monomers the dedupe is :" +
							monBList.isDedupedList());
			}

			// Match Products
			for (BatchesList<ProductBatchModel> prodBList : stepProducts) {
//				matchBatchesWithListKey(prodBList, sumProducts);
				String inputListKey =  prodBList.getListKey();
				for(BatchesList<ProductBatchModel> compareTo : sumProducts) {
					if(inputListKey.equalsIgnoreCase(compareTo.getListKey()))
					{
						//There is a match
						prodBList.setDedupedList(true);
						prodBList.setBatchModels(compareTo.getBatchModels());
						log.debug("Dedupe match product for interm step pos:" + prodBList.getPosition() + 
						          " with sum step pos:" + compareTo.getPosition());
						break;
					}
				}
				log.debug("After matching proudcts the dedupe is :" +
							prodBList.isDedupedList());
			}
		}
		stopwatch.stop();
		return pageModel;
	}
	
	
	//This method matches the Monomer and Product batches list based on the list key
//	private BatchesList matchBatchesWithListKey(BatchesList inputList, ArrayList<BatchesList> sumBatchesList)
//	{
//		String inputListKey =  inputList.getListKey();
//		for(BatchesList compareTo : sumBatchesList) {
//			if(inputListKey.equalsIgnoreCase(compareTo.getListKey()))
//			{
//				//There is a match
//				inputList.setDedupedList(true);
//				inputList.setBatchModels(compareTo.getBatchModels());
//				log.debug("Dedupe match for interm step pos:" + inputList.getPosition() + 
//				          " with sum step pos:" + compareTo.getPosition());
//				break;
//			}
//		}
//		return inputList;
//	}
	
	
	/**
	 * This method sorts the BatchesList objects based on their position A,B,C etc
	 * @param pageModel
	 */
	public void sortMonomerBatchesInSteps(NotebookPageModel pageModel)
	{
		if (pageModel == null ||
			pageModel.getSummaryReactionStep() == null) {
			return;
		}
		ArrayList<BatchesList<MonomerBatchModel>> stepMon = pageModel.getSummaryReactionStep().getMonomers();
		if (stepMon != null) {
			java.util.Collections.sort(stepMon);
		}

		int itrmStepSize = pageModel.getIntermediateReactionSteps().size();
		if (itrmStepSize > 0) {
			for (int i = 1; i <= itrmStepSize; i++) {
				ArrayList<BatchesList<MonomerBatchModel>> stepIntrm = pageModel.getReactionStep(i).getMonomers();
				if (stepIntrm != null) {
					java.util.Collections.sort(stepIntrm);
				}
			}
		}
	}
	
	
	

}
