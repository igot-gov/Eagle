/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.lex.hierarchy.service;

import java.util.List;
import java.util.Map;


import com.infosys.lex.core.exception.BadRequestException;

public interface HierarchyService {

	Map<String,Object> getHierarchyOfContentNode(String identifier,Map<String,Object> reqMap) throws BadRequestException,Exception;

	List<Map<String,Object>> getMetasHierarchy(Map<String, Object> requestMap)throws BadRequestException,Exception;

}
