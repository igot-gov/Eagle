package com.infosys.lexauthoringservices.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.springframework.stereotype.Component;

/**
 * this filter is used to inject a requestId for tracing the logs of one
 * particular request.
 * 
 * @author saurav.bhasin
 *
 */
@Component
@Order(1)
public class LogFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// Add request data to thread context

		ThreadContext.put("reqId", UuidUtil.getTimeBasedUuid().toString());

		HttpServletRequest req = (HttpServletRequest) request;

		String requestUrl = req.getRequestURI();
		String queryString = req.getQueryString();

		if (requestUrl != null) {
			ThreadContext.put("reqUrl", req.getRequestURI());
			if (queryString != null) {
				ThreadContext.put("reqUrl", ThreadContext.get("reqUrl") + "?" + queryString);
			}
		}

		try {
			chain.doFilter(request, response);
		} finally {
			ThreadContext.clearAll();
		}
	}

	@Override
	public void destroy() {
	}

}
