<%--
    Reads a value of CEN_PROPERTY table.  JWS launched client will access this page
    to get the service URL of the CeN server application.
--%>
<%@ page import="com.chemistry.enotebook.properties.CeNSystemXmlProperties" %>
<%= CeNSystemXmlProperties.getCeNProperty(request.getParameter("property"), null) %>