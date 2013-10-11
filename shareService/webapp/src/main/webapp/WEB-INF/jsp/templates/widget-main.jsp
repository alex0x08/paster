<%-- 
    Document   : widget-main
    Created on : 02.04.2013, 11:59:56
    Author     : achernyshev
--%>
<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>
<html>
    <head>
        <%@ include file="/WEB-INF/jsp/templates/common/template-common-head.jsp"%>
    </head>
    <body>
         <tiles:insertAttribute name="content" />
         
         
                 <%@ include file="/WEB-INF/jsp/templates/common/template-common-body.jsp"%>

    </body>
    
    
</html>
