package com.infosys.lexauthoringservices.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.infosys.lexauthoringservices.model.Competency;
import com.infosys.lexauthoringservices.producer.Producer;
import com.infosys.lexauthoringservices.service.CompetencyService;
import com.infosys.lexauthoringservices.service.ContentCrudService;
import com.infosys.lexauthoringservices.util.LexLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompetencyServiceImp implements CompetencyService {

    @Autowired
    private LexLogger logger;

    @Autowired
    private Producer producer;

    @Autowired
    private ContentCrudService contentCrudService;

    @Autowired
    private GraphServiceImpl graphService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${kafka.topics.competency.update}")
    private String competencyUpdateTopic;

    @Value("${sbext.service.host}")
    private String sbextServiceURL;

    @Value("${sbext.service.competencySearchPath}")
    private String competencySearchPath;

    public final String COMPETENCIES_CONST = "competencies";

    public final String ID_CONST = "id";

    public final String NAME_CONST = "name";

    public final String DESCRIPTION_CONST = "description";

    public final String IDENTIFIER_CONST = "identifier";

    public final String TYPE_CONST = "type";

    @Override
    public void updateCompetencyData(Competency competency) {
        producer.push(competencyUpdateTopic, competency);
    }

    /**
     * Update the copetency in neo4j & elasticsearch
     *
     * @param competency
     * @return
     */
    @Override
    public Competency processUpdateCompetencyData(Competency competency) {
        StringBuilder builder = new StringBuilder();
        String path = competencySearchPath.replace("{org}", competency.getRootOrg()).replace("{id}", competency.getId());
        builder.append(sbextServiceURL).append(path);
        Object response = fetchResult(builder);
        if (ObjectUtils.isEmpty(response))
            return competency;
        List<HashMap<String, Object>> listOfIds = (List<HashMap<String, Object>>) response;
        if (!listOfIds.isEmpty()) {
            List<String> successRecords = new ArrayList<>();
            List<String> failedRecords = new ArrayList<>();
            logger.info("Competency update started ..............");
            for (HashMap<String, Object> map : listOfIds) {
                String lex_id = (String) map.get(IDENTIFIER_CONST);
                try {
                    Map<String, Object> contentData = contentCrudService.getContentNode(competency.getRootOrg(), lex_id);
                    Object competencyObject = contentData.get(COMPETENCIES_CONST);
                    if (!ObjectUtils.isEmpty(competencyObject)) {
                        List<HashMap<String, String>> oldCompetencyData = (List<HashMap<String, String>>) competencyObject;
                        List<HashMap<String, String>> newCompetencyData = new ArrayList<>();
                        HashMap<String, String> competencyMap = new HashMap<>();
                        for (HashMap<String, String> data : oldCompetencyData) {
                            if (data.get(ID_CONST).equals(competency.getId())) {
                                competencyMap = new HashMap<>();
                                competencyMap.put(ID_CONST, competency.getId());
                                competencyMap.put(NAME_CONST, competency.getName());
                                competencyMap.put(DESCRIPTION_CONST, competency.getDescription());
                                newCompetencyData.add(competencyMap);
                            } else {
                                newCompetencyData.add(data);
                            }
                        }
                        contentData.put(COMPETENCIES_CONST, newCompetencyData);
                        contentCrudService.updateContentMetaNode(competency.getRootOrg(), "", lex_id, contentData);
                        successRecords.add(lex_id);
                    }
                } catch (Exception ex) {
                    failedRecords.add(lex_id);
                    logger.error(ex.toString());
                }
            }
            if(!successRecords.isEmpty())
            logger.info("Success Records : " + successRecords.toString());
            if(!failedRecords.isEmpty())
            logger.info("Failed Records : " + failedRecords.toString());
            logger.info("Competency update finished ..............");
        }
        return competency;
    }

    /**
     * @param uri
     * @return
     * @throws Exception
     */
    public Object fetchResult(StringBuilder uri) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Object response = null;
        StringBuilder str = new StringBuilder(this.getClass().getCanonicalName()).append(".fetchResult:")
                .append(System.lineSeparator());
        str.append("URI: ").append(uri.toString()).append(System.lineSeparator());
        try {
            String message = str.toString();
            logger.debug(message);
            response = restTemplate.getForObject(uri.toString(), List.class);
        } catch (HttpClientErrorException e) {
            logger.error("External Service threw an Exception: " + e.toString());
        } catch (Exception e) {
            logger.error("Exception while fetching from the external service: " + e.toString());
        }
        return response;
    }
}
