package org.sunbird.portal.department.model;

public class DeptTypeInfo {
	private Integer id;
	private String deptType;
	private String deptSubType;
	private String description;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDeptType() {
		return deptType;
	}

	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}

	public String getDeptSubType() {
		return deptSubType;
	}

	public void setDeptSubType(String deptSubType) {
		this.deptSubType = deptSubType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		StringBuilder str = new StringBuilder("DeptTypeInfo: ");
		str.append(", Id: ").append(id);
		str.append(", DeptType: ").append(deptType);
		str.append(", deptSubType: ").append(deptSubType);
		str.append(", description: ").append(description);
		str.append("]");
		return str.toString();
	}
}
