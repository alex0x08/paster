<!DOCTYPE html>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>
<html>
    <head>
        <%@ include file="/WEB-INF/jsp/templates/common/template-common-head.jsp"%>
    </head>
    <body>
          <div class="container">
               
        <tiles:insertAttribute name="content" />
        
          </div>
        
        <%@ include file="/WEB-INF/jsp/templates/common/template-common-body.jsp"%>
    </body>
</html>
