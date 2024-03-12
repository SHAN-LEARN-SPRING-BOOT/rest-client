package xyz.shanmugavel.poc.springboot.client.restclient;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AppConfig {

    
    @Value("${unsecured.user.endpoint}")
    private String unsecuredUserEndpoint;

    @Value("${secured.oauth.client-cred.endpoint}")
    private String securedClientCredEndpoint;

    @Bean
    @Primary
    @Qualifier("unsecuredClient")
    public RestClient unsecuredClient() {
        log.info("unsecuredUserEndpoint={}", unsecuredUserEndpoint);
        return RestClient.builder()
                .baseUrl(unsecuredUserEndpoint)
                .build();

    }

    @Bean
    @Qualifier("securedClientCredentialsFlowClient")
    RestClient restClientPassword(RestClient.Builder builder,
                                  OAuth2AuthorizedClientManager authorizedClientManager,
                                  ClientRegistrationRepository clientRegistrationRepository) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("client-cred");

        ClientHttpRequestInterceptor interceptor = new OAuth2ClientInterceptor(authorizedClientManager, clientRegistration);
        return builder.baseUrl(securedClientCredEndpoint).requestInterceptor(interceptor).build();
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
        OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> responseClient,
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientService clientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
            OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials(clientCredentials ->
                    clientCredentials.accessTokenResponseClient(responseClient))
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager clientManager =
            new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, clientService);
        clientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return clientManager;
    }


    @Bean
    JWKSource<SecurityContext> jwkSource() {
        JWK key = JwkUtils.generateEc();
        JWKSet jwkSet = new JWKSet(key);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    Function<ClientRegistration, JWK> jwkResolver(JWKSource<SecurityContext> jwkSource) {
        JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder().privateOnly(true).build());
        return (registration) -> getJwk(jwkSource, jwkSelector);
    }

    private JWK getJwk(JWKSource<SecurityContext> jwkSource, JWKSelector jwkSelector) {
        JWKSet jwkSet = null;
        try {
            jwkSet = new JWKSet(jwkSource.get(jwkSelector, null));
        } catch (KeySourceException ex) {
            log.error("cannot locate private key", ex);
        }
        return jwkSet != null ? jwkSet.getKeys().iterator().next() : null;
    }
    
    @Bean
    OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> clientCredentialsTokenResponseClient(
            Function<ClientRegistration, JWK> jwkResolver) {

        OAuth2ClientCredentialsGrantRequestEntityConverter clientCredentialsGrantRequestEntityConverter = new OAuth2ClientCredentialsGrantRequestEntityConverter();
        clientCredentialsGrantRequestEntityConverter.addParametersConverter(new NimbusJwtClientAuthenticationParametersConverter<>(jwkResolver));

        clientCredentialsGrantRequestEntityConverter.addParametersConverter(authorizationGrantRequest -> {
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add(OAuth2ParameterNames.CLIENT_ID, authorizationGrantRequest.getClientRegistration().getClientId());
            return parameters;
        });

        DefaultClientCredentialsTokenResponseClient clientCredentialsTokenResponseClient = new DefaultClientCredentialsTokenResponseClient();
        clientCredentialsTokenResponseClient.setRequestEntityConverter(clientCredentialsGrantRequestEntityConverter);

        return clientCredentialsTokenResponseClient;
    }

}
