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

package com.chemistry.enotebook.storage.ejb;

import com.chemistry.enotebook.domain.*;
import com.chemistry.enotebook.domain.container.Container;
import com.chemistry.enotebook.esig.delegate.SignatureDelegate;
import com.chemistry.enotebook.experiment.datamodel.page.InvalidNotebookRefException;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.experiment.utils.SystemPropertyException;
import com.chemistry.enotebook.searchquery.SearchQueryParams;
import com.chemistry.enotebook.servicelocator.PropertyException;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.signature.classes.TemplateVO;
import com.chemistry.enotebook.storage.*;
import com.chemistry.enotebook.storage.dao.*;
import com.chemistry.enotebook.storage.exceptions.SearchTooMuchDataException;
import com.chemistry.enotebook.storage.exceptions.StorageTokenInvalidException;
import com.chemistry.enotebook.storage.interfaces.StorageRemote;
import com.chemistry.enotebook.storage.service.ContainerServiceImpl;
import com.chemistry.enotebook.storage.service.StorageServiceImpl;
import com.chemistry.enotebook.storage.utils.print.*;
import com.chemistry.enotebook.util.ExceptionUtils;
import com.chemistry.enotebook.util.Stopwatch;
import com.chemistry.enotebook.utils.CommonUtils;
import com.chemistry.enotebook.utils.CompositeCompressedBytes;
import com.chemistry.enotebook.utils.SerializeObjectCompression;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StorageEJB implements StorageRemote {
	private static final Log log = LogFactory.getLog(StorageEJB.class);

	/**
	 * An example business method
	 * 
	 * @param context
	 * @param sessionID
	 */
	public StorageContextInterface retrieveData(StorageContextInterface context, SessionIdentifier sessionID) throws StorageException,
			StorageException, StorageTokenInvalidException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageEJB.retrieveData");

		ContextHandlerInterface hndlr = null;
		StorageContextInterface returnObj = null;

		// boolean isValidSession = false;
		// try {
		// SessionLocalDelegate sd = new SessionLocalDelegate();
		// isValidSession = sd.validateUser(sessionID);
		// } catch (Exception e) {
		// throw new StorageTokenInvalidException("StorageRemote:retrieveData:: Error handling Session Token.\nMessage: " +
		// ExceptionUtils.getRootCause(e));
		// }
		// if (!isValidSession) throw new StorageTokenInvalidException("StorageRemote:retrieveData:: Invalid Session Token");

		try {
			// Create a handler that specific for the context specified
			hndlr = ContextHandlerFactory.createHandler(context);

			// Begin processing this request
			if (hndlr != null) {
				hndlr.beginQueryProcess();

				returnObj = hndlr.getContext();
			}
		} catch (Exception e) {
			hndlr = null;
			// e.printStackTrace();
			throw new StorageException("StorageRemote:retrieveData:: Failure at Handler level.\nMessage: "
					+ ExceptionUtils.getRootCause(e));
		}

		stopwatch.stop();
		return returnObj;
	}

	/**
	 * An example business method
	 * 
	 * @param context
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public StorageContextInterface localRetrieveData(StorageContextInterface context) throws StorageException, StorageException {
		ContextHandlerInterface hndlr = null;
		StorageContextInterface returnObj = null;

		try {
			// Create a handler that specific for the context specified
			hndlr = ContextHandlerFactory.createHandler(context);
			// Begin processing this request
			if (hndlr != null) {
				hndlr.beginQueryProcess();
				returnObj = hndlr.getContext();
			}
		} catch (Exception e) {
			hndlr = null;
			e.printStackTrace();
			throw new StorageException("StorageRemote:localRetrieveData:: Failure at Handler level.\nMessage: "
					+ ExceptionUtils.getRootCause(e));
		}
		return returnObj;
	}

	/**
	 * An example business method
	 * 
	 * @param siteCode
	 * @param notebook
	 * @param experiment
	 * @param pageVersion
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public ValidationInfo validateNotebook(String siteCode, String notebook, String experiment, String pageVersion) throws StorageException, StorageException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageEJB.validateNotebook");

		ValidationInfo result = null;
		try {
			result = new ValidationDAO().validateNotebook(siteCode, notebook, experiment, pageVersion);
		} catch (Exception e) {
			e.printStackTrace();
			throw new StorageException("StorageRemote:validateNotebook:: Failure.\nMessage: " + ExceptionUtils.getRootCause(e));
		}

		stopwatch.stop();
		return result;
	}

	public ValidationInfo getNotebookInfo(String siteCode, String notebook) throws StorageException {
		try {
			return new ValidationDAO().validateNotebook(siteCode, notebook, null);
		} catch (Exception e) {
			log.error("Error getting notebook info: ", e);
			throw new StorageException(e);
		}
	}
	
	/**
	 * An example business method
	 * 
	 * @param siteCode
	 * @param notebook
	 * @param experiment
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public ValidationInfo validateNotebook(String siteCode, String notebook, String experiment) throws StorageException {
		return validateNotebook(siteCode, notebook, experiment, null);
	}

	/**
	 * An example business method
	 * 
	 * @param notebook
	 * @param sessionID
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public void createNotebook(String notebook, SessionIdentifier sessionID) 
			throws StorageException, NotebookInvalidException, NotebookExistsException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageEJB.createNotebook, notebook = " + notebook);

		ValidationInfo vi = null;

		String fmtNotebook = NotebookPageUtil.formatNotebookNumber(notebook);
		if ((fmtNotebook == null) || (fmtNotebook.length() <= 0)) {
			throw new NotebookInvalidException();
		}

		// verify notebook is unique accross site
		try {
			vi = this.validateNotebook(null, fmtNotebook, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new StorageException("StorageRemote:createNotebook:: Failure.\nMessage" + e.getMessage());
		}
		if (vi != null) {
			throw new NotebookExistsException(); // notebook already exists!
		}

		// create notebook in cen_notebooks
		try {
			DAOFactoryManager.getDAOFactory().getNotebookDAO().createNotebook(sessionID.getSiteCode(), sessionID.getUserID(), fmtNotebook, sessionID.getUserID());
		} catch (Exception e) {
			e.printStackTrace();
			throw new StorageException("StorageRemote:createNotebook:: Failure.\nMessage: " + ExceptionUtils.getRootCause(e));
		}

		stopwatch.stop();
	}

	/**
	 * An example business method
	 * 
	 * @param notebook
	 * @param sessionID
	 * @throws StorageException
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public void createNotebook(String siteCode, String userId, String notebook, SessionIdentifier sessionID) throws StorageException, StorageException, NotebookInvalidException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageEJB.createNotebook - " + siteCode + ", " + userId + ", " + notebook);

		ValidationInfo vi = null;

		// if (!userId.equals(sessionID.getUserID()) && !sessionID.isSuperUser()) {
		// throw new StorageException("User not authorized for this action.");
		// }

		String fmtNotebook = NotebookPageUtil.formatNotebookNumber(notebook);
		if ((fmtNotebook == null) || (fmtNotebook.length() <= 0)) {
			throw new NotebookInvalidException();
		}

		// verify notebook is unique accross site
		try {
			vi = this.validateNotebook(siteCode, fmtNotebook, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new StorageException("StorageRemote:createNotebook:: Failure" + e.getMessage());
		}
		if (vi != null) {
			// throw new NotebookExistsException(); // notebook already exists!
			// just return doing nothing further. No need to throw exception .
			// This will be used to add Notebooks comming from ExternalCollaborator.
			return;
		}

		// create notebook in cen_notebooks
		try {
			DAOFactoryManager.getDAOFactory().getNotebookDAO().createNotebook(siteCode, userId, fmtNotebook, userId);
			log.debug("New notebook created successfully: " + fmtNotebook + " for " + userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new StorageException("StorageRemote:createNotebook:: Failure" + ExceptionUtils.getRootCause(e));
		}

		stopwatch.stop();
	}

	/**
	 * An example business method
	 * 
	 * @param siteCode
	 * @param notebook
	 * @param experiment
	 * @param sessionID
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 * @throws StorageException
	 *             Thrown if the instance could not perform the function requested due to a software related error.
	 * @throws InvalidNotebookRefException
	 *             Thrown notebook or experiment are not formatted properly or not found.
	 */
	public boolean deleteExperiment(String siteCode, String notebook, String experiment, int version, SessionIdentifier sessionID)
			throws StorageException, StorageException, InvalidNotebookRefException {
		boolean status = false;

		if (!sessionID.isSuperUser()) {
			throw new StorageException("User not authorized for this action.");
		}

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageEJB.deleteExperiment - " + siteCode + ", " + notebook + ", " + experiment + ", " + version);

		// verify notebook / experiment are valid
		NotebookRef nbRef = new NotebookRef(notebook, experiment);
		ValidationInfo vi = null;
		try {
			vi = this.validateNotebook(siteCode, notebook, experiment);
		} catch (Exception e) {
			log.error("Notebook failed validation: site code: " + siteCode + ", notebook: " + notebook + ", experiment: " + experiment + ", version: " + version);
			throw new InvalidNotebookRefException("StorageRemote:deleteExperiment:: Failure" + e.getMessage());
		}
		if (vi == null) {
			throw new InvalidNotebookRefException("StorageRemote:deleteExperiment:: Experiment does not exist.");
		}

		// Delete experiment
		try {
			DAOFactoryManager.getDAOFactory().getNotebookDAO().deleteExperiment(siteCode, notebook, experiment, version);
			status = true;
		} catch (Exception e) {
			log.error("Failed to delete experiment! site code: " + siteCode + ", notebook: " + notebook + ", experiment: " + experiment + ", version: " + version);
			throw new StorageException("StorageRemote:deleteExperiment:: Failure" + ExceptionUtils.getRootCause(e));
		}

		stopwatch.stop();
		return status;
	}

	/**
	 * An example business method
	 * 
	 * @param params
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public CompositeCompressedBytes searchReactionInfo(SearchQueryParams params, boolean bool) throws StorageException, StorageException, SearchTooMuchDataException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageEJB.searchReactionInfo");
		log.debug("StorageEJB.searchReactionInfo");
		
		DAOFactory daoFactory = null;
		List<NotebookPageModel> result = new ArrayList<NotebookPageModel>();		
		
		try {
			List<String> nbkPages = new ArrayList<String>();			
			
			daoFactory = DAOFactoryManager.getDAOFactory(); 
			StructureSearchDAO structureSearchDAO = daoFactory.getStructureSearchDAO();
			if (structureSearchDAO == null) {
				DAOFactoryManager.release(daoFactory);
				throw new StorageException("Unable to load StructureSearchDAO object from storage-dao.xml!");
			}
			
			byte[] chemistry = params.getQueryChemistry();
			
			if (chemistry != null && chemistry.length > 0) {
				String structure = new String(chemistry);
				boolean searchInProducts = params.getCssCenDbSearchType().equals("Products");
												
				List<String> nbkRefs = structureSearchDAO.searchByStructure(structure, searchInProducts, params.getStructureSearchType(), params.getStrucSimilarity());
				
				if (nbkRefs == null || nbkRefs.size() == 0)
					return SerializeObjectCompression.serializeToCompressedBytes(result);
				
				for (String ref : nbkRefs) {
					if (!nbkPages.contains(ref))
						nbkPages.add(ref);
				}
			}			
			
			daoFactory = DAOFactoryManager.getDAOFactory(); 
			NotebookSearchDAO searchDAO = daoFactory.getNotebookSearchDAO();
			if (searchDAO == null) {
				DAOFactoryManager.release(daoFactory);
				throw new StorageException("Unable to load NotebookSearchDAO object from storage-dao.xml!");
			}
			
			result = searchDAO.getSearchedNotebookPages(params, nbkPages);
		} catch (SearchTooMuchDataException e) {
			throw new SearchTooMuchDataException(e.toString());
		} catch (Exception e) {
			log.error(e);
			log.error(ExceptionUtils.getStackTrace(e));
			throw new StorageException(ExceptionUtils.getRootCause(e).getMessage());
		} finally {
			if (daoFactory != null) {
				DAOFactoryManager.release(daoFactory);
			}
		}

		stopwatch.stop();
		return SerializeObjectCompression.serializeToCompressedBytes(result);
	}
	
	/**
	 * An example business method
	 * 
	 * @param siteCode
	 * @param notebook
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public int getNextExperimentForNotebook(String notebook) throws StorageException, StorageException, NotebookInvalidException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageEJB.getNextExperimentForNotebook");
		DAOFactory daoFact = null;
		int result = -1;
		try {
			daoFact = DAOFactoryManager.getDAOFactory();
			NotebookDAO nkbDao = daoFact.getNotebookDAO();
			result = nkbDao.getNextExperimentForNotebook(notebook);
		} catch (DAOException e) {
			e.printStackTrace();
			throw new StorageException("StorageRemote:getNextExperimentForNotebook:: Failure.\nMessage: "
					+ ExceptionUtils.getRootCause(e) + " at " + (new java.util.Date(System.currentTimeMillis())));
		} finally {
			if (daoFact != null) {
				DAOFactoryManager.release(daoFact);
			}
		}

		stopwatch.stop();
		return result;
	}

	/**
	 * A business method to get System Properties for a given site
	 * 
	 * @param userIDs
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public String[] getUsersFullName(String[] userIDs) throws StorageException, StorageException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageEJB.getUsersFullName");

		DAOFactory daoFact = null;
		String[] result = null;
		try {
			daoFact = DAOFactoryManager.getDAOFactory();
			NotebookDAO nkbDao = daoFact.getNotebookDAO();
			result = nkbDao.getUsersFullName(userIDs);
		} catch (DAOException e) {
			e.printStackTrace();
			throw new StorageException(e);
		} finally {
			if (daoFact != null) {
				DAOFactoryManager.release(daoFact);
			}
		}

		stopwatch.stop();
		return result;
	}

	/**
	 * A business method to get list of experiments in eSig Process
	 * 
	 * @param ntUserID
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public List<SignaturePageVO> getExperimentsBeingSigned(String ntUserID) throws StorageException, StorageException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageEJB.getExperimentsBeingSigned");

		List<SignaturePageVO> result = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookLoadDAO dao = daoFactory.getNotebookLoadDAO();
			result = dao.getExperimentsBeingSigned(ntUserID);
		} catch (DAOException e) {
			log.error(e);
			throw new StorageException(e);
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}

		return result;
	}

	/**
	 * A business method to get the Page Status for a given Notebook Reference
	 * 
	 * @param siteCode
	 * @param nbRefStr
	 * @param version
	 * @return
	 * @throws StorageException
	 * @throws InvalidNotebookRefException
	 */
	public SignaturePageVO getNotebookPageStatus(String siteCode, String nbRefStr, int version) 
			throws StorageException, StorageException, InvalidNotebookRefException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageEJB.getNotebookPageStatus");

		SignaturePageVO signaturePageVO;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookLoadDAO notebookDAO = daoFactory.getNotebookLoadDAO();
			signaturePageVO = notebookDAO.getNotebookPageStatus(siteCode, nbRefStr, version);

		} catch (InvalidNotebookRefException e1) {
			log.error(e1);
			throw e1;
		} catch (DAOException e2) {
			log.error(e2);
			throw new StorageException(e2);
		} finally {
			DAOFactoryManager.release(daoFactory);
			stopwatch.stop();
		}
		return signaturePageVO;
	}

	/**
	 * A business method to update the Page Status for a given Notebook Reference
	 * 
	 * @param userID
	 * @throws StorageException
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public void updateNotebookPageStatus(String siteCode, String nbRefStr, int version, String status) 
			throws StorageException, StorageException, InvalidNotebookRefException {
		log.debug("updateNotebookPageStatus(siteCode,nbRef,ver,status).enter");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			notebookDAO.updateNotebookPageStatus(siteCode, nbRefStr, version, status);
			log.debug("updateNotebookPageStatus().exit");
		} catch (InvalidNotebookRefException e1) {
			log.error(e1);
			throw e1;
		} catch (DAOException e2) {
			log.error(e2);
			throw new StorageException(e2);
		} finally {
			DAOFactoryManager.release(daoFactory);
		}
	}

	/**
	 * A business method to update the Page Status & UssiKey for a given Notebook Reference
	 * 
	 * @param userID
	 * @throws StorageException
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public void updateNotebookPageStatus(String siteCode, String nbRefStr, int version, String status, int ussiKey)
			throws StorageException, StorageException, InvalidNotebookRefException {
		log.debug("updateNotebookPageStatus(siteCode,nbRef,ver,status,usskey).enter");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			notebookDAO.updateNotebookPageStatus(siteCode, nbRefStr, version, status, ussiKey);
			log.debug("updateNotebookPageStatus().exit");
		} catch (InvalidNotebookRefException e1) {
			log.error("Error updating notebook page status", e1);
			throw e1;
		} catch (DAOException e2) {
			log.error(e2);
			throw new StorageException(e2);
		} finally {
			DAOFactoryManager.release(daoFactory);
		}
	}
	
	public CompositeCompressedBytes generateExperimentPDF(String siteCode, String nbRefStr, int version, String timeZoneId, boolean bool) 
			throws StorageException, StorageException, InvalidNotebookRefException {
		log.debug("generateExperimentPDF().enter");
		NotebookRef notebookRef = new NotebookRef();
		notebookRef.setNotebookRef(nbRefStr);
		notebookRef.setVersion(version);
		NotebookPageModel pageModel;
		try {
			pageModel = (NotebookPageModel) SerializeObjectCompression.convertCompressedBytesToObject(getNotebookPageExperimentInfo(notebookRef, null));
		} catch (Exception e1) {
			throw new StorageException(e1);
		}
		
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookUpdateDAO notebookUpdateDAO = daoFactory.getNotebookUpdateDAO();
			byte[] pdfBytes = getPrintedExperiment(pageModel, timeZoneId);
			log.debug("generateExperimentPDF().exit");
			return SerializeObjectCompression.serializeToCompressedBytes(pdfBytes);
		} catch (PropertyException e) {
			String msg = "PropertyException: Failed to retrieve experiment PDF: " + nbRefStr + " for site: " + siteCode + " ver " + version;
			log.error(msg, e);
			throw new StorageException(msg, e);
		} catch (SystemPropertyException e) {
			String msg = "SystemPropertyException: Failed to retrieve experiment PDF: " + nbRefStr + " for site: " + siteCode + " ver " + version;
			log.error(msg, e);
			throw new StorageException(msg, e);
		} catch (IOException e) {
			String msg = "IOException: Failed to retrieve experiment PDF: " + nbRefStr + " for site: " + siteCode + " ver " + version;
			log.error(msg, e);
			throw new StorageException(msg, e);
		} catch (DAOException e) {
			String msg = "Error: Failed to generate experiment PDF: " + nbRefStr + " for site: " + siteCode + " ver " + version;
			log.error(msg, e);
			throw new StorageException(msg, e);
		} finally {
			DAOFactoryManager.release(daoFactory);
		}
	}

	/**
	 * A business method to get the PDF stored for the designated experiment
	 * 
	 * @param userID
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public CompositeCompressedBytes getExperimentPDF(String siteCode, String nbRefStr, int version, boolean bool) 
			throws StorageException, StorageException, InvalidNotebookRefException {
		log.debug("getExperimentPDF().enter");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			byte[] pdfBytes = notebookDAO.getExperimentPDF(siteCode, nbRefStr, version);
			log.debug("getExperimentPDF().exit");
			return SerializeObjectCompression.serializeToCompressedBytes(pdfBytes);
		} catch (InvalidNotebookRefException e1) {
			log.error("Error: Failed to retrieve experiment PDF: " + nbRefStr + " for site: " + siteCode + " ver " + version, e1);
			throw e1;
		} catch (DAOException e2) {
			String msg = "Error: Failed to retrieve experiment PDF: " + nbRefStr + " for site: " + siteCode + " ver " + version;
			log.error(msg, e2);
			throw new StorageException(msg, e2);
		} finally {
			DAOFactoryManager.release(daoFactory);
		}
	}

	/**
	 * A business method to store the PDF stored for the designated experiment
	 * 
	 * @param userID
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public void storeExperimentPDF(String siteCode, String nbRefStr, int version, byte[] pdf) throws StorageException,
			StorageException, InvalidNotebookRefException {
		log.debug("storeExperimentPDF().enter");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			notebookDAO.storeExperimentPDF(siteCode, nbRefStr, version, pdf);
			log.debug("storeExperimentPDF().exit");
		} catch (InvalidNotebookRefException e1) {
			log.error("Error saving PDF", e1);
			throw e1;
		} catch (DAOException e2) {
			String msg = "Error: Failed to store experiment PDF: " + nbRefStr + " for site: " + siteCode + " ver " + version;
			log.error(msg, e2);
			throw new StorageException(msg, e2);
		} finally {
			DAOFactoryManager.release(daoFactory);
		}
	}


	public List getNotebookPagesForUser(String userid, SessionIdentifier identifier) {
		return null;
	}

	public NotebookPageModel getNotebookPageHeaderInfo(NotebookRef nbRef, int version, SessionIdentifier identifier) {
		return null;
	}

	public List getAllReactionsSteps(NotebookRef nbRef, SessionIdentifier identifier) {
		return null;
	}

	public List getAllReactionStepsWithDetails(NotebookRef nbRef, SessionIdentifier identifier) {
		return null;
	}

	public ReactionStepModel getAllReactionStepDetails(NotebookRef nbRef, int version, ReactionStepModel step,
			SessionIdentifier identifier) {
		return null;
	}

	public ReactionStepModel getSummaryReaction(NotebookRef nbRef, SessionIdentifier identifier) {
		return null;
	}

	public ReactionStepModel getIntermediateReactionStep(NotebookRef nbRef, int stepno, SessionIdentifier identifier) {
		return null;
	}

	/**
	 * 
	 *  This method returns complete notebook page data for a given experiment
	 * 
	 * @param nbRef
	 * @param identifier
	 * @return 
	 */
	public CompositeCompressedBytes getNotebookPageExperimentInfo(NotebookRef nbRef, SessionIdentifier identifier) throws StorageException {
		StorageServiceImpl storageImpl = new StorageServiceImpl();
		try {
			NotebookPageModel pageModel = storageImpl.getNotebookPageExperimentInfo(nbRef, identifier);
			return SerializeObjectCompression.serializeToCompressedBytes(pageModel);
		} catch (Exception err) {
			log.error("Failed to retrieve experiment info for: " + nbRef, err);
			throw new StorageException("Failed to retrieve experiment info for: " + nbRef, err);
		}
	}
  
	/**
	 * Get the attachment info and its contents for a notebook page experiment.
	 * 
	 * @param attachmentKey
	 * @return
	 */
	public CompositeCompressedBytes getNotebookPageExperimentAttachment(String attachmentKey, boolean bool) throws StorageException{
		DAOFactory daoFactory = null;

		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			AttachmentDAO attachmentDAO = daoFactory.getAttachmentDAO();
			AttachmentModel attachmentModel = attachmentDAO.loadAttachmentAndContents(attachmentKey);

			/*
			if (attachmentModel != null) {
				return SerializeObjectCompression.serializeToCompressedBytes(attachmentModel);
			}
			*/
			return SerializeObjectCompression.serializeToCompressedBytes(attachmentModel);
		} catch (Exception err) {
			log.error("Failed to retrieve attachment for attachment key: " + attachmentKey, err);
			throw new StorageException("Failed to retrieve attachment for attachment key: " + attachmentKey, err);
		} finally {
			if (daoFactory != null) {
				DAOFactoryManager.release(daoFactory);
			}
		}
	}

	public NotebookPageModel createParallelExperiment(String spid, String notebook, SessionIdentifier identifier) throws StorageException{
		StorageServiceImpl storageImpl = new StorageServiceImpl();
		try {
			return storageImpl.createParallelExperiment(spid, notebook, identifier);
		} catch (Exception err) {
			throw new StorageException("Failed to create experiment for SPID: " + spid + " notebook ref: " + notebook, err);
		}
	}

	/** 
	 * @deprecated - returned null - now it throws an exception
	 * @param userid
	 * @return
	 */
	public NotebookPageModel createConceptionRecord(String userid) throws StorageException {
		throw new StorageException("createConceptionRecord(userId) is not implemented.  Use createConceptionExperiment(notebook,session) instead.");
	}

	/**
	 * @deprecated - returned null - now it throws an exception
	 * @param userid
	 * @return
	 */
	public NotebookPageModel createSingletonExperiment(String userid) throws StorageException {
		throw new StorageException("createSingletonExperiment(userId) is not implemented.  Use createSingletonExperiment(notebook,session) instead.");
	}

	// Update only NotebookPage Header data
	/**
	 * @deprecated this method simply returns true without acting on anything.
	 */
	public boolean upadateNotebookPage(NotebookRef nbkRef, int version, NotebookPageHeaderModel pageHeader,
			SessionIdentifier identifier) {
		return true;
	}

	public NotebookPageModel getExperiment(String nbkNumber, String pageNumber) {
		return null;
	}

	public DesignSynthesisPlan getDesignSynthesisPlanHeader(String spid) {
		return null;
	}

	public DesignSynthesisPlan getDesignSynthesisPlanAll(String spid, boolean includeStructures) {
		return null;
	}

	public NotebookPageModel getDesignSynthesisPlanWithSummaryReaction(String spid, boolean includeStructures) {
		return null;
	}

	public ReactionStepModel getDesignSynthesisPlanReactionStep(String spid, String planId, boolean includeStructures) {
		return null;
	}

	public boolean addDSPPlanToCeNReactionStep(NotebookRef nbRef, String PID, SessionIdentifier identifier) throws StorageException {
		StorageServiceImpl storageImpl = new StorageServiceImpl();
		try {
			return storageImpl.addDSPPlanToCeNReactionStep(nbRef, PID, identifier);
		} catch (Exception err) {
			throw new StorageException(err);
		}
	}

	public ArrayList getAllContainers() throws StorageException {
		ContainerServiceImpl containerImpl = new ContainerServiceImpl();
		return containerImpl.getAllContainers();
	}

	public ArrayList getContainers(boolean isUserDefined) throws StorageException {
		try {
			ContainerServiceImpl containerImpl = new ContainerServiceImpl();
			return containerImpl.getContainers(isUserDefined);
		} catch (StorageException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public ArrayList getUserContainers(String userId) throws StorageException {
		try {
			ContainerServiceImpl containerImpl = new ContainerServiceImpl();
			return containerImpl.getUserContainers(userId);
		} catch (StorageException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public Container getContainer(String key) throws StorageException {
		try {
			ContainerServiceImpl containerImpl = new ContainerServiceImpl();
			return containerImpl.getContainer(key);
		} catch (StorageException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public void createContainer(Container container) throws StorageException {
		try {
			ContainerServiceImpl containerImpl = new ContainerServiceImpl();
			containerImpl.createContainer(container);
		} catch (StorageException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public void createContainers(List containerList) throws StorageException {
		try {
			ContainerServiceImpl containerImpl = new ContainerServiceImpl();
			containerImpl.createContainers(containerList);
		} catch (StorageException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public void updateContainer(Container container) throws StorageException {
		try {
			ContainerServiceImpl containerImpl = new ContainerServiceImpl();
			containerImpl.updateContainer(container);
		} catch (StorageException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public void updateContainers(List containerList) throws StorageException {
		try {
			ContainerServiceImpl containerImpl = new ContainerServiceImpl();
			containerImpl.updateContainers(containerList);
		} catch (StorageException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public void removeContainer(String key) throws StorageException {
		try {
			ContainerServiceImpl containerImpl = new ContainerServiceImpl();
			containerImpl.removeContainer(key);
		} catch (StorageException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public void removeContainers(List containerKeyList) throws StorageException {
		try {
			ContainerServiceImpl containerImpl = new ContainerServiceImpl();
			containerImpl.removeContainers(containerKeyList);
		} catch (StorageException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public ArrayList<Container> searchForCompoundManagementContainers(Container container) throws StorageException {
		try {
			ContainerServiceImpl containerImpl = new ContainerServiceImpl();
			return containerImpl.searchForCompoundManagementContainers(container);
		} catch (Exception se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public void removeNotebookPage(String pageKey, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			storageImpl.removeExperiment(pageKey, CommonUtils.getCurrentTimestamp());
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	@Override
	public void updateNotebookPageModel(CompositeCompressedBytes pageModel, SessionIdentifier identifier) throws StorageException {
		NotebookPageModel page = null;
		
		try {
			page = (NotebookPageModel) SerializeObjectCompression.convertCompressedBytesToObject(pageModel);
			
			log.debug("UPDATING NotebookPage :" + page.getNotebookRefAsString());
			
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			storageImpl.updateNotebookPage(page, identifier);
			
			log.debug("UPDATING NotebookPage Complete.");
		} catch (Exception e) {
			log.error("Failed to update NotebookPageModel: " + page == null ? "null" : page.getNotebookRefAsString(), e);
			throw new StorageException("Failed to update NotebookPageModel: " + page == null ? "null" : page.getNotebookRefAsString(), e);
		}
	}

	public NotebookPageModel createConceptionRecord(String notebook, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.createConceptionRecord(notebook, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public NotebookPageModel createSingletonExperiment(String notebook, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.createSingletonExperiment(notebook, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public void deletePlateWells(String[] plateWellKeys, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			storageImpl.deletePlateWells(plateWellKeys, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}
	
	public void deletePlates(String[] plateKeys, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			storageImpl.deletePlates(plateKeys, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}

	}

	/*
	 * @return ArrayList of CROPages
	 */
	public ArrayList getAllCROs(SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.getAllCROs(identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}

	}

	/*
	 * @ return ArrayList of CROPages
	 */
	public ArrayList getAllChemistsUnderCRO(String croId, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.getAllChemistsUnderCRO(croId, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	/*
	 * @ return ArrayList containing the NotebookPageModel with summary information which includes NotebookPageHeader and
	 * ReactionScheme information
	 */
	public ArrayList getAllPagesWithSummaryUnderChemist(String chemistId, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.getAllPagesWithSummaryUnderChemist(chemistId, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	/*
	 * @ return ArrayList containing the NotebookPageModel with summary information which includes NotebookPageHeader and
	 * ReactionScheme information
	 */
	public ArrayList getAllPagesWithSummaryForRequestId(String requestId, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.getAllPagesWithSummaryForRequestId(requestId, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	/**
	 * @return ArrayList containing request id's
	 */
	public ArrayList getAllRequestIdsForChemist(String chemistId, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.getAllRequestIdsForChemist(chemistId, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	// This is local ejb method called by ExternalCollaborator MDB
	public void createCeNExperiment(NotebookPageModel pageModel) throws StorageException,
			com.chemistry.enotebook.storage.StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			storageImpl.createCeNExperiment(pageModel);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	/**
	 * @param chemistId
	 * @param identifier
	 * @return List with Notebook numbers
	 */
	public List getAllNotebooksForChemistId(String chemistId, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.getAllNotebooksForChemistId(chemistId, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	/**
	 * @param nbkNo
	 *            Notebook number
	 * @param identifier
	 * @return List of pages with summary corresponding to notebookpage number
	 */
	public List getAllPagesForNotebook(String nbkNo, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.getAllPagesForNotebook(nbkNo, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	/**
	 * @param requestId
	 * @param identifier
	 * @return modifiedDate
	 */
	public java.util.Date getCROModifiedDate(String requestId, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.getCROModifiedDate(requestId, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	/**
	 * @param nbkRef
	 * @param identifier
	 * @return CROPageInfo Object corresponding to NotebookRef
	 */
	public CROPageInfo getCroPageForNBK(NotebookRef nbkRef, SessionIdentifier identifier) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.getCroPageForNBK(nbkRef, identifier);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	/**
	 * @param requestId
	 * @throws StorageException
	 */
	public void removeCRORequestId(String requestId) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			storageImpl.removeCRORequestId(requestId);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	/**
	 * @param jobModel
	 */
	public String[] insertRegistrationJobs(JobModel[] jobModels) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			return storageImpl.insertRegistrationJobs(jobModels);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	/**
	 * @param jobModels
	 */
	public void updateRegistrationJobs(JobModel[] jobModels, SessionIdentifier session) throws StorageException {
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			storageImpl.updateRegistrationJobs(jobModels);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public JobModel getRegistrationJob(String jobId) throws StorageException {
		try {
			return new StorageServiceImpl().getRegistrationJob(jobId);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}
	
	public List<JobModel> getAllRegistrationJobs(String userId, String status) throws StorageException {
		try {
			return new StorageServiceImpl().getAllRegistrationJobs(userId, status);
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}
	
	public List getAllRegistrationJobs() throws StorageException {
		List jobs = null;
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			jobs = storageImpl.getAllRegistrationJobs();
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
		return jobs;
	}

	/**
	 * @param status
	 */
	public List getAllRegistrationJobsAndUpdateStatus(String status) throws StorageException {
		List jobs = null;
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			jobs = storageImpl.getAllRegistrationJobsAndUpdateStatus(status);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
		return jobs;
	}
	
	/**
	 * @param jobModels
	 */
	public void updateBatchJobs(BatchRegInfoModel[] batchModels, SessionIdentifier session) throws StorageException {
		try {
			log.debug("updateBatchJobs.enter");
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			storageImpl.updateBatchJobs(batchModels);
			log.debug("updateBatchJobs.exit");
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
	}

	public List getAllBatchJobs(String jobId, SessionIdentifier session) throws StorageException {
		List jobs = null;
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			jobs = storageImpl.getAllBatchJobs(jobId);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
		return jobs;
	}

	public List getAllPendingCompoundRegistrationJobs() throws StorageException {
		List jobIds = null;
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			jobIds = storageImpl.getAllPendingCompoundRegistrationJobs();
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
		return jobIds;

	}

	/**
	 * @param jobId
	 * @return
	 */
	public List getAllCompoundRegistrationJobOffsets(String jobId) throws StorageException {
		List offsets = null;
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			offsets = storageImpl.getAllCompoundRegistrationJobOffsets(jobId);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
		return offsets;

	}

	/**
	 * @param pageKey
	 * @param sessionID
	 * @return List
	 */
	public List<BatchRegInfoModel> getAllRegisteredBatchesForPage(String pageKey, SessionIdentifier sessionID) throws StorageException {
		List<BatchRegInfoModel> regBatches = null;
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			regBatches = storageImpl.getAllRegisteredBatchesForPage(pageKey, sessionID);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
		return regBatches;
	}

	public List getAllRegisteredBatchesWithJobID(String jobid, SessionIdentifier sessionID) throws StorageException {
		log.debug("getAllRegisteredBatchesWithJobID.enter");
		List regBatches = null;
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			regBatches = storageImpl.getAllRegisteredBatchesWithJobID(jobid, sessionID);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
		log.info("getAllRegisteredBatchesWithJobID.exit");
		return regBatches;
	}

	public JobModel getCeNPlateJob(String plateKey) throws StorageException {
		log.debug("getCeNPlateJob.enter");
		JobModel jobModel = null;
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			jobModel = storageImpl.getCeNPlateJob(plateKey);
		} catch (DAOException se) {
			throw new StorageException(se.getMessage(), se);
		}
		log.info("getCeNPlateJob.exit");
		return jobModel;
	}

	/**
	 * Updates the object graph in NotebookPage Model.
	 */
	public void addNotebookPage(NotebookPageModel pageModel, SessionIdentifier identifier) throws StorageException {
		try {
			log.debug("Adding new NotebookPage :" + pageModel.getNotebookRefAsString());
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			storageImpl.addParallelExperimentPage(pageModel, identifier);
			log.debug("Adding new NotebookPage Complete.");
		} catch (DAOException dse) {
			log.error(dse);
			throw new StorageException(dse.getMessage(), dse);
		} catch (Exception se) {
			se.printStackTrace();
			throw new StorageException(se.getMessage(), se);
		}
	}
	
  /**
   * Updates the object graph in NotebookPage Model.
   */
  public void addNotebookPage(CompositeCompressedBytes pageModelCompressed, SessionIdentifier identifier) throws StorageException {
    NotebookPageModel pageModel = null;

    try {
      pageModel = (NotebookPageModel) SerializeObjectCompression.
        convertCompressedBytesToObject(pageModelCompressed);
      
      log.debug("Adding new NotebookPage :" + pageModel.getNotebookRefAsString());
      StorageServiceImpl storageImpl = new StorageServiceImpl();
      storageImpl.addParallelExperimentPage(pageModel, identifier);
      log.debug("Adding new NotebookPage Complete.");
    } catch (DAOException dse) {
      log.error(dse);
      throw new StorageException(dse.getMessage(), dse);
    } catch (Exception se) {
      se.printStackTrace();
      throw new StorageException(se.getMessage(), se);
    }
  }	

	public ProductBatchModel getProductBatchForBatchNumber(String batchNumber, SessionIdentifier identifier) throws StorageException {
		ProductBatchModel prodModel = null;
		try {
			StorageServiceImpl storageImpl = new StorageServiceImpl();
			prodModel = storageImpl.getProductBatchForBatchNumber(batchNumber, identifier);
			return prodModel;
		} catch (DAOException dse) {
			log.error("Error retrieving product batch for batch number: " + batchNumber, dse);
			throw new StorageException("Error retrieving product batch for batch number: " + batchNumber, dse);
		} catch (Exception se) {
			log.error("Error retrieving product batch for batch number: " + batchNumber, se);
			throw new StorageException("Error retrieving product batch for batch number: " + batchNumber, se);
		}
	}

	/**
	 * An example business method
	 * 
	 * @param username
	 * @param numComplianceDays
	 * @throws StorageException Thrown if the instance could not perform the function requested due to a software related error.
	 */
	public int checkCompliance(String username, int numComplianceDays) throws StorageException {
		int count = 0;
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("StorageServiceImpl.checkCompliance()");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			count = notebookDAO.checkCompliance(username, numComplianceDays);

		} catch (DAOException daoe) {
			log.error("DAO exception:" + CommonUtils.getStackTrace(daoe));
			throw new StorageException("StorageRemote:checkCompliance:: Failure.\nMessage: " + ExceptionUtils.getRootCause(daoe)
					+ " at " + (new java.util.Date(System.currentTimeMillis())));

		} finally {
			DAOFactoryManager.release(daoFactory);
		}
		log.debug("Compliance Days:" + count + " for user:" + username);
		log.debug("checkCompliance().exit");
		stopwatch.stop();
		return count;
	}

	/**
	 * A business method to update the Page Status & UssiKey for a given Notebook Reference
	 * 
	 * @param userID
	 * @throws StorageException
	 * Thrown if the instance could not perform the function requested by the container because of an system-level error.
	 */
	public void updateNotebookPage(String siteCode, String nbRefStr, int version, String status, int ussiKey, byte[] pdfContent,
			String completionDateTSStr) throws StorageException, InvalidNotebookRefException {
		log.debug("updateNotebookPage(siteCode,nbRef,ver,status,usskey,pdf,compldate).enter");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			notebookDAO.updateNotebookPage(siteCode, nbRefStr, version, status, ussiKey, pdfContent, completionDateTSStr);
			log.debug("updateNotebookPageStatus().exit");
		} catch (InvalidNotebookRefException e1) {
			log.error("Error updating page: " + nbRefStr + " Site: " + siteCode + " ver: " + version + " status " + status, e1);
			throw e1;
		} catch (DAOException e2) {
			log.error("Error updating page: " + nbRefStr + " Site: " + siteCode + " ver: " + version + " status " + status, e2);
			throw new StorageException("Error updating page: " + nbRefStr + " Site: " + siteCode + " ver: " + version + " status " + status, e2);
		} finally {
			DAOFactoryManager.release(daoFactory);
		}
	}

	public void updateNotebookPage(String siteCode, String nbRefStr, int version, String status, byte[] pdfContent,
			String pageXMLMetadata) throws StorageException, InvalidNotebookRefException {
		log.debug("updateNotebookPage(siteCode,nbRef,ver,status,pdf,xmlmetadat).enter");
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookUpdateDAO notebookUpdateDAO = daoFactory.getNotebookUpdateDAO();
			notebookUpdateDAO.updateNotebookPage(siteCode, nbRefStr, version, status, pdfContent, pageXMLMetadata);
			log.debug("updateNotebookPageStatus().exit");
		} catch (InvalidNotebookRefException e1) {
			log.error("Error updating page: " + nbRefStr + " Site: " + siteCode + " ver: " + version + " status " + status, e1);
			throw e1;
		} catch (Throwable e2) {
			log.error("Error updating page: " + nbRefStr + " Site: " + siteCode + " ver: " + version + " status " + status, e2);
			throw new StorageException("Error updating page: " + nbRefStr + " Site: " + siteCode + " ver: " + version + " status " + status, e2);
		} finally {
			DAOFactoryManager.release(daoFactory);
		}
	}
	
	/**
	 * Note Completion date should be set by the GUI as the server may not be in the same time zone.
	 */
	public boolean completeExperiment(String siteCode, String nbRefStr, int version, TemplateVO template, NotebookUser user, String timeZoneId) 
		throws StorageException, InvalidNotebookRefException {
		boolean result = false;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookRef nbRef = new NotebookRef(nbRefStr);
			nbRef.setVersion(version);
			NotebookPageModel pageModel;
			try {
				pageModel = (NotebookPageModel) SerializeObjectCompression.convertCompressedBytesToObject(getNotebookPageExperimentInfo(nbRef, user.getSessionIdentifier()));
			} catch (Exception e) {
				throw new StorageException(e);
			}
			
			NotebookUpdateDAO notebookUpdateDAO = daoFactory.getNotebookUpdateDAO();
			result = completeExperiment(siteCode, pageModel, template, user, timeZoneId);
		} catch (DAOException e2) {
			log.error("Error completing page: " + nbRefStr, e2);
			throw new StorageException("Error completing page: " + nbRefStr, e2);
		}
		return result;
	}
	
	private boolean completeExperiment(	String siteCode, NotebookPageModel pageModel, TemplateVO template, NotebookUser user, String timeZoneId) throws DAOException {
			boolean result = false;
			String nbRefStr = pageModel.getNotebookRefAsString();
			int version = pageModel.getVersion();
			try {
				if (template == null) { // No template - no sign in Signature Service
					result = true;
				} else { // Has template - let's sign!
					String pageHeaderXml = "";
					try {
						byte[] buffer = getPrintedExperiment(pageModel, timeZoneId);
						pageHeaderXml = new SignatureDelegate().publishDocument(pageModel, buffer, makeDocName(pageModel) + ".pdf", new Date(), template, user);
						updateNotebookPage(pageModel.getSiteCode(), pageModel.getNotebookRefWithoutVersion(), pageModel.getVersion(), pageModel.getStatus(), buffer, pageHeaderXml);
						result = true;
					} catch (IOException ex) {//something wrong with pdf generation
						updateNotebookPageStatus(pageModel.getSiteCode(), pageModel.getNotebookRefWithoutVersion(), pageModel.getVersion(), CeNConstants.PAGE_STATUS_OPEN);
						//Failed to generate IP report for Signature Service.
						throw new DAOException(ex);
					}				
				}
			} catch (Throwable e) {
				throw new DAOException("Error completing experiment: " + nbRefStr + ", version:" + version + " for user: " + (user != null ? user.getNTUserID() : "user was null"), e);
			}
			return result;
		}
	
	private byte[] getPrintedExperiment(NotebookPageModel pageModel, String timeZoneId) throws PropertyException, SystemPropertyException, IOException {
		ReportURLGenerator urlgen = new ReportURLGenerator();
		PrintOptions printOptions = null;
		String version = String.valueOf(pageModel.getNbRef().getVersion());
		if (pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_CONCEPTION)) {
			printOptions = new ConceptionPrintOptions();
			urlgen.addParameter(PrintSetupConstants.REPORT_NAME, "conception.rptdesign");
		} else if (pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_PARALLEL)) {
			printOptions = new ParallelPrintOptions();
			urlgen.addParameter(PrintSetupConstants.REPORT_NAME, "parallel.rptdesign");
		} else if (pageModel.getPageType().equals(CeNConstants.PAGE_TYPE_MED_CHEM)) {
			printOptions = new SingletonPrintOptions();
			urlgen.addParameter(PrintSetupConstants.REPORT_NAME, "medchem.rptdesign"); 
		}		
		printOptions.setOptionsRequiredForIP();
		
		urlgen.addParameter(PrintSetupConstants.TIME_ZONE, timeZoneId);
		urlgen.addParameter(PrintSetupConstants.NOTEBOOK_NUMBER, pageModel.getNbRef().getNbNumber());
		urlgen.addParameter(PrintSetupConstants.PAGE_NUMBER, pageModel.getNbRef().getNbPage());
		urlgen.addParameter(PrintSetupConstants.OUTPUT_FORMAT, ReportURLGenerator.PDF);
		urlgen.addParameter(PrintSetupConstants.VERSION, version);
        urlgen.addParameter(PrintSetupConstants.STOP_AFTER_IMAGE_LOAD_ERROR, "false");//temporary. TODO: change to true
		
		StringBuffer buff = new StringBuffer();
		buff.append(pageModel.getNbRef().getNbNumber()).append("-").append(pageModel.getNbRef().getNbPage());
		buff.append("v").append(version);
		
		String filename = buff.toString();
		urlgen.addParameter(PrintSetupConstants.FILE_NAME, filename);
		Map<String, String> otherOptions = printOptions.getOptions();
		for(String key : otherOptions.keySet()) {
			urlgen.addParameter(key, (String) otherOptions.get(key));
		}
		if (urlgen.isComplete()) {
			String url = urlgen.getURL();
			/*
			URL printUrl = new URL(url);
			URLConnection conn = printUrl.openConnection();
            final String error = conn.getHeaderField("Error");
            if (StringUtils.isNotBlank(error)) {
                final IOException exception = new IOException("Error in report generation: " + error);
                log.error("Error in report experiment " + error, exception);
                throw exception;
            }
			InputStream in = conn.getInputStream();
			ArrayList<Byte> contents = new ArrayList<Byte>();
			int numRead = 0;
			while ((numRead = in.read()) != -1) {
				contents.add(new Byte((byte) numRead));
			}
			byte[] retArray = new byte[contents.size()];
			for (int i = 0; i < contents.size(); ++i) {
				retArray[i] = contents.get(i).byteValue();
			}
			*/

			try {
				return CommonUtils.getReportHttpResponseResult(url);
			} catch (Exception e) {
				throw new IOException(e.getMessage());
			}
		}
		return null;
	}
	
	private String makeDocName(NotebookPageModel pageModel) {
		return pageModel.getNotebookRefAsString();
	}
	
	/**
	 * @throws StorageException 
	 */
	public String getNotebookPageCompleteStatus(String siteCode, String nbRefStr, int version) throws InvalidNotebookRefException, StorageException {
		String result = null;
		DAOFactory daoFactory = null;
		try {
			daoFactory = DAOFactoryManager.getDAOFactory();
			NotebookDAO notebookDAO = daoFactory.getNotebookDAO();
			result = notebookDAO.getNotebookPageCompleteStatus(siteCode, nbRefStr, version);
		} catch (InvalidNotebookRefException e1) {
			throw e1;
		} catch (DAOException e2) {
			throw new StorageException(e2);
		}
		return result;
	}
	
	public void insertCroPageInfo(CROPageInfo croInfo) throws StorageException {
		try {
			DAOFactoryManager.getDAOFactory().getCroPageDAO().insertCROPageInfo(croInfo);
		} catch (DAOException e) {
			throw new StorageException(e);
		}
	}
}
