package com.infosys.lexauthoringservices.service;

import com.infosys.lexauthoringservices.model.Catalog;

public interface CatalogService {


     static enum Constants {
        NODE("n"), NODEID("nodeId"), IDENTIFIER("identifier"), LEVEL("level"), TYPE("type")
        , NAME("name"),RELATION_LABEL("IS_PARENT_OF");

        private String value;

        private Constants(String value) { this.value = value; }
        public String getValue() {
            return this.value;
        }
    }



    /**
     * Add catalog nodes to existing tree or adding a new tree
     * @param catalog
     * @param rootOrg
     * @param locale
     * @param level
     * @param count
     * @return
     * @throws Exception
     */
    Boolean addCatalogTree(Catalog catalog, String rootOrg, String locale, int level, int count) throws Exception;

    /**
     * Delete a node and with it associated relationship nodes
     * @param catalog
     * @param rootOrg
     * @throws Exception
     */
    void deleteCatalogTree(Catalog catalog, String rootOrg) throws Exception;
}
