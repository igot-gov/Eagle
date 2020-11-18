/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.profile.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class util {

    public static void main(String[] args) {

        try {

            String searchJson = "{\"skills\":{\"osid\":\"aa4a4a77-ae04-433f-972e-a854eae50f32\",\"certificateDetails\":\"certificate details ...\",\"additionalSkills\":\"Skills acquired\",\"@type\":\"skills\",\"_osroot\":\"0bdd1dba-3fc9-46cf-a683-d853b6cdcf15\",\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"osCreatedBy\":\"\"},\"employmentDetails\":{\"osid\":\"effbf698-87d9-4139-8810-dacfc4ebf5f8\",\"allotmentYearOfService\":\"\",\"payType\":\"\",\"civilListNo\":\"\",\"dojOfService\":\"\",\"service\":\"\",\"officialPostalAddress\":\"Postal Address here along \",\"pinCode\":\"000011\",\"cadre\":\"\",\"employeeCode\":\"null\",\"@type\":\"employmentDetails\",\"_osroot\":\"0bdd1dba-3fc9-46cf-a683-d853b6cdcf15\",\"departmentName\":\"\",\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"osCreatedBy\":\"\"},\"personalDetails\":{\"osid\":\"93ff4620-5c31-456d-a7ed-7e2579454517\",\"pincode\":\"560067\",\"firstname\":\"Pritha\",\"gender\":\"Female\",\"domicileMedium\":\"Bengali\",\"mobile\":1234567890,\"middlename\":\"\",\"telephone\":\"\",\"knownLanguages\":[\"English\",\"Hindi\",\"Bengali\"],\"personalEmail\":\"\",\"postalAddress\":\"Bangalore, HSR, KA \",\"nationality\":\"India\",\"countryCode\":\"+91\",\"surname\":\"Chattopadhyay\",\"dob\":\"01-02-2016\",\"category\":\"General\",\"primaryEmail\":\"pritha.chattopadhyay@tarento.com\",\"officialEmail\":\"pritha.chattopadhyay@tarento.com\",\"maritalStatus\":\"Single\",\"@type\":\"personalDetails\",\"_osroot\":\"0bdd1dba-3fc9-46cf-a683-d853b6cdcf15\",\"osUpdatedBy\":\"\",\"osCreatedBy\":\"\",\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\"},\"osid\":\"0bdd1dba-3fc9-46cf-a683-d853b6cdcf15\",\"@id\":\"f3834e1b-315a-4de2-88a7-895ce0b7f46d\",\"id\":\"f3834e1b-315a-4de2-88a7-895ce0b7f46d\",\"professionalDetails\":[{\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"description\":\"\",\"industry\":\"\",\"designationOther\":\"\",\"osid\":\"ac91dbc8-7a18-41ed-b199-11d3c1a68468\",\"nameOther\":\"Tarento pvt ltd\",\"organisationType\":\"Non-Government\",\"responsibilities\":\"\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"name\":\"Other\",\"osCreatedBy\":\"\",\"location\":\"\",\"designation\":\"\",\"industryOther\":\"\",\"completePostalAddress\":\"\",\"doj\":\"\",\"additionalAttributes\":{\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"osCreatedBy\":\"\",\"osid\":\"d589b5c3-2b88-439c-8cc5-ae3de3a75f3d\"}}],\"academics\":[{\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"nameOfQualification\":\"\",\"yearOfPassing\":\"\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"osCreatedBy\":\"\",\"nameOfInstitute\":\"\",\"osid\":\"971d6c67-7135-48cf-87b8-1682e1c28d03\",\"type\":\"X_STANDARD\"},{\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"nameOfQualification\":\"\",\"yearOfPassing\":\"\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"osCreatedBy\":\"\",\"nameOfInstitute\":\"\",\"osid\":\"f609604f-3dec-45a2-8517-86f74c3d465c\",\"type\":\"XII_STANDARD\"},{\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"nameOfQualification\":\"\",\"yearOfPassing\":\"\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"osCreatedBy\":\"\",\"nameOfInstitute\":\"\",\"osid\":\"507322c4-c0c2-4a0e-afaa-299d4b49e2b7\",\"type\":\"GRADUATE\"},{\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"nameOfQualification\":\"\",\"yearOfPassing\":\"\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"osCreatedBy\":\"\",\"nameOfInstitute\":\"\",\"osid\":\"da1a3ed1-89a0-4d10-8b95-487c16dd2bd1\",\"type\":\"POSTGRADUATE\"},{\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"nameOfQualification\":\"Master of Advanced Study\",\"yearOfPassing\":\"2016\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"osCreatedBy\":\"\",\"nameOfInstitute\":\"Institute name\",\"osid\":\"24ff31d3-b65d-49a1-8fb4-a95d54d0f025\",\"type\":\"POSTGRADUATE\"},{\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"nameOfQualification\":\"\",\"yearOfPassing\":\"\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"osCreatedBy\":\"\",\"nameOfInstitute\":\"\",\"osid\":\"f80b6275-7c2f-4ee9-a411-3d0c8b66dda4\",\"type\":\"POSTGRADUATE\"}],\"interests\":{\"osid\":\"b9769c40-5179-4bda-9fd7-53d552ad3c4c\",\"hobbies\":[\"music\"],\"professional\":[\"tech\",\"AI\",\"Data engr\"],\"@type\":\"interests\",\"_osroot\":\"0bdd1dba-3fc9-46cf-a683-d853b6cdcf15\",\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"osCreatedBy\":\"\"},\"osUpdatedAt\":\"2020-11-17T12:55:03.305Z\",\"osUpdatedBy\":\"\",\"@type\":\"UserProfile\",\"osCreatedAt\":\"2020-11-17T12:55:03.305Z\",\"osCreatedBy\":\"\",\"competencies\":[{\"osUpdatedAt\":\"2020-11-16T15:23:10.975Z\",\"osUpdatedBy\":\"\",\"_osroot\":\"0bdd1dba-3fc9-46cf-a683-d853b6cdcf15\",\"@type\":\"competencies\",\"description\":\"Control project plans to manage project schedule and deliverables\",\"source\":\"\",\"osid\":\"63a60893-c29f-404e-a6be-b64add26a0da\",\"type\":\"COMPETENCY\",\"competencyType\":\"Behavioural\",\"osCreatedAt\":\"2020-11-16T15:23:10.975Z\",\"name\":\"Conceptual Thinking\",\"osCreatedBy\":\"\",\"id\":\"CID011\",\"status\":\"VERIFIED\"},{\"osUpdatedAt\":\"2020-11-16T15:23:10.975Z\",\"osUpdatedBy\":\"\",\"_osroot\":\"5b7e20ff-f08c-4149-bfc2-0d452c56ad57\",\"@type\":\"competencies\",\"description\":\"Handling phone calls efficiently.  Knowledge about office activities and procedures\",\"source\":\"\",\"osid\":\"5b7e20ff-f08c-4149-bfc2-0d452c56ad57\",\"type\":\"COMPETENCY\",\"competencyType\":\"Domain\",\"osCreatedAt\":\"2020-11-16T15:23:10.975Z\",\"name\":\"Handling Phone Calls\",\"osCreatedBy\":\"\",\"id\":\"CID016\",\"status\":\"VERIFIED\"}]}";

                    //"{\"personalDetails\":{\"osid\":\"1-87aa144d-b4cf-4e48-a0d6-bfce1cdcd889\",\"firstname\":\"ibrahim-S\",\"gender\":\"Male\",\"osUpdatedBy\":\"\"}}";
            String inputJson ="{\"personalDetails\":{\"firstname\":\"Pritha01\",\"middlename\":\"\",\"surname\":\"Chattopadhyay\",\"dob\":\"01-02-2016\",\"nationality\":\"India\",\"domicileMedium\":\"Bengali\",\"gender\":\"Female\",\"maritalStatus\":\"Single\",\"category\":\"General\",\"knownLanguages\":[\"English\",\"Hindi\",\"Bengali\"],\"countryCode\":\"+91\",\"mobile\":1234567890,\"telephone\":\"\",\"primaryEmail\":\"pritha.chattopadhyay@tarento.com\",\"officialEmail\":\"pritha.chattopadhyay@tarento.com\",\"personalEmail\":\"\",\"postalAddress\":\"Bangalore, HSR, KA \",\"pincode\":\"560067\"},\"academics\":[{\"nameOfQualification\":\"\",\"type\":\"X_STANDARD\",\"nameOfInstitute\":\"10th institute\",\"yearOfPassing\":\"\"},{\"nameOfQualification\":\"\",\"type\":\"XII_STANDARD\",\"nameOfInstitute\":\"\",\"yearOfPassing\":\"\"},{\"nameOfQualification\":\"\",\"type\":\"GRADUATE\",\"nameOfInstitute\":\"\",\"yearOfPassing\":\"\"},{\"nameOfQualification\":\"\",\"type\":\"POSTGRADUATE\",\"nameOfInstitute\":\"\",\"yearOfPassing\":\"\"},{\"nameOfQualification\":\"Master of Advanced Study\",\"type\":\"POSTGRADUATE\",\"nameOfInstitute\":\"Institute name\",\"yearOfPassing\":\"2016\"},{\"nameOfQualification\":\"\",\"type\":\"POSTGRADUATE\",\"nameOfInstitute\":\"\",\"yearOfPassing\":\"\"}],\"employmentDetails\":{\"service\":\"\",\"cadre\":\"\",\"allotmentYearOfService\":\"\",\"dojOfService\":\"\",\"payType\":\"\",\"civilListNo\":\"\",\"employeeCode\":\"null\",\"officialPostalAddress\":\"Postal Address here along \",\"pinCode\":\"000000\",\"departmentName\":\"\"},\"professionalDetails\":[{\"organisationType\":\"Non-Government\",\"name\":\"Other\",\"nameOther\":\"Tarento pvt ltd\",\"industry\":\"\",\"industryOther\":\"\",\"designation\":\"\",\"designationOther\":\"\",\"location\":\"\",\"responsibilities\":\"\",\"doj\":\"\",\"description\":\"\",\"completePostalAddress\":\"\",\"additionalAttributes\":{}}],\"skills\":{\"additionalSkills\":\"Skills acquired\",\"certificateDetails\":\"certificate details ...\"},\"interests\":{\"professional\":[\"tech\",\"AI\"],\"hobbies\":[\"music\"]}}";

                    //"{\"personalDetails\":{\"firstname\":\"ibrahim\",\"gender\":\"Male\",\"genderA\":\"MaleA\",\"osUpdatedBy\":\"value\"}}";


            HashMap<String, Object> map1=new ObjectMapper().readValue(searchJson, new TypeReference<HashMap<String, Object>>() {});
            HashMap<String, Object> map2 = new ObjectMapper().readValue(inputJson, new TypeReference<HashMap<String, Object>>() {});
            //Map-1

            //Merging Map-1 and Map-2 into Map-3
            //If any two keys are found same, largest value will be selected
            merge(map1, map2);


            System.out.println("Merged map : "+new ObjectMapper().writeValueAsString(map1));

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void merge(Map<String,Object> mapLeft, Map<String,Object> mapRight) {
        // go over all the keys of the right map
        for (String key : mapRight.keySet()) {

            Object ml = mapLeft.get(key);
            Object mr = mapRight.get(key);
            // if the left map already has this key, merge the maps that are behind that key
            if (mapLeft.containsKey(key) && ml instanceof HashMap) {
                merge((Map<String, Object>) ml, (Map<String, Object>) mr);

            } else if(mapLeft.containsKey(key) && !(mapLeft.get(key) instanceof HashMap)){
                mapLeft.put(key, mapRight.get(key));
            }else {
                // otherwise just add the map under that key
                mapLeft.put(key, mapRight.get(key));
            }
        }
    }

    public static void merge(String entityTypeJsonPtr, ObjectNode result, ObjectNode inputNode, List<String> ignoreFields) {
        inputNode.fields().forEachRemaining(prop -> {
            String propKey = prop.getKey();
            JsonNode propValue = prop.getValue();

            if ((propValue.isValueNode() && !ignoreFields.contains(propKey)) ||
                    propValue.isArray()) {
                // Must be a value node and not a uuidPropertyName key pair
                //((ObjectNode)result.get(entityType)).set(propKey, propValue);
                getNode(result, entityTypeJsonPtr).set(propKey, propValue);
            } else if (propValue.isObject()) {
                if (result.at(entityTypeJsonPtr + "/" + propKey).isMissingNode()) {
                    ((ObjectNode) result.at(entityTypeJsonPtr)).set(propKey, propValue);
                } else {
                    merge(entityTypeJsonPtr + "/" + propKey, result, (ObjectNode) propValue, ignoreFields);
                }
            }
        });
    }

    private static ObjectNode getNode(ObjectNode inputNode, String key) {
        JsonNode result = inputNode.at(key);
        if (result.isMissingNode()) {
            return getNode(inputNode, key.substring(0, key.lastIndexOf("/")));
        }
        return (ObjectNode) result;
    }

    private void ex() throws Exception{
        String searchJson = "{\"personalDetails\":{\"osid\":\"1-87aa144d-b4cf-4e48-a0d6-bfce1cdcd889\",\"firstname\":\"ibrahim-S\",\"gender\":\"Male\",\"osUpdatedBy\":\"\"}}";
        String inputJson ="{\"personalDetails\":{\"firstname\":\"ibrahim\",\"gender\":\"Male\",\"genderA\":\"MaleA\",\"osUpdatedBy\":\"value\"}}";


        Map<String, Object> v1=new ObjectMapper().readValue(searchJson, new TypeReference<HashMap<String, Object>>() {});
        Map<String, Object> v2=new ObjectMapper().readValue(inputJson, new TypeReference<HashMap<String, Object>>() {});
/*
        Stream combined = Stream.concat(v1.entrySet().stream(), v2.entrySet().stream());

        Map<String, Object> result = combined.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println("result "+result);*/
    }




}
