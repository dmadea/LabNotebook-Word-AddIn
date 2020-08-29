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
 * Created on Aug 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.dao;

import com.chemistry.enotebook.reagent.delegate.ReagentCallBackInterface;
import com.chemistry.enotebook.reagent.exceptions.ReagentMgmtException;
import com.chemistry.enotebook.reagent.threading.Listener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * 
 * 
 */
public class TextStructureDao extends TextSearchDao {
	
	private static final long serialVersionUID = 5430681916431389456L;
	
	public static final Log log = LogFactory.getLog(TextStructureDao.class);
	// private Element reagentDBXMLRoot;
	private StructureDao structureDao;
	private int threadCounter = 0;

	// private Object objLatch = new Object();
	private ReagentCallBackInterface cbObject = null;

	public TextStructureDao() {
		super();
		structureDao = new StructureDao();
		runnables = new Hashtable<Integer, TextDBSearcher>();
	}

	public List<String> doTextAndStructureSearch(Element paramsXMLRoot,
	                                             ReagentCallBackInterface cbObject) throws ReagentMgmtException {

		log.debug("...... Doing structure and text search....");
		this.cbObject = cbObject;
		try {
			// set reagent db XML for structureDao
			structureDao.setReagentDBXMLRoot(this.getReagentDBXMLRoot());

			// first do structure search to get the compound list
			String[] compoundList = structureDao.prepareReagentsSearchByStructure(paramsXMLRoot);

			if (isSearchCancelledByCallback())
				return null;

			if (compoundList == null) {
				return null;
			} else {
				// set up compound list for text search
				setCompoundList(compoundList);

				// then do text search
				return doReagentsSearchByText(paramsXMLRoot, cbObject);
			}
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	protected List<String> getReagentsFromDB() throws ReagentMgmtException {
		try {

			List<Element> dbslist = XPath.selectNodes(this.getReagentDBXMLRoot(),
					"/ReagentsDatabaseInfo/Databases/Database[@Visible='true']");

			ArrayList<Element> searchingDBList = new ArrayList<Element>();

			// while (dbsIterator.hasNext()) {
			for (int k = 0; k < dbslist.size(); k++) {
				// Element textDB = (Element) dbsIterator.next();
				Element textDB = dbslist.get(k);
				String textDBName = textDB.getAttributeValue("Name");

				for (int i = 0; i < this.getTextDBList().size(); i++) {
					if (this.getTextDBList().get(i).equals(textDBName)) {
						searchingDBList.add(textDB);
					}
				}
			}

			ArrayList<TextDBSearcher> dbSearcherlist = new ArrayList<TextDBSearcher>();
			for (int j = 0; j < searchingDBList.size(); j++) {
				TextDBSearcher dbSearcher = new TextDBSearcher(runnables);
				dbSearcher.setReagentDBXMLRoot(this.getReagentDBXMLRoot());
				dbSearcher.setSearchCriteriaMap(this.getSearchCriteriaMap());
				dbSearcher.setTextDBList(this.getTextDBList());
				dbSearcher.setCompoundList(this.getCompoundList());
				dbSearcher.setColumnMap(this.getResultColumnMap());
				dbSearcher.setDSMap(this.getDSMap());
				dbSearcher.setSearchType(TextDBSearcher.STRUCTURE_AND_TEXT);
				dbSearcher.setTextDB(searchingDBList.get(j));

				dbSearcherlist.add(dbSearcher);
				Listener.getExecutor().execute(dbSearcher);
				runnables.put(new Integer(dbSearcher.hashCode()), dbSearcher);
			}

			while (!this.runnables.isEmpty()) {

				try {
					Thread.sleep(1000);

					if (isSearchCancelledByCallback()) {
						log.debug(" Text Structure Search Cancelled by client");
						terminateSearchThreads();
						break;
					}
				} catch (InterruptedException interrupted) {
					interrupted.printStackTrace();
				}
			}

			boolean isOutOfmemory = false;
			for (int i = 0; i < dbSearcherlist.size(); i++) {
				if (dbSearcherlist.get(i).isOutOfMemory()) {
					isOutOfmemory = true;
					break;
				}
			}

			if (isOutOfmemory) {
				return null;
			} else {
				for (int i = 0; i < dbSearcherlist.size(); i++) {
					this.queryResults.addAll(dbSearcherlist.get(i).getQueryResults());
				}

				ArrayList<String> list = new ArrayList<String>(this.queryResults);
				log.debug("The total data record is: " + list.size());
				return list;
			}
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

}
