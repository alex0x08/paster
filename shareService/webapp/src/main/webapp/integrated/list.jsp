<%-- 
    Document   : list
    Created on : 02.10.2013, 19:40:22
    Author     : aachernyshev
--%>
<%@ include file="/WEB-INF/jsp/templates/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Integration tests</title>
    </head>
    <body>
        <h1>Integration tests</h1>
        
        
         <iframe id="shareFrame" src="${ctx}/main/file/integrated/list/test_integrated"
                 frameborder="0" scrolling="auto"
                style="width:320px; height: 800px;"  allowTransparency="true"   >

        </iframe>

         <iframe id="shareFrame" src="${ctx}/main/file/integrated/view?id=284"
                scrolling="no" frameborder="1"
                style="width:390px; height: 250px;"  allowTransparency="true"   >

        </iframe>
        
        
    </body>
</html>
