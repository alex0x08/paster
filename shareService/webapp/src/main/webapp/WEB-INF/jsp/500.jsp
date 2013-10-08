<%-- 
    Document   : 404
    Created on : Nov 30, 2011, 5:26:31 AM
    Author     : alex
--%>

<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" trimDirectiveWhitespaces="true"
         import="uber.megashare.base.SystemInfo"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% response.setStatus(500); %>


<% 

String appVersion = SystemInfo.getInstance().getRuntimeVersion().getImplBuildNum();

 pageContext.setAttribute("appVersion", appVersion);

%>

<!DOCTYPE html>
<html>
<head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>- ooops! -</title>
  <link href="<c:url value='/main/static/${appVersion}/css/error.css'/>" rel="stylesheet" type="text/css" media="all" />
</head>
<body>
<span class="neg">ERROR 500</span>
<p>
The page is missing or never was written. You can wait and<br />
see if it becomes available again, or you can restart your computer.	
</p>
<p>
* Send us an e-mail to notify this and try it later.<br />

* Press CTRL+ALT+DEL to restart your computer. You will<br />
 &nbsp; lose unsaved information in any programs that are running.
</p>
Press any link to continue <blink>_</blink>
<div class="menu">
<a href="index.html">index</a> | 
<a href="contact.html">webmaster</a> | 
</div>

</body>
</html>
