<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en">
    <head>

        <link rel="icon" href="<c:url value='/images/paste.png'/>"/>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

        <title><tiles:insertAttribute name="pageTitle" /></title>

        <link href="http://fonts.googleapis.com/css?family=PT+Sans:regular,italic,bold" rel="stylesheet" type="text/css">
        <link href="http://fonts.googleapis.com/css?family=PT+Sans+Narrow:regular,bold" rel="stylesheet" type="text/css">

        <!-- 
   The {app.less} file is magic. It loads the perkins configurations,
   establishes your preferences and calls me. You will write all your
   code (or most of it) here, all the time.
        -->

        <link href="<c:url value="/css/app.less"/>" media="all" rel="stylesheet/less" />



        <!-- 
          I am based in LESS.js to render stylesheets. More information can
          be found at {http://www.lesscss.org/}.
        -->
        <script src="<c:url value="/js/less-1.1.3.min.js"/>"></script>
        <script src="<c:url value="/js/mootools-core-1.4.5-full-compat.js"/>"></script>
        <script src="<c:url value="/js/mootools-more-1.4.0.1.js"/>"></script>

        <!-- 
          Under development, this section allows you to preview your changes
          immediately after saving them on the {app.less} file. However, you
          must remove it once it enters the production stage or it will open
          a bunch of requests to your server to retrieve new changes. 
        -->
        <script charset="utf-8">
            //less.env = "development";
            //less.watch();
        </script>

        <!--[if lt IE 9]>
          <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->



        <script type="text/javascript" src="<c:url value="/libs/syntax-highlight/scripts/xregexp.js"/>"></script>

        <script type="text/javascript" src="<c:url value="/libs/syntax-highlight/scripts/shCore.js"/>"></script>

        <script type="text/javascript" src="<c:url value="/libs/syntax-highlight/scripts/shAutoloader.js"/>"></script>
 
        <link type="text/css" rel="stylesheet" href="<c:url value="/libs/syntax-highlight/styles/shCoreDefault.css"/>"/>

    </head>
    <body>

    <div class="row">
        <div class="column grid-12">

            <tiles:insertAttribute name="menu" />

            <c:if test="${not empty statusMessageKey}">
                <p><fmt:message key="${statusMessageKey}"/></p>
            </c:if>

        </div>
        <div class="column grid-4">

        <sec:authorize access="!isAuthenticated()" >

            <c:forEach var="server" items="${availableServers}">
                <a href="<c:url value='/act/openid-login?openid_identifier=${server.code}'/>">

                    <img src="<c:url value='/images/openid/${server.icon}'/>" alt="${server.name}" title="${server.name}" border="0" style=" vertical-align: middle; " />
                </a>
            </c:forEach>

        </sec:authorize>


            <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">

               <div style="">
                   <a href="http://ru.gravatar.com/site/check/${currentUser.username}" title="GAvatar">
                       <img style="min-width: 40px; " src="<c:out value='http://www.gravatar.com/avatar/${currentUser.avatarHash}?s=32'/>"/>
                   </a>

                <span style="display: inline; vertical-align: top; ">

                        <a title="Contact ${currentUser.name}" style="display: inline;vertical-align: top;" href="mailto:${currentUser.username}"><c:out value="${currentUser.name}" /></a>

                    <a class="btn sbtn" style="display: inline;vertical-align: top;" title="Logout" href="<c:url value="/act/logout"/>">X</a>

                </span>

               </div>

            </sec:authorize>


        </div>
        </div>

    <div class="row">
        <div class="column grid-2">
        </div>
        <div class="column grid-14">
            <tiles:insertAttribute name="content" />

        </div>
     </div>



    </body>

</html>
