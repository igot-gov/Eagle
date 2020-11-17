

package org.eagle.hubservice.profile.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/v1/user")
public class UserProfileController {

    @Autowired
    RestTemplate restTemplate;

    @Value(value = "${registry.base.url}")
    String baseUrl;


    @PostMapping("/create/profile")
    public ResponseEntity createProfile(@RequestParam String userId, @RequestBody Map<String,Object> request) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        request.put("userId", userId);
        request.put("id", userId);

         RegistryRequest registryRequest = new RegistryRequest();
         registryRequest.setId(ProfileConstants.API.CREATE.getValue());
         registryRequest.getRequest().put("UserProfile", request);
        //request entity is created with request headers
        HttpEntity<RegistryRequest> requestEntity = new HttpEntity<>(registryRequest, requestHeaders);

        ResponseEntity responseEntity = restTemplate.exchange(
                baseUrl + ProfileConstants.URL.CREATE.getValue(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getStatusCode());
    }

    @PostMapping("/update/profile")
    public ResponseEntity updateProfile(@RequestBody Map<String,Object> request) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        //TODO: do a merge on request and with osid(s).

        RegistryRequest registryRequest = new RegistryRequest();
        registryRequest.setId(ProfileConstants.API.UPDATE.getValue());
        registryRequest.getRequest().put("UserProfile", request);
        HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(request, requestHeaders);

        ResponseEntity responseEntity = restTemplate.exchange(
                baseUrl + ProfileConstants.URL.UPDATE.getValue(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getStatusCode());
    }

    @PostMapping("/search/profile")
    public ResponseEntity searchProfile(@RequestParam String userId) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        List types = Arrays.asList("UserProfile");
        Map<String, Map<String, Object>> filters = new HashMap<>();
        filters.put("id", Stream.of(new AbstractMap.SimpleEntry<>("eq", userId)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));


        RegistryRequest registryRequest = new RegistryRequest();
        registryRequest.setId(ProfileConstants.API.SEARCH.getValue());
        registryRequest.getRequest().put("entityType", types);
        registryRequest.getRequest().put("filters", filters);

        HttpEntity<RegistryRequest> requestEntity = new HttpEntity<>(registryRequest, requestHeaders);

        ResponseEntity responseEntity = restTemplate.exchange(
                baseUrl + ProfileConstants.URL.READ.getValue(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getStatusCode());
    }

    @GetMapping("/get/profile")
    public ResponseEntity getProfile(@RequestParam String osid) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> params = Stream.of(new AbstractMap.SimpleEntry<>("osid", osid)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        RegistryRequest registryRequest = new RegistryRequest();
        registryRequest.setId(ProfileConstants.API.READ.getValue());
        registryRequest.getRequest().put("UserProfile", params);

        HttpEntity<RegistryRequest> requestEntity = new HttpEntity<>(registryRequest, requestHeaders);

        ResponseEntity responseEntity = restTemplate.exchange(
                ProfileConstants.URL.READ.getValue(),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getStatusCode());
    }

}
