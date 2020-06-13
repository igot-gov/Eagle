package com.infosys.lexauthoringservices.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.lexauthoringservices.service.GeneratePlagiarismService;

@RestController
public class GeneratePlagiarismController {

	@Autowired
	GeneratePlagiarismService generatePlagiarism;

	// TODO TODO LOts todo
	@RequestMapping(method = RequestMethod.GET, value = "/v2/auth/plagiarism", produces = "appliction/zip")
	public ResponseEntity<InputStreamResource> generatePlagiarism(@RequestParam("identifier") String identifier,
			@RequestParam("domain") String domain, @RequestParam("rootOrg") String rootOrg,
			@RequestParam("org") String org) throws Exception {
		File zipFolder = null;

		try {
			// response.put("result",
			// generatePlagiarism.generatePlagiarismReport(identifier));
			// returns file
			System.out.println("Before file creation");
			zipFolder = generatePlagiarism.generatePlagiarismReport(identifier, domain, rootOrg, org);
			System.out.println("After file creation");
			InputStreamResource res = new InputStreamResource(new FileInputStream(zipFolder));
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + zipFolder.getName())
					.contentType(MediaType.APPLICATION_OCTET_STREAM).contentLength(zipFolder.length()).body(res);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}

	}

	@RequestMapping(value = "/v1/auth/plagiarism", produces = "appliction/zip")
	public ResponseEntity<byte[]> generatePlagiarism(String hi, @RequestParam("identifier") String identifier,
			@RequestParam("domain") String domain, @RequestParam("rootOrg") String rootOrg,
			@RequestParam("org") String org) throws Exception {
		System.out.println("Hi from controller");
		HttpServletResponse response = null;
		ResponseEntity<byte[]> responseE = null;
		File zipFolder = null;
		HttpStatus httpStatus = null;
		HttpHeaders header = new HttpHeaders();

		try {
			// response.put("result",
			// generatePlagiarism.generatePlagiarismReport(identifier));
			httpStatus = HttpStatus.OK;
			zipFolder = generatePlagiarism.generatePlagiarismReport(identifier, domain, rootOrg, org);
			response.setContentType("APPLICATION/OCTET-STREAM");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + identifier + ".zip" + "\"");
			OutputStream out = response.getOutputStream();
			FileInputStream in = new FileInputStream(zipFolder);
			byte[] buffer = new byte[4096];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.flush();
			header.setContentDispositionFormData(identifier, identifier);
			header.setCacheControl("must-revalidate, post-check=0, pre-check=0");
			responseE = new ResponseEntity<>(buffer, header, HttpStatus.OK);
			// response.setResponseCode(ResponseCode.OK);
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().startsWith("No resource")) {
				httpStatus = HttpStatus.BAD_REQUEST;
				// response.put("errmsg", e.getMessage());
				// response.setResponseCode(ResponseCode.RESOURCE_NOT_FOUND);
			} else {
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				// response.put("errmsg", e.getMessage());
				// response.setResponseCode(ResponseCode.internalError);
			}
		}
		return responseE;
	}
}
