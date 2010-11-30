<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm" redirect="/module/patientflags/managePatientFlagsProperties.form" />
<openmrs:require privilege="Manage Global Properties" otherwise="/login.htm" redirect="/module/patientflags/managePatientFlagsProperties.form" />


<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp" %>

<h2><spring:message code="patientflags.admin.title"/></h2>

<div>
	<b class="boxHeader"><spring:message code="patientflags.managePatientFlagsProperties"/></b>
	<div class="box">
		<br/>
		<spring:message code="patientflags.managePatientFlagsProperties.success"/><br/>
		<br/>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>