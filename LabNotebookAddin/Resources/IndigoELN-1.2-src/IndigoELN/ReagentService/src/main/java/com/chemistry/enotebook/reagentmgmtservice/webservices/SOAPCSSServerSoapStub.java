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
// * SOAPCSSServerSoapStub.java
// *
// * This file was auto-generated from WSDL
// * by the Apache Axis WSDL2Java emitter.
// */
//
//package com.chemistry.enotebook.reagentmgmtservice.webservices;
//
//public class SOAPCSSServerSoapStub extends org.apache.axis.client.Stub implements SOAPCSSServerSoap {
//    private java.util.Vector cachedSerClasses = new java.util.Vector();
//    private java.util.Vector cachedSerQNames = new java.util.Vector();
//    private java.util.Vector cachedSerFactories = new java.util.Vector();
//    private java.util.Vector cachedDeserFactories = new java.util.Vector();
//
//    static org.apache.axis.description.OperationDesc [] _operations;
//
//    static {
//        _operations = new org.apache.axis.description.OperationDesc[12];
//        org.apache.axis.description.OperationDesc oper;
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("main");
//        oper.addParameter(new javax.xml.namespace.QName("", "args"), new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"), java.lang.String[].class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[0] = oper;
//
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("getDatabaseList");
//        oper.addParameter(new javax.xml.namespace.QName("", "dbType"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.setReturnType(new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"));
//        oper.setReturnClass(java.lang.String[].class);
//        oper.setReturnQName(new javax.xml.namespace.QName("", "Result"));
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[1] = oper;
//
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("getDatabaseTypes");
//        oper.setReturnType(new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"));
//        oper.setReturnClass(java.lang.String[].class);
//        oper.setReturnQName(new javax.xml.namespace.QName("", "Result"));
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[2] = oper;
//
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("getSearchTypes");
//        oper.addParameter(new javax.xml.namespace.QName("", "dbType"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.setReturnType(new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"));
//        oper.setReturnClass(java.lang.String[].class);
//        oper.setReturnQName(new javax.xml.namespace.QName("", "Result"));
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[3] = oper;
//
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("structureSearchSimple");
//        oper.addParameter(new javax.xml.namespace.QName("", "dbType"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "dbName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "resultField"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "molString"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "srchOperator"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "srchOptionValues"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "bReturnAsXML"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "bIncludeStructureData"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.setReturnType(new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"));
//        oper.setReturnClass(java.lang.String[].class);
//        oper.setReturnQName(new javax.xml.namespace.QName("", "Result"));
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[4] = oper;
//
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("structureSearchExt");
//        oper.addParameter(new javax.xml.namespace.QName("", "dbType"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "dbList"), new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"), java.lang.String[].class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "fldList"), new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"), java.lang.String[].class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "molDefn"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "srchOperator"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "srchOptionValues"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "bReturnAsXML"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "bIncludeStructureData"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.setReturnType(new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"));
//        oper.setReturnClass(java.lang.String[].class);
//        oper.setReturnQName(new javax.xml.namespace.QName("", "Result"));
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[5] = oper;
//
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("searchStructure");
//        oper.addParameter(new javax.xml.namespace.QName("", "molDefn"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "srchOperator"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "srchOptionValues"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "bReturnAsXML"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "bIncludeStuctureData"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.setReturnType(new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"));
//        oper.setReturnClass(java.lang.String[].class);
//        oper.setReturnQName(new javax.xml.namespace.QName("", "Result"));
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[6] = oper;
//
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("getStructureData");
//        oper.addParameter(new javax.xml.namespace.QName("", "inputCompoundIdList"), new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"), java.lang.String[].class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "bReturnAsXML"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.setReturnType(new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"));
//        oper.setReturnClass(java.lang.String[].class);
//        oper.setReturnQName(new javax.xml.namespace.QName("", "Result"));
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[7] = oper;
//
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("getStructure");
//        oper.addParameter(new javax.xml.namespace.QName("", "inputCompoundId"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "bReturnAsXML"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        oper.setReturnClass(java.lang.String.class);
//        oper.setReturnQName(new javax.xml.namespace.QName("", "Result"));
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[8] = oper;
//
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("getStructures");
//        oper.addParameter(new javax.xml.namespace.QName("", "inputCompoundIdList"), new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"), java.lang.String[].class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "bReturnAsXML"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.setReturnType(new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"));
//        oper.setReturnClass(java.lang.String[].class);
//        oper.setReturnQName(new javax.xml.namespace.QName("", "Result"));
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[9] = oper;
//
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("getValidStructure");
//        oper.addParameter(new javax.xml.namespace.QName("", "inputCompoundId"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "bReturnAsXML"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
//        oper.setReturnClass(java.lang.String.class);
//        oper.setReturnQName(new javax.xml.namespace.QName("", "Result"));
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[10] = oper;
//
//        oper = new org.apache.axis.description.OperationDesc();
//        oper.setName("getValidStructures");
//        oper.addParameter(new javax.xml.namespace.QName("", "inputCompoundIdList"), new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"), java.lang.String[].class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.addParameter(new javax.xml.namespace.QName("", "bReturnAsXML"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, org.apache.axis.description.ParameterDesc.IN, false, false);
//        oper.setReturnType(new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring"));
//        oper.setReturnClass(java.lang.String[].class);
//        oper.setReturnQName(new javax.xml.namespace.QName("", "Result"));
//        oper.setStyle(org.apache.axis.enum.Style.RPC);
//        oper.setUse(org.apache.axis.enum.Use.ENCODED);
//        _operations[11] = oper;
//
//    }
//
//    public SOAPCSSServerSoapStub() throws org.apache.axis.AxisFault {
//         this(null);
//    }
//
//    public SOAPCSSServerSoapStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
//         this(service);
//         super.cachedEndpoint = endpointURL;
//    }
//
//    public SOAPCSSServerSoapStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
//        if (service == null) {
//            super.service = new org.apache.axis.client.Service();
//        } else {
//            super.service = service;
//        }
//            java.lang.Class cls;
//            javax.xml.namespace.QName qName;
//            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
//            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
//            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
//            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
//            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
//            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
//            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
//            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
//            qName = new javax.xml.namespace.QName("http://www.themindelectric.com/package/java.lang/", "ArrayOfstring");
//            cachedSerQNames.add(qName);
//            cls = java.lang.String[].class;
//            cachedSerClasses.add(cls);
//            cachedSerFactories.add(arraysf);
//            cachedDeserFactories.add(arraydf);
//
//    }
//
//    private org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
//        try {
//            org.apache.axis.client.Call _call =
//                    (org.apache.axis.client.Call) super.service.createCall();
//            if (super.maintainSessionSet) {
//                _call.setMaintainSession(super.maintainSession);
//            }
//            if (super.cachedUsername != null) {
//                _call.setUsername(super.cachedUsername);
//            }
//            if (super.cachedPassword != null) {
//                _call.setPassword(super.cachedPassword);
//            }
//            if (super.cachedEndpoint != null) {
//                _call.setTargetEndpointAddress(super.cachedEndpoint);
//            }
//            if (super.cachedTimeout != null) {
//                _call.setTimeout(super.cachedTimeout);
//            }
//            if (super.cachedPortName != null) {
//                _call.setPortName(super.cachedPortName);
//            }
//            java.util.Enumeration keys = super.cachedProperties.keys();
//            while (keys.hasMoreElements()) {
//                java.lang.String key = (java.lang.String) keys.nextElement();
//                _call.setProperty(key, super.cachedProperties.get(key));
//            }
//            // All the type mapping information is registered
//            // when the first call is made.
//            // The type mapping information is actually registered in
//            // the TypeMappingRegistry of the service, which
//            // is the reason why registration is only needed for the first call.
//            synchronized (this) {
//                if (firstCall()) {
//                    // must set encoding style before registering serializers
//                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
//                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
//                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
//                        javax.xml.namespace.QName qName =
//                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
//                        java.lang.Class sf = (java.lang.Class)
//                                 cachedSerFactories.get(i);
//                        java.lang.Class df = (java.lang.Class)
//                                 cachedDeserFactories.get(i);
//                        _call.registerTypeMapping(cls, qName, sf, df, false);
//                    }
//                }
//            }
//            return _call;
//        }
//        catch (java.lang.Throwable t) {
//            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", t);
//        }
//    }
//
//    public void main(java.lang.String[] args) throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[0]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("main");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "main"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {args});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        extractAttachments(_call);
//    }
//
//    public java.lang.String[] getDatabaseList(java.lang.String dbType) throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[1]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("getDatabaseList");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "getDatabaseList"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {dbType});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        else {
//            extractAttachments(_call);
//            try {
//                return (java.lang.String[]) _resp;
//            } catch (java.lang.Exception _exception) {
//                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
//            }
//        }
//    }
//
//    public java.lang.String[] getDatabaseTypes() throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[2]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("getDatabaseTypes");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "getDatabaseTypes"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        else {
//            extractAttachments(_call);
//            try {
//                return (java.lang.String[]) _resp;
//            } catch (java.lang.Exception _exception) {
//                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
//            }
//        }
//    }
//
//    public java.lang.String[] getSearchTypes(java.lang.String dbType) throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[3]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("getSearchTypes");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "getSearchTypes"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {dbType});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        else {
//            extractAttachments(_call);
//            try {
//                return (java.lang.String[]) _resp;
//            } catch (java.lang.Exception _exception) {
//                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
//            }
//        }
//    }
//
//    public java.lang.String[] structureSearchSimple(java.lang.String dbType, java.lang.String dbName, java.lang.String resultField, java.lang.String molString, java.lang.String srchOperator, java.lang.String srchOptionValues, boolean bReturnAsXML, boolean bIncludeStructureData) throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[4]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("structureSearchSimple");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "structureSearchSimple"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {dbType, dbName, resultField, molString, srchOperator, srchOptionValues, new java.lang.Boolean(bReturnAsXML), new java.lang.Boolean(bIncludeStructureData)});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        else {
//            extractAttachments(_call);
//            try {
//                return (java.lang.String[]) _resp;
//            } catch (java.lang.Exception _exception) {
//                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
//            }
//        }
//    }
//
//    public java.lang.String[] structureSearchExt(java.lang.String dbType, java.lang.String[] dbList, java.lang.String[] fldList, java.lang.String molDefn, java.lang.String srchOperator, java.lang.String srchOptionValues, boolean bReturnAsXML, boolean bIncludeStructureData) throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[5]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("structureSearchExt");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "structureSearchExt"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {dbType, dbList, fldList, molDefn, srchOperator, srchOptionValues, new java.lang.Boolean(bReturnAsXML), new java.lang.Boolean(bIncludeStructureData)});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        else {
//            extractAttachments(_call);
//            try {
//                return (java.lang.String[]) _resp;
//            } catch (java.lang.Exception _exception) {
//                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
//            }
//        }
//    }
//
//    public java.lang.String[] searchStructure(java.lang.String molDefn, java.lang.String srchOperator, java.lang.String srchOptionValues, boolean bReturnAsXML, boolean bIncludeStuctureData) throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[6]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("searchStructure");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "searchStructure"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {molDefn, srchOperator, srchOptionValues, new java.lang.Boolean(bReturnAsXML), new java.lang.Boolean(bIncludeStuctureData)});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        else {
//            extractAttachments(_call);
//            try {
//                return (java.lang.String[]) _resp;
//            } catch (java.lang.Exception _exception) {
//                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
//            }
//        }
//    }
//
//    public java.lang.String[] getStructureData(java.lang.String[] inputCompoundIdList, boolean bReturnAsXML) throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[7]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("getStructureData");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "getStructureData"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {inputCompoundIdList, new java.lang.Boolean(bReturnAsXML)});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        else {
//            extractAttachments(_call);
//            try {
//                return (java.lang.String[]) _resp;
//            } catch (java.lang.Exception _exception) {
//                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
//            }
//        }
//    }
//
//    public java.lang.String getStructure(java.lang.String inputCompoundId, boolean bReturnAsXML) throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[8]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("getStructure");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "getStructure"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {inputCompoundId, new java.lang.Boolean(bReturnAsXML)});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        else {
//            extractAttachments(_call);
//            try {
//                return (java.lang.String) _resp;
//            } catch (java.lang.Exception _exception) {
//                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
//            }
//        }
//    }
//
//    public java.lang.String[] getStructures(java.lang.String[] inputCompoundIdList, boolean bReturnAsXML) throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[9]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("getStructures");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "getStructures"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {inputCompoundIdList, new java.lang.Boolean(bReturnAsXML)});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        else {
//            extractAttachments(_call);
//            try {
//                return (java.lang.String[]) _resp;
//            } catch (java.lang.Exception _exception) {
//                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
//            }
//        }
//    }
//
//    public java.lang.String getValidStructure(java.lang.String inputCompoundId, boolean bReturnAsXML) throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[10]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("getValidStructure");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "getValidStructure"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {inputCompoundId, new java.lang.Boolean(bReturnAsXML)});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        else {
//            extractAttachments(_call);
//            try {
//                return (java.lang.String) _resp;
//            } catch (java.lang.Exception _exception) {
//                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
//            }
//        }
//    }
//
//    public java.lang.String[] getValidStructures(java.lang.String[] inputCompoundIdList, boolean bReturnAsXML) throws java.rmi.RemoteException {
//        if (super.cachedEndpoint == null) {
//            throw new org.apache.axis.NoEndPointException();
//        }
//        org.apache.axis.client.Call _call = createCall();
//        _call.setOperation(_operations[11]);
//        _call.setUseSOAPAction(true);
//        _call.setSOAPActionURI("getValidStructures");
//        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
//        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/com.dtc.CSS_SOAPInterface.SOAPCSSServer", "getValidStructures"));
//
//        setRequestHeaders(_call);
//        setAttachments(_call);
//        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {inputCompoundIdList, new java.lang.Boolean(bReturnAsXML)});
//
//        if (_resp instanceof java.rmi.RemoteException) {
//            throw (java.rmi.RemoteException)_resp;
//        }
//        else {
//            extractAttachments(_call);
//            try {
//                return (java.lang.String[]) _resp;
//            } catch (java.lang.Exception _exception) {
//                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
//            }
//        }
//    }
//
//}
