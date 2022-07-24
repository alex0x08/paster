<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<div id="newPastasCountBlock" class="row" style="display: none;">
    <div class="col-md-12">
        <p class="notice" style="padding: 0.3em;">
            <fmt:message key="paste.list.since.info">
                <fmt:param value="<span id='newPastasCount'></span>"/>
            </fmt:message>
                <span class="right">
                    <a href="<c:url value='/main/paste/list/${sourceType}'/>" 
                        title="<fmt:message key='button.refresh'/>">
                   <span class="i" style="font-size: 1.3em;">P</span> </a>        
                </span>              
        </p>    
    </div>
</div>
        
<script type="text/javascript">

    function checkNewPastas() {
        const xmlhttp = new XMLHttpRequest();
        const cb = document.getElementById('newPastasCountBlock');

        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == XMLHttpRequest.DONE) {
               if (xmlhttp.status == 200) {
                  const obj = JSON.parse(xmlhttp.responseText, true);
                        const pcount = obj['count'];
                        if (pcount>0) {
                            cb = document.getElementById('newPastasCountBlock');
                            cb.style.display='';
                            cb.text = pcount;
                          	Tinycon.setBubble(pcount);
                        }
               } else {
                    cb.text= 'Sorry, your request failed :(';
               }
            }
        };

        xmlhttp.open("GET", '${ctx}/main/paste/count/form/'+new Date().getTime()+'.json', true);
        xmlhttp.send();
    }

    window.addEventListener('load', function() {
            setInterval(checkNewPastas,10000);
    }); 
</script>