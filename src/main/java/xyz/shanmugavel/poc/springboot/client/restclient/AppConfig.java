package xyz.shanmugavel.poc.springboot.client.restclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AppConfig {

    
    @Value("${unsecured.user.endpoint}")
    private String unsecuredUserEndpoint;

    @Bean
    public RestClient unsecuredClient() {
        log.info("unsecuredUserEndpoint={}", unsecuredUserEndpoint);
        return RestClient.builder()
                .baseUrl(unsecuredUserEndpoint)
                .build();

    }

}
