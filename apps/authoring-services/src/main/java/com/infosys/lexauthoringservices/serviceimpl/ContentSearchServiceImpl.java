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
package com.infosys.lexauthoringservices.serviceimpl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infosys.lexauthoringservices.model.ContentSearchRequest;
import com.infosys.lexauthoringservices.model.ContentSearchRequestNodeFilters;
import com.infosys.lexauthoringservices.model.ContentSearchRequestOrders;
import com.infosys.lexauthoringservices.model.ContentSearchRequestRelationFilters;
import com.infosys.lexauthoringservices.util.LexLogger;
import org.neo4j.driver.v1.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@Service
public class ContentSearchServiceImpl {

    private LexLogger logger = new LexLogger(ContentSearchServiceImpl.class.getName());

    private Map<String, PropertyDescriptor> propertyDescriptorsNodeFilters = null;
    private Map<String, PropertyDescriptor> propertyDescriptorsRelationFilters = null;
    private List<String> listTypeNodeFilters = Arrays.asList("labels","catalogPaths","region");

    @PostConstruct
    private void extractPropertyDescriptors() {
        propertyDescriptorsNodeFilters = Arrays.stream(BeanUtils.getPropertyDescriptors(ContentSearchRequestNodeFilters.class)).filter(pd -> Objects.nonNull(pd.getReadMethod()) && pd.getReadMethod().getDeclaringClass() == ContentSearchRequestNodeFilters.class).collect(Collectors.toMap(FeatureDescriptor::getName, pd -> pd));
        propertyDescriptorsRelationFilters = Arrays.stream(BeanUtils.getPropertyDescriptors(ContentSearchRequestRelationFilters.class)).filter(pd -> Objects.nonNull(pd.getReadMethod()) && pd.getReadMethod().getDeclaringClass() == ContentSearchRequestRelationFilters.class).collect(Collectors.toMap(FeatureDescriptor::getName, pd -> pd));
    }

    @Autowired
    private Driver neo4jDriver;

    @SuppressWarnings("unchecked")
    public Map<String, Object> search(ContentSearchRequest request){

        HashMap<String, Object> query = formQuery(request);

        Session session = neo4jDriver.session();
        StatementResult result = session.readTransaction(transaction -> transaction.run(query.get("query").toString(), (Map<String, Object>) query.get("params")));

        return formatResult(result);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> formatResult(StatementResult result) {

        Map<String,Object> contentMap = new HashMap<>();

        List<Record> data = result.list();
        data.forEach(item->{
            Map<String, Object> rowData = Maps.newHashMap((Map<String, Object>)item.asMap().get("row"));
            String identifier = rowData.get("identifier").toString();

            Map<String, Object> content = (Map<String, Object>) contentMap.getOrDefault(identifier,null);

            if (null == content) {
                List<Map<String, Object>> children = Lists.newArrayList((List < Map < String, Object >> ) rowData.get("children"));
                Map<String, Object> currentChild = Maps.newHashMap(children.get(0));
                children.remove(0);
                children.add(currentChild);
                rowData.put("children", children);
                contentMap.put(identifier, rowData);
            } else {
                List<Map<String, Object>> children = (List<Map<String, Object>>) content.get("children");

                List<Map<String, Object>> currentChildren = (List<Map<String, Object>>) rowData.get("children");
                Map<String, Object> currentChild = currentChildren.get(0);

                children.add(currentChild);
            }
        });

        return contentMap;
    }

    private HashMap<String, Object> formQuery(ContentSearchRequest request){
        StringBuilder query = new StringBuilder();
        Map<String, Object> paramMap = new HashMap<>();
         //replace rootOrg with clientname/rootorg
        query.append("MATCH (n:rootOrg)-[r:Has_Sub_Content]->(n1:rootOrg)");

        boolean leftFiltersApplied = false;

        if (null != request.getLeftNode().getFilters()) {
            boolean firstFilter = true;
            for (Map.Entry<String, PropertyDescriptor> stringPropertyDescriptorEntry : propertyDescriptorsNodeFilters.entrySet()) {
                String capitalizedName = stringPropertyDescriptorEntry.getKey().substring(0, 1).toUpperCase() + stringPropertyDescriptorEntry.getKey().substring(1);
                List<Object> value = null;
                try {
                    value = (List<Object>) stringPropertyDescriptorEntry.getValue().getReadMethod().invoke(request.getLeftNode().getFilters());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (!value.isEmpty()) {
                    if (listTypeNodeFilters.contains(stringPropertyDescriptorEntry.getKey())){
                        if (firstFilter) {
                            query.append(" WHERE (any(item IN");
                            query.append(" $filterLeft").append(capitalizedName);
                            query.append(" WHERE item IN n.").append(stringPropertyDescriptorEntry.getKey()).append(")");
                            paramMap.put("filterLeft" + capitalizedName, value);
                            leftFiltersApplied = true;
                            firstFilter = false;
                        } else {
                            query.append(" AND any(item IN");
                            query.append(" $filterLeft").append(capitalizedName);
                            query.append(" WHERE item IN n.").append(stringPropertyDescriptorEntry.getKey()).append(")");
                            paramMap.put("filterLeft" + capitalizedName, value);
                        }
                    } else {
                        if (firstFilter) {
                            query.append(" WHERE (n.").append(stringPropertyDescriptorEntry.getKey());
                            query.append(" in $filterLeft").append(capitalizedName);
                            paramMap.put("filterLeft" + capitalizedName, value);
                            leftFiltersApplied = true;
                            firstFilter = false;
                        } else {
                            query.append(" AND n.").append(stringPropertyDescriptorEntry.getKey());
                            query.append(" in $filterLeft").append(capitalizedName);
                            paramMap.put("filterLeft" + capitalizedName, value);
                        }
                    }
                }
            }
            if (leftFiltersApplied)
                query.append(")");
        }

        boolean rightFiltersApplied = false;

        if (null != request.getRightNode().getFilters()) {
            boolean firstFilter = true;
            for (Map.Entry<String, PropertyDescriptor> stringPropertyDescriptorEntry : propertyDescriptorsNodeFilters.entrySet()) {
                String capitalizedName = stringPropertyDescriptorEntry.getKey().substring(0, 1).toUpperCase() + stringPropertyDescriptorEntry.getKey().substring(1);
                List<Object> value = null;
                try {
                    value = (List<Object>) stringPropertyDescriptorEntry.getValue().getReadMethod().invoke(request.getRightNode().getFilters());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (!value.isEmpty()) {
                    if (listTypeNodeFilters.contains(stringPropertyDescriptorEntry.getKey())){
                        if (firstFilter && !leftFiltersApplied) {
                            query.append(" WHERE (any(item IN");
                            query.append(" $filterRight").append(capitalizedName);
                            query.append(" WHERE item IN n.").append(stringPropertyDescriptorEntry.getKey()).append(")");
                            paramMap.put("filterRight" + capitalizedName, value);
                            rightFiltersApplied = true;
                            firstFilter = false;
                        } else if (leftFiltersApplied) {
                            if (firstFilter) {
                                query.append(" AND (any(item IN");
                                query.append(" $filterRight").append(capitalizedName);
                                query.append(" WHERE item IN n.").append(stringPropertyDescriptorEntry.getKey()).append(")");
                                paramMap.put("filterRight" + capitalizedName, value);
                                firstFilter = false;
                                rightFiltersApplied = true;
                            } else {
                                query.append(" AND any(item IN");
                                query.append(" $filterRight").append(capitalizedName);
                                query.append(" WHERE item IN n.").append(stringPropertyDescriptorEntry.getKey()).append(")");
                                paramMap.put("filterRight" + capitalizedName, value);
                            }
                        }
                    } else {
                        if (firstFilter && !leftFiltersApplied) {
                            query.append(" WHERE (n1.").append(stringPropertyDescriptorEntry.getKey());
                            query.append(" in $filterRight").append(capitalizedName);
                            paramMap.put("filterRight" + capitalizedName, value);
                            rightFiltersApplied = true;
                            firstFilter = false;
                        } else if (leftFiltersApplied) {
                            if (firstFilter) {
                                query.append(" AND (n1.").append(stringPropertyDescriptorEntry.getKey());
                                query.append(" in $filterRight").append(capitalizedName);
                                paramMap.put("filterRight" + capitalizedName, value);
                                firstFilter = false;
                                rightFiltersApplied = true;
                            } else {
                                query.append("  AND n1.").append(stringPropertyDescriptorEntry.getKey());
                                query.append(" in $filterRight").append(capitalizedName);
                                paramMap.put("filterRight" + capitalizedName, value);
                            }
                        }
                    }
                }
            }
            if (rightFiltersApplied)
                query.append(")");
        }

        boolean relationFilterApplied = false;
        if (null != request.getRelation().getFilters()) {
            boolean firstFilter = true;
            for (Map.Entry<String, PropertyDescriptor> stringPropertyDescriptorEntry : propertyDescriptorsRelationFilters.entrySet()) {
                String capitalizedName = stringPropertyDescriptorEntry.getKey().substring(0, 1).toUpperCase() + stringPropertyDescriptorEntry.getKey().substring(1);
                List<Object> value = null;
                try {
                    value = (List<Object>) stringPropertyDescriptorEntry.getValue().getReadMethod().invoke(request.getRelation().getFilters());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (!value.isEmpty()) {
                    if (!leftFiltersApplied && !rightFiltersApplied) {
                        query.append(" WHERE (r.").append(stringPropertyDescriptorEntry.getKey());
                        query.append(" in $filterRelation").append(capitalizedName);
                        paramMap.put("filterRelation" + capitalizedName, value);
                        relationFilterApplied = true;
                        firstFilter = false;
                    }
                    if (leftFiltersApplied || rightFiltersApplied) {
                        if (firstFilter) {
                            query.append(" AND (r.").append(stringPropertyDescriptorEntry.getKey());
                            query.append(" in $filterRelation").append(capitalizedName);
                            paramMap.put("filterRelation" + capitalizedName, value);
                            firstFilter = false;
                            relationFilterApplied = true;
                        } else {
                            query.append(" ,r.").append(stringPropertyDescriptorEntry.getKey());
                            query.append(" in $filterRelation").append(capitalizedName);
                            paramMap.put("filterRelation" + capitalizedName, value);
                        }
                    }
                }
            }
            if (relationFilterApplied)
                query.append(")");
        }

        query.append(" WITH n,r,n1 SKIP ").append(request.getSkip());
        query.append(" LIMIT ").append(request.getLimit());

        query.append(" RETURN {");

        for (String field : request.getLeftNode().getSourceFields()){
            query.append(field).append(": n.").append(field).append(",");
        }

        query.append(" children:[{");

        for (String field : request.getRightNode().getSourceFields()){
            query.append(field).append(": n1.").append(field).append(",");
        }

        query.append(" relationMetaData: {");

        for (String field : request.getRelation().getSourceFields()){
            query.append(field).append(": r.").append(field).append(",");
        }

        query.deleteCharAt(query.lastIndexOf(","));

        query.append("}");

        query.append("}");

        query.append("]");

        query.append("} as row");

        if (!request.getOrderBy().isEmpty()){
            boolean orderByApplied = false;
            for (ContentSearchRequestOrders contentSearchRequestOrders : request.getOrderBy()) {
                if (!(contentSearchRequestOrders.getType().isEmpty() && contentSearchRequestOrders.getField().isEmpty() && contentSearchRequestOrders.getOrder().isEmpty())) {
                    if (!orderByApplied)
                        query.append(" ORDER BY ");
                    if (contentSearchRequestOrders.getType().equals("leftNode"))
                        query.append("n.").append(contentSearchRequestOrders.getField()).append(" ").append(contentSearchRequestOrders.getOrder()).append(",");
                    if (contentSearchRequestOrders.getType().equals("rightNode"))
                        query.append("n1.").append(contentSearchRequestOrders.getField()).append(" ").append(contentSearchRequestOrders.getOrder()).append(",");
                    if (contentSearchRequestOrders.getType().equals("relation"))
                        query.append("r.").append(contentSearchRequestOrders.getField()).append(" ").append(contentSearchRequestOrders.getOrder()).append(",");
                    orderByApplied = true;
                }
            }
            if (orderByApplied)
                query.deleteCharAt(query.lastIndexOf(","));
        }

        query.append(";");

        logger.info(query.toString());

        return new HashMap<String, Object>(){{ put("query", query.toString()); put("params", paramMap); }};
    }

}
