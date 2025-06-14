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

   <c:url var="stepUrl" value='/main/setup/welcome' />

   <form:form
        action="${stepUrl}"
        role="form"
        modelAttribute="updatedStep.step"
        method="POST">

      <fieldset class="row mb-3">
         <legend class="col-form-label">
            <fmt:message key='paster.setup.step.welcome.selectLanguage' />
         </legend>
         <div class="col-md-10 offset-md-2">
               <c:forEach var="l" items="${availableLocales}" varStatus="loopStatus">
                 <div class="form-check">
                              <form:radiobutton cssClass="form-check-input"
                                    path="defaultLang"
                                    name="defaultLang"
                               value="${l.language}" />
                              <form:errors element="div" path="defaultLang"
                                            cssClass="alert alert-danger" />
                     <label class="form-check-label">
                         <c:out value="${l.getDisplayLanguage(l)}"/>
                    </label>
                 </div>
               </c:forEach>  
         </div>
         </fieldset>

  <div class="row mb-3">
     <div class="col-sm-10">
        <p>
             <fmt:message key='paster.setup.step.welcome.additionalOptions' />
        </p>

       <div class="form-check">
               <form:checkbox cssClass="form-check-input"
                              path="switchToUserLocale"
                              name="switchToUserLocale"
                              value="${l.language}" />
               <form:errors element="div"
                            path="switchToUserLocale"
                            cssClass="alert alert-danger" />
         <label class="form-check-label">
           <fmt:message key='paster.setup.step.welcome.allowLocaleSwitchToBrowserLocale' />
         </label>
       </div>
     </div>
   </div>

      <jsp:include page="/WEB-INF/pages/setup/setup-buttons.jsp"/>

  </form:form>
    </div>
</div>


