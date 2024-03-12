package xyz.shanmugavel.poc.springboot.client.restclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SecuredClientCredService {

    @Autowired
    @Qualifier("securedClientCredentialsFlowClient")
    private RestClient securedClientCredRestClient;

    public String invokeReadAPI() {
        return securedClientCredRestClient.get()
        .uri("/read")
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->{throw new RuntimeException("Invalid request. 4XX Error");}) 
        .onStatus(HttpStatusCode::is5xxServerError, (request, response) ->{throw new RuntimeException("Server Error. 5XX Error");})
        .body(String.class);
	}
}
