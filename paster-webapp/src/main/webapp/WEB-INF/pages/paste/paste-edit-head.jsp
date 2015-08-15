<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

  
<link href="<c:url value='/main/assets/${appId}/paster/minified/paster-edit/css/paste-edit-style.min.css'/>" rel="stylesheet" type="text/css">

<%--

dirty fix with mouse events in Ace & Mootools

--%>
<script type="text/javascript">
    
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
 
</script>
    

<script type="text/javascript" src="<c:url value="/main/assets/${appId}/paster/minified/paster-edit/js/paste-edit-script.min.js"/>"></script>
<script src="<c:url value='/main/assets/${appId}/ace/01.08.2014/src-min-noconflict/ace.js'/>" type="text/javascript" charset="utf-8"></script>
