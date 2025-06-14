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

 <c:if test="${previousStep!=null}">
    <c:url var="prevUrl" value='/main/setup/prev/${previousStep.stepKey}' />

    <button type="submit" formaction="${prevUrl}" class="btn btn-primary">
         <fmt:message key="button.prev" />
    </button>
</c:if>

<c:if test="${nextStep!=null}">
    <button type="submit" class="btn btn-primary">
         <fmt:message key="button.next" />
    </button>
</c:if>

<c:if test="${nextStep==null}">
    <button type="submit" class="btn btn-primary">
         <fmt:message key="button.complete" />
    </button>
</c:if>
