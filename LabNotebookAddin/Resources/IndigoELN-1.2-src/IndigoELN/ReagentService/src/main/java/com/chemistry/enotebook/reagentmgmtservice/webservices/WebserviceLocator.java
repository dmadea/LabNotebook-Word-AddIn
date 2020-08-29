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
package com.chemistry.enotebook.reagentmgmtservice.webservices;
///*
// * Created on Sep 1, 2004
// *
// * TODO To change the template for this generated file go to
// * Window - Preferences - Java - Code Style - Code Templates
// */
//package com.chemistry.enotebook.reagentmgmtservice.webservices;
//
//import java.util.*;
//import java.rmi.*;
//import javax.xml.rpc.*;
//
//
///**
// * 
// *
// * TODO To change the template for this generated type comment go to
// * Window - Preferences - Java - Code Style - Code Templates
// */
//public class WebserviceLocator {
//	
//	public static final String CSS_WEBSERVICE = "css_webservice";
//
//
//
//    /**
//     *  Description of the Field
//     */
//
//
//    /**
//     *  Description of the Field
//     */
//    private static WebserviceLocator locator = null;
//
//    /**
//     *  Description of the Field
//     */
//    private static Map webServiceCache = null;
//
//
//    /**
//     *  Constructor for the WebserviceLocator object
//     */
//    public WebserviceLocator() {
//        webServiceCache = Collections.synchronizedMap(new HashMap());
//    }
//
//
//    /**
//     *  Gets the instance attribute of the WebserviceLocator class
//     *
//     *@return    The instance value
//     */
//    public static WebserviceLocator getInstance() {
//
//        if (locator == null) {
//            locator = new WebserviceLocator();
//        }
//        return locator;
//    }
//
//
//    /**
//     *  Gets the webService attribute of the WebserviceLocator object
//     *
//     *@param  webServiceName                  Description of the Parameter
//     *@return                                 The webService value
//     *@exception  WebServiceLocatorException  Description of the Exception
//     */
//    public Remote getWebService(String webServiceName) throws
//            WebServiceLocatorException {
//        
//        if (webServiceName.equals(CSS_WEBSERVICE)) {
//            return getCSSService();
//        }
//
//        return null;
//    }
//
//
//    
//    /**
//     *  Gets the SOAPCSSServerSoapStub of the WebserviceLocator object
//     *
//     *@return                                 The SOAPCSSServerSoapStub
//     *@exception  WebServiceLocatorException  Description of the Exception
//     */
//    private Remote getCSSService() throws WebServiceLocatorException {
//            try {
//                if (webServiceCache.containsKey(
//                		CSS_WEBSERVICE)) {
//                    return (Remote) webServiceCache.get(
//                    		CSS_WEBSERVICE);
//                } else {
//
//                	SOAPCSSServerSoapStub binding = (SOAPCSSServerSoapStub)new SOAPCSSServerLocator().getSOAPCSSServerSoap();
//
//                    webServiceCache.put(
//                    		CSS_WEBSERVICE, binding);
//                    return binding;
//                }
//            } catch (ServiceException ex) {
//                throw new WebServiceLocatorException(
//                        "can't get CSSWebservice" + ex.toString(), ex);
//            }
//    }
//
//}
//
