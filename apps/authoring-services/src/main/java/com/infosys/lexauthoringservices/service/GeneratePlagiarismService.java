package com.infosys.lexauthoringservices.service;

import java.io.File;

import org.springframework.stereotype.Service;

import com.infosys.lexauthoringservices.exception.ApplicationLogicError;


public interface GeneratePlagiarismService {

	public File generatePlagiarismReport(String identifier, String domain,String rootOrg,String org) throws ApplicationLogicError;

}
