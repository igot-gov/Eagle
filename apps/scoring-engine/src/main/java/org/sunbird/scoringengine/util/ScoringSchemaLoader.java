

package org.sunbird.scoringengine.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.sunbird.scoringengine.schema.model.ScoringSchema;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;

@Component
public class ScoringSchemaLoader {


    private ScoringSchema scoringSchema;

    @PostConstruct
    public void load() throws Exception {

        ClassPathResource classPathResource = new ClassPathResource("ScoringSchema.json");

        InputStream inputStream = classPathResource.getInputStream();

        File tempFile = File.createTempFile("ScoringSchema", ".json");
        try {
            FileUtils.copyInputStreamToFile(inputStream, tempFile);
            ObjectMapper objectMapper = new ObjectMapper();
            this.scoringSchema = objectMapper.readValue(tempFile, ScoringSchema.class);

        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public ScoringSchema getScoringSchema() {
        return scoringSchema;
    }
}
