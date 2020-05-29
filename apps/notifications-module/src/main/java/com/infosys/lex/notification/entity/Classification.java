package com.infosys.lex.notification.entity;
//package com.infosys.lex.notification.entity;
//
//import javax.persistence.Column;
//import javax.persistence.EmbeddedId;
//import javax.persistence.Entity;
//import javax.persistence.Table;
//import javax.validation.constraints.Pattern;
//
//@Entity
//@Table(name = "notification_classification", schema = "wingspan")
//public class Classification {
//
//	@EmbeddedId
//	private ClassificationKey primaryKey;
//
//	@Column(name = "classification")
//	@Pattern(regexp = "Information|Action")
//	private String classification;
//
//	public ClassificationKey getPrimaryKey() {
//		return primaryKey;
//	}
//
//	public String getClassification() {
//		return classification;
//	}
//
//	public void setPrimaryKey(ClassificationKey primaryKey) {
//		this.primaryKey = primaryKey;
//	}
//
//	public void setClassification(String classification) {
//		this.classification = classification;
//	}
//
//	public Classification() {
//		super();
//	}
//
//	public Classification(ClassificationKey primaryKey, String classification) {
//		super();
//		this.primaryKey = primaryKey;
//		this.classification = classification;
//	}
//
//	@Override
//	public String toString() {
//		return "Classification [primaryKey=" + primaryKey + ", classification=" + classification + "]";
//	}
//
//}
