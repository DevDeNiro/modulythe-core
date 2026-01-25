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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.util.Set;

/**
 * Security configuration for Servlet (MVC) applications.
 * <p>
 * This configuration enables Web Security and method security.
 * It configures the {@link SecurityFilterChain} to handle OAuth2 Resource Server
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
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletSecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletSecurityConfig.class);
    private static final Set<String> VALID_MODES = Set.of("jwt", "opaque");

    private final UserMapper userMapper;
    private final SecurityProperties securityProperties;

    public ServletSecurityConfig(final UserMapper userMapper, final SecurityProperties securityProperties) {
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
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                // Security headers configuration
                .headers(headers -> headers
                        .contentTypeOptions(Customizer.withDefaults())     // X-Content-Type-Options: nosniff
                        .frameOptions(frame -> frame.deny())               // X-Frame-Options: DENY
                        .xssProtection(Customizer.withDefaults())          // X-XSS-Protection
                        .httpStrictTransportSecurity(hsts -> hsts          // HSTS
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))                // 1 year
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .permissionsPolicyHeader(permissions -> permissions
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

    private OpaqueTokenAuthenticationConverter opaqueTokenAuthenticationConverter() {
        return (introspectionPrincipal, authenticatedPrincipal) -> {
            AuthenticatedUser user = userMapper.toAuthenticatedUser(authenticatedPrincipal.getAttributes());
            var authorities = user.roles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            return new UsernamePasswordAuthenticationToken(user, authenticatedPrincipal, authorities);
        };
    }

    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> {
            AuthenticatedUser user = userMapper.toAuthenticatedUser(jwt.getClaims());
            var authorities = user.roles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
            return new UsernamePasswordAuthenticationToken(user, jwt, authorities);
        };
    }
}
