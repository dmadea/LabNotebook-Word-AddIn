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

import com.chemistry.enotebook.analyticalservice.classes.SearchResult;
import com.chemistry.enotebook.analyticalservice.classes.SearchResultsSummary;
import com.chemistry.enotebook.analyticalservice.classes.UploadFileAttributes;
import com.chemistry.enotebook.analyticalservice.clean.SearchResultImpl;
import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.domain.AnalysisCacheModel;
import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class AnalyticalDetailUtility {

	private static final Log log = LogFactory.getLog(AnalyticalDetailUtility.class);
	private static int QUERY_TIMEOUT_MILLI_SECONDS = 180000;
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private NotebookPageModel nbPage;
	//private LinkedHashMap batchesToMolStringsMap = new LinkedHashMap();
	private AnalyticalDetailTableView summaryViewer;
	
	public AnalyticalDetailUtility(NotebookPageModel nbPage, AnalyticalDetailTableView summaryViewer) {
		this.nbPage = nbPage;
		this.summaryViewer = summaryViewer;
		//this.batchesToMolStringsMap = BatchAttributeComponentUtility.getCachedProductBatchesToMolstringsMap(batches, nbPage);
	}

	/**
	 * 
	 * @param sampleRef
	 * @param date
	 * @param insType	 
	 * @param fileName
	 * @param expMethod
	 */
	public void doFileUpload(String sampleRef, Date date, String insType, String fileName, String expMethod) {
		String fileNameOnly = null;
		try {
			int i = fileName.lastIndexOf(File.separator);
			String filePath = fileName.substring(0, i);
			fileNameOnly = fileName.substring(i + 1, fileName.length());
			String domain = MasterController.getUser().getNtDomain();
			int index = domain.indexOf(":");
			if (index > 1)
				domain = domain.substring(0, index);
			File f = new File(fileName);
			if (f.length() > Integer.MAX_VALUE) { // i.e. 2147483647
				JOptionPane.showMessageDialog(MasterController.getGuiComponent(),
						"This file is too large to upload", "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			byte[] b = new byte[(int) f.length()];
			InputStream fis = new FileInputStream(fileName);
			fis.read(b);
			fis.close();
			UploadFileAttributes fileAttributes = new UploadFileAttributes(fileNameOnly, f.length(), InetAddress
					.getLocalHost().getHostName(), filePath, sampleRef, null, MasterController.getUser().getSiteCode(), date,
					new Date(f.lastModified()), MasterController.getUser().getNTUserID(), domain);
			fileAttributes.setInstrumentType(insType);
			fileAttributes.setExperimentName(expMethod);
			AnalyticalServiceDelegate analyticalServiceDelegate = ServiceController.getAnalyticalServiceDelegate(MasterController.getUser().getSessionIdentifier());
//			if (!analyticalServiceDelegate.checkHealth())
//				throw new Exception("AnalyticalService Server is not available.");
			SearchResult resultI = analyticalServiceDelegate.doFileUpload(fileAttributes, b);
			if (resultI != null) {
				SearchResult result = (SearchResult) resultI;
				addAnalysis(result, null, true, sampleRef);
			}
		} catch (Throwable e) { // Temporary until we figure out why the AnalyticalService exception is four levels down.
			try {
				if (e.getMessage().indexOf("available") > -1) {
					JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "The AnalyticalService server is not available.", "Error", JOptionPane.ERROR_MESSAGE);	
					log.error(e.getMessage());
				}
				Throwable t = ExceptionUtils.getRootCause(e);
				t.getMessage();
				if (t.getMessage() != null && t.getMessage().indexOf("File already exists on server") > -1) {
					JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "The File \'" + fileNameOnly + "\' already exists on server.", "Error",	JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "Error occurred while performing Manual Upload.", "Error", JOptionPane.ERROR_MESSAGE);		
					ceh.logExceptionMsg(MasterController.getGuiComponent(), "Error occurred while performing Manual Upload\n" + e.getMessage(), e);
					log.error(t.getMessage());
				}
			} catch (HeadlessException e1) {
				JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "Error occurred while performing Manual Upload.", "Error", JOptionPane.ERROR_MESSAGE);		
				log.error(e1.getMessage());
				ceh.logExceptionMsg(MasterController.getGuiComponent(), "Error occurred while performing Manual Upload\n" + e.getMessage(), e);
			}
		}
	}
	private void addAnalysis(SearchResult analyticalServiceResult, ArrayList<SearchResult> al_Linked, boolean isAdvancedLinking, String nbRef) {
		if (analyticalServiceResult.getSampleReference() != null) {
			String[] sampleRefs = analyticalServiceResult.getSampleReference();
			if (sampleRefs != null && sampleRefs.length > 0) {
				AnalysisModel aa = null;
				AnalysisCacheModel acache = nbPage.getAnalysisCache();
				for (int i = 0; i < sampleRefs.length; i++) {
					if (!isAdvancedLinking && sampleRefs[i] != null
							&& !(stripLeading(sampleRefs[i], '0').startsWith(stripLeading(nbRef, '0')))) {
						continue;
					}
					// format the AnalyticalService result sampleRefs[i] to Cen if possible
					sampleRefs[i] = formatForLeadingZeros(sampleRefs[i]);
					aa = acache.isExistingAnalysis(analyticalServiceResult.getCyberlabID(), sampleRefs[i]);
					if (al_Linked == null)
						al_Linked = new ArrayList<SearchResult>();
					if (aa == null) {
						aa = createCeNAnalysis(analyticalServiceResult, sampleRefs[i]);
					} else {
						aa.setToDelete(false);
					}
					if (al_Linked != null && analyticalServiceResult.getSite() != null)
							al_Linked.add(analyticalServiceResult);
					summaryViewer.addInstrument(analyticalServiceResult.getInstrumentType());
				}
				summaryViewer.updateAnalyses();
				this.enableSaveButton();
			}
		}
	}


	/**
	 * @param analyticalServiceResult
	 * @param sampleRef
	 * @return
	 */
	private AnalysisModel createCeNAnalysis(SearchResult analyticalServiceResult, String sampleRef) {

		AnalysisModel aa ;
		AnalysisCacheModel ac = nbPage.getAnalysisCache();
		aa = ac.createAnalysis(analyticalServiceResult.getCyberlabID());
		aa.setLinked(true);
		aa.setCenSampleRef(sampleRef);
		aa.setAnalyticalServiceSampleRef(sampleRef);
		aa.setModelChanged(true);
		aa.setCyberLabDomainId(analyticalServiceResult.getCyberlabDomainID());
		aa.setCyberLabFolderId(analyticalServiceResult.getCyberlabFolderID());
		aa.setCyberLabLCDFId(analyticalServiceResult.getCyberlabLCDFID());
		aa.setCyberLabUserId(analyticalServiceResult.getCyberlabUserID());
		aa.setDomain(analyticalServiceResult.getDomain());
		aa.setExperiment(analyticalServiceResult.getExperiment());
		if (analyticalServiceResult.getExperimentTime() != null) {
			// SimpleDateFormat format = new SimpleDateFormat("EEE MMM d,
			// yyyy");
			SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");
			try {
				String s = format.format(analyticalServiceResult.getExperimentTime());
				aa.setExperimentTime(s);
			} catch (Exception e) {
				// silently process the error
				aa.setExperimentTime(analyticalServiceResult.getExperimentTime().toString());
			}
		} else {
			aa.setExperimentTime("");
		}
		aa.setFileName(analyticalServiceResult.getFilename());
		if (analyticalServiceResult.getFilesize() != null) {
			aa.setFileSize(analyticalServiceResult.getFilesize().longValue());
		}
		aa.setFileType(analyticalServiceResult.getFileType());
		aa.setGroupId(analyticalServiceResult.getGroupID());
		aa.setInstrument(analyticalServiceResult.getInstrument());
		if (analyticalServiceResult.getInstrumentType() == null) {
			aa.setInstrumentType("Unknown");
		} else {
			aa.setInstrumentType(analyticalServiceResult.getInstrumentType());
		}
		aa.setSite(analyticalServiceResult.getSite());
		aa.setSiteCode(analyticalServiceResult.getSite());
		aa.setServer(analyticalServiceResult.getServer());
		if (analyticalServiceResult.getFile() != null) {
			aa.setUrl(analyticalServiceResult.getFile().toString());
		} else {
			aa.setUrl("");
		}
		aa.setUserId(analyticalServiceResult.getUserID());
		if (analyticalServiceResult.getVersion() != null) {
			aa.setVersion(analyticalServiceResult.getVersion().longValue());
		}
		aa.setComments("");
		return aa;
	}

	public boolean performQuickLink(String sampleRef, ArrayList<String> sites) {
		long startTime = System.currentTimeMillis();
		try {
			/**
			 * split the nbRef and remove the leading zeroes first element is the notebook and second is the experiment
			 */
			AnalyticalServiceDelegate analyticalServiceDelegate = ServiceController.getAnalyticalServiceDelegate(MasterController.getUser().getSessionIdentifier());

            // String[] s = sampleRef.split("-");
            // if (s.length != 3) return false;
            // strip the leading zeroes as in analyticalService
            // String noteBookRef = stripLeading(s[0],'0');
            // String expNo = stripLeading(s[1],'0');
            // String lotNo = stripLeading(s[2],'0');
            // String analyticalServiceFormatSampleRef =
            // noteBookRef+"-"+expNo+"-"+lotNo;
            String sampleRef2 = stripLeading(sampleRef, '0');
            String query = "[Sample Reference] = \"" + sampleRef + "\" or " + "[Sample Reference] = \"" + sampleRef2 + "\"";
            sites = new ArrayList<String>();
            sites.add(nbPage.getSiteCode());
            // System.out.println("AnalyticalService Query: " + query + "\nSites: " +
            // sites );
            ArrayList<SearchResult> al_Linked = new ArrayList<SearchResult>();
            // and the following analyticalServiceSummary1 is a subset of analyticalServiceSummary
            SearchResultsSummary analyticalServiceSummary1 = analyticalServiceDelegate.retrieveAnalyticalServiceData(query, sites);
            Iterator<SearchResult> it = analyticalServiceSummary1.searchResultFiles();
            if (it.hasNext()) {
                while (it.hasNext()) {
                    SearchResult sResult = it.next();
                    addAnalysis(sResult, al_Linked, false, sampleRef);
                }
                //summaryViewer.refresh();
                ///////buildCeNAnalyticalData();
                if (summaryViewer != null)
                    summaryViewer.reload();
                markFileAsLinked(nbPage.getNbRef().getNotebookRef(), al_Linked, true);
                MasterController.getGUIComponent().enableSaveButtons();
                return true;
            } else {
                // No results found
                return false;
            }

		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long totalElapsed = endTime - startTime;
			Throwable t = e.getCause();
			if (t != null && t.toString().indexOf("CyberLab") > -1) {
				JOptionPane.showMessageDialog(null, "CyberLab API failure.", "Error", JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing QuickLink, Not Displayed to User");
				return true;
			} else if (t != null && t.toString().indexOf("java.rmi.MarshalException") > -1
					&& totalElapsed > QUERY_TIMEOUT_MILLI_SECONDS) {
				JOptionPane.showMessageDialog(null, "The AnalyticalService search has timed out due to no response from CyberLab.", "Error",
						JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing QuickLink, Not Displayed to User");
				return true;
			} else if (t != null && t.toString().indexOf("CORBA.COMM_FAILURE") > -1) {
				JOptionPane.showMessageDialog(null, "The AnalyticalService/CyberLab search service is not available.", "Error",
						JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing QuickLink, Not Displayed to User");
				return true;
			} else {
				ceh.logExceptionMsg(null, "Error occurred while performing QuickLink", e);
			}
			return false;
		}
	}
	private String stripLeading(String s, char c) {
		if (s == null)
			return "";
		int len = s.length();
		int n = 0;
		while (n < len && s.charAt(n) == c)
			++n;
		return s.substring(n);
	}

	/**
	 * Check if it is existing but with leading zeros
	 * 
	 * @param analyticalServiceResultSampleRef
	 * @param analyticalServiceResultSampleRef
	 * @return
	 */
	private String formatForLeadingZeros(String analyticalServiceResultSampleRef) {
		String result = analyticalServiceResultSampleRef;
		if (analyticalServiceResultSampleRef != null && analyticalServiceResultSampleRef.indexOf("-") > -1) {
			try {
				int index = analyticalServiceResultSampleRef.indexOf("-");
				String notebookPart = analyticalServiceResultSampleRef.substring(0, index);
				String restOftheSample = analyticalServiceResultSampleRef.substring(index, analyticalServiceResultSampleRef.length());
				notebookPart = NotebookPageUtil.formatNotebookNumber(notebookPart);
				result = notebookPart + restOftheSample;
			} catch (Exception e) {
				// ignore
				result = analyticalServiceResultSampleRef;
			}
		}
		return result;
	}

	public ArrayList<String> getSampleRefsFromBatchInfo(BatchType batchType) {
		ArrayList<String>  al_return = new ArrayList<String> ();
		if (nbPage.getAllProductBatchModelsInThisPage() == null)
			return al_return;
		for(ProductBatchModel actualBatch : nbPage.getAllProductBatchModelsInThisPage()){
			// Note this used to filter for ACTUAL batch vs. pulling all product batches.
			//      getAllProductBatchModelsInThisPage returns both Intended and Actual batchModels.
			//      Singleton pages may benefit from filtering for Actual Batches.
			al_return.add(actualBatch.getBatchNumberAsString());
		}
		return al_return;
	}
	
	/**
	 * @return
	 */
	public List<String> getAllSampleRefs() {
		return nbPage.getAnalysisCache().getDistinctSampleRefs();
	}
	
	public boolean isAnalysisEmptyForBatch(String batchNumber) {
		List<AnalysisModel> analyses = nbPage.getAnalysisListForBatch(batchNumber);
		return !(analyses != null && analyses.size() > 0);
	}
	
	/**
	 * @param sampleRef
	 */
	public void updateComments(String sampleRef, String comments) {
		for(AnalysisModel aa : nbPage.getAnalysisCache().getAnalysisListForBatch(sampleRef)) {
			aa.setComments(comments);
			aa.setModified(true);
		}
		summaryViewer.refresh();
		//////buildCeNAnalyticalData();
		if (summaryViewer != null)
			summaryViewer.reload();
	}
	
	public void unlinkAll(String sampleReference) {
		List<AnalysisModel> al = nbPage.getAnalysisCache().getAnalysisListForBatch(sampleReference);
		List<SearchResult> listAnalyticalServiceObjects = new ArrayList<SearchResult>();

		for (AnalysisModel a : al) {
			a.setModelChanged(true);
			
			String[] sampleRef = { a.getCenSampleRef() };
			String[] batchId = { a.getCenSampleRef() };
			
			SearchResult resultToUnlink = getSearchResult(sampleRef, batchId, a);
			
			if (a.getSite() != null) {
				listAnalyticalServiceObjects.add(resultToUnlink);
			}
			
			a.setLinked(false);
			
			nbPage.getAnalysisCache().deleteAnalysis(a);
		}

		if (summaryViewer != null) {
			summaryViewer.updateAnalyses();
		}

		if (listAnalyticalServiceObjects.size() > 0) {
			markFileAsLinked(sampleReference, listAnalyticalServiceObjects, false);
		}

		MasterController.getGUIComponent().enableSaveButtons();
		
		if (summaryViewer != null) {
			summaryViewer.reload();
		}
	}
	
	public SearchResult getSearchResult(String[] sampleRef, String[] batchId, AnalysisModel selectedAnalysis) {
		Date date = null;
		try {
			date = DateFormat.getDateInstance().parse(selectedAnalysis.getExperimentTime());
		} catch (ParseException error) {
			// exception case will take current date in milliseconds
			date = new Date();
		}
		return new SearchResultImpl(selectedAnalysis.getCyberLabDomainId(), 
				selectedAnalysis.getCyberLabFileId(), 
				selectedAnalysis.getCyberLabFolderId(),
				sampleRef,
				batchId,
				selectedAnalysis.getGroupId(),
				selectedAnalysis.getUserId(),
				selectedAnalysis.getDomain(),
				selectedAnalysis.getExperiment(),
				date,
				selectedAnalysis.getInstrument(),
				selectedAnalysis.getInstrumentType(),
				selectedAnalysis.getSite(),
				selectedAnalysis.getFileType(),
				selectedAnalysis.getFileName(),
				selectedAnalysis.getFileSize());
	}
	
	private void markFileAsLinked(String sampleRef, List<SearchResult> fileList, boolean linked) {
		try {
			AnalyticalServiceDelegate analyticalServiceDelegate = ServiceController.getAnalyticalServiceDelegate(MasterController.getUser().getSessionIdentifier());
			if (analyticalServiceDelegate.canMarkFileAsLinked())
				analyticalServiceDelegate.markFileAsLinked(sampleRef, fileList, linked);
		} catch (Exception e) {
			ceh.logExceptionMsg(null, "Error occurred while marking Files Linked/Unlinked in AnalyticalService", e);
		}
	}

	/**
	 * 
	 * @param message
	 */
	public boolean showJOptionMessage(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
		return true;
	}
	
	public void performMap(String mapFromSampleRef, String mapToSampleRef) {
		List<AnalysisModel> analysisModelList = new ArrayList<AnalysisModel>();
		AnalysisCacheModel ac = nbPage.getAnalysisCache();
		ArrayList<AnalysisModel> analyticals = ac.getAnalyticalList();
		if(analyticals != null && analyticals.size() > 0) {
			try {
				AnalyticalServiceDelegate analyticalServiceDelegate = ServiceController.getAnalyticalServiceDelegate(MasterController.getUser().getSessionIdentifier());
				for (AnalysisModel a : analyticals) {
					if (a.getCenSampleRef().equals(mapFromSampleRef)) {
						a.setCenSampleRef(mapToSampleRef);
						a.setModelChanged(true);
						analysisModelList.add(a);
					}
				}
				analyticalServiceDelegate.setFileBatchID(analysisModelList);
				this.enableSaveButton();
				if (summaryViewer != null)
					summaryViewer.updateAnalyses();
			} catch (Exception error) {
				log.error("Failed to map from sampleRef: " + mapFromSampleRef + " to sampleRef: " + mapToSampleRef, error);
			}
		} else {
			log.debug("No maping performed.  Notebook page: " + nbPage.getNotebookRefAsString() + " analysis cache is empty.");
		}
	}

	private void enableSaveButton() {
		if (nbPage != null) {
			nbPage.setModelChanged(true);
			MasterController.getGUIComponent().enableSaveButtons();
		}
	}
}
