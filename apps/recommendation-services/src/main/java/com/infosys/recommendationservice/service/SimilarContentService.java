/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.recommendationservice.service;

import com.infosys.recommendationservice.model.Response;
import com.infosys.recommendationservice.model.UserCompetencyRequest;

import java.util.Set;

public interface SimilarContentService {

    public Response findSimilarContents(String userId, String rootOrg, String org, String locale, String contentId, int pageNo, int pageSize, Set<String> sourceFields);

}
