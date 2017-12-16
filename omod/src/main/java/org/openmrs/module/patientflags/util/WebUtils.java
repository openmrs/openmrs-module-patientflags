package org.openmrs.module.patientflags.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openmrs.module.webservices.rest.web.RequestContext;

import com.mysql.jdbc.StringUtils;

public class WebUtils {
	
	public static final SimpleDateFormat GLOBAL_JAVA_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getStringFilter(String param, RequestContext req) {
		return (StringUtils.isEmptyOrWhitespaceOnly(req.getParameter(param))) ? null : req.getParameter(param);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Enum getEnumFilter(String param, Class enumType, RequestContext req) {
		String filterVal = getStringFilter(param, req);
		if (filterVal != null) {
			return Enum.valueOf(enumType, filterVal);
		}
		return null;
	}
	
	public static Integer getIntegerFilter(String param, RequestContext req) {
		String strval = getStringFilter(param, req);
		return strval == null ? null : Integer.parseInt(strval);
	}
	
	public static Boolean getBooleanFilter(String param, RequestContext req) {
		String strval = getStringFilter(param, req);
		return strval == null ? null : Boolean.parseBoolean(strval);
	}
	
	public static Float getFloatFilter(String param, RequestContext req) {
		String strval = getStringFilter(param, req);
		return strval == null ? null : Float.parseFloat(strval);
	}
	
	public static Date getDateFilter(String param, RequestContext req) throws ParseException {
		String strval = getStringFilter(param, req);
		return strval == null ? null : GLOBAL_JAVA_DATE_FORMAT.parse(strval);
	}
}
