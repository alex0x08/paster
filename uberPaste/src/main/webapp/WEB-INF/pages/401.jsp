<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<% response.setStatus(401); %>


<!DOCTYPE html>
<html lang="en">
    <head>
        <link rel="icon" href="<c:url value='/main/static/${pasteRuntime.appVersion}/images/ninja.png'/>"/>

        <!-- HTTP 1.1 -->
        <meta http-equiv="Cache-Control" content="no-store"/>
        <!-- HTTP 1.0 -->
        <meta http-equiv="Pragma" content="no-cache"/>
        <!-- Prevents caching at the Proxy Server -->
        <meta http-equiv="Expires" content="0"/>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

        <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9" />

        <title>401: Not authorized</title>
        
        <link href="<c:url value='/main/assets/${pasteRuntime.appVersion}/paster/minified/all/css/all-style.min.css'/>" rel="stylesheet" type="text/css">
        <link href="<c:url value='/main/assets/${pasteRuntime.appVersion}/paster/less/app.css'/>" rel="stylesheet" type="text/css">

    </head>
    <body>

           <div class="row">
        
               <div class="column grid-2">
                     <img src="<c:url  value='/main/static/${pasteRuntime.appVersion}/images/ninjas/ninja-back.png'/>"/>
               </div>
        
               <div class="column grid-8" >
          
                   <div style="padding-top: 5em;">
                   <h1>Not authorized</h1>
      
                   <a href="<c:url value='/'/>" title="<fmt:message key="index.title"/>">
                           <i class="fa fa-home"></i> <fmt:message key="index.title"/>
                    </a>
                   
                   </div>
                   <div style="padding-top: 5em;">
                       <p title="<c:out value="${pasteRuntime.systemInfo.runtimeVersion.full}"/>">
                           <c:out value="${pasteRuntime.systemInfo.runtimeVersion.implVersion}"/>
                       </p>

                       <fmt:formatDate pattern="${datePattern}" var="startDate" 
                                       value="${pasteRuntime.systemInfo.dateStart}" />

                       <fmt:formatDate pattern="${datePattern}" var="installDate" 
                                       value="${pasteRuntime.systemInfo.dateInstall}" />
                       <p>
                           <fmt:message key='system.installed.title'>
                               <fmt:param value="${startDate}"/>
                               <fmt:param value="${installDate}"/>
                           </fmt:message>   
                       </p>
                       <small><fmt:message key="site.footer"/></small>

                   </div>
                   
               </div>
            
           </div>
        
    </body>
</html>
