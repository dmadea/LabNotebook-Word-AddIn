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
 * Created on Aug 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.chemistry.enotebook.reagent.dao;

import com.chemistry.enotebook.reagent.exceptions.ReagentMgmtException;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
//public class TextDBSearcher extends Thread implements DAO {
public class TextDBSearcher 
	extends Thread 
{
	public static final int TEXT_ONLY = 0;
	public static final int STRUCTURE_ONLY = 1;
	public static final int STRUCTURE_AND_TEXT = 2;

	protected ArrayList queryResults = new ArrayList();
	protected HashMap columnMap = new HashMap();
	protected ArrayList textDBList;
	protected HashMap searchCriteriaMap;
	protected Element reagentDBXMLRoot;
	protected String[] compoundList;
	protected Element textDB;
	protected int searchType = 0;
	private boolean _completed = false;
	
	private HashMap datasourceMap = new HashMap();
	private int recordSetThreshold = 5000;
	private boolean isOutOfMemory = false;
	private boolean isInterrupted = false;

	private Timer timer=null;
	private Map runnables = null;
	private Connection dbcon = null;
	private Statement dbst = null;

	private static final Log log = LogFactory.getLog(TextDBSearcher.class);
	
	public TextDBSearcher(Map runnables)
	{
		this.runnables = runnables;
	}
	
	public void run() 
	{
	    super.setName("CeNSearcher"+hashCode());
	    isInterrupted = false;
		timer = new Timer();
		timer.schedule(new Task(), 1000*60*5);// setting to 5 minutes
		log.debug("No of Threads Running---:"+runnables.size());
		
		try {
			String queryString = buildQuery(TextDBSearcher.this.getTextDB());
			if(!isInterrupted)
			{
				log.debug("The query string for db "
					+ textDB.getAttributeValue("Name") + " is: "+queryString);
			    doTextSearch(queryString, textDB.getAttributeValue("Name"));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			setCompleted();
			runnables.remove(new Integer(this.hashCode()));
			log.debug("Removed From Runnables(Finally)---:"+hashCode());
			timer.cancel();
			log.debug(" Remaining Live Threads ---:"+runnables.size());
		}
	}

	private void cleanup() {
		this.queryResults.clear();
		
	}

	/**
	 * Latches the operation status to the completed state.
	 */
	public void setCompleted() {
		_completed = true;
	}

	/**
	 * Gets the operation completion state
	 * 
	 * @return boolean true if completed, false otherwise.
	 */
	public boolean isCompleted() {
		return _completed;
	}

	/**
	 * @param textDB
	 * @return
	 */

	public String buildQuery(Element textDB) 
		throws ReagentMgmtException
	{
		String queryString = "";
		String whereString = "";
		
		
		//try {
			String selectString = buildSelectString(textDB);
			if(isInterrupted)
			    return queryString;
			
			String fromString = buildFromString(textDB);
			if(isInterrupted)
			    return queryString;
			
			switch (this.getSearchType()) {
				default:
					log.debug("Default");
					break;
				case 0:
					whereString = buildWhereString(textDB);
					break;
				case 1:
					whereString = buildStructureWhereString(textDB);
					break;
				case 2:
					if (this.getCompoundList().length > 0){
						whereString = buildWhereString(textDB)
							+ " AND "
							+ this.buildStructureTextWhereString(this
									.getCompoundList(), textDB);
					}else {
						whereString = buildWhereString(textDB);
					}
					break;
			}

			queryString = selectString + fromString + whereString;

			return queryString;
//		} catch (Exception e) {
//			throw new ReagentMgmtException(e);
//		}
	}

	private String buildSelectString(Element textDB) 
		throws ReagentMgmtException
	{
		StringBuffer selectString = new StringBuffer();
		try {
			//build query string
			selectString.append("SELECT ");
			String dbName = (String) textDB.getAttributeValue("Name");
			log.debug("buildSelectString> before selectNode");
			List resultFieldlist = XPath.selectNodes(textDB,
					"//Database[@Name='" + dbName + "']/Tables/Table/Result_Fields/Field");
			
			log.debug("buildSelectString> resultFieldList ---"+resultFieldlist.size());
			for (int i = 0; i < resultFieldlist.size(); i++) {
				String name = ((Element) resultFieldlist.get(i)).getAttributeValue("Column_Name");
				if (name.equalsIgnoreCase("DENSITY_SUB")) {
					int idx = dbName.indexOf(".");  // Determine schema so we can prefix physical_properties table accordingly
					String schema = "";
					if (idx >= 0) schema = dbName.substring(0, idx+1);
					name = "(select * from (select PH_NUMERIC, ROW_NUMBER() OVER (ORDER BY PH_NUMERIC) INTERNALROWNUMBER from " + schema + "PHYSICAL_PROPERTIES b) where b.PH_PRDSEQNO = PRODUCT.PRD_SEQNO and PH_TYPE = 'D' and PH_NUMERIC > 0 and INTERNALROWNUMBER = 1) DENSITY_SUB";
				}
				
				if (i < resultFieldlist.size() - 1) {
					selectString.append(name + ",");
				} else {
					selectString.append(name);
				}
				if(isInterrupted){
				    return selectString.toString();
				}
			}

			return selectString.toString();
		} catch (Exception e) {
			throw new ReagentMgmtException(e);
		}
	}
	
	//for two phase search
	private String buildSelectCountString(Element textDB) {
		return "SELECT COUNT(*) ";
	}


	private String buildFromString(Element textDB) 
		throws ReagentMgmtException
	{
		StringBuffer fromString = new StringBuffer();
		try {
			//build from string
			String dbName = (String) textDB.getAttributeValue("Name");
			log.debug("buildFromString> before selectNodes");
			List tableList = XPath.selectNodes(textDB, "//Database[@Name='"
					+ dbName + "']/Tables/Table");

			fromString.append(" FROM ");
			for (int j = 0; j < tableList.size(); j++) {
				if (j < tableList.size() - 1) {
					fromString.append(((Element) tableList.get(j)).getAttributeValue("Name")+ ",");
				} else {
					fromString.append(((Element) tableList.get(j)).getAttributeValue("Name"));
				}
				if(isInterrupted)
				    return fromString.toString();
				
			}

			return fromString.toString();
		} catch (Exception e) {
			throw new ReagentMgmtException(e);
		}
	}

	private String buildWhereString(Element textDB) 
		throws ReagentMgmtException
	{
		String whereString = "";
	    //StringBuffer whereBuffer = new StringBuffer();
		boolean isWhereExist = false;

		try {
			//build where string
			//add table relations
			String dbName = (String) textDB.getAttributeValue("Name");
			
			Element tableRelation = (Element) XPath.selectSingleNode(textDB,
					"//Database[@Name='" + dbName+ "']/Table_Relationships/Table_Relationship");

			if (!((String) tableRelation.getAttributeValue("Relation")).equals("NULL")) {
				whereString += " WHERE " + (String) tableRelation.getAttributeValue("Relation");
				//whereBuffer.append("WHERE");
				//whereBuffer.append((String) tableRelation.getAttributeValue("Relation"));
			    isWhereExist = true;
			}
			log.debug("buildWhereString> after selectNode");	
			//add search criteria from params XML
			String criteria = (String) searchCriteriaMap.get(dbName);
			if (criteria != null && !criteria.equals("")) {
				if (isWhereExist) {
					whereString += " AND " + criteria;
					//whereBuffer.append(criteria);
				} else {
					whereString += " WHERE " + criteria;
					//whereBuffer.append(criteria);
				}
			}

			return whereString;
		} catch (Exception e) {
			throw new ReagentMgmtException(e);
		}
	}

	private String buildStructureTextWhereString(String[] reagentList, Element textDB) 
		throws ReagentMgmtException
	{
		//String whereString = "";
		StringBuffer whereStringBuffer = new StringBuffer();

		try {
			String dbName = (String) textDB.getAttributeValue("Name");
			//add compound list
			Element tsRelationElement = (Element) XPath.selectSingleNode(reagentDBXMLRoot,
							"/ReagentsDatabaseInfo/Text_Structure_Relationships/Text_Structure_Relationship[@Text='"
									+ dbName + "']");
			log.debug("buildWhereString(arg)> after selectNode");	
			whereStringBuffer.append(tsRelationElement.getAttributeValue("Relation"));
			whereStringBuffer.append( " IN ( ");

			for (int j = 0; j < reagentList.length; j++) {
				if (j < reagentList.length - 1) {
					whereStringBuffer.append("'"); 
					whereStringBuffer.append(reagentList[j]);
					whereStringBuffer.append("'"); 
					whereStringBuffer.append(",");
				} else {
				    whereStringBuffer.append("'"); 
					whereStringBuffer.append(reagentList[j]);
					whereStringBuffer.append("'"); 
					whereStringBuffer.append(")");
				}
				if(isInterrupted)
				    return whereStringBuffer.toString();
			}
			// following if statement is for completing the query in a proper
			//format.
			if(reagentList.length == 0)
				whereStringBuffer.append(")");
			    

			return whereStringBuffer.toString();
		} catch (Exception e) {
			throw new ReagentMgmtException(e);
		}
	}

	private String buildStructureWhereString(Element textDB) 
		throws ReagentMgmtException
	{
		//String whereString = "";
	    StringBuffer whereStringBuffer = new StringBuffer();
		try {
			//build where string
			//add table relations
			String dbName = (String) textDB.getAttributeValue("Name");
			Element tableRelation = (Element) XPath.selectSingleNode(textDB,
					"//Database[@Name='" + dbName + "']/Table_Relationships/Table_Relationship");

			whereStringBuffer.append(" WHERE ");
			log.debug("buildStructureWhereString> after selectNode");	
			if (!((String) tableRelation.getAttributeValue("Relation")).equals("NULL")) {
				whereStringBuffer.append((String) tableRelation.getAttributeValue("Relation"));
				whereStringBuffer.append(" AND ");
			}

			//add compound list
			//List tsRelationslist = XPath.selectNodes(root,
			// "/ReagentsDatabaseInfo/Text_Structure_Relationships/Text_Structure_Relationship");
			Element tsRelationElement = (Element) XPath.selectSingleNode(textDB,
							"/ReagentsDatabaseInfo/Text_Structure_Relationships/Text_Structure_Relationship[@Text='"
									+ dbName + "']");

			log.debug("buildStructureWhereString> after selectiSingleNode");	
			whereStringBuffer.append( tsRelationElement.getAttributeValue("Relation"));
			whereStringBuffer.append( " IN ( " );

			log.debug("buildStructureWhereString> CompoundList--"+getCompoundList().length);	
			for (int j = 0; j < this.getCompoundList().length; j++) {
				if (j < this.getCompoundList().length - 1) {
				    whereStringBuffer.append("'"); 
					whereStringBuffer.append(this.getCompoundList()[j]);
					whereStringBuffer.append("'"); 
					whereStringBuffer.append(",");
					
					//whereStringBuffer.append( "'" + this.getCompoundList()[j] + "'" + ",");
				} else {
					//whereStringBuffer.append ( "'" + this.getCompoundList()[j] + "'" + ")");
				    whereStringBuffer.append("'"); 
					whereStringBuffer.append(this.getCompoundList()[j]);
					whereStringBuffer.append("'"); 
					whereStringBuffer.append(")");
					
				}
				if(isInterrupted)
				    return whereStringBuffer.toString();
				
			}
			log.debug("buildStructureWhereString> Returning from here"+whereStringBuffer);	

			return whereStringBuffer.toString();
		} catch (Exception e) {
			throw new ReagentMgmtException(e);
		}
	}

	private ArrayList doTextSearch(String queryString, String DbName) 
		throws ReagentMgmtException
	{
		//--Connection con = null;
		//Statement st = null;
		ResultSet rs = null;
		int rowCount = 0;
		
		try {
			dbcon = (ServiceLocator.getInstance().locateDataSource((String) this.getDSMap().get(DbName))).getConnection();
			
			dbst = dbcon.createStatement();
			java.util.GregorianCalendar calendar = new java.util.GregorianCalendar(); 
			log.debug("Query Start-time of "+hashCode()+" =="+ new java.util.Date());
			rs = dbst.executeQuery(queryString);
			log.debug("Query End-time of "+hashCode()+" =="+ new java.util.Date());
			
			int colCount = rs.getMetaData().getColumnCount();
			while (rs.next()) {
			    if(isInterrupted)
				    break;
				
			    rowCount++;
				if( rowCount > this.recordSetThreshold){
					this.setOutOfMemory(true);
					break;
				}
				StringBuffer sb = new StringBuffer();
				
				String colName = null;
				Element dbDisplayNameElement = (Element) XPath.selectSingleNode(this.reagentDBXMLRoot, "/ReagentsDatabaseInfo/Databases/Database[@Name='" + DbName + "']");
				String dbDisplayName = dbDisplayNameElement.getAttributeValue("Display_Name");
				sb.append("<Reagent><Fields Database_Name=\"" + dbDisplayName + "\">");
				
				for (int i = 1; i <= colCount; i++) {
					colName = rs.getMetaData().getColumnName(i);
					if (rs.getObject(i) == null || rs.getObject(i).equals("")) {
						sb.append("<Field Display_Name=\"" + getDisplayName(colName) + "\"");
						sb.append(" Data_Type=\"" + rs.getMetaData().getColumnClassName(i) + "\"/>");
					} else {
						sb.append("<Field Display_Name=\"" + getDisplayName(colName) + "\"");
						sb.append(" Data_Type=\"" + rs.getMetaData().getColumnClassName(i) + "\">");

						Object object = rs.getObject(i);

						// For structures - Blob or Clob - convert to string
						if (object instanceof byte[]) {
							object = new String((byte[]) object);
						} else if (object instanceof java.sql.Blob) {
							Blob blob = (Blob) object;
							object = new String(blob.getBytes(1, (int) blob.length()));
						} else if (object instanceof java.sql.Clob) {
							Clob clob = (Clob) object;
							object = clob.getSubString(1, (int) clob.length());
						} else if (object instanceof java.lang.Number) {
							DecimalFormat df = new DecimalFormat("0.000");
							object = df.format(object);
						}

						if (colName.equals("REAGENT_NAME") || hasXMLEntity(object) || hasStructure(object)) {
							sb.append("<![CDATA[" + object + "]]>" + "</Field>");
						} else {
							sb.append(object + "</Field>");
						}
					}
					// If user cancelled the search then break
					if (isInterrupted)
						break;
				}

				sb.append("</Fields></Reagent>");
				queryResults.add(sb.toString());
			}
			
			return queryResults;
		} catch (Exception ex) {
			log.info(com.chemistry.enotebook.util.ExceptionUtils.getStackTrace(ex));
			throw new ReagentMgmtException(ex);
		} finally {
		    log.debug(" Finally is called for closing thread--"+hashCode());
			try { if (rs != null)  rs.close(); }  catch (SQLException e) {
				log.info(com.chemistry.enotebook.util.ExceptionUtils.getStackTrace(e));
			}
			try { if (dbst != null)  dbst.close(); }  catch (SQLException e) { 
				log.info(com.chemistry.enotebook.util.ExceptionUtils.getStackTrace(e));
			}
			try { if (dbcon != null) dbcon.close(); } catch (SQLException e) { 
				log.info(com.chemistry.enotebook.util.ExceptionUtils.getStackTrace(e));
			}
			
		}
	}

	private String getDisplayName(String colName) {
		for (Object name : columnMap.keySet()) {
			if ((getCleanColumnName(name)).equalsIgnoreCase(colName))
				return (String) columnMap.get(name);
		}
		return null;
	}

	private String getCleanColumnName(Object name) {
		String as = " as "; // Oracle "AS" statement
		String nameString = (String) name;
		
		if (nameString.toLowerCase().contains(as)) {
			nameString = nameString.substring(nameString.toLowerCase().indexOf(as) + as.length());
		}
		
		return nameString;
	}
	
	private boolean hasStructure(Object object) {		
		if (object != null && object instanceof String) {
			String str = (String) object;
			return (str.contains("$RXN") || str.contains("M  END") || str.contains("M END"));
		}
		
		return false;
	}

	public int getSearchType() {
		return this.searchType;
	}

	public HashMap getSearchCriteriaMap() {
		return this.searchCriteriaMap;
	}

	public ArrayList getQueryResults(){
		return this.queryResults;
	}
	
	public Element getTextDB() {
		return this.textDB;
	}

	public ArrayList getTextDBList() {
		return this.textDBList;
	}

	public Element getReagentDBXMLRoot() {
		return this.reagentDBXMLRoot;
	}

	public String[] getCompoundList() {
		return this.compoundList;
	}

	public void setTextDB(Element tDB) {
		this.textDB = tDB;
	}

	public void setTextDBList(ArrayList tDBList) {
		this.textDBList = tDBList;
	}

	public void setReagentDBXMLRoot(Element reagentDBRoot) {
		this.reagentDBXMLRoot = reagentDBRoot;
	}

	public void setSearchCriteriaMap(HashMap sCriteriaMap) {
		this.searchCriteriaMap = sCriteriaMap;
	}

	public void setQueryResults(ArrayList results){
		this.queryResults = results;
	}
	
	public void setCompoundList(String[] cList) {
		this.compoundList = cList;
	}

	public void setSearchType(int sType) {
		this.searchType = sType;
	}

	public HashMap getColumnMap() {
		return this.columnMap;
	}

	public void setColumnMap(HashMap cMap) {
		this.columnMap = cMap;
	}

	public HashMap getDSMap() {
		return this.datasourceMap;
	}

	public void setDSMap(HashMap dsMap) {
		this.datasourceMap = dsMap;
	}

	/**
	 * @return Returns the isOutOfMemory.
	 */
	public boolean isOutOfMemory() {
		return isOutOfMemory;
	}
	/**
	 * @param isOutOfMemory The isOutOfMemory to set.
	 */
	public void setOutOfMemory(boolean isOutOfMemory) {
		this.isOutOfMemory = isOutOfMemory;
	}
	
	/*
	 * This method to terminates the thread. Removes this object's hashcode
	 * entry from the runnables ArrayList, closes the Statement and Connection
	 * Object.  
	 *  
	 * 
	 */
	 
	public void terminateThread()
	{
	    log.debug("Terminating Thread----:"+hashCode());
		runnables.remove(new Integer(this.hashCode()));
		isInterrupted = true;		
		this.interrupt();
		log.debug(" Terminating Statment abruptly!! --"+hashCode());
		try{
		    if(dbst != null)
		    {
		        dbst.cancel();
		        log.debug(" After Cancel stmt ");
		    	dbst.close();
		    }
		}catch (SQLException sqlError)
		{
			log.debug("Exception while closing the \"DBStatement\" " +
					"object on TimeOut.");
			log.info(com.chemistry.enotebook.util.ExceptionUtils.getStackTrace(sqlError));;
		}
		
		log.debug(" Terminating Connection abruptly!! --"+hashCode());
		try{
		    if(dbcon != null )
		    {
		        if((!dbcon.isClosed()))
		        {
		            dbcon.close();
		        	log.debug("dbcon is closed---"+dbcon.isClosed());
		        }
		    }
		}catch (SQLException sqlError)
		{
			log.debug("Exception while closing the \"DBConnection\" " +
					"object on TimeOut.");
			log.info(com.chemistry.enotebook.util.ExceptionUtils.getStackTrace(sqlError));
		}
		log.debug(" Terminating THREAD abruptly!!-- "+hashCode());
		
	}
	/*
	 * This inner class defines a timeout handler and gets called when
	 * the timer expires. The run method will call terminate method
	 * to end the running thread.
	 * 
	 * 
	 *
	 */
	public class Task extends TimerTask {
		public void run() {
			log.debug("Time's up!");
			terminateThread();
			timer.cancel(); //Terminate the timer thread
		}
	}
	
	/**
	 * 
	 * @param xmlStr
	 * @return
	 */
	private boolean hasXMLEntity(Object xmlValueObj){
		boolean result=false;
		String xmlStrValue="";
		if(xmlValueObj != null ) xmlStrValue = xmlValueObj.toString();
		if(		xmlStrValue.indexOf("<")>-1 || 
				xmlStrValue.indexOf(">")>-1 ||
				xmlStrValue.indexOf("&")>-1 ||
				xmlStrValue.indexOf("'")>-1 ||
				xmlStrValue.indexOf("\"")>-1){
			result=true;
		}
		return result;
	}
}