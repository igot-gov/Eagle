package org.sunbird.progress.model;

import java.util.HashMap;
import java.util.Map;

public class MandatoryContentResponse {
    private boolean mandatoryCourseCompleted;
    private Map<String, MandatoryContentInfo> contentDetails;

    public void addContentInfo(String contentId, MandatoryContentInfo contentInfo) {
        if (contentDetails == null) {
            contentDetails = new HashMap<String, MandatoryContentInfo>();
        }
        contentDetails.put(contentId, contentInfo);
    }

    public boolean isMandatoryCourseCompleted() {
        return mandatoryCourseCompleted;
    }

    public void setMandatoryCourseCompleted(boolean mandatoryCourseCompleted) {
        this.mandatoryCourseCompleted = mandatoryCourseCompleted;
    }

    public Map<String, MandatoryContentInfo> getContentDetails() {
        return contentDetails;
    }

    public void setContentDetails(Map<String, MandatoryContentInfo> contentDetails) {
        this.contentDetails = contentDetails;
    }
}
