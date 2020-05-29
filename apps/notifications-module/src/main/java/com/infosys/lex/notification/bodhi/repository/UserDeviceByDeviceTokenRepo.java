package com.infosys.lex.notification.bodhi.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.notification.model.cassandra.UserNotificationDeviceByDeviceToken;
import com.infosys.lex.notification.model.cassandra.UserNotificationDeviceByDeviceTokenKey;
import com.infosys.lex.notification.projection.UserDeviceArnsProjection;

@Repository
public interface UserDeviceByDeviceTokenRepo extends CassandraRepository<UserNotificationDeviceByDeviceToken, UserNotificationDeviceByDeviceTokenKey>{

	public UserDeviceArnsProjection findByKeyDeviceToken(String deviceToken);
}
