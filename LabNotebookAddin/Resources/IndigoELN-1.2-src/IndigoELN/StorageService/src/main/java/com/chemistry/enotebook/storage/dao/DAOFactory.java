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
package com.chemistry.enotebook.storage.dao;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;


public class DAOFactory {
	
	private NotebookManager notebookManager = null;
	private NotebookDAO notebookDAO = null;
	private NotebookInsertDAO notebookInsertDAO = null;
	private NotebookLoadDAO notebookLoadDAO = null;
	private StructureSearchDAO structureSearchDAO = null;
	private NotebookSearchDAO notebookSearchDAO = null;
	private NotebookUpdateDAO notebookUpdateDAO = null;
	private NotebookRemoveDAO notebookRemoveDAO = null;
	private ContainerDAO containerDAO = null;
	private PlateDAO plateDAO = null;
	private CroPageDAO croPageDAO = null;
	private MonomerBatchDAO monomerBatchDAO = null;
	private ProductBatchDAO productBatchDAO = null;
	private AnalysisDAO analysisDAO = null;
	private AttachmentDAO attachmentDAO = null;
	private JobDAO jobDAO = null;
	private ProcedureImageDAO procedureImageDAO = null;

	
	private DataSourceTransactionManager transactionManager = null;

	private DAOFactory(){
		
	}
	
	/**
	 * @return the containerDAO
	 */
	public ContainerDAO getContainerDAO() {
		return containerDAO;
	}

	/**
	 * @param containerDAO the containerDAO to set
	 */
	public void setContainerDAO(ContainerDAO containerDAO) {
		this.containerDAO = containerDAO;
	}

	/**
	 * @return the croPageDAO
	 */
	public CroPageDAO getCroPageDAO() {
		return croPageDAO;
	}

	/**
	 * @param croPageDAO the croPageDAO to set
	 */
	public void setCroPageDAO(CroPageDAO croPageDAO) {
		this.croPageDAO = croPageDAO;
	}

	/**
	 * @return the notebookDAO
	 */
	public NotebookDAO getNotebookDAO() {
		return notebookDAO;
	}

	/**
	 * @param notebookDAO the notebookDAO to set
	 */
	public void setNotebookDAO(NotebookDAO notebookDAO) {
		this.notebookDAO = notebookDAO;
	}

	/**
	 * @return the notebookInsertDAO
	 */
	public NotebookInsertDAO getNotebookInsertDAO() {
		return notebookInsertDAO;
	}

	/**
	 * @param notebookInsertDAO the notebookInsertDAO to set
	 */
	public void setNotebookInsertDAO(NotebookInsertDAO notebookInsertDAO) {
		this.notebookInsertDAO = notebookInsertDAO;
	}

	/**
	 * @return the notebookLoadDAO
	 */
	public NotebookLoadDAO getNotebookLoadDAO() {
		return notebookLoadDAO;
	}

	/**
	 * @param notebookLoadDAO the notebookLoadDAO to set
	 */
	public void setNotebookLoadDAO(NotebookLoadDAO notebookLoadDAO) {
		this.notebookLoadDAO = notebookLoadDAO;
	}

	/**
	 * @return the notebookManager
	 */
	public NotebookManager getNotebookManager() {
		return notebookManager;
	}

	/**
	 * @param notebookManager the notebookManager to set
	 */
	public void setNotebookManager(NotebookManager notebookManager) {
		this.notebookManager = notebookManager;
	}

	/**
	 * @return the notebookRemoveDAO
	 */
	public NotebookRemoveDAO getNotebookRemoveDAO() {
		return notebookRemoveDAO;
	}

	/**
	 * @param notebookRemoveDAO the notebookRemoveDAO to set
	 */
	public void setNotebookRemoveDAO(NotebookRemoveDAO notebookRemoveDAO) {
		this.notebookRemoveDAO = notebookRemoveDAO;
	}

	/**
	 * @return the notebookSearchDAO
	 */
	public NotebookSearchDAO getNotebookSearchDAO() {
		return notebookSearchDAO;
	}
	
	/**
	 * @param notebookSearchDAO the notebookSearchDAO to set
	 */
	public void setNotebookSearchDAO(NotebookSearchDAO notebookSearchDAO) {
		this.notebookSearchDAO = notebookSearchDAO;
	}

	/**
	 * @return the notebookUpdateDAO
	 */
	public NotebookUpdateDAO getNotebookUpdateDAO() {
		return notebookUpdateDAO;
	}

	/**
	 * @param notebookUpdateDAO the notebookUpdateDAO to set
	 */
	public void setNotebookUpdateDAO(NotebookUpdateDAO notebookUpdateDAO) {
		this.notebookUpdateDAO = notebookUpdateDAO;
	}

	/**
	 * @return the plateDAO
	 */
	public PlateDAO getPlateDAO() {
		return plateDAO;
	}

	/**
	 * @param plateDAO the plateDAO to set
	 */
	public void setPlateDAO(PlateDAO plateDAO) {
		this.plateDAO = plateDAO;
	}

	/**
	 * @return the transactionManager
	 */
	public DataSourceTransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager the transactionManager to set
	 */
	public void setTransactionManager(DataSourceTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @return the monomerBatchDAO
	 */
	public MonomerBatchDAO getMonomerBatchDAO() {
		return monomerBatchDAO;
	}

	/**
	 * @param monomerBatchDAO the monomerBatchDAO to set
	 */
	public void setMonomerBatchDAO(MonomerBatchDAO monomerBatchDAO) {
		this.monomerBatchDAO = monomerBatchDAO;
	}

	/**
	 * @return the productBatchDAO
	 */
	public ProductBatchDAO getProductBatchDAO() {
		return productBatchDAO;
	}

	/**
	 * @param productBatchDAO the productBatchDAO to set
	 */
	public void setProductBatchDAO(ProductBatchDAO productBatchDAO) {
		this.productBatchDAO = productBatchDAO;
	}

	/**
	 * @return the analysisDAO
	 */
	public AnalysisDAO getAnalysisDAO() {
		return analysisDAO;
	}

	/**
	 * @param analysisDAO the analysisDAO to set
	 */
	public void setAnalysisDAO(AnalysisDAO analysisDAO) {
		this.analysisDAO = analysisDAO;
	}

	/**
	 * @return the attachmentDAO
	 */
	public AttachmentDAO getAttachmentDAO() {
		return attachmentDAO;
	}

	/**
	 * @param attachmentDAO the attachmentDAO to set
	 */
	public void setAttachmentDAO(AttachmentDAO attachmentDAO) {
		this.attachmentDAO = attachmentDAO;
	}

	/**
	 * @return the jobDAO
	 */
	public JobDAO getJobDAO() {
		return jobDAO;
	}

	/**
	 * @param jobDAO the jobDAO to set
	 */
	public void setJobDAO(JobDAO jobDAO) {
		this.jobDAO = jobDAO;
	}

	/**
	 * @param procedureImageDAO the procedureImageDAO to set
	 */
	public void setProcedureImageDAO(ProcedureImageDAO procedureImageDAO) {
		this.procedureImageDAO = procedureImageDAO;
	}

	/**
	 * @return the procedureImageDAO
	 */
	public ProcedureImageDAO getProcedureImageDAO() {
		return procedureImageDAO;
	}

	public void setStructureSearchDAO(StructureSearchDAO structureSearchDAO) {
		this.structureSearchDAO = structureSearchDAO;
	}

	public StructureSearchDAO getStructureSearchDAO() {
		return structureSearchDAO;
	}
}
