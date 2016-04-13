<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

  

<c:choose>
    <c:when test="${systemInfo.appProfile.code =='PRODUCTION'}">
        
      
        
    </c:when>
    
    <c:otherwise>

          <link type="text/css" rel="stylesheet"
      href="<c:url value="/main/resources/${appId}/paster/css/paste-edit/paste-edit.css"/>"/>

   
          <script src="<c:url value='/main/resources/${appId}/bower_components/ace-builds/src-min-noconflict/ace.js'/>" type="text/javascript" charset="utf-8"></script>

<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/canvas2image.js'/>"></script>

<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/canvas-to-blob.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/pixastic.core.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/pixastic/crop.js'/>"></script>


<script src="<c:url value='/main/resources/${appId}/bower_components/SyntaxHighlighter/scripts/XRegExp.js'/>"></script>

<script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shCore.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shLegacy.js'/>"></script>
<script src="<c:url value='/main/resources/${appId}/local_components/syntax_highlighter/shUber.js'/>"></script>


  <script src="<c:url value='/main/resources/${appId}/paster/js/paste-edit/word-count.js'/>" type="text/javascript" charset="utf-8"></script>

        
    </c:otherwise>
    
</c:choose>


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
    
