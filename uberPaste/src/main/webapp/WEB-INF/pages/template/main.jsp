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


        <!-- 
          I am based in LESS.js to render stylesheets. More information can
          be found at {http://www.lesscss.org/}.
        -->
        <script src="<c:url value="/js/less-1.1.3.min.js"/>"></script>
        <script src="<c:url value="/js/mootools-core-1.4.5-full-compat.js"/>"></script>
        <script src="<c:url value="/js/mootools-more-1.4.0.1.js"/>"></script>
        <script src="<c:url value="/libs/moodialog/js/Overlay.js"/>"></script>
        <script src="<c:url value="/libs/moodialog/js/MooDialog.js"/>"></script>

        <script src="<c:url value="/libs/moodialog/js/MooDialog.Fx.js"/>"></script>
        <script src="<c:url value="/libs/moodialog/js/MooDialog.Alert.js"/>"></script>
        <script src="<c:url value="/libs/moodialog/js/MooDialog.Confirm.js"/>"></script>
        <script src="<c:url value="/libs/moodialog/js/MooDialog.Error.js"/>"></script>
        <script src="<c:url value="/libs/moodialog/js/MooDialog.Prompt.js"/>"></script>
        <script src="<c:url value="/libs/moodialog/js/MooDialog.IFrame.js"/>"></script>
        <script src="<c:url value="/libs/moodialog/js/MooDialog.Request.js"/>"></script>

        <script src="<c:url value="/libs/growler/growler.js"/>"></script>

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


           <a href="<c:url value='/main/login'/>"><span style="font-size: 1em;vertical-align: middle;" class="i" title="Login here">x</span></a>

            <c:forEach var="server" items="${availableServers}">
                <a href="<c:url value='/act/openid-login?openid_identifier=${server.code}'/>">

                    <img src="<c:url value='/images/openid/${server.icon}'/>" alt="${server.name}" title="${server.name}" border="0" style=" vertical-align: middle; " />
                </a>
            </c:forEach>

        </sec:authorize>


            <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">



            <script type="text/javascript">

                var avatarUrl = 'http://www.gravatar.com/avatar/${currentUser.avatarHash}';

                window.addEvent('domready', function(){

                //First Example
                var el = $('avatarImageDiv'),
                color = el.getStyle('backgroundColor');

                // We are setting the opacity of the element to 0.5 and adding two events
                $('avatarImageDiv').addEvents({
                mouseenter: function(){

                    this.set('tween', {
                        duration: 1000,
                        transition: Fx.Transitions.Bounce.easeOut // This could have been also 'bounce:out'
                    }).tween('height', '140px');

                    $('avatarImage').src=avatarUrl+'?s=128';

                },
                mouseleave: function(){
                    this.set('tween', {}).tween('height', '40px');
                    $('avatarImage').src=avatarUrl+'?s=32';
                }
                });



                });
            </script>
               <div id="avatarImageDiv" >
                   <a href="http://ru.gravatar.com/site/check/${currentUser.username}" >
                       <img id="avatarImage" style="min-width: 40px;" alt="Gavatar" src="<c:out value='http://www.gravatar.com/avatar/${currentUser.avatarHash}?s=32'/>"/>
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
        <div class="column grid-14"  >
            <tiles:insertAttribute name="content" />

        </div>
     </div>



    </body>

</html>
