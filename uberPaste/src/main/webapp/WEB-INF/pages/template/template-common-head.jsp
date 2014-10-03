<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<c:choose>
                <c:when test="${not empty systemInfo.project.iconImage}">
<link id="favicon" rel="icon" type="image/jpeg"  href="${systemInfo.project.iconImage}"/>
                </c:when>
    <c:otherwise>
<link rel="icon" href="<c:url value='/main/static/${appVersion}/images/ninja.png'/>"/>
    </c:otherwise>
</c:choose>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

   <!-- HTTP 1.1 -->
        <meta http-equiv="Cache-Control" content="no-store"/>
        <!-- HTTP 1.0 -->
        <meta http-equiv="Pragma" content="no-cache"/>
        <!-- Prevents caching at the Proxy Server -->
        <meta http-equiv="Expires" content="0"/>
     

<link href="<c:url value='/main/assets/${appVersion}/paster/minified/all/css/all-style.min.css'/>" rel="stylesheet" type="text/css">

<style>
    @media screen and (min-width: 768px) {
	
	#newPasteDialog .modal-dialog  {width:1140px;}

}
</style>
<script src="<c:url value='/main/assets/${appVersion}/paster/minified/all/js/all-script.min.js'/>"></script>

<script  type="text/javascript">
    
    if (this.MooTools.version=='1.5.1') {
    delete Function.prototype.bind;

    Function.implement({

        /*<!ES5-bind>*/
        bind: function(that){
            var self = this,
                args = arguments.length > 1 ? Array.slice(arguments, 1) : null,
                F = function(){};

            var bound = function(){
                var context = that, length = arguments.length;
                if (this instanceof bound){
                    F.prototype = self.prototype;
                    context = new F;
                }
                var result = (!args && !length)
                    ? self.call(context)
                    : self.apply(context, args && length ? args.concat(Array.slice(arguments)) : args || arguments);
                return context == that ? result : context;
            };
            return bound;
        },
        /*</!ES5-bind>*/
    });
}
    
            var transmitText = '<fmt:message key="action.sending"/>';
            var growl= null;
        window.addEvent('domready',function() {
            growl = new Growler.init();            
        });
        
</script>
 