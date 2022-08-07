<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<div class='row justify-content-md-center' >
    <div class='col-auto'>

   <c:url var="stepUrl" value='/main/setup/db' />

   <form:form
        action="${stepUrl}"
        role="form"
        modelAttribute="updatedStep.step"
        method="POST">
        <form:input id="origName" path="origName" cssStyle="display:none;" />

      <fieldset class="row mb-3">
         <legend class="col-form-label">Supported databases</legend>
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
            <label >Url</label>
             <form:input cssClass="form-control" path="dbUrl" name="dbUrl" id="dbUrl"
                                                placeholder="Enter database url"/>
             <form:errors element="div" path="dbUrl" cssClass="alert alert-danger" />
        </div>
   </div>
   <div class="row mb-3">
        <div class="col-md-8">
            <label >Driver class</label>
             <form:input cssClass="form-control"
                        path="dbType"
                        name="dbType"
                        id="dbDriver"
                        placeholder="Enter driver class name"/>
             <form:errors element="div" path="dbType" cssClass="alert alert-danger" />
        </div>
   </div>

   <div class="row mb-3">
        <div class="col-auto">
            <label >Username</label>
            <form:input cssClass="form-control"
                                    path="dbUser"
                                    name="dbUser"
                                    id="dbUser"
                                    placeholder="Enter username"/>
            <form:errors element="div" path="dbUser" cssClass="alert alert-danger" />
        </div>
        <div class="col-auto">
            <label >Password</label>
                <div class="input-group">
                    <div class="input-group-text">@</div>
                    <form:input cssClass="form-control" type="password"
                                         path="dbPassword"
                                         name="dbPassword"
                                         id="dbPassword"
                                         placeholder="Enter password"/>
                 <form:errors element="div" path="dbPassword" cssClass="alert alert-danger" />
                </div>
        </div>
   </div>

    <div class="row mb-3">
           <div class="col-auto">

            <c:url var="checkUrl" value='/main/setup/checkConnection' />

    <button type="submit" formaction="${checkUrl}" class="btn btn-danger btn-sm">
       <i class="fa fa-database" aria-hidden="true"></i> Try connect
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

    function toggleDisabled(els,value) {
        els.forEach(
                function (el, i, array) {
                    if (value) {
                        el.setAttribute('disabled','true');
                    } else {
                        el.removeAttribute('disabled');
                    }
        });
    }

    function processChecked(el) {
        if (el.checked) {
                        const editable = el.getAttribute('x-editable');

                        const dbUrl = el.getAttribute('value'),
                              driver = el.getAttribute('x-driver-class'),
                              origName = el.getAttribute('x-name');

                        console.log('editable:',editable, 'url: ',dbUrl,'driver: ',driver)

                        const elUrl = document.getElementById('dbUrl'),
                              elDriver = document.getElementById('dbDriver'),
                              elOrigName = document.getElementById('origName');

                        elUrl.setAttribute('value',dbUrl);
                        elDriver.setAttribute('value',driver);
                        elOrigName.setAttribute('value',origName);

                        toggleDisabled([elUrl,
                                        elDriver,
                                        document.getElementById('dbUser'),
                                        document.getElementById('dbPassword')],editable == 'false');

                 }
    }

    window.addEventListener('load', function () {
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
