package xyz.shanmugavel.poc.springboot.client.restclient.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import xyz.shanmugavel.poc.springboot.client.restclient.model.User;

@Service
public class UnsecuredUserService {
    
    @Autowired
     @Qualifier("unsecuredClient")
    private RestClient unsecuredRestClient;


    public List<User> getAllUsers() {
        return unsecuredRestClient.get()
        .uri("/users")
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->{throw new RuntimeException("Invalid request. 4XX Error");}) 
        .onStatus(HttpStatusCode::is5xxServerError, (request, response) ->{throw new RuntimeException("Server Error. 5XX Error");})
        .body(new ParameterizedTypeReference<List<User>>() {});
	}
}
