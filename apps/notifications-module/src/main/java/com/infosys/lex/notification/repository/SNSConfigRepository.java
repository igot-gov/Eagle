package com.infosys.lex.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infosys.lex.notification.entity.SNSNotificationConfig;

@Repository
public interface SNSConfigRepository extends JpaRepository<SNSNotificationConfig, String>{

}
