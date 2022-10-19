<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>


<style>
.spinner {
   position: relative;
   width: 15.7px;
   height: 15.7px;
}
.spinner div {
   animation: spinner-4t3wzl 1.875s infinite backwards;
   background-color: #5078f6;
   border-radius: 50%;
   height: 100%;
   position: absolute;
   width: 100%;
}
.spinner div:nth-child(1) {
   animation-delay: 0.15s;
   background-color: rgba(80,120,246,0.9);
}
.spinner div:nth-child(2) {
   animation-delay: 0.3s;
   background-color: rgba(80,120,246,0.8);
}
.spinner div:nth-child(3) {
   animation-delay: 0.45s;
   background-color: rgba(80,120,246,0.7);
}
.spinner div:nth-child(4) {
   animation-delay: 0.6s;
   background-color: rgba(80,120,246,0.6);
}
.spinner div:nth-child(5) {
   animation-delay: 0.75s;
   background-color: rgba(80,120,246,0.5);
}
@keyframes spinner-4t3wzl {
   0% {
      transform: rotate(0deg) translateY(-200%);
   }
   60%, 100% {
      transform: rotate(360deg) translateY(-200%);
   }
}
</style>

<div class='row justify-content-md-center'>
    <div class='col-md-4'>
        <fmt:message key='paster.restating.title' />
    </div>
</div>
<div class='row justify-content-md-center'>
    <div class='col-md-2'>
        <p style='padding-top: 2em;'>
    <div class="spinner">
      <div></div>
      <div></div>
      <div></div>
      <div></div>
      <div></div>
    </div>
            </p>
    </div>
</div>
