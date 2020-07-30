package com.infosys.lex.ext.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;

import com.infosys.lex.ext.exception.BadRequestException;
import com.infosys.lex.ext.service.BatchJobsService;
import com.infosys.lex.ext.util.LexConstants;

@RestController
public class BatchJobsController {

	@Autowired
	BatchJobsService batchJobsService;

	@PostMapping("v1/batch-jobs/progress/external")
	ResponseEntity<Response> externalProgressImport(@RequestHeader("rootOrg") String rootOrg,
			@RequestBody Map<String, Object> requestBody) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.batch-job.progressimport");
		response.setTs(ProjectUtil.getFormattedDate());

		try {
			String resultMap = batchJobsService.externalProgressImport(rootOrg, requestBody);
			response.put(LexConstants.DATA_LIST, resultMap);
		} catch (BadRequestException badRequestException) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.CLIENT_ERROR.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, badRequestException.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception exception) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.internalError.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, exception.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("v1/batch-jobs/progress/users/{user_id}/assessment/{content_id}/recalculate")
	ResponseEntity<Response> recalculateProgress(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("user_id") String userId, @PathVariable("content_id") String contentId,
			@RequestBody Map<String, Object> requestBody) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.batch-job.assessment-recalculate");
		response.setTs(ProjectUtil.getFormattedDate());

		try {
			String resultMap = batchJobsService.recalculateProgress(rootOrg, userId, contentId, requestBody);
			response.put(LexConstants.DATA_LIST, resultMap);
		} catch (BadRequestException badRequestException) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.CLIENT_ERROR.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, badRequestException.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception exception) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.internalError.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, exception.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("v1/batch-jobs/notification/{notification_type}")
	ResponseEntity<Response> sendNotification(@RequestHeader("rootOrg") String rootOrg,
			@PathVariable("notification_type") String notificationType, @RequestBody Map<String, Object> requestBody) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.batch-job.notification-service");
		response.setTs(ProjectUtil.getFormattedDate());

		try {
			batchJobsService.sendNotification(rootOrg, notificationType, requestBody);
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.OK.getErrorCode());
			response.setResponseCode(responseCode);
		} catch (BadRequestException badRequestException) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.CLIENT_ERROR.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, badRequestException.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception exception) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.internalError.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, exception.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
