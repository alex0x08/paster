<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en">
    <head>

        <link rel="icon" href="<c:url value='/images/ninja.png'/>"/>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

        <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9" />

        <title><tiles:insertAttribute name="pageTitle" /></title>

        <!-- 
   The {app.less} file is magic. It loads the perkins configurations,
   establishes your preferences and calls me. You will write all your
   code (or most of it) here, all the time.
        -->

        <link href="<c:url value="/css/app.less"/>" media="all" rel="stylesheet/less" />

        <link href="<c:url value="/libs/moodialog/css/MooDialog.css"/>" rel="stylesheet" type="text/css">
        <link href="<c:url value="/libs/growler/growler.css"/>" rel="stylesheet" type="text/css">

        <link href="<c:url value="/libs/lightface/Assets/LightFace.css"/>" rel="stylesheet" type="text/css">

        <!-- 
          I am based in LESS.js to render stylesheets. More information can
          be found at {http://www.lesscss.org/}.
        -->
        <script src="<c:url value='/js/less-1.1.3.min.js'/>"></script>
        <script src="<c:url value='/js/mootools-core-1.4.5-full-compat.js'/>"></script>



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


           <a href="<c:url value='/main/login'/>"><span style="font-size: 1em;vertical-align: middle;" class="i" title="Login here">x</span></a>

            <c:forEach var="server" items="${availableServers}">
                <a href="<c:url value='/act/openid-login?openid_identifier=${server.code}'/>">

                    <img src="<c:url value='/images/openid/${server.icon}'/>" alt="${server.name}" title="${server.name}" border="0" style=" vertical-align: middle; " />
                </a>
            </c:forEach>

        </sec:authorize>


            <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">

                <jsp:include
                        page="/WEB-INF/pages/common/currentUser.jsp">
                <jsp:param name="currentUser" value="${currentUser}" />
            </jsp:include>

            </sec:authorize>

        </div>
        </div>

    <div class="row">
        <div class="column grid-2">
        </div>
        <div class="column grid-14"  >
            <tiles:insertAttribute name="content" />
        </div>
     </div>


    <script src="<c:url value='/js/mootools-more-1.4.0.1.js'/>"></script>
    <script src="<c:url value="/libs/growler/growler.js"/>"></script>


    <script type="text/javascript">

        var growl= null;

        window.addEvent('domready',function() {
            growl = new Growler.init();

        });
    </script>



    <script type="text/javascript" src="<c:url value="/libs/syntax-highlight/scripts/xregexp.js"/>"></script>

    <script type="text/javascript" src="<c:url value="/libs/syntax-highlight/scripts/shCore.js"/>"></script>

    <script type="text/javascript" src="<c:url value="/libs/syntax-highlight/scripts/shAutoloader.js"/>"></script>

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




    </body>

</html>
