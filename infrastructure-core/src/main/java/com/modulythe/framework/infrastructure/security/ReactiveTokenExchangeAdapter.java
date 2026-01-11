package com.modulythe.framework.infrastructure.security;

import com.modulythe.framework.application.security.ReactiveTokenExchangePort;
import com.modulythe.framework.application.security.TokenResponse;
import com.modulythe.framework.infrastructure.rest.ReactiveTechnicalRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveTokenExchangeAdapter implements ReactiveTokenExchangePort {

    private static final String GRANT_TYPE = "grant_type";
    private static final String JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    private static final String ASSERTION = "assertion";

    private final ReactiveTechnicalRestClient restClient;
    private final String tokenEndpoint;

    public ReactiveTokenExchangeAdapter(
            ReactiveTechnicalRestClient restClient,
            @Value("${modulythe.security.token-endpoint:/oauth/token}") String tokenEndpoint) {
        this.restClient = restClient;
        this.tokenEndpoint = tokenEndpoint;
    }

    @Override
    public Mono<TokenResponse> exchange(String assertion) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(GRANT_TYPE, JWT_BEARER);
        body.add(ASSERTION, assertion);

        return restClient.postData(
                tokenEndpoint,
                null,
                body,
                TokenResponse.class
        ).map(responseEntity -> {
            assert responseEntity.getBody() != null;
            return responseEntity.getBody();
        });
    }
}
