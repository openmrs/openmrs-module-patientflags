<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="springform" uri="resources/spring-form.tld"%>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm"
	redirect="/module/patientflags/editPriority.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>


<h2><spring:message code="patientflags.admin.title" /></h2>

<div><b class="boxHeader"><spring:message
	code="patientflags.editPriority" /></b>
<div class="box"><springform:form modelAttribute="priority">
	<springform:errors path="*" cssClass="error" />
	<br />
	<table>
		<tr>
			<td align="right"><spring:message code="patientflags.name" />:</td>
			<td><springform:input path="name" /><springform:errors path="name"
				cssClass="error" /></td>
		</tr>
		<tr>
			<td align="right"><spring:message code="patientflags.style" />:</td>
			<td><springform:input path="style" /><springform:errors path="style"
				cssClass="error" /></td>
		</tr>
		<tr>
			<td align="right"><spring:message code="patientflags.rank" />:</td>
			<td><springform:input path="rank" /><springform:errors path="rank"
				cssClass="error" /></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit"
				value="<spring:message code='patientflags.editPriority.savePriority'/>" /></springform:form> <a
				href="${pageContext.request.contextPath}/module/patientflags/managePriorities.list"><input
				type="button" value="<spring:message code='patientflags.cancel'/>" /></a></td>
		</tr>
	</table></div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>