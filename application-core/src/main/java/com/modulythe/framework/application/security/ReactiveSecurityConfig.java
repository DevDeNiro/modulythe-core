package com.modulythe.framework.application.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveSecurityConfig {

    private final UserMapper userMapper;
    private final SecurityProperties securityProperties;

    public ReactiveSecurityConfig(final UserMapper userMapper, final SecurityProperties securityProperties) {
        this.userMapper = userMapper;
        this.securityProperties = securityProperties;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated());

        if ("opaque".equalsIgnoreCase(securityProperties.getMode())) {
            http.oauth2ResourceServer(oauth2 -> oauth2
                    .opaqueToken(opaque -> opaque
                            .introspectionUri(securityProperties.getIntrospectionUri())
                            .introspectionClientCredentials(securityProperties.getClientId(), securityProperties.getClientSecret())
                            .authenticationConverter(opaqueTokenAuthenticationConverter())
                    )
            );
        } else {
            http.oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        }

        return http.build();
    }

    private ReactiveOpaqueTokenAuthenticationConverter opaqueTokenAuthenticationConverter() {
        return (introspectionPrincipal, authenticatedPrincipal) -> {
            AuthenticatedUser user = userMapper.toAuthenticatedUser(authenticatedPrincipal.getAttributes());
            var authorities = user.roles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            return Mono.just(new UsernamePasswordAuthenticationToken(user, authenticatedPrincipal, authorities));
        };
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        return jwt -> {
            AuthenticatedUser user = userMapper.toAuthenticatedUser(jwt.getClaims());
            var authorities = user.roles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            return Mono.just(new UsernamePasswordAuthenticationToken(user, jwt, authorities));
        };
    }
}
