/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
Version: 1.10

Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
under the law.

Highly Confidential
 
*/
package com.infosys.lex.common.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.util.UuidUtil;

@WebFilter("/*")
public class StatsFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		// Add request data to thread context
		ThreadContext.put("RequestId", UuidUtil.getTimeBasedUuid().toString());
		ThreadContext.put("req.url",
				((HttpServletRequest) req).getRequestURI() + (((HttpServletRequest) req).getQueryString() == null ? ""
						: ("?" + ((HttpServletRequest) req).getQueryString())));
		ThreadContext.put("req.headUrl",((HttpServletRequest) req).getRequestURI());
		ThreadContext.put("req.method",((HttpServletRequest) req).getMethod());
		
		try {
			chain.doFilter(req, resp);
		} finally {
			// clear the thread context
			ThreadContext.clearAll();
		}
	}

	@Override
	public void destroy() {
	}

}
