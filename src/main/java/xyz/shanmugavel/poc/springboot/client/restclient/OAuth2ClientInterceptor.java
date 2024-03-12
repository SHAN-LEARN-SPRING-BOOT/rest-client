package xyz.shanmugavel.poc.springboot.client.restclient;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OAuth2ClientInterceptor implements ClientHttpRequestInterceptor, ClientHttpRequestInitializer{

    private final OAuth2AuthorizedClientManager manager;
    private final ClientRegistration clientRegistration;

    public OAuth2ClientInterceptor(OAuth2AuthorizedClientManager manager, ClientRegistration clientRegistration) {
        this.manager = manager;
        this.clientRegistration = clientRegistration;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String bearerToken = getBearerToken();
        request.getHeaders().setBearerAuth(bearerToken);
        log.info("Added BearerToken['{}'] via intercept!!!", bearerToken);
        return execution.execute(request, body);
    }

    @Override
    public void initialize(ClientHttpRequest request) {
        String bearerToken = getBearerToken();
        request.getHeaders().setBearerAuth(bearerToken);
        log.info("Added BearerToken['{}'] via initialize!!!", bearerToken);
    }


    private String getBearerToken() {
        OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientRegistration.getRegistrationId())
                .principal(clientRegistration.getClientId())
                .build();

        OAuth2AuthorizedClient client = manager.authorize(oAuth2AuthorizeRequest);
        log.info("Client RegisrationId: {}", clientRegistration.getRegistrationId());
        return client.getAccessToken().getTokenValue();
    }
}
