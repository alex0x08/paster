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

        <p>
                Here you can setup users
        </p>

         <fieldset class="row mb-3">
                 <legend class="col-form-label">Security mode:</legend>
                 <div class="col-md-10 offset-md-2">
                       <c:forEach var="l" items="${availableSecurityModes}" varStatus="loopStatus">
                         <div class="form-check">
                             <form:radiobutton
                             cssClass="form-check-input"
                             path="step.securityMode"
                             name="securityMode"
                             value="${l.key}" />


                             <label class="form-check-label" >
                                 <c:out value="${l.name}"/>
                            </label>
                         </div>
                       </c:forEach>
                       <form:errors path="step.securityMode" />

                 </div>
                 </fieldset>



         <div class="row mb-3">
             <div class="col-sm-10">
                <p>
                        Additional options
                </p>
               <div class="form-check">
                       <form:checkbox
                       cssClass="form-check-input"
                       path="step.allowAnonymousCommentsCreate"
                       name="allowAnonymousCommentsCreate"
                       value="${allowAnonymousCommentsCreate}" />
                 <label class="form-check-label">
                   Allow anonymous to add comments
                 </label>
               </div>

               <div class="form-check">
                      <form:checkbox
                      cssClass="form-check-input"
                      path="step.allowAnonymousPastasCreate"
                      name="allowAnonymousPastasCreate"
                      value="${allowAnonymousPastasCreate}" />
                                <label class="form-check-label">
                                  Allow anonymous to create new records
                                </label>
               </div>

             </div>
           </div>

        <c:url var="addUserUrl" value='/main/setup/addUser' />
        <p>
                <button type="submit" formaction="${addUserUrl}" class="btn btn-primary btn-sm">
                   <i class="fa fa-user-plus" aria-hidden="true"></i> Add user
                </button>
        </p>


    <table class="table table-bordered table-striped table-condensed">
      <thead>
        <tr>
          <th>
                Name
          </th>
          <th>
                Login
          </th>
          <th>
                Password
          </th>
          <th>
                Admin
          </th>
          <th>
                Actions
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
                            <form:input
                            cssClass="form-control"
                            name="password"
                            path="step.users[${vstatus.index}].password"
                            placeholder="Enter password"
                            type="password"/>
                            <form:errors element="div"
                            path="step.users[${vstatus.index}].password"
                            cssClass="alert alert-danger" />
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
                              class="btn btn-primary" title="Remove user">
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


