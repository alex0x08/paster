<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>

    <link rel="icon" href="<c:url value='/images/ninja.png'/>"/>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9" />

    <title><tiles:insertAttribute name="pageTitle" /></title>
    <!--
       <link href="http://fonts.googleapis.com/css?family=PT+Sans:regular,italic,bold" rel="stylesheet" type="text/css">
       <link href="http://fonts.googleapis.com/css?family=PT+Sans+Narrow:regular,bold" rel="stylesheet" type="text/css">
        -->

    <!--
The {app.less} file is magic. It loads the perkins configurations,
establishes your preferences and calls me. You will write all your
code (or most of it) here, all the time.
    -->

    <link href="<c:url value='/css/app-integrated.less'/>" media="all" rel="stylesheet/less" />

    <link href="<c:url value='/libs/moodialog/css/MooDialog.css'/>" rel="stylesheet" type="text/css">
    <link href="<c:url value='/libs/growler/growler.css'/>" rel="stylesheet" type="text/css">


    <!--
      I am based in LESS.js to render stylesheets. More information can
      be found at {http://www.lesscss.org/}.
    -->
    <script src="<c:url value='/js/less-1.1.3.min.js'/>"></script>
    <script src="<c:url value='/js/mootools-core-1.4.5-full-compat.js'/>"></script>
    <script src="<c:url value='/js/mootools-more-1.4.0.1.js'/>"></script>
    <script src="<c:url value='/libs/moodialog/js/Overlay.js'/>"></script>
    <script src="<c:url value='/libs/moodialog/js/MooDialog.js'/>"></script>

    <script src="<c:url value='/libs/moodialog/js/MooDialog.Fx.js'/>"></script>
    <script src="<c:url value='/libs/moodialog/js/MooDialog.Alert.js'/>"></script>
    <script src="<c:url value='/libs/moodialog/js/MooDialog.Confirm.js'/>"></script>
    <script src="<c:url value='/libs/moodialog/js/MooDialog.Error.js'/>"></script>
    <script src="<c:url value='/libs/moodialog/js/MooDialog.Prompt.js'/>"></script>
    <script src="<c:url value='/libs/moodialog/js/MooDialog.IFrame.js'/>"></script>
    <script src="<c:url value='/libs/moodialog/js/MooDialog.Request.js'/>"></script>

    <script src="<c:url value='/libs/growler/growler.js'/>"></script>

    <script type="text/javascript">

    var growl= null;

    window.addEvent('domready',function() {
        growl = new Growler.init();


    });
    </script>


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



    <script type="text/javascript" src="<c:url value='/libs/syntax-highlight/scripts/xregexp.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/libs/syntax-highlight/scripts/shCore.js'/>"></script>

    <script type="text/javascript" src="<c:url value='/libs/syntax-highlight/scripts/shAutoloader.js'/>"></script>

    <link type="text/css" rel="stylesheet" href="<c:url value='/libs/syntax-highlight/styles/shCoreDefault.css'/>"/>

    </head>
            <body>


            <div class="row">
                <div class="column grid-16">


            <c:if test="${not empty statusMessageKey}">
            <p><fmt:message key="${statusMessageKey}"/></p>
            </c:if>

            </div>
            <div class="column grid-4" >



            <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">
                <jsp:include
                        page="/WEB-INF/pages/common/currentUser.jsp">
                    <jsp:param name="currentUser" value="${currentUser}" />
                </jsp:include>
             </sec:authorize>


</div>
</div>

<div class="row">
    <div class="column grid-18"  >
        <tiles:insertAttribute name="content" />

    </div>
</div>



</body>

</html>
