package com.infosys.lex.ext.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.responsecode.ResponseCode;

import com.infosys.lex.ext.exception.BadRequestException;
import com.infosys.lex.ext.service.TranscodeService;
import com.infosys.lex.ext.util.LexConstants;

@RestController
public class TranscodeController {
	
	@Autowired
	TranscodeService transcodeService;
	
	@PostMapping("/transcode/update")
	public ResponseEntity<Response> getUsersForRoleVariants(@RequestBody Map<String, Object> request) throws Exception {
		Response response = new Response();
		response.setVer("v1");
		response.setId("api.wingspan.trancodeService");
		response.setTs(ProjectUtil.getFormattedDate());
		try {
			response.put(LexConstants.RESULT,transcodeService.transcodeStatusUpdate(request));
		} catch (BadRequestException bre) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.CLIENT_ERROR.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, bre.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			ResponseCode responseCode = ResponseCode.getResponse(ResponseCode.internalError.getErrorCode());
			response.setResponseCode(responseCode);
			response.put(LexConstants.ERROR, e.getMessage());
			return new ResponseEntity<Response>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}

}
