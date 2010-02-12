<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="resources/spring-form.tld" %>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm" redirect="/module/patientflags/managePatientFlagsProperties.form" />
<openmrs:require privilege="Manage Global Properties" otherwise="/login.htm" redirect="/module/patientflags/managePatientFlagsProperties.form" />


<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp" %>

<h2><spring:message code="patientflags.admin.title"/></h2>

<div>
	<b class="boxHeader"><spring:message code="patientflags.managePatientFlagsProperties"/></b>
	<div class="box">
		<form:form modelAttribute="properties">
		<form:errors path="*" cssClass="error"/>
			<br/>
			<form:checkbox path="patientHeaderDisplay" />&nbsp;<spring:message code="patientflags.managePatientFlagsProperties.patientHeaderDisplay"/><br/>
			<form:checkbox path="patientOverviewDisplay" />&nbsp;<spring:message code="patientflags.managePatientFlagsProperties.patientOverviewDisplay"/><br/>
			<br/>
			&nbsp;<spring:message code="patientflags.managePatientFlagsProperties.username" />:&nbsp;<form:input path="username" /><br/>
			<br/>
			<input type="submit" value="<spring:message code='patientflags.saveChanges'/>"/>
		</form:form>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>