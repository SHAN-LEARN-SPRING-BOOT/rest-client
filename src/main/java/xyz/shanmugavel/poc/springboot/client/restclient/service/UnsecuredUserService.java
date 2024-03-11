package xyz.shanmugavel.poc.springboot.client.restclient.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import xyz.shanmugavel.poc.springboot.client.restclient.model.User;

@Service
public class UnsecuredUserService {
    
    @Autowired
    private RestClient unsecuredRestClient;


    public List<User> getAllUsers() {
        return unsecuredRestClient.get()
        .uri("/users")
        .retrieve()
        .body(new ParameterizedTypeReference<List<User>>() {});
	}
}
