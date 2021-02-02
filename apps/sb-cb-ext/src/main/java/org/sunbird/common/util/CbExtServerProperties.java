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


	@Override
	public String toString() {
		return "LexServerProperties [ wfServiceHost=" + wfServiceHost 
				+", wfServicePath="+ wfServicePath + ", isUserMultiMapDeptEnabled=" + isUserMultiMapDeptEnabled + ", sbUrl="+sbUrl+"]";
	}
}