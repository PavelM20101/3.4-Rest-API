package com.pavelm.communication;

import com.pavelm.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class Communication {

    private final String URL = "http://94.198.50.185:7081/api/users";
    private String sessionId;

    private final RestTemplate restTemplate;

    @Autowired
    public Communication(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<User> getAllUsers() {
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(URL, HttpMethod.GET, createHttpEntity(), new ParameterizedTypeReference<List<User>>() {});
        List<User> allUsers = responseEntity.getBody();

        HttpHeaders responseHeaders = responseEntity.getHeaders();
        String contentType = responseHeaders.getFirst(HttpHeaders.CONTENT_TYPE);
        System.out.println("Content-Type header: " + contentType);

        saveSessionId(responseHeaders);

        return allUsers;
    }

//    public User getUser(Long id){
//        User user = restTemplate.getForObject(URL + "/" + id, User.class);
//        return user;
//    }

    /*------------------------------------------------*/
    public void saveUser(User user) {
        HttpHeaders headers = createHttpHeadersWithSessionId();

        Long id = user.getId();
        if (id == null || id == 0) {
            restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(user, headers), String.class);
        } else {
            restTemplate.exchange(URL, HttpMethod.PUT, new HttpEntity<>(user, headers), String.class);
        }
    }
    /*---------------------------------------------*/
    public void createUser(User user){
        HttpHeaders headers = createHttpHeadersWithSessionId();
        restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(user, headers), String.class);
    }
    public void updateUser(User user){
        HttpHeaders headers = createHttpHeadersWithSessionId();
        restTemplate.exchange(URL, HttpMethod.PUT, new HttpEntity<>(user, headers), String.class);
    }

    public void deleteUser(Long id) {
        HttpHeaders headers = createHttpHeadersWithSessionId();

        User userToDelete = new User();
        userToDelete.setId(id);

        try {
            restTemplate.exchange(URL, HttpMethod.DELETE, new HttpEntity<>(userToDelete, headers), String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Error: " + e.getRawStatusCode() + "\n" + e.getResponseBodyAsString());
        }
    }


    private HttpHeaders createHttpHeadersWithSessionId() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "JSESSIONID=" + sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private void saveSessionId(HttpHeaders headers) {
        if (headers.containsKey(HttpHeaders.SET_COOKIE)) {
            List<String> cookies = headers.get(HttpHeaders.SET_COOKIE);
            for (String cookie : cookies) {
                if (cookie.startsWith("JSESSIONID")) {
                    sessionId = cookie.split(";")[0].split("=")[1];
                    break;
                }
            }
        }
    }

    private HttpEntity<Object> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        if (sessionId != null) {
            headers.set("Cookie", "JSESSIONID=" + sessionId);
        }
        return new HttpEntity<>(headers);
    }
    public String performActionAndGetInfo(HttpMethod method, User user) {
        HttpHeaders headers = createHttpHeadersWithSessionId();
        HttpEntity<User> httpEntity = new HttpEntity<>(user, headers);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(URL, method, httpEntity, String.class);
        } catch (HttpClientErrorException e) {
            return "HTTP Status: " + e.getRawStatusCode() + "\nError: " + e.getResponseBodyAsString();
        }

        return "HTTP Status: " + responseEntity.getStatusCode() + "\nResponse Body: " + responseEntity.getBody();
    }
    public String performActionAndGetInfo(HttpMethod method, Long id) {
        HttpHeaders headers = createHttpHeadersWithSessionId();
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(URL + "/" + id, method, httpEntity, String.class);
        } catch (HttpClientErrorException e) {
            return "HTTP Status: " + e.getRawStatusCode() + "\nError: " + e.getResponseBodyAsString();
        }

        return "HTTP Status: " + responseEntity.getStatusCode() + "\nResponse Body: " + responseEntity.getBody();
    }

}
