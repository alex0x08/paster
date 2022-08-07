<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class='row justify-content-md-center'>
    <div class='col-auto'>

   <c:url var="stepUrl" value='/main/setup/welcome' />

   <form:form
        action="${stepUrl}"
        role="form"
        modelAttribute="updatedStep.step"
        method="POST">

        <p>
            Welcome to Paster, comrade!
        </p>

      <fieldset class="row mb-3">
         <legend class="col-form-label">Please choose application language:</legend>
         <div class="col-md-10 offset-md-2">
               <c:forEach var="l" items="${availableLocales}" varStatus="loopStatus">
                 <div class="form-check">
                              <form:radiobutton cssClass="form-check-input" path="defaultLang" name="defaultLang"
                               value="${l.language}"
                                                     />
                                                 <form:errors element="div" path="defaultLang" cssClass="alert alert-danger" />
                     <label class="form-check-label" >
                         <c:out value="${l.getDisplayLanguage(l)}"/>
                    </label>
                 </div>
               </c:forEach>  
         </div>
         </fieldset>

  <div class="row mb-3">
     <div class="col-sm-10">
        <p>
                Additional options
        </p>

       <div class="form-check">
               <form:checkbox cssClass="form-check-input" path="switchToUserLocale" name="switchToUserLocale"
                                       value="${l.language}" />
               <form:errors element="div" path="switchToUserLocale" cssClass="alert alert-danger" />
         <label class="form-check-label">
           Allow locale switch to browser locale
         </label>
       </div>
     </div>
   </div>

      <jsp:include page="/WEB-INF/pages/setup/setup-buttons.jsp"/>

  </form:form>
    </div>
</div>



<script type="text/javascript">

    function checkES6() {
        "use strict";
        if (typeof Symbol == "undefined") return false;
        try {
            eval("class Foo {}");
            eval("var bar = (x) => x+1");
        } catch (e) { return false; }
        return true;
    }

    window.addEventListener('load', function () {
        if (checkES6()===false) {
    
        }
    });
</script>
