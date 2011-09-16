<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="springform" uri="resources/spring-form.tld"%>

<openmrs:require privilege="Manage Flags" otherwise="/login.htm"
	redirect="/module/patientflags/editFlag.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<script type="text/javascript"
	src="${pageContext.request.contextPath}/scripts/jquery/jquery-1.3.2.min.js"></script>

<script type="text/javascript"><!--

	// function that sets the value of the custom evaluator radiobutton to the value of the custom evaluator text box
	function updateCustomEvaluatorValue(){
		var className = $('#customEvaluatorTextbox').attr('value');
		$('#customEvaluator').attr('value', className);	
	}
	
	$(document).ready(function(){
		// 'check' the custom evaluator radio button if the custom evaluator text box has text upon loading
		if(!($('#customEvaluatorTextbox').attr('value') == "")){
			updateCustomEvaluatorValue();
			$('#customEvaluator').attr('checked', true);
		}else{
			$('#customEvaluatorBlock').hide();
		}

		// event handlers to hide and show custom evaluator text box
		$('.evaluator').click(function disableCustomEvaluator(){
			$('#customEvaluatorBlock').hide();
		});
		$('#customEvaluator').click(function (){
			$('#customEvaluatorBlock').show();
		});

		// event handler to update custom evaluator value on change to value of custom evaluator text box
		$('#customEvaluatorTextbox').change(updateCustomEvaluatorValue);
	});
-->
</script>

<h2><spring:message code="patientflags.admin.title" /></h2>

<div><b class="boxHeader"><spring:message
	code="patientflags.editFlag" /></b>
<div class="box"><springform:form modelAttribute="flag">
	<springform:errors path="*" cssClass="error" />
	<br />
	<table>
		<tr>
			<td align="right"><spring:message code="patientflags.name" />:</td>
			<td><springform:input path="name" size="50" /><springform:errors
				path="name" cssClass="error" /></td>
		</tr>
		<tr>
			<td align="right" valign="top"><spring:message
				code="patientflags.type" />:</td>
			<td><springform:radiobutton cssClass="evaluator" path="evaluator" value="groovy" /> <spring:message
				code="patientflags.editFlag.groovy" /></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><springform:radiobutton cssClass="evaluator" path="evaluator" value="sql" /> <spring:message
				code="patientflags.editFlag.sql" /> <spring:message
				code="patientflags.editFlag.exampleSQLCriteria" /></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><springform:radiobutton cssClass="evaluator" path="evaluator" value="logic" /> <spring:message
				code="patientflags.editFlag.logic" /> <spring:message
				code="patientflags.editFlag.exampleLogicCriteria" /> <springform:errors
				path="evaluator" cssClass="error" /></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><springform:radiobutton id="customEvaluator" path="evaluator" value="dummyValue"/> <spring:message
				code="patientflags.editFlag.custom" /> <span id="customEvaluatorBlock"><spring:message
				code="patientflags.editFlag.customEvaluator" /> <input id="customEvaluatorTextbox" name="customEvaluator" type="text" value="${customEvaluator}" size="70"/></div><springform:errors
				path="evaluator" cssClass="error" /></td>
		</tr>
		<tr>
			<td align="right" valign="top"><spring:message
				code="patientflags.criteria" />:</td>
			<td><springform:textarea path="criteria" rows="15" cols="80" /><springform:errors
				path="criteria" cssClass="error" /></td>
		</tr>
		<tr>
			<td align="right"><spring:message code="patientflags.message" />:</td>
			<td><springform:input path="message" size="50" /><springform:errors
				path="message" cssClass="error" /></td>
		</tr>
		<tr>
			<td align="right" valign="top"><spring:message
				code="patientflags.priority" />:</td>
			<td><springform:select path="priority">
				<option value="">&nbsp;</option>
				<springform:options items="${priorities}" itemValue="priorityId" itemLabel="name" />
			</springform:select></td>
		</tr>
		<tr>
			<td align="right" valign="top"><spring:message
				code="patientflags.editFlag.associatedTags" />:</td>
			<td><springform:select path="tags" multiple="true">
				<springform:options items="${tags}" itemValue="tagId" itemLabel="name" />
			</springform:select></td>
		</tr>
		<tr>
			<td align="right"><spring:message
				code="patientflags.editFlag.enabled" />:</td>
			<td><springform:checkbox path="enabled" /></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit"
				value="<spring:message code='patientflags.editFlag.saveFlag'/>" /></springform:form>
			<a
				href="${pageContext.request.contextPath}/module/patientflags/manageFlags.form"><input
				type="button" value="<spring:message code='patientflags.cancel'/>" /></a></td>
		</tr>
	</table></div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>