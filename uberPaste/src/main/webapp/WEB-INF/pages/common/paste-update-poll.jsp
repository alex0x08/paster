<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<div id="newPastasCountBlock" class="row" style="display: none;">
    <div class="column grid-10">
        <p class="notice" style="padding: 0.3em;">
            <fmt:message key="paste.list.since.info">
                <fmt:param value="<span id='newPastasCount'></span>"/>
            </fmt:message>
                <span class="right">
                    <a href="<c:url value='/main/paste/list/${sourceType}'/>" title="<fmt:message key='button.refresh'/>">
                   <span class="i" style="font-size: 1.3em;">P</span> </a>        
                </span>              
        </p>    
    </div>
</div>
        
<script type="text/javascript">
    
     var updatePastasCountRequest = new Request({
    method: 'GET',
    url: '${ctx}/main/paste/count/form/'+new Date().getTime()+'.json',
    initialDelay: 1000,
    delay: 15000,
    limit: 60000,
    onSuccess: function(responseText){
        var obj = JSON.decode(responseText, true);
        var pcount = obj['count'];
        
        if (pcount>0) {            
            $('newPastasCountBlock').setStyle('display','');
            $('newPastasCount').set('text', pcount);
          	Tinycon.setBubble(pcount);
            }
    },
    onFailure: function(){
        $('newPastasCount').set('text', 'Sorry, your request failed :(');
    }
});

    
    window.addEvent('domready', function() {
        
          updatePastasCountRequest.startTimer();
    }); 
</script>