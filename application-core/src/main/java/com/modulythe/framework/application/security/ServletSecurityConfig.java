package com.modulythe.framework.application.security;

import com.modulythe.framework.application.AuthenticatedUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletSecurityConfig {

    private final UserMapper userMapper;
    private final SecurityProperties securityProperties;

    public ServletSecurityConfig(final UserMapper userMapper, final SecurityProperties securityProperties) {
        this.userMapper = userMapper;
        this.securityProperties = securityProperties;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

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
