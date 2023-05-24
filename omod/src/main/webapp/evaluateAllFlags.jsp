<%@ include file="/WEB-INF/view/module/legacyui/template/include.jsp" %>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm"
	redirect="/module/patientflags/evaluateAllFlags.htm" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp" %>
<script type="text/javascript">

    function inProgress() {
        $j("#evaluateButton").prop("disabled", true);
        $j("#failure").hide();
        $j("#success").hide();
        $j("#progress").show();
    }

    function onSuccess(data) {
        $j("#progress").hide();
        if (data && data.success) {
            $j("#success").show();
        } else {
            $j("#failure").show();
        }
        $j("#evaluateButton").prop("disabled", false);
    }

    function onError(data) {
        $j("#progress").hide();
        $j("#success").hide();
        $j("#failure").show();
        $j("#evaluateButton").prop("disabled", false);
    }

    function evaluateAllFlags() {
        inProgress();
        $j.ajax({
            "type": "POST",
            "url": "${pageContext.request.contextPath}/module/patientflags/revaluateAllFlags.htm",
            "data": {},
            "dataType": "json",
            "success": checkStatus,
            "error": onError
        });
    }

    function updateStatusOnUi(data) {
        if (data.status === "success") {
            onSuccess({success: true});
        } else if (data.status === "error") {
            onError(data);
        } else {
            setTimeout(checkStatus, 5000);
        }
    }

    function checkStatus() {
        $j.ajax({
            "type": "GET",
            "url": "${pageContext.request.contextPath}/module/patientflags/evaluateAllFlagsStatus.htm",
            "data": {},
            "dataType": "json",
            "success": updateStatusOnUi,
            "error": onError
        });
    }

    $j(document).ready(function () {
        $j("#progress").hide();
        $j("#success").hide();
        $j("#failure").hide();
    });
</script>

<h2><openmrs:message code="patientflags.evaluateAllFlags"/></h2>

<div class="dashedAndHighlighted">
    <p><openmrs:message code="patientflags.evaluateAllFlags.message"/></p>
</div>
<br>
<input id="evaluateButton" type="submit" value='<openmrs:message code="patientflags.evaluateAllFlags"/>' onclick="evaluateAllFlags()">
<br>
<div id="progress">
    <p><openmrs:message code="patientflags.inProgress.message"/></p>
    <img id="evaluating_progress_img" src="<openmrs:contextPath/>/images/loading.gif"/>
</div>
<br>
<div id="success">
    <p><openmrs:message code="patientflags.completed.message"/></p>
</div>
<div class="error" id="failure">
    <p><openmrs:message code="patientflags.failure.message"/></p>
</div>


<%@ include file="/WEB-INF/view/module/legacyui/template/footer.jsp" %>