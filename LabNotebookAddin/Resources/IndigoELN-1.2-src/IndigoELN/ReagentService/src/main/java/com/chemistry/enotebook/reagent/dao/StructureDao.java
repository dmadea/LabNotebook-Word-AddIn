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

package com.chemistry.enotebook.reagent.dao;

import com.chemistry.enotebook.compoundmgmtservice.delegate.CompoundMgmtServiceDelegate;
import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundNotFoundException;
import com.chemistry.enotebook.reagent.common.DAO;
import com.chemistry.enotebook.reagent.delegate.ReagentCallBackInterface;
import com.chemistry.enotebook.reagent.exceptions.ReagentMgmtException;
import com.chemistry.enotebook.reagent.threading.Listener;
import com.chemistry.enotebook.reagent.valueobject.StructureParamsVO;
import com.chemistry.enotebook.sdk.delegate.ChemistryDelegate;
import com.chemistry.enotebook.search.Compound;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.io.Serializable;
import java.io.StringReader;
import java.util.*;

/**
 * 
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class StructureDao implements DAO, Serializable {
	
	private static final long serialVersionUID = -4965840613985472903L;
	
	public static final Log log = LogFactory.getLog(StructureDao.class);
	// protected SOAPCSSServerSoapStub cssStub;
	private StructureParamsVO structureParamsVO;
	private ArrayList textDBList;
	private ArrayList<String> queryResults = null;
	private Element reagentDBXMLRoot;
	private String[] compoundList;
	private int threadCounter = 0;
	// private Object objLatch = new Object();
	private int recordCount = 0;
	private HashMap columnMap = new HashMap();
	private HashMap datasourceMap = new HashMap();
	private Hashtable<Integer, TextDBSearcher> runnables = null;

	private ReagentCallBackInterface cbObject = null;

	public StructureDao() {
		textDBList = new ArrayList();
		queryResults = new ArrayList();
		structureParamsVO = new StructureParamsVO();
		runnables = new Hashtable<Integer, TextDBSearcher>();
	}

	/**
	 * This method retrives the reagents info by the structure
	 * 
	 * @param String
	 *            searchParamsXML
	 * 
	 * @return String[]
	 * 
	 * 
	 */
	public Collection<String> doReagentsSearchByStructure(Element searchParamsXMLRoot,
			ReagentCallBackInterface cbObject) throws ReagentMgmtException {
		log.debug("...... Doing structure search....");

		this.cbObject = cbObject;
		try {
			// parsing search parameters
			parsingSearchParams(searchParamsXMLRoot);

			// call searchStructures to perform the structure search then call
			// db search
			String[] structureResult = searchStructures(structureParamsVO);
			if (structureResult == null || structureResult.length == 0) {
				return new ArrayList<String>();
			} else {

				if (isSearchCancelledByCallback())
					return new ArrayList<String>();

				setCompoundList(structureResult);
				// then call db search
				Collection<String> reagents = getReagentsFromDB();
				if (reagents.size() < structureResult.length)
					reagents = buildCollectionFromCompounds(reagents, structureResult);

				cleanup();
				return reagents;
			}
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	private boolean isSearchCancelledByCallback() {
		boolean isCancelled = false;
		try {
			isCancelled = cbObject.isSearchCancelled();
		} catch (Exception e) {
			// log and ignore exceptions
			log.error(e);
		}
		return isCancelled;
	}

	private Collection<String> buildCollectionFromCompounds(Collection<String> reagents, String[] structureResult) {
		ArrayList<String> compoundList = new ArrayList<String>();
		
		for (String result : structureResult)			
			for (String reagent : reagents)			
				if (reagent.indexOf(result) >= 0)
					if (!compoundList.contains(reagent))
						compoundList.add(reagent);

		return compoundList;
	}

	public String[] prepareReagentsSearchByStructure(Element searchParamsXMLRoot)
			throws ReagentMgmtException {
		try {
			// parsing search parameters
			parsingSearchParams(searchParamsXMLRoot);

			// call searchStructures to perform the structure search then call
			// db search
			String[] structureList = searchStructures(structureParamsVO);

			return structureList;
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	protected Collection<String> getReagentsFromDB() throws ReagentMgmtException {
		try {
			List dbslist = XPath.selectNodes(getReagentDBXMLRoot(),
					"/ReagentsDatabaseInfo/Databases/Database[@Visible='true']");

			ArrayList<Element> searchingDBList = new ArrayList<Element>();

			for (int k = 0; k < dbslist.size(); k++) {
				Element textDB = (Element) dbslist.get(k);
				String textDBName = textDB.getAttributeValue("Name");
				for (int i = 0; i < getTextDBList().size(); i++) {
					if (((String) getTextDBList().get(i)).equals(textDBName)) {
						searchingDBList.add(textDB);
					}
				}
			}

			ArrayList<TextDBSearcher> dbSearcherlist = new ArrayList<TextDBSearcher>();
			for (int j = 0; j < searchingDBList.size(); j++) {
				TextDBSearcher dbSearcher = new TextDBSearcher(runnables);
				dbSearcher.setReagentDBXMLRoot(getReagentDBXMLRoot());
				dbSearcher.setTextDBList(getTextDBList());
//				dbSearcher.setQueryResults(getQueryResults());
				dbSearcher.setColumnMap(getColumnMap());
				dbSearcher.setDSMap(getDSMap());
				dbSearcher.setCompoundList(getCompoundList());
				dbSearcher.setSearchType(TextDBSearcher.STRUCTURE_ONLY);
				dbSearcher.setTextDB(searchingDBList.get(j));
				dbSearcherlist.add(dbSearcher);
				Listener.getExecutor().execute(dbSearcher);
				runnables.put(new Integer(dbSearcher.hashCode()), dbSearcher);
			}

			while (!this.runnables.isEmpty()) {
				try {
					Thread.sleep(1000);

					if (isSearchCancelledByCallback()) {
						terminateSearchThreads();
						break;
					}
				} catch (InterruptedException interrupted) {
					log.error(interrupted);
				}
			}

			for (int i = 0; i < dbSearcherlist.size(); i++) {
				if (dbSearcherlist.get(i).isOutOfMemory()) {
					return null;
				}
				queryResults.addAll(dbSearcherlist.get(i).getQueryResults());
			}

			ArrayList<String> list = new ArrayList<String>(queryResults);
			log.debug("The total data record is: " + list.size());

			return list;
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	public boolean terminateSearchThreads()
	{
	    if(runnables.size() == 0)
	    {
	        return false;
	    }
	    System.out.println(" StructureDAO > Terminating Threads :"+runnables.size());
	    Enumeration enumer = runnables.elements();
	    int count = 0;
	    while(enumer.hasMoreElements())
	    {
            TextDBSearcher searcher = (TextDBSearcher)enumer.nextElement();
            
            searcher.terminateThread();
	    }
	    runnables.clear();
	    System.out.println(" StrucutureDAO > Terminated Search Threads :");
	    return true;
	}

	/**
	 * This method parses the structure data got from searchStructures() method
	 * and build a reagent list
	 * 
	 * @param String
	 *            [] StructureData
	 * @return ArrayList --ReagentList
	 * 
	 * 
	 */
	protected ArrayList getReagentListFromStructureData(String[] StructureData)
			throws ReagentMgmtException {
		ArrayList reagentList = new ArrayList();
		try {
			SAXBuilder builder = new SAXBuilder();
			StringReader reader = null;
			for (int i = 0; i < StructureData.length; i++) {
				reader = new StringReader(StructureData[i]);
				Document doc = builder.build(reader);
				Element root = doc.getRootElement();

				List compoundNumberlist = XPath.selectNodes(root,
						"/CSSResults/ResultInfo/CompoundNumber");
				for (int j = 0; j < compoundNumberlist.size(); j++) {
					Element compoundNumberElement = (Element) compoundNumberlist
							.get(j);
					String compoundNumber = (String) (compoundNumberElement
							.getTextNormalize());
					reagentList.add(compoundNumber);
				}
			}
			return reagentList;
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	protected String[] searchStructures(StructureParamsVO structureParamsVO)
			throws ReagentMgmtException {
		List<String> compoundStructures = new ArrayList<String>();

		try {			
			List<Compound> compounds = new CompoundMgmtServiceDelegate().searchByStructure(Arrays.asList(structureParamsVO.getDbsList()), structureParamsVO.getMolDefn(), structureParamsVO.getSrchOperator(), structureParamsVO.getSrchOptionValue());
			for (Compound compound : compounds) {
				String compoundNo = compound.getCompoundNo();
				if (StringUtils.isNotBlank(compoundNo))
					if (!compoundStructures.contains(compoundNo))
						compoundStructures.add(compoundNo);
			}

			return compoundStructures.toArray(new String[0]);
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	protected void parsingSearchParams(Element paramsXMLRoot)
			throws ReagentMgmtException {
		try {
			// construct dbType
			Element dbTypeElement = (Element) XPath
					.selectSingleNode(paramsXMLRoot,
							"/ReagentsLookupParams/StructureDatabases/DatabaseTypes/DatabaseType");
			String dbType = dbTypeElement.getAttributeValue("Name");
			structureParamsVO.setDbType(dbType);

			// construct molDefn, need to check structure data format
			// if the format is not MDL Molfile type, do the format convertion
			// first
			Element molDefnElement = (Element) XPath
					.selectSingleNode(paramsXMLRoot,
							"/ReagentsLookupParams/StructureDatabases/DatabaseTypes/DatabaseType/MolDefn");
			String fileFormat = molDefnElement.getAttributeValue("FileType");
			String molDefn = molDefnElement.getText();
			if (!fileFormat.equals("MDL Molfile")) {
				ChemistryDelegate chemistryLocalDelegate = new ChemistryDelegate();
				byte[] molFile = chemistryLocalDelegate.convertChemistry(
						molDefn.getBytes(), "", "MDL Molfile");
				structureParamsVO.setMolDefn(new String(molFile));
			} else {
				structureParamsVO.setMolDefn(molDefn);
			}

			// construct srchOperator
			Element srchOperatorElement = (Element) XPath
					.selectSingleNode(
							paramsXMLRoot,
							"/ReagentsLookupParams/StructureDatabases/DatabaseTypes/DatabaseType/SearchType");
			String srchOperator = srchOperatorElement.getText();
			structureParamsVO.setSrchOperator(srchOperator);

			// construct srchOptionValues
			Element srchOptionValueElement = (Element) XPath
					.selectSingleNode(
							paramsXMLRoot,
							"/ReagentsLookupParams/StructureDatabases/DatabaseTypes/DatabaseType/SearchOptionValue");
			String srchOptionValue = srchOptionValueElement.getText();
			structureParamsVO.setSrchOptionValue(srchOptionValue);

			// construct dbList
			List dbslist = XPath
					.selectNodes(
							paramsXMLRoot,
							"/ReagentsLookupParams/StructureDatabases/DatabaseTypes/DatabaseType/DatabaseList/Database");
			ArrayList dbsArrayList = new ArrayList();
			for (int i = 0; i < dbslist.size(); i++) {
				Element dbElment = (Element) dbslist.get(i);
				String dbName = (String) dbElment.getTextNormalize();
				dbsArrayList.add(dbName);
			}
			structureParamsVO.setDbsArrayList(dbsArrayList);

			// construct fldList
			List fldlist = XPath
					.selectNodes(
							paramsXMLRoot,
							"/ReagentsLookupParams/StructureDatabases/DatabaseTypes/DatabaseType/FieldList/Field");
			ArrayList fldListArrayList = new ArrayList();
			for (int i = 0; i < fldlist.size(); i++) {
				Element fldElment = (Element) fldlist.get(i);
				String fldName = (String) fldElment.getTextNormalize();
				fldListArrayList.add(fldName);
			}
			structureParamsVO.setFldListArrayList(fldListArrayList);

			setStructureParamsVO(structureParamsVO);

			// construct textDBList
			buildTextDBList(dbsArrayList);
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	private void buildTextDBList(ArrayList structureDBList)
			throws ReagentMgmtException {
		try {
			HashMap textStructuremap = new HashMap();
			List tsRelationslist = XPath
					.selectNodes(
							getReagentDBXMLRoot(),
							"/ReagentsDatabaseInfo/Text_Structure_Relationships/Text_Structure_Relationship");

			for (int i = 0; i < tsRelationslist.size(); i++) {
				Element relationElment = (Element) tsRelationslist.get(i);
				String strucDBName = relationElment
						.getAttributeValue("Structure");
				String textDBName = relationElment.getAttributeValue("Text");
				if (structureDBList.contains(strucDBName)) {
					// check the text database uniqueness to avoid duplicate
					// searches
					if (!textStructuremap.containsKey(textDBName)) {
						textDBList.add(textDBName);
						textStructuremap.put(textDBName, strucDBName);
					}
				}
			}

			setTextDBList(textDBList);
		} catch (Exception e) {
			throw new ReagentMgmtException(e.getMessage(), e);
		}
	}

	public String getStructureByCompoundNo(String compoundNumber)
			throws Exception, ReagentMgmtException, CompoundNotFoundException {
		String compoundStruct = null;

		try {
			CompoundMgmtServiceDelegate compoundMgmtServiceLocalDelegate = new CompoundMgmtServiceDelegate();
			compoundStruct = compoundMgmtServiceLocalDelegate.getStructureByCompoundNo(compoundNumber).get(0);

			return compoundStruct;
		} catch (Exception e1) {
			throw e1;
		}
	}

	private void cleanup() {
		textDBList.clear();
		queryResults.clear();
	}

	public StructureParamsVO getStructureParamsVO() {
		return structureParamsVO;
	}

	public void setStructureParamsVO(StructureParamsVO strucParamsVO) {
		structureParamsVO = strucParamsVO;
	}

	public ArrayList getQueryResults() {
		return this.queryResults;
	}

	public ArrayList getTextDBList() {
		return textDBList;
	}

	public Element getReagentDBXMLRoot() {
		return reagentDBXMLRoot;
	}

	public String[] getCompoundList() {
		return compoundList;
	}

	public void setTextDBList(ArrayList tDBList) {
		textDBList = tDBList;
	}

	public void setReagentDBXMLRoot(Element reagentDBRoot) {
		reagentDBXMLRoot = reagentDBRoot;
	}

	public void setCompoundList(String[] cList) {
		compoundList = cList;
	}

	public HashMap getColumnMap() {
		return columnMap;
	}

	public void setColumnMap(HashMap cMap) {
		columnMap = cMap;
	}

	public HashMap getDSMap() {
		return datasourceMap;
	}

	public void setDSMap(HashMap dsMap) {
		datasourceMap = dsMap;
	}
}
