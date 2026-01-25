package com.modulythe.framework.infrastructure.security;

import com.modulythe.framework.application.security.AuthenticatedUser;
import com.modulythe.framework.application.security.SecurityProperties;
import com.modulythe.framework.application.security.UserMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;

/**
 * Security configuration for Reactive (WebFlux) applications.
 * <p>
 * This configuration enables WebFlux security and reactive method security.
 * It configures the {@link SecurityWebFilterChain} to handle OAuth2 Resource Server
 * authentication, supporting both JWT and Opaque Token introspection.
 * </p>
 * <p>
 * Security headers are configured to protect against common web vulnerabilities:
 * <ul>
 *   <li>X-Content-Type-Options: nosniff</li>
 *   <li>X-Frame-Options: DENY</li>
 *   <li>X-XSS-Protection: enabled</li>
 *   <li>Strict-Transport-Security (HSTS)</li>
 *   <li>Referrer-Policy: strict-origin-when-cross-origin</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveSecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveSecurityConfig.class);
    private static final Set<String> VALID_MODES = Set.of("jwt", "opaque");

    private final UserMapper userMapper;
    private final SecurityProperties securityProperties;

    public ReactiveSecurityConfig(final UserMapper userMapper, final SecurityProperties securityProperties) {
        this.userMapper = userMapper;
        this.securityProperties = securityProperties;
    }

    /**
     * Validates the security mode configuration at startup.
     *
     * @throws IllegalStateException if the configured mode is invalid.
     */
    @PostConstruct
    public void validateSecurityMode() {
        String mode = securityProperties.getMode();
        if (mode == null || !VALID_MODES.contains(mode.toLowerCase())) {
            throw new IllegalStateException(
                    "Invalid security mode: '" + mode + "'. Valid modes are: " + VALID_MODES);
        }
        LOGGER.info("Security mode configured: {}", mode);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityWebFilterChain.class)
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
                // Security headers configuration
                .headers(headers -> headers
                        .contentTypeOptions(contentType -> {})             // X-Content-Type-Options: nosniff (default)
                        .frameOptions(frame -> frame
                                .mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY))
                        .xssProtection(xss -> {})                          // X-XSS-Protection (default)
                        .hsts(hsts -> hsts                                 // HSTS
                                .includeSubdomains(true)
                                .maxAge(Duration.ofDays(365)))
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .permissionsPolicy(permissions -> permissions
                                .policy("geolocation=(), microphone=(), camera=()"))
                );

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
