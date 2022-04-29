package se.cambio.openservices.examples.app.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.apache.GZipContentInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.AdditionalRequestHeadersInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

/**
 * Spring context configuration holding configuration for generation of FHIR clients.
 */
@Configuration
public class FhirClientConfig {

    /**
     * The base URL of the API
     * Fetched from application.yaml, system properties or environment variables.
     * Defaults to https://api.openservices.camibo.se if no configured value was found.
     */
    @Value("${cambioopenservices.baseUrl:https://api.openservices.camibo.se}")
    private String apiBaseUrl;

    /**
     * The API key (subscription key) for Cambio Open Services
     * Fetched from application.yaml, system properties or environment variables.
     * No default value.
     */
    @Value("${cambioopenservices.apiKey}")
    private String apiKey;

    /**
     * The client registration id used by Spring to identify our COS client.
     * Must match the name of the client registration entry in the application.yaml
     */
    private static final String CLIENT_REGISTRATION_ID = "cambio-open-services-client";

    /**
     * Configuration of Spring's OAuth 2 client manager
     * @param clientRegistrationRepository will be autowired with the clientRegistrationRepository bean from the spring context
     * @param authorizedClientRepository will be autowired with the authorizedClientRepository bean from the spring context
     * @return An instance to be used as a Spring bean of type OAuth2AuthorizedClientManager
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
            OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        var authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            authorizedClientRepository);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    /**
     * FhirClient factory method
     * Will be used when an instance of type IGenericClient is requested from the Spring Context.
     * E.g. when using the @Autowired annotation on a member variable of a class within the Spring context.
     * @return A configured instance of a IGenericClient (i.e. a fhir client)
     */
    @Bean
    IGenericClient getFhirClient(OAuth2AuthorizedClientManager oauthClientManager){
        // Create a Fhir R4 context
        var ctx = FhirContext.forR4();

        // Get the client factory so it can be configured
        var clientFactory = ctx.getRestfulClientFactory();
        // Set how long to try and establish the initial TCP connection (in ms)
        clientFactory.setConnectTimeout(20 * 1000);
        // Set how long to block for individual read/write operations (in ms)
        clientFactory.setSocketTimeout(20 * 1000);

        // Create a generic fhir client
        IGenericClient genericClient = ctx.newRestfulGenericClient(apiBaseUrl + "/fhir");

        // Add an interceptor that gzips the request to minimize the network traffic
        genericClient.registerInterceptor(new GZipContentInterceptor());

        // Add an interceptor that adds the API Key header (Ocp-Apim-Subscription-Key)
        AdditionalRequestHeadersInterceptor interceptor = new AdditionalRequestHeadersInterceptor();
        interceptor.addHeaderValue("Ocp-Apim-Subscription-Key", apiKey);
        genericClient.registerInterceptor(interceptor);

        // Add the OAuth2 interceptor to (fetch and) attach an OAuth token to each request
        genericClient.registerInterceptor(new OAuth2BearerTokenAuthInterceptor(oauthClientManager, CLIENT_REGISTRATION_ID));

        return genericClient;
    }

}
