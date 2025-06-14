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

<script src="<c:url value='/main/resources/${appId}/paster/js/all/LazyPagination.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/paster/js/paste-list/paste-list.js'/>"></script>
<jsp:include page="/WEB-INF/pages/common/paste-update-poll.jsp"/>

<script type="text/javascript">
    const pasterList = new PasterList();
    window.addEventListener('load', function () {
        pasterList.init("<c:url value='/main/paste/raw/list'/>",  "<c:url value='/main/paste/list'/>",
                ${pageItems.pageCount-(pageItems.page+1)},
                ${pageItems.page} + 2);
    });
</script>
