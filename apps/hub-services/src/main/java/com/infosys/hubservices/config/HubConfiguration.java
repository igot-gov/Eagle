/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@EnableAsync
public class HubConfiguration {

	@Value("${taskExecutor.connection.threadPoolName}")
	private String connectionThreadName;

	@Value("${taskExecutor.connection.corePoolSize}")
	private int connectionCorePoolSize;

	@Value("${taskExecutor.connection.maxPoolSize}")
	private int connectionMaxPoolSize;

	@Value("${taskExecutor.connection.queueCapacity}")
	private int connectionQueueCapacity;

	@Bean(name = "connectionExecutor")
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(connectionCorePoolSize);
		executor.setMaxPoolSize(connectionMaxPoolSize);
		executor.setQueueCapacity(connectionQueueCapacity);
		executor.setThreadNamePrefix(connectionThreadName);
		executor.initialize();
		return executor;
	}

}
