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
package com.chemistry.enotebook.client.controller.scheduler;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarPage;
import com.chemistry.enotebook.client.datamodel.speedbar.SpeedBarPageGroup;
import com.chemistry.enotebook.client.gui.Gui;
import com.chemistry.enotebook.client.gui.NotebookPageGuiInterface;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.regis_submis.PCeNRegistration_BatchDetailsViewContainer;
import com.chemistry.enotebook.client.gui.speedbar.SpeedBarHandler;
import com.chemistry.enotebook.domain.BatchRegInfoModel;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.JobModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.esig.delegate.SignatureDelegate;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.utils.CeNSystemProperties;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.common.chemistry.codetable.CodeTableCache;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

public class TimerStatusHandler {

	private static final Log log = LogFactory.getLog(TimerStatusHandler.class);	
	
	private static TimerStatusHandler _instance;
	
	private Timer timer;
	private long period;
	private String finalStatus;
	
	private List<Task> tasks = new ArrayList<Task>();
	private Object sync = new Object();
		
	private TimerStatusHandler() {
		try {
			period = Long.parseLong(CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_ESIG_TIMER));
		} catch (Exception e) {
			log.debug("Unable to get timer period from DB. Period will be set to 1 minute.", e);
			period = 1L;
		}
		
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				process();
			}
		};

		period = period * 60 * 1000;
		
		timer = new Timer(TimerStatusHandler.class.getSimpleName());
		timer.schedule(task, period, period);
	}
	
	public static synchronized TimerStatusHandler getInstance() {
		if (_instance == null) {
			_instance = new TimerStatusHandler();
		}
		return _instance;
	}
	
	public void addRegistrationTask(String nbRef, String jobId) {
		addTask(TaskType.REGISTRATION, nbRef, jobId);
	}
	
	public void addSignatureTask(String nbRef) {
		addTask(TaskType.SIGNATURE, nbRef, null);
	}
	
	private void addTask(TaskType type, String nbRef, String jobId) {
		Task task = new Task(type, nbRef, jobId);
		
		synchronized (sync) {
			tasks.add(task);
		}
		
		log.debug("Added status task: " + task);
	}
	
	private void updateFinalStatus() {
		try {
			SignatureDelegate sigDel = new SignatureDelegate();
			finalStatus = sigDel.convertSigningStatusToPageStatus(sigDel.getFinalStatus());
		} catch (Exception e) {
			log.debug("Unable to get Signature Final Status. It will be set to " + CeNConstants.PAGE_STATUS_SIGNED, e);
			finalStatus = CeNConstants.PAGE_STATUS_SIGNED;
		}
	}
	
	private void process() {
		updateFinalStatus();
		
		List<Task> tasksToRun = new ArrayList<Task>();
		List<Task> tasksToAdd = new ArrayList<Task>();
		
		synchronized (sync) {
			for (Iterator<Task> it = tasks.iterator(); it.hasNext(); ) {
				tasksToRun.add(it.next());
				it.remove();
			}
		}
		
		for (Task task : tasksToRun) {
			if (!runTask(task)) {
				tasksToAdd.add(task);
			}
		}
		
		synchronized (sync) {
			for (Task task : tasksToAdd) {
				tasks.add(task);
			}
		}
	}
	
	private boolean runTask(Task task) {
		log.debug("Processing status task: " + task);
		
		try {
			if (task.getType() == TaskType.REGISTRATION) {
				return runRegistrationTask(task);
			}
		
			if (task.getType() == TaskType.SIGNATURE) {
				return runSignatureTask(task);
			}
		} catch (Exception e) {
			log.error("Error processing status task " + task, e);
		}
		
		return false;
	}

	private boolean runRegistrationTask(Task task) throws Exception {
		StorageDelegate delegate = ServiceController.getStorageDelegate();
		JobModel job = delegate.getRegistrationJob(task.getJobId());
		
		if (job != null) {
			if (StringUtils.equals(CeNConstants.JOB_FINISHED, job.getJobStatus())) {
				String siteCode = MasterController.getUser().getSiteCode();
				NotebookRef ref = new NotebookRef(task.getNbRef());
				NotebookPageGuiInterface page = MasterController.getGuiController().getLoadedPageGui(siteCode, ref, ref.getVersion());
				
				if (page != null) {
					SessionIdentifier sessionId = MasterController.getUser().getSessionIdentifier();
					String pageKey = job.getPageKey();
					
					List<ProductBatchModel> batches = page.getPageModel().getAllProductBatchModelsInThisPage();
					List<BatchRegInfoModel> regInfos = delegate.getAllRegisteredBatchesForPage(pageKey, sessionId);
					
					for (BatchRegInfoModel regInfo : regInfos) {
						for (ProductBatchModel batch : batches) {
							if (StringUtils.equals(batch.getKey(), regInfo.getBatchKey())) {
								batch.setRegInfo(regInfo);
								PCeNRegistration_BatchDetailsViewContainer.removeBatchFrommErrorMap(batch);
							}
						}
					}

					page.getPageModel().setModelChanged(false);
					page.refreshPage();
					
					MasterController.getGuiComponent().repaint();
				}
				
				return true;
			}
		}
		
		return false;
	}

	private boolean runSignatureTask(Task task) throws Exception {
		NotebookRef ref = new NotebookRef(task.getNbRef());
		String siteCode = MasterController.getUser().getSiteCode();
		
		String status = ServiceController.getStorageDelegate().getNotebookPageStatus(siteCode, ref.getNotebookRef(), ref.getVersion()).getStatus();
		
		updateSpeedBarWithStatus(status, siteCode, MasterController.getUser().getFullName(), ref, ref.getVersion());
		
		MasterController.getGUIComponent().refreshIcons();
		
		if (StringUtils.equals(CeNConstants.PAGE_STATUS_SUBMIT_FAILED, status) || StringUtils.equals(finalStatus, status)) {
			return true;
		}
		
		return false;
	}

	public static void updateSpeedBarWithStatus(String newStatus, String siteCode, String user, NotebookRef nbRef, int version) throws Exception {
		Gui gui = MasterController.getGUIComponent();
		
		if (gui != null) {
			NotebookPageGuiInterface loadedPage = MasterController.getGuiController().getLoadedPageGui(siteCode, nbRef, version);
			
			if (loadedPage != null) {
				String status = loadedPage.getPageModel().getStatus();
				
				if (!StringUtils.equals(status, newStatus)) {
					loadedPage.refreshPage();
				}
			}
			
			String nb = nbRef.getNbNumber();
			String ex = nbRef.getNbPage();
			String group = SpeedBarPageGroup.getGroupFromExperiment(ex);
			
			ex = version > 1 ? ex + " v" + version : ex;
						
			String[] path = new String[] { user, nb, group, ex };
			
			updateSpeedBarPage(gui.getMySpeedBar(), path, newStatus);
			
			path = new String[] { CodeTableCache.getCache().getSiteDescription(siteCode), user, nb, group, ex };

			updateSpeedBarPage(gui.getAllSitesSpeedBar(), path, newStatus);
		}
	}
	
	private static void updateSpeedBarPage(SpeedBarHandler handler, String[] path, String status) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) handler.getTree().getModel().getRoot();
		
		String rootName = rootNode.toString();
		
		String[] fullPath = new String[path.length + 1];
		fullPath[0] = rootName;
		
		for (int i = 0; i < path.length; ++i) {
			fullPath[i + 1] = path[i];
		}
				
		TreePath treePath = handler.findByName(fullPath);
		
		if (treePath != null) {
			Object obj = treePath.getLastPathComponent();
			
			if (obj != null && obj instanceof TreeNode) {
				Object o = ((DefaultMutableTreeNode) obj).getUserObject();
				
				if (o instanceof SpeedBarPage){
					SpeedBarPage sbp = (SpeedBarPage) o;
					
					sbp.setPageStatus(status);
					sbp.setCompleteInProgress(false);
					
					((DefaultTreeModel) handler.getModel()).nodeChanged((TreeNode) obj);
					((DefaultTreeModel) handler.getModel()).reload((TreeNode) obj);
				}
			}
		}
	}
	
	private enum TaskType {
		REGISTRATION,
		SIGNATURE
	}
	
	private class Task {
		
		private TaskType type;
		private String nbRef;
		private String jobId;
		
		public Task(TaskType type, String nbRef, String jobId) {
			this.type = type;
			this.nbRef = nbRef;
			this.jobId = jobId;
		}
		
		public TaskType getType() {
			return type;
		}
		
		public String getNbRef() {
			return nbRef;
		}
		
		public String getJobId() {
			return jobId;
		}
		
		@Override
		public String toString() {
			return ("[" + type + " - " + nbRef + " - " + jobId + "]");
		}
	}
}
