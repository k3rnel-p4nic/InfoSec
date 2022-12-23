package servlet.improved;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class ImprovedHttpServletRequestWrapper extends HttpServletRequestWrapper {

	public ImprovedHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		return super.getParameter(name);
	}

	@Override
	public String[] getParameterValues(String name) {
		return super.getParameterValues(name);
	}

	@Override
	public String getHeader(String name) {
		return super.getHeader(name);
	}

	
	private String sanitizeString(String value) {
		if (value == null || value.isEmpty())
			return value;
		
        value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        value = value.replaceAll("'", "& #39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        return value;
	}
}
