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

package com.infosys.lex.notification.filter;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HeaderFilter implements Filter {
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		System.out.println("Destroying HeadFilter");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		
			HttpServletRequest req = (HttpServletRequest) request;
			HeaderMapRequestWrapper requestWrapper = new 
					HeaderMapRequestWrapper(req);
			
			ThreadContext.put("RequestId", UuidUtil.getTimeBasedUuid().toString());
			ThreadContext.put("req.url",
					((HttpServletRequest) req).getRequestURI() + (((HttpServletRequest) req).getQueryString() == null ? ""
							: ("?" + ((HttpServletRequest) req).getQueryString())));
			String remote_addr = request.getRemoteAddr();
			requestWrapper.addHeader("remote_addr", remote_addr);
			filterChain.doFilter(requestWrapper, response); 
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("Initiating HeaderFilter");
	}
	
}
