<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="springform" uri="resources/spring-form.tld"%>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm"
	redirect="/module/patientflags/manageFlags.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>


<h2><spring:message code="patientflags.admin.title" /></h2>
<a
	href="${pageContext.request.contextPath}/module/patientflags/editFlag.form"><spring:message
	code="patientflags.manageFlags.addFlag" /></a>
<br />
<br />

<!--  display the filter box -->
<c:if test="${!empty tags}">
<div><b class="boxHeader"><spring:message
	code="patientflags.manageFlags.filterBy" /></b> <springform:form
	modelAttribute="filter">
	<table cellpadding="2" cellspacing="0" class="box">
		<tr>
			<td><springform:select path="tags" multiple="true">
				<springform:options items="${tags}" itemValue="tagId" itemLabel="name" />
			</springform:select></td>
			<td align="left" valign="top"><nobr><springform:radiobutton
				path="type" value="ANYTAG" /> <spring:message
				code='patientflags.manageFlags.anyTag' /></nobr><br />
			<nobr><springform:radiobutton path="type" value="ALLTAGS" /> <spring:message
				code='patientflags.manageFlags.allTags' /></nobr></td>
			<td width="100%">&nbsp;</td>
		</tr>
		<tr>
			<td><input type="submit"
				value="<spring:message code='patientflags.filter'/>" /></td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
	</table>
</springform:form></div>
<br />
</c:if>

<!--  display the flags -->
<div><b class="boxHeader"><spring:message
	code="patientflags.flags" /></b>
<table cellpadding="2" cellspacing="0" class="box">
	<tr>
		<th><spring:message code="patientflags.name" /></th>
		<th><spring:message code="patientflags.tags" /></th>
		<th><spring:message code="patientflags.priority" /></th>
		<th><spring:message code="patientflags.status" /></th>
		<th>&nbsp;</th>
	</tr>
	<c:set var="i" value="0" />
	<c:forEach items="${flags}" var="flag">
		<tr class="${i % 2 == 0 ? 'evenRow' : 'oddRow'}">
			<td><a
				href="${pageContext.request.contextPath}/module/patientflags/editFlag.form?flagId=${flag.flagId}">${flag.name}</a></td>
			<td><c:forEach items="${flag.tags}" var="tag">
					${tag.name};
				</c:forEach></td>
			<td>${flag.priority != null ? flag.priority.name : ''}</td>

			<c:choose>
				<c:when test="${flag.enabled}">
					<td><spring:message code="patientflags.enabled" /></td>
				</c:when>
				<c:otherwise>
					<td><spring:message code="patientflags.disabled" /></td>
				</c:otherwise>
			</c:choose>

			<td>
				<a href="${pageContext.request.contextPath}/module/patientflags/findFlaggedPatients.form?flagId=${flag.flagId}">
				   <spring:message code="patientflags.preview" />
				</a>
				&nbsp;|&nbsp;
				<a href="${pageContext.request.contextPath}/module/patientflags/deleteFlag.form?flagId=${flag.flagId}"
				   onclick="return confirm('<spring:message code="patientflags.manageFlags.deleteFlagConfirm"/>');">
				   <spring:message code="patientflags.manageFlags.deleteFlag" />
				</a>
			</td>
		</tr>
		<c:set var="i" value="${i + 1}" />
	</c:forEach>
</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>