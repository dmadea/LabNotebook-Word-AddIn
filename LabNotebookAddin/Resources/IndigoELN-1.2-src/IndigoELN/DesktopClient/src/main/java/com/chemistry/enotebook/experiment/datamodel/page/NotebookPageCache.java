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
package com.chemistry.enotebook.experiment.datamodel.page;

import com.chemistry.enotebook.client.controller.MasterController;
import com.chemistry.enotebook.client.delegate.NotebookDelegate;
import com.chemistry.enotebook.client.delegate.NotebookDelegateException;
import com.chemistry.enotebook.domain.CeNConstants;
import com.chemistry.enotebook.experiment.datamodel.user.NotebookUser;
import com.chemistry.enotebook.session.security.SessionIdentifier;
import com.chemistry.enotebook.util.Stopwatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/* 
 * NotebookPageCache.java
 * 
 * Created on Aug 5, 2004
 *
 * 
 */
/**
 * 
 * @date Aug 5, 2004
 * 
 * Relies on NotebookUser object
 */
public class NotebookPageCache {
	private static final Log log = LogFactory.getLog(NotebookPageCache.class);
	private Map notebooks = null;
	private Object objLatch;
	private static String rootDirectory = MasterController.getApplicationDirectory();
	private static String autoSaveDirName = "AutoSave";
	private static String offlineAccessDirName = "OfflineAccess";
	private static int MAX_AUTOSAVE_AGE_IN_DAYS = 30;

	public NotebookPageCache() throws NotebookDelegateException {
		if (notebooks == null)
			notebooks = Collections.synchronizedMap(new HashMap());
		objLatch = null;
	}

	protected void finalize() throws Throwable {
		super.finalize();
		dispose();
	}

	public void dispose() {
		if (notebooks != null)
			notebooks.clear();
		notebooks = null;
		objLatch = null;
	}

	public boolean hasPage(String siteCode, NotebookRef nbRef, int version) {
		if (notebooks != null)
			return notebooks.containsKey(siteCode + nbRef.getNotebookRef() + "v" + version);
		else
			return false;
	}

	public NotebookPage getCachedPage(String siteCode, NotebookRef nbRef, int version) {
		if (hasPage(siteCode, nbRef, version))
			return (NotebookPage) notebooks.get(siteCode + nbRef.getNotebookRef() + "v" + version);
		else
			return null;
	}

	public NotebookPage getNotebookPage(String siteCode, NotebookRef nbRef, int version, boolean blnLoadAutoSaves)
			throws NotebookDelegateException {
		return getNotebookPage(siteCode, nbRef, version, blnLoadAutoSaves, CeNConstants.PAGE_BYTE_RETRIEVE_ALL);
	}

	public NotebookPage getNotebookPage(String siteCode, NotebookRef nbRef, int version, boolean blnLoadAutoSaves, byte loadLevel)
			throws NotebookDelegateException {
		String requestedPage = siteCode + ", " + nbRef + ", v" + version;
		log.debug(requestedPage + " requested");
		// Checks for page in cache or loads new page
		if (hasPage(siteCode, nbRef, version)) {
			NotebookPage nbPage = (NotebookPage) notebooks.get(siteCode + nbRef.getNotebookRef() + "v" + version);
			if (nbPage != null) {
				log.debug("Cache exists for " + requestedPage);
				return nbPage;
			}
		}
		log.debug("Loading " + requestedPage);
		return loadNotebookPage(siteCode, nbRef, version, blnLoadAutoSaves, loadLevel);
	}

	public boolean addNotebookPage(NotebookPage nbPage) {
		// Checks for page in cache or loads new page
		if (getCachedPage(nbPage.getSiteCode(), nbPage.getNotebookRef(), nbPage.getVersion()) != null)
			return false;
		else {
			notebooks.put(nbPage.getSiteCode() + nbPage.getNotebookRef().getNotebookRef() + "v" + nbPage.getVersion(), nbPage);
			return true;
		}
	}

	public void reserveNotebookPage(String siteCode, NotebookRef nbRef, int version) {
		notebooks.put(siteCode + nbRef.getNotebookRef() + "v" + version, null);
	}

	private NotebookPage loadNotebookPage(String siteCode, NotebookRef nbRef, int version, boolean blnLoadAutosaves,
			byte loadOptions) throws NotebookDelegateException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start("loadNotebookPage, " + siteCode + ", " + nbRef + ", " + version);
		NotebookPage nbPage = null;
		try {
			NotebookUser user = MasterController.getUser();
			NotebookDelegate nbDelegate = new NotebookDelegate(new SessionIdentifier(user.getSiteCode(), user.getNTUserID(), user
					.getSessionIdentifier().getTokenString(), false));
			if (blnLoadAutosaves) {
				nbPage = nbDelegate.load(siteCode, nbRef, version, loadOptions, getAutoSaveLocation(), MasterController.getUser()
						.isSuperUser());
			} else {
				nbPage = nbDelegate.load(siteCode, nbRef, version, loadOptions, MasterController.getUser().isSuperUser());
			}
			if (nbPage != null) { // Add the page to the list
				notebooks.put(nbPage.getSiteCode() + nbPage.getNotebookRef().getNotebookRef() + "v" + nbPage.getVersion(), nbPage);
			} else {
				removeNotebookPage(siteCode, nbRef, version);
			}
		} catch (Exception e) {
			removeNotebookPage(siteCode, nbRef, version);
			throw new NotebookDelegateException(e.getMessage(), e);
		}
		stopwatch.stop();
		return nbPage;
	}

	public void loadNotebookPageData(NotebookPage nbPage, byte loadLevel, HashMap tableKeys) throws NotebookDelegateException {
		try {
			NotebookUser user = MasterController.getUser();
			NotebookDelegate nbDelegate = new NotebookDelegate(new SessionIdentifier(user.getSiteCode(), user.getNTUserID(), user
					.getSessionIdentifier().getTokenString(), false));
			nbDelegate.loadConstrainedPageData(nbPage, loadLevel, tableKeys, MasterController.getUser().isSuperUser());
		} catch (Exception e) {
			throw new NotebookDelegateException(e.getMessage(), e);
		}
	}

	public Collection getLoadedNotebookPages() {
		return Collections.synchronizedCollection(notebooks.values());
	}

	public boolean saveNotebookPage(NotebookPage nbPage, boolean autoSave) throws NotebookDelegateException {
		boolean status = false;
		NotebookUser user = MasterController.getUser();
		if (user != null && user.getSessionIdentifier() != null) {
			try {
				NotebookDelegate nbDelegate = null;
				if (autoSave) {
					nbDelegate = new NotebookDelegate();
				} else {
					nbDelegate = new NotebookDelegate(new SessionIdentifier(user.getSiteCode(), user.getNTUserID(), user
							.getSessionIdentifier().getTokenString(), false));
				}
				if (autoSave)
					status = nbDelegate.save(nbPage, getAutoSaveLocation());
				else {
					status = nbDelegate.save(nbPage, objLatch);
					if (status) // remove all autosaved pages
						deleteAutoSaveFiles(nbPage.getNotebookRefAsString());
				}
			} catch (Exception e) {
				throw new NotebookDelegateException(e.getMessage(), e);
			}
		}
		return status;
	}

	public boolean saveAllNotebookPages(boolean autoSave) throws NotebookDelegateException {
		boolean status = true;
		HashMap pages = getNotebookMapCopy();
		for (Iterator iP = pages.keySet().iterator(); iP.hasNext();) {
			NotebookPage page = (NotebookPage) pages.get((String) iP.next());
			if (page != null && page.isPageEditable() && page.isModelChanged())
				if (!saveNotebookPage(page, autoSave))
					status = false;
		}
		return status;
	}

	public void removeNotebookPage(String siteCode, NotebookRef nbRef, int version) {
		if (hasPage(siteCode, nbRef, version))
			notebooks.remove(siteCode + nbRef.getNotebookRef() + "v" + version);
	}

	public static boolean autoSavesExist(NotebookRef nbRef) throws Exception {
		String tempPath = getAutoSaveLocation() + File.separator;
		File dir = new File(tempPath);
		final String tempRef = nbRef.getNbRef();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.endsWith(".SAV") && name.startsWith(tempRef));
			}
		};
		File[] files = dir.listFiles(filter);
		if (files.length > 0)
			return true;
		else
			return false;
	}

	public static boolean deleteAutoSaveFiles(NotebookRef nbRef) throws NotebookDelegateException {
		return deleteAutoSaveFiles(nbRef.getNotebookRef());
	}

	public static boolean deleteAutoSaveFiles(String nbRef) throws NotebookDelegateException {
		boolean result = false;
		try {
			String tempPath = getAutoSaveLocation();
			File dir = new File(tempPath);
			final String temp = nbRef;
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (name.endsWith(".SAV") && name.startsWith(temp));
				}
			};
			File[] files = dir.listFiles(filter);
			for (int i = 0; i < files.length; i++)
				files[i].delete();
			result = true;
		} catch (Exception e) {
			throw new NotebookDelegateException("Unable to delete autosave files for " + nbRef, e);
		}
		return result;
	}

	public static void deleteOldOfflineAccessFiles() {
		// clean up files of a certain age

		try {
			File dir = new File(getOfflineAccessLocation());
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				long timeStampAge = System.currentTimeMillis() - files[i].lastModified();
				timeStampAge = timeStampAge / (24L * 60L * 60L * 1000L);
				if (timeStampAge > MAX_AUTOSAVE_AGE_IN_DAYS) {
					files[i].delete();
					log.debug("deleting offline access file + " + files[i].getAbsolutePath());
				}

			}
		} catch (Exception e) {
			// log error but fail silently
			log.error(e);
		}

	}
	public static void setRootDirectory(String newDirectoryPath) throws Exception {
		if (validateDirectory(newDirectoryPath))
			rootDirectory = newDirectoryPath;
	}
	
	public static String getRootDirectory() {
		return rootDirectory;
	}

	/**
	 * Side effect is that the directory is created.
	 * 
	 * @param path -
	 *            directory path to validate/create
	 * @return - true if the path exists or could be created
	 */
	private static boolean validateDirectory(String path) throws Exception {
		boolean result = false;
		if (path != null && path.length() > 0) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (dir.mkdir())
					result = true;
			} else
				result = true;
		}
		return result;
	}

	public static void setAutoSaveDirectoryName(String newDirectoryName) throws Exception {
		if (validateDirectory(rootDirectory))
			if (validateDirectory(rootDirectory + File.separator + newDirectoryName))
				autoSaveDirName = newDirectoryName;
	}

	public static String getOfflineAccessLocation() throws Exception {
		String path = rootDirectory + File.separator + offlineAccessDirName;
		if (!validateDirectory(path))
			throw new Exception("Could not create directory " + path);
		return path;
	}

	private static String getAutoSaveLocation() throws Exception {
		String path = rootDirectory + File.separator + autoSaveDirName;
		if (!validateDirectory(path))
			throw new Exception("Could note create directory " + path);
		return path;
	}

	public void setObjectLatch(Object latch) {
		objLatch = latch;
	}

	/**
	 * This method should be threaded. _existsInDB should be the boolean used to determine this fact. Not a function call that can
	 * waste cycles.
	 * 
	 * @param nbPage
	 * @return
	 * @throws NotebookDelegateException
	 */
	public boolean isDBExperimentModified(NotebookPage nbPage) throws NotebookDelegateException {
		boolean modified = false;
		try {
			NotebookUser user = MasterController.getUser();
			NotebookDelegate nbDelegate = new NotebookDelegate(new SessionIdentifier(user.getSiteCode(), user.getNTUserID(), user
					.getSessionIdentifier().getTokenString(), false));
			modified = nbDelegate.isNbDbPageUpdated(nbPage);
		} catch (Exception e) {
			throw new NotebookDelegateException(e.getMessage(), e);
		}
		return modified;
	}

	public HashMap getNotebookMapCopy() {
		return (HashMap) (new HashMap(notebooks)).clone();
	}
}
