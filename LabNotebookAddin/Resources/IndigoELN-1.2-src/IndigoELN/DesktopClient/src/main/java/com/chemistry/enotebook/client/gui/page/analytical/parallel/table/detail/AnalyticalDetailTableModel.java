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
package com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.page.batch.BatchAttributeComponentUtility;
import com.chemistry.enotebook.client.gui.page.table.PCeNTableView;
import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.domain.BatchModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.common.MolString;
import com.chemistry.enotebook.utils.ProductBatchStatusMapper;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.*;

public class AnalyticalDetailTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 5479801127688687146L;
	
	private NotebookPageModel pageModel;
	private List<ProductBatchModel> batches;
	private List<String> distinctInsTypes;
	private JTable view;
	
	private List<String> cols = new ArrayList<String>();
	private List<String> externalBatches = new ArrayList<String>();
	private List<String> batchNumberList = new ArrayList<String>();
	private Map<String, MolString> batchesToMolStringsMap = new HashMap<String, MolString>();
	
	public AnalyticalDetailTableModel(NotebookPageModel pageModel, List<ProductBatchModel> batches, JTable view) {
		this.pageModel = pageModel;
		this.batches = batches;
		this.view = view;
		this.batchesToMolStringsMap = BatchAttributeComponentUtility.getCachedProductBatchesToMolstringsMap(batches, pageModel);
		this.distinctInsTypes = pageModel.getAnalysisCache().getDistinctInstrumentTypes();
		this.createHeaderNames();
		this.updateBatchNumberList(batches);
	}

	public List<? extends BatchModel> getBatches() {
		return batches;
	}
	
	private void createHeaderNames() {
		cols.add(PCeNTableView.STRUCTURE);
		cols.add(PCeNTableView.NBK_BATCH_NUM);
		
		if (!pageModel.isSingletonExperiment()) {
			cols.add(PCeNTableView.STATUS);
		}
		
		cols.add(PCeNTableView.QUICK_LINK);
		cols.add(PCeNTableView.PURIFICATION_SERVICE_DATA);
		cols.add(PCeNTableView.BATCH_ANALYTICAL_COMMENTS);	
		
		if (distinctInsTypes != null) {
			for (String type : distinctInsTypes) {
				cols.add(type);
			}
		}
	}
	
	private void updateBatchNumberList(List<? extends BatchModel> batches) {
		batchNumberList.clear();
		for (BatchModel batch : batches) {
			batchNumberList.add(batch.getBatchNumberAsString());
		}
	}
	
	public boolean isColumnEditable(String columnName) {
		if (columnName.equalsIgnoreCase(PCeNTableView.STRUCTURE) || columnName.equalsIgnoreCase(PCeNTableView.NBK_BATCH_NUM)) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isCellEditable(int rowIndex, int colIndex) {
		if (! this.pageModel.isEditable()) {
			return false;
		}
		
		if (rowIndex >= batches.size()) { 
			return false;
		}
		
		if (cols.get(colIndex).equals(PCeNTableView.STATUS) 
				|| cols.get(colIndex).equals(PCeNTableView.BATCH_ANALYTICAL_COMMENTS)
				|| cols.get(colIndex).equals(PCeNTableView.QUICK_LINK)
				|| cols.get(colIndex).equals(PCeNTableView.PURIFICATION_SERVICE_DATA)) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getColumnName(int colIndex) {
		return (String) cols.get(colIndex);
	}
		
	public int getColumnCount() {
		return cols.size();
	}

	public int getRowCount() {
		return batches.size() + externalBatches.size();
	}

	public Object getValueAt(int rowIndex, int colIndex) {
		// If this row represents a product batch in this experiment
		if (rowIndex < batches.size()) {
			ProductBatchModel batch = (ProductBatchModel) batches.get(rowIndex);
			if (cols.get(colIndex).equals(PCeNTableView.STRUCTURE)) {
				if (this.batchesToMolStringsMap.containsKey(batch.getKey())) {
					return this.batchesToMolStringsMap.get(batch.getKey());
				} else {
					return "";
				}
			} else if (cols.get(colIndex).equals(PCeNTableView.NBK_BATCH_NUM)) {
				return batch.getBatchNumberAsString();
			} else if (cols.get(colIndex).equals(PCeNTableView.STATUS)) {
				return ProductBatchStatusMapper.getInstance().getStatusString(batch.getSelectivityStatus(), batch.getContinueStatus());
			} else if (cols.get(colIndex).equals(PCeNTableView.QUICK_LINK)) {
				return "Q";
			} else if (cols.get(colIndex).equals(PCeNTableView.PURIFICATION_SERVICE_DATA)) {
				if (batch.getRegInfo().getCompoundSource() == null || batch.getRegInfo().getConversationalBatchNumber() == null) {
					return "";
				} else {
					if (((batch.getRegInfo().getCompoundSource().equals("INPRODINT") || (batch.getRegInfo().getCompoundSource().equals("INPRODEXT"))) 
							&& (batch.getRegInfo().getConversationalBatchNumber().length() > 0))) {
						return "P";		// Link to PurificationService Data
					} else {
						return "";
					}
				}
			} else if (cols.get(colIndex).equals(PCeNTableView.BATCH_ANALYTICAL_COMMENTS)) {
				if (batch.getAnalyticalComment() != null) {
					return batch.getAnalyticalComment().equalsIgnoreCase("null") ? "" : batch.getAnalyticalComment();
				} else {
					return "";
				}
			} else {
				for (String type : distinctInsTypes) {
					if (cols.get(colIndex).equals(type)) {
						return Integer.toString(getInstrumentCount(batch.getBatchNumberAsString(), type));
					}
				}
			}
		} else if (rowIndex - batches.size() < externalBatches.size()) {
			int index = rowIndex - batches.size();
			String sampleRef = (String) externalBatches.get(index);
			
			if (cols.get(colIndex).equals(PCeNTableView.STRUCTURE)) {
				return "";
			} else if (cols.get(colIndex).equals(PCeNTableView.NBK_BATCH_NUM)) {
				return sampleRef;
			} else if (cols.get(colIndex).equals(PCeNTableView.STATUS)) {
				return "";
			} else if (cols.get(colIndex).equals(PCeNTableView.QUICK_LINK)) {
				return "";
			} else if (cols.get(colIndex).equals(PCeNTableView.PURIFICATION_SERVICE_DATA)) {
				return "";
			} else if (cols.get(colIndex).equals(PCeNTableView.BATCH_ANALYTICAL_COMMENTS)) {
				return "";
			} else {
				for (String type : distinctInsTypes) {
					if (cols.get(colIndex).equals(type)) {
						return Integer.toString(getInstrumentCount(sampleRef, type));
					}
				}
			} 
		}
		
		return "";
	}
	
	
	public void setValueAt(Object value, int rowIndex, int colIndex) {
		if (value == null || value.toString().equals(""))
			return;
		boolean modelChanged = false;
		ProductBatchModel batch = (ProductBatchModel) batches.get(rowIndex);

		int modelIndex = colIndex;

		if (view != null) {
			modelIndex = view.convertColumnIndexToModel(colIndex);
		}

		if (cols.get(modelIndex).equals(PCeNTableView.BATCH_ANALYTICAL_COMMENTS)) {
			if (value instanceof String) {
				if (batch.getAnalyticalComment() != null && batch.getAnalyticalComment().equals((String) value))
					return;
				batch.setAnalyticalComment((String) value);
				modelChanged = true;
			}
		} 
		else if (cols.get(modelIndex).equals(PCeNTableView.STATUS)) {
			if (value instanceof String) {
				if (BatchAttributeComponentUtility.setProductBatchStatus(batch, (String) value)) {
					modelChanged = true;
				}
			}
		}
		if (modelChanged) {
			batch.setModelChanged(true);
			MasterController.getGUIComponent().enableSaveButtons();
		}
	}
	
	private int getInstrumentCount(String sampleRef, String insType) {
		int count = 0;
		List<AnalysisModel> l_analyticalList = pageModel.getAnalysisCache().getAnalyticalList();
		for( AnalysisModel a : l_analyticalList) {
			if (a.getCenSampleRef().equals(sampleRef) && a.getInstrumentType().equals(insType) && !a.isSetToDelete()) 
				count++;
		}
		return count;
	}
	
	public int getModelIndexFromHeaderName(String name) {
		for (int i = 0; i < this.cols.size(); i++) {
			String temp = cols.get(i).toString();
			if (name.equalsIgnoreCase(temp)) {
				return i;
			}
		}
		return -1;
	}
	
	public void refreshColumns() {
		List<String> oldDistinctInsTypes = this.distinctInsTypes; 
		this.distinctInsTypes = pageModel.getAnalysisCache().getDistinctInstrumentTypes(); 
		for (String instType : distinctInsTypes) {
			if (! this.cols.contains(instType)) {
				this.cols.add(instType);
			}
		}
		boolean tableStructureChanged = !oldDistinctInsTypes.containsAll(this.distinctInsTypes) || !this.distinctInsTypes.containsAll(oldDistinctInsTypes);
		if (tableStructureChanged)
			this.fireTableStructureChanged();
	}
	
	public void addColumn(String colname) {
		if (! this.cols.contains(colname)) {
			cols.add(colname);
			distinctInsTypes.add(colname);
		}
	}
	
	// Add analysis models for sample refs that are not in this table
	public void updateAnalyses() {
		this.refreshColumns();
		// Append the actual analytical information
		this.cleanExternalBatchList();
		List<AnalysisModel> alist = pageModel.getAnalysisCache().getAnalyticalList();
		// First sort the analyses into a map
		Map<String, AnalysisModel> analysisMap = new HashMap<String, AnalysisModel>();
		for (AnalysisModel amodel : alist) {
			if (! (this.batchNumberList.contains(amodel.getCenSampleRef()) || (externalBatches.contains(amodel.getCenSampleRef()))) )
			//if (! (analysisMap.containsKey(amodel.getCenSampleRef())))
				analysisMap.put(amodel.getCenSampleRef(), amodel);
		}
		Set<String> sampleRefKeys = analysisMap.keySet();
		List<String> sampleRefList = new ArrayList<String>(sampleRefKeys);
		Collections.sort(sampleRefList);
		for (String sampleRef : sampleRefList) {
			if (! externalBatches.contains(sampleRef)) {
				this.externalBatches.add(sampleRef);
			}
		}
	}
	
	// Remove analysis models which are not in this experiment and whose analyses have 
	// been mapped.
	public void cleanExternalBatchList() {
		if (externalBatches != null && externalBatches.size() > 0) {
			List<String> deleteList = new ArrayList<String>();
			List<AnalysisModel> analysisList = this.pageModel.getAnalysisCache().getAnalyticalList();
			for (String sampleRef : externalBatches){
				boolean found = false;
				for (AnalysisModel model : analysisList) {
					if (model.getCenSampleRef().equals(sampleRef) && !model.isSetToDelete()) {
						found = true;
						break;
					}
				}
				if (!found)
					deleteList.add(sampleRef);
			}
			for (String deleteMe : deleteList) {
				externalBatches.remove(deleteMe);
			}
		}
	}
		
	public void updateCompounds(List<ProductBatchModel> batches) {
		this.batches = batches;
		this.batchesToMolStringsMap = BatchAttributeComponentUtility.getCachedProductBatchesToMolstringsMap(batches, pageModel);
		this.updateBatchNumberList(batches);
	}
}
