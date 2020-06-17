package com.infosys.lexauthoringservices.serviceimpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.model.Response;
import com.infosys.lexauthoringservices.service.ContentCrudService;
import com.infosys.lexauthoringservices.service.TextExtractionService;
import com.infosys.lexauthoringservices.util.LexConstants;
import com.infosys.lexauthoringservices.util.LexServerProperties;

@Service
public class TextExtractionServiceImpl implements TextExtractionService{
	
	@Autowired
	ContentCrudService contentCrudService;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	LexServerProperties lexServerProps;
	
	@Override
	public Response resourceTextExtraction(String rootOrg, String org, Map<String, Object> reqMap) throws Exception {
		Response response = new Response();
		String identifier = (String) reqMap.get(LexConstants.IDENTIFIER);
		Map<String,Object> contentMeta = contentCrudService.getContentHierarchy(identifier, rootOrg, org);
		hierarchyIterator(contentMeta);
		response.put("Message", "Successful");
		return response;
	}
	
	//TODO most likely will not be used..
	@Override
	public Response hierarchialExtraction(String rootOrg, String org, Map<String, Object> requestMap) throws Exception {
		Response response = new Response();
		String identifier = (String) requestMap.get(LexConstants.IDENTIFIER);
		Map<String,Object> contentMeta = contentCrudService.getContentHierarchy(identifier, rootOrg, org);
		createTextBlock(contentMeta);
		System.out.println(contentMeta);
		response.put("Message", "Successful");
		return response;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createTextBlock(Map<String, Object> contentMeta) {
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(contentMeta);
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			List<Map<String, Object>> childrenList = (ArrayList) parent.get(LexConstants.CHILDREN);
			for(Map<String, Object> child:childrenList) {
				List<String> textBlock = new ArrayList<>();
				String name = (String) child.getOrDefault(LexConstants.NAME, " ");
				String desc = (String) child.getOrDefault(LexConstants.DESC, " ");
				String lo = (String) child.getOrDefault(LexConstants.LEARNING_OBJECTIVE, " ");
				List<String> keywords = (List<String>) child.get(LexConstants.KEYWORDS);
				String strKeywords = String.join(",", keywords);
				textBlock.add(name);
				textBlock.add(desc);
				textBlock.add(lo);
				textBlock.add(strKeywords);
				child.put("textBlock", textBlock);
			}
			parent.put(LexConstants.CHILDREN, childrenList);
			parentObjs.addAll(childrenList);
		}
	}

	//PRIVATE METHODS
	@SuppressWarnings("unchecked")
	private void hierarchyIterator(Map<String, Object> contentMeta) throws Exception {
		System.out.println(contentMeta);
		StringBuffer writeText = new StringBuffer();
		Queue<Map<String, Object>> parentObjs = new LinkedList<>();
		parentObjs.add(contentMeta);
		while (!parentObjs.isEmpty()) {
			Map<String, Object> parent = parentObjs.poll();
			if (parent.get(LexConstants.CONTENT_TYPE).equals(LexConstants.RESOURCE)) {
				resourceHandler(parent);
				break;
			}
			List<Map<String, Object>> childrenList = (List<Map<String, Object>>) parent.get(LexConstants.CHILDREN);
			for (Map<String, Object> child : childrenList) {
				String childContentType = child.get(LexConstants.CONTENT_TYPE).toString();
				if (!childContentType.equals(LexConstants.RESOURCE)) {
					writeText.append("<b>" + child.get(LexConstants.IDENTIFIER) + " : " + childContentType + "<br><br>"
							+ "\r\n" + "\r\n" + "</b>");
				} else {
					resourceHandler(child);
				}
			}
			parentObjs.addAll(childrenList);
		}
	}
	
	
	private void resourceHandler(Map<String, Object> resourceMeta) throws Exception {
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
				generateForWebHtml(resourceMeta);
			} else if (mimeType.equals(LexConstants.MIME_TYPE_QUIZ)) {
				generateQuizJson(resourceMeta);
			} else if (mimeType.equals(LexConstants.MIME_TYPE_PDF)) {
				generatePdf(resourceMeta);
			} else if (mimeType.equals(LexConstants.MIME_TYPE_HANDSONQUIZ)) {
				generateIntegratedHandsOn(resourceMeta);
			} else if (mimeType.equals(LexConstants.MIME_TYPE_DNDQUIZ)) {
				generateDragDrop(resourceMeta);
			} else if (mimeType.equals(LexConstants.MIME_TYPE_HTMLQUIZ)) {
				generateHTMLQuiz(resourceMeta);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void generateQuizJson(Map<String, Object> resourceMeta) {
		ObjectMapper mapper = new ObjectMapper();
		URL authArtifactUrl = null;
		StringBuffer writeText = new StringBuffer();
		URL artifactUrl = null;
		try {
			artifactUrl = new URL((String) resourceMeta.get(LexConstants.ARTIFACT_URL));
			authArtifactUrl = convertToAuthUrl((String) resourceMeta.get(LexConstants.ARTIFACT_URL));
//			authArtifactUrl = lexServerProps.getContentServiceUrl() + 
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Map<String, Object> quizJson = new HashMap<>();
		try {
			quizJson = (Map<String, Object>) mapper.readValue(authArtifactUrl, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try{
			List<Map<String, Object>> questions = (List<Map<String, Object>>) quizJson.get("questions");
			for (Map<String, Object> question : questions) {
				writeText.append(question.get("question") + " ");
				List<Map<String, Object>> options = (List<Map<String, Object>>) question.get("options");
				for (Map<String, Object> optionObj : options) {
					writeText.append(optionObj.get("text") + " ");
				}
				for (Map<String, Object> optionObj : options) {
					if (optionObj.get("hint") != null) {
						writeText.append(optionObj.get("hint") + " ");
					}
				}
			}
		System.out.println(writeText);
		Map<String,Object> sendMap = new HashMap<>();
		sendMap.put("itemId", resourceMeta.get(LexConstants.IDENTIFIER));
		sendMap.put("filePath", "");
		sendMap.put("itemMimeType", LexConstants.MIME_TYPE_QUIZ);
		sendMap.put("textExtracted", writeText);
		//TODO writeText to shaan API
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Map<String,Object>> request = new HttpEntity<Map<String,Object>>(sendMap,headers);
			ResponseEntity<Object> response = restTemplate.postForEntity(lexServerProps.getTopicServiceUrl(), request, Object.class);
//			ResponseEntity<String> responseEntity = restTemplate.exchange(
//					lexServerProps.getTopicServiceUrl(), HttpMethod.POST,
//					new HttpEntity<Map<String,Object>>(sendMap,headers), String.class);
			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
		} catch (HttpServerErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private URL convertToAuthUrl(String url) throws MalformedURLException {
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
		return new URL(finalStr);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private void generateIntegratedHandsOn(Map<String, Object> resourceMeta) {
		StringBuffer writeText = new StringBuffer();
		ObjectMapper mapper = new ObjectMapper();
		URL artifactUrl = null;
		URL authArtifactUrl = null;
		try {
			artifactUrl = new URL((String) resourceMeta.get(LexConstants.ARTIFACT_URL));
			authArtifactUrl = convertToAuthUrl((String) resourceMeta.get(LexConstants.ARTIFACT_URL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Map<String, Object> integratedJson = null;
		try {
			integratedJson = (Map<String, Object>) mapper.readValue(authArtifactUrl, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		writeText.append(integratedJson.get("problemStatement") + " ");
		//TODO shaan API
		Map<String,Object> sendMap = new HashMap<>();
		sendMap.put("itemId", resourceMeta.get(LexConstants.IDENTIFIER));
		sendMap.put("filePath", "");
		sendMap.put("itemMimeType",LexConstants.MIME_TYPE_HANDSONQUIZ);
		sendMap.put("textExtracted", writeText);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Map<String,Object>> request = new HttpEntity<Map<String,Object>>(sendMap,headers);
			ResponseEntity<Object> response = restTemplate.postForEntity(lexServerProps.getTopicServiceUrl(), request, Object.class);
//			ResponseEntity<String> responseEntity = restTemplate.exchange(
//					lexServerProps.getTopicServiceUrl(), HttpMethod.POST,
//					new HttpEntity<Map<String,Object>>(sendMap,headers), String.class);
			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
			
//			ResponseEntity<String> responseEntity = restTemplate.exchange(
//					lexServerProps.getTopicServiceUrl(), HttpMethod.POST,
//					new HttpEntity<Object>(sendMap), String.class);
//			System.out.println(responseEntity.getStatusCode());
//			System.out.println(responseEntity.getBody());
		} catch (HttpServerErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
		}
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void generateDragDrop(Map<String, Object> resourceMeta) {
		StringBuffer writeText = new StringBuffer();
		ObjectMapper mapper = new ObjectMapper();
		URL authArtifactUrl = null;
		URL artifactUrl = null;
		try {
			artifactUrl = new URL((String) resourceMeta.get(LexConstants.ARTIFACT_URL));
			authArtifactUrl = convertToAuthUrl((String) resourceMeta.get(LexConstants.ARTIFACT_URL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Map<String, Object> dndJson = null;
		try {
			dndJson = (Map<String, Object>) mapper.readValue(authArtifactUrl, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> dndQuestions = (Map<String, Object>) dndJson.get("dndQuestions");
		Map<String, Object> options = (Map<String, Object>) dndQuestions.get("options");
		writeText.append(dndQuestions.get("question") + " ");
		List<Map<String, Object>> answerOptions = (List<Map<String, Object>>) options.get("answerOptions");
		List<Map<String, Object>> additionalOptions = (List<Map<String, Object>>) options.get("additionalOptions");
		writeText.append("Answer Options: ");
		for (Map<String, Object> ansOptObj : answerOptions) {
			writeText.append(ansOptObj.get("text") + " ");
		}
		for (Map<String, Object> addOptObj : additionalOptions) {
			writeText.append(addOptObj.get("text") + " ");
		}
		//TODO shaan API
		Map<String,Object> sendMap = new HashMap<>();
		sendMap.put("itemId", resourceMeta.get(LexConstants.IDENTIFIER));
		sendMap.put("filePath", "");
		sendMap.put("itemMimeType", LexConstants.MIME_TYPE_DNDQUIZ);
		sendMap.put("textExtracted", writeText);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Map<String,Object>> request = new HttpEntity<Map<String,Object>>(sendMap,headers);
			ResponseEntity<Object> response = restTemplate.postForEntity(lexServerProps.getTopicServiceUrl(), request, Object.class);
//			ResponseEntity<String> responseEntity = restTemplate.exchange(
//					lexServerProps.getTopicServiceUrl(), HttpMethod.POST,
//					new HttpEntity<Map<String,Object>>(sendMap,headers), String.class);
			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
//			ResponseEntity<String> responseEntity = restTemplate.exchange(
//					lexServerProps.getTopicServiceUrl(), HttpMethod.POST,
//					new HttpEntity<Object>(sendMap), String.class);
//			System.out.println(responseEntity.getStatusCode());
//			System.out.println(responseEntity.getBody());
		} catch (HttpServerErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
		}
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	private void generateHTMLQuiz(Map<String, Object> resourceMeta) {
		ObjectMapper mapper = new ObjectMapper();
		StringBuffer writeText = new StringBuffer();
		URL artifactUrl = null;
		URL authArtifactUrl = null;
		try {
			artifactUrl = new URL((String) resourceMeta.get(LexConstants.ARTIFACT_URL));
			authArtifactUrl = convertToAuthUrl((String) resourceMeta.get(LexConstants.ARTIFACT_URL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Map<String, Object> htmlJson = null;
		try {
			htmlJson = mapper.readValue(authArtifactUrl, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		writeText.append(htmlJson.get("question") + " ");
		writeText.append(htmlJson.get("html") + " ");
		//TODO shaan API
		Map<String,Object> sendMap = new HashMap<>();
		sendMap.put("itemId", resourceMeta.get(LexConstants.IDENTIFIER));
		sendMap.put("filePath", "");
		sendMap.put("itemMimeType", LexConstants.MIME_TYPE_HTMLQUIZ);
		sendMap.put("textExtracted", writeText);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Map<String,Object>> request = new HttpEntity<Map<String,Object>>(sendMap,headers);
			ResponseEntity<Object> response = restTemplate.postForEntity(lexServerProps.getTopicServiceUrl(), request, Object.class);
			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
		} catch (HttpServerErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void generateForWebHtml(Map<String,Object> contentMeta) throws Exception {
		try {
			ObjectMapper mapper = new ObjectMapper();
			StringBuffer writeText = new StringBuffer();
			String identifier = (String) contentMeta.get(LexConstants.IDENTIFIER);
			URL authArtifactUrl = convertToAuthUrl((String) contentMeta.get(LexConstants.ARTIFACT_URL));
			List<Map<String, Object>> manifestJson = (List<Map<String, Object>>) mapper.readValue(authArtifactUrl,List.class);
			String finalStr = authArtifactUrl.toString();
			int fnIndex = finalStr.lastIndexOf("%2F");
			String prefix = finalStr.substring(0, fnIndex);
			for (Map<String, Object> mObj : manifestJson) {
				String htmlPath = mObj.get("URL").toString();			
				htmlPath = htmlPath.replace("/", "%2F");
				URL readUrl = new URL((String)prefix + (String)htmlPath);
				BufferedReader reader = new BufferedReader(new InputStreamReader(readUrl.openStream()));
				String line;
				while((line = reader.readLine())!=null) {
					writeText.append(line);
				}
				reader.close();
			}
			Map<String,Object> sendMap = new HashMap<>();
			sendMap.put("itemId", identifier);
			sendMap.put("filePath", "");
			sendMap.put("itemMimeType", LexConstants.MIME_TYPE_WEB);
			sendMap.put("textExtracted", writeText);
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<Map<String,Object>> request = new HttpEntity<Map<String,Object>>(sendMap,headers);
				ResponseEntity<Object> response = restTemplate.postForEntity(lexServerProps.getTopicServiceUrl(), request, Object.class);
				
				System.out.println(response.getStatusCode());
				System.out.println(response.getBody());
			} catch (HttpServerErrorException e) {
				System.out.println(e.getStatusCode());
				System.out.println(e.getResponseBodyAsString());
			}
		} catch (Exception e) {
			System.out.println("Error inside the funcion:'File Not Found(html,web-module)'");
			e.printStackTrace();
			throw new Exception(e);
		}
	}
	
	private void generatePdf(Map<String,Object> resourceMeta) {
		try {
			String identifier = (String) resourceMeta.get(LexConstants.IDENTIFIER);
			String artUrl = (String) resourceMeta.get(LexConstants.ARTIFACT_URL);
			URL authArtifactUrl = convertToAuthUrl(artUrl);
			StringBuffer writeText = new StringBuffer();
			Map<String,Object> sendMap = new HashMap<>();
			sendMap.put("itemId", identifier);
			sendMap.put("filePath", authArtifactUrl.toString());
			sendMap.put("itemMimeType", LexConstants.MIME_TYPE_PDF);
			sendMap.put("textExtracted", writeText);
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<Map<String,Object>> request = new HttpEntity<Map<String,Object>>(sendMap,headers);
				ResponseEntity<Object> response = restTemplate.postForEntity(lexServerProps.getTopicServiceUrl(), request, Object.class);
				System.out.println(response.getStatusCode());
				System.out.println(response.getBody());
			} catch (HttpServerErrorException e) {
				System.out.println(e.getStatusCode());
				System.out.println(e.getResponseBodyAsString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
