package com.infosys.lex.portal.department.model;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.List;

public class UserRoleDepartmentInfo {

    private String userId;

    private String oldDeptName;

    private String newDeptName;

    private List<String> roles;

    private Boolean isActive;

    private Boolean isBlocked;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOldDeptName() {
        return oldDeptName;
    }

    public void setOldDeptName(String oldDeptName) {
        this.oldDeptName = oldDeptName;
    }

    public String getNewDeptName() {
        return newDeptName;
    }

    public void setNewDeptName(String newDeptName) {
        this.newDeptName = newDeptName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }
}
