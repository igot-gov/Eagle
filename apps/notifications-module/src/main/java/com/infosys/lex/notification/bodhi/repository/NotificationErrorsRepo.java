package com.infosys.lex.notification.bodhi.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.notification.model.cassandra.NotificationErrors;
import com.infosys.lex.notification.model.cassandra.NotificationErrorsPrimaryKey;

@Repository
public interface NotificationErrorsRepo extends CassandraRepository<NotificationErrors, NotificationErrorsPrimaryKey> {

}
