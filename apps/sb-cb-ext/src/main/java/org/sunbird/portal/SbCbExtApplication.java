package org.sunbird.portal;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@ComponentScan(basePackages = "org.sunbird")
@SpringBootApplication
public class SbCbExtApplication {

	/**
	 * Runs The application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(SbCbExtApplication.class, args);
	}
	
	/**
	 * Initializes the rest template
	 * 
	 * @return
	 * @throws Exception
	 */

	@Bean
	public RestTemplate restTemplate() throws Exception {

		return new RestTemplate(getClientHttpRequestFactory());
	}

	private ClientHttpRequestFactory getClientHttpRequestFactory() {

		int timeout = 5000;
              RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                           .setSocketTimeout(timeout).build();
              CloseableHttpClient client = HttpClientBuilder.create().setMaxConnTotal(2000).setMaxConnPerRoute(500)
                           .setDefaultRequestConfig(config).build();
              return new HttpComponentsClientHttpRequestFactory(client);
	}
}
