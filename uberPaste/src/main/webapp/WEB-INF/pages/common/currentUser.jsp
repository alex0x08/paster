<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>

<script type="text/javascript">

    var avatarUrl = 'http://www.gravatar.com/avatar/${currentUser.avatarHash}';

    var avatarOpen = false;

    window.addEvent('domready', function(){

        //First Example
        var el = $('avatarImageDiv'),
                color = el.getStyle('backgroundColor');

        // We are setting the opacity of the element to 0.5 and adding two events
        $('avatarImageDiv').addEvents({
            click: function(){
                if (avatarOpen) {
                    
                this.set('tween', {}).tween('height', '40px');
                $('avatarImage').src=avatarUrl+'?s=32&d=monsterid';
                    avatarOpen = false;
                } else {
                this.set('tween', {
                    duration: 1000,
                    transition: Fx.Transitions.Bounce.easeOut // This could have been also 'bounce:out'
                }).tween('height', '140px');

                $('avatarImage').src=avatarUrl+'?s=128&d=monsterid';
                             avatarOpen = true;
           
                }
            }
        });

    });
</script>
<div id="avatarImageDiv" >
        <img id="avatarImage" style="min-width: 40px;" alt="Gavatar" src="
             <c:url value='http://www.gravatar.com/avatar/${currentUser.avatarHash}'>
                 <c:param name='s' value='32'/>
                 <c:param name='d' value='monsterid'/>
             </c:url>"/>

    <span style="display: inline; vertical-align: top; ">
        <a title="Contact ${currentUser.name}" style="display: inline;vertical-align: top;" href="mailto:${currentUser.username}"><c:out value="${currentUser.name}" /></a>
        <a class="btn sbtn" style="vertical-align: top;" title="Logout" href="<c:url value='/act/logout'/>">
            <i class="fa fa-sign-in"></i></a>
    </span>
</div>
