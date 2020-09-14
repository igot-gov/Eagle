package com.infosys.lex.ext.Models.Cassandra;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class SharedPlayListsPrimaryKey implements Serializable {
	private static final long serialVersionUID = 1L;
	@PrimaryKeyColumn(name = "shared_with", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String sharedWith;
	@PrimaryKeyColumn(name = "playlist_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
	private String playListId;

}
