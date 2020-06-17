package com.infosys.lexauthoringservices.serviceimpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.exception.ApplicationLogicError;
import com.infosys.lexauthoringservices.service.GeneratePlagiarismService;
import com.infosys.lexauthoringservices.util.LexConstants;
import com.infosys.lexauthoringservices.util.LexServerProperties;

@Service
public class GeneratePlagiarismServiceImpl implements GeneratePlagiarismService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ContentCrudServiceImpl contentCrudServiceImpl;
	
	@Autowired
	LexServerProperties lexServerProps;

	Path des = null;

	@Override
	public File generatePlagiarismReport(String identifier, String domain, String rootOrg,String org) {
		File txtFile = null;
		File zipFolder = null;

		try {
			//StringBuffer to hold all the textual content
			StringBuffer writeText = new StringBuffer();

			if (Files.notExists(Paths.get("Plagiarism"))) {
				new File("Plagiarism").mkdir();
			}
			
			//writes all textual content to buffer
			generatePlagiarism(identifier, writeText, true, identifier, domain, rootOrg,org);
			System.out.println("back after writeBuffer creation " + writeText);
			
			//create new text file
			txtFile = new File("Plagiarism/" + identifier + "/" + identifier + ".html");
			new File("Plagiarism/" + identifier).mkdir();
			BufferedWriter writer = new BufferedWriter(new FileWriter(txtFile));
			writer.write(writeText.toString());
			writer.flush();
			writer.close();
			String src = "Plagiarism/" + identifier;
			String dest = "Plagiarism/" + identifier + ".zip";
			zipFolder = new File(dest);
			zipFolder(Paths.get(src), Paths.get(dest));

			if (!zipFolder.exists()) {
				System.out.println("FILE NOT FOUND");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zipFolder;
	}
	
	

	@SuppressWarnings("unchecked")
	private void generatePlagiarism(String identifier, StringBuffer writeText, boolean firstRecur,
			String parentIdentifier, String domain, String rootOrg,String org) {
		Map<String, Object> contentMeta = new HashMap<>();
		if (firstRecur) {
			if (!new File("Plagiarism/" + identifier).mkdir()) {
				try {
					identifier = identifier.trim();
					des = Paths.get("Plagiarism/" + identifier);
				} catch (Exception e) {
					System.out.println("Error: Cannot create the URI dest path");
					e.printStackTrace();
					throw new ApplicationLogicError("Error: Cannot create the URI dest path");

				}
			}
			firstRecur = false;
		}
		try {
			
			List<String> fields = Arrays.asList(LexConstants.IDENTIFIER,LexConstants.MIME_TYPE,LexConstants.ARTIFACT_URL,LexConstants.CONTENT_TYPE,LexConstants.ISEXTERNAL);
			Map<String,Object> requestMap = new HashMap<>();
			requestMap.put("fields",fields);
			contentMeta = contentCrudServiceImpl.getContentHierarchyV2(parentIdentifier, rootOrg, org, requestMap);
			//contentMeta = (Map<String, Object>) contentCrudServiceImpl.getContentHierarchy(identifier,rootOrg,org);
//			contentMeta = (Map<String, Object>) contentMeta.get(LexConstants.CONTENT);
//			System.out.println(contentMeta);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		writeText.append("<b>" + identifier + ":" + contentType + "<br><br>" + "\r\n" + "\r\n" + "</b>");
		hierarchyIterator(contentMeta, writeText, domain,identifier);

	}

	@SuppressWarnings("unchecked")
	private void hierarchyIterator(Map<String, Object> contentMeta, StringBuffer writeText, String domain,String identifier) {
		
		Stack<Map<String,Object>> parentObjs = new Stack<Map<String,Object>>();
		parentObjs.add(contentMeta);
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.pop();
			writeText.append("<b>" + parent.get(LexConstants.IDENTIFIER) + " : " + parent.get(LexConstants.CONTENT_TYPE) + "<br><br>"
					+ "\r\n" + "\r\n" + "</b>");
			if (parent.get(LexConstants.CONTENT_TYPE).equals(LexConstants.RESOURCE)) {
				try {
					resourceHandler(parent, writeText, domain,identifier);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			List<Map<String, Object>> childrenList = (List<Map<String, Object>>) parent.get(LexConstants.CHILDREN);
			Collections.reverse(childrenList);
			parentObjs.addAll(childrenList);
		}
	}
	
	private void zipFolder(Path sourceFolderPath, Path zipPath) throws Exception {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
		Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
				Files.copy(file, zos);
				zos.closeEntry();
				return FileVisitResult.CONTINUE;
			}
		});
		zos.close();
	}


	private void resourceHandler(Map<String, Object> resourceMeta, StringBuffer writeText, String domain,String topLevelId) throws Exception {
		System.out.println("inside resource handler");
		boolean isExternal = false;
		try {
			isExternal = (boolean) resourceMeta.get(LexConstants.ISEXTERNAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isExternal == false) {
			String mimeType = (String) resourceMeta.get(LexConstants.MIME_TYPE);
			if (mimeType.equals(LexConstants.MIME_TYPE_WEB) || mimeType.equals(LexConstants.MIME_TYPE_HTML)) {
				generateForWebHtml(resourceMeta,writeText,domain);
			} else if (mimeType.equals(LexConstants.MIME_TYPE_QUIZ)) {
				generateQuizJson(resourceMeta, writeText, domain);
			} else if (mimeType.equals(LexConstants.MIME_TYPE_PDF)) {
				generatePDF(resourceMeta, writeText, domain,topLevelId);
			} else if (mimeType.equals(LexConstants.MIME_TYPE_HANDSONQUIZ)) {
				generateIntegratedHandsOn(resourceMeta, writeText, domain);
			} else if (mimeType.equals(LexConstants.MIME_TYPE_DNDQUIZ)) {
				generateDragDrop(resourceMeta, writeText, domain);
			} else if (mimeType.equals(LexConstants.MIME_TYPE_HTMLQUIZ)) {
				generateHTMLQuiz(resourceMeta,writeText,domain);
			}
		}
	}

	
	//DUNZO
	@SuppressWarnings("unchecked")
	private void generateForWebHtml(Map<String,Object> contentMeta,StringBuffer writeText,String domain) throws Exception {
		
		try {

			String identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);
			String authArtifactUrl = convertToAuthUrl((String) contentMeta.get(LexConstants.ARTIFACT_URL));
			List<Map<String, Object>> manifestJson = restTemplate.getForObject(authArtifactUrl, ArrayList.class);
			String finalStr = authArtifactUrl.toString();
			int fnIndex = finalStr.lastIndexOf("%2F");
			String prefix = finalStr.substring(0, fnIndex);
			String viewerUrl = domain;
			viewerUrl = viewerUrl + "/" + identifier;
			writeText.append("Viewer URL : " + "<a href = \"" + viewerUrl + "\" target=\"_blank\"" + ">"
					+ "Click here" + "</a>" + "\r\n");
			for (Map<String, Object> mObj : manifestJson) {
				String htmlPath = mObj.get("URL").toString();			
				htmlPath = htmlPath.replace("/", "%2F");
				URL readUrl = new URL((String)prefix + (String)htmlPath);
				BufferedReader reader = new BufferedReader(new InputStreamReader(readUrl.openStream()));
				String line;
				while((line = reader.readLine())!=null) {
					writeText.append(line);
				}
				writeText.append("\r\n");
				reader.close();
			}
		} catch (Exception e) {
			System.out.println("Error inside the funcion:'File Not Found(html,web-module)'");
			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
	
	@SuppressWarnings("unused")
	private String convertToAuthUrl(String url) throws MalformedURLException {
		String[] parts = url.split("/");
		System.out.println(parts);
		URL artifactUrl = null;
		List<String> partsList = Arrays.asList(parts);
		System.out.println(partsList);
		int index = partsList.indexOf("content-store");
		index = index + 1;
		String finalStr = lexServerProps.getContentServiceUrl()+ "/contentv3/download/" + partsList.get(index);
		for(int i=index+1;i<partsList.size();i++) {
			finalStr = finalStr+ "%2F" +  partsList.get(i);
		}
		System.out.println(finalStr);
		return finalStr;
//		return new URL(finalStr);
	}
	
	
	@SuppressWarnings("unchecked")
	private void generateHTMLQuiz(Map<String, Object> resourceMeta, StringBuffer writeText, String domain) throws MalformedURLException {
		
		String authArtifactUrl = null;
		String viewerUrl = domain + resourceMeta.get(LexConstants.IDENTIFIER);
		writeText.append("Viewer URL : " + "<a href = \"" + viewerUrl + "\" target=\"_blank\"" + ">" + "Click here"+ "</a>" + "\r\n");
		
		authArtifactUrl = convertToAuthUrl((String) resourceMeta.get(LexConstants.ARTIFACT_URL));
		Map<String, Object> htmlJson = null;
		
		htmlJson = restTemplate.getForObject(authArtifactUrl, Map.class);
		
		writeText.append("<br><div>"+htmlJson.get("question"));
		writeText.append("<br><div>"+htmlJson.get("html"));
	}

	@SuppressWarnings("unchecked")
	private void generateDragDrop(Map<String, Object> resourceMeta, StringBuffer writeText, String domain) throws MalformedURLException {
		String authUrl = convertToAuthUrl((String) resourceMeta.get(LexConstants.ARTIFACT_URL));
		String viewerUrl = domain + "/" + resourceMeta.get(LexConstants.IDENTIFIER);
		writeText.append("Viewer URL : " + "<a href = \"" + viewerUrl + "\" target=\"_blank\"" + ">" + "Click here"
				+ "</a>" + "\r\n");
		
		Map<String, Object> dndJson = new HashMap<>();
		
		dndJson = restTemplate.getForObject(authUrl, Map.class);
		Map<String, Object> dndQuestions = (Map<String, Object>) dndJson.get("dndQuestions");
		Map<String, Object> options = (Map<String, Object>) dndQuestions.get("options");
		writeText.append(dndQuestions.get("question"));
		writeText.append("<br><div>");
		List<Map<String, Object>> answerOptions = (List<Map<String, Object>>) options.get("answerOptions");
		List<Map<String, Object>> additionalOptions = (List<Map<String, Object>>) options.get("additionalOptions");
		writeText.append("Answer Options: ");
		writeText.append("<ul>");
		for (Map<String, Object> ansOptObj : answerOptions) {
			writeText.append("<li>" + ansOptObj.get("text") + "</li>");
		}
		writeText.append("</ul>");
		writeText.append("<br>");
		writeText.append("Additional Options: ");
		writeText.append("<ul>");
		for (Map<String, Object> addOptObj : additionalOptions) {
			writeText.append("<li>" + addOptObj.get("text") + "</li>");
		}
		writeText.append("<ul>");

	}

	//DUNZO
	@SuppressWarnings("unchecked")
	private void generateIntegratedHandsOn(Map<String, Object> resourceMeta, StringBuffer writeText, String domain) throws MalformedURLException {
		
		String authurl = convertToAuthUrl((String)resourceMeta.get(LexConstants.ARTIFACT_URL));
		String viewerUrl = domain + "/" + resourceMeta.get(LexConstants.IDENTIFIER);
		writeText.append("Viewer URL : " + "<a href = \"" + viewerUrl + "\" target=\"_blank\"" + ">" + "Click here"
				+ "</a>" + "\r\n");
		Map<String, Object> integratedJson = null;
		integratedJson = restTemplate.getForObject(authurl, Map.class);
		writeText.append("<br><div>" + integratedJson.get("problemStatement"));
	}

	//DUNZO
	private void generatePDF(Map<String, Object> resourceMeta, StringBuffer writeText, String domain,String topLevelId) {
		try {
			String identifier = (String) resourceMeta.get(LexConstants.IDENTIFIER);
			String artifactUrl = (String) resourceMeta.get(LexConstants.ARTIFACT_URL);
			String pdfFileName = artifactUrl.substring(artifactUrl.lastIndexOf("/"));
			String authUrl = convertToAuthUrl(artifactUrl);
			
			byte[] response = readDataFromContentDirectory(authUrl);
			Path path = Paths.get("Plagiarism/" + topLevelId+"/" + pdfFileName);
			InputStream stream = new ByteArrayInputStream(response);
			File file = new File("Plagiarism/" + topLevelId + "/" + pdfFileName);
			
			long result = Files.copy(stream, file.toPath(),StandardCopyOption.REPLACE_EXISTING);
			String viewerUrl = domain + "/" + identifier;
			writeText.append("Viewer URL : " + "<a href = \"" + viewerUrl + "\" target=\"_blank\"" + ">" + "Click here"+ "</a><br><br>" + "\r\n");
			stream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private byte[] readDataFromContentDirectory(String filePath) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_PDF, MediaType.APPLICATION_OCTET_STREAM));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		// pic url from env
		ResponseEntity<byte[]> response = restTemplate.exchange(filePath, HttpMethod.GET, entity,byte[].class);
		
		return response.getBody();
	}


	//DUNZO
	@SuppressWarnings("unchecked")
	private void generateQuizJson(Map<String, Object> resourceMeta, StringBuffer writeText, String domain) throws MalformedURLException {
		
		String authUrl = convertToAuthUrl((String) resourceMeta.get(LexConstants.ARTIFACT_URL));
		String viewerUrl = domain + "/" + resourceMeta.get(LexConstants.IDENTIFIER);
		writeText.append("Viewer URL : " + "<a href = \"" + viewerUrl + "\" target=\"_blank\"" + ">" + "Click here"+ "</a>" + "\r\n");
		System.out.println(writeText);
		Map<String, Object> quizJson = new HashMap<>();
		quizJson = restTemplate.getForObject(authUrl, HashMap.class);
		try{
			List<Map<String, Object>> questions = (List<Map<String, Object>>) quizJson.get("questions");
			for (Map<String, Object> question : questions) {
				writeText.append("<br><div>" + question.get("question"));
				List<Map<String, Object>> options = (List<Map<String, Object>>) question.get("options");
				writeText.append("<ul>");
				for (Map<String, Object> optionObj : options) {
					writeText.append("<li>" + optionObj.get("text") + "</l>");
				}
				writeText.append("</ul>");
				writeText.append("<br>");
				for (Map<String, Object> optionObj : options) {
					if (optionObj.get("hint") != null) {
						writeText.append("<li> Hint:" + optionObj.get("hint") + "</l>");
					}
				}
			}
			writeText.append("</div><br><br>");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
