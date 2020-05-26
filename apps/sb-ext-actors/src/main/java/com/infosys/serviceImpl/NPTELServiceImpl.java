/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.cassandra.CassandraOperation;
import com.infosys.elastic.common.ElasticSearchUtil;
import com.infosys.helper.ServiceFactory;
import com.infosys.model.ContentMeta;
import com.infosys.model.CreatorDetails;
import com.infosys.model.Details;
import com.infosys.model.ObjectMeta;
import com.infosys.service.DefaultMetaService;
import com.infosys.service.NPTELService;
import com.infosys.util.LexJsonKey;
import com.infosys.util.LexProjectUtil;
import com.infosys.util.Util;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.sunbird.common.Constants;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NPTELServiceImpl implements NPTELService {

    private static CassandraOperation cassandraOperation = ServiceFactory.getInstance();
    private static PropertiesCache properties = PropertiesCache.getInstance();
    private static String keyspace = properties.getProperty(JsonKey.DB_KEYSPACE);
    private static Util.DbInfo userDb = Util.dbInfoMap.get(JsonKey.USER_DB);

    static {
        Util.checkCassandraDbConnections(keyspace);
    }

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DefaultMetaService defaultMetaService;

    @Override
    public List<String> importCourses(String userId, String disciplineId) {

        List<String> moduleIds = new ArrayList<>();

        //Validating the submitter
        Response cassandraResponse = cassandraOperation.getRecordById(userDb.getKeySpace(), userDb.getTableName(),
                userId);
        List<Map<String, Object>> userDetails = (List<Map<String, Object>>) cassandraResponse.get(Constants.RESPONSE);
        if (userDetails.size() == 0 || userDetails.get(0) == null || userDetails.get(0).isEmpty()) {
            moduleIds.add("UserId is invalid");
            return moduleIds;
        }

        //Preparing user details
        Map<String, Object> user = userDetails.get(0);
        Details submitterDetails = new Details();
        String email = (String) user.get(JsonKey.EMAIL);
        String name = user.get(JsonKey.FIRST_NAME) + " " + user.get(JsonKey.LAST_NAME);

        submitterDetails.setEmail(email);
        submitterDetails.setId(userId);
        submitterDetails.setName(name);

        //Preparing dates and times
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date presentDate = new Date();
        String date = sdf.format(presentDate);

        Number versionKey = presentDate.getTime();

        //Below code is used as the nptel response is not as JSON
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        //Add the Jackson Message converter
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // Note: here we are making this converter to process any kind of response,
        // not only application/*json, which is the default behaviour
        MediaType[] media = {MediaType.ALL};
        converter.setSupportedMediaTypes(Arrays.asList(media));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);

        //Setting the proxy to rest template
        String infy_proxy_host = properties.getProperty("infy_proxy_host");
        int infy_proxy_port = Integer.parseInt(properties.getProperty("infy_proxy_port"));

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(infy_proxy_host, infy_proxy_port));
        requestFactory.setProxy(proxy);
        restTemplate.setRequestFactory(requestFactory);

        //Fetching NPTEL urls
        String nptel_subject_url = properties.getProperty("nptel_subject_url");
        String nptel_url = properties.getProperty("nptel_url");
        String nptel_course_url = properties.getProperty("nptel_course_url");
//        String nptel_download_url = properties.getProperty("nptel_download_url");
        String nptel_discipline_url = properties.getProperty("nptel_discipline_url");

        String youtube = properties.getProperty("youtube_url");
        String youtube_thumbnail = properties.getProperty("youtube_thumbnail_url");

        String lex_artifact_url = properties.getProperty("lex_artifact_url");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<JSONObject> entity = new HttpEntity<JSONObject>(headers);

        Random random = new Random();
        int envId = random.nextInt(99999);

        ResponseEntity<List> nptelDisciplineResponse = restTemplate.exchange(
                nptel_discipline_url + disciplineId, HttpMethod.GET, entity, List.class);

        List<Map<String, Object>> courseList = nptelDisciplineResponse.getBody();

        long totalModule = 0;
        long totalResource = 0;

        for (Map<String, Object> course : courseList) {

            String courseId = (String) course.get("s_id");

            ContentMeta courseData = new ContentMeta();

            courseData.setName((String) course.get("s_name"));

            String transcriptTemp = (String) course.get("c_abs");
            if (transcriptTemp != null && !transcriptTemp.isEmpty())
                courseData.setTranscript(transcriptTemp);

            String durationStr = (String) course.get("h_type");
            if (durationStr != null && !durationStr.isEmpty())
                courseData.setDuration(Long.parseLong(durationStr) * 3600);

            String author_data = course.get("p_data").toString();
            if (author_data != null && !author_data.isEmpty()
                    && !author_data.equals("null") && !author_data.equals("false")) {
                try {
                    List<Map<String, Object>> authors = new ObjectMapper().readValue(author_data, List.class);
                    CreatorDetails[] creators = new CreatorDetails[authors.size()];
                    int count = 0;
                    for (Map<String, Object> author : authors) {
                        creators[count] = new CreatorDetails("", (String) author.get(JsonKey.NAME), "",
                                (String) author.get("About"));
                        count++;
                    }
                    courseData.setCreator(creators[0].getName());
                    courseData.setCreatorDetails(creators);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            String courseImageUrl = "";

            courseData.setIdentifier(LexJsonKey.LEX + "_" + ProjectUtil.getUniqueIdFromTimestamp(envId));
            String desc = "This is " + courseData.getName() + " module";
            courseData.setDescription(desc);
            courseData.setMediaType(LexProjectUtil.MediaType.content.get());
            courseData.setContentType(LexProjectUtil.ContentType.collection.get());
            courseData.setResourceType(LexProjectUtil.ResourceType.module.get());
            courseData.setMimeType(LexProjectUtil.MimeType.collection.get());
            courseData.setSourceName("NPTEL");
            courseData.setSourceShortName("NPTEL");
            courseData.setSourceUrl(nptel_url);
            courseData.setContentIdAtSource(courseId);
            courseData.setContentUrlAtSource(nptel_course_url + courseId);
            courseData.setVersionDate(date);
            courseData.setVersionKey(versionKey);
            courseData.setLastUpdatedOn(date);
            courseData.setLastUpdatedBy(userId);
            courseData.setSubmitterDetails(submitterDetails);
            courseData.setPortalOwner(name);
            courseData.setVisibility(LexProjectUtil.Visibility.defaultVisibility.get());
            courseData.setIsExternal("Yes");
            courseData.setStatus(LexProjectUtil.Status.DRAFT.getValue());

            ObjectMeta parent = ObjectMeta.fromContentMeta(courseData, null,
                    LexProjectUtil.Visibility.defaultVisibility.get());

            ResponseEntity<List> nptelSubjectResponse = restTemplate.exchange(
                    nptel_subject_url + courseId,
                    HttpMethod.GET, entity, List.class);

            List<Map<String, Object>> resourceList = nptelSubjectResponse.getBody();

            if (resourceList != null && !resourceList.isEmpty()) {

                Collections.sort(resourceList, new Comparator<Map<String, Object>>() {
                    public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                        Integer i1 = Integer.parseInt((String) m1.get("lectId"));
                        Integer i2 = Integer.parseInt((String) m2.get("lectId"));
                        return i1.compareTo(i2); //ascending order
                    }
                });

                List<ObjectMeta> children = new ArrayList<>();
                int count = 0;
                String resourceDesc = "This is resource from " + courseData.getName() + " Module";
                for (Map<String, Object> resource : resourceList) {
                    String lectId = (String) resource.get("lectId");
                    String lectName = (String) resource.get("lectName");
                    String lectIdLink = (String) resource.get("lectidlink");
                    String youtubeLink = (String) resource.get("youtubelink");
                    String videoId = "";
                    try {
                        lectIdLink = URLDecoder.decode(lectIdLink, "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        ProjectLogger.log("Invalid url for NPTEL Course " +
                                courseData.getIdentifier() + " Resource" + lectId, lectIdLink, LoggerEnum.INFO.name());
                        lectIdLink = "";
                    }
                    try {
                        youtubeLink = URLDecoder.decode(youtubeLink, "UTF-8");
                        videoId = youtubeLink.substring(youtubeLink.lastIndexOf("/") + 1);
                        youtubeLink = youtube + videoId;
                    } catch (UnsupportedEncodingException ex) {
                        ProjectLogger.log("Invalid url for NPTEL Course " +
                                courseData.getIdentifier() + " Resource" + lectId, youtubeLink, LoggerEnum.INFO.name());
                        youtubeLink = "";
                    }

                    envId = random.nextInt(99999);
                    versionKey = presentDate.getTime();

                    String downloadUrl = "";

                    ContentMeta resourceData = new ContentMeta();
                    resourceData.setIdentifier(LexJsonKey.LEX + "_" + ProjectUtil.getUniqueIdFromTimestamp(envId));
                    resourceData.setName(lectName);
                    resourceData.setDescription(resourceDesc);
                    resourceData.setCreator(courseData.getCreator());
                    resourceData.setCreatorDetails(courseData.getCreatorDetails());
                    resourceData.setMediaType(LexProjectUtil.MediaType.content.get());
                    resourceData.setContentType(LexProjectUtil.ContentType.resource.get());
                    resourceData.setResourceType(LexProjectUtil.ResourceType.lecture.get());
                    resourceData.setMimeType(LexProjectUtil.MimeType.youtube.get());
                    resourceData.setSourceName("NPTEL");
                    resourceData.setSourceShortName("NPTEL");
                    resourceData.setSourceUrl(nptel_url);
                    resourceData.setContentIdAtSource(courseId + "_" + lectId);
                    resourceData.setContentUrlAtSource(lectIdLink);
                    resourceData.setArtifactUrl(youtubeLink);
                    resourceData.setDownloadUrl(downloadUrl);
                    resourceData.setVersionDate(date);
                    resourceData.setVersionKey(versionKey);
                    resourceData.setLastUpdatedOn(date);
                    resourceData.setLastUpdatedBy(userId);
                    resourceData.setVisibility(LexProjectUtil.Visibility.defaultVisibility.get());
                    resourceData.setSubmitterDetails(submitterDetails);
                    resourceData.setPortalOwner(name);
                    resourceData.setIsExternal("Yes");
                    resourceData.setStatus(LexProjectUtil.Status.DRAFT.getValue());

                    String imageUrl = youtube_thumbnail;
                    imageUrl = imageUrl.replaceAll("_videoId", videoId);

                    resourceData.setAppIcon(imageUrl);
                    resourceData.setThumbnail(imageUrl);
                    resourceData.setPosterImage(imageUrl);

                    ObjectMeta[] parentList = new ObjectMeta[]{parent};
                    resourceData.setCollections(parentList);

                    ObjectMeta child = ObjectMeta.fromContentMeta(resourceData, Integer.toString(count),
                            LexProjectUtil.Visibility.defaultVisibility.get());
                    children.add(child);
                    ProjectLogger.log("Adding resource " + resourceData.getName() + " to elasticsearch for " +
                            courseData.getName() + " module");

                    if (count == 0) {
                        courseImageUrl = imageUrl;
                    }

                    //Making nulls as default
                    ContentMeta newResourceData = defaultMetaService.setDefaultToNullValues(resourceData);

                    ElasticSearchUtil.createData(LexProjectUtil.EsIndex.authoring_tool.getIndexName(),
                            LexProjectUtil.EsType.resource.getTypeName(), resourceData.getIdentifier(),
                            newResourceData.toMap(true));
//                    System.out.println(newResourceData.toJson());
                    totalResource += 1;
                    count += 1;
                }
                courseData.setChildren(children.toArray(new ObjectMeta[children.size()]));
                moduleIds.add(courseData.getIdentifier());

                courseData.setAppIcon(courseImageUrl);
                courseData.setThumbnail(courseImageUrl);
                courseData.setPosterImage(courseImageUrl);

                //Making nulls as default
                ContentMeta newCourseData = defaultMetaService.setDefaultToNullValues(courseData);

                ProjectLogger.log("Adding module " + newCourseData.getName());
                ElasticSearchUtil.createData(LexProjectUtil.EsIndex.authoring_tool.getIndexName(),
                        LexProjectUtil.EsType.resource.getTypeName(), courseData.getIdentifier(),
                        newCourseData.toMap(true));
//                System.out.println(newCourseData);
                totalModule += 1;
//                System.out.println("Resources in this module: " + count);
                System.out.println("Total Resource: " + totalResource);
                System.out.println("Total Module: " + totalModule);
            }
        }
        return moduleIds;
    }
}
