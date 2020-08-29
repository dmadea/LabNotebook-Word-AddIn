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
 * Created on Aug 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.dao;

import com.chemistry.enotebook.reagent.common.DAO;
import com.chemistry.enotebook.reagent.delegate.ReagentCallBackInterface;
import com.chemistry.enotebook.reagent.exceptions.ReagentMgmtException;
import com.chemistry.enotebook.reagent.threading.Listener;
import com.chemistry.enotebook.reagent.valueobject.IteratingVO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.*;

/**
 * 
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class TextSearchDao implements DAO, Serializable {
	
	private static final long serialVersionUID = 8040807902465455976L;

	public static final Log log = LogFactory.getLog(TextSearchDao.class);
	
	// noted a missing ']' on attribute was missing previously.
	private static final String REAGENT_DBXML_ROOT_XPATH = "/ReagentsDatabaseInfo/Databases/Database[@Visible='true']";

	private ArrayList<String> textDBList;
	private HashMap searchCriteriaMap;
	protected ArrayList<String> queryResults = null;
	private Element reagentDBXMLRoot;
	private String[] compoundList;
	private ArrayList reagentList;
	private IteratingVO iteratingVO;
	// private Object objLatch = new Object();
	private int threadCounter = 0;

	private HashMap resultColumnMap = new HashMap();
	private HashMap searchColumnMap = new HashMap();
	private HashMap datasourceMap = new HashMap();

	protected Hashtable<Integer, TextDBSearcher> runnables = null;
	private ReagentCallBackInterface cbObject = null;

	public TextSearchDao() {
		searchCriteriaMap = new HashMap();
		textDBList = new ArrayList<String>();

		iteratingVO = new IteratingVO();
		queryResults = new ArrayList();
		runnables = new Hashtable<Integer, TextDBSearcher>();
	}

	/**
	 * This method retrieves the reagents info by the structure
	 * 
	 * @param reagents
	 * @return Collection
	 * 
	 * 
	 */
	public List<String> doReagentsSearchByText(Element searchParamsXMLroot,
			ReagentCallBackInterface cbInterface) throws ReagentMgmtException {
		log.debug("...... Doing text search....");
		cbObject = cbInterface;
		try {
			// parsing text search parameters
			parsingSearchParams(searchParamsXMLroot);
			// then call db search
			List<String> reagents = getReagentsFromDB();
			cleanup();
			return reagents;
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	/**
	 * This method retrives the reagents info by the structure
	 * 
	 * @param Collection
	 * 
	 * @return String
	 * 
	 * 
	 */
	public String doReagentsSearchByText(String searchParamsXML)
			throws ReagentMgmtException {
		try {
			// check if to do the iterating
			StringReader reader = new StringReader(searchParamsXML);
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(reader);
			Element root = doc.getRootElement();

			if (!this.isDoIterating(root)) { // perform db search
				// parsing text search parameters
				parsingSearchParams(root);
				// then call db search
				reagentList = new ArrayList(getReagentsFromDB());

				iteratingVO.setReagentsTotal(this.reagentList.size());
				iteratingVO.setLastPosition(0);
			}

			return doIterating(iteratingVO.getLastPosition(), iteratingVO
					.calculateLastPos());
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	/**
	 * @param lastPos
	 *            TODO
	 * @param currentPos
	 *            TODO
	 * @return
	 */
	private String doIterating(int lastPos, int currentPos) {
		StringBuffer sb = new StringBuffer();
		sb.append("<Reagents>");

		for (int i = lastPos; i < currentPos; i++) {
			sb.append(this.reagentList.get(i));
		}

		// need to add one more node to indicate if still data remaining
		sb.append("<IteratingInfo HasMore=\"" + iteratingVO.ifHasMore()
				+ "\" LastPosition=\"" + iteratingVO.calculateLastPos()
				+ "\"/>");
		sb.append("</Reagents>");
		iteratingVO.setLastPosition(iteratingVO.calculateLastPos());

		return sb.toString();
	}

	/**
	 * @param paramsXMLRoot
	 * @return @throws JDOMException
	 * @throws IOException
	 */
	private boolean isDoIterating(Element paramsXMLRoot)
			throws ReagentMgmtException 
	{
		try {
			Element lastPosElement = (Element) XPath.selectSingleNode(paramsXMLRoot, 
			                                                          "/ReagentsLookupParams/Iterating");
			String lastPosString = lastPosElement.getAttributeValue("LastPosition");
			int lastPos = (new Integer(lastPosString)).intValue();
			if (lastPos != -1) {
				return true;
			} else {
				iteratingVO.setLastPosition(lastPos);
				return false;
			}
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	protected List<String> getReagentsFromDB() throws ReagentMgmtException {

		try {
			List<Element> dbslist = XPath.selectNodes(this.getReagentDBXMLRoot(),
			                                          REAGENT_DBXML_ROOT_XPATH);  

			ArrayList<Element> searchingDBList = new ArrayList<Element>();
			List<String> textDBs = getTextDBList();
			if(dbslist != null && textDBs != null) {
				for (int k = 0; k < dbslist.size(); k++) {
					Element textDB = dbslist.get(k);
					String textDBName = textDB.getAttributeValue("Name");
					if(StringUtils.isNotBlank(textDBName)) {
						for (int i = 0; i < textDBs.size(); i++) {
							if (StringUtils.equals(textDBs.get(i), textDBName)) {
								searchingDBList.add(textDB);
							}
						}
					}
				}
			} else {
				if(dbslist == null) {
					log.error("No reagents database info found at ReagentDBXMLRoot = " + REAGENT_DBXML_ROOT_XPATH);
				}
				if(textDBs == null) {
					log.error("No database labels were initialized for this run.  Remember to call setTextDBList().");
				}
				log.debug("Nothing to process... returning early from search");
				return new ArrayList<String>();
			}
			ArrayList<TextDBSearcher> dbSearcherlist = new ArrayList<TextDBSearcher>();
			for (Element db : searchingDBList) {
				TextDBSearcher dbSearcher = new TextDBSearcher(runnables);
				dbSearcher.setReagentDBXMLRoot(this.getReagentDBXMLRoot());
				log.debug("....check search Criteria map...");
				String dName = db.getAttributeValue("Name");
				log.debug("....search Criteria for " + dName + " "
						+ getSearchCriteriaMap().get(dName));

				dbSearcher.setSearchCriteriaMap(this.getSearchCriteriaMap());
				dbSearcher.setTextDBList(this.getTextDBList());
				dbSearcher.setCompoundList(this.getCompoundList());

				dbSearcher.setSearchType(TextDBSearcher.TEXT_ONLY);

				dbSearcher.setTextDB(db);
				dbSearcher.setColumnMap(this.getResultColumnMap());
				dbSearcher.setDSMap(this.getDSMap());
				dbSearcherlist.add(dbSearcher);

				if (Listener.getExecutor() == null) {
					log.debug("THREAD POOL IS NULL");
				} else {
					Listener.getExecutor().execute(dbSearcher);
					runnables.put(new Integer(dbSearcher.hashCode()),
							dbSearcher);
				}
			}

			while (!this.runnables.isEmpty()) {

				try {
					Thread.sleep(1000);
					if (isSearchCancelledByCallback()) {
						terminateSearchThreads();
						break;
					}
				} catch (InterruptedException interrupted) {
					interrupted.printStackTrace();
				}
			}

			for (TextDBSearcher searcher : dbSearcherlist) {
				if (searcher.isOutOfMemory()) {
					return null;
				}
				this.queryResults.addAll(searcher.getQueryResults());

			}

			ArrayList<String> list = new ArrayList<String>(this.queryResults);
			return list;
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	public boolean terminateSearchThreads() {
		if (runnables.size() == 0) {
			return false;
		}
		log.debug(" TextSearchDAO> Terminating Threads :" + runnables.size());
		Iterator iter = runnables.values().iterator();
		{
			TextDBSearcher searcher = (TextDBSearcher) iter.next();
			searcher.terminateThread();
		}
		runnables.clear();

		log.debug(" TextSearchDAO> Terminated All Search Threads");

		return true;
	}

	protected boolean isSearchCancelledByCallback() {
		boolean isCancelled = false;
		try {
			isCancelled = cbObject.isSearchCancelled();
		} catch (Exception e) {
			// log and ignore exceptions
			log.error(e);
		}
		return isCancelled;
	}

	private void parsingSearchParams(Element root) throws ReagentMgmtException {
		try {
			// construct database / search criteria map
			buildSearchCriteriaMap(root);

			// construct textDBList
			buildTextDBList(root);
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	private void buildSearchCriteriaMap(Element root)
			throws ReagentMgmtException {
		try {
			// construct Database List
			List dbList = XPath.selectNodes(root,
					"/ReagentsLookupParams/TextDatabases/Database");

			for (int i = 0; i < dbList.size(); i++) {
				Element dbElment = (Element) dbList.get(i);
				String dbName = (String) dbElment.getAttributeValue("Name");
				// construct search field list for this db
				List searchFieldList = XPath.selectNodes(root,
						"/ReagentsLookupParams/TextDatabases/Database[@Name='"
								+ dbName + "']/SearchFields/Field");
				String searchCriteria = "";
				for (int j = 0; j < searchFieldList.size(); j++) {
					Element searchFieldElment = (Element) searchFieldList
							.get(j);
					String columnName = (String) searchFieldElment
							.getAttributeValue("ColumnName");
					String criteria = (String) searchFieldElment
							.getAttributeValue("Criteria");
					String value = (String) searchFieldElment
							.getAttributeValue("Value");
					value = value.replaceAll("&lt;", "<").replaceAll("&gt;",
							">");
					String useUpper = (String) searchFieldElment
							.getAttributeValue("UseUpper");
					if (j < searchFieldList.size() - 1) {
						searchCriteria += buildSearchCriteria(columnName,
								criteria, value, useUpper)
								+ " AND ";
					} else {
						searchCriteria += buildSearchCriteria(columnName,
								criteria, value, useUpper);
					}
				}

				if (searchCriteria != null && !searchCriteria.equals("")) {
					searchCriteriaMap.put(dbName, searchCriteria);
				}
			}

			this.setSearchCriteriaMap(searchCriteriaMap);
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	private String buildSearchCriteria(String columnName, String criteria,
			String value, String useUpper) {
		String searchCriteria = "";
		int dotPosition = columnName.indexOf(".");
		String colName = columnName.substring(dotPosition + 1, columnName
				.length());
		// System.out.println("The columnName is: " + columnName);
		// System.out.println("The colName is: " + colName);
		// System.out.println("The display name for colName is: " +
		// this.getSearchColumnMap().get(colName));
		if (getSearchColumnMap().get(colName) != null
				&& getSearchColumnMap().get(colName)
						.equals("Molecular Formula")) {
			return buildSearchCriteriaForMF(columnName, criteria, value);
		} else {
			if (columnName.equals("CHEMICAL.CH_PRNM")
					|| columnName.equals("NAME_MDLNUM.NM_NAME")) {
				return buildSearchCriteriaForPRNM(columnName, criteria, value);
			} else {
				String upperString = "";

				// if
				// (!getSearchColumnMap().get(colName).equals("Molecular Weight")
				// && !getSearchColumnMap().get(colName).equals("CAS Number") )
				// {
				if (useUpper != null && useUpper.toLowerCase().equals("true")) {
					columnName = "UPPER(" + columnName + ")";
					upperString = "UPPER";
				}

				if (criteria.toLowerCase().equals(CONTAINS_OPERAND)) {
					searchCriteria = " " + columnName + " LIKE " + upperString
							+ "('%" + value + "%') ";
				} else if (criteria.toLowerCase().equals(STARTS_WITH_OPERAND)) {
					searchCriteria = " " + columnName + " LIKE " + upperString
							+ "('" + value + "%') ";
				} else if (criteria.toLowerCase().equals(ENDS_WITH_OPERAND)) {
					searchCriteria = " " + columnName + " LIKE " + upperString
							+ "('%" + value + "') ";
				} else if (criteria.toLowerCase().equals(GREATER_THAN_OPERAND)) {
					searchCriteria = " " + columnName + " > " + value + " ";
				} else if (criteria.toLowerCase().equals(
						GREATER_THAN_OR_EQUALS_OPERAND)) {
					searchCriteria = " " + columnName + " >= " + value + " ";
				} else if (criteria.toLowerCase().equals(LESS_THAN_OPERAND)) {
					searchCriteria = " " + columnName + " < " + value + " ";
				} else if (criteria.toLowerCase().equals(
						LESS_THAN_OR_EQUALS_OPERAND)) {
					searchCriteria = " " + columnName + " <= " + value + " ";
				} else if (criteria.toLowerCase().equals(EQUALS_OPERAND)) {
					searchCriteria = " " + columnName + " = " + upperString
							+ "('" + value + "') ";
				}
				return searchCriteria;
			}
		}
	}

	public String buildSearchCriteriaForMF(String columnName, String criteria,
			String value) {
		String searchCriteria = "";
		String valueWithoutSpaces = value.replaceAll(" ", "");

		if (criteria.toLowerCase().equals(CONTAINS_OPERAND)) {
			searchCriteria = " (" + columnName + " LIKE " + "'%" + value
					+ "%' " + " or " + columnName + " LIKE " + "'%"
					+ valueWithoutSpaces + "%') ";
		} else if (criteria.toLowerCase().equals(STARTS_WITH_OPERAND)) {
			searchCriteria = " (" + columnName + " LIKE " + "'" + value + "%' "
					+ " or " + columnName + " LIKE " + "'" + valueWithoutSpaces
					+ "%') ";

		} else if (criteria.toLowerCase().equals(ENDS_WITH_OPERAND)) {
			searchCriteria = " (" + columnName + " LIKE " + "'%" + value + "' "
					+ " or " + columnName + " LIKE " + "'%"
					+ valueWithoutSpaces + "') ";

		} else if (criteria.toLowerCase().equals(EQUALS_OPERAND)) {
			searchCriteria = " (" + columnName + " = " + "'" + value + "' "
					+ " or " + columnName + " = " + "'" + valueWithoutSpaces
					+ "') ";
		}
		return searchCriteria;
	}

	public String buildSearchCriteriaForPRNM(String colName, String criteria,
			String value) {
		String searchCriteria = "";
		String columnName = "UPPER(" + colName + ")";
		String upperString = "UPPER";

		if (criteria.toLowerCase().equals(CONTAINS_OPERAND)) {
			value = value.replaceAll("-", "\\\\" + "-"); // minus means exclude
			// from result so
			// escape to take as
			// literal
			searchCriteria = "contains(" + colName + ", '"
					+ value.toUpperCase() + "')  > 0";
		} else if (criteria.toLowerCase().equals(STARTS_WITH_OPERAND)) {
			searchCriteria = " " + columnName + " LIKE " + upperString + "('"
					+ value + "%') ";
		} else if (criteria.toLowerCase().equals(ENDS_WITH_OPERAND)) {
			searchCriteria = " " + columnName + " LIKE " + upperString + "('%"
					+ value + "') ";
		} else if (criteria.toLowerCase().equals(EQUALS_OPERAND)) {
			searchCriteria = " " + columnName + " = " + upperString + "('"
					+ value + "') ";
		}
		return searchCriteria;
	}

	private void buildTextDBList(Element root) throws ReagentMgmtException {
		try {
			// construct dbList
			List dbslist = XPath.selectNodes(root,
					"/ReagentsLookupParams/TextDatabases/Database");

			for (int i = 0; i < dbslist.size(); i++) {
				Element dbElment = (Element) dbslist.get(i);
				String dbName = (String) dbElment.getAttributeValue("Name");
				this.textDBList.add(dbName);
			}

			this.setTextDBList(textDBList);
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	private void cleanup() {
		this.queryResults.clear();
		this.searchCriteriaMap.clear();
		this.textDBList.clear();
	}

	public HashMap getSearchCriteriaMap() {
		return this.searchCriteriaMap;
	}

	public ArrayList getQueryResults() {
		return this.queryResults;
	}

	public ArrayList<String> getTextDBList() {
		return this.textDBList;
	}

	public Element getReagentDBXMLRoot() {
		return this.reagentDBXMLRoot;
	}

	public String[] getCompoundList() {
		return this.compoundList;
	}

	public ArrayList getReagentList() {
		return this.reagentList;
	}

	public void setTextDBList(ArrayList<String> tDBList) {
		this.textDBList = tDBList;
	}

	public void setReagentDBXMLRoot(Element reagentDBRoot) {
		this.reagentDBXMLRoot = reagentDBRoot;
	}

	public void setSearchCriteriaMap(HashMap sCriteriaMap) {
		this.searchCriteriaMap = sCriteriaMap;
	}

	public HashMap getResultColumnMap() {
		return this.resultColumnMap;
	}

	public void setResultColumnMap(HashMap cMap) {
		this.resultColumnMap = cMap;
	}

	public void setCompoundList(String[] cList) {
		this.compoundList = cList;
	}

	public void setReagentList(ArrayList rList) {
		this.reagentList = rList;
	}

	public HashMap getDSMap() {
		return this.datasourceMap;
	}

	public void setDSMap(HashMap dsMap) {
		this.datasourceMap = dsMap;
	}

	/**
	 * @return Returns the searchColumnMap.
	 */
	public HashMap getSearchColumnMap() {
		return searchColumnMap;
	}

	/**
	 * @param searchColumnMap
	 *            The searchColumnMap to set.
	 */
	public void setSearchColumnMap(HashMap searchColumnMap) {
		this.searchColumnMap = searchColumnMap;
	}
}