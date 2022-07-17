<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" trimDirectiveWhitespaces="true" %>
<%--

          Global taglibs configuration
--%>    

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<%--
        Global variables used on all pages
--%>    


<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="datePattern"><fmt:message key="date.format"/></c:set>
<c:set var="dateTimePattern"><fmt:message key="date.format.hours"/></c:set>

<%--<sec:authentication property="principal" var="currentUser" />--%>

