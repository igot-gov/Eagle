package org.sunbird.common.model;

public class OpenSaberApiResp {
	private String id;
	private String ver;
	private String ets;
	private SunbirdApiRespParam params;
	private String responseCode;
	private OpenSaberApiRespResult result;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getEts() {
		return ets;
	}

	public void setEts(String ets) {
		this.ets = ets;
	}

	public SunbirdApiRespParam getParams() {
		return params;
	}

	public void setParams(SunbirdApiRespParam params) {
		this.params = params;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public OpenSaberApiRespResult getResult() {
		return result;
	}

	public void setResult(OpenSaberApiRespResult result) {
		this.result = result;
	}

}
