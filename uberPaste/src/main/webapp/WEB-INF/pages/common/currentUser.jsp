<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">

    var avatarUrl = 'http://www.gravatar.com/avatar/${currentUser.avatarHash}';

    window.addEvent('domready', function(){

        //First Example
        var el = $('avatarImageDiv'),
                color = el.getStyle('backgroundColor');

        // We are setting the opacity of the element to 0.5 and adding two events
        $('avatarImageDiv').addEvents({
            mouseenter: function(){

                this.set('tween', {
                    duration: 1000,
                    transition: Fx.Transitions.Bounce.easeOut // This could have been also 'bounce:out'
                }).tween('height', '140px');

                $('avatarImage').src=avatarUrl+'?s=128';

            },
            mouseleave: function(){
                this.set('tween', {}).tween('height', '40px');
                $('avatarImage').src=avatarUrl+'?s=32';
            }
        });

    });
</script>
<div id="avatarImageDiv" >
    <a href="http://ru.gravatar.com/site/check/${currentUser.username}" >
        <img id="avatarImage" style="min-width: 40px;" alt="Gavatar" src="<c:out value='http://www.gravatar.com/avatar/${currentUser.avatarHash}?s=32'/>"/>
    </a>
                <span style="display: inline; vertical-align: top; ">
                           <a title="Contact ${currentUser.name}" style="display: inline;vertical-align: top;" href="mailto:${currentUser.username}"><c:out value="${currentUser.name}" /></a>
                            <a class="btn sbtn" style="display: inline;vertical-align: top;" title="Logout" href="<c:url value='/act/logout'/>">X</a>
                </span>
</div>
