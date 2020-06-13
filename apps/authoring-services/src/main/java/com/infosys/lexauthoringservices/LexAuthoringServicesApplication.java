package com.infosys.lexauthoringservices;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class LexAuthoringServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(LexAuthoringServicesApplication.class, args);
    }

    /**
     * Initializes the rest template
     *
     * @return
     * @throws Exception
     */
    @Bean
    public RestTemplate restTemplate() throws Exception {
        return new RestTemplate();
    }

//    private ClientHttpRequestFactory getClientHttpRequestFactory() {
//
//        int timeout = 5000;
//
//        RequestConfig config = RequestConfig.custom()
//                .setConnectTimeout(timeout)
//                .setConnectionRequestTimeout(timeout)
//                .setSocketTimeout(timeout)
//                .build();
//
//        CloseableHttpClient client = HttpClientBuilder.create()
//                .setDefaultRequestConfig(config)
//                .build();
//
//        return new HttpComponentsClientHttpRequestFactory(client);
//    }

}
