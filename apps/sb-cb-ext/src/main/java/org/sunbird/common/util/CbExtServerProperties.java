package org.sunbird.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CbExtServerProperties {

    @Value("${wf.service.host}")
    private String wfServiceHost;

    @Value("${wf.service.updateUserProfilePath}")
    private String wfServicePath;

    @Value("${user.enable.multidept.mapping}")
    private boolean isUserMultiMapDeptEnabled;

    @Value("${sb.service.url}")
    private String sbUrl;

    @Value("${sb.hub.graph.service.url}")
    private String sbHubGraphServiceUrl;

    @Value("${spring.data.cassandra.sb.username}")
    private String cassandraUserName;

    @Value("${spring.data.cassandra.sb.password}")
    private String cassandraPassword;

    @Value("${spring.data.cassandra.sb.keyspace-name}")
    private String sbExtKeyspace;

    public String getWfServiceHost() {
        return wfServiceHost;
    }

    public void setWfServiceHost(String wfServiceHost) {
        this.wfServiceHost = wfServiceHost;
    }

    public String getWfServicePath() {
        return wfServicePath;
    }

    public void setWfServicePath(String wfServicePath) {
        this.wfServicePath = wfServicePath;
    }

    public boolean isUserMultiMapDeptEnabled() {
        return isUserMultiMapDeptEnabled;
    }

    public void setUserMultiMapDeptEnabled(boolean isUserMultiMapDeptEnabled) {
        this.isUserMultiMapDeptEnabled = isUserMultiMapDeptEnabled;
    }

    public String getSbUrl() {
        return sbUrl;
    }

    public void setSbUrl(String sbUrl) {
        this.sbUrl = sbUrl;
    }

    public String getSbHubGraphServiceUrl() {
        return sbHubGraphServiceUrl;
    }

    public void setSbHubGraphServiceUrl(String sbHubGraphServiceUrl) {
        this.sbHubGraphServiceUrl = sbHubGraphServiceUrl;
    }

    public String getCassandraUserName() {
        return cassandraUserName;
    }

    public void setCassandraUserName(String cassandraUserName) {
        this.cassandraUserName = cassandraUserName;
    }

    public String getCassandraPassword() {
        return cassandraPassword;
    }

    public void setCassandraPassword(String cassandraPassword) {
        this.cassandraPassword = cassandraPassword;
    }

    public String getSunbirdKeyspace() {
        return sbExtKeyspace;
    }

    public void setSunbirdKeyspace(String sunbirdKeyspace) {
        this.sbExtKeyspace = sunbirdKeyspace;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("SB-CB-Ext Server Properties: ");
        str.append("[wfServiceHost=").append(wfServiceHost).append("],");
        str.append("[wfServicePath=").append(wfServicePath).append("],");
        str.append("[isUserMultiMapDeptEnabled=").append(isUserMultiMapDeptEnabled).append("],");
        str.append("[sbUrl=").append(sbUrl).append("],");
        str.append("[sbHubGraphServiceUrl=").append(sbHubGraphServiceUrl).append("]");
        return str.toString();
    }


}