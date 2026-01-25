package com.modulythe.framework.infrastructure.security;

import com.modulythe.framework.application.security.TokenExchangePort;
import com.modulythe.framework.application.security.TokenResponse;
import com.modulythe.framework.infrastructure.rest.ServletTechnicalRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletTokenExchangeAdapter implements TokenExchangePort {

    private static final String GRANT_TYPE = "grant_type";
    private static final String JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    private static final String ASSERTION = "assertion";

    private final ServletTechnicalRestClient restClient;
    private final String tokenEndpoint;

    public ServletTokenExchangeAdapter(
            ServletTechnicalRestClient restClient,
            @Value("${modulythe.security.token-endpoint:/oauth/token}") String tokenEndpoint) {
        this.restClient = restClient;
        this.tokenEndpoint = tokenEndpoint;
    }

    @Override
    public TokenResponse exchange(String assertion) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(GRANT_TYPE, JWT_BEARER);
        body.add(ASSERTION, assertion);

        return restClient.postData(
                tokenEndpoint,
                null,
                body,
                TokenResponse.class
        ).getBody();
    }
}
