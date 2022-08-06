<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<div class='row'>
    <div class='col-md-offset-3 col-xs-4 col-md-5'>


   <c:url var="stepUrl" value='/main/setup/welcome' />

   <form:form
        action="${stepUrl}"
        role="form"
        modelAttribute="updatedStep.step"
        method="POST">

      <fieldset class="row mb-3">
         <legend class="col-form-label col-sm-2 pt-0">Languages</legend>
         <div class="col-sm-10">
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
               
  <div class="row mb-3">
     <div class="col-sm-10 offset-sm-2">
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
