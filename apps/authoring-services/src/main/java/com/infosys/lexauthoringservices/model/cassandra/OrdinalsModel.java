/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"*/
/**
 * © 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved.
 * Version: 1.10
 * <p>
 * Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
 * this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
 * the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
 * by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of
 * this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
 * under the law.
 * <p>
 * Highly Confidential
 */


package com.infosys.lexauthoringservices.model.cassandra;

import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("master_values_v2")
public class OrdinalsModel {

	@Column("date_modified")
	private Date dateModified;
	
	@Column("values")
	private List<String> values;
	
	@Column("strignify")
	Boolean stringify;
	
	@PrimaryKey
	OrdinalsPrimaryKeyModel ordinalsPrimaryKeyModel;

	public OrdinalsModel() {
		super();
	}

	public OrdinalsModel(Date dateModified, List<String> values, Boolean stringify,
			OrdinalsPrimaryKeyModel ordinalsPrimaryKeyModel) {
		super();
		this.dateModified = dateModified;
		this.values = values;
		this.stringify = stringify;
		this.ordinalsPrimaryKeyModel = ordinalsPrimaryKeyModel;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Boolean getStringify() {
		return stringify;
	}

	public void setStringify(Boolean stringify) {
		this.stringify = stringify;
	}

	public OrdinalsPrimaryKeyModel getOrdinalsPrimaryKeyModel() {
		return ordinalsPrimaryKeyModel;
	}

	public void setOrdinalsPrimaryKeyModel(OrdinalsPrimaryKeyModel ordinalsPrimaryKeyModel) {
		this.ordinalsPrimaryKeyModel = ordinalsPrimaryKeyModel;
	}
	
	
}
