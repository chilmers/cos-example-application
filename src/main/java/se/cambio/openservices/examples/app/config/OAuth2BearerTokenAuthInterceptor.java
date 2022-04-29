package se.cambio.openservices.examples.app.config;

import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

/**
 * HAPI FHIR Client Interceptor that uses Spring's OAuth 2.0 support to decorate the request with an access token.
 */
public class OAuth2BearerTokenAuthInterceptor extends BearerTokenAuthInterceptor {

    private static final Logger log = LoggerFactory.getLogger(OAuth2BearerTokenAuthInterceptor.class);
    /**
     * Used for handing off token management to Spring's OAuth 2.0 mechanisms
     */
    private final OAuth2AuthorizedClientManager oauthClientManager;

    /**
     * The registration-id that is used for our COS client in the Spring config
     * I.e. cambio-open-services-client
     */
    private final String clientRegistrationId;

    public OAuth2BearerTokenAuthInterceptor(OAuth2AuthorizedClientManager oauthClientManager, String clientRegistrationId) {
        super();
        this.oauthClientManager = oauthClientManager;
        this.clientRegistrationId = clientRegistrationId;
    }

    /**
     * Intercepts each request to Cambio Open Services and attaches an OAuth 2.0 token.
     * Logs some information about the request and the received token with level trace.
     * @param theRequest Representation of the request, will be provided by the HAPI FHIR filtering chain
     */
    @Override
    public void interceptRequest(ca.uhn.fhir.rest.client.api.IHttpRequest theRequest) {
        log.trace("REQUEST {}: {}", theRequest.getHttpVerbName(), theRequest.getUri());
        log.trace("GETTING OAUTH TOKEN");
        OAuth2AuthorizedClient authorizedClient = this.oauthClientManager.authorize(
            OAuth2AuthorizeRequest.withClientRegistrationId(this.clientRegistrationId)
                // Since we use client-credentials, we don't have a specific user associated with the OAuth request.
                // Instead, we use the client registration id as principal
                .principal(new UsernamePasswordAuthenticationToken(this.clientRegistrationId, "")).build()
        );

        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            log.trace("ATTACHING TOKEN: {}", authorizedClient.getAccessToken().getTokenValue());
            this.setToken(authorizedClient.getAccessToken().getTokenValue());
        } else {
            log.warn("Failed to attach access token to the request");
        }

        super.interceptRequest(theRequest);
    }

    /**
     * Logs (to trace) the response status and headers from Cambio Open Services
     * @param theResponse Representation of the response, will be provided by the HAPI FHIR filtering chain
     */
    @Override
    public void interceptResponse(IHttpResponse theResponse) {
        log.trace("RESPONSE status: {} {}", theResponse.getStatus(), theResponse.getStatusInfo());
        theResponse.getAllHeaders().forEach((key, values) -> {
            StringBuilder sb = new StringBuilder(key).append(": ");
            log.trace(sb.toString());
        });
        super.interceptResponse(theResponse);
    }
}
