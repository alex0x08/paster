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
 

<%--
        Common content for all pages (will be attached to bottom of body)
--%>    

<div  class="modal hide" id="deletePopup">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button"
                        class="close"
                        data-bs-dismiss="modal">X</button>
                <h4 class="modal-title" id="dialogTitle"></h4>
            </div>
            <div id="dialogMessage" class="modal-body">                
            </div>
            <div class="modal-footer">
                <a id="dialogAction" href="" class="btn btn-primary"></a>
                <button type="button"
                        class="btn btn-default"
                        data-bs-dismiss="modal">
                            <fmt:message key='button.cancel' />
                        </button>
            </div>
        </div>
    </div>
</div>
<div class="modal hide" id="newPasteDialog" >
    <div class="modal-dialog">
        <div class="modal-content" style="padding:0;margin:0;">           
            <div id="dialogMessage" class="modal-body">                
            </div>          
        </div>
    </div>
</div>
<div class="position-fixed bottom-0 end-0 p-3" style="z-index:11">
<div id="pasterToast" class="toast"
                      role="alert" aria-live="assertive" aria-atomic="true">
  <div class="toast-header">
    <strong class="me-auto">Paster</strong>
    <button type="button" class="btn-close"
            data-bs-dismiss="toast" aria-label="Close"></button>
  </div>
  <div class="toast-body"></div>
</div>
</div>
<%-- common js libraries, used across paster --%>
<script type="text/javascript"
    src="<c:url value='/main/resources/${appId}/local_components/logger.js'/>"></script>
<script type="text/javascript"
    src="<c:url value='/main/resources/${appId}/local_components/fastdom.js'/>"></script>
<script type="text/javascript"
    src="<c:url value='/main/resources/${appId}/local_components/bootstrap/bootstrap.bundle.min.js'/>"></script>
<script type="text/javascript"
    src="<c:url value='/main/resources/${appId}/local_components/tinyicon/tinycon.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/canvas-to-blob.min.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/html2canvas.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/canvas2image.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/pica.min.js'/>"></script>

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
            pasterApp.showNotify('<fmt:message key="${paster.web.noES6Support}"/>');
        }
    });
</script>


<script type="text/javascript" src="<c:url value='/main/resources/${appId}/paster/js/all/paster-app.js'/>"></script>

<script type="text/javascript">

    Logger.useDefaults();
    Logger.setLevel(Logger.DEBUG);

    window.pica.prototype.debug = console.log.bind(console);

    class PasterI18nClass {
        text = {
            notify: {
                transmitMessage: '<fmt:message key="action.sending"/>'
            },
            dialog: {
                removal: {
                    title: '<fmt:message key="button.delete"/>',
                    message: '<fmt:message key="dialog.confirm.remove"/>'
                    }
            }
        }
    }

    const PasterI18n = new PasterI18nClass();
    var pasterApp = new PasterApp();

    window.addEventListener('load', function () {
        pasterApp.appInit(document.body);

        <c:if test="${not empty statusMessageKey}">
            pasterApp.showNotify('<fmt:message key="${statusMessageKey}"/>');   
        </c:if>
    });
</script>
