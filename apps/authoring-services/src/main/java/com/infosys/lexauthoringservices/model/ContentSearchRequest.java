/*
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
package com.infosys.lexauthoringservices.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContentSearchRequest implements Serializable {

    private ContentSearchRequestNode leftNode = new ContentSearchRequestNode();
    private ContentSearchRequestNode rightNode = new ContentSearchRequestNode();;
    private ContentSearchRequestRelation relation = new ContentSearchRequestRelation();
    private List<ContentSearchRequestOrders> orderBy = Collections.emptyList();
    private Integer limit = 10;
    private Integer skip = 0;

    public ContentSearchRequest() {
    }

    public ContentSearchRequestNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(ContentSearchRequestNode leftNode) {
        this.leftNode = leftNode;
    }

    public ContentSearchRequestRelation getRelation() {
        return relation;
    }

    public void setRelation(ContentSearchRequestRelation relation) {
        this.relation = relation;
    }

    public ContentSearchRequestNode getRightNode() {
        return rightNode;
    }

    public void setRightNode(ContentSearchRequestNode rightNode) {
        this.rightNode = rightNode;
    }

    public List<ContentSearchRequestOrders> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<ContentSearchRequestOrders> orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }
}
