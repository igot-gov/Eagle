package org.sunbird.progress.model;

public class MandatoryContentInfo {
    private String rootOrg;
    private String org;
    private String contentType;
    private Float minProgressForCompletion = 0.0f;

    private Float userProgress = 0.0f;

    public String getRootOrg() {
        return rootOrg;
    }

    public void setRootOrg(String rootOrg) {
        this.rootOrg = rootOrg;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Float getMinProgressForCompletion() {
        return minProgressForCompletion;
    }

    public void setMinProgressForCompletion(Float minProgressForCompletion) {
        this.minProgressForCompletion = minProgressForCompletion;
    }

    public Float getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(Float userProgress) {
        this.userProgress = userProgress;
    }
}
