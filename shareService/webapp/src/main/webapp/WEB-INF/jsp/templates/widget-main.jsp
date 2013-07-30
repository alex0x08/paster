<%-- 
    Document   : widget-main
    Created on : 02.04.2013, 11:59:56
    Author     : achernyshev
--%>
<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>
<html>
    <head>
        <meta charset="utf-8">
        <title><fmt:message key="site.title"/></title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="alex">

        <link href="<c:url value='/css/metro-bootstrap.css'/>" rel="stylesheet"/>
    
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
            <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->


        <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/css/main.css'/>" />

        <script src="<c:url value='/js/jquery-1.8.0.js'/>"></script>
        
    </head>
    <body>
      
         <tiles:insertAttribute name="content" />
        
    </body>
    
      <script src="<c:url value='/js/bootstrap-transition.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-alert.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-modal.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-dropdown.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-scrollspy.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-tab.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-tooltip.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-popover.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-button.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-collapse.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-carousel.js'/>"></script>
    <script src="<c:url value='/js/bootstrap-typeahead.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/jquery.validate.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/jquery.validate.unobtrusive.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/jquery.unobtrusive-ajax.js'/>"></script>

    <script type="text/javascript">

     var commentsUrl = '<c:url value="/main/file/raw/comments"/>';

        $(document).ready(function() {

            $("[rel='tooltip']").tooltip();
  
               $(".commentsBtn").live('click', function(e) {
               
                 var thisTab = e.target // activated tab
                  var pageTarget = $(thisTab).attr('href');
                  
                  $(pageTarget).load(commentsUrl+'?id='+$(thisTab).attr('modelId'));
                  
                 
                });
        });
        </script>
   
</html>
