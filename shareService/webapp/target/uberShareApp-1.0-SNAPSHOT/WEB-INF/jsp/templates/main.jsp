<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><fmt:message key="site.title"/></title>
<link rel="stylesheet" type="text/css" media="all"  href="<c:url value="/css/libraries.css"/>" />
<link rel="stylesheet" type="text/css" media="all"  href="<c:url value="/css/template.css"/>" />
<!--<link rel="stylesheet" type="text/css" media="all"  href="css/template_debug.css" />-->
<link rel="stylesheet" type="text/css" media="all" href="<c:url value="/css/grids.css"/>" />
<link rel="stylesheet" type="text/css" media="all" href="<c:url value="/css/content.css"/>" />

<link rel="stylesheet" type="text/css" media="all" href="<c:url value="/css/main.css"/>" />
 <link rel="icon" href="<c:url value='/favicon.ico'/>"/>


</head>
<body>

<div class="page">
	<div class="head">
           
	<sec:authorize ifAnyGranted="ROLE_ADMIN">
            
	    <jsp:useBean id="statsBean" scope="page"
                     class="uber.megashare.web.StatsBean"/>
  	   
	Total Memory: <c:out value="${statsBean.totalMemory}"/>  MB <br/>
	Heap Max Size: <c:out value="${statsBean.heapMaxSize}"/>  MB <br/>
	Heap Free Size: <c:out value="${statsBean.heapFreeSize}"/>  MB <br/>
	 </sec:authorize>
           
	
	    
	    <div id="headerTitle">
    <a href="<c:url value="/"/>"><fmt:message key="site.title"/></a>    
</div>

<div class="subHeader">
    <div>

        <div style="float:left;" >
        	<tiles:insertAttribute name="header" />
        </div>


        <div style="display:inline;float:right;" >
            <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
                <a href="<c:url value="/main/login"/>"><fmt:message key="login.title"/></a>

            </sec:authorize>
            <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
                Logged in as
               
                 <img style="display:inline;vertical-align:middle;width:16px;" src="<c:out value='http://www.gravatar.com/avatar/${currentUser.avatarHash}?s=16'/>"/>
        		<sec:authentication property="principal.username" />
                
                <a href="<c:url value="/act/doLogout"/>"><fmt:message key="button.logout"/></a>

            </sec:authorize>
        </div>
    </div>
    <div style="clear:both;"/>
</div>

	    
	</div>
	<div class="body">
            <div class="leftCol" style="padding-right: 2em;" >
                    <tiles:insertAttribute name="menu" />
                </div>
		<div class="main">
		<tiles:insertAttribute name="content" />
                </div>
	</div>
	<div class="foot" style="padding-top: 2em;" >
            <tiles:insertAttribute name="footer" />
	</div>	
</div>
</body>
</html>
