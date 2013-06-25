<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>
<html >
    <head>
        <meta charset="utf-8">
        <title><fmt:message key="site.title"/></title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        <link href="<c:url value='/assets/css/bootstrap.css'/>" rel="stylesheet">
        <style type="text/css">
            body {
                padding-top: 60px;
                padding-bottom: 40px;
            }
            .sidebar-nav {
                padding: 9px 0;
            }
        </style>
        <link href="<c:url value='/assets/css/bootstrap-responsive.css'/>" rel="stylesheet">

        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->


        <link rel="stylesheet" type="text/css" media="all" href="<c:url value="/css/main.css"/>" />

        <!-- Le fav and touch icons -->
        <link rel="shortcut icon" href="<c:url value='/assets/ico/favicon.ico'/>">
        <link rel="apple-touch-icon-precomposed" sizes="144x144" 
              href="<c:url value='/assets/ico/apple-touch-icon-144-precomposed.png'/>">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" 
              href="<c:url value='/assets/ico/apple-touch-icon-114-precomposed.png'/>">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" 
              href="<c:url value='/assets/ico/apple-touch-icon-72-precomposed.png'/>">
        <link rel="apple-touch-icon-precomposed" 
              href="<c:url value='/assets/ico/apple-touch-icon-57-precomposed.png'/>">

        <script src="<c:url value='/assets/js/jquery.js'/>"></script>

    </head>

    <body>

        
        <div class="navbar navbar-inverse navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container-fluid">
                    <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </a>

                    <a class="brand" href="<c:url value="/"/>"><fmt:message key="site.title"/></a>    


                    <div class="nav-collapse collapse navbar-responsive-collapse">

                        <tiles:insertAttribute name="header" />

                        <ul class="nav">
                            <li >
                                <a  href="<c:url value="/main/file/list"/>">
                                    <fmt:message key="files.title"/></a>
                            </li>
                            <li>
                                <sec:authorize ifAnyGranted="ROLE_ADMIN,ROLE_USER">    
                                <li>
                                    <a  href="<c:url value="/main/file/new"/>"><fmt:message key="file.upload.title"/></a> 
                                </li>
                            </sec:authorize>
                            </li>
                        </ul>
                        <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">


                            <div class="btn-group pull-right">

                                <a class="btn dropdown-toggle btn-danger" data-toggle="dropdown" href="#">
                                    <fmt:message key="login.title"/>
                                    <span class="caret"></span>
                                </a>

                                <ul class="dropdown-menu" id="auth-dropdown">


                                    <li style="padding-right:5px;padding-left: 5px;" >


                                        <form  action="<c:url value="/j_security_check" />"  method="POST">



                                            <div class="input-prepend">
                                                <span class="add-on"><i class="icon-user"></i></span>
                                                <input class="span2"  name="j_username" type="text" placeholder="Username">
                                            </div>



                                            <div class="input-prepend">
                                                <span class="add-on"><i class="icon-password"></i></span>
                                                <input class="span2"  name="j_password" type="password" placeholder="Password">
                                            </div>




                                            <label class="checkbox">
                                                <input type="checkbox" name="_spring_security_remember_me"> <fmt:message key="login.rememberMe"/>
                                            </label>

                                            <button type="submit" class="btn">Sign in</button>


                                        </form>
                                    </li>


                                </ul>
                            </div>



                        </sec:authorize>
                        <sec:authorize ifAnyGranted="ROLE_USER,ROLE_ADMIN">

                            <div class="btn-group pull-right">

                                <a class="btn dropdown-toggle btn-primary" data-toggle="dropdown" href="#">
                                    <img  src="<c:out value='http://www.gravatar.com/avatar/${currentUser.avatarHash}?s=16'/>"/>
                                    <sec:authentication property="principal.username" />
                                    <span class="caret"></span>
                                </a>

                                <ul class="dropdown-menu">


                                    <li >

                                        <a class="profile" href="<c:url value="/main/profile"/>">
                                            <i class="icon-user"></i><fmt:message key="profile.title"/></a> 
                                    </li>

                                    <li>  <a href="<c:url value="/act/doLogout"/>">
                                            <i class="icon-off"></i>
                                            <fmt:message key="button.logout"/></a>
                                    </li>
                                    <!-- dropdown menu links -->
                                </ul>
                            </div>

                        </sec:authorize>




                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>

        <div class="container-fluid">
            <div class="row-fluid">
                <div class="span3">
                    <div class="well sidebar-nav">

                        <tiles:insertAttribute name="menu" />



                    </div><!--/.well -->
                </div><!--/span-->
                <div class="span9">

                    <tiles:insertAttribute name="content" />

                    <!--/span-->
                </div><!--/row-->
            </div><!--/span-->
        </div><!--/row-->

        <hr>

        <footer>
            <tiles:insertAttribute name="footer" />


        </footer>

    </div><!--/.fluid-container-->

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="<c:url value="/assets/js/bootstrap-transition.js"/>"></script>
    <script src="<c:url value="/assets/js/bootstrap-alert.js"/>"></script>
    <script src="<c:url value="/assets/js/bootstrap-modal.js"/>"></script>
    <script src="<c:url value="/assets/js/bootstrap-dropdown.js"/>"></script>
    <script src="<c:url value="/assets/js/bootstrap-scrollspy.js"/>"></script>
    <script src="<c:url value="/assets/js/bootstrap-tab.js"/>"></script>
    <script src="<c:url value="/assets/js/bootstrap-tooltip.js"/>"></script>
    <script src="<c:url value="/assets/js/bootstrap-popover.js"/>"></script>
    <script src="<c:url value="/assets/js/bootstrap-button.js"/>"></script>
    <script src="<c:url value="/assets/js/bootstrap-collapse.js"/>"></script>
    <script src="<c:url value="/assets/js/bootstrap-carousel.js"/>"></script>
    <script src="<c:url value="/assets/js/bootstrap-typeahead.js"/>"></script>


    <script type="text/javascript">
        $('#auth-dropdown').click(function(event){
            event.stopPropagation();
        }
    );
    </script>
</body>
</html>
