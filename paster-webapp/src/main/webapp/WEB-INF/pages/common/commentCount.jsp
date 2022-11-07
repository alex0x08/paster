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
<tiles:importAttribute name="model" />
<tiles:importAttribute name="modelName" />
<c:if test="${not empty model.commentsCount and model.commentsCount>0}">
    <span title="has ${model.commentsCount} comments ">
    <span style="vertical-align: middle;" class="i">C</span>
    <span style="font-size: 10px;"> x <c:out value='${model.commentsCount}'/></span>
    </span>,
</c:if>
