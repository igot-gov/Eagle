package com.infosys.lex.ext.serviceImpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.sunbird.common.models.util.ProjectLogger;

import com.infosys.lex.ext.exception.BadRequestException;
import com.infosys.lex.ext.service.BatchJobsService;
import com.infosys.lex.ext.util.LexServerProperties;

@Service
public class BatchJobsServiceImpl implements BatchJobsService {

	@Autowired
	LexServerProperties lexServerProperties;

	@Autowired
	RestTemplate restTemplate;

	@Override
	public String externalProgressImport(String rootOrg, Map<String, Object> requestBody) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("rootOrg", rootOrg);

		HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
		String url = "http://" + lexServerProperties.getLexCoreSerivce() + "/v1/progress/external";
		ResponseEntity<String> responseEntity;
		try {
			responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			return responseEntity.getBody();
		} catch (HttpServerErrorException httpServerErrorException) {
			ProjectLogger.log("Error : " + httpServerErrorException.getMessage(), httpServerErrorException);
			throw new BadRequestException("Progress Rest Call Exception");
		} catch (HttpClientErrorException clientError) {
			throw new BadRequestException("invalid.data");
		}
	}

	@Override
	public String recalculateProgress(String rootOrg, String userId, String contentId,
			Map<String, Object> requestBody) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("rootOrg", rootOrg);
		headers.add("Content-Type", "application/json");

		HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
		String url = "http://" + lexServerProperties.getLexCoreSerivce() + "/v1/users/" + userId + "/assessment/"
				+ contentId + "/progress/recalculate";
		ResponseEntity<String> responseEntity;
		try {

			responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			return responseEntity.getBody();
		} catch (HttpServerErrorException httpServerErrorException) {
			ProjectLogger.log("Error : " + httpServerErrorException.getMessage(), httpServerErrorException);
			throw new BadRequestException("Progress Rest Call Exception");
		} catch (HttpClientErrorException clientError) {
			throw new BadRequestException("invalid.data");
		}
	}

	@Override
	public void sendNotification(String rootOrg, String notificationType, Map<String, Object> requestBody) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("rootOrg", rootOrg);

		HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
		String url = "http://" + lexServerProperties.getNotificationService() + "/v1/notification/" + notificationType;

		try {
			restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
		} catch (HttpServerErrorException httpServerErrorException) {
			ProjectLogger.log("Error : " + httpServerErrorException.getMessage(), httpServerErrorException);
			throw new BadRequestException("Notification Rest Call Exception");
		} catch (HttpClientErrorException clientError) {
			throw new BadRequestException("invalid.data");
		}
	}

}
