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


<%--

    Renders view page for selected paste without template (raw)

--%>


<div id="${model.id}_pagedPaste">
    
    <jsp:include page="/WEB-INF/pages/paste/common/paste-view-top.jsp"/>
    <jsp:include page="/WEB-INF/pages/paste/common/paste-view-common.jsp"/>
    
</div>
