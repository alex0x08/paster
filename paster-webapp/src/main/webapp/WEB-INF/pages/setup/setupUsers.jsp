<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class='row'>

    <div class='col-md-offset-3 col-xs-4 col-md-5'>

    <table class="table table-bordered table-striped table-condensed">
      <thead>
        <tr>
          <th>
                Login
          </th>
          <th>
                Name
          </th>
          <th>
                Admin
          </th>
        </tr>
      </thead>
      <tbody>
             <c:forEach var="user" items="${availableUsers}" varStatus="status">
                 <tr>
                     <td>
                         <c:out value="${user.username}"/>
                     </td>
                     <td>
                         <c:out value="${user.name}"/>
                     </td>
                     <td>
                         <c:out value="${user.admin}"/>
                     </td>
                 </tr>

       </c:forEach>

      </tbody>
    </table>



   <c:url var="stepUrl" value='/main/setup/users' />

   <form:form
        action="${stepUrl}"
        role="form"
        modelAttribute="updatedStep.step"
        method="POST">

        There will be users setup

      <jsp:include page="/WEB-INF/pages/setup/setup-buttons.jsp"/>

  </form:form>


    </div>
</div>


