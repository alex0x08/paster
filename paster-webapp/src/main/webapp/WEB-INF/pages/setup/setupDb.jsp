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
            <input id="dbUrl" type="text" class="form-control"  placeholder="Enter database url">
        </div>
   </div>
   <div class="row mb-3">
        <div class="col-md-8">
            <label >Driver class</label>
            <input id="dbDriver" type="text" class="form-control"  placeholder="Enter driver class name">
        </div>
   </div>

   <div class="row mb-3">
        <div class="col-auto">
            <label >Username</label>
            <input id="dbUsername" type="text" class="form-control" placeholder="Jane Doe">
        </div>
        <div class="col-auto">
            <label >Password</label>
                <div class="input-group">
                    <div class="input-group-text">@</div>
                    <input id="dbPassword" type="secret" class="form-control"  placeholder="Password">
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
                        const dbUrl = el.getAttribute('value');
                        const driver = el.getAttribute('x-driver-class');

                        console.log('url: ',dbUrl,'driver: ',driver)

                        document.getElementById('dbUrl').setAttribute('value',dbUrl)
                        document.getElementById('dbDriver').setAttribute('value',driver)
                
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
