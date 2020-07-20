package com.infosysit.sdk.models;

/**
 * Created by akansha.goyal on 3/10/2018.
 */

public class ContentJava {
//    Id, Type, Date , Date , Status, % completed , JSON
    private String  id;
    private String type;
    private  String reqDate;
    private String modDate;
    private String expDate;
    private  String status;
    private String perCompleted;
    private  String JSON;
    private String reqByUser;
    private int downloadAttempt;
    private String downloadId;
    private String extension;
    private String children;
    private String downloadChildren;
    private String parentId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getModDate() {
        return modDate;
    }

    public void setModDate(String modDate) {
        this.modDate = modDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPerCompleted() {
        return perCompleted;
    }

    public void setPerCompleted(String perCompleted) {
        this.perCompleted = perCompleted;
    }

    public String getJSON() {
        return JSON;
    }

    public void setJSON(String JSON) {
        this.JSON = JSON;
    }

    @Override
    public String toString() {
        return id;
    }

    public String fullContent(){
        return "Id: "+id+"Status: "+status+"Type: "+type+"RDate: "+reqDate+"MDate: "+modDate+"Json: "+JSON+"PComp: "+perCompleted;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getReqByUser() {
        return reqByUser;
    }

    public int getDownloadAttempt() {
        return downloadAttempt;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public void setReqByUser(String reqByUser) {
        this.reqByUser = reqByUser;
    }

    public void setDownloadAttempt(int downloadAttempt) {
        this.downloadAttempt = downloadAttempt;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String  getExtension() {
        return extension;
    }

    public String getChildren() {
        return children;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getDownloadChildren() {
        return downloadChildren;
    }

    public void setDownloadChildren(String downloadChildren) {
        this.downloadChildren = downloadChildren;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
