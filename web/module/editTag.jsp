<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm"
	redirect="/module/patientflags/editTag.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>


<h2><spring:message code="patientflags.admin.title" /></h2>

<div><b class="boxHeader"><spring:message
	code="patientflags.editTag" /></b>
<div class="box"><form:form modelAttribute="tag">
	<form:errors path="*" cssClass="error" />
	<br />
	<table>
		<tr>
			<td align="right"><spring:message code="patientflags.tag" />:</td>
			<td><form:input path="name" /><form:errors path="name"
				cssClass="error" /></td>
		</tr>
		<tr>
			<td align="right" valign="top"><spring:message
				code="patientflags.editTag.visibleTo" />:</td>
			<td><form:select path="roles" multiple="true">
				<form:options items="${roles}" itemValue="role" itemLabel="role" />
			</form:select></td>
		</tr>
		<tr>
			<td align="right" valign="top"><spring:message
				code="patientflags.editTag.shownIn" />:</td>
			<td><form:select path="displayPoints" multiple="true">
				<form:options items="${displayPoints}" itemValue="displayPointId"
					itemLabel="name" />
			</form:select></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit"
				value="<spring:message code='patientflags.editTag.saveTag'/>" /></form:form> <a
				href="${pageContext.request.contextPath}/module/patientflags/manageTags.list"><input
				type="button" value="<spring:message code='patientflags.cancel'/>" /></a></td>
		</tr>
	</table></div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>