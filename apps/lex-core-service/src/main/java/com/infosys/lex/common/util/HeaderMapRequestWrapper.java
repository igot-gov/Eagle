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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class HeaderMapRequestWrapper extends HttpServletRequestWrapper{
    private Map<String, String> headerMap = new HashMap<String, String>();

	 public HeaderMapRequestWrapper(HttpServletRequest request) {
	        super(request);
	    }

	    public void addHeader(String name, String value) {
	        headerMap.put(name, value);
	    }

	   
	    public String getHeader(String name) {
	        String headerValue = super.getHeader(name);
	        if (headerMap.containsKey(name)) {
	            headerValue = headerMap.get(name);
	        }
	        return headerValue;
	    }

	    public Enumeration<String> getHeaderNames() {
	        List<String> names = Collections.list(super.getHeaderNames());
	        for (String name : headerMap.keySet()) {
	            names.add(name);
	        }
	        return Collections.enumeration(names);
	    }

	  
	    public Enumeration<String> getHeaders(String name) {
	        List<String> values = Collections.list(super.getHeaders(name));
	        if (headerMap.containsKey(name)) {
	            values.add(headerMap.get(name));
	        }
	        return Collections.enumeration(values);
	    }
}