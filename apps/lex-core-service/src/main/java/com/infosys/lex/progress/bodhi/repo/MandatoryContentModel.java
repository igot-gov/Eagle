package com.infosys.lex.progress.bodhi.repo;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("mandatory_user_content")
public class MandatoryContentModel {

    @PrimaryKey
    private MandatoryContentPrimaryKeyModel primaryKey;

    @Column("content_type")
    private String contentType;

    public MandatoryContentPrimaryKeyModel getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(MandatoryContentPrimaryKeyModel primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}