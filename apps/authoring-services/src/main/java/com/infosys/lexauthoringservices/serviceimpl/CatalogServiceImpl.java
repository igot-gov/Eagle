package com.infosys.lexauthoringservices.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.model.Catalog;
import com.infosys.lexauthoringservices.model.Properties;
import com.infosys.lexauthoringservices.service.CatalogService;
import com.infosys.lexauthoringservices.util.LexLogger;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private LexLogger logger;

    @Autowired
    Driver neo4jDriver;

    @Autowired
    ObjectMapper mapper;

    private Boolean existflag = Boolean.FALSE;

    @Override
    public Boolean addCatalogTree(Catalog catalog, String rootOrg, String locale, int level, int count) throws Exception {

        catalog.getProperties().setRootOrg(rootOrg);
        catalog.getProperties().setPrimaryLanguage(locale);
        setDefaultProperties(catalog, level, count);
        level = level + 1;

        for (Catalog childCatalog : catalog.getChild()) {
            childCatalog.getProperties().setRootOrg(rootOrg);
            childCatalog.getProperties().setPrimaryLanguage(locale);
            count = count + 1;
            setDefaultProperties(childCatalog, level, count);
            existflag = createNodeWithRelation(catalog.getType(), catalog.getProperties(), childCatalog, Constants.RELATION_LABEL.getValue());
            addCatalogTree(childCatalog, rootOrg, locale, level, count);

        }
        return existflag;
    }

    @Override
    public void deleteCatalogTree(Catalog catalog, String rootOrg) throws Exception {

        deleteNodeWithRelation(catalog, rootOrg);
        for (Catalog childCatalog : catalog.getChild()) {
            deleteCatalogTree(childCatalog, rootOrg);
        }


    }

    /**
     * Delete a node with its associated child
     * @param catalog
     * @param rootOrg
     * @throws Exception
     */
    private void deleteNodeWithRelation(Catalog catalog, String rootOrg) throws Exception {

        Session session = neo4jDriver.session();
        Transaction transaction = session.beginTransaction();
        try {
            String text = "match (n:" + catalog.getType() + ")-[*0..]->(x) where  n.name='" + catalog.getName() + "' and n.rootOrg='" + rootOrg + "' detach delete n";
            Statement statement = new Statement(text);
            logger.info("delete statement: " + statement.text());
            transaction.run(statement);
            transaction.commitAsync().toCompletableFuture().get();

        } catch (Exception e) {
            transaction.rollbackAsync().toCompletableFuture().get();
            logger.error("Catalog node deletion failed : " + catalog.getName());
            throw e;
        } finally {
            transaction.close();
            session.close();
        }

    }


    /**
     * Creates catalog node tree with child immediate node
     * @param type
     * @param properties
     * @param childCatalog
     * @param relation
     * @return
     * @throws Exception
     */
    private Boolean createNodeWithRelation(String type, Properties properties, Catalog childCatalog, String relation) throws Exception {

        Session session = neo4jDriver.session();
        Transaction transaction = session.beginTransaction();
        Map<String, Object> params = new HashMap<>();
        boolean creatorFlag = Boolean.FALSE;

        try {
            //update the properties from existing node
            creatorFlag = updatePropertiesForNodeExist(type, properties, transaction);
            creatorFlag = updatePropertiesForNodeExist(childCatalog.getType(), childCatalog.getProperties(), transaction);

            params.put("data", mapper.convertValue(properties, Map.class));
            params.put("childData", mapper.convertValue(childCatalog.getProperties(), Map.class));

            String parentProperties = "{identifier:{data}.id, primaryLanguage:{data}.primaryLanguage, name:{data}.name, rootOrg:{data}.rootOrg, level:{data}.level, nodeId:{data}.nodeId,en:{data}.en}";
            String text1 = "merge (n:" + type + parentProperties + ") ";
            String childProperties = "{identifier:{childData}.id, primaryLanguage:{data}.primaryLanguage, name:{childData}.name, rootOrg:{childData}.rootOrg, level:{childData}.level, nodeId:{childData}.nodeId,en:{childData}.en}";
            String text2 = "merge (n)-[:" + relation + "]->(n1:" + childCatalog.getType() + childProperties + ") return n";
            Statement statement = new Statement(text1 + text2, params);
            logger.info("Merge Cypher query:: " + statement.text());

            StatementResult result = transaction.run(statement);
            List<Record> records = result.list();

            if (records == null || records.isEmpty()) {
                throw new Exception("Something went wrong");
            }
            transaction.commitAsync().toCompletableFuture().get();
            logger.info("Catalog node with relation created successfully: " + properties.getName());


        } catch (Exception e) {
            transaction.rollbackAsync().toCompletableFuture().get();
            logger.error("Catalog node creation failed : " + properties.getName());
            throw e;
        } finally {
            transaction.close();
            session.close();
        }
        return creatorFlag;
    }

    /**
     * Sets properties for a catalog node
     * @param catalog
     * @param level
     * @param nodeCount
     */
    private void setDefaultProperties(Catalog catalog, int level, int nodeCount) {
        if (catalog.getProperties().getId() == null || catalog.getProperties().getId().isEmpty()) {
            catalog.getProperties().setId(UUID.randomUUID().toString());
            catalog.getProperties().setEn(catalog.getName());
            catalog.getProperties().setName(catalog.getName());
            catalog.getProperties().setLevel(level);
            catalog.getProperties().setNodeId(nodeCount);

        }
    }

    /**
     * For a type exist as node to update it properties after fetching the node
     * @param type
     * @param properties
     * @param transaction
     * @return
     */
    private Boolean updatePropertiesForNodeExist(String type, Properties properties, Transaction transaction) {

        String text = "match (n:" + type + ") where  n.name='" + properties.getName() + "' and n.rootOrg='" + properties.getRootOrg() + "' return n";
        Statement statement = new Statement(text);

        StatementResult result = transaction.run(statement);
        List<Record> records = result.list();
        int size = records.size();

        if (size == 0) {
            return Boolean.FALSE;
        } else {
            logger.info("Catalog node exists: " + properties.getName());
            for (Record record : records) {
                Node node = record.get(Constants.NODE.getValue()).asNode();
                properties.setNodeId(node.get(Constants.NODEID.getValue()).asInt());
                properties.setId(node.get(Constants.IDENTIFIER.getValue()).asString());
                properties.setLevel(node.get(Constants.LEVEL.getValue()).asInt());

            }
            return Boolean.TRUE;

        }
    }


}
