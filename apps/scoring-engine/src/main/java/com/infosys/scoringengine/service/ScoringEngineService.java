/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.scoringengine.service;

import com.infosys.scoringengine.models.EvaluatorModel;
import com.infosys.scoringengine.models.Response;

public interface ScoringEngineService {

    public Response addV2(EvaluatorModel evaluatorModel) throws Exception;
    public Response addV3(EvaluatorModel evaluatorModel) throws Exception;
    public Response searchV2(EvaluatorModel evaluatorModel) throws Exception;
}
