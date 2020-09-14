package com.infosys.lex.ext.Models.Cassandra;

import java.util.List;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("api_authentication")
public class AuthorizeData {

	@PrimaryKey
	@Column("key")
	private String key;

	@Column("name")
	private String name;

	@Column("value")
	private String value;

	@Column("urls")
	private List<String> urls;

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public AuthorizeData(String key, String name, String value, List<String> urls) {
		super();
		this.key = key;
		this.name = name;
		this.value = value;
		this.urls = urls;
	}

	public AuthorizeData() {
	}

}
