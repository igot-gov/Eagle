package com.infosys.lexauthoringservices.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.infosys.lexauthoringservices.service.OutBoundRequestService;
import com.infosys.lexauthoringservices.util.LexLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OutBoundRequestServiceImpl implements OutBoundRequestService {

    @Autowired
    private LexLogger logger;

    @Autowired
    private RestTemplate restTemplate;

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
