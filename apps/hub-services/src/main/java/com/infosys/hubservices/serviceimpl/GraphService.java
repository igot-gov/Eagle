/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.hubservices.model.Node;
import com.infosys.hubservices.service.IGraphService;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GraphService implements IGraphService {

    private Logger logger = LoggerFactory.getLogger(GraphService.class);

    @Autowired
    Driver neo4jDriver;

    @Autowired
    private ObjectMapper mapper;

    @Override
    public Boolean createNodeWithRelation(Node from, Node to, String relation) throws Exception{

        Session session = neo4jDriver.session();
        Transaction transaction = session.beginTransaction();
        Map<String, Object> params = new HashMap<>();

        try {

            params.put("data", mapper.convertValue(from, Map.class));
            params.put("childData", mapper.convertValue(to, Map.class));

            String parentProperties = "{identifier:{data}.identifier, name:{data}.name, department:{data}.department}";
            String childProperties = "{identifier:{childData}.identifier, name:{childData}.name, department:{childData}.department}";

            String text1 = "merge (n: user" + parentProperties + ") ";
            String text01 = "merge (n1: user" + childProperties + ") ";
            String text2 = "merge (n)-[r:" + relation + "]->(n1) return n";
            Statement statement = new Statement(text1 + text01 + text2, params);
            logger.info("Merge Cypher query:: " + statement.text());

            StatementResult result = transaction.run(statement);
            List<Record> records = result.list();

            if (records == null || records.isEmpty()) {
                throw new Exception("Something went wrong");
            }
            transaction.commitAsync().toCompletableFuture().get();
            logger.info("user node with relation created successfully " );

        } catch (Exception e) {
            transaction.rollbackAsync().toCompletableFuture().get();
            logger.error("user node creation failed : " ,e);
            return Boolean.FALSE;

        } finally {
            transaction.close();
            session.close();
        }


        return Boolean.TRUE;
    }

    @Override
    public Boolean deleteRelation(Node from, Node to, String relation) throws Exception{
        Session session = neo4jDriver.session();
        Transaction transaction = session.beginTransaction();
        Map<String, Object> params = new HashMap<>();

        try {

            params.put("data", mapper.convertValue(from, Map.class));
            params.put("childData", mapper.convertValue(to, Map.class));

            //TODO: run in single transaction
            String text02 = "MATCH (:user {name:{data}.name})-[r:"+relation+"]-(:user {name:{childData}.name}) DELETE r ";
            Statement statement1 = new Statement(text02, params);
            transaction.run(statement1);
            transaction.commitAsync().toCompletableFuture().get();
            logger.info("user node relation deleted successfully " );


        } catch (Exception e) {
            transaction.rollbackAsync().toCompletableFuture().get();
            logger.error("user delete relation failed : {}", e);
            return Boolean.FALSE;
        } finally {
            transaction.close();
            session.close();
        }
        return Boolean.TRUE;
    }
}
