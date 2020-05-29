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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class HeaderMapRequestWrapper extends HttpServletRequestWrapper{
    private Map<String, String> headerMap = new HashMap<String, String>();

	 /**
	 * @param request
	 */
	public HeaderMapRequestWrapper(HttpServletRequest request) {
	        super(request);
	    }

	    /**
	     * @param name
	     * @param value
	     */
	    public void addHeader(String name, String value) {
	        headerMap.put(name, value);
	    }

	   
	    /* (non-Javadoc)
	     * @see javax.servlet.http.HttpServletRequestWrapper#getHeader(java.lang.String)
	     */
	    public String getHeader(String name) {
	        String headerValue = super.getHeader(name);
	        if (headerMap.containsKey(name)) {
	            headerValue = headerMap.get(name);
	        }
	        return headerValue;
	    }

	    /* (non-Javadoc)
	     * @see javax.servlet.http.HttpServletRequestWrapper#getHeaderNames()
	     */
	    public Enumeration<String> getHeaderNames() {
	        List<String> names = Collections.list(super.getHeaderNames());
	        for (String name : headerMap.keySet()) {
	            names.add(name);
	        }
	        return Collections.enumeration(names);
	    }

	  
	    /* (non-Javadoc)
	     * @see javax.servlet.http.HttpServletRequestWrapper#getHeaders(java.lang.String)
	     */
	    public Enumeration<String> getHeaders(String name) {
	        List<String> values = Collections.list(super.getHeaders(name));
	        if (headerMap.containsKey(name)) {
	            values.add(headerMap.get(name));
	        }
	        return Collections.enumeration(values);
	    }
}
