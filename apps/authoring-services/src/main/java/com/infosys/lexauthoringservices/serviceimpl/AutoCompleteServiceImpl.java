package com.infosys.lexauthoringservices.serviceimpl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion.Entry;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion.Entry.Option;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.lexauthoringservices.exception.ApplicationLogicError;
import com.infosys.lexauthoringservices.exception.BadRequestException;
import com.infosys.lexauthoringservices.exception.ResourceNotFoundException;
import com.infosys.lexauthoringservices.model.Tag;
import com.infosys.lexauthoringservices.model.Track;
import com.infosys.lexauthoringservices.model.cassandra.MasterValues;
import com.infosys.lexauthoringservices.model.cassandra.User;
import com.infosys.lexauthoringservices.repository.cassandra.bodhi.MasterValuesRepository;
import com.infosys.lexauthoringservices.repository.cassandra.sunbird.UserRepository;
import com.infosys.lexauthoringservices.service.AutoCompleteService;
import com.infosys.lexauthoringservices.util.AuthoringUtil;
import com.infosys.lexauthoringservices.util.LexConstants;

@Service
public class AutoCompleteServiceImpl implements AutoCompleteService {

	@Autowired
	RestHighLevelClient esClient;

	@Autowired
	MasterValuesRepository masterValuesRepo;

	@Autowired
	UserRepository userRepository;

	@Override
	public List<Map<String, Object>> getUnits(Map<String, Object> reqMap) {

		if (reqMap == null || reqMap.isEmpty())
			throw new BadRequestException("Valid Contract not adhered");

		String query = (String) reqMap.get("query");
		String limit = (String) reqMap.get("limit");

		if (query == null || query.trim().isEmpty())
			throw new BadRequestException("Query cant be null or empty");

		List<Map<String, Object>> unitList = new ArrayList<Map<String, Object>>();

		SearchRequest searchRequestBuilder = new SearchRequest();
		searchRequestBuilder.indices(LexConstants.EsIndex.unit.getIndexName());
		searchRequestBuilder.types(LexConstants.EsType.skills.getTypeName());
		searchRequestBuilder.searchType(SearchType.QUERY_THEN_FETCH);

		if (query == null || query.trim().isEmpty())
			throw new BadRequestException("Query cant be null or empty");

		int size = 5;

		if (limit != null && !limit.trim().isEmpty()) {
			try {
				size = Integer.parseInt(limit);
			} catch (NumberFormatException e) {
				throw new BadRequestException("Limit has to be a number");
			}
		}

		CompletionSuggestionBuilder unitSuggest = SuggestBuilders.completionSuggestion("unit_suggest").prefix(query)
				.size(size);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		SuggestBuilder suggestBuilder = new SuggestBuilder();
		suggestBuilder.addSuggestion("unit_suggest", unitSuggest);
		sourceBuilder.suggest(suggestBuilder);
		searchRequestBuilder.source(sourceBuilder);
		SearchResponse searchResponse = null;

		try {
			searchResponse = esClient.search(searchRequestBuilder, RequestOptions.DEFAULT);
			CompletionSuggestion completionSuggestion = searchResponse.getSuggest().getSuggestion("unit_suggest");
			Entry entry = completionSuggestion.getEntries().get(0);
			List<Option> optionList = entry.getOptions();

			for (Option option : optionList) {
				Map<String, Object> sourceMapForEachHit = option.getHit().getSourceAsMap();
				unitList.add(sourceMapForEachHit);
			}
		} catch (Exception e) {
			throw new ApplicationLogicError("Could not fetch units");
		}
		return unitList;
	}

	@Override
	public List<Map<String, Object>> getSkills(Map<String, Object> reqMap) {

		if (reqMap == null || reqMap.isEmpty())
			throw new BadRequestException("Valid Contract not adhered");

		String query = (String) reqMap.get("query");
		String limit = (String) reqMap.get("limit");

		if (query == null || query.trim().isEmpty())
			throw new BadRequestException("Query cant be null or empty");

		List<Map<String, Object>> skillsList = new ArrayList<Map<String, Object>>();

		String index = LexConstants.EsIndex.skills.getIndexName();

		SearchRequest searchRequestBuilder = new SearchRequest();
		searchRequestBuilder.indices(index);
		searchRequestBuilder.types(LexConstants.EsType.skills.getTypeName());
		searchRequestBuilder.searchType(SearchType.QUERY_THEN_FETCH);

		if (query == null || query.trim().isEmpty())
			throw new BadRequestException("Query cant be null or empty");

		int size = 5;

		if (limit != null && !limit.trim().isEmpty()) {
			try {
				size = Integer.parseInt(limit);
			} catch (NumberFormatException e) {
				throw new BadRequestException("Limit has to be a number");
			}
		}

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		CompletionSuggestionBuilder skillNameSuggest = SuggestBuilders.completionSuggestion("level3").prefix(query)
				.size(size);
		SuggestBuilder suggestBuilder = new SuggestBuilder();
		suggestBuilder.addSuggestion("level_suggest", skillNameSuggest);
		sourceBuilder.suggest(suggestBuilder);
		searchRequestBuilder.source(sourceBuilder);
		SearchResponse searchResponse = null;

		try {
			searchResponse = esClient.search(searchRequestBuilder, RequestOptions.DEFAULT);
			CompletionSuggestion completionSuggestion = searchResponse.getSuggest().getSuggestion("level_suggest");
			Entry entry = completionSuggestion.getEntries().get(0);
			List<Option> optionList = entry.getOptions();

			for (Option option : optionList) {
				Map<String, Object> sourceMapForEachHit = option.getHit().getSourceAsMap();
				Map<String, Object> sourceMapToUI = new HashMap<String, Object>();
				sourceMapToUI.put(LexConstants.IDENTIFIER, sourceMapForEachHit.get(LexConstants.IDENTIFIER));
				sourceMapToUI.put(LexConstants.NAME, sourceMapForEachHit.get("level3"));
				sourceMapToUI.put(LexConstants.CATEGORY.toLowerCase(),
						sourceMapForEachHit.get(LexConstants.CATEGORY.toLowerCase()));
				sourceMapToUI.put(LexConstants.SKILL, sourceMapForEachHit.get(LexConstants.SKILL));
				skillsList.add(sourceMapToUI);
			}
		} catch (Exception ex) {
			throw new ApplicationLogicError("could not fetch skills");
		}
		return skillsList;
	}

	@Override
	public List<Map<String, Object>> getClients(Map<String, Object> reqMap) {

		if (reqMap == null || reqMap.isEmpty())
			throw new BadRequestException("Valid Contract not adhered");

		String query = (String) reqMap.get("query");
		String limit = (String) reqMap.get("limit");

		if (query == null || query.trim().isEmpty())
			throw new BadRequestException("Query cant be null or empty");

		List<Map<String, Object>> clientNamesList = new ArrayList<Map<String, Object>>();

		SearchRequest searchRequestBuilder = new SearchRequest();
		searchRequestBuilder.indices(LexConstants.EsIndex.client.getIndexName());
		searchRequestBuilder.types(LexConstants.EsType.skills.getTypeName());
		searchRequestBuilder.searchType(SearchType.QUERY_THEN_FETCH);

		if (query == null || query.trim().isEmpty())
			throw new BadRequestException("Query cant be null or empty");

		int size = 5;

		if (limit != null && !limit.trim().isEmpty()) {
			try {
				size = Integer.parseInt(limit);
			} catch (NumberFormatException e) {
				throw new BadRequestException("Limit has to be a number");
			}
		}

		CompletionSuggestionBuilder skillNameSuggest = SuggestBuilders.completionSuggestion("customerName")
				.prefix(query).size(size);

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		SuggestBuilder suggestBuilder = new SuggestBuilder();
		suggestBuilder.addSuggestion("customerName_suggest", skillNameSuggest);
		sourceBuilder.suggest(suggestBuilder);
		searchRequestBuilder.source(sourceBuilder);
		SearchResponse searchResponse = null;

		try {
			searchResponse = esClient.search(searchRequestBuilder, RequestOptions.DEFAULT);
			CompletionSuggestion completionSuggestion = searchResponse.getSuggest()
					.getSuggestion("customerName_suggest");
			Entry entry = completionSuggestion.getEntries().get(0);
			List<Option> optionList = entry.getOptions();

			for (Option option : optionList) {
				Map<String, Object> sourceMapForEachHit = option.getHit().getSourceAsMap();
				Map<String, Object> sourceMapToUI = new HashMap<String, Object>();
				sourceMapToUI.put("name", sourceMapForEachHit.get("customerName"));
				sourceMapToUI.put("id", sourceMapForEachHit.get("customerCode"));
				sourceMapToUI.put("displayName",
						sourceMapForEachHit.get("customerName") + "#" + sourceMapForEachHit.get("customerCode"));
				clientNamesList.add(sourceMapToUI);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationLogicError("could not fetch clients");
		}
		return clientNamesList;
	}

	@Override
	public Map<String, Object> getEnums() {

		List<String> idsToFetch = new ArrayList<>();
		idsToFetch.addAll(Arrays.asList(LexConstants.CONTENT_TYPE, LexConstants.RESOURCE_TYPE,
				LexConstants.COMPLEXITY_TYPE, LexConstants.SOURCE_NAME, LexConstants.AUDIENCE, LexConstants.COPYRIGHT,
				LexConstants.IDEAL_SCREEN_SIZE, LexConstants.RESOURCE_CATEGORY, LexConstants.LEARNING_MODE,
				LexConstants.UNITS, LexConstants.DIMENSION, LexConstants.REGION, LexConstants.OFFERING_MODE,LexConstants.CATEGORY_TYPE));

		Map<String, Object> enumsToBeDisplayed = new HashMap<>();

		try {
			List<MasterValues> masterValues = masterValuesRepo.findAll();

			if (masterValues == null || masterValues.isEmpty()) {
				throw new ApplicationLogicError("Requested Resource could not be found");
			}
			List<String> units = new ArrayList<>();
			Map<String, Object> etaTrack = new HashMap<>();

			for (MasterValues masterValue : masterValues) {
				if (masterValue.getEntity().equals(LexConstants.DIMENSION)
						|| masterValue.getEntity().equals(LexConstants.REGION)
						|| masterValue.getEntity().equals(LexConstants.OFFERING_MODE)) {
					enumsToBeDisplayed.put(masterValue.getEntity(), masterValue.getValues());
				}
			}

			for (MasterValues masterValue : masterValues) {
				if (idsToFetch.contains(masterValue.getEntity())) {
					enumsToBeDisplayed.put(masterValue.getEntity(), masterValue.getValues());
					if (masterValue.getEntity().equals(LexConstants.UNITS))
						units = masterValue.getValues();
				}
			}

			for (MasterValues masterValue : masterValues) {
				if (units.contains(masterValue.getEntity())) {
					etaTrack.put(masterValue.getEntity(), masterValue.getValues());
				}
			}
			List<String> sourceNames = new ArrayList<>();
			for (MasterValues masterValue : masterValues) {
				if(masterValue.getEntity().equals(LexConstants.SOURCE_NAME)) {
					sourceNames.addAll(masterValue.getValues());
				}
			}
			
			List<String> complexityLevel = new ArrayList<>();
			for (MasterValues masterValue : masterValues) {
				if(masterValue.getEntity().equals(LexConstants.COMPLEXITY_TYPE)) {
					complexityLevel.addAll(masterValue.getValues());
				}
			}

			List<Map<String, Object>> subTitles = getSubTitles();
			List<Track> trackArray = getTrackObjects();
			enumsToBeDisplayed.put(LexConstants.TRACK, trackArray);
			Map<String, List<Tag>> tagArrays = new HashMap<String, List<Tag>>();
			List<Tag> categoryTagArray = getTagObjects(LexConstants.CATEGORY);
			tagArrays.put(LexConstants.CATEGORY, categoryTagArray);
			List<Tag> subTrackTagArray = getTagObjects(LexConstants.SUB_TRACK);
			tagArrays.put(LexConstants.SUB_TRACK, subTrackTagArray);
			List<Tag> newServicesThemeTagArray = getTagObjects(LexConstants.NEW_SERVICES_THEME);
			tagArrays.put(LexConstants.NEW_SERVICES_THEME, newServicesThemeTagArray);
			List<Tag> newServicesTagArray = getTagObjects(LexConstants.NEW_SERVICES);
			tagArrays.put(LexConstants.NEW_SERVICES, newServicesTagArray);
			enumsToBeDisplayed.put(LexConstants.TAG, tagArrays);
			enumsToBeDisplayed.put(LexConstants.LEARNING_TRACK, etaTrack);
			String portalOwner[] = { "email_addr" };
			enumsToBeDisplayed.put(LexConstants.PORTAL_OWNER, portalOwner);
			enumsToBeDisplayed.put(LexConstants.SUB_TITLES, subTitles);
			
			enumsToBeDisplayed.put(LexConstants.COMPLEXITY_TYPE, complexityLevel);
			enumsToBeDisplayed.put(LexConstants.SOURCE_NAME, sourceNames);
			enumsToBeDisplayed.put("categoryType", Arrays.asList("Analyst Report", "Article", "Assessment", "Brochure", "Capstone Project", "Case Study", "Certification", "Client Deck", "Competitive", "Elevator Pitch", "Exercise", "HandsOnAssessment", "Infographics", "Insights", "Lecture", "Live Stream", "Offering", "Podcast", "Point Of View", "Prelude", "Primer", "Quiz", "Reference", "Research Study", "Tryout", "Webinar Recording"));


		} catch (NullPointerException nullPointerException) {
			throw new ApplicationLogicError(nullPointerException.getMessage());
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new ApplicationLogicError("Invalid Arguments");
		} catch (Exception exception) {
			throw new ApplicationLogicError(exception.getMessage());
		}
		return enumsToBeDisplayed;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getSubTitles() throws JsonParseException, IOException {
		InputStream srtFile = (this.getClass().getResourceAsStream("/srt_languages.json"));
		StringWriter writer = new StringWriter();
		IOUtils.copy(srtFile, writer, null);
		String json = writer.toString();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, List.class);
	}

	public List<Tag> getTagObjects(String type) {
		String fileName = "";
		if (type == "Category")
			fileName = "CategoryTag.txt";
		else if (type == "SubTrack")
			fileName = "SubTrack.txt";
		else if (type == "NewServicesTheme")
			fileName = "NewServiceTheme.txt";
		else if (type == "NewServices")
			fileName = "NewServices.txt";

		try {
			InputStream in = getClass().getResourceAsStream("/" + fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			List<Tag> tagList = new ArrayList<Tag>();
			while ((line = reader.readLine()) != null) {
				String tempLine = line;
				Tag tag = new Tag();
				tag.setType(type);
				tag.setValue(tempLine);
				String tagId = AuthoringUtil.md5HashGenerator(tempLine).toString().substring(0, 22);
				tag.setId(tagId);
				tagList.add(tag);
			}
			return tagList;
		} catch (FileNotFoundException e) {
			throw new ApplicationLogicError("File Not Found Error");
		} catch (IOException e) {
			throw new ApplicationLogicError("IO Exception");
		}
	}

	public List<Track> getTrackObjects() {

		String tracks[] = { "AI and Automation", "Big Data and Analytics", "CIS", "DataScience", "Digital", "Mobility",
				"Agile", "Engineering", "Primer", "Sales Play", "Credentials", "Point of View", "Sales Competencies",
				"Process", "Financial Services", "Manufacturing", "SOURCE", "Heath , Insurance and Life Sciences",
				"Retail", "Dev Competencies", "PM competencies" };

		Arrays.sort(tracks);

		List<Track> trackList = new ArrayList<Track>();
		for (int i = 0; i < tracks.length; i++) {
			String tempLine = tracks[i];
			Track track = new Track();
			track.setName(tempLine);
			String trackId = AuthoringUtil.md5HashGenerator(tempLine).toString().substring(0, 22);
			track.setId(trackId);
			track.setStatus("active");
			track.setVisibility("public");
			trackList.add(track);
		}
		return trackList;
	}

	@Override
	public Map<String, Object> getEmail() {

		Optional<MasterValues> masterValues = masterValuesRepo.findById("trackLeads");

		if (!masterValues.isPresent()) {
			throw new ResourceNotFoundException("No record Found");
		} else {
			Map<String, Object> mapReturn = new HashMap<String, Object>();
			List<String> returnList = new ArrayList<String>();
			for (String uuid : masterValues.get().getValues()) {
				Optional<User> user = userRepository.findById(uuid);
				if (!user.isPresent()) {
					throw new ResourceNotFoundException("No record found for id");
				} else {
					returnList.add(user.get().getEmail());
					mapReturn.put("Email", returnList);
				}
			}
			return mapReturn;
		}
	}

}
