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

import java.util.*;

public class ExperimentPageUtils {
	private static final Log log = LogFactory.getLog(ExperimentPageUtils.class);

	public double getTotalAmountInWellsForABatch(String batchKey, NotebookPageModel pageModel) {
		double totalAmount = 0.0;
		if (pageModel != null && pageModel.getBatchPlateWellsMap() != null) {
			List<PlateWell<? extends BatchModel>> list = getMatchingWellsInMap(pageModel.getBatchPlateWellsMap(), batchKey);
			int size = list.size();
			for (int i = 0; i < size; i++) {
				PlateWell<? extends BatchModel> well = list.get(i);
				totalAmount = totalAmount + well.getContainedWeightAmount().GetValueInStdUnitsAsDouble();
			}
		}
		return totalAmount;
	}

	public MonomerBatchModel getMatchingMonomerBatchInTheExperiment(String monomerID, NotebookPageModel pageModel) {
		HashMap<String, MonomerBatchModel> map = pageModel.getMonomerBatchModelMap();
		Collection<MonomerBatchModel> list = map.values();
		if (list != null && list.size() > 0) {
			Iterator<MonomerBatchModel> iter = list.iterator();
			while (iter.hasNext()) {
				MonomerBatchModel batchModel = (MonomerBatchModel) iter.next();
				if (batchModel.getMonomerId().compareToIgnoreCase(monomerID) == 0) {
					return batchModel;
				}
			}
		}

		return null;
	}

	public MonomerBatchModel getMatchingStoicBatchInTheSingletonExperiment(String monomerID, NotebookPageModel pageModel) {
		try {
			if (!pageModel.isParallelExperiment()) {
				ReactionStepModel step = pageModel.getReactionSteps().get(0);
				BatchesList<MonomerBatchModel> stoicBatches = step.getStoicBatchesList();
				Collection<MonomerBatchModel> batches = stoicBatches.getBatchModels();
				for (MonomerBatchModel batchModel : batches) {
					if (batchModel.getMonomerId().compareToIgnoreCase(monomerID) == 0) {
						return batchModel;
					}
				}
			}
		} catch(NullPointerException ignored) {}
		
		return null;
	}

	private List<PlateWell<? extends BatchModel>> getMatchingWellsInMap(HashMap<BatchModel, ArrayList<PlateWell<? extends BatchModel>>> batchPlateWellMap, String key) {
		List<PlateWell<? extends BatchModel>> list = new ArrayList<PlateWell<? extends BatchModel>>();
		Set<BatchModel> keySet = batchPlateWellMap.keySet();
		for (BatchModel batchModel : keySet) {
			if (batchModel.getKey().compareToIgnoreCase(key) == 0) {
				list = batchPlateWellMap.get(batchModel);
			}
		}

		return list;
	}

	// this method should be called after every add/update of the page model.
	// this method is also called after pageModel load
	public void refreshMonomerBatchToPlateWellsMapping(NotebookPageModel pageModel) {
		ParallelExpModelUtils util = new ParallelExpModelUtils(pageModel);
		util.linkBatchesWithPlateWells();
		calcTheTotalWeightInWellsForAllBatches(pageModel);
	}

	public void calcTheTotalWeightInWellsForAllBatches(NotebookPageModel pageModel) {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("ParallelExpModelUtils.calcTheTotalWeightInWellsForAllBatches()");
		// Loop through the HashMap key and calc the property
		HashMap map = pageModel.getBatchPlateWellsMap();
		if (map == null) {
			log.info("batchPlateWellsMap is null");
			return;
		}
		Set batchesSet = map.keySet();
		if (batchesSet == null) {
			log.info("batchPlateWellsMap key set is null");
			return;
		}
		Iterator iter = batchesSet.iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (obj instanceof MonomerBatchModel) {
				MonomerBatchModel model = (MonomerBatchModel) obj;
				double totalWeightInWells = getTotalAmountInWellsForABatch(model.getKey(), pageModel);
				model.getTotalWeight().setValue(totalWeightInWells);
			}
		}
		stopwatch.stop();
	}

	public static List sortStoicElementsByTransactionOrder(List list) {
		if (list == null || list.size() == 0)
			return null;
		int size = list.size();
		ArrayList newList = new ArrayList(size);

		for (int i = 0; i < size; i++) {
			StoicModelInterface iface = (StoicModelInterface) list.get(i);
			int tran = iface.getStoicTransactionOrder();

			if (tran == -1) {
				tran = i;
				iface.setStoicTransactionOrder(tran);
			}
			/*
			 * if (newList.size() < (tran-1)) { //don't use set. Set will overide the object at current location newList.add(tran,
			 * iface); }else { newList.add(tran, iface); System.out.print("Two stoich elements with same transaction order."); }
			 */
			newList.add(tran, iface);
		}
		return newList;
	}

	public static void sortStoicElementsInListOnTransactionOrder(List list) {
		// If List is null or size is less than 2 there is nothing to sort
		if (list == null || list.size() < 2)
			return;
		int listsize = list.size();
		for (int k = 0; k < listsize; k++) {
			int size = (listsize - k);
			for (int i = 0; i < (size - 1); i++) {
				StoicModelInterface iface = (StoicModelInterface) list.get(i);
				int tran = iface.getStoicTransactionOrder();

				StoicModelInterface ifaceNext = (StoicModelInterface) list.get(i + 1);
				int tranNext = ifaceNext.getStoicTransactionOrder();

				if (tran > tranNext) {
					StoicModelInterface temp = ifaceNext;
					list.set((i + 1), iface);
					list.set(i, temp);
				}
			}
		}
	}

	public MonomerBatchModel getMonomerBatchInTheExperiment(String monomerKey, NotebookPageModel pageModel) {
		HashMap map = pageModel.getMonomerBatchModelMap();
		if (map != null && map.get(monomerKey) != null) {
			return (MonomerBatchModel) map.get(monomerKey);
		} else
			return null;
	}

	public ProductBatchModel getProductBatchInTheExperiment(String productKey, NotebookPageModel pageModel) {
		HashMap<String, ProductBatchModel> map = pageModel.getProductBatchModelMap();
		if (map.get(productKey) != null) {
			return (ProductBatchModel) map.get(productKey);
		} else
			return null;
	}

	public synchronized void setPrecusorMap(NotebookPageModel pageModel) {
		Map precursorMap = pageModel.getPrecursorMap();
		List productBatches = pageModel.getAllProductBatchModelsInThisPage();
		for (Iterator bit = productBatches.iterator(); bit.hasNext();) {
			ProductBatchModel pbatch = (ProductBatchModel) bit.next();
			List monomerBatchKeys = pbatch.getReactantBatchKeys();
			for (Iterator mit = monomerBatchKeys.iterator(); mit.hasNext();) {
				String monomerKey = (String) mit.next();
				List productKeys;
				if (precursorMap.containsKey(monomerKey)) {
					productKeys = (List) precursorMap.get(monomerKey);
				} else { // new entry
					productKeys = new ArrayList();
				}
				productKeys.add(pbatch.getKey());
				precursorMap.put(monomerKey, productKeys);
			}
		}
	}

	public List getProductsForPrecursor(String monomerBatchKey, NotebookPageModel pageModel) {
		List results = new ArrayList();
		Map precursorMap = pageModel.getPrecursorMap();
		if (precursorMap.containsKey(monomerBatchKey)) {
			List productKeys = (List) precursorMap.get(monomerBatchKey);
			for (Iterator it = productKeys.iterator(); it.hasNext();) {
				String productKey = (String) it.next();
				BatchModel batch = this.getProductBatchInTheExperiment(productKey, pageModel);
				if (batch != null)
					results.add(batch);
			}
		}
		return results;
	}
}