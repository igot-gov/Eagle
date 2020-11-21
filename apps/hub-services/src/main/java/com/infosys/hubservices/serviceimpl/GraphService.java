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
import com.infosys.hubservices.util.Constants;
import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;

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
            ((Map)params.get("data")).put("status",relation);
            params.put("childData", mapper.convertValue(to, Map.class));

            String parentProperties = "{identifier:{data}.identifier, name:{data}.name, department:{data}.department}";
            String childProperties = "{identifier:{childData}.identifier, name:{childData}.name, department:{childData}.department}";
            String relProperties = "{updatedAt:{data}.updatedAt, status:{data}.status}";


            String text1 = "merge (n: user" + parentProperties + ") ";
            String text01 = "merge (n1: user" + childProperties + ") ";
            String text2 = "merge (n)-[r:" + relation + relProperties + "]->(n1) return n";
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

            String text02 = "MATCH (:user {name:{data}.name})-[r]-(:user {name:{childData}.name}) DELETE r ";
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

    @Override
    public List<Node> getNodesInEdge(String identifier, String relation, int offset, int size) throws Exception {

        Session session = neo4jDriver.session();
        Transaction transaction = session.beginTransaction();

        List<Node> nodes = new ArrayList<>();
        try {

            //String text = "MATCH (n)<-[r:"+relation+"]-(n1) WHERE n.identifier = '"+identifier+"' RETURN n1 ORDER BY updatedAt DESC Skip "+offset+ " LIMIT " +size;
            String text = "MATCH (n)<-[r:"+relation+"]-(n1) WHERE n.identifier = '"+identifier+"' RETURN n1 ORDER BY r.updatedAt DESC Skip "+offset+ " limit " +size;
            logger.info("text:: {}", text);
            Statement statement = new Statement(text);

            StatementResult result = transaction.run(statement);
            List<Record> records = result.list();
            nodes = getNodes(records);

        } catch (Exception e) {
            transaction.rollbackAsync().toCompletableFuture().get();
            logger.error("Fetching user node failed : " ,e);

        } finally {
            transaction.close();
            session.close();
        }
        return nodes;
    }


    @Override
    public List<Node> getNodesOutEdge(String identifier, String relation, int offset, int size) throws Exception {

        Session session = neo4jDriver.session();
        Transaction transaction = session.beginTransaction();

        List<Node> nodes = new ArrayList<>();
        try {

            //String text = "MATCH (n)<-[r:"+relation+"]-(n1) WHERE n.identifier = '"+identifier+"' RETURN n1 ORDER BY updatedAt DESC Skip "+offset+ " LIMIT " +size;
            String text = "MATCH (n)-[r:"+relation+"]->(n1) WHERE n.identifier = '"+identifier+"' RETURN n1 ORDER BY r.updatedAt DESC Skip "+offset+ " limit " +size;
            logger.info("text:: {}", text);
            Statement statement = new Statement(text);

            StatementResult result = transaction.run(statement);
            List<Record> records = result.list();

            nodes = getNodes(records);

        } catch (Exception e) {
            transaction.rollbackAsync().toCompletableFuture().get();
            logger.error("Fetching user node failed : " ,e);

        } finally {
            transaction.close();
            session.close();
        }
        return nodes;
    }

    @Override
    public List<Node> getNodesInAndOutEdge(String identifier, String relation, int offset, int size) throws Exception{
        Session session = neo4jDriver.session();
        Transaction transaction = session.beginTransaction();

        List<Node> nodes = new ArrayList<>();
        try {

            //String text = "MATCH (n)<-[r:"+relation+"]-(n1) WHERE n.identifier = '"+identifier+"' RETURN n1 ORDER BY updatedAt DESC Skip "+offset+ " LIMIT " +size;
            String text = "MATCH (n)-[r:"+relation+"]-(n1) WHERE n.identifier = '"+identifier+"' RETURN n1 ORDER BY r.updatedAt DESC Skip "+offset+ " limit " +size;
            logger.info("text:: {}", text);
            Statement statement = new Statement(text);

            StatementResult result = transaction.run(statement);
            List<Record> records = result.list();

            nodes = getNodes(records);

        } catch (Exception e) {
            transaction.rollbackAsync().toCompletableFuture().get();
            logger.error("Fetching user node failed : " ,e);

        } finally {
            transaction.close();
            session.close();
        }
        return nodes;
    }

    @Override
    public List<Node> getNodesNextLevel(String identifier, String relation, int offset, int size) throws Exception{
        Session session = neo4jDriver.session();
        Transaction transaction = session.beginTransaction();

        List<Node> nodes = new ArrayList<>();
        try {

            //TODO generic for any level
            //String text = "MATCH (n)<-[r:"+relation+"]-(n1) WHERE n.identifier = '"+identifier+"' RETURN n1 ORDER BY r1.updatedAt DESC Skip "+offset+ " LIMIT " +size;
            String text = "MATCH (n)-[r:"+relation+"]-(n0)-[r1:"+relation+"]-(n1) WHERE n.identifier = '"+identifier+"' RETURN n1  Skip "+offset+ " limit " +size;
            logger.info("text:: {}", text);
            Statement statement = new Statement(text);

            StatementResult result = transaction.run(statement);
            List<Record> records = result.list();

            nodes = getNodes(records);

        } catch (Exception e) {
            transaction.rollbackAsync().toCompletableFuture().get();
            logger.error("Fetching user node failed : " ,e);

        } finally {
            transaction.close();
            session.close();
        }
        return nodes;
    }

    private List<Node> getNodes(List<Record> records){

        List<Node> nodes = new ArrayList<>();

        if (records.size() > 0) {
            logger.info("{} User node fetched.",records.size());
            for (Record record : records) {
                org.neo4j.driver.v1.types.Node node = record.get("n1").asNode();
                //TODO: optimise
                Node nodePojo = new Node(node.get("identifier").asString(), node.get("name").asString(), node.get("department").asString());
                //Date d = Date.from(node.get("updatedAt").asLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                //nodePojo.setUpdatedAt(d);
                logger.info("########### nodePojo fetched {}",nodePojo);

                nodes.add(nodePojo);

            }
        }
        return nodes;
    }

    @Override
    public int getAllNodeCount(String identifier, String relation, Constants.DIRECTION direction) throws Exception {
        Session session = neo4jDriver.session();
        Transaction transaction = session.beginTransaction();
        int count =0;
        String text = null;
        try {

            if(direction == null) {
             text = "MATCH (n)-[r:"+relation+"]-(n1) WHERE n.identifier = '"+identifier+"' RETURN count(r) ";

            } else if(direction.equals(Constants.DIRECTION.IN)){
                text = "MATCH (n)<-[r:"+relation+"]-(n1) WHERE n.identifier = '"+identifier+"' RETURN count(r) ";

            } else if(direction.equals(Constants.DIRECTION.OUT)){
                text = "MATCH (n)-[r:"+relation+"]->(n1) WHERE n.identifier = '"+identifier+"' RETURN count(r) ";

            }
            //String text = "MATCH (n)<-[r:"+relation+"]-(n1) WHERE n.identifier = '"+identifier+"' RETURN n1 ORDER BY updatedAt DESC Skip "+offset+ " LIMIT " +size;
            logger.info("text:: {}", text);
            Statement statement = new Statement(text);

            StatementResult result = transaction.run(statement);
            List<Record> records = result.list();

            if (records.size() > 0) {
                logger.info("{} User node fetched.",records.size());
                for (Record record : records) {
                    count = record.get("count(r)").asInt();


                }
            }

        } catch (Exception e) {
            transaction.rollbackAsync().toCompletableFuture().get();
            logger.error("Fetching user node failed : " ,e);

        } finally {
            transaction.close();
            session.close();
        }

        return count;
    }

    @Override
    public List<Node> getAllNodes(String identifier) throws Exception {
        Session session = neo4jDriver.session();
        Transaction transaction = session.beginTransaction();
        List<Node> nodes =new ArrayList<>();

        try {

            //String text = "MATCH (n)<-[r:"+relation+"]-(n1) WHERE n.identifier = '"+identifier+"' RETURN n1 ORDER BY updatedAt DESC Skip "+offset+ " LIMIT " +size;
            String text = "MATCH (n)-[r]-(n1) WHERE n.identifier = '"+identifier+"' RETURN n1 ";
            logger.info("text:: {}", text);
            Statement statement = new Statement(text);

            StatementResult result = transaction.run(statement);
            List<Record> records = result.list();

            nodes =getNodes(records);
        } catch (Exception e) {
            transaction.rollbackAsync().toCompletableFuture().get();
            logger.error("Fetching user node failed : " ,e);

        } finally {
            transaction.close();
            session.close();
        }

        return nodes;
    }

}
