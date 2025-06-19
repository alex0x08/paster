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

<div class='row justify-content-md-center' >
    <div class='col-auto'>
        <c:url var="stepUrl" value='/main/setup/db' />
   <form:form
        action="${stepUrl}"
        role="form"
        modelAttribute="updatedStep"
        method="POST">
            <form:input id="origName" path="step.origName" cssStyle="display:none;" />
            <form:errors path="" />
      <fieldset class="row mb-3">
         <legend class="col-form-label">
            <fmt:message key='paster.setup.step.db.supportedDatabases'/>
         </legend>
         <div class="col-sm-10">
                <c:forEach var="l" items="${availableDrivers}" varStatus="loopStatus">
                 <div class="form-check">
                        <input class="form-check-input driverInput"
                            type="radio"
                            id="${'driver_'.concat(loopStatus.index)}"
                            name="selectedDriver"
                            x-driver-class="${l.driver}"
                            x-editable="${l.editable}"
                            x-name="${l.name}"
                            value="${l.url}"
                            ${l.current ? 'checked' : '' } />
                        <label class="form-check-label" >
                                <c:out value="${l.name}"/>
                        </label>
                 </div>
               </c:forEach>  
         </div>               
      </fieldset>

   <div class="row mb-3">
        <div class="col-md-12">
            <label>
                <fmt:message key='paster.setup.step.db.url' />
            </label>
             <form:input
                    cssClass="form-control"
                    path="step.dbUrl"
                    name="dbUrl"
                    id="dbUrl"
                    placeholder="Enter database url"/>
             <form:errors element="div" path="step.dbUrl" cssClass="alert alert-danger" />
        </div>
   </div>
   <div class="row mb-3">
        <div class="col-md-8">
            <label>
                <fmt:message key='paster.setup.step.db.driver'/>
            </label>
             <form:input cssClass="form-control"
                        path="step.dbType"
                        name="dbType"
                        id="dbDriver"
                        placeholder="Enter driver class name"/>
             <form:errors element="div" path="step.dbType" cssClass="alert alert-danger" />
        </div>
   </div>
   <div class="row mb-3">
        <div class="col-auto">
            <label>
                <fmt:message key='paster.setup.step.db.username'/>
            </label>
            <form:input cssClass="form-control"
                                    path="step.dbUser"
                                    name="dbUser"
                                    id="dbUser"
                                    placeholder="Enter username"/>
            <form:errors element="div" path="step.dbUser" cssClass="alert alert-danger" />
        </div>
        <div class="col-auto">
            <label>
                   <fmt:message key='paster.setup.step.db.password'/>
            </label>
                <div class="input-group">
                    <div class="input-group-text">
                        <a href="#" id="showHidePasswdLnk" >
                            <i class="fa fa-eye" aria-hidden="true"></i>
                        </a>
                     </div>
                    <form:input cssClass="form-control" type="password"
                                         path="step.dbPassword"
                                         name="dbPassword"
                                         id="dbPassword"
                                         placeholder="Enter password"/>
                 <form:errors element="div" path="step.dbPassword" cssClass="alert alert-danger" />
                </div>
        </div>
   </div>
    <div class="row mb-3">
           <div class="col-auto">

            <c:url var="checkUrl" value='/main/setup/checkConnection' />

                <button type="submit"
                        formaction="${checkUrl}"
                        class="btn btn-danger btn-sm">
                    <i class="fa fa-database" aria-hidden="true"></i>
                    <fmt:message key='paster.setup.step.db.tryConnect'/>
                </button>
           </div>
    </div>

    <c:if test="${updatedStep.step.connectionLog!=null}">
        <div class="row mb-3">
               <div class="col-auto">
                <textarea class="form-control" rows="6" cols="80">
                    <c:out value="${updatedStep.step.connectionLog}"/>
                </textarea>
           </div>
        </div>
    </c:if>

   <jsp:include page="/WEB-INF/pages/setup/setup-buttons.jsp"/>

  </form:form>
    </div>
</div>


<script type="text/javascript">

    function processChecked(el) {
        if (el.checked) {
                        const editable = el.getAttribute('x-editable');
                        const dbUrl = el.getAttribute('value'),
                              driver = el.getAttribute('x-driver-class'),
                              origName = el.getAttribute('x-name');

                        // console.log('editable:',editable, 'url: ',dbUrl,'driver: ',driver)

                        const elUrl = document.getElementById('dbUrl'),
                              elDriver = document.getElementById('dbDriver'),
                              elOrigName = document.getElementById('origName');

                        elUrl.setAttribute('value',dbUrl);
                        elDriver.setAttribute('value',driver);
                        elOrigName.setAttribute('value',origName);

                        toggleDisabled([elUrl,
                                        elDriver,
                                        document.getElementById('dbUser'),
                                        document.getElementById('dbPassword')],
                                        editable == 'false');

                 }
    }

    window.addEventListener('load', function () {

        document.getElementById('showHidePasswdLnk')
            .addEventListener("click", function (event) {
                            event.preventDefault();
                            showHidePassword(document.getElementById('dbPassword'));
                          });

        var once=true;
        Array.from(document.getElementsByClassName("driverInput")).forEach(
            function (el, i, array) {
                if (once) {
                    processChecked(el);
                    once = false;
                }
                el.addEventListener("change", function (event) {
                    event.preventDefault();
                    processChecked(el);            
                });
            });
    });
</script>
