package com.infosys.lexauthoringservices.repository.cassandra.bodhi;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lexauthoringservices.model.cassandra.MasterValues;

@Repository
public interface MasterValuesRepository extends CassandraRepository<MasterValues, String> {

}