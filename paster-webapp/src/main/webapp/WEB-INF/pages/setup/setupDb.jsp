<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<div class='row justify-content-md-center' >
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
                        <input class="form-check-input driverInput"
                            type="radio" 
                            id="${'driver_'.concat(loopStatus.index)}"
                            name="selectedDriver"
                            x-driver-class="${l.driver}"
                            x-editable="${l.editable}"
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

   <jsp:include page="/WEB-INF/pages/setup/setup-buttons.jsp"/>


  </form:form>
    </div>
</div>


<script type="text/javascript">
    
    function processChecked(el) {
        if (el.checked) {
                        const editable = el.getAttribute('x-editable');

                        const dbUrl = el.getAttribute('value');
                        const driver = el.getAttribute('x-driver-class');

                        console.log('editable:',editable, 'url: ',dbUrl,'driver: ',driver)

                        const elUrl = document.getElementById('dbUrl');
                        const elDriver = document.getElementById('dbDriver');

                        elUrl.setAttribute('value',dbUrl);
                        elDriver.setAttribute('value',driver);

                        if (editable == 'false') {
                            elUrl.setAttribute('disabled','true');
                            elDriver.setAttribute('disabled','true');

                            document.getElementById('dbUser').setAttribute('disabled','true');
                            document.getElementById('dbPassword').setAttribute('disabled','true');

                            console.log('disabled');
                        } else {
                            elUrl.removeAttribute('disabled');
                            elDriver.removeAttribute('disabled');

                            document.getElementById('dbUser').removeAttribute('disabled');
                            document.getElementById('dbPassword').removeAttribute('disabled');
                        }
                 }
    }

    window.addEventListener('load', function () {

        Array.from(document.getElementsByClassName("driverInput")).forEach(
            function (el, i, array) {
                processChecked(el);  
                el.addEventListener("change", function (event) {
                    event.preventDefault();
                    processChecked(el);            
                });
            });
    });
</script>
