package com.infosys.lex.notification.dto;

public class TemplateInfo {


	private String templateText;
	private String templateSubject;
	private String templateOrg;
	private String templateLanguage;

	public TemplateInfo( String templateText, String templateSubject, String templateOrg,
			String templateLanguage) {
		this.templateText = templateText;
		this.templateSubject = templateSubject;
		this.templateOrg = templateOrg;
		this.templateLanguage = templateLanguage;
	}
	public String getTemplateText() {
		return templateText;
	}
	public void setTemplateText(String templateText) {
		this.templateText = templateText;
	}
	public String getTemplateSubject() {
		return templateSubject;
	}
	public void setTemplateSubject(String templateSubject) {
		this.templateSubject = templateSubject;
	}
	
	@Override
	public String toString() {
		return "TemplateInfo [templateText=" + templateText + ", templateSubject=" + templateSubject + ", templateOrg="
				+ templateOrg + ", templateLanguage=" + templateLanguage + "]";
	}
	public String getTemplateOrg() {
		return templateOrg;
	}
	public void setTemplateOrg(String templateOrg) {
		this.templateOrg = templateOrg;
	}
	public String getTemplateLanguage() {
		return templateLanguage;
	}
	public void setTemplateLanguage(String templateLanguage) {
		this.templateLanguage = templateLanguage;
	}
}
