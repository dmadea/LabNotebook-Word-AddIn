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
 * Created on Jan 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.chemistry.enotebook.experiment.utils;

import com.chemistry.enotebook.experiment.utils.xml.JDomUtilException;
import com.chemistry.enotebook.experiment.utils.xml.JDomUtils;
import com.chemistry.enotebook.properties.CeNSystemXmlProperties;
import com.chemistry.enotebook.session.SystemProperties;
import com.chemistry.enotebook.session.delegate.SessionTokenDelegate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 * Without a getInstance this is a static object and the DOM is built each time.
 * 
 */
public class CeNSystemProperties {
	
	public static final Log log = LogFactory.getLog(CeNSystemProperties.class);
	
	private static final Object sitePropsLatch = new Object();
	private static final Object sitePropsLoadLatch = new Object();
	private static final Object sysPropsLatch = new Object();
	private static final Object sysPropsLoadLatch = new Object();
	private Document siteProps = null;
	private Document sysProps = null;
//	private static SystemProperties site = null;
//	private static SystemProperties gblSite = null;
	private static CeNSystemProperties instance = null;

	private static String foundACDLocally = null;
	private static String acdPath = null;
	
	private CeNSystemProperties() {
	}
	
	private static CeNSystemProperties getInstance() {
		if(instance == null) {
			synchronized(sitePropsLatch) {
				if(instance == null) {
					instance = new CeNSystemProperties();
				}
			}
		}
		return instance;
	}
	
	/**
	 * @param xpathVal
	 * @return
	 * @throws SystemPropertyException
	 */
	public static String getCeNServerSideSystemProperty(String xpathVal) throws SystemPropertyException {
		String value=null;
		try{
			value = getServerProperty(xpathVal);
		} catch (Exception e){
			// log so we don't lose the stack trace
			throw new SystemPropertyException("Failed to retrieve server property: " + xpathVal, e);
		}
		return value;
	}
	
	
	/**
	 * Pulls property from cached properties object - should have been populated from pull from DB.
	 * 
	 * @param xpathVal
	 * @return String value of the property
	 * @throws SystemPropertyException
	 */
	public static String getCeNSystemProperty(String xpathVal) throws SystemPropertyException {
		String value = null;
		
		try{
			value = getProperty(xpathVal, null);
		} catch (Exception e){
			log.error("Failed to retrieve server property: " + xpathVal, e);
			throw new SystemPropertyException("Failed to retrieve server property: " + xpathVal, e);
		}
		
		return value;
	
	}
	
	public static String getCeNSystemProperty(String xpathVal, String siteCode) throws SystemPropertyException {
		String value = null;
		
		try{
			value = getProperty(xpathVal, siteCode);
		} catch (Exception e){
			log.error("Failed to retrieve server property: " + xpathVal, e);
			throw new SystemPropertyException("Failed to retrieve server property: " + xpathVal, e);
		}
		
		return value;
	
	}	
	
	private static String getServerProperty(String xpathVal) throws SystemPropertyException {
		String result = null;
		
		if (getInstance().sysProps == null) {
			try {
				final SessionTokenDelegate session = new SessionTokenDelegate();
				log.debug("Local Session Delegate created!!-"+session);
				log.info("Getting GBL properties :");
				final SystemProperties gblSite = session.getSystemPropertiesOnServer("GBL");
				log.info("GBL Site  :" + gblSite);
				
				if (gblSite != null) {
					// For the very first time getValueInTag would be called since getDocFromString takes
					// quite sometime to convert the string into Document Object.
					result = getValueInTag(xpathVal, gblSite.getProperties());
					log.info("Result: "+result);
					Thread sysPropsThread = new Thread() {
						public void run() {
							try {
								log.debug("Loading System Properties...");
								synchronized(sysPropsLoadLatch) {
									log.debug("Reloading System Properties from Server...");
									getInstance().sysProps = JDomUtils.getDocFromString(gblSite.getProperties());
									log.debug("Finished reloading System Properties...");
								}
							} catch (Exception error) {
								log.error("Failed reloading system properties: extracting XML DOM document from string gblSite.getProperties() returns: " + gblSite.getProperties(), error);
							}
						}
					};
					sysPropsThread.start();
				}
				log.info("sys properties : "+ getInstance().sysProps);
			} catch (Exception e) {
				throw new SystemPropertyException("Failed to retrieve global properties", e);
			}
		}
		// try and extract the value from the global properties.
		// if ((result == null || result.length() == 0) && sysProps != null) {
		if (StringUtils.isEmpty(result) && getInstance().sysProps != null) {
			try {
				result = JDomUtils.getPrefFromDoc(getInstance().sysProps, xpathVal);
			} catch (JDomUtilException e) { /* Fail once it's ok. */
			}
		}
		if (result == null)
			result = "";
		return result;

	}
		
	/**
	 * Get the system XML properties stored in database via session service.
	 * @param xpathVal
	 * @param siteCode - null value will return global properties
	 * @return value of the property
	 * @throws SystemPropertyException
	 */
	private static String getProperty(String xpathVal, String siteCode) throws SystemPropertyException {
		String result = null;
		if (getInstance().siteProps == null) {
			try {
				// Make sure we are the only builder of the property cache
				synchronized (sitePropsLatch) {
					// Ensure no one got here first.
					if (getInstance().siteProps == null) {
						if (StringUtils.isNotBlank(siteCode)) {
							final SessionTokenDelegate session = new SessionTokenDelegate();

							final SystemProperties site = session.getSystemProperties(siteCode);
							if (site != null) {
								// For the very first time getValueInTag would be called since getDocFromString takes
								// quite sometime to convert the string into Document Object.
								result = getValueInTag(xpathVal, site.getProperties());
								//load the site properties in the background
								Thread sitePropsThread = new Thread() {
									public void run() {
										try {
											log.info("Loading CeN System Site Properties...");
											synchronized(sitePropsLoadLatch) {
												if (getInstance().siteProps == null) {
													getInstance().siteProps = JDomUtils.getDocFromString(site.getProperties());
													log.debug("Finished loading CeN System Site Properties.");
												} else {
													log.debug("Site props previously loaded! Skipping.");
												}
											}
										} catch (Exception error) {
											log.error("Failed extracting XML DOM document from string site.getProperties() returns: " + site.getProperties(), error);
										}
									}
								};
								sitePropsThread.start();
							}
						}
					}
				}
			} catch (Exception e) {
				throw new SystemPropertyException("Failed to retrieve site properties: "+ xpathVal +" for site " + siteCode, e);
			}
		}
		if (getInstance().sysProps == null) {
			try {
				// Make sure we are the only builder of the property cache
				synchronized (sysPropsLatch) {
					// Ensure no one got here first.
					if (getInstance().sysProps == null) {
						final SessionTokenDelegate session = new SessionTokenDelegate();
						final SystemProperties gblSite = session.getSystemProperties("GBL");
						if (gblSite != null) {
							// For the very first time getValueInTag would be called since getDocFromString takes
							// quite sometime to convert the string into Document Object.
							result = getValueInTag(xpathVal, gblSite.getProperties());
							// I seem to recall that this took soooo long we decided to skip the whole caching part 
							// in favor of just getting the value.  Hene the reason this was a static class
							Thread sysPropsThread = new Thread() {
								public void run() {
									try {
										log.debug("Loading System Properties...");
										synchronized(sysPropsLoadLatch) {
											if(getInstance().sysProps == null) {
												getInstance().sysProps = JDomUtils.getDocFromString(gblSite.getProperties());
												log.debug("Finished loading System Properties...");
											}
											else
											{
												log.debug("System Properties were previously loaded.  Skipping!");
											}
										}
									} catch (Exception error) {
										log.error("Failed extracting XML DOM document from string gblSite.getProperties() returns: " + gblSite.getProperties(), error);
									}
								}
							};
							sysPropsThread.start();
						}
					}
				}
			} catch (Exception e) {
				throw new SystemPropertyException("Failed to retrieve global properties", e);
			}
		}
		// try and extract the value from the site level properties first.
		if (StringUtils.isEmpty(result) && getInstance().siteProps != null) {
			try {
				result = JDomUtils.getPrefFromDoc(getInstance().siteProps, xpathVal);
			} catch (JDomUtilException e) { /* Fail once it's ok. */
			}
		}
		// try and extract the value from the global properties.
		// if ((result == null || result.length() == 0) && sysProps != null) {
		if (StringUtils.isEmpty(result) && getInstance().sysProps != null) {
			try {
				result = JDomUtils.getPrefFromDoc(getInstance().sysProps, xpathVal);
			} catch (JDomUtilException e) { /* Fail once it's ok. */
			}
		}
		if (result == null)
			result = "";
		return result;
	}

	/*
	 * Method introduced to perform the parsing of the xmlString and return the value specified for an attribute. This method is
	 * introduce to avoid the delay of the reading Property attributes at the time of startups.
	 */
	private static String getValueInTag(String tag, String xmlStr) {
		String value = null;
		StringBuffer initTag = new StringBuffer();
		StringBuffer endTag = new StringBuffer();
		java.util.StringTokenizer tagTok = new java.util.StringTokenizer(tag, "/");
		String xmlSubString = null;
		int startIndex = 0;
		int endIndex = 0;

		while (tagTok.hasMoreTokens()) {

			tag = tagTok.nextToken();
			initTag.append("<" + tag + ">");
			endTag.append("</" + tag + ">");
			startIndex = xmlStr.indexOf(initTag.toString());
			endIndex = xmlStr.indexOf(endTag.toString());
			if (startIndex == -1 || endIndex == -1) {
				xmlSubString = "";
				break;
			}
			xmlSubString = xmlStr.substring(startIndex, endIndex);

			if (tagTok.hasMoreTokens()) {
				initTag.replace(0, initTag.length(), "");
				endTag.replace(0, endTag.length(), "");
				xmlStr = xmlSubString;
			}
		}
		if (xmlSubString.equals("")) {
			value = xmlSubString;
		} else {
			value = xmlSubString.substring(initTag.length());
		}
		return value;
	}

	public static String getVersionNumber() throws SystemPropertyException {
		return getCeNSystemProperty(CeNSystemXmlProperties.PROP_VERSION_NUMBER);
	}

	public static String getRunMode() throws SystemPropertyException {
		return getCeNSystemProperty(CeNSystemXmlProperties.PROP_RUN_MODE);
	}

	public static String getSystemMessage() throws SystemPropertyException {
		return getCeNSystemProperty(CeNSystemXmlProperties.PROP_SYSTEM_MSG);
	}

	public static String getSystemAlertMessage() throws SystemPropertyException {
		return getCeNSystemProperty(CeNSystemXmlProperties.PROP_SYSTEM_ALERT_MSG);
	}

	public static String getMestreNovaNMRSoftware() throws SystemPropertyException
	{
		return CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_MESTRE_NOVA_NMR_PATH);
	}

    public static String getMestreNovaLCMSSoftware() throws SystemPropertyException
	{
		 return CeNSystemProperties.getCeNSystemProperty(CeNSystemXmlProperties.PROP_MESTRE_NOVA_LCMS_PATH);
	}

	public static String getACDNMRViewerFullyQualifiedPath() throws SystemPropertyException
	{	
		return getACDSoftwareFullyQualifiedPath() + File.separator + getACDNMRViewerExeName();
	}

	public static String getACDLCMSViewerFullyQualifiedPath() throws SystemPropertyException
	{	
		return getACDSoftwareFullyQualifiedPath() + File.separator + getACDLCMSViewerExeName();
	}

	private static boolean isACDInstalledLocally() throws SystemPropertyException
	{
		if (foundACDLocally == null) {		   	
			acdPath = "C:\\Program Files\\ACD14";
			for (int i=14; i >= 10; ) {
				File dir = new File(acdPath);			// Check w/o a "U"
				if (dir.exists()) {
					File exe = new File(acdPath + File.separator + getACDNMRViewerExeName());		// Applies to both NMR & LCMS, just using NMR to find the directory
					if (exe.exists()) break;
				}

				dir = new File(acdPath + "U");	// Check w/ a "U"
				if (dir.exists()) { 
					File exe = new File(acdPath + "U\\" + getACDNMRViewerExeName());
					if (exe.exists()) {
						acdPath += "U";  // Add "U" if it is found
						break;
					}
				}		

				acdPath = acdPath.replaceFirst("ACD"+i, "ACD"+(--i));
			}
			foundACDLocally = (acdPath.indexOf("ACD9") > 0) ? "N" : "Y";
			if (foundACDLocally.equals("Y")) {
				log.debug("ACD Labs found @ " + acdPath);
			} else {
				log.debug("ACD Labs not found locally");
			}
			// LCMS Specman.exe does not work if we use "Program Files" in the command line.
			if (StringUtils.isNotBlank(acdPath) && acdPath.contains("Program Files")) {
				acdPath = acdPath.replaceFirst("Program Files", "Progra~1");
			}
		}

		return (foundACDLocally != null && foundACDLocally.equals("Y"));
	}

	public static String getACDSoftwareFullyQualifiedPath() throws SystemPropertyException
	{
		if (isACDInstalledLocally()) {
			return acdPath;
		} else {
			if (getACDMachineName().startsWith(".\\"))
				return getACDMachineName().substring(2) + getACDSoftwarePath();
			else if (getACDMachineName().toLowerCase().startsWith("c:"))
				return getACDMachineName() + getACDSoftwarePath();
			else
				return "\\\\" + getACDMachineName() + getCeNSystemProperty(CeNSystemXmlProperties.PROP_ACD_SOFTWARE_PATH);
		}
	}

	public static String getACDMachineName() throws SystemPropertyException
	{	
		return getCeNSystemProperty(CeNSystemXmlProperties.PROP_ACD_MACHINE_NAME);
	}

	public static String getACDSoftwarePath() throws SystemPropertyException
	{
		return getCeNSystemProperty(CeNSystemXmlProperties.PROP_ACD_SOFTWARE_PATH);
	}

	public static String getACDNMRViewerExeName() throws SystemPropertyException {
		return getCeNSystemProperty(CeNSystemXmlProperties.PROP_ACD_SOFTWARE_NMR_VIEWER);
	}

	public static String getACDLCMSViewerExeName() throws SystemPropertyException
	{	
		return getCeNSystemProperty(CeNSystemXmlProperties.PROP_ACD_SOFTWARE_LCMS_VIEWER);
	}

	public static String getTemFilesThreshold() throws SystemPropertyException {
		return getCeNSystemProperty(CeNSystemXmlProperties.PROP_CEN_THRESHOLD_DEL_FILES);
	}

	/**
	 * This method retrieves temp file extensions from a SystemProperties Document. It uses getElementAt method of JDomUtils to
	 * retrieve the right element based of the Xpath and SystemProperties document. After file extensions are extracted from the
	 * document it traverses each element and stores its value in a list which is returned to the caller
	 * 
	 * 
	 * 
	 */
	public static List<String> getTempFileExtentions() throws SystemPropertyException {
		List<Element> result = null;
		ArrayList<String> tempList = null;
		Element el = null;
		if (getInstance().sysProps != null) {
			el = JDomUtils.getElementAt(getInstance().sysProps, CeNSystemXmlProperties.PROP_CEN_PREFIX_TEMFILES);
		}
		if (el != null)
			result = el.getChildren();
		if (result != null) {
			tempList = new ArrayList<String>(result.size());
			for (Iterator i = result.iterator(); i.hasNext();) {
				tempList.add(((Element) i.next()).getValue());
			}// end for
		}// end if
		return tempList;
	}
}
