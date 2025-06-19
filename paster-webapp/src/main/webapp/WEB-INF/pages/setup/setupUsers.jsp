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

   <c:url var="stepUrl" value='/main/setup/users' />

   <form:form
        action="${stepUrl}"
        role="form"
        modelAttribute="updatedStep"
        method="POST">

        <form:errors path="" />

         <fieldset class="row mb-3">
                 <legend class="col-form-label">
                    <i class="fa fa-exclamation-triangle text-danger" aria-hidden="true"></i> <fmt:message key='paster.setup.step.users.securityMode' />
                 </legend>
                 <div class="col-md-10 offset-md-2">
                       <c:forEach var="l" items="${availableSecurityModes}" varStatus="loopStatus">
                         <div class="form-check">
                             <form:radiobutton
                             cssClass="form-check-input securityModeInput"
                             path="step.securityMode"
                             name="securityMode"
                             value="${l.key}" />
                             <label class="form-check-label" >
                                 <fmt:message key='${l.name}'/>
                             </label>
                         </div>
                       </c:forEach>
                       <div class="text-danger">
                            <form:errors path="step.securityMode" />
                       </div>

                 </div>
                 </fieldset>

         <div class="row mb-3">
             <div class="col-sm-10">
                <p>
                      <fmt:message key='paster.setup.step.users.additionalOptions'/>
                </p>
               <div class="form-check">
                       <form:checkbox
                       cssClass="form-check-input"
                       id="allowAnonymousCommentsCreate"
                       path="step.allowAnonymousCommentsCreate"
                       name="allowAnonymousCommentsCreate"
                       value="${allowAnonymousCommentsCreate}" />
                 <label class="form-check-label">
                   <fmt:message key='paster.setup.step.users.allowAnonAddComments' />
                 </label>
               </div>

               <div class="form-check">
                      <form:checkbox
                      cssClass="form-check-input"
                      id="allowAnonymousPastasCreate"
                      path="step.allowAnonymousPastasCreate"
                      name="allowAnonymousPastasCreate"
                      value="${allowAnonymousPastasCreate}" />
                                <label class="form-check-label">
                                  <fmt:message key='paster.setup.step.users.allowAnonAddRecords' />
                                </label>
               </div>

             </div>
           </div>

        <c:url var="addUserUrl" value='/main/setup/addUser' />
        <p>
                <button type="submit" formaction="${addUserUrl}" class="btn btn-primary btn-sm">
                   <i class="fa fa-user-plus" aria-hidden="true"></i>
                   <fmt:message key='button.addUser' />
                </button>
        </p>


    <table class="table table-bordered table-striped table-condensed">
      <thead>
        <tr>
          <th>
            <fmt:message key='user.name' />
          </th>
          <th>
            <fmt:message key='user.login' />
          </th>
          <th>
           <fmt:message key='user.password' />
          </th>
          <th>
           <fmt:message key='role.admin.name' />
           </th>
          <th>
          </th>
        </tr>
      </thead>
      <tbody>
             <c:forEach var="user" items="${availableUsers}" varStatus="vstatus">
                 <tr>
                     <td>
                           <form:input cssClass="form-control"
                           name="name"
                           path="step.users[${vstatus.index}].name"
                           placeholder="Enter name"/>
                           <form:errors element="div"
                           path="step.users[${vstatus.index}].name"
                           cssClass="alert alert-danger" />
                     </td>
                     <td>
                           <form:input
                           cssClass="form-control"
                           name="username"
                           path="step.users[${vstatus.index}].username"
                           placeholder="Enter username"/>
                           <form:errors element="div"
                           path="step.users[${vstatus.index}].username"
                           cssClass="alert alert-danger" />
                     </td>
                     <td>

                     <div class="input-group passwordGroup">
                                         <div class="input-group-text">
                                             <a href="#" id="showHidePasswdLnk" >
                                                 <i class="fa fa-eye" aria-hidden="true"></i>
                                             </a>
                                          </div>

                                           <form:input id='passwordInput'
                                                                      cssClass="form-control"
                                                                      name="password"
                                                                      path="step.users[${vstatus.index}].password"
                                                                      placeholder="Enter password"
                                                                      type="password"/>
                                                                      <form:errors element="div"
                                                                      path="step.users[${vstatus.index}].password"
                                                                      cssClass="alert alert-danger" />

                                     </div>
                     </td>
                     <td>
                           <form:checkbox
                           cssClass="form-check-input"
                           path="step.users[${vstatus.index}].admin"
                           name="admin"
                           value="${step.users[vstatus.index].admin}" />
                     </td>
                      <td>
                       <c:url var="removeUserUrl" value='/main/setup/removeUser' >
                            <c:param name="index" value="${vstatus.index}" />
                       </c:url>

                        <button type="submit"
                              formaction="${removeUserUrl}"
                              class="btn btn-primary"
                              title="<fmt:message key='button.delete'/>">
                                 <i class="fa fa-trash" aria-hidden="true"></i>
                        </button>
                      </td>
                 </tr>

       </c:forEach>

      </tbody>
    </table>


      <jsp:include page="/WEB-INF/pages/setup/setup-buttons.jsp"/>

  </form:form>


    </div>
</div>


<script type="text/javascript">

    function processChecked(el) {
        const secMode = el.getAttribute('value');
        // console.log('sec mode:',secMode)
        toggleDisabled([document.getElementById('allowAnonymousCommentsCreate'),
                             document.getElementById('allowAnonymousPastasCreate')],
                             secMode != 'public');
    }

  window.addEventListener('load', function () {

         var once=true;
         Array.from(document.getElementsByClassName("securityModeInput")).forEach(
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

         Array.from(document.getElementsByClassName("passwordGroup")).forEach(
                    function (el, i, array) {
                        el.querySelector('#showHidePasswdLnk')
                            .addEventListener("click", function (event) {
                                                       event.preventDefault();
                                                       showHidePassword(el.querySelector('#passwordInput'));
                                                     });
                    });

    });
</script>
