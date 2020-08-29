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
package com.chemistry.enotebook.reagent.ejb;

import com.chemistry.enotebook.compoundmgmtservice.exception.CompoundNotFoundException;
import com.chemistry.enotebook.hazard.HazardServiceFactory;
import com.chemistry.enotebook.hazard.exceptions.HazardException;
import com.chemistry.enotebook.reagent.dao.*;
import com.chemistry.enotebook.reagent.delegate.ReagentCallBackInterface;
import com.chemistry.enotebook.reagent.exceptions.ReagentInvalidTokenException;
import com.chemistry.enotebook.reagent.exceptions.ReagentMgmtException;
import com.chemistry.enotebook.reagent.interfaces.ReagentMgmtService;
import com.chemistry.enotebook.reagent.util.ZIPUtil;
import com.chemistry.enotebook.reagent.valueobject.IteratingVO;
import com.chemistry.enotebook.reagent.valueobject.UserInfoVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ReagentMgmtServiceEJB implements ReagentMgmtService {

    private static final Log log = LogFactory.getLog(ReagentMgmtServiceEJB.class);

    private TextSearchDao textDao = null;
    private StructureDao structureDao = null;
    private TextStructureDao textStructureDao = null;
    private ReagentDBXMLDao reagentDBXMLDao = null;
    private MyReagentsDao myReagentsDao = null;

    private Element reagentDBInfoRoot = null;
    private ArrayList reagentList = new ArrayList();
    private IteratingVO iteratingVO = new IteratingVO();

    private HashMap<String, String> resultColumnMap = new HashMap<String, String>();
    private HashMap<String, String> searchColumnMap = new HashMap<String, String>();
    private HashMap<String, String> dataSourceMap = new HashMap<String, String>();

    private final int TEXT_SEARCH = 1;
    private final int STRUCTURE_SEARCH = 2;
    private final int TEXT_STRUCTURE_SEARCH = 3;
    private int searchType = 0;

    public void init() throws ReagentMgmtException {
        log.debug("CeNReagentMgmtService EJB - ejbCreate() start");
        
        try {
            if (reagentDBXMLDao == null) initReagentDBXMLDao();

            //retrieve the reagentDBInfo
            String reagentDBInfo = reagentDBXMLDao.getReagentDBXML("GBL");

            StringReader reader = null;
            reader = new StringReader(reagentDBInfo);
            SAXBuilder builder = new SAXBuilder();

            Document doc = builder.build(reader);
            reagentDBInfoRoot = doc.getRootElement();
            buildResultColumnMap();
            buildSearchColumnMap();
            buildDataSourceMap();
        } catch (JDOMException e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        } catch (IOException e2) {
            throw new ReagentMgmtException(e2.getMessage(), e2);
        }
        
        log.debug("CeNReagentMgmtService EJB - ejbCreate() end");
    }

    /**
     * @param UserID
     * @return byte[]
     * @throws RemoteException
     * interface-method view-type = "remote"
     * transaction type = "NotSupported"
     */
    @Override
    public byte[] getMyReagentList(String UserID)
            throws ReagentMgmtException, ReagentInvalidTokenException             //need to define the return type:XML type
    {
        String myReagentsList = null;

//		boolean isValidSession = false;
//		try {
//			SessionLocalDelegate sd = new SessionLocalDelegate();
//			isValidSession = sd.validateUser(sessionID);
//		} catch (Exception e) {
//			throw new ReagentInvalidTokenException("ReagentMgmtEJB:getMyReagentList:: Error handling Session Token", e);
//		}
//		if (!isValidSession) throw new ReagentInvalidTokenException("ReagentMgmtEJB:getMyReagentList:: Invalid Session Token");


        try {
            if (myReagentsDao == null) initMyReagentsDao();

            myReagentsList = myReagentsDao.getMyReagentList(UserID);
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }

        //send compressed data.
        return ZIPUtil.zip(myReagentsList.getBytes());
    }

    @Override
    public void UpdateMyReagentList(UserInfoVO myInfo, String MyReagentList)
            throws ReagentMgmtException, ReagentInvalidTokenException {
//		boolean isValidSession = false;
//		try {
//			SessionLocalDelegate sd = new SessionLocalDelegate();
//			isValidSession = sd.validateUser(sessionID);
//		} catch (Exception e) {
//			throw new ReagentInvalidTokenException("ReagentMgmtEJB:UpdateMyReagentList:: Error handling Session Token", e);
//		}
//		if (!isValidSession) throw new ReagentInvalidTokenException("ReagentMgmtEJB:UpdateMyReagentList:: Invalid Session Token");


        try {
            if (myReagentsDao == null) initMyReagentsDao();

            myReagentsDao.UpdateMyReagentList(myInfo, MyReagentList);
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }
    }

    @Override
    public void UpdateMyReagentList(String userName, byte[] MyReagentList)
            throws ReagentMgmtException, ReagentInvalidTokenException {
//		boolean isValidSession = false;
//		try {
//			SessionLocalDelegate sd = new SessionLocalDelegate();
//			isValidSession = sd.validateUser(sessionID);
//		} catch (Exception e) {
//			throw new ReagentInvalidTokenException("ReagentMgmtEJB:UpdateMyReagentList:: Error handling Session Token", e);
//		}
//		if (!isValidSession) throw new ReagentInvalidTokenException("ReagentMgmtEJB:UpdateMyReagentList:: Invalid Session Token");


        try {
            if (myReagentsDao == null) initMyReagentsDao();

            myReagentsDao.UpdateMyReagentList(userName, MyReagentList);
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }
    }

    /**
     * This method returns the database info containing all the available
     * databases, tables used to search, the searching fields in each table and
     * result fields in each table <br>
     */
    @Override
    public String getDBInfo(String SiteID)
            throws ReagentInvalidTokenException, ReagentMgmtException {
        try {
            if (reagentDBXMLDao == null) initReagentDBXMLDao();

            return reagentDBXMLDao.getReagentDBXML(SiteID);
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }
    }

    @Override
    public void updateReagentDBXML(String SiteCode, String reagentDBXML)
            throws  ReagentMgmtException {
        try {
            if (reagentDBXMLDao == null) initReagentDBXMLDao();

            reagentDBXMLDao.updateReagentDBXML(SiteCode, reagentDBXML);
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }
    }

    /**
     * This method retrives the reagents info based on the search
     * parameters
     *
     * @param String searchParamsXML
     * @return byte[]
     * interface-method view-type = "remote"
     * transaction type = "NotSupported"
     */
    @Override
    public byte[] doReagentsSearch(String searchParamsXML,
                                   ReagentCallBackInterface cbInterface)
            throws ReagentMgmtException, ReagentInvalidTokenException {
//		boolean isValidSession = false;
//	    log.debug("SessionID = "+sessionID.getTokenString());
//		try {
//			SessionLocalDelegate sd = new SessionLocalDelegate();
//			isValidSession = sd.validateUser(sessionID);
//		} catch (Exception e) {
//			throw new ReagentInvalidTokenException("ReagentMgmtEJB:doReagentsSearch:: Error handling Session Token", e);
//		}
//		if (!isValidSession) throw new ReagentInvalidTokenException("ReagentMgmtEJB:doReagentsSearch:: Invalid Session Token");


        Collection collection = null;
        boolean isDoIterating = true;
        try {
            //check if to do the iterating
            StringReader reader = new StringReader(searchParamsXML);
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(reader);
            Element root = doc.getRootElement();

            if (!isDoIterating(root)) {//perform db search
                isDoIterating = false;

                reagentList = new ArrayList();
                iteratingVO = new IteratingVO();

                //dispatching search operation based on the search type
                Element textIndicator = (Element) XPath.selectSingleNode(root, "/ReagentsLookupParams/TextDatabases");
                boolean doTextSearch = new Boolean(textIndicator.getAttributeValue("DoTextSearch")).booleanValue();
                Element structureIndicator = (Element) XPath.selectSingleNode(root, "/ReagentsLookupParams/StructureDatabases");
                boolean doStructureSearch = new Boolean(structureIndicator.getAttributeValue("DoStructureSearch")).booleanValue();

                if (doTextSearch && doStructureSearch) {
                    searchType = TEXT_STRUCTURE_SEARCH;
                    log.debug("Started TEXT_STRUCTURE_SEARCH");
                    collection = doReagentsSearchByStructureAndText(root, cbInterface);
                } else if (doTextSearch) {
                    searchType = TEXT_SEARCH;
                    log.debug("Started TEXT_SEARCH");
                    collection = doReagentsSearchByText(root, cbInterface);
                } else {
                    searchType = STRUCTURE_SEARCH;
                    log.debug("Started STRUCTURE_SEARCH");
                    collection = doReagentsSearchByStructure(root, cbInterface);
                }
                if (collection != null) {
                    reagentList = new ArrayList(collection);
                    iteratingVO.setReagentsTotal(reagentList.size());
                    iteratingVO.setLastPosition(0);
                }
            }

            if (!isDoIterating && collection == null) {
                //it is better to send some useful message than null
                return ZIPUtil.zip(buildEmptyReturn().getBytes());
            } else {
                String reagentsResult = doIterating(iteratingVO.getLastPosition(), iteratingVO.calculateLastPos());
                return ZIPUtil.zip(reagentsResult.getBytes());
            }
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }
    }

    /**
     * This method searches the struture by given compound number
     *
     * @param String compoundNumber
     * @return String
     * interface-method view-type = "remote"
     * transaction type = "NotSupported"
     */
    @Override
    public String getStructureByCompoundNo(String compoundNumber)
            throws ReagentInvalidTokenException, ReagentMgmtException, CompoundNotFoundException {
        try {
            if (structureDao == null) initStructureDao();

            return structureDao.getStructureByCompoundNo(compoundNumber);
        } catch (CompoundNotFoundException e1) {
            throw e1;
        } catch (Exception e2) {
            throw new ReagentMgmtException(e2.getMessage(), e2);
        }
    }

    /*
      * private methods
      *
      */

    private String buildEmptyReturn() {
        //log.debug("The lastpos = " + lastPos + " and the currentPos = " + currentPos);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<Reagents>");

        //need to add one more node to indicate if still data remaining
        sb.append("<IteratingInfo HasMore=\"False\" LastPosition=\"0\"  Total=\"-1\" />");
        sb.append("</Reagents>");
        //iteratingVO.setLastPosition(iteratingVO.calculateLastPos());

        return sb.toString();
    }

    /**
     * @param lastPos    TODO
     * @param currentPos TODO
     * @return
     */
    private String doIterating(int lastPos, int currentPos) {
        //log.debug("The lastpos = " + lastPos + " and the currentPos = " + currentPos);
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<Reagents>");

        for (int i = lastPos; i < currentPos; i++) {
            sb.append(reagentList.get(i));
        }
        //update the last position
        iteratingVO.setLastPosition(iteratingVO.calculateLastPos());

        //need to add one more node to indicate if still data remaining
        sb.append("<IteratingInfo HasMore=\"" + iteratingVO.ifHasMore()
                + "\" LastPosition=\"" + currentPos
                + "\" Total=\"" + iteratingVO.getReagentsTotal() + "\"/>");
        sb.append("</Reagents>");
        //iteratingVO.setLastPosition(iteratingVO.calculateLastPos());

        return sb.toString();
    }

    /**
     * @param paramsXMLRoot
     * @return @throws
     *         JDOMException
     * @throws IOException
     */
    private boolean isDoIterating(Element paramsXMLRoot)
            throws ReagentMgmtException {
        try {
            Element iteratingElement = (Element) XPath.selectSingleNode(
                    paramsXMLRoot, "/ReagentsLookupParams/Iterating");
            String lastPosString = iteratingElement.getAttributeValue("LastPosition");
            String chunkSize = iteratingElement.getAttributeValue("ChunkSize");
            iteratingVO.setIteratingSize((new Integer(chunkSize)).intValue());
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

    /**
     * This method performs the reagents search by text
     *
     * @param Element                  searchParamsXMLRoot
     * @param ReagentCallBackInterface cbObject
     * @return Collection
     */
    private Collection doReagentsSearchByText(Element searchParamsXMLRoot,
                                              ReagentCallBackInterface cbObject)
            throws ReagentMgmtException {
        try {
            if (textDao == null) initTextDao();

            textDao.setReagentDBXMLRoot(reagentDBInfoRoot);
            textDao.setSearchColumnMap(searchColumnMap);
            textDao.setResultColumnMap(resultColumnMap);
            textDao.setDSMap(dataSourceMap);

            return textDao.doReagentsSearchByText(searchParamsXMLRoot, cbObject);
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }
    }

    /**
     * This method performs the reagents search by structure
     *
     * @param Element searchParamsXMLRoot
     * @return Collection
     */
    private Collection doReagentsSearchByStructure(Element searchParamsXMLRoot,
                                                   ReagentCallBackInterface cbObject)
            throws ReagentMgmtException {
        try {
            if (structureDao == null) initStructureDao();

            structureDao.setReagentDBXMLRoot(reagentDBInfoRoot);
            structureDao.setColumnMap(resultColumnMap);
            structureDao.setDSMap(dataSourceMap);

            return structureDao.doReagentsSearchByStructure(searchParamsXMLRoot, cbObject);
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }
    }

    /**
     * This method performs the reagents search by structure and text
     *
     * @param Element searchParamsXMLRoot
     * @return Collection
     */
    private Collection doReagentsSearchByStructureAndText(Element searchParamsXMLRoot,
                                                          ReagentCallBackInterface cbObject)
            throws  ReagentMgmtException {
        try {
            if (textStructureDao == null) initTextStructureDao();

            textStructureDao.setReagentDBXMLRoot(reagentDBInfoRoot);
            textStructureDao.setResultColumnMap(resultColumnMap);
            textStructureDao.setSearchColumnMap(searchColumnMap);
            textStructureDao.setDSMap(dataSourceMap);

            return textStructureDao.doTextAndStructureSearch(searchParamsXMLRoot, cbObject);
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }
    }


    /**
     * This method buils the result coulmn name and display name map
     */
    private void buildResultColumnMap()
            throws  ReagentMgmtException {
        try {
            //construct culumn list
            List resultFiledList = XPath.selectNodes(reagentDBInfoRoot,
                    "/ReagentsDatabaseInfo/Databases/Database/Tables/Table/Result_Fields/Field");
            for (int i = 0; i < resultFiledList.size(); i++) {
                Element resultFiledElement = (Element) resultFiledList.get(i);
                String columnName = resultFiledElement.getAttributeValue("Column_Name");
                int dotPosition = columnName.indexOf(".");
                String colName = columnName.substring(dotPosition + 1, columnName.length());
                if (colName.endsWith("REAGENT_NAME")) {
                    colName = "REAGENT_NAME";
                }
                resultColumnMap.put(colName, resultFiledElement.getAttributeValue("Display_Name"));
//				log.debug("The column name is: " + columnName.substring(dotPosition + 1,columnName.length()));
//				log.debug("The display name is: " + resultFiledElement.getAttributeValue("Display_Name"));
            }

//			log.debug("The column map size is: " + columnMap.size());
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }
    }

    /**
     * This method buils the coulmn name and display name map
     */
    private void buildSearchColumnMap()
            throws ReagentMgmtException {
        try {
            //construct culumn list
            List searchFiledList = XPath.selectNodes(reagentDBInfoRoot,
                    "/ReagentsDatabaseInfo/Databases/Database/Tables/Table/Search_Fields/Field");
            for (int i = 0; i < searchFiledList.size(); i++) {
                Element searchFiledElement = (Element) searchFiledList.get(i);
                String columnName = searchFiledElement.getAttributeValue("Column_Name");
                int dotPosition = columnName.indexOf(".");
                int spacePosition = columnName.lastIndexOf(" ");
                if (spacePosition >= 0) dotPosition = spacePosition;
                String colName = columnName.substring(dotPosition + 1, columnName.length());
                if (colName.endsWith("REAGENT_NAME")) {
                    colName = "REAGENT_NAME";
                }
                searchColumnMap.put(colName, searchFiledElement.getAttributeValue("Display_Name"));
            }
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }
    }

    /**
     * This method buils the database and datasource JNDI map
     */
    private void buildDataSourceMap()
            throws ReagentMgmtException {
        try {
            //construct database list
            List databaseList = XPath.selectNodes(reagentDBInfoRoot, "/ReagentsDatabaseInfo/Databases/Database");
            for (int i = 0; i < databaseList.size(); i++) {
                Element databaseElement = (Element) databaseList.get(i);

                dataSourceMap.put(databaseElement.getAttributeValue("Name"), databaseElement.getAttributeValue("Datasource"));
            }
        } catch (Exception e) {
            throw new ReagentMgmtException(e.getMessage(), e);
        }
    }

    private void initTextDao() {
        textDao = (TextSearchDao) OracleDAOFactory.getDAOFactory(OracleDAOFactory.ORACLE).createDao("TextSearchDao");
    }

    private void initStructureDao() {
        structureDao = (StructureDao) OracleDAOFactory.getDAOFactory(OracleDAOFactory.ORACLE).createDao("StructureDao");
    }

    private void initTextStructureDao() {
        textStructureDao = (TextStructureDao) OracleDAOFactory.getDAOFactory(OracleDAOFactory.ORACLE).createDao("TextStructureDao");
    }

    private void initReagentDBXMLDao() {
        reagentDBXMLDao = (ReagentDBXMLDao) OracleDAOFactory.getDAOFactory(OracleDAOFactory.ORACLE).createDao("ReagentDBXMLDao");
    }

    private void initMyReagentsDao() {
        myReagentsDao = (MyReagentsDao) OracleDAOFactory.getDAOFactory(OracleDAOFactory.ORACLE).createDao("MyReagentsDao");
    }

    @Override
    public String getHazardInfo(String id, String idtype, String lang) throws HazardException {
    	try {
			return HazardServiceFactory.getService().getHazardInfo(id, idtype, lang);
		} catch (Exception e) {
			throw new HazardException(e);
		}
    }

    @Override
	public boolean getHazardInfoHealth() throws HazardException {
		try {
			return HazardServiceFactory.getService().getHazardInfoHealth();
		} catch (Exception e) {
			throw new HazardException(e);
		}
	}
}
