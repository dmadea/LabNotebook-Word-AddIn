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
 * NotebookDelegate.java
 * 
 * Created on Aug 10, 2004
 *
 *
 */
package com.chemistry.enotebook.client.delegate;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.gui.controller.ServiceController;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.common.PersistableObject;
import com.chemistry.enotebook.experiment.datahandlers.*;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookPage;
import com.chemistry.enotebook.experiment.datamodel.page.NotebookRef;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.storage.*;
import com.chemistry.enotebook.storage.delegate.StorageAccessException;
import com.chemistry.enotebook.storage.delegate.StorageDelegate;
import com.chemistry.enotebook.storage.exceptions.StorageInitException;
import com.chemistry.enotebook.storage.exceptions.StorageTokenInvalidException;
import com.chemistry.enotebook.util.EncryptionUtil;
import com.chemistry.enotebook.util.Stopwatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

/**
 * 
 * @date Aug 10, 2004
 */
public class NotebookDelegate {
	private static final Log log = LogFactory.getLog(NotebookDelegate.class);
	// Vars
	private StorageDelegate storageDelegate = null;
	private SessionIdentifier sessionId = null;

	/**
	 * 
	 * @param sessionId
	 * @throws NotebookDelegateException
	 */
	public NotebookDelegate(SessionIdentifier sessionId) throws NotebookDelegateException {
		try {
			storageDelegate = ServiceController.getStorageDelegate(sessionId);
			// xpathAPI = new CachedXPathAPI();
			this.sessionId = sessionId;
		} catch (StorageInitException e) {
			throw new NotebookDelegateException("Failed accessing storage delegate", e);
		}
	}

	/**
	 * 
	 */
	public NotebookDelegate() {
	}

	/**
	 * 
	 * @param nbPage
	 * @param loadLevel
	 * @param tableKeys
	 * @param isSuperUser
	 * @return
	 * @throws NotebookDelegateException
	 * @throws StorageTokenInvalidException
	 */
	public NotebookPage loadConstrainedPageData(NotebookPage nbPage, byte loadLevel, HashMap tableKeys, boolean isSuperUser)
			throws NotebookDelegateException, StorageTokenInvalidException {
		NotebookPage notebookPage = null;
		notebookPage = load(nbPage, 
		                    loadLevel, 
		                    tableKeys, 
		                    null, 
		                    nbPage.getSiteCode(), 
		                    nbPage.getNotebookRef(), 
		                    nbPage.getVersion(), 
		                    isSuperUser);
		return notebookPage;
	}

	/**
	 * Loads auto-saved.
	 * 
	 * @param siteCode
	 * @param nbRef
	 * @param version
	 * @param loadLevel
	 * @param autoSaveLocation
	 * @param isSuperUser
	 * @return
	 * @throws NotebookDelegateException
	 * @throws StorageTokenInvalidException
	 */
	public NotebookPage load(String siteCode, NotebookRef nbRef, int version, byte loadLevel, String autoSaveLocation,
			boolean isSuperUser) throws NotebookDelegateException, StorageTokenInvalidException {
		NotebookPage notebookPage = null;
		notebookPage = load(null, loadLevel, null, autoSaveLocation, siteCode, nbRef, version, isSuperUser);
		return notebookPage;
	}

	/**
	 * 
	 * @param siteCode
	 * @param nbRef
	 * @param version
	 * @param loadLevel
	 * @param isSuperUser
	 * @return
	 * @throws NotebookDelegateException
	 * @throws StorageTokenInvalidException
	 */
	public NotebookPage load(String siteCode, NotebookRef nbRef, int version, byte loadLevel, boolean isSuperUser)
			throws NotebookDelegateException, StorageTokenInvalidException {
		NotebookPage notebookPage = null;
		notebookPage = load(null, loadLevel, null, null, siteCode, nbRef, version, isSuperUser);
		return notebookPage;
	}

	/**
	 * this is the method that all other methods to load notebook data from storage will use
	 * 
	 * @param nbPage
	 * @param loadLevel
	 * @param tableKeys
	 * @param autoSaveLocation
	 * @param siteCode
	 * @param nbRef
	 * @param version
	 * @param isSuperUser
	 * @return
	 * @throws NotebookDelegateException
	 * @throws StorageTokenInvalidException
	 */
	private NotebookPage load(NotebookPage nbPage, byte loadLevel, HashMap tableKeys, String autoSaveLocation, String siteCode,
			NotebookRef nbRef, int version, boolean isSuperUser) throws NotebookDelegateException, StorageTokenInvalidException {
		Stopwatch stopwatch = new Stopwatch();
		NotebookPageModel pageModel = null;
		stopwatch.start("NotebookDelegate.load " + nbRef);
		try {
			pageModel = storageDelegate.getNotebookPageHeaderInfo(nbRef, version, MasterController.getUser().getSessionIdentifier());
		} catch (StorageException e1) {
			throw new NotebookDelegateException("Middle Tier failed to process request to completion.", e1);
		}
		// Notebook Page should be created here with readonly flag set
		// appropriately.
		// i.e. user of this object cannot change this flag
		if (nbPage == null && pageModel != null) {
			boolean editable = ((isSuperUser && sessionId.isSuperUser())
					|| sessionId.getUserID().equalsIgnoreCase(pageModel.getUserName()) );
			nbPage = new NotebookPage(!editable);
			nbPage.setLoading(true);
			nbPage.setNotebookRef(nbRef);
		}
		// Fill in details with NotebookPageModel return
		nbPage = getNotebookPageFromPageModel(pageModel, nbPage);
		nbPage.setLoading(false);
		if (autoSaveLocation != null && nbPage.isPageEditable()) {
			// autoRecover(nbPage, _nbContext.getNotebookMap(),
			// autoSaveLocation);
			// System.out.println("");
		}
		stopwatch.stop();
		return nbPage;
	}

	/**
	 * 
	 * @param nbPage
	 * @return
	 * @throws NotebookDelegateException
	 * @throws StorageTokenInvalidException
	 */
	public boolean isNbDbPageUpdated(NotebookPage nbPage) throws NotebookDelegateException, StorageTokenInvalidException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("NotebookDelegate.isNbDbPageUpdated");
		// get the date of the last modification of this page from the database
		try {
			NotebookContext tempContext = new NotebookContext();
			setTablesInNotebookContext(tempContext, CeNConstants.PAGE_BYTE_RETRIEVE_BRIEF_LEVEL);
			tempContext.setSiteCode(nbPage.getSiteCode());
			tempContext.setNotebookRef(nbPage.getNotebookRefAsString());
			tempContext.setVersion(nbPage.getVersion());
			ArrayList<String> fieldList = new ArrayList<String>();
			fieldList.add("XML_METADATA");
			fieldList.add("PROCEDURE");
			fieldList.add("AUDIT_LOG");
			tempContext.addTableFieldExclusions(CeNTableName.CEN_PAGES, fieldList);
			try {
				tempContext = (NotebookContext) storageDelegate.retrieveData(tempContext,MasterController.getUser().getSessionIdentifier());
			} catch (StorageException e) {
				throw new NotebookDelegateException("", e);
			}
			RowSet rs = (RowSet) tempContext.getNotebookMap().get(CeNTableName.CEN_PAGES.toString());
			if (rs.next()) {
				if (rs.getObject("MODIFIED_DATE") != null && nbPage.getDbLastModificationDate() != null) {
					Date dbModDate = NotebookPageUtil.getLocalDate(rs.getObject("MODIFIED_DATE").toString());
					Date modelModDate = NotebookPageUtil.getLocalDate(nbPage.getDbLastModificationDate());
					if (dbModDate.compareTo(modelModDate) > 0)
						return true;
					else
						return false;
				}
				return false;
			} else
				return false;
		} catch (SQLException sqle) {
			throw new NotebookDelegateException("Unable to retrieve notebook details from database", sqle);
		} catch (ParseException pe) {
			throw new NotebookDelegateException("Unable to format the last modified date correctly.", pe);
		} finally {
			stopwatch.stop();
		}
	}

	/**
	 * 
	 * @param nbPage
	 * @param objLatch
	 * @return
	 * @throws NotebookDelegateException
	 * @throws StorageTokenInvalidException
	 */
	public boolean save(NotebookPage nbPage, Object objLatch) throws NotebookDelegateException, StorageTokenInvalidException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("NotebookDelegate.save");
		if (nbPage.isSaving()) {
			if (objLatch != null)
				synchronized (objLatch) {
					objLatch.notify();
				}
			stopwatch.stop();
			return false;
		}
		nbPage.setSavingFlag(true);
		NotebookContext _nbContext = null;
		// _nbContext = nbPage.getNoteBookContext();
		LinkedHashSet modifiedObjectsBackup = null;
		try {
			if (_nbContext == null) {
				// this is a new page being saved to the database so create
				// a new notebookContext
				_nbContext = new NotebookContext();
				// get a blank set of rowsets back from the Storage
				if (setTablesInNotebookContext(_nbContext, CeNConstants.PAGE_BYTE_RETRIEVE_ALL)) {
					_nbContext = (NotebookContext) storageDelegate.retrieveData(_nbContext, MasterController.getUser().getSessionIdentifier());
				}
				_nbContext.setSiteCode(nbPage.getSiteCode());
				_nbContext.setNotebookRef(nbPage.getNotebookRefAsString());
				_nbContext.setVersion(nbPage.getVersion());
			}
			// Don't save anything if nothing on the page has changed
			if (nbPage.isModelChanged()) {
				if (updateRowSetsInContext(nbPage, _nbContext)) {
					// if some rowsets haven't been updated then we don't
					// need
					// to save them - so remove from the notebookMap
					HashMap notebookMap = _nbContext.getNotebookMap();
					HashMap notebookMapCopy = (HashMap) notebookMap.clone();
					Iterator mapIterator = notebookMap.keySet().iterator();
					while (mapIterator.hasNext()) {
						boolean blnRowChange = false;
						String tableName = (String) mapIterator.next();
						RowSet updatedRs = (RowSet) notebookMap.get(tableName);
						updatedRs.beforeFirst();
						while (updatedRs.next()) {
							if (updatedRs.rowDeleted() || updatedRs.rowInserted() || updatedRs.rowUpdated()) {
								blnRowChange = true;
								break;
							}
						}
						if (!blnRowChange)
							notebookMapCopy.remove(tableName);
					}
					// background save completed - so clear all modified
					// flags
					// but keep a backup of this in case of a save failure
					if (nbPage.isModified())
						nbPage.setModified(false);
					modifiedObjectsBackup = new LinkedHashSet(nbPage.getModifedObjects());
					Iterator listIterator = nbPage.getModifedObjects().iterator();
					while (listIterator.hasNext()) {
						PersistableObject po = (PersistableObject) listIterator.next();
						po.setModified(false);
						po.setExistsInDB(true);
						po.setCachedLocally(false);
					}
					nbPage.clearModifiedObjects();
					// release the lock on the main thread so GUI
					// interaction
					// can continue
					if (objLatch != null)
						synchronized (objLatch) {
							objLatch.notify();
						}
					// save data
					NotebookContext savedNbContext = null;
					// savedNbContext = (NotebookContext)
					// storageDelegate.persistData(_nbContext);
					if (savedNbContext != null)
						System.out.print("TODO");
					// nbPage.setNotebookContext(savedNbContext);
					else
						throw new NotebookDelegateException(
								"Unable to retrieve updated notebook records - Future saves may fail for "
										+ nbPage.getNotebookRefAsString()
										+ ".\n\rClose the page, then reopen before making further changes.");
					// update last modified date for database record
					nbPage.setDblastModificationDate(nbPage.getModificationDate());
					nbPage.setSavingFlag(false);
				} else
					return false;
			}
			return true;
		} catch (SQLException sqle) {
			nbPage.setModified(true);
			throw new NotebookDelegateException("Unable to save notebook data, ", sqle);
		} catch (StorageException e1) {
			nbPage.setModified(true);
			log.error("Initial failure saving notebook data", e1);
			// retrieve the experiment to allow the save to be retried
			try {
				NotebookContext newContext = new NotebookContext();
				setTablesInNotebookContext(newContext, CeNConstants.PAGE_BYTE_RETRIEVE_ALL);
				newContext.setNotebookRef(nbPage.getNotebookRefAsString());
				newContext.setVersion(nbPage.getVersion());
				newContext.setSiteCode(nbPage.getSiteCode());
				_nbContext = (NotebookContext) storageDelegate.retrieveData(newContext, MasterController.getUser().getSessionIdentifier());
				// nbPage.setNotebookContext(_nbContext);
				// reset the modified objects for the datamodel
				for (Iterator it = modifiedObjectsBackup.iterator(); it.hasNext();) {
					PersistableObject po = (PersistableObject) it.next();
					po.setModified(true);
					po.setExistsInDB(false);
					po.setCachedLocally(true);
					nbPage.getModifedObjects().add(po);
				}
			} catch (Exception e) {
				log.error("Attempt to retrieve notebook data for retry also failed", e);
			}
			throw new NotebookDelegateException("Middle Tier failed to complete request.", e1);
		} catch (Exception e) {
            nbPage.setModified(true);
            throw new NotebookDelegateException("Unexpected error occured while trying to save notebook data", e);
		} finally {
			stopwatch.stop();
			if (objLatch != null)
				synchronized (objLatch) {
					objLatch.notify();
				}
			synchronized (nbPage) {
				nbPage.notify();
			}
		}
	}

	/**
	 * 
	 * @param nbPage
	 * @param autoSaveLocation
	 * @return
	 * @throws NotebookDelegateException
	 * @throws StorageTokenInvalidException
	 */
	public boolean save(NotebookPage nbPage, String autoSaveLocation) throws NotebookDelegateException,
			StorageTokenInvalidException {
		if (nbPage.isSaving())
			return false;
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("NotebookDelegate.saveA");
		nbPage.setSavingFlag(true);
		NotebookContext _nbContext = null;
		
		// FIXME  _nvContext is null from now on
		
		// _nbContext = nbPage.getNoteBookContext();
		try {
			// Currently there can never be a new page that's not in the
			// database as the
			// page is
			// saved to the database when fierst created
			// if (_nbContext == null) {
			// // this is a new page being saved to the database so create
			// // a new notebookContext
			// _nbContext = new NotebookContext();
			//	
			// // get a blank set of rowsets back from the Storage
			// if (setTablesInNotebookContext(_nbContext,
			// BYTE_RETRIEVE_ALL)) {
			// // retrieve data
			// _nbContext = (NotebookContext)ServiceController.getStorageDelegate().retrieveData(_nbContext);
			// }
			// _nbContext.setSiteCode(nbPage.getSiteCode());
			// _nbContext.setNotebookRef(nbPage.getNotebookRefAsString());
			// }
			// Don't save anything if nothing on the page has changed
			if (nbPage.isModelChanged()) {
				if (updateRowSetsInContext(nbPage, _nbContext)) {
					// if some rowsets haven't been updated then we don't
					// need
					// to save them - so remove from the notebookMap
					HashMap notebookMap = _nbContext.getNotebookMap();
					HashMap notebookMapCopy = (HashMap) notebookMap.clone();
					Iterator mapIterator = notebookMap.keySet().iterator();
					while (mapIterator.hasNext()) {
						boolean blnRowChange = false;
						String tableName = (String) mapIterator.next();
						RowSet updatedRs = (RowSet) notebookMap.get(tableName);
						updatedRs.beforeFirst();
						while (updatedRs.next()) {
							if (updatedRs.rowDeleted() || updatedRs.rowInserted() || updatedRs.rowUpdated()) {
								blnRowChange = true;
								break;
							}
						}
						if (!blnRowChange)
							notebookMapCopy.remove(tableName);
					}
			
					// save to c: drive
					autoSave(nbPage.getNotebookRefAsString(), notebookMapCopy, autoSaveLocation);
			
				} else
					return false;
			}
			return true;
		} catch (Exception e) {
			throw new NotebookDelegateException("Unable to save notebook data, ", e);
		} finally {
			stopwatch.stop();
			nbPage.setSavingFlag(false);
		}
	}

	private boolean autoSave(String nbRef, HashMap nbMap, String autoSaveLocation) throws NotebookDelegateException {
		boolean encryptFailure = false;
		// saves the latest version of the notebook page to the local C:drive
		// this can then be used to recover the latest changes in case of
		// storage failure.
		try {
			// create a new temporary outputFile
			String tempPath = autoSaveLocation + File.separator;
			File tmpFile = new File(tempPath + nbRef + ".TMP");
			// encrypt the notebookpage context
			// write the encrypted notebook page to the file
			FileOutputStream fout = new FileOutputStream(tmpFile);
			if (EncryptionUtil.encryptObjectToFile(fout, nbMap)) {
				// check for existing versions on the c: drive
				File dir = new File(tempPath);
				final String tempRef = nbRef;
				FilenameFilter filter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return (name.endsWith(".SAV") && name.startsWith(tempRef));
					}
				};
				String[] fileList = dir.list(filter);
				String autoSaveNumber = (new DecimalFormat("000")).format(fileList.length + 1);
				File autoSaveFile = new File(tempPath + nbRef + autoSaveNumber + ".SAV");
				if (autoSaveFile.exists())
					autoSaveFile.delete();
				// rename the temp file
				tmpFile.renameTo(autoSaveFile);
			} else
				encryptFailure = true;
		} catch (FileNotFoundException fnfe) {
			// Error in creating the output file
			throw new NotebookDelegateException("Error accessing local file system", fnfe);
		} catch (Exception e) {
			// Error in creating the output file
			throw new NotebookDelegateException("Error retrieving temporary folder property", e);
		}
		if (encryptFailure)
			throw new NotebookDelegateException("Error creating encrypted notebook page.");
		return true;
	}

//	private boolean autoRecover(NotebookPage nbPage, HashMap loadedNotebookMap, String autoSaveLocation)
//			throws NotebookDelegateException {
//		String recoverError = null;
//		try {
//			String tempPath = autoSaveLocation + File.separator;
//			File dir = new File(tempPath);
//			final String tempRef = nbPage.getNotebookRefAsString();
//			FilenameFilter filter = new FilenameFilter() {
//				public boolean accept(File dir, String name) {
//					return (name.endsWith(".SAV") && name.startsWith(tempRef));
//				}
//			};
//			String[] fileList = dir.list(filter);
//			if (fileList.length > 0) {
//				Arrays.sort(fileList, new AutoSaveComparator());
//				// Grab the last auto-save file to check modification times
//				File tempFile = new File(tempPath + fileList[fileList.length - 1]);
//				Date fileDate = new Date(tempFile.lastModified());
//				// Check the audit log dates - if any date is after the file
//				// modified date
//				// then don't recover from the file
//				Iterator tableIterator = loadedNotebookMap.entrySet().iterator();
//				while (tableIterator.hasNext()) {
//					RowSet rs = (RowSet) ((Map.Entry) tableIterator.next()).getValue();
//					rs.beforeFirst();
//					if (rs.next()) {
//						Clob auditLog = (Clob) rs.getClob("AUDIT_LOG");
//						StringReader reader = new StringReader(auditLog.getSubString(0, (int) auditLog.length()));
//						String strDate = JDomUtils.getElementValue(new SAXBuilder().build(reader), "Audit_Log/Last_Modified");
//						if (strDate != null
//								&& (new SimpleDateFormat("MMM dd, yyyy HH:mm:ss")).parse(strDate).compareTo(fileDate) >= 0)
//							return true; // don't load from the autosave
//						// file
//					}
//				}
//				// this is currently just recovering from the latest autosave
//				// file
//				// as autosaves are not currently incremental saves
//				// for (int i=0; i < fileList.length; i++) {
//				// File autoSaveFile = new File(tempPath + fileList[i]);
//				File autoSaveFile = tempFile;
//				if (autoSaveFile.exists()) {
//					FileInputStream fin = new FileInputStream(autoSaveFile);
//					// decode the byte array
//					Object savedObject = EncryptionUtil.decryptObjectFromFile(fin);
//					if (savedObject != null) {
//						HashMap savedMap = (HashMap) savedObject;
//						// create a new notebookcontext for the recovered
//						// rowsets
//						NotebookContext ctx = new NotebookContext();
//						// get the rowsets from the retrieved context and save
//						// to the loaded context
//						Iterator mapIterator = savedMap.keySet().iterator();
//						while (mapIterator.hasNext()) {
//							String tableName = (String) mapIterator.next();
//							RowSet rs = (RowSet) savedMap.get(tableName);
//							loadedNotebookMap.put(tableName, rs);
//							ctx.addTableName(CeNTableName.getInstance(tableName));
//						}
//						// update the model with the recovered data
//						ctx.setNotebookMap(savedMap);
//						getNotebookPageFromContext(ctx, nbPage);
//					} else {
//						recoverError = "Error in decrypting the autosave file " + autoSaveFile.getName();
//						// break;
//					}
//				} else {
//					recoverError = "Error in finding the autosave file " + autoSaveFile.getName();
//					// break;
//				}
//			}
//			// }
//		} catch (FileNotFoundException fnfe) {
//			// file not exists is already checked
//		} catch (Exception ioe) {
//			throw new NotebookDelegateException("Error recovering from autosave files", ioe);
//		}
//		if (recoverError != null)
//			throw new NotebookDelegateException(recoverError);
//		return true;
//	}

	private boolean setTablesInNotebookContext(NotebookContext nbContext, byte loadLevel) {
		boolean result = false;
		if (nbContext != null) {
			if ((loadLevel & CeNConstants.PAGE_BYTE_RETRIEVE_BRIEF_LEVEL) != 0) {
				nbContext.addTableName(CeNTableName.CEN_PAGES);
				// We don't want to pull PDF_DOCUMENT
				ArrayList fieldList = new ArrayList();
				fieldList.add("PDF_DOCUMENT");
				nbContext.addTableFieldExclusions(CeNTableName.CEN_PAGES, fieldList);
			}
			
			// if ((loadLevel & BYTE_RETRIEVE_CONTAINERS) != 0)
			// 		nbContext.addTableName(CeNTableName.CEN_CONTAINERS);
			//		    
			// if ((loadLevel & BYTE_RETRIEVE_TRANSACTIONS) != 0)
			// 		nbContext.addTableName(CeNTableName.CEN_TRANSACTIONS);
			
			if ((loadLevel & CeNConstants.PAGE_BYTE_RETRIEVE_ATTACHMENTS) != 0) {
				nbContext.addTableName(CeNTableName.CEN_ATTACHMENTS);
				
				// Initially we don't want to pull back the Blob data for the attachments
				// check if there are any query constraints for this table
				if (nbContext.getTableKeyConstraint(CeNTableName.CEN_ATTACHMENTS.toString()) == null) {
					ArrayList fieldList = new ArrayList();
					fieldList.add("BLOB_DATA");
					nbContext.addTableFieldExclusions(CeNTableName.CEN_ATTACHMENTS, fieldList);
				}
				
				// We will get an extra attachments rowset, which will contain the blob field, but
				// the rowset will be blank. This will be used to add new attachments for saving to the db.
				nbContext.addTableName("ADD_CEN_ATTACHMENTS", CeNTableName.CEN_ATTACHMENTS);
				HashMap tableConstraint = new HashMap();
				tableConstraint.put("ADD_CEN_ATTACHMENTS", "");
				nbContext.setTableKeyConstraints(tableConstraint);
			}
			
			if ((loadLevel & CeNConstants.PAGE_BYTE_RETRIEVE_BATCHES) != 0) {
				nbContext.addTableName(CeNTableName.CEN_STRUCTURES);
				nbContext.addTableName(CeNTableName.CEN_BATCHES);
			}
			
			if ((loadLevel & CeNConstants.PAGE_BYTE_RETRIEVE_ANALYTICAL) != 0)
				nbContext.addTableName(CeNTableName.CEN_ANALYSIS);

			if ((loadLevel & CeNConstants.PAGE_BYTE_RETRIEVE_RXNS) != 0) {
				nbContext.addTableName(CeNTableName.CEN_REACTION_STEPS);
				nbContext.addTableName(CeNTableName.CEN_REACTION_SCHEMES);
			}

			result = true;
		}
		return result;
	}

	private boolean updateRowSetsInContext(NotebookPage nbPage, NotebookContext nbContext) throws NotebookDelegateException {
		boolean result = true;
		// get the page rowset back from the context
		if (nbContext != null) {
			try {
				if (nbContext.getTableNames().containsKey(CeNTableName.CEN_PAGES.toString())) {
					if (!updatePage(nbContext, nbPage))
						result = false;
				}
				if (result && nbContext.getTableNames().containsKey(CeNTableName.CEN_BATCHES.toString())) {
					if (!updateBatches(nbContext, nbPage))
						result = false;
				}
				if (result && nbContext.getTableNames().containsKey(CeNTableName.CEN_REACTION_SCHEMES.toString())) {
					if (!updateReactions(nbContext, nbPage))
						result = false;
				}
				if (result && nbContext.getTableNames().containsKey(CeNTableName.CEN_ANALYSIS.toString())) {
					if (!updateAnalysis(nbContext, nbPage))
						result = false;
				}
				if (result && nbContext.getTableNames().containsKey(CeNTableName.CEN_ATTACHMENTS.toString())) {
					if (!updateAttachments(nbContext, nbPage))
						result = false;
				}
			} catch (StorageInitException e) {
				throw new NotebookDelegateException("Could not initialize StorageDelegate.", e);
			} catch (StorageAccessException e1) {
				throw new NotebookDelegateException("Could not access Middle Tier.", e1);
			} catch (Exception e) {
				throw new NotebookDelegateException(e.getMessage(), e);
			}
		}
		return result;
	}

	private NotebookPage getNotebookPageFromContext(NotebookContext nbContext, NotebookPage result)
			throws NotebookDelegateException {
		// If we have a context we might have top level info of notebook page.
		if (nbContext != null && nbContext.getTableNames().size() > 0) {
			// Fills NotebookPage with data from the rowset returned
			if (nbContext.getTableNames().containsKey(CeNTableName.CEN_PAGES.toString())) {
				if (!processPage(nbContext, result)) {
					throw new NotebookDelegateException("Failed to process notebook page information.");
				}
			}
			// and it has some tables in the table names field then we have
			// something to load.
			if (nbContext.getTableNames().containsKey(CeNTableName.CEN_BATCHES.toString())) {
				if (!processBatches(nbContext, result)) {
					throw new NotebookDelegateException("Failed to process notebook batch information.");
				}
			}
			if (nbContext.getTableNames().containsKey(CeNTableName.CEN_REACTION_SCHEMES.toString())) {
				if (!processReactions(nbContext, result)) {
					throw new NotebookDelegateException("Failed to process reaction scheme information.");
				}
			}
			if (nbContext.getTableNames().containsKey(CeNTableName.CEN_ANALYSIS.toString())) {
				if (!processAnalysis(nbContext, result)) {
					throw new NotebookDelegateException("Failed to process analysis information.");
				}
			}
			if (nbContext.getTableNames().containsKey(CeNTableName.CEN_ATTACHMENTS.toString())
					|| nbContext.getTableNames().containsKey(CeNTableName.ADD_CEN_ATTACHMENTS.toString())) {
				if (!processAttachments(nbContext, result)) {
					throw new NotebookDelegateException("Failed to process attachment information.");
				}
			}
		} else {
			throw new NotebookDelegateException("No information returned from query.");
		}
		return result;
	}

	private boolean processPage(NotebookContext nbContext, NotebookPage nbPage) throws NotebookDelegateException {
		log.debug("processPage - Enter");
		RowSet rs = null;
		boolean result = false;
		HashMap hm = nbContext.getNotebookMap();
		// Load toplevel NB info
		rs = (RowSet) hm.get(CeNTableName.CEN_PAGES.toString());
		try {
			result = NotebookPageHandler.getInstance().processNotebookPage(rs, nbPage, nbPage.isLoading());
		} catch (Exception e) {
			throw new NotebookDelegateException("Failed to process notebook page information.", e);
		}
		log.debug("processPage - Exit");
		return result;
	}

	private boolean processBatches(NotebookContext nbContext, NotebookPage nbPage) throws NotebookDelegateException {
		log.debug("processBatches - Enter");
		boolean result = false;
		// Load batch level info
		RowSet rsBatch = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_BATCHES.toString());
		RowSet rsStructures = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_STRUCTURES.toString());
		try {
			result = BatchHandler.getInstance().processBatches(rsBatch, nbPage, nbPage.isLoading());
			if (rsStructures != null && result && nbPage.getBatchCache() != null) {
				result = StructureHandler.getInstance().processStructures(rsStructures, nbPage.getBatchCache().getMap(),
						nbPage.isLoading());
			}
		} catch (SQLException e) {
			throw new NotebookDelegateException("Failed to process notebook batch information.", e);
		} catch (Exception e2) {
			throw new NotebookDelegateException("Failed to process notebook batch information.", e2);
		}
		log.debug("processBatches - Exit");
		return result;
	}

	private boolean processReactions(NotebookContext nbContext, NotebookPage nbPage) throws NotebookDelegateException {
		log.debug("processReactions - Enter");
		boolean result = true;
		// Load reaction info
		RowSet rsReactionSchemes = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_REACTION_SCHEMES.toString());
		RowSet rsReactionSteps = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_REACTION_STEPS.toString());
		try {
			if (rsReactionSteps != null) {
				result = ReactionStepHandler.getInstance().processReactionSteps(rsReactionSteps, nbPage, nbPage.isLoading());
			}
			if (result) {
				result = ReactionSchemeHandler.getInstance().processReactionSchemes(rsReactionSchemes, nbPage, nbPage.isLoading());
			}
		} catch (Exception e) {
			throw new NotebookDelegateException("Failed to process reaction scheme information.", e);
		}
		log.debug("processReactions - Exit");
		return result;
	}

	private boolean processAnalysis(NotebookContext nbContext, NotebookPage nbPage) throws NotebookDelegateException {
		log.debug("processAnalysis - Enter");
		boolean result = true;
		// Load reaction info
		RowSet rsAnalysis = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_ANALYSIS.toString());
		try {
			result = AnalysisHandler.getInstance().processAnalysis(rsAnalysis, nbPage, nbPage.isLoading());
		} catch (Exception e) {
			throw new NotebookDelegateException("Failed to process analysis information.", e);
		}
		log.debug("processAnalysis - Exit");
		return result;
	}

	private boolean processAttachments(NotebookContext nbContext, NotebookPage nbPage) throws NotebookDelegateException {
		log.debug("processAttachments - Enter");
		boolean result = true;
		// Load Attachment info
		RowSet rsAttachments = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_ATTACHMENTS.toString());
		RowSet rsAddAttachments = (RowSet) nbContext.getNotebookMap().get("ADD_CEN_ATTACHMENTS");
		try {
			result = AttachmentHandler.getInstance().processAttachment(rsAttachments, nbPage, nbPage.isLoading());
			if (rsAddAttachments != null) {
				result = AttachmentHandler.getInstance().processAttachment(rsAddAttachments, nbPage, nbPage.isLoading());
			}
		} catch (Exception e) {
			throw new NotebookDelegateException("Failed to process analysis information.", e);
		}
		log.debug("processAttachments - Exit");
		return result;
	}

	private boolean updatePage(NotebookContext nbContext, NotebookPage nbPage) throws Exception {
		boolean result = true;
		// check the page object to see if it has changed
		if (nbPage.isModified()) {
			result = false;
			RowSet rs = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_PAGES.toString());
			try {
				if (rs != null) {
					result = NotebookPageHandler.getInstance().updateNotebookPage(rs, nbPage);
				}
			} catch (Exception e) {
				throw new Exception(e.getMessage(), e);
			}
		}
		return result;
	}

	private boolean updateBatches(NotebookContext nbContext, NotebookPage nbPage) throws Exception {
		boolean result = false;
		RowSet rsBatches = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_BATCHES.toString());
		RowSet rsStructures = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_STRUCTURES.toString());
		try {
			if (rsBatches != null) {
				result = BatchHandler.getInstance().updateBatches(rsBatches, rsStructures, nbPage);
				if (result) {
					result = StructureHandler.getInstance().updateStructures(rsStructures, nbPage);
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
		return result;
	}

	private boolean updateReactions(NotebookContext nbContext, NotebookPage nbPage) throws Exception {
		boolean result = false;
		RowSet rsSchemes = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_REACTION_SCHEMES.toString());
		RowSet rsSteps = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_REACTION_STEPS.toString());
		try {
			if (rsSteps != null) {
				result = ReactionStepHandler.getInstance().updateReactionSteps(rsSteps, nbPage.getReactionCache(), nbPage);
				if (result && rsSchemes != null) {
					result = ReactionSchemeHandler.getInstance()
							.updateReactionSchemes(rsSchemes, nbPage.getReactionCache(), nbPage);
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
		return result;
	}

	private boolean updateAnalysis(NotebookContext nbContext, NotebookPage nbPage) throws Exception {
		boolean result = false;
		RowSet rsAnalysis = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_ANALYSIS.toString());
		try {
			if (rsAnalysis != null) {
				result = AnalysisHandler.getInstance().updateAnalysis(rsAnalysis, nbPage);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
		return result;
	}

	private boolean updateAttachments(NotebookContext nbContext, NotebookPage nbPage) throws Exception {
		boolean result = false;
		RowSet rsAttachments = (RowSet) nbContext.getNotebookMap().get(CeNTableName.CEN_ATTACHMENTS.toString());
		try {
			if (rsAttachments != null) {
				result = AttachmentHandler.getInstance().updateAttachment(rsAttachments, nbPage);
				// Do this with the additions rowset as well, in case any were
				// removed
				if (result) {
					
					RowSet rsNewAttachments = (RowSet) nbContext.getNotebookMap().get("ADD_CEN_ATTACHMENTS");
					if (rsNewAttachments == null) return result;
					
					rsNewAttachments.afterLast();
					
					if (rsNewAttachments.getRow() > 0) {
						result = AttachmentHandler.getInstance().updateAttachment(rsNewAttachments, nbPage);
					}
					// Need to handle additions separately as rowset columns
					// are
					// different
					if (result) {
						rsAttachments.afterLast();
						if (nbPage.getAttachmentCache().size() > rsAttachments.getRow() + rsNewAttachments.getRow()) {
							result = AttachmentHandler.getInstance().addNewAttachments(nbPage, rsNewAttachments);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 
	 * @param siteCode
	 * @param notebook
	 * @param experiment
	 * @param pageVersion
	 * @return
	 * @throws NotebookDelegateException
	 */
	public ValidationInfo validateNotebook(String siteCode, String notebook, String experiment, String pageVersion) throws NotebookDelegateException {
		log.debug("validateNotebook - Enter");
		ValidationInfo result = null;
		try {
			result = storageDelegate.validateNotebook(siteCode, notebook, experiment, pageVersion);
		} catch (StorageException e1) {
			throw new NotebookDelegateException("Could not access Middle Tier.", e1);
		}
		log.debug("validateNotebook - Exit");
		return result;
	}

	/**
	 * 
	 * @param siteCode
	 * @param notebook
	 * @param experiment
	 * @return
	 * @throws NotebookDelegateException
	 */
	public ValidationInfo validateNotebook(String siteCode, String notebook, String experiment) throws NotebookDelegateException {
		return validateNotebook(siteCode, notebook, experiment, null);
	}

	/**
	 * 
	 * @param siteCode
	 * @param notebook
	 * @return
	 * @throws StorageAccessException
	 * @throws NotebookInvalidException
	 */
	public int getNextExperimentForNotebook(String siteCode, String notebook) throws StorageAccessException,
			NotebookInvalidException {
		try {
			return storageDelegate.getNextExperimentForNotebook(notebook);
		} catch (StorageException e) {
			throw new StorageAccessException(e);
		}
	}

	/**
	 * 
	 * @param userIDs
	 * @return
	 * @throws StorageAccessException
	 */
	public String[] getUsersFullName(String[] userIDs) throws StorageAccessException {
		log.debug("getUsersFullName - Enter");
		if (userIDs == null || userIDs.length == 0) {
			log.debug("getUsersFullName - Exit");
			return null;
		}
		log.debug("getUsersFullName - Exit");
		try {
			return storageDelegate.getUsersFullName(userIDs);
		} catch (StorageException e) {
			throw new StorageAccessException(e);
		}
	}

	/**
	 * 
	 * @param notebook
	 * @throws StorageAccessException
	 * @throws NotebookInvalidException
	 * @throws NotebookExistsException
	 */
	public void createNotebook(String notebook) throws StorageAccessException, NotebookInvalidException, NotebookExistsException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("NotebookDelegate.createNotebook");
		try {
			storageDelegate.createNotebook(notebook, MasterController.getUser().getSessionIdentifier());
		} catch (StorageException e) {
			throw new StorageAccessException(e);
		}
		stopwatch.stop();
	}

	/**
	 * 
	 * @param siteCode
	 * @param userId
	 * @param notebook
	 * @throws StorageAccessException
	 * @throws NotebookInvalidException
	 * @throws NotebookExistsException
	 */
	public void createNotebook(String siteCode, String userId, String notebook) throws StorageAccessException,
			NotebookInvalidException, NotebookExistsException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("NotebookDelegate.createNotebook, " + siteCode + ", " + userId + ", " + notebook);
		try {
			storageDelegate.createNotebook(siteCode, userId, notebook, MasterController.getUser().getSessionIdentifier());
		} catch (StorageException e) {
			throw new StorageAccessException(e);
		}
		stopwatch.stop();
	}

	private class AutoSaveComparator implements Comparator {
		// Auto Save Filenames are in the following format 12345678-1234x.SAV
		// where x is 123 to start but will expand to 1234 should it exceed 3
		// digits.
		// Comparator interface requires defining compare method.
		public int compare(Object a, Object b) {
			String aStr = ((String) a).substring(13); // Strip off Notebook
			// Reference (8-4)
			String bStr = ((String) b).substring(13); // Strip off Notebook
			// Reference (8-4)
			// Strip off the .SAV
			int i = aStr.indexOf(".");
			if (i != -1)
				aStr = aStr.substring(0, i);
			i = bStr.indexOf(".");
			if (i != -1)
				bStr = bStr.substring(0, i);
			// Parse the result as an integer so it can be compared
			// numerically
			// vs as a string
			int aInt = Integer.parseInt(aStr);
			int bInt = Integer.parseInt(bStr);
			if (aInt > bInt)
				return 1;
			else if (aInt < bInt)
				return -1;
			else
				return 0;
		}
	}

	private NotebookPage getNotebookPageFromPageModel(NotebookPageModel model, NotebookPage result)
			throws NotebookDelegateException {
		if (model != null) {
		} else {
			throw new NotebookDelegateException("No information returned from query.");
		}
		return result;
	}

	private boolean processPageHeaderInfo(NotebookPageModel model, NotebookPage nbPage) throws NotebookDelegateException {
		log.debug("processPageHeader - Enter");
		boolean result = false;
		log.debug("processPageHeader - Exit");
		return result;
	}
}
