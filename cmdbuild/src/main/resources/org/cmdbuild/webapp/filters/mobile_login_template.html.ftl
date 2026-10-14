[#ftl]<html><head><title>MOBILE LOGIN</title></head><body>
<h3>Mobile Login [#if sessionOk]OK[#else]NOT OK[/#if]</h3>
<div style="display:none" id="cm_login_success">${sessionOk?c}</div>
<div style="display:none" id="cm_login_session_token">${sessionToken!}</div>
<script>window.cm_login_success = ${sessionOk?c}; window.cm_login_session_token = '${sessionToken!}';</script>
</body></html>
      