<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="springform" uri="resources/spring-form.tld" %>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm" redirect="/module/patientflags/managePatientFlagsProperties.form" />
<openmrs:require privilege="Manage Global Properties" otherwise="/login.htm" redirect="/module/patientflags/managePatientFlagsProperties.form" />


<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp" %>

<h2><spring:message code="patientflags.admin.title"/></h2>

<div>
	<b class="boxHeader"><spring:message code="patientflags.managePatientFlagsProperties"/></b>
	<div class="box">
		<springform:form modelAttribute="properties">
		<springform:errors path="*" cssClass="error"/>
			<br/>
			<springform:checkbox path="patientHeaderDisplay" />&nbsp;<spring:message code="patientflags.managePatientFlagsProperties.patientHeaderDisplay"/><br/>
			<springform:checkbox path="patientOverviewDisplay" />&nbsp;<spring:message code="patientflags.managePatientFlagsProperties.patientOverviewDisplay"/><br/>
			<br/>
			&nbsp;<spring:message code="patientflags.managePatientFlagsProperties.username" />:&nbsp;<springform:input path="username" /><br/>
			<br/>
			<input type="submit" value="<spring:message code='patientflags.saveChanges'/>"/>
		</springform:form>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>