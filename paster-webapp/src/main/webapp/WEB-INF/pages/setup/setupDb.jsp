<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<div class='row'>
    <div class='col-auto'>


   <c:url var="stepUrl" value='/main/setup/db' />

   <form:form
        action="${stepUrl}"
        role="form"
        modelAttribute="updatedStep.step"
        method="POST">

      <fieldset class="row mb-3">
         <legend class="col-form-label">Supported databases</legend>
         <div class="col-sm-10">
               <c:forEach var="l" items="${availableDrivers}" varStatus="loopStatus">
                 <div class="form-check">
                            <input type="radio" name="${l.driver}" value="${l.url}" class="form-check-input" ${l.current ? 'checked' : '' } />

                      <label class="form-check-label" >
                         <c:out value="${l.name}"/>
                    </label>
                 </div>
               </c:forEach>  
         </div>               
        </fieldset>

    <div class="row mb-3">
        <div class="col-md-12">
            <label >Url</label>
            <input type="text" class="form-control"  placeholder="Enter database url">
        </div>
    </div>              

    <div class="row mb-3">
    
        <div class="col-auto">
        <label >Username</label>
        <input type="text" class="form-control" id="autoSizingInput" placeholder="Jane Doe">
      </div>
      <div class="col-auto">
        <label >Password</label>
        <div class="input-group">
          <div class="input-group-text">@</div>
          <input type="text" class="form-control" id="autoSizingInputGroup" placeholder="Username">
        </div>
      </div>

   </div>
   <button type="submit" class="btn btn-primary">
      <i class="fa fa-sign-in"></i>
       Next
   </button>

  </form:form>
    </div>
</div>


