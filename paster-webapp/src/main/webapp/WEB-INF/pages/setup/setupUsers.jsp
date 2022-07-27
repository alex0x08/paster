<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class='row'>
    <div class='col-md-offset-3 col-xs-4 col-md-5'>


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


