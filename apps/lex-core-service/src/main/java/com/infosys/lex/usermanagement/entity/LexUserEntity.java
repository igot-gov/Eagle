/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
 © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
 Version: 1.10

 Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
 this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
 the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
 by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
 this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
 under the law.

 Highly Confidential

 */

package com.infosys.lex.usermanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_details",schema = "wingspan")
public class LexUserEntity {
    @Id
    @Column(name = "emp_number")
    private int empNumber;

    @Column
    private String email;

    @Column
    private String name;

    @Column
    private String status;

    @Column(name = "onsite_offshore_indicator")
    private String onsiteOffshoreIndicator;

    @Column
    private String company;

    @Column(name = "job_level")
    private String jobLevel;

    @Column(name = "current_city")
    private String currentCity;

    @Column(name = "ibu_code")
    private String ibuCode;

    @Column(name = "pu_code")
    private String puCode;

    @Column(name = "cu_code")
    private String cuCode;

    @Column(name = "master_customer_code")
    private String masterCustomerCode;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "master_project_code")
    private String masterProjectCode;

    @Column(name = "project_code")
    private String projectCode;

    @Column(name = "joining_date")
    private String joiningDate;

    @Column(name = "is_download_allowed")
    private Boolean isDownloadAllowed;

    public LexUserEntity() {
    }

    public LexUserEntity(int empNumber, String email, String name, String status, String onsiteOffshoreIndicator, String company, String jobLevel, String currentCity, String ibuCode, String puCode, String cuCode, String masterCustomerCode, String customerCode, String masterProjectCode, String projectCode, String joiningDate, Boolean isDownloadAllowed) {
        this.empNumber = empNumber;
        this.email = email;
        this.name = name;
        this.status = status;
        this.onsiteOffshoreIndicator = onsiteOffshoreIndicator;
        this.company = company;
        this.jobLevel = jobLevel;
        this.currentCity = currentCity;
        this.ibuCode = ibuCode;
        this.puCode = puCode;
        this.cuCode = cuCode;
        this.masterCustomerCode = masterCustomerCode;
        this.customerCode = customerCode;
        this.masterProjectCode = masterProjectCode;
        this.projectCode = projectCode;
        this.joiningDate = joiningDate;
        this.isDownloadAllowed = isDownloadAllowed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOnsiteOffshoreIndicator() {
        return onsiteOffshoreIndicator;
    }

    public void setOnsiteOffshoreIndicator(String onsiteOffshoreIndicator) {
        this.onsiteOffshoreIndicator = onsiteOffshoreIndicator;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getIbuCode() {
        return ibuCode;
    }

    public void setIbuCode(String ibuCode) {
        this.ibuCode = ibuCode;
    }

    public String getPuCode() {
        return puCode;
    }

    public void setPuCode(String puCode) {
        this.puCode = puCode;
    }

    public String getCuCode() {
        return cuCode;
    }

    public void setCuCode(String cuCode) {
        this.cuCode = cuCode;
    }

    public String getMasterCustomerCode() {
        return masterCustomerCode;
    }

    public void setMasterCustomerCode(String masterCustomerCode) {
        this.masterCustomerCode = masterCustomerCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getMasterProjectCode() {
        return masterProjectCode;
    }

    public void setMasterProjectCode(String masterProjectCode) {
        this.masterProjectCode = masterProjectCode;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public int getEmpNumber() {
        return empNumber;
    }

    public void setEmpNumber(int empNumber) {
        this.empNumber = empNumber;
    }

    public LexUserEntity(int empNumber) {
        this.empNumber = empNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getDownloadAllowed() {
        return isDownloadAllowed;
    }

    public void setDownloadAllowed(Boolean downloadAllowed) {
        isDownloadAllowed = downloadAllowed;
    }
}
