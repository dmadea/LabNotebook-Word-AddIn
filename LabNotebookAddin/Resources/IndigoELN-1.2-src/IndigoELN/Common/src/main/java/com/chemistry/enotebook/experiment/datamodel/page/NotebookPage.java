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
 * Created on 12-Jul-2004
 *
 * This class should hold all of the data for a notebook page
 * and have getters and setters for each lump of data
 *
 */
package com.chemistry.enotebook.experiment.datamodel.page;

import com.chemistry.enotebook.domain.NotebookPageModel;
import com.chemistry.enotebook.experiment.common.PersistableObject;
import com.chemistry.enotebook.experiment.common.interfaces.AutoCalc;
import com.chemistry.enotebook.experiment.common.interfaces.DeepClone;
import com.chemistry.enotebook.experiment.common.interfaces.DeepCopy;
import com.chemistry.enotebook.experiment.datamodel.analytical.AnalysisCache;
import com.chemistry.enotebook.experiment.datamodel.attachments.AttachmentCache;
import com.chemistry.enotebook.experiment.datamodel.batch.*;
import com.chemistry.enotebook.experiment.datamodel.reaction.ReactionCache;
import com.chemistry.enotebook.experiment.utils.NotebookPageUtil;

import java.text.ParseException;
import java.util.*;

public class NotebookPage extends PersistableObject implements Observer, AutoCalc, DeepCopy, DeepClone {
	
	private static final long serialVersionUID = -7777364384848644602L;
	
	public static final String STATUS_OPEN = "OPEN";
	public static final String STATUS_COMPLETE = "COMPLETE";
	public static final String STATUS_SUBMITTED = "SUBMITTED";
	public static final String STATUS_SUBMIT_FAILED = "SUBMIT_FAIL";
	public static final String STATUS_SIGNED = "SIGNED";
	public static final String STATUS_ARCHIVING = "ARCHIVING";
	public static final String STATUS_ARCHIVE_FAILED = "ARCHIVE_FAIL";
	public static final String STATUS_ARCHIVED = "ARCHIVED";

	public static final String STOICH_TABLE = "<Reactants_Table>";
	public static final String INTENDED_PRODUCTS_TABLE = "<Intended_Products_Table>";
	public static final String STOICH_TABLE_END = "</Reactants_Table>";
	public static final String INTENDED_PRODUCTS_TABLE_END = "</Intended_Products_Table>";

	public static final String TYPE_MED_CHEM = "MED-CHEM";
	public static final String TYPE_PARALLEL = "PARALLEL";
	public static final String TYPE_CONCEPTION = "CONCEPTION";

	public static final int COPY_LEVEL_TOP = 0; // only those items in this object. Shallow copy - no objects other than strings
	public static final int COPY_LEVEL_NBREF = 1; // inlcude TOP and notebook ref
	public static final int COPY_LEVEL_REACTION = 2; // include NBREF and all reaction transaction and batch info. BatchNumbers
	// change
	public static final int COPY_LEVEL_ATTACHMENTS = 4; // all items including attachments
	public static final int COPY_LEVEL_CLONE_ACTUAL_BATCHES = 8; // include actual batches
	public static final int COPY_LEVEL_CLONE_ANALYTICAL = 16; // include analytical
	public static final int COPY_LEVEL_RETAIN_REGISTRATION_STATUS = 32; // retain registration status of batches.

	public static final int COPY_LEVEL_VERSION_EXPERIMENT = 31; // include all batch info, and reg info, but set reg info to not
	// registered.
	public static final int COPY_LEVEL_COMPLETE = 63; // all items including attachments and registration status

	private NotebookRef _nbRef = null;
	private int _version = 1;
	private String _latestVersion = "Y";

	private String _userNTID = ""; // Used to identifiy person repsonsible for the page and who is able to edit.
	private String _procedure = "";
	private int _procedureWidth = 0;
	private String _siteCode = "";
	private String _owner = ""; // Not used in CeN. Kept for legacy system: Chemistry Workbook and Chemical Notebook.
	private String _laf = "";
	private String _status = "";

	private String _cenVersion = "";
	private String _subject = "";
	private String _literatureRef = "";
	private String _taCode = "";
	private String _projectCode = "";
	private String _creationDate = "";
	private String _completionDate = "";
	private String _modificationDate = "";
	private String _continuedFromRxn = "";
	private String _continuedToRxn = "";
	private String _projectAlias = "";
	private String _protocolID = "";
	// private String _sourceCode = "";
	// private String _sourceDetailCode = "";

	private String _tableProperties = "";

	private String _spid = "";

	private String _ussiKey = "";
	private String _archiveDate = "";
	private String _signatureUrl = "";

	private boolean _autoCalcOn = true;

	private transient boolean _pageEditable = false;

	private transient ReactionCache _reactions;
	private transient BatchCache _batchList;
	private transient AnalysisCache _analyses;
	private transient AttachmentCache _attachments;

	// Notebook Context contains the query & result sets for this page and is
	// used when this page is persisted.
	// private transient NotebookContext _nbCtxt;

	// Holds modified objects
	private transient LinkedHashSet _modifiedObjects = new LinkedHashSet();

	private transient String _dbLastModifiedDate;

	private NotebookPageModel pageModelPojo;

	public NotebookPageModel getPageModelPojo() {
		return pageModelPojo;
	}

	public void setPageModelPojo(NotebookPageModel pageModelPojo) {
		this.pageModelPojo = pageModelPojo;
	}

	//
	// Constructors
	// 
	public NotebookPage(boolean readOnly) {
		super();
		_pageEditable = !readOnly;

		_reactions = new ReactionCache();
		_batchList = new BatchCache();
		_attachments = new AttachmentCache();
		_analyses = new AnalysisCache();
		_reactions.addObserver(this);
		_batchList.addObserver(this);
		_attachments.addObserver(this);
		_analyses.addObserver(this);
	}

	public NotebookPage(String nbRef, boolean readOnly) throws InvalidNotebookRefException {
		this(readOnly);
		_nbRef = new NotebookRef(nbRef);
		// pageModelPojo = ServiceController.getStorageService().;
		// pageModelPojo =
	}

	public NotebookPage(NotebookRef nbRef, boolean readOnly) {
		this(readOnly);
		_nbRef = nbRef;
		// pageModelPojo = ServiceController.getStorageService().getNotebookPageExperimentInfo(_nbRef, null);
	}

	public void dispose() throws Throwable {
		if (_reactions != null)
			_reactions.dispose();
		_reactions = null;
		if (_batchList != null)
			_batchList.dispose();
		_batchList = null;
		if (_attachments != null)
			_attachments.dispose();
		_attachments = null;
		if (_analyses != null)
			_analyses.dispose();
		_analyses = null;
	}

	public boolean isPageEditable() {
		return (_pageEditable && _status.equals(NotebookPage.STATUS_OPEN));
	}

	// 
	// Getters / Setters
	// 

	/**
	 * @return Returns the NotebookRef's toString().
	 */
	public String getNotebookRefAsString() {
		if (_nbRef != null)
			return _nbRef.toString();
		return "";
	}

	/**
	 * @return Returns the NotebookRef.
	 */
	public NotebookRef getNotebookRef() {
		return _nbRef;
	}

	/**
	 * @param key
	 *            The NotebookRef object to set.
	 */
	public void setNotebookRef(NotebookRef nbRef) {
		_nbRef = nbRef;
		setModified(true);
	}

	/**
	 * @param key
	 *            The notebook reference number as a string to set.
	 */
	public void setNotebookRef(String nbRef) throws InvalidNotebookRefException {
		setNotebookRef(new NotebookRef(nbRef));
	}

	/**
	 * @return Returns the Procedure.
	 */
	public String getProcedure() {
		return _procedure;
	}

	/**
	 * @param procedure
	 *            The Procedure to set.
	 */
	public void setProcedure(String procedure) {
		if (procedure == null) procedure = "";
		if (!_procedure.equals(procedure)) {
			this._procedure = procedure;
			setModified(true);
		}
	}

	/**
	 * @return Returns the Procedure Screen Width.
	 */
	public int getProcedureWidth() {
		return _procedureWidth;
	}

	/**
	 * @param procedure
	 *            The Procedure Screen Width to set.
	 */
	public void setProcedureWidth(int procedureWidth) {
		if (_procedureWidth != procedureWidth) {
			_procedureWidth = procedureWidth;
			setModified(true);
		}
	}

	/**
	 * @return Returns the SiteCode.
	 */
	public String getSiteCode() {
		return _siteCode;
	}

	/**
	 * @param code
	 *            The SiteCode to set.
	 */
	public void setSiteCode(String code) {
		if (code == null) code = "";
		if (!_siteCode.equals(code)) {
			_siteCode = code;
			setModified(true);
		}
	}

	/**
	 * @return Returns the UserNTID.
	 */
	public String getUserNTID() {
		return _userNTID;
	}

	/**
	 * @param _userntid
	 *            The UserNTID to set.
	 */
	public void setUserNTID(String userNTID) {
		if (userNTID == null) userNTID = "";
		userNTID = userNTID.toUpperCase();
		if (!_userNTID.equals(userNTID)) {
			_userNTID = userNTID;
			setModified(true);
		}
	}

	/**
	 * @return Returns the laf.
	 */
	public String getLaf() {
		return _laf;
	}

	/**
	 * @param look
	 *            and feel The look and feel to set (Med_Chem or Combi_Chem).
	 */
	public void setLaf(String laf) {
		if (laf == null) laf = "";
		if (!_laf.equals(laf)) {
			this._laf = laf;
			setModified(true);
		}
	}

	/**
	 * @return Returns the owner's NTID.
	 */
	public String getOwner() {
		return _owner;
	}

	/**
	 * @param owner
	 *            The owner user's NTID to set.
	 */
	public void setOwner(String owner) {
		if (owner == null) owner = "";
		if (!_owner.equals(owner)) {
			this._owner = owner;
			setModified(true);
		}
	}

	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return _status;
	}

	/**
	 * @param status
	 *            The status to set.
	 */
	public void setStatus(String status) {
		if (status == null) status = "";
		if (!_status.equals(status)) {
			this._status = status;
			setModified(true);
		}
	}

	public BatchCache getBatchCache() {
		return _batchList;
	}

	/**
	 * Used to tell when subObjects created should be calculating automatically or not. return boolean if autoCalc is being
	 * performed.
	 */
	public boolean isAutoCalcOn() {
		return _autoCalcOn;
	}

	/**
	 * @param boolean
	 *            autoCalc = true if you want the page to calculate values or false if no calcs are to be performed automatically
	 */
	public void setAutoCalcOn(boolean autoCalc) {
		_autoCalcOn = autoCalc;
		if (!isLoading()) {
			for (Iterator it = _batchList.getBatches().iterator(); it.hasNext();) {
				AbstractBatch ab = (AbstractBatch) it.next();
				ab.setAutoCalcOn(_autoCalcOn);
			}
		}
	}

	public AbstractBatch getBatch(String batchNumber) {
		return _batchList.getBatch(batchNumber);
	}

	public AbstractBatch getBatch(BatchNumber bn) {
		return _batchList.getBatch(bn);
	}

	public int getVersion() {
		return _version;
	}

	public void setVersion(int ver) {
		if (_version != ver) {
			_version = ver;
			setModified(true);
		}
	}

	public String getLatestVersion() {
		return _latestVersion;
	}

	public void setLatestVersion(String ver) {
		if (ver == null) ver = "";
		if (areStringsDifferent(_latestVersion, ver)) {
			_latestVersion = ver;
			setModified(true);
		}
	}

	public String getCenVersion() {
		return _cenVersion;
	}

	public void setCenVersion(String ver) {
		if (ver == null) ver = "";
		if (areStringsDifferent(_cenVersion, ver)) {
			_cenVersion = ver;
			setModified(true);
		}
	}

	public String getSubject() {
		return _subject;
	}

	public void setSubject(String subject) {
		if (subject == null) subject = "";
		if (areStringsDifferent(_subject, subject)) {
			_subject = subject;
			setModified(true);
		}
	}

	public String getLiteratureRef() {
		return _literatureRef;
	}

	public void setLiteratureRef(String litRef) {
		if (litRef == null) litRef = "";
		if (areStringsDifferent(_literatureRef, litRef)) {
			_literatureRef = litRef;
			setModified(true);
		}
	}

	public String getTaCode() {
		return _taCode;
	}

	public void setTaCode(String taCode) {
		if (taCode == null) taCode = "";
		if (areStringsDifferent(_taCode, taCode)) {
			_taCode = taCode;
			setModified(true);
		}
	}

	public String getProjectCode() {
		return _projectCode;
	}

	public void setProjectCode(String projCode) {
		if (projCode == null) projCode = "";
		if (areStringsDifferent(_projectCode, projCode)) {
			_projectCode = projCode;
			setModified(true);
		}
	}

	public String getCreationDate() {
		return _creationDate;
	}

	public void setCreationDate(String createDate) throws ParseException {
		if (createDate == null) createDate = "";
		if (areStringsDifferent(_creationDate, createDate)) {
			_creationDate = NotebookPageUtil.getLocalDateString(createDate);
			setModified(true);
		}
	}

	public String getCompletionDate() {
		return _completionDate;
	}

	public void setCompletionDate(String completeDate) throws ParseException {
		if (completeDate == null) completeDate = "";
		if (areStringsDifferent(_completionDate, completeDate)) {
			_completionDate = NotebookPageUtil.getLocalDateString(completeDate);
			setModified(true);
		}
	}

	public String getModificationDate() {
		return _modificationDate;
	}

	public void setModificationDate(String modDate) throws ParseException {
		if (modDate == null) modDate = "";
		if (areStringsDifferent(_modificationDate, modDate)) {
			_modificationDate = NotebookPageUtil.getLocalDateString(modDate);
			setModified(true);
			if (_dbLastModifiedDate == null)
				setDblastModificationDate(_modificationDate);
		}
	}

	public String getDbLastModificationDate() {
		return _dbLastModifiedDate;
	}

	public void setDblastModificationDate(String modDate) {
		_dbLastModifiedDate = modDate;
	}

	public String getContinuedFromRxn() {
		return _continuedFromRxn;
	}

	public void setContinuedFromRxn(String rx) {
		if (rx == null) rx = "";
		
		if (areStringsDifferent(_continuedFromRxn, rx)) {
			_continuedFromRxn = rx;
			setModified(true);
		}
	}

	public String getContinuedToRxn() {
		return _continuedToRxn;
	}

	public void setContinuedToRxn(String rx) {
		if (rx == null) rx = "";
		
		if (areStringsDifferent(_continuedToRxn, rx)) {
			_continuedToRxn = rx;
			setModified(true);
		}
	}

	public String getProjectAlias() {
		return _projectAlias;
	}

	public void setProjectAlias(String projAlias) {
		if (projAlias == null) projAlias = "";
		
		if (areStringsDifferent(_projectAlias, projAlias)) {
			_projectAlias = projAlias;
			setModified(true);
		}
	}

	public String getProtocolID() {
		return _protocolID;
	}

	public void setProtocolID(String id) {
		if (id == null) id = "";
		
		if (areStringsDifferent(_protocolID, id)) {
			_protocolID = id;
			setModified(true);
		}
	}

	// public String getSourceCode() { return _sourceCode; }
	// public void setSourceCode(String code) { if (areStringsDifferent(_sourceCode, code)) { _sourceCode = code; setModified(true);
	// } }
	//
	// public String getSourceDetailCode() { return _sourceDetailCode; }
	// public void setSourceDetailCode(String code) { if (areStringsDifferent(_sourceDetailCode, code)) { _sourceDetailCode = code;
	// setModified(true); } }

	public String getSpid() {
		return _spid;
	}

	public void setSpid(String spid) {
		if (spid == null) spid = "";
		
		if (areStringsDifferent(_spid, spid)) {
			_spid = spid;
			setModified(true);
		}
	}

	public String getTableProperties() {
		return _tableProperties;
	}

	public void setTableProperties(String props) {
		if (props == null) props = "";
		
		if (areStringsDifferent(_tableProperties, props)) {
			_tableProperties = props;
			setModified(true);
		}
	}

	public String getUssiKey() {
		return _ussiKey;
	}

	public void setUssiKey(String key) {
		if (key == null) key = "";
		if (areStringsDifferent(_ussiKey, key)) {
			_ussiKey = key;
			setModified(true);
		}
	}

	public String getArchiveDate() {
		return _archiveDate;
	}

	public void setArchiveDate(String date) {
		if (date == null) date = "";
		if (areStringsDifferent(_archiveDate, date)) {
			_archiveDate = date;
			setModified(true);
		}
	}

	public String getSignatureUrl() {
		return _signatureUrl;
	}

	public void setSignatureUrl(String url) {
		if (url == null) url = "";
		
		if (areStringsDifferent(_signatureUrl, url)) {
			_signatureUrl = url;
			setModified(true);
		}
	}

	// public NotebookContext getNoteBookContext() { return this._nbCtxt; }
	// public void setNotebookContext(NotebookContext nbCtxt) { this._nbCtxt = nbCtxt; }

	// Passthrough for ReactionCache
	//
	public ReactionCache getReactionCache() {
		return _reactions;
	}

	//
	// AnalyisCache
	//
	public AnalysisCache getAnalysisCache() {
		return _analyses;
	}

	//
	// AttachmentCache
	//
	public AttachmentCache getAttachmentCache() {
		return _attachments;
	}

	//
	// Modification Interface
	//
	public void clearModifiedObjects() {
		_modifiedObjects.clear();
		setSubObjectModified(false);
	}

	public Set getModifedObjects() {
		return _modifiedObjects;
	}

	public void update(Observable observed, Object obj) {
		super.update(observed, obj);

		try {
			setModificationDate(NotebookPageUtil.formatDate(new Date()));
		} catch (Exception e) { /* ignored */
		}

		if (obj instanceof PersistableObject) {
			PersistableObject modifiedObj = (PersistableObject) obj;
			if (modifiedObj.isModified())
				_modifiedObjects.add(obj);
		}
	}

	public void update(Observable observed) {
		super.update(observed);

		try {
			setModificationDate(NotebookPageUtil.formatDate(new Date()));
		} catch (Exception e) { /* ignored */
		}

		if (observed instanceof PersistableObject) {
			PersistableObject modifiedObj = (PersistableObject) observed;
			if (modifiedObj.isModified())
				_modifiedObjects.add(observed);
		}
	}

	private void removeActualBatchRegistrationStatus(List productBatches) {
		for (Iterator it = productBatches.iterator(); it.hasNext();) {
			// ensure batch can be removed
			ProductBatch prodBatch = (ProductBatch) it.next();
			prodBatch.getRegInfo().setRegistrationStatus(BatchRegistrationInfo.NOT_REGISTERED);
			prodBatch.setRegStatus(BatchRegistrationInfo.NOT_REGISTERED);
		}
	}

	// 
	// DeepCopy Interface
	//
	public void copy(Object resource, int copyLevel) {
		if (resource instanceof NotebookPage) {
			NotebookPage srcPage = (NotebookPage) resource;

			if (_nbRef == null)
				_nbRef = new NotebookRef();
			if (srcPage._nbRef != null && (copyLevel & COPY_LEVEL_NBREF) > 0)
				_nbRef.deepCopy(srcPage._nbRef);

			_autoCalcOn = srcPage._autoCalcOn;
			_subject = srcPage._subject;
			_procedure = srcPage._procedure;
			_procedureWidth = srcPage._procedureWidth;
			_laf = srcPage._laf;
			_literatureRef = srcPage._literatureRef;
			_taCode = srcPage._taCode;
			_projectCode = srcPage._projectCode;
			_continuedFromRxn = srcPage._continuedFromRxn;
			_continuedToRxn = srcPage._continuedToRxn;
			_projectAlias = srcPage._projectAlias;
			_protocolID = srcPage._protocolID;
			// _sourceCode = srcPage._sourceCode;
			// _sourceDetailCode = srcPage._sourceDetailCode;
			_spid = srcPage._spid;
			_tableProperties = srcPage._tableProperties;

			if ((copyLevel & COPY_LEVEL_REACTION) > 0) {
				_reactions.clear();
				_reactions.deepCopy(srcPage.getReactionCache());
				_batchList.clear(true);
				_batchList.deepCopy(srcPage.getBatchCache());

				List prodList = _batchList.getBatches(BatchType.ACTUAL_PRODUCT);
				if ((copyLevel & COPY_LEVEL_CLONE_ACTUAL_BATCHES) > 0) {
					if (!((copyLevel & COPY_LEVEL_RETAIN_REGISTRATION_STATUS) > 0)) {
						removeActualBatchRegistrationStatus(prodList);
					}
				} else {
					removeActualBatchRegistrationStatus(prodList);
					_batchList.deleteBatches(prodList);
				}
			}

			// if ((copyLevel & COPY_LEVEL_CLONE_BATCHES) > 0) {
			// _analyses.deepCopy(srcPage.getAnalysisCache());
			// // we need to renumber batches and get rid of batch reg info
			// for (Iterator i = _batchList.getProducts().iterator(); i.hasNext();) {
			// ProductBatch pb = (ProductBatch) i.next();
			// if (pb.getType() == BatchType.ACTUAL_PRODUCT) {
			// pb.getBatchNumber().setNbRef(getNotebookRef());
			// }
			// pb.setRegInfo(new BatchRegistrationInfo()); // RegStatus set to NOT_REGISTERED here
			// pb.getCompound().setRegNumber(null);
			// pb.setParentBatchNumber(null);
			// pb.setConversationalBatchNumber(null);
			// // pb.setRegStatus(BatchRegistrationInfo.NOT_REGISTERED);
			// }
			// // _batchList.updateProductBatchesWithNotebookRef(getNotebookRef());
			// }

			if ((copyLevel & COPY_LEVEL_CLONE_ANALYTICAL) > 0) {
				_analyses.deepCopy(srcPage.getAnalysisCache());
			}

			if ((copyLevel & COPY_LEVEL_ATTACHMENTS) > 0) {
				_attachments.deepCopy(srcPage.getAttachmentCache());
			}

			setModified(true);
		}
	}

	public void deepCopy(Object resource) {
		if (resource instanceof NotebookPage) {
			NotebookPage srcPage = (NotebookPage) resource;

			_status = srcPage._status;
			_cenVersion = srcPage._cenVersion;
			_version = srcPage._version;
			_latestVersion = srcPage._latestVersion;
			_userNTID = srcPage._userNTID;
			_nbRef = srcPage._nbRef;
			_siteCode = srcPage._siteCode;
			_owner = srcPage._owner;
			_creationDate = srcPage._creationDate;
			_modificationDate = srcPage._modificationDate;
			_completionDate = srcPage._completionDate;
			copy(resource, COPY_LEVEL_COMPLETE);

			setModified(true);
		}
	}

	/**
	 * Removes registration information from actual batches which allows editing of stoich.
	 * 
	 * @param resource
	 */
	public void versionPage(Object resource) {
		if (resource instanceof NotebookPage) {
			NotebookPage srcPage = (NotebookPage) resource;

			_status = srcPage._status;
			_cenVersion = srcPage._cenVersion;
			_version = srcPage._version;
			_latestVersion = srcPage._latestVersion;
			_userNTID = srcPage._userNTID;
			_nbRef = srcPage._nbRef;
			_siteCode = srcPage._siteCode;
			_owner = srcPage._owner;
			_creationDate = srcPage._creationDate;
			_modificationDate = srcPage._modificationDate;
			_completionDate = srcPage._completionDate;
			copy(resource, COPY_LEVEL_VERSION_EXPERIMENT);

			setModified(true);
		}
	}

	public Object deepClone() {
		NotebookPage newPage = new NotebookPage(false);
		newPage.deepCopy(this);
		return newPage;
	}
}