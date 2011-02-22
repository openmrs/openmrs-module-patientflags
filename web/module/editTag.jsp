<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="springform" uri="resources/spring-form.tld"%>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm"
	redirect="/module/patientflags/editTag.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>


<h2><spring:message code="patientflags.admin.title" /></h2>

<div><b class="boxHeader"><spring:message
	code="patientflags.editTag" /></b>
<div class="box"><springform:form modelAttribute="tag">
	<springform:errors path="*" cssClass="error" />
	<br />
	<table>
		<tr>
			<td align="right"><spring:message code="patientflags.tag" />:</td>
			<td><springform:input path="name" /><springform:errors path="name"
				cssClass="error" /></td>
		</tr>
		<tr>
			<td align="right" valign="top"><spring:message
				code="patientflags.editTag.visibleTo" />:</td>
			<td><springform:select path="roles" multiple="true">
				<springform:options items="${roles}" itemValue="role" itemLabel="role" />
			</springform:select></td>
		</tr>
		<tr>
			<td align="right" valign="top"><spring:message
				code="patientflags.editTag.shownIn" />:</td>
			<td><springform:select path="displayPoints" multiple="true">
				<springform:options items="${displayPoints}" itemValue="displayPointId"
					itemLabel="name" />
			</springform:select></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit"
				value="<spring:message code='patientflags.editTag.saveTag'/>" /></springform:form> <a
				href="${pageContext.request.contextPath}/module/patientflags/manageTags.list"><input
				type="button" value="<spring:message code='patientflags.cancel'/>" /></a></td>
		</tr>
	</table></div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>