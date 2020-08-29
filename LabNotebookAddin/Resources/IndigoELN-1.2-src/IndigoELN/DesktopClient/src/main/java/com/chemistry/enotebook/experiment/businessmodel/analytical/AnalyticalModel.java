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
/*
 * Created on Dec 16, 2004
 *
 * 
 */
package com.chemistry.enotebook.experiment.businessmodel.analytical;

import com.chemistry.enotebook.analyticalservice.classes.SearchResult;
import com.chemistry.enotebook.analyticalservice.classes.SearchResultsSummary;
import com.chemistry.enotebook.analyticalservice.classes.UploadFileAttributes;
import com.chemistry.enotebook.analyticalservice.clean.SearchResultImpl;
import com.chemistry.enotebook.analyticalservice.delegate.AnalyticalServiceDelegate;
import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.common.errorhandler.CeNErrorHandler;
import com.chemistry.enotebook.experiment.datamodel.analytical.AbstractAnalysis;
import com.chemistry.enotebook.experiment.datamodel.analytical.Analysis;
import com.chemistry.enotebook.experiment.datamodel.analytical.AnalysisCache;
import com.chemistry.enotebook.experiment.datamodel.analytical.AnalysisFactory;
import com.chemistry.enotebook.experiment.datamodel.batch.AbstractBatch;
import com.chemistry.enotebook.experiment.datamodel.batch.BatchType;
import com.chemistry.enotebook.experiment.datamodel.batch.ProductBatch;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * 
 * The data that appears on the QC analytical tab section is built here.
 * 
 */
public class AnalyticalModel {
	private NotebookPage nbPage;		// Used to get the BatchCache and AnalysisCache
	private Vector v_columnHeader;		// AnalyticalSummary table header values
	private Vector v_analyticalData;	// A Vector of Vectors, each inner Vector is a row of AnalyticalSummarytable
	private static SearchResultsSummary analyticalServiceSummary; // static because AnalyticalService search is not performed 
	
	// for subsequent clicks
	private static final CeNErrorHandler ceh = CeNErrorHandler.getInstance();
	private static int QUERY_TIMEOUT_MILLI_SECONDS = 180000;

    private static final Log log = LogFactory.getLog(AnalyticalModel.class);

    /**
	 * Constructor
	 * 
	 * @param nbPage
	 */
	public AnalyticalModel(NotebookPage nbPage) {
		super();
		this.setNotebookPage(nbPage);
	}

	/**
	 * Empty Constructor
	 * 
	 */
	public AnalyticalModel() {
		super();
	}

	/**
	 * @param nbPage
	 */
	public void setNotebookPage(NotebookPage nbPage) {
		this.nbPage = nbPage;
		if (nbPage != null && nbPage.getBatchCache() != null)
			buildCeNAnalyticalData(); // gets the data from CeNDB,
		// pre-AnalyticalServiceSearch
	}

	/**
	 * Indicates whether or not the model should be edited.
	 * 
	 * @return
	 */
	public boolean isEditable() {
		boolean result = false;
		if (nbPage != null)
			result = nbPage.isPageEditable();
		return result;
	}

	/**
	 * parses the analyticaldata read from AnalysisCache and intializes the data vectors
	 * 
	 */
	private void buildCeNAnalyticalData() {
		List l_analyticalList = new ArrayList();
		if (nbPage.getAnalysisCache() != null)
			l_analyticalList = nbPage.getAnalysisCache().getAnalyticalList();
		if (l_analyticalList.size() > 0) { // If there is Analytical data
			// saved in the CeN database
			// build header
			v_columnHeader = new Vector();
			v_columnHeader.add("Nbk Batch #");
			
			// Add Instruments
			ArrayList al_distinctInsTypes = nbPage.getAnalysisCache().getDistinctInstrumentTypes();
			ArrayList al_distinctSampleRefs = nbPage.getAnalysisCache().getDistinctSampleRefes();
			Collections.sort(al_distinctSampleRefs, String.CASE_INSENSITIVE_ORDER);
			for (int i = 0; i < al_distinctInsTypes.size(); i++)
				v_columnHeader.add(al_distinctInsTypes.get(i).toString());
			v_columnHeader.add("Quick Link");
			v_columnHeader.add("PurificationService Data");
			v_columnHeader.add("Analytical Comments");
			
			// build Analytical Data from CeN
			v_analyticalData = new Vector();
			
			// First add the rows for sampleRefs of the Batch info
			List cenBatches = nbPage.getBatchCache().getSortedBatchList();
			Iterator it = cenBatches.iterator();
			while (it.hasNext()) {
				AbstractBatch ab = (AbstractBatch) it.next();
//				if (!ab.getType().equals(BatchType.ACTUAL_PRODUCT))
//					continue;
				
				if (al_distinctSampleRefs.contains(ab.getBatchNumberAsString()))
					al_distinctSampleRefs.remove(ab.getBatchNumberAsString());
				
				Vector v = new Vector(); // each row vector
				v.add(ab.getBatchNumber());
				for (int j = 0; j < al_distinctInsTypes.size(); j++)
					v.add(getInstrumentCount(ab.getBatchNumberAsString(), al_distinctInsTypes.get(j).toString()) + "");
				v.add("Q"); // for the quick Link, ButtonCellRenderer in the viewer class;
				if ((((ProductBatch)ab).getRegInfo().getCompoundSource().equals("INPRODINT") ||
						 ((ProductBatch)ab).getRegInfo().getCompoundSource().equals("INPRODEXT")) &&
						 ((ProductBatch)ab).getConversationalBatchNumber().length() > 0)
						v.add("P");		// Link to PurificationService Data
					else
						v.add("");
				v.add(nbPage.getAnalysisCache().getComments(ab.getBatchNumberAsString()));
				v_analyticalData.add(v);
			}
			
			// Append the actual Analyticalinfo
			for (int i = 0; i < al_distinctSampleRefs.size(); i++) {
				if (al_distinctSampleRefs.get(i) == null)
					continue;
				Vector v = new Vector(); // represents each row
				String sampleRef = al_distinctSampleRefs.get(i).toString();
				v.add(sampleRef);
				for (int j = 0; j < al_distinctInsTypes.size(); j++)
					v.add(getInstrumentCount(sampleRef, al_distinctInsTypes.get(j).toString()) + "");
				v.add(""); 		// for the quick Link, ButtonCellRenderer in the viewer class;
				v.add("");		// No PurificationService Data for non-batch results
				v.add(nbPage.getAnalysisCache().getComments(al_distinctSampleRefs.get(i).toString()));
				v_analyticalData.add(v);
			}
		} else {
			// Build Intital Analytical Summary Table Header
			v_columnHeader = new Vector();
			v_columnHeader.add("Nbk Batch #");
			v_columnHeader.add(""); // Blanks for better GUI look
			v_columnHeader.add(" ");
			v_columnHeader.add("Quick Link");
			v_columnHeader.add("PurificationService Data");
			v_columnHeader.add("Analytical Comments");
			// Build Initial Analytical Summary Table Data
			v_analyticalData = new Vector();
			List cenBatches = nbPage.getBatchCache().getSortedBatchList();
			Iterator it = cenBatches.iterator();
			while (it.hasNext()) {
				AbstractBatch ab = (AbstractBatch) it.next();
				if (!ab.getType().equals(BatchType.ACTUAL_PRODUCT))
					continue;
				Vector v = new Vector(); // each row vector
				v.add(ab.getBatchNumber());
				v.add("");
				v.add(""); // for the above two blank columns
				v.add("Q"); // for the quick Link, ButtonCellRenderer in the viewer class;
				if ((((ProductBatch)ab).getRegInfo().getCompoundSource().equals("INPRODINT") ||
						 ((ProductBatch)ab).getRegInfo().getCompoundSource().equals("INPRODEXT")) &&
						 ((ProductBatch)ab).getConversationalBatchNumber().length() > 0)
					v.add("P");		// Link to PurificationService Data
				else
					v.add("");
				v.add("");
				v_analyticalData.add(v);
			}
		}
	}

	/**
	 * Performs the Quick Link All from AnalyticalService
	 * 
	 * 
	 */
	public boolean performQuickLinkAll(String nbRef, ArrayList<String> sites) {
		long startTime = System.currentTimeMillis();
		try {
			/**
			 * split the nbRef and remove the leading zeroes first element is the notebook and second is the experiment
			 */
			AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();
			// nbRef = nbRef + "-";
			// strip the leading zeroes as in analyticalService
			// String[] s = nbRef.split("-");
			// String noteBookRef = stripLeading(s[0], '0');
			// String expNo = stripLeading(s[1],'0');
			// String analyticalServiceFormatNbRef = noteBookRef+"-"+expNo;
			String nbRef2 = stripLeading(nbRef, '0');

            String query = "[Sample Reference] Contains \"" + nbRef + "%\" or " + "[Sample Reference] Contains \"" + nbRef2 + "%\"";
            log.info("AnalyticalService QLA Query: " + query + "\nSites: " + sites );
            ArrayList<SearchResult> al_Linked = new ArrayList<SearchResult>();
            analyticalServiceSummary = analyticalServiceDelegate.retrieveAnalyticalServiceData(query, sites);
            Iterator<SearchResult> it = analyticalServiceSummary.searchResultFiles();
            if (it.hasNext()) {
                while (it.hasNext()) {
                    SearchResult sResult = it.next();
                    addAnalysis(sResult, al_Linked, false, nbRef);
                }
                refreshAnalyticalData();
                markFileAsLinked(nbPage.getNotebookRefAsString(), al_Linked, true);
                return true;
            } else{
                return false;
            }
            /*
             * } else { // do not perform analyticalService search if it is just performed return false; }
             */

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

	/**
	 * Adds to Analysis Cache
	 * 
	 * @param analyticalServiceResult
	 */
	private void addAnalysis(SearchResult analyticalServiceResult, ArrayList<SearchResult> al_Linked, boolean isAdvancedLinking, String nbRef) {
		if (analyticalServiceResult.getSampleReference() != null) {
			String[] sampleRefs = analyticalServiceResult.getSampleReference();
			if (sampleRefs != null && sampleRefs.length > 0) {
				AbstractAnalysis aa = null;
				AnalysisCache ac = nbPage.getAnalysisCache();
				for (int i = 0; i < sampleRefs.length; i++) {
					if (!isAdvancedLinking && sampleRefs[i] != null
							&& !(stripLeading(sampleRefs[i], '0').startsWith(stripLeading(nbRef, '0')))) {
						continue;
					}
					// format the AnalyticalService result sampleRefs[i] to Cen if possible
					String formattedSampleRefs;
					if (isAdvancedLinking &&
						!(stripLeading(sampleRefs[i], '0').startsWith(stripLeading(nbRef, '0'))))
						formattedSampleRefs = sampleRefs[i];
					else
						formattedSampleRefs = formatForLeadingZeros(sampleRefs[i]);
					
					aa = ac.isExistingAnalysis(analyticalServiceResult.getCyberlabID(), sampleRefs[i]);
					if (aa == null){
						aa = ac.createAnalysis(analyticalServiceResult.getCyberlabID());
						fillCeNAnalysis(analyticalServiceResult, formattedSampleRefs, sampleRefs[i], aa);
						if (al_Linked != null && analyticalServiceResult.getSite() != null)
							al_Linked.add(analyticalServiceResult);
					}
				}
			}
		}
	}

	/**
	 * @param analyticalServiceResult
	 * @param sampleRef
	 * @return
	 */
	private void fillCeNAnalysis(SearchResult analyticalServiceResult, String cenSampleRef, String analyticalServiceSampleRef, AbstractAnalysis aa) 
	{
		aa.setLinked(true);
		aa.setCenSampleRef(cenSampleRef);
		aa.setAnalyticalServiceSampleRef(analyticalServiceSampleRef);
		aa.setModified(true);
		aa.setCyberLabDomainId(analyticalServiceResult.getCyberlabDomainID());
		aa.setCyberLabFolderId(analyticalServiceResult.getCyberlabFolderID());
		aa.setCyberLabLCDFId(analyticalServiceResult.getCyberlabLCDFID());
		aa.setCyberLabUserId(analyticalServiceResult.getCyberlabUserID());
		aa.setDomain(analyticalServiceResult.getDomain());
		aa.setExperiment(analyticalServiceResult.getExperiment());
		if (analyticalServiceResult.getExperimentTime() != null) {
//			SimpleDateFormat format = new SimpleDateFormat("EEE MMM d, yyyy");
//			SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy HH:mm:ss");
			SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");
			TimeZone tz = TimeZone.getTimeZone("GMT");  // need to use GMT to prevent wrong date display
			format.setTimeZone(tz);
			try {
				String s = format.format(analyticalServiceResult.getExperimentTime());
				aa.setExperimentTime(s);
			} catch (Exception e) {
				// silently process the error
				aa.setExperimentTime(analyticalServiceResult.getExperimentTime().toString());
			}
		} else
			aa.setExperimentTime("");
		aa.setFileName(AnalyticalServiceDelegate.windowsValidFileName( analyticalServiceResult.getFilename()));
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
		aa.setServer(analyticalServiceResult.getServer());
		if (analyticalServiceResult.getFile() != null)
			aa.setUrl(analyticalServiceResult.getFile().toString());
		else
			aa.setUrl("");
		aa.setUserId(analyticalServiceResult.getUserID());
		if (analyticalServiceResult.getVersion() != null)
			aa.setVersion(analyticalServiceResult.getVersion().longValue());
		aa.setComments("");
	}

	/**
	 * Fills with the latest batch values, to update the new batches or if the comments have been changed in the batchInfo
	 * 
	 */
	public void refreshAnalyticalData() {
		buildCeNAnalyticalData();
	}

	/**
	 * builds each row of the table with data
	 * 
	 */
	public Vector getAnalyticalData() {
		return v_analyticalData;
	}

	/**
	 * Returns the column header, if there is no analyticals in the CEN Database and the quicklink is not performed, blank cloumns
	 * are retuned
	 * 
	 * @return
	 */
	public Vector getColumnHeader() {
		return v_columnHeader;
	}

	/**
	 * 
	 * @param sampleRef
	 * @param insType
	 * @return
	 */
	public int getInstrumentCount(String sampleRef, String insType) {
		int count = 0;
		List l_analyticalList = nbPage.getAnalysisCache().getAnalyticalList();
		Iterator i_analyticals = l_analyticalList.iterator();
		while (i_analyticals.hasNext()) {
			Analysis a = (Analysis) i_analyticals.next();
			if (a.getCenSampleRef().equals(sampleRef) && a.getInstrumentType().equals(insType))
				count++;
		}
		return count;
	}

	/**
	 * performs the AnalyticalService link for a single Sample Ref
	 * 
	 * 
	 */
	public boolean performQuickLink(String sampleRef, ArrayList sites) {
		long startTime = System.currentTimeMillis();
		try {
			/**
			 * split the nbRef and remove the leading zeroes first element is the notebook and second is the experiment
			 */
			AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();

            // String[] s = sampleRef.split("-");
            // if (s.length != 3) return false;
            // strip the leading zeroes as in analyticalService
            // String noteBookRef = stripLeading(s[0],'0');
            // String expNo = stripLeading(s[1],'0');
            // String lotNo = stripLeading(s[2],'0');
            // String analyticalServiceFormatSampleRef = noteBookRef+"-"+expNo+"-"+lotNo;
            String sampleRef2 = stripLeading(sampleRef, '0');
            String query = "[Sample Reference] = \"" + sampleRef + "\" or " + "[Sample Reference] = \"" + sampleRef2 + "\"";
            sites = new ArrayList();
            sites.add(nbPage.getSiteCode());
            log.info("AnalyticalService QL Query: " + query + "\nSites: " + sites );
            ArrayList al_Linked = new ArrayList();

            // and the following analyticalServiceSummary1 is a subset of analyticalServiceSummary
            SearchResultsSummary analyticalServiceSummary1 = analyticalServiceDelegate.retrieveAnalyticalServiceData(query, sites);
            Iterator it = analyticalServiceSummary1.searchResultFiles();
            if (it.hasNext()) {
                while (it.hasNext()) {
                    SearchResult sResult = (SearchResult) it.next();
                    addAnalysis(sResult, al_Linked, false, sampleRef);
                }
                refreshAnalyticalData();
                markFileAsLinked(nbPage.getNotebookRefAsString(), al_Linked, true);
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
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "CyberLab API failure.", "Error", JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing QuickLink, Not Displayed to User");
				return true;
			} else if (t != null && t.toString().indexOf("java.rmi.MarshalException") > -1
					&& totalElapsed > QUERY_TIMEOUT_MILLI_SECONDS) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "The AnalyticalService search has timed out due to no response from CyberLab.", "Error",
						JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing QuickLink, Not Displayed to User");
				return true;
			} else if (t != null && t.toString().indexOf("CORBA.COMM_FAILURE") > -1) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "The AnalyticalService/CyberLab search service is not available.", "Error",
						JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing QuickLink, Not Displayed to User");
				return true;
			} else {
				ceh.logExceptionMsg(MasterController.getGUIComponent(), "Error occurred while performing QuickLink", e);
			}
			return false;
		}
	}

	/** 
	 * performs the AnalyticalService PurificationService Data Search for a single Sample Ref
	 *
	 */
	public ArrayList performPurificationServiceDataSearch(String cenSampleRef, String purificationServiceSampleRef, ArrayList sites)
	{
		ArrayList analytical = new ArrayList();

		long startTime = System.currentTimeMillis();
		try {
			AnalyticalServiceDelegate analyticalServiceDelegate= new AnalyticalServiceDelegate();

            String query1 = "", query2 = "";

//				if (purificationServiceSampleRef != null)
//					query1 = "[Sample Reference] = \"" + purificationServiceSampleRef + "\" or [Sample Reference] = \"" + stripLeading(purificationServiceSampleRef, '0') + "\"";
//				if (cenSampleRef != null)
//					query1 += ((query1.length() > 0) ? " or " : "") +
//					          "[Sample Reference] = \"" + cenSampleRef + "\" or [Sample Reference] = \"" + stripLeading(cenSampleRef, '0') + "\"";
            if (purificationServiceSampleRef != null)
                query1 = "[Sample Reference] contains \"%" + stripLeading(purificationServiceSampleRef, '0') + "\"";
            if (cenSampleRef != null)
                query1 += ((query1.length() > 0) ? " or " : "") +
                          "[Sample Reference] contains \"%" + stripLeading(cenSampleRef, '0') + "\"";
//				if (cenSampleRef != null) query2 = "[Sample Reference] contains \"%" + stripLeading(cenSampleRef, '0') + "\"";

            sites = new ArrayList();
            sites.add(nbPage.getSiteCode());
            log.info("PurificationService AnalyticalService Query: " + query1 + "\nSites: " + sites );
//				log.info("PurificationService AnalyticalService Query: " + query1 + "\n" + query2 + "\nSites: " + sites );

            //and the following analyticalServiceSummary1 is a subset of analyticalServiceSummary
            SearchResultsSummary analyticalServiceSummary = null;
            if (query1.length() > 0) {
                analyticalServiceSummary = analyticalServiceDelegate.retrieveAnalyticalServiceData(query1, sites);
                processPurificationServiceResults(analyticalServiceSummary.searchResultFiles(), cenSampleRef, purificationServiceSampleRef, analytical);
            }
//				if (query2.length() > 0) {
//					analyticalServiceSummary = analyticalServiceDelegate.retrieveAnalyticalServiceData(query2, sites);
//					processResults(analyticalServiceSummary.searchResultFiles(), cenSampleRef, purificationServiceSampleRef, analytical);
//				}
//				Iterator it = analyticalServiceSummary1.searchResultFiles();
//				if (it.hasNext()) {
//					AnalysisCache ac = nbPage.getAnalysisCache();
//
//					while(it.hasNext()) {
//						SearchResult sResult = (SearchResult)it.next();
//
//						// Verify that the
//						AbstractAnalysis aa = null;
//						if (cenSampleRef != null && sResult.getSampleReference()[0].equals(cenSampleRef))
//							aa = ac.isExistingAnalysis(sResult.getCyberlabID(), sResult.getSampleReference()[0]);
//
//						if (aa == null) {
//							aa = AnalysisFactory.getAnalysis(sResult.getCyberlabID());
//							if (cenSampleRef != null && sResult.getSampleReference() != null && sResult.getSampleReference().length > 0 &&
//								sResult.getSampleReference()[0].equals(cenSampleRef))
//								fillCeNAnalysis(sResult, cenSampleRef, cenSampleRef, aa);
//							else
//								fillCeNAnalysis(sResult, purificationServiceSampleRef, purificationServiceSampleRef, aa);
//							analytical.add(aa);
//						}
//					}
//					return analytical;
//				}
            return (analytical.size() > 0) ? analytical : null;

		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long totalElapsed = endTime - startTime;
			Throwable t = e.getCause();
			if (t != null && t.toString().indexOf("CyberLab") > -1) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "CyberLab API failure.",
						"Error", JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing PurificationServiceDataSearch, Not Displayed to User");
			} else if (t != null && t.toString().indexOf("java.rmi.MarshalException") > -1 && totalElapsed > QUERY_TIMEOUT_MILLI_SECONDS) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "The AnalyticalService search has timed out due to no response from CyberLab.",
						"Error", JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing PurificationServiceDataSearch, Not Displayed to User");
			} else if (t != null && t.toString().indexOf("CORBA.COMM_FAILURE") > -1 ) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "The AnalyticalService/CyberLab search service is not available.",
						"Error", JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing PurificationServiceDataSearch, Not Displayed to User");
			} else {
				ceh.logExceptionMsg(MasterController.getGUIComponent(), "Error occurred while performing PurificationServiceDataSearch", e);
			}
		}
		return null;
	}

	private void processPurificationServiceResults(Iterator it, String cenSampleRef, String purificationServiceSampleRef, ArrayList analytical) {
		if (it.hasNext()) {
			AnalysisCache ac = nbPage.getAnalysisCache();

			while(it.hasNext()) {
				SearchResult sResult = (SearchResult)it.next();				
				// Verify that the 
				AbstractAnalysis aa = null;
//				if (cenSampleRef != null && sResult.getSampleReference()[0].equals(cenSampleRef)) 
				aa = ac.isExistingAnalysisById(sResult.getCyberlabID());
				
				if (aa == null) {
					aa = AnalysisFactory.getAnalysis(sResult.getCyberlabID());
					if (cenSampleRef != null && sResult.getSampleReference() != null && sResult.getSampleReference().length > 0 &&
						sResult.getSampleReference()[0].equals(cenSampleRef))
						fillCeNAnalysis(sResult, cenSampleRef, cenSampleRef, aa);
					else
						fillCeNAnalysis(sResult, purificationServiceSampleRef, purificationServiceSampleRef, aa);
					analytical.add(aa);
				}
			}
		}
	}

	/**
	 * 
	 * @param query
	 * @param sites
	 */
	public boolean performAdvancedLinkAll(String query, ArrayList sites) {
		boolean returnFlag = false;
		long startTime = System.currentTimeMillis();
		try {
			AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();
			
            log.info("AnalyticalService AL Query: " + query + "\nSites: " + sites );
            SearchResultsSummary analyticalServiceSummary2 = analyticalServiceDelegate.retrieveAnalyticalServiceData(query, sites);
            if (analyticalServiceSummary2.getNumberOfFilesMatchingQuery() > 15) {
                int selection = JOptionPane.showConfirmDialog(MasterController.getGUIComponent(),
                        "There are more than 15 results found, Do you want to display the results?", "Warning",
                        JOptionPane.YES_NO_OPTION);
                if (selection == JOptionPane.NO_OPTION)
                    return false;
            }
            ArrayList al_Linked = new ArrayList();
            Iterator it = analyticalServiceSummary2.searchResultFiles();
            if (it.hasNext()) {
                while (it.hasNext()) {
                    SearchResult sResult = (SearchResult) it.next();
                    addAnalysis(sResult, al_Linked, true, nbPage.getNotebookRefAsString());
                }
                refreshAnalyticalData();
                returnFlag = true;
            } else {
                // No results found
                returnFlag = false;
            }
            if (al_Linked.size() > 0){
                markFileAsLinked(nbPage.getNotebookRefAsString(), al_Linked, true);
            }

		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long totalElapsed = endTime - startTime;
			// System.out.println(totalElapsed + " ms");
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
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "Failed to communicate with AnalyticalService/CyberLab search service.",
						"Error", JOptionPane.ERROR_MESSAGE);
				ceh.logExceptionWithoutDisplay(e, "Error occurred while performing Advanced Link, Not Displayed to User");
				return true;
			} else {
				ceh.logExceptionMsg(MasterController.getGUIComponent(), "Error occurred while performing Advanced Link", e);
			}
			returnFlag = false;
		}
		return returnFlag;
	}

	/**
	 * if instruction flag is true resultList would be marked as linked in AnalyticalService for the sampleRef
	 * 
	 * @param string
	 * @param object
	 * @param b
	 */
	private void markFileAsLinked(String sampleRef, List fileList, boolean linked) {
		try {
			AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();
			if (analyticalServiceDelegate.canMarkFileAsLinked())
				analyticalServiceDelegate.markFileAsLinked(sampleRef, fileList, linked);
		} catch (Exception e) {
			ceh.logExceptionMsg(null, "Error occurred while marking Files Linked/Unlinked in AnalyticalService", e);
		}
	}

	/**
	 * Maps all the entries from mapFrom to mapTo batch numbers.
	 * 
	 * @param mapFrom
	 * @param mapTo
	 */
	public void performMap(String mapFromSampleRef, String mapToSampleRef) {
		ArrayList searchResultList = new ArrayList();
		String batchNumbers[] = new String[1];
		String sampleRefs[] = new String[1];
		AnalysisCache ac = nbPage.getAnalysisCache();
		Iterator i_analyticals = ac.getAnalyticalList().iterator();
		try {
			AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();
			SearchResult SearchResult = null;
			while (i_analyticals.hasNext()) {
				Analysis a = (Analysis) i_analyticals.next();
				if (a.getCenSampleRef().equals(mapFromSampleRef)) {
					a.setCenSampleRef(mapToSampleRef);
					a.setModified(true);
					batchNumbers[0] = a.getCenSampleRef();
					sampleRefs[0] = a.getCenSampleRef();
					SearchResult = getSearchResult(sampleRefs, batchNumbers, a);
					searchResultList.add(SearchResult);
				}
			}
			analyticalServiceDelegate.setFileBatchID(searchResultList);
			refreshAnalyticalData();
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public boolean isAnalysisEmptyForBatch(String batchNumber) {
		boolean isEmpty = true;
		AnalysisCache ac = nbPage.getAnalysisCache();
		Iterator i_analyticals = ac.getAnalyticalList().iterator();
		while (i_analyticals.hasNext()) {
			Analysis a = (Analysis) i_analyticals.next();
			if (a.getCenSampleRef().equals(batchNumber))
				isEmpty = false;
		}
		return isEmpty;
	}

	public void performAnalysisMap(String mapFrom, String mapTo, Analysis selectedAnalysis) {
		ArrayList searchResultList = new ArrayList();
		String batchNumbers[] = new String[1];
		String sampleRefs[] = new String[1];
		try {
			AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();
			SearchResult SearchResult = null;
			selectedAnalysis.setCenSampleRef(mapTo);
			selectedAnalysis.setModified(true);
			batchNumbers[0] = selectedAnalysis.getCenSampleRef();
			sampleRefs[0] = selectedAnalysis.getCenSampleRef();
			SearchResult = getSearchResult(sampleRefs, batchNumbers, selectedAnalysis);
			searchResultList.add(SearchResult);
			analyticalServiceDelegate.setFileBatchID(searchResultList);
			refreshAnalyticalData();
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public SearchResult getSearchResult(String[] sampleRef, String[] batchId, Analysis selectedAnalysis) {
		Date date = null;
		if (selectedAnalysis.getExperimentTime() == null || selectedAnalysis.getExperimentTime().length() == 0) {
			date = new Date();
		} else {
			try {
				//date = DateFormat.getDateInstance().parse(selectedAnalysis.getExperimentTime());
				date = NotebookPageUtil.getLocalDate(selectedAnalysis.getExperimentTime());
			} catch (ParseException error) {
				//exception case will take current date in milliseconds
				date = new Date();
			}
		}

		return new SearchResultImpl(selectedAnalysis.getCyberLabDomainId(), selectedAnalysis.getCyberLabFileId(), selectedAnalysis
				.getCyberLabFolderId(), sampleRef, batchId, selectedAnalysis.getGroupId(), selectedAnalysis.getUserId(),
				selectedAnalysis.getDomain(), selectedAnalysis.getExperiment(), date, selectedAnalysis.getInstrument(),
				selectedAnalysis.getInstrumentType(), selectedAnalysis.getSite(), selectedAnalysis.getFileType(), selectedAnalysis
						.getFileName(), selectedAnalysis.getFileSize());
	}

	/**
	 * Retrieves a file listed in the Analysis object from the AnalyticalService repository
	 * 
	 * @param a -
	 *            null or Analysis object
	 * 
	 */
	public byte[] retrieveFileFromAnalyticalService(Analysis a) {
		byte[] fileContents = null;
		if (a != null) {
			try {
				log.info("Retrieving file from AnalyticalService: " + a.getAnalyticalServiceSampleRef() + " " + a.getCyberLabFileId() + " " + a.getFileName());
				AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();
				fileContents = analyticalServiceDelegate.retrieveFileContents(a.getCyberLabDomainId(), a.getCyberLabFileId(), a.getSite());
			} catch (Exception e) {
				ceh.logExceptionMsg(null, "Error occurred while performing viewFile", e);
			}
		}
		return fileContents;
	}

	/**
	 * Removes the leading characters
	 * 
	 * @param s
	 * @param c
	 * @return
	 */
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
	 * @return
	 */
	public ArrayList getAllSampleRefs() {
		return nbPage.getAnalysisCache().getDistinctSampleRefes();
	}

	/**
	 * comment from the batchInfo
	 * 
	 * @param sampleRef
	 * @return
	 */
	public String getComment(String sampleRef) {
		if (nbPage.getAnalysisCache() == null)
			return "";
		String comment = "";
		List al = nbPage.getAnalysisCache().getAnalyticalList(sampleRef);
		Iterator it = al.iterator();
		while (it.hasNext()) {
			AbstractAnalysis aa = (AbstractAnalysis) it.next();
			if (aa.getComments() != null)
				comment = aa.getComments();
			break;
		}
		return comment;
	}

	public ArrayList getSampleRefsFromBatchInfo(BatchType batchType) {
		ArrayList al_return = new ArrayList();
		if (nbPage.getBatchCache() == null)
			return al_return;
		List al = nbPage.getBatchCache().getSortedBatchList();
		Iterator it = al.iterator();
		while (it.hasNext()) {
			AbstractBatch ab = (AbstractBatch) it.next();
			if (ab.getType().equals(batchType))
				al_return.add(ab.getBatchNumberAsString());
		}
		return al_return;
	}

	public void UnLink(Analysis a) {
		a.setModified(true);
		a.setLinked(false);
		nbPage.getAnalysisCache().deleteAnalysis(a);
		String[] sampleRef = { a.getCenSampleRef() };
		String[] batchId = { a.getCenSampleRef() };
		SearchResult resultToUnlink = getSearchResult(sampleRef, batchId, a);
		ArrayList list = new ArrayList();
		list.add(resultToUnlink);
		refreshAnalyticalData();
		if (resultToUnlink.getSite() != null) {
			markFileAsLinked(nbPage.getNotebookRefAsString(), list, false);
			// markFileAsLinked(a.getAnalyticalServiceSampleRef(), list, false);
		}
	}

	/**
	 * @param sampleRef
	 */
	public void updateComments(String sampleRef, String comments) {
		List al = nbPage.getAnalysisCache().getAnalyticalList(sampleRef);
		Iterator it = al.iterator();
		while (it.hasNext()) {
			AbstractAnalysis aa = (AbstractAnalysis) it.next();
			aa.setComments(comments);
			aa.setModified(true);
		}
		refreshAnalyticalData();
	}

	// Added to enable tool tips.
	public AbstractBatch getBatchAt(int row) {
		AbstractBatch result = null;
		List al = nbPage.getBatchCache().getBatches(BatchType.ACTUAL_PRODUCT_ORDINAL);
		Collections.sort(al);
		if (row >= 0 && row < al.size())
			result = (AbstractBatch) al.get(row);
		return result;
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
	public void doFileUpload(String sampleRef, String batchID, Date date, String insType, String fileName, String expMethod, String siteCode) {
		String fileNameOnly = null;
		try {
			int i = fileName.lastIndexOf(File.separator);
			String filePath = fileName.substring(0, i);
			fileNameOnly = fileName.substring(i + 1, fileName.length());
			String domain = MasterController.getUser().getNtDomain();
			// 
			int index = (domain == null) ? 0 : domain.indexOf(":");
			if (index > 1)
				domain = domain.substring(0, index);
			File f = new File(fileName);
			if (f.length() > Integer.MAX_VALUE) { // i.e. 2147483647
				JOptionPane.showMessageDialog(MasterController.getGUIComponent().getActiveDesktopWindow(),
						"This file is too large to upload", "Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			byte[] b = new byte[(int) f.length()];
			InputStream fis = new FileInputStream(fileName);
			fis.read(b);
			fis.close();
			UploadFileAttributes fileAttributes = new UploadFileAttributes(fileNameOnly, f.length(), 
					InetAddress.getLocalHost().getHostName(), filePath, sampleRef, batchID, siteCode, date,
					new Date(f.lastModified()), MasterController.getUser().getNTUserID(), domain);
			fileAttributes.setInstrumentType(insType);
			fileAttributes.setExperimentName(expMethod);
			AnalyticalServiceDelegate analyticalServiceDelegate = new AnalyticalServiceDelegate();
			SearchResult resultI = analyticalServiceDelegate.doFileUpload(fileAttributes, b);
			if (resultI != null) {
				SearchResult result = (SearchResult) resultI;
				addAnalysis(result, null, true, sampleRef);
			}
		} catch (Exception e) {
//			if (e.getCause().toString().indexOf("File already exists on server") > -1) {
			if (e.getCause().getCause().getMessage().indexOf("File already exists on server") > -1) {
				JOptionPane.showMessageDialog(MasterController.getGUIComponent(), "The File \'" + fileNameOnly + "\' already exists on server.",
						"Error", JOptionPane.ERROR_MESSAGE);
			} else {
				ceh.logExceptionMsg(MasterController.getGUIComponent(), "Error occurred while performing Manual Upload\n" + e.getMessage(), e);
			}
		} catch (Throwable t) {
			ceh.logExceptionMsg(MasterController.getGUIComponent(), "Error occurred while performing Manual Upload\n" + t.getMessage(), t);
		}
	}

	/**
	 * @param s
	 */
	public void unlinkAll(String sampleReference) {
		List al = nbPage.getAnalysisCache().getAnalyticalList(sampleReference);
		ArrayList listAnalyticalServiceObjects = new ArrayList();
		Iterator it = al.iterator();
		while (it.hasNext()) {
			Analysis a = (Analysis) it.next();
			a.setModified(true);
			String[] sampleRef = { a.getCenSampleRef() };
			String[] batchId = { a.getCenSampleRef() };
			SearchResult resultToUnlink = getSearchResult(sampleRef, batchId, a);
			if (a.getSite() != null)
				listAnalyticalServiceObjects.add(resultToUnlink);
			a.setLinked(false);
			nbPage.getAnalysisCache().deleteAnalysis(a);
		}
		refreshAnalyticalData();
		markFileAsLinked(sampleReference, listAnalyticalServiceObjects, false);
	}

	/**
	 * 
	 * @param message
	 */
	public boolean showJOptionMessage(String message) {
		JOptionPane.showMessageDialog(MasterController.getGUIComponent(), message, "Error", JOptionPane.ERROR_MESSAGE);
		return true;
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
}