<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<fmt:message key="settings.dbconsole.title"/>

<iframe id="dbconsoleFrame" src="<c:url value='/act/admin/dbconsole/frame.jsp'>
        <c:param name="jsessionid" value="${applicationScope['h2console-session-id']}"/>
        </c:url>"
        scrolling="auto" 
        frameborder="0"
        style="width:95%;"  
        allowTransparency="true"   >
</iframe>

<script type="text/javascript">

    window.addEventListener('load', function(){
        document.getElementById('dbconsoleFrame').style.height=document.body.scrollHeight +'px';
    });

</script>
                