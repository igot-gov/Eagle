package com.infosys.lex.ext.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lex.ext.exception.ApplicationLogicError;
import com.infosys.lex.ext.exception.BadRequestException;
import com.infosys.lex.ext.service.LAService;
import com.infosys.lex.ext.util.LexConstants;

@RestController
public class LAController {

	@Autowired
	LAService laService;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("v1/la/social/posts")
	public ResponseEntity<Response> getPosts(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.DEFAULT_VALUE_MIN, required = false, name = LexConstants.START_DATE_TIME) String startDateTime,
			@RequestParam(defaultValue = LexConstants.DEFAULT_VALUE_MIN, required = false, name = LexConstants.END_DATE_TIME) String endDateTime,
			@RequestParam(defaultValue = LexConstants.DEFAULT_VALUE_MIN, required = false, name = LexConstants.RECORD_LINK) String recordLink,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(defaultValue = LexConstants.DEFAULT_SOCIAL_POST_STATUS, required = false, name = LexConstants.SOCIAL_POST_STATUS) String socialPostStatus) {
		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.socialPosts");
		response.setTs(ProjectUtil.getFormattedDate());

		try {
			List<String> postStatus = Arrays.asList(socialPostStatus.split(","));
			Map<String, Object> resultMap = laService.getSocialPosts(rootOrg, recordLink, startDateTime, endDateTime,
					pageSize, postStatus);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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

	@GetMapping("v1/la/tags")
	public ResponseEntity<Response> tags() {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.tags");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			response.put(LexConstants.DATA_LIST, laService.getTags().get(LexConstants.RESULT));
		} catch (Exception exception) {
			exception.printStackTrace();
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.internalError.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, exception.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/lex/userProgress")
	public ResponseEntity<Response> userProgress(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(defaultValue = LexConstants.DEFAULT_START_DATE, required = false, name = LexConstants.START_DATE) String startDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_END_DATE, required = false, name = LexConstants.END_DATE) String endDate) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.userProgress");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getUserProgress(rootOrg, pagingSequence, pageSize, startDate,
					endDate);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/la/feedback")
	public ResponseEntity<Response> feedBack(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(defaultValue = LexConstants.DEFAULT_START_DATE_TIME, required = false, name = LexConstants.START_DATE_TIME) String startDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_END_DATE_TIME, required = false, name = LexConstants.END_DATE_TIME) String endDate) {

		Response response = new Response();
		try {
			response.setVer("v1");
			response.setId("api.lex.feedback");
			response.setTs(ProjectUtil.getFormattedDate());
			Map<String, Object> resultMap = laService.getFeedBack(rootOrg, pagingSequence, pageSize, startDate,
					endDate);
			response.put(LexConstants.DATA_LIST, resultMap.get("hits"));
			response.put(LexConstants.NEXT_RECORD, resultMap.get("scrollId"));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v2/la/catalog")
	public ResponseEntity<Response> catalogv2(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(required = false, name = LexConstants.IDENTIFIER) String identifier) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.catalog");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getCatalogv2(rootOrg, pagingSequence, pageSize, identifier);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/infytq/catalog")
	public ResponseEntity<Response> courseCatalog(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.catalog");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getCourseCatalog(rootOrg, pagingSequence, pageSize);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/la/userassessments")
	public ResponseEntity<Response> assessments(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(defaultValue = LexConstants.DEFAULT_START_DATE_TIME, required = false, name = LexConstants.START_DATE_TIME) String startDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_END_DATE_TIME, required = false, name = LexConstants.END_DATE_TIME) String endDate) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.userassessments");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getAssessmentDetails(rootOrg, pageSize, pagingSequence, startDate,
					endDate);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/la/sharedplaylist")
	public ResponseEntity<Response> sharedPlaylists(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(defaultValue = LexConstants.DEFAULT_START_DATE_TIME, required = false, name = LexConstants.START_DATE_TIME) String startDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_END_DATE_TIME, required = false, name = LexConstants.END_DATE_TIME) String endDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_VALUE_INCREMENTAL_OR_FULL_DUMP, required = false, name = LexConstants.DUMP_TYPE) String dumpType) {
		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.sharedplaylist");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getSharedPlayLists(rootOrg, pageSize, pagingSequence, startDate,
					endDate, dumpType);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/la/userplaylist")
	public ResponseEntity<Response> userPlayLists(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(defaultValue = LexConstants.DEFAULT_START_DATE_TIME, required = false, name = LexConstants.START_DATE_TIME) String startDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_END_DATE_TIME, required = false, name = LexConstants.END_DATE_TIME) String endDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_VALUE_INCREMENTAL_OR_FULL_DUMP, required = false, name = LexConstants.DUMP_TYPE) String dumpType) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.userplaylist");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getPlayList(rootOrg, pageSize, pagingSequence, startDate, endDate,
					dumpType);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/la/usersharedgoals")
	public ResponseEntity<Response> usersharedGoals(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(defaultValue = LexConstants.DEFAULT_START_DATE_TIME, required = false, name = LexConstants.START_DATE_TIME) String startDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_END_DATE_TIME, required = false, name = LexConstants.END_DATE_TIME) String endDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_VALUE_INCREMENTAL_OR_FULL_DUMP, required = false, name = LexConstants.DUMP_TYPE) String dumpType) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.usersharedgoals");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getUserSharedGoals(rootOrg, pageSize, pagingSequence, startDate,
					endDate, dumpType);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/la/userlearninggoals")
	public ResponseEntity<Response> userLearningGoals(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(defaultValue = LexConstants.DEFAULT_START_DATE_TIME, required = false, name = LexConstants.START_DATE_TIME) String startDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_END_DATE_TIME, required = false, name = LexConstants.END_DATE_TIME) String endDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_VALUE_INCREMENTAL_OR_FULL_DUMP, required = false, name = LexConstants.DUMP_TYPE) String dumpType) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.userlearninggoals");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getUserLearningGoals(rootOrg, pageSize, pagingSequence, startDate,
					endDate, dumpType);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	// will come from api
	@GetMapping("v1/la/commonlearninggoals")
	public ResponseEntity<Response> commonLearningGoals(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.commonlearninggoals");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getCommonLearningGoals(rootOrg);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/la/masterinterests")
	public ResponseEntity<Response> masterInterests(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.masterinterests");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getMasterInterests(rootOrg);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/la/userinterests")
	public ResponseEntity<Response> interests(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(defaultValue = LexConstants.DEFAULT_START_DATE_TIME, required = false, name = LexConstants.START_DATE_TIME) String startDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_END_DATE_TIME, required = false, name = LexConstants.END_DATE_TIME) String endDate) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.userinterests");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getInterests(rootOrg, pageSize, pagingSequence, startDate,
					endDate);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/la/badges")
	public ResponseEntity<Response> badges(@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(defaultValue = LexConstants.DEFAULT_START_DATE_TIME, required = false, name = LexConstants.START_DATE_TIME) String startDate,
			@RequestParam(defaultValue = LexConstants.DEFAULT_END_DATE_TIME, required = false, name = LexConstants.END_DATE_TIME) String endDate) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.badges");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getBadges(rootOrg, pageSize, pagingSequence, startDate, endDate);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/la/leaderboard")
	public ResponseEntity<Response> leaderboard(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize,
			@RequestParam(name = LexConstants.LEADERBOARD_YEAR) Integer leaderBoardYear,
			@RequestParam(name = LexConstants.LEADERBOARD_DURATION_TYPE) String durationType,
			@RequestParam(name = LexConstants.LEADERBOARD_DURATION_VALUE) Integer durationValue,
			@RequestParam(name = LexConstants.LEADER_BOARD_TYPE) String leaderBoardType) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.leaderboard");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getLeaderBoardsData(rootOrg, pageSize, pagingSequence,
					leaderBoardYear, durationType, durationValue, leaderBoardType);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	@GetMapping("v1/la/usertnc")
	public ResponseEntity<Response> userTnc(
			@RequestHeader(required = true, name = LexConstants.ROOT_ORG) String rootOrg,
			@RequestParam(defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE, required = false, name = LexConstants.RECORD_LINK) String pagingSequence,
			@RequestParam(defaultValue = LexConstants.DEFAULT_PAGE_SIZE_VALUE_LA, required = false, name = LexConstants.PAGE_SIZE) String pageSize) {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.user_terms_conditions");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> resultMap = laService.getUserTermsAndCondtion(rootOrg, pageSize, pagingSequence);
			response.put(LexConstants.DATA_LIST, resultMap.get(LexConstants.RESULT));
			response.put(LexConstants.NEXT_RECORD, resultMap.get(LexConstants.NEXT_RECORD_STATE));
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
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	/**
	 * This method fetches all the assignments by date
	 *
	 * @param rootOrg
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@GetMapping("/v1/la/content-assignments")
	public ResponseEntity<Response> getContentAssignments(@RequestHeader("rootOrg") String rootOrg,
			@RequestParam(name = LexConstants.START_DATE_TIME, required = false, defaultValue = LexConstants.DEFAULT_START_DATE_TIME) String startDate,
			@RequestParam(name = LexConstants.END_DATE_TIME, required = false, defaultValue = LexConstants.DEFAULT_END_DATE_TIME) String endDate,
			@RequestParam(name = LexConstants.RECORD_LINK, required = false, defaultValue = LexConstants.PREV_PAGE_NO_DEFAULT_VALUE) Integer prevPageNo,
			@RequestParam(name = LexConstants.PAGE_SIZE, required = false, defaultValue = LexConstants.PAGE_SIZE_DEFAULT_VALUE) Integer pageSize)
			throws JsonParseException, JsonMappingException, IOException {

		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.content_assignments");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			Map<String, Object> respMap = laService.getAllAssignments(rootOrg, startDate, endDate, prevPageNo,
					pageSize);
			response.put(LexConstants.DATA_LIST, respMap.get(LexConstants.ASSIGNMENTS));
			response.put(LexConstants.NEXT_RECORD, respMap.get(LexConstants.PAGE_NO));
			response.put(LexConstants.HAS_NEXT_PAGE, respMap.get(LexConstants.HAS_NEXT_PAGE));
		} catch (HttpStatusCodeException rce) {
			Map<String, Object> responseMap = objectMapper.readValue(rce.getResponseBodyAsString(),
					new TypeReference<Map<String, Object>>() {
					});
			ResponseCode responseCode = ResponseCode.getResponse(rce.getStatusText());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, responseMap);
			return new ResponseEntity<Response>(response, rce.getStatusCode());
		} catch (BadRequestException ex) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.CLIENT_ERROR.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, ex.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.internalError.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, e.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

	/**
	 * This method fetches all the user assignments by date
	 *
	 * @param rootOrg
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@GetMapping("/v1/la/users/content-assignments")
	public ResponseEntity<?> getUserAssignments(@RequestHeader("rootOrg") String rootOrg,
			@RequestParam(name = LexConstants.START_DATE, required = false, defaultValue = LexConstants.DEFAULT_START_DATE) String startDateStr,
			@RequestParam(name = LexConstants.END_DATE, required = false, defaultValue = LexConstants.DEFAULT_END_DATE) String endDateStr,
			@RequestParam(name = LexConstants.RECORD_LINK, required = false, defaultValue = LexConstants.RECORD_LINK_DEFAULT_VALUE) String pageState,
			@RequestParam(name = LexConstants.PAGE_SIZE, required = false, defaultValue = LexConstants.PAGE_SIZE_DEFAULT_VALUE) Integer pageSize)
			throws JsonParseException, JsonMappingException, IOException {
		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.user_content_assignments");
		response.setTs(ProjectUtil.getFormattedDate());
		try {

			Map<String, Object> respMap = laService.getUsersAssignments(rootOrg, startDateStr, endDateStr, pageState,
					pageSize);
			response.put(LexConstants.DATA_LIST, respMap.get(LexConstants.USER_ASSIGNMENTS));
			response.put(LexConstants.NEXT_RECORD, respMap.get(LexConstants.PAGE_STATE));
			response.put(LexConstants.HAS_NEXT_PAGE, respMap.get(LexConstants.HAS_NEXT_PAGE));
		} catch (HttpStatusCodeException rce) {
			Map<String, Object> responseMap = objectMapper.readValue(rce.getResponseBodyAsString(),
					new TypeReference<Map<String, Object>>() {
					});
			ResponseCode responseCode = ResponseCode.getResponse(rce.getStatusText());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, responseMap);
			return new ResponseEntity<Response>(response, rce.getStatusCode());
		} catch (BadRequestException ex) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.CLIENT_ERROR.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, ex.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.internalError.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, e.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Response>(response, HttpStatus.OK);

	}

	@GetMapping("v1/la/contentv3/download-content")
	ResponseEntity<?> getChannelJson(@RequestHeader("rootOrg") String rootOrg,
			@RequestParam("contentId") String contentId) throws ApplicationLogicError, IOException {
		Response response = new Response();
		response.setVer("v1");
		response.setId("api.lex.channel.json");
		response.setTs(ProjectUtil.getFormattedDate());
		Map<String, Object> respMap;
		try {
			respMap = laService.getChannelJson(rootOrg, contentId);
			response.put(LexConstants.DATA_LIST, respMap);
		} catch (BadRequestException ex) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.CLIENT_ERROR.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, ex.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

}