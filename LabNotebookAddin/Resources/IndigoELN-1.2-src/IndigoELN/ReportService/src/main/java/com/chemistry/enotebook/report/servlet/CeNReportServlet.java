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
package com.chemistry.enotebook.report.servlet;

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.domain.ProcedureImage;
import com.chemistry.enotebook.report.beans.experiment.PrintableExperiment;
import com.chemistry.enotebook.report.beans.synthesis.SynthesisPlateReport;
import com.chemistry.enotebook.report.datamanager.ExperimentConverter;
import com.chemistry.enotebook.report.datamanager.ExperimentLoader;
import com.chemistry.enotebook.report.datamanager.TableOfContentsLoader;
import com.chemistry.enotebook.report.print.AttachmentsManager;
import com.chemistry.enotebook.report.print.PrintOptions;
import com.chemistry.enotebook.report.utils.PrintSetupConstants;
import com.chemistry.enotebook.report.utils.TextUtils;
import com.chemistry.enotebook.storage.ReactionPageInfo;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.report.engine.api.*;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class CeNReportServlet extends HttpServlet {

	private static final long serialVersionUID = -2567225004515960779L;

	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String SYNTHESIS = "synthesis";
	private static final String PARALLEL = "parallel";
	private static final String MEDCHEM = "medchem";
	private static final String CONCEPTION = "conception";
	private static final String ACCEPT_ENCODING = "Accept-Encoding";

	/**
	 * Constructor of the object.
	 */

	private static final Log log = LogFactory.getLog(CeNReportServlet.class);

	public CeNReportServlet() {
		super();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
        SLF4JBridgeHandler.install();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 *
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.debug("doGet()");
		
		if (CommonUtils.isNotNull(req.getParameter(PrintSetupConstants.PRINT_TABLE_OF_CONTENTS))) {
			printTableOfContents(req, resp);
			return;
		}
		
		List<ProcedureImage> procedureImages = null;
		
		try {
			resp.setContentType("text/html");

			Stopwatch stopwatch;
			
			// Get parameters from request
			String notebookNumber = req.getParameter(PrintSetupConstants.NOTEBOOK_NUMBER);
			// if (notebookNumber == null || notebookNumber.length() == 0)
			// throw new ServletException("notebookRefNumber not in params");
			if (notebookNumber == null || notebookNumber.length() == 0) {
				throw new Exception("Empty or null notebook number paremeters passed");
			}
			
			String pageNumber = req.getParameter(PrintSetupConstants.PAGE_NUMBER);
			if (pageNumber == null || pageNumber.length() == 0) {
				throw new Exception("Empty or null experiment/page number paremeter passed");
			}
			
	        String version = req.getParameter(PrintSetupConstants.PAGE_VERSION);
			// if (pageNumber == null || pageNumber.length() == 0)
			// throw new ServletException("pageNumber not in params");
			// default output is pdf
			String outputFormat = req.getParameter(PrintSetupConstants.OUTPUT_FORMAT);
	
			// These should come from the client so this is just temporary
			String reportName = req.getParameter(PrintSetupConstants.REPORT_NAME);
			
			PrintOptions.reportType reportType = getReportTypeFromDesignName(reportName);
	
			String timeZone = req.getParameter(PrintSetupConstants.TIME_ZONE);
			if (timeZone == null) {
				timeZone = TimeZone.getDefault().getID();
			}
	        
			String encodings = req.getHeader(ACCEPT_ENCODING);
			
			stopwatch = new Stopwatch();
			stopwatch.start("CeNReportServlet buildReportTask");
			IRunAndRenderTask task = buildReportTask(reportName);
			stopwatch.stop();
			
			ExperimentConverter converter = new ExperimentConverter();
			
			int numsteps = 0;
			String datasource = "";
			AttachmentsManager ap = null;

			stopwatch = new Stopwatch();
			stopwatch.start("CeNReportServlet loadExperiment");
			NotebookPageModel pageModel = new ExperimentLoader().loadExperiment(notebookNumber, pageNumber, version,
					new URL(req.getRequestURL().toString()).getHost());
			stopwatch.stop();
			
			procedureImages = pageModel.getPageHeader().getProcedureImages();
			
			// //////////////////////////////////////////////////////////////////////////////////
			// Convert notebook page model into datasource for report generation.
			// //////////////////////////////////////////////////////////////////////////////////
			boolean includeMonomerWells = true;
			boolean includeProductBatches = true;
			String includeNoMonomers = req.getParameter(PrintSetupConstants.INCLUDE_NO_MONOMERS);
			if (includeNoMonomers != null && includeNoMonomers.equalsIgnoreCase(TRUE))
				includeMonomerWells = false;
			String includeNoProducts = req.getParameter(PrintSetupConstants.INCLUDE_NO_PRODUCTS);
			if (includeNoProducts != null && includeNoProducts.equalsIgnoreCase(TRUE))
				includeProductBatches = false;

			numsteps = pageModel.getReactionSteps().size();
			if (numsteps >= 2) // Is this logic correct???????????????????
				numsteps = numsteps - 1;

			stopwatch = new Stopwatch();
			stopwatch.start("CeNReportServlet convertPageModelToPrintableExperiment");
            Map<String, Object> imageKeys = new HashMap<String, Object>();
            
			if (reportType == PrintOptions.reportType.CONCEPTION || reportType == PrintOptions.reportType.PARALLEL
					|| reportType == PrintOptions.reportType.SINGLETON) {
				numsteps = pageModel.getReactionSteps().size();
				if (numsteps >= 2) // Is this logic correct???????????????????
					numsteps = numsteps - 1;
				PrintableExperiment printableExp = converter.convertPageModelToPrintableExperiment(pageModel,
						includeMonomerWells, includeProductBatches, imageKeys, timeZone);
				datasource = printableExp.toXml();
				converter.dispose();
			} else {
				SynthesisPlateReport report = converter.extractSynthesisPlateReportFromPageModel(pageModel, imageKeys);
				datasource = report.toXml();
				converter.dispose();
			}
			stopwatch.stop();
			
			HashMap<String, Object> contextMap = contextPut(datasource);

            for (String key : imageKeys.keySet()) {
            	Object btar = imageKeys.get(key);
            	byte[] bytes = (byte[])btar; 
            	contextMap.put(key + ".jpg", bytes);
            }
						
			task.setAppContext(contextMap);
			
			// set parameters
			stopwatch = new Stopwatch();
			stopwatch.start("CeNReportServlet set and validate parameters");

			task.setParameterValue("printedDate", TextUtils.getTimeForTimeZone(new Date(), timeZone));
			task.setParameterValue("timeZone", timeZone);			
			
			try {
				setVisibilityParams(req, task, numsteps);
				task.validateParameters();
			} catch (RuntimeException e) {
				// add error handling display
				log.error("Failed to set and/or validate parameters.", e);
				throw new Exception("Failed to set and/or validate parameters.", e);
			}
			stopwatch.stop();
			
			// //////////////////////////////
			// get attachment parameters
			// //////////////////////////////
			boolean includeAttachments = this.getBooleanParameterFromString(req
					.getParameter(PrintSetupConstants.INCLUDE_ATTACHMENTS));
			boolean includeAnalytical = this.getBooleanParameterFromString(req
					.getParameter(PrintSetupConstants.INCLUDE_ANALYTICAL_SERVICE_FILES));
			boolean includeAllInstruments = this.getBooleanParameterFromString(req
					.getParameter(PrintSetupConstants.INCLUDE_ALL_ANALYTICAL_SERVICE_INSTRUMENTS));
			List<String> includedInstruments = this.getListFromCSVString(req
					.getParameter(PrintSetupConstants.INCLUDED_ANALYTICAL_SERVICE_INSTRUMENTS));
			// boolean includeAllInstruments = includeAnalytical;
			if (includedInstruments.size() > 0)
				includeAllInstruments = false;
			
			if (includeAttachments || includeAnalytical) {
				stopwatch = new Stopwatch();
				stopwatch.start("CeNReportServlet getting AttachmentsManager");
				ap = new AttachmentsManager(pageModel, reportType, includeAttachments, includeAnalytical,
						includeAllInstruments, includedInstruments);
				stopwatch.stop();
			}

			// run report
			ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			if (PrintSetupConstants.RTF.equalsIgnoreCase(outputFormat)) {
				// ///////////////
				// RTF case
				// ///////////////
				stopwatch = new Stopwatch();
				stopwatch.start("CeNReportServlet run the report");
				
				RenderOption options = new RenderOption();
				options.setOutputFormat(PrintSetupConstants.RTF);
				options.setOutputStream(os1);
				task.setRenderOption(options);
				task.run();
				task.close();
				stopwatch.stop();
				
				resp.setContentType("text/rtf");
			} else if (PrintSetupConstants.DOC.equalsIgnoreCase(outputFormat)) {
				// ///////////////
				// Word case
				// ///////////////
				stopwatch = new Stopwatch();
				stopwatch.start("CeNReportServlet run the report");
				
				PDFRenderOption options = new org.eclipse.birt.report.engine.api.PDFRenderOption();
				options.setEmbededFont(false);
				options.setOutputFormat(PrintSetupConstants.DOC);
				options.setOutputStream(os1);
				task.setRenderOption(options);
				task.run();
				task.close();
				stopwatch.stop();
				
				resp.setContentType("x-application/ms-word");
			} else {
				// /////////////
				// PDF case
				// /////////////
				stopwatch = new Stopwatch();
				stopwatch.start("CeNReportServlet run the report");
				
				PDFRenderOption options = new PDFRenderOption();
				options.setOutputFormat(PrintSetupConstants.PDF);
				options.setOutputStream(os1);
				task.setRenderOption(options);
				task.run();
				task.close();
				stopwatch.stop();
				stopwatch.stop();

				stopwatch = new Stopwatch();
				stopwatch.start("CeNReportServlet adding attachments");
				if (ap != null) {
					os1 = ap.addAttachments(os1);
				}
				stopwatch.stop();
				
				resp.setHeader("Content-Disposition", "inline; filename=\"test.pdf\"");
                //resp.setHeader("Error", error.toString());
				resp.setContentType("application/pdf");
			}

			stopwatch = new Stopwatch();
			stopwatch.start("CeNReportServlet getting output bytes");
			byte[] bytes = os1.toByteArray();
			stopwatch.stop();
			
			stopwatch = new Stopwatch();
			stopwatch.start("CeNReportServlet writing to output stream " + bytes.length + " bytes.");

			OutputStream outputStream;// = resp.getOutputStream();			
			if(encodings != null && (encodings.indexOf("gzip") != -1)) {
				resp.setHeader("Content-Encoding", "gzip");
				outputStream = new GZIPOutputStream(resp.getOutputStream());
			} else {
				outputStream = resp.getOutputStream();
			}
			outputStream.write(bytes);
			outputStream.flush();
			outputStream.close();
			stopwatch.stop();

			log.debug("doGet().Request processed.");
		} catch (Exception e) {
			log.error("Possible rendering failure", e);
			resp.setHeader("Error", e.getMessage());
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		removeProcedureImageFiles(procedureImages);
	}
	
	private void removeProcedureImageFiles(List<ProcedureImage> procedureImages) {
		if (procedureImages != null) {
			String tempDir = System.getProperty("java.io.tmpdir");
			for (ProcedureImage procedureImage : procedureImages) {
				String path = tempDir + File.separator + procedureImage.getKey() + "." + procedureImage.getImageType();
				File f = new File(path);
				if (f.exists())
					f.delete();
			}
		}
	}
	
	private void printTableOfContents(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
		try {
			log.debug("printTableOfContents()");
			
			String siteCode = req.getParameter(PrintSetupConstants.SITE_CODE);
			if("null".equalsIgnoreCase(siteCode))siteCode = null;
			
			String notebookNumber = req.getParameter(PrintSetupConstants.NOTEBOOK_NUMBER);
			if (CommonUtils.isNull(notebookNumber) || notebookNumber.equalsIgnoreCase("null")) {
				throw new Exception("Empty or null notebook number paremeters passed");
			}
			
			int startPageNumber;
			String param = req.getParameter(PrintSetupConstants.START_PAGE_NUMBER);
			if (CommonUtils.isNotNull(param) && !param.equalsIgnoreCase("null")) {
				startPageNumber = Integer.parseInt(param);
			} else {
				throw new Exception("Empty or null experiment/page number paremeter passed");
			}
			
			int endPageNumber;
			param = req.getParameter(PrintSetupConstants.END_PAGE_NUMBER);
			if (CommonUtils.isNotNull(param) && !param.equalsIgnoreCase("null")) {
				endPageNumber = Integer.parseInt(param);
			} else {
				throw new Exception("Empty or null experiment/page number paremeter passed");
			}
			
			String fileName = req.getParameter(PrintSetupConstants.FILE_NAME);
			String reportName = req.getParameter(PrintSetupConstants.REPORT_NAME);		
			
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start("CeNReportServlet tableOfContents buildReportTask");
			final IRunAndRenderTask task = buildReportTask(reportName);
			stopwatch.stop();
			
			TableOfContentsLoader contentsLoader = new TableOfContentsLoader();
			
			List<ReactionPageInfo> pagesList = contentsLoader.loadTableOfContentsInfo(siteCode, notebookNumber, startPageNumber, endPageNumber);
			
			log.debug("tableOfContents - model loaded");

			ExperimentConverter converter = new ExperimentConverter();
			
			Map<String, Object> imageKeys = new HashMap<String, Object>();
			
			String datasource = converter.convertNotebookContentsTableModelToPrintableXMLAndConstructImageUrlKeys(imageKeys, pagesList, notebookNumber, startPageNumber, endPageNumber);
			HashMap<String, Object> contextMap = contextPut(datasource);

            for (String key : imageKeys.keySet()) {
            	Object btar = imageKeys.get(key);
            	byte[] bytes = (byte[])btar; 
            	contextMap.put(key + ".jpg", bytes);
            }
            
			task.setAppContext(contextMap);
			task.validateParameters();
			stopwatch = new Stopwatch();
			stopwatch.start("CeNReportServlet tableOfContents run the report");
			// run report
			String outputFormat = req.getParameter(PrintSetupConstants.OUTPUT_FORMAT);
			ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			
			PDFRenderOption options = new PDFRenderOption();
			if (PrintSetupConstants.RTF.equalsIgnoreCase(outputFormat) || PrintSetupConstants.DOC.equalsIgnoreCase(outputFormat)) {				
				options.setEmbededFont(false);
				options.setOutputFormat(PrintSetupConstants.DOC);				
				resp.setContentType("x-application/ms-word");
				resp.setHeader("Content-Disposition", "inline; filename=\"" + fileName + ".doc\"");
			} else {
				options.setOutputFormat(PrintSetupConstants.PDF);
				resp.setContentType("application/pdf");
				resp.setHeader("Content-Disposition", "inline; filename=\"" + fileName + ".pdf\"");
			}
			options.setOutputStream(os1);
			task.setRenderOption(options);
			task.run();
			task.close();
			stopwatch.stop();
			
			log.debug("tableOfContents - bytes constructed");
			
			byte[] bytes = os1.toByteArray();
			OutputStream outputStream;
			
			String encodings = req.getHeader(ACCEPT_ENCODING);
			if(encodings != null && (encodings.indexOf("gzip") != -1)) {
				resp.setHeader("Content-Encoding", "gzip");
				outputStream = new GZIPOutputStream(resp.getOutputStream());
			} else {
				outputStream = resp.getOutputStream();
			}

			outputStream.write(bytes);
			outputStream.flush();
			outputStream.close();
			
			log.debug("tableOfContents - bytes sent to output stream");
			
		} catch (Exception e) {
			log.error("Possible rendering failure", e);
			resp.setHeader("Error", e.getMessage());
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	private IRunAndRenderTask buildReportTask(String reportName) throws Exception {
		// //////////////////////////////////////////
		// create task to run and render report
		// //////////////////////////////////////////
		ServletContext context = getServletContext();
		IReportEngine engine = (IReportEngine) context.getAttribute("birtEngine");
		if (engine == null) {
			throw new ServletException("ReportEngine not initialized");
		}
		
		IReportRunnable design = loadDesign(reportName);
		
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("CeNReportServlet birtReportEngine.createRunAndRenderTask");
		IRunAndRenderTask task = engine.createRunAndRenderTask(design);
		stopwatch.stop();
		return task;
	}

	private IReportRunnable loadDesign(String reportName) throws Exception {
		// //////////////////////////////////////////////////////////////
		// Initialize BIRT
		// //////////////////////////////////////////////////////////////
		ServletContext context = getServletContext();
		IReportEngine engine = (IReportEngine) context.getAttribute("birtEngine");
		if (engine == null) {
			throw new ServletException("ReportEngine not initialized");
		}

		IReportRunnable design = (IReportRunnable)context.getAttribute(reportName);
		if (design == null) {
			// ///////////////////////////////////////
			// Open report design with getResource
			// ///////////////////////////////////////
			String filepath = "/reports/" + reportName;
			InputStream is = CeNReportServlet.class.getResourceAsStream(filepath);
	
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start("CeNReportServlet birtReportEngine.openReportDesign " + filepath);
			try {
				design = engine.openReportDesign(is);
				//optimization fix
				context.setAttribute(reportName, design);
			} catch (EngineException e1) {
				log.warn("Failed opening Report Designer. Will make second attempt.", e1);
				try {
					// for exploded deployment in testing
					design = engine.openReportDesign(context.getRealPath("/reports") + "/" + reportName);
				} catch (EngineException e2) {
					log.error("Failed opening a Report Design.", e2);
					throw new ServletException(e1);
				}
			}
			stopwatch.stop();
		}
		return design;
	}
	
	private HashMap<String, Object> contextPut(String datasource) {
		HashMap<String, Object> contextMap = new HashMap<String, Object>();
		HTMLRenderOption renderOption = new HTMLRenderOption();
		contextMap.put(EngineConstants.APPCONTEXT_HTML_RENDER_CONTEXT, renderOption);
		InputStream istream;
		try {
			istream = new ByteArrayInputStream(datasource.getBytes("UTF8"));
		} catch (UnsupportedEncodingException e) {
			istream = new ByteArrayInputStream(datasource.getBytes());
		}
		contextMap.put("org.eclipse.datatools.enablement.oda.xml.inputStream", istream);
		return contextMap;
	}

	/**
	 * Set the BIRT show/hide params from the request parameters.
	 *
	 * @param req
	 * @param task
	 * @param numsteps
	 * @throws Exception
	 */
	private void setVisibilityParams(HttpServletRequest req, IRunAndRenderTask task, int numsteps) throws Exception {
		String designName = req.getParameter(PrintSetupConstants.REPORT_NAME);
		// Common sections
		String includeProcedure = req.getParameter(PrintSetupConstants.INCLUDE_PROCEDURE);
		if (includeProcedure != null && includeProcedure.equalsIgnoreCase(FALSE)) {
			task.setParameterValue("hideProcedure", new Boolean(true));
        }

        setHideParameter(task, req, "hideBatches", "noProducts", TRUE);
        setHideParameter(task, req, "hideRegistrationSummary", PrintSetupConstants.INCLUDE_REG_SUMMARY, FALSE);
        setHideParameter(task, req, "hideRegistrationDetails", PrintSetupConstants.INCLUDE_REG_DETAILS, FALSE);
        setHideParameter(task, req, "hideSubmissionDetails", PrintSetupConstants.INCLUDE_SUBMISSION_DETAILS, FALSE);
        setHideParameter(task, req, "hideHazards", PrintSetupConstants.INCLUDE_HAZARDS, FALSE);

		// String includeAttachments = req.getParameter(PrintSetupConstants.INCLUDE_ATTACHMENTS);
		// if (includeAttachments != null && includeAttachments.equalsIgnoreCase(FALSE)) {
		// task.setParameterValue("hideAttachments", new Boolean(true));
		// }
		String includeFooter = req.getParameter(PrintSetupConstants.INCLUDE_FOOTER);
		if (includeFooter != null && includeFooter.equalsIgnoreCase(FALSE)) {
			task.setParameterValue("hideFooter", new Boolean(true));
		}
		String includeDetails = req.getParameter(PrintSetupConstants.INCLUDE_DETAILS);
		if (includeDetails != null && includeDetails.equalsIgnoreCase(FALSE)) {
			task.setParameterValue("hideDetails", new Boolean(true));
		}
		String includeAnalyticalSummary = req.getParameter(PrintSetupConstants.INCLUDE_ANALYTICAL_SUMMARY);
		if (includeAnalyticalSummary != null && includeAnalyticalSummary.equalsIgnoreCase(FALSE)) {
			task.setParameterValue("hideAnalyticalSummary", new Boolean(true));
		}
		if (designName.indexOf("synthesis") >= 0) { // make constant
			String showAllSteps = req.getParameter(PrintSetupConstants.INCLUDE_SYNTHESIS_PLATES_FOR_ALL_STEPS);
			if (showAllSteps == null || showAllSteps.equalsIgnoreCase(FALSE)) {
				// Handle show/hide steps preferences
				String stepsSelected = req.getParameter(PrintSetupConstants.INCLUDE_SYNTHESIS_PLATES_STEPS);
				// Hide some steps
				if (stepsSelected != null && stepsSelected.equalsIgnoreCase(TRUE)) {
					String includedSteps = req.getParameter(PrintSetupConstants.INCLUDED_SYNTHESIS_PLATES_STEPS);
					if (includedSteps != null && includedSteps.length() > 0) {
						String hideTheseSteps = this.getStepsToHide(includedSteps, numsteps);
						if (hideTheseSteps != null && hideTheseSteps.length() > 0)
							task.setParameterValue("hideSteps", this.getStepsToHide(includedSteps, numsteps));
					}
				} else {
					String showOnlyFinalStep = req.getParameter(PrintSetupConstants.INCLUDE_FINAL_STEP_SYNTHESIS_PLATES);
					if (showOnlyFinalStep != null && showOnlyFinalStep.equalsIgnoreCase(TRUE)) {
						task.setParameterValue("hideSteps", this.getStepsToHide("" + numsteps, numsteps));
					}
				}
			}
		}

        task.setParameterValue("hideReaction",
                TRUE.equalsIgnoreCase(req.getParameter(PrintSetupConstants.INCLUDE_NO_REACTIONS)) ||
                FALSE.equalsIgnoreCase(req.getParameter(PrintSetupConstants.INCLUDE_REACTION)));
        task.setParameterValue("hideStoic", TRUE.equalsIgnoreCase(req.getParameter(PrintSetupConstants.INCLUDE_NO_STOIC)));

        // Conception page sections
		if (designName.indexOf(PrintSetupConstants.CONCEPTION) >= 0) {
			String includeCompounds = req.getParameter(PrintSetupConstants.INCLUDE_COMPOUNDS);
			if (includeCompounds != null && includeCompounds.equalsIgnoreCase(FALSE)) {
				task.setParameterValue("hideCompounds", new Boolean(true));
			}
		}
		// Experiment page sections
		if (designName.indexOf(PrintSetupConstants.PARALLEL) >= 0) {
			// Check if there should be no reactions printed
			String includeNoReactions = req.getParameter(PrintSetupConstants.INCLUDE_NO_REACTIONS);
			if (includeNoReactions != null && includeNoReactions.equalsIgnoreCase(TRUE)) {
				// Exclude summary reaction
				task.setParameterValue("hideSummaryReaction", new Boolean(true));
				// Exclude all other reactions
				task.setParameterValue("hideReactionSteps", this.getStepsToHide("0", numsteps));
			} else { // Handle which reactions to print
				String includeSummaryReaction = req.getParameter(PrintSetupConstants.INCLUDE_SUMMARY_REACTION);
				if (includeSummaryReaction != null && includeSummaryReaction.equalsIgnoreCase(FALSE)) {
					task.setParameterValue("hideSummaryReaction", new Boolean(true));
				}
				// Handle show/hide reaction steps preferences
				String stepsSelected = req.getParameter(PrintSetupConstants.INCLUDE_REACTION_STEPS);
				// Hide some steps
				if (stepsSelected != null && stepsSelected.equalsIgnoreCase(TRUE)) {
					String includedReactionSteps = req.getParameter(PrintSetupConstants.INCLUDED_REACTION_STEPS);
					if (includedReactionSteps != null && includedReactionSteps.length() > 0) {
						task.setParameterValue("hideReactionSteps", this.getStepsToHide(includedReactionSteps, numsteps));
					}
				} else {
					String showOnlyFinalStepReaction = req.getParameter(PrintSetupConstants.INCLUDE_FINAL_STEP_REACTION);
					if (showOnlyFinalStepReaction != null && showOnlyFinalStepReaction.equalsIgnoreCase(TRUE)) {
						task.setParameterValue("hideReactionSteps", this.getStepsToHide("" + numsteps, numsteps));
					}
				}
			}
			// Stoic table show/hide preference
			// Check if there should be no stoic tables printed

			String includeNoStoic = req.getParameter(PrintSetupConstants.INCLUDE_NO_STOIC);
			if (includeNoStoic != null && includeNoStoic.equalsIgnoreCase(TRUE)) {
				// Exclude all stoic tables
				task.setParameterValue("hideStoicSteps", this.getStepsToHide("0", numsteps));
			} else { // Handle which stoic tables to print
				String showAllSteps = req.getParameter(PrintSetupConstants.INCLUDE_STOIC_FOR_ALL_STEPS);
				if (showAllSteps == null || showAllSteps.equalsIgnoreCase(FALSE)) {
					// Handle show/hide stoic table for steps preferences
					String stepsSelected = req.getParameter(PrintSetupConstants.INCLUDE_STOIC_STEPS);
					// Hide some steps
					if (stepsSelected != null && stepsSelected.equalsIgnoreCase(TRUE)) {
						String includedStoicSteps = req.getParameter(PrintSetupConstants.INCLUDED_STOIC_STEPS);
						if (includedStoicSteps != null && includedStoicSteps.length() > 0) {
							task.setParameterValue("hideStoicSteps", this.getStepsToHide(includedStoicSteps, numsteps));
						}
					} else {
						String showOnlyFinalStepStoic = req.getParameter(PrintSetupConstants.INCLUDE_FINAL_STEP_STOIC);
						if (showOnlyFinalStepStoic != null && showOnlyFinalStepStoic.equalsIgnoreCase(TRUE)) {
							task.setParameterValue("hideStoicSteps", this.getStepsToHide("" + numsteps, numsteps));
						}
					}
				}
			}
			// Monomer batches show/hide preference
			// Check if there should be no monomer batches printed
			String includeNoMonomers = req.getParameter(PrintSetupConstants.INCLUDE_NO_MONOMERS);
			if (includeNoMonomers != null && includeNoMonomers.equalsIgnoreCase(TRUE)) {
				// Exclude all monomer batches
				task.setParameterValue("hideMonomerSteps", this.getStepsToHide("0", numsteps));
			} else { // Handle which monomer batches to print
				String showAllSteps = req.getParameter(PrintSetupConstants.INCLUDE_MONOMERS_FOR_ALL_STEPS);
				if (showAllSteps == null || showAllSteps.equalsIgnoreCase(FALSE)) {
					// Handle show/hide monomer batches for steps preferences
					String stepsSelected = req.getParameter(PrintSetupConstants.INCLUDE_MONOMERS_STEPS);
					// Hide some steps
					if (stepsSelected != null && stepsSelected.equalsIgnoreCase(TRUE)) {
						String includedMonomersSteps = req.getParameter(PrintSetupConstants.INCLUDED_MONOMERS_STEPS);
						if (includedMonomersSteps != null && includedMonomersSteps.length() > 0) {
							task.setParameterValue("hideMonomerSteps", this.getStepsToHide(includedMonomersSteps, numsteps));
						}
					} else {
						String showOnlyFinalStepMonomers = req.getParameter(PrintSetupConstants.INCLUDE_FINAL_STEP_MONOMERS);
						if (showOnlyFinalStepMonomers != null && showOnlyFinalStepMonomers.equalsIgnoreCase(TRUE)) {
							if (numsteps > 1)
								task.setParameterValue("hideMonomerSteps", this.getStepsToHide("" + numsteps, numsteps));
						}
					}
				}
			}
			// Product batches show/hide preference
			// Check if there should be no product batches printed
			String includeNoProducts = req.getParameter(PrintSetupConstants.INCLUDE_NO_PRODUCTS);
			if (includeNoProducts != null && includeNoProducts.equalsIgnoreCase(TRUE)) {
				// Exclude all product batches
				task.setParameterValue("hideProductSteps", this.getStepsToHide("0", numsteps));
			} else { // Handle which product batches to print
				String showAllSteps = req.getParameter(PrintSetupConstants.INCLUDE_PRODUCTS_FOR_ALL_STEPS);
				if (showAllSteps == null || showAllSteps.equalsIgnoreCase(FALSE)) {
					// Handle show/hide product batches for steps preferences
					String stepsSelected = req.getParameter(PrintSetupConstants.INCLUDE_PRODUCTS_STEPS);
					// Hide some steps
					if (stepsSelected != null && stepsSelected.equalsIgnoreCase(TRUE)) {
						String includedProductsSteps = req.getParameter(PrintSetupConstants.INCLUDED_PRODUCTS_STEPS);
						if (includedProductsSteps != null && includedProductsSteps.length() > 0) {
							task.setParameterValue("hideProductSteps", this.getStepsToHide(includedProductsSteps, numsteps));
						}
					} else {
						String showOnlyFinalStepProducts = req.getParameter(PrintSetupConstants.INCLUDE_FINAL_STEP_PRODUCTS);
						if (showOnlyFinalStepProducts != null && showOnlyFinalStepProducts.equalsIgnoreCase(TRUE)) {
							task.setParameterValue("hideProductSteps", this.getStepsToHide("" + numsteps, numsteps));
						}
					}
				}
			}
			// Product batches show/hide preference
			// Check if there should be no product batches printed
            /*
			 * else { // Handle which product batches to print String showAllSteps =
			 * req.getParameter(PrintSetupConstants.INCLUDE_PRODUCTS_FOR_ALL_STEPS); if (showAllSteps == null ||
			 * showAllSteps.equalsIgnoreCase(FALSE)) { // Handle show/hide product batches for steps preferences String
			 * stepsSelected = req.getParameter(PrintSetupConstants.INCLUDE_PRODUCTS_STEPS); // Hide some steps if (stepsSelected !=
			 * null && stepsSelected.equalsIgnoreCase(TRUE)) { String includedProductsSteps =
			 * req.getParameter(PrintSetupConstants.INCLUDED_PRODUCTS_STEPS); if (includedProductsSteps != null &&
			 * includedProductsSteps.length() > 0) { task.setParameterValue("hideProductSteps",
			 * this.getStepsToHide(includedProductsSteps, numsteps)); } } else { String showOnlyFinalStepProducts =
			 * req.getParameter(PrintSetupConstants.INCLUDE_FINAL_STEP_PRODUCTS); if (showOnlyFinalStepProducts != null &&
			 * showOnlyFinalStepProducts.equalsIgnoreCase(TRUE)) { task.setParameterValue("hideProductSteps", this.getStepsToHide(""
			 * + numsteps, numsteps)); } } } }
			 */
		}
	}

    private void setHideParameter(IRunAndRenderTask task, HttpServletRequest req, String hideParameterName, String requestParameter, String expectedForDoHide) {
        task.setParameterValue(hideParameterName, expectedForDoHide.equalsIgnoreCase(req.getParameter(requestParameter)));
    }

	/**
	 * This method takes the list of steps to include and the total number of steps and creates a list of steps to exclude. If the
	 * includedReactionsSteps is "0", all reaction steps are in the returned exclude list.
	 *
	 * @param includedReactionSteps
	 * @param numsteps
	 * @return
	 */
	private String getStepsToHide(String includedReactionSteps, int numsteps) {
		List<String> allsteps = new ArrayList<String>();
		for (int i = 0; i < numsteps; i++) {
			allsteps.add("" + (i + 1));
		}
		StringBuffer excludedSteps = new StringBuffer();
		// handle case where no steps are included
		if (includedReactionSteps.equals("0")) {
			for (String step : allsteps) {
				excludedSteps.append(step).append(",");
			}
		} else { // there is a list of steps to include
			String[] incstepsArray = includedReactionSteps.split(PrintSetupConstants.STEPS_DELIMITER);
			List<String> incsteps = Arrays.asList(incstepsArray);
			for (String step : allsteps) {
				if (incsteps.contains(step))
					continue;
				else
					excludedSteps.append(step).append(",");
			}
		}
		if (excludedSteps.length() > 1)
			excludedSteps.deleteCharAt(excludedSteps.length() - 1);
		return excludedSteps.toString();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	private PrintOptions.reportType getReportTypeFromDesignName(String designName) {
		if (designName.indexOf(SYNTHESIS) >= 0)
			return PrintOptions.reportType.SYNTHESIS;
		if (designName.indexOf(PARALLEL) >= 0)
			return PrintOptions.reportType.PARALLEL;
		if (designName.indexOf(CONCEPTION) >= 0)
			return PrintOptions.reportType.CONCEPTION;
		if (designName.indexOf(MEDCHEM) >= 0)
			return PrintOptions.reportType.SINGLETON;
		return null;
	}

	private boolean getBooleanParameterFromString(String param) {
		if (param == null)
			return false;
		if (param.equalsIgnoreCase("true"))
			return true;
		return false;
	}

	private List<String> getListFromCSVString(String csvString) {
		List<String> list = new ArrayList<String>();
		
		if (csvString != null && csvString.length() > 0) {
			String[] items = csvString.split(",");
			for (int i = 0; i < items.length; i++)
				list.add(items[i]);
		}
		
		return list;
	}
}
