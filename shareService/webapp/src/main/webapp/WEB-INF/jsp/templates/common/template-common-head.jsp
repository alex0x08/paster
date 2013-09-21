<%-- 
    Document   : template-common-head
    Created on : 20.09.2013, 9:55:31
    Author     : aachernyshev
--%>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


        <meta charset="utf-8">
        <title><fmt:message key="site.title"/></title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="<fmt:message key="site.title"/>">
        <meta name="author" content="Alex">

        <link href="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/css/bootstrap.min.css'/>" rel="stylesheet"/>
        <link href="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/css/bootstrap-theme.min.css'/>" rel="stylesheet"/>
        <style type="text/css">
           
            .sidebar-nav {
                padding: 9px 0;
            }
            .dropdown-menu {
             min-width: 0px;
            }

        </style>

        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/main/static/${appVersion}/css/main.css'/>" />
        <link href="<c:url value='/main/static/${appVersion}/libs/zoombox/zoombox.css'/>" rel="stylesheet" type="text/css" media="screen" />
      
        <link rel="shortcut icon" href="<c:url value='/favicon.png'/>">

          <script src="<c:url value='/main/assets/${appVersion}/stacktrace/0.5.0/stacktrace.js'/>"></script>

      <script src="<c:url value='/main/assets/${appVersion}/jquery/2.0.3/jquery.js'/>"></script>
      <script src="<c:url value='/main/assets/${appVersion}/jquery-ui/1.10.2/ui/minified/jquery-ui.min.js'/>"></script>
      
      <script src="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/js/bootstrap.min.js'/>"></script>
      <script src="<c:url value='/main/static/${appVersion}/libs/zoombox/zoombox.js'/>"></script>

       <script src="<c:url value='/main/assets/${appVersion}/prettify/4-Mar-2013/run_prettify.js'/>"></script> 