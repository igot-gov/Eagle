package org.sunbird.progress.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.sunbird.core.logger.CbExtLogger;
import org.sunbird.progress.cassandraRepo.MandatoryContentModel;
import org.sunbird.progress.cassandraRepo.MandatoryContentRepository;
import org.sunbird.progress.model.MandatoryContentInfo;
import org.sunbird.progress.model.MandatoryContentResponse;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MandatoryContentServiceImpl implements MandatoryContentService {

    @Autowired
    private MandatoryContentRepository mandatoryContentRepository;

    private CbExtLogger logger = new CbExtLogger(getClass().getName());


    @Override
    public MandatoryContentResponse getMandatoryContentStatusForUser(String rootOrg, String org, String userId) {
        MandatoryContentResponse response = new MandatoryContentResponse();
        List<MandatoryContentModel> contentList = mandatoryContentRepository.getMandatoryContentsInfo(rootOrg, org);
        if (CollectionUtils.isEmpty(contentList)) {
            logger.info("getMandatoryContentStatusForUser: There are no mandatory Content set in DB.");
            return response;
        }
        for (MandatoryContentModel content : contentList) {
            MandatoryContentInfo info = new MandatoryContentInfo();
            info.setRootOrg(rootOrg);
            info.setOrg(org);
            info.setContentType(content.getContentType());
            info.setMinProgressForCompletion(content.getMinProgressCheck());
            response.addContentInfo(content.getPrimaryKey().getContent_id(), info);
        }
        List<String> contentIds = contentList.stream().map(content -> content.getPrimaryKey().getContent_id())
                .collect(Collectors.toList());
        try {
            logger.info("getMandatoryContentStatusForUser: MandatoryCourse Details : " + new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(response));
        } catch (JsonProcessingException e) {
            logger.error(e);
        }
        try {
            //TO-DO
            // Call the progess API and get the progress
        } catch (Exception e) {
            logger.error(e);
            return response;
        }
        try {
            logger.info("getMandatoryContentStatusForUser: Ret Value is: " + new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(response));
        } catch (JsonProcessingException e) {
            logger.error(e);
        }
        // Update the response object for course completion
        Iterator<MandatoryContentInfo> entries = response.getContentDetails().values().iterator();
        boolean isCompleted = false;
        while (entries.hasNext()) {
            MandatoryContentInfo entry = entries.next();
            if (entry.getUserProgress() < entry.getMinProgressForCompletion()) {
                response.setMandatoryCourseCompleted(false);
                isCompleted = false;
                break;
            } else {
                isCompleted = true;
            }
        }
        if (isCompleted) {
            response.setMandatoryCourseCompleted(true);
        }
        return response;
    }
}
