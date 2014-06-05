<<<<<<< HEAD
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
           
            .fileDeleteBtn{
                
                cursor: pointer; cursor: hand;
            }
            
            .chzn-container-single .chzn-single {
                height: 2.5em !important;
            }
             .search-field input {
                height: 2em !important;
            }
            .sidebar-nav {
                padding: 9px 0;
            }
            .dropdown-menu {
             min-width: 0px;
            }
   
            .pace .pace-progress {
                background: #29d;
                position: fixed;
                z-index: 2000;
                top: 0;
                left: 0;
                height: 2px;

                -webkit-transition: width 1s;
                -moz-transition: width 1s;
                -o-transition: width 1s;
                transition: width 1s;
            }

            .pace-inactive {
                display: none;
            }
            
            
            #upload_block {
                list-style:none;
            }

            #upload_block li{
            }

            #upload_block li input{
                display: none;
            }

            #upload_block li p{
                overflow: hidden;
                white-space: nowrap;
            }


            #upload_block li canvas{
            }

            #upload_block li span{
                cursor:pointer;
            }

#upload_block li.working span{
    height: 16px;
    background-position: 0 -12px;
}

#upload_block li.error p{
    color:red;
}

#scrollUp {
	bottom: 0px;
	right: 230px;
	width: 70px;
	height: 70px;
	margin-bottom: -10px;
	padding: 10px 5px;
	font-family: sans-serif;
	font-size: 14px;
	line-height: 20px;
	text-align: center;
	text-decoration: none;
	text-shadow: 0 1px 0 #fff;
	color: #828282;
	-webkit-box-shadow: 0 0px 2px 1px rgba(0, 0, 0, 0.2);
	-moz-box-shadow: 0 0px 2px 1px rgba(0, 0, 0, 0.2);
	box-shadow: 0 0px 2px 1px rgba(0, 0, 0, 0.2);
	background-color: #E6E6E6;
	background-image: -moz-linear-gradient(top, #EBEBEB, #DEDEDE);
	background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#EBEBEB), to(#DEDEDE));
	background-image: -webkit-linear-gradient(top, #EBEBEB, #DEDEDE);
	background-image: -o-linear-gradient(top, #EBEBEB, #DEDEDE);
	background-image: linear-gradient(to bottom, #EBEBEB, #DEDEDE);
	background-repeat: repeat-x;
	-webkit-transition: margin-bottom 150ms linear;
	-moz-transition: margin-bottom 150ms linear;
	transition: margin-bottom 150ms linear;
}
	#scrollUp:hover {
		margin-bottom: 0px;
	}
           
       </style>
        
       <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/main/static/${appVersion}/css/main.css'/>" />
       <link href="<c:url value='/main/static/${appVersion}/libs/zoombox/zoombox.css'/>" rel="stylesheet" type="text/css" media="screen" />
       <link href="<c:url value='/main/assets/${appVersion}/chosen/0.9.12/chosen/chosen.css'/>" rel="stylesheet" type="text/css" media="screen" />
    <link href="<c:url value='/main/static/${appVersion}/libs/chosen-image/chosenImage.css'/>" rel="stylesheet" type="text/css" media="screen" />

       <link rel="shortcut icon" href="<c:url value='/favicon.png'/>">

       <script src="<c:url value='/main/assets/${appVersion}/stacktrace/0.5.0/stacktrace.js'/>"></script>
       <script src="<c:url value='/main/static/${appVersion}/libs/pace/pace.min.js'/>"></script>

       <script src="<c:url value='/main/assets/${appVersion}/jquery/2.0.3/jquery.js'/>"></script>
       <script src="<c:url value='/main/assets/${appVersion}/jquery-ui/1.10.2/ui/minified/jquery-ui.min.js'/>"></script>

              <script src="<c:url value='/main/static/${appVersion}/libs/jquery.tools.min.js'/>"></script>

       
       <script src="<c:url value='/main/assets/${appVersion}/jquery-easing/1.3/jquery.easing.min.js'/>"></script>
       <script src="<c:url value='/main/assets/${appVersion}/chosen/0.9.12/chosen/chosen.jquery.min.js'/>"></script>
    <script src="<c:url value='/main/static/${appVersion}/libs/chosen-image/chosenImage.jquery.js'/>"></script>
   
       <script src="<c:url value='/main/static/${appVersion}/libs/jquery-scrollup/jquery.scrollUp.min.js'/>"></script>

       <script src="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/js/bootstrap.min.js'/>"></script>
       <script src="<c:url value='/main/static/${appVersion}/libs/zoombox/zoombox.js'/>"></script>
       <script src="<c:url value='/main/assets/${appVersion}/prettify/4-Mar-2013/run_prettify.js'/>"></script> 
=======
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

         
            <meta name="_csrf" content="${_csrf.token}"/>
    <!-- default header name is X-CSRF-TOKEN -->
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
 
        
        <link href="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/css/bootstrap.min.css'/>" rel="stylesheet"/>
        <link href="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/css/bootstrap-theme.min.css'/>" rel="stylesheet"/>
        <style type="text/css">
           
            .fileDeleteBtn{
                
                cursor: pointer; cursor: hand;
            }
            
            .chzn-container-single .chzn-single {
                height: 2.5em !important;
            }
             .search-field input {
                height: 2em !important;
            }
            .sidebar-nav {
                padding: 9px 0;
            }
            .dropdown-menu {
             min-width: 0px;
            }
   
            .pace .pace-progress {
                background: #29d;
                position: fixed;
                z-index: 2000;
                top: 0;
                left: 0;
                height: 2px;

                -webkit-transition: width 1s;
                -moz-transition: width 1s;
                -o-transition: width 1s;
                transition: width 1s;
            }

            .pace-inactive {
                display: none;
            }
            
            
            #upload_block {
                list-style:none;
            }

            #upload_block li{
            }

            #upload_block li input{
                display: none;
            }

            #upload_block li p{
                overflow: hidden;
                white-space: nowrap;
            }


            #upload_block li canvas{
            }

            #upload_block li span{
                cursor:pointer;
            }

#upload_block li.working span{
    height: 16px;
    background-position: 0 -12px;
}

#upload_block li.error p{
    color:red;
}

#scrollUp {
	bottom: 0px;
	right: 230px;
	width: 70px;
	height: 70px;
	margin-bottom: -10px;
	padding: 10px 5px;
	font-family: sans-serif;
	font-size: 14px;
	line-height: 20px;
	text-align: center;
	text-decoration: none;
	text-shadow: 0 1px 0 #fff;
	color: #828282;
	-webkit-box-shadow: 0 0px 2px 1px rgba(0, 0, 0, 0.2);
	-moz-box-shadow: 0 0px 2px 1px rgba(0, 0, 0, 0.2);
	box-shadow: 0 0px 2px 1px rgba(0, 0, 0, 0.2);
	background-color: #E6E6E6;
	background-image: -moz-linear-gradient(top, #EBEBEB, #DEDEDE);
	background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#EBEBEB), to(#DEDEDE));
	background-image: -webkit-linear-gradient(top, #EBEBEB, #DEDEDE);
	background-image: -o-linear-gradient(top, #EBEBEB, #DEDEDE);
	background-image: linear-gradient(to bottom, #EBEBEB, #DEDEDE);
	background-repeat: repeat-x;
	-webkit-transition: margin-bottom 150ms linear;
	-moz-transition: margin-bottom 150ms linear;
	transition: margin-bottom 150ms linear;
}
	#scrollUp:hover {
		margin-bottom: 0px;
	}
           
       </style>
        
       <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/main/static/${appVersion}/css/main.css'/>" />
       <link href="<c:url value='/main/static/${appVersion}/libs/zoombox/zoombox.css'/>" rel="stylesheet" type="text/css" media="screen" />
       <link href="<c:url value='/main/assets/${appVersion}/chosen/0.9.12/chosen/chosen.css'/>" rel="stylesheet" type="text/css" media="screen" />
    <link href="<c:url value='/main/static/${appVersion}/libs/chosen-image/chosenImage.css'/>" rel="stylesheet" type="text/css" media="screen" />

       <link rel="shortcut icon" href="<c:url value='/favicon.png'/>">

       <script src="<c:url value='/main/assets/${appVersion}/stacktrace/0.5.0/stacktrace.js'/>"></script>
       <script src="<c:url value='/main/static/${appVersion}/libs/pace/pace.min.js'/>"></script>

       <script src="<c:url value='/main/assets/${appVersion}/jquery/2.0.3/jquery.js'/>"></script>
       <script src="<c:url value='/main/assets/${appVersion}/jquery-ui/1.10.2/ui/minified/jquery-ui.min.js'/>"></script>

              <script src="<c:url value='/main/static/${appVersion}/libs/jquery.tools.min.js'/>"></script>

       
       <script src="<c:url value='/main/assets/${appVersion}/jquery-easing/1.3/jquery.easing.min.js'/>"></script>
       <script src="<c:url value='/main/assets/${appVersion}/chosen/0.9.12/chosen/chosen.jquery.min.js'/>"></script>
    <script src="<c:url value='/main/static/${appVersion}/libs/chosen-image/chosenImage.jquery.js'/>"></script>
   
       <script src="<c:url value='/main/static/${appVersion}/libs/jquery-scrollup/jquery.scrollUp.min.js'/>"></script>

       <script src="<c:url value='/main/assets/${appVersion}/bootstrap/3.0.0/js/bootstrap.min.js'/>"></script>
       <script src="<c:url value='/main/static/${appVersion}/libs/zoombox/zoombox.js'/>"></script>
       <script src="<c:url value='/main/assets/${appVersion}/prettify/4-Mar-2013/run_prettify.js'/>"></script> 
>>>>>>> dbf52a094d477965568a60d39b920438f36ce077
       