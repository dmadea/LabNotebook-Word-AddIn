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
package com.chemistry.enotebook.properties;

import com.chemistry.enotebook.servicelocator.PropertyReader;
import com.chemistry.enotebook.servicelocator.ServiceLocator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class CeNSystemXmlProperties {
	
	private static Log log = LogFactory.getLog(CeNSystemXmlProperties.class);
	
	private CeNSystemXmlProperties() {
	}

	// CeN Info Properties
	public static final String PROP_VERSION_NUMBER = "/Notebook_Properties/VersionNumber";
	public static final String PROP_RUN_MODE = "/Notebook_Properties/RunMode";
	public static final String PROP_SYSTEM_MSG = "/Notebook_Properties/SystemMessage";
	public static final String PROP_SYSTEM_STATUS = "/Notebook_Properties/Status";
	public static final String PROP_SYSTEM_STATUS_MSG = "/Notebook_Properties/StatusMessage";
	public static final String PROP_SYSTEM_ALERT_MSG = "/Notebook_Properties/AlertMessage";
    public static final String PROP_TIMEZONE_INFO = "/Notebook_Properties/TimeZones";

	// CeN System Modifiers
	public static final String PROP_NEW_NOTEBOOK = "/Notebook_Properties/Properties/Allow_Notebook_Create";
	public static final String PROP_NEW_CONCEPTION_ENABLED = "/Notebook_Properties/Properties/Allow_Conception_Create";
	public static final String PROP_NEW_SINGLETON_ENABLED = "/Notebook_Properties/Properties/Allow_Singleton_Create";
	public static final String PROP_NEW_PARALLEL_ENABLED = "/Notebook_Properties/Properties/Allow_Parallel_Create";	
	public static final String PROP_VIEW_PARALLEL_ENABLED = "/Notebook_Properties/Properties/Allow_Parallel_View";	
	public static final String PROP_REPEAT_PARALLEL_ENABLED = "/Notebook_Properties/Properties/Allow_Parallel_Repeat";
	
	public static final String PROP_VNV_ENABLED = "/Notebook_Properties/Properties/Allow_VnV";
	
	public static final String PROP_EDITOR_IDRAW = "/Notebook_Properties/Properties/DrawEditors/IsisDrawEnabled";
	public static final String PROP_EDITOR_CSDRAW = "/Notebook_Properties/Properties/DrawEditors/CSDrawEnabled";
	public static final String PROP_CACHE_EJB_HOMES = "/Notebook_Properties/Properties/CacheEJBHomes";
    public static final String PROP_VERIFY_NO_CHANGE = "/Notebook_Properties/Properties/VerifyNotChanged";
    public static final String PROP_NUM_COMPLANCE_DAYS = "/Notebook_Properties/Properties/Complete_Compliance_Days";

	// CeN Service Properties
	public static final String PROP_CEN_THRESHOLD_DEL_FILES = "/Notebook_Properties/Properties/TempFiles/Threshold";
	public static final String PROP_CEN_PREFIX_TEMFILES = "/Notebook_Properties/Properties/TempFiles/FilePrefixes";

	// CeN Table Preferences
	public static final String PROP_CEN_TABLE_PROPERTIES = "/Notebook_Properties/Table_Properties";
	public static final String PROP_CEN_REACTANTS_TABLE = "/Notebook_Properties/Table_Properties/Reactants_Table";
	public static final String PROP_CEN_INTENDED_PRODUCTS_TABLE = "/Notebook_Properties/Table_Properties/Intended_Products_Table";

	// CompoundRegistration Properties
	public static final String PROP_COMPOUND_REGISTRATION_AVAIL = "/Notebook_Properties/Services/CompoundRegistration/Available";
	public static final String PROP_COMPOUND_REGISTRATION_PAGE_TRUNCATE = "/Notebook_Properties/Services/CompoundRegistration/StripLeadingPageDigit";
	public static final String PROP_COMPOUND_REGISTRATION_SUBMIT_MG = "/Notebook_Properties/Services/CompoundRegistration/SubmitUsingMgs";
	public static final String PROP_COMPOUND_REGISTRATION_SRC_KNWN_DEF = "/Notebook_Properties/Services/CompoundRegistration/SourceKnownDef";
	public static final String PROP_COMPOUND_REGISTRATION_SRC_UNKNWN_DEF = "/Notebook_Properties/Services/CompoundRegistration/SourceUnknownDef";
	public static final String PROP_COMPOUND_REGISTRATION_SRCDTL_KNWN_DEF = "/Notebook_Properties/Services/CompoundRegistration/SourceDetailKnownDef";
	public static final String PROP_COMPOUND_REGISTRATION_SRCDTL_UNKNWN_DEF = "/Notebook_Properties/Services/CompoundRegistration/SourceDetailUnknownDef";
    public static final String PROP_COMPOUND_REGISTRATION_MAX_LIT_REF = "/Notebook_Properties/Services/CompoundRegistration/MaxLitRefSize";

	// CompoundAggregation Properties
	// IIOP URL retired for any app > JRE 1.4  since we are on 1.5 it won't work.
	public static final String PROP_COMPOUND_AGGREGATION_IIOP_URL = "/Notebook_Properties/Services/CompoundAggregation/iiopUrl";
	public static final String PROP_COMPOUND_AGGREGATION_HTTP_URL = "/Notebook_Properties/Services/CompoundAggregation/httpUrl";
	public static final String PROP_COMPOUND_AGGREGATION_USER_NAME = "/Notebook_Properties/Services/CompoundAggregation/UserName";
	public static final String PROP_COMPOUND_AGGREGATION_PASSWORD = "/Notebook_Properties/Services/CompoundAggregation/Password";
	
	public static final String PROP_COMPOUND_AGGREGATION_SERVICE_CENAPP_ACCOUNT = "/Notebook_Properties/Services/CompoundAggregation/CenApplicationAccount";
	public static final String PROP_COMPOUND_AGGREGATION_SERVICE_CENAPP_PASSWORD = "/Notebook_Properties/Services/CompoundAggregation/CenApplicationPassword";

	// Ties Properties
	public static final String PROP_MSDS_SEARCH = "/Notebook_Properties/Services/Ties/MSDSSearchUrl";

    // eSig Properties
    public static final String PROP_ESIG_SUBMIT_AVAIL = "/Notebook_Properties/Services/USSI/SubmissionsAllowed";
    public static final String PROP_ESIG_TIMER = "/Notebook_Properties/Services/USSI/UpdateTimerInMin";

	// AnalyticalService Properties
	public static final String PROP_ANALYTICAL_SERVICE_SITES = "/Notebook_Properties/Services/AnalyticalService/Sites";
	public static final String PROP_ANALYTICAL_SERVICE_INS_TYPES = "/Notebook_Properties/Services/AnalyticalService/InstrumentTypes";
	public static final String PROP_ANALYTICAL_SERVICE_CAN_MARK_AS_LINK = "/Notebook_Properties/Services/AnalyticalService/Can_Mark_As_Linked";
	public static final String PROP_ANALYTICAL_SERVICE_NO_OF_TABLES_SEARCH = "/Notebook_Properties/Services/AnalyticalService/No_Of_Tables_To_Search";
	public static final String PROP_ANALYTICAL_SERVICE_MAX_RESULTS = "/Notebook_Properties/Services/AnalyticalService/Max_No_Results";
    public static final String PROP_ANALYTICAL_SERVICE_UPLOAD_TYPES = "/Notebook_Properties/Services/AnalyticalService/Allowed_Upload_Types";
    public static final String PROP_ANALYTICAL_SERVICE_UPLOAD_EXT = "/Notebook_Properties/Services/AnalyticalService/Allowed_Upload_Extensions";

	// ACD Labs  Software
	public static final String PROP_ACD_MACHINE_NAME = "/Notebook_Properties/ACD_Labs/Software_Location/Machine_Name";
	public static final String PROP_ACD_SOFTWARE_PATH = "/Notebook_Properties/ACD_Labs/Software_Location/Path";
	public static final String PROP_ACD_SOFTWARE_NMR_VIEWER = "/Notebook_Properties/ACD_Labs/Software_Location/NMR_Viewer";
    public static final String PROP_ACD_SOFTWARE_LCMS_VIEWER = "/Notebook_Properties/ACD_Labs/Software_Location/LCMS_Viewer";

    // MestRe Nova NMR Software
    public static final String PROP_MESTRE_NOVA_NMR_PATH = "/Notebook_Properties/MestreNova/Software_Location/NMR_Viewer";
    public static final String PROP_MESTRE_NOVA_LCMS_PATH = "/Notebook_Properties/MestreNova/Software_Location/LCMS_Viewer";

	// Help Menu
	public static final String PROP_HELP_WEB_TRAIN = "/Notebook_Properties/Help/WebTrainingUrl";
	public static final String PROP_HELP_INSTRUCT_TRAIN = "/Notebook_Properties/Help/InstructorTrainingUrl";
	public static final String PROP_HELP_QUICK_START = "/Notebook_Properties/Help/QuickStartUrl";
	public static final String PROP_HELP_QUICK_REF = "/Notebook_Properties/Help/QuickReferenceUrl";
	public static final String PROP_HELP_Q_A = "/Notebook_Properties/Help/QuestionAnswerUrl";
	public static final String PROP_HELP_TEST_SCRIPTS = "/Notebook_Properties/Help/TestScriptsUrl";
	public static final String PROP_HELP_BUSINESS_RULES = "/Notebook_Properties/Help/BusinessRulesUrl";
    public static final String PROP_HELP_CEN_TLC = "/Notebook_Properties/Help/TlcTemplate";
    public static final String PROP_HELP_CEN_OFFLINE = "/Notebook_Properties/Help/OfflineTemplate";
	public static final String PROP_HELP_GRMG = "/Notebook_Properties/Help/GrmgUrl";
    public static final String PROP_HELP_RT003 = "/Notebook_Properties/Help/Rt003Url";
	public static final String PROP_HELP_ESIG_WEBSITE = "/Notebook_Properties/Help/eSigWebsite";
	public static final String PROP_HELP_SAFE = "/Notebook_Properties/Help/SAFESign";
	public static final String PROP_HELP_CERTS = "/Notebook_Properties/Help/SAFECerts";
	public static final String PROP_HELP_CEN_ESIG = "/Notebook_Properties/Help/CeNeSig";

	// Calculation Properties
	public static final String PROP_ALERT_ENABLED = "/Notebook_Properties/Services/CCT/SturctureAlertsEnabled";
	public static final String PROP_ALERT_SHOW_NONCHLORACNEGENS = "/Notebook_Properties/Services/CCT/ShowNonChloracnegens";
	public static final String PROP_ALERT_TEST_ONLYUNTESTEDBATCHES = "/Notebook_Properties/Services/CCT/TestOnlyUnTestedBatches";
	
	// Container Properties
	public static final String PROP_CONTAINER_VIAL_TYPE = "/Notebook_Properties/Containers/Vial/Type";
	public static final String PROP_CONTAINER_VIAL_LOCATION = "/Notebook_Properties/Containers/Vial/Location";
	public static final String PROP_CONTAINER_TUBE_TYPE = "/Notebook_Properties/Containers/Tube/Type";
	public static final String PROP_CONTAINER_TUBE_LOCATION = "/Notebook_Properties/Containers/Tube/Location";


	public static final String PROP_CONTAINER_PLATE_LOCATION = "/Notebook_Properties/Containers/Plate/Location";
//    public static final String PROP_CONTAINER_VIAL_LOCATION = "/Notebook_Properties/Containers/Vial/Location";
    //public static final String PROP_CONTAINER_TUBE_LOCATION = "/Notebook_Properties/Containers/Tube/Location";
    public static final String PROP_CONTAINER_TOTE_LOCATION = "/Notebook_Properties/Containers/Tote/Location";    
    public static final String PROP_CONTAINER_RACK_LOCATION = "/Notebook_Properties/Containers/Rack/Location";
    public static final String PROP_CONTAINER_UDPLATE_LOCATION = "/Notebook_Properties/Containers/UDPlate/Location";

	public static final String PROP_LOCAL_WEBLOGIC_MAX_MESSAGE_SIZE = "/Notebook_Properties/Properties/ClientMaxMessageSize";    
	public static final String PROP_LOCAL_CORBA_TIMOUT = "/Notebook_Properties/Properties/CORBATimeout";    
	
    private static final String[] TRUE_VALUES_ARRAY = {"true", "t", "TRUE", "T", "y", "yes", "Y", "YES", "on", "On", "ON"};
	public static final List<String> TRUE_VALUES_LIST = Arrays.asList( TRUE_VALUES_ARRAY );
	
	/**
	 * 
	 * @param propertyKey -
	 *            XML property key e.g. /Notebook_Properties/Services/CCT/SturctureAlertsEnabled
	 * @param siteCode -
	 *            Site code
	 * @return
	 * @throws Exception
	 */
	public static String getCeNProperty(String propertyKey, String siteCode) throws Exception {
		String result = null;

		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		
		String queryString = 
			      "SELECT site_code, x.xml_metadata " + "   FROM cen_properties x " + "   WHERE site_code = 'GBL'"
				+ ((siteCode != null && siteCode.length() > 0) ? " OR site_code = '" + siteCode + "'" : "");

		try {
			log.debug(queryString);

			conn = (ServiceLocator.getInstance().locateDataSource(PropertyReader.getJNDI(ServiceLocator.CEN_DS_JNDI))).getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(queryString);

			if (rs.next()) {
				// For GBL
				String xml_metadata = rs.getString("xml_metadata");
				
				String propertyGBL = getProperty(xml_metadata, propertyKey);
				String propertySite = "";
				
				if (rs.next()) {
					// For given Site Code
					xml_metadata = rs.getString("xml_metadata");
					propertySite = getProperty(xml_metadata, propertyKey);
				}
				
				if (StringUtils.isNotBlank(propertySite))
					result = propertySite;
				else 
					result = (propertyGBL == null) ? "" : propertyGBL;
			}
		} catch (Exception e) {
			log.debug("Failed to Retrieve CeN Property '" + propertyKey + "': " + e.getMessage(), e);
			throw e;
		} finally {
			try { if (rs != null)   rs.close(); } catch (SQLException e) { /* ignored */ }
			try { if (st != null)   st.close(); } catch (SQLException e) { /* ignored */ }
			try { if (conn != null) conn.close(); } catch (SQLException e) { /* ignored */ }
		}

		log.debug("Value for CeN Property '" + propertyKey + "': " + result);
		
		return result;
	}
	
	public static String getProperty(String xmlMetadata, String propertyKey) {
		String result = "";
		
		try {
			result = getNode(xmlMetadata, propertyKey).getTextContent();
		} catch (Exception e) {
		}
		
		return result;
	}
	
	public static String getPropertyAsXml(String xmlMetadata, String propertyKey) {
		String result = "";
		
		try {
			Node node = getNode(xmlMetadata, propertyKey);
			
			StreamResult xmlOutput = new StreamResult(new StringWriter());
	        
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        transformer.transform(new DOMSource(node), xmlOutput);
	        
	        result = xmlOutput.getWriter().toString();
		} catch (Exception e) {
		}
			
		return result;
	}
	
	private static Node getNode(String xmlMetadata, String propertyKey) {
		Node result = null;
		
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xmlMetadata)));
			Object obj = XPathFactory.newInstance().newXPath().evaluate(propertyKey, doc, XPathConstants.NODE);
			result = (Node) obj;
		} catch (Exception e) {
		}
		
		return result;
	}

	public static String updateProperty(String xmlMetadata, String propertyKey, String value) {
		String result = xmlMetadata;
		
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xmlMetadata)));
			Object node = XPathFactory.newInstance().newXPath().evaluate(propertyKey, doc, XPathConstants.NODE);
			
			((Node)node).setTextContent(value);
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			
			StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            
            result = writer.getBuffer().toString();
		} catch (Exception e) {
		}
		
		return result;
	}
}
