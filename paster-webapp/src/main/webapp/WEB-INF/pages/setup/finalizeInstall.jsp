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


<div class='row justify-content-md-center'>
    <div class='col-auto'>
        <p>
            <fmt:message key='paster.setup.step.completed.title'/>
        </p>
                <c:url var="finalizeInstallUrl" value='/main/setup/doFinalizeInstall' />

                 <form:form cssClass="form-inline"
                        style="padding-left:1em;"
                        role="form"
                        action="${finalizeInstallUrl}" method="POST">
                     <button type="submit" class="btn btn-danger">
                         <fmt:message key="button.restart" />
                         </a>
                     </button>
                 </form:form>
    </div>
</div>


