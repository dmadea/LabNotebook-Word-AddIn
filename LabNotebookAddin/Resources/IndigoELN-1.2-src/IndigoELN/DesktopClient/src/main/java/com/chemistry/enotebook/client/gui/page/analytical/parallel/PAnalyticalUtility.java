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
package com.chemistry.enotebook.client.gui.page.analytical.parallel;

import com.chemistry.enotebook.analyticalservice.classes.SearchResult;
import com.chemistry.enotebook.analyticalservice.classes.SearchResultsSummary;
import com.chemistry.enotebook.analyticalservice.classes.UploadFileAttributes;
import com.chemistry.enotebook.analyticalservice.clean.SearchResultImpl;
import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.client.gui.page.analytical.parallel.table.detail.AnalyticalChangeListener;
import com.chemistry.enotebook.domain.AnalysisCacheModel;
import com.chemistry.enotebook.domain.AnalysisModel;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProductBatchModel;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.utils.CommonUtils;
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

public class PAnalyticalUtility {

	public static final Log log = LogFactory.getLog(PAnalyticalUtility.class);

	private NotebookPageModel nbPage;// Used to get the BatchCache and AnalysisCache
	private static SearchResultsSummary analyticalServiceSummary;// static
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private static int QUERY_TIMEOUT_MILLI_SECONDS = 180000;
	private AnalyticalChangeListener analyticalChangeListener;

	private PAnalyticalSampleRefContainer sampleRefContainer;

	/**
	 * Constructor
	 *
	 * @param nbPage
	 */

	// This is used by the tree panel (sampleRefContainer)
	public PAnalyticalUtility(NotebookPageModel nbPage) {
		this.nbPage = nbPage;
	}

	// This is used by the summary batch container for the quick link all and advanced search
	public PAnalyticalUtility(NotebookPageModel nbPage, AnalyticalChangeListener analyticalChangeListener) {
		this.nbPage = nbPage;
		this.analyticalChangeListener = analyticalChangeListener;
	}

//	/**
//	 * @param nbPage
//	 */
//	public void setNotebookPage(NotebookPageModel nbPage) {
//		this.nbPage = nbPage;
//		//if (nbPage != null && nbPage.getAllProductBatchModelsInThisPage() != null)
//		//	buildCeNAnalyticalData(); // gets the data from CeNDB,
//		// pre-AnalyticalServiceSearch
//	}

	/**
	 * Indicates whether or not the model should be edited.
	 *
	 * @return
	 */
	public boolean isEditable() {
		boolean result = false;
		if (nbPage != null)
			result = nbPage.isEditable();
		return result;
	}

	public void setSampleRefContainer(PAnalyticalSampleRefContainer sampleRefContainer) {
		this.sampleRefContainer = sampleRefContainer;
	}

	public PAnalyticalSampleRefContainer getSampleRefContainer() {
		return sampleRefContainer;
	}

	public boolean performAdvancedLinkAll(String query, List<String> sites) {
		boolean returnFlag = false;
		long startTime = System.currentTimeMillis();
		try {
			AnalyticalServiceDelegate analyticalServiceDelegate = ServiceController.getAnalyticalServiceDelegate(MasterController.getUser().getSessionIdentifier());

            // System.out.println("AnalyticalService Query: " + query + "\nSites: " +
            // sites );
            SearchResultsSummary analyticalServiceSummary2 = analyticalServiceDelegate.retrieveAnalyticalServiceData(query, new ArrayList<String>(sites));
            if (analyticalServiceSummary2.getNumberOfFilesMatchingQuery() > 15) {
                int selection = JOptionPane.showConfirmDialog(MasterController.getGUIComponent(),
                        "There are more than 15 results found, Do you want to display the results?", "Warning",
                        JOptionPane.YES_NO_OPTION);
                if (selection == JOptionPane.NO_OPTION)
                    return false;
            }
            List<SearchResult> al_Linked = new ArrayList<SearchResult>();
            Iterator<SearchResult> it = analyticalServiceSummary2.searchResultFiles();
            if (it.hasNext()) {
                while (it.hasNext()) {
                    SearchResult sResult = it.next();
                    addAnalysis(sResult, al_Linked, true, nbPage.getNotebookRefAsString());
                }
                returnFlag = true;
            } else {
                // No results found
                returnFlag = false;
            }
            if (al_Linked.size() > 0) {
                markFileAsLinked(nbPage.getNotebookRefAsString(), al_Linked, true);
                this.analyticalChangeListener.updateAnalyses();
                nbPage.setModified(true);
            }

		} catch (Throwable e) {
			long endTime = System.currentTimeMillis();
			long totalElapsed = endTime - startTime;
			e.printStackTrace();
			Throwable t = e.getCause();
			if (t != null && t.toString().indexOf("CyberLab") > -1) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "CyberLab API failure.", "Error",
						JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing Advanced Link, Not Displayed to User");
				return true;
			} else if (t != null && t.toString().indexOf("java.rmi.MarshalException") > -1
					&& totalElapsed > QUERY_TIMEOUT_MILLI_SECONDS) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
						"The AnalyticalService search has timed out due to no response from CyberLab.", "Error", JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing Advanced Link, Not Displayed to User");
				return true;
			} else if (t != null && t.toString().indexOf("CORBA.COMM_FAILURE") > -1) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(),
						"The AnalyticalService/CyberLab search service is not available.", "Error", JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing Advanced Link, Not Displayed to User");
				return true;
			} else {
				ceh.logExceptionMsg(null,
	                    "Error occurred while performing Advanced Link.\n" +
	                    "Query: " + query + " sites: " + CommonUtils.getConcatenatedString(sites), e);
			}
			returnFlag = false;
		}
		return returnFlag;
	}

	/**
	 *
	 * @param message
	 */
	public boolean showJOptionMessage(String message) {
		JOptionPane.showMessageDialog(MasterController.getGuiComponent(), message, "Error", JOptionPane.ERROR_MESSAGE);
		return true;
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

		if (listAnalyticalServiceObjects.size() > 0) {
			markFileAsLinked(sampleReference, listAnalyticalServiceObjects, false);
			nbPage.setModified(true);
		}

		MasterController.getGUIComponent().enableSaveButtons();

		if (analyticalChangeListener != null) {
			analyticalChangeListener.updateAnalyses();
		}
	}

	/**
	 * Fills with the latest batch values, to update the new batches or if the comments have been changed in the batchInfo
	 *
	 */
	public void refreshAnalyticalData() {
		log.debug("No op refreshAnalyticalData called.");
	}


	/**
	 *
	 * @param sampleRef
	 * @param insType
	 * @return
	 */
	public int getInstrumentCount(String sampleRef, String insType) {
		int count = 0;
		List<AnalysisModel> l_analyticalList = nbPage.getAnalysisCache().getAnalyticalList();
		Iterator<AnalysisModel> i_analyticals = l_analyticalList.iterator();
		while (i_analyticals.hasNext()) {
			AnalysisModel a = (AnalysisModel) i_analyticals.next();
			if (a.getCenSampleRef().equals(sampleRef) && a.getInstrumentType().equals(insType) && !a.isSetToDelete())
				count++;
		}
		return count;
	}

	/**
	 * Performs the Quick Link All from AnalyticalService
	 * This method links all batches matching NBRef
	 *
	 */
	public boolean performQuickLinkAll(String nbRef, List<String> sites) {
		long startTime = System.currentTimeMillis();
		try {
			/**
			 * split the nbRef and remove the leading zeroes first element is the notebook and second is the experiment
			 */
			AnalyticalServiceDelegate analyticalServiceDelegate = ServiceController.getAnalyticalServiceDelegate(MasterController.getUser().getSessionIdentifier());
			String nbRef2 = stripLeading(nbRef, '0');

            String query = "[Sample Reference] Contains \"" + nbRef + "%\" or " + "[Sample Reference] Contains \"" + nbRef2
                    + "%\"";
            List<SearchResult> al_Linked = new ArrayList<SearchResult>();
            analyticalServiceSummary = analyticalServiceDelegate.retrieveAnalyticalServiceData(query, new ArrayList<String>(sites));
            Iterator<SearchResult> it = analyticalServiceSummary.searchResultFiles();
            if (it.hasNext()) {
                while (it.hasNext()) {
                    SearchResult sResult = (SearchResult) it.next();
                    addAnalysis(sResult, al_Linked, false, nbRef);
                }
                if (al_Linked.size() > 0) {
                    markFileAsLinked(nbPage.getNotebookRefAsString(), al_Linked, true);
                    this.analyticalChangeListener.updateAnalyses();
                }
                return true;
            } else{
                return false;
            }

		} catch (Throwable e) {
			long endTime = System.currentTimeMillis();
			long totalElapsed = endTime - startTime;
			Throwable t = e.getCause();
			if (t != null && t.toString().indexOf("CyberLab") > -1) {
				JOptionPane.showMessageDialog(null, "CyberLab API failure.", "Error", JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing quickLinkAll, Not Displayed to User");
				return true;
			} else if (t != null && t.toString().indexOf("java.rmi.MarshalException") > -1
					&& totalElapsed > QUERY_TIMEOUT_MILLI_SECONDS) {
				JOptionPane.showMessageDialog(null, "The AnalyticalService search has timed out due to no response from CyberLab.", "Error",
						JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing quickLinkAll, Not Displayed to User");
				return true;
			} else if (t != null && t.toString().indexOf("CORBA.COMM_FAILURE") > -1) {
				JOptionPane.showMessageDialog(null, "The AnalyticalService/CyberLab search service is not available.", "Error",
						JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing quickLinkAll, Not Displayed to User");
				return true;
			} else {
				ceh.logExceptionMsg(null, "Error occurred while performing quickLinkAll", e);
			}
			return false;
		}
	}


	private void addAnalysis(SearchResult analyticalServiceResult, List<SearchResult> al_Linked, boolean isAdvancedLinking, String nbRef) {
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
//					// format the AnalyticalService result sampleRefs[i] to Cen if possible
//					String formattedSampleRefs;
//					if (isAdvancedLinking && !(stripLeading(sampleRefs[i], '0').startsWith(stripLeading(nbRef, '0'))))
//						formattedSampleRefs = sampleRefs[i];
//					else
//						formattedSampleRefs = formatForLeadingZeros(sampleRefs[i]);

//					sampleRefs[i] = formatForLeadingZeros(sampleRefs[i]);
					aa = acache.isExistingAnalysis(analyticalServiceResult.getCyberlabID(), sampleRefs[i]);
					if (al_Linked == null) {
						al_Linked = new ArrayList<SearchResult>();
					}
					if (aa == null) {
						aa = createCeNAnalysis(analyticalServiceResult, sampleRefs[i]);
					} else {
						aa.setToDelete(false);
					}
					if (analyticalServiceResult.getSite() != null) {
						al_Linked.add(analyticalServiceResult);
					}
				}
				if (sampleRefContainer != null) {
					sampleRefContainer.buildAnalyticalTree(nbRef);
				}
				MasterController.getGUIComponent().enableSaveButtons();
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
				SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");
				try {
					String s = format.format(analyticalServiceResult.getExperimentTime());
					aa.setExperimentTime(s);
				} catch (Exception e) {
					// silently process the error
					aa.setExperimentTime(analyticalServiceResult.getExperimentTime().toString());
				}
			} else
				aa.setExperimentTime("");
			aa.setFileName(AnalyticalServiceDelegate.windowsValidFileName(analyticalServiceResult.getFilename()));
			if (analyticalServiceResult.getFilesize() != null)
				aa.setFileSize(analyticalServiceResult.getFilesize().longValue());
			aa.setFileType(analyticalServiceResult.getFileType());
			aa.setGroupId(analyticalServiceResult.getGroupID());
			aa.setInstrument(analyticalServiceResult.getInstrument());
			if (analyticalServiceResult.getInstrumentType() == null)
				aa.setInstrumentType("Unknown");
			else
				aa.setInstrumentType(analyticalServiceResult.getInstrumentType());
			aa.setSite(analyticalServiceResult.getSite());
			aa.setSiteCode(analyticalServiceResult.getSite());  // VB 12/3 siteCode is saved in DB
			aa.setServer(analyticalServiceResult.getServer());
			if (analyticalServiceResult.getFile() != null)
				aa.setUrl(analyticalServiceResult.getFile().toString());
			else
				aa.setUrl("");
			aa.setUserId(analyticalServiceResult.getUserID());
			if (analyticalServiceResult.getVersion() != null)
				aa.setVersion(analyticalServiceResult.getVersion().longValue());
			aa.setComments("");
			return aa;
		}



	//This method links to a Specific batch mentioned in the sampleRef
	public boolean performQuickLink(String sampleRef, List<String> sites) {
		long startTime = System.currentTimeMillis();
		try {
			/**
			 * split the nbRef and remove the leading zeroes first element is the notebook and second is the experiment
			 */
			AnalyticalServiceDelegate analyticalServiceDelegate = ServiceController.getAnalyticalServiceDelegate(MasterController.getUser().getSessionIdentifier());

            String sampleRef2 = stripLeading(sampleRef, '0');
            String query = "[Sample Reference] = \"" + sampleRef + "\" or " + "[Sample Reference] = \"" + sampleRef2 + "\"";
            sites = new ArrayList<String>();
            sites.add(nbPage.getSiteCode());
            List<SearchResult> al_Linked = new ArrayList<SearchResult>();
            // and the following analyticalServiceSummary1 is a subset of analyticalServiceSummary
            SearchResultsSummary analyticalServiceSummary1 = analyticalServiceDelegate.retrieveAnalyticalServiceData(query, new ArrayList<String>(sites));
            Iterator<SearchResult> it = analyticalServiceSummary1.searchResultFiles();
            if (it.hasNext()) {
                while (it.hasNext()) {
                    SearchResult sResult = (SearchResult) it.next();
                    addAnalysis(sResult, al_Linked, false, sampleRef);
                }
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


	private void markFileAsLinked(String sampleRef, List<SearchResult> fileList, boolean linked) {
		try {
			AnalyticalServiceDelegate analyticalServiceDelegate = ServiceController.getAnalyticalServiceDelegate(MasterController.getUser().getSessionIdentifier());
			if (analyticalServiceDelegate.canMarkFileAsLinked())
				analyticalServiceDelegate.markFileAsLinked(sampleRef, fileList, linked);
		} catch (Exception e) {
			ceh.logExceptionMsg(null, "Error occurred while marking Files Linked/Unlinked in AnalyticalService", e);
		}
	}

	public void performMap(String mapFromSampleRef, String mapToSampleRef) {
		List<AnalysisModel> analysisModelList = new ArrayList<AnalysisModel>();
		AnalysisCacheModel ac = nbPage.getAnalysisCache();
		Iterator<AnalysisModel> i_analyticals = ac.getAnalyticalList().iterator();
		try {
			AnalyticalServiceDelegate analyticalServiceDelegate = ServiceController.getAnalyticalServiceDelegate(MasterController.getUser().getSessionIdentifier());
			while (i_analyticals.hasNext()) {
				AnalysisModel a = (AnalysisModel) i_analyticals.next();
				if (a.getCenSampleRef().equals(mapFromSampleRef)) {
					a.setCenSampleRef(mapToSampleRef);
					a.setModelChanged(true);
					analysisModelList.add(a);
				}
			}
			analyticalServiceDelegate.setFileBatchID(analysisModelList);
			MasterController.getGUIComponent().enableSaveButtons();
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public void performAnalysisMap(String mapFrom, String mapTo, AnalysisModel selectedAnalysis) {
		List<AnalysisModel> analysisModelList = new ArrayList<AnalysisModel>();
		try {
			AnalyticalServiceDelegate analyticalServiceDelegate = ServiceController.getAnalyticalServiceDelegate(MasterController.getUser().getSessionIdentifier());
			analysisModelList.add(selectedAnalysis);
			analyticalServiceDelegate.setFileBatchID(analysisModelList);
		} catch (Exception error) {
			error.printStackTrace();
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
		return new SearchResultImpl(selectedAnalysis.getCyberLabDomainId(), selectedAnalysis.getCyberLabFileId(), selectedAnalysis
				.getCyberLabFolderId(), sampleRef, batchId, selectedAnalysis.getGroupId(), selectedAnalysis.getUserId(),
				selectedAnalysis.getDomain(), selectedAnalysis.getExperiment(), date, selectedAnalysis.getInstrument(),
				selectedAnalysis.getInstrumentType(), selectedAnalysis.getSite(), selectedAnalysis.getFileType(), selectedAnalysis
						.getFileName(), selectedAnalysis.getFileSize());
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
	 * @param cenSampleRef
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


	public void unLink(AnalysisModel a) {
		a.setModelChanged(true);
		a.setLinked(false);
		nbPage.getAnalysisCache().deleteAnalysis(a);
		String[] sampleRef = { a.getCenSampleRef() };
		String[] batchId = { a.getCenSampleRef() };
		SearchResult resultToUnlink = getSearchResult(sampleRef, batchId, a);
		List<SearchResult> list = new ArrayList<SearchResult>();
		list.add(resultToUnlink);
		if (resultToUnlink.getSite() != null) {
			markFileAsLinked(nbPage.getNotebookRefAsString(), list, false);
			// markFileAsLinked(a.getAnalyticalServiceSampleRef(), list, false);
		}
	}

	/**
	 * @param sampleRef
	 */
	public void updateComments(String sampleRef, String comments) {
		List<AnalysisModel> al = nbPage.getAnalysisCache().getAnalysisListForBatch(sampleRef);
		Iterator<AnalysisModel> it = al.iterator();
		while (it.hasNext()) {
			AnalysisModel aa = (AnalysisModel) it.next();
			aa.setComments(comments);
			aa.setModified(true);
		}
	}

	/**
	 *
	 * @param sampleRef
	 * @param date
	 * @param insType
	 * @param insName
	 * @param fileName
	 * @param expMethod
	 */
	public void doFileUpload(String sampleRef, String batchID, Date date, String insType, String fileName, String expMethod) {
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
				JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "This file is too large to upload", "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			byte[] b = new byte[(int) f.length()];
			InputStream fis = new FileInputStream(fileName);
			fis.read(b);
			fis.close();
			UploadFileAttributes fileAttributes = new UploadFileAttributes(fileNameOnly, f.length(), InetAddress
					.getLocalHost().getHostName(), filePath, sampleRef, batchID, MasterController.getUser().getSiteCode(), date,
					new Date(f.lastModified()), MasterController.getUser().getNTUserID(), domain);
			fileAttributes.setInstrumentType(insType);
			fileAttributes.setExperimentName(expMethod);
			AnalyticalServiceDelegate analyticalServiceDelegate = ServiceController.getAnalyticalServiceDelegate(MasterController.getUser().getSessionIdentifier());
			SearchResult resultI = analyticalServiceDelegate.doFileUpload(fileAttributes, b);
			if (resultI != null) {
				SearchResult result = (SearchResult) resultI;
				addAnalysis(result, null, true, sampleRef);
			}
		} catch (Exception e) { // Temporary until we figure out why the AnalyticalService exception is four levels down.
			try {
				e.printStackTrace();
				if (e.getCause().getCause().getCause().getCause().getMessage().indexOf("File already exists on server") > -1) {
				//if (e.getCause().toString().indexOf("File already exists on server") > -1) {
					JOptionPane.showMessageDialog(MasterController.getGuiComponent(), "The File \'" + fileNameOnly + "\' already exists on server.", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					e.printStackTrace();
					ceh.logExceptionMsg(MasterController.getGuiComponent(), "Error occurred while performing Manual Upload\n" + e.getMessage(), e);
				}
			} catch (HeadlessException e1) {
				ceh.logExceptionMsg(MasterController.getGuiComponent(), "Error occurred while performing Manual Upload\n" + e.getMessage(), e);
			}
		}
	}

	/**
	 * @return
	 */
	public List<String> getAllSampleRefs() {
		return nbPage.getAnalysisCache().getDistinctSampleRefs();
	}

	public List<String> getSampleRefsFromBatchInfo(BatchType batchType) {
		List<String> al_return = new ArrayList<String>();
		if (nbPage.getAllProductBatchModelsInThisPage() == null)
			return al_return;
		List<ProductBatchModel> al = nbPage.getAllProductBatchModelsInThisPage();
		Iterator<ProductBatchModel> it = al.iterator();
		while (it.hasNext()) {
			//AbstractBatch ab = (AbstractBatch) it.next();
			ProductBatchModel ab = (ProductBatchModel) it.next();
			////////////if (ab.getType().equals(batchType))
				al_return.add(ab.getBatchNumberAsString());
		}
		return al_return;
	}

	public boolean isAnalysisEmptyForBatch(String batchNumber) {
		List<AnalysisModel> analyses = nbPage.getAnalysisListForBatch(batchNumber);
		return !(analyses != null && analyses.size() > 0);
	}
}
