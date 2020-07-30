package com.infosys.lex.ext;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableScheduling
public class BodhiApplication {

	public static void main(String[] args) {
		Properties props = System.getProperties();
		props.setProperty("es.set.netty.runtime.available.processors", "false");
		SpringApplication.run(BodhiApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
