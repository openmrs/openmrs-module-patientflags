<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm"
	redirect="/module/patientflags/manageTags.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>


<h2><spring:message code="patientflags.admin.title" /></h2>
<a
	href="${pageContext.request.contextPath}/module/patientflags/editTag.form"><spring:message
	code="patientflags.manageTags.addTag" /></a>
<br />
<br />
<!--  display the results -->
<div><b class="boxHeader"><spring:message
	code="patientflags.tags" /></b>
<table cellpadding="2" cellspacing="0" class="box">
	<tr>
		<th><spring:message code="patientflags.name" /></th>
		<th>&nbsp;</th>
	</tr>
	<c:set var="i" value="0" />
	<c:forEach items="${tags}" var="tag">
		<tr class="${i % 2 == 0 ? 'evenRow' : 'oddRow'}">
			<td><a
				href="${pageContext.request.contextPath}/module/patientflags/editTag.form?tagId=${tag.tagId}">${tag.name}</a></td>
			<td><a
				href="${pageContext.request.contextPath}/module/patientflags/deleteTag.form?tagId=${tag.tagId}"
				onclick="return confirm('<spring:message code="patientflags.manageTags.deleteTagConfirm"/>');"><spring:message
				code="patientflags.manageTags.deleteTag" /></a></td>
		</tr>
		<c:set var="i" value="${i + 1}" />
	</c:forEach>
</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>