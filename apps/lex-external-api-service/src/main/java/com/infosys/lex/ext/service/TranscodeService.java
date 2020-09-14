package com.infosys.lex.ext.service;

import com.infosys.lex.ext.exception.BadRequestException;

import java.util.Map;

public interface TranscodeService {
	
	public Map<String,Object> transcodeStatusUpdate(Map<String,Object> requestMap)throws BadRequestException, Exception;
}
