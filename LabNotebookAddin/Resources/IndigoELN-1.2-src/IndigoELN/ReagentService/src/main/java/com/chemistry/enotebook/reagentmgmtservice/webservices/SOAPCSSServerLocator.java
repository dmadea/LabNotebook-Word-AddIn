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
///**
// * SOAPCSSServerLocator.java
// *
// * This file was auto-generated from WSDL
// * by the Apache Axis WSDL2Java emitter.
// */
//
//package com.chemistry.enotebook.reagentmgmtservice.webservices;
//
//public class SOAPCSSServerLocator extends org.apache.axis.client.Service implements SOAPCSSServer {
//
//    // com.dtc.CSS_SOAPInterface.SOAPCSSServer web service
//
//    // Use to get a proxy class for SOAPCSSServerSoap
//    public java.lang.String getSOAPCSSServerSoapAddress() {
//        return SOAPCSSServerSoap_address;
//    }
//
//    // The WSDD service name defaults to the port name.
//    private java.lang.String SOAPCSSServerSoapWSDDServiceName = "SOAPCSSServerSoap";
//
//    public java.lang.String getSOAPCSSServerSoapWSDDServiceName() {
//        return SOAPCSSServerSoapWSDDServiceName;
//    }
//
//    public void setSOAPCSSServerSoapWSDDServiceName(java.lang.String name) {
//        SOAPCSSServerSoapWSDDServiceName = name;
//    }
//
//    public SOAPCSSServerSoap getSOAPCSSServerSoap() throws javax.xml.rpc.ServiceException {
//       java.net.URL endpoint;
//        try {
//            endpoint = new java.net.URL(SOAPCSSServerSoap_address);
//        }
//        catch (java.net.MalformedURLException e) {
//            throw new javax.xml.rpc.ServiceException(e);
//        }
//        return getSOAPCSSServerSoap(endpoint);
//    }
//
//    public SOAPCSSServerSoap getSOAPCSSServerSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
//        try {
//            SOAPCSSServerSoapStub _stub = new SOAPCSSServerSoapStub(portAddress, this);
//            _stub.setPortName(getSOAPCSSServerSoapWSDDServiceName());
//            return _stub;
//        }
//        catch (org.apache.axis.AxisFault e) {
//            return null;
//        }
//    }
//
//    /**
//     * For the given interface, get the stub implementation.
//     * If this service has no port for the given interface,
//     * then ServiceException is thrown.
//     */
//    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
//        try {
//            if (SOAPCSSServerSoap.class.isAssignableFrom(serviceEndpointInterface)) {
//                SOAPCSSServerSoapStub _stub = new SOAPCSSServerSoapStub(new java.net.URL(SOAPCSSServerSoap_address), this);
//                _stub.setPortName(getSOAPCSSServerSoapWSDDServiceName());
//                return _stub;
//            }
//        }
//        catch (java.lang.Throwable t) {
//            throw new javax.xml.rpc.ServiceException(t);
//        }
//        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
//    }
//
//    /**
//     * For the given interface, get the stub implementation.
//     * If this service has no port for the given interface,
//     * then ServiceException is thrown.
//     */
//    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
//        if (portName == null) {
//            return getPort(serviceEndpointInterface);
//        }
//        String inputPortName = portName.getLocalPart();
//        if ("SOAPCSSServerSoap".equals(inputPortName)) {
//            return getSOAPCSSServerSoap();
//        }
//        else  {
//            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
//            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
//            return _stub;
//        }
//    }
//
//    public javax.xml.namespace.QName getServiceName() {
//        return new javax.xml.namespace.QName("http://www.themindelectric.com/wsdl/SOAPCSSServer/", "SOAPCSSServer");
//    }
//
//    private java.util.HashSet ports = null;
//
//    public java.util.Iterator getPorts() {
//        if (ports == null) {
//            ports = new java.util.HashSet();
//            ports.add(new javax.xml.namespace.QName("SOAPCSSServerSoap"));
//        }
//        return ports.iterator();
//    }
//
//}
