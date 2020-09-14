package com.infosys.lex.ext.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.sunbird.common.models.util.LoggerEnum;
import org.sunbird.common.models.util.ProjectLogger;

@Component
@WebFilter("/*")
public class ResponseTimeLogger implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		long time = System.currentTimeMillis();
		try {
			chain.doFilter(req, resp);
		} finally {
			time = System.currentTimeMillis() - time;
			ProjectLogger.log("URL : " + ((HttpServletRequest) req).getRequestURI() + " Time Taken : " + time + " ms",
					LoggerEnum.INFO);
		}
	}

	@Override
	public void destroy() {
	}
}