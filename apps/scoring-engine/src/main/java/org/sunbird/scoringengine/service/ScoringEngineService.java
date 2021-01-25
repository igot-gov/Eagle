

package org.sunbird.scoringengine.service;

import org.sunbird.scoringengine.models.EvaluatorModel;
import org.sunbird.scoringengine.models.Response;

public interface ScoringEngineService {

    public Response addV2(EvaluatorModel evaluatorModel) throws Exception;
    public Response addV3(EvaluatorModel evaluatorModel) throws Exception;
    public Response searchV2(EvaluatorModel evaluatorModel) throws Exception;
    public Response getTemplate(String templateId, String rootOrg, String org) throws Exception;
}
