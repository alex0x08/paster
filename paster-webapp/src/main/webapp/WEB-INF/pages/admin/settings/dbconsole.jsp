<%--

    Copyright © 2011 Alex Chernyshev (alex3.145@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

--%>
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
                