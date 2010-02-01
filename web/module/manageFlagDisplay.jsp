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
		<form:form modelAttribute="properties">
		<form:errors path="*" cssClass="error"/>
			<br/>
			<form:checkbox path="patientHeaderDisplay" />&nbsp;<spring:message code="patientflags.manageFlagDisplay.patientHeaderDisplay"/><br/>
			<form:checkbox path="patientOverviewDisplay" />&nbsp;<spring:message code="patientflags.manageFlagDisplay.patientOverviewDisplay"/><br/>
			<br/>
			<input type="submit" value="<spring:message code='patientflags.saveChanges'/>"/>
		</form:form>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>