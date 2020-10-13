package com.infosys.lexauthoringservices.serviceimpl;

import com.infosys.lexauthoringservices.model.Competency;
import com.infosys.lexauthoringservices.producer.Producer;
import com.infosys.lexauthoringservices.service.CompetencyService;
import com.infosys.lexauthoringservices.service.ContentCrudService;
import com.infosys.lexauthoringservices.util.LexLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

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
    private OutBoundRequestServiceImpl outBoundRequestService;

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
        Object response = outBoundRequestService.fetchResult(builder);
        if (ObjectUtils.isEmpty(response))
            return competency;
        List<HashMap<String, Object>> listOfIds = (List<HashMap<String, Object>>) response;

        if (listOfIds.isEmpty())
            return competency;
        List<String> successRecords = new ArrayList<>();
        List<String> failedRecords = new ArrayList<>();
        logger.info("Competency update started ..............");
        for (HashMap<String, Object> map : listOfIds) {
            String lex_id = (String) map.get(IDENTIFIER_CONST);
            try {
                Map<String, Object> contentData = contentCrudService.getContentNode(competency.getRootOrg(), lex_id);
                Object competencyObject = contentData.get(COMPETENCIES_CONST);
                if (ObjectUtils.isEmpty(competencyObject)) {
                    failedRecords.add(lex_id);
                    continue;
                }
                List<HashMap<String, String>> competencyData = (List<HashMap<String, String>>) competencyObject;
                for (HashMap<String, String> data : competencyData) {
                    if (data.get(ID_CONST).equals(competency.getId())) {
                        data.put(NAME_CONST, competency.getName());
                        data.put(DESCRIPTION_CONST, competency.getDescription());
                    }
                }
                contentData.put(COMPETENCIES_CONST, competencyData);
                contentCrudService.updateContentMetaNode(competency.getRootOrg(), "", lex_id, contentData);
                successRecords.add(lex_id);
            } catch (Exception ex) {
                failedRecords.add(lex_id);
                logger.error(ex.toString());
            }
        }
        if (!successRecords.isEmpty())
            logger.info("Success Records : " + successRecords.toString());
        if (!failedRecords.isEmpty())
            logger.info("Failed Records : " + failedRecords.toString());
        logger.info("Competency update finished ..............");
        return competency;
    }
}
