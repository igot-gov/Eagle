package org.sunbird.progress.service;

import org.sunbird.progress.model.MandatoryContentResponse;

public interface MandatoryContentService {

    public MandatoryContentResponse getMandatoryContentStatusForUser(String rootOrg, String org, String userId);
}
