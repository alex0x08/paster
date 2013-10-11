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
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        
        
         <iframe id="shareFrame" src="${ctx}/main/file/integrated/list/test_integrated"
                scrolling="auto" frameborder="0"
                style="width:380px; height: 600px;"  allowTransparency="true"   >

        </iframe>

        
    </body>
</html>
