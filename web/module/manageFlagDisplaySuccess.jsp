<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="resources/spring-form.tld" %>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm" redirect="/module/accesslogging/accessLogManage.form" />
<openmrs:require privilege="Manage Global Properties" otherwise="/login.htm" redirect="/module/accesslogging/accessLogManage.form" />


<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp" %>

<h2><spring:message code="patientflags.admin.title"/></h2>

<div>
	<b class="boxHeader"><spring:message code="patientflags.manageFlagDisplay"/></b>
	<div class="box">
		<br/>
		<spring:message code="patientflags.manageFlagDisplay.success"/><br/>
		<br/>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>